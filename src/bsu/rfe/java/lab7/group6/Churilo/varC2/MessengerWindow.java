package bsu.rfe.java.lab7.group6.Churilo.varC2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.UnknownHostException;

public class MessengerWindow extends JFrame {
    private static final int FRAME_MINIMUM_WIDTH = 500;
    private static final int FRAME_MINIMUM_HEIGHT = 500;

    private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
    private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;

    private static final int SMALL_GAP = 5;
    private static final int MEDIUM_GAP = 10;
    private static final int LARGE_GAP = 15;

    private InstantMessenger messenger;
    private User recipient;

    private JTextArea textAreaIncoming;
    private JTextArea textAreaOutgoing;

    MessengerWindow(InstantMessenger messenger, User recipient){
        super("Чат с пользователем " + recipient.getName());
        setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));

        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - getWidth()) / 2 + FRAME_MINIMUM_WIDTH,(kit.getScreenSize().height - getHeight()) / 2);

        this.messenger = messenger;
        this.recipient = recipient;

        //Messages Panel
        textAreaIncoming = new JTextArea(INCOMING_AREA_DEFAULT_ROWS, 0);
        textAreaIncoming.setEditable(false);
        textAreaIncoming.setText(recipient.getMessageHistory().toString());

        JScrollPane scrollPaneIncoming = new JScrollPane(textAreaIncoming);

        MessageListener messageListener = new MessageListener() {
            public void messageReceived(User sender, String message) {
                if (sender.equals(recipient))
                    textAreaIncoming.append(message);
            }
        };

        messenger.addMessageListener(messageListener);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                messenger.removeMessageListener(messageListener);
            }
        });
        //

        //Your message Panel
        JPanel messagePanel = new JPanel();
        messagePanel.setBorder(BorderFactory.createTitledBorder("Сообщение пользователя"));

        textAreaOutgoing = new JTextArea(OUTGOING_AREA_DEFAULT_ROWS, 0);
        JScrollPane scrollPaneOutgoing = new JScrollPane(textAreaOutgoing);

        JButton sendButton = new JButton("Отправить");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String message = textAreaOutgoing.getText();

                    if (message.isEmpty()) {
                        JOptionPane.showMessageDialog(MessengerWindow.this, "Введите текст сообщения", "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    messenger.sendMessage(recipient, message);

                    textAreaOutgoing.setText("");
                }
                catch (UnknownHostException ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MessengerWindow.this,"Не удалось отправить сообщение: узел-адресат не в сети", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                catch (IOException ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MessengerWindow.this,"Не удалось отправить сообщение", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        GroupLayout messageLayout = new GroupLayout(messagePanel);
        messagePanel.setLayout(messageLayout);

        messageLayout.setHorizontalGroup(messageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(messageLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(scrollPaneOutgoing)
                        .addComponent(sendButton))
                .addContainerGap()
        );
        messageLayout.setVerticalGroup(messageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneOutgoing)
                .addGap(MEDIUM_GAP)
                .addComponent(sendButton)
                .addContainerGap()
        );
        //


        GroupLayout mainLayout = new GroupLayout(getContentPane());
        setLayout(mainLayout);

        mainLayout.setHorizontalGroup(mainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainLayout.createParallelGroup()
                        .addComponent(scrollPaneIncoming)
                        .addComponent(messagePanel))
                .addContainerGap()
        );
        mainLayout.setVerticalGroup(mainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneIncoming)
                .addComponent(messagePanel)
                .addContainerGap()
        );
    }
}
