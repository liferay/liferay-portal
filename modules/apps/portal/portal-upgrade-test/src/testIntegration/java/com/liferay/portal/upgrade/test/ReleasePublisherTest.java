/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.model.impl.ReleaseImpl;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.util.Collection;

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
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.util.promise.Promise;

/**
 * @author Alberto Chaparro
 */
@RunWith(Arquillian.class)
public class ReleasePublisherTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(ReleasePublisherTest.class);

		_bundleSymbolicName = bundle.getSymbolicName();

		_bundleContext = bundle.getBundleContext();
	}

	@After
	public void tearDown() throws Exception {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testNoPublishFailedReleaseOnActivation() throws Exception {
		_addReleaseBeforeActivation(ReleaseConstants.STATE_UPGRADE_FAILURE);

		Assert.assertEquals(0, _getReleaseServiceReferences().size());
	}

	@Test
	public void testNoPublishFailedUpgrade() throws Exception {
		_registerUpgradeProcesses(true);

		Assert.assertEquals(0, _getReleaseServiceReferences().size());
	}

	@Test
	public void testPublishOnActivation() throws Exception {
		_addReleaseBeforeActivation(ReleaseConstants.STATE_GOOD);

		Assert.assertEquals(1, _getReleaseServiceReferences().size());
	}

	@Test
	public void testPublishUpgrade() throws Exception {
		_registerUpgradeProcesses(false);

		Assert.assertEquals(1, _getReleaseServiceReferences().size());
	}

	@Test
	public void testUnPublishFailedUpgrade() throws Exception {
		Release release = new ReleaseImpl();

		release.setServletContextName(_bundleSymbolicName);
		release.setSchemaVersion("0.0.0");

		ReflectionTestUtil.invoke(
			_releasePublisher, "publish",
			new Class<?>[] {Release.class, boolean.class}, release, false);

		Assert.assertEquals(1, _getReleaseServiceReferences().size());

		_registerUpgradeProcesses(true);

		Assert.assertEquals(0, _getReleaseServiceReferences().size());
	}

	private void _addReleaseBeforeActivation(int state) throws Exception {
		Class<?> clazz = _releasePublisher.getClass();

		ComponentDescriptionDTO componentDescriptionDTO =
			_serviceComponentRuntime.getComponentDescriptionDTO(
				FrameworkUtil.getBundle(_releasePublisher.getClass()),
				clazz.getName());

		Promise<?> promise = _serviceComponentRuntime.disableComponent(
			componentDescriptionDTO);

		promise.getValue();

		_release = _releaseLocalService.addRelease(
			_bundleSymbolicName, "0.0.0");

		_release.setState(state);

		_release = _releaseLocalService.updateRelease(_release);

		promise = _serviceComponentRuntime.enableComponent(
			componentDescriptionDTO);

		promise.getValue();
	}

	private Collection<ServiceReference<Release>> _getReleaseServiceReferences()
		throws Exception {

		return _bundleContext.getServiceReferences(
			Release.class,
			"(release.bundle.symbolic.name=" + _bundleSymbolicName + ")");
	}

	private void _registerUpgradeProcesses(boolean throwException) {
		try (LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.upgrade.internal.executor.UpgradeExecutor",
				LoggerTestUtil.OFF);
			LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.upgrade.internal.release." +
					"ReleaseManagerImpl",
				LoggerTestUtil.OFF)) {

			_serviceRegistration = _bundleContext.registerService(
				UpgradeStepRegistrator.class,
				new TestUpgradeStepRegistrator(throwException), null);

			_release = _releaseLocalService.fetchRelease(_bundleSymbolicName);
		}
	}

	private static String _bundleSymbolicName;

	private BundleContext _bundleContext;

	@DeleteAfterTestRun
	private Release _release;

	@Inject
	private ReleaseLocalService _releaseLocalService;

	@Inject(
		filter = "component.name=com.liferay.portal.upgrade.internal.release.ReleasePublisher",
		type = Inject.NoType.class
	)
	private Object _releasePublisher;

	@Inject
	private ServiceComponentRuntime _serviceComponentRuntime;

	private ServiceRegistration<UpgradeStepRegistrator> _serviceRegistration;

	private static class TestUpgradeStepRegistrator
		implements UpgradeStepRegistrator {

		public TestUpgradeStepRegistrator(boolean throwException) {
			_throwException = throwException;
		}

		@Override
		public void register(Registry registry) {
			registry.register("0.0.0", "1.0.0", new DummyUpgradeStep());

			registry.register(
				"1.0.0", "2.0.0",
				new UpgradeProcess() {

					@Override
					protected void doUpgrade() throws Exception {
						if (_throwException) {
							throw new Exception();
						}
					}

				});
		}

		private final boolean _throwException;

	}

}