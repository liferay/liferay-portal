/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.template.model.TemplateEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the template entry service. This utility wraps <code>com.liferay.template.service.persistence.impl.TemplateEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see TemplateEntryPersistence
 * @generated
 */
public class TemplateEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<TemplateEntry> templateEntries) {
		getPersistence().cacheResult(templateEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(TemplateEntry templateEntry) {
		getPersistence().cacheResult(templateEntry);
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
	public static void clearCache(TemplateEntry templateEntry) {
		getPersistence().clearCache(templateEntry);
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
	public static Map<Serializable, TemplateEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<TemplateEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<TemplateEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<TemplateEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static TemplateEntry update(TemplateEntry templateEntry) {
		return getPersistence().update(templateEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static TemplateEntry update(
		TemplateEntry templateEntry, ServiceContext serviceContext) {

		return getPersistence().update(templateEntry, serviceContext);
	}

	/**
	 * Returns an ordered range of all the template entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching template entries
	 */
	public static List<TemplateEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first template entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching template entry
	 * @throws NoSuchTemplateEntryException if a matching template entry could not be found
	 */
	public static TemplateEntry findByUuid_First(
			String uuid, OrderByComparator<TemplateEntry> orderByComparator)
		throws com.liferay.template.exception.NoSuchTemplateEntryException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first template entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	public static TemplateEntry fetchByUuid_First(
		String uuid, OrderByComparator<TemplateEntry> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the template entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of template entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching template entries
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the template entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchTemplateEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching template entry
	 * @throws NoSuchTemplateEntryException if a matching template entry could not be found
	 */
	public static TemplateEntry findByUUID_G(String uuid, long groupId)
		throws com.liferay.template.exception.NoSuchTemplateEntryException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the template entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	public static TemplateEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the template entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the template entry that was removed
	 */
	public static TemplateEntry removeByUUID_G(String uuid, long groupId)
		throws com.liferay.template.exception.NoSuchTemplateEntryException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of template entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching template entries
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching template entries
	 */
	public static List<TemplateEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first template entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching template entry
	 * @throws NoSuchTemplateEntryException if a matching template entry could not be found
	 */
	public static TemplateEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<TemplateEntry> orderByComparator)
		throws com.liferay.template.exception.NoSuchTemplateEntryException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first template entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	public static TemplateEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<TemplateEntry> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the template entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching template entries
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the template entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching template entries
	 */
	public static List<TemplateEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first template entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching template entry
	 * @throws NoSuchTemplateEntryException if a matching template entry could not be found
	 */
	public static TemplateEntry findByGroupId_First(
			long groupId, OrderByComparator<TemplateEntry> orderByComparator)
		throws com.liferay.template.exception.NoSuchTemplateEntryException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first template entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	public static TemplateEntry fetchByGroupId_First(
		long groupId, OrderByComparator<TemplateEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the template entries where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching template entries
	 */
	public static List<TemplateEntry> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupIds, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the template entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of template entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching template entries
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of template entries where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching template entries
	 */
	public static int countByGroupId(long[] groupIds) {
		return getPersistence().countByGroupId(groupIds);
	}

	/**
	 * Returns the template entry where ddmTemplateId = &#63; or throws a <code>NoSuchTemplateEntryException</code> if it could not be found.
	 *
	 * @param ddmTemplateId the ddm template ID
	 * @return the matching template entry
	 * @throws NoSuchTemplateEntryException if a matching template entry could not be found
	 */
	public static TemplateEntry findByDDMTemplateId(long ddmTemplateId)
		throws com.liferay.template.exception.NoSuchTemplateEntryException {

		return getPersistence().findByDDMTemplateId(ddmTemplateId);
	}

	/**
	 * Returns the template entry where ddmTemplateId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param ddmTemplateId the ddm template ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	public static TemplateEntry fetchByDDMTemplateId(
		long ddmTemplateId, boolean useFinderCache) {

		return getPersistence().fetchByDDMTemplateId(
			ddmTemplateId, useFinderCache);
	}

	/**
	 * Removes the template entry where ddmTemplateId = &#63; from the database.
	 *
	 * @param ddmTemplateId the ddm template ID
	 * @return the template entry that was removed
	 */
	public static TemplateEntry removeByDDMTemplateId(long ddmTemplateId)
		throws com.liferay.template.exception.NoSuchTemplateEntryException {

		return getPersistence().removeByDDMTemplateId(ddmTemplateId);
	}

	/**
	 * Returns the number of template entries where ddmTemplateId = &#63;.
	 *
	 * @param ddmTemplateId the ddm template ID
	 * @return the number of matching template entries
	 */
	public static int countByDDMTemplateId(long ddmTemplateId) {
		return getPersistence().countByDDMTemplateId(ddmTemplateId);
	}

	/**
	 * Returns an ordered range of all the template entries where groupId = &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching template entries
	 */
	public static List<TemplateEntry> findByG_IICN_IIFVK(
		long groupId, String infoItemClassName, String infoItemFormVariationKey,
		int start, int end, OrderByComparator<TemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_IICN_IIFVK(
			groupId, infoItemClassName, infoItemFormVariationKey, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first template entry in the ordered set where groupId = &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching template entry
	 * @throws NoSuchTemplateEntryException if a matching template entry could not be found
	 */
	public static TemplateEntry findByG_IICN_IIFVK_First(
			long groupId, String infoItemClassName,
			String infoItemFormVariationKey,
			OrderByComparator<TemplateEntry> orderByComparator)
		throws com.liferay.template.exception.NoSuchTemplateEntryException {

		return getPersistence().findByG_IICN_IIFVK_First(
			groupId, infoItemClassName, infoItemFormVariationKey,
			orderByComparator);
	}

	/**
	 * Returns the first template entry in the ordered set where groupId = &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	public static TemplateEntry fetchByG_IICN_IIFVK_First(
		long groupId, String infoItemClassName, String infoItemFormVariationKey,
		OrderByComparator<TemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_IICN_IIFVK_First(
			groupId, infoItemClassName, infoItemFormVariationKey,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the template entries where groupId = &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching template entries
	 */
	public static List<TemplateEntry> findByG_IICN_IIFVK(
		long[] groupIds, String infoItemClassName,
		String infoItemFormVariationKey, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_IICN_IIFVK(
			groupIds, infoItemClassName, infoItemFormVariationKey, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the template entries where groupId = &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 */
	public static void removeByG_IICN_IIFVK(
		long groupId, String infoItemClassName,
		String infoItemFormVariationKey) {

		getPersistence().removeByG_IICN_IIFVK(
			groupId, infoItemClassName, infoItemFormVariationKey);
	}

	/**
	 * Returns the number of template entries where groupId = &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 * @return the number of matching template entries
	 */
	public static int countByG_IICN_IIFVK(
		long groupId, String infoItemClassName,
		String infoItemFormVariationKey) {

		return getPersistence().countByG_IICN_IIFVK(
			groupId, infoItemClassName, infoItemFormVariationKey);
	}

	/**
	 * Returns the number of template entries where groupId = any &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 * @return the number of matching template entries
	 */
	public static int countByG_IICN_IIFVK(
		long[] groupIds, String infoItemClassName,
		String infoItemFormVariationKey) {

		return getPersistence().countByG_IICN_IIFVK(
			groupIds, infoItemClassName, infoItemFormVariationKey);
	}

	/**
	 * Returns the template entry where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchTemplateEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching template entry
	 * @throws NoSuchTemplateEntryException if a matching template entry could not be found
	 */
	public static TemplateEntry findByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.template.exception.NoSuchTemplateEntryException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the template entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	public static TemplateEntry fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the template entry where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the template entry that was removed
	 */
	public static TemplateEntry removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.template.exception.NoSuchTemplateEntryException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of template entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching template entries
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Creates a new template entry with the primary key. Does not add the template entry to the database.
	 *
	 * @param templateEntryId the primary key for the new template entry
	 * @return the new template entry
	 */
	public static TemplateEntry create(long templateEntryId) {
		return getPersistence().create(templateEntryId);
	}

	/**
	 * Removes the template entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param templateEntryId the primary key of the template entry
	 * @return the template entry that was removed
	 * @throws NoSuchTemplateEntryException if a template entry with the primary key could not be found
	 */
	public static TemplateEntry remove(long templateEntryId)
		throws com.liferay.template.exception.NoSuchTemplateEntryException {

		return getPersistence().remove(templateEntryId);
	}

	public static TemplateEntry updateImpl(TemplateEntry templateEntry) {
		return getPersistence().updateImpl(templateEntry);
	}

	/**
	 * Returns the template entry with the primary key or throws a <code>NoSuchTemplateEntryException</code> if it could not be found.
	 *
	 * @param templateEntryId the primary key of the template entry
	 * @return the template entry
	 * @throws NoSuchTemplateEntryException if a template entry with the primary key could not be found
	 */
	public static TemplateEntry findByPrimaryKey(long templateEntryId)
		throws com.liferay.template.exception.NoSuchTemplateEntryException {

		return getPersistence().findByPrimaryKey(templateEntryId);
	}

	/**
	 * Returns the template entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param templateEntryId the primary key of the template entry
	 * @return the template entry, or <code>null</code> if a template entry with the primary key could not be found
	 */
	public static TemplateEntry fetchByPrimaryKey(long templateEntryId) {
		return getPersistence().fetchByPrimaryKey(templateEntryId);
	}

	/**
	 * Returns the template entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	public static TemplateEntry fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the template entry where ddmTemplateId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param ddmTemplateId the ddm template ID
	 * @return the matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	public static TemplateEntry fetchByDDMTemplateId(long ddmTemplateId) {
		return getPersistence().fetchByDDMTemplateId(ddmTemplateId);
	}

	/**
	 * Returns the template entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	public static TemplateEntry fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns all the template entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching template entries
	 */
	public static List<TemplateEntry> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the template entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @return the range of matching template entries
	 */
	public static List<TemplateEntry> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the template entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching template entries
	 */
	public static List<TemplateEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching template entries
	 */
	public static List<TemplateEntry> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @return the range of matching template entries
	 */
	public static List<TemplateEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching template entries
	 */
	public static List<TemplateEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the template entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching template entries
	 */
	public static List<TemplateEntry> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the template entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @return the range of matching template entries
	 */
	public static List<TemplateEntry> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the template entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching template entries
	 */
	public static List<TemplateEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the template entries where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the matching template entries
	 */
	public static List<TemplateEntry> findByGroupId(long[] groupIds) {
		return getPersistence().findByGroupId(groupIds);
	}

	/**
	 * Returns a range of all the template entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @return the range of matching template entries
	 */
	public static List<TemplateEntry> findByGroupId(
		long[] groupIds, int start, int end) {

		return getPersistence().findByGroupId(groupIds, start, end);
	}

	/**
	 * Returns an ordered range of all the template entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching template entries
	 */
	public static List<TemplateEntry> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator) {

		return getPersistence().findByGroupId(
			groupIds, start, end, orderByComparator);
	}

	/**
	 * Returns all the template entries where groupId = &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 * @return the matching template entries
	 */
	public static List<TemplateEntry> findByG_IICN_IIFVK(
		long groupId, String infoItemClassName,
		String infoItemFormVariationKey) {

		return getPersistence().findByG_IICN_IIFVK(
			groupId, infoItemClassName, infoItemFormVariationKey);
	}

	/**
	 * Returns a range of all the template entries where groupId = &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @return the range of matching template entries
	 */
	public static List<TemplateEntry> findByG_IICN_IIFVK(
		long groupId, String infoItemClassName, String infoItemFormVariationKey,
		int start, int end) {

		return getPersistence().findByG_IICN_IIFVK(
			groupId, infoItemClassName, infoItemFormVariationKey, start, end);
	}

	/**
	 * Returns an ordered range of all the template entries where groupId = &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching template entries
	 */
	public static List<TemplateEntry> findByG_IICN_IIFVK(
		long groupId, String infoItemClassName, String infoItemFormVariationKey,
		int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator) {

		return getPersistence().findByG_IICN_IIFVK(
			groupId, infoItemClassName, infoItemFormVariationKey, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the template entries where groupId = any &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 * @return the matching template entries
	 */
	public static List<TemplateEntry> findByG_IICN_IIFVK(
		long[] groupIds, String infoItemClassName,
		String infoItemFormVariationKey) {

		return getPersistence().findByG_IICN_IIFVK(
			groupIds, infoItemClassName, infoItemFormVariationKey);
	}

	/**
	 * Returns a range of all the template entries where groupId = any &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @return the range of matching template entries
	 */
	public static List<TemplateEntry> findByG_IICN_IIFVK(
		long[] groupIds, String infoItemClassName,
		String infoItemFormVariationKey, int start, int end) {

		return getPersistence().findByG_IICN_IIFVK(
			groupIds, infoItemClassName, infoItemFormVariationKey, start, end);
	}

	/**
	 * Returns an ordered range of all the template entries where groupId = any &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching template entries
	 */
	public static List<TemplateEntry> findByG_IICN_IIFVK(
		long[] groupIds, String infoItemClassName,
		String infoItemFormVariationKey, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator) {

		return getPersistence().findByG_IICN_IIFVK(
			groupIds, infoItemClassName, infoItemFormVariationKey, start, end,
			orderByComparator);
	}

	public static TemplateEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(TemplateEntryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile TemplateEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:2045496912