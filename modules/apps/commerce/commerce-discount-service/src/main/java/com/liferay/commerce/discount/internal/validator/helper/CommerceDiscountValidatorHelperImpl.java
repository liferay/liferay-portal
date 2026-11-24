/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.internal.validator.helper;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.discount.exception.CommerceDiscountValidatorException;
import com.liferay.commerce.discount.internal.validator.comparator.CommerceDiscountValidatorServiceReferenceComparator;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.validator.CommerceDiscountValidator;
import com.liferay.commerce.discount.validator.CommerceDiscountValidatorResult;
import com.liferay.commerce.discount.validator.helper.CommerceDiscountValidatorHelper;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Riccardo Alberti
 */
@Component(service = CommerceDiscountValidatorHelper.class)
public class CommerceDiscountValidatorHelperImpl
	implements CommerceDiscountValidatorHelper {

	@Override
	public void checkValid(
			CommerceContext commerceContext, CommerceDiscount commerceDiscount,
			String... types)
		throws PortalException {

		List<CommerceDiscountValidator> commerceDiscountValidators =
			_getCommerceDiscountValidators(types);

		for (CommerceDiscountValidator commerceDiscountValidator :
				commerceDiscountValidators) {

			CommerceDiscountValidatorResult commerceDiscountValidatorResult =
				commerceDiscountValidator.validate(
					commerceContext, commerceDiscount);

			if (!commerceDiscountValidatorResult.isValid()) {
				throw new CommerceDiscountValidatorException(
					commerceDiscountValidatorResult.getMessage());
			}
		}
	}

	@Override
	public boolean isValid(
			CommerceContext commerceContext, CommerceDiscount commerceDiscount,
			String... types)
		throws PortalException {

		List<CommerceDiscountValidatorResult> commerceDiscountValidatorResults =
			validate(commerceContext, commerceDiscount, types);

		return commerceDiscountValidatorResults.isEmpty();
	}

	@Override
	public List<CommerceDiscountValidatorResult> validate(
			CommerceContext commerceContext, CommerceDiscount commerceDiscount,
			String... types)
		throws PortalException {

		return TransformUtil.transform(
			_getCommerceDiscountValidators(types),
			commerceDiscountValidator -> {
				CommerceDiscountValidatorResult
					commerceDiscountValidatorResult =
						commerceDiscountValidator.validate(
							commerceContext, commerceDiscount);

				if (!commerceDiscountValidatorResult.isValid()) {
					return commerceDiscountValidatorResult;
				}

				return null;
			});
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, CommerceDiscountValidator.class,
			"(commerce.discount.validator.type=*)",
			new PropertyServiceReferenceMapper<>(
				"commerce.discount.validator.type"),
			new CommerceDiscountValidatorServiceReferenceComparator());
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private List<CommerceDiscountValidator> _getCommerceDiscountValidators(
		String... types) {

		List<CommerceDiscountValidator> commerceDiscountValidators =
			new ArrayList<>();

		if (ArrayUtil.isEmpty(types)) {
			for (List<CommerceDiscountValidator>
					typeCommerceDiscountValidators :
						_serviceTrackerMap.values()) {

				commerceDiscountValidators.addAll(
					typeCommerceDiscountValidators);
			}

			return commerceDiscountValidators;
		}

		for (String type : types) {
			List<CommerceDiscountValidator> typeCommerceDiscountValidators =
				_serviceTrackerMap.getService(type);

			if (typeCommerceDiscountValidators == null) {
				continue;
			}

			commerceDiscountValidators.addAll(typeCommerceDiscountValidators);
		}

		return commerceDiscountValidators;
	}

	private ServiceTrackerMap<String, List<CommerceDiscountValidator>>
		_serviceTrackerMap;

}