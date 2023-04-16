package tw.badminton.eeit58;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/ActivityEnroll")
public class ActivityEnroll extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	System.out.println("success");
    }
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		ServletContext sc=getServletConfig().getServletContext();
		Connection conn=(Connection)sc.getAttribute("conn"); 
		
		String userId = (String)request.getSession().getAttribute("userId");//從Session中存放用戶id的屬性得到目前登入用戶的UserId
//		userId = "5";//測試用
		userId = request.getParameter("userId");//測試用
		System.out.println("userId為: " +userId );
		
		//要得到使用者點擊的那個活動的活動id
//		activityId = "6";//測試用
		String activityId = "5";//測試用
		System.out.println("activityId為: " + activityId);
		
		//取出 activity 資料表中指定 活動編號(`activity`.`id`) 的 參與人(participataion)欄位
				String select_sql1= "SELECT `participataion` FROM `activity` WHERE `id` = " + activityId + ";"; 
				try {
					PreparedStatement pstmt_select = conn.prepareStatement(select_sql1);
					
					ResultSet rs = pstmt_select.executeQuery();
					
					while(rs.next()) {
						String unSortData= rs.getString("participataion");//參與人:1,2,3
						System.out.println("參與人有:");
						System.out.println(unSortData);
					}
					
				} catch (SQLException e) {
					System.out.println(e);
				}
		
		//執行SQL指令 把 activity 資料表中指定 活動編號(`activity`.`id`) 的 參與人(participataion)欄位 更新成 原先 參與人(participataion)欄位 的內容 再加上pstmt.setString(1,放入的字串)
		String update_sql = "UPDATE `activity` SET `participataion` = CONCAT(`participataion`, ?) WHERE `activity`.`id` = " + activityId;
		try {
			PreparedStatement pstmt = conn.prepareStatement(update_sql);
			pstmt.setString(1,"," + userId);
			if(pstmt.executeUpdate()>0) {
				
				System.out.println("sql執行加入活動成功");
				
				String insert_sql = "INSERT INTO `activity_add` (`id`, `memberId`)VALUES (?,?)";
				PreparedStatement pstmtInsert = conn.prepareStatement(insert_sql);
				pstmtInsert.setString(1, activityId);
				pstmtInsert.setString(2, userId);
				if(pstmtInsert.executeUpdate() > 0) {
					System.out.println("sql執行把活動編號和成員編號塞入中間表成功");
				}else {
					System.out.println("sql執行把活動編號和成員編號塞入中間表失敗");
				}
			}else {
				System.out.println("sql執行失敗");
			}
		} catch (SQLException e) {
			System.out.println("出現SQL例外");
			System.out.println(e);
		}
		
		//取出 activity 資料表中指定 活動編號(`activity`.`id`) 的 參與人(participataion)欄位
		String select_sql2= "SELECT `participataion` FROM `activity` WHERE `id` = " + activityId + ";"; 
		try {
			PreparedStatement pstmt_select = conn.prepareStatement(select_sql2);
			
			ResultSet rs = pstmt_select.executeQuery();
			
			while(rs.next()) {
				String unSortData= rs.getString("participataion");//參與人:1,2,3
				System.out.println("參與人有:");
				System.out.println(unSortData);
				String[] numbers = unSortData.split(",");// [1, 2 ,3]
				System.out.println("String[]: " + numbers);
				LinkedList participataionList = new LinkedList(Arrays.asList(numbers));
				System.out.println("participataionList[]: " + participataionList);
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
		
	}

}
