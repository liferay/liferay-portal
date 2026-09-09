/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.product.menu.web.internal.omni.search;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.product.navigation.omni.search.OmniSearchResult;
import com.liferay.product.navigation.omni.search.OmniSearchResultProvider;
import com.liferay.product.navigation.omni.search.constants.OmniSearchConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Castro
 * @author Thiago Buarque
 */
@Component(
	property = "omni.search.result.provider.order:Integer=100",
	service = OmniSearchResultProvider.class
)
public class PanelAppOmniSearchResultProvider
	implements OmniSearchResultProvider {

	@Override
	public List<OmniSearchResult> getOmniSearchResults(
			String keywords, LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			ThemeDisplay themeDisplay)
		throws PortalException {

		List<OmniSearchResult> omniSearchResults = new ArrayList<>();

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(liferayPortletRequest));

		Locale locale = themeDisplay.getLocale();

		String lowerCaseKeywords = StringUtil.toLowerCase(keywords.trim());

		for (String rootPanelCategoryKey : _ROOT_PANEL_CATEGORY_KEYS) {
			_addOmniSearchResult(
				httpServletRequest, locale, lowerCaseKeywords,
				omniSearchResults, rootPanelCategoryKey, themeDisplay);
		}

		return omniSearchResults;
	}

	@Activate
	protected void activate() {
		_panelCategoryHelper = new PanelCategoryHelper(_panelAppRegistry);
	}

	private void _addOmniSearchResult(
			HttpServletRequest httpServletRequest, Locale locale,
			String lowerCaseKeywords, List<OmniSearchResult> omniSearchResults,
			String rootPanelCategoryKey, ThemeDisplay themeDisplay)
		throws PortalException {

		List<OmniSearchResult> entryOmniSearchResults = new ArrayList<>();

		for (PanelCategory childPanelCategory :
				_panelCategoryHelper.getChildPanelCategories(
					rootPanelCategoryKey, themeDisplay)) {

			_addOmniSearchResults(
				httpServletRequest, locale, lowerCaseKeywords,
				entryOmniSearchResults, childPanelCategory, null, themeDisplay);

			if (entryOmniSearchResults.size() >=
					OmniSearchConstants.MAX_ENTRIES_PER_SECTION) {

				break;
			}

			for (PanelCategory grandchildPanelCategory :
					_panelCategoryHelper.getChildPanelCategories(
						childPanelCategory.getKey(), themeDisplay)) {

				_addOmniSearchResults(
					httpServletRequest, locale, lowerCaseKeywords,
					entryOmniSearchResults, grandchildPanelCategory,
					childPanelCategory.getLabel(locale), themeDisplay);

				if (entryOmniSearchResults.size() >=
						OmniSearchConstants.MAX_ENTRIES_PER_SECTION) {

					break;
				}
			}
		}

		if (entryOmniSearchResults.isEmpty()) {
			return;
		}

		omniSearchResults.add(
			new OmniSearchResult(
				"grid", entryOmniSearchResults,
				_getRootPanelCategoryLabel(locale, rootPanelCategoryKey)));
	}

	private void _addOmniSearchResults(
			HttpServletRequest httpServletRequest, Locale locale,
			String lowerCaseKeywords, List<OmniSearchResult> omniSearchResults,
			PanelCategory panelCategory, String parentLabel,
			ThemeDisplay themeDisplay)
		throws PortalException {

		if (omniSearchResults.size() >=
				OmniSearchConstants.MAX_ENTRIES_PER_SECTION) {

			return;
		}

		String description = panelCategory.getLabel(locale);

		if (Validator.isNotNull(parentLabel)) {
			description = parentLabel + " \u203a " + description;
		}

		for (PanelApp panelApp :
				_panelAppRegistry.getPanelApps(
					panelCategory.getKey(), themeDisplay.getPermissionChecker(),
					themeDisplay.getScopeGroup())) {

			String title = panelApp.getLabel(locale);

			String lowerCaseTitle = StringUtil.toLowerCase(title);

			if (!lowerCaseTitle.contains(lowerCaseKeywords)) {
				continue;
			}

			omniSearchResults.add(
				new OmniSearchResult(
					description, "grid", title,
					String.valueOf(
						panelApp.getPortletURL(httpServletRequest))));

			if (omniSearchResults.size() >=
					OmniSearchConstants.MAX_ENTRIES_PER_SECTION) {

				return;
			}
		}
	}

	private String _getRootPanelCategoryLabel(
		Locale locale, String rootPanelCategoryKey) {

		if (rootPanelCategoryKey.equals(
				PanelCategoryKeys.SITE_ADMINISTRATION)) {

			return LanguageUtil.get(locale, "site-administration");
		}

		return LanguageUtil.get(locale, "applications-menu");
	}

	private static final String[] _ROOT_PANEL_CATEGORY_KEYS = {
		PanelCategoryKeys.APPLICATIONS_MENU,
		PanelCategoryKeys.SITE_ADMINISTRATION
	};

	@Reference
	private PanelAppRegistry _panelAppRegistry;

	private PanelCategoryHelper _panelCategoryHelper;

	@Reference
	private Portal _portal;

}