/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDefinedDefaultOrderEntryException;
import com.liferay.portal.tools.service.builder.test.model.DefinedDefaultOrderEntry;
import com.liferay.portal.tools.service.builder.test.model.DefinedDefaultOrderEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.DefinedDefaultOrderEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.DefinedDefaultOrderEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.DefinedDefaultOrderEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.DefinedDefaultOrderEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the defined default order entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DefinedDefaultOrderEntryPersistenceImpl
	extends BasePersistenceImpl
		<DefinedDefaultOrderEntry, NoSuchDefinedDefaultOrderEntryException>
	implements DefinedDefaultOrderEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DefinedDefaultOrderEntryUtil</code> to access the defined default order entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DefinedDefaultOrderEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<DefinedDefaultOrderEntry, NoSuchDefinedDefaultOrderEntryException>
			_uniquePersistenceFinderByName;

	/**
	 * Returns the defined default order entry where name = &#63; or throws a <code>NoSuchDefinedDefaultOrderEntryException</code> if it could not be found.
	 *
	 * @param name the name
	 * @return the matching defined default order entry
	 * @throws NoSuchDefinedDefaultOrderEntryException if a matching defined default order entry could not be found
	 */
	@Override
	public DefinedDefaultOrderEntry findByName(String name)
		throws NoSuchDefinedDefaultOrderEntryException {

		return _uniquePersistenceFinderByName.find(
			finderCache, new Object[] {name});
	}

	/**
	 * Returns the defined default order entry where name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching defined default order entry, or <code>null</code> if a matching defined default order entry could not be found
	 */
	@Override
	public DefinedDefaultOrderEntry fetchByName(
		String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByName.fetch(
			finderCache, new Object[] {name}, useFinderCache);
	}

	/**
	 * Removes the defined default order entry where name = &#63; from the database.
	 *
	 * @param name the name
	 * @return the defined default order entry that was removed
	 */
	@Override
	public DefinedDefaultOrderEntry removeByName(String name)
		throws NoSuchDefinedDefaultOrderEntryException {

		DefinedDefaultOrderEntry definedDefaultOrderEntry = findByName(name);

		return remove(definedDefaultOrderEntry);
	}

	/**
	 * Returns the number of defined default order entries where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching defined default order entries
	 */
	@Override
	public int countByName(String name) {
		return _uniquePersistenceFinderByName.count(
			finderCache, new Object[] {name});
	}

	private CollectionPersistenceFinder
		<DefinedDefaultOrderEntry, NoSuchDefinedDefaultOrderEntryException>
			_collectionPersistenceFinderByName_Collection;

	/**
	 * Returns an ordered range of all the defined default order entries where name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DefinedDefaultOrderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param start the lower bound of the range of defined default order entries
	 * @param end the upper bound of the range of defined default order entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching defined default order entries
	 */
	@Override
	public List<DefinedDefaultOrderEntry> findByName_Collection(
		String name, int start, int end,
		OrderByComparator<DefinedDefaultOrderEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByName_Collection.find(
			finderCache, new Object[] {name}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first defined default order entry in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching defined default order entry
	 * @throws NoSuchDefinedDefaultOrderEntryException if a matching defined default order entry could not be found
	 */
	@Override
	public DefinedDefaultOrderEntry findByName_Collection_First(
			String name,
			OrderByComparator<DefinedDefaultOrderEntry> orderByComparator)
		throws NoSuchDefinedDefaultOrderEntryException {

		return _collectionPersistenceFinderByName_Collection.findFirst(
			finderCache, new Object[] {name}, orderByComparator);
	}

	/**
	 * Returns the first defined default order entry in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching defined default order entry, or <code>null</code> if a matching defined default order entry could not be found
	 */
	@Override
	public DefinedDefaultOrderEntry fetchByName_Collection_First(
		String name,
		OrderByComparator<DefinedDefaultOrderEntry> orderByComparator) {

		return _collectionPersistenceFinderByName_Collection.fetchFirst(
			finderCache, new Object[] {name}, orderByComparator);
	}

	/**
	 * Removes all the defined default order entries where name = &#63; from the database.
	 *
	 * @param name the name
	 */
	@Override
	public void removeByName_Collection(String name) {
		_collectionPersistenceFinderByName_Collection.remove(
			finderCache, new Object[] {name});
	}

	/**
	 * Returns the number of defined default order entries where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching defined default order entries
	 */
	@Override
	public int countByName_Collection(String name) {
		return _collectionPersistenceFinderByName_Collection.count(
			finderCache, new Object[] {name});
	}

	public DefinedDefaultOrderEntryPersistenceImpl() {
		setModelClass(DefinedDefaultOrderEntry.class);

		setModelImplClass(DefinedDefaultOrderEntryImpl.class);
		setModelPKClass(long.class);

		setTable(DefinedDefaultOrderEntryTable.INSTANCE);
	}

	/**
	 * Creates a new defined default order entry with the primary key. Does not add the defined default order entry to the database.
	 *
	 * @param definedDefaultOrderEntryId the primary key for the new defined default order entry
	 * @return the new defined default order entry
	 */
	@Override
	public DefinedDefaultOrderEntry create(long definedDefaultOrderEntryId) {
		DefinedDefaultOrderEntry definedDefaultOrderEntry =
			new DefinedDefaultOrderEntryImpl();

		definedDefaultOrderEntry.setNew(true);
		definedDefaultOrderEntry.setPrimaryKey(definedDefaultOrderEntryId);

		return definedDefaultOrderEntry;
	}

	/**
	 * Removes the defined default order entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param definedDefaultOrderEntryId the primary key of the defined default order entry
	 * @return the defined default order entry that was removed
	 * @throws NoSuchDefinedDefaultOrderEntryException if a defined default order entry with the primary key could not be found
	 */
	@Override
	public DefinedDefaultOrderEntry remove(long definedDefaultOrderEntryId)
		throws NoSuchDefinedDefaultOrderEntryException {

		return remove((Serializable)definedDefaultOrderEntryId);
	}

	@Override
	protected DefinedDefaultOrderEntry removeImpl(
		DefinedDefaultOrderEntry definedDefaultOrderEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(definedDefaultOrderEntry)) {
				definedDefaultOrderEntry =
					(DefinedDefaultOrderEntry)session.get(
						DefinedDefaultOrderEntryImpl.class,
						definedDefaultOrderEntry.getPrimaryKeyObj());
			}

			if (definedDefaultOrderEntry != null) {
				session.delete(definedDefaultOrderEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (definedDefaultOrderEntry != null) {
			clearCache(definedDefaultOrderEntry);
		}

		return definedDefaultOrderEntry;
	}

	@Override
	public DefinedDefaultOrderEntry updateImpl(
		DefinedDefaultOrderEntry definedDefaultOrderEntry) {

		boolean isNew = definedDefaultOrderEntry.isNew();

		if (!(definedDefaultOrderEntry instanceof
				DefinedDefaultOrderEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(definedDefaultOrderEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					definedDefaultOrderEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in definedDefaultOrderEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DefinedDefaultOrderEntry implementation " +
					definedDefaultOrderEntry.getClass());
		}

		DefinedDefaultOrderEntryModelImpl definedDefaultOrderEntryModelImpl =
			(DefinedDefaultOrderEntryModelImpl)definedDefaultOrderEntry;

		if (!definedDefaultOrderEntryModelImpl.hasSetModifiedDate()) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				definedDefaultOrderEntry.setModifiedDate(date);
			}
			else {
				definedDefaultOrderEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(definedDefaultOrderEntry);
			}
			else {
				definedDefaultOrderEntry =
					(DefinedDefaultOrderEntry)session.merge(
						definedDefaultOrderEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(definedDefaultOrderEntry, false);

		if (isNew) {
			definedDefaultOrderEntry.setNew(false);
		}

		definedDefaultOrderEntry.resetOriginalValues();

		return definedDefaultOrderEntry;
	}

	/**
	 * Returns the defined default order entry with the primary key or throws a <code>NoSuchDefinedDefaultOrderEntryException</code> if it could not be found.
	 *
	 * @param definedDefaultOrderEntryId the primary key of the defined default order entry
	 * @return the defined default order entry
	 * @throws NoSuchDefinedDefaultOrderEntryException if a defined default order entry with the primary key could not be found
	 */
	@Override
	public DefinedDefaultOrderEntry findByPrimaryKey(
			long definedDefaultOrderEntryId)
		throws NoSuchDefinedDefaultOrderEntryException {

		return findByPrimaryKey((Serializable)definedDefaultOrderEntryId);
	}

	/**
	 * Returns the defined default order entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param definedDefaultOrderEntryId the primary key of the defined default order entry
	 * @return the defined default order entry, or <code>null</code> if a defined default order entry with the primary key could not be found
	 */
	@Override
	public DefinedDefaultOrderEntry fetchByPrimaryKey(
		long definedDefaultOrderEntryId) {

		return fetchByPrimaryKey((Serializable)definedDefaultOrderEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "definedDefaultOrderEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DEFINEDDEFAULTORDERENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return DefinedDefaultOrderEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the defined default order entry persistence.
	 */
	public void afterPropertiesSet() {
		_uniquePersistenceFinderByName = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByName",
				new String[] {String.class.getName()}, new String[] {"name"}, 0,
				1, false,
				convertNullFunction(DefinedDefaultOrderEntry::getName)),
			_SQL_SELECT_DEFINEDDEFAULTORDERENTRY_WHERE, "",
			new FinderColumn<>(
				"definedDefaultOrderEntry.", "name", FinderColumn.Type.STRING,
				"=", true, true, DefinedDefaultOrderEntry::getName));

		_collectionPersistenceFinderByName_Collection =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByName_Collection",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"name"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByName_Collection",
					new String[] {String.class.getName()},
					new String[] {"name"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByName_Collection",
					new String[] {String.class.getName()},
					new String[] {"name"}, 0, 1, false, null),
				_SQL_SELECT_DEFINEDDEFAULTORDERENTRY_WHERE,
				_SQL_COUNT_DEFINEDDEFAULTORDERENTRY_WHERE,
				DefinedDefaultOrderEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"definedDefaultOrderEntry.", "name",
					FinderColumn.Type.STRING, "=", true, true,
					DefinedDefaultOrderEntry::getName));

		DefinedDefaultOrderEntryUtil.setPersistence(this);
	}

	public void destroy() {
		DefinedDefaultOrderEntryUtil.setPersistence(null);

		entityCache.removeCache(DefinedDefaultOrderEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		DefinedDefaultOrderEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DEFINEDDEFAULTORDERENTRY =
		"SELECT definedDefaultOrderEntry FROM DefinedDefaultOrderEntry definedDefaultOrderEntry";

	private static final String _SQL_SELECT_DEFINEDDEFAULTORDERENTRY_WHERE =
		"SELECT definedDefaultOrderEntry FROM DefinedDefaultOrderEntry definedDefaultOrderEntry WHERE ";

	private static final String _SQL_COUNT_DEFINEDDEFAULTORDERENTRY_WHERE =
		"SELECT COUNT(definedDefaultOrderEntry) FROM DefinedDefaultOrderEntry definedDefaultOrderEntry WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-637702866