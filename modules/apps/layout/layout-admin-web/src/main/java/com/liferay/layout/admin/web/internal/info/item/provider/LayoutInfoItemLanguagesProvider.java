/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.info.item.provider;

import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.admin.web.internal.info.item.helper.LayoutInfoItemLanguagesProviderHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.translation.info.item.provider.InfoItemLanguagesProvider;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = InfoItemLanguagesProvider.class)
public class LayoutInfoItemLanguagesProvider
	implements InfoItemLanguagesProvider<Layout> {

	@Override
	public String[] getAvailableLanguageIds(Layout layout)
		throws PortalException {

		Set<String> availableLanguageIds = new TreeSet<>(
			Comparator.naturalOrder());

		if (layout.isTypeContent()) {
			Collections.addAll(
				availableLanguageIds, layout.getAvailableLanguageIds());
		}

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		Collections.addAll(
			availableLanguageIds,
			_layoutInfoItemLanguagesProviderHelper.getAvailableLanguageIds(
				layout, defaultSegmentsExperienceId));

		Set<String> siteAvailableLanguageIds = new HashSet<>();

		for (Locale locale :
				LanguageUtil.getAvailableLocales(layout.getGroupId())) {

			siteAvailableLanguageIds.add(LocaleUtil.toLanguageId(locale));
		}

		availableLanguageIds.retainAll(siteAvailableLanguageIds);

		return availableLanguageIds.toArray(new String[0]);
	}

	@Override
	public String getDefaultLanguageId(Layout layout) {
		return _layoutInfoItemLanguagesProviderHelper.getDefaultLanguageId(
			layout);
	}

	@Activate
	@Modified
	protected void activate() {
		_layoutInfoItemLanguagesProviderHelper =
			new LayoutInfoItemLanguagesProviderHelper(
				_fragmentEntryLinkLocalService, _language);
	}

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private Language _language;

	private volatile LayoutInfoItemLanguagesProviderHelper
		_layoutInfoItemLanguagesProviderHelper;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}