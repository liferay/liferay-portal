/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.impl;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.FaroChannelLocalService;
import com.liferay.osb.faro.service.FaroPreferencesLocalService;
import com.liferay.osb.faro.service.FaroProjectEmailDomainLocalService;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.osb.faro.service.base.FaroProjectLocalServiceBaseImpl;
import com.liferay.osb.faro.util.EmailUtil;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.comparator.GroupNameComparator;

import jakarta.mail.internet.InternetAddress;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@Component(
	property = "model.class.name=com.liferay.osb.faro.model.FaroProject",
	service = AopService.class
)
public class FaroProjectLocalServiceImpl
	extends FaroProjectLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public FaroProject addFaroProject(
			long userId, String name, String accountKey, String accountName,
			String corpProjectName, String corpProjectUuid,
			List<String> emailAddressDomains, String friendlyURL,
			String incidentReportEmailAddresses, String serverLocation,
			String services, String state, String subscription,
			String timeZoneId, String weDeployKey)
		throws PortalException {

		long faroProjectId = counterLocalService.increment();

		Group group = _groupLocalService.addGroup(
			userId, 0, FaroProject.class.getName(), faroProjectId, 0,
			Collections.singletonMap(LocaleUtil.getDefault(), name), null,
			GroupConstants.TYPE_SITE_PRIVATE, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, friendlyURL, true,
			true, null);

		// friendlyURL will be derived from the group name if empty, so it has
		// to be removed after creation

		if ((friendlyURL == null) || Validator.isBlank(friendlyURL.trim())) {
			group.setFriendlyURL(null);

			group = _groupLocalService.updateGroup(group);
		}

		long groupId = group.getGroupId();

		FaroProject faroProject = faroProjectPersistence.create(faroProjectId);

		faroProject.setGroupId(group.getGroupId());
		faroProject.setUserId(userId);

		long now = System.currentTimeMillis();

		faroProject.setCreateTime(now);
		faroProject.setModifiedTime(now);

		faroProject.setName(name);
		faroProject.setAccountKey(accountKey);
		faroProject.setAccountName(accountName);
		faroProject.setCorpProjectName(corpProjectName);
		faroProject.setCorpProjectUuid(corpProjectUuid);
		faroProject.setIncidentReportEmailAddresses(
			incidentReportEmailAddresses);
		faroProject.setServerLocation(serverLocation);
		faroProject.setServices(services);
		faroProject.setState(state);
		faroProject.setSubscription(subscription);
		faroProject.setSubscriptionModifiedTime(now);
		faroProject.setTimeZoneId(timeZoneId);
		faroProject.setWeDeployKey(weDeployKey);

		_faroProjectEmailDomainLocalService.addFaroProjectEmailDomains(
			groupId, faroProjectId, emailAddressDomains);

		return faroProjectPersistence.update(faroProject);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public FaroProject deleteFaroProjectByGroupId(long groupId)
		throws PortalException {

		_faroChannelLocalService.deleteFaroChannels(groupId);
		_faroPreferencesLocalService.deleteFaroPreferencesByGroupId(groupId);
		_faroUserLocalService.deleteFaroUsers(groupId);

		_groupLocalService.deleteGroup(groupId);

		return faroProjectPersistence.removeByGroupId(groupId);
	}

	@Override
	public <T> T dslQuery(DSLQuery dslQuery) {
		return faroProjectPersistence.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(DSLQuery dslQuery) {
		return faroProjectPersistence.dslQueryCount(dslQuery);
	}

	@Override
	public FaroProject fetchFaroProjectByCorpProjectUuid(
		String corpProjectUuid) {

		return faroProjectPersistence.fetchByCorpProjectUuid(corpProjectUuid);
	}

	@Override
	public FaroProject fetchFaroProjectByGroupId(long groupId) {
		return faroProjectPersistence.fetchByGroupId(groupId);
	}

	@Override
	public FaroProject fetchFaroProjectByWeDeployKey(String weDeployKey) {
		return faroProjectPersistence.fetchByWeDeployKey(weDeployKey);
	}

	@Override
	public FaroProject getFaroProjectByGroupId(long groupId)
		throws PortalException {

		return faroProjectPersistence.findByGroupId(groupId);
	}

	@Override
	public FaroProject getFaroProjectByWeDeployKey(String weDeployKey)
		throws PortalException {

		return faroProjectPersistence.findByWeDeployKey(weDeployKey);
	}

	@Override
	public List<FaroProject> getFaroProjects(String serverLocation) {
		return faroProjectPersistence.findByServerLocation(serverLocation);
	}

	@Override
	public List<FaroProject> getFaroProjectsByEmailAddressDomain(
		String emailAddressDomains) {

		return faroProjectFinder.findByEmailAddressDomain(emailAddressDomains);
	}

	@Override
	public List<FaroProject> getFaroProjectsByUserId(long userId) {
		return faroProjectPersistence.findByUserId(userId);
	}

	@Override
	public List<FaroProject> getJoinableFaroProjects(User user)
		throws PortalException {

		List<FaroProject> faroProjects =
			faroProjectLocalService.getFaroProjectsByEmailAddressDomain(
				StringUtil.extractLast(user.getEmailAddress(), CharPool.AT));

		List<Group> groups = _groupLocalService.getUserGroups(
			user.getUserId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			new GroupNameComparator(true));

		List<Long> groupIds = TransformUtil.transform(
			groups, Group::getGroupId);

		return TransformUtil.transform(
			faroProjects,
			faroProject -> {
				if (!groupIds.contains(faroProject.getGroupId()) &&
					StringUtil.equals(
						faroProject.getState(),
						FaroProjectConstants.STATE_READY)) {

					return faroProject;
				}

				return null;
			});
	}

	@Override
	public void sendCreatedWorkspaceEmail(String weDeployKey) throws Exception {
		FaroProject faroProject = fetchFaroProjectByWeDeployKey(weDeployKey);

		FaroUser faroUser = _faroUserLocalService.fetchOwnerFaroUser(
			faroProject.getGroupId());

		if (faroUser == null) {
			return;
		}

		String body = StringUtil.read(
			getClassLoader(),
			"com/liferay/osb/faro/dependencies/created-workspace.html");

		User user = _userLocalService.getUser(faroProject.getUserId());

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", user.getLocale(), getClass());

		String subject = _language.get(
			resourceBundle, "welcome-to-analytics-cloud");

		body = StringUtil.replace(
			body,
			new String[] {
				"[$BUTTON_TEXT$]", "[$BUTTON_URL$]", "[$EMAIL_HEADER_URL$]",
				"[$EMAIL_TITLE$]", "[$FARO_URL$]", "[$FOOTER_MENU_1$]",
				"[$FOOTER_MENU_2$]", "[$FOOTER_MENU_3$]", "[$FOOTER_MSG_1$]",
				"[$FOOTER_MSG_2$]", "[$FOOTER_MSG_3$]", "[$FOOTER_MSG_4$]",
				"[$HEADER_MSG_1$]", "[$ICON_CHECK_URL$]",
				"[$LIFERAY_LOGO_URL$]", "[$NOTIFICATION_MSG_1$]",
				"[$NOTIFICATION_MSG_2$]", "[$NOTIFICATION_MSG_3$]",
				"[$NOTIFICATION_MSG_4$]", "[$NOTIFICATION_MSG_5$]",
				"[$NOTIFICATION_MSG_6$]", "[$NOTIFICATION_MSG_7$]", "[$YEAR$]"
			},
			new String[] {
				_language.get(resourceBundle, "go-to-analytics-cloud"),
				EmailUtil.getShareIconURL(), EmailUtil.getEmailHeaderURL(),
				subject, FaroPropsValues.FARO_URL,
				_language.get(resourceBundle, "contact-support"),
				_language.get(resourceBundle, "documentation"),
				_language.get(resourceBundle, "announcements"),
				_language.format(
					resourceBundle, "this-email-was-sent-by-x",
					new String[] {
						"<a style=\"color: #0b5fff; text-decoration: none;\" " +
							"href=\"https://liferay.com\" target=\"_blank\">",
						"</a>"
					}),
				_language.get(resourceBundle, "need-help"),
				_language.get(
					resourceBundle, "let-our-team-do-the-work-for-you"),
				_language.get(
					resourceBundle,
					"liferay-experts-are-available-to-answer-your-questions-" +
						"anytime"),
				subject, EmailUtil.getCheckIconURL(),
				EmailUtil.getLiferayIconURL(),
				_language.format(
					resourceBundle, "your-workspace-x-is-ready",
					faroProject.getName()),
				_language.format(
					resourceBundle,
					"sign-in-with-your-existing-liferay-username-and-" +
						"password-or-create-an-account-using-x",
					new String[] {
						"<a style=\"color: #0b5fff; text-decoration: none;\" " +
							"href=\"https://login.liferay.com/signin" +
								"/register\" target=\"_blank\">",
						"</a>", faroUser.getEmailAddress()
					}),
				_language.get(resourceBundle, "getting-started"),
				_language.get(
					resourceBundle, "get-up-and-running-in-three-steps"),
				_language.get(
					resourceBundle,
					"copy-the-token-to-your-liferay-dxp-instance"),
				_language.get(
					resourceBundle,
					"sync-your-dxp-sites-and-contacts-to-a-property"),
				_language.get(
					resourceBundle, "invite-teammates-to-collaborate"),
				String.valueOf(DateUtil.getYear(new Date()))
			});

		_mailService.sendEmail(
			new MailMessage(
				new InternetAddress("ac@liferay.com", "Analytics Cloud"),
				new InternetAddress(faroUser.getEmailAddress(), null), subject,
				body, true));

		if (_log.isInfoEnabled()) {
			_log.info(
				"Created workspace email notification sent to " +
					faroUser.getEmailAddress());
		}
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public FaroProject updateState(long faroProjectId, String state) {
		FaroProject faroProject = faroProjectPersistence.fetchByPrimaryKey(
			faroProjectId);

		if (faroProject == null) {
			return null;
		}

		faroProject.setState(state);

		return faroProjectPersistence.update(faroProject);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public FaroProject updateSubscription(
		long faroProjectId, String subscription) {

		FaroProject faroProject = faroProjectPersistence.fetchByPrimaryKey(
			faroProjectId);

		if (faroProject == null) {
			return null;
		}

		long currentTimeMillis = System.currentTimeMillis();

		faroProject.setModifiedTime(currentTimeMillis);

		faroProject.setSubscription(subscription);

		try {
			JSONObject oldSubscriptionJSONObject =
				_jsonFactory.createJSONObject(faroProject.getSubscription());

			String oldSubscriptionName = oldSubscriptionJSONObject.getString(
				"name");

			oldSubscriptionName = StringUtil.replace(
				oldSubscriptionName, "LXC ", "Liferay SaaS ");

			JSONObject newSubscriptionJSONObject =
				_jsonFactory.createJSONObject(subscription);

			if (!Objects.equals(
					oldSubscriptionName,
					newSubscriptionJSONObject.get("name"))) {

				faroProject.setSubscriptionModifiedTime(currentTimeMillis);
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return faroProjectPersistence.update(faroProject);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FaroProjectLocalServiceImpl.class);

	@Reference
	private FaroChannelLocalService _faroChannelLocalService;

	@Reference
	private FaroPreferencesLocalService _faroPreferencesLocalService;

	@Reference
	private FaroProjectEmailDomainLocalService
		_faroProjectEmailDomainLocalService;

	@Reference
	private FaroUserLocalService _faroUserLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private MailService _mailService;

	@Reference
	private UserLocalService _userLocalService;

}