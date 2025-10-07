/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.filter.date.display.context;

import com.liferay.fragment.constants.FragmentConfigurationFieldDataType;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;

/**
 * @author Víctor Galán
 */
public class FragmentCollectionFilterDateDisplayContext {

	public FragmentCollectionFilterDateDisplayContext(
		JSONObject configurationJSONObject,
		FragmentEntryConfigurationParser fragmentEntryConfigurationParser,
		FragmentRendererContext fragmentRendererContext) {

		_configurationJSONObject = configurationJSONObject;
		_fragmentEntryConfigurationParser = fragmentEntryConfigurationParser;
		_fragmentRendererContext = fragmentRendererContext;

		_fragmentEntryLink = fragmentRendererContext.getFragmentEntryLink();
	}

	public String getLabel() {
		return GetterUtil.getString(_getFieldValue("label"));
	}

	public Map<String, Object> getProps() {
		return HashMapBuilder.<String, Object>put(
			"date", _getDate()
		).put(
			"fragmentEntryLinkId",
			String.valueOf(_fragmentEntryLink.getFragmentEntryLinkId())
		).put(
			"isDisabled", isDisabled()
		).put(
			"targetCollections",
			_fragmentEntryConfigurationParser.getConfigurationFieldValue(
				_fragmentEntryLink.getEditableValuesJSONObject(),
				"targetCollections", FragmentConfigurationFieldDataType.ARRAY)
		).build();
	}

	public boolean isDisabled() {
		return !_fragmentRendererContext.isViewMode();
	}

	public boolean isShowLabel() {
		return GetterUtil.getBoolean(_getFieldValue("showLabel"));
	}

	private String _getDate() {
		return GetterUtil.getString(_getFieldValue("date"));
	}

	private Object _getFieldValue(String fieldName) {
		return _fragmentEntryConfigurationParser.getFieldValue(
			_configurationJSONObject,
			_fragmentEntryLink.getEditableValuesJSONObject(),
			_fragmentRendererContext.getLocale(), fieldName);
	}

	private final JSONObject _configurationJSONObject;
	private final FragmentEntryConfigurationParser
		_fragmentEntryConfigurationParser;
	private final FragmentEntryLink _fragmentEntryLink;
	private final FragmentRendererContext _fragmentRendererContext;

}