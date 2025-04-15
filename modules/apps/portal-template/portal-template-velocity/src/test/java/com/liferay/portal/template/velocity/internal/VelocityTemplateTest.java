/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.velocity.internal;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.template.TemplateResourceCache;
import com.liferay.portal.kernel.template.TemplateResourceLoader;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaDetector;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.template.ClassLoaderResourceParser;
import com.liferay.portal.template.TemplateResourceParser;
import com.liferay.portal.template.engine.TemplateContextHelper;
import com.liferay.portal.template.velocity.configuration.VelocityEngineConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.io.StringReader;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.runtime.RuntimeConstants;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Tina Tian
 * @author Raymond Augé
 */
public class VelocityTemplateTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		VelocityManager velocityManager = new VelocityManager();

		_templateResourceCache =
			velocityManager.new VelocityTemplateResourceCache() {

				@Override
				public boolean isEnabled() {
					return false;
				}

			};

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_templateResourceLoader =
			velocityManager.new VelocityTemplateResourceLoader(
				bundleContext, _templateResourceCache);

		_templateResourceParserServiceRegistration =
			bundleContext.registerService(
				TemplateResourceParser.class, new ClassLoaderResourceParser(),
				MapUtil.singletonDictionary(
					"lang.type", TemplateConstants.LANG_TYPE_VM));
	}

	@AfterClass
	public static void tearDownClass() {
		if (_templateResourceParserServiceRegistration != null) {
			_templateResourceParserServiceRegistration.unregister();
		}

		if (_templateResourceLoader != null) {
			_templateResourceLoader.destroy();
		}
	}

	@Before
	public void setUp() throws Exception {
		VelocityEngineConfiguration velocityEngineConfiguration =
			ConfigurableUtil.createConfigurable(
				VelocityEngineConfiguration.class, Collections.emptyMap());

		ExtendedProperties extendedProperties = new FastExtendedProperties();

		extendedProperties.setProperty(
			RuntimeConstants.INTROSPECTOR_RESTRICT_CLASSES,
			StringUtil.merge(
				_filterRestrictedClasses(
					velocityEngineConfiguration.restrictedClasses())));
		extendedProperties.setProperty(
			RuntimeConstants.INTROSPECTOR_RESTRICT_PACKAGES,
			StringUtil.merge(velocityEngineConfiguration.restrictedPackages()));
		extendedProperties.setProperty(
			RuntimeConstants.UBERSPECT_CLASSNAME,
			LiferaySecureUberspector.class.getName());
		extendedProperties.setProperty(
			VelocityEngine.DIRECTIVE_IF_TOSTRING_NULLCHECK,
			String.valueOf(
				velocityEngineConfiguration.directiveIfToStringNullCheck()));
		extendedProperties.setProperty(
			VelocityEngine.EVENTHANDLER_METHODEXCEPTION,
			LiferayMethodExceptionEventHandler.class.getName());
		extendedProperties.setProperty(
			VelocityEngine.RESOURCE_LOADER, "liferay");
		extendedProperties.setProperty(
			VelocityEngine.RESOURCE_MANAGER_CLASS,
			LiferayResourceManager.class.getName());
		extendedProperties.setProperty(
			VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS,
			velocityEngineConfiguration.logger());
		extendedProperties.setProperty(
			VelocityEngine.RUNTIME_LOG_LOGSYSTEM + ".log4j.category",
			velocityEngineConfiguration.loggerCategory());
		extendedProperties.setProperty(
			VelocityEngine.VM_LIBRARY,
			StringUtil.merge(velocityEngineConfiguration.velocimacroLibrary()));
		extendedProperties.setProperty(
			VelocityEngine.VM_LIBRARY_AUTORELOAD, Boolean.TRUE.toString());
		extendedProperties.setProperty(
			VelocityEngine.VM_PERM_ALLOW_INLINE_REPLACE_GLOBAL,
			Boolean.TRUE.toString());
		extendedProperties.setProperty(
			VelocityManager.VelocityTemplateResourceLoader.class.getName(),
			_templateResourceLoader);
		extendedProperties.setProperty(
			"liferay." + RuntimeConstants.INTROSPECTOR_RESTRICT_CLASSES +
				".methods",
			velocityEngineConfiguration.restrictedMethods());
		extendedProperties.setProperty(
			"liferay." + VelocityEngine.RESOURCE_LOADER + ".cache",
			Boolean.FALSE.toString());
		extendedProperties.setProperty(
			"liferay." + VelocityEngine.RESOURCE_LOADER + ".class",
			LiferayResourceLoader.class.getName());

		_velocityEngine.setExtendedProperties(extendedProperties);

		_velocityEngine.init();
	}

	@Test
	public void testGet() throws Exception {
		Template template = new VelocityTemplate(
			new MockTemplateResource(_TEMPLATE_FILE_NAME), null,
			_velocityEngine, _templateContextHelper, _templateResourceCache,
			false);

		template.put(_TEST_KEY, _TEST_VALUE);

		Object result = template.get(_TEST_KEY);

		Assert.assertNotNull(result);

		Assert.assertTrue(result instanceof String);

		String stringResult = (String)result;

		Assert.assertEquals(_TEST_VALUE, stringResult);
	}

	@Test
	public void testPrepare() throws Exception {
		Template template = new VelocityTemplate(
			new MockTemplateResource(_TEMPLATE_FILE_NAME), null,
			_velocityEngine, _templateContextHelper, _templateResourceCache,
			false);

		template.put(_TEST_KEY, _TEST_VALUE);

		template.prepare(null);

		Object result = template.get(_TEST_VALUE);

		Assert.assertNotNull(result);

		Assert.assertTrue(result instanceof String);

		String stringResult = (String)result;

		Assert.assertEquals(_TEST_VALUE, stringResult);
	}

	@Test
	public void testProcessTemplate1() throws Exception {
		Template template = new VelocityTemplate(
			new MockTemplateResource(_TEMPLATE_FILE_NAME), null,
			_velocityEngine, _templateContextHelper, _templateResourceCache,
			false);

		template.put(_TEST_KEY, _TEST_VALUE);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		String result = unsyncStringWriter.toString();

		Assert.assertEquals(_TEST_VALUE, result);
	}

	@Test
	public void testProcessTemplate2() throws Exception {
		Template template = new VelocityTemplate(
			new MockTemplateResource(_WRONG_TEMPLATE_ID), null, _velocityEngine,
			_templateContextHelper, _templateResourceCache, false);

		template.put(_TEST_KEY, _TEST_VALUE);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		try {
			template.processTemplate(unsyncStringWriter);

			Assert.fail();
		}
		catch (TemplateException templateException) {
			String message = templateException.getMessage();

			Assert.assertTrue(message, message.contains(_WRONG_TEMPLATE_ID));
		}
	}

	@Test
	public void testProcessTemplate3() throws Exception {
		Template template = new VelocityTemplate(
			new StringTemplateResource(
				_WRONG_TEMPLATE_ID, _TEST_TEMPLATE_CONTENT),
			null, _velocityEngine, _templateContextHelper,
			_templateResourceCache, false);

		template.put(_TEST_KEY, _TEST_VALUE);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		String result = unsyncStringWriter.toString();

		Assert.assertEquals(_TEST_VALUE, result);
	}

	@Test
	public void testProcessTemplate4() throws Exception {
		Template template = new VelocityTemplate(
			new MockTemplateResource(_TEMPLATE_FILE_NAME), null,
			_velocityEngine, _templateContextHelper, _templateResourceCache,
			false);

		template.put(_TEST_KEY, _TEST_VALUE);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(
			unsyncStringWriter,
			() -> new MockTemplateResource(_WRONG_ERROR_TEMPLATE_ID));

		String result = unsyncStringWriter.toString();

		Assert.assertEquals(_TEST_VALUE, result);
	}

	@Test
	public void testProcessTemplate5() throws Exception {
		Template template = new VelocityTemplate(
			new MockTemplateResource(_WRONG_TEMPLATE_ID), null, _velocityEngine,
			_templateContextHelper, _templateResourceCache, false);

		template.put(_TEST_KEY, _TEST_VALUE);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(
			unsyncStringWriter,
			() -> new MockTemplateResource(_TEMPLATE_FILE_NAME));

		String result = unsyncStringWriter.toString();

		Assert.assertEquals(_TEST_VALUE, result);
	}

	@Test
	public void testProcessTemplate6() throws Exception {
		Template template = new VelocityTemplate(
			new MockTemplateResource(_WRONG_TEMPLATE_ID), null, _velocityEngine,
			_templateContextHelper, _templateResourceCache, false);

		template.put(_TEST_KEY, _TEST_VALUE);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		try {
			template.processTemplate(
				unsyncStringWriter,
				() -> new MockTemplateResource(_WRONG_ERROR_TEMPLATE_ID));

			Assert.fail();
		}
		catch (TemplateException templateException) {
			String message = templateException.getMessage();

			Assert.assertTrue(
				message, message.contains(_WRONG_ERROR_TEMPLATE_ID));
		}
	}

	@Test
	public void testProcessTemplate7() throws Exception {
		Template template = new VelocityTemplate(
			new MockTemplateResource(_WRONG_TEMPLATE_ID), null, _velocityEngine,
			_templateContextHelper, _templateResourceCache, false);

		template.put(_TEST_KEY, _TEST_VALUE);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(
			unsyncStringWriter,
			() -> new StringTemplateResource(
				_WRONG_ERROR_TEMPLATE_ID, _TEST_TEMPLATE_CONTENT));

		String result = unsyncStringWriter.toString();

		Assert.assertEquals(_TEST_VALUE, result);
	}

	@Test
	public void testProcessTemplate8() throws Exception {
		Template template = new VelocityTemplate(
			new MockTemplateResource(_TEMPLATE_FILE_NAME),
			HashMapBuilder.<String, Object>put(
				_TEST_KEY, _TEST_VALUE
			).build(),
			_velocityEngine, _templateContextHelper, _templateResourceCache,
			false);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		String result = unsyncStringWriter.toString();

		Assert.assertEquals(_TEST_VALUE, result);
	}

	private String[] _filterRestrictedClasses(String[] restrictedClasses) {
		if (JavaDetector.isJDK21()) {

			// TODO: remove java.lang.Compiler from
			// VelocityEngineConfiguration.restrictedClasses() and this method
			// once fully upgraded to JDK21

			return ArrayUtil.remove(restrictedClasses, "java.lang.Compiler");
		}

		return restrictedClasses;
	}

	private static final String _TEMPLATE_FILE_NAME = "test.vm";

	private static final String _TEST_KEY = "TEST_KEY";

	private static final String _TEST_TEMPLATE_CONTENT = "$" + _TEST_KEY;

	private static final String _TEST_VALUE = "TEST_VALUE";

	private static final String _WRONG_ERROR_TEMPLATE_ID =
		"WRONG_ERROR_TEMPLATE_ID";

	private static final String _WRONG_TEMPLATE_ID = "WRONG_TEMPLATE_ID";

	private static TemplateResourceCache _templateResourceCache;
	private static TemplateResourceLoader _templateResourceLoader;
	private static ServiceRegistration<TemplateResourceParser>
		_templateResourceParserServiceRegistration;

	private final TemplateContextHelper _templateContextHelper =
		new MockTemplateContextHelper();
	private final VelocityEngine _velocityEngine = new VelocityEngine();

	private static class MockTemplateContextHelper
		extends TemplateContextHelper {

		@Override
		public Map<String, Object> getHelperUtilities(boolean restricted) {
			return Collections.emptyMap();
		}

		@Override
		public Set<String> getRestrictedVariables() {
			return Collections.emptySet();
		}

		@Override
		public void prepare(
			Map<String, Object> contextObjects,
			HttpServletRequest httpServletRequest) {

			String testValue = (String)contextObjects.get(_TEST_KEY);

			contextObjects.put(testValue, testValue);
		}

	}

	private static class MockTemplateResource implements TemplateResource {

		/**
		 * The empty constructor is required by {@link java.io.Externalizable}.
		 * Do not use this for any other purpose.
		 */
		@SuppressWarnings("unused")
		public MockTemplateResource() {
		}

		public MockTemplateResource(String templateId) {
			_templateId = templateId;
		}

		@Override
		public long getLastModified() {
			return _lastModified;
		}

		@Override
		public Reader getReader() {
			if (_templateId.equals(_TEMPLATE_FILE_NAME)) {
				return new StringReader(_TEST_TEMPLATE_CONTENT);
			}

			throw new ParseErrorException(
				"Unable to get reader for template source " + _templateId);
		}

		@Override
		public String getTemplateId() {
			return _templateId;
		}

		@Override
		public void readExternal(ObjectInput objectInput) throws IOException {
			_lastModified = objectInput.readLong();
			_templateId = objectInput.readUTF();
		}

		@Override
		public void writeExternal(ObjectOutput objectOutput)
			throws IOException {

			objectOutput.writeLong(_lastModified);
			objectOutput.writeUTF(_templateId);
		}

		private long _lastModified = System.currentTimeMillis();
		private String _templateId;

	}

}