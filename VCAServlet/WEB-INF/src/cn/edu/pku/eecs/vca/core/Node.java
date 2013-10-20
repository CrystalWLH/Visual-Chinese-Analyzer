package cn.edu.pku.eecs.vca.core;

import java.io.Serializable;

public class Node implements Serializable					//依存结点
{
	private static final long serialVersionUID = 3814665455981390554L;
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
		this.szReln = szReln;
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
