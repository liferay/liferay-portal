/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.internal.search;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountAccountRel;
import com.liferay.commerce.discount.model.CommerceDiscountCommerceAccountGroupRel;
import com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel;
import com.liferay.commerce.discount.service.CommerceDiscountAccountRelLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountCommerceAccountGroupRelLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountOrderTypeRelLocalService;
import com.liferay.commerce.discount.target.CommerceDiscountOrderTarget;
import com.liferay.commerce.discount.target.CommerceDiscountProductTarget;
import com.liferay.commerce.discount.target.CommerceDiscountTarget;
import com.liferay.commerce.discount.target.CommerceDiscountTargetRegistry;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.MissingFilter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.filter.FilterBuilders;
import com.liferay.portal.search.filter.TermsSetFilterBuilder;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(service = Indexer.class)
public class CommerceDiscountIndexer extends BaseIndexer<CommerceDiscount> {

	public static final String CLASS_NAME = CommerceDiscount.class.getName();

	public static final String FIELD_ACTIVE = "active";

	public static final String FIELD_COUPON_CODE = "couponCode";

	public static final String FIELD_GROUP_IDS = "groupIds";

	public static final String FIELD_TARGET_TYPE = "targetType";

	public static final String FIELD_USE_COUPON_CODE = "useCouponCode";

