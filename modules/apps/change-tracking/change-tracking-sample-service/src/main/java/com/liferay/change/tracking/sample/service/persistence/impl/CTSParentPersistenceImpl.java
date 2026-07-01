/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service.persistence.impl;

import com.liferay.change.tracking.sample.exception.NoSuchCTSParentException;
import com.liferay.change.tracking.sample.model.CTSParent;
import com.liferay.change.tracking.sample.model.CTSParentTable;
import com.liferay.change.tracking.sample.model.impl.CTSParentImpl;
import com.liferay.change.tracking.sample.model.impl.CTSParentModelImpl;
import com.liferay.change.tracking.sample.service.persistence.CTSParentPersistence;
import com.liferay.change.tracking.sample.service.persistence.CTSParentUtil;
import com.liferay.change.tracking.sample.service.persistence.impl.constants.CTSPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

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
 * The persistence implementation for the cts parent service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = CTSParentPersistence.class)
public class CTSParentPersistenceImpl
	extends BasePersistenceImpl<CTSParent, NoSuchCTSParentException>
	implements CTSParentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CTSParentUtil</code> to access the cts parent persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CTSParentImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<CTSParent, NoSuchCTSParentException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the cts parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts parents
	 */
	@Override
	public List<CTSParent> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTSParent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cts parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts parent
	 * @throws NoSuchCTSParentException if a matching cts parent could not be found
	 */
	@Override
	public CTSParent findByCompanyId_First(
			long companyId, OrderByComparator<CTSParent> orderByComparator)
		throws NoSuchCTSParentException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first cts parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts parent, or <code>null</code> if a matching cts parent could not be found
	 */
	@Override
	public CTSParent fetchByCompanyId_First(
		long companyId, OrderByComparator<CTSParent> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the cts parents where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of cts parents where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cts parents
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder<CTSParent, NoSuchCTSParentException>
		_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the cts parents where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts parents
	 */
	@Override
	public List<CTSParent> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end,
		OrderByComparator<CTSParent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {companyId, ctsGrandParentId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cts parent in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts parent
	 * @throws NoSuchCTSParentException if a matching cts parent could not be found
	 */
	@Override
	public CTSParent findByC_C_First(
			long companyId, long ctsGrandParentId,
			OrderByComparator<CTSParent> orderByComparator)
		throws NoSuchCTSParentException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {companyId, ctsGrandParentId},
			orderByComparator);
	}

	/**
	 * Returns the first cts parent in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts parent, or <code>null</code> if a matching cts parent could not be found
	 */
	@Override
	public CTSParent fetchByC_C_First(
		long companyId, long ctsGrandParentId,
		OrderByComparator<CTSParent> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {companyId, ctsGrandParentId},
			orderByComparator);
	}

	/**
	 * Removes all the cts parents where companyId = &#63; and ctsGrandParentId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 */
	@Override
	public void removeByC_C(long companyId, long ctsGrandParentId) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {companyId, ctsGrandParentId});
	}

	/**
	 * Returns the number of cts parents where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @return the number of matching cts parents
	 */
	@Override
	public int countByC_C(long companyId, long ctsGrandParentId) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {companyId, ctsGrandParentId});
	}

	public CTSParentPersistenceImpl() {
		setModelClass(CTSParent.class);

		setModelImplClass(CTSParentImpl.class);
		setModelPKClass(long.class);

		setTable(CTSParentTable.INSTANCE);
	}

	/**
	 * Creates a new cts parent with the primary key. Does not add the cts parent to the database.
	 *
	 * @param ctsParentId the primary key for the new cts parent
	 * @return the new cts parent
	 */
	@Override
	public CTSParent create(long ctsParentId) {
		CTSParent ctsParent = new CTSParentImpl();

		ctsParent.setNew(true);
		ctsParent.setPrimaryKey(ctsParentId);

		ctsParent.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ctsParent;
	}

	/**
	 * Removes the cts parent with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctsParentId the primary key of the cts parent
	 * @return the cts parent that was removed
	 * @throws NoSuchCTSParentException if a cts parent with the primary key could not be found
	 */
	@Override
	public CTSParent remove(long ctsParentId) throws NoSuchCTSParentException {
		return remove((Serializable)ctsParentId);
	}

	@Override
	protected CTSParent removeImpl(CTSParent ctsParent) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ctsParent)) {
				ctsParent = (CTSParent)session.get(
					CTSParentImpl.class, ctsParent.getPrimaryKeyObj());
			}

			if ((ctsParent != null) &&
				ctPersistenceHelper.isRemove(ctsParent)) {

				session.delete(ctsParent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ctsParent != null) {
			clearCache(ctsParent);
		}

		return ctsParent;
	}

	@Override
	public CTSParent updateImpl(CTSParent ctsParent) {
		boolean isNew = ctsParent.isNew();

		if (!(ctsParent instanceof CTSParentModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ctsParent.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(ctsParent);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ctsParent proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CTSParent implementation " +
					ctsParent.getClass());
		}

		CTSParentModelImpl ctsParentModelImpl = (CTSParentModelImpl)ctsParent;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ctsParent)) {
				if (!isNew) {
					session.evict(
						CTSParentImpl.class, ctsParent.getPrimaryKeyObj());
				}

				session.save(ctsParent);
			}
			else {
				ctsParent = (CTSParent)session.merge(ctsParent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ctsParent, false);

		if (isNew) {
			ctsParent.setNew(false);
		}

		ctsParent.resetOriginalValues();

		return ctsParent;
	}

	/**
	 * Returns the cts parent with the primary key or throws a <code>NoSuchCTSParentException</code> if it could not be found.
	 *
	 * @param ctsParentId the primary key of the cts parent
	 * @return the cts parent
	 * @throws NoSuchCTSParentException if a cts parent with the primary key could not be found
	 */
	@Override
	public CTSParent findByPrimaryKey(long ctsParentId)
		throws NoSuchCTSParentException {

		return findByPrimaryKey((Serializable)ctsParentId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cts parent with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctsParentId the primary key of the cts parent
	 * @return the cts parent, or <code>null</code> if a cts parent with the primary key could not be found
	 */
	@Override
	public CTSParent fetchByPrimaryKey(long ctsParentId) {
		return fetchByPrimaryKey((Serializable)ctsParentId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "ctsParentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CTSPARENT;
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
		return CTSParentModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CTSParent";
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
		ctMergeColumnNames.add("ctsGrandParentId");
		ctMergeColumnNames.add("name");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("ctsParentId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the cts parent persistence.
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
				_SQL_SELECT_CTSPARENT_WHERE, _SQL_COUNT_CTSPARENT_WHERE,
				CTSParentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"ctsParent.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, CTSParent::getCompanyId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "ctsGrandParentId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "ctsGrandParentId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "ctsGrandParentId"}, false),
			_SQL_SELECT_CTSPARENT_WHERE, _SQL_COUNT_CTSPARENT_WHERE,
			CTSParentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"ctsParent.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, CTSParent::getCompanyId),
			new FinderColumn<>(
				"ctsParent.", "ctsGrandParentId", FinderColumn.Type.LONG, "=",
				true, true, CTSParent::getCtsGrandParentId));

		CTSParentUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CTSParentUtil.setPersistence(null);

		entityCache.removeCache(CTSParentImpl.class.getName());
	}

	@Override
	@Reference(
		target = CTSPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CTSPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CTSPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		CTSParentModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CTSPARENT =
		"SELECT ctsParent FROM CTSParent ctsParent";

	private static final String _SQL_SELECT_CTSPARENT_WHERE =
		"SELECT ctsParent FROM CTSParent ctsParent WHERE ";

	private static final String _SQL_COUNT_CTSPARENT_WHERE =
		"SELECT COUNT(ctsParent) FROM CTSParent ctsParent WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1041992044