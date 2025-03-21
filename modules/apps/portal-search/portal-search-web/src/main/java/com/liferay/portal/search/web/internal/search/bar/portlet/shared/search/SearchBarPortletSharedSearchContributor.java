/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.bar.portlet.shared.search;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.constants.SearchContextAttributes;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.web.constants.SearchBarPortletKeys;
import com.liferay.portal.search.web.internal.display.context.Keywords;
import com.liferay.portal.search.web.internal.display.context.SearchScope;
import com.liferay.portal.search.web.internal.display.context.SearchScopePreference;
import com.liferay.portal.search.web.internal.search.bar.portlet.SearchBarPortletDestinationUtil;
import com.liferay.portal.search.web.internal.search.bar.portlet.SearchBarPortletPreferences;
import com.liferay.portal.search.web.internal.search.bar.portlet.SearchBarPortletPreferencesImpl;
import com.liferay.portal.search.web.internal.search.bar.portlet.helper.SearchBarPrecedenceHelper;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(
	property = "jakarta.portlet.name=" + SearchBarPortletKeys.SEARCH_BAR,
	service = PortletSharedSearchContributor.class
)
public class SearchBarPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SearchBarPortletPreferences searchBarPortletPreferences =
			new SearchBarPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		portletSharedSearchSettings.setIncludeAttachments(
			searchBarPortletPreferences.isIncludeAttachments());

		SearchRequestBuilder searchRequestBuilder =
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(
				searchBarPortletPreferences.getFederatedSearchKey());

		if (!_shouldContributeToCurrentPageSearch(
				searchBarPortletPreferences, portletSharedSearchSettings)) {

			return;
		}

		searchRequestBuilder.withSearchContext(
			searchContext -> {
				searchContext.setAttribute(
					SearchContextAttributes.
						ATTRIBUTE_KEY_CONTRIBUTE_TUNING_RANKINGS,
					Boolean.TRUE);
				searchContext.setIncludeAttachments(
					searchBarPortletPreferences.isIncludeAttachments());
				searchContext.setIncludeInternalAssetCategories(false);
			});

		_setKeywords(
			searchRequestBuilder, searchBarPortletPreferences,
			portletSharedSearchSettings);

		_setScope(searchBarPortletPreferences, portletSharedSearchSettings);

		_filterByThisSite(
			searchRequestBuilder, searchBarPortletPreferences,
			portletSharedSearchSettings);
	}

	@Reference
	protected GroupLocalService groupLocalService;

	@Reference
	protected SearchBarPrecedenceHelper searchBarPrecedenceHelper;

	private void _filterByThisSite(
		SearchRequestBuilder searchRequestBuilder,
		SearchBarPortletPreferences searchBarPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SearchScope searchScope = _getSearchScope(
			searchBarPortletPreferences, portletSharedSearchSettings);

		if (searchScope == SearchScope.THIS_SITE) {
			searchRequestBuilder.withSearchContext(
				searchContext -> searchContext.setGroupIds(
					_getGroupIds(portletSharedSearchSettings)));

			return;
		}

		ThemeDisplay themeDisplay =
			portletSharedSearchSettings.getThemeDisplay();

		Group group = groupLocalService.fetchGroup(
			themeDisplay.getScopeGroupId());

		if (!searchBarPortletPreferences.isShowStagedResults() ||
			!group.isStagingGroup()) {

			searchRequestBuilder.withSearchContext(
				searchContext -> searchContext.setIncludeStagingGroups(false));
		}
	}

	private SearchScope _getDefaultSearchScope() {
		SearchBarPortletPreferences searchBarPortletPreferences =
			new SearchBarPortletPreferencesImpl(null);

		SearchScopePreference searchScopePreference =
			searchBarPortletPreferences.getSearchScopePreference();

		return searchScopePreference.getSearchScope();
	}

	private long[] _getGroupIds(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		ThemeDisplay themeDisplay =
			portletSharedSearchSettings.getThemeDisplay();

		try {
			List<Long> groupIds = new ArrayList<>();

			groupIds.add(themeDisplay.getScopeGroupId());

			List<Group> groups = groupLocalService.getGroups(
				themeDisplay.getCompanyId(), Layout.class.getName(),
				themeDisplay.getScopeGroupId());

			for (Group group : groups) {
				groupIds.add(group.getGroupId());
			}

			return ArrayUtil.toLongArray(groupIds);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return new long[] {themeDisplay.getScopeGroupId()};
		}
	}

	private SearchScope _getSearchScope(
		SearchBarPortletPreferences searchBarPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SearchScopePreference searchScopePreference =
			searchBarPortletPreferences.getSearchScopePreference();

		if (searchScopePreference !=
				SearchScopePreference.LET_THE_USER_CHOOSE) {

			return searchScopePreference.getSearchScope();
		}

		String scopeParameterValue = portletSharedSearchSettings.getParameter(
			searchBarPortletPreferences.getScopeParameterName());

		if (scopeParameterValue == null) {
			return _getDefaultSearchScope();
		}

		return SearchScope.getSearchScope(scopeParameterValue);
	}

	private boolean _isLuceneSyntax(
		SearchBarPortletPreferences searchBarPortletPreferences,
		Keywords keywords) {

		if (searchBarPortletPreferences.isUseAdvancedSearchSyntax() ||
			keywords.isLuceneSyntax()) {

			return true;
		}

		return false;
	}

	private void _setKeywords(
		SearchRequestBuilder searchRequestBuilder,
		SearchBarPortletPreferences searchBarPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		String parameterName =
			searchBarPortletPreferences.getKeywordsParameterName();

		portletSharedSearchSettings.setKeywordsParameterName(parameterName);

		String parameterValue = portletSharedSearchSettings.getParameter(
			parameterName);

		if (parameterValue != null) {
			Keywords keywords = new Keywords(parameterValue);

			searchRequestBuilder.queryString(keywords.getKeywords());

			if (_isLuceneSyntax(searchBarPortletPreferences, keywords)) {
				_setLuceneSyntax(searchRequestBuilder);
			}
		}
	}

	private void _setLuceneSyntax(SearchRequestBuilder searchRequestBuilder) {
		searchRequestBuilder.withSearchContext(
			searchContext -> searchContext.setAttribute(
				SearchContextAttributes.ATTRIBUTE_KEY_LUCENE_SYNTAX,
				Boolean.TRUE));
	}

	private void _setScope(
		SearchBarPortletPreferences searchBarPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SearchScopePreference searchScopePreference =
			searchBarPortletPreferences.getSearchScopePreference();

		portletSharedSearchSettings.setScope(
			searchScopePreference.getPreferenceString());

		portletSharedSearchSettings.setScopeParameterName(
			searchBarPortletPreferences.getScopeParameterName());
	}

	private boolean _shouldContributeToCurrentPageSearch(
		SearchBarPortletPreferences searchBarPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		if (!SearchBarPortletDestinationUtil.isSameDestination(
				searchBarPortletPreferences,
				portletSharedSearchSettings.getThemeDisplay()) ||
			searchBarPrecedenceHelper.
				isSearchBarInBodyWithHeaderSearchBarAlreadyPresent(
					portletSharedSearchSettings.getThemeDisplay(),
					portletSharedSearchSettings.getPortletId())) {

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SearchBarPortletSharedSearchContributor.class);

}