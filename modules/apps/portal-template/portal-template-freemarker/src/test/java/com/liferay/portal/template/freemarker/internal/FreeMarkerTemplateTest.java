/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.freemarker.internal;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.template.TemplateResourceCache;
import com.liferay.portal.kernel.template.TemplateResourceLoader;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.template.engine.TemplateContextHelper;
import com.liferay.portal.template.freemarker.configuration.FreeMarkerEngineConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import freemarker.cache.TemplateCache;

import freemarker.core.ParseException;

import freemarker.ext.beans.BeansWrapper;

import freemarker.template.Configuration;
import freemarker.template.SimpleNumber;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class FreeMarkerTemplateTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_freeMarkerManager = new FreeMarkerManager();

		FreeMarkerEngineConfiguration freeMarkerEngineConfiguration =
			ConfigurableUtil.createConfigurable(
				FreeMarkerEngineConfiguration.class, Collections.emptyMap());

		ReflectionTestUtil.setFieldValue(
			_freeMarkerManager, "_freeMarkerEngineConfiguration",
			freeMarkerEngineConfiguration);

		_templateResourceCache =
			_freeMarkerManager.new FreeMarkerTemplateResourceCache() {

				@Override
				public boolean isEnabled() {
					return false;
				}

			};

		_templateResourceLoader =
			_freeMarkerManager.new FreeMarkerTemplateResourceLoader(
				SystemBundleUtil.getBundleContext(), _templateResourceCache);
	}

	@AfterClass
	public static void tearDownClass() {
		if (_templateResourceLoader != null) {
			_templateResourceLoader.destroy();
		}
	}

	@Before
	public void setUp() throws Exception {
		_configuration = new Configuration();

		_configuration.setLogTemplateExceptions(false);

		TemplateCache templateCache = new LiferayTemplateCache(
			_configuration, _templateResourceLoader, null);

		ReflectionTestUtil.setFieldValue(
			_configuration, "cache", templateCache);

		_configuration.setLocalizedLookup(false);

		_templateContextHelper = new MockTemplateContextHelper();
	}

	@Test
	public void testGet() throws Exception {
		Template template = new FreeMarkerTemplate(
			new MockTemplateResource(_TEMPLATE_FILE_NAME), null, _configuration,
			_templateContextHelper, _templateResourceCache, false,
			(BeansWrapper)_configuration.getObjectWrapper(),
			_freeMarkerManager);

		template.put(_TEST_KEY, _TEST_VALUE);

		Object result = template.get(_TEST_KEY);

		Assert.assertNotNull(result);

		Assert.assertTrue(result instanceof String);

		String stringResult = (String)result;

		Assert.assertEquals(_TEST_VALUE, stringResult);
	}

	@Test
	public void testLoopCountThreshold() throws Exception {
		_testLoopCountThreshold(
			"<#list 1..3 as x>${x}</#list>", Collections.emptyMap(), "123");
		_testLoopCountThreshold(
			"<#list ['1', '2', '3'] as x>${x}</#list>", Collections.emptyMap(),
			"123");
		_testLoopCountThreshold(
			"<#list 1..3><#items as x>${x}</#items></#list>",
			Collections.emptyMap(), "123");
		_testLoopCountThreshold(
			"<#list 1..2 as i>${i}<#list 1..3 as j>${j}</#list></#list>",
			Collections.emptyMap(), "11232123");
		_testLoopCountThreshold(
			"<#list {'1':1,'2':2,'3':3} as x, y>${x},${y};</#list>",
			Collections.emptyMap(), "1,1;2,2;3,3;");

		List<String> testList = new ArrayList<>();

		testList.add("1");
		testList.add("2");
		testList.add("3");

		_testLoopCountThreshold(
			"<#list testList as x>${x}</#list>",
			Collections.singletonMap("testList", testList), "123");
		_testLoopCountThreshold(
			"<#list testList><#items as x>${x}</#items></#list>",
			Collections.singletonMap("testList", testList), "123");

		Set<String> testSet = new HashSet<>();

		testSet.add("1");
		testSet.add("2");
		testSet.add("3");

		_testLoopCountThreshold(
			"<#list testSet as x>${x}</#list>",
			Collections.singletonMap("testSet", testSet), "123");
		_testLoopCountThreshold(
			"<#list testSet><#items as x>${x}</#items></#list>",
			Collections.singletonMap("testSet", testSet), "123");

		_testLoopCountThreshold(
			"<#list testMap as x, y>${x},${y};</#list>",
			Collections.singletonMap(
				"testMap",
				HashMapBuilder.put(
					"1", 1
				).put(
					"2", 2
				).put(
					"3", 3
				).build()),
			"1,1;2,2;3,3;");

		_configuration.setSharedVariable("loop-size-threshold", null);
	}

	@Test
	public void testPrepare() throws Exception {
		Template template = new FreeMarkerTemplate(
			new MockTemplateResource(_TEMPLATE_FILE_NAME), null, _configuration,
			_templateContextHelper, _templateResourceCache, false,
			(BeansWrapper)_configuration.getObjectWrapper(),
			_freeMarkerManager);

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
		Template template = new FreeMarkerTemplate(
			new MockTemplateResource(_TEMPLATE_FILE_NAME), null, _configuration,
			_templateContextHelper, _templateResourceCache, false,
			(BeansWrapper)_configuration.getObjectWrapper(),
			_freeMarkerManager);

		template.put(_TEST_KEY, _TEST_VALUE);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		String result = unsyncStringWriter.toString();

		Assert.assertEquals(_TEST_VALUE, result);
	}

	@Test
	public void testProcessTemplate2() throws Exception {
		Template template = new FreeMarkerTemplate(
			new MockTemplateResource(_WRONG_TEMPLATE_ID), null, _configuration,
			_templateContextHelper, _templateResourceCache, false,
			(BeansWrapper)_configuration.getObjectWrapper(),
			_freeMarkerManager);

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
		Template template = new FreeMarkerTemplate(
			new StringTemplateResource(
				_WRONG_TEMPLATE_ID, _TEST_TEMPLATE_CONTENT),
			null, _configuration, _templateContextHelper,
			_templateResourceCache, false,
			(BeansWrapper)_configuration.getObjectWrapper(),
			_freeMarkerManager);

		template.put(_TEST_KEY, _TEST_VALUE);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		String result = unsyncStringWriter.toString();

		Assert.assertEquals(_TEST_VALUE, result);
	}

	@Test
	public void testProcessTemplate4() throws Exception {
		Template template = new FreeMarkerTemplate(
			new MockTemplateResource(_TEMPLATE_FILE_NAME), null, _configuration,
			_templateContextHelper, _templateResourceCache, false,
			(BeansWrapper)_configuration.getObjectWrapper(),
			_freeMarkerManager);

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
		Template template = new FreeMarkerTemplate(
			new MockTemplateResource(_WRONG_TEMPLATE_ID), null, _configuration,
			_templateContextHelper, _templateResourceCache, false,
			(BeansWrapper)_configuration.getObjectWrapper(),
			_freeMarkerManager);

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
		Template template = new FreeMarkerTemplate(
			new MockTemplateResource(_WRONG_TEMPLATE_ID), null, _configuration,
			_templateContextHelper, _templateResourceCache, false,
			(BeansWrapper)_configuration.getObjectWrapper(),
			_freeMarkerManager);

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
		Template template = new FreeMarkerTemplate(
			new MockTemplateResource(_WRONG_TEMPLATE_ID), null, _configuration,
			_templateContextHelper, _templateResourceCache, false,
			(BeansWrapper)_configuration.getObjectWrapper(),
			_freeMarkerManager);

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
		Template template = new FreeMarkerTemplate(
			new MockTemplateResource(_TEMPLATE_FILE_NAME),
			HashMapBuilder.<String, Object>put(
				_TEST_KEY, _TEST_VALUE
			).build(),
			_configuration, _templateContextHelper, _templateResourceCache,
			false, (BeansWrapper)_configuration.getObjectWrapper(),
			_freeMarkerManager);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		String result = unsyncStringWriter.toString();

		Assert.assertEquals(_TEST_VALUE, result);
	}

	private void _testLoopCountThreshold(
			String templateContent, Map<String, Object> context,
			String expectedResult)
		throws Exception {

		Template template = new FreeMarkerTemplate(
			new StringTemplateResource(_TEMPLATE_FILE_NAME, templateContent),
			context, _configuration, _templateContextHelper,
			_templateResourceCache, false,
			(BeansWrapper)_configuration.getObjectWrapper(),
			_freeMarkerManager);

		_configuration.setSharedVariable(
			"loop-count-threshold", new SimpleNumber(2));

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		try {
			template.processTemplate(unsyncStringWriter);

			Assert.fail(
				"Template processing should fail by exceeding threshold");
		}
		catch (Exception exception) {
			String result = unsyncStringWriter.toString();

			Assert.assertTrue(
				result, result.contains("Loop count exceeds threshold 2"));
		}

		_configuration.setSharedVariable("loop-count-threshold", null);

		unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		Assert.assertEquals(expectedResult, unsyncStringWriter.toString());
	}

	private static final String _TEMPLATE_FILE_NAME = "test.ftl";

	private static final String _TEST_KEY = "TEST_KEY";

	private static final String _TEST_TEMPLATE_CONTENT = "${" + _TEST_KEY + "}";

	private static final String _TEST_VALUE = "TEST_VALUE";

	private static final String _WRONG_ERROR_TEMPLATE_ID =
		"WRONG_ERROR_TEMPLATE_ID";

	private static final String _WRONG_TEMPLATE_ID = "WRONG_TEMPLATE_ID";

	private static FreeMarkerManager _freeMarkerManager;
	private static TemplateResourceCache _templateResourceCache;
	private static TemplateResourceLoader _templateResourceLoader;

	private Configuration _configuration;
	private TemplateContextHelper _templateContextHelper;

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
		public Reader getReader() throws IOException {
			if (_templateId.equals(_TEMPLATE_FILE_NAME)) {
				return new StringReader(_TEST_TEMPLATE_CONTENT);
			}

			throw new ParseException(
				"Unable to get reader for template source " + _templateId, 0,
				0);
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