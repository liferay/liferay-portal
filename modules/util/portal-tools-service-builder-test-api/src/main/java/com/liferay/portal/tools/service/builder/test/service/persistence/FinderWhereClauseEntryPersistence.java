/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchFinderWhereClauseEntryException;
import com.liferay.portal.tools.service.builder.test.model.FinderWhereClauseEntry;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the finder where clause entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FinderWhereClauseEntryUtil
 * @generated
 */
@ProviderType
public interface FinderWhereClauseEntryPersistence
	extends BasePersistence<FinderWhereClauseEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link FinderWhereClauseEntryUtil} to access the finder where clause entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the finder where clause entries where headId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param headId the head ID
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching finder where clause entries
	 */
	public java.util.List<FinderWhereClauseEntry> findByHeadId(
		long headId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FinderWhereClauseEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first finder where clause entry in the ordered set where headId = &#63;.
	 *
	 * @param headId the head ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder where clause entry
	 * @throws NoSuchFinderWhereClauseEntryException if a matching finder where clause entry could not be found
	 */
	public FinderWhereClauseEntry findByHeadId_First(
			long headId,
			com.liferay.portal.kernel.util.OrderByComparator
				<FinderWhereClauseEntry> orderByComparator)
		throws NoSuchFinderWhereClauseEntryException;

	/**
	 * Returns the first finder where clause entry in the ordered set where headId = &#63;.
	 *
	 * @param headId the head ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder where clause entry, or <code>null</code> if a matching finder where clause entry could not be found
	 */
	public FinderWhereClauseEntry fetchByHeadId_First(
		long headId,
		com.liferay.portal.kernel.util.OrderByComparator<FinderWhereClauseEntry>
			orderByComparator);

	/**
	 * Removes all the finder where clause entries where headId = &#63; from the database.
	 *
	 * @param headId the head ID
	 */
	public void removeByHeadId(long headId);

	/**
	 * Returns the number of finder where clause entries where headId = &#63;.
	 *
	 * @param headId the head ID
	 * @return the number of matching finder where clause entries
	 */
	public int countByHeadId(long headId);

	/**
	 * Returns an ordered range of all the finder where clause entries where name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching finder where clause entries
	 */
	public java.util.List<FinderWhereClauseEntry> findByName_Nickname(
		String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FinderWhereClauseEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first finder where clause entry in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder where clause entry
	 * @throws NoSuchFinderWhereClauseEntryException if a matching finder where clause entry could not be found
	 */
	public FinderWhereClauseEntry findByName_Nickname_First(
			String name,
			com.liferay.portal.kernel.util.OrderByComparator
				<FinderWhereClauseEntry> orderByComparator)
		throws NoSuchFinderWhereClauseEntryException;

	/**
	 * Returns the first finder where clause entry in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder where clause entry, or <code>null</code> if a matching finder where clause entry could not be found
	 */
	public FinderWhereClauseEntry fetchByName_Nickname_First(
		String name,
		com.liferay.portal.kernel.util.OrderByComparator<FinderWhereClauseEntry>
			orderByComparator);

	/**
	 * Removes all the finder where clause entries where name = &#63; from the database.
	 *
	 * @param name the name
	 */
	public void removeByName_Nickname(String name);

	/**
	 * Returns the number of finder where clause entries where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching finder where clause entries
	 */
	public int countByName_Nickname(String name);

	/**
	 * Returns an ordered range of all the finder where clause entries where status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param status the status
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching finder where clause entries
	 */
	public java.util.List<FinderWhereClauseEntry> findByStatus(
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FinderWhereClauseEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first finder where clause entry in the ordered set where status = &#63;.
	 *
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder where clause entry
	 * @throws NoSuchFinderWhereClauseEntryException if a matching finder where clause entry could not be found
	 */
	public FinderWhereClauseEntry findByStatus_First(
			int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<FinderWhereClauseEntry> orderByComparator)
		throws NoSuchFinderWhereClauseEntryException;

	/**
	 * Returns the first finder where clause entry in the ordered set where status = &#63;.
	 *
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder where clause entry, or <code>null</code> if a matching finder where clause entry could not be found
	 */
	public FinderWhereClauseEntry fetchByStatus_First(
		int status,
		com.liferay.portal.kernel.util.OrderByComparator<FinderWhereClauseEntry>
			orderByComparator);

	/**
	 * Removes all the finder where clause entries where status = &#63; from the database.
	 *
	 * @param status the status
	 */
	public void removeByStatus(int status);

	/**
	 * Returns the number of finder where clause entries where status = &#63;.
	 *
	 * @param status the status
	 * @return the number of matching finder where clause entries
	 */
	public int countByStatus(int status);

	/**
	 * Returns an ordered range of all the finder where clause entries where name = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching finder where clause entries
	 */
	public java.util.List<FinderWhereClauseEntry> findByName_Status(
		String name, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FinderWhereClauseEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first finder where clause entry in the ordered set where name = &#63; and status = &#63;.
	 *
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder where clause entry
	 * @throws NoSuchFinderWhereClauseEntryException if a matching finder where clause entry could not be found
	 */
	public FinderWhereClauseEntry findByName_Status_First(
			String name, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<FinderWhereClauseEntry> orderByComparator)
		throws NoSuchFinderWhereClauseEntryException;

	/**
	 * Returns the first finder where clause entry in the ordered set where name = &#63; and status = &#63;.
	 *
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder where clause entry, or <code>null</code> if a matching finder where clause entry could not be found
	 */
	public FinderWhereClauseEntry fetchByName_Status_First(
		String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator<FinderWhereClauseEntry>
			orderByComparator);

	/**
	 * Removes all the finder where clause entries where name = &#63; and status = &#63; from the database.
	 *
	 * @param name the name
	 * @param status the status
	 */
	public void removeByName_Status(String name, int status);

	/**
	 * Returns the number of finder where clause entries where name = &#63; and status = &#63;.
	 *
	 * @param name the name
	 * @param status the status
	 * @return the number of matching finder where clause entries
	 */
	public int countByName_Status(String name, int status);

	/**
	 * Creates a new finder where clause entry with the primary key. Does not add the finder where clause entry to the database.
	 *
	 * @param finderWhereClauseEntryId the primary key for the new finder where clause entry
	 * @return the new finder where clause entry
	 */
	public FinderWhereClauseEntry create(long finderWhereClauseEntryId);

	/**
	 * Removes the finder where clause entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param finderWhereClauseEntryId the primary key of the finder where clause entry
	 * @return the finder where clause entry that was removed
	 * @throws NoSuchFinderWhereClauseEntryException if a finder where clause entry with the primary key could not be found
	 */
	public FinderWhereClauseEntry remove(long finderWhereClauseEntryId)
		throws NoSuchFinderWhereClauseEntryException;

	public FinderWhereClauseEntry updateImpl(
		FinderWhereClauseEntry finderWhereClauseEntry);

	/**
	 * Returns the finder where clause entry with the primary key or throws a <code>NoSuchFinderWhereClauseEntryException</code> if it could not be found.
	 *
	 * @param finderWhereClauseEntryId the primary key of the finder where clause entry
	 * @return the finder where clause entry
	 * @throws NoSuchFinderWhereClauseEntryException if a finder where clause entry with the primary key could not be found
	 */
	public FinderWhereClauseEntry findByPrimaryKey(
			long finderWhereClauseEntryId)
		throws NoSuchFinderWhereClauseEntryException;

	/**
	 * Returns the finder where clause entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param finderWhereClauseEntryId the primary key of the finder where clause entry
	 * @return the finder where clause entry, or <code>null</code> if a finder where clause entry with the primary key could not be found
	 */
	public FinderWhereClauseEntry fetchByPrimaryKey(
		long finderWhereClauseEntryId);

	/**
	 * Returns all the finder where clause entries where headId = &#63;.
	 *
	 * @param headId the head ID
	 * @return the matching finder where clause entries
	 */
	public default java.util.List<FinderWhereClauseEntry> findByHeadId(
		long headId) {

		return findByHeadId(
			headId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the finder where clause entries where headId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param headId the head ID
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @return the range of matching finder where clause entries
	 */
	public default java.util.List<FinderWhereClauseEntry> findByHeadId(
		long headId, int start, int end) {

		return findByHeadId(headId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the finder where clause entries where headId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param headId the head ID
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching finder where clause entries
	 */
	public default java.util.List<FinderWhereClauseEntry> findByHeadId(
		long headId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FinderWhereClauseEntry>
			orderByComparator) {

		return findByHeadId(headId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the finder where clause entries where name = &#63;.
	 *
	 * @param name the name
	 * @return the matching finder where clause entries
	 */
	public default java.util.List<FinderWhereClauseEntry> findByName_Nickname(
		String name) {

		return findByName_Nickname(
			name, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the finder where clause entries where name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @return the range of matching finder where clause entries
	 */
	public default java.util.List<FinderWhereClauseEntry> findByName_Nickname(
		String name, int start, int end) {

		return findByName_Nickname(name, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the finder where clause entries where name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching finder where clause entries
	 */
	public default java.util.List<FinderWhereClauseEntry> findByName_Nickname(
		String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FinderWhereClauseEntry>
			orderByComparator) {

		return findByName_Nickname(name, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the finder where clause entries where status = &#63;.
	 *
	 * @param status the status
	 * @return the matching finder where clause entries
	 */
	public default java.util.List<FinderWhereClauseEntry> findByStatus(
		int status) {

		return findByStatus(
			status, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the finder where clause entries where status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param status the status
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @return the range of matching finder where clause entries
	 */
	public default java.util.List<FinderWhereClauseEntry> findByStatus(
		int status, int start, int end) {

		return findByStatus(status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the finder where clause entries where status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param status the status
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching finder where clause entries
	 */
	public default java.util.List<FinderWhereClauseEntry> findByStatus(
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FinderWhereClauseEntry>
			orderByComparator) {

		return findByStatus(status, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the finder where clause entries where name = &#63; and status = &#63;.
	 *
	 * @param name the name
	 * @param status the status
	 * @return the matching finder where clause entries
	 */
	public default java.util.List<FinderWhereClauseEntry> findByName_Status(
		String name, int status) {

		return findByName_Status(
			name, status, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the finder where clause entries where name = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @return the range of matching finder where clause entries
	 */
	public default java.util.List<FinderWhereClauseEntry> findByName_Status(
		String name, int status, int start, int end) {

		return findByName_Status(name, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the finder where clause entries where name = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching finder where clause entries
	 */
	public default java.util.List<FinderWhereClauseEntry> findByName_Status(
		String name, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FinderWhereClauseEntry>
			orderByComparator) {

		return findByName_Status(
			name, status, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-312741306