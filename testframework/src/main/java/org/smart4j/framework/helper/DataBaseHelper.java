/**
 * 
 */
package org.smart4j.framework.helper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.helper.ConfigHelper;
import org.smart4j.framework.util.CollectionUtil;
import org.smart4j.framework.util.StringUtil;

/**
 * 数据库操作助手类
 * 
 * @author: wangkai
 * 
 *          Date: 2016年4月3日 上午1:15:29
 */
public class DataBaseHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseHelper.class);

	private static final QueryRunner QUERY_RUNNER = new QueryRunner();

	private static final ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal<Connection>();

	private static final BasicDataSource DATA_SOURCE;

	public static final String DRIVER;

	public static final String URL;

	public static final String USERNAME;

	public static final String PASSWORD;

	static {
		DRIVER = ConfigHelper.getJdbcDriver();
		URL = ConfigHelper.getJdbcUrl();
		USERNAME = ConfigHelper.getJdbcUserName();
		PASSWORD = ConfigHelper.getJdbcPassword();

		DATA_SOURCE = new BasicDataSource();
		DATA_SOURCE.setDriverClassName(DRIVER);
		DATA_SOURCE.setUrl(URL);
		DATA_SOURCE.setUsername(USERNAME);
		DATA_SOURCE.setPassword(PASSWORD);

		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			LOGGER.error("could not find jdbc driver", e);
		}
	}

	/**
	 * 获取数据库连接
	 */
	public static Connection getConnection() {
		Connection connection = CONNECTION_HOLDER.get();
		if (connection == null) {
			try {
				connection = DATA_SOURCE.getConnection();
			} catch (SQLException e) {
				LOGGER.error("get connection failure", e);
				throw new RuntimeException(e);
			} finally {
				CONNECTION_HOLDER.set(connection);
			}
		}
		return connection;
	}

	/**
	 * 查询所有实体记录
	 * 
	 * @param entityClass
	 * @param connection
	 * @param sql
	 * @return
	 */
	public static <T> List<T> queryEntityList(Class<T> entityClass, String sql) {
		Connection connection = getConnection();
		List<T> entityList = null;
		try {
			entityList = QUERY_RUNNER.query(connection, sql, new BeanListHandler<T>(entityClass));
		} catch (SQLException e) {
			LOGGER.error("query entity list failure", e);
		}
		return entityList;
	}

	/**
	 * 查询单个实体
	 * 
	 * @param entityClass
	 * @param sql
	 * @param params
	 * @return
	 */
	public static <T> T queryEntity(Class<T> entityClass, String sql, Object... params) {
		T entity;
		try {
			Connection connection = getConnection();
			entity = QUERY_RUNNER.query(connection, sql, new BeanHandler<T>(entityClass), params);
		} catch (SQLException e) {
			LOGGER.error("query entity failure", e);
			throw new RuntimeException(e);
		}
		return entity;
	}

	/**
	 * 综合查询
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public static List<Map<String, Object>> executeQuery(String sql, Object... params) {
		List<Map<String, Object>> result;
		try {
			Connection connection = getConnection();
			result = QUERY_RUNNER.query(connection, sql, new MapListHandler(), params);
		} catch (Exception e) {
			LOGGER.error("execute query failure", e);
			throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * 执行更新，
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public static int executeUpdate(String sql, Object... params) {
		int rows = 0;
		try {
			Connection connection = getConnection();
			rows = QUERY_RUNNER.update(connection, sql, params);
		} catch (SQLException e) {
			LOGGER.error("execute update failure", e);
			throw new RuntimeException(e);
		}
		return rows;
	}

	/**
	 * 新增实体
	 * 
	 * @param entityClass
	 * @param fieldMap
	 * @return
	 */
	public static <T> boolean insertEntity(Class<T> entityClass, Map<String, Object> fieldMap) {
		if (CollectionUtil.isEmpty(fieldMap)) {
			LOGGER.error("can not insert entity:fieldMap is empty");
			return false;
		}
		String sql = "insert into " + getTableName(entityClass);
		StringBuilder columns = new StringBuilder(" (");
		StringBuilder values = new StringBuilder(" (");
		for (String fieldName : fieldMap.keySet()) {
			columns.append(fieldName).append(", ");
			values.append("?, ");
		}
		columns.replace(columns.lastIndexOf(", "), columns.length(), ")");
		values.replace(values.lastIndexOf(", "), values.length(), ")");
		sql += columns + " VALUES " + values;
		Object[] params = fieldMap.values().toArray();
		return executeUpdate(sql, params) == 1;
	}

	/**
	 * 根据id更新一条记录
	 * 
	 * @param entityClass
	 * @param id
	 * @param fieldMap
	 * @return
	 */
	public static <T> boolean updateEntity(Class<T> entityClass, long id, Map<String, Object> fieldMap) {
		if (CollectionUtil.isEmpty(fieldMap)) {
			LOGGER.error("can not insert entity:fieldMap is empty");
			return false;
		}
		String sql = "UPDATE " + getTableName(entityClass) + " SET ";
		StringBuilder columns = new StringBuilder();
		for (String fieldName : fieldMap.keySet()) {
			columns.append(fieldName).append("=?, ");
		}
		sql += columns.substring(0, columns.lastIndexOf(", ")) + " WHERE ID=?";
		List<Object> paramList = new ArrayList<Object>();
		paramList.addAll(fieldMap.values());
		paramList.add(id);
		Object[] params = paramList.toArray();
		return executeUpdate(sql, params) == 1;
	}

	/**
	 * 根据id删除一条记录
	 * 
	 * @param entityClass
	 * @param id
	 * @return
	 */
	public static <T> boolean deleteEntity(Class<T> entityClass, long id) {
		String sql = "DELETE FROM " + getTableName(entityClass) + " WHERE ID=?";
		return executeUpdate(sql, id) == 1;
	}

	/**
	 * 根据实体类名，获取数据库表名：t_实体类名
	 * 
	 * @param entityClass
	 * @return
	 */
	public static <T> String getTableName(Class<T> entityClass) {
		return "T_" + StringUtil.toUpperCase(entityClass.getSimpleName());
	}

}
