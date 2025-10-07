/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.configuration.persistence.listener;

import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.exception.CTStagingEnabledException;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTPreferences;
import com.liferay.change.tracking.model.CTPreferencesTable;
import com.liferay.change.tracking.scheduler.PublishScheduler;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupTable;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(
	property = "model.class.name=com.liferay.change.tracking.configuration.CTSettingsConfiguration",
	service = ConfigurationModelListener.class
)
public class CTSettingsConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onAfterSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		boolean enabled = GetterUtil.getBoolean(properties.get("enabled"));

		if (enabled) {
			return;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			long companyId = GetterUtil.getLong(properties.get("companyId"));

			_cleanUpCTPreferences(companyId);

			_cleanUpScheduledCTCollections(companyId);
		}
		catch (PortalException portalException) {
			throw new ConfigurationModelListenerException(
				portalException, CTSettingsConfiguration.class, getClass(),
				properties);
		}
	}

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			long companyId = GetterUtil.getLong(properties.get("companyId"));

			if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPS-186360")) {
				properties.put("remoteEnabled", false);
			}

			boolean enabled = GetterUtil.getBoolean(properties.get("enabled"));

			if (enabled) {
				_checkStagingEnabled(companyId);

				_setUpRemotePublications(companyId, properties);
			}
			else {
				properties.put("remoteEnabled", false);
				properties.put("sandboxEnabled", false);
			}

			_cleanUpRemotePublications(companyId, properties);
		}
		catch (PortalException portalException) {
			throw new ConfigurationModelListenerException(
				portalException, CTSettingsConfiguration.class, getClass(),
				properties);
		}
	}

	private void _checkStagingEnabled(long companyId) throws PortalException {
		for (Group group :
				_groupLocalService.<List<Group>>dslQuery(
					DSLQueryFactoryUtil.select(
						GroupTable.INSTANCE
					).from(
						GroupTable.INSTANCE
					).where(
						GroupTable.INSTANCE.companyId.eq(
							companyId
						).and(
							GroupTable.INSTANCE.liveGroupId.neq(
								GroupConstants.DEFAULT_LIVE_GROUP_ID
							).or(
								GroupTable.INSTANCE.typeSettings.like(
									"%staged=true%")
							).or(
								GroupTable.INSTANCE.remoteStagingGroupCount.gt(
									0)
							).withParentheses()
						)
					))) {

			if (group.hasRemoteStagingGroup() || group.isStaged() ||
				group.isStagingGroup()) {

				throw new CTStagingEnabledException();
			}
		}
	}

	private void _cleanUpCTPreferences(long companyId) {
		for (CTPreferences ctPreferences :
				_ctPreferencesLocalService.<List<CTPreferences>>dslQuery(
					DSLQueryFactoryUtil.select(
						CTPreferencesTable.INSTANCE
					).from(
						CTPreferencesTable.INSTANCE
					).where(
						CTPreferencesTable.INSTANCE.companyId.eq(companyId)
					))) {

			_ctPreferencesLocalService.deleteCTPreferences(ctPreferences);
		}
	}

	private void _cleanUpRemotePublications(
			long companyId, Dictionary<String, Object> properties)
		throws PortalException {

		boolean remoteEnabled = GetterUtil.getBoolean(
			properties.get("remoteEnabled"));

		if (remoteEnabled) {
			return;
		}

		String clientId = GetterUtil.getString(
			properties.get("remoteClientId"));

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.fetchOAuth2Application(
				companyId, clientId);

		if (oAuth2Application != null) {
			_oAuth2ApplicationLocalService.deleteOAuth2Application(
				oAuth2Application);
		}

		properties.put("remoteClientId", StringPool.BLANK);
		properties.put("remoteClientSecret", StringPool.BLANK);
	}

	private void _cleanUpScheduledCTCollections(long companyId)
		throws PortalException {

		if (PropsValues.SCHEDULER_ENABLED) {
			for (CTCollection ctCollection :
					_ctCollectionLocalService.getCTCollections(
						companyId, WorkflowConstants.STATUS_SCHEDULED,
						QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

				_publishScheduler.unschedulePublish(
					ctCollection.getCtCollectionId());
			}
		}
	}

	private void _setUpRemotePublications(
			long companyId, Dictionary<String, Object> properties)
		throws PortalException {

		boolean remoteEnabled = GetterUtil.getBoolean(
			properties.get("remoteEnabled"));

		if (!remoteEnabled) {
			return;
		}

		User user = _userLocalService.fetchUserByScreenName(
			companyId, _SCREEN_NAME);

		if (user == null) {
			Company company = _companyLocalService.getCompany(companyId);

			Role role = _roleLocalService.getRole(
				companyId, RoleConstants.PUBLICATIONS_USER);

			user = _userLocalService.addUser(
				UserConstants.USER_ID_DEFAULT, companyId, true, null, null,
				false, _SCREEN_NAME,
				_SCREEN_NAME + StringPool.AT + company.getMx(),
				LocaleUtil.fromLanguageId(PropsValues.COMPANY_DEFAULT_LOCALE),
				"Publications", StringPool.BLANK, "Service Account", 0, 0, true,
				Calendar.JANUARY, 1, 1970, StringPool.BLANK,
				UserConstants.TYPE_SERVICE_ACCOUNT, null, null,
				new long[] {role.getRoleId()}, null, false,
				new ServiceContext());

			user.setEmailAddressVerified(true);

			user = _userLocalService.updateUser(user);
		}

		String clientId = GetterUtil.getString(
			properties.get("remoteClientId"));

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.fetchOAuth2Application(
				companyId, clientId);

		if (oAuth2Application != null) {
			return;
		}

		if (Validator.isNull(clientId)) {
			clientId = OAuth2SecureRandomGenerator.generateClientId();

			properties.put("remoteClientId", clientId);
		}

		String clientSecret = GetterUtil.getString(
			properties.get("remoteClientSecret"));

		if (Validator.isNull(clientSecret)) {
			clientSecret = OAuth2SecureRandomGenerator.generateClientSecret();

			properties.put("remoteClientSecret", clientSecret);
		}

		_oAuth2ApplicationLocalService.addOAuth2Application(
			companyId, user.getUserId(), user.getScreenName(),
			new ArrayList<GrantType>() {
				{
					add(GrantType.CLIENT_CREDENTIALS);
					add(GrantType.JWT_BEARER);
				}
			},
			"client_secret_post", user.getUserId(), clientId,
			ClientProfile.HEADLESS_SERVER.id(), clientSecret, null,
			new ArrayList<String>() {
				{
					add("token.introspection");
				}
			},
			null, 0, null, _APPLICATION_NAME, null, null, false, false,
			builder -> builder.forApplication(
				"Liferay.Change.Tracking.REST",
				"com.liferay.change.tracking.rest.impl",
				applicationScopeAssigner ->
					applicationScopeAssigner.assignScope(
						"DELETE", "GET", "PATCH", "POST", "PUT"
					).mapToScopeAlias(
						"Liferay.Change.Tracking.REST.everything"
					)),
			new ServiceContext());
	}

	private static final String _APPLICATION_NAME =
		"Remote Publications Headless Server";

	private static final String _SCREEN_NAME = "publications-service-account";

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTPreferencesLocalService _ctPreferencesLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Reference
	private PublishScheduler _publishScheduler;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}