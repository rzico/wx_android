package com.rzico.weex.net.session;

import java.util.LinkedList;

/**
 * Created by Jinlesoft on 2017/9/19.
 *  会话失效管理器
 */

public class SessionOutManager {
    //用来保存会话失效时 的请求以及回调，用于登录成功后调用并且返回
    private  static LinkedList<TaskBean> list = new LinkedList<TaskBean>();
    public static void clear()//销毁队列
    {
        list.clear();
    }
    public static boolean isEmpty()//判断队列是否为空
    {
        return list.isEmpty();
    }
    public static void enQueue(TaskBean o)//进队
    {
        if(!list.isEmpty()){//  如果里面有任务的话就清理掉
            deQueue();
        }
        list.addLast(o);
    }
    public static TaskBean deQueue()//出队
    {
        if(!list.isEmpty())
        {
            return list.removeFirst();
        }
        return null;
    }
    public static int length()//获取队列长度
    {
        return list.size();
    }
    public static TaskBean peek()//查看队首元素
    {
        return list.getFirst();
    }

}
