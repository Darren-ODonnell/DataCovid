package com.data.covid.services;



import com.data.covid.models.Country;
import com.data.covid.models.CovidVariant;
import com.data.covid.repositories.CountryRepository;
import com.data.covid.repositories.CovidVariantRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.List;


public class DataLoader implements CommandLineRunner {

    @Autowired
    CovidVariantRepository variantRepository;
    @Autowired
    CountryRepository countryRepository;

    public DataLoader()  {

    }
    @Override
    public void run(String... args) throws Exception {
        init();
    }

    public void init() throws IOException, ParserConfigurationException, SAXException {


        int weekToParse = parseCurrentWeek();

        List<Country> countries = parseCountryData(weekToParse);

        File file = new File("variants.xml");

        countries = XMLParser.getInstance().getCountryCodes(file, countries);

        List<CovidVariant> variants = parseCovidVariants(file, countries);

        persistData(countries, variants);
    }

    private void persistData(List<Country> countries, List<CovidVariant> variants) {
        // clear db before start
        variantRepository.deleteAll();
        countryRepository.deleteAll();
        // load db.
        countryRepository.saveAll(countries);
        variantRepository.saveAll(variants);

    }

    private List<CovidVariant> parseCovidVariants(File file, List<Country> countries) throws IOException, ParserConfigurationException, SAXException {
        return XMLParser.getInstance().parseCovidVariants(file, countries);
    }

    private List<Country> parseCountryData(int week) throws IOException, SAXException, ParserConfigurationException {
        File file = new File("download.xml");
        String weekStr = "2022-W" + week;
        return XMLParser.getInstance().parseCountries(file, weekStr);
    }

    public int parseCurrentWeek() throws IOException {
        String url = "https://www.epochconverter.com/weeks/2022";
        Document document = Jsoup.connect(url).get();

        Elements rows = document.getElementsByClass("currentWeek");
        String textResult = rows.get(0).text();
        String weekNo = textResult.substring(5,7);

        int weekNumber = Integer.parseInt(weekNo);
        return weekNumber - 10;

    }
}
