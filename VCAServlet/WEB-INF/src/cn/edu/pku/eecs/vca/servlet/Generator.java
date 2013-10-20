package cn.edu.pku.eecs.vca.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.pku.eecs.vca.core.Node;
import cn.edu.pku.eecs.vca.core.RootNode;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.international.pennchinese.ChineseTreebankLanguagePack;

public class Generator extends HttpServlet
{
	private static final long serialVersionUID = -3172225981297335992L;
	private LexicalizedParser parser;
	private TreebankLanguagePack pack;
	private GrammaticalStructureFactory factory;
	
	public void init()
	{
		parser = LexicalizedParser.loadModel("../webapps/VCAServlet/WEB-INF/chinesePCFG.ser.gz");
		pack = new ChineseTreebankLanguagePack();
		factory = pack.grammaticalStructureFactory();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
	    {
			response.setContentType("application/octet-stream");  
	        response.setHeader("Cache-Control", "no-cache");
	        
	        InputStream in = request.getInputStream();
	    	ObjectInputStream oin = new ObjectInputStream(in);
	    	Vector<String> lstNewWord = (Vector<String>) oin.readObject();
	    	oin.close();
	    	in.close();
	        
	        RootNode root = new RootNode();
			Vector<Node> lstNode = new Vector<Node>();
			
			Vector<CoreLabel> lstCoreLabel = new Vector<CoreLabel>();
			
			for (String szWord : lstNewWord)
			{
				CoreLabel label = new CoreLabel();
				label.setWord(szWord);
				lstCoreLabel.add(label);
			}
			
			Tree tree = parser.apply(lstCoreLabel);							//生成依存树
			if (tree.value().equals("ROOT"))
			{
				GrammaticalStructure structure = factory.newGrammaticalStructure(tree);//语法结构
				List<TypedDependency> lstTypedDependency = (List<TypedDependency>) structure.allTypedDependencies();//依存关系列表
				Hashtable<Integer, Node> mapNode = new Hashtable<Integer, Node>();
				
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
				while (iter.hasNext()) lstNode.add(0, mapNode.get(iter.next().intValue()));
			}
			lstNode.add(root);
			
			OutputStream out = response.getOutputStream();
	    	ObjectOutputStream oout = new ObjectOutputStream(out);
	        oout.writeObject(lstNode);
	    	oout.flush();
	    	oout.close();
	    	out.close();
	    }
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
