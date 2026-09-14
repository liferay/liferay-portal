/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service.persistence;

import com.liferay.audiences.model.AudiencesEntryGroupRel;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the audiences entry group rel service. This utility wraps <code>com.liferay.audiences.service.persistence.impl.AudiencesEntryGroupRelPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AudiencesEntryGroupRelPersistence
 * @generated
 */
public class AudiencesEntryGroupRelUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<AudiencesEntryGroupRel> audiencesEntryGroupRels) {

		getPersistence().cacheResult(audiencesEntryGroupRels);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		AudiencesEntryGroupRel audiencesEntryGroupRel) {

		getPersistence().cacheResult(audiencesEntryGroupRel);
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
	public static void clearCache(
		AudiencesEntryGroupRel audiencesEntryGroupRel) {

		getPersistence().clearCache(audiencesEntryGroupRel);
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
	public static Map<Serializable, AudiencesEntryGroupRel> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<AudiencesEntryGroupRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<AudiencesEntryGroupRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<AudiencesEntryGroupRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<AudiencesEntryGroupRel> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static AudiencesEntryGroupRel update(
		AudiencesEntryGroupRel audiencesEntryGroupRel) {

		return getPersistence().update(audiencesEntryGroupRel);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static AudiencesEntryGroupRel update(
		AudiencesEntryGroupRel audiencesEntryGroupRel,
		ServiceContext serviceContext) {

		return getPersistence().update(audiencesEntryGroupRel, serviceContext);
	}

	/**
	 * Returns an ordered range of all the audiences entry group rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of audiences entry group rels
	 * @param end the upper bound of the range of audiences entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching audiences entry group rels
	 */
	public static List<AudiencesEntryGroupRel> findByC_AEERC(
		long companyId, String audienceEntryERC, int start, int end,
		OrderByComparator<AudiencesEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_AEERC(
			companyId, audienceEntryERC, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first audiences entry group rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audiences entry group rel
	 * @throws NoSuchAudiencesEntryGroupRelException if a matching audiences entry group rel could not be found
	 */
	public static AudiencesEntryGroupRel findByC_AEERC_First(
			long companyId, String audienceEntryERC,
			OrderByComparator<AudiencesEntryGroupRel> orderByComparator)
		throws com.liferay.audiences.exception.
			NoSuchAudiencesEntryGroupRelException {

		return getPersistence().findByC_AEERC_First(
			companyId, audienceEntryERC, orderByComparator);
	}

	/**
	 * Returns the first audiences entry group rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audiences entry group rel, or <code>null</code> if a matching audiences entry group rel could not be found
	 */
	public static AudiencesEntryGroupRel fetchByC_AEERC_First(
		long companyId, String audienceEntryERC,
		OrderByComparator<AudiencesEntryGroupRel> orderByComparator) {

		return getPersistence().fetchByC_AEERC_First(
			companyId, audienceEntryERC, orderByComparator);
	}

	/**
	 * Removes all the audiences entry group rels where companyId = &#63; and audienceEntryERC = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 */
	public static void removeByC_AEERC(
		long companyId, String audienceEntryERC) {

		getPersistence().removeByC_AEERC(companyId, audienceEntryERC);
	}

	/**
	 * Returns the number of audiences entry group rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the number of matching audiences entry group rels
	 */
	public static int countByC_AEERC(long companyId, String audienceEntryERC) {
		return getPersistence().countByC_AEERC(companyId, audienceEntryERC);
	}

	/**
	 * Returns an ordered range of all the audiences entry group rels where companyId = &#63; and groupERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 * @param start the lower bound of the range of audiences entry group rels
	 * @param end the upper bound of the range of audiences entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching audiences entry group rels
	 */
	public static List<AudiencesEntryGroupRel> findByC_GERC(
		long companyId, String groupERC, int start, int end,
		OrderByComparator<AudiencesEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_GERC(
			companyId, groupERC, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first audiences entry group rel in the ordered set where companyId = &#63; and groupERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audiences entry group rel
	 * @throws NoSuchAudiencesEntryGroupRelException if a matching audiences entry group rel could not be found
	 */
	public static AudiencesEntryGroupRel findByC_GERC_First(
			long companyId, String groupERC,
			OrderByComparator<AudiencesEntryGroupRel> orderByComparator)
		throws com.liferay.audiences.exception.
			NoSuchAudiencesEntryGroupRelException {

		return getPersistence().findByC_GERC_First(
			companyId, groupERC, orderByComparator);
	}

	/**
	 * Returns the first audiences entry group rel in the ordered set where companyId = &#63; and groupERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audiences entry group rel, or <code>null</code> if a matching audiences entry group rel could not be found
	 */
	public static AudiencesEntryGroupRel fetchByC_GERC_First(
		long companyId, String groupERC,
		OrderByComparator<AudiencesEntryGroupRel> orderByComparator) {

		return getPersistence().fetchByC_GERC_First(
			companyId, groupERC, orderByComparator);
	}

	/**
	 * Removes all the audiences entry group rels where companyId = &#63; and groupERC = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 */
	public static void removeByC_GERC(long companyId, String groupERC) {
		getPersistence().removeByC_GERC(companyId, groupERC);
	}

	/**
	 * Returns the number of audiences entry group rels where companyId = &#63; and groupERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 * @return the number of matching audiences entry group rels
	 */
	public static int countByC_GERC(long companyId, String groupERC) {
		return getPersistence().countByC_GERC(companyId, groupERC);
	}

	/**
	 * Returns the audiences entry group rel where companyId = &#63; and audienceEntryERC = &#63; and groupERC = &#63; or throws a <code>NoSuchAudiencesEntryGroupRelException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param groupERC the group erc
	 * @return the matching audiences entry group rel
	 * @throws NoSuchAudiencesEntryGroupRelException if a matching audiences entry group rel could not be found
	 */
	public static AudiencesEntryGroupRel findByC_AEERC_GERC(
			long companyId, String audienceEntryERC, String groupERC)
		throws com.liferay.audiences.exception.
			NoSuchAudiencesEntryGroupRelException {

		return getPersistence().findByC_AEERC_GERC(
			companyId, audienceEntryERC, groupERC);
	}

	/**
	 * Returns the audiences entry group rel where companyId = &#63; and audienceEntryERC = &#63; and groupERC = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param groupERC the group erc
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching audiences entry group rel, or <code>null</code> if a matching audiences entry group rel could not be found
	 */
	public static AudiencesEntryGroupRel fetchByC_AEERC_GERC(
		long companyId, String audienceEntryERC, String groupERC,
		boolean useFinderCache) {

		return getPersistence().fetchByC_AEERC_GERC(
			companyId, audienceEntryERC, groupERC, useFinderCache);
	}

	/**
	 * Removes the audiences entry group rel where companyId = &#63; and audienceEntryERC = &#63; and groupERC = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param groupERC the group erc
	 * @return the audiences entry group rel that was removed
	 */
	public static AudiencesEntryGroupRel removeByC_AEERC_GERC(
			long companyId, String audienceEntryERC, String groupERC)
		throws com.liferay.audiences.exception.
			NoSuchAudiencesEntryGroupRelException {

		return getPersistence().removeByC_AEERC_GERC(
			companyId, audienceEntryERC, groupERC);
	}

	/**
	 * Returns the number of audiences entry group rels where companyId = &#63; and audienceEntryERC = &#63; and groupERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param groupERC the group erc
	 * @return the number of matching audiences entry group rels
	 */
	public static int countByC_AEERC_GERC(
		long companyId, String audienceEntryERC, String groupERC) {

		return getPersistence().countByC_AEERC_GERC(
			companyId, audienceEntryERC, groupERC);
	}

	/**
	 * Creates a new audiences entry group rel with the primary key. Does not add the audiences entry group rel to the database.
	 *
	 * @param audiencesEntryGroupRelId the primary key for the new audiences entry group rel
	 * @return the new audiences entry group rel
	 */
	public static AudiencesEntryGroupRel create(long audiencesEntryGroupRelId) {
		return getPersistence().create(audiencesEntryGroupRelId);
	}

	/**
	 * Removes the audiences entry group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param audiencesEntryGroupRelId the primary key of the audiences entry group rel
	 * @return the audiences entry group rel that was removed
	 * @throws NoSuchAudiencesEntryGroupRelException if a audiences entry group rel with the primary key could not be found
	 */
	public static AudiencesEntryGroupRel remove(long audiencesEntryGroupRelId)
		throws com.liferay.audiences.exception.
			NoSuchAudiencesEntryGroupRelException {

		return getPersistence().remove(audiencesEntryGroupRelId);
	}

	public static AudiencesEntryGroupRel updateImpl(
		AudiencesEntryGroupRel audiencesEntryGroupRel) {

		return getPersistence().updateImpl(audiencesEntryGroupRel);
	}

	/**
	 * Returns the audiences entry group rel with the primary key or throws a <code>NoSuchAudiencesEntryGroupRelException</code> if it could not be found.
	 *
	 * @param audiencesEntryGroupRelId the primary key of the audiences entry group rel
	 * @return the audiences entry group rel
	 * @throws NoSuchAudiencesEntryGroupRelException if a audiences entry group rel with the primary key could not be found
	 */
	public static AudiencesEntryGroupRel findByPrimaryKey(
			long audiencesEntryGroupRelId)
		throws com.liferay.audiences.exception.
			NoSuchAudiencesEntryGroupRelException {

		return getPersistence().findByPrimaryKey(audiencesEntryGroupRelId);
	}

	/**
	 * Returns the audiences entry group rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param audiencesEntryGroupRelId the primary key of the audiences entry group rel
	 * @return the audiences entry group rel, or <code>null</code> if a audiences entry group rel with the primary key could not be found
	 */
	public static AudiencesEntryGroupRel fetchByPrimaryKey(
		long audiencesEntryGroupRelId) {

		return getPersistence().fetchByPrimaryKey(audiencesEntryGroupRelId);
	}

	/**
	 * Returns the audiences entry group rel where companyId = &#63; and audienceEntryERC = &#63; and groupERC = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param groupERC the group erc
	 * @return the matching audiences entry group rel, or <code>null</code> if a matching audiences entry group rel could not be found
	 */
	public static AudiencesEntryGroupRel fetchByC_AEERC_GERC(
		long companyId, String audienceEntryERC, String groupERC) {

		return getPersistence().fetchByC_AEERC_GERC(
			companyId, audienceEntryERC, groupERC);
	}

	/**
	 * Returns all the audiences entry group rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the matching audiences entry group rels
	 */
	public static List<AudiencesEntryGroupRel> findByC_AEERC(
		long companyId, String audienceEntryERC) {

		return getPersistence().findByC_AEERC(companyId, audienceEntryERC);
	}

	/**
	 * Returns a range of all the audiences entry group rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of audiences entry group rels
	 * @param end the upper bound of the range of audiences entry group rels (not inclusive)
	 * @return the range of matching audiences entry group rels
	 */
	public static List<AudiencesEntryGroupRel> findByC_AEERC(
		long companyId, String audienceEntryERC, int start, int end) {

		return getPersistence().findByC_AEERC(
			companyId, audienceEntryERC, start, end);
	}

	/**
	 * Returns an ordered range of all the audiences entry group rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of audiences entry group rels
	 * @param end the upper bound of the range of audiences entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching audiences entry group rels
	 */
	public static List<AudiencesEntryGroupRel> findByC_AEERC(
		long companyId, String audienceEntryERC, int start, int end,
		OrderByComparator<AudiencesEntryGroupRel> orderByComparator) {

		return getPersistence().findByC_AEERC(
			companyId, audienceEntryERC, start, end, orderByComparator);
	}

	/**
	 * Returns all the audiences entry group rels where companyId = &#63; and groupERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 * @return the matching audiences entry group rels
	 */
	public static List<AudiencesEntryGroupRel> findByC_GERC(
		long companyId, String groupERC) {

		return getPersistence().findByC_GERC(companyId, groupERC);
	}

	/**
	 * Returns a range of all the audiences entry group rels where companyId = &#63; and groupERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 * @param start the lower bound of the range of audiences entry group rels
	 * @param end the upper bound of the range of audiences entry group rels (not inclusive)
	 * @return the range of matching audiences entry group rels
	 */
	public static List<AudiencesEntryGroupRel> findByC_GERC(
		long companyId, String groupERC, int start, int end) {

		return getPersistence().findByC_GERC(companyId, groupERC, start, end);
	}

	/**
	 * Returns an ordered range of all the audiences entry group rels where companyId = &#63; and groupERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 * @param start the lower bound of the range of audiences entry group rels
	 * @param end the upper bound of the range of audiences entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching audiences entry group rels
	 */
	public static List<AudiencesEntryGroupRel> findByC_GERC(
		long companyId, String groupERC, int start, int end,
		OrderByComparator<AudiencesEntryGroupRel> orderByComparator) {

		return getPersistence().findByC_GERC(
			companyId, groupERC, start, end, orderByComparator);
	}

	public static AudiencesEntryGroupRelPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		AudiencesEntryGroupRelPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile AudiencesEntryGroupRelPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2002671703