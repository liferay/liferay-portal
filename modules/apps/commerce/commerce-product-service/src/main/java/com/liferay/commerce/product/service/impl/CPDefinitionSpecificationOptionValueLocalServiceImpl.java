/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.exception.CPDefinitionSpecificationOptionValueKeyException;
import com.liferay.commerce.product.internal.util.CPDefinitionLocalServiceCircularDependencyUtil;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValueTable;
import com.liferay.commerce.product.model.CPSpecificationOptionTable;
import com.liferay.commerce.product.service.base.CPDefinitionSpecificationOptionValueLocalServiceBaseImpl;
import com.liferay.commerce.product.service.persistence.CPDefinitionPersistence;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue",
	service = AopService.class
)
public class CPDefinitionSpecificationOptionValueLocalServiceImpl
	extends CPDefinitionSpecificationOptionValueLocalServiceBaseImpl {

	@Override
	public CPDefinitionSpecificationOptionValue
			addCPDefinitionSpecificationOptionValue(
				String externalReferenceCode, long cpDefinitionId,
				long cpSpecificationOptionId, long cpOptionCategoryId,
				double priority, Map<Locale, String> valueMap, boolean visible,
				ServiceContext serviceContext)
		throws PortalException {

		CPDefinition cpDefinition = _cpDefinitionPersistence.findByPrimaryKey(
			cpDefinitionId);

		if (CPDefinitionLocalServiceCircularDependencyUtil.isVersionable(
				cpDefinitionId)) {

			cpDefinition =
				CPDefinitionLocalServiceCircularDependencyUtil.copyCPDefinition(
					cpDefinitionId);

			cpDefinitionId = cpDefinition.getCPDefinitionId();
		}

		User user = _userLocalService.getUser(serviceContext.getUserId());

		long cpDefinitionSpecificationOptionValueId =
			counterLocalService.increment();

		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue =
				cpDefinitionSpecificationOptionValuePersistence.create(
					cpDefinitionSpecificationOptionValueId);

		cpDefinitionSpecificationOptionValue.setExternalReferenceCode(
			externalReferenceCode);
		cpDefinitionSpecificationOptionValue.setGroupId(
			cpDefinition.getGroupId());
		cpDefinitionSpecificationOptionValue.setCompanyId(user.getCompanyId());
		cpDefinitionSpecificationOptionValue.setUserId(user.getUserId());
		cpDefinitionSpecificationOptionValue.setUserName(user.getFullName());
		cpDefinitionSpecificationOptionValue.setCPDefinitionId(
			cpDefinition.getCPDefinitionId());
		cpDefinitionSpecificationOptionValue.setCPSpecificationOptionId(
			cpSpecificationOptionId);
		cpDefinitionSpecificationOptionValue.setCPOptionCategoryId(
			cpOptionCategoryId);
		cpDefinitionSpecificationOptionValue.setKey(
			String.valueOf(cpDefinitionSpecificationOptionValueId));
		cpDefinitionSpecificationOptionValue.setPriority(priority);
		cpDefinitionSpecificationOptionValue.setValueMap(valueMap);
		cpDefinitionSpecificationOptionValue.setVisible(visible);
		cpDefinitionSpecificationOptionValue.setExpandoBridgeAttributes(
			serviceContext);

		cpDefinitionSpecificationOptionValue =
			cpDefinitionSpecificationOptionValuePersistence.update(
				cpDefinitionSpecificationOptionValue);

		// Commerce product definition

		_reindexCPDefinition(cpDefinitionId);

		return cpDefinitionSpecificationOptionValue;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPDefinitionSpecificationOptionValue
			deleteCPDefinitionSpecificationOptionValue(
				CPDefinitionSpecificationOptionValue
					cpDefinitionSpecificationOptionValue)
		throws PortalException {

		return cpDefinitionSpecificationOptionValueLocalService.
			deleteCPDefinitionSpecificationOptionValue(
				cpDefinitionSpecificationOptionValue, true);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPDefinitionSpecificationOptionValue
			deleteCPDefinitionSpecificationOptionValue(
				CPDefinitionSpecificationOptionValue
					cpDefinitionSpecificationOptionValue,
				boolean makeCopy)
		throws PortalException {

		if (makeCopy &&
			CPDefinitionLocalServiceCircularDependencyUtil.isVersionable(
				cpDefinitionSpecificationOptionValue.getCPDefinitionId())) {

			try {
				CPDefinition newCPDefinition =
					CPDefinitionLocalServiceCircularDependencyUtil.
						copyCPDefinition(
							cpDefinitionSpecificationOptionValue.
								getCPDefinitionId());

				cpDefinitionSpecificationOptionValue =
					cpDefinitionSpecificationOptionValuePersistence.
						findByC_CSOVI(
							newCPDefinition.getCPDefinitionId(),
							cpDefinitionSpecificationOptionValue.
								getCPDefinitionSpecificationOptionValueId());
			}
			catch (PortalException portalException) {
				throw new SystemException(portalException);
			}
		}

		// Commerce product definition specification option value

		cpDefinitionSpecificationOptionValuePersistence.remove(
			cpDefinitionSpecificationOptionValue);

		// Expando

		_expandoRowLocalService.deleteRows(
			cpDefinitionSpecificationOptionValue.
				getCPDefinitionSpecificationOptionValueId());

		_reindexCPDefinition(
			cpDefinitionSpecificationOptionValue.getCPDefinitionId());

		return cpDefinitionSpecificationOptionValue;
	}

	@Override
	public CPDefinitionSpecificationOptionValue
			deleteCPDefinitionSpecificationOptionValue(
				long cpDefinitionSpecificationOptionValueId)
		throws PortalException {

		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue =
				cpDefinitionSpecificationOptionValuePersistence.
					findByPrimaryKey(cpDefinitionSpecificationOptionValueId);

		return cpDefinitionSpecificationOptionValueLocalService.
			deleteCPDefinitionSpecificationOptionValue(
				cpDefinitionSpecificationOptionValue);
	}

	@Override
	public void deleteCPDefinitionSpecificationOptionValues(long cpDefinitionId)
		throws PortalException {

		cpDefinitionSpecificationOptionValueLocalService.
			deleteCPDefinitionSpecificationOptionValues(cpDefinitionId, true);
	}

	@Override
	public void deleteCPDefinitionSpecificationOptionValues(
			long cpDefinitionId, boolean makeCopy)
		throws PortalException {

		List<CPDefinitionSpecificationOptionValue>
			cpDefinitionSpecificationOptionValues =
				getCPDefinitionSpecificationOptionValues(
					cpDefinitionId, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null);

		// Commerce product definition specification option value

		for (CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue :
					cpDefinitionSpecificationOptionValues) {

			cpDefinitionSpecificationOptionValueLocalService.
				deleteCPDefinitionSpecificationOptionValue(
					cpDefinitionSpecificationOptionValue, makeCopy);
		}

		// Commerce product definition

		_reindexCPDefinition(cpDefinitionId);
	}

	@Override
	public void deleteCPSpecificationOptionDefinitionValues(
			long cpSpecificationOptionId)
		throws PortalException {

		List<CPDefinitionSpecificationOptionValue>
			cpDefinitionSpecificationOptionValues =
				getCPDefinitionSpecificationOptionValues(
					cpSpecificationOptionId);

		// Commerce product definition specification option value

		for (CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue :
					cpDefinitionSpecificationOptionValues) {

			cpDefinitionSpecificationOptionValueLocalService.
				deleteCPDefinitionSpecificationOptionValue(
					cpDefinitionSpecificationOptionValue);

			// Commerce product definition

			_reindexCPDefinition(
				cpDefinitionSpecificationOptionValue.getCPDefinitionId());
		}
	}

	@Override
	public CPDefinitionSpecificationOptionValue
		fetchCPDefinitionSpecificationOptionValue(
			long cpDefinitionId, long cpDefinitionSpecificationOptionValueId) {

		return cpDefinitionSpecificationOptionValuePersistence.fetchByC_CSOVI(
			cpDefinitionId, cpDefinitionSpecificationOptionValueId);
	}

	@Override
	public CPDefinitionSpecificationOptionValue
		fetchCPDefinitionSpecificationOptionValue(
			long cpDefinitionId, String key) {

		return cpDefinitionSpecificationOptionValuePersistence.fetchByC_K(
			cpDefinitionId, key);
	}

	@Override
	public List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues(long cpSpecificationOptionId) {

		return cpDefinitionSpecificationOptionValuePersistence.
			findByCPSpecificationOptionId(cpSpecificationOptionId);
	}

	@Override
	public List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues(
			long cpDefinitionId, Boolean visible, int start, int end,
			OrderByComparator<CPDefinitionSpecificationOptionValue>
				orderByComparator) {

		if (visible == null) {
			return cpDefinitionSpecificationOptionValuePersistence.
				findByCPDefinitionId(
					cpDefinitionId, start, end, orderByComparator);
		}

		return dslQuery(
			DSLQueryFactoryUtil.select(
				CPDefinitionSpecificationOptionValueTable.INSTANCE
			).from(
				CPDefinitionSpecificationOptionValueTable.INSTANCE
			).innerJoinON(
				CPSpecificationOptionTable.INSTANCE,
				CPSpecificationOptionTable.INSTANCE.CPSpecificationOptionId.eq(
					CPDefinitionSpecificationOptionValueTable.INSTANCE.
						CPSpecificationOptionId)
			).where(
				_getPredicate(cpDefinitionId, visible)
			).orderBy(
				CPDefinitionSpecificationOptionValueTable.INSTANCE,
				orderByComparator
			).limit(
				start, end
			));
	}

	@Override
	public List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues(
			long cpSpecificationOptionId, int start, int end) {

		return cpDefinitionSpecificationOptionValuePersistence.
			findByCPSpecificationOptionId(cpSpecificationOptionId, start, end);
	}

	@Override
	public List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues(
			long cpDefinitionId, long cpOptionCategoryId, Boolean visible) {

		if (visible == null) {
			return cpDefinitionSpecificationOptionValuePersistence.findByC_COC(
				cpDefinitionId, cpOptionCategoryId);
		}

		return dslQuery(
			DSLQueryFactoryUtil.select(
				CPDefinitionSpecificationOptionValueTable.INSTANCE
			).from(
				CPDefinitionSpecificationOptionValueTable.INSTANCE
			).innerJoinON(
				CPSpecificationOptionTable.INSTANCE,
				CPSpecificationOptionTable.INSTANCE.CPSpecificationOptionId.eq(
					CPDefinitionSpecificationOptionValueTable.INSTANCE.
						CPSpecificationOptionId)
			).where(
				_getPredicate(
					cpDefinitionId, visible
				).and(
					CPDefinitionSpecificationOptionValueTable.INSTANCE.
						CPOptionCategoryId.eq(cpOptionCategoryId)
				)
			));
	}

	@Override
	public List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValuesByC_CSO(
			long cpDefinitionId, long cpSpecificationOptionId) {

		return cpDefinitionSpecificationOptionValuePersistence.findByC_CSO(
			cpDefinitionId, cpSpecificationOptionId);
	}

	@Override
	public int getCPDefinitionSpecificationOptionValuesCount(
		long cpDefinitionId, Boolean visible) {

		if (visible == null) {
			return cpDefinitionSpecificationOptionValuePersistence.
				countByCPDefinitionId(cpDefinitionId);
		}

		return dslQueryCount(
			DSLQueryFactoryUtil.countDistinct(
				CPDefinitionSpecificationOptionValueTable.INSTANCE.
					CPDefinitionSpecificationOptionValueId
			).from(
				CPDefinitionSpecificationOptionValueTable.INSTANCE
			).innerJoinON(
				CPSpecificationOptionTable.INSTANCE,
				CPSpecificationOptionTable.INSTANCE.CPSpecificationOptionId.eq(
					CPDefinitionSpecificationOptionValueTable.INSTANCE.
						CPSpecificationOptionId)
			).where(
				_getPredicate(cpDefinitionId, visible)
			));
	}

	@Override
	public int getCPSpecificationOptionDefinitionValuesCount(
		long cpSpecificationOptionId) {

		return cpDefinitionSpecificationOptionValuePersistence.
			countByCPSpecificationOptionId(cpSpecificationOptionId);
	}

	@Override
	public CPDefinitionSpecificationOptionValue
			updateCPDefinitionSpecificationOptionValue(
				String externalReferenceCode,
				long cpDefinitionSpecificationOptionValueId,
				long cpOptionCategoryId, String key, double priority,
				Map<Locale, String> valueMap, boolean visible,
				ServiceContext serviceContext)
		throws PortalException {

		// Commerce product definition specification option value

		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue =
				cpDefinitionSpecificationOptionValuePersistence.
					findByPrimaryKey(cpDefinitionSpecificationOptionValueId);

		_validateKey(
			cpDefinitionSpecificationOptionValue.
				getCPDefinitionSpecificationOptionValueId(),
			cpDefinitionSpecificationOptionValue.getCPDefinitionId(), key);

		if (CPDefinitionLocalServiceCircularDependencyUtil.isVersionable(
				cpDefinitionSpecificationOptionValue.getCPDefinitionId())) {

			CPDefinition newCPDefinition =
				CPDefinitionLocalServiceCircularDependencyUtil.copyCPDefinition(
					cpDefinitionSpecificationOptionValue.getCPDefinitionId());

			cpDefinitionSpecificationOptionValue =
				cpDefinitionSpecificationOptionValuePersistence.findByC_CSOVI(
					newCPDefinition.getCPDefinitionId(),
					cpDefinitionSpecificationOptionValue.
						getCPDefinitionSpecificationOptionValueId());
		}

		cpDefinitionSpecificationOptionValue.setExternalReferenceCode(
			externalReferenceCode);
		cpDefinitionSpecificationOptionValue.setCPOptionCategoryId(
			cpOptionCategoryId);
		cpDefinitionSpecificationOptionValue.setKey(key);
		cpDefinitionSpecificationOptionValue.setPriority(priority);
		cpDefinitionSpecificationOptionValue.setValueMap(valueMap);
		cpDefinitionSpecificationOptionValue.setVisible(visible);
		cpDefinitionSpecificationOptionValue.setExpandoBridgeAttributes(
			serviceContext);

		cpDefinitionSpecificationOptionValue =
			cpDefinitionSpecificationOptionValuePersistence.update(
				cpDefinitionSpecificationOptionValue);

		// Commerce product definition

		_reindexCPDefinition(
			cpDefinitionSpecificationOptionValue.getCPDefinitionId());

		return cpDefinitionSpecificationOptionValue;
	}

	@Override
	public CPDefinitionSpecificationOptionValue updateCPOptionCategoryId(
			long cpDefinitionSpecificationOptionValueId,
			long cpOptionCategoryId)
		throws PortalException {

		// Commerce product definition specification option value

		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue =
				cpDefinitionSpecificationOptionValuePersistence.
					findByPrimaryKey(cpDefinitionSpecificationOptionValueId);

		if (CPDefinitionLocalServiceCircularDependencyUtil.isVersionable(
				cpDefinitionSpecificationOptionValue.getCPDefinitionId())) {

			CPDefinition newCPDefinition =
				CPDefinitionLocalServiceCircularDependencyUtil.copyCPDefinition(
					cpDefinitionSpecificationOptionValue.getCPDefinitionId());

			cpDefinitionSpecificationOptionValue =
				cpDefinitionSpecificationOptionValuePersistence.findByC_CSOVI(
					newCPDefinition.getCPDefinitionId(),
					cpDefinitionSpecificationOptionValue.
						getCPDefinitionSpecificationOptionValueId());
		}

		cpDefinitionSpecificationOptionValue.setCPOptionCategoryId(
			cpOptionCategoryId);

		cpDefinitionSpecificationOptionValue =
			cpDefinitionSpecificationOptionValuePersistence.update(
				cpDefinitionSpecificationOptionValue);

		// Commerce product definition

		_reindexCPDefinition(
			cpDefinitionSpecificationOptionValue.getCPDefinitionId());

		return cpDefinitionSpecificationOptionValue;
	}

	private Predicate _getPredicate(long cpDefinitionId, boolean visible) {
		return CPDefinitionSpecificationOptionValueTable.INSTANCE.
			CPDefinitionId.eq(
				cpDefinitionId
			).and(
				() -> {
					if (visible) {
						return CPDefinitionSpecificationOptionValueTable.
							INSTANCE.visible.eq(
								true
							).and(
								CPSpecificationOptionTable.INSTANCE.visible.eq(
									true)
							);
					}

					return CPDefinitionSpecificationOptionValueTable.INSTANCE.
						visible.eq(
							false
						).or(
							CPSpecificationOptionTable.INSTANCE.visible.eq(
								false)
						);
				}
			);
	}

	private void _reindexCPDefinition(long cpDefinitionId)
		throws PortalException {

		Indexer<CPDefinition> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			CPDefinition.class);

		indexer.reindex(CPDefinition.class.getName(), cpDefinitionId);
	}

	private void _validateKey(
			long cpDefinitionSpecificationOptionValueId, long cpDefinitionId,
			String key)
		throws PortalException {

		if (Validator.isNull(key)) {
			throw new CPDefinitionSpecificationOptionValueKeyException();
		}

		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue =
				cpDefinitionSpecificationOptionValuePersistence.fetchByC_K(
					cpDefinitionId, key);

		if (cpDefinitionSpecificationOptionValue == null) {
			return;
		}

		long oldCPDefinitionSpecificationOptionValueId =
			cpDefinitionSpecificationOptionValue.
				getCPDefinitionSpecificationOptionValueId();

		if (oldCPDefinitionSpecificationOptionValueId !=
				cpDefinitionSpecificationOptionValueId) {

			throw new CPDefinitionSpecificationOptionValueKeyException();
		}
	}

	@Reference
	private CPDefinitionPersistence _cpDefinitionPersistence;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private UserLocalService _userLocalService;

}