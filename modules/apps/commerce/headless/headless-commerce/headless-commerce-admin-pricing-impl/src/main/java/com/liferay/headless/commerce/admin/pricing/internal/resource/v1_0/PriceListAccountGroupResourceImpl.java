/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.resource.v1_0;

import com.liferay.account.service.AccountGroupService;
import com.liferay.commerce.discount.model.CommerceDiscountCommerceAccountGroupRel;
import com.liferay.commerce.price.list.exception.NoSuchPriceListException;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListCommerceAccountGroupRel;
import com.liferay.commerce.price.list.service.CommercePriceListCommerceAccountGroupRelService;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.headless.commerce.admin.pricing.dto.v1_0.PriceListAccountGroup;
import com.liferay.headless.commerce.admin.pricing.internal.util.v1_0.PriceListAccountGroupUtil;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.PriceListAccountGroupResource;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.Response;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/price-list-account-group.properties",
	scope = ServiceScope.PROTOTYPE,
	service = PriceListAccountGroupResource.class
)
public class PriceListAccountGroupResourceImpl
	extends BasePriceListAccountGroupResourceImpl {

	@Override
	public Response deletePriceListAccountGroup(Long id) throws Exception {
		_commercePriceListCommerceAccountGroupRelService.
			deleteCommercePriceListCommerceAccountGroupRel(id);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Page<PriceListAccountGroup>
			getPriceListByExternalReferenceCodePriceListAccountGroupPage(
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

		List<CommercePriceListCommerceAccountGroupRel>
			commercePriceListCommerceAccountGroupRels =
				_commercePriceListCommerceAccountGroupRelService.
					getCommercePriceListCommerceAccountGroupRels(
						commercePriceList.getCommercePriceListId(),
						pagination.getStartPosition(),
						pagination.getEndPosition(), null);

		int totalCount =
			_commercePriceListCommerceAccountGroupRelService.
				getCommercePriceListCommerceAccountGroupRelsCount(
					commercePriceList.getCommercePriceListId());

		return Page.of(
			_toPriceListAccountGroups(
				commercePriceListCommerceAccountGroupRels),
			pagination, totalCount);
	}

	@Override
	public Page<PriceListAccountGroup> getPriceListIdPriceListAccountGroupsPage(
			Long id, Pagination pagination)
		throws Exception {

		CommercePriceList commercePriceList =
			_commercePriceListService.fetchCommercePriceList(id);

		if (commercePriceList == null) {
			throw new NoSuchPriceListException(
				"Unable to find Price List with id: " + id);
		}

		List<CommercePriceListCommerceAccountGroupRel>
			commercePriceListCommerceAccountGroupRels =
				_commercePriceListCommerceAccountGroupRelService.
					getCommercePriceListCommerceAccountGroupRels(
						id, pagination.getStartPosition(),
						pagination.getEndPosition(), null);

		int totalCount =
			_commercePriceListCommerceAccountGroupRelService.
				getCommercePriceListCommerceAccountGroupRelsCount(id);

		return Page.of(
			_toPriceListAccountGroups(
				commercePriceListCommerceAccountGroupRels),
			pagination, totalCount);
	}

	@Override
	public PriceListAccountGroup
			postPriceListByExternalReferenceCodePriceListAccountGroup(
				String externalReferenceCode,
				PriceListAccountGroup priceListAccountGroup)
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

		CommercePriceListCommerceAccountGroupRel
			commercePriceListCommerceAccountGroupRel =
				PriceListAccountGroupUtil.addCommercePriceListAccountGroupRel(
					_accountGroupService,
					_commercePriceListCommerceAccountGroupRelService,
					priceListAccountGroup, commercePriceList,
					_serviceContextHelper.getServiceContext(
						commercePriceList.getGroupId()));

		return _toPriceListAccountGroup(
			commercePriceListCommerceAccountGroupRel.
				getCommercePriceListCommerceAccountGroupRelId());
	}

	@Override
	public PriceListAccountGroup postPriceListIdPriceListAccountGroup(
			Long id, PriceListAccountGroup priceListAccountGroup)
		throws Exception {

		CommercePriceList commercePriceList =
			_commercePriceListService.getCommercePriceList(id);

		CommercePriceListCommerceAccountGroupRel
			commercePriceListCommerceAccountGroupRel =
				PriceListAccountGroupUtil.addCommercePriceListAccountGroupRel(
					_accountGroupService,
					_commercePriceListCommerceAccountGroupRelService,
					priceListAccountGroup, commercePriceList,
					_serviceContextHelper.getServiceContext(
						commercePriceList.getGroupId()));

		return _toPriceListAccountGroup(
			commercePriceListCommerceAccountGroupRel.
				getCommercePriceListCommerceAccountGroupRelId());
	}

	private PriceListAccountGroup _toPriceListAccountGroup(
			Long commercePriceListCommerceAccountGroupRelId)
		throws Exception {

		return _priceListAccountGroupDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				commercePriceListCommerceAccountGroupRelId,
				contextAcceptLanguage.getPreferredLocale()));
	}

	private List<PriceListAccountGroup> _toPriceListAccountGroups(
			List<CommercePriceListCommerceAccountGroupRel>
				commercePriceListCommerceAccountGroupRels)
		throws Exception {

		return transform(
			commercePriceListCommerceAccountGroupRels,
			commercePriceListCommerceAccountGroupRel ->
				_toPriceListAccountGroup(
					commercePriceListCommerceAccountGroupRel.
						getCommercePriceListCommerceAccountGroupRelId()));
	}

	@Reference
	private AccountGroupService _accountGroupService;

	@Reference
	private CommercePriceListCommerceAccountGroupRelService
		_commercePriceListCommerceAccountGroupRelService;

	@Reference
	private CommercePriceListService _commercePriceListService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.pricing.internal.dto.v1_0.converter.PriceListAccountGroupDTOConverter)"
	)
	private DTOConverter
		<CommerceDiscountCommerceAccountGroupRel, PriceListAccountGroup>
			_priceListAccountGroupDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}