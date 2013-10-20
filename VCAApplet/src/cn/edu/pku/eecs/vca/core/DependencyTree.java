package cn.edu.pku.eecs.vca.core;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import cn.edu.pku.eecs.vca.applet.VisualChineseAnalyzer;

public class DependencyTree implements Serializable						//单个依存树
{
	private static final long serialVersionUID = 938962563012531554L;
	
	private Vector<Node> lstNode = new Vector<Node>();					//结点列表
	private Vector<String> lstNewWord = new Vector<String>();			//新单词列表
	
	public DependencyTree()
	{
		lstNode.add(new RootNode());
	}
	
	public boolean add(String szWord)									//添加单词
	{
		lstNewWord.add(szWord);
		
		if (!szWord.equals("。") && !szWord.equals("？") && 
			!szWord.equals("！") && !szWord.equals("；") &&
			!szWord.equals("!") && !szWord.equals("?") && !szWord.equals(";") &&
			!szWord.equals("……") && !(lstNewWord.size() > 1 && 
			 szWord.equals("…") && lstNewWord.get(lstNewWord.size() - 2).equals("…")))
			return false;	
		
		return generate();												//断句符号则生成依存树
	}
	
	@SuppressWarnings("unchecked")
	public boolean generate()
	{
		try
		{
			String szUrl = VisualChineseAnalyzer.SERVLET_PREFIX + "generator";
			
			URL url = new URL(szUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.connect();
			
			OutputStream out = connection.getOutputStream();
			ObjectOutputStream oout = new ObjectOutputStream(out);
			oout.writeObject(lstNewWord);
			oout.flush();
			oout.close();
			out.close();
			
			InputStream in = connection.getInputStream();
			ObjectInputStream oin = new ObjectInputStream(in);
			lstNode =  (Vector<Node>) oin.readObject();
			oin.close();
			in.close();
		}
		catch (Exception e) { return false; }
		
		lstNewWord.clear();
		return lstNode.size() > 1;										//依存树不为平凡树
	}
	
	public int getCountOfNewWord()										//获取剩余未生成依存树的词数目
	{
		return lstNewWord.size();
	}
	
	public boolean insert(int nId, Node node)							//插入
	{
		if (nId < 0  || nId >= lstNode.size()) return false;
		lstNode.add(nId, node);
		return true;
	}
	
	public boolean remove(Node node)									//删除
	{
		return lstNode.remove(node);
	}
	
	public Vector<Node> getNodes()										//获取结点列表
	{
		return lstNode;
	}
	
	@Override
	public String toString()											//转换字符串
	{
		String szTree = "";
		for (Node node : lstNode)
		{
			if (szTree.equals("")) szTree += node;
			else szTree += " " + node;
		}
		return szTree;
	}
}
