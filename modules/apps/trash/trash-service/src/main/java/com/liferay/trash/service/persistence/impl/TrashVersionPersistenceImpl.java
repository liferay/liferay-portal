/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.service.persistence.impl;

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
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.trash.exception.NoSuchVersionException;
import com.liferay.trash.model.TrashVersion;
import com.liferay.trash.model.TrashVersionTable;
import com.liferay.trash.model.impl.TrashVersionImpl;
import com.liferay.trash.model.impl.TrashVersionModelImpl;
import com.liferay.trash.service.persistence.TrashVersionPersistence;
import com.liferay.trash.service.persistence.TrashVersionUtil;
import com.liferay.trash.service.persistence.impl.constants.TrashPersistenceConstants;

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
 * The persistence implementation for the trash version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = TrashVersionPersistence.class)
public class TrashVersionPersistenceImpl
	extends BasePersistenceImpl<TrashVersion, NoSuchVersionException>
	implements TrashVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>TrashVersionUtil</code> to access the trash version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		TrashVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<TrashVersion, NoSuchVersionException>
		_collectionPersistenceFinderByEntryId;

	/**
	 * Returns an ordered range of all the trash versions where entryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashVersionModelImpl</code>.
	 * </p>
	 *
	 * @param entryId the entry ID
	 * @param start the lower bound of the range of trash versions
	 * @param end the upper bound of the range of trash versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash versions
	 */
	@Override
	public List<TrashVersion> findByEntryId(
		long entryId, int start, int end,
		OrderByComparator<TrashVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByEntryId.find(
			finderCache, new Object[] {entryId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first trash version in the ordered set where entryId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash version
	 * @throws NoSuchVersionException if a matching trash version could not be found
	 */
	@Override
	public TrashVersion findByEntryId_First(
			long entryId, OrderByComparator<TrashVersion> orderByComparator)
		throws NoSuchVersionException {

		return _collectionPersistenceFinderByEntryId.findFirst(
			finderCache, new Object[] {entryId}, orderByComparator);
	}

	/**
	 * Returns the first trash version in the ordered set where entryId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash version, or <code>null</code> if a matching trash version could not be found
	 */
	@Override
	public TrashVersion fetchByEntryId_First(
		long entryId, OrderByComparator<TrashVersion> orderByComparator) {

		return _collectionPersistenceFinderByEntryId.fetchFirst(
			finderCache, new Object[] {entryId}, orderByComparator);
	}

	/**
	 * Removes all the trash versions where entryId = &#63; from the database.
	 *
	 * @param entryId the entry ID
	 */
	@Override
	public void removeByEntryId(long entryId) {
		_collectionPersistenceFinderByEntryId.remove(
			finderCache, new Object[] {entryId});
	}

	/**
	 * Returns the number of trash versions where entryId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @return the number of matching trash versions
	 */
	@Override
	public int countByEntryId(long entryId) {
		return _collectionPersistenceFinderByEntryId.count(
			finderCache, new Object[] {entryId});
	}

	private CollectionPersistenceFinder<TrashVersion, NoSuchVersionException>
		_collectionPersistenceFinderByE_CN;

	/**
	 * Returns an ordered range of all the trash versions where entryId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashVersionModelImpl</code>.
	 * </p>
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash versions
	 * @param end the upper bound of the range of trash versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash versions
	 */
	@Override
	public List<TrashVersion> findByE_CN(
		long entryId, long classNameId, int start, int end,
		OrderByComparator<TrashVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByE_CN.find(
			finderCache, new Object[] {entryId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first trash version in the ordered set where entryId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash version
	 * @throws NoSuchVersionException if a matching trash version could not be found
	 */
	@Override
	public TrashVersion findByE_CN_First(
			long entryId, long classNameId,
			OrderByComparator<TrashVersion> orderByComparator)
		throws NoSuchVersionException {

		return _collectionPersistenceFinderByE_CN.findFirst(
			finderCache, new Object[] {entryId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first trash version in the ordered set where entryId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash version, or <code>null</code> if a matching trash version could not be found
	 */
	@Override
	public TrashVersion fetchByE_CN_First(
		long entryId, long classNameId,
		OrderByComparator<TrashVersion> orderByComparator) {

		return _collectionPersistenceFinderByE_CN.fetchFirst(
			finderCache, new Object[] {entryId, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the trash versions where entryId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByE_CN(long entryId, long classNameId) {
		_collectionPersistenceFinderByE_CN.remove(
			finderCache, new Object[] {entryId, classNameId});
	}

	/**
	 * Returns the number of trash versions where entryId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @return the number of matching trash versions
	 */
	@Override
	public int countByE_CN(long entryId, long classNameId) {
		return _collectionPersistenceFinderByE_CN.count(
			finderCache, new Object[] {entryId, classNameId});
	}

	private UniquePersistenceFinder<TrashVersion, NoSuchVersionException>
		_uniquePersistenceFinderByCN_CPK;

	/**
	 * Returns the trash version where classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchVersionException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching trash version
	 * @throws NoSuchVersionException if a matching trash version could not be found
	 */
	@Override
	public TrashVersion findByCN_CPK(long classNameId, long classPK)
		throws NoSuchVersionException {

		return _uniquePersistenceFinderByCN_CPK.find(
			finderCache, new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the trash version where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching trash version, or <code>null</code> if a matching trash version could not be found
	 */
	@Override
	public TrashVersion fetchByCN_CPK(
		long classNameId, long classPK, boolean useFinderCache) {

		return _uniquePersistenceFinderByCN_CPK.fetch(
			finderCache, new Object[] {classNameId, classPK}, useFinderCache);
	}

	/**
	 * Removes the trash version where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the trash version that was removed
	 */
	@Override
	public TrashVersion removeByCN_CPK(long classNameId, long classPK)
		throws NoSuchVersionException {

		TrashVersion trashVersion = findByCN_CPK(classNameId, classPK);

		return remove(trashVersion);
	}

	/**
	 * Returns the number of trash versions where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching trash versions
	 */
	@Override
	public int countByCN_CPK(long classNameId, long classPK) {
		return _uniquePersistenceFinderByCN_CPK.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	public TrashVersionPersistenceImpl() {
		setModelClass(TrashVersion.class);

		setModelImplClass(TrashVersionImpl.class);
		setModelPKClass(long.class);

		setTable(TrashVersionTable.INSTANCE);
	}

	/**
	 * Creates a new trash version with the primary key. Does not add the trash version to the database.
	 *
	 * @param versionId the primary key for the new trash version
	 * @return the new trash version
	 */
	@Override
	public TrashVersion create(long versionId) {
		TrashVersion trashVersion = new TrashVersionImpl();

		trashVersion.setNew(true);
		trashVersion.setPrimaryKey(versionId);

		trashVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return trashVersion;
	}

	/**
	 * Removes the trash version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param versionId the primary key of the trash version
	 * @return the trash version that was removed
	 * @throws NoSuchVersionException if a trash version with the primary key could not be found
	 */
	@Override
	public TrashVersion remove(long versionId) throws NoSuchVersionException {
		return remove((Serializable)versionId);
	}

	@Override
	protected TrashVersion removeImpl(TrashVersion trashVersion) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(trashVersion)) {
				trashVersion = (TrashVersion)session.get(
					TrashVersionImpl.class, trashVersion.getPrimaryKeyObj());
			}

			if ((trashVersion != null) &&
				ctPersistenceHelper.isRemove(trashVersion)) {

				session.delete(trashVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (trashVersion != null) {
			clearCache(trashVersion);
		}

		return trashVersion;
	}

	@Override
	public TrashVersion updateImpl(TrashVersion trashVersion) {
		boolean isNew = trashVersion.isNew();

		if (!(trashVersion instanceof TrashVersionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(trashVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					trashVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in trashVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom TrashVersion implementation " +
					trashVersion.getClass());
		}

		TrashVersionModelImpl trashVersionModelImpl =
			(TrashVersionModelImpl)trashVersion;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(trashVersion)) {
				if (!isNew) {
					session.evict(
						TrashVersionImpl.class,
						trashVersion.getPrimaryKeyObj());
				}

				session.save(trashVersion);
			}
			else {
				trashVersion = (TrashVersion)session.merge(trashVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(trashVersion, false);

		if (isNew) {
			trashVersion.setNew(false);
		}

		trashVersion.resetOriginalValues();

		return trashVersion;
	}

	/**
	 * Returns the trash version with the primary key or throws a <code>NoSuchVersionException</code> if it could not be found.
	 *
	 * @param versionId the primary key of the trash version
	 * @return the trash version
	 * @throws NoSuchVersionException if a trash version with the primary key could not be found
	 */
	@Override
	public TrashVersion findByPrimaryKey(long versionId)
		throws NoSuchVersionException {

		return findByPrimaryKey((Serializable)versionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the trash version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param versionId the primary key of the trash version
	 * @return the trash version, or <code>null</code> if a trash version with the primary key could not be found
	 */
	@Override
	public TrashVersion fetchByPrimaryKey(long versionId) {
		return fetchByPrimaryKey((Serializable)versionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "versionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_TRASHVERSION;
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
		return TrashVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "TrashVersion";
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
		ctMergeColumnNames.add("entryId");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("typeSettings");
		ctMergeColumnNames.add("status");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("versionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"classNameId", "classPK"});
	}

	/**
	 * Initializes the trash version persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"entryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByEntryId",
					new String[] {Long.class.getName()},
					new String[] {"entryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByEntryId",
					new String[] {Long.class.getName()},
					new String[] {"entryId"}, false),
				_SQL_SELECT_TRASHVERSION_WHERE, _SQL_COUNT_TRASHVERSION_WHERE,
				TrashVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"trashVersion.", "entryId", FinderColumn.Type.LONG, "=",
					true, true, TrashVersion::getEntryId));

		_collectionPersistenceFinderByE_CN = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByE_CN",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"entryId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByE_CN",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"entryId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByE_CN",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"entryId", "classNameId"}, false),
			_SQL_SELECT_TRASHVERSION_WHERE, _SQL_COUNT_TRASHVERSION_WHERE,
			TrashVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"trashVersion.", "entryId", FinderColumn.Type.LONG, "=", true,
				true, TrashVersion::getEntryId),
			new FinderColumn<>(
				"trashVersion.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, TrashVersion::getClassNameId));

		_uniquePersistenceFinderByCN_CPK = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCN_CPK",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, 0, 0, false,
				TrashVersion::getClassNameId, TrashVersion::getClassPK),
			_SQL_SELECT_TRASHVERSION_WHERE, "",
			new FinderColumn<>(
				"trashVersion.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, TrashVersion::getClassNameId),
			new FinderColumn<>(
				"trashVersion.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, TrashVersion::getClassPK));

		TrashVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		TrashVersionUtil.setPersistence(null);

		entityCache.removeCache(TrashVersionImpl.class.getName());
	}

	@Override
	@Reference(
		target = TrashPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = TrashPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = TrashPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		TrashVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_TRASHVERSION =
		"SELECT trashVersion FROM TrashVersion trashVersion";

	private static final String _SQL_SELECT_TRASHVERSION_WHERE =
		"SELECT trashVersion FROM TrashVersion trashVersion WHERE ";

	private static final String _SQL_COUNT_TRASHVERSION_WHERE =
		"SELECT COUNT(trashVersion) FROM TrashVersion trashVersion WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-708048102