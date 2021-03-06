﻿package edu.pku.java.g3l;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Word;

import edu.stanford.nlp.util.StringUtils;

public class Forest implements Serializable						//依存森林
{
	private static final long serialVersionUID = 8495943381120324062L;
	
	private static Dictionary dic;
    private static ComplexSeg seg;
    
    public static Forest createFromBytes(byte[] bytes)			//从字节数组创建依存森林
    {
    	Forest forest = null;
    	
    	try
    	{
    		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);//字节数组输入流
    		ObjectInputStream ois = new ObjectInputStream(bis);	//对象输入流
    		
    		forest = (Forest)ois.readObject();					//读取对象
    		
    		ois.close();  										//关闭各流
    		bis.close();  		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	return forest;											//返回创建的依存森林
    }
    
    public static void init()									//初始化分词模块
    {
    	dic = Dictionary.getInstance();
    	seg = new ComplexSeg(dic);
    }
    
	private String szOriginal = "";								//原始文本
	private List<String> lstSegmented= new ArrayList<String>();	//分词后的词条
	private List<DependencyTree> lstTree = new ArrayList<DependencyTree>();//依存树列表
	
	public Forest clone()										//复制依存森林副本
	{
		return createFromBytes(this.getBytes());				//当前对象转换为字节数组再创建新对象
	}
	
	public int generate()										//生成依存树
	{
		List<DependencyTree> lstNewTrees = new ArrayList<DependencyTree>();//新依存树列表
		DependencyTree tree = new DependencyTree();				//要添加的依存树
	    
		for (String szWord : lstSegmented)						//枚举各词条
		{
			if (tree.add(szWord))								//如果添加词条导致了依存树的创建
			{
				lstNewTrees.add(tree);							//加入依存树
				tree = new DependencyTree();					//新依存树
			}
		}
		if (tree.getCountOfNewWord() > 0 && tree.generate()) lstNewTrees.add(tree);//还没创建完的依存树
		
		if (lstNewTrees.size() == 0) return 0;					//没有建立依存树
		lstTree = lstNewTrees;									//替换依存树列表
		return lstTree.size();									//返回依存树个数
	}
	
	public String getOriginal()									//获取原始文本
	{
		return szOriginal;
	}
	
	public String getSegmented()								//获取分词文本
	{
		return StringUtils.join(lstSegmented, " ");				//分开的词条合并，以空格相隔
	}
	
	public byte[] getBytes()									//转换为字节数组
    {
		byte[] bytes = null;
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();//字节数组输出流
			ObjectOutputStream oos = new ObjectOutputStream(bos);//对象输出流
    	
			oos.writeObject(this);								//写对象
			bytes = bos.toByteArray();							//转换为字节数组
			
			oos.close();										//关闭各流
			bos.close();		
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return bytes;											//返回字节数组
    }
	
	public List<DependencyTree> getTrees()						//获取依存树列表
	{
		return lstTree;
	}
	
	public boolean insert(int nId, DependencyTree tree)			//插入
	{
		if (nId < 0  || nId > lstTree.size()) return false;
		lstTree.add(nId, tree);
		return true;
	}
	
	public boolean remove(DependencyTree tree)					//删除
	{
		return lstTree.remove(tree);
	}
	
	public int segment()										//分词
	{
		List<String> lstNewSegmented = new ArrayList<String>();	//新分词列表
		MMSeg mmSeg = new MMSeg(new StringReader(szOriginal), seg);
        Word word = null;
        
        try
        {
			while ((word = mmSeg.next()) != null) lstNewSegmented.add(word.getString());
		}														//添加各词条
        catch (IOException e)
		{	
			return 0;
		}
		
		if (lstNewSegmented.size() ==0) return 0;				//分词条目为0
		lstSegmented = lstNewSegmented;							//替换分词列表
		return lstSegmented.size();								//返回分词数
	}
	
	public void setOriginal(String szOriginal)					//设置原始文本
	{
		this.szOriginal = szOriginal;
	}
	
	public void setSegmented(String szSegmented)				//设置分词文本
	{
		String[] arrSegmented = szSegmented.split(" ");			//分词文本由空格隔开
		
		lstSegmented.clear();									//清空旧分词列表
		for (String szWord : arrSegmented)
		{
			if (szWord.equals("")) continue;					//分词不能为空
			lstSegmented.add(szWord);							//添加新分词
		}
	}
	
	@Override
	public String toString()									//转换字符串
	{
		return lstTree.toString();
	}
}
