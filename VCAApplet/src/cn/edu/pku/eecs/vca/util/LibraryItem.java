package cn.edu.pku.eecs.vca.util;

import java.io.Serializable;

public class LibraryItem implements Serializable		//在树库面板中显示的项目
{
	private static final long serialVersionUID = 7270981669144195726L;

	public static final int NEW = 0;					//新创建记录的ID为0
	
	private int nId;									//保存ID和原始文本
	private String szOriginal;
	
	public LibraryItem(int nId, String szOriginal)		//构造函数
	{
		this.nId = nId;
		this.szOriginal = szOriginal;
	}
	
	public int getId()									//返回ID
	{
		return nId;
	}
	
	@Override
	public String toString()							//转换字符串
	{
		return nId + " : " + szOriginal;
	}
}