/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.resource.v1_0;

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
import com.liferay.headless.commerce.admin.pricing.dto.v1_0.PriceEntry;
import com.liferay.headless.commerce.admin.pricing.dto.v1_0.TierPrice;
import com.liferay.headless.commerce.admin.pricing.internal.util.v1_0.TierPriceUtil;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.PriceEntryResource;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Zoltán Takács
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/price-entry.properties",
	scope = ServiceScope.PROTOTYPE, service = PriceEntryResource.class
)
public class PriceEntryResourceImpl extends BasePriceEntryResourceImpl {

	@Override
	public Response deletePriceEntry(Long id) throws Exception {
		_commercePriceEntryService.deleteCommercePriceEntry(id);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response deletePriceEntryByExternalReferenceCode(
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

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
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
			String externalReferenceCode, Pagination pagination)
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

		List<CommercePriceEntry> commercePriceEntries =
			_commercePriceEntryService.getCommercePriceEntries(
				commercePriceList.getCommercePriceListId(),
				pagination.getStartPosition(), pagination.getEndPosition());

		int totalCount =
			_commercePriceEntryService.getCommercePriceEntriesCount(
				commercePriceList.getCommercePriceListId());

		return Page.of(
			_toPriceEntries(commercePriceEntries), pagination, totalCount);
	}

	@Override
	public Page<PriceEntry> getPriceListIdPriceEntriesPage(
			Long id, Pagination pagination)
		throws Exception {

		CommercePriceList commercePriceList =
			_commercePriceListService.fetchCommercePriceList(id);

		if (commercePriceList == null) {
			throw new NoSuchPriceListException(
				"Unable to find Price List with id: " + id);
		}

		List<CommercePriceEntry> commercePriceEntries =
			_commercePriceEntryService.getCommercePriceEntries(
				id, pagination.getStartPosition(), pagination.getEndPosition());

		int totalCount =
			_commercePriceEntryService.getCommercePriceEntriesCount(id);

		return Page.of(
			_toPriceEntries(commercePriceEntries), pagination, totalCount);
	}

	@Override
	public Response patchPriceEntry(Long id, PriceEntry priceEntry)
		throws Exception {

		_updatePriceEntry(
			_commercePriceEntryService.getCommercePriceEntry(id), priceEntry);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response patchPriceEntryByExternalReferenceCode(
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

		_updatePriceEntry(commercePriceEntry, priceEntry);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
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

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryService.addOrUpdateCommercePriceEntry(
				priceEntry.getExternalReferenceCode(),
				GetterUtil.getLong(priceEntry.getId()), cProductId,
				cpInstanceUuid, commercePriceList.getCommercePriceListId(),
				priceEntry.getPrice(),
				(BigDecimal)GetterUtil.get(
					priceEntry.getPromoPrice(), BigDecimal.ZERO),
				priceEntry.getSkuExternalReferenceCode(), null, serviceContext);

		// Update nested resources

		_updateNestedResources(priceEntry, commercePriceEntry, serviceContext);

		return commercePriceEntry;
	}

	private List<PriceEntry> _toPriceEntries(
			List<CommercePriceEntry> commercePriceEntries)
		throws Exception {

		return transform(
			commercePriceEntries,
			commercePriceEntry -> _toPriceEntry(
				commercePriceEntry.getCommercePriceEntryId()));
	}

	private PriceEntry _toPriceEntry(Long commercePriceEntryId)
		throws Exception {

		return _priceEntryDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				commercePriceEntryId,
				contextAcceptLanguage.getPreferredLocale()));
	}

	private void _updateNestedResources(
			PriceEntry priceEntry, CommercePriceEntry commercePriceEntry,
			ServiceContext serviceContext)
		throws Exception {

		TierPrice[] tierPrices = priceEntry.getTierPrices();

		if (tierPrices != null) {
			for (TierPrice tierPrice : tierPrices) {
				TierPriceUtil.addOrUpdateCommerceTierPriceEntry(
					_commerceTierPriceEntryService, tierPrice,
					commercePriceEntry, serviceContext);
			}
		}
	}

	private CommercePriceEntry _updatePriceEntry(
			CommercePriceEntry commercePriceEntry, PriceEntry priceEntry)
		throws Exception {

		// Commerce price entry

		commercePriceEntry = _commercePriceEntryService.updatePricingInfo(
			commercePriceEntry.getCommercePriceEntryId(),
			commercePriceEntry.isBulkPricing(), priceEntry.getPrice(), false,
			priceEntry.getPromoPrice(), null,
			_serviceContextHelper.getServiceContext());

		// Update nested resources

		_updateNestedResources(
			priceEntry, commercePriceEntry,
			_serviceContextHelper.getServiceContext());

		return commercePriceEntry;
	}

	@Reference
	private CommercePriceEntryService _commercePriceEntryService;

	@Reference
	private CommercePriceListService _commercePriceListService;

	@Reference
	private CommerceTierPriceEntryService _commerceTierPriceEntryService;

	@Reference
	private CPInstanceService _cpInstanceService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.pricing.internal.dto.v1_0.converter.PriceEntryDTOConverter)"
	)
	private DTOConverter<CommercePriceEntry, PriceEntry>
		_priceEntryDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}