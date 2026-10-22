/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

/**
 * @author Brian Wing Shun Chan
 */
public class LayoutConstants {

	public static final String CUSTOMIZABLE_LAYOUT = "CUSTOMIZABLE_LAYOUT";

	public static final long DEFAULT_PARENT_LAYOUT_ID = 0;

	public static final long DEFAULT_PLID = 0;

	public static final String EXTERNAL_REFERENCE_CODE_SUFFIX_DEFAULT =
		"-default";

	public static final String EXTERNAL_REFERENCE_CODE_SUFFIX_DRAFT = "-draft";

	public static final String EXTERNAL_REFERENCE_CODE_SUFFIX_PRIVATE =
		"-private";

	public static final String EXTERNAL_REFERENCE_CODE_SUFFIX_PUBLISHED =
		"-published";

	public static final int FIRST_PRIORITY = 0;

	public static final int FRIENDLY_URL_MAX_LENGTH = 255;

	public static final String NAME_CONTROL_PANEL_DEFAULT = "Control Panel";

	public static final String TYPE_ASSET_DISPLAY = "asset_display";

	public static final String TYPE_CONTENT = "content";

	public static final String TYPE_CONTROL_PANEL = "control_panel";

	public static final String TYPE_EMBEDDED = "embedded";

	public static final String TYPE_EMPTY = "empty";

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #TYPE_CONTENT}
	 */
	@Deprecated
	public static final String TYPE_FULL_PAGE_APPLICATION =
		"full_page_application";

	public static final String TYPE_LINK_TO_LAYOUT = "link_to_layout";

	public static final String TYPE_NODE = "node";

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #TYPE_CONTENT}
	 */
	@Deprecated
	public static final String TYPE_PANEL = "panel";

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #TYPE_CONTENT}
	 */
	@Deprecated
	public static final String TYPE_PORTLET = "portlet";

	public static final String TYPE_URL = "url";

	public static final String TYPE_UTILITY = "utility";

}