package javassist;

public class MyGenerator {

    public static void main(String[] args) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        //创建Programmer类
        CtClass cc= pool.makeClass("Programmer");
        //定义code方法
        CtMethod method = CtNewMethod.make("public void code(){}", cc);
        //插入方法代码
        method.insertBefore("System.out.println(\"I'm a Programmer,Just javassist Coding.....\");");
        cc.addMethod(method);
        //保存生成的字节码
        cc.writeFile("d://temp");
    }
}

