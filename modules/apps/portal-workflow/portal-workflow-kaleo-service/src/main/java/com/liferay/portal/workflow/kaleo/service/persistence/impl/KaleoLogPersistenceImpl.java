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
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.workflow.kaleo.exception.NoSuchLogException;
import com.liferay.portal.workflow.kaleo.model.KaleoLog;
import com.liferay.portal.workflow.kaleo.model.KaleoLogTable;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoLogImpl;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoLogModelImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoLogPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoLogUtil;
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
 * The persistence implementation for the kaleo log service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KaleoLogPersistence.class)
public class KaleoLogPersistenceImpl
	extends BasePersistenceImpl<KaleoLog, NoSuchLogException>
	implements KaleoLogPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoLogUtil</code> to access the kaleo log persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoLogImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<KaleoLog, NoSuchLogException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the kaleo logs where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoLogModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo logs
	 * @param end the upper bound of the range of kaleo logs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo logs
	 */
	@Override
	public List<KaleoLog> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoLog> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo log in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo log
	 * @throws NoSuchLogException if a matching kaleo log could not be found
	 */
	@Override
	public KaleoLog findByCompanyId_First(
			long companyId, OrderByComparator<KaleoLog> orderByComparator)
		throws NoSuchLogException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo log in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo log, or <code>null</code> if a matching kaleo log could not be found
	 */
	@Override
	public KaleoLog fetchByCompanyId_First(
		long companyId, OrderByComparator<KaleoLog> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo logs where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of kaleo logs where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo logs
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder<KaleoLog, NoSuchLogException>
		_collectionPersistenceFinderByKaleoDefinitionVersionId;

	/**
	 * Returns an ordered range of all the kaleo logs where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoLogModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo logs
	 * @param end the upper bound of the range of kaleo logs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo logs
	 */
	@Override
	public List<KaleoLog> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end,
		OrderByComparator<KaleoLog> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.find(
			finderCache, new Object[] {kaleoDefinitionVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo log in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo log
	 * @throws NoSuchLogException if a matching kaleo log could not be found
	 */
	@Override
	public KaleoLog findByKaleoDefinitionVersionId_First(
			long kaleoDefinitionVersionId,
			OrderByComparator<KaleoLog> orderByComparator)
		throws NoSuchLogException {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.findFirst(
			finderCache, new Object[] {kaleoDefinitionVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo log in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo log, or <code>null</code> if a matching kaleo log could not be found
	 */
	@Override
	public KaleoLog fetchByKaleoDefinitionVersionId_First(
		long kaleoDefinitionVersionId,
		OrderByComparator<KaleoLog> orderByComparator) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.
			fetchFirst(
				finderCache, new Object[] {kaleoDefinitionVersionId},
				orderByComparator);
	}

	/**
	 * Removes all the kaleo logs where kaleoDefinitionVersionId = &#63; from the database.
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
	 * Returns the number of kaleo logs where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo logs
	 */
	@Override
	public int countByKaleoDefinitionVersionId(long kaleoDefinitionVersionId) {
		return _collectionPersistenceFinderByKaleoDefinitionVersionId.count(
			finderCache, new Object[] {kaleoDefinitionVersionId});
	}

	private CollectionPersistenceFinder<KaleoLog, NoSuchLogException>
		_collectionPersistenceFinderByKaleoInstanceId;

	/**
	 * Returns an ordered range of all the kaleo logs where kaleoInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoLogModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param start the lower bound of the range of kaleo logs
	 * @param end the upper bound of the range of kaleo logs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo logs
	 */
	@Override
	public List<KaleoLog> findByKaleoInstanceId(
		long kaleoInstanceId, int start, int end,
		OrderByComparator<KaleoLog> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoInstanceId.find(
			finderCache, new Object[] {kaleoInstanceId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo log in the ordered set where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo log
	 * @throws NoSuchLogException if a matching kaleo log could not be found
	 */
	@Override
	public KaleoLog findByKaleoInstanceId_First(
			long kaleoInstanceId, OrderByComparator<KaleoLog> orderByComparator)
		throws NoSuchLogException {

		return _collectionPersistenceFinderByKaleoInstanceId.findFirst(
			finderCache, new Object[] {kaleoInstanceId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo log in the ordered set where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo log, or <code>null</code> if a matching kaleo log could not be found
	 */
	@Override
	public KaleoLog fetchByKaleoInstanceId_First(
		long kaleoInstanceId, OrderByComparator<KaleoLog> orderByComparator) {

		return _collectionPersistenceFinderByKaleoInstanceId.fetchFirst(
			finderCache, new Object[] {kaleoInstanceId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo logs where kaleoInstanceId = &#63; from the database.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 */
	@Override
	public void removeByKaleoInstanceId(long kaleoInstanceId) {
		_collectionPersistenceFinderByKaleoInstanceId.remove(
			finderCache, new Object[] {kaleoInstanceId});
	}

	/**
	 * Returns the number of kaleo logs where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @return the number of matching kaleo logs
	 */
	@Override
	public int countByKaleoInstanceId(long kaleoInstanceId) {
		return _collectionPersistenceFinderByKaleoInstanceId.count(
			finderCache, new Object[] {kaleoInstanceId});
	}

	private CollectionPersistenceFinder<KaleoLog, NoSuchLogException>
		_collectionPersistenceFinderByKaleoTaskInstanceTokenId;

	/**
	 * Returns an ordered range of all the kaleo logs where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoLogModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param start the lower bound of the range of kaleo logs
	 * @param end the upper bound of the range of kaleo logs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo logs
	 */
	@Override
	public List<KaleoLog> findByKaleoTaskInstanceTokenId(
		long kaleoTaskInstanceTokenId, int start, int end,
		OrderByComparator<KaleoLog> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoTaskInstanceTokenId.find(
			finderCache, new Object[] {kaleoTaskInstanceTokenId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo log in the ordered set where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo log
	 * @throws NoSuchLogException if a matching kaleo log could not be found
	 */
	@Override
	public KaleoLog findByKaleoTaskInstanceTokenId_First(
			long kaleoTaskInstanceTokenId,
			OrderByComparator<KaleoLog> orderByComparator)
		throws NoSuchLogException {

		return _collectionPersistenceFinderByKaleoTaskInstanceTokenId.findFirst(
			finderCache, new Object[] {kaleoTaskInstanceTokenId},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo log in the ordered set where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo log, or <code>null</code> if a matching kaleo log could not be found
	 */
	@Override
	public KaleoLog fetchByKaleoTaskInstanceTokenId_First(
		long kaleoTaskInstanceTokenId,
		OrderByComparator<KaleoLog> orderByComparator) {

		return _collectionPersistenceFinderByKaleoTaskInstanceTokenId.
			fetchFirst(
				finderCache, new Object[] {kaleoTaskInstanceTokenId},
				orderByComparator);
	}

	/**
	 * Removes all the kaleo logs where kaleoTaskInstanceTokenId = &#63; from the database.
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
	 * Returns the number of kaleo logs where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @return the number of matching kaleo logs
	 */
	@Override
	public int countByKaleoTaskInstanceTokenId(long kaleoTaskInstanceTokenId) {
		return _collectionPersistenceFinderByKaleoTaskInstanceTokenId.count(
			finderCache, new Object[] {kaleoTaskInstanceTokenId});
	}

	private CollectionPersistenceFinder<KaleoLog, NoSuchLogException>
		_collectionPersistenceFinderByKITI_T;

	/**
	 * Returns an ordered range of all the kaleo logs where kaleoInstanceTokenId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoLogModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param type the type
	 * @param start the lower bound of the range of kaleo logs
	 * @param end the upper bound of the range of kaleo logs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo logs
	 */
	@Override
	public List<KaleoLog> findByKITI_T(
		long kaleoInstanceTokenId, String type, int start, int end,
		OrderByComparator<KaleoLog> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByKITI_T.find(
			finderCache, new Object[] {kaleoInstanceTokenId, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo log in the ordered set where kaleoInstanceTokenId = &#63; and type = &#63;.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo log
	 * @throws NoSuchLogException if a matching kaleo log could not be found
	 */
	@Override
	public KaleoLog findByKITI_T_First(
			long kaleoInstanceTokenId, String type,
			OrderByComparator<KaleoLog> orderByComparator)
		throws NoSuchLogException {

		return _collectionPersistenceFinderByKITI_T.findFirst(
			finderCache, new Object[] {kaleoInstanceTokenId, type},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo log in the ordered set where kaleoInstanceTokenId = &#63; and type = &#63;.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo log, or <code>null</code> if a matching kaleo log could not be found
	 */
	@Override
	public KaleoLog fetchByKITI_T_First(
		long kaleoInstanceTokenId, String type,
		OrderByComparator<KaleoLog> orderByComparator) {

		return _collectionPersistenceFinderByKITI_T.fetchFirst(
			finderCache, new Object[] {kaleoInstanceTokenId, type},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo logs where kaleoInstanceTokenId = &#63; and type = &#63; from the database.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param type the type
	 */
	@Override
	public void removeByKITI_T(long kaleoInstanceTokenId, String type) {
		_collectionPersistenceFinderByKITI_T.remove(
			finderCache, new Object[] {kaleoInstanceTokenId, type});
	}

	/**
	 * Returns the number of kaleo logs where kaleoInstanceTokenId = &#63; and type = &#63;.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param type the type
	 * @return the number of matching kaleo logs
	 */
	@Override
	public int countByKITI_T(long kaleoInstanceTokenId, String type) {
		return _collectionPersistenceFinderByKITI_T.count(
			finderCache, new Object[] {kaleoInstanceTokenId, type});
	}

	private CollectionPersistenceFinder<KaleoLog, NoSuchLogException>
		_collectionPersistenceFinderByKCN_KCPK_KITI_T;

	/**
	 * Returns an ordered range of all the kaleo logs where kaleoClassName = &#63; and kaleoClassPK = &#63; and kaleoInstanceTokenId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoLogModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param type the type
	 * @param start the lower bound of the range of kaleo logs
	 * @param end the upper bound of the range of kaleo logs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo logs
	 */
	@Override
	public List<KaleoLog> findByKCN_KCPK_KITI_T(
		String kaleoClassName, long kaleoClassPK, long kaleoInstanceTokenId,
		String type, int start, int end,
		OrderByComparator<KaleoLog> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByKCN_KCPK_KITI_T.find(
			finderCache,
			new Object[] {
				kaleoClassName, kaleoClassPK, kaleoInstanceTokenId, type
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo log in the ordered set where kaleoClassName = &#63; and kaleoClassPK = &#63; and kaleoInstanceTokenId = &#63; and type = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo log
	 * @throws NoSuchLogException if a matching kaleo log could not be found
	 */
	@Override
	public KaleoLog findByKCN_KCPK_KITI_T_First(
			String kaleoClassName, long kaleoClassPK, long kaleoInstanceTokenId,
			String type, OrderByComparator<KaleoLog> orderByComparator)
		throws NoSuchLogException {

		return _collectionPersistenceFinderByKCN_KCPK_KITI_T.findFirst(
			finderCache,
			new Object[] {
				kaleoClassName, kaleoClassPK, kaleoInstanceTokenId, type
			},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo log in the ordered set where kaleoClassName = &#63; and kaleoClassPK = &#63; and kaleoInstanceTokenId = &#63; and type = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo log, or <code>null</code> if a matching kaleo log could not be found
	 */
	@Override
	public KaleoLog fetchByKCN_KCPK_KITI_T_First(
		String kaleoClassName, long kaleoClassPK, long kaleoInstanceTokenId,
		String type, OrderByComparator<KaleoLog> orderByComparator) {

		return _collectionPersistenceFinderByKCN_KCPK_KITI_T.fetchFirst(
			finderCache,
			new Object[] {
				kaleoClassName, kaleoClassPK, kaleoInstanceTokenId, type
			},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo logs where kaleoClassName = &#63; and kaleoClassPK = &#63; and kaleoInstanceTokenId = &#63; and type = &#63; from the database.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param type the type
	 */
	@Override
	public void removeByKCN_KCPK_KITI_T(
		String kaleoClassName, long kaleoClassPK, long kaleoInstanceTokenId,
		String type) {

		_collectionPersistenceFinderByKCN_KCPK_KITI_T.remove(
			finderCache,
			new Object[] {
				kaleoClassName, kaleoClassPK, kaleoInstanceTokenId, type
			});
	}

	/**
	 * Returns the number of kaleo logs where kaleoClassName = &#63; and kaleoClassPK = &#63; and kaleoInstanceTokenId = &#63; and type = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param type the type
	 * @return the number of matching kaleo logs
	 */
	@Override
	public int countByKCN_KCPK_KITI_T(
		String kaleoClassName, long kaleoClassPK, long kaleoInstanceTokenId,
		String type) {

		return _collectionPersistenceFinderByKCN_KCPK_KITI_T.count(
			finderCache,
			new Object[] {
				kaleoClassName, kaleoClassPK, kaleoInstanceTokenId, type
			});
	}

	public KaleoLogPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");
		dbColumnNames.put("comment", "comment_");

		setDBColumnNames(dbColumnNames);

		setModelClass(KaleoLog.class);

		setModelImplClass(KaleoLogImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoLogTable.INSTANCE);
	}

	/**
	 * Creates a new kaleo log with the primary key. Does not add the kaleo log to the database.
	 *
	 * @param kaleoLogId the primary key for the new kaleo log
	 * @return the new kaleo log
	 */
	@Override
	public KaleoLog create(long kaleoLogId) {
		KaleoLog kaleoLog = new KaleoLogImpl();

		kaleoLog.setNew(true);
		kaleoLog.setPrimaryKey(kaleoLogId);

		kaleoLog.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kaleoLog;
	}

	/**
	 * Removes the kaleo log with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoLogId the primary key of the kaleo log
	 * @return the kaleo log that was removed
	 * @throws NoSuchLogException if a kaleo log with the primary key could not be found
	 */
	@Override
	public KaleoLog remove(long kaleoLogId) throws NoSuchLogException {
		return remove((Serializable)kaleoLogId);
	}

	@Override
	protected KaleoLog removeImpl(KaleoLog kaleoLog) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoLog)) {
				kaleoLog = (KaleoLog)session.get(
					KaleoLogImpl.class, kaleoLog.getPrimaryKeyObj());
			}

			if ((kaleoLog != null) && ctPersistenceHelper.isRemove(kaleoLog)) {
				session.delete(kaleoLog);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoLog != null) {
			clearCache(kaleoLog);
		}

		return kaleoLog;
	}

	@Override
	public KaleoLog updateImpl(KaleoLog kaleoLog) {
		boolean isNew = kaleoLog.isNew();

		if (!(kaleoLog instanceof KaleoLogModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kaleoLog.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(kaleoLog);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoLog proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoLog implementation " +
					kaleoLog.getClass());
		}

		KaleoLogModelImpl kaleoLogModelImpl = (KaleoLogModelImpl)kaleoLog;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kaleoLog.getCreateDate() == null)) {
			if (serviceContext == null) {
				kaleoLog.setCreateDate(date);
			}
			else {
				kaleoLog.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!kaleoLogModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kaleoLog.setModifiedDate(date);
			}
			else {
				kaleoLog.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kaleoLog)) {
				if (!isNew) {
					session.evict(
						KaleoLogImpl.class, kaleoLog.getPrimaryKeyObj());
				}

				session.save(kaleoLog);
			}
			else {
				kaleoLog = (KaleoLog)session.merge(kaleoLog);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kaleoLog, false);

		if (isNew) {
			kaleoLog.setNew(false);
		}

		kaleoLog.resetOriginalValues();

		return kaleoLog;
	}

	/**
	 * Returns the kaleo log with the primary key or throws a <code>NoSuchLogException</code> if it could not be found.
	 *
	 * @param kaleoLogId the primary key of the kaleo log
	 * @return the kaleo log
	 * @throws NoSuchLogException if a kaleo log with the primary key could not be found
	 */
	@Override
	public KaleoLog findByPrimaryKey(long kaleoLogId)
		throws NoSuchLogException {

		return findByPrimaryKey((Serializable)kaleoLogId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kaleo log with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoLogId the primary key of the kaleo log
	 * @return the kaleo log, or <code>null</code> if a kaleo log with the primary key could not be found
	 */
	@Override
	public KaleoLog fetchByPrimaryKey(long kaleoLogId) {
		return fetchByPrimaryKey((Serializable)kaleoLogId);
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
		return "kaleoLogId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEOLOG;
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
		return KaleoLogModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KaleoLog";
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
		ctMergeColumnNames.add("kaleoInstanceId");
		ctMergeColumnNames.add("kaleoInstanceTokenId");
		ctMergeColumnNames.add("kaleoTaskInstanceTokenId");
		ctMergeColumnNames.add("kaleoNodeName");
		ctMergeColumnNames.add("terminalKaleoNode");
		ctMergeColumnNames.add("kaleoActionId");
		ctMergeColumnNames.add("kaleoActionName");
		ctMergeColumnNames.add("kaleoActionDescription");
		ctMergeColumnNames.add("previousKaleoNodeId");
		ctMergeColumnNames.add("previousKaleoNodeName");
		ctMergeColumnNames.add("previousAssigneeClassName");
		ctMergeColumnNames.add("previousAssigneeClassPK");
		ctMergeColumnNames.add("currentAssigneeClassName");
		ctMergeColumnNames.add("currentAssigneeClassPK");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("comment_");
		ctMergeColumnNames.add("startDate");
		ctMergeColumnNames.add("endDate");
		ctMergeColumnNames.add("duration");
		ctMergeColumnNames.add("workflowContext");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("kaleoLogId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the kaleo log persistence.
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
				_SQL_SELECT_KALEOLOG_WHERE, _SQL_COUNT_KALEOLOG_WHERE,
				KaleoLogModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoLog.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, KaleoLog::getCompanyId));

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
				_SQL_SELECT_KALEOLOG_WHERE, _SQL_COUNT_KALEOLOG_WHERE,
				KaleoLogModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoLog.", "kaleoDefinitionVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoLog::getKaleoDefinitionVersionId));

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
				_SQL_SELECT_KALEOLOG_WHERE, _SQL_COUNT_KALEOLOG_WHERE,
				KaleoLogModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoLog.", "kaleoInstanceId", FinderColumn.Type.LONG, "=",
					true, true, KaleoLog::getKaleoInstanceId));

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
				_SQL_SELECT_KALEOLOG_WHERE, _SQL_COUNT_KALEOLOG_WHERE,
				KaleoLogModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoLog.", "kaleoTaskInstanceTokenId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoLog::getKaleoTaskInstanceTokenId));

		_collectionPersistenceFinderByKITI_T =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByKITI_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoInstanceTokenId", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByKITI_T",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"kaleoInstanceTokenId", "type_"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByKITI_T",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"kaleoInstanceTokenId", "type_"}, 0, 2, false,
					null),
				_SQL_SELECT_KALEOLOG_WHERE, _SQL_COUNT_KALEOLOG_WHERE,
				KaleoLogModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoLog.", "kaleoInstanceTokenId", FinderColumn.Type.LONG,
					"=", true, true, KaleoLog::getKaleoInstanceTokenId),
				new FinderColumn<>(
					"kaleoLog.", "type", "type_", FinderColumn.Type.STRING, "=",
					true, true, KaleoLog::getType));

		_collectionPersistenceFinderByKCN_KCPK_KITI_T =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByKCN_KCPK_KITI_T",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"kaleoClassName", "kaleoClassPK",
						"kaleoInstanceTokenId", "type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByKCN_KCPK_KITI_T",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName()
					},
					new String[] {
						"kaleoClassName", "kaleoClassPK",
						"kaleoInstanceTokenId", "type_"
					},
					0, 9, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKCN_KCPK_KITI_T",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName()
					},
					new String[] {
						"kaleoClassName", "kaleoClassPK",
						"kaleoInstanceTokenId", "type_"
					},
					0, 9, false, null),
				_SQL_SELECT_KALEOLOG_WHERE, _SQL_COUNT_KALEOLOG_WHERE,
				KaleoLogModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoLog.", "kaleoClassName", FinderColumn.Type.STRING,
					"=", true, true, KaleoLog::getKaleoClassName),
				new FinderColumn<>(
					"kaleoLog.", "kaleoClassPK", FinderColumn.Type.LONG, "=",
					true, true, KaleoLog::getKaleoClassPK),
				new FinderColumn<>(
					"kaleoLog.", "kaleoInstanceTokenId", FinderColumn.Type.LONG,
					"=", true, true, KaleoLog::getKaleoInstanceTokenId),
				new FinderColumn<>(
					"kaleoLog.", "type", "type_", FinderColumn.Type.STRING, "=",
					true, true, KaleoLog::getType));

		KaleoLogUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoLogUtil.setPersistence(null);

		entityCache.removeCache(KaleoLogImpl.class.getName());
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
		KaleoLogModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KALEOLOG =
		"SELECT kaleoLog FROM KaleoLog kaleoLog";

	private static final String _SQL_SELECT_KALEOLOG_WHERE =
		"SELECT kaleoLog FROM KaleoLog kaleoLog WHERE ";

	private static final String _SQL_COUNT_KALEOLOG_WHERE =
		"SELECT COUNT(kaleoLog) FROM KaleoLog kaleoLog WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No KaleoLog exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type", "comment"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1725502787