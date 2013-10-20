package edu.pku.java.g3l;

public class Operation											//操作记录
{
	public static DocumentPanel document;						//当前文档
	
	private String szOperation;									//操作内容
	private Forest oldForest, newForest;						//操作前和操作后的依存森林
	
	public Operation(String szOperation, Forest oldForest)
	{
		this.szOperation = szOperation;							//设置操作内容和操作前依存森林
		if (oldForest != null) this.oldForest = oldForest.clone();
	}
	
	public void redo()											//恢复操作
	{
		document.setForest(newForest.clone());
	}
	
	public void setNewForest(Forest newForest)					//设置操作后依存森林
	{
		if (newForest != null) this.newForest = newForest.clone();
	}
	
	@Override
	public String toString()									//字符串输出其操作内容
	{
		return szOperation;
	}
	
	public void undo()											//撤消操作
	{
		document.setForest(oldForest.clone());
	}
}

class IrreversibleOperation extends Operation					//不可逆操作（打开文档，创建文档）
{
	public IrreversibleOperation(String szOperation)
	{
		super(szOperation, null);
	}

	@Override
	public void redo() { }
	
	@Override
	public void undo() { }
}