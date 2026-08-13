/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.resource.type;

import com.liferay.depot.model.DepotEntry;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Contributes a resource type to the Design Library Admin. Register one
 * component per type from the module that owns the type, and the type appears
 * without any change to the Design Library Admin itself.
 *
 * <p>
 * The listing is backed by a single search query, so a contributed type has two
 * indexing requirements. Its entries must be indexed with a
 * <code>groupId</code> field, or they never match the query. Its entry class
 * name must also have a <code>DTOConverter</code> registered under
 * <code>dto.class.name</code>, or its rows list with empty columns.
 * </p>
 *
 * @author Lourdes Fernández Besada
 * @author Thiago Buarque
 */
public interface DesignLibraryResourceTypeContributor {

	/**
	 * Returns the name of the Clay palette custom property that tints this
	 * type's sticker, such as <code>"purple"</code>. The Design Library Admin
	 * wraps the name as <code>var(--purple)</code>, so omit the leading
	 * <code>--</code>.
	 */
	public String getColor();

	/**
	 * Returns the creation menu items this type contributes to the Design
	 * Library Admin, or an empty list when the type cannot be created. The
	 * Design Library Admin calls this method only when
	 * {@link #hasAddPermission} returns <code>true</code>.
	 *
	 * <p>
	 * Each item names the React module that renders its modal, written as an ES
	 * import declaration such as
	 * <code>"{AddFooDesignLibraryModalContent} from foo-web"</code>, together
	 * with the props to pass to it. Name the component after the action and the
	 * entity it creates, ending in <code>DesignLibraryModalContent</code>, and
	 * export its props type, so that a flat module export still shows which
	 * components belong to this contract. The Design Library Admin resolves the
	 * declaration to an absolute URL, loads the module when the user clicks the
	 * item, and passes <code>closeModal</code> alongside the props. Build any URL
	 * the modal needs from <code>backURL</code>, so that the user returns to the
	 * Design Library after submitting.
	 * </p>
	 */
	public default List<DesignLibraryResourceCreationItem> getCreationItems(
			HttpServletRequest httpServletRequest, DepotEntry depotEntry,
			String backURL)
		throws PortalException {

		return Collections.emptyList();
	}

	/**
	 * Returns the ID of the action the row title links to, such as
	 * <code>"view"</code> or <code>"edit"</code>. The ID must match an action
	 * returned by {@link #getFDSActionDropdownItems}.
	 */
	public String getDefaultActionId();

	/**
	 * Returns the class name of the model this type lists, such as
	 * <code>Foo.class.getName()</code>.
	 *
	 * <p>
	 * The Design Library Admin adds the name to the search query and, with
	 * {@link #getType}, uses it to decide which rows belong to this type.
	 * Several types may share one class name.
	 * </p>
	 */
	public String getEntryClassName();

	/**
	 * Returns this type's row actions.
	 *
	 * <p>
	 * Actions are declared once for the type rather than once per row, so build
	 * hrefs from templates such as
	 * <code>"{embedded.externalReferenceCode}"</code> that the data set expands
	 * per row. Leave the visibility filters unset. The Design Library Admin
	 * replaces them with filters built from {@link #getEntryClassName} and
	 * {@link #getType}, so that one type's actions never appear on another's
	 * rows.
	 * </p>
	 *
	 * <p>
	 * Because the Design Library Admin replaces the visibility filters, an
	 * action cannot depend on the state of an individual entry.
	 * </p>
	 */
	public List<FDSActionDropdownItem> getFDSActionDropdownItems(
			HttpServletRequest httpServletRequest, DepotEntry depotEntry,
			String backURL)
		throws PortalException;

	/**
	 * Returns the Clay icon symbol shown on this type's rows, such as
	 * <code>"book"</code>.
	 */
	public String getIcon();

	/**
	 * Returns the unique and stable key of this type, such as
	 * <code>"foo"</code>. Unlike {@link #getEntryClassName}, the key identifies
	 * the type even when several types share a model class.
	 */
	public String getKey();

	/**
	 * Returns the localized name of this type, shown in the type column.
	 */
	public String getLabel(Locale locale);

	/**
	 * Returns the value that the <code>type</code> field of a row must hold for
	 * the row to belong to this type, or <code>null</code> when the entry class
	 * name alone identifies it.
	 *
	 * <p>
	 * Override this method only when more than one type shares an entry class
	 * name. Several types may all be backed by <code>Foo</code> and differ only
	 * by the <code>type</code> field, so each returns the same entry class name
	 * and a distinct value here. The Design Library Admin compares the value as
	 * a string against the indexed <code>type</code> field, so an
	 * <code>int</code> constant such as <code>FooConstants.TYPE_BAR</code>
	 * becomes <code>"3"</code>.
	 * </p>
	 */
	public default String getType() {
		return null;
	}

	/**
	 * Returns <code>true</code> when the user may add entries of this type to
	 * this Design Library. The Design Library Admin uses the result to gate the
	 * creation menu items.
	 */
	public boolean hasAddPermission(
		PermissionChecker permissionChecker, DepotEntry depotEntry);

	/**
	 * Returns <code>true</code> when the user may see entries of this type in
	 * this Design Library.
	 */
	public boolean hasViewPermission(
		PermissionChecker permissionChecker, DepotEntry depotEntry);

}