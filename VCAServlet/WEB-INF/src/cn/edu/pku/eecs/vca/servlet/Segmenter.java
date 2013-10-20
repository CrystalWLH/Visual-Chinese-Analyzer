package cn.edu.pku.eecs.vca.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Word;

public class Segmenter extends HttpServlet
{
	private static final long serialVersionUID = -1483204242757995066L;
	private Dictionary dic;
    private ComplexSeg seg;
    
    @Override
	public void init()
	{
		dic = Dictionary.getInstance("../webapps/VCAServlet/WEB-INF/data");
    	seg = new ComplexSeg(dic);
	}
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	try
    	{
    		response.setContentType("application/octet-stream");  
            response.setHeader("Cache-Control", "no-cache"); 
            Vector<String> lstSegmented = new Vector<String>();
            
	    	InputStream in = request.getInputStream();
	    	ObjectInputStream oin = new ObjectInputStream(in);
	    	String szOriginal = (String) oin.readObject();
	    	oin.close();
	    	in.close();
	    	
			MMSeg mmSeg = new MMSeg(new StringReader(szOriginal), seg);
	        Word word = null;
	        
	        try
	        {
				while ((word = mmSeg.next()) != null) lstSegmented.add(word.getString());
			}														//添加各词条
	        catch (Exception e)
	        { 
	        	lstSegmented.clear();
	        	e.printStackTrace();
	        }
	        
	        OutputStream out = response.getOutputStream();
	    	ObjectOutputStream oout = new ObjectOutputStream(out);
	        oout.writeObject(lstSegmented);
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