	public CommerceDiscountIndexer() {
		setDefaultSelectedFieldNames(
			Field.COMPANY_ID, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.MODIFIED_DATE, Field.NAME, Field.UID);
		setFilterSearch(true);
		setPermissionAware(true);
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {

		int[] statuses = GetterUtil.getIntegerValues(
			searchContext.getAttribute(Field.STATUS), null);

		if (ArrayUtil.isEmpty(statuses)) {
			int status = GetterUtil.getInteger(
				searchContext.getAttribute(Field.STATUS),
				WorkflowConstants.STATUS_APPROVED);

			statuses = new int[] {status};
		}

		if (!ArrayUtil.contains(statuses, WorkflowConstants.STATUS_ANY)) {
			TermsFilter statusesTermsFilter = new TermsFilter(Field.STATUS);

			statusesTermsFilter.addValues(ArrayUtil.toStringArray(statuses));

			contextBooleanFilter.add(
				statusesTermsFilter, BooleanClauseOccur.MUST);
		}
		else {
			contextBooleanFilter.addTerm(
				Field.STATUS, String.valueOf(WorkflowConstants.STATUS_IN_TRASH),
				BooleanClauseOccur.MUST_NOT);
		}

		boolean skipCommerceAccountGroupValidation = GetterUtil.getBoolean(
			searchContext.getAttribute("skipCommerceAccountGroupValidation"));

		if (!skipCommerceAccountGroupValidation) {
			long[] commerceAccountGroupIds = GetterUtil.getLongValues(
				searchContext.getAttribute("commerceAccountGroupIds"), null);

			if (commerceAccountGroupIds == null) {
				commerceAccountGroupIds = new long[0];
			}

			TermsSetFilterBuilder termsSetFilterBuilder =
				_filterBuilders.termsSetFilterBuilder();

			termsSetFilterBuilder.setFieldName("commerceAccountGroupIds");
			termsSetFilterBuilder.setMinimumShouldMatchField(
				"commerceAccountGroupIds_required_matches");

			List<String> values = new ArrayList<>(
				commerceAccountGroupIds.length);

			for (long commerceAccountGroupId : commerceAccountGroupIds) {
				values.add(String.valueOf(commerceAccountGroupId));
			}

			termsSetFilterBuilder.setValues(values);

			BooleanFilter fieldBooleanFilter = new BooleanFilter();

			fieldBooleanFilter.add(
				new TermFilter("commerceAccountGroupIds_required_matches", "0"),
				BooleanClauseOccur.SHOULD);
			fieldBooleanFilter.add(
				termsSetFilterBuilder.build(), BooleanClauseOccur.SHOULD);

			contextBooleanFilter.add(
				fieldBooleanFilter, BooleanClauseOccur.MUST);
		}

		long[] groupIds = GetterUtil.getLongValues(
			searchContext.getAttribute(FIELD_GROUP_IDS), null);

		if ((groupIds != null) && (groupIds.length > 0)) {
			BooleanFilter groupBooleanFilter = new BooleanFilter();

			for (long groupId : groupIds) {
				groupBooleanFilter.add(
					new TermFilter(FIELD_GROUP_IDS, String.valueOf(groupId)),
					BooleanClauseOccur.SHOULD);
			}

			groupBooleanFilter.add(
				new MissingFilter(FIELD_GROUP_IDS), BooleanClauseOccur.SHOULD);

			contextBooleanFilter.add(
				groupBooleanFilter, BooleanClauseOccur.MUST);
		}

		boolean active = GetterUtil.getBoolean(
			searchContext.getAttribute(FIELD_ACTIVE));

		if (active) {
			contextBooleanFilter.addRequiredTerm(
				FIELD_ACTIVE, String.valueOf(active));
		}

		String targetType = GetterUtil.getString(
			searchContext.getAttribute("targetType"), null);

		if (Validator.isNotNull(targetType)) {
			contextBooleanFilter.addRequiredTerm(FIELD_TARGET_TYPE, targetType);
		}

		String couponCode = GetterUtil.getString(
			searchContext.getAttribute(FIELD_COUPON_CODE), null);

		if (Validator.isNotNull(couponCode)) {
			BooleanFilter booleanFilter = new BooleanFilter();

			BooleanFilter booleanFilterCoupon = new BooleanFilter();

			booleanFilterCoupon.addRequiredTerm(FIELD_COUPON_CODE, couponCode);
			booleanFilterCoupon.addRequiredTerm(
				FIELD_USE_COUPON_CODE, Boolean.TRUE.toString());

			booleanFilter.add(booleanFilterCoupon);

			booleanFilter.addTerm(
				FIELD_USE_COUPON_CODE, Boolean.FALSE.toString());

			contextBooleanFilter.add(booleanFilter);
		}

		long cpDefinitionId = GetterUtil.getLong(
			searchContext.getAttribute("cpDefinitionId"));

		if (cpDefinitionId > 0) {
			CPDefinition cpDefinition =
				_cpDefinitionLocalService.getCPDefinition(cpDefinitionId);

			for (CommerceDiscountProductTarget commerceDiscountProductTarget :
					_commerceDiscountProductTargetServiceTrackerList) {

				commerceDiscountProductTarget.postProcessContextBooleanFilter(
					contextBooleanFilter, cpDefinition);
			}
		}

		long commerceOrderId = GetterUtil.getLong(
			searchContext.getAttribute("commerceOrderId"));

		if (commerceOrderId > 0) {
			CommerceOrder commerceOrder =
				_commerceOrderLocalService.getCommerceOrder(commerceOrderId);

			for (CommerceDiscountOrderTarget commerceDiscountOrderTarget :
					_commerceDiscountOrderTargetServiceTrackerList) {

				commerceDiscountOrderTarget.postProcessContextBooleanFilter(
					contextBooleanFilter, commerceOrder);
			}
		}
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			SearchContext searchContext)
		throws Exception {

		addSearchTerm(searchQuery, searchContext, Field.ENTRY_CLASS_PK, false);
		addSearchTerm(searchQuery, searchContext, Field.NAME, false);
		addSearchTerm(searchQuery, searchContext, Field.USER_NAME, false);

		LinkedHashMap<String, Object> params =
			(LinkedHashMap<String, Object>)searchContext.getAttribute("params");

		if (params != null) {
			String expandoAttributes = (String)params.get("expandoAttributes");

			if (Validator.isNotNull(expandoAttributes)) {
				addSearchExpando(searchQuery, searchContext, expandoAttributes);
			}
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_commerceDiscountOrderTargetServiceTrackerList =
			ServiceTrackerListFactory.open(
				bundleContext, CommerceDiscountOrderTarget.class);
		_commerceDiscountProductTargetServiceTrackerList =
			ServiceTrackerListFactory.open(
				bundleContext, CommerceDiscountProductTarget.class);
	}

	@Deactivate
	protected void deactivate() {
		_commerceDiscountOrderTargetServiceTrackerList.close();
		_commerceDiscountProductTargetServiceTrackerList.close();
	}

	@Override
	protected void doDelete(CommerceDiscount commerceDiscount)
		throws Exception {

		deleteDocument(
			commerceDiscount.getCompanyId(),
			commerceDiscount.getCommerceDiscountId());
	}

	@Override
	protected Document doGetDocument(CommerceDiscount commerceDiscount)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Indexing commerce discount " + commerceDiscount);
		}

		CommerceDiscountTarget commerceDiscountTarget =
			_commerceDiscountTargetRegistry.getCommerceDiscountTarget(
				commerceDiscount.getTarget());

		CommerceDiscountTarget.Type commerceDiscountTargetType =
			commerceDiscountTarget.getType();

		Document document = getBaseModelDocument(CLASS_NAME, commerceDiscount);

		document.addText(Field.TITLE, commerceDiscount.getTitle());
		document.addText(Field.USER_NAME, commerceDiscount.getUserName());
		document.addKeyword(FIELD_ACTIVE, commerceDiscount.isActive());
		document.addKeyword(
			FIELD_COUPON_CODE, commerceDiscount.getCouponCode());
		document.addText(
			FIELD_TARGET_TYPE, commerceDiscountTargetType.toString());
		document.addKeyword(
			FIELD_USE_COUPON_CODE, commerceDiscount.isUseCouponCode());

		document.addNumber(
			"commerceAccountId",
			TransformUtil.transformToLongArray(
				_commerceDiscountAccountRelLocalService.
					getCommerceDiscountAccountRels(
						commerceDiscount.getCommerceDiscountId(),
						QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
				CommerceDiscountAccountRel::getCommerceAccountId));

		long[] commerceAccountGroupIds = TransformUtil.transformToLongArray(
			_commerceDiscountCommerceAccountGroupRelLocalService.
				getCommerceDiscountCommerceAccountGroupRels(
					commerceDiscount.getCommerceDiscountId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null),
			CommerceDiscountCommerceAccountGroupRel::getCommerceAccountGroupId);

		document.addNumber("commerceAccountGroupIds", commerceAccountGroupIds);
		document.addNumber(
			"commerceAccountGroupIds_required_matches",
			commerceAccountGroupIds.length);

		List<Long> channelIdList = new ArrayList<>();
		List<Long> groupIdList = new ArrayList<>();

		List<CommerceChannelRel> commerceChannelRels =
			_commerceChannelRelLocalService.getCommerceChannelRels(
				commerceDiscount.getModelClassName(),
				commerceDiscount.getCommerceDiscountId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		for (CommerceChannelRel commerceChannelRel : commerceChannelRels) {
			CommerceChannel commerceChannel =
				_commerceChannelLocalService.fetchCommerceChannel(
					commerceChannelRel.getCommerceChannelId());

			if (commerceChannel == null) {
				continue;
			}

			channelIdList.add(commerceChannel.getCommerceChannelId());
			groupIdList.add(commerceChannel.getGroupId());
		}

		document.addNumber(
			"commerceChannelId", ArrayUtil.toLongArray(channelIdList));
		document.addNumber(
			"commerceOrderTypeId",
			TransformUtil.transformToLongArray(
				_commerceDiscountOrderTypeRelLocalService.
					getCommerceDiscountOrderTypeRels(
						commerceDiscount.getCommerceDiscountId()),
				CommerceDiscountOrderTypeRel::getCommerceOrderTypeId));
		document.addNumber(FIELD_GROUP_IDS, ArrayUtil.toLongArray(groupIdList));

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Commerce discount " + commerceDiscount +
					" indexed successfully");
		}

		if (commerceDiscountTarget instanceof CommerceDiscountProductTarget) {
			CommerceDiscountProductTarget commerceDiscountProductTarget =
				(CommerceDiscountProductTarget)commerceDiscountTarget;

			commerceDiscountProductTarget.contributeDocument(
				document, commerceDiscount);
		}

		if (commerceDiscountTarget instanceof CommerceDiscountOrderTarget) {
			CommerceDiscountOrderTarget commerceDiscountOrderTarget =
				(CommerceDiscountOrderTarget)commerceDiscountTarget;

			commerceDiscountOrderTarget.contributeDocument(
				document, commerceDiscount);
		}

		return document;
	}

