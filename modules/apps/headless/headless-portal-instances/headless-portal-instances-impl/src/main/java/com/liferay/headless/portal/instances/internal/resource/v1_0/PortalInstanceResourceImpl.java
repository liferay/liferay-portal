/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.internal.resource.v1_0;

import com.liferay.headless.portal.instances.dto.v1_0.Admin;
import com.liferay.headless.portal.instances.dto.v1_0.PortalInstance;
import com.liferay.headless.portal.instances.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.portal.instances.internal.notifications.PortalInstanceNotificationUtil;
import com.liferay.headless.portal.instances.internal.security.permission.PortalInstancePermissionUtil;
import com.liferay.headless.portal.instances.resource.v1_0.PortalInstanceResource;
import com.liferay.portal.instances.constants.PortalInstancesNotificationConstants;
import com.liferay.portal.kernel.exception.ContactNameException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.EmailAddressValidator;
import com.liferay.portal.kernel.service.CompanyService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.auth.EmailAddressValidatorFactory;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

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
		PortalInstancePermissionUtil.check();

		Company company = _companyService.getCompanyByWebId(portalInstanceId);

		_companyService.deleteCompany(company.getCompanyId());

		PortalInstanceNotificationUtil.sendUserNotificationEvent(
			JSONUtil.put(
				"operationType",
				PortalInstancesNotificationConstants.OPERATION_TYPE_DELETE
			).put(
				"portalInstanceId", portalInstanceId
			).put(
				"status", PortalInstancesNotificationConstants.STATUS_SUCCESS
			),
			contextUser.getUserId());
	}

	@Override
	public PortalInstance getPortalInstance(String portalInstanceId)
		throws Exception {

		PortalInstancePermissionUtil.check();

		return _portalInstanceDTOConverter.toDTO(
			_companyService.getCompanyByWebId(portalInstanceId));
	}

	@Override
	public Page<PortalInstance> getPortalInstancesPage(Boolean skipDefault)
		throws Exception {

		PortalInstancePermissionUtil.check();

		boolean finalSkipDefault = GetterUtil.getBoolean(skipDefault);

		List<PortalInstance> portalInstances = new ArrayList<>();

		_companyService.forEachCompany(
			company -> {
				if (!finalSkipDefault ||
					(PortalInstancePool.getDefaultCompanyId() !=
						company.getCompanyId())) {

					portalInstances.add(
						_portalInstanceDTOConverter.toDTO(company));
				}
			});

		return Page.of(portalInstances);
	}

	@Override
	public PortalInstance patchPortalInstance(
			String portalInstanceId, PortalInstance portalInstance)
		throws Exception {

		PortalInstancePermissionUtil.check();

		Company company = _companyService.getCompanyByWebId(portalInstanceId);

		String virtualHostname = GetterUtil.getString(
			portalInstance.getVirtualHost(), company.getVirtualHostname());
		String domain = GetterUtil.getString(
			portalInstance.getDomain(), company.getMx());

		return _portalInstanceDTOConverter.toDTO(
			_companyService.updateCompany(
				company.getCompanyId(), virtualHostname, domain,
				company.getMaxUsers(), company.isActive()));
	}

	@Override
	public PortalInstance postPortalInstance(PortalInstance portalInstance)
		throws Exception {

		PortalInstancePermissionUtil.check();

		return _addPortalInstance(portalInstance);
	}

	@Override
	public void putPortalInstanceActivate(String portalInstanceId)
		throws Exception {

		PortalInstancePermissionUtil.check();

		Company company = _companyService.getCompanyByWebId(portalInstanceId);

		_companyService.updateCompany(
			company.getCompanyId(), company.getVirtualHostname(),
			company.getMx(), company.getMaxUsers(), true);
	}

	@Override
	public void putPortalInstanceDeactivate(String portalInstanceId)
		throws Exception {

		PortalInstancePermissionUtil.check();

		Company company = _companyService.getCompanyByWebId(portalInstanceId);

		_companyService.updateCompany(
			company.getCompanyId(), company.getVirtualHostname(),
			company.getMx(), company.getMaxUsers(), false);
	}

	private PortalInstance _addPortalInstance(PortalInstance portalInstance)
		throws Exception {

		_validateSiteInitializerKey(portalInstance.getSiteInitializerKey());

		Admin admin = portalInstance.getAdmin();

		Long companyId = portalInstance.getCompanyId();

		if (companyId == null) {
			companyId = 0L;
		}

		long finalCompanyId = companyId;

		boolean active = GetterUtil.getBoolean(
			portalInstance.getActive(), true);
		int maxUsers = GetterUtil.getInteger(portalInstance.getMaxUsers());

		if (admin != null) {
			_validateAdmin(admin);

			return _portalInstanceDTOConverter.toDTO(
				PortalInstances.addCompany(
					portalInstance.getSiteInitializerKey(),
					() -> _companyService.addCompany(
						finalCompanyId, portalInstance.getPortalInstanceId(),
						portalInstance.getVirtualHost(),
						portalInstance.getDomain(), maxUsers, active,
						admin.getPassword(), admin.getScreenName(),
						admin.getEmailAddress(), admin.getGivenName(),
						admin.getMiddleName(), admin.getFamilyName())));
		}

		return _portalInstanceDTOConverter.toDTO(
			PortalInstances.addCompany(
				portalInstance.getSiteInitializerKey(),
				() -> _companyService.addCompany(
					finalCompanyId, portalInstance.getPortalInstanceId(),
					portalInstance.getVirtualHost(), portalInstance.getDomain(),
					maxUsers, active)));
	}

	private void _validateAdmin(Admin admin) throws Exception {
		if (Validator.isNull(admin.getEmailAddress())) {
			throw new UserEmailAddressException.MustNotBeNull();
		}

		if (Validator.isNull(admin.getFamilyName())) {
			throw new ContactNameException.MustHaveLastName();
		}

		if (Validator.isNull(admin.getGivenName())) {
			throw new ContactNameException.MustHaveFirstName();
		}

		EmailAddressValidator emailAddressValidator =
			EmailAddressValidatorFactory.getInstance();

		if (!emailAddressValidator.validate(0, admin.getEmailAddress())) {
			throw new UserEmailAddressException.MustValidate(
				admin.getEmailAddress(), emailAddressValidator);
		}
	}

	private void _validateSiteInitializerKey(String siteInitializerKey) {
		if (Validator.isNull(siteInitializerKey)) {
			return;
		}

		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(siteInitializerKey);

		if (siteInitializer == null) {
			throw new IllegalArgumentException(
				"Site initializer " + siteInitializerKey + " does not exist");
		}

		if (!siteInitializer.isActive(contextCompany.getCompanyId())) {
			throw new IllegalArgumentException(
				"Site initializer " + siteInitializerKey + " is inactive");
		}
	}

	@Reference
	private CompanyService _companyService;

	@Reference(target = DTOConverterConstants.PORTAL_INSTANCE_DTO_CONVERTER)
	private DTOConverter<Company, PortalInstance> _portalInstanceDTOConverter;

	@Reference
	private SiteInitializerRegistry _siteInitializerRegistry;

}