/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.ServiceComponent;
import com.liferay.portal.kernel.service.ServiceComponentLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.verify.VerifyProcess;

import java.sql.Connection;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class ServiceComponentDataCleanupVerifyProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_connection = DataAccess.getConnection();

		_dbInspector = new DBInspector(_connection);
	}

	@Test
	public void testLiferayExistentPortletBuildNumberOutdatedIsError()
		throws Exception {

		ServiceComponent serviceComponent = _getLatestServiceComponent(
			"com.liferay.layout.service");

		AtomicReference<ServiceComponent> serviceComponentAtomicReference =
			new AtomicReference<>();

		_test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.contains(
						StringBundler.concat(
							"Content of table ",
							_dbInspector.normalizeName("ServiceComponent"),
							" for bundle com.liferay.layout.service is ",
							"outdated")));
			},
			() -> {
				if (serviceComponentAtomicReference.get() != null) {
					_serviceComponentLocalService.deleteServiceComponent(
						serviceComponentAtomicReference.get());
				}
			},
			() -> serviceComponentAtomicReference.set(
				_addServiceComponent(
					serviceComponent.getBuildNamespace(),
					serviceComponent.getBuildNumber() + 1,
					serviceComponent.getData())));
	}

	@Test
	public void testLiferayExistentPortletDataOutdatedIsError()
		throws Exception {

		AtomicReference<ServiceComponent> serviceComponentAtomicReference =
			new AtomicReference<>();

		serviceComponentAtomicReference.set(
			_getLatestServiceComponent("com.liferay.layout.service"));

		String originalData = serviceComponentAtomicReference.get(
		).getData();

		_test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.contains(
						StringBundler.concat(
							"Content of table ",
							_dbInspector.normalizeName("ServiceComponent"),
							" for bundle com.liferay.layout.service is ",
							"outdated")));
			},
			() -> {
				ServiceComponent serviceComponent = _getLatestServiceComponent(
					"com.liferay.layout.service");

				serviceComponent.setData(originalData);

				_serviceComponentLocalService.updateServiceComponent(
					serviceComponent);
			},
			() -> {
				serviceComponentAtomicReference.get(
				).setData(
					originalData + RandomTestUtil.randomString()
				);

				_serviceComponentLocalService.updateServiceComponent(
					serviceComponentAtomicReference.get());
			});
	}

	@Test
	public void testLiferayExistentPortletOldBuildNumbersAreNotChecked()
		throws Exception {

		AtomicReference<ServiceComponent> serviceComponentAtomicReference =
			new AtomicReference<>();

		_test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(messages.isEmpty());
			},
			() -> {
				if (serviceComponentAtomicReference.get() != null) {
					_serviceComponentLocalService.deleteServiceComponent(
						serviceComponentAtomicReference.get());
				}
			},
			() -> serviceComponentAtomicReference.set(
				_addServiceComponent("com.liferay.layout.service", 0, null)));
	}

	@Test
	public void testLiferayNonexistentPortletIsDeleted() throws Exception {
		AtomicReference<ServiceComponent> serviceComponentAtomicReference =
			new AtomicReference<>();

		String portletName = "com.liferay.test.portlet";

		_test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.contains(
						StringBundler.concat(
							"Table ",
							_dbInspector.normalizeName("ServiceComponent"),
							", 1 row deleted because ", portletName,
							" does not match with any existing bundle")));
			},
			() -> {
				if (serviceComponentAtomicReference.get() != null) {
					_serviceComponentLocalService.deleteServiceComponent(
						serviceComponentAtomicReference.get());
				}
			},
			() -> serviceComponentAtomicReference.set(
				_addServiceComponent(portletName, 1, null)));
	}

	@Test
	public void testNonliferayPortletIsNotDeleted() throws Exception {
		AtomicReference<ServiceComponent> serviceComponentAtomicReference =
			new AtomicReference<>();

		_test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(messages.isEmpty());
			},
			() -> {
				if (serviceComponentAtomicReference.get() != null) {
					_serviceComponentLocalService.deleteServiceComponent(
						serviceComponentAtomicReference.get());
				}
			},
			() -> serviceComponentAtomicReference.set(
				_addServiceComponent("com.test.portlet", 1, null)));
	}

	@Test
	public void testPortletNotFullyQualifiedNameIsDeleted() throws Exception {
		AtomicReference<ServiceComponent> serviceComponentAtomicReference =
			new AtomicReference<>();

		String portletName = "shortNamePortlet";

		_test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.contains(
						StringBundler.concat(
							"Table ",
							_dbInspector.normalizeName("ServiceComponent"),
							", 1 row deleted because ", portletName,
							" is not a fully qualified name")));
			},
			() -> {
				if (serviceComponentAtomicReference.get() != null) {
					_serviceComponentLocalService.deleteServiceComponent(
						serviceComponentAtomicReference.get());
				}
			},
			() -> serviceComponentAtomicReference.set(
				_addServiceComponent(portletName, 1, null)));
	}

	private ServiceComponent _addServiceComponent(
		String buildNameSpace, long buildNumber, String data) {

		long serviceComponentId = _counterLocalService.increment();

		ServiceComponent serviceComponent =
			_serviceComponentLocalService.createServiceComponent(
				serviceComponentId);

		serviceComponent.setBuildNamespace(buildNameSpace);
		serviceComponent.setBuildNumber(buildNumber);

		if (data != null) {
			serviceComponent.setData(data);
		}

		return _serviceComponentLocalService.updateServiceComponent(
			serviceComponent);
	}

	private ServiceComponent _getLatestServiceComponent(String buildNamespace) {
		List<ServiceComponent> serviceComponents =
			_serviceComponentLocalService.getLatestServiceComponents();

		for (ServiceComponent serviceComponent : serviceComponents) {
			if (buildNamespace.equals(serviceComponent.getBuildNamespace())) {
				return serviceComponent;
			}
		}

		return null;
	}

	private void _test(
			UnsafeConsumer<LogCapture, Exception> assertUnsafeConsumer,
			UnsafeRunnable<Exception> cleanUpDataUnsafeRunnable,
			UnsafeRunnable<Exception> initializeDataUnsafeRunnable)
		throws Exception {

		initializeDataUnsafeRunnable.run();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.data.cleanup.internal.verify." +
					"ServiceComponentDataCleanupVerifyProcess",
				LoggerTestUtil.INFO)) {

			_serviceComponentDataCleanupVerifyProcess.verify();

			assertUnsafeConsumer.accept(logCapture);
		}
		finally {
			cleanUpDataUnsafeRunnable.run();
		}
	}

	private static Connection _connection;
	private static DBInspector _dbInspector;

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.data.cleanup.internal.verify.ServiceComponentDataCleanupVerifyProcess))"
	)
	private VerifyProcess _serviceComponentDataCleanupVerifyProcess;

	@Inject
	private ServiceComponentLocalService _serviceComponentLocalService;

}