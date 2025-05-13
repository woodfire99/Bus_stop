package com.example.project.thread;

import com.example.project.bus.Bus;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class BusThread extends Thread {
    int stationID;
    ArrayList<Bus> buses;

    public BusThread(int stationID){
        this.stationID = stationID;
    }

    @Override
    public void run() {
        buses = parsingBusXML(stationID);
    }

    public ArrayList<Bus> getBuses(){
        return buses;
    }

    private Document getBusAPI(int stationID) {
        Document doc = null;
        String url = "http://openapitraffic.daejeon.go.kr/api/rest/arrive/getArrInfoByStopID";
        try {
            url += "?" + URLEncoder.encode("serviceKey", "UTF-8") + "=VJn97nYprDseZFtTE9k6fHXhaWq2APaFCPOS/XauqQzo9XgJNOJb1Mt52nl8116fKruyHdMcyfYT2qUoayeTGw=="; /*Service Key*/
            url += "&" + URLEncoder.encode("BusStopID", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(stationID), "UTF-8");
            DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
            doc = dBuilder.parse(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return doc;
    }

    private String getTagValue(String tag, Element eElement) {
        String result = "";
        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
        result = nlList.item(0).getTextContent();

        return result;
    }

    private ArrayList<Bus> parsingBusXML(int stationID) {
        Document document = getBusAPI(stationID);
        ArrayList<Bus> buses = new ArrayList<>();
        try {
            document.getDocumentElement().normalize();
            NodeList busList = document.getElementsByTagName("itemList");
            for (int i = 0; i < busList.getLength(); i++) {
                Node nNode = busList.item(i);
                Element eElement = (Element) nNode;
                String des = getTagValue("DESTINATION", eElement);
                int cd = Integer.parseInt(getTagValue("ROUTE_CD", eElement));
                int no = Integer.parseInt(getTagValue("ROUTE_NO", eElement));
                int msg = Integer.parseInt(getTagValue("MSG_TP", eElement));
                int extime = Integer.parseInt(getTagValue("EXTIME_MIN", eElement));
                Bus bus = new Bus(des, extime, cd, no, msg);
                buses.add(bus);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buses;
    }
}