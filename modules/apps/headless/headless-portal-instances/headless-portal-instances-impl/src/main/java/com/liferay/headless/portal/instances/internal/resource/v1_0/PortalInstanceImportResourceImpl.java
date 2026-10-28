/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.internal.resource.v1_0;

import com.liferay.headless.portal.instances.dto.v1_0.PortalInstance;
import com.liferay.headless.portal.instances.dto.v1_0.PortalInstanceImport;
import com.liferay.headless.portal.instances.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.portal.instances.internal.notifications.PortalInstanceNotificationUtil;
import com.liferay.headless.portal.instances.internal.security.permission.PortalInstancePermissionUtil;
import com.liferay.headless.portal.instances.resource.v1_0.PortalInstanceImportResource;
import com.liferay.portal.instances.constants.PortalInstancesNotificationConstants;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;

import jakarta.ws.rs.BadRequestException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alberto Chaparro
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/portal-instance-import.properties",
	scope = ServiceScope.PROTOTYPE, service = PortalInstanceImportResource.class
)
public class PortalInstanceImportResourceImpl
	extends BasePortalInstanceImportResourceImpl {

	@Override
	public PortalInstance postPortalInstanceImport(
			PortalInstanceImport portalInstanceImport)
		throws Exception {

		PortalInstancePermissionUtil.check();

		if (Validator.isNull(portalInstanceImport.getSchemaName())) {
			throw new BadRequestException("Schema name is required");
		}

		try {
			PortalInstance portalInstance = _portalInstanceDTOConverter.toDTO(
				_companyService.addDBPartitionCompany(
					portalInstanceImport.getSchemaName(),
					portalInstanceImport.getName(),
					portalInstanceImport.getVirtualHost(),
					portalInstanceImport.getWebId()));

			_sendUserNotificationEvent(
				portalInstance.getPortalInstanceId(),
				portalInstanceImport.getSchemaName());

			return portalInstance;
		}
		catch (Exception exception) {
			_log.error(
				"Unable to import portal instance " +
					portalInstanceImport.getSchemaName(),
				exception);

			throw exception;
		}
	}

	private void _sendUserNotificationEvent(
		String portalInstanceId, String schemaName) {

		PortalInstanceNotificationUtil.sendUserNotificationEvent(
			JSONUtil.put(
				"operationType",
				PortalInstancesNotificationConstants.OPERATION_TYPE_IMPORT
			).put(
				"portalInstanceId", portalInstanceId
			).put(
				"schemaName", schemaName
			).put(
				"status", PortalInstancesNotificationConstants.STATUS_SUCCESS
			),
			contextUser.getUserId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalInstanceImportResourceImpl.class);

	@Reference
	private CompanyService _companyService;

	@Reference(target = DTOConverterConstants.PORTAL_INSTANCE_DTO_CONVERTER)
	private DTOConverter<Company, PortalInstance> _portalInstanceDTOConverter;

}