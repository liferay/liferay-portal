/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.configuration.definition;

import com.liferay.bookmarks.configuration.BookmarksGroupServiceConfigurationOverride;
import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.portal.kernel.settings.TypedSettings;

/**
 * @author Iván Zaera
 */
public class BookmarksGroupServiceConfigurationOverrideImpl
	implements BookmarksGroupServiceConfigurationOverride {

	public BookmarksGroupServiceConfigurationOverrideImpl(
		TypedSettings typedSettings) {

		_typedSettings = typedSettings;
	}

	@Override
	public long rootFolderId() {
		return _typedSettings.getLongValue(
			"rootFolderId", BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID);
	}

	private final TypedSettings _typedSettings;

}