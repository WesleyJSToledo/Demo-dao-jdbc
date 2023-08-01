package model.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.exception.DB;
import db.exception.DBException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	private static String SQL_FIND_BY_ID = "SELECT s.*, d.Name as DepName " + "FROM seller s "
			+ "INNER JOIN department d ON s.DepartmentId = d.Id " + "WHERE s.Id = ?";
	private static String SQL_FIND_ALL = "SELECT seller.*,department.Name as DepName "
			+ "FROM seller INNER JOIN department " + "ON seller.DepartmentId = department.Id " + "ORDER BY Name";
	private static String SQL_FIND_BY_DEPARTMENT = "SELECT seller.*,department.Name as DepName "
			+ "FROM seller INNER JOIN department " + "ON seller.DepartmentId = department.Id "
			+ "WHERE DepartmentId = ? " + "ORDER BY Name";
	private static String SQL_INSERT_SELLER = "INSERT INTO seller (Name, Email, BirthDate, BaseSalary, DepartmentId) "
			+ "VALUES (?, ?, ?, ?, ?)";
	private static String SQL_UPDATE_SELLER = "UPDATE seller "
			+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? " + "WHERE Id = ?";
	private static String SQL_DELETE_SELLER = "DELETE FROM seller " + "WHERE Id = ?";

	private Connection connection;

	public SellerDaoJDBC(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void insert(Seller sel) {
		PreparedStatement ps = null;

		try {
			ps = connection.prepareStatement(SQL_INSERT_SELLER, Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, sel.getName());
			ps.setString(2, sel.getEmail());
			ps.setDate(3, new java.sql.Date(sel.getBirthDate().getTime()));
			ps.setDouble(4, sel.getBaseSalary());
			ps.setInt(5, sel.getDepartment().getId());

			int rowsAffected = ps.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {
					sel.setId(rs.getInt(1));
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
	public void update(Seller sel) {
		PreparedStatement ps = null;

		try {
			ps = connection.prepareStatement(SQL_UPDATE_SELLER);

			ps.setString(1, sel.getName());
			ps.setString(2, sel.getEmail());
			ps.setDate(3, new java.sql.Date(sel.getBirthDate().getTime()));
			ps.setDouble(4, sel.getBaseSalary());
			ps.setInt(5, sel.getDepartment().getId());
			ps.setInt(6, sel.getId());

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
			ps = connection.prepareStatement(SQL_DELETE_SELLER);

			ps.setInt(1, id);

			int rowsAffected = ps.executeUpdate();
			
			if(rowsAffected == 0) {
				throw new DBException("Id does not exist");
			}

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}


	}

	@Override
	public Seller findById(Integer id) {

		PreparedStatement pst = null;
		ResultSet rs = null;

		Department dep = null;
		Seller sel = null;

		try {
			pst = connection.prepareStatement(SQL_FIND_BY_ID);
			pst.setInt(1, id);

			rs = pst.executeQuery();

			if (rs.next()) {
				dep = instanciateDepartment(rs);
				sel = instanciateSeller(rs, dep);
			}

			return sel;
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(pst);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement pst = null;
		ResultSet rs = null;

		Department dep = null;
		Seller sel = null;

		List<Seller> listSeller = new ArrayList<>();
		Map<Integer, Department> mapDepartment = new HashMap<>();

		try {
			pst = connection.prepareStatement(SQL_FIND_ALL);

			rs = pst.executeQuery();

			while (rs.next()) {
				dep = mapDepartment.get(rs.getInt("DepartmentId"));

				if (dep == null) {
					dep = instanciateDepartment(rs);
					mapDepartment.put(dep.getId(), dep);
				}

				sel = instanciateSeller(rs, dep);
				listSeller.add(sel);
			}

			return listSeller;
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(pst);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {

		PreparedStatement pst = null;
		ResultSet rs = null;

		Department dep = null;
		Seller sel = null;

		List<Seller> listSeller = new ArrayList<>();
		Map<Integer, Department> mapDepartment = new HashMap<>();

		try {
			pst = connection.prepareStatement(SQL_FIND_BY_DEPARTMENT);
			pst.setInt(1, department.getId());

			rs = pst.executeQuery();

			while (rs.next()) {
				dep = mapDepartment.get(rs.getInt("DepartmentId"));

				if (dep == null) {
					dep = instanciateDepartment(rs);
					mapDepartment.put(dep.getId(), dep);
				}

				sel = instanciateSeller(rs, dep);
				listSeller.add(sel);
			}

			return listSeller;
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(pst);
			DB.closeResultSet(rs);
		}
	}

	private Seller instanciateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller sel = new Seller();
		sel.setId(rs.getInt("Id"));
		sel.setName(rs.getString("Name"));
		sel.setEmail(rs.getString("Email"));
		sel.setBirthDate(rs.getDate("BirthDate"));
		sel.setBaseSalary(rs.getDouble("BaseSalary"));
		sel.setDepartment(dep);
		return sel;
	}

	private Department instanciateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

}
