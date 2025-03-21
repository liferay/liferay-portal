/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.web.internal.search;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletRequest;

/**
 * Defines display terms used by the <code>SearchIteratorTag</code> (in
 * <code>com.liferay.util.taglib</code>) to render the list of Recycle Bin
 * entries.
 *
 * <p>
 * Supported display terms:
 * </p>
 *
 * <ul>
 * <li>
 * <code>name</code> - the name of the entry
 * </li>
 * <li>
 * <code>removedDate</code> - the date the entry was moved to the Recycle Bin
 * </li>
 * <li>
 * <code>removedBy</code> - the user who moved the entry to the Recycle Bin
 * </li>
 * <li>
 * <code>type</code> - the type of entry that was moved to the Recycle Bin
 * </li>
 * </ul>
 *
 * @author Sergio González
 */
public class EntryDisplayTerms extends DisplayTerms {

	public static final String NAME = "name";

	public static final String REMOVED_BY = "removedBy";

	public static final String REMOVED_DATE = "removedDate";

	public static final String TYPE = "type";

	public EntryDisplayTerms(PortletRequest portletRequest) {
		super(portletRequest);

		name = ParamUtil.getString(portletRequest, NAME);
		removedDate = ParamUtil.getString(portletRequest, REMOVED_DATE);
		removedBy = ParamUtil.getString(portletRequest, REMOVED_BY);
		type = ParamUtil.getString(portletRequest, TYPE);
	}

	public String getName() {
		return name;
	}

	public String getRemovedBy() {
		return removedBy;
	}

	public String getRemovedDate() {
		return removedDate;
	}

	public String getType() {
		return type;
	}

	protected String name;
	protected String removedBy;
	protected String removedDate;
	protected String type;

}