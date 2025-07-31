/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;

/**
 * @author Christian Dorado
 */
public class ViewHomeQuickActionsDisplayContext {

	public ViewHomeQuickActionsDisplayContext(ThemeDisplay themeDisplay) {
		_themeDisplay = themeDisplay;
	}

	public Map<String, Object> getConstants() {
		return HashMapBuilder.<String, Object>put(
			"ercContentStructures",
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES
		).put(
			"ercFileTypes",
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
		).build();
	}

	public Map<String, Object> getReactData() throws PortalException {
		return HashMapBuilder.<String, Object>put(
			"constants", getConstants()
		).build();
	}

	private final ThemeDisplay _themeDisplay;

}