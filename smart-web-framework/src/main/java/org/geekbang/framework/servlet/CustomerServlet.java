package org.geekbang.framework.servlet;

import org.geekbang.framework.model.Customer;
import org.geekbang.framework.service.CustomerService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/customer")
public class CustomerServlet extends HttpServlet {

    private CustomerService customerService;

    @Override
    public void init() throws ServletException {
        this.customerService = new CustomerService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Customer> customerList = customerService.getCustomerList();
        request.setAttribute("customerList", customerList);
        request.getRequestDispatcher("/WEB-INF/view/customer.jsp").forward(request, response);
    }

    /**
     * 处理客户创建的请求
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
