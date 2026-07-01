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
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.workflow.kaleo.exception.NoSuchInstanceException;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceTable;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceImpl;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoInstancePersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoInstanceUtil;
import com.liferay.portal.workflow.kaleo.service.persistence.impl.constants.KaleoPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
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
 * The persistence implementation for the kaleo instance service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KaleoInstancePersistence.class)
public class KaleoInstancePersistenceImpl
	extends BasePersistenceImpl<KaleoInstance, NoSuchInstanceException>
	implements KaleoInstancePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoInstanceUtil</code> to access the kaleo instance persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoInstanceImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<KaleoInstance, NoSuchInstanceException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the kaleo instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo instances
	 */
	@Override
	public List<KaleoInstance> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	@Override
	public KaleoInstance findByCompanyId_First(
			long companyId, OrderByComparator<KaleoInstance> orderByComparator)
		throws NoSuchInstanceException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	@Override
	public KaleoInstance fetchByCompanyId_First(
		long companyId, OrderByComparator<KaleoInstance> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo instances where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of kaleo instances where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo instances
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder<KaleoInstance, NoSuchInstanceException>
		_collectionPersistenceFinderByKaleoDefinitionVersionId;

	/**
	 * Returns an ordered range of all the kaleo instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo instances
	 */
	@Override
	public List<KaleoInstance> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.find(
			finderCache, new Object[] {kaleoDefinitionVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	@Override
	public KaleoInstance findByKaleoDefinitionVersionId_First(
			long kaleoDefinitionVersionId,
			OrderByComparator<KaleoInstance> orderByComparator)
		throws NoSuchInstanceException {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.findFirst(
			finderCache, new Object[] {kaleoDefinitionVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	@Override
	public KaleoInstance fetchByKaleoDefinitionVersionId_First(
		long kaleoDefinitionVersionId,
		OrderByComparator<KaleoInstance> orderByComparator) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.
			fetchFirst(
				finderCache, new Object[] {kaleoDefinitionVersionId},
				orderByComparator);
	}

	/**
	 * Removes all the kaleo instances where kaleoDefinitionVersionId = &#63; from the database.
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
	 * Returns the number of kaleo instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo instances
	 */
	@Override
	public int countByKaleoDefinitionVersionId(long kaleoDefinitionVersionId) {
		return _collectionPersistenceFinderByKaleoDefinitionVersionId.count(
			finderCache, new Object[] {kaleoDefinitionVersionId});
	}

	private CollectionPersistenceFinder<KaleoInstance, NoSuchInstanceException>
		_collectionPersistenceFinderByC_U;

	/**
	 * Returns an ordered range of all the kaleo instances where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo instances
	 */
	@Override
	public List<KaleoInstance> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_U.find(
			finderCache, new Object[] {companyId, userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	@Override
	public KaleoInstance findByC_U_First(
			long companyId, long userId,
			OrderByComparator<KaleoInstance> orderByComparator)
		throws NoSuchInstanceException {

		return _collectionPersistenceFinderByC_U.findFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	@Override
	public KaleoInstance fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<KaleoInstance> orderByComparator) {

		return _collectionPersistenceFinderByC_U.fetchFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo instances where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByC_U(long companyId, long userId) {
		_collectionPersistenceFinderByC_U.remove(
			finderCache, new Object[] {companyId, userId});
	}

	/**
	 * Returns the number of kaleo instances where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching kaleo instances
	 */
	@Override
	public int countByC_U(long companyId, long userId) {
		return _collectionPersistenceFinderByC_U.count(
			finderCache, new Object[] {companyId, userId});
	}

	private CollectionPersistenceFinder<KaleoInstance, NoSuchInstanceException>
		_collectionPersistenceFinderByKDI_C;

	/**
	 * Returns an ordered range of all the kaleo instances where kaleoDefinitionId = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo instances
	 */
	@Override
	public List<KaleoInstance> findByKDI_C(
		long kaleoDefinitionId, boolean completed, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKDI_C.find(
			finderCache, new Object[] {kaleoDefinitionId, completed}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where kaleoDefinitionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	@Override
	public KaleoInstance findByKDI_C_First(
			long kaleoDefinitionId, boolean completed,
			OrderByComparator<KaleoInstance> orderByComparator)
		throws NoSuchInstanceException {

		return _collectionPersistenceFinderByKDI_C.findFirst(
			finderCache, new Object[] {kaleoDefinitionId, completed},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where kaleoDefinitionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	@Override
	public KaleoInstance fetchByKDI_C_First(
		long kaleoDefinitionId, boolean completed,
		OrderByComparator<KaleoInstance> orderByComparator) {

		return _collectionPersistenceFinderByKDI_C.fetchFirst(
			finderCache, new Object[] {kaleoDefinitionId, completed},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo instances where kaleoDefinitionId = &#63; and completed = &#63; from the database.
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 */
	@Override
	public void removeByKDI_C(long kaleoDefinitionId, boolean completed) {
		_collectionPersistenceFinderByKDI_C.remove(
			finderCache, new Object[] {kaleoDefinitionId, completed});
	}

	/**
	 * Returns the number of kaleo instances where kaleoDefinitionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 * @return the number of matching kaleo instances
	 */
	@Override
	public int countByKDI_C(long kaleoDefinitionId, boolean completed) {
		return _collectionPersistenceFinderByKDI_C.count(
			finderCache, new Object[] {kaleoDefinitionId, completed});
	}

	private CollectionPersistenceFinder<KaleoInstance, NoSuchInstanceException>
		_collectionPersistenceFinderByKDVI_C;

	/**
	 * Returns an ordered range of all the kaleo instances where kaleoDefinitionVersionId = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo instances
	 */
	@Override
	public List<KaleoInstance> findByKDVI_C(
		long kaleoDefinitionVersionId, boolean completed, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKDVI_C.find(
			finderCache, new Object[] {kaleoDefinitionVersionId, completed},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where kaleoDefinitionVersionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	@Override
	public KaleoInstance findByKDVI_C_First(
			long kaleoDefinitionVersionId, boolean completed,
			OrderByComparator<KaleoInstance> orderByComparator)
		throws NoSuchInstanceException {

		return _collectionPersistenceFinderByKDVI_C.findFirst(
			finderCache, new Object[] {kaleoDefinitionVersionId, completed},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where kaleoDefinitionVersionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	@Override
	public KaleoInstance fetchByKDVI_C_First(
		long kaleoDefinitionVersionId, boolean completed,
		OrderByComparator<KaleoInstance> orderByComparator) {

		return _collectionPersistenceFinderByKDVI_C.fetchFirst(
			finderCache, new Object[] {kaleoDefinitionVersionId, completed},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo instances where kaleoDefinitionVersionId = &#63; and completed = &#63; from the database.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 */
	@Override
	public void removeByKDVI_C(
		long kaleoDefinitionVersionId, boolean completed) {

		_collectionPersistenceFinderByKDVI_C.remove(
			finderCache, new Object[] {kaleoDefinitionVersionId, completed});
	}

	/**
	 * Returns the number of kaleo instances where kaleoDefinitionVersionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 * @return the number of matching kaleo instances
	 */
	@Override
	public int countByKDVI_C(long kaleoDefinitionVersionId, boolean completed) {
		return _collectionPersistenceFinderByKDVI_C.count(
			finderCache, new Object[] {kaleoDefinitionVersionId, completed});
	}

	private CollectionPersistenceFinder<KaleoInstance, NoSuchInstanceException>
		_collectionPersistenceFinderByCN_CPK;

	/**
	 * Returns an ordered range of all the kaleo instances where className = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param className the class name
	 * @param classPK the class pk
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo instances
	 */
	@Override
	public List<KaleoInstance> findByCN_CPK(
		String className, long classPK, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCN_CPK.find(
			finderCache, new Object[] {className, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where className = &#63; and classPK = &#63;.
	 *
	 * @param className the class name
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	@Override
	public KaleoInstance findByCN_CPK_First(
			String className, long classPK,
			OrderByComparator<KaleoInstance> orderByComparator)
		throws NoSuchInstanceException {

		return _collectionPersistenceFinderByCN_CPK.findFirst(
			finderCache, new Object[] {className, classPK}, orderByComparator);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where className = &#63; and classPK = &#63;.
	 *
	 * @param className the class name
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	@Override
	public KaleoInstance fetchByCN_CPK_First(
		String className, long classPK,
		OrderByComparator<KaleoInstance> orderByComparator) {

		return _collectionPersistenceFinderByCN_CPK.fetchFirst(
			finderCache, new Object[] {className, classPK}, orderByComparator);
	}

	/**
	 * Removes all the kaleo instances where className = &#63; and classPK = &#63; from the database.
	 *
	 * @param className the class name
	 * @param classPK the class pk
	 */
	@Override
	public void removeByCN_CPK(String className, long classPK) {
		_collectionPersistenceFinderByCN_CPK.remove(
			finderCache, new Object[] {className, classPK});
	}

	/**
	 * Returns the number of kaleo instances where className = &#63; and classPK = &#63;.
	 *
	 * @param className the class name
	 * @param classPK the class pk
	 * @return the number of matching kaleo instances
	 */
	@Override
	public int countByCN_CPK(String className, long classPK) {
		return _collectionPersistenceFinderByCN_CPK.count(
			finderCache, new Object[] {className, classPK});
	}

	private UniquePersistenceFinder<KaleoInstance, NoSuchInstanceException>
		_uniquePersistenceFinderByKII_C_U;

	/**
	 * Returns the kaleo instance where kaleoInstanceId = &#63; and companyId = &#63; and userId = &#63; or throws a <code>NoSuchInstanceException</code> if it could not be found.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	@Override
	public KaleoInstance findByKII_C_U(
			long kaleoInstanceId, long companyId, long userId)
		throws NoSuchInstanceException {

		return _uniquePersistenceFinderByKII_C_U.find(
			finderCache, new Object[] {kaleoInstanceId, companyId, userId});
	}

	/**
	 * Returns the kaleo instance where kaleoInstanceId = &#63; and companyId = &#63; and userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	@Override
	public KaleoInstance fetchByKII_C_U(
		long kaleoInstanceId, long companyId, long userId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByKII_C_U.fetch(
			finderCache, new Object[] {kaleoInstanceId, companyId, userId},
			useFinderCache);
	}

	/**
	 * Removes the kaleo instance where kaleoInstanceId = &#63; and companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the kaleo instance that was removed
	 */
	@Override
	public KaleoInstance removeByKII_C_U(
			long kaleoInstanceId, long companyId, long userId)
		throws NoSuchInstanceException {

		KaleoInstance kaleoInstance = findByKII_C_U(
			kaleoInstanceId, companyId, userId);

		return remove(kaleoInstance);
	}

	/**
	 * Returns the number of kaleo instances where kaleoInstanceId = &#63; and companyId = &#63; and userId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching kaleo instances
	 */
	@Override
	public int countByKII_C_U(
		long kaleoInstanceId, long companyId, long userId) {

		return _uniquePersistenceFinderByKII_C_U.count(
			finderCache, new Object[] {kaleoInstanceId, companyId, userId});
	}

	private CollectionPersistenceFinder<KaleoInstance, NoSuchInstanceException>
		_collectionPersistenceFinderByC_KDN_KDV_CD;

	/**
	 * Returns an ordered range of all the kaleo instances where companyId = &#63; and kaleoDefinitionName = &#63; and kaleoDefinitionVersion = &#63; and completionDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param kaleoDefinitionName the kaleo definition name
	 * @param kaleoDefinitionVersion the kaleo definition version
	 * @param completionDate the completion date
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo instances
	 */
	@Override
	public List<KaleoInstance> findByC_KDN_KDV_CD(
		long companyId, String kaleoDefinitionName, int kaleoDefinitionVersion,
		Date completionDate, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_KDN_KDV_CD.find(
			finderCache,
			new Object[] {
				companyId, kaleoDefinitionName, kaleoDefinitionVersion,
				completionDate
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where companyId = &#63; and kaleoDefinitionName = &#63; and kaleoDefinitionVersion = &#63; and completionDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param kaleoDefinitionName the kaleo definition name
	 * @param kaleoDefinitionVersion the kaleo definition version
	 * @param completionDate the completion date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	@Override
	public KaleoInstance findByC_KDN_KDV_CD_First(
			long companyId, String kaleoDefinitionName,
			int kaleoDefinitionVersion, Date completionDate,
			OrderByComparator<KaleoInstance> orderByComparator)
		throws NoSuchInstanceException {

		return _collectionPersistenceFinderByC_KDN_KDV_CD.findFirst(
			finderCache,
			new Object[] {
				companyId, kaleoDefinitionName, kaleoDefinitionVersion,
				completionDate
			},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where companyId = &#63; and kaleoDefinitionName = &#63; and kaleoDefinitionVersion = &#63; and completionDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param kaleoDefinitionName the kaleo definition name
	 * @param kaleoDefinitionVersion the kaleo definition version
	 * @param completionDate the completion date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	@Override
	public KaleoInstance fetchByC_KDN_KDV_CD_First(
		long companyId, String kaleoDefinitionName, int kaleoDefinitionVersion,
		Date completionDate,
		OrderByComparator<KaleoInstance> orderByComparator) {

		return _collectionPersistenceFinderByC_KDN_KDV_CD.fetchFirst(
			finderCache,
			new Object[] {
				companyId, kaleoDefinitionName, kaleoDefinitionVersion,
				completionDate
			},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo instances where companyId = &#63; and kaleoDefinitionName = &#63; and kaleoDefinitionVersion = &#63; and completionDate = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param kaleoDefinitionName the kaleo definition name
	 * @param kaleoDefinitionVersion the kaleo definition version
	 * @param completionDate the completion date
	 */
	@Override
	public void removeByC_KDN_KDV_CD(
		long companyId, String kaleoDefinitionName, int kaleoDefinitionVersion,
		Date completionDate) {

		_collectionPersistenceFinderByC_KDN_KDV_CD.remove(
			finderCache,
			new Object[] {
				companyId, kaleoDefinitionName, kaleoDefinitionVersion,
				completionDate
			});
	}

	/**
	 * Returns the number of kaleo instances where companyId = &#63; and kaleoDefinitionName = &#63; and kaleoDefinitionVersion = &#63; and completionDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param kaleoDefinitionName the kaleo definition name
	 * @param kaleoDefinitionVersion the kaleo definition version
	 * @param completionDate the completion date
	 * @return the number of matching kaleo instances
	 */
	@Override
	public int countByC_KDN_KDV_CD(
		long companyId, String kaleoDefinitionName, int kaleoDefinitionVersion,
		Date completionDate) {

		return _collectionPersistenceFinderByC_KDN_KDV_CD.count(
			finderCache,
			new Object[] {
				companyId, kaleoDefinitionName, kaleoDefinitionVersion,
				completionDate
			});
	}

	public KaleoInstancePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(KaleoInstance.class);

		setModelImplClass(KaleoInstanceImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoInstanceTable.INSTANCE);
	}

	/**
	 * Creates a new kaleo instance with the primary key. Does not add the kaleo instance to the database.
	 *
	 * @param kaleoInstanceId the primary key for the new kaleo instance
	 * @return the new kaleo instance
	 */
	@Override
	public KaleoInstance create(long kaleoInstanceId) {
		KaleoInstance kaleoInstance = new KaleoInstanceImpl();

		kaleoInstance.setNew(true);
		kaleoInstance.setPrimaryKey(kaleoInstanceId);

		kaleoInstance.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kaleoInstance;
	}

	/**
	 * Removes the kaleo instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoInstanceId the primary key of the kaleo instance
	 * @return the kaleo instance that was removed
	 * @throws NoSuchInstanceException if a kaleo instance with the primary key could not be found
	 */
	@Override
	public KaleoInstance remove(long kaleoInstanceId)
		throws NoSuchInstanceException {

		return remove((Serializable)kaleoInstanceId);
	}

	@Override
	protected KaleoInstance removeImpl(KaleoInstance kaleoInstance) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoInstance)) {
				kaleoInstance = (KaleoInstance)session.get(
					KaleoInstanceImpl.class, kaleoInstance.getPrimaryKeyObj());
			}

			if ((kaleoInstance != null) &&
				ctPersistenceHelper.isRemove(kaleoInstance)) {

				session.delete(kaleoInstance);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoInstance != null) {
			clearCache(kaleoInstance);
		}

		return kaleoInstance;
	}

	@Override
	public KaleoInstance updateImpl(KaleoInstance kaleoInstance) {
		boolean isNew = kaleoInstance.isNew();

		if (!(kaleoInstance instanceof KaleoInstanceModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kaleoInstance.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					kaleoInstance);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoInstance proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoInstance implementation " +
					kaleoInstance.getClass());
		}

		KaleoInstanceModelImpl kaleoInstanceModelImpl =
			(KaleoInstanceModelImpl)kaleoInstance;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kaleoInstance.getCreateDate() == null)) {
			if (serviceContext == null) {
				kaleoInstance.setCreateDate(date);
			}
			else {
				kaleoInstance.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!kaleoInstanceModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kaleoInstance.setModifiedDate(date);
			}
			else {
				kaleoInstance.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kaleoInstance)) {
				if (!isNew) {
					session.evict(
						KaleoInstanceImpl.class,
						kaleoInstance.getPrimaryKeyObj());
				}

				session.save(kaleoInstance);
			}
			else {
				kaleoInstance = (KaleoInstance)session.merge(kaleoInstance);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kaleoInstance, false);

		if (isNew) {
			kaleoInstance.setNew(false);
		}

		kaleoInstance.resetOriginalValues();

		return kaleoInstance;
	}

	/**
	 * Returns the kaleo instance with the primary key or throws a <code>NoSuchInstanceException</code> if it could not be found.
	 *
	 * @param kaleoInstanceId the primary key of the kaleo instance
	 * @return the kaleo instance
	 * @throws NoSuchInstanceException if a kaleo instance with the primary key could not be found
	 */
	@Override
	public KaleoInstance findByPrimaryKey(long kaleoInstanceId)
		throws NoSuchInstanceException {

		return findByPrimaryKey((Serializable)kaleoInstanceId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kaleo instance with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoInstanceId the primary key of the kaleo instance
	 * @return the kaleo instance, or <code>null</code> if a kaleo instance with the primary key could not be found
	 */
	@Override
	public KaleoInstance fetchByPrimaryKey(long kaleoInstanceId) {
		return fetchByPrimaryKey((Serializable)kaleoInstanceId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "kaleoInstanceId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEOINSTANCE;
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
		return KaleoInstanceModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KaleoInstance";
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
		ctMergeColumnNames.add("kaleoDefinitionName");
		ctMergeColumnNames.add("kaleoDefinitionVersion");
		ctMergeColumnNames.add("rootKaleoInstanceTokenId");
		ctMergeColumnNames.add("active_");
		ctMergeColumnNames.add("className");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("completed");
		ctMergeColumnNames.add("completionDate");
		ctMergeColumnNames.add("workflowContext");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("kaleoInstanceId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the kaleo instance persistence.
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
				_SQL_SELECT_KALEOINSTANCE_WHERE, _SQL_COUNT_KALEOINSTANCE_WHERE,
				KaleoInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"kaleoInstance.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, KaleoInstance::getCompanyId));

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
				_SQL_SELECT_KALEOINSTANCE_WHERE, _SQL_COUNT_KALEOINSTANCE_WHERE,
				KaleoInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"kaleoInstance.", "kaleoDefinitionVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoInstance::getKaleoDefinitionVersionId));

		_collectionPersistenceFinderByC_U = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_U",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "userId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "userId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "userId"}, false),
			_SQL_SELECT_KALEOINSTANCE_WHERE, _SQL_COUNT_KALEOINSTANCE_WHERE,
			KaleoInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"kaleoInstance.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, KaleoInstance::getCompanyId),
			new FinderColumn<>(
				"kaleoInstance.", "userId", FinderColumn.Type.LONG, "=", true,
				true, KaleoInstance::getUserId));

		_collectionPersistenceFinderByKDI_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByKDI_C",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"kaleoDefinitionId", "completed"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByKDI_C",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"kaleoDefinitionId", "completed"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByKDI_C",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"kaleoDefinitionId", "completed"}, false),
			_SQL_SELECT_KALEOINSTANCE_WHERE, _SQL_COUNT_KALEOINSTANCE_WHERE,
			KaleoInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"kaleoInstance.", "kaleoDefinitionId", FinderColumn.Type.LONG,
				"=", true, true, KaleoInstance::getKaleoDefinitionId),
			new FinderColumn<>(
				"kaleoInstance.", "completed", FinderColumn.Type.BOOLEAN, "=",
				true, true, KaleoInstance::isCompleted));

		_collectionPersistenceFinderByKDVI_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByKDVI_C",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoDefinitionVersionId", "completed"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByKDVI_C",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"kaleoDefinitionVersionId", "completed"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByKDVI_C",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"kaleoDefinitionVersionId", "completed"},
					false),
				_SQL_SELECT_KALEOINSTANCE_WHERE, _SQL_COUNT_KALEOINSTANCE_WHERE,
				KaleoInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"kaleoInstance.", "kaleoDefinitionVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoInstance::getKaleoDefinitionVersionId),
				new FinderColumn<>(
					"kaleoInstance.", "completed", FinderColumn.Type.BOOLEAN,
					"=", true, true, KaleoInstance::isCompleted));

		_collectionPersistenceFinderByCN_CPK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCN_CPK",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"className", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCN_CPK",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"className", "classPK"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCN_CPK",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"className", "classPK"}, 0, 1, false, null),
				_SQL_SELECT_KALEOINSTANCE_WHERE, _SQL_COUNT_KALEOINSTANCE_WHERE,
				KaleoInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"kaleoInstance.", "className", FinderColumn.Type.STRING,
					"=", true, true, KaleoInstance::getClassName),
				new FinderColumn<>(
					"kaleoInstance.", "classPK", FinderColumn.Type.LONG, "=",
					true, true, KaleoInstance::getClassPK));

		_uniquePersistenceFinderByKII_C_U = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByKII_C_U",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"kaleoInstanceId", "companyId", "userId"}, 0, 0,
				false, KaleoInstance::getKaleoInstanceId,
				KaleoInstance::getCompanyId, KaleoInstance::getUserId),
			_SQL_SELECT_KALEOINSTANCE_WHERE, "",
			new FinderColumn<>(
				"kaleoInstance.", "kaleoInstanceId", FinderColumn.Type.LONG,
				"=", true, true, KaleoInstance::getKaleoInstanceId),
			new FinderColumn<>(
				"kaleoInstance.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, KaleoInstance::getCompanyId),
			new FinderColumn<>(
				"kaleoInstance.", "userId", FinderColumn.Type.LONG, "=", true,
				true, KaleoInstance::getUserId));

		_collectionPersistenceFinderByC_KDN_KDV_CD =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByC_KDN_KDV_CD",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Date.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "kaleoDefinitionName",
						"kaleoDefinitionVersion", "completionDate"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByC_KDN_KDV_CD",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Date.class.getName()
					},
					new String[] {
						"companyId", "kaleoDefinitionName",
						"kaleoDefinitionVersion", "completionDate"
					},
					0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByC_KDN_KDV_CD",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Date.class.getName()
					},
					new String[] {
						"companyId", "kaleoDefinitionName",
						"kaleoDefinitionVersion", "completionDate"
					},
					0, 2, false, null),
				_SQL_SELECT_KALEOINSTANCE_WHERE, _SQL_COUNT_KALEOINSTANCE_WHERE,
				KaleoInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"kaleoInstance.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, KaleoInstance::getCompanyId),
				new FinderColumn<>(
					"kaleoInstance.", "kaleoDefinitionName",
					FinderColumn.Type.STRING, "=", true, true,
					KaleoInstance::getKaleoDefinitionName),
				new FinderColumn<>(
					"kaleoInstance.", "kaleoDefinitionVersion",
					FinderColumn.Type.INTEGER, "=", true, true,
					KaleoInstance::getKaleoDefinitionVersion),
				new FinderColumn<>(
					"kaleoInstance.", "completionDate", FinderColumn.Type.DATE,
					"=", true, true, KaleoInstance::getCompletionDate));

		KaleoInstanceUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoInstanceUtil.setPersistence(null);

		entityCache.removeCache(KaleoInstanceImpl.class.getName());
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
		KaleoInstanceModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KALEOINSTANCE =
		"SELECT kaleoInstance FROM KaleoInstance kaleoInstance";

	private static final String _SQL_SELECT_KALEOINSTANCE_WHERE =
		"SELECT kaleoInstance FROM KaleoInstance kaleoInstance WHERE ";

	private static final String _SQL_COUNT_KALEOINSTANCE_WHERE =
		"SELECT COUNT(kaleoInstance) FROM KaleoInstance kaleoInstance WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No KaleoInstance exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoInstancePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"active"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1768362675