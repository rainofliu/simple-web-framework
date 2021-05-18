package org.geekbang.framework.helper;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.geekbang.framework.utils.CollectionUtil;
import org.geekbang.framework.utils.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 数据库操作 辅助类
 *
 * @author ajin
 */
public final class DatabaseHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);

    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    private static final QueryRunner QUERY_RUNNER;

    private static final BasicDataSource DATA_SOURCE;

    /**
     * 引入{@link ThreadLocal}存储{@link Connection}
     */
    private static final ThreadLocal<Connection> CONNECTION_HOLDER;

    static {

        CONNECTION_HOLDER = new ThreadLocal<>();

        Properties jdbcConfigProperties = PropertiesUtil.loadProperties("config.properties");
        DRIVER = jdbcConfigProperties.getProperty("jdbc.driver");
        URL = jdbcConfigProperties.getProperty("jdbc.url");
        USERNAME = jdbcConfigProperties.getProperty("jdbc.username");
        PASSWORD = jdbcConfigProperties.getProperty("jdbc.password");
        // 加载mysql JDBC驱动
        // try {
        //     // 通过反射，此时会触发  驱动注册  -> DriverManager.registerDriver(new Driver());
        //     // 这里应该是多余的 FIXME
        //     // Class.forName(DRIVER);
        // } catch (ClassNotFoundException e) {
        //     LOGGER.error("can not load jdbc driver", e);
        // }

        QUERY_RUNNER = new QueryRunner();

        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setUrl(URL);
        DATA_SOURCE.setDriverClassName(DRIVER);
        DATA_SOURCE.setUsername(USERNAME);
        DATA_SOURCE.setPassword(PASSWORD);

    }

    /**
     * 查询实体列表
     *
     * @param <T>         实体类的具体类型
     * @param entityClass 实体类
     * @param sql         查询sql
     */
    @Deprecated
    public static <T> List<T> queryEntityList(Class<T> entityClass, Connection conn, String sql) {
        List<T> entityList;
        try {
            entityList = QUERY_RUNNER.query(conn, sql, new BeanListHandler<T>(entityClass));
        } catch (SQLException e) {
            LOGGER.error("query entity list failure", e);
            throw new RuntimeException(e);
        }
        // finally {
        //     closeConnection(conn);
        // }
        return entityList;
    }

    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object... params) {
        List<T> entityList;
        try {
            Connection connection = getConnection();
            entityList = QUERY_RUNNER.query(connection, sql, new BeanListHandler<T>(entityClass), params);
        } catch (SQLException e) {
            LOGGER.error("query entity list failure", e);
            throw new RuntimeException(e);
        }
        // finally {
        //     closeConnection();
        // }
        return entityList;
    }

    public static <T> T getEntity(Class<T> entityClass, String sql, Object... params) {
        T entity;
        try {
            Connection connection = getConnection();
            entity = QUERY_RUNNER.query(connection, sql, new BeanHandler<T>(entityClass), params);
        } catch (SQLException e) {
            LOGGER.error("query entity  failure", e);
            throw new RuntimeException(e);
        }
        // finally {
        //     closeConnection();
        // }
        return entity;

    }

    public static List<Map<String, Object>> executeQuery(String sql, Object... params) {
        List<Map<String, Object>> result;
        try {
            Connection connection = getConnection();
            result = QUERY_RUNNER.query(connection, sql, new MapListHandler(), params);
        } catch (Exception e) {
            LOGGER.error("execute query  failure", e);
            throw new RuntimeException(e);
        }
        // finally {
        //     closeConnection();
        // }
        return result;
    }

    /**
     * 执行更新语句(update/insert/delete)
     */
    public static int executeUpdate(String sql, Object... params) {
        int        rows       = 0;
        Connection connection = getConnection();
        try {
            rows = QUERY_RUNNER.update(connection, sql, params);
        } catch (SQLException e) {
            LOGGER.error("execute update  failure", e);
            throw new RuntimeException(e);
        }
        // finally {
        //     closeConnection();
        // }
        return rows;
    }

    /**
     * 新增实体
     */
    public static <T> boolean insertEntity(Class<T> entityClass, Map<String, Object> fieldMap) {
        if (CollectionUtil.isEmpty(fieldMap)) {
            LOGGER.error("can not insert entity: fieldMap is empty");
            return false;
        }
        // such as INSERT INTO `customer`(name, contact, telephone, email, remark)
        // VALUES ('customer1','Jack','13512345678', 'jack@gmail.com',null); 
        String sql = "INSERT INTO " + getTableName(entityClass);
        // (name, contact, telephone, email, remark)
        StringBuilder columns = new StringBuilder("(");
        // (? ,? ,? ,'jack@gmail.com',null)
        StringBuilder values = new StringBuilder("(");

        for (String fieldName : fieldMap.keySet()) {
            columns.append(fieldName).append(", ");
            values.append("?, ");
        }
        columns.replace(columns.lastIndexOf(", "), columns.length(), ")");
        values.replace(values.lastIndexOf(", "), values.length(), ")");
        sql += columns.toString() + " VALUES " + values.toString();

        Object[] params = fieldMap.values().toArray();

        return executeUpdate(sql, params) == 1;
    }

    /**
     * 更新实体
     */
    public static <T> boolean updateEntity(Class<T> entityClass, long id, Map<String, Object> fieldMap) {

        if (CollectionUtil.isEmpty(fieldMap)) {
            LOGGER.error("can not update entity: fieldMap is empty");
            return false;
        }

        StringBuilder sqlBuilder = new StringBuilder("UPDATE ");
        sqlBuilder.append(getTableName(entityClass)).append(" SET ");
        // UPDATE customer set name=?,contact=?,telephone=?,email=?,email=?
        // where id=?
        for (String fieldName : fieldMap.keySet()) {
            sqlBuilder.append(fieldName).append("=?, ");
        }
        sqlBuilder.replace(sqlBuilder.lastIndexOf(","), sqlBuilder.length(), "");
        sqlBuilder.append(" WHERE id = ?");

        List<Object> paramList = new ArrayList<>();
        paramList.addAll(fieldMap.values());
        paramList.add(id);

        Object[] params = paramList.toArray();

        return executeUpdate(sqlBuilder.toString(), params) == 1;
    }

    public static <T> boolean deleteEntity(Class<T> entityClass, long id) {
        String sql = "DELETE FROM  " + getTableName(entityClass) + " WHERE id=?";
        return executeUpdate(sql, id) == 1;
    }

    private static <T> String getTableName(Class<T> entityClass) {
        return entityClass.getSimpleName();
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() {
        Connection connection = CONNECTION_HOLDER.get();

        if (connection == null) {
            try {
                // 首次调用会触发 mysql驱动注册的动作
                // connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
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
     * 关闭数据库连接
     */
    @Deprecated
    public static void closeConnection(Connection connection) {

        if (null != connection) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.error("close jdbc connection error", e);
            }
        }
    }

    @Deprecated
    public static void closeConnection() {

        Connection connection = CONNECTION_HOLDER.get();
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.error("close jdbc connection error", e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_HOLDER.remove();
            }
        }
    }

    public static void executeSqlFile(String filePath) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        String sql;

        try (InputStream in = classLoader.getResourceAsStream(filePath); BufferedReader reader = new BufferedReader(
            new InputStreamReader(in))) {
            while ((sql = reader.readLine()) != null) {
                executeUpdate(sql);
            }
        } catch (IOException e) {
            LOGGER.error("execute sql file failure", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 开启事务
     */
    public static void beginTransaction() {
        Connection connection = getConnection();
        if (connection != null) {
            try {
                connection.setAutoCommit(false);

            } catch (SQLException e) {
                LOGGER.error("begin transaction failure", e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_HOLDER.set(connection);
            }
        }

    }

    /**
     * 提交事务
     */
    public static void commitTransaction() {
        Connection connection = getConnection();
        if (connection != null) {
            try {
                connection.commit();
                connection.close();
            } catch (SQLException e) {
                LOGGER.error("commit transaction failure", e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_HOLDER.remove();
            }
        }

    }

    /**
     * 回滚事务
     */
    public static void rollBackTransaction() {
        Connection connection = getConnection();
        if (connection != null) {
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException e) {
                LOGGER.error("rollback transaction failure", e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_HOLDER.remove();
            }
        }

    }

}
