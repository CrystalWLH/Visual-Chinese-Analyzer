package edu.pku.java.g3l;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Node implements Serializable					//依存结点
{
	private static final long serialVersionUID = 3814665455981390554L;
	private static Map<String, String> mapStanford2Harbin;	//斯坦福到哈尔滨标记的映射
	
	public static void init()								//初始化
	{
		 mapStanford2Harbin = new HashMap<String, String>();//创建映射
	}
	
	private String szWord, szReln;
	private Node parent;
	
	public Node(String szWord, String szReln, Node parent)	//根据单词内容，依存关系，父结点创建
	{
		setWord(szWord);
		setRelation(szReln);								//设置依存关系
		setParent(parent);
	}
	
	public Node getParent()									//获取父结点
	{
		return parent;
	}
	
	public String getReln()									//获取依存关系
	{
		return szReln;
	}
	
	public String getWord()									//获取单词内容
	{
		return szWord;
	}
	
	public void setParent(Node parent)						//设置父结点
	{
		this.parent = parent;
	}

	public void setRelation(String szReln)					//设置依存关系
	{
		this.szReln = mapStanford2Harbin.get(szReln);		//通过映射转换依存标记
		if (this.szReln == null) this.szReln = szReln;
	}
	
	public void setWord(String szWord)						//设置文本
	{
		this.szWord = szWord;
	}
	
	@Override
	public String toString()								//字符串输出
	{
		return szWord;	
	}
}

class RootNode extends Node									//根结点
{
	private static final long serialVersionUID = -3370124988568337481L;

	public RootNode()										//无依存结点
	{
		super("Root", null, null);
	}
	
}
