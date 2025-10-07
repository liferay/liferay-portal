/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.xml;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProvider;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.xml.Attribute;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Entity;
import com.liferay.portal.kernel.xml.Namespace;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.ProcessingInstruction;
import com.liferay.portal.kernel.xml.QName;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.kernel.xml.Text;
import com.liferay.portal.kernel.xml.XMLSchema;
import com.liferay.portal.kernel.xml.XPath;
import com.liferay.portal.security.xml.SecureXMLFactoryProviderImpl;
import com.liferay.util.xml.XMLSafeReader;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class SAXReaderImpl implements SAXReader {

	public static List<Attribute> toNewAttributes(
		List<org.dom4j.Attribute> oldAttributes) {

		List<Attribute> newAttributes = new ArrayList<>(oldAttributes.size());

		for (org.dom4j.Attribute oldAttribute : oldAttributes) {
			newAttributes.add(new AttributeImpl(oldAttribute));
		}

		return new NodeList<>(newAttributes, oldAttributes);
	}

	public static List<Element> toNewElements(
		List<org.dom4j.Element> oldElements) {

		List<Element> newElements = new ArrayList<>(oldElements.size());

		for (org.dom4j.Element oldElement : oldElements) {
			newElements.add(new ElementImpl(oldElement));
		}

		return new NodeList<>(newElements, oldElements);
	}

	public static List<Namespace> toNewNamespaces(
		List<org.dom4j.Namespace> oldNamespaces) {

		List<Namespace> newNamespaces = new ArrayList<>(oldNamespaces.size());

		for (org.dom4j.Namespace oldNamespace : oldNamespaces) {
			newNamespaces.add(new NamespaceImpl(oldNamespace));
		}

		return new NodeList<>(newNamespaces, oldNamespaces);
	}

	public static List<Node> toNewNodes(List<org.dom4j.Node> oldNodes) {
		List<Node> newNodes = new ArrayList<>(oldNodes.size());

		for (org.dom4j.Node oldNode : oldNodes) {
			if (oldNode instanceof org.dom4j.Element) {
				newNodes.add(new ElementImpl((org.dom4j.Element)oldNode));
			}
			else {
				newNodes.add(new NodeImpl(oldNode));
			}
		}

		return new NodeList<>(newNodes, oldNodes);
	}

	public static List<ProcessingInstruction> toNewProcessingInstructions(
		List<org.dom4j.ProcessingInstruction> oldProcessingInstructions) {

		List<ProcessingInstruction> newProcessingInstructions = new ArrayList<>(
			oldProcessingInstructions.size());

		for (org.dom4j.ProcessingInstruction oldProcessingInstruction :
				oldProcessingInstructions) {

			newProcessingInstructions.add(
				new ProcessingInstructionImpl(oldProcessingInstruction));
		}

		return new NodeList<>(
			newProcessingInstructions, oldProcessingInstructions);
	}

	public static List<org.dom4j.Attribute> toOldAttributes(
		List<Attribute> newAttributes) {

		List<org.dom4j.Attribute> oldAttributes = new ArrayList<>(
			newAttributes.size());

		for (Attribute newAttribute : newAttributes) {
			AttributeImpl newAttributeImpl = (AttributeImpl)newAttribute;

			oldAttributes.add(newAttributeImpl.getWrappedAttribute());
		}

		return oldAttributes;
	}

	public static List<org.dom4j.Node> toOldNodes(List<Node> newNodes) {
		List<org.dom4j.Node> oldNodes = new ArrayList<>(newNodes.size());

		for (Node newNode : newNodes) {
			NodeImpl newNodeImpl = (NodeImpl)newNode;

			oldNodes.add(newNodeImpl.getWrappedNode());
		}

		return oldNodes;
	}

	public static List<org.dom4j.ProcessingInstruction>
		toOldProcessingInstructions(
			List<ProcessingInstruction> newProcessingInstructions) {

		List<org.dom4j.ProcessingInstruction> oldProcessingInstructions =
			new ArrayList<>(newProcessingInstructions.size());

		for (ProcessingInstruction newProcessingInstruction :
				newProcessingInstructions) {

			ProcessingInstructionImpl newProcessingInstructionImpl =
				(ProcessingInstructionImpl)newProcessingInstruction;

			oldProcessingInstructions.add(
				newProcessingInstructionImpl.getWrappedProcessingInstruction());
		}

		return oldProcessingInstructions;
	}

	@Override
	public Attribute createAttribute(
		Element element, QName qName, String value) {

		ElementImpl elementImpl = (ElementImpl)element;
		QNameImpl qNameImpl = (QNameImpl)qName;

		return new AttributeImpl(
			_documentFactory.createAttribute(
				elementImpl.getWrappedElement(), qNameImpl.getWrappedQName(),
				value));
	}

	@Override
	public Attribute createAttribute(
		Element element, String name, String value) {

		ElementImpl elementImpl = (ElementImpl)element;

		return new AttributeImpl(
			_documentFactory.createAttribute(
				elementImpl.getWrappedElement(), name, value));
	}

	@Override
	public Document createDocument() {
		return new DocumentImpl(_documentFactory.createDocument());
	}

	@Override
	public Document createDocument(Element rootElement) {
		ElementImpl rootElementImpl = (ElementImpl)rootElement;

		return new DocumentImpl(
			_documentFactory.createDocument(
				rootElementImpl.getWrappedElement()));
	}

	@Override
	public Document createDocument(String encoding) {
		return new DocumentImpl(_documentFactory.createDocument(encoding));
	}

	@Override
	public Element createElement(QName qName) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		return new ElementImpl(
			_documentFactory.createElement(qNameImpl.getWrappedQName()));
	}

	@Override
	public Element createElement(String name) {
		return new ElementImpl(_documentFactory.createElement(name));
	}

	@Override
	public Entity createEntity(String name, String text) {
		return new EntityImpl(_documentFactory.createEntity(name, text));
	}

	@Override
	public Namespace createNamespace(String uri) {
		return new NamespaceImpl(org.dom4j.Namespace.get(uri));
	}

	@Override
	public Namespace createNamespace(String prefix, String uri) {
		return new NamespaceImpl(_documentFactory.createNamespace(prefix, uri));
	}

	@Override
	public ProcessingInstruction createProcessingInstruction(
		String target, Map<String, String> data) {

		org.dom4j.ProcessingInstruction processingInstruction =
			_documentFactory.createProcessingInstruction(target, data);

		if (processingInstruction == null) {
			return null;
		}

		return new ProcessingInstructionImpl(processingInstruction);
	}

	@Override
	public ProcessingInstruction createProcessingInstruction(
		String target, String data) {

		org.dom4j.ProcessingInstruction processingInstruction =
			_documentFactory.createProcessingInstruction(target, data);

		if (processingInstruction == null) {
			return null;
		}

		return new ProcessingInstructionImpl(processingInstruction);
	}

	@Override
	public QName createQName(String localName) {
		return new QNameImpl(_documentFactory.createQName(localName));
	}

	@Override
	public QName createQName(String localName, Namespace namespace) {
		NamespaceImpl namespaceImpl = (NamespaceImpl)namespace;

		return new QNameImpl(
			_documentFactory.createQName(
				localName, namespaceImpl.getWrappedNamespace()));
	}

	@Override
	public Text createText(String text) {
		return new TextImpl(_documentFactory.createText(text));
	}

	@Override
	public XPath createXPath(String xPathExpression) {
		return createXPath(xPathExpression, null);
	}

	@Override
	public XPath createXPath(
		String xPathExpression, Map<String, String> namespaceContextMap) {

		return new XPathImpl(
			_documentFactory.createXPath(xPathExpression), namespaceContextMap);
	}

	@Override
	public XPath createXPath(
		String xPathExpression, String prefix, String namespace) {

		return createXPath(
			xPathExpression,
			HashMapBuilder.put(
				prefix, namespace
			).build());
	}

	@Override
	public Document read(File file) throws DocumentException {
		return read(file, false);
	}

	@Override
	public Document read(File file, boolean validate) throws DocumentException {
		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				SAXReaderImpl.class.getClassLoader())) {

			org.dom4j.io.SAXReader saxReader = getSAXReader(validate);

			return new DocumentImpl(saxReader.read(file));
		}
		catch (org.dom4j.DocumentException documentException) {
			throw new DocumentException(
				documentException.getMessage(), documentException);
		}
	}

	@Override
	public Document read(InputStream inputStream) throws DocumentException {
		return read(inputStream, false);
	}

	@Override
	public Document read(InputStream inputStream, boolean validate)
		throws DocumentException {

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				SAXReaderImpl.class.getClassLoader())) {

			org.dom4j.io.SAXReader saxReader = getSAXReader(validate);

			return new DocumentImpl(saxReader.read(inputStream));
		}
		catch (org.dom4j.DocumentException documentException) {
			throw new DocumentException(
				documentException.getMessage(), documentException);
		}
	}

	@Override
	public Document read(Reader reader) throws DocumentException {
		return read(reader, false);
	}

	@Override
	public Document read(Reader reader, boolean validate)
		throws DocumentException {

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				SAXReaderImpl.class.getClassLoader())) {

			org.dom4j.io.SAXReader saxReader = getSAXReader(validate);

			return new DocumentImpl(saxReader.read(reader));
		}
		catch (org.dom4j.DocumentException documentException) {
			throw new DocumentException(
				documentException.getMessage(), documentException);
		}
	}

	@Override
	public Document read(String xml) throws DocumentException {
		return read(new XMLSafeReader(xml));
	}

	@Override
	public Document read(String xml, boolean validate)
		throws DocumentException {

		return read(new XMLSafeReader(xml), validate);
	}

	@Override
	public Document read(String xml, XMLSchema xmlSchema)
		throws DocumentException {

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				SAXReaderImpl.class.getClassLoader())) {

			org.dom4j.io.SAXReader saxReader = getSAXReader(xmlSchema);

			Reader reader = new XMLSafeReader(xml);

			return new DocumentImpl(saxReader.read(reader));
		}
		catch (org.dom4j.DocumentException documentException) {
			throw new DocumentException(
				documentException.getMessage(), documentException);
		}
	}

	@Override
	public Document read(URL url) throws DocumentException {
		return read(url, false);
	}

	@Override
	public Document read(URL url, boolean validate) throws DocumentException {
		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				SAXReaderImpl.class.getClassLoader())) {

			org.dom4j.io.SAXReader saxReader = getSAXReader(validate);

			return new DocumentImpl(saxReader.read(url));
		}
		catch (org.dom4j.DocumentException documentException) {
			throw new DocumentException(
				documentException.getMessage(), documentException);
		}
	}

	@Override
	public Document readURL(String url)
		throws DocumentException, MalformedURLException {

		return read(new URL(url), false);
	}

	@Override
	public Document readURL(String url, boolean validate)
		throws DocumentException, MalformedURLException {

		return read(new URL(url), validate);
	}

	@Override
	public List<Node> selectNodes(
		String xPathFilterExpression, List<Node> nodes) {

		org.dom4j.XPath xPath = _documentFactory.createXPath(
			xPathFilterExpression);

		return toNewNodes(xPath.selectNodes(toOldNodes(nodes)));
	}

	@Override
	public List<Node> selectNodes(String xPathFilterExpression, Node node) {
		NodeImpl nodeImpl = (NodeImpl)node;

		org.dom4j.XPath xPath = _documentFactory.createXPath(
			xPathFilterExpression);

		return toNewNodes(xPath.selectNodes(nodeImpl.getWrappedNode()));
	}

	public void setSecure(boolean secure) {
		_secure = secure;
	}

	public void setSecureXMLFactoryProvider(
		SecureXMLFactoryProvider secureXMLFactoryProvider) {

		_secureXMLFactoryProvider = secureXMLFactoryProvider;
	}

	@Override
	public void sort(List<Node> nodes, String xPathExpression) {
		org.dom4j.XPath xPath = _documentFactory.createXPath(xPathExpression);

		xPath.sort(toOldNodes(nodes));
	}

	@Override
	public void sort(
		List<Node> nodes, String xPathExpression, boolean distinct) {

		org.dom4j.XPath xPath = _documentFactory.createXPath(xPathExpression);

		xPath.sort(toOldNodes(nodes), distinct);
	}

	protected org.dom4j.io.SAXReader getSAXReader(boolean validate) {
		if (!PropsValues.XML_VALIDATION_ENABLED) {
			validate = false;
		}

		return SAXReaderFactory.getSAXReader(
			_secureXMLFactoryProvider.newXMLReader(), validate, _secure);
	}

	protected org.dom4j.io.SAXReader getSAXReader(XMLSchema xmlSchema) {
		boolean validate = true;

		if (!PropsValues.XML_VALIDATION_ENABLED) {
			validate = false;
		}

		return SAXReaderFactory.getSAXReader(
			_secureXMLFactoryProvider.newXMLReader(), xmlSchema, validate,
			_secure);
	}

	private final DocumentFactory _documentFactory =
		DocumentFactory.getInstance();
	private boolean _secure;
	private SecureXMLFactoryProvider _secureXMLFactoryProvider =
		new SecureXMLFactoryProviderImpl();

}