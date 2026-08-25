/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.internal.resource.v1_0;

import com.liferay.headless.portal.instances.dto.v1_0.Admin;
import com.liferay.headless.portal.instances.dto.v1_0.PortalInstance;
import com.liferay.headless.portal.instances.dto.v1_0.PortalInstanceCopy;
import com.liferay.headless.portal.instances.dto.v1_0.PortalInstanceExport;
import com.liferay.headless.portal.instances.dto.v1_0.PortalInstanceImport;
import com.liferay.headless.portal.instances.resource.v1_0.PortalInstanceResource;
import com.liferay.portal.instances.exporter.PortalInstanceExporter;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.EmailAddressValidator;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.auth.EmailAddressValidatorFactory;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.ws.rs.BadRequestException;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alberto Chaparro
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/portal-instance.properties",
	scope = ServiceScope.PROTOTYPE, service = PortalInstanceResource.class
)
public class PortalInstanceResourceImpl extends BasePortalInstanceResourceImpl {

	@Override
	public void deletePortalInstance(String portalInstanceId) throws Exception {
		_checkPermission();

		Company company = _companyService.getCompanyByWebId(portalInstanceId);

		_companyService.deleteCompany(company.getCompanyId());
	}

	@Override
	public PortalInstance getPortalInstance(String portalInstanceId)
		throws Exception {

		_checkPermission();

		return _toPortalInstance(
			_companyService.getCompanyByWebId(portalInstanceId));
	}

	@Override
	public Page<PortalInstance> getPortalInstancesPage(Boolean skipDefault)
		throws Exception {

		_checkPermission();

		boolean finalSkipDefault = GetterUtil.getBoolean(skipDefault);

		List<PortalInstance> portalInstances = new ArrayList<>();

		_companyService.forEachCompany(
			company -> {
				if (!finalSkipDefault ||
					(PortalInstancePool.getDefaultCompanyId() !=
						company.getCompanyId())) {

					portalInstances.add(_toPortalInstance(company));
				}
			});

		return Page.of(portalInstances);
	}

	@Override
	public PortalInstance patchPortalInstance(
			String portalInstanceId, PortalInstance portalInstance)
		throws Exception {

		_checkPermission();

		Company company = _companyService.getCompanyByWebId(portalInstanceId);

		String virtualHostname = GetterUtil.getString(
			portalInstance.getVirtualHost(), company.getVirtualHostname());
		String domain = GetterUtil.getString(
			portalInstance.getDomain(), company.getMx());

		return _toPortalInstance(
			_companyService.updateCompany(
				company.getCompanyId(), virtualHostname, domain,
				company.getMaxUsers(), company.isActive()));
	}

	@Override
	public PortalInstance postPortalInstance(PortalInstance portalInstance)
		throws Exception {

		_checkPermission();

		Admin admin = portalInstance.getAdmin();

		Long companyId = portalInstance.getCompanyId();

		if (companyId == null) {
			companyId = 0L;
		}

		long finalCompanyId = companyId;

		if (admin != null) {
			_validateAdmin(admin);

			return _toPortalInstance(
				PortalInstances.addCompany(
					portalInstance.getSiteInitializerKey(),
					() -> _companyService.addCompany(
						finalCompanyId, portalInstance.getPortalInstanceId(),
						portalInstance.getVirtualHost(),
						portalInstance.getDomain(), 0, true, null, null,
						admin.getEmailAddress(), admin.getGivenName(), null,
						admin.getFamilyName())));
		}

		return _toPortalInstance(
			PortalInstances.addCompany(
				portalInstance.getSiteInitializerKey(),
				() -> _companyService.addCompany(
					finalCompanyId, portalInstance.getPortalInstanceId(),
					portalInstance.getVirtualHost(), portalInstance.getDomain(),
					0, true)));
	}

	@Override
	public PortalInstance postPortalInstanceCopy(
			String portalInstanceId, PortalInstanceCopy portalInstanceCopy)
		throws Exception {

		_checkFeatureFlag();

		_checkPermission();

		if (portalInstanceCopy == null) {
			throw new BadRequestException("Copy configuration is required");
		}

		if (Validator.isNull(portalInstanceCopy.getName())) {
			throw new BadRequestException("Name is required");
		}

		if (Validator.isNull(portalInstanceCopy.getVirtualHost())) {
			throw new BadRequestException("Virtual host is required");
		}

		if (Validator.isNull(portalInstanceCopy.getWebId())) {
			throw new BadRequestException("Web ID is required");
		}

		Company fromCompany = _companyService.getCompanyByWebId(
			portalInstanceId);

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
				"Unable to copy portal instance " + portalInstanceId,
				exception);

			throw exception;
		}
	}

	@Override
	public PortalInstanceExport postPortalInstanceExport(
			String portalInstanceId)
		throws Exception {

		_checkFeatureFlag();

		_checkPermission();

		try {
			PortalInstanceExport portalInstanceExport =
				new PortalInstanceExport();

			Company company = _companyService.getCompanyByWebId(
				portalInstanceId);

			String exportedPartitionName =
				_portalInstanceExporter.exportPortalInstance(
					company.getCompanyId());

			portalInstanceExport.setExportedPartitionName(
				() -> exportedPartitionName);

			portalInstanceExport.setSourceCompanyId(company::getCompanyId);

			return portalInstanceExport;
		}
		catch (Exception exception) {
			_log.error(
				"Unable to export portal instance " + portalInstanceId,
				exception);

			throw exception;
		}
	}

	@Override
	public PortalInstance postPortalInstanceImport(
			PortalInstanceImport portalInstanceImport)
		throws Exception {

		_checkFeatureFlag();

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

	@Override
	public void putPortalInstanceActivate(String portalInstanceId)
		throws Exception {

		_checkPermission();

		Company company = _companyService.getCompanyByWebId(portalInstanceId);

		_companyService.updateCompany(
			company.getCompanyId(), company.getVirtualHostname(),
			company.getMx(), company.getMaxUsers(), true);
	}

	@Override
	public void putPortalInstanceDeactivate(String portalInstanceId)
		throws Exception {

		_checkPermission();

		Company company = _companyService.getCompanyByWebId(portalInstanceId);

		_companyService.updateCompany(
			company.getCompanyId(), company.getVirtualHostname(),
			company.getMx(), company.getMaxUsers(), false);
	}

	private void _checkFeatureFlag() {
		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-11342")) {

			throw new UnsupportedOperationException();
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

	private void _validateAdmin(Admin admin) throws Exception {
		if (Validator.isNull(admin.getEmailAddress()) ||
			Validator.isNull(admin.getFamilyName()) ||
			Validator.isNull(admin.getGivenName())) {

			throw new UserScreenNameException.MustNotBeNull();
		}

		EmailAddressValidator emailAddressValidator =
			EmailAddressValidatorFactory.getInstance();

		if (!emailAddressValidator.validate(0, admin.getEmailAddress())) {
			throw new UserEmailAddressException.MustValidate(
				admin.getEmailAddress(), emailAddressValidator);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalInstanceResourceImpl.class);

	@Reference
	private CompanyService _companyService;

	@Reference
	private PortalInstanceExporter _portalInstanceExporter;

}