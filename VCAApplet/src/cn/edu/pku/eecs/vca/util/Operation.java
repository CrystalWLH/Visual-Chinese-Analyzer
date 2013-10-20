package cn.edu.pku.eecs.vca.util;

import cn.edu.pku.eecs.vca.applet.ui.DocumentPanel;
import cn.edu.pku.eecs.vca.core.Forest;

public class Operation											//操作记录
{
	public static DocumentPanel document;						//当前文档
	
	private String szOperation;									//操作内容
	private Forest forest;									//操作前和操作后的依存森林
	
	public Operation(String szOperation)
	{
		this.szOperation = szOperation;							//设置操作内容和操作前依存森林
		this.forest = document.getForest().clone();
	}

	public void redo()											//恢复操作
	{
		document.setForest(forest.clone());
	}
	
	@Override
	public String toString()									//字符串输出其操作内容
	{
		return szOperation;
	}
}