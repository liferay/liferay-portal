/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.web.internal.instance.lifecycle;

import com.liferay.calendar.constants.CalendarPortletKeys;
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
import com.liferay.portal.kernel.util.StringUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class AddDefaultSharedCalendarLayoutPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		Group group = _groupLocalService.fetchFriendlyURLGroup(
			company.getCompanyId(), GroupConstants.CALENDAR_FRIENDLY_URL);

		if (group == null) {
			group = _groupLocalService.addGroup(
				StringPool.BLANK,
				_userLocalService.getGuestUserId(company.getCompanyId()),
				GroupConstants.DEFAULT_PARENT_GROUP_ID, null, 0,
				GroupConstants.DEFAULT_LIVE_GROUP_ID,
				HashMapBuilder.put(
					LocaleUtil.getDefault(), GroupConstants.CALENDAR
				).build(),
				null, GroupConstants.TYPE_SITE_PRIVATE, null, true,
				GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
				GroupConstants.CALENDAR_FRIENDLY_URL, false, false, true, null);
		}

		Layout privateLayout = _getLayout(
			company.getCompanyId(), group.getGroupId(), true);

		_updateLayout(privateLayout);

		Layout publicLayout = _getLayout(
			company.getCompanyId(), group.getGroupId(), false);

		_updateLayout(publicLayout);
	}

	private Layout _getLayout(
			long companyId, long groupId, boolean privateLayout)
		throws Exception {

		Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			groupId, privateLayout, "/shared");

		if (layout != null) {
			return layout;
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

		return _layoutLocalService.addLayout(
			null, guestUserId, groupId, privateLayout,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, "Shared",
			StringPool.BLANK, StringPool.BLANK, CalendarPortletKeys.CALENDAR,
			true, "/shared", serviceContext);
	}

	private void _updateLayout(Layout layout) throws Exception {
		if (StringUtil.equals(layout.getType(), CalendarPortletKeys.CALENDAR)) {
			return;
		}

		layout.setType(CalendarPortletKeys.CALENDAR);

		_layoutLocalService.updateLayout(layout);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private UserLocalService _userLocalService;

}