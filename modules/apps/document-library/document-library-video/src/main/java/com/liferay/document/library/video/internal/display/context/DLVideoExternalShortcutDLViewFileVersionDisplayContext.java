/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.internal.display.context;

import com.liferay.document.library.display.context.BaseDLViewFileVersionDisplayContext;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.document.library.video.internal.util.DLVideoExternalShortcutUIItemsUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileVersion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.UUID;

/**
 * @author Iván Zaera
 * @author Alejandro Tardín
 */
public class DLVideoExternalShortcutDLViewFileVersionDisplayContext
	extends BaseDLViewFileVersionDisplayContext {

	public DLVideoExternalShortcutDLViewFileVersionDisplayContext(
		DLViewFileVersionDisplayContext parentDLDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileVersion fileVersion) {

		super(
			_UUID, parentDLDisplayContext, httpServletRequest,
			httpServletResponse, fileVersion);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() throws PortalException {
		List<DropdownItem> actionDropdownItems = super.getActionDropdownItems();

		DLVideoExternalShortcutUIItemsUtil.processDropdownItems(
			actionDropdownItems);

		return actionDropdownItems;
	}

	private static final UUID _UUID = UUID.fromString(
		"7deb426a-96b9-4db6-88ac-9afbc7fc2151");

}