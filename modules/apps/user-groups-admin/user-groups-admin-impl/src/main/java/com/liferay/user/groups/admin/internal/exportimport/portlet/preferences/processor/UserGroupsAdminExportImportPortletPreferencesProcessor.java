/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.internal.exportimport.portlet.preferences.processor;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.user.groups.admin.constants.UserGroupsAdminPortletKeys;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = "jakarta.portlet.name=" + UserGroupsAdminPortletKeys.USER_GROUPS_ADMIN,
	service = ExportImportPortletPreferencesProcessor.class
)
public class UserGroupsAdminExportImportPortletPreferencesProcessor
	implements ExportImportPortletPreferencesProcessor {

	@Override
	public List<Capability> getExportCapabilities() {
		return ListUtil.fromArray(
			_userGroupsAdminPortletDisplayTemplateExportCapability);
	}

	@Override
	public List<Capability> getImportCapabilities() {
		return ListUtil.fromArray(
			_userGroupsAdminPortletDisplayTemplateImportCapability);
	}

	@Override
	public PortletPreferences processExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		return null;
	}

	@Override
	public PortletPreferences processImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		return null;
	}

	@Reference(target = "(name=UserGroupsAdminExportCapability)")
	private Capability _userGroupsAdminPortletDisplayTemplateExportCapability;

	@Reference(target = "(name=UserGroupsAdminImportCapability)")
	private Capability _userGroupsAdminPortletDisplayTemplateImportCapability;

}