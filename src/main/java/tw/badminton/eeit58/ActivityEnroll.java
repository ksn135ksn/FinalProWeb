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

import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONWriter;

import tw.badminton.api.Member;


@WebServlet("/ActivityEnroll")
public class ActivityEnroll extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Member member;
	private Connection conn;
	private JSONObject json;
	private String SelectParticipataion = "SELECT `participataion` FROM `activity` WHERE `id` = ?;"; 
       
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	System.out.println("doGet");
    	doPost(req, resp);
    }
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("doPost");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		ServletContext sc=getServletConfig().getServletContext();
		Connection conn=(Connection)sc.getAttribute("conn"); 
		
//		Member memberData = (Member)request.getSession().getAttribute("member"); //從Session中取得登入的會員資料物件 他在tw.badminton.api
//		String userId = memberData.getID();//從Session會員資料物件得到他的id編號
		
		JSONStringer js = new JSONStringer();
		JSONWriter jw = js.array();
		
		String userId = request.getParameter("userId");//從http那傳來的 {userId: null}的null
		
		System.out.println("userId為: " + userId);
		if(userId == null) {//如果傳進來的值是null
			System.out.println(userId);

			response.setContentType("application/json");
			json = new JSONObject();
			json.put("test", "1");//member.getID()
			
			response.getWriter().print(json);//送到前端

			return;
		}
		//要得到使用者點擊的那個活動的活動id
		String activityId = "5";//測試用
		
//		String activityId = request.getParameter("id");
		System.out.println("activityId為: " + activityId);
		
		//取出 activity 資料表中指定 活動編號(`activity`.`id`) 的 參與人(participataion)欄位
		//private String SelectParticipataion = "SELECT `participataion` FROM `activity` WHERE `id` = ?;";
		try {
			PreparedStatement pstmt_select = conn.prepareStatement(SelectParticipataion);
			pstmt_select.setString(1, activityId);//現在這個活動的編號
			ResultSet rs = pstmt_select.executeQuery();
			
			while(rs.next()) {
				String unSortData= rs.getString("participataion");//參與人:1,2,
				System.out.println("參與人有:");
				System.out.println(unSortData);//1,2
				String[] numbers = unSortData.split(",");//以 逗號 來分割 建立字串陣列
				System.out.println("String[]: " + numbers);
				
				LinkedList participataionList = new LinkedList(Arrays.asList(numbers));//[1, 2, 3]
				
				if(participataionList.indexOf(userId) > 0) {//如果在活動參與人欄位 有該帳號的用戶編號時
					System.out.println("該用戶已經報名過");
					response.setContentType("application/json");
					json = new JSONObject();
					json.put("alreadyJoin", "yes");//member.getID()
					
					response.getWriter().print(json);//送到前端

					return;
				}
				System.out.println("participataionList[]: " + participataionList);
			}
		} catch (SQLException e) {
			System.out.println("134行");
			System.out.println(e);
		}
		
		//執行SQL指令 把 activity 資料表中指定 活動編號(`activity`.`id`) 的 參與人(participataion)欄位  把新報名的 加在 參與人(participataion)欄位  的內容最後面 
		String update_sql = "UPDATE `activity` SET `participataion` = CONCAT(`participataion`, ?) WHERE `activity`.`id` = " + activityId;
		try {
			PreparedStatement pstmt = conn.prepareStatement(update_sql);
			pstmt.setString(1,"," + userId);//pstmt.setString(1,現在登入該帳號的id編號)
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
		try {
			PreparedStatement pstmt_select = conn.prepareStatement(SelectParticipataion);
			pstmt_select.setString(1, activityId);//現在這個活動的編號
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
			System.out.println("134行");
			System.out.println(e);
		}

		response.setContentType("application/json");
		json = new JSONObject();
		json.put("redirectToIndex", "yes");//member.getID()
		
		response.getWriter().print(json);//送到前端
	}

}
