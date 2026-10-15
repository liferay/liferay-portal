/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.service.ClientExtensionEntryLocalService;
import com.liferay.client.extension.service.persistence.ClientExtensionEntryPersistence;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;
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

/**
 * @author Drew Brokke
 */
@RunWith(Arquillian.class)
public class ClientExtensionEntryLocalServiceRegistrationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			ClientExtensionEntryLocalServiceRegistrationTest.class);

		_bundleContext = bundle.getBundleContext();

		for (Bundle curBundle : _bundleContext.getBundles()) {
			if (Objects.equals(
					curBundle.getSymbolicName(),
					"com.liferay.client.extension.service")) {

				_bundle = curBundle;

				break;
			}
		}

		Assert.assertNotNull(_bundle);
	}

	@FeatureFlag(enable = false, value = "LPS-164563")
	@Test
	public void testSetAopProxyNotInterrupted() throws Throwable {
		TransactionConfig.Builder builder = new TransactionConfig.Builder();
		ClientExtensionEntry clientExtensionEntry = null;

		TransactionConfig transactionConfig = builder.build();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.client.extension.service.impl." +
					"ClientExtensionEntryLocalServiceImpl",
				LoggerTestUtil.ERROR)) {

			_assertLocalServiceRegistered();

			clientExtensionEntry = _clientExtensionEntryPersistence.create(
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
			clientExtensionEntry.setType(
				ClientExtensionEntryConstants.TYPE_FDS_CELL_RENDERER);
			clientExtensionEntry.setTypeSettings(StringPool.BLANK);
			clientExtensionEntry.setStatus(WorkflowConstants.STATUS_APPROVED);
			clientExtensionEntry.setStatusByUserId(user.getUserId());
			clientExtensionEntry.setStatusDate(new Date());
			clientExtensionEntry.setNew(true);

			AtomicReference<ClientExtensionEntry>
				clientExtensionEntryAtomicReference = new AtomicReference<>(
					clientExtensionEntry);

			clientExtensionEntry = TransactionInvokerUtil.invoke(
				transactionConfig,
				() -> _clientExtensionEntryPersistence.update(
					clientExtensionEntryAtomicReference.get()));

			_restartBundle();

			_assertLocalServiceRegistered();
		}
		finally {
			if (clientExtensionEntry != null) {
				long clientExtensionEntryId =
					clientExtensionEntry.getClientExtensionEntryId();

				TransactionInvokerUtil.invoke(
					transactionConfig,
					() -> _clientExtensionEntryPersistence.remove(
						clientExtensionEntryId));
			}

			_restartBundle();

			_assertLocalServiceRegistered();
		}
	}

	private void _assertLocalServiceRegistered() {
		Assert.assertNotNull(
			_bundleContext.getServiceReference(
				ClientExtensionEntryLocalService.class));
	}

	private void _restartBundle() throws Exception {
		_bundle.stop();

		_bundle.start();
	}

	private Bundle _bundle;
	private BundleContext _bundleContext;

	@Inject
	private ClientExtensionEntryPersistence _clientExtensionEntryPersistence;

	@Inject
	private CounterLocalService _counterLocalService;

}