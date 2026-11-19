/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence;

import com.liferay.change.tracking.exception.NoSuchProcessException;
import com.liferay.change.tracking.model.CTProcess;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the ct process service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see CTProcessUtil
 * @generated
 */
@ProviderType
public interface CTProcessPersistence extends BasePersistence<CTProcess> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CTProcessUtil} to access the ct process persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the ct processes where ctCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTProcessModelImpl</code>.
	 * </p>
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param start the lower bound of the range of ct processes
	 * @param end the upper bound of the range of ct processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct processes
	 */
	public java.util.List<CTProcess> findByCtCollectionId(
		long ctCollectionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTProcess>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ct process in the ordered set where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct process
	 * @throws NoSuchProcessException if a matching ct process could not be found
	 */
	public CTProcess findByCtCollectionId_First(
			long ctCollectionId,
			com.liferay.portal.kernel.util.OrderByComparator<CTProcess>
				orderByComparator)
		throws NoSuchProcessException;

	/**
	 * Returns the first ct process in the ordered set where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct process, or <code>null</code> if a matching ct process could not be found
	 */
	public CTProcess fetchByCtCollectionId_First(
		long ctCollectionId,
		com.liferay.portal.kernel.util.OrderByComparator<CTProcess>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ct processes that the user has permissions to view where ctCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTProcessModelImpl</code>.
	 * </p>
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param start the lower bound of the range of ct processes
	 * @param end the upper bound of the range of ct processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct processes that the user has permission to view
	 */
	public java.util.List<CTProcess> filterFindByCtCollectionId(
		long ctCollectionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTProcess>
			orderByComparator);

	/**
	 * Removes all the ct processes where ctCollectionId = &#63; from the database.
	 *
	 * @param ctCollectionId the ct collection ID
	 */
	public void removeByCtCollectionId(long ctCollectionId);

	/**
	 * Returns the number of ct processes where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @return the number of matching ct processes
	 */
	public int countByCtCollectionId(long ctCollectionId);

	/**
	 * Returns the number of ct processes that the user has permission to view where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @return the number of matching ct processes that the user has permission to view
	 */
	public int filterCountByCtCollectionId(long ctCollectionId);

	/**
	 * Returns an ordered range of all the ct processes where ctCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTProcessModelImpl</code>.
	 * </p>
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param type the type
	 * @param start the lower bound of the range of ct processes
	 * @param end the upper bound of the range of ct processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct processes
	 */
	public java.util.List<CTProcess> findByC_T(
		long ctCollectionId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTProcess>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ct process in the ordered set where ctCollectionId = &#63; and type = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct process
	 * @throws NoSuchProcessException if a matching ct process could not be found
	 */
	public CTProcess findByC_T_First(
			long ctCollectionId, int type,
			com.liferay.portal.kernel.util.OrderByComparator<CTProcess>
				orderByComparator)
		throws NoSuchProcessException;

	/**
	 * Returns the first ct process in the ordered set where ctCollectionId = &#63; and type = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct process, or <code>null</code> if a matching ct process could not be found
	 */
	public CTProcess fetchByC_T_First(
		long ctCollectionId, int type,
		com.liferay.portal.kernel.util.OrderByComparator<CTProcess>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ct processes that the user has permissions to view where ctCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTProcessModelImpl</code>.
	 * </p>
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param type the type
	 * @param start the lower bound of the range of ct processes
	 * @param end the upper bound of the range of ct processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct processes that the user has permission to view
	 */
	public java.util.List<CTProcess> filterFindByC_T(
		long ctCollectionId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTProcess>
			orderByComparator);

	/**
	 * Removes all the ct processes where ctCollectionId = &#63; and type = &#63; from the database.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param type the type
	 */
	public void removeByC_T(long ctCollectionId, int type);

	/**
	 * Returns the number of ct processes where ctCollectionId = &#63; and type = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param type the type
	 * @return the number of matching ct processes
	 */
	public int countByC_T(long ctCollectionId, int type);

	/**
	 * Returns the number of ct processes that the user has permission to view where ctCollectionId = &#63; and type = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param type the type
	 * @return the number of matching ct processes that the user has permission to view
	 */
	public int filterCountByC_T(long ctCollectionId, int type);

	/**
	 * Creates a new ct process with the primary key. Does not add the ct process to the database.
	 *
	 * @param ctProcessId the primary key for the new ct process
	 * @return the new ct process
	 */
	public CTProcess create(long ctProcessId);

	/**
	 * Removes the ct process with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctProcessId the primary key of the ct process
	 * @return the ct process that was removed
	 * @throws NoSuchProcessException if a ct process with the primary key could not be found
	 */
	public CTProcess remove(long ctProcessId) throws NoSuchProcessException;

	public CTProcess updateImpl(CTProcess ctProcess);

	/**
	 * Returns the ct process with the primary key or throws a <code>NoSuchProcessException</code> if it could not be found.
	 *
	 * @param ctProcessId the primary key of the ct process
	 * @return the ct process
	 * @throws NoSuchProcessException if a ct process with the primary key could not be found
	 */
	public CTProcess findByPrimaryKey(long ctProcessId)
		throws NoSuchProcessException;

	/**
	 * Returns the ct process with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctProcessId the primary key of the ct process
	 * @return the ct process, or <code>null</code> if a ct process with the primary key could not be found
	 */
	public CTProcess fetchByPrimaryKey(long ctProcessId);

	/**
	 * Returns all the ct processes where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @return the matching ct processes
	 */
	public default java.util.List<CTProcess> findByCtCollectionId(
		long ctCollectionId) {

		return findByCtCollectionId(
			ctCollectionId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ct processes where ctCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTProcessModelImpl</code>.
	 * </p>
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param start the lower bound of the range of ct processes
	 * @param end the upper bound of the range of ct processes (not inclusive)
	 * @return the range of matching ct processes
	 */
	public default java.util.List<CTProcess> findByCtCollectionId(
		long ctCollectionId, int start, int end) {

		return findByCtCollectionId(ctCollectionId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the ct processes where ctCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTProcessModelImpl</code>.
	 * </p>
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param start the lower bound of the range of ct processes
	 * @param end the upper bound of the range of ct processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct processes
	 */
	public default java.util.List<CTProcess> findByCtCollectionId(
		long ctCollectionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTProcess>
			orderByComparator) {

		return findByCtCollectionId(
			ctCollectionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the ct processes that the user has permission to view where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @return the matching ct processes that the user has permission to view
	 */
	public default java.util.List<CTProcess> filterFindByCtCollectionId(
		long ctCollectionId) {

		return filterFindByCtCollectionId(
			ctCollectionId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ct processes that the user has permission to view where ctCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTProcessModelImpl</code>.
	 * </p>
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param start the lower bound of the range of ct processes
	 * @param end the upper bound of the range of ct processes (not inclusive)
	 * @return the range of matching ct processes that the user has permission to view
	 */
	public default java.util.List<CTProcess> filterFindByCtCollectionId(
		long ctCollectionId, int start, int end) {

		return filterFindByCtCollectionId(ctCollectionId, start, end, null);
	}

	/**
	 * Returns all the ct processes where ctCollectionId = &#63; and type = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param type the type
	 * @return the matching ct processes
	 */
	public default java.util.List<CTProcess> findByC_T(
		long ctCollectionId, int type) {

		return findByC_T(
			ctCollectionId, type,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the ct processes where ctCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTProcessModelImpl</code>.
	 * </p>
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param type the type
	 * @param start the lower bound of the range of ct processes
	 * @param end the upper bound of the range of ct processes (not inclusive)
	 * @return the range of matching ct processes
	 */
	public default java.util.List<CTProcess> findByC_T(
		long ctCollectionId, int type, int start, int end) {

		return findByC_T(ctCollectionId, type, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the ct processes where ctCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTProcessModelImpl</code>.
	 * </p>
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param type the type
	 * @param start the lower bound of the range of ct processes
	 * @param end the upper bound of the range of ct processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct processes
	 */
	public default java.util.List<CTProcess> findByC_T(
		long ctCollectionId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CTProcess>
			orderByComparator) {

		return findByC_T(
			ctCollectionId, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the ct processes that the user has permission to view where ctCollectionId = &#63; and type = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param type the type
	 * @return the matching ct processes that the user has permission to view
	 */
	public default java.util.List<CTProcess> filterFindByC_T(
		long ctCollectionId, int type) {

		return filterFindByC_T(
			ctCollectionId, type,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ct processes that the user has permission to view where ctCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTProcessModelImpl</code>.
	 * </p>
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param type the type
	 * @param start the lower bound of the range of ct processes
	 * @param end the upper bound of the range of ct processes (not inclusive)
	 * @return the range of matching ct processes that the user has permission to view
	 */
	public default java.util.List<CTProcess> filterFindByC_T(
		long ctCollectionId, int type, int start, int end) {

		return filterFindByC_T(ctCollectionId, type, start, end, null);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:754248224