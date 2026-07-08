/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.configuration.internal.resource.v1_0;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.exportimport.ConfigurationExportImportProcessor;
import com.liferay.configuration.admin.util.ConfigurationFilterStringUtil;
import com.liferay.headless.admin.configuration.dto.v1_0.SystemConfiguration;
import com.liferay.headless.admin.configuration.internal.util.ConfigurationScreenUtil;
import com.liferay.headless.admin.configuration.internal.util.ConfigurationUtil;
import com.liferay.headless.admin.configuration.resource.v1_0.SystemConfigurationResource;
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
import com.liferay.portal.kernel.util.Portal;
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
	properties = "OSGI-INF/liferay/rest/v1_0/system-configuration.properties",
	scope = ServiceScope.PROTOTYPE, service = SystemConfigurationResource.class
)
public class SystemConfigurationResourceImpl
	extends BaseSystemConfigurationResourceImpl {

	@Override
	public SystemConfiguration getSystemConfiguration(
			String systemConfigurationExternalReferenceCode)
		throws Exception {

		_checkFeatureFlag();

		_checkPermission();

		_validateDefaultCompany();

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			systemConfigurationExternalReferenceCode);

		if (configurationScreen != null) {
			return _getConfigurationScreenSystemConfiguration(
				configurationScreen);
		}

		return _getSystemConfiguration(
			systemConfigurationExternalReferenceCode);
	}

	@Override
	public Page<SystemConfiguration> getSystemConfigurationsPage(
			Pagination pagination)
		throws Exception {

		_checkFeatureFlag();

		_checkPermission();

		_validateDefaultCompany();

		List<SystemConfiguration> systemConfigurations = new ArrayList<>();

		_addSystemConfigurations(systemConfigurations);

		_addConfigurationScreenSystemConfigurations(systemConfigurations);

		return Page.of(
			ListUtil.subList(
				systemConfigurations, pagination.getStartPosition(),
				pagination.getEndPosition()),
			pagination, systemConfigurations.size());
	}

	@Override
	public SystemConfiguration postSystemConfiguration(
			SystemConfiguration systemConfiguration)
		throws Exception {

		return putSystemConfiguration(
			systemConfiguration.getExternalReferenceCode(),
			systemConfiguration);
	}

	@Override
	public SystemConfiguration putSystemConfiguration(
			String systemConfigurationExternalReferenceCode,
			SystemConfiguration systemConfiguration)
		throws Exception {

		_checkFeatureFlag();

		_checkPermission();

		_validateDefaultCompany();

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			systemConfigurationExternalReferenceCode);

		if (configurationScreen != null) {
			return _putConfigurationScreenSystemConfiguration(
				configurationScreen, systemConfiguration);
		}

		systemConfiguration.setExternalReferenceCode(
			() -> systemConfigurationExternalReferenceCode);

		return _putSystemConfiguration(systemConfiguration);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ConfigurationScreenUtil.createServiceTracker(
			bundleContext, ExtendedObjectClassDefinition.Scope.SYSTEM);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private void _addConfigurationScreenSystemConfigurations(
		List<SystemConfiguration> systemConfigurations) {

		for (ConfigurationScreen configurationScreen :
				_serviceTrackerMap.values()) {

			try {
				SystemConfiguration systemConfiguration =
					_toSystemConfiguration(configurationScreen);

				Map<String, Object> properties =
					systemConfiguration.getProperties();

				if (properties.isEmpty()) {
					continue;
				}

				systemConfigurations.add(systemConfiguration);
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

	private void _addSystemConfigurations(
			List<SystemConfiguration> systemConfigurations)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			ConfigurationFilterStringUtil.getSystemScopedFilterString());

		if (ArrayUtil.isEmpty(configurations)) {
			return;
		}

		for (Configuration configuration : configurations) {
			SystemConfiguration systemConfiguration = _toSystemConfiguration(
				configuration);

			if (systemConfiguration == null) {
				continue;
			}

			systemConfigurations.add(systemConfiguration);
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

		if (!permissionChecker.isOmniadmin()) {
			throw new PrincipalException.MustBeOmniadmin(permissionChecker);
		}
	}

	private SystemConfiguration _getConfigurationScreenSystemConfiguration(
		ConfigurationScreen configurationScreen) {

		try {
			SystemConfiguration systemConfiguration = _toSystemConfiguration(
				configurationScreen);

			Map<String, Object> properties =
				systemConfiguration.getProperties();

			if (properties.isEmpty()) {
				throw new NotFoundException(
					StringBundler.concat(
						"Unable to find entry for system configuration with ",
						"external reference code: ",
						configurationScreen.getKey()));
			}

			return systemConfiguration;
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			throw new ServerErrorException(
				unsupportedOperationException.getMessage(),
				Response.Status.NOT_IMPLEMENTED);
		}
	}

	private SystemConfiguration _getSystemConfiguration(
			String systemConfigurationExternalReferenceCode)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			ConfigurationFilterStringUtil.getSystemScopedFilterString(
				systemConfigurationExternalReferenceCode));

		if (ArrayUtil.isEmpty(configurations)) {
			throw new NotFoundException(
				StringBundler.concat(
					"Unable to find entry for system configuration with ",
					"external reference code: ",
					systemConfigurationExternalReferenceCode));
		}

		if (configurations.length > 1) {
			List<String> pids = new ArrayList<>();

			for (Configuration configuration : configurations) {
				pids.add(configuration.getPid());
			}

			throw new BadRequestException(
				StringBundler.concat(
					systemConfigurationExternalReferenceCode,
					" is a factory configuration. Specify one of these PIDs: ",
					ListUtil.toString(pids, StringPool.BLANK, StringPool.COMMA),
					"."));
		}

		return _toSystemConfiguration(configurations[0]);
	}

	private SystemConfiguration _putConfigurationScreenSystemConfiguration(
		ConfigurationScreen configurationScreen,
		SystemConfiguration systemConfiguration) {

		try {
			ConfigurationScreenUtil.importProperties(
				_configurationExportImportProcessor, configurationScreen,
				HashMapDictionaryBuilder.putAll(
					systemConfiguration.getProperties()
				).build(),
				ExtendedObjectClassDefinition.Scope.SYSTEM, null);

			return _toSystemConfiguration(configurationScreen);
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

	private SystemConfiguration _putSystemConfiguration(
			SystemConfiguration systemConfiguration)
		throws Exception {

		String filterString =
			ConfigurationFilterStringUtil.getSystemScopedFilterString(
				systemConfiguration.getExternalReferenceCode());

		try {
			Configuration configuration =
				ConfigurationUtil.addOrUpdateConfiguration(
					null, _configurationAdmin,
					systemConfiguration.getExternalReferenceCode(),
					filterString, systemConfiguration.getProperties(),
					ExtendedObjectClassDefinition.Scope.SYSTEM,
					_settingsLocatorHelper);

			if (configuration == null) {
				throw new NotFoundException(
					StringBundler.concat(
						"Unable to find system configuration with external ",
						"reference code: ",
						systemConfiguration.getExternalReferenceCode()));
			}

			return _toSystemConfiguration(configuration);
		}
		catch (ValidationException validationException) {
			throw new BadRequestException(validationException.getMessage());
		}
	}

	private SystemConfiguration _toSystemConfiguration(
			Configuration configuration)
		throws Exception {

		Map<String, Object> properties = ConfigurationUtil.getProperties(
			configuration, _configurationExportImportProcessor,
			ExtendedObjectClassDefinition.Scope.SYSTEM, _settingsLocatorHelper);

		if (properties.isEmpty()) {
			return null;
		}

		SystemConfiguration systemConfiguration = new SystemConfiguration();

		systemConfiguration.setExternalReferenceCode(configuration::getPid);
		systemConfiguration.setProperties(() -> properties);

		return systemConfiguration;
	}

	private SystemConfiguration _toSystemConfiguration(
		ConfigurationScreen configurationScreen) {

		SystemConfiguration systemConfiguration = new SystemConfiguration();

		systemConfiguration.setExternalReferenceCode(
			configurationScreen::getKey);
		systemConfiguration.setProperties(
			() -> ConfigurationScreenUtil.getProperties(
				_configurationExportImportProcessor, configurationScreen,
				ExtendedObjectClassDefinition.Scope.SYSTEM, null));

		return systemConfiguration;
	}

	private void _validateDefaultCompany() {
		if (contextCompany.getCompanyId() != _portal.getDefaultCompanyId()) {
			throw new BadRequestException(
				"You must be authenticated into the default company to " +
					"manage system configurations");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SystemConfigurationResourceImpl.class);

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationExportImportProcessor
		_configurationExportImportProcessor;

	@Reference
	private Portal _portal;

	private ServiceTrackerMap<String, ConfigurationScreen> _serviceTrackerMap;

	@Reference
	private SettingsLocatorHelper _settingsLocatorHelper;

}