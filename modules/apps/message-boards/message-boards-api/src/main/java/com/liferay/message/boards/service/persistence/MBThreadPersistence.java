/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence;

import com.liferay.message.boards.exception.NoSuchThreadException;
import com.liferay.message.boards.model.MBThread;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import java.util.Date;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the message boards thread service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see MBThreadUtil
 * @generated
 */
@ProviderType
public interface MBThreadPersistence
	extends BasePersistence<MBThread>, CTPersistence<MBThread> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link MBThreadUtil} to access the message boards thread persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the message boards threads where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public java.util.List<MBThread> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first message boards thread in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public MBThread findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<MBThread>
				orderByComparator)
		throws NoSuchThreadException;

	/**
	 * Returns the first message boards thread in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public MBThread fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Removes all the message boards threads where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of message boards threads where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching message boards threads
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the message boards thread where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchThreadException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public MBThread findByUUID_G(String uuid, long groupId)
		throws NoSuchThreadException;

	/**
	 * Returns the message boards thread where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public MBThread fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the message boards thread where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the message boards thread that was removed
	 */
	public MBThread removeByUUID_G(String uuid, long groupId)
		throws NoSuchThreadException;

	/**
	 * Returns the number of message boards threads where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching message boards threads
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the message boards threads where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public java.util.List<MBThread> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first message boards thread in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public MBThread findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<MBThread>
				orderByComparator)
		throws NoSuchThreadException;

	/**
	 * Returns the first message boards thread in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public MBThread fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Removes all the message boards threads where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of message boards threads where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching message boards threads
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public java.util.List<MBThread> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public MBThread findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<MBThread>
				orderByComparator)
		throws NoSuchThreadException;

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public MBThread fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Returns an ordered range of all the message boards threads that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads that the user has permission to view
	 */
	public java.util.List<MBThread> filterFindByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Removes all the message boards threads where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of message boards threads where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching message boards threads
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns the number of message boards threads that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching message boards threads that the user has permission to view
	 */
	public int filterCountByGroupId(long groupId);

	/**
	 * Returns the message boards thread where rootMessageId = &#63; or throws a <code>NoSuchThreadException</code> if it could not be found.
	 *
	 * @param rootMessageId the root message ID
	 * @return the matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public MBThread findByRootMessageId(long rootMessageId)
		throws NoSuchThreadException;

	/**
	 * Returns the message boards thread where rootMessageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param rootMessageId the root message ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public MBThread fetchByRootMessageId(
		long rootMessageId, boolean useFinderCache);

	/**
	 * Removes the message boards thread where rootMessageId = &#63; from the database.
	 *
	 * @param rootMessageId the root message ID
	 * @return the message boards thread that was removed
	 */
	public MBThread removeByRootMessageId(long rootMessageId)
		throws NoSuchThreadException;

	/**
	 * Returns the number of message boards threads where rootMessageId = &#63;.
	 *
	 * @param rootMessageId the root message ID
	 * @return the number of matching message boards threads
	 */
	public int countByRootMessageId(long rootMessageId);

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public java.util.List<MBThread> findByG_C(
		long groupId, long categoryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public MBThread findByG_C_First(
			long groupId, long categoryId,
			com.liferay.portal.kernel.util.OrderByComparator<MBThread>
				orderByComparator)
		throws NoSuchThreadException;

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public MBThread fetchByG_C_First(
		long groupId, long categoryId,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Returns an ordered range of all the message boards threads that the user has permissions to view where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads that the user has permission to view
	 */
	public java.util.List<MBThread> filterFindByG_C(
		long groupId, long categoryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Returns an ordered range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads that the user has permission to view
	 */
	public java.util.List<MBThread> filterFindByG_C(
		long groupId, long[] categoryIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public java.util.List<MBThread> findByG_C(
		long groupId, long[] categoryIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the message boards threads where groupId = &#63; and categoryId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 */
	public void removeByG_C(long groupId, long categoryId);

	/**
	 * Returns the number of message boards threads where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the number of matching message boards threads
	 */
	public int countByG_C(long groupId, long categoryId);

	/**
	 * Returns the number of message boards threads where groupId = &#63; and categoryId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @return the number of matching message boards threads
	 */
	public int countByG_C(long groupId, long[] categoryIds);

	/**
	 * Returns the number of message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the number of matching message boards threads that the user has permission to view
	 */
	public int filterCountByG_C(long groupId, long categoryId);

	/**
	 * Returns the number of message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @return the number of matching message boards threads that the user has permission to view
	 */
	public int filterCountByG_C(long groupId, long[] categoryIds);

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public java.util.List<MBThread> findByG_S(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public MBThread findByG_S_First(
			long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<MBThread>
				orderByComparator)
		throws NoSuchThreadException;

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public MBThread fetchByG_S_First(
		long groupId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Returns an ordered range of all the message boards threads that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads that the user has permission to view
	 */
	public java.util.List<MBThread> filterFindByG_S(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Removes all the message boards threads where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	public void removeByG_S(long groupId, int status);

	/**
	 * Returns the number of message boards threads where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching message boards threads
	 */
	public int countByG_S(long groupId, int status);

	/**
	 * Returns the number of message boards threads that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching message boards threads that the user has permission to view
	 */
	public int filterCountByG_S(long groupId, int status);

	/**
	 * Returns an ordered range of all the message boards threads where lastPostDate = &#63; and priority = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param lastPostDate the last post date
	 * @param priority the priority
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public java.util.List<MBThread> findByL_P(
		Date lastPostDate, double priority, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first message boards thread in the ordered set where lastPostDate = &#63; and priority = &#63;.
	 *
	 * @param lastPostDate the last post date
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public MBThread findByL_P_First(
			Date lastPostDate, double priority,
			com.liferay.portal.kernel.util.OrderByComparator<MBThread>
				orderByComparator)
		throws NoSuchThreadException;

	/**
	 * Returns the first message boards thread in the ordered set where lastPostDate = &#63; and priority = &#63;.
	 *
	 * @param lastPostDate the last post date
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public MBThread fetchByL_P_First(
		Date lastPostDate, double priority,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Removes all the message boards threads where lastPostDate = &#63; and priority = &#63; from the database.
	 *
	 * @param lastPostDate the last post date
	 * @param priority the priority
	 */
	public void removeByL_P(Date lastPostDate, double priority);

	/**
	 * Returns the number of message boards threads where lastPostDate = &#63; and priority = &#63;.
	 *
	 * @param lastPostDate the last post date
	 * @param priority the priority
	 * @return the number of matching message boards threads
	 */
	public int countByL_P(Date lastPostDate, double priority);

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public java.util.List<MBThread> findByG_C_L(
		long groupId, long categoryId, Date lastPostDate, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public MBThread findByG_C_L_First(
			long groupId, long categoryId, Date lastPostDate,
			com.liferay.portal.kernel.util.OrderByComparator<MBThread>
				orderByComparator)
		throws NoSuchThreadException;

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public MBThread fetchByG_C_L_First(
		long groupId, long categoryId, Date lastPostDate,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Returns an ordered range of all the message boards threads that the user has permissions to view where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads that the user has permission to view
	 */
	public java.util.List<MBThread> filterFindByG_C_L(
		long groupId, long categoryId, Date lastPostDate, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Removes all the message boards threads where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 */
	public void removeByG_C_L(long groupId, long categoryId, Date lastPostDate);

	/**
	 * Returns the number of message boards threads where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @return the number of matching message boards threads
	 */
	public int countByG_C_L(long groupId, long categoryId, Date lastPostDate);

	/**
	 * Returns the number of message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @return the number of matching message boards threads that the user has permission to view
	 */
	public int filterCountByG_C_L(
		long groupId, long categoryId, Date lastPostDate);

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public java.util.List<MBThread> findByG_C_S(
		long groupId, long categoryId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public MBThread findByG_C_S_First(
			long groupId, long categoryId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<MBThread>
				orderByComparator)
		throws NoSuchThreadException;

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public MBThread fetchByG_C_S_First(
		long groupId, long categoryId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Returns an ordered range of all the message boards threads that the user has permissions to view where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads that the user has permission to view
	 */
	public java.util.List<MBThread> filterFindByG_C_S(
		long groupId, long categoryId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Returns an ordered range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads that the user has permission to view
	 */
	public java.util.List<MBThread> filterFindByG_C_S(
		long groupId, long[] categoryIds, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public java.util.List<MBThread> findByG_C_S(
		long groupId, long[] categoryIds, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the message boards threads where groupId = &#63; and categoryId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 */
	public void removeByG_C_S(long groupId, long categoryId, int status);

	/**
	 * Returns the number of message boards threads where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the number of matching message boards threads
	 */
	public int countByG_C_S(long groupId, long categoryId, int status);

	/**
	 * Returns the number of message boards threads where groupId = &#63; and categoryId = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @return the number of matching message boards threads
	 */
	public int countByG_C_S(long groupId, long[] categoryIds, int status);

	/**
	 * Returns the number of message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the number of matching message boards threads that the user has permission to view
	 */
	public int filterCountByG_C_S(long groupId, long categoryId, int status);

	/**
	 * Returns the number of message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @return the number of matching message boards threads that the user has permission to view
	 */
	public int filterCountByG_C_S(long groupId, long[] categoryIds, int status);

	/**
	 * Returns all the message boards threads where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the matching message boards threads
	 */
	public java.util.List<MBThread> findByG_C_NotS(
		long groupId, long categoryId, int status);

	/**
	 * Returns a range of all the message boards threads where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public java.util.List<MBThread> findByG_C_NotS(
		long groupId, long categoryId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public java.util.List<MBThread> findByG_C_NotS(
		long groupId, long categoryId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public java.util.List<MBThread> findByG_C_NotS(
		long groupId, long categoryId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public MBThread findByG_C_NotS_First(
			long groupId, long categoryId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<MBThread>
				orderByComparator)
		throws NoSuchThreadException;

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public MBThread fetchByG_C_NotS_First(
		long groupId, long categoryId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Returns all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the matching message boards threads that the user has permission to view
	 */
	public java.util.List<MBThread> filterFindByG_C_NotS(
		long groupId, long categoryId, int status);

	/**
	 * Returns a range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads that the user has permission to view
	 */
	public java.util.List<MBThread> filterFindByG_C_NotS(
		long groupId, long categoryId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the message boards threads that the user has permissions to view where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads that the user has permission to view
	 */
	public java.util.List<MBThread> filterFindByG_C_NotS(
		long groupId, long categoryId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Returns all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @return the matching message boards threads that the user has permission to view
	 */
	public java.util.List<MBThread> filterFindByG_C_NotS(
		long groupId, long[] categoryIds, int status);

	/**
	 * Returns a range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads that the user has permission to view
	 */
	public java.util.List<MBThread> filterFindByG_C_NotS(
		long groupId, long[] categoryIds, int status, int start, int end);

	/**
	 * Returns an ordered range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads that the user has permission to view
	 */
	public java.util.List<MBThread> filterFindByG_C_NotS(
		long groupId, long[] categoryIds, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Returns all the message boards threads where groupId = &#63; and categoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @return the matching message boards threads
	 */
	public java.util.List<MBThread> findByG_C_NotS(
		long groupId, long[] categoryIds, int status);

	/**
	 * Returns a range of all the message boards threads where groupId = &#63; and categoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public java.util.List<MBThread> findByG_C_NotS(
		long groupId, long[] categoryIds, int status, int start, int end);

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public java.util.List<MBThread> findByG_C_NotS(
		long groupId, long[] categoryIds, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator);

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public java.util.List<MBThread> findByG_C_NotS(
		long groupId, long[] categoryIds, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the message boards threads where groupId = &#63; and categoryId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 */
	public void removeByG_C_NotS(long groupId, long categoryId, int status);

	/**
	 * Returns the number of message boards threads where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the number of matching message boards threads
	 */
	public int countByG_C_NotS(long groupId, long categoryId, int status);

	/**
	 * Returns the number of message boards threads where groupId = &#63; and categoryId = any &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @return the number of matching message boards threads
	 */
	public int countByG_C_NotS(long groupId, long[] categoryIds, int status);

	/**
	 * Returns the number of message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the number of matching message boards threads that the user has permission to view
	 */
	public int filterCountByG_C_NotS(long groupId, long categoryId, int status);

	/**
	 * Returns the number of message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @return the number of matching message boards threads that the user has permission to view
	 */
	public int filterCountByG_C_NotS(
		long groupId, long[] categoryIds, int status);

	/**
	 * Creates a new message boards thread with the primary key. Does not add the message boards thread to the database.
	 *
	 * @param threadId the primary key for the new message boards thread
	 * @return the new message boards thread
	 */
	public MBThread create(long threadId);

	/**
	 * Removes the message boards thread with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param threadId the primary key of the message boards thread
	 * @return the message boards thread that was removed
	 * @throws NoSuchThreadException if a message boards thread with the primary key could not be found
	 */
	public MBThread remove(long threadId) throws NoSuchThreadException;

	public MBThread updateImpl(MBThread mbThread);

	/**
	 * Returns the message boards thread with the primary key or throws a <code>NoSuchThreadException</code> if it could not be found.
	 *
	 * @param threadId the primary key of the message boards thread
	 * @return the message boards thread
	 * @throws NoSuchThreadException if a message boards thread with the primary key could not be found
	 */
	public MBThread findByPrimaryKey(long threadId)
		throws NoSuchThreadException;

	/**
	 * Returns the message boards thread with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param threadId the primary key of the message boards thread
	 * @return the message boards thread, or <code>null</code> if a message boards thread with the primary key could not be found
	 */
	public MBThread fetchByPrimaryKey(long threadId);

	/**
	 * Returns the message boards thread where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public default MBThread fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the message boards thread where rootMessageId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param rootMessageId the root message ID
	 * @return the matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public default MBThread fetchByRootMessageId(long rootMessageId) {
		return fetchByRootMessageId(rootMessageId, true);
	}

	/**
	 * Returns all the message boards threads where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching message boards threads
	 */
	public default java.util.List<MBThread> findByUuid(String uuid) {
		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the message boards threads where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the message boards threads where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the message boards threads where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching message boards threads
	 */
	public default java.util.List<MBThread> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the message boards threads where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the message boards threads where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the message boards threads where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching message boards threads
	 */
	public default java.util.List<MBThread> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the message boards threads where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the message boards threads that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching message boards threads that the user has permission to view
	 */
	public default java.util.List<MBThread> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards threads that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads that the user has permission to view
	 */
	public default java.util.List<MBThread> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns all the message boards threads where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the matching message boards threads
	 */
	public default java.util.List<MBThread> findByG_C(
		long groupId, long categoryId) {

		return findByG_C(
			groupId, categoryId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the message boards threads where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByG_C(
		long groupId, long categoryId, int start, int end) {

		return findByG_C(groupId, categoryId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByG_C(
		long groupId, long categoryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator) {

		return findByG_C(
			groupId, categoryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the message boards threads where groupId = &#63; and categoryId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @return the matching message boards threads
	 */
	public default java.util.List<MBThread> findByG_C(
		long groupId, long[] categoryIds) {

		return findByG_C(
			groupId, categoryIds,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the message boards threads where groupId = &#63; and categoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByG_C(
		long groupId, long[] categoryIds, int start, int end) {

		return findByG_C(groupId, categoryIds, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByG_C(
		long groupId, long[] categoryIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator) {

		return findByG_C(
			groupId, categoryIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the matching message boards threads that the user has permission to view
	 */
	public default java.util.List<MBThread> filterFindByG_C(
		long groupId, long categoryId) {

		return filterFindByG_C(
			groupId, categoryId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads that the user has permission to view
	 */
	public default java.util.List<MBThread> filterFindByG_C(
		long groupId, long categoryId, int start, int end) {

		return filterFindByG_C(groupId, categoryId, start, end, null);
	}

	/**
	 * Returns all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @return the matching message boards threads that the user has permission to view
	 */
	public default java.util.List<MBThread> filterFindByG_C(
		long groupId, long[] categoryIds) {

		return filterFindByG_C(
			groupId, categoryIds,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads that the user has permission to view
	 */
	public default java.util.List<MBThread> filterFindByG_C(
		long groupId, long[] categoryIds, int start, int end) {

		return filterFindByG_C(groupId, categoryIds, start, end, null);
	}

	/**
	 * Returns all the message boards threads where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching message boards threads
	 */
	public default java.util.List<MBThread> findByG_S(
		long groupId, int status) {

		return findByG_S(
			groupId, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the message boards threads where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByG_S(
		long groupId, int status, int start, int end) {

		return findByG_S(groupId, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByG_S(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator) {

		return findByG_S(groupId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the message boards threads that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching message boards threads that the user has permission to view
	 */
	public default java.util.List<MBThread> filterFindByG_S(
		long groupId, int status) {

		return filterFindByG_S(
			groupId, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards threads that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads that the user has permission to view
	 */
	public default java.util.List<MBThread> filterFindByG_S(
		long groupId, int status, int start, int end) {

		return filterFindByG_S(groupId, status, start, end, null);
	}

	/**
	 * Returns all the message boards threads where lastPostDate = &#63; and priority = &#63;.
	 *
	 * @param lastPostDate the last post date
	 * @param priority the priority
	 * @return the matching message boards threads
	 */
	public default java.util.List<MBThread> findByL_P(
		Date lastPostDate, double priority) {

		return findByL_P(
			lastPostDate, priority,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the message boards threads where lastPostDate = &#63; and priority = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param lastPostDate the last post date
	 * @param priority the priority
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByL_P(
		Date lastPostDate, double priority, int start, int end) {

		return findByL_P(lastPostDate, priority, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the message boards threads where lastPostDate = &#63; and priority = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param lastPostDate the last post date
	 * @param priority the priority
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByL_P(
		Date lastPostDate, double priority, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator) {

		return findByL_P(
			lastPostDate, priority, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the message boards threads where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @return the matching message boards threads
	 */
	public default java.util.List<MBThread> findByG_C_L(
		long groupId, long categoryId, Date lastPostDate) {

		return findByG_C_L(
			groupId, categoryId, lastPostDate,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the message boards threads where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByG_C_L(
		long groupId, long categoryId, Date lastPostDate, int start, int end) {

		return findByG_C_L(
			groupId, categoryId, lastPostDate, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByG_C_L(
		long groupId, long categoryId, Date lastPostDate, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator) {

		return findByG_C_L(
			groupId, categoryId, lastPostDate, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @return the matching message boards threads that the user has permission to view
	 */
	public default java.util.List<MBThread> filterFindByG_C_L(
		long groupId, long categoryId, Date lastPostDate) {

		return filterFindByG_C_L(
			groupId, categoryId, lastPostDate,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads that the user has permission to view
	 */
	public default java.util.List<MBThread> filterFindByG_C_L(
		long groupId, long categoryId, Date lastPostDate, int start, int end) {

		return filterFindByG_C_L(
			groupId, categoryId, lastPostDate, start, end, null);
	}

	/**
	 * Returns all the message boards threads where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the matching message boards threads
	 */
	public default java.util.List<MBThread> findByG_C_S(
		long groupId, long categoryId, int status) {

		return findByG_C_S(
			groupId, categoryId, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the message boards threads where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByG_C_S(
		long groupId, long categoryId, int status, int start, int end) {

		return findByG_C_S(groupId, categoryId, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByG_C_S(
		long groupId, long categoryId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator) {

		return findByG_C_S(
			groupId, categoryId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the message boards threads where groupId = &#63; and categoryId = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @return the matching message boards threads
	 */
	public default java.util.List<MBThread> findByG_C_S(
		long groupId, long[] categoryIds, int status) {

		return findByG_C_S(
			groupId, categoryIds, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the message boards threads where groupId = &#63; and categoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByG_C_S(
		long groupId, long[] categoryIds, int status, int start, int end) {

		return findByG_C_S(
			groupId, categoryIds, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public default java.util.List<MBThread> findByG_C_S(
		long groupId, long[] categoryIds, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBThread>
			orderByComparator) {

		return findByG_C_S(
			groupId, categoryIds, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the matching message boards threads that the user has permission to view
	 */
	public default java.util.List<MBThread> filterFindByG_C_S(
		long groupId, long categoryId, int status) {

		return filterFindByG_C_S(
			groupId, categoryId, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads that the user has permission to view
	 */
	public default java.util.List<MBThread> filterFindByG_C_S(
		long groupId, long categoryId, int status, int start, int end) {

		return filterFindByG_C_S(groupId, categoryId, status, start, end, null);
	}

	/**
	 * Returns all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @return the matching message boards threads that the user has permission to view
	 */
	public default java.util.List<MBThread> filterFindByG_C_S(
		long groupId, long[] categoryIds, int status) {

		return filterFindByG_C_S(
			groupId, categoryIds, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads that the user has permission to view
	 */
	public default java.util.List<MBThread> filterFindByG_C_S(
		long groupId, long[] categoryIds, int status, int start, int end) {

		return filterFindByG_C_S(
			groupId, categoryIds, status, start, end, null);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:288004819