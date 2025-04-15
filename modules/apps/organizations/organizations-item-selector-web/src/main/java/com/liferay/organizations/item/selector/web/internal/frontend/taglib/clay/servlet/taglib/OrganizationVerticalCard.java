/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.item.selector.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseVerticalCard;
import com.liferay.petra.string.StringPool;
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
		return "organizations";
	}

	@Override
	public String getImageSrc() {
		return _organization.getLogoURL();
	}

	@Override
	public String getStickerCssClass() {
		return StringPool.BLANK;
	}

	@Override
	public String getStickerIcon() {
		return null;
	}

	@Override
	public String getStickerImageSrc() {
		return null;
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