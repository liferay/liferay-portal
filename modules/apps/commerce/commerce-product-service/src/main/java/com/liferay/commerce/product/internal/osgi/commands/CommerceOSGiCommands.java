/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.osgi.commands;

import com.liferay.commerce.product.constants.CPInstanceConstants;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.osgi.util.osgi.commands.OSGiCommands;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"osgi.command.function=createProducts", "osgi.command.scope=commerce"
	},
	service = OSGiCommands.class
)
public class CommerceOSGiCommands implements OSGiCommands {

	public void createProducts(long groupId, String prefix, int quantity)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		User user = _userLocalService.getGuestUser(group.getCompanyId());

		List<CommerceCatalog> commerceCatalogs =
			_commerceCatalogLocalService.getCommerceCatalogs(
				user.getCompanyId(), true);

		CommerceCatalog commerceCatalog = commerceCatalogs.get(0);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setCompanyId(user.getCompanyId());
		serviceContext.setScopeGroupId(groupId);
		serviceContext.setTimeZone(user.getTimeZone());
		serviceContext.setUserId(user.getUserId());

		Calendar displayCalendar = CalendarFactoryUtil.getCalendar(
			serviceContext.getTimeZone());

		displayCalendar.add(Calendar.YEAR, -1);

		int displayDateMonth = displayCalendar.get(Calendar.MONTH);
		int displayDateDay = displayCalendar.get(Calendar.DAY_OF_MONTH);
		int displayDateYear = displayCalendar.get(Calendar.YEAR);
		int displayDateHour = displayCalendar.get(Calendar.HOUR);
		int displayDateMinute = displayCalendar.get(Calendar.MINUTE);
		int displayDateAmPm = displayCalendar.get(Calendar.AM_PM);

		if (displayDateAmPm == Calendar.PM) {
			displayDateHour += 12;
		}

		Map<Locale, String> titleMap = new HashMap<>();

		for (int i = 0; i < quantity; i++) {
			String title = prefix + i;

			titleMap.put(LocaleUtil.US, title);

			_cpDefinitionLocalService.addCPDefinition(
				null, user.getUserId(), commerceCatalog.getGroupId(), titleMap,
				null, null, null, null, null, null, "simple", true, false,
				false, false, 0, 0, 0, 0, 0, 0, false, false, null, true,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, 0, 0, 0, 0, 0, true,
				CPInstanceConstants.DEFAULT_SKU, false, 0, null, null, 0, false,
				1, null, null, 0, WorkflowConstants.STATUS_DRAFT,
				serviceContext);
		}
	}

	@Reference
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}