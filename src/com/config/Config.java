package com.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Config {
	//�����б����
	public static final String[] proxyList_header = new String[]{"����ip","�˿�","״̬"} ;
	//�����б����
	public static final String[] blogList_header = new String[]{"���","����","����"};
	//Ĭ����ʾ�����б�
	public static boolean cadStatus = false ;
	//���沩���б�
	public static Map<String , Map<String , String>> blogList = new HashMap();
	//���������Ϣ
	public static Map<String , Map<String , String>> proxyList = new HashMap();
	//����߳���
	public static Integer threadCount = 80 ;
	//��ǰ����ȡ����ҳ��
	public static Integer page = 0 ;
	//ÿ����ȡ����ҳ��
	public static Integer size = 10 ;
	//�Ƿ���Կ�ʼˢ�ÿ���
	public static boolean canStart = false ;
	//ÿ����ȴ�ʱ��
	public static Integer maxWaitTime = 30000 ;
	//�������ҳ���ʱ��
	public static Integer maxSplitTime = 1800 ;
	//���������վ����Ҫ�Ĵ���
	public static List<Map<String, String>> proxy_web = new ArrayList<>() ;
	//���ô�����
	public static Set<String> cache = new HashSet<>();
	//��ǰ��Ծ�߳���
	public static Integer activeThread = 0 ;
	//û����ý��е��û���ˢ������Ĵ����δ�����Ч�ʣ�
	public static int spaceCount = 4 ;
	//��ȡ�����ļ�
	static{
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("./config.conf"));
			String line = "";
			int i = 1 ;
			while((line = reader.readLine()) != null){
				line = line.trim();
				if(!line.equals("") && !line.startsWith("#")){
					if(line.startsWith("blog")){
						String[] strs = line.split("==>")[1].trim().split("=");
						Map<String, String> map = new HashMap() ;
						map.put("name" , strs[0].trim()) ;
						map.put("count" , 0+"") ;
						blogList.put(strs[1].trim(), map) ;
					}else if(line.startsWith("threadCount")){
						String[] strs = line.split("=");
						threadCount = Integer.parseInt(strs[1].trim());
					}else if(line.startsWith("proxy")){
						String[] strs = line.split("==>")[1].trim().split(":");
						Map<String, String> map = new HashMap() ;
						map.put("ip" , strs[0].trim()) ;
						map.put("port" , 0+"") ;
						proxy_web.add(map) ;
					}else if(line.startsWith("maxWaitTime")){
						String[] strs = line.split("=") ;
						maxWaitTime = Integer.parseInt(strs[1].trim()) ;
					}else if(line.startsWith("maxSplitTime")){
						String[] strs = line.split("=") ;
						maxSplitTime = Integer.parseInt(strs[1].trim()) ;
					}else if(line.startsWith("size")){
						String[] strs = line.split("=");
						size = Integer.parseInt(strs[1].trim()) ;
					}else if(line.startsWith("spaceCount")){
						String[] strs = line.split("=");
						spaceCount = Integer.parseInt(strs[1].trim()) ;
					}
				}
			}
		} catch (Exception e) {
			System.exit(-1);
		} finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	static{
		BufferedReader reader = null ;
		try {
			reader = new BufferedReader(new FileReader("./page.mem")) ;
			String line = "" ;
			while((line = reader.readLine()) != null){
				line = line.trim() ;
				if(line.startsWith("page")){
					String[] strs = line.split("=");
					page = Integer.parseInt(strs[1].trim()) ;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	//�̳߳س�ʼ��
	public static ExecutorService pool =  Executors.newFixedThreadPool(threadCount);
	//page����
	public synchronized static void pageAdd(){
		page ++ ;
	}
	//��Ծ�߳�����
	public synchronized static void activeAdd(){
		activeThread++ ;
	}
	//���ͷ���������
	public synchronized static void blogAdd(String key){
		int count = Integer.parseInt(blogList.get(key).get("count")) ;
		blogList.get(key).put("count", (++count)+"") ;
	}
	public synchronized static void putProxy(String key , Map<String , String> value){
		proxyList.put(key, value) ;
	} 
}
