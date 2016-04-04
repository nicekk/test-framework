/**
 * 
 */
package org.smart4j.framework.service;

import java.util.List;
import java.util.Map;

import org.smart4j.framework.annotation.Service;
import org.smart4j.framework.helper.DataBaseHelper;
import org.smart4j.framework.model.Customer;

/**
 * @author: wangkai
 * 
 *          Date: 2016年4月3日 上午12:10:30
 */
@Service
public class CustomerService {

	/**
	 * 获取客户列表
	 */
	public List<Customer> getCustomerList() {
		String sql = "SELECT * FROM T_CUSTOMER";
		return DataBaseHelper.queryEntityList(Customer.class, sql);
	}

	/**
	 * 获取客户
	 * 
	 * @param id
	 * @return
	 */
	public Customer getById(long id) {
		String sql = "SELECT * FROM T_CUSTOMER WHERE ID = ?";
		return DataBaseHelper.queryEntity(Customer.class, sql, id);
	}

	/**
	 * 创建客户
	 * 
	 * @param fieldMap
	 * @return
	 */
	public boolean createCustomer(Map<String, Object> fieldMap) {
		return DataBaseHelper.insertEntity(Customer.class, fieldMap);
	}

	/**
	 * 更新客户
	 * 
	 * @param id
	 * @param fieldMap
	 * @return
	 */
	public boolean updateCustomer(long id, Map<String, Object> fieldMap) {
		return DataBaseHelper.updateEntity(Customer.class, id, fieldMap);
	}

	/**
	 * 删除客户
	 * 
	 * @param id
	 * @return
	 */
	public boolean deleteCustomer(long id) {
		return DataBaseHelper.deleteEntity(Customer.class, id);
	}

}
