/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.exception.NoSuchOrganizationException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the organization service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see OrganizationUtil
 * @generated
 */
@ProviderType
public interface OrganizationPersistence
	extends BasePersistence<Organization>, CTPersistence<Organization> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link OrganizationUtil} to access the organization persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the organizations where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching organizations
	 */
	public java.util.List<Organization> findByUuid(String uuid);

	/**
	 * Returns a range of all the organizations where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations
	 */
	public java.util.List<Organization> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the organizations where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns an ordered range of all the organizations where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first organization in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the first organization in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the last organization in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the last organization in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the organizations before and after the current organization in the ordered set where uuid = &#63;.
	 *
	 * @param organizationId the primary key of the current organization
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next organization
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization[] findByUuid_PrevAndNext(
			long organizationId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns all the organizations that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByUuid(String uuid);

	/**
	 * Returns a range of all the organizations that the user has permission to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the organizations that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the organizations before and after the current organization in the ordered set of organizations that the user has permission to view where uuid = &#63;.
	 *
	 * @param organizationId the primary key of the current organization
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next organization
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization[] filterFindByUuid_PrevAndNext(
			long organizationId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Removes all the organizations where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of organizations where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching organizations
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the number of organizations that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching organizations that the user has permission to view
	 */
	public int filterCountByUuid(String uuid);

	/**
	 * Returns all the organizations where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching organizations
	 */
	public java.util.List<Organization> findByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the organizations where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations
	 */
	public java.util.List<Organization> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the organizations where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns an ordered range of all the organizations where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first organization in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the first organization in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the last organization in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the last organization in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the organizations before and after the current organization in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param organizationId the primary key of the current organization
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next organization
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization[] findByUuid_C_PrevAndNext(
			long organizationId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns all the organizations that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the organizations that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the organizations that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the organizations before and after the current organization in the ordered set of organizations that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param organizationId the primary key of the current organization
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next organization
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization[] filterFindByUuid_C_PrevAndNext(
			long organizationId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Removes all the organizations where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of organizations where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching organizations
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of organizations that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching organizations that the user has permission to view
	 */
	public int filterCountByUuid_C(String uuid, long companyId);

	/**
	 * Returns all the organizations where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching organizations
	 */
	public java.util.List<Organization> findByCompanyId(long companyId);

	/**
	 * Returns a range of all the organizations where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations
	 */
	public java.util.List<Organization> findByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the organizations where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns an ordered range of all the organizations where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first organization in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the first organization in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the last organization in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByCompanyId_Last(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the last organization in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the organizations before and after the current organization in the ordered set where companyId = &#63;.
	 *
	 * @param organizationId the primary key of the current organization
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next organization
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization[] findByCompanyId_PrevAndNext(
			long organizationId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns all the organizations that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByCompanyId(long companyId);

	/**
	 * Returns a range of all the organizations that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the organizations that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the organizations before and after the current organization in the ordered set of organizations that the user has permission to view where companyId = &#63;.
	 *
	 * @param organizationId the primary key of the current organization
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next organization
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization[] filterFindByCompanyId_PrevAndNext(
			long organizationId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Removes all the organizations where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of organizations where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching organizations
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns the number of organizations that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching organizations that the user has permission to view
	 */
	public int filterCountByCompanyId(long companyId);

	/**
	 * Returns all the organizations where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching organizations
	 */
	public java.util.List<Organization> findByCompanyIdLocations(
		long companyId);

	/**
	 * Returns a range of all the organizations where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations
	 */
	public java.util.List<Organization> findByCompanyIdLocations(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the organizations where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByCompanyIdLocations(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns an ordered range of all the organizations where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByCompanyIdLocations(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first organization in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByCompanyIdLocations_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the first organization in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByCompanyIdLocations_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the last organization in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByCompanyIdLocations_Last(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the last organization in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByCompanyIdLocations_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the organizations before and after the current organization in the ordered set where companyId = &#63;.
	 *
	 * @param organizationId the primary key of the current organization
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next organization
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization[] findByCompanyIdLocations_PrevAndNext(
			long organizationId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns all the organizations that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByCompanyIdLocations(
		long companyId);

	/**
	 * Returns a range of all the organizations that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByCompanyIdLocations(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the organizations that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByCompanyIdLocations(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the organizations before and after the current organization in the ordered set of organizations that the user has permission to view where companyId = &#63;.
	 *
	 * @param organizationId the primary key of the current organization
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next organization
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization[] filterFindByCompanyIdLocations_PrevAndNext(
			long organizationId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Removes all the organizations where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyIdLocations(long companyId);

	/**
	 * Returns the number of organizations where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching organizations
	 */
	public int countByCompanyIdLocations(long companyId);

	/**
	 * Returns the number of organizations that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching organizations that the user has permission to view
	 */
	public int filterCountByCompanyIdLocations(long companyId);

	/**
	 * Returns all the organizations where logoId = &#63;.
	 *
	 * @param logoId the logo ID
	 * @return the matching organizations
	 */
	public java.util.List<Organization> findByLogoId(long logoId);

	/**
	 * Returns a range of all the organizations where logoId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param logoId the logo ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations
	 */
	public java.util.List<Organization> findByLogoId(
		long logoId, int start, int end);

	/**
	 * Returns an ordered range of all the organizations where logoId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param logoId the logo ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByLogoId(
		long logoId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns an ordered range of all the organizations where logoId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param logoId the logo ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByLogoId(
		long logoId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first organization in the ordered set where logoId = &#63;.
	 *
	 * @param logoId the logo ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByLogoId_First(
			long logoId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the first organization in the ordered set where logoId = &#63;.
	 *
	 * @param logoId the logo ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByLogoId_First(
		long logoId,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the last organization in the ordered set where logoId = &#63;.
	 *
	 * @param logoId the logo ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByLogoId_Last(
			long logoId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the last organization in the ordered set where logoId = &#63;.
	 *
	 * @param logoId the logo ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByLogoId_Last(
		long logoId,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the organizations before and after the current organization in the ordered set where logoId = &#63;.
	 *
	 * @param organizationId the primary key of the current organization
	 * @param logoId the logo ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next organization
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization[] findByLogoId_PrevAndNext(
			long organizationId, long logoId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns all the organizations that the user has permission to view where logoId = &#63;.
	 *
	 * @param logoId the logo ID
	 * @return the matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByLogoId(long logoId);

	/**
	 * Returns a range of all the organizations that the user has permission to view where logoId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param logoId the logo ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByLogoId(
		long logoId, int start, int end);

	/**
	 * Returns an ordered range of all the organizations that the user has permissions to view where logoId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param logoId the logo ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByLogoId(
		long logoId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the organizations before and after the current organization in the ordered set of organizations that the user has permission to view where logoId = &#63;.
	 *
	 * @param organizationId the primary key of the current organization
	 * @param logoId the logo ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next organization
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization[] filterFindByLogoId_PrevAndNext(
			long organizationId, long logoId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Removes all the organizations where logoId = &#63; from the database.
	 *
	 * @param logoId the logo ID
	 */
	public void removeByLogoId(long logoId);

	/**
	 * Returns the number of organizations where logoId = &#63;.
	 *
	 * @param logoId the logo ID
	 * @return the number of matching organizations
	 */
	public int countByLogoId(long logoId);

	/**
	 * Returns the number of organizations that the user has permission to view where logoId = &#63;.
	 *
	 * @param logoId the logo ID
	 * @return the number of matching organizations that the user has permission to view
	 */
	public int filterCountByLogoId(long logoId);

	/**
	 * Returns all the organizations where companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @return the matching organizations
	 */
	public java.util.List<Organization> findByC_P(
		long companyId, long parentOrganizationId);

	/**
	 * Returns a range of all the organizations where companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations
	 */
	public java.util.List<Organization> findByC_P(
		long companyId, long parentOrganizationId, int start, int end);

	/**
	 * Returns an ordered range of all the organizations where companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByC_P(
		long companyId, long parentOrganizationId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns an ordered range of all the organizations where companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByC_P(
		long companyId, long parentOrganizationId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first organization in the ordered set where companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByC_P_First(
			long companyId, long parentOrganizationId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the first organization in the ordered set where companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByC_P_First(
		long companyId, long parentOrganizationId,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the last organization in the ordered set where companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByC_P_Last(
			long companyId, long parentOrganizationId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the last organization in the ordered set where companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByC_P_Last(
		long companyId, long parentOrganizationId,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the organizations before and after the current organization in the ordered set where companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * @param organizationId the primary key of the current organization
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next organization
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization[] findByC_P_PrevAndNext(
			long organizationId, long companyId, long parentOrganizationId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns all the organizations that the user has permission to view where companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @return the matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByC_P(
		long companyId, long parentOrganizationId);

	/**
	 * Returns a range of all the organizations that the user has permission to view where companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByC_P(
		long companyId, long parentOrganizationId, int start, int end);

	/**
	 * Returns an ordered range of all the organizations that the user has permissions to view where companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByC_P(
		long companyId, long parentOrganizationId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the organizations before and after the current organization in the ordered set of organizations that the user has permission to view where companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * @param organizationId the primary key of the current organization
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next organization
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization[] filterFindByC_P_PrevAndNext(
			long organizationId, long companyId, long parentOrganizationId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Removes all the organizations where companyId = &#63; and parentOrganizationId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 */
	public void removeByC_P(long companyId, long parentOrganizationId);

	/**
	 * Returns the number of organizations where companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @return the number of matching organizations
	 */
	public int countByC_P(long companyId, long parentOrganizationId);

	/**
	 * Returns the number of organizations that the user has permission to view where companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @return the number of matching organizations that the user has permission to view
	 */
	public int filterCountByC_P(long companyId, long parentOrganizationId);

	/**
	 * Returns all the organizations where companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @return the matching organizations
	 */
	public java.util.List<Organization> findByC_LikeT(
		long companyId, String treePath);

	/**
	 * Returns a range of all the organizations where companyId = &#63; and treePath LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations
	 */
	public java.util.List<Organization> findByC_LikeT(
		long companyId, String treePath, int start, int end);

	/**
	 * Returns an ordered range of all the organizations where companyId = &#63; and treePath LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByC_LikeT(
		long companyId, String treePath, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns an ordered range of all the organizations where companyId = &#63; and treePath LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByC_LikeT(
		long companyId, String treePath, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first organization in the ordered set where companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByC_LikeT_First(
			long companyId, String treePath,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the first organization in the ordered set where companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByC_LikeT_First(
		long companyId, String treePath,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the last organization in the ordered set where companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByC_LikeT_Last(
			long companyId, String treePath,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the last organization in the ordered set where companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByC_LikeT_Last(
		long companyId, String treePath,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the organizations before and after the current organization in the ordered set where companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param organizationId the primary key of the current organization
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next organization
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization[] findByC_LikeT_PrevAndNext(
			long organizationId, long companyId, String treePath,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns all the organizations that the user has permission to view where companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @return the matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByC_LikeT(
		long companyId, String treePath);

	/**
	 * Returns a range of all the organizations that the user has permission to view where companyId = &#63; and treePath LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByC_LikeT(
		long companyId, String treePath, int start, int end);

	/**
	 * Returns an ordered range of all the organizations that the user has permissions to view where companyId = &#63; and treePath LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByC_LikeT(
		long companyId, String treePath, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the organizations before and after the current organization in the ordered set of organizations that the user has permission to view where companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param organizationId the primary key of the current organization
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next organization
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization[] filterFindByC_LikeT_PrevAndNext(
			long organizationId, long companyId, String treePath,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Removes all the organizations where companyId = &#63; and treePath LIKE &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 */
	public void removeByC_LikeT(long companyId, String treePath);

	/**
	 * Returns the number of organizations where companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @return the number of matching organizations
	 */
	public int countByC_LikeT(long companyId, String treePath);

	/**
	 * Returns the number of organizations that the user has permission to view where companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @return the number of matching organizations that the user has permission to view
	 */
	public int filterCountByC_LikeT(long companyId, String treePath);

	/**
	 * Returns the organization where companyId = &#63; and name = &#63; or throws a <code>NoSuchOrganizationException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByC_N(long companyId, String name)
		throws NoSuchOrganizationException;

	/**
	 * Returns the organization where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByC_N(long companyId, String name);

	/**
	 * Returns the organization where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByC_N(
		long companyId, String name, boolean useFinderCache);

	/**
	 * Removes the organization where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the organization that was removed
	 */
	public Organization removeByC_N(long companyId, String name)
		throws NoSuchOrganizationException;

	/**
	 * Returns the number of organizations where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching organizations
	 */
	public int countByC_N(long companyId, String name);

	/**
	 * Returns all the organizations where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching organizations
	 */
	public java.util.List<Organization> findByC_LikeN(
		long companyId, String name);

	/**
	 * Returns a range of all the organizations where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations
	 */
	public java.util.List<Organization> findByC_LikeN(
		long companyId, String name, int start, int end);

	/**
	 * Returns an ordered range of all the organizations where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByC_LikeN(
		long companyId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns an ordered range of all the organizations where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByC_LikeN(
		long companyId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first organization in the ordered set where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByC_LikeN_First(
			long companyId, String name,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the first organization in the ordered set where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByC_LikeN_First(
		long companyId, String name,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the last organization in the ordered set where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByC_LikeN_Last(
			long companyId, String name,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the last organization in the ordered set where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByC_LikeN_Last(
		long companyId, String name,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the organizations before and after the current organization in the ordered set where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param organizationId the primary key of the current organization
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next organization
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization[] findByC_LikeN_PrevAndNext(
			long organizationId, long companyId, String name,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns all the organizations that the user has permission to view where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByC_LikeN(
		long companyId, String name);

	/**
	 * Returns a range of all the organizations that the user has permission to view where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByC_LikeN(
		long companyId, String name, int start, int end);

	/**
	 * Returns an ordered range of all the organizations that the user has permissions to view where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByC_LikeN(
		long companyId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the organizations before and after the current organization in the ordered set of organizations that the user has permission to view where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param organizationId the primary key of the current organization
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next organization
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization[] filterFindByC_LikeN_PrevAndNext(
			long organizationId, long companyId, String name,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Removes all the organizations where companyId = &#63; and name LIKE &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 */
	public void removeByC_LikeN(long companyId, String name);

	/**
	 * Returns the number of organizations where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching organizations
	 */
	public int countByC_LikeN(long companyId, String name);

	/**
	 * Returns the number of organizations that the user has permission to view where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching organizations that the user has permission to view
	 */
	public int filterCountByC_LikeN(long companyId, String name);

	/**
	 * Returns all the organizations where organizationId &gt; &#63; and companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * @param organizationId the organization ID
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @return the matching organizations
	 */
	public java.util.List<Organization> findByGtO_C_P(
		long organizationId, long companyId, long parentOrganizationId);

	/**
	 * Returns a range of all the organizations where organizationId &gt; &#63; and companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param organizationId the organization ID
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations
	 */
	public java.util.List<Organization> findByGtO_C_P(
		long organizationId, long companyId, long parentOrganizationId,
		int start, int end);

	/**
	 * Returns an ordered range of all the organizations where organizationId &gt; &#63; and companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param organizationId the organization ID
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByGtO_C_P(
		long organizationId, long companyId, long parentOrganizationId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns an ordered range of all the organizations where organizationId &gt; &#63; and companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param organizationId the organization ID
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByGtO_C_P(
		long organizationId, long companyId, long parentOrganizationId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first organization in the ordered set where organizationId &gt; &#63; and companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * @param organizationId the organization ID
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByGtO_C_P_First(
			long organizationId, long companyId, long parentOrganizationId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the first organization in the ordered set where organizationId &gt; &#63; and companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * @param organizationId the organization ID
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByGtO_C_P_First(
		long organizationId, long companyId, long parentOrganizationId,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the last organization in the ordered set where organizationId &gt; &#63; and companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * @param organizationId the organization ID
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByGtO_C_P_Last(
			long organizationId, long companyId, long parentOrganizationId,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the last organization in the ordered set where organizationId &gt; &#63; and companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * @param organizationId the organization ID
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByGtO_C_P_Last(
		long organizationId, long companyId, long parentOrganizationId,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns all the organizations that the user has permission to view where organizationId &gt; &#63; and companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * @param organizationId the organization ID
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @return the matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByGtO_C_P(
		long organizationId, long companyId, long parentOrganizationId);

	/**
	 * Returns a range of all the organizations that the user has permission to view where organizationId &gt; &#63; and companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param organizationId the organization ID
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByGtO_C_P(
		long organizationId, long companyId, long parentOrganizationId,
		int start, int end);

	/**
	 * Returns an ordered range of all the organizations that the user has permissions to view where organizationId &gt; &#63; and companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param organizationId the organization ID
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByGtO_C_P(
		long organizationId, long companyId, long parentOrganizationId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Removes all the organizations where organizationId &gt; &#63; and companyId = &#63; and parentOrganizationId = &#63; from the database.
	 *
	 * @param organizationId the organization ID
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 */
	public void removeByGtO_C_P(
		long organizationId, long companyId, long parentOrganizationId);

	/**
	 * Returns the number of organizations where organizationId &gt; &#63; and companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * @param organizationId the organization ID
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @return the number of matching organizations
	 */
	public int countByGtO_C_P(
		long organizationId, long companyId, long parentOrganizationId);

	/**
	 * Returns the number of organizations that the user has permission to view where organizationId &gt; &#63; and companyId = &#63; and parentOrganizationId = &#63;.
	 *
	 * @param organizationId the organization ID
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @return the number of matching organizations that the user has permission to view
	 */
	public int filterCountByGtO_C_P(
		long organizationId, long companyId, long parentOrganizationId);

	/**
	 * Returns all the organizations where companyId = &#63; and parentOrganizationId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param name the name
	 * @return the matching organizations
	 */
	public java.util.List<Organization> findByC_P_LikeN(
		long companyId, long parentOrganizationId, String name);

	/**
	 * Returns a range of all the organizations where companyId = &#63; and parentOrganizationId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param name the name
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations
	 */
	public java.util.List<Organization> findByC_P_LikeN(
		long companyId, long parentOrganizationId, String name, int start,
		int end);

	/**
	 * Returns an ordered range of all the organizations where companyId = &#63; and parentOrganizationId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param name the name
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByC_P_LikeN(
		long companyId, long parentOrganizationId, String name, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns an ordered range of all the organizations where companyId = &#63; and parentOrganizationId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param name the name
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching organizations
	 */
	public java.util.List<Organization> findByC_P_LikeN(
		long companyId, long parentOrganizationId, String name, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first organization in the ordered set where companyId = &#63; and parentOrganizationId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByC_P_LikeN_First(
			long companyId, long parentOrganizationId, String name,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the first organization in the ordered set where companyId = &#63; and parentOrganizationId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByC_P_LikeN_First(
		long companyId, long parentOrganizationId, String name,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the last organization in the ordered set where companyId = &#63; and parentOrganizationId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByC_P_LikeN_Last(
			long companyId, long parentOrganizationId, String name,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns the last organization in the ordered set where companyId = &#63; and parentOrganizationId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByC_P_LikeN_Last(
		long companyId, long parentOrganizationId, String name,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the organizations before and after the current organization in the ordered set where companyId = &#63; and parentOrganizationId = &#63; and name LIKE &#63;.
	 *
	 * @param organizationId the primary key of the current organization
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next organization
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization[] findByC_P_LikeN_PrevAndNext(
			long organizationId, long companyId, long parentOrganizationId,
			String name,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Returns all the organizations that the user has permission to view where companyId = &#63; and parentOrganizationId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param name the name
	 * @return the matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByC_P_LikeN(
		long companyId, long parentOrganizationId, String name);

	/**
	 * Returns a range of all the organizations that the user has permission to view where companyId = &#63; and parentOrganizationId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param name the name
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByC_P_LikeN(
		long companyId, long parentOrganizationId, String name, int start,
		int end);

	/**
	 * Returns an ordered range of all the organizations that the user has permissions to view where companyId = &#63; and parentOrganizationId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param name the name
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching organizations that the user has permission to view
	 */
	public java.util.List<Organization> filterFindByC_P_LikeN(
		long companyId, long parentOrganizationId, String name, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns the organizations before and after the current organization in the ordered set of organizations that the user has permission to view where companyId = &#63; and parentOrganizationId = &#63; and name LIKE &#63;.
	 *
	 * @param organizationId the primary key of the current organization
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next organization
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization[] filterFindByC_P_LikeN_PrevAndNext(
			long organizationId, long companyId, long parentOrganizationId,
			String name,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws NoSuchOrganizationException;

	/**
	 * Removes all the organizations where companyId = &#63; and parentOrganizationId = &#63; and name LIKE &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param name the name
	 */
	public void removeByC_P_LikeN(
		long companyId, long parentOrganizationId, String name);

	/**
	 * Returns the number of organizations where companyId = &#63; and parentOrganizationId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param name the name
	 * @return the number of matching organizations
	 */
	public int countByC_P_LikeN(
		long companyId, long parentOrganizationId, String name);

	/**
	 * Returns the number of organizations that the user has permission to view where companyId = &#63; and parentOrganizationId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentOrganizationId the parent organization ID
	 * @param name the name
	 * @return the number of matching organizations that the user has permission to view
	 */
	public int filterCountByC_P_LikeN(
		long companyId, long parentOrganizationId, String name);

	/**
	 * Returns the organization where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchOrganizationException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching organization
	 * @throws NoSuchOrganizationException if a matching organization could not be found
	 */
	public Organization findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOrganizationException;

	/**
	 * Returns the organization where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByERC_C(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the organization where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public Organization fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the organization where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the organization that was removed
	 */
	public Organization removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOrganizationException;

	/**
	 * Returns the number of organizations where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching organizations
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Caches the organization in the entity cache if it is enabled.
	 *
	 * @param organization the organization
	 */
	public void cacheResult(Organization organization);

	/**
	 * Caches the organizations in the entity cache if it is enabled.
	 *
	 * @param organizations the organizations
	 */
	public void cacheResult(java.util.List<Organization> organizations);

	/**
	 * Creates a new organization with the primary key. Does not add the organization to the database.
	 *
	 * @param organizationId the primary key for the new organization
	 * @return the new organization
	 */
	public Organization create(long organizationId);

	/**
	 * Removes the organization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the organization that was removed
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization remove(long organizationId)
		throws NoSuchOrganizationException;

	public Organization updateImpl(Organization organization);

	/**
	 * Returns the organization with the primary key or throws a <code>NoSuchOrganizationException</code> if it could not be found.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the organization
	 * @throws NoSuchOrganizationException if a organization with the primary key could not be found
	 */
	public Organization findByPrimaryKey(long organizationId)
		throws NoSuchOrganizationException;

	/**
	 * Returns the organization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the organization, or <code>null</code> if a organization with the primary key could not be found
	 */
	public Organization fetchByPrimaryKey(long organizationId);

	/**
	 * Returns all the organizations.
	 *
	 * @return the organizations
	 */
	public java.util.List<Organization> findAll();

	/**
	 * Returns a range of all the organizations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of organizations
	 */
	public java.util.List<Organization> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the organizations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of organizations
	 */
	public java.util.List<Organization> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator);

	/**
	 * Returns an ordered range of all the organizations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of organizations
	 */
	public java.util.List<Organization> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the organizations from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of organizations.
	 *
	 * @return the number of organizations
	 */
	public int countAll();

	/**
	 * Returns the primaryKeys of groups associated with the organization.
	 *
	 * @param pk the primary key of the organization
	 * @return long[] of the primaryKeys of groups associated with the organization
	 */
	public long[] getGroupPrimaryKeys(long pk);

	/**
	 * Returns all the groups associated with the organization.
	 *
	 * @param pk the primary key of the organization
	 * @return the groups associated with the organization
	 */
	public java.util.List<com.liferay.portal.kernel.model.Group> getGroups(
		long pk);

	/**
	 * Returns a range of all the groups associated with the organization.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the organization
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of groups associated with the organization
	 */
	public java.util.List<com.liferay.portal.kernel.model.Group> getGroups(
		long pk, int start, int end);

	/**
	 * Returns an ordered range of all the groups associated with the organization.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the organization
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of groups associated with the organization
	 */
	public java.util.List<com.liferay.portal.kernel.model.Group> getGroups(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<com.liferay.portal.kernel.model.Group> orderByComparator);

	/**
	 * Returns the number of groups associated with the organization.
	 *
	 * @param pk the primary key of the organization
	 * @return the number of groups associated with the organization
	 */
	public int getGroupsSize(long pk);

	/**
	 * Returns <code>true</code> if the group is associated with the organization.
	 *
	 * @param pk the primary key of the organization
	 * @param groupPK the primary key of the group
	 * @return <code>true</code> if the group is associated with the organization; <code>false</code> otherwise
	 */
	public boolean containsGroup(long pk, long groupPK);

	/**
	 * Returns <code>true</code> if the organization has any groups associated with it.
	 *
	 * @param pk the primary key of the organization to check for associations with groups
	 * @return <code>true</code> if the organization has any groups associated with it; <code>false</code> otherwise
	 */
	public boolean containsGroups(long pk);

	/**
	 * Adds an association between the organization and the group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param groupPK the primary key of the group
	 * @return <code>true</code> if an association between the organization and the group was added; <code>false</code> if they were already associated
	 */
	public boolean addGroup(long pk, long groupPK);

	/**
	 * Adds an association between the organization and the group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param group the group
	 * @return <code>true</code> if an association between the organization and the group was added; <code>false</code> if they were already associated
	 */
	public boolean addGroup(
		long pk, com.liferay.portal.kernel.model.Group group);

	/**
	 * Adds an association between the organization and the groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param groupPKs the primary keys of the groups
	 * @return <code>true</code> if at least one association between the organization and the groups was added; <code>false</code> if they were all already associated
	 */
	public boolean addGroups(long pk, long[] groupPKs);

	/**
	 * Adds an association between the organization and the groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param groups the groups
	 * @return <code>true</code> if at least one association between the organization and the groups was added; <code>false</code> if they were all already associated
	 */
	public boolean addGroups(
		long pk, java.util.List<com.liferay.portal.kernel.model.Group> groups);

	/**
	 * Clears all associations between the organization and its groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization to clear the associated groups from
	 */
	public void clearGroups(long pk);

	/**
	 * Removes the association between the organization and the group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param groupPK the primary key of the group
	 */
	public void removeGroup(long pk, long groupPK);

	/**
	 * Removes the association between the organization and the group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param group the group
	 */
	public void removeGroup(
		long pk, com.liferay.portal.kernel.model.Group group);

	/**
	 * Removes the association between the organization and the groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param groupPKs the primary keys of the groups
	 */
	public void removeGroups(long pk, long[] groupPKs);

	/**
	 * Removes the association between the organization and the groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param groups the groups
	 */
	public void removeGroups(
		long pk, java.util.List<com.liferay.portal.kernel.model.Group> groups);

	/**
	 * Sets the groups associated with the organization, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param groupPKs the primary keys of the groups to be associated with the organization
	 */
	public void setGroups(long pk, long[] groupPKs);

	/**
	 * Sets the groups associated with the organization, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param groups the groups to be associated with the organization
	 */
	public void setGroups(
		long pk, java.util.List<com.liferay.portal.kernel.model.Group> groups);

	/**
	 * Returns the primaryKeys of users associated with the organization.
	 *
	 * @param pk the primary key of the organization
	 * @return long[] of the primaryKeys of users associated with the organization
	 */
	public long[] getUserPrimaryKeys(long pk);

	/**
	 * Returns all the users associated with the organization.
	 *
	 * @param pk the primary key of the organization
	 * @return the users associated with the organization
	 */
	public java.util.List<com.liferay.portal.kernel.model.User> getUsers(
		long pk);

	/**
	 * Returns a range of all the users associated with the organization.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the organization
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of users associated with the organization
	 */
	public java.util.List<com.liferay.portal.kernel.model.User> getUsers(
		long pk, int start, int end);

	/**
	 * Returns an ordered range of all the users associated with the organization.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the organization
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of users associated with the organization
	 */
	public java.util.List<com.liferay.portal.kernel.model.User> getUsers(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<com.liferay.portal.kernel.model.User> orderByComparator);

	/**
	 * Returns the number of users associated with the organization.
	 *
	 * @param pk the primary key of the organization
	 * @return the number of users associated with the organization
	 */
	public int getUsersSize(long pk);

	/**
	 * Returns <code>true</code> if the user is associated with the organization.
	 *
	 * @param pk the primary key of the organization
	 * @param userPK the primary key of the user
	 * @return <code>true</code> if the user is associated with the organization; <code>false</code> otherwise
	 */
	public boolean containsUser(long pk, long userPK);

	/**
	 * Returns <code>true</code> if the organization has any users associated with it.
	 *
	 * @param pk the primary key of the organization to check for associations with users
	 * @return <code>true</code> if the organization has any users associated with it; <code>false</code> otherwise
	 */
	public boolean containsUsers(long pk);

	/**
	 * Adds an association between the organization and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param userPK the primary key of the user
	 * @return <code>true</code> if an association between the organization and the user was added; <code>false</code> if they were already associated
	 */
	public boolean addUser(long pk, long userPK);

	/**
	 * Adds an association between the organization and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param user the user
	 * @return <code>true</code> if an association between the organization and the user was added; <code>false</code> if they were already associated
	 */
	public boolean addUser(long pk, com.liferay.portal.kernel.model.User user);

	/**
	 * Adds an association between the organization and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param userPKs the primary keys of the users
	 * @return <code>true</code> if at least one association between the organization and the users was added; <code>false</code> if they were all already associated
	 */
	public boolean addUsers(long pk, long[] userPKs);

	/**
	 * Adds an association between the organization and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param users the users
	 * @return <code>true</code> if at least one association between the organization and the users was added; <code>false</code> if they were all already associated
	 */
	public boolean addUsers(
		long pk, java.util.List<com.liferay.portal.kernel.model.User> users);

	/**
	 * Clears all associations between the organization and its users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization to clear the associated users from
	 */
	public void clearUsers(long pk);

	/**
	 * Removes the association between the organization and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param userPK the primary key of the user
	 */
	public void removeUser(long pk, long userPK);

	/**
	 * Removes the association between the organization and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param user the user
	 */
	public void removeUser(long pk, com.liferay.portal.kernel.model.User user);

	/**
	 * Removes the association between the organization and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param userPKs the primary keys of the users
	 */
	public void removeUsers(long pk, long[] userPKs);

	/**
	 * Removes the association between the organization and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param users the users
	 */
	public void removeUsers(
		long pk, java.util.List<com.liferay.portal.kernel.model.User> users);

	/**
	 * Sets the users associated with the organization, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param userPKs the primary keys of the users to be associated with the organization
	 */
	public void setUsers(long pk, long[] userPKs);

	/**
	 * Sets the users associated with the organization, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the organization
	 * @param users the users to be associated with the organization
	 */
	public void setUsers(
		long pk, java.util.List<com.liferay.portal.kernel.model.User> users);

}