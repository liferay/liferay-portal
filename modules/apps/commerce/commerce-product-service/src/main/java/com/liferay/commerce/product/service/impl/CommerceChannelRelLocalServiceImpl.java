/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceCurrencyTable;
import com.liferay.commerce.product.exception.DuplicateCommerceChannelRelException;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.model.CommerceChannelRelTable;
import com.liferay.commerce.product.service.base.CommerceChannelRelLocalServiceBaseImpl;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.CountryTable;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CommerceChannelRel",
	service = AopService.class
)
public class CommerceChannelRelLocalServiceImpl
	extends CommerceChannelRelLocalServiceBaseImpl {

	@Override
	public CommerceChannelRel addCommerceChannelRel(
			String className, long classPK, long commerceChannelId,
			ServiceContext serviceContext)
		throws PortalException {

		long classNameId = _classNameLocalService.getClassNameId(className);

		CommerceChannelRel existingCommerceChannelRel =
			commerceChannelRelPersistence.fetchByC_C_C(
				classNameId, classPK, commerceChannelId);

		if (existingCommerceChannelRel != null) {
			throw new DuplicateCommerceChannelRelException();
		}

		User user = _userLocalService.getUser(serviceContext.getUserId());

		long commerceChannelRelId = counterLocalService.increment();

		CommerceChannelRel commerceChannelRel =
			commerceChannelRelPersistence.create(commerceChannelRelId);

		commerceChannelRel.setCompanyId(user.getCompanyId());
		commerceChannelRel.setUserId(user.getUserId());
		commerceChannelRel.setUserName(user.getFullName());
		commerceChannelRel.setClassNameId(classNameId);
		commerceChannelRel.setClassPK(classPK);
		commerceChannelRel.setCommerceChannelId(commerceChannelId);

		commerceChannelRel = commerceChannelRelPersistence.update(
			commerceChannelRel);

		_reindexCommerceChannelRels(
			commerceChannelRel.getClassNameId(),
			commerceChannelRel.getClassPK());

		return commerceChannelRel;
	}

	@Override
	public List<CommerceChannelRel> addCommerceChannelRels(
		String className, long[] classPKs, long commerceChannelId,
		ServiceContext serviceContext) {

		return TransformUtil.transformToList(
			classPKs,
			classPK -> addCommerceChannelRel(
				className, classPK, commerceChannelId, serviceContext));
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceChannelRel deleteCommerceChannelRel(
		CommerceChannelRel commerceChannelRel) {

		commerceChannelRelPersistence.remove(commerceChannelRel);

		_reindexCommerceChannelRels(
			commerceChannelRel.getClassNameId(),
			commerceChannelRel.getClassPK());

		return commerceChannelRel;
	}

	@Override
	public CommerceChannelRel deleteCommerceChannelRel(
			long commerceChannelRelId)
		throws PortalException {

		CommerceChannelRel commerceChannelRel =
			commerceChannelRelPersistence.findByPrimaryKey(
				commerceChannelRelId);

		return commerceChannelRelLocalService.deleteCommerceChannelRel(
			commerceChannelRel);
	}

	@Override
	public void deleteCommerceChannelRels(long commerceChannelId) {
		commerceChannelRelPersistence.removeByCommerceChannelId(
			commerceChannelId);
	}

	@Override
	public void deleteCommerceChannelRels(String className, long classPK) {
		commerceChannelRelPersistence.removeByC_C(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public CommerceChannelRel fetchCommerceChannelRel(
		String className, long classPK, long commerceChannelId) {

		return commerceChannelRelPersistence.fetchByC_C_C(
			_classNameLocalService.getClassNameId(className), classPK,
			commerceChannelId);
	}

	@Override
	public List<CommerceChannelRel> getCommerceChannelRels(
		long commerceChannelId, int start, int end,
		OrderByComparator<CommerceChannelRel> orderByComparator) {

		return commerceChannelRelPersistence.findByCommerceChannelId(
			commerceChannelId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceChannelRel> getCommerceChannelRels(
		String className, long classPK, int start, int end,
		OrderByComparator<CommerceChannelRel> orderByComparator) {

		return commerceChannelRelPersistence.findByC_C(
			_classNameLocalService.getClassNameId(className), classPK, start,
			end, orderByComparator);
	}

	@Override
	public List<CommerceChannelRel> getCommerceChannelRels(
		String className, long classPK, String name, int start, int end) {

		return commerceChannelRelFinder.findByC_C(
			className, classPK, name, start, end);
	}

	@Override
	public int getCommerceChannelRelsCount(long commerceChannelId) {
		return commerceChannelRelPersistence.countByCommerceChannelId(
			commerceChannelId);
	}

	@Override
	public int getCommerceChannelRelsCount(String className, long classPK) {
		return commerceChannelRelPersistence.countByC_C(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public List<CommerceChannelRel> getCommerceCurrencyCommerceChannelRels(
		long commerceChannelId, String name, int start, int end) {

		return dslQuery(
			DSLQueryFactoryUtil.select(
				CommerceChannelRelTable.INSTANCE
			).from(
				CommerceChannelRelTable.INSTANCE
			).leftJoinOn(
				CommerceCurrencyTable.INSTANCE,
				CommerceCurrencyTable.INSTANCE.commerceCurrencyId.eq(
					CommerceChannelRelTable.INSTANCE.classPK)
			).where(
				CommerceChannelRelTable.INSTANCE.classNameId.eq(
					_classNameLocalService.getClassNameId(
						CommerceCurrency.class.getName())
				).and(
					CommerceChannelRelTable.INSTANCE.commerceChannelId.eq(
						commerceChannelId)
				).and(
					() -> {
						if (Validator.isNull(name)) {
							return null;
						}

						return DSLFunctionFactoryUtil.lower(
							CommerceCurrencyTable.INSTANCE.name
						).like(
							StringPool.PERCENT + StringUtil.toLowerCase(name) +
								StringPool.PERCENT
						);
					}
				)
			).limit(
				start, end
			));
	}

	@Override
	public int getCommerceCurrencyCommerceChannelRelsCount(
		long commerceChannelId, String name) {

		return dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				CommerceChannelRelTable.INSTANCE
			).leftJoinOn(
				CommerceCurrencyTable.INSTANCE,
				CommerceCurrencyTable.INSTANCE.commerceCurrencyId.eq(
					CommerceChannelRelTable.INSTANCE.classPK)
			).where(
				CommerceChannelRelTable.INSTANCE.commerceChannelId.eq(
					commerceChannelId
				).and(
					CommerceChannelRelTable.INSTANCE.classNameId.eq(
						_classNameLocalService.getClassNameId(
							CommerceCurrency.class.getName()))
				).and(
					() -> {
						if (Validator.isNull(name)) {
							return null;
						}

						return DSLFunctionFactoryUtil.lower(
							CommerceCurrencyTable.INSTANCE.name
						).like(
							StringPool.PERCENT + StringUtil.toLowerCase(name) +
								StringPool.PERCENT
						);
					}
				)
			));
	}

	@Override
	public List<CommerceChannelRel> getCountryCommerceChannelRels(
		long commerceChannelId, String name, int start, int end) {

		return dslQuery(
			DSLQueryFactoryUtil.select(
				CommerceChannelRelTable.INSTANCE
			).from(
				CommerceChannelRelTable.INSTANCE
			).leftJoinOn(
				CountryTable.INSTANCE,
				CountryTable.INSTANCE.countryId.eq(
					CommerceChannelRelTable.INSTANCE.classPK)
			).where(
				CommerceChannelRelTable.INSTANCE.classNameId.eq(
					_classNameLocalService.getClassNameId(
						Country.class.getName())
				).and(
					CommerceChannelRelTable.INSTANCE.commerceChannelId.eq(
						commerceChannelId)
				).and(
					() -> {
						if (Validator.isNull(name)) {
							return null;
						}

						return DSLFunctionFactoryUtil.lower(
							CountryTable.INSTANCE.name
						).like(
							StringPool.PERCENT + StringUtil.toLowerCase(name) +
								StringPool.PERCENT
						);
					}
				)
			).limit(
				start, end
			));
	}

	@Override
	public int getCountryCommerceChannelRelsCount(
		long commerceChannelId, String name) {

		return dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				CommerceChannelRelTable.INSTANCE
			).leftJoinOn(
				CountryTable.INSTANCE,
				CountryTable.INSTANCE.countryId.eq(
					CommerceChannelRelTable.INSTANCE.classPK)
			).where(
				CommerceChannelRelTable.INSTANCE.commerceChannelId.eq(
					commerceChannelId
				).and(
					CommerceChannelRelTable.INSTANCE.classNameId.eq(
						_classNameLocalService.getClassNameId(
							Country.class.getName()))
				).and(
					() -> {
						if (Validator.isNull(name)) {
							return null;
						}

						return DSLFunctionFactoryUtil.lower(
							CountryTable.INSTANCE.name
						).like(
							StringPool.PERCENT + StringUtil.toLowerCase(name) +
								StringPool.PERCENT
						);
					}
				)
			));
	}

	private void _reindexCommerceChannelRels(long classNameId, long classPK) {
		try {
			if (classNameId == _classNameLocalService.getClassNameId(
					CommerceCurrency.class)) {

				Indexer<CommerceCurrency> indexer =
					IndexerRegistryUtil.nullSafeGetIndexer(
						CommerceCurrency.class);

				indexer.reindex(CommerceCurrency.class.getName(), classPK);
			}
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CountryLocalService _countryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}