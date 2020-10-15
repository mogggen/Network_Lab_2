package com.company;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class Main {
    //https://www.tutorialspoint.com/java_xml/java_sax_parse_document.htm : adjust test code to your needs
    //copy-paste lon= lat= from places.xml and use them to download xml for the current whether
    //set a killTimer to how frequently the prognosis will update
    //done!

    //kill timer
    long timer;
    //"<?xml version=\"1.0\" encoding=\"UTF-8\"?><places><locality name=\"Skelleftea\"><location altitude=\"55\" latitude=\"64.4444\" longitude=\"20.9644\"/></locality>      <locality name=\"Kage\">        <location altitude=\"20\" latitude=\"64.8444\" longitude=\"20.9844\"/>      </locality>      <locality name=\"Stockholm\">        <location altitude=\"20\" latitude=\"59.3250\" longitude=\"18.0707\"/>      </locality></places>"


    public static void main(String[] args) {
        try {
            //File inputFile = new File("");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            UserHandler userHandler = new UserHandler();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //create GUI
        GUI gui = new GUI();
    }
}

    class UserHandler extends DefaultHandler {
        boolean lon = false;
        boolean lat = false;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
        {

            if (qName.equalsIgnoreCase("student")) {
                String rollNo = attributes.getValue("rollno");
                System.out.println("Roll No : " + rollNo);
            } else if (qName.equalsIgnoreCase("firstname")) {
                lon = true;
            } else if (qName.equalsIgnoreCase("lastname")) {
                lat = true;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {

            if (qName.equalsIgnoreCase("student")) {
                System.out.println("End Element :" + qName);
            }
        }

        @Override
        public void characters(char ch[], int start, int length) throws SAXException {

            if (lon) {
                System.out.println("First Name: " + new String(ch, start, length));
                lon = false;
            } else if (lat) {
                System.out.println("Last Name: " + new String(ch, start, length));
                lat = false;
            }
        }
    }

class GUI implements ActionListener {
    JFrame frame;

    JComboBox<String> Cities;
    JTextField killTimer;
    JButton submit;
    JLabel desc;

    JPanel panel;

    public GUI()
    {
        //set window
        frame = new JFrame();

        //get killTimer value
        killTimer = new JTextField();
        killTimer.addActionListener(this);

        //select city
        Cities = new JComboBox<String>();
        Cities.addItem("Skelleftea");
        Cities.addItem("Kage");
        Cities.addItem("Stockholm");

        //get prognosis
        submit = new JButton("get prognosis");
        submit.addActionListener(this);

        desc = new JLabel("update time in ms: ");

        //panel to hold all content of the GUI
        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(100, 100, 201, 201));
        panel.setLayout(new GridLayout(3, 2));

        panel.add(desc);
        panel.add(killTimer);
        panel.add(Cities);
        panel.add(submit);


        //the GUI mainframe
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("GUI Client");
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(killTimer.getText());
    }
}