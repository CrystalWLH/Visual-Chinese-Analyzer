package cn.edu.pku.eecs.vca.applet.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cn.edu.pku.eecs.vca.applet.VisualChineseAnalyzer;
import cn.edu.pku.eecs.vca.core.Forest;
import cn.edu.pku.eecs.vca.util.Library;
import cn.edu.pku.eecs.vca.util.LibraryItem;






public class LibraryPanel extends FrameworkPanel implements ActionListener
{
	private static final long serialVersionUID = 8049012673554836767L;
	
	public static VisualChineseAnalyzer program;
	public static DocumentPanel document;
	
	private JPanel panMultiTitle, panSubTitle;			//复合标题、副标题面板
	
	private JLabel lblSubTitle;							//副标题标签
	private JTextField txtSearch;						//搜索框
	private AquaButton btnNew, btnDelete, btnShowAll;	//新建文档，删除文档，显示所有文档按钮
	
	private JList lstLibrary;							//树库列表
	private DefaultListModel dlmLibrary;
	
	public LibraryPanel()
	{
		super("Library", new JList(), 65);				//创建一个标题为Library内容为JList的框架面版
		
		lstLibrary = (JList) getMain();					//获取列表
		dlmLibrary = new DefaultListModel();
		
		lstLibrary.setModel(dlmLibrary);
		lstLibrary.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//只允许选中一项
		
		lstLibrary.addFocusListener(new FocusAdapter()	// 焦点监听
		{
			private final Color clrFocused = new Color(57, 105, 138);	//获得焦点时蓝色背景
			private final Color clrNotFocused = new Color(187, 207, 229);//失去焦点时淡蓝色背景
			
			
			public void focusGained(FocusEvent event)	//列表获得焦点
			{
				lstLibrary.setSelectionBackground(clrFocused);//设置选中项背景
			} 
			
			
			public void focusLost(FocusEvent event)		//列表失去焦点
			{
				lstLibrary.setSelectionBackground(clrNotFocused);//设置选中项背景
			} 	
		});
		
		lstLibrary.addListSelectionListener(new ListSelectionListener()//项目选中监听
		{
			
			public void valueChanged(ListSelectionEvent e)
			{
				openDocument();							//若有项目选中，打开文档
			}
		});
		
		lstLibrary.addMouseListener(new MouseAdapter()	//鼠标监听
		{
			
			public void mouseClicked(MouseEvent e)
			{
				openDocument();							//点击了项目，打开文档
			}
		});
		
		btnNew = new AquaButton("New");					//添加各按钮，设置各按钮监听
		btnNew.addActionListener(this);
		addTitleButton(btnNew);
		
		btnDelete = new AquaButton("Delete");
		btnDelete.addActionListener(this);
		btnDelete.setEnabled(false);
		addTitleButton(btnDelete);
		
		btnShowAll = new AquaButton("All");
		btnShowAll.addActionListener(this);
		addTitleButton(btnShowAll);
		
		
		lblSubTitle = new JLabel("Search:");			//副标题标签
		lblSubTitle.setFont(null);
		
		txtSearch = new JTextField();					//搜索框
		txtSearch.addMouseListener(new TextMenu());		//右键菜单
		txtSearch.getDocument().addDocumentListener(new DocumentListener()
		{												//搜索框文本一旦改变，立即刷新列表
			
			public void changedUpdate(DocumentEvent e)
			{
				refresh();
			}

			
			public void insertUpdate(DocumentEvent e)
			{
				refresh();
			}

			
			public void removeUpdate(DocumentEvent e)
			{
				refresh();
			}
			
		});
		
		panSubTitle = new JPanel();						//副标题
		panSubTitle.setLayout(new BorderLayout());
		
		panSubTitle.add(lblSubTitle, BorderLayout.WEST);
		panSubTitle.add(txtSearch, BorderLayout.CENTER);
				
		panMultiTitle = new JPanel(new BorderLayout());
		panMultiTitle.add(getTitleBar(), BorderLayout.NORTH);
		panMultiTitle.add(panSubTitle, BorderLayout.CENTER);
		
		add(panMultiTitle, BorderLayout.NORTH);			//将原有标题和副标题和为复合标题
		
		refresh();										//刷新列表
	}
	
	
	public void actionPerformed(ActionEvent e)			//按钮按下
	{
		if (e.getSource() == btnNew)					//创建文档
		{
			document.createDocument();							
			refresh();									//刷新列表
			return;
		}
		
		if (e.getSource() == btnDelete)					//删除选中记录
		{
			if (program.isLocked())
			{
				program.setStatus("Another thread accessing servlet is already running.");
				return;
			}
			
			program.setStatus("Loading Servlet...");
			
			new Thread(new Runnable()
			{
				
				public void run()
				{
					final LibraryItem item = (LibraryItem) lstLibrary.getSelectedValue();
					program.lock();
					final boolean bFlag = Library.delete(item.getId());
					program.unlock();
					
					try
					{
						SwingUtilities.invokeAndWait(new Runnable()
						{
							public void run()
							{
								if (bFlag)									//返回被删除成功与否，显示提示
								{
									program.setStatus("Dependency tree deleted successfully.");
									refresh();								//删除成功，刷新列表
								}
								else
								{
									program.setStatus("Failed to delete the dependency tree.");
								}
							}
						});
					}
					catch (Exception e) { }
				}
			}).start();
			
			return;
		}
		
		if (e.getSource() == btnShowAll)				//显示全部
		{
			txtSearch.setText("");						//清空搜索框
			return;
		}		
	}
	
