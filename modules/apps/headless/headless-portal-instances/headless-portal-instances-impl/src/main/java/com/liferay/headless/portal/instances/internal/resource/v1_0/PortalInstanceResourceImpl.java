/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.internal.resource.v1_0;

import com.liferay.headless.portal.instances.dto.v1_0.Admin;
import com.liferay.headless.portal.instances.dto.v1_0.PortalInstance;
import com.liferay.headless.portal.instances.dto.v1_0.PortalInstanceExport;
import com.liferay.headless.portal.instances.resource.v1_0.PortalInstanceResource;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.EmailAddressValidator;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.CompanyService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.auth.EmailAddressValidatorFactory;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.felix.cm.file.ConfigurationHandler;

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
		Company company = _companyService.getCompanyByWebId(portalInstanceId);

		_companyService.deleteCompany(company.getCompanyId());
	}

	@Override
	public PortalInstance getPortalInstance(String portalInstanceId)
		throws Exception {

		return _toPortalInstance(
			_companyService.getCompanyByWebId(portalInstanceId));
	}

	@Override
	public Page<PortalInstance> getPortalInstancesPage(Boolean skipDefault)
		throws Exception {

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
	public PortalInstanceExport postPortalInstanceExport(
			String portalInstanceId)
		throws Exception {

		_checkFeatureFlag();

		_checkPermission();

		Company company = _companyService.getCompanyByWebId(portalInstanceId);

		long companyId = company.getCompanyId();

		try {
			_companyLocalService.exportCompany(companyId);

			_exportConfigurations(companyId);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to export portal instance " + portalInstanceId,
				exception);

			throw exception;
		}

		return new PortalInstanceExport() {
			{
				setExportedPartitionName(() -> "lexported_" + companyId);
				setSourcePartitionName(
					() ->
						PropsValues.DATABASE_PARTITION_SCHEMA_NAME_PREFIX +
							companyId);
			}
		};
	}

	@Override
	public void putPortalInstanceActivate(String portalInstanceId)
		throws Exception {

		Company company = _companyService.getCompanyByWebId(portalInstanceId);

		_companyService.updateCompany(
			company.getCompanyId(), company.getVirtualHostname(),
			company.getMx(), company.getMaxUsers(), true);
	}

	@Override
	public void putPortalInstanceDeactivate(String portalInstanceId)
		throws Exception {

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

	private void _exportConfigurations(long companyId) throws Exception {
		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			return;
		}

		List<ScopedConfiguration> scopedConfigurations = new ArrayList<>();

		Map<String, String> configurations = DBPartitionUtil.getConfigurations(
			CompanyConstants.SYSTEM);

		for (Map.Entry<String, String> entry : configurations.entrySet()) {
			ScopedConfiguration scopedConfiguration = _getScopedConfiguration(
				entry.getKey(), entry.getValue());

			if (scopedConfiguration == null) {
				continue;
			}

			if (Objects.equals(
					scopedConfiguration.getScope(),
					ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE)) {

				scopedConfigurations.add(scopedConfiguration);

				continue;
			}

			if (_isApplicable(companyId, scopedConfiguration)) {
				scopedConfigurations.add(scopedConfiguration);
			}
		}

		for (ScopedConfiguration scopedConfiguration : scopedConfigurations) {
			DBPartitionUtil.exportConfiguration(
				companyId, scopedConfiguration.getConfigurationId(),
				scopedConfiguration.getEncodedDictionary());
		}
	}

	private ScopedConfiguration _getScopedConfiguration(
			String configurationId, String encodedDictionary)
		throws Exception {

		if (Validator.isNull(encodedDictionary)) {
			return null;
		}

		Dictionary<String, String> dictionary = ConfigurationHandler.read(
			new UnsyncByteArrayInputStream(
				encodedDictionary.getBytes(StringPool.UTF8)));

		Object value = dictionary.get(
			ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey());

		if (value != null) {
			return new ScopedConfiguration(
				configurationId, encodedDictionary,
				ExtendedObjectClassDefinition.Scope.GROUP,
				GetterUtil.getLong(value));
		}

		value = dictionary.get(
			ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey());

		if (value != null) {
			return new ScopedConfiguration(
				configurationId, encodedDictionary,
				ExtendedObjectClassDefinition.Scope.COMPANY,
				GetterUtil.getLong(value));
		}

		value = dictionary.get(
			ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.
				getPropertyKey());

		if (value != null) {
			return new ScopedConfiguration(
				configurationId, encodedDictionary,
				ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE,
				GetterUtil.getString(value));
		}

		return null;
	}

	private boolean _isApplicable(
			long companyId, ScopedConfiguration scopedConfiguration)
		throws Exception {

		if (Objects.equals(
				scopedConfiguration.getScope(),
				ExtendedObjectClassDefinition.Scope.COMPANY)) {

			if (companyId == (long)scopedConfiguration.getScopePK()) {
				return true;
			}

			return false;
		}

		if (Objects.equals(
				scopedConfiguration.getScope(),
				ExtendedObjectClassDefinition.Scope.GROUP)) {

			long groupId = (long)scopedConfiguration.getScopePK();

			Group group = _groupLocalService.fetchGroup(groupId);

			if (group == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to export configuration ",
							scopedConfiguration.getConfigurationId(),
							" because group ", groupId, " does not exist"));
				}

				return false;
			}

			if (group.getCompanyId() == companyId) {
				return true;
			}

			return false;
		}

		return true;
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
	private CompanyLocalService _companyLocalService;

	@Reference
	private CompanyService _companyService;

	@Reference
	private GroupLocalService _groupLocalService;

	private static class ScopedConfiguration {

		public ScopedConfiguration(
			String configurationId, String encodedDictionary,
			ExtendedObjectClassDefinition.Scope scope, Object scopePK) {

			_configurationId = configurationId;
			_encodedDictionary = encodedDictionary;
			_scope = scope;
			_scopePK = scopePK;
		}

		public String getConfigurationId() {
			return _configurationId;
		}

		public String getEncodedDictionary() {
			return _encodedDictionary;
		}

		public ExtendedObjectClassDefinition.Scope getScope() {
			return _scope;
		}

		public Object getScopePK() {
			return _scopePK;
		}

		private final String _configurationId;
		private final String _encodedDictionary;
		private final ExtendedObjectClassDefinition.Scope _scope;
		private final Object _scopePK;

	}

}