/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.resource.v1_0;

import com.liferay.analytics.cms.rest.dto.v1_0.PerformanceTopAsset;
import com.liferay.analytics.cms.rest.internal.client.AnalyticsCloudClient;
import com.liferay.analytics.cms.rest.internal.depot.entry.util.DepotEntryUtil;
import com.liferay.analytics.cms.rest.resource.v1_0.PerformanceTopAssetResource;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegateBuilder;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegateBuilderRegistry;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

import java.io.InputStream;

import java.time.LocalDate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rachael Koestartyo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/performance-top-asset.properties",
	scope = ServiceScope.PROTOTYPE, service = PerformanceTopAssetResource.class
)
public class PerformanceTopAssetResourceImpl
	extends BasePerformanceTopAssetResourceImpl {

	@Override
	public Response getPerformanceTopAssetExport(
			Long[] depotEntryIds, Integer rangeKey, String search,
			Filter filter, Sort[] sorts)
		throws Exception {

		LicenseManagerUtil.checkFreeTier();

		Long[] groupIds = DepotEntryUtil.getGroupIds(
			DepotEntryUtil.getDepotEntries(
				contextCompany.getCompanyId(), depotEntryIds));

		AnalyticsCloudClient analyticsCloudClient = new AnalyticsCloudClient(
			_http);

		InputStream inputStream = analyticsCloudClient.getInputStream(
			_analyticsSettingsManager.getAnalyticsConfiguration(
				contextCompany.getCompanyId()),
			_getFilterString(), Arrays.asList(groupIds), search, null,
			"/summaries/export", rangeKey, sorts);

		return Response.ok(
			(StreamingOutput)outputStream -> StreamUtil.transfer(
				inputStream, outputStream)
		).header(
			"Content-Disposition",
			StringBundler.concat(
				"attachment; filename=top-assets-", LocalDate.now(), ".csv")
		).build();
	}

	@Override
	public Page<PerformanceTopAsset> getPerformanceTopAssetPage(
			Long[] depotEntryIds, Integer rangeKey, String search,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		LicenseManagerUtil.checkFreeTier();

		AnalyticsCloudClient analyticsCloudClient = new AnalyticsCloudClient(
			_http);

		Long[] groupIds = DepotEntryUtil.getGroupIds(
			DepotEntryUtil.getDepotEntries(
				contextCompany.getCompanyId(), depotEntryIds));

		Page<PerformanceTopAsset> performanceTopAssetPage =
			analyticsCloudClient.getPerformanceTopAssetPage(
				_analyticsSettingsManager.getAnalyticsConfiguration(
					contextCompany.getCompanyId()),
				_getFilterString(), Arrays.asList(groupIds), search, pagination,
				rangeKey, sorts);

		if ((contextHttpServletRequest != null) &&
			StringUtil.contains(
				ParamUtil.getString(contextHttpServletRequest, "nestedFields"),
				"embedded")) {

			Map<String, ObjectDefinition> objectDefinitions = new HashMap<>();

			for (PerformanceTopAsset performanceTopAsset :
					performanceTopAssetPage.getItems()) {

				_setEmbedded(groupIds, objectDefinitions, performanceTopAsset);
			}
		}

		return performanceTopAssetPage;
	}

	private String _getFilterString() {
		MultivaluedMap<String, String> queryParameters =
			contextUriInfo.getQueryParameters();

		return queryParameters.getFirst("filter");
	}

	private ObjectDefinition _getObjectDefinition(
		String name, Map<String, ObjectDefinition> objectDefinitions) {

		if (objectDefinitions.containsKey(name)) {
			return objectDefinitions.get(name);
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				contextCompany.getCompanyId(), name);

		objectDefinitions.put(name, objectDefinition);

		return objectDefinition;
	}

	private ObjectEntry _getObjectEntry(
		String externalReferenceCode, Long[] groupIds,
		ObjectDefinition objectDefinition) {

		if (objectDefinition == null) {
			return null;
		}

		if (Objects.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_COMPANY)) {

			return _objectEntryLocalService.fetchObjectEntry(
				externalReferenceCode, 0,
				objectDefinition.getObjectDefinitionId());
		}

		for (Long groupId : groupIds) {
			ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
				externalReferenceCode, groupId,
				objectDefinition.getObjectDefinitionId());

			if (objectEntry != null) {
				return objectEntry;
			}
		}

		return null;
	}

	private void _setEmbedded(
		Long[] groupIds, Map<String, ObjectDefinition> objectDefinitions,
		PerformanceTopAsset performanceTopAsset) {

		ObjectEntry objectEntry = _getObjectEntry(
			performanceTopAsset.getExternalReferenceCode(), groupIds,
			_getObjectDefinition(
				performanceTopAsset.getType(), objectDefinitions));

		if (objectEntry == null) {
			return;
		}

		performanceTopAsset.setClassName(objectEntry::getModelClassName);

		DTOConverter<?, ?> dtoConverter = _dtoConverterRegistry.getDTOConverter(
			objectEntry.getModelClassName());

		if (dtoConverter == null) {
			return;
		}

		VulcanCRUDItemDelegateBuilder vulcanCRUDItemDelegateBuilder =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				contextCompany, dtoConverter.getExternalDTOClassName());

		if (vulcanCRUDItemDelegateBuilder == null) {
			return;
		}

		performanceTopAsset.setEmbedded(
			NestedFieldsSupplier.supplyScopedUnsafeSupplier(
				"embedded",
				() -> vulcanCRUDItemDelegateBuilder.acceptLanguage(
					contextAcceptLanguage
				).groupLocalService(
					groupLocalService
				).httpServletRequest(
					contextHttpServletRequest
				).httpServletResponse(
					contextHttpServletResponse
				).resourceActionLocalService(
					resourceActionLocalService
				).resourcePermissionLocalService(
					resourcePermissionLocalService
				).roleLocalService(
					roleLocalService
				).scopeChecker(
					contextScopeChecker
				).uriInfo(
					contextUriInfo
				).user(
					contextUser
				).build(
				).fetchItem(
					objectEntry.getObjectEntryId()
				)));
	}

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private Http _http;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private VulcanCRUDItemDelegateBuilderRegistry
		_vulcanCRUDItemDelegateBuilderRegistry;

}