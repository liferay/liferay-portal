/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.web.internal.instance.lifecycle;

import com.liferay.digital.signature.constants.DigitalSignaturePortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class AddSignDigitalSignatureLayoutPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		Group group = _groupLocalService.fetchFriendlyURLGroup(
			company.getCompanyId(),
			GroupConstants.DIGITAL_SIGNATURE_FRIENDLY_URL);

		if (group == null) {
			group = _groupLocalService.addGroup(
				StringPool.BLANK,
				_userLocalService.getGuestUserId(company.getCompanyId()),
				GroupConstants.DEFAULT_PARENT_GROUP_ID, null, 0,
				GroupConstants.DEFAULT_LIVE_GROUP_ID,
				HashMapBuilder.put(
					LocaleUtil.getDefault(), GroupConstants.DIGITAL_SIGNATURE
				).build(),
				null, GroupConstants.TYPE_SITE_PRIVATE, null, true,
				GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
				GroupConstants.DIGITAL_SIGNATURE_FRIENDLY_URL, false, false,
				true, null);
		}

		_addLayout(company.getCompanyId(), group.getGroupId());
	}

	private void _addLayout(long companyId, long groupId) throws Exception {
		Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			groupId, false, "/sign");

		if (layout != null) {
			return;
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAttribute(
			"layout.instanceable.allowed", Boolean.TRUE);
		serviceContext.setAttribute("layoutUpdateable", Boolean.FALSE);
		serviceContext.setScopeGroupId(groupId);

		long guestUserId = _userLocalService.getGuestUserId(companyId);

		serviceContext.setUserId(guestUserId);

		_layoutLocalService.addLayout(
			null, guestUserId, groupId, false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, "Sign", StringPool.BLANK,
			StringPool.BLANK,
			DigitalSignaturePortletKeys.SIGN_DIGITAL_SIGNATURE, true, "/sign",
			serviceContext);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private UserLocalService _userLocalService;

}