package edu.pku.java.g3l;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class FrameworkPanel extends JPanel					//框架面板 
{	
	public class SmartPanel extends JPanel					//标题或状态栏
	{
		private static final long serialVersionUID = 5639768125857668303L;
	    
		private Color clrA = new Color(255, 255, 255);		//渐变色
	    private Color clrB = new Color(208, 208, 208);
	    
		@Override
		public void paintComponent(Graphics g)				//重绘标题栏
		{ 
	        int nWidth = getWidth();
	        int nHeight = getHeight();
	        
	        Graphics2D g2 = (Graphics2D) g;
	        GradientPaint grpPaint = new GradientPaint(0, 0, clrA, 0, nHeight * 3 / 5, clrB, true);
	        g2.setPaint(grpPaint);
	        g2.fillRect(0, 0, nWidth, nHeight);				//绘制渐变矩形
		}
	}
	
	private static final long serialVersionUID = 3109308727272294569L;
	
	private SmartPanel panTitle, panTitleButton;			//标题栏，标题按钮栏
	private JScrollPane panMain;							//核心控件放在一个滚动面板中
	
	private JLabel lblTitle;								//标题标签
	private JComponent comMain;								//核心控件
	
	private int nTitleButtonWidth;							//标题按钮统一宽度
	
	public FrameworkPanel(String szTitle, JComponent comMain_, int nTitleButtonWidth_)
	{
		lblTitle = new JLabel(szTitle);						//创建标题标签
		lblTitle.setFont(null);
		
		comMain =comMain_;									//设置核心控件
		nTitleButtonWidth = nTitleButtonWidth_;				//按钮宽度

		panTitleButton = new SmartPanel();					//标题按钮拦
		panTitleButton.setLayout(new GridLayout());			//设置布局
		
		panTitle = new SmartPanel();						//标题栏
		panTitle.setLayout(new BorderLayout());
		
		panTitle.add(lblTitle, BorderLayout.WEST);			//设置各控件布局
		panTitle.add(panTitleButton, BorderLayout.EAST);		
		
		setLayout(new BorderLayout());
		
		add(panTitle, BorderLayout.NORTH);
		
		if (comMain != null)								//核心控件非空
		{
			panMain = new JScrollPane(getMain());			//放入滚动面板中
			panMain.setBorder(BorderFactory.createEtchedBorder());//添加边框
			
			add(panMain, BorderLayout.CENTER);
		}
		
		addMouseListener(new MouseAdapter()					//添加鼠标监听
		{
			@Override
			public void mouseClicked(MouseEvent e)		//点击面板设置焦点
			{
				requestFocus();
			}
		});	
	}
	
	public void addTitleButton(AquaButton button)			//添加标题按钮
	{
		panTitleButton.add(button);
		button.setPreferredSize(new Dimension(nTitleButtonWidth, 16));//设置默认大小
	}
	
	public SmartPanel getTitleBar()							//获取标题栏，以制作复合标题
	{
		return panTitle;
	}
	
	public SmartPanel getTitleButtonBar()					//获取按钮栏
	{
		return panTitleButton;
	}

	public JComponent getMain()								//获取核心控件
	{
		return comMain;
	}
	
	public JScrollPane getScrollMain()
	{
		return panMain;
	}
	
	public void setTitle(String szTitle)					//设置标题
	{
		lblTitle.setText(szTitle);
	}
}
