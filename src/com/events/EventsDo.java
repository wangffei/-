package com.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import com.config.Config;
import com.data.ProxyWrom;
import com.data.VisitorsDo;
import com.views.Window;

public class EventsDo {
	public Window window = null;
	static int cacheCount = 0 ;
	static int flag = 0 ;
	
	public EventsDo() {
		window = Window.getInstance();
		init();
	}
	
	public void init(){
		//������������״��
		window.changeNetStatus("�����...");
		new Thread(){
			public void run(){
				boolean status = false ;
				BufferedReader br = null;
				try{
					Process process = Runtime.getRuntime().exec("ping -n 3 -w 2000 14.215.177.38");
					br = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String str = "";
					while((str = br.readLine()) != null){
						if(str.contains("����") && str.contains("TTL")){
							status = true ;
							break;
						}
					}
				}catch(Exception e){}finally{
					if(br != null){
						try {
							br.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				if(status){
					window.changeNetStatus("��");
				}else{
					window.changeNetStatus("��");
				}
			}
		}.start();
		//Ĭ�ϲ�������
		if(Config.cadStatus){
			window.button.setEnabled(true);
			window.button_1.setEnabled(false);
			window.addColumn(Config.blogList_header);
			addBlog();
		}else{
			window.button.setEnabled(false);
			window.button_1.setEnabled(true);
			window.addColumn(Config.proxyList_header);
		}
		//�رճ����¼�
		window.frmTool.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				FileWriter writer = null ;
				FileWriter writer2 = null ;
				try {
					writer = new FileWriter("./cache.txt") ;
					writer2 = new FileWriter("./page.mem") ;
					for(String line : Config.cache){
						writer.write(line+"\r\n");
					}
					writer2.write("page="+Config.page);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally{
					if(writer != null){
						try {
							writer.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					if(writer2 != null){
						try {
							writer2.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				System.exit(0);
			}
		});
		//��ť�����¼�
		ActionListener action = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == window.beginBtn){
					begin();	
				}else if(e.getSource() == window.button){
					window.button.setEnabled(false);
					window.button_1.setEnabled(true);
					proxyShow();
					Config.cadStatus = false ;
				}else if(e.getSource() == window.button_1){
					window.button.setEnabled(true);
					window.button_1.setEnabled(false);
					blogShow();
					addBlog() ;
					Config.cadStatus = true ;
				}
			}
		};
		window.beginBtn.addActionListener(action) ;
		window.button.addActionListener(action);
		window.button_1.addActionListener(action);
	}
	
	//�����ʼ��ť�¼�
	public void begin(){
		String str = window.beginBtn.getText();
		if(str.equals("��ʼ")){
			if(!Config.canStart){
				JOptionPane.showMessageDialog(null, "�����������δ׼���ã������ĵȴ�������");
				return ;
			}
			Config.canStart = false ;
			window.changeBeginStatus("��");
			window.changeBegin2Stop();
			window.beginBtn.setEnabled(false);
			Config.pool = Executors.newFixedThreadPool(Config.threadCount) ;
			VisitorsDo.visitors();
			new Thread(){
				public void run(){
					try {
						while(true){
							Thread.sleep(Config.maxWaitTime);
							Config.proxyList.clear();
							Config.pool = Executors.newFixedThreadPool(Config.threadCount) ;
							ProxyWrom.getProxy(Config.page, Config.size);
							Config.pool.shutdown();
							while(true){
								try {
									Thread.sleep(500);
									if(Config.pool.isTerminated()){
										break ;
									}
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							System.out.println(Config.spaceCount+" "+flag);
							if(flag % Config.spaceCount == 0 && flag != 0){
								System.out.println("laile");
								window.addColumn(Config.proxyList_header);
								Config.pool = Executors.newFixedThreadPool(Config.threadCount) ;
								Config.proxyList.clear();
								for(String string : Config.cache){
									String ip = string.split(":")[0].trim() ;
									String port = string.split(":")[1].trim() ;
									//���������д��proxyList����
									Map<String, String> map = new HashMap<String, String>() ;
									map.put("ip", ip) ;
									map.put("port", port) ;
									map.put("status", "δ֪") ;
									int row = Window.getInstance().addRow(new String[]{ip , port , "δ֪"}) ;
									map.put("row", row+"") ;
									Config.proxyList.put(ip, map) ;
								}
								VisitorsDo.visitors();
							}else{
								Config.pool = Executors.newFixedThreadPool(Config.threadCount) ;
								VisitorsDo.visitors();
							}
							flag++ ;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}else{
			window.changeBeginStatus("��");
			window.changeStop2Begin();
		}
	}
	//��������б�ť�¼�
	public void proxyShow(){
		Window.getInstance().addColumn(Config.proxyList_header);
		Object[] keys = Config.proxyList.keySet().toArray() ;
		for (Object object : keys) {
			Integer row = window.addRow(new String[]{Config.proxyList.get(object).get("ip") , Config.proxyList.get(object).get("port") , Config.proxyList.get(object).get("status")});
			Config.proxyList.get(object).put("row", ""+row) ;
		}
	}
	//��������б�ť�¼�
	public void blogShow(){
		window.addColumn(Config.blogList_header);
	}
	//�������б����б�
	public void addBlog(){
		Object[] keys = Config.blogList.keySet().toArray() ;
		for(int i=0 ; i<keys.length ; i++){
			Integer row = window.addRow(new String[]{String.valueOf(i+1) , Config.blogList.get(keys[i]).get("name") , Config.blogList.get(keys[i]).get("count")});
			Config.blogList.get(keys[i]).put("row", row+"") ;
		}
	}	
	public static void main(String[] args) {
		new EventsDo();
		//����ʹ�û���
		BufferedReader reader = null ;
		try {
			reader = new BufferedReader(new FileReader("./cache.txt")) ;
			StringBuffer buf = new StringBuffer() ;
			String line = "";
			while((line = reader.readLine()) != null){
				buf.append(line+"\r\n") ;
			}
			String data = buf.toString().trim() ;
			if(data != null && !data.equals("")){
				String[] strs = data.split("\r\n") ;
				for (String string : strs) {
					if(!string.trim().equals("")){
						cacheCount++ ;
						Config.cache.add(string) ;
						String ip = string.split(":")[0].trim() ;
						String port = string.split(":")[1].trim() ;
						//���������д��proxyList����
						Map<String, String> map = new HashMap<String, String>() ;
						map.put("ip", ip) ;
						map.put("port", port) ;
						map.put("status", "δ֪") ;
						int row = Window.getInstance().addRow(new String[]{ip , port , "δ֪"}) ;
						map.put("row", row+"") ;
						Config.proxyList.put(ip, map) ;
					}
				}
				if(strs.length != 0){
					Config.canStart = true ;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		new Thread(){
			public void run(){
				if(Config.canStart){
					return ;
				}
				ProxyWrom.getProxy(Config.page, Config.size);
				Config.pool.shutdown();
				while(true){
					try {
						if(Config.pool.isTerminated()){
							Config.canStart = true ;
							break ;
						}
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				JOptionPane.showMessageDialog(null, "���������׼����ɡ������Կ�ʼ");
			}
		}.start();
	}
}
