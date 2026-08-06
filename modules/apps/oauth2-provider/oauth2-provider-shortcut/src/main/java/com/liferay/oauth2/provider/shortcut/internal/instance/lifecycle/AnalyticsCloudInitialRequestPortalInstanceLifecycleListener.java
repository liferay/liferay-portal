/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.shortcut.internal.instance.lifecycle;

import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.constants.OAuth2ProviderActionKeys;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.scope.spi.application.descriptor.ApplicationDescriptor;
import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandlerFactory;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.oauth2.provider.scope.spi.scope.mapper.ScopeMapper;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.shortcut.internal.constants.OAuth2ProviderShortcutConstants;
import com.liferay.oauth2.provider.shortcut.internal.spi.scope.finder.OAuth2ProviderShortcutScopeFinder;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.oauth2.provider.util.builder.OAuth2ScopeBuilder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.instance.lifecycle.InitialRequestPortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyService;
import com.liferay.portal.kernel.service.ContactService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.PortalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shinn Lok
 */
@Component(
	property = {
		"osgi.jaxrs.name=liferay-json-web-services-analytics",
		"sap.scope.finder=true", "sap.system.entry=OAUTH2_analytics.read",
		"sap.system.entry=OAUTH2_analytics.write"
	},
	service = PortalInstanceLifecycleListener.class
)
public class AnalyticsCloudInitialRequestPortalInstanceLifecycleListener
	extends InitialRequestPortalInstanceLifecycleListener {

	@Activate
	@Override
	protected void activate(BundleContext bundleContext) {
		super.activate(bundleContext);

		_scopeAliasesList = new ArrayList<>(_SAP_ENTRY_OBJECT_ARRAYS.length);

		for (String[] sapEntryObjectArray : _SAP_ENTRY_OBJECT_ARRAYS) {
			_scopeAliasesList.add(
				StringUtil.replaceFirst(
					sapEntryObjectArray[0], "OAUTH2_", StringPool.BLANK));
		}

		_serviceRegistration = bundleContext.registerService(
			new String[] {
				ApplicationDescriptor.class.getName(),
				PrefixHandlerFactory.class.getName(),
				ScopeFinder.class.getName(), ScopeMapper.class.getName()
			},
			new OAuth2ProviderShortcutScopeFinder(_sapEntryLocalService),
			HashMapDictionaryBuilder.<String, Object>put(
				"osgi.jaxrs.name",
				OAuth2ProviderShortcutConstants.APPLICATION_NAME
			).put(
				"sap.scope.finder", true
			).build());
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	@Override
	protected void doPortalInstanceRegistered(long companyId) throws Exception {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(null);

		try {
			OAuth2Application oAuth2Application = _addOAuth2Application(
				companyId);

			if (oAuth2Application != null) {
				_addResourcePermissions(oAuth2Application);
			}
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	private OAuth2Application _addOAuth2Application(long companyId)
		throws Exception {

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.
				fetchOAuth2ApplicationByExternalReferenceCode(
					"ANALYTICS-CLOUD", companyId);

		if (oAuth2Application != null) {
			return oAuth2Application;
		}

		User user = _userLocalService.fetchUserByScreenName(
			companyId, UserConstants.SCREEN_NAME_DEFAULT_SERVICE_ACCOUNT);

		if (user == null) {
			return null;
		}

		_addSAPEntries(companyId, user.getUserId());

		oAuth2Application =
			_oAuth2ApplicationLocalService.addOrUpdateOAuth2Application(
				"ANALYTICS-CLOUD", user.getUserId(), user.getScreenName(),
				new ArrayList<GrantType>() {
					{
						add(GrantType.CLIENT_CREDENTIALS);
						add(GrantType.JWT_BEARER);
					}
				},
				"client_secret_post", user.getUserId(),
				OAuth2SecureRandomGenerator.generateClientId(),
				ClientProfile.HEADLESS_SERVER.id(),
				OAuth2SecureRandomGenerator.generateClientSecret(), null, null,
				"https://ldp.liferay.com", 0, null, _APPLICATION_NAME, null,
				Arrays.asList(
					"https://analytics.liferay.com/oauth/receive",
					"https://ldp.liferay.com/oauth/receive"),
				false, false, this::_buildScopes, new ServiceContext());

		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/logo.png");

		return _oAuth2ApplicationLocalService.updateIcon(
			oAuth2Application.getOAuth2ApplicationId(), inputStream);
	}

	private void _addResourcePermissions(OAuth2Application oAuth2Application)
		throws Exception {

		Role role = _roleLocalService.fetchRole(
			oAuth2Application.getCompanyId(),
			RoleConstants.ANALYTICS_ADMINISTRATOR);

		if (role == null) {
			return;
		}

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				oAuth2Application.getCompanyId(),
				OAuth2Application.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(oAuth2Application.getPrimaryKey()),
				role.getRoleId());

		if (resourcePermission != null) {
			return;
		}

		_resourcePermissionLocalService.setResourcePermissions(
			oAuth2Application.getCompanyId(), OAuth2Application.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(oAuth2Application.getPrimaryKey()), role.getRoleId(),
			new String[] {
				ActionKeys.VIEW, OAuth2ProviderActionKeys.CREATE_TOKEN
			});
	}

	private void _addSAPEntries(long companyId, long userId) throws Exception {
		for (String[] sapEntryObjectArray : _SAP_ENTRY_OBJECT_ARRAYS) {
			String sapEntryName = sapEntryObjectArray[0];

			SAPEntry sapEntry = _sapEntryLocalService.fetchSAPEntry(
				companyId, sapEntryName);

			if (sapEntry != null) {
				continue;
			}

			Map<Locale, String> titleMap =
				ResourceBundleUtil.getLocalizationMap(
					LanguageResources.PORTAL_RESOURCE_BUNDLE_LOADER,
					sapEntryName);

			_sapEntryLocalService.addSAPEntry(
				userId, sapEntryObjectArray[1], false, true, sapEntryName,
				titleMap, new ServiceContext());
		}
	}

	private void _buildAnalyticsCloudScopes(OAuth2ScopeBuilder builder) {
		builder.forApplication(
			OAuth2ProviderShortcutConstants.APPLICATION_NAME,
			"com.liferay.oauth2.provider.shortcut",
			applicationScopeAssigner -> _scopeAliasesList.forEach(
				applicationScopeAssigner::assignScope));
	}

	private void _buildScopes(OAuth2ScopeBuilder builder) {
		_buildAnalyticsCloudScopes(builder);

		_buildSegmentsAsahScopes(builder);
	}

	private void _buildSegmentsAsahScopes(OAuth2ScopeBuilder builder) {
		builder.forApplication(
			"Liferay.Segments.Asah.REST", "com.liferay.segments.asah.rest.impl",
			applicationScopeAssigner -> applicationScopeAssigner.assignScope(
				_SEGMENTS_ASAH_DEFAULT_OAUTH2_SCOPE_GRANTS
			).mapToScopeAlias(
				"Liferay.Segments.Asah.REST.everything"
			));
	}

	private static final String _APPLICATION_NAME = "Analytics Cloud";

	private static final String[][] _SAP_ENTRY_OBJECT_ARRAYS = {
		{
			"OAUTH2_analytics.read",
			StringBundler.concat(
				"com.liferay.portal.security.audit.storage.service.",
				"AuditEventService#getAuditEvents\n",
				ContactService.class.getName(), "#getContact\n",
				GroupService.class.getName(), "#getGroup\n",
				GroupService.class.getName(), "#getGroups\n",
				GroupService.class.getName(), "#getGroupsCount\n",
				GroupService.class.getName(), "#getGtGroups\n",
				OrganizationService.class.getName(), "#fetchOrganization\n",
				OrganizationService.class.getName(), "#getGtOrganizations\n",
				OrganizationService.class.getName(), "#getOrganization\n",
				OrganizationService.class.getName(), "#getOrganizations\n",
				OrganizationService.class.getName(), "#getOrganizationsCount\n",
				OrganizationService.class.getName(), "#getUserOrganizations\n",
				PortalService.class.getName(), "#getBuildNumber\n",
				UserService.class.getName(), "#getCompanyUsers\n",
				UserService.class.getName(), "#getCompanyUsersCount\n",
				UserService.class.getName(), "#getCurrentUser\n",
				UserService.class.getName(), "#getGtCompanyUsers\n",
				UserService.class.getName(), "#getGtOrganizationUsers\n",
				UserService.class.getName(), "#getGtUserGroupUsers\n",
				UserService.class.getName(), "#getOrganizationUsers\n",
				UserService.class.getName(), "#getOrganizationUsersCount\n",
				UserService.class.getName(),
				"#getOrganizationsAndUserGroupsUsersCount\n",
				UserService.class.getName(), "#getUserById\n",
				UserService.class.getName(), "#getUserGroupUsers\n",
				UserGroupService.class.getName(), "#fetchUserGroup\n",
				UserGroupService.class.getName(), "#getGtUserGroups\n",
				UserGroupService.class.getName(), "#getUserGroup\n",
				UserGroupService.class.getName(), "#getUserGroups\n",
				UserGroupService.class.getName(), "#getUserGroupsCount\n",
				UserGroupService.class.getName(), "#getUserUserGroups")
		},
		{
			"OAUTH2_analytics.write",
			CompanyService.class.getName() + "#updatePreferences"
		}
	};

	private static final String[] _SEGMENTS_ASAH_DEFAULT_OAUTH2_SCOPE_GRANTS = {
		"DELETE", "GET", "POST"
	};

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SAPEntryLocalService _sapEntryLocalService;

	private List<String> _scopeAliasesList;
	private ServiceRegistration<?> _serviceRegistration;

	@Reference
	private UserLocalService _userLocalService;

}