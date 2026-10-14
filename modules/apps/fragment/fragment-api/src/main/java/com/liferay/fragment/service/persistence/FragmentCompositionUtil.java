/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.persistence;

import com.liferay.fragment.model.FragmentComposition;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the fragment composition service. This utility wraps <code>com.liferay.fragment.service.persistence.impl.FragmentCompositionPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FragmentCompositionPersistence
 * @generated
 */
public class FragmentCompositionUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<FragmentComposition> fragmentCompositions) {

		getPersistence().cacheResult(fragmentCompositions);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(FragmentComposition fragmentComposition) {
		getPersistence().cacheResult(fragmentComposition);
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
	public static void clearCache(FragmentComposition fragmentComposition) {
		getPersistence().clearCache(fragmentComposition);
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
	public static Map<Serializable, FragmentComposition> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<FragmentComposition> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<FragmentComposition> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<FragmentComposition> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<FragmentComposition> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static FragmentComposition update(
		FragmentComposition fragmentComposition) {

		return getPersistence().update(fragmentComposition);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static FragmentComposition update(
		FragmentComposition fragmentComposition,
		ServiceContext serviceContext) {

		return getPersistence().update(fragmentComposition, serviceContext);
	}

	/**
	 * Returns an ordered range of all the fragment compositions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<FragmentComposition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment composition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment composition
	 * @throws NoSuchCompositionException if a matching fragment composition could not be found
	 */
	public static FragmentComposition findByUuid_First(
			String uuid,
			OrderByComparator<FragmentComposition> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchCompositionException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first fragment composition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment composition, or <code>null</code> if a matching fragment composition could not be found
	 */
	public static FragmentComposition fetchByUuid_First(
		String uuid, OrderByComparator<FragmentComposition> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the fragment compositions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of fragment compositions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching fragment compositions
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the fragment composition where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCompositionException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching fragment composition
	 * @throws NoSuchCompositionException if a matching fragment composition could not be found
	 */
	public static FragmentComposition findByUUID_G(String uuid, long groupId)
		throws com.liferay.fragment.exception.NoSuchCompositionException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the fragment composition where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment composition, or <code>null</code> if a matching fragment composition could not be found
	 */
	public static FragmentComposition fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the fragment composition where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the fragment composition that was removed
	 */
	public static FragmentComposition removeByUUID_G(String uuid, long groupId)
		throws com.liferay.fragment.exception.NoSuchCompositionException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of fragment compositions where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching fragment compositions
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the fragment compositions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<FragmentComposition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment composition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment composition
	 * @throws NoSuchCompositionException if a matching fragment composition could not be found
	 */
	public static FragmentComposition findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<FragmentComposition> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchCompositionException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first fragment composition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment composition, or <code>null</code> if a matching fragment composition could not be found
	 */
	public static FragmentComposition fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<FragmentComposition> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the fragment compositions where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of fragment compositions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching fragment compositions
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the fragment compositions where fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByFragmentCollectionId(
		long fragmentCollectionId, int start, int end,
		OrderByComparator<FragmentComposition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByFragmentCollectionId(
			fragmentCollectionId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment composition in the ordered set where fragmentCollectionId = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment composition
	 * @throws NoSuchCompositionException if a matching fragment composition could not be found
	 */
	public static FragmentComposition findByFragmentCollectionId_First(
			long fragmentCollectionId,
			OrderByComparator<FragmentComposition> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchCompositionException {

		return getPersistence().findByFragmentCollectionId_First(
			fragmentCollectionId, orderByComparator);
	}

	/**
	 * Returns the first fragment composition in the ordered set where fragmentCollectionId = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment composition, or <code>null</code> if a matching fragment composition could not be found
	 */
	public static FragmentComposition fetchByFragmentCollectionId_First(
		long fragmentCollectionId,
		OrderByComparator<FragmentComposition> orderByComparator) {

		return getPersistence().fetchByFragmentCollectionId_First(
			fragmentCollectionId, orderByComparator);
	}

	/**
	 * Removes all the fragment compositions where fragmentCollectionId = &#63; from the database.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 */
	public static void removeByFragmentCollectionId(long fragmentCollectionId) {
		getPersistence().removeByFragmentCollectionId(fragmentCollectionId);
	}

	/**
	 * Returns the number of fragment compositions where fragmentCollectionId = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @return the number of matching fragment compositions
	 */
	public static int countByFragmentCollectionId(long fragmentCollectionId) {
		return getPersistence().countByFragmentCollectionId(
			fragmentCollectionId);
	}

	/**
	 * Returns an ordered range of all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByG_FCI(
		long groupId, long fragmentCollectionId, int start, int end,
		OrderByComparator<FragmentComposition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FCI(
			groupId, fragmentCollectionId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment composition in the ordered set where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment composition
	 * @throws NoSuchCompositionException if a matching fragment composition could not be found
	 */
	public static FragmentComposition findByG_FCI_First(
			long groupId, long fragmentCollectionId,
			OrderByComparator<FragmentComposition> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchCompositionException {

		return getPersistence().findByG_FCI_First(
			groupId, fragmentCollectionId, orderByComparator);
	}

	/**
	 * Returns the first fragment composition in the ordered set where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment composition, or <code>null</code> if a matching fragment composition could not be found
	 */
	public static FragmentComposition fetchByG_FCI_First(
		long groupId, long fragmentCollectionId,
		OrderByComparator<FragmentComposition> orderByComparator) {

		return getPersistence().fetchByG_FCI_First(
			groupId, fragmentCollectionId, orderByComparator);
	}

	/**
	 * Removes all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 */
	public static void removeByG_FCI(long groupId, long fragmentCollectionId) {
		getPersistence().removeByG_FCI(groupId, fragmentCollectionId);
	}

	/**
	 * Returns the number of fragment compositions where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @return the number of matching fragment compositions
	 */
	public static int countByG_FCI(long groupId, long fragmentCollectionId) {
		return getPersistence().countByG_FCI(groupId, fragmentCollectionId);
	}

	/**
	 * Returns the fragment composition where groupId = &#63; and fragmentCompositionKey = &#63; or throws a <code>NoSuchCompositionException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param fragmentCompositionKey the fragment composition key
	 * @return the matching fragment composition
	 * @throws NoSuchCompositionException if a matching fragment composition could not be found
	 */
	public static FragmentComposition findByG_FCK(
			long groupId, String fragmentCompositionKey)
		throws com.liferay.fragment.exception.NoSuchCompositionException {

		return getPersistence().findByG_FCK(groupId, fragmentCompositionKey);
	}

	/**
	 * Returns the fragment composition where groupId = &#63; and fragmentCompositionKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param fragmentCompositionKey the fragment composition key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment composition, or <code>null</code> if a matching fragment composition could not be found
	 */
	public static FragmentComposition fetchByG_FCK(
		long groupId, String fragmentCompositionKey, boolean useFinderCache) {

		return getPersistence().fetchByG_FCK(
			groupId, fragmentCompositionKey, useFinderCache);
	}

	/**
	 * Removes the fragment composition where groupId = &#63; and fragmentCompositionKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCompositionKey the fragment composition key
	 * @return the fragment composition that was removed
	 */
	public static FragmentComposition removeByG_FCK(
			long groupId, String fragmentCompositionKey)
		throws com.liferay.fragment.exception.NoSuchCompositionException {

		return getPersistence().removeByG_FCK(groupId, fragmentCompositionKey);
	}

	/**
	 * Returns the number of fragment compositions where groupId = &#63; and fragmentCompositionKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCompositionKey the fragment composition key
	 * @return the number of matching fragment compositions
	 */
	public static int countByG_FCK(
		long groupId, String fragmentCompositionKey) {

		return getPersistence().countByG_FCK(groupId, fragmentCompositionKey);
	}

	/**
	 * Returns all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @return the matching fragment compositions
	 */
	public static List<FragmentComposition> findByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name) {

		return getPersistence().findByG_FCI_LikeN(
			groupId, fragmentCollectionId, name);
	}

	/**
	 * Returns a range of all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @return the range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name, int start,
		int end) {

		return getPersistence().findByG_FCI_LikeN(
			groupId, fragmentCollectionId, name, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name, int start,
		int end, OrderByComparator<FragmentComposition> orderByComparator) {

		return getPersistence().findByG_FCI_LikeN(
			groupId, fragmentCollectionId, name, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name, int start,
		int end, OrderByComparator<FragmentComposition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FCI_LikeN(
			groupId, fragmentCollectionId, name, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment composition in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment composition
	 * @throws NoSuchCompositionException if a matching fragment composition could not be found
	 */
	public static FragmentComposition findByG_FCI_LikeN_First(
			long groupId, long fragmentCollectionId, String name,
			OrderByComparator<FragmentComposition> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchCompositionException {

		return getPersistence().findByG_FCI_LikeN_First(
			groupId, fragmentCollectionId, name, orderByComparator);
	}

	/**
	 * Returns the first fragment composition in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment composition, or <code>null</code> if a matching fragment composition could not be found
	 */
	public static FragmentComposition fetchByG_FCI_LikeN_First(
		long groupId, long fragmentCollectionId, String name,
		OrderByComparator<FragmentComposition> orderByComparator) {

		return getPersistence().fetchByG_FCI_LikeN_First(
			groupId, fragmentCollectionId, name, orderByComparator);
	}

	/**
	 * Removes all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 */
	public static void removeByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name) {

		getPersistence().removeByG_FCI_LikeN(
			groupId, fragmentCollectionId, name);
	}

	/**
	 * Returns the number of fragment compositions where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @return the number of matching fragment compositions
	 */
	public static int countByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name) {

		return getPersistence().countByG_FCI_LikeN(
			groupId, fragmentCollectionId, name);
	}

	/**
	 * Returns an ordered range of all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByG_FCI_S(
		long groupId, long fragmentCollectionId, int status, int start, int end,
		OrderByComparator<FragmentComposition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FCI_S(
			groupId, fragmentCollectionId, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment composition in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment composition
	 * @throws NoSuchCompositionException if a matching fragment composition could not be found
	 */
	public static FragmentComposition findByG_FCI_S_First(
			long groupId, long fragmentCollectionId, int status,
			OrderByComparator<FragmentComposition> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchCompositionException {

		return getPersistence().findByG_FCI_S_First(
			groupId, fragmentCollectionId, status, orderByComparator);
	}

	/**
	 * Returns the first fragment composition in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment composition, or <code>null</code> if a matching fragment composition could not be found
	 */
	public static FragmentComposition fetchByG_FCI_S_First(
		long groupId, long fragmentCollectionId, int status,
		OrderByComparator<FragmentComposition> orderByComparator) {

		return getPersistence().fetchByG_FCI_S_First(
			groupId, fragmentCollectionId, status, orderByComparator);
	}

	/**
	 * Removes all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 */
	public static void removeByG_FCI_S(
		long groupId, long fragmentCollectionId, int status) {

		getPersistence().removeByG_FCI_S(groupId, fragmentCollectionId, status);
	}

	/**
	 * Returns the number of fragment compositions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @return the number of matching fragment compositions
	 */
	public static int countByG_FCI_S(
		long groupId, long fragmentCollectionId, int status) {

		return getPersistence().countByG_FCI_S(
			groupId, fragmentCollectionId, status);
	}

	/**
	 * Returns all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @return the matching fragment compositions
	 */
	public static List<FragmentComposition> findByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status) {

		return getPersistence().findByG_FCI_LikeN_S(
			groupId, fragmentCollectionId, name, status);
	}

	/**
	 * Returns a range of all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @return the range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status,
		int start, int end) {

		return getPersistence().findByG_FCI_LikeN_S(
			groupId, fragmentCollectionId, name, status, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status,
		int start, int end,
		OrderByComparator<FragmentComposition> orderByComparator) {

		return getPersistence().findByG_FCI_LikeN_S(
			groupId, fragmentCollectionId, name, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status,
		int start, int end,
		OrderByComparator<FragmentComposition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FCI_LikeN_S(
			groupId, fragmentCollectionId, name, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment composition in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment composition
	 * @throws NoSuchCompositionException if a matching fragment composition could not be found
	 */
	public static FragmentComposition findByG_FCI_LikeN_S_First(
			long groupId, long fragmentCollectionId, String name, int status,
			OrderByComparator<FragmentComposition> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchCompositionException {

		return getPersistence().findByG_FCI_LikeN_S_First(
			groupId, fragmentCollectionId, name, status, orderByComparator);
	}

	/**
	 * Returns the first fragment composition in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment composition, or <code>null</code> if a matching fragment composition could not be found
	 */
	public static FragmentComposition fetchByG_FCI_LikeN_S_First(
		long groupId, long fragmentCollectionId, String name, int status,
		OrderByComparator<FragmentComposition> orderByComparator) {

		return getPersistence().fetchByG_FCI_LikeN_S_First(
			groupId, fragmentCollectionId, name, status, orderByComparator);
	}

	/**
	 * Removes all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 */
	public static void removeByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status) {

		getPersistence().removeByG_FCI_LikeN_S(
			groupId, fragmentCollectionId, name, status);
	}

	/**
	 * Returns the number of fragment compositions where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @return the number of matching fragment compositions
	 */
	public static int countByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status) {

		return getPersistence().countByG_FCI_LikeN_S(
			groupId, fragmentCollectionId, name, status);
	}

	/**
	 * Returns the fragment composition where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchCompositionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching fragment composition
	 * @throws NoSuchCompositionException if a matching fragment composition could not be found
	 */
	public static FragmentComposition findByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.fragment.exception.NoSuchCompositionException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the fragment composition where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment composition, or <code>null</code> if a matching fragment composition could not be found
	 */
	public static FragmentComposition fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the fragment composition where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the fragment composition that was removed
	 */
	public static FragmentComposition removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.fragment.exception.NoSuchCompositionException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of fragment compositions where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching fragment compositions
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Creates a new fragment composition with the primary key. Does not add the fragment composition to the database.
	 *
	 * @param fragmentCompositionId the primary key for the new fragment composition
	 * @return the new fragment composition
	 */
	public static FragmentComposition create(long fragmentCompositionId) {
		return getPersistence().create(fragmentCompositionId);
	}

	/**
	 * Removes the fragment composition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fragmentCompositionId the primary key of the fragment composition
	 * @return the fragment composition that was removed
	 * @throws NoSuchCompositionException if a fragment composition with the primary key could not be found
	 */
	public static FragmentComposition remove(long fragmentCompositionId)
		throws com.liferay.fragment.exception.NoSuchCompositionException {

		return getPersistence().remove(fragmentCompositionId);
	}

	public static FragmentComposition updateImpl(
		FragmentComposition fragmentComposition) {

		return getPersistence().updateImpl(fragmentComposition);
	}

	/**
	 * Returns the fragment composition with the primary key or throws a <code>NoSuchCompositionException</code> if it could not be found.
	 *
	 * @param fragmentCompositionId the primary key of the fragment composition
	 * @return the fragment composition
	 * @throws NoSuchCompositionException if a fragment composition with the primary key could not be found
	 */
	public static FragmentComposition findByPrimaryKey(
			long fragmentCompositionId)
		throws com.liferay.fragment.exception.NoSuchCompositionException {

		return getPersistence().findByPrimaryKey(fragmentCompositionId);
	}

	/**
	 * Returns the fragment composition with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fragmentCompositionId the primary key of the fragment composition
	 * @return the fragment composition, or <code>null</code> if a fragment composition with the primary key could not be found
	 */
	public static FragmentComposition fetchByPrimaryKey(
		long fragmentCompositionId) {

		return getPersistence().fetchByPrimaryKey(fragmentCompositionId);
	}

	/**
	 * Returns the fragment composition where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching fragment composition, or <code>null</code> if a matching fragment composition could not be found
	 */
	public static FragmentComposition fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the fragment composition where groupId = &#63; and fragmentCompositionKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param fragmentCompositionKey the fragment composition key
	 * @return the matching fragment composition, or <code>null</code> if a matching fragment composition could not be found
	 */
	public static FragmentComposition fetchByG_FCK(
		long groupId, String fragmentCompositionKey) {

		return getPersistence().fetchByG_FCK(groupId, fragmentCompositionKey);
	}

	/**
	 * Returns the fragment composition where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching fragment composition, or <code>null</code> if a matching fragment composition could not be found
	 */
	public static FragmentComposition fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns all the fragment compositions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching fragment compositions
	 */
	public static List<FragmentComposition> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the fragment compositions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @return the range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment compositions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<FragmentComposition> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment compositions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching fragment compositions
	 */
	public static List<FragmentComposition> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the fragment compositions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @return the range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment compositions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<FragmentComposition> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment compositions where fragmentCollectionId = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @return the matching fragment compositions
	 */
	public static List<FragmentComposition> findByFragmentCollectionId(
		long fragmentCollectionId) {

		return getPersistence().findByFragmentCollectionId(
			fragmentCollectionId);
	}

	/**
	 * Returns a range of all the fragment compositions where fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @return the range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByFragmentCollectionId(
		long fragmentCollectionId, int start, int end) {

		return getPersistence().findByFragmentCollectionId(
			fragmentCollectionId, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment compositions where fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByFragmentCollectionId(
		long fragmentCollectionId, int start, int end,
		OrderByComparator<FragmentComposition> orderByComparator) {

		return getPersistence().findByFragmentCollectionId(
			fragmentCollectionId, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @return the matching fragment compositions
	 */
	public static List<FragmentComposition> findByG_FCI(
		long groupId, long fragmentCollectionId) {

		return getPersistence().findByG_FCI(groupId, fragmentCollectionId);
	}

	/**
	 * Returns a range of all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @return the range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByG_FCI(
		long groupId, long fragmentCollectionId, int start, int end) {

		return getPersistence().findByG_FCI(
			groupId, fragmentCollectionId, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByG_FCI(
		long groupId, long fragmentCollectionId, int start, int end,
		OrderByComparator<FragmentComposition> orderByComparator) {

		return getPersistence().findByG_FCI(
			groupId, fragmentCollectionId, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @return the matching fragment compositions
	 */
	public static List<FragmentComposition> findByG_FCI_S(
		long groupId, long fragmentCollectionId, int status) {

		return getPersistence().findByG_FCI_S(
			groupId, fragmentCollectionId, status);
	}

	/**
	 * Returns a range of all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @return the range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByG_FCI_S(
		long groupId, long fragmentCollectionId, int status, int start,
		int end) {

		return getPersistence().findByG_FCI_S(
			groupId, fragmentCollectionId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment compositions where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment compositions
	 */
	public static List<FragmentComposition> findByG_FCI_S(
		long groupId, long fragmentCollectionId, int status, int start, int end,
		OrderByComparator<FragmentComposition> orderByComparator) {

		return getPersistence().findByG_FCI_S(
			groupId, fragmentCollectionId, status, start, end,
			orderByComparator);
	}

	public static FragmentCompositionPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		FragmentCompositionPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile FragmentCompositionPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-378238443