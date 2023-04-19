package tw.badminton.eeit58;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Properties;

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


@WebServlet("/PasteToActivityHTML")
public class PasteToActivityHTML extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Member member;
	private Connection conn;
	private JSONObject json;
       
    public PasteToActivityHTML() {
    	try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Properties prop = new Properties();
			prop.put("user", "root");
			prop.put("password", "root");    		
			conn = DriverManager.getConnection("jdbc:mysql://localhost/eeit58group3", prop);
		}catch(Exception e) {
			System.out.println("41行");
			e.printStackTrace();
		}
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("doPost");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		ServletContext sc=getServletConfig().getServletContext();
		Connection conn=(Connection)sc.getAttribute("conn"); 

		String activityId = request.getParameter("activityId");//從http那傳來的 {activityId: 5}的5
		
		System.out.println("目前頁面的activityId為: " + activityId);
		if(activityId == null) {//如果傳進來的值是null
			response.setContentType("application/json");
			json = new JSONObject();
			json.put("cannotFoundActivity", "yes");
			response.getWriter().print(json);//送到前端
			return;
		}
		
		try {
			String queryActivityInformation = "SELECT * FROM `activity` WHERE `id` = ?;";//查詢活動資料表中該活動編號的所有欄位
			PreparedStatement pstmt = conn.prepareStatement(queryActivityInformation);
			pstmt.setString(1, activityId);//現在這個活動的編號
			ResultSet rs = pstmt.executeQuery();
			rs.next(); 
			String activityTitle= rs.getString("activityTitle");//該筆資料"activityTitle"欄位的值
			System.out.println("activityTitle: " + activityTitle);
			String pic= rs.getString("pic");
			System.out.println("pic: " + pic);
			String location= rs.getString("location");
			System.out.println("location: " + location);
			String activityTime= rs.getString("activityTime");
			System.out.println("activityTime: " + activityTime);
			String deadline= rs.getString("deadline");
			System.out.println("deadline: " + deadline);
			String min= rs.getString("min");
			System.out.println("min: " + min);
			String max= rs.getString("max");
			System.out.println("max: " + max);
			String contact= rs.getString("contact");
			System.out.println("contact: " + contact);
			String reservation= rs.getString("reservation");
			System.out.println("reservation: " + reservation);
			String level= rs.getString("level");
			System.out.println("level: " + level);
			String description= rs.getString("description");
			System.out.println("description: " + description);
			
			String participataion= rs.getString("participataion");
			System.out.println("participataion: " + participataion);
			
			String[] numbers = participataion.split(",");//以 逗號 來分割 建立字串陣列
			
			LinkedList participataionList = new LinkedList(Arrays.asList(numbers));//[1, 2]
			
			json = new JSONObject();
			json.put("activityTitle", activityTitle);
			json.put("pic", pic);
			json.put("location", location);
			json.put("activityTime", activityTime);
			json.put("deadline", deadline);
			json.put("min", min);
			json.put("max", max);
			json.put("contact",contact);
			json.put("reservation", reservation);
			json.put("level", level);
			json.put("description", description);
			json.put("participataion",participataion);
			System.out.println(json);
			
			response.setContentType("application/json");
			json.put("successToAccessActivityData", "yes");
			response.getWriter().print(json);//送到前端
			return;
//			if(participataionList.size() == Integer.parseInt(max)) {//如果報名人數到達上限時
//				response.setContentType("application/json");
//				json = new JSONObject();
//				json.put("activityMemberIsMax", "yes");//member.getID()
//				response.getWriter().print(json);//送到前端
//				return;
//			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}



