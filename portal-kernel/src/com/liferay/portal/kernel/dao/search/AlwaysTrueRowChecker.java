/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.search;

import jakarta.portlet.RenderResponse;

/**
 * @author Raymond Augé
 */
public class AlwaysTrueRowChecker extends RowChecker {

	public AlwaysTrueRowChecker(RenderResponse renderResponse) {
		super(renderResponse);
	}

	@Override
	public boolean isChecked(Object object) {
		return true;
	}

}