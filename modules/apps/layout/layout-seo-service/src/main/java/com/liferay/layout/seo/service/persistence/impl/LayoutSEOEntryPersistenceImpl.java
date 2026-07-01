/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.service.persistence.impl;

import com.liferay.layout.seo.exception.NoSuchEntryException;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.model.LayoutSEOEntryTable;
import com.liferay.layout.seo.model.impl.LayoutSEOEntryImpl;
import com.liferay.layout.seo.model.impl.LayoutSEOEntryModelImpl;
import com.liferay.layout.seo.service.persistence.LayoutSEOEntryPersistence;
import com.liferay.layout.seo.service.persistence.LayoutSEOEntryUtil;
import com.liferay.layout.seo.service.persistence.impl.constants.LayoutSEOPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
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
 * The persistence implementation for the layout seo entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = LayoutSEOEntryPersistence.class)
public class LayoutSEOEntryPersistenceImpl
	extends BasePersistenceImpl<LayoutSEOEntry, NoSuchEntryException>
	implements LayoutSEOEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutSEOEntryUtil</code> to access the layout seo entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutSEOEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<LayoutSEOEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the layout seo entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSEOEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout seo entries
	 * @param end the upper bound of the range of layout seo entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout seo entries
	 */
	@Override
	public List<LayoutSEOEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutSEOEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout seo entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout seo entry
	 * @throws NoSuchEntryException if a matching layout seo entry could not be found
	 */
	@Override
	public LayoutSEOEntry findByUuid_First(
			String uuid, OrderByComparator<LayoutSEOEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first layout seo entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout seo entry, or <code>null</code> if a matching layout seo entry could not be found
	 */
	@Override
	public LayoutSEOEntry fetchByUuid_First(
		String uuid, OrderByComparator<LayoutSEOEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the layout seo entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of layout seo entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout seo entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<LayoutSEOEntry, NoSuchEntryException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the layout seo entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout seo entry
	 * @throws NoSuchEntryException if a matching layout seo entry could not be found
	 */
	@Override
	public LayoutSEOEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the layout seo entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout seo entry, or <code>null</code> if a matching layout seo entry could not be found
	 */
	@Override
	public LayoutSEOEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the layout seo entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout seo entry that was removed
	 */
	@Override
	public LayoutSEOEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException {

		LayoutSEOEntry layoutSEOEntry = findByUUID_G(uuid, groupId);

		return remove(layoutSEOEntry);
	}

	/**
	 * Returns the number of layout seo entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout seo entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<LayoutSEOEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the layout seo entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSEOEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout seo entries
	 * @param end the upper bound of the range of layout seo entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout seo entries
	 */
	@Override
	public List<LayoutSEOEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutSEOEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout seo entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout seo entry
	 * @throws NoSuchEntryException if a matching layout seo entry could not be found
	 */
	@Override
	public LayoutSEOEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutSEOEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first layout seo entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout seo entry, or <code>null</code> if a matching layout seo entry could not be found
	 */
	@Override
	public LayoutSEOEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutSEOEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the layout seo entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of layout seo entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout seo entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private UniquePersistenceFinder<LayoutSEOEntry, NoSuchEntryException>
		_uniquePersistenceFinderByG_P_L;

	/**
	 * Returns the layout seo entry where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @return the matching layout seo entry
	 * @throws NoSuchEntryException if a matching layout seo entry could not be found
	 */
	@Override
	public LayoutSEOEntry findByG_P_L(
			long groupId, boolean privateLayout, long layoutId)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByG_P_L.find(
			finderCache, new Object[] {groupId, privateLayout, layoutId});
	}

	/**
	 * Returns the layout seo entry where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout seo entry, or <code>null</code> if a matching layout seo entry could not be found
	 */
	@Override
	public LayoutSEOEntry fetchByG_P_L(
		long groupId, boolean privateLayout, long layoutId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_P_L.fetch(
			finderCache, new Object[] {groupId, privateLayout, layoutId},
			useFinderCache);
	}

	/**
	 * Removes the layout seo entry where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @return the layout seo entry that was removed
	 */
	@Override
	public LayoutSEOEntry removeByG_P_L(
			long groupId, boolean privateLayout, long layoutId)
		throws NoSuchEntryException {

		LayoutSEOEntry layoutSEOEntry = findByG_P_L(
			groupId, privateLayout, layoutId);

		return remove(layoutSEOEntry);
	}

	/**
	 * Returns the number of layout seo entries where groupId = &#63; and privateLayout = &#63; and layoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @return the number of matching layout seo entries
	 */
	@Override
	public int countByG_P_L(
		long groupId, boolean privateLayout, long layoutId) {

		return _uniquePersistenceFinderByG_P_L.count(
			finderCache, new Object[] {groupId, privateLayout, layoutId});
	}

	public LayoutSEOEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"openGraphImageFileEntryScopeERC", "openGraphImageFileEntrySERC");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutSEOEntry.class);

		setModelImplClass(LayoutSEOEntryImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutSEOEntryTable.INSTANCE);
	}

	/**
	 * Creates a new layout seo entry with the primary key. Does not add the layout seo entry to the database.
	 *
	 * @param layoutSEOEntryId the primary key for the new layout seo entry
	 * @return the new layout seo entry
	 */
	@Override
	public LayoutSEOEntry create(long layoutSEOEntryId) {
		LayoutSEOEntry layoutSEOEntry = new LayoutSEOEntryImpl();

		layoutSEOEntry.setNew(true);
		layoutSEOEntry.setPrimaryKey(layoutSEOEntryId);

		String uuid = PortalUUIDUtil.generate();

		layoutSEOEntry.setUuid(uuid);

		layoutSEOEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return layoutSEOEntry;
	}

	/**
	 * Removes the layout seo entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutSEOEntryId the primary key of the layout seo entry
	 * @return the layout seo entry that was removed
	 * @throws NoSuchEntryException if a layout seo entry with the primary key could not be found
	 */
	@Override
	public LayoutSEOEntry remove(long layoutSEOEntryId)
		throws NoSuchEntryException {

		return remove((Serializable)layoutSEOEntryId);
	}

	@Override
	protected LayoutSEOEntry removeImpl(LayoutSEOEntry layoutSEOEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutSEOEntry)) {
				layoutSEOEntry = (LayoutSEOEntry)session.get(
					LayoutSEOEntryImpl.class,
					layoutSEOEntry.getPrimaryKeyObj());
			}

			if ((layoutSEOEntry != null) &&
				ctPersistenceHelper.isRemove(layoutSEOEntry)) {

				session.delete(layoutSEOEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutSEOEntry != null) {
			clearCache(layoutSEOEntry);
		}

		return layoutSEOEntry;
	}

	@Override
	public LayoutSEOEntry updateImpl(LayoutSEOEntry layoutSEOEntry) {
		boolean isNew = layoutSEOEntry.isNew();

		if (!(layoutSEOEntry instanceof LayoutSEOEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(layoutSEOEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutSEOEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutSEOEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutSEOEntry implementation " +
					layoutSEOEntry.getClass());
		}

		LayoutSEOEntryModelImpl layoutSEOEntryModelImpl =
			(LayoutSEOEntryModelImpl)layoutSEOEntry;

		if (Validator.isNull(layoutSEOEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			layoutSEOEntry.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutSEOEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutSEOEntry.setCreateDate(date);
			}
			else {
				layoutSEOEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutSEOEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutSEOEntry.setModifiedDate(date);
			}
			else {
				layoutSEOEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(layoutSEOEntry)) {
				if (!isNew) {
					session.evict(
						LayoutSEOEntryImpl.class,
						layoutSEOEntry.getPrimaryKeyObj());
				}

				session.save(layoutSEOEntry);
			}
			else {
				layoutSEOEntry = (LayoutSEOEntry)session.merge(layoutSEOEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layoutSEOEntry, false);

		if (isNew) {
			layoutSEOEntry.setNew(false);
		}

		layoutSEOEntry.resetOriginalValues();

		return layoutSEOEntry;
	}

	/**
	 * Returns the layout seo entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param layoutSEOEntryId the primary key of the layout seo entry
	 * @return the layout seo entry
	 * @throws NoSuchEntryException if a layout seo entry with the primary key could not be found
	 */
	@Override
	public LayoutSEOEntry findByPrimaryKey(long layoutSEOEntryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)layoutSEOEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the layout seo entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutSEOEntryId the primary key of the layout seo entry
	 * @return the layout seo entry, or <code>null</code> if a layout seo entry with the primary key could not be found
	 */
	@Override
	public LayoutSEOEntry fetchByPrimaryKey(long layoutSEOEntryId) {
		return fetchByPrimaryKey((Serializable)layoutSEOEntryId);
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
		return "layoutSEOEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTSEOENTRY;
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
		return LayoutSEOEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LayoutSEOEntry";
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
		ctMergeColumnNames.add("privateLayout");
		ctMergeColumnNames.add("layoutId");
		ctMergeColumnNames.add("canonicalURL");
		ctMergeColumnNames.add("canonicalURLEnabled");
		ctMergeColumnNames.add("openGraphDescription");
		ctMergeColumnNames.add("openGraphDescriptionEnabled");
		ctMergeColumnNames.add("openGraphImageAlt");
		ctMergeColumnNames.add("openGraphImageFileEntryERC");
		ctMergeColumnNames.add("openGraphImageFileEntrySERC");
		ctMergeColumnNames.add("openGraphTitle");
		ctMergeColumnNames.add("openGraphTitleEnabled");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("layoutSEOEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "privateLayout", "layoutId"});
	}

	/**
	 * Initializes the layout seo entry persistence.
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
			_SQL_SELECT_LAYOUTSEOENTRY_WHERE, _SQL_COUNT_LAYOUTSEOENTRY_WHERE,
			LayoutSEOEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"layoutSEOEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, LayoutSEOEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(LayoutSEOEntry::getUuid),
				LayoutSEOEntry::getGroupId),
			_SQL_SELECT_LAYOUTSEOENTRY_WHERE, "",
			new FinderColumn<>(
				"layoutSEOEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, LayoutSEOEntry::getUuid),
			new FinderColumn<>(
				"layoutSEOEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, LayoutSEOEntry::getGroupId));

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
				_SQL_SELECT_LAYOUTSEOENTRY_WHERE,
				_SQL_COUNT_LAYOUTSEOENTRY_WHERE,
				LayoutSEOEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"layoutSEOEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutSEOEntry::getUuid),
				new FinderColumn<>(
					"layoutSEOEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, LayoutSEOEntry::getCompanyId));

		_uniquePersistenceFinderByG_P_L = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_P_L",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "privateLayout", "layoutId"}, 0, 0,
				false, LayoutSEOEntry::getGroupId,
				LayoutSEOEntry::isPrivateLayout, LayoutSEOEntry::getLayoutId),
			_SQL_SELECT_LAYOUTSEOENTRY_WHERE, "",
			new FinderColumn<>(
				"layoutSEOEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, LayoutSEOEntry::getGroupId),
			new FinderColumn<>(
				"layoutSEOEntry.", "privateLayout", FinderColumn.Type.BOOLEAN,
				"=", true, true, LayoutSEOEntry::isPrivateLayout),
			new FinderColumn<>(
				"layoutSEOEntry.", "layoutId", FinderColumn.Type.LONG, "=",
				true, true, LayoutSEOEntry::getLayoutId));

		LayoutSEOEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LayoutSEOEntryUtil.setPersistence(null);

		entityCache.removeCache(LayoutSEOEntryImpl.class.getName());
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
		LayoutSEOEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUTSEOENTRY =
		"SELECT layoutSEOEntry FROM LayoutSEOEntry layoutSEOEntry";

	private static final String _SQL_SELECT_LAYOUTSEOENTRY_WHERE =
		"SELECT layoutSEOEntry FROM LayoutSEOEntry layoutSEOEntry WHERE ";

	private static final String _SQL_COUNT_LAYOUTSEOENTRY_WHERE =
		"SELECT COUNT(layoutSEOEntry) FROM LayoutSEOEntry layoutSEOEntry WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "openGraphImageFileEntryScopeERC"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-15793261