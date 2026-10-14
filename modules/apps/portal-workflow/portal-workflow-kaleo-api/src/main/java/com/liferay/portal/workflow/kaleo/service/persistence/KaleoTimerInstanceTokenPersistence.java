/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.workflow.kaleo.exception.NoSuchTimerInstanceTokenException;
import com.liferay.portal.workflow.kaleo.model.KaleoTimerInstanceToken;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the kaleo timer instance token service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see KaleoTimerInstanceTokenUtil
 * @generated
 */
@ProviderType
public interface KaleoTimerInstanceTokenPersistence
	extends BasePersistence<KaleoTimerInstanceToken>,
			CTPersistence<KaleoTimerInstanceToken> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link KaleoTimerInstanceTokenUtil} to access the kaleo timer instance token persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the kaleo timer instance tokens where kaleoInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param start the lower bound of the range of kaleo timer instance tokens
	 * @param end the upper bound of the range of kaleo timer instance tokens (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo timer instance tokens
	 */
	public java.util.List<KaleoTimerInstanceToken> findByKaleoInstanceId(
		long kaleoInstanceId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTimerInstanceToken> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kaleo timer instance token in the ordered set where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo timer instance token
	 * @throws NoSuchTimerInstanceTokenException if a matching kaleo timer instance token could not be found
	 */
	public KaleoTimerInstanceToken findByKaleoInstanceId_First(
			long kaleoInstanceId,
			com.liferay.portal.kernel.util.OrderByComparator
				<KaleoTimerInstanceToken> orderByComparator)
		throws NoSuchTimerInstanceTokenException;

	/**
	 * Returns the first kaleo timer instance token in the ordered set where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo timer instance token, or <code>null</code> if a matching kaleo timer instance token could not be found
	 */
	public KaleoTimerInstanceToken fetchByKaleoInstanceId_First(
		long kaleoInstanceId,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTimerInstanceToken> orderByComparator);

	/**
	 * Removes all the kaleo timer instance tokens where kaleoInstanceId = &#63; from the database.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 */
	public void removeByKaleoInstanceId(long kaleoInstanceId);

	/**
	 * Returns the number of kaleo timer instance tokens where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @return the number of matching kaleo timer instance tokens
	 */
	public int countByKaleoInstanceId(long kaleoInstanceId);

	/**
	 * Returns an ordered range of all the kaleo timer instance tokens where kaleoInstanceTokenId = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo timer instance tokens
	 * @param end the upper bound of the range of kaleo timer instance tokens (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo timer instance tokens
	 */
	public java.util.List<KaleoTimerInstanceToken> findByKITI_C(
		long kaleoInstanceTokenId, boolean completed, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTimerInstanceToken> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kaleo timer instance token in the ordered set where kaleoInstanceTokenId = &#63; and completed = &#63;.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo timer instance token
	 * @throws NoSuchTimerInstanceTokenException if a matching kaleo timer instance token could not be found
	 */
	public KaleoTimerInstanceToken findByKITI_C_First(
			long kaleoInstanceTokenId, boolean completed,
			com.liferay.portal.kernel.util.OrderByComparator
				<KaleoTimerInstanceToken> orderByComparator)
		throws NoSuchTimerInstanceTokenException;

	/**
	 * Returns the first kaleo timer instance token in the ordered set where kaleoInstanceTokenId = &#63; and completed = &#63;.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo timer instance token, or <code>null</code> if a matching kaleo timer instance token could not be found
	 */
	public KaleoTimerInstanceToken fetchByKITI_C_First(
		long kaleoInstanceTokenId, boolean completed,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTimerInstanceToken> orderByComparator);

	/**
	 * Removes all the kaleo timer instance tokens where kaleoInstanceTokenId = &#63; and completed = &#63; from the database.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param completed the completed
	 */
	public void removeByKITI_C(long kaleoInstanceTokenId, boolean completed);

	/**
	 * Returns the number of kaleo timer instance tokens where kaleoInstanceTokenId = &#63; and completed = &#63;.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param completed the completed
	 * @return the number of matching kaleo timer instance tokens
	 */
	public int countByKITI_C(long kaleoInstanceTokenId, boolean completed);

	/**
	 * Returns an ordered range of all the kaleo timer instance tokens where kaleoInstanceTokenId = &#63; and blocking = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param blocking the blocking
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo timer instance tokens
	 * @param end the upper bound of the range of kaleo timer instance tokens (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo timer instance tokens
	 */
	public java.util.List<KaleoTimerInstanceToken> findByKITI_B_C(
		long kaleoInstanceTokenId, boolean blocking, boolean completed,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTimerInstanceToken> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kaleo timer instance token in the ordered set where kaleoInstanceTokenId = &#63; and blocking = &#63; and completed = &#63;.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param blocking the blocking
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo timer instance token
	 * @throws NoSuchTimerInstanceTokenException if a matching kaleo timer instance token could not be found
	 */
	public KaleoTimerInstanceToken findByKITI_B_C_First(
			long kaleoInstanceTokenId, boolean blocking, boolean completed,
			com.liferay.portal.kernel.util.OrderByComparator
				<KaleoTimerInstanceToken> orderByComparator)
		throws NoSuchTimerInstanceTokenException;

	/**
	 * Returns the first kaleo timer instance token in the ordered set where kaleoInstanceTokenId = &#63; and blocking = &#63; and completed = &#63;.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param blocking the blocking
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo timer instance token, or <code>null</code> if a matching kaleo timer instance token could not be found
	 */
	public KaleoTimerInstanceToken fetchByKITI_B_C_First(
		long kaleoInstanceTokenId, boolean blocking, boolean completed,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTimerInstanceToken> orderByComparator);

	/**
	 * Removes all the kaleo timer instance tokens where kaleoInstanceTokenId = &#63; and blocking = &#63; and completed = &#63; from the database.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param blocking the blocking
	 * @param completed the completed
	 */
	public void removeByKITI_B_C(
		long kaleoInstanceTokenId, boolean blocking, boolean completed);

	/**
	 * Returns the number of kaleo timer instance tokens where kaleoInstanceTokenId = &#63; and blocking = &#63; and completed = &#63;.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param blocking the blocking
	 * @param completed the completed
	 * @return the number of matching kaleo timer instance tokens
	 */
	public int countByKITI_B_C(
		long kaleoInstanceTokenId, boolean blocking, boolean completed);

	/**
	 * Creates a new kaleo timer instance token with the primary key. Does not add the kaleo timer instance token to the database.
	 *
	 * @param kaleoTimerInstanceTokenId the primary key for the new kaleo timer instance token
	 * @return the new kaleo timer instance token
	 */
	public KaleoTimerInstanceToken create(long kaleoTimerInstanceTokenId);

	/**
	 * Removes the kaleo timer instance token with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoTimerInstanceTokenId the primary key of the kaleo timer instance token
	 * @return the kaleo timer instance token that was removed
	 * @throws NoSuchTimerInstanceTokenException if a kaleo timer instance token with the primary key could not be found
	 */
	public KaleoTimerInstanceToken remove(long kaleoTimerInstanceTokenId)
		throws NoSuchTimerInstanceTokenException;

	public KaleoTimerInstanceToken updateImpl(
		KaleoTimerInstanceToken kaleoTimerInstanceToken);

	/**
	 * Returns the kaleo timer instance token with the primary key or throws a <code>NoSuchTimerInstanceTokenException</code> if it could not be found.
	 *
	 * @param kaleoTimerInstanceTokenId the primary key of the kaleo timer instance token
	 * @return the kaleo timer instance token
	 * @throws NoSuchTimerInstanceTokenException if a kaleo timer instance token with the primary key could not be found
	 */
	public KaleoTimerInstanceToken findByPrimaryKey(
			long kaleoTimerInstanceTokenId)
		throws NoSuchTimerInstanceTokenException;

	/**
	 * Returns the kaleo timer instance token with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoTimerInstanceTokenId the primary key of the kaleo timer instance token
	 * @return the kaleo timer instance token, or <code>null</code> if a kaleo timer instance token with the primary key could not be found
	 */
	public KaleoTimerInstanceToken fetchByPrimaryKey(
		long kaleoTimerInstanceTokenId);

	/**
	 * Returns all the kaleo timer instance tokens where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @return the matching kaleo timer instance tokens
	 */
	public default java.util.List<KaleoTimerInstanceToken>
		findByKaleoInstanceId(long kaleoInstanceId) {

		return findByKaleoInstanceId(
			kaleoInstanceId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kaleo timer instance tokens where kaleoInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param start the lower bound of the range of kaleo timer instance tokens
	 * @param end the upper bound of the range of kaleo timer instance tokens (not inclusive)
	 * @return the range of matching kaleo timer instance tokens
	 */
	public default java.util.List<KaleoTimerInstanceToken>
		findByKaleoInstanceId(long kaleoInstanceId, int start, int end) {

		return findByKaleoInstanceId(kaleoInstanceId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kaleo timer instance tokens where kaleoInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param start the lower bound of the range of kaleo timer instance tokens
	 * @param end the upper bound of the range of kaleo timer instance tokens (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo timer instance tokens
	 */
	public default java.util.List<KaleoTimerInstanceToken>
		findByKaleoInstanceId(
			long kaleoInstanceId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<KaleoTimerInstanceToken> orderByComparator) {

		return findByKaleoInstanceId(
			kaleoInstanceId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the kaleo timer instance tokens where kaleoInstanceTokenId = &#63; and completed = &#63;.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param completed the completed
	 * @return the matching kaleo timer instance tokens
	 */
	public default java.util.List<KaleoTimerInstanceToken> findByKITI_C(
		long kaleoInstanceTokenId, boolean completed) {

		return findByKITI_C(
			kaleoInstanceTokenId, completed,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kaleo timer instance tokens where kaleoInstanceTokenId = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo timer instance tokens
	 * @param end the upper bound of the range of kaleo timer instance tokens (not inclusive)
	 * @return the range of matching kaleo timer instance tokens
	 */
	public default java.util.List<KaleoTimerInstanceToken> findByKITI_C(
		long kaleoInstanceTokenId, boolean completed, int start, int end) {

		return findByKITI_C(
			kaleoInstanceTokenId, completed, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kaleo timer instance tokens where kaleoInstanceTokenId = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo timer instance tokens
	 * @param end the upper bound of the range of kaleo timer instance tokens (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo timer instance tokens
	 */
	public default java.util.List<KaleoTimerInstanceToken> findByKITI_C(
		long kaleoInstanceTokenId, boolean completed, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTimerInstanceToken> orderByComparator) {

		return findByKITI_C(
			kaleoInstanceTokenId, completed, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the kaleo timer instance tokens where kaleoInstanceTokenId = &#63; and blocking = &#63; and completed = &#63;.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param blocking the blocking
	 * @param completed the completed
	 * @return the matching kaleo timer instance tokens
	 */
	public default java.util.List<KaleoTimerInstanceToken> findByKITI_B_C(
		long kaleoInstanceTokenId, boolean blocking, boolean completed) {

		return findByKITI_B_C(
			kaleoInstanceTokenId, blocking, completed,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kaleo timer instance tokens where kaleoInstanceTokenId = &#63; and blocking = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param blocking the blocking
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo timer instance tokens
	 * @param end the upper bound of the range of kaleo timer instance tokens (not inclusive)
	 * @return the range of matching kaleo timer instance tokens
	 */
	public default java.util.List<KaleoTimerInstanceToken> findByKITI_B_C(
		long kaleoInstanceTokenId, boolean blocking, boolean completed,
		int start, int end) {

		return findByKITI_B_C(
			kaleoInstanceTokenId, blocking, completed, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kaleo timer instance tokens where kaleoInstanceTokenId = &#63; and blocking = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param blocking the blocking
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo timer instance tokens
	 * @param end the upper bound of the range of kaleo timer instance tokens (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo timer instance tokens
	 */
	public default java.util.List<KaleoTimerInstanceToken> findByKITI_B_C(
		long kaleoInstanceTokenId, boolean blocking, boolean completed,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTimerInstanceToken> orderByComparator) {

		return findByKITI_B_C(
			kaleoInstanceTokenId, blocking, completed, start, end,
			orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-468621818