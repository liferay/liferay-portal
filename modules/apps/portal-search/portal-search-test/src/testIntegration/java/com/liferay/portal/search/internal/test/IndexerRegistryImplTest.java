/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.TestIndexer;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Gregory Amerson
 */
@RunWith(Arquillian.class)
public class IndexerRegistryImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(IndexerRegistryImplTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			(Class<Indexer<?>>)(Class<?>)Indexer.class,
			new TestIndexer(_CLASS_NAME), null);
	}

	@AfterClass
	public static void tearDownClass() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testGetIndexerByIndexerClassName() {
		Indexer<Object> testIndexer = _indexerRegistry.getIndexer(
			TestIndexer.class.getName());

		Assert.assertNotNull(testIndexer);
		Assert.assertSame(TestIndexer.class, testIndexer.getClass());
	}

	@Test
	public void testGetIndexerByModelClassName() {
		Indexer<Object> testIndexer = _indexerRegistry.getIndexer(_CLASS_NAME);

		Assert.assertNotNull(testIndexer);
		Assert.assertEquals(_CLASS_NAME, testIndexer.getClassName());
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private static final String _CLASS_NAME = RandomTestUtil.randomString();

	private static ServiceRegistration<Indexer<?>> _serviceRegistration;

	@Inject
	private IndexerRegistry _indexerRegistry;

}