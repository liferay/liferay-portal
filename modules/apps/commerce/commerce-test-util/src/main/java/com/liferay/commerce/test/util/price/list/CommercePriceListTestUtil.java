/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.test.util.price.list;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalServiceUtil;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListAccountRelLocalServiceUtil;
import com.liferay.commerce.price.list.service.CommercePriceListChannelRelLocalServiceUtil;
import com.liferay.commerce.price.list.service.CommercePriceListCommerceAccountGroupRelLocalServiceUtil;
import com.liferay.commerce.price.list.service.CommercePriceListLocalServiceUtil;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Zoltán Takács
 * @author Ethan Bustad
 * @author Luca Pellizzon
 */
public class CommercePriceListTestUtil {

	public static CommercePriceList addAccountAndChannelPriceList(
			long groupId, long commerceAccountId, long commerceChannelId,
			String type)
		throws Exception {

		CommercePriceList commercePriceList = addCommercePriceList(
			groupId, false, type, 1.0);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		CommercePriceListAccountRelLocalServiceUtil.
			addCommercePriceListAccountRel(
				serviceContext.getUserId(),
				commercePriceList.getCommercePriceListId(), commerceAccountId,
				0, serviceContext);

		CommercePriceListChannelRelLocalServiceUtil.
			addCommercePriceListChannelRel(
				serviceContext.getUserId(),
				commercePriceList.getCommercePriceListId(), commerceChannelId,
				0, serviceContext);

		return commercePriceList;
	}

	public static CommercePriceList addAccountGroupAndChannelPriceList(
			long groupId, long[] commerceAccountGroupIds,
			long commerceChannelId, String type)
		throws Exception {

		CommercePriceList commercePriceList = addCommercePriceList(
			groupId, false, type, 1.0);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		for (long commerceAccountGroupId : commerceAccountGroupIds) {
			CommercePriceListCommerceAccountGroupRelLocalServiceUtil.
				addCommercePriceListCommerceAccountGroupRel(
					serviceContext.getUserId(),
					commercePriceList.getCommercePriceListId(),
					commerceAccountGroupId, 0, serviceContext);
		}

		CommercePriceListChannelRelLocalServiceUtil.
			addCommercePriceListChannelRel(
				serviceContext.getUserId(),
				commercePriceList.getCommercePriceListId(), commerceChannelId,
				0, serviceContext);

		return commercePriceList;
	}

	public static CommercePriceList addAccountGroupPriceList(
			long groupId, long[] commerceAccountGroupIds, String type)
		throws Exception {

		CommercePriceList commercePriceList = addCommercePriceList(
			groupId, false, type, 1.0);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		for (long commerceAccountGroupId : commerceAccountGroupIds) {
			CommercePriceListCommerceAccountGroupRelLocalServiceUtil.
				addCommercePriceListCommerceAccountGroupRel(
					serviceContext.getUserId(),
					commercePriceList.getCommercePriceListId(),
					commerceAccountGroupId, 0, serviceContext);
		}

		return commercePriceList;
	}

	public static CommercePriceList addAccountGroupsToPriceList(
			long groupId, long[] commerceAccountGroupIds,
			long commercePriceListId)
		throws PortalException {

		CommercePriceList commercePriceList =
			CommercePriceListLocalServiceUtil.getCommercePriceList(
				commercePriceListId);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		for (long commerceAccountGroupId : commerceAccountGroupIds) {
			CommercePriceListCommerceAccountGroupRelLocalServiceUtil.
				addCommercePriceListCommerceAccountGroupRel(
					serviceContext.getUserId(),
					commercePriceList.getCommercePriceListId(),
					commerceAccountGroupId, 0, serviceContext);
		}

		return commercePriceList;
	}

	public static CommercePriceList addAccountPriceList(
			long groupId, long commerceAccountId, String type)
		throws Exception {

		CommercePriceList commercePriceList = addCommercePriceList(
			groupId, false, type, 1.0);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		CommercePriceListAccountRelLocalServiceUtil.
			addCommercePriceListAccountRel(
				serviceContext.getUserId(),
				commercePriceList.getCommercePriceListId(), commerceAccountId,
				0, serviceContext);

		return commercePriceList;
	}

	public static CommercePriceList addAccountToPriceList(
			long groupId, long commerceAccountId, long commercePriceListId)
		throws PortalException {

		CommercePriceList commercePriceList =
			CommercePriceListLocalServiceUtil.getCommercePriceList(
				commercePriceListId);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		CommercePriceListAccountRelLocalServiceUtil.
			addCommercePriceListAccountRel(
				serviceContext.getUserId(),
				commercePriceList.getCommercePriceListId(), commerceAccountId,
				0, serviceContext);

		return commercePriceList;
	}

	public static CommercePriceList addChannelPriceList(
			long groupId, long commerceChannelId, String type)
		throws Exception {

		CommercePriceList commercePriceList = addCommercePriceList(
			groupId, false, type, 1.0);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		CommercePriceListChannelRelLocalServiceUtil.
			addCommercePriceListChannelRel(
				serviceContext.getUserId(),
				commercePriceList.getCommercePriceListId(), commerceChannelId,
				0, serviceContext);

		return commercePriceList;
	}

