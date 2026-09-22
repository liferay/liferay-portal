/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.helper;

import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.exception.FaroEngineClientException;
import com.liferay.osb.faro.engine.client.exception.NoSuchEntryException;
import com.liferay.osb.faro.engine.client.model.AccountLifecycle;
import com.liferay.osb.faro.engine.client.model.AccountLifecycleStageTransition;
import com.liferay.osb.faro.engine.client.model.LifecycleTriggerResult;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.osb.faro.util.EmailUtil;
import com.liferay.osb.faro.web.internal.constants.FaroNotificationTemplateConstants;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SubscriptionSender;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Caio Pinheiro
 */
@Component(service = NotificationHelper.class)
public class NotificationHelper {

	public NotificationContext getNotificationContext(long groupId) {
		FaroProject faroProject =
			_faroProjectLocalService.fetchFaroProjectByGroupId(groupId);
		Group group = _groupLocalService.fetchGroup(groupId);

		if ((faroProject == null) || (group == null)) {
			return null;
		}

		return new NotificationContext(faroProject, group);
	}

	public void sendLifecycleNotifications(
			String frequency, String lifecycleId,
			Map<Long, List<LifecycleTriggerType>> lifecycleTriggerTypesByUserId,
			NotificationContext notificationContext)
		throws Exception {

		Group group = notificationContext.getGroup();

		Map<Long, User> users = _resolveUsers(
			group.getGroupId(), lifecycleTriggerTypesByUserId.keySet());

		if (users.isEmpty()) {
			return;
		}

		FaroProject faroProject = notificationContext.getFaroProject();

		Set<LifecycleTriggerType> lifecycleTriggerTypes = new LinkedHashSet<>();

		for (Long userId : users.keySet()) {
			lifecycleTriggerTypes.addAll(
				lifecycleTriggerTypesByUserId.get(userId));
		}

		AccountLifecycle accountLifecycle = null;
		NotificationTemplate notificationTemplate =
			notificationContext.getLifecycleNotificationTemplate();

		for (LifecycleTriggerType lifecycleTriggerType :
				lifecycleTriggerTypes) {

			try {
				LifecycleTriggerResult lifecycleTriggerResult =
					_getLifecycleTriggerResult(
						faroProject, lifecycleId, lifecycleTriggerType,
						_emailReportHelper.getRangeKey(frequency));

				long count = lifecycleTriggerResult.getCount();

				if (count == 0) {
					continue;
				}

				if (accountLifecycle == null) {
					accountLifecycle =
						_contactsEngineClient.getAccountLifecycle(
							faroProject, lifecycleId);

					if (accountLifecycle == null) {
						return;
					}
				}

				if (notificationTemplate == null) {
					notificationTemplate = _fetchNotificationTemplate(
						group.getCompanyId(),
						FaroNotificationTemplateConstants.
							EXTERNAL_REFERENCE_CODE_LIFECYCLE_NOTIFICATION);

					notificationContext.setLifecycleNotificationTemplate(
						notificationTemplate);
				}

				String lifecycleResultsText = null;

				if ((count == 1) &&
					(lifecycleTriggerResult.getAccountName() != null)) {

					lifecycleResultsText =
						lifecycleTriggerResult.getAccountName();
				}
				else {
					lifecycleResultsText = count + " accounts";
				}

				Map<String, String> tokensMap = HashMapBuilder.put(
					"[%LIFECYCLE_NAME%]", accountLifecycle.getName()
				).put(
					"[%LIFECYCLE_RESULT_TEXT%]", lifecycleResultsText
				).put(
					"[%LIFECYCLE_URL%]",
					EmailUtil.getWorkspaceURL(group) + "/lifecycle"
				).put(
					"[%SENDER_NAME%]", EmailUtil.getSenderName(faroProject)
				).build();

				Map<Locale, String> bodyMap = _replaceTokens(
					true, lifecycleTriggerType,
					notificationTemplate.getBodyMap(), tokensMap);
				Map<Locale, String> subjectMap = _replaceTokens(
					false, lifecycleTriggerType,
					notificationTemplate.getSubjectMap(), tokensMap);

				for (Map.Entry<Long, User> entry : users.entrySet()) {
					List<LifecycleTriggerType> userLifecycleTriggerTypes =
						lifecycleTriggerTypesByUserId.get(entry.getKey());

					if (!userLifecycleTriggerTypes.contains(
							lifecycleTriggerType)) {

						continue;
					}

					_sendNotification(
						bodyMap, faroProject, group, subjectMap,
						entry.getValue());
				}
			}
			catch (Exception exception) {
				_log.error(
					"Unable to send " + lifecycleTriggerType.getKey() +
						" notifications for lifecycle " + lifecycleId,
					exception);
			}
		}
	}

