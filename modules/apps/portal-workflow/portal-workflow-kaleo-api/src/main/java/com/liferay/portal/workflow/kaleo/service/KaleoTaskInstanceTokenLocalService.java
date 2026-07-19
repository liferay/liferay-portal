/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;

import java.io.Serializable;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for KaleoTaskInstanceToken. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoTaskInstanceTokenLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface KaleoTaskInstanceTokenLocalService
	extends BaseLocalService, CTService<KaleoTaskInstanceToken>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoTaskInstanceTokenLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the kaleo task instance token local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link KaleoTaskInstanceTokenLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the kaleo task instance token to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskInstanceToken the kaleo task instance token
	 * @return the kaleo task instance token that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public KaleoTaskInstanceToken addKaleoTaskInstanceToken(
		KaleoTaskInstanceToken kaleoTaskInstanceToken);

	@Indexable(type = IndexableType.REINDEX)
	public KaleoTaskInstanceToken addKaleoTaskInstanceToken(
			long kaleoInstanceTokenId, long kaleoTaskId, String kaleoTaskName,
			Collection<KaleoTaskAssignment> kaleoTaskAssignments, Date dueDate,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public KaleoTaskInstanceToken assignKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId,
			Collection<KaleoTaskAssignment> kaleoTaskAssignments,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public KaleoTaskInstanceToken assignKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId, String assigneeClassName,
			long assigneeClassPK, Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public KaleoTaskInstanceToken completeKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new kaleo task instance token with the primary key. Does not add the kaleo task instance token to the database.
	 *
	 * @param kaleoTaskInstanceTokenId the primary key for the new kaleo task instance token
	 * @return the new kaleo task instance token
	 */
	@Transactional(enabled = false)
	public KaleoTaskInstanceToken createKaleoTaskInstanceToken(
		long kaleoTaskInstanceTokenId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteCompanyKaleoTaskInstanceTokens(long companyId);

	public void deleteKaleoDefinitionVersionKaleoTaskInstanceTokens(
		long kaleoDefinitionVersionId);

	public void deleteKaleoInstanceKaleoTaskInstanceTokens(
		long kaleoInstanceId);

	/**
	 * Deletes the kaleo task instance token from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskInstanceToken the kaleo task instance token
	 * @return the kaleo task instance token that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public KaleoTaskInstanceToken deleteKaleoTaskInstanceToken(
		KaleoTaskInstanceToken kaleoTaskInstanceToken);

	/**
	 * Deletes the kaleo task instance token with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskInstanceTokenId the primary key of the kaleo task instance token
	 * @return the kaleo task instance token that was removed
	 * @throws PortalException if a kaleo task instance token with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public KaleoTaskInstanceToken deleteKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KaleoTaskInstanceToken fetchKaleoTaskInstanceToken(
		long kaleoTaskInstanceTokenId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoTaskInstanceToken> getCompanyKaleoTaskInstanceTokens(
		long companyId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCompanyKaleoTaskInstanceTokensCount(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the kaleo task instance token with the primary key.
	 *
	 * @param kaleoTaskInstanceTokenId the primary key of the kaleo task instance token
	 * @return the kaleo task instance token
	 * @throws PortalException if a kaleo task instance token with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KaleoTaskInstanceToken getKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		Boolean completed, int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext);

	/**
	 * Returns a range of all the kaleo task instance tokens.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo task instance tokens
	 * @param end the upper bound of the range of kaleo task instance tokens (not inclusive)
	 * @return the range of kaleo task instance tokens
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		List<Long> roleIds, Boolean completed, int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		long kaleoInstanceId, Boolean completed, int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KaleoTaskInstanceToken getKaleoTaskInstanceTokens(
			long kaleoInstanceId, long kaleoTaskId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		String className, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		String assigneeClassName, long assigneeClassPK, Boolean completed,
		int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext);

	/**
	 * Returns the number of kaleo task instance tokens.
	 *
	 * @return the number of kaleo task instance tokens
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKaleoTaskInstanceTokensCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKaleoTaskInstanceTokensCount(
		Boolean completed, ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKaleoTaskInstanceTokensCount(
		List<Long> roleIds, Boolean completed, ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKaleoTaskInstanceTokensCount(
		long kaleoInstanceId, Boolean completed, ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKaleoTaskInstanceTokensCount(
		String assigneeClassName, long assigneeClassPK, Boolean completed,
		ServiceContext serviceContext);

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoTaskInstanceToken>
		getSubmittingUserKaleoTaskInstanceTokens(
			long userId, Boolean completed, int start, int end,
			OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
			ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSubmittingUserKaleoTaskInstanceTokensCount(
		long userId, Boolean completed, ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasPendingKaleoTaskForms(long kaleoTaskInstanceTokenId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean isNotifiableUser(long userId, long workflowTaskId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoTaskInstanceToken> search(
		String keywords, Boolean completed, Boolean searchByUserRoles,
		int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoTaskInstanceToken> search(
		String taskName, String assetType, Long[] assetPrimaryKeys,
		Date dueDateGT, Date dueDateLT, Boolean completed,
		Boolean searchByUserRoles, boolean andOperator, int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoTaskInstanceToken> search(
		String assetTitle, String taskName, String[] assetTypes,
		Long[] assetPrimaryKeys, Date dueDateGT, Date dueDateLT,
		Boolean completed, Boolean searchByUserRoles, boolean andOperator,
		int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoTaskInstanceToken> search(
		String keywords, String[] assetTypes, Boolean completed,
		Boolean searchByUserRoles, int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoTaskInstanceToken> search(
		String assetTitle, String[] taskNames, String[] assetTypes,
		Long[] assetPrimaryKeys, Long[] assigneeClassPKs, Date dueDateGT,
		Date dueDateLT, Boolean completed, Long[] kaleoInstanceIds,
		Boolean searchByUserRoles, boolean andOperator, int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoTaskInstanceToken> search(
		String assetTitle, String[] taskNames, String[] assetTypes,
		Long[] assetPrimaryKeys, String assigneeClassName,
		Long[] assigneeClassPKs, Date dueDateGT, Date dueDateLT,
		Boolean completed, Long kaleoDefinitionId, Long[] kaleoInstanceIds,
		Boolean searchByUserRoles, boolean andOperator, int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long kaleoInstanceId, Boolean completed, Boolean searchByUserRoles,
		ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		Long kaleoInstanceId, String assetTitle, String taskName,
		String[] assetTypes, Long[] assetPrimaryKeys, Date dueDateGT,
		Date dueDateLT, Boolean completed, Boolean searchByUserRoles,
		boolean andOperator, ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		String keywords, Boolean completed, Boolean searchByUserRoles,
		ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		String taskName, String assetType, Long[] assetPrimaryKeys,
		Date dueDateGT, Date dueDateLT, Boolean completed,
		Boolean searchByUserRoles, boolean andOperator,
		ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		String keywords, String[] assetTypes, Boolean completed,
		Boolean searchByUserRoles, ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		String assetTitle, String[] taskNames, String[] assetTypes,
		Long[] assetPrimaryKeys, Long[] assigneeClassPKs, Date dueDateGT,
		Date dueDateLT, Boolean completed, Long[] kaleoInstanceIds,
		Boolean searchByUserRoles, boolean andOperator,
		ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		String assetTitle, String[] taskNames, String[] assetTypes,
		Long[] assetPrimaryKeys, String assigneeClassName,
		Long[] assigneeClassPKs, Date dueDateGT, Date dueDateLT,
		Boolean completed, Long kaleoDefinitionId, Long[] kaleoInstanceIds,
		Boolean searchByUserRoles, boolean andOperator,
		ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<KaleoTaskInstanceToken>
			searchKaleoTaskInstanceTokens(
				String assetTitle, String[] taskNames, String[] assetTypes,
				Long[] assetPrimaryKeys, String assigneeClassName,
				Long[] assigneeClassPKs, Date dueDateGT, Date dueDateLT,
				Boolean completed, Long kaleoDefinitionId,
				Long[] kaleoInstanceIds, boolean searchByActiveWorkflowHandlers,
				Boolean searchByUserRoles, boolean andOperator, int start,
				int end,
				OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
				ServiceContext serviceContext)
		throws PortalException;

	public KaleoTaskInstanceToken updateDueDate(
			long kaleoTaskInstanceTokenId, Date dueDate,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates the kaleo task instance token in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskInstanceToken the kaleo task instance token
	 * @return the kaleo task instance token that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public KaleoTaskInstanceToken updateKaleoTaskInstanceToken(
		KaleoTaskInstanceToken kaleoTaskInstanceToken);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<KaleoTaskInstanceToken> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<KaleoTaskInstanceToken> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<KaleoTaskInstanceToken>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:573371394