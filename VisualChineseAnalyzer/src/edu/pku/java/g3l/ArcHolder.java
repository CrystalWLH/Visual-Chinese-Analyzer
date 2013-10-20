package edu.pku.java.g3l;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

public class ArcHolder implements HasContainerHolder		//弧线容器
{
	public static GraphPanel graph;							//所在图
	private static int TRIANGLE_SIZE = 4;					//箭头三角大小
	
	private NodeHolder nodeHolder, parentHolder;			//结点容器，父结点容器
	private TreeHolder treeHolder;							//树容器
	private Node node;										//结点
	
	private boolean bHover;									//悬停
	
	public ArcHolder(ContainerHolder container, NodeHolder nodeHolder, NodeHolder parentHolder)
	{
		this.nodeHolder = nodeHolder;						//构造函数
		this.parentHolder = parentHolder;
		
		treeHolder = (TreeHolder) container;				//父容器
		node = nodeHolder.getNode();						//结点
	}
	
	@Override
	public ContainerHolder getContainerHolder()				//返回父容器
	{
		return treeHolder;
	}
	
	@Override
	public Node getNode()									//获取结点
	{
		return node;
	}
	
	public int getRadius()									//半径
	{
		if (parentHolder == null) return 0;
		return Math.abs(nodeHolder.getX() - parentHolder.getX() + (nodeHolder.getWidth() - parentHolder.getWidth()) / 2) / 2;
	}
	
	public boolean isContaining(Point point)				//鼠标是否悬停
	{
		if (parentHolder == null) return false;
		
		final double DELTA = 5;								//误差像素数
		double fPointX, fPointY, fCenterX, fCenterY, fRadius, fResult;
		
		fPointX = point.getX();								//鼠标坐标
		fPointY = point.getY();
		
		fRadius = getRadius();								//半径
		fCenterX = (nodeHolder.getX() + nodeHolder.getWidth() / 2 + parentHolder.getX() + parentHolder.getWidth() / 2) / 2;
		fCenterY = treeHolder.getPanArcHeight();			//圆心
		
		fResult = Math.sqrt((fCenterX - fPointX) * (fCenterX - fPointX) + (fCenterY - fPointY) * (fCenterY - fPointY));
		return bHover = Math.abs(fResult - fRadius) < DELTA;//比较是否在圆上
	}
	
	public void paintComponent(Graphics g)					//重绘（事实上这不是控件）
	{
		Graphics2D gra2D = (Graphics2D) g;
		gra2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    
		if (bHover || graph.getCurrentHolder() == this || graph.getCurrentHolder() == nodeHolder)
		{
			gra2D.setPaint(Color.red);						//鼠标悬停、被选中、对应结点被选中，变红
		}
		else
		{
			gra2D.setPaint(Color.black);					//黑色
		}
	    
		if (parentHolder == null) return;					//没有父结点什么也不画
		
		int nRadius, nMaxRadius, nNodeX, nParentX;
		nRadius = getRadius();
		
		nMaxRadius = treeHolder.getPanArcHeight();
		
		nNodeX = nodeHolder.getX() + nodeHolder.getWidth() / 2;
		nParentX = parentHolder.getX() + parentHolder.getWidth() / 2;
		
		
		if (nNodeX < nParentX)								//父结点在子结点右边
		{
			int[] arrTriangleX = { nNodeX + nRadius - TRIANGLE_SIZE, nNodeX + nRadius - TRIANGLE_SIZE, nNodeX + nRadius + TRIANGLE_SIZE };
			int[] arrTriangleY = { nMaxRadius - nRadius - TRIANGLE_SIZE, nMaxRadius - nRadius + TRIANGLE_SIZE, nMaxRadius - nRadius };
		 
			gra2D.drawArc(nNodeX, nMaxRadius - nRadius, nRadius * 2, nRadius * 2, 0, 180);//画弧
			gra2D.fillPolygon(arrTriangleX, arrTriangleY, 3);//画三角
			 
			gra2D.drawString(node.getReln(), nNodeX + nRadius - 10, nMaxRadius - nRadius - 5);//输出关系类型
		}
		else												//父结点在子结点左边，同上
		{
			int[] arrTriangleX = { nParentX + nRadius - TRIANGLE_SIZE, nParentX + nRadius + TRIANGLE_SIZE, nParentX + nRadius + TRIANGLE_SIZE };
			int[] arrTriangleY = { nMaxRadius - nRadius, nMaxRadius - nRadius - TRIANGLE_SIZE, nMaxRadius - nRadius + TRIANGLE_SIZE };
			
			gra2D.drawArc(nParentX, nMaxRadius - nRadius, nRadius * 2, nRadius * 2, 0, 180);
			gra2D.fillPolygon(arrTriangleX, arrTriangleY, 3);
			gra2D.drawString(node.getReln(), nParentX + nRadius - 10, nMaxRadius - nRadius - 5);
		}
	}
}
