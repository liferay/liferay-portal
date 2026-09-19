/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.ServiceComponent;
import com.liferay.portal.kernel.service.ServiceComponentLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.sql.Connection;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class ServiceComponentPostUpgradeDataCleanupProcessTest
	extends BasePostUpgradeDataCleanupProcessTestCase {

	@Test
	public void testLiferayExistentPortletBuildNumberOutdatedIsError()
		throws Exception {

		ServiceComponent serviceComponent = _getLatestServiceComponent(
			"com.liferay.layout.service");

		AtomicReference<ServiceComponent> serviceComponentAtomicReference =
			new AtomicReference<>();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Content of table ",
							dbInspector.normalizeName("ServiceComponent"),
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

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Content of table ",
							dbInspector.normalizeName("ServiceComponent"),
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

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(messages.toString(), messages.isEmpty());
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

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Table ",
							dbInspector.normalizeName("ServiceComponent"),
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

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(messages.toString(), messages.isEmpty());
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

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Table ",
							dbInspector.normalizeName("ServiceComponent"),
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

	@Override
	protected Class<?>[] getPostUpgradeDataCleanupProcessArgumentTypes() {
		return new Class<?>[] {
			Connection.class, ServiceComponentLocalService.class
		};
	}

	@Override
	protected Object[] getPostUpgradeDataCleanupProcessArguments() {
		return new Object[] {connection, _serviceComponentLocalService};
	}

	@Override
	protected String getPostUpgradeDataCleanupProcessClassName() {
		return "com.liferay.data.cleanup.internal.verify." +
			"ServiceComponentPostUpgradeDataCleanupProcess";
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

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private ServiceComponentLocalService _serviceComponentLocalService;

}