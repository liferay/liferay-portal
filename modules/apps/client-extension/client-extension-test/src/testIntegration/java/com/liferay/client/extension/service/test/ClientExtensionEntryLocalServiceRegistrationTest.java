/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.service.ClientExtensionEntryLocalService;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Drew Brokke
 */
@RunWith(Arquillian.class)
public class ClientExtensionEntryLocalServiceRegistrationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testSetAopProxyNotInterrupted() throws Exception {
		ClientExtensionEntry clientExtensionEntry = null;

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.client.extension.service.impl." +
					"ClientExtensionEntryLocalServiceImpl",
				LoggerTestUtil.ERROR)) {

			clientExtensionEntry =
				_clientExtensionEntryLocalService.createClientExtensionEntry(
					_counterLocalService.increment());

			User user = TestPropsValues.getUser();

			clientExtensionEntry.setExternalReferenceCode(
				RandomTestUtil.randomString());
			clientExtensionEntry.setCompanyId(TestPropsValues.getCompanyId());
			clientExtensionEntry.setUserId(user.getUserId());
			clientExtensionEntry.setUserName(user.getFullName());
			clientExtensionEntry.setDescription(RandomTestUtil.randomString());
			clientExtensionEntry.setNameMap(
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()));
			clientExtensionEntry.setProperties(StringPool.BLANK);
			clientExtensionEntry.setSourceCodeURL("http://foo.test/index.js");
			clientExtensionEntry.setType(RandomTestUtil.randomString());
			clientExtensionEntry.setTypeSettings(StringPool.BLANK);
			clientExtensionEntry.setStatus(WorkflowConstants.STATUS_APPROVED);
			clientExtensionEntry.setStatusByUserId(user.getUserId());
			clientExtensionEntry.setStatusDate(new Date());

			clientExtensionEntry =
				_clientExtensionEntryLocalService.addClientExtensionEntry(
					clientExtensionEntry);

			_restartBundle();

			_assertLocalServiceRegistered();
		}
		finally {
			if (clientExtensionEntry != null) {
				_clientExtensionEntryLocalService.deleteClientExtensionEntry(
					clientExtensionEntry.getClientExtensionEntryId());
			}

			_restartBundle();

			_assertLocalServiceRegistered();
		}
	}

	private void _assertLocalServiceRegistered() {
		Bundle bundle = FrameworkUtil.getBundle(
			ClientExtensionEntryLocalServiceRegistrationTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceReference<ClientExtensionEntryLocalService> serviceReference =
			bundleContext.getServiceReference(
				ClientExtensionEntryLocalService.class);

		ClientExtensionEntryLocalService clientExtensionEntryLocalService =
			bundleContext.getService(serviceReference);

		Assert.assertNotNull(clientExtensionEntryLocalService);
		Assert.assertNotSame(
			_clientExtensionEntryLocalService,
			clientExtensionEntryLocalService);

		bundleContext.ungetService(serviceReference);
	}

	private void _restartBundle() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			_clientExtensionEntryLocalService.getClass());

		bundle.stop();

		bundle.start();
	}

	@Inject
	private ClientExtensionEntryLocalService _clientExtensionEntryLocalService;

	@Inject
	private CounterLocalService _counterLocalService;

}