	public static CommercePriceList addChannelToPriceList(
			long groupId, long commerceChannelId, long commercePriceListId)
		throws PortalException {

		CommercePriceList commercePriceList =
			CommercePriceListLocalServiceUtil.getCommercePriceList(
				commercePriceListId);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		CommercePriceListChannelRelLocalServiceUtil.
			addCommercePriceListChannelRel(
				serviceContext.getUserId(),
				commercePriceList.getCommercePriceListId(), commerceChannelId,
				0, serviceContext);

		return commercePriceList;
	}

	public static CommercePriceList addCommercePriceList(
			long groupId, boolean catalogBasePriceList, String type,
			double priority)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		CommerceCurrency commerceCurrency =
			CommerceCurrencyLocalServiceUtil.fetchPrimaryCommerceCurrency(
				serviceContext.getCompanyId());

		if (commerceCurrency == null) {
			commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
				serviceContext.getCompanyId());
		}

		return addCommercePriceList(
			groupId, catalogBasePriceList, commerceCurrency.getCode(), type,
			priority);
	}

	public static CommercePriceList addCommercePriceList(
			long groupId, boolean catalogBasePriceList, String currencyCode,
			String type, double priority)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		User user = UserLocalServiceUtil.getGuestUser(
			serviceContext.getCompanyId());

		Calendar calendar = CalendarFactoryUtil.getCalendar(user.getTimeZone());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		return CommercePriceListLocalServiceUtil.addCommercePriceList(
			null, user.getUserId(), groupId, catalogBasePriceList, currencyCode,
			calendar.get(Calendar.DAY_OF_MONTH),
			calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
			calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR),
			calendar.get(Calendar.DAY_OF_MONTH),
			calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
			calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR),
			RandomTestUtil.randomString(), true, true, 0, priority, type,
			serviceContext);
	}

	public static CommercePriceList addCommercePriceList(
			String externalReferenceCode, long groupId, String currency,
			boolean netPrice, long parentCommercePriceListId, String name,
			Double priority, Boolean neverExpire, Date displayDate,
			Date expirationDate)
		throws PortalException {

		if (priority == null) {
			priority = 0D;
		}

		if (neverExpire == null) {
			neverExpire = Boolean.TRUE;
		}

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		User user = UserLocalServiceUtil.getUserById(
			serviceContext.getUserId());

		List<CommerceCatalog> commerceCatalogs =
			CommerceCatalogLocalServiceUtil.getCommerceCatalogs(
				user.getCompanyId(), true);

		CommerceCatalog commerceCatalog = commerceCatalogs.get(0);

		DateElements displayDateElements = new DateElements(
			displayDate, CalendarFactoryUtil.getCalendar(user.getTimeZone()));

		Calendar defaultExpirationCalendar = CalendarFactoryUtil.getCalendar(
			user.getTimeZone());

		defaultExpirationCalendar.add(Calendar.MONTH, 1);

		DateElements expirationDateElements = new DateElements(
			expirationDate, defaultExpirationCalendar);

		return CommercePriceListLocalServiceUtil.addCommercePriceList(
			externalReferenceCode, user.getUserId(),
			commerceCatalog.getGroupId(), false, currency,
			displayDateElements.getDay(), displayDateElements.getHour(),
			displayDateElements.getMinute(), displayDateElements.getMonth(),
			displayDateElements.getYear(), expirationDateElements.getDay(),
			expirationDateElements.getHour(),
			expirationDateElements.getMinute(),
			expirationDateElements.getMonth(), expirationDateElements.getYear(),
			name, netPrice, neverExpire, parentCommercePriceListId, priority,
			CommercePriceListConstants.TYPE_PRICE_LIST, serviceContext);
	}

	public static CommercePriceList addCommercePriceList(
			String externalReferenceCode, long groupId, String currency,
			boolean netPrice, String name, Double priority, Boolean neverExpire,
			Date displayDate, Date expirationDate)
		throws PortalException {

		return addCommercePriceList(
			externalReferenceCode, groupId, currency, netPrice, 0, name,
			priority, neverExpire, displayDate, expirationDate);
	}

	public static CommercePriceList addCommercePriceList(
			String externalReferenceCode, long groupId, String currency,
			long parentCommercePriceListId, String name, Double priority,
			Boolean neverExpire, Date displayDate, Date expirationDate)
		throws PortalException {

		return addCommercePriceList(
			externalReferenceCode, groupId, currency, true,
			parentCommercePriceListId, name, priority, neverExpire, displayDate,
			expirationDate);
	}

	public static CommercePriceList addCommercePriceList(
			String externalReferenceCode, long groupId, String currency,
			String name, Double priority, Boolean neverExpire, Date displayDate,
			Date expirationDate)
		throws PortalException {

		return addCommercePriceList(
			externalReferenceCode, groupId, currency, true, 0, name, priority,
			neverExpire, displayDate, expirationDate);
	}

	public static CommercePriceList addOrUpdateCommercePriceList(
			String externalReferenceCode, long groupId,
			long commercePriceListId, String currency,
			long parentCommercePriceListId, String name, Double priority,
			Boolean neverExpire, Date displayDate, Date expirationDate)
		throws PortalException {

		if (neverExpire == null) {
			neverExpire = Boolean.TRUE;
		}

		if (priority == null) {
			priority = 0D;
		}

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		User user = UserLocalServiceUtil.getUserById(
			serviceContext.getUserId());

		List<CommerceCatalog> commerceCatalogs =
			CommerceCatalogLocalServiceUtil.getCommerceCatalogs(
				user.getCompanyId(), true);

		CommerceCatalog commerceCatalog = commerceCatalogs.get(0);

		DateElements displayDateElements = new DateElements(
			displayDate, CalendarFactoryUtil.getCalendar(user.getTimeZone()));

		Calendar defaultExpirationCalendar = CalendarFactoryUtil.getCalendar(
			user.getTimeZone());

		defaultExpirationCalendar.add(Calendar.MONTH, 1);

		DateElements expirationDateElements = new DateElements(
			expirationDate, defaultExpirationCalendar);

		return CommercePriceListLocalServiceUtil.addOrUpdateCommercePriceList(
			externalReferenceCode, user.getUserId(),
			commerceCatalog.getGroupId(), commercePriceListId, false, currency,
			displayDateElements.getDay(), displayDateElements.getHour(),
			displayDateElements.getMinute(), displayDateElements.getMonth(),
			displayDateElements.getYear(), expirationDateElements.getDay(),
			expirationDateElements.getHour(),
			expirationDateElements.getMinute(),
			expirationDateElements.getMonth(), expirationDateElements.getYear(),
			name, true, neverExpire, parentCommercePriceListId, priority,
			CommercePriceListConstants.TYPE_PRICE_LIST, serviceContext);
	}

	public static CommercePriceList addOrUpdateCommercePriceList(
			String externalReferenceCode, long groupId,
			long commercePriceListId, String currency, String name,
			Double priority, Boolean neverExpire, Date displayDate,
			Date expirationDate)
		throws PortalException {

		return addOrUpdateCommercePriceList(
			externalReferenceCode, groupId, commercePriceListId, currency, 0,
			name, priority, neverExpire, displayDate, expirationDate);
	}

	public static CommercePriceList updateCommercePriceList(
			long groupId, long commercePriceListId,
			boolean catalogBasePriceList, String currency, Date displayDate,
			Date expirationDate, String name, boolean netPrice,
			Boolean neverExpire, long parentCommercePriceListId,
			Double priority)
		throws PortalException {

		if (neverExpire == null) {
			neverExpire = Boolean.TRUE;
		}

		if (priority == null) {
			priority = 0D;
		}

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		User user = UserLocalServiceUtil.getUserById(
			serviceContext.getUserId());

		DateElements displayDateElements = new DateElements(
			displayDate, CalendarFactoryUtil.getCalendar(user.getTimeZone()));

		Calendar defaultExpirationCalendar = CalendarFactoryUtil.getCalendar(
			user.getTimeZone());

		defaultExpirationCalendar.add(Calendar.MONTH, 1);

		DateElements expirationDateElements = new DateElements(
			expirationDate, defaultExpirationCalendar);

		return CommercePriceListLocalServiceUtil.updateCommercePriceList(
			commercePriceListId, catalogBasePriceList, currency,
			displayDateElements.getDay(), displayDateElements.getHour(),
			displayDateElements.getMinute(), displayDateElements.getMonth(),
			displayDateElements.getYear(), expirationDateElements.getDay(),
			expirationDateElements.getHour(),
			expirationDateElements.getMinute(),
			expirationDateElements.getMonth(), expirationDateElements.getYear(),
			name, netPrice, neverExpire, parentCommercePriceListId, priority,
			CommercePriceListConstants.TYPE_PRICE_LIST, serviceContext);
	}

	private static class DateElements {

		public DateElements(Date date, Calendar defaultCalendar) {
			if (date != null) {
				_calendar = _convertDateToCalendar(date);
			}
			else {
				_calendar = defaultCalendar;
			}
		}

		public int getDay() {
			return _calendar.get(Calendar.DAY_OF_MONTH);
		}

		public int getHour() {
			int hour = _calendar.get(Calendar.HOUR);

			if (_calendar.get(Calendar.AM_PM) == Calendar.PM) {
				hour += 12;
			}

			return hour;
		}

		public int getMinute() {
			return _calendar.get(Calendar.MINUTE);
		}

		public int getMonth() {
			return _calendar.get(Calendar.MONTH);
		}

		public int getYear() {
			return _calendar.get(Calendar.YEAR);
		}

		private Calendar _convertDateToCalendar(Date date) {
			Calendar calendar = CalendarFactoryUtil.getCalendar();

			calendar.setTime(date);

			return calendar;
		}

		private final Calendar _calendar;

	}

}