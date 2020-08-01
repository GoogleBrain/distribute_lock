package cn.k.distributelock.controller;

public class AutounLockTest implements AutoCloseable {
    private String name;

    public AutounLockTest(String name) {
        this.name = name;
    }


    public String getLock() {
        return name;
    }

    public void unLock() {
        System.out.println("自动释放锁........");
    }


    @Override
    public void close() throws Exception {
        unLock();
    }
}
