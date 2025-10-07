/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.filter.tags.display.context;

import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.fragment.constants.FragmentConfigurationFieldDataType;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Pablo Molina
 */
public class FragmentCollectionFilterTagsDisplayContext {

	public FragmentCollectionFilterTagsDisplayContext(
		JSONObject configurationJSONObject,
		FragmentEntryConfigurationParser fragmentEntryConfigurationParser,
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		_configurationJSONObject = configurationJSONObject;
		_fragmentEntryConfigurationParser = fragmentEntryConfigurationParser;
		_fragmentRendererContext = fragmentRendererContext;

		_fragmentEntryLink = fragmentRendererContext.getFragmentEntryLink();
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getHelpText() {
		String helpText = GetterUtil.getString(_getFieldValue("helpText"));

		if (Validator.isNotNull(helpText)) {
			return helpText;
		}

		JSONObject defaultValuesJSONObject =
			_fragmentEntryConfigurationParser.
				getConfigurationDefaultValuesJSONObject(
					_configurationJSONObject);

		return defaultValuesJSONObject.getString("helpText", StringPool.BLANK);
	}

	public String getLabel() {
		String label = GetterUtil.getString(_getFieldValue("label"));

		if (Validator.isNotNull(label)) {
			return label;
		}

		return StringPool.BLANK;
	}

	public Map<String, Object> getProps() throws PortalException {
		if (_props != null) {
			return _props;
		}

		_props = HashMapBuilder.<String, Object>put(
			"disabled", isDisabled()
		).put(
			"fragmentEntryLinkId",
			String.valueOf(_fragmentEntryLink.getFragmentEntryLinkId())
		).put(
			"groupIds",
			ArrayUtil.toStringArray(
				SiteConnectedGroupGroupProviderUtil.
					getCurrentAndAncestorSiteAndDepotGroupIds(
						_themeDisplay.getScopeGroupId()))
		).put(
			"helpText",
			() -> {
				if (isShowHelpText()) {
					return getHelpText();
				}

				return StringPool.BLANK;
			}
		).put(
			"label", getLabel()
		).put(
			"showLabel", isShowLabel()
		).put(
			"targetCollections",
			_fragmentEntryConfigurationParser.getConfigurationFieldValue(
				_fragmentEntryLink.getEditableValuesJSONObject(),
				"targetCollections", FragmentConfigurationFieldDataType.ARRAY)
		).build();

		return _props;
	}

	public boolean isDisabled() {
		return _fragmentRendererContext.isEditMode();
	}

	public boolean isShowHelpText() {
		return GetterUtil.getBoolean(_getFieldValue("showHelpText"));
	}

	public boolean isShowLabel() {
		return GetterUtil.getBoolean(_getFieldValue("showLabel"));
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
	private Map<String, Object> _props;
	private final ThemeDisplay _themeDisplay;

}