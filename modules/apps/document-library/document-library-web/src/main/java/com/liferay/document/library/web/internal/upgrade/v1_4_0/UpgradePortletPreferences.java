/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.upgrade.v1_4_0;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

import java.util.Objects;

/**
 * @author Chaitanya Sammetla
 */
public class UpgradePortletPreferences
	extends com.liferay.document.library.web.internal.upgrade.v1_1_0.
				UpgradePortletPreferences {

	public UpgradePortletPreferences(
		DLAppLocalService dlAppLocalService,
		GroupLocalService groupLocalService,
		RepositoryLocalService repositoryLocalService) {

		super(dlAppLocalService, groupLocalService, repositoryLocalService);

		_groupLocalService = groupLocalService;
	}

	@Override
	protected String[] getPortletIds() {
		return new String[] {
			DLPortletKeys.DOCUMENT_LIBRARY, DLPortletKeys.MEDIA_GALLERY_DISPLAY
		};
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		xml = super.upgradePreferences(
			companyId, ownerId, ownerType, plid, portletId, xml);

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		String selectedGroupExternalReferenceCode = portletPreferences.getValue(
			"selectedGroupExternalReferenceCode", null);

		if (Validator.isNull(selectedGroupExternalReferenceCode)) {
			return xml;
		}

		Group group = null;

		if (ownerType == PortletKeys.PREFS_OWNER_TYPE_GROUP) {
			group = _groupLocalService.fetchGroup(ownerId);
		}
		else if (ownerType == PortletKeys.PREFS_OWNER_TYPE_LAYOUT) {
			Object[] layout = getLayout(plid);

			if (layout != null) {
				group = _groupLocalService.fetchGroup(
					GetterUtil.getLong(layout[0]));
			}
		}

		if ((group != null) && group.isStagingGroup()) {
			group = group.getLiveGroup();
		}

		if ((group == null) ||
			!Objects.equals(
				selectedGroupExternalReferenceCode,
				group.getExternalReferenceCode())) {

			return xml;
		}

		portletPreferences.reset("selectedGroupExternalReferenceCode");

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	private final GroupLocalService _groupLocalService;

}