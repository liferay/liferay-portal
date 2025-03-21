/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseVerticalCard;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Organization;

import jakarta.portlet.RenderRequest;

/**
 * @author Eudaldo Alonso
 */
public class OrganizationVerticalCard extends BaseVerticalCard {

	public OrganizationVerticalCard(
		Organization organization, RenderRequest renderRequest,
		RowChecker rowChecker) {

		super(organization, renderRequest, rowChecker);

		_organization = organization;
	}

	@Override
	public String getIcon() {
		return "users";
	}

	@Override
	public String getInputValue() {
		return String.valueOf(_organization.getGroupId());
	}

	@Override
	public String getSubtitle() {
		return LanguageUtil.get(
			themeDisplay.getLocale(), _organization.getType());
	}

	@Override
	public String getTitle() {
		return _organization.getName();
	}

	private final Organization _organization;

}