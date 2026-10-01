/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.internal.model.listener;

import com.liferay.commerce.discount.model.CommerceDiscountRel;
import com.liferay.commerce.discount.service.CommerceDiscountRelLocalService;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

import java.util.List;

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
			int cpInstanceUnitOfMeasuresCount =
				_cpInstanceUnitOfMeasureLocalService.
					getCPInstanceUnitOfMeasuresCount(
						cpInstanceUnitOfMeasure.getCPInstanceId());

			if ((cpInstanceUnitOfMeasuresCount == 1) &&
				!LazyReferencingThreadLocal.isEnabled()) {

				List<CommerceDiscountRel> commerceDiscountRels =
					_commerceDiscountRelLocalService.getCommerceDiscountRels(
						_classNameLocalService.getClassNameId(
							CPInstance.class.getName()),
						cpInstanceUnitOfMeasure.getCPInstanceId());

				for (CommerceDiscountRel commerceDiscountRel :
						commerceDiscountRels) {

					commerceDiscountRel.setTypeSettingsUnicodeProperties(
						UnicodePropertiesBuilder.create(
							HashMapBuilder.put(
								"unitOfMeasureKey",
								cpInstanceUnitOfMeasure.getKey()
							).build(),
							true
						).build());

					_commerceDiscountRelLocalService.updateCommerceDiscountRel(
						commerceDiscountRel);
				}
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterRemove(CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure) {
		try {
			List<CommerceDiscountRel> commerceDiscountRels =
				_commerceDiscountRelLocalService.getCommerceDiscountRels(
					_classNameLocalService.getClassNameId(
						CPInstance.class.getName()),
					cpInstanceUnitOfMeasure.getCPInstanceId(),
					cpInstanceUnitOfMeasure.getKey());

			for (CommerceDiscountRel commerceDiscountRel :
					commerceDiscountRels) {

				_commerceDiscountRelLocalService.deleteCommerceDiscountRel(
					commerceDiscountRel);
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommerceDiscountRelLocalService _commerceDiscountRelLocalService;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

}