/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Jorge Avalos
 */
@RunWith(Arquillian.class)
public class BackgroundTaskLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddBackgroundTask() throws Exception {
		BackgroundTaskExecutor backgroundTaskExecutor =
			(BackgroundTaskExecutor)ProxyUtil.newProxyInstance(
				BackgroundTaskExecutor.class.getClassLoader(),
				new Class<?>[] {BackgroundTaskExecutor.class},
				(proxy, method, args) -> {
					if (Objects.equals(method.getName(), "clone")) {
						return proxy;
					}
					else if (Objects.equals(method.getName(), "execute")) {
						return new BackgroundTaskResult(
							BackgroundTaskConstants.STATUS_FAILED);
					}
					else if (Objects.equals(
								method.getName(), "getIsolationLevel")) {

						return BackgroundTaskConstants.
							ISOLATION_LEVEL_NOT_ISOLATED;
					}
					else if (Objects.equals(method.getName(), "isSerial")) {
						return false;
					}

					return null;
				});

		Class<?> backgroundTaskExecutorClass =
			backgroundTaskExecutor.getClass();

		Bundle bundle = FrameworkUtil.getBundle(
			BackgroundTaskLocalServiceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<BackgroundTaskExecutor> serviceRegistration =
			bundleContext.registerService(
				BackgroundTaskExecutor.class, backgroundTaskExecutor,
				HashMapDictionaryBuilder.<String, Object>put(
					"background.task.executor.class.name",
					backgroundTaskExecutorClass.getName()
				).build());

		try {
			_companyLocalService.forEachCompanyId(
				companyId -> {
					BackgroundTask backgroundTask =
						_backgroundTaskLocalService.addBackgroundTask(
							UserConstants.USER_ID_DEFAULT,
							CompanyConstants.SYSTEM,
							RandomTestUtil.randomString(), null,
							backgroundTaskExecutorClass, null, null);

					Assert.assertEquals(
						backgroundTask.getCompanyId(), (long)companyId);

					_backgroundTaskLocalService.deleteBackgroundTask(
						backgroundTask.getBackgroundTaskId());
				});
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Inject
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

}