/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.deploy;

import com.liferay.portal.kernel.deploy.auto.AutoDeployer;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.util.FileImpl;
import com.liferay.portal.xml.SAXReaderImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/**
 * @author Igor Beslic
 */
public abstract class BaseAutoDeployerTestCase {

	public abstract AutoDeployer getAutoDeployer();

	@Before
	public void setUp() throws Exception {
		FileUtil fileUtil = new FileUtil();

		fileUtil.setFile(new FileImpl());

		SAXReaderUtil saxReaderUtil = new SAXReaderUtil();

		SAXReaderImpl secureSAXReaderImpl = new SAXReaderImpl();

		secureSAXReaderImpl.setSecure(true);

		saxReaderUtil.setSAXReader(secureSAXReaderImpl);

		UnsecureSAXReaderUtil unsecureSAXReaderUtil =
			new UnsecureSAXReaderUtil();

		unsecureSAXReaderUtil.setSAXReader(new SAXReaderImpl());

		setUpLiferayPluginPackageProperties();
	}

	@After
	public void tearDown() throws Exception {
		FileUtil.deltree(getRootDir());
	}

	protected Properties getLiferayPluginPackageProperties()
		throws IOException {

		InputStream inputStream = new FileInputStream(
			new File(getWebInfDir(), "liferay-plugin-package.properties"));

		Properties properties = PropertiesUtil.load(
			StringUtil.read(inputStream));

		Assert.assertFalse(properties.isEmpty());

		return properties;
	}

	protected File getRootDir() {
		if (_rootDir == null) {
			_rootDir = new File(
				SystemProperties.get(SystemProperties.TMP_DIR),
				StringUtil.randomId());
		}

		return _rootDir;
	}

	protected File getWebInfDir() {
		return new File(getRootDir(), "WEB-INF");
	}

	protected void setUpLiferayPluginPackageProperties() throws Exception {
		try (InputStream inputStream =
				BaseAutoDeployerTestCase.class.getResourceAsStream(
					"dependencies/liferay-plugin-package.properties")) {

			File webInfDir = getWebInfDir();

			webInfDir.mkdirs();

			try (OutputStream outputStream = new FileOutputStream(
					new File(webInfDir, "liferay-plugin-package.properties"))) {

				StreamUtil.transfer(inputStream, outputStream);
			}
		}
	}

	protected void validateLiferayPluginPackageXMLFile(File xmlFile)
		throws Exception {

		Assert.assertTrue(xmlFile.exists());

		String liferayPluginPackageXML = FileUtil.read(xmlFile);

		Assert.assertNotNull(liferayPluginPackageXML);

		Document document = UnsecureSAXReaderUtil.read(
			liferayPluginPackageXML, true);

		Element rootElement = document.getRootElement();

		Element element = rootElement.element("name");

		Assert.assertNotNull(element);

		element = rootElement.element("tags");

		Assert.assertNotNull(element);
		Assert.assertNotNull(element.getTextTrim());

		element = rootElement.element("short-description");

		Assert.assertNotNull(element);
		Assert.assertEquals("Test", element.getTextTrim());

		element = rootElement.element("page-url");

		Assert.assertNotNull(element);
		Assert.assertNotNull(element.getTextTrim());

		element = rootElement.element("author");

		Assert.assertNotNull(element);
		Assert.assertNotNull(element.getTextTrim());

		element = rootElement.element("liferay-versions");

		Assert.assertNotNull(element);
		Assert.assertNotNull(element.getTextTrim());
	}

	private File _rootDir;

}