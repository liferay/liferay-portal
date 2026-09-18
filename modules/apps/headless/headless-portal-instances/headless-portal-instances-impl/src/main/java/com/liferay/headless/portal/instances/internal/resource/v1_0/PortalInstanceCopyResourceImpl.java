/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.internal.resource.v1_0;

import com.liferay.headless.portal.instances.dto.v1_0.PortalInstance;
import com.liferay.headless.portal.instances.dto.v1_0.PortalInstanceCopy;
import com.liferay.headless.portal.instances.resource.v1_0.PortalInstanceCopyResource;
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
	properties = "OSGI-INF/liferay/rest/v1_0/portal-instance-copy.properties",
	scope = ServiceScope.PROTOTYPE, service = PortalInstanceCopyResource.class
)
public class PortalInstanceCopyResourceImpl
	extends BasePortalInstanceCopyResourceImpl {

	@Override
	public PortalInstance postPortalInstanceCopy(
			PortalInstanceCopy portalInstanceCopy)
		throws Exception {

		_checkPermission();

		if (portalInstanceCopy == null) {
			throw new BadRequestException("Copy configuration is required");
		}

		if (Validator.isNull(portalInstanceCopy.getName())) {
			throw new BadRequestException("Name is required");
		}

		if (Validator.isNull(portalInstanceCopy.getSourcePortalInstanceId())) {
			throw new BadRequestException(
				"Source portal instance ID is required");
		}

		if (Validator.isNull(portalInstanceCopy.getVirtualHost())) {
			throw new BadRequestException("Virtual host is required");
		}

		if (Validator.isNull(portalInstanceCopy.getWebId())) {
			throw new BadRequestException("Web ID is required");
		}

		String sourcePortalInstanceId =
			portalInstanceCopy.getSourcePortalInstanceId();

		Company fromCompany = _companyService.getCompanyByWebId(
			sourcePortalInstanceId);

		Long toCompanyId = portalInstanceCopy.getDestinationCompanyId();

		if ((toCompanyId != null) && (toCompanyId <= 0)) {
			toCompanyId = null;
		}

		try {
			return _toPortalInstance(
				_companyService.copyDBPartitionCompany(
					fromCompany.getCompanyId(), toCompanyId,
					portalInstanceCopy.getName(),
					portalInstanceCopy.getVirtualHost(),
					portalInstanceCopy.getWebId()));
		}
		catch (Exception exception) {
			_log.error(
				"Unable to copy portal instance " + sourcePortalInstanceId,
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
		PortalInstanceCopyResourceImpl.class);

	@Reference
	private CompanyService _companyService;

}