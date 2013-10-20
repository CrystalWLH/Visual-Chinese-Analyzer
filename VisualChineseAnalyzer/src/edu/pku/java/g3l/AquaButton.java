package edu.pku.java.g3l;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JButton;

public class AquaButton extends JButton						//重绘按钮
{
	private static final long serialVersionUID = -8914153473555065239L;
	
	private final Color clrA = new Color(243, 249, 254);	//两种渐变色
	private final Color clrB = new Color(13, 155, 217);
	private final Color clrC = new Color(100, 100, 100);	//按钮边框颜色
	
	private boolean bHover;									//是否有鼠标悬停
	
	public AquaButton(String szText)						//根据文本内容创建按钮
	{
		super(szText);
		
		setMargin(new Insets(0, 0, 0, 0));					//边缘设为最小
		setBorderPainted(false);							//不自动绘制边缘
		setFocusPainted(false);								//不绘制获得焦点
		setContentAreaFilled(false);						//不填满区域
		
		addMouseListener(new MouseAdapter()					//鼠标监听
		{
			@Override
			public void mouseEntered(MouseEvent e)			//鼠标移入
			{
				setForeground(Color.black);					//前景为黑色
				bHover = true;								//悬停
				repaint();									//重绘
			}
			
			@Override
			public void mouseExited(MouseEvent e)			//鼠标移出
			{
				setForeground(Color.darkGray);				//前景为深灰色
				bHover = false;								//不悬停
				repaint();									//重绘
			}
		});
	}
	
	@Override
	protected void paintComponent(Graphics g)				//重绘
	{
		Graphics2D g2 = (Graphics2D) g.create();
		int nHeight = getHeight();							//按钮高度
		int nWidth = getWidth();							//按钮宽度
		float fTranparency;									//渐变的透明度
		
		if (bHover && isEnabled())							//鼠标悬停且按钮未被禁用
		{
			if (getModel().isPressed())						//按钮被点击
			{
				fTranparency = 1F;							//渐变不透明
			}
			else
			{
				fTranparency = 0.7F;						//渐变较不透明
			}
		}
		else
		{
			fTranparency = 0.3F;							//渐变较透明
		}

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);			
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fTranparency));
															//应用设好的透明度
		RoundRectangle2D.Float rou2D = new RoundRectangle2D.Float(0, 0, nWidth, nHeight, 12, 12);
		Shape shaClip = g2.getClip();						//保存绘图区域
		g2.clip(rou2D);										//设置绘图区域为圆角矩形
		
		GradientPaint graPaint = new GradientPaint(0, 0, clrA, 0, nHeight / 2, clrB, true);
		g2.setPaint(graPaint);
		g2.fillRect(-1, 0, nWidth, nHeight);				//绘制渐变
		
		if (getModel().isPressed())							//鼠标按下与否按钮边框渐变方向相反
		{
			graPaint = new GradientPaint(0, 0, Color.black, 0, nHeight, clrC, true);
		}
		else
		{
			graPaint = new GradientPaint(0, 0, clrC, 0, nHeight, Color.black, true);
		}
		
		g2.setPaint(graPaint);
		g2.setClip(shaClip);								//绘制圆角矩形边框
		g2.drawRoundRect(0, 0, nWidth - 1, nHeight - 1, 12, 12);
		
		super.paintComponent(g);							//基类的绘图函数
	}
}
