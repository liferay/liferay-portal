/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.configuration.internal.resource.v1_0;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.exportimport.ConfigurationExportImportProcessor;
import com.liferay.configuration.admin.util.ConfigurationFilterStringUtil;
import com.liferay.headless.admin.configuration.dto.v1_0.InstanceConfiguration;
import com.liferay.headless.admin.configuration.internal.util.ConfigurationScreenUtil;
import com.liferay.headless.admin.configuration.internal.util.ConfigurationUtil;
import com.liferay.headless.admin.configuration.resource.v1_0.InstanceConfigurationResource;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.settings.SettingsLocatorHelper;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.validation.ValidationException;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Thiago Buarque
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/instance-configuration.properties",
	scope = ServiceScope.PROTOTYPE,
	service = InstanceConfigurationResource.class
)
public class InstanceConfigurationResourceImpl
	extends BaseInstanceConfigurationResourceImpl {

	@Override
	public InstanceConfiguration getInstanceConfiguration(
			String instanceConfigurationExternalReferenceCode)
		throws Exception {

		_checkFeatureFlag();

		_checkPermission();

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			instanceConfigurationExternalReferenceCode);

		if (configurationScreen != null) {
			return _getConfigurationScreenInstanceConfiguration(
				configurationScreen);
		}

		return _getInstanceConfiguration(
			instanceConfigurationExternalReferenceCode);
	}

	@Override
	public Page<InstanceConfiguration> getInstanceConfigurationsPage(
			Pagination pagination)
		throws Exception {

		_checkFeatureFlag();

		_checkPermission();

		List<InstanceConfiguration> instanceConfigurations = new ArrayList<>();

		_addInstanceConfigurations(instanceConfigurations);

		_addConfigurationScreenInstanceConfigurations(instanceConfigurations);

		return Page.of(
			ListUtil.subList(
				instanceConfigurations, pagination.getStartPosition(),
				pagination.getEndPosition()),
			pagination, instanceConfigurations.size());
	}

	@Override
	public InstanceConfiguration postInstanceConfiguration(
			InstanceConfiguration instanceConfiguration)
		throws Exception {

		return putInstanceConfiguration(
			instanceConfiguration.getExternalReferenceCode(),
			instanceConfiguration);
	}

	@Override
	public InstanceConfiguration putInstanceConfiguration(
			String instanceConfigurationExternalReferenceCode,
			InstanceConfiguration instanceConfiguration)
		throws Exception {

		_checkFeatureFlag();

		_checkPermission();

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			instanceConfigurationExternalReferenceCode);

		if (configurationScreen != null) {
			return _putConfigurationScreenInstanceConfiguration(
				configurationScreen, instanceConfiguration);
		}

		instanceConfiguration.setExternalReferenceCode(
			() -> instanceConfigurationExternalReferenceCode);

		return _putInstanceConfiguration(instanceConfiguration);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ConfigurationScreenUtil.createServiceTracker(
			bundleContext, ExtendedObjectClassDefinition.Scope.COMPANY);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private void _addConfigurationScreenInstanceConfigurations(
		List<InstanceConfiguration> instanceConfigurations) {

		for (ConfigurationScreen configurationScreen :
				_serviceTrackerMap.values()) {

			try {
				InstanceConfiguration instanceConfiguration =
					_toInstanceConfiguration(configurationScreen);

				Map<String, Object> properties =
					instanceConfiguration.getProperties();

				if (properties.isEmpty()) {
					continue;
				}

				instanceConfigurations.add(instanceConfiguration);
			}
			catch (UnsupportedOperationException
						unsupportedOperationException) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Skipping configuration ",
							configurationScreen.getKey(),
							" because it does not have export capability"),
						unsupportedOperationException);
				}
			}
		}
	}

	private void _addInstanceConfigurations(
			List<InstanceConfiguration> instanceConfigurations)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			ConfigurationFilterStringUtil.getCompanyScopedFilterString(
				contextCompany.getCompanyId(),
				contextCompany.getDefaultWebId()));

		if (ArrayUtil.isEmpty(configurations)) {
			return;
		}

		for (Configuration configuration : configurations) {
			InstanceConfiguration instanceConfiguration =
				_toInstanceConfiguration(configuration);

			if (instanceConfiguration == null) {
				continue;
			}

			instanceConfigurations.add(instanceConfiguration);
		}
	}

	private void _checkFeatureFlag() {
		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-65399")) {

			throw new UnsupportedOperationException();
		}
	}

	private void _checkPermission() throws Exception {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (!permissionChecker.isCompanyAdmin() &&
			!permissionChecker.isOmniadmin()) {

			throw new PrincipalException.MustBeCompanyAdmin(permissionChecker);
		}
	}

	private InstanceConfiguration _getConfigurationScreenInstanceConfiguration(
		ConfigurationScreen configurationScreen) {

		try {
			InstanceConfiguration instanceConfiguration =
				_toInstanceConfiguration(configurationScreen);

			Map<String, Object> properties =
				instanceConfiguration.getProperties();

			if (properties.isEmpty()) {
				throw new NotFoundException(
					StringBundler.concat(
						"Unable to find entry for instance configuration with ",
						"external reference code: ",
						configurationScreen.getKey()));
			}

			return instanceConfiguration;
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			throw new ServerErrorException(
				unsupportedOperationException.getMessage(),
				Response.Status.NOT_IMPLEMENTED);
		}
	}

	private InstanceConfiguration _getInstanceConfiguration(
			String instanceConfigurationExternalReferenceCode)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			ConfigurationFilterStringUtil.getCompanyScopedFilterString(
				contextCompany.getCompanyId(),
				instanceConfigurationExternalReferenceCode,
				contextCompany.getDefaultWebId()));

		if (ArrayUtil.isEmpty(configurations)) {
			throw new NotFoundException(
				StringBundler.concat(
					"Unable to find entry for instance configuration with ",
					"external reference code: ",
					instanceConfigurationExternalReferenceCode));
		}

		if (configurations.length > 1) {
			List<String> pids = new ArrayList<>();

			for (Configuration configuration : configurations) {
				pids.add(configuration.getPid());
			}

			throw new BadRequestException(
				StringBundler.concat(
					instanceConfigurationExternalReferenceCode,
					" is a factory configuration. Specify one of these PIDs: ",
					ListUtil.toString(pids, StringPool.BLANK, StringPool.COMMA),
					"."));
		}

		return _toInstanceConfiguration(configurations[0]);
	}

	private InstanceConfiguration _putConfigurationScreenInstanceConfiguration(
		ConfigurationScreen configurationScreen,
		InstanceConfiguration instanceConfiguration) {

		try {
			ConfigurationScreenUtil.importProperties(
				_configurationExportImportProcessor, configurationScreen,
				HashMapDictionaryBuilder.putAll(
					instanceConfiguration.getProperties()
				).build(),
				ExtendedObjectClassDefinition.Scope.COMPANY,
				contextCompany.getCompanyId());

			return _toInstanceConfiguration(configurationScreen);
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			throw new ServerErrorException(
				unsupportedOperationException.getMessage(),
				Response.Status.NOT_IMPLEMENTED);
		}
		catch (Exception exception) {
			throw new BadRequestException(exception.getMessage());
		}
	}

	private InstanceConfiguration _putInstanceConfiguration(
			InstanceConfiguration instanceConfiguration)
		throws Exception {

		long companyId = contextCompany.getCompanyId();

		String filterString =
			ConfigurationFilterStringUtil.getCompanyScopedFilterString(
				companyId, instanceConfiguration.getExternalReferenceCode(),
				contextCompany.getDefaultWebId());

		try {
			Configuration configuration =
				ConfigurationUtil.addOrUpdateConfiguration(
					companyId, _configurationAdmin,
					instanceConfiguration.getExternalReferenceCode(),
					filterString, instanceConfiguration.getProperties(),
					ExtendedObjectClassDefinition.Scope.COMPANY,
					_settingsLocatorHelper);

			if (configuration == null) {
				throw new NotFoundException(
					StringBundler.concat(
						"Unable to find instance configuration with external ",
						"reference code: ",
						instanceConfiguration.getExternalReferenceCode()));
			}

			return _toInstanceConfiguration(configuration);
		}
		catch (ValidationException validationException) {
			throw new BadRequestException(validationException.getMessage());
		}
	}

	private InstanceConfiguration _toInstanceConfiguration(
			Configuration configuration)
		throws Exception {

		Map<String, Object> properties = ConfigurationUtil.getProperties(
			configuration, _configurationExportImportProcessor,
			ExtendedObjectClassDefinition.Scope.COMPANY,
			_settingsLocatorHelper);

		if (properties.isEmpty()) {
			return null;
		}

		InstanceConfiguration instanceConfiguration =
			new InstanceConfiguration();

		instanceConfiguration.setExternalReferenceCode(configuration::getPid);
		instanceConfiguration.setProperties(() -> properties);

		return instanceConfiguration;
	}

	private InstanceConfiguration _toInstanceConfiguration(
		ConfigurationScreen configurationScreen) {

		InstanceConfiguration instanceConfiguration =
			new InstanceConfiguration();

		instanceConfiguration.setExternalReferenceCode(
			configurationScreen::getKey);
		instanceConfiguration.setProperties(
			() -> ConfigurationScreenUtil.getProperties(
				_configurationExportImportProcessor, configurationScreen,
				ExtendedObjectClassDefinition.Scope.COMPANY,
				contextCompany.getCompanyId()));

		return instanceConfiguration;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InstanceConfigurationResourceImpl.class);

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationExportImportProcessor
		_configurationExportImportProcessor;

	private ServiceTrackerMap<String, ConfigurationScreen> _serviceTrackerMap;

	@Reference
	private SettingsLocatorHelper _settingsLocatorHelper;

}