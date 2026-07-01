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
import com.liferay.portal.workflow.kaleo.exception.NoSuchActionException;
import com.liferay.portal.workflow.kaleo.model.KaleoAction;
import com.liferay.portal.workflow.kaleo.model.KaleoActionTable;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoActionImpl;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoActionModelImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoActionPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoActionUtil;
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
 * The persistence implementation for the kaleo action service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KaleoActionPersistence.class)
public class KaleoActionPersistenceImpl
	extends BasePersistenceImpl<KaleoAction, NoSuchActionException>
	implements KaleoActionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoActionUtil</code> to access the kaleo action persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoActionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<KaleoAction, NoSuchActionException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the kaleo actions where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoActionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo actions
	 * @param end the upper bound of the range of kaleo actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo actions
	 */
	@Override
	public List<KaleoAction> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoAction> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo action in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo action
	 * @throws NoSuchActionException if a matching kaleo action could not be found
	 */
	@Override
	public KaleoAction findByCompanyId_First(
			long companyId, OrderByComparator<KaleoAction> orderByComparator)
		throws NoSuchActionException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo action in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo action, or <code>null</code> if a matching kaleo action could not be found
	 */
	@Override
	public KaleoAction fetchByCompanyId_First(
		long companyId, OrderByComparator<KaleoAction> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo actions where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of kaleo actions where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo actions
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder<KaleoAction, NoSuchActionException>
		_collectionPersistenceFinderByKaleoDefinitionVersionId;

	/**
	 * Returns an ordered range of all the kaleo actions where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoActionModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo actions
	 * @param end the upper bound of the range of kaleo actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo actions
	 */
	@Override
	public List<KaleoAction> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end,
		OrderByComparator<KaleoAction> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.find(
			finderCache, new Object[] {kaleoDefinitionVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo action in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo action
	 * @throws NoSuchActionException if a matching kaleo action could not be found
	 */
	@Override
	public KaleoAction findByKaleoDefinitionVersionId_First(
			long kaleoDefinitionVersionId,
			OrderByComparator<KaleoAction> orderByComparator)
		throws NoSuchActionException {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.findFirst(
			finderCache, new Object[] {kaleoDefinitionVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo action in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo action, or <code>null</code> if a matching kaleo action could not be found
	 */
	@Override
	public KaleoAction fetchByKaleoDefinitionVersionId_First(
		long kaleoDefinitionVersionId,
		OrderByComparator<KaleoAction> orderByComparator) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.
			fetchFirst(
				finderCache, new Object[] {kaleoDefinitionVersionId},
				orderByComparator);
	}

	/**
	 * Removes all the kaleo actions where kaleoDefinitionVersionId = &#63; from the database.
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
	 * Returns the number of kaleo actions where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo actions
	 */
	@Override
	public int countByKaleoDefinitionVersionId(long kaleoDefinitionVersionId) {
		return _collectionPersistenceFinderByKaleoDefinitionVersionId.count(
			finderCache, new Object[] {kaleoDefinitionVersionId});
	}

	private CollectionPersistenceFinder<KaleoAction, NoSuchActionException>
		_collectionPersistenceFinderByKCN_KCPK;

	/**
	 * Returns an ordered range of all the kaleo actions where kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoActionModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param start the lower bound of the range of kaleo actions
	 * @param end the upper bound of the range of kaleo actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo actions
	 */
	@Override
	public List<KaleoAction> findByKCN_KCPK(
		String kaleoClassName, long kaleoClassPK, int start, int end,
		OrderByComparator<KaleoAction> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKCN_KCPK.find(
			finderCache, new Object[] {kaleoClassName, kaleoClassPK}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo action in the ordered set where kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo action
	 * @throws NoSuchActionException if a matching kaleo action could not be found
	 */
	@Override
	public KaleoAction findByKCN_KCPK_First(
			String kaleoClassName, long kaleoClassPK,
			OrderByComparator<KaleoAction> orderByComparator)
		throws NoSuchActionException {

		return _collectionPersistenceFinderByKCN_KCPK.findFirst(
			finderCache, new Object[] {kaleoClassName, kaleoClassPK},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo action in the ordered set where kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo action, or <code>null</code> if a matching kaleo action could not be found
	 */
	@Override
	public KaleoAction fetchByKCN_KCPK_First(
		String kaleoClassName, long kaleoClassPK,
		OrderByComparator<KaleoAction> orderByComparator) {

		return _collectionPersistenceFinderByKCN_KCPK.fetchFirst(
			finderCache, new Object[] {kaleoClassName, kaleoClassPK},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo actions where kaleoClassName = &#63; and kaleoClassPK = &#63; from the database.
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
	 * Returns the number of kaleo actions where kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @return the number of matching kaleo actions
	 */
	@Override
	public int countByKCN_KCPK(String kaleoClassName, long kaleoClassPK) {
		return _collectionPersistenceFinderByKCN_KCPK.count(
			finderCache, new Object[] {kaleoClassName, kaleoClassPK});
	}

	private CollectionPersistenceFinder<KaleoAction, NoSuchActionException>
		_collectionPersistenceFinderByKCN_KDVI;

	/**
	 * Returns an ordered range of all the kaleo actions where kaleoClassName = &#63; and kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoActionModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo actions
	 * @param end the upper bound of the range of kaleo actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo actions
	 */
	@Override
	public List<KaleoAction> findByKCN_KDVI(
		String kaleoClassName, long kaleoDefinitionVersionId, int start,
		int end, OrderByComparator<KaleoAction> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKCN_KDVI.find(
			finderCache,
			new Object[] {kaleoClassName, kaleoDefinitionVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo action in the ordered set where kaleoClassName = &#63; and kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo action
	 * @throws NoSuchActionException if a matching kaleo action could not be found
	 */
	@Override
	public KaleoAction findByKCN_KDVI_First(
			String kaleoClassName, long kaleoDefinitionVersionId,
			OrderByComparator<KaleoAction> orderByComparator)
		throws NoSuchActionException {

		return _collectionPersistenceFinderByKCN_KDVI.findFirst(
			finderCache,
			new Object[] {kaleoClassName, kaleoDefinitionVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo action in the ordered set where kaleoClassName = &#63; and kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo action, or <code>null</code> if a matching kaleo action could not be found
	 */
	@Override
	public KaleoAction fetchByKCN_KDVI_First(
		String kaleoClassName, long kaleoDefinitionVersionId,
		OrderByComparator<KaleoAction> orderByComparator) {

		return _collectionPersistenceFinderByKCN_KDVI.fetchFirst(
			finderCache,
			new Object[] {kaleoClassName, kaleoDefinitionVersionId},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo actions where kaleoClassName = &#63; and kaleoDefinitionVersionId = &#63; from the database.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 */
	@Override
	public void removeByKCN_KDVI(
		String kaleoClassName, long kaleoDefinitionVersionId) {

		_collectionPersistenceFinderByKCN_KDVI.remove(
			finderCache,
			new Object[] {kaleoClassName, kaleoDefinitionVersionId});
	}

	/**
	 * Returns the number of kaleo actions where kaleoClassName = &#63; and kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo actions
	 */
	@Override
	public int countByKCN_KDVI(
		String kaleoClassName, long kaleoDefinitionVersionId) {

		return _collectionPersistenceFinderByKCN_KDVI.count(
			finderCache,
			new Object[] {kaleoClassName, kaleoDefinitionVersionId});
	}

	private CollectionPersistenceFinder<KaleoAction, NoSuchActionException>
		_collectionPersistenceFinderByC_KCN_KCPK;

	/**
	 * Returns an ordered range of all the kaleo actions where companyId = &#63; and kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoActionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param start the lower bound of the range of kaleo actions
	 * @param end the upper bound of the range of kaleo actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo actions
	 */
	@Override
	public List<KaleoAction> findByC_KCN_KCPK(
		long companyId, String kaleoClassName, long kaleoClassPK, int start,
		int end, OrderByComparator<KaleoAction> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_KCN_KCPK.find(
			finderCache, new Object[] {companyId, kaleoClassName, kaleoClassPK},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo action in the ordered set where companyId = &#63; and kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo action
	 * @throws NoSuchActionException if a matching kaleo action could not be found
	 */
	@Override
	public KaleoAction findByC_KCN_KCPK_First(
			long companyId, String kaleoClassName, long kaleoClassPK,
			OrderByComparator<KaleoAction> orderByComparator)
		throws NoSuchActionException {

		return _collectionPersistenceFinderByC_KCN_KCPK.findFirst(
			finderCache, new Object[] {companyId, kaleoClassName, kaleoClassPK},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo action in the ordered set where companyId = &#63; and kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo action, or <code>null</code> if a matching kaleo action could not be found
	 */
	@Override
	public KaleoAction fetchByC_KCN_KCPK_First(
		long companyId, String kaleoClassName, long kaleoClassPK,
		OrderByComparator<KaleoAction> orderByComparator) {

		return _collectionPersistenceFinderByC_KCN_KCPK.fetchFirst(
			finderCache, new Object[] {companyId, kaleoClassName, kaleoClassPK},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo actions where companyId = &#63; and kaleoClassName = &#63; and kaleoClassPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 */
	@Override
	public void removeByC_KCN_KCPK(
		long companyId, String kaleoClassName, long kaleoClassPK) {

		_collectionPersistenceFinderByC_KCN_KCPK.remove(
			finderCache,
			new Object[] {companyId, kaleoClassName, kaleoClassPK});
	}

	/**
	 * Returns the number of kaleo actions where companyId = &#63; and kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @return the number of matching kaleo actions
	 */
	@Override
	public int countByC_KCN_KCPK(
		long companyId, String kaleoClassName, long kaleoClassPK) {

		return _collectionPersistenceFinderByC_KCN_KCPK.count(
			finderCache,
			new Object[] {companyId, kaleoClassName, kaleoClassPK});
	}

	public KaleoActionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(KaleoAction.class);

		setModelImplClass(KaleoActionImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoActionTable.INSTANCE);
	}

	/**
	 * Creates a new kaleo action with the primary key. Does not add the kaleo action to the database.
	 *
	 * @param kaleoActionId the primary key for the new kaleo action
	 * @return the new kaleo action
	 */
	@Override
	public KaleoAction create(long kaleoActionId) {
		KaleoAction kaleoAction = new KaleoActionImpl();

		kaleoAction.setNew(true);
		kaleoAction.setPrimaryKey(kaleoActionId);

		kaleoAction.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kaleoAction;
	}

	/**
	 * Removes the kaleo action with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoActionId the primary key of the kaleo action
	 * @return the kaleo action that was removed
	 * @throws NoSuchActionException if a kaleo action with the primary key could not be found
	 */
	@Override
	public KaleoAction remove(long kaleoActionId) throws NoSuchActionException {
		return remove((Serializable)kaleoActionId);
	}

	@Override
	protected KaleoAction removeImpl(KaleoAction kaleoAction) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoAction)) {
				kaleoAction = (KaleoAction)session.get(
					KaleoActionImpl.class, kaleoAction.getPrimaryKeyObj());
			}

			if ((kaleoAction != null) &&
				ctPersistenceHelper.isRemove(kaleoAction)) {

				session.delete(kaleoAction);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoAction != null) {
			clearCache(kaleoAction);
		}

		return kaleoAction;
	}

	@Override
	public KaleoAction updateImpl(KaleoAction kaleoAction) {
		boolean isNew = kaleoAction.isNew();

		if (!(kaleoAction instanceof KaleoActionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kaleoAction.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(kaleoAction);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoAction proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoAction implementation " +
					kaleoAction.getClass());
		}

		KaleoActionModelImpl kaleoActionModelImpl =
			(KaleoActionModelImpl)kaleoAction;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kaleoAction.getCreateDate() == null)) {
			if (serviceContext == null) {
				kaleoAction.setCreateDate(date);
			}
			else {
				kaleoAction.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!kaleoActionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kaleoAction.setModifiedDate(date);
			}
			else {
				kaleoAction.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kaleoAction)) {
				if (!isNew) {
					session.evict(
						KaleoActionImpl.class, kaleoAction.getPrimaryKeyObj());
				}

				session.save(kaleoAction);
			}
			else {
				kaleoAction = (KaleoAction)session.merge(kaleoAction);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kaleoAction, false);

		if (isNew) {
			kaleoAction.setNew(false);
		}

		kaleoAction.resetOriginalValues();

		return kaleoAction;
	}

	/**
	 * Returns the kaleo action with the primary key or throws a <code>NoSuchActionException</code> if it could not be found.
	 *
	 * @param kaleoActionId the primary key of the kaleo action
	 * @return the kaleo action
	 * @throws NoSuchActionException if a kaleo action with the primary key could not be found
	 */
	@Override
	public KaleoAction findByPrimaryKey(long kaleoActionId)
		throws NoSuchActionException {

		return findByPrimaryKey((Serializable)kaleoActionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kaleo action with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoActionId the primary key of the kaleo action
	 * @return the kaleo action, or <code>null</code> if a kaleo action with the primary key could not be found
	 */
	@Override
	public KaleoAction fetchByPrimaryKey(long kaleoActionId) {
		return fetchByPrimaryKey((Serializable)kaleoActionId);
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
		return "kaleoActionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEOACTION;
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
		return KaleoActionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KaleoAction";
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
		ctMergeColumnNames.add("kaleoNodeName");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("executionType");
		ctMergeColumnNames.add("script");
		ctMergeColumnNames.add("scriptLanguage");
		ctMergeColumnNames.add("scriptRequiredContexts");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("status");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("kaleoActionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the kaleo action persistence.
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
				_SQL_SELECT_KALEOACTION_WHERE, _SQL_COUNT_KALEOACTION_WHERE,
				KaleoActionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"kaleoAction.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, KaleoAction::getCompanyId));

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
				_SQL_SELECT_KALEOACTION_WHERE, _SQL_COUNT_KALEOACTION_WHERE,
				KaleoActionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"kaleoAction.", "kaleoDefinitionVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoAction::getKaleoDefinitionVersionId));

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
				_SQL_SELECT_KALEOACTION_WHERE, _SQL_COUNT_KALEOACTION_WHERE,
				KaleoActionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"kaleoAction.", "kaleoClassName", FinderColumn.Type.STRING,
					"=", true, true, KaleoAction::getKaleoClassName),
				new FinderColumn<>(
					"kaleoAction.", "kaleoClassPK", FinderColumn.Type.LONG, "=",
					true, true, KaleoAction::getKaleoClassPK));

		_collectionPersistenceFinderByKCN_KDVI =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByKCN_KDVI",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoClassName", "kaleoDefinitionVersionId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByKCN_KDVI",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"kaleoClassName", "kaleoDefinitionVersionId"},
					0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKCN_KDVI",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"kaleoClassName", "kaleoDefinitionVersionId"},
					0, 1, false, null),
				_SQL_SELECT_KALEOACTION_WHERE, _SQL_COUNT_KALEOACTION_WHERE,
				KaleoActionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"kaleoAction.", "kaleoClassName", FinderColumn.Type.STRING,
					"=", true, true, KaleoAction::getKaleoClassName),
				new FinderColumn<>(
					"kaleoAction.", "kaleoDefinitionVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoAction::getKaleoDefinitionVersionId));

		_collectionPersistenceFinderByC_KCN_KCPK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_KCN_KCPK",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "kaleoClassName", "kaleoClassPK"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByC_KCN_KCPK",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"companyId", "kaleoClassName", "kaleoClassPK"
					},
					0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByC_KCN_KCPK",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"companyId", "kaleoClassName", "kaleoClassPK"
					},
					0, 2, false, null),
				_SQL_SELECT_KALEOACTION_WHERE, _SQL_COUNT_KALEOACTION_WHERE,
				KaleoActionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"kaleoAction.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, KaleoAction::getCompanyId),
				new FinderColumn<>(
					"kaleoAction.", "kaleoClassName", FinderColumn.Type.STRING,
					"=", true, true, KaleoAction::getKaleoClassName),
				new FinderColumn<>(
					"kaleoAction.", "kaleoClassPK", FinderColumn.Type.LONG, "=",
					true, true, KaleoAction::getKaleoClassPK));

		KaleoActionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoActionUtil.setPersistence(null);

		entityCache.removeCache(KaleoActionImpl.class.getName());
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
		KaleoActionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KALEOACTION =
		"SELECT kaleoAction FROM KaleoAction kaleoAction";

	private static final String _SQL_SELECT_KALEOACTION_WHERE =
		"SELECT kaleoAction FROM KaleoAction kaleoAction WHERE ";

	private static final String _SQL_COUNT_KALEOACTION_WHERE =
		"SELECT COUNT(kaleoAction) FROM KaleoAction kaleoAction WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1931740299