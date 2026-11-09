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
 * submenu. A {@code data.permissionKey} on a group hides the group and all its
 * items when the permission is not granted.
 * </p>
 *
 * <p>
 * Importing the frontend data set into Data Set Manager handles nested items
 * according to {@link #getFDSEntryItemImportPolicy()}:
 * </p>
 *
 * <ul>
 * <li>
 * {@code ITEM_PROXY}, the default, keeps only the top level items that have a
 * {@code data.id}, together with their nested items. A group without one is
 * skipped along with all its nested items, and only a warning is logged, so
 * give every group a {@code data.id}.
 * </li>
 * <li>
 * {@code DETACHED} imports every nested item as a separate action and drops the
 * groups. An item without a {@code data.permissionKey} takes the one of its
 * closest group that has one. An item with its own permission key keeps only
 * that one, so the permission key of its groups no longer applies to it.
 * </li>
 * </ul>
 *
 * @author Daniel Sanz
 */
public interface FDSCreationMenu {

	public CreationMenu getCreationMenu(HttpServletRequest httpServletRequest);

	public default FDSEntryItemImportPolicy getFDSEntryItemImportPolicy() {
		return FDSEntryItemImportPolicy.ITEM_PROXY;
	}

}