package com.util;

import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class DBUtil {
	
	private static SqlSession session;
	
	public static SqlSession getsession(){
		
		try{
			if(session == null){
				String resource = "mybatis_cfg.xml";
				Reader read = Resources.getResourceAsReader(resource);
				Properties props = new Properties();
				props.load(Resources.getResourceAsReader("jdbc.properties"));
				SqlSessionFactory sessionfactory = new SqlSessionFactoryBuilder().build(read, props);
				session = sessionfactory.openSession();
			}
			return session;
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void closesession(){
		if(session != null){
			session.close();
			session = null;
		}
	}
}
