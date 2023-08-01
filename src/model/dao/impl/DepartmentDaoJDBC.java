package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.exception.DB;
import db.exception.DBException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

	private static String SQL_INSERT_DEPARTMENT = "INSERT INTO department(Name) " + "VALUES (?)";
	private static String SQL_UPDATE_DEPARTMENT = "UPDATE department " + "SET Name = ?" + "WHERE Id = ?";
	private static String SQL_DELETE_DEPARTMENT = "DELETE  FROM department " + "WHERE Id = ?";
	private static String SQL_FIND_BY_DEPARTMENT = "SELECT d.* " + "FROM department d " + "WHERE Id = ?";
	private static String SQL_FIND_ALL = "SELECT * FROM department";

	private Connection connection;

	public DepartmentDaoJDBC(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void insert(Department dep) {
		PreparedStatement ps = null;

		try {
			ps = connection.prepareStatement(SQL_INSERT_DEPARTMENT, Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, dep.getName());

			int rowsAffecteds = ps.executeUpdate();

			if (rowsAffecteds > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {
					dep.setId(rs.getInt(1));
				}
				DB.closeResultSet(rs);
			} else {
				throw new DBException("Unexpected error! No rows addected!");
			}

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}

	}

	@Override
	public void update(Department dep) {
		PreparedStatement ps = null;

		try {
			ps = connection.prepareStatement(SQL_UPDATE_DEPARTMENT);

			ps.setString(1, dep.getName());
			ps.setInt(2, dep.getId());

			ps.executeUpdate();

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}

	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement ps = null;

		try {
			ps = connection.prepareStatement(SQL_DELETE_DEPARTMENT);

			ps.setInt(1, id);

			int rowsAffected = ps.executeUpdate();

			if (rowsAffected == 0) {
				throw new DBException("Id does not exist");
			}

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}
	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Department dep = null;
		
		try {
			ps = connection.prepareStatement(SQL_FIND_BY_DEPARTMENT);
			
			ps.setInt(1, id);
			
			rs = ps.executeQuery();
			
			if(rs.next()) {
				dep = instaciateDepartment(rs);	
			}
			return dep;
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}


	@Override
	public List<Department> findAll() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List <Department> listDepartment = new ArrayList<Department>();
		
		try {
			ps = connection.prepareStatement(SQL_FIND_ALL);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				listDepartment.add(instaciateDepartment(rs));
			}
			
			return listDepartment;
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		}finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}

	
	private Department instaciateDepartment(ResultSet rs) throws SQLException {
		return new Department(rs.getInt("Id"), rs.getString("Name"));
	}
}