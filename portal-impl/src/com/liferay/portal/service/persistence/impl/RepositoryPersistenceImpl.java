/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.DuplicateRepositoryExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchRepositoryException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.RepositoryTable;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.RepositoryPersistence;
import com.liferay.portal.kernel.service.persistence.RepositoryUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.model.impl.RepositoryImpl;
import com.liferay.portal.model.impl.RepositoryModelImpl;

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

/**
 * The persistence implementation for the repository service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class RepositoryPersistenceImpl
	extends BasePersistenceImpl<Repository, NoSuchRepositoryException>
	implements RepositoryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>RepositoryUtil</code> to access the repository persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		RepositoryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<Repository, NoSuchRepositoryException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the repositories where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching repositories
	 */
	@Override
	public List<Repository> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Repository> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first repository in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository
	 * @throws NoSuchRepositoryException if a matching repository could not be found
	 */
	@Override
	public Repository findByUuid_First(
			String uuid, OrderByComparator<Repository> orderByComparator)
		throws NoSuchRepositoryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first repository in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository, or <code>null</code> if a matching repository could not be found
	 */
	@Override
	public Repository fetchByUuid_First(
		String uuid, OrderByComparator<Repository> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the repositories where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of repositories where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching repositories
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private UniquePersistenceFinder<Repository, NoSuchRepositoryException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the repository where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchRepositoryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching repository
	 * @throws NoSuchRepositoryException if a matching repository could not be found
	 */
	@Override
	public Repository findByUUID_G(String uuid, long groupId)
		throws NoSuchRepositoryException {

		return _uniquePersistenceFinderByUUID_G.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	/**
	 * Returns the repository where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching repository, or <code>null</code> if a matching repository could not be found
	 */
	@Override
	public Repository fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId},
			useFinderCache);
	}

	/**
	 * Removes the repository where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the repository that was removed
	 */
	@Override
	public Repository removeByUUID_G(String uuid, long groupId)
		throws NoSuchRepositoryException {

		Repository repository = findByUUID_G(uuid, groupId);

		return remove(repository);
	}

	/**
	 * Returns the number of repositories where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching repositories
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<Repository, NoSuchRepositoryException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the repositories where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching repositories
	 */
	@Override
	public List<Repository> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Repository> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first repository in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository
	 * @throws NoSuchRepositoryException if a matching repository could not be found
	 */
	@Override
	public Repository findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<Repository> orderByComparator)
		throws NoSuchRepositoryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first repository in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository, or <code>null</code> if a matching repository could not be found
	 */
	@Override
	public Repository fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<Repository> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the repositories where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of repositories where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching repositories
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<Repository, NoSuchRepositoryException>
		_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the repositories where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching repositories
	 */
	@Override
	public List<Repository> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<Repository> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first repository in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository
	 * @throws NoSuchRepositoryException if a matching repository could not be found
	 */
	@Override
	public Repository findByGroupId_First(
			long groupId, OrderByComparator<Repository> orderByComparator)
		throws NoSuchRepositoryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first repository in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository, or <code>null</code> if a matching repository could not be found
	 */
	@Override
	public Repository fetchByGroupId_First(
		long groupId, OrderByComparator<Repository> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Removes all the repositories where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of repositories where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching repositories
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	private CollectionPersistenceFinder<Repository, NoSuchRepositoryException>
		_collectionPersistenceFinderByPortletId;

	/**
	 * Returns an ordered range of all the repositories where portletId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching repositories
	 */
	@Override
	public List<Repository> findByPortletId(
		String portletId, int start, int end,
		OrderByComparator<Repository> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPortletId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {portletId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first repository in the ordered set where portletId = &#63;.
	 *
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository
	 * @throws NoSuchRepositoryException if a matching repository could not be found
	 */
	@Override
	public Repository findByPortletId_First(
			String portletId, OrderByComparator<Repository> orderByComparator)
		throws NoSuchRepositoryException {

		return _collectionPersistenceFinderByPortletId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {portletId},
			orderByComparator);
	}

	/**
	 * Returns the first repository in the ordered set where portletId = &#63;.
	 *
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository, or <code>null</code> if a matching repository could not be found
	 */
	@Override
	public Repository fetchByPortletId_First(
		String portletId, OrderByComparator<Repository> orderByComparator) {

		return _collectionPersistenceFinderByPortletId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {portletId},
			orderByComparator);
	}

	/**
	 * Removes all the repositories where portletId = &#63; from the database.
	 *
	 * @param portletId the portlet ID
	 */
	@Override
	public void removeByPortletId(String portletId) {
		_collectionPersistenceFinderByPortletId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {portletId});
	}

	/**
	 * Returns the number of repositories where portletId = &#63;.
	 *
	 * @param portletId the portlet ID
	 * @return the number of matching repositories
	 */
	@Override
	public int countByPortletId(String portletId) {
		return _collectionPersistenceFinderByPortletId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {portletId});
	}

	private UniquePersistenceFinder<Repository, NoSuchRepositoryException>
		_uniquePersistenceFinderByG_N_P;

	/**
	 * Returns the repository where groupId = &#63; and name = &#63; and portletId = &#63; or throws a <code>NoSuchRepositoryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param portletId the portlet ID
	 * @return the matching repository
	 * @throws NoSuchRepositoryException if a matching repository could not be found
	 */
	@Override
	public Repository findByG_N_P(long groupId, String name, String portletId)
		throws NoSuchRepositoryException {

		return _uniquePersistenceFinderByG_N_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, name, portletId});
	}

	/**
	 * Returns the repository where groupId = &#63; and name = &#63; and portletId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param portletId the portlet ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching repository, or <code>null</code> if a matching repository could not be found
	 */
	@Override
	public Repository fetchByG_N_P(
		long groupId, String name, String portletId, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_N_P.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, name, portletId}, useFinderCache);
	}

	/**
	 * Removes the repository where groupId = &#63; and name = &#63; and portletId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param portletId the portlet ID
	 * @return the repository that was removed
	 */
	@Override
	public Repository removeByG_N_P(long groupId, String name, String portletId)
		throws NoSuchRepositoryException {

		Repository repository = findByG_N_P(groupId, name, portletId);

		return remove(repository);
	}

	/**
	 * Returns the number of repositories where groupId = &#63; and name = &#63; and portletId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param portletId the portlet ID
	 * @return the number of matching repositories
	 */
	@Override
	public int countByG_N_P(long groupId, String name, String portletId) {
		return _uniquePersistenceFinderByG_N_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, name, portletId});
	}

	private UniquePersistenceFinder<Repository, NoSuchRepositoryException>
		_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the repository where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchRepositoryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching repository
	 * @throws NoSuchRepositoryException if a matching repository could not be found
	 */
	@Override
	public Repository findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchRepositoryException {

		return _uniquePersistenceFinderByERC_G.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the repository where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching repository, or <code>null</code> if a matching repository could not be found
	 */
	@Override
	public Repository fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId}, useFinderCache);
	}

	/**
	 * Removes the repository where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the repository that was removed
	 */
	@Override
	public Repository removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchRepositoryException {

		Repository repository = findByERC_G(externalReferenceCode, groupId);

		return remove(repository);
	}

	/**
	 * Returns the number of repositories where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching repositories
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	public RepositoryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(Repository.class);

		setModelImplClass(RepositoryImpl.class);
		setModelPKClass(long.class);

		setTable(RepositoryTable.INSTANCE);
	}

	/**
	 * Creates a new repository with the primary key. Does not add the repository to the database.
	 *
	 * @param repositoryId the primary key for the new repository
	 * @return the new repository
	 */
	@Override
	public Repository create(long repositoryId) {
		Repository repository = new RepositoryImpl();

		repository.setNew(true);
		repository.setPrimaryKey(repositoryId);

		String uuid = PortalUUIDUtil.generate();

		repository.setUuid(uuid);

		repository.setCompanyId(CompanyThreadLocal.getCompanyId());

		return repository;
	}

	/**
	 * Removes the repository with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param repositoryId the primary key of the repository
	 * @return the repository that was removed
	 * @throws NoSuchRepositoryException if a repository with the primary key could not be found
	 */
	@Override
	public Repository remove(long repositoryId)
		throws NoSuchRepositoryException {

		return remove((Serializable)repositoryId);
	}

	@Override
	protected Repository removeImpl(Repository repository) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(repository)) {
				repository = (Repository)session.get(
					RepositoryImpl.class, repository.getPrimaryKeyObj());
			}

			if ((repository != null) &&
				CTPersistenceHelperUtil.isRemove(repository)) {

				session.delete(repository);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (repository != null) {
			clearCache(repository);
		}

		return repository;
	}

	@Override
	public Repository updateImpl(Repository repository) {
		boolean isNew = repository.isNew();

		if (!(repository instanceof RepositoryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(repository.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(repository);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in repository proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom Repository implementation " +
					repository.getClass());
		}

		RepositoryModelImpl repositoryModelImpl =
			(RepositoryModelImpl)repository;

		if (Validator.isNull(repository.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			repository.setUuid(uuid);
		}

		if (Validator.isNull(repository.getExternalReferenceCode())) {
			repository.setExternalReferenceCode(repository.getUuid());
		}
		else {
			if (!Objects.equals(
					repositoryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					repository.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = repository.getCompanyId();

					long groupId = repository.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = repository.getPrimaryKey();
					}

					try {
						repository.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								Repository.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								repository.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			Repository ercRepository = fetchByERC_G(
				repository.getExternalReferenceCode(), repository.getGroupId());

			if (isNew) {
				if (ercRepository != null) {
					throw new DuplicateRepositoryExternalReferenceCodeException(
						"Duplicate repository with external reference code " +
							repository.getExternalReferenceCode() +
								" and group " + repository.getGroupId());
				}
			}
			else {
				if ((ercRepository != null) &&
					(repository.getRepositoryId() !=
						ercRepository.getRepositoryId())) {

					throw new DuplicateRepositoryExternalReferenceCodeException(
						"Duplicate repository with external reference code " +
							repository.getExternalReferenceCode() +
								" and group " + repository.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (repository.getCreateDate() == null)) {
			if (serviceContext == null) {
				repository.setCreateDate(date);
			}
			else {
				repository.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!repositoryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				repository.setModifiedDate(date);
			}
			else {
				repository.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(repository)) {
				if (!isNew) {
					session.evict(
						RepositoryImpl.class, repository.getPrimaryKeyObj());
				}

				session.save(repository);
			}
			else {
				repository = (Repository)session.merge(repository);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(repository, false);

		if (isNew) {
			repository.setNew(false);
		}

		repository.resetOriginalValues();

		return repository;
	}

	/**
	 * Returns the repository with the primary key or throws a <code>NoSuchRepositoryException</code> if it could not be found.
	 *
	 * @param repositoryId the primary key of the repository
	 * @return the repository
	 * @throws NoSuchRepositoryException if a repository with the primary key could not be found
	 */
	@Override
	public Repository findByPrimaryKey(long repositoryId)
		throws NoSuchRepositoryException {

		return findByPrimaryKey((Serializable)repositoryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the repository with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param repositoryId the primary key of the repository
	 * @return the repository, or <code>null</code> if a repository with the primary key could not be found
	 */
	@Override
	public Repository fetchByPrimaryKey(long repositoryId) {
		return fetchByPrimaryKey((Serializable)repositoryId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "repositoryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_REPOSITORY;
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
		return RepositoryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "Repository";
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
		ctStrictColumnNames.add("classNameId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("portletId");
		ctMergeColumnNames.add("typeSettings");
		ctMergeColumnNames.add("dlFolderId");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("repositoryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "name", "portletId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the repository persistence.
	 */
	public void afterPropertiesSet() {
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
			_SQL_SELECT_REPOSITORY_WHERE, _SQL_COUNT_REPOSITORY_WHERE,
			RepositoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"repository.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, Repository::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(Repository::getUuid),
				Repository::getGroupId),
			_SQL_SELECT_REPOSITORY_WHERE, "",
			new FinderColumn<>(
				"repository.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, Repository::getUuid),
			new FinderColumn<>(
				"repository.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, Repository::getGroupId));

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
				_SQL_SELECT_REPOSITORY_WHERE, _SQL_COUNT_REPOSITORY_WHERE,
				RepositoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"repository.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, Repository::getUuid),
				new FinderColumn<>(
					"repository.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, Repository::getCompanyId));

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
				_SQL_SELECT_REPOSITORY_WHERE, _SQL_COUNT_REPOSITORY_WHERE,
				RepositoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"repository.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, Repository::getGroupId));

		_collectionPersistenceFinderByPortletId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPortletId",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"portletId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByPortletId", new String[] {String.class.getName()},
					new String[] {"portletId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByPortletId", new String[] {String.class.getName()},
					new String[] {"portletId"}, 0, 1, false, null),
				_SQL_SELECT_REPOSITORY_WHERE, _SQL_COUNT_REPOSITORY_WHERE,
				RepositoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"repository.", "portletId", FinderColumn.Type.STRING, "=",
					true, true, Repository::getPortletId));

		_uniquePersistenceFinderByG_N_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_N_P",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "name", "portletId"}, 0, 6, false,
				Repository::getGroupId,
				convertNullFunction(Repository::getName),
				convertNullFunction(Repository::getPortletId)),
			_SQL_SELECT_REPOSITORY_WHERE, "",
			new FinderColumn<>(
				"repository.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, Repository::getGroupId),
			new FinderColumn<>(
				"repository.", "name", FinderColumn.Type.STRING, "=", true,
				true, Repository::getName),
			new FinderColumn<>(
				"repository.", "portletId", FinderColumn.Type.STRING, "=", true,
				true, Repository::getPortletId));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(Repository::getExternalReferenceCode),
				Repository::getGroupId),
			_SQL_SELECT_REPOSITORY_WHERE, "",
			new FinderColumn<>(
				"repository.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				Repository::getExternalReferenceCode),
			new FinderColumn<>(
				"repository.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, Repository::getGroupId));

		RepositoryUtil.setPersistence(this);
	}

	public void destroy() {
		RepositoryUtil.setPersistence(null);

		EntityCacheUtil.removeCache(RepositoryImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		RepositoryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_REPOSITORY =
		"SELECT repository FROM Repository repository";

	private static final String _SQL_SELECT_REPOSITORY_WHERE =
		"SELECT repository FROM Repository repository WHERE ";

	private static final String _SQL_COUNT_REPOSITORY_WHERE =
		"SELECT COUNT(repository) FROM Repository repository WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No Repository exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		RepositoryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:312494454