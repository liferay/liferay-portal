/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.resource.v1_0;

import com.liferay.commerce.price.list.exception.NoSuchPriceEntryException;
import com.liferay.commerce.price.list.exception.NoSuchTierPriceEntryException;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.service.CommercePriceEntryService;
import com.liferay.commerce.price.list.service.CommerceTierPriceEntryService;
import com.liferay.headless.commerce.admin.pricing.dto.v1_0.TierPrice;
import com.liferay.headless.commerce.admin.pricing.internal.util.v1_0.TierPriceUtil;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.TierPriceResource;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.kernel.util.GetterUtil;
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
	properties = "OSGI-INF/liferay/rest/v1_0/tier-price.properties",
	scope = ServiceScope.PROTOTYPE, service = TierPriceResource.class
)
public class TierPriceResourceImpl extends BaseTierPriceResourceImpl {

	@Override
	public Response deleteTierPrice(Long id) throws Exception {
		_commerceTierPriceEntryService.deleteCommerceTierPriceEntry(id);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response deleteTierPriceByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceTierPriceEntry commerceTierPriceEntry =
			_commerceTierPriceEntryService.
				fetchCommerceTierPriceEntryByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceTierPriceEntry == null) {
			throw new NoSuchTierPriceEntryException(
				"Unable to find tier price with external reference code " +
					externalReferenceCode);
		}

		_commerceTierPriceEntryService.deleteCommerceTierPriceEntry(
			commerceTierPriceEntry.getCommerceTierPriceEntryId());

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Page<TierPrice> getPriceEntryByExternalReferenceCodeTierPricesPage(
			String externalReferenceCode, Pagination pagination)
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

		List<CommerceTierPriceEntry> commerceTierPriceEntries =
			_commerceTierPriceEntryService.getCommerceTierPriceEntries(
				commercePriceEntry.getCommercePriceEntryId(),
				pagination.getStartPosition(), pagination.getEndPosition());

		int totalCount =
			_commerceTierPriceEntryService.getCommerceTierPriceEntriesCount(
				commercePriceEntry.getCommercePriceEntryId());

		return Page.of(
			_toTierPrices(commerceTierPriceEntries), pagination, totalCount);
	}

	@Override
	public Page<TierPrice> getPriceEntryIdTierPricesPage(
			Long id, Pagination pagination)
		throws Exception {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryService.fetchCommercePriceEntry(id);

		if (commercePriceEntry == null) {
			throw new NoSuchPriceEntryException(
				"Unable to find Price Entry with id: " + id);
		}

		List<CommerceTierPriceEntry> commerceTierPriceEntries =
			_commerceTierPriceEntryService.getCommerceTierPriceEntries(
				id, pagination.getStartPosition(), pagination.getEndPosition());

		int totalCount =
			_commerceTierPriceEntryService.getCommerceTierPriceEntriesCount(id);

		return Page.of(
			_toTierPrices(commerceTierPriceEntries), pagination, totalCount);
	}

	@Override
	public TierPrice getTierPrice(Long id) throws Exception {
		return _toTierPrice(GetterUtil.getLong(id));
	}

	@Override
	public TierPrice getTierPriceByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceTierPriceEntry commerceTierPriceEntry =
			_commerceTierPriceEntryService.
				fetchCommerceTierPriceEntryByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceTierPriceEntry == null) {
			throw new NoSuchTierPriceEntryException(
				"Unable to find tier price with external reference code " +
					externalReferenceCode);
		}

		return _toTierPrice(
			commerceTierPriceEntry.getCommerceTierPriceEntryId());
	}

	@Override
	public Response patchTierPrice(Long id, TierPrice tierPrice)
		throws Exception {

		_updateCommerceTierPriceEntry(
			_commerceTierPriceEntryService.getCommerceTierPriceEntry(id),
			tierPrice);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response patchTierPriceByExternalReferenceCode(
			String externalReferenceCode, TierPrice tierPrice)
		throws Exception {

		CommerceTierPriceEntry commerceTierPriceEntry =
			_commerceTierPriceEntryService.
				fetchCommerceTierPriceEntryByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceTierPriceEntry == null) {
			throw new NoSuchTierPriceEntryException(
				"Unable to find tier price with external reference code " +
					externalReferenceCode);
		}

		_updateCommerceTierPriceEntry(commerceTierPriceEntry, tierPrice);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public TierPrice postPriceEntryByExternalReferenceCodeTierPrice(
			String externalReferenceCode, TierPrice tierPrice)
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

		CommerceTierPriceEntry commerceTierPriceEntry =
			TierPriceUtil.addOrUpdateCommerceTierPriceEntry(
				_commerceTierPriceEntryService, tierPrice, commercePriceEntry,
				_serviceContextHelper.getServiceContext());

		return _toTierPrice(
			commerceTierPriceEntry.getCommerceTierPriceEntryId());
	}

	@Override
	public TierPrice postPriceEntryIdTierPrice(Long id, TierPrice tierPrice)
		throws Exception {

		CommerceTierPriceEntry commerceTierPriceEntry =
			TierPriceUtil.addOrUpdateCommerceTierPriceEntry(
				_commerceTierPriceEntryService, tierPrice,
				_commercePriceEntryService.getCommercePriceEntry(id),
				_serviceContextHelper.getServiceContext());

		return _toTierPrice(
			commerceTierPriceEntry.getCommerceTierPriceEntryId());
	}

	private TierPrice _toTierPrice(Long commerceTierPriceEntryId)
		throws Exception {

		return _tierPriceDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				commerceTierPriceEntryId,
				contextAcceptLanguage.getPreferredLocale()));
	}

	private List<TierPrice> _toTierPrices(
			List<CommerceTierPriceEntry> commerceTierPriceEntries)
		throws Exception {

		return transform(
			commerceTierPriceEntries,
			commerceTierPriceEntry -> _toTierPrice(
				commerceTierPriceEntry.getCommerceTierPriceEntryId()));
	}

	private CommerceTierPriceEntry _updateCommerceTierPriceEntry(
			CommerceTierPriceEntry commerceTierPriceEntry, TierPrice tierPrice)
		throws Exception {

		return _commerceTierPriceEntryService.updateCommerceTierPriceEntry(
			commerceTierPriceEntry.getCommerceTierPriceEntryId(),
			tierPrice.getPrice(), tierPrice.getPromoPrice(),
			(BigDecimal)GetterUtil.get(
				tierPrice.getMinimumQuantity(),
				commerceTierPriceEntry.getMinQuantity()),
			_serviceContextHelper.getServiceContext());
	}

	@Reference
	private CommercePriceEntryService _commercePriceEntryService;

	@Reference
	private CommerceTierPriceEntryService _commerceTierPriceEntryService;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.pricing.internal.dto.v1_0.converter.TierPriceDTOConverter)"
	)
	private DTOConverter<CommerceTierPriceEntry, TierPrice>
		_tierPriceDTOConverter;

}