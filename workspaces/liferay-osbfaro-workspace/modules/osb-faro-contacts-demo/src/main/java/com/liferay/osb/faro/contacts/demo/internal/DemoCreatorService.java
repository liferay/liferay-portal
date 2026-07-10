/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.contacts.demo.internal;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;

import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.osb.faro.contacts.demo.internal.model.display.contacts.mixin.BaseMixin;
import com.liferay.osb.faro.contacts.demo.internal.model.display.contacts.mixin.CredentialsMixin;
import com.liferay.osb.faro.contacts.demo.internal.model.display.contacts.mixin.OAuth1CredentialsMixin;
import com.liferay.osb.faro.contacts.demo.internal.model.display.contacts.mixin.OAuth2CredentialsMixin;
import com.liferay.osb.faro.contacts.demo.internal.model.display.contacts.mixin.PageVisitMixin;
import com.liferay.osb.faro.contacts.demo.internal.model.display.contacts.mixin.TokenCredentialsMixin;
import com.liferay.osb.faro.contacts.model.constants.JSONConstants;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Channel;
import com.liferay.osb.faro.engine.client.model.Credentials;
import com.liferay.osb.faro.engine.client.model.Individual;
import com.liferay.osb.faro.engine.client.model.Interest;
import com.liferay.osb.faro.engine.client.model.LCPProject;
import com.liferay.osb.faro.engine.client.model.PageVisited;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.model.credentials.OAuth1Credentials;
import com.liferay.osb.faro.engine.client.model.credentials.OAuth2Credentials;
import com.liferay.osb.faro.engine.client.model.credentials.TokenCredentials;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.provisioning.client.ProvisioningClient;
import com.liferay.osb.faro.provisioning.client.constants.ProductConstants;
import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntry;
import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntryBuilder;
import com.liferay.osb.faro.provisioning.client.model.OSBOfferingEntry;
import com.liferay.osb.faro.provisioning.client.model.display.main.FaroSubscriptionDisplay;
import com.liferay.osb.faro.service.FaroChannelLocalService;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Collections;
import java.util.Date;

import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Matthew Kong
 */
public abstract class DemoCreatorService {

	public void createDemo() throws Exception {
		faroProject = faroProjectLocalService.fetchFaroProjectByCorpProjectUuid(
			FaroPropsValues.FARO_PROJECT_ID);

		if (faroProject == null) {
			faroProject = createFaroProject();
		}

		try {
			createUsers(faroProject);
		}
		catch (Exception exception) {
			log.error("Unable to add users", exception);
		}

		if (hasExistingData()) {
			if (log.isInfoEnabled()) {
				log.info("Skipping demo creation because of existing data");
			}

			return;
		}

		createData();

		createFaroChannels();
	}

	protected abstract void createData() throws Exception;

	protected void createFaroChannels() throws Exception {
		Results<Channel> results = contactsEngineClient.getChannels(
			faroProject, 0, 10000, null, null);

		User user = userLocalService.getUserByEmailAddress(
			portal.getDefaultCompanyId(), "test@liferay.com");

		for (Channel channel : results.getItems()) {
			faroChannelLocalService.addFaroChannel(
				user.getUserId(), channel.getName(), channel.getId(),
				faroProject.getGroupId());
		}
	}

	protected FaroProject createFaroProject() throws Exception {
		User user = userLocalService.getUserByEmailAddress(
			portal.getDefaultCompanyId(), "test@liferay.com");

		OSBAccountEntry osbAccountEntry = null;

		if (FaroPropsValues.OSB_FARO_SUBSCRIPTION_PUSH_ENABLED) {
			OSBOfferingEntry osbOfferingEntry = new OSBOfferingEntry();

			osbOfferingEntry.setProductEntryId(
				ProductConstants.ENTERPRISE_PRODUCT_ENTRY_ID);
			osbOfferingEntry.setQuantity(1);
			osbOfferingEntry.setStartDate(new Date());
			osbOfferingEntry.setStatus(
				ProductConstants.OSB_OFFERING_ENTRY_STATUS_ACTIVE);

			osbAccountEntry = OSBAccountEntryBuilder.setCorpProjectUuid(
				FaroPropsValues.FARO_PROJECT_ID
			).setName(
				FaroPropsValues.FARO_PROJECT_ID
			).setOfferingEntries(
				Collections.singletonList(osbOfferingEntry)
			).build();
		}
		else {
			osbAccountEntry = provisioningClient.getOSBAccountEntry(
				FaroPropsValues.FARO_PROJECT_ID);
		}

		FaroSubscriptionDisplay faroSubscriptionDisplay =
			new FaroSubscriptionDisplay(osbAccountEntry);

		FaroProject faroProject = faroProjectLocalService.addFaroProject(
			user.getUserId(), FaroPropsValues.FARO_PROJECT_ID,
			osbAccountEntry.getDossieraAccountKey(),
			osbAccountEntry.getCorpEntryName(), osbAccountEntry.getName(),
			FaroPropsValues.FARO_PROJECT_ID, Collections.emptyList(),
			StringPool.BLANK, StringPool.BLANK,
			LCPProject.Cluster.US.toString(), JSONConstants.NULL_JSON_ARRAY,
			FaroProjectConstants.STATE_UNCONFIGURED,
			_objectMapper.writeValueAsString(faroSubscriptionDisplay), "UTC",
			null);

		faroProject.setWeDeployKey(FaroPropsValues.FARO_DEFAULT_WE_DEPLOY_KEY);

		String weDeployKey =
			contactsEngineClient.addProject(faroProject) + ".lfr.cloud";

		faroProject.setWeDeployKey(weDeployKey);

		faroProject = faroProjectLocalService.updateFaroProject(faroProject);

		Role role = roleLocalService.getRole(
			user.getCompanyId(), RoleConstants.SITE_OWNER);

		faroUserLocalService.addFaroUser(
			user.getUserId(), faroProject.getGroupId(), 0, role.getRoleId(),
			"test@liferay.com", FaroUserConstants.STATUS_PENDING, false);

		faroProject.setState(FaroProjectConstants.STATE_NOT_READY);

		return faroProjectLocalService.updateFaroProject(faroProject);
	}

