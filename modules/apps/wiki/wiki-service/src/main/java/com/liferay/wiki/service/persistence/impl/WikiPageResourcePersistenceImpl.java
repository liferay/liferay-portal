/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.service.persistence.impl;

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
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.wiki.exception.NoSuchPageResourceException;
import com.liferay.wiki.model.WikiPageResource;
import com.liferay.wiki.model.WikiPageResourceTable;
import com.liferay.wiki.model.impl.WikiPageResourceImpl;
import com.liferay.wiki.model.impl.WikiPageResourceModelImpl;
import com.liferay.wiki.service.persistence.WikiPageResourcePersistence;
import com.liferay.wiki.service.persistence.WikiPageResourceUtil;
import com.liferay.wiki.service.persistence.impl.constants.WikiPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
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
 * The persistence implementation for the wiki page resource service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = WikiPageResourcePersistence.class)
public class WikiPageResourcePersistenceImpl
	extends BasePersistenceImpl<WikiPageResource, NoSuchPageResourceException>
	implements WikiPageResourcePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>WikiPageResourceUtil</code> to access the wiki page resource persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		WikiPageResourceImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<WikiPageResource, NoSuchPageResourceException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the wiki page resources where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageResourceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of wiki page resources
	 * @param end the upper bound of the range of wiki page resources (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki page resources
	 */
	@Override
	public List<WikiPageResource> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<WikiPageResource> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first wiki page resource in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page resource
	 * @throws NoSuchPageResourceException if a matching wiki page resource could not be found
	 */
	@Override
	public WikiPageResource findByUuid_First(
			String uuid, OrderByComparator<WikiPageResource> orderByComparator)
		throws NoSuchPageResourceException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first wiki page resource in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page resource, or <code>null</code> if a matching wiki page resource could not be found
	 */
	@Override
	public WikiPageResource fetchByUuid_First(
		String uuid, OrderByComparator<WikiPageResource> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the wiki page resources where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of wiki page resources where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching wiki page resources
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<WikiPageResource, NoSuchPageResourceException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the wiki page resource where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPageResourceException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching wiki page resource
	 * @throws NoSuchPageResourceException if a matching wiki page resource could not be found
	 */
	@Override
	public WikiPageResource findByUUID_G(String uuid, long groupId)
		throws NoSuchPageResourceException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the wiki page resource where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching wiki page resource, or <code>null</code> if a matching wiki page resource could not be found
	 */
	@Override
	public WikiPageResource fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the wiki page resource where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the wiki page resource that was removed
	 */
	@Override
	public WikiPageResource removeByUUID_G(String uuid, long groupId)
		throws NoSuchPageResourceException {

		WikiPageResource wikiPageResource = findByUUID_G(uuid, groupId);

		return remove(wikiPageResource);
	}

	/**
	 * Returns the number of wiki page resources where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching wiki page resources
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<WikiPageResource, NoSuchPageResourceException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the wiki page resources where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageResourceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of wiki page resources
	 * @param end the upper bound of the range of wiki page resources (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki page resources
	 */
	@Override
	public List<WikiPageResource> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<WikiPageResource> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page resource in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page resource
	 * @throws NoSuchPageResourceException if a matching wiki page resource could not be found
	 */
	@Override
	public WikiPageResource findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<WikiPageResource> orderByComparator)
		throws NoSuchPageResourceException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first wiki page resource in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page resource, or <code>null</code> if a matching wiki page resource could not be found
	 */
	@Override
	public WikiPageResource fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<WikiPageResource> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the wiki page resources where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of wiki page resources where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching wiki page resources
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private UniquePersistenceFinder
		<WikiPageResource, NoSuchPageResourceException>
			_uniquePersistenceFinderByN_T;

	/**
	 * Returns the wiki page resource where nodeId = &#63; and title = &#63; or throws a <code>NoSuchPageResourceException</code> if it could not be found.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @return the matching wiki page resource
	 * @throws NoSuchPageResourceException if a matching wiki page resource could not be found
	 */
	@Override
	public WikiPageResource findByN_T(long nodeId, String title)
		throws NoSuchPageResourceException {

		return _uniquePersistenceFinderByN_T.find(
			finderCache, new Object[] {nodeId, title});
	}

	/**
	 * Returns the wiki page resource where nodeId = &#63; and title = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching wiki page resource, or <code>null</code> if a matching wiki page resource could not be found
	 */
	@Override
	public WikiPageResource fetchByN_T(
		long nodeId, String title, boolean useFinderCache) {

		return _uniquePersistenceFinderByN_T.fetch(
			finderCache, new Object[] {nodeId, title}, useFinderCache);
	}

	/**
	 * Removes the wiki page resource where nodeId = &#63; and title = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @return the wiki page resource that was removed
	 */
	@Override
	public WikiPageResource removeByN_T(long nodeId, String title)
		throws NoSuchPageResourceException {

		WikiPageResource wikiPageResource = findByN_T(nodeId, title);

		return remove(wikiPageResource);
	}

	/**
	 * Returns the number of wiki page resources where nodeId = &#63; and title = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @return the number of matching wiki page resources
	 */
	@Override
	public int countByN_T(long nodeId, String title) {
		return _uniquePersistenceFinderByN_T.count(
			finderCache, new Object[] {nodeId, title});
	}

	public WikiPageResourcePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(WikiPageResource.class);

		setModelImplClass(WikiPageResourceImpl.class);
		setModelPKClass(long.class);

		setTable(WikiPageResourceTable.INSTANCE);
	}

	/**
	 * Creates a new wiki page resource with the primary key. Does not add the wiki page resource to the database.
	 *
	 * @param resourcePrimKey the primary key for the new wiki page resource
	 * @return the new wiki page resource
	 */
	@Override
	public WikiPageResource create(long resourcePrimKey) {
		WikiPageResource wikiPageResource = new WikiPageResourceImpl();

		wikiPageResource.setNew(true);
		wikiPageResource.setPrimaryKey(resourcePrimKey);

		String uuid = PortalUUIDUtil.generate();

		wikiPageResource.setUuid(uuid);

		wikiPageResource.setCompanyId(CompanyThreadLocal.getCompanyId());

		return wikiPageResource;
	}

	/**
	 * Removes the wiki page resource with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param resourcePrimKey the primary key of the wiki page resource
	 * @return the wiki page resource that was removed
	 * @throws NoSuchPageResourceException if a wiki page resource with the primary key could not be found
	 */
	@Override
	public WikiPageResource remove(long resourcePrimKey)
		throws NoSuchPageResourceException {

		return remove((Serializable)resourcePrimKey);
	}

	@Override
	protected WikiPageResource removeImpl(WikiPageResource wikiPageResource) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(wikiPageResource)) {
				wikiPageResource = (WikiPageResource)session.get(
					WikiPageResourceImpl.class,
					wikiPageResource.getPrimaryKeyObj());
			}

			if ((wikiPageResource != null) &&
				ctPersistenceHelper.isRemove(wikiPageResource)) {

				session.delete(wikiPageResource);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (wikiPageResource != null) {
			clearCache(wikiPageResource);
		}

		return wikiPageResource;
	}

	@Override
	public WikiPageResource updateImpl(WikiPageResource wikiPageResource) {
		boolean isNew = wikiPageResource.isNew();

		if (!(wikiPageResource instanceof WikiPageResourceModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(wikiPageResource.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					wikiPageResource);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in wikiPageResource proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom WikiPageResource implementation " +
					wikiPageResource.getClass());
		}

		WikiPageResourceModelImpl wikiPageResourceModelImpl =
			(WikiPageResourceModelImpl)wikiPageResource;

		if (Validator.isNull(wikiPageResource.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			wikiPageResource.setUuid(uuid);
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(wikiPageResource)) {
				if (!isNew) {
					session.evict(
						WikiPageResourceImpl.class,
						wikiPageResource.getPrimaryKeyObj());
				}

				session.save(wikiPageResource);
			}
			else {
				wikiPageResource = (WikiPageResource)session.merge(
					wikiPageResource);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(wikiPageResource, false);

		if (isNew) {
			wikiPageResource.setNew(false);
		}

		wikiPageResource.resetOriginalValues();

		return wikiPageResource;
	}

	/**
	 * Returns the wiki page resource with the primary key or throws a <code>NoSuchPageResourceException</code> if it could not be found.
	 *
	 * @param resourcePrimKey the primary key of the wiki page resource
	 * @return the wiki page resource
	 * @throws NoSuchPageResourceException if a wiki page resource with the primary key could not be found
	 */
	@Override
	public WikiPageResource findByPrimaryKey(long resourcePrimKey)
		throws NoSuchPageResourceException {

		return findByPrimaryKey((Serializable)resourcePrimKey);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the wiki page resource with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param resourcePrimKey the primary key of the wiki page resource
	 * @return the wiki page resource, or <code>null</code> if a wiki page resource with the primary key could not be found
	 */
	@Override
	public WikiPageResource fetchByPrimaryKey(long resourcePrimKey) {
		return fetchByPrimaryKey((Serializable)resourcePrimKey);
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
		return "resourcePrimKey";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_WIKIPAGERESOURCE;
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
		return WikiPageResourceModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "WikiPageResource";
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
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctMergeColumnNames.add("nodeId");
		ctMergeColumnNames.add("title");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("resourcePrimKey"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"nodeId", "title"});
	}

	/**
	 * Initializes the wiki page resource persistence.
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
			_SQL_SELECT_WIKIPAGERESOURCE_WHERE,
			_SQL_COUNT_WIKIPAGERESOURCE_WHERE,
			WikiPageResourceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"wikiPageResource.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, WikiPageResource::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(WikiPageResource::getUuid),
				WikiPageResource::getGroupId),
			_SQL_SELECT_WIKIPAGERESOURCE_WHERE, "",
			new FinderColumn<>(
				"wikiPageResource.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, WikiPageResource::getUuid),
			new FinderColumn<>(
				"wikiPageResource.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, WikiPageResource::getGroupId));

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
				_SQL_SELECT_WIKIPAGERESOURCE_WHERE,
				_SQL_COUNT_WIKIPAGERESOURCE_WHERE,
				WikiPageResourceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"wikiPageResource.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					WikiPageResource::getUuid),
				new FinderColumn<>(
					"wikiPageResource.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, WikiPageResource::getCompanyId));

		_uniquePersistenceFinderByN_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByN_T",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"nodeId", "title"}, 0, 2, false,
				WikiPageResource::getNodeId,
				convertNullFunction(WikiPageResource::getTitle)),
			_SQL_SELECT_WIKIPAGERESOURCE_WHERE, "",
			new FinderColumn<>(
				"wikiPageResource.", "nodeId", FinderColumn.Type.LONG, "=",
				true, true, WikiPageResource::getNodeId),
			new FinderColumn<>(
				"wikiPageResource.", "title", FinderColumn.Type.STRING, "=",
				true, true, WikiPageResource::getTitle));

		WikiPageResourceUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		WikiPageResourceUtil.setPersistence(null);

		entityCache.removeCache(WikiPageResourceImpl.class.getName());
	}

	@Override
	@Reference(
		target = WikiPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = WikiPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = WikiPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		WikiPageResourceModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_WIKIPAGERESOURCE =
		"SELECT wikiPageResource FROM WikiPageResource wikiPageResource";

	private static final String _SQL_SELECT_WIKIPAGERESOURCE_WHERE =
		"SELECT wikiPageResource FROM WikiPageResource wikiPageResource WHERE ";

	private static final String _SQL_COUNT_WIKIPAGERESOURCE_WHERE =
		"SELECT COUNT(wikiPageResource) FROM WikiPageResource wikiPageResource WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No WikiPageResource exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		WikiPageResourcePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1894906021