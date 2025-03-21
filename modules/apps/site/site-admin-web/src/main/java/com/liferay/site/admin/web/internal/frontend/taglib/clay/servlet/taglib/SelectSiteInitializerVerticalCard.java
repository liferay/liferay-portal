/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.admin.web.internal.util.SiteInitializerItem;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class SelectSiteInitializerVerticalCard implements VerticalCard {

	public SelectSiteInitializerVerticalCard(
		SiteInitializerItem siteInitializerItem, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_siteInitializerItem = siteInitializerItem;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
	}

	@Override
	public String getAriaLabel() {
		return LanguageUtil.format(
			_httpServletRequest, "select-x-x",
			new Object[] {"template", _siteInitializerItem.getName()});
	}

	@Override
	public String getCssClass() {
		return "add-site-action-card card-interactive " +
			"card-interactive-primary c-mb-2";
	}

	@Override
	public Map<String, String> getDynamicAttributes() {
		return HashMapBuilder.put(
			"data-add-site-url",
			() -> PortletURLBuilder.createRenderURL(
				_renderResponse
			).setMVCRenderCommandName(
				"/site_admin/add_group"
			).setBackURL(
				ParamUtil.getString(_httpServletRequest, "redirect")
			).setParameter(
				"creationType", _siteInitializerItem.getType()
			).setParameter(
				"layoutSetPrototypeId",
				_siteInitializerItem.getLayoutSetPrototypeId()
			).setParameter(
				"parentGroupId",
				ParamUtil.getLong(_httpServletRequest, "parentGroupId")
			).setParameter(
				"siteInitializerKey",
				_siteInitializerItem.getSiteInitializerKey()
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString()
		).put(
			"role", "button"
		).put(
			"tabIndex", "0"
		).build();
	}

	@Override
	public String getIcon() {
		return "site-template";
	}

	@Override
	public String getImageAlt() {
		return StringPool.BLANK;
	}

	@Override
	public String getImageSrc() {
		if (_siteInitializerItem.isCreationTypeSiteTemplate() ||
			Validator.isNull(_siteInitializerItem.getIcon())) {

			return null;
		}

		return PortalUtil.getPathProxy() + _siteInitializerItem.getIcon();
	}

	@Override
	public String getTitle() {
		return _siteInitializerItem.getName();
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	private final HttpServletRequest _httpServletRequest;
	private final RenderResponse _renderResponse;
	private final SiteInitializerItem _siteInitializerItem;

}