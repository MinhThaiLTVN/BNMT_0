# Proxy Pattern

>     **Proxy Pattern** (hay còn gọi là Surrogate) là một design pattern thuộc nhóm cấu trúc ( Structure Design Pattern ).

---

<img title="Cung cấp 1 class đại diện để quản lí sự truy xuất đến thành phần của 1 class khác. Giải quyết vấn đề security, perfomance, validation,…" src="https://refactoring.guru/images/patterns/content/proxy/proxy.png?id=efece4647fb11e3f7539291796327666" alt="" data-align="center" style="zoom:100%;">

    Một ví dụ thực tế của Proxy Pattern có thể là khi bạn mua sắm trực tuyến. Khi bạn thanh toán cho đơn hàng của mình, thay vì trực tiếp gửi thông tin thẻ tín dụng của bạn đến cửa hàng, bạn sẽ gửi thông tin đó đến một công ty thanh toán trung gian (ví dụ như PayPal). Công ty này sẽ đóng vai trò là một proxy, kiểm tra thông tin thẻ tín dụng của bạn và xác nhận thanh toán với cửa hàng mà không cần tiết lộ thông tin thẻ tín dụng của bạn cho cửa hàng.

#### # Khái niệm :

>     **Proxy Pattern** (hay còn gọi là Surrogate) là một mẫu thiết kế thuộc nhóm cấu trúc (Structural Pattern). 
> 
>     Nó điều khiển gián tiếp việc truy xuất đối tượng thông qua một đối tượng được ủy nhiệm. Proxy cung cấp một class đại diện để quản lí sự truy xuất đến thành phần của một class khác.
> 
>     Bạn có thể hình dung Proxy Pattern như một người đại diện giữa bạn và một đối tượng mà bạn muốn truy cập. Người đại diện này sẽ kiểm soát việc truy cập của bạn đến đối tượng đó và có thể thêm các chức năng kiểm tra hoặc xác thực trước khi cho phép bạn truy cập.

 

#### # Sơ đồ UML Proxy Pattern :

<img title="UML Proxy pattern" src="https://www.dofactory.com/img/diagrams/net/proxy.png" alt="" data-align="center">

#### # Các thành phần :

Các lớp và đối tượng tham gia trong mẫu thiết kế này bao gồm:

* **Proxy (MathProxy) :**
  
  * Giữ một tham chiếu cho phép proxy truy cập vào đối tượng thật sự (RealSubject). Proxy có thể tham chiếu đến một Subject nếu RealSubject và Subject có cùng giao diện.
  * Cung cấp một giao diện giống hệt với Subject, để proxy có thể thay thế cho đối tượng thật sự.
  * Kiểm soát việc truy cập vào đối tượng thật và có thể chịu trách nhiệm tạo và xóa đối tượng đó.
  * Các trách nhiệm khác phụ thuộc vào loại proxy:
    * *Remote proxies* : chịu trách nhiệm mã hóa yêu cầu và các đối số của nó, và gửi yêu cầu đã mã hóa đến đối tượng thật trong một không gian địa chỉ khác.
    * *Virtual proxies* : có thể lưu trữ thông tin bổ sung về đối tượng thật để trì hoãn việc truy cập đến nó. Ví dụ, ImageProxy từ Motivation lưu trữ kích thước thực của hình ảnh.
    * *Protection proxies* : kiểm tra xem người gọi có quyền truy cập cần thiết để thực hiện một yêu cầu hay không.

* **Subject (IMath) :**
  
  * Xác định giao diện chung cho RealSubject và Proxy để Proxy có thể được sử dụng bất cứ nơi nào cần một RealSubject.

* **RealSubject (Math) :**
  
  * Xác định đối tượng thật sự mà proxy đại diện.
    
    

#### # Ta hãy đến với ví dụ :

- **Structural code in C#**

    Đoạn Code cấu trúc này mô tả mẫu thiết kế Proxy, cung cấp một đối tượng đại diện (proxy) kiểm soát quyền truy cập đến một đối tượng tương tự khác.

