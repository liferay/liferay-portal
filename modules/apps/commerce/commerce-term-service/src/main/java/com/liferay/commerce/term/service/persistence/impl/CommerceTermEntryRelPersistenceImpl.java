/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.term.service.persistence.impl;

import com.liferay.commerce.term.exception.NoSuchTermEntryRelException;
import com.liferay.commerce.term.model.CommerceTermEntryRel;
import com.liferay.commerce.term.model.CommerceTermEntryRelTable;
import com.liferay.commerce.term.model.impl.CommerceTermEntryRelImpl;
import com.liferay.commerce.term.model.impl.CommerceTermEntryRelModelImpl;
import com.liferay.commerce.term.service.persistence.CommerceTermEntryRelPersistence;
import com.liferay.commerce.term.service.persistence.CommerceTermEntryRelUtil;
import com.liferay.commerce.term.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the commerce term entry rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(service = CommerceTermEntryRelPersistence.class)
public class CommerceTermEntryRelPersistenceImpl
	extends BasePersistenceImpl
		<CommerceTermEntryRel, NoSuchTermEntryRelException>
	implements CommerceTermEntryRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceTermEntryRelUtil</code> to access the commerce term entry rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceTermEntryRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceTermEntryRel, NoSuchTermEntryRelException>
			_collectionPersistenceFinderByCommerceTermEntryId;

	/**
	 * Returns an ordered range of all the commerce term entry rels where commerceTermEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTermEntryId the commerce term entry ID
	 * @param start the lower bound of the range of commerce term entry rels
	 * @param end the upper bound of the range of commerce term entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce term entry rels
	 */
	@Override
	public List<CommerceTermEntryRel> findByCommerceTermEntryId(
		long commerceTermEntryId, int start, int end,
		OrderByComparator<CommerceTermEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceTermEntryId.find(
			finderCache, new Object[] {commerceTermEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce term entry rel in the ordered set where commerceTermEntryId = &#63;.
	 *
	 * @param commerceTermEntryId the commerce term entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce term entry rel
	 * @throws NoSuchTermEntryRelException if a matching commerce term entry rel could not be found
	 */
	@Override
	public CommerceTermEntryRel findByCommerceTermEntryId_First(
			long commerceTermEntryId,
			OrderByComparator<CommerceTermEntryRel> orderByComparator)
		throws NoSuchTermEntryRelException {

		return _collectionPersistenceFinderByCommerceTermEntryId.findFirst(
			finderCache, new Object[] {commerceTermEntryId}, orderByComparator);
	}

	/**
	 * Returns the first commerce term entry rel in the ordered set where commerceTermEntryId = &#63;.
	 *
	 * @param commerceTermEntryId the commerce term entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce term entry rel, or <code>null</code> if a matching commerce term entry rel could not be found
	 */
	@Override
	public CommerceTermEntryRel fetchByCommerceTermEntryId_First(
		long commerceTermEntryId,
		OrderByComparator<CommerceTermEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByCommerceTermEntryId.fetchFirst(
			finderCache, new Object[] {commerceTermEntryId}, orderByComparator);
	}

	/**
	 * Removes all the commerce term entry rels where commerceTermEntryId = &#63; from the database.
	 *
	 * @param commerceTermEntryId the commerce term entry ID
	 */
	@Override
	public void removeByCommerceTermEntryId(long commerceTermEntryId) {
		_collectionPersistenceFinderByCommerceTermEntryId.remove(
			finderCache, new Object[] {commerceTermEntryId});
	}

	/**
	 * Returns the number of commerce term entry rels where commerceTermEntryId = &#63;.
	 *
	 * @param commerceTermEntryId the commerce term entry ID
	 * @return the number of matching commerce term entry rels
	 */
	@Override
	public int countByCommerceTermEntryId(long commerceTermEntryId) {
		return _collectionPersistenceFinderByCommerceTermEntryId.count(
			finderCache, new Object[] {commerceTermEntryId});
	}

	private CollectionPersistenceFinder
		<CommerceTermEntryRel, NoSuchTermEntryRelException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the commerce term entry rels where classNameId = &#63; and commerceTermEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param commerceTermEntryId the commerce term entry ID
	 * @param start the lower bound of the range of commerce term entry rels
	 * @param end the upper bound of the range of commerce term entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce term entry rels
	 */
	@Override
	public List<CommerceTermEntryRel> findByC_C(
		long classNameId, long commerceTermEntryId, int start, int end,
		OrderByComparator<CommerceTermEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, commerceTermEntryId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce term entry rel in the ordered set where classNameId = &#63; and commerceTermEntryId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param commerceTermEntryId the commerce term entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce term entry rel
	 * @throws NoSuchTermEntryRelException if a matching commerce term entry rel could not be found
	 */
	@Override
	public CommerceTermEntryRel findByC_C_First(
			long classNameId, long commerceTermEntryId,
			OrderByComparator<CommerceTermEntryRel> orderByComparator)
		throws NoSuchTermEntryRelException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {classNameId, commerceTermEntryId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce term entry rel in the ordered set where classNameId = &#63; and commerceTermEntryId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param commerceTermEntryId the commerce term entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce term entry rel, or <code>null</code> if a matching commerce term entry rel could not be found
	 */
	@Override
	public CommerceTermEntryRel fetchByC_C_First(
		long classNameId, long commerceTermEntryId,
		OrderByComparator<CommerceTermEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {classNameId, commerceTermEntryId},
			orderByComparator);
	}

	/**
	 * Removes all the commerce term entry rels where classNameId = &#63; and commerceTermEntryId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param commerceTermEntryId the commerce term entry ID
	 */
	@Override
	public void removeByC_C(long classNameId, long commerceTermEntryId) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {classNameId, commerceTermEntryId});
	}

	/**
	 * Returns the number of commerce term entry rels where classNameId = &#63; and commerceTermEntryId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param commerceTermEntryId the commerce term entry ID
	 * @return the number of matching commerce term entry rels
	 */
	@Override
	public int countByC_C(long classNameId, long commerceTermEntryId) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, commerceTermEntryId});
	}

	private UniquePersistenceFinder
		<CommerceTermEntryRel, NoSuchTermEntryRelException>
			_uniquePersistenceFinderByC_C_C;

	/**
	 * Returns the commerce term entry rel where classNameId = &#63; and classPK = &#63; and commerceTermEntryId = &#63; or throws a <code>NoSuchTermEntryRelException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceTermEntryId the commerce term entry ID
	 * @return the matching commerce term entry rel
	 * @throws NoSuchTermEntryRelException if a matching commerce term entry rel could not be found
	 */
	@Override
	public CommerceTermEntryRel findByC_C_C(
			long classNameId, long classPK, long commerceTermEntryId)
		throws NoSuchTermEntryRelException {

		return _uniquePersistenceFinderByC_C_C.find(
			finderCache,
			new Object[] {classNameId, classPK, commerceTermEntryId});
	}

	/**
	 * Returns the commerce term entry rel where classNameId = &#63; and classPK = &#63; and commerceTermEntryId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceTermEntryId the commerce term entry ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce term entry rel, or <code>null</code> if a matching commerce term entry rel could not be found
	 */
	@Override
	public CommerceTermEntryRel fetchByC_C_C(
		long classNameId, long classPK, long commerceTermEntryId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_C.fetch(
			finderCache,
			new Object[] {classNameId, classPK, commerceTermEntryId},
			useFinderCache);
	}

	/**
	 * Removes the commerce term entry rel where classNameId = &#63; and classPK = &#63; and commerceTermEntryId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceTermEntryId the commerce term entry ID
	 * @return the commerce term entry rel that was removed
	 */
	@Override
	public CommerceTermEntryRel removeByC_C_C(
			long classNameId, long classPK, long commerceTermEntryId)
		throws NoSuchTermEntryRelException {

		CommerceTermEntryRel commerceTermEntryRel = findByC_C_C(
			classNameId, classPK, commerceTermEntryId);

		return remove(commerceTermEntryRel);
	}

	/**
	 * Returns the number of commerce term entry rels where classNameId = &#63; and classPK = &#63; and commerceTermEntryId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceTermEntryId the commerce term entry ID
	 * @return the number of matching commerce term entry rels
	 */
	@Override
	public int countByC_C_C(
		long classNameId, long classPK, long commerceTermEntryId) {

		return _uniquePersistenceFinderByC_C_C.count(
			finderCache,
			new Object[] {classNameId, classPK, commerceTermEntryId});
	}

	public CommerceTermEntryRelPersistenceImpl() {
		setModelClass(CommerceTermEntryRel.class);

		setModelImplClass(CommerceTermEntryRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceTermEntryRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce term entry rel with the primary key. Does not add the commerce term entry rel to the database.
	 *
	 * @param commerceTermEntryRelId the primary key for the new commerce term entry rel
	 * @return the new commerce term entry rel
	 */
	@Override
	public CommerceTermEntryRel create(long commerceTermEntryRelId) {
		CommerceTermEntryRel commerceTermEntryRel =
			new CommerceTermEntryRelImpl();

		commerceTermEntryRel.setNew(true);
		commerceTermEntryRel.setPrimaryKey(commerceTermEntryRelId);

		commerceTermEntryRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceTermEntryRel;
	}

	/**
	 * Removes the commerce term entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceTermEntryRelId the primary key of the commerce term entry rel
	 * @return the commerce term entry rel that was removed
	 * @throws NoSuchTermEntryRelException if a commerce term entry rel with the primary key could not be found
	 */
	@Override
	public CommerceTermEntryRel remove(long commerceTermEntryRelId)
		throws NoSuchTermEntryRelException {

		return remove((Serializable)commerceTermEntryRelId);
	}

	@Override
	protected CommerceTermEntryRel removeImpl(
		CommerceTermEntryRel commerceTermEntryRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceTermEntryRel)) {
				commerceTermEntryRel = (CommerceTermEntryRel)session.get(
					CommerceTermEntryRelImpl.class,
					commerceTermEntryRel.getPrimaryKeyObj());
			}

			if (commerceTermEntryRel != null) {
				session.delete(commerceTermEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceTermEntryRel != null) {
			clearCache(commerceTermEntryRel);
		}

		return commerceTermEntryRel;
	}

	@Override
	public CommerceTermEntryRel updateImpl(
		CommerceTermEntryRel commerceTermEntryRel) {

		boolean isNew = commerceTermEntryRel.isNew();

		if (!(commerceTermEntryRel instanceof CommerceTermEntryRelModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceTermEntryRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceTermEntryRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceTermEntryRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceTermEntryRel implementation " +
					commerceTermEntryRel.getClass());
		}

		CommerceTermEntryRelModelImpl commerceTermEntryRelModelImpl =
			(CommerceTermEntryRelModelImpl)commerceTermEntryRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceTermEntryRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceTermEntryRel.setCreateDate(date);
			}
			else {
				commerceTermEntryRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceTermEntryRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceTermEntryRel.setModifiedDate(date);
			}
			else {
				commerceTermEntryRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceTermEntryRel);
			}
			else {
				commerceTermEntryRel = (CommerceTermEntryRel)session.merge(
					commerceTermEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceTermEntryRel, false);

		if (isNew) {
			commerceTermEntryRel.setNew(false);
		}

		commerceTermEntryRel.resetOriginalValues();

		return commerceTermEntryRel;
	}

	/**
	 * Returns the commerce term entry rel with the primary key or throws a <code>NoSuchTermEntryRelException</code> if it could not be found.
	 *
	 * @param commerceTermEntryRelId the primary key of the commerce term entry rel
	 * @return the commerce term entry rel
	 * @throws NoSuchTermEntryRelException if a commerce term entry rel with the primary key could not be found
	 */
	@Override
	public CommerceTermEntryRel findByPrimaryKey(long commerceTermEntryRelId)
		throws NoSuchTermEntryRelException {

		return findByPrimaryKey((Serializable)commerceTermEntryRelId);
	}

	/**
	 * Returns the commerce term entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceTermEntryRelId the primary key of the commerce term entry rel
	 * @return the commerce term entry rel, or <code>null</code> if a commerce term entry rel with the primary key could not be found
	 */
	@Override
	public CommerceTermEntryRel fetchByPrimaryKey(long commerceTermEntryRelId) {
		return fetchByPrimaryKey((Serializable)commerceTermEntryRelId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "commerceTermEntryRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCETERMENTRYREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceTermEntryRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce term entry rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCommerceTermEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceTermEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceTermEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceTermEntryId",
					new String[] {Long.class.getName()},
					new String[] {"commerceTermEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceTermEntryId",
					new String[] {Long.class.getName()},
					new String[] {"commerceTermEntryId"}, false),
				_SQL_SELECT_COMMERCETERMENTRYREL_WHERE,
				_SQL_COUNT_COMMERCETERMENTRYREL_WHERE,
				CommerceTermEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceTermEntryRel.", "commerceTermEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceTermEntryRel::getCommerceTermEntryId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "commerceTermEntryId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "commerceTermEntryId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "commerceTermEntryId"}, false),
			_SQL_SELECT_COMMERCETERMENTRYREL_WHERE,
			_SQL_COUNT_COMMERCETERMENTRYREL_WHERE,
			CommerceTermEntryRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"commerceTermEntryRel.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, CommerceTermEntryRel::getClassNameId),
			new FinderColumn<>(
				"commerceTermEntryRel.", "commerceTermEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceTermEntryRel::getCommerceTermEntryId));

		_uniquePersistenceFinderByC_C_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"classNameId", "classPK", "commerceTermEntryId"},
				0, 0, false, CommerceTermEntryRel::getClassNameId,
				CommerceTermEntryRel::getClassPK,
				CommerceTermEntryRel::getCommerceTermEntryId),
			_SQL_SELECT_COMMERCETERMENTRYREL_WHERE, "",
			new FinderColumn<>(
				"commerceTermEntryRel.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, CommerceTermEntryRel::getClassNameId),
			new FinderColumn<>(
				"commerceTermEntryRel.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, CommerceTermEntryRel::getClassPK),
			new FinderColumn<>(
				"commerceTermEntryRel.", "commerceTermEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceTermEntryRel::getCommerceTermEntryId));

		CommerceTermEntryRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceTermEntryRelUtil.setPersistence(null);

		entityCache.removeCache(CommerceTermEntryRelImpl.class.getName());
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
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CommerceTermEntryRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCETERMENTRYREL =
		"SELECT commerceTermEntryRel FROM CommerceTermEntryRel commerceTermEntryRel";

	private static final String _SQL_SELECT_COMMERCETERMENTRYREL_WHERE =
		"SELECT commerceTermEntryRel FROM CommerceTermEntryRel commerceTermEntryRel WHERE ";

	private static final String _SQL_COUNT_COMMERCETERMENTRYREL_WHERE =
		"SELECT COUNT(commerceTermEntryRel) FROM CommerceTermEntryRel commerceTermEntryRel WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1630880553