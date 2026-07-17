/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.frontend.data.set.serializer.FDSSerializer;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Map;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Noor Najjar
 */
public abstract class BaseFragmentRenderer<T> implements FragmentRenderer {

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		return false;
	}

	@Override
	public void render(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			RequestDispatcher requestDispatcher =
				servletContext.getRequestDispatcher(getJSPPath());

			if (requestDispatcher == null) {
				throw new IOException(
					"Unable to get request dispatcher for " + getJSPPath());
			}

			T displayContext = getDisplayContext(httpServletRequest);

			if (displayContext != null) {
				Class<?> clazz = displayContext.getClass();

				httpServletRequest.setAttribute(
					clazz.getName(), displayContext);
			}

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (IOException | RuntimeException exception) {
			throw exception;
		}
		catch (Exception exception) {
			throw new IOException(exception);
		}
	}

	protected JSONObject fetchSEOStudioScanMetricJSONObject(
			HttpServletRequest httpServletRequest, String scope,
			long seoStudioScanRunId)
		throws Exception {

		long companyId = portal.getCompanyId(httpServletRequest);

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_SCAN_METRIC", companyId);

		if (objectDefinition == null) {
			return null;
		}

		Page<ObjectEntry> page = objectEntryManager.getObjectEntries(
			companyId, objectDefinition, null, null,
			getDTOConverterContext(objectDefinition),
			StringBundler.concat(
				"r_seoStudioScanRunToSEOStudioScanMetrics_seoStudioScanRunId ",
				"eq '", seoStudioScanRunId, "' and scope eq '", scope, "'"),
			Pagination.of(1, 1), null, null);

		ObjectEntry objectEntry = page.fetchFirstItem();

		if (objectEntry == null) {
			return null;
		}

		Map<String, Object> properties = objectEntry.getProperties();

		return JSONUtil.put(
			"affectedPagesCount",
			MapUtil.getInteger(properties, "affectedPagesCount")
		).put(
			"averageInsightsPerAffectedPage",
			MapUtil.getDouble(properties, "averageInsightsPerAffectedPage")
		).put(
			"categoryBreakdown",
			jsonFactory.createJSONObject(
				MapUtil.getString(properties, "categoryBreakdown"))
		).put(
			"criticalInsights",
			MapUtil.getInteger(properties, "criticalInsights")
		).put(
			"impactMix",
			jsonFactory.createJSONObject(
				MapUtil.getString(properties, "impactMix"))
		).put(
			"totalInsights", MapUtil.getInteger(properties, "totalInsights")
		);
	}

	protected T getDisplayContext(HttpServletRequest httpServletRequest) {
		return null;
	}

	protected DTOConverterContext getDTOConverterContext(
			ObjectDefinition objectDefinition)
		throws Exception {

		return new DefaultDTOConverterContext(
			false, null, null, null, null, LocaleUtil.getSiteDefault(), null,
			userLocalService.getUser(objectDefinition.getUserId()));
	}

	protected abstract String getJSPPath();

	@Reference
	protected FDSSerializer fdsSerializer;

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected Language language;

	@Reference
	protected ObjectDefinitionLocalService objectDefinitionLocalService;

	@Reference(target = "(object.entry.manager.storage.type=default)")
	protected ObjectEntryManager objectEntryManager;

	@Reference
	protected Portal portal;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.seo.studio.web)")
	protected ServletContext servletContext;

	@Reference
	protected UserLocalService userLocalService;

}