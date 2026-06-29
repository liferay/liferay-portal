/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.seo.studio.web.internal.display.context.PageSpeedChartsDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Kiana Suetani
 */
@Component(service = FragmentRenderer.class)
public class PageSpeedChartsFragmentRenderer
	extends BaseFragmentRenderer<PageSpeedChartsDisplayContext> {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	public String getLabel(Locale locale) {
		return language.get(locale, "google-pagespeed-metrics");
	}

	@Override
	protected PageSpeedChartsDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new PageSpeedChartsDisplayContext(
			_fetchSEOStudioPageSpeedResultObjectEntry(httpServletRequest));
	}

	@Override
	protected String getJSPPath() {
		return "/pagespeed_charts.jsp";
	}

	private ObjectEntry _fetchSEOStudioPageSpeedResultObjectEntry(
		HttpServletRequest httpServletRequest) {

		try {
			long companyId = portal.getCompanyId(httpServletRequest);

			ObjectDefinition objectDefinition =
				objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						"L_SEO_STUDIO_PAGESPEED_RESULT", companyId);

			if (objectDefinition == null) {
				return null;
			}

			Page<ObjectEntry> page = objectEntryManager.getObjectEntries(
				companyId, objectDefinition, null, null,
				getDTOConverterContext(objectDefinition), null,
				Pagination.of(1, 1), null,
				new Sort[] {new Sort("dateCreated", true)});

			if (page != null) {
				for (ObjectEntry objectEntry : page.getItems()) {
					return objectEntry;
				}
			}

			return null;
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PageSpeedChartsFragmentRenderer.class);

}