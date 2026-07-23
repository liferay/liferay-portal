/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.seo.studio.web.internal.constants.SEOStudioFDSNames;
import com.liferay.seo.studio.web.internal.display.context.IntegrationsDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kiana Suetani
 */
@Component(service = FragmentRenderer.class)
public class IntegrationsFragmentRenderer
	extends BaseFragmentRenderer<IntegrationsDisplayContext> {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	public String getLabel(Locale locale) {
		return language.get(locale, "integrations");
	}

	@Override
	protected IntegrationsDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long companyId = portal.getCompanyId(httpServletRequest);

		List<ListTypeEntry> seoStudioIntegrationTypeListTypeEntries =
			_getSEOStudioIntegrationTypeListTypeEntries(companyId);

		Map<String, String> configurationURLsMap = _getConfigurationURLsMap(
			seoStudioIntegrationTypeListTypeEntries, themeDisplay);

		List<ObjectEntry> seoStudioIntegrationObjectEntries =
			_getSEOStudioIntegrationObjectEntries(companyId);

		JSONArray viewsJSONArray = fdsSerializer.serializeViews(
			SEOStudioFDSNames.INTEGRATIONS, httpServletRequest);

		return new IntegrationsDisplayContext(
			configurationURLsMap, httpServletRequest, language,
			seoStudioIntegrationObjectEntries,
			seoStudioIntegrationTypeListTypeEntries, viewsJSONArray);
	}

	@Override
	protected String getJSPPath() {
		return "/integrations.jsp";
	}

	private Map<String, String> _getConfigurationURLsMap(
		List<ListTypeEntry> seoStudioIntegrationTypeListTypeEntries,
		ThemeDisplay themeDisplay) {

		Map<String, String> configurationURLsMap = new HashMap<>();

		for (ListTypeEntry listTypeEntry :
				seoStudioIntegrationTypeListTypeEntries) {

			String key = listTypeEntry.getKey();

			Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
				themeDisplay.getScopeGroupId(), false,
				StringPool.SLASH + StringUtil.toLowerCase(key));

			if (layout == null) {
				continue;
			}

			try {
				configurationURLsMap.put(
					key, portal.getLayoutFullURL(layout, themeDisplay));
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(portalException);
				}
			}
		}

		return configurationURLsMap;
	}

	private List<ObjectEntry> _getSEOStudioIntegrationObjectEntries(
		long companyId) {

		try {
			ObjectDefinition objectDefinition =
				objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						"L_SEO_STUDIO_INTEGRATION", companyId);

			if (objectDefinition == null) {
				return Collections.emptyList();
			}

			Page<ObjectEntry> page = objectEntryManager.getObjectEntries(
				companyId, objectDefinition, null, null,
				getDTOConverterContext(objectDefinition), null,
				Pagination.of(1, 100), null, null);

			return new ArrayList<>(page.getItems());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return Collections.emptyList();
		}
	}

	private List<ListTypeEntry> _getSEOStudioIntegrationTypeListTypeEntries(
		long companyId) {

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.
				fetchListTypeDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_INTEGRATION_TYPES", companyId);

		if (listTypeDefinition == null) {
			return Collections.emptyList();
		}

		return _listTypeEntryLocalService.getListTypeEntries(
			listTypeDefinition.getListTypeDefinitionId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IntegrationsFragmentRenderer.class);

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

}