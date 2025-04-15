/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0;

import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.commerce.price.list.exception.NoSuchPriceListException;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.pricing.exception.NoSuchPriceModifierException;
import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.pricing.service.CommercePriceModifierRelService;
import com.liferay.commerce.pricing.service.CommercePriceModifierService;
import com.liferay.commerce.pricing.service.CommercePricingClassService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifier;
import com.liferay.headless.commerce.admin.pricing.internal.util.v2_0.PriceModifierUtil;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.PriceModifierResource;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Riccardo Alberti
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v2_0/price-modifier.properties",
	scope = ServiceScope.PROTOTYPE, service = PriceModifierResource.class
)
public class PriceModifierResourceImpl extends BasePriceModifierResourceImpl {

	@Override
	public void deletePriceModifier(Long id) throws Exception {
		_commercePriceModifierService.deleteCommercePriceModifier(id);
	}

	@Override
	public void deletePriceModifierByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommercePriceModifier commercePriceModifier =
			_commercePriceModifierService.
				fetchCommercePriceModifierByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commercePriceModifier == null) {
			throw new NoSuchPriceModifierException(
				"Unable to find price modifier with external reference code " +
					externalReferenceCode);
		}

		_commercePriceModifierService.deleteCommercePriceModifier(
			commercePriceModifier.getCommercePriceModifierId());
	}

	@Override
	public Page<PriceModifier>
			getPriceListByExternalReferenceCodePriceModifiersPage(
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

		List<CommercePriceModifier> commercePriceModifiers =
			_commercePriceModifierService.getCommercePriceModifiers(
				commercePriceList.getCommercePriceListId(),
				pagination.getStartPosition(), pagination.getEndPosition(),
				null);

		int totalCount =
			_commercePriceModifierService.getCommercePriceModifiersCount(
				commercePriceList.getCommercePriceListId());

		return Page.of(
			_toPriceModifiers(commercePriceModifiers), pagination, totalCount);
	}

	@Override
	public Page<PriceModifier> getPriceListIdPriceModifiersPage(
			Long id, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		List<CommercePriceModifier> commercePriceModifiers =
			_commercePriceModifierService.getCommercePriceModifiers(
				id, pagination.getStartPosition(), pagination.getEndPosition(),
				null);

		int totalCount =
			_commercePriceModifierService.getCommercePriceModifiersCount(id);

		return Page.of(
			_toPriceModifiers(commercePriceModifiers), pagination, totalCount);
	}

	@Override
	public PriceModifier getPriceModifier(Long id) throws Exception {
		CommercePriceModifier commercePriceModifier =
			_commercePriceModifierService.getCommercePriceModifier(id);

		return _toPriceModifier(
			commercePriceModifier.getCommercePriceModifierId());
	}

	@Override
	public PriceModifier getPriceModifierByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommercePriceModifier commercePriceModifier =
			_commercePriceModifierService.
				fetchCommercePriceModifierByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commercePriceModifier == null) {
			throw new NoSuchPriceModifierException(
				"Unable to find price modifier with external reference code " +
					externalReferenceCode);
		}

		return _toPriceModifier(
			commercePriceModifier.getCommercePriceModifierId());
	}

	@Override
	public Response patchPriceModifier(Long id, PriceModifier priceModifier)
		throws Exception {

		_updatePriceModifier(
			_commercePriceModifierService.getCommercePriceModifier(id),
			priceModifier);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response patchPriceModifierByExternalReferenceCode(
			String externalReferenceCode, PriceModifier priceModifier)
		throws Exception {

		CommercePriceModifier commercePriceModifier =
			_commercePriceModifierService.
				fetchCommercePriceModifierByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commercePriceModifier == null) {
			throw new NoSuchPriceModifierException(
				"Unable to find price modifier with external reference code " +
					externalReferenceCode);
		}

		_updatePriceModifier(commercePriceModifier, priceModifier);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public PriceModifier postPriceListByExternalReferenceCodePriceModifier(
			String externalReferenceCode, PriceModifier priceModifier)
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

		CommercePriceModifier commercePriceModifier =
			_addOrUpdateCommercePriceModifier(commercePriceList, priceModifier);

		return _toPriceModifier(
			commercePriceModifier.getCommercePriceModifierId());
	}

	@Override
	public PriceModifier postPriceListIdPriceModifier(
			Long id, PriceModifier priceModifier)
		throws Exception {

		CommercePriceModifier commercePriceModifier =
			_addOrUpdateCommercePriceModifier(
				_commercePriceListService.getCommercePriceList(id),
				priceModifier);

		return _toPriceModifier(
			commercePriceModifier.getCommercePriceModifierId());
	}

	private CommercePriceModifier _addOrUpdateCommercePriceModifier(
			CommercePriceList commercePriceList, PriceModifier priceModifier)
		throws Exception {

		ServiceContext serviceContext = _serviceContextHelper.getServiceContext(
			commercePriceList.getGroupId());

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			priceModifier.getDisplayDate(), serviceContext.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			priceModifier.getExpirationDate(), serviceContext.getTimeZone());

		CommercePriceModifier commercePriceModifier =
			_commercePriceModifierService.addOrUpdateCommercePriceModifier(
				priceModifier.getExternalReferenceCode(),
				GetterUtil.getLong(priceModifier.getId()),
				commercePriceList.getGroupId(), priceModifier.getTitle(),
				priceModifier.getTarget(),
				commercePriceList.getCommercePriceListId(),
				priceModifier.getModifierType(),
				priceModifier.getModifierAmount(),
				GetterUtil.get(priceModifier.getPriority(), 0D),
				GetterUtil.getBoolean(priceModifier.getActive(), true),
				displayDateConfig.getMonth(), displayDateConfig.getDay(),
				displayDateConfig.getYear(), displayDateConfig.getHour(),
				displayDateConfig.getMinute(), expirationDateConfig.getMonth(),
				expirationDateConfig.getDay(), expirationDateConfig.getYear(),
				expirationDateConfig.getHour(),
				expirationDateConfig.getMinute(),
				GetterUtil.getBoolean(priceModifier.getNeverExpire(), true),
				serviceContext);

		// Update nested resources

		_updateNestedResources(priceModifier, commercePriceModifier);

		return commercePriceModifier;
	}

	private Map<String, Map<String, String>> _getActions(
			CommercePriceModifier commercePriceModifier)
		throws Exception {

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			addAction(
				"UPDATE", commercePriceModifier.getCommercePriceModifierId(),
				"deletePriceModifier",
				_commercePriceModifierModelResourcePermission)
		).put(
			"get",
			addAction(
				"VIEW", commercePriceModifier.getCommercePriceModifierId(),
				"getPriceModifier",
				_commercePriceModifierModelResourcePermission)
		).put(
			"update",
			addAction(
				"UPDATE", commercePriceModifier.getCommercePriceModifierId(),
				"patchPriceModifier",
				_commercePriceModifierModelResourcePermission)
		).build();
	}

	private PriceModifier _toPriceModifier(Long commercePriceModifierId)
		throws Exception {

		CommercePriceModifier commercePriceModifier =
			_commercePriceModifierService.getCommercePriceModifier(
				commercePriceModifierId);

		return _priceModifierDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(commercePriceModifier), _dtoConverterRegistry,
				commercePriceModifierId,
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private List<PriceModifier> _toPriceModifiers(
			List<CommercePriceModifier> commercePriceModifiers)
		throws Exception {

		return transform(
			commercePriceModifiers,
			commercePriceModifier -> _toPriceModifier(
				commercePriceModifier.getCommercePriceModifierId()));
	}

	private void _updateNestedResources(
			PriceModifier priceModifier,
			CommercePriceModifier commercePriceModifier)
		throws Exception {

		PriceModifierUtil.addOrUpdateCommercePriceModifierRels(
			contextCompany.getGroupId(), _assetCategoryLocalService,
			_commercePricingClassService, _cProductLocalService,
			_commercePriceModifierRelService, priceModifier,
			commercePriceModifier, _serviceContextHelper);
	}

	private CommercePriceModifier _updatePriceModifier(
			CommercePriceModifier commercePriceModifier,
			PriceModifier priceModifier)
		throws Exception {

		ServiceContext serviceContext =
			_serviceContextHelper.getServiceContext();

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			priceModifier.getDisplayDate(), serviceContext.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			priceModifier.getExpirationDate(), serviceContext.getTimeZone());

		commercePriceModifier =
			_commercePriceModifierService.updateCommercePriceModifier(
				commercePriceModifier.getCommercePriceModifierId(),
				commercePriceModifier.getGroupId(), priceModifier.getTitle(),
				priceModifier.getTarget(), priceModifier.getPriceListId(),
				priceModifier.getModifierType(),
				priceModifier.getModifierAmount(), priceModifier.getPriority(),
				GetterUtil.getBoolean(priceModifier.getActive(), true),
				displayDateConfig.getMonth(), displayDateConfig.getDay(),
				displayDateConfig.getYear(), displayDateConfig.getHour(),
				displayDateConfig.getMinute(), expirationDateConfig.getMonth(),
				expirationDateConfig.getDay(), expirationDateConfig.getYear(),
				expirationDateConfig.getHour(),
				expirationDateConfig.getMinute(),
				GetterUtil.getBoolean(priceModifier, true), serviceContext);

		// Update nested resources

		_updateNestedResources(priceModifier, commercePriceModifier);

		return commercePriceModifier;
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private CommercePriceListService _commercePriceListService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.pricing.model.CommercePriceModifier)"
	)
	private ModelResourcePermission<CommercePriceModifier>
		_commercePriceModifierModelResourcePermission;

	@Reference
	private CommercePriceModifierRelService _commercePriceModifierRelService;

	@Reference
	private CommercePriceModifierService _commercePriceModifierService;

	@Reference
	private CommercePricingClassService _commercePricingClassService;

	@Reference
	private CProductLocalService _cProductLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter.PriceModifierDTOConverter)"
	)
	private DTOConverter<CommercePriceModifier, PriceModifier>
		_priceModifierDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}