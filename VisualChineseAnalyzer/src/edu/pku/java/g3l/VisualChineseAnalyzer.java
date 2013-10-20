package edu.pku.java.g3l;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class VisualChineseAnalyzer extends JFrame							//主界面
{		
	private static final long serialVersionUID = 5642575057432746168L;
	
	public static void main(String[] args)
	{
		Library.init();														//初始化数据库
		Node.init();														//初始化结点（斯坦福-哈工大映射）
		DependencyTree.init();												//初始化依存树（Stanford Parser）
		Forest.init();														//初始化森林（经过改写的mmseg中文分词）
		
		VisualChineseAnalyzer frmMain = new VisualChineseAnalyzer();		//创建窗体
		frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMain.setVisible(true);
		
		Runtime.getRuntime().addShutdownHook(new OnExit());					//添加钩子，关闭时断开数据库连接
	}
	
	private FrameworkPanel panStatus;										//状态栏
	private LibraryPanel panLibrary;										//树库面板
	private DocumentPanel panDocument;										//文档面板
	
	private VisualChineseAnalyzer()
	{
		setStatic();
		
		JPanel panMain, panWorkspace;										//主面板，工作区（去状态栏）面板
		setTitle("Visual Chinese Analyzer");								//设置标题
		
		panStatus = new FrameworkPanel("", null, 0);						//状态栏
		
		panLibrary = new LibraryPanel();									//创建树库面板，按钮宽度55
		panLibrary.setPreferredSize(new Dimension(250, 400));				//默认大小

		panDocument = new DocumentPanel();									//文档面板
		
		panWorkspace = new JPanel(new BorderLayout());
		panWorkspace.add(panLibrary, BorderLayout.WEST);
		panWorkspace.add(panDocument, BorderLayout.CENTER);
		
		panMain = new JPanel(new BorderLayout());
		setContentPane(panMain);
		panMain.add(panWorkspace, BorderLayout.CENTER);
		panMain.add(panStatus, BorderLayout.SOUTH);							//安排各面板布局
		
		setStatus("Copyright (C) Aqua 2012. All rights reserved.");
		addMouseListener(new MouseAdapter()									//提示欢迎信息，添加鼠标监听
		{
			@Override
			public void mouseClicked(MouseEvent e)							//点击窗体设置焦点
			{
				requestFocus();
			}
		});
		
		pack();																//调整窗体大小
	}
	
	public DocumentPanel getDocument()										//获得文档面板
	{
		return panDocument;
	}
	
	public LibraryPanel getLibrary()										//获得树库面板
	{
		return panLibrary;
	}
	
	public void setTitle(int nId, String szTitle)							//根据在数据库中id和原始文本设置标题
	{
		super.setTitle("Visual Chinese Analyzer - " + nId + " : " + szTitle);
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
}

class OnExit extends Thread													//退出时断开SQLite数据库连接
{
	@Override
	public void run()
	{
		Library.close();
	}
}

interface HasContainerHolder												//画依存树时有包含面板的组件
{
	public ContainerHolder getContainerHolder();
	public Node getNode();
}
