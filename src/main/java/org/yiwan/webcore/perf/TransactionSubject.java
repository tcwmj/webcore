package org.yiwan.webcore.perf;

import org.yiwan.webcore.test.ITestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenny Wang on 3/14/2016.
 */
public class TransactionSubject implements Subject {

    private List<Observer> observers = new ArrayList<Observer>();
    private ITestTemplate testCase;

    public TransactionSubject(ITestTemplate testCase) {
        this.testCase = testCase;
    }

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void nodifyObserversStart() {
        for (Observer observer : observers) {
            observer.start(testCase);
        }
    }

    @Override
    public void nodifyObserversStop() {
        for (Observer observer : observers) {
            observer.stop(testCase);
        }
    }
}
