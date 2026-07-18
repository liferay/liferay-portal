/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.service.impl;

import com.liferay.commerce.currency.configuration.CommerceCurrencyConfiguration;
import com.liferay.commerce.currency.configuration.RoundingTypeConfiguration;
import com.liferay.commerce.currency.constants.CommerceCurrencyConstants;
import com.liferay.commerce.currency.constants.CommerceCurrencyExchangeRateConstants;
import com.liferay.commerce.currency.constants.RoundingTypeConstants;
import com.liferay.commerce.currency.exception.CommerceCurrencyCodeException;
import com.liferay.commerce.currency.exception.CommerceCurrencyNameException;
import com.liferay.commerce.currency.exception.CommerceCurrencyRateException;
import com.liferay.commerce.currency.exception.DuplicateCommerceCurrencyException;
import com.liferay.commerce.currency.exception.NoSuchCurrencyException;
import com.liferay.commerce.currency.internal.model.listener.PortalInstanceLifecycleListenerImpl;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.base.CommerceCurrencyLocalServiceBaseImpl;
import com.liferay.commerce.currency.util.ExchangeRateProvider;
import com.liferay.commerce.currency.util.ExchangeRateProviderRegistry;
import com.liferay.commerce.currency.util.comparator.CommerceCurrencyPriorityComparator;
import com.liferay.commerce.product.constants.CPField;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.settings.SystemSettingsLocator;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.FieldSort;
import com.liferay.portal.search.sort.SortFieldBuilder;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;

