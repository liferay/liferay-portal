/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.ldap;

import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.Properties;

/**
 * @author Edward Han
 */
public class LDAPSettingsUtil {

	public static Properties getContactExpandoMappings(
			long ldapServerId, long companyId)
		throws Exception {

		LDAPSettings ldapSettings = _ldapSettingsSnapshot.get();

		return ldapSettings.getContactExpandoMappings(ldapServerId, companyId);
	}

	public static Properties getContactMappings(
			long ldapServerId, long companyId)
		throws Exception {

		LDAPSettings ldapSettings = _ldapSettingsSnapshot.get();

		return ldapSettings.getContactMappings(ldapServerId, companyId);
	}

	public static String[] getErrorPasswordHistoryKeywords(long companyId) {
		LDAPSettings ldapSettings = _ldapSettingsSnapshot.get();

		return ldapSettings.getErrorPasswordHistoryKeywords(companyId);
	}

	public static Properties getGroupMappings(long ldapServerId, long companyId)
		throws Exception {

		LDAPSettings ldapSettings = _ldapSettingsSnapshot.get();

		return ldapSettings.getGroupMappings(ldapServerId, companyId);
	}

	public static long getPreferredLDAPServerId(
		long companyId, String screenName) {

		LDAPSettings ldapSettings = _ldapSettingsSnapshot.get();

		return ldapSettings.getPreferredLDAPServerId(companyId, screenName);
	}

	public static String getPropertyPostfix(long ldapServerId) {
		LDAPSettings ldapSettings = _ldapSettingsSnapshot.get();

		return ldapSettings.getPropertyPostfix(ldapServerId);
	}

	public static Properties getUserExpandoMappings(
			long ldapServerId, long companyId)
		throws Exception {

		LDAPSettings ldapSettings = _ldapSettingsSnapshot.get();

		return ldapSettings.getUserExpandoMappings(ldapServerId, companyId);
	}

	public static Properties getUserMappings(long ldapServerId, long companyId)
		throws Exception {

		LDAPSettings ldapSettings = _ldapSettingsSnapshot.get();

		return ldapSettings.getUserMappings(ldapServerId, companyId);
	}

	public static boolean isExportEnabled(long companyId) {
		LDAPSettings ldapSettings = _ldapSettingsSnapshot.get();

		return ldapSettings.isExportEnabled(companyId);
	}

	public static boolean isExportGroupEnabled(long companyId) {
		LDAPSettings ldapSettings = _ldapSettingsSnapshot.get();

		return ldapSettings.isExportGroupEnabled(companyId);
	}

	public static boolean isImportEnabled(long companyId) {
		LDAPSettings ldapSettings = _ldapSettingsSnapshot.get();

		return ldapSettings.isImportEnabled(companyId);
	}

	public static boolean isImportOnStartup(long companyId) {
		LDAPSettings ldapSettings = _ldapSettingsSnapshot.get();

		return ldapSettings.isImportOnStartup(companyId);
	}

	public static boolean isPasswordPolicyEnabled(long companyId) {
		LDAPSettings ldapSettings = _ldapSettingsSnapshot.get();

		return ldapSettings.isPasswordPolicyEnabled(companyId);
	}

	public static boolean isPasswordPolicyEnabled(
		long ldapServerId, long companyId) {

		LDAPSettings ldapSettings = _ldapSettingsSnapshot.get();

		return ldapSettings.isPasswordPolicyEnabled(ldapServerId, companyId);
	}

	private static final Snapshot<LDAPSettings> _ldapSettingsSnapshot =
		new Snapshot<>(LDAPSettingsUtil.class, LDAPSettings.class);

}