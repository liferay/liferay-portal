/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.internal.dao.orm;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Tina Tian
 */
public class EntityCacheImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Mockito.when(
			FrameworkUtil.getBundle(Mockito.any())
		).thenReturn(
			bundleContext.getBundle()
		);

		_finderCacheServiceRegistration = bundleContext.registerService(
			FinderCache.class, _finderCacheImpl, null);
	}

	@AfterClass
	public static void tearDownClass() {
		_finderCacheServiceRegistration.unregister();

		_frameworkUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		_classLoader = EntityCacheImplTest.class.getClassLoader();
		_nullModel = ReflectionTestUtil.getFieldValue(
			BasePersistenceImpl.class, "nullModel");
	}

	@Test
	public void testNotifyPortalCacheRemovedPortalCacheName() {
		EntityCacheImpl entityCacheImpl = new EntityCacheImpl();

		MultiVMPool multiVMPool = (MultiVMPool)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {MultiVMPool.class},
			new MultiVMPoolInvocationHandler(_classLoader, true));

		ReflectionTestUtil.setFieldValue(
			entityCacheImpl, "_clusterExecutor",
			ProxyFactory.newDummyInstance(ClusterExecutor.class));
		ReflectionTestUtil.setFieldValue(
			entityCacheImpl, "_multiVMPool", multiVMPool);

		ReflectionTestUtil.setFieldValue(
			_finderCacheImpl, "_multiVMPool", multiVMPool);
		ReflectionTestUtil.setFieldValue(
			_finderCacheImpl, "_serviceTrackerMap",
			ServiceTrackerMapFactory.openSingleValueMap(
				SystemBundleUtil.getBundleContext(), ArgumentsResolver.class,
				"class.name"));

		entityCacheImpl.activate(_bundleContext);

		PortalCache<?, ?> portalCache = entityCacheImpl.getPortalCache(
			EntityCacheImplTest.class);

		Map<String, PortalCache<Serializable, Serializable>> portalCaches =
			ReflectionTestUtil.getFieldValue(entityCacheImpl, "_portalCaches");

		Assert.assertEquals(portalCaches.toString(), 1, portalCaches.size());
		Assert.assertSame(
			portalCache, portalCaches.get(EntityCacheImplTest.class.getName()));

		entityCacheImpl.notifyPortalCacheRemoved(
			portalCache.getPortalCacheName());

		Assert.assertTrue(portalCaches.toString(), portalCaches.isEmpty());
	}

	@Test
	public void testPutAndGetNullModel() throws Exception {
		_testPutAndGetNullModel(false);
		_testPutAndGetNullModel(true);
	}

	private void _testPutAndGetNullModel(boolean serialized) {
		EntityCacheImpl entityCacheImpl = new EntityCacheImpl();

		ReflectionTestUtil.setFieldValue(
			entityCacheImpl, "_multiVMPool",
			ProxyUtil.newProxyInstance(
				_classLoader, new Class<?>[] {MultiVMPool.class},
				new MultiVMPoolInvocationHandler(_classLoader, serialized)));

		entityCacheImpl.activate(_bundleContext);

		entityCacheImpl.putResult(EntityCacheImplTest.class, 12345, _nullModel);

		Assert.assertSame(
			_nullModel,
			entityCacheImpl.getResult(EntityCacheImplTest.class, 12345));
	}

	private static final FinderCacheImpl _finderCacheImpl =
		new FinderCacheImpl();
	private static ServiceRegistration<FinderCache>
		_finderCacheServiceRegistration;
	private static final MockedStatic<FrameworkUtil>
		_frameworkUtilMockedStatic = Mockito.mockStatic(FrameworkUtil.class);

	private final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private ClassLoader _classLoader;
	private Serializable _nullModel;

}