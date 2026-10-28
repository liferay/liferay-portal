/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.messaging;

import com.liferay.osb.faro.model.FaroPreferences;
import com.liferay.osb.faro.service.FaroPreferencesLocalService;
import com.liferay.osb.faro.web.internal.helper.AccountLifecycleTriggerType;
import com.liferay.osb.faro.web.internal.helper.EmailReportHelper;
import com.liferay.osb.faro.web.internal.helper.NotificationHelper;
import com.liferay.osb.faro.web.internal.model.preferences.AccountLifecycleNotificationPreferences;
import com.liferay.osb.faro.web.internal.model.preferences.EmailReportPreferences;
import com.liferay.osb.faro.web.internal.model.preferences.SegmentNotificationPreferences;
import com.liferay.osb.faro.web.internal.model.preferences.WorkspacePreferences;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rachael Koestartyo
 */
public abstract class BaseEmailReportMessageListener
	extends BaseMessageListener {

	protected abstract void activate(BundleContext bundleContext);

	protected abstract void deactivate();

	@Override
	protected void doReceive(Message message) throws Exception {
		Map<Long, Map<String, Map<Long, List<AccountLifecycleTriggerType>>>>
			accountLifecycleTriggerTypesMap = new LinkedHashMap<>();
		Map<Long, Map<String, List<Long>>> segmentUserIdsMap =
			new LinkedHashMap<>();

		for (FaroPreferences faroPreferences :
				faroPreferencesLocalService.getFaroPreferenceses(-1, -1)) {

			WorkspacePreferences workspacePreferences = null;

			try {
				workspacePreferences = JSONUtil.readValue(
					faroPreferences.getPreferences(),
					WorkspacePreferences.class);
			}
			catch (Exception exception) {
				_log.error(
					"Unable to read the preferences of group " +
						faroPreferences.getGroupId(),
					exception);

				continue;
			}

			Map<String, EmailReportPreferences> emailReportPreferencesMap =
				workspacePreferences.getEmailReportPreferences(null);

			for (Map.Entry<String, EmailReportPreferences> entry :
					emailReportPreferencesMap.entrySet()) {

				EmailReportPreferences emailReportPreferences =
					entry.getValue();

				if (!emailReportPreferences.getEnabled() ||
					!Objects.equals(
						emailReportPreferences.getFrequency(),
						getFrequency())) {

					continue;
				}

				try {
					emailReportHelper.sendEmail(
						entry.getKey(), getFrequency(),
						faroPreferences.getGroupId(),
						faroPreferences.getUserId());
				}
				catch (Exception exception) {
					_log.error(
						String.format(
							"Unable to send %s email for channel ID %s and " +
								"user ID %s",
							getFrequency(), entry.getKey(),
							faroPreferences.getUserId()),
						exception);
				}
			}

			_collectAccountLifecycleNotifications(
				accountLifecycleTriggerTypesMap, faroPreferences,
				workspacePreferences);
			_collectSegmentNotifications(
				faroPreferences, segmentUserIdsMap, workspacePreferences);
		}

		_sendNotifications(accountLifecycleTriggerTypesMap, segmentUserIdsMap);
	}

	protected abstract String getFrequency();

	@Reference
	protected EmailReportHelper emailReportHelper;

	@Reference
	protected FaroPreferencesLocalService faroPreferencesLocalService;

	@Reference
	protected NotificationHelper notificationHelper;

	@Reference
	protected SchedulerEngineHelper schedulerEngineHelper;

	@Reference
	protected TriggerFactory triggerFactory;

	private void _collectAccountLifecycleNotifications(
		Map<Long, Map<String, Map<Long, List<AccountLifecycleTriggerType>>>>
			accountLifecycleTriggerTypesMap,
		FaroPreferences faroPreferences,
		WorkspacePreferences workspacePreferences) {

		if (_isGroupScope(faroPreferences)) {
			return;
		}

		Map<String, AccountLifecycleNotificationPreferences>
			accountLifecycleNotificationPreferencesMap =
				workspacePreferences.getAccountLifecycleNotificationPreferences(
					null);

		for (Map.Entry<String, AccountLifecycleNotificationPreferences> entry :
				accountLifecycleNotificationPreferencesMap.entrySet()) {

			AccountLifecycleNotificationPreferences
				accountLifecycleNotificationPreferences = entry.getValue();

			if (!Objects.equals(
					accountLifecycleNotificationPreferences.getEmailFrequency(),
					getFrequency())) {

				continue;
			}

			List<AccountLifecycleTriggerType> accountLifecycleTriggerTypes =
				new ArrayList<>();

			for (AccountLifecycleTriggerType accountLifecycleTriggerType :
					AccountLifecycleTriggerType.values()) {

				if (!_isEnabled(
						accountLifecycleNotificationPreferences,
						accountLifecycleTriggerType)) {

					continue;
				}

				accountLifecycleTriggerTypes.add(accountLifecycleTriggerType);
			}

			if (accountLifecycleTriggerTypes.isEmpty()) {
				continue;
			}

			Map<String, Map<Long, List<AccountLifecycleTriggerType>>>
				accountLifecycleTriggerTypesById =
					accountLifecycleTriggerTypesMap.computeIfAbsent(
						faroPreferences.getGroupId(),
						groupId -> new LinkedHashMap<>());

			Map<Long, List<AccountLifecycleTriggerType>>
				accountLifecycleTriggerTypesByUserId =
					accountLifecycleTriggerTypesById.computeIfAbsent(
						entry.getKey(),
						accountLifecycleId -> new LinkedHashMap<>());

			accountLifecycleTriggerTypesByUserId.put(
				faroPreferences.getUserId(), accountLifecycleTriggerTypes);
		}
	}

	private void _collectSegmentNotifications(
		FaroPreferences faroPreferences,
		Map<Long, Map<String, List<Long>>> segmentUserIdsMap,
		WorkspacePreferences workspacePreferences) {

		if (_isGroupScope(faroPreferences)) {
			return;
		}

		Map<String, SegmentNotificationPreferences>
			segmentNotificationPreferencesMap =
				workspacePreferences.getSegmentNotificationPreferences(null);

		for (Map.Entry<String, SegmentNotificationPreferences> entry :
				segmentNotificationPreferencesMap.entrySet()) {

			SegmentNotificationPreferences segmentNotificationPreferences =
				entry.getValue();

			if (!GetterUtil.getBoolean(
					segmentNotificationPreferences.getNewMemberAdded()) ||
				!Objects.equals(
					segmentNotificationPreferences.getEmailFrequency(),
					getFrequency())) {

				continue;
			}

			Map<String, List<Long>> userIdsBySegmentId =
				segmentUserIdsMap.computeIfAbsent(
					faroPreferences.getGroupId(),
					groupId -> new LinkedHashMap<>());

			List<Long> userIds = userIdsBySegmentId.computeIfAbsent(
				entry.getKey(), segmentId -> new ArrayList<>());

			userIds.add(faroPreferences.getUserId());
		}
	}

	private boolean _isEnabled(
		AccountLifecycleNotificationPreferences
			accountLifecycleNotificationPreferences,
		AccountLifecycleTriggerType accountLifecycleTriggerType) {

		if (accountLifecycleTriggerType ==
				AccountLifecycleTriggerType.ACCOUNT_STAGE_CHANGES) {

			return GetterUtil.getBoolean(
				accountLifecycleNotificationPreferences.
					getAccountStageChanges());
		}
		else if (accountLifecycleTriggerType ==
					AccountLifecycleTriggerType.NET_NEW_PIPELINE_ACCOUNTS) {

			return GetterUtil.getBoolean(
				accountLifecycleNotificationPreferences.
					getNetNewPipelineAccounts());
		}
		else if (accountLifecycleTriggerType ==
					AccountLifecycleTriggerType.NEW_ACCOUNTS) {

			return GetterUtil.getBoolean(
				accountLifecycleNotificationPreferences.getNewAccounts());
		}
		else if (accountLifecycleTriggerType ==
					AccountLifecycleTriggerType.NEW_AT_RISK_ACCOUNTS) {

			return GetterUtil.getBoolean(
				accountLifecycleNotificationPreferences.getNewAtRiskAccounts());
		}
		else if (accountLifecycleTriggerType ==
					AccountLifecycleTriggerType.NEW_STALLED_ACCOUNTS) {

			return GetterUtil.getBoolean(
				accountLifecycleNotificationPreferences.
					getNewStalledAccounts());
		}

		return false;
	}

	private boolean _isGroupScope(FaroPreferences faroPreferences) {
		if (faroPreferences.getOwnerId() == faroPreferences.getGroupId()) {
			return true;
		}

		return false;
	}

	private void _sendAccountLifecycleNotifications(
		Map<String, Map<Long, List<AccountLifecycleTriggerType>>>
			accountLifecycleTriggerTypesById,
		NotificationHelper.NotificationContext notificationContext) {

		for (Map.Entry<String, Map<Long, List<AccountLifecycleTriggerType>>>
				entry : accountLifecycleTriggerTypesById.entrySet()) {

			try {
				notificationHelper.sendAccountLifecycleNotifications(
					entry.getKey(), entry.getValue(), getFrequency(),
					notificationContext);
			}
			catch (Exception exception) {
				_log.error(
					StringBundler.concat(
						"Unable to send ", getFrequency(),
						" notifications for account lifecycle ",
						entry.getKey()),
					exception);
			}
		}
	}

	private void _sendNotifications(
		Map<Long, Map<String, Map<Long, List<AccountLifecycleTriggerType>>>>
			accountLifecycleTriggerTypesMap,
		Map<Long, Map<String, List<Long>>> segmentUserIdsMap) {

		Set<Long> groupIds = new LinkedHashSet<>(
			accountLifecycleTriggerTypesMap.keySet());

		groupIds.addAll(segmentUserIdsMap.keySet());

		for (long groupId : groupIds) {
			NotificationHelper.NotificationContext notificationContext =
				notificationHelper.getNotificationContext(groupId);

			if (notificationContext == null) {
				continue;
			}

			_sendAccountLifecycleNotifications(
				accountLifecycleTriggerTypesMap.getOrDefault(
					groupId, Collections.emptyMap()),
				notificationContext);
			_sendSegmentNotifications(
				notificationContext,
				segmentUserIdsMap.getOrDefault(
					groupId, Collections.emptyMap()));
		}
	}

	private void _sendSegmentNotifications(
		NotificationHelper.NotificationContext notificationContext,
		Map<String, List<Long>> userIdsBySegmentId) {

		for (Map.Entry<String, List<Long>> segmentEntry :
				userIdsBySegmentId.entrySet()) {

			try {
				notificationHelper.sendSegmentNotifications(
					getFrequency(), notificationContext, segmentEntry.getKey(),
					segmentEntry.getValue());
			}
			catch (Exception exception) {
				_log.error(
					"Unable to send " + getFrequency() +
						" notifications for segment " + segmentEntry.getKey(),
					exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseEmailReportMessageListener.class);

}