package bsu.rfe.java.lab7.group6.Churilo.varC2;

import javax.swing.*;
import java.awt.*;

public class MessengerWindow extends JFrame {
    private static final int FRAME_MINIMUM_WIDTH = 500;
    private static final int FRAME_MINIMUM_HEIGHT = 500;

    private InstantMessenger messenger;
    private User recipient;

    MessengerWindow(InstantMessenger messenger, User recipient){
        super("Чат с пользователем" + recipient.getName());
        setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));

        this.messenger = messenger;
        this.recipient = recipient;

    }
}
