/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.internal.resource.v1_0;

import com.liferay.headless.portal.instances.dto.v1_0.PortalInstance;
import com.liferay.headless.portal.instances.dto.v1_0.PortalInstanceImport;
import com.liferay.headless.portal.instances.resource.v1_0.PortalInstanceImportResource;
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
	properties = "OSGI-INF/liferay/rest/v1_0/portal-instance-import.properties",
	scope = ServiceScope.PROTOTYPE, service = PortalInstanceImportResource.class
)
public class PortalInstanceImportResourceImpl
	extends BasePortalInstanceImportResourceImpl {

	@Override
	public PortalInstance postPortalInstanceImport(
			PortalInstanceImport portalInstanceImport)
		throws Exception {

		_checkPermission();

		if (portalInstanceImport == null) {
			throw new BadRequestException("Import configuration is required");
		}

		String schemaName = portalInstanceImport.getSchemaName();

		if (Validator.isNull(schemaName)) {
			throw new BadRequestException("Schema name is required");
		}

		try {
			return _toPortalInstance(
				_companyService.addDBPartitionCompany(
					schemaName, portalInstanceImport.getName(),
					portalInstanceImport.getVirtualHost(),
					portalInstanceImport.getWebId()));
		}
		catch (Exception exception) {
			_log.error(
				"Unable to import portal instance " + schemaName, exception);

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

	private PortalInstance _toPortalInstance(Company company) {
		return new PortalInstance() {
			{
				setActive(company::isActive);
				setCompanyId(company::getCompanyId);
				setDomain(company::getMx);
				setPortalInstanceId(company::getWebId);
				setVirtualHost(company::getVirtualHostname);
			}
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalInstanceImportResourceImpl.class);

	@Reference
	private CompanyService _companyService;

}