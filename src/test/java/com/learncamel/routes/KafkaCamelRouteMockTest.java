package com.learncamel.routes;

import com.learncamel.domain.Item;
import com.learncamel.exception.DataException;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.DisableJmx;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by z001qgd on 1/13/18.
 */
@ActiveProfiles("mock")
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisableJmx(true)
public class KafkaCamelRouteMockTest extends CamelTestSupport{


    @Autowired
    private CamelContext context;

    @Autowired
    protected CamelContext createCamelContext() {
        return context;
    }

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private ConsumerTemplate consumerTemplate;

    @Override
    protected RouteBuilder createRouteBuilder(){
        return new KafkaCamelRoute();
    }

    @Autowired
    Environment environment;

    @Before
    public void setUp(){

    }


    @Test
    public void unmarshallTest(){
        String input = "{\"transactionType\":\"ADD\",\"sku\":\"300\",\"itemDescription\":\"Samsung TV\",\"price\":\"500.00\"}";
        String output = "insert into items(sku,item_description,price) values('300','Samsung TV',500.00)";
        String item = String.valueOf(producerTemplate.requestBodyAndHeader(environment.getProperty("fromRoute"),input,"env","mock"));
        System.out.println("Item -> "+item);
        assertEquals(output,item);
    }

    @Test(expected = CamelExecutionException.class)
    public void unmarshallTest_ERROR(){
        String input = "{\"transactionType\":\"ADD\",\"sku\":\"\",\"itemDescription\":\"Samsung TV\",\"price\":\"500.00\"}";
        Item item = (Item) producerTemplate.requestBodyAndHeader(environment.getProperty("fromRoute"),input,"env","mock");
        System.out.println("Item -> "+item);
        assertEquals("100",item.getSku());
    }

}
