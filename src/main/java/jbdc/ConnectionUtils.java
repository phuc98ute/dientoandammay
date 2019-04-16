package jbdc;

import java.sql.Connection;


import com.mysql.jdbc.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


	public class ConnectionUtils {
		 
		  public static Connection getMyConnection() throws SQLException,
		          ClassNotFoundException {
		      // Sử dụng Oracle.
		      // Bạn có thể thay thế bởi Database nào đó.
		      return MySQLConnUtils.getMySQLConnection();
		  }
		 
		  //
		  // Test Connection ...
		  //
		 /* public static void main(String[] args) throws SQLException,
		          ClassNotFoundException {
			  Article article =new Article();
			  PreparedStatement pr;
			  article.setArticleId(3);
			  article.setCategory("jhkjdasd");
			  article.setTitle("sshsadsa");
			  article.setConttent("jhdjsakjdhkjashdkjahskjhaksjS");
		      System.out.println("Get connection ... ");
		 
		      // Lấy ra đối tượng Connection kết nối vào database.
		      Connection connection = ConnectionUtils.getMyConnection();
		 
		      System.out.println("Get connection " + connection);
		 
		      System.out.println("Done!");
		      Statement statement = connection.createStatement();
		      pr=(PreparedStatement) connection.prepareStatement("insert into articles (article_id, title, category, conttent) values (5,'dsad','Hai','djdskahda')");
		     pr.executeUpdate();
;
		      String sql = "Select * from articles";
		 
		      // Thực thi câu lệnh SQL trả về đối tượng ResultSet.

		      ResultSet rs = statement.executeQuery(sql);
		 
		 
		      // Duyệt trên kết quả trả về.
		      while (rs.next()) {// Di chuyển con trỏ xuống bản ghi kế tiếp.
		          int id = rs.getInt("article_id");
		          
		          
		          System.out.println("--------------------");
		          System.out.println("EmpId:" + id);
		    
		      }
		      // Đóng kết nối
		      connection.close();
			
		  }*/
		 
		}