import java.io.Serializable;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.currency.model.CommerceCurrency",
	service = AopService.class
)
public class CommerceCurrencyLocalServiceImpl
	extends CommerceCurrencyLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceCurrency addCommerceCurrency(
			String externalReferenceCode, long userId, String code,
			Map<Locale, String> nameMap, String symbol, BigDecimal rate,
			Map<Locale, String> formatPatternMap, int maxFractionDigits,
			int minFractionDigits, String roundingMode, boolean primary,
			double priority, boolean active)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		if (primary) {
			rate = BigDecimal.ONE;
		}

		_validate(0, user.getCompanyId(), code, nameMap, rate, primary);

		if (formatPatternMap.isEmpty()) {
			formatPatternMap.put(
				user.getLocale(),
				CommerceCurrencyConstants.DECIMAL_FORMAT_PATTERN);
		}

		if (Validator.isNull(roundingMode)) {
			RoundingTypeConfiguration roundingTypeConfiguration =
				_configurationProvider.getConfiguration(
					RoundingTypeConfiguration.class,
					new SystemSettingsLocator(
						RoundingTypeConstants.SERVICE_NAME));

			RoundingMode roundingModeEnum =
				roundingTypeConfiguration.roundingMode();

			roundingMode = roundingModeEnum.name();
		}

		long commerceCurrencyId = counterLocalService.increment();

		CommerceCurrency commerceCurrency = commerceCurrencyPersistence.create(
			commerceCurrencyId);

		commerceCurrency.setExternalReferenceCode(externalReferenceCode);
		commerceCurrency.setCompanyId(user.getCompanyId());
		commerceCurrency.setUserId(user.getUserId());
		commerceCurrency.setUserName(user.getFullName());
		commerceCurrency.setCode(code);
		commerceCurrency.setNameMap(nameMap);
		commerceCurrency.setSymbol(symbol);
		commerceCurrency.setRate(rate);
		commerceCurrency.setFormatPatternMap(formatPatternMap);
		commerceCurrency.setMaxFractionDigits(maxFractionDigits);
		commerceCurrency.setMinFractionDigits(minFractionDigits);
		commerceCurrency.setRoundingMode(roundingMode);
		commerceCurrency.setPrimary(primary);
		commerceCurrency.setPriority(priority);
		commerceCurrency.setActive(active);

		return commerceCurrencyPersistence.update(commerceCurrency);
	}

	@Override
	public void deleteCommerceCurrencies(long companyId) {
		commerceCurrencyPersistence.removeByCompanyId(companyId);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceCurrency deleteCommerceCurrency(
		CommerceCurrency commerceCurrency) {

		return commerceCurrencyPersistence.remove(commerceCurrency);
	}

	@Override
	public CommerceCurrency deleteCommerceCurrency(long commerceCurrencyId)
		throws PortalException {

		CommerceCurrency commerceCurrency =
			commerceCurrencyPersistence.findByPrimaryKey(commerceCurrencyId);

		return commerceCurrencyLocalService.deleteCommerceCurrency(
			commerceCurrency);
	}

	@Override
	public CommerceCurrency fetchCommerceCurrency(long companyId, String code) {
		return commerceCurrencyPersistence.fetchByC_C(companyId, code);
	}

	@Override
	public CommerceCurrency fetchPrimaryCommerceCurrency(long companyId) {
		return commerceCurrencyPersistence.fetchByC_P_A_First(
			companyId, true, true,
			CommerceCurrencyPriorityComparator.getInstance(false));
	}

	@Override
	public List<CommerceCurrency> getCommerceCurrencies(
		long companyId, boolean active) {

		return commerceCurrencyPersistence.findByC_A(companyId, active);
	}

	@Override
	public List<CommerceCurrency> getCommerceCurrencies(
		long companyId, boolean active, int start, int end,
		OrderByComparator<CommerceCurrency> orderByComparator) {

		return commerceCurrencyPersistence.findByC_A(
			companyId, active, start, end, orderByComparator);
	}

	@Override
	public List<CommerceCurrency> getCommerceCurrencies(
		long companyId, int start, int end,
		OrderByComparator<CommerceCurrency> orderByComparator) {

		return commerceCurrencyPersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceCurrenciesCount(long companyId) {
		return commerceCurrencyPersistence.countByCompanyId(companyId);
	}

	@Override
	public int getCommerceCurrenciesCount(long companyId, boolean active) {
		return commerceCurrencyPersistence.countByC_A(companyId, active);
	}

	@Override
	public CommerceCurrency getCommerceCurrency(long companyId, String code)
		throws NoSuchCurrencyException {

		return commerceCurrencyPersistence.findByC_C(companyId, code);
	}

	@Override
	public void importDefaultValues(
			boolean updateExchangeRate, ServiceContext serviceContext)
		throws Exception {

		Class<?> clazz = getClass();

		String currenciesPath =
			"com/liferay/commerce/currency/service/impl/dependencies" +
				"/currencies.json";

		String countriesJSON = StringUtil.read(
			clazz.getClassLoader(), currenciesPath, false);

		JSONArray jsonArray = _jsonFactory.createJSONArray(countriesJSON);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String code = jsonObject.getString("code");

			CommerceCurrency commerceCurrency =
				commerceCurrencyPersistence.fetchByC_C(
					serviceContext.getCompanyId(), code);

			if (commerceCurrency == null) {
				String externalReferenceCode = jsonObject.getString(
					"externalReferenceCode");
				boolean primary = jsonObject.getBoolean("primary");
				double priority = jsonObject.getDouble("priority");
				double rate = jsonObject.getDouble("rate");
				String symbol = jsonObject.getString("symbol");

				RoundingTypeConfiguration roundingTypeConfiguration =
					_configurationProvider.getConfiguration(
						RoundingTypeConfiguration.class,
						new SystemSettingsLocator(
							RoundingTypeConstants.SERVICE_NAME));

				Map<Locale, String> nameMap = HashMapBuilder.put(
					serviceContext.getLocale(), jsonObject.getString("name")
				).build();

				Map<Locale, String> formatPatternMap = HashMapBuilder.put(
					serviceContext.getLocale(),
					StringBundler.concat(
						symbol, StringPool.SPACE,
						CommerceCurrencyConstants.DECIMAL_FORMAT_PATTERN)
				).build();

				RoundingMode roundingMode =
					roundingTypeConfiguration.roundingMode();

				commerceCurrencyLocalService.addCommerceCurrency(
					externalReferenceCode, serviceContext.getUserId(), code,
					nameMap, symbol, BigDecimal.valueOf(rate), formatPatternMap,
					roundingTypeConfiguration.maximumFractionDigits(),
					roundingTypeConfiguration.minimumFractionDigits(),
					roundingMode.name(), primary, priority, true);
			}
		}

		if (updateExchangeRate) {
			for (String exchangeRateProviderKey :
					_exchangeRateProviderRegistry.
						getExchangeRateProviderKeys()) {

				_updateExchangeRates(
					serviceContext.getCompanyId(), exchangeRateProviderKey);

				break;
			}
		}
	}

	@Override
	public BaseModelSearchResult<CommerceCurrency> searchCommerceCurrencies(
			long companyId, String keywords,
			LinkedHashMap<String, Object> params, int start, int end, Sort sort)
		throws PortalException {

		SearchResponse searchResponse = _searcher.search(
			_getSearchRequest(companyId, keywords, params, start, end, sort));

		SearchHits searchHits = searchResponse.getSearchHits();

		return new BaseModelSearchResult<>(
			(List<CommerceCurrency>)TransformUtil.transform(
				searchHits.getSearchHits(),
				searchHit -> {
					Document document = searchHit.getDocument();

					long commerceCurrencyId = document.getLong(
						Field.ENTRY_CLASS_PK);

					CommerceCurrency commerceCurrency = fetchCommerceCurrency(
						commerceCurrencyId);

					if (commerceCurrency == null) {
						Indexer<CommerceCurrency> indexer =
							IndexerRegistryUtil.getIndexer(
								CommerceCurrency.class);

						indexer.delete(
							document.getLong(Field.COMPANY_ID),
							document.getString(Field.UID));
					}

					return commerceCurrency;
				}),
			searchResponse.getTotalHits());
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceCurrency setActive(long commerceCurrencyId, boolean active)
		throws PortalException {

		CommerceCurrency commerceCurrency =
			commerceCurrencyPersistence.findByPrimaryKey(commerceCurrencyId);

		commerceCurrency.setActive(active);

		return commerceCurrencyPersistence.update(commerceCurrency);
	}

	@Override
	public void setAopProxy(Object aopProxy) {
		super.setAopProxy(aopProxy);

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			PortalInstanceLifecycleListener.class,
			new PortalInstanceLifecycleListenerImpl(
				commerceCurrencyLocalService),
			null);
	}

	@Override
	public CommerceCurrency setPrimary(long commerceCurrencyId, boolean primary)
		throws PortalException {

		CommerceCurrency commerceCurrency =
			commerceCurrencyPersistence.findByPrimaryKey(commerceCurrencyId);

		_validate(
			commerceCurrencyId, commerceCurrency.getCompanyId(),
			commerceCurrency.getCode(), commerceCurrency.getNameMap(),
			commerceCurrency.getRate(), primary);

		commerceCurrency.setPrimary(primary);

		return commerceCurrencyPersistence.update(commerceCurrency);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceCurrency updateCommerceCurrency(
			String externalReferenceCode, long commerceCurrencyId,
			Map<Locale, String> nameMap, String symbol, BigDecimal rate,
			Map<Locale, String> formatPatternMap, int maxFractionDigits,
			int minFractionDigits, String roundingMode, boolean primary,
			double priority, boolean active, ServiceContext serviceContext)
		throws PortalException {

		CommerceCurrency commerceCurrency =
			commerceCurrencyPersistence.findByPrimaryKey(commerceCurrencyId);

		if (primary) {
			rate = BigDecimal.ONE;
		}

		_validate(
			commerceCurrency.getCommerceCurrencyId(),
			serviceContext.getCompanyId(), commerceCurrency.getCode(), nameMap,
			rate, primary);

		if (formatPatternMap.isEmpty()) {
			formatPatternMap.put(
				serviceContext.getLocale(),
				CommerceCurrencyConstants.DECIMAL_FORMAT_PATTERN);
		}

		if (Validator.isNull(roundingMode)) {
			RoundingTypeConfiguration roundingTypeConfiguration =
				_configurationProvider.getConfiguration(
					RoundingTypeConfiguration.class,
					new SystemSettingsLocator(
						RoundingTypeConstants.SERVICE_NAME));

			RoundingMode roundingModeEnum =
				roundingTypeConfiguration.roundingMode();

			roundingMode = roundingModeEnum.name();
		}

		commerceCurrency.setExternalReferenceCode(externalReferenceCode);
		commerceCurrency.setNameMap(nameMap);
		commerceCurrency.setSymbol(symbol);
		commerceCurrency.setRate(rate);
		commerceCurrency.setFormatPatternMap(formatPatternMap);
		commerceCurrency.setMaxFractionDigits(maxFractionDigits);
		commerceCurrency.setMinFractionDigits(minFractionDigits);
		commerceCurrency.setRoundingMode(roundingMode);
		commerceCurrency.setPrimary(primary);
		commerceCurrency.setPriority(priority);
		commerceCurrency.setActive(active);

		return commerceCurrencyPersistence.update(commerceCurrency);
	}

	@Override
	public CommerceCurrency updateCommerceCurrencyRate(
			long commerceCurrencyId, BigDecimal rate)
		throws PortalException {

		CommerceCurrency commerceCurrency =
			commerceCurrencyPersistence.findByPrimaryKey(commerceCurrencyId);

		commerceCurrency.setRate(rate);

		return commerceCurrencyPersistence.update(commerceCurrency);
	}

	@Override
	public void updateExchangeRate(
			long commerceCurrencyId, String exchangeRateProviderKey)
		throws PortalException {

		ExchangeRateProvider exchangeRateProvider =
			_exchangeRateProviderRegistry.getExchangeRateProvider(
				exchangeRateProviderKey);

		if (exchangeRateProvider == null) {
			return;
		}

		CommerceCurrency commerceCurrency =
			commerceCurrencyPersistence.findByPrimaryKey(commerceCurrencyId);

		CommerceCurrency primaryCommerceCurrency =
			commerceCurrencyPersistence.fetchByC_P_A_First(
				commerceCurrency.getCompanyId(), true, true,
				CommerceCurrencyPriorityComparator.getInstance(false));

		if (primaryCommerceCurrency == null) {
			return;
		}

		try {
			BigDecimal exchangeRate = exchangeRateProvider.getExchangeRate(
				primaryCommerceCurrency, commerceCurrency);

			if (BigDecimalUtil.gt(exchangeRate, BigDecimal.ZERO)) {
				commerceCurrency.setRate(exchangeRate);

				commerceCurrencyLocalService.updateCommerceCurrency(
					commerceCurrency);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	@Override
	public void updateExchangeRates() throws PortalException {
		_companyLocalService.forEachCompanyId(
			companyId -> {
				CommerceCurrencyConfiguration commerceCurrencyConfiguration =
					_configurationProvider.getConfiguration(
						CommerceCurrencyConfiguration.class,
						new CompanyServiceSettingsLocator(
							companyId,
							CommerceCurrencyExchangeRateConstants.
								SERVICE_NAME));

				if (commerceCurrencyConfiguration.enableAutoUpdate()) {
					String defaultExchangeRateProviderKey =
						commerceCurrencyConfiguration.
							defaultExchangeRateProviderKey();

					_updateExchangeRates(
						companyId, defaultExchangeRateProviderKey);
				}
			},
			ArrayUtil.toLongArray(commerceCurrencyFinder.getCompanyIds()));
	}

	@Deactivate
	@Override
	protected void deactivate() {
		super.deactivate();

		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	private SearchRequest _getSearchRequest(
		long companyId, String keywords, LinkedHashMap<String, Object> params,
		int start, int end, Sort sort) {

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder();

		searchRequestBuilder.entryClassNames(
			CommerceCurrency.class.getName()
		).emptySearchEnabled(
			true
		).highlightEnabled(
			false
		).withSearchContext(
			searchContext -> _populateSearchContext(
				searchContext, companyId, keywords, params, start, end, sort)
		);

		if (start != QueryUtil.ALL_POS) {
			searchRequestBuilder.from(start);
			searchRequestBuilder.size(end);
		}

		if (Validator.isNotNull(sort.getFieldName())) {
			SortOrder sortOrder = SortOrder.ASC;

			if (sort.isReverse()) {
				sortOrder = SortOrder.DESC;
			}

			FieldSort fieldSort = _sorts.field(
				_sortFieldBuilder.getSortField(
					CommerceCurrency.class, sort.getFieldName()),
				sortOrder);

			searchRequestBuilder.sorts(fieldSort);
		}

		return searchRequestBuilder.build();
	}

	private void _populateSearchContext(
		SearchContext searchContext, long companyId, String keywords,
		LinkedHashMap<String, Object> params, int start, int end, Sort sort) {

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				CPField.CODE, keywords
			).put(
				Field.NAME, keywords
			).put(
				"params",
				LinkedHashMapBuilder.<String, Object>put(
					"keywords", keywords
				).build()
			).build());

		Boolean active = (Boolean)params.get("active");

		if (active != null) {
			searchContext.setAttribute(CPField.ACTIVE, active);
		}

		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		searchContext.setStart(start);
	}

	private void _updateExchangeRates(
			long companyId, String exchangeRateProviderKey)
		throws PortalException {

		List<CommerceCurrency> commerceCurrencies =
			commerceCurrencyLocalService.getCommerceCurrencies(companyId, true);

		for (CommerceCurrency commerceCurrency : commerceCurrencies) {
			commerceCurrencyLocalService.updateExchangeRate(
				commerceCurrency.getCommerceCurrencyId(),
				exchangeRateProviderKey);
		}
	}

	private void _validate(
			long commerceCurrencyId, long companyId, String code,
			Map<Locale, String> nameMap, BigDecimal rate, boolean primary)
		throws PortalException {

		if (Validator.isNull(code)) {
			throw new CommerceCurrencyCodeException();
		}

		String name = nameMap.get(LocaleUtil.getSiteDefault());

		if (Validator.isNull(name)) {
			throw new CommerceCurrencyNameException();
		}

		if (BigDecimalUtil.lte(rate, BigDecimal.ZERO)) {
			throw new CommerceCurrencyRateException();
		}

		CommerceCurrency oldCommerceCurrency =
			commerceCurrencyPersistence.fetchByC_C(companyId, code);

		if ((oldCommerceCurrency != null) &&
			(commerceCurrencyId !=
				oldCommerceCurrency.getCommerceCurrencyId())) {

			throw new DuplicateCommerceCurrencyException();
		}

		if (primary) {
			List<CommerceCurrency> commerceCurrencies =
				commerceCurrencyPersistence.findByC_P(companyId, primary);

			for (CommerceCurrency commerceCurrency : commerceCurrencies) {
				if (commerceCurrency.getCommerceCurrencyId() !=
						commerceCurrencyId) {

					commerceCurrency.setPrimary(false);

					commerceCurrencyPersistence.update(commerceCurrency);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceCurrencyLocalServiceImpl.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private ExchangeRateProviderRegistry _exchangeRateProviderRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	private ServiceRegistration<?> _serviceRegistration;

	@Reference
	private SortFieldBuilder _sortFieldBuilder;

	@Reference
	private Sorts _sorts;

	@Reference
	private UserLocalService _userLocalService;

}