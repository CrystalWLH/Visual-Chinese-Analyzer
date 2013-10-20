package cn.edu.pku.eecs.vca.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import cn.edu.pku.eecs.vca.applet.VisualChineseAnalyzer;
import cn.edu.pku.eecs.vca.core.Forest;

public class Library
{
	public static boolean delete(int nId)				//删除记录
	{
		try
		{
			String szUrl = VisualChineseAnalyzer.SERVLET_PREFIX + "dbDelete";
			
			URL url = new URL(szUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.connect();
			
			OutputStream out = connection.getOutputStream();
			DataOutputStream dout = new DataOutputStream(out);
			dout.writeInt(nId);
			dout.flush();
			dout.close();
			out.close();
			
			InputStream in = connection.getInputStream();
			DataInputStream din = new DataInputStream(in);
			
			boolean ret = din.readBoolean();
			din.close();
			in.close();
			
			return ret;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Vector<LibraryItem> queryAll()			//查询所有记录
	{
		try
		{
			String szUrl = VisualChineseAnalyzer.SERVLET_PREFIX + "dbQueryAll";
			
			URL url = new URL(szUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			connection.setRequestMethod("POST");
			connection.connect();
			
			InputStream in = connection.getInputStream();
			ObjectInputStream oin = new ObjectInputStream(in);
			
			Vector<LibraryItem> ret =  (Vector<LibraryItem>) oin.readObject();
			oin.close();
			in.close();
			
			return ret;
		}
		catch(Exception e) 
		{
			return null;
		}
	}
	
	public static Forest queryById(int nId)				//根据ID查询记录，返回文档（森林）
	{
		try
		{
			String szUrl = VisualChineseAnalyzer.SERVLET_PREFIX + "dbQueryById";
			
			URL url = new URL(szUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.connect();
			
			OutputStream out = connection.getOutputStream();
			DataOutputStream dout = new DataOutputStream(out);
			dout.writeInt(nId);
			dout.flush();
			dout.close();
			out.close();
			
			InputStream in = connection.getInputStream();
			ObjectInputStream oin = new ObjectInputStream(in);
			
			Forest ret =  (Forest) oin.readObject();
			oin.close();
			in.close();
			
			return ret;
		}
		catch(Exception e) 
		{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Vector<LibraryItem> queryByText(String szText)
	{
		try
		{
			String szUrl = VisualChineseAnalyzer.SERVLET_PREFIX + "dbQueryByText";
			
			URL url = new URL(szUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.connect();
			
			OutputStream out = connection.getOutputStream();
			ObjectOutputStream oout = new ObjectOutputStream(out);
			oout.writeObject(szText);
			oout.flush();
			oout.close();
			out.close();
			
			InputStream in = connection.getInputStream();
			ObjectInputStream oin = new ObjectInputStream(in);
			
			Vector<LibraryItem> ret =  (Vector<LibraryItem>) oin.readObject();
			oin.close();
			in.close();
			
			return ret;
		}
		catch(Exception e) 
		{
			return null;
		}
	}
	
	public static int save(int nId, Forest forest)		//保存，返回记录的ID
	{
		try
		{
			String szUrl = VisualChineseAnalyzer.SERVLET_PREFIX + "dbSave";
			
			URL url = new URL(szUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.connect();
			
			OutputStream out = connection.getOutputStream();
			ObjectOutputStream dout = new ObjectOutputStream(out);
			dout.writeInt(nId);
			dout.writeObject(forest);
			dout.flush();
			dout.close();
			out.close();
			
			InputStream in = connection.getInputStream();
			DataInputStream din = new DataInputStream(in);
			
			int ret = din.readInt();
			din.close();
			in.close();
			
			return ret;
			
		}
		catch(Exception e)
		{
			return LibraryItem.NEW;
		}
	}
}