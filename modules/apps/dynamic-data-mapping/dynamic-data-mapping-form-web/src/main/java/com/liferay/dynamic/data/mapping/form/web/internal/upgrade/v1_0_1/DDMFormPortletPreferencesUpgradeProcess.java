/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.upgrade.v1_0_1;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.portlet.PortletPreferences;

/**
 * @author Paulo Albuquerque
 */
public class DDMFormPortletPreferencesUpgradeProcess
	extends BasePortletPreferencesUpgradeProcess {

	public DDMFormPortletPreferencesUpgradeProcess(
		DDMFormInstanceLocalService ddmFormInstanceLocalService,
		DDMStructureLocalService ddmStructureLocalService,
		GroupLocalService groupLocalService) {

		_ddmFormInstanceLocalService = ddmFormInstanceLocalService;
		_ddmStructureLocalService = ddmStructureLocalService;
		_groupLocalService = groupLocalService;
	}

	@Override
	protected String[] getPortletIds() {
		return new String[] {
			DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM + "_INSTANCE_%"
		};
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		long formInstanceId = GetterUtil.getLong(
			portletPreferences.getValue("formInstanceId", StringPool.BLANK));

		if (formInstanceId == 0) {
			return xml;
		}

		DDMFormInstance ddmFormInstance =
			_ddmFormInstanceLocalService.fetchDDMFormInstance(formInstanceId);

		if (ddmFormInstance == null) {
			return xml;
		}

		DDMStructure ddmStructure = _ddmStructureLocalService.getDDMStructure(
			ddmFormInstance.getStructureId());

		portletPreferences.setValue(
			"ddmStructureExternalReferenceCode",
			ddmStructure.getExternalReferenceCode());

		Group group = _groupLocalService.getGroup(ddmStructure.getGroupId());

		portletPreferences.setValue(
			"groupExternalReferenceCode", group.getExternalReferenceCode());

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	private final DDMFormInstanceLocalService _ddmFormInstanceLocalService;
	private final DDMStructureLocalService _ddmStructureLocalService;
	private final GroupLocalService _groupLocalService;

}