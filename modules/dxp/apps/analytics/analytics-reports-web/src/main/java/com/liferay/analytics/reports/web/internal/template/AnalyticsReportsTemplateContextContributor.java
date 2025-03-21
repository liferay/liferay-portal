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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sandro Chinea
 */
@Component(
	property = "type=" + TemplateContextContributor.TYPE_THEME,
	service = TemplateContextContributor.class
)
public class AnalyticsReportsTemplateContextContributor
	implements TemplateContextContributor {

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

	@Reference(
		target = "(component.name=com.liferay.analytics.reports.web.internal.product.navigation.control.menu.AnalyticsReportsProductNavigationControlMenuEntry)"
	)
	private ProductNavigationControlMenuEntry
		_analyticsReportsProductNavigationControlMenuEntry;

}