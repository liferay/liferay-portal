/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.persistence;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the fragment entry link service. This utility wraps <code>com.liferay.fragment.service.persistence.impl.FragmentEntryLinkPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryLinkPersistence
 * @generated
 */
public class FragmentEntryLinkUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<FragmentEntryLink> fragmentEntryLinks) {
		getPersistence().cacheResult(fragmentEntryLinks);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(FragmentEntryLink fragmentEntryLink) {
		getPersistence().cacheResult(fragmentEntryLink);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(FragmentEntryLink fragmentEntryLink) {
		getPersistence().clearCache(fragmentEntryLink);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, FragmentEntryLink> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<FragmentEntryLink> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<FragmentEntryLink> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<FragmentEntryLink> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static FragmentEntryLink update(
		FragmentEntryLink fragmentEntryLink) {

		return getPersistence().update(fragmentEntryLink);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static FragmentEntryLink update(
		FragmentEntryLink fragmentEntryLink, ServiceContext serviceContext) {

		return getPersistence().update(fragmentEntryLink, serviceContext);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByUuid_First(
			String uuid, OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByUuid_First(
		String uuid, OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of fragment entry links where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching fragment entry links
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the fragment entry link where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryLinkException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByUUID_G(String uuid, long groupId)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the fragment entry link where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the fragment entry link where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the fragment entry link that was removed
	 */
	public static FragmentEntryLink removeByUUID_G(String uuid, long groupId)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of fragment entry links where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching fragment entry links
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of fragment entry links where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching fragment entry links
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByGroupId_First(
			long groupId,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByGroupId_First(
		long groupId, OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching fragment entry links
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByType(
		int type, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByType(
			type, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByType_First(
			int type, OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByType_First(type, orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByType_First(
		int type, OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByType_First(type, orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	public static void removeByType(int type) {
		getPersistence().removeByType(type);
	}

	/**
	 * Returns the number of fragment entry links where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching fragment entry links
	 */
	public static int countByType(int type) {
		return getPersistence().countByType(type);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_P(
		long groupId, long plid, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P(
			groupId, plid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByG_P_First(
			long groupId, long plid,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByG_P_First(
			groupId, plid, orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByG_P_First(
		long groupId, long plid,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByG_P_First(
			groupId, plid, orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 */
	public static void removeByG_P(long groupId, long plid) {
		getPersistence().removeByG_P(groupId, plid);
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the number of matching fragment entry links
	 */
	public static int countByG_P(long groupId, long plid) {
		return getPersistence().countByG_P(groupId, plid);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where companyId = &#63; and rendererKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByC_R(
		long companyId, String rendererKey, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_R(
			companyId, rendererKey, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where companyId = &#63; and rendererKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByC_R_First(
			long companyId, String rendererKey,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByC_R_First(
			companyId, rendererKey, orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where companyId = &#63; and rendererKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByC_R_First(
		long companyId, String rendererKey,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByC_R_First(
			companyId, rendererKey, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where companyId = &#63; and rendererKey = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param rendererKeys the renderer keys
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByC_R(
		long companyId, String[] rendererKeys, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_R(
			companyId, rendererKeys, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the fragment entry links where companyId = &#63; and rendererKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 */
	public static void removeByC_R(long companyId, String rendererKey) {
		getPersistence().removeByC_R(companyId, rendererKey);
	}

	/**
	 * Returns the number of fragment entry links where companyId = &#63; and rendererKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @return the number of matching fragment entry links
	 */
	public static int countByC_R(long companyId, String rendererKey) {
		return getPersistence().countByC_R(companyId, rendererKey);
	}

	/**
	 * Returns the number of fragment entry links where companyId = &#63; and rendererKey = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param rendererKeys the renderer keys
	 * @return the number of matching fragment entry links
	 */
	public static int countByC_R(long companyId, String[] rendererKeys) {
		return getPersistence().countByC_R(companyId, rendererKeys);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByFEERC_FESERC(
		String fragmentEntryERC, String fragmentEntryScopeERC, int start,
		int end, OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByFEERC_FESERC(
			fragmentEntryERC, fragmentEntryScopeERC, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByFEERC_FESERC_First(
			String fragmentEntryERC, String fragmentEntryScopeERC,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByFEERC_FESERC_First(
			fragmentEntryERC, fragmentEntryScopeERC, orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByFEERC_FESERC_First(
		String fragmentEntryERC, String fragmentEntryScopeERC,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByFEERC_FESERC_First(
			fragmentEntryERC, fragmentEntryScopeERC, orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; from the database.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 */
	public static void removeByFEERC_FESERC(
		String fragmentEntryERC, String fragmentEntryScopeERC) {

		getPersistence().removeByFEERC_FESERC(
			fragmentEntryERC, fragmentEntryScopeERC);
	}

	/**
	 * Returns the number of fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @return the number of matching fragment entry links
	 */
	public static int countByFEERC_FESERC(
		String fragmentEntryERC, String fragmentEntryScopeERC) {

		return getPersistence().countByFEERC_FESERC(
			fragmentEntryERC, fragmentEntryScopeERC);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_OFELERC_P(
		long groupId, String originalFragmentEntryLinkERC, long plid, int start,
		int end, OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_OFELERC_P(
			groupId, originalFragmentEntryLinkERC, plid, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByG_OFELERC_P_First(
			long groupId, String originalFragmentEntryLinkERC, long plid,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByG_OFELERC_P_First(
			groupId, originalFragmentEntryLinkERC, plid, orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByG_OFELERC_P_First(
		long groupId, String originalFragmentEntryLinkERC, long plid,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByG_OFELERC_P_First(
			groupId, originalFragmentEntryLinkERC, plid, orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 */
	public static void removeByG_OFELERC_P(
		long groupId, String originalFragmentEntryLinkERC, long plid) {

		getPersistence().removeByG_OFELERC_P(
			groupId, originalFragmentEntryLinkERC, plid);
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @return the number of matching fragment entry links
	 */
	public static int countByG_OFELERC_P(
		long groupId, String originalFragmentEntryLinkERC, long plid) {

		return getPersistence().countByG_OFELERC_P(
			groupId, originalFragmentEntryLinkERC, plid);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FEERC_FESERC(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByG_FEERC_FESERC_First(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByG_FEERC_FESERC_First(
			groupId, fragmentEntryERC, fragmentEntryScopeERC,
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByG_FEERC_FESERC_First(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByG_FEERC_FESERC_First(
			groupId, fragmentEntryERC, fragmentEntryScopeERC,
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 */
	public static void removeByG_FEERC_FESERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC) {

		getPersistence().removeByG_FEERC_FESERC(
			groupId, fragmentEntryERC, fragmentEntryScopeERC);
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @return the number of matching fragment entry links
	 */
	public static int countByG_FEERC_FESERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC) {

		return getPersistence().countByG_FEERC_FESERC(
			groupId, fragmentEntryERC, fragmentEntryScopeERC);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P(
		long groupId, long segmentsExperienceId, long plid, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_S_P(
			groupId, segmentsExperienceId, plid, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByG_S_P_First(
			long groupId, long segmentsExperienceId, long plid,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByG_S_P_First(
			groupId, segmentsExperienceId, plid, orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByG_S_P_First(
		long groupId, long segmentsExperienceId, long plid,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByG_S_P_First(
			groupId, segmentsExperienceId, plid, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P(
		long groupId, long[] segmentsExperienceIds, long plid, int start,
		int end, OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_S_P(
			groupId, segmentsExperienceIds, plid, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 */
	public static void removeByG_S_P(
		long groupId, long segmentsExperienceId, long plid) {

		getPersistence().removeByG_S_P(groupId, segmentsExperienceId, plid);
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @return the number of matching fragment entry links
	 */
	public static int countByG_S_P(
		long groupId, long segmentsExperienceId, long plid) {

		return getPersistence().countByG_S_P(
			groupId, segmentsExperienceId, plid);
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and segmentsExperienceId = any &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @return the number of matching fragment entry links
	 */
	public static int countByG_S_P(
		long groupId, long[] segmentsExperienceIds, long plid) {

		return getPersistence().countByG_S_P(
			groupId, segmentsExperienceIds, plid);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_C(
			groupId, classNameId, classPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByG_C_C_First(
			long groupId, long classNameId, long classPK,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByG_C_C_First(
			groupId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByG_C_C_First(
		long groupId, long classNameId, long classPK,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByG_C_C_First(
			groupId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByG_C_C(
		long groupId, long classNameId, long classPK) {

		getPersistence().removeByG_C_C(groupId, classNameId, classPK);
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching fragment entry links
	 */
	public static int countByG_C_C(
		long groupId, long classNameId, long classPK) {

		return getPersistence().countByG_C_C(groupId, classNameId, classPK);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_P_D(
		long groupId, long plid, boolean deleted, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_D(
			groupId, plid, deleted, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByG_P_D_First(
			long groupId, long plid, boolean deleted,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByG_P_D_First(
			groupId, plid, deleted, orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByG_P_D_First(
		long groupId, long plid, boolean deleted,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByG_P_D_First(
			groupId, plid, deleted, orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and plid = &#63; and deleted = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 */
	public static void removeByG_P_D(long groupId, long plid, boolean deleted) {
		getPersistence().removeByG_P_D(groupId, plid, deleted);
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @return the number of matching fragment entry links
	 */
	public static int countByG_P_D(long groupId, long plid, boolean deleted) {
		return getPersistence().countByG_P_D(groupId, plid, deleted);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByFEERC_FESERC_D(
		String fragmentEntryERC, String fragmentEntryScopeERC, boolean deleted,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByFEERC_FESERC_D(
			fragmentEntryERC, fragmentEntryScopeERC, deleted, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByFEERC_FESERC_D_First(
			String fragmentEntryERC, String fragmentEntryScopeERC,
			boolean deleted,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByFEERC_FESERC_D_First(
			fragmentEntryERC, fragmentEntryScopeERC, deleted,
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByFEERC_FESERC_D_First(
		String fragmentEntryERC, String fragmentEntryScopeERC, boolean deleted,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByFEERC_FESERC_D_First(
			fragmentEntryERC, fragmentEntryScopeERC, deleted,
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63; from the database.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 */
	public static void removeByFEERC_FESERC_D(
		String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted) {

		getPersistence().removeByFEERC_FESERC_D(
			fragmentEntryERC, fragmentEntryScopeERC, deleted);
	}

	/**
	 * Returns the number of fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @return the number of matching fragment entry links
	 */
	public static int countByFEERC_FESERC_D(
		String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted) {

		return getPersistence().countByFEERC_FESERC_D(
			fragmentEntryERC, fragmentEntryScopeERC, deleted);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FEERC_FESERC_C(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId,
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByG_FEERC_FESERC_C_First(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			long classNameId,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByG_FEERC_FESERC_C_First(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId,
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByG_FEERC_FESERC_C_First(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByG_FEERC_FESERC_C_First(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId,
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 */
	public static void removeByG_FEERC_FESERC_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId) {

		getPersistence().removeByG_FEERC_FESERC_C(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId);
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @return the number of matching fragment entry links
	 */
	public static int countByG_FEERC_FESERC_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId) {

		return getPersistence().countByG_FEERC_FESERC_C(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC_P(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long plid, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FEERC_FESERC_P(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, plid, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByG_FEERC_FESERC_P_First(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			long plid, OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByG_FEERC_FESERC_P_First(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, plid,
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByG_FEERC_FESERC_P_First(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long plid, OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByG_FEERC_FESERC_P_First(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, plid,
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 */
	public static void removeByG_FEERC_FESERC_P(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long plid) {

		getPersistence().removeByG_FEERC_FESERC_P(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, plid);
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @return the number of matching fragment entry links
	 */
	public static int countByG_FEERC_FESERC_P(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long plid) {

		return getPersistence().countByG_FEERC_FESERC_P(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, plid);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC_D(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FEERC_FESERC_D(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, deleted, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByG_FEERC_FESERC_D_First(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			boolean deleted,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByG_FEERC_FESERC_D_First(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, deleted,
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByG_FEERC_FESERC_D_First(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByG_FEERC_FESERC_D_First(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, deleted,
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 */
	public static void removeByG_FEERC_FESERC_D(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted) {

		getPersistence().removeByG_FEERC_FESERC_D(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, deleted);
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @return the number of matching fragment entry links
	 */
	public static int countByG_FEERC_FESERC_D(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted) {

		return getPersistence().countByG_FEERC_FESERC_D(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, deleted);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_C_C(
		long groupId, long segmentsExperienceId, long classNameId, long classPK,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_S_C_C(
			groupId, segmentsExperienceId, classNameId, classPK, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByG_S_C_C_First(
			long groupId, long segmentsExperienceId, long classNameId,
			long classPK,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByG_S_C_C_First(
			groupId, segmentsExperienceId, classNameId, classPK,
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByG_S_C_C_First(
		long groupId, long segmentsExperienceId, long classNameId, long classPK,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByG_S_C_C_First(
			groupId, segmentsExperienceId, classNameId, classPK,
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByG_S_C_C(
		long groupId, long segmentsExperienceId, long classNameId,
		long classPK) {

		getPersistence().removeByG_S_C_C(
			groupId, segmentsExperienceId, classNameId, classPK);
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching fragment entry links
	 */
	public static int countByG_S_C_C(
		long groupId, long segmentsExperienceId, long classNameId,
		long classPK) {

		return getPersistence().countByG_S_C_C(
			groupId, segmentsExperienceId, classNameId, classPK);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P_D(
		long groupId, long segmentsExperienceId, long plid, boolean deleted,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_S_P_D(
			groupId, segmentsExperienceId, plid, deleted, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByG_S_P_D_First(
			long groupId, long segmentsExperienceId, long plid, boolean deleted,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByG_S_P_D_First(
			groupId, segmentsExperienceId, plid, deleted, orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByG_S_P_D_First(
		long groupId, long segmentsExperienceId, long plid, boolean deleted,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByG_S_P_D_First(
			groupId, segmentsExperienceId, plid, deleted, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P_D(
		long groupId, long[] segmentsExperienceIds, long plid, boolean deleted,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_S_P_D(
			groupId, segmentsExperienceIds, plid, deleted, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 */
	public static void removeByG_S_P_D(
		long groupId, long segmentsExperienceId, long plid, boolean deleted) {

		getPersistence().removeByG_S_P_D(
			groupId, segmentsExperienceId, plid, deleted);
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @return the number of matching fragment entry links
	 */
	public static int countByG_S_P_D(
		long groupId, long segmentsExperienceId, long plid, boolean deleted) {

		return getPersistence().countByG_S_P_D(
			groupId, segmentsExperienceId, plid, deleted);
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and segmentsExperienceId = any &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @param deleted the deleted
	 * @return the number of matching fragment entry links
	 */
	public static int countByG_S_P_D(
		long groupId, long[] segmentsExperienceIds, long plid,
		boolean deleted) {

		return getPersistence().countByG_S_P_D(
			groupId, segmentsExperienceIds, plid, deleted);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P_R(
		long groupId, long segmentsExperienceId, long plid, String rendererKey,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_S_P_R(
			groupId, segmentsExperienceId, plid, rendererKey, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByG_S_P_R_First(
			long groupId, long segmentsExperienceId, long plid,
			String rendererKey,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByG_S_P_R_First(
			groupId, segmentsExperienceId, plid, rendererKey,
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByG_S_P_R_First(
		long groupId, long segmentsExperienceId, long plid, String rendererKey,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByG_S_P_R_First(
			groupId, segmentsExperienceId, plid, rendererKey,
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 */
	public static void removeByG_S_P_R(
		long groupId, long segmentsExperienceId, long plid,
		String rendererKey) {

		getPersistence().removeByG_S_P_R(
			groupId, segmentsExperienceId, plid, rendererKey);
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @return the number of matching fragment entry links
	 */
	public static int countByG_S_P_R(
		long groupId, long segmentsExperienceId, long plid,
		String rendererKey) {

		return getPersistence().countByG_S_P_R(
			groupId, segmentsExperienceId, plid, rendererKey);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC_C_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, long classPK, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FEERC_FESERC_C_C(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId,
			classPK, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByG_FEERC_FESERC_C_C_First(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			long classNameId, long classPK,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByG_FEERC_FESERC_C_C_First(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId,
			classPK, orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByG_FEERC_FESERC_C_C_First(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, long classPK,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().fetchByG_FEERC_FESERC_C_C_First(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId,
			classPK, orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByG_FEERC_FESERC_C_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, long classPK) {

		getPersistence().removeByG_FEERC_FESERC_C_C(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId,
			classPK);
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching fragment entry links
	 */
	public static int countByG_FEERC_FESERC_C_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, long classPK) {

		return getPersistence().countByG_FEERC_FESERC_C_C(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId,
			classPK);
	}

	/**
	 * Returns the fragment entry link where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchEntryLinkException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink findByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the fragment entry link where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the fragment entry link where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the fragment entry link that was removed
	 */
	public static FragmentEntryLink removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of fragment entry links where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching fragment entry links
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Creates a new fragment entry link with the primary key. Does not add the fragment entry link to the database.
	 *
	 * @param fragmentEntryLinkId the primary key for the new fragment entry link
	 * @return the new fragment entry link
	 */
	public static FragmentEntryLink create(long fragmentEntryLinkId) {
		return getPersistence().create(fragmentEntryLinkId);
	}

	/**
	 * Removes the fragment entry link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fragmentEntryLinkId the primary key of the fragment entry link
	 * @return the fragment entry link that was removed
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public static FragmentEntryLink remove(long fragmentEntryLinkId)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().remove(fragmentEntryLinkId);
	}

	public static FragmentEntryLink updateImpl(
		FragmentEntryLink fragmentEntryLink) {

		return getPersistence().updateImpl(fragmentEntryLink);
	}

	/**
	 * Returns the fragment entry link with the primary key or throws a <code>NoSuchEntryLinkException</code> if it could not be found.
	 *
	 * @param fragmentEntryLinkId the primary key of the fragment entry link
	 * @return the fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public static FragmentEntryLink findByPrimaryKey(long fragmentEntryLinkId)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {

		return getPersistence().findByPrimaryKey(fragmentEntryLinkId);
	}

	/**
	 * Returns the fragment entry link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fragmentEntryLinkId the primary key of the fragment entry link
	 * @return the fragment entry link, or <code>null</code> if a fragment entry link with the primary key could not be found
	 */
	public static FragmentEntryLink fetchByPrimaryKey(
		long fragmentEntryLinkId) {

		return getPersistence().fetchByPrimaryKey(fragmentEntryLinkId);
	}

	/**
	 * Returns the fragment entry link where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the fragment entry link where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns all the fragment entry links where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the fragment entry links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the fragment entry links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where type = &#63;.
	 *
	 * @param type the type
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByType(int type) {
		return getPersistence().findByType(type);
	}

	/**
	 * Returns a range of all the fragment entry links where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByType(
		int type, int start, int end) {

		return getPersistence().findByType(type, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByType(
		int type, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByType(type, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_P(long groupId, long plid) {
		return getPersistence().findByG_P(groupId, plid);
	}

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_P(
		long groupId, long plid, int start, int end) {

		return getPersistence().findByG_P(groupId, plid, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_P(
		long groupId, long plid, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByG_P(
			groupId, plid, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where companyId = &#63; and rendererKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByC_R(
		long companyId, String rendererKey) {

		return getPersistence().findByC_R(companyId, rendererKey);
	}

	/**
	 * Returns a range of all the fragment entry links where companyId = &#63; and rendererKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByC_R(
		long companyId, String rendererKey, int start, int end) {

		return getPersistence().findByC_R(companyId, rendererKey, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where companyId = &#63; and rendererKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByC_R(
		long companyId, String rendererKey, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByC_R(
			companyId, rendererKey, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where companyId = &#63; and rendererKey = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param rendererKeys the renderer keys
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByC_R(
		long companyId, String[] rendererKeys) {

		return getPersistence().findByC_R(companyId, rendererKeys);
	}

	/**
	 * Returns a range of all the fragment entry links where companyId = &#63; and rendererKey = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param rendererKeys the renderer keys
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByC_R(
		long companyId, String[] rendererKeys, int start, int end) {

		return getPersistence().findByC_R(companyId, rendererKeys, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where companyId = &#63; and rendererKey = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param rendererKeys the renderer keys
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByC_R(
		long companyId, String[] rendererKeys, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByC_R(
			companyId, rendererKeys, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByFEERC_FESERC(
		String fragmentEntryERC, String fragmentEntryScopeERC) {

		return getPersistence().findByFEERC_FESERC(
			fragmentEntryERC, fragmentEntryScopeERC);
	}

	/**
	 * Returns a range of all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByFEERC_FESERC(
		String fragmentEntryERC, String fragmentEntryScopeERC, int start,
		int end) {

		return getPersistence().findByFEERC_FESERC(
			fragmentEntryERC, fragmentEntryScopeERC, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByFEERC_FESERC(
		String fragmentEntryERC, String fragmentEntryScopeERC, int start,
		int end, OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByFEERC_FESERC(
			fragmentEntryERC, fragmentEntryScopeERC, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_OFELERC_P(
		long groupId, String originalFragmentEntryLinkERC, long plid) {

		return getPersistence().findByG_OFELERC_P(
			groupId, originalFragmentEntryLinkERC, plid);
	}

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_OFELERC_P(
		long groupId, String originalFragmentEntryLinkERC, long plid, int start,
		int end) {

		return getPersistence().findByG_OFELERC_P(
			groupId, originalFragmentEntryLinkERC, plid, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_OFELERC_P(
		long groupId, String originalFragmentEntryLinkERC, long plid, int start,
		int end, OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByG_OFELERC_P(
			groupId, originalFragmentEntryLinkERC, plid, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC) {

		return getPersistence().findByG_FEERC_FESERC(
			groupId, fragmentEntryERC, fragmentEntryScopeERC);
	}

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		int start, int end) {

		return getPersistence().findByG_FEERC_FESERC(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByG_FEERC_FESERC(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P(
		long groupId, long segmentsExperienceId, long plid) {

		return getPersistence().findByG_S_P(
			groupId, segmentsExperienceId, plid);
	}

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P(
		long groupId, long segmentsExperienceId, long plid, int start,
		int end) {

		return getPersistence().findByG_S_P(
			groupId, segmentsExperienceId, plid, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P(
		long groupId, long segmentsExperienceId, long plid, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByG_S_P(
			groupId, segmentsExperienceId, plid, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where groupId = &#63; and segmentsExperienceId = any &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P(
		long groupId, long[] segmentsExperienceIds, long plid) {

		return getPersistence().findByG_S_P(
			groupId, segmentsExperienceIds, plid);
	}

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = any &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P(
		long groupId, long[] segmentsExperienceIds, long plid, int start,
		int end) {

		return getPersistence().findByG_S_P(
			groupId, segmentsExperienceIds, plid, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = any &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P(
		long groupId, long[] segmentsExperienceIds, long plid, int start,
		int end, OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByG_S_P(
			groupId, segmentsExperienceIds, plid, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_C_C(
		long groupId, long classNameId, long classPK) {

		return getPersistence().findByG_C_C(groupId, classNameId, classPK);
	}

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end) {

		return getPersistence().findByG_C_C(
			groupId, classNameId, classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByG_C_C(
			groupId, classNameId, classPK, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_P_D(
		long groupId, long plid, boolean deleted) {

		return getPersistence().findByG_P_D(groupId, plid, deleted);
	}

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_P_D(
		long groupId, long plid, boolean deleted, int start, int end) {

		return getPersistence().findByG_P_D(groupId, plid, deleted, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_P_D(
		long groupId, long plid, boolean deleted, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByG_P_D(
			groupId, plid, deleted, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByFEERC_FESERC_D(
		String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted) {

		return getPersistence().findByFEERC_FESERC_D(
			fragmentEntryERC, fragmentEntryScopeERC, deleted);
	}

	/**
	 * Returns a range of all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByFEERC_FESERC_D(
		String fragmentEntryERC, String fragmentEntryScopeERC, boolean deleted,
		int start, int end) {

		return getPersistence().findByFEERC_FESERC_D(
			fragmentEntryERC, fragmentEntryScopeERC, deleted, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByFEERC_FESERC_D(
		String fragmentEntryERC, String fragmentEntryScopeERC, boolean deleted,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByFEERC_FESERC_D(
			fragmentEntryERC, fragmentEntryScopeERC, deleted, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId) {

		return getPersistence().findByG_FEERC_FESERC_C(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId);
	}

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, int start, int end) {

		return getPersistence().findByG_FEERC_FESERC_C(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId,
			start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByG_FEERC_FESERC_C(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId,
			start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC_P(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long plid) {

		return getPersistence().findByG_FEERC_FESERC_P(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, plid);
	}

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC_P(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long plid, int start, int end) {

		return getPersistence().findByG_FEERC_FESERC_P(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, plid, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC_P(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long plid, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByG_FEERC_FESERC_P(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, plid, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC_D(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted) {

		return getPersistence().findByG_FEERC_FESERC_D(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, deleted);
	}

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC_D(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted, int start, int end) {

		return getPersistence().findByG_FEERC_FESERC_D(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, deleted, start,
			end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC_D(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByG_FEERC_FESERC_D(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, deleted, start,
			end, orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_C_C(
		long groupId, long segmentsExperienceId, long classNameId,
		long classPK) {

		return getPersistence().findByG_S_C_C(
			groupId, segmentsExperienceId, classNameId, classPK);
	}

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_C_C(
		long groupId, long segmentsExperienceId, long classNameId, long classPK,
		int start, int end) {

		return getPersistence().findByG_S_C_C(
			groupId, segmentsExperienceId, classNameId, classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_C_C(
		long groupId, long segmentsExperienceId, long classNameId, long classPK,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByG_S_C_C(
			groupId, segmentsExperienceId, classNameId, classPK, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P_D(
		long groupId, long segmentsExperienceId, long plid, boolean deleted) {

		return getPersistence().findByG_S_P_D(
			groupId, segmentsExperienceId, plid, deleted);
	}

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P_D(
		long groupId, long segmentsExperienceId, long plid, boolean deleted,
		int start, int end) {

		return getPersistence().findByG_S_P_D(
			groupId, segmentsExperienceId, plid, deleted, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P_D(
		long groupId, long segmentsExperienceId, long plid, boolean deleted,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByG_S_P_D(
			groupId, segmentsExperienceId, plid, deleted, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where groupId = &#63; and segmentsExperienceId = any &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @param deleted the deleted
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P_D(
		long groupId, long[] segmentsExperienceIds, long plid,
		boolean deleted) {

		return getPersistence().findByG_S_P_D(
			groupId, segmentsExperienceIds, plid, deleted);
	}

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = any &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P_D(
		long groupId, long[] segmentsExperienceIds, long plid, boolean deleted,
		int start, int end) {

		return getPersistence().findByG_S_P_D(
			groupId, segmentsExperienceIds, plid, deleted, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = any &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P_D(
		long groupId, long[] segmentsExperienceIds, long plid, boolean deleted,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByG_S_P_D(
			groupId, segmentsExperienceIds, plid, deleted, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P_R(
		long groupId, long segmentsExperienceId, long plid,
		String rendererKey) {

		return getPersistence().findByG_S_P_R(
			groupId, segmentsExperienceId, plid, rendererKey);
	}

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P_R(
		long groupId, long segmentsExperienceId, long plid, String rendererKey,
		int start, int end) {

		return getPersistence().findByG_S_P_R(
			groupId, segmentsExperienceId, plid, rendererKey, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_S_P_R(
		long groupId, long segmentsExperienceId, long plid, String rendererKey,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByG_S_P_R(
			groupId, segmentsExperienceId, plid, rendererKey, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC_C_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, long classPK) {

		return getPersistence().findByG_FEERC_FESERC_C_C(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId,
			classPK);
	}

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC_C_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, long classPK, int start, int end) {

		return getPersistence().findByG_FEERC_FESERC_C_C(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId,
			classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public static List<FragmentEntryLink> findByG_FEERC_FESERC_C_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, long classPK, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getPersistence().findByG_FEERC_FESERC_C_C(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId,
			classPK, start, end, orderByComparator);
	}

	public static FragmentEntryLinkPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		FragmentEntryLinkPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile FragmentEntryLinkPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:986856161