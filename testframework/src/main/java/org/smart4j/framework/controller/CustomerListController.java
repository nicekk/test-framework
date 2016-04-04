/**
 * 
 */
package org.smart4j.framework.controller;

import java.util.List;

import org.smart4j.framework.annotation.Action;
import org.smart4j.framework.annotation.Controller;
import org.smart4j.framework.annotation.Inject;
import org.smart4j.framework.bean.Param;
import org.smart4j.framework.bean.View;
import org.smart4j.framework.model.Customer;
import org.smart4j.framework.service.CustomerService;


/**
 * 
 *
 * Author: wangkai
 * Edit Time: 2016年4月4日  下午5:49:59 
 */
@Controller
public class CustomerListController {
	
	@Inject
	private CustomerService customerService;
	
	@Action("get:/init")
	public Object doGet(Param param) {
		List<Customer> list = customerService.getCustomerList();
		View view = new View("customer.jsp");
		view.addModel("customerList", list);
		view.addModel("test", new String("hello,world"));
		return view;
	}
	
	@Action("get:/test")
	public Object test(Param param) {
		View view = new View("test.jsp");
		view.addModel("test", new String("hello,world"));
		return view;
	}
}
