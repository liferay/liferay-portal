/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.template.velocity.internal;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.template.TemplateResourceLoader;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.template.ClassLoaderResourceParser;
import com.liferay.portal.template.TemplateContextHelper;
import com.liferay.portal.template.TemplateResourceParser;
import com.liferay.portal.template.velocity.configuration.VelocityEngineConfiguration;
import com.liferay.portal.tools.ToolDependencies;
import com.liferay.portal.util.FileImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.ServiceRegistration;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.io.StringReader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.runtime.RuntimeConstants;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Tina Tian
 * @author Raymond Augé
 */
public class VelocityTemplateTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		ToolDependencies.wireCaches();

		FileUtil fileUtil = new FileUtil();

		fileUtil.setFile(new FileImpl());

		_templateResourceLoader = new MockTemplateResourceLoader();

		_templateResourceLoader.activate(
			Collections.<String, Object>emptyMap());

		Registry registry = RegistryUtil.getRegistry();

		_serviceRegistrations.add(
			registry.registerService(
				TemplateResourceLoader.class, _templateResourceLoader));

		_serviceRegistrations.add(
			registry.registerService(
				TemplateResourceParser.class, new ClassLoaderResourceParser(),
				Collections.<String, Object>singletonMap(
					"lang.type", TemplateConstants.LANG_TYPE_VM)));

		_serviceReference = registry.getServiceReference(SingleVMPool.class);

		_singleVMPool = registry.getService(_serviceReference);
	}

	@AfterClass
	public static void tearDownClass() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();

		Registry registry = RegistryUtil.getRegistry();

		registry.ungetService(_serviceReference);
	}

	@Before
	public void setUp() throws Exception {
		VelocityEngineConfiguration velocityEngineConfiguration =
			ConfigurableUtil.createConfigurable(
				VelocityEngineConfiguration.class, Collections.emptyMap());

		_templateContextHelper = new MockTemplateContextHelper();

		_velocityEngine = new VelocityEngine();

		boolean cacheEnabled = false;

		ExtendedProperties extendedProperties = new FastExtendedProperties();

		extendedProperties.setProperty(
			VelocityEngine.DIRECTIVE_IF_TOSTRING_NULLCHECK,
			String.valueOf(
				velocityEngineConfiguration.directiveIfToStringNullCheck()));
		extendedProperties.setProperty(
			VelocityEngine.EVENTHANDLER_METHODEXCEPTION,
			LiferayMethodExceptionEventHandler.class.getName());
		extendedProperties.setProperty(
			RuntimeConstants.INTROSPECTOR_RESTRICT_CLASSES,
			StringUtil.merge(velocityEngineConfiguration.restrictedClasses()));
		extendedProperties.setProperty(
			RuntimeConstants.INTROSPECTOR_RESTRICT_PACKAGES,
			StringUtil.merge(velocityEngineConfiguration.restrictedPackages()));
		extendedProperties.setProperty(
			VelocityEngine.RESOURCE_LOADER, "liferay");
		extendedProperties.setProperty(
			"liferay." + VelocityEngine.RESOURCE_LOADER + ".cache",
			String.valueOf(cacheEnabled));
		extendedProperties.setProperty(
			"liferay." + VelocityEngine.RESOURCE_LOADER +
				".resourceModificationCheckInterval",
			velocityEngineConfiguration.resourceModificationCheckInterval() +
				"");
		extendedProperties.setProperty(
			"liferay." + VelocityEngine.RESOURCE_LOADER + ".class",
			LiferayResourceLoader.class.getName());
		extendedProperties.setProperty(
			VelocityEngine.RESOURCE_MANAGER_CLASS,
			LiferayResourceManager.class.getName());
		extendedProperties.setProperty(
			"liferay." + VelocityEngine.RESOURCE_MANAGER_CLASS +
				".resourceModificationCheckInterval",
			velocityEngineConfiguration.resourceModificationCheckInterval() +
				"");
		extendedProperties.setProperty(
			VelocityTemplateResourceLoader.class.getName(),
			_templateResourceLoader);
		extendedProperties.setProperty(
			VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS,
			velocityEngineConfiguration.logger());
		extendedProperties.setProperty(
			VelocityEngine.RUNTIME_LOG_LOGSYSTEM + ".log4j.category",
			velocityEngineConfiguration.loggerCategory());
		extendedProperties.setProperty(
			RuntimeConstants.UBERSPECT_CLASSNAME,
			LiferaySecureUberspector.class.getName());
		extendedProperties.setProperty(
			VelocityEngine.VM_LIBRARY,
			StringUtil.merge(velocityEngineConfiguration.velocimacroLibrary()));
		extendedProperties.setProperty(
			VelocityEngine.VM_LIBRARY_AUTORELOAD,
			String.valueOf(!cacheEnabled));
		extendedProperties.setProperty(
			VelocityEngine.VM_PERM_ALLOW_INLINE_REPLACE_GLOBAL,
			String.valueOf(!cacheEnabled));
		extendedProperties.setProperty(
			PortalCacheManagerNames.SINGLE_VM, _singleVMPool);

		_velocityEngine.setExtendedProperties(extendedProperties);

		_velocityEngine.init();
	}

	@Test
	public void testGet() throws Exception {
		Template template = new VelocityTemplate(
			new MockTemplateResource(_TEMPLATE_FILE_NAME), null, null,
			_velocityEngine, _templateContextHelper, 60, false);

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
			new MockTemplateResource(_TEMPLATE_FILE_NAME), null, null,
			_velocityEngine, _templateContextHelper, 60, false);

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
			new MockTemplateResource(_TEMPLATE_FILE_NAME), null, null,
			_velocityEngine, _templateContextHelper, 60, false);

		template.put(_TEST_KEY, _TEST_VALUE);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		String result = unsyncStringWriter.toString();

		Assert.assertEquals(_TEST_VALUE, result);
	}

	@Test
	public void testProcessTemplate2() throws Exception {
		Template template = new VelocityTemplate(
			new MockTemplateResource(_WRONG_TEMPLATE_ID), null, null,
			_velocityEngine, _templateContextHelper, 60, false);

		template.put(_TEST_KEY, _TEST_VALUE);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		try {
			template.processTemplate(unsyncStringWriter);

			Assert.fail();
		}
		catch (TemplateException te) {
			String message = te.getMessage();

			Assert.assertTrue(message, message.contains(_WRONG_TEMPLATE_ID));
		}
	}

	@Test
	public void testProcessTemplate3() throws Exception {
		Template template = new VelocityTemplate(
			new StringTemplateResource(
				_WRONG_TEMPLATE_ID, _TEST_TEMPLATE_CONTENT),
			null, null, _velocityEngine, _templateContextHelper, 60, false);

		template.put(_TEST_KEY, _TEST_VALUE);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		String result = unsyncStringWriter.toString();

		Assert.assertEquals(_TEST_VALUE, result);
	}

	@Test
	public void testProcessTemplate4() throws Exception {
		Template template = new VelocityTemplate(
			new MockTemplateResource(_TEMPLATE_FILE_NAME),
			new MockTemplateResource(_WRONG_ERROR_TEMPLATE_ID), null,
			_velocityEngine, _templateContextHelper, 60, false);

		template.put(_TEST_KEY, _TEST_VALUE);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		String result = unsyncStringWriter.toString();

		Assert.assertEquals(_TEST_VALUE, result);
	}

	@Test
	public void testProcessTemplate5() throws Exception {
		Template template = new VelocityTemplate(
			new MockTemplateResource(_WRONG_TEMPLATE_ID),
			new MockTemplateResource(_TEMPLATE_FILE_NAME), null,
			_velocityEngine, _templateContextHelper, 60, false);

		template.put(_TEST_KEY, _TEST_VALUE);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		String result = unsyncStringWriter.toString();

		Assert.assertEquals(_TEST_VALUE, result);
	}

	@Test
	public void testProcessTemplate6() throws Exception {
		Template template = new VelocityTemplate(
			new MockTemplateResource(_WRONG_TEMPLATE_ID),
			new MockTemplateResource(_WRONG_ERROR_TEMPLATE_ID), null,
			_velocityEngine, _templateContextHelper, 60, false);

		template.put(_TEST_KEY, _TEST_VALUE);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		try {
			template.processTemplate(unsyncStringWriter);

			Assert.fail();
		}
		catch (TemplateException te) {
			String message = te.getMessage();

			Assert.assertTrue(
				message, message.contains(_WRONG_ERROR_TEMPLATE_ID));
		}
	}

	@Test
	public void testProcessTemplate7() throws Exception {
		Template template = new VelocityTemplate(
			new MockTemplateResource(_WRONG_TEMPLATE_ID),
			new StringTemplateResource(
				_WRONG_ERROR_TEMPLATE_ID, _TEST_TEMPLATE_CONTENT),
			null, _velocityEngine, _templateContextHelper, 60, false);

		template.put(_TEST_KEY, _TEST_VALUE);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		String result = unsyncStringWriter.toString();

		Assert.assertEquals(_TEST_VALUE, result);
	}

	@Test
	public void testProcessTemplate8() throws Exception {
		Map<String, Object> context = new HashMap<>();

		context.put(_TEST_KEY, _TEST_VALUE);

		Template template = new VelocityTemplate(
			new MockTemplateResource(_TEMPLATE_FILE_NAME), null, context,
			_velocityEngine, _templateContextHelper, 60, false);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		String result = unsyncStringWriter.toString();

		Assert.assertEquals(_TEST_VALUE, result);
	}

	private static final String _TEMPLATE_FILE_NAME = "test.vm";

	private static final String _TEST_KEY = "TEST_KEY";

	private static final String _TEST_TEMPLATE_CONTENT = "$" + _TEST_KEY;

	private static final String _TEST_VALUE = "TEST_VALUE";

	private static final String _WRONG_ERROR_TEMPLATE_ID =
		"WRONG_ERROR_TEMPLATE_ID";

	private static final String _WRONG_TEMPLATE_ID = "WRONG_TEMPLATE_ID";

	private static ServiceReference<SingleVMPool> _serviceReference;
	private static final Set<ServiceRegistration<?>> _serviceRegistrations =
		Collections.newSetFromMap(new ConcurrentHashMap<>());
	private static SingleVMPool _singleVMPool;
	private static MockTemplateResourceLoader _templateResourceLoader;

	private TemplateContextHelper _templateContextHelper;
	private VelocityEngine _velocityEngine;

	private static class MockTemplateContextHelper
		extends TemplateContextHelper {

		@Override
		public Map<String, Object> getHelperUtilities(
			ClassLoader classLoader, boolean restricted) {

			return Collections.emptyMap();
		}

		@Override
		public Set<String> getRestrictedVariables() {
			return Collections.emptySet();
		}

		@Override
		public void prepare(
			Map<String, Object> contextObjects, HttpServletRequest request) {

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

	private static class MockTemplateResourceLoader
		extends VelocityTemplateResourceLoader {

		@Override
		protected void activate(Map<String, Object> properties) {
			Registry registry = RegistryUtil.getRegistry();

			setMultiVMPool(
				registry.callService(MultiVMPool.class, Function.identity()));
			setSingleVMPool(
				registry.callService(SingleVMPool.class, Function.identity()));

			super.activate(properties);
		}

	}

}