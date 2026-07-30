/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.dsr.site.initializer.internal.display.context.ViewAnalyticsDocumentsStatisticsAnalyticsSectionDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gianmarco Brunialti Masera
 */
@Component(service = FragmentRenderer.class)
public class ViewAnalyticsDocumentsStatisticsJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer
		<ViewAnalyticsDocumentsStatisticsAnalyticsSectionDisplayContext> {

	@Override
	public String getCollectionKey() {
		return "digital-sales-room-analytics";
	}

	@Override
	public String getKey() {
		return "dsr-view-analytics-documents-statistics";
	}

	@Override
	public String getLabelKey() {
		return "documents-statistics";
	}

	@Override
	protected ViewAnalyticsDocumentsStatisticsAnalyticsSectionDisplayContext
		getDisplayContext(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return new ViewAnalyticsDocumentsStatisticsAnalyticsSectionDisplayContext(
			analyticsSettingsManager, httpServletRequest,
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", themeDisplay.getCompanyId()));
	}

	@Override
	protected String getJSPPath() {
		return "/view_analytics_documents_statistics.jsp";
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}