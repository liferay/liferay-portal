/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.configuration.internal.resource.v1_0;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.exportimport.ConfigurationExportImportProcessor;
import com.liferay.configuration.admin.util.ConfigurationFilterStringUtil;
import com.liferay.headless.admin.configuration.dto.v1_0.SiteConfiguration;
import com.liferay.headless.admin.configuration.internal.util.ConfigurationScreenUtil;
import com.liferay.headless.admin.configuration.internal.util.ConfigurationUtil;
import com.liferay.headless.admin.configuration.resource.v1_0.SiteConfigurationResource;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.settings.SettingsLocatorHelper;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
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
	properties = "OSGI-INF/liferay/rest/v1_0/site-configuration.properties",
	scope = ServiceScope.PROTOTYPE, service = SiteConfigurationResource.class
)
public class SiteConfigurationResourceImpl
	extends BaseSiteConfigurationResourceImpl {

	@Override
	public SiteConfiguration getSiteSiteConfiguration(
			String siteExternalReferenceCode,
			String siteConfigurationExternalReferenceCode)
		throws Exception {

		_checkFeatureFlag();

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			siteExternalReferenceCode, contextCompany.getCompanyId());

		_checkPermission(group.getGroupId());

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			siteConfigurationExternalReferenceCode);

		if (configurationScreen != null) {
			return _getConfigurationScreenSiteConfiguration(
				configurationScreen, group.getGroupId());
		}

		return _getSiteSiteConfiguration(
			group.getGroupId(), siteConfigurationExternalReferenceCode,
			siteExternalReferenceCode);
	}

	@Override
	public Page<SiteConfiguration> getSiteSiteConfigurationsPage(
			String siteExternalReferenceCode, Pagination pagination)
		throws Exception {

		_checkFeatureFlag();

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			siteExternalReferenceCode, contextCompany.getCompanyId());

		_checkPermission(group.getGroupId());

		List<SiteConfiguration> siteConfigurations = new ArrayList<>();

		_addSiteConfigurations(
			group.getGroupId(), siteConfigurations, siteExternalReferenceCode);

		_addConfigurationScreenSiteConfigurations(
			group.getGroupId(), siteConfigurations);

		return Page.of(
			HashMapBuilder.put(
				"createBatch",
				addAction(
					ActionKeys.UPDATE, "postSiteSiteConfigurationBatch",
					Group.class.getName(), group.getGroupId())
			).build(),
			ListUtil.subList(
				siteConfigurations, pagination.getStartPosition(),
				pagination.getEndPosition()),
			pagination, siteConfigurations.size());
	}

	@Override
	public SiteConfiguration postSiteSiteConfiguration(
			String siteExternalReferenceCode,
			SiteConfiguration siteConfiguration)
		throws Exception {

		return putSiteSiteConfiguration(
			siteExternalReferenceCode,
			siteConfiguration.getExternalReferenceCode(), siteConfiguration);
	}

	@Override
	public SiteConfiguration putSiteSiteConfiguration(
			String siteExternalReferenceCode,
			String siteConfigurationExternalReferenceCode,
			SiteConfiguration siteConfiguration)
		throws Exception {

		_checkFeatureFlag();

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			siteExternalReferenceCode, contextCompany.getCompanyId());

		long groupId = group.getGroupId();

		_checkPermission(groupId);

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			siteConfigurationExternalReferenceCode);

		if (configurationScreen != null) {
			return _putConfigurationScreenSiteConfiguration(
				siteConfiguration, configurationScreen, groupId);
		}

		siteConfiguration.setExternalReferenceCode(
			() -> siteConfigurationExternalReferenceCode);

		return _putSiteConfiguration(
			groupId, siteConfiguration, siteExternalReferenceCode);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ConfigurationScreenUtil.createServiceTracker(
			bundleContext, ExtendedObjectClassDefinition.Scope.GROUP);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private void _addConfigurationScreenSiteConfigurations(
		long groupId, List<SiteConfiguration> siteConfigurations) {

		for (ConfigurationScreen configurationScreen :
				_serviceTrackerMap.values()) {

			try {
				SiteConfiguration siteConfiguration = _toSiteConfiguration(
					configurationScreen, groupId);

				Map<String, Object> properties =
					siteConfiguration.getProperties();

				if (properties.isEmpty()) {
					continue;
				}

				siteConfigurations.add(siteConfiguration);
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

	private void _addSiteConfigurations(
			long groupId, List<SiteConfiguration> siteConfigurations,
			String siteExternalReferenceCode)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			ConfigurationFilterStringUtil.getGroupScopedFilterString(
				contextCompany.getCompanyId(), groupId,
				siteExternalReferenceCode));

		if (ArrayUtil.isEmpty(configurations)) {
			return;
		}

		for (Configuration configuration : configurations) {
			SiteConfiguration siteConfiguration = _toSiteConfiguration(
				configuration);

			if (siteConfiguration == null) {
				continue;
			}

			siteConfigurations.add(siteConfiguration);
		}
	}

	private void _checkFeatureFlag() {
		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-65399")) {

			throw new UnsupportedOperationException();
		}
	}

	private void _checkPermission(long groupId) throws Exception {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (!permissionChecker.isCompanyAdmin() &&
			!permissionChecker.isGroupAdmin(groupId) &&
			!permissionChecker.isOmniadmin()) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, Group.class.getName(), groupId,
				ActionKeys.UPDATE);
		}
	}

	private SiteConfiguration _getConfigurationScreenSiteConfiguration(
		ConfigurationScreen configurationScreen, long groupId) {

		try {
			SiteConfiguration siteConfiguration = _toSiteConfiguration(
				configurationScreen, groupId);

			Map<String, Object> properties = siteConfiguration.getProperties();

			if (properties.isEmpty()) {
				throw new NotFoundException(
					StringBundler.concat(
						"Unable to find entry for site configuration with ",
						"external reference code: ",
						configurationScreen.getKey()));
			}

			return siteConfiguration;
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			throw new ServerErrorException(
				unsupportedOperationException.getMessage(),
				Response.Status.NOT_IMPLEMENTED);
		}
	}

	private SiteConfiguration _getSiteSiteConfiguration(
			long groupId, String siteConfigurationExternalReferenceCode,
			String siteExternalReferenceCode)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			ConfigurationFilterStringUtil.getGroupScopedFilterString(
				contextCompany.getCompanyId(), groupId,
				siteConfigurationExternalReferenceCode,
				siteExternalReferenceCode));

		if (ArrayUtil.isEmpty(configurations)) {
			throw new NotFoundException(
				StringBundler.concat(
					"Unable to find entry for site configuration with ",
					"external reference code: ",
					siteConfigurationExternalReferenceCode));
		}

		if (configurations.length > 1) {
			List<String> pids = new ArrayList<>();

			for (Configuration configuration : configurations) {
				pids.add(configuration.getPid());
			}

			throw new BadRequestException(
				StringBundler.concat(
					siteConfigurationExternalReferenceCode,
					" is a factory configuration. Specify one of these PIDs: ",
					ListUtil.toString(pids, StringPool.BLANK, StringPool.COMMA),
					"."));
		}

		return _toSiteConfiguration(configurations[0]);
	}

	private SiteConfiguration _putConfigurationScreenSiteConfiguration(
		SiteConfiguration siteConfiguration,
		ConfigurationScreen configurationScreen, long groupId) {

		try {
			ConfigurationScreenUtil.importProperties(
				_configurationExportImportProcessor, configurationScreen,
				HashMapDictionaryBuilder.putAll(
					siteConfiguration.getProperties()
				).build(),
				ExtendedObjectClassDefinition.Scope.GROUP, groupId);

			return _toSiteConfiguration(configurationScreen, groupId);
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

	private SiteConfiguration _putSiteConfiguration(
			long groupId, SiteConfiguration siteConfiguration,
			String siteExternalReferenceCode)
		throws Exception {

		String filterString =
			ConfigurationFilterStringUtil.getGroupScopedFilterString(
				contextCompany.getCompanyId(), groupId,
				siteConfiguration.getExternalReferenceCode(),
				siteExternalReferenceCode);

		try {
			Configuration configuration =
				ConfigurationUtil.addOrUpdateConfiguration(
					groupId, _configurationAdmin,
					siteConfiguration.getExternalReferenceCode(), filterString,
					siteConfiguration.getProperties(),
					ExtendedObjectClassDefinition.Scope.GROUP,
					_settingsLocatorHelper);

			if (configuration == null) {
				throw new NotFoundException(
					StringBundler.concat(
						"Unable to find site configuration with external ",
						"reference code: ",
						siteConfiguration.getExternalReferenceCode()));
			}

			return _toSiteConfiguration(configuration);
		}
		catch (ValidationException validationException) {
			throw new BadRequestException(validationException.getMessage());
		}
	}

	private SiteConfiguration _toSiteConfiguration(Configuration configuration)
		throws Exception {

		Map<String, Object> properties = ConfigurationUtil.getProperties(
			configuration, _configurationExportImportProcessor,
			ExtendedObjectClassDefinition.Scope.GROUP, _settingsLocatorHelper);

		if (properties.isEmpty()) {
			return null;
		}

		SiteConfiguration siteConfiguration = new SiteConfiguration();

		siteConfiguration.setExternalReferenceCode(configuration::getPid);
		siteConfiguration.setProperties(() -> properties);

		return siteConfiguration;
	}

	private SiteConfiguration _toSiteConfiguration(
		ConfigurationScreen configurationScreen, long groupId) {

		SiteConfiguration siteConfiguration = new SiteConfiguration();

		siteConfiguration.setExternalReferenceCode(configurationScreen::getKey);
		siteConfiguration.setProperties(
			() -> ConfigurationScreenUtil.getProperties(
				_configurationExportImportProcessor, configurationScreen,
				ExtendedObjectClassDefinition.Scope.GROUP, groupId));

		return siteConfiguration;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SiteConfigurationResourceImpl.class);

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationExportImportProcessor
		_configurationExportImportProcessor;

	@Reference
	private GroupLocalService _groupLocalService;

	private ServiceTrackerMap<String, ConfigurationScreen> _serviceTrackerMap;

	@Reference
	private SettingsLocatorHelper _settingsLocatorHelper;

}