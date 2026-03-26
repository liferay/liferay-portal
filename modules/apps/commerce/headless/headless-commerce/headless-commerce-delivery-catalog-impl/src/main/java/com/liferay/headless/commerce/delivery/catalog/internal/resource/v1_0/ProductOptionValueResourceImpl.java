/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.resource.v1_0;

import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.commerce.product.exception.NoSuchCProductException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelLocalService;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.ProductOptionValue;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.SkuOption;
import com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.commerce.delivery.catalog.internal.util.v1_0.AccountUtil;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.ProductOptionValueResource;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/product-option-value.properties",
	scope = ServiceScope.PROTOTYPE, service = ProductOptionValueResource.class
)
@CTAware
public class ProductOptionValueResourceImpl
	extends BaseProductOptionValueResourceImpl {

	@Override
	public Page<ProductOptionValue>
			getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage(
				String channelExternalReferenceCode,
				String productExternalReferenceCode,
				String productOptionExternalReferenceCode, Long accountId,
				String currencyCode, Long productOptionValueId, Long skuId,
				Pagination pagination)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.
				getCommerceChannelByExternalReferenceCode(
					channelExternalReferenceCode,
					contextCompany.getCompanyId());

		CProduct cProduct =
			_cProductLocalService.getCProductByExternalReferenceCode(
				productExternalReferenceCode, contextCompany.getCompanyId());

		CPOption cpOption =
			_cpOptionLocalService.getCPOptionByExternalReferenceCode(
				productOptionExternalReferenceCode,
				contextCompany.getCompanyId());

		return getChannelProductProductOptionProductOptionValuesPage(
			commerceChannel.getCommerceChannelId(), cProduct.getCProductId(),
			cpOption.getCPOptionId(), accountId, currencyCode,
			productOptionValueId, skuId, pagination);
	}

	@Override
	public Page<ProductOptionValue>
			getChannelProductProductOptionProductOptionValuesPage(
				Long channelId, Long productId, Long productOptionId,
				Long accountId, String currencyCode, Long productOptionValueId,
				Long skuId, Pagination pagination)
		throws Exception {

		return _getProductOptionValuePage(
			channelId, productId, productOptionId, accountId, currencyCode,
			productOptionValueId, skuId, pagination, null);
	}

	@Override
	public Page<ProductOptionValue>
			postChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage(
				String channelExternalReferenceCode,
				String productExternalReferenceCode,
				String productOptionExternalReferenceCode, Long accountId,
				String currencyCode, Long productOptionValueId, Long skuId,
				Pagination pagination, SkuOption[] skuOptions)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.
				getCommerceChannelByExternalReferenceCode(
					channelExternalReferenceCode,
					contextCompany.getCompanyId());

		CProduct cProduct =
			_cProductLocalService.getCProductByExternalReferenceCode(
				productExternalReferenceCode, contextCompany.getCompanyId());

		CPOption cpOption =
			_cpOptionLocalService.getCPOptionByExternalReferenceCode(
				productOptionExternalReferenceCode,
				contextCompany.getCompanyId());

		return postChannelProductProductOptionProductOptionValuesPage(
			commerceChannel.getCommerceChannelId(), cProduct.getCProductId(),
			cpOption.getCPOptionId(), accountId, currencyCode,
			productOptionValueId, skuId, pagination, skuOptions);
	}

	@Override
	public Page<ProductOptionValue>
			postChannelProductProductOptionProductOptionValuesPage(
				Long channelId, Long productId, Long productOptionId,
				Long accountId, String currencyCode, Long productOptionValueId,
				Long skuId, Pagination pagination, SkuOption[] skuOptions)
		throws Exception {

		return _getProductOptionValuePage(
			channelId, productId, productOptionId, accountId, currencyCode,
			productOptionValueId, skuId, pagination, skuOptions);
	}

	private Page<ProductOptionValue> _getProductOptionValuePage(
			Long channelId, Long productId, Long productOptionId,
			Long accountId, String currencyCode, Long productOptionValueId,
			Long skuId, Pagination pagination, SkuOption[] skuOptions)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionLocalService.fetchCPDefinitionByCProductId(
				productId, true);

		if (cpDefinition == null) {
			throw new NoSuchCProductException();
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(channelId);

		accountId = AccountUtil.getAccountId(
			contextCompany.getCompanyId(), commerceChannel.getGroupId(),
			contextUser.getUserId(), _accountEntryLocalService,
			_accountEntryService, accountId, _commerceAccountHelper, null);

		_commerceProductViewPermission.check(
			PermissionThreadLocal.getPermissionChecker(), accountId,
			commerceChannel.getGroupId(), cpDefinition.getCPDefinitionId());

		ServiceContextThreadLocal.pushServiceContext(
			_serviceContextHelper.getServiceContext(
				commerceChannel.getGroupId()));

		return Page.of(
			_toProductOptionValues(
				_commerceContextFactory.create(
					accountId, commerceChannel.getGroupId(), currencyCode, 0,
					contextCompany.getCompanyId()),
				_cpDefinitionOptionValueRelLocalService.
					getCPDefinitionOptionValueRels(
						productOptionId, pagination.getStartPosition(),
						pagination.getEndPosition()),
				productOptionValueId, skuId, skuOptions),
			pagination,
			_cpDefinitionOptionValueRelLocalService.
				getCPDefinitionOptionValueRelsCount(productOptionId));
	}

	private List<ProductOptionValue> _toProductOptionValues(
			CommerceContext commerceContext,
			List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels,
			Long productOptionValueId, Long skuId, SkuOption[] skuOptions)
		throws Exception {

		return transform(
			cpDefinitionOptionValueRels,
			cpDefinitionOptionValueRel -> {
				DefaultDTOConverterContext defaultDTOConverterContext =
					new DefaultDTOConverterContext(
						cpDefinitionOptionValueRel.
							getCPDefinitionOptionValueRelId(),
						contextAcceptLanguage.getPreferredLocale());

				defaultDTOConverterContext.setAttribute(
					"commerceContext", commerceContext);
				defaultDTOConverterContext.setAttribute(
					"productOptionValueId", productOptionValueId);
				defaultDTOConverterContext.setAttribute("skuId", skuId);
				defaultDTOConverterContext.setAttribute(
					"skuOptions", skuOptions);

				return _productOptionValueDTOConverter.toDTO(
					defaultDTOConverterContext, cpDefinitionOptionValueRel);
			});
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CommerceProductViewPermission _commerceProductViewPermission;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPDefinitionOptionValueRelLocalService
		_cpDefinitionOptionValueRelLocalService;

	@Reference
	private CPOptionLocalService _cpOptionLocalService;

	@Reference
	private CProductLocalService _cProductLocalService;

	@Reference(
		target = DTOConverterConstants.PRODUCT_OPTION_VALUE_DTO_CONVERTER
	)
	private DTOConverter<CPDefinitionOptionValueRel, ProductOptionValue>
		_productOptionValueDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}