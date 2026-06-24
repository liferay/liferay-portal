/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.preview;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class PreviewableResolverUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			PersistedModelLocalService.class,
			new TestPersistedModelLocalService(TestModel.class, _testModels),
			MapUtil.singletonDictionary(
				"model.class.name", TestModel.class.getName()));
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetPreviewableMap() {
		Map<Serializable, Serializable> pkMap =
			Collections.<Serializable, Serializable>singletonMap(1L, 2L);

		Long previewId = PreviewableResolverUtil.addPreviewableMap(
			Collections.<Class<?>, Map<Serializable, Serializable>>singletonMap(
				TestModel.class, pkMap));

		try {
			Assert.assertNull(
				PreviewableResolverUtil.getPreviewableMap(TestModel.class));

			try (SafeCloseable safeCloseable =
					PreviewableResolverUtil.setPreviewIdWithSafeCloseable(
						previewId)) {

				Assert.assertSame(
					pkMap,
					PreviewableResolverUtil.getPreviewableMap(TestModel.class));
				Assert.assertNull(
					PreviewableResolverUtil.getPreviewableMap(String.class));
			}

			Assert.assertNull(
				PreviewableResolverUtil.getPreviewableMap(TestModel.class));
		}
		finally {
			PreviewableResolverUtil.removePreviewableMap(previewId);
		}
	}

	@Test
	public void testGetPreviewIds() {
		Long previewId = PreviewableResolverUtil.addPreviewableMap(
			Collections.emptyMap());

		try {
			Set<Long> previewIds = PreviewableResolverUtil.getPreviewIds();

			Assert.assertTrue(
				previewIds.toString(), previewIds.contains(previewId));
		}
		finally {
			PreviewableResolverUtil.removePreviewableMap(previewId);
		}

		Set<Long> previewIds = PreviewableResolverUtil.getPreviewIds();

		Assert.assertFalse(
			previewIds.toString(), previewIds.contains(previewId));
	}

	@Test
	public void testRemovePreviewableMap() {
		Map<Class<?>, Map<Serializable, Serializable>> previewableMap =
			_createPreviewableMap(1L, 2L);

		Long previewId = PreviewableResolverUtil.addPreviewableMap(
			previewableMap);

		Assert.assertSame(
			previewableMap,
			PreviewableResolverUtil.removePreviewableMap(previewId));
		Assert.assertNull(
			PreviewableResolverUtil.removePreviewableMap(previewId));
	}

	@Test
	public void testResolveBaseModelWithMappedPrimaryKey() {
		TestModel fromTestModel = new TestModel(1L);

		TestModel toTestModel = new TestModel(2L);

		_testModels.put(2L, toTestModel);

		Long previewId = PreviewableResolverUtil.addPreviewableMap(
			_createPreviewableMap(1L, 2L));

		try (SafeCloseable safeCloseable =
				PreviewableResolverUtil.setPreviewIdWithSafeCloseable(
					previewId)) {

			Assert.assertSame(
				toTestModel, PreviewableResolverUtil.resolve(fromTestModel));
		}
		finally {
			PreviewableResolverUtil.removePreviewableMap(previewId);
		}
	}

	@Test
	public void testResolveBaseModelWithMissingTarget() {
		TestModel fromTestModel = new TestModel(1L);

		Long previewId = PreviewableResolverUtil.addPreviewableMap(
			_createPreviewableMap(1L, 2L));

		try (SafeCloseable safeCloseable =
				PreviewableResolverUtil.setPreviewIdWithSafeCloseable(
					previewId)) {

			PreviewableResolverUtil.resolve(fromTestModel);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertSame(NoSuchModelException.class, exception.getClass());
			Assert.assertEquals("2", exception.getMessage());
		}
		finally {
			PreviewableResolverUtil.removePreviewableMap(previewId);
		}
	}

	@Test
	public void testResolveBaseModelWithoutPreviewId() {
		TestModel testModel = new TestModel(1L);

		Assert.assertSame(
			testModel, PreviewableResolverUtil.resolve(testModel));
	}

	@Test
	public void testResolveBaseModelWithUnmappedPrimaryKey() {
		TestModel testModel = new TestModel(3L);

		Long previewId = PreviewableResolverUtil.addPreviewableMap(
			_createPreviewableMap(1L, 2L));

		try (SafeCloseable safeCloseable =
				PreviewableResolverUtil.setPreviewIdWithSafeCloseable(
					previewId)) {

			Assert.assertSame(
				testModel, PreviewableResolverUtil.resolve(testModel));
		}
		finally {
			PreviewableResolverUtil.removePreviewableMap(previewId);
		}
	}

	@Test
	public void testResolveCollectionWithMappedPrimaryKey() {
		TestModel fromTestModel = new TestModel(1L);
		TestModel toTestModel = new TestModel(2L);
		TestModel unmappedTestModel = new TestModel(3L);

		_testModels.put(2L, toTestModel);

		Long previewId = PreviewableResolverUtil.addPreviewableMap(
			_createPreviewableMap(1L, 2L));

		try (SafeCloseable safeCloseable =
				PreviewableResolverUtil.setPreviewIdWithSafeCloseable(
					previewId)) {

			List<BaseModel<?>> toBaseModels = new ArrayList<>();

			Assert.assertSame(
				toBaseModels,
				PreviewableResolverUtil.resolve(
					Arrays.<BaseModel<?>>asList(
						fromTestModel, unmappedTestModel),
					toBaseModels));
			Assert.assertEquals(
				Arrays.<BaseModel<?>>asList(toTestModel, unmappedTestModel),
				toBaseModels);
		}
		finally {
			PreviewableResolverUtil.removePreviewableMap(previewId);
		}
	}

	@Test
	public void testResolveCollectionWithMissingTarget() {
		TestModel fromTestModel = new TestModel(1L);

		Long previewId = PreviewableResolverUtil.addPreviewableMap(
			_createPreviewableMap(1L, 2L));

		try (SafeCloseable safeCloseable =
				PreviewableResolverUtil.setPreviewIdWithSafeCloseable(
					previewId)) {

			PreviewableResolverUtil.resolve(
				Arrays.<BaseModel<?>>asList(fromTestModel), new ArrayList<>());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertSame(NoSuchModelException.class, exception.getClass());
			Assert.assertEquals("2", exception.getMessage());
		}
		finally {
			PreviewableResolverUtil.removePreviewableMap(previewId);
		}
	}

	@Test
	public void testResolveCollectionWithoutPreviewId() {
		List<BaseModel<?>> fromBaseModels = Arrays.<BaseModel<?>>asList(
			new TestModel(1L));

		Assert.assertSame(
			fromBaseModels,
			PreviewableResolverUtil.resolve(fromBaseModels, new ArrayList<>()));
	}

	@Test
	public void testSetPreviewIdWithSafeCloseable() {
		Assert.assertNull(PreviewableResolverUtil.getPreviewId());

		try (SafeCloseable safeCloseable1 =
				PreviewableResolverUtil.setPreviewIdWithSafeCloseable(1L)) {

			Assert.assertEquals(
				Long.valueOf(1), PreviewableResolverUtil.getPreviewId());

			try (SafeCloseable safeCloseable2 =
					PreviewableResolverUtil.setPreviewIdWithSafeCloseable(2L)) {

				Assert.assertEquals(
					Long.valueOf(2), PreviewableResolverUtil.getPreviewId());
			}

			Assert.assertEquals(
				Long.valueOf(1), PreviewableResolverUtil.getPreviewId());
		}

		Assert.assertNull(PreviewableResolverUtil.getPreviewId());
	}

	private Map<Class<?>, Map<Serializable, Serializable>>
		_createPreviewableMap(
			Serializable fromPrimaryKey, Serializable toPrimaryKey) {

		return Collections.
			<Class<?>, Map<Serializable, Serializable>>singletonMap(
				TestModel.class,
				Collections.<Serializable, Serializable>singletonMap(
					fromPrimaryKey, toPrimaryKey));
	}

	private ServiceRegistration<PersistedModelLocalService>
		_serviceRegistration;
	private final Map<Serializable, TestModel> _testModels = new HashMap<>();

	private static class TestModel extends BaseTestModel<TestModel> {

		@Override
		public Class<?> getModelClass() {
			return TestModel.class;
		}

		private TestModel(Serializable primaryKey) {
			super(primaryKey);
		}

	}

}