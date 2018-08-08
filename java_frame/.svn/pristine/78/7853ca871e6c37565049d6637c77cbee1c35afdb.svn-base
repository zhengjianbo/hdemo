package com.ram.kettle.xml;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.util.Const;
import com.ram.kettle.value.ValueMeta;

public class XMLHandler {
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			ValueMeta.DEFAULT_DATE_FORMAT_MASK);

	public static final String getTagValue(Node n, String tag) {
		NodeList children;
		Node childnode;

		if (n == null)
			return null;

		children = n.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			childnode = children.item(i);
			if (childnode.getNodeName().equalsIgnoreCase(tag)) {
				if (childnode.getFirstChild() != null)
					return childnode.getFirstChild().getNodeValue();
			}
		}
		return null;
	}

	public static final String getTagValueWithAttribute(Node n, String tag,
			String attribute) {
		NodeList children;
		Node childnode;

		if (n == null)
			return null;

		children = n.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			childnode = children.item(i);
			if (childnode.getNodeName().equalsIgnoreCase(tag)
					&& childnode.getAttributes().getNamedItem(attribute) != null) {
				if (childnode.getFirstChild() != null)
					return childnode.getFirstChild().getNodeValue();
			}
		}
		return null;
	}

	public static final String getTagValue(Node n, String tag, String subtag) {
		NodeList children, tags;
		Node childnode, tagnode;

		if (n == null)
			return null;

		children = n.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			childnode = children.item(i);
			if (childnode.getNodeName().equalsIgnoreCase(tag)) // <file>
			{
				tags = childnode.getChildNodes();
				for (int j = 0; j < tags.getLength(); j++) {
					tagnode = tags.item(j);
					if (tagnode.getNodeName().equalsIgnoreCase(subtag)) {
						if (tagnode.getFirstChild() != null)
							return tagnode.getFirstChild().getNodeValue();
					}
				}
			}
		}
		return null;
	}

	public static final int countNodes(Node n, String tag) {
		NodeList children;
		Node childnode;

		int count = 0;

		if (n == null)
			return 0;

		children = n.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			childnode = children.item(i);
			if (childnode.getNodeName().equalsIgnoreCase(tag)) // <file>
			{
				count++;
			}
		}
		return count;
	}

	/**
	 * Get nodes with a certain tag one level down
	 * 
	 * @param n
	 *            The node to look in
	 * @param tag
	 *            The tags to count
	 * @return The list of nodes found with the specified tag
	 */
	public static final List<Node> getNodes(Node n, String tag) {
		NodeList children;
		Node childnode;

		List<Node> nodes = new ArrayList<Node>();

		if (n == null)
			return nodes;

		children = n.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			childnode = children.item(i);
			if (childnode.getNodeName().equalsIgnoreCase(tag)) // <file>
			{
				nodes.add(childnode);
			}
		}
		return nodes;
	}

	/**
	 * Get node child with a certain subtag set to a certain value
	 * 
	 * @param n
	 *            The node to search in
	 * @param tag
	 *            The tag to look for
	 * @param subtag
	 *            The subtag to look for
	 * @param subtagvalue
	 *            The value the subtag should have
	 * @param nr
	 *            The nr of occurance of the value
	 * @return The node found or null if we couldn't find anything.
	 */
	public static final Node getNodeWithTagValue(Node n, String tag,
			String subtag, String subtagvalue, int nr) {
		NodeList children;
		Node childnode, tagnode;
		String value;

		int count = 0;

		children = n.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			childnode = children.item(i);
			if (childnode.getNodeName().equalsIgnoreCase(tag)) // <hop>
			{
				tagnode = getSubNode(childnode, subtag);
				value = getNodeValue(tagnode);
				if (value.equalsIgnoreCase(subtagvalue)) {
					if (count == nr)
						return childnode;
					count++;
				}
			}
		}
		return null;
	}

	/**
	 * Get node child with a certain subtag set to a certain value
	 * 
	 * @param n
	 *            The node to search in
	 * @param tag
	 *            The tag to look for
	 * @param subtag
	 *            The subtag to look for
	 * @param subtagvalue
	 *            The value the subtag should have
	 * @param copyNr
	 *            The nr of occurance of the value
	 * @return The node found or null if we couldn't find anything.
	 */
	public static final Node getNodeWithAttributeValue(Node n, String tag,
			String attributeName, String attributeValue) {
		NodeList children;
		Node childnode;

		children = n.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			childnode = children.item(i);
			if (childnode.getNodeName().equalsIgnoreCase(tag)) // <hop>
			{
				Node attribute = childnode.getAttributes().getNamedItem(
						attributeName);

				if (attribute != null
						&& attributeValue.equals(attribute.getTextContent()))
					return childnode;
			}
		}
		return null;
	}

	/**
	 * Search for a subnode in the node with a certain tag.
	 * 
	 * @param n
	 *            The node to look in
	 * @param tag
	 *            The tag to look for
	 * @return The subnode if the tag was found, or null if nothing was found.
	 */
	public static final Node getSubNode(Node n, String tag) {
		int i;
		NodeList children;
		Node childnode;

		if (n == null)
			return null;

		// Get the childres one by one out of the node,
		// compare the tags and return the first found.
		//
		children = n.getChildNodes();
		for (i = 0; i < children.getLength(); i++) {
			childnode = children.item(i);
			if (childnode.getNodeName().equalsIgnoreCase(tag)) {
				return childnode;
			}
		}
		return null;
	}

	/**
	 * Search a node for a child of child
	 * 
	 * @param n
	 *            The node to look in
	 * @param tag
	 *            The tag to look for in the node
	 * @param subtag
	 *            The tag to look for in the children of the node
	 * @return The sub-node found or null if nothing was found.
	 */
	public static final Node getSubNode(Node n, String tag, String subtag) {
		Node t = getSubNode(n, tag);
		if (t != null)
			return getSubNode(t, subtag);
		return null;
	}

	/**
	 * Get a subnode in a node by nr.<br>
	 * This method uses caching and assumes you loop over subnodes in sequential
	 * order (nr is increasing by 1 each call)
	 * 
	 * @param n
	 *            The node to look in
	 * @param tag
	 *            The tag to count
	 * @param nr
	 *            The position in the node
	 * @return The subnode found or null in case the position was invalid.
	 */
	public static final Node getSubNodeByNr(Node n, String tag, int nr) {
		return getSubNodeByNr(n, tag, nr, true);
	}

	/**
	 * Get a subnode in a node by nr.<br>
	 * It optially allows you to use caching.<br>
	 * Caching assumes that you loop over subnodes in sequential order (nr is
	 * increasing by 1 each call)
	 * 
	 * @param n
	 *            The node to look in
	 * @param tag
	 *            The tag to count
	 * @param nr
	 *            The position in the node
	 * @param useCache
	 *            set this to false if you don't want to use caching. For
	 *            example in cases where you want to loop over subnodes of a
	 *            certain tag in reverse or random order.
	 * @return The subnode found or null in case the position was invalid.
	 */
	public static final Node getSubNodeByNr(Node n, String tag, int nr,
			boolean useCache) {
		NodeList children;
		Node childnode;

		if (n == null)
			return null;

		int count = 0;
		// Find the child-nodes of this Node n:
		children = n.getChildNodes();

		int lastChildNr = -1;
		// XMLHandlerCacheEntry entry = null;

		// if (useCache) {
		// entry = new XMLHandlerCacheEntry(n, tag);
		// lastChildNr = cache.getLastChildNr(entry);
		// }
		if (lastChildNr < 0) {
			lastChildNr = 0;
		} else {
			count = nr; // we assume we found the previous nr-1 at the
						// lastChildNr
			lastChildNr++; // we left off at the previouso one, so continue with
							// the next.
		}

		for (int i = lastChildNr; i < children.getLength(); i++) // Try all
																	// children
		{
			childnode = children.item(i);
			if (childnode.getNodeName().equalsIgnoreCase(tag)) // We found the
																// right tag
			{
				if (count == nr) {
					// if (useCache)
					// cache.storeCache(entry, i);
					return childnode;
				}
				count++;
			}
		}
		return null;
	}

	/**
	 * Find the value entry in a node
	 * 
	 * @param n
	 *            The node
	 * @return The value entry as a string
	 */
	public static final String getNodeValue(Node n) {
		if (n == null)
			return null;

		// Find the child-nodes of this Node n:
		NodeList children = n.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) // Try all children
		{
			Node childnode = children.item(i);
			String retval = childnode.getNodeValue();
			if (retval != null) // We found the right value
			{
				return retval;
			}
		}
		return null;
	}

	public static final String getTagAttribute(Node node, String attribute) {
		if (node == null)
			return null;

		String retval = null;

		NamedNodeMap nnm = node.getAttributes();
		if (nnm != null) {
			Node attr = nnm.getNamedItem(attribute);
			if (attr != null) {
				retval = attr.getNodeValue();
			}
		}
		return retval;
	}

	/**
	 * Load a file into an XML document
	 * 
	 * @param filename
	 *            The filename to load into a document
	 * @return the Document if all went well, null if an error occurred!
	 */
	public static final Document loadXMLFile(String filename)
			throws KettleException {
		try {
			return loadXMLFile(KettleVFS.getFileObject(filename));
		} catch (Exception e) {
			throw new KettleException(e);
		}
	}

	/**
	 * Load a file into an XML document
	 * 
	 * @param filename
	 *            The filename to load into a document
	 * @return the Document if all went well, null if an error occured!
	 */
	public static final Document loadXMLFile(FileObject fileObject)
			throws KettleException {
		return loadXMLFile(fileObject, null, false, false);
	}

	public static final Document loadXMLFile(FileObject fileObject,
			String systemID, boolean ignoreEntities, boolean namespaceAware)
			throws KettleException {
		try {
			return loadXMLFile(KettleVFS.getInputStream(fileObject), systemID,
					ignoreEntities, namespaceAware);
		} catch (FileSystemException e) {
			throw new KettleException("Unable to read file ["
					+ fileObject.toString() + "]", e);
		}
	}

	/**
	 * Load a file into an XML document
	 * 
	 * @param inputStream
	 *            The stream to load a document from
	 * @param systemId
	 *            Provide a base for resolving relative URIs.
	 * @param ignoreEntities
	 *            Ignores external entities and returns an empty dummy.
	 * @param namespaceAware
	 *            support XML namespaces.
	 * @return the Document if all went well, null if an error occured!
	 */
	public static final Document loadXMLFile(InputStream inputStream,
			String systemID, boolean ignoreEntities, boolean namespaceAware)
			throws KettleException {
		DocumentBuilderFactory dbf;
		DocumentBuilder db;
		Document doc;

		try {
			dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringComments(true);
			dbf.setNamespaceAware(namespaceAware);
			db = dbf.newDocumentBuilder();

			if (ignoreEntities) {
				db.setEntityResolver(new DTDIgnoringEntityResolver());
			}

			try {
				if (Const.isEmpty(systemID)) {
					doc = db.parse(inputStream);
				} else {
					String systemIDwithEndingSlash = systemID.trim();

					if (!systemIDwithEndingSlash.endsWith("/")
							&& !systemIDwithEndingSlash.endsWith("\\")) {
						systemIDwithEndingSlash = systemIDwithEndingSlash
								.concat("/");
					}
					doc = db.parse(inputStream, systemIDwithEndingSlash);
				}
			} catch (FileNotFoundException ef) {
				throw new KettleException(ef);
			} finally {
				if (inputStream != null)
					inputStream.close();
			}

			return doc;
		} catch (Exception e) {
			throw new KettleException(
					"Error reading information from input stream", e);
		}
	}

	public static final String getString() {
		return XMLHandler.class.getName();
	}

	/**
	 * Take the characters from string val and append them to the value
	 * stringbuffer In case a character is not allowed in XML, we convert it to
	 * an XML code
	 * 
	 * @param value
	 *            the stringbuffer to append to
	 * @param string
	 *            the string to "encode"
	 */
	public static void appendReplacedChars(StringBuffer value, String string) {
		// If it's a CDATA content block, leave those parts alone.
		//
		boolean isCDATA = string.startsWith("<![CDATA[")
				&& string.endsWith("]]>");

		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			switch (c) {
			case '&':
				value.append("&amp;");
				break;
			case '\'':
				value.append("&apos;");
				break;
			case '<':
				if (i != 0 || !isCDATA) {
					value.append("&lt;");
				} else {
					value.append(c);
				}
				break;
			case '>':
				if (i != string.length() - 1 || !isCDATA) {
					value.append("&gt;");
				} else {
					value.append(c);
				}
				break;
			case '"':
				value.append("&quot;");
				break;
			case '/':
				if (isCDATA) // Don't replace slashes in a CDATA block, it's
								// just not right.
				{
					value.append(c);
				} else {
					value.append("&#47;");
				}
				break;
			case 0x1A:
				value.append("{ILLEGAL XML CHARACTER 0x1A}");
				break;
			default:
				value.append(c);
			}
		}
	}

	public static String[] getNodeAttributes(Node node) {
		NamedNodeMap nnm = node.getAttributes();
		if (nnm != null) {
			String attributes[] = new String[nnm.getLength()];
			for (int i = 0; i < nnm.getLength(); i++) {
				Node attr = nnm.item(i);
				attributes[i] = attr.getNodeName();
			}
			return attributes;
		}
		return null;

	}

	public static String[] getNodeElements(Node node) {
		ArrayList<String> elements = new ArrayList<String>(); // List of String

		NodeList nodeList = node.getChildNodes();
		if (nodeList == null)
			return null;

		for (int i = 0; i < nodeList.getLength(); i++) {
			String nodeName = nodeList.item(i).getNodeName();
			if (elements.indexOf(nodeName) < 0)
				elements.add(nodeName);
		}

		if (elements.isEmpty())
			return null;

		return elements.toArray(new String[elements.size()]);
	}

	public static Date stringToDate(String dateString) {
		if (Const.isEmpty(dateString))
			return null;

		try {
			synchronized (simpleDateFormat) {
				return simpleDateFormat.parse(dateString);
			}
		} catch (ParseException e) {
			return null;
		}
	}

	public static String date2string(Date date) {
		if (date == null)
			return null;
		synchronized (simpleDateFormat) {
			return simpleDateFormat.format(date);
		}
	}

	/**
	 * Convert a XML encoded binary string back to binary format
	 * 
	 * @param string
	 *            the (Byte64/GZip) encoded string
	 * @return the decoded binary (byte[]) object
	 * @throws KettleException
	 *             In case there is a decoding error
	 */
	public static byte[] stringToBinary(String string) throws KettleException {
		try {
			byte[] bytes;
			if (string == null) {
				bytes = new byte[] {};
			} else {
				bytes = (string.getBytes());// Base64.decodeBase64
			}
			if (bytes.length > 0) {
				ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				GZIPInputStream gzip = new GZIPInputStream(bais);
				BufferedInputStream bi = new BufferedInputStream(gzip);
				byte[] result = new byte[] {};

				byte[] extra = new byte[1000000];
				int nrExtra = bi.read(extra);
				while (nrExtra >= 0) {
					// add it to bytes...
					//
					int newSize = result.length + nrExtra;
					byte[] tmp = new byte[newSize];
					for (int i = 0; i < result.length; i++)
						tmp[i] = result[i];
					for (int i = 0; i < nrExtra; i++)
						tmp[result.length + i] = extra[i];

					// change the result
					result = tmp;
					nrExtra = bi.read(extra);
				}
				bytes = result;
				gzip.close();
			}

			return bytes;
		} catch (Exception e) {
			throw new KettleException("Error converting string to binary", e);
		}
	}

	public static String buildCDATA(String string) {
		StringBuffer cdata = new StringBuffer("<![CDATA[");
		cdata.append(Const.NVL(string, "")).append("]]>");
		return cdata.toString();
	}

	public static final String openTag(String tag) {
		return "<" + tag + ">";
	}

	public static final String closeTag(String tag) {
		return "</" + tag + ">";
	}

}

class DTDIgnoringEntityResolver implements EntityResolver {
	public DTDIgnoringEntityResolver() {
		// nothing
	}

	public InputSource resolveEntity(java.lang.String publicID,
			java.lang.String systemID) throws IOException {
		ConstLog.message("Public-ID: " + publicID.toString());
		ConstLog.message("System-ID: " + systemID.toString());
		return new InputSource(new ByteArrayInputStream(
				"<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
	}

}
