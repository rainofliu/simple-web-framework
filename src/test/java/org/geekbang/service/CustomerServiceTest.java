package org.geekbang.service;

import org.geekbang.helper.DatabaseHelper;
import org.geekbang.model.Customer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerServiceTest {

    /**
     * 后续是否可以改成依赖注入的形式
     */
    private final CustomerService customerService;

    public CustomerServiceTest() {
        this.customerService = new CustomerService();
    }

    /**
     * 在单元测试执行前会初始化，准备一些需要的环境
     */
    @Before
    public void init() throws IOException {
        // 初始化仅仅用于测试的数据库
        String sqlFileName = "sql/customer-init.sql";
        DatabaseHelper.executeSqlFile(sqlFileName);

    }

    @Test
    public void getCustomerListTest() throws Exception {
        List<Customer> customers = customerService.getCustomerList();
        Assert.assertEquals(2, customers.size());
    }

    @Test
    public void getCustomerTest() throws Exception {
        Customer customer = customerService.getCustomer(1L);
        Assert.assertNotNull(customer);
    }

    @Test
    public void createCustomerTest() throws Exception {
        Map<String, Object> fieldMap = new HashMap<>(8);
        fieldMap.put("name", "ajin");
        fieldMap.put("contact", "ajin");
        fieldMap.put("telephone", "1888888888");
        boolean result = customerService.createCustomer(fieldMap);
        Assert.assertTrue(result);
    }

    @Test
    public void updateCustomerTest() throws Exception {
        Map<String, Object> fieldMap = new HashMap<>(8);
        fieldMap.put("contact", "Eric");

        boolean result = customerService.updateCustomer(1L, fieldMap);
        Assert.assertTrue(result);

    }

    @Test
    public void deleteCustomer() throws Exception {
        boolean result = customerService.deleteCustomer(1L);
        Assert.assertTrue(result);
    }
}
