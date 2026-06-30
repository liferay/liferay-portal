/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.seo.studio.web.internal.constants.SEOStudioFDSNames;
import com.liferay.seo.studio.web.internal.display.context.ViewOnPageDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Noor Najjar
 */
@Component(service = FragmentRenderer.class)
public class ViewOnPageFragmentRenderer
	extends BaseFragmentRenderer<ViewOnPageDisplayContext> {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	public String getLabel(Locale locale) {
		return language.get(locale, "on-page-view");
	}

	@Override
	protected ViewOnPageDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		ObjectEntry objectEntry = _fetchObjectEntry(httpServletRequest);

		JSONArray filtersJSONArray = fdsSerializer.serializeFilters(
			SEOStudioFDSNames.INSIGHT_TYPE_SECTION, httpServletRequest);

		List<Long> seoStudioScanIds = _getSEOStudioScanIds(
			httpServletRequest, objectEntry);

		JSONArray viewsJSONArray = fdsSerializer.serializeViews(
			SEOStudioFDSNames.INSIGHT_TYPE_SECTION, httpServletRequest);

		return new ViewOnPageDisplayContext(
			filtersJSONArray, httpServletRequest, language, objectEntry,
			seoStudioScanIds, viewsJSONArray);
	}

	@Override
	protected String getJSPPath() {
		return "/view_on_page.jsp";
	}

	private ObjectEntry _fetchObjectEntry(
		HttpServletRequest httpServletRequest) {

		try {
			long companyId = portal.getCompanyId(httpServletRequest);

			ObjectDefinition objectDefinition =
				objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						"L_SEO_STUDIO_SCAN_RUN", companyId);

			if (objectDefinition == null) {
				return null;
			}

			Page<ObjectEntry> page = objectEntryManager.getObjectEntries(
				companyId, objectDefinition, null, null,
				getDTOConverterContext(objectDefinition),
				"state eq 'completed'", Pagination.of(1, 1), null,
				new Sort[] {new Sort("requestDate", true)});

			if (page == null) {
				return null;
			}

			return page.fetchFirstItem();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return null;
		}
	}

	private List<Long> _getSEOStudioScanIds(
		HttpServletRequest httpServletRequest, ObjectEntry objectEntry) {

		if (objectEntry == null) {
			return Collections.emptyList();
		}

		try {
			long companyId = portal.getCompanyId(httpServletRequest);

			ObjectDefinition objectDefinition =
				objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						"L_SEO_STUDIO_SCAN", companyId);

			if (objectDefinition == null) {
				return Collections.emptyList();
			}

			Page<ObjectEntry> page = objectEntryManager.getObjectEntries(
				companyId, objectDefinition, null, null,
				getDTOConverterContext(objectDefinition),
				"r_seoStudioScanRunToSEOStudioScans_seoStudioScanRunId eq '" +
					objectEntry.getId() + "'",
				Pagination.of(1, 100), null, null);

			if (page == null) {
				return Collections.emptyList();
			}

			return TransformUtil.transform(page.getItems(), ObjectEntry::getId);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return Collections.emptyList();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewOnPageFragmentRenderer.class);

}