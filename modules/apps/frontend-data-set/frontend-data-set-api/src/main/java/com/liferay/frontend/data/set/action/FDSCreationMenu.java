/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.action;

import com.liferay.frontend.data.set.FDSEntryItemImportPolicy;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Provides the creation menu of a frontend data set.
 *
 * <p>
 * Creation menu items can be nested. An item of type {@code group} renders its
 * items as a section, and an item of type {@code contextual} renders them in a
 * submenu. Give every group a {@code data.id}, because importing the frontend
 * data set into Data Set Manager with the {@code ITEM_PROXY} policy only keeps
 * the top level items that have one, together with their nested items.
 * </p>
 *
 * @author Daniel Sanz
 */
public interface FDSCreationMenu {

	public CreationMenu getCreationMenu(HttpServletRequest httpServletRequest);

	public default FDSEntryItemImportPolicy getFDSEntryItemImportPolicy() {
		return FDSEntryItemImportPolicy.ITEM_PROXY;
	}

}