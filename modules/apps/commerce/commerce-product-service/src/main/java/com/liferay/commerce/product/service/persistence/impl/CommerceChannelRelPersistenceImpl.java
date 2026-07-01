/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.NoSuchChannelRelException;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.model.CommerceChannelRelTable;
import com.liferay.commerce.product.model.impl.CommerceChannelRelImpl;
import com.liferay.commerce.product.model.impl.CommerceChannelRelModelImpl;
import com.liferay.commerce.product.service.persistence.CommerceChannelRelPersistence;
import com.liferay.commerce.product.service.persistence.CommerceChannelRelUtil;
import com.liferay.commerce.product.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce channel rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CommerceChannelRelPersistence.class)
public class CommerceChannelRelPersistenceImpl
	extends BasePersistenceImpl<CommerceChannelRel, NoSuchChannelRelException>
	implements CommerceChannelRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceChannelRelUtil</code> to access the commerce channel rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceChannelRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceChannelRel, NoSuchChannelRelException>
			_collectionPersistenceFinderByCommerceChannelId;

	/**
	 * Returns an ordered range of all the commerce channel rels where commerceChannelId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceChannelRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @param start the lower bound of the range of commerce channel rels
	 * @param end the upper bound of the range of commerce channel rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce channel rels
	 */
	@Override
	public List<CommerceChannelRel> findByCommerceChannelId(
		long commerceChannelId, int start, int end,
		OrderByComparator<CommerceChannelRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceChannelId.find(
			finderCache, new Object[] {commerceChannelId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce channel rel in the ordered set where commerceChannelId = &#63;.
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce channel rel
	 * @throws NoSuchChannelRelException if a matching commerce channel rel could not be found
	 */
	@Override
	public CommerceChannelRel findByCommerceChannelId_First(
			long commerceChannelId,
			OrderByComparator<CommerceChannelRel> orderByComparator)
		throws NoSuchChannelRelException {

		return _collectionPersistenceFinderByCommerceChannelId.findFirst(
			finderCache, new Object[] {commerceChannelId}, orderByComparator);
	}

	/**
	 * Returns the first commerce channel rel in the ordered set where commerceChannelId = &#63;.
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce channel rel, or <code>null</code> if a matching commerce channel rel could not be found
	 */
	@Override
	public CommerceChannelRel fetchByCommerceChannelId_First(
		long commerceChannelId,
		OrderByComparator<CommerceChannelRel> orderByComparator) {

		return _collectionPersistenceFinderByCommerceChannelId.fetchFirst(
			finderCache, new Object[] {commerceChannelId}, orderByComparator);
	}

	/**
	 * Removes all the commerce channel rels where commerceChannelId = &#63; from the database.
	 *
	 * @param commerceChannelId the commerce channel ID
	 */
	@Override
	public void removeByCommerceChannelId(long commerceChannelId) {
		_collectionPersistenceFinderByCommerceChannelId.remove(
			finderCache, new Object[] {commerceChannelId});
	}

	/**
	 * Returns the number of commerce channel rels where commerceChannelId = &#63;.
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @return the number of matching commerce channel rels
	 */
	@Override
	public int countByCommerceChannelId(long commerceChannelId) {
		return _collectionPersistenceFinderByCommerceChannelId.count(
			finderCache, new Object[] {commerceChannelId});
	}

	private CollectionPersistenceFinder
		<CommerceChannelRel, NoSuchChannelRelException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the commerce channel rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceChannelRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce channel rels
	 * @param end the upper bound of the range of commerce channel rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce channel rels
	 */
	@Override
	public List<CommerceChannelRel> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<CommerceChannelRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce channel rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce channel rel
	 * @throws NoSuchChannelRelException if a matching commerce channel rel could not be found
	 */
	@Override
	public CommerceChannelRel findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<CommerceChannelRel> orderByComparator)
		throws NoSuchChannelRelException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first commerce channel rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce channel rel, or <code>null</code> if a matching commerce channel rel could not be found
	 */
	@Override
	public CommerceChannelRel fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<CommerceChannelRel> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the commerce channel rels where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C(long classNameId, long classPK) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the number of commerce channel rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching commerce channel rels
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private UniquePersistenceFinder
		<CommerceChannelRel, NoSuchChannelRelException>
			_uniquePersistenceFinderByC_C_C;

	/**
	 * Returns the commerce channel rel where classNameId = &#63; and classPK = &#63; and commerceChannelId = &#63; or throws a <code>NoSuchChannelRelException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceChannelId the commerce channel ID
	 * @return the matching commerce channel rel
	 * @throws NoSuchChannelRelException if a matching commerce channel rel could not be found
	 */
	@Override
	public CommerceChannelRel findByC_C_C(
			long classNameId, long classPK, long commerceChannelId)
		throws NoSuchChannelRelException {

		return _uniquePersistenceFinderByC_C_C.find(
			finderCache,
			new Object[] {classNameId, classPK, commerceChannelId});
	}

	/**
	 * Returns the commerce channel rel where classNameId = &#63; and classPK = &#63; and commerceChannelId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceChannelId the commerce channel ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce channel rel, or <code>null</code> if a matching commerce channel rel could not be found
	 */
	@Override
	public CommerceChannelRel fetchByC_C_C(
		long classNameId, long classPK, long commerceChannelId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_C.fetch(
			finderCache, new Object[] {classNameId, classPK, commerceChannelId},
			useFinderCache);
	}

	/**
	 * Removes the commerce channel rel where classNameId = &#63; and classPK = &#63; and commerceChannelId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceChannelId the commerce channel ID
	 * @return the commerce channel rel that was removed
	 */
	@Override
	public CommerceChannelRel removeByC_C_C(
			long classNameId, long classPK, long commerceChannelId)
		throws NoSuchChannelRelException {

		CommerceChannelRel commerceChannelRel = findByC_C_C(
			classNameId, classPK, commerceChannelId);

		return remove(commerceChannelRel);
	}

	/**
	 * Returns the number of commerce channel rels where classNameId = &#63; and classPK = &#63; and commerceChannelId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceChannelId the commerce channel ID
	 * @return the number of matching commerce channel rels
	 */
	@Override
	public int countByC_C_C(
		long classNameId, long classPK, long commerceChannelId) {

		return _uniquePersistenceFinderByC_C_C.count(
			finderCache,
			new Object[] {classNameId, classPK, commerceChannelId});
	}

	public CommerceChannelRelPersistenceImpl() {
		setModelClass(CommerceChannelRel.class);

		setModelImplClass(CommerceChannelRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceChannelRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce channel rel with the primary key. Does not add the commerce channel rel to the database.
	 *
	 * @param commerceChannelRelId the primary key for the new commerce channel rel
	 * @return the new commerce channel rel
	 */
	@Override
	public CommerceChannelRel create(long commerceChannelRelId) {
		CommerceChannelRel commerceChannelRel = new CommerceChannelRelImpl();

		commerceChannelRel.setNew(true);
		commerceChannelRel.setPrimaryKey(commerceChannelRelId);

		commerceChannelRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceChannelRel;
	}

	/**
	 * Removes the commerce channel rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceChannelRelId the primary key of the commerce channel rel
	 * @return the commerce channel rel that was removed
	 * @throws NoSuchChannelRelException if a commerce channel rel with the primary key could not be found
	 */
	@Override
	public CommerceChannelRel remove(long commerceChannelRelId)
		throws NoSuchChannelRelException {

		return remove((Serializable)commerceChannelRelId);
	}

	@Override
	protected CommerceChannelRel removeImpl(
		CommerceChannelRel commerceChannelRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceChannelRel)) {
				commerceChannelRel = (CommerceChannelRel)session.get(
					CommerceChannelRelImpl.class,
					commerceChannelRel.getPrimaryKeyObj());
			}

			if ((commerceChannelRel != null) &&
				ctPersistenceHelper.isRemove(commerceChannelRel)) {

				session.delete(commerceChannelRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceChannelRel != null) {
			clearCache(commerceChannelRel);
		}

		return commerceChannelRel;
	}

	@Override
	public CommerceChannelRel updateImpl(
		CommerceChannelRel commerceChannelRel) {

		boolean isNew = commerceChannelRel.isNew();

		if (!(commerceChannelRel instanceof CommerceChannelRelModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceChannelRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceChannelRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceChannelRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceChannelRel implementation " +
					commerceChannelRel.getClass());
		}

		CommerceChannelRelModelImpl commerceChannelRelModelImpl =
			(CommerceChannelRelModelImpl)commerceChannelRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceChannelRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceChannelRel.setCreateDate(date);
			}
			else {
				commerceChannelRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceChannelRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceChannelRel.setModifiedDate(date);
			}
			else {
				commerceChannelRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(commerceChannelRel)) {
				if (!isNew) {
					session.evict(
						CommerceChannelRelImpl.class,
						commerceChannelRel.getPrimaryKeyObj());
				}

				session.save(commerceChannelRel);
			}
			else {
				commerceChannelRel = (CommerceChannelRel)session.merge(
					commerceChannelRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceChannelRel, false);

		if (isNew) {
			commerceChannelRel.setNew(false);
		}

		commerceChannelRel.resetOriginalValues();

		return commerceChannelRel;
	}

	/**
	 * Returns the commerce channel rel with the primary key or throws a <code>NoSuchChannelRelException</code> if it could not be found.
	 *
	 * @param commerceChannelRelId the primary key of the commerce channel rel
	 * @return the commerce channel rel
	 * @throws NoSuchChannelRelException if a commerce channel rel with the primary key could not be found
	 */
	@Override
	public CommerceChannelRel findByPrimaryKey(long commerceChannelRelId)
		throws NoSuchChannelRelException {

		return findByPrimaryKey((Serializable)commerceChannelRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the commerce channel rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceChannelRelId the primary key of the commerce channel rel
	 * @return the commerce channel rel, or <code>null</code> if a commerce channel rel with the primary key could not be found
	 */
	@Override
	public CommerceChannelRel fetchByPrimaryKey(long commerceChannelRelId) {
		return fetchByPrimaryKey((Serializable)commerceChannelRelId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "commerceChannelRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCECHANNELREL;
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
		return CommerceChannelRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CommerceChannelRel";
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
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("commerceChannelId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("commerceChannelRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"classNameId", "classPK", "commerceChannelId"});
	}

	/**
	 * Initializes the commerce channel rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCommerceChannelId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceChannelId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceChannelId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceChannelId",
					new String[] {Long.class.getName()},
					new String[] {"commerceChannelId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceChannelId",
					new String[] {Long.class.getName()},
					new String[] {"commerceChannelId"}, false),
				_SQL_SELECT_COMMERCECHANNELREL_WHERE,
				_SQL_COUNT_COMMERCECHANNELREL_WHERE,
				CommerceChannelRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceChannelRel.", "commerceChannelId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceChannelRel::getCommerceChannelId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, false),
			_SQL_SELECT_COMMERCECHANNELREL_WHERE,
			_SQL_COUNT_COMMERCECHANNELREL_WHERE,
			CommerceChannelRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"commerceChannelRel.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, CommerceChannelRel::getClassNameId),
			new FinderColumn<>(
				"commerceChannelRel.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, CommerceChannelRel::getClassPK));

		_uniquePersistenceFinderByC_C_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"classNameId", "classPK", "commerceChannelId"}, 0,
				0, false, CommerceChannelRel::getClassNameId,
				CommerceChannelRel::getClassPK,
				CommerceChannelRel::getCommerceChannelId),
			_SQL_SELECT_COMMERCECHANNELREL_WHERE, "",
			new FinderColumn<>(
				"commerceChannelRel.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, CommerceChannelRel::getClassNameId),
			new FinderColumn<>(
				"commerceChannelRel.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, CommerceChannelRel::getClassPK),
			new FinderColumn<>(
				"commerceChannelRel.", "commerceChannelId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceChannelRel::getCommerceChannelId));

		CommerceChannelRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceChannelRelUtil.setPersistence(null);

		entityCache.removeCache(CommerceChannelRelImpl.class.getName());
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
		CommerceChannelRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCECHANNELREL =
		"SELECT commerceChannelRel FROM CommerceChannelRel commerceChannelRel";

	private static final String _SQL_SELECT_COMMERCECHANNELREL_WHERE =
		"SELECT commerceChannelRel FROM CommerceChannelRel commerceChannelRel WHERE ";

	private static final String _SQL_COUNT_COMMERCECHANNELREL_WHERE =
		"SELECT COUNT(commerceChannelRel) FROM CommerceChannelRel commerceChannelRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceChannelRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceChannelRelPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:101060766