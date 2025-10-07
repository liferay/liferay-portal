/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.site.provider;

import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Brian I. Kim
 */
public class SitemapURLProviderUtil {

	protected static Map<Locale, String> getAlternateFriendlyURLs(
		Map<Locale, String> alternateSiteURLs, long friendlyURLEntryId,
		FriendlyURLEntryLocalService friendlyURLEntryLocalService) {

		Map<Locale, String> alternateFriendlyURLs = new HashMap<>();

		for (FriendlyURLEntryLocalization friendlyURLEntryLocalization :
				friendlyURLEntryLocalService.getFriendlyURLEntryLocalizations(
					friendlyURLEntryId)) {

			String alternateSiteURL = alternateSiteURLs.get(
				LocaleUtil.fromLanguageId(
					friendlyURLEntryLocalization.getLanguageId()));

			if (alternateSiteURL != null) {
				alternateFriendlyURLs.put(
					LocaleUtil.fromLanguageId(
						friendlyURLEntryLocalization.getLanguageId()),
					alternateSiteURL);
			}
		}

		return alternateFriendlyURLs;
	}

	protected static boolean hasPortletId(Layout layout, String portletId) {
		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		for (Portlet portlet : layoutTypePortlet.getAllNonembeddedPortlets()) {
			if (portletId.equals(portlet.getPortletId()) ||
				portletId.equals(portlet.getRootPortletId())) {

				return true;
			}
		}

		return false;
	}

}