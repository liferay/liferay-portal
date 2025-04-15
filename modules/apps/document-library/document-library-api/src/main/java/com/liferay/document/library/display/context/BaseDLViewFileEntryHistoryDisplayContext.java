/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileVersion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.UUID;

/**
 * @author Mauro Mariuzzo
 */
public class BaseDLViewFileEntryHistoryDisplayContext
	extends BaseDLDisplayContext<DLViewFileEntryHistoryDisplayContext>
	implements DLViewFileEntryHistoryDisplayContext {

	public BaseDLViewFileEntryHistoryDisplayContext(
		UUID uuid,
		DLViewFileEntryHistoryDisplayContext
			parentDLViewFileEntryHistoryDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileVersion fileVersion) {

		super(
			uuid, parentDLViewFileEntryHistoryDisplayContext,
			httpServletRequest, httpServletResponse);

		this.fileVersion = fileVersion;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() throws PortalException {
		return parentDisplayContext.getActionDropdownItems();
	}

	protected FileVersion fileVersion;

}