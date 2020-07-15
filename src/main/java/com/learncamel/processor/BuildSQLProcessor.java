package com.learncamel.processor;

import com.learncamel.domain.Item;
import com.sun.org.apache.xpath.internal.operations.String;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@Slf4j
public class BuildSQLProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Item item = exchange.getIn().getBody(Item.class);
        log.info("Item is -> {}",item);
        StringBuilder query= new StringBuilder();
        java.lang.String tblName = "items";

        if(item.getTransactionType().equals("ADD")){
            query.append("insert into "+tblName+"(sku,item_description,price) values('" + item.getSku() +"','"+item.getItemDescription()+"',"+item.getPrice()+")");
        }else if(item.getTransactionType().equals("UPDATE")) {
            query.append("update "+tblName+" set price="+item.getPrice()+" where sku='"+item.getSku()+"'");
        }else if(item.getTransactionType().equals("DELETE")){
            query.append("delete from "+tblName+" where sku='"+item.getSku()+"'");
        }
        log.info("Final Query -> {}",query);
        exchange.getIn().setBody(query);
        exchange.getIn().setHeader("skuId",item.getSku());
    }
}
