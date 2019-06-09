package dynamicProxy.jdk.proxyUtil;

import dynamicProxy.jdk.ElectricCar;

public class Test {

    public static void main(String[] args) {
        ElectricCar car = new ElectricCar();
        ProxyUtils.generateClassFile(car.getClass(), "ElectricCarProxy");
    }
}

