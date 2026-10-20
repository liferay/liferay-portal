/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.provisioning.client.constants;

/**
 * @author Marcos Martins
 */
public class KoroneikiConstants {

	public static final String ACCOUNT_KEY_PREFIX = "KOR-";

	public static final String CONTACT_ROLE_NAME_MEMBER = "Member";

	public static final String DOMAIN_ANALYTICS_CLOUD = "analytics-cloud";

	public static final String DOMAIN_SALESFORCE = "salesforce";

	public static final String DOMAIN_WEB = "web";

	public static final String ENTITY_NAME_ACCOUNT = "account";

	public static final String ENTITY_NAME_CORP_PROJECT = "corp-project";

	public static final String ENTITY_NAME_WORKSPACE = "workspace";

	public static String translateContactRoleName(String roleName) {
		if (roleName.equals("OSB Corp Analytics Cloud Owner")) {
			return "Analytics Cloud Owner";
		}
		else if (roleName.equals("OSB Corp LCS User")) {
			return "LCS User";
		}

		return roleName;
	}

}