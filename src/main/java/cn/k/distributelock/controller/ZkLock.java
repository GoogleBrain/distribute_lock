package cn.k.distributelock.controller;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ZkLock implements AutoCloseable, Watcher {

    private ZooKeeper zooKeeper;

    private String znode;

    public ZkLock() throws IOException {
        this.zooKeeper = new ZooKeeper("localhost:2181", 10000, this);
    }

    public boolean getLock(String businessCode) {
        try {
            //创建根节点
            Stat exists = zooKeeper.exists("/" + businessCode, false);
            if (exists == null) {
                zooKeeper.create("/" + businessCode, businessCode.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            //创建顺序节点
            znode = zooKeeper.create("/" + businessCode + "/" + businessCode + "_", businessCode.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            List<String> children = zooKeeper.getChildren("/" + businessCode, false);
            Collections.sort(children);
            //获取序列号最低的那个
            String firstNode = children.get(0);
            if (znode.endsWith(firstNode)) {
                return true;
            }

            //不是第一个节点，则监听第一个节点
            String lastNode = firstNode;
            for (String node : children) {
                if (znode.endsWith(node)) {
                    zooKeeper.exists("/" + businessCode + "/" + lastNode, true);
                    break;
                } else {
                    lastNode = node;
                }
            }
            synchronized (this) {
                wait();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void close() throws Exception {
        zooKeeper.delete(znode, -1);
        zooKeeper.close();
        System.out.println("已经释放了锁。。。。。。");
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
            synchronized (this) {
                notify();
            }
        }
    }
}
