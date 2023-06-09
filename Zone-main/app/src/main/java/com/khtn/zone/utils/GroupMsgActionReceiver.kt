package com.khtn.zone.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.CallSuper
import androidx.core.app.RemoteInput
import com.google.firebase.firestore.CollectionReference
import com.khtn.zone.KEY_TEXT_REPLY
import com.khtn.zone.TYPE_NEW_GROUP_MESSAGE
import com.khtn.zone.core.GroupMessageSender
import com.khtn.zone.core.GroupMsgStatusUpdater
import com.khtn.zone.core.OnGrpMessageResponse
import com.khtn.zone.database.dao.GroupDao
import com.khtn.zone.database.dao.GroupMessageDao
import com.khtn.zone.database.data.Group
import com.khtn.zone.database.data.GroupMessage
import com.khtn.zone.database.data.GroupWithMessages
import com.khtn.zone.database.data.TextMessage
import com.khtn.zone.di.GroupCollection
import com.khtn.zone.model.UserProfile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

abstract class HiltGroupBroadcastReceiver: BroadcastReceiver() {
    @CallSuper
    override fun onReceive(context: Context?, intent: Intent?) {}
}

@Suppress("DEPRECATION")
@AndroidEntryPoint
class GroupMsgActionReceiver: HiltGroupBroadcastReceiver(), OnGrpMessageResponse {

    @Inject
    lateinit var preference: SharedPreferencesManager

    @GroupCollection
    @Inject
    lateinit var grpCollection: CollectionReference

    @Inject
    lateinit var groupDao: GroupDao

    @Inject
    lateinit var groupMsgStatusUpdater: GroupMsgStatusUpdater

    @Inject
    lateinit var groupMsgDao: GroupMessageDao

    lateinit var groupWithMsg: GroupWithMessages
    private var notificationId: Int = 0
    lateinit var context: Context
    private lateinit var myUserId: String
    private lateinit var group: Group
    private lateinit var profile: UserProfile

    override fun onReceive(
        context: Context?,
        intent: Intent?
    ) {
        super.onReceive(context, intent)
        this.context = context!!
        myUserId = preference.retrieveStringByKey(SharedPrefConstants.UID)!!
        profile = preference.getUserProfile()!!
        groupWithMsg = intent!!.getParcelableExtra(DataConstants.GROUP_DATA)!!
        group = groupWithMsg.group
        val notiIdString = groupWithMsg.group.createdAt.toString()
        // last 4 digits as notificationId
        notificationId = notiIdString.substring(notiIdString.length - 4)
            .toInt()
        if (intent.action == ActionConstants.ACTION_MARK_AS_READ) {
            groupMsgStatusUpdater.updateToSeen(myUserId, groupWithMsg.messages, group.id)
            Utils.removeNotificationById(context, notificationId)
            updateOnDb()
        } else {
            val reply = getMessageText(intent)
            if (reply.isNotBlank()) {
                val txtMessage = TextMessage(reply)
                val message = createMessage()
                message.textMessage = txtMessage
                val messageSender = GroupMessageSender(grpCollection)
                messageSender.sendMessage(message, group, this)
            }
        }
    }

    private fun createMessage(): GroupMessage {
        val toUsers = groupWithMsg.group.members?.map { it.id } as ArrayList
        val groupSize = group.members!!.size
        val statusList = ArrayList<Int>()
        val deliveryTimeList = ArrayList<Long>()
        for (index in 0 until groupSize) {
            statusList.add(0)
            deliveryTimeList.add(0L)
        }
        return GroupMessage(
            System.currentTimeMillis(), group.id,
            from = myUserId, to = toUsers,
            profile.userName, profile.image, statusList, deliveryTimeList,
            deliveryTimeList
        )
    }


    private fun updateOnDb() {
        val list = groupWithMsg.messages.filter {
            Utils.myMsgStatus(myUserId, it) < 3 && it.from != myUserId
        }.map {
            it.status[Utils.myIndexOfStatus(myUserId, it)] = 3
            it
        }
        // change status to seen for other message of the user
        UserUtils.setUnReadCountGroup(groupDao, groupWithMsg.group)
        UserUtils.insertMultipleGroupMsg(groupMsgDao, list)
    }

    private fun getMessageText(intent: Intent): String {
        return RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_TEXT_REPLY).toString()
    }

    override fun onSuccess(message: GroupMessage) {
        Utils.removeNotificationById(context, notificationId)
        // update to seen status
        groupMsgStatusUpdater.updateToSeen(myUserId, groupWithMsg.messages, group.id)
        updateOnDb()
        UserUtils.insertGroupMsg(groupMsgDao, message)
        for (user in group.members!!) {
            val token = user.user.token
            if (token.isNotEmpty())
                UserUtils.sendPush(
                    context, TYPE_NEW_GROUP_MESSAGE,
                    Json.encodeToString(message), token, user.id
                )
        }
    }

    override fun onFailed(message: GroupMessage) {
        UserUtils.insertGroupMsg(groupMsgDao, message)
    }
}