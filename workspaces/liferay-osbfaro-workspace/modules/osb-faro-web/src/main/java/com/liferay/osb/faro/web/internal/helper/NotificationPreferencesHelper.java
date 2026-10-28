/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.helper;

import com.liferay.osb.faro.model.FaroPreferences;
import com.liferay.osb.faro.service.FaroPreferencesLocalService;
import com.liferay.osb.faro.web.internal.model.preferences.WorkspacePreferences;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Caio Pinheiro
 */
@Component(service = NotificationPreferencesHelper.class)
public class NotificationPreferencesHelper {

	public void removeAccountLifecycleNotificationPreferences(
		long groupId, Collection<String> keys) {

		for (FaroPreferences faroPreferences :
				_faroPreferencesLocalService.getFaroPreferencesByGroupId(
					groupId)) {

			try {
				WorkspacePreferences workspacePreferences = JSONUtil.readValue(
					faroPreferences.getPreferences(),
					WorkspacePreferences.class);

				if (workspacePreferences.
						removeAccountLifecycleNotificationPreferences(keys)) {

					_faroPreferencesLocalService.savePreferences(
						faroPreferences.getUserId(), groupId,
						faroPreferences.getOwnerId(),
						JSONUtil.writeValueAsString(workspacePreferences));
				}
			}
			catch (Exception exception) {
				_log.error(
					"Unable to remove account lifecycle notification " +
						"preferences of group " + groupId,
					exception);
			}
		}
	}

	public void removeSegmentNotificationPreferences(
		long groupId, Collection<String> segmentIds) {

		for (FaroPreferences faroPreferences :
				_faroPreferencesLocalService.getFaroPreferencesByGroupId(
					groupId)) {

			try {
				WorkspacePreferences workspacePreferences = JSONUtil.readValue(
					faroPreferences.getPreferences(),
					WorkspacePreferences.class);

				if (workspacePreferences.removeSegmentNotificationPreferences(
						segmentIds)) {

					_faroPreferencesLocalService.savePreferences(
						faroPreferences.getUserId(), groupId,
						faroPreferences.getOwnerId(),
						JSONUtil.writeValueAsString(workspacePreferences));
				}
			}
			catch (Exception exception) {
				_log.error(
					"Unable to remove segment notification preferences of " +
						"group " + groupId,
					exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NotificationPreferencesHelper.class);

	@Reference
	private FaroPreferencesLocalService _faroPreferencesLocalService;

}