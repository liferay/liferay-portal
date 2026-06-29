/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.persistence.impl;

import com.liferay.journal.exception.NoSuchArticleException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleTable;
import com.liferay.journal.model.impl.JournalArticleImpl;
import com.liferay.journal.model.impl.JournalArticleModelImpl;
import com.liferay.journal.service.persistence.JournalArticlePersistence;
import com.liferay.journal.service.persistence.JournalArticleUtil;
import com.liferay.journal.service.persistence.impl.constants.JournalPersistenceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
 * The persistence implementation for the journal article service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = JournalArticlePersistence.class)
public class JournalArticlePersistenceImpl
	extends BasePersistenceImpl<JournalArticle, NoSuchArticleException>
	implements JournalArticlePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>JournalArticleUtil</code> to access the journal article persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		JournalArticleImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<JournalArticle, NoSuchArticleException>
		_collectionPersistenceFinderByResourcePrimKey;

	/**
	 * Returns an ordered range of all the journal articles where resourcePrimKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByResourcePrimKey(
		long resourcePrimKey, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByResourcePrimKey.find(
			finderCache, new Object[] {resourcePrimKey}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByResourcePrimKey_First(
			long resourcePrimKey,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByResourcePrimKey.findFirst(
			finderCache, new Object[] {resourcePrimKey}, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByResourcePrimKey_First(
		long resourcePrimKey,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByResourcePrimKey.fetchFirst(
			finderCache, new Object[] {resourcePrimKey}, orderByComparator);
	}

	/**
	 * Removes all the journal articles where resourcePrimKey = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 */
	@Override
	public void removeByResourcePrimKey(long resourcePrimKey) {
		_collectionPersistenceFinderByResourcePrimKey.remove(
			finderCache, new Object[] {resourcePrimKey});
	}

	/**
	 * Returns the number of journal articles where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByResourcePrimKey(long resourcePrimKey) {
		return _collectionPersistenceFinderByResourcePrimKey.count(
			finderCache, new Object[] {resourcePrimKey});
	}

	private CollectionPersistenceFinder<JournalArticle, NoSuchArticleException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the journal articles where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByUuid_First(
			String uuid, OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByUuid_First(
		String uuid, OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the journal articles where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of journal articles where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<JournalArticle, NoSuchArticleException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the journal article where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByUUID_G(String uuid, long groupId)
		throws NoSuchArticleException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the journal article where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the journal article where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the journal article that was removed
	 */
	@Override
	public JournalArticle removeByUUID_G(String uuid, long groupId)
		throws NoSuchArticleException {

		JournalArticle journalArticle = findByUUID_G(uuid, groupId);

		return remove(journalArticle);
	}

	/**
	 * Returns the number of journal articles where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<JournalArticle, NoSuchArticleException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the journal articles where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the journal articles where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of journal articles where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByGroupId_First(
			long groupId, OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByGroupId_First(
		long groupId, OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupId}, groupId);
	}

	private CollectionPersistenceFinder<JournalArticle, NoSuchArticleException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the journal articles where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByCompanyId_First(
			long companyId, OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByCompanyId_First(
		long companyId, OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the journal articles where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of journal articles where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder<JournalArticle, NoSuchArticleException>
		_collectionPersistenceFinderByDDMStructureId;

	/**
	 * Returns an ordered range of all the journal articles where DDMStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByDDMStructureId(
		long DDMStructureId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByDDMStructureId.find(
			finderCache, new Object[] {DDMStructureId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where DDMStructureId = &#63;.
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByDDMStructureId_First(
			long DDMStructureId,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByDDMStructureId.findFirst(
			finderCache, new Object[] {DDMStructureId}, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where DDMStructureId = &#63;.
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByDDMStructureId_First(
		long DDMStructureId,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByDDMStructureId.fetchFirst(
			finderCache, new Object[] {DDMStructureId}, orderByComparator);
	}

	/**
	 * Removes all the journal articles where DDMStructureId = &#63; from the database.
	 *
	 * @param DDMStructureId the ddm structure ID
	 */
	@Override
	public void removeByDDMStructureId(long DDMStructureId) {
		_collectionPersistenceFinderByDDMStructureId.remove(
			finderCache, new Object[] {DDMStructureId});
	}

	/**
	 * Returns the number of journal articles where DDMStructureId = &#63;.
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByDDMStructureId(long DDMStructureId) {
		return _collectionPersistenceFinderByDDMStructureId.count(
			finderCache, new Object[] {DDMStructureId});
	}

	private CollectionPersistenceFinder<JournalArticle, NoSuchArticleException>
		_collectionPersistenceFinderByDDMTemplateKey;

	/**
	 * Returns an ordered range of all the journal articles where DDMTemplateKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param DDMTemplateKey the ddm template key
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByDDMTemplateKey(
		String DDMTemplateKey, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByDDMTemplateKey.find(
			finderCache, new Object[] {DDMTemplateKey}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where DDMTemplateKey = &#63;.
	 *
	 * @param DDMTemplateKey the ddm template key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByDDMTemplateKey_First(
			String DDMTemplateKey,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByDDMTemplateKey.findFirst(
			finderCache, new Object[] {DDMTemplateKey}, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where DDMTemplateKey = &#63;.
	 *
	 * @param DDMTemplateKey the ddm template key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByDDMTemplateKey_First(
		String DDMTemplateKey,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByDDMTemplateKey.fetchFirst(
			finderCache, new Object[] {DDMTemplateKey}, orderByComparator);
	}

	/**
	 * Removes all the journal articles where DDMTemplateKey = &#63; from the database.
	 *
	 * @param DDMTemplateKey the ddm template key
	 */
	@Override
	public void removeByDDMTemplateKey(String DDMTemplateKey) {
		_collectionPersistenceFinderByDDMTemplateKey.remove(
			finderCache, new Object[] {DDMTemplateKey});
	}

	/**
	 * Returns the number of journal articles where DDMTemplateKey = &#63;.
	 *
	 * @param DDMTemplateKey the ddm template key
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByDDMTemplateKey(String DDMTemplateKey) {
		return _collectionPersistenceFinderByDDMTemplateKey.count(
			finderCache, new Object[] {DDMTemplateKey});
	}

	private CollectionPersistenceFinder<JournalArticle, NoSuchArticleException>
		_collectionPersistenceFinderByLayoutUuid;

	/**
	 * Returns an ordered range of all the journal articles where layoutUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByLayoutUuid(
		String layoutUuid, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLayoutUuid.find(
			finderCache, new Object[] {layoutUuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where layoutUuid = &#63;.
	 *
	 * @param layoutUuid the layout uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByLayoutUuid_First(
			String layoutUuid,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByLayoutUuid.findFirst(
			finderCache, new Object[] {layoutUuid}, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where layoutUuid = &#63;.
	 *
	 * @param layoutUuid the layout uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByLayoutUuid_First(
		String layoutUuid,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByLayoutUuid.fetchFirst(
			finderCache, new Object[] {layoutUuid}, orderByComparator);
	}

	/**
	 * Removes all the journal articles where layoutUuid = &#63; from the database.
	 *
	 * @param layoutUuid the layout uuid
	 */
	@Override
	public void removeByLayoutUuid(String layoutUuid) {
		_collectionPersistenceFinderByLayoutUuid.remove(
			finderCache, new Object[] {layoutUuid});
	}

	/**
	 * Returns the number of journal articles where layoutUuid = &#63;.
	 *
	 * @param layoutUuid the layout uuid
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByLayoutUuid(String layoutUuid) {
		return _collectionPersistenceFinderByLayoutUuid.count(
			finderCache, new Object[] {layoutUuid});
	}

	private CollectionPersistenceFinder<JournalArticle, NoSuchArticleException>
		_collectionPersistenceFinderBySmallImageId;

	/**
	 * Returns an ordered range of all the journal articles where smallImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findBySmallImageId(
		long smallImageId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySmallImageId.find(
			finderCache, new Object[] {smallImageId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findBySmallImageId_First(
			long smallImageId,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderBySmallImageId.findFirst(
			finderCache, new Object[] {smallImageId}, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchBySmallImageId_First(
		long smallImageId,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderBySmallImageId.fetchFirst(
			finderCache, new Object[] {smallImageId}, orderByComparator);
	}

	/**
	 * Removes all the journal articles where smallImageId = &#63; from the database.
	 *
	 * @param smallImageId the small image ID
	 */
	@Override
	public void removeBySmallImageId(long smallImageId) {
		_collectionPersistenceFinderBySmallImageId.remove(
			finderCache, new Object[] {smallImageId});
	}

	/**
	 * Returns the number of journal articles where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @return the number of matching journal articles
	 */
	@Override
	public int countBySmallImageId(long smallImageId) {
		return _collectionPersistenceFinderBySmallImageId.count(
			finderCache, new Object[] {smallImageId});
	}

	private CollectionPersistenceFinder<JournalArticle, NoSuchArticleException>
		_collectionPersistenceFinderByR_I;

	/**
	 * Returns an ordered range of all the journal articles where resourcePrimKey = &#63; and indexable = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByR_I(
		long resourcePrimKey, boolean indexable, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_I.find(
			finderCache, new Object[] {resourcePrimKey, indexable}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where resourcePrimKey = &#63; and indexable = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByR_I_First(
			long resourcePrimKey, boolean indexable,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByR_I.findFirst(
			finderCache, new Object[] {resourcePrimKey, indexable},
			orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where resourcePrimKey = &#63; and indexable = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByR_I_First(
		long resourcePrimKey, boolean indexable,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_I.fetchFirst(
			finderCache, new Object[] {resourcePrimKey, indexable},
			orderByComparator);
	}

	/**
	 * Removes all the journal articles where resourcePrimKey = &#63; and indexable = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 */
	@Override
	public void removeByR_I(long resourcePrimKey, boolean indexable) {
		_collectionPersistenceFinderByR_I.remove(
			finderCache, new Object[] {resourcePrimKey, indexable});
	}

	/**
	 * Returns the number of journal articles where resourcePrimKey = &#63; and indexable = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByR_I(long resourcePrimKey, boolean indexable) {
		return _collectionPersistenceFinderByR_I.count(
			finderCache, new Object[] {resourcePrimKey, indexable});
	}

	private CollectionPersistenceFinder<JournalArticle, NoSuchArticleException>
		_collectionPersistenceFinderByR_ST;

	/**
	 * Returns an ordered range of all the journal articles where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByR_ST(
		long resourcePrimKey, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_ST.find(
			finderCache, new Object[] {resourcePrimKey, new int[] {status}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByR_ST_First(
			long resourcePrimKey, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		JournalArticle journalArticle = fetchByR_ST_First(
			resourcePrimKey, status, orderByComparator);

		if (journalArticle != null) {
			return journalArticle;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("resourcePrimKey=");
		sb.append(resourcePrimKey);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchArticleException(sb.toString());
	}

	/**
	 * Returns the first journal article in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByR_ST_First(
		long resourcePrimKey, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_ST.fetchFirst(
			finderCache, new Object[] {resourcePrimKey, new int[] {status}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles where resourcePrimKey = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByR_ST(
		long resourcePrimKey, int[] statuses, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_ST.find(
			finderCache,
			new Object[] {resourcePrimKey, ArrayUtil.sortedUnique(statuses)},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the journal articles where resourcePrimKey = &#63; and status = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 */
	@Override
	public void removeByR_ST(long resourcePrimKey, int status) {
		_collectionPersistenceFinderByR_ST.remove(
			finderCache, new Object[] {resourcePrimKey, new int[] {status}});
	}

	/**
	 * Returns the number of journal articles where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByR_ST(long resourcePrimKey, int status) {
		return _collectionPersistenceFinderByR_ST.count(
			finderCache, new Object[] {resourcePrimKey, new int[] {status}});
	}

	/**
	 * Returns the number of journal articles where resourcePrimKey = &#63; and status = any &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param statuses the statuses
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByR_ST(long resourcePrimKey, int[] statuses) {
		return _collectionPersistenceFinderByR_ST.count(
			finderCache,
			new Object[] {resourcePrimKey, ArrayUtil.sortedUnique(statuses)});
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_U;

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U.find(
			finderCache, new Object[] {groupId, userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_U_First(
			long groupId, long userId,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_U.findFirst(
			finderCache, new Object[] {groupId, userId}, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_U_First(
		long groupId, long userId,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_U.fetchFirst(
			finderCache, new Object[] {groupId, userId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_U.filterFind(
			finderCache, new Object[] {groupId, userId}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and userId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByG_U(long groupId, long userId) {
		_collectionPersistenceFinderByG_U.remove(
			finderCache, new Object[] {groupId, userId});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_U(long groupId, long userId) {
		return _collectionPersistenceFinderByG_U.count(
			finderCache, new Object[] {groupId, userId});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_U(long groupId, long userId) {
		return _collectionPersistenceFinderByG_U.filterCount(
			finderCache, new Object[] {groupId, userId}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_ERC;

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_ERC(
		long groupId, String externalReferenceCode, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_ERC.find(
			finderCache, new Object[] {groupId, externalReferenceCode}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_ERC_First(
			long groupId, String externalReferenceCode,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_ERC.findFirst(
			finderCache, new Object[] {groupId, externalReferenceCode},
			orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_ERC_First(
		long groupId, String externalReferenceCode,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_ERC.fetchFirst(
			finderCache, new Object[] {groupId, externalReferenceCode},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_ERC(
		long groupId, String externalReferenceCode, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_ERC.filterFind(
			finderCache, new Object[] {groupId, externalReferenceCode}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and externalReferenceCode = &#63; from the database.
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
	 * Returns the number of journal articles where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_ERC(long groupId, String externalReferenceCode) {
		return _collectionPersistenceFinderByG_ERC.count(
			finderCache, new Object[] {groupId, externalReferenceCode});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_ERC(long groupId, String externalReferenceCode) {
		return _collectionPersistenceFinderByG_ERC.filterCount(
			finderCache, new Object[] {groupId, externalReferenceCode},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_F;

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_F(
		long groupId, long folderId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_F.find(
			finderCache, new Object[] {groupId, new long[] {folderId}}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_F_First(
			long groupId, long folderId,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		JournalArticle journalArticle = fetchByG_F_First(
			groupId, folderId, orderByComparator);

		if (journalArticle != null) {
			return journalArticle;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", folderId=");
		sb.append(folderId);

		sb.append("}");

		throw new NoSuchArticleException(sb.toString());
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_F_First(
		long groupId, long folderId,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_F.fetchFirst(
			finderCache, new Object[] {groupId, new long[] {folderId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_F(
		long groupId, long folderId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_F.filterFind(
			finderCache, new Object[] {groupId, new long[] {folderId}}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_F(
		long groupId, long[] folderIds, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_F.filterFind(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(folderIds)}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and folderId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_F(
		long groupId, long[] folderIds, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_F.find(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(folderIds)}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and folderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 */
	@Override
	public void removeByG_F(long groupId, long folderId) {
		_collectionPersistenceFinderByG_F.remove(
			finderCache, new Object[] {groupId, new long[] {folderId}});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_F(long groupId, long folderId) {
		return _collectionPersistenceFinderByG_F.count(
			finderCache, new Object[] {groupId, new long[] {folderId}});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_F(long groupId, long[] folderIds) {
		return _collectionPersistenceFinderByG_F.count(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(folderIds)});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_F(long groupId, long folderId) {
		return _collectionPersistenceFinderByG_F.filterCount(
			finderCache, new Object[] {groupId, new long[] {folderId}},
			groupId);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_F(long groupId, long[] folderIds) {
		return _collectionPersistenceFinderByG_F.filterCount(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(folderIds)}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_A;

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and articleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_A(
		long groupId, String articleId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_A.find(
			finderCache, new Object[] {groupId, articleId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and articleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_A_First(
			long groupId, String articleId,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_A.findFirst(
			finderCache, new Object[] {groupId, articleId}, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and articleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_A_First(
		long groupId, String articleId,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_A.fetchFirst(
			finderCache, new Object[] {groupId, articleId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and articleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_A(
		long groupId, String articleId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_A.filterFind(
			finderCache, new Object[] {groupId, articleId}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and articleId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 */
	@Override
	public void removeByG_A(long groupId, String articleId) {
		_collectionPersistenceFinderByG_A.remove(
			finderCache, new Object[] {groupId, articleId});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and articleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_A(long groupId, String articleId) {
		return _collectionPersistenceFinderByG_A.count(
			finderCache, new Object[] {groupId, articleId});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and articleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_A(long groupId, String articleId) {
		return _collectionPersistenceFinderByG_A.filterCount(
			finderCache, new Object[] {groupId, articleId}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_UT;

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_UT(
		long groupId, String urlTitle, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_UT.find(
			finderCache, new Object[] {groupId, urlTitle}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_UT_First(
			long groupId, String urlTitle,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_UT.findFirst(
			finderCache, new Object[] {groupId, urlTitle}, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_UT_First(
		long groupId, String urlTitle,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_UT.fetchFirst(
			finderCache, new Object[] {groupId, urlTitle}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_UT(
		long groupId, String urlTitle, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_UT.filterFind(
			finderCache, new Object[] {groupId, urlTitle}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 */
	@Override
	public void removeByG_UT(long groupId, String urlTitle) {
		_collectionPersistenceFinderByG_UT.remove(
			finderCache, new Object[] {groupId, urlTitle});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_UT(long groupId, String urlTitle) {
		return _collectionPersistenceFinderByG_UT.count(
			finderCache, new Object[] {groupId, urlTitle});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_UT(long groupId, String urlTitle) {
		return _collectionPersistenceFinderByG_UT.filterCount(
			finderCache, new Object[] {groupId, urlTitle}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_DDMSI;

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and DDMStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param DDMStructureId the ddm structure ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_DDMSI(
		long groupId, long DDMStructureId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_DDMSI.find(
			finderCache, new Object[] {groupId, DDMStructureId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and DDMStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param DDMStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_DDMSI_First(
			long groupId, long DDMStructureId,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_DDMSI.findFirst(
			finderCache, new Object[] {groupId, DDMStructureId},
			orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and DDMStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param DDMStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_DDMSI_First(
		long groupId, long DDMStructureId,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_DDMSI.fetchFirst(
			finderCache, new Object[] {groupId, DDMStructureId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and DDMStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param DDMStructureId the ddm structure ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_DDMSI(
		long groupId, long DDMStructureId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_DDMSI.filterFind(
			finderCache, new Object[] {groupId, DDMStructureId}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and DDMStructureId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param DDMStructureId the ddm structure ID
	 */
	@Override
	public void removeByG_DDMSI(long groupId, long DDMStructureId) {
		_collectionPersistenceFinderByG_DDMSI.remove(
			finderCache, new Object[] {groupId, DDMStructureId});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and DDMStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param DDMStructureId the ddm structure ID
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_DDMSI(long groupId, long DDMStructureId) {
		return _collectionPersistenceFinderByG_DDMSI.count(
			finderCache, new Object[] {groupId, DDMStructureId});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and DDMStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param DDMStructureId the ddm structure ID
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_DDMSI(long groupId, long DDMStructureId) {
		return _collectionPersistenceFinderByG_DDMSI.filterCount(
			finderCache, new Object[] {groupId, DDMStructureId}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_DDMTK;

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and DDMTemplateKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param DDMTemplateKey the ddm template key
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_DDMTK(
		long groupId, String DDMTemplateKey, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_DDMTK.find(
			finderCache, new Object[] {groupId, DDMTemplateKey}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and DDMTemplateKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param DDMTemplateKey the ddm template key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_DDMTK_First(
			long groupId, String DDMTemplateKey,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_DDMTK.findFirst(
			finderCache, new Object[] {groupId, DDMTemplateKey},
			orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and DDMTemplateKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param DDMTemplateKey the ddm template key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_DDMTK_First(
		long groupId, String DDMTemplateKey,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_DDMTK.fetchFirst(
			finderCache, new Object[] {groupId, DDMTemplateKey},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and DDMTemplateKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param DDMTemplateKey the ddm template key
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_DDMTK(
		long groupId, String DDMTemplateKey, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_DDMTK.filterFind(
			finderCache, new Object[] {groupId, DDMTemplateKey}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and DDMTemplateKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param DDMTemplateKey the ddm template key
	 */
	@Override
	public void removeByG_DDMTK(long groupId, String DDMTemplateKey) {
		_collectionPersistenceFinderByG_DDMTK.remove(
			finderCache, new Object[] {groupId, DDMTemplateKey});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and DDMTemplateKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param DDMTemplateKey the ddm template key
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_DDMTK(long groupId, String DDMTemplateKey) {
		return _collectionPersistenceFinderByG_DDMTK.count(
			finderCache, new Object[] {groupId, DDMTemplateKey});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and DDMTemplateKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param DDMTemplateKey the ddm template key
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_DDMTK(long groupId, String DDMTemplateKey) {
		return _collectionPersistenceFinderByG_DDMTK.filterCount(
			finderCache, new Object[] {groupId, DDMTemplateKey}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_L;

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_L(
		long groupId, String layoutUuid, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_L.find(
			finderCache, new Object[] {groupId, layoutUuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_L_First(
			long groupId, String layoutUuid,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_L.findFirst(
			finderCache, new Object[] {groupId, layoutUuid}, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_L_First(
		long groupId, String layoutUuid,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_L.fetchFirst(
			finderCache, new Object[] {groupId, layoutUuid}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_L(
		long groupId, String layoutUuid, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_L.filterFind(
			finderCache, new Object[] {groupId, layoutUuid}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and layoutUuid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 */
	@Override
	public void removeByG_L(long groupId, String layoutUuid) {
		_collectionPersistenceFinderByG_L.remove(
			finderCache, new Object[] {groupId, layoutUuid});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_L(long groupId, String layoutUuid) {
		return _collectionPersistenceFinderByG_L.count(
			finderCache, new Object[] {groupId, layoutUuid});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_L(long groupId, String layoutUuid) {
		return _collectionPersistenceFinderByG_L.filterCount(
			finderCache, new Object[] {groupId, layoutUuid}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_ST;

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_ST(
		long groupId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_ST.find(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_ST_First(
			long groupId, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_ST.findFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_ST_First(
		long groupId, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_ST.fetchFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_ST(
		long groupId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_ST.filterFind(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_ST(long groupId, int status) {
		_collectionPersistenceFinderByG_ST.remove(
			finderCache, new Object[] {groupId, status});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_ST(long groupId, int status) {
		return _collectionPersistenceFinderByG_ST.count(
			finderCache, new Object[] {groupId, status});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_ST(long groupId, int status) {
		return _collectionPersistenceFinderByG_ST.filterCount(
			finderCache, new Object[] {groupId, status}, groupId);
	}

	private CollectionPersistenceFinder<JournalArticle, NoSuchArticleException>
		_collectionPersistenceFinderByC_V;

	/**
	 * Returns an ordered range of all the journal articles where companyId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByC_V(
		long companyId, double version, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_V.find(
			finderCache, new Object[] {companyId, version}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where companyId = &#63; and version = &#63;.
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByC_V_First(
			long companyId, double version,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByC_V.findFirst(
			finderCache, new Object[] {companyId, version}, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where companyId = &#63; and version = &#63;.
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByC_V_First(
		long companyId, double version,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByC_V.fetchFirst(
			finderCache, new Object[] {companyId, version}, orderByComparator);
	}

	/**
	 * Removes all the journal articles where companyId = &#63; and version = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param version the version
	 */
	@Override
	public void removeByC_V(long companyId, double version) {
		_collectionPersistenceFinderByC_V.remove(
			finderCache, new Object[] {companyId, version});
	}

	/**
	 * Returns the number of journal articles where companyId = &#63; and version = &#63;.
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByC_V(long companyId, double version) {
		return _collectionPersistenceFinderByC_V.count(
			finderCache, new Object[] {companyId, version});
	}

	private CollectionPersistenceFinder<JournalArticle, NoSuchArticleException>
		_collectionPersistenceFinderByC_ST;

	/**
	 * Returns an ordered range of all the journal articles where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByC_ST(
		long companyId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_ST.find(
			finderCache, new Object[] {companyId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByC_ST_First(
			long companyId, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByC_ST.findFirst(
			finderCache, new Object[] {companyId, status}, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByC_ST_First(
		long companyId, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByC_ST.fetchFirst(
			finderCache, new Object[] {companyId, status}, orderByComparator);
	}

	/**
	 * Removes all the journal articles where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_ST(long companyId, int status) {
		_collectionPersistenceFinderByC_ST.remove(
			finderCache, new Object[] {companyId, status});
	}

	/**
	 * Returns the number of journal articles where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByC_ST(long companyId, int status) {
		return _collectionPersistenceFinderByC_ST.count(
			finderCache, new Object[] {companyId, status});
	}

	private CollectionPersistenceFinder<JournalArticle, NoSuchArticleException>
		_collectionPersistenceFinderByC_NotST;

	/**
	 * Returns all the journal articles where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching journal articles
	 */
	@Override
	public List<JournalArticle> findByC_NotST(long companyId, int status) {
		return findByC_NotST(
			companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal articles where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByC_NotST(
		long companyId, int status, int start, int end) {

		return findByC_NotST(companyId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal articles where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByC_NotST(
		long companyId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return findByC_NotST(
			companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the journal articles where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByC_NotST(
		long companyId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_NotST.find(
			finderCache, new Object[] {companyId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByC_NotST_First(
			long companyId, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByC_NotST.findFirst(
			finderCache, new Object[] {companyId, status}, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByC_NotST_First(
		long companyId, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByC_NotST.fetchFirst(
			finderCache, new Object[] {companyId, status}, orderByComparator);
	}

	/**
	 * Removes all the journal articles where companyId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_NotST(long companyId, int status) {
		_collectionPersistenceFinderByC_NotST.remove(
			finderCache, new Object[] {companyId, status});
	}

	/**
	 * Returns the number of journal articles where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByC_NotST(long companyId, int status) {
		return _collectionPersistenceFinderByC_NotST.count(
			finderCache, new Object[] {companyId, status});
	}

	private CollectionPersistenceFinder<JournalArticle, NoSuchArticleException>
		_collectionPersistenceFinderByLtD_S;

	/**
	 * Returns all the journal articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching journal articles
	 */
	@Override
	public List<JournalArticle> findByLtD_S(Date displayDate, int status) {
		return findByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return findByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return findByLtD_S(
			displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the journal articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtD_S.find(
			finderCache, new Object[] {displayDate, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByLtD_S.findFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByLtD_S.fetchFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Removes all the journal articles where displayDate &lt; &#63; and status = &#63; from the database.
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
	 * Returns the number of journal articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByLtD_S(Date displayDate, int status) {
		return _collectionPersistenceFinderByLtD_S.count(
			finderCache, new Object[] {displayDate, status});
	}

	private CollectionPersistenceFinder<JournalArticle, NoSuchArticleException>
		_collectionPersistenceFinderByR_I_S;

	/**
	 * Returns an ordered range of all the journal articles where resourcePrimKey = &#63; and indexable = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByR_I_S(
		long resourcePrimKey, boolean indexable, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_I_S.find(
			finderCache,
			new Object[] {resourcePrimKey, indexable, new int[] {status}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where resourcePrimKey = &#63; and indexable = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByR_I_S_First(
			long resourcePrimKey, boolean indexable, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		JournalArticle journalArticle = fetchByR_I_S_First(
			resourcePrimKey, indexable, status, orderByComparator);

		if (journalArticle != null) {
			return journalArticle;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("resourcePrimKey=");
		sb.append(resourcePrimKey);

		sb.append(", indexable=");
		sb.append(indexable);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchArticleException(sb.toString());
	}

	/**
	 * Returns the first journal article in the ordered set where resourcePrimKey = &#63; and indexable = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByR_I_S_First(
		long resourcePrimKey, boolean indexable, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByR_I_S.fetchFirst(
			finderCache,
			new Object[] {resourcePrimKey, indexable, new int[] {status}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles where resourcePrimKey = &#63; and indexable = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByR_I_S(
		long resourcePrimKey, boolean indexable, int[] statuses, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_I_S.find(
			finderCache,
			new Object[] {
				resourcePrimKey, indexable, ArrayUtil.sortedUnique(statuses)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the journal articles where resourcePrimKey = &#63; and indexable = &#63; and status = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param status the status
	 */
	@Override
	public void removeByR_I_S(
		long resourcePrimKey, boolean indexable, int status) {

		_collectionPersistenceFinderByR_I_S.remove(
			finderCache,
			new Object[] {resourcePrimKey, indexable, new int[] {status}});
	}

	/**
	 * Returns the number of journal articles where resourcePrimKey = &#63; and indexable = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByR_I_S(
		long resourcePrimKey, boolean indexable, int status) {

		return _collectionPersistenceFinderByR_I_S.count(
			finderCache,
			new Object[] {resourcePrimKey, indexable, new int[] {status}});
	}

	/**
	 * Returns the number of journal articles where resourcePrimKey = &#63; and indexable = &#63; and status = any &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param statuses the statuses
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByR_I_S(
		long resourcePrimKey, boolean indexable, int[] statuses) {

		return _collectionPersistenceFinderByR_I_S.count(
			finderCache,
			new Object[] {
				resourcePrimKey, indexable, ArrayUtil.sortedUnique(statuses)
			});
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_U_C;

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and userId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_U_C(
		long groupId, long userId, long classNameId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U_C.find(
			finderCache, new Object[] {groupId, userId, classNameId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and userId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_U_C_First(
			long groupId, long userId, long classNameId,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_U_C.findFirst(
			finderCache, new Object[] {groupId, userId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and userId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_U_C_First(
		long groupId, long userId, long classNameId,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_U_C.fetchFirst(
			finderCache, new Object[] {groupId, userId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and userId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_U_C(
		long groupId, long userId, long classNameId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_U_C.filterFind(
			finderCache, new Object[] {groupId, userId, classNameId}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and userId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_U_C(long groupId, long userId, long classNameId) {
		_collectionPersistenceFinderByG_U_C.remove(
			finderCache, new Object[] {groupId, userId, classNameId});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and userId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_U_C(long groupId, long userId, long classNameId) {
		return _collectionPersistenceFinderByG_U_C.count(
			finderCache, new Object[] {groupId, userId, classNameId});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and userId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_C(long groupId, long userId, long classNameId) {
		return _collectionPersistenceFinderByG_U_C.filterCount(
			finderCache, new Object[] {groupId, userId, classNameId}, groupId);
	}

	private UniquePersistenceFinder<JournalArticle, NoSuchArticleException>
		_uniquePersistenceFinderByG_ERC_V;

	/**
	 * Returns the journal article where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_ERC_V(
			long groupId, String externalReferenceCode, double version)
		throws NoSuchArticleException {

		return _uniquePersistenceFinderByG_ERC_V.find(
			finderCache,
			new Object[] {groupId, externalReferenceCode, version});
	}

	/**
	 * Returns the journal article where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_ERC_V(
		long groupId, String externalReferenceCode, double version,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_ERC_V.fetch(
			finderCache, new Object[] {groupId, externalReferenceCode, version},
			useFinderCache);
	}

	/**
	 * Removes the journal article where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the journal article that was removed
	 */
	@Override
	public JournalArticle removeByG_ERC_V(
			long groupId, String externalReferenceCode, double version)
		throws NoSuchArticleException {

		JournalArticle journalArticle = findByG_ERC_V(
			groupId, externalReferenceCode, version);

		return remove(journalArticle);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and externalReferenceCode = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_ERC_V(
		long groupId, String externalReferenceCode, double version) {

		return _uniquePersistenceFinderByG_ERC_V.count(
			finderCache,
			new Object[] {groupId, externalReferenceCode, version});
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_ERC_ST;

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_ERC_ST(
		long groupId, String externalReferenceCode, int status, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_ERC_ST.find(
			finderCache,
			new Object[] {groupId, externalReferenceCode, new int[] {status}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_ERC_ST_First(
			long groupId, String externalReferenceCode, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		JournalArticle journalArticle = fetchByG_ERC_ST_First(
			groupId, externalReferenceCode, status, orderByComparator);

		if (journalArticle != null) {
			return journalArticle;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchArticleException(sb.toString());
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_ERC_ST_First(
		long groupId, String externalReferenceCode, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_ERC_ST.fetchFirst(
			finderCache,
			new Object[] {groupId, externalReferenceCode, new int[] {status}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_ERC_ST(
		long groupId, String externalReferenceCode, int status, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_ERC_ST.filterFind(
			finderCache,
			new Object[] {groupId, externalReferenceCode, new int[] {status}},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_ERC_ST(
		long groupId, String externalReferenceCode, int[] statuses, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_ERC_ST.filterFind(
			finderCache,
			new Object[] {
				groupId, externalReferenceCode, ArrayUtil.sortedUnique(statuses)
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_ERC_ST(
		long groupId, String externalReferenceCode, int[] statuses, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_ERC_ST.find(
			finderCache,
			new Object[] {
				groupId, externalReferenceCode, ArrayUtil.sortedUnique(statuses)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 */
	@Override
	public void removeByG_ERC_ST(
		long groupId, String externalReferenceCode, int status) {

		_collectionPersistenceFinderByG_ERC_ST.remove(
			finderCache,
			new Object[] {groupId, externalReferenceCode, new int[] {status}});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_ERC_ST(
		long groupId, String externalReferenceCode, int status) {

		return _collectionPersistenceFinderByG_ERC_ST.count(
			finderCache,
			new Object[] {groupId, externalReferenceCode, new int[] {status}});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and externalReferenceCode = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param statuses the statuses
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_ERC_ST(
		long groupId, String externalReferenceCode, int[] statuses) {

		return _collectionPersistenceFinderByG_ERC_ST.count(
			finderCache,
			new Object[] {
				groupId, externalReferenceCode, ArrayUtil.sortedUnique(statuses)
			});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_ERC_ST(
		long groupId, String externalReferenceCode, int status) {

		return _collectionPersistenceFinderByG_ERC_ST.filterCount(
			finderCache,
			new Object[] {groupId, externalReferenceCode, new int[] {status}},
			groupId);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param statuses the statuses
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_ERC_ST(
		long groupId, String externalReferenceCode, int[] statuses) {

		return _collectionPersistenceFinderByG_ERC_ST.filterCount(
			finderCache,
			new Object[] {
				groupId, externalReferenceCode, ArrayUtil.sortedUnique(statuses)
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_F_ST;

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_F_ST(
		long groupId, long folderId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_F_ST.find(
			finderCache, new Object[] {groupId, folderId, new int[] {status}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_F_ST_First(
			long groupId, long folderId, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		JournalArticle journalArticle = fetchByG_F_ST_First(
			groupId, folderId, status, orderByComparator);

		if (journalArticle != null) {
			return journalArticle;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", folderId=");
		sb.append(folderId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchArticleException(sb.toString());
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_F_ST_First(
		long groupId, long folderId, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_F_ST.fetchFirst(
			finderCache, new Object[] {groupId, folderId, new int[] {status}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_F_ST(
		long groupId, long folderId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_F_ST.filterFind(
			finderCache, new Object[] {groupId, folderId, new int[] {status}},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permission to view where groupId = &#63; and folderId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_F_ST(
		long groupId, long folderId, int[] statuses, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_F_ST.filterFind(
			finderCache,
			new Object[] {groupId, folderId, ArrayUtil.sortedUnique(statuses)},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and folderId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_F_ST(
		long groupId, long folderId, int[] statuses, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_F_ST.find(
			finderCache,
			new Object[] {groupId, folderId, ArrayUtil.sortedUnique(statuses)},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and folderId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 */
	@Override
	public void removeByG_F_ST(long groupId, long folderId, int status) {
		_collectionPersistenceFinderByG_F_ST.remove(
			finderCache, new Object[] {groupId, folderId, new int[] {status}});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_F_ST(long groupId, long folderId, int status) {
		return _collectionPersistenceFinderByG_F_ST.count(
			finderCache, new Object[] {groupId, folderId, new int[] {status}});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and folderId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param statuses the statuses
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_F_ST(long groupId, long folderId, int[] statuses) {
		return _collectionPersistenceFinderByG_F_ST.count(
			finderCache,
			new Object[] {groupId, folderId, ArrayUtil.sortedUnique(statuses)});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_F_ST(long groupId, long folderId, int status) {
		return _collectionPersistenceFinderByG_F_ST.filterCount(
			finderCache, new Object[] {groupId, folderId, new int[] {status}},
			groupId);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and folderId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param statuses the statuses
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_F_ST(
		long groupId, long folderId, int[] statuses) {

		return _collectionPersistenceFinderByG_F_ST.filterCount(
			finderCache,
			new Object[] {groupId, folderId, ArrayUtil.sortedUnique(statuses)},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_C_C;

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C.find(
			finderCache, new Object[] {groupId, classNameId, classPK}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_C_C_First(
			long groupId, long classNameId, long classPK,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_C_C.findFirst(
			finderCache, new Object[] {groupId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_C_C_First(
		long groupId, long classNameId, long classPK,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C.fetchFirst(
			finderCache, new Object[] {groupId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C.filterFind(
			finderCache, new Object[] {groupId, classNameId, classPK}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByG_C_C(long groupId, long classNameId, long classPK) {
		_collectionPersistenceFinderByG_C_C.remove(
			finderCache, new Object[] {groupId, classNameId, classPK});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_C_C(long groupId, long classNameId, long classPK) {
		return _collectionPersistenceFinderByG_C_C.count(
			finderCache, new Object[] {groupId, classNameId, classPK});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C(
		long groupId, long classNameId, long classPK) {

		return _collectionPersistenceFinderByG_C_C.filterCount(
			finderCache, new Object[] {groupId, classNameId, classPK}, groupId);
	}

	private UniquePersistenceFinder<JournalArticle, NoSuchArticleException>
		_uniquePersistenceFinderByG_C_DDMSI;

	/**
	 * Returns the journal article where groupId = &#63; and classNameId = &#63; and DDMStructureId = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param DDMStructureId the ddm structure ID
	 * @return the matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_C_DDMSI(
			long groupId, long classNameId, long DDMStructureId)
		throws NoSuchArticleException {

		return _uniquePersistenceFinderByG_C_DDMSI.find(
			finderCache, new Object[] {groupId, classNameId, DDMStructureId});
	}

	/**
	 * Returns the journal article where groupId = &#63; and classNameId = &#63; and DDMStructureId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param DDMStructureId the ddm structure ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_C_DDMSI(
		long groupId, long classNameId, long DDMStructureId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_C_DDMSI.fetch(
			finderCache, new Object[] {groupId, classNameId, DDMStructureId},
			useFinderCache);
	}

	/**
	 * Removes the journal article where groupId = &#63; and classNameId = &#63; and DDMStructureId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param DDMStructureId the ddm structure ID
	 * @return the journal article that was removed
	 */
	@Override
	public JournalArticle removeByG_C_DDMSI(
			long groupId, long classNameId, long DDMStructureId)
		throws NoSuchArticleException {

		JournalArticle journalArticle = findByG_C_DDMSI(
			groupId, classNameId, DDMStructureId);

		return remove(journalArticle);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and classNameId = &#63; and DDMStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param DDMStructureId the ddm structure ID
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_C_DDMSI(
		long groupId, long classNameId, long DDMStructureId) {

		return _uniquePersistenceFinderByG_C_DDMSI.count(
			finderCache, new Object[] {groupId, classNameId, DDMStructureId});
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_C_DDMTK;

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and classNameId = &#63; and DDMTemplateKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param DDMTemplateKey the ddm template key
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_C_DDMTK(
		long groupId, long classNameId, String DDMTemplateKey, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_DDMTK.find(
			finderCache, new Object[] {groupId, classNameId, DDMTemplateKey},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and classNameId = &#63; and DDMTemplateKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param DDMTemplateKey the ddm template key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_C_DDMTK_First(
			long groupId, long classNameId, String DDMTemplateKey,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_C_DDMTK.findFirst(
			finderCache, new Object[] {groupId, classNameId, DDMTemplateKey},
			orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and classNameId = &#63; and DDMTemplateKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param DDMTemplateKey the ddm template key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_C_DDMTK_First(
		long groupId, long classNameId, String DDMTemplateKey,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_C_DDMTK.fetchFirst(
			finderCache, new Object[] {groupId, classNameId, DDMTemplateKey},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and classNameId = &#63; and DDMTemplateKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param DDMTemplateKey the ddm template key
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_C_DDMTK(
		long groupId, long classNameId, String DDMTemplateKey, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_C_DDMTK.filterFind(
			finderCache, new Object[] {groupId, classNameId, DDMTemplateKey},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and classNameId = &#63; and DDMTemplateKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param DDMTemplateKey the ddm template key
	 */
	@Override
	public void removeByG_C_DDMTK(
		long groupId, long classNameId, String DDMTemplateKey) {

		_collectionPersistenceFinderByG_C_DDMTK.remove(
			finderCache, new Object[] {groupId, classNameId, DDMTemplateKey});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and classNameId = &#63; and DDMTemplateKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param DDMTemplateKey the ddm template key
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_C_DDMTK(
		long groupId, long classNameId, String DDMTemplateKey) {

		return _collectionPersistenceFinderByG_C_DDMTK.count(
			finderCache, new Object[] {groupId, classNameId, DDMTemplateKey});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and DDMTemplateKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param DDMTemplateKey the ddm template key
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_DDMTK(
		long groupId, long classNameId, String DDMTemplateKey) {

		return _collectionPersistenceFinderByG_C_DDMTK.filterCount(
			finderCache, new Object[] {groupId, classNameId, DDMTemplateKey},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_C_L;

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_C_L(
		long groupId, long classNameId, String layoutUuid, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_L.find(
			finderCache, new Object[] {groupId, classNameId, layoutUuid}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and classNameId = &#63; and layoutUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_C_L_First(
			long groupId, long classNameId, String layoutUuid,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_C_L.findFirst(
			finderCache, new Object[] {groupId, classNameId, layoutUuid},
			orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and classNameId = &#63; and layoutUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_C_L_First(
		long groupId, long classNameId, String layoutUuid,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_C_L.fetchFirst(
			finderCache, new Object[] {groupId, classNameId, layoutUuid},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and classNameId = &#63; and layoutUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_C_L(
		long groupId, long classNameId, String layoutUuid, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_C_L.filterFind(
			finderCache, new Object[] {groupId, classNameId, layoutUuid}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 */
	@Override
	public void removeByG_C_L(
		long groupId, long classNameId, String layoutUuid) {

		_collectionPersistenceFinderByG_C_L.remove(
			finderCache, new Object[] {groupId, classNameId, layoutUuid});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_C_L(long groupId, long classNameId, String layoutUuid) {
		return _collectionPersistenceFinderByG_C_L.count(
			finderCache, new Object[] {groupId, classNameId, layoutUuid});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and layoutUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_L(
		long groupId, long classNameId, String layoutUuid) {

		return _collectionPersistenceFinderByG_C_L.filterCount(
			finderCache, new Object[] {groupId, classNameId, layoutUuid},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_C_NotL;

	/**
	 * Returns all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @return the matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_C_NotL(
		long groupId, long classNameId, String layoutUuid) {

		return findByG_C_NotL(
			groupId, classNameId, layoutUuid, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_C_NotL(
		long groupId, long classNameId, String layoutUuid, int start, int end) {

		return findByG_C_NotL(
			groupId, classNameId, layoutUuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_C_NotL(
		long groupId, long classNameId, String layoutUuid, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return findByG_C_NotL(
			groupId, classNameId, layoutUuid, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_C_NotL(
		long groupId, long classNameId, String layoutUuid, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_NotL.find(
			finderCache,
			new Object[] {groupId, classNameId, new String[] {layoutUuid}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_C_NotL_First(
			long groupId, long classNameId, String layoutUuid,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		JournalArticle journalArticle = fetchByG_C_NotL_First(
			groupId, classNameId, layoutUuid, orderByComparator);

		if (journalArticle != null) {
			return journalArticle;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", layoutUuid!=");
		sb.append(layoutUuid);

		sb.append("}");

		throw new NoSuchArticleException(sb.toString());
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_C_NotL_First(
		long groupId, long classNameId, String layoutUuid,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_C_NotL.fetchFirst(
			finderCache,
			new Object[] {groupId, classNameId, new String[] {layoutUuid}},
			orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @return the matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_C_NotL(
		long groupId, long classNameId, String layoutUuid) {

		return filterFindByG_C_NotL(
			groupId, classNameId, layoutUuid, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_C_NotL(
		long groupId, long classNameId, String layoutUuid, int start, int end) {

		return filterFindByG_C_NotL(
			groupId, classNameId, layoutUuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_C_NotL(
		long groupId, long classNameId, String layoutUuid, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_C_NotL.filterFind(
			finderCache,
			new Object[] {groupId, classNameId, new String[] {layoutUuid}},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; all &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuids the layout uuids
	 * @return the matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_C_NotL(
		long groupId, long classNameId, String[] layoutUuids) {

		return filterFindByG_C_NotL(
			groupId, classNameId, layoutUuids, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; all &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuids the layout uuids
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_C_NotL(
		long groupId, long classNameId, String[] layoutUuids, int start,
		int end) {

		return filterFindByG_C_NotL(
			groupId, classNameId, layoutUuids, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; all &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuids the layout uuids
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_C_NotL(
		long groupId, long classNameId, String[] layoutUuids, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_C_NotL.filterFind(
			finderCache,
			new Object[] {
				groupId, classNameId, ArrayUtil.sortedUnique(layoutUuids)
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; all &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuids the layout uuids
	 * @return the matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_C_NotL(
		long groupId, long classNameId, String[] layoutUuids) {

		return findByG_C_NotL(
			groupId, classNameId, layoutUuids, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; all &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuids the layout uuids
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_C_NotL(
		long groupId, long classNameId, String[] layoutUuids, int start,
		int end) {

		return findByG_C_NotL(
			groupId, classNameId, layoutUuids, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; all &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuids the layout uuids
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_C_NotL(
		long groupId, long classNameId, String[] layoutUuids, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator) {

		return findByG_C_NotL(
			groupId, classNameId, layoutUuids, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuids the layout uuids
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_C_NotL(
		long groupId, long classNameId, String[] layoutUuids, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_NotL.find(
			finderCache,
			new Object[] {
				groupId, classNameId, ArrayUtil.sortedUnique(layoutUuids)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 */
	@Override
	public void removeByG_C_NotL(
		long groupId, long classNameId, String layoutUuid) {

		_collectionPersistenceFinderByG_C_NotL.remove(
			finderCache,
			new Object[] {groupId, classNameId, new String[] {layoutUuid}});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_C_NotL(
		long groupId, long classNameId, String layoutUuid) {

		return _collectionPersistenceFinderByG_C_NotL.count(
			finderCache,
			new Object[] {groupId, classNameId, new String[] {layoutUuid}});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; all &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuids the layout uuids
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_C_NotL(
		long groupId, long classNameId, String[] layoutUuids) {

		return _collectionPersistenceFinderByG_C_NotL.count(
			finderCache,
			new Object[] {
				groupId, classNameId, ArrayUtil.sortedUnique(layoutUuids)
			});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_NotL(
		long groupId, long classNameId, String layoutUuid) {

		return _collectionPersistenceFinderByG_C_NotL.filterCount(
			finderCache,
			new Object[] {groupId, classNameId, new String[] {layoutUuid}},
			groupId);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; all &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuids the layout uuids
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_NotL(
		long groupId, long classNameId, String[] layoutUuids) {

		return _collectionPersistenceFinderByG_C_NotL.filterCount(
			finderCache,
			new Object[] {
				groupId, classNameId, ArrayUtil.sortedUnique(layoutUuids)
			},
			groupId);
	}

	private UniquePersistenceFinder<JournalArticle, NoSuchArticleException>
		_uniquePersistenceFinderByG_A_V;

	/**
	 * Returns the journal article where groupId = &#63; and articleId = &#63; and version = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param version the version
	 * @return the matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_A_V(
			long groupId, String articleId, double version)
		throws NoSuchArticleException {

		return _uniquePersistenceFinderByG_A_V.find(
			finderCache, new Object[] {groupId, articleId, version});
	}

	/**
	 * Returns the journal article where groupId = &#63; and articleId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_A_V(
		long groupId, String articleId, double version,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_A_V.fetch(
			finderCache, new Object[] {groupId, articleId, version},
			useFinderCache);
	}

	/**
	 * Removes the journal article where groupId = &#63; and articleId = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param version the version
	 * @return the journal article that was removed
	 */
	@Override
	public JournalArticle removeByG_A_V(
			long groupId, String articleId, double version)
		throws NoSuchArticleException {

		JournalArticle journalArticle = findByG_A_V(
			groupId, articleId, version);

		return remove(journalArticle);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and articleId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param version the version
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_A_V(long groupId, String articleId, double version) {
		return _uniquePersistenceFinderByG_A_V.count(
			finderCache, new Object[] {groupId, articleId, version});
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_A_ST;

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and articleId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_A_ST(
		long groupId, String articleId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_A_ST.find(
			finderCache, new Object[] {groupId, articleId, new int[] {status}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and articleId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_A_ST_First(
			long groupId, String articleId, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		JournalArticle journalArticle = fetchByG_A_ST_First(
			groupId, articleId, status, orderByComparator);

		if (journalArticle != null) {
			return journalArticle;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", articleId=");
		sb.append(articleId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchArticleException(sb.toString());
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and articleId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_A_ST_First(
		long groupId, String articleId, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_A_ST.fetchFirst(
			finderCache, new Object[] {groupId, articleId, new int[] {status}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and articleId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_A_ST(
		long groupId, String articleId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_A_ST.filterFind(
			finderCache, new Object[] {groupId, articleId, new int[] {status}},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permission to view where groupId = &#63; and articleId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_A_ST(
		long groupId, String articleId, int[] statuses, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_A_ST.filterFind(
			finderCache,
			new Object[] {groupId, articleId, ArrayUtil.sortedUnique(statuses)},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and articleId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_A_ST(
		long groupId, String articleId, int[] statuses, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_A_ST.find(
			finderCache,
			new Object[] {groupId, articleId, ArrayUtil.sortedUnique(statuses)},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and articleId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 */
	@Override
	public void removeByG_A_ST(long groupId, String articleId, int status) {
		_collectionPersistenceFinderByG_A_ST.remove(
			finderCache, new Object[] {groupId, articleId, new int[] {status}});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and articleId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_A_ST(long groupId, String articleId, int status) {
		return _collectionPersistenceFinderByG_A_ST.count(
			finderCache, new Object[] {groupId, articleId, new int[] {status}});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and articleId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param statuses the statuses
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_A_ST(long groupId, String articleId, int[] statuses) {
		return _collectionPersistenceFinderByG_A_ST.count(
			finderCache,
			new Object[] {
				groupId, articleId, ArrayUtil.sortedUnique(statuses)
			});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and articleId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_A_ST(long groupId, String articleId, int status) {
		return _collectionPersistenceFinderByG_A_ST.filterCount(
			finderCache, new Object[] {groupId, articleId, new int[] {status}},
			groupId);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and articleId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param statuses the statuses
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_A_ST(
		long groupId, String articleId, int[] statuses) {

		return _collectionPersistenceFinderByG_A_ST.filterCount(
			finderCache,
			new Object[] {groupId, articleId, ArrayUtil.sortedUnique(statuses)},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_A_NotST;

	/**
	 * Returns all the journal articles where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @return the matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_A_NotST(
		long groupId, String articleId, int status) {

		return findByG_A_NotST(
			groupId, articleId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_A_NotST(
		long groupId, String articleId, int status, int start, int end) {

		return findByG_A_NotST(groupId, articleId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_A_NotST(
		long groupId, String articleId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return findByG_A_NotST(
			groupId, articleId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_A_NotST(
		long groupId, String articleId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_A_NotST.find(
			finderCache, new Object[] {groupId, articleId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_A_NotST_First(
			long groupId, String articleId, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_A_NotST.findFirst(
			finderCache, new Object[] {groupId, articleId, status},
			orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_A_NotST_First(
		long groupId, String articleId, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_A_NotST.fetchFirst(
			finderCache, new Object[] {groupId, articleId, status},
			orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @return the matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_A_NotST(
		long groupId, String articleId, int status) {

		return filterFindByG_A_NotST(
			groupId, articleId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_A_NotST(
		long groupId, String articleId, int status, int start, int end) {

		return filterFindByG_A_NotST(
			groupId, articleId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_A_NotST(
		long groupId, String articleId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_A_NotST.filterFind(
			finderCache, new Object[] {groupId, articleId, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and articleId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 */
	@Override
	public void removeByG_A_NotST(long groupId, String articleId, int status) {
		_collectionPersistenceFinderByG_A_NotST.remove(
			finderCache, new Object[] {groupId, articleId, status});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_A_NotST(long groupId, String articleId, int status) {
		return _collectionPersistenceFinderByG_A_NotST.count(
			finderCache, new Object[] {groupId, articleId, status});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_A_NotST(
		long groupId, String articleId, int status) {

		return _collectionPersistenceFinderByG_A_NotST.filterCount(
			finderCache, new Object[] {groupId, articleId, status}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_UT_ST;

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_UT_ST(
		long groupId, String urlTitle, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_UT_ST.find(
			finderCache, new Object[] {groupId, urlTitle, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_UT_ST_First(
			long groupId, String urlTitle, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_UT_ST.findFirst(
			finderCache, new Object[] {groupId, urlTitle, status},
			orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_UT_ST_First(
		long groupId, String urlTitle, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_UT_ST.fetchFirst(
			finderCache, new Object[] {groupId, urlTitle, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_UT_ST(
		long groupId, String urlTitle, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_UT_ST.filterFind(
			finderCache, new Object[] {groupId, urlTitle, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and urlTitle = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param status the status
	 */
	@Override
	public void removeByG_UT_ST(long groupId, String urlTitle, int status) {
		_collectionPersistenceFinderByG_UT_ST.remove(
			finderCache, new Object[] {groupId, urlTitle, status});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_UT_ST(long groupId, String urlTitle, int status) {
		return _collectionPersistenceFinderByG_UT_ST.count(
			finderCache, new Object[] {groupId, urlTitle, status});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_UT_ST(long groupId, String urlTitle, int status) {
		return _collectionPersistenceFinderByG_UT_ST.filterCount(
			finderCache, new Object[] {groupId, urlTitle, status}, groupId);
	}

	private CollectionPersistenceFinder<JournalArticle, NoSuchArticleException>
		_collectionPersistenceFinderByC_V_ST;

	/**
	 * Returns an ordered range of all the journal articles where companyId = &#63; and version = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByC_V_ST(
		long companyId, double version, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_V_ST.find(
			finderCache, new Object[] {companyId, version, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where companyId = &#63; and version = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByC_V_ST_First(
			long companyId, double version, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByC_V_ST.findFirst(
			finderCache, new Object[] {companyId, version, status},
			orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where companyId = &#63; and version = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByC_V_ST_First(
		long companyId, double version, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByC_V_ST.fetchFirst(
			finderCache, new Object[] {companyId, version, status},
			orderByComparator);
	}

	/**
	 * Removes all the journal articles where companyId = &#63; and version = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param status the status
	 */
	@Override
	public void removeByC_V_ST(long companyId, double version, int status) {
		_collectionPersistenceFinderByC_V_ST.remove(
			finderCache, new Object[] {companyId, version, status});
	}

	/**
	 * Returns the number of journal articles where companyId = &#63; and version = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByC_V_ST(long companyId, double version, int status) {
		return _collectionPersistenceFinderByC_V_ST.count(
			finderCache, new Object[] {companyId, version, status});
	}

	private FilterCollectionPersistenceFinder
		<JournalArticle, NoSuchArticleException>
			_collectionPersistenceFinderByG_F_C_NotST;

	/**
	 * Returns all the journal articles where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @return the matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status) {

		return findByG_F_C_NotST(
			groupId, folderId, classNameId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status, int start,
		int end) {

		return findByG_F_C_NotST(
			groupId, folderId, classNameId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator) {

		return findByG_F_C_NotST(
			groupId, folderId, classNameId, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	@Override
	public List<JournalArticle> findByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_F_C_NotST.find(
			finderCache, new Object[] {groupId, folderId, classNameId, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle findByG_F_C_NotST_First(
			long groupId, long folderId, long classNameId, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws NoSuchArticleException {

		return _collectionPersistenceFinderByG_F_C_NotST.findFirst(
			finderCache, new Object[] {groupId, folderId, classNameId, status},
			orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchByG_F_C_NotST_First(
		long groupId, long folderId, long classNameId, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_F_C_NotST.fetchFirst(
			finderCache, new Object[] {groupId, folderId, classNameId, status},
			orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @return the matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status) {

		return filterFindByG_F_C_NotST(
			groupId, folderId, classNameId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status, int start,
		int end) {

		return filterFindByG_F_C_NotST(
			groupId, folderId, classNameId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	@Override
	public List<JournalArticle> filterFindByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator) {

		return _collectionPersistenceFinderByG_F_C_NotST.filterFind(
			finderCache, new Object[] {groupId, folderId, classNameId, status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 */
	@Override
	public void removeByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status) {

		_collectionPersistenceFinderByG_F_C_NotST.remove(
			finderCache, new Object[] {groupId, folderId, classNameId, status});
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	@Override
	public int countByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status) {

		return _collectionPersistenceFinderByG_F_C_NotST.count(
			finderCache, new Object[] {groupId, folderId, classNameId, status});
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @return the number of matching journal articles that the user has permission to view
	 */
	@Override
	public int filterCountByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status) {

		return _collectionPersistenceFinderByG_F_C_NotST.filterCount(
			finderCache, new Object[] {groupId, folderId, classNameId, status},
			groupId);
	}

	public JournalArticlePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("id", "id_");

		setDBColumnNames(dbColumnNames);

		setModelClass(JournalArticle.class);

		setModelImplClass(JournalArticleImpl.class);
		setModelPKClass(long.class);

		setTable(JournalArticleTable.INSTANCE);
	}

	/**
	 * Creates a new journal article with the primary key. Does not add the journal article to the database.
	 *
	 * @param id the primary key for the new journal article
	 * @return the new journal article
	 */
	@Override
	public JournalArticle create(long id) {
		JournalArticle journalArticle = new JournalArticleImpl();

		journalArticle.setNew(true);
		journalArticle.setPrimaryKey(id);

		String uuid = PortalUUIDUtil.generate();

		journalArticle.setUuid(uuid);

		journalArticle.setCompanyId(CompanyThreadLocal.getCompanyId());

		return journalArticle;
	}

	/**
	 * Removes the journal article with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param id the primary key of the journal article
	 * @return the journal article that was removed
	 * @throws NoSuchArticleException if a journal article with the primary key could not be found
	 */
	@Override
	public JournalArticle remove(long id) throws NoSuchArticleException {
		return remove((Serializable)id);
	}

	@Override
	protected JournalArticle removeImpl(JournalArticle journalArticle) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(journalArticle)) {
				journalArticle = (JournalArticle)session.get(
					JournalArticleImpl.class,
					journalArticle.getPrimaryKeyObj());
			}

			if ((journalArticle != null) &&
				ctPersistenceHelper.isRemove(journalArticle)) {

				session.delete(journalArticle);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (journalArticle != null) {
			clearCache(journalArticle);
		}

		return journalArticle;
	}

	@Override
	public JournalArticle updateImpl(JournalArticle journalArticle) {
		boolean isNew = journalArticle.isNew();

		if (!(journalArticle instanceof JournalArticleModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(journalArticle.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					journalArticle);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in journalArticle proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom JournalArticle implementation " +
					journalArticle.getClass());
		}

		JournalArticleModelImpl journalArticleModelImpl =
			(JournalArticleModelImpl)journalArticle;

		if (Validator.isNull(journalArticle.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			journalArticle.setUuid(uuid);
		}

		if (Validator.isNull(journalArticle.getExternalReferenceCode())) {
			journalArticle.setExternalReferenceCode(journalArticle.getUuid());
		}
		else {
			if (!Objects.equals(
					journalArticleModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					journalArticle.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = journalArticle.getCompanyId();

					long groupId = journalArticle.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = journalArticle.getPrimaryKey();
					}

					try {
						journalArticle.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								JournalArticle.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								journalArticle.getExternalReferenceCode(),
								null));
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

		if (isNew && (journalArticle.getCreateDate() == null)) {
			if (serviceContext == null) {
				journalArticle.setCreateDate(date);
			}
			else {
				journalArticle.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!journalArticleModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				journalArticle.setModifiedDate(date);
			}
			else {
				journalArticle.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(journalArticle)) {
				if (!isNew) {
					session.evict(
						JournalArticleImpl.class,
						journalArticle.getPrimaryKeyObj());
				}

				session.save(journalArticle);
			}
			else {
				journalArticle = (JournalArticle)session.merge(journalArticle);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(journalArticle, false);

		if (isNew) {
			journalArticle.setNew(false);
		}

		journalArticle.resetOriginalValues();

		return journalArticle;
	}

	/**
	 * Returns the journal article with the primary key or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param id the primary key of the journal article
	 * @return the journal article
	 * @throws NoSuchArticleException if a journal article with the primary key could not be found
	 */
	@Override
	public JournalArticle findByPrimaryKey(long id)
		throws NoSuchArticleException {

		return findByPrimaryKey((Serializable)id);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the journal article with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param id the primary key of the journal article
	 * @return the journal article, or <code>null</code> if a journal article with the primary key could not be found
	 */
	@Override
	public JournalArticle fetchByPrimaryKey(long id) {
		return fetchByPrimaryKey((Serializable)id);
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
		return "id_";
	}

	@Override
	protected String getPKFieldName() {
		return "id";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_JOURNALARTICLE;
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
		return JournalArticleModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "JournalArticle";
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
		ctMergeColumnNames.add("folderId");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("treePath");
		ctMergeColumnNames.add("articleId");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("urlTitle");
		ctMergeColumnNames.add("DDMStructureId");
		ctMergeColumnNames.add("DDMTemplateKey");
		ctMergeColumnNames.add("defaultLanguageId");
		ctMergeColumnNames.add("layoutUuid");
		ctMergeColumnNames.add("displayDate");
		ctMergeColumnNames.add("expirationDate");
		ctMergeColumnNames.add("reviewDate");
		ctMergeColumnNames.add("indexable");
		ctMergeColumnNames.add("smallImage");
		ctMergeColumnNames.add("smallImageId");
		ctMergeColumnNames.add("smallImageSource");
		ctMergeColumnNames.add("smallImageURL");
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
			CTColumnResolutionType.PK, Collections.singleton("id_"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "externalReferenceCode", "version"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "articleId", "version"});
	}

	/**
	 * Initializes the journal article persistence.
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
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "resourcePrimKey",
					FinderColumn.Type.LONG, "=", true, true,
					JournalArticle::getResourcePrimKey));

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
			_SQL_SELECT_JOURNALARTICLE_WHERE, _SQL_COUNT_JOURNALARTICLE_WHERE,
			JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"journalArticle.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, JournalArticle::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(JournalArticle::getUuid),
				JournalArticle::getGroupId),
			_SQL_SELECT_JOURNALARTICLE_WHERE, "",
			new FinderColumn<>(
				"journalArticle.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, JournalArticle::getUuid),
			new FinderColumn<>(
				"journalArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, JournalArticle::getGroupId));

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
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					JournalArticle::getUuid),
				new FinderColumn<>(
					"journalArticle.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getCompanyId));

		_collectionPersistenceFinderByGroupId =
			new FilterCollectionPersistenceFinder<>(
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
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId));

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
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getCompanyId));

		_collectionPersistenceFinderByDDMStructureId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByDDMStructureId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"DDMStructureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByDDMStructureId", new String[] {Long.class.getName()},
					new String[] {"DDMStructureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByDDMStructureId",
					new String[] {Long.class.getName()},
					new String[] {"DDMStructureId"}, false),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "DDMStructureId", FinderColumn.Type.LONG,
					"=", true, true, JournalArticle::getDDMStructureId));

		_collectionPersistenceFinderByDDMTemplateKey =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByDDMTemplateKey",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"DDMTemplateKey"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByDDMTemplateKey",
					new String[] {String.class.getName()},
					new String[] {"DDMTemplateKey"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByDDMTemplateKey",
					new String[] {String.class.getName()},
					new String[] {"DDMTemplateKey"}, 0, 1, false, null),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "DDMTemplateKey",
					FinderColumn.Type.STRING, "=", true, true,
					JournalArticle::getDDMTemplateKey));

		_collectionPersistenceFinderByLayoutUuid =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLayoutUuid",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"layoutUuid"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLayoutUuid", new String[] {String.class.getName()},
					new String[] {"layoutUuid"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLayoutUuid", new String[] {String.class.getName()},
					new String[] {"layoutUuid"}, 0, 1, false, null),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "layoutUuid", FinderColumn.Type.STRING,
					"=", true, true, JournalArticle::getLayoutUuid));

		_collectionPersistenceFinderBySmallImageId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findBySmallImageId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"smallImageId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findBySmallImageId", new String[] {Long.class.getName()},
					new String[] {"smallImageId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countBySmallImageId", new String[] {Long.class.getName()},
					new String[] {"smallImageId"}, false),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "smallImageId", FinderColumn.Type.LONG,
					"=", true, true, JournalArticle::getSmallImageId));

		_collectionPersistenceFinderByR_I = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_I",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"resourcePrimKey", "indexable"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_I",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"resourcePrimKey", "indexable"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_I",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"resourcePrimKey", "indexable"}, false),
			_SQL_SELECT_JOURNALARTICLE_WHERE, _SQL_COUNT_JOURNALARTICLE_WHERE,
			JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"journalArticle.", "resourcePrimKey", FinderColumn.Type.LONG,
				"=", true, true, JournalArticle::getResourcePrimKey),
			new FinderColumn<>(
				"journalArticle.", "indexable", FinderColumn.Type.BOOLEAN, "=",
				true, true, JournalArticle::isIndexable));

		_collectionPersistenceFinderByR_ST = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_ST",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"resourcePrimKey", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_ST",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"resourcePrimKey", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByR_ST",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"resourcePrimKey", "status"}, false),
			_SQL_SELECT_JOURNALARTICLE_WHERE, _SQL_COUNT_JOURNALARTICLE_WHERE,
			JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"journalArticle.", "resourcePrimKey", FinderColumn.Type.LONG,
				"=", true, true, JournalArticle::getResourcePrimKey),
			new ArrayableFinderColumn<>(
				"journalArticle.", "status", FinderColumn.Type.INTEGER, "=",
				false, true, true, JournalArticle::getStatus));

		_collectionPersistenceFinderByG_U =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "userId"}, false),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new FinderColumn<>(
					"journalArticle.", "userId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getUserId));

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
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new FinderColumn<>(
					"journalArticle.", "externalReferenceCode",
					FinderColumn.Type.STRING, "=", true, true,
					JournalArticle::getExternalReferenceCode));

		_collectionPersistenceFinderByG_F =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_F",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "folderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_F",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "folderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_F",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "folderId"}, false),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new ArrayableFinderColumn<>(
					"journalArticle.", "folderId", FinderColumn.Type.LONG, "=",
					false, true, true, JournalArticle::getFolderId));

		_collectionPersistenceFinderByG_A =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_A",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "articleId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_A",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "articleId"}, 0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_A",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "articleId"}, 0, 2, false, null),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new FinderColumn<>(
					"journalArticle.", "articleId", FinderColumn.Type.STRING,
					"=", true, true, JournalArticle::getArticleId));

		_collectionPersistenceFinderByG_UT =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_UT",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "urlTitle"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_UT",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "urlTitle"}, 0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_UT",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "urlTitle"}, 0, 2, false, null),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new FinderColumn<>(
					"journalArticle.", "urlTitle", FinderColumn.Type.STRING,
					"=", true, true, JournalArticle::getUrlTitle));

		_collectionPersistenceFinderByG_DDMSI =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_DDMSI",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "DDMStructureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_DDMSI",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "DDMStructureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_DDMSI",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "DDMStructureId"}, false),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new FinderColumn<>(
					"journalArticle.", "DDMStructureId", FinderColumn.Type.LONG,
					"=", true, true, JournalArticle::getDDMStructureId));

		_collectionPersistenceFinderByG_DDMTK =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_DDMTK",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "DDMTemplateKey"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_DDMTK",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "DDMTemplateKey"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_DDMTK",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "DDMTemplateKey"}, 0, 2, false,
					null),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new FinderColumn<>(
					"journalArticle.", "DDMTemplateKey",
					FinderColumn.Type.STRING, "=", true, true,
					JournalArticle::getDDMTemplateKey));

		_collectionPersistenceFinderByG_L =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_L",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "layoutUuid"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_L",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "layoutUuid"}, 0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_L",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "layoutUuid"}, 0, 2, false, null),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new FinderColumn<>(
					"journalArticle.", "layoutUuid", FinderColumn.Type.STRING,
					"=", true, true, JournalArticle::getLayoutUuid));

		_collectionPersistenceFinderByG_ST =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_ST",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_ST",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_ST",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "status"}, false),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new FinderColumn<>(
					"journalArticle.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, JournalArticle::getStatus));

		_collectionPersistenceFinderByC_V = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_V",
				new String[] {
					Long.class.getName(), Double.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "version"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_V",
				new String[] {Long.class.getName(), Double.class.getName()},
				new String[] {"companyId", "version"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_V",
				new String[] {Long.class.getName(), Double.class.getName()},
				new String[] {"companyId", "version"}, false),
			_SQL_SELECT_JOURNALARTICLE_WHERE, _SQL_COUNT_JOURNALARTICLE_WHERE,
			JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"journalArticle.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, JournalArticle::getCompanyId),
			new FinderColumn<>(
				"journalArticle.", "version", FinderColumn.Type.DOUBLE, "=",
				true, true, JournalArticle::getVersion));

		_collectionPersistenceFinderByC_ST = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_ST",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_ST",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"companyId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_ST",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"companyId", "status"}, false),
			_SQL_SELECT_JOURNALARTICLE_WHERE, _SQL_COUNT_JOURNALARTICLE_WHERE,
			JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"journalArticle.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, JournalArticle::getCompanyId),
			new FinderColumn<>(
				"journalArticle.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, JournalArticle::getStatus));

		_collectionPersistenceFinderByC_NotST =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_NotST",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_NotST",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"companyId", "status"}, false),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getCompanyId),
				new FinderColumn<>(
					"journalArticle.", "status", FinderColumn.Type.INTEGER,
					"!=", true, true, JournalArticle::getStatus));

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
			_SQL_SELECT_JOURNALARTICLE_WHERE, _SQL_COUNT_JOURNALARTICLE_WHERE,
			JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"journalArticle.", "displayDate", FinderColumn.Type.DATE, "<",
				true, true, JournalArticle::getDisplayDate),
			new FinderColumn<>(
				"journalArticle.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, JournalArticle::getStatus));

		_collectionPersistenceFinderByR_I_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_I_S",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"resourcePrimKey", "indexable", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_I_S",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName()
				},
				new String[] {"resourcePrimKey", "indexable", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByR_I_S",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName()
				},
				new String[] {"resourcePrimKey", "indexable", "status"}, false),
			_SQL_SELECT_JOURNALARTICLE_WHERE, _SQL_COUNT_JOURNALARTICLE_WHERE,
			JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"journalArticle.", "resourcePrimKey", FinderColumn.Type.LONG,
				"=", true, true, JournalArticle::getResourcePrimKey),
			new FinderColumn<>(
				"journalArticle.", "indexable", FinderColumn.Type.BOOLEAN, "=",
				true, true, JournalArticle::isIndexable),
			new ArrayableFinderColumn<>(
				"journalArticle.", "status", FinderColumn.Type.INTEGER, "=",
				false, true, true, JournalArticle::getStatus));

		_collectionPersistenceFinderByG_U_C =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "userId", "classNameId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "userId", "classNameId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "userId", "classNameId"}, false),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new FinderColumn<>(
					"journalArticle.", "userId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getUserId),
				new FinderColumn<>(
					"journalArticle.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, JournalArticle::getClassNameId));

		_uniquePersistenceFinderByG_ERC_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_ERC_V",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Double.class.getName()
				},
				new String[] {"groupId", "externalReferenceCode", "version"}, 0,
				2, false, JournalArticle::getGroupId,
				convertNullFunction(JournalArticle::getExternalReferenceCode),
				JournalArticle::getVersion),
			_SQL_SELECT_JOURNALARTICLE_WHERE, "",
			new FinderColumn<>(
				"journalArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, JournalArticle::getGroupId),
			new FinderColumn<>(
				"journalArticle.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				JournalArticle::getExternalReferenceCode),
			new FinderColumn<>(
				"journalArticle.", "version", FinderColumn.Type.DOUBLE, "=",
				true, true, JournalArticle::getVersion));

		_collectionPersistenceFinderByG_ERC_ST =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_ERC_ST",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "externalReferenceCode", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_ERC_ST",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "externalReferenceCode", "status"},
					0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_ERC_ST",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "externalReferenceCode", "status"},
					0, 2, false, null),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new FinderColumn<>(
					"journalArticle.", "externalReferenceCode",
					FinderColumn.Type.STRING, "=", true, true,
					JournalArticle::getExternalReferenceCode),
				new ArrayableFinderColumn<>(
					"journalArticle.", "status", FinderColumn.Type.INTEGER, "=",
					false, true, true, JournalArticle::getStatus));

		_collectionPersistenceFinderByG_F_ST =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_F_ST",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "folderId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_F_ST",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "folderId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_F_ST",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "folderId", "status"}, false),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new FinderColumn<>(
					"journalArticle.", "folderId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getFolderId),
				new ArrayableFinderColumn<>(
					"journalArticle.", "status", FinderColumn.Type.INTEGER, "=",
					false, true, true, JournalArticle::getStatus));

		_collectionPersistenceFinderByG_C_C =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "classNameId", "classPK"}, false),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new FinderColumn<>(
					"journalArticle.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, JournalArticle::getClassNameId),
				new FinderColumn<>(
					"journalArticle.", "classPK", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getClassPK));

		_uniquePersistenceFinderByG_C_DDMSI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_C_DDMSI",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "classNameId", "DDMStructureId"}, 0, 0,
				false, JournalArticle::getGroupId,
				JournalArticle::getClassNameId,
				JournalArticle::getDDMStructureId),
			_SQL_SELECT_JOURNALARTICLE_WHERE, "",
			new FinderColumn<>(
				"journalArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, JournalArticle::getGroupId),
			new FinderColumn<>(
				"journalArticle.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, JournalArticle::getClassNameId),
			new FinderColumn<>(
				"journalArticle.", "DDMStructureId", FinderColumn.Type.LONG,
				"=", true, true, JournalArticle::getDDMStructureId));

		_collectionPersistenceFinderByG_C_DDMTK =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_DDMTK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "classNameId", "DDMTemplateKey"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_C_DDMTK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName()
					},
					new String[] {"groupId", "classNameId", "DDMTemplateKey"},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_C_DDMTK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName()
					},
					new String[] {"groupId", "classNameId", "DDMTemplateKey"},
					0, 4, false, null),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new FinderColumn<>(
					"journalArticle.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, JournalArticle::getClassNameId),
				new FinderColumn<>(
					"journalArticle.", "DDMTemplateKey",
					FinderColumn.Type.STRING, "=", true, true,
					JournalArticle::getDDMTemplateKey));

		_collectionPersistenceFinderByG_C_L =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_L",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "classNameId", "layoutUuid"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_L",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName()
					},
					new String[] {"groupId", "classNameId", "layoutUuid"}, 0, 4,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_L",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName()
					},
					new String[] {"groupId", "classNameId", "layoutUuid"}, 0, 4,
					false, null),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new FinderColumn<>(
					"journalArticle.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, JournalArticle::getClassNameId),
				new FinderColumn<>(
					"journalArticle.", "layoutUuid", FinderColumn.Type.STRING,
					"=", true, true, JournalArticle::getLayoutUuid));

		_collectionPersistenceFinderByG_C_NotL =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_NotL",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "classNameId", "layoutUuid"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_C_NotL",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName()
					},
					new String[] {"groupId", "classNameId", "layoutUuid"},
					false),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new FinderColumn<>(
					"journalArticle.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, JournalArticle::getClassNameId),
				new ArrayableFinderColumn<>(
					"journalArticle.", "layoutUuid", FinderColumn.Type.STRING,
					"!=", true, true, true, JournalArticle::getLayoutUuid));

		_uniquePersistenceFinderByG_A_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_A_V",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Double.class.getName()
				},
				new String[] {"groupId", "articleId", "version"}, 0, 2, false,
				JournalArticle::getGroupId,
				convertNullFunction(JournalArticle::getArticleId),
				JournalArticle::getVersion),
			_SQL_SELECT_JOURNALARTICLE_WHERE, "",
			new FinderColumn<>(
				"journalArticle.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, JournalArticle::getGroupId),
			new FinderColumn<>(
				"journalArticle.", "articleId", FinderColumn.Type.STRING, "=",
				true, true, JournalArticle::getArticleId),
			new FinderColumn<>(
				"journalArticle.", "version", FinderColumn.Type.DOUBLE, "=",
				true, true, JournalArticle::getVersion));

		_collectionPersistenceFinderByG_A_ST =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_A_ST",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "articleId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_A_ST",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "articleId", "status"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_A_ST",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "articleId", "status"}, 0, 2,
					false, null),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new FinderColumn<>(
					"journalArticle.", "articleId", FinderColumn.Type.STRING,
					"=", true, true, JournalArticle::getArticleId),
				new ArrayableFinderColumn<>(
					"journalArticle.", "status", FinderColumn.Type.INTEGER, "=",
					false, true, true, JournalArticle::getStatus));

		_collectionPersistenceFinderByG_A_NotST =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_A_NotST",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "articleId", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_A_NotST",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "articleId", "status"}, false),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new FinderColumn<>(
					"journalArticle.", "articleId", FinderColumn.Type.STRING,
					"=", true, true, JournalArticle::getArticleId),
				new FinderColumn<>(
					"journalArticle.", "status", FinderColumn.Type.INTEGER,
					"!=", true, true, JournalArticle::getStatus));

		_collectionPersistenceFinderByG_UT_ST =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_UT_ST",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "urlTitle", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_UT_ST",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "urlTitle", "status"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_UT_ST",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "urlTitle", "status"}, 0, 2, false,
					null),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new FinderColumn<>(
					"journalArticle.", "urlTitle", FinderColumn.Type.STRING,
					"=", true, true, JournalArticle::getUrlTitle),
				new FinderColumn<>(
					"journalArticle.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, JournalArticle::getStatus));

		_collectionPersistenceFinderByC_V_ST =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_V_ST",
					new String[] {
						Long.class.getName(), Double.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "version", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_V_ST",
					new String[] {
						Long.class.getName(), Double.class.getName(),
						Integer.class.getName()
					},
					new String[] {"companyId", "version", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_V_ST",
					new String[] {
						Long.class.getName(), Double.class.getName(),
						Integer.class.getName()
					},
					new String[] {"companyId", "version", "status"}, false),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getCompanyId),
				new FinderColumn<>(
					"journalArticle.", "version", FinderColumn.Type.DOUBLE, "=",
					true, true, JournalArticle::getVersion),
				new FinderColumn<>(
					"journalArticle.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, JournalArticle::getStatus));

		_collectionPersistenceFinderByG_F_C_NotST =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_F_C_NotST",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "folderId", "classNameId", "status"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_F_C_NotST",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "folderId", "classNameId", "status"
					},
					false),
				_SQL_SELECT_JOURNALARTICLE_WHERE,
				_SQL_COUNT_JOURNALARTICLE_WHERE,
				JournalArticleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalArticle.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getGroupId),
				new FinderColumn<>(
					"journalArticle.", "folderId", FinderColumn.Type.LONG, "=",
					true, true, JournalArticle::getFolderId),
				new FinderColumn<>(
					"journalArticle.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, JournalArticle::getClassNameId),
				new FinderColumn<>(
					"journalArticle.", "status", FinderColumn.Type.INTEGER,
					"!=", true, true, JournalArticle::getStatus));

		JournalArticleUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		JournalArticleUtil.setPersistence(null);

		entityCache.removeCache(JournalArticleImpl.class.getName());
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
		JournalArticleModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_JOURNALARTICLE =
		"SELECT journalArticle FROM JournalArticle journalArticle";

	private static final String _SQL_SELECT_JOURNALARTICLE_WHERE =
		"SELECT journalArticle FROM JournalArticle journalArticle WHERE ";

	private static final String _SQL_COUNT_JOURNALARTICLE_WHERE =
		"SELECT COUNT(journalArticle) FROM JournalArticle journalArticle WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No JournalArticle exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticlePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "id"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-881466728