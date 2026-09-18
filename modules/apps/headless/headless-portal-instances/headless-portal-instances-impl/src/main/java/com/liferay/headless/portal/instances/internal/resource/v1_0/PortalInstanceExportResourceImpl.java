/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.internal.resource.v1_0;

import com.liferay.headless.portal.instances.dto.v1_0.PortalInstanceExport;
import com.liferay.headless.portal.instances.resource.v1_0.PortalInstanceExportResource;
import com.liferay.portal.instances.exporter.PortalInstanceExporter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyService;
import com.liferay.portal.kernel.util.Validator;

import jakarta.ws.rs.BadRequestException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alberto Chaparro
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/portal-instance-export.properties",
	scope = ServiceScope.PROTOTYPE, service = PortalInstanceExportResource.class
)
public class PortalInstanceExportResourceImpl
	extends BasePortalInstanceExportResourceImpl {

	@Override
	public PortalInstanceExport postPortalInstanceExport(
			PortalInstanceExport portalInstanceExport)
		throws Exception {

		_checkPermission();

		if ((portalInstanceExport == null) ||
			Validator.isNull(portalInstanceExport.getPortalInstanceId())) {

			throw new BadRequestException("Portal instance ID is required");
		}

		String portalInstanceId = portalInstanceExport.getPortalInstanceId();

		Company company = _companyService.getCompanyByWebId(portalInstanceId);

		try {
			String exportedPartitionName =
				_portalInstanceExporter.exportPortalInstance(
					company.getCompanyId());

			PortalInstanceExport newPortalInstanceExport =
				new PortalInstanceExport();

			newPortalInstanceExport.setExportedPartitionName(
				() -> exportedPartitionName);
			newPortalInstanceExport.setPortalInstanceId(() -> portalInstanceId);
			newPortalInstanceExport.setSourceCompanyId(company::getCompanyId);

			return newPortalInstanceExport;
		}
		catch (Exception exception) {
			_log.error(
				"Unable to export portal instance " + portalInstanceId,
				exception);

			throw exception;
		}
	}

	private void _checkPermission() throws Exception {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (!permissionChecker.isOmniadmin()) {
			throw new PrincipalException.MustBeOmniadmin(permissionChecker);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalInstanceExportResourceImpl.class);

	@Reference
	private CompanyService _companyService;

	@Reference
	private PortalInstanceExporter _portalInstanceExporter;

}