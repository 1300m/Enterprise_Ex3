/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmsprimeclient;

import java.util.Scanner;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 *
 * @author jiapat
 */
public class Main {
    @Resource(mappedName = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;
    
    @Resource(mappedName = "jms/SimpleJMSQueue")
    private static Queue queue;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MessageProducer replyProducer = null;
        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        TextMessage message = null;
        TextListener listener = null;
        
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue tempDest = session.createTemporaryQueue();
            consumer = session.createConsumer(tempDest);
            consumer.setMessageListener(listener);
            replyProducer = session.createProducer(queue);
            connection.start();
            
            String ch = "";
            Scanner inp = new Scanner(System.in);
            while(true) {
                System.out.println("Enter two number. Use ',' to separate each number. To end program press enter");
                ch = inp.nextLine();
                if (ch.equals("")) {
                    break;
                }
                message = session.createTextMessage();
                message.setText(ch);
                String correlationId = "12345";
                message.setJMSCorrelationID(correlationId);
                message.setJMSReplyTo(tempDest);
                System.out.println("sending message: " + message.getText());
                replyProducer.send(message);
            }
        } catch (JMSException e) {
            System.err.println("Exception occurred: " + e.toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    }
    
}
