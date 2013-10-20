package cn.edu.pku.eecs.vca.applet.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import cn.edu.pku.eecs.vca.applet.VisualChineseAnalyzer;
import cn.edu.pku.eecs.vca.applet.graph.ContainerHolder;
import cn.edu.pku.eecs.vca.applet.graph.GraphPanel;
import cn.edu.pku.eecs.vca.applet.graph.NodeHolder;
import cn.edu.pku.eecs.vca.applet.graph.RelationPanel;
import cn.edu.pku.eecs.vca.core.Forest;
import cn.edu.pku.eecs.vca.util.IrreversibleOperation;
import cn.edu.pku.eecs.vca.util.Library;
import cn.edu.pku.eecs.vca.util.LibraryItem;
import cn.edu.pku.eecs.vca.util.Operation;






public class DocumentPanel extends JPanel implements ActionListener	//文档面板
{	
	public static VisualChineseAnalyzer program;					//当前程序
	private static final long serialVersionUID = 5534614329828798025L;
	
	private Forest forest;											//依存森林
	private int nId;												//文档编号
	
	private OperationPanel panOperation;							//操作记录面板
	private GraphPanel panGraph;									//绘图面板
	private JPanel panText;											//文本面板
	
	private AquaButton btnSegment, btnGenerate;						//分词按钮
	private JTextArea txtOriginal, txtSegmented;					//原始文本框，分词文本框

	private FocusAdapter txtOriginalFocusAdapter, txtSegmentedFocusAdapter;//文本框焦点适配器
	
