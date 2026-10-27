/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service.persistence;

import com.liferay.layout.content.exception.NoSuchLayoutContentVersionException;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the layout content version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Lourdes Fernández Besada
 * @see LayoutContentVersionUtil
 * @generated
 */
@ProviderType
public interface LayoutContentVersionPersistence
	extends BasePersistence<LayoutContentVersion> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link LayoutContentVersionUtil} to access the layout content version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the layout content versions where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout content versions
	 * @param end the upper bound of the range of layout content versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout content versions
	 */
	public java.util.List<LayoutContentVersion> findByPlid(
		long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutContentVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout content version in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version
	 * @throws NoSuchLayoutContentVersionException if a matching layout content version could not be found
	 */
	public LayoutContentVersion findByPlid_First(
			long plid,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutContentVersion> orderByComparator)
		throws NoSuchLayoutContentVersionException;

	/**
	 * Returns the first layout content version in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version, or <code>null</code> if a matching layout content version could not be found
	 */
	public LayoutContentVersion fetchByPlid_First(
		long plid,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutContentVersion>
			orderByComparator);

	/**
	 * Removes all the layout content versions where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	public void removeByPlid(long plid);

	/**
	 * Returns the number of layout content versions where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout content versions
	 */
	public int countByPlid(long plid);

	/**
	 * Returns the layout content version where plid = &#63; and version = &#63; or throws a <code>NoSuchLayoutContentVersionException</code> if it could not be found.
	 *
	 * @param plid the plid
	 * @param version the version
	 * @return the matching layout content version
	 * @throws NoSuchLayoutContentVersionException if a matching layout content version could not be found
	 */
	public LayoutContentVersion findByP_V(long plid, int version)
		throws NoSuchLayoutContentVersionException;

	/**
	 * Returns the layout content version where plid = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param plid the plid
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout content version, or <code>null</code> if a matching layout content version could not be found
	 */
	public LayoutContentVersion fetchByP_V(
		long plid, int version, boolean useFinderCache);

	/**
	 * Removes the layout content version where plid = &#63; and version = &#63; from the database.
	 *
	 * @param plid the plid
	 * @param version the version
	 * @return the layout content version that was removed
	 */
	public LayoutContentVersion removeByP_V(long plid, int version)
		throws NoSuchLayoutContentVersionException;

	/**
	 * Returns the number of layout content versions where plid = &#63; and version = &#63;.
	 *
	 * @param plid the plid
	 * @param version the version
	 * @return the number of matching layout content versions
	 */
	public int countByP_V(long plid, int version);

	/**
	 * Returns an ordered range of all the layout content versions where plid = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param start the lower bound of the range of layout content versions
	 * @param end the upper bound of the range of layout content versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout content versions
	 */
	public java.util.List<LayoutContentVersion> findByP_S(
		long plid, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutContentVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout content version in the ordered set where plid = &#63; and status = &#63;.
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version
	 * @throws NoSuchLayoutContentVersionException if a matching layout content version could not be found
	 */
	public LayoutContentVersion findByP_S_First(
			long plid, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutContentVersion> orderByComparator)
		throws NoSuchLayoutContentVersionException;

	/**
	 * Returns the first layout content version in the ordered set where plid = &#63; and status = &#63;.
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version, or <code>null</code> if a matching layout content version could not be found
	 */
	public LayoutContentVersion fetchByP_S_First(
		long plid, int status,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutContentVersion>
			orderByComparator);

	/**
	 * Removes all the layout content versions where plid = &#63; and status = &#63; from the database.
	 *
	 * @param plid the plid
	 * @param status the status
	 */
	public void removeByP_S(long plid, int status);

	/**
	 * Returns the number of layout content versions where plid = &#63; and status = &#63;.
	 *
	 * @param plid the plid
	 * @param status the status
	 * @return the number of matching layout content versions
	 */
	public int countByP_S(long plid, int status);

	/**
	 * Returns the layout content version where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchLayoutContentVersionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout content version
	 * @throws NoSuchLayoutContentVersionException if a matching layout content version could not be found
	 */
	public LayoutContentVersion findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchLayoutContentVersionException;

	/**
	 * Returns the layout content version where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout content version, or <code>null</code> if a matching layout content version could not be found
	 */
	public LayoutContentVersion fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache);

	/**
	 * Removes the layout content version where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout content version that was removed
	 */
	public LayoutContentVersion removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchLayoutContentVersionException;

	/**
	 * Returns the number of layout content versions where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layout content versions
	 */
	public int countByERC_G(String externalReferenceCode, long groupId);

	/**
	 * Creates a new layout content version with the primary key. Does not add the layout content version to the database.
	 *
	 * @param layoutContentVersionId the primary key for the new layout content version
	 * @return the new layout content version
	 */
	public LayoutContentVersion create(long layoutContentVersionId);

	/**
	 * Removes the layout content version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutContentVersionId the primary key of the layout content version
	 * @return the layout content version that was removed
	 * @throws NoSuchLayoutContentVersionException if a layout content version with the primary key could not be found
	 */
	public LayoutContentVersion remove(long layoutContentVersionId)
		throws NoSuchLayoutContentVersionException;

	public LayoutContentVersion updateImpl(
		LayoutContentVersion layoutContentVersion);

	/**
	 * Returns the layout content version with the primary key or throws a <code>NoSuchLayoutContentVersionException</code> if it could not be found.
	 *
	 * @param layoutContentVersionId the primary key of the layout content version
	 * @return the layout content version
	 * @throws NoSuchLayoutContentVersionException if a layout content version with the primary key could not be found
	 */
	public LayoutContentVersion findByPrimaryKey(long layoutContentVersionId)
		throws NoSuchLayoutContentVersionException;

	/**
	 * Returns the layout content version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutContentVersionId the primary key of the layout content version
	 * @return the layout content version, or <code>null</code> if a layout content version with the primary key could not be found
	 */
	public LayoutContentVersion fetchByPrimaryKey(long layoutContentVersionId);

	/**
	 * Returns the layout content version where plid = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param plid the plid
	 * @param version the version
	 * @return the matching layout content version, or <code>null</code> if a matching layout content version could not be found
	 */
	public default LayoutContentVersion fetchByP_V(long plid, int version) {
		return fetchByP_V(plid, version, true);
	}

	/**
	 * Returns the layout content version where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout content version, or <code>null</code> if a matching layout content version could not be found
	 */
	public default LayoutContentVersion fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns all the layout content versions where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the matching layout content versions
	 */
	public default java.util.List<LayoutContentVersion> findByPlid(long plid) {
		return findByPlid(
			plid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout content versions where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout content versions
	 * @param end the upper bound of the range of layout content versions (not inclusive)
	 * @return the range of matching layout content versions
	 */
	public default java.util.List<LayoutContentVersion> findByPlid(
		long plid, int start, int end) {

		return findByPlid(plid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout content versions where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout content versions
	 * @param end the upper bound of the range of layout content versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout content versions
	 */
	public default java.util.List<LayoutContentVersion> findByPlid(
		long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutContentVersion>
			orderByComparator) {

		return findByPlid(plid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout content versions where plid = &#63; and status = &#63;.
	 *
	 * @param plid the plid
	 * @param status the status
	 * @return the matching layout content versions
	 */
	public default java.util.List<LayoutContentVersion> findByP_S(
		long plid, int status) {

		return findByP_S(
			plid, status, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout content versions where plid = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param start the lower bound of the range of layout content versions
	 * @param end the upper bound of the range of layout content versions (not inclusive)
	 * @return the range of matching layout content versions
	 */
	public default java.util.List<LayoutContentVersion> findByP_S(
		long plid, int status, int start, int end) {

		return findByP_S(plid, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout content versions where plid = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param start the lower bound of the range of layout content versions
	 * @param end the upper bound of the range of layout content versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout content versions
	 */
	public default java.util.List<LayoutContentVersion> findByP_S(
		long plid, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutContentVersion>
			orderByComparator) {

		return findByP_S(plid, status, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-2010843019