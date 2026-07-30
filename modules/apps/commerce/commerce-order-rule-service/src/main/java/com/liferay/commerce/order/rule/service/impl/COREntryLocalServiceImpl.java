/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.service.impl;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.order.rule.exception.COREntryDisplayDateException;
import com.liferay.commerce.order.rule.exception.COREntryExpirationDateException;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.model.COREntryRelTable;
import com.liferay.commerce.order.rule.model.COREntryTable;
import com.liferay.commerce.order.rule.service.COREntryRelLocalService;
import com.liferay.commerce.order.rule.service.base.COREntryLocalServiceBaseImpl;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;

import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.order.rule.model.COREntry",
	service = AopService.class
)
public class COREntryLocalServiceImpl extends COREntryLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public COREntry addCOREntry(
			String externalReferenceCode, long userId, boolean active,
			String description, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire, String name,
			int priority, String type, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException {

		COREntry corEntry = corEntryPersistence.create(
			counterLocalService.increment());

		corEntry.setExternalReferenceCode(externalReferenceCode);

		User user = _userLocalService.getUser(userId);

		corEntry.setCompanyId(user.getCompanyId());
		corEntry.setUserId(user.getUserId());
		corEntry.setUserName(user.getFullName());

		corEntry.setActive(active);
		corEntry.setDescription(description);
		corEntry.setDisplayDate(
			_portal.getDate(
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, user.getTimeZone(),
				COREntryDisplayDateException.class));

		Date expirationDate = null;

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				COREntryExpirationDateException.class);
		}

		corEntry.setExpirationDate(expirationDate);
		corEntry.setName(name);
		corEntry.setPriority(priority);
		corEntry.setType(type);
		corEntry.setTypeSettingsUnicodeProperties(
			UnicodePropertiesBuilder.fastLoad(
				typeSettings
			).build());

		Date date = new Date();

		if ((expirationDate == null) || expirationDate.after(date)) {
			corEntry.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			corEntry.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		corEntry.setStatusByUserId(user.getUserId());
		corEntry.setStatusDate(serviceContext.getModifiedDate(date));

		corEntry = corEntryPersistence.update(corEntry);

		_resourceLocalService.addModelResources(corEntry, serviceContext);

		return _startWorkflowInstance(
			user.getUserId(), corEntry, serviceContext);
	}

	@Override
	public void checkCOREntries() throws PortalException {
		_checkCOREntriesByDisplayDate();
		_checkCOREntriesByExpirationDate();
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public COREntry deleteCOREntry(COREntry corEntry) throws PortalException {
		corEntryPersistence.remove(corEntry);

		_resourceLocalService.deleteResource(
			corEntry.getCompanyId(), COREntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, corEntry.getCOREntryId());

		_corEntryRelLocalService.deleteCOREntryRels(corEntry.getCOREntryId());

		_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
			corEntry.getCompanyId(), 0L, COREntry.class.getName(),
			corEntry.getCOREntryId());

		return corEntry;
	}

	@Override
	public COREntry deleteCOREntry(long corEntryId) throws PortalException {
		COREntry corEntry = corEntryPersistence.findByPrimaryKey(corEntryId);

		return corEntryLocalService.deleteCOREntry(corEntry);
	}

	@Override
	public List<COREntry>
		getAccountEntryAndCommerceChannelAndCommerceOrderTypeCOREntries(
			long companyId, long accountEntryId, long commerceChannelId,
			long commerceOrderTypeId) {

		return dslQuery(
			_getGroupByStep(
				accountEntryId, null, companyId, commerceChannelId,
				commerceOrderTypeId,
				DSLQueryFactoryUtil.select(COREntryTable.INSTANCE)
			).orderBy(
				COREntryTable.INSTANCE.type.descending(),
				COREntryTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<COREntry> getAccountEntryAndCommerceChannelCOREntries(
		long companyId, long accountEntryId, long commerceChannelId) {

		return dslQuery(
			_getGroupByStep(
				accountEntryId, null, companyId, commerceChannelId, null,
				DSLQueryFactoryUtil.select(COREntryTable.INSTANCE)
			).orderBy(
				COREntryTable.INSTANCE.type.descending(),
				COREntryTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<COREntry> getAccountEntryAndCommerceOrderTypeCOREntries(
		long companyId, long accountEntryId, long commerceOrderTypeId) {

		return dslQuery(
			_getGroupByStep(
				accountEntryId, null, companyId, null, commerceOrderTypeId,
				DSLQueryFactoryUtil.select(COREntryTable.INSTANCE)
			).orderBy(
				COREntryTable.INSTANCE.type.descending(),
				COREntryTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<COREntry> getAccountEntryCOREntries(
		long companyId, long accountEntryId) {

		return dslQuery(
			_getGroupByStep(
				accountEntryId, null, companyId, null, null,
				DSLQueryFactoryUtil.select(COREntryTable.INSTANCE)
			).orderBy(
				COREntryTable.INSTANCE.type.descending(),
				COREntryTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<COREntry>
		getAccountGroupsAndCommerceChannelAndCommerceOrderTypeCOREntries(
			long companyId, long[] accountGroupIds, long commerceChannelId,
			long commerceOrderTypeId) {

		return dslQuery(
			_getGroupByStep(
				null, accountGroupIds, companyId, commerceChannelId,
				commerceOrderTypeId,
				DSLQueryFactoryUtil.select(COREntryTable.INSTANCE)
			).orderBy(
				COREntryTable.INSTANCE.type.descending(),
				COREntryTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<COREntry> getAccountGroupsAndCommerceChannelCOREntries(
		long companyId, long[] accountGroupIds, long commerceChannelId) {

		return dslQuery(
			_getGroupByStep(
				null, accountGroupIds, companyId, commerceChannelId, null,
				DSLQueryFactoryUtil.select(COREntryTable.INSTANCE)
			).orderBy(
				COREntryTable.INSTANCE.type.descending(),
				COREntryTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<COREntry> getAccountGroupsAndCommerceOrderTypeCOREntries(
		long companyId, long[] accountGroupIds, long commerceOrderTypeId) {

		return dslQuery(
			_getGroupByStep(
				null, accountGroupIds, companyId, null, commerceOrderTypeId,
				DSLQueryFactoryUtil.select(COREntryTable.INSTANCE)
			).orderBy(
				COREntryTable.INSTANCE.type.descending(),
				COREntryTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<COREntry> getAccountGroupsCOREntries(
		long companyId, long[] accountGroupIds) {

		return dslQuery(
			_getGroupByStep(
				null, accountGroupIds, companyId, null, null,
				DSLQueryFactoryUtil.select(COREntryTable.INSTANCE)
			).orderBy(
				COREntryTable.INSTANCE.type.descending(),
				COREntryTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<COREntry> getCommerceChannelAndCommerceOrderTypeCOREntries(
		long companyId, long commerceChannelId, long commerceOrderTypeId) {

		return dslQuery(
			_getGroupByStep(
				null, null, companyId, commerceChannelId, commerceOrderTypeId,
				DSLQueryFactoryUtil.select(COREntryTable.INSTANCE)
			).orderBy(
				COREntryTable.INSTANCE.type.descending(),
				COREntryTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<COREntry> getCommerceChannelCOREntries(
		long companyId, long commerceChannelId) {

		return dslQuery(
			_getGroupByStep(
				null, null, companyId, commerceChannelId, null,
				DSLQueryFactoryUtil.select(COREntryTable.INSTANCE)
			).orderBy(
				COREntryTable.INSTANCE.type.descending(),
				COREntryTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<COREntry> getCommerceOrderTypeCOREntries(
		long companyId, long commerceOrderTypeId) {

		return dslQuery(
			_getGroupByStep(
				null, null, companyId, null, commerceOrderTypeId,
				DSLQueryFactoryUtil.select(COREntryTable.INSTANCE)
			).orderBy(
				COREntryTable.INSTANCE.type.descending(),
				COREntryTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<COREntry> getCOREntries(
		long companyId, boolean active, int start, int end) {

		return corEntryPersistence.findByC_A(companyId, active, start, end);
	}

	@Override
	public List<COREntry> getCOREntries(
		long companyId, boolean active, String type, int start, int end) {

		return corEntryPersistence.findByC_A_LikeType(
			companyId, active, type, start, end);
	}

	@Override
	public List<COREntry> getCOREntries(
		long companyId, String type, int start, int end) {

		return corEntryPersistence.findByC_LikeType(
			companyId, type, start, end);
	}

	@Override
	public List<COREntry> getUnqualifiedCOREntries(long companyId) {
		return dslQuery(
			_getGroupByStep(
				null, null, companyId, null, null,
				DSLQueryFactoryUtil.select(COREntryTable.INSTANCE)
			).orderBy(
				COREntryTable.INSTANCE.type.descending(),
				COREntryTable.INSTANCE.priority.descending()
			));
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public COREntry updateCOREntry(
			long userId, long corEntryId, boolean active, String description,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, String name, int priority, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException {

		COREntry corEntry = corEntryPersistence.findByPrimaryKey(corEntryId);

		corEntry.setActive(active);
		corEntry.setDescription(description);

		User user = _userLocalService.getUser(userId);

		corEntry.setDisplayDate(
			_portal.getDate(
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, user.getTimeZone(),
				COREntryDisplayDateException.class));

		Date expirationDate = null;

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				COREntryExpirationDateException.class);
		}

		corEntry.setExpirationDate(expirationDate);
		corEntry.setName(name);
		corEntry.setPriority(priority);
		corEntry.setTypeSettingsUnicodeProperties(
			UnicodePropertiesBuilder.fastLoad(
				typeSettings
			).build());

		Date date = new Date();

		if ((expirationDate == null) || expirationDate.after(date)) {
			corEntry.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			corEntry.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		corEntry.setStatusByUserId(user.getUserId());
		corEntry.setStatusDate(serviceContext.getModifiedDate(date));
		corEntry.setExpandoBridgeAttributes(serviceContext);

		corEntry = corEntryPersistence.update(corEntry);

		return _startWorkflowInstance(
			user.getUserId(), corEntry, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public COREntry updateCOREntryExternalReferenceCode(
			String externalReferenceCode, long corEntryId)
		throws PortalException {

		COREntry corEntry = corEntryPersistence.findByPrimaryKey(corEntryId);

		corEntry.setExternalReferenceCode(externalReferenceCode);

		return corEntryPersistence.update(corEntry);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public COREntry updateCOREntryTypeSettings(
			long corEntryId, String typeSettings)
		throws PortalException {

		COREntry corEntry = corEntryPersistence.findByPrimaryKey(corEntryId);

		corEntry.setTypeSettingsUnicodeProperties(
			UnicodePropertiesBuilder.fastLoad(
				typeSettings
			).build());

		return corEntryPersistence.update(corEntry);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public COREntry updateStatus(
			long userId, long corEntryId, int status,
			ServiceContext serviceContext)
		throws PortalException {

		COREntry corEntry = corEntryPersistence.findByPrimaryKey(corEntryId);

		Date date = new Date();

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(corEntry.getDisplayDate() != null) &&
			date.before(corEntry.getDisplayDate())) {

			status = WorkflowConstants.STATUS_SCHEDULED;
		}

		if (status == WorkflowConstants.STATUS_APPROVED) {
			Date expirationDate = corEntry.getExpirationDate();

			if ((expirationDate != null) && expirationDate.before(date)) {
				corEntry.setExpirationDate(null);
			}
		}

		if (status == WorkflowConstants.STATUS_EXPIRED) {
			corEntry.setActive(false);
			corEntry.setExpirationDate(date);
		}

		corEntry.setStatus(status);

		User user = _userLocalService.getUser(userId);

		corEntry.setStatusByUserId(user.getUserId());
		corEntry.setStatusByUserName(user.getFullName());

		corEntry.setStatusDate(serviceContext.getModifiedDate(date));

		return corEntryPersistence.update(corEntry);
	}

	private void _checkCOREntriesByDisplayDate() throws PortalException {
		List<COREntry> corEntries = corEntryPersistence.findByLtD_S(
			new Date(), WorkflowConstants.STATUS_SCHEDULED);

		for (COREntry corEntry : corEntries) {
			long userId = _portal.getValidUserId(
				corEntry.getCompanyId(), corEntry.getUserId());

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCommand(Constants.UPDATE);

			corEntryLocalService.updateStatus(
				userId, corEntry.getCOREntryId(),
				WorkflowConstants.STATUS_APPROVED, serviceContext);
		}
	}

	private void _checkCOREntriesByExpirationDate() throws PortalException {
		List<COREntry> corEntries = corEntryPersistence.findByLtE_S(
			new Date(), WorkflowConstants.STATUS_APPROVED);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Expiring " + corEntries.size() +
					" commerce order rule entries");
		}

		for (COREntry corEntry : corEntries) {
			long userId = _portal.getValidUserId(
				corEntry.getCompanyId(), corEntry.getUserId());

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCommand(Constants.UPDATE);

			corEntryLocalService.updateStatus(
				userId, corEntry.getCOREntryId(),
				WorkflowConstants.STATUS_EXPIRED, serviceContext);
		}
	}

	private GroupByStep _getGroupByStep(
		Long accountEntryId, long[] accountGroupIds, Long companyId,
		Long commerceChannelId, Long commerceOrderTypeId, FromStep fromStep) {

		COREntryRelTable accountEntryCOREntryRel = COREntryRelTable.INSTANCE.as(
			"accountEntryCOREntryRel");
		COREntryRelTable accountGroupCOREntryRel = COREntryRelTable.INSTANCE.as(
			"accountGroupCOREntryRel");
		COREntryRelTable commerceChannelCOREntryRel =
			COREntryRelTable.INSTANCE.as("commerceChannelCOREntryRel");
		COREntryRelTable commerceOrderTypeCOREntryRel =
			COREntryRelTable.INSTANCE.as("commerceOrderTypeCOREntryRel");

		JoinStep joinStep = fromStep.from(
			COREntryTable.INSTANCE
		).leftJoinOn(
			accountEntryCOREntryRel,
			_getPredicate(
				AccountEntry.class.getName(),
				accountEntryCOREntryRel.classNameId,
				accountEntryCOREntryRel.COREntryId)
		).leftJoinOn(
			accountGroupCOREntryRel,
			_getPredicate(
				AccountGroup.class.getName(),
				accountGroupCOREntryRel.classNameId,
				accountGroupCOREntryRel.COREntryId)
		).leftJoinOn(
			commerceChannelCOREntryRel,
			_getPredicate(
				CommerceChannel.class.getName(),
				commerceChannelCOREntryRel.classNameId,
				commerceChannelCOREntryRel.COREntryId)
		).leftJoinOn(
			commerceOrderTypeCOREntryRel,
			_getPredicate(
				CommerceOrderType.class.getName(),
				commerceOrderTypeCOREntryRel.classNameId,
				commerceOrderTypeCOREntryRel.COREntryId)
		);

		Predicate predicate = COREntryTable.INSTANCE.status.eq(
			WorkflowConstants.STATUS_APPROVED
		).and(
			COREntryTable.INSTANCE.companyId.eq(companyId)
		).and(
			COREntryTable.INSTANCE.active.eq(true)
		).and(
			() -> {
				if (accountEntryId != null) {
					return accountEntryCOREntryRel.classPK.eq(accountEntryId);
				}

				return accountEntryCOREntryRel.COREntryId.isNull();
			}
		);

		if (accountGroupIds != null) {
			if (accountGroupIds.length == 0) {
				accountGroupIds = new long[] {0};
			}

			List<Long> accountGroupIdsList = TransformUtil.transformToList(
				accountGroupIds, Long::valueOf);

			predicate = predicate.and(
				accountGroupCOREntryRel.classPK.in(
					accountGroupIdsList.toArray(new Long[0])));
		}
		else {
			predicate = predicate.and(
				accountGroupCOREntryRel.COREntryId.isNull());
		}

		return joinStep.where(
			predicate.and(
				() -> {
					if (commerceChannelId != null) {
						return commerceChannelCOREntryRel.classPK.eq(
							commerceChannelId);
					}

					return commerceChannelCOREntryRel.COREntryId.isNull();
				}
			).and(
				() -> {
					if (commerceOrderTypeId != null) {
						return commerceOrderTypeCOREntryRel.classPK.eq(
							commerceOrderTypeId);
					}

					return commerceOrderTypeCOREntryRel.COREntryId.isNull();
				}
			));
	}

	private Predicate _getPredicate(
		String className, Column<COREntryRelTable, Long> classNameIdColumn,
		Column<COREntryRelTable, Long> corEntryIdColumn) {

		return classNameIdColumn.eq(
			_classNameLocalService.getClassNameId(className)
		).and(
			corEntryIdColumn.eq(COREntryTable.INSTANCE.COREntryId)
		);
	}

	private COREntry _startWorkflowInstance(
			long userId, COREntry corEntry, ServiceContext serviceContext)
		throws PortalException {

		Map<String, Serializable> workflowContext = new HashMap<>();

		return WorkflowHandlerRegistryUtil.startWorkflowInstance(
			corEntry.getCompanyId(), 0L, userId, COREntry.class.getName(),
			corEntry.getCOREntryId(), corEntry, serviceContext,
			workflowContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		COREntryLocalServiceImpl.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private COREntryRelLocalService _corEntryRelLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}