	public DocumentPanel()											//构造函数
	{
		super(new BorderLayout());
		setStatic();
		
		initText();													//初始化文本框相关内容

		panOperation = new OperationPanel();						//操作记录面板
		panOperation.setPreferredSize(new Dimension(200, 400));		//设置默认大小
		
		panGraph = new GraphPanel();								//绘图面板
		panGraph.setPreferredSize(new Dimension(600, 400));			//设置默认面板
		
		add(panText, BorderLayout.NORTH);							//添加各面板
		add(panOperation, BorderLayout.EAST);		
		add(panGraph, BorderLayout.CENTER);
		
		createDocument();											//创建文档
	}
	
	
	public void actionPerformed(ActionEvent e)						//监视按钮点击事件
	{	
		if (e.getSource() == btnSegment)							//分词
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
					program.lock();
					final int nSegmented = forest.segment();
					program.unlock();
					
					try
					{
						SwingUtilities.invokeAndWait(new Runnable()
						{
							
							public void run()
							{
								if (nSegmented > 0)										//获得了分词结果
								{		
									showText();											//文本框和标题栏显示结果，状态栏显示提示
									program.setStatus("Sentence segmented successfully. " + Integer.toString(nSegmented) + " words found.");
									
									addOperation(new Operation("Segment"));							//添加操作
								}
								else
								{
									program.setStatus("Failed to segment the original sentence.");//状态栏提示
								}
							}
						});
					} catch (Exception e) { }
				}
			}).start();
			
			return;
		}
		
		if (e.getSource() == btnGenerate)												//生成树
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
					program.lock();
					final int nGenerated = forest.generate();
					program.unlock();
					try
					{
						SwingUtilities.invokeAndWait(new Runnable()
						{
							
							public void run()
							{
								if (nGenerated > 0)										//获得了生成树结果
								{			
									program.setStatus("Dependency trees generated successfully. " + Integer.toString(nGenerated) + " sentences found.");
																						//状态栏提示
									addOperation(new Operation("Generate Trees"));		//添加操作
									
									refresh();
								}
								else
								{
									program.setStatus("Failed to generate the dependency trees.");//状态栏提示
								}
							}
						});
					} 
					catch (Exception e) { }
				}
			}).start();
			
			return;
		}
	}
	
	public void addOperation(Operation operation)					//添加操作
	{
		panOperation.addOperation(operation);
	}
	
	public void createDocument()									//创建文档
	{
		nId = LibraryItem.NEW;										//ID标记为新文档
		setForest(new Forest());
		refresh();
		
		addOperation(new IrreversibleOperation("Create New Document"));//添加不可逆转的创建文档操作
	}
	
	public Forest getForest()										//获取依存森林
	{
		return forest;
	}
	
	public int getID()												//获取文档编号
	{
		return nId;
	}
		
	public void openDocument(int nId, Forest forest)				//打开文档
	{
		this.nId = nId;												//文档ID
		setForest(forest);											//设置森林
		refresh();
		
		program.setStatus("Open document " + nId + ": " + forest.getOriginal() + ".");
		addOperation(new IrreversibleOperation("Open Document " + nId + ": " + forest.getOriginal()));
	}																//添加不可逆转的打开文档操作
	
	public void refresh()											//刷新
	{
		showText();													//显示文本框等信息
		
		panGraph.getCanvas().enableHolder();
		panGraph.rebuild();
	}
	
	public void saveDocument()										//保存文档
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
				program.lock();
				nId = Library.save(nId, forest);							//获取保存后的新ID
				program.unlock();
				try
				{
					SwingUtilities.invokeAndWait(new Runnable()
					{
						
						public void run()
						{
							if (nId != LibraryItem.NEW)									//保存成功
							{
								program.setStatus("Dependency trees saved successfully.");//状态栏提示
								program.getLibrary().refresh();							//更新树库列表
								
								showText();
							}
							else
							{
								program.setStatus("Failed to save the dependency trees.");//提示保存失败
							}
						}
					});
				}
				catch (Exception e) { }
			}
		}).start();
	}
	
	public void setForest(Forest forest)							//设置森林
	{
		this.forest = forest;
		panGraph.setCurrentHolder(null);
	}
	
	public void setStatic()											//设置静态变量
	{
		LibraryPanel.document = this;
		
		Operation.document = this;
		OperationPanel.document = this;
		
		GraphPanel.document = this;
		RelationPanel.document = this;
		
		ContainerHolder.document = this;
		NodeHolder.document = this;
	}
	
	private void initText()
	{
		btnSegment = new AquaButton("Segment");						//分词按钮
		btnSegment.addActionListener(this);
				
		txtOriginal = new JTextArea();								//原始文本框
		txtOriginal.setLineWrap(true);								//自动换行
		txtOriginal.setRows(3);										//默认三行
		txtOriginal.addMouseListener(new TextMenu());				//右键菜单
		
		FrameworkPanel panOriginal = new FrameworkPanel("Original Sentence", txtOriginal, 120);
		panOriginal.addTitleButton(btnSegment);						//创建框架面板

		btnGenerate = new AquaButton("Generate Trees");				//生成树按钮
		btnGenerate.addActionListener(this);
		
		txtSegmented = new JTextArea();								//分词文本框，设置同原始文本框
		txtSegmented.setLineWrap(true);
		txtSegmented.setRows(3);
		txtSegmented.addMouseListener(new TextMenu());

		FrameworkPanel panSegmented = new FrameworkPanel("Segmented Sentence", txtSegmented, 120);
		panSegmented.addTitleButton(btnGenerate);					//创建框架面板
		
		panText = new JPanel(new BorderLayout());
		panText.add(panOriginal, BorderLayout.CENTER);
		panText.add(panSegmented, BorderLayout.SOUTH);				//两个面板叠加
		
		txtOriginalFocusAdapter = new FocusAdapter()				//原始文本框焦点监听
		{
			
			public void focusLost(FocusEvent event)
			{
				String szOriginal = txtOriginal.getText();
				if (!szOriginal.equals(forest.getOriginal()))		//原始文本框内容与依存森林原始文本不符
				{					
					forest.setOriginal(szOriginal);					//更新原始文本
					showText();
					program.setStatus("Change the original text to " + szOriginal + ".");
					
					addOperation(new Operation("Change Original Text: " + szOriginal));
				}
			} 
		};
		txtOriginal.addFocusListener(txtOriginalFocusAdapter);		//添加原始文本框焦点监听
		
		txtSegmentedFocusAdapter = new FocusAdapter()				//分词文本框焦点监听
		{
			
			public void focusLost(FocusEvent event)
			{
				String szSegmented = txtSegmented.getText();		//分词文本框内容与依存森林分词文本不符
				if (!szSegmented.equals(forest.getSegmented()))
				{					
					forest.setSegmented(szSegmented);				//更新分词文本
					program.setStatus("Change the segmented text to " + szSegmented + ".");
					
					addOperation(new Operation("Change Segmented Text: " + szSegmented));
				}
			} 
		};
		txtSegmented.addFocusListener(txtSegmentedFocusAdapter);	//添加分词文本框监听
	}
	
	private void showText()											//刷新各文本框内容和标题
	{
		txtOriginal.setText(forest.getOriginal());					//原始文本框显示依存森林原始文本
		txtSegmented.setText(forest.getSegmented());				//分词文本框显示依存森林分词文本
	}
}
