package com.vkrish11_caakula_vaniredd.gae;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.Channels;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFileMetadata;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

public class FileSearchRegXServlet extends HttpServlet {
	private static final long serialVersionUID = 7021199467789730572L;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter pw = resp.getWriter();
		String filename = req.getParameter("serachfname");
		String text = req.getParameter("serachtext");
		long start = System.currentTimeMillis();
		String from;
		boolean found = false;
		MemcacheService mservice;
		mservice = MemcacheServiceFactory.getMemcacheService();
		if(mservice.contains(filename)){
			from = "memcache";
		}else{
			from = "bucket";
		}
		
		
		if (from.equals("bucket")) {
			
			GcsService gcsService = GcsServiceFactory
					.createGcsService(RetryParams.getDefaultInstance());
			AppIdentityService appIdentity = AppIdentityServiceFactory
					.getAppIdentityService();

			GcsFilename asdf = new GcsFilename(
					appIdentity.getDefaultGcsBucketName(), filename);
			GcsFileMetadata md = gcsService.getMetadata(asdf);
			if (md != null) {
				pw.print("<h3>File read from bucket</h3>");
				pw.print("<hr>");
			GcsInputChannel readChannel = null;
			BufferedReader reader = null;
			try {
				readChannel = gcsService.openReadChannel(asdf, 0);
				reader = new BufferedReader(Channels.newReader(readChannel,
						"UTF8"));
				String line;
				while ((line = reader.readLine()) != null) {
					if(line.contains(text)){
						found =true;
						line.replaceAll(text, "<b><u>"+text+"</u></b>");
					}
					pw.print(line);
					pw.print("<br>");
				}
			} finally {
				if (reader != null) {
					long laps = System.currentTimeMillis() - start;
					if(found)resp.getWriter().println("<h3>Text '"+text+"' is found in File '"+filename+ "' </h3>");
					resp.getWriter().println("<h3>Total Time Taken To Read File From Bucket: "+laps+" MilliSeconds</h3>");
					reader.close();
				}
			}}else {
				pw.print("<h3>no file found with key : "+filename+"</h3>");
			}
		}else if (from.equals("memcache")){
			pw.print("<h3>File read from memCache</h3>");
			pw.print("<hr>");
			
			String val = (String) mservice.get(filename);
			if(val.contains(text)){
				found =true;
				val.replaceAll(text, "<b><u>"+text+"</u></b>");
			}
			pw.print(val);
			long laps = System.currentTimeMillis() - start;
			if(found)resp.getWriter().println("<h3>Text '"+text+"' is found in File '"+filename+ "' </h3>");
			resp.getWriter().println("<h3>Total Time Taken To Read File From MemCache: "+laps+" MilliSeconds</h3>");
		}else{
			pw.print("<h4>no file found with key : "+filename+"</h4>");
		}
	}

}
