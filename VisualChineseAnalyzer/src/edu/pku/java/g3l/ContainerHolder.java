package edu.pku.java.g3l;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;

public abstract class ContainerHolder extends JPanel	//包含内容的面板容器（非结点容器和弧线容器）
{
	public static VisualChineseAnalyzer program;
	public static DocumentPanel document;
	public static GraphPanel graph;
	
	private static final long serialVersionUID = -8180896074495045356L;
	
	public ContainerHolder()
	{
		if (this instanceof ArcPanel)
		{
			addMouseListener(new MouseAdapter()			//点击获得焦点
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					((ArcPanel)ContainerHolder.this).getContainerHolder().requestFocus();
					graph.setCurrentHolder(null);		//获得焦点后设置当前处理对象
					refresh();
				}
			});
			return;			
		}
		
		addMouseListener(new MouseAdapter()				//点击获得焦点
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				requestFocus();
				graph.setCurrentHolder(null);			//获得焦点后设置当前处理对象
				refresh();
			}
		});
	}
	
	public abstract void disableHolder();				//禁用
	public abstract void enableHolder();				//启用
	public abstract void insert(JComponent com);		//插入
	public abstract void rebuild();						//重构
	public abstract void refresh();						//刷新
	public abstract void remove(JComponent com);		//删除
}
