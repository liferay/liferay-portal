/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.persistence;

import com.liferay.knowledge.base.model.KBComment;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the kb comment service. This utility wraps <code>com.liferay.knowledge.base.service.persistence.impl.KBCommentPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see KBCommentPersistence
 * @generated
 */
public class KBCommentUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<KBComment> kbComments) {
		getPersistence().cacheResult(kbComments);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(KBComment kbComment) {
		getPersistence().cacheResult(kbComment);
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
	public static void clearCache(KBComment kbComment) {
		getPersistence().clearCache(kbComment);
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
	public static Map<Serializable, KBComment> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<KBComment> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<KBComment> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<KBComment> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<KBComment> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static KBComment update(KBComment kbComment) {
		return getPersistence().update(kbComment);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static KBComment update(
		KBComment kbComment, ServiceContext serviceContext) {

		return getPersistence().update(kbComment, serviceContext);
	}

	/**
	 * Returns an ordered range of all the kb comments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	public static List<KBComment> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<KBComment> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb comment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	public static KBComment findByUuid_First(
			String uuid, OrderByComparator<KBComment> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchCommentException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first kb comment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	public static KBComment fetchByUuid_First(
		String uuid, OrderByComparator<KBComment> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the kb comments where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of kb comments where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching kb comments
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the kb comment where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCommentException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	public static KBComment findByUUID_G(String uuid, long groupId)
		throws com.liferay.knowledge.base.exception.NoSuchCommentException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the kb comment where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	public static KBComment fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the kb comment where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the kb comment that was removed
	 */
	public static KBComment removeByUUID_G(String uuid, long groupId)
		throws com.liferay.knowledge.base.exception.NoSuchCommentException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of kb comments where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching kb comments
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the kb comments where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	public static List<KBComment> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KBComment> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb comment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	public static KBComment findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<KBComment> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchCommentException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first kb comment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	public static KBComment fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<KBComment> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the kb comments where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of kb comments where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching kb comments
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the kb comments where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	public static List<KBComment> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<KBComment> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb comment in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	public static KBComment findByGroupId_First(
			long groupId, OrderByComparator<KBComment> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchCommentException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first kb comment in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	public static KBComment fetchByGroupId_First(
		long groupId, OrderByComparator<KBComment> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Removes all the kb comments where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of kb comments where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching kb comments
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the kb comments where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	public static List<KBComment> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<KBComment> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_S(
			groupId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb comment in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	public static KBComment findByG_S_First(
			long groupId, int status,
			OrderByComparator<KBComment> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchCommentException {

		return getPersistence().findByG_S_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns the first kb comment in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	public static KBComment fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<KBComment> orderByComparator) {

		return getPersistence().fetchByG_S_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Removes all the kb comments where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	public static void removeByG_S(long groupId, int status) {
		getPersistence().removeByG_S(groupId, status);
	}

	/**
	 * Returns the number of kb comments where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb comments
	 */
	public static int countByG_S(long groupId, int status) {
		return getPersistence().countByG_S(groupId, status);
	}

	/**
	 * Returns an ordered range of all the kb comments where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	public static List<KBComment> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<KBComment> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C(
			classNameId, classPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb comment in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	public static KBComment findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<KBComment> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchCommentException {

		return getPersistence().findByC_C_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the first kb comment in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	public static KBComment fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<KBComment> orderByComparator) {

		return getPersistence().fetchByC_C_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Removes all the kb comments where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByC_C(long classNameId, long classPK) {
		getPersistence().removeByC_C(classNameId, classPK);
	}

	/**
	 * Returns the number of kb comments where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching kb comments
	 */
	public static int countByC_C(long classNameId, long classPK) {
		return getPersistence().countByC_C(classNameId, classPK);
	}

	/**
	 * Returns an ordered range of all the kb comments where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	public static List<KBComment> findByU_C_C(
		long userId, long classNameId, long classPK, int start, int end,
		OrderByComparator<KBComment> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByU_C_C(
			userId, classNameId, classPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb comment in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	public static KBComment findByU_C_C_First(
			long userId, long classNameId, long classPK,
			OrderByComparator<KBComment> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchCommentException {

		return getPersistence().findByU_C_C_First(
			userId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the first kb comment in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	public static KBComment fetchByU_C_C_First(
		long userId, long classNameId, long classPK,
		OrderByComparator<KBComment> orderByComparator) {

		return getPersistence().fetchByU_C_C_First(
			userId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Removes all the kb comments where userId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByU_C_C(
		long userId, long classNameId, long classPK) {

		getPersistence().removeByU_C_C(userId, classNameId, classPK);
	}

	/**
	 * Returns the number of kb comments where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching kb comments
	 */
	public static int countByU_C_C(
		long userId, long classNameId, long classPK) {

		return getPersistence().countByU_C_C(userId, classNameId, classPK);
	}

	/**
	 * Returns an ordered range of all the kb comments where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	public static List<KBComment> findByC_C_S(
		long classNameId, long classPK, int status, int start, int end,
		OrderByComparator<KBComment> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C_S(
			classNameId, classPK, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb comment in the ordered set where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	public static KBComment findByC_C_S_First(
			long classNameId, long classPK, int status,
			OrderByComparator<KBComment> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchCommentException {

		return getPersistence().findByC_C_S_First(
			classNameId, classPK, status, orderByComparator);
	}

	/**
	 * Returns the first kb comment in the ordered set where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	public static KBComment fetchByC_C_S_First(
		long classNameId, long classPK, int status,
		OrderByComparator<KBComment> orderByComparator) {

		return getPersistence().fetchByC_C_S_First(
			classNameId, classPK, status, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb comments where classNameId = &#63; and classPK = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	public static List<KBComment> findByC_C_S(
		long classNameId, long classPK, int[] statuses, int start, int end,
		OrderByComparator<KBComment> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C_S(
			classNameId, classPK, statuses, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the kb comments where classNameId = &#63; and classPK = &#63; and status = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 */
	public static void removeByC_C_S(
		long classNameId, long classPK, int status) {

		getPersistence().removeByC_C_S(classNameId, classPK, status);
	}

	/**
	 * Returns the number of kb comments where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @return the number of matching kb comments
	 */
	public static int countByC_C_S(long classNameId, long classPK, int status) {
		return getPersistence().countByC_C_S(classNameId, classPK, status);
	}

	/**
	 * Returns the number of kb comments where classNameId = &#63; and classPK = &#63; and status = any &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param statuses the statuses
	 * @return the number of matching kb comments
	 */
	public static int countByC_C_S(
		long classNameId, long classPK, int[] statuses) {

		return getPersistence().countByC_C_S(classNameId, classPK, statuses);
	}

	/**
	 * Creates a new kb comment with the primary key. Does not add the kb comment to the database.
	 *
	 * @param kbCommentId the primary key for the new kb comment
	 * @return the new kb comment
	 */
	public static KBComment create(long kbCommentId) {
		return getPersistence().create(kbCommentId);
	}

	/**
	 * Removes the kb comment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kbCommentId the primary key of the kb comment
	 * @return the kb comment that was removed
	 * @throws NoSuchCommentException if a kb comment with the primary key could not be found
	 */
	public static KBComment remove(long kbCommentId)
		throws com.liferay.knowledge.base.exception.NoSuchCommentException {

		return getPersistence().remove(kbCommentId);
	}

	public static KBComment updateImpl(KBComment kbComment) {
		return getPersistence().updateImpl(kbComment);
	}

	/**
	 * Returns the kb comment with the primary key or throws a <code>NoSuchCommentException</code> if it could not be found.
	 *
	 * @param kbCommentId the primary key of the kb comment
	 * @return the kb comment
	 * @throws NoSuchCommentException if a kb comment with the primary key could not be found
	 */
	public static KBComment findByPrimaryKey(long kbCommentId)
		throws com.liferay.knowledge.base.exception.NoSuchCommentException {

		return getPersistence().findByPrimaryKey(kbCommentId);
	}

	/**
	 * Returns the kb comment with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kbCommentId the primary key of the kb comment
	 * @return the kb comment, or <code>null</code> if a kb comment with the primary key could not be found
	 */
	public static KBComment fetchByPrimaryKey(long kbCommentId) {
		return getPersistence().fetchByPrimaryKey(kbCommentId);
	}

	/**
	 * Returns the kb comment where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	public static KBComment fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns all the kb comments where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching kb comments
	 */
	public static List<KBComment> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the kb comments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @return the range of matching kb comments
	 */
	public static List<KBComment> findByUuid(String uuid, int start, int end) {
		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the kb comments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb comments
	 */
	public static List<KBComment> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<KBComment> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the kb comments where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching kb comments
	 */
	public static List<KBComment> findByUuid_C(String uuid, long companyId) {
		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the kb comments where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @return the range of matching kb comments
	 */
	public static List<KBComment> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the kb comments where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb comments
	 */
	public static List<KBComment> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KBComment> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the kb comments where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching kb comments
	 */
	public static List<KBComment> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the kb comments where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @return the range of matching kb comments
	 */
	public static List<KBComment> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the kb comments where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb comments
	 */
	public static List<KBComment> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<KBComment> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the kb comments where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb comments
	 */
	public static List<KBComment> findByG_S(long groupId, int status) {
		return getPersistence().findByG_S(groupId, status);
	}

	/**
	 * Returns a range of all the kb comments where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @return the range of matching kb comments
	 */
	public static List<KBComment> findByG_S(
		long groupId, int status, int start, int end) {

		return getPersistence().findByG_S(groupId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb comments where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb comments
	 */
	public static List<KBComment> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<KBComment> orderByComparator) {

		return getPersistence().findByG_S(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the kb comments where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching kb comments
	 */
	public static List<KBComment> findByC_C(long classNameId, long classPK) {
		return getPersistence().findByC_C(classNameId, classPK);
	}

	/**
	 * Returns a range of all the kb comments where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @return the range of matching kb comments
	 */
	public static List<KBComment> findByC_C(
		long classNameId, long classPK, int start, int end) {

		return getPersistence().findByC_C(classNameId, classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the kb comments where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb comments
	 */
	public static List<KBComment> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<KBComment> orderByComparator) {

		return getPersistence().findByC_C(
			classNameId, classPK, start, end, orderByComparator);
	}

	/**
	 * Returns all the kb comments where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching kb comments
	 */
	public static List<KBComment> findByU_C_C(
		long userId, long classNameId, long classPK) {

		return getPersistence().findByU_C_C(userId, classNameId, classPK);
	}

	/**
	 * Returns a range of all the kb comments where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @return the range of matching kb comments
	 */
	public static List<KBComment> findByU_C_C(
		long userId, long classNameId, long classPK, int start, int end) {

		return getPersistence().findByU_C_C(
			userId, classNameId, classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the kb comments where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb comments
	 */
	public static List<KBComment> findByU_C_C(
		long userId, long classNameId, long classPK, int start, int end,
		OrderByComparator<KBComment> orderByComparator) {

		return getPersistence().findByU_C_C(
			userId, classNameId, classPK, start, end, orderByComparator);
	}

	/**
	 * Returns all the kb comments where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @return the matching kb comments
	 */
	public static List<KBComment> findByC_C_S(
		long classNameId, long classPK, int status) {

		return getPersistence().findByC_C_S(classNameId, classPK, status);
	}

	/**
	 * Returns a range of all the kb comments where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @return the range of matching kb comments
	 */
	public static List<KBComment> findByC_C_S(
		long classNameId, long classPK, int status, int start, int end) {

		return getPersistence().findByC_C_S(
			classNameId, classPK, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb comments where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb comments
	 */
	public static List<KBComment> findByC_C_S(
		long classNameId, long classPK, int status, int start, int end,
		OrderByComparator<KBComment> orderByComparator) {

		return getPersistence().findByC_C_S(
			classNameId, classPK, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the kb comments where classNameId = &#63; and classPK = &#63; and status = any &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param statuses the statuses
	 * @return the matching kb comments
	 */
	public static List<KBComment> findByC_C_S(
		long classNameId, long classPK, int[] statuses) {

		return getPersistence().findByC_C_S(classNameId, classPK, statuses);
	}

	/**
	 * Returns a range of all the kb comments where classNameId = &#63; and classPK = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @return the range of matching kb comments
	 */
	public static List<KBComment> findByC_C_S(
		long classNameId, long classPK, int[] statuses, int start, int end) {

		return getPersistence().findByC_C_S(
			classNameId, classPK, statuses, start, end);
	}

	/**
	 * Returns an ordered range of all the kb comments where classNameId = &#63; and classPK = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb comments
	 */
	public static List<KBComment> findByC_C_S(
		long classNameId, long classPK, int[] statuses, int start, int end,
		OrderByComparator<KBComment> orderByComparator) {

		return getPersistence().findByC_C_S(
			classNameId, classPK, statuses, start, end, orderByComparator);
	}

	public static KBCommentPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(KBCommentPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile KBCommentPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-859110411