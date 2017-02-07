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
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.ListItem;
import com.google.appengine.tools.cloudstorage.ListOptions;
import com.google.appengine.tools.cloudstorage.ListResult;
import com.google.appengine.tools.cloudstorage.RetryParams;

public class FileDeleteAllServlet extends HttpServlet {
	private static final long serialVersionUID = 6652678985217321552L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType("text/html");
		String from = req.getParameter("from");
		
		long start = System.currentTimeMillis();
		
		if (from.equals("memcache")) {
			MemcacheService mservice;
			mservice = MemcacheServiceFactory.getMemcacheService();
			mservice.clearAll();
			long laps = System.currentTimeMillis() - start;
			resp.getWriter().println(
					"<h3>All files from memcache are deleted! in "+laps+" MilliSeconds</h3>");
		} else if (from.equals("bucket")) {
			GcsService gcsService = GcsServiceFactory
					.createGcsService(RetryParams.getDefaultInstance());
			AppIdentityService appIdentity = AppIdentityServiceFactory
					.getAppIdentityService();

			ListResult result = gcsService.list(
					appIdentity.getDefaultGcsBucketName(), ListOptions.DEFAULT);
			while (result.hasNext()) {
				ListItem l = result.next();
				String filename = l.getName();
				GcsFilename asdf = new GcsFilename(
						appIdentity.getDefaultGcsBucketName(), filename);
				gcsService.delete(asdf);
			}
			long laps = System.currentTimeMillis() - start;
			resp.getWriter().println(
					"<h3>All files from bucket are deleted! in "+laps+" MilliSeconds</h3>");
		}
	}
}
