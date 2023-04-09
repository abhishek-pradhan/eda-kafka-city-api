package com.mts.cityapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mts.cityapi.events.DetailEntity;
import com.mts.cityapi.events.DomainEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OutboundApi {
    private static final Logger logger = LoggerFactory.getLogger(OutboundApi.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Value("${outbound-api-base-url}")
    private String outbound_api_base_url;

    @Value("${outbound-api-x-api-key}")
    private String outbound_api_x_api_key;

    public String publishMessage(String city) throws IOException {
        String response = getCityInfo(city);

        DetailEntity detailEntity = new DetailEntity();
        detailEntity.setJsonResult(response);

        DomainEvent domainEvent = new DomainEvent();
        domainEvent.setDetailEntity(detailEntity);
        String messageKey = domainEvent.getId().toString();

        this.kafkaProducer.sendMessage(messageKey, domainEvent);

        return "Published 1 message, key=" + messageKey +", value=" + domainEvent.toString();
    }

    public String getCityInfo(String city) throws IOException {
        String encodedCity = URLEncoder.encode(city,"UTF-8"); // to handle spaces in City names
        String urlWithCity = outbound_api_base_url + "v1/city?name=" + encodedCity;
        logger.info("urlWithCity = " + urlWithCity);

        URL url = new URL(urlWithCity);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/json");
        connection.setRequestProperty("x-api-key", outbound_api_x_api_key);
        InputStream responseStream = connection.getInputStream();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseStream);
        // result = root.path("fact").asText();
        String result = root.toString();
        logger.info("result = " + result);

        return result;
    }

    public void consumeMessage(DomainEvent domainEvent)
    {

    }

    public String getRandomCity()
    {
        // https://en.wikipedia.org/wiki/List_of_largest_cities
        String[] largestCities = {
                "Tokyo",
                "Delhi",
                "Shanghai",
                "São Paulo",
                "Mexico City",
                "Cairo",
                "Mumbai",
                "Beijing",
                "Dhaka",
                "Osaka",
                "New York",
                "Buenos Aires",
                "Chongqing",
                "Istanbul",
                "Kolkata",
                "Manila",
                "Lagos",
                "Rio de Janeiro",
                "Tianjin",
                "Kinshasa",
                "Guangzhou",
                "Los Angeles",
                "Moscow",
                "Shenzhen",
                "Bangalore",
                "Paris",
                "Bogotá",
                "Jakarta",
                "Chennai",
                "Lima",
                "Bangkok",
                "Seoul",
                "Nagoya",
                "Hyderabad",
                "London",
                "Tehran",
                "Chicago",
                "Chengdu",
                "Nanjing",
                "Wuhan",
                "Luanda",
                "Ahmedabad",
                "Kuala Lumpur",
                "Hong Kong",
                "Dongguan",
                "Hangzhou",
                "Foshan",
                "Shenyang",
                "Riyadh",
                "Baghdad",
                "Santiago",
                "Surat",
                "Madrid",
                "Suzhou",
                "Pune",
                "Harbin",
                "Houston",
                "Dallas",
                "Toronto",
                "Dar es Salaam",
                "Miami",
                "Belo Horizonte",
                "Singapore",
                "Philadelphia",
                "Atlanta",
                "Fukuoka",
                "Khartoum",
                "Barcelona",
                "Johannesburg",
                "Saint Petersburg",
                "Qingdao",
                "Dalian",
                "Washington",
                "Yangon",
                "Alexandria",
                "Jinan",
                "Guadalajara"
        };

        List<String> cities = Arrays.asList(largestCities);

        // get a random city
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(cities.size());
        String randomCity = cities.get(index);

        logger.info("Random city=" + randomCity);

        return randomCity;
    }
}
