/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.resource.v1_0;

import com.liferay.account.service.AccountGroupService;
import com.liferay.commerce.discount.exception.NoSuchDiscountException;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountCommerceAccountGroupRel;
import com.liferay.commerce.discount.service.CommerceDiscountCommerceAccountGroupRelService;
import com.liferay.commerce.discount.service.CommerceDiscountService;
import com.liferay.headless.commerce.admin.pricing.dto.v1_0.DiscountAccountGroup;
import com.liferay.headless.commerce.admin.pricing.internal.util.v1_0.DiscountAccountGroupUtil;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.DiscountAccountGroupResource;
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
	properties = "OSGI-INF/liferay/rest/v1_0/discount-account-group.properties",
	scope = ServiceScope.PROTOTYPE, service = DiscountAccountGroupResource.class
)
public class DiscountAccountGroupResourceImpl
	extends BaseDiscountAccountGroupResourceImpl {

	@Override
	public Response deleteDiscountAccountGroup(Long id) throws Exception {
		_commerceDiscountCommerceAccountGroupRelService.
			deleteCommerceDiscountCommerceAccountGroupRel(id);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Page<DiscountAccountGroup>
			getDiscountByExternalReferenceCodeDiscountAccountGroupsPage(
				String externalReferenceCode, Pagination pagination)
		throws Exception {

		CommerceDiscount commerceDiscount =
			_commerceDiscountService.
				fetchCommerceDiscountByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceDiscount == null) {
			throw new NoSuchDiscountException(
				"Unable to find discount with external reference code " +
					externalReferenceCode);
		}

		List<CommerceDiscountCommerceAccountGroupRel>
			commerceDiscountCommerceAccountGroupRels =
				_commerceDiscountCommerceAccountGroupRelService.
					getCommerceDiscountCommerceAccountGroupRels(
						commerceDiscount.getCommerceDiscountId(),
						pagination.getStartPosition(),
						pagination.getEndPosition(), null);

		int totalCount =
			_commerceDiscountCommerceAccountGroupRelService.
				getCommerceDiscountCommerceAccountGroupRelsCount(
					commerceDiscount.getCommerceDiscountId());

		return Page.of(
			_toDiscountAccountGroups(commerceDiscountCommerceAccountGroupRels),
			pagination, totalCount);
	}

	@Override
	public Page<DiscountAccountGroup> getDiscountIdDiscountAccountGroupsPage(
			Long id, Pagination pagination)
		throws Exception {

		List<CommerceDiscountCommerceAccountGroupRel>
			commerceDiscountCommerceAccountGroupRels =
				_commerceDiscountCommerceAccountGroupRelService.
					getCommerceDiscountCommerceAccountGroupRels(
						id, pagination.getStartPosition(),
						pagination.getEndPosition(), null);

		int totalCount =
			_commerceDiscountCommerceAccountGroupRelService.
				getCommerceDiscountCommerceAccountGroupRelsCount(id);

		return Page.of(
			_toDiscountAccountGroups(commerceDiscountCommerceAccountGroupRels),
			pagination, totalCount);
	}

	@Override
	public DiscountAccountGroup
			postDiscountByExternalReferenceCodeDiscountAccountGroup(
				String externalReferenceCode,
				DiscountAccountGroup discountAccountGroup)
		throws Exception {

		CommerceDiscount commerceDiscount =
			_commerceDiscountService.
				fetchCommerceDiscountByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceDiscount == null) {
			throw new NoSuchDiscountException(
				"Unable to find discount with external reference code " +
					externalReferenceCode);
		}

		CommerceDiscountCommerceAccountGroupRel
			commerceDiscountCommerceAccountGroupRel =
				DiscountAccountGroupUtil.addCommerceDiscountAccountGroupRel(
					_accountGroupService,
					_commerceDiscountCommerceAccountGroupRelService,
					discountAccountGroup, commerceDiscount,
					_serviceContextHelper.getServiceContext());

		return _toDiscountAccountGroup(
			commerceDiscountCommerceAccountGroupRel.
				getCommerceDiscountCommerceAccountGroupRelId());
	}

	@Override
	public DiscountAccountGroup postDiscountIdDiscountAccountGroup(
			Long id, DiscountAccountGroup discountAccountGroup)
		throws Exception {

		CommerceDiscountCommerceAccountGroupRel
			commerceDiscountCommerceAccountGroupRel =
				DiscountAccountGroupUtil.addCommerceDiscountAccountGroupRel(
					_accountGroupService,
					_commerceDiscountCommerceAccountGroupRelService,
					discountAccountGroup,
					_commerceDiscountService.getCommerceDiscount(id),
					_serviceContextHelper.getServiceContext());

		return _toDiscountAccountGroup(
			commerceDiscountCommerceAccountGroupRel.
				getCommerceDiscountCommerceAccountGroupRelId());
	}

	private DiscountAccountGroup _toDiscountAccountGroup(
			Long commerceDiscountCommerceAccountGroupRelId)
		throws Exception {

		return _discountAccountGroupDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				commerceDiscountCommerceAccountGroupRelId,
				contextAcceptLanguage.getPreferredLocale()));
	}

	private List<DiscountAccountGroup> _toDiscountAccountGroups(
			List<CommerceDiscountCommerceAccountGroupRel>
				commerceDiscountCommerceAccountGroupRels)
		throws Exception {

		return transform(
			commerceDiscountCommerceAccountGroupRels,
			commerceDiscountCommerceAccountGroupRel -> _toDiscountAccountGroup(
				commerceDiscountCommerceAccountGroupRel.
					getCommerceDiscountCommerceAccountGroupRelId()));
	}

	@Reference
	private AccountGroupService _accountGroupService;

	@Reference
	private CommerceDiscountCommerceAccountGroupRelService
		_commerceDiscountCommerceAccountGroupRelService;

	@Reference
	private CommerceDiscountService _commerceDiscountService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.pricing.internal.dto.v1_0.converter.DiscountAccountGroupDTOConverter)"
	)
	private DTOConverter
		<CommerceDiscountCommerceAccountGroupRel, DiscountAccountGroup>
			_discountAccountGroupDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}