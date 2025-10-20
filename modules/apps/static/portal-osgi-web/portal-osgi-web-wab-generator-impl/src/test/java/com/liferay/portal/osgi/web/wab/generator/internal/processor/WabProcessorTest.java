/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.wab.generator.internal.processor;

import aQute.bnd.component.DSAnnotations;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.make.component.ServiceComponent;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.Domain;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.bnd.version.Version;

import aQute.lib.filter.Filter;

import com.liferay.ant.bnd.jsp.JspAnalyzerPlugin;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.deploy.auto.context.AutoDeploymentContext;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.security.xml.SecureXMLFactoryProviderImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.FastDateFormatFactoryImpl;
import com.liferay.portal.xml.SAXReaderImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import java.net.URISyntaxException;
import java.net.URL;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Raymond Augé
 */
public class WabProcessorTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			new FastDateFormatFactoryImpl());

		SAXReaderUtil saxReaderUtil = new SAXReaderUtil();

		SAXReaderImpl secureSAXReaderImpl = new SAXReaderImpl();

		secureSAXReaderImpl.setSecure(true);

		saxReaderUtil.setSAXReader(secureSAXReaderImpl);

		SecureXMLFactoryProviderUtil secureXMLFactoryProviderUtil =
			new SecureXMLFactoryProviderUtil();

		secureXMLFactoryProviderUtil.setSecureXMLFactoryProvider(
			new SecureXMLFactoryProviderImpl());

		UnsecureSAXReaderUtil unsecureSAXReaderUtil =
			new UnsecureSAXReaderUtil();

		unsecureSAXReaderUtil.setSAXReader(new SAXReaderImpl());
	}

	@Test
	public void testClassicThemeWab() throws Exception {
		File file = getFile("dependencies/classic-theme.autodeployed.war");

		try (Jar jar = new Jar(file)) {
			Assert.assertNull(jar.getBsn());

			Map<String, Resource> resources = jar.getResources();

			Assert.assertEquals(resources.toString(), 1244, resources.size());
		}

		WabProcessor wabProcessor = new TestWabProcessor(
			file,
			HashMapBuilder.put(
				"Bundle-Version", new String[] {"7.0.0.8"}
			).put(
				"Web-ContextPath", new String[] {"/classic-theme"}
			).build());

		File processedFile = wabProcessor.getProcessedFile();

		Assert.assertNotNull(processedFile);

		try (Jar jar = new Jar(processedFile)) {
			Map<String, Map<String, Resource>> directories =
				jar.getDirectories();

			Map<String, Resource> resources = jar.getResources();

			// Check to see that the right number of resources are in the WAB

			Assert.assertEquals(resources.toString(), 1240, resources.size());

			// Check if the basic metadata is correct

			Assert.assertEquals("classic-theme", jar.getBsn());
			Assert.assertEquals("7.0.0.8", jar.getVersion());

			// Assert that the Bundle-ClassPath is properly formed to our
			// conventions

			Domain domain = Domain.domain(jar.getManifest());

			Parameters bundleClassPath = domain.getBundleClassPath();

			Assert.assertEquals(4, bundleClassPath.size());
			Assert.assertTrue(
				bundleClassPath.containsKey("ext/WEB-INF/classes"));

			for (String bundleClassPathEntry : bundleClassPath.keySet()) {
				if (bundleClassPathEntry.equals("ext/WEB-INF/classes")) {
					Assert.assertNull(resources.get(bundleClassPathEntry));
				}
				else if (bundleClassPathEntry.equals("WEB-INF/classes")) {
					Assert.assertNull(resources.get(bundleClassPathEntry));
					Assert.assertTrue(
						directories.containsKey(bundleClassPathEntry));
				}
				else {

					// Check that all the libraries on the Bundle-ClassPath
					// exist in the WAB

					Assert.assertNotNull(resources.get(bundleClassPathEntry));
				}
			}

			Parameters importedPackages = domain.getImportPackage();

			// Check basic servlet and jsp packages are imported

			Assert.assertTrue(importedPackages.containsKey("javax.servlet"));
			Assert.assertTrue(
				importedPackages.containsKey("javax.servlet.http"));

			// Check if packages declared in portal property
			// module.framework.web.generator.default.servlet.packages are
			// included

			Assert.assertTrue(
				importedPackages.containsKey("com.liferay.portal.model"));
			Assert.assertTrue(
				importedPackages.containsKey("com.liferay.portal.service"));
			Assert.assertTrue(
				importedPackages.containsKey(
					"com.liferay.portal.servlet.filters.aggregate"));
			Assert.assertTrue(
				importedPackages.containsKey(
					"com.liferay.portal.osgi.web.servlet.jsp.compiler"));
			Assert.assertTrue(
				importedPackages.containsKey(
					"com.liferay.portal.spring.context"));
			Assert.assertTrue(
				importedPackages.containsKey("com.liferay.portal.util"));
			Assert.assertTrue(
				importedPackages.containsKey("com.liferay.portlet"));
			Assert.assertTrue(importedPackages.containsKey("com.sun.el"));
			Assert.assertTrue(
				importedPackages.containsKey("org.apache.naming.java"));

			// Check if packages only referenced in web.xml are imported

			Assert.assertTrue(
				importedPackages.containsKey(
					"com.liferay.portal.kernel.servlet.filters.invoker"));
			Assert.assertTrue(
				importedPackages.containsKey("com.liferay.portal.webserver"));
		}
	}

	@Test
	public void testCustomizedPlugins() throws Exception {
		File file = FileUtil.createTempFile("war");

		try (JarOutputStream jarOutputStream = new JarOutputStream(
				new FileOutputStream(file))) {

			Manifest manifest = new Manifest();

			Attributes attributes = manifest.getMainAttributes();

			attributes.putValue("Manifest-Version", "1.0");

			jarOutputStream.putNextEntry(new ZipEntry(JarFile.MANIFEST_NAME));

			manifest.write(jarOutputStream);

			jarOutputStream.closeEntry();

			jarOutputStream.putNextEntry(new ZipEntry("WEB-INF/beans.xml"));
			jarOutputStream.write(
				"<?xml version=\"1.0\" ?><beans/>".getBytes());

			jarOutputStream.closeEntry();

			jarOutputStream.finish();
		}

		WabProcessor wabProcessor = new TestWabProcessor(
			file,
			Collections.singletonMap(
				"Web-ContextPath", new String[] {"/test-plugins"}));

		Logger logger = LoggerFactory.getLogger("aQute.bnd.osgi.Processor");

		int originalCurrentLogLevel = ReflectionTestUtil.getAndSetFieldValue(
			logger, "currentLogLevel", 10);

		PrintStream originalErr = System.err;

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		System.setErr(new PrintStream(unsyncByteArrayOutputStream));

		try {
			wabProcessor.getProcessedFile();

			String message = unsyncByteArrayOutputStream.toString();

			Assert.assertFalse(
				message, message.contains(DSAnnotations.class.getSimpleName()));
			Assert.assertFalse(
				message,
				message.contains(ServiceComponent.class.getSimpleName()));
			Assert.assertTrue(
				message,
				message.contains(JspAnalyzerPlugin.class.getSimpleName()));
			Assert.assertTrue(
				message,
				message.contains(WabProcessor.class.getSimpleName() + "$2"));
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				logger, "currentLogLevel", originalCurrentLogLevel);

			System.setErr(originalErr);
		}
	}

	@Test
	public void testFatCDIWabOptsOutOfOSGiCDIIntegration() throws Exception {
		WabProcessor wabProcessor = new TestWabProcessor(
			getFile("dependencies/jsf.cdi.applicant.portlet.war"),
			Collections.singletonMap(
				"Web-ContextPath",
				new String[] {"/jsf-cdi-applicant-portlet"}));

		File processedFile = wabProcessor.getProcessedFile();

		Assert.assertNotNull(processedFile);

		try (Jar jar = new Jar(processedFile)) {

			// Check if the basic metadata is correct

			Assert.assertEquals("jsf-cdi-applicant-portlet", jar.getBsn());
			Assert.assertEquals("4.1.2", jar.getVersion());

			// Does this WAR have a beans.xml file that would trigger
			// OSGi CDI Integration analysis?

			Resource beansXMLFile = jar.getResource("WEB-INF/beans.xml");

			Assert.assertNotNull(beansXMLFile);

			// Did the beans.xml file have a discovery mode of none?

			try (InputStream inputStream = beansXMLFile.openInputStream()) {
				Document document = SAXReaderUtil.read(inputStream);

				Node beanDiscoveryMode = document.selectSingleNode(
					"/beans/@bean-discovery-mode");

				String value = beanDiscoveryMode.getStringValue();

				Assert.assertNotEquals("none", value);
			}

			// Now that we've established CDI discovery would kick
			// in, check to see if the WAB opted-out of integration by
			// having the "-cdiannotations" instruction set to the empty
			// value in liferay-plugin-package.properties.

			Resource packageProperties = jar.getResource(
				"WEB-INF/liferay-plugin-package.properties");

			Properties properties = new Properties();

			try (InputStream inputStream =
					packageProperties.openInputStream()) {

				properties.load(inputStream);
			}

			Assert.assertEquals(
				"", properties.getProperty(Constants.CDIANNOTATIONS));

			// Finally, make sure no requirement on the OSGi CDI
			// Integration extender was added to the manifest

			Domain domain = Domain.domain(jar.getManifest());

			Parameters requirements = domain.getRequireCapability();

			Map<String, Object> arguments = HashMapBuilder.<String, Object>put(
				"osgi.extender", "osgi.cdi"
			).put(
				"version", new Version(1)
			).build();

			for (Attrs attrs : requirements.values()) {
				String filterString = attrs.get("filter:");

				if (filterString == null) {
					continue;
				}

				Filter filter = new Filter(filterString);

				Assert.assertFalse(filter.matchMap(arguments));
			}
		}
	}

	@Test
	public void testSkinnyCDIWabGainsOSGiCDIIntegration() throws Exception {
		WabProcessor wabProcessor = new TestWabProcessor(
			getFile("dependencies/PortletV3AnnotatedDemo.war"),
			Collections.singletonMap(
				"Web-ContextPath",
				new String[] {"/portlet-V3-annotated-demo"}));

		File processedFile = wabProcessor.getProcessedFile();

		Assert.assertNotNull(processedFile);

		try (Jar jar = new Jar(processedFile)) {

			// Check if the basic metadata is correct

			Assert.assertEquals("portlet-V3-annotated-demo", jar.getBsn());
			Assert.assertEquals("1.0.0", jar.getVersion());

			// Does this WAR have a beans.xml file that would trigger
			// OSGi CDI Integration analysis?

			Resource beansXMLFile = jar.getResource("WEB-INF/beans.xml");

			Assert.assertNotNull(beansXMLFile);

			// Did the beans.xml file have a discovery mode of none?

			try (InputStream inputStream = beansXMLFile.openInputStream()) {
				Document document = SAXReaderUtil.read(inputStream);

				Node beanDiscoveryMode = document.selectSingleNode(
					"/beans/@bean-discovery-mode");

				String value = beanDiscoveryMode.getStringValue();

				Assert.assertNotEquals("none", value);
			}

			// Now that we've established CDI discovery would kick
			// in, check to see if the WAB opted-out of integration by
			// having the "-cdiannotations" instruction set to the empty
			// value in liferay-plugin-package.properties.

			Resource packageProperties = jar.getResource(
				"WEB-INF/liferay-plugin-package.properties");

			Properties properties = new Properties();

			try (InputStream inputStream =
					packageProperties.openInputStream()) {

				properties.load(inputStream);
			}

			Assert.assertFalse(
				properties.containsKey(Constants.CDIANNOTATIONS));

			// Finally, make sure the requirement on the OSGi CDI
			// Integration extender was added to the manifest

			Domain domain = Domain.domain(jar.getManifest());

			Parameters requirements = domain.getRequireCapability();

			Map.Entry<String, Attrs> entry = _findRequirement(
				requirements, "osgi.extender",
				HashMapBuilder.<String, Object>put(
					"osgi.extender", "osgi.cdi"
				).put(
					"version", new Version(1)
				).build());

			Assert.assertNotNull(entry);

			// Assert the expected number of beans were discovered

			Attrs attrs = entry.getValue();

			List<String> beans = attrs.getTyped(Attrs.LIST_STRING, "beans");

			Assert.assertEquals(beans.toString(), 23, beans.size());

			// Make sure other CDI requirements were added

			// The bean portlet extension

			Assert.assertNotNull(
				_findRequirement(
					requirements, "osgi.cdi.extension",
					Collections.singletonMap(
						"osgi.cdi.extension",
						"com.liferay.bean.portlet.cdi.extension")));

			// The http extension

			Assert.assertNotNull(
				_findRequirement(
					requirements, "osgi.cdi.extension",
					Collections.singletonMap(
						"osgi.cdi.extension", "aries.cdi.http")));

			// The EL extension

			Assert.assertNotNull(
				_findRequirement(
					requirements, "osgi.cdi.extension",
					Collections.singletonMap(
						"osgi.cdi.extension", "aries.cdi.el.jsp")));
		}
	}

	@Test
	public void testThatEmbeddedLibsAreHandledProperly() throws Exception {
		WabProcessor wabProcessor = new TestWabProcessor(
			getFile("dependencies/tck-V3URLTests.wab.war"),
			Collections.singletonMap(
				"Web-ContextPath",
				new String[] {"/portlet-V3-annotated-demo"}));

		File processedFile = wabProcessor.getProcessedFile();

		Assert.assertNotNull(processedFile);

		try (Jar jar = new Jar(processedFile)) {

			// Check if the basic metadata is correct

			Assert.assertEquals("portlet-V3-annotated-demo", jar.getBsn());
			Assert.assertEquals("1.0.0", jar.getVersion());

			// Does this WAR have a beans.xml file that would trigger
			// OSGi CDI Integration analysis?

			Resource beansXMLFile = jar.getResource("WEB-INF/beans.xml");

			Assert.assertNotNull(beansXMLFile);

			// Did the beans.xml file have a discovery mode of none?

			try (InputStream inputStream = beansXMLFile.openInputStream()) {
				Document document = SAXReaderUtil.read(inputStream);

				Node beanDiscoveryMode = document.selectSingleNode(
					"/beans/@bean-discovery-mode");

				String value = beanDiscoveryMode.getStringValue();

				Assert.assertNotEquals("none", value);
			}

			// Finally, make sure the requirement on the OSGi CDI
			// Integration extender was added to the manifest

			Domain domain = Domain.domain(jar.getManifest());

			Parameters requirements = domain.getRequireCapability();

			Map.Entry<String, Attrs> entry = _findRequirement(
				requirements, "osgi.extender",
				HashMapBuilder.<String, Object>put(
					"osgi.extender", "osgi.cdi"
				).put(
					"version", new Version(1)
				).build());

			Assert.assertNotNull(entry);

			// Assert the expected number of beans were discovered

			Attrs attrs = entry.getValue();

			List<String> beans = attrs.getTyped(Attrs.LIST_STRING, "beans");

			Assert.assertEquals(beans.toString(), 5, beans.size());

			List<String> expectedList = Arrays.asList(
				"javax.portlet.tck.portlets.URLTests_ActionURL",
				"javax.portlet.tck.portlets.URLTests_BaseURL",
				"javax.portlet.tck.portlets.URLTests_RenderURL",
				"javax.portlet.tck.portlets.URLTests_ResourceURL",
				"javax.portlet.tck.util.ModuleTestCaseDetails");

			Assert.assertEquals(expectedList.toString(), expectedList, beans);

			// Make sure other CDI requirements were added

			// The bean portlet extension

			Assert.assertNotNull(
				_findRequirement(
					requirements, "osgi.cdi.extension",
					Collections.singletonMap(
						"osgi.cdi.extension",
						"com.liferay.bean.portlet.cdi.extension")));

			// The http extension

			Assert.assertNotNull(
				_findRequirement(
					requirements, "osgi.cdi.extension",
					Collections.singletonMap(
						"osgi.cdi.extension", "aries.cdi.http")));

			// The EL extension

			Assert.assertNotNull(
				_findRequirement(
					requirements, "osgi.cdi.extension",
					Collections.singletonMap(
						"osgi.cdi.extension", "aries.cdi.el.jsp")));
		}
	}

	protected File getFile(String fileName) throws URISyntaxException {
		URL url = WabProcessor.class.getResource(fileName);

		Assert.assertEquals(
			url + "is not file protocol", "file", url.getProtocol());

		Path path = Paths.get(url.toURI());

		return path.toFile();
	}

	private Map.Entry<String, Attrs> _findRequirement(
			Parameters requirements, String namespace,
			Map<String, Object> arguments)
		throws Exception {

		for (Map.Entry<String, Attrs> entry : requirements.entrySet()) {
			String key = Processor.removeDuplicateMarker(entry.getKey());

			if (!namespace.equals(key)) {
				continue;
			}

			Attrs attrs = entry.getValue();

			String filterString = attrs.get("filter:");

			if (filterString == null) {
				continue;
			}

			Filter filter = new Filter(filterString);

			if (filter.matchMap(arguments)) {
				return new AbstractMap.SimpleEntry<>(key, attrs);
			}
		}

		return null;
	}

	private static class TestWabProcessor extends WabProcessor {

		@Override
		protected void executeAutoDeployers(
			AutoDeploymentContext autoDeploymentContext) {

			try {
				File deployDir = autoDeploymentContext.getDeployDir();

				File parent = deployDir.getParentFile();

				Files.walkFileTree(
					parent.toPath(),
					new SimpleFileVisitor<Path>() {

						@Override
						public FileVisitResult postVisitDirectory(
								Path path, IOException ioException)
							throws IOException {

							Files.delete(path);

							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFile(
								Path path,
								BasicFileAttributes basicFileAttributes)
							throws IOException {

							Files.delete(path);

							return FileVisitResult.CONTINUE;
						}

					});

				parent.mkdirs();

				File newFile = new File(parent, _file.getName());

				Files.copy(
					_file.toPath(), newFile.toPath(),
					StandardCopyOption.REPLACE_EXISTING);
			}
			catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		private TestWabProcessor(File file, Map<String, String[]> parameters) {
			super(TestWabProcessor.class.getClassLoader(), file, parameters);

			_file = file;
		}

		private final File _file;

	}

}