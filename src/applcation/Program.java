package applcation;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class Program {
	static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {

		System.out.println("============= TESTE SELLER ===============");

		
		SellerDao sellerDao = DaoFactory.createSellerDao();

		
		System.out.println("=== TEST 1: seller findById =====");
		Seller seller = sellerDao.findById(3);
		System.out.println(seller);
		System.out.println();

		
		System.out.println("=== TEST 2: seller findByDepartment =====");
		Department department = new Department(2, null);
		List<Seller> listSeller = sellerDao.findByDepartment(department);
		listSeller.forEach(System.out::println);
		System.out.println();
		

		System.out.println("=== TEST 3: seller findAll =====");
		listSeller = sellerDao.findAll();
		listSeller.forEach(System.out::println);
		System.out.println();

		System
		.out.println("=== TEST 4: seller insert =====");
		Seller newSeller = new Seller(null, "Greg", "greg@gmail.com", new Date(), 4000.0, department);
		sellerDao.insert(newSeller);
		System.out.println("Inserted! \n" + newSeller.toString());
		System.out.println();

		
		System.out.println("=== TEST 5: seller update =====");
		seller = sellerDao.findById(newSeller.getId());
		seller.setName("Martha Waine");
		sellerDao.update(seller);
		System.out.println("Update completed \n" + seller.toString());
		System.out.println();

		
		System.out.println("=== TEST 6: seller delete =====");
		System.out.println("Delete " + newSeller.toString() + "\nPRESS ENTER");
		sc.nextLine();
		sellerDao.deleteById(newSeller.getId());
		System.out.println("Delete completed");

		
		
		System.out.println("============= TESTE DEPARTMENT ===============");
		
		
		DepartmentDao departmentDao = DaoFactory.createDepartmentDao();
		
		
		System.out.println("=== TEST 1: findById =======");
		Department dep = departmentDao.findById(1);
		System.out.println(dep);
		System.out.println();
		
		
		System.out.println("=== TEST 2: findAll =======");
		List<Department> listDepartment = departmentDao.findAll();
		listDepartment.forEach(System.out::println);
		System.out.println();

		
		System.out.println("=== TEST 3: insert =======");
		Department newDepartment = new Department(null, "Music");
		departmentDao.insert(newDepartment);
		System.out.println("Inserted! " + newDepartment.toString());
		System.out.println();

		
		System.out.println("=== TEST 4: update =======");
		dep = departmentDao.findById(newDepartment.getId());
		dep.setName("Food");
		departmentDao.update(dep);
		System.out.println("Update co \n" + dep.toString());
		System.out.println();
		
		
		System.out.println("\n=== TEST 5: delete =======");
		System.out.print("Delete " + newDepartment.toString() + "\nPRESS ENTER");
		sc.nextLine();
		departmentDao.deleteById(newDepartment.getId());
		System.out.println("Delete completed");

		
		
		sc.close();

	}
}
