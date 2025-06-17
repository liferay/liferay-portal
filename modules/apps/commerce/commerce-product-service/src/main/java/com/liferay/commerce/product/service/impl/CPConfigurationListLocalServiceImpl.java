/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.commerce.constants.CPDefinitionInventoryConstants;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.price.list.exception.CommercePriceListDisplayDateException;
import com.liferay.commerce.price.list.exception.CommercePriceListExpirationDateException;
import com.liferay.commerce.product.constants.CPConfigurationEntrySettingConstants;
import com.liferay.commerce.product.exception.CPConfigurationListParentCPConfigurationListGroupIdException;
import com.liferay.commerce.product.exception.DuplicateCPConfigurationListException;
import com.liferay.commerce.product.exception.NoSuchCPConfigurationListException;
import com.liferay.commerce.product.exception.RequiredCPConfigurationListException;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationEntrySetting;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPConfigurationListRelTable;
import com.liferay.commerce.product.model.CPConfigurationListTable;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceChannelRelTable;
import com.liferay.commerce.product.service.CPConfigurationEntryLocalService;
import com.liferay.commerce.product.service.CPConfigurationEntrySettingLocalService;
import com.liferay.commerce.product.service.base.CPConfigurationListLocalServiceBaseImpl;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.math.BigDecimal;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CPConfigurationList",
	service = AopService.class
)
public class CPConfigurationListLocalServiceImpl
	extends CPConfigurationListLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPConfigurationList addCPConfigurationList(
			String externalReferenceCode, long userId, long groupId,
			long parentCPConfigurationListId, boolean master, String name,
			double priority, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		_validate(groupId, 0, master, parentCPConfigurationListId);

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			CommercePriceListDisplayDateException.class);

		Date expirationDate = null;

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				CommercePriceListExpirationDateException.class);
		}

		CPConfigurationList cpConfigurationList =
			cpConfigurationListPersistence.create(
				counterLocalService.increment());

		cpConfigurationList.setExternalReferenceCode(externalReferenceCode);
		cpConfigurationList.setGroupId(groupId);
		cpConfigurationList.setCompanyId(user.getCompanyId());
		cpConfigurationList.setUserId(user.getUserId());
		cpConfigurationList.setUserName(user.getFullName());
		cpConfigurationList.setParentCPConfigurationListId(
			parentCPConfigurationListId);
		cpConfigurationList.setMaster(master);
		cpConfigurationList.setName(name);
		cpConfigurationList.setPriority(priority);
		cpConfigurationList.setDisplayDate(displayDate);
		cpConfigurationList.setExpirationDate(expirationDate);

		cpConfigurationList = cpConfigurationListPersistence.update(
			cpConfigurationList);

		if (parentCPConfigurationListId > 0) {
			Set<Long> classPKs = new HashSet<>();
			Indexer<CPConfigurationEntry> cpConfigurationEntryIndexer =
				IndexerRegistryUtil.nullSafeGetIndexer(
					CPConfigurationEntry.class);
			Indexer<CPDefinition> cpDefinitionIndexer =
				IndexerRegistryUtil.nullSafeGetIndexer(CPDefinition.class);

			while (parentCPConfigurationListId > 0) {
				for (CPConfigurationEntry cpConfigurationEntry :
						_cpConfigurationEntryLocalService.
							getCPConfigurationEntries(
								parentCPConfigurationListId)) {

					if (Objects.equals(
							cpConfigurationEntry.getClassName(),
							CPConfigurationList.class.getName()) ||
						classPKs.contains(cpConfigurationEntry.getClassPK())) {

						continue;
					}

					CPConfigurationEntrySetting cpConfigurationEntrySetting =
						_cpConfigurationEntrySettingLocalService.
							fetchCPConfigurationEntrySetting(
								cpConfigurationEntry.
									getCPConfigurationEntryId(),
								CPConfigurationEntrySettingConstants.
									TYPE_INDEX_IDS);

					if (Validator.isNull(
							cpConfigurationEntrySetting.getValue())) {

						cpConfigurationEntrySetting.setValue(
							String.valueOf(
								cpConfigurationList.
									getCPConfigurationListId()));
					}
					else {
						cpConfigurationEntrySetting.setValue(
							StringBundler.concat(
								cpConfigurationEntrySetting.getValue(),
								StringPool.COMMA,
								cpConfigurationList.
									getCPConfigurationListId()));
					}

					_cpConfigurationEntrySettingLocalService.
						updateCPConfigurationEntrySetting(
							cpConfigurationEntrySetting);

					classPKs.add(cpConfigurationEntry.getClassPK());

					cpConfigurationEntryIndexer.reindex(
						CPConfigurationEntry.class.getName(),
						cpConfigurationEntry.getCPConfigurationEntryId());

					cpDefinitionIndexer.reindex(
						CPDefinition.class.getName(),
						cpConfigurationEntry.getClassPK());
				}

				CPConfigurationList parentCPConfigurationList =
					cpConfigurationListLocalService.getCPConfigurationList(
						parentCPConfigurationListId);

				parentCPConfigurationListId =
					parentCPConfigurationList.getParentCPConfigurationListId();
			}
		}
		else if (master) {
			_cpConfigurationEntryLocalService.addCPConfigurationEntry(
				null, userId, groupId,
				_portal.getClassNameId(CPConfigurationList.class),
				cpConfigurationList.getCPConfigurationListId(),
				cpConfigurationList.getCPConfigurationListId(), 0,
				StringPool.BLANK, true, 0, "default", 0, false, false, false, 0,
				StringPool.BLANK,
				CPDefinitionInventoryConstants.DEFAULT_MAX_ORDER_QUANTITY,
				CPDefinitionInventoryConstants.DEFAULT_MIN_ORDER_QUANTITY,
				BigDecimal.ZERO,
				CPDefinitionInventoryConstants.DEFAULT_MULTIPLE_ORDER_QUANTITY,
				true, true, 0, false, false, true, 0, 0);
		}

		return cpConfigurationList;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPConfigurationList addOrUpdateCPConfigurationList(
			String externalReferenceCode, long companyId, long userId,
			long groupId, long parentCPConfigurationListId, boolean master,
			String name, double priority, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire)
		throws PortalException {

		if (Validator.isNotNull(externalReferenceCode)) {
			CPConfigurationList cpConfigurationList =
				cpConfigurationListPersistence.fetchByERC_C(
					externalReferenceCode, companyId);

			if (cpConfigurationList != null) {
				return cpConfigurationListLocalService.
					updateCPConfigurationList(
						externalReferenceCode,
						cpConfigurationList.getCPConfigurationListId(), groupId,
						userId, parentCPConfigurationListId, master, name,
						priority, displayDateMonth, displayDateDay,
						displayDateYear, displayDateHour, displayDateMinute,
						expirationDateMonth, expirationDateDay,
						expirationDateYear, expirationDateHour,
						expirationDateMinute, neverExpire);
			}
		}

		return cpConfigurationListLocalService.addCPConfigurationList(
			externalReferenceCode, userId, groupId, parentCPConfigurationListId,
			master, name, priority, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire);
	}

	@Override
	public CPConfigurationList deleteCPConfigurationList(
			CPConfigurationList cpConfigurationList)
		throws PortalException {

		return cpConfigurationListLocalService.deleteCPConfigurationList(
			cpConfigurationList, false);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPConfigurationList deleteCPConfigurationList(
			CPConfigurationList cpConfigurationList, boolean force)
		throws PortalException {

		if (cpConfigurationList.isMaster() && !force) {
			throw new RequiredCPConfigurationListException();
		}

		_cpConfigurationEntryLocalService.deleteCPConfigurationEntries(
			cpConfigurationList.getCPConfigurationListId());

		return super.deleteCPConfigurationList(cpConfigurationList);
	}

	@Override
	public CPConfigurationList deleteCPConfigurationList(
			long cpConfigurationListId)
		throws PortalException {

		return cpConfigurationListLocalService.deleteCPConfigurationList(
			cpConfigurationListId, false);
	}

	@Override
	public CPConfigurationList deleteCPConfigurationList(
			long cpConfigurationListId, boolean force)
		throws PortalException {

		return cpConfigurationListLocalService.deleteCPConfigurationList(
			cpConfigurationListPersistence.findByPrimaryKey(
				cpConfigurationListId),
			force);
	}

	@Override
	public void deleteCPConfigurationLists(long companyId)
		throws PortalException {

		List<CPConfigurationList> cpConfigurationLists =
			cpConfigurationListPersistence.findByCompanyId(companyId);

		for (CPConfigurationList cpConfigurationList : cpConfigurationLists) {
			cpConfigurationListLocalService.deleteCPConfigurationList(
				cpConfigurationList, true);
		}
	}

	@Override
	public List<CPConfigurationList> getCPConfigurationLists(
		long groupId, long companyId) {

		return cpConfigurationListPersistence.findByG_C(groupId, companyId);
	}

	@Override
	public List<CPConfigurationList> getCPConfigurationLists(
		long companyId, long groupId, long accountEntryId,
		long[] accountGroupIds, long commerceChannelId,
		long commerceOrderTypeId) {

		return dslQuery(
			_getGroupByStep(
				companyId, groupId, accountEntryId, accountGroupIds,
				commerceChannelId, commerceOrderTypeId,
				DSLQueryFactoryUtil.select(CPConfigurationListTable.INSTANCE)
			).orderBy(
				CPConfigurationListTable.INSTANCE.priority.ascending()
			));
	}

	@Override
	public CPConfigurationList getMasterCPConfigurationList(long groupId)
		throws NoSuchCPConfigurationListException {

		return cpConfigurationListPersistence.findByG_M(groupId, true);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPConfigurationList updateCPConfigurationList(
			String externalReferenceCode, long cpConfigurationListId,
			long userId, long groupId, long parentCPConfigurationListId,
			boolean master, String name, double priority, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		_validate(
			groupId, cpConfigurationListId, master,
			parentCPConfigurationListId);

		Date expirationDate = null;

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			CommercePriceListDisplayDateException.class);

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				CommercePriceListExpirationDateException.class);
		}

		CPConfigurationList cpConfigurationList =
			cpConfigurationListPersistence.findByPrimaryKey(
				cpConfigurationListId);

		cpConfigurationList.setExternalReferenceCode(externalReferenceCode);
		cpConfigurationList.setGroupId(groupId);
		cpConfigurationList.setParentCPConfigurationListId(
			parentCPConfigurationListId);
		cpConfigurationList.setMaster(master);
		cpConfigurationList.setName(name);
		cpConfigurationList.setPriority(priority);
		cpConfigurationList.setDisplayDate(displayDate);
		cpConfigurationList.setExpirationDate(expirationDate);

		return cpConfigurationListPersistence.update(cpConfigurationList);
	}

	private GroupByStep _getGroupByStep(
		long companyId, long groupId, Long accountEntryId,
		long[] accountGroupIds, Long commerceChannelId,
		Long commerceOrderTypeId, FromStep fromStep) {

		CPConfigurationListRelTable accountEntryCPConfigurationListRel =
			CPConfigurationListRelTable.INSTANCE.as(
				"accountEntryCPConfigurationListRel");
		CPConfigurationListRelTable accountGroupCPConfigurationListRel =
			CPConfigurationListRelTable.INSTANCE.as(
				"accountGroupCPConfigurationListRel");
		CPConfigurationListRelTable commerceOrderTypeCPConfigurationListRel =
			CPConfigurationListRelTable.INSTANCE.as(
				"commerceOrderTypeCPConfigurationListRel");

		JoinStep joinStep = fromStep.from(
			CPConfigurationListTable.INSTANCE
		).leftJoinOn(
			accountEntryCPConfigurationListRel,
			_getPredicate(
				AccountEntry.class.getName(),
				accountEntryCPConfigurationListRel.classNameId,
				accountEntryCPConfigurationListRel.CPConfigurationListId)
		).leftJoinOn(
			accountGroupCPConfigurationListRel,
			_getPredicate(
				AccountGroup.class.getName(),
				accountGroupCPConfigurationListRel.classNameId,
				accountGroupCPConfigurationListRel.CPConfigurationListId)
		).leftJoinOn(
			CommerceChannelRelTable.INSTANCE,
			CommerceChannelRelTable.INSTANCE.classPK.eq(
				CPConfigurationListTable.INSTANCE.CPConfigurationListId
			).and(
				CommerceChannelRelTable.INSTANCE.classNameId.eq(
					_classNameLocalService.getClassNameId(
						CPConfigurationList.class))
			)
		).leftJoinOn(
			commerceOrderTypeCPConfigurationListRel,
			_getPredicate(
				CommerceOrderType.class.getName(),
				commerceOrderTypeCPConfigurationListRel.classNameId,
				commerceOrderTypeCPConfigurationListRel.CPConfigurationListId)
		);

		Predicate predicate = CPConfigurationListTable.INSTANCE.status.eq(
			WorkflowConstants.STATUS_APPROVED
		).and(
			CPConfigurationListTable.INSTANCE.companyId.eq(companyId)
		).and(
			CPConfigurationListTable.INSTANCE.groupId.eq(groupId)
		).and(
			CPConfigurationListTable.INSTANCE.master.eq(false)
		).and(
			() -> {
				if (accountEntryId != null) {
					return accountEntryCPConfigurationListRel.classPK.eq(
						accountEntryId
					).or(
						accountEntryCPConfigurationListRel.
							CPConfigurationListId.isNull()
					).withParentheses();
				}

				return accountEntryCPConfigurationListRel.CPConfigurationListId.
					isNull();
			}
		);

		if (accountGroupIds != null) {
			if (accountGroupIds.length == 0) {
				accountGroupIds = new long[] {0};
			}

			List<Long> accountGroupIdsList = TransformUtil.transformToList(
				accountGroupIds, Long::valueOf);

			predicate = predicate.and(
				accountGroupCPConfigurationListRel.classPK.in(
					accountGroupIdsList.toArray(new Long[0])
				).or(
					accountGroupCPConfigurationListRel.CPConfigurationListId.
						isNull()
				).withParentheses());
		}
		else {
			predicate = predicate.and(
				accountGroupCPConfigurationListRel.CPConfigurationListId.
					isNull());
		}

		return joinStep.where(
			predicate.and(
				() -> {
					if (commerceChannelId != null) {
						return CommerceChannelRelTable.INSTANCE.
							commerceChannelId.eq(
								commerceChannelId
							).or(
								CommerceChannelRelTable.INSTANCE.classPK.
									isNull()
							).withParentheses();
					}

					return CommerceChannelRelTable.INSTANCE.classPK.isNull();
				}
			).and(
				() -> {
					if (commerceOrderTypeId != null) {
						return commerceOrderTypeCPConfigurationListRel.classPK.
							eq(
								commerceOrderTypeId
							).or(
								commerceOrderTypeCPConfigurationListRel.
									CPConfigurationListId.isNull()
							).withParentheses();
					}

					return commerceOrderTypeCPConfigurationListRel.
						CPConfigurationListId.isNull();
				}
			));
	}

	private Predicate _getPredicate(
		String className,
		Column<CPConfigurationListRelTable, Long> classNameIdColumn,
		Column<CPConfigurationListRelTable, Long> cpConfigurationListIdColumn) {

		return classNameIdColumn.eq(
			_classNameLocalService.getClassNameId(className)
		).and(
			cpConfigurationListIdColumn.eq(
				CPConfigurationListTable.INSTANCE.CPConfigurationListId)
		);
	}

	private void _validate(
			long groupId, long cpConfigurationListId,
			boolean masterConfigurationList, long parentCPConfigurationListId)
		throws PortalException {

		if (masterConfigurationList) {
			CPConfigurationList cpConfigurationList =
				cpConfigurationListPersistence.fetchByG_M(
					groupId, masterConfigurationList);

			if ((cpConfigurationList != null) &&
				(cpConfigurationList.getCPConfigurationListId() !=
					cpConfigurationListId)) {

				throw new DuplicateCPConfigurationListException();
			}
		}

		if (parentCPConfigurationListId > 0) {
			if (cpConfigurationListId == parentCPConfigurationListId) {
				throw new CPConfigurationListParentCPConfigurationListGroupIdException();
			}

			CPConfigurationList cpConfigurationList =
				cpConfigurationListLocalService.fetchCPConfigurationList(
					parentCPConfigurationListId);

			if ((cpConfigurationList != null) &&
				(cpConfigurationList.getGroupId() != groupId)) {

				throw new CPConfigurationListParentCPConfigurationListGroupIdException();
			}
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CPConfigurationEntryLocalService _cpConfigurationEntryLocalService;

	@Reference
	private CPConfigurationEntrySettingLocalService
		_cpConfigurationEntrySettingLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}