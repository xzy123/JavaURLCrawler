package com.util;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.session.SqlSession;

import com.UserDao.URLDao;
import com.UserDao.URLDaoImpl;
import com.model.urldata;

public class crawler {
	
	ArrayList<String> urls  = new ArrayList<String>();
	HashSet<String> visited  = new HashSet<String>();
	ArrayList<String> queue  ;//= new ArrayList<String>();
	int deep = 0;
	HashMap<String,Integer> hm = new HashMap<String,Integer>(); 
	SqlSession session;
	URLDao urldao;
//	urldata ud;
	
	public crawler(){
		session = DBUtil.getsession();
		urldao = new URLDaoImpl(session);
//		ud = new urldata();
	}
	
	public  void setHomePage(String url){
		urls.add(url);
		hm.put(url, 1);
		
		urldata ud = new urldata();
		ud.setDeep(1);
		ud.setUrl(url);
		urldao.inserturl(ud);
		
		for(int i=0;i<3;i++){
			new Thread(new process(this)).start();
		}
		//URLDao
		
		
	}
	
	public synchronized String getURL(){
		if(urls.isEmpty())
			return "";
		String str = urls.get(0);
		urls.remove(0);
//		System.out.println(urls.size());
		return str;
	}
	
	public void saveURL(HashMap<String,Integer> hm){
		
	}
	
	public boolean getWebURL(){
		if(urls.isEmpty())
			return false;
		
		String start ;
		while(true){
//			System.out.println("b");
			start = getURL();
			if(start == ""){
//				System.out.println("a");
				return false;
			};
			if(start != "" && !visited.contains(start)){
//				System.out.println("b");
				break;
			}
		}
		visited.add(start);
		int stdeep = hm.get(start);	
		try {
			
//			System.out.println("a");
			
			URL url = new URL(start);
			URLConnection con = url.openConnection();
			con.setDoOutput(true);
			InputStream in = null;
			in = url.openStream();
			BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
			String line;
			int strlen;
			while ( (line = bReader.readLine()) != null){
//				System.out.println(line);
				strlen = line.length();
				if(strlen > 0){
//					String tempurl = "";
//					tempurl = this.WebToURL(line);
//					if(tempurl != "")
					saveURL(line,stdeep);
				}
			}
			in.close();
//			bReader.close();
		} catch (MalformedURLException e) {
			System.out.println("Get URL Failed.");
		} catch (IOException e) {
			System.out.println("Get Connection Failed.");
		}
		
		return true;
	}
	
	public  void saveURL(String line,int urldep){
		
		String regUrl = "[http|https]+[://]+[0-9A-Za-z:/[-]_#[?][=][.][&]]*";
		Pattern p = Pattern.compile(regUrl, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(line);
		boolean bl = m.find();
		while(bl){
			String tmp = m.group(0);
			int len = tmp.length();
			if(len > 10 
					&& tmp.substring(0,4).equals("http") 
					&& !(tmp.substring(len-3, len).equals(".js") 
					|| tmp.substring(len-4, len).equals(".css"))){
				if(!visited.contains(tmp)){
					System.out.println(tmp);
					urls.add(tmp);
//					visited.add(tmp);
					urldata ud = new urldata();
					ud.setDeep(urldep+1);
					ud.setUrl(tmp);
					urldao.inserturl(ud);
					
					hm.put(tmp, urldep+1);
				}
			}
			line = line.substring(m.end(), line.length());
			m = p.matcher(line);
			bl = m.find();
		}
		
	}
	
	
	
	public static void main(String[] args){
		crawler cra = new crawler();
		cra.setHomePage("http://www.sohu.com/");
		
		/*SqlSession session = DBUtil.getsession();
		URLDao urldao = new URLDaoImpl(session);
		urldata ud = new urldata();
		ud.setDeep(1);
		ud.setUrl("www.sohu.com");
		urldao.inserturl(ud);*/
		
		/*while(!cra.urls.isEmpty()){
			System.out.println(cra.urls.size());
			cra.getWebURL();
		}
		cra.getWebURL();
		System.out.println(cra.urls.size());
		for(int i=0;i<20;i++){
			System.out.println(cra.urls.get(i));
		}*/
	}
	
	class process implements Runnable{
		crawler cr;
		process(crawler cr){
			this.cr = cr;
		}
		public void run() {
			while(!urls.isEmpty()){
//				System.out.println(cr.urls.size());
				cr.getWebURL();
//				System.out.println(cr.visited.size());
			}
		}
		
	}
	
}
