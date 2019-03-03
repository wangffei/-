package com.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import com.config.Config;
import com.views.Window;

public class VisitorsDo {
	public static void visitors(){
		Object[] proxyKeys = Config.proxyList.keySet().toArray() ;
		Object[] articleKeys = Config.blogList.keySet().toArray() ;
		
		for (int i = 0; i < proxyKeys.length; i++) {
			for(int j = 0 ; j < articleKeys.length ; j++){
				int row = Integer.parseInt(Config.proxyList.get(proxyKeys[i]).get("row")) ;
				Config.pool.execute(new MyThread((String)proxyKeys[i], Config.proxyList.get(proxyKeys[i]).get("port") , (String)articleKeys[j] , row ));
				if(!Config.cadStatus){
					Window.getInstance().model.setValueAt("׼������...", row-1, 2);
				}
			}
		}
	}
	static class MyThread implements Runnable{
		String key = null ;
		String article = null;
		String port = null;
		int row = 0 ;
		public MyThread(String key , String port , String article , int row) {
			this.key = key ;
			this.port = port ;
			this.row = row ;
			this.article = article ;
		}
		@Override
		public void run() {
			if(!Config.cadStatus){
				Window.getInstance().model.setValueAt("������...", row-1, 2);
			}
			//�����ȡ��io����
			BufferedReader reader = null ;
			try {
				URL url = new URL(article);
				//��������������
				InetSocketAddress addr = new InetSocketAddress(key, Integer.parseInt(port)) ;
				//�����������
				Proxy proxy = new Proxy(Proxy.Type.HTTP, addr) ;
				//��������
				HttpURLConnection con = (HttpURLConnection)url.openConnection(proxy) ;
				con.setRequestMethod("GET");
				con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.62 Safari/537.36");
				con.setConnectTimeout(1000);
				
				int statusCode = con.getResponseCode() ;
				if(statusCode != 200){
					if(!Config.cadStatus){
						Window.getInstance().model.setValueAt("faild", row-1, 2);
					}
					Config.proxyList.get(key).put("status", "faild");
					Config.activeAdd();
					return;
				}
				
				reader = new BufferedReader(new InputStreamReader(con.getInputStream() , "utf-8"));
				String line = "" ;
				while((line = reader.readLine()) != null){
				//	System.out.println(line);
				}
			} catch (Exception e) {	
				Config.proxyList.get(key).put("status", "faild");
				if(!Config.cadStatus){
					Window.getInstance().model.setValueAt("faild", row-1, 2);
				}
				Config.activeAdd();
				return;
			} finally{
				if(reader != null){
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			if(!Config.cadStatus){
				Window.getInstance().model.setValueAt("success", row-1, 2);
			}
			Config.proxyList.get(key).put("status", "success");
			Config.blogAdd(article);
			Window.getInstance().addCount();
			//���ô���д�뻺��
			Config.cache.add(key+":"+port) ;
			Config.activeAdd();
		}		
	}
}
