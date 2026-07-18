/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.impl;

import com.liferay.commerce.constants.CommercePaymentEntryConstants;
import com.liferay.commerce.payment.entry.CommercePaymentEntryRefundType;
import com.liferay.commerce.payment.entry.CommercePaymentEntryRefundTypeRegistry;
import com.liferay.commerce.payment.exception.CommercePaymentEntryAmountException;
import com.liferay.commerce.payment.exception.CommercePaymentEntryClassNameIdException;
import com.liferay.commerce.payment.exception.CommercePaymentEntryClassPKException;
import com.liferay.commerce.payment.exception.CommercePaymentEntryPaymentIntegrationTypeException;
import com.liferay.commerce.payment.exception.CommercePaymentEntryPaymentStatusException;
import com.liferay.commerce.payment.exception.CommercePaymentEntryReasonKeyException;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.model.CommercePaymentEntryTable;
import com.liferay.commerce.payment.service.CommercePaymentEntryAuditLocalService;
import com.liferay.commerce.payment.service.base.CommercePaymentEntryLocalServiceBaseImpl;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
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
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;

import java.math.BigDecimal;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 * @author Crescenzo Rega
 */
@Component(
	property = "model.class.name=com.liferay.commerce.payment.model.CommercePaymentEntry",
	service = AopService.class
)
public class CommercePaymentEntryLocalServiceImpl
	extends CommercePaymentEntryLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommercePaymentEntry addCommercePaymentEntry(
			long userId, long classNameId, long classPK, long commerceChannelId,
			BigDecimal amount, String callbackURL, String cancelURL,
			String currencyCode, String languageId, String note, String payload,
			String paymentIntegrationKey, int paymentIntegrationType,
			String reasonKey, String transactionCode, int type,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		CommercePaymentEntryRefundType commercePaymentEntryRefundType =
			_commercePaymentEntryRefundTypeRegistry.
				getCommercePaymentEntryRefundType(
					user.getCompanyId(), reasonKey);

		_validate(
			commercePaymentEntryRefundType, classNameId, classPK, amount,
			paymentIntegrationType,
			CommercePaymentEntryConstants.STATUS_PENDING, reasonKey, type);

		CommercePaymentEntry commercePaymentEntry =
			commercePaymentEntryPersistence.create(
				counterLocalService.increment());

		commercePaymentEntry.setCompanyId(user.getCompanyId());
		commercePaymentEntry.setUserId(user.getUserId());
		commercePaymentEntry.setUserName(user.getFullName());

		commercePaymentEntry.setClassNameId(classNameId);
		commercePaymentEntry.setClassPK(classPK);
		commercePaymentEntry.setCommerceChannelId(commerceChannelId);
		commercePaymentEntry.setAmount(amount);
		commercePaymentEntry.setCallbackURL(callbackURL);
		commercePaymentEntry.setCancelURL(cancelURL);
		commercePaymentEntry.setCurrencyCode(currencyCode);
		commercePaymentEntry.setLanguageId(languageId);
		commercePaymentEntry.setNote(note);
		commercePaymentEntry.setPayload(payload);
		commercePaymentEntry.setPaymentIntegrationKey(paymentIntegrationKey);
		commercePaymentEntry.setPaymentIntegrationType(paymentIntegrationType);
		commercePaymentEntry.setPaymentStatus(
			CommercePaymentEntryConstants.STATUS_PENDING);
		commercePaymentEntry.setReasonKey(reasonKey);

		if (commercePaymentEntryRefundType != null) {
			commercePaymentEntry.setReasonNameMap(
				commercePaymentEntryRefundType.getNameMap());
		}

		commercePaymentEntry.setTransactionCode(transactionCode);
		commercePaymentEntry.setType(type);

		commercePaymentEntry = commercePaymentEntryPersistence.update(
			commercePaymentEntry);

		_resourceLocalService.addModelResources(
			commercePaymentEntry, serviceContext);

		return commercePaymentEntry;
	}

	@Override
	public CommercePaymentEntry addOrUpdateCommercePaymentEntry(
			String externalReferenceCode, long userId, long classNameId,
			long classPK, long commerceChannelId, BigDecimal amount,
			String callbackURL, String cancelURL, String currencyCode,
			String errorMessages, String languageId, String note,
			String payload, String paymentIntegrationKey,
			int paymentIntegrationType, int paymentStatus, String reasonKey,
			String redirectURL, String transactionCode, int type,
			ServiceContext serviceContext)
		throws PortalException {

		CommercePaymentEntry commercePaymentEntry = null;

		if (Validator.isNotNull(externalReferenceCode)) {
			commercePaymentEntry = commercePaymentEntryPersistence.fetchByERC_C(
				externalReferenceCode, serviceContext.getCompanyId());
		}

		if (commercePaymentEntry != null) {
			return commercePaymentEntryLocalService.updateCommercePaymentEntry(
				externalReferenceCode,
				commercePaymentEntry.getCommercePaymentEntryId(),
				commerceChannelId, amount, callbackURL, cancelURL, currencyCode,
				errorMessages, languageId, note, payload, paymentIntegrationKey,
				paymentIntegrationType, paymentStatus, reasonKey, redirectURL,
				transactionCode, type);
		}

		commercePaymentEntry =
			commercePaymentEntryLocalService.addCommercePaymentEntry(
				userId, classNameId, classPK, commerceChannelId, amount,
				callbackURL, cancelURL, currencyCode, languageId, note, payload,
				paymentIntegrationKey, paymentIntegrationType, reasonKey,
				transactionCode, type, serviceContext);

		commercePaymentEntry.setExternalReferenceCode(externalReferenceCode);

		return commercePaymentEntryPersistence.update(commercePaymentEntry);
	}

	@Override
	public void deleteCommercePaymentEntries(long companyId)
		throws PortalException {

		List<CommercePaymentEntry> commercePaymentEntries =
			commercePaymentEntryPersistence.findByCompanyId(companyId);

		for (CommercePaymentEntry commercePaymentEntry :
				commercePaymentEntries) {

			commercePaymentEntryLocalService.deleteCommercePaymentEntry(
				commercePaymentEntry.getCommercePaymentEntryId());
		}
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommercePaymentEntry deleteCommercePaymentEntry(
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		commercePaymentEntryPersistence.remove(commercePaymentEntry);

		_resourceLocalService.deleteResource(
			commercePaymentEntry, ResourceConstants.SCOPE_INDIVIDUAL);

		_commercePaymentEntryAuditLocalService.deleteCommercePaymentEntryAudits(
			commercePaymentEntry.getCommercePaymentEntryId());

		return commercePaymentEntry;
	}

	@Override
	public CommercePaymentEntry deleteCommercePaymentEntry(
			long commercePaymentEntryId)
		throws PortalException {

		CommercePaymentEntry commercePaymentEntry =
			commercePaymentEntryPersistence.findByPrimaryKey(
				commercePaymentEntryId);

		return commercePaymentEntryLocalService.deleteCommercePaymentEntry(
			commercePaymentEntry);
	}

	@Override
	public List<CommercePaymentEntry> getCommercePaymentEntries(
		long companyId, long classNameId, long classPK, int type, int start,
		int end, OrderByComparator<CommercePaymentEntry> orderByComparator) {

		return commercePaymentEntryPersistence.findByC_C_C_T(
			companyId, classNameId, classPK, type, start, end,
			orderByComparator);
	}

	@Override
	public List<CommercePaymentEntry> getCommercePaymentEntries(
		long companyId, long classNameId, long classPK, int start, int end,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

		return commercePaymentEntryPersistence.findByC_C_C(
			companyId, classNameId, classPK, start, end, orderByComparator);
	}

	@Override
	public int getCommercePaymentEntriesCount(
		long companyId, long classNameId, long classPK) {

		return commercePaymentEntryPersistence.countByC_C_C(
			companyId, classNameId, classPK);
	}

	@Override
	public int getCommercePaymentEntriesCount(
		long companyId, long classNameId, long classPK, int type) {

		return commercePaymentEntryPersistence.countByC_C_C_T(
			companyId, classNameId, classPK, type);
	}

	@Override
	public List<CommercePaymentEntry> getRefundCommercePaymentEntries(
		long companyId, long classNameId, long classPK, int start, int end) {

		return dslQuery(
			DSLQueryFactoryUtil.select(
				CommercePaymentEntryTable.INSTANCE
			).from(
				CommercePaymentEntryTable.INSTANCE
			).where(
				_getPredicate(companyId, classNameId, classPK)
			).limit(
				start, end
			));
	}

	@Override
	public int getRefundCommercePaymentEntriesCount(
		long companyId, long classNameId, long classPK) {

		return dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				CommercePaymentEntryTable.INSTANCE
			).where(
				_getPredicate(companyId, classNameId, classPK)
			));
	}

	@Override
	public BigDecimal getRefundedAmount(
		long companyId, long classNameId, long classPK) {

		Iterable<BigDecimal> iterable = dslQuery(
			DSLQueryFactoryUtil.select(
				DSLFunctionFactoryUtil.sum(
					CommercePaymentEntryTable.INSTANCE.amount
				).as(
					"SUM_VALUE"
				)
			).from(
				CommercePaymentEntryTable.INSTANCE
			).where(
				_getPredicate(
					companyId, classNameId, classPK
				).and(
					CommercePaymentEntryTable.INSTANCE.paymentStatus.eq(
						CommercePaymentEntryConstants.STATUS_REFUNDED)
				)
			));

		Iterator<BigDecimal> iterator = iterable.iterator();

		BigDecimal refundedAmount = iterator.next();

		if (refundedAmount == null) {
			return BigDecimal.ZERO;
		}

		return refundedAmount;
	}

	@Override
	public BaseModelSearchResult<CommercePaymentEntry>
		searchCommercePaymentEntries(
			long companyId, String keywords,
			LinkedHashMap<String, Object> params, int start, int end,
			Sort sort) {

		SearchResponse searchResponse = _searcher.search(
			_getSearchRequest(companyId, keywords, params, start, end, sort));

		SearchHits searchHits = searchResponse.getSearchHits();

		List<CommercePaymentEntry> commercePaymentEntries =
			TransformUtil.transform(
				searchHits.getSearchHits(),
				searchHit -> {
					Document document = searchHit.getDocument();

					long commercePaymentEntryId = document.getLong(
						Field.ENTRY_CLASS_PK);

					CommercePaymentEntry commercePaymentEntry =
						fetchCommercePaymentEntry(commercePaymentEntryId);

					if (commercePaymentEntry == null) {
						Indexer<CommercePaymentEntry>
							commercePaymentEntryIndexer =
								IndexerRegistryUtil.getIndexer(
									CommercePaymentEntry.class);

						commercePaymentEntryIndexer.delete(
							document.getLong(Field.COMPANY_ID),
							document.getString(Field.UID));
					}

					return commercePaymentEntry;
				});

		return new BaseModelSearchResult<>(
			commercePaymentEntries, searchResponse.getTotalHits());
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommercePaymentEntry updateCommercePaymentEntry(
			String externalReferenceCode, long commercePaymentEntryId,
			long commerceChannelId, BigDecimal amount, String callbackURL,
			String cancelURL, String currencyCode, String errorMessages,
			String languageId, String note, String payload,
			String paymentIntegrationKey, int paymentIntegrationType,
			int paymentStatus, String reasonKey, String redirectURL,
			String transactionCode, int type)
		throws PortalException {

		CommercePaymentEntry commercePaymentEntry =
			commercePaymentEntryPersistence.findByPrimaryKey(
				commercePaymentEntryId);

		CommercePaymentEntryRefundType commercePaymentEntryRefundType =
			_commercePaymentEntryRefundTypeRegistry.
				getCommercePaymentEntryRefundType(
					commercePaymentEntry.getCompanyId(), reasonKey);

		_validate(
			commercePaymentEntryRefundType,
			commercePaymentEntry.getClassNameId(),
			commercePaymentEntry.getClassPK(), amount, paymentIntegrationType,
			commercePaymentEntry.getPaymentStatus(), reasonKey, type);

		commercePaymentEntry.setExternalReferenceCode(externalReferenceCode);
		commercePaymentEntry.setCommerceChannelId(commerceChannelId);
		commercePaymentEntry.setAmount(amount);
		commercePaymentEntry.setCallbackURL(callbackURL);
		commercePaymentEntry.setCancelURL(cancelURL);
		commercePaymentEntry.setCurrencyCode(currencyCode);
		commercePaymentEntry.setErrorMessages(errorMessages);
		commercePaymentEntry.setLanguageId(languageId);
		commercePaymentEntry.setNote(note);
		commercePaymentEntry.setPayload(payload);
		commercePaymentEntry.setPaymentIntegrationKey(paymentIntegrationKey);
		commercePaymentEntry.setPaymentIntegrationType(paymentIntegrationType);
		commercePaymentEntry.setPaymentStatus(paymentStatus);

		if (Validator.isNull(reasonKey)) {
			commercePaymentEntry.setReasonKey(null);
			commercePaymentEntry.setReasonNameMap(null);
		}
		else if (!reasonKey.equals(commercePaymentEntry.getReasonKey())) {
			commercePaymentEntry.setReasonKey(reasonKey);
			commercePaymentEntry.setReasonNameMap(
				commercePaymentEntryRefundType.getNameMap());
		}

		commercePaymentEntry.setRedirectURL(redirectURL);

		if (Validator.isNotNull(transactionCode)) {
			commercePaymentEntry.setTransactionCode(transactionCode);
		}

		commercePaymentEntry.setType(type);

		return commercePaymentEntryPersistence.update(commercePaymentEntry);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommercePaymentEntry updateExternalReferenceCode(
			long commercePaymentEntryId, String externalReferenceCode)
		throws PortalException {

		CommercePaymentEntry commercePaymentEntry =
			commercePaymentEntryPersistence.findByPrimaryKey(
				commercePaymentEntryId);

		commercePaymentEntry.setExternalReferenceCode(externalReferenceCode);

		return commercePaymentEntryPersistence.update(commercePaymentEntry);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommercePaymentEntry updateNote(
			long commercePaymentEntryId, String note)
		throws PortalException {

		CommercePaymentEntry commercePaymentEntry =
			commercePaymentEntryPersistence.findByPrimaryKey(
				commercePaymentEntryId);

		commercePaymentEntry.setNote(note);

		return commercePaymentEntryPersistence.update(commercePaymentEntry);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommercePaymentEntry updateReasonKey(
			long commercePaymentEntryId, String reasonKey)
		throws PortalException {

		CommercePaymentEntry commercePaymentEntry =
			commercePaymentEntryPersistence.findByPrimaryKey(
				commercePaymentEntryId);

		CommercePaymentEntryRefundType commercePaymentEntryRefundType =
			_commercePaymentEntryRefundTypeRegistry.
				getCommercePaymentEntryRefundType(
					commercePaymentEntry.getCompanyId(), reasonKey);

		_validate(
			commercePaymentEntryRefundType,
			commercePaymentEntry.getClassNameId(),
			commercePaymentEntry.getClassPK(), commercePaymentEntry.getAmount(),
			commercePaymentEntry.getPaymentIntegrationType(),
			commercePaymentEntry.getPaymentStatus(), reasonKey,
			commercePaymentEntry.getType());

		if (Validator.isNull(reasonKey)) {
			commercePaymentEntry.setReasonKey(null);
			commercePaymentEntry.setReasonNameMap(null);
		}
		else if (!reasonKey.equals(commercePaymentEntry.getReasonKey())) {
			commercePaymentEntry.setReasonKey(reasonKey);
			commercePaymentEntry.setReasonNameMap(
				commercePaymentEntryRefundType.getNameMap());
		}

		return commercePaymentEntryPersistence.update(commercePaymentEntry);
	}

	private Predicate _getPredicate(
		long companyId, long classNameId, long classPK) {

		return CommercePaymentEntryTable.INSTANCE.classPK.in(
			DSLQueryFactoryUtil.select(
				CommercePaymentEntryTable.INSTANCE.commercePaymentEntryId
			).from(
				CommercePaymentEntryTable.INSTANCE
			).where(
				CommercePaymentEntryTable.INSTANCE.companyId.eq(
					companyId
				).and(
					CommercePaymentEntryTable.INSTANCE.classNameId.eq(
						classNameId)
				).and(
					CommercePaymentEntryTable.INSTANCE.classPK.eq(classPK)
				).and(
					CommercePaymentEntryTable.INSTANCE.paymentStatus.eq(
						CommercePaymentEntryConstants.STATUS_COMPLETED)
				).and(
					CommercePaymentEntryTable.INSTANCE.type.eq(
						CommercePaymentEntryConstants.TYPE_PAYMENT)
				)
			)
		).and(
			CommercePaymentEntryTable.INSTANCE.type.eq(
				CommercePaymentEntryConstants.TYPE_REFUND)
		);
	}

	private SearchRequest _getSearchRequest(
		long companyId, String keywords, LinkedHashMap<String, Object> params,
		int start, int end, Sort sort) {

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder();

		searchRequestBuilder.entryClassNames(
			CommercePaymentEntry.class.getName()
		).emptySearchEnabled(
			true
		).highlightEnabled(
			false
		).withSearchContext(
			searchContext -> _populateSearchContext(
				searchContext, companyId, keywords, params, sort)
		);

		if (start != QueryUtil.ALL_POS) {
			searchRequestBuilder.from(start);
			searchRequestBuilder.size(end);
		}

		return searchRequestBuilder.build();
	}

	private void _populateSearchContext(
		SearchContext searchContext, long companyId, String keywords,
		LinkedHashMap<String, Object> params, Sort sort) {

		searchContext.setCompanyId(companyId);

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		if (MapUtil.isEmpty(params)) {
			return;
		}

		long[] classNameIds = (long[])params.get("classNameIds");

		if (ArrayUtil.isNotEmpty(classNameIds)) {
			searchContext.setAttribute("classNameIds", classNameIds);
		}

		long[] classPKs = (long[])params.get("classPKs");

		if (ArrayUtil.isNotEmpty(classPKs)) {
			searchContext.setAttribute("classPKs", classPKs);
		}

		String[] currencyCodes = (String[])params.get("currencyCodes");

		if (ArrayUtil.isNotEmpty(currencyCodes)) {
			searchContext.setAttribute("currencyCodes", currencyCodes);
		}

		String[] paymentMethodNames = (String[])params.get(
			"paymentMethodNames");

		if (ArrayUtil.isNotEmpty(paymentMethodNames)) {
			searchContext.setAttribute(
				"paymentMethodNames", paymentMethodNames);
		}

		long permissionUserId = GetterUtil.getLong(
			params.get("permissionUserId"));

		if (permissionUserId > 0) {
			searchContext.setUserId(permissionUserId);
		}

		int[] statuses = (int[])params.get("paymentStatuses");

		if (ArrayUtil.isNotEmpty(statuses)) {
			searchContext.setAttribute("paymentStatuses", statuses);
		}

		boolean excludeStatuses = GetterUtil.getBoolean(
			params.get("excludePaymentStatuses"));

		searchContext.setAttribute("excludePaymentStatuses", excludeStatuses);

		Integer type = (Integer)params.get("type");

		if (type != null) {
			searchContext.setAttribute("type", type);
		}

		if (sort == null) {
			sort = SortFactoryUtil.getSort(
				CommercePaymentEntry.class, Sort.LONG_TYPE, Field.CREATE_DATE,
				"DESC");
		}
		else {
			sort.setFieldName(Field.CREATE_DATE);
			sort.setType(Sort.LONG_TYPE);
		}

		searchContext.setSorts(sort);
	}

	private void _validate(
			CommercePaymentEntryRefundType commercePaymentEntryRefundType,
			long classNameId, long classPK, BigDecimal amount,
			int paymentIntegrationType, int paymentStatus, String reasonKey,
			int type)
		throws PortalException {

		if (classNameId <= 0) {
			throw new CommercePaymentEntryClassNameIdException();
		}

		if (type == CommercePaymentEntryConstants.TYPE_REFUND) {
			if (classNameId != _classNameLocalService.getClassNameId(
					CommercePaymentEntry.class.getName())) {

				throw new CommercePaymentEntryClassNameIdException();
			}

			CommercePaymentEntry commercePaymentEntry =
				commercePaymentEntryPersistence.fetchByPrimaryKey(classPK);

			if ((commercePaymentEntry == null) ||
				(commercePaymentEntry.getPaymentStatus() !=
					CommercePaymentEntryConstants.STATUS_COMPLETED)) {

				throw new CommercePaymentEntryClassPKException();
			}
		}

		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new CommercePaymentEntryAmountException();
		}

		if (paymentIntegrationType < 0) {
			throw new CommercePaymentEntryPaymentIntegrationTypeException();
		}

		if (paymentStatus == CommercePaymentEntryConstants.STATUS_REFUNDED) {
			throw new CommercePaymentEntryPaymentStatusException();
		}

		if (Validator.isNotNull(reasonKey) &&
			((commercePaymentEntryRefundType == null) ||
			 (type != CommercePaymentEntryConstants.TYPE_REFUND))) {

			throw new CommercePaymentEntryReasonKeyException();
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommercePaymentEntryAuditLocalService
		_commercePaymentEntryAuditLocalService;

	@Reference
	private CommercePaymentEntryRefundTypeRegistry
		_commercePaymentEntryRefundTypeRegistry;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private UserLocalService _userLocalService;

}