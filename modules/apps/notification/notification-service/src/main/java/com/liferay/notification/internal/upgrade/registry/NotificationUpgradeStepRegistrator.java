/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.upgrade.registry;

import com.liferay.notification.internal.upgrade.v1_1_0.util.NotificationQueueEntryAttachmentTable;
import com.liferay.notification.internal.upgrade.v1_1_0.util.NotificationTemplateAttachmentTable;
import com.liferay.notification.internal.upgrade.v1_2_0.NotificationQueueEntryUpgradeProcess;
import com.liferay.notification.internal.upgrade.v2_1_0.NotificationTemplateUpgradeProcess;
import com.liferay.notification.internal.upgrade.v3_10_4.DeleteStaleNotificationQueueEntriesAndNotificationTemplatesUpgradeProcess;
import com.liferay.notification.internal.upgrade.v3_4_0.NotificationRecipientUpgradeProcess;
import com.liferay.notification.internal.upgrade.v3_7_0.ResourcePermissionUpgradeProcess;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = UpgradeStepRegistrator.class)
public class NotificationUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "1.1.0", NotificationQueueEntryAttachmentTable.create(),
			NotificationTemplateAttachmentTable.create(),
			UpgradeProcessFactory.addColumns(
				"NotificationTemplate", "objectDefinitionId LONG"));

		registry.register(
			"1.1.0", "1.2.0", new NotificationQueueEntryUpgradeProcess());

		registry.register(
			"1.2.0", "2.0.0",
			UpgradeProcessFactory.dropColumns(
				"NotificationQueueEntry", "sent"));

		registry.register(
			"2.0.0", "2.1.0", new NotificationTemplateUpgradeProcess());

		registry.register(
			"2.1.0", "2.2.0",
			new com.liferay.notification.internal.upgrade.v2_2_0.
				NotificationQueueEntryUpgradeProcess());

		registry.register(
			"2.2.0", "3.0.0",
			new com.liferay.notification.internal.upgrade.v3_0_0.
				NotificationRecipientUpgradeProcess());

		registry.register(
			"3.0.0", "3.1.0",
			new com.liferay.notification.internal.upgrade.v3_1_0.
				NotificationTemplateUpgradeProcess());

		registry.register(
			"3.1.0", "3.2.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"NotificationTemplate"};
				}

			});

		registry.register(
			"3.2.0", "3.3.0",
			UpgradeProcessFactory.dropColumns(
				"NotificationRecipient", "className"),
			UpgradeProcessFactory.addColumns(
				"NotificationRecipient", "classNameId LONG"));

		registry.register(
			"3.3.0", "3.4.0", new NotificationRecipientUpgradeProcess());

		registry.register(
			"3.4.0", "3.5.0",
			new com.liferay.notification.internal.upgrade.v3_5_0.
				NotificationTemplateUpgradeProcess());

		registry.register(
			"3.5.0", "3.5.1",
			UpgradeProcessFactory.alterColumnType(
				"NotificationQueueEntry", "subject", "TEXT"));

		registry.register(
			"3.5.1", "3.5.2",
			UpgradeProcessFactory.alterColumnType(
				"NotificationTemplate", "description", "VARCHAR(255) null"));

		registry.register(
			"3.5.2", "3.6.0",
			new com.liferay.notification.internal.upgrade.v3_6_0.
				NotificationQueueEntryUpgradeProcess(
					_classNameLocalService, _resourceLocalService));

		registry.register(
			"3.6.0", "3.7.0", new ResourcePermissionUpgradeProcess());

		registry.register(
			"3.7.0", "3.8.0",
			new com.liferay.notification.internal.upgrade.v3_8_0.
				NotificationQueueEntryUpgradeProcess());

		registry.register(
			"3.8.0", "3.9.0",
			new com.liferay.notification.internal.upgrade.v3_9_0.
				NotificationRecipientSettingUpgradeProcess());

		registry.register(
			"3.9.0", "3.9.1",
			UpgradeProcessFactory.alterColumnType(
				"NotificationTemplate", "type_", "VARCHAR(255) null"));

		registry.register("3.9.1", "3.9.2", new DummyUpgradeStep());

		registry.register(
			"3.9.2", "3.10.0",
			new com.liferay.notification.internal.upgrade.v3_10_0.
				NotificationTemplateUpgradeProcess());

		registry.register(
			"3.10.0", "3.10.1",
			new com.liferay.notification.internal.upgrade.v3_10_1.
				NotificationRecipientSettingUpgradeProcess());

		registry.register(
			"3.10.1", "3.10.2",
			new com.liferay.notification.internal.upgrade.v3_10_2.
				NotificationRecipientSettingUpgradeProcess());

		registry.register(
			"3.10.2", "3.10.3",
			UpgradeProcessFactory.runSQL(
				"delete from NotificationRecipientSetting where name = " +
					"'usePreferredLocaleForGuestUsers'"));

		registry.register(
			"3.10.3", "3.10.4",
			new DeleteStaleNotificationQueueEntriesAndNotificationTemplatesUpgradeProcess(
				_classNameLocalService, _groupLocalService,
				_portletFileRepository, _resourcePermissionLocalService));

		registry.register(
			"3.10.4", "4.0.0",
			UpgradeProcessFactory.alterColumnType(
				"NotificationQueueEntry", "body", "TEXT null"));
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

}