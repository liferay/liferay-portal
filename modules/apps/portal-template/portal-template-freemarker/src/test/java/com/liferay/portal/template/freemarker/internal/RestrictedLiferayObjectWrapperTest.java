/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.freemarker.internal;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.impl.BaseModelImpl;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvoker;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.aop.AopCacheManager;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import freemarker.ext.beans.InvalidPropertyException;
import freemarker.ext.beans.SimpleMethodModel;
import freemarker.ext.beans.StringModel;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class RestrictedLiferayObjectWrapperTest
	extends BaseObjectWrapperTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() {
		TransactionInvokerUtil transactionInvokerUtil =
			new TransactionInvokerUtil();

		transactionInvokerUtil.setTransactionInvoker(
			new TestTransactionInvoker());
	}

	@Test
	public void testConstructor() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				RestrictedLiferayObjectWrapper.class.getName(),
				LoggerTestUtil.INFO)) {

			Assert.assertEquals(
				Collections.singletonList("com.liferay.package.name"),
				ReflectionTestUtil.getFieldValue(
					new RestrictedLiferayObjectWrapper(
						null, new String[] {"com.liferay.package.name"}, null),
					"_restrictedPackageNames"));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Registering com.liferay.package.name as a package because " +
					"the class cannot be found",
				logEntry.getMessage());
		}

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				RestrictedLiferayObjectWrapper.class.getName(),
				LoggerTestUtil.OFF)) {

			Assert.assertEquals(
				Collections.singletonList("com.liferay.package.name"),
				ReflectionTestUtil.getFieldValue(
					new RestrictedLiferayObjectWrapper(
						null, new String[] {"com.liferay.package.name"}, null),
					"_restrictedPackageNames"));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());
		}

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				null)) {

			Assert.assertEquals(
				Collections.singletonList(TestLiferayObject.class),
				ReflectionTestUtil.getFieldValue(
					new RestrictedLiferayObjectWrapper(
						null, new String[] {TestLiferayObject.class.getName()},
						null),
					"_restrictedClasses"));
		}
	}

	@Test
	public void testGetWithAllowedClass() throws Exception {
		_testGetWithAllowedClass("*", "com.liferay.portal.template.freemarker");
		_testGetWithAllowedClass("*", TestLiferayMethodObject.class.getName());
		_testGetWithAllowedClass(
			"com.liferay.portal.*", "com.liferay.portal.template.freemarker");
		_testGetWithAllowedClass(
			"com.liferay.portal.*", TestLiferayMethodObject.class.getName());
		_testGetWithAllowedClass(
			"com.liferay.portal.template.freemarker",
			"com.liferay.portal.template.freemarker");
		_testGetWithAllowedClass(
			"com.liferay.portal.template.freemarker",
			TestLiferayMethodObject.class.getName());
		_testGetWithAllowedClass(
			TestLiferayMethodObject.class.getName(),
			"com.liferay.portal.template.freemarker");
		_testGetWithAllowedClass(
			TestLiferayMethodObject.class.getName(),
			TestLiferayMethodObject.class.getName());
	}

	@Test
	public void testGetWithRestrictedClass() throws Exception {
		_testGetWithRestrictedClass("com.liferay.portal.*");
		_testGetWithRestrictedClass("com.liferay.portal.template.freemarker");
		_testGetWithRestrictedClass(TestLiferayMethodObject.class.getName());
	}

	@Test
	public void testGetWithRestrictedMethodNames() throws Exception {
		RestrictedLiferayObjectWrapper restrictedLiferayObjectWrapper =
			new RestrictedLiferayObjectWrapper(
				null, null,
				new String[] {
					TestLiferayMethodObject.class.getName() + "#getName",
					TestLiferayMethodObject.class.getName() + "#toString"
				});

		TemplateModel templateModel = restrictedLiferayObjectWrapper.wrap(
			new TestLiferayMethodObject("test"));

		Assert.assertThat(
			templateModel,
			CoreMatchers.instanceOf(LiferayFreeMarkerStringModel.class));

		LiferayFreeMarkerStringModel liferayFreeMarkerStringModel =
			(LiferayFreeMarkerStringModel)templateModel;

		_testGetWithRestricted("Name", liferayFreeMarkerStringModel);
		_testGetWithRestricted("getName", liferayFreeMarkerStringModel);
		_testGetWithRestricted("getname", liferayFreeMarkerStringModel);
		_testGetWithRestricted("name", liferayFreeMarkerStringModel);

		Assert.assertEquals(
			"Denied access to the toString method in class " +
				TestLiferayMethodObject.class,
			liferayFreeMarkerStringModel.getAsString());

		SimpleMethodModel simpleMethodModel =
			(SimpleMethodModel)liferayFreeMarkerStringModel.get("generate");

		TemplateModel resultTemplateModel =
			(TemplateModel)simpleMethodModel.exec(
				Collections.singletonList(new SimpleScalar("generate")));

		Assert.assertEquals("test-generate", resultTemplateModel.toString());
	}

	@Test
	public void testIsMatch() {
		Assert.assertFalse(
			_isMatch(
				Collections.emptyList(), TestLiferayObject.class,
				new HashMap<>(), Collections.emptyList()));
		Assert.assertFalse(
			_isMatch(
				Collections.emptyList(), TestLiferayObject.class,
				new HashMap<>(),
				Collections.singletonList("com.liferay.portal.cache")));
		Assert.assertFalse(
			_isMatch(
				Collections.emptyList(), byte.class, new HashMap<>(),
				Collections.singletonList(
					"com.liferay.portal.template.freemarker")));
		Assert.assertFalse(
			_isMatch(
				Collections.singletonList(String.class),
				TestLiferayObject.class, new HashMap<>(),
				Collections.emptyList()));
		Assert.assertTrue(
			_isMatch(
				Collections.emptyList(), TestLiferayObject.class,
				new HashMap<>(),
				Collections.singletonList("com.liferay.portal.")));
		Assert.assertTrue(
			_isMatch(
				Collections.emptyList(), TestLiferayObject.class,
				new HashMap<>(),
				Collections.singletonList(
					"com.liferay.portal.template.freemarker")));
		Assert.assertTrue(
			_isMatch(
				Collections.singletonList(TestLiferayObject.class),
				TestLiferayObject.class, new HashMap<>(),
				Collections.emptyList()));
	}

	@Test
	public void testRestrictedMethodNamesIncorrectSyntax() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				RestrictedLiferayObjectWrapper.class.getName(),
				LoggerTestUtil.INFO)) {

			String methodName =
				TestLiferayMethodObject.class.getName() + ".getName";

			new RestrictedLiferayObjectWrapper(
				null, null, new String[] {methodName});

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"\"", methodName, "\" does not match format ",
					"\"className#methodName\""),
				logEntry.getMessage());
		}
	}

	@NewEnv(type = NewEnv.Type.CLASSLOADER)
	@Test
	public void testWrapWithCompanyRestrictForFalse() throws Exception {
		TransactionInvokerUtil transactionInvokerUtil =
			new TransactionInvokerUtil();

		transactionInvokerUtil.setTransactionInvoker(
			new TestTransactionInvoker());

		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "TEMPLATE_ENGINE_FREEMARKER_COMPANY_RESTRICT",
			false);

		_testWrap();
	}

	@NewEnv(type = NewEnv.Type.CLASSLOADER)
	@Test
	public void testWrapWithCompanyRestrictForTrue() throws Exception {
		TransactionInvokerUtil transactionInvokerUtil =
			new TransactionInvokerUtil();

		transactionInvokerUtil.setTransactionInvoker(
			new TestTransactionInvoker());

		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "TEMPLATE_ENGINE_FREEMARKER_COMPANY_RESTRICT",
			true);

		_testWrap();
	}

	@NewEnv(type = NewEnv.Type.CLASSLOADER)
	@Test
	public void testWrapWithTransactionStrictReadOnlyForFalse()
		throws Exception {

		TransactionInvokerUtil transactionInvokerUtil =
			new TransactionInvokerUtil();

		transactionInvokerUtil.setTransactionInvoker(
			new TestTransactionInvoker());

		ReflectionTestUtil.setFieldValue(
			PropsValues.class,
			"TEMPLATE_ENGINE_FREEMARKER_TRANSACTION_READ_ONLY", false);

		_testWrap();
	}

	@NewEnv(type = NewEnv.Type.CLASSLOADER)
	@Test
	public void testWrapWithTransactionStrictReadOnlyForTrue()
		throws Exception {

		TransactionInvokerUtil transactionInvokerUtil =
			new TransactionInvokerUtil();

		transactionInvokerUtil.setTransactionInvoker(
			new TestTransactionInvoker());

		ReflectionTestUtil.setFieldValue(
			PropsValues.class,
			"TEMPLATE_ENGINE_FREEMARKER_TRANSACTION_READ_ONLY", true);

		_testWrap();
	}

	public class TestLiferayMethodObject {

		public String generate(String postfix) {
			return _name + StringPool.DASH + postfix;
		}

		public String getName() {
			return _name;
		}

		public void setName(String name) {
			_name = name;
		}

		@Override
		public String toString() {
			return _name;
		}

		private TestLiferayMethodObject(String name) {
			_name = name;
		}

		private String _name;

	}

	@Override
	protected void testWrap(ObjectWrapper objectWrapper) throws Exception {
		super.testWrap(objectWrapper);

		// Local service object with proxy

		assertTemplateModel(
			"TestLocalServiceObject1", stringModel -> stringModel.getAsString(),
			StringModel.class.cast(
				objectWrapper.wrap(
					_createAopProxy(
						new TestLocalService("TestLocalServiceObject1")))));

		assertTemplateModel(
			"TestLocalServiceObject2", stringModel -> stringModel.getAsString(),
			StringModel.class.cast(
				objectWrapper.wrap(
					_createAopProxy(
						new TestLocalService("TestLocalServiceObject2")))));

		// Local service object without proxy

		assertTemplateModel(
			"TestLocalServiceObject", stringModel -> stringModel.getAsString(),
			StringModel.class.cast(
				objectWrapper.wrap(
					new TestLocalService("TestLocalServiceObject"))));

		// Service object

		assertTemplateModel(
			"TestServiceObject", stringModel -> stringModel.getAsString(),
			StringModel.class.cast(
				objectWrapper.wrap(
					_createAopProxy(new TestService("TestServiceObject")))));

		// System company ID

		assertTemplateModel(
			"123", stringModel -> stringModel.getAsString(),
			StringModel.class.cast(
				objectWrapper.wrap(new TestBaseModel(123L))));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				CompanyThreadLocal.class.getName(), LoggerTestUtil.OFF);
			SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					Long.valueOf(1))) {

			// Base model with company ID

			assertTemplateModel(
				"1", stringModel -> stringModel.getAsString(),
				StringModel.class.cast(
					objectWrapper.wrap(new TestBaseModel(1L))));

			// Base model with wrong company ID

			boolean companyRestrict = ReflectionTestUtil.getFieldValue(
				PropsValues.class,
				"TEMPLATE_ENGINE_FREEMARKER_COMPANY_RESTRICT");

			if (companyRestrict) {
				try {
					objectWrapper.wrap(new TestBaseModel(123L));

					Assert.fail();
				}
				catch (TemplateModelException templateModelException) {
					Assert.assertEquals(
						"Denied access to model object as it does not belong " +
							"to current company 1",
						templateModelException.getMessage());
				}
			}
			else {
				assertTemplateModel(
					"123", stringModel -> stringModel.getAsString(),
					StringModel.class.cast(
						objectWrapper.wrap(new TestBaseModel(123L))));
			}

			// Base model with wrong company ID and disabled checking

			try (SafeCloseable safeCloseable2 =
					CompanyThreadLocal.
						setInitializingPortalInstanceWithSafeCloseable(true)) {

				assertTemplateModel(
					"123", stringModel -> stringModel.getAsString(),
					StringModel.class.cast(
						objectWrapper.wrap(new TestBaseModel(123L))));
			}

			// Base model without company ID

			assertTemplateModel(
				"123", stringModel -> stringModel.getAsString(),
				StringModel.class.cast(
					objectWrapper.wrap(
						new TestBaseModel(123L) {

							public Map<String, Function<TestBaseModel, Object>>
								getAttributeGetterFunctions() {

								return Collections.emptyMap();
							}

						})));
		}
	}

	private Object _createAopProxy(Object target) {
		Class<?> clazz = target.getClass();

		return ProxyUtil.newProxyInstance(
			clazz.getClassLoader(), clazz.getInterfaces(),
			AopCacheManager.create(target, null));
	}

	private boolean _isMatch(
		List<Class<?>> classes, Class<?> clazz,
		Map<String, Boolean> matchResults, List<String> packageNames) {

		return ReflectionTestUtil.invoke(
			new RestrictedLiferayObjectWrapper(null, null, null), "_isMatch",
			new Class<?>[] {List.class, Class.class, Map.class, List.class},
			classes, clazz, matchResults, packageNames);
	}

	private void _testGetWithAllowedClass(
			String allowedClassName, String restrictedClassName)
		throws Exception {

		RestrictedLiferayObjectWrapper restrictedLiferayObjectWrapper =
			new RestrictedLiferayObjectWrapper(
				new String[] {allowedClassName},
				new String[] {restrictedClassName}, null);

		TemplateModel templateModel = restrictedLiferayObjectWrapper.wrap(
			new TestLiferayMethodObject("test"));

		Assert.assertThat(
			templateModel,
			CoreMatchers.not(
				CoreMatchers.instanceOf(LiferayFreeMarkerStringModel.class)));

		StringModel stringModel = (StringModel)templateModel;

		Assert.assertNull(stringModel.get("Name"));
		Assert.assertNull(stringModel.get("generateName"));

		SimpleMethodModel simpleMethodModel =
			(SimpleMethodModel)stringModel.get("getName");

		TemplateModel resultTemplateModel =
			(TemplateModel)simpleMethodModel.exec(Collections.emptyList());

		Assert.assertEquals("test", resultTemplateModel.toString());

		Assert.assertNull(stringModel.get("getname"));

		TemplateModel nameTemplateModel = stringModel.get("name");

		Assert.assertEquals("test", nameTemplateModel.toString());
	}

	private void _testGetWithRestricted(
		String key, LiferayFreeMarkerStringModel liferayFreeMarkerStringModel) {

		try {
			liferayFreeMarkerStringModel.get(key);

			Assert.assertNull("Should throw TemplateModelException for " + key);
		}
		catch (TemplateModelException templateModelException) {
			Assert.assertSame(
				InvalidPropertyException.class,
				templateModelException.getClass());

			Assert.assertEquals(
				StringBundler.concat(
					"Denied access to method or field ", key, " of ",
					TestLiferayMethodObject.class),
				templateModelException.getMessage());
		}
	}

	private void _testGetWithRestrictedClass(String restrictedClassName)
		throws Exception {

		RestrictedLiferayObjectWrapper restrictedLiferayObjectWrapper =
			new RestrictedLiferayObjectWrapper(
				null, new String[] {restrictedClassName}, null);

		TemplateModel templateModel = restrictedLiferayObjectWrapper.wrap(
			new TestLiferayMethodObject("test"));

		Assert.assertThat(
			templateModel,
			CoreMatchers.instanceOf(LiferayFreeMarkerStringModel.class));

		LiferayFreeMarkerStringModel liferayFreeMarkerStringModel =
			(LiferayFreeMarkerStringModel)templateModel;

		_testGetWithRestricted("Name", liferayFreeMarkerStringModel);
		_testGetWithRestricted("generateName", liferayFreeMarkerStringModel);
		_testGetWithRestricted("getName", liferayFreeMarkerStringModel);
		_testGetWithRestricted("getname", liferayFreeMarkerStringModel);
		_testGetWithRestricted("name", liferayFreeMarkerStringModel);
	}

	private void _testWrap() throws Exception {
		testWrap(
			new RestrictedLiferayObjectWrapper(
				new String[] {StringPool.BLANK},
				new String[] {StringPool.BLANK},
				new String[] {StringPool.BLANK}));
		testWrap(
			new RestrictedLiferayObjectWrapper(
				new String[] {StringPool.BLANK},
				new String[] {StringPool.BLANK},
				new String[] {TestBaseModel.class.getName() + "#getName"}));
		testWrap(
			new RestrictedLiferayObjectWrapper(
				new String[] {StringPool.BLANK},
				new String[] {StringPool.BLANK},
				new String[] {
					TestLiferayMethodObject.class.getName() + "#getName"
				}));
		testWrap(
			new RestrictedLiferayObjectWrapper(
				new String[] {StringPool.BLANK},
				new String[] {StringPool.BLANK}, null));
		testWrap(
			new RestrictedLiferayObjectWrapper(
				new String[] {StringPool.BLANK}, null, null));
		testWrap(
			new RestrictedLiferayObjectWrapper(
				new String[] {StringPool.STAR},
				new String[] {LiferayObjectWrapper.class.getName()}, null));
		testWrap(
			new RestrictedLiferayObjectWrapper(
				new String[] {StringPool.STAR}, null, null));
		testWrap(
			new RestrictedLiferayObjectWrapper(
				null, new String[] {StringPool.BLANK}, null));
		testWrap(new RestrictedLiferayObjectWrapper(null, null, null));
	}

	private static class TestBaseModel extends BaseModelImpl<TestBaseModel> {

		@Override
		public Object clone() {
			return null;
		}

		@Override
		public TestBaseModel cloneWithOriginalValues() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int compareTo(TestBaseModel testBaseModel) {
			return 0;
		}

		public Map<String, Function<TestBaseModel, Object>>
			getAttributeGetterFunctions() {

			return Collections.singletonMap(
				"companyId", TestBaseModel::getCompanyId);
		}

		public long getCompanyId() {
			return _companyId;
		}

		@Override
		public Map<String, Object> getModelAttributes() {
			return null;
		}

		@Override
		public Class<?> getModelClass() {
			return null;
		}

		@Override
		public String getModelClassName() {
			return null;
		}

		@Override
		public Serializable getPrimaryKeyObj() {
			return null;
		}

		@Override
		public boolean isEntityCacheEnabled() {
			return false;
		}

		@Override
		public boolean isFinderCacheEnabled() {
			return false;
		}

		@Override
		public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		}

		@Override
		public String toString() {
			return String.valueOf(_companyId);
		}

		private TestBaseModel(long companyId) {
			_companyId = companyId;
		}

		private final long _companyId;

	}

	private static class TestLocalService implements BaseLocalService {

		@Override
		public String toString() {
			return _name;
		}

		private TestLocalService(String name) {
			_name = name;
		}

		private final String _name;

	}

	private static class TestService implements BaseService {

		@Override
		public String toString() {
			return _name;
		}

		private TestService(String name) {
			_name = name;
		}

		private final String _name;

	}

	private static class TestTransactionInvoker implements TransactionInvoker {

		@Override
		public <T> T invoke(
				TransactionConfig transactionConfig, Callable<T> callable)
			throws Throwable {

			boolean transactionReadOnly = ReflectionTestUtil.getFieldValue(
				PropsValues.class,
				"TEMPLATE_ENGINE_FREEMARKER_TRANSACTION_READ_ONLY");

			if ((transactionConfig.isStrictReadOnly() && transactionReadOnly) ||
				(!transactionConfig.isStrictReadOnly() &&
				 !transactionReadOnly)) {

				return callable.call();
			}

			return null;
		}

	}

}