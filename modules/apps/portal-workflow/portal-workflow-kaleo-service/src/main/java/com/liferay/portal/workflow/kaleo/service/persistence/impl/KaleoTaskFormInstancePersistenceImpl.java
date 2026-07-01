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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.workflow.kaleo.exception.NoSuchTaskFormInstanceException;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskFormInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskFormInstanceTable;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceImpl;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceModelImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskFormInstancePersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskFormInstanceUtil;
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
 * The persistence implementation for the kaleo task form instance service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KaleoTaskFormInstancePersistence.class)
public class KaleoTaskFormInstancePersistenceImpl
	extends BasePersistenceImpl
		<KaleoTaskFormInstance, NoSuchTaskFormInstanceException>
	implements KaleoTaskFormInstancePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoTaskFormInstanceUtil</code> to access the kaleo task form instance persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoTaskFormInstanceImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<KaleoTaskFormInstance, NoSuchTaskFormInstanceException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the kaleo task form instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo task form instances
	 * @param end the upper bound of the range of kaleo task form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task form instances
	 */
	@Override
	public List<KaleoTaskFormInstance> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task form instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form instance
	 * @throws NoSuchTaskFormInstanceException if a matching kaleo task form instance could not be found
	 */
	@Override
	public KaleoTaskFormInstance findByCompanyId_First(
			long companyId,
			OrderByComparator<KaleoTaskFormInstance> orderByComparator)
		throws NoSuchTaskFormInstanceException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo task form instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form instance, or <code>null</code> if a matching kaleo task form instance could not be found
	 */
	@Override
	public KaleoTaskFormInstance fetchByCompanyId_First(
		long companyId,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo task form instances where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of kaleo task form instances where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo task form instances
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<KaleoTaskFormInstance, NoSuchTaskFormInstanceException>
			_collectionPersistenceFinderByKaleoDefinitionVersionId;

	/**
	 * Returns an ordered range of all the kaleo task form instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo task form instances
	 * @param end the upper bound of the range of kaleo task form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task form instances
	 */
	@Override
	public List<KaleoTaskFormInstance> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.find(
			finderCache, new Object[] {kaleoDefinitionVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task form instance in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form instance
	 * @throws NoSuchTaskFormInstanceException if a matching kaleo task form instance could not be found
	 */
	@Override
	public KaleoTaskFormInstance findByKaleoDefinitionVersionId_First(
			long kaleoDefinitionVersionId,
			OrderByComparator<KaleoTaskFormInstance> orderByComparator)
		throws NoSuchTaskFormInstanceException {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.findFirst(
			finderCache, new Object[] {kaleoDefinitionVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo task form instance in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form instance, or <code>null</code> if a matching kaleo task form instance could not be found
	 */
	@Override
	public KaleoTaskFormInstance fetchByKaleoDefinitionVersionId_First(
		long kaleoDefinitionVersionId,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.
			fetchFirst(
				finderCache, new Object[] {kaleoDefinitionVersionId},
				orderByComparator);
	}

	/**
	 * Removes all the kaleo task form instances where kaleoDefinitionVersionId = &#63; from the database.
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
	 * Returns the number of kaleo task form instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo task form instances
	 */
	@Override
	public int countByKaleoDefinitionVersionId(long kaleoDefinitionVersionId) {
		return _collectionPersistenceFinderByKaleoDefinitionVersionId.count(
			finderCache, new Object[] {kaleoDefinitionVersionId});
	}

	private CollectionPersistenceFinder
		<KaleoTaskFormInstance, NoSuchTaskFormInstanceException>
			_collectionPersistenceFinderByKaleoInstanceId;

	/**
	 * Returns an ordered range of all the kaleo task form instances where kaleoInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param start the lower bound of the range of kaleo task form instances
	 * @param end the upper bound of the range of kaleo task form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task form instances
	 */
	@Override
	public List<KaleoTaskFormInstance> findByKaleoInstanceId(
		long kaleoInstanceId, int start, int end,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoInstanceId.find(
			finderCache, new Object[] {kaleoInstanceId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task form instance in the ordered set where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form instance
	 * @throws NoSuchTaskFormInstanceException if a matching kaleo task form instance could not be found
	 */
	@Override
	public KaleoTaskFormInstance findByKaleoInstanceId_First(
			long kaleoInstanceId,
			OrderByComparator<KaleoTaskFormInstance> orderByComparator)
		throws NoSuchTaskFormInstanceException {

		return _collectionPersistenceFinderByKaleoInstanceId.findFirst(
			finderCache, new Object[] {kaleoInstanceId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo task form instance in the ordered set where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form instance, or <code>null</code> if a matching kaleo task form instance could not be found
	 */
	@Override
	public KaleoTaskFormInstance fetchByKaleoInstanceId_First(
		long kaleoInstanceId,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator) {

		return _collectionPersistenceFinderByKaleoInstanceId.fetchFirst(
			finderCache, new Object[] {kaleoInstanceId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo task form instances where kaleoInstanceId = &#63; from the database.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 */
	@Override
	public void removeByKaleoInstanceId(long kaleoInstanceId) {
		_collectionPersistenceFinderByKaleoInstanceId.remove(
			finderCache, new Object[] {kaleoInstanceId});
	}

	/**
	 * Returns the number of kaleo task form instances where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @return the number of matching kaleo task form instances
	 */
	@Override
	public int countByKaleoInstanceId(long kaleoInstanceId) {
		return _collectionPersistenceFinderByKaleoInstanceId.count(
			finderCache, new Object[] {kaleoInstanceId});
	}

	private CollectionPersistenceFinder
		<KaleoTaskFormInstance, NoSuchTaskFormInstanceException>
			_collectionPersistenceFinderByKaleoTaskId;

	/**
	 * Returns an ordered range of all the kaleo task form instances where kaleoTaskId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoTaskId the kaleo task ID
	 * @param start the lower bound of the range of kaleo task form instances
	 * @param end the upper bound of the range of kaleo task form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task form instances
	 */
	@Override
	public List<KaleoTaskFormInstance> findByKaleoTaskId(
		long kaleoTaskId, int start, int end,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoTaskId.find(
			finderCache, new Object[] {kaleoTaskId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task form instance in the ordered set where kaleoTaskId = &#63;.
	 *
	 * @param kaleoTaskId the kaleo task ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form instance
	 * @throws NoSuchTaskFormInstanceException if a matching kaleo task form instance could not be found
	 */
	@Override
	public KaleoTaskFormInstance findByKaleoTaskId_First(
			long kaleoTaskId,
			OrderByComparator<KaleoTaskFormInstance> orderByComparator)
		throws NoSuchTaskFormInstanceException {

		return _collectionPersistenceFinderByKaleoTaskId.findFirst(
			finderCache, new Object[] {kaleoTaskId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo task form instance in the ordered set where kaleoTaskId = &#63;.
	 *
	 * @param kaleoTaskId the kaleo task ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form instance, or <code>null</code> if a matching kaleo task form instance could not be found
	 */
	@Override
	public KaleoTaskFormInstance fetchByKaleoTaskId_First(
		long kaleoTaskId,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator) {

		return _collectionPersistenceFinderByKaleoTaskId.fetchFirst(
			finderCache, new Object[] {kaleoTaskId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo task form instances where kaleoTaskId = &#63; from the database.
	 *
	 * @param kaleoTaskId the kaleo task ID
	 */
	@Override
	public void removeByKaleoTaskId(long kaleoTaskId) {
		_collectionPersistenceFinderByKaleoTaskId.remove(
			finderCache, new Object[] {kaleoTaskId});
	}

	/**
	 * Returns the number of kaleo task form instances where kaleoTaskId = &#63;.
	 *
	 * @param kaleoTaskId the kaleo task ID
	 * @return the number of matching kaleo task form instances
	 */
	@Override
	public int countByKaleoTaskId(long kaleoTaskId) {
		return _collectionPersistenceFinderByKaleoTaskId.count(
			finderCache, new Object[] {kaleoTaskId});
	}

	private CollectionPersistenceFinder
		<KaleoTaskFormInstance, NoSuchTaskFormInstanceException>
			_collectionPersistenceFinderByKaleoTaskInstanceTokenId;

	/**
	 * Returns an ordered range of all the kaleo task form instances where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param start the lower bound of the range of kaleo task form instances
	 * @param end the upper bound of the range of kaleo task form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task form instances
	 */
	@Override
	public List<KaleoTaskFormInstance> findByKaleoTaskInstanceTokenId(
		long kaleoTaskInstanceTokenId, int start, int end,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoTaskInstanceTokenId.find(
			finderCache, new Object[] {kaleoTaskInstanceTokenId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task form instance in the ordered set where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form instance
	 * @throws NoSuchTaskFormInstanceException if a matching kaleo task form instance could not be found
	 */
	@Override
	public KaleoTaskFormInstance findByKaleoTaskInstanceTokenId_First(
			long kaleoTaskInstanceTokenId,
			OrderByComparator<KaleoTaskFormInstance> orderByComparator)
		throws NoSuchTaskFormInstanceException {

		return _collectionPersistenceFinderByKaleoTaskInstanceTokenId.findFirst(
			finderCache, new Object[] {kaleoTaskInstanceTokenId},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo task form instance in the ordered set where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form instance, or <code>null</code> if a matching kaleo task form instance could not be found
	 */
	@Override
	public KaleoTaskFormInstance fetchByKaleoTaskInstanceTokenId_First(
		long kaleoTaskInstanceTokenId,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator) {

		return _collectionPersistenceFinderByKaleoTaskInstanceTokenId.
			fetchFirst(
				finderCache, new Object[] {kaleoTaskInstanceTokenId},
				orderByComparator);
	}

	/**
	 * Removes all the kaleo task form instances where kaleoTaskInstanceTokenId = &#63; from the database.
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
	 * Returns the number of kaleo task form instances where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @return the number of matching kaleo task form instances
	 */
	@Override
	public int countByKaleoTaskInstanceTokenId(long kaleoTaskInstanceTokenId) {
		return _collectionPersistenceFinderByKaleoTaskInstanceTokenId.count(
			finderCache, new Object[] {kaleoTaskInstanceTokenId});
	}

	private UniquePersistenceFinder
		<KaleoTaskFormInstance, NoSuchTaskFormInstanceException>
			_uniquePersistenceFinderByKaleoTaskFormId;

	/**
	 * Returns the kaleo task form instance where kaleoTaskFormId = &#63; or throws a <code>NoSuchTaskFormInstanceException</code> if it could not be found.
	 *
	 * @param kaleoTaskFormId the kaleo task form ID
	 * @return the matching kaleo task form instance
	 * @throws NoSuchTaskFormInstanceException if a matching kaleo task form instance could not be found
	 */
	@Override
	public KaleoTaskFormInstance findByKaleoTaskFormId(long kaleoTaskFormId)
		throws NoSuchTaskFormInstanceException {

		return _uniquePersistenceFinderByKaleoTaskFormId.find(
			finderCache, new Object[] {kaleoTaskFormId});
	}

	/**
	 * Returns the kaleo task form instance where kaleoTaskFormId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param kaleoTaskFormId the kaleo task form ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo task form instance, or <code>null</code> if a matching kaleo task form instance could not be found
	 */
	@Override
	public KaleoTaskFormInstance fetchByKaleoTaskFormId(
		long kaleoTaskFormId, boolean useFinderCache) {

		return _uniquePersistenceFinderByKaleoTaskFormId.fetch(
			finderCache, new Object[] {kaleoTaskFormId}, useFinderCache);
	}

	/**
	 * Removes the kaleo task form instance where kaleoTaskFormId = &#63; from the database.
	 *
	 * @param kaleoTaskFormId the kaleo task form ID
	 * @return the kaleo task form instance that was removed
	 */
	@Override
	public KaleoTaskFormInstance removeByKaleoTaskFormId(long kaleoTaskFormId)
		throws NoSuchTaskFormInstanceException {

		KaleoTaskFormInstance kaleoTaskFormInstance = findByKaleoTaskFormId(
			kaleoTaskFormId);

		return remove(kaleoTaskFormInstance);
	}

	/**
	 * Returns the number of kaleo task form instances where kaleoTaskFormId = &#63;.
	 *
	 * @param kaleoTaskFormId the kaleo task form ID
	 * @return the number of matching kaleo task form instances
	 */
	@Override
	public int countByKaleoTaskFormId(long kaleoTaskFormId) {
		return _uniquePersistenceFinderByKaleoTaskFormId.count(
			finderCache, new Object[] {kaleoTaskFormId});
	}

	public KaleoTaskFormInstancePersistenceImpl() {
		setModelClass(KaleoTaskFormInstance.class);

		setModelImplClass(KaleoTaskFormInstanceImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoTaskFormInstanceTable.INSTANCE);
	}

	/**
	 * Creates a new kaleo task form instance with the primary key. Does not add the kaleo task form instance to the database.
	 *
	 * @param kaleoTaskFormInstanceId the primary key for the new kaleo task form instance
	 * @return the new kaleo task form instance
	 */
	@Override
	public KaleoTaskFormInstance create(long kaleoTaskFormInstanceId) {
		KaleoTaskFormInstance kaleoTaskFormInstance =
			new KaleoTaskFormInstanceImpl();

		kaleoTaskFormInstance.setNew(true);
		kaleoTaskFormInstance.setPrimaryKey(kaleoTaskFormInstanceId);

		kaleoTaskFormInstance.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kaleoTaskFormInstance;
	}

	/**
	 * Removes the kaleo task form instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoTaskFormInstanceId the primary key of the kaleo task form instance
	 * @return the kaleo task form instance that was removed
	 * @throws NoSuchTaskFormInstanceException if a kaleo task form instance with the primary key could not be found
	 */
	@Override
	public KaleoTaskFormInstance remove(long kaleoTaskFormInstanceId)
		throws NoSuchTaskFormInstanceException {

		return remove((Serializable)kaleoTaskFormInstanceId);
	}

	@Override
	protected KaleoTaskFormInstance removeImpl(
		KaleoTaskFormInstance kaleoTaskFormInstance) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoTaskFormInstance)) {
				kaleoTaskFormInstance = (KaleoTaskFormInstance)session.get(
					KaleoTaskFormInstanceImpl.class,
					kaleoTaskFormInstance.getPrimaryKeyObj());
			}

			if ((kaleoTaskFormInstance != null) &&
				ctPersistenceHelper.isRemove(kaleoTaskFormInstance)) {

				session.delete(kaleoTaskFormInstance);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoTaskFormInstance != null) {
			clearCache(kaleoTaskFormInstance);
		}

		return kaleoTaskFormInstance;
	}

	@Override
	public KaleoTaskFormInstance updateImpl(
		KaleoTaskFormInstance kaleoTaskFormInstance) {

		boolean isNew = kaleoTaskFormInstance.isNew();

		if (!(kaleoTaskFormInstance instanceof
				KaleoTaskFormInstanceModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kaleoTaskFormInstance.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					kaleoTaskFormInstance);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoTaskFormInstance proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoTaskFormInstance implementation " +
					kaleoTaskFormInstance.getClass());
		}

		KaleoTaskFormInstanceModelImpl kaleoTaskFormInstanceModelImpl =
			(KaleoTaskFormInstanceModelImpl)kaleoTaskFormInstance;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kaleoTaskFormInstance.getCreateDate() == null)) {
			if (serviceContext == null) {
				kaleoTaskFormInstance.setCreateDate(date);
			}
			else {
				kaleoTaskFormInstance.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!kaleoTaskFormInstanceModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kaleoTaskFormInstance.setModifiedDate(date);
			}
			else {
				kaleoTaskFormInstance.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kaleoTaskFormInstance)) {
				if (!isNew) {
					session.evict(
						KaleoTaskFormInstanceImpl.class,
						kaleoTaskFormInstance.getPrimaryKeyObj());
				}

				session.save(kaleoTaskFormInstance);
			}
			else {
				kaleoTaskFormInstance = (KaleoTaskFormInstance)session.merge(
					kaleoTaskFormInstance);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kaleoTaskFormInstance, false);

		if (isNew) {
			kaleoTaskFormInstance.setNew(false);
		}

		kaleoTaskFormInstance.resetOriginalValues();

		return kaleoTaskFormInstance;
	}

	/**
	 * Returns the kaleo task form instance with the primary key or throws a <code>NoSuchTaskFormInstanceException</code> if it could not be found.
	 *
	 * @param kaleoTaskFormInstanceId the primary key of the kaleo task form instance
	 * @return the kaleo task form instance
	 * @throws NoSuchTaskFormInstanceException if a kaleo task form instance with the primary key could not be found
	 */
	@Override
	public KaleoTaskFormInstance findByPrimaryKey(long kaleoTaskFormInstanceId)
		throws NoSuchTaskFormInstanceException {

		return findByPrimaryKey((Serializable)kaleoTaskFormInstanceId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kaleo task form instance with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoTaskFormInstanceId the primary key of the kaleo task form instance
	 * @return the kaleo task form instance, or <code>null</code> if a kaleo task form instance with the primary key could not be found
	 */
	@Override
	public KaleoTaskFormInstance fetchByPrimaryKey(
		long kaleoTaskFormInstanceId) {

		return fetchByPrimaryKey((Serializable)kaleoTaskFormInstanceId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "kaleoTaskFormInstanceId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEOTASKFORMINSTANCE;
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
		return KaleoTaskFormInstanceModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KaleoTaskFormInstance";
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
		ctMergeColumnNames.add("kaleoTaskId");
		ctMergeColumnNames.add("kaleoTaskInstanceTokenId");
		ctMergeColumnNames.add("kaleoTaskFormId");
		ctMergeColumnNames.add("formValues");
		ctMergeColumnNames.add("formValueEntryGroupId");
		ctMergeColumnNames.add("formValueEntryId");
		ctMergeColumnNames.add("formValueEntryUuid");
		ctMergeColumnNames.add("metadata");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("kaleoTaskFormInstanceId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the kaleo task form instance persistence.
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
				_SQL_SELECT_KALEOTASKFORMINSTANCE_WHERE,
				_SQL_COUNT_KALEOTASKFORMINSTANCE_WHERE,
				KaleoTaskFormInstanceModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoTaskFormInstance.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTaskFormInstance::getCompanyId));

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
				_SQL_SELECT_KALEOTASKFORMINSTANCE_WHERE,
				_SQL_COUNT_KALEOTASKFORMINSTANCE_WHERE,
				KaleoTaskFormInstanceModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoTaskFormInstance.", "kaleoDefinitionVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTaskFormInstance::getKaleoDefinitionVersionId));

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
				_SQL_SELECT_KALEOTASKFORMINSTANCE_WHERE,
				_SQL_COUNT_KALEOTASKFORMINSTANCE_WHERE,
				KaleoTaskFormInstanceModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoTaskFormInstance.", "kaleoInstanceId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTaskFormInstance::getKaleoInstanceId));

		_collectionPersistenceFinderByKaleoTaskId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByKaleoTaskId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoTaskId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByKaleoTaskId", new String[] {Long.class.getName()},
					new String[] {"kaleoTaskId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKaleoTaskId", new String[] {Long.class.getName()},
					new String[] {"kaleoTaskId"}, false),
				_SQL_SELECT_KALEOTASKFORMINSTANCE_WHERE,
				_SQL_COUNT_KALEOTASKFORMINSTANCE_WHERE,
				KaleoTaskFormInstanceModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoTaskFormInstance.", "kaleoTaskId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTaskFormInstance::getKaleoTaskId));

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
				_SQL_SELECT_KALEOTASKFORMINSTANCE_WHERE,
				_SQL_COUNT_KALEOTASKFORMINSTANCE_WHERE,
				KaleoTaskFormInstanceModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoTaskFormInstance.", "kaleoTaskInstanceTokenId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTaskFormInstance::getKaleoTaskInstanceTokenId));

		_uniquePersistenceFinderByKaleoTaskFormId =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByKaleoTaskFormId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoTaskFormId"}, 0, 0, false,
					KaleoTaskFormInstance::getKaleoTaskFormId),
				_SQL_SELECT_KALEOTASKFORMINSTANCE_WHERE, "",
				new FinderColumn<>(
					"kaleoTaskFormInstance.", "kaleoTaskFormId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTaskFormInstance::getKaleoTaskFormId));

		KaleoTaskFormInstanceUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoTaskFormInstanceUtil.setPersistence(null);

		entityCache.removeCache(KaleoTaskFormInstanceImpl.class.getName());
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
		KaleoTaskFormInstanceModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KALEOTASKFORMINSTANCE =
		"SELECT kaleoTaskFormInstance FROM KaleoTaskFormInstance kaleoTaskFormInstance";

	private static final String _SQL_SELECT_KALEOTASKFORMINSTANCE_WHERE =
		"SELECT kaleoTaskFormInstance FROM KaleoTaskFormInstance kaleoTaskFormInstance WHERE ";

	private static final String _SQL_COUNT_KALEOTASKFORMINSTANCE_WHERE =
		"SELECT COUNT(kaleoTaskFormInstance) FROM KaleoTaskFormInstance kaleoTaskFormInstance WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No KaleoTaskFormInstance exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoTaskFormInstancePersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-152343429