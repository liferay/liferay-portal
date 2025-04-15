/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.layout.model.LayoutClassedModelUsage;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public interface LayoutClassedModelUsageActionMenuContributor {

	public List<DropdownItem> getLayoutClassedModelUsageActionDropdownItems(
		HttpServletRequest httpServletRequest,
		LayoutClassedModelUsage layoutClassedModelUsage);

}