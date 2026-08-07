/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;

/**
 * @author Fábio Alves
 */
public class CMPLicenseUtil {

	public static void checkResources(
		long companyId, GroupLocalService groupLocalService,
		LayoutLocalService layoutLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService) {

		boolean appEnabled = LicenseManagerUtil.isAppEnabled(App.CMP);

		_checkLayouts(
			appEnabled, companyId, groupLocalService, layoutLocalService);
		_checkObjectDefinitions(
			appEnabled, companyId, objectDefinitionLocalService);
	}

	private static void _checkLayouts(
		boolean appEnabled, long companyId, GroupLocalService groupLocalService,
		LayoutLocalService layoutLocalService) {

		Group group = groupLocalService.fetchGroup(
			companyId, GroupConstants.CMS);

		if (group == null) {
			return;
		}

		for (String friendlyURL : _CMP_LAYOUT_FRIENDLY_URLS) {
			Layout layout = layoutLocalService.fetchLayoutByFriendlyURL(
				group.getGroupId(), false, friendlyURL);

			if ((layout == null) || (layout.isHidden() != appEnabled)) {
				continue;
			}

			layout.setHidden(!appEnabled);

			layoutLocalService.updateLayout(layout);
		}
	}

	private static void _checkObjectDefinitions(
		boolean appEnabled, long companyId,
		ObjectDefinitionLocalService objectDefinitionLocalService) {

		List<ObjectDefinition> objectDefinitions =
			objectDefinitionLocalService.getObjectDefinitions(
				companyId, WorkflowConstants.STATUS_APPROVED);

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			if (!objectDefinition.isCMP() ||
				(objectDefinition.isActive() == appEnabled)) {

				continue;
			}

			objectDefinition.setActive(appEnabled);

			objectDefinition =
				objectDefinitionLocalService.updateObjectDefinition(
					objectDefinition);

			if (appEnabled) {
				objectDefinitionLocalService.deployObjectDefinition(
					objectDefinition);
			}
			else {
				objectDefinitionLocalService.deployInactiveObjectDefinition(
					objectDefinition);
			}
		}
	}

	private static final String[] _CMP_LAYOUT_FRIENDLY_URLS = {
		"/planning", "/projects", "/tasks"
	};

}