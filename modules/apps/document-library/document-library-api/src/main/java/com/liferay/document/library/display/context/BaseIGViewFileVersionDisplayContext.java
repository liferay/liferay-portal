/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.repository.model.FileVersion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.UUID;

/**
 * @author Iván Zaera
 */
public class BaseIGViewFileVersionDisplayContext
	extends BaseIGDisplayContext<IGViewFileVersionDisplayContext>
	implements IGViewFileVersionDisplayContext {

	public BaseIGViewFileVersionDisplayContext(
		UUID uuid, IGViewFileVersionDisplayContext parentIGDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileVersion fileVersion) {

		super(
			uuid, parentIGDisplayContext, httpServletRequest,
			httpServletResponse);

		this.fileVersion = fileVersion;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return parentDisplayContext.getActionDropdownItems();
	}

	protected FileVersion fileVersion;

}