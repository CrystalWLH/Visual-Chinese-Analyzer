package edu.pku.java.g3l;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;

public class ArcPanel extends ContainerHolder implements HasContainerHolder//弧线面板
{
	private static final long serialVersionUID = 443901369209345158L;
	private TreeHolder treeHolder;

	public ArcPanel(ContainerHolder container)							//构造函数
	{
		treeHolder = (TreeHolder) container;
		setBackground(Color.white);
		
		addMouseListener(new MouseAdapter()								//鼠标监听
		{
			@Override
			public void mouseClicked(MouseEvent e)						//鼠标点击
			{
				if (!isEnabled()) return;								//禁用状态什么也不会发生
				
				boolean bFlag = false;
				for (ArcHolder arcHolder : treeHolder.getArcHoders())	//检查是否点中弧线
				{
					if (arcHolder.isContaining(e.getPoint()))
					{
						graph.setCurrentHolder(arcHolder);				//设置当前选中对象
						bFlag = true;
					}
					if (!bFlag)											//没有选中对象
					{
						graph.setCurrentHolder(treeHolder);				//选中树容器面板
					}
				}
				
				repaint();												//重绘
			}
			
			@Override
			public void mouseEntered(MouseEvent e)						//鼠标移入
			{
				treeHolder.setHover(true);								//树面版悬停
			}
			
			@Override
			public void mouseExited(MouseEvent e)						//鼠标移出
			{
				treeHolder.setHover(false);								//树面版取消悬停
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent e)						//鼠标移动
			{
				if (!isEnabled()) return;
				for (ArcHolder arcHolder : treeHolder.getArcHoders()) arcHolder.isContaining(e.getPoint());
			}															//悬停显示
		});
	}
	
	@Override
	public void disableHolder() { }
	@Override
	public void enableHolder() { }
	@Override
	public void insert(JComponent com) { }
	
	@Override
	public void paint(Graphics g)										//绘图时依次画出各弧
	{
		super.paint(g);
		
		for (ArcHolder arcHolder : treeHolder.getArcHoders()) arcHolder.paintComponent(g);
	}
	
	@Override
	public void rebuild() { }
	
	@Override
	public void refresh()
	{ 
		validate();
		repaint();
	}
	
	@Override
	public void remove(JComponent com) { }

	@Override
	public ContainerHolder getContainerHolder()
	{
		return treeHolder;
	}

	@Override
	public Node getNode()
	{
		return null;
	}
}
