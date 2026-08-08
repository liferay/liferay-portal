/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.module.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.module.util.ServiceLatch;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class ServiceLatchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(ServiceLatchTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@Test
	public void testCapturesTheFirstServiceOfEachAwaitedClass() {
		ServiceA serviceA = new ServiceA() {
		};
		ServiceB serviceB = new ServiceB() {
		};

		ServiceRegistration<ServiceA> serviceRegistration1 =
			_bundleContext.registerService(ServiceA.class, serviceA, null);
		ServiceRegistration<ServiceB> serviceRegistration2 =
			_bundleContext.registerService(ServiceB.class, serviceB, null);

		try {
			AtomicInteger openCounter = new AtomicInteger();

			AtomicReference<ServiceA> serviceAAtomicReference =
				new AtomicReference<>();
			AtomicReference<ServiceB> serviceBAtomicReference =
				new AtomicReference<>();

			ServiceLatch serviceLatch = new ServiceLatch(_bundleContext);

			serviceLatch.waitFor(ServiceA.class, serviceAAtomicReference::set);
			serviceLatch.waitFor(ServiceB.class, serviceBAtomicReference::set);

			serviceLatch.openOn(openCounter::incrementAndGet);

			Assert.assertEquals(1, openCounter.get());
			Assert.assertSame(serviceA, serviceAAtomicReference.get());
			Assert.assertSame(serviceB, serviceBAtomicReference.get());
		}
		finally {
			serviceRegistration1.unregister();
			serviceRegistration2.unregister();
		}
	}

	@Test
	public void testOpensAfterEveryAwaitedServiceWithDuplicates() {
		ServiceA serviceA1 = new ServiceA() {
		};
		ServiceA serviceA2 = new ServiceA() {
		};

		ServiceRegistration<ServiceA> serviceRegistration1 =
			_bundleContext.registerService(ServiceA.class, serviceA1, null);
		ServiceRegistration<ServiceA> serviceRegistration2 =
			_bundleContext.registerService(ServiceA.class, serviceA2, null);

		try {
			AtomicInteger openCounter = new AtomicInteger();

			List<ServiceA> serviceAs = new ArrayList<>();
			List<ServiceB> serviceBs = new ArrayList<>();

			ServiceLatch serviceLatch = new ServiceLatch(_bundleContext);

			serviceLatch.waitFor(ServiceA.class, serviceAs::add);
			serviceLatch.waitFor(ServiceB.class, serviceBs::add);

			serviceLatch.openOn(openCounter::incrementAndGet);

			Assert.assertEquals(serviceAs.toString(), 1, serviceAs.size());
			Assert.assertSame(serviceA1, serviceAs.get(0));
			Assert.assertEquals(serviceBs.toString(), 0, serviceBs.size());
			Assert.assertEquals(0, openCounter.get());

			ServiceB serviceB = new ServiceB() {
			};

			ServiceRegistration<ServiceB> serviceRegistration3 =
				_bundleContext.registerService(ServiceB.class, serviceB, null);

			try {
				Assert.assertEquals(1, openCounter.get());
				Assert.assertEquals(serviceAs.toString(), 1, serviceAs.size());
				Assert.assertSame(serviceA1, serviceAs.get(0));
				Assert.assertEquals(serviceBs.toString(), 1, serviceBs.size());
				Assert.assertSame(serviceB, serviceBs.get(0));
			}
			finally {
				serviceRegistration3.unregister();
			}
		}
		finally {
			serviceRegistration1.unregister();
			serviceRegistration2.unregister();
		}
	}

	@Test
	public void testOpensAfterEveryAwaitedServiceWithReregistration() {
		AtomicInteger openCounter = new AtomicInteger();

		List<ServiceA> serviceAs = new ArrayList<>();
		List<ServiceB> serviceBs = new ArrayList<>();

		ServiceLatch serviceLatch = new ServiceLatch(_bundleContext);

		serviceLatch.waitFor(ServiceA.class, serviceAs::add);
		serviceLatch.waitFor(ServiceB.class, serviceBs::add);

		serviceLatch.openOn(openCounter::incrementAndGet);

		ServiceA serviceA1 = new ServiceA() {
		};

		ServiceRegistration<ServiceA> serviceRegistration1 =
			_bundleContext.registerService(ServiceA.class, serviceA1, null);

		serviceRegistration1.unregister();

		ServiceA serviceA2 = new ServiceA() {
		};

		ServiceRegistration<ServiceA> serviceRegistration2 =
			_bundleContext.registerService(ServiceA.class, serviceA2, null);

		try {
			Assert.assertEquals(serviceAs.toString(), 1, serviceAs.size());
			Assert.assertSame(serviceA1, serviceAs.get(0));
			Assert.assertEquals(0, openCounter.get());

			ServiceB serviceB = new ServiceB() {
			};

			ServiceRegistration<ServiceB> serviceRegistration3 =
				_bundleContext.registerService(ServiceB.class, serviceB, null);

			try {
				Assert.assertEquals(1, openCounter.get());
				Assert.assertEquals(serviceAs.toString(), 1, serviceAs.size());
				Assert.assertSame(serviceA1, serviceAs.get(0));
				Assert.assertEquals(serviceBs.toString(), 1, serviceBs.size());
				Assert.assertSame(serviceB, serviceBs.get(0));
			}
			finally {
				serviceRegistration3.unregister();
			}
		}
		finally {
			serviceRegistration2.unregister();
		}
	}

	private BundleContext _bundleContext;

	private interface ServiceA {
	}

	private interface ServiceB {
	}

}