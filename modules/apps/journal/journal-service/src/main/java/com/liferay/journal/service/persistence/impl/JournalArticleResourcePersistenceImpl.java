/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.persistence.impl;

import com.liferay.journal.exception.NoSuchArticleResourceException;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.model.JournalArticleResourceTable;
import com.liferay.journal.model.impl.JournalArticleResourceImpl;
import com.liferay.journal.model.impl.JournalArticleResourceModelImpl;
import com.liferay.journal.service.persistence.JournalArticleResourcePersistence;
import com.liferay.journal.service.persistence.JournalArticleResourceUtil;
import com.liferay.journal.service.persistence.impl.constants.JournalPersistenceConstants;
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
 * The persistence implementation for the journal article resource service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = JournalArticleResourcePersistence.class)
public class JournalArticleResourcePersistenceImpl
	extends BasePersistenceImpl
		<JournalArticleResource, NoSuchArticleResourceException>
	implements JournalArticleResourcePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>JournalArticleResourceUtil</code> to access the journal article resource persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		JournalArticleResourceImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<JournalArticleResource, NoSuchArticleResourceException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the journal article resources where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleResourceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of journal article resources
	 * @param end the upper bound of the range of journal article resources (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal article resources
	 */
	@Override
	public List<JournalArticleResource> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<JournalArticleResource> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first journal article resource in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article resource
	 * @throws NoSuchArticleResourceException if a matching journal article resource could not be found
	 */
	@Override
	public JournalArticleResource findByUuid_First(
			String uuid,
			OrderByComparator<JournalArticleResource> orderByComparator)
		throws NoSuchArticleResourceException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first journal article resource in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article resource, or <code>null</code> if a matching journal article resource could not be found
	 */
	@Override
	public JournalArticleResource fetchByUuid_First(
		String uuid,
		OrderByComparator<JournalArticleResource> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the journal article resources where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of journal article resources where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching journal article resources
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<JournalArticleResource, NoSuchArticleResourceException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the journal article resource where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchArticleResourceException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching journal article resource
	 * @throws NoSuchArticleResourceException if a matching journal article resource could not be found
	 */
	@Override
	public JournalArticleResource findByUUID_G(String uuid, long groupId)
		throws NoSuchArticleResourceException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the journal article resource where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching journal article resource, or <code>null</code> if a matching journal article resource could not be found
	 */
	@Override
	public JournalArticleResource fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the journal article resource where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the journal article resource that was removed
	 */
	@Override
	public JournalArticleResource removeByUUID_G(String uuid, long groupId)
		throws NoSuchArticleResourceException {

		JournalArticleResource journalArticleResource = findByUUID_G(
			uuid, groupId);

		return remove(journalArticleResource);
	}

	/**
	 * Returns the number of journal article resources where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching journal article resources
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<JournalArticleResource, NoSuchArticleResourceException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the journal article resources where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleResourceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of journal article resources
	 * @param end the upper bound of the range of journal article resources (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal article resources
	 */
	@Override
	public List<JournalArticleResource> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<JournalArticleResource> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article resource in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article resource
	 * @throws NoSuchArticleResourceException if a matching journal article resource could not be found
	 */
	@Override
	public JournalArticleResource findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<JournalArticleResource> orderByComparator)
		throws NoSuchArticleResourceException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first journal article resource in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article resource, or <code>null</code> if a matching journal article resource could not be found
	 */
	@Override
	public JournalArticleResource fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<JournalArticleResource> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the journal article resources where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of journal article resources where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching journal article resources
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<JournalArticleResource, NoSuchArticleResourceException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the journal article resources where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleResourceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of journal article resources
	 * @param end the upper bound of the range of journal article resources (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal article resources
	 */
	@Override
	public List<JournalArticleResource> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<JournalArticleResource> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first journal article resource in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article resource
	 * @throws NoSuchArticleResourceException if a matching journal article resource could not be found
	 */
	@Override
	public JournalArticleResource findByGroupId_First(
			long groupId,
			OrderByComparator<JournalArticleResource> orderByComparator)
		throws NoSuchArticleResourceException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first journal article resource in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article resource, or <code>null</code> if a matching journal article resource could not be found
	 */
	@Override
	public JournalArticleResource fetchByGroupId_First(
		long groupId,
		OrderByComparator<JournalArticleResource> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the journal article resources where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of journal article resources where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching journal article resources
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private UniquePersistenceFinder
		<JournalArticleResource, NoSuchArticleResourceException>
			_uniquePersistenceFinderByG_A;

	/**
	 * Returns the journal article resource where groupId = &#63; and articleId = &#63; or throws a <code>NoSuchArticleResourceException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @return the matching journal article resource
	 * @throws NoSuchArticleResourceException if a matching journal article resource could not be found
	 */
	@Override
	public JournalArticleResource findByG_A(long groupId, String articleId)
		throws NoSuchArticleResourceException {

		return _uniquePersistenceFinderByG_A.find(
			finderCache, new Object[] {groupId, articleId});
	}

	/**
	 * Returns the journal article resource where groupId = &#63; and articleId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching journal article resource, or <code>null</code> if a matching journal article resource could not be found
	 */
	@Override
	public JournalArticleResource fetchByG_A(
		long groupId, String articleId, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_A.fetch(
			finderCache, new Object[] {groupId, articleId}, useFinderCache);
	}

	/**
	 * Removes the journal article resource where groupId = &#63; and articleId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @return the journal article resource that was removed
	 */
	@Override
	public JournalArticleResource removeByG_A(long groupId, String articleId)
		throws NoSuchArticleResourceException {

		JournalArticleResource journalArticleResource = findByG_A(
			groupId, articleId);

		return remove(journalArticleResource);
	}

	/**
	 * Returns the number of journal article resources where groupId = &#63; and articleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @return the number of matching journal article resources
	 */
	@Override
	public int countByG_A(long groupId, String articleId) {
		return _uniquePersistenceFinderByG_A.count(
			finderCache, new Object[] {groupId, articleId});
	}

	public JournalArticleResourcePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(JournalArticleResource.class);

		setModelImplClass(JournalArticleResourceImpl.class);
		setModelPKClass(long.class);

		setTable(JournalArticleResourceTable.INSTANCE);
	}

	/**
	 * Creates a new journal article resource with the primary key. Does not add the journal article resource to the database.
	 *
	 * @param resourcePrimKey the primary key for the new journal article resource
	 * @return the new journal article resource
	 */
	@Override
	public JournalArticleResource create(long resourcePrimKey) {
		JournalArticleResource journalArticleResource =
			new JournalArticleResourceImpl();

		journalArticleResource.setNew(true);
		journalArticleResource.setPrimaryKey(resourcePrimKey);

		String uuid = PortalUUIDUtil.generate();

		journalArticleResource.setUuid(uuid);

		journalArticleResource.setCompanyId(CompanyThreadLocal.getCompanyId());

		return journalArticleResource;
	}

	/**
	 * Removes the journal article resource with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param resourcePrimKey the primary key of the journal article resource
	 * @return the journal article resource that was removed
	 * @throws NoSuchArticleResourceException if a journal article resource with the primary key could not be found
	 */
	@Override
	public JournalArticleResource remove(long resourcePrimKey)
		throws NoSuchArticleResourceException {

		return remove((Serializable)resourcePrimKey);
	}

	@Override
	protected JournalArticleResource removeImpl(
		JournalArticleResource journalArticleResource) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(journalArticleResource)) {
				journalArticleResource = (JournalArticleResource)session.get(
					JournalArticleResourceImpl.class,
					journalArticleResource.getPrimaryKeyObj());
			}

			if ((journalArticleResource != null) &&
				ctPersistenceHelper.isRemove(journalArticleResource)) {

				session.delete(journalArticleResource);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (journalArticleResource != null) {
			clearCache(journalArticleResource);
		}

		return journalArticleResource;
	}

	@Override
	public JournalArticleResource updateImpl(
		JournalArticleResource journalArticleResource) {

		boolean isNew = journalArticleResource.isNew();

		if (!(journalArticleResource instanceof
				JournalArticleResourceModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(journalArticleResource.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					journalArticleResource);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in journalArticleResource proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom JournalArticleResource implementation " +
					journalArticleResource.getClass());
		}

		JournalArticleResourceModelImpl journalArticleResourceModelImpl =
			(JournalArticleResourceModelImpl)journalArticleResource;

		if (Validator.isNull(journalArticleResource.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			journalArticleResource.setUuid(uuid);
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(journalArticleResource)) {
				if (!isNew) {
					session.evict(
						JournalArticleResourceImpl.class,
						journalArticleResource.getPrimaryKeyObj());
				}

				session.save(journalArticleResource);
			}
			else {
				journalArticleResource = (JournalArticleResource)session.merge(
					journalArticleResource);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(journalArticleResource, false);

		if (isNew) {
			journalArticleResource.setNew(false);
		}

		journalArticleResource.resetOriginalValues();

		return journalArticleResource;
	}

	/**
	 * Returns the journal article resource with the primary key or throws a <code>NoSuchArticleResourceException</code> if it could not be found.
	 *
	 * @param resourcePrimKey the primary key of the journal article resource
	 * @return the journal article resource
	 * @throws NoSuchArticleResourceException if a journal article resource with the primary key could not be found
	 */
	@Override
	public JournalArticleResource findByPrimaryKey(long resourcePrimKey)
		throws NoSuchArticleResourceException {

		return findByPrimaryKey((Serializable)resourcePrimKey);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the journal article resource with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param resourcePrimKey the primary key of the journal article resource
	 * @return the journal article resource, or <code>null</code> if a journal article resource with the primary key could not be found
	 */
	@Override
	public JournalArticleResource fetchByPrimaryKey(long resourcePrimKey) {
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
		return _SQL_SELECT_JOURNALARTICLERESOURCE;
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
		return JournalArticleResourceModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "JournalArticleResource";
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
		ctMergeColumnNames.add("articleId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("resourcePrimKey"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"groupId", "articleId"});
	}

	/**
	 * Initializes the journal article resource persistence.
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
			_SQL_SELECT_JOURNALARTICLERESOURCE_WHERE,
			_SQL_COUNT_JOURNALARTICLERESOURCE_WHERE,
			JournalArticleResourceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"journalArticleResource.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				JournalArticleResource::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(JournalArticleResource::getUuid),
				JournalArticleResource::getGroupId),
			_SQL_SELECT_JOURNALARTICLERESOURCE_WHERE, "",
			new FinderColumn<>(
				"journalArticleResource.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				JournalArticleResource::getUuid),
			new FinderColumn<>(
				"journalArticleResource.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, JournalArticleResource::getGroupId));

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
				_SQL_SELECT_JOURNALARTICLERESOURCE_WHERE,
				_SQL_COUNT_JOURNALARTICLERESOURCE_WHERE,
				JournalArticleResourceModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"journalArticleResource.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					JournalArticleResource::getUuid),
				new FinderColumn<>(
					"journalArticleResource.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					JournalArticleResource::getCompanyId));

		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_JOURNALARTICLERESOURCE_WHERE,
				_SQL_COUNT_JOURNALARTICLERESOURCE_WHERE,
				JournalArticleResourceModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"journalArticleResource.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					JournalArticleResource::getGroupId));

		_uniquePersistenceFinderByG_A = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_A",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "articleId"}, 0, 2, false,
				JournalArticleResource::getGroupId,
				convertNullFunction(JournalArticleResource::getArticleId)),
			_SQL_SELECT_JOURNALARTICLERESOURCE_WHERE, "",
			new FinderColumn<>(
				"journalArticleResource.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, JournalArticleResource::getGroupId),
			new FinderColumn<>(
				"journalArticleResource.", "articleId",
				FinderColumn.Type.STRING, "=", true, true,
				JournalArticleResource::getArticleId));

		JournalArticleResourceUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		JournalArticleResourceUtil.setPersistence(null);

		entityCache.removeCache(JournalArticleResourceImpl.class.getName());
	}

	@Override
	@Reference(
		target = JournalPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = JournalPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = JournalPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		JournalArticleResourceModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_JOURNALARTICLERESOURCE =
		"SELECT journalArticleResource FROM JournalArticleResource journalArticleResource";

	private static final String _SQL_SELECT_JOURNALARTICLERESOURCE_WHERE =
		"SELECT journalArticleResource FROM JournalArticleResource journalArticleResource WHERE ";

	private static final String _SQL_COUNT_JOURNALARTICLERESOURCE_WHERE =
		"SELECT COUNT(journalArticleResource) FROM JournalArticleResource journalArticleResource WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No JournalArticleResource exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleResourcePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1371555885