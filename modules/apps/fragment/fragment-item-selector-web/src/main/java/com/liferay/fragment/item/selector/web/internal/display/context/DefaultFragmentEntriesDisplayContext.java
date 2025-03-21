/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.item.selector.web.internal.display.context;

import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.item.selector.FragmentEntryItemSelectorCriterion;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryListBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Víctor Galán
 */
public class DefaultFragmentEntriesDisplayContext {

	public DefaultFragmentEntriesDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;

		_fragmentCollectionContributorRegistry =
			(FragmentCollectionContributorRegistry)
				httpServletRequest.getAttribute(
					FragmentCollectionContributorRegistry.class.getName());

		_fragmentEntryItemSelectorCriterion =
			(FragmentEntryItemSelectorCriterion)httpServletRequest.getAttribute(
				FragmentEntryItemSelectorCriterion.class.getName());

		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<BreadcrumbEntry> getBreadcrumbEntries() {
		return BreadcrumbEntryListBuilder.add(
			breadcrumbEntry -> {
				if (Validator.isNotNull(getFragmentCollectionKey())) {
					breadcrumbEntry.setBrowsable(false);
				}

				breadcrumbEntry.setTitle(
					LanguageUtil.get(_httpServletRequest, "default"));
				breadcrumbEntry.setURL(
					PortletURLBuilder.create(
						PortletURLUtil.getCurrent(
							_liferayPortletRequest, _liferayPortletResponse)
					).setParameter(
						"fragmentCollectionKey", StringPool.BLANK
					).buildString());
			}
		).add(
			() -> Validator.isNotNull(getFragmentCollectionKey()),
			breadcrumbEntry -> {
				FragmentCollectionContributor fragmentCollectionContributor =
					_fragmentCollectionContributorRegistry.
						getFragmentCollectionContributor(
							getFragmentCollectionKey());

				breadcrumbEntry.setTitle(
					fragmentCollectionContributor.getName(
						_themeDisplay.getLocale()));
			}
		).build();
	}

	public SearchContainer<FragmentCollectionContributor>
		getFragmentCollectionContributorsSearchContainer() {

		if (_fragmentCollectionSearchContainer != null) {
			return _fragmentCollectionSearchContainer;
		}

		SearchContainer<FragmentCollectionContributor> searchContainer =
			new SearchContainer<>(
				_liferayPortletRequest,
				PortletURLUtil.getCurrent(
					_liferayPortletRequest, _liferayPortletResponse),
				null, "no-fragment-collection-was-found");

		searchContainer.setId("fragmentCollections");

		List<FragmentCollectionContributor> fragmentCollectionContributors =
			ListUtil.filter(
				_fragmentCollectionContributorRegistry.
					getFragmentCollectionContributors(),
				fragmentCollectionContributor -> {
					if (ListUtil.exists(
							fragmentCollectionContributor.getFragmentEntries(
								_fragmentEntryItemSelectorCriterion.getType(),
								_themeDisplay.getLocale()),
							fragmentEntry ->
								SetUtil.isEmpty(
									_fragmentEntryItemSelectorCriterion.
										getInputTypes()) ||
								_filterInputTypes(
									fragmentEntry,
									_fragmentEntryItemSelectorCriterion.
										getInputTypes()))) {

						return true;
					}

					return false;
				});

		searchContainer.setResultsAndTotal(fragmentCollectionContributors);

		_fragmentCollectionSearchContainer = searchContainer;

		return _fragmentCollectionSearchContainer;
	}

	public String getFragmentCollectionKey() {
		if (_fragmentCollectionKey != null) {
			return _fragmentCollectionKey;
		}

		_fragmentCollectionKey = ParamUtil.getString(
			_httpServletRequest, "fragmentCollectionKey");

		return _fragmentCollectionKey;
	}

	public SearchContainer<FragmentEntry> getFragmentsSearchContainer() {
		if (_fragmentsSearchContainer != null) {
			return _fragmentsSearchContainer;
		}

		SearchContainer<FragmentEntry> searchContainer = new SearchContainer<>(
			_liferayPortletRequest,
			PortletURLUtil.getCurrent(
				_liferayPortletRequest, _liferayPortletResponse),
			null, "no-fragment-was-found");

		searchContainer.setId("fragments");

		FragmentCollectionContributor fragmentCollectionContributor =
			_fragmentCollectionContributorRegistry.
				getFragmentCollectionContributor(getFragmentCollectionKey());

		List<FragmentEntry> fragmentEntries = ListUtil.filter(
			fragmentCollectionContributor.getFragmentEntries(
				_fragmentEntryItemSelectorCriterion.getType(),
				_themeDisplay.getLocale()),
			fragmentEntry ->
				SetUtil.isEmpty(
					_fragmentEntryItemSelectorCriterion.getInputTypes()) ||
				_filterInputTypes(
					fragmentEntry,
					_fragmentEntryItemSelectorCriterion.getInputTypes()));

		if (isSearch()) {
			fragmentEntries = ListUtil.filter(
				fragmentEntries,
				contributedEntry -> {
					String lowerCaseName = StringUtil.toLowerCase(
						contributedEntry.getName());

					return lowerCaseName.contains(
						StringUtil.toLowerCase(_getKeywords()));
				});
		}

		searchContainer.setResultsAndTotal(fragmentEntries);

		_fragmentsSearchContainer = searchContainer;

		return _fragmentsSearchContainer;
	}

	public boolean isSearch() {
		return Validator.isNotNull(_getKeywords());
	}

	private boolean _filterInputTypes(
		FragmentEntry fragmentEntry, Set<String> inputTypes) {

		try {
			JSONObject typeOptionsJSONObject = JSONFactoryUtil.createJSONObject(
				fragmentEntry.getTypeOptions());

			Set<String> fieldTypes = JSONUtil.toStringSet(
				typeOptionsJSONObject.getJSONArray("fieldTypes"));

			if (SetUtil.isEmpty(
					SetUtil.intersect(fieldTypes, new HashSet<>(inputTypes)))) {

				return false;
			}

			return true;
		}
		catch (JSONException jsonException) {
			_log.error(jsonException);

			return false;
		}
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultFragmentEntriesDisplayContext.class.getName());

	private final FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;
	private String _fragmentCollectionKey;
	private SearchContainer<FragmentCollectionContributor>
		_fragmentCollectionSearchContainer;
	private final FragmentEntryItemSelectorCriterion
		_fragmentEntryItemSelectorCriterion;
	private SearchContainer<FragmentEntry> _fragmentsSearchContainer;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;

}