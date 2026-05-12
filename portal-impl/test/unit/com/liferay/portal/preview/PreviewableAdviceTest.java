/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.preview;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.preview.Previewable;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.aop.AopInvocationHandler;
import com.liferay.portal.spring.transaction.TransactionExecutor;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Shuyang Zhou
 */
public class PreviewableAdviceTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_persistedModelLocalService = new TestPersistedModelLocalService(
			TestModel.class, _testModels);

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			PersistedModelLocalService.class, _persistedModelLocalService,
			MapUtil.singletonDictionary(
				"model.class.name", TestModel.class.getName()));
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testAfterReturningResolvesProxiedModel() throws Exception {
		_testModels.put(2L, new TestModel(2L));

		TestPreviewableService testPreviewableService = _createProxy(
			new TestModel(1L));

		try (SafeCloseable safeCloseable = _openPreviewScope()) {
			Assert.assertSame(
				_testModels.get(2L), testPreviewableService.getTestModel());
		}
	}

	@Test
	public void testAfterReturningWithNullResult() throws Exception {
		TestPreviewableService testPreviewableService = _createProxy(null);

		try (SafeCloseable safeCloseable = _openPreviewScope()) {
			Assert.assertNull(testPreviewableService.getTestModel());
		}
	}

	@Test
	public void testDisabledPreviewable() {
		Assert.assertNull(
			_createMethodContext(
				_persistedModelLocalService, "getTestModel",
				_getAnnotations(DisabledPreviewableHolder.class)));
	}

	@Test
	public void testListReturnType() {
		Assert.assertNotNull(
			_createMethodContext(
				_persistedModelLocalService, "getTestModels",
				_getAnnotations(PreviewableHolder.class)));
	}

	@Test
	public void testMissingPreviewable() {
		Assert.assertNull(
			_createMethodContext(
				_persistedModelLocalService, "getTestModel",
				Collections.emptyMap()));
	}

	@Test
	public void testModelReturnType() {
		Assert.assertNotNull(
			_createMethodContext(
				_persistedModelLocalService, "getTestModel",
				_getAnnotations(PreviewableHolder.class)));
	}

	@Test
	public void testPrimitiveReturnType() {
		Assert.assertNull(
			_createMethodContext(
				_persistedModelLocalService, "dslQueryCount",
				_getAnnotations(PreviewableHolder.class)));
	}

	@Test
	public void testResolveRuntimeWithEmptyList() {
		List<Object> list = Collections.emptyList();

		try (SafeCloseable safeCloseable = _openPreviewScope()) {
			Assert.assertSame(list, _resolveRuntime(list));
		}
	}

	@Test
	public void testResolveRuntimeWithModel() {
		TestModel fromTestModel = new TestModel(1L);

		_testModels.put(2L, new TestModel(2L));

		try (SafeCloseable safeCloseable = _openPreviewScope()) {
			Assert.assertSame(
				_testModels.get(2L), _resolveRuntime(fromTestModel));
		}
	}

	@Test
	public void testResolveRuntimeWithModelList() {
		TestModel fromTestModel = new TestModel(1L);

		_testModels.put(2L, new TestModel(2L));

		try (SafeCloseable safeCloseable = _openPreviewScope()) {
			Assert.assertEquals(
				Arrays.<BaseModel<?>>asList(_testModels.get(2L)),
				_resolveRuntime(Arrays.<BaseModel<?>>asList(fromTestModel)));
		}
	}

	@Test
	public void testResolveRuntimeWithProjectionList() {
		List<Long> list = Arrays.asList(1L, 2L);

		try (SafeCloseable safeCloseable = _openPreviewScope()) {
			Assert.assertSame(list, _resolveRuntime(list));
		}
	}

	@Test
	public void testResolveRuntimeWithScalar() {
		Long count = 5L;

		try (SafeCloseable safeCloseable = _openPreviewScope()) {
			Assert.assertSame(count, _resolveRuntime(count));
		}
	}

	@Test
	public void testTypeVariableListReturnType() {
		Assert.assertNotNull(
			_createMethodContext(
				_persistedModelLocalService, "dynamicQuery",
				_getAnnotations(PreviewableHolder.class)));
	}

	@Test
	public void testTypeVariableReturnType() {
		Assert.assertNotNull(
			_createMethodContext(
				_persistedModelLocalService, "dslQuery",
				_getAnnotations(PreviewableHolder.class)));
	}

	@Test
	public void testUnrelatedReturnType() {
		Assert.assertNull(
			_createMethodContext(
				_persistedModelLocalService, "getUnrelated",
				_getAnnotations(PreviewableHolder.class)));
	}

	@Test
	public void testUnsupportedMethodName() {
		Assert.assertNull(
			_createMethodContext(
				_persistedModelLocalService, "updateTestModel",
				_getAnnotations(PreviewableHolder.class)));
	}

	@Test
	public void testUnsupportedTarget() {
		Assert.assertNull(
			_createMethodContext(
				new Object(), "getTestModel",
				_getAnnotations(PreviewableHolder.class)));
	}

	private Object _createMethodContext(
		Object target, String methodName,
		Map<Class<? extends Annotation>, Annotation> annotations) {

		return _previewableAdvice.createMethodContext(
			target, ReflectionTestUtil.getMethod(TestService.class, methodName),
			annotations);
	}

	private TestPreviewableService _createProxy(TestModel testModel)
		throws Exception {

		Constructor<AopInvocationHandler> constructor =
			AopInvocationHandler.class.getDeclaredConstructor(
				Object.class, ChainableMethodAdvice[].class,
				TransactionExecutor.class);

		constructor.setAccessible(true);

		AopInvocationHandler aopInvocationHandler = constructor.newInstance(
			new TestPreviewableServiceImpl(_testModels, testModel),
			new ChainableMethodAdvice[] {new PreviewableAdvice()}, null);

		return (TestPreviewableService)ProxyUtil.newProxyInstance(
			PreviewableAdviceTest.class.getClassLoader(),
			new Class<?>[] {TestPreviewableService.class},
			aopInvocationHandler);
	}

	private Map<Class<? extends Annotation>, Annotation> _getAnnotations(
		Class<?> clazz) {

		return Collections.singletonMap(
			Previewable.class, clazz.getAnnotation(Previewable.class));
	}

	private SafeCloseable _openPreviewScope() {
		Long previewId = PreviewableResolverUtil.addPreviewableMap(
			Collections.<Class<?>, Map<Serializable, Serializable>>singletonMap(
				TestModel.class,
				Collections.<Serializable, Serializable>singletonMap(1L, 2L)));

		SafeCloseable safeCloseable =
			PreviewableResolverUtil.setPreviewIdWithSafeCloseable(previewId);

		return () -> {
			safeCloseable.close();

			PreviewableResolverUtil.removePreviewableMap(previewId);
		};
	}

	private Object _resolveRuntime(Object object) {
		return ReflectionTestUtil.invoke(
			PreviewableAdvice.class, "_resolveRuntime",
			new Class<?>[] {Object.class}, object);
	}

	private PersistedModelLocalService _persistedModelLocalService;
	private final PreviewableAdvice _previewableAdvice =
		new PreviewableAdvice();
	private ServiceRegistration<PersistedModelLocalService>
		_serviceRegistration;
	private final Map<Serializable, TestModel> _testModels = new HashMap<>();

	@Previewable(enabled = false)
	private static class DisabledPreviewableHolder {
	}

	@Previewable
	private static class PreviewableHolder {
	}

	private static class TestModel extends BaseTestModel<TestModel> {

		@Override
		public Class<?> getModelClass() {
			return TestModel.class;
		}

		private TestModel(Serializable primaryKey) {
			super(primaryKey);
		}

	}

	private static class TestPreviewableServiceImpl
		extends TestPersistedModelLocalService
		implements TestPreviewableService {

		@Override
		public TestModel getTestModel() {
			return _testModel;
		}

		private TestPreviewableServiceImpl(
			Map<Serializable, TestModel> testModels, TestModel testModel) {

			super(TestModel.class, testModels);

			_testModel = testModel;
		}

		private final TestModel _testModel;

	}

	@Previewable
	private interface TestPreviewableService
		extends PersistedModelLocalService {

		public TestModel getTestModel();

	}

	private interface TestService {

		public <T> T dslQuery();

		public int dslQueryCount();

		public <T> List<T> dynamicQuery();

		public TestModel getTestModel();

		public List<TestModel> getTestModels();

		public String getUnrelated();

		public TestModel updateTestModel();

	}

}