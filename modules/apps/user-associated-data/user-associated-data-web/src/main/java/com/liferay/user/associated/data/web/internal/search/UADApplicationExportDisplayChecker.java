/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.user.associated.data.web.internal.display.UADApplicationExportDisplay;

import jakarta.portlet.PortletResponse;

/**
 * @author Pei-Jung Lan
 */
public class UADApplicationExportDisplayChecker extends EmptyOnClickRowChecker {

	public UADApplicationExportDisplayChecker(PortletResponse portletResponse) {
		super(portletResponse);
	}

	@Override
	public boolean isDisabled(Object object) {
		UADApplicationExportDisplay uadApplicationExportDisplay =
			(UADApplicationExportDisplay)object;

		if (!uadApplicationExportDisplay.isExportSupported() ||
			(uadApplicationExportDisplay.getDataCount() == 0)) {

			return true;
		}

		return false;
	}

}