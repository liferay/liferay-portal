/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.currency.constants.CommerceCurrencyConstants;
import com.liferay.commerce.currency.constants.CommerceCurrencyPortletKeys;
import com.liferay.commerce.currency.exception.NoSuchCurrencyException;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyService;
import com.liferay.exportimport.constants.ExportImportConstants;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Currency;
import com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.commerce.admin.catalog.internal.odata.entity.v1_0.CurrencyEntityModel;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.CurrencyResource;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.math.BigDecimal;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Zoltán Takács
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/currency.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE, service = CurrencyResource.class
)
public class CurrencyResourceImpl
	extends BaseCurrencyResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<Currency> {

	@Override
	public void deleteCurrency(Long id) throws Exception {
		_commerceCurrencyService.deleteCommerceCurrency(id);
	}

	@Override
	public void deleteCurrencyByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceCurrency commerceCurrency =
			_commerceCurrencyService.
				fetchCommerceCurrencyByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceCurrency == null) {
			throw new NoSuchCurrencyException(
				"Unable to find currency with external reference code " +
					externalReferenceCode);
		}

		_commerceCurrencyService.deleteCommerceCurrency(
			commerceCurrency.getCommerceCurrencyId());
	}

	@Override
	public Page<Currency> getCurrenciesPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CommerceCurrency.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toCurrency(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public Currency getCurrency(Long id) throws Exception {
		return _toCurrency(_commerceCurrencyService.getCommerceCurrency(id));
	}

	@Override
	public Currency getCurrencyByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceCurrency commerceCurrency =
			_commerceCurrencyService.
				fetchCommerceCurrencyByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceCurrency == null) {
			throw new NoSuchCurrencyException(
				"Unable to find currency with external reference code " +
					externalReferenceCode);
		}

		return _toCurrency(
			_commerceCurrencyService.getCommerceCurrency(
				commerceCurrency.getCommerceCurrencyId()));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public ExportImportDescriptor<CommerceCurrency>
		getExportImportDescriptor() {

		return new ExportImportDescriptor<>() {

			@Override
			public String getKey() {
				return CurrencyResourceImpl.class.getName();
			}

			@Override
			public String getLabelLanguageKey() {
				return "currencies";
			}

			@Override
			public Class<CommerceCurrency> getModelClass() {
				return CommerceCurrency.class;
			}

			@Override
			public List<String> getNestedFields() {
				return List.of("creator");
			}

			@Override
			public String getPortletId() {
				return CommerceCurrencyPortletKeys.COMMERCE_CURRENCY;
			}

			@Override
			public Scope getScope() {
				return Scope.COMPANY;
			}

			@Override
			public String getSectionKey() {
				return ExportImportConstants.SECTION_KEY_STORE_MANAGEMENT;
			}

		};
	}

	@Override
	public Currency patchCurrency(Long id, Currency currency) throws Exception {
		return _updateCurrency(
			_commerceCurrencyService.getCommerceCurrency(id), currency);
	}

	@Override
	public Currency patchCurrencyByExternalReferenceCode(
			String externalReferenceCode, Currency currency)
		throws Exception {

		CommerceCurrency commerceCurrency =
			_commerceCurrencyService.
				fetchCommerceCurrencyByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceCurrency == null) {
			throw new NoSuchCurrencyException(
				"Unable to find currency with external reference code " +
					externalReferenceCode);
		}

		return _updateCurrency(commerceCurrency, currency);
	}

	@Override
	public Currency postCurrency(Currency currency) throws Exception {
		Map<Locale, String> formatPatternMap = LanguageUtils.getLocalizedMap(
			currency.getFormatPattern());

		if (MapUtil.isEmpty(formatPatternMap)) {
			formatPatternMap = _localization.getLocalizationMap(
				CommerceCurrencyConstants.DECIMAL_FORMAT_PATTERN);
		}

		return _toCurrency(
			_commerceCurrencyService.addCommerceCurrency(
				GetterUtil.getString(
					currency.getExternalReferenceCode(), currency.getCode()),
				currency.getCode(),
				LanguageUtils.getLocalizedMap(currency.getName()),
				GetterUtil.getString(currency.getSymbol()),
				(BigDecimal)GetterUtil.getNumber(currency.getRate()),
				formatPatternMap,
				GetterUtil.getInteger(currency.getMaxFractionDigits(), 2),
				GetterUtil.getInteger(currency.getMinFractionDigits(), 2),
				GetterUtil.getString(
					currency.getRoundingModeAsString(),
					Currency.RoundingMode.HALF_EVEN.getValue()),
				GetterUtil.getBoolean(currency.getPrimary()),
				GetterUtil.getDouble(currency.getPriority()),
				GetterUtil.getBoolean(currency.getActive())));
	}

	@Override
	public Currency putCurrencyByExternalReferenceCode(
			String externalReferenceCode, Currency currency)
		throws Exception {

		Map<Locale, String> formatPatternMap = LanguageUtils.getLocalizedMap(
			currency.getFormatPattern());

		if (MapUtil.isEmpty(formatPatternMap)) {
			formatPatternMap = _localization.getLocalizationMap(
				CommerceCurrencyConstants.DECIMAL_FORMAT_PATTERN);
		}

		CommerceCurrency commerceCurrency =
			_commerceCurrencyService.
				fetchCommerceCurrencyByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceCurrency == null) {
			return _toCurrency(
				_commerceCurrencyService.addCommerceCurrency(
					externalReferenceCode, currency.getCode(),
					LanguageUtils.getLocalizedMap(currency.getName()),
					GetterUtil.getString(currency.getSymbol()),
					(BigDecimal)GetterUtil.getNumber(
						currency.getRate(), BigDecimal.ONE),
					formatPatternMap,
					GetterUtil.getInteger(currency.getMaxFractionDigits(), 2),
					GetterUtil.getInteger(currency.getMinFractionDigits(), 2),
					GetterUtil.getString(
						currency.getRoundingModeAsString(),
						Currency.RoundingMode.HALF_EVEN.getValue()),
					GetterUtil.getBoolean(currency.getPrimary()),
					GetterUtil.getDouble(currency.getPriority()),
					GetterUtil.getBoolean(currency.getActive())));
		}

		return _toCurrency(
			_commerceCurrencyService.updateCommerceCurrency(
				externalReferenceCode, commerceCurrency.getCommerceCurrencyId(),
				LanguageUtils.getLocalizedMap(currency.getName()),
				GetterUtil.getString(currency.getSymbol()),
				(BigDecimal)GetterUtil.getNumber(
					currency.getRate(), BigDecimal.ONE),
				formatPatternMap,
				GetterUtil.getInteger(currency.getMaxFractionDigits(), 2),
				GetterUtil.getInteger(currency.getMinFractionDigits(), 2),
				GetterUtil.getString(
					currency.getRoundingModeAsString(),
					Currency.RoundingMode.HALF_EVEN.getValue()),
				GetterUtil.getBoolean(currency.getPrimary()),
				GetterUtil.getDouble(currency.getPriority()),
				GetterUtil.getBoolean(currency.getActive()),
				_serviceContextHelper.getServiceContext(
					contextUser.getUserId())));
	}

	private Currency _toCurrency(CommerceCurrency commerceCurrency)
		throws Exception {

		return _toCurrency(commerceCurrency.getCommerceCurrencyId());
	}

	private Currency _toCurrency(Long commerceCurrencyId) throws Exception {
		CommerceCurrency commerceCurrency =
			_commerceCurrencyService.getCommerceCurrency(commerceCurrencyId);

		return new Currency() {
			{
				setActive(commerceCurrency::isActive);
				setCode(commerceCurrency::getCode);
				setCreator(
					() -> NestedFieldsSupplier.supply(
						"creator",
						fieldName -> CreatorUtil.toCreator(
							_portal,
							_userLocalService.fetchUser(
								commerceCurrency.getUserId()))));
				setDateCreated(commerceCurrency::getCreateDate);
				setDateModified(commerceCurrency::getModifiedDate);
				setExternalReferenceCode(
					commerceCurrency::getExternalReferenceCode);
				setFormatPattern(
					() -> LanguageUtils.getLanguageIdMap(
						commerceCurrency.getFormatPatternMap()));
				setId(commerceCurrency::getCommerceCurrencyId);
				setMaxFractionDigits(commerceCurrency::getMaxFractionDigits);
				setMinFractionDigits(commerceCurrency::getMinFractionDigits);
				setName(
					() -> LanguageUtils.getLanguageIdMap(
						commerceCurrency.getNameMap()));
				setPrimary(commerceCurrency::isPrimary);
				setPriority(commerceCurrency::getPriority);
				setRate(commerceCurrency::getRate);
				setRoundingMode(
					() -> RoundingMode.valueOf(
						commerceCurrency.getRoundingMode()));
				setSymbol(commerceCurrency::getSymbol);
			}
		};
	}

	private Currency _updateCurrency(
			CommerceCurrency commerceCurrency, Currency currency)
		throws Exception {

		Map<String, String> nameMap = currency.getName();

		if (nameMap == null) {
			nameMap = LanguageUtils.getLanguageIdMap(
				commerceCurrency.getNameMap());
		}

		Map<String, String> formatPatternMap = currency.getFormatPattern();

		if (formatPatternMap == null) {
			formatPatternMap = LanguageUtils.getLanguageIdMap(
				commerceCurrency.getFormatPatternMap());
		}

		return _toCurrency(
			_commerceCurrencyService.updateCommerceCurrency(
				GetterUtil.getString(
					currency.getExternalReferenceCode(),
					commerceCurrency.getExternalReferenceCode()),
				commerceCurrency.getCommerceCurrencyId(),
				LanguageUtils.getLocalizedMap(nameMap),
				GetterUtil.getString(
					currency.getSymbol(), commerceCurrency.getSymbol()),
				(BigDecimal)GetterUtil.getNumber(
					currency.getRate(), commerceCurrency.getRate()),
				LanguageUtils.getLocalizedMap(formatPatternMap),
				GetterUtil.getInteger(
					currency.getMaxFractionDigits(),
					commerceCurrency.getMaxFractionDigits()),
				GetterUtil.getInteger(
					currency.getMinFractionDigits(),
					commerceCurrency.getMinFractionDigits()),
				GetterUtil.getString(
					currency.getRoundingModeAsString(),
					commerceCurrency.getRoundingMode()),
				GetterUtil.getBoolean(
					currency.getPrimary(), commerceCurrency.isPrimary()),
				GetterUtil.getDouble(
					currency.getPriority(), commerceCurrency.getPriority()),
				GetterUtil.getBoolean(
					currency.getActive(), commerceCurrency.isActive()),
				_serviceContextHelper.getServiceContext(
					contextUser.getUserId())));
	}

	private static final EntityModel _entityModel = new CurrencyEntityModel();

	@Reference
	private CommerceCurrencyService _commerceCurrencyService;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

	@Reference
	private UserLocalService _userLocalService;

}