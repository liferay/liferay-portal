/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.ldap;

import java.util.Properties;

/**
 * @author Edward C. Han
 */
public interface LDAPSettings {

	public Properties getContactExpandoMappings(
			long ldapServerId, long companyId)
		throws Exception;

	public Properties getContactMappings(long ldapServerId, long companyId)
		throws Exception;

	public String[] getErrorPasswordHistoryKeywords(long companyId);

	public Properties getGroupMappings(long ldapServerId, long companyId)
		throws Exception;

	public long getPreferredLDAPServerId(long companyId, String screenName);

	public String getPropertyPostfix(long ldapServerId);

	public Properties getUserExpandoMappings(long ldapServerId, long companyId)
		throws Exception;

	public Properties getUserMappings(long ldapServerId, long companyId)
		throws Exception;

	public boolean isExportEnabled(long companyId);

	public boolean isExportGroupEnabled(long companyId);

	public boolean isImportEnabled(long companyId);

	public boolean isImportOnStartup(long companyId);

	public boolean isPasswordPolicyEnabled(long companyId);

	public boolean isPasswordPolicyEnabled(long ldapServerId, long companyId);

}