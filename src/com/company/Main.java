package com.company;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Timer;
import java.util.TimerTask;
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
    //set a killTimer to how frequently the prognosis will update

    Timer timer;
    long lifespan;

    public static void main(String[] args) {
        new GUI();
    }
}

class UserHandler extends DefaultHandler {
        String from, to;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equalsIgnoreCase("time")) {
                from = attributes.getValue(1).substring(11, 13);
                to = attributes.getValue(2).substring(11, 13);
                if (from.equals(to)) return;
                System.out.print("kl. " + from + " - " + to);
            }
            if (qName.equalsIgnoreCase("temperature")) {
                System.out.println("\t" + attributes.getValue(2) + " C");
            }
            super.startElement(uri, localName, qName, attributes);
        }
    }

class GUI implements ActionListener {
    long lifespan;
    Timer timer = new Timer();
    TimerTask timerTask;

    JFrame frame;

    JComboBox<String> Cities;
    JTextField lifeTime;
    JButton submit;
    JLabel desc;

    JPanel panel;
    JTextField hour;
    JLabel temperature;



    public GUI() {
        //set window
        frame = new JFrame();

        desc = new JLabel("update time in minutes: ");

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
        timer.schedule(timerTask, lifespan);
        try {
                ParseXml(getURLStream());
        } catch (ParserConfigurationException | SAXException | IOException Error) {
            Error.printStackTrace();
        }
    }

    InputStream getURLStream() throws IOException {
        float lat, lon; lat = lon = 0f;
        HttpURLConnection huc;
        InputStream website;

        System.out.println(Cities.getItemAt(Cities.getSelectedIndex()) + ": ");
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
        huc = (HttpURLConnection) url.openConnection();
        huc.setRequestProperty("User-Agent", "low flying 747");
        huc.setRequestMethod("GET");
        website = (InputStream) huc.getContent();
        return website;
    }

    void ParseXml(InputStream stream) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        UserHandler userHandler = new UserHandler();
        saxParser.parse(stream, userHandler);
    }
}
