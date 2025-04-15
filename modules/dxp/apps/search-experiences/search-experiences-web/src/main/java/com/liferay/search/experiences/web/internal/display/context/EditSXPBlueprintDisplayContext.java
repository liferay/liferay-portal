/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.web.internal.display.context;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.GroupItemSelectorReturnType;
import com.liferay.learn.LearnMessageUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.item.selector.SiteItemSelectorCriterion;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class EditSXPBlueprintDisplayContext {

	public EditSXPBlueprintDisplayContext(
		ItemSelector itemSelector, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_itemSelector = itemSelector;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getProps() {
		return HashMapBuilder.<String, Object>put(
			"defaultLocale", LocaleUtil.toLanguageId(LocaleUtil.getDefault())
		).put(
			"fetchSitesURL",
			ResourceURLBuilder.createResourceURL(
				_renderResponse
			).setCMD(
				"getSitesJSONObject"
			).setResourceID(
				"/sxp_blueprint_admin/get_sites"
			).buildString()
		).put(
			"isCompanyAdmin",
			() -> {
				PermissionChecker permissionChecker =
					_themeDisplay.getPermissionChecker();

				return permissionChecker.isCompanyAdmin();
			}
		).put(
			"learnMessages",
			LearnMessageUtil.getJSONObject("search-experiences-web")
		).put(
			"locale", _themeDisplay.getLanguageId()
		).put(
			"namespace", _renderResponse.getNamespace()
		).put(
			"redirectURL", getRedirect()
		).put(
			"selectSitesURL",
			() -> {
				ItemSelectorCriterion itemSelectorCriterion =
					new SiteItemSelectorCriterion();

				itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
					new GroupItemSelectorReturnType());

				return PortletURLBuilder.create(
					_itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							_renderRequest),
						_renderResponse.getNamespace() + "selectSite",
						itemSelectorCriterion)
				).buildString();
			}
		).put(
			"sxpBlueprintId",
			ParamUtil.getLong(_renderRequest, "sxpBlueprintId")
		).build();
	}

	public String getRedirect() {
		if (Validator.isNotNull(_redirect)) {
			return _redirect;
		}

		String redirect = ParamUtil.getString(_renderRequest, "redirect");

		if (Validator.isNull(redirect)) {
			redirect = PortletURLBuilder.createRenderURL(
				_renderResponse
			).setMVCRenderCommandName(
				"/sxp_blueprint_admin/view_sxp_blueprints"
			).buildString();
		}

		_redirect = redirect;

		return _redirect;
	}

	private final ItemSelector _itemSelector;
	private String _redirect;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}