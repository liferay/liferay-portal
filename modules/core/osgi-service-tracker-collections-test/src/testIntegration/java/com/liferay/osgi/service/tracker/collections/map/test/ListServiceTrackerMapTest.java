/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osgi.service.tracker.collections.map.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapListener;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Carlos Sierra Andrés
 */
@RunWith(Arquillian.class)
public class ListServiceTrackerMapTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			ListServiceTrackerMapTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@After
	public void tearDown() {
		_serviceTrackerMap.close();

		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();
	}

	@Test
	public void testGestServiceWithDifferentRanking() {
		_serviceTrackerMap = createServiceTrackerMap(_bundleContext);

		TrackedOne trackedOne1 = new TrackedOne();

		_serviceRegistrations.add(registerService(trackedOne1, 1));

		TrackedOne trackedOne3 = new TrackedOne();

		_serviceRegistrations.add(registerService(trackedOne3, 3));

		TrackedOne trackedOne2 = new TrackedOne();

		_serviceRegistrations.add(registerService(trackedOne2, 2));

		List<TrackedOne> trackedOnes = _serviceTrackerMap.getService("aTarget");

		Assert.assertEquals(trackedOnes.toString(), 3, trackedOnes.size());

		Iterator<? extends TrackedOne> iterator = trackedOnes.iterator();

		Assert.assertEquals(trackedOne3, iterator.next());
		Assert.assertEquals(trackedOne2, iterator.next());
		Assert.assertEquals(trackedOne1, iterator.next());
	}

	@Test
	public void testGestServiceWithUnregistering() {
		_serviceTrackerMap = createServiceTrackerMap(_bundleContext);

		TrackedOne trackedOne1 = new TrackedOne();

		_serviceRegistrations.add(registerService(trackedOne1, 1));

		TrackedOne trackedOne3 = new TrackedOne();

		_serviceRegistrations.add(registerService(trackedOne3, 3));

		TrackedOne trackedOne2 = new TrackedOne();

		ServiceRegistration<TrackedOne> serviceRegistration2 = registerService(
			trackedOne2, 2);

		List<TrackedOne> trackedOnes = _serviceTrackerMap.getService("aTarget");

		Assert.assertEquals(trackedOnes.toString(), 3, trackedOnes.size());

		Iterator<? extends TrackedOne> iterator = trackedOnes.iterator();

		serviceRegistration2.unregister();

		// Deregistering a service should not affect an already existing
		// collection or iterator

		Assert.assertEquals(trackedOne3, iterator.next());
		Assert.assertEquals(trackedOne2, iterator.next());
		Assert.assertEquals(trackedOne1, iterator.next());

		trackedOnes = _serviceTrackerMap.getService("aTarget");

		// Getting the list of services should return a list with the affected
		// changes

		Assert.assertEquals(trackedOnes.toString(), 2, trackedOnes.size());

		iterator = trackedOnes.iterator();

		Assert.assertEquals(trackedOne3, iterator.next());
		Assert.assertEquals(trackedOne1, iterator.next());
	}

	@Test
	public void testGestServiceWithUnregisteringAndCustomComparator() {
		_serviceTrackerMap = createServiceTrackerMap(
			_bundleContext, (serviceReference1, serviceReference2) -> 0);

		TrackedOne trackedOne1 = new TrackedOne();

		ServiceRegistration<TrackedOne> serviceRegistration1 = registerService(
			trackedOne1);

		ServiceRegistration<TrackedOne> serviceRegistration2 = registerService(
			new TrackedOne());

		TrackedOne trackedOne3 = new TrackedOne();

		ServiceRegistration<TrackedOne> serviceRegistration3 = registerService(
			trackedOne3);

		serviceRegistration2.unregister();

		List<TrackedOne> trackedOnes = _serviceTrackerMap.getService("aTarget");

		// Getting the list of services should return a list with the affected
		// changes

		Assert.assertEquals(trackedOnes.toString(), 2, trackedOnes.size());
		Assert.assertTrue(
			trackedOnes.toString(), trackedOnes.contains(trackedOne1));
		Assert.assertTrue(
			trackedOnes.toString(), trackedOnes.contains(trackedOne3));

		serviceRegistration3.unregister();

		trackedOnes = _serviceTrackerMap.getService("aTarget");

		Assert.assertEquals(trackedOnes.toString(), 1, trackedOnes.size());

		Assert.assertTrue(
			trackedOnes.toString(), trackedOnes.contains(trackedOne1));

		serviceRegistration1.unregister();
	}

	@Test
	public void testGetServiceWithChangingServiceRanking() {
		_serviceTrackerMap = createServiceTrackerMap(_bundleContext);

		TrackedOne trackedOne1 = new TrackedOne();

		ServiceRegistration<TrackedOne> serviceRegistration1 = registerService(
			trackedOne1, -1);

		TrackedOne trackedOne2 = new TrackedOne();

		ServiceRegistration<TrackedOne> serviceRegistration2 = registerService(
			trackedOne2, 0);

		TrackedOne trackedOne3 = new TrackedOne();

		ServiceRegistration<TrackedOne> serviceRegistration3 = registerService(
			trackedOne3, 1);

		List<TrackedOne> trackedOnes = _serviceTrackerMap.getService("aTarget");

		Assert.assertEquals(trackedOnes.toString(), 3, trackedOnes.size());

		Iterator<? extends TrackedOne> iterator = trackedOnes.iterator();

		Assert.assertEquals(trackedOne3, iterator.next());
		Assert.assertEquals(trackedOne2, iterator.next());
		Assert.assertEquals(trackedOne1, iterator.next());

		Hashtable<String, Object> properties = new Hashtable<>();

		properties.put("service.ranking", -2);
		properties.put("target", "aTarget");

		serviceRegistration3.setProperties(properties);

		trackedOnes = _serviceTrackerMap.getService("aTarget");

		Assert.assertEquals(trackedOnes.toString(), 3, trackedOnes.size());

		iterator = trackedOnes.iterator();

		Assert.assertEquals(trackedOne2, iterator.next());
		Assert.assertEquals(trackedOne1, iterator.next());
		Assert.assertEquals(trackedOne3, iterator.next());

		properties = new Hashtable<>();

		properties.put("service.ranking", 1);
		properties.put("target", "aTarget");

		serviceRegistration3.setProperties(properties);

		trackedOnes = _serviceTrackerMap.getService("aTarget");

		Assert.assertEquals(trackedOnes.toString(), 3, trackedOnes.size());

		iterator = trackedOnes.iterator();

		Assert.assertEquals(trackedOne3, iterator.next());
		Assert.assertEquals(trackedOne2, iterator.next());
		Assert.assertEquals(trackedOne1, iterator.next());

		properties = new Hashtable<>();

		properties.put("target", "aTarget");

		serviceRegistration2.setProperties(properties);
		serviceRegistration3.setProperties(properties);

		trackedOnes = _serviceTrackerMap.getService("aTarget");

		Assert.assertEquals(trackedOnes.toString(), 3, trackedOnes.size());

		iterator = trackedOnes.iterator();

		Assert.assertEquals(trackedOne2, iterator.next());
		Assert.assertEquals(trackedOne3, iterator.next());
		Assert.assertEquals(trackedOne1, iterator.next());

		serviceRegistration1.unregister();
		serviceRegistration2.unregister();
		serviceRegistration3.unregister();
	}

	@Test
	public void testGetServiceWithCustomComparatorReturningZero() {
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			_bundleContext, TrackedOne.class, null,
			new PropertyServiceReferenceMapper<>("target"),
			(serviceReference1, serviceReference2) -> 0);

		_serviceRegistrations.add(registerService(new TrackedOne()));
		_serviceRegistrations.add(registerService(new TrackedOne()));

		List<TrackedOne> trackedOnes = _serviceTrackerMap.getService("aTarget");

		Assert.assertEquals(trackedOnes.toString(), 2, trackedOnes.size());
	}

	@Test
	public void testGetServiceWithRegisteredServiceRanking() {
		_serviceTrackerMap = createServiceTrackerMap(_bundleContext);

		TrackedOne trackedOne1 = new TrackedOne();

		ServiceRegistration<TrackedOne> serviceRegistration1 = registerService(
			trackedOne1);

		TrackedOne trackedOne2 = new TrackedOne();

		ServiceRegistration<TrackedOne> serviceRegistration2 = registerService(
			trackedOne2, 0);

		List<TrackedOne> trackedOnes = _serviceTrackerMap.getService("aTarget");

		Assert.assertEquals(trackedOnes.toString(), 2, trackedOnes.size());

		Iterator<TrackedOne> iterator = trackedOnes.iterator();

		Assert.assertEquals(trackedOne1, iterator.next());
		Assert.assertEquals(trackedOne2, iterator.next());

		serviceRegistration1.unregister();

		serviceRegistration1 = registerService(trackedOne1);

		TrackedOne trackedOne3 = new TrackedOne();

		ServiceRegistration<TrackedOne> serviceRegistration3 = registerService(
			trackedOne3, 1);

		trackedOnes = _serviceTrackerMap.getService("aTarget");

		Assert.assertEquals(trackedOnes.toString(), 3, trackedOnes.size());

		iterator = trackedOnes.iterator();

		Assert.assertEquals(trackedOne3, iterator.next());
		Assert.assertEquals(trackedOne2, iterator.next());
		Assert.assertEquals(trackedOne1, iterator.next());

		serviceRegistration1.unregister();
		serviceRegistration2.unregister();
		serviceRegistration3.unregister();
	}

	@Test
	public void testGetServiceWithSimpleRegistration() {
		_serviceTrackerMap = createServiceTrackerMap(_bundleContext);

		_serviceRegistrations.add(registerService(new TrackedOne()));

		List<TrackedOne> trackedOnes = _serviceTrackerMap.getService("aTarget");

		Assert.assertEquals(trackedOnes.toString(), 1, trackedOnes.size());
	}

	@Test
	public void testGetServiceWithSimpleRegistrationTwice() {
		_serviceTrackerMap = createServiceTrackerMap(_bundleContext);

		_serviceRegistrations.add(registerService(new TrackedOne()));
		_serviceRegistrations.add(registerService(new TrackedOne()));

		List<TrackedOne> trackedOnes = _serviceTrackerMap.getService("aTarget");

		Assert.assertEquals(trackedOnes.toString(), 2, trackedOnes.size());
	}

	@Test
	public void testGetServicesIsNullAfterDeregistration() {
		_serviceTrackerMap = createServiceTrackerMap(_bundleContext);

		ServiceRegistration<TrackedOne> serviceRegistration1 = registerService(
			new TrackedOne());
		ServiceRegistration<TrackedOne> serviceRegistration2 = registerService(
			new TrackedOne());

		Assert.assertNotNull(_serviceTrackerMap.getService("aTarget"));

		serviceRegistration1.unregister();
		serviceRegistration2.unregister();

		Assert.assertNull(_serviceTrackerMap.getService("aTarget"));
	}

	@Test
	public void testGetServicesWithDifferentKeys() {
		_serviceTrackerMap = createServiceTrackerMap(_bundleContext);

		_serviceRegistrations.add(registerService(new TrackedOne(), "aTarget"));
		_serviceRegistrations.add(
			registerService(new TrackedOne(), "anotherTarget"));
		_serviceRegistrations.add(registerService(new TrackedOne(), "aTarget"));
		_serviceRegistrations.add(
			registerService(new TrackedOne(), "anotherTarget"));
		_serviceRegistrations.add(
			registerService(new TrackedOne(), "anotherTarget"));

		List<TrackedOne> aTargetList = _serviceTrackerMap.getService("aTarget");

		Assert.assertNotNull(aTargetList);
		Assert.assertEquals(aTargetList.toString(), 2, aTargetList.size());

		List<TrackedOne> anotherTargetList = _serviceTrackerMap.getService(
			"anotherTarget");

		Assert.assertNotNull(anotherTargetList);
		Assert.assertEquals(
			anotherTargetList.toString(), 3, anotherTargetList.size());
	}

	@Test
	public void testOperationBalancesOutGetServiceAndUngetService() {
		BundleContextWrapper bundleContextWrapper = wrapContext();

		_serviceTrackerMap = createServiceTrackerMap(bundleContextWrapper);

		ServiceRegistration<TrackedOne> serviceRegistration1 = registerService(
			new TrackedOne());

		ServiceRegistration<TrackedOne> serviceRegistration2 = registerService(
			new TrackedOne());

		serviceRegistration2.unregister();

		serviceRegistration2 = registerService(new TrackedOne());

		serviceRegistration2.unregister();

		serviceRegistration1.unregister();

		Map<ServiceReference<?>, AtomicInteger> serviceReferenceCountsMap =
			bundleContextWrapper.getServiceReferenceCountsMap();

		Collection<AtomicInteger> serviceReferenceCounts =
			serviceReferenceCountsMap.values();

		Assert.assertEquals(
			serviceReferenceCounts.toString(), 3,
			serviceReferenceCounts.size());

		for (AtomicInteger serviceReferenceCount : serviceReferenceCounts) {
			Assert.assertEquals(0, serviceReferenceCount.get());
		}
	}

	@Test
	public void testServiceRegistrationInvokesServiceTrackerMapListener() {
		final Collection<TrackedOne> trackedOnes = new ArrayList<>();

		ServiceTrackerMapListener<String, TrackedOne, List<TrackedOne>>
			serviceTrackerMapListener =
				new ServiceTrackerMapListener
					<String, TrackedOne, List<TrackedOne>>() {

					@Override
					public void keyEmitted(
						ServiceTrackerMap<String, List<TrackedOne>>
							serviceTrackerMap,
						String key, TrackedOne serviceTrackedOne,
						List<TrackedOne> contentTrackedOnes) {

						trackedOnes.add(serviceTrackedOne);
					}

					@Override
					public void keyRemoved(
						ServiceTrackerMap<String, List<TrackedOne>>
							serviceTrackerMap,
						String key, TrackedOne serviceTrackedOne,
						List<TrackedOne> contentTrackedOnes) {
					}

				};

		_serviceTrackerMap = createServiceTrackerMap(serviceTrackerMapListener);

		_serviceRegistrations.add(registerService(new TrackedOne()));

		Assert.assertEquals(trackedOnes.toString(), 1, trackedOnes.size());
	}

	@Test
	public void testServiceTrackerMapListenerCannotModifyContent() {
		ServiceTrackerMapListener<String, TrackedOne, List<TrackedOne>>
			serviceTrackerMapListener =
				new ServiceTrackerMapListener
					<String, TrackedOne, List<TrackedOne>>() {

					@Override
					public void keyEmitted(
						ServiceTrackerMap<String, List<TrackedOne>>
							serviceTrackerMap,
						String key, TrackedOne serviceTrackedOne,
						List<TrackedOne> contentTrackedOnes) {

						try {
							contentTrackedOnes.add(new TrackedOne("spurious"));
						}
						catch (Exception exception) {
						}
					}

					@Override
					public void keyRemoved(
						ServiceTrackerMap<String, List<TrackedOne>>
							serviceTrackerMap,
						String key, TrackedOne serviceTrackedOne,
						List<TrackedOne> contentTrackedOnes) {
					}

				};

		_serviceTrackerMap = createServiceTrackerMap(serviceTrackerMapListener);

		_serviceRegistrations.add(registerService(new TrackedOne(), "aTarget"));

		List<TrackedOne> trackedOnes = _serviceTrackerMap.getService("aTarget");

		Assert.assertEquals(trackedOnes.toString(), 1, trackedOnes.size());
	}

	@Test
	public void testServiceTrackerMapListenerKeyEmitted() throws Throwable {
		final TrackedOne trackedOne = new TrackedOne();

		final Collection<Throwable> throwables = new ArrayList<>();

		ServiceTrackerMapListener<String, TrackedOne, List<TrackedOne>>
			serviceTrackerMapListener =
				new ServiceTrackerMapListener
					<String, TrackedOne, List<TrackedOne>>() {

					@Override
					public void keyEmitted(
						ServiceTrackerMap<String, List<TrackedOne>>
							serviceTrackerMap,
						String key, TrackedOne serviceTrackedOne,
						List<TrackedOne> contentTrackedOnes) {

						try {
							Assert.assertEquals("aTarget", key);
							Assert.assertEquals(trackedOne, serviceTrackedOne);
							Assert.assertEquals(
								contentTrackedOnes, Arrays.asList(trackedOne));
						}
						catch (Throwable throwable) {
							throwables.add(throwable);
						}
					}

					@Override
					public void keyRemoved(
						ServiceTrackerMap<String, List<TrackedOne>>
							serviceTrackerMap,
						String key, TrackedOne serviceTrackedOne,
						List<TrackedOne> contentTrackedOnes) {
					}

				};

		_serviceTrackerMap = createServiceTrackerMap(serviceTrackerMapListener);

		_serviceRegistrations.add(registerService(trackedOne, "aTarget"));

		for (Throwable throwable : throwables) {
			throw throwable;
		}
	}

	@Test
	public void testUnkeyedServiceReferencesBalanceReferenceCount() {
		BundleContextWrapper bundleContextWrapper = wrapContext();

		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContextWrapper, TrackedOne.class, null,
			(serviceReference, emitter) -> {
			});

		ServiceRegistration<TrackedOne> serviceRegistration1 = registerService(
			new TrackedOne());
		ServiceRegistration<TrackedOne> serviceRegistration2 = registerService(
			new TrackedOne());

		Map<ServiceReference<?>, AtomicInteger> serviceReferenceCountsMap =
			bundleContextWrapper.getServiceReferenceCountsMap();

		Collection<AtomicInteger> serviceReferenceCounts =
			serviceReferenceCountsMap.values();

		Assert.assertEquals(
			serviceReferenceCounts.toString(), 0,
			serviceReferenceCounts.size());

		serviceRegistration1.unregister();
		serviceRegistration2.unregister();

		Assert.assertEquals(
			serviceReferenceCounts.toString(), 0,
			serviceReferenceCounts.size());
	}

	protected ServiceTrackerMap<String, List<TrackedOne>>
		createServiceTrackerMap(BundleContext bundleContext) {

		ServiceTrackerMap<String, List<TrackedOne>> serviceTrackerMap =
			ServiceTrackerMapFactory.openMultiValueMap(
				bundleContext, TrackedOne.class, "target");

		serviceTrackerMap.keySet();

		return serviceTrackerMap;
	}

	protected ServiceTrackerMap<String, List<TrackedOne>>
		createServiceTrackerMap(
			BundleContext bundleContext,
			Comparator<ServiceReference<TrackedOne>> comparator) {

		ServiceTrackerMap<String, List<TrackedOne>> serviceTrackerMap =
			ServiceTrackerMapFactory.openMultiValueMap(
				bundleContext, TrackedOne.class, null,
				new PropertyServiceReferenceMapper<>("target"), comparator);

		serviceTrackerMap.keySet();

		return serviceTrackerMap;
	}

	protected ServiceTrackerMap<String, List<TrackedOne>>
		createServiceTrackerMap(
			ServiceTrackerMapListener<String, TrackedOne, List<TrackedOne>>
				serviceTrackerMapListener) {

		ServiceTrackerMap<String, List<TrackedOne>> serviceTrackerMap =
			ServiceTrackerMapFactory.openMultiValueMap(
				_bundleContext, TrackedOne.class, null,
				new PropertyServiceReferenceMapper<>("target"),
				serviceTrackerMapListener);

		serviceTrackerMap.keySet();

		return serviceTrackerMap;
	}

	protected ServiceRegistration<TrackedOne> registerService(
		TrackedOne trackedOne) {

		return registerService(trackedOne, "aTarget");
	}

	protected ServiceRegistration<TrackedOne> registerService(
		TrackedOne trackedOne, int ranking) {

		return registerService(trackedOne, ranking, "aTarget");
	}

	protected ServiceRegistration<TrackedOne> registerService(
		TrackedOne trackedOne, int ranking, String target) {

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("service.ranking", ranking);
		properties.put("target", target);

		return _bundleContext.registerService(
			TrackedOne.class, trackedOne, properties);
	}

	protected ServiceRegistration<TrackedOne> registerService(
		TrackedOne trackedOne, String target) {

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("target", target);

		return _bundleContext.registerService(
			TrackedOne.class, trackedOne, properties);
	}

	protected BundleContextWrapper wrapContext() {
		return new BundleContextWrapper(_bundleContext);
	}

	private BundleContext _bundleContext;
	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();
	private ServiceTrackerMap<String, List<TrackedOne>> _serviceTrackerMap;

}