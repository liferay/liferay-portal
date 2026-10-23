/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.jaxrs.container.response.filter;

import com.liferay.dispatch.executor.DispatchTaskStatus;
import com.liferay.dispatch.model.DispatchLog;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;
import com.liferay.site.pim.site.initializer.internal.util.PIMConnectorDispatchTriggerUtil;
import com.liferay.site.pim.site.initializer.internal.util.PIMConnectorFieldMappingsUtil;

import jakarta.annotation.Priority;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

import java.text.SimpleDateFormat;

import java.util.Map;
import java.util.TimeZone;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Tancredi Covioli
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=pimconnector)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Site.PIM.Connector.ContainerResponseFilter"
	},
	scope = ServiceScope.PROTOTYPE, service = ContainerResponseFilter.class
)
@Priority(Priorities.USER + 20)
public class PIMConnectorContainerResponseFilter
	implements ContainerResponseFilter {

	@Override
	public void filter(
		ContainerRequestContext containerRequestContext,
		ContainerResponseContext containerResponseContext) {

		if (!FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-96666")) {

			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_CONNECTOR,
					CompanyThreadLocal.getCompanyId());

		if (objectDefinition == null) {
			return;
		}

		Object entity = containerResponseContext.getEntity();

		try {
			if (entity instanceof ObjectEntry objectEntry) {
				_addProperties(objectDefinition, objectEntry);
			}
			else if (entity instanceof Page<?> page) {
				for (Object item : page.getItems()) {
					if (!(item instanceof ObjectEntry objectEntry)) {
						continue;
					}

					_addProperties(objectDefinition, objectEntry);
				}
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private void _addProperties(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws PortalException {

		if (objectEntry.getId() == null) {
			return;
		}

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(objectEntry.getId());

		if ((serviceBuilderObjectEntry == null) ||
			(serviceBuilderObjectEntry.getObjectDefinitionId() !=
				objectDefinition.getObjectDefinitionId())) {

			return;
		}

		Map<String, Object> properties = objectEntry.getProperties();

		if (properties == null) {
			return;
		}

		DispatchLog dispatchLog =
			PIMConnectorDispatchTriggerUtil.fetchLatestDispatchLog(
				serviceBuilderObjectEntry.getCompanyId(),
				serviceBuilderObjectEntry.getExternalReferenceCode());

		properties.put(
			"lastExecutionStatus", _getLastExecutionStatus(dispatchLog));

		if (dispatchLog != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

			properties.put(
				"lastExecutionDate",
				simpleDateFormat.format(dispatchLog.getStartDate()));
		}

		properties.put(
			"mappingStatus",
			_getMappingStatus(
				PIMConnectorFieldMappingsUtil.isMappingComplete(
					serviceBuilderObjectEntry,
					_pimConnectorRegistry.getPIMConnector(
						MapUtil.getString(
							serviceBuilderObjectEntry.getValues(), "key")))));
	}

	private String _getLastExecutionStatus(DispatchLog dispatchLog) {
		if (dispatchLog == null) {
			return "never-executed";
		}

		if (dispatchLog.getStatus() == DispatchTaskStatus.FAILED.getStatus()) {
			return "failed";
		}

		return "success";
	}

	private String _getMappingStatus(boolean mappingComplete) {
		if (mappingComplete) {
			return "complete";
		}

		return "incomplete";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PIMConnectorContainerResponseFilter.class);

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private PIMConnectorRegistry _pimConnectorRegistry;

}