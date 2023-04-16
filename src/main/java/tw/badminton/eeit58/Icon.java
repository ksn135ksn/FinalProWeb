package tw.badminton.eeit58;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig(location = "C:\\MyFile\\eclipse-workspace\\FinalProWeb\\src\\main\\webapp\\memberIcon")
@WebServlet("/Icon")
public class Icon extends HttpServlet {

	public Icon() {
					
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		ServletContext sc=getServletConfig().getServletContext();
		
		Connection conn=(Connection)sc.getAttribute("conn"); 
		
		int id = 0;//暫時先宣告並初始化id 
		//正式時需要從session那getattribute得到member的id
		request.getSession().getAttribute("存放用戶id的那個Attribute");

		String sql = "SELECT * FROM member WHERE account=?";//sql語法 從member資料表透過該用戶的account得到他的id編號
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "eeit58test1@gmail.com");//把 會員的帳戶 塞入
			ResultSet rs = pstmt.executeQuery();//執行SQL查詢
			rs.next();
			id = rs.getInt("id");//查到該用戶的id值
			System.out.println("sql執行成功");
		} catch (SQLException e) {
			System.out.println("出現SQL例外");
		}
		Part photo = request.getPart("photo");//member.html的第40行 input新增name屬性 的屬性值為("photo)
		String filename = String.format("%s.jpg",id);//把該帳號的id值當作檔名
		
		long fileSize = photo.getSize();//得到上傳檔案的大小
		if (fileSize < 2 * 1024 * 1024) {//如果檔案小於2MB
			photo.write(filename);//Part 的write()將上傳檔案以指定的檔名寫入
		}else {
			//PrintWriter out = response.getWriter();
			//out.print(" <script>alert('image can`t exceed 2MB')</script>;");
		}		
	}
}