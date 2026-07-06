/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.preferences.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.PortalPreferenceValue;
import com.liferay.portal.kernel.model.PortalPreferences;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.PortalPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.PortalPreferencesImpl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

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
 * @author Dante Wang
 */
@RunWith(Arquillian.class)
public class PortalPreferencesLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			PortalPreferencesLocalServiceTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@Test
	public void testUpdatePreferences() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		ServiceRegistration<?> serviceRegistration = null;
		PortalPreferences portalPreferences = null;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			portalPreferences =
				_portalPreferencesLocalService.addPortalPreferences(
					companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY, null);

			Assert.assertNull(_prefsProps.getString(companyId, "test.key"));

			CountDownLatch countDownLatch1 = new CountDownLatch(1);
			CountDownLatch countDownLatch2 = new CountDownLatch(1);
			CountDownLatch countDownLatch3 = new CountDownLatch(1);

			FutureTask<Boolean> futureTask = new FutureTask<>(
				() -> {
					countDownLatch1.await();

					Assert.assertNull(
						_prefsProps.getString(companyId, "test.key"));

					countDownLatch2.countDown();

					countDownLatch3.await();

					Assert.assertEquals(
						"test.value",
						_prefsProps.getString(companyId, "test.key"));

					return null;
				});

			Thread thread = new Thread(futureTask);

			thread.start();

			serviceRegistration = _bundleContext.registerService(
				ModelListener.class,
				new BaseModelListener<PortalPreferenceValue>() {

					@Override
					public Class<?> getModelClass() {
						return PortalPreferenceValue.class;
					}

					@Override
					public void onAfterCreate(
							PortalPreferenceValue portalPreferenceValue)
						throws ModelListenerException {

						TransactionCallbackUtil.registerCommitCallback(
							() -> {
								countDownLatch3.countDown();

								return null;
							});

						countDownLatch1.countDown();

						try {
							countDownLatch2.await(5, TimeUnit.SECONDS);
						}
						catch (InterruptedException interruptedException) {
							ReflectionUtil.throwException(interruptedException);
						}
					}

				},
				null);

			PortalPreferencesImpl portalPreferencesImpl =
				(PortalPreferencesImpl)
					_portalPreferenceValueLocalService.getPortalPreferences(
						portalPreferences, false);

			portalPreferencesImpl.setValue(null, "test.key", "test.value");

			_portalPreferencesLocalService.updatePreferences(
				companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY,
				portalPreferencesImpl);

			futureTask.get();
		}
		finally {
			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}

			if (portalPreferences != null) {
				_portalPreferencesLocalService.deletePortalPreferences(
					portalPreferences);
			}
		}
	}

	private BundleContext _bundleContext;

	@Inject
	private PortalPreferencesLocalService _portalPreferencesLocalService;

	@Inject
	private PortalPreferenceValueLocalService
		_portalPreferenceValueLocalService;

	@Inject
	private PrefsProps _prefsProps;

}