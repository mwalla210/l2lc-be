package com.line2linecoatings.api.dao;

import com.line2linecoatings.api.tracking.models.Employee;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;

public class TrackingDAOImpl {
    public static final Log log = LogFactory.getLog(TrackingDAOImpl.class);

    public Employee createEmployee(Employee employee) throws Exception {
        Connection conn = createConnection();

        String query = "INSERT INTO employee (first_name, last_name) VALUES (?, ?)";

        PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, employee.getFirstName());
        preparedStatement.setString(2, employee.getLastName());

        preparedStatement.executeUpdate();
        int affectedRows = preparedStatement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating user failed, no rows affected.");
        }

        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                employee.setId(generatedKeys.getInt(1));
                log.info("Employee Created with id " + employee.getId());

            }
            else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
        preparedStatement.close();
        conn.close();
        return employee;
    }

    public Employee getEmployeeById(int id) throws Exception {
        Employee employee = null;

        Connection conn = createConnection();

        String query = String.format("SELECT * FROM Employee WHERE id='%d'", id);

        Statement st = conn.createStatement();

        ResultSet rs = st.executeQuery(query);
        employee = mapEmployeeFromResultSet(rs);

        st.close();
        conn.close();
        return employee;
    }

    private Employee mapEmployeeFromResultSet(ResultSet rs) throws Exception {
        Employee employee = null;
        if (rs.next()) {
            employee = new Employee();
            employee.setId(rs.getInt("id"));
            employee.setFirstName(rs.getString("first_name"));
            employee.setLastName(rs.getString("last_name"));
        }

        return employee;
    }
    private Connection createConnection() throws Exception{
        Connection conn = null;
        Context context = new InitialContext();
        DataSource ds = (DataSource) context.lookup("java:comp/env/jdbc/sqlite");
        conn = ds.getConnection();
        return conn;
    }
}
