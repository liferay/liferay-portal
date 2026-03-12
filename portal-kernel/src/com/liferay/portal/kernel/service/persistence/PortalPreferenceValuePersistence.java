/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.exception.NoSuchPreferenceValueException;
import com.liferay.portal.kernel.model.PortalPreferenceValue;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the portal preference value service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PortalPreferenceValueUtil
 * @generated
 */
@ProviderType
public interface PortalPreferenceValuePersistence
	extends BasePersistence<PortalPreferenceValue> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link PortalPreferenceValueUtil} to access the portal preference value persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the portal preference values where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @return the matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByPortalPreferencesId(
		long portalPreferencesId);

	/**
	 * Returns a range of all the portal preference values where portalPreferencesId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @return the range of matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByPortalPreferencesId(
		long portalPreferencesId, int start, int end);

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByPortalPreferencesId(
		long portalPreferencesId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator);

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByPortalPreferencesId(
		long portalPreferencesId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	public PortalPreferenceValue findByPortalPreferencesId_First(
			long portalPreferencesId,
			com.liferay.portal.kernel.util.OrderByComparator
				<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException;

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public PortalPreferenceValue fetchByPortalPreferencesId_First(
		long portalPreferencesId,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator);

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	public PortalPreferenceValue findByPortalPreferencesId_Last(
			long portalPreferencesId,
			com.liferay.portal.kernel.util.OrderByComparator
				<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException;

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public PortalPreferenceValue fetchByPortalPreferencesId_Last(
		long portalPreferencesId,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator);

	/**
	 * Returns the portal preference values before and after the current portal preference value in the ordered set where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferenceValueId the primary key of the current portal preference value
	 * @param portalPreferencesId the portal preferences ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next portal preference value
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	public PortalPreferenceValue[] findByPortalPreferencesId_PrevAndNext(
			long portalPreferenceValueId, long portalPreferencesId,
			com.liferay.portal.kernel.util.OrderByComparator
				<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException;

	/**
	 * Returns all the portal preference values where portalPreferencesId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesIds the portal preferences IDs
	 * @return the matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByPortalPreferencesId(
		long[] portalPreferencesIds);

	/**
	 * Returns a range of all the portal preference values where portalPreferencesId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesIds the portal preferences IDs
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @return the range of matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByPortalPreferencesId(
		long[] portalPreferencesIds, int start, int end);

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesIds the portal preferences IDs
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByPortalPreferencesId(
		long[] portalPreferencesIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator);

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesIds the portal preferences IDs
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByPortalPreferencesId(
		long[] portalPreferencesIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the portal preference values where portalPreferencesId = &#63; from the database.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 */
	public void removeByPortalPreferencesId(long portalPreferencesId);

	/**
	 * Returns the number of portal preference values where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @return the number of matching portal preference values
	 */
	public int countByPortalPreferencesId(long portalPreferencesId);

	/**
	 * Returns the number of portal preference values where portalPreferencesId = any &#63;.
	 *
	 * @param portalPreferencesIds the portal preferences IDs
	 * @return the number of matching portal preference values
	 */
	public int countByPortalPreferencesId(long[] portalPreferencesIds);

	/**
	 * Returns all the portal preference values where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @return the matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByP_N(
		long portalPreferencesId, String namespace);

	/**
	 * Returns a range of all the portal preference values where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @return the range of matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByP_N(
		long portalPreferencesId, String namespace, int start, int end);

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByP_N(
		long portalPreferencesId, String namespace, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator);

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByP_N(
		long portalPreferencesId, String namespace, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	public PortalPreferenceValue findByP_N_First(
			long portalPreferencesId, String namespace,
			com.liferay.portal.kernel.util.OrderByComparator
				<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException;

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public PortalPreferenceValue fetchByP_N_First(
		long portalPreferencesId, String namespace,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator);

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	public PortalPreferenceValue findByP_N_Last(
			long portalPreferencesId, String namespace,
			com.liferay.portal.kernel.util.OrderByComparator
				<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException;

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public PortalPreferenceValue fetchByP_N_Last(
		long portalPreferencesId, String namespace,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator);

	/**
	 * Returns the portal preference values before and after the current portal preference value in the ordered set where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferenceValueId the primary key of the current portal preference value
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next portal preference value
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	public PortalPreferenceValue[] findByP_N_PrevAndNext(
			long portalPreferenceValueId, long portalPreferencesId,
			String namespace,
			com.liferay.portal.kernel.util.OrderByComparator
				<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException;

	/**
	 * Removes all the portal preference values where portalPreferencesId = &#63; and namespace = &#63; from the database.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 */
	public void removeByP_N(long portalPreferencesId, String namespace);

	/**
	 * Returns the number of portal preference values where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @return the number of matching portal preference values
	 */
	public int countByP_N(long portalPreferencesId, String namespace);

	/**
	 * Returns all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @return the matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByP_K_N(
		long portalPreferencesId, String key, String namespace);

	/**
	 * Returns a range of all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @return the range of matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByP_K_N(
		long portalPreferencesId, String key, String namespace, int start,
		int end);

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByP_K_N(
		long portalPreferencesId, String key, String namespace, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator);

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByP_K_N(
		long portalPreferencesId, String key, String namespace, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	public PortalPreferenceValue findByP_K_N_First(
			long portalPreferencesId, String key, String namespace,
			com.liferay.portal.kernel.util.OrderByComparator
				<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException;

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public PortalPreferenceValue fetchByP_K_N_First(
		long portalPreferencesId, String key, String namespace,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator);

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	public PortalPreferenceValue findByP_K_N_Last(
			long portalPreferencesId, String key, String namespace,
			com.liferay.portal.kernel.util.OrderByComparator
				<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException;

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public PortalPreferenceValue fetchByP_K_N_Last(
		long portalPreferencesId, String key, String namespace,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator);

	/**
	 * Returns the portal preference values before and after the current portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferenceValueId the primary key of the current portal preference value
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next portal preference value
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	public PortalPreferenceValue[] findByP_K_N_PrevAndNext(
			long portalPreferenceValueId, long portalPreferencesId, String key,
			String namespace,
			com.liferay.portal.kernel.util.OrderByComparator
				<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException;

	/**
	 * Removes all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; from the database.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 */
	public void removeByP_K_N(
		long portalPreferencesId, String key, String namespace);

	/**
	 * Returns the number of portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @return the number of matching portal preference values
	 */
	public int countByP_K_N(
		long portalPreferencesId, String key, String namespace);

	/**
	 * Returns the portal preference value where portalPreferencesId = &#63; and index = &#63; and key = &#63; and namespace = &#63; or throws a <code>NoSuchPreferenceValueException</code> if it could not be found.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param index the index
	 * @param key the key
	 * @param namespace the namespace
	 * @return the matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	public PortalPreferenceValue findByP_I_K_N(
			long portalPreferencesId, int index, String key, String namespace)
		throws NoSuchPreferenceValueException;

	/**
	 * Returns the portal preference value where portalPreferencesId = &#63; and index = &#63; and key = &#63; and namespace = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param index the index
	 * @param key the key
	 * @param namespace the namespace
	 * @return the matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public PortalPreferenceValue fetchByP_I_K_N(
		long portalPreferencesId, int index, String key, String namespace);

	/**
	 * Returns the portal preference value where portalPreferencesId = &#63; and index = &#63; and key = &#63; and namespace = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param index the index
	 * @param key the key
	 * @param namespace the namespace
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public PortalPreferenceValue fetchByP_I_K_N(
		long portalPreferencesId, int index, String key, String namespace,
		boolean useFinderCache);

	/**
	 * Removes the portal preference value where portalPreferencesId = &#63; and index = &#63; and key = &#63; and namespace = &#63; from the database.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param index the index
	 * @param key the key
	 * @param namespace the namespace
	 * @return the portal preference value that was removed
	 */
	public PortalPreferenceValue removeByP_I_K_N(
			long portalPreferencesId, int index, String key, String namespace)
		throws NoSuchPreferenceValueException;

	/**
	 * Returns the number of portal preference values where portalPreferencesId = &#63; and index = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param index the index
	 * @param key the key
	 * @param namespace the namespace
	 * @return the number of matching portal preference values
	 */
	public int countByP_I_K_N(
		long portalPreferencesId, int index, String key, String namespace);

	/**
	 * Returns all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @return the matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByP_K_N_SV(
		long portalPreferencesId, String key, String namespace,
		String smallValue);

	/**
	 * Returns a range of all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @return the range of matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByP_K_N_SV(
		long portalPreferencesId, String key, String namespace,
		String smallValue, int start, int end);

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByP_K_N_SV(
		long portalPreferencesId, String key, String namespace,
		String smallValue, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator);

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findByP_K_N_SV(
		long portalPreferencesId, String key, String namespace,
		String smallValue, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	public PortalPreferenceValue findByP_K_N_SV_First(
			long portalPreferencesId, String key, String namespace,
			String smallValue,
			com.liferay.portal.kernel.util.OrderByComparator
				<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException;

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public PortalPreferenceValue fetchByP_K_N_SV_First(
		long portalPreferencesId, String key, String namespace,
		String smallValue,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator);

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	public PortalPreferenceValue findByP_K_N_SV_Last(
			long portalPreferencesId, String key, String namespace,
			String smallValue,
			com.liferay.portal.kernel.util.OrderByComparator
				<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException;

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public PortalPreferenceValue fetchByP_K_N_SV_Last(
		long portalPreferencesId, String key, String namespace,
		String smallValue,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator);

	/**
	 * Returns the portal preference values before and after the current portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferenceValueId the primary key of the current portal preference value
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next portal preference value
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	public PortalPreferenceValue[] findByP_K_N_SV_PrevAndNext(
			long portalPreferenceValueId, long portalPreferencesId, String key,
			String namespace, String smallValue,
			com.liferay.portal.kernel.util.OrderByComparator
				<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException;

	/**
	 * Removes all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63; from the database.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 */
	public void removeByP_K_N_SV(
		long portalPreferencesId, String key, String namespace,
		String smallValue);

	/**
	 * Returns the number of portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @return the number of matching portal preference values
	 */
	public int countByP_K_N_SV(
		long portalPreferencesId, String key, String namespace,
		String smallValue);

	/**
	 * Caches the portal preference value in the entity cache if it is enabled.
	 *
	 * @param portalPreferenceValue the portal preference value
	 */
	public void cacheResult(PortalPreferenceValue portalPreferenceValue);

	/**
	 * Caches the portal preference values in the entity cache if it is enabled.
	 *
	 * @param portalPreferenceValues the portal preference values
	 */
	public void cacheResult(
		java.util.List<PortalPreferenceValue> portalPreferenceValues);

	/**
	 * Creates a new portal preference value with the primary key. Does not add the portal preference value to the database.
	 *
	 * @param portalPreferenceValueId the primary key for the new portal preference value
	 * @return the new portal preference value
	 */
	public PortalPreferenceValue create(long portalPreferenceValueId);

	/**
	 * Removes the portal preference value with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param portalPreferenceValueId the primary key of the portal preference value
	 * @return the portal preference value that was removed
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	public PortalPreferenceValue remove(long portalPreferenceValueId)
		throws NoSuchPreferenceValueException;

	public PortalPreferenceValue updateImpl(
		PortalPreferenceValue portalPreferenceValue);

	/**
	 * Returns the portal preference value with the primary key or throws a <code>NoSuchPreferenceValueException</code> if it could not be found.
	 *
	 * @param portalPreferenceValueId the primary key of the portal preference value
	 * @return the portal preference value
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	public PortalPreferenceValue findByPrimaryKey(long portalPreferenceValueId)
		throws NoSuchPreferenceValueException;

	/**
	 * Returns the portal preference value with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param portalPreferenceValueId the primary key of the portal preference value
	 * @return the portal preference value, or <code>null</code> if a portal preference value with the primary key could not be found
	 */
	public PortalPreferenceValue fetchByPrimaryKey(
		long portalPreferenceValueId);

	/**
	 * Returns all the portal preference values.
	 *
	 * @return the portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findAll();

	/**
	 * Returns a range of all the portal preference values.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @return the range of portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the portal preference values.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator);

	/**
	 * Returns an ordered range of all the portal preference values.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of portal preference values
	 */
	public java.util.List<PortalPreferenceValue> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PortalPreferenceValue>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the portal preference values from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of portal preference values.
	 *
	 * @return the number of portal preference values
	 */
	public int countAll();

}