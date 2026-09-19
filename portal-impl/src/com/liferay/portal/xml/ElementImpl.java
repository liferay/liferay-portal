/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.xml;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Attribute;
import com.liferay.portal.kernel.xml.CDATA;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Entity;
import com.liferay.portal.kernel.xml.Namespace;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.QName;
import com.liferay.portal.kernel.xml.Text;
import com.liferay.portal.kernel.xml.Visitor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Brian Wing Shun Chan
 */
public class ElementImpl extends BranchImpl implements Element {

	public ElementImpl(org.dom4j.Element element) {
		super(element);

		_element = element;
	}

	@Override
	public <T, V extends Visitor<T>> T accept(V visitor) {
		return visitor.visitElement(this);
	}

	@Override
	public void add(Attribute attribute) {
		AttributeImpl attributeImpl = (AttributeImpl)attribute;

		_element.add(attributeImpl.getWrappedAttribute());
	}

	@Override
	public void add(CDATA cdata) {
		CDATAImpl cdataImpl = (CDATAImpl)cdata;

		_element.add(cdataImpl.getWrappedCDATA());
	}

	@Override
	public void add(Entity entity) {
		EntityImpl entityImpl = (EntityImpl)entity;

		_element.add(entityImpl.getWrappedEntity());
	}

	@Override
	public void add(Namespace namespace) {
		NamespaceImpl namespaceImpl = (NamespaceImpl)namespace;

		_element.add(namespaceImpl.getWrappedNamespace());
	}

	@Override
	public void add(Text text) {
		TextImpl textImpl = (TextImpl)text;

		_element.add(textImpl.getWrappedText());
	}

