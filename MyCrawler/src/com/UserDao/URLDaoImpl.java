package com.UserDao;

import org.apache.ibatis.session.SqlSession;

import com.model.urldata;

public class URLDaoImpl implements URLDao{
	
	SqlSession session;
	
	public URLDaoImpl(SqlSession s){
		this.session = s;
	}
	
	 public int inserturl(urldata ud){
		int i = session.insert("insertURL", ud);
		session.commit();
		return i;
	}
	 
}
