/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence;

import com.liferay.osb.faro.exception.NoSuchFaroProjectEmailDomainException;
import com.liferay.osb.faro.model.FaroProjectEmailDomain;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the faro project email domain service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matthew Kong
 * @see FaroProjectEmailDomainUtil
 * @generated
 */
@ProviderType
public interface FaroProjectEmailDomainPersistence
	extends BasePersistence<FaroProjectEmailDomain> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link FaroProjectEmailDomainUtil} to access the faro project email domain persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the faro project email domains where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectEmailDomainModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of faro project email domains
	 * @param end the upper bound of the range of faro project email domains (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro project email domains
	 */
	public java.util.List<FaroProjectEmailDomain> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FaroProjectEmailDomain>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first faro project email domain in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project email domain
	 * @throws NoSuchFaroProjectEmailDomainException if a matching faro project email domain could not be found
	 */
	public FaroProjectEmailDomain findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<FaroProjectEmailDomain> orderByComparator)
		throws NoSuchFaroProjectEmailDomainException;

	/**
	 * Returns the first faro project email domain in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project email domain, or <code>null</code> if a matching faro project email domain could not be found
	 */
	public FaroProjectEmailDomain fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<FaroProjectEmailDomain>
			orderByComparator);

	/**
	 * Removes all the faro project email domains where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of faro project email domains where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching faro project email domains
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns an ordered range of all the faro project email domains where faroProjectId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectEmailDomainModelImpl</code>.
	 * </p>
	 *
	 * @param faroProjectId the faro project ID
	 * @param start the lower bound of the range of faro project email domains
	 * @param end the upper bound of the range of faro project email domains (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro project email domains
	 */
	public java.util.List<FaroProjectEmailDomain> findByFaroProjectId(
		long faroProjectId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FaroProjectEmailDomain>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first faro project email domain in the ordered set where faroProjectId = &#63;.
	 *
	 * @param faroProjectId the faro project ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project email domain
	 * @throws NoSuchFaroProjectEmailDomainException if a matching faro project email domain could not be found
	 */
	public FaroProjectEmailDomain findByFaroProjectId_First(
			long faroProjectId,
			com.liferay.portal.kernel.util.OrderByComparator
				<FaroProjectEmailDomain> orderByComparator)
		throws NoSuchFaroProjectEmailDomainException;

	/**
	 * Returns the first faro project email domain in the ordered set where faroProjectId = &#63;.
	 *
	 * @param faroProjectId the faro project ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project email domain, or <code>null</code> if a matching faro project email domain could not be found
	 */
	public FaroProjectEmailDomain fetchByFaroProjectId_First(
		long faroProjectId,
		com.liferay.portal.kernel.util.OrderByComparator<FaroProjectEmailDomain>
			orderByComparator);

	/**
	 * Removes all the faro project email domains where faroProjectId = &#63; from the database.
	 *
	 * @param faroProjectId the faro project ID
	 */
	public void removeByFaroProjectId(long faroProjectId);

	/**
	 * Returns the number of faro project email domains where faroProjectId = &#63;.
	 *
	 * @param faroProjectId the faro project ID
	 * @return the number of matching faro project email domains
	 */
	public int countByFaroProjectId(long faroProjectId);

	/**
	 * Creates a new faro project email domain with the primary key. Does not add the faro project email domain to the database.
	 *
	 * @param faroProjectEmailDomainId the primary key for the new faro project email domain
	 * @return the new faro project email domain
	 */
	public FaroProjectEmailDomain create(long faroProjectEmailDomainId);

	/**
	 * Removes the faro project email domain with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param faroProjectEmailDomainId the primary key of the faro project email domain
	 * @return the faro project email domain that was removed
	 * @throws NoSuchFaroProjectEmailDomainException if a faro project email domain with the primary key could not be found
	 */
	public FaroProjectEmailDomain remove(long faroProjectEmailDomainId)
		throws NoSuchFaroProjectEmailDomainException;

	public FaroProjectEmailDomain updateImpl(
		FaroProjectEmailDomain faroProjectEmailDomain);

	/**
	 * Returns the faro project email domain with the primary key or throws a <code>NoSuchFaroProjectEmailDomainException</code> if it could not be found.
	 *
	 * @param faroProjectEmailDomainId the primary key of the faro project email domain
	 * @return the faro project email domain
	 * @throws NoSuchFaroProjectEmailDomainException if a faro project email domain with the primary key could not be found
	 */
	public FaroProjectEmailDomain findByPrimaryKey(
			long faroProjectEmailDomainId)
		throws NoSuchFaroProjectEmailDomainException;

	/**
	 * Returns the faro project email domain with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param faroProjectEmailDomainId the primary key of the faro project email domain
	 * @return the faro project email domain, or <code>null</code> if a faro project email domain with the primary key could not be found
	 */
	public FaroProjectEmailDomain fetchByPrimaryKey(
		long faroProjectEmailDomainId);

	/**
	 * Returns all the faro project email domains where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching faro project email domains
	 */
	public default java.util.List<FaroProjectEmailDomain> findByGroupId(
		long groupId) {

		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the faro project email domains where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectEmailDomainModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of faro project email domains
	 * @param end the upper bound of the range of faro project email domains (not inclusive)
	 * @return the range of matching faro project email domains
	 */
	public default java.util.List<FaroProjectEmailDomain> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the faro project email domains where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectEmailDomainModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of faro project email domains
	 * @param end the upper bound of the range of faro project email domains (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro project email domains
	 */
	public default java.util.List<FaroProjectEmailDomain> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FaroProjectEmailDomain>
			orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the faro project email domains where faroProjectId = &#63;.
	 *
	 * @param faroProjectId the faro project ID
	 * @return the matching faro project email domains
	 */
	public default java.util.List<FaroProjectEmailDomain> findByFaroProjectId(
		long faroProjectId) {

		return findByFaroProjectId(
			faroProjectId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the faro project email domains where faroProjectId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectEmailDomainModelImpl</code>.
	 * </p>
	 *
	 * @param faroProjectId the faro project ID
	 * @param start the lower bound of the range of faro project email domains
	 * @param end the upper bound of the range of faro project email domains (not inclusive)
	 * @return the range of matching faro project email domains
	 */
	public default java.util.List<FaroProjectEmailDomain> findByFaroProjectId(
		long faroProjectId, int start, int end) {

		return findByFaroProjectId(faroProjectId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the faro project email domains where faroProjectId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectEmailDomainModelImpl</code>.
	 * </p>
	 *
	 * @param faroProjectId the faro project ID
	 * @param start the lower bound of the range of faro project email domains
	 * @param end the upper bound of the range of faro project email domains (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro project email domains
	 */
	public default java.util.List<FaroProjectEmailDomain> findByFaroProjectId(
		long faroProjectId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FaroProjectEmailDomain>
			orderByComparator) {

		return findByFaroProjectId(
			faroProjectId, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1609621345