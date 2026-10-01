/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.internal.model.listener;

import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.math.BigDecimal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(service = ModelListener.class)
public class CPInstanceUnitOfMeasureModelListener
	extends BaseModelListener<CPInstanceUnitOfMeasure> {

	@Override
	public void onAfterCreate(CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure) {
		try {
			CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
				cpInstanceUnitOfMeasure.getCPInstanceId());

			if (LazyReferencingThreadLocal.isEnabled()) {
				for (CommercePriceEntry commercePriceEntry :
						_commercePriceEntryLocalService.getCommercePriceEntries(
							cpInstance.getCPInstanceUuid(), null,
							cpInstanceUnitOfMeasure.getKey())) {

					commercePriceEntry.setPricingQuantity(
						cpInstanceUnitOfMeasure.getPricingQuantity());
					commercePriceEntry.setQuantity(
						cpInstanceUnitOfMeasure.getIncrementalOrderQuantity());

					_commercePriceEntryLocalService.updateCommercePriceEntry(
						commercePriceEntry);
				}
			}

			int count =
				_cpInstanceUnitOfMeasureLocalService.
					getCPInstanceUnitOfMeasuresCount(
						cpInstanceUnitOfMeasure.getCPInstanceId());

			if ((count == 1) && !LazyReferencingThreadLocal.isEnabled()) {
				for (CommercePriceEntry commercePriceEntry :
						_commercePriceEntryLocalService.getCommercePriceEntries(
							cpInstance.getCPInstanceUuid(), null,
							StringPool.BLANK)) {

					commercePriceEntry.setPricingQuantity(
						cpInstanceUnitOfMeasure.getPricingQuantity());
					commercePriceEntry.setQuantity(
						cpInstanceUnitOfMeasure.getIncrementalOrderQuantity());
					commercePriceEntry.setUnitOfMeasureKey(
						cpInstanceUnitOfMeasure.getKey());

					_commercePriceEntryLocalService.updateCommercePriceEntry(
						commercePriceEntry);
				}
			}
			else if (count > 1) {
				_addCommercePriceEntry(
					cpInstance, cpInstanceUnitOfMeasure,
					CommercePriceListConstants.TYPE_PRICE_LIST);
				_addCommercePriceEntry(
					cpInstance, cpInstanceUnitOfMeasure,
					CommercePriceListConstants.TYPE_PROMOTION);
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterRemove(CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure)
		throws ModelListenerException {

		try {
			CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
				cpInstanceUnitOfMeasure.getCPInstanceId());

			if (cpInstance == null) {
				return;
			}

			int count =
				_cpInstanceUnitOfMeasureLocalService.
					getCPInstanceUnitOfMeasuresCount(
						cpInstanceUnitOfMeasure.getCPInstanceId());

			if (count == 0) {
				for (CommercePriceEntry commercePriceEntry :
						_commercePriceEntryLocalService.getCommercePriceEntries(
							cpInstance.getCPInstanceUuid(),
							cpInstanceUnitOfMeasure.
								getIncrementalOrderQuantity(),
							cpInstanceUnitOfMeasure.getKey())) {

					commercePriceEntry.setPricingQuantity(null);
					commercePriceEntry.setQuantity(null);
					commercePriceEntry.setUnitOfMeasureKey(null);

					_commercePriceEntryLocalService.updateCommercePriceEntry(
						commercePriceEntry);
				}
			}
			else {
				_commercePriceEntryLocalService.deleteCommercePriceEntries(
					cpInstance.getCPInstanceUuid(),
					cpInstanceUnitOfMeasure.getIncrementalOrderQuantity(),
					cpInstanceUnitOfMeasure.getKey());
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterUpdate(
			CPInstanceUnitOfMeasure originalCPInstanceUnitOfMeasure,
			CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure)
		throws ModelListenerException {

		try {
			String originalUnitOfMeasureKey =
				originalCPInstanceUnitOfMeasure.getKey();
			String unitOfMeasureKey = cpInstanceUnitOfMeasure.getKey();

			BigDecimal originalIncrementalOrderQuantity =
				originalCPInstanceUnitOfMeasure.getIncrementalOrderQuantity();
			BigDecimal incrementalOrderQuantity =
				cpInstanceUnitOfMeasure.getIncrementalOrderQuantity();

			int compare = originalIncrementalOrderQuantity.compareTo(
				incrementalOrderQuantity);

			BigDecimal pricingQuantity =
				cpInstanceUnitOfMeasure.getPricingQuantity();

			if ((compare == 0) &&
				!BigDecimalUtil.eq(
					originalCPInstanceUnitOfMeasure.getPricingQuantity(),
					pricingQuantity)) {

				compare = 1;
			}

			if (!StringUtil.equals(
					originalUnitOfMeasureKey, unitOfMeasureKey) ||
				(compare != 0)) {

				CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
					cpInstanceUnitOfMeasure.getCPInstanceId());

				for (CommercePriceEntry commercePriceEntry :
						_commercePriceEntryLocalService.getCommercePriceEntries(
							cpInstance.getCPInstanceUuid(),
							originalIncrementalOrderQuantity,
							originalUnitOfMeasureKey)) {

					commercePriceEntry.setPricingQuantity(pricingQuantity);
					commercePriceEntry.setQuantity(incrementalOrderQuantity);
					commercePriceEntry.setUnitOfMeasureKey(unitOfMeasureKey);

					_commercePriceEntryLocalService.updateCommercePriceEntry(
						commercePriceEntry);
				}
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private void _addCommercePriceEntry(
			CPInstance cpInstance,
			CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure, String type)
		throws Exception {

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.
				fetchCatalogBaseCommercePriceListByType(
					cpInstance.getGroupId(), type);

		if (commercePriceList == null) {
			return;
		}

		if (LazyReferencingThreadLocal.isEnabled()) {
			CommercePriceEntry commercePriceEntry =
				_commercePriceEntryLocalService.fetchCommercePriceEntry(
					commercePriceList.getCommercePriceListId(),
					cpInstance.getCPInstanceUuid(),
					cpInstanceUnitOfMeasure.getKey());

			if (commercePriceEntry != null) {
				return;
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if ((serviceContext == null) || (serviceContext.getUserId() == 0)) {
			serviceContext = new ServiceContext();

			serviceContext.setUserId(cpInstanceUnitOfMeasure.getUserId());
		}

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		_commercePriceEntryLocalService.addCommercePriceEntry(
			StringPool.BLANK, cpDefinition.getCProductId(),
			cpInstance.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), BigDecimal.ZERO, false,
			BigDecimal.ZERO, cpInstanceUnitOfMeasure.getKey(), serviceContext);
	}

	@Reference
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Reference
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

}