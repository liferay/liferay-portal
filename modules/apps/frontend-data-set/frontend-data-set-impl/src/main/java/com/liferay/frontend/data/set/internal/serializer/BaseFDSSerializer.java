/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.serializer;

import com.liferay.frontend.data.set.internal.url.FDSAPIURLBuilder;
import com.liferay.frontend.data.set.url.FDSAPIURLResolverRegistry;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
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
			"('fdsName' eq '", fdsName, "' and 'creatorId' eq '",
			themeDisplay.getUserId(), "')");

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.fetchObjectDefinition(
				PortalUtil.getCompanyId(httpServletRequest),
				"DataSetSnapshotFDSConfig");

		ObjectEntryManager objectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				objectEntryManagerRegistry.getObjectEntryManager(
					objectDefinition.getCompanyId(),
					objectDefinition.getStorageType()));

		Group scopeGroup = themeDisplay.getScopeGroup();

		Page<ObjectEntry> page = objectEntryManager.getObjectEntries(
			PortalUtil.getCompanyId(httpServletRequest), objectDefinition,
			scopeGroup.getGroupKey(), null,
			new DefaultDTOConverterContext(
				false, null, null, null, null,
				LocaleUtil.getMostRelevantLocale(), null, null),
			filterExpression, null, null, null);

		Collection<ObjectEntry> objectEntries = page.getItems();

		return JSONUtil.toJSONArray(
			objectEntries,
			(ObjectEntry objectEntry) -> {
				Map<String, Object> properties = objectEntry.getProperties();

				return JSONUtil.put(
					"snapshotConfig", properties.get("viewConfig")
				).put(
					"snapshotERC", objectEntry.getExternalReferenceCode()
				).put(
					"snapshotLabel", String.valueOf(properties.get("label"))
				);
			});
	}

	@Reference
	protected FDSAPIURLResolverRegistry fdsAPIURLResolverRegistry;

}