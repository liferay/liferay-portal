/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.test.util;

import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemLocalServiceUtil;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseLocalServiceUtil;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceLocalServiceUtil;
import com.liferay.commerce.product.service.CommerceChannelRelLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.service.CountryLocalServiceUtil;
import com.liferay.portal.kernel.service.RegionLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.util.CountryTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;

import java.math.BigDecimal;

import java.util.Locale;
import java.util.Map;

/**
 * @author Luca Pellizzon
 */
public class CommerceInventoryTestUtil {

	public static CommerceInventoryWarehouse addCommerceInventoryWarehouse()
		throws Exception {

		return addCommerceInventoryWarehouse(
			RandomTestUtil.randomLocaleStringMap(), true);
	}

	public static CommerceInventoryWarehouse addCommerceInventoryWarehouse(
			boolean active)
		throws Exception {

		return addCommerceInventoryWarehouse(
			RandomTestUtil.randomLocaleStringMap(), active);
	}

	public static CommerceInventoryWarehouse addCommerceInventoryWarehouse(
			boolean active, ServiceContext serviceContext)
		throws Exception {

		return addCommerceInventoryWarehouse(
			RandomTestUtil.randomLocaleStringMap(), active, serviceContext);
	}

	public static CommerceInventoryWarehouse addCommerceInventoryWarehouse(
			Map<Locale, String> nameMap)
		throws Exception {

		return addCommerceInventoryWarehouse(nameMap, true);
	}

	public static CommerceInventoryWarehouse addCommerceInventoryWarehouse(
			Map<Locale, String> nameMap, boolean active)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		Country country = _setUpCountry(serviceContext);

		Region region = addRegion(country.getCountryId(), serviceContext);

		return CommerceInventoryWarehouseLocalServiceUtil.
			addCommerceInventoryWarehouse(
				null, nameMap, RandomTestUtil.randomLocaleStringMap(), active,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), region.getRegionCode(),
				country.getA2(), RandomTestUtil.nextDouble(),
				RandomTestUtil.nextDouble(), serviceContext);
	}

	public static CommerceInventoryWarehouse addCommerceInventoryWarehouse(
			Map<Locale, String> nameMap, boolean active,
			ServiceContext serviceContext)
		throws Exception {

		Country country = _setUpCountry(serviceContext);

		Region region = addRegion(country.getCountryId(), serviceContext);

		return CommerceInventoryWarehouseLocalServiceUtil.
			addCommerceInventoryWarehouse(
				null, nameMap, RandomTestUtil.randomLocaleStringMap(), active,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), region.getRegionCode(),
				country.getA2(), RandomTestUtil.nextDouble(),
				RandomTestUtil.nextDouble(), serviceContext);
	}

	public static CommerceInventoryWarehouse addCommerceInventoryWarehouse(
			Map<Locale, String> nameMap, ServiceContext serviceContext)
		throws Exception {

		return addCommerceInventoryWarehouse(nameMap, true, serviceContext);
	}

	public static CommerceInventoryWarehouse addCommerceInventoryWarehouse(
			ServiceContext serviceContext)
		throws Exception {

		return addCommerceInventoryWarehouse(
			RandomTestUtil.randomLocaleStringMap(), true, serviceContext);
	}

	public static CommerceInventoryWarehouseItem
			addCommerceInventoryWarehouseItem(
				long commerceChannelId, BigDecimal quantity, String sku,
				String unitOfMeasureKey, ServiceContext serviceContext)
		throws Exception {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			addCommerceInventoryWarehouse(serviceContext);

		CommerceChannelRelLocalServiceUtil.addCommerceChannelRel(
			CommerceInventoryWarehouse.class.getName(),
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			commerceChannelId, serviceContext);

		return CommerceInventoryWarehouseItemLocalServiceUtil.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, serviceContext.getUserId(),
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				quantity, BigDecimal.ZERO, sku, unitOfMeasureKey);
	}

	public static CommerceInventoryWarehouseItem
			addCommerceInventoryWarehouseItem(
				long userId,
				CommerceInventoryWarehouse commerceInventoryWarehouse,
				BigDecimal quantity, String sku, String unitOfMeasureKey)
		throws Exception {

		return CommerceInventoryWarehouseItemLocalServiceUtil.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, userId,
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				quantity, BigDecimal.ZERO, sku, unitOfMeasureKey);
	}

	public static CommerceInventoryWarehouse
			addCommerceInventoryWarehouseWithExternalReferenceCode(
				long groupId, Map<Locale, String> nameMap)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		Country country = addCountry(serviceContext);

		Region region = addRegion(country.getCountryId(), serviceContext);

		return CommerceInventoryWarehouseLocalServiceUtil.
			addCommerceInventoryWarehouse(
				RandomTestUtil.randomString(), nameMap,
				RandomTestUtil.randomLocaleStringMap(), true,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), region.getRegionCode(),
				country.getA2(), RandomTestUtil.randomDouble(),
				RandomTestUtil.randomDouble(), serviceContext);
	}

	public static Country addCountry(ServiceContext serviceContext)
		throws Exception {

		return CountryLocalServiceUtil.addCountry(
			null, CountryTestUtil.randomA2(serviceContext.getCompanyId()),
			CountryTestUtil.randomA3(serviceContext.getCompanyId()), true, true,
			null, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(NumericStringRandomizerBumper.INSTANCE),
			0, true, false, false, serviceContext);
	}

	public static CPInstance addRandomCPInstanceSku(long groupId)
		throws Exception {

		CPInstance cpInstance = CPTestUtil.addCPInstance(groupId);

		cpInstance.setSku(RandomTestUtil.randomString());

		return CPInstanceLocalServiceUtil.updateCPInstance(cpInstance);
	}

	public static Region addRegion(
			long countryId, ServiceContext serviceContext)
		throws PortalException {

		return RegionLocalServiceUtil.addRegion(
			null, countryId, true, RandomTestUtil.randomString(), 0,
			RandomTestUtil.randomString(), serviceContext);
	}

	private static Country _setUpCountry(ServiceContext serviceContext)
		throws Exception {

		Country country = CountryLocalServiceUtil.fetchCountryByNumber(
			serviceContext.getCompanyId(), "000");

		if (country == null) {
			country = CountryLocalServiceUtil.addCountry(
				null, CountryTestUtil.randomA2(serviceContext.getCompanyId()),
				CountryTestUtil.randomA3(serviceContext.getCompanyId()), true,
				true, null, RandomTestUtil.randomString(), "000",
				RandomTestUtil.randomDouble(), true, false, false,
				serviceContext);
		}

		return country;
	}

}