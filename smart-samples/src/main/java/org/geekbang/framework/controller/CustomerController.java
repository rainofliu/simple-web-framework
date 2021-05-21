package org.geekbang.framework.controller;

import org.geekbang.framework.annotation.Action;
import org.geekbang.framework.annotation.Controller;
import org.geekbang.framework.annotation.Inject;
import org.geekbang.framework.bean.Data;
import org.geekbang.framework.bean.FileParam;
import org.geekbang.framework.bean.Param;
import org.geekbang.framework.bean.View;
import org.geekbang.framework.helper.ServletHelper;
import org.geekbang.framework.model.Customer;
import org.geekbang.framework.service.CustomerService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class CustomerController {

    @Inject
    private CustomerService customerService;

    /**
     * 进入客户列表页面
     */
    @Action("get:/customer")
    public View index() {
        // 获取HttpServletRequest（借助于ThreadLocal）
        HttpServletRequest request      = ServletHelper.getRequest();
        List<Customer>     customerList = customerService.getCustomerList();
        return new View("customer.jsp").addModel("customerList", customerList);

    }

    @Action("post:/customer_create")
    public Data createSubmit(Param param) {
        Map<String, Object> fieldMap = param.getParamMap();
        // 获取上传的文件参数
        FileParam fileParam = param.getFile("photo");
        boolean   result    = customerService.createCustomer(fieldMap, fileParam);
        return new Data(result);
    }
}
