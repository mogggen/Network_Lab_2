package com.company;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main implements ActionListener
{
    private int count = 0;
    private final JLabel label;
    private final JFrame frame;
    private final JPanel panel;
    private final JButton button;

    public Main()
    {
        frame = new JFrame();

        button = new JButton("Click me");
        button.addActionListener(this);

        label = new JLabel("Number of clicks: " + count);
        label.setSize(10, 25);

        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(100, 100, 201, 201));
        panel.setLayout(new GridLayout(0, 1));
        panel.add(button);
        panel.add(label);

        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("WhetherChannelClient");
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args)
    {
        new Main();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        count++;
        label.setText("Number of clicks: " + count);
    }
}
