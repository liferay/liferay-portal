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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.wiki.exception.NoSuchPageException;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageTable;
import com.liferay.wiki.model.impl.WikiPageImpl;
import com.liferay.wiki.model.impl.WikiPageModelImpl;
import com.liferay.wiki.service.persistence.WikiPagePersistence;
import com.liferay.wiki.service.persistence.WikiPageUtil;
import com.liferay.wiki.service.persistence.impl.constants.WikiPersistenceConstants;

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
 * The persistence implementation for the wiki page service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = WikiPagePersistence.class)
public class WikiPagePersistenceImpl
	extends BasePersistenceImpl<WikiPage, NoSuchPageException>
	implements WikiPagePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>WikiPageUtil</code> to access the wiki page persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		WikiPageImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByResourcePrimKey;

	/**
	 * Returns an ordered range of all the wiki pages where resourcePrimKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByResourcePrimKey(
		long resourcePrimKey, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByResourcePrimKey.find(
			finderCache, new Object[] {resourcePrimKey}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByResourcePrimKey_First(
			long resourcePrimKey, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByResourcePrimKey.findFirst(
			finderCache, new Object[] {resourcePrimKey}, orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByResourcePrimKey_First(
		long resourcePrimKey, OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByResourcePrimKey.fetchFirst(
			finderCache, new Object[] {resourcePrimKey}, orderByComparator);
	}

	/**
	 * Removes all the wiki pages where resourcePrimKey = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 */
	@Override
	public void removeByResourcePrimKey(long resourcePrimKey) {
		_collectionPersistenceFinderByResourcePrimKey.remove(
			finderCache, new Object[] {resourcePrimKey});
	}

	/**
	 * Returns the number of wiki pages where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByResourcePrimKey(long resourcePrimKey) {
		return _collectionPersistenceFinderByResourcePrimKey.count(
			finderCache, new Object[] {resourcePrimKey});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the wiki pages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByUuid_First(
			String uuid, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByUuid_First(
		String uuid, OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the wiki pages where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of wiki pages where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<WikiPage, NoSuchPageException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the wiki page where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPageException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByUUID_G(String uuid, long groupId)
		throws NoSuchPageException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the wiki page where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the wiki page where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the wiki page that was removed
	 */
	@Override
	public WikiPage removeByUUID_G(String uuid, long groupId)
		throws NoSuchPageException {

		WikiPage wikiPage = findByUUID_G(uuid, groupId);

		return remove(wikiPage);
	}

	/**
	 * Returns the number of wiki pages where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the wiki pages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the wiki pages where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of wiki pages where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the wiki pages where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByCompanyId_First(
			long companyId, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByCompanyId_First(
		long companyId, OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the wiki pages where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of wiki pages where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByNodeId;

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByNodeId(
		long nodeId, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByNodeId.find(
			finderCache, new Object[] {nodeId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByNodeId_First(
			long nodeId, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByNodeId.findFirst(
			finderCache, new Object[] {nodeId}, orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByNodeId_First(
		long nodeId, OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByNodeId.fetchFirst(
			finderCache, new Object[] {nodeId}, orderByComparator);
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 */
	@Override
	public void removeByNodeId(long nodeId) {
		_collectionPersistenceFinderByNodeId.remove(
			finderCache, new Object[] {nodeId});
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63;.
	 *
	 * @param nodeId the node ID
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByNodeId(long nodeId) {
		return _collectionPersistenceFinderByNodeId.count(
			finderCache, new Object[] {nodeId});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByFormat;

	/**
	 * Returns an ordered range of all the wiki pages where format = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param format the format
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByFormat(
		String format, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByFormat.find(
			finderCache, new Object[] {format}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where format = &#63;.
	 *
	 * @param format the format
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByFormat_First(
			String format, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByFormat.findFirst(
			finderCache, new Object[] {format}, orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where format = &#63;.
	 *
	 * @param format the format
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByFormat_First(
		String format, OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByFormat.fetchFirst(
			finderCache, new Object[] {format}, orderByComparator);
	}

	/**
	 * Removes all the wiki pages where format = &#63; from the database.
	 *
	 * @param format the format
	 */
	@Override
	public void removeByFormat(String format) {
		_collectionPersistenceFinderByFormat.remove(
			finderCache, new Object[] {format});
	}

	/**
	 * Returns the number of wiki pages where format = &#63;.
	 *
	 * @param format the format
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByFormat(String format) {
		return _collectionPersistenceFinderByFormat.count(
			finderCache, new Object[] {format});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByR_N;

	/**
	 * Returns an ordered range of all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_N(
		long resourcePrimKey, long nodeId, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByR_N.find(
			finderCache, new Object[] {resourcePrimKey, nodeId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByR_N_First(
			long resourcePrimKey, long nodeId,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByR_N.findFirst(
			finderCache, new Object[] {resourcePrimKey, nodeId},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByR_N_First(
		long resourcePrimKey, long nodeId,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByR_N.fetchFirst(
			finderCache, new Object[] {resourcePrimKey, nodeId},
			orderByComparator);
	}

	/**
	 * Removes all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 */
	@Override
	public void removeByR_N(long resourcePrimKey, long nodeId) {
		_collectionPersistenceFinderByR_N.remove(
			finderCache, new Object[] {resourcePrimKey, nodeId});
	}

	/**
	 * Returns the number of wiki pages where resourcePrimKey = &#63; and nodeId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByR_N(long resourcePrimKey, long nodeId) {
		return _collectionPersistenceFinderByR_N.count(
			finderCache, new Object[] {resourcePrimKey, nodeId});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByR_S;

	/**
	 * Returns an ordered range of all the wiki pages where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_S(
		long resourcePrimKey, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByR_S.find(
			finderCache, new Object[] {resourcePrimKey, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByR_S_First(
			long resourcePrimKey, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByR_S.findFirst(
			finderCache, new Object[] {resourcePrimKey, status},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByR_S_First(
		long resourcePrimKey, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByR_S.fetchFirst(
			finderCache, new Object[] {resourcePrimKey, status},
			orderByComparator);
	}

	/**
	 * Removes all the wiki pages where resourcePrimKey = &#63; and status = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 */
	@Override
	public void removeByR_S(long resourcePrimKey, int status) {
		_collectionPersistenceFinderByR_S.remove(
			finderCache, new Object[] {resourcePrimKey, status});
	}

	/**
	 * Returns the number of wiki pages where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByR_S(long resourcePrimKey, int status) {
		return _collectionPersistenceFinderByR_S.count(
			finderCache, new Object[] {resourcePrimKey, status});
	}

	private FilterCollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByG_ERC;

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_ERC(
		long groupId, String externalReferenceCode, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_ERC.find(
			finderCache, new Object[] {groupId, externalReferenceCode}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_ERC_First(
			long groupId, String externalReferenceCode,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByG_ERC.findFirst(
			finderCache, new Object[] {groupId, externalReferenceCode},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_ERC_First(
		long groupId, String externalReferenceCode,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByG_ERC.fetchFirst(
			finderCache, new Object[] {groupId, externalReferenceCode},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the wiki pages that the user has permissions to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_ERC(
		long groupId, String externalReferenceCode, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByG_ERC.filterFind(
			finderCache, new Object[] {groupId, externalReferenceCode}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the wiki pages where groupId = &#63; and externalReferenceCode = &#63; from the database.
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
	 * Returns the number of wiki pages where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByG_ERC(long groupId, String externalReferenceCode) {
		return _collectionPersistenceFinderByG_ERC.count(
			finderCache, new Object[] {groupId, externalReferenceCode});
	}

	/**
	 * Returns the number of wiki pages that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the number of matching wiki pages that the user has permission to view
	 */
	@Override
	public int filterCountByG_ERC(long groupId, String externalReferenceCode) {
		return _collectionPersistenceFinderByG_ERC.filterCount(
			finderCache, new Object[] {groupId, externalReferenceCode},
			groupId);
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByN_T;

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and title = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_T(
		long nodeId, String title, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByN_T.find(
			finderCache, new Object[] {nodeId, title}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and title = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_T_First(
			long nodeId, String title,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByN_T.findFirst(
			finderCache, new Object[] {nodeId, title}, orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and title = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_T_First(
		long nodeId, String title,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByN_T.fetchFirst(
			finderCache, new Object[] {nodeId, title}, orderByComparator);
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and title = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 */
	@Override
	public void removeByN_T(long nodeId, String title) {
		_collectionPersistenceFinderByN_T.remove(
			finderCache, new Object[] {nodeId, title});
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and title = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_T(long nodeId, String title) {
		return _collectionPersistenceFinderByN_T.count(
			finderCache, new Object[] {nodeId, title});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByN_H;

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H(
		long nodeId, boolean head, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByN_H.find(
			finderCache, new Object[] {nodeId, head}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_First(
			long nodeId, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByN_H.findFirst(
			finderCache, new Object[] {nodeId, head}, orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_First(
		long nodeId, boolean head,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByN_H.fetchFirst(
			finderCache, new Object[] {nodeId, head}, orderByComparator);
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and head = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 */
	@Override
	public void removeByN_H(long nodeId, boolean head) {
		_collectionPersistenceFinderByN_H.remove(
			finderCache, new Object[] {nodeId, head});
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and head = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_H(long nodeId, boolean head) {
		return _collectionPersistenceFinderByN_H.count(
			finderCache, new Object[] {nodeId, head});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByN_P;

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and parentTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param parentTitle the parent title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_P(
		long nodeId, String parentTitle, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByN_P.find(
			finderCache, new Object[] {nodeId, parentTitle}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and parentTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param parentTitle the parent title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_P_First(
			long nodeId, String parentTitle,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByN_P.findFirst(
			finderCache, new Object[] {nodeId, parentTitle}, orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and parentTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param parentTitle the parent title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_P_First(
		long nodeId, String parentTitle,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByN_P.fetchFirst(
			finderCache, new Object[] {nodeId, parentTitle}, orderByComparator);
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and parentTitle = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param parentTitle the parent title
	 */
	@Override
	public void removeByN_P(long nodeId, String parentTitle) {
		_collectionPersistenceFinderByN_P.remove(
			finderCache, new Object[] {nodeId, parentTitle});
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and parentTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param parentTitle the parent title
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_P(long nodeId, String parentTitle) {
		return _collectionPersistenceFinderByN_P.count(
			finderCache, new Object[] {nodeId, parentTitle});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByN_R;

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and redirectTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param redirectTitle the redirect title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_R(
		long nodeId, String redirectTitle, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByN_R.find(
			finderCache, new Object[] {nodeId, redirectTitle}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and redirectTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param redirectTitle the redirect title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_R_First(
			long nodeId, String redirectTitle,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByN_R.findFirst(
			finderCache, new Object[] {nodeId, redirectTitle},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and redirectTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param redirectTitle the redirect title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_R_First(
		long nodeId, String redirectTitle,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByN_R.fetchFirst(
			finderCache, new Object[] {nodeId, redirectTitle},
			orderByComparator);
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and redirectTitle = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param redirectTitle the redirect title
	 */
	@Override
	public void removeByN_R(long nodeId, String redirectTitle) {
		_collectionPersistenceFinderByN_R.remove(
			finderCache, new Object[] {nodeId, redirectTitle});
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and redirectTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param redirectTitle the redirect title
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_R(long nodeId, String redirectTitle) {
		return _collectionPersistenceFinderByN_R.count(
			finderCache, new Object[] {nodeId, redirectTitle});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByN_S;

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_S(
		long nodeId, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByN_S.find(
			finderCache, new Object[] {nodeId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_S_First(
			long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByN_S.findFirst(
			finderCache, new Object[] {nodeId, status}, orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_S_First(
		long nodeId, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByN_S.fetchFirst(
			finderCache, new Object[] {nodeId, status}, orderByComparator);
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and status = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param status the status
	 */
	@Override
	public void removeByN_S(long nodeId, int status) {
		_collectionPersistenceFinderByN_S.remove(
			finderCache, new Object[] {nodeId, status});
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_S(long nodeId, int status) {
		return _collectionPersistenceFinderByN_S.count(
			finderCache, new Object[] {nodeId, status});
	}

	private UniquePersistenceFinder<WikiPage, NoSuchPageException>
		_uniquePersistenceFinderByR_N_V;

	/**
	 * Returns the wiki page where resourcePrimKey = &#63; and nodeId = &#63; and version = &#63; or throws a <code>NoSuchPageException</code> if it could not be found.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param version the version
	 * @return the matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByR_N_V(
			long resourcePrimKey, long nodeId, double version)
		throws NoSuchPageException {

		return _uniquePersistenceFinderByR_N_V.find(
			finderCache, new Object[] {resourcePrimKey, nodeId, version});
	}

	/**
	 * Returns the wiki page where resourcePrimKey = &#63; and nodeId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByR_N_V(
		long resourcePrimKey, long nodeId, double version,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByR_N_V.fetch(
			finderCache, new Object[] {resourcePrimKey, nodeId, version},
			useFinderCache);
	}

	/**
	 * Removes the wiki page where resourcePrimKey = &#63; and nodeId = &#63; and version = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param version the version
	 * @return the wiki page that was removed
	 */
	@Override
	public WikiPage removeByR_N_V(
			long resourcePrimKey, long nodeId, double version)
		throws NoSuchPageException {

		WikiPage wikiPage = findByR_N_V(resourcePrimKey, nodeId, version);

		return remove(wikiPage);
	}

	/**
	 * Returns the number of wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and version = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param version the version
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByR_N_V(long resourcePrimKey, long nodeId, double version) {
		return _uniquePersistenceFinderByR_N_V.count(
			finderCache, new Object[] {resourcePrimKey, nodeId, version});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByR_N_H;

	/**
	 * Returns an ordered range of all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_N_H(
		long resourcePrimKey, long nodeId, boolean head, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByR_N_H.find(
			finderCache, new Object[] {resourcePrimKey, nodeId, head}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByR_N_H_First(
			long resourcePrimKey, long nodeId, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByR_N_H.findFirst(
			finderCache, new Object[] {resourcePrimKey, nodeId, head},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByR_N_H_First(
		long resourcePrimKey, long nodeId, boolean head,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByR_N_H.fetchFirst(
			finderCache, new Object[] {resourcePrimKey, nodeId, head},
			orderByComparator);
	}

	/**
	 * Removes all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and head = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param head the head
	 */
	@Override
	public void removeByR_N_H(long resourcePrimKey, long nodeId, boolean head) {
		_collectionPersistenceFinderByR_N_H.remove(
			finderCache, new Object[] {resourcePrimKey, nodeId, head});
	}

	/**
	 * Returns the number of wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param head the head
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByR_N_H(long resourcePrimKey, long nodeId, boolean head) {
		return _collectionPersistenceFinderByR_N_H.count(
			finderCache, new Object[] {resourcePrimKey, nodeId, head});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByR_N_S;

	/**
	 * Returns an ordered range of all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_N_S(
		long resourcePrimKey, long nodeId, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByR_N_S.find(
			finderCache, new Object[] {resourcePrimKey, nodeId, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByR_N_S_First(
			long resourcePrimKey, long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByR_N_S.findFirst(
			finderCache, new Object[] {resourcePrimKey, nodeId, status},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByR_N_S_First(
		long resourcePrimKey, long nodeId, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByR_N_S.fetchFirst(
			finderCache, new Object[] {resourcePrimKey, nodeId, status},
			orderByComparator);
	}

	/**
	 * Removes all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and status = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param status the status
	 */
	@Override
	public void removeByR_N_S(long resourcePrimKey, long nodeId, int status) {
		_collectionPersistenceFinderByR_N_S.remove(
			finderCache, new Object[] {resourcePrimKey, nodeId, status});
	}

	/**
	 * Returns the number of wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByR_N_S(long resourcePrimKey, long nodeId, int status) {
		return _collectionPersistenceFinderByR_N_S.count(
			finderCache, new Object[] {resourcePrimKey, nodeId, status});
	}

	private UniquePersistenceFinder<WikiPage, NoSuchPageException>
		_uniquePersistenceFinderByG_ERC_V;

	/**
	 * Returns the wiki page where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; or throws a <code>NoSuchPageException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_ERC_V(
			long groupId, String externalReferenceCode, double version)
		throws NoSuchPageException {

		return _uniquePersistenceFinderByG_ERC_V.find(
			finderCache,
			new Object[] {groupId, externalReferenceCode, version});
	}

	/**
	 * Returns the wiki page where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_ERC_V(
		long groupId, String externalReferenceCode, double version,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_ERC_V.fetch(
			finderCache, new Object[] {groupId, externalReferenceCode, version},
			useFinderCache);
	}

	/**
	 * Removes the wiki page where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the wiki page that was removed
	 */
	@Override
	public WikiPage removeByG_ERC_V(
			long groupId, String externalReferenceCode, double version)
		throws NoSuchPageException {

		WikiPage wikiPage = findByG_ERC_V(
			groupId, externalReferenceCode, version);

		return remove(wikiPage);
	}

	/**
	 * Returns the number of wiki pages where groupId = &#63; and externalReferenceCode = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByG_ERC_V(
		long groupId, String externalReferenceCode, double version) {

		return _uniquePersistenceFinderByG_ERC_V.count(
			finderCache,
			new Object[] {groupId, externalReferenceCode, version});
	}

	private FilterCollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByG_N_H;

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_H(
		long groupId, long nodeId, boolean head, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_N_H.find(
			finderCache, new Object[] {groupId, nodeId, head}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_N_H_First(
			long groupId, long nodeId, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByG_N_H.findFirst(
			finderCache, new Object[] {groupId, nodeId, head},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_N_H_First(
		long groupId, long nodeId, boolean head,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByG_N_H.fetchFirst(
			finderCache, new Object[] {groupId, nodeId, head},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the wiki pages that the user has permissions to view where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_H(
		long groupId, long nodeId, boolean head, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByG_N_H.filterFind(
			finderCache, new Object[] {groupId, nodeId, head}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 */
	@Override
	public void removeByG_N_H(long groupId, long nodeId, boolean head) {
		_collectionPersistenceFinderByG_N_H.remove(
			finderCache, new Object[] {groupId, nodeId, head});
	}

	/**
	 * Returns the number of wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByG_N_H(long groupId, long nodeId, boolean head) {
		return _collectionPersistenceFinderByG_N_H.count(
			finderCache, new Object[] {groupId, nodeId, head});
	}

	/**
	 * Returns the number of wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @return the number of matching wiki pages that the user has permission to view
	 */
	@Override
	public int filterCountByG_N_H(long groupId, long nodeId, boolean head) {
		return _collectionPersistenceFinderByG_N_H.filterCount(
			finderCache, new Object[] {groupId, nodeId, head}, groupId);
	}

	private FilterCollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByG_N_S;

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_S(
		long groupId, long nodeId, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_N_S.find(
			finderCache, new Object[] {groupId, nodeId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_N_S_First(
			long groupId, long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByG_N_S.findFirst(
			finderCache, new Object[] {groupId, nodeId, status},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_N_S_First(
		long groupId, long nodeId, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByG_N_S.fetchFirst(
			finderCache, new Object[] {groupId, nodeId, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the wiki pages that the user has permissions to view where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_S(
		long groupId, long nodeId, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByG_N_S.filterFind(
			finderCache, new Object[] {groupId, nodeId, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the wiki pages where groupId = &#63; and nodeId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 */
	@Override
	public void removeByG_N_S(long groupId, long nodeId, int status) {
		_collectionPersistenceFinderByG_N_S.remove(
			finderCache, new Object[] {groupId, nodeId, status});
	}

	/**
	 * Returns the number of wiki pages where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByG_N_S(long groupId, long nodeId, int status) {
		return _collectionPersistenceFinderByG_N_S.count(
			finderCache, new Object[] {groupId, nodeId, status});
	}

	/**
	 * Returns the number of wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the number of matching wiki pages that the user has permission to view
	 */
	@Override
	public int filterCountByG_N_S(long groupId, long nodeId, int status) {
		return _collectionPersistenceFinderByG_N_S.filterCount(
			finderCache, new Object[] {groupId, nodeId, status}, groupId);
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByU_N_S;

	/**
	 * Returns an ordered range of all the wiki pages where userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByU_N_S(
		long userId, long nodeId, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByU_N_S.find(
			finderCache, new Object[] {userId, nodeId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByU_N_S_First(
			long userId, long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByU_N_S.findFirst(
			finderCache, new Object[] {userId, nodeId, status},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByU_N_S_First(
		long userId, long nodeId, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByU_N_S.fetchFirst(
			finderCache, new Object[] {userId, nodeId, status},
			orderByComparator);
	}

	/**
	 * Removes all the wiki pages where userId = &#63; and nodeId = &#63; and status = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 */
	@Override
	public void removeByU_N_S(long userId, long nodeId, int status) {
		_collectionPersistenceFinderByU_N_S.remove(
			finderCache, new Object[] {userId, nodeId, status});
	}

	/**
	 * Returns the number of wiki pages where userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByU_N_S(long userId, long nodeId, int status) {
		return _collectionPersistenceFinderByU_N_S.count(
			finderCache, new Object[] {userId, nodeId, status});
	}

	private UniquePersistenceFinder<WikiPage, NoSuchPageException>
		_uniquePersistenceFinderByN_T_V;

	/**
	 * Returns the wiki page where nodeId = &#63; and title = &#63; and version = &#63; or throws a <code>NoSuchPageException</code> if it could not be found.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param version the version
	 * @return the matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_T_V(long nodeId, String title, double version)
		throws NoSuchPageException {

		return _uniquePersistenceFinderByN_T_V.find(
			finderCache, new Object[] {nodeId, title, version});
	}

	/**
	 * Returns the wiki page where nodeId = &#63; and title = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_T_V(
		long nodeId, String title, double version, boolean useFinderCache) {

		return _uniquePersistenceFinderByN_T_V.fetch(
			finderCache, new Object[] {nodeId, title, version}, useFinderCache);
	}

	/**
	 * Removes the wiki page where nodeId = &#63; and title = &#63; and version = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param version the version
	 * @return the wiki page that was removed
	 */
	@Override
	public WikiPage removeByN_T_V(long nodeId, String title, double version)
		throws NoSuchPageException {

		WikiPage wikiPage = findByN_T_V(nodeId, title, version);

		return remove(wikiPage);
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and title = &#63; and version = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param version the version
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_T_V(long nodeId, String title, double version) {
		return _uniquePersistenceFinderByN_T_V.count(
			finderCache, new Object[] {nodeId, title, version});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByN_T_H;

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_T_H(
		long nodeId, String title, boolean head, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByN_T_H.find(
			finderCache, new Object[] {nodeId, title, head}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_T_H_First(
			long nodeId, String title, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByN_T_H.findFirst(
			finderCache, new Object[] {nodeId, title, head}, orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_T_H_First(
		long nodeId, String title, boolean head,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByN_T_H.fetchFirst(
			finderCache, new Object[] {nodeId, title, head}, orderByComparator);
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and title = &#63; and head = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 */
	@Override
	public void removeByN_T_H(long nodeId, String title, boolean head) {
		_collectionPersistenceFinderByN_T_H.remove(
			finderCache, new Object[] {nodeId, title, head});
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_T_H(long nodeId, String title, boolean head) {
		return _collectionPersistenceFinderByN_T_H.count(
			finderCache, new Object[] {nodeId, title, head});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByN_T_S;

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and title = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_T_S(
		long nodeId, String title, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByN_T_S.find(
			finderCache, new Object[] {nodeId, title, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and title = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_T_S_First(
			long nodeId, String title, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByN_T_S.findFirst(
			finderCache, new Object[] {nodeId, title, status},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and title = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_T_S_First(
		long nodeId, String title, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByN_T_S.fetchFirst(
			finderCache, new Object[] {nodeId, title, status},
			orderByComparator);
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and title = &#63; and status = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param status the status
	 */
	@Override
	public void removeByN_T_S(long nodeId, String title, int status) {
		_collectionPersistenceFinderByN_T_S.remove(
			finderCache, new Object[] {nodeId, title, status});
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and title = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_T_S(long nodeId, String title, int status) {
		return _collectionPersistenceFinderByN_T_S.count(
			finderCache, new Object[] {nodeId, title, status});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByN_H_P;

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_P(
		long nodeId, boolean head, String parentTitle, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByN_H_P.find(
			finderCache, new Object[] {nodeId, head, parentTitle}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_P_First(
			long nodeId, boolean head, String parentTitle,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByN_H_P.findFirst(
			finderCache, new Object[] {nodeId, head, parentTitle},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_P_First(
		long nodeId, boolean head, String parentTitle,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByN_H_P.fetchFirst(
			finderCache, new Object[] {nodeId, head, parentTitle},
			orderByComparator);
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 */
	@Override
	public void removeByN_H_P(long nodeId, boolean head, String parentTitle) {
		_collectionPersistenceFinderByN_H_P.remove(
			finderCache, new Object[] {nodeId, head, parentTitle});
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_H_P(long nodeId, boolean head, String parentTitle) {
		return _collectionPersistenceFinderByN_H_P.count(
			finderCache, new Object[] {nodeId, head, parentTitle});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByN_H_R;

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_R(
		long nodeId, boolean head, String redirectTitle, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByN_H_R.find(
			finderCache, new Object[] {nodeId, head, redirectTitle}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_R_First(
			long nodeId, boolean head, String redirectTitle,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByN_H_R.findFirst(
			finderCache, new Object[] {nodeId, head, redirectTitle},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_R_First(
		long nodeId, boolean head, String redirectTitle,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByN_H_R.fetchFirst(
			finderCache, new Object[] {nodeId, head, redirectTitle},
			orderByComparator);
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 */
	@Override
	public void removeByN_H_R(long nodeId, boolean head, String redirectTitle) {
		_collectionPersistenceFinderByN_H_R.remove(
			finderCache, new Object[] {nodeId, head, redirectTitle});
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_H_R(long nodeId, boolean head, String redirectTitle) {
		return _collectionPersistenceFinderByN_H_R.count(
			finderCache, new Object[] {nodeId, head, redirectTitle});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByN_H_S;

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_S(
		long nodeId, boolean head, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByN_H_S.find(
			finderCache, new Object[] {nodeId, head, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_S_First(
			long nodeId, boolean head, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByN_H_S.findFirst(
			finderCache, new Object[] {nodeId, head, status},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_S_First(
		long nodeId, boolean head, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByN_H_S.fetchFirst(
			finderCache, new Object[] {nodeId, head, status},
			orderByComparator);
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and head = &#63; and status = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 */
	@Override
	public void removeByN_H_S(long nodeId, boolean head, int status) {
		_collectionPersistenceFinderByN_H_S.remove(
			finderCache, new Object[] {nodeId, head, status});
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_H_S(long nodeId, boolean head, int status) {
		return _collectionPersistenceFinderByN_H_S.count(
			finderCache, new Object[] {nodeId, head, status});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByN_H_NotS;

	/**
	 * Returns all the wiki pages where nodeId = &#63; and head = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_NotS(
		long nodeId, boolean head, int status) {

		return findByN_H_NotS(
			nodeId, head, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where nodeId = &#63; and head = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_NotS(
		long nodeId, boolean head, int status, int start, int end) {

		return findByN_H_NotS(nodeId, head, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_NotS(
		long nodeId, boolean head, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByN_H_NotS(
			nodeId, head, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_NotS(
		long nodeId, boolean head, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByN_H_NotS.find(
			finderCache, new Object[] {nodeId, head, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_NotS_First(
			long nodeId, boolean head, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByN_H_NotS.findFirst(
			finderCache, new Object[] {nodeId, head, status},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_NotS_First(
		long nodeId, boolean head, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByN_H_NotS.fetchFirst(
			finderCache, new Object[] {nodeId, head, status},
			orderByComparator);
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and head = &#63; and status &ne; &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 */
	@Override
	public void removeByN_H_NotS(long nodeId, boolean head, int status) {
		_collectionPersistenceFinderByN_H_NotS.remove(
			finderCache, new Object[] {nodeId, head, status});
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and head = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_H_NotS(long nodeId, boolean head, int status) {
		return _collectionPersistenceFinderByN_H_NotS.count(
			finderCache, new Object[] {nodeId, head, status});
	}

	private FilterCollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByG_U_N_S;

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_U_N_S(
		long groupId, long userId, long nodeId, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U_N_S.find(
			finderCache, new Object[] {groupId, userId, nodeId, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_U_N_S_First(
			long groupId, long userId, long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByG_U_N_S.findFirst(
			finderCache, new Object[] {groupId, userId, nodeId, status},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_U_N_S_First(
		long groupId, long userId, long nodeId, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByG_U_N_S.fetchFirst(
			finderCache, new Object[] {groupId, userId, nodeId, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the wiki pages that the user has permissions to view where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_U_N_S(
		long groupId, long userId, long nodeId, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByG_U_N_S.filterFind(
			finderCache, new Object[] {groupId, userId, nodeId, status}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the wiki pages where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 */
	@Override
	public void removeByG_U_N_S(
		long groupId, long userId, long nodeId, int status) {

		_collectionPersistenceFinderByG_U_N_S.remove(
			finderCache, new Object[] {groupId, userId, nodeId, status});
	}

	/**
	 * Returns the number of wiki pages where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByG_U_N_S(
		long groupId, long userId, long nodeId, int status) {

		return _collectionPersistenceFinderByG_U_N_S.count(
			finderCache, new Object[] {groupId, userId, nodeId, status});
	}

	/**
	 * Returns the number of wiki pages that the user has permission to view where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the number of matching wiki pages that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_N_S(
		long groupId, long userId, long nodeId, int status) {

		return _collectionPersistenceFinderByG_U_N_S.filterCount(
			finderCache, new Object[] {groupId, userId, nodeId, status},
			groupId);
	}

	private FilterCollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByG_N_T_H;

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_T_H(
		long groupId, long nodeId, String title, boolean head, int start,
		int end, OrderByComparator<WikiPage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_N_T_H.find(
			finderCache, new Object[] {groupId, nodeId, title, head}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_N_T_H_First(
			long groupId, long nodeId, String title, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByG_N_T_H.findFirst(
			finderCache, new Object[] {groupId, nodeId, title, head},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_N_T_H_First(
		long groupId, long nodeId, String title, boolean head,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByG_N_T_H.fetchFirst(
			finderCache, new Object[] {groupId, nodeId, title, head},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the wiki pages that the user has permissions to view where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_T_H(
		long groupId, long nodeId, String title, boolean head, int start,
		int end, OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByG_N_T_H.filterFind(
			finderCache, new Object[] {groupId, nodeId, title, head}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the wiki pages where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 */
	@Override
	public void removeByG_N_T_H(
		long groupId, long nodeId, String title, boolean head) {

		_collectionPersistenceFinderByG_N_T_H.remove(
			finderCache, new Object[] {groupId, nodeId, title, head});
	}

	/**
	 * Returns the number of wiki pages where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByG_N_T_H(
		long groupId, long nodeId, String title, boolean head) {

		return _collectionPersistenceFinderByG_N_T_H.count(
			finderCache, new Object[] {groupId, nodeId, title, head});
	}

	/**
	 * Returns the number of wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @return the number of matching wiki pages that the user has permission to view
	 */
	@Override
	public int filterCountByG_N_T_H(
		long groupId, long nodeId, String title, boolean head) {

		return _collectionPersistenceFinderByG_N_T_H.filterCount(
			finderCache, new Object[] {groupId, nodeId, title, head}, groupId);
	}

	private FilterCollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByG_N_H_S;

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_H_S(
		long groupId, long nodeId, boolean head, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_N_H_S.find(
			finderCache, new Object[] {groupId, nodeId, head, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_N_H_S_First(
			long groupId, long nodeId, boolean head, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByG_N_H_S.findFirst(
			finderCache, new Object[] {groupId, nodeId, head, status},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_N_H_S_First(
		long groupId, long nodeId, boolean head, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByG_N_H_S.fetchFirst(
			finderCache, new Object[] {groupId, nodeId, head, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the wiki pages that the user has permissions to view where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_H_S(
		long groupId, long nodeId, boolean head, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByG_N_H_S.filterFind(
			finderCache, new Object[] {groupId, nodeId, head, status}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 */
	@Override
	public void removeByG_N_H_S(
		long groupId, long nodeId, boolean head, int status) {

		_collectionPersistenceFinderByG_N_H_S.remove(
			finderCache, new Object[] {groupId, nodeId, head, status});
	}

	/**
	 * Returns the number of wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByG_N_H_S(
		long groupId, long nodeId, boolean head, int status) {

		return _collectionPersistenceFinderByG_N_H_S.count(
			finderCache, new Object[] {groupId, nodeId, head, status});
	}

	/**
	 * Returns the number of wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @return the number of matching wiki pages that the user has permission to view
	 */
	@Override
	public int filterCountByG_N_H_S(
		long groupId, long nodeId, boolean head, int status) {

		return _collectionPersistenceFinderByG_N_H_S.filterCount(
			finderCache, new Object[] {groupId, nodeId, head, status}, groupId);
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByN_H_P_S;

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_P_S(
		long nodeId, boolean head, String parentTitle, int status, int start,
		int end, OrderByComparator<WikiPage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByN_H_P_S.find(
			finderCache, new Object[] {nodeId, head, parentTitle, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_P_S_First(
			long nodeId, boolean head, String parentTitle, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByN_H_P_S.findFirst(
			finderCache, new Object[] {nodeId, head, parentTitle, status},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_P_S_First(
		long nodeId, boolean head, String parentTitle, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByN_H_P_S.fetchFirst(
			finderCache, new Object[] {nodeId, head, parentTitle, status},
			orderByComparator);
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 */
	@Override
	public void removeByN_H_P_S(
		long nodeId, boolean head, String parentTitle, int status) {

		_collectionPersistenceFinderByN_H_P_S.remove(
			finderCache, new Object[] {nodeId, head, parentTitle, status});
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_H_P_S(
		long nodeId, boolean head, String parentTitle, int status) {

		return _collectionPersistenceFinderByN_H_P_S.count(
			finderCache, new Object[] {nodeId, head, parentTitle, status});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByN_H_P_NotS;

	/**
	 * Returns all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_P_NotS(
		long nodeId, boolean head, String parentTitle, int status) {

		return findByN_H_P_NotS(
			nodeId, head, parentTitle, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_P_NotS(
		long nodeId, boolean head, String parentTitle, int status, int start,
		int end) {

		return findByN_H_P_NotS(
			nodeId, head, parentTitle, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_P_NotS(
		long nodeId, boolean head, String parentTitle, int status, int start,
		int end, OrderByComparator<WikiPage> orderByComparator) {

		return findByN_H_P_NotS(
			nodeId, head, parentTitle, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_P_NotS(
		long nodeId, boolean head, String parentTitle, int status, int start,
		int end, OrderByComparator<WikiPage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByN_H_P_NotS.find(
			finderCache, new Object[] {nodeId, head, parentTitle, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_P_NotS_First(
			long nodeId, boolean head, String parentTitle, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByN_H_P_NotS.findFirst(
			finderCache, new Object[] {nodeId, head, parentTitle, status},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_P_NotS_First(
		long nodeId, boolean head, String parentTitle, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByN_H_P_NotS.fetchFirst(
			finderCache, new Object[] {nodeId, head, parentTitle, status},
			orderByComparator);
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status &ne; &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 */
	@Override
	public void removeByN_H_P_NotS(
		long nodeId, boolean head, String parentTitle, int status) {

		_collectionPersistenceFinderByN_H_P_NotS.remove(
			finderCache, new Object[] {nodeId, head, parentTitle, status});
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_H_P_NotS(
		long nodeId, boolean head, String parentTitle, int status) {

		return _collectionPersistenceFinderByN_H_P_NotS.count(
			finderCache, new Object[] {nodeId, head, parentTitle, status});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByN_H_R_S;

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_R_S(
		long nodeId, boolean head, String redirectTitle, int status, int start,
		int end, OrderByComparator<WikiPage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByN_H_R_S.find(
			finderCache, new Object[] {nodeId, head, redirectTitle, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_R_S_First(
			long nodeId, boolean head, String redirectTitle, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByN_H_R_S.findFirst(
			finderCache, new Object[] {nodeId, head, redirectTitle, status},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_R_S_First(
		long nodeId, boolean head, String redirectTitle, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByN_H_R_S.fetchFirst(
			finderCache, new Object[] {nodeId, head, redirectTitle, status},
			orderByComparator);
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 */
	@Override
	public void removeByN_H_R_S(
		long nodeId, boolean head, String redirectTitle, int status) {

		_collectionPersistenceFinderByN_H_R_S.remove(
			finderCache, new Object[] {nodeId, head, redirectTitle, status});
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_H_R_S(
		long nodeId, boolean head, String redirectTitle, int status) {

		return _collectionPersistenceFinderByN_H_R_S.count(
			finderCache, new Object[] {nodeId, head, redirectTitle, status});
	}

	private CollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByN_H_R_NotS;

	/**
	 * Returns all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_R_NotS(
		long nodeId, boolean head, String redirectTitle, int status) {

		return findByN_H_R_NotS(
			nodeId, head, redirectTitle, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_R_NotS(
		long nodeId, boolean head, String redirectTitle, int status, int start,
		int end) {

		return findByN_H_R_NotS(
			nodeId, head, redirectTitle, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_R_NotS(
		long nodeId, boolean head, String redirectTitle, int status, int start,
		int end, OrderByComparator<WikiPage> orderByComparator) {

		return findByN_H_R_NotS(
			nodeId, head, redirectTitle, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_R_NotS(
		long nodeId, boolean head, String redirectTitle, int status, int start,
		int end, OrderByComparator<WikiPage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByN_H_R_NotS.find(
			finderCache, new Object[] {nodeId, head, redirectTitle, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_R_NotS_First(
			long nodeId, boolean head, String redirectTitle, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByN_H_R_NotS.findFirst(
			finderCache, new Object[] {nodeId, head, redirectTitle, status},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_R_NotS_First(
		long nodeId, boolean head, String redirectTitle, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByN_H_R_NotS.fetchFirst(
			finderCache, new Object[] {nodeId, head, redirectTitle, status},
			orderByComparator);
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status &ne; &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 */
	@Override
	public void removeByN_H_R_NotS(
		long nodeId, boolean head, String redirectTitle, int status) {

		_collectionPersistenceFinderByN_H_R_NotS.remove(
			finderCache, new Object[] {nodeId, head, redirectTitle, status});
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_H_R_NotS(
		long nodeId, boolean head, String redirectTitle, int status) {

		return _collectionPersistenceFinderByN_H_R_NotS.count(
			finderCache, new Object[] {nodeId, head, redirectTitle, status});
	}

	private FilterCollectionPersistenceFinder<WikiPage, NoSuchPageException>
		_collectionPersistenceFinderByG_N_H_P_S;

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_H_P_S(
		long groupId, long nodeId, boolean head, String parentTitle, int status,
		int start, int end, OrderByComparator<WikiPage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_N_H_P_S.find(
			finderCache,
			new Object[] {groupId, nodeId, head, parentTitle, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_N_H_P_S_First(
			long groupId, long nodeId, boolean head, String parentTitle,
			int status, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		return _collectionPersistenceFinderByG_N_H_P_S.findFirst(
			finderCache,
			new Object[] {groupId, nodeId, head, parentTitle, status},
			orderByComparator);
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_N_H_P_S_First(
		long groupId, long nodeId, boolean head, String parentTitle, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByG_N_H_P_S.fetchFirst(
			finderCache,
			new Object[] {groupId, nodeId, head, parentTitle, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the wiki pages that the user has permissions to view where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_H_P_S(
		long groupId, long nodeId, boolean head, String parentTitle, int status,
		int start, int end, OrderByComparator<WikiPage> orderByComparator) {

		return _collectionPersistenceFinderByG_N_H_P_S.filterFind(
			finderCache,
			new Object[] {groupId, nodeId, head, parentTitle, status}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 */
	@Override
	public void removeByG_N_H_P_S(
		long groupId, long nodeId, boolean head, String parentTitle,
		int status) {

		_collectionPersistenceFinderByG_N_H_P_S.remove(
			finderCache,
			new Object[] {groupId, nodeId, head, parentTitle, status});
	}

	/**
	 * Returns the number of wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByG_N_H_P_S(
		long groupId, long nodeId, boolean head, String parentTitle,
		int status) {

		return _collectionPersistenceFinderByG_N_H_P_S.count(
			finderCache,
			new Object[] {groupId, nodeId, head, parentTitle, status});
	}

	/**
	 * Returns the number of wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @return the number of matching wiki pages that the user has permission to view
	 */
	@Override
	public int filterCountByG_N_H_P_S(
		long groupId, long nodeId, boolean head, String parentTitle,
		int status) {

		return _collectionPersistenceFinderByG_N_H_P_S.filterCount(
			finderCache,
			new Object[] {groupId, nodeId, head, parentTitle, status}, groupId);
	}

	public WikiPagePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(WikiPage.class);

		setModelImplClass(WikiPageImpl.class);
		setModelPKClass(long.class);

		setTable(WikiPageTable.INSTANCE);
	}

	/**
	 * Creates a new wiki page with the primary key. Does not add the wiki page to the database.
	 *
	 * @param pageId the primary key for the new wiki page
	 * @return the new wiki page
	 */
	@Override
	public WikiPage create(long pageId) {
		WikiPage wikiPage = new WikiPageImpl();

		wikiPage.setNew(true);
		wikiPage.setPrimaryKey(pageId);

		String uuid = PortalUUIDUtil.generate();

		wikiPage.setUuid(uuid);

		wikiPage.setCompanyId(CompanyThreadLocal.getCompanyId());

		return wikiPage;
	}

	/**
	 * Removes the wiki page with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param pageId the primary key of the wiki page
	 * @return the wiki page that was removed
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage remove(long pageId) throws NoSuchPageException {
		return remove((Serializable)pageId);
	}

	@Override
	protected WikiPage removeImpl(WikiPage wikiPage) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(wikiPage)) {
				wikiPage = (WikiPage)session.get(
					WikiPageImpl.class, wikiPage.getPrimaryKeyObj());
			}

			if ((wikiPage != null) && ctPersistenceHelper.isRemove(wikiPage)) {
				session.delete(wikiPage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (wikiPage != null) {
			clearCache(wikiPage);
		}

		return wikiPage;
	}

	@Override
	public WikiPage updateImpl(WikiPage wikiPage) {
		boolean isNew = wikiPage.isNew();

		if (!(wikiPage instanceof WikiPageModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(wikiPage.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(wikiPage);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in wikiPage proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom WikiPage implementation " +
					wikiPage.getClass());
		}

		WikiPageModelImpl wikiPageModelImpl = (WikiPageModelImpl)wikiPage;

		if (Validator.isNull(wikiPage.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			wikiPage.setUuid(uuid);
		}

		if (Validator.isNull(wikiPage.getExternalReferenceCode())) {
			wikiPage.setExternalReferenceCode(wikiPage.getUuid());
		}
		else {
			if (!Objects.equals(
					wikiPageModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					wikiPage.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = wikiPage.getCompanyId();

					long groupId = wikiPage.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = wikiPage.getPrimaryKey();
					}

					try {
						wikiPage.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								WikiPage.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								wikiPage.getExternalReferenceCode(), null));
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

		if (isNew && (wikiPage.getCreateDate() == null)) {
			if (serviceContext == null) {
				wikiPage.setCreateDate(date);
			}
			else {
				wikiPage.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!wikiPageModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				wikiPage.setModifiedDate(date);
			}
			else {
				wikiPage.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		long userId = GetterUtil.getLong(PrincipalThreadLocal.getName());

		if (userId > 0) {
			long companyId = wikiPage.getCompanyId();

			long groupId = wikiPage.getGroupId();

			long pageId = 0;

			if (!isNew) {
				pageId = wikiPage.getPrimaryKey();
			}

			try {
				wikiPage.setTitle(
					SanitizerUtil.sanitize(
						companyId, groupId, userId, WikiPage.class.getName(),
						pageId, ContentTypes.TEXT_PLAIN, Sanitizer.MODE_ALL,
						wikiPage.getTitle(), null));
			}
			catch (SanitizerException sanitizerException) {
				throw new SystemException(sanitizerException);
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(wikiPage)) {
				if (!isNew) {
					session.evict(
						WikiPageImpl.class, wikiPage.getPrimaryKeyObj());
				}

				session.save(wikiPage);
			}
			else {
				wikiPage = (WikiPage)session.merge(wikiPage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(wikiPage, false);

		if (isNew) {
			wikiPage.setNew(false);
		}

		wikiPage.resetOriginalValues();

		return wikiPage;
	}

	/**
	 * Returns the wiki page with the primary key or throws a <code>NoSuchPageException</code> if it could not be found.
	 *
	 * @param pageId the primary key of the wiki page
	 * @return the wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage findByPrimaryKey(long pageId) throws NoSuchPageException {
		return findByPrimaryKey((Serializable)pageId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the wiki page with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param pageId the primary key of the wiki page
	 * @return the wiki page, or <code>null</code> if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage fetchByPrimaryKey(long pageId) {
		return fetchByPrimaryKey((Serializable)pageId);
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
		return "pageId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_WIKIPAGE;
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
		return WikiPageModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "WikiPage";
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
		ctMergeColumnNames.add("nodeId");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("minorEdit");
		ctMergeColumnNames.add("content");
		ctMergeColumnNames.add("summary");
		ctMergeColumnNames.add("format");
		ctMergeColumnNames.add("head");
		ctMergeColumnNames.add("parentTitle");
		ctMergeColumnNames.add("redirectTitle");
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
			CTColumnResolutionType.PK, Collections.singleton("pageId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"resourcePrimKey", "nodeId", "version"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "externalReferenceCode", "version"});

		_uniqueIndexColumnNames.add(
			new String[] {"nodeId", "title", "version"});
	}

	/**
	 * Initializes the wiki page persistence.
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
				_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
				WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"wikiPage.", "resourcePrimKey", FinderColumn.Type.LONG, "=",
					true, true, WikiPage::getResourcePrimKey));

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
			_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
			WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"wikiPage.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, WikiPage::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(WikiPage::getUuid), WikiPage::getGroupId),
			_SQL_SELECT_WIKIPAGE_WHERE, "",
			new FinderColumn<>(
				"wikiPage.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, WikiPage::getUuid),
			new FinderColumn<>(
				"wikiPage.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				WikiPage::getGroupId));

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
				_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
				WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"wikiPage.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, WikiPage::getUuid),
				new FinderColumn<>(
					"wikiPage.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getCompanyId));

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
				_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
				WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"wikiPage.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getCompanyId));

		_collectionPersistenceFinderByNodeId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByNodeId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"nodeId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByNodeId",
					new String[] {Long.class.getName()},
					new String[] {"nodeId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByNodeId",
					new String[] {Long.class.getName()},
					new String[] {"nodeId"}, false),
				_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
				WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getNodeId));

		_collectionPersistenceFinderByFormat =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByFormat",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"format"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByFormat",
					new String[] {String.class.getName()},
					new String[] {"format"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByFormat",
					new String[] {String.class.getName()},
					new String[] {"format"}, 0, 1, false, null),
				_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
				WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"wikiPage.", "format", FinderColumn.Type.STRING, "=", true,
					true, WikiPage::getFormat));

		_collectionPersistenceFinderByR_N = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_N",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"resourcePrimKey", "nodeId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_N",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"resourcePrimKey", "nodeId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_N",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"resourcePrimKey", "nodeId"}, false),
			_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
			WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"wikiPage.", "resourcePrimKey", FinderColumn.Type.LONG, "=",
				true, true, WikiPage::getResourcePrimKey),
			new FinderColumn<>(
				"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true, true,
				WikiPage::getNodeId));

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
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"resourcePrimKey", "status"}, false),
			_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
			WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"wikiPage.", "resourcePrimKey", FinderColumn.Type.LONG, "=",
				true, true, WikiPage::getResourcePrimKey),
			new FinderColumn<>(
				"wikiPage.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, WikiPage::getStatus));

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
				_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
				WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"wikiPage.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getGroupId),
				new FinderColumn<>(
					"wikiPage.", "externalReferenceCode",
					FinderColumn.Type.STRING, "=", true, true,
					WikiPage::getExternalReferenceCode));

		_collectionPersistenceFinderByN_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_T",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"nodeId", "title"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_T",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"nodeId", "title"}, 2, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_T",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"nodeId", "title"}, 2, 2, false, null),
			_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
			WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true, true,
				WikiPage::getNodeId),
			new FinderColumn<>(
				"wikiPage.", "title", FinderColumn.Type.STRING, "=", false,
				true, WikiPage::getTitle));

		_collectionPersistenceFinderByN_H = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_H",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"nodeId", "head"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_H",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"nodeId", "head"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_H",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"nodeId", "head"}, false),
			_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
			WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true, true,
				WikiPage::getNodeId),
			new FinderColumn<>(
				"wikiPage.", "head", FinderColumn.Type.BOOLEAN, "=", true, true,
				WikiPage::isHead));

		_collectionPersistenceFinderByN_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_P",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"nodeId", "parentTitle"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_P",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"nodeId", "parentTitle"}, 2, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_P",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"nodeId", "parentTitle"}, 2, 2, false, null),
			_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
			WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true, true,
				WikiPage::getNodeId),
			new FinderColumn<>(
				"wikiPage.", "parentTitle", FinderColumn.Type.STRING, "=",
				false, true, WikiPage::getParentTitle));

		_collectionPersistenceFinderByN_R = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_R",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"nodeId", "redirectTitle"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_R",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"nodeId", "redirectTitle"}, 2, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_R",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"nodeId", "redirectTitle"}, 2, 2, false, null),
			_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
			WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true, true,
				WikiPage::getNodeId),
			new FinderColumn<>(
				"wikiPage.", "redirectTitle", FinderColumn.Type.STRING, "=",
				false, true, WikiPage::getRedirectTitle));

		_collectionPersistenceFinderByN_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"nodeId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"nodeId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"nodeId", "status"}, false),
			_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
			WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true, true,
				WikiPage::getNodeId),
			new FinderColumn<>(
				"wikiPage.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, WikiPage::getStatus));

		_uniquePersistenceFinderByR_N_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByR_N_V",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Double.class.getName()
				},
				new String[] {"resourcePrimKey", "nodeId", "version"}, 0, 0,
				false, WikiPage::getResourcePrimKey, WikiPage::getNodeId,
				WikiPage::getVersion),
			_SQL_SELECT_WIKIPAGE_WHERE, "",
			new FinderColumn<>(
				"wikiPage.", "resourcePrimKey", FinderColumn.Type.LONG, "=",
				true, true, WikiPage::getResourcePrimKey),
			new FinderColumn<>(
				"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true, true,
				WikiPage::getNodeId),
			new FinderColumn<>(
				"wikiPage.", "version", FinderColumn.Type.DOUBLE, "=", true,
				true, WikiPage::getVersion));

		_collectionPersistenceFinderByR_N_H = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_N_H",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"resourcePrimKey", "nodeId", "head"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_N_H",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"resourcePrimKey", "nodeId", "head"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_N_H",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"resourcePrimKey", "nodeId", "head"}, false),
			_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
			WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"wikiPage.", "resourcePrimKey", FinderColumn.Type.LONG, "=",
				true, true, WikiPage::getResourcePrimKey),
			new FinderColumn<>(
				"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true, true,
				WikiPage::getNodeId),
			new FinderColumn<>(
				"wikiPage.", "head", FinderColumn.Type.BOOLEAN, "=", true, true,
				WikiPage::isHead));

		_collectionPersistenceFinderByR_N_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_N_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"resourcePrimKey", "nodeId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_N_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"resourcePrimKey", "nodeId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_N_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"resourcePrimKey", "nodeId", "status"}, false),
			_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
			WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"wikiPage.", "resourcePrimKey", FinderColumn.Type.LONG, "=",
				true, true, WikiPage::getResourcePrimKey),
			new FinderColumn<>(
				"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true, true,
				WikiPage::getNodeId),
			new FinderColumn<>(
				"wikiPage.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, WikiPage::getStatus));

		_uniquePersistenceFinderByG_ERC_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_ERC_V",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Double.class.getName()
				},
				new String[] {"groupId", "externalReferenceCode", "version"}, 0,
				2, false, WikiPage::getGroupId,
				convertNullFunction(WikiPage::getExternalReferenceCode),
				WikiPage::getVersion),
			_SQL_SELECT_WIKIPAGE_WHERE, "",
			new FinderColumn<>(
				"wikiPage.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				WikiPage::getGroupId),
			new FinderColumn<>(
				"wikiPage.", "externalReferenceCode", FinderColumn.Type.STRING,
				"=", true, true, WikiPage::getExternalReferenceCode),
			new FinderColumn<>(
				"wikiPage.", "version", FinderColumn.Type.DOUBLE, "=", true,
				true, WikiPage::getVersion));

		_collectionPersistenceFinderByG_N_H =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N_H",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "nodeId", "head"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N_H",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "nodeId", "head"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_N_H",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "nodeId", "head"}, false),
				_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
				WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"wikiPage.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getGroupId),
				new FinderColumn<>(
					"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getNodeId),
				new FinderColumn<>(
					"wikiPage.", "head", FinderColumn.Type.BOOLEAN, "=", true,
					true, WikiPage::isHead));

		_collectionPersistenceFinderByG_N_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "nodeId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "nodeId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_N_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "nodeId", "status"}, false),
				_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
				WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"wikiPage.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getGroupId),
				new FinderColumn<>(
					"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getNodeId),
				new FinderColumn<>(
					"wikiPage.", "status", FinderColumn.Type.INTEGER, "=", true,
					true, WikiPage::getStatus));

		_collectionPersistenceFinderByU_N_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_N_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"userId", "nodeId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_N_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"userId", "nodeId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_N_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"userId", "nodeId", "status"}, false),
			_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
			WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"wikiPage.", "userId", FinderColumn.Type.LONG, "=", true, true,
				WikiPage::getUserId),
			new FinderColumn<>(
				"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true, true,
				WikiPage::getNodeId),
			new FinderColumn<>(
				"wikiPage.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, WikiPage::getStatus));

		_uniquePersistenceFinderByN_T_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByN_T_V",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Double.class.getName()
				},
				new String[] {"nodeId", "title", "version"}, 2, 2, false,
				WikiPage::getNodeId, convertCaseFunction(WikiPage::getTitle),
				WikiPage::getVersion),
			_SQL_SELECT_WIKIPAGE_WHERE, "",
			new FinderColumn<>(
				"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true, true,
				WikiPage::getNodeId),
			new FinderColumn<>(
				"wikiPage.", "title", FinderColumn.Type.STRING, "=", false,
				true, WikiPage::getTitle),
			new FinderColumn<>(
				"wikiPage.", "version", FinderColumn.Type.DOUBLE, "=", true,
				true, WikiPage::getVersion));

		_collectionPersistenceFinderByN_T_H = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_T_H",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Boolean.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"nodeId", "title", "head"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_T_H",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"nodeId", "title", "head"}, 2, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_T_H",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"nodeId", "title", "head"}, 2, 2, false, null),
			_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
			WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true, true,
				WikiPage::getNodeId),
			new FinderColumn<>(
				"wikiPage.", "title", FinderColumn.Type.STRING, "=", false,
				true, WikiPage::getTitle),
			new FinderColumn<>(
				"wikiPage.", "head", FinderColumn.Type.BOOLEAN, "=", true, true,
				WikiPage::isHead));

		_collectionPersistenceFinderByN_T_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_T_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"nodeId", "title", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_T_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName()
				},
				new String[] {"nodeId", "title", "status"}, 2, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_T_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName()
				},
				new String[] {"nodeId", "title", "status"}, 2, 2, false, null),
			_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
			WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true, true,
				WikiPage::getNodeId),
			new FinderColumn<>(
				"wikiPage.", "title", FinderColumn.Type.STRING, "=", false,
				true, WikiPage::getTitle),
			new FinderColumn<>(
				"wikiPage.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, WikiPage::getStatus));

		_collectionPersistenceFinderByN_H_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_H_P",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"nodeId", "head", "parentTitle"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_H_P",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					String.class.getName()
				},
				new String[] {"nodeId", "head", "parentTitle"}, 4, 4, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_H_P",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					String.class.getName()
				},
				new String[] {"nodeId", "head", "parentTitle"}, 4, 4, false,
				null),
			_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
			WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true, true,
				WikiPage::getNodeId),
			new FinderColumn<>(
				"wikiPage.", "head", FinderColumn.Type.BOOLEAN, "=", true, true,
				WikiPage::isHead),
			new FinderColumn<>(
				"wikiPage.", "parentTitle", FinderColumn.Type.STRING, "=",
				false, true, WikiPage::getParentTitle));

		_collectionPersistenceFinderByN_H_R = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_H_R",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"nodeId", "head", "redirectTitle"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_H_R",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					String.class.getName()
				},
				new String[] {"nodeId", "head", "redirectTitle"}, 4, 4, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_H_R",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					String.class.getName()
				},
				new String[] {"nodeId", "head", "redirectTitle"}, 4, 4, false,
				null),
			_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
			WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true, true,
				WikiPage::getNodeId),
			new FinderColumn<>(
				"wikiPage.", "head", FinderColumn.Type.BOOLEAN, "=", true, true,
				WikiPage::isHead),
			new FinderColumn<>(
				"wikiPage.", "redirectTitle", FinderColumn.Type.STRING, "=",
				false, true, WikiPage::getRedirectTitle));

		_collectionPersistenceFinderByN_H_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_H_S",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"nodeId", "head", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_H_S",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName()
				},
				new String[] {"nodeId", "head", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_H_S",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName()
				},
				new String[] {"nodeId", "head", "status"}, false),
			_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
			WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true, true,
				WikiPage::getNodeId),
			new FinderColumn<>(
				"wikiPage.", "head", FinderColumn.Type.BOOLEAN, "=", true, true,
				WikiPage::isHead),
			new FinderColumn<>(
				"wikiPage.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, WikiPage::getStatus));

		_collectionPersistenceFinderByN_H_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_H_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"nodeId", "head", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByN_H_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {"nodeId", "head", "status"}, false),
				_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
				WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getNodeId),
				new FinderColumn<>(
					"wikiPage.", "head", FinderColumn.Type.BOOLEAN, "=", true,
					true, WikiPage::isHead),
				new FinderColumn<>(
					"wikiPage.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, WikiPage::getStatus));

		_collectionPersistenceFinderByG_U_N_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_N_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "userId", "nodeId", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U_N_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "userId", "nodeId", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U_N_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "userId", "nodeId", "status"},
					false),
				_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
				WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"wikiPage.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getGroupId),
				new FinderColumn<>(
					"wikiPage.", "userId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getUserId),
				new FinderColumn<>(
					"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getNodeId),
				new FinderColumn<>(
					"wikiPage.", "status", FinderColumn.Type.INTEGER, "=", true,
					true, WikiPage::getStatus));

		_collectionPersistenceFinderByG_N_T_H =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N_T_H",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "nodeId", "title", "head"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N_T_H",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "nodeId", "title", "head"}, 4, 4,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_N_T_H",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "nodeId", "title", "head"}, 4, 4,
					false, null),
				_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
				WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"wikiPage.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getGroupId),
				new FinderColumn<>(
					"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getNodeId),
				new FinderColumn<>(
					"wikiPage.", "title", FinderColumn.Type.STRING, "=", false,
					true, WikiPage::getTitle),
				new FinderColumn<>(
					"wikiPage.", "head", FinderColumn.Type.BOOLEAN, "=", true,
					true, WikiPage::isHead));

		_collectionPersistenceFinderByG_N_H_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N_H_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "nodeId", "head", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N_H_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "nodeId", "head", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_N_H_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "nodeId", "head", "status"},
					false),
				_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
				WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"wikiPage.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getGroupId),
				new FinderColumn<>(
					"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getNodeId),
				new FinderColumn<>(
					"wikiPage.", "head", FinderColumn.Type.BOOLEAN, "=", true,
					true, WikiPage::isHead),
				new FinderColumn<>(
					"wikiPage.", "status", FinderColumn.Type.INTEGER, "=", true,
					true, WikiPage::getStatus));

		_collectionPersistenceFinderByN_H_P_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_H_P_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"nodeId", "head", "parentTitle", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_H_P_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {"nodeId", "head", "parentTitle", "status"}, 4,
					4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_H_P_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {"nodeId", "head", "parentTitle", "status"}, 4,
					4, false, null),
				_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
				WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getNodeId),
				new FinderColumn<>(
					"wikiPage.", "head", FinderColumn.Type.BOOLEAN, "=", true,
					true, WikiPage::isHead),
				new FinderColumn<>(
					"wikiPage.", "parentTitle", FinderColumn.Type.STRING, "=",
					false, true, WikiPage::getParentTitle),
				new FinderColumn<>(
					"wikiPage.", "status", FinderColumn.Type.INTEGER, "=", true,
					true, WikiPage::getStatus));

		_collectionPersistenceFinderByN_H_P_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_H_P_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"nodeId", "head", "parentTitle", "status"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByN_H_P_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {"nodeId", "head", "parentTitle", "status"},
					false),
				_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
				WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getNodeId),
				new FinderColumn<>(
					"wikiPage.", "head", FinderColumn.Type.BOOLEAN, "=", true,
					true, WikiPage::isHead),
				new FinderColumn<>(
					"wikiPage.", "parentTitle", FinderColumn.Type.STRING, "=",
					false, true, WikiPage::getParentTitle),
				new FinderColumn<>(
					"wikiPage.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, WikiPage::getStatus));

		_collectionPersistenceFinderByN_H_R_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_H_R_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"nodeId", "head", "redirectTitle", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_H_R_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {"nodeId", "head", "redirectTitle", "status"},
					4, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_H_R_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {"nodeId", "head", "redirectTitle", "status"},
					4, 4, false, null),
				_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
				WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getNodeId),
				new FinderColumn<>(
					"wikiPage.", "head", FinderColumn.Type.BOOLEAN, "=", true,
					true, WikiPage::isHead),
				new FinderColumn<>(
					"wikiPage.", "redirectTitle", FinderColumn.Type.STRING, "=",
					false, true, WikiPage::getRedirectTitle),
				new FinderColumn<>(
					"wikiPage.", "status", FinderColumn.Type.INTEGER, "=", true,
					true, WikiPage::getStatus));

		_collectionPersistenceFinderByN_H_R_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_H_R_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"nodeId", "head", "redirectTitle", "status"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByN_H_R_NotS",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {"nodeId", "head", "redirectTitle", "status"},
					false),
				_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
				WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getNodeId),
				new FinderColumn<>(
					"wikiPage.", "head", FinderColumn.Type.BOOLEAN, "=", true,
					true, WikiPage::isHead),
				new FinderColumn<>(
					"wikiPage.", "redirectTitle", FinderColumn.Type.STRING, "=",
					false, true, WikiPage::getRedirectTitle),
				new FinderColumn<>(
					"wikiPage.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, WikiPage::getStatus));

		_collectionPersistenceFinderByG_N_H_P_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N_H_P_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "nodeId", "head", "parentTitle", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_N_H_P_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "nodeId", "head", "parentTitle", "status"
					},
					8, 8, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_N_H_P_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "nodeId", "head", "parentTitle", "status"
					},
					8, 8, false, null),
				_SQL_SELECT_WIKIPAGE_WHERE, _SQL_COUNT_WIKIPAGE_WHERE,
				WikiPageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"wikiPage.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getGroupId),
				new FinderColumn<>(
					"wikiPage.", "nodeId", FinderColumn.Type.LONG, "=", true,
					true, WikiPage::getNodeId),
				new FinderColumn<>(
					"wikiPage.", "head", FinderColumn.Type.BOOLEAN, "=", true,
					true, WikiPage::isHead),
				new FinderColumn<>(
					"wikiPage.", "parentTitle", FinderColumn.Type.STRING, "=",
					false, true, WikiPage::getParentTitle),
				new FinderColumn<>(
					"wikiPage.", "status", FinderColumn.Type.INTEGER, "=", true,
					true, WikiPage::getStatus));

		WikiPageUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		WikiPageUtil.setPersistence(null);

		entityCache.removeCache(WikiPageImpl.class.getName());
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
		WikiPageModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_WIKIPAGE =
		"SELECT wikiPage FROM WikiPage wikiPage";

	private static final String _SQL_SELECT_WIKIPAGE_WHERE =
		"SELECT wikiPage FROM WikiPage wikiPage WHERE ";

	private static final String _SQL_COUNT_WIKIPAGE_WHERE =
		"SELECT COUNT(wikiPage) FROM WikiPage wikiPage WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1223465307