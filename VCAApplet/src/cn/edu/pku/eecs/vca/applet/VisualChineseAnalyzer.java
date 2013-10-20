package cn.edu.pku.eecs.vca.applet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JApplet;
import javax.swing.JPanel;
import javax.swing.UIManager;

import cn.edu.pku.eecs.vca.applet.graph.ContainerHolder;
import cn.edu.pku.eecs.vca.applet.graph.GraphPanel;
import cn.edu.pku.eecs.vca.applet.graph.NodeHolder;
import cn.edu.pku.eecs.vca.applet.graph.RelationPanel;
import cn.edu.pku.eecs.vca.applet.ui.DocumentPanel;
import cn.edu.pku.eecs.vca.applet.ui.FrameworkPanel;
import cn.edu.pku.eecs.vca.applet.ui.LibraryPanel;
import cn.edu.pku.eecs.vca.applet.ui.OperationPanel;

public class VisualChineseAnalyzer extends JApplet							//主界面
{
	public static final String SERVLET_PREFIX = "http://localhost:8080/VCAServlet/";
	private static final long serialVersionUID = 5642575057432746168L;
	
	private FrameworkPanel panStatus;										//状态栏
	private LibraryPanel panLibrary;										//树库面板
	private DocumentPanel panDocument;										//文档面板

	private Boolean bMtx;
	
	public boolean isLocked()
	{
		synchronized(bMtx)
		{
			return bMtx;
		}
	}
	
	public void lock()
	{
		synchronized(bMtx)
		{
			bMtx = true;
		}
	}
	
	public void unlock()
	{
		synchronized(bMtx)
		{
			bMtx = false;
		}
	}
	
	public void start()
	{
		setSize(1024, 576);
	}
	
	@Override
	public  void init()
	{
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}
		catch (Exception e) { }
		
        System.setProperty("awt.useSystemAAFontSettings", "on");  
        System.setProperty("swing.aatext", "true");  
		
		setStatic();
		bMtx = false;
		
		JPanel panMain, panWorkspace;										//主面板，工作区（去状态栏）面板
		
		panStatus = new FrameworkPanel("", null, 0);						//状态栏
		
		panLibrary = new LibraryPanel();									//创建树库面板，按钮宽度55
		panLibrary.setPreferredSize(new Dimension(250, 400));				//默认大小

		panDocument = new DocumentPanel();									//文档面板
		
		panWorkspace = new JPanel(new BorderLayout());
		panWorkspace.add(panLibrary, BorderLayout.WEST);
		panWorkspace.add(panDocument, BorderLayout.CENTER);
		
		panMain = new JPanel(new BorderLayout())
		{
			private static final long serialVersionUID = 376333955042668222L;

			@Override
			public void update(Graphics g)//双缓冲
			{	
				Image bufferImg = null;
				if (bufferImg == null) bufferImg = createImage(getWidth(), getHeight());
				
				Graphics buffer = bufferImg.getGraphics();
				paint(buffer);
				
				g.drawImage(bufferImg, 0, 0, null);
			}
		};
		setContentPane(panMain);
		panMain.add(panWorkspace, BorderLayout.CENTER);
		panMain.add(panStatus, BorderLayout.SOUTH);							//安排各面板布局
		
		setStatus("Copyright (C) Aqua, JL, Randomizey 2013. All rights reserved.");
		addMouseListener(new MouseAdapter()									//提示欢迎信息，添加鼠标监听
		{
			@Override
			public void mouseClicked(MouseEvent e)							//点击窗体设置焦点
			{
				requestFocus();
			}
		});
	}
	
	public DocumentPanel getDocument()										//获得文档面板
	{
		return panDocument;
	}
	
	public LibraryPanel getLibrary()										//获得树库面板
	{
		return panLibrary;
	}
	
	public void setStatic()													//设置静态变量
	{
		DocumentPanel.program = this;
		LibraryPanel.program = this;
		
		OperationPanel.program = this;
		
		GraphPanel.program = this;
		RelationPanel.program = this;
		
		ContainerHolder.program = this;
		NodeHolder.program = this;
	}

	public void setStatus(String szStatus)									//设置状态栏状态
	{ 
		panStatus.setTitle(szStatus); 
	}

	public String getStatus()
	{
		return panStatus.getTitle();
	}
}
