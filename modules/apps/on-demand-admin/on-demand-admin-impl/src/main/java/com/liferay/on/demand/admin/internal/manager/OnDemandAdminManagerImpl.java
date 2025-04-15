/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.on.demand.admin.internal.manager;

import com.liferay.on.demand.admin.constants.OnDemandAdminConstants;
import com.liferay.on.demand.admin.manager.OnDemandAdminManager;
import com.liferay.on.demand.admin.ticket.generator.OnDemandAdminTicketGenerator;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.PortletRequest;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = OnDemandAdminManager.class)
public class OnDemandAdminManagerImpl implements OnDemandAdminManager {

	@Override
	public void cleanUpOnDemandAdminUsers(Date olderThanDate)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			_userLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property createDateProperty = PropertyFactoryUtil.forName(
					"createDate");

				dynamicQuery.add(createDateProperty.lt(olderThanDate));

				Property screenNameProperty = PropertyFactoryUtil.forName(
					"screenName");

				dynamicQuery.add(
					screenNameProperty.like(
						OnDemandAdminConstants.
							SCREEN_NAME_PREFIX_ON_DEMAND_ADMIN +
								StringPool.PERCENT));
			});
		actionableDynamicQuery.setPerformActionMethod(
			(User user) -> _userLocalService.deleteUser(user));

		actionableDynamicQuery.performActions();
	}

	@Override
	public String getLoginURL(
			Company company, PortletRequest portletRequest, long userId)
		throws PortalException {

		StringBundler sb = new StringBundler(4);

		boolean secure = _portal.isSecure(
			_portal.getHttpServletRequest(portletRequest));

		sb.append(
			_portal.getPortalURL(
				company.getVirtualHostname(),
				_portal.getPortalServerPort(secure), secure));

		sb.append(_portal.getPathContext());
		sb.append("?ticketKey=");

		Ticket ticket = _onDemandAdminTicketGenerator.generate(
			company, ParamUtil.getString(portletRequest, "justification"),
			userId);

		sb.append(ticket.getKey());

		return sb.toString();
	}

	@Override
	public boolean isOnDemandAdminUser(User user) {
		if ((user != null) &&
			StringUtil.startsWith(
				user.getScreenName(),
				OnDemandAdminConstants.SCREEN_NAME_PREFIX_ON_DEMAND_ADMIN)) {

			return true;
		}

		return false;
	}

	@Reference
	private OnDemandAdminTicketGenerator _onDemandAdminTicketGenerator;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}