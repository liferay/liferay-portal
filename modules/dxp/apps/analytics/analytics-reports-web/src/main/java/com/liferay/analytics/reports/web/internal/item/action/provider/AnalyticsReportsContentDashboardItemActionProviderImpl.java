/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.web.internal.item.action.provider;

import com.liferay.analytics.reports.info.action.provider.AnalyticsReportsContentDashboardItemActionProvider;
import com.liferay.analytics.reports.info.item.AnalyticsReportsInfoItem;
import com.liferay.analytics.reports.info.item.AnalyticsReportsInfoItemRegistry;
import com.liferay.analytics.reports.info.item.provider.AnalyticsReportsInfoItemObjectProvider;
import com.liferay.analytics.reports.web.internal.info.item.provider.util.AnalyticsReportsInfoItemObjectProviderRegistryUtil;
import com.liferay.analytics.reports.web.internal.item.action.AnalyticsReportsContentDashboardItemAction;
import com.liferay.analytics.reports.web.internal.util.AnalyticsReportsUtil;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.exception.ContentDashboardItemActionException;
import com.liferay.info.item.InfoItemReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Arques
 */
@Component(service = AnalyticsReportsContentDashboardItemActionProvider.class)
public class AnalyticsReportsContentDashboardItemActionProviderImpl
	implements AnalyticsReportsContentDashboardItemActionProvider {

	@Override
	public ContentDashboardItemAction getContentDashboardItemAction(
			HttpServletRequest httpServletRequest,
			InfoItemReference infoItemReference)
		throws ContentDashboardItemActionException {

		try {
			if (!isShowContentDashboardItemAction(
					httpServletRequest, infoItemReference)) {

				return null;
			}

			return new AnalyticsReportsContentDashboardItemAction(
				AnalyticsReportsUtil.getAnalyticsReportsPanelURL(
					infoItemReference, httpServletRequest, _portal,
					_portletURLFactory));
		}
		catch (PortalException | WindowStateException exception) {
			throw new ContentDashboardItemActionException(exception);
		}
	}

	@Override
	public boolean isShowContentDashboardItemAction(
			HttpServletRequest httpServletRequest,
			InfoItemReference infoItemReference)
		throws PortalException {

		AnalyticsReportsInfoItemObjectProvider<Object>
			analyticsReportsInfoItemObjectProvider =
				(AnalyticsReportsInfoItemObjectProvider<Object>)
					AnalyticsReportsInfoItemObjectProviderRegistryUtil.
						getAnalyticsReportsInfoItemObjectProvider(
							infoItemReference.getClassName());

		if (analyticsReportsInfoItemObjectProvider == null) {
			return false;
		}

		Object analyticsReportsInfoItemObject =
			analyticsReportsInfoItemObjectProvider.
				getAnalyticsReportsInfoItemObject(infoItemReference);

		if (analyticsReportsInfoItemObject == null) {
			return false;
		}

		AnalyticsReportsInfoItem<Object> analyticsReportsInfoItem =
			(AnalyticsReportsInfoItem<Object>)
				_analyticsReportsInfoItemRegistry.getAnalyticsReportsInfoItem(
					infoItemReference.getClassName());

		if ((analyticsReportsInfoItem == null) ||
			!analyticsReportsInfoItem.isShow(analyticsReportsInfoItemObject)) {

			return false;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			if (AnalyticsReportsUtil.isShowAnalyticsReportsPanel(
					_analyticsSettingsManager, themeDisplay.getCompanyId(),
					httpServletRequest)) {

				return true;
			}
		}
		catch (PortalException portalException) {
			throw portalException;
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsReportsContentDashboardItemActionProviderImpl.class);

	@Reference
	private AnalyticsReportsInfoItemRegistry _analyticsReportsInfoItemRegistry;

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private Portal _portal;

	@Reference
	private PortletURLFactory _portletURLFactory;

}