	public void refresh()								//刷新
	{
		lstLibrary.clearSelection();					//清除列表选择
		final String szKeyword = txtSearch.getText().trim();	//去掉搜索关键词首尾空格
		
		final String szStatus = program.getStatus();
		program.setStatus("Loading Servlet...");
		
		new Thread(new Runnable()
		{
			
			public void run()
			{
				final List<LibraryItem> lstResult;
				program.lock();
				if (szKeyword.equals(""))						//无关键词，搜索全部记录
				{
					lstResult = Library.queryAll();
				}
				else											//搜索原始文本包含指定内容的记录
				{
					lstResult = Library.queryByText(szKeyword);
				}
				program.unlock();
				
				try
				{
					SwingUtilities.invokeAndWait(new Runnable()
					{
						public void run()
						{
							dlmLibrary.clear();								//清空列表，重新添加各项
							if (lstResult != null) for (LibraryItem item : lstResult) dlmLibrary.addElement(item);
							if (!szStatus.equals("Loading Servlet...")) program.setStatus(szStatus);
						}
					});
				}
				catch (Exception e) { }
			}
		}).start();
	}

	
	private void openDocument()							//打开文档
	{
		btnDelete.setEnabled(!lstLibrary.isSelectionEmpty());//如果没有选中项目，不能启用删除按钮
		if (lstLibrary.isSelectionEmpty()) return;		//没有选中项目，直接返回
		
		if (program.isLocked())
		{
			program.setStatus("Another thread accessing servlet is already running.");
			return;
		}
		
		program.setStatus("Loading Servlet...");
		
		new Thread(new Runnable()
		{
			
			public void run()
			{
				final LibraryItem item = (LibraryItem) lstLibrary.getSelectedValue();
				final int nId = item.getId();
				program.lock();
				final Forest forest = Library.queryById(nId);			//获取文档，显示提示
				program.unlock();
				
				try
				{
					SwingUtilities.invokeAndWait(new Runnable()
					{
						public void run()
						{
							if (forest == null)
							{
								program.setStatus("Failed to open " + item);//无法获取文档
							}
							else
							{
								document.openDocument(nId, forest);			//打开文档
							}
						}
					});
				}
				catch (Exception e) { }
			}
		}).start();
	}
}
