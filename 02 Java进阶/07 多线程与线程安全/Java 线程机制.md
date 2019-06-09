# 一. 线程与进程的概述 #

## 1. 什么是进程 ##
  进程就是正在运行的程序，是系统进行资源分配和调用的独立单位。每一个进程都有它自己的内存空间和系统资源。当我们打开电脑资源管理器时，就可以显示当前正在运行的所有进程。

## 2. 多进程的意义 ##
  大家应该有过这样的经历：我可以同时在电脑上做很多事情，比如我可以一边玩游戏，一边听音乐，网速够快我还可以同时用迅雷下载电影。这是因为我们的操作系统支持多进程，**简而言之就是：能在同一时段内执行多个任务。**
 
>需要注意的是，对于单核计算机来讲，游戏和听音乐这两个任务并不是“同时进行”的，因为CPU在某个时间点上只能做一件事情，计算机是在游戏进程和音乐进程间做着频繁切换，且切换速度很快（也就是轮流执行CPU时间片），所以，我们感觉游戏和音乐好像在“同时”进行，其实并不是同时执行的。
**多进程的作用不是提高执行速度，而是提高CPU的使用率。**

## 3. 什么是多线程 ##
  在一个进程内部又可以执行多个任务，而这每一个任务我们就可以看成是一个线程。

## 4. 多进程的意义 ##
  多线程的作用不是提高执行速度，而是为了提高应用程序的使用率。

    并行：逻辑上同时发生，指在某一段时间段同时运行多段程序
    并发：物理上同时发生，指在某一个时间点同时运行多段程序

 多线程却给了我们一个错觉：让我们认为多个线程是并发执行的。其实不是：多个线程共享同一个进程的资源，但是栈内存是独立的，一个线程一个栈。所以他们仍然是在抢CPU的资源执行。一个时间点上只有能有一个线程执行。而且谁抢到，这个不一定，所以，造成了线程运行的随机性。其中的某一个进程如果执行路径比较多的话，就会有更高的几率抢到CPU的执行权。

## 5. Java中的多线程 ##
  Java程序运行会启动JVM，相当于启动了一个进程，该进程会自动启动一个 “主线程” ，而main方法就运行在这个主线程当中，所以我们之前写的程序都是单线程。
# 二. 实现多线程的两种方式 #
## 1. 多线程实现方式V1.0（继承Thread类） ##
根据API文档查询可以得到创建多线程的方法：

1. 自定义类继承Thread类
2. 该子类重写子类的run（）方法
3. 并启动该子类的实例
4. 调用实例的start方法

放在run方法中的代码会被线程执行

    注意：run()方法实际上是单线程，不能直接调用run()方法，要先看到多线程的效果，必须使用start()方法。
    run()仅仅只是被封装的执行代码，而是普通方法，然而start方法会先启用线程，然后再用jvm去调用线程的run方法。
    因此：要启动多线程，一定要调用start()方法，不能使用run()方法。
示例：
    
    class ThreadDemo extends Thread {

		private Thread t;
		private String threadName;
	
		ThreadDemo(String name) {
			threadName = name;
			System.out.println("Creating " + threadName);
		}
	
		public void run() {
			System.out.println("Running " + threadName);
			try {
				for (int i = 4; i > 0; i--) {
					System.out.println("Thread: " + threadName + ", " + i); // 让线程睡眠一会
					Thread.sleep(50);
				}
			} catch (InterruptedException e) {
				System.out.println("Thread " + threadName + " interrupted.");
			}
			System.out.println("Thread " + threadName + " exiting.");
		}
	
		public void start() {
			System.out.println("Starting " + threadName);
			if (t == null) {
				t = new Thread(this, threadName);
				t.start();
			}
		}

	    public static void main(String args[]) {
		    ThreadDemo T1 = new ThreadDemo("Thread-1");
			T1.start();
	
			ThreadDemo T2 = new ThreadDemo("Thread-2");
			T2.start();
		}    
    }

运行结果
     
    Creating Thread-1
    Starting Thread-1
	Creating Thread-2
	Starting Thread-2
	Running Thread-1
	Thread: Thread-1, 4
	Running Thread-2
	Thread: Thread-2, 4
	Thread: Thread-1, 3
	Thread: Thread-2, 3
	Thread: Thread-1, 2
	Thread: Thread-2, 2
	Thread: Thread-1, 1
	Thread: Thread-2, 1
	Thread Thread-1 exiting.
	Thread Thread-2 exiting.

