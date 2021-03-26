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

    private static final int SMALL_GAP = 5;
    private static final int MEDIUM_GAP = 10;
    private static final int LARGE_GAP = 15;

    private int SERVER_PORT;

    private InstantMessenger messenger;

    private JPanel userListPanel;

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

        userListPanel = new JPanel();
        JScrollPane scrollPaneUsers = new JScrollPane(userListPanel);

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

        GroupLayout mainLayout = new GroupLayout(getContentPane());
        setLayout(mainLayout);

        mainLayout.setHorizontalGroup(mainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(scrollPaneUsers)
                        .addComponent(addFriendButton))
                .addContainerGap()
        );
        mainLayout.setVerticalGroup(mainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneUsers)
                .addGap(MEDIUM_GAP)
                .addComponent(addFriendButton)
                .addContainerGap()
        );
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
