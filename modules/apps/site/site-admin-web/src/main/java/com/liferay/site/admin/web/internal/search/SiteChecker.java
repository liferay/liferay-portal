/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletResponse;

/**
 * @author Jorge Ferrer
 */
public class SiteChecker extends EmptyOnClickRowChecker {

	public SiteChecker(PortletResponse portletResponse) {
		super(portletResponse);
	}

	@Override
	public boolean isDisabled(Object object) {
		Group group = (Group)object;

		try {
			if (group.isCompany() ||
				PortalUtil.isSystemGroup(group.getGroupKey())) {

				return true;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return super.isDisabled(object);
	}

	private static final Log _log = LogFactoryUtil.getLog(SiteChecker.class);

}