## 2. 多线程实现方式V2.0（实现Runnable接口） ##

1. 自定义类MyRunnerble()实现Runnable接口
2. 在MyRunnerble中重写run()方法
3. 创建MyRunnerble的实例对象
4. 将所创建的对象作为参数传入到Thread当中

示例

    class RunnableDemo implements Runnable {
	    private String name;
	
		public Thread2(String name) {
			this.name = name;
		}
	
		@Override
		public void run() {
			for (int i = 0; i < 5; i++) {
				System.out.println(name + "运行  :  " + i);
				try {
					Thread.sleep((int) Math.random() * 10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	
		public static void main(String[] args) {
			new Thread(new RunnableDemo("C")).start();
			new Thread(new RunnableDemo("D")).start();
		}
    }
运行结果
     
    C运行 :  0
	D运行 :  0
	D运行 :  1
	C运行 :  1
	D运行 :  2
	C运行 :  2
	D运行 :  3
	C运行 :  3
	D运行 :  4
	C运行 :  4

## 3. 两种创建方式的比较 ##

  比较两种创建方式如下:
  第一种方式是通过继承的方式实现。
  第二种方式是通过接口的方式实现。
     
      继承Runnerble接口的优点：
        1.可以避免Java单继承带来的局限性。
        2.适合多个相同程序的代码去处理同一个资源的情况，把线程同程序的代码，数据有效的分离，较好体现了面向对象的设计思想。

# 三. 多线程的生命周期 #
![生命周期](http://git.jd.com/jdwl_tc/JavaCourse/blob/master/02%20Java%E8%BF%9B%E9%98%B6/07%20%E5%A4%9A%E7%BA%BF%E7%A8%8B%E4%B8%8E%E7%BA%BF%E7%A8%8B%E5%AE%89%E5%85%A8/images/Thread%20in%20Java.png)
# 四. 线程调度和控制 #
## 1. 线程调度 ##
  如果我们的计算机只有一个 CPU，那么 CPU 在某一个时刻只能执行一条指令，线程只有得到 CPU时间片，也就是使用权，才可以执行指令。那么Java是如何对线程进行调用的呢？

     线程调度的两种模式
     分时调度模式：所有线程轮流使用 CPU 的使用权，平均分配每个线程占用 CPU 的时间片。
     抢占式调度模型：优先让优先级高的线程使用 CPU，如果线程的优先级相同，那么会随机选择一个，优先级高的线程获取的 CPU 时间片相对多一些。
![单线程模式](http://git.jd.com/jdwl_tc/JavaCourse/blob/master/02%20Java%E8%BF%9B%E9%98%B6/07%20%E5%A4%9A%E7%BA%BF%E7%A8%8B%E4%B8%8E%E7%BA%BF%E7%A8%8B%E5%AE%89%E5%85%A8/images/%E5%8D%95%E7%BA%BF%E7%A8%8B%E6%96%B9%E5%BC%8F.png)
![多线程模式](http://git.jd.com/jdwl_tc/JavaCourse/blob/master/02%20Java%E8%BF%9B%E9%98%B6/07%20%E5%A4%9A%E7%BA%BF%E7%A8%8B%E4%B8%8E%E7%BA%BF%E7%A8%8B%E5%AE%89%E5%85%A8/images/%E5%A4%9A%E7%BA%BF%E7%A8%8B%E6%96%B9%E5%BC%8F.png)

**Java采用的是抢占式调度模式，用优先级控制时间片轮转。**

设置和获取优先级的方式如下：

    public final int getPriority()
    public final void setPriority(int newPriority)

示例
    
    Thread4 t1 = new Thread4("t1");
	Thread4 t2 = new Thread4("t2");
	t1.setPriority(Thread.MAX_PRIORITY);
	t2.setPriority(Thread.MIN_PRIORITY);

## 2. 线程的控制 ##
- 线程休眠

相当于在线程中暂停了几秒。
    
    public static void sleep(long millis)

- 线程加入

为了让某些需要执行的线程执行完毕，别的线程才能拿够继续。

    public final void join()

- 线程礼让

暂停执行当前线程，并执行其他线程，在一定的程度上能够交替执行，但不能保证一定是交替执行的。

    public static void yield()



- 后台线程（守护线程）

将该线程标记为守护线程或者是用户线程，当正在运行的线程都是守护线程时，java虚拟机退出。
   
    public final void setDaemon(boolean on)

- 线程中断

中途关闭线程

    public final void stop()//过时了，但是还是可以使用的，不安全不建议使用
    public void interrupt()//他让我们抛出一个异常，如果受阻，那么状态终止，抛出异常

> interrupt():它只是线线程发送一个中断信号，让线程在无限等待时（如死锁时）能抛出抛出，从而结束线程，但是如果你吃掉了这个异常，那么这个线程还是不会中断的！

- 线程等待

JDK提供三个版本的方法
 
    public void wait()
    public void wait(long timeout)
    public void wait(long timeout,int nanos)

>1）wait()方法的作用是将当前运行的线程挂起（即让其进入阻塞状态），直到notify或notifyAll方法来唤醒线程.
> 
>2）wait(long timeout)，该方法与wait()方法类似，唯一的区别就是在指定时间内，如果没有notify或notifAll方法的唤醒，也会自动唤醒。
> 
>3）至于wait(long timeout,long nanos)，本意在于更精确的控制调度时间，不过从目前版本来看，该方法貌似没有完整的实现该功能。

----------

示例

    class Thread1 extends Thread {
		private String name;
	
		public Thread1(String name) {
			super(name);
			this.name = name;
		}
	
		public void run() {
			System.out.println(Thread.currentThread().getName() + " 线程运行开始!");
			for (int i = 0; i < 5; i++) {
				System.out.println("子线程" + name + "运行 : " + i);
				try {
					sleep((int) Math.random() * 10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println(Thread.currentThread().getName() + " 线程运行结束!");
		}
	
		public static void main(String[] args) {
			System.out.println(Thread.currentThread().getName() + "主线程运行开始!");
			Thread1 mTh1 = new Thread1("A");
			Thread1 mTh2 = new Thread1("B");
			mTh1.start();
			mTh2.start();
			System.out.println(Thread.currentThread().getName() + "主线程运行结束!");
	
		}
    }
运行结果

    main主线程运行开始!
	main主线程运行结束!
	B 线程运行开始!
	子线程B运行 : 0
	A 线程运行开始!
	子线程A运行 : 0
	子线程B运行 : 1
	子线程A运行 : 1
	子线程A运行 : 2
	子线程A运行 : 3
	子线程A运行 : 4
	A 线程运行结束!
	子线程B运行 : 2
	子线程B运行 : 3
	子线程B运行 : 4
	B 线程运行结束!
	发现主线程比子线程早结束

示例 **Join**
    
    public class Main {

		public static void main(String[] args) {
			System.out.println(Thread.currentThread().getName()+"主线程运行开始!");
			Thread1 mTh1=new Thread1("A");
			Thread1 mTh2=new Thread1("B");
			mTh1.start();
			mTh2.start();

			try {
				mTh1.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				mTh2.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName()+ "主线程运行结束!");	 
		}
	 
    }
运行结果
    
    main主线程运行开始!
	A 线程运行开始!
	子线程A运行 : 0
	B 线程运行开始!
	子线程B运行 : 0
	子线程A运行 : 1
	子线程B运行 : 1
	子线程A运行 : 2
	子线程B运行 : 2
	子线程A运行 : 3
	子线程B运行 : 3
	子线程A运行 : 4
	子线程B运行 : 4
	A 线程运行结束!
	主线程一定会等子线程都结束了才结束


示例 **Yield** 
    
暂停当前正在执行的线程对象，并执行其他线程
    
    class ThreadYield extends Thread{

	    public ThreadYield(String name) {
	        super(name);
	    }
	 
	    @Override
	    public void run() {
	        for (int i = 1; i <= 50; i++) {
	            System.out.println("" + this.getName() + "-----" + i);
	            // 当i为30时，该线程就会把CPU时间让掉，让其他或者自己的线程执行（也就是谁先抢到谁执行）
	            if (i ==30) {
	                this.yield();
	            }
	        }
		
	    }

		public static void main(String[] args) {	
			ThreadYield yt1 = new ThreadYield("张三");
	    	ThreadYield yt2 = new ThreadYield("李四");
	        yt1.start();
	        yt2.start();
		}

    }
 
运行结果
 
    第一种情况：李四（线程）当执行到30时会CPU时间让掉，这时张三（线程）抢到CPU时间并执行。
    
    第二种情况：李四（线程）当执行到30时会CPU时间让掉，这时李四（线程）抢到CPU时间并执行。 




> **Sleep()**和**Yield()**的区别
    
> sleep()使当前线程进入停滞状态，所以执行sleep()的线程在指定的时间内肯定不会被执行；yield()只是使当前线程重新回到可执行状态，所以执行yield()的线程有可能在进入到可执行状态后马上又被执行。

> sleep 方法使当前运行中的线程睡眼一段时间，进入不可运行状态，这段时间的长短是由程序设定的，yield 方法使当前线程让出 CPU 占有权，但让出的时间是不可设定的。实际上，yield()方法对应了如下操作：先检测当前是否有相同优先级的线程处于同可运行状态，如有，则把 CPU  的占有权交给此线程，否则，继续运行原来的线程。所以yield()方法称为“退让”，它把运行机会让给了同等优先级的其他线程。
       
> 另外，sleep 方法允许较低优先级的线程获得运行机会，但 yield()  方法执行时，当前线程仍处在可运行状态，所以，不可能让出较低优先级的线程些时获得 CPU 占有权。在一个运行系统中，如果较高优先级的线程没有调用 sleep 方法，又没有受到 I\O 阻塞，那么，较低优先级线程只能等待所有较高优先级的线程运行结束，才有机会运行。 

示例

建立三个线程，A线程打印10次A，B线程打印10次B,C线程打印10次C，要求线程同时运行，交替打印10次ABC。这个问题用Object的wait()，notify()就可以很方便的解决。

    class MyThreadPrinter implements Runnable {   
	  
	    private String name;   
	    private Object prev;   
	    private Object self;   
	  
	    private MyThreadPrinter2(String name, Object prev, Object self) {   
	        this.name = name;   
	        this.prev = prev;   
	        this.self = self;   
	    }   
	  
	    @Override  
	    public void run() {   
	        int count = 10;   
	        while (count > 0) {   
	            synchronized (prev) {   
	                synchronized (self) {   
	                    System.out.print(name);   
	                    count--;   
	                    self.notify();   
	                }   
	                try {   
	                    prev.wait();   
	                } catch (InterruptedException e) {   
	                    e.printStackTrace();   
	                }   
	            }   
	        }   
	    }   
	  
	    public static void main(String[] args) throws Exception {   
	        Object a = new Object();   
	        Object b = new Object();   
	        Object c = new Object();   
	        MyThreadPrinter pa = new MyThreadPrinter("A", c, a);   
	        MyThreadPrinter pb = new MyThreadPrinter("B", a, b);   
	        MyThreadPrinter pc = new MyThreadPrinter("C", b, c);   
     
	        new Thread(pa).start();
	        Thread.sleep(100);  //确保按顺序A、B、C执行
	        new Thread(pb).start();
	        Thread.sleep(100);  
	        new Thread(pc).start();   
	        Thread.sleep(100);  
	   }   
    } 

运行结果
  
    ABCABCABCABCABCABCABCABCABCABC

> **wait和sleep区别**
> 
>-**sleep()方法**
> 
> sleep()使当前线程进入停滞状态（阻塞当前线程），让出CUP的使用、目的是不让当前线程独自霸占该进程所获的CPU资源，以留一定时间给其他线程执行的机会;
>
sleep()是Thread类的Static(静态)的方法；因此他不能改变对象的机锁，所以当在一个Synchronized块中调用Sleep()方法是，线程虽然休眠了，但是对象的机锁并木有被释放，其他线程无法访问这个对象（即使睡着也持有对象锁）。

>在sleep()休眠时间期满后，该线程不一定会立即执行，这是因为其它线程可能正在运行而且没有被调度为放弃执行，除非此线程具有更高的优先级。
>
>-**wait()方法**
> 
> wait()方法是Object类里的方法；当一个线程执行到wait()方法时，它就进入到一个和该对象相关的等待池中，同时失去（释放）了对象的机锁（暂时失去机锁，wait(long timeout)超时时间到后还需要返还对象锁）；其他线程可以访问；
> 
wait()使用notify或者notifyAlll或者指定睡眠时间来唤醒当前等待池中的线程。
> 
wiat()必须放在synchronized block中，否则会在program runtime时扔出”java.lang.IllegalMonitorStateException“异常。

>-**最大区别**：
> 
> sleep()睡眠时，保持对象锁，仍然占有该锁；而wait()睡眠时，释放对象锁。但是wait()和sleep()都可以通过interrupt()方法打断线程的暂停状态，从而使线程立刻抛出InterruptedException（但不建议使用该方法）。
>