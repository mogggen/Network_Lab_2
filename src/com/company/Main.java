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

    public static String start, stop, tempForTime; //to filter the outPutStream

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
        }
        if (qName.equalsIgnoreCase("temperature") && Main.start.equals(Main.tempForTime)
        && Integer.parseInt(Main.stop) - Integer.parseInt(Main.start) == 1) {
            System.out.println("\t" + attributes.getValue(2) + " C");
        }
        super.startElement(uri, localName, qName, attributes);
    }
}

class GUI implements ActionListener {
    long lifespan;

    JFrame frame;

    JComboBox<String> cbxCities;
    JTextField tfdLifeSpan;
    JButton btnSubmit;
    JLabel lblLifeSpan;

    JPanel panel;
    JLabel lblHour;
    JTextField tfdHour;
    JLabel temperature;

    public GUI() {
        //set window
        frame = new JFrame();

        lblLifeSpan = new JLabel("lifetime in ms: ");

        //get lifespan value
        tfdLifeSpan = new JTextField();
        tfdLifeSpan.addActionListener(this);

        //select city
        cbxCities = new JComboBox<>();
        cbxCities.addItem("Skelleftea");
        cbxCities.addItem("Kage");
        cbxCities.addItem("Stockholm");

        lblHour = new JLabel("time (h): ");

        tfdHour = new JTextField();
        tfdHour.addActionListener(this);

        //get prognosis
        btnSubmit = new JButton("get prognosis");
        btnSubmit.addActionListener(this);
        temperature = new JLabel("temperature in ");

        //panel to hold all content of the GUI
        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));
        panel.setLayout(new GridLayout(4, 2));

        panel.add(lblLifeSpan);    // row 0 cols 0
        panel.add(tfdLifeSpan);    // row 0 cols 1

        panel.add(lblHour);        // row 1 cols 1
        panel.add(tfdHour);        // row 1 cols 1
        panel.add(cbxCities);      // row 1 cols 0
        panel.add(btnSubmit);      // row 1 cols 2

        panel.add(temperature);    // row 2 cols 0

        //the GUI mainframe
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("GUI Client");
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Main.tempForTime = tfdHour.getText();
        try {
            while(true) {
                ParseXml(getURLStream(), Main.start, Main.stop);
                Thread.sleep(Integer.parseInt(tfdLifeSpan.getText()));
            }
        } catch (ParserConfigurationException | SAXException | IOException | InterruptedException Error) {
            Error.printStackTrace();
        }
    }

    InputStream getURLStream() throws IOException {
        float lat = 0f, lon = 0f;
        temperature.setText("temperature in " + cbxCities.getItemAt(cbxCities.getSelectedIndex()) + ": ");

        switch (cbxCities.getItemAt(cbxCities.getSelectedIndex())) {
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
        SAXParserFactory factory;
        SAXParser saxParser;
        UserHandler userHandler;
        if (true) {
            factory = SAXParserFactory.newInstance();
            saxParser = factory.newSAXParser();
            userHandler = new UserHandler();
            saxParser.parse(stream, userHandler);
        }
    }
}