	@Override
	protected Summary doGetSummary(
		Document document, Locale locale, String snippet,
		PortletRequest portletRequest, PortletResponse portletResponse) {

		Summary summary = createSummary(
			document, Field.ENTRY_CLASS_PK, Field.NAME);

		summary.setMaxContentLength(200);

		return summary;
	}

	@Override
	protected void doReindex(CommerceDiscount commerceDiscount)
		throws Exception {

		_indexWriterHelper.updateDocument(
			commerceDiscount.getCompanyId(), getDocument(commerceDiscount));
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		doReindex(_commerceDiscountLocalService.getCommerceDiscount(classPK));
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		_reindexCommerceDiscounts(companyId);
	}

	private void _reindexCommerceDiscounts(long companyId) throws Exception {
		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_commerceDiscountLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(CommerceDiscount commerceDiscount) -> {
				try {
					indexableActionableDynamicQuery.addDocuments(
						getDocument(commerceDiscount));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index commerce discount " +
								commerceDiscount,
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceDiscountIndexer.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	@Reference
	private CommerceDiscountAccountRelLocalService
		_commerceDiscountAccountRelLocalService;

	@Reference
	private CommerceDiscountCommerceAccountGroupRelLocalService
		_commerceDiscountCommerceAccountGroupRelLocalService;

	@Reference
	private CommerceDiscountLocalService _commerceDiscountLocalService;

	private ServiceTrackerList<CommerceDiscountOrderTarget>
		_commerceDiscountOrderTargetServiceTrackerList;

	@Reference
	private CommerceDiscountOrderTypeRelLocalService
		_commerceDiscountOrderTypeRelLocalService;

	private ServiceTrackerList<CommerceDiscountProductTarget>
		_commerceDiscountProductTargetServiceTrackerList;

	@Reference
	private CommerceDiscountTargetRegistry _commerceDiscountTargetRegistry;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private FilterBuilders _filterBuilders;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

}