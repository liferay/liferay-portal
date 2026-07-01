/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchWhereClauseEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.WhereClauseEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.WhereClauseEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.WhereClauseEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.WhereClauseEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.WhereClauseEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.WhereClauseEntryUtil;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl.constants.SBCompat740PersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the where clause entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = WhereClauseEntryPersistence.class)
public class WhereClauseEntryPersistenceImpl
	extends BasePersistenceImpl
		<WhereClauseEntry, NoSuchWhereClauseEntryException>
	implements WhereClauseEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>WhereClauseEntryUtil</code> to access the where clause entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		WhereClauseEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<WhereClauseEntry, NoSuchWhereClauseEntryException>
			_collectionPersistenceFinderByName_Nickname;

	/**
	 * Returns an ordered range of all the where clause entries where name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param start the lower bound of the range of where clause entries
	 * @param end the upper bound of the range of where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching where clause entries
	 */
	@Override
	public List<WhereClauseEntry> findByName_Nickname(
		String name, int start, int end,
		OrderByComparator<WhereClauseEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByName_Nickname.find(
			finderCache, new Object[] {name}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first where clause entry in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching where clause entry
	 * @throws NoSuchWhereClauseEntryException if a matching where clause entry could not be found
	 */
	@Override
	public WhereClauseEntry findByName_Nickname_First(
			String name, OrderByComparator<WhereClauseEntry> orderByComparator)
		throws NoSuchWhereClauseEntryException {

		return _collectionPersistenceFinderByName_Nickname.findFirst(
			finderCache, new Object[] {name}, orderByComparator);
	}

	/**
	 * Returns the first where clause entry in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching where clause entry, or <code>null</code> if a matching where clause entry could not be found
	 */
	@Override
	public WhereClauseEntry fetchByName_Nickname_First(
		String name, OrderByComparator<WhereClauseEntry> orderByComparator) {

		return _collectionPersistenceFinderByName_Nickname.fetchFirst(
			finderCache, new Object[] {name}, orderByComparator);
	}

	/**
	 * Removes all the where clause entries where name = &#63; from the database.
	 *
	 * @param name the name
	 */
	@Override
	public void removeByName_Nickname(String name) {
		_collectionPersistenceFinderByName_Nickname.remove(
			finderCache, new Object[] {name});
	}

	/**
	 * Returns the number of where clause entries where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching where clause entries
	 */
	@Override
	public int countByName_Nickname(String name) {
		return _collectionPersistenceFinderByName_Nickname.count(
			finderCache, new Object[] {name});
	}

	public WhereClauseEntryPersistenceImpl() {
		setModelClass(WhereClauseEntry.class);

		setModelImplClass(WhereClauseEntryImpl.class);
		setModelPKClass(long.class);

		setTable(WhereClauseEntryTable.INSTANCE);
	}

	/**
	 * Creates a new where clause entry with the primary key. Does not add the where clause entry to the database.
	 *
	 * @param whereClauseEntryId the primary key for the new where clause entry
	 * @return the new where clause entry
	 */
	@Override
	public WhereClauseEntry create(long whereClauseEntryId) {
		WhereClauseEntry whereClauseEntry = new WhereClauseEntryImpl();

		whereClauseEntry.setNew(true);
		whereClauseEntry.setPrimaryKey(whereClauseEntryId);

		return whereClauseEntry;
	}

	/**
	 * Removes the where clause entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param whereClauseEntryId the primary key of the where clause entry
	 * @return the where clause entry that was removed
	 * @throws NoSuchWhereClauseEntryException if a where clause entry with the primary key could not be found
	 */
	@Override
	public WhereClauseEntry remove(long whereClauseEntryId)
		throws NoSuchWhereClauseEntryException {

		return remove((Serializable)whereClauseEntryId);
	}

	@Override
	protected WhereClauseEntry removeImpl(WhereClauseEntry whereClauseEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(whereClauseEntry)) {
				whereClauseEntry = (WhereClauseEntry)session.get(
					WhereClauseEntryImpl.class,
					whereClauseEntry.getPrimaryKeyObj());
			}

			if (whereClauseEntry != null) {
				session.delete(whereClauseEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (whereClauseEntry != null) {
			clearCache(whereClauseEntry);
		}

		return whereClauseEntry;
	}

	@Override
	public WhereClauseEntry updateImpl(WhereClauseEntry whereClauseEntry) {
		boolean isNew = whereClauseEntry.isNew();

		if (!(whereClauseEntry instanceof WhereClauseEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(whereClauseEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					whereClauseEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in whereClauseEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom WhereClauseEntry implementation " +
					whereClauseEntry.getClass());
		}

		WhereClauseEntryModelImpl whereClauseEntryModelImpl =
			(WhereClauseEntryModelImpl)whereClauseEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(whereClauseEntry);
			}
			else {
				whereClauseEntry = (WhereClauseEntry)session.merge(
					whereClauseEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(whereClauseEntry, false);

		if (isNew) {
			whereClauseEntry.setNew(false);
		}

		whereClauseEntry.resetOriginalValues();

		return whereClauseEntry;
	}

	/**
	 * Returns the where clause entry with the primary key or throws a <code>NoSuchWhereClauseEntryException</code> if it could not be found.
	 *
	 * @param whereClauseEntryId the primary key of the where clause entry
	 * @return the where clause entry
	 * @throws NoSuchWhereClauseEntryException if a where clause entry with the primary key could not be found
	 */
	@Override
	public WhereClauseEntry findByPrimaryKey(long whereClauseEntryId)
		throws NoSuchWhereClauseEntryException {

		return findByPrimaryKey((Serializable)whereClauseEntryId);
	}

	/**
	 * Returns the where clause entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param whereClauseEntryId the primary key of the where clause entry
	 * @return the where clause entry, or <code>null</code> if a where clause entry with the primary key could not be found
	 */
	@Override
	public WhereClauseEntry fetchByPrimaryKey(long whereClauseEntryId) {
		return fetchByPrimaryKey((Serializable)whereClauseEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "whereClauseEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_WHERECLAUSEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return WhereClauseEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the where clause entry persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByName_Nickname =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByName_Nickname",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"name"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByName_Nickname",
					new String[] {String.class.getName()},
					new String[] {"name"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByName_Nickname",
					new String[] {String.class.getName()},
					new String[] {"name"}, 0, 1, false, null),
				_SQL_SELECT_WHERECLAUSEENTRY_WHERE,
				_SQL_COUNT_WHERECLAUSEENTRY_WHERE,
				WhereClauseEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"whereClauseEntry.nickname IS NOT NULL",
				"whereClauseEntry.nickname IS NOT NULL", null,
				new FinderColumn<>(
					"whereClauseEntry.", "name", FinderColumn.Type.STRING, "=",
					true, true, WhereClauseEntry::getName));

		WhereClauseEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		WhereClauseEntryUtil.setPersistence(null);

		entityCache.removeCache(WhereClauseEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		WhereClauseEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_WHERECLAUSEENTRY =
		"SELECT whereClauseEntry FROM WhereClauseEntry whereClauseEntry";

	private static final String _SQL_SELECT_WHERECLAUSEENTRY_WHERE =
		"SELECT whereClauseEntry FROM WhereClauseEntry whereClauseEntry WHERE ";

	private static final String _SQL_COUNT_WHERECLAUSEENTRY_WHERE =
		"SELECT COUNT(whereClauseEntry) FROM WhereClauseEntry whereClauseEntry WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1956139003