package com.data.covid.services;

import com.data.covid.models.Country;
import com.data.covid.models.CovidVariant;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class XMLParser {
    private static XMLParser instance;

    private XMLParser(){

    }

    public static XMLParser getInstance(){
        instance = (instance == null) ? new XMLParser(): instance;
        return instance;
    }

    public List<Country> parseCountries(File file, String weekStr) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);

        HashMap<String, List<Double>> countryOccs = new HashMap<>();

        NodeList sheets = doc.getElementsByTagName("fme:Sheet");
        for(int i = 0; i < sheets.getLength(); i++){
            Node sheet  = sheets.item(i);

            if(sheet.getNodeType() == Node.ELEMENT_NODE){

                Node co = ((Element) sheet).getElementsByTagName("fme:country").item(0);
                String country = co.getTextContent();

                Node yw = ((Element) sheet).getElementsByTagName("fme:year_week").item(0);
                String yearWeek = yw.getTextContent();

                Node occ = ((Element) sheet).getElementsByTagName("fme:value").item(0);
                double occurrences = Double.parseDouble(occ.getTextContent());

                Node in = ((Element) sheet).getElementsByTagName("fme:indicator").item(0);
                String indicator = in.getTextContent();

                if(yearWeek.equals(weekStr) && indicator.equals("Daily hospital occupancy")){

                    List<Double> list;

                    if(countryOccs.containsKey(country)){
                        list = countryOccs.get(country);
                    }else{
                        list = new ArrayList<>();
                    }
                    list.add(occurrences);
                    countryOccs.put(country, list);

                }

            }
        }

        HashMap<String, Double> countryAverages = new HashMap<>();

        // Loop through lists in hashmap and average per country
        for(String country: countryOccs.keySet()){
            List<Double> dailyOccs = countryOccs.get(country);
            Double total = 0.0;
            for(Double i: dailyOccs){
                total += i;
            }
            Double average = total/dailyOccs.size();

            countryAverages.put(country,average);
        }
        List<Country> countries = new ArrayList<>();
        for(String c: countryAverages.keySet()){
            Country country = new Country();
            country.setName(c);
            country.setWeek(weekStr);
            country.setOccupancy(countryAverages.get(c));
            countries.add(country);
        }
        return countries;
    }

    public List<CovidVariant> parseCovidVariants(File file, List<Country> countries) throws IOException, SAXException, ParserConfigurationException {
        List<CovidVariant> variants = new ArrayList<>();

        Map<String, Country> countryCodes = new HashMap<>();
        for(Country country: countries){
            countryCodes.put(country.getCode(), country);
        }

        List<String> countryNames = countries.stream()
                .map(Country::getName)
                .collect(Collectors.toList());

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);

        NodeList sheets = doc.getElementsByTagName("fme:Sheet");
        for(int i = 0; i < sheets.getLength(); i++){
            Node sheet  = sheets.item(i);

            if(sheet.getNodeType() == Node.ELEMENT_NODE){

                Node co = ((Element) sheet).getElementsByTagName("fme:country").item(0);
                String country = co.getTextContent();

                Node cc = ((Element) sheet).getElementsByTagName("fme:country_code").item(0);
                String countryCode = cc.getTextContent();

                Node va = ((Element) sheet).getElementsByTagName("fme:variant").item(0);
                String variant = va.getTextContent();

                Node occ = ((Element) sheet).getElementsByTagName("fme:number_detections_variant").item(0);
                int occurrences = Integer.parseInt(occ.getTextContent());

                if(countryNames.contains(country)){
                    boolean exists = false;


                    for(CovidVariant var: variants){
                        String mapCode = countryCodes.get(countryCode).getCode();
                        String loopCode = var.getCountryCode().getCode();
                        if(var.getName().equals(variant)
                                && mapCode.equals(loopCode)
                        ){
                            var.setOccurrences(var.getOccurrences() + occurrences);
                            exists = true;
                        }
                    }
                    if(!exists) {
                        CovidVariant covidVariant = new CovidVariant();
                        covidVariant.setName(variant);
                        covidVariant.setCountryCode(countryCodes.get(countryCode));
                        covidVariant.setOccurrences(occurrences);
                        variants.add(covidVariant);
                    }
                }

            }
        }
        return variants;
    }
    public List<Country> getCountryCodes(File file, List<Country> countries) throws IOException, SAXException, ParserConfigurationException {

        Map<String, Country> countryMap = new HashMap<>();
        for(Country country: countries){
            countryMap.put(country.getName(), country);
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);

        NodeList sheets = doc.getElementsByTagName("fme:Sheet");
        for(int i = 0; i < sheets.getLength(); i++){
            Node sheet  = sheets.item(i);

            if(sheet.getNodeType() == Node.ELEMENT_NODE){

                Node co = ((Element) sheet).getElementsByTagName("fme:country").item(0);
                String country = co.getTextContent();

                Node cc = ((Element) sheet).getElementsByTagName("fme:country_code").item(0);
                String countryCode = cc.getTextContent();

                if(countryMap.containsKey(country)){
                    Country c = countryMap.get(country);
                    c.setCode(countryCode);
                    countryMap.put(country, c);
                }

            }
        }
        return countries;
    }
}