	@Override
	public Element addAttribute(QName qName, String value) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		return new ElementImpl(
			_element.addAttribute(qNameImpl.getWrappedQName(), value));
	}

	@Override
	public Element addAttribute(String name, String value) {
		return new ElementImpl(_element.addAttribute(name, value));
	}

	@Override
	public Element addCDATA(String cdata) {
		cdata = StringUtil.replace(cdata, "]]>", "]]]]><![CDATA[>");

		return new ElementImpl(_element.addCDATA(cdata));
	}

	@Override
	public Element addComment(String comment) {
		return new ElementImpl(_element.addComment(comment));
	}

	@Override
	public Element addEntity(String name, String text) {
		return new ElementImpl(_element.addEntity(name, text));
	}

	@Override
	public Element addNamespace(String prefix, String uri) {
		return new ElementImpl(_element.addNamespace(prefix, uri));
	}

	@Override
	public Element addProcessingInstruction(
		String target, Map<String, String> data) {

		return new ElementImpl(_element.addProcessingInstruction(target, data));
	}

	@Override
	public Element addProcessingInstruction(String target, String data) {
		return new ElementImpl(_element.addProcessingInstruction(target, data));
	}

	@Override
	public Element addText(String text) {
		return new ElementImpl(_element.addText(text));
	}

	@Override
	public List<Namespace> additionalNamespaces() {
		return SAXReaderImpl.toNewNamespaces(_element.additionalNamespaces());
	}

	@Override
	public void appendAttributes(Element element) {
		ElementImpl elementImpl = (ElementImpl)element;

		_element.appendAttributes(elementImpl.getWrappedElement());
	}

	@Override
	public Attribute attribute(int index) {
		org.dom4j.Attribute attribute = _element.attribute(index);

		if (attribute == null) {
			return null;
		}

		return new AttributeImpl(attribute);
	}

	@Override
	public Attribute attribute(QName qName) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		org.dom4j.Attribute attribute = _element.attribute(
			qNameImpl.getWrappedQName());

		if (attribute == null) {
			return null;
		}

		return new AttributeImpl(attribute);
	}

	@Override
	public Attribute attribute(String name) {
		org.dom4j.Attribute attribute = _element.attribute(name);

		if (attribute == null) {
			return null;
		}

		return new AttributeImpl(attribute);
	}

	@Override
	public int attributeCount() {
		return _element.attributeCount();
	}

	@Override
	public Iterator<Attribute> attributeIterator() {
		return attributes().iterator();
	}

	@Override
	public String attributeValue(QName qName) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		return _element.attributeValue(qNameImpl.getWrappedQName());
	}

	@Override
	public String attributeValue(QName qName, String defaultValue) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		return _element.attributeValue(
			qNameImpl.getWrappedQName(), defaultValue);
	}

	@Override
	public String attributeValue(String name) {
		return _element.attributeValue(name);
	}

	@Override
	public String attributeValue(String name, String defaultValue) {
		return _element.attributeValue(name, defaultValue);
	}

	@Override
	public List<Attribute> attributes() {
		return SAXReaderImpl.toNewAttributes(_element.attributes());
	}

	@Override
	public Element createCopy() {
		return new ElementImpl(_element.createCopy());
	}

	@Override
	public Element createCopy(QName qName) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		return new ElementImpl(
			_element.createCopy(qNameImpl.getWrappedQName()));
	}

	@Override
	public Element createCopy(String name) {
		return new ElementImpl(_element.createCopy(name));
	}

	@Override
	public List<Namespace> declaredNamespaces() {
		return SAXReaderImpl.toNewNamespaces(_element.declaredNamespaces());
	}

	@Override
	public Element element(QName qName) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		org.dom4j.Element element = _element.element(
			qNameImpl.getWrappedQName());

		if (element == null) {
			return null;
		}

		return new ElementImpl(element);
	}

	@Override
	public Element element(String name) {
		org.dom4j.Element element = _element.element(name);

		if (element == null) {
			return null;
		}

		return new ElementImpl(element);
	}

	@Override
	public Iterator<Element> elementIterator() {
		return elements().iterator();
	}

	@Override
	public Iterator<Element> elementIterator(QName qName) {
		List<Element> elementList = elements(qName);

		return elementList.iterator();
	}

	@Override
	public Iterator<Element> elementIterator(String name) {
		List<Element> elementList = elements(name);

		return elementList.iterator();
	}

	@Override
	public String elementText(QName qName) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		return _element.elementText(qNameImpl.getWrappedQName());
	}

	@Override
	public String elementText(String name) {
		return _element.elementText(name);
	}

	@Override
	public String elementTextTrim(QName qName) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		return _element.elementTextTrim(qNameImpl.getWrappedQName());
	}

	@Override
	public String elementTextTrim(String name) {
		return _element.elementTextTrim(name);
	}

	@Override
	public List<Element> elements() {
		return SAXReaderImpl.toNewElements(_element.elements());
	}

	@Override
	public List<Element> elements(QName qName) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		return SAXReaderImpl.toNewElements(
			_element.elements(qNameImpl.getWrappedQName()));
	}

	@Override
	public List<Element> elements(String name) {
		return SAXReaderImpl.toNewElements(_element.elements(name));
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (object instanceof NodeImpl) {
			NodeImpl nodeImpl = (NodeImpl)object;

			if (nodeImpl.getWrappedNode() instanceof org.dom4j.Element) {
				object = new ElementImpl(
					(org.dom4j.Element)nodeImpl.getWrappedNode());
			}
			else {
				return false;
			}
		}
		else if (!(object instanceof ElementImpl)) {
			return false;
		}

		ElementImpl elementImpl = (ElementImpl)object;

		org.dom4j.Element element = elementImpl.getWrappedElement();

		return _element.equals(element);
	}

	@Override
	public Object getData() {
		return _element.getData();
	}

	@Override
	public Namespace getNamespace() {
		org.dom4j.Namespace namespace = _element.getNamespace();

		if (namespace == null) {
			return null;
		}

		return new NamespaceImpl(namespace);
	}

	@Override
	public Namespace getNamespaceForPrefix(String prefix) {
		org.dom4j.Namespace namespace = _element.getNamespaceForPrefix(prefix);

		if (namespace == null) {
			return null;
		}

		return new NamespaceImpl(namespace);
	}

	@Override
	public Namespace getNamespaceForURI(String uri) {
		org.dom4j.Namespace namespace = _element.getNamespaceForURI(uri);

		if (namespace == null) {
			return null;
		}

		return new NamespaceImpl(namespace);
	}

	@Override
	public String getNamespacePrefix() {
		return _element.getNamespacePrefix();
	}

	@Override
	public String getNamespaceURI() {
		return _element.getNamespaceURI();
	}

	@Override
	public List<Namespace> getNamespacesForURI(String uri) {
		return SAXReaderImpl.toNewNamespaces(_element.getNamespacesForURI(uri));
	}

	@Override
	public QName getQName() {
		org.dom4j.QName qName = _element.getQName();

		if (qName == null) {
			return null;
		}

		return new QNameImpl(qName);
	}

	@Override
	public QName getQName(String qualifiedName) {
		org.dom4j.QName qName = _element.getQName(qualifiedName);

		if (qName == null) {
			return null;
		}

		return new QNameImpl(qName);
	}

	@Override
	public String getQualifiedName() {
		return _element.getQualifiedName();
	}

	@Override
	public String getTextTrim() {
		return _element.getTextTrim();
	}

	public org.dom4j.Element getWrappedElement() {
		return _element;
	}

	@Override
	public Node getXPathResult(int index) {
		org.dom4j.Node node = _element.getXPathResult(index);

		if (node == null) {
			return null;
		}

		return new NodeImpl(node);
	}

	@Override
	public boolean hasMixedContent() {
		return _element.hasMixedContent();
	}

	@Override
	public int hashCode() {
		return _element.hashCode();
	}

	@Override
	public boolean isRootElement() {
		return _element.isRootElement();
	}

	@Override
	public boolean isTextOnly() {
		return _element.isTextOnly();
	}

	@Override
	public boolean remove(Attribute attribute) {
		AttributeImpl attributeImpl = (AttributeImpl)attribute;

		return _element.remove(attributeImpl.getWrappedAttribute());
	}

	@Override
	public boolean remove(CDATA cdata) {
		CDATAImpl cdataImpl = (CDATAImpl)cdata;

		return _element.remove(cdataImpl.getWrappedCDATA());
	}

	@Override
	public boolean remove(Entity entity) {
		EntityImpl entityImpl = (EntityImpl)entity;

		return _element.remove(entityImpl.getWrappedEntity());
	}

	@Override
	public boolean remove(Namespace namespace) {
		NamespaceImpl namespaceImpl = (NamespaceImpl)namespace;

		return _element.remove(namespaceImpl.getWrappedNamespace());
	}

	@Override
	public boolean remove(Text text) {
		TextImpl textImpl = (TextImpl)text;

		return _element.remove(textImpl.getWrappedText());
	}

	@Override
	public void setAttributes(List<Attribute> attributes) {
		_element.setAttributes(SAXReaderImpl.toOldAttributes(attributes));
	}

	@Override
	public void setData(Object data) {
		_element.setData(data);
	}

	@Override
	public void setQName(QName qName) {
		QNameImpl qNameImpl = (QNameImpl)qName;

		_element.setQName(qNameImpl.getWrappedQName());
	}

	@Override
	public void sortAttributes(boolean recursive) {
		Map<String, Attribute> attributesMap = new TreeMap<>();

		List<Attribute> attributes = attributes();

		for (Attribute attribute : attributes) {
			attribute.detach();

			attributesMap.put(attribute.getName(), attribute);
		}

		for (Map.Entry<String, Attribute> entry : attributesMap.entrySet()) {
			Attribute attribute = entry.getValue();

			add(attribute);
		}

		if (!recursive) {
			return;
		}

		List<Element> elements = elements();

		for (Element element : elements) {
			element.sortAttributes(true);
		}
	}

	@Override
	public void sortElementsByAttribute(
		String elementName, String attributeName) {

		Map<String, Element> elementsMap = new TreeMap<>();

		List<Element> elements = elements();

		for (Element element : elements) {
			element.detach();

			if (elementName.equals(element.getName())) {
				String attributeValue = element.attributeValue(attributeName);

				elementsMap.put(attributeValue, element);
			}
		}

		for (Element element : elements) {
			if (elementName.equals(element.getName())) {
				break;
			}

			add(element);
		}

		for (Map.Entry<String, Element> entry : elementsMap.entrySet()) {
			Element element = entry.getValue();

			add(element);
		}

		boolean foundLastElementWithElementName = false;

		for (int i = 0; i < elements.size(); i++) {
			Element element = elements.get(i);

			if (!foundLastElementWithElementName) {
				if (elementName.equals(element.getName()) &&
					((i + 1) < elements.size())) {

					Element nextElement = elements.get(i + 1);

					if (!elementName.equals(nextElement.getName())) {
						foundLastElementWithElementName = true;
					}
				}
			}
			else {
				add(element);
			}
		}
	}

	@Override
	public void sortElementsByChildElement(
		String elementName, String childElementName) {

		Map<String, Element> elementsMap = new TreeMap<>();

		List<Element> elements = elements();

		for (Element element : elements) {
			element.detach();

			if (elementName.equals(element.getName())) {
				String childElementValue = element.elementText(
					childElementName);

				elementsMap.put(childElementValue, element);
			}
		}

		for (Element element : elements) {
			if (elementName.equals(element.getName())) {
				break;
			}

			add(element);
		}

		for (Map.Entry<String, Element> entry : elementsMap.entrySet()) {
			Element element = entry.getValue();

			add(element);
		}

		boolean foundLastElementWithElementName = false;

		for (int i = 0; i < elements.size(); i++) {
			Element element = elements.get(i);

			if (!foundLastElementWithElementName) {
				if (elementName.equals(element.getName()) &&
					((i + 1) < elements.size())) {

					Element nextElement = elements.get(i + 1);

					if (!elementName.equals(nextElement.getName())) {
						foundLastElementWithElementName = true;
					}
				}
			}
			else {
				add(element);
			}
		}
	}

	@Override
	public String toString() {
		return _element.toString();
	}

	private final org.dom4j.Element _element;

}