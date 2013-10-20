package edu.pku.java.g3l;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class CanvasPanel extends ContainerHolder	//画板
{	
	private static final long serialVersionUID = -631863239584114200L;
	
	private ForestHolder forestHolder;				//包含的森林容器
	private JPanel panContent; 
	
	public CanvasPanel()
	{
		RelationPanel.canvas = this;				//设置静态变量
		setBackground(Color.white);					//白色背景
		setLayout(new BorderLayout());
		
		panContent = new JPanel(new BorderLayout());//内容面板
		panContent.setBackground(Color.white);
		add(panContent, BorderLayout.NORTH);
	}
	
	@Override
	public void insert(JComponent com) { }
	
	@Override
	public void disableHolder()
	{
		setEnabled(false);							//禁用
		forestHolder.disableHolder();				//禁用其包含的森林
	}
	
	@Override
	public void enableHolder()
	{
		setEnabled(true);							//启用
		if (forestHolder != null) forestHolder.enableHolder();//启用其包含的森林
	}
	
	@Override
	public void rebuild()							//刷新
	{
		graph.setCurrentHolder(null);				//设置当前选中容器为空
		panContent.removeAll();						//删除内容
		
		forestHolder = new ForestHolder(document.getForest());
		panContent.add(forestHolder);				//添加森林容器
	}
	
	@Override
	public void refresh()							//刷新
	{
		forestHolder.refresh();
		
		validate();
		repaint();
	}

	@Override
	public void remove(JComponent com) { }
}
