/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ant.bnd.service;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.FileResource;
import aQute.bnd.osgi.Instruction;
import aQute.bnd.osgi.Instructions;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Packages;
import aQute.bnd.osgi.Resource;
import aQute.bnd.service.AnalyzerPlugin;
import aQute.bnd.version.Version;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Andrea Di Giorgi
 * @author Gregory Amerson
 */
public class ServiceAnalyzerPlugin implements AnalyzerPlugin {

	@Override
	public boolean analyzeJar(Analyzer analyzer) throws Exception {
		boolean liferayService = Boolean.parseBoolean(
			analyzer.getProperty("Liferay-Service"));

		if (!liferayService) {
			return false;
		}

		processProvideCapability(analyzer);

		// Use the API JAR if available, otherwise fallback to Spring Extender
		// itself

		Jar jar = getClasspathJar(
			analyzer, "com.liferay.portal.spring.extender.api");

		if (jar == null) {
			jar = getClasspathJar(
				analyzer, "com.liferay.portal.spring.extender");
		}

		Packages referredPackages = analyzer.getReferred();

		Attrs springExtenderServiceAttrs = referredPackages.getByFQN(
			"com.liferay.portal.spring.extender.service");

		if ((jar == null) && (springExtenderServiceAttrs == null)) {
			return false;
		}

		if (jar != null) {
			processRequireCapability(
				analyzer, Version.parseVersion(jar.getVersion()));
		}

		processSpringContext(analyzer);
		processSpringDependency(analyzer);

		return false;
	}

	protected String getAttributeValue(Node node, String name) {
		NamedNodeMap namedNodeMap = node.getAttributes();

		Node attributeItem = namedNodeMap.getNamedItem(name);

		if (attributeItem != null) {
			return attributeItem.getNodeValue();
		}

		return null;
	}

	protected boolean getAttributeValue(
		Node node, String name, boolean defaultValue) {

		String value = getAttributeValue(node, name);

		if (value == null) {
			return defaultValue;
		}

		return Boolean.parseBoolean(value);
	}

	protected Jar getClasspathJar(Analyzer analyzer, String bundleSymbolicName)
		throws Exception {

		for (Jar jar : analyzer.getClasspath()) {
			if (bundleSymbolicName.equals(jar.getBsn())) {
				return jar;
			}
		}

		return null;
	}

	protected boolean isValidPackagePath(String packagePath) {
		if ((packagePath != null) && (packagePath.indexOf('@') == -1)) {
			return true;
		}

		return false;
	}

	protected void merge(Analyzer analyzer, String key, String value) {
		Parameters parameters = new Parameters(analyzer.getProperty(key));

		parameters.mergeWith(new Parameters(value), false);

		analyzer.setProperty(key, parameters.toString());
	}

	protected void populateServiceClassNames(
			Set<String> serviceClassNames, DocumentBuilder documentBuilder,
			XPath xPath, File serviceXmlFile)
		throws Exception {

		Document document = documentBuilder.parse(serviceXmlFile);

		XPathExpression apiPackagePathXPathExpression = xPath.compile(
			"/service-builder/@api-package-path");

		String packagePath = apiPackagePathXPathExpression.evaluate(document);

		if ((packagePath == null) || packagePath.isEmpty()) {
			XPathExpression packagePathXPathExpression = xPath.compile(
				"/service-builder/@package-path");

			packagePath = packagePathXPathExpression.evaluate(document);
		}

		if (!isValidPackagePath(packagePath)) {
			return;
		}

		XPathExpression entityXPathExpression = xPath.compile("//entity");

		NodeList entityNodeList = (NodeList)entityXPathExpression.evaluate(
			document, XPathConstants.NODESET);

		for (int i = 0; i < entityNodeList.getLength(); i++) {
			Node entityNode = entityNodeList.item(i);

			String name = getAttributeValue(entityNode, "name");
			boolean localService = getAttributeValue(
				entityNode, "local-service", false);
			boolean remoteService = getAttributeValue(
				entityNode, "remote-service", true);

			if (localService) {
				serviceClassNames.add(
					packagePath + ".service." + name + "LocalService");
			}

			if (!remoteService) {
				continue;
			}

			serviceClassNames.add(packagePath + ".service." + name + "Service");
		}
	}

