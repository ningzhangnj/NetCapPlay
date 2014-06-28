package com.network.mq;

import com.network.worker.RunnableDecorator;
import com.network.worker.ThreadDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MessageQueue.
 *
 * @author ningzhangnj
 */
public class MessageQueue {
    private static final Logger logger = LoggerFactory.getLogger(MessageQueue.class);

    /**
     * Message Queue name.
     */
    private String name = "MessageQueue";

    /**
     * Time tick of the message queue. Timeunit: milliseconds.
     */
    private long tickTime;

    /**
     * Message queue to hold the messages. Key is the tick.
     */
    private Map<Long, List<MessageEvent>> mqueue = new HashMap<Long, List<MessageEvent>>();

    /**
     * Shutdown flag of the message queue.
     */
    private boolean shutDown = false;

    /**
     * Points to the current tick in the message queue.
     */
    private long clock = 0L;

    /**
     * Used to calculate the time inaccuracy.
     */
    private long timeOffset = System.currentTimeMillis();

    /**
     * Used to avoid message queue concurrent access.
     */
    private MessageQueueLock lock;

    public MessageQueue(long tickTime, MessageQueueLock lock) {
        this.tickTime = tickTime;
        this.lock = lock;
    }

    public MessageQueue(long tickTime, MessageQueueLock lock, String name) {
        this(tickTime, lock);
        this.name = name;
    }

    public void start() {
        ThreadDispatcher.dispatch2(new RunnableDecorator(name, new Runnable() {

            @Override
            public void run() {
                timeOffset = System.currentTimeMillis();
                while (shutDown == false) {
                    synchronized (lock) {

                        if (mqueue.containsKey(clock)) {
                            while (mqueue.get(clock) != null && mqueue.get(clock).size() > 0) {
                                MessageEvent msg = mqueue.get(clock).get(0);
                                mqueue.get(clock).remove(0);
                                if (msg != null) {
                                    logger.info("Current clock: " + clock + ", tickTime: " + tickTime);
                                    logger.info("Start to process one message. MessageEvent: " + msg.toString());
                                    msg.handle();
                                }

                                startProtect();
                                stopProtect();
                            }

                            mqueue.remove(clock);
                        }
                        clock++;
                    }

                    long time2sleep = clock * tickTime - (System.currentTimeMillis() - timeOffset);
                    if (time2sleep > 0) {
                        ThreadDispatcher.sleep(time2sleep);
                    }
                }
            }
        }));
    }

    /**
     * Send one message event to the message queue.
     * @param wait  time before message is handled in seconds: <br></br>
     *              wait > 0  : Delay message 'wait' milliseconds. <br></br>
     *              wait = 0  : Handle in current 'tick'.  <br></br>
     *              wait = -1 : Execute now, bypassing the messageQueue entirely.
     *                          In this case ONLY: the method will return the return value (retval) of the transition
     *                          code being run as a result of the sent signal. <br></br>
     * @param msg  the message event to send.
     * @return the result of message handing. Currently it only make sense when triggering one immediate message.
     */
    public Object send(long wait, MessageEvent msg) {
        Object result = null;
        synchronized (lock) {
            if (wait == -1) {
                logger.info("Execute one msg right now. MessageEvent: " + msg.toString());
                result = msg.handle();
            } else {
                long targetTime = wait + (System.currentTimeMillis() - timeOffset);
                long targetTick = Math.max(targetTime/tickTime, clock);
                logger.info("MessageQueue received one msg. Target time: " +  targetTime + ", Target tick: " + targetTick + ", MessageEvent: " + msg.toString());

                if (mqueue.containsKey(targetTick)) {
                    mqueue.get(targetTick).add(msg);
                } else {
                    List<MessageEvent> msgs = new ArrayList<MessageEvent>();
                    msgs.add(msg);
                    mqueue.put(targetTick, msgs);
                }
            }
        }

        return result;
    }

    /**
     * Cancel one message. The message instance should implement the {@link MessageEvent#isSame(MessageEvent)} method.
     * @param msg1 the message to cancel.
     */
    public void cancel(MessageEvent msg1) {
        synchronized (lock) {
            for (List<MessageEvent> msgs: mqueue.values()) {
                if (msgs != null) {
                    int i=0;
                    int size = msgs.size();
                    while (i<size) {
                        MessageEvent msg = msgs.get(i);
                        if (msg1 != null && msg1.isSame(msg)) {
                            msgs.remove(i);
                            size--;
                            logger.info("Remove one msg from message queue. MessageEvent: " + msg.toString());
                        } else {
                            i++;
                        }
                    }
                }
            }
        }
    }

    /**
     * Shut down the message queue.
     */
    public void shutDown() {
        this.shutDown = true;
    }

    /**
     * Protect a operation on a set of data within the message queue.
     */
    public void startProtect() {
        this.lock.startProtect();
    }

    /**
     * For {@link #startProtect()}.
     */
    public void stopProtect() {
        this.lock.stopProtect();
    }

    /**
     *
     * @return   the message queue access lock.
     */
    public MessageQueueLock getLock() {
        return lock;
    }
}
