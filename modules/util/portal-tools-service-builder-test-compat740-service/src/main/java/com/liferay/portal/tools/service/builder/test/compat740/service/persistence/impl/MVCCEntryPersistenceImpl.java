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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchMVCCEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.MVCCEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.MVCCEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.MVCCEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.MVCCEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.MVCCEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.MVCCEntryUtil;
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
 * The persistence implementation for the mvcc entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = MVCCEntryPersistence.class)
public class MVCCEntryPersistenceImpl
	extends BasePersistenceImpl<MVCCEntry, NoSuchMVCCEntryException>
	implements MVCCEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>MVCCEntryUtil</code> to access the mvcc entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		MVCCEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<MVCCEntry, NoSuchMVCCEntryException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the mvcc entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MVCCEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of mvcc entries
	 * @param end the upper bound of the range of mvcc entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching mvcc entries
	 */
	@Override
	public List<MVCCEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<MVCCEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first mvcc entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching mvcc entry
	 * @throws NoSuchMVCCEntryException if a matching mvcc entry could not be found
	 */
	@Override
	public MVCCEntry findByCompanyId_First(
			long companyId, OrderByComparator<MVCCEntry> orderByComparator)
		throws NoSuchMVCCEntryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first mvcc entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching mvcc entry, or <code>null</code> if a matching mvcc entry could not be found
	 */
	@Override
	public MVCCEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<MVCCEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the mvcc entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of mvcc entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching mvcc entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private UniquePersistenceFinder<MVCCEntry, NoSuchMVCCEntryException>
		_uniquePersistenceFinderByC_N;

	/**
	 * Returns the mvcc entry where companyId = &#63; and name = &#63; or throws a <code>NoSuchMVCCEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching mvcc entry
	 * @throws NoSuchMVCCEntryException if a matching mvcc entry could not be found
	 */
	@Override
	public MVCCEntry findByC_N(long companyId, String name)
		throws NoSuchMVCCEntryException {

		return _uniquePersistenceFinderByC_N.find(
			finderCache, new Object[] {companyId, name});
	}

	/**
	 * Returns the mvcc entry where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching mvcc entry, or <code>null</code> if a matching mvcc entry could not be found
	 */
	@Override
	public MVCCEntry fetchByC_N(
		long companyId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_N.fetch(
			finderCache, new Object[] {companyId, name}, useFinderCache);
	}

	/**
	 * Removes the mvcc entry where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the mvcc entry that was removed
	 */
	@Override
	public MVCCEntry removeByC_N(long companyId, String name)
		throws NoSuchMVCCEntryException {

		MVCCEntry mvccEntry = findByC_N(companyId, name);

		return remove(mvccEntry);
	}

	/**
	 * Returns the number of mvcc entries where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching mvcc entries
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		return _uniquePersistenceFinderByC_N.count(
			finderCache, new Object[] {companyId, name});
	}

	public MVCCEntryPersistenceImpl() {
		setModelClass(MVCCEntry.class);

		setModelImplClass(MVCCEntryImpl.class);
		setModelPKClass(long.class);

		setTable(MVCCEntryTable.INSTANCE);
	}

	/**
	 * Creates a new mvcc entry with the primary key. Does not add the mvcc entry to the database.
	 *
	 * @param mvccEntryId the primary key for the new mvcc entry
	 * @return the new mvcc entry
	 */
	@Override
	public MVCCEntry create(long mvccEntryId) {
		MVCCEntry mvccEntry = new MVCCEntryImpl();

		mvccEntry.setNew(true);
		mvccEntry.setPrimaryKey(mvccEntryId);

		mvccEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return mvccEntry;
	}

	/**
	 * Removes the mvcc entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param mvccEntryId the primary key of the mvcc entry
	 * @return the mvcc entry that was removed
	 * @throws NoSuchMVCCEntryException if a mvcc entry with the primary key could not be found
	 */
	@Override
	public MVCCEntry remove(long mvccEntryId) throws NoSuchMVCCEntryException {
		return remove((Serializable)mvccEntryId);
	}

	@Override
	protected MVCCEntry removeImpl(MVCCEntry mvccEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(mvccEntry)) {
				mvccEntry = (MVCCEntry)session.get(
					MVCCEntryImpl.class, mvccEntry.getPrimaryKeyObj());
			}

			if (mvccEntry != null) {
				session.delete(mvccEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (mvccEntry != null) {
			clearCache(mvccEntry);
		}

		return mvccEntry;
	}

	@Override
	public MVCCEntry updateImpl(MVCCEntry mvccEntry) {
		boolean isNew = mvccEntry.isNew();

		if (!(mvccEntry instanceof MVCCEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(mvccEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(mvccEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in mvccEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom MVCCEntry implementation " +
					mvccEntry.getClass());
		}

		MVCCEntryModelImpl mvccEntryModelImpl = (MVCCEntryModelImpl)mvccEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(mvccEntry);
			}
			else {
				mvccEntry = (MVCCEntry)session.merge(mvccEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(mvccEntry, false);

		if (isNew) {
			mvccEntry.setNew(false);
		}

		mvccEntry.resetOriginalValues();

		return mvccEntry;
	}

	/**
	 * Returns the mvcc entry with the primary key or throws a <code>NoSuchMVCCEntryException</code> if it could not be found.
	 *
	 * @param mvccEntryId the primary key of the mvcc entry
	 * @return the mvcc entry
	 * @throws NoSuchMVCCEntryException if a mvcc entry with the primary key could not be found
	 */
	@Override
	public MVCCEntry findByPrimaryKey(long mvccEntryId)
		throws NoSuchMVCCEntryException {

		return findByPrimaryKey((Serializable)mvccEntryId);
	}

	/**
	 * Returns the mvcc entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param mvccEntryId the primary key of the mvcc entry
	 * @return the mvcc entry, or <code>null</code> if a mvcc entry with the primary key could not be found
	 */
	@Override
	public MVCCEntry fetchByPrimaryKey(long mvccEntryId) {
		return fetchByPrimaryKey((Serializable)mvccEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "mvccEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_MVCCENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return MVCCEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the mvcc entry persistence.
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
				_SQL_SELECT_MVCCENTRY_WHERE, _SQL_COUNT_MVCCENTRY_WHERE,
				MVCCEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"mvccEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, MVCCEntry::getCompanyId));

		_uniquePersistenceFinderByC_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "name"}, 0, 2, false,
				MVCCEntry::getCompanyId,
				convertNullFunction(MVCCEntry::getName)),
			_SQL_SELECT_MVCCENTRY_WHERE, "",
			new FinderColumn<>(
				"mvccEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, MVCCEntry::getCompanyId),
			new FinderColumn<>(
				"mvccEntry.", "name", FinderColumn.Type.STRING, "=", true, true,
				MVCCEntry::getName));

		MVCCEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		MVCCEntryUtil.setPersistence(null);

		entityCache.removeCache(MVCCEntryImpl.class.getName());
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
		MVCCEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_MVCCENTRY =
		"SELECT mvccEntry FROM MVCCEntry mvccEntry";

	private static final String _SQL_SELECT_MVCCENTRY_WHERE =
		"SELECT mvccEntry FROM MVCCEntry mvccEntry WHERE ";

	private static final String _SQL_COUNT_MVCCENTRY_WHERE =
		"SELECT COUNT(mvccEntry) FROM MVCCEntry mvccEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No MVCCEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		MVCCEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-943121120