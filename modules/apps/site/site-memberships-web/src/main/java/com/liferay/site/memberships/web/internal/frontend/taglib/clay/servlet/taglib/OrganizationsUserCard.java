/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseBaseClayCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.UserCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.site.memberships.web.internal.servlet.taglib.util.OrganizationActionDropdownItemsProvider;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class OrganizationsUserCard
	extends BaseBaseClayCard implements UserCard {

	public OrganizationsUserCard(
		Organization organization, RenderRequest renderRequest,
		RenderResponse renderResponse, RowChecker rowChecker) {

		super(organization, rowChecker);

		_organization = organization;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		try {
			OrganizationActionDropdownItemsProvider
				organizationActionDropdownItemsProvider =
					new OrganizationActionDropdownItemsProvider(
						_organization, _renderRequest, _renderResponse);

			return organizationActionDropdownItemsProvider.
				getActionDropdownItems();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public String getImageSrc() {
		return _organization.getLogoURL();
	}

	@Override
	public String getName() {
		return _organization.getName();
	}

	@Override
	public String getSubtitle() {
		return LanguageUtil.get(_httpServletRequest, _organization.getType());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OrganizationsUserCard.class);

	private final HttpServletRequest _httpServletRequest;
	private final Organization _organization;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}