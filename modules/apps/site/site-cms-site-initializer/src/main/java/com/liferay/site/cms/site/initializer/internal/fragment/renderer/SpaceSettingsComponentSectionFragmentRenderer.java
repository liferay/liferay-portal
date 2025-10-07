/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.util.InfoItemUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Verónica González
 */
@Component(service = FragmentRenderer.class)
public class SpaceSettingsComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	protected String getLabelKey() {
		return "space-settings";
	}

	@Override
	protected String getModuleName() {
		return "SpaceSettings";
	}

	@Override
	protected Map<String, Object> getProps(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (Locale availableLocale :
				LanguageUtil.getAvailableLocales(
					themeDisplay.getScopeGroupId())) {

			jsonArray.put(
				JSONUtil.put(
					"label",
					availableLocale.getDisplayName(themeDisplay.getLocale())
				).put(
					"value", LanguageUtil.getLanguageId(availableLocale)
				));
		}

		long groupId = InfoItemUtil.getGroupId(httpServletRequest);

		Group group = _groupLocalService.getGroup(groupId);

		return HashMapBuilder.<String, Object>put(
			"backURL", ParamUtil.getString(httpServletRequest, "redirect")
		).put(
			"companyAvailableLanguages", jsonArray
		).put(
			"externalReferenceCode", group.getExternalReferenceCode()
		).put(
			"groupId", groupId
		).build();
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

}