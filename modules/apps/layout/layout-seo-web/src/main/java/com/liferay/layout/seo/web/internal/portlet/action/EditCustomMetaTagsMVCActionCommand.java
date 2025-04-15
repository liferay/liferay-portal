/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.web.internal.portlet.action;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.seo.model.LayoutSEOEntryCustomMetaTagProperty;
import com.liferay.layout.seo.service.LayoutSEOEntryService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout/edit_custom_meta_tags"
	},
	service = MVCActionCommand.class
)
public class EditCustomMetaTagsMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		boolean privateLayout = ParamUtil.getBoolean(
			actionRequest, "privateLayout");
		long layoutId = ParamUtil.getLong(actionRequest, "layoutId");

		_layoutSEOEntryService.updateCustomMetaTags(
			groupId, privateLayout, layoutId,
			_getLayoutSEOEntryCustomMetaTagProperties(actionRequest, groupId),
			ServiceContextFactory.getInstance(
				Layout.class.getName(), actionRequest));

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		Layout layout = _layoutLocalService.getLayout(
			groupId, privateLayout, layoutId);

		if (Validator.isNull(redirect)) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			redirect = _portal.getLayoutFullURL(layout, themeDisplay);
		}

		String portletResource = ParamUtil.getString(
			actionRequest, "portletResource");

		MultiSessionMessages.add(
			actionRequest, portletResource + "layoutUpdated", layout);

		actionRequest.setAttribute(WebKeys.REDIRECT, redirect);
	}

	private List<LayoutSEOEntryCustomMetaTagProperty>
		_getLayoutSEOEntryCustomMetaTagProperties(
			ActionRequest actionRequest, long groupId) {

		List<LayoutSEOEntryCustomMetaTagProperty>
			layoutSEOEntryCustomMetaTagProperties = new ArrayList<>();

		Set<Locale> locales = _language.getAvailableLocales(groupId);

		String[] propertiesIndexes = StringUtil.split(
			ParamUtil.getString(actionRequest, "propertiesIndexes"));

		for (String propertyIndex : propertiesIndexes) {
			String property = ParamUtil.getString(
				actionRequest, "property" + propertyIndex);

			if (Validator.isNull(property)) {
				continue;
			}

			Map<Locale, String> contentMap = new HashMap<>();

			for (Locale locale : locales) {
				String content = ParamUtil.getString(
					actionRequest,
					StringBundler.concat(
						"content", propertyIndex, StringPool.UNDERLINE,
						_language.getLanguageId(locale)));

				if (Validator.isNotNull(content)) {
					contentMap.put(locale, content);
				}
			}

			if (MapUtil.isNotEmpty(contentMap)) {
				layoutSEOEntryCustomMetaTagProperties.add(
					new LayoutSEOEntryCustomMetaTagProperty(
						contentMap, property));
			}
		}

		return layoutSEOEntryCustomMetaTagProperties;
	}

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutSEOEntryService _layoutSEOEntryService;

	@Reference
	private Portal _portal;

}