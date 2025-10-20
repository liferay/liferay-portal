/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.upgrade.registry;

import com.liferay.message.boards.constants.MBConstants;
import com.liferay.message.boards.internal.upgrade.v1_0_0.UpgradeClassNames;
import com.liferay.message.boards.internal.upgrade.v1_1_0.MBThreadUpgradeProcess;
import com.liferay.message.boards.internal.upgrade.v2_0_0.util.MBBanTable;
import com.liferay.message.boards.internal.upgrade.v2_0_0.util.MBCategoryTable;
import com.liferay.message.boards.internal.upgrade.v2_0_0.util.MBDiscussionTable;
import com.liferay.message.boards.internal.upgrade.v2_0_0.util.MBMailingListTable;
import com.liferay.message.boards.internal.upgrade.v2_0_0.util.MBMessageTable;
import com.liferay.message.boards.internal.upgrade.v2_0_0.util.MBStatsUserTable;
import com.liferay.message.boards.internal.upgrade.v2_0_0.util.MBThreadFlagTable;
import com.liferay.message.boards.internal.upgrade.v2_0_0.util.MBThreadTable;
import com.liferay.message.boards.internal.upgrade.v3_0_0.MBMessageTreePathUpgradeProcess;
import com.liferay.message.boards.internal.upgrade.v3_1_0.UrlSubjectUpgradeProcess;
import com.liferay.message.boards.internal.upgrade.v6_3_0.util.MBSuspiciousActivityTable;
import com.liferay.message.boards.internal.upgrade.v6_5_0.FriendlyURLUpgradeProcess;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BaseSQLServerDatetimeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.GuestUnsupportedResourcePermissionsUpgradeProcess;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.ViewCountUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(service = UpgradeStepRegistrator.class)
public class MBServiceUpgradeStepRegistrator implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register("0.0.1", "1.0.0", new UpgradeClassNames());

		registry.register(
			"1.0.0", "1.0.0.step-1",
			new GuestUnsupportedResourcePermissionsUpgradeProcess(
				MBCategory.class.getName(), ActionKeys.DELETE,
				ActionKeys.MOVE_THREAD, ActionKeys.PERMISSIONS));

		registry.register(
			"1.0.0.step-1", "1.0.0.step-2",
			new GuestUnsupportedResourcePermissionsUpgradeProcess(
				MBMessage.class.getName(), ActionKeys.DELETE,
				ActionKeys.PERMISSIONS));

		registry.register(
			"1.0.0.step-2", "1.0.0.step-3",
			new GuestUnsupportedResourcePermissionsUpgradeProcess(
				MBConstants.RESOURCE_NAME, ActionKeys.LOCK_THREAD,
				ActionKeys.MOVE_THREAD));

		registry.register(
			"1.0.0.step-3", "1.0.1",
			new GuestUnsupportedResourcePermissionsUpgradeProcess(
				MBThread.class.getName(), ActionKeys.DELETE));

		registry.register("1.0.1", "1.1.0", new MBThreadUpgradeProcess());

		registry.register(
			"1.1.0", "2.0.0",
			new BaseSQLServerDatetimeUpgradeProcess(
				new Class<?>[] {
					MBBanTable.class, MBCategoryTable.class,
					MBDiscussionTable.class, MBMailingListTable.class,
					MBMessageTable.class, MBStatsUserTable.class,
					MBThreadFlagTable.class, MBThreadTable.class
				}));

		registry.register(
			"2.0.0", "2.0.1",
			new ViewCountUpgradeProcess(
				"MBThread", MBThread.class, "threadId", "viewCount"));

		registry.register(
			"2.0.1", "3.0.0", new MBMessageTreePathUpgradeProcess());

		registry.register("3.0.0", "3.1.0", new UrlSubjectUpgradeProcess());

		registry.register(
			"3.1.0", "4.0.0",
			UpgradeProcessFactory.dropColumns(
				"MBCategory", "lastPostDate", "messageCount", "threadCount"));

		registry.register(
			"4.0.0", "5.0.0",
			UpgradeProcessFactory.dropColumns("MBThread", "messageCount"),
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"MBBan", "MBCategory", "MBDiscussion", "MBMailingList",
						"MBMessage", "MBStatsUser", "MBThread", "MBThreadFlag"
					};
				}

			});

		registry.register(
			"5.0.0", "5.1.0",
			new CTModelUpgradeProcess(
				"MBBan", "MBCategory", "MBDiscussion", "MBMailingList",
				"MBMessage", "MBStatsUser", "MBThread", "MBThreadFlag"));

		registry.register("5.1.0", "5.2.0", new DummyUpgradeStep());

		registry.register(
			"5.2.0", "6.0.0", UpgradeProcessFactory.dropTables("MBStatsUser"));

		registry.register(
			"6.0.0", "6.1.0",
			UpgradeProcessFactory.alterColumnType(
				"MBThread", "title", "VARCHAR(75) null"));

		registry.register(
			"6.1.0", "6.1.1",
			UpgradeProcessFactory.alterColumnType(
				"MBMessage", "subject", "VARCHAR(255) null"),
			UpgradeProcessFactory.alterColumnType(
				"MBThread", "title", "VARCHAR(255) null"));

		registry.register(
			"6.1.1", "6.2.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"MBMessage"};
				}

			});

		registry.register("6.2.0", "6.3.0", MBSuspiciousActivityTable.create());

		registry.register(
			"6.3.0", "6.4.0",
			UpgradeProcessFactory.alterColumnName(
				"MBSuspiciousActivity", "type_", "reason VARCHAR(75) null"),
			UpgradeProcessFactory.alterColumnType(
				"MBSuspiciousActivity", "reason", "VARCHAR(255)"),
			UpgradeProcessFactory.dropColumns(
				"MBSuspiciousActivity", "description"));

		registry.register(
			"6.4.0", "6.4.1",
			UpgradeProcessFactory.alterColumnType(
				"MBSuspiciousActivity", "reason", "VARCHAR(255) null"));

		registry.register("6.4.1", "6.5.0", new FriendlyURLUpgradeProcess());

		registry.register(
			"6.5.0", "6.5.1",
			UpgradeProcessFactory.alterColumnType(
				"MBCategory", "name", "VARCHAR(255) null"));

		registry.register(
			"6.5.1", "6.6.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"MBCategory"};
				}

			});
	}

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.view.count.service)(&(release.schema.version>=1.0.0)))"
	)
	private Release _release;

}