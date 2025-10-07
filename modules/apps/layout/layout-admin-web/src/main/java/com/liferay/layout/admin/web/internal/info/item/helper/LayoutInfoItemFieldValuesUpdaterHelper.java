/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.info.item.helper;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.layout.admin.web.internal.info.item.LayoutInfoItemFields;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Adolfo Pérez
 */
public class LayoutInfoItemFieldValuesUpdaterHelper {

	public LayoutInfoItemFieldValuesUpdaterHelper(
		FragmentEntryLinkLocalService fragmentEntryLinkLocalService,
		LayoutLocalService layoutLocalService) {

		_fragmentEntryLinkLocalService = fragmentEntryLinkLocalService;
		_layoutLocalService = layoutLocalService;
	}

	public Layout updateFromInfoItemFieldValues(
		Layout layout, InfoItemFieldValues infoItemFieldValues,
		long segmentsExperienceId) {

		_updateFragmentEntryLinks(infoItemFieldValues);

		if (layout.isDraftLayout()) {
			_updateLayout(
				_layoutLocalService.fetchLayout(layout.getClassPK()),
				infoItemFieldValues, segmentsExperienceId);
		}

		return _updateLayout(layout, infoItemFieldValues, segmentsExperienceId);
	}

	private Map<Locale, String> _getFieldMap(
		String fieldName, InfoItemFieldValues infoItemFieldValues,
		Map<Locale, String> initialValue) {

		Map<Locale, String> fieldMap = new HashMap<>(initialValue);

		InfoFieldValue<Object> infoFieldValue =
			infoItemFieldValues.getInfoFieldValue(fieldName);

		InfoLocalizedValue<Object> infoLocalizedValue =
			(InfoLocalizedValue)infoFieldValue.getValue();

		for (Locale locale : infoLocalizedValue.getAvailableLocales()) {
			fieldMap.put(locale, (String)infoLocalizedValue.getValue(locale));
		}

		return fieldMap;
	}

	private void _updateEditableValuesJSONObject(
		JSONObject jsonObject, String fieldName,
		InfoFieldValue<Object> infoFieldValue) {

		for (String processorKey : jsonObject.keySet()) {
			JSONObject processorJSONObject = jsonObject.getJSONObject(
				processorKey);

			if (!processorJSONObject.has(fieldName)) {
				continue;
			}

			JSONObject valuesJSONObject = processorJSONObject.getJSONObject(
				fieldName);

			InfoLocalizedValue<String> infoLocalizedValue =
				(InfoLocalizedValue<String>)infoFieldValue.getValue();

			for (Locale locale : infoLocalizedValue.getAvailableLocales()) {
				valuesJSONObject.put(
					LanguageUtil.getLanguageId(locale),
					infoLocalizedValue.getValue(locale));
			}
		}
	}

	private void _updateFragmentEntryLinks(
		InfoItemFieldValues infoItemFieldValues) {

		Map<FragmentEntryLink, JSONObject> editableValuesJSONObjects =
			new HashMap<>();

		for (InfoFieldValue<Object> infoFieldValue :
				infoItemFieldValues.getInfoFieldValues()) {

			InfoField infoField = infoFieldValue.getInfoField();

			Matcher matcher = _fragmentEntryLinkInfoFieldPattern.matcher(
				infoField.getName());

			if (!matcher.matches()) {
				continue;
			}

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
					GetterUtil.getLong(matcher.group("id")));

			if (fragmentEntryLink == null) {
				continue;
			}

			_updateEditableValuesJSONObject(
				editableValuesJSONObjects.computeIfAbsent(
					fragmentEntryLink,
					FragmentEntryLink::getEditableValuesJSONObject),
				matcher.group("name"), infoFieldValue);
		}

		for (Map.Entry<FragmentEntryLink, JSONObject> entry :
				editableValuesJSONObjects.entrySet()) {

			JSONObject jsonObject = entry.getValue();

			if (jsonObject != null) {
				FragmentEntryLink fragmentEntryLink = entry.getKey();

				fragmentEntryLink.setEditableValues(jsonObject.toString());

				_fragmentEntryLinkLocalService.updateFragmentEntryLink(
					fragmentEntryLink);
			}
		}
	}

	private Layout _updateLayout(
		Layout layout, InfoItemFieldValues infoItemFieldValues,
		long segmentsExperienceId) {

		if (layout == null) {
			return null;
		}

		long defaultSegmentsExperienceId =
			SegmentsExperienceLocalServiceUtil.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		if (segmentsExperienceId == defaultSegmentsExperienceId) {
			layout.setNameMap(
				_getFieldMap(
					LayoutInfoItemFields.nameInfoField.getName(),
					infoItemFieldValues, layout.getNameMap()));
		}

		if (layout.isDraftLayout()) {
			layout.setStatus(WorkflowConstants.STATUS_DRAFT);
		}

		return _layoutLocalService.updateLayout(layout);
	}

	private static final Pattern _fragmentEntryLinkInfoFieldPattern =
		Pattern.compile("(?<id>\\d+):(?<name>.+)");

	private final FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;
	private final LayoutLocalService _layoutLocalService;

}