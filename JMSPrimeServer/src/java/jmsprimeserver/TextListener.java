/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmsprimeserver;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 *
 * @author jiapat
 */
public class TextListener implements MessageListener {
    private MessageProducer producer;
    private Session session = null;
    
    public TextListener(Session session, MessageProducer producer) {
        this.session = session;
        this.producer = producer;
    }
    
    @Override
    public void onMessage(Message message) {
        TextMessage msg = null;

        try {
            if (message instanceof TextMessage) {
                msg = (TextMessage) message;
                System.out.println("Reading message: " + msg.getText());
            } else {
                System.err.println("Message is not a TextMessage");
            }
            
            Queue tempDest = session.createTemporaryQueue();
            TextMessage response = session.createTextMessage();
            
            String[] range = msg.getText().split(",");
            int start = Integer.parseInt(range[0]);
            int end = Integer.parseInt(range[1]);
            int primes = countPrimes(start, end);
            response.setText("The number of primes between " + start + " and " + end
                            + " is " + primes);
            response.setJMSReplyTo(tempDest);
            System.out.println("Sending message: " + response.getText());
            producer.send(response);
            
        } catch (JMSException e) {
            System.err.println("JMSException in onMessage(): " + e.toString());
        } catch (Throwable t) {
            System.err.println("Exception in onMessage():" + t.getMessage());
        }
    }
    
    private boolean isPrime(int n) {
        int i;
        for (i = 2; i*i <= n; i++) {
            if ((n % i) == 0) {
                return false;
            }
        }
        return true;
    }
    
    private int countPrimes(int start, int end) {
        int count = 0;
        for(int i = start; i <= end; i++) {
            if(isPrime(i)) {
                count++;
            }
        }
        return count;
    }
}
