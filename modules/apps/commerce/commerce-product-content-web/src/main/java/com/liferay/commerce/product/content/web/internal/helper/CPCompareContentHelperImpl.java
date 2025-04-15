/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.helper;

import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.constants.CPMeasurementUnitConstants;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.helper.CPCompareContentHelper;
import com.liferay.commerce.product.content.web.internal.configuration.CPCompareContentMiniPortletInstanceConfiguration;
import com.liferay.commerce.product.content.web.internal.configuration.CPCompareContentPortletInstanceConfiguration;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPMeasurementUnit;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.option.CommerceOptionType;
import com.liferay.commerce.product.option.CommerceOptionTypeRegistry;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueLocalService;
import com.liferay.commerce.product.service.CPMeasurementUnitLocalService;
import com.liferay.commerce.product.service.CPOptionCategoryLocalService;
import com.liferay.commerce.product.util.CPCompareHelper;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = CPCompareContentHelper.class)
public class CPCompareContentHelperImpl implements CPCompareContentHelper {

	@Override
	public Set<CPSpecificationOption> getCategorizedCPSpecificationOptions(
			CPDataSourceResult cpDataSourceResult)
		throws PortalException {

		Set<CPSpecificationOption> cpSpecificationOptions =
			new LinkedHashSet<>();

		for (CPCatalogEntry cpCatalogEntry :
				cpDataSourceResult.getCPCatalogEntries()) {

			cpSpecificationOptions.addAll(
				getCPSpecificationOptions(cpCatalogEntry, true));
		}

		return cpSpecificationOptions;
	}

	@Override
	public String getCompareContentPortletNamespace() {
		return _portal.getPortletNamespace(
			CPPortletKeys.CP_COMPARE_CONTENT_WEB);
	}

	@Override
	public String getCompareProductsURL(ThemeDisplay themeDisplay)
		throws PortalException {

		Layout curLayout = themeDisplay.getLayout();

		long plid = _portal.getPlidFromPortletId(
			themeDisplay.getScopeGroupId(), curLayout.isPrivateLayout(),
			CPPortletKeys.CP_COMPARE_CONTENT_WEB);

		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout == null) {
			return StringPool.BLANK;
		}

