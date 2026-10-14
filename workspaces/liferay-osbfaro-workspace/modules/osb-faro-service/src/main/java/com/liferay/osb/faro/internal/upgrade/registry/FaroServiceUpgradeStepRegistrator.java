/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.internal.upgrade.registry;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Matthew Kong
 */
@Component(service = UpgradeStepRegistrator.class)
public class FaroServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "1.1.0",
			new com.liferay.osb.faro.internal.upgrade.v1_1_0.
				UpgradeFaroProjectUpgradeProcess());

		registry.register(
			"1.1.0", "2.0.0",
			new com.liferay.osb.faro.internal.upgrade.v2_0_0.
				UpgradeFaroProjectUpgradeProcess());

		registry.register(
			"2.0.0", "3.0.0",
			new com.liferay.osb.faro.internal.upgrade.v3_0_0.
				UpgradeFaroProjectUpgradeProcess());

		registry.register(
			"3.0.0", "3.0.1",
			UpgradeProcessFactory.runSQL(
				StringBundler.concat(
					"create table OSBFaro_FaroPreferences (faroPreferencesId ",
					"LONG not null primary key, groupId LONG, userId LONG, ",
					"userName VARCHAR(75) null, createTime LONG, modifiedTime ",
					"LONG, ownerId LONG, preferences STRING null)")));

		registry.register(
			"3.0.1", "4.0.0",
			new com.liferay.osb.faro.internal.upgrade.v4_0_0.
				UpgradeFaroProjectUpgradeProcess());

		registry.register(
			"4.0.0", "5.0.0",
			new com.liferay.osb.faro.internal.upgrade.v5_0_0.
				UpgradeFaroProjectUpgradeProcess());

		registry.register(
			"5.0.0", "6.0.0",
			UpgradeProcessFactory.runSQL(
				StringBundler.concat(
					"create table OSBFaro_FaroProjectEmailAddressDomain ",
					"(faroProjectEmailAddressDomainId LONG not null primary ",
					"key, groupId LONG, faroProjectId LONG, ",
					"emailAddressDomain VARCHAR(255) null)")));

		registry.register(
			"6.0.0", "7.0.0",
			UpgradeProcessFactory.runSQL(
				StringBundler.concat(
					"create table OSBFaro_FaroChannel (faroChannelId LONG not ",
					"null primary key, groupId LONG, userId LONG, userName ",
					"VARCHAR(75) null, createTime LONG, modifiedTime LONG, ",
					"channelId VARCHAR(75), name VARCHAR(75) null, ",
					"permissionType INTEGER, workspaceGroupId LONG)")));

		registry.register(
			"7.0.0", "8.0.0",
			new com.liferay.osb.faro.internal.upgrade.v8_0_0.
				UpgradeFaroProjectUpgradeProcess());

		registry.register(
			"8.0.0", "9.0.0",
			new com.liferay.osb.faro.internal.upgrade.v9_0_0.
				UpgradeFaroProjectUpgradeProcess());

		registry.register(
			"9.0.0", "10.0.0",
			new com.liferay.osb.faro.internal.upgrade.v10_0_0.
				UpgradeFaroProjectUpgradeProcess());

		registry.register(
			"10.0.0", "11.0.0",
			new com.liferay.osb.faro.internal.upgrade.v11_0_0.
				UpgradeFaroNotificationUpgradeProcess());

		registry.register(
			"11.0.0", "12.0.0",
			new com.liferay.osb.faro.internal.upgrade.v12_0_0.
				UpgradeFaroProjectUpgradeProcess());

		registry.register(
			"12.0.0", "13.0.0",
			new com.liferay.osb.faro.internal.upgrade.v13_0_0.
				UpgradeFaroUserUpgradeProcess());

		registry.register(
			"13.0.0", "14.0.0",
			new com.liferay.osb.faro.internal.upgrade.v14_0_0.
				UpgradeFaroChannelUpgradeProcess());

		registry.register(
			"14.0.0", "15.0.0",
			new com.liferay.osb.faro.internal.upgrade.v15_0_0.
				UpgradeFaroProjectUpgradeProcess());

		registry.register(
			"15.0.0", "16.0.0",
			new com.liferay.osb.faro.internal.upgrade.v16_0_0.
				UpgradeUserGroupRoleUpgradeProcess());

		registry.register(
			"16.0.0", "17.0.0",
			new com.liferay.osb.faro.internal.upgrade.v17_0_0.
				UpgradeFaroProjectEmailDomainUpgradeProcess());

		registry.register(
			"17.0.0", "17.0.1",
			UpgradeProcessFactory.runSQL(
				"update LayoutSet set themeId = " +
					"'osbfarotheme_WAR_osbfarotheme' where themeId = " +
						"'osbfaro_WAR_osbfarotheme'"));

		registry.register(
			"17.0.1", "17.0.2",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"OSBFaro_FaroChannel", "OSBFaro_FaroNotification",
						"OSBFaro_FaroPreferences", "OSBFaro_FaroProject",
						"OSBFaro_FaroProjectEmailDomain", "OSBFaro_FaroUser"
					};
				}

			});

		registry.register(
			"17.0.2", "18.0.0",
			new com.liferay.osb.faro.internal.upgrade.v18_0_0.
				UpgradeCompanyId());

		registry.register(
			"18.0.0", "19.0.0",
			new com.liferay.osb.faro.internal.upgrade.v19_0_0.
				UpgradeFaroProjectUpgradeProcess());

		registry.register(
			"19.0.0", "20.0.0",
			UpgradeProcessFactory.runSQL(
				"update OSBFaro_FaroProject set subscriptionModifiedTime = " +
					"createTime"));

		registry.register(
			"20.0.0", "21.0.0",
			UpgradeProcessFactory.runSQL(
				"update OSBFaro_FaroProject set dataSourceConnected = " +
					"[$TRUE$]"));

		registry.register(
			"21.0.0", "21.0.1",
			UpgradeProcessFactory.runSQL(
				"update Layout set colorSchemeId = '', themeId = '' where " +
					"themeId = 'osbfarotheme_WAR_osbfarotheme'",
				StringBundler.concat(
					"update LayoutSet set colorSchemeId = '",
					PropsValues.DEFAULT_REGULAR_COLOR_SCHEME_ID,
					"', themeId = '", PropsValues.DEFAULT_REGULAR_THEME_ID,
					"' where themeId = 'osbfarotheme_WAR_osbfarotheme'")));
	}

}