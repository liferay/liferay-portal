/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.web.internal.util;

import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.layout.utility.page.kernel.LayoutUtilityPageEntryTypeUtil;
import com.liferay.layout.utility.page.kernel.provider.util.LayoutUtilityPageEntryLayoutProviderUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ListMergeable;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Cristina González
 */
public class TitleProvider {

	public TitleProvider(LayoutSEOLinkManager layoutSEOLinkManager) {
		_layoutSEOLinkManager = layoutSEOLinkManager;
	}

	public String getTitle(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String layoutUtilityPageEntryType =
			_getStatusLayoutUtilityPageEntryType(themeDisplay);

		if (Validator.isNotNull(layoutUtilityPageEntryType)) {
			Layout layout =
				LayoutUtilityPageEntryLayoutProviderUtil.
					getDefaultLayoutUtilityPageEntryLayout(
						themeDisplay.getScopeGroupId(),
						layoutUtilityPageEntryType);

			if (layout != null) {
				Company company = themeDisplay.getCompany();

				return _layoutSEOLinkManager.getFullPageTitle(
					layout, null, themeDisplay.getTilesTitle(), null, null,
					company.getName(), themeDisplay.getLocale());
			}
		}

		String portletId = (String)httpServletRequest.getAttribute(
			WebKeys.PORTLET_ID);

		ListMergeable<String> titleListMergeable =
			(ListMergeable<String>)httpServletRequest.getAttribute(
				WebKeys.PAGE_TITLE);
		ListMergeable<String> subtitleListMergeable =
			(ListMergeable<String>)httpServletRequest.getAttribute(
				WebKeys.PAGE_SUBTITLE);

		Company company = themeDisplay.getCompany();

		String title = _layoutSEOLinkManager.getFullPageTitle(
			themeDisplay.getLayout(), portletId, themeDisplay.getTilesTitle(),
			titleListMergeable, subtitleListMergeable, company.getName(),
			themeDisplay.getLocale());

		String titleModifierKey = _getTitleModifierKey(httpServletRequest);

		if (Validator.isNotNull(titleModifierKey)) {
			StringBundler sb = new StringBundler(5);

			sb.append(title);
			sb.append(StringPool.SPACE);
			sb.append(StringPool.OPEN_PARENTHESIS);
			sb.append(
				LanguageUtil.get(themeDisplay.getLocale(), titleModifierKey));
			sb.append(StringPool.CLOSE_PARENTHESIS);

			return sb.toString();
		}

		return title;
	}

	private String _getStatusLayoutUtilityPageEntryType(
		ThemeDisplay themeDisplay) {

		HttpServletResponse httpServletResponse = themeDisplay.getResponse();

		if (httpServletResponse == null) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			if (serviceContext != null) {
				httpServletResponse = serviceContext.getResponse();
			}
		}

		if (httpServletResponse == null) {
			return null;
		}

		return LayoutUtilityPageEntryTypeUtil.
			getStatusLayoutUtilityPageEntryType(
				httpServletResponse.getStatus());
	}

	private String _getTitleModifierKey(HttpServletRequest httpServletRequest) {
		if (_isEditMode(httpServletRequest)) {
			return "editing";
		}
		else if (_isConfigurationMode(httpServletRequest)) {
			return "configuring";
		}

		return null;
	}

	private boolean _isConfigurationMode(
		HttpServletRequest httpServletRequest) {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			httpServletRequest = serviceContext.getRequest();
		}

		String mvcCommand = ParamUtil.getString(
			httpServletRequest, "mvcRenderCommandName");

		return mvcCommand.equals("/layout_admin/edit_layout");
	}

	private boolean _isEditMode(HttpServletRequest httpServletRequest) {
		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(httpServletRequest);

		String layoutMode = ParamUtil.getString(
			originalHttpServletRequest, "p_l_mode", Constants.VIEW);

		return layoutMode.equals(Constants.EDIT);
	}

	private final LayoutSEOLinkManager _layoutSEOLinkManager;

}