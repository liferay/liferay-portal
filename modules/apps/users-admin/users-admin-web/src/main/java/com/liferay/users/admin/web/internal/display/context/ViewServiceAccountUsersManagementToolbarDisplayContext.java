/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.display.context;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.users.admin.management.toolbar.FilterContributor;

/**
 * @author Pei-Jung Lan
 */
public class ViewServiceAccountUsersManagementToolbarDisplayContext
	extends ViewFlatUsersManagementToolbarDisplayContext {

	public ViewServiceAccountUsersManagementToolbarDisplayContext(
		FilterContributor[] filterContributors,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<User> searchContainer, boolean showDeleteButton,
		boolean showRestoreButton) {

		super(
			filterContributors, liferayPortletRequest, liferayPortletResponse,
			searchContainer, showDeleteButton, showRestoreButton);
	}

	@Override
	public Boolean isShowCreationMenu() {
		return false;
	}

}