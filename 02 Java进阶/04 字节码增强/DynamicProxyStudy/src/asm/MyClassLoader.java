package asm;

/**
 * Created by huangliang on 2018/8/14.
 */
public class MyClassLoader extends ClassLoader{


    public Class<?> defineMyClass( byte[] b, int off, int len)
    {
        return super.defineClass(b, off, len);
    }

}
