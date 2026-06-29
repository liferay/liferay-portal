/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service.persistence.impl;

import com.liferay.commerce.pricing.exception.NoSuchPricingClassCPDefinitionRelException;
import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel;
import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRelTable;
import com.liferay.commerce.pricing.model.impl.CommercePricingClassCPDefinitionRelImpl;
import com.liferay.commerce.pricing.model.impl.CommercePricingClassCPDefinitionRelModelImpl;
import com.liferay.commerce.pricing.service.persistence.CommercePricingClassCPDefinitionRelPersistence;
import com.liferay.commerce.pricing.service.persistence.CommercePricingClassCPDefinitionRelUtil;
import com.liferay.commerce.pricing.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce pricing class cp definition rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Riccardo Alberti
 * @generated
 */
@Component(service = CommercePricingClassCPDefinitionRelPersistence.class)
public class CommercePricingClassCPDefinitionRelPersistenceImpl
	extends BasePersistenceImpl
		<CommercePricingClassCPDefinitionRel,
		 NoSuchPricingClassCPDefinitionRelException>
	implements CommercePricingClassCPDefinitionRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommercePricingClassCPDefinitionRelUtil</code> to access the commerce pricing class cp definition rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommercePricingClassCPDefinitionRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommercePricingClassCPDefinitionRel,
		 NoSuchPricingClassCPDefinitionRelException>
			_collectionPersistenceFinderByCommercePricingClassId;

	/**
	 * Returns an ordered range of all the commerce pricing class cp definition rels where commercePricingClassId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePricingClassCPDefinitionRelModelImpl</code>.
	 * </p>
	 *
	 * @param commercePricingClassId the commerce pricing class ID
	 * @param start the lower bound of the range of commerce pricing class cp definition rels
	 * @param end the upper bound of the range of commerce pricing class cp definition rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce pricing class cp definition rels
	 */
	@Override
	public List<CommercePricingClassCPDefinitionRel>
		findByCommercePricingClassId(
			long commercePricingClassId, int start, int end,
			OrderByComparator<CommercePricingClassCPDefinitionRel>
				orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByCommercePricingClassId.find(
			finderCache, new Object[] {commercePricingClassId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce pricing class cp definition rel in the ordered set where commercePricingClassId = &#63;.
	 *
	 * @param commercePricingClassId the commerce pricing class ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce pricing class cp definition rel
	 * @throws NoSuchPricingClassCPDefinitionRelException if a matching commerce pricing class cp definition rel could not be found
	 */
	@Override
	public CommercePricingClassCPDefinitionRel
			findByCommercePricingClassId_First(
				long commercePricingClassId,
				OrderByComparator<CommercePricingClassCPDefinitionRel>
					orderByComparator)
		throws NoSuchPricingClassCPDefinitionRelException {

		return _collectionPersistenceFinderByCommercePricingClassId.findFirst(
			finderCache, new Object[] {commercePricingClassId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce pricing class cp definition rel in the ordered set where commercePricingClassId = &#63;.
	 *
	 * @param commercePricingClassId the commerce pricing class ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce pricing class cp definition rel, or <code>null</code> if a matching commerce pricing class cp definition rel could not be found
	 */
	@Override
	public CommercePricingClassCPDefinitionRel
		fetchByCommercePricingClassId_First(
			long commercePricingClassId,
			OrderByComparator<CommercePricingClassCPDefinitionRel>
				orderByComparator) {

		return _collectionPersistenceFinderByCommercePricingClassId.fetchFirst(
			finderCache, new Object[] {commercePricingClassId},
			orderByComparator);
	}

	/**
	 * Removes all the commerce pricing class cp definition rels where commercePricingClassId = &#63; from the database.
	 *
	 * @param commercePricingClassId the commerce pricing class ID
	 */
	@Override
	public void removeByCommercePricingClassId(long commercePricingClassId) {
		_collectionPersistenceFinderByCommercePricingClassId.remove(
			finderCache, new Object[] {commercePricingClassId});
	}

	/**
	 * Returns the number of commerce pricing class cp definition rels where commercePricingClassId = &#63;.
	 *
	 * @param commercePricingClassId the commerce pricing class ID
	 * @return the number of matching commerce pricing class cp definition rels
	 */
	@Override
	public int countByCommercePricingClassId(long commercePricingClassId) {
		return _collectionPersistenceFinderByCommercePricingClassId.count(
			finderCache, new Object[] {commercePricingClassId});
	}

	private CollectionPersistenceFinder
		<CommercePricingClassCPDefinitionRel,
		 NoSuchPricingClassCPDefinitionRelException>
			_collectionPersistenceFinderByCPDefinitionId;

	/**
	 * Returns an ordered range of all the commerce pricing class cp definition rels where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePricingClassCPDefinitionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of commerce pricing class cp definition rels
	 * @param end the upper bound of the range of commerce pricing class cp definition rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce pricing class cp definition rels
	 */
	@Override
	public List<CommercePricingClassCPDefinitionRel> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		OrderByComparator<CommercePricingClassCPDefinitionRel>
			orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPDefinitionId.find(
			finderCache, new Object[] {CPDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce pricing class cp definition rel in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce pricing class cp definition rel
	 * @throws NoSuchPricingClassCPDefinitionRelException if a matching commerce pricing class cp definition rel could not be found
	 */
	@Override
	public CommercePricingClassCPDefinitionRel findByCPDefinitionId_First(
			long CPDefinitionId,
			OrderByComparator<CommercePricingClassCPDefinitionRel>
				orderByComparator)
		throws NoSuchPricingClassCPDefinitionRelException {

		return _collectionPersistenceFinderByCPDefinitionId.findFirst(
			finderCache, new Object[] {CPDefinitionId}, orderByComparator);
	}

	/**
	 * Returns the first commerce pricing class cp definition rel in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce pricing class cp definition rel, or <code>null</code> if a matching commerce pricing class cp definition rel could not be found
	 */
	@Override
	public CommercePricingClassCPDefinitionRel fetchByCPDefinitionId_First(
		long CPDefinitionId,
		OrderByComparator<CommercePricingClassCPDefinitionRel>
			orderByComparator) {

		return _collectionPersistenceFinderByCPDefinitionId.fetchFirst(
			finderCache, new Object[] {CPDefinitionId}, orderByComparator);
	}

	/**
	 * Removes all the commerce pricing class cp definition rels where CPDefinitionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 */
	@Override
	public void removeByCPDefinitionId(long CPDefinitionId) {
		_collectionPersistenceFinderByCPDefinitionId.remove(
			finderCache, new Object[] {CPDefinitionId});
	}

	/**
	 * Returns the number of commerce pricing class cp definition rels where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the number of matching commerce pricing class cp definition rels
	 */
	@Override
	public int countByCPDefinitionId(long CPDefinitionId) {
		return _collectionPersistenceFinderByCPDefinitionId.count(
			finderCache, new Object[] {CPDefinitionId});
	}

	private UniquePersistenceFinder
		<CommercePricingClassCPDefinitionRel,
		 NoSuchPricingClassCPDefinitionRelException>
			_uniquePersistenceFinderByC_C;

	/**
	 * Returns the commerce pricing class cp definition rel where commercePricingClassId = &#63; and CPDefinitionId = &#63; or throws a <code>NoSuchPricingClassCPDefinitionRelException</code> if it could not be found.
	 *
	 * @param commercePricingClassId the commerce pricing class ID
	 * @param CPDefinitionId the cp definition ID
	 * @return the matching commerce pricing class cp definition rel
	 * @throws NoSuchPricingClassCPDefinitionRelException if a matching commerce pricing class cp definition rel could not be found
	 */
	@Override
	public CommercePricingClassCPDefinitionRel findByC_C(
			long commercePricingClassId, long CPDefinitionId)
		throws NoSuchPricingClassCPDefinitionRelException {

		return _uniquePersistenceFinderByC_C.find(
			finderCache, new Object[] {commercePricingClassId, CPDefinitionId});
	}

	/**
	 * Returns the commerce pricing class cp definition rel where commercePricingClassId = &#63; and CPDefinitionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commercePricingClassId the commerce pricing class ID
	 * @param CPDefinitionId the cp definition ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce pricing class cp definition rel, or <code>null</code> if a matching commerce pricing class cp definition rel could not be found
	 */
	@Override
	public CommercePricingClassCPDefinitionRel fetchByC_C(
		long commercePricingClassId, long CPDefinitionId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C.fetch(
			finderCache, new Object[] {commercePricingClassId, CPDefinitionId},
			useFinderCache);
	}

	/**
	 * Removes the commerce pricing class cp definition rel where commercePricingClassId = &#63; and CPDefinitionId = &#63; from the database.
	 *
	 * @param commercePricingClassId the commerce pricing class ID
	 * @param CPDefinitionId the cp definition ID
	 * @return the commerce pricing class cp definition rel that was removed
	 */
	@Override
	public CommercePricingClassCPDefinitionRel removeByC_C(
			long commercePricingClassId, long CPDefinitionId)
		throws NoSuchPricingClassCPDefinitionRelException {

		CommercePricingClassCPDefinitionRel
			commercePricingClassCPDefinitionRel = findByC_C(
				commercePricingClassId, CPDefinitionId);

		return remove(commercePricingClassCPDefinitionRel);
	}

	/**
	 * Returns the number of commerce pricing class cp definition rels where commercePricingClassId = &#63; and CPDefinitionId = &#63;.
	 *
	 * @param commercePricingClassId the commerce pricing class ID
	 * @param CPDefinitionId the cp definition ID
	 * @return the number of matching commerce pricing class cp definition rels
	 */
	@Override
	public int countByC_C(long commercePricingClassId, long CPDefinitionId) {
		return _uniquePersistenceFinderByC_C.count(
			finderCache, new Object[] {commercePricingClassId, CPDefinitionId});
	}

	public CommercePricingClassCPDefinitionRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"CommercePricingClassCPDefinitionRelId",
			"CPricingClassCPDefinitionRelId");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommercePricingClassCPDefinitionRel.class);

		setModelImplClass(CommercePricingClassCPDefinitionRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommercePricingClassCPDefinitionRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce pricing class cp definition rel with the primary key. Does not add the commerce pricing class cp definition rel to the database.
	 *
	 * @param CommercePricingClassCPDefinitionRelId the primary key for the new commerce pricing class cp definition rel
	 * @return the new commerce pricing class cp definition rel
	 */
	@Override
	public CommercePricingClassCPDefinitionRel create(
		long CommercePricingClassCPDefinitionRelId) {

		CommercePricingClassCPDefinitionRel
			commercePricingClassCPDefinitionRel =
				new CommercePricingClassCPDefinitionRelImpl();

		commercePricingClassCPDefinitionRel.setNew(true);
		commercePricingClassCPDefinitionRel.setPrimaryKey(
			CommercePricingClassCPDefinitionRelId);

		commercePricingClassCPDefinitionRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commercePricingClassCPDefinitionRel;
	}

	/**
	 * Removes the commerce pricing class cp definition rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CommercePricingClassCPDefinitionRelId the primary key of the commerce pricing class cp definition rel
	 * @return the commerce pricing class cp definition rel that was removed
	 * @throws NoSuchPricingClassCPDefinitionRelException if a commerce pricing class cp definition rel with the primary key could not be found
	 */
	@Override
	public CommercePricingClassCPDefinitionRel remove(
			long CommercePricingClassCPDefinitionRelId)
		throws NoSuchPricingClassCPDefinitionRelException {

		return remove((Serializable)CommercePricingClassCPDefinitionRelId);
	}

	@Override
	protected CommercePricingClassCPDefinitionRel removeImpl(
		CommercePricingClassCPDefinitionRel
			commercePricingClassCPDefinitionRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commercePricingClassCPDefinitionRel)) {
				commercePricingClassCPDefinitionRel =
					(CommercePricingClassCPDefinitionRel)session.get(
						CommercePricingClassCPDefinitionRelImpl.class,
						commercePricingClassCPDefinitionRel.getPrimaryKeyObj());
			}

			if ((commercePricingClassCPDefinitionRel != null) &&
				ctPersistenceHelper.isRemove(
					commercePricingClassCPDefinitionRel)) {

				session.delete(commercePricingClassCPDefinitionRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commercePricingClassCPDefinitionRel != null) {
			clearCache(commercePricingClassCPDefinitionRel);
		}

		return commercePricingClassCPDefinitionRel;
	}

	@Override
	public CommercePricingClassCPDefinitionRel updateImpl(
		CommercePricingClassCPDefinitionRel
			commercePricingClassCPDefinitionRel) {

		boolean isNew = commercePricingClassCPDefinitionRel.isNew();

		if (!(commercePricingClassCPDefinitionRel instanceof
				CommercePricingClassCPDefinitionRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commercePricingClassCPDefinitionRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commercePricingClassCPDefinitionRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commercePricingClassCPDefinitionRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommercePricingClassCPDefinitionRel implementation " +
					commercePricingClassCPDefinitionRel.getClass());
		}

		CommercePricingClassCPDefinitionRelModelImpl
			commercePricingClassCPDefinitionRelModelImpl =
				(CommercePricingClassCPDefinitionRelModelImpl)
					commercePricingClassCPDefinitionRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew &&
			(commercePricingClassCPDefinitionRel.getCreateDate() == null)) {

			if (serviceContext == null) {
				commercePricingClassCPDefinitionRel.setCreateDate(date);
			}
			else {
				commercePricingClassCPDefinitionRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commercePricingClassCPDefinitionRelModelImpl.
				hasSetModifiedDate()) {

			if (serviceContext == null) {
				commercePricingClassCPDefinitionRel.setModifiedDate(date);
			}
			else {
				commercePricingClassCPDefinitionRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(
					commercePricingClassCPDefinitionRel)) {

				if (!isNew) {
					session.evict(
						CommercePricingClassCPDefinitionRelImpl.class,
						commercePricingClassCPDefinitionRel.getPrimaryKeyObj());
				}

				session.save(commercePricingClassCPDefinitionRel);
			}
			else {
				commercePricingClassCPDefinitionRel =
					(CommercePricingClassCPDefinitionRel)session.merge(
						commercePricingClassCPDefinitionRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commercePricingClassCPDefinitionRel, false);

		if (isNew) {
			commercePricingClassCPDefinitionRel.setNew(false);
		}

		commercePricingClassCPDefinitionRel.resetOriginalValues();

		return commercePricingClassCPDefinitionRel;
	}

	/**
	 * Returns the commerce pricing class cp definition rel with the primary key or throws a <code>NoSuchPricingClassCPDefinitionRelException</code> if it could not be found.
	 *
	 * @param CommercePricingClassCPDefinitionRelId the primary key of the commerce pricing class cp definition rel
	 * @return the commerce pricing class cp definition rel
	 * @throws NoSuchPricingClassCPDefinitionRelException if a commerce pricing class cp definition rel with the primary key could not be found
	 */
	@Override
	public CommercePricingClassCPDefinitionRel findByPrimaryKey(
			long CommercePricingClassCPDefinitionRelId)
		throws NoSuchPricingClassCPDefinitionRelException {

		return findByPrimaryKey(
			(Serializable)CommercePricingClassCPDefinitionRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the commerce pricing class cp definition rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CommercePricingClassCPDefinitionRelId the primary key of the commerce pricing class cp definition rel
	 * @return the commerce pricing class cp definition rel, or <code>null</code> if a commerce pricing class cp definition rel with the primary key could not be found
	 */
	@Override
	public CommercePricingClassCPDefinitionRel fetchByPrimaryKey(
		long CommercePricingClassCPDefinitionRelId) {

		return fetchByPrimaryKey(
			(Serializable)CommercePricingClassCPDefinitionRelId);
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
		return "CPricingClassCPDefinitionRelId";
	}

	@Override
	protected String getPKFieldName() {
		return "CommercePricingClassCPDefinitionRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEPRICINGCLASSCPDEFINITIONREL;
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
		return CommercePricingClassCPDefinitionRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPricingClassCPDefinitionRel";
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
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("commercePricingClassId");
		ctMergeColumnNames.add("CPDefinitionId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CPricingClassCPDefinitionRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"commercePricingClassId", "CPDefinitionId"});
	}

	/**
	 * Initializes the commerce pricing class cp definition rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCommercePricingClassId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommercePricingClassId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commercePricingClassId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommercePricingClassId",
					new String[] {Long.class.getName()},
					new String[] {"commercePricingClassId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommercePricingClassId",
					new String[] {Long.class.getName()},
					new String[] {"commercePricingClassId"}, false),
				_SQL_SELECT_COMMERCEPRICINGCLASSCPDEFINITIONREL_WHERE,
				_SQL_COUNT_COMMERCEPRICINGCLASSCPDEFINITIONREL_WHERE,
				CommercePricingClassCPDefinitionRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commercePricingClassCPDefinitionRel.",
					"commercePricingClassId", FinderColumn.Type.LONG, "=", true,
					true,
					CommercePricingClassCPDefinitionRel::
						getCommercePricingClassId));

		_collectionPersistenceFinderByCPDefinitionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPDefinitionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPDefinitionId", new String[] {Long.class.getName()},
					new String[] {"CPDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPDefinitionId",
					new String[] {Long.class.getName()},
					new String[] {"CPDefinitionId"}, false),
				_SQL_SELECT_COMMERCEPRICINGCLASSCPDEFINITIONREL_WHERE,
				_SQL_COUNT_COMMERCEPRICINGCLASSCPDEFINITIONREL_WHERE,
				CommercePricingClassCPDefinitionRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commercePricingClassCPDefinitionRel.", "CPDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePricingClassCPDefinitionRel::getCPDefinitionId));

		_uniquePersistenceFinderByC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"commercePricingClassId", "CPDefinitionId"}, 0, 0,
				false,
				CommercePricingClassCPDefinitionRel::getCommercePricingClassId,
				CommercePricingClassCPDefinitionRel::getCPDefinitionId),
			_SQL_SELECT_COMMERCEPRICINGCLASSCPDEFINITIONREL_WHERE, "",
			new FinderColumn<>(
				"commercePricingClassCPDefinitionRel.",
				"commercePricingClassId", FinderColumn.Type.LONG, "=", true,
				true,
				CommercePricingClassCPDefinitionRel::getCommercePricingClassId),
			new FinderColumn<>(
				"commercePricingClassCPDefinitionRel.", "CPDefinitionId",
				FinderColumn.Type.LONG, "=", true, true,
				CommercePricingClassCPDefinitionRel::getCPDefinitionId));

		CommercePricingClassCPDefinitionRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommercePricingClassCPDefinitionRelUtil.setPersistence(null);

		entityCache.removeCache(
			CommercePricingClassCPDefinitionRelImpl.class.getName());
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		CommercePricingClassCPDefinitionRelModelImpl.ENTITY_ALIAS + ".";

	private static final String
		_SQL_SELECT_COMMERCEPRICINGCLASSCPDEFINITIONREL =
			"SELECT commercePricingClassCPDefinitionRel FROM CommercePricingClassCPDefinitionRel commercePricingClassCPDefinitionRel";

	private static final String
		_SQL_SELECT_COMMERCEPRICINGCLASSCPDEFINITIONREL_WHERE =
			"SELECT commercePricingClassCPDefinitionRel FROM CommercePricingClassCPDefinitionRel commercePricingClassCPDefinitionRel WHERE ";

	private static final String
		_SQL_COUNT_COMMERCEPRICINGCLASSCPDEFINITIONREL_WHERE =
			"SELECT COUNT(commercePricingClassCPDefinitionRel) FROM CommercePricingClassCPDefinitionRel commercePricingClassCPDefinitionRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommercePricingClassCPDefinitionRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePricingClassCPDefinitionRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"CommercePricingClassCPDefinitionRelId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:305957177