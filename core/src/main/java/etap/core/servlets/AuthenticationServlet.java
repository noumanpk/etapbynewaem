package etap.core.servlets;

import java.io.IOException;
import java.rmi.ServerException;

import javax.jcr.Session;
import javax.servlet.ServletException;


import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

@SlingServlet(paths = "/bin/loginservlet")
public class AuthenticationServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;


	@Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		
		/*
		 * //authentication using static values
		 * response.setContentType("text/html"); String[]
		 * query=request.getQueryString().split("&"); String
		 * username=query[0].split("=")[1]; String
		 * password=query[1].split("=")[1];
		 * 
		 * if(username.equals("anirudh")&&password.equals("anirudh")){
		 * response.setStatus(200);
		 * 
		 * response.getWriter().print("<h1> hi "+username+"</h1>"); }else{
		 * response.getWriter().print("wrong username and password");
		 * response.setStatus(400); }
		 */
		response.getWriter().println("Get Authentication Servlet");
    }
	
	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServerException, IOException {
		ResourceResolver resolver = null;
		try {

			resolver = request.getResourceResolver();
			Session session = resolver.adaptTo(Session.class);
			
			String userName = request.getParameter("username");
			String password = request.getParameter("password");
			javax.jcr.query.QueryManager queryManager = session.getWorkspace().getQueryManager();
			String sqlStatement = "";

			sqlStatement = "SELECT * FROM [nt:unstructured] AS t WHERE ISDESCENDANTNODE('/content/Users')";

			javax.jcr.query.Query query = queryManager.createQuery(sqlStatement, "JCR-SQL2");

			javax.jcr.query.QueryResult result = query.execute();
			
			javax.jcr.NodeIterator nodeIter = result.getNodes();
			
			boolean flag=false;
			while (nodeIter.hasNext()) {
				javax.jcr.Node node = nodeIter.nextNode();
				if(node.getProperty("userName").getString().equals(userName)&&node.getProperty("password").getString().equals(password)){
					flag=true;
				}

			} 
			if(flag){
				response.setStatus(200);
			}else{
				response.setStatus(400);
			}
			resolver.close();

		} catch (Exception e) {
			response.getWriter().println(e.toString());
		}

	}
}