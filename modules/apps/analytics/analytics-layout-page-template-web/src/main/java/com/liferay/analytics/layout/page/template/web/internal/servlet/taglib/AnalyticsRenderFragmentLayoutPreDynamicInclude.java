/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.layout.page.template.web.internal.servlet.taglib;

import com.liferay.analytics.layout.page.template.web.internal.servlet.taglib.constants.AnalyticsRenderFragmentLayoutConstants;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.TreeMapBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(
	property = "dynamic.include.key=com.liferay.layout,taglib#/render_fragment_layout/page.jsp#pre",
	service = DynamicInclude.class
)
public class AnalyticsRenderFragmentLayoutPreDynamicInclude
	extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String dynamicIncludeKey)
		throws IOException {

		try {
			if (!_analyticsSettingsManager.isAnalyticsEnabled(
					_portal.getCompanyId(httpServletRequest))) {

				return;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			(LayoutDisplayPageObjectProvider<?>)httpServletRequest.getAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);

		if ((layoutDisplayPageObjectProvider == null) ||
			!AnalyticsRenderFragmentLayoutConstants.analyticsAssetTypes.
				contains(layoutDisplayPageObjectProvider.getClassName())) {

			return;
		}

		PrintWriter printWriter = httpServletResponse.getWriter();

		String data = HtmlUtil.buildData(
			TreeMapBuilder.<String, Object>put(
				"analytics-asset-id",
				layoutDisplayPageObjectProvider.getClassPK()
			).put(
				"analytics-asset-title",
				layoutDisplayPageObjectProvider.getTitle(
					_portal.getLocale(httpServletRequest))
			).put(
				"analytics-external-reference-code",
				layoutDisplayPageObjectProvider.getExternalReferenceCode()
			).putAll(
				_getAnalyticsAssetTypeData(
					layoutDisplayPageObjectProvider.getDisplayObject())
			).build());

		printWriter.print("<div " + data.strip() + ">");
	}

	private Map<String, Object> _getAnalyticsAssetTypeData(
		Object displayObject) {

		if (displayObject instanceof BlogsEntry) {
			return HashMapBuilder.<String, Object>put(
				"analytics-asset-type", "blog"
			).build();
		}
		else if (displayObject instanceof JournalArticle journalArticle) {
			return HashMapBuilder.<String, Object>put(
				"analytics-asset-type", "web-content"
			).put(
				"analytics-web-content-resource-pk",
				journalArticle.getResourcePrimKey()
			).build();
		}
		else if (displayObject instanceof FileEntry fileEntry) {
			return HashMapBuilder.<String, Object>put(
				"analytics-asset-action", "preview"
			).put(
				"analytics-asset-type", "document"
			).put(
				"analytics-asset-version", fileEntry.getVersion()
			).build();
		}

		return Collections.emptyMap();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsRenderFragmentLayoutPreDynamicInclude.class);

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private Portal _portal;

}