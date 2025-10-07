/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.internal.upgrade.registry;

import com.liferay.calendar.internal.upgrade.v1_0_2.CalendarUpgradeProcess;
import com.liferay.calendar.internal.upgrade.v1_0_4.UpgradeClassNames;
import com.liferay.calendar.internal.upgrade.v1_0_5.CalendarResourceUpgradeProcess;
import com.liferay.calendar.internal.upgrade.v1_0_5.UpgradeCompanyId;
import com.liferay.calendar.internal.upgrade.v1_0_5.UpgradeLastPublishDate;
import com.liferay.calendar.internal.upgrade.v1_0_6.ResourcePermissionUpgradeProcess;
import com.liferay.calendar.internal.upgrade.v2_0_0.SchemaUpgradeProcess;
import com.liferay.calendar.internal.upgrade.v3_0_0.UpgradeCalendarBookingResourceBlock;
import com.liferay.calendar.internal.upgrade.v3_0_0.UpgradeCalendarResourceBlock;
import com.liferay.calendar.internal.upgrade.v3_0_0.UpgradeCalendarResourceResourceBlock;
import com.liferay.calendar.internal.upgrade.v4_0_0.util.CalendarBookingTable;
import com.liferay.calendar.internal.upgrade.v4_0_0.util.CalendarNotificationTemplateTable;
import com.liferay.calendar.internal.upgrade.v4_0_0.util.CalendarResourceTable;
import com.liferay.calendar.internal.upgrade.v4_0_0.util.CalendarTable;
import com.liferay.calendar.internal.upgrade.v4_2_1.CalendarBookingUpgradeProcess;
import com.liferay.calendar.internal.upgrade.v4_3_0.CalendarNotificationTemplateResourceUpgradeProcess;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.comment.upgrade.DiscussionSubscriptionClassNameUpgradeProcess;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.BaseSQLServerDatetimeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.subscription.service.SubscriptionLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera
 * @author Manuel de la Peña
 */
@Component(service = UpgradeStepRegistrator.class)
public class CalendarServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"0.0.1", "1.0.0",
			UpgradeProcessFactory.alterColumnType(
				"CalendarBooking", "description", "TEXT null"));

		registry.register(
			"1.0.0", "1.0.1",
			new com.liferay.calendar.internal.upgrade.v1_0_1.
				CalendarBookingUpgradeProcess());

		registry.register("1.0.1", "1.0.2", new CalendarUpgradeProcess());

		registry.register("1.0.2", "1.0.3", new DummyUpgradeStep());

		registry.register("1.0.3", "1.0.4", new UpgradeClassNames());

		registry.register(
			"1.0.4", "1.0.4.step-1",
			new CalendarResourceUpgradeProcess(
				_classNameLocalService, _companyLocalService,
				_userLocalService));

		registry.register(
			"1.0.4.step-1", "1.0.4.step-2", new UpgradeCompanyId());

		registry.register(
			"1.0.4.step-2", "1.0.5", new UpgradeLastPublishDate());

		registry.register(
			"1.0.5", "1.0.6",
			new ResourcePermissionUpgradeProcess(
				_resourceActionLocalService, _resourcePermissionLocalService,
				_roleLocalService));

		registry.register("1.0.6", "1.0.7", new DummyUpgradeStep());

		registry.register("1.0.7", "2.0.0", new SchemaUpgradeProcess());

		registry.register(
			"2.0.0", "2.0.1", new UpgradeCalendarBookingResourceBlock());

		registry.register("2.0.1", "2.0.2", new UpgradeCalendarResourceBlock());

		registry.register(
			"2.0.2", "3.0.0", new UpgradeCalendarResourceResourceBlock());

		registry.register(
			"3.0.0", "3.0.1",
			new DiscussionSubscriptionClassNameUpgradeProcess(
				_classNameLocalService, _subscriptionLocalService,
				CalendarBooking.class.getName(),
				DiscussionSubscriptionClassNameUpgradeProcess.DeletionMode.
					UPDATE));

		registry.register(
			"3.0.1", "4.0.0",
			new BaseSQLServerDatetimeUpgradeProcess(
				new Class<?>[] {
					CalendarBookingTable.class,
					CalendarNotificationTemplateTable.class,
					CalendarResourceTable.class, CalendarTable.class
				}));

		registry.register(
			"4.0.0", "4.0.1",
			new DiscussionSubscriptionClassNameUpgradeProcess(
				_classNameLocalService, _subscriptionLocalService,
				CalendarBooking.class.getName(),
				DiscussionSubscriptionClassNameUpgradeProcess.DeletionMode.
					DELETE_OLD));

		registry.register(
			"4.0.1", "4.1.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"Calendar", "CalendarBooking",
						"CalendarNotificationTemplate", "CalendarResource"
					};
				}

			});

		registry.register(
			"4.1.0", "4.1.1",
			UpgradeProcessFactory.alterColumnType(
				"CalendarNotificationTemplate", "notificationTypeSettings",
				"VARCHAR(150) null"));

		registry.register(
			"4.1.1", "4.1.2",
			UpgradeProcessFactory.alterColumnType(
				"CalendarNotificationTemplate", "notificationTypeSettings",
				"VARCHAR(200) null"));

		registry.register("4.1.2", "4.1.3", new DummyUpgradeStep());

		registry.register(
			"4.1.3", "4.2.0",
			new CTModelUpgradeProcess(
				"Calendar", "CalendarBooking", "CalendarNotificationTemplate",
				"CalendarResource"));

		registry.register(
			"4.2.0", "4.2.1",
			new CalendarBookingUpgradeProcess(_userLocalService));

		registry.register(
			"4.2.1", "4.3.0",
			new CalendarNotificationTemplateResourceUpgradeProcess(
				_resourceLocalService));

		registry.register(
			"4.3.0", "4.4.0",
			UpgradeProcessFactory.addColumns(
				"CalendarBooking", "externalReferenceCode VARCHAR(75)"));
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}