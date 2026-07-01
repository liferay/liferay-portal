/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.workflow.kaleo.exception.NoSuchTaskAssignmentException;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentTable;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentImpl;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentModelImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskAssignmentPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskAssignmentUtil;
import com.liferay.portal.workflow.kaleo.service.persistence.impl.constants.KaleoPersistenceConstants;

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

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the kaleo task assignment service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KaleoTaskAssignmentPersistence.class)
public class KaleoTaskAssignmentPersistenceImpl
	extends BasePersistenceImpl
		<KaleoTaskAssignment, NoSuchTaskAssignmentException>
	implements KaleoTaskAssignmentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoTaskAssignmentUtil</code> to access the kaleo task assignment persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoTaskAssignmentImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<KaleoTaskAssignment, NoSuchTaskAssignmentException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the kaleo task assignments where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskAssignmentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo task assignments
	 * @param end the upper bound of the range of kaleo task assignments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignments
	 */
	@Override
	public List<KaleoTaskAssignment> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoTaskAssignment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task assignment in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment
	 * @throws NoSuchTaskAssignmentException if a matching kaleo task assignment could not be found
	 */
	@Override
	public KaleoTaskAssignment findByCompanyId_First(
			long companyId,
			OrderByComparator<KaleoTaskAssignment> orderByComparator)
		throws NoSuchTaskAssignmentException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo task assignment in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment, or <code>null</code> if a matching kaleo task assignment could not be found
	 */
	@Override
	public KaleoTaskAssignment fetchByCompanyId_First(
		long companyId,
		OrderByComparator<KaleoTaskAssignment> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo task assignments where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of kaleo task assignments where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo task assignments
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<KaleoTaskAssignment, NoSuchTaskAssignmentException>
			_collectionPersistenceFinderByKaleoDefinitionVersionId;

	/**
	 * Returns an ordered range of all the kaleo task assignments where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskAssignmentModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo task assignments
	 * @param end the upper bound of the range of kaleo task assignments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignments
	 */
	@Override
	public List<KaleoTaskAssignment> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end,
		OrderByComparator<KaleoTaskAssignment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.find(
			finderCache, new Object[] {kaleoDefinitionVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task assignment in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment
	 * @throws NoSuchTaskAssignmentException if a matching kaleo task assignment could not be found
	 */
	@Override
	public KaleoTaskAssignment findByKaleoDefinitionVersionId_First(
			long kaleoDefinitionVersionId,
			OrderByComparator<KaleoTaskAssignment> orderByComparator)
		throws NoSuchTaskAssignmentException {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.findFirst(
			finderCache, new Object[] {kaleoDefinitionVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo task assignment in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment, or <code>null</code> if a matching kaleo task assignment could not be found
	 */
	@Override
	public KaleoTaskAssignment fetchByKaleoDefinitionVersionId_First(
		long kaleoDefinitionVersionId,
		OrderByComparator<KaleoTaskAssignment> orderByComparator) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.
			fetchFirst(
				finderCache, new Object[] {kaleoDefinitionVersionId},
				orderByComparator);
	}

	/**
	 * Removes all the kaleo task assignments where kaleoDefinitionVersionId = &#63; from the database.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 */
	@Override
	public void removeByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId) {

		_collectionPersistenceFinderByKaleoDefinitionVersionId.remove(
			finderCache, new Object[] {kaleoDefinitionVersionId});
	}

	/**
	 * Returns the number of kaleo task assignments where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo task assignments
	 */
	@Override
	public int countByKaleoDefinitionVersionId(long kaleoDefinitionVersionId) {
		return _collectionPersistenceFinderByKaleoDefinitionVersionId.count(
			finderCache, new Object[] {kaleoDefinitionVersionId});
	}

	private CollectionPersistenceFinder
		<KaleoTaskAssignment, NoSuchTaskAssignmentException>
			_collectionPersistenceFinderByKCN_KCPK;

	/**
	 * Returns an ordered range of all the kaleo task assignments where kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskAssignmentModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param start the lower bound of the range of kaleo task assignments
	 * @param end the upper bound of the range of kaleo task assignments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignments
	 */
	@Override
	public List<KaleoTaskAssignment> findByKCN_KCPK(
		String kaleoClassName, long kaleoClassPK, int start, int end,
		OrderByComparator<KaleoTaskAssignment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKCN_KCPK.find(
			finderCache, new Object[] {kaleoClassName, kaleoClassPK}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task assignment in the ordered set where kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment
	 * @throws NoSuchTaskAssignmentException if a matching kaleo task assignment could not be found
	 */
	@Override
	public KaleoTaskAssignment findByKCN_KCPK_First(
			String kaleoClassName, long kaleoClassPK,
			OrderByComparator<KaleoTaskAssignment> orderByComparator)
		throws NoSuchTaskAssignmentException {

		return _collectionPersistenceFinderByKCN_KCPK.findFirst(
			finderCache, new Object[] {kaleoClassName, kaleoClassPK},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo task assignment in the ordered set where kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment, or <code>null</code> if a matching kaleo task assignment could not be found
	 */
	@Override
	public KaleoTaskAssignment fetchByKCN_KCPK_First(
		String kaleoClassName, long kaleoClassPK,
		OrderByComparator<KaleoTaskAssignment> orderByComparator) {

		return _collectionPersistenceFinderByKCN_KCPK.fetchFirst(
			finderCache, new Object[] {kaleoClassName, kaleoClassPK},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo task assignments where kaleoClassName = &#63; and kaleoClassPK = &#63; from the database.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 */
	@Override
	public void removeByKCN_KCPK(String kaleoClassName, long kaleoClassPK) {
		_collectionPersistenceFinderByKCN_KCPK.remove(
			finderCache, new Object[] {kaleoClassName, kaleoClassPK});
	}

	/**
	 * Returns the number of kaleo task assignments where kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @return the number of matching kaleo task assignments
	 */
	@Override
	public int countByKCN_KCPK(String kaleoClassName, long kaleoClassPK) {
		return _collectionPersistenceFinderByKCN_KCPK.count(
			finderCache, new Object[] {kaleoClassName, kaleoClassPK});
	}

	private CollectionPersistenceFinder
		<KaleoTaskAssignment, NoSuchTaskAssignmentException>
			_collectionPersistenceFinderByKCN_KCPK_ACN;

	/**
	 * Returns an ordered range of all the kaleo task assignments where kaleoClassName = &#63; and kaleoClassPK = &#63; and assigneeClassName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskAssignmentModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param assigneeClassName the assignee class name
	 * @param start the lower bound of the range of kaleo task assignments
	 * @param end the upper bound of the range of kaleo task assignments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignments
	 */
	@Override
	public List<KaleoTaskAssignment> findByKCN_KCPK_ACN(
		String kaleoClassName, long kaleoClassPK, String assigneeClassName,
		int start, int end,
		OrderByComparator<KaleoTaskAssignment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKCN_KCPK_ACN.find(
			finderCache,
			new Object[] {kaleoClassName, kaleoClassPK, assigneeClassName},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task assignment in the ordered set where kaleoClassName = &#63; and kaleoClassPK = &#63; and assigneeClassName = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param assigneeClassName the assignee class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment
	 * @throws NoSuchTaskAssignmentException if a matching kaleo task assignment could not be found
	 */
	@Override
	public KaleoTaskAssignment findByKCN_KCPK_ACN_First(
			String kaleoClassName, long kaleoClassPK, String assigneeClassName,
			OrderByComparator<KaleoTaskAssignment> orderByComparator)
		throws NoSuchTaskAssignmentException {

		return _collectionPersistenceFinderByKCN_KCPK_ACN.findFirst(
			finderCache,
			new Object[] {kaleoClassName, kaleoClassPK, assigneeClassName},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo task assignment in the ordered set where kaleoClassName = &#63; and kaleoClassPK = &#63; and assigneeClassName = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param assigneeClassName the assignee class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment, or <code>null</code> if a matching kaleo task assignment could not be found
	 */
	@Override
	public KaleoTaskAssignment fetchByKCN_KCPK_ACN_First(
		String kaleoClassName, long kaleoClassPK, String assigneeClassName,
		OrderByComparator<KaleoTaskAssignment> orderByComparator) {

		return _collectionPersistenceFinderByKCN_KCPK_ACN.fetchFirst(
			finderCache,
			new Object[] {kaleoClassName, kaleoClassPK, assigneeClassName},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo task assignments where kaleoClassName = &#63; and kaleoClassPK = &#63; and assigneeClassName = &#63; from the database.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param assigneeClassName the assignee class name
	 */
	@Override
	public void removeByKCN_KCPK_ACN(
		String kaleoClassName, long kaleoClassPK, String assigneeClassName) {

		_collectionPersistenceFinderByKCN_KCPK_ACN.remove(
			finderCache,
			new Object[] {kaleoClassName, kaleoClassPK, assigneeClassName});
	}

	/**
	 * Returns the number of kaleo task assignments where kaleoClassName = &#63; and kaleoClassPK = &#63; and assigneeClassName = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param assigneeClassName the assignee class name
	 * @return the number of matching kaleo task assignments
	 */
	@Override
	public int countByKCN_KCPK_ACN(
		String kaleoClassName, long kaleoClassPK, String assigneeClassName) {

		return _collectionPersistenceFinderByKCN_KCPK_ACN.count(
			finderCache,
			new Object[] {kaleoClassName, kaleoClassPK, assigneeClassName});
	}

	public KaleoTaskAssignmentPersistenceImpl() {
		setModelClass(KaleoTaskAssignment.class);

		setModelImplClass(KaleoTaskAssignmentImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoTaskAssignmentTable.INSTANCE);
	}

	/**
	 * Creates a new kaleo task assignment with the primary key. Does not add the kaleo task assignment to the database.
	 *
	 * @param kaleoTaskAssignmentId the primary key for the new kaleo task assignment
	 * @return the new kaleo task assignment
	 */
	@Override
	public KaleoTaskAssignment create(long kaleoTaskAssignmentId) {
		KaleoTaskAssignment kaleoTaskAssignment = new KaleoTaskAssignmentImpl();

		kaleoTaskAssignment.setNew(true);
		kaleoTaskAssignment.setPrimaryKey(kaleoTaskAssignmentId);

		kaleoTaskAssignment.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kaleoTaskAssignment;
	}

	/**
	 * Removes the kaleo task assignment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoTaskAssignmentId the primary key of the kaleo task assignment
	 * @return the kaleo task assignment that was removed
	 * @throws NoSuchTaskAssignmentException if a kaleo task assignment with the primary key could not be found
	 */
	@Override
	public KaleoTaskAssignment remove(long kaleoTaskAssignmentId)
		throws NoSuchTaskAssignmentException {

		return remove((Serializable)kaleoTaskAssignmentId);
	}

	@Override
	protected KaleoTaskAssignment removeImpl(
		KaleoTaskAssignment kaleoTaskAssignment) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoTaskAssignment)) {
				kaleoTaskAssignment = (KaleoTaskAssignment)session.get(
					KaleoTaskAssignmentImpl.class,
					kaleoTaskAssignment.getPrimaryKeyObj());
			}

			if ((kaleoTaskAssignment != null) &&
				ctPersistenceHelper.isRemove(kaleoTaskAssignment)) {

				session.delete(kaleoTaskAssignment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoTaskAssignment != null) {
			clearCache(kaleoTaskAssignment);
		}

		return kaleoTaskAssignment;
	}

	@Override
	public KaleoTaskAssignment updateImpl(
		KaleoTaskAssignment kaleoTaskAssignment) {

		boolean isNew = kaleoTaskAssignment.isNew();

		if (!(kaleoTaskAssignment instanceof KaleoTaskAssignmentModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kaleoTaskAssignment.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					kaleoTaskAssignment);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoTaskAssignment proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoTaskAssignment implementation " +
					kaleoTaskAssignment.getClass());
		}

		KaleoTaskAssignmentModelImpl kaleoTaskAssignmentModelImpl =
			(KaleoTaskAssignmentModelImpl)kaleoTaskAssignment;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kaleoTaskAssignment.getCreateDate() == null)) {
			if (serviceContext == null) {
				kaleoTaskAssignment.setCreateDate(date);
			}
			else {
				kaleoTaskAssignment.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!kaleoTaskAssignmentModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kaleoTaskAssignment.setModifiedDate(date);
			}
			else {
				kaleoTaskAssignment.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kaleoTaskAssignment)) {
				if (!isNew) {
					session.evict(
						KaleoTaskAssignmentImpl.class,
						kaleoTaskAssignment.getPrimaryKeyObj());
				}

				session.save(kaleoTaskAssignment);
			}
			else {
				kaleoTaskAssignment = (KaleoTaskAssignment)session.merge(
					kaleoTaskAssignment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kaleoTaskAssignment, false);

		if (isNew) {
			kaleoTaskAssignment.setNew(false);
		}

		kaleoTaskAssignment.resetOriginalValues();

		return kaleoTaskAssignment;
	}

	/**
	 * Returns the kaleo task assignment with the primary key or throws a <code>NoSuchTaskAssignmentException</code> if it could not be found.
	 *
	 * @param kaleoTaskAssignmentId the primary key of the kaleo task assignment
	 * @return the kaleo task assignment
	 * @throws NoSuchTaskAssignmentException if a kaleo task assignment with the primary key could not be found
	 */
	@Override
	public KaleoTaskAssignment findByPrimaryKey(long kaleoTaskAssignmentId)
		throws NoSuchTaskAssignmentException {

		return findByPrimaryKey((Serializable)kaleoTaskAssignmentId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kaleo task assignment with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoTaskAssignmentId the primary key of the kaleo task assignment
	 * @return the kaleo task assignment, or <code>null</code> if a kaleo task assignment with the primary key could not be found
	 */
	@Override
	public KaleoTaskAssignment fetchByPrimaryKey(long kaleoTaskAssignmentId) {
		return fetchByPrimaryKey((Serializable)kaleoTaskAssignmentId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "kaleoTaskAssignmentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEOTASKASSIGNMENT;
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
		return KaleoTaskAssignmentModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KaleoTaskAssignment";
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
		ctMergeColumnNames.add("kaleoClassName");
		ctMergeColumnNames.add("kaleoClassPK");
		ctMergeColumnNames.add("kaleoDefinitionId");
		ctMergeColumnNames.add("kaleoDefinitionVersionId");
		ctMergeColumnNames.add("kaleoNodeId");
		ctMergeColumnNames.add("assigneeClassName");
		ctMergeColumnNames.add("assigneeClassPK");
		ctMergeColumnNames.add("assigneeActionId");
		ctMergeColumnNames.add("assigneeScript");
		ctMergeColumnNames.add("assigneeScriptLanguage");
		ctMergeColumnNames.add("assigneeScriptRequiredContexts");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("kaleoTaskAssignmentId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the kaleo task assignment persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCompanyId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, false),
				_SQL_SELECT_KALEOTASKASSIGNMENT_WHERE,
				_SQL_COUNT_KALEOTASKASSIGNMENT_WHERE,
				KaleoTaskAssignmentModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoTaskAssignment.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, KaleoTaskAssignment::getCompanyId));

		_collectionPersistenceFinderByKaleoDefinitionVersionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByKaleoDefinitionVersionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoDefinitionVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByKaleoDefinitionVersionId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoDefinitionVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKaleoDefinitionVersionId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoDefinitionVersionId"}, false),
				_SQL_SELECT_KALEOTASKASSIGNMENT_WHERE,
				_SQL_COUNT_KALEOTASKASSIGNMENT_WHERE,
				KaleoTaskAssignmentModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoTaskAssignment.", "kaleoDefinitionVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTaskAssignment::getKaleoDefinitionVersionId));

		_collectionPersistenceFinderByKCN_KCPK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByKCN_KCPK",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoClassName", "kaleoClassPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByKCN_KCPK",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"kaleoClassName", "kaleoClassPK"}, 0, 1, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKCN_KCPK",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"kaleoClassName", "kaleoClassPK"}, 0, 1,
					false, null),
				_SQL_SELECT_KALEOTASKASSIGNMENT_WHERE,
				_SQL_COUNT_KALEOTASKASSIGNMENT_WHERE,
				KaleoTaskAssignmentModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoTaskAssignment.", "kaleoClassName",
					FinderColumn.Type.STRING, "=", true, true,
					KaleoTaskAssignment::getKaleoClassName),
				new FinderColumn<>(
					"kaleoTaskAssignment.", "kaleoClassPK",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTaskAssignment::getKaleoClassPK));

		_collectionPersistenceFinderByKCN_KCPK_ACN =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByKCN_KCPK_ACN",
					new String[] {
						String.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"kaleoClassName", "kaleoClassPK", "assigneeClassName"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByKCN_KCPK_ACN",
					new String[] {
						String.class.getName(), Long.class.getName(),
						String.class.getName()
					},
					new String[] {
						"kaleoClassName", "kaleoClassPK", "assigneeClassName"
					},
					0, 5, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKCN_KCPK_ACN",
					new String[] {
						String.class.getName(), Long.class.getName(),
						String.class.getName()
					},
					new String[] {
						"kaleoClassName", "kaleoClassPK", "assigneeClassName"
					},
					0, 5, false, null),
				_SQL_SELECT_KALEOTASKASSIGNMENT_WHERE,
				_SQL_COUNT_KALEOTASKASSIGNMENT_WHERE,
				KaleoTaskAssignmentModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoTaskAssignment.", "kaleoClassName",
					FinderColumn.Type.STRING, "=", true, true,
					KaleoTaskAssignment::getKaleoClassName),
				new FinderColumn<>(
					"kaleoTaskAssignment.", "kaleoClassPK",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTaskAssignment::getKaleoClassPK),
				new FinderColumn<>(
					"kaleoTaskAssignment.", "assigneeClassName",
					FinderColumn.Type.STRING, "=", true, true,
					KaleoTaskAssignment::getAssigneeClassName));

		KaleoTaskAssignmentUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoTaskAssignmentUtil.setPersistence(null);

		entityCache.removeCache(KaleoTaskAssignmentImpl.class.getName());
	}

	@Override
	@Reference(
		target = KaleoPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = KaleoPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = KaleoPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		KaleoTaskAssignmentModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KALEOTASKASSIGNMENT =
		"SELECT kaleoTaskAssignment FROM KaleoTaskAssignment kaleoTaskAssignment";

	private static final String _SQL_SELECT_KALEOTASKASSIGNMENT_WHERE =
		"SELECT kaleoTaskAssignment FROM KaleoTaskAssignment kaleoTaskAssignment WHERE ";

	private static final String _SQL_COUNT_KALEOTASKASSIGNMENT_WHERE =
		"SELECT COUNT(kaleoTaskAssignment) FROM KaleoTaskAssignment kaleoTaskAssignment WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No KaleoTaskAssignment exists with the key {";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1163168371