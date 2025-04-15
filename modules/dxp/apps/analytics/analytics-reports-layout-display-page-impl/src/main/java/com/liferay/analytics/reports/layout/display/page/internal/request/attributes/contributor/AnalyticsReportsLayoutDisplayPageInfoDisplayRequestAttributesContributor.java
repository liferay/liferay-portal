/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.layout.display.page.internal.request.attributes.contributor;

import com.liferay.analytics.reports.constants.AnalyticsReportsWebKeys;
import com.liferay.analytics.reports.info.item.AnalyticsReportsInfoItem;
import com.liferay.analytics.reports.info.item.AnalyticsReportsInfoItemRegistry;
import com.liferay.analytics.reports.info.item.ClassNameClassPKInfoItemIdentifier;
import com.liferay.info.display.request.attributes.contributor.InfoDisplayRequestAttributesContributor;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = InfoDisplayRequestAttributesContributor.class)
public class
	AnalyticsReportsLayoutDisplayPageInfoDisplayRequestAttributesContributor
		implements InfoDisplayRequestAttributesContributor {

	@Override
	public void addAttributes(HttpServletRequest httpServletRequest) {
		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			(LayoutDisplayPageObjectProvider<?>)httpServletRequest.getAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);

		if (layoutDisplayPageObjectProvider == null) {
			return;
		}

		InfoItemReference infoItemReference = new InfoItemReference(
			layoutDisplayPageObjectProvider.getClassName(),
			layoutDisplayPageObjectProvider.getClassPK());

		AnalyticsReportsInfoItem<?> analyticsReportsInfoItem =
			_analyticsReportsInfoItemRegistry.getAnalyticsReportsInfoItem(
				layoutDisplayPageObjectProvider.getClassName());

		if (analyticsReportsInfoItem == null) {
			infoItemReference = new InfoItemReference(
				LayoutDisplayPageObjectProvider.class.getName(),
				new ClassNameClassPKInfoItemIdentifier(
					layoutDisplayPageObjectProvider.getClassName(),
					layoutDisplayPageObjectProvider.getClassPK()));
		}

		httpServletRequest.setAttribute(
			AnalyticsReportsWebKeys.ANALYTICS_INFO_ITEM_REFERENCE,
			infoItemReference);
	}

	@Reference
	private AnalyticsReportsInfoItemRegistry _analyticsReportsInfoItemRegistry;

}