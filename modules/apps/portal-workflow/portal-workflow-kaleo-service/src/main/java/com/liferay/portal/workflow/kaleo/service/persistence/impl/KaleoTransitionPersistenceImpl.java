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
import com.liferay.portal.workflow.kaleo.exception.NoSuchTransitionException;
import com.liferay.portal.workflow.kaleo.model.KaleoTransition;
import com.liferay.portal.workflow.kaleo.model.KaleoTransitionTable;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoTransitionImpl;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoTransitionModelImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTransitionPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTransitionUtil;
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
 * The persistence implementation for the kaleo transition service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KaleoTransitionPersistence.class)
public class KaleoTransitionPersistenceImpl
	extends BasePersistenceImpl<KaleoTransition, NoSuchTransitionException>
	implements KaleoTransitionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoTransitionUtil</code> to access the kaleo transition persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoTransitionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<KaleoTransition, NoSuchTransitionException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the kaleo transitions where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTransitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo transitions
	 * @param end the upper bound of the range of kaleo transitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo transitions
	 */
	@Override
	public List<KaleoTransition> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoTransition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo transition in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo transition
	 * @throws NoSuchTransitionException if a matching kaleo transition could not be found
	 */
	@Override
	public KaleoTransition findByCompanyId_First(
			long companyId,
			OrderByComparator<KaleoTransition> orderByComparator)
		throws NoSuchTransitionException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo transition in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo transition, or <code>null</code> if a matching kaleo transition could not be found
	 */
	@Override
	public KaleoTransition fetchByCompanyId_First(
		long companyId, OrderByComparator<KaleoTransition> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo transitions where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of kaleo transitions where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo transitions
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<KaleoTransition, NoSuchTransitionException>
			_collectionPersistenceFinderByKaleoDefinitionVersionId;

	/**
	 * Returns an ordered range of all the kaleo transitions where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTransitionModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo transitions
	 * @param end the upper bound of the range of kaleo transitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo transitions
	 */
	@Override
	public List<KaleoTransition> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end,
		OrderByComparator<KaleoTransition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.find(
			finderCache, new Object[] {kaleoDefinitionVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo transition in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo transition
	 * @throws NoSuchTransitionException if a matching kaleo transition could not be found
	 */
	@Override
	public KaleoTransition findByKaleoDefinitionVersionId_First(
			long kaleoDefinitionVersionId,
			OrderByComparator<KaleoTransition> orderByComparator)
		throws NoSuchTransitionException {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.findFirst(
			finderCache, new Object[] {kaleoDefinitionVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo transition in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo transition, or <code>null</code> if a matching kaleo transition could not be found
	 */
	@Override
	public KaleoTransition fetchByKaleoDefinitionVersionId_First(
		long kaleoDefinitionVersionId,
		OrderByComparator<KaleoTransition> orderByComparator) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.
			fetchFirst(
				finderCache, new Object[] {kaleoDefinitionVersionId},
				orderByComparator);
	}

	/**
	 * Removes all the kaleo transitions where kaleoDefinitionVersionId = &#63; from the database.
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
	 * Returns the number of kaleo transitions where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo transitions
	 */
	@Override
	public int countByKaleoDefinitionVersionId(long kaleoDefinitionVersionId) {
		return _collectionPersistenceFinderByKaleoDefinitionVersionId.count(
			finderCache, new Object[] {kaleoDefinitionVersionId});
	}

	private CollectionPersistenceFinder
		<KaleoTransition, NoSuchTransitionException>
			_collectionPersistenceFinderByKaleoNodeId;

	/**
	 * Returns an ordered range of all the kaleo transitions where kaleoNodeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTransitionModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param start the lower bound of the range of kaleo transitions
	 * @param end the upper bound of the range of kaleo transitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo transitions
	 */
	@Override
	public List<KaleoTransition> findByKaleoNodeId(
		long kaleoNodeId, int start, int end,
		OrderByComparator<KaleoTransition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoNodeId.find(
			finderCache, new Object[] {kaleoNodeId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo transition in the ordered set where kaleoNodeId = &#63;.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo transition
	 * @throws NoSuchTransitionException if a matching kaleo transition could not be found
	 */
	@Override
	public KaleoTransition findByKaleoNodeId_First(
			long kaleoNodeId,
			OrderByComparator<KaleoTransition> orderByComparator)
		throws NoSuchTransitionException {

		return _collectionPersistenceFinderByKaleoNodeId.findFirst(
			finderCache, new Object[] {kaleoNodeId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo transition in the ordered set where kaleoNodeId = &#63;.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo transition, or <code>null</code> if a matching kaleo transition could not be found
	 */
	@Override
	public KaleoTransition fetchByKaleoNodeId_First(
		long kaleoNodeId,
		OrderByComparator<KaleoTransition> orderByComparator) {

		return _collectionPersistenceFinderByKaleoNodeId.fetchFirst(
			finderCache, new Object[] {kaleoNodeId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo transitions where kaleoNodeId = &#63; from the database.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 */
	@Override
	public void removeByKaleoNodeId(long kaleoNodeId) {
		_collectionPersistenceFinderByKaleoNodeId.remove(
			finderCache, new Object[] {kaleoNodeId});
	}

	/**
	 * Returns the number of kaleo transitions where kaleoNodeId = &#63;.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @return the number of matching kaleo transitions
	 */
	@Override
	public int countByKaleoNodeId(long kaleoNodeId) {
		return _collectionPersistenceFinderByKaleoNodeId.count(
			finderCache, new Object[] {kaleoNodeId});
	}

	private UniquePersistenceFinder<KaleoTransition, NoSuchTransitionException>
		_uniquePersistenceFinderByKNI_N;

	/**
	 * Returns the kaleo transition where kaleoNodeId = &#63; and name = &#63; or throws a <code>NoSuchTransitionException</code> if it could not be found.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param name the name
	 * @return the matching kaleo transition
	 * @throws NoSuchTransitionException if a matching kaleo transition could not be found
	 */
	@Override
	public KaleoTransition findByKNI_N(long kaleoNodeId, String name)
		throws NoSuchTransitionException {

		return _uniquePersistenceFinderByKNI_N.find(
			finderCache, new Object[] {kaleoNodeId, name});
	}

	/**
	 * Returns the kaleo transition where kaleoNodeId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo transition, or <code>null</code> if a matching kaleo transition could not be found
	 */
	@Override
	public KaleoTransition fetchByKNI_N(
		long kaleoNodeId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByKNI_N.fetch(
			finderCache, new Object[] {kaleoNodeId, name}, useFinderCache);
	}

	/**
	 * Removes the kaleo transition where kaleoNodeId = &#63; and name = &#63; from the database.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param name the name
	 * @return the kaleo transition that was removed
	 */
	@Override
	public KaleoTransition removeByKNI_N(long kaleoNodeId, String name)
		throws NoSuchTransitionException {

		KaleoTransition kaleoTransition = findByKNI_N(kaleoNodeId, name);

		return remove(kaleoTransition);
	}

	/**
	 * Returns the number of kaleo transitions where kaleoNodeId = &#63; and name = &#63;.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param name the name
	 * @return the number of matching kaleo transitions
	 */
	@Override
	public int countByKNI_N(long kaleoNodeId, String name) {
		return _uniquePersistenceFinderByKNI_N.count(
			finderCache, new Object[] {kaleoNodeId, name});
	}

	private UniquePersistenceFinder<KaleoTransition, NoSuchTransitionException>
		_uniquePersistenceFinderByKNI_DT;

	/**
	 * Returns the kaleo transition where kaleoNodeId = &#63; and defaultTransition = &#63; or throws a <code>NoSuchTransitionException</code> if it could not be found.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param defaultTransition the default transition
	 * @return the matching kaleo transition
	 * @throws NoSuchTransitionException if a matching kaleo transition could not be found
	 */
	@Override
	public KaleoTransition findByKNI_DT(
			long kaleoNodeId, boolean defaultTransition)
		throws NoSuchTransitionException {

		return _uniquePersistenceFinderByKNI_DT.find(
			finderCache, new Object[] {kaleoNodeId, defaultTransition});
	}

	/**
	 * Returns the kaleo transition where kaleoNodeId = &#63; and defaultTransition = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param defaultTransition the default transition
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo transition, or <code>null</code> if a matching kaleo transition could not be found
	 */
	@Override
	public KaleoTransition fetchByKNI_DT(
		long kaleoNodeId, boolean defaultTransition, boolean useFinderCache) {

		return _uniquePersistenceFinderByKNI_DT.fetch(
			finderCache, new Object[] {kaleoNodeId, defaultTransition},
			useFinderCache);
	}

	/**
	 * Removes the kaleo transition where kaleoNodeId = &#63; and defaultTransition = &#63; from the database.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param defaultTransition the default transition
	 * @return the kaleo transition that was removed
	 */
	@Override
	public KaleoTransition removeByKNI_DT(
			long kaleoNodeId, boolean defaultTransition)
		throws NoSuchTransitionException {

		KaleoTransition kaleoTransition = findByKNI_DT(
			kaleoNodeId, defaultTransition);

		return remove(kaleoTransition);
	}

	/**
	 * Returns the number of kaleo transitions where kaleoNodeId = &#63; and defaultTransition = &#63;.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param defaultTransition the default transition
	 * @return the number of matching kaleo transitions
	 */
	@Override
	public int countByKNI_DT(long kaleoNodeId, boolean defaultTransition) {
		return _uniquePersistenceFinderByKNI_DT.count(
			finderCache, new Object[] {kaleoNodeId, defaultTransition});
	}

	public KaleoTransitionPersistenceImpl() {
		setModelClass(KaleoTransition.class);

		setModelImplClass(KaleoTransitionImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoTransitionTable.INSTANCE);
	}

	/**
	 * Creates a new kaleo transition with the primary key. Does not add the kaleo transition to the database.
	 *
	 * @param kaleoTransitionId the primary key for the new kaleo transition
	 * @return the new kaleo transition
	 */
	@Override
	public KaleoTransition create(long kaleoTransitionId) {
		KaleoTransition kaleoTransition = new KaleoTransitionImpl();

		kaleoTransition.setNew(true);
		kaleoTransition.setPrimaryKey(kaleoTransitionId);

		kaleoTransition.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kaleoTransition;
	}

	/**
	 * Removes the kaleo transition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoTransitionId the primary key of the kaleo transition
	 * @return the kaleo transition that was removed
	 * @throws NoSuchTransitionException if a kaleo transition with the primary key could not be found
	 */
	@Override
	public KaleoTransition remove(long kaleoTransitionId)
		throws NoSuchTransitionException {

		return remove((Serializable)kaleoTransitionId);
	}

	@Override
	protected KaleoTransition removeImpl(KaleoTransition kaleoTransition) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoTransition)) {
				kaleoTransition = (KaleoTransition)session.get(
					KaleoTransitionImpl.class,
					kaleoTransition.getPrimaryKeyObj());
			}

			if ((kaleoTransition != null) &&
				ctPersistenceHelper.isRemove(kaleoTransition)) {

				session.delete(kaleoTransition);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoTransition != null) {
			clearCache(kaleoTransition);
		}

		return kaleoTransition;
	}

	@Override
	public KaleoTransition updateImpl(KaleoTransition kaleoTransition) {
		boolean isNew = kaleoTransition.isNew();

		if (!(kaleoTransition instanceof KaleoTransitionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kaleoTransition.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					kaleoTransition);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoTransition proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoTransition implementation " +
					kaleoTransition.getClass());
		}

		KaleoTransitionModelImpl kaleoTransitionModelImpl =
			(KaleoTransitionModelImpl)kaleoTransition;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kaleoTransition.getCreateDate() == null)) {
			if (serviceContext == null) {
				kaleoTransition.setCreateDate(date);
			}
			else {
				kaleoTransition.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!kaleoTransitionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kaleoTransition.setModifiedDate(date);
			}
			else {
				kaleoTransition.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kaleoTransition)) {
				if (!isNew) {
					session.evict(
						KaleoTransitionImpl.class,
						kaleoTransition.getPrimaryKeyObj());
				}

				session.save(kaleoTransition);
			}
			else {
				kaleoTransition = (KaleoTransition)session.merge(
					kaleoTransition);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kaleoTransition, false);

		if (isNew) {
			kaleoTransition.setNew(false);
		}

		kaleoTransition.resetOriginalValues();

		return kaleoTransition;
	}

	/**
	 * Returns the kaleo transition with the primary key or throws a <code>NoSuchTransitionException</code> if it could not be found.
	 *
	 * @param kaleoTransitionId the primary key of the kaleo transition
	 * @return the kaleo transition
	 * @throws NoSuchTransitionException if a kaleo transition with the primary key could not be found
	 */
	@Override
	public KaleoTransition findByPrimaryKey(long kaleoTransitionId)
		throws NoSuchTransitionException {

		return findByPrimaryKey((Serializable)kaleoTransitionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kaleo transition with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoTransitionId the primary key of the kaleo transition
	 * @return the kaleo transition, or <code>null</code> if a kaleo transition with the primary key could not be found
	 */
	@Override
	public KaleoTransition fetchByPrimaryKey(long kaleoTransitionId) {
		return fetchByPrimaryKey((Serializable)kaleoTransitionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "kaleoTransitionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEOTRANSITION;
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
		return KaleoTransitionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KaleoTransition";
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
		ctMergeColumnNames.add("kaleoNodeId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("label");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("sourceKaleoNodeId");
		ctMergeColumnNames.add("sourceKaleoNodeName");
		ctMergeColumnNames.add("targetKaleoNodeId");
		ctMergeColumnNames.add("targetKaleoNodeName");
		ctMergeColumnNames.add("defaultTransition");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("kaleoTransitionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the kaleo transition persistence.
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
				_SQL_SELECT_KALEOTRANSITION_WHERE,
				_SQL_COUNT_KALEOTRANSITION_WHERE,
				KaleoTransitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"kaleoTransition.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, KaleoTransition::getCompanyId));

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
				_SQL_SELECT_KALEOTRANSITION_WHERE,
				_SQL_COUNT_KALEOTRANSITION_WHERE,
				KaleoTransitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"kaleoTransition.", "kaleoDefinitionVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTransition::getKaleoDefinitionVersionId));

		_collectionPersistenceFinderByKaleoNodeId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByKaleoNodeId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoNodeId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByKaleoNodeId", new String[] {Long.class.getName()},
					new String[] {"kaleoNodeId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKaleoNodeId", new String[] {Long.class.getName()},
					new String[] {"kaleoNodeId"}, false),
				_SQL_SELECT_KALEOTRANSITION_WHERE,
				_SQL_COUNT_KALEOTRANSITION_WHERE,
				KaleoTransitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"kaleoTransition.", "kaleoNodeId", FinderColumn.Type.LONG,
					"=", true, true, KaleoTransition::getKaleoNodeId));

		_uniquePersistenceFinderByKNI_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByKNI_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"kaleoNodeId", "name"}, 0, 2, false,
				KaleoTransition::getKaleoNodeId,
				convertNullFunction(KaleoTransition::getName)),
			_SQL_SELECT_KALEOTRANSITION_WHERE, "",
			new FinderColumn<>(
				"kaleoTransition.", "kaleoNodeId", FinderColumn.Type.LONG, "=",
				true, true, KaleoTransition::getKaleoNodeId),
			new FinderColumn<>(
				"kaleoTransition.", "name", FinderColumn.Type.STRING, "=", true,
				true, KaleoTransition::getName));

		_uniquePersistenceFinderByKNI_DT = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByKNI_DT",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"kaleoNodeId", "defaultTransition"}, 0, 0, false,
				KaleoTransition::getKaleoNodeId,
				KaleoTransition::isDefaultTransition),
			_SQL_SELECT_KALEOTRANSITION_WHERE, "",
			new FinderColumn<>(
				"kaleoTransition.", "kaleoNodeId", FinderColumn.Type.LONG, "=",
				true, true, KaleoTransition::getKaleoNodeId),
			new FinderColumn<>(
				"kaleoTransition.", "defaultTransition",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				KaleoTransition::isDefaultTransition));

		KaleoTransitionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoTransitionUtil.setPersistence(null);

		entityCache.removeCache(KaleoTransitionImpl.class.getName());
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
		KaleoTransitionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KALEOTRANSITION =
		"SELECT kaleoTransition FROM KaleoTransition kaleoTransition";

	private static final String _SQL_SELECT_KALEOTRANSITION_WHERE =
		"SELECT kaleoTransition FROM KaleoTransition kaleoTransition WHERE ";

	private static final String _SQL_COUNT_KALEOTRANSITION_WHERE =
		"SELECT COUNT(kaleoTransition) FROM KaleoTransition kaleoTransition WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No KaleoTransition exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoTransitionPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-387824067