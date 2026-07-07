/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.learn.LearnMessageUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Roberto Díaz
 */
@Component(service = FragmentRenderer.class)
public class NewSpaceComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	protected String getComponentName() {
		return "NewSpace";
	}

	@Override
	protected String getLabelKey() {
		return "new-space";
	}

	@Override
	protected Map<String, Object> getProps(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return HashMapBuilder.<String, Object>put(
			"backURL", _getBackURL(httpServletRequest, themeDisplay)
		).put(
			"baseAddSpaceMembersURL",
			ActionUtil.getBaseAddSpaceMembersURL(themeDisplay)
		).put(
			"description", _getDescription(httpServletRequest, themeDisplay)
		).put(
			"learnResources",
			LearnMessageUtil.getReactDataJSONObject("site-cms-site-initializer")
		).build();
	}

	private String _getBackURL(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		String backURL = ParamUtil.getString(httpServletRequest, "backURL");

		if (Validator.isNotNull(backURL)) {
			return backURL;
		}

		return themeDisplay.getPathFriendlyURLPublic() +
			GroupConstants.CMS_FRIENDLY_URL;
	}

	private String _getDescription(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		if (_isFirstTimeAccess(themeDisplay)) {
			return LanguageUtil.get(
				httpServletRequest,
				"spaces-are-essential-for-organizing,-defining,-and-managing-" +
					"your-content-and-files.-before-you-get-started,-create-" +
						"your-first-space");
		}

		return LanguageUtil.get(
			httpServletRequest,
			"spaces-are-essential-for-organizing,-defining,-and-managing-" +
				"your-content-and-files");
	}

	private boolean _isFirstTimeAccess(ThemeDisplay themeDisplay) {
		Company company = themeDisplay.getCompany();

		ExpandoBridge bridge = company.getExpandoBridge();

		return !bridge.hasAttribute("cmsFirstTimeAccess");
	}

}