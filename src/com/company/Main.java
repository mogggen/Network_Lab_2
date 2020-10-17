package com.company;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class Main {
    //https://www.tutorialspoint.com/java_xml/java_sax_parse_document.htm : adjust test code to your needs
    //set a killTimer to how frequently the prognosis will update

    //kill timer
    long timer;


    public static void main(String[] args) throws SAXException {
        //create GUI
        GUI gui = new GUI();


        UserHandler userHandler = new UserHandler();
    }
}

    class UserHandler extends DefaultHandler {
        String from, to;
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if(qName.equalsIgnoreCase("time")){
                from = attributes.getValue(1).substring(11, 13);
                to = attributes.getValue(2).substring(11, 13);
                if (from.equals(to)) return;
                System.out.print("kl. " + from + " - " + to);
            }
            if(qName.equalsIgnoreCase("temperature")){
                System.out.println("\t" + attributes.getValue(2) + " C");
            }
            super.startElement(uri, localName, qName, attributes);
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
        ParseXml(getURLStream());
    }

    //The content of the URL in String representation
    String getURLString()
    {
        //get longitude and latitude for selected city
        ArrayList<Character> content = new ArrayList<>();
        float lat = 0f;
        float lon = 0f;
        switch (Cities.getItemAt(Cities.getSelectedIndex()))
        {
            case "Skelleftea":
                lat = 64.4444f;
                lon = 20.9644f;
                break;
            case "Kage":
                lat = 64.8444f;
                lon = 20.9844f;
                break;
            case "Stockholm":
                lat = 59.3250f;
                lon = 18.0707f;
                break;
        }

        //HttpURLConnection
        try
        {
            URL url = new URL("https://api.met.no/weatherapi/locationforecast/2.0/classic?lat=" + lat + "&lon=" + lon);
            HttpURLConnection huc = (HttpURLConnection)url.openConnection();
            huc.setRequestProperty("User-Agent", "low flying 747");
            huc.setRequestMethod("GET");
            InputStream website = (InputStream) huc.getContent();
            do {
                int next = website.read();
                if (next > 0)
                    content.add((char)next);
                else {
                    huc.disconnect();
                    return content.stream().map(Object::toString).collect(Collectors.joining());
                }
            }
            while(true);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return null;
    }

    //the stream received from the URL
    InputStream getURLStream()
    {
        //get longitude and latitude for selected city
        HttpURLConnection huc;
        InputStream website = null;
        float lat = 0f;
        float lon = 0f;
        switch (Cities.getItemAt(Cities.getSelectedIndex()))
        {
            case "Skelleftea":
                lat = 64.4444f;
                lon = 20.9644f;
                break;
            case "Kage":
                lat = 64.8444f;
                lon = 20.9844f;
                break;
            case "Stockholm":
                lat = 59.3250f;
                lon = 18.0707f;
                break;
        }

        //HttpURLConnection
        try
        {
            URL url = new URL("https://api.met.no/weatherapi/locationforecast/2.0/classic?lat=" + lat + "&lon=" + lon);
            huc = (HttpURLConnection)url.openConnection();
            huc.setRequestProperty("User-Agent", "low flying 747");
            huc.setRequestMethod("GET");
            website = (InputStream) huc.getContent();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return website;
    }

    void ParseXml(InputStream stream)
    {
        //parse xml
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            UserHandler userHandler = new UserHandler();
            saxParser.parse(stream, userHandler);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
}