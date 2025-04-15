/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0;

import com.liferay.commerce.price.list.exception.NoSuchPriceEntryException;
import com.liferay.commerce.price.list.exception.NoSuchPriceListException;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryService;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.price.list.service.CommerceTierPriceEntryService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceEntry;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.TierPrice;
import com.liferay.headless.commerce.admin.pricing.internal.odata.entity.v2_0.PriceEntryEntityModel;
import com.liferay.headless.commerce.admin.pricing.internal.util.v2_0.TierPriceUtil;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.PriceEntryResource;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.math.BigDecimal;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Zoltán Takács
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v2_0/price-entry.properties",
	scope = ServiceScope.PROTOTYPE, service = PriceEntryResource.class
)
public class PriceEntryResourceImpl extends BasePriceEntryResourceImpl {

	@Override
	public void deletePriceEntry(Long id) throws Exception {
		_commercePriceEntryService.deleteCommercePriceEntry(id);
	}

	@Override
	public void deletePriceEntryByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryService.
				fetchCommercePriceEntryByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commercePriceEntry == null) {
			throw new NoSuchPriceEntryException(
				"Unable to find price entry with external reference code " +
					externalReferenceCode);
		}

		_commercePriceEntryService.deleteCommercePriceEntry(
			commercePriceEntry.getCommercePriceEntryId());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public PriceEntry getPriceEntry(Long id) throws Exception {
		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryService.getCommercePriceEntry(id);

		return _toPriceEntry(commercePriceEntry.getCommercePriceEntryId());
	}

	@Override
	public PriceEntry getPriceEntryByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryService.
				fetchCommercePriceEntryByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commercePriceEntry == null) {
			throw new NoSuchPriceEntryException(
				"Unable to find price entry with external reference code " +
					externalReferenceCode);
		}

		return _toPriceEntry(commercePriceEntry.getCommercePriceEntryId());
	}

	@Override
	public Page<PriceEntry> getPriceListByExternalReferenceCodePriceEntriesPage(
			String externalReferenceCode, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		CommercePriceList commercePriceList =
			_commercePriceListService.
				fetchCommercePriceListByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commercePriceList == null) {
			throw new NoSuchPriceListException(
				"Unable to find price list with external reference code " +
					externalReferenceCode);
		}

		return getPriceListIdPriceEntriesPage(
			commercePriceList.getCommercePriceListId(), search, filter,
			pagination, sorts);
	}

	@Override
	public Page<PriceEntry> getPriceListIdPriceEntriesPage(
			Long id, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		CommercePriceList commercePriceList =
			_commercePriceListService.fetchCommercePriceList(id);

		if (commercePriceList == null) {
			throw new NoSuchPriceListException(
				"Unable to find Price List with id: " + id);
		}

		return SearchUtil.search(
			null, booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CommercePriceEntry.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute("commercePriceListId", id);
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> _toPriceEntry(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public PriceEntry patchPriceEntry(Long id, PriceEntry priceEntry)
		throws Exception {

		return _toPriceEntry(
			_updatePriceEntry(
				_commercePriceEntryService.getCommercePriceEntry(id),
				priceEntry));
	}

	@Override
	public PriceEntry patchPriceEntryByExternalReferenceCode(
			String externalReferenceCode, PriceEntry priceEntry)
		throws Exception {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryService.
				fetchCommercePriceEntryByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commercePriceEntry == null) {
			throw new NoSuchPriceEntryException(
				"Unable to find price entry with external reference code " +
					externalReferenceCode);
		}

		return _toPriceEntry(_updatePriceEntry(commercePriceEntry, priceEntry));
	}

	@Override
	public PriceEntry postPriceListByExternalReferenceCodePriceEntry(
			String externalReferenceCode, PriceEntry priceEntry)
		throws Exception {

		CommercePriceList commercePriceList =
			_commercePriceListService.
				fetchCommercePriceListByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commercePriceList == null) {
			throw new NoSuchPriceListException(
				"Unable to find price list with external reference code " +
					externalReferenceCode);
		}

		CommercePriceEntry commercePriceEntry = _addOrUpdateCommercePriceEntry(
			commercePriceList, priceEntry);

		return _toPriceEntry(commercePriceEntry.getCommercePriceEntryId());
	}

	@Override
	public PriceEntry postPriceListIdPriceEntry(Long id, PriceEntry priceEntry)
		throws Exception {

		CommercePriceEntry commercePriceEntry = _addOrUpdateCommercePriceEntry(
			_commercePriceListService.getCommercePriceList(id), priceEntry);

		return _toPriceEntry(commercePriceEntry.getCommercePriceEntryId());
	}

	private CommercePriceEntry _addOrUpdateCommercePriceEntry(
			CommercePriceList commercePriceList, PriceEntry priceEntry)
		throws Exception {

		ServiceContext serviceContext = _serviceContextHelper.getServiceContext(
			commercePriceList.getGroupId());

		// Commerce price entry

		long cProductId = 0;
		String cpInstanceUuid = null;
		CPInstance cpInstance = null;

		long skuId = GetterUtil.getLong(priceEntry.getSkuId());
		String skuExternalReferenceCode =
			priceEntry.getSkuExternalReferenceCode();

		if (skuId > 0) {
			cpInstance = _cpInstanceService.fetchCPInstance(skuId);
		}
		else if (Validator.isNotNull(skuExternalReferenceCode)) {
			cpInstance =
				_cpInstanceService.fetchCPInstanceByExternalReferenceCode(
					skuExternalReferenceCode, serviceContext.getCompanyId());
		}

		if (cpInstance != null) {
			CPDefinition cpDefinition = cpInstance.getCPDefinition();

			cProductId = cpDefinition.getCProductId();

			cpInstanceUuid = cpInstance.getCPInstanceUuid();
		}

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			priceEntry.getDisplayDate(), serviceContext.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			priceEntry.getExpirationDate(), serviceContext.getTimeZone());

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryService.addOrUpdateCommercePriceEntry(
				priceEntry.getExternalReferenceCode(),
				GetterUtil.getLong(priceEntry.getPriceEntryId()), cProductId,
				cpInstanceUuid, commercePriceList.getCommercePriceListId(),
				GetterUtil.getBoolean(priceEntry.getDiscountDiscovery(), true),
				priceEntry.getDiscountLevel1(), priceEntry.getDiscountLevel2(),
				priceEntry.getDiscountLevel3(), priceEntry.getDiscountLevel4(),
				displayDateConfig.getMonth(), displayDateConfig.getDay(),
				displayDateConfig.getYear(), displayDateConfig.getHour(),
				displayDateConfig.getMinute(), expirationDateConfig.getMonth(),
				expirationDateConfig.getDay(), expirationDateConfig.getYear(),
				expirationDateConfig.getHour(),
				expirationDateConfig.getMinute(),
				GetterUtil.getBoolean(priceEntry.getNeverExpire(), true),
				BigDecimal.valueOf(priceEntry.getPrice()),
				GetterUtil.getBoolean(priceEntry.getPriceOnApplication()),
				priceEntry.getSkuExternalReferenceCode(),
				priceEntry.getUnitOfMeasureKey(), serviceContext);

		// Update nested resources

		_updateNestedResources(priceEntry, commercePriceEntry);

		return commercePriceEntry;
	}

	private Map<String, Map<String, String>> _getActions(
			CommercePriceEntry commercePriceEntry)
		throws Exception {

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			addAction(
				"UPDATE", commercePriceEntry.getCommercePriceEntryId(),
				"deletePriceEntry", _commercePriceEntryModelResourcePermission)
		).put(
			"get",
			addAction(
				"VIEW", commercePriceEntry.getCommercePriceEntryId(),
				"getPriceEntry", _commercePriceEntryModelResourcePermission)
		).put(
			"update",
			addAction(
				"UPDATE", commercePriceEntry.getCommercePriceEntryId(),
				"patchPriceEntry", _commercePriceEntryModelResourcePermission)
		).build();
	}

	private PriceEntry _toPriceEntry(CommercePriceEntry commercePriceEntry)
		throws Exception {

		return _toPriceEntry(commercePriceEntry.getCommercePriceEntryId());
	}

	private PriceEntry _toPriceEntry(Long commercePriceEntryId)
		throws Exception {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryService.getCommercePriceEntry(
				commercePriceEntryId);

		return _priceEntryDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(commercePriceEntry), _dtoConverterRegistry,
				commercePriceEntryId,
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private void _updateNestedResources(
			PriceEntry priceEntry, CommercePriceEntry commercePriceEntry)
		throws Exception {

		TierPrice[] tierPrices = priceEntry.getTierPrices();

		if (tierPrices != null) {
			for (TierPrice tierPrice : tierPrices) {
				TierPriceUtil.addOrUpdateCommerceTierPriceEntry(
					_commerceTierPriceEntryService, tierPrice,
					commercePriceEntry, _serviceContextHelper);
			}
		}
	}

	private CommercePriceEntry _updatePriceEntry(
			CommercePriceEntry commercePriceEntry, PriceEntry priceEntry)
		throws Exception {

		// Commerce price entry

		ServiceContext serviceContext =
			_serviceContextHelper.getServiceContext();

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			priceEntry.getDisplayDate(), serviceContext.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			priceEntry.getExpirationDate(), serviceContext.getTimeZone());

		commercePriceEntry =
			_commercePriceEntryService.updateCommercePriceEntry(
				commercePriceEntry.getCommercePriceEntryId(),
				GetterUtil.getBoolean(
					priceEntry.getBulkPricing(),
					commercePriceEntry.isBulkPricing()),
				GetterUtil.getBoolean(
					priceEntry.getDiscountDiscovery(),
					commercePriceEntry.isDiscountDiscovery()),
				BigDecimalUtil.get(
					priceEntry.getDiscountLevel1(),
					commercePriceEntry.getDiscountLevel1()),
				BigDecimalUtil.get(
					priceEntry.getDiscountLevel2(),
					commercePriceEntry.getDiscountLevel2()),
				BigDecimalUtil.get(
					priceEntry.getDiscountLevel3(),
					commercePriceEntry.getDiscountLevel3()),
				BigDecimalUtil.get(
					priceEntry.getDiscountLevel4(),
					commercePriceEntry.getDiscountLevel4()),
				displayDateConfig.getMonth(), displayDateConfig.getDay(),
				displayDateConfig.getYear(), displayDateConfig.getHour(),
				displayDateConfig.getMinute(), expirationDateConfig.getMonth(),
				expirationDateConfig.getDay(), expirationDateConfig.getYear(),
				expirationDateConfig.getHour(),
				expirationDateConfig.getMinute(),
				GetterUtil.getBoolean(priceEntry.getNeverExpire(), true),
				BigDecimalUtil.get(
					priceEntry.getPrice(), commercePriceEntry.getPrice()),
				GetterUtil.getBoolean(
					priceEntry.getPriceOnApplication(),
					commercePriceEntry.isPriceOnApplication()),
				commercePriceEntry.getUnitOfMeasureKey(), serviceContext);

		// Update nested resources

		_updateNestedResources(priceEntry, commercePriceEntry);

		return commercePriceEntry;
	}

	private static final EntityModel _entityModel = new PriceEntryEntityModel();

	@Reference(
		target = "(model.class.name=com.liferay.commerce.price.list.model.CommercePriceEntry)"
	)
	private ModelResourcePermission<CommercePriceEntry>
		_commercePriceEntryModelResourcePermission;

	@Reference
	private CommercePriceEntryService _commercePriceEntryService;

	@Reference
	private CommercePriceListService _commercePriceListService;

	@Reference
	private CommerceTierPriceEntryService _commerceTierPriceEntryService;

	@Reference
	private CPInstanceService _cpInstanceService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter.PriceEntryDTOConverter)"
	)
	private DTOConverter<CommercePriceEntry, PriceEntry>
		_priceEntryDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}