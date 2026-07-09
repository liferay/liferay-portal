/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence;

import com.liferay.commerce.exception.NoSuchAvailabilityEstimateException;
import com.liferay.commerce.model.CommerceAvailabilityEstimate;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the commerce availability estimate service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CommerceAvailabilityEstimateUtil
 * @generated
 */
@ProviderType
public interface CommerceAvailabilityEstimatePersistence
	extends BasePersistence<CommerceAvailabilityEstimate> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CommerceAvailabilityEstimateUtil} to access the commerce availability estimate persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the commerce availability estimates where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce availability estimates
	 */
	public java.util.List<CommerceAvailabilityEstimate> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceAvailabilityEstimate> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce availability estimate in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce availability estimate
	 * @throws NoSuchAvailabilityEstimateException if a matching commerce availability estimate could not be found
	 */
	public CommerceAvailabilityEstimate findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceAvailabilityEstimate> orderByComparator)
		throws NoSuchAvailabilityEstimateException;

	/**
	 * Returns the first commerce availability estimate in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce availability estimate, or <code>null</code> if a matching commerce availability estimate could not be found
	 */
	public CommerceAvailabilityEstimate fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceAvailabilityEstimate> orderByComparator);

	/**
	 * Returns an ordered range of all the commerce availability estimates that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce availability estimates that the user has permission to view
	 */
	public java.util.List<CommerceAvailabilityEstimate> filterFindByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceAvailabilityEstimate> orderByComparator);

	/**
	 * Removes all the commerce availability estimates where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of commerce availability estimates where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce availability estimates
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the number of commerce availability estimates that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce availability estimates that the user has permission to view
	 */
	public int filterCountByUuid(String uuid);

	/**
	 * Returns an ordered range of all the commerce availability estimates where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce availability estimates
	 */
	public java.util.List<CommerceAvailabilityEstimate> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceAvailabilityEstimate> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce availability estimate in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce availability estimate
	 * @throws NoSuchAvailabilityEstimateException if a matching commerce availability estimate could not be found
	 */
	public CommerceAvailabilityEstimate findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceAvailabilityEstimate> orderByComparator)
		throws NoSuchAvailabilityEstimateException;

	/**
	 * Returns the first commerce availability estimate in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce availability estimate, or <code>null</code> if a matching commerce availability estimate could not be found
	 */
	public CommerceAvailabilityEstimate fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceAvailabilityEstimate> orderByComparator);

	/**
	 * Returns an ordered range of all the commerce availability estimates that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce availability estimates that the user has permission to view
	 */
	public java.util.List<CommerceAvailabilityEstimate> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceAvailabilityEstimate> orderByComparator);

	/**
	 * Removes all the commerce availability estimates where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of commerce availability estimates where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce availability estimates
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of commerce availability estimates that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce availability estimates that the user has permission to view
	 */
	public int filterCountByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the commerce availability estimates where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce availability estimates
	 */
	public java.util.List<CommerceAvailabilityEstimate> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceAvailabilityEstimate> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce availability estimate in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce availability estimate
	 * @throws NoSuchAvailabilityEstimateException if a matching commerce availability estimate could not be found
	 */
	public CommerceAvailabilityEstimate findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceAvailabilityEstimate> orderByComparator)
		throws NoSuchAvailabilityEstimateException;

	/**
	 * Returns the first commerce availability estimate in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce availability estimate, or <code>null</code> if a matching commerce availability estimate could not be found
	 */
	public CommerceAvailabilityEstimate fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceAvailabilityEstimate> orderByComparator);

	/**
	 * Returns an ordered range of all the commerce availability estimates that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce availability estimates that the user has permission to view
	 */
	public java.util.List<CommerceAvailabilityEstimate> filterFindByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceAvailabilityEstimate> orderByComparator);

	/**
	 * Removes all the commerce availability estimates where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of commerce availability estimates where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce availability estimates
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns the number of commerce availability estimates that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce availability estimates that the user has permission to view
	 */
	public int filterCountByCompanyId(long companyId);

	/**
	 * Returns the commerce availability estimate where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchAvailabilityEstimateException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce availability estimate
	 * @throws NoSuchAvailabilityEstimateException if a matching commerce availability estimate could not be found
	 */
	public CommerceAvailabilityEstimate findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchAvailabilityEstimateException;

	/**
	 * Returns the commerce availability estimate where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce availability estimate, or <code>null</code> if a matching commerce availability estimate could not be found
	 */
	public CommerceAvailabilityEstimate fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the commerce availability estimate where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce availability estimate that was removed
	 */
	public CommerceAvailabilityEstimate removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchAvailabilityEstimateException;

	/**
	 * Returns the number of commerce availability estimates where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce availability estimates
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Creates a new commerce availability estimate with the primary key. Does not add the commerce availability estimate to the database.
	 *
	 * @param commerceAvailabilityEstimateId the primary key for the new commerce availability estimate
	 * @return the new commerce availability estimate
	 */
	public CommerceAvailabilityEstimate create(
		long commerceAvailabilityEstimateId);

	/**
	 * Removes the commerce availability estimate with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceAvailabilityEstimateId the primary key of the commerce availability estimate
	 * @return the commerce availability estimate that was removed
	 * @throws NoSuchAvailabilityEstimateException if a commerce availability estimate with the primary key could not be found
	 */
	public CommerceAvailabilityEstimate remove(
			long commerceAvailabilityEstimateId)
		throws NoSuchAvailabilityEstimateException;

	public CommerceAvailabilityEstimate updateImpl(
		CommerceAvailabilityEstimate commerceAvailabilityEstimate);

	/**
	 * Returns the commerce availability estimate with the primary key or throws a <code>NoSuchAvailabilityEstimateException</code> if it could not be found.
	 *
	 * @param commerceAvailabilityEstimateId the primary key of the commerce availability estimate
	 * @return the commerce availability estimate
	 * @throws NoSuchAvailabilityEstimateException if a commerce availability estimate with the primary key could not be found
	 */
	public CommerceAvailabilityEstimate findByPrimaryKey(
			long commerceAvailabilityEstimateId)
		throws NoSuchAvailabilityEstimateException;

	/**
	 * Returns the commerce availability estimate with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceAvailabilityEstimateId the primary key of the commerce availability estimate
	 * @return the commerce availability estimate, or <code>null</code> if a commerce availability estimate with the primary key could not be found
	 */
	public CommerceAvailabilityEstimate fetchByPrimaryKey(
		long commerceAvailabilityEstimateId);

	/**
	 * Returns the commerce availability estimate where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce availability estimate, or <code>null</code> if a matching commerce availability estimate could not be found
	 */
	public default CommerceAvailabilityEstimate fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns all the commerce availability estimates where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce availability estimates
	 */
	public default java.util.List<CommerceAvailabilityEstimate> findByUuid(
		String uuid) {

		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce availability estimates where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @return the range of matching commerce availability estimates
	 */
	public default java.util.List<CommerceAvailabilityEstimate> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce availability estimates where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce availability estimates
	 */
	public default java.util.List<CommerceAvailabilityEstimate> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceAvailabilityEstimate> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce availability estimates that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce availability estimates that the user has permission to view
	 */
	public default java.util.List<CommerceAvailabilityEstimate>
		filterFindByUuid(String uuid) {

		return filterFindByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce availability estimates that the user has permission to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @return the range of matching commerce availability estimates that the user has permission to view
	 */
	public default java.util.List<CommerceAvailabilityEstimate>
		filterFindByUuid(String uuid, int start, int end) {

		return filterFindByUuid(uuid, start, end, null);
	}

	/**
	 * Returns all the commerce availability estimates where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce availability estimates
	 */
	public default java.util.List<CommerceAvailabilityEstimate> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce availability estimates where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @return the range of matching commerce availability estimates
	 */
	public default java.util.List<CommerceAvailabilityEstimate> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce availability estimates where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce availability estimates
	 */
	public default java.util.List<CommerceAvailabilityEstimate> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceAvailabilityEstimate> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce availability estimates that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce availability estimates that the user has permission to view
	 */
	public default java.util.List<CommerceAvailabilityEstimate>
		filterFindByUuid_C(String uuid, long companyId) {

		return filterFindByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce availability estimates that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @return the range of matching commerce availability estimates that the user has permission to view
	 */
	public default java.util.List<CommerceAvailabilityEstimate>
		filterFindByUuid_C(String uuid, long companyId, int start, int end) {

		return filterFindByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns all the commerce availability estimates where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching commerce availability estimates
	 */
	public default java.util.List<CommerceAvailabilityEstimate> findByCompanyId(
		long companyId) {

		return findByCompanyId(
			companyId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce availability estimates where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @return the range of matching commerce availability estimates
	 */
	public default java.util.List<CommerceAvailabilityEstimate> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce availability estimates where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce availability estimates
	 */
	public default java.util.List<CommerceAvailabilityEstimate> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceAvailabilityEstimate> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce availability estimates that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching commerce availability estimates that the user has permission to view
	 */
	public default java.util.List<CommerceAvailabilityEstimate>
		filterFindByCompanyId(long companyId) {

		return filterFindByCompanyId(
			companyId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce availability estimates that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @return the range of matching commerce availability estimates that the user has permission to view
	 */
	public default java.util.List<CommerceAvailabilityEstimate>
		filterFindByCompanyId(long companyId, int start, int end) {

		return filterFindByCompanyId(companyId, start, end, null);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1479959817