		return _portal.getLayoutURL(layout, themeDisplay);
	}

	@Override
	public List<CPCatalogEntry> getCPCatalogEntries(
			long groupId, long commerceAccountId,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		return _cpCompareHelper.getCPCatalogEntries(
			groupId, commerceAccountId,
			CookiesManagerUtil.getCookieValue(
				_cpCompareHelper.getCPDefinitionIdsCookieKey(groupId),
				httpServletRequest));
	}

	@Override
	public Set<String> getCPDefinitionOptionRelNames(
			CPDataSourceResult cpDataSourceResult, Locale locale)
		throws PortalException {

		Set<String> cpDefinitionOptionRelNames = new HashSet<>();

		for (CPCatalogEntry cpCatalogEntry :
				cpDataSourceResult.getCPCatalogEntries()) {

			List<CPDefinitionOptionRel> cpDefinitionOptionRels =
				_getMultiValueCPDefinitionOptionRels(cpCatalogEntry);

			for (CPDefinitionOptionRel cpDefinitionOptionRel :
					cpDefinitionOptionRels) {

				String cpDefinitionOptionRelName =
					cpDefinitionOptionRel.getName(locale);

				cpDefinitionOptionRelNames.add(cpDefinitionOptionRelName);
			}
		}

		return cpDefinitionOptionRelNames;
	}

	@Override
	public String getCPDefinitionOptionValueRels(
			CPCatalogEntry cpCatalogEntry, String cpDefinitionOptionRelName,
			Locale locale)
		throws PortalException {

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpCatalogEntry.getCPDefinitionId());

		for (CPDefinitionOptionRel cpDefinitionOptionRel :
				cpDefinition.getCPDefinitionOptionRels()) {

			if (cpDefinitionOptionRelName.equals(
					cpDefinitionOptionRel.getName(locale))) {

				return StringUtil.merge(
					getCPDefinitionOptionValueRels(
						cpDefinitionOptionRel.getCPDefinitionOptionValueRels(),
						locale),
					StringPool.COMMA_AND_SPACE);
			}
		}

		return StringPool.BLANK;
	}

	@Override
	public String getCPDefinitionSpecificationOptionValue(
		long cpDefinitionId, long cpSpecificationOptionId, Locale locale) {

		List<CPDefinitionSpecificationOptionValue>
			cpDefinitionSpecificationOptionValues =
				_cpDefinitionSpecificationOptionValueLocalService.
					getCPDefinitionSpecificationOptionValuesByC_CSO(
						cpDefinitionId, cpSpecificationOptionId);

		StringBundler sb = new StringBundler(
			cpDefinitionSpecificationOptionValues.size() * 2);

		for (CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue :
					cpDefinitionSpecificationOptionValues) {

			sb.append(cpDefinitionSpecificationOptionValue.getValue(locale));
			sb.append(StringPool.COMMA_AND_SPACE);
		}

		if (!cpDefinitionSpecificationOptionValues.isEmpty()) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	@Override
	public List<CPOptionCategory> getCPOptionCategories(long groupId) {
		return _cpOptionCategoryLocalService.getCPOptionCategories(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	public Set<CPSpecificationOption> getCPSpecificationOptions(
			CPDataSourceResult cpDataSourceResult)
		throws PortalException {

		Set<CPSpecificationOption> cpSpecificationOptions =
			new LinkedHashSet<>();

		for (CPCatalogEntry cpCatalogEntry :
				cpDataSourceResult.getCPCatalogEntries()) {

			cpSpecificationOptions.addAll(
				getCPSpecificationOptions(cpCatalogEntry, false));
		}

		return cpSpecificationOptions;
	}

	@Override
	public String getDefaultImageFileURL(
			long commerceAccountId, long cpDefinitionId)
		throws PortalException {

		return _cpDefinitionHelper.getDefaultImageFileURL(
			commerceAccountId, cpDefinitionId);
	}

	@Override
	public String getDeleteCompareProductURL(
		long cpDefinitionId, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		return PortletURLBuilder.createActionURL(
			renderResponse
		).setActionName(
			"/cp_compare_content_mini_web/delete_compare_product"
		).setRedirect(
			_portal.getCurrentURL(renderRequest)
		).setParameter(
			"cpDefinitionId", cpDefinitionId
		).buildString();
	}

	@Override
	public String getDimensionCPMeasurementUnitName(
		long companyId, Locale locale) {

		CPMeasurementUnit cpMeasurementUnit =
			_cpMeasurementUnitLocalService.fetchPrimaryCPMeasurementUnit(
				companyId, CPMeasurementUnitConstants.TYPE_DIMENSION);

		if (cpMeasurementUnit == null) {
			return StringPool.BLANK;
		}

		return cpMeasurementUnit.getName(locale);
	}

	@Override
	public int getProductsLimit(PortletDisplay portletDisplay)
		throws PortalException {

		if (CPPortletKeys.CP_COMPARE_CONTENT_MINI_WEB.equals(
				portletDisplay.getPortletName())) {

			CPCompareContentMiniPortletInstanceConfiguration
				cpCompareContentMiniPortletInstanceConfiguration =
					_configurationProvider.getPortletInstanceConfiguration(
						CPCompareContentMiniPortletInstanceConfiguration.class,
						portletDisplay.getThemeDisplay());

			return cpCompareContentMiniPortletInstanceConfiguration.
				productsLimit();
		}
		else if (CPPortletKeys.CP_COMPARE_CONTENT_WEB.equals(
					portletDisplay.getPortletName())) {

			CPCompareContentPortletInstanceConfiguration
				cpCompareContentPortletInstanceConfiguration =
					_configurationProvider.getPortletInstanceConfiguration(
						CPCompareContentPortletInstanceConfiguration.class,
						portletDisplay.getThemeDisplay());

			return cpCompareContentPortletInstanceConfiguration.productsLimit();
		}

		return 0;
	}

	@Override
	public boolean hasCategorizedCPDefinitionSpecificationOptionValues(
			CPDataSourceResult cpDataSourceResult, long cpOptionCategoryId)
		throws PortalException {

		Set<CPSpecificationOption> cpSpecificationOptions =
			getCategorizedCPSpecificationOptions(cpDataSourceResult);

		for (CPSpecificationOption cpSpecificationOption :
				cpSpecificationOptions) {

			if (cpSpecificationOption.getCPOptionCategoryId() ==
					cpOptionCategoryId) {

				return true;
			}
		}

		return false;
	}

	protected List<String> getCPDefinitionOptionValueRels(
		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels,
		Locale locale) {

		return TransformUtil.transform(
			cpDefinitionOptionValueRels,
			cpDefinitionOptionValueRel -> cpDefinitionOptionValueRel.getName(
				locale));
	}

	protected List<CPSpecificationOption> getCPSpecificationOptions(
			CPCatalogEntry cpCatalogEntry, boolean categorized)
		throws PortalException {

		List<CPSpecificationOption> cpSpecificationOptions = new ArrayList<>();

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpCatalogEntry.getCPDefinitionId());

		for (CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue :
					_cpDefinitionSpecificationOptionValueLocalService.
						getCPDefinitionSpecificationOptionValues(
							cpDefinition.getCPDefinitionId(), true,
							QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			CPSpecificationOption cpSpecificationOption =
				cpDefinitionSpecificationOptionValue.getCPSpecificationOption();

			if (categorized &&
				(cpSpecificationOption.getCPOptionCategoryId() > 0)) {

				cpSpecificationOptions.add(cpSpecificationOption);
			}

			if (!categorized &&
				(cpSpecificationOption.getCPOptionCategoryId() == 0)) {

				cpSpecificationOptions.add(cpSpecificationOption);
			}
		}

		return cpSpecificationOptions;
	}

	private List<CPDefinitionOptionRel> _getMultiValueCPDefinitionOptionRels(
			CPCatalogEntry cpCatalogEntry)
		throws PortalException {

		List<CPDefinitionOptionRel> multiValueCPDefinitionOptionRels =
			new ArrayList<>();

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpCatalogEntry.getCPDefinitionId());

		for (CPDefinitionOptionRel cpDefinitionOptionRel :
				cpDefinition.getCPDefinitionOptionRels()) {

			CommerceOptionType commerceOptionType =
				_commerceOptionTypeRegistry.getCommerceOptionType(
					cpDefinitionOptionRel.getCommerceOptionTypeKey());

			if ((commerceOptionType != null) &&
				commerceOptionType.hasValues()) {

				multiValueCPDefinitionOptionRels.add(cpDefinitionOptionRel);
			}
		}

		return multiValueCPDefinitionOptionRels;
	}

	@Reference
	private CommerceOptionTypeRegistry _commerceOptionTypeRegistry;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPCompareHelper _cpCompareHelper;

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPDefinitionSpecificationOptionValueLocalService
		_cpDefinitionSpecificationOptionValueLocalService;

	@Reference
	private CPMeasurementUnitLocalService _cpMeasurementUnitLocalService;

	@Reference
	private CPOptionCategoryLocalService _cpOptionCategoryLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}