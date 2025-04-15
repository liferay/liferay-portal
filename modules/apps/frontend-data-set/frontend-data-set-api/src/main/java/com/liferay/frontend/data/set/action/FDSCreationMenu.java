/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.action;

import com.liferay.frontend.data.set.FDSEntryItemImportPolicy;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Daniel Sanz
 */
public interface FDSCreationMenu {

	public CreationMenu getCreationMenu(HttpServletRequest httpServletRequest);

	public default FDSEntryItemImportPolicy getFDSEntryItemImportPolicy() {
		return FDSEntryItemImportPolicy.ITEM_PROXY;
	}

}