/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.info.item.helper;

import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author Adolfo Pérez
 */
public class LayoutInfoItemLanguagesProviderHelper {

	public LayoutInfoItemLanguagesProviderHelper(
		FragmentEntryLinkLocalService fragmentEntryLinkLocalService,
		Language language) {

		_fragmentEntryLinkLocalService = fragmentEntryLinkLocalService;
		_language = language;
	}

	public String[] getAvailableLanguageIds(
			Layout layout, long segmentsExperienceId)
		throws PortalException {

		if (!layout.isTypeAssetDisplay() && !layout.isTypeContent()) {
			return layout.getAvailableLanguageIds();
		}

		Set<String> availableLocalesIds = new HashSet<>();

		Set<Locale> siteAvailableLocales = _language.getAvailableLocales(
			layout.getGroupId());

		List<FragmentEntryLink> fragmentEntryLinks = _getFragmentEntryLinks(
			layout, segmentsExperienceId);

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			JSONObject editableValuesJSONObject =
				fragmentEntryLink.getEditableValuesJSONObject();

			for (String translatableFragment : _TRANSLATABLE_FRAGMENTS) {
				availableLocalesIds.addAll(
					_getTranslatableFragmentLanguagesIds(
						editableValuesJSONObject, translatableFragment,
						siteAvailableLocales));
			}
		}

		availableLocalesIds.add(getDefaultLanguageId(layout));

		return availableLocalesIds.toArray(new String[0]);
	}

	public String getDefaultLanguageId(Layout layout) {
		return layout.getDefaultLanguageId();
	}

	private List<FragmentEntryLink> _getFragmentEntryLinks(
		Layout layout, long segmentsExperienceId) {

		long defaultSegmentsExperienceId =
			SegmentsExperienceLocalServiceUtil.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		if (segmentsExperienceId == defaultSegmentsExperienceId) {
			return _fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				layout.getGroupId(), layout.getPlid());
		}

		return _fragmentEntryLinkLocalService.
			getFragmentEntryLinksBySegmentsExperienceId(
				layout.getGroupId(), segmentsExperienceId, layout.getPlid());
	}

	private Set<String> _getTranslatableFragmentLanguagesIds(
		JSONObject jsonObject, String key, Set<Locale> siteAvailableLocales) {

		Set<String> availableLocalesIds = new HashSet<>();

		JSONObject editableFragmentJSONObject = jsonObject.getJSONObject(key);

		if (!(editableFragmentJSONObject instanceof JSONObject) ||
			(editableFragmentJSONObject.length() <= 0)) {

			return availableLocalesIds;
		}

		Iterator<String> editableFragmentIterator =
			editableFragmentJSONObject.keys();

		while (editableFragmentIterator.hasNext()) {
			Object editableValueObject = editableFragmentJSONObject.get(
				editableFragmentIterator.next());

			if (!(editableValueObject instanceof JSONObject)) {
				continue;
			}

			JSONObject editableValueJSONObject =
				(JSONObject)editableValueObject;

			if (editableValueJSONObject.length() <= 0) {
				continue;
			}

			for (Locale siteAvailableLocale : siteAvailableLocales) {
				Object valueObject = editableValueJSONObject.get(
					_language.getLanguageId(siteAvailableLocale));

				if (valueObject != null) {
					availableLocalesIds.add(
						LocaleUtil.toLanguageId(siteAvailableLocale));
				}
			}
		}

		return availableLocalesIds;
	}

	private static final String[] _TRANSLATABLE_FRAGMENTS = {
		FragmentEntryProcessorConstants.
			KEY_BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR,
		FragmentEntryProcessorConstants.KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR
	};

	private final FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;
	private final Language _language;

}