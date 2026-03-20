/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.configuration.provider;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.util.ConfigurationFilterStringUtil;
import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration;
import com.liferay.cookies.configuration.banner.CookiesBannerConfiguration;
import com.liferay.cookies.configuration.consent.CookiesConsentConfiguration;
import com.liferay.cookies.internal.configuration.admin.service.CookiesPreferenceHandlingManagedServiceFactory;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Date;
import java.util.Dictionary;
import java.util.function.Function;
import java.util.function.Supplier;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Sanz
 */
@Component(service = CookiesConfigurationProvider.class)
public class CookiesConfigurationProviderImpl
	implements CookiesConfigurationProvider {

	@Override
	public String getCompanyConfigurationURL(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_portal.getUser(httpServletRequest));

		if (!permissionChecker.isCompanyAdmin()) {
			return null;
		}

		String factoryPid =
			CookiesPreferenceHandlingConfiguration.class.getName();

		String pid = factoryPid;

		Configuration configuration =
			_getCookiesPreferenceHandlingCompanyConfiguration(
				_portal.getCompanyId(httpServletRequest));

		if (configuration != null) {
			pid = configuration.getPid();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest,
				ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/configuration_admin/edit_configuration"
		).setRedirect(
			ParamUtil.getString(
				httpServletRequest, "backURL",
				_portal.getCurrentCompleteURL(httpServletRequest))
		).setParameter(
			"factoryPid", factoryPid
		).setParameter(
			"pid", pid
		).buildString();
	}

	@Override
	public CookiesBannerConfiguration getCookiesBannerConfiguration(
			ThemeDisplay themeDisplay)
		throws Exception {

		return _getCookiesConfiguration(
			CookiesBannerConfiguration.class, themeDisplay);
	}

	@Override
	public CookiesConsentConfiguration getCookiesConsentConfiguration(
			ThemeDisplay themeDisplay)
		throws Exception {

		return _getCookiesConfiguration(
			CookiesConsentConfiguration.class, themeDisplay);
	}

	@Override
	public CookiesPreferenceHandlingConfiguration
			getCookiesPreferenceHandlingConfiguration(ThemeDisplay themeDisplay)
		throws Exception {

		return _getCookiesConfiguration(
			CookiesPreferenceHandlingConfiguration.class, themeDisplay);
	}

	@Override
	public int getCookiesPreferenceHandlingConsentRenewalPeriod(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		return _getScopeConfigurationAttribute(
			scope, scopePK,
			this::_getCompanyCookiesPreferenceHandlingConsentRenewalPeriod,
			this::_getGroupCookiesPreferenceHandlingConsentRenewalPeriod,
			this::_getSystemCookiesPreferenceHandlingConsentRenewalPeriod);
	}

	public long getCookiesPreferenceHandlingCustomFloatingIconImageId(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		return _getScopeConfigurationAttribute(
			scope, scopePK,
			this::_getCompanyCookiesPreferenceHandlingCustomFloatingIconImageId,
			this::_getGroupCookiesPreferenceHandlingCustomFloatingIconImageId,
			this::_getSystemCookiesPreferenceHandlingCustomFloatingIconImageId);
	}

	@Override
	public int getCookiesPreferenceHandlingDissentRenewalPeriod(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		return _getScopeConfigurationAttribute(
			scope, scopePK,
			this::_getCompanyCookiesPreferenceHandlingDissentRenewalPeriod,
			this::_getGroupCookiesPreferenceHandlingDissentRenewalPeriod,
			this::_getSystemCookiesPreferenceHandlingDissentRenewalPeriod);
	}

	@Override
	public String getCookiesPreferenceHandlingFloatingIcon(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		return _getScopeConfigurationAttribute(
			scope, scopePK, this::_getCompanyFloatingIcon,
			this::_getGroupFloatingIcon, this::_getSystemFloatingIcon);
	}

	@Override
	public String getGroupConfigurationURL(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_portal.getUser(httpServletRequest));

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!permissionChecker.isGroupAdmin(themeDisplay.getScopeGroupId())) {
			return null;
		}

		String factoryPid =
			CookiesPreferenceHandlingConfiguration.class.getName();

		String pid = factoryPid;

		Configuration configuration =
			_getCookiesPreferenceHandlingGroupConfiguration(
				themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId());

		if (configuration != null) {
			pid = configuration.getPid();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, ConfigurationAdminPortletKeys.SITE_SETTINGS,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/configuration_admin/edit_configuration"
		).setRedirect(
			ParamUtil.getString(
				httpServletRequest, "backURL",
				_portal.getCurrentCompleteURL(httpServletRequest))
		).setParameter(
			"factoryPid", factoryPid
		).setParameter(
			"pid", pid
		).buildString();
	}

	@Override
	public String getSystemConfigurationURL(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_portal.getUser(httpServletRequest));

		if (!permissionChecker.isOmniadmin()) {
			return null;
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest,
				ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/configuration_admin/edit_configuration"
		).setRedirect(
			_portal.getCurrentCompleteURL(httpServletRequest)
		).setParameter(
			"factoryPid", CookiesPreferenceHandlingConfiguration.class.getName()
		).buildString();
	}

	@Override
	public boolean isCookiesPreferenceHandlingConfigurationDefined(
			ExtendedObjectClassDefinition.Scope scope, long scopePK)
		throws Exception {

		if (scope == ExtendedObjectClassDefinition.Scope.SYSTEM) {
			try {
				CookiesPreferenceHandlingConfiguration
					cookiesPreferenceHandlingConfiguration =
						_configurationProvider.getSystemConfiguration(
							CookiesPreferenceHandlingConfiguration.class);

				if (cookiesPreferenceHandlingConfiguration != null) {
					return true;
				}
			}
			catch (ConfigurationException configurationException) {
				_log.error(configurationException);

				return false;
			}
		}

		if (_getScopedConfiguration(scope, scopePK) == null) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isCookiesPreferenceHandlingEnabled(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		return _getScopeConfigurationAttribute(
			scope, scopePK, this::_isCompanyCookiesPreferenceHandlingEnabled,
			this::_isGroupCookiesPreferenceHandlingEnabled,
			this::_isSystemCookiesPreferenceHandlingEnabled);
	}

	@Override
	public boolean isCookiesPreferenceHandlingExplicitConsentMode(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		return _getScopeConfigurationAttribute(
			scope, scopePK,
			this::_isCompanyCookiesPreferenceHandlingExplicitConsentMode,
			this::_isGroupCookiesPreferenceHandlingExplicitConsentMode,
			this::_isSystemCookiesPreferenceHandlingExplicitConsentMode);
	}

	@Override
	public boolean isCookiesPreferenceHandlingFloatingIconEnabled(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		return _getScopeConfigurationAttribute(
			scope, scopePK, this::_isCompanyFloatingIconEnabled,
			this::_isGroupFloatingIconEnabled,
			this::_isSystemFloatingIconEnabled);
	}

	@Override
	public boolean isCookiesPreferenceHandlingStoreConsent(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		return _getScopeConfigurationAttribute(
			scope, scopePK,
			this::_isCompanyCookiesPreferenceHandlingStoreConsent,
			this::_isGroupCookiesPreferenceHandlingStoreConsent,
			this::_isSystemCookiesPreferenceHandlingStoreConsent);
	}

	@Override
	public void resetCookiesPreferenceHandlingConfiguration(
			ExtendedObjectClassDefinition.Scope scope, long scopePK)
		throws ConfigurationException {

		if (scope == ExtendedObjectClassDefinition.Scope.COMPANY) {
			_configurationProvider.deleteCompanyConfiguration(
				CookiesPreferenceHandlingConfiguration.class, scopePK);
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.GROUP) {
			Group group = _groupLocalService.fetchGroup(scopePK);

			_configurationProvider.deleteGroupConfiguration(
				CookiesPreferenceHandlingConfiguration.class,
				group.getCompanyId(), scopePK);
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.SYSTEM) {
			_configurationProvider.deleteSystemConfiguration(
				CookiesPreferenceHandlingConfiguration.class);
		}
	}

	@Override
	public void updateCookiesPreferenceHandlingConfiguration(
			int consentRenewalPeriod, boolean enabled,
			boolean explicitConsentMode,
			ExtendedObjectClassDefinition.Scope scope, long scopePK,
			boolean storeConsent)
		throws Exception {

		Dictionary<String, Object> dictionary = _createDictionary(
			consentRenewalPeriod, enabled, explicitConsentMode, storeConsent);

		if (scope == ExtendedObjectClassDefinition.Scope.COMPANY) {
			_configurationProvider.saveCompanyConfiguration(
				CookiesPreferenceHandlingConfiguration.class, scopePK,
				dictionary);
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.GROUP) {
			Group group = _groupLocalService.fetchGroup(scopePK);

			_configurationProvider.saveGroupConfiguration(
				CookiesPreferenceHandlingConfiguration.class,
				group.getCompanyId(), scopePK, dictionary);
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.SYSTEM) {
			_configurationProvider.saveSystemConfiguration(
				CookiesPreferenceHandlingConfiguration.class, dictionary);
		}
		else {
			throw new IllegalArgumentException("Unsupported scope: " + scope);
		}
	}

	private HashMapDictionary<String, Object> _createDictionary(
		int consentRenewalPeriod, boolean enabled, boolean explicitConsentMode,
		boolean storeConsent) {

		return HashMapDictionaryBuilder.<String, Object>put(
			"consentRenewalPeriod", consentRenewalPeriod
		).put(
			"enabled", enabled
		).put(
			"explicitConsentMode", explicitConsentMode
		).put(
			"modifiedDate",
			new Date(
			).getTime()
		).put(
			"storeConsent",
			() -> {
				if (FeatureFlagManagerUtil.isEnabled(
						CompanyThreadLocal.getCompanyId(), "LPD-75032")) {

					return storeConsent;
				}

				return null;
			}
		).build();
	}

	private int _getCompanyCookiesPreferenceHandlingConsentRenewalPeriod(
		long companyId) {

		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getCompanyConsentRenewalPeriod(companyId);
	}

	private long _getCompanyCookiesPreferenceHandlingCustomFloatingIconImageId(
		long companyId) {

		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getCompanyCustomFloatingIconImageId(companyId);
	}

	private int _getCompanyCookiesPreferenceHandlingDissentRenewalPeriod(
		long companyId) {

		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getCompanyDissentRenewalPeriod(companyId);
	}

	private String _getCompanyFloatingIcon(long companyId) {
		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getCompanyFloatingIcon(companyId);
	}

	private long _getCompanyId(long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		long companyId = CompanyThreadLocal.getCompanyId();

		if (group != null) {
			companyId = group.getCompanyId();
		}

		return companyId;
	}

	private <T> T _getCookiesConfiguration(
			Class<T> clazz, ThemeDisplay themeDisplay)
		throws Exception {

		LayoutSet layoutSet = _layoutSetLocalService.fetchLayoutSet(
			themeDisplay.getServerName());

		if (layoutSet != null) {
			Group group = layoutSet.getGroup();

			return _configurationProvider.getGroupConfiguration(
				clazz, group.getCompanyId(), group.getGroupId());
		}

		return _configurationProvider.getCompanyConfiguration(
			clazz, themeDisplay.getCompanyId());
	}

	private Configuration _getCookiesPreferenceHandlingCompanyConfiguration(
			long companyId)
		throws ConfigurationException {

		try {
			Configuration[] configuration =
				_configurationAdmin.listConfigurations(
					ConfigurationFilterStringUtil.getCompanyScopedFilterString(
						companyId,
						CookiesPreferenceHandlingConfiguration.class.getName(),
						null));

			if (configuration != null) {
				return configuration[0];
			}

			return null;
		}
		catch (InvalidSyntaxException | IOException exception) {
			throw new ConfigurationException(exception);
		}
	}

	private Configuration _getCookiesPreferenceHandlingGroupConfiguration(
			long companyId, long groupId)
		throws ConfigurationException {

		try {
			Configuration[] configuration =
				_configurationAdmin.listConfigurations(
					ConfigurationFilterStringUtil.getGroupScopedFilterString(
						companyId, groupId,
						CookiesPreferenceHandlingConfiguration.class.getName(),
						null));

			if (configuration != null) {
				return configuration[0];
			}

			return null;
		}
		catch (InvalidSyntaxException | IOException exception) {
			throw new ConfigurationException(exception);
		}
	}

	private int _getGroupCookiesPreferenceHandlingConsentRenewalPeriod(
		long groupId) {

		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getGroupConsentRenewalPeriod(_getCompanyId(groupId), groupId);
	}

	private long _getGroupCookiesPreferenceHandlingCustomFloatingIconImageId(
		long groupId) {

		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getGroupCustomFloatingIconImageId(_getCompanyId(groupId), groupId);
	}

	private int _getGroupCookiesPreferenceHandlingDissentRenewalPeriod(
		long groupId) {

		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getGroupDissentRenewalPeriod(_getCompanyId(groupId), groupId);
	}

	private String _getGroupFloatingIcon(long groupId) {
		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getGroupFloatingIcon(_getCompanyId(groupId), groupId);
	}

	private <T> T _getScopeConfigurationAttribute(
		ExtendedObjectClassDefinition.Scope scope, long scopePK,
		Function<Long, T> companyFunction, Function<Long, T> groupFunction,
		Supplier<T> systemFunction) {

		if (scope == ExtendedObjectClassDefinition.Scope.COMPANY) {
			return companyFunction.apply(scopePK);
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.GROUP) {
			return groupFunction.apply(scopePK);
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.SYSTEM) {
			return systemFunction.get();
		}

		throw new IllegalArgumentException("Unsupported scope: " + scope);
	}

	private Configuration _getScopedConfiguration(
			ExtendedObjectClassDefinition.Scope scope, long scopePK)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			ConfigurationFilterStringUtil.getScopedFilterString(
				CompanyThreadLocal.getCompanyId(),
				CookiesPreferenceHandlingConfiguration.class.getName(), scope,
				scopePK));

		if (configurations == null) {
			return null;
		}

		return configurations[0];
	}

	private int _getSystemCookiesPreferenceHandlingConsentRenewalPeriod() {
		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getSystemConsentRenewalPeriod();
	}

	private long
		_getSystemCookiesPreferenceHandlingCustomFloatingIconImageId() {

		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getSystemCustomFloatingIconImageId();
	}

	private int _getSystemCookiesPreferenceHandlingDissentRenewalPeriod() {
		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getSystemDissentRenewalPeriod();
	}

	private String _getSystemFloatingIcon() {
		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getSystemFloatingIcon();
	}

	private boolean _isCompanyCookiesPreferenceHandlingEnabled(long companyId) {
		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getCompanyEnabled(companyId);
	}

	private boolean _isCompanyCookiesPreferenceHandlingExplicitConsentMode(
		long companyId) {

		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getCompanyExplicitConsentMode(companyId);
	}

	private boolean _isCompanyCookiesPreferenceHandlingStoreConsent(
		long companyId) {

		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getCompanyStoreConsent(companyId);
	}

	private boolean _isCompanyFloatingIconEnabled(long companyId) {
		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getCompanyFloatingIconEnabled(companyId);
	}

	private boolean _isGroupCookiesPreferenceHandlingEnabled(long groupId) {
		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.getGroupEnabled(
			_getCompanyId(groupId), groupId);
	}

	private boolean _isGroupCookiesPreferenceHandlingExplicitConsentMode(
		long groupId) {

		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getGroupExplicitConsentMode(_getCompanyId(groupId), groupId);
	}

	private boolean _isGroupCookiesPreferenceHandlingStoreConsent(
		long groupId) {

		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getGroupStoreConsent(_getCompanyId(groupId), groupId);
	}

	private boolean _isGroupFloatingIconEnabled(long groupId) {
		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getGroupFloatingIconEnabled(_getCompanyId(groupId), groupId);
	}

	private boolean _isSystemCookiesPreferenceHandlingEnabled() {
		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getSystemEnabled();
	}

	private boolean _isSystemCookiesPreferenceHandlingExplicitConsentMode() {
		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getSystemExplicitConsentMode();
	}

	private boolean _isSystemCookiesPreferenceHandlingStoreConsent() {
		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getSystemStoreConsent();
	}

	private boolean _isSystemFloatingIconEnabled() {
		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getSystemFloatingIconEnabled();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CookiesConfigurationProviderImpl.class);

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationProvider _configurationProvider;

	private CookiesPreferenceHandlingManagedServiceFactory
		_cookiesPreferenceHandlingManagedServiceFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference(
		target = "(component.name=com.liferay.cookies.internal.configuration.admin.service.CookiesPreferenceHandlingManagedServiceFactory)"
	)
	private ManagedServiceFactory _managedServiceFactory;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

}