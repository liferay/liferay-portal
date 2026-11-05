/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.integration.point.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.xstream.XStreamConverter;
import com.liferay.exportimport.kernel.xstream.XStreamConverterRegistryUtil;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.AuthToken;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGeneratorFactory;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ProxyFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Dante Wang
 */
@RunWith(Arquillian.class)
public class IntegrationPointTest {

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(IntegrationPointTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@After
	public void tearDown() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testServiceTrackerCustomizerIntegrationPoint() {
		XStreamConverter xStreamConverter = ProxyFactory.newDummyInstance(
			XStreamConverter.class);

		_serviceRegistration = _bundleContext.registerService(
			XStreamConverter.class, xStreamConverter, null);

		Set<XStreamConverter> xStreamConverters =
			XStreamConverterRegistryUtil.getXStreamConverters();

		Assert.assertTrue(
			"Mock XStreamConverter not found in " + xStreamConverters,
			xStreamConverters.removeIf(item -> xStreamConverter == item));
	}

	@Test
	public void testServiceTrackerIntegrationPoint() {
		FullNameGenerator fullNameGenerator = ProxyFactory.newDummyInstance(
			FullNameGenerator.class);

		_serviceRegistration = _bundleContext.registerService(
			FullNameGenerator.class, fullNameGenerator,
			MapUtil.singletonDictionary("service.ranking", Integer.MAX_VALUE));

		Assert.assertSame(
			fullNameGenerator, FullNameGeneratorFactory.getInstance());
	}

	@Test
	public void testServiceTrackerListIntegrationPoint() {
		Sanitizer sanitizer = ProxyFactory.newDummyInstance(Sanitizer.class);

		_serviceRegistration = _bundleContext.registerService(
			Sanitizer.class, sanitizer, null);

		List<Sanitizer> sanitizers = new ArrayList<>();

		ServiceTrackerList<Sanitizer> serviceTrackerList =
			ReflectionTestUtil.getFieldValue(
				SanitizerUtil.class, "_sanitizers");

		serviceTrackerList.forEach(sanitizers::add);

		Assert.assertTrue(
			"Mock sanitizer not found in " + sanitizers,
			sanitizers.removeIf(item -> sanitizer == item));
	}

	@Test
	public void testSnapshotIntegrationPoint() {
		AuthToken authToken = ProxyFactory.newDummyInstance(AuthToken.class);

		_serviceRegistration = _bundleContext.registerService(
			AuthToken.class, authToken,
			MapUtil.singletonDictionary("service.ranking", Integer.MAX_VALUE));

		Snapshot authTokenSnapshot = (Snapshot)ReflectionTestUtil.getFieldValue(
			AuthTokenUtil.class, "_authTokenSnapshot");

		Assert.assertSame(authToken, authTokenSnapshot.get());
	}

	private static BundleContext _bundleContext;

	private ServiceRegistration<?> _serviceRegistration;

}