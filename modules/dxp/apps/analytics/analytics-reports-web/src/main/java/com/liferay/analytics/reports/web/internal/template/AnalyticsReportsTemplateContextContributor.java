/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.web.internal.template;

import com.liferay.analytics.reports.web.internal.constants.ProductNavigationControlMenuEntryConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Sandro Chinea
 */
public class AnalyticsReportsTemplateContextContributor
	implements TemplateContextContributor {

	public AnalyticsReportsTemplateContextContributor(
		ProductNavigationControlMenuEntry
			analyticsReportsProductNavigationControlMenuEntry) {

		_analyticsReportsProductNavigationControlMenuEntry =
			analyticsReportsProductNavigationControlMenuEntry;
	}

	@Override
	public void prepare(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest) {

		try {
			if (!_analyticsReportsProductNavigationControlMenuEntry.isShow(
					httpServletRequest)) {

				return;
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return;
		}

		if (_analyticsReportsProductNavigationControlMenuEntry.isPanelStateOpen(
				httpServletRequest,
				ProductNavigationControlMenuEntryConstants.
					SESSION_CLICKS_KEY)) {

			String cssClass = GetterUtil.getString(
				contextObjects.get("bodyCssClass"));

			contextObjects.put(
				"bodyCssClass",
				cssClass +
					" lfr-has-analytics-reports-panel open-admin-panel open");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsReportsTemplateContextContributor.class);

	private final ProductNavigationControlMenuEntry
		_analyticsReportsProductNavigationControlMenuEntry;

}