```csharp

using System;

namespace Proxy.Structural
{
    /// <summary>
    /// Proxy Design Pattern
    /// </summary>

    public class Program
    {
        public static void Main(string[] args)
        {
            // Create proxy and request a service

            Proxy proxy = new Proxy();
            proxy.Request();

            // Wait for user

            Console.ReadKey();
        }
    }

    /// <summary>
    /// The 'Subject' abstract class
    /// </summary>

    public abstract class Subject
    {
        public abstract void Request();
    }

    /// <summary>
    /// The 'RealSubject' class
    /// </summary>

    public class RealSubject : Subject
    {
        public override void Request()
        {
            Console.WriteLine("Called RealSubject.Request()");
        }
    }

    /// <summary>
    /// The 'Proxy' class
    /// </summary>

    public class Proxy : Subject
    {
        private RealSubject realSubject;

        public override void Request()
        {
            // Use 'lazy initialization'

            if (realSubject == null)
            {
                realSubject = new RealSubject();
            }

            realSubject.Request();
        }
    }
}



```

**Output**

```powershell
Called RealSubject.Request()
```

>     Trong đó, `Subject` là một lớp trừu tượng định nghĩa một phương thức `Request()` để thực hiện một yêu cầu. `RealSubject` là một lớp kế thừa từ `Subject` và thực hiện phương thức `Request()` bằng cách in ra một thông báo.
> 
>     `Proxy` cũng là một lớp kế thừa từ `Subject` và chứa một tham chiếu đến một đối tượng `RealSubject`. Khi phương thức `Request()` của `Proxy` được gọi, nó sẽ kiểm tra xem đối tượng `RealSubject` đã được khởi tạo hay chưa (sử dụng kỹ thuật khởi tạo lười - lazy initialization). Nếu chưa, nó sẽ khởi tạo đối tượng `RealSubject` và sau đó gọi phương thức `Request()` của đối tượng này.
> 
>     Trong hàm `Main`, chúng ta tạo ra một đối tượng `Proxy` và gọi phương thức `Request()` của nó. Điều này sẽ dẫn đến việc khởi tạo đối tượng `RealSubject` và gọi phương thức `Request()` của nó.



- **Real-world code in C#**

   Đoạn Code thực tế này mô tả mẫu thiết kế Proxy trong một đối tượng Math được đại diện bởi đối tượng MathProxy.

```csharp
using System;

namespace Proxy.RealWorld
{
    /// <summary>
    /// Proxy Design Pattern
    /// </summary>

    public class Program
    {
        public static void Main(string[] args)
        {
            // Create math proxy

            MathProxy proxy = new MathProxy();

            // Do the math

            Console.WriteLine("4 + 2 = " + proxy.Add(4, 2));
            Console.WriteLine("4 - 2 = " + proxy.Sub(4, 2));
            Console.WriteLine("4 * 2 = " + proxy.Mul(4, 2));
            Console.WriteLine("4 / 2 = " + proxy.Div(4, 2));

            // Wait for user

            Console.ReadKey();
        }
    }

    /// <summary>
    /// The 'Subject interface
    /// </summary>

    public interface IMath
    {
        double Add(double x, double y);
        double Sub(double x, double y);
        double Mul(double x, double y);
        double Div(double x, double y);
    }

    /// <summary>
    /// The 'RealSubject' class
    /// </summary>

    public class Math : IMath
    {
        public double Add(double x, double y) { return x + y; }
        public double Sub(double x, double y) { return x - y; }
        public double Mul(double x, double y) { return x * y; }
        public double Div(double x, double y) { return x / y; }
    }

    /// <summary>
    /// The 'Proxy Object' class
    /// </summary>

    public class MathProxy : IMath
    {
        private Math math = new Math();

        public double Add(double x, double y)
        {
            return math.Add(x, y);
        }
        public double Sub(double x, double y)
        {
            return math.Sub(x, y);
        }
        public double Mul(double x, double y)
        {
            return math.Mul(x, y);
        }
        public double Div(double x, double y)
        {
            return math.Div(x, y);
        }
    }
}


```

**Output**

```powershell
4 + 2 = 6
4 - 2 = 2
4 * 2 = 8
4 / 2 = 2
```

>     Trong đó, `IMath` là một interface định nghĩa các phương thức cơ bản cho các phép toán số học. `Math` là một lớp triển khai interface `IMath` và thực hiện các phép toán số học cơ bản.
> 
>     `MathProxy` là một lớp triển khai interface `IMath` và chứa một tham chiếu đến một đối tượng `Math`. Khi các phương thức của `MathProxy` được gọi, nó sẽ gọi các phương thức tương ứng của đối tượng `Math`.
> 
>     Trong hàm `Main`, chúng ta tạo ra một đối tượng `MathProxy` và sử dụng nó để thực hiện các phép toán số học. Điều này sẽ dẫn đến việc gọi các phương thức tương ứng của đối tượng `Math`.
