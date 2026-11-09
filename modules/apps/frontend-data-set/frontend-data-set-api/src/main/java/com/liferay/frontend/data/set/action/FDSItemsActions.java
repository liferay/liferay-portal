/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.action;

import com.liferay.frontend.data.set.FDSEntryItemImportPolicy;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * Provides the actions of each item of a frontend data set.
 *
 * <p>
 * Item actions can be nested. An action of type {@code group} renders its
 * items as a section, and an action of type {@code contextual} renders them in
 * a submenu. A {@code data.permissionKey} on a group hides the group and all
 * its items when the permission is not granted.
 * </p>
 *
 * <p>
 * Importing the frontend data set into Data Set Manager handles nested actions
 * according to {@link #getFDSEntryItemImportPolicy()}:
 * </p>
 *
 * <ul>
 * <li>
 * {@code ITEM_PROXY}, the default, keeps only the top level actions that have a
 * {@code data.id}, together with their nested actions. A group without one is
 * skipped along with all its nested actions, and only a warning is logged, so
 * give every group a {@code data.id}.
 * </li>
 * <li>
 * {@code DETACHED} imports every nested action that has a {@code data.id} as a
 * separate action and drops the groups. An action without a {@code
 * data.permissionKey} takes the one of its closest group that has one, except
 * a {@code headless} action, because its permission key also selects the
 * endpoint it calls. An action with its own permission key keeps only that one,
 * so the permission key of its groups no longer applies to it.
 * </li>
 * </ul>
 *
 * @author Daniel Sanz
 */
public interface FDSItemsActions {

	public List<FDSActionDropdownItem> getFDSActionDropdownItems(
		HttpServletRequest httpServletRequest);

	public default FDSEntryItemImportPolicy getFDSEntryItemImportPolicy() {
		return FDSEntryItemImportPolicy.ITEM_PROXY;
	}

}