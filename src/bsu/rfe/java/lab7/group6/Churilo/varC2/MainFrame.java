package bsu.rfe.java.lab7.group6.Churilo.varC2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MainFrame extends JFrame {
    private static final String FRAME_TITLE = "Клиент мгновенных сообщений";

    private static final int FRAME_MINIMUM_WIDTH = 500;
    private static final int FRAME_MINIMUM_HEIGHT = 500;

    private static final int FROM_FIELD_DEFAULT_COLUMNS = 10;
    private static final int TO_FIELD_DEFAULT_COLUMNS = 20;

    private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
    private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;

    private static final int SMALL_GAP = 5;
    private static final int MEDIUM_GAP = 10;
    private static final int LARGE_GAP = 15;

    private int SERVER_PORT;

    private InstantMessenger messenger;

    private JTextField textFieldFrom;
    private JTextField textFieldTo;

    private JPanel userListPanel;

    private JTextArea textAreaIncoming;
    private JTextArea textAreaOutgoing;

    public MainFrame(){
        super(FRAME_TITLE);
        setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));

        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - getWidth()) / 2,(kit.getScreenSize().height - getHeight()) / 2);

        Scanner in = new Scanner(System.in);
        System.out.println("Your name:");
        String name = in.nextLine();
        System.out.println("Your port:");
        SERVER_PORT = in.nextInt();
        InetSocketAddress myAddress = new InetSocketAddress("127.0.0.1",SERVER_PORT);
        User me = new User(name, myAddress);
        messenger = new InstantMessenger(me);

        textAreaIncoming = new JTextArea(INCOMING_AREA_DEFAULT_ROWS,0);
        textAreaIncoming.setEditable(false);

        messenger.addMessageListener(new MessageListener() {
            public void messageReceived(User sender, String message) {
                textAreaIncoming.append(sender.getName() + ": " + message + "\n");
            }
        });

        userListPanel = new JPanel();
        //JScrollPane scrollPaneIncoming = new JScrollPane(textAreaIncoming);
        JScrollPane scrollPaneIncoming = new JScrollPane(userListPanel);

        final JLabel labelFrom = new JLabel("Подпись");
        final JLabel labelTo = new JLabel("Получатель");

        textFieldFrom = new JTextField(FROM_FIELD_DEFAULT_COLUMNS);
        textFieldTo = new JTextField(TO_FIELD_DEFAULT_COLUMNS);

        textAreaOutgoing = new JTextArea(OUTGOING_AREA_DEFAULT_ROWS, 0);

        JScrollPane scrollPaneOutgoing = new JScrollPane(textAreaOutgoing);

        JPanel messagePanel = new JPanel();
        messagePanel.setBorder(BorderFactory.createTitledBorder("Сообщение пользователя" + SERVER_PORT));

        JButton addFriendButton = new JButton("Добавить собеседника");
        addFriendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String port = JOptionPane.showInputDialog(MainFrame.this, "Введите порт собеседника", "Добавление собеседника", JOptionPane.QUESTION_MESSAGE);
                try{
                    messenger.sendFriendRequest(port);
                }
                catch (UnknownHostException ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this,"Не удалось добавить собеседника: узел-адресат не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                catch (IOException ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this,"Не удалось добавить собеседника", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                catch (NumberFormatException ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this,"Не удалось добавить собеседника: неверный формат порта", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        messenger.addUserListener(new UserListener() {
            public void addedNewUser(User newUser) {
                JButton button = createUserButton(newUser);
                userListPanel.add(button);
                MainFrame.this.revalidate();
            }
        });


        JButton sendButton = new JButton("Отправить");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                try {
//                    String senderName = textFieldFrom.getText();
//                    String destinationAddress = textFieldTo.getText();
//                    String message = textAreaOutgoing.getText();
//
//                    if (senderName.isEmpty()){
//                        JOptionPane.showMessageDialog(MainFrame.this, "Введите имя отправителя", "Ошибка", JOptionPane.ERROR_MESSAGE);
//                        return;
//                    }
//
//                    if (destinationAddress.isEmpty()) {
//                        JOptionPane.showMessageDialog(MainFrame.this, "Введите адрес узла-получателя", "Ошибка", JOptionPane.ERROR_MESSAGE);
//                        return;
//                    }
//
//                    if (message.isEmpty()) {
//                        JOptionPane.showMessageDialog(MainFrame.this, "Введите текст сообщения", "Ошибка", JOptionPane.ERROR_MESSAGE);
//                        return;
//                    }
//
//                    //messenger.sendMessage(senderName, destinationAddress, message);
//
//                    textAreaIncoming.append("Me -> " + destinationAddress + ": " + message + "\n");
//
//                    textAreaOutgoing.setText("");
//                }
//                catch (UnknownHostException ex){
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(MainFrame.this,"Не удалось отправить сообщение: узел-адресат не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
//                }
//                catch (IOException ex){
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(MainFrame.this,"Не удалось отправить сообщение", "Ошибка", JOptionPane.ERROR_MESSAGE);
//                }
            }
        });

        GroupLayout messageLayout = new GroupLayout(messagePanel);
        messagePanel.setLayout(messageLayout);

        messageLayout.setHorizontalGroup(
                messageLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(messageLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addGroup(messageLayout.createSequentialGroup()
                                        .addComponent(labelFrom)
                                        .addGap(SMALL_GAP)
                                        .addComponent(textFieldFrom)
                                        .addGap(LARGE_GAP)
                                        .addComponent(labelTo)
                                        .addGap(SMALL_GAP)
                                        .addComponent(textFieldTo))
                                .addComponent(scrollPaneOutgoing)
                                .addComponent(addFriendButton))
                        .addContainerGap());
        messageLayout.setVerticalGroup(
                messageLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(messageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(labelFrom)
                                .addComponent(textFieldFrom)
                                .addComponent(labelTo)
                                .addComponent(textFieldTo))
                        .addGap(MEDIUM_GAP)
                        .addComponent(scrollPaneOutgoing)
                        .addGap(MEDIUM_GAP)
                        .addComponent(addFriendButton)
                        .addContainerGap());

        GroupLayout layout = new GroupLayout(getContentPane());
        setLayout(layout);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup()
                                .addComponent(scrollPaneIncoming)
                                .addComponent(messagePanel))
                        .addContainerGap());
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(scrollPaneIncoming)
                        .addGap(MEDIUM_GAP)
                        .addComponent(messagePanel)
                        .addContainerGap());
    }

    private JButton createUserButton(User user){
        JButton button = new JButton(user.getName());
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MessengerWindow window = new MessengerWindow(messenger, user);
                window.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                window.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        button.setEnabled(true);
                    }
                });
                window.setVisible(true);
                button.setEnabled(false);
            }
        });
        return button;
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainFrame frame = new MainFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
