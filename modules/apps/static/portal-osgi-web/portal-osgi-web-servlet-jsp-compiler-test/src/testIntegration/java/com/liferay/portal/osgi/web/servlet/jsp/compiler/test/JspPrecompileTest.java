/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.jsp.compiler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTemplate;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.osgi.web.servlet.jsp.compiler.test.servlet.PrecompileTestServlet;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.Portlet;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Matthew Tambara
 */
@RunWith(Arquillian.class)
public class JspPrecompileTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_bundle = FrameworkUtil.getBundle(JspPrecompileTest.class);

		BundleContext bundleContext = _bundle.getBundleContext();

		_bundle = bundleContext.installBundle(
			JspPrecompilePortlet.PORTLET_NAME, _createTestBundle());

		_bundle.start();

		_workDirPath = Paths.get(
			PropsValues.LIFERAY_HOME, "work",
			_bundle.getSymbolicName() + StringPool.DASH + _bundle.getVersion());

		Files.createDirectories(_workDirPath);
	}

	@AfterClass
	public static void tearDownClass() throws BundleException {
		FileUtil.deltree(_workDirPath.toFile());

		_bundle.uninstall();
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		LayoutTemplate layoutTemplate = layoutTypePortlet.getLayoutTemplate();

		List<String> columnIds = layoutTemplate.getColumns();

		String columnId = columnIds.get(0);

		layoutTypePortlet.addPortletId(
			TestPropsValues.getUserId(), JspPrecompilePortlet.PORTLET_NAME,
			columnId, -1, false);

		LayoutLocalServiceUtil.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());
	}

	@After
	public void tearDown() throws PortalException {
		GroupLocalServiceUtil.deleteGroup(_group);
	}

	@Test
	public void testPrecompiledJsp() throws Exception {
		String packagePathString = StringUtil.replace(
			_JSP_PACKAGE_NAME, CharPool.PERIOD, CharPool.SLASH);

		Path packagePath = _workDirPath.resolve(packagePathString);

		Files.createDirectories(packagePath);

		String jspClassName = StringUtil.replace(
			_PRECOMPILE_JSP_FILE_NAME, CharPool.PERIOD, CharPool.UNDERLINE);

		Path jspClassPath = packagePath.resolve(jspClassName.concat(".class"));

		final String className = packagePathString.concat(jspClassName);

		try (InputStream inputStream =
				PrecompileTestServlet.class.getResourceAsStream(
					PrecompileTestServlet.class.getSimpleName() + ".class");
			OutputStream outputStream = Files.newOutputStream(jspClassPath)) {

			ClassReader classReader = new ClassReader(inputStream);

			ClassWriter classWriter = new ClassWriter(classReader, 0);

			ClassVisitor classVisitor = new ClassVisitor(
				Opcodes.ASM5, classWriter) {

				@Override
				public void visit(
					int version, int access, String name, String signature,
					String superName, String[] interfaces) {

					super.visit(
						version, access, className, signature, superName,
						interfaces);
				}

			};

			classReader.accept(classVisitor, 0);

			outputStream.write(classWriter.toByteArray());
		}

		Files.setLastModifiedTime(jspClassPath, _fileTime);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_JSP_COMPILER, LoggerTestUtil.DEBUG)) {

			_invokeJSP(_PRECOMPILE_JSP_FILE_NAME, "Precompiled");

			Assert.assertFalse(
				"JSP was compiled at runtime",
				_containsCompilerLog(logCapture, _PRECOMPILE_JSP_FILE_NAME));
		}
		finally {
			Files.delete(jspClassPath);
		}
	}

	@Test
	public void testRuntimeCompiledJsp() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_JSP_COMPILER, LoggerTestUtil.DEBUG)) {

			_invokeJSP(_RUNTIME_COMPILE_JSP_FILE_NAME, "Runtime Compiled");

			Assert.assertTrue(
				"No JSP was compiled at runtime",
				_containsCompilerLog(
					logCapture, _RUNTIME_COMPILE_JSP_FILE_NAME));
		}
	}

	private static String _buildImportPackage(Class<?>... classes) {
		if (ArrayUtil.isEmpty(classes)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(classes.length * 2);

		Set<Package> packages = new HashSet<>();

		for (Class<?> clazz : classes) {
			Package pkg = clazz.getPackage();

			if (packages.add(pkg)) {
				sb.append(pkg.getName());
				sb.append(StringPool.COMMA);
			}
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private static InputStream _createTestBundle() throws Exception {
		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream()) {

			try (JarOutputStream jarOutputStream = new JarOutputStream(
					unsyncByteArrayOutputStream)) {

				Manifest manifest = new Manifest();

				Attributes attributes = manifest.getMainAttributes();

				attributes.putValue(
					Constants.BUNDLE_ACTIVATOR,
					JspPrecompileBundleActivator.class.getName());
				attributes.putValue(Constants.BUNDLE_MANIFESTVERSION, "2");

				Package pkg = JspPrecompileTest.class.getPackage();

				attributes.putValue(
					Constants.BUNDLE_SYMBOLICNAME, pkg.getName() + ".bundle");

				attributes.putValue(Constants.BUNDLE_VERSION, "1.0.0");
				attributes.putValue(
					Constants.IMPORT_PACKAGE,
					_buildImportPackage(
						BundleActivator.class, HttpServletRequest.class,
						MVCPortlet.class, PortalUtil.class, Portlet.class));
				attributes.putValue("Manifest-Version", "2");

				jarOutputStream.putNextEntry(
					new ZipEntry(JarFile.MANIFEST_NAME));

				manifest.write(jarOutputStream);

				jarOutputStream.closeEntry();

				_writeClasses(
					jarOutputStream, JspPrecompileBundleActivator.class,
					JspPrecompilePortlet.class);

				ClassLoader classLoader =
					JspPrecompileTest.class.getClassLoader();

				String path = "META-INF/resources/".concat(
					_RUNTIME_COMPILE_JSP_FILE_NAME);

				jarOutputStream.putNextEntry(new ZipEntry(path));

				try (InputStream inputStream = classLoader.getResourceAsStream(
						path);
					OutputStream outputStream = StreamUtil.uncloseable(
						jarOutputStream)) {

					StreamUtil.transfer(inputStream, outputStream);
				}

				jarOutputStream.closeEntry();

				ZipEntry zipEntry = new ZipEntry(
					"META-INF/resources/".concat(_PRECOMPILE_JSP_FILE_NAME));

				_fileTime = FileTime.from(
					System.currentTimeMillis() / 1000, TimeUnit.SECONDS);

				zipEntry.setLastModifiedTime(_fileTime);

				jarOutputStream.putNextEntry(zipEntry);

				jarOutputStream.closeEntry();
			}

			return new UnsyncByteArrayInputStream(
				unsyncByteArrayOutputStream.unsafeGetByteArray(), 0,
				unsyncByteArrayOutputStream.size());
		}
	}

	private static void _writeClasses(
			JarOutputStream jarOutputStream, Class<?>... classes)
		throws IOException {

		ClassLoader classLoader = JspPrecompileTest.class.getClassLoader();

		for (Class<?> clazz : classes) {
			String className = clazz.getName();

			String path = StringUtil.replace(
				className, CharPool.PERIOD, CharPool.SLASH);

			String resourcePath = path.concat(".class");

			jarOutputStream.putNextEntry(new ZipEntry(resourcePath));

			try (InputStream inputStream = classLoader.getResourceAsStream(
					resourcePath);
				OutputStream outputStream = StreamUtil.uncloseable(
					jarOutputStream)) {

				StreamUtil.transfer(inputStream, outputStream);
			}

			jarOutputStream.closeEntry();
		}
	}

	private boolean _containsCompilerLog(
		LogCapture logCapture, String jspName) {

		String compilerLog = StringBundler.concat(
			"Compiling JSP: ", _JSP_PACKAGE_NAME,
			StringUtil.replace(jspName, CharPool.PERIOD, CharPool.UNDERLINE));

		for (LogEntry logEntry : logCapture.getLogEntries()) {
			String message = logEntry.getMessage();

			if (message.equals(compilerLog)) {
				return true;
			}
		}

		return false;
	}

	private void _invokeJSP(String jspFileName, String expectedMessage)
		throws IOException {

		URL url = new URL(
			StringBundler.concat(
				"http://localhost:8080/web", _group.getFriendlyURL(),
				"?p_p_id=", JspPrecompilePortlet.PORTLET_NAME,
				StringPool.AMPERSAND,
				JspPrecompilePortlet.getJspFileNameParameterName(), "=/",
				jspFileName));

		String content = URLUtil.toString(url);

		Assert.assertTrue(
			StringBundler.concat(
				"Content {", content, "} does not contain expected message {",
				expectedMessage, "}"),
			content.contains(expectedMessage));
	}

	private static final String _CLASS_NAME_JSP_COMPILER =
		"com.liferay.portal.osgi.web.servlet.jsp.compiler.internal." +
			"CompilerWrapper";

	private static final String _JSP_PACKAGE_NAME = "org.apache.jsp.";

	private static final String _PRECOMPILE_JSP_FILE_NAME =
		"PrecompileTestServlet.jsp";

	private static final String _RUNTIME_COMPILE_JSP_FILE_NAME = "runtime.jsp";

	private static Bundle _bundle;
	private static FileTime _fileTime;
	private static Path _workDirPath;

	private Group _group;

}