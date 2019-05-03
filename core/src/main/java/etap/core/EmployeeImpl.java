package etap.core;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;

import java.util.List;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//Sling Imports
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Session;

@Component
public class EmployeeImpl implements EmployeeInter {

	/** Default log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	private Session session;

	// Inject a Sling ResourceResolverFactory
	@Reference
	private ResourceResolverFactory resolverFactory;

	public String getEmployeeData() {
		Employee employee = null;

		List<Employee> employList = new ArrayList<Employee>();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE, "datawrite");
		ResourceResolver resolver = null;

		try {

			// Invoke the adaptTo method to create a Session used to create a
			// QueryManager
			resolver = resolverFactory.getServiceResourceResolver(param);
			session = resolver.adaptTo(Session.class);

			log.info("Created session");

			// Obtain the query manager for the session ...
			javax.jcr.query.QueryManager queryManager = session.getWorkspace().getQueryManager();

			// Setup the quesry based on user input
			String sqlStatement = "";

			// Setup the query to get all employee nodes
			sqlStatement = "SELECT * FROM [nt:unstructured] AS t WHERE ISDESCENDANTNODE('/content/employees')And contains (status, 'employee')";

			javax.jcr.query.Query query = queryManager.createQuery(sqlStatement, "JCR-SQL2");

			// Execute the query and get the results ...
			javax.jcr.query.QueryResult result = query.execute();

			// Iterate over the nodes in the results ...
			javax.jcr.NodeIterator nodeIter = result.getNodes();

			while (nodeIter.hasNext()) {

				// For each node-- create an Employee instance
				employee = new Employee();

				javax.jcr.Node node = nodeIter.nextNode();

				// Set all Employee object fields
				employee.setName(node.getProperty("name").getString());
				employee.setAddress(node.getProperty("address").getString());
				employee.setPosition(node.getProperty("job").getString());
				employee.setAge(node.getProperty("age").getString());
				employee.setDate(node.getProperty("start").getString());
				employee.setSalary(node.getProperty("salary").getString());

				// Push the Employee Object to the list
				employList.add(employee);
			}

			// Log out
			session.logout();
			return convertToString(toXml(employList));

		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// Convert Employee data retrieved from the AEM JCR
	// into an XML schema to pass back to client
	private Document toXml(List<Employee> employeeList) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();

			// Start building the XML to pass back to the AEM client
			Element root = doc.createElement("Employees");
			doc.appendChild(root);

			// Get the elements from the collection
			int custCount = employeeList.size();

			// Iterate through the collection to build up the DOM
			for (int index = 0; index < custCount; index++) {

				// Get the Employee object from the collection
				Employee myEmployee = (Employee) employeeList.get(index);

				Element Employee = doc.createElement("Employee");
				root.appendChild(Employee);

				// Add rest of data as child elements to Employee
				// Set Name
				Element name = doc.createElement("Name");
				name.appendChild(doc.createTextNode(myEmployee.getName()));
				Employee.appendChild(name);

				// Set Address
				Element address = doc.createElement("Address");
				address.appendChild(doc.createTextNode(myEmployee.getAddress()));
				Employee.appendChild(address);

				// Set position
				Element position = doc.createElement("Position");
				position.appendChild(doc.createTextNode(myEmployee.getPosition()));
				Employee.appendChild(position);

				// Set age
				Element age = doc.createElement("Age");
				age.appendChild(doc.createTextNode(myEmployee.getAge()));
				Employee.appendChild(age);

				// Set Date
				Element date = doc.createElement("Date");
				date.appendChild(doc.createTextNode(myEmployee.getDate()));
				Employee.appendChild(date);

				// Set sal
				Element salary = doc.createElement("Salary");
				salary.appendChild(doc.createTextNode(myEmployee.getSalary()));
				Employee.appendChild(salary);
			}

			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String convertToString(Document xml) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(xml);
			transformer.transform(source, result);
			return result.getWriter().toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}