	protected void createUsers(FaroProject faroProject) throws Exception {
		for (String[] userInfo : _USER_INFO) {
			String screenName = userInfo[0];

			String emailAddress = screenName.concat("@faro.io");

			User user = userLocalService.fetchUserByEmailAddress(
				portal.getDefaultCompanyId(), emailAddress);

			if (user != null) {
				continue;
			}

			String[] screenNameParts = StringUtil.split(
				screenName, StringPool.PERIOD);

			String firstName = screenNameParts[0];

			String lastName = null;

			if (screenNameParts.length > 1) {
				lastName = screenNameParts[1];
			}
			else {
				lastName = screenNameParts[0];
			}

			user = userLocalService.addUserWithWorkflow(
				UserConstants.USER_ID_DEFAULT, portal.getDefaultCompanyId(),
				false, "test", "test", true, screenName, emailAddress,
				LocaleUtil.US, firstName, null, lastName, 0, 0, true, 1, 1,
				1970, null, UserConstants.TYPE_REGULAR, null, null, null, null,
				false, null);

			user.setPasswordReset(false);
			user.setPasswordModifiedDate(new Date());
			user.setLastLoginDate(new Date());
			user.setAgreedToTermsOfUse(true);

			user = userLocalService.updateUser(user);

			Role role = roleLocalService.getRole(
				portal.getDefaultCompanyId(), userInfo[1]);

			faroUserLocalService.addFaroUser(
				user.getUserId(), faroProject.getGroupId(), user.getUserId(),
				role.getRoleId(), emailAddress,
				FaroUserConstants.STATUS_APPROVED, false);
		}
	}

	protected boolean hasExistingData() {
		Results<Individual> individuals = contactsEngineClient.getIndividuals(
			faroProject, (String)null, false, 1, 0, null);

		if (individuals.getTotal() > 0) {
			return true;
		}

		return false;
	}

	protected static final Log log = LogFactoryUtil.getLog(
		DemoCreatorService.class);

	@Reference
	protected ContactsEngineClient contactsEngineClient;

	@Reference
	protected FaroChannelLocalService faroChannelLocalService;

	protected FaroProject faroProject;

	@Reference
	protected FaroProjectLocalService faroProjectLocalService;

	@Reference
	protected FaroUserLocalService faroUserLocalService;

	@Reference
	protected Portal portal;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected volatile ProvisioningClient provisioningClient;

	@Reference
	protected RoleLocalService roleLocalService;

	@Reference
	protected UserLocalService userLocalService;

	private static final String[][] _USER_INFO = {
		{"bryan.cheung", RoleConstants.SITE_OWNER},
		{"corbin.murakami", RoleConstants.SITE_MEMBER},
		{"michelle.hoshi", RoleConstants.SITE_ADMINISTRATOR},
		{"test", RoleConstants.SITE_OWNER}
	};

	private static final ObjectMapper _objectMapper = new ObjectMapper() {
		{
			addMixIn(Credentials.class, CredentialsMixin.class);
			addMixIn(Interest.class, BaseMixin.class);
			addMixIn(OAuth1Credentials.class, OAuth1CredentialsMixin.class);
			addMixIn(OAuth2Credentials.class, OAuth2CredentialsMixin.class);
			addMixIn(PageVisited.class, PageVisitMixin.class);
			addMixIn(TokenCredentials.class, TokenCredentialsMixin.class);
			configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			setPropertyNamingStrategy(
				new PropertyNamingStrategy() {

					@Override
					public String nameForField(
						MapperConfig<?> config, AnnotatedField field,
						String defaultName) {

						if (defaultName.startsWith(StringPool.UNDERLINE)) {
							defaultName = defaultName.substring(1);
						}

						defaultName = StringUtil.removeSubstring(
							defaultName, "Display");

						return super.nameForField(config, field, defaultName);
					}

				});
			setVisibility(
				PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		}
	};

}