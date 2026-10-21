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
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.GroupLocalService;
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

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Sanz
 */
@Component(service = CookiesConfigurationProvider.class)
public class CookiesConfigurationProviderImpl
	implements CookiesConfigurationProvider {

	@Override
	public void forceCookiesPreferenceHandlingReconsent(
			ExtendedObjectClassDefinition.Scope scope, long scopePK)
		throws Exception {

		Configuration configuration = null;

		if (scope == ExtendedObjectClassDefinition.Scope.SYSTEM) {
			configuration = _configurationAdmin.getConfiguration(
				CookiesPreferenceHandlingConfiguration.class.getName(), "?");
		}
		else {
			configuration = _getScopedConfiguration(scope, scopePK);
		}

		if (configuration == null) {
			return;
		}

		Dictionary<String, Object> properties = configuration.getProperties();

		if (properties == null) {
			properties = new HashMapDictionary<>();
		}

		properties.put("modifiedDate", System.currentTimeMillis());

		configuration.update(properties);
	}

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

		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCookiesPreferenceHandlingConfiguration(scope, scopePK);

		return cookiesPreferenceHandlingConfiguration.consentRenewalPeriod();
	}

	@Override
	public String getCookiesPreferenceHandlingConsentRenewalPeriodTimeUnit(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCookiesPreferenceHandlingConfiguration(scope, scopePK);

		return cookiesPreferenceHandlingConfiguration.
			consentRenewalPeriodTimeUnit();
	}

	@Override
	public long getCookiesPreferenceHandlingCustomFloatingIconImageId(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCookiesPreferenceHandlingConfiguration(scope, scopePK);

		return cookiesPreferenceHandlingConfiguration.
			customFloatingIconImageId();
	}

	@Override
	public int getCookiesPreferenceHandlingDissentRenewalPeriod(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCookiesPreferenceHandlingConfiguration(scope, scopePK);

		return cookiesPreferenceHandlingConfiguration.dissentRenewalPeriod();
	}

	@Override
	public String getCookiesPreferenceHandlingDissentRenewalPeriodTimeUnit(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCookiesPreferenceHandlingConfiguration(scope, scopePK);

		return cookiesPreferenceHandlingConfiguration.
			dissentRenewalPeriodTimeUnit();
	}

	@Override
	public String getCookiesPreferenceHandlingFloatingIcon(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCookiesPreferenceHandlingConfiguration(scope, scopePK);

		return cookiesPreferenceHandlingConfiguration.floatingIcon();
	}

	@Override
	public long getCookiesPreferenceHandlingModifiedDate(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCookiesPreferenceHandlingConfiguration(scope, scopePK);

		return cookiesPreferenceHandlingConfiguration.modifiedDate();
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
	public boolean isCookiesPreferenceHandlingActive(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCookiesPreferenceHandlingConfiguration(scope, scopePK);

		return cookiesPreferenceHandlingConfiguration.active();
	}

	@Override
	public boolean isCookiesPreferenceHandlingConfigurationDefined(
			ExtendedObjectClassDefinition.Scope scope, long scopePK)
		throws Exception {

		if (scope == ExtendedObjectClassDefinition.Scope.SYSTEM) {
			return true;
		}

		if (_getScopedConfiguration(scope, scopePK) == null) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isCookiesPreferenceHandlingEnabled(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCookiesPreferenceHandlingConfiguration(scope, scopePK);

		return cookiesPreferenceHandlingConfiguration.enabled();
	}

	@Override
	public boolean isCookiesPreferenceHandlingExplicitConsentMode(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCookiesPreferenceHandlingConfiguration(scope, scopePK);

		return cookiesPreferenceHandlingConfiguration.explicitConsentMode();
	}

	@Override
	public boolean isCookiesPreferenceHandlingFloatingIconEnabled(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCookiesPreferenceHandlingConfiguration(scope, scopePK);

		return cookiesPreferenceHandlingConfiguration.floatingIconEnabled();
	}

	@Override
	public boolean isCookiesPreferenceHandlingGlobalPrivacyControlEnabled(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCookiesPreferenceHandlingConfiguration(scope, scopePK);

		return cookiesPreferenceHandlingConfiguration.
			globalPrivacyControlEnabled();
	}

	@Override
	public boolean isCookiesPreferenceHandlingStoreConsent(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCookiesPreferenceHandlingConfiguration(scope, scopePK);

		return cookiesPreferenceHandlingConfiguration.storeConsent();
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
			boolean active, int consentRenewalPeriod, boolean enabled,
			boolean explicitConsentMode,
			ExtendedObjectClassDefinition.Scope scope, long scopePK,
			boolean storeConsent)
		throws Exception {

		Dictionary<String, Object> dictionary = _createDictionary(
			active, consentRenewalPeriod, enabled, explicitConsentMode,
			storeConsent);

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
		boolean active, int consentRenewalPeriod, boolean enabled,
		boolean explicitConsentMode, boolean storeConsent) {

		return HashMapDictionaryBuilder.<String, Object>put(
			"active", active
		).put(
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
			"storeConsent", storeConsent
		).build();
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

		Group scopeGroup = themeDisplay.getScopeGroup();

		return _configurationProvider.getGroupConfiguration(
			clazz, scopeGroup.getCompanyId(), scopeGroup.getGroupId());
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

	private CookiesPreferenceHandlingConfiguration
		_getCookiesPreferenceHandlingConfiguration(
			ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		try {
			if (scope.equals(ExtendedObjectClassDefinition.Scope.COMPANY)) {
				return _configurationProvider.getCompanyConfiguration(
					CookiesPreferenceHandlingConfiguration.class, scopePK);
			}
			else if (scope.equals(ExtendedObjectClassDefinition.Scope.GROUP)) {
				return _configurationProvider.getGroupConfiguration(
					CookiesPreferenceHandlingConfiguration.class,
					_getCompanyId(scopePK), scopePK);
			}
			else if (scope.equals(ExtendedObjectClassDefinition.Scope.SYSTEM)) {
				return _configurationProvider.getSystemConfiguration(
					CookiesPreferenceHandlingConfiguration.class);
			}

			throw new IllegalArgumentException("Unsupported scope: " + scope);
		}
		catch (ConfigurationException configurationException) {
			return ReflectionUtil.throwException(configurationException);
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

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

}