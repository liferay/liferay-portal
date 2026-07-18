/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portlet.social.service.base.SocialActivitySettingServiceBaseImpl;
import com.liferay.social.kernel.model.SocialActivityCounterDefinition;
import com.liferay.social.kernel.model.SocialActivityDefinition;
import com.liferay.social.kernel.model.SocialActivitySetting;
import com.liferay.social.kernel.service.permission.SocialActivityPermissionUtil;
import com.liferay.social.kernel.util.comparator.SocialActivityDefinitionNameComparator;

import java.util.Collections;
import java.util.List;

/**
 * @author Zsolt Berentey
 */
public class SocialActivitySettingServiceImpl
	extends SocialActivitySettingServiceBaseImpl {

	@Override
	public SocialActivityDefinition getActivityDefinition(
			long groupId, String className, int activityType)
		throws PortalException {

		SocialActivityPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.VIEW);

		return socialActivitySettingLocalService.getActivityDefinition(
			groupId, className, activityType);
	}

	@Override
	public List<SocialActivityDefinition> getActivityDefinitions(
			long groupId, String className)
		throws PortalException {

		SocialActivityPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.VIEW);

		return socialActivitySettingLocalService.getActivityDefinitions(
			groupId, className);
	}

	@Override
	public List<SocialActivitySetting> getActivitySettings(long groupId)
		throws PortalException {

		SocialActivityPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.VIEW);

		return socialActivitySettingPersistence.findByG_A(groupId, 0);
	}

	@Override
	public JSONArray getJSONActivityDefinitions(long groupId, String className)
		throws PortalException {

		SocialActivityPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.VIEW);

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		List<SocialActivityDefinition> activityDefinitions =
			socialActivitySettingLocalService.getActivityDefinitions(
				groupId, className);

		Collections.sort(
			activityDefinitions,
			new SocialActivityDefinitionNameComparator(
				LocaleUtil.getMostRelevantLocale()));

		for (SocialActivityDefinition activityDefinition :
				activityDefinitions) {

			if (!activityDefinition.isCountersEnabled()) {
				continue;
			}

			JSONObject activityDefinitionJSONObject =
				JSONFactoryUtil.createJSONObject(
					JSONFactoryUtil.looseSerialize(activityDefinition));

			JSONArray activityCounterDefinitionsJSONArray =
				JSONFactoryUtil.createJSONArray();

			for (SocialActivityCounterDefinition activityCounterDefinition :
					activityDefinition.getActivityCounterDefinitions()) {

				JSONObject activityCounterDefinitionJSONObject =
					JSONFactoryUtil.createJSONObject(
						JSONFactoryUtil.looseSerialize(
							activityCounterDefinition));

				activityCounterDefinitionsJSONArray.put(
					activityCounterDefinitionJSONObject);
			}

			activityDefinitionJSONObject.put(
				"counters", activityCounterDefinitionsJSONArray);

			jsonArray.put(activityDefinitionJSONObject);
		}

		return jsonArray;
	}

	@Override
	public void updateActivitySetting(
			long groupId, String className, boolean enabled)
		throws PortalException {

		SocialActivityPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.CONFIGURATION);

		socialActivitySettingLocalService.updateActivitySetting(
			groupId, className, enabled);
	}

	@Override
	public void updateActivitySetting(
			long groupId, String className, int activityType,
			SocialActivityCounterDefinition activityCounterDefinition)
		throws PortalException {

		SocialActivityPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.CONFIGURATION);

		socialActivitySettingLocalService.updateActivitySetting(
			groupId, className, activityType, activityCounterDefinition);
	}

	@Override
	public void updateActivitySettings(
			long groupId, String className, int activityType,
			List<SocialActivityCounterDefinition> activityCounterDefinitions)
		throws PortalException {

		SocialActivityPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.CONFIGURATION);

		socialActivitySettingLocalService.updateActivitySettings(
			groupId, className, activityType, activityCounterDefinitions);
	}

}