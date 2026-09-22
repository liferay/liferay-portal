/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.messaging;

import com.liferay.osb.faro.model.FaroPreferences;
import com.liferay.osb.faro.service.FaroPreferencesLocalService;
import com.liferay.osb.faro.web.internal.helper.EmailReportHelper;
import com.liferay.osb.faro.web.internal.helper.LifecycleTriggerType;
import com.liferay.osb.faro.web.internal.helper.NotificationHelper;
import com.liferay.osb.faro.web.internal.model.preferences.EmailReportPreferences;
import com.liferay.osb.faro.web.internal.model.preferences.LifecycleNotificationPreferences;
import com.liferay.osb.faro.web.internal.model.preferences.SegmentNotificationPreferences;
import com.liferay.osb.faro.web.internal.model.preferences.WorkspacePreferences;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
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
		Map<Long, Map<String, Map<Long, List<LifecycleTriggerType>>>>
			lifecycleTriggerTypesMap = new LinkedHashMap<>();
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

			_collectLifecycleNotifications(
				faroPreferences, lifecycleTriggerTypesMap,
				workspacePreferences);
			_collectSegmentNotifications(
				faroPreferences, segmentUserIdsMap, workspacePreferences);
		}

		_sendNotifications(lifecycleTriggerTypesMap, segmentUserIdsMap);
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

	private void _collectLifecycleNotifications(
		FaroPreferences faroPreferences,
		Map<Long, Map<String, Map<Long, List<LifecycleTriggerType>>>>
			lifecycleTriggerTypesMap,
		WorkspacePreferences workspacePreferences) {

		if (_isGroupScope(faroPreferences)) {
			return;
		}

		Map<String, LifecycleNotificationPreferences>
			lifecycleNotificationPreferencesMap =
				workspacePreferences.getLifecycleNotificationPreferences(null);

		for (Map.Entry<String, LifecycleNotificationPreferences> entry :
				lifecycleNotificationPreferencesMap.entrySet()) {

			LifecycleNotificationPreferences lifecycleNotificationPreferences =
				entry.getValue();

			if (!Objects.equals(
					lifecycleNotificationPreferences.getEmailFrequency(),
					getFrequency())) {

				continue;
			}

			List<LifecycleTriggerType> lifecycleTriggerTypes =
				new ArrayList<>();

			for (LifecycleTriggerType lifecycleTriggerType :
					LifecycleTriggerType.values()) {

				if (!_isEnabled(
						lifecycleNotificationPreferences,
						lifecycleTriggerType)) {

					continue;
				}

				lifecycleTriggerTypes.add(lifecycleTriggerType);
			}

			if (lifecycleTriggerTypes.isEmpty()) {
				continue;
			}

			Map<String, Map<Long, List<LifecycleTriggerType>>>
				lifecycleTriggerTypesById =
					lifecycleTriggerTypesMap.computeIfAbsent(
						faroPreferences.getGroupId(),
						groupId -> new LinkedHashMap<>());

			Map<Long, List<LifecycleTriggerType>>
				lifecycleTriggerTypesByUserId =
					lifecycleTriggerTypesById.computeIfAbsent(
						entry.getKey(), lifecycleId -> new LinkedHashMap<>());

			lifecycleTriggerTypesByUserId.put(
				faroPreferences.getUserId(), lifecycleTriggerTypes);
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
		LifecycleNotificationPreferences lifecycleNotificationPreferences,
		LifecycleTriggerType lifecycleTriggerType) {

		if (lifecycleTriggerType ==
				LifecycleTriggerType.ACCOUNT_STAGE_CHANGES) {

			return GetterUtil.getBoolean(
				lifecycleNotificationPreferences.getAccountStageChanges());
		}
		else if (lifecycleTriggerType ==
					LifecycleTriggerType.NET_NEW_PIPELINE_ACCOUNTS) {

			return GetterUtil.getBoolean(
				lifecycleNotificationPreferences.getNetNewPipelineAccounts());
		}
		else if (lifecycleTriggerType == LifecycleTriggerType.NEW_ACCOUNTS) {
			return GetterUtil.getBoolean(
				lifecycleNotificationPreferences.getNewAccounts());
		}
		else if (lifecycleTriggerType ==
					LifecycleTriggerType.NEW_AT_RISK_ACCOUNTS) {

			return GetterUtil.getBoolean(
				lifecycleNotificationPreferences.getNewAtRiskAccounts());
		}
		else if (lifecycleTriggerType ==
					LifecycleTriggerType.NEW_STALLED_ACCOUNTS) {

			return GetterUtil.getBoolean(
				lifecycleNotificationPreferences.getNewStalledAccounts());
		}

		return false;
	}

	private boolean _isGroupScope(FaroPreferences faroPreferences) {
		if (faroPreferences.getOwnerId() == faroPreferences.getGroupId()) {
			return true;
		}

		return false;
	}

	private void _sendLifecycleNotifications(
		Map<String, Map<Long, List<LifecycleTriggerType>>>
			lifecycleTriggerTypesById,
		NotificationHelper.NotificationContext notificationContext) {

		for (Map.Entry<String, Map<Long, List<LifecycleTriggerType>>>
				lifecycleEntry : lifecycleTriggerTypesById.entrySet()) {

			try {
				notificationHelper.sendLifecycleNotifications(
					getFrequency(), lifecycleEntry.getKey(),
					lifecycleEntry.getValue(), notificationContext);
			}
			catch (Exception exception) {
				_log.error(
					"Unable to send " + getFrequency() +
						" notifications for lifecycle " +
							lifecycleEntry.getKey(),
					exception);
			}
		}
	}

	private void _sendNotifications(
		Map<Long, Map<String, Map<Long, List<LifecycleTriggerType>>>>
			lifecycleTriggerTypesMap,
		Map<Long, Map<String, List<Long>>> segmentUserIdsMap) {

		Set<Long> groupIds = new LinkedHashSet<>(
			lifecycleTriggerTypesMap.keySet());

		groupIds.addAll(segmentUserIdsMap.keySet());

		for (long groupId : groupIds) {
			NotificationHelper.NotificationContext notificationContext =
				notificationHelper.getNotificationContext(groupId);

			if (notificationContext == null) {
				continue;
			}

			_sendLifecycleNotifications(
				lifecycleTriggerTypesMap.getOrDefault(
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