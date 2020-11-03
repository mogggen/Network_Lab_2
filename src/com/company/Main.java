package com.company;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {
    //https://www.tutorialspoint.com/java_xml/java_sax_parse_document.htm : adjust test code to your needs

    public static String start, stop; //to filter the outPutStream

    public static void main(String[] args) {
        new GUI();
    }
}

class UserHandler extends DefaultHandler {
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("time")) {
            Main.start = attributes.getValue(1).substring(11, 13);
            Main.stop = attributes.getValue(2).substring(11, 13);
            if (Main.start.equals(Main.stop)) return;
            System.out.print("kl. " + Main.start + " - " + Main.stop);
        }
        if (qName.equalsIgnoreCase("temperature")) {
            System.out.println("\t" + attributes.getValue(2) + " C");
        }
        super.startElement(uri, localName, qName, attributes);
    }
}

class GUI implements ActionListener {
    long lifespan;
    Timer timer;

    JFrame frame;

    JComboBox<String> Cities;
    JTextField lifeTime;
    JButton submit;
    JLabel desc;

    JPanel panel;
    JTextField manual;
    JLabel city;
    JTextField hour;
    JLabel temperature;

    public GUI() {
        //set window
        frame = new JFrame();

        desc = new JLabel("lifetime in minutes: ");

        //get lifespan value
        lifeTime = new JTextField();
        lifeTime.addActionListener(this);

        //select city
        Cities = new JComboBox<>();
        Cities.addItem("Skelleftea");
        Cities.addItem("Kage");
        Cities.addItem("Stockholm");
        hour = new JTextField();
        hour.addActionListener(this);

        //get prognosis
        submit = new JButton("get prognosis");
        submit.addActionListener(this);
        city = new JLabel();
        temperature = new JLabel("temperature in ");

        //panel to hold all content of the GUI
        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new GridLayout(4, 3));

        panel.add(desc);        // row 0 cols 0
        panel.add(lifeTime);    // row 0 cols 1

        panel.add(Cities);      // row 1 cols 0
        panel.add(hour);        // row 1 cols 1
        panel.add(submit);      // row 1 cols 2

        panel.add(temperature); // row 2 cols 0

        //the GUI mainframe
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("GUI Client");
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            ParseXml(getURLStream(), Main.start, Main.stop);
            timer.start();
        } catch (ParserConfigurationException | SAXException | IOException Error) {
            Error.printStackTrace();
        }
    }

    InputStream getURLStream() throws IOException {
        float lat = 0f, lon = 0f;
        temperature.setText("temperature in " + Cities.getItemAt(Cities.getSelectedIndex()) + ": ");

        switch (Cities.getItemAt(Cities.getSelectedIndex())) {
            case "Skelleftea" -> {
                lat = 64.4444f;
                lon = 20.9644f;
            }
            case "Kage" -> {
                lat = 64.8444f;
                lon = 20.9844f;
            }
            case "Stockholm" -> {
                lat = 59.3250f;
                lon = 18.0707f;
            }
        }

        //HttpURLConnection
        URL url = new URL("https://api.met.no/weatherapi/locationforecast/2.0/classic?lat=" + lat + "&lon=" + lon);
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        huc.setRequestProperty("User-Agent", "low flying 747");
        huc.setRequestMethod("GET");
        return (InputStream) huc.getContent();
    }

    void ParseXml(InputStream stream, String start, String stop) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        UserHandler userHandler = new UserHandler();
        saxParser.parse(stream, userHandler);
    }
}