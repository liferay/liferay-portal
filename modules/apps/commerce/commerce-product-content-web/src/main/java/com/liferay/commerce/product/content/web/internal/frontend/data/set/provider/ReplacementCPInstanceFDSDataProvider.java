/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.frontend.data.set.provider;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.frontend.helper.ProductHelper;
import com.liferay.commerce.frontend.model.PriceModel;
import com.liferay.commerce.product.catalog.CPSku;
import com.liferay.commerce.product.content.web.internal.constants.CPContentFDSNames;
import com.liferay.commerce.product.content.web.internal.model.ReplacementSku;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CPContentFDSNames.REPLACEMENT_CP_INSTANCES,
	service = FDSDataProvider.class
)
public class ReplacementCPInstanceFDSDataProvider
	implements FDSDataProvider<ReplacementSku> {

	@Override
	public List<ReplacementSku> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long commerceAccountId = ParamUtil.getLong(
			httpServletRequest, "commerceAccountId");
		long commerceChannelGroupId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelGroupId");
		long commerceOrderId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderId");
		String cpInstanceUuid = ParamUtil.getString(
			httpServletRequest, "cpInstanceUuid");
		long cProductId = ParamUtil.getLong(httpServletRequest, "cProductId");

		if (sort == null) {
			sort = new Sort("sku", Sort.STRING_TYPE, false);
		}

		CommerceContext commerceContext = _commerceContextFactory.create(
			commerceAccountId, commerceChannelGroupId, null, commerceOrderId,
			_portal.getCompanyId(httpServletRequest));

		Locale locale = _portal.getLocale(httpServletRequest);

		return TransformUtil.transform(
			_getReplacementCPSkus(
				_portal.getCompanyId(httpServletRequest), cpInstanceUuid,
				cProductId, fdsKeywords, fdsPagination, sort),
			replacementCPSku -> {
				CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
					replacementCPSku.getCPInstanceId());

				CPDefinition cpDefinition = cpInstance.getCPDefinition();

				return new ReplacementSku(
					cpDefinition.getName(LocaleUtil.toLanguageId(locale)),
					_getPriceModel(
						commerceContext, cpDefinition.getCPDefinitionId(),
						cpInstance.getCPInstanceId(), StringPool.BLANK, locale),
					cpInstance.getCPInstanceId(), cpInstance.getSku());
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		String cpInstanceUuid = ParamUtil.getString(
			httpServletRequest, "cpInstanceUuid");
		long cProductId = ParamUtil.getLong(httpServletRequest, "cProductId");

		return _cpInstanceLocalService.searchCPInstancesCount(
			_portal.getCompanyId(httpServletRequest), cpInstanceUuid,
			cProductId, fdsKeywords.getKeywords(),
			WorkflowConstants.STATUS_APPROVED);
	}

	private PriceModel _getPriceModel(
			CommerceContext commerceContext, long cpDefinitionId,
			long cpInstanceId, String unitOfMeasureKey, Locale locale)
		throws PortalException {

		if (cpInstanceId > 0) {
			return _productHelper.getPriceModel(
				cpInstanceId, StringPool.BLANK, BigDecimal.ONE,
				unitOfMeasureKey, commerceContext, locale);
		}

		return _productHelper.getMinPriceModel(
			cpDefinitionId, commerceContext, locale);
	}

	private List<CPSku> _getReplacementCPSkus(
			long companyId, String cpInstanceUuid, long cProductId,
			FDSKeywords fdsKeywords, FDSPagination fdsPagination, Sort sort)
		throws PortalException {

		List<CPSku> cpSkus = new ArrayList<>();

		BaseModelSearchResult<CPInstance> cpInstanceBaseModelSearchResult =
			_cpInstanceLocalService.searchCPInstances(
				companyId, cpInstanceUuid, cProductId,
				fdsKeywords.getKeywords(), WorkflowConstants.STATUS_APPROVED,
				fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition(), sort);

		for (CPInstance replacementCPInstance :
				cpInstanceBaseModelSearchResult.getBaseModels()) {

			cpSkus.add(_cpInstanceHelper.toCPSku(replacementCPInstance));
		}

		return cpSkus;
	}

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private ProductHelper _productHelper;

}