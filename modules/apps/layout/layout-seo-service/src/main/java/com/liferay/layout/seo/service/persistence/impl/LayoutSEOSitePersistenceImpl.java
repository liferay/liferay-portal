/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.service.persistence.impl;

import com.liferay.layout.seo.exception.NoSuchSiteException;
import com.liferay.layout.seo.model.LayoutSEOSite;
import com.liferay.layout.seo.model.LayoutSEOSiteTable;
import com.liferay.layout.seo.model.impl.LayoutSEOSiteImpl;
import com.liferay.layout.seo.model.impl.LayoutSEOSiteModelImpl;
import com.liferay.layout.seo.service.persistence.LayoutSEOSitePersistence;
import com.liferay.layout.seo.service.persistence.LayoutSEOSiteUtil;
import com.liferay.layout.seo.service.persistence.impl.constants.LayoutSEOPersistenceConstants;
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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
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
 * The persistence implementation for the layout seo site service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = LayoutSEOSitePersistence.class)
public class LayoutSEOSitePersistenceImpl
	extends BasePersistenceImpl<LayoutSEOSite, NoSuchSiteException>
	implements LayoutSEOSitePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutSEOSiteUtil</code> to access the layout seo site persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutSEOSiteImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<LayoutSEOSite, NoSuchSiteException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the layout seo sites where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSEOSiteModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout seo sites
	 * @param end the upper bound of the range of layout seo sites (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout seo sites
	 */
	@Override
	public List<LayoutSEOSite> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutSEOSite> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout seo site in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout seo site
	 * @throws NoSuchSiteException if a matching layout seo site could not be found
	 */
	@Override
	public LayoutSEOSite findByUuid_First(
			String uuid, OrderByComparator<LayoutSEOSite> orderByComparator)
		throws NoSuchSiteException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first layout seo site in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout seo site, or <code>null</code> if a matching layout seo site could not be found
	 */
	@Override
	public LayoutSEOSite fetchByUuid_First(
		String uuid, OrderByComparator<LayoutSEOSite> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the layout seo sites where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of layout seo sites where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout seo sites
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<LayoutSEOSite, NoSuchSiteException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the layout seo site where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchSiteException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout seo site
	 * @throws NoSuchSiteException if a matching layout seo site could not be found
	 */
	@Override
	public LayoutSEOSite findByUUID_G(String uuid, long groupId)
		throws NoSuchSiteException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the layout seo site where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout seo site, or <code>null</code> if a matching layout seo site could not be found
	 */
	@Override
	public LayoutSEOSite fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the layout seo site where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout seo site that was removed
	 */
	@Override
	public LayoutSEOSite removeByUUID_G(String uuid, long groupId)
		throws NoSuchSiteException {

		LayoutSEOSite layoutSEOSite = findByUUID_G(uuid, groupId);

		return remove(layoutSEOSite);
	}

	/**
	 * Returns the number of layout seo sites where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout seo sites
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<LayoutSEOSite, NoSuchSiteException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the layout seo sites where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSEOSiteModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout seo sites
	 * @param end the upper bound of the range of layout seo sites (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout seo sites
	 */
	@Override
	public List<LayoutSEOSite> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutSEOSite> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout seo site in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout seo site
	 * @throws NoSuchSiteException if a matching layout seo site could not be found
	 */
	@Override
	public LayoutSEOSite findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutSEOSite> orderByComparator)
		throws NoSuchSiteException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first layout seo site in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout seo site, or <code>null</code> if a matching layout seo site could not be found
	 */
	@Override
	public LayoutSEOSite fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutSEOSite> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the layout seo sites where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of layout seo sites where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout seo sites
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private UniquePersistenceFinder<LayoutSEOSite, NoSuchSiteException>
		_uniquePersistenceFinderByGroupId;

	/**
	 * Returns the layout seo site where groupId = &#63; or throws a <code>NoSuchSiteException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @return the matching layout seo site
	 * @throws NoSuchSiteException if a matching layout seo site could not be found
	 */
	@Override
	public LayoutSEOSite findByGroupId(long groupId)
		throws NoSuchSiteException {

		return _uniquePersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the layout seo site where groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout seo site, or <code>null</code> if a matching layout seo site could not be found
	 */
	@Override
	public LayoutSEOSite fetchByGroupId(long groupId, boolean useFinderCache) {
		return _uniquePersistenceFinderByGroupId.fetch(
			finderCache, new Object[] {groupId}, useFinderCache);
	}

	/**
	 * Removes the layout seo site where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @return the layout seo site that was removed
	 */
	@Override
	public LayoutSEOSite removeByGroupId(long groupId)
		throws NoSuchSiteException {

		LayoutSEOSite layoutSEOSite = findByGroupId(groupId);

		return remove(layoutSEOSite);
	}

	/**
	 * Returns the number of layout seo sites where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout seo sites
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _uniquePersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	public LayoutSEOSitePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutSEOSite.class);

		setModelImplClass(LayoutSEOSiteImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutSEOSiteTable.INSTANCE);
	}

	/**
	 * Creates a new layout seo site with the primary key. Does not add the layout seo site to the database.
	 *
	 * @param layoutSEOSiteId the primary key for the new layout seo site
	 * @return the new layout seo site
	 */
	@Override
	public LayoutSEOSite create(long layoutSEOSiteId) {
		LayoutSEOSite layoutSEOSite = new LayoutSEOSiteImpl();

		layoutSEOSite.setNew(true);
		layoutSEOSite.setPrimaryKey(layoutSEOSiteId);

		String uuid = PortalUUIDUtil.generate();

		layoutSEOSite.setUuid(uuid);

		layoutSEOSite.setCompanyId(CompanyThreadLocal.getCompanyId());

		return layoutSEOSite;
	}

	/**
	 * Removes the layout seo site with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutSEOSiteId the primary key of the layout seo site
	 * @return the layout seo site that was removed
	 * @throws NoSuchSiteException if a layout seo site with the primary key could not be found
	 */
	@Override
	public LayoutSEOSite remove(long layoutSEOSiteId)
		throws NoSuchSiteException {

		return remove((Serializable)layoutSEOSiteId);
	}

	@Override
	protected LayoutSEOSite removeImpl(LayoutSEOSite layoutSEOSite) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutSEOSite)) {
				layoutSEOSite = (LayoutSEOSite)session.get(
					LayoutSEOSiteImpl.class, layoutSEOSite.getPrimaryKeyObj());
			}

			if ((layoutSEOSite != null) &&
				ctPersistenceHelper.isRemove(layoutSEOSite)) {

				session.delete(layoutSEOSite);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutSEOSite != null) {
			clearCache(layoutSEOSite);
		}

		return layoutSEOSite;
	}

	@Override
	public LayoutSEOSite updateImpl(LayoutSEOSite layoutSEOSite) {
		boolean isNew = layoutSEOSite.isNew();

		if (!(layoutSEOSite instanceof LayoutSEOSiteModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(layoutSEOSite.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutSEOSite);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutSEOSite proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutSEOSite implementation " +
					layoutSEOSite.getClass());
		}

		LayoutSEOSiteModelImpl layoutSEOSiteModelImpl =
			(LayoutSEOSiteModelImpl)layoutSEOSite;

		if (Validator.isNull(layoutSEOSite.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			layoutSEOSite.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutSEOSite.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutSEOSite.setCreateDate(date);
			}
			else {
				layoutSEOSite.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!layoutSEOSiteModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutSEOSite.setModifiedDate(date);
			}
			else {
				layoutSEOSite.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(layoutSEOSite)) {
				if (!isNew) {
					session.evict(
						LayoutSEOSiteImpl.class,
						layoutSEOSite.getPrimaryKeyObj());
				}

				session.save(layoutSEOSite);
			}
			else {
				layoutSEOSite = (LayoutSEOSite)session.merge(layoutSEOSite);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layoutSEOSite, false);

		if (isNew) {
			layoutSEOSite.setNew(false);
		}

		layoutSEOSite.resetOriginalValues();

		return layoutSEOSite;
	}

	/**
	 * Returns the layout seo site with the primary key or throws a <code>NoSuchSiteException</code> if it could not be found.
	 *
	 * @param layoutSEOSiteId the primary key of the layout seo site
	 * @return the layout seo site
	 * @throws NoSuchSiteException if a layout seo site with the primary key could not be found
	 */
	@Override
	public LayoutSEOSite findByPrimaryKey(long layoutSEOSiteId)
		throws NoSuchSiteException {

		return findByPrimaryKey((Serializable)layoutSEOSiteId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the layout seo site with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutSEOSiteId the primary key of the layout seo site
	 * @return the layout seo site, or <code>null</code> if a layout seo site with the primary key could not be found
	 */
	@Override
	public LayoutSEOSite fetchByPrimaryKey(long layoutSEOSiteId) {
		return fetchByPrimaryKey((Serializable)layoutSEOSiteId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "layoutSEOSiteId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTSEOSITE;
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
		return LayoutSEOSiteModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LayoutSEOSite";
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
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("openGraphEnabled");
		ctMergeColumnNames.add("openGraphImageAlt");
		ctMergeColumnNames.add("openGraphImageFileEntryId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("layoutSEOSiteId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"groupId"});
	}

	/**
	 * Initializes the layout seo site persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_LAYOUTSEOSITE_WHERE, _SQL_COUNT_LAYOUTSEOSITE_WHERE,
			LayoutSEOSiteModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layoutSEOSite.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, LayoutSEOSite::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(LayoutSEOSite::getUuid),
				LayoutSEOSite::getGroupId),
			_SQL_SELECT_LAYOUTSEOSITE_WHERE, "",
			new FinderColumn<>(
				"layoutSEOSite.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, LayoutSEOSite::getUuid),
			new FinderColumn<>(
				"layoutSEOSite.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, LayoutSEOSite::getGroupId));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_LAYOUTSEOSITE_WHERE, _SQL_COUNT_LAYOUTSEOSITE_WHERE,
				LayoutSEOSiteModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"layoutSEOSite.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, LayoutSEOSite::getUuid),
				new FinderColumn<>(
					"layoutSEOSite.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, LayoutSEOSite::getCompanyId));

		_uniquePersistenceFinderByGroupId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByGroupId",
				new String[] {Long.class.getName()}, new String[] {"groupId"},
				0, 0, false, LayoutSEOSite::getGroupId),
			_SQL_SELECT_LAYOUTSEOSITE_WHERE, "",
			new FinderColumn<>(
				"layoutSEOSite.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, LayoutSEOSite::getGroupId));

		LayoutSEOSiteUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LayoutSEOSiteUtil.setPersistence(null);

		entityCache.removeCache(LayoutSEOSiteImpl.class.getName());
	}

	@Override
	@Reference(
		target = LayoutSEOPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = LayoutSEOPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = LayoutSEOPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		LayoutSEOSiteModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUTSEOSITE =
		"SELECT layoutSEOSite FROM LayoutSEOSite layoutSEOSite";

	private static final String _SQL_SELECT_LAYOUTSEOSITE_WHERE =
		"SELECT layoutSEOSite FROM LayoutSEOSite layoutSEOSite WHERE ";

	private static final String _SQL_COUNT_LAYOUTSEOSITE_WHERE =
		"SELECT COUNT(layoutSEOSite) FROM LayoutSEOSite layoutSEOSite WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LayoutSEOSite exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSEOSitePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-2142982200