	protected void processProvideCapability(Analyzer analyzer)
		throws Exception {

		Parameters parameters = OSGiHeader.parseHeader(
			analyzer.getProperty("-liferay-service-xml"));

		if (parameters.isEmpty()) {
			return;
		}

		Set<File> serviceXmlFiles = new HashSet<>();

		Jar jar = analyzer.getJar();

		Instructions instructions = new Instructions(parameters);

		Map<String, Resource> resources = jar.getResources();

		for (Map.Entry<String, Resource> entry : resources.entrySet()) {
			String key = entry.getKey();
			Resource resource = entry.getValue();

			for (Instruction instruction : instructions.keySet()) {
				if (!instruction.matches(key)) {
					continue;
				}

				if (resource instanceof FileResource) {
					FileResource fileResource = (FileResource)resource;

					File serviceXmlFile = fileResource.getFile();

					serviceXmlFiles.add(serviceXmlFile);
				}
				else {
					Path path = Files.createTempFile("service", "xml");

					Files.copy(
						resource.openInputStream(), path,
						StandardCopyOption.REPLACE_EXISTING);

					File serviceXmlFile = path.toFile();

					serviceXmlFiles.add(serviceXmlFile);

					serviceXmlFile.deleteOnExit();
				}
			}
		}

		if (serviceXmlFiles.isEmpty()) {
			analyzer.warning(
				"This bundle contains Liferay services but does not include " +
					"a service.xml in the final jar. No OSGi service " +
						"capabilities for these will be emitted.");

			return;
		}

		Set<String> serviceClassNames = new TreeSet<>();

		DocumentBuilderFactory documentBuilderFactory =
			DocumentBuilderFactory.newInstance();

		documentBuilderFactory.setFeature(_LOAD_EXTERNAL_DTD, false);

		DocumentBuilder documentBuilder =
			documentBuilderFactory.newDocumentBuilder();

		XPathFactory xPathFactory = XPathFactory.newInstance();

		XPath xPath = xPathFactory.newXPath();

		for (File serviceXmlFile : serviceXmlFiles) {
			populateServiceClassNames(
				serviceClassNames, documentBuilder, xPath, serviceXmlFile);

			documentBuilder.reset();
			xPath.reset();
		}

		if (!serviceClassNames.isEmpty()) {
			Parameters provideCapabilityHeaders = new Parameters(
				analyzer.getProperty(Constants.PROVIDE_CAPABILITY));

			for (String serviceClassName : serviceClassNames) {
				provideCapabilityHeaders.add(
					"osgi.service",
					Attrs.create("objectClass:List<String>", serviceClassName));
			}

			analyzer.setProperty(
				Constants.PROVIDE_CAPABILITY,
				provideCapabilityHeaders.toString());
		}
	}

	protected void processRequireCapability(
			Analyzer analyzer, Version portalSpringExtenderVersion)
		throws Exception {

		Parameters requireCapabilityHeaders = new Parameters(
			analyzer.getProperty(Constants.REQUIRE_CAPABILITY));

		Parameters parameters = new Parameters();

		Attrs attrs = new Attrs();

		StringBuilder sb = new StringBuilder();

		sb.append("(&(");
		sb.append(_LIFERAY_EXTENDER);
		sb.append("=spring.extender)(version>=");
		sb.append(portalSpringExtenderVersion.getMajor());
		sb.append(".0)(!(version>=");
		sb.append(portalSpringExtenderVersion.getMajor() + 1);
		sb.append(".0)))");

		attrs.put(Constants.FILTER_DIRECTIVE, sb.toString());

		parameters.add(_LIFERAY_EXTENDER, attrs);

		requireCapabilityHeaders.mergeWith(parameters, false);

		analyzer.setProperty(
			Constants.REQUIRE_CAPABILITY, requireCapabilityHeaders.toString());
	}

	protected void processSpringContext(Analyzer analyzer) {
		Jar jar = analyzer.getJar();

		Map<String, Map<String, Resource>> directories = jar.getDirectories();

		if (!directories.containsKey("META-INF/spring")) {
			return;
		}

		merge(analyzer, "Liferay-Spring-Context", "META-INF/spring");
	}

	protected void processSpringDependency(Analyzer analyzer) {
		merge(
			analyzer, "-liferay-spring-dependency",
			"com.liferay.portal.spring.extender.service.ServiceReference");
	}

	private static final String _LIFERAY_EXTENDER = "liferay.extender";

	private static final String _LOAD_EXTERNAL_DTD =
		"http://apache.org/xml/features/nonvalidating/load-external-dtd";

}