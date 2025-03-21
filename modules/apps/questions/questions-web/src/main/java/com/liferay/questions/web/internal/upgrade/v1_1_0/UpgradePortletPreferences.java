/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.questions.web.internal.upgrade.v1_1_0;

import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.questions.web.internal.constants.QuestionsPortletKeys;

import jakarta.portlet.PortletPreferences;

/**
 * @author Marco Galluzzi
 */
public class UpgradePortletPreferences
	extends BasePortletPreferencesUpgradeProcess {

	public UpgradePortletPreferences(
		MBCategoryLocalService mbCategoryLocalService) {

		_mbCategoryLocalService = mbCategoryLocalService;
	}

	@Override
	protected String[] getPortletIds() {
		return new String[] {QuestionsPortletKeys.QUESTIONS + "%"};
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		String rootTopicExternalReferenceCode =
			_getRootTopicExternalReferenceCode(portletPreferences);

		if (rootTopicExternalReferenceCode != null) {
			portletPreferences.setValue(
				"rootTopicExternalReferenceCode",
				rootTopicExternalReferenceCode);
		}

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	private String _getRootTopicExternalReferenceCode(
		PortletPreferences portletPreferences) {

		String value = portletPreferences.getValue("rootTopicId", null);

		if (value == null) {
			return null;
		}

		long rootTopicId = GetterUtil.getLong(value);

		if (rootTopicId == MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {
			return StringPool.BLANK;
		}

		MBCategory mbCategory = _mbCategoryLocalService.fetchMBCategory(
			rootTopicId);

		if (mbCategory == null) {
			return StringPool.BLANK;
		}

		return mbCategory.getExternalReferenceCode();
	}

	private final MBCategoryLocalService _mbCategoryLocalService;

}