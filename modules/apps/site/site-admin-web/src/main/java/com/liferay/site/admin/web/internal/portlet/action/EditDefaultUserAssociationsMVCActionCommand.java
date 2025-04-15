/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SITE_SETTINGS,
		"mvc.command.name=/site_admin/edit_default_user_associations"
	},
	service = MVCActionCommand.class
)
public class EditDefaultUserAssociationsMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long liveGroupId = ParamUtil.getLong(actionRequest, "liveGroupId");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			Group.class.getName(), actionRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		Group liveGroup = _groupLocalService.getGroup(liveGroupId);

		UnicodeProperties typeSettingsUnicodeProperties =
			liveGroup.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"defaultSiteRoleIds",
			ListUtil.toString(
				ActionUtil.getRoleIds(actionRequest), StringPool.BLANK));
		typeSettingsUnicodeProperties.setProperty(
			"defaultTeamIds",
			ListUtil.toString(
				ActionUtil.getTeamIds(actionRequest), StringPool.BLANK));

		UnicodeProperties formTypeSettingsUnicodeProperties =
			PropertiesParamUtil.getProperties(
				actionRequest, "TypeSettingsProperties--");

		typeSettingsUnicodeProperties.putAll(formTypeSettingsUnicodeProperties);

		if (liveGroup.hasStagingGroup()) {
			Group stagingGroup = liveGroup.getStagingGroup();

			UnicodeProperties stagedGroupTypeSettingsUnicodeProperties =
				stagingGroup.getTypeSettingsProperties();

			stagedGroupTypeSettingsUnicodeProperties.putAll(
				formTypeSettingsUnicodeProperties);

			_groupService.updateGroup(
				stagingGroup.getGroupId(),
				stagedGroupTypeSettingsUnicodeProperties.toString());
		}

		_groupService.updateGroup(
			liveGroup.getGroupId(), typeSettingsUnicodeProperties.toString());
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private GroupService _groupService;

}