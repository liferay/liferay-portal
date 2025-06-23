/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.commerce.product.exception.CPDefinitionLinkDisplayDateException;
import com.liferay.commerce.product.exception.CPDefinitionLinkExpirationDateException;
import com.liferay.commerce.product.internal.util.CPDefinitionLocalServiceCircularDependencyUtil;
import com.liferay.commerce.product.links.CPDefinitionLinkType;
import com.liferay.commerce.product.links.CPDefinitionLinkTypeRegistry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.base.CPDefinitionLinkLocalServiceBaseImpl;
import com.liferay.commerce.product.service.persistence.CPDefinitionPersistence;
import com.liferay.commerce.product.service.persistence.CProductPersistence;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CPDefinitionLink",
	service = AopService.class
)
public class CPDefinitionLinkLocalServiceImpl
	extends CPDefinitionLinkLocalServiceBaseImpl {

	@Override
	public CPDefinitionLink addCPDefinitionLinkByCProductId(
			long cpDefinitionId, long cProductId, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, double priority, String type,
			ServiceContext serviceContext)
		throws PortalException {

		CPDefinitionLink cpDefinitionLink = _addCPDefinitionLinkByCProductId(
			cpDefinitionId, cProductId, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire, priority,
			type, serviceContext);

		CPDefinitionLinkType cpDefinitionLinkType =
			_cpDefinitionLinkTypeRegistry.getCPDefinitionLinkType(type);

		if ((cpDefinitionLinkType == null) ||
			!cpDefinitionLinkType.isBiDirectional()) {

			return cpDefinitionLink;
		}

		CProduct cProduct = _cProductPersistence.findByPrimaryKey(cProductId);
		CPDefinition cpDefinition = _cpDefinitionPersistence.findByPrimaryKey(
			cpDefinitionId);

		_addCPDefinitionLinkByCProductId(
			cProduct.getPublishedCPDefinitionId(), cpDefinition.getCProductId(),
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, priority, type, serviceContext);

		return cpDefinitionLink;
	}

	@Override
	public void checkCPDefinitionLinks() throws PortalException {
		_checkCPDefinitionLinksByDisplayDate();
		_checkCPDefinitionLinksByExpirationDate();
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPDefinitionLink deleteCPDefinitionLink(
			CPDefinitionLink cpDefinitionLink)
		throws PortalException {

		_deleteCPDefinitionLink(cpDefinitionLink);

		CPDefinitionLinkType cpDefinitionLinkType =
			_cpDefinitionLinkTypeRegistry.getCPDefinitionLinkType(
				cpDefinitionLink.getType());

		if ((cpDefinitionLink == null) ||
			!cpDefinitionLinkType.isBiDirectional()) {

			return cpDefinitionLink;
		}

		CProduct cProduct = _cProductPersistence.findByPrimaryKey(
			cpDefinitionLink.getCProductId());
		CPDefinition cpDefinition = _cpDefinitionPersistence.findByPrimaryKey(
			cpDefinitionLink.getCPDefinitionId());

		CPDefinitionLink relatedCPDefinitionLink =
			cpDefinitionLinkPersistence.fetchByC_C_T(
				cProduct.getPublishedCPDefinitionId(),
				cpDefinition.getCProductId(), cpDefinitionLink.getType());

		_deleteCPDefinitionLink(relatedCPDefinitionLink);

		return cpDefinitionLink;
	}

	@Override
	public CPDefinitionLink deleteCPDefinitionLink(long cpDefinitionLinkId)
		throws PortalException {

		CPDefinitionLink cpDefinitionLink =
			cpDefinitionLinkPersistence.findByPrimaryKey(cpDefinitionLinkId);

		return cpDefinitionLinkLocalService.deleteCPDefinitionLink(
			cpDefinitionLink);
	}

	@Override
	public void deleteCPDefinitionLinksByCPDefinitionId(long cpDefinitionId)
		throws PortalException {

		List<CPDefinitionLink> cpDefinitionLinks =
			cpDefinitionLinkPersistence.findByCPDefinitionId(cpDefinitionId);

		for (CPDefinitionLink cpDefinitionLink : cpDefinitionLinks) {
			cpDefinitionLinkLocalService.deleteCPDefinitionLink(
				cpDefinitionLink);
		}
	}

	@Override
	public void deleteCPDefinitionLinksByCProductId(long cProductId)
		throws PortalException {

		List<CPDefinitionLink> cpDefinitionLinks =
			cpDefinitionLinkPersistence.findByCProductId(cProductId);

		for (CPDefinitionLink cpDefinitionLink : cpDefinitionLinks) {
			cpDefinitionLinkLocalService.deleteCPDefinitionLink(
				cpDefinitionLink);
		}
	}

	@Override
	public CPDefinitionLink fetchCPDefinitionLink(
		long cpDefinitionId, long cProductId, String type) {

		return cpDefinitionLinkPersistence.fetchByC_C_T(
			cpDefinitionId, cProductId, type);
	}

	@Override
	public List<CPDefinitionLink> getCPDefinitionLinks(long cpDefinitionId) {
		return cpDefinitionLinkPersistence.findByCPDefinitionId(cpDefinitionId);
	}

	@Override
	public List<CPDefinitionLink> getCPDefinitionLinks(
		long cpDefinitionId, int status) {

		return cpDefinitionLinkPersistence.findByCPD_S(cpDefinitionId, status);
	}

	@Override
	public List<CPDefinitionLink> getCPDefinitionLinks(
		long cpDefinitionId, int start, int end) {

		return cpDefinitionLinkPersistence.findByCPDefinitionId(
			cpDefinitionId, start, end);
	}

	@Override
	public List<CPDefinitionLink> getCPDefinitionLinks(
		long cpDefinitionId, int status, int start, int end) {

		return cpDefinitionLinkPersistence.findByCPD_S(
			cpDefinitionId, status, start, end);
	}

	@Override
	public List<CPDefinitionLink> getCPDefinitionLinks(
		long cpDefinitionId, String type) {

		return cpDefinitionLinkPersistence.findByCPD_T(cpDefinitionId, type);
	}

	@Override
	public List<CPDefinitionLink> getCPDefinitionLinks(
		long cpDefinitionId, String type, int status) {

		return cpDefinitionLinkPersistence.findByCPD_T_S(
			cpDefinitionId, type, status);
	}

	@Override
	public List<CPDefinitionLink> getCPDefinitionLinks(
		long cpDefinitionId, String type, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return cpDefinitionLinkPersistence.findByCPD_T_S(
			cpDefinitionId, type, status, start, end, orderByComparator);
	}

	@Override
	public List<CPDefinitionLink> getCPDefinitionLinks(
		long cpDefinitionId, String type, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return cpDefinitionLinkPersistence.findByCPD_T(
			cpDefinitionId, type, start, end, orderByComparator);
	}

	@Override
	public int getCPDefinitionLinksCount(long cpDefinitionId) {
		return cpDefinitionLinkPersistence.countByCPDefinitionId(
			cpDefinitionId);
	}

	@Override
	public int getCPDefinitionLinksCount(long cpDefinitionId, int status) {
		return cpDefinitionLinkPersistence.countByCPD_S(cpDefinitionId, status);
	}

	@Override
	public int getCPDefinitionLinksCount(long cpDefinitionId, String type) {
		return cpDefinitionLinkPersistence.countByCPD_T(cpDefinitionId, type);
	}

	@Override
	public int getCPDefinitionLinksCount(
		long cpDefinitionId, String type, int status) {

		return cpDefinitionLinkPersistence.countByCPD_T_S(
			cpDefinitionId, type, status);
	}

	@Override
	public List<CPDefinitionLink> getReverseCPDefinitionLinks(
		long cProductId, String type) {

		return cpDefinitionLinkPersistence.findByCP_T(cProductId, type);
	}

	@Override
	public List<CPDefinitionLink> getReverseCPDefinitionLinks(
		long cProductId, String type, int status) {

		return cpDefinitionLinkPersistence.findByCP_T_S(
			cProductId, type, status);
	}

	@Override
	public CPDefinitionLink updateCPDefinitionLink(
			long userId, long cpDefinitionLinkId, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, double priority, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		CPDefinitionLink cpDefinitionLink =
			cpDefinitionLinkPersistence.findByPrimaryKey(cpDefinitionLinkId);

		if (CPDefinitionLocalServiceCircularDependencyUtil.isVersionable(
				cpDefinitionLink.getCPDefinitionId())) {

			CPDefinition newCPDefinition =
				CPDefinitionLocalServiceCircularDependencyUtil.copyCPDefinition(
					cpDefinitionLink.getCPDefinitionId());

			cpDefinitionLink = cpDefinitionLinkPersistence.findByC_C_T(
				newCPDefinition.getCPDefinitionId(),
				cpDefinitionLink.getCProductId(), cpDefinitionLink.getType());
		}

		Date expirationDate = null;
		Date date = new Date();

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			CPDefinitionLinkDisplayDateException.class);

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				CPDefinitionLinkExpirationDateException.class);
		}

		if ((expirationDate != null) &&
			(expirationDate.before(date) ||
			 ((displayDate != null) && expirationDate.before(displayDate)))) {

			throw new CPDefinitionLinkExpirationDateException(
				"Expiration date " + expirationDate + " is in the past");
		}

		cpDefinitionLink.setDisplayDate(displayDate);
		cpDefinitionLink.setExpirationDate(expirationDate);

		if ((expirationDate == null) || expirationDate.after(date)) {
			cpDefinitionLink.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			cpDefinitionLink.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		cpDefinitionLink.setPriority(priority);
		cpDefinitionLink.setExpandoBridgeAttributes(serviceContext);

		cpDefinitionLink = cpDefinitionLinkPersistence.update(cpDefinitionLink);

		_reindexCPDefinition(cpDefinitionLink.getCPDefinitionId());

		CProduct cProduct = _cProductPersistence.findByPrimaryKey(
			cpDefinitionLink.getCProductId());

		_reindexCPDefinition(cProduct.getPublishedCPDefinitionId());

		if (serviceContext != null) {
			_updateAsset(cpDefinitionLink, serviceContext);
		}

		return _startWorkflowInstance(
			user.getUserId(), cpDefinitionLink, serviceContext);
	}

	@Override
	public void updateCPDefinitionLinkCProductIds(
			long cpDefinitionId, long[] cProductIds, String type,
			ServiceContext serviceContext)
		throws PortalException {

		if (cProductIds == null) {
			return;
		}

		List<CPDefinitionLink> cpDefinitionLinks = getCPDefinitionLinks(
			cpDefinitionId, type);

		for (CPDefinitionLink cpDefinitionLink : cpDefinitionLinks) {
			if (!ArrayUtil.contains(
					cProductIds, cpDefinitionLink.getCProductId())) {

				cpDefinitionLinkLocalService.deleteCPDefinitionLink(
					cpDefinitionLink);
			}
		}

		CPDefinition cpDefinition = _cpDefinitionPersistence.findByPrimaryKey(
			cpDefinitionId);

		Calendar calendar = new GregorianCalendar();

		calendar.setTime(new Date());

		for (long cProductId : cProductIds) {
			if (cpDefinition.getCProductId() != cProductId) {
				CPDefinitionLink cpDefinitionLink =
					cpDefinitionLinkPersistence.fetchByC_C_T(
						cpDefinitionId, cProductId, type);

				if (cpDefinitionLink == null) {
					cpDefinitionLinkLocalService.
						addCPDefinitionLinkByCProductId(
							cpDefinitionId, cProductId,
							calendar.get(Calendar.MONTH),
							calendar.get(Calendar.DAY_OF_MONTH),
							calendar.get(Calendar.YEAR),
							calendar.get(Calendar.HOUR_OF_DAY),
							calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
							0, type, serviceContext);
				}
			}

			CProduct cProduct = _cProductPersistence.findByPrimaryKey(
				cProductId);

			_reindexCPDefinition(cProduct.getPublishedCPDefinitionId());
		}

		_reindexCPDefinition(cpDefinitionId);
	}

	@Override
	public CPDefinitionLink updateStatus(
			long userId, long cpDefinitionLinkId, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);
		Date date = new Date();

		CPDefinitionLink cpDefinitionLink =
			cpDefinitionLinkPersistence.findByPrimaryKey(cpDefinitionLinkId);

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(cpDefinitionLink.getDisplayDate() != null) &&
			date.before(cpDefinitionLink.getDisplayDate())) {

			status = WorkflowConstants.STATUS_SCHEDULED;
		}

		Date modifiedDate = serviceContext.getModifiedDate(date);

		if (status == WorkflowConstants.STATUS_APPROVED) {
			Date expirationDate = cpDefinitionLink.getExpirationDate();

			if ((expirationDate != null) && expirationDate.before(date)) {
				cpDefinitionLink.setExpirationDate(null);
			}
		}

		if (status == WorkflowConstants.STATUS_EXPIRED) {
			cpDefinitionLink.setExpirationDate(date);
		}

		cpDefinitionLink.setStatus(status);
		cpDefinitionLink.setStatusByUserId(user.getUserId());
		cpDefinitionLink.setStatusByUserName(user.getFullName());
		cpDefinitionLink.setStatusDate(modifiedDate);

		cpDefinitionLink = cpDefinitionLinkPersistence.update(cpDefinitionLink);

		CProduct cProduct = _cProductPersistence.findByPrimaryKey(
			cpDefinitionLink.getCProductId());

		_reindexCPDefinition(cProduct.getPublishedCPDefinitionId());

		_reindexCPDefinition(cpDefinitionLink.getCPDefinitionId());

		return cpDefinitionLink;
	}

	private CPDefinitionLink _addCPDefinitionLinkByCProductId(
			long cpDefinitionId, long cProductId, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, double priority, String type,
			ServiceContext serviceContext)
		throws PortalException {

		CPDefinition cpDefinition;

		if (CPDefinitionLocalServiceCircularDependencyUtil.isVersionable(
				cpDefinitionId)) {

			cpDefinition =
				CPDefinitionLocalServiceCircularDependencyUtil.copyCPDefinition(
					cpDefinitionId);

			cpDefinitionId = cpDefinition.getCPDefinitionId();
		}
		else {
			cpDefinition = _cpDefinitionPersistence.findByPrimaryKey(
				cpDefinitionId);
		}

		User user = _userLocalService.getUser(serviceContext.getUserId());

		Date expirationDate = null;
		Date date = new Date();

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			CPDefinitionLinkDisplayDateException.class);

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				CPDefinitionLinkExpirationDateException.class);
		}

		if ((expirationDate != null) &&
			(expirationDate.before(date) ||
			 ((displayDate != null) && expirationDate.before(displayDate)))) {

			throw new CPDefinitionLinkExpirationDateException(
				"Expiration date " + expirationDate + " is in the past");
		}

		long cpDefinitionLinkId = counterLocalService.increment();

		CPDefinitionLink cpDefinitionLink = cpDefinitionLinkPersistence.create(
			cpDefinitionLinkId);

		cpDefinitionLink.setGroupId(cpDefinition.getGroupId());
		cpDefinitionLink.setCompanyId(user.getCompanyId());
		cpDefinitionLink.setUserId(user.getUserId());
		cpDefinitionLink.setUserName(user.getFullName());
		cpDefinitionLink.setCPDefinitionId(cpDefinition.getCPDefinitionId());
		cpDefinitionLink.setCProductId(cProductId);
		cpDefinitionLink.setDisplayDate(displayDate);
		cpDefinitionLink.setExpirationDate(expirationDate);

		if ((expirationDate == null) || expirationDate.after(date)) {
			cpDefinitionLink.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			cpDefinitionLink.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		cpDefinitionLink.setPriority(priority);
		cpDefinitionLink.setType(type);
		cpDefinitionLink.setExpandoBridgeAttributes(serviceContext);

		cpDefinitionLink = cpDefinitionLinkPersistence.update(cpDefinitionLink);

		CProduct cProduct = _cProductPersistence.findByPrimaryKey(cProductId);

		_reindexCPDefinition(cProduct.getPublishedCPDefinitionId());

		_reindexCPDefinition(cpDefinitionId);

		if (serviceContext != null) {
			_updateAsset(cpDefinitionLink, serviceContext);
		}

		return _startWorkflowInstance(
			user.getUserId(), cpDefinitionLink, serviceContext);
	}

	private void _checkCPDefinitionLinksByDisplayDate() throws PortalException {
		for (CPDefinitionLink cpDefinitionLink :
				cpDefinitionLinkPersistence.findByLtD_S(
					new Date(), WorkflowConstants.STATUS_SCHEDULED)) {

			long userId = _portal.getValidUserId(
				cpDefinitionLink.getCompanyId(), cpDefinitionLink.getUserId());

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCommand(Constants.UPDATE);
			serviceContext.setScopeGroupId(cpDefinitionLink.getGroupId());

			cpDefinitionLinkLocalService.updateStatus(
				userId, cpDefinitionLink.getCPDefinitionLinkId(),
				WorkflowConstants.STATUS_APPROVED, serviceContext,
				new HashMap<String, Serializable>());
		}
	}

	private void _checkCPDefinitionLinksByExpirationDate()
		throws PortalException {

		List<CPDefinitionLink> cpDefinitionLinks =
			cpDefinitionLinkPersistence.findByLtE_S(
				new Date(), WorkflowConstants.STATUS_APPROVED);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Expiring " + cpDefinitionLinks.size() +
					" commerce product definition links");
		}

		for (CPDefinitionLink cpDefinitionLink : cpDefinitionLinks) {
			long userId = _portal.getValidUserId(
				cpDefinitionLink.getCompanyId(), cpDefinitionLink.getUserId());

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCommand(Constants.UPDATE);
			serviceContext.setScopeGroupId(cpDefinitionLink.getGroupId());

			cpDefinitionLinkLocalService.updateStatus(
				userId, cpDefinitionLink.getCPDefinitionLinkId(),
				WorkflowConstants.STATUS_EXPIRED, serviceContext,
				new HashMap<String, Serializable>());
		}
	}

	private CPDefinitionLink _deleteCPDefinitionLink(
			CPDefinitionLink cpDefinitionLink)
		throws PortalException {

		if (CPDefinitionLocalServiceCircularDependencyUtil.isVersionable(
				cpDefinitionLink.getCPDefinitionId())) {

			try {
				CPDefinition newCPDefinition =
					CPDefinitionLocalServiceCircularDependencyUtil.
						copyCPDefinition(cpDefinitionLink.getCPDefinitionId());

				cpDefinitionLink = cpDefinitionLinkPersistence.findByC_C_T(
					newCPDefinition.getCPDefinitionId(),
					cpDefinitionLink.getCProductId(),
					cpDefinitionLink.getType());
			}
			catch (PortalException portalException) {
				throw new SystemException(portalException);
			}
		}

		cpDefinitionLinkPersistence.remove(cpDefinitionLink);

		_assetEntryLocalService.deleteEntry(
			CPDefinitionLink.class.getName(),
			cpDefinitionLink.getCPDefinitionLinkId());

		_expandoRowLocalService.deleteRows(
			cpDefinitionLink.getCPDefinitionLinkId());

		CProduct cProduct = _cProductPersistence.fetchByPrimaryKey(
			cpDefinitionLink.getCProductId());

		if (cProduct != null) {
			_reindexCPDefinition(cProduct.getPublishedCPDefinitionId());
		}

		_reindexCPDefinition(cpDefinitionLink.getCPDefinitionId());

		return cpDefinitionLink;
	}

	private void _reindexCPDefinition(long cpDefinitionId)
		throws PortalException {

		Indexer<CPDefinition> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			CPDefinition.class);

		indexer.reindex(CPDefinition.class.getName(), cpDefinitionId);
	}

	private CPDefinitionLink _startWorkflowInstance(
			long userId, CPDefinitionLink cpDefinitionLink,
			ServiceContext serviceContext)
		throws PortalException {

		Map<String, Serializable> workflowContext = new HashMap<>();

		return WorkflowHandlerRegistryUtil.startWorkflowInstance(
			cpDefinitionLink.getCompanyId(), cpDefinitionLink.getGroupId(),
			userId, CPDefinitionLink.class.getName(),
			cpDefinitionLink.getCPDefinitionLinkId(), cpDefinitionLink,
			serviceContext, workflowContext);
	}

	private void _updateAsset(
			CPDefinitionLink cpDefinitionLink, ServiceContext serviceContext)
		throws PortalException {

		Company company = _companyLocalService.getCompany(
			serviceContext.getCompanyId());

		CProduct cProduct = _cProductPersistence.findByPrimaryKey(
			cpDefinitionLink.getCProductId());

		CPDefinition cpDefinition = _cpDefinitionPersistence.findByPrimaryKey(
			cProduct.getPublishedCPDefinitionId());

		_assetEntryLocalService.updateEntry(
			serviceContext.getUserId(), company.getGroupId(),
			cpDefinitionLink.getCreateDate(),
			cpDefinitionLink.getModifiedDate(),
			CPDefinitionLink.class.getName(),
			cpDefinitionLink.getCPDefinitionLinkId(), null, 0,
			serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(), true, true, null, null, null,
			null, ContentTypes.TEXT_PLAIN, cpDefinition.getNameMapAsXML(),
			cpDefinition.getDescriptionMapAsXML(), null, null, null, 0, 0,
			null);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionLinkLocalServiceImpl.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private CPDefinitionLinkTypeRegistry _cpDefinitionLinkTypeRegistry;

	@Reference
	private CPDefinitionPersistence _cpDefinitionPersistence;

	@Reference
	private CProductPersistence _cProductPersistence;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}