package edu.pku.java.g3l;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.international.pennchinese.ChineseTreebankLanguagePack;
import edu.stanford.nlp.util.StringUtils;

public class DependencyTree implements Serializable						//单个依存树
{
	private static final long serialVersionUID = 938962563012531554L;
	private static LexicalizedParser parser;
	private static TreebankLanguagePack pack;
	private static GrammaticalStructureFactory factory;
	
	public static void init()											//初始化Stanford Parser
	{
		parser = LexicalizedParser.loadModel("chinesePCFG.ser.gz");
		pack = new ChineseTreebankLanguagePack();
		factory = pack.grammaticalStructureFactory();
	}
	
	private List<Node> lstNode = new ArrayList<Node>();					//结点列表
	private List<String> lstNewWord = new ArrayList<String>();			//新单词列表
	private RootNode root = new RootNode();								//根结点
	
	public DependencyTree()
	{
		lstNode.add(root);												//默认情况只有根结点
	}

	
	public boolean add(String szWord)									//添加单词
	{
		lstNewWord.add(szWord);
		
		if (!szWord.equals("。") && !szWord.equals("？") && 
			!szWord.equals("！") && !szWord.equals("；") &&
			!szWord.equals("!") && !szWord.equals("?") && !szWord.equals(";") &&
			!szWord.equals("……") && !(lstNewWord.size() > 1 && 
			 szWord.equals("…") && lstNewWord.get(lstNewWord.size() - 2).equals("…")))
			return false;	
		
		return generate();												//断句符号则生成依存树
	}
	
	public boolean generate()
	{
		List<CoreLabel> lstCoreLabel = new ArrayList<CoreLabel>();
		
		for (String szWord : lstNewWord)
		{
			CoreLabel label = new CoreLabel();
			label.setWord(szWord);
			lstCoreLabel.add(label);
		}
		lstNewWord.clear();												//清空加入的单词
		
		Tree tree = parser.apply(lstCoreLabel);							//生成依存树
		if (!tree.value().equals("ROOT")) return false;					//根结点标签必须为ROOT否则出错
		
		GrammaticalStructure structure = factory.newGrammaticalStructure(tree);//语法结构
		List<TypedDependency> lstTypedDependency = (List<TypedDependency>) structure.allTypedDependencies();//依存关系列表
		Map<Integer, Node> mapNode = new HashMap<Integer, Node>();
		
		for (TreeGraphNode nodGraph : structure.getNodes())				//添加各词
		{
			if (!nodGraph.isLeaf()) continue;							//必须是叶节点
			mapNode.put(nodGraph.index(), new Node(nodGraph.value(), null, null));
		}
		
		for (TypedDependency td : lstTypedDependency) 					//添加各词，父结点均设为root
		{
			Node node = mapNode.get(td.dep().index());					//获取结点
			
			int nParent = td.gov().index();								//父结点编号
			if (nParent > 0)											//父结点编号大于0
			{
				node.setRelation(td.reln().getShortName());
				node.setParent(mapNode.get(nParent));
			}															//直接设置父结点
			else
			{
				node.setRelation(td.reln().getShortName());				//父结点为根结点
				node.setParent(root);
			}
		}
		
		lstNode.clear();												//清空列表
		
		Iterator<Integer> iter = mapNode.keySet().iterator();			//将Map中结点装入List
		while (iter.hasNext()) lstNode.add(mapNode.get(iter.next().intValue()));
		
		lstNode.add(root);												//加入根结点
		
		return lstNode.size() > 1;										//依存树不为平凡树
	}
	
	public int getCountOfNewWord()										//获取剩余未生成依存树的词数目
	{
		return lstNewWord.size();
	}
	
	public boolean insert(int nId, Node node)							//插入
	{
		if (nId < 0  || nId >= lstNode.size()) return false;
		lstNode.add(nId, node);
		return true;
	}
	
	public boolean remove(Node node)									//删除
	{
		return lstNode.remove(node);
	}
	
	public List<Node> getNodes()										//获取结点列表
	{
		return lstNode;
	}
	
	@Override
	public String toString()											//转换字符串
	{
		return StringUtils.join(lstNode);
	}
}
