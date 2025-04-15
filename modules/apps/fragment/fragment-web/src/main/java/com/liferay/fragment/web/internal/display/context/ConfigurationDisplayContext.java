/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.display.context;

import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.helper.DefaultInputFragmentEntryConfigurationProvider;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryLocalServiceUtil;
import com.liferay.fragment.web.internal.info.field.type.FormButtonInfoFieldType;
import com.liferay.info.field.type.BooleanInfoFieldType;
import com.liferay.info.field.type.DateInfoFieldType;
import com.liferay.info.field.type.DateTimeInfoFieldType;
import com.liferay.info.field.type.FileInfoFieldType;
import com.liferay.info.field.type.FriendlyURLInfoFieldType;
import com.liferay.info.field.type.HTMLInfoFieldType;
import com.liferay.info.field.type.InfoFieldType;
import com.liferay.info.field.type.LongTextInfoFieldType;
import com.liferay.info.field.type.MultiselectInfoFieldType;
import com.liferay.info.field.type.NumberInfoFieldType;
import com.liferay.info.field.type.RelationshipInfoFieldType;
import com.liferay.info.field.type.SelectInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Víctor Galán
 */
public class ConfigurationDisplayContext {

	public ConfigurationDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;

		_defaultInputFragmentEntryConfigurationProvider =
			(DefaultInputFragmentEntryConfigurationProvider)
				httpServletRequest.getAttribute(
					DefaultInputFragmentEntryConfigurationProvider.class.
						getName());

		_fragmentCollectionContributorRegistry =
			(FragmentCollectionContributorRegistry)
				httpServletRequest.getAttribute(
					FragmentCollectionContributorRegistry.class.getName());

		_liferayPortletResponse = liferayPortletResponse;
	}

	public Map<String, Object> getData() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return HashMapBuilder.<String, Object>put(
			"formTypes",
			() -> {
				List<Map<String, String>> formTypes = new ArrayList<>();

				JSONObject defaultInputFragmentEntryKeysJSONObject =
					_defaultInputFragmentEntryConfigurationProvider.
						getDefaultInputFragmentEntryKeysJSONObject(
							themeDisplay.getScopeGroupId());

				Map<String, FragmentEntry> fragmentEntries =
					_fragmentCollectionContributorRegistry.getFragmentEntries(
						themeDisplay.getLocale());

				for (InfoFieldType infoFieldType : _INFO_FIELD_TYPES) {
					formTypes.add(
						HashMapBuilder.put(
							"fragmentName",
							() -> _getFragmentName(
								themeDisplay.getCompanyId(), fragmentEntries,
								defaultInputFragmentEntryKeysJSONObject.
									getJSONObject(
										_getInfoFieldTypeName(infoFieldType)))
						).put(
							"inputType", infoFieldType.getName()
						).put(
							"label",
							infoFieldType.getLabel(themeDisplay.getLocale())
						).put(
							"name", _getInfoFieldTypeName(infoFieldType)
						).build());
				}

				return formTypes;
			}
		).put(
			"selectFragmentEntryURL",
			PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setMVCRenderCommandName(
				"/fragment/select_default_input_fragment_entry"
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString()
		).put(
			"updateInputFragmentEntriesURL",
			PortletURLBuilder.createActionURL(
				_liferayPortletResponse
			).setActionName(
				"/fragment/update_default_input_fragment_entries"
			).setRedirect(
				themeDisplay.getURLCurrent()
			).buildString()
		).build();
	}

	private String _getFragmentName(
		long companyId, Map<String, FragmentEntry> fragmentEntries,
		JSONObject jsonObject) {

		FragmentEntry fragmentEntry = fragmentEntries.get(
			jsonObject.getString("key"));

		if (fragmentEntry != null) {
			return fragmentEntry.getName();
		}

		Group group = GroupLocalServiceUtil.fetchGroup(
			companyId, jsonObject.getString("groupKey"));

		if (group == null) {
			return null;
		}

		fragmentEntry = FragmentEntryLocalServiceUtil.fetchFragmentEntry(
			group.getGroupId(), jsonObject.getString("key"));

		if (fragmentEntry != null) {
			return fragmentEntry.getName();
		}

		return null;
	}

	private String _getInfoFieldTypeName(InfoFieldType infoFieldType) {
		if (infoFieldType == FormButtonInfoFieldType.INSTANCE) {
			return DefaultInputFragmentEntryConfigurationProvider.
				FORM_INPUT_SUBMIT_BUTTON;
		}

		return infoFieldType.getName();
	}

	private static final InfoFieldType[] _INFO_FIELD_TYPES = {
		BooleanInfoFieldType.INSTANCE, DateInfoFieldType.INSTANCE,
		DateTimeInfoFieldType.INSTANCE, FileInfoFieldType.INSTANCE,
		FormButtonInfoFieldType.INSTANCE, FriendlyURLInfoFieldType.INSTANCE,
		HTMLInfoFieldType.INSTANCE, LongTextInfoFieldType.INSTANCE,
		MultiselectInfoFieldType.INSTANCE, NumberInfoFieldType.INSTANCE,
		RelationshipInfoFieldType.INSTANCE, SelectInfoFieldType.INSTANCE,
		TextInfoFieldType.INSTANCE
	};

	private final DefaultInputFragmentEntryConfigurationProvider
		_defaultInputFragmentEntryConfigurationProvider;
	private final FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;

}