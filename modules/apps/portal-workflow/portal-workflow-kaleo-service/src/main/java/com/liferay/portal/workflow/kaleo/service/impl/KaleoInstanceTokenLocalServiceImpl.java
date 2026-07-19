/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.impl;

import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.workflow.kaleo.constants.KaleoInstanceTokenConstants;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.service.base.KaleoInstanceTokenLocalServiceBaseImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoInstancePersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoInstanceTokenQuery;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoNodePersistence;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken",
	service = AopService.class
)
public class KaleoInstanceTokenLocalServiceImpl
	extends KaleoInstanceTokenLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public KaleoInstanceToken addKaleoInstanceToken(
			long currentKaleoNodeId, long kaleoDefinitionId,
			long kaleoDefinitionVersionId, long kaleoInstanceId,
			long parentKaleoInstanceTokenId,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(
			serviceContext.getGuestOrUserId());
		Date date = new Date();

		long kaleoInstanceTokenId = counterLocalService.increment();

		KaleoInstanceToken kaleoInstanceToken =
			kaleoInstanceTokenPersistence.create(kaleoInstanceTokenId);

		kaleoInstanceToken.setGroupId(
			_staging.getLiveGroupId(serviceContext.getScopeGroupId()));
		kaleoInstanceToken.setCompanyId(user.getCompanyId());
		kaleoInstanceToken.setUserId(user.getUserId());
		kaleoInstanceToken.setUserName(user.getFullName());
		kaleoInstanceToken.setCreateDate(date);
		kaleoInstanceToken.setModifiedDate(date);
		kaleoInstanceToken.setKaleoDefinitionId(kaleoDefinitionId);
		kaleoInstanceToken.setKaleoDefinitionVersionId(
			kaleoDefinitionVersionId);
		kaleoInstanceToken.setKaleoInstanceId(kaleoInstanceId);
		kaleoInstanceToken.setParentKaleoInstanceTokenId(
			parentKaleoInstanceTokenId);

		if (currentKaleoNodeId > 0) {
			_setCurrentKaleoNode(kaleoInstanceToken, currentKaleoNodeId);
		}

		kaleoInstanceToken.setClassName(
			(String)workflowContext.get(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME));

		if (workflowContext.containsKey(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_PK)) {

			kaleoInstanceToken.setClassPK(
				GetterUtil.getLong(
					(String)workflowContext.get(
						WorkflowConstants.CONTEXT_ENTRY_CLASS_PK)));
		}

		kaleoInstanceToken.setCompleted(false);

		return kaleoInstanceTokenPersistence.update(kaleoInstanceToken);
	}

	@Override
	public KaleoInstanceToken addKaleoInstanceToken(
			long parentKaleoInstanceTokenId,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws PortalException {

		KaleoInstanceToken parentKaleoInstanceToken =
			kaleoInstanceTokenPersistence.findByPrimaryKey(
				parentKaleoInstanceTokenId);

		return kaleoInstanceTokenLocalService.addKaleoInstanceToken(
			parentKaleoInstanceToken.getCurrentKaleoNodeId(),
			parentKaleoInstanceToken.getKaleoDefinitionId(),
			parentKaleoInstanceToken.getKaleoDefinitionVersionId(),
			parentKaleoInstanceToken.getKaleoInstanceId(),
			parentKaleoInstanceToken.getKaleoInstanceTokenId(), workflowContext,
			serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public KaleoInstanceToken completeKaleoInstanceToken(
			long kaleoInstanceTokenId)
		throws PortalException {

		KaleoInstanceToken kaleoInstanceToken =
			kaleoInstanceTokenPersistence.findByPrimaryKey(
				kaleoInstanceTokenId);

		kaleoInstanceTokenPersistence.reassociateIfAbsent(kaleoInstanceToken);

		kaleoInstanceToken.setCompleted(true);
		kaleoInstanceToken.setCompletionDate(new Date());

		return kaleoInstanceTokenPersistence.update(kaleoInstanceToken);
	}

	@Override
	public void deleteCompanyKaleoInstanceTokens(long companyId) {
		for (KaleoInstanceToken kaleoInstanceToken :
				kaleoInstanceTokenPersistence.findByCompanyId(companyId)) {

			kaleoInstanceTokenLocalService.deleteKaleoInstanceToken(
				kaleoInstanceToken);
		}
	}

	@Override
	public void deleteKaleoDefinitionVersionKaleoInstanceTokens(
		long kaleoDefinitionVersionId) {

		for (KaleoInstanceToken kaleoInstanceToken :
				kaleoInstanceTokenPersistence.findByKaleoDefinitionVersionId(
					kaleoDefinitionVersionId)) {

			kaleoInstanceTokenLocalService.deleteKaleoInstanceToken(
				kaleoInstanceToken);
		}
	}

	@Override
	public void deleteKaleoInstanceKaleoInstanceTokens(long kaleoInstanceId) {
		for (KaleoInstanceToken kaleoInstanceToken :
				kaleoInstanceTokenPersistence.findByKaleoInstanceId(
					kaleoInstanceId)) {

			kaleoInstanceTokenLocalService.deleteKaleoInstanceToken(
				kaleoInstanceToken);
		}
	}

	@Override
	public List<KaleoInstanceToken> getKaleoInstanceTokens(
		long kaleoInstanceId) {

		return kaleoInstanceTokenPersistence.findByKaleoInstanceId(
			kaleoInstanceId);
	}

	@Override
	public List<KaleoInstanceToken> getKaleoInstanceTokens(
		long parentKaleoInstanceTokenId, Date completionDate,
		ServiceContext serviceContext) {

		return kaleoInstanceTokenPersistence.findByC_PKITI_CD(
			serviceContext.getCompanyId(), parentKaleoInstanceTokenId,
			completionDate);
	}

	@Override
	public List<KaleoInstanceToken> getKaleoInstanceTokens(
		long parentKaleoInstanceTokenId, ServiceContext serviceContext) {

		return kaleoInstanceTokenPersistence.findByC_PKITI(
			serviceContext.getCompanyId(), parentKaleoInstanceTokenId);
	}

	@Override
	public int getKaleoInstanceTokensCount(
		long parentKaleoInstanceTokenId, Date completionDate,
		ServiceContext serviceContext) {

		return kaleoInstanceTokenPersistence.countByC_PKITI_CD(
			serviceContext.getCompanyId(), parentKaleoInstanceTokenId,
			completionDate);
	}

	@Override
	public int getKaleoInstanceTokensCount(
		long parentKaleoInstanceTokenId, ServiceContext serviceContext) {

		return kaleoInstanceTokenPersistence.countByC_PKITI(
			serviceContext.getCompanyId(), parentKaleoInstanceTokenId);
	}

	@Override
	public KaleoInstanceToken getRootKaleoInstanceToken(
			long kaleoInstanceId, Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws PortalException {

		KaleoInstance kaleoInstance =
			_kaleoInstancePersistence.findByPrimaryKey(kaleoInstanceId);

		long rootKaleoInstanceTokenId =
			kaleoInstance.getRootKaleoInstanceTokenId();

		if (rootKaleoInstanceTokenId > 0) {
			return kaleoInstanceTokenPersistence.findByPrimaryKey(
				rootKaleoInstanceTokenId);
		}

		// Kaleo instance token

		KaleoInstanceToken kaleoInstanceToken =
			kaleoInstanceTokenLocalService.addKaleoInstanceToken(
				0, kaleoInstance.getKaleoDefinitionId(),
				kaleoInstance.getKaleoDefinitionVersionId(),
				kaleoInstance.getKaleoInstanceId(),
				KaleoInstanceTokenConstants.
					PARENT_KALEO_INSTANCE_TOKEN_ID_DEFAULT,
				workflowContext, serviceContext);

		// Kaleo instance

		kaleoInstance.setRootKaleoInstanceTokenId(
			kaleoInstanceToken.getKaleoInstanceTokenId());

		_kaleoInstancePersistence.update(kaleoInstance);

		return kaleoInstanceToken;
	}

	@Override
	public Hits search(
		Long userId, String assetClassName, String assetTitle,
		String assetDescription, String currentKaleoNodeName,
		String kaleoDefinitionName, Boolean completed,
		boolean searchByActiveWorkflowHandlers, int start, int end,
		Sort[] sorts, ServiceContext serviceContext) {

		try {
			KaleoInstanceTokenQuery kaleoInstanceTokenQuery =
				new KaleoInstanceTokenQuery(serviceContext);

			kaleoInstanceTokenQuery.setAssetDescription(assetDescription);
			kaleoInstanceTokenQuery.setAssetTitle(assetTitle);
			kaleoInstanceTokenQuery.setClassName(assetClassName);
			kaleoInstanceTokenQuery.setCompleted(completed);
			kaleoInstanceTokenQuery.setCurrentKaleoNodeName(
				currentKaleoNodeName);
			kaleoInstanceTokenQuery.setKaleoDefinitionName(kaleoDefinitionName);
			kaleoInstanceTokenQuery.setSearchByActiveWorkflowHandlers(
				searchByActiveWorkflowHandlers);
			kaleoInstanceTokenQuery.setUserId(userId);

			Indexer<KaleoInstanceToken> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(
					KaleoInstanceToken.class);

			SearchContext searchContext = _buildSearchContext(
				kaleoInstanceTokenQuery, start, end, sorts, serviceContext);

			return indexer.search(searchContext);
		}
		catch (SearchException searchException) {
			throw new SystemException(searchException);
		}
	}

	@Override
	public int searchCount(
		Long userId, String assetClassName, String assetTitle,
		String assetDescription, String currentKaleoNodeName,
		String kaleoDefinitionName, Boolean completed,
		boolean searchByActiveWorkflowHandlers, ServiceContext serviceContext) {

		KaleoInstanceTokenQuery kaleoInstanceTokenQuery =
			new KaleoInstanceTokenQuery(serviceContext);

		kaleoInstanceTokenQuery.setAssetDescription(assetDescription);
		kaleoInstanceTokenQuery.setAssetTitle(assetTitle);
		kaleoInstanceTokenQuery.setClassName(assetClassName);
		kaleoInstanceTokenQuery.setCurrentKaleoNodeName(currentKaleoNodeName);
		kaleoInstanceTokenQuery.setCompleted(completed);
		kaleoInstanceTokenQuery.setKaleoDefinitionName(kaleoDefinitionName);
		kaleoInstanceTokenQuery.setSearchByActiveWorkflowHandlers(
			searchByActiveWorkflowHandlers);
		kaleoInstanceTokenQuery.setUserId(userId);

		try {
			Indexer<KaleoInstanceToken> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(
					KaleoInstanceToken.class);

			SearchContext searchContext = _buildSearchContext(
				kaleoInstanceTokenQuery, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null, serviceContext);

			return (int)indexer.searchCount(searchContext);
		}
		catch (SearchException searchException) {
			throw new SystemException(searchException);
		}
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public KaleoInstanceToken updateKaleoInstanceToken(
			long kaleoInstanceTokenId, long currentKaleoNodeId,
			String currentKaleoNodeName)
		throws PortalException {

		KaleoInstanceToken kaleoInstanceToken =
			kaleoInstanceTokenPersistence.findByPrimaryKey(
				kaleoInstanceTokenId);

		kaleoInstanceTokenPersistence.reassociateIfAbsent(kaleoInstanceToken);

		kaleoInstanceToken.setCurrentKaleoNodeId(currentKaleoNodeId);
		kaleoInstanceToken.setCurrentKaleoNodeName(currentKaleoNodeName);

		return kaleoInstanceTokenPersistence.update(kaleoInstanceToken);
	}

	private SearchContext _buildSearchContext(
		KaleoInstanceTokenQuery kaleoInstanceTokenQuery, int start, int end,
		Sort[] sorts, ServiceContext serviceContext) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute(
			"kaleoInstanceTokenQuery", kaleoInstanceTokenQuery);
		searchContext.setCompanyId(kaleoInstanceTokenQuery.getCompanyId());
		searchContext.setEnd(end);
		searchContext.setGroupIds(new long[] {-1L});
		searchContext.setStart(start);

		if (sorts != null) {
			searchContext.setSorts(sorts);
		}

		searchContext.setUserId(serviceContext.getUserId());

		return searchContext;
	}

	private void _setCurrentKaleoNode(
			KaleoInstanceToken kaleoInstanceToken, long currentKaleoNodeId)
		throws PortalException {

		kaleoInstanceToken.setCurrentKaleoNodeId(currentKaleoNodeId);

		KaleoNode currentKaleoNode = _kaleoNodePersistence.findByPrimaryKey(
			currentKaleoNodeId);

		kaleoInstanceToken.setCurrentKaleoNodeName(currentKaleoNode.getName());
	}

	@Reference
	private KaleoInstancePersistence _kaleoInstancePersistence;

	@Reference
	private KaleoNodePersistence _kaleoNodePersistence;

	@Reference
	private Staging _staging;

	@Reference
	private UserLocalService _userLocalService;

}