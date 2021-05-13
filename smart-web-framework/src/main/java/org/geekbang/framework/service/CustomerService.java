package org.geekbang.framework.service;

import org.geekbang.framework.helper.DatabaseHelper;
import org.geekbang.framework.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 客户服务
 */
public class CustomerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

    /**
     * 获取客户列表
     */
    public List<Customer> getCustomerList() {
        // List<Customer> customerList = new ArrayList<>();

        // Connection     connection = DatabaseHelper.getConnection();
        String sql = "SELECT * FROM customer ";
        return DatabaseHelper.queryEntityList(Customer.class, sql);

        // Connection     connection   = null;
        // try {
        //     connection = DatabaseHelper.getConnection();
        //     PreparedStatement statement = connection.prepareStatement(sql);
        //     ResultSet         rs        = statement.executeQuery();
        //
        //     while (rs.next()) {
        //         Customer customer = new Customer();
        //
        //         customer.setId(rs.getLong("id"));
        //         customer.setName(rs.getString("name"));
        //         customer.setContact(rs.getString("contact"));
        //         customer.setTelephone(rs.getString("telephone"));
        //         customer.setEmail(rs.getString("email"));
        //         customer.setRemark(rs.getString("remark"));
        //
        //         customerList.add(customer);
        //     }
        //
        // } catch (SQLException e) {
        //
        //     LOGGER.error("execute sql query error", e);
        // } finally {
        //     DatabaseHelper.closeConnection(connection);
        // }

    }

    /**
     * 获取客户
     */
    public Customer getCustomer(Long id) {

        String sql = "SELECT * FROM customer where id=? ";
        return DatabaseHelper.getEntity(Customer.class, sql, id);
    }

    /**
     * 创建客户
     */
    public boolean createCustomer(Map<String, Object> fieldMap) {
        return DatabaseHelper.insertEntity(Customer.class, fieldMap);
    }

    /**
     * 更新客户信息
     */
    public boolean updateCustomer(Long id, Map<String, Object> fieldMap) {
        return DatabaseHelper.updateEntity(Customer.class, id, fieldMap);
    }

    /**
     * 删除客户
     */
    public boolean deleteCustomer(Long id) {
        return DatabaseHelper.deleteEntity(Customer.class, id);
    }
}
