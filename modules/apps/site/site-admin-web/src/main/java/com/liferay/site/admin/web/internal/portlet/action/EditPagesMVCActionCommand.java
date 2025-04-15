/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.sites.kernel.util.Sites;

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
		"mvc.command.name=/site_admin/edit_pages"
	},
	service = MVCActionCommand.class
)
public class EditPagesMVCActionCommand
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

		String customJspServletContextName = ParamUtil.getString(
			actionRequest, "customJspServletContextName",
			typeSettingsUnicodeProperties.getProperty(
				"customJspServletContextName"));

		typeSettingsUnicodeProperties.setProperty(
			"customJspServletContextName", customJspServletContextName);

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

		liveGroup = _groupService.updateGroup(
			liveGroup.getGroupId(), typeSettingsUnicodeProperties.toString());

		ActionUtil.updateLayoutSetPrototypesLinks(
			actionRequest, liveGroup, _sites);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private GroupService _groupService;

	@Reference
	private Sites _sites;

}