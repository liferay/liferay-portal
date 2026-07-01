/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.exception.NoSuchStructureLinkException;
import com.liferay.dynamic.data.mapping.model.DDMStructureLink;
import com.liferay.dynamic.data.mapping.model.DDMStructureLinkTable;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureLinkImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureLinkModelImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureLinkPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureLinkUtil;
import com.liferay.dynamic.data.mapping.service.persistence.impl.constants.DDMPersistenceConstants;
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
 * The persistence implementation for the ddm structure link service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDMStructureLinkPersistence.class)
public class DDMStructureLinkPersistenceImpl
	extends BasePersistenceImpl<DDMStructureLink, NoSuchStructureLinkException>
	implements DDMStructureLinkPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDMStructureLinkUtil</code> to access the ddm structure link persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDMStructureLinkImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DDMStructureLink, NoSuchStructureLinkException>
			_collectionPersistenceFinderByStructureId;

	/**
	 * Returns an ordered range of all the ddm structure links where structureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureLinkModelImpl</code>.
	 * </p>
	 *
	 * @param structureId the structure ID
	 * @param start the lower bound of the range of ddm structure links
	 * @param end the upper bound of the range of ddm structure links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structure links
	 */
	@Override
	public List<DDMStructureLink> findByStructureId(
		long structureId, int start, int end,
		OrderByComparator<DDMStructureLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByStructureId.find(
			finderCache, new Object[] {structureId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure link in the ordered set where structureId = &#63;.
	 *
	 * @param structureId the structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure link
	 * @throws NoSuchStructureLinkException if a matching ddm structure link could not be found
	 */
	@Override
	public DDMStructureLink findByStructureId_First(
			long structureId,
			OrderByComparator<DDMStructureLink> orderByComparator)
		throws NoSuchStructureLinkException {

		return _collectionPersistenceFinderByStructureId.findFirst(
			finderCache, new Object[] {structureId}, orderByComparator);
	}

	/**
	 * Returns the first ddm structure link in the ordered set where structureId = &#63;.
	 *
	 * @param structureId the structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure link, or <code>null</code> if a matching ddm structure link could not be found
	 */
	@Override
	public DDMStructureLink fetchByStructureId_First(
		long structureId,
		OrderByComparator<DDMStructureLink> orderByComparator) {

		return _collectionPersistenceFinderByStructureId.fetchFirst(
			finderCache, new Object[] {structureId}, orderByComparator);
	}

	/**
	 * Removes all the ddm structure links where structureId = &#63; from the database.
	 *
	 * @param structureId the structure ID
	 */
	@Override
	public void removeByStructureId(long structureId) {
		_collectionPersistenceFinderByStructureId.remove(
			finderCache, new Object[] {structureId});
	}

	/**
	 * Returns the number of ddm structure links where structureId = &#63;.
	 *
	 * @param structureId the structure ID
	 * @return the number of matching ddm structure links
	 */
	@Override
	public int countByStructureId(long structureId) {
		return _collectionPersistenceFinderByStructureId.count(
			finderCache, new Object[] {structureId});
	}

	private CollectionPersistenceFinder
		<DDMStructureLink, NoSuchStructureLinkException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the ddm structure links where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureLinkModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm structure links
	 * @param end the upper bound of the range of ddm structure links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structure links
	 */
	@Override
	public List<DDMStructureLink> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<DDMStructureLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure link in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure link
	 * @throws NoSuchStructureLinkException if a matching ddm structure link could not be found
	 */
	@Override
	public DDMStructureLink findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<DDMStructureLink> orderByComparator)
		throws NoSuchStructureLinkException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first ddm structure link in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure link, or <code>null</code> if a matching ddm structure link could not be found
	 */
	@Override
	public DDMStructureLink fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<DDMStructureLink> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the ddm structure links where classNameId = &#63; and classPK = &#63; from the database.
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
	 * Returns the number of ddm structure links where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching ddm structure links
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private UniquePersistenceFinder
		<DDMStructureLink, NoSuchStructureLinkException>
			_uniquePersistenceFinderByC_C_S;

	/**
	 * Returns the ddm structure link where classNameId = &#63; and classPK = &#63; and structureId = &#63; or throws a <code>NoSuchStructureLinkException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param structureId the structure ID
	 * @return the matching ddm structure link
	 * @throws NoSuchStructureLinkException if a matching ddm structure link could not be found
	 */
	@Override
	public DDMStructureLink findByC_C_S(
			long classNameId, long classPK, long structureId)
		throws NoSuchStructureLinkException {

		return _uniquePersistenceFinderByC_C_S.find(
			finderCache, new Object[] {classNameId, classPK, structureId});
	}

	/**
	 * Returns the ddm structure link where classNameId = &#63; and classPK = &#63; and structureId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param structureId the structure ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm structure link, or <code>null</code> if a matching ddm structure link could not be found
	 */
	@Override
	public DDMStructureLink fetchByC_C_S(
		long classNameId, long classPK, long structureId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_S.fetch(
			finderCache, new Object[] {classNameId, classPK, structureId},
			useFinderCache);
	}

	/**
	 * Removes the ddm structure link where classNameId = &#63; and classPK = &#63; and structureId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param structureId the structure ID
	 * @return the ddm structure link that was removed
	 */
	@Override
	public DDMStructureLink removeByC_C_S(
			long classNameId, long classPK, long structureId)
		throws NoSuchStructureLinkException {

		DDMStructureLink ddmStructureLink = findByC_C_S(
			classNameId, classPK, structureId);

		return remove(ddmStructureLink);
	}

	/**
	 * Returns the number of ddm structure links where classNameId = &#63; and classPK = &#63; and structureId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param structureId the structure ID
	 * @return the number of matching ddm structure links
	 */
	@Override
	public int countByC_C_S(long classNameId, long classPK, long structureId) {
		return _uniquePersistenceFinderByC_C_S.count(
			finderCache, new Object[] {classNameId, classPK, structureId});
	}

	public DDMStructureLinkPersistenceImpl() {
		setModelClass(DDMStructureLink.class);

		setModelImplClass(DDMStructureLinkImpl.class);
		setModelPKClass(long.class);

		setTable(DDMStructureLinkTable.INSTANCE);
	}

	/**
	 * Creates a new ddm structure link with the primary key. Does not add the ddm structure link to the database.
	 *
	 * @param structureLinkId the primary key for the new ddm structure link
	 * @return the new ddm structure link
	 */
	@Override
	public DDMStructureLink create(long structureLinkId) {
		DDMStructureLink ddmStructureLink = new DDMStructureLinkImpl();

		ddmStructureLink.setNew(true);
		ddmStructureLink.setPrimaryKey(structureLinkId);

		ddmStructureLink.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ddmStructureLink;
	}

	/**
	 * Removes the ddm structure link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param structureLinkId the primary key of the ddm structure link
	 * @return the ddm structure link that was removed
	 * @throws NoSuchStructureLinkException if a ddm structure link with the primary key could not be found
	 */
	@Override
	public DDMStructureLink remove(long structureLinkId)
		throws NoSuchStructureLinkException {

		return remove((Serializable)structureLinkId);
	}

	@Override
	protected DDMStructureLink removeImpl(DDMStructureLink ddmStructureLink) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddmStructureLink)) {
				ddmStructureLink = (DDMStructureLink)session.get(
					DDMStructureLinkImpl.class,
					ddmStructureLink.getPrimaryKeyObj());
			}

			if ((ddmStructureLink != null) &&
				ctPersistenceHelper.isRemove(ddmStructureLink)) {

				session.delete(ddmStructureLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmStructureLink != null) {
			clearCache(ddmStructureLink);
		}

		return ddmStructureLink;
	}

	@Override
	public DDMStructureLink updateImpl(DDMStructureLink ddmStructureLink) {
		boolean isNew = ddmStructureLink.isNew();

		if (!(ddmStructureLink instanceof DDMStructureLinkModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddmStructureLink.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ddmStructureLink);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddmStructureLink proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDMStructureLink implementation " +
					ddmStructureLink.getClass());
		}

		DDMStructureLinkModelImpl ddmStructureLinkModelImpl =
			(DDMStructureLinkModelImpl)ddmStructureLink;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddmStructureLink)) {
				if (!isNew) {
					session.evict(
						DDMStructureLinkImpl.class,
						ddmStructureLink.getPrimaryKeyObj());
				}

				session.save(ddmStructureLink);
			}
			else {
				ddmStructureLink = (DDMStructureLink)session.merge(
					ddmStructureLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ddmStructureLink, false);

		if (isNew) {
			ddmStructureLink.setNew(false);
		}

		ddmStructureLink.resetOriginalValues();

		return ddmStructureLink;
	}

	/**
	 * Returns the ddm structure link with the primary key or throws a <code>NoSuchStructureLinkException</code> if it could not be found.
	 *
	 * @param structureLinkId the primary key of the ddm structure link
	 * @return the ddm structure link
	 * @throws NoSuchStructureLinkException if a ddm structure link with the primary key could not be found
	 */
	@Override
	public DDMStructureLink findByPrimaryKey(long structureLinkId)
		throws NoSuchStructureLinkException {

		return findByPrimaryKey((Serializable)structureLinkId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the ddm structure link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param structureLinkId the primary key of the ddm structure link
	 * @return the ddm structure link, or <code>null</code> if a ddm structure link with the primary key could not be found
	 */
	@Override
	public DDMStructureLink fetchByPrimaryKey(long structureLinkId) {
		return fetchByPrimaryKey((Serializable)structureLinkId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "structureLinkId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDMSTRUCTURELINK;
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
		return DDMStructureLinkModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDMStructureLink";
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
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("structureId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("structureLinkId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"classNameId", "classPK", "structureId"});
	}

	/**
	 * Initializes the ddm structure link persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByStructureId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByStructureId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"structureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByStructureId", new String[] {Long.class.getName()},
					new String[] {"structureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByStructureId", new String[] {Long.class.getName()},
					new String[] {"structureId"}, false),
				_SQL_SELECT_DDMSTRUCTURELINK_WHERE,
				_SQL_COUNT_DDMSTRUCTURELINK_WHERE,
				DDMStructureLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"ddmStructureLink.", "structureId", FinderColumn.Type.LONG,
					"=", true, true, DDMStructureLink::getStructureId));

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
			_SQL_SELECT_DDMSTRUCTURELINK_WHERE,
			_SQL_COUNT_DDMSTRUCTURELINK_WHERE,
			DDMStructureLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"ddmStructureLink.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, DDMStructureLink::getClassNameId),
			new FinderColumn<>(
				"ddmStructureLink.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, DDMStructureLink::getClassPK));

		_uniquePersistenceFinderByC_C_S = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"classNameId", "classPK", "structureId"}, 0, 0,
				false, DDMStructureLink::getClassNameId,
				DDMStructureLink::getClassPK, DDMStructureLink::getStructureId),
			_SQL_SELECT_DDMSTRUCTURELINK_WHERE, "",
			new FinderColumn<>(
				"ddmStructureLink.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, DDMStructureLink::getClassNameId),
			new FinderColumn<>(
				"ddmStructureLink.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, DDMStructureLink::getClassPK),
			new FinderColumn<>(
				"ddmStructureLink.", "structureId", FinderColumn.Type.LONG, "=",
				true, true, DDMStructureLink::getStructureId));

		DDMStructureLinkUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDMStructureLinkUtil.setPersistence(null);

		entityCache.removeCache(DDMStructureLinkImpl.class.getName());
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		DDMStructureLinkModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DDMSTRUCTURELINK =
		"SELECT ddmStructureLink FROM DDMStructureLink ddmStructureLink";

	private static final String _SQL_SELECT_DDMSTRUCTURELINK_WHERE =
		"SELECT ddmStructureLink FROM DDMStructureLink ddmStructureLink WHERE ";

	private static final String _SQL_COUNT_DDMSTRUCTURELINK_WHERE =
		"SELECT COUNT(ddmStructureLink) FROM DDMStructureLink ddmStructureLink WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DDMStructureLink exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DDMStructureLinkPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:737499342