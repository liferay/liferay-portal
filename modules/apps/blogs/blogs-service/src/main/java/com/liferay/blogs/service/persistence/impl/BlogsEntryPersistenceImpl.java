/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.service.persistence.impl;

import com.liferay.blogs.exception.DuplicateBlogsEntryExternalReferenceCodeException;
import com.liferay.blogs.exception.NoSuchEntryException;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.model.BlogsEntryTable;
import com.liferay.blogs.model.impl.BlogsEntryImpl;
import com.liferay.blogs.model.impl.BlogsEntryModelImpl;
import com.liferay.blogs.service.persistence.BlogsEntryPersistence;
import com.liferay.blogs.service.persistence.BlogsEntryUtil;
import com.liferay.blogs.service.persistence.impl.constants.BlogsPersistenceConstants;
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
 * The persistence implementation for the blogs entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = BlogsEntryPersistence.class)
public class BlogsEntryPersistenceImpl
	extends BasePersistenceImpl<BlogsEntry, NoSuchEntryException>
	implements BlogsEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>BlogsEntryUtil</code> to access the blogs entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		BlogsEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the blogs entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByUuid_First(
			String uuid, OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByUuid_First(
		String uuid, OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of blogs entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<BlogsEntry, NoSuchEntryException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the blogs entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the blogs entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the blogs entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the blogs entry that was removed
	 */
	@Override
	public BlogsEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByUUID_G(uuid, groupId);

		return remove(blogsEntry);
	}

	/**
	 * Returns the number of blogs entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the blogs entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of blogs entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByGroupId_First(
			long groupId, OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByGroupId_First(
		long groupId, OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupId}, groupId);
	}

	private CollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByCompanyId_First(
			long companyId, OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private UniquePersistenceFinder<BlogsEntry, NoSuchEntryException>
		_uniquePersistenceFinderByG_UT;

	/**
	 * Returns the blogs entry where groupId = &#63; and urlTitle = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @return the matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_UT(long groupId, String urlTitle)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByG_UT.find(
			finderCache, new Object[] {groupId, urlTitle});
	}

	/**
	 * Returns the blogs entry where groupId = &#63; and urlTitle = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_UT(
		long groupId, String urlTitle, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_UT.fetch(
			finderCache, new Object[] {groupId, urlTitle}, useFinderCache);
	}

	/**
	 * Removes the blogs entry where groupId = &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @return the blogs entry that was removed
	 */
	@Override
	public BlogsEntry removeByG_UT(long groupId, String urlTitle)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByG_UT(groupId, urlTitle);

		return remove(blogsEntry);
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_UT(long groupId, String urlTitle) {
		return _uniquePersistenceFinderByG_UT.count(
			finderCache, new Object[] {groupId, urlTitle});
	}

	private FilterCollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_LtD;

	/**
	 * Returns all the blogs entries where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD(long groupId, Date displayDate) {
		return findByG_LtD(
			groupId, displayDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD(
		long groupId, Date displayDate, int start, int end) {

		return findByG_LtD(groupId, displayDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD(
		long groupId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_LtD(
			groupId, displayDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD(
		long groupId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LtD.find(
			finderCache, new Object[] {groupId, displayDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_LtD_First(
			long groupId, Date displayDate,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_LtD.findFirst(
			finderCache, new Object[] {groupId, displayDate},
			orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_LtD_First(
		long groupId, Date displayDate,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LtD.fetchFirst(
			finderCache, new Object[] {groupId, displayDate},
			orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_LtD(long groupId, Date displayDate) {
		return filterFindByG_LtD(
			groupId, displayDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_LtD(
		long groupId, Date displayDate, int start, int end) {

		return filterFindByG_LtD(groupId, displayDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_LtD(
		long groupId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LtD.filterFind(
			finderCache, new Object[] {groupId, displayDate}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and displayDate &lt; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 */
	@Override
	public void removeByG_LtD(long groupId, Date displayDate) {
		_collectionPersistenceFinderByG_LtD.remove(
			finderCache, new Object[] {groupId, displayDate});
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_LtD(long groupId, Date displayDate) {
		return _collectionPersistenceFinderByG_LtD.count(
			finderCache, new Object[] {groupId, displayDate});
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_LtD(long groupId, Date displayDate) {
		return _collectionPersistenceFinderByG_LtD.filterCount(
			finderCache, new Object[] {groupId, displayDate}, groupId);
	}

	private FilterCollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_S;

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S.find(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_S_First(
			long groupId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_S.findFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_S.fetchFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_S.filterFind(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and status = &#63; from the database.
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
	 * Returns the number of blogs entries where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_S(long groupId, int status) {
		return _collectionPersistenceFinderByG_S.count(
			finderCache, new Object[] {groupId, status});
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_S(long groupId, int status) {
		return _collectionPersistenceFinderByG_S.filterCount(
			finderCache, new Object[] {groupId, status}, groupId);
	}

	private FilterCollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_NotS;

	/**
	 * Returns all the blogs entries where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_NotS(long groupId, int status) {
		return findByG_NotS(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_NotS(
		long groupId, int status, int start, int end) {

		return findByG_NotS(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_NotS(
		long groupId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_NotS(
			groupId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_NotS(
		long groupId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_NotS.find(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_NotS_First(
			long groupId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_NotS.findFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_NotS_First(
		long groupId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_NotS.fetchFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_NotS(long groupId, int status) {
		return filterFindByG_NotS(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_NotS(
		long groupId, int status, int start, int end) {

		return filterFindByG_NotS(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_NotS(
		long groupId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_NotS.filterFind(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_NotS(long groupId, int status) {
		_collectionPersistenceFinderByG_NotS.remove(
			finderCache, new Object[] {groupId, status});
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_NotS(long groupId, int status) {
		return _collectionPersistenceFinderByG_NotS.count(
			finderCache, new Object[] {groupId, status});
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_NotS(long groupId, int status) {
		return _collectionPersistenceFinderByG_NotS.filterCount(
			finderCache, new Object[] {groupId, status}, groupId);
	}

	private CollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByC_U;

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_U.find(
			finderCache, new Object[] {companyId, userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_U_First(
			long companyId, long userId,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_U.findFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_U.fetchFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByC_U(long companyId, long userId) {
		_collectionPersistenceFinderByC_U.remove(
			finderCache, new Object[] {companyId, userId});
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByC_U(long companyId, long userId) {
		return _collectionPersistenceFinderByC_U.count(
			finderCache, new Object[] {companyId, userId});
	}

	private CollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByC_LtD;

	/**
	 * Returns all the blogs entries where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD(long companyId, Date displayDate) {
		return findByC_LtD(
			companyId, displayDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD(
		long companyId, Date displayDate, int start, int end) {

		return findByC_LtD(companyId, displayDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD(
		long companyId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByC_LtD(
			companyId, displayDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD(
		long companyId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_LtD.find(
			finderCache, new Object[] {companyId, displayDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_LtD_First(
			long companyId, Date displayDate,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_LtD.findFirst(
			finderCache, new Object[] {companyId, displayDate},
			orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_LtD_First(
		long companyId, Date displayDate,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_LtD.fetchFirst(
			finderCache, new Object[] {companyId, displayDate},
			orderByComparator);
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and displayDate &lt; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 */
	@Override
	public void removeByC_LtD(long companyId, Date displayDate) {
		_collectionPersistenceFinderByC_LtD.remove(
			finderCache, new Object[] {companyId, displayDate});
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByC_LtD(long companyId, Date displayDate) {
		return _collectionPersistenceFinderByC_LtD.count(
			finderCache, new Object[] {companyId, displayDate});
	}

	private CollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByC_S;

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S.find(
			finderCache, new Object[] {companyId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_S_First(
			long companyId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_S.findFirst(
			finderCache, new Object[] {companyId, status}, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_S_First(
		long companyId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_S.fetchFirst(
			finderCache, new Object[] {companyId, status}, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and status = &#63; from the database.
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
	 * Returns the number of blogs entries where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByC_S(long companyId, int status) {
		return _collectionPersistenceFinderByC_S.count(
			finderCache, new Object[] {companyId, status});
	}

	private CollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByC_NotS;

	/**
	 * Returns all the blogs entries where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_NotS(long companyId, int status) {
		return findByC_NotS(
			companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_NotS(
		long companyId, int status, int start, int end) {

		return findByC_NotS(companyId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByC_NotS(
			companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_NotS.find(
			finderCache, new Object[] {companyId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_NotS_First(
			long companyId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_NotS.findFirst(
			finderCache, new Object[] {companyId, status}, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_NotS_First(
		long companyId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_NotS.fetchFirst(
			finderCache, new Object[] {companyId, status}, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_NotS(long companyId, int status) {
		_collectionPersistenceFinderByC_NotS.remove(
			finderCache, new Object[] {companyId, status});
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByC_NotS(long companyId, int status) {
		return _collectionPersistenceFinderByC_NotS.count(
			finderCache, new Object[] {companyId, status});
	}

	private CollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByLtD_S;

	/**
	 * Returns all the blogs entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByLtD_S(Date displayDate, int status) {
		return findByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return findByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByLtD_S(
			displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtD_S.find(
			finderCache, new Object[] {displayDate, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByLtD_S.findFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByLtD_S.fetchFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where displayDate &lt; &#63; and status = &#63; from the database.
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
	 * Returns the number of blogs entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByLtD_S(Date displayDate, int status) {
		return _collectionPersistenceFinderByLtD_S.count(
			finderCache, new Object[] {displayDate, status});
	}

	private FilterCollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_U_LtD;

	/**
	 * Returns all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD(
		long groupId, long userId, Date displayDate) {

		return findByG_U_LtD(
			groupId, userId, displayDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD(
		long groupId, long userId, Date displayDate, int start, int end) {

		return findByG_U_LtD(groupId, userId, displayDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD(
		long groupId, long userId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_U_LtD(
			groupId, userId, displayDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD(
		long groupId, long userId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U_LtD.find(
			finderCache, new Object[] {groupId, userId, displayDate}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_U_LtD_First(
			long groupId, long userId, Date displayDate,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_U_LtD.findFirst(
			finderCache, new Object[] {groupId, userId, displayDate},
			orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_U_LtD_First(
		long groupId, long userId, Date displayDate,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_U_LtD.fetchFirst(
			finderCache, new Object[] {groupId, userId, displayDate},
			orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_LtD(
		long groupId, long userId, Date displayDate) {

		return filterFindByG_U_LtD(
			groupId, userId, displayDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_LtD(
		long groupId, long userId, Date displayDate, int start, int end) {

		return filterFindByG_U_LtD(
			groupId, userId, displayDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_LtD(
		long groupId, long userId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_U_LtD.filterFind(
			finderCache, new Object[] {groupId, userId, displayDate}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 */
	@Override
	public void removeByG_U_LtD(long groupId, long userId, Date displayDate) {
		_collectionPersistenceFinderByG_U_LtD.remove(
			finderCache, new Object[] {groupId, userId, displayDate});
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_U_LtD(long groupId, long userId, Date displayDate) {
		return _collectionPersistenceFinderByG_U_LtD.count(
			finderCache, new Object[] {groupId, userId, displayDate});
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_LtD(
		long groupId, long userId, Date displayDate) {

		return _collectionPersistenceFinderByG_U_LtD.filterCount(
			finderCache, new Object[] {groupId, userId, displayDate}, groupId);
	}

	private FilterCollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_U_S;

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U_S.find(
			finderCache, new Object[] {groupId, userId, new int[] {status}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_U_S_First(
			long groupId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_U_S_First(
			groupId, userId, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_U_S_First(
		long groupId, long userId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_U_S.fetchFirst(
			finderCache, new Object[] {groupId, userId, new int[] {status}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_U_S.filterFind(
			finderCache, new Object[] {groupId, userId, new int[] {status}},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_U_S.filterFind(
			finderCache,
			new Object[] {groupId, userId, ArrayUtil.sortedUnique(statuses)},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U_S.find(
			finderCache,
			new Object[] {groupId, userId, ArrayUtil.sortedUnique(statuses)},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and userId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 */
	@Override
	public void removeByG_U_S(long groupId, long userId, int status) {
		_collectionPersistenceFinderByG_U_S.remove(
			finderCache, new Object[] {groupId, userId, new int[] {status}});
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_U_S(long groupId, long userId, int status) {
		return _collectionPersistenceFinderByG_U_S.count(
			finderCache, new Object[] {groupId, userId, new int[] {status}});
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_U_S(long groupId, long userId, int[] statuses) {
		return _collectionPersistenceFinderByG_U_S.count(
			finderCache,
			new Object[] {groupId, userId, ArrayUtil.sortedUnique(statuses)});
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_S(long groupId, long userId, int status) {
		return _collectionPersistenceFinderByG_U_S.filterCount(
			finderCache, new Object[] {groupId, userId, new int[] {status}},
			groupId);
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_S(long groupId, long userId, int[] statuses) {
		return _collectionPersistenceFinderByG_U_S.filterCount(
			finderCache,
			new Object[] {groupId, userId, ArrayUtil.sortedUnique(statuses)},
			groupId);
	}

	private FilterCollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_U_NotS;

	/**
	 * Returns all the blogs entries where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_NotS(
		long groupId, long userId, int status) {

		return findByG_U_NotS(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_NotS(
		long groupId, long userId, int status, int start, int end) {

		return findByG_U_NotS(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_NotS(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_U_NotS(
			groupId, userId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_NotS(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U_NotS.find(
			finderCache, new Object[] {groupId, userId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_U_NotS_First(
			long groupId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_U_NotS.findFirst(
			finderCache, new Object[] {groupId, userId, status},
			orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_U_NotS_First(
		long groupId, long userId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_U_NotS.fetchFirst(
			finderCache, new Object[] {groupId, userId, status},
			orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_NotS(
		long groupId, long userId, int status) {

		return filterFindByG_U_NotS(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_NotS(
		long groupId, long userId, int status, int start, int end) {

		return filterFindByG_U_NotS(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_NotS(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_U_NotS.filterFind(
			finderCache, new Object[] {groupId, userId, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and userId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 */
	@Override
	public void removeByG_U_NotS(long groupId, long userId, int status) {
		_collectionPersistenceFinderByG_U_NotS.remove(
			finderCache, new Object[] {groupId, userId, status});
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_U_NotS(long groupId, long userId, int status) {
		return _collectionPersistenceFinderByG_U_NotS.count(
			finderCache, new Object[] {groupId, userId, status});
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_NotS(long groupId, long userId, int status) {
		return _collectionPersistenceFinderByG_U_NotS.filterCount(
			finderCache, new Object[] {groupId, userId, status}, groupId);
	}

	private FilterCollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_D_S;

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_D_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_D_S.find(
			finderCache, new Object[] {groupId, displayDate, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_D_S_First(
			long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_D_S.findFirst(
			finderCache, new Object[] {groupId, displayDate, status},
			orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_D_S_First(
		long groupId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_D_S.fetchFirst(
			finderCache, new Object[] {groupId, displayDate, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_D_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_D_S.filterFind(
			finderCache, new Object[] {groupId, displayDate, status}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and displayDate = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByG_D_S(long groupId, Date displayDate, int status) {
		_collectionPersistenceFinderByG_D_S.remove(
			finderCache, new Object[] {groupId, displayDate, status});
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_D_S(long groupId, Date displayDate, int status) {
		return _collectionPersistenceFinderByG_D_S.count(
			finderCache, new Object[] {groupId, displayDate, status});
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_D_S(long groupId, Date displayDate, int status) {
		return _collectionPersistenceFinderByG_D_S.filterCount(
			finderCache, new Object[] {groupId, displayDate, status}, groupId);
	}

	private FilterCollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_GtD_S;

	/**
	 * Returns all the blogs entries where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_GtD_S(
		long groupId, Date displayDate, int status) {

		return findByG_GtD_S(
			groupId, displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_GtD_S(
		long groupId, Date displayDate, int status, int start, int end) {

		return findByG_GtD_S(groupId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_GtD_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_GtD_S(
			groupId, displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_GtD_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_GtD_S.find(
			finderCache, new Object[] {groupId, displayDate, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_GtD_S_First(
			long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_GtD_S.findFirst(
			finderCache, new Object[] {groupId, displayDate, status},
			orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_GtD_S_First(
		long groupId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_GtD_S.fetchFirst(
			finderCache, new Object[] {groupId, displayDate, status},
			orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_GtD_S(
		long groupId, Date displayDate, int status) {

		return filterFindByG_GtD_S(
			groupId, displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_GtD_S(
		long groupId, Date displayDate, int status, int start, int end) {

		return filterFindByG_GtD_S(
			groupId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_GtD_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_GtD_S.filterFind(
			finderCache, new Object[] {groupId, displayDate, status}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and displayDate &gt; &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByG_GtD_S(long groupId, Date displayDate, int status) {
		_collectionPersistenceFinderByG_GtD_S.remove(
			finderCache, new Object[] {groupId, displayDate, status});
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_GtD_S(long groupId, Date displayDate, int status) {
		return _collectionPersistenceFinderByG_GtD_S.count(
			finderCache, new Object[] {groupId, displayDate, status});
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_GtD_S(
		long groupId, Date displayDate, int status) {

		return _collectionPersistenceFinderByG_GtD_S.filterCount(
			finderCache, new Object[] {groupId, displayDate, status}, groupId);
	}

	private FilterCollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_LtD_S;

	/**
	 * Returns all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD_S(
		long groupId, Date displayDate, int status) {

		return findByG_LtD_S(
			groupId, displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD_S(
		long groupId, Date displayDate, int status, int start, int end) {

		return findByG_LtD_S(groupId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_LtD_S(
			groupId, displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LtD_S.find(
			finderCache, new Object[] {groupId, displayDate, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_LtD_S_First(
			long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_LtD_S.findFirst(
			finderCache, new Object[] {groupId, displayDate, status},
			orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_LtD_S_First(
		long groupId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LtD_S.fetchFirst(
			finderCache, new Object[] {groupId, displayDate, status},
			orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_LtD_S(
		long groupId, Date displayDate, int status) {

		return filterFindByG_LtD_S(
			groupId, displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_LtD_S(
		long groupId, Date displayDate, int status, int start, int end) {

		return filterFindByG_LtD_S(
			groupId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_LtD_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LtD_S.filterFind(
			finderCache, new Object[] {groupId, displayDate, status}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByG_LtD_S(long groupId, Date displayDate, int status) {
		_collectionPersistenceFinderByG_LtD_S.remove(
			finderCache, new Object[] {groupId, displayDate, status});
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_LtD_S(long groupId, Date displayDate, int status) {
		return _collectionPersistenceFinderByG_LtD_S.count(
			finderCache, new Object[] {groupId, displayDate, status});
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_LtD_S(
		long groupId, Date displayDate, int status) {

		return _collectionPersistenceFinderByG_LtD_S.filterCount(
			finderCache, new Object[] {groupId, displayDate, status}, groupId);
	}

	private FilterCollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_LtD_NotS;

	/**
	 * Returns all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD_NotS(
		long groupId, Date displayDate, int status) {

		return findByG_LtD_NotS(
			groupId, displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD_NotS(
		long groupId, Date displayDate, int status, int start, int end) {

		return findByG_LtD_NotS(groupId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD_NotS(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_LtD_NotS(
			groupId, displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD_NotS(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LtD_NotS.find(
			finderCache, new Object[] {groupId, displayDate, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_LtD_NotS_First(
			long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_LtD_NotS.findFirst(
			finderCache, new Object[] {groupId, displayDate, status},
			orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_LtD_NotS_First(
		long groupId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LtD_NotS.fetchFirst(
			finderCache, new Object[] {groupId, displayDate, status},
			orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_LtD_NotS(
		long groupId, Date displayDate, int status) {

		return filterFindByG_LtD_NotS(
			groupId, displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_LtD_NotS(
		long groupId, Date displayDate, int status, int start, int end) {

		return filterFindByG_LtD_NotS(
			groupId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_LtD_NotS(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LtD_NotS.filterFind(
			finderCache, new Object[] {groupId, displayDate, status}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByG_LtD_NotS(long groupId, Date displayDate, int status) {
		_collectionPersistenceFinderByG_LtD_NotS.remove(
			finderCache, new Object[] {groupId, displayDate, status});
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_LtD_NotS(long groupId, Date displayDate, int status) {
		return _collectionPersistenceFinderByG_LtD_NotS.count(
			finderCache, new Object[] {groupId, displayDate, status});
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_LtD_NotS(
		long groupId, Date displayDate, int status) {

		return _collectionPersistenceFinderByG_LtD_NotS.filterCount(
			finderCache, new Object[] {groupId, displayDate, status}, groupId);
	}

	private CollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByC_U_S;

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_U_S(
		long companyId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_U_S.find(
			finderCache, new Object[] {companyId, userId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_U_S_First(
			long companyId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_U_S.findFirst(
			finderCache, new Object[] {companyId, userId, status},
			orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_U_S_First(
		long companyId, long userId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_U_S.fetchFirst(
			finderCache, new Object[] {companyId, userId, status},
			orderByComparator);
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and userId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 */
	@Override
	public void removeByC_U_S(long companyId, long userId, int status) {
		_collectionPersistenceFinderByC_U_S.remove(
			finderCache, new Object[] {companyId, userId, status});
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByC_U_S(long companyId, long userId, int status) {
		return _collectionPersistenceFinderByC_U_S.count(
			finderCache, new Object[] {companyId, userId, status});
	}

	private CollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByC_U_NotS;

	/**
	 * Returns all the blogs entries where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_U_NotS(
		long companyId, long userId, int status) {

		return findByC_U_NotS(
			companyId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_U_NotS(
		long companyId, long userId, int status, int start, int end) {

		return findByC_U_NotS(companyId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_U_NotS(
		long companyId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByC_U_NotS(
			companyId, userId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_U_NotS(
		long companyId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_U_NotS.find(
			finderCache, new Object[] {companyId, userId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_U_NotS_First(
			long companyId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_U_NotS.findFirst(
			finderCache, new Object[] {companyId, userId, status},
			orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_U_NotS_First(
		long companyId, long userId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_U_NotS.fetchFirst(
			finderCache, new Object[] {companyId, userId, status},
			orderByComparator);
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and userId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 */
	@Override
	public void removeByC_U_NotS(long companyId, long userId, int status) {
		_collectionPersistenceFinderByC_U_NotS.remove(
			finderCache, new Object[] {companyId, userId, status});
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByC_U_NotS(long companyId, long userId, int status) {
		return _collectionPersistenceFinderByC_U_NotS.count(
			finderCache, new Object[] {companyId, userId, status});
	}

	private CollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByC_LtD_S;

	/**
	 * Returns all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD_S(
		long companyId, Date displayDate, int status) {

		return findByC_LtD_S(
			companyId, displayDate, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD_S(
		long companyId, Date displayDate, int status, int start, int end) {

		return findByC_LtD_S(companyId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD_S(
		long companyId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByC_LtD_S(
			companyId, displayDate, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD_S(
		long companyId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_LtD_S.find(
			finderCache, new Object[] {companyId, displayDate, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_LtD_S_First(
			long companyId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_LtD_S.findFirst(
			finderCache, new Object[] {companyId, displayDate, status},
			orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_LtD_S_First(
		long companyId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_LtD_S.fetchFirst(
			finderCache, new Object[] {companyId, displayDate, status},
			orderByComparator);
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByC_LtD_S(long companyId, Date displayDate, int status) {
		_collectionPersistenceFinderByC_LtD_S.remove(
			finderCache, new Object[] {companyId, displayDate, status});
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByC_LtD_S(long companyId, Date displayDate, int status) {
		return _collectionPersistenceFinderByC_LtD_S.count(
			finderCache, new Object[] {companyId, displayDate, status});
	}

	private CollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByC_LtD_NotS;

	/**
	 * Returns all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD_NotS(
		long companyId, Date displayDate, int status) {

		return findByC_LtD_NotS(
			companyId, displayDate, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD_NotS(
		long companyId, Date displayDate, int status, int start, int end) {

		return findByC_LtD_NotS(
			companyId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD_NotS(
		long companyId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByC_LtD_NotS(
			companyId, displayDate, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD_NotS(
		long companyId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_LtD_NotS.find(
			finderCache, new Object[] {companyId, displayDate, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_LtD_NotS_First(
			long companyId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_LtD_NotS.findFirst(
			finderCache, new Object[] {companyId, displayDate, status},
			orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_LtD_NotS_First(
		long companyId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_LtD_NotS.fetchFirst(
			finderCache, new Object[] {companyId, displayDate, status},
			orderByComparator);
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByC_LtD_NotS(
		long companyId, Date displayDate, int status) {

		_collectionPersistenceFinderByC_LtD_NotS.remove(
			finderCache, new Object[] {companyId, displayDate, status});
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByC_LtD_NotS(long companyId, Date displayDate, int status) {
		return _collectionPersistenceFinderByC_LtD_NotS.count(
			finderCache, new Object[] {companyId, displayDate, status});
	}

	private FilterCollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_U_LtD_S;

	/**
	 * Returns all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status) {

		return findByG_U_LtD_S(
			groupId, userId, displayDate, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status, int start,
		int end) {

		return findByG_U_LtD_S(
			groupId, userId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status, int start,
		int end, OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_U_LtD_S(
			groupId, userId, displayDate, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status, int start,
		int end, OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U_LtD_S.find(
			finderCache, new Object[] {groupId, userId, displayDate, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_U_LtD_S_First(
			long groupId, long userId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_U_LtD_S.findFirst(
			finderCache, new Object[] {groupId, userId, displayDate, status},
			orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_U_LtD_S_First(
		long groupId, long userId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_U_LtD_S.fetchFirst(
			finderCache, new Object[] {groupId, userId, displayDate, status},
			orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status) {

		return filterFindByG_U_LtD_S(
			groupId, userId, displayDate, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status, int start,
		int end) {

		return filterFindByG_U_LtD_S(
			groupId, userId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status, int start,
		int end, OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_U_LtD_S.filterFind(
			finderCache, new Object[] {groupId, userId, displayDate, status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status) {

		_collectionPersistenceFinderByG_U_LtD_S.remove(
			finderCache, new Object[] {groupId, userId, displayDate, status});
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status) {

		return _collectionPersistenceFinderByG_U_LtD_S.count(
			finderCache, new Object[] {groupId, userId, displayDate, status});
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status) {

		return _collectionPersistenceFinderByG_U_LtD_S.filterCount(
			finderCache, new Object[] {groupId, userId, displayDate, status},
			groupId);
	}

	private FilterCollectionPersistenceFinder<BlogsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_U_LtD_NotS;

	/**
	 * Returns all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status) {

		return findByG_U_LtD_NotS(
			groupId, userId, displayDate, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status, int start,
		int end) {

		return findByG_U_LtD_NotS(
			groupId, userId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status, int start,
		int end, OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_U_LtD_NotS(
			groupId, userId, displayDate, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status, int start,
		int end, OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U_LtD_NotS.find(
			finderCache, new Object[] {groupId, userId, displayDate, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_U_LtD_NotS_First(
			long groupId, long userId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_U_LtD_NotS.findFirst(
			finderCache, new Object[] {groupId, userId, displayDate, status},
			orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_U_LtD_NotS_First(
		long groupId, long userId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_U_LtD_NotS.fetchFirst(
			finderCache, new Object[] {groupId, userId, displayDate, status},
			orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status) {

		return filterFindByG_U_LtD_NotS(
			groupId, userId, displayDate, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status, int start,
		int end) {

		return filterFindByG_U_LtD_NotS(
			groupId, userId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status, int start,
		int end, OrderByComparator<BlogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_U_LtD_NotS.filterFind(
			finderCache, new Object[] {groupId, userId, displayDate, status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status) {

		_collectionPersistenceFinderByG_U_LtD_NotS.remove(
			finderCache, new Object[] {groupId, userId, displayDate, status});
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status) {

		return _collectionPersistenceFinderByG_U_LtD_NotS.count(
			finderCache, new Object[] {groupId, userId, displayDate, status});
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status) {

		return _collectionPersistenceFinderByG_U_LtD_NotS.filterCount(
			finderCache, new Object[] {groupId, userId, displayDate, status},
			groupId);
	}

	private UniquePersistenceFinder<BlogsEntry, NoSuchEntryException>
		_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the blogs entry where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the blogs entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the blogs entry where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the blogs entry that was removed
	 */
	@Override
	public BlogsEntry removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByERC_G(externalReferenceCode, groupId);

		return remove(blogsEntry);
	}

	/**
	 * Returns the number of blogs entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public BlogsEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(BlogsEntry.class);

		setModelImplClass(BlogsEntryImpl.class);
		setModelPKClass(long.class);

		setTable(BlogsEntryTable.INSTANCE);
	}

	/**
	 * Creates a new blogs entry with the primary key. Does not add the blogs entry to the database.
	 *
	 * @param entryId the primary key for the new blogs entry
	 * @return the new blogs entry
	 */
	@Override
	public BlogsEntry create(long entryId) {
		BlogsEntry blogsEntry = new BlogsEntryImpl();

		blogsEntry.setNew(true);
		blogsEntry.setPrimaryKey(entryId);

		String uuid = PortalUUIDUtil.generate();

		blogsEntry.setUuid(uuid);

		blogsEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return blogsEntry;
	}

	/**
	 * Removes the blogs entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the blogs entry
	 * @return the blogs entry that was removed
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry remove(long entryId) throws NoSuchEntryException {
		return remove((Serializable)entryId);
	}

	@Override
	protected BlogsEntry removeImpl(BlogsEntry blogsEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(blogsEntry)) {
				blogsEntry = (BlogsEntry)session.get(
					BlogsEntryImpl.class, blogsEntry.getPrimaryKeyObj());
			}

			if ((blogsEntry != null) &&
				ctPersistenceHelper.isRemove(blogsEntry)) {

				session.delete(blogsEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (blogsEntry != null) {
			clearCache(blogsEntry);
		}

		return blogsEntry;
	}

	@Override
	public BlogsEntry updateImpl(BlogsEntry blogsEntry) {
		boolean isNew = blogsEntry.isNew();

		if (!(blogsEntry instanceof BlogsEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(blogsEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(blogsEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in blogsEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom BlogsEntry implementation " +
					blogsEntry.getClass());
		}

		BlogsEntryModelImpl blogsEntryModelImpl =
			(BlogsEntryModelImpl)blogsEntry;

		if (Validator.isNull(blogsEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			blogsEntry.setUuid(uuid);
		}

		if (Validator.isNull(blogsEntry.getExternalReferenceCode())) {
			blogsEntry.setExternalReferenceCode(blogsEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					blogsEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					blogsEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = blogsEntry.getCompanyId();

					long groupId = blogsEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = blogsEntry.getPrimaryKey();
					}

					try {
						blogsEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								BlogsEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								blogsEntry.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			BlogsEntry ercBlogsEntry = fetchByERC_G(
				blogsEntry.getExternalReferenceCode(), blogsEntry.getGroupId());

			if (isNew) {
				if (ercBlogsEntry != null) {
					throw new DuplicateBlogsEntryExternalReferenceCodeException(
						"Duplicate blogs entry with external reference code " +
							blogsEntry.getExternalReferenceCode() +
								" and group " + blogsEntry.getGroupId());
				}
			}
			else {
				if ((ercBlogsEntry != null) &&
					(blogsEntry.getEntryId() != ercBlogsEntry.getEntryId())) {

					throw new DuplicateBlogsEntryExternalReferenceCodeException(
						"Duplicate blogs entry with external reference code " +
							blogsEntry.getExternalReferenceCode() +
								" and group " + blogsEntry.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (blogsEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				blogsEntry.setCreateDate(date);
			}
			else {
				blogsEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!blogsEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				blogsEntry.setModifiedDate(date);
			}
			else {
				blogsEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		long userId = GetterUtil.getLong(PrincipalThreadLocal.getName());

		if (userId > 0) {
			long companyId = blogsEntry.getCompanyId();

			long groupId = blogsEntry.getGroupId();

			long entryId = 0;

			if (!isNew) {
				entryId = blogsEntry.getPrimaryKey();
			}

			try {
				blogsEntry.setTitle(
					SanitizerUtil.sanitize(
						companyId, groupId, userId, BlogsEntry.class.getName(),
						entryId, ContentTypes.TEXT_PLAIN, Sanitizer.MODE_ALL,
						blogsEntry.getTitle(), null));

				blogsEntry.setContent(
					SanitizerUtil.sanitize(
						companyId, groupId, userId, BlogsEntry.class.getName(),
						entryId, ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
						blogsEntry.getContent(), null));

				blogsEntry.setCoverImageCaption(
					SanitizerUtil.sanitize(
						companyId, groupId, userId, BlogsEntry.class.getName(),
						entryId, ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
						blogsEntry.getCoverImageCaption(), null));
			}
			catch (SanitizerException sanitizerException) {
				throw new SystemException(sanitizerException);
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(blogsEntry)) {
				if (!isNew) {
					session.evict(
						BlogsEntryImpl.class, blogsEntry.getPrimaryKeyObj());
				}

				session.save(blogsEntry);
			}
			else {
				blogsEntry = (BlogsEntry)session.merge(blogsEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(blogsEntry, false);

		if (isNew) {
			blogsEntry.setNew(false);
		}

		blogsEntry.resetOriginalValues();

		return blogsEntry;
	}

	/**
	 * Returns the blogs entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param entryId the primary key of the blogs entry
	 * @return the blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry findByPrimaryKey(long entryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)entryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the blogs entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param entryId the primary key of the blogs entry
	 * @return the blogs entry, or <code>null</code> if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry fetchByPrimaryKey(long entryId) {
		return fetchByPrimaryKey((Serializable)entryId);
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
		return "entryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_BLOGSENTRY;
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
		return BlogsEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "BlogsEntry";
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
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("subtitle");
		ctMergeColumnNames.add("urlTitle");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("content");
		ctMergeColumnNames.add("displayDate");
		ctMergeColumnNames.add("allowPingbacks");
		ctMergeColumnNames.add("allowTrackbacks");
		ctMergeColumnNames.add("trackbacks");
		ctMergeColumnNames.add("coverImageCaption");
		ctMergeColumnNames.add("coverImageFileEntryId");
		ctMergeColumnNames.add("coverImageURL");
		ctMergeColumnNames.add("smallImage");
		ctMergeColumnNames.add("smallImageFileEntryId");
		ctMergeColumnNames.add("smallImageId");
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
			CTColumnResolutionType.PK, Collections.singleton("entryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"groupId", "urlTitle"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the blogs entry persistence.
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
			_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
			BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"blogsEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, BlogsEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(BlogsEntry::getUuid),
				BlogsEntry::getGroupId),
			_SQL_SELECT_BLOGSENTRY_WHERE, "",
			new FinderColumn<>(
				"blogsEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, BlogsEntry::getUuid),
			new FinderColumn<>(
				"blogsEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, BlogsEntry::getGroupId));

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
				_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
				BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"blogsEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, BlogsEntry::getUuid),
				new FinderColumn<>(
					"blogsEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, BlogsEntry::getCompanyId));

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
				_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
				BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"blogsEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, BlogsEntry::getGroupId));

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
				_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
				BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"blogsEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, BlogsEntry::getCompanyId));

		_uniquePersistenceFinderByG_UT = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_UT",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "urlTitle"}, 0, 2, false,
				BlogsEntry::getGroupId,
				convertNullFunction(BlogsEntry::getUrlTitle)),
			_SQL_SELECT_BLOGSENTRY_WHERE, "",
			new FinderColumn<>(
				"blogsEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, BlogsEntry::getGroupId),
			new FinderColumn<>(
				"blogsEntry.", "urlTitle", FinderColumn.Type.STRING, "=", true,
				true, BlogsEntry::getUrlTitle));

		_collectionPersistenceFinderByG_LtD =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LtD",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "displayDate"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LtD",
					new String[] {Long.class.getName(), Date.class.getName()},
					new String[] {"groupId", "displayDate"}, false),
				_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
				BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"blogsEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, BlogsEntry::getGroupId),
				new FinderColumn<>(
					"blogsEntry.", "displayDate", FinderColumn.Type.DATE, "<",
					true, true, BlogsEntry::getDisplayDate));

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
				_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
				BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"blogsEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, BlogsEntry::getGroupId),
				new FinderColumn<>(
					"blogsEntry.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, BlogsEntry::getStatus));

		_collectionPersistenceFinderByG_NotS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_NotS",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_NotS",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "status"}, false),
				_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
				BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"blogsEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, BlogsEntry::getGroupId),
				new FinderColumn<>(
					"blogsEntry.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, BlogsEntry::getStatus));

		_collectionPersistenceFinderByC_U = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_U",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "userId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "userId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "userId"}, false),
			_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
			BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"blogsEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, BlogsEntry::getCompanyId),
			new FinderColumn<>(
				"blogsEntry.", "userId", FinderColumn.Type.LONG, "=", true,
				true, BlogsEntry::getUserId));

		_collectionPersistenceFinderByC_LtD = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LtD",
				new String[] {
					Long.class.getName(), Date.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "displayDate"}, true),
			null,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LtD",
				new String[] {Long.class.getName(), Date.class.getName()},
				new String[] {"companyId", "displayDate"}, false),
			_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
			BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"blogsEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, BlogsEntry::getCompanyId),
			new FinderColumn<>(
				"blogsEntry.", "displayDate", FinderColumn.Type.DATE, "<", true,
				true, BlogsEntry::getDisplayDate));

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
			_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
			BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"blogsEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, BlogsEntry::getCompanyId),
			new FinderColumn<>(
				"blogsEntry.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, BlogsEntry::getStatus));

		_collectionPersistenceFinderByC_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_NotS",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_NotS",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"companyId", "status"}, false),
				_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
				BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"blogsEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, BlogsEntry::getCompanyId),
				new FinderColumn<>(
					"blogsEntry.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, BlogsEntry::getStatus));

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
			_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
			BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"blogsEntry.", "displayDate", FinderColumn.Type.DATE, "<", true,
				true, BlogsEntry::getDisplayDate),
			new FinderColumn<>(
				"blogsEntry.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, BlogsEntry::getStatus));

		_collectionPersistenceFinderByG_U_LtD =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_LtD",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "userId", "displayDate"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_U_LtD",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Date.class.getName()
					},
					new String[] {"groupId", "userId", "displayDate"}, false),
				_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
				BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"blogsEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, BlogsEntry::getGroupId),
				new FinderColumn<>(
					"blogsEntry.", "userId", FinderColumn.Type.LONG, "=", true,
					true, BlogsEntry::getUserId),
				new FinderColumn<>(
					"blogsEntry.", "displayDate", FinderColumn.Type.DATE, "<",
					true, true, BlogsEntry::getDisplayDate));

		_collectionPersistenceFinderByG_U_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "userId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "userId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_U_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "userId", "status"}, false),
				_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
				BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"blogsEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, BlogsEntry::getGroupId),
				new FinderColumn<>(
					"blogsEntry.", "userId", FinderColumn.Type.LONG, "=", true,
					true, BlogsEntry::getUserId),
				new ArrayableFinderColumn<>(
					"blogsEntry.", "status", FinderColumn.Type.INTEGER, "=",
					false, true, true, BlogsEntry::getStatus));

		_collectionPersistenceFinderByG_U_NotS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "userId", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_U_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "userId", "status"}, false),
				_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
				BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"blogsEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, BlogsEntry::getGroupId),
				new FinderColumn<>(
					"blogsEntry.", "userId", FinderColumn.Type.LONG, "=", true,
					true, BlogsEntry::getUserId),
				new FinderColumn<>(
					"blogsEntry.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, BlogsEntry::getStatus));

		_collectionPersistenceFinderByG_D_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_D_S",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "displayDate", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_D_S",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "displayDate", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_D_S",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "displayDate", "status"}, false),
				_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
				BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"blogsEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, BlogsEntry::getGroupId),
				new FinderColumn<>(
					"blogsEntry.", "displayDate", FinderColumn.Type.DATE, "=",
					true, true, BlogsEntry::getDisplayDate),
				new FinderColumn<>(
					"blogsEntry.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, BlogsEntry::getStatus));

		_collectionPersistenceFinderByG_GtD_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_GtD_S",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "displayDate", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_GtD_S",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "displayDate", "status"}, false),
				_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
				BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"blogsEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, BlogsEntry::getGroupId),
				new FinderColumn<>(
					"blogsEntry.", "displayDate", FinderColumn.Type.DATE, ">",
					true, true, BlogsEntry::getDisplayDate),
				new FinderColumn<>(
					"blogsEntry.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, BlogsEntry::getStatus));

		_collectionPersistenceFinderByG_LtD_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LtD_S",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "displayDate", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LtD_S",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "displayDate", "status"}, false),
				_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
				BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"blogsEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, BlogsEntry::getGroupId),
				new FinderColumn<>(
					"blogsEntry.", "displayDate", FinderColumn.Type.DATE, "<",
					true, true, BlogsEntry::getDisplayDate),
				new FinderColumn<>(
					"blogsEntry.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, BlogsEntry::getStatus));

		_collectionPersistenceFinderByG_LtD_NotS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LtD_NotS",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "displayDate", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LtD_NotS",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "displayDate", "status"}, false),
				_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
				BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"blogsEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, BlogsEntry::getGroupId),
				new FinderColumn<>(
					"blogsEntry.", "displayDate", FinderColumn.Type.DATE, "<",
					true, true, BlogsEntry::getDisplayDate),
				new FinderColumn<>(
					"blogsEntry.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, BlogsEntry::getStatus));

		_collectionPersistenceFinderByC_U_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_U_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "userId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_U_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"companyId", "userId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_U_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"companyId", "userId", "status"}, false),
			_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
			BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"blogsEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, BlogsEntry::getCompanyId),
			new FinderColumn<>(
				"blogsEntry.", "userId", FinderColumn.Type.LONG, "=", true,
				true, BlogsEntry::getUserId),
			new FinderColumn<>(
				"blogsEntry.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, BlogsEntry::getStatus));

		_collectionPersistenceFinderByC_U_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_U_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "userId", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_U_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"companyId", "userId", "status"}, false),
				_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
				BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"blogsEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, BlogsEntry::getCompanyId),
				new FinderColumn<>(
					"blogsEntry.", "userId", FinderColumn.Type.LONG, "=", true,
					true, BlogsEntry::getUserId),
				new FinderColumn<>(
					"blogsEntry.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, BlogsEntry::getStatus));

		_collectionPersistenceFinderByC_LtD_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LtD_S",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "displayDate", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LtD_S",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName()
					},
					new String[] {"companyId", "displayDate", "status"}, false),
				_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
				BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"blogsEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, BlogsEntry::getCompanyId),
				new FinderColumn<>(
					"blogsEntry.", "displayDate", FinderColumn.Type.DATE, "<",
					true, true, BlogsEntry::getDisplayDate),
				new FinderColumn<>(
					"blogsEntry.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, BlogsEntry::getStatus));

		_collectionPersistenceFinderByC_LtD_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LtD_NotS",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "displayDate", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LtD_NotS",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName()
					},
					new String[] {"companyId", "displayDate", "status"}, false),
				_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
				BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"blogsEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, BlogsEntry::getCompanyId),
				new FinderColumn<>(
					"blogsEntry.", "displayDate", FinderColumn.Type.DATE, "<",
					true, true, BlogsEntry::getDisplayDate),
				new FinderColumn<>(
					"blogsEntry.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, BlogsEntry::getStatus));

		_collectionPersistenceFinderByG_U_LtD_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_LtD_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "userId", "displayDate", "status"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_U_LtD_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Date.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "userId", "displayDate", "status"},
					false),
				_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
				BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"blogsEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, BlogsEntry::getGroupId),
				new FinderColumn<>(
					"blogsEntry.", "userId", FinderColumn.Type.LONG, "=", true,
					true, BlogsEntry::getUserId),
				new FinderColumn<>(
					"blogsEntry.", "displayDate", FinderColumn.Type.DATE, "<",
					true, true, BlogsEntry::getDisplayDate),
				new FinderColumn<>(
					"blogsEntry.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, BlogsEntry::getStatus));

		_collectionPersistenceFinderByG_U_LtD_NotS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_U_LtD_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "userId", "displayDate", "status"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_U_LtD_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Date.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "userId", "displayDate", "status"},
					false),
				_SQL_SELECT_BLOGSENTRY_WHERE, _SQL_COUNT_BLOGSENTRY_WHERE,
				BlogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"blogsEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, BlogsEntry::getGroupId),
				new FinderColumn<>(
					"blogsEntry.", "userId", FinderColumn.Type.LONG, "=", true,
					true, BlogsEntry::getUserId),
				new FinderColumn<>(
					"blogsEntry.", "displayDate", FinderColumn.Type.DATE, "<",
					true, true, BlogsEntry::getDisplayDate),
				new FinderColumn<>(
					"blogsEntry.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, BlogsEntry::getStatus));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(BlogsEntry::getExternalReferenceCode),
				BlogsEntry::getGroupId),
			_SQL_SELECT_BLOGSENTRY_WHERE, "",
			new FinderColumn<>(
				"blogsEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				BlogsEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"blogsEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, BlogsEntry::getGroupId));

		BlogsEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		BlogsEntryUtil.setPersistence(null);

		entityCache.removeCache(BlogsEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = BlogsPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = BlogsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = BlogsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		BlogsEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_BLOGSENTRY =
		"SELECT blogsEntry FROM BlogsEntry blogsEntry";

	private static final String _SQL_SELECT_BLOGSENTRY_WHERE =
		"SELECT blogsEntry FROM BlogsEntry blogsEntry WHERE ";

	private static final String _SQL_COUNT_BLOGSENTRY_WHERE =
		"SELECT COUNT(blogsEntry) FROM BlogsEntry blogsEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No BlogsEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		BlogsEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1078250168