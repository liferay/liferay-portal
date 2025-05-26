/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.constants;

/**
 * @author Zsolt Balogh
 */
public class PatcherConstants {

	public static final String CURRENTLY_INSTALLED_PATCHES_REGEX =
		"Currently installed patches:\\s*(.*)";

	public static final String FIX_COMPONENT_REGEX = "^([a-z-]+)$";

	public static final String FIX_PACK_TAG_REGEX =
		"^(fix-pack-([a-z]+)-)(\\d+)-(\\d+)(-private)?$";

	public static final String FIX_PACKS_REGEX = "^([a-z-]+-[0-9]+)$";

	public static final String GIT_REMOTE_URL_REGEX =
		"git@github\\.com:.+\\/(\\w+\\-)+\\w+\\.git";

	public static final String GIT_REMOTE_URL_REPOSITORY_REGEX =
		"git@github\\.com:.+\\/(.+)\\.git";

	public static final String HELP_CENTER_ACCOUNT_ID_REGEX =
		"accountEntryId:(\\d*)";

	public static final String INVALID_TICKET_KEY = "LPS-";

	public static final String LIFERAY_HOTFIX_FILENAME_REGEX =
		"\\/([A-Za-z0-9\\.\\s-]*\\.(lpkg|zip))$";

	public static final String LIFERAY_HOTFIX_ID_REGEX = "(\\d+)(?=\\.zip)";

	public static final String LIFERAY_PORTAL_REPOSITORY_REGEX =
		"^liferay-portal-ee$";

	public static final String REQUIREMENTS_REGEX = "([A-Za-z-]+)>=([0-9]+)";

	public static final String TICKET_NAME_6X_REGEX = "^(LP[A-Z]+-[0-9]+)$";

	public static final String TICKET_NAME_ALL_REGEX =
		"^([A-Z]+[0-9]*-[0-9]+)$";

}