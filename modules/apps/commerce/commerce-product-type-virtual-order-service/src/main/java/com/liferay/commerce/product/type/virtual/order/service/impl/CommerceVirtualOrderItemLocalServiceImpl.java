/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.order.service.impl;

import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.commerce.product.type.virtual.order.service.CommerceVirtualOrderItemFileEntryLocalService;
import com.liferay.commerce.product.type.virtual.order.service.base.CommerceVirtualOrderItemLocalServiceBaseImpl;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingLocalService;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceSubscriptionEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem",
	service = AopService.class
)
public class CommerceVirtualOrderItemLocalServiceImpl
	extends CommerceVirtualOrderItemLocalServiceBaseImpl {

	@Override
	public CommerceVirtualOrderItem addCommerceVirtualOrderItem(
			long commerceOrderItemId,
			List<CPDVirtualSettingFileEntry> cpdVirtualSettingFileEntries,
			int activationStatus, long duration, int maxUsages,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(serviceContext.getUserId());
		long groupId = serviceContext.getScopeGroupId();

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		long commerceVirtualOrderItemId = counterLocalService.increment();

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			commerceVirtualOrderItemPersistence.create(
				commerceVirtualOrderItemId);

		commerceVirtualOrderItem.setGroupId(groupId);
		commerceVirtualOrderItem.setCompanyId(user.getCompanyId());
		commerceVirtualOrderItem.setUserId(user.getUserId());
		commerceVirtualOrderItem.setUserName(user.getFullName());
		commerceVirtualOrderItem.setCommerceOrderItemId(commerceOrderItemId);
		commerceVirtualOrderItem.setActivationStatus(activationStatus);
		commerceVirtualOrderItem.setDuration(duration);
		commerceVirtualOrderItem.setMaxUsages(maxUsages);

		if (Objects.equals(
				commerceVirtualOrderItem.getActivationStatus(),
				commerceOrder.getOrderStatus())) {

			commerceVirtualOrderItem.setActive(true);

			commerceVirtualOrderItem = _setDurationDates(
				commerceVirtualOrderItem);
		}

		commerceVirtualOrderItem = commerceVirtualOrderItemPersistence.update(
			commerceVirtualOrderItem);

		if (cpdVirtualSettingFileEntries == null) {
			return commerceVirtualOrderItem;
		}

		for (CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry :
				cpdVirtualSettingFileEntries) {

			_commerceVirtualOrderItemFileEntryLocalService.
				addCommerceVirtualOrderItemFileEntry(
					user.getUserId(), groupId,
					commerceVirtualOrderItem.getCommerceVirtualOrderItemId(),
					cpdVirtualSettingFileEntry.getFileEntryId(),
					cpdVirtualSettingFileEntry.getUrl(), 0,
					cpdVirtualSettingFileEntry.getVersion());
		}

		return commerceVirtualOrderItem;
	}

	@Override
	public CommerceVirtualOrderItem addCommerceVirtualOrderItem(
			long commerceOrderItemId, ServiceContext serviceContext)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			_cpDefinitionVirtualSettingLocalService.
				fetchCPDefinitionVirtualSetting(
					CPInstance.class.getName(),
					commerceOrderItem.getCPInstanceId());

		if ((cpDefinitionVirtualSetting == null) ||
			!cpDefinitionVirtualSetting.isOverride()) {

			cpDefinitionVirtualSetting =
				_cpDefinitionVirtualSettingLocalService.
					fetchCPDefinitionVirtualSetting(
						CPDefinition.class.getName(),
						commerceOrderItem.getCPDefinitionId());
		}

		if (cpDefinitionVirtualSetting == null) {
			return commerceVirtualOrderItemLocalService.
				addCommerceVirtualOrderItem(
					commerceOrderItemId, null,
					CommerceOrderConstants.ORDER_STATUS_COMPLETED, 0, 0,
					serviceContext);
		}

		return commerceVirtualOrderItemLocalService.addCommerceVirtualOrderItem(
			commerceOrderItemId,
			cpDefinitionVirtualSetting.getCPDVirtualSettingFileEntries(),
			cpDefinitionVirtualSetting.getActivationStatus(),
			cpDefinitionVirtualSetting.getDuration(),
			cpDefinitionVirtualSetting.getMaxUsages(), serviceContext);
	}

	@Override
	public void checkCommerceVirtualOrderItems() throws PortalException {
		List<CommerceVirtualOrderItem> commerceVirtualOrderItems =
			commerceVirtualOrderItemFinder.findByEndDate(new Date());

		for (CommerceVirtualOrderItem commerceVirtualOrderItem :
				commerceVirtualOrderItems) {

			commerceVirtualOrderItemLocalService.setActive(
				commerceVirtualOrderItem.getCommerceVirtualOrderItemId(),
				false);
		}
	}

	@Override
	public void deleteCommerceVirtualOrderItemByCommerceOrderItemId(
		long commerceOrderItemId) {

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			commerceVirtualOrderItemPersistence.fetchByCommerceOrderItemId(
				commerceOrderItemId);

		if (commerceVirtualOrderItem != null) {
			commerceVirtualOrderItemLocalService.deleteCommerceVirtualOrderItem(
				commerceVirtualOrderItem);
		}
	}

	@Override
	public CommerceVirtualOrderItem
		fetchCommerceVirtualOrderItemByCommerceOrderItemId(
			long commerceOrderItemId) {

		return commerceVirtualOrderItemPersistence.fetchByCommerceOrderItemId(
			commerceOrderItemId);
	}

	@Override
	public CommerceVirtualOrderItem
		fetchCommerceVirtualOrderItemByCommerceOrderItemId(
			long commerceOrderItemId, boolean useFinderCache) {

		return commerceVirtualOrderItemPersistence.fetchByCommerceOrderItemId(
			commerceOrderItemId, useFinderCache);
	}

	@Override
	public List<CommerceVirtualOrderItem> getCommerceVirtualOrderItems(
		long groupId, long commerceAccountId, int start, int end,
		OrderByComparator<CommerceVirtualOrderItem> orderByComparator) {

		return commerceVirtualOrderItemFinder.findByG_C(
			groupId, commerceAccountId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceVirtualOrderItemsCount(
		long groupId, long commerceAccountId) {

		return commerceVirtualOrderItemFinder.countByG_C(
			groupId, commerceAccountId);
	}

	@Override
	public File getFile(
			long commerceVirtualOrderItemId,
			long commerceVirtualOrderItemFileEntryId)
		throws Exception {

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			commerceVirtualOrderItemPersistence.findByPrimaryKey(
				commerceVirtualOrderItemId);

		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry =
			commerceVirtualOrderItem.getCommerceVirtualOrderItemFileEntry(
				commerceVirtualOrderItemFileEntryId);

		InputStream contentInputStream;
		String extension = StringPool.BLANK;

		if (commerceVirtualOrderItemFileEntry.getFileEntryId() > 0) {
			FileEntry fileEntry =
				commerceVirtualOrderItemFileEntry.getFileEntry();

			contentInputStream = fileEntry.getContentStream();

			extension = fileEntry.getExtension();
		}
		else {
			URL url = new URL(commerceVirtualOrderItemFileEntry.getUrl());

			contentInputStream = url.openStream();

			String mimeType = URLConnection.guessContentTypeFromStream(
				contentInputStream);

			Set<String> extensions = MimeTypesUtil.getExtensions(mimeType);

			if (!extensions.isEmpty()) {
				Iterator<String> iterator = extensions.iterator();

				extension = iterator.next();
			}
		}

		CommerceOrderItem commerceOrderItem =
			commerceVirtualOrderItem.getCommerceOrderItem();

		File tempFile = _file.createTempFile(contentInputStream);

		File file = new File(
			tempFile.getParent(),
			commerceOrderItem.getNameCurrentValue() + StringPool.PERIOD +
				extension);

		if (file.exists() && !file.delete()) {
			throw new IOException();
		}

		if (!tempFile.renameTo(file)) {
			file = tempFile;
		}

		return file;
	}

	@Override
	public void setActive(long commerceVirtualOrderItemId, boolean active)
		throws PortalException {

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			commerceVirtualOrderItemPersistence.findByPrimaryKey(
				commerceVirtualOrderItemId);

		commerceVirtualOrderItem.setActive(active);

		commerceVirtualOrderItemPersistence.update(commerceVirtualOrderItem);
	}

	@Override
	public CommerceVirtualOrderItem updateCommerceVirtualOrderItem(
			long commerceVirtualOrderItemId, int activationStatus,
			long duration, int maxUsages, boolean active)
		throws PortalException {

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			commerceVirtualOrderItemPersistence.fetchByPrimaryKey(
				commerceVirtualOrderItemId);

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.getCommerceOrderItem(
				commerceVirtualOrderItem.getCommerceOrderItemId());

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		commerceVirtualOrderItem.setActivationStatus(activationStatus);

		if (duration > commerceVirtualOrderItem.getDuration()) {
			duration = duration - commerceVirtualOrderItem.getDuration();
		}
		else {
			duration = 0;
		}

		commerceVirtualOrderItem.setDuration(duration);
		commerceVirtualOrderItem.setMaxUsages(maxUsages);
		commerceVirtualOrderItem.setActive(active);

		if (Objects.equals(
				commerceVirtualOrderItem.getActivationStatus(),
				commerceOrder.getOrderStatus())) {

			commerceVirtualOrderItem = _setDurationDates(
				commerceVirtualOrderItem);
		}

		return commerceVirtualOrderItemPersistence.update(
			commerceVirtualOrderItem);
	}

	@Override
	public CommerceVirtualOrderItem updateCommerceVirtualOrderItemDates(
			long commerceVirtualOrderItemId)
		throws PortalException {

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			commerceVirtualOrderItemPersistence.fetchByPrimaryKey(
				commerceVirtualOrderItemId);

		commerceVirtualOrderItem = _setDurationDates(commerceVirtualOrderItem);

		return commerceVirtualOrderItemPersistence.update(
			commerceVirtualOrderItem);
	}

	private Date _calculateCommerceVirtualOrderItemEndDate(
			CommerceVirtualOrderItem commerceVirtualOrderItem)
		throws PortalException {

		long duration = commerceVirtualOrderItem.getDuration();

		if (duration == 0) {
			return new Date(Long.MIN_VALUE);
		}

		User guestUser = _userLocalService.getGuestUser(
			commerceVirtualOrderItem.getCompanyId());

		Calendar calendar = CalendarFactoryUtil.getCalendar(
			guestUser.getTimeZone());

		calendar.setTimeInMillis(calendar.getTimeInMillis() + duration);

		return calendar.getTime();
	}

	private CommerceSubscriptionEntry _getCommerceSubscriptionEntry(
		long commerceOrderItemId) {

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.fetchCommerceOrderItem(
				commerceOrderItemId);

		if (commerceOrderItem == null) {
			return null;
		}

		return _commerceSubscriptionEntryLocalService.
			fetchCommerceSubscriptionEntryByCommerceOrderItemId(
				commerceOrderItemId);
	}

	private CommerceVirtualOrderItem _setDurationDates(
			CommerceVirtualOrderItem commerceVirtualOrderItem)
		throws PortalException {

		Date startDate = new Date();
		Date endDate;

		CommerceSubscriptionEntry commerceSubscriptionEntry =
			_getCommerceSubscriptionEntry(
				commerceVirtualOrderItem.getCommerceOrderItemId());

		if (commerceSubscriptionEntry == null) {
			endDate = _calculateCommerceVirtualOrderItemEndDate(
				commerceVirtualOrderItem);
		}
		else {
			startDate = commerceSubscriptionEntry.getStartDate();
			endDate = commerceSubscriptionEntry.getNextIterationDate();
		}

		commerceVirtualOrderItem.setStartDate(startDate);

		if (endDate.after(startDate)) {
			commerceVirtualOrderItem.setEndDate(endDate);
		}

		return commerceVirtualOrderItem;
	}

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private CommerceSubscriptionEntryLocalService
		_commerceSubscriptionEntryLocalService;

	@Reference
	private CommerceVirtualOrderItemFileEntryLocalService
		_commerceVirtualOrderItemFileEntryLocalService;

	@Reference
	private CPDefinitionVirtualSettingLocalService
		_cpDefinitionVirtualSettingLocalService;

	@Reference
	private com.liferay.portal.kernel.util.File _file;

	@Reference
	private UserLocalService _userLocalService;

}