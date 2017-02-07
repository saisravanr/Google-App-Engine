package com.vkrish11_caakula_vaniredd.gae;

import java.io.IOException;

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
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

public class FileFindServlet extends HttpServlet {
	private static final long serialVersionUID = -3388945387474831775L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html");

		String filename = req.getParameter("findfilename");
		long start = System.currentTimeMillis();
		MemcacheService mservice;
		mservice = MemcacheServiceFactory.getMemcacheService();
		if(mservice.contains(filename)){
			long laps = System.currentTimeMillis() - start;
			resp.getWriter().println("<h4>file '" + filename + "' exists in memcache</h4>");
			resp.getWriter().println("<h3>Time Taken in "+laps+" MilliSeconds</h3>");
			resp.getWriter().println("<a href='/FileView?fname="+filename+"&from=memcache'>Click Here To View File</a>");
		}else{	
		
		GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams
				.getDefaultInstance());
		AppIdentityService appIdentity = AppIdentityServiceFactory
				.getAppIdentityService();

		GcsFilename asdf = new GcsFilename(
				appIdentity.getDefaultGcsBucketName(), filename);
		GcsFileMetadata md = gcsService.getMetadata(asdf);
		if (md == null) {
			resp.getWriter().println("<h4>file '" + filename + "' Does not exist in both memcache and bucket</h4>");
		} else {
			long laps = System.currentTimeMillis() - start;
			resp.getWriter().println("<h4>file '" + filename + "' exists in bucket</h4>");
			resp.getWriter().println("<h3>Time Taken in "+laps+" MilliSeconds</h3>");
			resp.getWriter().println("<a href='/FileView?fname="+filename+"&from=bucket'>Click Here To View File</a>");
		}
		}

	}
}
