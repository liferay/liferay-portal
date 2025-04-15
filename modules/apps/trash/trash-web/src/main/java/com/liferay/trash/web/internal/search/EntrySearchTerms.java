/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.web.internal.search;

import com.liferay.portal.kernel.dao.search.DAOParamUtil;

import jakarta.portlet.PortletRequest;

/**
 * Defines search terms used by the <code>SearchContainer</code> (in
 * <code>com.liferay.portal.kernel</code>) to filter Recycle Bin entries.
 *
 * <p>
 * Supported search parameters:
 * </p>
 *
 * <ul>
 * <li>
 * <code>keywords</code> - the keywords for which to search in the entries
 * content
 * </li>
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
public class EntrySearchTerms extends EntryDisplayTerms {

	public EntrySearchTerms(PortletRequest portletRequest) {
		super(portletRequest);

		keywords = DAOParamUtil.getString(portletRequest, KEYWORDS);
		name = DAOParamUtil.getString(portletRequest, NAME);
		removedDate = DAOParamUtil.getString(portletRequest, REMOVED_DATE);
		removedBy = DAOParamUtil.getString(portletRequest, REMOVED_BY);
		type = DAOParamUtil.getString(portletRequest, TYPE);
	}

}