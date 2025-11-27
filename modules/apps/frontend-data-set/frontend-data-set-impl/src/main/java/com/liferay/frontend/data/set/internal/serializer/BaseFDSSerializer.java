/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.serializer;

import com.liferay.frontend.data.set.internal.url.FDSAPIURLBuilder;
import com.liferay.frontend.data.set.url.FDSAPIURLResolverRegistry;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.Map;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Sanz
 */
public abstract class BaseFDSSerializer {

	public FDSAPIURLBuilder createFDSAPIURLBuilder(
		HttpServletRequest httpServletRequest, String restApplication,
		String restEndpoint, String restSchema) {

		return new FDSAPIURLBuilder(
			fdsAPIURLResolverRegistry, httpServletRequest, restApplication,
			restEndpoint, restSchema);
	}

	protected JSONArray serializeSnapshots(
			String fdsName, HttpServletRequest httpServletRequest,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectEntryManagerRegistry objectEntryManagerRegistry)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String filterExpression = StringBundler.concat(
			"(fdsName eq '", fdsName, "' and creatorId eq ",
			themeDisplay.getUserId(), ")");

		ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);

		JSONArray jsonArray = JSONUtil.putAll();

		try {
			ObjectDefinition objectDefinition =
				objectDefinitionLocalService.fetchObjectDefinition(
					PortalUtil.getCompanyId(httpServletRequest),
					"DataSetSnapshotFDSConfig");

			ObjectEntryManager objectEntryManager =
				DefaultObjectEntryManagerProvider.provide(
					objectEntryManagerRegistry.getObjectEntryManager(
						objectDefinition.getCompanyId(),
						objectDefinition.getStorageType()));

			Page<ObjectEntry> page = objectEntryManager.getObjectEntries(
				PortalUtil.getCompanyId(httpServletRequest), objectDefinition,
				null, null,
				new DefaultDTOConverterContext(
					false, null, null, null, null,
					LocaleUtil.getMostRelevantLocale(), null, null),
				filterExpression, null, null, null);

			Collection<ObjectEntry> objectEntries = page.getItems();

			jsonArray = JSONUtil.toJSONArray(
				objectEntries,
				(ObjectEntry objectEntry) -> {
					Map<String, Object> properties =
						objectEntry.getProperties();

					return JSONUtil.put(
						"configuration", properties.get("viewConfig")
					).put(
						"erc", objectEntry.getExternalReferenceCode()
					).put(
						"label", String.valueOf(properties.get("label"))
					);
				});
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get snapshot FDS config object entries",
					exception);
			}
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(false);
		}

		return jsonArray;
	}

	@Reference
	protected FDSAPIURLResolverRegistry fdsAPIURLResolverRegistry;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseFDSSerializer.class);

}