/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchWorkflowInstanceLinkException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.model.WorkflowInstanceLinkTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.WorkflowInstanceLinkPersistence;
import com.liferay.portal.kernel.service.persistence.WorkflowInstanceLinkUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.WorkflowInstanceLinkImpl;
import com.liferay.portal.model.impl.WorkflowInstanceLinkModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the workflow instance link service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class WorkflowInstanceLinkPersistenceImpl
	extends BasePersistenceImpl
		<WorkflowInstanceLink, NoSuchWorkflowInstanceLinkException>
	implements WorkflowInstanceLinkPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>WorkflowInstanceLinkUtil</code> to access the workflow instance link persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		WorkflowInstanceLinkImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<WorkflowInstanceLink, NoSuchWorkflowInstanceLinkException>
			_uniquePersistenceFinderByWorkflowInstanceId;

	/**
	 * Returns the workflow instance link where workflowInstanceId = &#63; or throws a <code>NoSuchWorkflowInstanceLinkException</code> if it could not be found.
	 *
	 * @param workflowInstanceId the workflow instance ID
	 * @return the matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink findByWorkflowInstanceId(
			long workflowInstanceId)
		throws NoSuchWorkflowInstanceLinkException {

		return _uniquePersistenceFinderByWorkflowInstanceId.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {workflowInstanceId});
	}

	/**
	 * Returns the workflow instance link where workflowInstanceId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param workflowInstanceId the workflow instance ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink fetchByWorkflowInstanceId(
		long workflowInstanceId, boolean useFinderCache) {

		return _uniquePersistenceFinderByWorkflowInstanceId.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {workflowInstanceId},
			useFinderCache);
	}

	/**
	 * Removes the workflow instance link where workflowInstanceId = &#63; from the database.
	 *
	 * @param workflowInstanceId the workflow instance ID
	 * @return the workflow instance link that was removed
	 */
	@Override
	public WorkflowInstanceLink removeByWorkflowInstanceId(
			long workflowInstanceId)
		throws NoSuchWorkflowInstanceLinkException {

		WorkflowInstanceLink workflowInstanceLink = findByWorkflowInstanceId(
			workflowInstanceId);

		return remove(workflowInstanceLink);
	}

	/**
	 * Returns the number of workflow instance links where workflowInstanceId = &#63;.
	 *
	 * @param workflowInstanceId the workflow instance ID
	 * @return the number of matching workflow instance links
	 */
	@Override
	public int countByWorkflowInstanceId(long workflowInstanceId) {
		return _uniquePersistenceFinderByWorkflowInstanceId.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {workflowInstanceId});
	}

	private CollectionPersistenceFinder
		<WorkflowInstanceLink, NoSuchWorkflowInstanceLinkException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the workflow instance links where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow instance links
	 */
	@Override
	public List<WorkflowInstanceLink> findByC_C(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<WorkflowInstanceLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow instance link in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink findByC_C_First(
			long companyId, long classNameId,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException {

		return _collectionPersistenceFinderByC_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, orderByComparator);
	}

	/**
	 * Returns the first workflow instance link in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink fetchByC_C_First(
		long companyId, long classNameId,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, orderByComparator);
	}

	/**
	 * Removes all the workflow instance links where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_C(long companyId, long classNameId) {
		_collectionPersistenceFinderByC_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId});
	}

	/**
	 * Returns the number of workflow instance links where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching workflow instance links
	 */
	@Override
	public int countByC_C(long companyId, long classNameId) {
		return _collectionPersistenceFinderByC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId});
	}

	private CollectionPersistenceFinder
		<WorkflowInstanceLink, NoSuchWorkflowInstanceLinkException>
			_collectionPersistenceFinderByG_C_C;

	/**
	 * Returns an ordered range of all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow instance links
	 */
	@Override
	public List<WorkflowInstanceLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end,
		OrderByComparator<WorkflowInstanceLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink findByG_C_C_First(
			long groupId, long companyId, long classNameId,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException {

		return _collectionPersistenceFinderByG_C_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId}, orderByComparator);
	}

	/**
	 * Returns the first workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink fetchByG_C_C_First(
		long groupId, long companyId, long classNameId,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId}, orderByComparator);
	}

	/**
	 * Removes all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_C_C(long groupId, long companyId, long classNameId) {
		_collectionPersistenceFinderByG_C_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId});
	}

	/**
	 * Returns the number of workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching workflow instance links
	 */
	@Override
	public int countByG_C_C(long groupId, long companyId, long classNameId) {
		return _collectionPersistenceFinderByG_C_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId});
	}

	private CollectionPersistenceFinder
		<WorkflowInstanceLink, NoSuchWorkflowInstanceLinkException>
			_collectionPersistenceFinderByG_C_C_C;

	/**
	 * Returns an ordered range of all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow instance links
	 */
	@Override
	public List<WorkflowInstanceLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end, OrderByComparator<WorkflowInstanceLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink findByG_C_C_C_First(
			long groupId, long companyId, long classNameId, long classPK,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException {

		return _collectionPersistenceFinderByG_C_C_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink fetchByG_C_C_C_First(
		long groupId, long companyId, long classNameId, long classPK,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK) {

		_collectionPersistenceFinderByG_C_C_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, classPK});
	}

	/**
	 * Returns the number of workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching workflow instance links
	 */
	@Override
	public int countByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK) {

		return _collectionPersistenceFinderByG_C_C_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, classPK});
	}

	public WorkflowInstanceLinkPersistenceImpl() {
		setModelClass(WorkflowInstanceLink.class);

		setModelImplClass(WorkflowInstanceLinkImpl.class);
		setModelPKClass(long.class);

		setTable(WorkflowInstanceLinkTable.INSTANCE);
	}

	/**
	 * Creates a new workflow instance link with the primary key. Does not add the workflow instance link to the database.
	 *
	 * @param workflowInstanceLinkId the primary key for the new workflow instance link
	 * @return the new workflow instance link
	 */
	@Override
	public WorkflowInstanceLink create(long workflowInstanceLinkId) {
		WorkflowInstanceLink workflowInstanceLink =
			new WorkflowInstanceLinkImpl();

		workflowInstanceLink.setNew(true);
		workflowInstanceLink.setPrimaryKey(workflowInstanceLinkId);

		workflowInstanceLink.setCompanyId(CompanyThreadLocal.getCompanyId());

		return workflowInstanceLink;
	}

	/**
	 * Removes the workflow instance link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param workflowInstanceLinkId the primary key of the workflow instance link
	 * @return the workflow instance link that was removed
	 * @throws NoSuchWorkflowInstanceLinkException if a workflow instance link with the primary key could not be found
	 */
	@Override
	public WorkflowInstanceLink remove(long workflowInstanceLinkId)
		throws NoSuchWorkflowInstanceLinkException {

		return remove((Serializable)workflowInstanceLinkId);
	}

	@Override
	protected WorkflowInstanceLink removeImpl(
		WorkflowInstanceLink workflowInstanceLink) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(workflowInstanceLink)) {
				workflowInstanceLink = (WorkflowInstanceLink)session.get(
					WorkflowInstanceLinkImpl.class,
					workflowInstanceLink.getPrimaryKeyObj());
			}

			if ((workflowInstanceLink != null) &&
				CTPersistenceHelperUtil.isRemove(workflowInstanceLink)) {

				session.delete(workflowInstanceLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (workflowInstanceLink != null) {
			clearCache(workflowInstanceLink);
		}

		return workflowInstanceLink;
	}

	@Override
	public WorkflowInstanceLink updateImpl(
		WorkflowInstanceLink workflowInstanceLink) {

		boolean isNew = workflowInstanceLink.isNew();

		if (!(workflowInstanceLink instanceof WorkflowInstanceLinkModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(workflowInstanceLink.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					workflowInstanceLink);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in workflowInstanceLink proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom WorkflowInstanceLink implementation " +
					workflowInstanceLink.getClass());
		}

		WorkflowInstanceLinkModelImpl workflowInstanceLinkModelImpl =
			(WorkflowInstanceLinkModelImpl)workflowInstanceLink;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (workflowInstanceLink.getCreateDate() == null)) {
			if (serviceContext == null) {
				workflowInstanceLink.setCreateDate(date);
			}
			else {
				workflowInstanceLink.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!workflowInstanceLinkModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				workflowInstanceLink.setModifiedDate(date);
			}
			else {
				workflowInstanceLink.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(workflowInstanceLink)) {
				if (!isNew) {
					session.evict(
						WorkflowInstanceLinkImpl.class,
						workflowInstanceLink.getPrimaryKeyObj());
				}

				session.save(workflowInstanceLink);
			}
			else {
				workflowInstanceLink = (WorkflowInstanceLink)session.merge(
					workflowInstanceLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(workflowInstanceLink, false);

		if (isNew) {
			workflowInstanceLink.setNew(false);
		}

		workflowInstanceLink.resetOriginalValues();

		return workflowInstanceLink;
	}

	/**
	 * Returns the workflow instance link with the primary key or throws a <code>NoSuchWorkflowInstanceLinkException</code> if it could not be found.
	 *
	 * @param workflowInstanceLinkId the primary key of the workflow instance link
	 * @return the workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a workflow instance link with the primary key could not be found
	 */
	@Override
	public WorkflowInstanceLink findByPrimaryKey(long workflowInstanceLinkId)
		throws NoSuchWorkflowInstanceLinkException {

		return findByPrimaryKey((Serializable)workflowInstanceLinkId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the workflow instance link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param workflowInstanceLinkId the primary key of the workflow instance link
	 * @return the workflow instance link, or <code>null</code> if a workflow instance link with the primary key could not be found
	 */
	@Override
	public WorkflowInstanceLink fetchByPrimaryKey(long workflowInstanceLinkId) {
		return fetchByPrimaryKey((Serializable)workflowInstanceLinkId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "workflowInstanceLinkId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_WORKFLOWINSTANCELINK;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return WorkflowInstanceLinkModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "WorkflowInstanceLink";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("workflowInstanceId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("workflowInstanceLinkId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"workflowInstanceId"});
	}

	/**
	 * Initializes the workflow instance link persistence.
	 */
	public void afterPropertiesSet() {
		_uniquePersistenceFinderByWorkflowInstanceId =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByWorkflowInstanceId",
					new String[] {Long.class.getName()},
					new String[] {"workflowInstanceId"}, 0, 0, false,
					WorkflowInstanceLink::getWorkflowInstanceId),
				_SQL_SELECT_WORKFLOWINSTANCELINK_WHERE, "",
				new FinderColumn<>(
					"workflowInstanceLink.", "workflowInstanceId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowInstanceLink::getWorkflowInstanceId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, false),
			_SQL_SELECT_WORKFLOWINSTANCELINK_WHERE,
			_SQL_COUNT_WORKFLOWINSTANCELINK_WHERE,
			WorkflowInstanceLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"workflowInstanceLink.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, WorkflowInstanceLink::getCompanyId),
			new FinderColumn<>(
				"workflowInstanceLink.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, WorkflowInstanceLink::getClassNameId));

		_collectionPersistenceFinderByG_C_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "companyId", "classNameId"}, false),
			_SQL_SELECT_WORKFLOWINSTANCELINK_WHERE,
			_SQL_COUNT_WORKFLOWINSTANCELINK_WHERE,
			WorkflowInstanceLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"workflowInstanceLink.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, WorkflowInstanceLink::getGroupId),
			new FinderColumn<>(
				"workflowInstanceLink.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, WorkflowInstanceLink::getCompanyId),
			new FinderColumn<>(
				"workflowInstanceLink.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, WorkflowInstanceLink::getClassNameId));

		_collectionPersistenceFinderByG_C_C_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "companyId", "classNameId", "classPK"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName()
					},
					new String[] {
						"groupId", "companyId", "classNameId", "classPK"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName()
					},
					new String[] {
						"groupId", "companyId", "classNameId", "classPK"
					},
					false),
				_SQL_SELECT_WORKFLOWINSTANCELINK_WHERE,
				_SQL_COUNT_WORKFLOWINSTANCELINK_WHERE,
				WorkflowInstanceLinkModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"workflowInstanceLink.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, WorkflowInstanceLink::getGroupId),
				new FinderColumn<>(
					"workflowInstanceLink.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowInstanceLink::getCompanyId),
				new FinderColumn<>(
					"workflowInstanceLink.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowInstanceLink::getClassNameId),
				new FinderColumn<>(
					"workflowInstanceLink.", "classPK", FinderColumn.Type.LONG,
					"=", true, true, WorkflowInstanceLink::getClassPK));

		WorkflowInstanceLinkUtil.setPersistence(this);
	}

	public void destroy() {
		WorkflowInstanceLinkUtil.setPersistence(null);

		EntityCacheUtil.removeCache(WorkflowInstanceLinkImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		WorkflowInstanceLinkModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_WORKFLOWINSTANCELINK =
		"SELECT workflowInstanceLink FROM WorkflowInstanceLink workflowInstanceLink";

	private static final String _SQL_SELECT_WORKFLOWINSTANCELINK_WHERE =
		"SELECT workflowInstanceLink FROM WorkflowInstanceLink workflowInstanceLink WHERE ";

	private static final String _SQL_COUNT_WORKFLOWINSTANCELINK_WHERE =
		"SELECT COUNT(workflowInstanceLink) FROM WorkflowInstanceLink workflowInstanceLink WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No WorkflowInstanceLink exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowInstanceLinkPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1088793257