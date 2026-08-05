/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.inventory.exception.CommerceInventoryWarehouseItemUnitOfMeasureKeyException;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.exception.CPDefinitionOptionValueRelCPInstanceException;
import com.liferay.commerce.product.exception.CPDefinitionOptionValueRelKeyException;
import com.liferay.commerce.product.exception.CPDefinitionOptionValueRelPriceException;
import com.liferay.commerce.product.exception.CPDefinitionOptionValueRelQuantityException;
import com.liferay.commerce.product.exception.DuplicateCPDefinitionOptionValueRelKeyException;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionOptionValueRelException;
import com.liferay.commerce.product.exception.NoSuchCPInstanceUnitOfMeasureException;
import com.liferay.commerce.product.helper.CPCollectionProviderHelper;
import com.liferay.commerce.product.internal.util.CPDefinitionLocalServiceCircularDependencyUtil;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRelTable;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceOptionValueRel;
import com.liferay.commerce.product.model.CPInstanceOptionValueRelTable;
import com.liferay.commerce.product.model.CPInstanceTable;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CPOptionValue;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceOptionValueRelLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.base.CPDefinitionOptionValueRelLocalServiceBaseImpl;
import com.liferay.commerce.product.service.persistence.CPDefinitionOptionRelPersistence;
import com.liferay.commerce.product.service.persistence.CPInstancePersistence;
import com.liferay.commerce.product.service.persistence.CPOptionPersistence;
import com.liferay.commerce.product.service.persistence.CPOptionValuePersistence;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.info.pagination.Pagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.taglib.util.CustomAttributesUtil;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Igor Beslic
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CPDefinitionOptionValueRel",
	service = AopService.class
)
public class CPDefinitionOptionValueRelLocalServiceImpl
	extends CPDefinitionOptionValueRelLocalServiceBaseImpl {

	@Override
	public CPDefinitionOptionValueRel addCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId, CPOptionValue cpOptionValue,
			ServiceContext serviceContext)
		throws PortalException {

		return cpDefinitionOptionValueRelLocalService.
			addCPDefinitionOptionValueRel(
				cpDefinitionOptionRelId, cpOptionValue.getKey(),
				cpOptionValue.getNameMap(), cpOptionValue.getPriority(),
				serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPDefinitionOptionValueRel addCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId, long cpInstanceId, String key,
			Map<Locale, String> nameMap, boolean preselected,
			BigDecimal deltaPrice, double priority, BigDecimal quantity,
			String unitOfMeasureKey, ServiceContext serviceContext)
		throws PortalException {

		// Commerce product definition option value rel

		User user = _userLocalService.getUser(serviceContext.getUserId());

		key = _friendlyURLNormalizer.normalize(key);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionRelId);

		_validate(
			0, cpDefinitionOptionRel, cpInstanceId, key, unitOfMeasureKey);

		long cpDefinitionOptionValueRelId = counterLocalService.increment();

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelPersistence.create(
				cpDefinitionOptionValueRelId);

		if (CPDefinitionLocalServiceCircularDependencyUtil.isVersionable(
				cpDefinitionOptionRel.getCPDefinitionId(),
				serviceContext.getRequest())) {

			CPDefinition newCPDefinition =
				CPDefinitionLocalServiceCircularDependencyUtil.copyCPDefinition(
					cpDefinitionOptionRel.getCPDefinitionId());

			cpDefinitionOptionRel = _cpDefinitionOptionRelPersistence.findByC_C(
				newCPDefinition.getCPDefinitionId(),
				cpDefinitionOptionRel.getCPOptionId());

			cpDefinitionOptionRelId =
				cpDefinitionOptionRel.getCPDefinitionOptionRelId();
		}

		cpDefinitionOptionValueRel =
			_updateCPDefinitionOptionValueRelCPInstance(
				cpDefinitionOptionValueRel, cpInstanceId);

		cpDefinitionOptionValueRel.setGroupId(
			cpDefinitionOptionRel.getGroupId());
		cpDefinitionOptionValueRel.setCompanyId(user.getCompanyId());
		cpDefinitionOptionValueRel.setUserId(user.getUserId());
		cpDefinitionOptionValueRel.setUserName(user.getFullName());
		cpDefinitionOptionValueRel.setCPDefinitionOptionRelId(
			cpDefinitionOptionRelId);
		cpDefinitionOptionValueRel.setKey(key);
		cpDefinitionOptionValueRel.setNameMap(nameMap);

		if (cpDefinitionOptionRel.isPriceTypeStatic()) {
			cpDefinitionOptionValueRel.setPrice(
				BigDecimalUtil.get(deltaPrice, BigDecimal.ZERO));
		}

		cpDefinitionOptionValueRel.setPriority(priority);
		cpDefinitionOptionValueRel.setQuantity(
			BigDecimalUtil.get(quantity, BigDecimal.ONE));
		cpDefinitionOptionValueRel.setUnitOfMeasureKey(unitOfMeasureKey);
		cpDefinitionOptionValueRel.setExpandoBridgeAttributes(serviceContext);

		_validateLinkedCPDefinitionOptionValueRel(cpDefinitionOptionValueRel);

		CPInstance cpInstance = _cpInstanceLocalService.fetchCProductInstance(
			cpDefinitionOptionValueRel.getCProductId(),
			cpDefinitionOptionValueRel.getCPInstanceUuid());

		if (cpInstance != null) {
			_validateLinkableCPInstance(cpInstance);
		}

		_validateLinkedCPDefinitionOptionValueRel(cpDefinitionOptionValueRel);
		_validatePriceableCPDefinitionOptionValue(
			cpDefinitionOptionValueRel, cpDefinitionOptionRel.getPriceType());

		cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelPersistence.update(
				cpDefinitionOptionValueRel);

		cpDefinitionOptionValueRel =
			_updateCPDefinitionOptionValueRelPreselected(
				cpDefinitionOptionValueRel, preselected);

		// Commerce product definition

		_reindexCPDefinition(cpDefinitionOptionRel);

		return cpDefinitionOptionValueRel;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPDefinitionOptionValueRel addCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId, String key,
			Map<Locale, String> nameMap, double priority,
			ServiceContext serviceContext)
		throws PortalException {

		// Commerce product definition option value rel

		User user = _userLocalService.getUser(serviceContext.getUserId());

		key = _friendlyURLNormalizer.normalize(key);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionRelId);

		_validate(0, cpDefinitionOptionRel, 0, key, StringPool.BLANK);

		long cpDefinitionOptionValueRelId = counterLocalService.increment();

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelPersistence.create(
				cpDefinitionOptionValueRelId);

		if (CPDefinitionLocalServiceCircularDependencyUtil.isVersionable(
				cpDefinitionOptionRel.getCPDefinitionId(),
				serviceContext.getRequest())) {

			CPDefinition newCPDefinition =
				CPDefinitionLocalServiceCircularDependencyUtil.copyCPDefinition(
					cpDefinitionOptionRel.getCPDefinitionId());

			cpDefinitionOptionRel = _cpDefinitionOptionRelPersistence.findByC_C(
				newCPDefinition.getCPDefinitionId(),
				cpDefinitionOptionRel.getCPOptionId());

			cpDefinitionOptionRelId =
				cpDefinitionOptionRel.getCPDefinitionOptionRelId();
		}

		cpDefinitionOptionValueRel.setGroupId(
			cpDefinitionOptionRel.getGroupId());
		cpDefinitionOptionValueRel.setCompanyId(user.getCompanyId());
		cpDefinitionOptionValueRel.setUserId(user.getUserId());
		cpDefinitionOptionValueRel.setUserName(user.getFullName());
		cpDefinitionOptionValueRel.setCPDefinitionOptionRelId(
			cpDefinitionOptionRelId);
		cpDefinitionOptionValueRel.setKey(key);
		cpDefinitionOptionValueRel.setNameMap(nameMap);

		if (cpDefinitionOptionRel.isPriceTypeStatic()) {
			cpDefinitionOptionValueRel.setPrice(BigDecimal.ZERO);
		}

		cpDefinitionOptionValueRel.setPriority(priority);
		cpDefinitionOptionValueRel.setQuantity(BigDecimal.ZERO);
		cpDefinitionOptionValueRel.setExpandoBridgeAttributes(serviceContext);

		_validateLinkedCPDefinitionOptionValueRel(cpDefinitionOptionValueRel);
		_validatePriceableCPDefinitionOptionValue(
			cpDefinitionOptionValueRel, cpDefinitionOptionRel.getPriceType());

		cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelPersistence.update(
				cpDefinitionOptionValueRel);

		// Commerce product definition

		_reindexCPDefinition(cpDefinitionOptionRel);

		return cpDefinitionOptionValueRel;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPDefinitionOptionValueRel deleteCPDefinitionOptionValueRel(
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionValueRel.getCPDefinitionOptionRel();

		if (CPDefinitionLocalServiceCircularDependencyUtil.isVersionable(
				cpDefinitionOptionRel.getCPDefinitionId())) {

			CPDefinition newCPDefinition =
				CPDefinitionLocalServiceCircularDependencyUtil.copyCPDefinition(
					cpDefinitionOptionRel.getCPDefinitionId());

			cpDefinitionOptionRel = _cpDefinitionOptionRelPersistence.findByC_C(
				newCPDefinition.getCPDefinitionId(),
				cpDefinitionOptionRel.getCPOptionId());

			cpDefinitionOptionValueRel =
				cpDefinitionOptionValueRelPersistence.findByC_K(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
					cpDefinitionOptionValueRel.getKey());
		}

		// Commerce product definition option value rel

		cpDefinitionOptionValueRelPersistence.remove(
			cpDefinitionOptionValueRel);

		// Expando

		_expandoRowLocalService.deleteRows(
			cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId());

		_cpInstanceLocalService.inactivateCPDefinitionOptionValueRelCPInstances(
			PrincipalThreadLocal.getUserId(),
			cpDefinitionOptionRel.getCPDefinitionId(),
			cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId());

		// Commerce product definition

		_reindexCPDefinition(cpDefinitionOptionRel);

		return cpDefinitionOptionValueRel;
	}

	@Override
	public CPDefinitionOptionValueRel deleteCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId)
		throws PortalException {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelPersistence.findByPrimaryKey(
				cpDefinitionOptionValueRelId);

		return cpDefinitionOptionValueRelLocalService.
			deleteCPDefinitionOptionValueRel(cpDefinitionOptionValueRel);
	}

	@Override
	public void deleteCPDefinitionOptionValueRels(long cpDefinitionOptionRelId)
		throws PortalException {

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			cpDefinitionOptionValueRelLocalService.
				getCPDefinitionOptionValueRels(
					cpDefinitionOptionRelId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

		for (CPDefinitionOptionValueRel cpDefinitionOptionValueRel :
				cpDefinitionOptionValueRels) {

			cpDefinitionOptionValueRelLocalService.
				deleteCPDefinitionOptionValueRel(cpDefinitionOptionValueRel);
		}
	}

	@Override
	public CPDefinitionOptionValueRel fetchCPDefinitionOptionValueRel(
		long cpDefinitionOptionRelId, long cpInstanceId) {

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			cpDefinitionOptionValueRelPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					CPDefinitionOptionValueRelTable.INSTANCE
				).from(
					CPDefinitionOptionValueRelTable.INSTANCE
				).innerJoinON(
					CPInstanceOptionValueRelTable.INSTANCE,
					CPInstanceOptionValueRelTable.INSTANCE.
						CPDefinitionOptionValueRelId.eq(
							CPDefinitionOptionValueRelTable.INSTANCE.
								CPDefinitionOptionValueRelId)
				).innerJoinON(
					CPInstanceTable.INSTANCE,
					CPInstanceTable.INSTANCE.CPInstanceId.eq(
						CPInstanceOptionValueRelTable.INSTANCE.CPInstanceId)
				).where(
					CPDefinitionOptionValueRelTable.INSTANCE.
						CPDefinitionOptionRelId.eq(
							cpDefinitionOptionRelId
						).and(
							CPInstanceOptionValueRelTable.INSTANCE.CPInstanceId.
								eq(cpInstanceId)
						).and(
							CPInstanceTable.INSTANCE.status.eq(
								WorkflowConstants.STATUS_APPROVED)
						)
				).limit(
					0, 1
				));

		if (cpDefinitionOptionValueRels.isEmpty()) {
			return null;
		}

		return cpDefinitionOptionValueRels.get(0);
	}

	@Override
	public CPDefinitionOptionValueRel fetchCPDefinitionOptionValueRel(
		long cpDefinitionOptionRelId, String key) {

		return cpDefinitionOptionValueRelPersistence.fetchByC_K(
			cpDefinitionOptionRelId, key);
	}

	@Override
	public CPDefinitionOptionValueRel
		fetchPreselectedCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId) {

		List<CPDefinitionOptionValueRel>
			preselectedCPDefinitionOptionValueRels =
				cpDefinitionOptionValueRelPersistence.findByCDORI_P(
					cpDefinitionOptionRelId, true);

		if (preselectedCPDefinitionOptionValueRels.isEmpty()) {
			return null;
		}

		return preselectedCPDefinitionOptionValueRels.get(0);
	}

	@Override
	public List<CPDefinitionOptionValueRel> filterByCPInstanceOptionValueRels(
		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels,
		List<CPInstanceOptionValueRel> cpInstanceOptionValueRels) {

		return TransformUtil.transform(
			cpDefinitionOptionValueRels,
			cpDefinitionOptionValueRel -> {
				for (CPInstanceOptionValueRel cpInstanceOptionValueRel :
						cpInstanceOptionValueRels) {

					long cpDefinitionOptionValueRelId1 =
						cpDefinitionOptionValueRel.
							getCPDefinitionOptionValueRelId();
					long cpDefinitionOptionValueRelId2 =
						cpInstanceOptionValueRel.
							getCPDefinitionOptionValueRelId();

					if (cpDefinitionOptionValueRelId1 ==
							cpDefinitionOptionValueRelId2) {

						return cpDefinitionOptionValueRel;
					}
				}

				return null;
			});
	}

	@Override
	public List<CPDefinitionOptionValueRel>
		getApprovedCPInstanceCPDefinitionOptionValueRels(
			long cpDefinitionOptionRelId) {

		return cpDefinitionOptionValueRelPersistence.dslQuery(
			DSLQueryFactoryUtil.selectDistinct(
				CPDefinitionOptionValueRelTable.INSTANCE
			).from(
				CPDefinitionOptionValueRelTable.INSTANCE
			).innerJoinON(
				CPInstanceOptionValueRelTable.INSTANCE,
				CPInstanceOptionValueRelTable.INSTANCE.
					CPDefinitionOptionValueRelId.eq(
						CPDefinitionOptionValueRelTable.INSTANCE.
							CPDefinitionOptionValueRelId)
			).innerJoinON(
				CPInstanceTable.INSTANCE,
				CPInstanceTable.INSTANCE.CPInstanceId.eq(
					CPInstanceOptionValueRelTable.INSTANCE.CPInstanceId)
			).where(
				CPDefinitionOptionValueRelTable.INSTANCE.
					CPDefinitionOptionRelId.eq(
						cpDefinitionOptionRelId
					).and(
						CPInstanceTable.INSTANCE.status.eq(
							WorkflowConstants.STATUS_APPROVED)
					).and(
						Predicate.or(
							CPInstanceTable.INSTANCE.expirationDate.isNull(),
							CPInstanceTable.INSTANCE.expirationDate.gt(
								new Date()))
					)
			).orderBy(
				CPDefinitionOptionValueRelTable.INSTANCE.priority.ascending(),
				CPDefinitionOptionValueRelTable.INSTANCE.createDate.ascending()
			));
	}

	@Override
	public List<CPDefinitionOptionValueRel> getCPDefinitionOptionValueRels(
		long cpDefinitionOptionRelId) {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.fetchByPrimaryKey(
				cpDefinitionOptionRelId);

		if ((cpDefinitionOptionRel != null) &&
			cpDefinitionOptionRel.isDefinedExternally()) {

			return _cpCollectionProviderHelper.getCPDefinitionOptionValueRels(
				cpDefinitionOptionRel, null, null);
		}

		return cpDefinitionOptionValueRelPersistence.
			findByCPDefinitionOptionRelId(cpDefinitionOptionRelId);
	}

	@Override
	public List<CPDefinitionOptionValueRel> getCPDefinitionOptionValueRels(
		long cpDefinitionOptionRelId, int start, int end) {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.fetchByPrimaryKey(
				cpDefinitionOptionRelId);

		if ((cpDefinitionOptionRel != null) &&
			cpDefinitionOptionRel.isDefinedExternally()) {

			return _cpCollectionProviderHelper.getCPDefinitionOptionValueRels(
				cpDefinitionOptionRel, null, Pagination.of(end, start));
		}

		return cpDefinitionOptionValueRelPersistence.
			findByCPDefinitionOptionRelId(cpDefinitionOptionRelId, start, end);
	}

	@Override
	public List<CPDefinitionOptionValueRel> getCPDefinitionOptionValueRels(
		long cpDefinitionOptionRelId, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.fetchByPrimaryKey(
				cpDefinitionOptionRelId);

		if ((cpDefinitionOptionRel != null) &&
			cpDefinitionOptionRel.isDefinedExternally()) {

			return _cpCollectionProviderHelper.getCPDefinitionOptionValueRels(
				cpDefinitionOptionRel, null, Pagination.of(end, start));
		}

		return cpDefinitionOptionValueRelPersistence.
			findByCPDefinitionOptionRelId(
				cpDefinitionOptionRelId, start, end, orderByComparator);
	}

	@Override
	public List<CPDefinitionOptionValueRel> getCPDefinitionOptionValueRels(
			long[] cpDefinitionOptionValueRelsId)
		throws PortalException {

		if (ArrayUtil.isEmpty(cpDefinitionOptionValueRelsId)) {
			return Collections.emptyList();
		}

		DynamicQuery dynamicQuery = dynamicQuery();

		Property property = PropertyFactoryUtil.forName(
			"CPDefinitionOptionValueRelId");

		Criterion criterion = property.in(cpDefinitionOptionValueRelsId);

		dynamicQuery.add(criterion);

		dynamicQuery.addOrder(OrderFactoryUtil.asc("priority"));

		return cpDefinitionOptionValueRelPersistence.findWithDynamicQuery(
			dynamicQuery);
	}

	@Override
	public List<CPDefinitionOptionValueRel> getCPDefinitionOptionValueRels(
		String key, int start, int end) {

		return cpDefinitionOptionValueRelPersistence.findByKey(key, start, end);
	}

	@Override
	public int getCPDefinitionOptionValueRelsCount(
		long cpDefinitionOptionRelId) {

		return cpDefinitionOptionValueRelPersistence.
			countByCPDefinitionOptionRelId(cpDefinitionOptionRelId);
	}

	@Override
	public CPDefinitionOptionValueRel getCPInstanceCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId, long cpInstanceId)
		throws PortalException {

		List<CPInstanceOptionValueRel> cpInstanceCPInstanceOptionValueRels =
			_cpInstanceOptionValueRelLocalService.
				getCPInstanceCPInstanceOptionValueRels(
					cpDefinitionOptionRelId, cpInstanceId);

		for (CPInstanceOptionValueRel cpInstanceCPInstanceOptionValueRel :
				cpInstanceCPInstanceOptionValueRels) {

			if (cpDefinitionOptionRelId !=
					cpInstanceCPInstanceOptionValueRel.
						getCPDefinitionOptionRelId()) {

				continue;
			}

			return cpDefinitionOptionValueRelPersistence.findByPrimaryKey(
				cpInstanceCPInstanceOptionValueRel.
					getCPDefinitionOptionValueRelId());
		}

		throw new NoSuchCPDefinitionOptionValueRelException(
			String.format(
				"Unable to find option value with CP definition option ID %d " +
					"assigned to CP instance ID %d",
				cpDefinitionOptionRelId, cpInstanceId));
	}

	@Override
	public boolean hasCPDefinitionOptionValueRels(
		long cpDefinitionOptionRelId) {

		int count =
			cpDefinitionOptionValueRelPersistence.
				countByCPDefinitionOptionRelId(cpDefinitionOptionRelId);

		if (count > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasPreselectedCPDefinitionOptionValueRel(
		long cpDefinitionOptionRelId) {

		int count = cpDefinitionOptionValueRelPersistence.countByCDORI_P(
			cpDefinitionOptionRelId, true);

		if (count == 0) {
			return false;
		}

		return true;
	}

	@Override
	public void importCPDefinitionOptionRels(
			long cpDefinitionOptionRelId, ServiceContext serviceContext)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionRelId);

		CPOption cpOption = _cpOptionPersistence.fetchByPrimaryKey(
			cpDefinitionOptionRel.getCPOptionId());

		if (cpOption == null) {
			return;
		}

		List<CPOptionValue> cpOptionValues =
			_cpOptionValuePersistence.findByCPOptionId(
				cpOption.getCPOptionId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Map<String, Serializable> expandoBridgeAttributes =
			serviceContext.getExpandoBridgeAttributes();

		try {
			_addCPDefinitionOptionValueRel(
				cpDefinitionOptionRelId, cpOptionValues, serviceContext);
		}
		finally {
			serviceContext.setExpandoBridgeAttributes(expandoBridgeAttributes);
		}
	}

	@Override
	public CPDefinitionOptionValueRel resetCPInstanceCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId)
		throws PortalException {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelLocalService.
				getCPDefinitionOptionValueRel(cpDefinitionOptionValueRelId);

		cpDefinitionOptionValueRel.setCPInstanceUuid(null);
		cpDefinitionOptionValueRel.setCProductId(0);
		cpDefinitionOptionValueRel.setPrice(BigDecimal.ZERO);
		cpDefinitionOptionValueRel.setQuantity(BigDecimal.ZERO);
		cpDefinitionOptionValueRel.setUnitOfMeasureKey(null);

		return cpDefinitionOptionValueRelLocalService.
			updateCPDefinitionOptionValueRel(cpDefinitionOptionValueRel);
	}

	@Override
	public void resetCPInstanceCPDefinitionOptionValueRels(
			String cpInstanceUuid)
		throws PortalException {

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			cpDefinitionOptionValueRelPersistence.findByCPInstanceUuid(
				cpInstanceUuid);

		for (CPDefinitionOptionValueRel cpDefinitionOptionValueRel :
				cpDefinitionOptionValueRels) {

			cpDefinitionOptionValueRelLocalService.
				resetCPInstanceCPDefinitionOptionValueRel(
					cpDefinitionOptionValueRel.
						getCPDefinitionOptionValueRelId());
		}
	}

	@Override
	public Hits search(SearchContext searchContext) {
		try {
			Indexer<CPDefinitionOptionValueRel> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(
					CPDefinitionOptionValueRel.class);

			return indexer.search(searchContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public BaseModelSearchResult<CPDefinitionOptionValueRel>
			searchCPDefinitionOptionValueRels(
				long companyId, long groupId, long cpDefinitionOptionRelId,
				String keywords, int start, int end, Sort[] sorts)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.fetchByPrimaryKey(
				cpDefinitionOptionRelId);

		if ((cpDefinitionOptionRel != null) &&
			cpDefinitionOptionRel.isDefinedExternally()) {

			return new BaseModelSearchResult<>(
				_cpCollectionProviderHelper.getCPDefinitionOptionValueRels(
					companyId, groupId, cpDefinitionOptionRel, keywords,
					Pagination.of(end, start)),
				_cpCollectionProviderHelper.getCPDefinitionOptionValueRelsCount(
					companyId, groupId, cpDefinitionOptionRel, keywords));
		}

		SearchContext searchContext = _buildSearchContext(
			companyId, groupId, cpDefinitionOptionRelId, keywords, start, end,
			sorts);

		return _searchCPOptions(searchContext);
	}

	@Override
	public int searchCPDefinitionOptionValueRelsCount(
			long companyId, long groupId, long cpDefinitionOptionRelId,
			String keywords)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.fetchByPrimaryKey(
				cpDefinitionOptionRelId);

		if ((cpDefinitionOptionRel != null) &&
			cpDefinitionOptionRel.isDefinedExternally()) {

			return _cpCollectionProviderHelper.
				getCPDefinitionOptionValueRelsCount(
					companyId, groupId, cpDefinitionOptionRel, keywords);
		}

		SearchContext searchContext = _buildSearchContext(
			companyId, groupId, cpDefinitionOptionRelId, keywords,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		return _searchCPOptionsCount(searchContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPDefinitionOptionValueRel updateCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId, long cpInstanceId, String key,
			Map<Locale, String> nameMap, boolean preselected, BigDecimal price,
			double priority, BigDecimal quantity, String unitOfMeasureKey,
			ServiceContext serviceContext)
		throws PortalException {

		// Commerce product definition option value rel

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelPersistence.findByPrimaryKey(
				cpDefinitionOptionValueRelId);

		key = _friendlyURLNormalizer.normalize(key);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionValueRel.getCPDefinitionOptionRel();

		_validate(
			cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId(),
			cpDefinitionOptionRel, cpInstanceId, key, unitOfMeasureKey);

		if (CPDefinitionLocalServiceCircularDependencyUtil.isVersionable(
				cpDefinitionOptionRel.getCPDefinitionId(),
				serviceContext.getRequest())) {

			CPDefinition newCPDefinition =
				CPDefinitionLocalServiceCircularDependencyUtil.copyCPDefinition(
					cpDefinitionOptionRel.getCPDefinitionId());

			cpDefinitionOptionRel = _cpDefinitionOptionRelPersistence.findByC_C(
				newCPDefinition.getCPDefinitionId(),
				cpDefinitionOptionRel.getCPOptionId());

			cpDefinitionOptionValueRel =
				cpDefinitionOptionValueRelPersistence.findByC_K(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
					cpDefinitionOptionValueRel.getKey());
		}

		cpDefinitionOptionValueRel =
			_updateCPDefinitionOptionValueRelCPInstance(
				cpDefinitionOptionValueRel, cpInstanceId);

		cpDefinitionOptionValueRel.setKey(key);
		cpDefinitionOptionValueRel.setNameMap(nameMap);
		cpDefinitionOptionValueRel.setPriority(priority);

		if (cpDefinitionOptionRel.isPriceTypeStatic()) {
			cpDefinitionOptionValueRel.setPrice(price);
		}

		cpDefinitionOptionValueRel.setQuantity(quantity);
		cpDefinitionOptionValueRel.setUnitOfMeasureKey(unitOfMeasureKey);
		cpDefinitionOptionValueRel.setExpandoBridgeAttributes(serviceContext);

		_validateLinkedCPDefinitionOptionValueRel(cpDefinitionOptionValueRel);
		_validatePriceableCPDefinitionOptionValue(
			cpDefinitionOptionValueRel, cpDefinitionOptionRel.getPriceType());

		cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelPersistence.update(
				cpDefinitionOptionValueRel);

		cpDefinitionOptionValueRel =
			_updateCPDefinitionOptionValueRelPreselected(
				cpDefinitionOptionValueRel, preselected);

		// Commerce product definition

		_reindexCPDefinition(cpDefinitionOptionRel);

		return cpDefinitionOptionValueRel;
	}

	@Override
	public CPDefinitionOptionValueRel
		updateCPDefinitionOptionValueRelPreselected(
			long cpDefinitionOptionValueRelId, boolean preselected) {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelPersistence.fetchByPrimaryKey(
				cpDefinitionOptionValueRelId);

		return _updateCPDefinitionOptionValueRelPreselected(
			cpDefinitionOptionValueRel, preselected);
	}

	@Override
	public CPDefinitionOptionValueRel updateExternalReferenceCode(
			long cpDefinitionOptionValueRelId, String externalReferenceCode)
		throws PortalException {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelPersistence.findByPrimaryKey(
				cpDefinitionOptionValueRelId);

		cpDefinitionOptionValueRel.setExternalReferenceCode(
			externalReferenceCode);

		return cpDefinitionOptionValueRelPersistence.update(
			cpDefinitionOptionValueRel);
	}

	private void _addCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId, List<CPOptionValue> cpOptionValues,
			ServiceContext serviceContext)
		throws PortalException {

		for (CPOptionValue cpOptionValue : cpOptionValues) {
			if (_hasCustomAttributes(cpOptionValue)) {
				ExpandoBridge expandoBridge = cpOptionValue.getExpandoBridge();

				serviceContext.setExpandoBridgeAttributes(
					expandoBridge.getAttributes());
			}
			else {
				serviceContext.setExpandoBridgeAttributes(
					Collections.emptyMap());
			}

			cpDefinitionOptionValueRelLocalService.
				addCPDefinitionOptionValueRel(
					cpDefinitionOptionRelId, cpOptionValue, serviceContext);
		}
	}

	private SearchContext _buildSearchContext(
		long companyId, long groupId, long cpDefinitionOptionRelId,
		String keywords, int start, int end, Sort[] sorts) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				_FIELD_KEY, keywords
			).put(
				Field.CONTENT, keywords
			).put(
				Field.ENTRY_CLASS_PK, keywords
			).put(
				Field.NAME, keywords
			).put(
				"CPDefinitionOptionRelId", cpDefinitionOptionRelId
			).put(
				"params",
				LinkedHashMapBuilder.<String, Object>put(
					"keywords", keywords
				).build()
			).build());
		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);
		searchContext.setGroupIds(new long[] {groupId});

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		if (sorts != null) {
			searchContext.setSorts(sorts);
		}

		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	private List<CPDefinitionOptionValueRel> _getCPDefinitionOptionValueRels(
			Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			new ArrayList<>(documents.size());

		for (Document document : documents) {
			long cpDefinitionOptionValueRelId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
				fetchCPDefinitionOptionValueRel(cpDefinitionOptionValueRelId);

			if (cpDefinitionOptionValueRel == null) {
				cpDefinitionOptionValueRels = null;

				Indexer<CPDefinitionOptionValueRel> indexer =
					IndexerRegistryUtil.getIndexer(
						CPDefinitionOptionValueRel.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (cpDefinitionOptionValueRels != null) {
				cpDefinitionOptionValueRels.add(cpDefinitionOptionValueRel);
			}
		}

		return cpDefinitionOptionValueRels;
	}

	private String _getTimeZone(String[] splits) {
		if ((splits == null) || (splits.length < 7) || splits[7].isEmpty()) {
			return StringPool.BLANK;
		}

		if (splits.length == 8) {
			return splits[7].toUpperCase();
		}

		String timeZone = StringBundler.concat(
			StringUtil.upperCaseFirstLetter(splits[7]),
			StringPool.FORWARD_SLASH,
			StringUtil.upperCaseFirstLetter(splits[8]));

		if ((splits.length > 9) && Validator.isNotNull(splits[9])) {
			return StringBundler.concat(
				timeZone, StringPool.UNDERLINE,
				StringUtil.upperCaseFirstLetter(splits[9]));
		}

		return timeZone;
	}

	private boolean _hasCustomAttributes(CPOptionValue cpOptionValue)
		throws PortalException {

		try {
			return CustomAttributesUtil.hasCustomAttributes(
				cpOptionValue.getCompanyId(), CPOptionValue.class.getName(),
				cpOptionValue.getCPOptionValueId(), null);
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	private void _reindexCPDefinition(
			CPDefinitionOptionRel cpDefinitionOptionRel)
		throws PortalException {

		Indexer<CPDefinition> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			CPDefinition.class);

		indexer.reindex(
			CPDefinition.class.getName(),
			cpDefinitionOptionRel.getCPDefinitionId());
	}

	private BaseModelSearchResult<CPDefinitionOptionValueRel> _searchCPOptions(
			SearchContext searchContext)
		throws PortalException {

		Indexer<CPDefinitionOptionValueRel> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(
				CPDefinitionOptionValueRel.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext, _SELECTED_FIELD_NAMES);

			List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
				_getCPDefinitionOptionValueRels(hits);

			if (cpDefinitionOptionValueRels != null) {
				return new BaseModelSearchResult<>(
					cpDefinitionOptionValueRels, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	private int _searchCPOptionsCount(SearchContext searchContext)
		throws PortalException {

		Indexer<CPDefinitionOptionValueRel> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(
				CPDefinitionOptionValueRel.class);

		return GetterUtil.getInteger(indexer.searchCount(searchContext));
	}

	private CPDefinitionOptionValueRel
			_updateCPDefinitionOptionValueRelCPInstance(
				CPDefinitionOptionValueRel cpDefinitionOptionValueRel,
				long cpInstanceId)
		throws PortalException {

		if (cpInstanceId <= 0) {
			cpDefinitionOptionValueRel.setCPInstanceUuid(null);
			cpDefinitionOptionValueRel.setCProductId(0);

			return cpDefinitionOptionValueRel;
		}

		CPInstance cpInstance = _cpInstancePersistence.findByPrimaryKey(
			cpInstanceId);

		cpDefinitionOptionValueRel.setCPInstanceUuid(
			cpInstance.getCPInstanceUuid());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinitionOptionValueRel.setCProductId(cpDefinition.getCProductId());

		return cpDefinitionOptionValueRel;
	}

	private CPDefinitionOptionValueRel
		_updateCPDefinitionOptionValueRelPreselected(
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel,
			boolean preselected) {

		if (!preselected) {
			cpDefinitionOptionValueRel.setPreselected(false);

			return cpDefinitionOptionValueRelPersistence.update(
				cpDefinitionOptionValueRel);
		}

		CPDefinitionOptionValueRel curPreselectedCPDefinitionOptionValueRel =
			fetchPreselectedCPDefinitionOptionValueRel(
				cpDefinitionOptionValueRel.getCPDefinitionOptionRelId());

		if (curPreselectedCPDefinitionOptionValueRel != null) {
			curPreselectedCPDefinitionOptionValueRel.setPreselected(false);

			cpDefinitionOptionValueRelPersistence.update(
				curPreselectedCPDefinitionOptionValueRel);
		}

		cpDefinitionOptionValueRel.setPreselected(true);

		return cpDefinitionOptionValueRelPersistence.update(
			cpDefinitionOptionValueRel);
	}

	private void _validate(
			long cpDefinitionOptionValueRelId,
			CPDefinitionOptionRel cpDefinitionOptionRel, long cpInstanceId,
			String key, String unitOfMeasureKey)
		throws PortalException {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelPersistence.fetchByC_K(
				cpDefinitionOptionRel.getCPDefinitionOptionRelId(), key);

		if ((cpDefinitionOptionValueRel != null) &&
			(cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId() !=
				cpDefinitionOptionValueRelId)) {

			throw new DuplicateCPDefinitionOptionValueRelKeyException();
		}

		if (Objects.equals(
				CPConstants.PRODUCT_OPTION_SELECT_DATE_KEY,
				cpDefinitionOptionRel.getCommerceOptionTypeKey())) {

			if (key == null) {
				throw new CPDefinitionOptionValueRelKeyException(
					"Key is mandatory");
			}

			if (!key.matches("^[a-z0-9-]*$")) {
				throw new CPDefinitionOptionValueRelKeyException("Invalid key");
			}

			String[] splits = key.split(StringPool.DASH);

			Integer month = 0;
			Integer day = 0;
			Integer year = 0;
			Integer hour = 0;
			Integer minute = 0;

			try {
				month = Integer.valueOf(splits[0]);
				day = Integer.valueOf(splits[1]);
				year = Integer.valueOf(splits[2]);
				hour = Integer.valueOf(splits[3]);
				minute = Integer.valueOf(splits[4]);
				Integer.valueOf(splits[5]);
			}
			catch (NumberFormatException numberFormatException) {
				throw new CPDefinitionOptionValueRelKeyException(
					"Invalid date", numberFormatException);
			}

			_portal.getDate(
				month - 1, day, year, hour, minute,
				TimeZoneUtil.getTimeZone(_getTimeZone(splits)),
				CPDefinitionOptionValueRelKeyException.class);

			if (!Objects.equals(CPConstants.DAYS_DURATION_TYPE, splits[6]) &&
				!Objects.equals(CPConstants.HOURS_DURATION_TYPE, splits[6])) {

				throw new CPDefinitionOptionValueRelKeyException(
					"Invalid duration type");
			}
		}

		if (cpInstanceId > 0) {
			if (Validator.isNotNull(unitOfMeasureKey)) {
				CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
					_cpInstanceUnitOfMeasureLocalService.
						fetchCPInstanceUnitOfMeasure(
							cpInstanceId, unitOfMeasureKey);

				if (cpInstanceUnitOfMeasure == null) {
					throw new NoSuchCPInstanceUnitOfMeasureException(
						"No commerce product instance unit of measure exists " +
							"with the primary key " + unitOfMeasureKey);
				}
			}
			else {
				int cpInstanceUnitOfMeasuresCount =
					_cpInstanceUnitOfMeasureLocalService.
						getCPInstanceUnitOfMeasuresCount(cpInstanceId);

				if (cpInstanceUnitOfMeasuresCount > 0) {
					throw new CommerceInventoryWarehouseItemUnitOfMeasureKeyException(
						"Unit of measure key is mandatory");
				}
			}
		}
	}

	private void _validateLinkableCPInstance(CPInstance cpInstance)
		throws PortalException {

		if (_cpDefinitionOptionRelLocalService.
				hasCPDefinitionRequiredCPDefinitionOptionRels(
					cpInstance.getCPDefinitionId()) ||
			(cpInstance.getCPSubscriptionInfo() != null)) {

			throw new CPDefinitionOptionValueRelCPInstanceException();
		}
	}

	private void _validateLinkedCPDefinitionOptionValueRel(
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel)
		throws PortalException {

		if (Validator.isNull(cpDefinitionOptionValueRel.getCPInstanceUuid()) ||
			(cpDefinitionOptionValueRel.getCProductId() == 0)) {

			return;
		}

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			cpDefinitionOptionValueRelPersistence.findByCPDefinitionOptionRelId(
				cpDefinitionOptionValueRel.getCPDefinitionOptionRelId());

		for (CPDefinitionOptionValueRel curCPDefinitionOptionValueRel :
				cpDefinitionOptionValueRels) {

			if (cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId() ==
					curCPDefinitionOptionValueRel.
						getCPDefinitionOptionValueRelId()) {

				continue;
			}

			if (Objects.equals(
					cpDefinitionOptionValueRel.getCPInstanceUuid(),
					curCPDefinitionOptionValueRel.getCPInstanceUuid()) &&
				(cpDefinitionOptionValueRel.getCProductId() ==
					curCPDefinitionOptionValueRel.getCProductId()) &&
				BigDecimalUtil.eq(
					cpDefinitionOptionValueRel.getQuantity(),
					curCPDefinitionOptionValueRel.getQuantity()) &&
				Objects.equals(
					cpDefinitionOptionValueRel.getUnitOfMeasureKey(),
					curCPDefinitionOptionValueRel.getUnitOfMeasureKey())) {

				throw new CPDefinitionOptionValueRelQuantityException();
			}
		}
	}

	private void _validatePriceableCPDefinitionOptionValue(
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel,
			String priceType)
		throws PortalException {

		if (cpDefinitionOptionValueRel.isNew()) {
			return;
		}

		if (Validator.isNull(priceType)) {
			BigDecimal quantity = cpDefinitionOptionValueRel.getQuantity();

			if (Validator.isNotNull(
					cpDefinitionOptionValueRel.getCPInstanceUuid()) ||
				(cpDefinitionOptionValueRel.getCProductId() != 0) ||
				(cpDefinitionOptionValueRel.getPrice() != null) ||
				((quantity != null) &&
				 (quantity.compareTo(BigDecimal.ZERO) != 0))) {

				throw new CPDefinitionOptionValueRelCPInstanceException();
			}

			return;
		}

		if (Objects.equals(
				priceType, CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC) &&
			(cpDefinitionOptionValueRel.getPrice() == null)) {

			throw new CPDefinitionOptionValueRelPriceException();
		}

		CPInstance cpInstance = _cpInstanceLocalService.fetchCProductInstance(
			cpDefinitionOptionValueRel.getCProductId(),
			cpDefinitionOptionValueRel.getCPInstanceUuid());

		if (((cpInstance == null) ||
			 (cpDefinitionOptionValueRel.getPrice() != null)) &&
			Objects.equals(
				priceType, CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC)) {

			throw new CPDefinitionOptionValueRelCPInstanceException();
		}

		if (cpInstance == null) {
			return;
		}

		_validateLinkableCPInstance(cpInstance);

		if (BigDecimalUtil.lte(
				cpDefinitionOptionValueRel.getQuantity(), BigDecimal.ZERO)) {

			throw new CPDefinitionOptionValueRelQuantityException();
		}

		if (!cpInstance.isApproved()) {
			throw new CPDefinitionOptionValueRelCPInstanceException();
		}
	}

	private static final String _FIELD_KEY = "key";

	private static final String[] _SELECTED_FIELD_NAMES = {
		Field.ENTRY_CLASS_PK, Field.COMPANY_ID, Field.GROUP_ID, Field.UID
	};

	@Reference
	private CPCollectionProviderHelper _cpCollectionProviderHelper;

	@Reference
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	@Reference
	private CPDefinitionOptionRelPersistence _cpDefinitionOptionRelPersistence;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private CPInstanceOptionValueRelLocalService
		_cpInstanceOptionValueRelLocalService;

	@Reference
	private CPInstancePersistence _cpInstancePersistence;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@Reference
	private CPOptionPersistence _cpOptionPersistence;

	@Reference
	private CPOptionValuePersistence _cpOptionValuePersistence;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}