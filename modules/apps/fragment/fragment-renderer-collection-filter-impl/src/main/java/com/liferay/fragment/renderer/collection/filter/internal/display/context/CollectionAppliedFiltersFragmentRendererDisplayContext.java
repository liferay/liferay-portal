/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.renderer.collection.filter.internal.display.context;

import com.liferay.fragment.collection.filter.FragmentCollectionFilter;
import com.liferay.fragment.collection.filter.FragmentCollectionFilterRegistry;
import com.liferay.fragment.collection.filter.constants.FragmentCollectionFilterConstants;
import com.liferay.fragment.constants.FragmentConfigurationFieldDataType;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Pablo.Molina
 */
public class CollectionAppliedFiltersFragmentRendererDisplayContext {

	public CollectionAppliedFiltersFragmentRendererDisplayContext(
		FragmentCollectionFilterRegistry fragmentCollectionFilterRegistry,
		FragmentEntryConfigurationParser fragmentEntryConfigurationParser,
		FragmentEntryLinkLocalService fragmentEntryLinkLocalService,
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		_fragmentCollectionFilterRegistry = fragmentCollectionFilterRegistry;
		_fragmentEntryConfigurationParser = fragmentEntryConfigurationParser;
		_fragmentEntryLinkLocalService = fragmentEntryLinkLocalService;
		_httpServletRequest = httpServletRequest;

		_editMode = fragmentRendererContext.isEditMode();
		_fragmentEntryLink = fragmentRendererContext.getFragmentEntryLink();
		_locale = fragmentRendererContext.getLocale();
	}

	public List<Map<String, String>> getAppliedFilters() {
		List<Map<String, String>> appliedFilters = new ArrayList<>();

		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(_httpServletRequest);

		Map<String, String[]> parameters =
			originalHttpServletRequest.getParameterMap();

		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			String parameterName = entry.getKey();

			if (!parameterName.startsWith(
					FragmentCollectionFilterConstants.FILTER_PREFIX) ||
				ArrayUtil.isEmpty(entry.getValue())) {

				continue;
			}

			List<String> parameterData = StringUtil.split(
				parameterName, CharPool.UNDERLINE);

			if (parameterData.size() != 3) {
				continue;
			}

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
					GetterUtil.getLong(parameterData.get(2)));

			if (fragmentEntryLink == null) {
				continue;
			}

			JSONArray targetCollectionsJSONArray =
				(JSONArray)
					_fragmentEntryConfigurationParser.
						getConfigurationFieldValue(
							fragmentEntryLink.getEditableValuesJSONObject(),
							"targetCollections",
							FragmentConfigurationFieldDataType.ARRAY);

			if ((targetCollectionsJSONArray == null) ||
				Collections.disjoint(
					JSONUtil.toStringSet(targetCollectionsJSONArray),
					_getTargetCollections())) {

				continue;
			}

			FragmentCollectionFilter fragmentCollectionFilter =
				_fragmentCollectionFilterRegistry.getFragmentCollectionFilter(
					parameterData.get(1));

			if (fragmentCollectionFilter == null) {
				continue;
			}

			for (String filterValue : entry.getValue()) {
				appliedFilters.add(
					HashMapBuilder.put(
						"filterFragmentEntryLinkId",
						HtmlUtil.escapeAttribute(parameterData.get(2))
					).put(
						"filterLabel",
						HtmlUtil.escape(
							fragmentCollectionFilter.getFilterValueLabel(
								filterValue, _locale))
					).put(
						"filterType", parameterData.get(1)
					).put(
						"filterValue", HtmlUtil.escapeAttribute(filterValue)
					).build());
			}
		}

		return appliedFilters;
	}

	public Map<String, Object> getCollectionAppliedFiltersProps() {
		if (_collectionAppliedFiltersProps != null) {
			return _collectionAppliedFiltersProps;
		}

		_collectionAppliedFiltersProps = HashMapBuilder.<String, Object>put(
			"filterPrefix", FragmentCollectionFilterConstants.FILTER_PREFIX
		).put(
			"fragmentEntryLinkNamespace", getFragmentEntryLinkNamespace()
		).build();

		return _collectionAppliedFiltersProps;
	}

	public String getFragmentEntryLinkNamespace() {
		return StringBundler.concat(
			"fragment_", _fragmentEntryLink.getFragmentEntryLinkId(),
			StringPool.UNDERLINE, _fragmentEntryLink.getNamespace());
	}

	public boolean isEditMode() {
		return _editMode;
	}

	public boolean showClearFiltersButton() {
		return GetterUtil.getBoolean(
			_fragmentEntryConfigurationParser.getConfigurationFieldValue(
				_fragmentEntryLink.getEditableValuesJSONObject(),
				"showClearFilters",
				FragmentConfigurationFieldDataType.BOOLEAN));
	}

	private Set<String> _getTargetCollections() {
		if (_targetCollections != null) {
			return _targetCollections;
		}

		JSONArray targetCollectionsJSONArray =
			(JSONArray)
				_fragmentEntryConfigurationParser.getConfigurationFieldValue(
					_fragmentEntryLink.getEditableValuesJSONObject(),
					"targetCollections",
					FragmentConfigurationFieldDataType.ARRAY);

		_targetCollections = JSONUtil.toStringSet(targetCollectionsJSONArray);

		return _targetCollections;
	}

	private Map<String, Object> _collectionAppliedFiltersProps;
	private final boolean _editMode;
	private final FragmentCollectionFilterRegistry
		_fragmentCollectionFilterRegistry;
	private final FragmentEntryConfigurationParser
		_fragmentEntryConfigurationParser;
	private final FragmentEntryLink _fragmentEntryLink;
	private final FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final Locale _locale;
	private Set<String> _targetCollections;

}