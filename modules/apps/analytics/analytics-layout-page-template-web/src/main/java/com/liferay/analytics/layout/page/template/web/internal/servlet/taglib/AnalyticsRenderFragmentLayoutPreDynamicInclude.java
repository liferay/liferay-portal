/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.layout.page.template.web.internal.servlet.taglib;

import com.liferay.analytics.layout.page.template.web.internal.servlet.taglib.util.AnalyticsRenderFragmentLayoutUtil;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.TreeMapBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = DynamicInclude.class)
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
			!AnalyticsRenderFragmentLayoutUtil.isTrackeable(
				layoutDisplayPageObjectProvider)) {

			return;
		}

		_printAnalyticsCloudAssetTracker(
			layoutDisplayPageObjectProvider.getClassName(),
			layoutDisplayPageObjectProvider.getClassPK(),
			layoutDisplayPageObjectProvider.getDisplayObject(),
			httpServletResponse.getWriter(),
			layoutDisplayPageObjectProvider.getTitle(
				_portal.getLocale(httpServletRequest)));
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"com.liferay.layout,taglib#/render_fragment_layout/page.jsp#pre");
	}

	private <T> Map<String, Function<T, String>> _initAttributes(
		AnalyticsRenderFragmentLayoutUtil.AnalyticsAssetType analyticsAssetType,
		long classPK, String title) {

		return TreeMapBuilder.<String, Function<T, String>>put(
			"data-analytics-asset-id", displayObject -> String.valueOf(classPK)
		).put(
			"data-analytics-asset-title",
			displayObject -> HtmlUtil.escapeAttribute(title)
		).put(
			"data-analytics-asset-type",
			displayObject -> analyticsAssetType.getType()
		).putAll(
			analyticsAssetType.getAttributes()
		).build();
	}

	private <T> void _printAnalyticsCloudAssetTracker(
		String className, long classPK, T displayObject,
		PrintWriter printWriter, String title) {

		AnalyticsRenderFragmentLayoutUtil.AnalyticsAssetType
			analyticsAssetType =
				AnalyticsRenderFragmentLayoutUtil.getAnalyticsAssetType(
					className);

		if (analyticsAssetType == null) {
			return;
		}

		Map<String, Function<T, String>> attributes = _initAttributes(
			analyticsAssetType, classPK, title);

		StringBundler sb = new StringBundler((attributes.size() * 5) + 2);

		sb.append("<div ");

		Set<Map.Entry<String, Function<T, String>>> entries =
			attributes.entrySet();

		Iterator<Map.Entry<String, Function<T, String>>> iterator =
			entries.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, Function<T, String>> entry = iterator.next();

			sb.append(entry.getKey());

			sb.append("=\"");

			Function<T, String> function = entry.getValue();

			sb.append(function.apply(displayObject));

			sb.append("\"");

			if (iterator.hasNext()) {
				sb.append(" ");
			}
		}

		sb.append(">");

		printWriter.print(sb);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsRenderFragmentLayoutPreDynamicInclude.class);

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private Portal _portal;

}