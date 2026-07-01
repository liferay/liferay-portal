/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl;

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
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchCTEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.CTEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.CTEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.CTEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.CTEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.CTEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.CTEntryUtil;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl.constants.SBCompat740PersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
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
 * The persistence implementation for the ct entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = CTEntryPersistence.class)
public class CTEntryPersistenceImpl
	extends BasePersistenceImpl<CTEntry, NoSuchCTEntryException>
	implements CTEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CTEntryUtil</code> to access the ct entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CTEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<CTEntry, NoSuchCTEntryException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the ct entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct entries
	 * @param end the upper bound of the range of ct entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct entries
	 */
	@Override
	public List<CTEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTEntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			dummyFinderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ct entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct entry
	 * @throws NoSuchCTEntryException if a matching ct entry could not be found
	 */
	@Override
	public CTEntry findByCompanyId_First(
			long companyId, OrderByComparator<CTEntry> orderByComparator)
		throws NoSuchCTEntryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			dummyFinderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first ct entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct entry, or <code>null</code> if a matching ct entry could not be found
	 */
	@Override
	public CTEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<CTEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			dummyFinderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the ct entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			dummyFinderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of ct entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching ct entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			dummyFinderCache, new Object[] {companyId});
	}

	private UniquePersistenceFinder<CTEntry, NoSuchCTEntryException>
		_uniquePersistenceFinderByC_N;

	/**
	 * Returns the ct entry where companyId = &#63; and name = &#63; or throws a <code>NoSuchCTEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching ct entry
	 * @throws NoSuchCTEntryException if a matching ct entry could not be found
	 */
	@Override
	public CTEntry findByC_N(long companyId, String name)
		throws NoSuchCTEntryException {

		return _uniquePersistenceFinderByC_N.find(
			dummyFinderCache, new Object[] {companyId, name});
	}

	/**
	 * Returns the ct entry where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ct entry, or <code>null</code> if a matching ct entry could not be found
	 */
	@Override
	public CTEntry fetchByC_N(
		long companyId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_N.fetch(
			dummyFinderCache, new Object[] {companyId, name}, useFinderCache);
	}

	/**
	 * Removes the ct entry where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the ct entry that was removed
	 */
	@Override
	public CTEntry removeByC_N(long companyId, String name)
		throws NoSuchCTEntryException {

		CTEntry ctEntry = findByC_N(companyId, name);

		return remove(ctEntry);
	}

	/**
	 * Returns the number of ct entries where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching ct entries
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		return _uniquePersistenceFinderByC_N.count(
			dummyFinderCache, new Object[] {companyId, name});
	}

	public CTEntryPersistenceImpl() {
		setModelClass(CTEntry.class);

		setModelImplClass(CTEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CTEntryTable.INSTANCE);
	}

	/**
	 * Creates a new ct entry with the primary key. Does not add the ct entry to the database.
	 *
	 * @param ctEntryId the primary key for the new ct entry
	 * @return the new ct entry
	 */
	@Override
	public CTEntry create(long ctEntryId) {
		CTEntry ctEntry = new CTEntryImpl();

		ctEntry.setNew(true);
		ctEntry.setPrimaryKey(ctEntryId);

		ctEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ctEntry;
	}

	/**
	 * Removes the ct entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctEntryId the primary key of the ct entry
	 * @return the ct entry that was removed
	 * @throws NoSuchCTEntryException if a ct entry with the primary key could not be found
	 */
	@Override
	public CTEntry remove(long ctEntryId) throws NoSuchCTEntryException {
		return remove((Serializable)ctEntryId);
	}

	@Override
	protected CTEntry removeImpl(CTEntry ctEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ctEntry)) {
				ctEntry = (CTEntry)session.get(
					CTEntryImpl.class, ctEntry.getPrimaryKeyObj());
			}

			if ((ctEntry != null) && ctPersistenceHelper.isRemove(ctEntry)) {
				session.delete(ctEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ctEntry != null) {
			clearCache(ctEntry);
		}

		return ctEntry;
	}

	@Override
	public CTEntry updateImpl(CTEntry ctEntry) {
		boolean isNew = ctEntry.isNew();

		if (!(ctEntry instanceof CTEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ctEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(ctEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ctEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CTEntry implementation " +
					ctEntry.getClass());
		}

		CTEntryModelImpl ctEntryModelImpl = (CTEntryModelImpl)ctEntry;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ctEntry)) {
				if (!isNew) {
					session.evict(
						CTEntryImpl.class, ctEntry.getPrimaryKeyObj());
				}

				session.save(ctEntry);
			}
			else {
				ctEntry = (CTEntry)session.merge(ctEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ctEntry, false);

		if (isNew) {
			ctEntry.setNew(false);
		}

		ctEntry.resetOriginalValues();

		return ctEntry;
	}

	/**
	 * Returns the ct entry with the primary key or throws a <code>NoSuchCTEntryException</code> if it could not be found.
	 *
	 * @param ctEntryId the primary key of the ct entry
	 * @return the ct entry
	 * @throws NoSuchCTEntryException if a ct entry with the primary key could not be found
	 */
	@Override
	public CTEntry findByPrimaryKey(long ctEntryId)
		throws NoSuchCTEntryException {

		return findByPrimaryKey((Serializable)ctEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the ct entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctEntryId the primary key of the ct entry
	 * @return the ct entry, or <code>null</code> if a ct entry with the primary key could not be found
	 */
	@Override
	public CTEntry fetchByPrimaryKey(long ctEntryId) {
		return fetchByPrimaryKey((Serializable)ctEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return dummyEntityCache;
	}

	@Override
	protected String getPKDBName() {
		return "ctEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CTENTRY;
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
		return CTEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CTEntry";
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
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("companyId");
		ctMergeColumnNames.add("name");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("ctEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"companyId", "name"});
	}

	/**
	 * Initializes the ct entry persistence.
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
				_SQL_SELECT_CTENTRY_WHERE, _SQL_COUNT_CTENTRY_WHERE,
				CTEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"ctEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, CTEntry::getCompanyId));

		_uniquePersistenceFinderByC_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "name"}, 0, 2, false,
				CTEntry::getCompanyId, convertNullFunction(CTEntry::getName)),
			_SQL_SELECT_CTENTRY_WHERE, "",
			new FinderColumn<>(
				"ctEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, CTEntry::getCompanyId),
			new FinderColumn<>(
				"ctEntry.", "name", FinderColumn.Type.STRING, "=", true, true,
				CTEntry::getName));

		CTEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CTEntryUtil.setPersistence(null);

		dummyEntityCache.removeCache(CTEntryImpl.class.getName());
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
	protected CTPersistenceHelper ctPersistenceHelper;

	private static final String _ENTITY_ALIAS_PREFIX =
		CTEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CTENTRY =
		"SELECT ctEntry FROM CTEntry ctEntry";

	private static final String _SQL_SELECT_CTENTRY_WHERE =
		"SELECT ctEntry FROM CTEntry ctEntry WHERE ";

	private static final String _SQL_COUNT_CTENTRY_WHERE =
		"SELECT COUNT(ctEntry) FROM CTEntry ctEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CTEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CTEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return dummyFinderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1562328602