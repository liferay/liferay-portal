/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchRedundantIndexEntryException;
import com.liferay.portal.tools.service.builder.test.model.RedundantIndexEntry;
import com.liferay.portal.tools.service.builder.test.model.RedundantIndexEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.RedundantIndexEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.RedundantIndexEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.RedundantIndexEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.RedundantIndexEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Map;

/**
 * The persistence implementation for the redundant index entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class RedundantIndexEntryPersistenceImpl
	extends BasePersistenceImpl
		<RedundantIndexEntry, NoSuchRedundantIndexEntryException>
	implements RedundantIndexEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>RedundantIndexEntryUtil</code> to access the redundant index entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		RedundantIndexEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<RedundantIndexEntry, NoSuchRedundantIndexEntryException>
			_uniquePersistenceFinderByC_N;

	/**
	 * Returns the redundant index entry where companyId = &#63; and name = &#63; or throws a <code>NoSuchRedundantIndexEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching redundant index entry
	 * @throws NoSuchRedundantIndexEntryException if a matching redundant index entry could not be found
	 */
	@Override
	public RedundantIndexEntry findByC_N(long companyId, String name)
		throws NoSuchRedundantIndexEntryException {

		return _uniquePersistenceFinderByC_N.find(
			finderCache, new Object[] {companyId, name});
	}

	/**
	 * Returns the redundant index entry where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching redundant index entry, or <code>null</code> if a matching redundant index entry could not be found
	 */
	@Override
	public RedundantIndexEntry fetchByC_N(
		long companyId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_N.fetch(
			finderCache, new Object[] {companyId, name}, useFinderCache);
	}

	/**
	 * Removes the redundant index entry where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the redundant index entry that was removed
	 */
	@Override
	public RedundantIndexEntry removeByC_N(long companyId, String name)
		throws NoSuchRedundantIndexEntryException {

		RedundantIndexEntry redundantIndexEntry = findByC_N(companyId, name);

		return remove(redundantIndexEntry);
	}

	/**
	 * Returns the number of redundant index entries where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching redundant index entries
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		return _uniquePersistenceFinderByC_N.count(
			finderCache, new Object[] {companyId, name});
	}

	public RedundantIndexEntryPersistenceImpl() {
		setModelClass(RedundantIndexEntry.class);

		setModelImplClass(RedundantIndexEntryImpl.class);
		setModelPKClass(long.class);

		setTable(RedundantIndexEntryTable.INSTANCE);
	}

	/**
	 * Creates a new redundant index entry with the primary key. Does not add the redundant index entry to the database.
	 *
	 * @param redundantIndexEntryId the primary key for the new redundant index entry
	 * @return the new redundant index entry
	 */
	@Override
	public RedundantIndexEntry create(long redundantIndexEntryId) {
		RedundantIndexEntry redundantIndexEntry = new RedundantIndexEntryImpl();

		redundantIndexEntry.setNew(true);
		redundantIndexEntry.setPrimaryKey(redundantIndexEntryId);

		redundantIndexEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return redundantIndexEntry;
	}

	/**
	 * Removes the redundant index entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param redundantIndexEntryId the primary key of the redundant index entry
	 * @return the redundant index entry that was removed
	 * @throws NoSuchRedundantIndexEntryException if a redundant index entry with the primary key could not be found
	 */
	@Override
	public RedundantIndexEntry remove(long redundantIndexEntryId)
		throws NoSuchRedundantIndexEntryException {

		return remove((Serializable)redundantIndexEntryId);
	}

	@Override
	protected RedundantIndexEntry removeImpl(
		RedundantIndexEntry redundantIndexEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(redundantIndexEntry)) {
				redundantIndexEntry = (RedundantIndexEntry)session.get(
					RedundantIndexEntryImpl.class,
					redundantIndexEntry.getPrimaryKeyObj());
			}

			if (redundantIndexEntry != null) {
				session.delete(redundantIndexEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (redundantIndexEntry != null) {
			clearCache(redundantIndexEntry);
		}

		return redundantIndexEntry;
	}

	@Override
	public RedundantIndexEntry updateImpl(
		RedundantIndexEntry redundantIndexEntry) {

		boolean isNew = redundantIndexEntry.isNew();

		if (!(redundantIndexEntry instanceof RedundantIndexEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(redundantIndexEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					redundantIndexEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in redundantIndexEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom RedundantIndexEntry implementation " +
					redundantIndexEntry.getClass());
		}

		RedundantIndexEntryModelImpl redundantIndexEntryModelImpl =
			(RedundantIndexEntryModelImpl)redundantIndexEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(redundantIndexEntry);
			}
			else {
				redundantIndexEntry = (RedundantIndexEntry)session.merge(
					redundantIndexEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(redundantIndexEntry, false);

		if (isNew) {
			redundantIndexEntry.setNew(false);
		}

		redundantIndexEntry.resetOriginalValues();

		return redundantIndexEntry;
	}

	/**
	 * Returns the redundant index entry with the primary key or throws a <code>NoSuchRedundantIndexEntryException</code> if it could not be found.
	 *
	 * @param redundantIndexEntryId the primary key of the redundant index entry
	 * @return the redundant index entry
	 * @throws NoSuchRedundantIndexEntryException if a redundant index entry with the primary key could not be found
	 */
	@Override
	public RedundantIndexEntry findByPrimaryKey(long redundantIndexEntryId)
		throws NoSuchRedundantIndexEntryException {

		return findByPrimaryKey((Serializable)redundantIndexEntryId);
	}

	/**
	 * Returns the redundant index entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param redundantIndexEntryId the primary key of the redundant index entry
	 * @return the redundant index entry, or <code>null</code> if a redundant index entry with the primary key could not be found
	 */
	@Override
	public RedundantIndexEntry fetchByPrimaryKey(long redundantIndexEntryId) {
		return fetchByPrimaryKey((Serializable)redundantIndexEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "redundantIndexEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_REDUNDANTINDEXENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return RedundantIndexEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the redundant index entry persistence.
	 */
	public void afterPropertiesSet() {
		_uniquePersistenceFinderByC_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "name"}, 0, 2, false,
				RedundantIndexEntry::getCompanyId,
				convertNullFunction(RedundantIndexEntry::getName)),
			_SQL_SELECT_REDUNDANTINDEXENTRY_WHERE, "",
			new FinderColumn<>(
				"redundantIndexEntry.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, RedundantIndexEntry::getCompanyId),
			new FinderColumn<>(
				"redundantIndexEntry.", "name", FinderColumn.Type.STRING, "=",
				true, true, RedundantIndexEntry::getName));

		RedundantIndexEntryUtil.setPersistence(this);
	}

	public void destroy() {
		RedundantIndexEntryUtil.setPersistence(null);

		entityCache.removeCache(RedundantIndexEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_REDUNDANTINDEXENTRY =
		"SELECT redundantIndexEntry FROM RedundantIndexEntry redundantIndexEntry";

	private static final String _SQL_SELECT_REDUNDANTINDEXENTRY_WHERE =
		"SELECT redundantIndexEntry FROM RedundantIndexEntry redundantIndexEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No RedundantIndexEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		RedundantIndexEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1537778752