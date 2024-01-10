package com.company.navcomponentanalyzer.core.model.process;

import com.company.navcomponentanalyzer.core.model.BodyParser;
import com.company.navcomponentanalyzer.core.model.NavObject;
import com.company.navcomponentanalyzer.core.model.NavType;

import java.util.concurrent.BlockingQueue;

public class ProcessObject implements Runnable{
    private final BlockingQueue<NavObject> navQueue;

    public ProcessObject(BlockingQueue<NavObject> navQueue) {
        this.navQueue = navQueue;
    }

    @Override
    public void run() {
        NavObject navObject;
        try {
            navObject = navQueue.take();
            while (navObject.getId() != 0) {
                BodyParser.parseProcedures(navObject);
                navObject = navQueue.take();
            }
            navObject = new NavObject(0, "", NavType.Table);
            navQueue.put(navObject);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
