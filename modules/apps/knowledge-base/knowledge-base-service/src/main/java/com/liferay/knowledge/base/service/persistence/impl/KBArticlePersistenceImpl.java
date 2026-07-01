/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.persistence.impl;

import com.liferay.knowledge.base.exception.NoSuchArticleException;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBArticleTable;
import com.liferay.knowledge.base.model.impl.KBArticleImpl;
import com.liferay.knowledge.base.model.impl.KBArticleModelImpl;
import com.liferay.knowledge.base.service.persistence.KBArticlePersistence;
import com.liferay.knowledge.base.service.persistence.KBArticleUtil;
import com.liferay.knowledge.base.service.persistence.impl.constants.KBPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
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
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the kb article service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KBArticlePersistence.class)
public class KBArticlePersistenceImpl
	extends BasePersistenceImpl<KBArticle, NoSuchArticleException>
	implements KBArticlePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KBArticleUtil</code> to access the kb article persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KBArticleImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByResourcePrimKey;

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByResourcePrimKey(
		long resourcePrimKey, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByResourcePrimKey.find(
			finderCache, new Object[] {resourcePrimKey}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByResourcePrimKey_First(
			long resourcePrimKey,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByResourcePrimKey.findFirst(
			finderCache, new Object[] {resourcePrimKey}, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByResourcePrimKey_First(
		long resourcePrimKey, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByResourcePrimKey.fetchFirst(
			finderCache, new Object[] {resourcePrimKey}, orderByComparator);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 */
	@Override
	public void removeByResourcePrimKey(long resourcePrimKey) {
		_collectionPersistenceFinderByResourcePrimKey.remove(
			finderCache, new Object[] {resourcePrimKey});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByResourcePrimKey(long resourcePrimKey) {
		return _collectionPersistenceFinderByResourcePrimKey.count(
			finderCache, new Object[] {resourcePrimKey});
	}

	private CollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the kb articles where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByUuid_First(
			String uuid, OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByUuid_First(
		String uuid, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the kb articles where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of kb articles where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<KBArticle, NoSuchArticleException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the kb article where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByUUID_G(String uuid, long groupId)
		throws NoSuchArticleException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the kb article where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the kb article where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the kb article that was removed
	 */
	@Override
	public KBArticle removeByUUID_G(String uuid, long groupId)
		throws NoSuchArticleException {

		KBArticle kbArticle = findByUUID_G(uuid, groupId);

		return remove(kbArticle);
	}

	/**
	 * Returns the number of kb articles where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the kb articles where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the kb articles where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of kb articles where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByR_G;

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G(
		long resourcePrimKey, long groupId, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_G.find(
			finderCache, new Object[] {resourcePrimKey, groupId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByR_G_First(
			long resourcePrimKey, long groupId,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByR_G.findFirst(
			finderCache, new Object[] {resourcePrimKey, groupId},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByR_G_First(
		long resourcePrimKey, long groupId,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_G.fetchFirst(
			finderCache, new Object[] {resourcePrimKey, groupId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G(
		long resourcePrimKey, long groupId, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_G.filterFind(
			finderCache, new Object[] {resourcePrimKey, groupId}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 */
	@Override
	public void removeByR_G(long resourcePrimKey, long groupId) {
		_collectionPersistenceFinderByR_G.remove(
			finderCache, new Object[] {resourcePrimKey, groupId});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_G(long resourcePrimKey, long groupId) {
		return _collectionPersistenceFinderByR_G.count(
			finderCache, new Object[] {resourcePrimKey, groupId});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByR_G(long resourcePrimKey, long groupId) {
		return _collectionPersistenceFinderByR_G.filterCount(
			finderCache, new Object[] {resourcePrimKey, groupId}, groupId);
	}

	private UniquePersistenceFinder<KBArticle, NoSuchArticleException>
		_uniquePersistenceFinderByR_V;

	/**
	 * Returns the kb article where resourcePrimKey = &#63; and version = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param version the version
	 * @return the matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByR_V(long resourcePrimKey, int version)
		throws NoSuchArticleException {

		return _uniquePersistenceFinderByR_V.find(
			finderCache, new Object[] {resourcePrimKey, version});
	}

	/**
	 * Returns the kb article where resourcePrimKey = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByR_V(
		long resourcePrimKey, int version, boolean useFinderCache) {

		return _uniquePersistenceFinderByR_V.fetch(
			finderCache, new Object[] {resourcePrimKey, version},
			useFinderCache);
	}

	/**
	 * Removes the kb article where resourcePrimKey = &#63; and version = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param version the version
	 * @return the kb article that was removed
	 */
	@Override
	public KBArticle removeByR_V(long resourcePrimKey, int version)
		throws NoSuchArticleException {

		KBArticle kbArticle = findByR_V(resourcePrimKey, version);

		return remove(kbArticle);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and version = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param version the version
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_V(long resourcePrimKey, int version) {
		return _uniquePersistenceFinderByR_V.count(
			finderCache, new Object[] {resourcePrimKey, version});
	}

	private CollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByR_L;

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_L(
		long resourcePrimKey, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_L.find(
			finderCache, new Object[] {new long[] {resourcePrimKey}, latest},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByR_L_First(
			long resourcePrimKey, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByR_L.findFirst(
			finderCache, new Object[] {new long[] {resourcePrimKey}, latest},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByR_L_First(
		long resourcePrimKey, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_L.fetchFirst(
			finderCache, new Object[] {new long[] {resourcePrimKey}, latest},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and latest = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_L(
		long[] resourcePrimKeys, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_L.find(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(resourcePrimKeys), latest},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and latest = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 */
	@Override
	public void removeByR_L(long resourcePrimKey, boolean latest) {
		_collectionPersistenceFinderByR_L.remove(
			finderCache, new Object[] {new long[] {resourcePrimKey}, latest});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_L(long resourcePrimKey, boolean latest) {
		return _collectionPersistenceFinderByR_L.count(
			finderCache, new Object[] {new long[] {resourcePrimKey}, latest});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_L(long[] resourcePrimKeys, boolean latest) {
		return _collectionPersistenceFinderByR_L.count(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(resourcePrimKeys), latest});
	}

	private CollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByR_M;

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_M(
		long resourcePrimKey, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_M.find(
			finderCache, new Object[] {new long[] {resourcePrimKey}, main},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByR_M_First(
			long resourcePrimKey, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByR_M.findFirst(
			finderCache, new Object[] {new long[] {resourcePrimKey}, main},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByR_M_First(
		long resourcePrimKey, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_M.fetchFirst(
			finderCache, new Object[] {new long[] {resourcePrimKey}, main},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and main = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_M(
		long[] resourcePrimKeys, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_M.find(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(resourcePrimKeys), main},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and main = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 */
	@Override
	public void removeByR_M(long resourcePrimKey, boolean main) {
		_collectionPersistenceFinderByR_M.remove(
			finderCache, new Object[] {new long[] {resourcePrimKey}, main});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_M(long resourcePrimKey, boolean main) {
		return _collectionPersistenceFinderByR_M.count(
			finderCache, new Object[] {new long[] {resourcePrimKey}, main});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and main = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_M(long[] resourcePrimKeys, boolean main) {
		return _collectionPersistenceFinderByR_M.count(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(resourcePrimKeys), main});
	}

	private CollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByR_S;

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_S(
		long resourcePrimKey, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_S.find(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, new int[] {status}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByR_S_First(
			long resourcePrimKey, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByR_S.findFirst(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, new int[] {status}},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByR_S_First(
		long resourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_S.fetchFirst(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, new int[] {status}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_S(
		long[] resourcePrimKeys, int[] statuses, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_S.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys),
				ArrayUtil.sortedUnique(statuses)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and status = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 */
	@Override
	public void removeByR_S(long resourcePrimKey, int status) {
		_collectionPersistenceFinderByR_S.remove(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, new int[] {status}});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_S(long resourcePrimKey, int status) {
		return _collectionPersistenceFinderByR_S.count(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, new int[] {status}});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and status = any &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param statuses the statuses
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_S(long[] resourcePrimKeys, int[] statuses) {
		return _collectionPersistenceFinderByR_S.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys),
				ArrayUtil.sortedUnique(statuses)
			});
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_ERC;

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_ERC(
		long groupId, String externalReferenceCode, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_ERC.find(
			finderCache, new Object[] {groupId, externalReferenceCode}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_ERC_First(
			long groupId, String externalReferenceCode,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_ERC.findFirst(
			finderCache, new Object[] {groupId, externalReferenceCode},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_ERC_First(
		long groupId, String externalReferenceCode,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_ERC.fetchFirst(
			finderCache, new Object[] {groupId, externalReferenceCode},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_ERC(
		long groupId, String externalReferenceCode, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_ERC.filterFind(
			finderCache, new Object[] {groupId, externalReferenceCode}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and externalReferenceCode = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 */
	@Override
	public void removeByG_ERC(long groupId, String externalReferenceCode) {
		_collectionPersistenceFinderByG_ERC.remove(
			finderCache, new Object[] {groupId, externalReferenceCode});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_ERC(long groupId, String externalReferenceCode) {
		return _collectionPersistenceFinderByG_ERC.count(
			finderCache, new Object[] {groupId, externalReferenceCode});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_ERC(long groupId, String externalReferenceCode) {
		return _collectionPersistenceFinderByG_ERC.filterCount(
			finderCache, new Object[] {groupId, externalReferenceCode},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_L;

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_L(
		long groupId, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_L.find(
			finderCache, new Object[] {groupId, latest}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_L_First(
			long groupId, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_L.findFirst(
			finderCache, new Object[] {groupId, latest}, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_L_First(
		long groupId, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_L.fetchFirst(
			finderCache, new Object[] {groupId, latest}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_L(
		long groupId, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_L.filterFind(
			finderCache, new Object[] {groupId, latest}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and latest = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 */
	@Override
	public void removeByG_L(long groupId, boolean latest) {
		_collectionPersistenceFinderByG_L.remove(
			finderCache, new Object[] {groupId, latest});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_L(long groupId, boolean latest) {
		return _collectionPersistenceFinderByG_L.count(
			finderCache, new Object[] {groupId, latest});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_L(long groupId, boolean latest) {
		return _collectionPersistenceFinderByG_L.filterCount(
			finderCache, new Object[] {groupId, latest}, groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_M;

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_M(
		long groupId, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_M.find(
			finderCache, new Object[] {groupId, main}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_M_First(
			long groupId, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_M.findFirst(
			finderCache, new Object[] {groupId, main}, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_M_First(
		long groupId, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_M.fetchFirst(
			finderCache, new Object[] {groupId, main}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_M(
		long groupId, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_M.filterFind(
			finderCache, new Object[] {groupId, main}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and main = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 */
	@Override
	public void removeByG_M(long groupId, boolean main) {
		_collectionPersistenceFinderByG_M.remove(
			finderCache, new Object[] {groupId, main});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_M(long groupId, boolean main) {
		return _collectionPersistenceFinderByG_M.count(
			finderCache, new Object[] {groupId, main});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_M(long groupId, boolean main) {
		return _collectionPersistenceFinderByG_M.filterCount(
			finderCache, new Object[] {groupId, main}, groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_S;

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S.find(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_S_First(
			long groupId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_S.findFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_S.fetchFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_S.filterFind(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_S(long groupId, int status) {
		_collectionPersistenceFinderByG_S.remove(
			finderCache, new Object[] {groupId, status});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_S(long groupId, int status) {
		return _collectionPersistenceFinderByG_S.count(
			finderCache, new Object[] {groupId, status});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_S(long groupId, int status) {
		return _collectionPersistenceFinderByG_S.filterCount(
			finderCache, new Object[] {groupId, status}, groupId);
	}

	private CollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByC_L;

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByC_L(
		long companyId, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_L.find(
			finderCache, new Object[] {companyId, latest}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and latest = &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByC_L_First(
			long companyId, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByC_L.findFirst(
			finderCache, new Object[] {companyId, latest}, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and latest = &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByC_L_First(
		long companyId, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByC_L.fetchFirst(
			finderCache, new Object[] {companyId, latest}, orderByComparator);
	}

	/**
	 * Removes all the kb articles where companyId = &#63; and latest = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 */
	@Override
	public void removeByC_L(long companyId, boolean latest) {
		_collectionPersistenceFinderByC_L.remove(
			finderCache, new Object[] {companyId, latest});
	}

	/**
	 * Returns the number of kb articles where companyId = &#63; and latest = &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByC_L(long companyId, boolean latest) {
		return _collectionPersistenceFinderByC_L.count(
			finderCache, new Object[] {companyId, latest});
	}

	private CollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByC_M;

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByC_M(
		long companyId, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_M.find(
			finderCache, new Object[] {companyId, main}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and main = &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByC_M_First(
			long companyId, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByC_M.findFirst(
			finderCache, new Object[] {companyId, main}, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and main = &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByC_M_First(
		long companyId, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByC_M.fetchFirst(
			finderCache, new Object[] {companyId, main}, orderByComparator);
	}

	/**
	 * Removes all the kb articles where companyId = &#63; and main = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 */
	@Override
	public void removeByC_M(long companyId, boolean main) {
		_collectionPersistenceFinderByC_M.remove(
			finderCache, new Object[] {companyId, main});
	}

	/**
	 * Returns the number of kb articles where companyId = &#63; and main = &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByC_M(long companyId, boolean main) {
		return _collectionPersistenceFinderByC_M.count(
			finderCache, new Object[] {companyId, main});
	}

	private CollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByC_S;

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S.find(
			finderCache, new Object[] {companyId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByC_S_First(
			long companyId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByC_S.findFirst(
			finderCache, new Object[] {companyId, status}, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByC_S_First(
		long companyId, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByC_S.fetchFirst(
			finderCache, new Object[] {companyId, status}, orderByComparator);
	}

	/**
	 * Removes all the kb articles where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_S(long companyId, int status) {
		_collectionPersistenceFinderByC_S.remove(
			finderCache, new Object[] {companyId, status});
	}

	/**
	 * Returns the number of kb articles where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByC_S(long companyId, int status) {
		return _collectionPersistenceFinderByC_S.count(
			finderCache, new Object[] {companyId, status});
	}

	private CollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByP_L;

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_L(
		long parentResourcePrimKey, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_L.find(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, latest}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByP_L_First(
			long parentResourcePrimKey, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByP_L.findFirst(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, latest},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByP_L_First(
		long parentResourcePrimKey, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByP_L.fetchFirst(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, latest},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_L(
		long[] parentResourcePrimKeys, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_L.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(parentResourcePrimKeys), latest
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; from the database.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 */
	@Override
	public void removeByP_L(long parentResourcePrimKey, boolean latest) {
		_collectionPersistenceFinderByP_L.remove(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, latest});
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByP_L(long parentResourcePrimKey, boolean latest) {
		return _collectionPersistenceFinderByP_L.count(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, latest});
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByP_L(long[] parentResourcePrimKeys, boolean latest) {
		return _collectionPersistenceFinderByP_L.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(parentResourcePrimKeys), latest
			});
	}

	private CollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByP_M;

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_M(
		long parentResourcePrimKey, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_M.find(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, main}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByP_M_First(
			long parentResourcePrimKey, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByP_M.findFirst(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, main},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByP_M_First(
		long parentResourcePrimKey, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByP_M.fetchFirst(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, main},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_M(
		long[] parentResourcePrimKeys, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_M.find(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(parentResourcePrimKeys), main},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where parentResourcePrimKey = &#63; and main = &#63; from the database.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 */
	@Override
	public void removeByP_M(long parentResourcePrimKey, boolean main) {
		_collectionPersistenceFinderByP_M.remove(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, main});
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByP_M(long parentResourcePrimKey, boolean main) {
		return _collectionPersistenceFinderByP_M.count(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, main});
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByP_M(long[] parentResourcePrimKeys, boolean main) {
		return _collectionPersistenceFinderByP_M.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(parentResourcePrimKeys), main
			});
	}

	private CollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByP_S;

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_S(
		long parentResourcePrimKey, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_S.find(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByP_S_First(
			long parentResourcePrimKey, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByP_S.findFirst(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByP_S_First(
		long parentResourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByP_S.fetchFirst(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_S(
		long[] parentResourcePrimKeys, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_S.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(parentResourcePrimKeys), status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where parentResourcePrimKey = &#63; and status = &#63; from the database.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 */
	@Override
	public void removeByP_S(long parentResourcePrimKey, int status) {
		_collectionPersistenceFinderByP_S.remove(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, status});
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByP_S(long parentResourcePrimKey, int status) {
		return _collectionPersistenceFinderByP_S.count(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, status});
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByP_S(long[] parentResourcePrimKeys, int status) {
		return _collectionPersistenceFinderByP_S.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(parentResourcePrimKeys), status
			});
	}

	private CollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByLtD_S;

	/**
	 * Returns all the kb articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByLtD_S(Date displayDate, int status) {
		return findByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return findByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return findByLtD_S(
			displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtD_S.find(
			finderCache, new Object[] {displayDate, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByLtD_S.findFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByLtD_S.fetchFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Removes all the kb articles where displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByLtD_S(Date displayDate, int status) {
		_collectionPersistenceFinderByLtD_S.remove(
			finderCache, new Object[] {displayDate, status});
	}

	/**
	 * Returns the number of kb articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByLtD_S(Date displayDate, int status) {
		return _collectionPersistenceFinderByLtD_S.count(
			finderCache, new Object[] {displayDate, status});
	}

	private UniquePersistenceFinder<KBArticle, NoSuchArticleException>
		_uniquePersistenceFinderByR_G_V;

	/**
	 * Returns the kb article where resourcePrimKey = &#63; and groupId = &#63; and version = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param version the version
	 * @return the matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByR_G_V(
			long resourcePrimKey, long groupId, int version)
		throws NoSuchArticleException {

		return _uniquePersistenceFinderByR_G_V.find(
			finderCache, new Object[] {resourcePrimKey, groupId, version});
	}

	/**
	 * Returns the kb article where resourcePrimKey = &#63; and groupId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByR_G_V(
		long resourcePrimKey, long groupId, int version,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByR_G_V.fetch(
			finderCache, new Object[] {resourcePrimKey, groupId, version},
			useFinderCache);
	}

	/**
	 * Removes the kb article where resourcePrimKey = &#63; and groupId = &#63; and version = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param version the version
	 * @return the kb article that was removed
	 */
	@Override
	public KBArticle removeByR_G_V(
			long resourcePrimKey, long groupId, int version)
		throws NoSuchArticleException {

		KBArticle kbArticle = findByR_G_V(resourcePrimKey, groupId, version);

		return remove(kbArticle);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and version = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param version the version
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_G_V(long resourcePrimKey, long groupId, int version) {
		return _uniquePersistenceFinderByR_G_V.count(
			finderCache, new Object[] {resourcePrimKey, groupId, version});
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByR_G_L;

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_L(
		long resourcePrimKey, long groupId, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_G_L.find(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, latest}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByR_G_L_First(
			long resourcePrimKey, long groupId, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByR_G_L.findFirst(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, latest},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByR_G_L_First(
		long resourcePrimKey, long groupId, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_G_L.fetchFirst(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, latest},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_L(
		long resourcePrimKey, long groupId, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_G_L.filterFind(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, latest}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_G_L.filterFind(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, latest
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_G_L.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, latest
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 */
	@Override
	public void removeByR_G_L(
		long resourcePrimKey, long groupId, boolean latest) {

		_collectionPersistenceFinderByR_G_L.remove(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, latest});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_G_L(
		long resourcePrimKey, long groupId, boolean latest) {

		return _collectionPersistenceFinderByR_G_L.count(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, latest});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest) {

		return _collectionPersistenceFinderByR_G_L.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, latest
			});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByR_G_L(
		long resourcePrimKey, long groupId, boolean latest) {

		return _collectionPersistenceFinderByR_G_L.filterCount(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, latest},
			groupId);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest) {

		return _collectionPersistenceFinderByR_G_L.filterCount(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, latest
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByR_G_M;

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_M(
		long resourcePrimKey, long groupId, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_G_M.find(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, main}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByR_G_M_First(
			long resourcePrimKey, long groupId, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByR_G_M.findFirst(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, main},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByR_G_M_First(
		long resourcePrimKey, long groupId, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_G_M.fetchFirst(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, main},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_M(
		long resourcePrimKey, long groupId, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_G_M.filterFind(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, main}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_G_M.filterFind(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, main
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_G_M.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, main
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 */
	@Override
	public void removeByR_G_M(
		long resourcePrimKey, long groupId, boolean main) {

		_collectionPersistenceFinderByR_G_M.remove(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, main});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_G_M(long resourcePrimKey, long groupId, boolean main) {
		return _collectionPersistenceFinderByR_G_M.count(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, main});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main) {

		return _collectionPersistenceFinderByR_G_M.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, main
			});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByR_G_M(
		long resourcePrimKey, long groupId, boolean main) {

		return _collectionPersistenceFinderByR_G_M.filterCount(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, main},
			groupId);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main) {

		return _collectionPersistenceFinderByR_G_M.filterCount(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, main
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByR_G_S;

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_S(
		long resourcePrimKey, long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_G_S.find(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByR_G_S_First(
			long resourcePrimKey, long groupId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByR_G_S.findFirst(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByR_G_S_First(
		long resourcePrimKey, long groupId, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_G_S.fetchFirst(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_S(
		long resourcePrimKey, long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_G_S.filterFind(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, status}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_S(
		long[] resourcePrimKeys, long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_G_S.filterFind(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_S(
		long[] resourcePrimKeys, long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_G_S.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByR_G_S(long resourcePrimKey, long groupId, int status) {
		_collectionPersistenceFinderByR_G_S.remove(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, status});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_G_S(long resourcePrimKey, long groupId, int status) {
		return _collectionPersistenceFinderByR_G_S.count(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, status});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_G_S(long[] resourcePrimKeys, long groupId, int status) {
		return _collectionPersistenceFinderByR_G_S.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, status
			});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByR_G_S(
		long resourcePrimKey, long groupId, int status) {

		return _collectionPersistenceFinderByR_G_S.filterCount(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, status},
			groupId);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByR_G_S(
		long[] resourcePrimKeys, long groupId, int status) {

		return _collectionPersistenceFinderByR_G_S.filterCount(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, status
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByR_G_NotS;

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_NotS(
		long resourcePrimKey, long groupId, int status) {

		return findByR_G_NotS(
			resourcePrimKey, groupId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_NotS(
		long resourcePrimKey, long groupId, int status, int start, int end) {

		return findByR_G_NotS(
			resourcePrimKey, groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_NotS(
		long resourcePrimKey, long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return findByR_G_NotS(
			resourcePrimKey, groupId, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_NotS(
		long resourcePrimKey, long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_G_NotS.find(
			finderCache, new Object[] {resourcePrimKey, groupId, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByR_G_NotS_First(
			long resourcePrimKey, long groupId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByR_G_NotS.findFirst(
			finderCache, new Object[] {resourcePrimKey, groupId, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByR_G_NotS_First(
		long resourcePrimKey, long groupId, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_G_NotS.fetchFirst(
			finderCache, new Object[] {resourcePrimKey, groupId, status},
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_NotS(
		long resourcePrimKey, long groupId, int status) {

		return filterFindByR_G_NotS(
			resourcePrimKey, groupId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_NotS(
		long resourcePrimKey, long groupId, int status, int start, int end) {

		return filterFindByR_G_NotS(
			resourcePrimKey, groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_NotS(
		long resourcePrimKey, long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_G_NotS.filterFind(
			finderCache, new Object[] {resourcePrimKey, groupId, status}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByR_G_NotS(
		long resourcePrimKey, long groupId, int status) {

		_collectionPersistenceFinderByR_G_NotS.remove(
			finderCache, new Object[] {resourcePrimKey, groupId, status});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_G_NotS(long resourcePrimKey, long groupId, int status) {
		return _collectionPersistenceFinderByR_G_NotS.count(
			finderCache, new Object[] {resourcePrimKey, groupId, status});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByR_G_NotS(
		long resourcePrimKey, long groupId, int status) {

		return _collectionPersistenceFinderByR_G_NotS.filterCount(
			finderCache, new Object[] {resourcePrimKey, groupId, status},
			groupId);
	}

	private CollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByR_L_NotS;

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_L_NotS(
		long resourcePrimKey, boolean latest, int status) {

		return findByR_L_NotS(
			resourcePrimKey, latest, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_L_NotS(
		long resourcePrimKey, boolean latest, int status, int start, int end) {

		return findByR_L_NotS(
			resourcePrimKey, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_L_NotS(
		long resourcePrimKey, boolean latest, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return findByR_L_NotS(
			resourcePrimKey, latest, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_L_NotS(
		long resourcePrimKey, boolean latest, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_L_NotS.find(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, latest, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByR_L_NotS_First(
			long resourcePrimKey, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByR_L_NotS.findFirst(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, latest, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByR_L_NotS_First(
		long resourcePrimKey, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_L_NotS.fetchFirst(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, latest, status},
			orderByComparator);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_L_NotS(
		long[] resourcePrimKeys, boolean latest, int status) {

		return findByR_L_NotS(
			resourcePrimKeys, latest, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_L_NotS(
		long[] resourcePrimKeys, boolean latest, int status, int start,
		int end) {

		return findByR_L_NotS(
			resourcePrimKeys, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_L_NotS(
		long[] resourcePrimKeys, boolean latest, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return findByR_L_NotS(
			resourcePrimKeys, latest, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_L_NotS(
		long[] resourcePrimKeys, boolean latest, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_L_NotS.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), latest, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 */
	@Override
	public void removeByR_L_NotS(
		long resourcePrimKey, boolean latest, int status) {

		_collectionPersistenceFinderByR_L_NotS.remove(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, latest, status});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_L_NotS(
		long resourcePrimKey, boolean latest, int status) {

		return _collectionPersistenceFinderByR_L_NotS.count(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, latest, status});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_L_NotS(
		long[] resourcePrimKeys, boolean latest, int status) {

		return _collectionPersistenceFinderByR_L_NotS.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), latest, status
			});
	}

	private CollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByR_M_NotS;

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_M_NotS(
		long resourcePrimKey, boolean main, int status) {

		return findByR_M_NotS(
			resourcePrimKey, main, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_M_NotS(
		long resourcePrimKey, boolean main, int status, int start, int end) {

		return findByR_M_NotS(resourcePrimKey, main, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_M_NotS(
		long resourcePrimKey, boolean main, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return findByR_M_NotS(
			resourcePrimKey, main, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_M_NotS(
		long resourcePrimKey, boolean main, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_M_NotS.find(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, main, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByR_M_NotS_First(
			long resourcePrimKey, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByR_M_NotS.findFirst(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, main, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByR_M_NotS_First(
		long resourcePrimKey, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_M_NotS.fetchFirst(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, main, status},
			orderByComparator);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_M_NotS(
		long[] resourcePrimKeys, boolean main, int status) {

		return findByR_M_NotS(
			resourcePrimKeys, main, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_M_NotS(
		long[] resourcePrimKeys, boolean main, int status, int start, int end) {

		return findByR_M_NotS(resourcePrimKeys, main, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_M_NotS(
		long[] resourcePrimKeys, boolean main, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return findByR_M_NotS(
			resourcePrimKeys, main, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_M_NotS(
		long[] resourcePrimKeys, boolean main, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_M_NotS.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), main, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 */
	@Override
	public void removeByR_M_NotS(
		long resourcePrimKey, boolean main, int status) {

		_collectionPersistenceFinderByR_M_NotS.remove(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, main, status});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_M_NotS(long resourcePrimKey, boolean main, int status) {
		return _collectionPersistenceFinderByR_M_NotS.count(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, main, status});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_M_NotS(
		long[] resourcePrimKeys, boolean main, int status) {

		return _collectionPersistenceFinderByR_M_NotS.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), main, status
			});
	}

	private UniquePersistenceFinder<KBArticle, NoSuchArticleException>
		_uniquePersistenceFinderByG_ERC_V;

	/**
	 * Returns the kb article where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_ERC_V(
			long groupId, String externalReferenceCode, int version)
		throws NoSuchArticleException {

		return _uniquePersistenceFinderByG_ERC_V.find(
			finderCache,
			new Object[] {groupId, externalReferenceCode, version});
	}

	/**
	 * Returns the kb article where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_ERC_V(
		long groupId, String externalReferenceCode, int version,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_ERC_V.fetch(
			finderCache, new Object[] {groupId, externalReferenceCode, version},
			useFinderCache);
	}

	/**
	 * Removes the kb article where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the kb article that was removed
	 */
	@Override
	public KBArticle removeByG_ERC_V(
			long groupId, String externalReferenceCode, int version)
		throws NoSuchArticleException {

		KBArticle kbArticle = findByG_ERC_V(
			groupId, externalReferenceCode, version);

		return remove(kbArticle);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and externalReferenceCode = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_ERC_V(
		long groupId, String externalReferenceCode, int version) {

		return _uniquePersistenceFinderByG_ERC_V.count(
			finderCache,
			new Object[] {groupId, externalReferenceCode, version});
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_ERC_S;

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_ERC_S(
		long groupId, String externalReferenceCode, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_ERC_S.find(
			finderCache, new Object[] {groupId, externalReferenceCode, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_ERC_S_First(
			long groupId, String externalReferenceCode, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_ERC_S.findFirst(
			finderCache, new Object[] {groupId, externalReferenceCode, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_ERC_S_First(
		long groupId, String externalReferenceCode, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_ERC_S.fetchFirst(
			finderCache, new Object[] {groupId, externalReferenceCode, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_ERC_S(
		long groupId, String externalReferenceCode, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_ERC_S.filterFind(
			finderCache, new Object[] {groupId, externalReferenceCode, status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 */
	@Override
	public void removeByG_ERC_S(
		long groupId, String externalReferenceCode, int status) {

		_collectionPersistenceFinderByG_ERC_S.remove(
			finderCache, new Object[] {groupId, externalReferenceCode, status});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_ERC_S(
		long groupId, String externalReferenceCode, int status) {

		return _collectionPersistenceFinderByG_ERC_S.count(
			finderCache, new Object[] {groupId, externalReferenceCode, status});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_ERC_S(
		long groupId, String externalReferenceCode, int status) {

		return _collectionPersistenceFinderByG_ERC_S.filterCount(
			finderCache, new Object[] {groupId, externalReferenceCode, status},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_P_L;

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_L.find(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, latest},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_P_L_First(
			long groupId, long parentResourcePrimKey, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_P_L.findFirst(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, latest},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_P_L_First(
		long groupId, long parentResourcePrimKey, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_L.fetchFirst(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, latest},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_L.filterFind(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, latest},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_L.filterFind(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), latest
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_L.find(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), latest
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 */
	@Override
	public void removeByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest) {

		_collectionPersistenceFinderByG_P_L.remove(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, latest});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest) {

		return _collectionPersistenceFinderByG_P_L.count(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, latest});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest) {

		return _collectionPersistenceFinderByG_P_L.count(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), latest
			});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest) {

		return _collectionPersistenceFinderByG_P_L.filterCount(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, latest},
			groupId);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest) {

		return _collectionPersistenceFinderByG_P_L.filterCount(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), latest
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_P_M;

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_M.find(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, main},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_P_M_First(
			long groupId, long parentResourcePrimKey, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_P_M.findFirst(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, main},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_P_M_First(
		long groupId, long parentResourcePrimKey, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_M.fetchFirst(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, main},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_M.filterFind(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, main},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_M.filterFind(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), main
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_M.find(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), main
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 */
	@Override
	public void removeByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main) {

		_collectionPersistenceFinderByG_P_M.remove(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, main});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main) {

		return _collectionPersistenceFinderByG_P_M.count(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, main});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main) {

		return _collectionPersistenceFinderByG_P_M.count(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), main
			});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main) {

		return _collectionPersistenceFinderByG_P_M.filterCount(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, main},
			groupId);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main) {

		return _collectionPersistenceFinderByG_P_M.filterCount(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), main
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_P_S;

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_S(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_S.find(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_P_S_First(
			long groupId, long parentResourcePrimKey, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_P_S.findFirst(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_P_S_First(
		long groupId, long parentResourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_S.fetchFirst(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_S(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_S.filterFind(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_S.filterFind(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_S.find(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 */
	@Override
	public void removeByG_P_S(
		long groupId, long parentResourcePrimKey, int status) {

		_collectionPersistenceFinderByG_P_S.remove(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, status});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_P_S(
		long groupId, long parentResourcePrimKey, int status) {

		return _collectionPersistenceFinderByG_P_S.count(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, status});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status) {

		return _collectionPersistenceFinderByG_P_S.count(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), status
			});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_S(
		long groupId, long parentResourcePrimKey, int status) {

		return _collectionPersistenceFinderByG_P_S.filterCount(
			finderCache,
			new Object[] {groupId, new long[] {parentResourcePrimKey}, status},
			groupId);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status) {

		return _collectionPersistenceFinderByG_P_S.filterCount(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), status
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_KBFI_UT;

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_KBFI_UT.find(
			finderCache, new Object[] {groupId, kbFolderId, urlTitle}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_KBFI_UT_First(
			long groupId, long kbFolderId, String urlTitle,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_KBFI_UT.findFirst(
			finderCache, new Object[] {groupId, kbFolderId, urlTitle},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_KBFI_UT_First(
		long groupId, long kbFolderId, String urlTitle,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_KBFI_UT.fetchFirst(
			finderCache, new Object[] {groupId, kbFolderId, urlTitle},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_KBFI_UT.filterFind(
			finderCache, new Object[] {groupId, kbFolderId, urlTitle}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 */
	@Override
	public void removeByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle) {

		_collectionPersistenceFinderByG_KBFI_UT.remove(
			finderCache, new Object[] {groupId, kbFolderId, urlTitle});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle) {

		return _collectionPersistenceFinderByG_KBFI_UT.count(
			finderCache, new Object[] {groupId, kbFolderId, urlTitle});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle) {

		return _collectionPersistenceFinderByG_KBFI_UT.filterCount(
			finderCache, new Object[] {groupId, kbFolderId, urlTitle}, groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_KBFI_L;

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_KBFI_L.find(
			finderCache, new Object[] {groupId, kbFolderId, latest}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_KBFI_L_First(
			long groupId, long kbFolderId, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_KBFI_L.findFirst(
			finderCache, new Object[] {groupId, kbFolderId, latest},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_KBFI_L_First(
		long groupId, long kbFolderId, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_KBFI_L.fetchFirst(
			finderCache, new Object[] {groupId, kbFolderId, latest},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_KBFI_L.filterFind(
			finderCache, new Object[] {groupId, kbFolderId, latest}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 */
	@Override
	public void removeByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest) {

		_collectionPersistenceFinderByG_KBFI_L.remove(
			finderCache, new Object[] {groupId, kbFolderId, latest});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_KBFI_L(long groupId, long kbFolderId, boolean latest) {
		return _collectionPersistenceFinderByG_KBFI_L.count(
			finderCache, new Object[] {groupId, kbFolderId, latest});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest) {

		return _collectionPersistenceFinderByG_KBFI_L.filterCount(
			finderCache, new Object[] {groupId, kbFolderId, latest}, groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_KBFI_S;

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_KBFI_S(
		long groupId, long kbFolderId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_KBFI_S.find(
			finderCache, new Object[] {groupId, kbFolderId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_KBFI_S_First(
			long groupId, long kbFolderId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_KBFI_S.findFirst(
			finderCache, new Object[] {groupId, kbFolderId, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_KBFI_S_First(
		long groupId, long kbFolderId, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_KBFI_S.fetchFirst(
			finderCache, new Object[] {groupId, kbFolderId, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_KBFI_S(
		long groupId, long kbFolderId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_KBFI_S.filterFind(
			finderCache, new Object[] {groupId, kbFolderId, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and kbFolderId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 */
	@Override
	public void removeByG_KBFI_S(long groupId, long kbFolderId, int status) {
		_collectionPersistenceFinderByG_KBFI_S.remove(
			finderCache, new Object[] {groupId, kbFolderId, status});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_KBFI_S(long groupId, long kbFolderId, int status) {
		return _collectionPersistenceFinderByG_KBFI_S.count(
			finderCache, new Object[] {groupId, kbFolderId, status});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_KBFI_S(
		long groupId, long kbFolderId, int status) {

		return _collectionPersistenceFinderByG_KBFI_S.filterCount(
			finderCache, new Object[] {groupId, kbFolderId, status}, groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_LikeS_L;

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_L(
		long groupId, String sections, boolean latest) {

		return findByG_LikeS_L(
			groupId, sections, latest, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_L(
		long groupId, String sections, boolean latest, int start, int end) {

		return findByG_LikeS_L(groupId, sections, latest, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_L(
		long groupId, String sections, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return findByG_LikeS_L(
			groupId, sections, latest, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_L(
		long groupId, String sections, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeS_L.find(
			finderCache,
			new Object[] {groupId, new String[] {sections}, latest}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_LikeS_L_First(
			long groupId, String sections, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_LikeS_L.findFirst(
			finderCache,
			new Object[] {groupId, new String[] {sections}, latest},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_LikeS_L_First(
		long groupId, String sections, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeS_L.fetchFirst(
			finderCache,
			new Object[] {groupId, new String[] {sections}, latest},
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_L(
		long groupId, String sections, boolean latest) {

		return filterFindByG_LikeS_L(
			groupId, sections, latest, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_L(
		long groupId, String sections, boolean latest, int start, int end) {

		return filterFindByG_LikeS_L(
			groupId, sections, latest, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_L(
		long groupId, String sections, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeS_L.filterFind(
			finderCache,
			new Object[] {groupId, new String[] {sections}, latest}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest) {

		return filterFindByG_LikeS_L(
			groupId, sectionses, latest, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest, int start, int end) {

		return filterFindByG_LikeS_L(
			groupId, sectionses, latest, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeS_L.filterFind(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(sectionses), latest},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest) {

		return findByG_LikeS_L(
			groupId, sectionses, latest, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest, int start, int end) {

		return findByG_LikeS_L(groupId, sectionses, latest, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return findByG_LikeS_L(
			groupId, sectionses, latest, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeS_L.find(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(sectionses), latest},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 */
	@Override
	public void removeByG_LikeS_L(
		long groupId, String sections, boolean latest) {

		_collectionPersistenceFinderByG_LikeS_L.remove(
			finderCache,
			new Object[] {groupId, new String[] {sections}, latest});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_LikeS_L(long groupId, String sections, boolean latest) {
		return _collectionPersistenceFinderByG_LikeS_L.count(
			finderCache,
			new Object[] {groupId, new String[] {sections}, latest});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest) {

		return _collectionPersistenceFinderByG_LikeS_L.count(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(sectionses), latest});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeS_L(
		long groupId, String sections, boolean latest) {

		return _collectionPersistenceFinderByG_LikeS_L.filterCount(
			finderCache,
			new Object[] {groupId, new String[] {sections}, latest}, groupId);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest) {

		return _collectionPersistenceFinderByG_LikeS_L.filterCount(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(sectionses), latest},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_LikeS_M;

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_M(
		long groupId, String sections, boolean main) {

		return findByG_LikeS_M(
			groupId, sections, main, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_M(
		long groupId, String sections, boolean main, int start, int end) {

		return findByG_LikeS_M(groupId, sections, main, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_M(
		long groupId, String sections, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return findByG_LikeS_M(
			groupId, sections, main, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_M(
		long groupId, String sections, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeS_M.find(
			finderCache, new Object[] {groupId, new String[] {sections}, main},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_LikeS_M_First(
			long groupId, String sections, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_LikeS_M.findFirst(
			finderCache, new Object[] {groupId, new String[] {sections}, main},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_LikeS_M_First(
		long groupId, String sections, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeS_M.fetchFirst(
			finderCache, new Object[] {groupId, new String[] {sections}, main},
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_M(
		long groupId, String sections, boolean main) {

		return filterFindByG_LikeS_M(
			groupId, sections, main, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_M(
		long groupId, String sections, boolean main, int start, int end) {

		return filterFindByG_LikeS_M(groupId, sections, main, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_M(
		long groupId, String sections, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeS_M.filterFind(
			finderCache, new Object[] {groupId, new String[] {sections}, main},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_M(
		long groupId, String[] sectionses, boolean main) {

		return filterFindByG_LikeS_M(
			groupId, sectionses, main, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_M(
		long groupId, String[] sectionses, boolean main, int start, int end) {

		return filterFindByG_LikeS_M(
			groupId, sectionses, main, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_M(
		long groupId, String[] sectionses, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeS_M.filterFind(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(sectionses), main},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_M(
		long groupId, String[] sectionses, boolean main) {

		return findByG_LikeS_M(
			groupId, sectionses, main, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_M(
		long groupId, String[] sectionses, boolean main, int start, int end) {

		return findByG_LikeS_M(groupId, sectionses, main, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_M(
		long groupId, String[] sectionses, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return findByG_LikeS_M(
			groupId, sectionses, main, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_M(
		long groupId, String[] sectionses, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeS_M.find(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(sectionses), main},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 */
	@Override
	public void removeByG_LikeS_M(long groupId, String sections, boolean main) {
		_collectionPersistenceFinderByG_LikeS_M.remove(
			finderCache, new Object[] {groupId, new String[] {sections}, main});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_LikeS_M(long groupId, String sections, boolean main) {
		return _collectionPersistenceFinderByG_LikeS_M.count(
			finderCache, new Object[] {groupId, new String[] {sections}, main});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_LikeS_M(
		long groupId, String[] sectionses, boolean main) {

		return _collectionPersistenceFinderByG_LikeS_M.count(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(sectionses), main});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeS_M(
		long groupId, String sections, boolean main) {

		return _collectionPersistenceFinderByG_LikeS_M.filterCount(
			finderCache, new Object[] {groupId, new String[] {sections}, main},
			groupId);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeS_M(
		long groupId, String[] sectionses, boolean main) {

		return _collectionPersistenceFinderByG_LikeS_M.filterCount(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(sectionses), main},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_LikeS_S;

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_S(
		long groupId, String sections, int status) {

		return findByG_LikeS_S(
			groupId, sections, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_S(
		long groupId, String sections, int status, int start, int end) {

		return findByG_LikeS_S(groupId, sections, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_S(
		long groupId, String sections, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return findByG_LikeS_S(
			groupId, sections, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_S(
		long groupId, String sections, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeS_S.find(
			finderCache,
			new Object[] {groupId, new String[] {sections}, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_LikeS_S_First(
			long groupId, String sections, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_LikeS_S.findFirst(
			finderCache,
			new Object[] {groupId, new String[] {sections}, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_LikeS_S_First(
		long groupId, String sections, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeS_S.fetchFirst(
			finderCache,
			new Object[] {groupId, new String[] {sections}, status},
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_S(
		long groupId, String sections, int status) {

		return filterFindByG_LikeS_S(
			groupId, sections, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_S(
		long groupId, String sections, int status, int start, int end) {

		return filterFindByG_LikeS_S(
			groupId, sections, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_S(
		long groupId, String sections, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeS_S.filterFind(
			finderCache,
			new Object[] {groupId, new String[] {sections}, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_S(
		long groupId, String[] sectionses, int status) {

		return filterFindByG_LikeS_S(
			groupId, sectionses, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_S(
		long groupId, String[] sectionses, int status, int start, int end) {

		return filterFindByG_LikeS_S(
			groupId, sectionses, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_S(
		long groupId, String[] sectionses, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeS_S.filterFind(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(sectionses), status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_S(
		long groupId, String[] sectionses, int status) {

		return findByG_LikeS_S(
			groupId, sectionses, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_S(
		long groupId, String[] sectionses, int status, int start, int end) {

		return findByG_LikeS_S(groupId, sectionses, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_S(
		long groupId, String[] sectionses, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return findByG_LikeS_S(
			groupId, sectionses, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_S(
		long groupId, String[] sectionses, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeS_S.find(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(sectionses), status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 */
	@Override
	public void removeByG_LikeS_S(long groupId, String sections, int status) {
		_collectionPersistenceFinderByG_LikeS_S.remove(
			finderCache,
			new Object[] {groupId, new String[] {sections}, status});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_LikeS_S(long groupId, String sections, int status) {
		return _collectionPersistenceFinderByG_LikeS_S.count(
			finderCache,
			new Object[] {groupId, new String[] {sections}, status});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_LikeS_S(long groupId, String[] sectionses, int status) {
		return _collectionPersistenceFinderByG_LikeS_S.count(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(sectionses), status});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeS_S(
		long groupId, String sections, int status) {

		return _collectionPersistenceFinderByG_LikeS_S.filterCount(
			finderCache,
			new Object[] {groupId, new String[] {sections}, status}, groupId);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeS_S(
		long groupId, String[] sectionses, int status) {

		return _collectionPersistenceFinderByG_LikeS_S.filterCount(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(sectionses), status},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_L_NotS;

	/**
	 * Returns all the kb articles where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_L_NotS(
		long groupId, boolean latest, int status) {

		return findByG_L_NotS(
			groupId, latest, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_L_NotS(
		long groupId, boolean latest, int status, int start, int end) {

		return findByG_L_NotS(groupId, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_L_NotS(
		long groupId, boolean latest, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return findByG_L_NotS(
			groupId, latest, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_L_NotS(
		long groupId, boolean latest, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_L_NotS.find(
			finderCache, new Object[] {groupId, latest, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_L_NotS_First(
			long groupId, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_L_NotS.findFirst(
			finderCache, new Object[] {groupId, latest, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_L_NotS_First(
		long groupId, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_L_NotS.fetchFirst(
			finderCache, new Object[] {groupId, latest, status},
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_L_NotS(
		long groupId, boolean latest, int status) {

		return filterFindByG_L_NotS(
			groupId, latest, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_L_NotS(
		long groupId, boolean latest, int status, int start, int end) {

		return filterFindByG_L_NotS(groupId, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_L_NotS(
		long groupId, boolean latest, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_L_NotS.filterFind(
			finderCache, new Object[] {groupId, latest, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 */
	@Override
	public void removeByG_L_NotS(long groupId, boolean latest, int status) {
		_collectionPersistenceFinderByG_L_NotS.remove(
			finderCache, new Object[] {groupId, latest, status});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_L_NotS(long groupId, boolean latest, int status) {
		return _collectionPersistenceFinderByG_L_NotS.count(
			finderCache, new Object[] {groupId, latest, status});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_L_NotS(long groupId, boolean latest, int status) {
		return _collectionPersistenceFinderByG_L_NotS.filterCount(
			finderCache, new Object[] {groupId, latest, status}, groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_M_NotS;

	/**
	 * Returns all the kb articles where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_M_NotS(
		long groupId, boolean main, int status) {

		return findByG_M_NotS(
			groupId, main, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_M_NotS(
		long groupId, boolean main, int status, int start, int end) {

		return findByG_M_NotS(groupId, main, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_M_NotS(
		long groupId, boolean main, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return findByG_M_NotS(
			groupId, main, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_M_NotS(
		long groupId, boolean main, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_M_NotS.find(
			finderCache, new Object[] {groupId, main, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_M_NotS_First(
			long groupId, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_M_NotS.findFirst(
			finderCache, new Object[] {groupId, main, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_M_NotS_First(
		long groupId, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_M_NotS.fetchFirst(
			finderCache, new Object[] {groupId, main, status},
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_M_NotS(
		long groupId, boolean main, int status) {

		return filterFindByG_M_NotS(
			groupId, main, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_M_NotS(
		long groupId, boolean main, int status, int start, int end) {

		return filterFindByG_M_NotS(groupId, main, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_M_NotS(
		long groupId, boolean main, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_M_NotS.filterFind(
			finderCache, new Object[] {groupId, main, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 */
	@Override
	public void removeByG_M_NotS(long groupId, boolean main, int status) {
		_collectionPersistenceFinderByG_M_NotS.remove(
			finderCache, new Object[] {groupId, main, status});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_M_NotS(long groupId, boolean main, int status) {
		return _collectionPersistenceFinderByG_M_NotS.count(
			finderCache, new Object[] {groupId, main, status});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_M_NotS(long groupId, boolean main, int status) {
		return _collectionPersistenceFinderByG_M_NotS.filterCount(
			finderCache, new Object[] {groupId, main, status}, groupId);
	}

	private CollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByC_L_NotS;

	/**
	 * Returns all the kb articles where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByC_L_NotS(
		long companyId, boolean latest, int status) {

		return findByC_L_NotS(
			companyId, latest, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the kb articles where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByC_L_NotS(
		long companyId, boolean latest, int status, int start, int end) {

		return findByC_L_NotS(companyId, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByC_L_NotS(
		long companyId, boolean latest, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return findByC_L_NotS(
			companyId, latest, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByC_L_NotS(
		long companyId, boolean latest, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_L_NotS.find(
			finderCache, new Object[] {companyId, latest, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByC_L_NotS_First(
			long companyId, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByC_L_NotS.findFirst(
			finderCache, new Object[] {companyId, latest, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByC_L_NotS_First(
		long companyId, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByC_L_NotS.fetchFirst(
			finderCache, new Object[] {companyId, latest, status},
			orderByComparator);
	}

	/**
	 * Removes all the kb articles where companyId = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 */
	@Override
	public void removeByC_L_NotS(long companyId, boolean latest, int status) {
		_collectionPersistenceFinderByC_L_NotS.remove(
			finderCache, new Object[] {companyId, latest, status});
	}

	/**
	 * Returns the number of kb articles where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByC_L_NotS(long companyId, boolean latest, int status) {
		return _collectionPersistenceFinderByC_L_NotS.count(
			finderCache, new Object[] {companyId, latest, status});
	}

	private CollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByC_M_NotS;

	/**
	 * Returns all the kb articles where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByC_M_NotS(
		long companyId, boolean main, int status) {

		return findByC_M_NotS(
			companyId, main, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the kb articles where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByC_M_NotS(
		long companyId, boolean main, int status, int start, int end) {

		return findByC_M_NotS(companyId, main, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByC_M_NotS(
		long companyId, boolean main, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return findByC_M_NotS(
			companyId, main, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByC_M_NotS(
		long companyId, boolean main, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_M_NotS.find(
			finderCache, new Object[] {companyId, main, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByC_M_NotS_First(
			long companyId, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByC_M_NotS.findFirst(
			finderCache, new Object[] {companyId, main, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByC_M_NotS_First(
		long companyId, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByC_M_NotS.fetchFirst(
			finderCache, new Object[] {companyId, main, status},
			orderByComparator);
	}

	/**
	 * Removes all the kb articles where companyId = &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 */
	@Override
	public void removeByC_M_NotS(long companyId, boolean main, int status) {
		_collectionPersistenceFinderByC_M_NotS.remove(
			finderCache, new Object[] {companyId, main, status});
	}

	/**
	 * Returns the number of kb articles where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByC_M_NotS(long companyId, boolean main, int status) {
		return _collectionPersistenceFinderByC_M_NotS.count(
			finderCache, new Object[] {companyId, main, status});
	}

	private CollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByP_L_NotS;

	/**
	 * Returns all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_L_NotS(
		long parentResourcePrimKey, boolean latest, int status) {

		return findByP_L_NotS(
			parentResourcePrimKey, latest, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_L_NotS(
		long parentResourcePrimKey, boolean latest, int status, int start,
		int end) {

		return findByP_L_NotS(
			parentResourcePrimKey, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_L_NotS(
		long parentResourcePrimKey, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return findByP_L_NotS(
			parentResourcePrimKey, latest, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_L_NotS(
		long parentResourcePrimKey, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_L_NotS.find(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, latest, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByP_L_NotS_First(
			long parentResourcePrimKey, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByP_L_NotS.findFirst(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, latest, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByP_L_NotS_First(
		long parentResourcePrimKey, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByP_L_NotS.fetchFirst(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, latest, status},
			orderByComparator);
	}

	/**
	 * Returns all the kb articles where parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_L_NotS(
		long[] parentResourcePrimKeys, boolean latest, int status) {

		return findByP_L_NotS(
			parentResourcePrimKeys, latest, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_L_NotS(
		long[] parentResourcePrimKeys, boolean latest, int status, int start,
		int end) {

		return findByP_L_NotS(
			parentResourcePrimKeys, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_L_NotS(
		long[] parentResourcePrimKeys, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return findByP_L_NotS(
			parentResourcePrimKeys, latest, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_L_NotS(
		long[] parentResourcePrimKeys, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_L_NotS.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(parentResourcePrimKeys), latest, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 */
	@Override
	public void removeByP_L_NotS(
		long parentResourcePrimKey, boolean latest, int status) {

		_collectionPersistenceFinderByP_L_NotS.remove(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, latest, status});
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByP_L_NotS(
		long parentResourcePrimKey, boolean latest, int status) {

		return _collectionPersistenceFinderByP_L_NotS.count(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, latest, status});
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByP_L_NotS(
		long[] parentResourcePrimKeys, boolean latest, int status) {

		return _collectionPersistenceFinderByP_L_NotS.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(parentResourcePrimKeys), latest, status
			});
	}

	private CollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByP_M_NotS;

	/**
	 * Returns all the kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_M_NotS(
		long parentResourcePrimKey, boolean main, int status) {

		return findByP_M_NotS(
			parentResourcePrimKey, main, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_M_NotS(
		long parentResourcePrimKey, boolean main, int status, int start,
		int end) {

		return findByP_M_NotS(
			parentResourcePrimKey, main, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_M_NotS(
		long parentResourcePrimKey, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return findByP_M_NotS(
			parentResourcePrimKey, main, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_M_NotS(
		long parentResourcePrimKey, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_M_NotS.find(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, main, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByP_M_NotS_First(
			long parentResourcePrimKey, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByP_M_NotS.findFirst(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, main, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByP_M_NotS_First(
		long parentResourcePrimKey, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByP_M_NotS.fetchFirst(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, main, status},
			orderByComparator);
	}

	/**
	 * Returns all the kb articles where parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_M_NotS(
		long[] parentResourcePrimKeys, boolean main, int status) {

		return findByP_M_NotS(
			parentResourcePrimKeys, main, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_M_NotS(
		long[] parentResourcePrimKeys, boolean main, int status, int start,
		int end) {

		return findByP_M_NotS(
			parentResourcePrimKeys, main, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_M_NotS(
		long[] parentResourcePrimKeys, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return findByP_M_NotS(
			parentResourcePrimKeys, main, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByP_M_NotS(
		long[] parentResourcePrimKeys, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_M_NotS.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(parentResourcePrimKeys), main, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 */
	@Override
	public void removeByP_M_NotS(
		long parentResourcePrimKey, boolean main, int status) {

		_collectionPersistenceFinderByP_M_NotS.remove(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, main, status});
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByP_M_NotS(
		long parentResourcePrimKey, boolean main, int status) {

		return _collectionPersistenceFinderByP_M_NotS.count(
			finderCache,
			new Object[] {new long[] {parentResourcePrimKey}, main, status});
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByP_M_NotS(
		long[] parentResourcePrimKeys, boolean main, int status) {

		return _collectionPersistenceFinderByP_M_NotS.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(parentResourcePrimKeys), main, status
			});
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByR_G_L_NotS;

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status) {

		return findByR_G_L_NotS(
			resourcePrimKey, groupId, latest, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status,
		int start, int end) {

		return findByR_G_L_NotS(
			resourcePrimKey, groupId, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return findByR_G_L_NotS(
			resourcePrimKey, groupId, latest, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_G_L_NotS.find(
			finderCache,
			new Object[] {
				new long[] {resourcePrimKey}, groupId, latest, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByR_G_L_NotS_First(
			long resourcePrimKey, long groupId, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByR_G_L_NotS.findFirst(
			finderCache,
			new Object[] {
				new long[] {resourcePrimKey}, groupId, latest, status
			},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByR_G_L_NotS_First(
		long resourcePrimKey, long groupId, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_G_L_NotS.fetchFirst(
			finderCache,
			new Object[] {
				new long[] {resourcePrimKey}, groupId, latest, status
			},
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status) {

		return filterFindByR_G_L_NotS(
			resourcePrimKey, groupId, latest, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status,
		int start, int end) {

		return filterFindByR_G_L_NotS(
			resourcePrimKey, groupId, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_G_L_NotS.filterFind(
			finderCache,
			new Object[] {
				new long[] {resourcePrimKey}, groupId, latest, status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status) {

		return filterFindByR_G_L_NotS(
			resourcePrimKeys, groupId, latest, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status,
		int start, int end) {

		return filterFindByR_G_L_NotS(
			resourcePrimKeys, groupId, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_G_L_NotS.filterFind(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, latest,
				status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status) {

		return findByR_G_L_NotS(
			resourcePrimKeys, groupId, latest, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status,
		int start, int end) {

		return findByR_G_L_NotS(
			resourcePrimKeys, groupId, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return findByR_G_L_NotS(
			resourcePrimKeys, groupId, latest, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_G_L_NotS.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, latest,
				status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 */
	@Override
	public void removeByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status) {

		_collectionPersistenceFinderByR_G_L_NotS.remove(
			finderCache,
			new Object[] {
				new long[] {resourcePrimKey}, groupId, latest, status
			});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status) {

		return _collectionPersistenceFinderByR_G_L_NotS.count(
			finderCache,
			new Object[] {
				new long[] {resourcePrimKey}, groupId, latest, status
			});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status) {

		return _collectionPersistenceFinderByR_G_L_NotS.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, latest,
				status
			});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status) {

		return _collectionPersistenceFinderByR_G_L_NotS.filterCount(
			finderCache,
			new Object[] {
				new long[] {resourcePrimKey}, groupId, latest, status
			},
			groupId);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status) {

		return _collectionPersistenceFinderByR_G_L_NotS.filterCount(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, latest,
				status
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByR_G_M_NotS;

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status) {

		return findByR_G_M_NotS(
			resourcePrimKey, groupId, main, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status, int start,
		int end) {

		return findByR_G_M_NotS(
			resourcePrimKey, groupId, main, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return findByR_G_M_NotS(
			resourcePrimKey, groupId, main, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_G_M_NotS.find(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, main, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByR_G_M_NotS_First(
			long resourcePrimKey, long groupId, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByR_G_M_NotS.findFirst(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, main, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByR_G_M_NotS_First(
		long resourcePrimKey, long groupId, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_G_M_NotS.fetchFirst(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, main, status},
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status) {

		return filterFindByR_G_M_NotS(
			resourcePrimKey, groupId, main, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status, int start,
		int end) {

		return filterFindByR_G_M_NotS(
			resourcePrimKey, groupId, main, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_G_M_NotS.filterFind(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, main, status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status) {

		return filterFindByR_G_M_NotS(
			resourcePrimKeys, groupId, main, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status,
		int start, int end) {

		return filterFindByR_G_M_NotS(
			resourcePrimKeys, groupId, main, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_G_M_NotS.filterFind(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, main, status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status) {

		return findByR_G_M_NotS(
			resourcePrimKeys, groupId, main, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status,
		int start, int end) {

		return findByR_G_M_NotS(
			resourcePrimKeys, groupId, main, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return findByR_G_M_NotS(
			resourcePrimKeys, groupId, main, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_G_M_NotS.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, main, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 */
	@Override
	public void removeByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status) {

		_collectionPersistenceFinderByR_G_M_NotS.remove(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, main, status});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status) {

		return _collectionPersistenceFinderByR_G_M_NotS.count(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, main, status});
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status) {

		return _collectionPersistenceFinderByR_G_M_NotS.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, main, status
			});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status) {

		return _collectionPersistenceFinderByR_G_M_NotS.filterCount(
			finderCache,
			new Object[] {new long[] {resourcePrimKey}, groupId, main, status},
			groupId);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status) {

		return _collectionPersistenceFinderByR_G_M_NotS.filterCount(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(resourcePrimKeys), groupId, main, status
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_P_L_S;

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_L_S.find(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, latest, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_P_L_S_First(
			long groupId, long parentResourcePrimKey, boolean latest,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_P_L_S.findFirst(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, latest, status
			},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_P_L_S_First(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_L_S.fetchFirst(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, latest, status
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_L_S.filterFind(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, latest, status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_L_S.filterFind(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), latest,
				status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_L_S.find(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), latest,
				status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 */
	@Override
	public void removeByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status) {

		_collectionPersistenceFinderByG_P_L_S.remove(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, latest, status
			});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status) {

		return _collectionPersistenceFinderByG_P_L_S.count(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, latest, status
			});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status) {

		return _collectionPersistenceFinderByG_P_L_S.count(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), latest,
				status
			});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status) {

		return _collectionPersistenceFinderByG_P_L_S.filterCount(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, latest, status
			},
			groupId);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status) {

		return _collectionPersistenceFinderByG_P_L_S.filterCount(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), latest,
				status
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_P_L_NotS;

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status) {

		return findByG_P_L_NotS(
			groupId, parentResourcePrimKey, latest, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end) {

		return findByG_P_L_NotS(
			groupId, parentResourcePrimKey, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return findByG_P_L_NotS(
			groupId, parentResourcePrimKey, latest, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_L_NotS.find(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, latest, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_P_L_NotS_First(
			long groupId, long parentResourcePrimKey, boolean latest,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_P_L_NotS.findFirst(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, latest, status
			},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_P_L_NotS_First(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_L_NotS.fetchFirst(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, latest, status
			},
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status) {

		return filterFindByG_P_L_NotS(
			groupId, parentResourcePrimKey, latest, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end) {

		return filterFindByG_P_L_NotS(
			groupId, parentResourcePrimKey, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_L_NotS.filterFind(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, latest, status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status) {

		return filterFindByG_P_L_NotS(
			groupId, parentResourcePrimKeys, latest, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end) {

		return filterFindByG_P_L_NotS(
			groupId, parentResourcePrimKeys, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_L_NotS.filterFind(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), latest,
				status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status) {

		return findByG_P_L_NotS(
			groupId, parentResourcePrimKeys, latest, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end) {

		return findByG_P_L_NotS(
			groupId, parentResourcePrimKeys, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return findByG_P_L_NotS(
			groupId, parentResourcePrimKeys, latest, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_L_NotS.find(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), latest,
				status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 */
	@Override
	public void removeByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status) {

		_collectionPersistenceFinderByG_P_L_NotS.remove(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, latest, status
			});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status) {

		return _collectionPersistenceFinderByG_P_L_NotS.count(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, latest, status
			});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status) {

		return _collectionPersistenceFinderByG_P_L_NotS.count(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), latest,
				status
			});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status) {

		return _collectionPersistenceFinderByG_P_L_NotS.filterCount(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, latest, status
			},
			groupId);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status) {

		return _collectionPersistenceFinderByG_P_L_NotS.filterCount(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), latest,
				status
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_P_M_S;

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_M_S.find(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, main, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_P_M_S_First(
			long groupId, long parentResourcePrimKey, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_P_M_S.findFirst(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, main, status
			},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_P_M_S_First(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_M_S.fetchFirst(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, main, status
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_M_S.filterFind(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, main, status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_M_S.filterFind(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), main,
				status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_M_S.find(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), main,
				status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 */
	@Override
	public void removeByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status) {

		_collectionPersistenceFinderByG_P_M_S.remove(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, main, status
			});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status) {

		return _collectionPersistenceFinderByG_P_M_S.count(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, main, status
			});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status) {

		return _collectionPersistenceFinderByG_P_M_S.count(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), main,
				status
			});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status) {

		return _collectionPersistenceFinderByG_P_M_S.filterCount(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, main, status
			},
			groupId);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status) {

		return _collectionPersistenceFinderByG_P_M_S.filterCount(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), main,
				status
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_P_M_NotS;

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status) {

		return findByG_P_M_NotS(
			groupId, parentResourcePrimKey, main, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end) {

		return findByG_P_M_NotS(
			groupId, parentResourcePrimKey, main, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return findByG_P_M_NotS(
			groupId, parentResourcePrimKey, main, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_M_NotS.find(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, main, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_P_M_NotS_First(
			long groupId, long parentResourcePrimKey, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_P_M_NotS.findFirst(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, main, status
			},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_P_M_NotS_First(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_M_NotS.fetchFirst(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, main, status
			},
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status) {

		return filterFindByG_P_M_NotS(
			groupId, parentResourcePrimKey, main, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end) {

		return filterFindByG_P_M_NotS(
			groupId, parentResourcePrimKey, main, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_M_NotS.filterFind(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, main, status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status) {

		return filterFindByG_P_M_NotS(
			groupId, parentResourcePrimKeys, main, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end) {

		return filterFindByG_P_M_NotS(
			groupId, parentResourcePrimKeys, main, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_P_M_NotS.filterFind(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), main,
				status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status) {

		return findByG_P_M_NotS(
			groupId, parentResourcePrimKeys, main, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end) {

		return findByG_P_M_NotS(
			groupId, parentResourcePrimKeys, main, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return findByG_P_M_NotS(
			groupId, parentResourcePrimKeys, main, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_M_NotS.find(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), main,
				status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 */
	@Override
	public void removeByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status) {

		_collectionPersistenceFinderByG_P_M_NotS.remove(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, main, status
			});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status) {

		return _collectionPersistenceFinderByG_P_M_NotS.count(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, main, status
			});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status) {

		return _collectionPersistenceFinderByG_P_M_NotS.count(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), main,
				status
			});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status) {

		return _collectionPersistenceFinderByG_P_M_NotS.filterCount(
			finderCache,
			new Object[] {
				groupId, new long[] {parentResourcePrimKey}, main, status
			},
			groupId);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status) {

		return _collectionPersistenceFinderByG_P_M_NotS.filterCount(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentResourcePrimKeys), main,
				status
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_KBFI_UT_S;

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_KBFI_UT_S.find(
			finderCache,
			new Object[] {groupId, kbFolderId, urlTitle, new int[] {status}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_KBFI_UT_S_First(
			long groupId, long kbFolderId, String urlTitle, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_KBFI_UT_S.findFirst(
			finderCache,
			new Object[] {groupId, kbFolderId, urlTitle, new int[] {status}},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_KBFI_UT_S_First(
		long groupId, long kbFolderId, String urlTitle, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_KBFI_UT_S.fetchFirst(
			finderCache,
			new Object[] {groupId, kbFolderId, urlTitle, new int[] {status}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_KBFI_UT_S.filterFind(
			finderCache,
			new Object[] {groupId, kbFolderId, urlTitle, new int[] {status}},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_KBFI_UT_S.filterFind(
			finderCache,
			new Object[] {
				groupId, kbFolderId, urlTitle, ArrayUtil.sortedUnique(statuses)
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_KBFI_UT_S.find(
			finderCache,
			new Object[] {
				groupId, kbFolderId, urlTitle, ArrayUtil.sortedUnique(statuses)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 */
	@Override
	public void removeByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status) {

		_collectionPersistenceFinderByG_KBFI_UT_S.remove(
			finderCache,
			new Object[] {groupId, kbFolderId, urlTitle, new int[] {status}});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status) {

		return _collectionPersistenceFinderByG_KBFI_UT_S.count(
			finderCache,
			new Object[] {groupId, kbFolderId, urlTitle, new int[] {status}});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses) {

		return _collectionPersistenceFinderByG_KBFI_UT_S.count(
			finderCache,
			new Object[] {
				groupId, kbFolderId, urlTitle, ArrayUtil.sortedUnique(statuses)
			});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status) {

		return _collectionPersistenceFinderByG_KBFI_UT_S.filterCount(
			finderCache,
			new Object[] {groupId, kbFolderId, urlTitle, new int[] {status}},
			groupId);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses) {

		return _collectionPersistenceFinderByG_KBFI_UT_S.filterCount(
			finderCache,
			new Object[] {
				groupId, kbFolderId, urlTitle, ArrayUtil.sortedUnique(statuses)
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_KBFI_UT_NotS;

	/**
	 * Returns all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status) {

		return findByG_KBFI_UT_NotS(
			groupId, kbFolderId, urlTitle, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end) {

		return findByG_KBFI_UT_NotS(
			groupId, kbFolderId, urlTitle, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return findByG_KBFI_UT_NotS(
			groupId, kbFolderId, urlTitle, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_KBFI_UT_NotS.find(
			finderCache, new Object[] {groupId, kbFolderId, urlTitle, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_KBFI_UT_NotS_First(
			long groupId, long kbFolderId, String urlTitle, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_KBFI_UT_NotS.findFirst(
			finderCache, new Object[] {groupId, kbFolderId, urlTitle, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_KBFI_UT_NotS_First(
		long groupId, long kbFolderId, String urlTitle, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_KBFI_UT_NotS.fetchFirst(
			finderCache, new Object[] {groupId, kbFolderId, urlTitle, status},
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status) {

		return filterFindByG_KBFI_UT_NotS(
			groupId, kbFolderId, urlTitle, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end) {

		return filterFindByG_KBFI_UT_NotS(
			groupId, kbFolderId, urlTitle, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_KBFI_UT_NotS.filterFind(
			finderCache, new Object[] {groupId, kbFolderId, urlTitle, status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 */
	@Override
	public void removeByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status) {

		_collectionPersistenceFinderByG_KBFI_UT_NotS.remove(
			finderCache, new Object[] {groupId, kbFolderId, urlTitle, status});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status) {

		return _collectionPersistenceFinderByG_KBFI_UT_NotS.count(
			finderCache, new Object[] {groupId, kbFolderId, urlTitle, status});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status) {

		return _collectionPersistenceFinderByG_KBFI_UT_NotS.filterCount(
			finderCache, new Object[] {groupId, kbFolderId, urlTitle, status},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_KBFI_L_NotS;

	/**
	 * Returns all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status) {

		return findByG_KBFI_L_NotS(
			groupId, kbFolderId, latest, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status, int start,
		int end) {

		return findByG_KBFI_L_NotS(
			groupId, kbFolderId, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return findByG_KBFI_L_NotS(
			groupId, kbFolderId, latest, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_KBFI_L_NotS.find(
			finderCache, new Object[] {groupId, kbFolderId, latest, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_KBFI_L_NotS_First(
			long groupId, long kbFolderId, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_KBFI_L_NotS.findFirst(
			finderCache, new Object[] {groupId, kbFolderId, latest, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_KBFI_L_NotS_First(
		long groupId, long kbFolderId, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_KBFI_L_NotS.fetchFirst(
			finderCache, new Object[] {groupId, kbFolderId, latest, status},
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status) {

		return filterFindByG_KBFI_L_NotS(
			groupId, kbFolderId, latest, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status, int start,
		int end) {

		return filterFindByG_KBFI_L_NotS(
			groupId, kbFolderId, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_KBFI_L_NotS.filterFind(
			finderCache, new Object[] {groupId, kbFolderId, latest, status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 */
	@Override
	public void removeByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status) {

		_collectionPersistenceFinderByG_KBFI_L_NotS.remove(
			finderCache, new Object[] {groupId, kbFolderId, latest, status});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status) {

		return _collectionPersistenceFinderByG_KBFI_L_NotS.count(
			finderCache, new Object[] {groupId, kbFolderId, latest, status});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status) {

		return _collectionPersistenceFinderByG_KBFI_L_NotS.filterCount(
			finderCache, new Object[] {groupId, kbFolderId, latest, status},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_LikeS_L_NotS;

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status) {

		return findByG_LikeS_L_NotS(
			groupId, sections, latest, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status, int start,
		int end) {

		return findByG_LikeS_L_NotS(
			groupId, sections, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return findByG_LikeS_L_NotS(
			groupId, sections, latest, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeS_L_NotS.find(
			finderCache,
			new Object[] {groupId, new String[] {sections}, latest, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_LikeS_L_NotS_First(
			long groupId, String sections, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_LikeS_L_NotS.findFirst(
			finderCache,
			new Object[] {groupId, new String[] {sections}, latest, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_LikeS_L_NotS_First(
		long groupId, String sections, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeS_L_NotS.fetchFirst(
			finderCache,
			new Object[] {groupId, new String[] {sections}, latest, status},
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status) {

		return filterFindByG_LikeS_L_NotS(
			groupId, sections, latest, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status, int start,
		int end) {

		return filterFindByG_LikeS_L_NotS(
			groupId, sections, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeS_L_NotS.filterFind(
			finderCache,
			new Object[] {groupId, new String[] {sections}, latest, status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status) {

		return filterFindByG_LikeS_L_NotS(
			groupId, sectionses, latest, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status,
		int start, int end) {

		return filterFindByG_LikeS_L_NotS(
			groupId, sectionses, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeS_L_NotS.filterFind(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(sectionses), latest, status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status) {

		return findByG_LikeS_L_NotS(
			groupId, sectionses, latest, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status,
		int start, int end) {

		return findByG_LikeS_L_NotS(
			groupId, sectionses, latest, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return findByG_LikeS_L_NotS(
			groupId, sectionses, latest, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeS_L_NotS.find(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(sectionses), latest, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 */
	@Override
	public void removeByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status) {

		_collectionPersistenceFinderByG_LikeS_L_NotS.remove(
			finderCache,
			new Object[] {groupId, new String[] {sections}, latest, status});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status) {

		return _collectionPersistenceFinderByG_LikeS_L_NotS.count(
			finderCache,
			new Object[] {groupId, new String[] {sections}, latest, status});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status) {

		return _collectionPersistenceFinderByG_LikeS_L_NotS.count(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(sectionses), latest, status
			});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status) {

		return _collectionPersistenceFinderByG_LikeS_L_NotS.filterCount(
			finderCache,
			new Object[] {groupId, new String[] {sections}, latest, status},
			groupId);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status) {

		return _collectionPersistenceFinderByG_LikeS_L_NotS.filterCount(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(sectionses), latest, status
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder<KBArticle, NoSuchArticleException>
		_collectionPersistenceFinderByG_LikeS_M_NotS;

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status) {

		return findByG_LikeS_M_NotS(
			groupId, sections, main, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status, int start,
		int end) {

		return findByG_LikeS_M_NotS(
			groupId, sections, main, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return findByG_LikeS_M_NotS(
			groupId, sections, main, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeS_M_NotS.find(
			finderCache,
			new Object[] {groupId, new String[] {sections}, main, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	@Override
	public KBArticle findByG_LikeS_M_NotS_First(
			long groupId, String sections, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_LikeS_M_NotS.findFirst(
			finderCache,
			new Object[] {groupId, new String[] {sections}, main, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Override
	public KBArticle fetchByG_LikeS_M_NotS_First(
		long groupId, String sections, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeS_M_NotS.fetchFirst(
			finderCache,
			new Object[] {groupId, new String[] {sections}, main, status},
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status) {

		return filterFindByG_LikeS_M_NotS(
			groupId, sections, main, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status, int start,
		int end) {

		return filterFindByG_LikeS_M_NotS(
			groupId, sections, main, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeS_M_NotS.filterFind(
			finderCache,
			new Object[] {groupId, new String[] {sections}, main, status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status) {

		return filterFindByG_LikeS_M_NotS(
			groupId, sectionses, main, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status, int start,
		int end) {

		return filterFindByG_LikeS_M_NotS(
			groupId, sectionses, main, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	@Override
	public List<KBArticle> filterFindByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeS_M_NotS.filterFind(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(sectionses), main, status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status) {

		return findByG_LikeS_M_NotS(
			groupId, sectionses, main, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status, int start,
		int end) {

		return findByG_LikeS_M_NotS(
			groupId, sectionses, main, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return findByG_LikeS_M_NotS(
			groupId, sectionses, main, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	@Override
	public List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeS_M_NotS.find(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(sectionses), main, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 */
	@Override
	public void removeByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status) {

		_collectionPersistenceFinderByG_LikeS_M_NotS.remove(
			finderCache,
			new Object[] {groupId, new String[] {sections}, main, status});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status) {

		return _collectionPersistenceFinderByG_LikeS_M_NotS.count(
			finderCache,
			new Object[] {groupId, new String[] {sections}, main, status});
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	@Override
	public int countByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status) {

		return _collectionPersistenceFinderByG_LikeS_M_NotS.count(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(sectionses), main, status
			});
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status) {

		return _collectionPersistenceFinderByG_LikeS_M_NotS.filterCount(
			finderCache,
			new Object[] {groupId, new String[] {sections}, main, status},
			groupId);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status) {

		return _collectionPersistenceFinderByG_LikeS_M_NotS.filterCount(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(sectionses), main, status
			},
			groupId);
	}

	public KBArticlePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(KBArticle.class);

		setModelImplClass(KBArticleImpl.class);
		setModelPKClass(long.class);

		setTable(KBArticleTable.INSTANCE);
	}

	/**
	 * Creates a new kb article with the primary key. Does not add the kb article to the database.
	 *
	 * @param kbArticleId the primary key for the new kb article
	 * @return the new kb article
	 */
	@Override
	public KBArticle create(long kbArticleId) {
		KBArticle kbArticle = new KBArticleImpl();

		kbArticle.setNew(true);
		kbArticle.setPrimaryKey(kbArticleId);

		String uuid = PortalUUIDUtil.generate();

		kbArticle.setUuid(uuid);

		kbArticle.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kbArticle;
	}

	/**
	 * Removes the kb article with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kbArticleId the primary key of the kb article
	 * @return the kb article that was removed
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	@Override
	public KBArticle remove(long kbArticleId) throws NoSuchArticleException {
		return remove((Serializable)kbArticleId);
	}

	@Override
	protected KBArticle removeImpl(KBArticle kbArticle) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kbArticle)) {
				kbArticle = (KBArticle)session.get(
					KBArticleImpl.class, kbArticle.getPrimaryKeyObj());
			}

			if ((kbArticle != null) &&
				ctPersistenceHelper.isRemove(kbArticle)) {

				session.delete(kbArticle);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kbArticle != null) {
			clearCache(kbArticle);
		}

		return kbArticle;
	}

	@Override
	public KBArticle updateImpl(KBArticle kbArticle) {
		boolean isNew = kbArticle.isNew();

		if (!(kbArticle instanceof KBArticleModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kbArticle.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(kbArticle);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kbArticle proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KBArticle implementation " +
					kbArticle.getClass());
		}

		KBArticleModelImpl kbArticleModelImpl = (KBArticleModelImpl)kbArticle;

		if (Validator.isNull(kbArticle.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			kbArticle.setUuid(uuid);
		}

		if (Validator.isNull(kbArticle.getExternalReferenceCode())) {
			kbArticle.setExternalReferenceCode(kbArticle.getUuid());
		}
		else {
			if (!Objects.equals(
					kbArticleModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					kbArticle.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = kbArticle.getCompanyId();

					long groupId = kbArticle.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = kbArticle.getPrimaryKey();
					}

					try {
						kbArticle.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								KBArticle.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								kbArticle.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kbArticle.getCreateDate() == null)) {
			if (serviceContext == null) {
				kbArticle.setCreateDate(date);
			}
			else {
				kbArticle.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!kbArticleModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kbArticle.setModifiedDate(date);
			}
			else {
				kbArticle.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		long userId = GetterUtil.getLong(PrincipalThreadLocal.getName());

		if (userId > 0) {
			long companyId = kbArticle.getCompanyId();

			long groupId = kbArticle.getGroupId();

			long kbArticleId = 0;

			if (!isNew) {
				kbArticleId = kbArticle.getPrimaryKey();
			}

			try {
				kbArticle.setContent(
					SanitizerUtil.sanitize(
						companyId, groupId, userId, KBArticle.class.getName(),
						kbArticleId, ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
						kbArticle.getContent(), null));
			}
			catch (SanitizerException sanitizerException) {
				throw new SystemException(sanitizerException);
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kbArticle)) {
				if (!isNew) {
					session.evict(
						KBArticleImpl.class, kbArticle.getPrimaryKeyObj());
				}

				session.save(kbArticle);
			}
			else {
				kbArticle = (KBArticle)session.merge(kbArticle);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kbArticle, false);

		if (isNew) {
			kbArticle.setNew(false);
		}

		kbArticle.resetOriginalValues();

		return kbArticle;
	}

	/**
	 * Returns the kb article with the primary key or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param kbArticleId the primary key of the kb article
	 * @return the kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	@Override
	public KBArticle findByPrimaryKey(long kbArticleId)
		throws NoSuchArticleException {

		return findByPrimaryKey((Serializable)kbArticleId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kb article with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kbArticleId the primary key of the kb article
	 * @return the kb article, or <code>null</code> if a kb article with the primary key could not be found
	 */
	@Override
	public KBArticle fetchByPrimaryKey(long kbArticleId) {
		return fetchByPrimaryKey((Serializable)kbArticleId);
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
		return "kbArticleId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KBARTICLE;
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
		return KBArticleModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KBArticle";
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
		ctMergeColumnNames.add("resourcePrimKey");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("externalReferenceCode");
		ctMergeColumnNames.add("rootResourcePrimKey");
		ctMergeColumnNames.add("parentResourceClassNameId");
		ctMergeColumnNames.add("parentResourcePrimKey");
		ctMergeColumnNames.add("kbFolderId");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("urlTitle");
		ctMergeColumnNames.add("content");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("sections");
		ctMergeColumnNames.add("latest");
		ctMergeColumnNames.add("main");
		ctMergeColumnNames.add("sourceURL");
		ctMergeColumnNames.add("displayDate");
		ctMergeColumnNames.add("expirationDate");
		ctMergeColumnNames.add("reviewDate");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("kbArticleId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"resourcePrimKey", "version"});

		_uniqueIndexColumnNames.add(
			new String[] {"resourcePrimKey", "groupId", "version"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "externalReferenceCode", "version"});
	}

	/**
	 * Initializes the kb article persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByResourcePrimKey =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByResourcePrimKey",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"resourcePrimKey"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByResourcePrimKey",
					new String[] {Long.class.getName()},
					new String[] {"resourcePrimKey"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByResourcePrimKey",
					new String[] {Long.class.getName()},
					new String[] {"resourcePrimKey"}, false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "resourcePrimKey", FinderColumn.Type.LONG,
					"=", true, true, KBArticle::getResourcePrimKey));

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
			_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
			KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"kbArticle.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, KBArticle::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(KBArticle::getUuid), KBArticle::getGroupId),
			_SQL_SELECT_KBARTICLE_WHERE, "",
			new FinderColumn<>(
				"kbArticle.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, KBArticle::getUuid),
			new FinderColumn<>(
				"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, KBArticle::getGroupId));

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
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, KBArticle::getUuid),
				new FinderColumn<>(
					"kbArticle.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, KBArticle::getCompanyId));

		_collectionPersistenceFinderByR_G =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_G",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"resourcePrimKey", "groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_G",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"resourcePrimKey", "groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_G",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"resourcePrimKey", "groupId"}, false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "resourcePrimKey", FinderColumn.Type.LONG,
					"=", true, true, KBArticle::getResourcePrimKey),
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId));

		_uniquePersistenceFinderByR_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByR_V",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"resourcePrimKey", "version"}, 0, 0, false,
				KBArticle::getResourcePrimKey, KBArticle::getVersion),
			_SQL_SELECT_KBARTICLE_WHERE, "",
			new FinderColumn<>(
				"kbArticle.", "resourcePrimKey", FinderColumn.Type.LONG, "=",
				true, true, KBArticle::getResourcePrimKey),
			new FinderColumn<>(
				"kbArticle.", "version", FinderColumn.Type.INTEGER, "=", true,
				true, KBArticle::getVersion));

		_collectionPersistenceFinderByR_L = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_L",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"resourcePrimKey", "latest"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_L",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"resourcePrimKey", "latest"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByR_L",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"resourcePrimKey", "latest"}, false),
			_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
			KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new ArrayableFinderColumn<>(
				"kbArticle.", "resourcePrimKey", FinderColumn.Type.LONG, "=",
				false, true, true, KBArticle::getResourcePrimKey),
			new FinderColumn<>(
				"kbArticle.", "latest", FinderColumn.Type.BOOLEAN, "=", true,
				true, KBArticle::isLatest));

		_collectionPersistenceFinderByR_M = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_M",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"resourcePrimKey", "main"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_M",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"resourcePrimKey", "main"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByR_M",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"resourcePrimKey", "main"}, false),
			_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
			KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new ArrayableFinderColumn<>(
				"kbArticle.", "resourcePrimKey", FinderColumn.Type.LONG, "=",
				false, true, true, KBArticle::getResourcePrimKey),
			new FinderColumn<>(
				"kbArticle.", "main", FinderColumn.Type.BOOLEAN, "=", true,
				true, KBArticle::isMain));

		_collectionPersistenceFinderByR_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"resourcePrimKey", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"resourcePrimKey", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByR_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"resourcePrimKey", "status"}, false),
			_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
			KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new ArrayableFinderColumn<>(
				"kbArticle.", "resourcePrimKey", FinderColumn.Type.LONG, "=",
				false, true, true, KBArticle::getResourcePrimKey),
			new ArrayableFinderColumn<>(
				"kbArticle.", "status", FinderColumn.Type.INTEGER, "=", false,
				true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByG_ERC =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_ERC",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "externalReferenceCode"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_ERC",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "externalReferenceCode"}, 0, 2,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_ERC",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "externalReferenceCode"}, 0, 2,
					false, null),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new FinderColumn<>(
					"kbArticle.", "externalReferenceCode",
					FinderColumn.Type.STRING, "=", true, true,
					KBArticle::getExternalReferenceCode));

		_collectionPersistenceFinderByG_L =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_L",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "latest"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_L",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "latest"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_L",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "latest"}, false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new FinderColumn<>(
					"kbArticle.", "latest", FinderColumn.Type.BOOLEAN, "=",
					true, true, KBArticle::isLatest));

		_collectionPersistenceFinderByG_M =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_M",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "main"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_M",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "main"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_M",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "main"}, false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new FinderColumn<>(
					"kbArticle.", "main", FinderColumn.Type.BOOLEAN, "=", true,
					true, KBArticle::isMain));

		_collectionPersistenceFinderByG_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_S",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_S",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_S",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "status"}, false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByC_L = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_L",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "latest"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_L",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"companyId", "latest"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_L",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"companyId", "latest"}, false),
			_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
			KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"kbArticle.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, KBArticle::getCompanyId),
			new FinderColumn<>(
				"kbArticle.", "latest", FinderColumn.Type.BOOLEAN, "=", true,
				true, KBArticle::isLatest));

		_collectionPersistenceFinderByC_M = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_M",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "main"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_M",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"companyId", "main"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_M",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"companyId", "main"}, false),
			_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
			KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"kbArticle.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, KBArticle::getCompanyId),
			new FinderColumn<>(
				"kbArticle.", "main", FinderColumn.Type.BOOLEAN, "=", true,
				true, KBArticle::isMain));

		_collectionPersistenceFinderByC_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"companyId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"companyId", "status"}, false),
			_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
			KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"kbArticle.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, KBArticle::getCompanyId),
			new FinderColumn<>(
				"kbArticle.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, KBArticle::getStatus));

		_collectionPersistenceFinderByP_L = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_L",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"parentResourcePrimKey", "latest"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_L",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"parentResourcePrimKey", "latest"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByP_L",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"parentResourcePrimKey", "latest"}, false),
			_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
			KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new ArrayableFinderColumn<>(
				"kbArticle.", "parentResourcePrimKey", FinderColumn.Type.LONG,
				"=", false, true, true, KBArticle::getParentResourcePrimKey),
			new FinderColumn<>(
				"kbArticle.", "latest", FinderColumn.Type.BOOLEAN, "=", true,
				true, KBArticle::isLatest));

		_collectionPersistenceFinderByP_M = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_M",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"parentResourcePrimKey", "main"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_M",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"parentResourcePrimKey", "main"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByP_M",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"parentResourcePrimKey", "main"}, false),
			_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
			KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new ArrayableFinderColumn<>(
				"kbArticle.", "parentResourcePrimKey", FinderColumn.Type.LONG,
				"=", false, true, true, KBArticle::getParentResourcePrimKey),
			new FinderColumn<>(
				"kbArticle.", "main", FinderColumn.Type.BOOLEAN, "=", true,
				true, KBArticle::isMain));

		_collectionPersistenceFinderByP_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"parentResourcePrimKey", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"parentResourcePrimKey", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByP_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"parentResourcePrimKey", "status"}, false),
			_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
			KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new ArrayableFinderColumn<>(
				"kbArticle.", "parentResourcePrimKey", FinderColumn.Type.LONG,
				"=", false, true, true, KBArticle::getParentResourcePrimKey),
			new FinderColumn<>(
				"kbArticle.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, KBArticle::getStatus));

		_collectionPersistenceFinderByLtD_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtD_S",
				new String[] {
					Date.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"displayDate", "status"}, true),
			null,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtD_S",
				new String[] {Date.class.getName(), Integer.class.getName()},
				new String[] {"displayDate", "status"}, false),
			_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
			KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"kbArticle.", "displayDate", FinderColumn.Type.DATE, "<", true,
				true, KBArticle::getDisplayDate),
			new FinderColumn<>(
				"kbArticle.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, KBArticle::getStatus));

		_uniquePersistenceFinderByR_G_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByR_G_V",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"resourcePrimKey", "groupId", "version"}, 0, 0,
				false, KBArticle::getResourcePrimKey, KBArticle::getGroupId,
				KBArticle::getVersion),
			_SQL_SELECT_KBARTICLE_WHERE, "",
			new FinderColumn<>(
				"kbArticle.", "resourcePrimKey", FinderColumn.Type.LONG, "=",
				true, true, KBArticle::getResourcePrimKey),
			new FinderColumn<>(
				"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, KBArticle::getGroupId),
			new FinderColumn<>(
				"kbArticle.", "version", FinderColumn.Type.INTEGER, "=", true,
				true, KBArticle::getVersion));

		_collectionPersistenceFinderByR_G_L =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_G_L",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"resourcePrimKey", "groupId", "latest"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_G_L",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"resourcePrimKey", "groupId", "latest"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByR_G_L",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"resourcePrimKey", "groupId", "latest"},
					false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new ArrayableFinderColumn<>(
					"kbArticle.", "resourcePrimKey", FinderColumn.Type.LONG,
					"=", false, true, true, KBArticle::getResourcePrimKey),
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new FinderColumn<>(
					"kbArticle.", "latest", FinderColumn.Type.BOOLEAN, "=",
					true, true, KBArticle::isLatest));

		_collectionPersistenceFinderByR_G_M =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_G_M",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"resourcePrimKey", "groupId", "main"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_G_M",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"resourcePrimKey", "groupId", "main"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByR_G_M",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"resourcePrimKey", "groupId", "main"}, false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new ArrayableFinderColumn<>(
					"kbArticle.", "resourcePrimKey", FinderColumn.Type.LONG,
					"=", false, true, true, KBArticle::getResourcePrimKey),
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new FinderColumn<>(
					"kbArticle.", "main", FinderColumn.Type.BOOLEAN, "=", true,
					true, KBArticle::isMain));

		_collectionPersistenceFinderByR_G_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_G_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"resourcePrimKey", "groupId", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_G_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"resourcePrimKey", "groupId", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByR_G_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"resourcePrimKey", "groupId", "status"},
					false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new ArrayableFinderColumn<>(
					"kbArticle.", "resourcePrimKey", FinderColumn.Type.LONG,
					"=", false, true, true, KBArticle::getResourcePrimKey),
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByR_G_NotS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_G_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"resourcePrimKey", "groupId", "status"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByR_G_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"resourcePrimKey", "groupId", "status"},
					false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "resourcePrimKey", FinderColumn.Type.LONG,
					"=", true, true, KBArticle::getResourcePrimKey),
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByR_L_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_L_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"resourcePrimKey", "latest", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByR_L_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {"resourcePrimKey", "latest", "status"},
					false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new ArrayableFinderColumn<>(
					"kbArticle.", "resourcePrimKey", FinderColumn.Type.LONG,
					"=", false, true, true, KBArticle::getResourcePrimKey),
				new FinderColumn<>(
					"kbArticle.", "latest", FinderColumn.Type.BOOLEAN, "=",
					true, true, KBArticle::isLatest),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByR_M_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_M_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"resourcePrimKey", "main", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByR_M_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {"resourcePrimKey", "main", "status"}, false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new ArrayableFinderColumn<>(
					"kbArticle.", "resourcePrimKey", FinderColumn.Type.LONG,
					"=", false, true, true, KBArticle::getResourcePrimKey),
				new FinderColumn<>(
					"kbArticle.", "main", FinderColumn.Type.BOOLEAN, "=", true,
					true, KBArticle::isMain),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, KBArticle::getStatus));

		_uniquePersistenceFinderByG_ERC_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_ERC_V",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName()
				},
				new String[] {"groupId", "externalReferenceCode", "version"}, 0,
				2, false, KBArticle::getGroupId,
				convertNullFunction(KBArticle::getExternalReferenceCode),
				KBArticle::getVersion),
			_SQL_SELECT_KBARTICLE_WHERE, "",
			new FinderColumn<>(
				"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, KBArticle::getGroupId),
			new FinderColumn<>(
				"kbArticle.", "externalReferenceCode", FinderColumn.Type.STRING,
				"=", true, true, KBArticle::getExternalReferenceCode),
			new FinderColumn<>(
				"kbArticle.", "version", FinderColumn.Type.INTEGER, "=", true,
				true, KBArticle::getVersion));

		_collectionPersistenceFinderByG_ERC_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_ERC_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "externalReferenceCode", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_ERC_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "externalReferenceCode", "status"},
					0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_ERC_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "externalReferenceCode", "status"},
					0, 2, false, null),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new FinderColumn<>(
					"kbArticle.", "externalReferenceCode",
					FinderColumn.Type.STRING, "=", true, true,
					KBArticle::getExternalReferenceCode),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByG_P_L =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_L",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "parentResourcePrimKey", "latest"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_L",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "parentResourcePrimKey", "latest"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_L",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "parentResourcePrimKey", "latest"},
					false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new ArrayableFinderColumn<>(
					"kbArticle.", "parentResourcePrimKey",
					FinderColumn.Type.LONG, "=", false, true, true,
					KBArticle::getParentResourcePrimKey),
				new FinderColumn<>(
					"kbArticle.", "latest", FinderColumn.Type.BOOLEAN, "=",
					true, true, KBArticle::isLatest));

		_collectionPersistenceFinderByG_P_M =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_M",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "parentResourcePrimKey", "main"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_M",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "parentResourcePrimKey", "main"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_M",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "parentResourcePrimKey", "main"},
					false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new ArrayableFinderColumn<>(
					"kbArticle.", "parentResourcePrimKey",
					FinderColumn.Type.LONG, "=", false, true, true,
					KBArticle::getParentResourcePrimKey),
				new FinderColumn<>(
					"kbArticle.", "main", FinderColumn.Type.BOOLEAN, "=", true,
					true, KBArticle::isMain));

		_collectionPersistenceFinderByG_P_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "parentResourcePrimKey", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "parentResourcePrimKey", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "parentResourcePrimKey", "status"},
					false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new ArrayableFinderColumn<>(
					"kbArticle.", "parentResourcePrimKey",
					FinderColumn.Type.LONG, "=", false, true, true,
					KBArticle::getParentResourcePrimKey),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByG_KBFI_UT =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_KBFI_UT",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "kbFolderId", "urlTitle"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_KBFI_UT",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName()
					},
					new String[] {"groupId", "kbFolderId", "urlTitle"}, 0, 4,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_KBFI_UT",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName()
					},
					new String[] {"groupId", "kbFolderId", "urlTitle"}, 0, 4,
					false, null),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new FinderColumn<>(
					"kbArticle.", "kbFolderId", FinderColumn.Type.LONG, "=",
					true, true, KBArticle::getKbFolderId),
				new FinderColumn<>(
					"kbArticle.", "urlTitle", FinderColumn.Type.STRING, "=",
					true, true, KBArticle::getUrlTitle));

		_collectionPersistenceFinderByG_KBFI_L =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_KBFI_L",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "kbFolderId", "latest"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_KBFI_L",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "kbFolderId", "latest"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_KBFI_L",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "kbFolderId", "latest"}, false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new FinderColumn<>(
					"kbArticle.", "kbFolderId", FinderColumn.Type.LONG, "=",
					true, true, KBArticle::getKbFolderId),
				new FinderColumn<>(
					"kbArticle.", "latest", FinderColumn.Type.BOOLEAN, "=",
					true, true, KBArticle::isLatest));

		_collectionPersistenceFinderByG_KBFI_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_KBFI_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "kbFolderId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_KBFI_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "kbFolderId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_KBFI_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "kbFolderId", "status"}, false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new FinderColumn<>(
					"kbArticle.", "kbFolderId", FinderColumn.Type.LONG, "=",
					true, true, KBArticle::getKbFolderId),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByG_LikeS_L =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeS_L",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "sections", "latest"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LikeS_L",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "sections", "latest"}, false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new ArrayableFinderColumn<>(
					"kbArticle.", "sections", FinderColumn.Type.STRING, "LIKE",
					false, true, true, KBArticle::getSections),
				new FinderColumn<>(
					"kbArticle.", "latest", FinderColumn.Type.BOOLEAN, "=",
					true, true, KBArticle::isLatest));

		_collectionPersistenceFinderByG_LikeS_M =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeS_M",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "sections", "main"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LikeS_M",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "sections", "main"}, false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new ArrayableFinderColumn<>(
					"kbArticle.", "sections", FinderColumn.Type.STRING, "LIKE",
					false, true, true, KBArticle::getSections),
				new FinderColumn<>(
					"kbArticle.", "main", FinderColumn.Type.BOOLEAN, "=", true,
					true, KBArticle::isMain));

		_collectionPersistenceFinderByG_LikeS_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeS_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "sections", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LikeS_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "sections", "status"}, false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new ArrayableFinderColumn<>(
					"kbArticle.", "sections", FinderColumn.Type.STRING, "LIKE",
					false, true, true, KBArticle::getSections),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByG_L_NotS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_L_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "latest", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_L_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "latest", "status"}, false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new FinderColumn<>(
					"kbArticle.", "latest", FinderColumn.Type.BOOLEAN, "=",
					true, true, KBArticle::isLatest),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByG_M_NotS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_M_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "main", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_M_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "main", "status"}, false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new FinderColumn<>(
					"kbArticle.", "main", FinderColumn.Type.BOOLEAN, "=", true,
					true, KBArticle::isMain),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByC_L_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_L_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "latest", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_L_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {"companyId", "latest", "status"}, false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, KBArticle::getCompanyId),
				new FinderColumn<>(
					"kbArticle.", "latest", FinderColumn.Type.BOOLEAN, "=",
					true, true, KBArticle::isLatest),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByC_M_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_M_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "main", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_M_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {"companyId", "main", "status"}, false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, KBArticle::getCompanyId),
				new FinderColumn<>(
					"kbArticle.", "main", FinderColumn.Type.BOOLEAN, "=", true,
					true, KBArticle::isMain),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByP_L_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_L_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"parentResourcePrimKey", "latest", "status"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByP_L_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {"parentResourcePrimKey", "latest", "status"},
					false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new ArrayableFinderColumn<>(
					"kbArticle.", "parentResourcePrimKey",
					FinderColumn.Type.LONG, "=", false, true, true,
					KBArticle::getParentResourcePrimKey),
				new FinderColumn<>(
					"kbArticle.", "latest", FinderColumn.Type.BOOLEAN, "=",
					true, true, KBArticle::isLatest),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByP_M_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_M_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"parentResourcePrimKey", "main", "status"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByP_M_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {"parentResourcePrimKey", "main", "status"},
					false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new ArrayableFinderColumn<>(
					"kbArticle.", "parentResourcePrimKey",
					FinderColumn.Type.LONG, "=", false, true, true,
					KBArticle::getParentResourcePrimKey),
				new FinderColumn<>(
					"kbArticle.", "main", FinderColumn.Type.BOOLEAN, "=", true,
					true, KBArticle::isMain),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByR_G_L_NotS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_G_L_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"resourcePrimKey", "groupId", "latest", "status"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByR_G_L_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {
						"resourcePrimKey", "groupId", "latest", "status"
					},
					false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new ArrayableFinderColumn<>(
					"kbArticle.", "resourcePrimKey", FinderColumn.Type.LONG,
					"=", false, true, true, KBArticle::getResourcePrimKey),
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new FinderColumn<>(
					"kbArticle.", "latest", FinderColumn.Type.BOOLEAN, "=",
					true, true, KBArticle::isLatest),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByR_G_M_NotS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_G_M_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"resourcePrimKey", "groupId", "main", "status"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByR_G_M_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {
						"resourcePrimKey", "groupId", "main", "status"
					},
					false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new ArrayableFinderColumn<>(
					"kbArticle.", "resourcePrimKey", FinderColumn.Type.LONG,
					"=", false, true, true, KBArticle::getResourcePrimKey),
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new FinderColumn<>(
					"kbArticle.", "main", FinderColumn.Type.BOOLEAN, "=", true,
					true, KBArticle::isMain),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByG_P_L_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_L_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "parentResourcePrimKey", "latest", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_L_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "parentResourcePrimKey", "latest", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_L_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "parentResourcePrimKey", "latest", "status"
					},
					false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new ArrayableFinderColumn<>(
					"kbArticle.", "parentResourcePrimKey",
					FinderColumn.Type.LONG, "=", false, true, true,
					KBArticle::getParentResourcePrimKey),
				new FinderColumn<>(
					"kbArticle.", "latest", FinderColumn.Type.BOOLEAN, "=",
					true, true, KBArticle::isLatest),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByG_P_L_NotS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_L_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "parentResourcePrimKey", "latest", "status"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_L_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "parentResourcePrimKey", "latest", "status"
					},
					false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new ArrayableFinderColumn<>(
					"kbArticle.", "parentResourcePrimKey",
					FinderColumn.Type.LONG, "=", false, true, true,
					KBArticle::getParentResourcePrimKey),
				new FinderColumn<>(
					"kbArticle.", "latest", FinderColumn.Type.BOOLEAN, "=",
					true, true, KBArticle::isLatest),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByG_P_M_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_M_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "parentResourcePrimKey", "main", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_M_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "parentResourcePrimKey", "main", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_M_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "parentResourcePrimKey", "main", "status"
					},
					false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new ArrayableFinderColumn<>(
					"kbArticle.", "parentResourcePrimKey",
					FinderColumn.Type.LONG, "=", false, true, true,
					KBArticle::getParentResourcePrimKey),
				new FinderColumn<>(
					"kbArticle.", "main", FinderColumn.Type.BOOLEAN, "=", true,
					true, KBArticle::isMain),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByG_P_M_NotS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_M_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "parentResourcePrimKey", "main", "status"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_M_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "parentResourcePrimKey", "main", "status"
					},
					false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new ArrayableFinderColumn<>(
					"kbArticle.", "parentResourcePrimKey",
					FinderColumn.Type.LONG, "=", false, true, true,
					KBArticle::getParentResourcePrimKey),
				new FinderColumn<>(
					"kbArticle.", "main", FinderColumn.Type.BOOLEAN, "=", true,
					true, KBArticle::isMain),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByG_KBFI_UT_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_KBFI_UT_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "kbFolderId", "urlTitle", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_KBFI_UT_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "kbFolderId", "urlTitle", "status"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_KBFI_UT_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "kbFolderId", "urlTitle", "status"
					},
					0, 4, false, null),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new FinderColumn<>(
					"kbArticle.", "kbFolderId", FinderColumn.Type.LONG, "=",
					true, true, KBArticle::getKbFolderId),
				new FinderColumn<>(
					"kbArticle.", "urlTitle", FinderColumn.Type.STRING, "=",
					true, true, KBArticle::getUrlTitle),
				new ArrayableFinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "=",
					false, true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByG_KBFI_UT_NotS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_KBFI_UT_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "kbFolderId", "urlTitle", "status"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_KBFI_UT_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "kbFolderId", "urlTitle", "status"
					},
					false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new FinderColumn<>(
					"kbArticle.", "kbFolderId", FinderColumn.Type.LONG, "=",
					true, true, KBArticle::getKbFolderId),
				new FinderColumn<>(
					"kbArticle.", "urlTitle", FinderColumn.Type.STRING, "=",
					true, true, KBArticle::getUrlTitle),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByG_KBFI_L_NotS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_KBFI_L_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "kbFolderId", "latest", "status"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_KBFI_L_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "kbFolderId", "latest", "status"},
					false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new FinderColumn<>(
					"kbArticle.", "kbFolderId", FinderColumn.Type.LONG, "=",
					true, true, KBArticle::getKbFolderId),
				new FinderColumn<>(
					"kbArticle.", "latest", FinderColumn.Type.BOOLEAN, "=",
					true, true, KBArticle::isLatest),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByG_LikeS_L_NotS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_LikeS_L_NotS",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "sections", "latest", "status"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_LikeS_L_NotS",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "sections", "latest", "status"},
					false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new ArrayableFinderColumn<>(
					"kbArticle.", "sections", FinderColumn.Type.STRING, "LIKE",
					false, true, true, KBArticle::getSections),
				new FinderColumn<>(
					"kbArticle.", "latest", FinderColumn.Type.BOOLEAN, "=",
					true, true, KBArticle::isLatest),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, KBArticle::getStatus));

		_collectionPersistenceFinderByG_LikeS_M_NotS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_LikeS_M_NotS",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "sections", "main", "status"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_LikeS_M_NotS",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "sections", "main", "status"},
					false),
				_SQL_SELECT_KBARTICLE_WHERE, _SQL_COUNT_KBARTICLE_WHERE,
				KBArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBArticle::getGroupId),
				new ArrayableFinderColumn<>(
					"kbArticle.", "sections", FinderColumn.Type.STRING, "LIKE",
					false, true, true, KBArticle::getSections),
				new FinderColumn<>(
					"kbArticle.", "main", FinderColumn.Type.BOOLEAN, "=", true,
					true, KBArticle::isMain),
				new FinderColumn<>(
					"kbArticle.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, KBArticle::getStatus));

		KBArticleUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KBArticleUtil.setPersistence(null);

		entityCache.removeCache(KBArticleImpl.class.getName());
	}

	@Override
	@Reference(
		target = KBPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = KBPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = KBPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		KBArticleModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KBARTICLE =
		"SELECT kbArticle FROM KBArticle kbArticle";

	private static final String _SQL_SELECT_KBARTICLE_WHERE =
		"SELECT kbArticle FROM KBArticle kbArticle WHERE ";

	private static final String _SQL_COUNT_KBARTICLE_WHERE =
		"SELECT COUNT(kbArticle) FROM KBArticle kbArticle WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1245179503