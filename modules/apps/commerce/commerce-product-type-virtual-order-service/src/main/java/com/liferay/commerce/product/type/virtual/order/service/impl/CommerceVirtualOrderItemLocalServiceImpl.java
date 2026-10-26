/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.order.service.impl;

import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceOrderItemTable;
import com.liferay.commerce.model.CommerceOrderTable;
import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceTable;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSettingTable;
import com.liferay.commerce.product.type.virtual.order.exception.CommerceVirtualOrderItemUrlException;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntryTable;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemTable;
import com.liferay.commerce.product.type.virtual.order.service.CommerceVirtualOrderItemFileEntryLocalService;
import com.liferay.commerce.product.type.virtual.order.service.base.CommerceVirtualOrderItemLocalServiceBaseImpl;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingLocalService;
import com.liferay.commerce.product.type.virtual.service.persistence.CPDVirtualSettingFileEntryPersistence;
import com.liferay.commerce.product.type.virtual.util.VirtualCPTypeURLValidatorUtil;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceSubscriptionEntryLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
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
			contentInputStream = _getContentInputStream(
				commerceVirtualOrderItem.getCompanyId(),
				commerceVirtualOrderItemFileEntry.getUrl());

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

		String fileName = _file.getShortFileName(
			commerceOrderItem.getNameCurrentValue() + StringPool.PERIOD +
				extension);

		File tempFile = _file.createTempFile(contentInputStream);

		if (!Validator.isFileName(fileName)) {
			return tempFile;
		}

		File file = new File(tempFile.getParent(), fileName);

		if (file.exists() && !file.delete()) {
			throw new IOException();
		}

		if (!tempFile.renameTo(file)) {
			file = tempFile;
		}

		return file;
	}

	@Override
	public List<CommerceVirtualOrderItem>
			getNoCPDVirtualSettingFileEntryCommerceVirtualOrderItems(
				long cpdVirtualSettingFileEntryId)
		throws PortalException {

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
			_cpdVirtualSettingFileEntryPersistence.findByPrimaryKey(
				cpdVirtualSettingFileEntryId);

		return dslQuery(
			DSLQueryFactoryUtil.select(
				CommerceVirtualOrderItemTable.INSTANCE
			).from(
				CommerceVirtualOrderItemTable.INSTANCE
			).innerJoinON(
				CommerceOrderItemTable.INSTANCE,
				CommerceOrderItemTable.INSTANCE.commerceOrderItemId.eq(
					CommerceVirtualOrderItemTable.INSTANCE.commerceOrderItemId)
			).innerJoinON(
				CommerceOrderTable.INSTANCE,
				CommerceOrderTable.INSTANCE.commerceOrderId.eq(
					CommerceOrderItemTable.INSTANCE.commerceOrderId)
			).innerJoinON(
				CPInstanceTable.INSTANCE,
				CPInstanceTable.INSTANCE.CPInstanceId.eq(
					CommerceOrderItemTable.INSTANCE.CPInstanceId)
			).where(
				CommerceOrderTable.INSTANCE.orderStatus.neq(
					CommerceOrderConstants.ORDER_STATUS_CANCELLED
				).and(
					_getCPDefinitionVirtualSettingPredicate(
						cpdVirtualSettingFileEntry.
							getCPDefinitionVirtualSetting())
				).and(
					CommerceVirtualOrderItemTable.INSTANCE.
						commerceVirtualOrderItemId.notIn(
							_getCommerceVirtualOrderItemIdsDSLQuery(
								cpdVirtualSettingFileEntry))
				)
			));
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

	private Predicate _getCPDefinitionVirtualSettingPredicate(
			CPDefinitionVirtualSetting cpDefinitionVirtualSetting)
		throws PortalException {

		if (Objects.equals(
				cpDefinitionVirtualSetting.getClassName(),
				CPInstance.class.getName())) {

			CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
				cpDefinitionVirtualSetting.getClassPK());

			CPDefinition cpDefinition = cpInstance.getCPDefinition();

			return CommerceOrderItemTable.INSTANCE.CProductId.eq(
				cpDefinition.getCProductId()
			).and(
				CPInstanceTable.INSTANCE.CPInstanceUuid.eq(
					cpInstance.getCPInstanceUuid())
			).and(
				CommerceOrderItemTable.INSTANCE.CPInstanceId.in(
					_getOverrideCPInstanceIdsDSLQuery())
			);
		}

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinitionVirtualSetting.getClassPK());

		return CommerceOrderItemTable.INSTANCE.CProductId.eq(
			cpDefinition.getCProductId()
		).and(
			CommerceOrderItemTable.INSTANCE.CPInstanceId.notIn(
				_getOverrideCPInstanceIdsDSLQuery())
		);
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

	private DSLQuery _getCommerceVirtualOrderItemIdsDSLQuery(
		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry) {

		Predicate predicate = null;

		if (cpdVirtualSettingFileEntry.getFileEntryId() > 0) {
			predicate =
				CommerceVirtualOrderItemFileEntryTable.INSTANCE.fileEntryId.eq(
					cpdVirtualSettingFileEntry.getFileEntryId());
		}
		else {
			predicate = CommerceVirtualOrderItemFileEntryTable.INSTANCE.url.eq(
				cpdVirtualSettingFileEntry.getUrl());
		}

		return DSLQueryFactoryUtil.select(
			CommerceVirtualOrderItemFileEntryTable.INSTANCE.
				commerceVirtualOrderItemId
		).from(
			CommerceVirtualOrderItemFileEntryTable.INSTANCE
		).where(
			predicate
		);
	}

	private InputStream _getContentInputStream(long companyId, String url)
		throws Exception {

		URL currentURL = new URL(url);

		for (int i = 0; i <= _MAX_REDIRECTS; i++) {
			if (!VirtualCPTypeURLValidatorUtil.isValidURL(
					companyId, currentURL.toString())) {

				throw new CommerceVirtualOrderItemUrlException();
			}

			Http.Options options = new Http.Options();

			options.setFollowRedirects(false);
			options.setLocation(currentURL.toString());

			InputStream inputStream = _http.URLtoInputStream(options);

			Http.Response response = options.getResponse();

			int responseCode = response.getResponseCode();

			if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
				inputStream.close();

				throw new IOException(
					StringBundler.concat(
						"Unable to download ", currentURL,
						" with response code ", responseCode));
			}

			String location = response.getHeader(HttpHeaders.LOCATION);

			if ((responseCode < HttpURLConnection.HTTP_MULT_CHOICE) ||
				Validator.isNull(location)) {

				return inputStream;
			}

			inputStream.close();

			currentURL = new URL(currentURL, location);
		}

		throw new CommerceVirtualOrderItemUrlException();
	}

	private DSLQuery _getOverrideCPInstanceIdsDSLQuery() {
		return DSLQueryFactoryUtil.select(
			CPDefinitionVirtualSettingTable.INSTANCE.classPK
		).from(
			CPDefinitionVirtualSettingTable.INSTANCE
		).where(
			CPDefinitionVirtualSettingTable.INSTANCE.classNameId.eq(
				_classNameLocalService.getClassNameId(CPInstance.class)
			).and(
				CPDefinitionVirtualSettingTable.INSTANCE.override.eq(true)
			)
		);
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

	private static final int _MAX_REDIRECTS = 5;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private CommerceSubscriptionEntryLocalService
		_commerceSubscriptionEntryLocalService;

	@Reference
	private CommerceVirtualOrderItemFileEntryLocalService
		_commerceVirtualOrderItemFileEntryLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPDefinitionVirtualSettingLocalService
		_cpDefinitionVirtualSettingLocalService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private CPDVirtualSettingFileEntryPersistence
		_cpdVirtualSettingFileEntryPersistence;

	@Reference
	private com.liferay.portal.kernel.util.File _file;

	@Reference
	private Http _http;

	@Reference
	private UserLocalService _userLocalService;

}