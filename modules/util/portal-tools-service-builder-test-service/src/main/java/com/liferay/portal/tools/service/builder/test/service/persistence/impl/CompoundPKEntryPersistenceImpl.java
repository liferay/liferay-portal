/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchCompoundPKEntryException;
import com.liferay.portal.tools.service.builder.test.model.CompoundPKEntry;
import com.liferay.portal.tools.service.builder.test.model.CompoundPKEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.CompoundPKEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.CompoundPKEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.CompoundPKEntryPK;
import com.liferay.portal.tools.service.builder.test.service.persistence.CompoundPKEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.CompoundPKEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the compound pk entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class CompoundPKEntryPersistenceImpl
	extends BasePersistenceImpl<CompoundPKEntry, NoSuchCompoundPKEntryException>
	implements CompoundPKEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CompoundPKEntryUtil</code> to access the compound pk entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CompoundPKEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CompoundPKEntry, NoSuchCompoundPKEntryException>
			_collectionPersistenceFinderByC_CN;

	/**
	 * Returns an ordered range of all the compound pk entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CompoundPKEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of compound pk entries
	 * @param end the upper bound of the range of compound pk entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching compound pk entries
	 */
	@Override
	public List<CompoundPKEntry> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<CompoundPKEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CN.find(
			finderCache, new Object[] {companyId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first compound pk entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching compound pk entry
	 * @throws NoSuchCompoundPKEntryException if a matching compound pk entry could not be found
	 */
	@Override
	public CompoundPKEntry findByC_CN_First(
			long companyId, long classNameId,
			OrderByComparator<CompoundPKEntry> orderByComparator)
		throws NoSuchCompoundPKEntryException {

		return _collectionPersistenceFinderByC_CN.findFirst(
			finderCache, new Object[] {companyId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first compound pk entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching compound pk entry, or <code>null</code> if a matching compound pk entry could not be found
	 */
	@Override
	public CompoundPKEntry fetchByC_CN_First(
		long companyId, long classNameId,
		OrderByComparator<CompoundPKEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_CN.fetchFirst(
			finderCache, new Object[] {companyId, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the compound pk entries where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_CN(long companyId, long classNameId) {
		_collectionPersistenceFinderByC_CN.remove(
			finderCache, new Object[] {companyId, classNameId});
	}

	/**
	 * Returns the number of compound pk entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching compound pk entries
	 */
	@Override
	public int countByC_CN(long companyId, long classNameId) {
		return _collectionPersistenceFinderByC_CN.count(
			finderCache, new Object[] {companyId, classNameId});
	}

	private UniquePersistenceFinder
		<CompoundPKEntry, NoSuchCompoundPKEntryException>
			_uniquePersistenceFinderByC_N;

	/**
	 * Returns the compound pk entry where companyId = &#63; and name = &#63; or throws a <code>NoSuchCompoundPKEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching compound pk entry
	 * @throws NoSuchCompoundPKEntryException if a matching compound pk entry could not be found
	 */
	@Override
	public CompoundPKEntry findByC_N(long companyId, String name)
		throws NoSuchCompoundPKEntryException {

		return _uniquePersistenceFinderByC_N.find(
			finderCache, new Object[] {companyId, name});
	}

	/**
	 * Returns the compound pk entry where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching compound pk entry, or <code>null</code> if a matching compound pk entry could not be found
	 */
	@Override
	public CompoundPKEntry fetchByC_N(
		long companyId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_N.fetch(
			finderCache, new Object[] {companyId, name}, useFinderCache);
	}

	/**
	 * Removes the compound pk entry where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the compound pk entry that was removed
	 */
	@Override
	public CompoundPKEntry removeByC_N(long companyId, String name)
		throws NoSuchCompoundPKEntryException {

		CompoundPKEntry compoundPKEntry = findByC_N(companyId, name);

		return remove(compoundPKEntry);
	}

	/**
	 * Returns the number of compound pk entries where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching compound pk entries
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		return _uniquePersistenceFinderByC_N.count(
			finderCache, new Object[] {companyId, name});
	}

	public CompoundPKEntryPersistenceImpl() {
		setModelClass(CompoundPKEntry.class);

		setModelImplClass(CompoundPKEntryImpl.class);
		setModelPKClass(CompoundPKEntryPK.class);

		setTable(CompoundPKEntryTable.INSTANCE);
	}

	/**
	 * Creates a new compound pk entry with the primary key. Does not add the compound pk entry to the database.
	 *
	 * @param compoundPKEntryPK the primary key for the new compound pk entry
	 * @return the new compound pk entry
	 */
	@Override
	public CompoundPKEntry create(CompoundPKEntryPK compoundPKEntryPK) {
		CompoundPKEntry compoundPKEntry = new CompoundPKEntryImpl();

		compoundPKEntry.setNew(true);
		compoundPKEntry.setPrimaryKey(compoundPKEntryPK);

		compoundPKEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return compoundPKEntry;
	}

	/**
	 * Removes the compound pk entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param compoundPKEntryPK the primary key of the compound pk entry
	 * @return the compound pk entry that was removed
	 * @throws NoSuchCompoundPKEntryException if a compound pk entry with the primary key could not be found
	 */
	@Override
	public CompoundPKEntry remove(CompoundPKEntryPK compoundPKEntryPK)
		throws NoSuchCompoundPKEntryException {

		return remove((Serializable)compoundPKEntryPK);
	}

	@Override
	protected CompoundPKEntry removeImpl(CompoundPKEntry compoundPKEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(compoundPKEntry)) {
				compoundPKEntry = (CompoundPKEntry)session.get(
					CompoundPKEntryImpl.class,
					compoundPKEntry.getPrimaryKeyObj());
			}

			if (compoundPKEntry != null) {
				session.delete(compoundPKEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (compoundPKEntry != null) {
			clearCache(compoundPKEntry);
		}

		return compoundPKEntry;
	}

	@Override
	public CompoundPKEntry updateImpl(CompoundPKEntry compoundPKEntry) {
		boolean isNew = compoundPKEntry.isNew();

		if (!(compoundPKEntry instanceof CompoundPKEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(compoundPKEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					compoundPKEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in compoundPKEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CompoundPKEntry implementation " +
					compoundPKEntry.getClass());
		}

		CompoundPKEntryModelImpl compoundPKEntryModelImpl =
			(CompoundPKEntryModelImpl)compoundPKEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(compoundPKEntry);
			}
			else {
				compoundPKEntry = (CompoundPKEntry)session.merge(
					compoundPKEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(compoundPKEntry, false);

		if (isNew) {
			compoundPKEntry.setNew(false);
		}

		compoundPKEntry.resetOriginalValues();

		return compoundPKEntry;
	}

	/**
	 * Returns the compound pk entry with the primary key or throws a <code>NoSuchCompoundPKEntryException</code> if it could not be found.
	 *
	 * @param compoundPKEntryPK the primary key of the compound pk entry
	 * @return the compound pk entry
	 * @throws NoSuchCompoundPKEntryException if a compound pk entry with the primary key could not be found
	 */
	@Override
	public CompoundPKEntry findByPrimaryKey(CompoundPKEntryPK compoundPKEntryPK)
		throws NoSuchCompoundPKEntryException {

		return findByPrimaryKey((Serializable)compoundPKEntryPK);
	}

	/**
	 * Returns the compound pk entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param compoundPKEntryPK the primary key of the compound pk entry
	 * @return the compound pk entry, or <code>null</code> if a compound pk entry with the primary key could not be found
	 */
	@Override
	public CompoundPKEntry fetchByPrimaryKey(
		CompoundPKEntryPK compoundPKEntryPK) {

		return fetchByPrimaryKey((Serializable)compoundPKEntryPK);
	}

	@Override
	public Set<String> getCompoundPKColumnNames() {
		return _compoundPKColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "compoundPKEntryPK";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMPOUNDPKENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CompoundPKEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the compound pk entry persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByC_CN = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CN",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CN",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_CN",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, false),
			_SQL_SELECT_COMPOUNDPKENTRY_WHERE, _SQL_COUNT_COMPOUNDPKENTRY_WHERE,
			CompoundPKEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"compoundPKEntry.", "id.companyId", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				CompoundPKEntry::getCompanyId),
			new FinderColumn<>(
				"compoundPKEntry.", "id.classNameId", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CompoundPKEntry::getClassNameId));

		_uniquePersistenceFinderByC_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "name"}, 0, 2, false,
				CompoundPKEntry::getCompanyId,
				convertNullFunction(CompoundPKEntry::getName)),
			_SQL_SELECT_COMPOUNDPKENTRY_WHERE, "",
			new FinderColumn<>(
				"compoundPKEntry.", "id.companyId", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				CompoundPKEntry::getCompanyId),
			new FinderColumn<>(
				"compoundPKEntry.", "name", FinderColumn.Type.STRING, "=", true,
				true, CompoundPKEntry::getName));

		CompoundPKEntryUtil.setPersistence(this);
	}

	public void destroy() {
		CompoundPKEntryUtil.setPersistence(null);

		entityCache.removeCache(CompoundPKEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CompoundPKEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMPOUNDPKENTRY =
		"SELECT compoundPKEntry FROM CompoundPKEntry compoundPKEntry";

	private static final String _SQL_SELECT_COMPOUNDPKENTRY_WHERE =
		"SELECT compoundPKEntry FROM CompoundPKEntry compoundPKEntry WHERE ";

	private static final String _SQL_COUNT_COMPOUNDPKENTRY_WHERE =
		"SELECT COUNT(compoundPKEntry) FROM CompoundPKEntry compoundPKEntry WHERE ";

	private static final Set<String> _compoundPKColumnNames = SetUtil.fromArray(
		new String[] {"companyId", "classNameId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:533101023