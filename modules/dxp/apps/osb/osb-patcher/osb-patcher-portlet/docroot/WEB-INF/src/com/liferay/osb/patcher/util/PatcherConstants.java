/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.patcher.util;

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