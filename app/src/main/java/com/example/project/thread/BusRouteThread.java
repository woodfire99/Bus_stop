package com.example.project.thread;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class BusRouteThread extends Thread{
    ArrayList<String> routes;
    int busID;

    public BusRouteThread(int busID){
        this.busID = busID;
    }

    @Override
    public void run() {
        Document document = getBusRouteAPI(busID);
        routes = parsingBusRouteXML(document);
    }

    public ArrayList<String> getRoutes(){
        return routes;
    }

    private Document getBusRouteAPI(int busID){
        Document doc = null;
        String url = "http://openapitraffic.daejeon.go.kr/api/rest/busRouteInfo/getStaionByRoute";
        try {
            url += "?" + URLEncoder.encode("serviceKey", "UTF-8") + "=VJn97nYprDseZFtTE9k6fHXhaWq2APaFCPOS/XauqQzo9XgJNOJb1Mt52nl8116fKruyHdMcyfYT2qUoayeTGw=="; /*Service Key*/
            url += "&" + URLEncoder.encode("BusRouteId", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(busID), "UTF-8");
            DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
            doc = dBuilder.parse(url);
        } catch (Exception e){
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

    private ArrayList<String> parsingBusRouteXML(Document document){
        ArrayList<String> arr = new ArrayList<>();
        try{
            document.getDocumentElement().normalize();
            NodeList busList = document.getElementsByTagName("itemList");
            for(int i=0; i<busList.getLength(); i++){
                Node nNode = busList.item(i);
                Element eElement = (Element) nNode;
                arr.add(getTagValue("BUSSTOP_NM", eElement));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return arr;
    }
}
