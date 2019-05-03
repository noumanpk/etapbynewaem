package etap.core.servlets;

import java.io.IOException;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Reference;

import etap.core.EmployeeInter;

@SlingServlet(paths = "/bin/datacheck")
public class CheckServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	@Reference
    private EmployeeInter emplData;
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		try
        {
             
        String data=  emplData.getEmployeeData(); 
        response.getWriter().write(data);
      
        }
        catch (Exception e)
        {
            response.getWriter().println(e.getMessage());
        }
	}
}