	public void sendSegmentNotifications(
			String frequency, NotificationContext notificationContext,
			String segmentId, Collection<Long> userIds)
		throws Exception {

		Group group = notificationContext.getGroup();

		long groupId = group.getGroupId();

		Map<Long, User> users = _resolveUsers(groupId, userIds);

		if (users.isEmpty()) {
			return;
		}

		FaroProject faroProject = notificationContext.getFaroProject();

		long segmentNewMembersCount = 0;

		try {
			segmentNewMembersCount =
				_contactsEngineClient.fetchSegmentNewMembersCount(
					faroProject, segmentId,
					_emailReportHelper.getRangeKey(frequency));
		}
		catch (NoSuchEntryException noSuchEntryException) {
			if (!_isSegmentDeleted(faroProject, segmentId)) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to find new members of segment " + segmentId,
						noSuchEntryException);
				}

				return;
			}

			if (_log.isWarnEnabled()) {
				_log.warn("Unable to find segment " + segmentId);
			}

			_notificationPreferencesHelper.removeSegmentNotificationPreferences(
				groupId, Collections.singleton(segmentId));

			return;
		}

		if (segmentNewMembersCount == 0) {
			return;
		}

		NotificationTemplate notificationTemplate =
			notificationContext.getSegmentNotificationTemplate();

		if (notificationTemplate == null) {
			notificationTemplate = _fetchNotificationTemplate(
				group.getCompanyId(),
				FaroNotificationTemplateConstants.
					EXTERNAL_REFERENCE_CODE_SEGMENT_NOTIFICATION);

			notificationContext.setSegmentNotificationTemplate(
				notificationTemplate);
		}

		Map<String, String> tokensMap = HashMapBuilder.put(
			"[%SEGMENT_NEW_MEMBERS_COUNT%]",
			String.valueOf(segmentNewMembersCount)
		).put(
			"[%SEGMENT_URL%]",
			EmailUtil.getWorkspaceURL(group) + "/contacts/segments/" + segmentId
		).put(
			"[%SENDER_NAME%]", EmailUtil.getSenderName(faroProject)
		).build();

		Map<Locale, String> bodyMap = _replaceTokens(
			true, notificationTemplate.getBodyMap(), tokensMap);
		Map<Locale, String> subjectMap = _replaceTokens(
			false, notificationTemplate.getSubjectMap(), tokensMap);

		for (User user : users.values()) {
			_sendNotification(bodyMap, faroProject, group, subjectMap, user);
		}
	}

	public static class NotificationContext {

		public FaroProject getFaroProject() {
			return _faroProject;
		}

		public Group getGroup() {
			return _group;
		}

		public NotificationTemplate getLifecycleNotificationTemplate() {
			return _lifecycleNotificationTemplate;
		}

		public NotificationTemplate getSegmentNotificationTemplate() {
			return _segmentNotificationTemplate;
		}

		public void setLifecycleNotificationTemplate(
			NotificationTemplate lifecycleNotificationTemplate) {

			_lifecycleNotificationTemplate = lifecycleNotificationTemplate;
		}

		public void setSegmentNotificationTemplate(
			NotificationTemplate segmentNotificationTemplate) {

			_segmentNotificationTemplate = segmentNotificationTemplate;
		}

		private NotificationContext(FaroProject faroProject, Group group) {
			_faroProject = faroProject;
			_group = group;
		}

		private final FaroProject _faroProject;
		private final Group _group;
		private NotificationTemplate _lifecycleNotificationTemplate;
		private NotificationTemplate _segmentNotificationTemplate;

	}

	private NotificationTemplate _createDefaultNotificationTemplate(
		String externalReferenceCode) {

		NotificationTemplate notificationTemplate =
			_notificationTemplateLocalService.createNotificationTemplate(0);

		notificationTemplate.setBodyMap(
			FaroNotificationTemplateConstants.getDefaultLocalizedMap(
				externalReferenceCode, "body"));
		notificationTemplate.setSubjectMap(
			FaroNotificationTemplateConstants.getDefaultLocalizedMap(
				externalReferenceCode, "subject"));

		return notificationTemplate;
	}

	private NotificationTemplate _fetchNotificationTemplate(
		long companyId, String externalReferenceCode) {

		NotificationTemplate notificationTemplate =
			_notificationTemplateLocalService.
				fetchNotificationTemplateByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (notificationTemplate != null) {
			return notificationTemplate;
		}

		try {
			return _faroNotificationTemplateProvisioner.
				verifyNotificationTemplate(companyId, externalReferenceCode);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to provision notification template " +
					externalReferenceCode,
				exception);
		}

		return _createDefaultNotificationTemplate(externalReferenceCode);
	}

	private void _flushSubscriptionSender(
			FaroProject faroProject, Group group,
			SubscriptionSender subscriptionSender, User user)
		throws Exception {

		subscriptionSender.setCompanyId(group.getCompanyId());
		subscriptionSender.setFrom(
			EmailUtil.getSenderEmailAddress(faroProject),
			EmailUtil.getSenderName(faroProject));
		subscriptionSender.setHtmlFormat(true);
		subscriptionSender.setMailId(
			"faro_notification", group.getGroupId(),
			System.currentTimeMillis());

		subscriptionSender.addRuntimeSubscribers(
			user.getEmailAddress(), user.getFullName());

		subscriptionSender.flushNotifications();
	}

	private LifecycleTriggerResult _getLifecycleTriggerResult(
			FaroProject faroProject, String lifecycleId,
			LifecycleTriggerType lifecycleTriggerType, int rangeKey)
		throws Exception {

		LifecycleTriggerResult lifecycleTriggerResult = null;

		if (lifecycleTriggerType == LifecycleTriggerType.NEW_ACCOUNTS) {
			lifecycleTriggerResult =
				_contactsEngineClient.getNewAccountsLifecycleTriggerResult(
					faroProject, lifecycleId, rangeKey);
		}
		else if (lifecycleTriggerType ==
					LifecycleTriggerType.NEW_STALLED_ACCOUNTS) {

			lifecycleTriggerResult =
				_contactsEngineClient.getStalledAccountsLifecycleTriggerResult(
					faroProject, lifecycleId, rangeKey);
		}
		else {
			Results<AccountLifecycleStageTransition> results =
				_contactsEngineClient.getAccountLifecycleStageTransitions(
					faroProject, null, null, lifecycleId, null, null, rangeKey,
					null, null, lifecycleTriggerType.getToLifecycleStage(), 0,
					2, null);

			lifecycleTriggerResult = new LifecycleTriggerResult();

			lifecycleTriggerResult.setCount(results.getTotal());

			List<AccountLifecycleStageTransition>
				accountLifecycleStageTransitions = results.getItems();

			if ((results.getTotal() == 1) &&
				ListUtil.isNotEmpty(accountLifecycleStageTransitions)) {

				AccountLifecycleStageTransition
					accountLifecycleStageTransition =
						accountLifecycleStageTransitions.get(0);

				lifecycleTriggerResult.setAccountName(
					accountLifecycleStageTransition.getAccountName());
			}
		}

		if (lifecycleTriggerResult == null) {
			return new LifecycleTriggerResult();
		}

		return lifecycleTriggerResult;
	}

	private boolean _isNoSuchSegmentError(
		FaroEngineClientException faroEngineClientException) {

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				faroEngineClientException.getMessage());

			JSONObject errorAttributesJSONObject = jsonObject.getJSONObject(
				"errorAttributes");

			if (errorAttributesJSONObject != null) {
				jsonObject = errorAttributesJSONObject;
			}

			return StringUtil.startsWith(
				jsonObject.getString("messageKey"),
				"there-is-no-segment-with-segment-id");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	private boolean _isSegmentDeleted(
		FaroProject faroProject, String segmentId) {

		try {
			_contactsEngineClient.getIndividualSegment(
				faroProject, segmentId, false);

			return false;
		}
		catch (NoSuchEntryException noSuchEntryException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchEntryException);
			}

			return true;
		}
		catch (FaroEngineClientException faroEngineClientException) {
			return _isNoSuchSegmentError(faroEngineClientException);
		}
	}

	private Map<Locale, String> _replaceTokens(
		boolean escape, LifecycleTriggerType lifecycleTriggerType,
		Map<Locale, String> templateMap, Map<String, String> tokensMap) {

		Map<Locale, String> replacedTemplateMap = new HashMap<>();

		for (Map.Entry<Locale, String> entry : templateMap.entrySet()) {
			Locale locale = entry.getKey();

			Map<String, String> localeTokensMap = new HashMap<>(tokensMap);

			if (lifecycleTriggerType != null) {
				localeTokensMap.put(
					"[%LIFECYCLE_TRIGGER_LABEL%]",
					LanguageUtil.get(
						locale, lifecycleTriggerType.getLanguageKey()));
			}

			String value = entry.getValue();

			for (Map.Entry<String, String> tokenEntry :
					localeTokensMap.entrySet()) {

				String tokenValue = tokenEntry.getValue();

				if (escape) {
					tokenValue = HtmlUtil.escape(tokenValue);
				}

				value = StringUtil.replace(
					value, tokenEntry.getKey(), tokenValue);
			}

			replacedTemplateMap.put(locale, value);
		}

		return replacedTemplateMap;
	}

	private Map<Locale, String> _replaceTokens(
		boolean escape, Map<Locale, String> templateMap,
		Map<String, String> tokensMap) {

		return _replaceTokens(escape, null, templateMap, tokensMap);
	}

	private Map<Long, User> _resolveUsers(
		long groupId, Collection<Long> userIds) {

		Map<Long, User> users = new LinkedHashMap<>();

		for (long userId : userIds) {
			FaroUser faroUser = _faroUserLocalService.fetchFaroUser(
				groupId, userId);

			if ((faroUser == null) ||
				(faroUser.getStatus() != FaroUserConstants.STATUS_APPROVED)) {

				continue;
			}

			User user = _userLocalService.fetchUser(userId);

			if ((user == null) || !user.isActive()) {
				continue;
			}

			users.put(userId, user);
		}

		return users;
	}

	private void _sendNotification(
		Map<Locale, String> bodyMap, FaroProject faroProject, Group group,
		Map<Locale, String> subjectMap, User user) {

		try {
			SubscriptionSender subscriptionSender = new SubscriptionSender();

			subscriptionSender.setLocalizedBodyMap(bodyMap);
			subscriptionSender.setLocalizedSubjectMap(subjectMap);

			_flushSubscriptionSender(
				faroProject, group, subscriptionSender, user);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to send notification to user " + user.getUserId(),
				exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NotificationHelper.class);

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	@Reference
	private EmailReportHelper _emailReportHelper;

	@Reference
	private FaroNotificationTemplateProvisioner
		_faroNotificationTemplateProvisioner;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference
	private FaroUserLocalService _faroUserLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private NotificationPreferencesHelper _notificationPreferencesHelper;

	@Reference
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Reference
	private UserLocalService _userLocalService;

}