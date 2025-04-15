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
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;

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
		"mvc.command.name=/site_admin/edit_recycle_bin"
	},
	service = MVCActionCommand.class
)
public class EditRecycleBinMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long liveGroupId = ParamUtil.getLong(actionRequest, "liveGroupId");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			Group.class.getName(), actionRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		Group liveGroup = _groupLocalService.getGroup(liveGroupId);

		UnicodeProperties typeSettingsUnicodeProperties =
			liveGroup.getTypeSettingsProperties();

		boolean trashEnabled = ParamUtil.getBoolean(
			actionRequest, "trashEnabled",
			GetterUtil.getBoolean(
				typeSettingsUnicodeProperties.getProperty("trashEnabled"),
				true));

		typeSettingsUnicodeProperties.setProperty(
			"trashEnabled", String.valueOf(trashEnabled));

		int trashEntriesMaxAgeCompany = PrefsPropsUtil.getInteger(
			themeDisplay.getCompanyId(), PropsKeys.TRASH_ENTRIES_MAX_AGE);

		int trashEntriesMaxAgeGroup = ParamUtil.getInteger(
			actionRequest, "trashEntriesMaxAge");

		if (trashEntriesMaxAgeGroup <= 0) {
			trashEntriesMaxAgeGroup = GetterUtil.getInteger(
				typeSettingsUnicodeProperties.getProperty("trashEntriesMaxAge"),
				trashEntriesMaxAgeCompany);
		}

		if (trashEntriesMaxAgeGroup != trashEntriesMaxAgeCompany) {
			typeSettingsUnicodeProperties.setProperty(
				"trashEntriesMaxAge",
				String.valueOf(GetterUtil.getInteger(trashEntriesMaxAgeGroup)));
		}
		else {
			typeSettingsUnicodeProperties.remove("trashEntriesMaxAge");
		}

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