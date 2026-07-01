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
import com.liferay.portal.workflow.kaleo.exception.NoSuchTaskAssignmentInstanceException;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstanceTable;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceImpl;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskAssignmentInstancePersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskAssignmentInstanceUtil;
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
 * The persistence implementation for the kaleo task assignment instance service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KaleoTaskAssignmentInstancePersistence.class)
public class KaleoTaskAssignmentInstancePersistenceImpl
	extends BasePersistenceImpl
		<KaleoTaskAssignmentInstance, NoSuchTaskAssignmentInstanceException>
	implements KaleoTaskAssignmentInstancePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoTaskAssignmentInstanceUtil</code> to access the kaleo task assignment instance persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoTaskAssignmentInstanceImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<KaleoTaskAssignmentInstance, NoSuchTaskAssignmentInstanceException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	@Override
	public List<KaleoTaskAssignmentInstance> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance findByCompanyId_First(
			long companyId,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator)
		throws NoSuchTaskAssignmentInstanceException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance fetchByCompanyId_First(
		long companyId,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo task assignment instances where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of kaleo task assignment instances where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo task assignment instances
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<KaleoTaskAssignmentInstance, NoSuchTaskAssignmentInstanceException>
			_collectionPersistenceFinderByKaleoDefinitionVersionId;

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	@Override
	public List<KaleoTaskAssignmentInstance> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.find(
			finderCache, new Object[] {kaleoDefinitionVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance findByKaleoDefinitionVersionId_First(
			long kaleoDefinitionVersionId,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator)
		throws NoSuchTaskAssignmentInstanceException {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.findFirst(
			finderCache, new Object[] {kaleoDefinitionVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance fetchByKaleoDefinitionVersionId_First(
		long kaleoDefinitionVersionId,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.
			fetchFirst(
				finderCache, new Object[] {kaleoDefinitionVersionId},
				orderByComparator);
	}

	/**
	 * Removes all the kaleo task assignment instances where kaleoDefinitionVersionId = &#63; from the database.
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
	 * Returns the number of kaleo task assignment instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo task assignment instances
	 */
	@Override
	public int countByKaleoDefinitionVersionId(long kaleoDefinitionVersionId) {
		return _collectionPersistenceFinderByKaleoDefinitionVersionId.count(
			finderCache, new Object[] {kaleoDefinitionVersionId});
	}

	private CollectionPersistenceFinder
		<KaleoTaskAssignmentInstance, NoSuchTaskAssignmentInstanceException>
			_collectionPersistenceFinderByKaleoInstanceId;

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where kaleoInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	@Override
	public List<KaleoTaskAssignmentInstance> findByKaleoInstanceId(
		long kaleoInstanceId, int start, int end,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoInstanceId.find(
			finderCache, new Object[] {kaleoInstanceId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance findByKaleoInstanceId_First(
			long kaleoInstanceId,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator)
		throws NoSuchTaskAssignmentInstanceException {

		return _collectionPersistenceFinderByKaleoInstanceId.findFirst(
			finderCache, new Object[] {kaleoInstanceId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance fetchByKaleoInstanceId_First(
		long kaleoInstanceId,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return _collectionPersistenceFinderByKaleoInstanceId.fetchFirst(
			finderCache, new Object[] {kaleoInstanceId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo task assignment instances where kaleoInstanceId = &#63; from the database.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 */
	@Override
	public void removeByKaleoInstanceId(long kaleoInstanceId) {
		_collectionPersistenceFinderByKaleoInstanceId.remove(
			finderCache, new Object[] {kaleoInstanceId});
	}

	/**
	 * Returns the number of kaleo task assignment instances where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @return the number of matching kaleo task assignment instances
	 */
	@Override
	public int countByKaleoInstanceId(long kaleoInstanceId) {
		return _collectionPersistenceFinderByKaleoInstanceId.count(
			finderCache, new Object[] {kaleoInstanceId});
	}

	private CollectionPersistenceFinder
		<KaleoTaskAssignmentInstance, NoSuchTaskAssignmentInstanceException>
			_collectionPersistenceFinderByKaleoTaskInstanceTokenId;

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	@Override
	public List<KaleoTaskAssignmentInstance> findByKaleoTaskInstanceTokenId(
		long kaleoTaskInstanceTokenId, int start, int end,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoTaskInstanceTokenId.find(
			finderCache, new Object[] {kaleoTaskInstanceTokenId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance findByKaleoTaskInstanceTokenId_First(
			long kaleoTaskInstanceTokenId,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator)
		throws NoSuchTaskAssignmentInstanceException {

		return _collectionPersistenceFinderByKaleoTaskInstanceTokenId.findFirst(
			finderCache, new Object[] {kaleoTaskInstanceTokenId},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance fetchByKaleoTaskInstanceTokenId_First(
		long kaleoTaskInstanceTokenId,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return _collectionPersistenceFinderByKaleoTaskInstanceTokenId.
			fetchFirst(
				finderCache, new Object[] {kaleoTaskInstanceTokenId},
				orderByComparator);
	}

	/**
	 * Removes all the kaleo task assignment instances where kaleoTaskInstanceTokenId = &#63; from the database.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 */
	@Override
	public void removeByKaleoTaskInstanceTokenId(
		long kaleoTaskInstanceTokenId) {

		_collectionPersistenceFinderByKaleoTaskInstanceTokenId.remove(
			finderCache, new Object[] {kaleoTaskInstanceTokenId});
	}

	/**
	 * Returns the number of kaleo task assignment instances where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @return the number of matching kaleo task assignment instances
	 */
	@Override
	public int countByKaleoTaskInstanceTokenId(long kaleoTaskInstanceTokenId) {
		return _collectionPersistenceFinderByKaleoTaskInstanceTokenId.count(
			finderCache, new Object[] {kaleoTaskInstanceTokenId});
	}

	private CollectionPersistenceFinder
		<KaleoTaskAssignmentInstance, NoSuchTaskAssignmentInstanceException>
			_collectionPersistenceFinderByAssigneeClassName;

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where assigneeClassName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param assigneeClassName the assignee class name
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	@Override
	public List<KaleoTaskAssignmentInstance> findByAssigneeClassName(
		String assigneeClassName, int start, int end,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAssigneeClassName.find(
			finderCache, new Object[] {assigneeClassName}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where assigneeClassName = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance findByAssigneeClassName_First(
			String assigneeClassName,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator)
		throws NoSuchTaskAssignmentInstanceException {

		return _collectionPersistenceFinderByAssigneeClassName.findFirst(
			finderCache, new Object[] {assigneeClassName}, orderByComparator);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where assigneeClassName = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance fetchByAssigneeClassName_First(
		String assigneeClassName,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return _collectionPersistenceFinderByAssigneeClassName.fetchFirst(
			finderCache, new Object[] {assigneeClassName}, orderByComparator);
	}

	/**
	 * Removes all the kaleo task assignment instances where assigneeClassName = &#63; from the database.
	 *
	 * @param assigneeClassName the assignee class name
	 */
	@Override
	public void removeByAssigneeClassName(String assigneeClassName) {
		_collectionPersistenceFinderByAssigneeClassName.remove(
			finderCache, new Object[] {assigneeClassName});
	}

	/**
	 * Returns the number of kaleo task assignment instances where assigneeClassName = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @return the number of matching kaleo task assignment instances
	 */
	@Override
	public int countByAssigneeClassName(String assigneeClassName) {
		return _collectionPersistenceFinderByAssigneeClassName.count(
			finderCache, new Object[] {assigneeClassName});
	}

	private CollectionPersistenceFinder
		<KaleoTaskAssignmentInstance, NoSuchTaskAssignmentInstanceException>
			_collectionPersistenceFinderByG_ACPK;

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where groupId = &#63; and assigneeClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assigneeClassPK the assignee class pk
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	@Override
	public List<KaleoTaskAssignmentInstance> findByG_ACPK(
		long groupId, long assigneeClassPK, int start, int end,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_ACPK.find(
			finderCache, new Object[] {groupId, assigneeClassPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where groupId = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assigneeClassPK the assignee class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance findByG_ACPK_First(
			long groupId, long assigneeClassPK,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator)
		throws NoSuchTaskAssignmentInstanceException {

		return _collectionPersistenceFinderByG_ACPK.findFirst(
			finderCache, new Object[] {groupId, assigneeClassPK},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where groupId = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assigneeClassPK the assignee class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance fetchByG_ACPK_First(
		long groupId, long assigneeClassPK,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return _collectionPersistenceFinderByG_ACPK.fetchFirst(
			finderCache, new Object[] {groupId, assigneeClassPK},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo task assignment instances where groupId = &#63; and assigneeClassPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param assigneeClassPK the assignee class pk
	 */
	@Override
	public void removeByG_ACPK(long groupId, long assigneeClassPK) {
		_collectionPersistenceFinderByG_ACPK.remove(
			finderCache, new Object[] {groupId, assigneeClassPK});
	}

	/**
	 * Returns the number of kaleo task assignment instances where groupId = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assigneeClassPK the assignee class pk
	 * @return the number of matching kaleo task assignment instances
	 */
	@Override
	public int countByG_ACPK(long groupId, long assigneeClassPK) {
		return _collectionPersistenceFinderByG_ACPK.count(
			finderCache, new Object[] {groupId, assigneeClassPK});
	}

	private CollectionPersistenceFinder
		<KaleoTaskAssignmentInstance, NoSuchTaskAssignmentInstanceException>
			_collectionPersistenceFinderByKTITI_ACN;

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where kaleoTaskInstanceTokenId = &#63; and assigneeClassName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param assigneeClassName the assignee class name
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	@Override
	public List<KaleoTaskAssignmentInstance> findByKTITI_ACN(
		long kaleoTaskInstanceTokenId, String assigneeClassName, int start,
		int end,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKTITI_ACN.find(
			finderCache,
			new Object[] {kaleoTaskInstanceTokenId, assigneeClassName}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoTaskInstanceTokenId = &#63; and assigneeClassName = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param assigneeClassName the assignee class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance findByKTITI_ACN_First(
			long kaleoTaskInstanceTokenId, String assigneeClassName,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator)
		throws NoSuchTaskAssignmentInstanceException {

		return _collectionPersistenceFinderByKTITI_ACN.findFirst(
			finderCache,
			new Object[] {kaleoTaskInstanceTokenId, assigneeClassName},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoTaskInstanceTokenId = &#63; and assigneeClassName = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param assigneeClassName the assignee class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance fetchByKTITI_ACN_First(
		long kaleoTaskInstanceTokenId, String assigneeClassName,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return _collectionPersistenceFinderByKTITI_ACN.fetchFirst(
			finderCache,
			new Object[] {kaleoTaskInstanceTokenId, assigneeClassName},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo task assignment instances where kaleoTaskInstanceTokenId = &#63; and assigneeClassName = &#63; from the database.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param assigneeClassName the assignee class name
	 */
	@Override
	public void removeByKTITI_ACN(
		long kaleoTaskInstanceTokenId, String assigneeClassName) {

		_collectionPersistenceFinderByKTITI_ACN.remove(
			finderCache,
			new Object[] {kaleoTaskInstanceTokenId, assigneeClassName});
	}

	/**
	 * Returns the number of kaleo task assignment instances where kaleoTaskInstanceTokenId = &#63; and assigneeClassName = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param assigneeClassName the assignee class name
	 * @return the number of matching kaleo task assignment instances
	 */
	@Override
	public int countByKTITI_ACN(
		long kaleoTaskInstanceTokenId, String assigneeClassName) {

		return _collectionPersistenceFinderByKTITI_ACN.count(
			finderCache,
			new Object[] {kaleoTaskInstanceTokenId, assigneeClassName});
	}

	private CollectionPersistenceFinder
		<KaleoTaskAssignmentInstance, NoSuchTaskAssignmentInstanceException>
			_collectionPersistenceFinderByACN_ACPK;

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where assigneeClassName = &#63; and assigneeClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param assigneeClassName the assignee class name
	 * @param assigneeClassPK the assignee class pk
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	@Override
	public List<KaleoTaskAssignmentInstance> findByACN_ACPK(
		String assigneeClassName, long assigneeClassPK, int start, int end,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByACN_ACPK.find(
			finderCache, new Object[] {assigneeClassName, assigneeClassPK},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where assigneeClassName = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param assigneeClassPK the assignee class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance findByACN_ACPK_First(
			String assigneeClassName, long assigneeClassPK,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator)
		throws NoSuchTaskAssignmentInstanceException {

		return _collectionPersistenceFinderByACN_ACPK.findFirst(
			finderCache, new Object[] {assigneeClassName, assigneeClassPK},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where assigneeClassName = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param assigneeClassPK the assignee class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance fetchByACN_ACPK_First(
		String assigneeClassName, long assigneeClassPK,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return _collectionPersistenceFinderByACN_ACPK.fetchFirst(
			finderCache, new Object[] {assigneeClassName, assigneeClassPK},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo task assignment instances where assigneeClassName = &#63; and assigneeClassPK = &#63; from the database.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param assigneeClassPK the assignee class pk
	 */
	@Override
	public void removeByACN_ACPK(
		String assigneeClassName, long assigneeClassPK) {

		_collectionPersistenceFinderByACN_ACPK.remove(
			finderCache, new Object[] {assigneeClassName, assigneeClassPK});
	}

	/**
	 * Returns the number of kaleo task assignment instances where assigneeClassName = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param assigneeClassPK the assignee class pk
	 * @return the number of matching kaleo task assignment instances
	 */
	@Override
	public int countByACN_ACPK(String assigneeClassName, long assigneeClassPK) {
		return _collectionPersistenceFinderByACN_ACPK.count(
			finderCache, new Object[] {assigneeClassName, assigneeClassPK});
	}

	public KaleoTaskAssignmentInstancePersistenceImpl() {
		setModelClass(KaleoTaskAssignmentInstance.class);

		setModelImplClass(KaleoTaskAssignmentInstanceImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoTaskAssignmentInstanceTable.INSTANCE);
	}

	/**
	 * Creates a new kaleo task assignment instance with the primary key. Does not add the kaleo task assignment instance to the database.
	 *
	 * @param kaleoTaskAssignmentInstanceId the primary key for the new kaleo task assignment instance
	 * @return the new kaleo task assignment instance
	 */
	@Override
	public KaleoTaskAssignmentInstance create(
		long kaleoTaskAssignmentInstanceId) {

		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance =
			new KaleoTaskAssignmentInstanceImpl();

		kaleoTaskAssignmentInstance.setNew(true);
		kaleoTaskAssignmentInstance.setPrimaryKey(
			kaleoTaskAssignmentInstanceId);

		kaleoTaskAssignmentInstance.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return kaleoTaskAssignmentInstance;
	}

	/**
	 * Removes the kaleo task assignment instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoTaskAssignmentInstanceId the primary key of the kaleo task assignment instance
	 * @return the kaleo task assignment instance that was removed
	 * @throws NoSuchTaskAssignmentInstanceException if a kaleo task assignment instance with the primary key could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance remove(
			long kaleoTaskAssignmentInstanceId)
		throws NoSuchTaskAssignmentInstanceException {

		return remove((Serializable)kaleoTaskAssignmentInstanceId);
	}

	@Override
	protected KaleoTaskAssignmentInstance removeImpl(
		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoTaskAssignmentInstance)) {
				kaleoTaskAssignmentInstance =
					(KaleoTaskAssignmentInstance)session.get(
						KaleoTaskAssignmentInstanceImpl.class,
						kaleoTaskAssignmentInstance.getPrimaryKeyObj());
			}

			if ((kaleoTaskAssignmentInstance != null) &&
				ctPersistenceHelper.isRemove(kaleoTaskAssignmentInstance)) {

				session.delete(kaleoTaskAssignmentInstance);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoTaskAssignmentInstance != null) {
			clearCache(kaleoTaskAssignmentInstance);
		}

		return kaleoTaskAssignmentInstance;
	}

	@Override
	public KaleoTaskAssignmentInstance updateImpl(
		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance) {

		boolean isNew = kaleoTaskAssignmentInstance.isNew();

		if (!(kaleoTaskAssignmentInstance instanceof
				KaleoTaskAssignmentInstanceModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					kaleoTaskAssignmentInstance.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					kaleoTaskAssignmentInstance);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoTaskAssignmentInstance proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoTaskAssignmentInstance implementation " +
					kaleoTaskAssignmentInstance.getClass());
		}

		KaleoTaskAssignmentInstanceModelImpl
			kaleoTaskAssignmentInstanceModelImpl =
				(KaleoTaskAssignmentInstanceModelImpl)
					kaleoTaskAssignmentInstance;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kaleoTaskAssignmentInstance.getCreateDate() == null)) {
			if (serviceContext == null) {
				kaleoTaskAssignmentInstance.setCreateDate(date);
			}
			else {
				kaleoTaskAssignmentInstance.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!kaleoTaskAssignmentInstanceModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kaleoTaskAssignmentInstance.setModifiedDate(date);
			}
			else {
				kaleoTaskAssignmentInstance.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kaleoTaskAssignmentInstance)) {
				if (!isNew) {
					session.evict(
						KaleoTaskAssignmentInstanceImpl.class,
						kaleoTaskAssignmentInstance.getPrimaryKeyObj());
				}

				session.save(kaleoTaskAssignmentInstance);
			}
			else {
				kaleoTaskAssignmentInstance =
					(KaleoTaskAssignmentInstance)session.merge(
						kaleoTaskAssignmentInstance);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kaleoTaskAssignmentInstance, false);

		if (isNew) {
			kaleoTaskAssignmentInstance.setNew(false);
		}

		kaleoTaskAssignmentInstance.resetOriginalValues();

		return kaleoTaskAssignmentInstance;
	}

	/**
	 * Returns the kaleo task assignment instance with the primary key or throws a <code>NoSuchTaskAssignmentInstanceException</code> if it could not be found.
	 *
	 * @param kaleoTaskAssignmentInstanceId the primary key of the kaleo task assignment instance
	 * @return the kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a kaleo task assignment instance with the primary key could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance findByPrimaryKey(
			long kaleoTaskAssignmentInstanceId)
		throws NoSuchTaskAssignmentInstanceException {

		return findByPrimaryKey((Serializable)kaleoTaskAssignmentInstanceId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kaleo task assignment instance with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoTaskAssignmentInstanceId the primary key of the kaleo task assignment instance
	 * @return the kaleo task assignment instance, or <code>null</code> if a kaleo task assignment instance with the primary key could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance fetchByPrimaryKey(
		long kaleoTaskAssignmentInstanceId) {

		return fetchByPrimaryKey((Serializable)kaleoTaskAssignmentInstanceId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "kaleoTaskAssignmentInstanceId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEOTASKASSIGNMENTINSTANCE;
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
		return KaleoTaskAssignmentInstanceModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KaleoTaskAssignmentInstance";
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
		ctMergeColumnNames.add("kaleoDefinitionId");
		ctMergeColumnNames.add("kaleoDefinitionVersionId");
		ctMergeColumnNames.add("kaleoInstanceId");
		ctMergeColumnNames.add("kaleoInstanceTokenId");
		ctMergeColumnNames.add("kaleoTaskInstanceTokenId");
		ctMergeColumnNames.add("kaleoTaskId");
		ctMergeColumnNames.add("kaleoTaskName");
		ctMergeColumnNames.add("assigneeClassName");
		ctMergeColumnNames.add("assigneeClassPK");
		ctMergeColumnNames.add("completed");
		ctMergeColumnNames.add("completionDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("kaleoTaskAssignmentInstanceId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the kaleo task assignment instance persistence.
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
				_SQL_SELECT_KALEOTASKASSIGNMENTINSTANCE_WHERE,
				_SQL_COUNT_KALEOTASKASSIGNMENTINSTANCE_WHERE,
				KaleoTaskAssignmentInstanceModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"kaleoTaskAssignmentInstance.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTaskAssignmentInstance::getCompanyId));

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
				_SQL_SELECT_KALEOTASKASSIGNMENTINSTANCE_WHERE,
				_SQL_COUNT_KALEOTASKASSIGNMENTINSTANCE_WHERE,
				KaleoTaskAssignmentInstanceModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"kaleoTaskAssignmentInstance.", "kaleoDefinitionVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTaskAssignmentInstance::getKaleoDefinitionVersionId));

		_collectionPersistenceFinderByKaleoInstanceId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByKaleoInstanceId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoInstanceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByKaleoInstanceId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoInstanceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKaleoInstanceId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoInstanceId"}, false),
				_SQL_SELECT_KALEOTASKASSIGNMENTINSTANCE_WHERE,
				_SQL_COUNT_KALEOTASKASSIGNMENTINSTANCE_WHERE,
				KaleoTaskAssignmentInstanceModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"kaleoTaskAssignmentInstance.", "kaleoInstanceId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTaskAssignmentInstance::getKaleoInstanceId));

		_collectionPersistenceFinderByKaleoTaskInstanceTokenId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByKaleoTaskInstanceTokenId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoTaskInstanceTokenId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByKaleoTaskInstanceTokenId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoTaskInstanceTokenId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKaleoTaskInstanceTokenId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoTaskInstanceTokenId"}, false),
				_SQL_SELECT_KALEOTASKASSIGNMENTINSTANCE_WHERE,
				_SQL_COUNT_KALEOTASKASSIGNMENTINSTANCE_WHERE,
				KaleoTaskAssignmentInstanceModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"kaleoTaskAssignmentInstance.", "kaleoTaskInstanceTokenId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTaskAssignmentInstance::getKaleoTaskInstanceTokenId));

		_collectionPersistenceFinderByAssigneeClassName =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByAssigneeClassName",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"assigneeClassName"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByAssigneeClassName",
					new String[] {String.class.getName()},
					new String[] {"assigneeClassName"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByAssigneeClassName",
					new String[] {String.class.getName()},
					new String[] {"assigneeClassName"}, 0, 1, false, null),
				_SQL_SELECT_KALEOTASKASSIGNMENTINSTANCE_WHERE,
				_SQL_COUNT_KALEOTASKASSIGNMENTINSTANCE_WHERE,
				KaleoTaskAssignmentInstanceModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"kaleoTaskAssignmentInstance.", "assigneeClassName",
					FinderColumn.Type.STRING, "=", true, true,
					KaleoTaskAssignmentInstance::getAssigneeClassName));

		_collectionPersistenceFinderByG_ACPK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_ACPK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "assigneeClassPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_ACPK",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "assigneeClassPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_ACPK",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "assigneeClassPK"}, false),
				_SQL_SELECT_KALEOTASKASSIGNMENTINSTANCE_WHERE,
				_SQL_COUNT_KALEOTASKASSIGNMENTINSTANCE_WHERE,
				KaleoTaskAssignmentInstanceModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"kaleoTaskAssignmentInstance.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTaskAssignmentInstance::getGroupId),
				new FinderColumn<>(
					"kaleoTaskAssignmentInstance.", "assigneeClassPK",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTaskAssignmentInstance::getAssigneeClassPK));

		_collectionPersistenceFinderByKTITI_ACN =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByKTITI_ACN",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"kaleoTaskInstanceTokenId", "assigneeClassName"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByKTITI_ACN",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {
						"kaleoTaskInstanceTokenId", "assigneeClassName"
					},
					0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKTITI_ACN",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {
						"kaleoTaskInstanceTokenId", "assigneeClassName"
					},
					0, 2, false, null),
				_SQL_SELECT_KALEOTASKASSIGNMENTINSTANCE_WHERE,
				_SQL_COUNT_KALEOTASKASSIGNMENTINSTANCE_WHERE,
				KaleoTaskAssignmentInstanceModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"kaleoTaskAssignmentInstance.", "kaleoTaskInstanceTokenId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTaskAssignmentInstance::getKaleoTaskInstanceTokenId),
				new FinderColumn<>(
					"kaleoTaskAssignmentInstance.", "assigneeClassName",
					FinderColumn.Type.STRING, "=", true, true,
					KaleoTaskAssignmentInstance::getAssigneeClassName));

		_collectionPersistenceFinderByACN_ACPK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByACN_ACPK",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"assigneeClassName", "assigneeClassPK"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByACN_ACPK",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"assigneeClassName", "assigneeClassPK"}, 0, 1,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByACN_ACPK",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"assigneeClassName", "assigneeClassPK"}, 0, 1,
					false, null),
				_SQL_SELECT_KALEOTASKASSIGNMENTINSTANCE_WHERE,
				_SQL_COUNT_KALEOTASKASSIGNMENTINSTANCE_WHERE,
				KaleoTaskAssignmentInstanceModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"kaleoTaskAssignmentInstance.", "assigneeClassName",
					FinderColumn.Type.STRING, "=", true, true,
					KaleoTaskAssignmentInstance::getAssigneeClassName),
				new FinderColumn<>(
					"kaleoTaskAssignmentInstance.", "assigneeClassPK",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTaskAssignmentInstance::getAssigneeClassPK));

		KaleoTaskAssignmentInstanceUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoTaskAssignmentInstanceUtil.setPersistence(null);

		entityCache.removeCache(
			KaleoTaskAssignmentInstanceImpl.class.getName());
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
		KaleoTaskAssignmentInstanceModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KALEOTASKASSIGNMENTINSTANCE =
		"SELECT kaleoTaskAssignmentInstance FROM KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance";

	private static final String _SQL_SELECT_KALEOTASKASSIGNMENTINSTANCE_WHERE =
		"SELECT kaleoTaskAssignmentInstance FROM KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance WHERE ";

	private static final String _SQL_COUNT_KALEOTASKASSIGNMENTINSTANCE_WHERE =
		"SELECT COUNT(kaleoTaskAssignmentInstance) FROM KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-927428864