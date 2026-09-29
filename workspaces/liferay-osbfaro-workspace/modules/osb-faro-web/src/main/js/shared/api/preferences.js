import sendRequest from 'shared/util/request';
import {pickBy} from 'lodash';

export function addDistributionTab({
	distributionTab,
	distributionTabId,
	groupId,
	scope,
	segmentId,
}) {
	return sendRequest({
		data: pickBy({
			distributionCardTabPreferences: distributionTab,
			distributionTabId,
			individualSegmentId: segmentId,
			scope,
		}),
		method: 'POST',
		path: `main/${groupId}/preferences/distribution_tabs`,
	});
}

export function fetchDefaultChannelId({groupId, scope}) {
	return sendRequest({
		data: {scope},
		method: 'GET',
		path: `main/${groupId}/preferences/default_channel_id`,
	});
}

export function fetchLifecycleNotifications({groupId, lifecycleId}) {
	return sendRequest({
		data: {lifecycleId},
		method: 'GET',
		path: `main/${groupId}/preferences/lifecycle_notification`,
	});
}

export function fetchSegmentNotifications({groupId, segmentId}) {
	return sendRequest({
		data: {segmentId},
		method: 'GET',
		path: `main/${groupId}/preferences/segment_notification`,
	});
}

export function fetchUpgradeModalSeen({groupId, scope}) {
	return sendRequest({
		data: {scope},
		method: 'GET',
		path: `main/${groupId}/preferences/upgrade_modal_seen`,
	});
}

export function fetchDistributionTabs({groupId, scope, segmentId}) {
	return sendRequest({
		data: pickBy({individualSegmentId: segmentId, scope}),
		method: 'GET',
		path: `main/${groupId}/preferences/distribution_tabs`,
	});
}

export function fetchEmailReport({groupId}) {
	return sendRequest({
		method: 'GET',
		path: `main/${groupId}/preferences/email_report`,
	});
}

export function fetchPreferences() {
	return sendRequest({
		method: 'GET',
		path: 'main/preferences',
	});
}

export function updateEmailReport({channelId, groupId, report}) {
	return sendRequest({
		data: pickBy({channelId, ...report}),
		method: 'POST',
		path: `main/${groupId}/preferences/email_report`,
	});
}

export function updateLifecycleNotification({
	accountStageChanges = undefined,
	emailFrequency,
	groupId,
	lifecycleId,
	netNewPipelineAccounts = undefined,
	newAccounts = undefined,
	newAtRiskAccounts = undefined,
	newStalledAccounts = undefined,
}) {
	return sendRequest({
		data: pickBy(
			{
				accountStageChanges,
				emailFrequency,
				lifecycleId,
				netNewPipelineAccounts,
				newAccounts,
				newAtRiskAccounts,
				newStalledAccounts,
			},
			(value) => value !== undefined
		),
		method: 'POST',
		path: `main/${groupId}/preferences/lifecycle_notification`,
	});
}

export function updateSegmentNotification({
	emailFrequency,
	groupId,
	newMemberAdded = undefined,
	segmentId,
}) {
	return sendRequest({
		data: pickBy(
			{emailFrequency, newMemberAdded, segmentId},
			(value) => value !== undefined
		),
		method: 'POST',
		path: `main/${groupId}/preferences/segment_notification`,
	});
}

export function removeDistributionTab({
	distributionTabId,
	groupId,
	scope,
	segmentId,
}) {
	return sendRequest({
		data: pickBy({
			distributionTabId,
			individualSegmentId: segmentId,
			scope,
		}),
		method: 'DELETE',
		path: `main/${groupId}/preferences/distribution_tabs`,
	});
}

export function updateDefaultChannelId({defaultChannelId, groupId, scope}) {
	return sendRequest({
		data: {defaultChannelId, scope},
		method: 'POST',
		path: `main/${groupId}/preferences/default_channel_id`,
	});
}

export function updateUpgradeModalSeen({groupId, scope, upgradeModalSeen}) {
	return sendRequest({
		data: {scope, upgradeModalSeen},
		method: 'POST',
		path: `main/${groupId}/preferences/upgrade_modal_seen`,
	});
}
