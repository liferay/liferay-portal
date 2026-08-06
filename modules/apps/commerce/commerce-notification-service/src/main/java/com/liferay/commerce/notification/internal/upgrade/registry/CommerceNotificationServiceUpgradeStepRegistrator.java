/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.internal.upgrade.registry;

import com.liferay.commerce.notification.internal.upgrade.v2_0_0.util.CommerceNotificationTemplateCommerceAccountGroupRelTable;
import com.liferay.commerce.notification.internal.upgrade.v2_2_1.CommerceNotificationTemplateGroupIdUpgradeProcess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upgrade.DummyUpgradeProcess;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(service = UpgradeStepRegistrator.class)
public class CommerceNotificationServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		if (_log.isInfoEnabled()) {
			_log.info("Commerce notification upgrade step registrator started");
		}

		registry.register("1.0.0", "1.1.0", new DummyUpgradeProcess());

		registry.register(
			"1.1.0", "2.0.0",
			CommerceNotificationTemplateCommerceAccountGroupRelTable.create());

		registry.register(
			"2.0.0", "2.1.0",
			UpgradeProcessFactory.addColumns(
				"CommerceNotificationQueueEntry", "classNameId LONG",
				"classPK LONG"));

		registry.register(
			"2.1.0", "2.2.0",
			UpgradeProcessFactory.addColumns(
				"CommerceNotificationTemplate", "to_ VARCHAR(75)"));

		registry.register(
			"2.2.0", "2.2.1",
			new CommerceNotificationTemplateGroupIdUpgradeProcess(
				_classNameLocalService, _groupLocalService));

		registry.register(
			"2.2.1", "2.3.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"CNTemplateCAccountGroupRel", "CNotificationAttachment",
						"CommerceNotificationQueueEntry",
						"CommerceNotificationTemplate"
					};
				}

			});

		registry.register(
			"2.3.0", "3.0.0",
			UpgradeProcessFactory.alterColumnType(
				"CommerceNotificationTemplate", "from_", "TEXT null"),
			UpgradeProcessFactory.alterColumnType(
				"CommerceNotificationTemplate", "to_", "TEXT null"),
			UpgradeProcessFactory.alterColumnType(
				"CommerceNotificationTemplate", "cc", "TEXT null"),
			UpgradeProcessFactory.alterColumnType(
				"CommerceNotificationTemplate", "bcc", "TEXT null"));

		registry.register(
			"3.0.0", "3.1.0",
			UpgradeProcessFactory.runSQL(
				"update NotificationTemplate set externalReferenceCode = " +
					"'COMMERCE_ORDER_TEMPLATE' where externalReferenceCode = " +
						"'L_COMMERCE_ORDER_TEMPLATE'"));

		if (_log.isInfoEnabled()) {
			_log.info(
				"Commerce notification upgrade step registrator finished");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceNotificationServiceUpgradeStepRegistrator.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}