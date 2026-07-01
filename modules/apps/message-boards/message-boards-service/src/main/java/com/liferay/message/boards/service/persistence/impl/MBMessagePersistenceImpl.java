/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.impl;

import com.liferay.message.boards.exception.DuplicateMBMessageExternalReferenceCodeException;
import com.liferay.message.boards.exception.NoSuchMessageException;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageTable;
import com.liferay.message.boards.model.impl.MBMessageImpl;
import com.liferay.message.boards.model.impl.MBMessageModelImpl;
import com.liferay.message.boards.service.persistence.MBMessagePersistence;
import com.liferay.message.boards.service.persistence.MBMessageUtil;
import com.liferay.message.boards.service.persistence.impl.constants.MBPersistenceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the message-boards message service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = MBMessagePersistence.class)
public class MBMessagePersistenceImpl
	extends BasePersistenceImpl<MBMessage, NoSuchMessageException>
	implements MBMessagePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>MBMessageUtil</code> to access the message-boards message persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		MBMessageImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the message-boards messages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByUuid_First(
			String uuid, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByUuid_First(
		String uuid, OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of message-boards messages where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<MBMessage, NoSuchMessageException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the message-boards message where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchMessageException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByUUID_G(String uuid, long groupId)
		throws NoSuchMessageException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the message-boards message where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the message-boards message where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the message-boards message that was removed
	 */
	@Override
	public MBMessage removeByUUID_G(String uuid, long groupId)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByUUID_G(uuid, groupId);

		return remove(mbMessage);
	}

	/**
	 * Returns the number of message-boards messages where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the message-boards messages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of message-boards messages where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByGroupId_First(
			long groupId, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByGroupId_First(
		long groupId, OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupId}, groupId);
	}

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the message-boards messages where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByCompanyId_First(
			long companyId, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByCompanyId_First(
		long companyId, OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of message-boards messages where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByUserId(
		long userId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			finderCache, new Object[] {userId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByUserId_First(
			long userId, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByUserId.findFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByUserId_First(
		long userId, OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the number of message-boards messages where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			finderCache, new Object[] {userId});
	}

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByThreadId;

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByThreadId(
		long threadId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByThreadId.find(
			finderCache, new Object[] {threadId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByThreadId_First(
			long threadId, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByThreadId.findFirst(
			finderCache, new Object[] {threadId}, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByThreadId_First(
		long threadId, OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByThreadId.fetchFirst(
			finderCache, new Object[] {threadId}, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where threadId = &#63; from the database.
	 *
	 * @param threadId the thread ID
	 */
	@Override
	public void removeByThreadId(long threadId) {
		_collectionPersistenceFinderByThreadId.remove(
			finderCache, new Object[] {threadId});
	}

	/**
	 * Returns the number of message-boards messages where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByThreadId(long threadId) {
		return _collectionPersistenceFinderByThreadId.count(
			finderCache, new Object[] {threadId});
	}

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByThreadIdReplies;

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByThreadIdReplies(
		long threadId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByThreadIdReplies.find(
			finderCache, new Object[] {threadId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByThreadIdReplies_First(
			long threadId, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByThreadIdReplies.findFirst(
			finderCache, new Object[] {threadId}, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByThreadIdReplies_First(
		long threadId, OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByThreadIdReplies.fetchFirst(
			finderCache, new Object[] {threadId}, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where threadId = &#63; from the database.
	 *
	 * @param threadId the thread ID
	 */
	@Override
	public void removeByThreadIdReplies(long threadId) {
		_collectionPersistenceFinderByThreadIdReplies.remove(
			finderCache, new Object[] {threadId});
	}

	/**
	 * Returns the number of message-boards messages where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByThreadIdReplies(long threadId) {
		return _collectionPersistenceFinderByThreadIdReplies.count(
			finderCache, new Object[] {threadId});
	}

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByParentMessageId;

	/**
	 * Returns an ordered range of all the message-boards messages where parentMessageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param parentMessageId the parent message ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByParentMessageId(
		long parentMessageId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByParentMessageId.find(
			finderCache, new Object[] {parentMessageId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where parentMessageId = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByParentMessageId_First(
			long parentMessageId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByParentMessageId.findFirst(
			finderCache, new Object[] {parentMessageId}, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where parentMessageId = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByParentMessageId_First(
		long parentMessageId, OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByParentMessageId.fetchFirst(
			finderCache, new Object[] {parentMessageId}, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where parentMessageId = &#63; from the database.
	 *
	 * @param parentMessageId the parent message ID
	 */
	@Override
	public void removeByParentMessageId(long parentMessageId) {
		_collectionPersistenceFinderByParentMessageId.remove(
			finderCache, new Object[] {parentMessageId});
	}

	/**
	 * Returns the number of message-boards messages where parentMessageId = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByParentMessageId(long parentMessageId) {
		return _collectionPersistenceFinderByParentMessageId.count(
			finderCache, new Object[] {parentMessageId});
	}

	private FilterCollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByG_U;

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U.find(
			finderCache, new Object[] {groupId, userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_U_First(
			long groupId, long userId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByG_U.findFirst(
			finderCache, new Object[] {groupId, userId}, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_U_First(
		long groupId, long userId,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByG_U.fetchFirst(
			finderCache, new Object[] {groupId, userId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByG_U.filterFind(
			finderCache, new Object[] {groupId, userId}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and userId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByG_U(long groupId, long userId) {
		_collectionPersistenceFinderByG_U.remove(
			finderCache, new Object[] {groupId, userId});
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByG_U(long groupId, long userId) {
		return _collectionPersistenceFinderByG_U.count(
			finderCache, new Object[] {groupId, userId});
	}

	/**
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	@Override
	public int filterCountByG_U(long groupId, long userId) {
		return _collectionPersistenceFinderByG_U.filterCount(
			finderCache, new Object[] {groupId, userId}, groupId);
	}

	private FilterCollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByG_C;

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C(
		long groupId, long categoryId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C.find(
			finderCache, new Object[] {groupId, categoryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_C_First(
			long groupId, long categoryId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByG_C.findFirst(
			finderCache, new Object[] {groupId, categoryId}, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_C_First(
		long groupId, long categoryId,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByG_C.fetchFirst(
			finderCache, new Object[] {groupId, categoryId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C(
		long groupId, long categoryId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByG_C.filterFind(
			finderCache, new Object[] {groupId, categoryId}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and categoryId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 */
	@Override
	public void removeByG_C(long groupId, long categoryId) {
		_collectionPersistenceFinderByG_C.remove(
			finderCache, new Object[] {groupId, categoryId});
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByG_C(long groupId, long categoryId) {
		return _collectionPersistenceFinderByG_C.count(
			finderCache, new Object[] {groupId, categoryId});
	}

	/**
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	@Override
	public int filterCountByG_C(long groupId, long categoryId) {
		return _collectionPersistenceFinderByG_C.filterCount(
			finderCache, new Object[] {groupId, categoryId}, groupId);
	}

	private UniquePersistenceFinder<MBMessage, NoSuchMessageException>
		_uniquePersistenceFinderByG_US;

	/**
	 * Returns the message-boards message where groupId = &#63; and urlSubject = &#63; or throws a <code>NoSuchMessageException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param urlSubject the url subject
	 * @return the matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_US(long groupId, String urlSubject)
		throws NoSuchMessageException {

		return _uniquePersistenceFinderByG_US.find(
			finderCache, new Object[] {groupId, urlSubject});
	}

	/**
	 * Returns the message-boards message where groupId = &#63; and urlSubject = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param urlSubject the url subject
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_US(
		long groupId, String urlSubject, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_US.fetch(
			finderCache, new Object[] {groupId, urlSubject}, useFinderCache);
	}

	/**
	 * Removes the message-boards message where groupId = &#63; and urlSubject = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param urlSubject the url subject
	 * @return the message-boards message that was removed
	 */
	@Override
	public MBMessage removeByG_US(long groupId, String urlSubject)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByG_US(groupId, urlSubject);

		return remove(mbMessage);
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and urlSubject = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlSubject the url subject
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByG_US(long groupId, String urlSubject) {
		return _uniquePersistenceFinderByG_US.count(
			finderCache, new Object[] {groupId, urlSubject});
	}

	private FilterCollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByG_S;

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S.find(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_S_First(
			long groupId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByG_S.findFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByG_S.fetchFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByG_S.filterFind(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_S(long groupId, int status) {
		_collectionPersistenceFinderByG_S.remove(
			finderCache, new Object[] {groupId, status});
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByG_S(long groupId, int status) {
		return _collectionPersistenceFinderByG_S.count(
			finderCache, new Object[] {groupId, status});
	}

	/**
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	@Override
	public int filterCountByG_S(long groupId, int status) {
		return _collectionPersistenceFinderByG_S.filterCount(
			finderCache, new Object[] {groupId, status}, groupId);
	}

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByC_S;

	/**
	 * Returns an ordered range of all the message-boards messages where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S.find(
			finderCache, new Object[] {companyId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByC_S_First(
			long companyId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByC_S.findFirst(
			finderCache, new Object[] {companyId, status}, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByC_S_First(
		long companyId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByC_S.fetchFirst(
			finderCache, new Object[] {companyId, status}, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_S(long companyId, int status) {
		_collectionPersistenceFinderByC_S.remove(
			finderCache, new Object[] {companyId, status});
	}

	/**
	 * Returns the number of message-boards messages where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByC_S(long companyId, int status) {
		return _collectionPersistenceFinderByC_S.count(
			finderCache, new Object[] {companyId, status});
	}

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByU_C;

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C(
		long userId, long classNameId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_C.find(
			finderCache, new Object[] {userId, new long[] {classNameId}}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByU_C_First(
			long userId, long classNameId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByU_C_First(
			userId, classNameId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByU_C_First(
		long userId, long classNameId,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByU_C.fetchFirst(
			finderCache, new Object[] {userId, new long[] {classNameId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameIds the class name IDs
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C(
		long userId, long[] classNameIds, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_C.find(
			finderCache,
			new Object[] {userId, ArrayUtil.sortedUnique(classNameIds)}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the message-boards messages where userId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByU_C(long userId, long classNameId) {
		_collectionPersistenceFinderByU_C.remove(
			finderCache, new Object[] {userId, new long[] {classNameId}});
	}

	/**
	 * Returns the number of message-boards messages where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByU_C(long userId, long classNameId) {
		return _collectionPersistenceFinderByU_C.count(
			finderCache, new Object[] {userId, new long[] {classNameId}});
	}

	/**
	 * Returns the number of message-boards messages where userId = &#63; and classNameId = any &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameIds the class name IDs
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByU_C(long userId, long[] classNameIds) {
		return _collectionPersistenceFinderByU_C.count(
			finderCache,
			new Object[] {userId, ArrayUtil.sortedUnique(classNameIds)});
	}

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the message-boards messages where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C(long classNameId, long classPK) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the number of message-boards messages where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByT_P;

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_P(
		long threadId, long parentMessageId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByT_P.find(
			finderCache, new Object[] {threadId, parentMessageId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByT_P_First(
			long threadId, long parentMessageId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByT_P.findFirst(
			finderCache, new Object[] {threadId, parentMessageId},
			orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByT_P_First(
		long threadId, long parentMessageId,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByT_P.fetchFirst(
			finderCache, new Object[] {threadId, parentMessageId},
			orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where threadId = &#63; and parentMessageId = &#63; from the database.
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 */
	@Override
	public void removeByT_P(long threadId, long parentMessageId) {
		_collectionPersistenceFinderByT_P.remove(
			finderCache, new Object[] {threadId, parentMessageId});
	}

	/**
	 * Returns the number of message-boards messages where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByT_P(long threadId, long parentMessageId) {
		return _collectionPersistenceFinderByT_P.count(
			finderCache, new Object[] {threadId, parentMessageId});
	}

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByT_A;

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and answer = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_A(
		long threadId, boolean answer, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByT_A.find(
			finderCache, new Object[] {threadId, answer}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and answer = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByT_A_First(
			long threadId, boolean answer,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByT_A.findFirst(
			finderCache, new Object[] {threadId, answer}, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and answer = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByT_A_First(
		long threadId, boolean answer,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByT_A.fetchFirst(
			finderCache, new Object[] {threadId, answer}, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where threadId = &#63; and answer = &#63; from the database.
	 *
	 * @param threadId the thread ID
	 * @param answer the answer
	 */
	@Override
	public void removeByT_A(long threadId, boolean answer) {
		_collectionPersistenceFinderByT_A.remove(
			finderCache, new Object[] {threadId, answer});
	}

	/**
	 * Returns the number of message-boards messages where threadId = &#63; and answer = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByT_A(long threadId, boolean answer) {
		return _collectionPersistenceFinderByT_A.count(
			finderCache, new Object[] {threadId, answer});
	}

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByT_S;

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_S(
		long threadId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByT_S.find(
			finderCache, new Object[] {threadId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByT_S_First(
			long threadId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByT_S.findFirst(
			finderCache, new Object[] {threadId, status}, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByT_S_First(
		long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByT_S.fetchFirst(
			finderCache, new Object[] {threadId, status}, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where threadId = &#63; and status = &#63; from the database.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 */
	@Override
	public void removeByT_S(long threadId, int status) {
		_collectionPersistenceFinderByT_S.remove(
			finderCache, new Object[] {threadId, status});
	}

	/**
	 * Returns the number of message-boards messages where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByT_S(long threadId, int status) {
		return _collectionPersistenceFinderByT_S.count(
			finderCache, new Object[] {threadId, status});
	}

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByT_NotS;

	/**
	 * Returns all the message-boards messages where threadId = &#63; and status &ne; &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_NotS(long threadId, int status) {
		return findByT_NotS(
			threadId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where threadId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_NotS(
		long threadId, int status, int start, int end) {

		return findByT_NotS(threadId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_NotS(
		long threadId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByT_NotS(
			threadId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_NotS(
		long threadId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByT_NotS.find(
			finderCache, new Object[] {threadId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and status &ne; &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByT_NotS_First(
			long threadId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByT_NotS.findFirst(
			finderCache, new Object[] {threadId, status}, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and status &ne; &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByT_NotS_First(
		long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByT_NotS.fetchFirst(
			finderCache, new Object[] {threadId, status}, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where threadId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 */
	@Override
	public void removeByT_NotS(long threadId, int status) {
		_collectionPersistenceFinderByT_NotS.remove(
			finderCache, new Object[] {threadId, status});
	}

	/**
	 * Returns the number of message-boards messages where threadId = &#63; and status &ne; &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByT_NotS(long threadId, int status) {
		return _collectionPersistenceFinderByT_NotS.count(
			finderCache, new Object[] {threadId, status});
	}

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByTR_S;

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByTR_S(
		long threadId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByTR_S.find(
			finderCache, new Object[] {threadId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByTR_S_First(
			long threadId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByTR_S.findFirst(
			finderCache, new Object[] {threadId, status}, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByTR_S_First(
		long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByTR_S.fetchFirst(
			finderCache, new Object[] {threadId, status}, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where threadId = &#63; and status = &#63; from the database.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 */
	@Override
	public void removeByTR_S(long threadId, int status) {
		_collectionPersistenceFinderByTR_S.remove(
			finderCache, new Object[] {threadId, status});
	}

	/**
	 * Returns the number of message-boards messages where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByTR_S(long threadId, int status) {
		return _collectionPersistenceFinderByTR_S.count(
			finderCache, new Object[] {threadId, status});
	}

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByP_S;

	/**
	 * Returns an ordered range of all the message-boards messages where parentMessageId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByP_S(
		long parentMessageId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_S.find(
			finderCache, new Object[] {parentMessageId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where parentMessageId = &#63; and status = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByP_S_First(
			long parentMessageId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByP_S.findFirst(
			finderCache, new Object[] {parentMessageId, status},
			orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where parentMessageId = &#63; and status = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByP_S_First(
		long parentMessageId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByP_S.fetchFirst(
			finderCache, new Object[] {parentMessageId, status},
			orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where parentMessageId = &#63; and status = &#63; from the database.
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 */
	@Override
	public void removeByP_S(long parentMessageId, int status) {
		_collectionPersistenceFinderByP_S.remove(
			finderCache, new Object[] {parentMessageId, status});
	}

	/**
	 * Returns the number of message-boards messages where parentMessageId = &#63; and status = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByP_S(long parentMessageId, int status) {
		return _collectionPersistenceFinderByP_S.count(
			finderCache, new Object[] {parentMessageId, status});
	}

	private FilterCollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByG_U_S;

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U_S.find(
			finderCache, new Object[] {groupId, userId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_U_S_First(
			long groupId, long userId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByG_U_S.findFirst(
			finderCache, new Object[] {groupId, userId, status},
			orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_U_S_First(
		long groupId, long userId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByG_U_S.fetchFirst(
			finderCache, new Object[] {groupId, userId, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByG_U_S.filterFind(
			finderCache, new Object[] {groupId, userId, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and userId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 */
	@Override
	public void removeByG_U_S(long groupId, long userId, int status) {
		_collectionPersistenceFinderByG_U_S.remove(
			finderCache, new Object[] {groupId, userId, status});
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByG_U_S(long groupId, long userId, int status) {
		return _collectionPersistenceFinderByG_U_S.count(
			finderCache, new Object[] {groupId, userId, status});
	}

	/**
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_S(long groupId, long userId, int status) {
		return _collectionPersistenceFinderByG_U_S.filterCount(
			finderCache, new Object[] {groupId, userId, status}, groupId);
	}

	private FilterCollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByG_C_T;

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_T(
		long groupId, long categoryId, long threadId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_T.find(
			finderCache, new Object[] {groupId, categoryId, threadId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_C_T_First(
			long groupId, long categoryId, long threadId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByG_C_T.findFirst(
			finderCache, new Object[] {groupId, categoryId, threadId},
			orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_C_T_First(
		long groupId, long categoryId, long threadId,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByG_C_T.fetchFirst(
			finderCache, new Object[] {groupId, categoryId, threadId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C_T(
		long groupId, long categoryId, long threadId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByG_C_T.filterFind(
			finderCache, new Object[] {groupId, categoryId, threadId}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 */
	@Override
	public void removeByG_C_T(long groupId, long categoryId, long threadId) {
		_collectionPersistenceFinderByG_C_T.remove(
			finderCache, new Object[] {groupId, categoryId, threadId});
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByG_C_T(long groupId, long categoryId, long threadId) {
		return _collectionPersistenceFinderByG_C_T.count(
			finderCache, new Object[] {groupId, categoryId, threadId});
	}

	/**
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_T(
		long groupId, long categoryId, long threadId) {

		return _collectionPersistenceFinderByG_C_T.filterCount(
			finderCache, new Object[] {groupId, categoryId, threadId}, groupId);
	}

	private FilterCollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByG_C_S;

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_S(
		long groupId, long categoryId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_S.find(
			finderCache, new Object[] {groupId, categoryId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_C_S_First(
			long groupId, long categoryId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByG_C_S.findFirst(
			finderCache, new Object[] {groupId, categoryId, status},
			orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_C_S_First(
		long groupId, long categoryId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByG_C_S.fetchFirst(
			finderCache, new Object[] {groupId, categoryId, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C_S(
		long groupId, long categoryId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByG_C_S.filterFind(
			finderCache, new Object[] {groupId, categoryId, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and categoryId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 */
	@Override
	public void removeByG_C_S(long groupId, long categoryId, int status) {
		_collectionPersistenceFinderByG_C_S.remove(
			finderCache, new Object[] {groupId, categoryId, status});
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByG_C_S(long groupId, long categoryId, int status) {
		return _collectionPersistenceFinderByG_C_S.count(
			finderCache, new Object[] {groupId, categoryId, status});
	}

	/**
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_S(long groupId, long categoryId, int status) {
		return _collectionPersistenceFinderByG_C_S.filterCount(
			finderCache, new Object[] {groupId, categoryId, status}, groupId);
	}

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByU_C_C;

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_C(
		long userId, long classNameId, long classPK, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_C_C.find(
			finderCache, new Object[] {userId, classNameId, classPK}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByU_C_C_First(
			long userId, long classNameId, long classPK,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByU_C_C.findFirst(
			finderCache, new Object[] {userId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByU_C_C_First(
		long userId, long classNameId, long classPK,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByU_C_C.fetchFirst(
			finderCache, new Object[] {userId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByU_C_C(long userId, long classNameId, long classPK) {
		_collectionPersistenceFinderByU_C_C.remove(
			finderCache, new Object[] {userId, classNameId, classPK});
	}

	/**
	 * Returns the number of message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByU_C_C(long userId, long classNameId, long classPK) {
		return _collectionPersistenceFinderByU_C_C.count(
			finderCache, new Object[] {userId, classNameId, classPK});
	}

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByU_C_S;

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_S(
		long userId, long classNameId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_C_S.find(
			finderCache,
			new Object[] {userId, new long[] {classNameId}, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByU_C_S_First(
			long userId, long classNameId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByU_C_S_First(
			userId, classNameId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByU_C_S_First(
		long userId, long classNameId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByU_C_S.fetchFirst(
			finderCache,
			new Object[] {userId, new long[] {classNameId}, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameIds the class name IDs
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_S(
		long userId, long[] classNameIds, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_C_S.find(
			finderCache,
			new Object[] {userId, ArrayUtil.sortedUnique(classNameIds), status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the message-boards messages where userId = &#63; and classNameId = &#63; and status = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param status the status
	 */
	@Override
	public void removeByU_C_S(long userId, long classNameId, int status) {
		_collectionPersistenceFinderByU_C_S.remove(
			finderCache,
			new Object[] {userId, new long[] {classNameId}, status});
	}

	/**
	 * Returns the number of message-boards messages where userId = &#63; and classNameId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByU_C_S(long userId, long classNameId, int status) {
		return _collectionPersistenceFinderByU_C_S.count(
			finderCache,
			new Object[] {userId, new long[] {classNameId}, status});
	}

	/**
	 * Returns the number of message-boards messages where userId = &#63; and classNameId = any &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameIds the class name IDs
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByU_C_S(long userId, long[] classNameIds, int status) {
		return _collectionPersistenceFinderByU_C_S.count(
			finderCache,
			new Object[] {
				userId, ArrayUtil.sortedUnique(classNameIds), status
			});
	}

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByC_C_S;

	/**
	 * Returns an ordered range of all the message-boards messages where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByC_C_S(
		long classNameId, long classPK, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_S.find(
			finderCache, new Object[] {classNameId, classPK, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByC_C_S_First(
			long classNameId, long classPK, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByC_C_S.findFirst(
			finderCache, new Object[] {classNameId, classPK, status},
			orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByC_C_S_First(
		long classNameId, long classPK, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByC_C_S.fetchFirst(
			finderCache, new Object[] {classNameId, classPK, status},
			orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where classNameId = &#63; and classPK = &#63; and status = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 */
	@Override
	public void removeByC_C_S(long classNameId, long classPK, int status) {
		_collectionPersistenceFinderByC_C_S.remove(
			finderCache, new Object[] {classNameId, classPK, status});
	}

	/**
	 * Returns the number of message-boards messages where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByC_C_S(long classNameId, long classPK, int status) {
		return _collectionPersistenceFinderByC_C_S.count(
			finderCache, new Object[] {classNameId, classPK, status});
	}

	private FilterCollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByG_C_T_A;

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer, int start,
		int end, OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_T_A.find(
			finderCache, new Object[] {groupId, categoryId, threadId, answer},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_C_T_A_First(
			long groupId, long categoryId, long threadId, boolean answer,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByG_C_T_A.findFirst(
			finderCache, new Object[] {groupId, categoryId, threadId, answer},
			orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_C_T_A_First(
		long groupId, long categoryId, long threadId, boolean answer,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByG_C_T_A.fetchFirst(
			finderCache, new Object[] {groupId, categoryId, threadId, answer},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer, int start,
		int end, OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByG_C_T_A.filterFind(
			finderCache, new Object[] {groupId, categoryId, threadId, answer},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 */
	@Override
	public void removeByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer) {

		_collectionPersistenceFinderByG_C_T_A.remove(
			finderCache, new Object[] {groupId, categoryId, threadId, answer});
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer) {

		return _collectionPersistenceFinderByG_C_T_A.count(
			finderCache, new Object[] {groupId, categoryId, threadId, answer});
	}

	/**
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer) {

		return _collectionPersistenceFinderByG_C_T_A.filterCount(
			finderCache, new Object[] {groupId, categoryId, threadId, answer},
			groupId);
	}

	private FilterCollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByG_C_T_S;

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_T_S(
		long groupId, long categoryId, long threadId, int status, int start,
		int end, OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_T_S.find(
			finderCache, new Object[] {groupId, categoryId, threadId, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_C_T_S_First(
			long groupId, long categoryId, long threadId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByG_C_T_S.findFirst(
			finderCache, new Object[] {groupId, categoryId, threadId, status},
			orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_C_T_S_First(
		long groupId, long categoryId, long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByG_C_T_S.fetchFirst(
			finderCache, new Object[] {groupId, categoryId, threadId, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C_T_S(
		long groupId, long categoryId, long threadId, int status, int start,
		int end, OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByG_C_T_S.filterFind(
			finderCache, new Object[] {groupId, categoryId, threadId, status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 */
	@Override
	public void removeByG_C_T_S(
		long groupId, long categoryId, long threadId, int status) {

		_collectionPersistenceFinderByG_C_T_S.remove(
			finderCache, new Object[] {groupId, categoryId, threadId, status});
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByG_C_T_S(
		long groupId, long categoryId, long threadId, int status) {

		return _collectionPersistenceFinderByG_C_T_S.count(
			finderCache, new Object[] {groupId, categoryId, threadId, status});
	}

	/**
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_T_S(
		long groupId, long categoryId, long threadId, int status) {

		return _collectionPersistenceFinderByG_C_T_S.filterCount(
			finderCache, new Object[] {groupId, categoryId, threadId, status},
			groupId);
	}

	private CollectionPersistenceFinder<MBMessage, NoSuchMessageException>
		_collectionPersistenceFinderByU_C_C_S;

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_C_S(
		long userId, long classNameId, long classPK, int status, int start,
		int end, OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_C_C_S.find(
			finderCache, new Object[] {userId, classNameId, classPK, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByU_C_C_S_First(
			long userId, long classNameId, long classPK, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		return _collectionPersistenceFinderByU_C_C_S.findFirst(
			finderCache, new Object[] {userId, classNameId, classPK, status},
			orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByU_C_C_S_First(
		long userId, long classNameId, long classPK, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return _collectionPersistenceFinderByU_C_C_S.fetchFirst(
			finderCache, new Object[] {userId, classNameId, classPK, status},
			orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 */
	@Override
	public void removeByU_C_C_S(
		long userId, long classNameId, long classPK, int status) {

		_collectionPersistenceFinderByU_C_C_S.remove(
			finderCache, new Object[] {userId, classNameId, classPK, status});
	}

	/**
	 * Returns the number of message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByU_C_C_S(
		long userId, long classNameId, long classPK, int status) {

		return _collectionPersistenceFinderByU_C_C_S.count(
			finderCache, new Object[] {userId, classNameId, classPK, status});
	}

	private UniquePersistenceFinder<MBMessage, NoSuchMessageException>
		_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the message-boards message where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchMessageException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchMessageException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the message-boards message where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the message-boards message where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the message-boards message that was removed
	 */
	@Override
	public MBMessage removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByERC_G(externalReferenceCode, groupId);

		return remove(mbMessage);
	}

	/**
	 * Returns the number of message-boards messages where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public MBMessagePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(MBMessage.class);

		setModelImplClass(MBMessageImpl.class);
		setModelPKClass(long.class);

		setTable(MBMessageTable.INSTANCE);
	}

	/**
	 * Creates a new message-boards message with the primary key. Does not add the message-boards message to the database.
	 *
	 * @param messageId the primary key for the new message-boards message
	 * @return the new message-boards message
	 */
	@Override
	public MBMessage create(long messageId) {
		MBMessage mbMessage = new MBMessageImpl();

		mbMessage.setNew(true);
		mbMessage.setPrimaryKey(messageId);

		String uuid = PortalUUIDUtil.generate();

		mbMessage.setUuid(uuid);

		mbMessage.setCompanyId(CompanyThreadLocal.getCompanyId());

		return mbMessage;
	}

	/**
	 * Removes the message-boards message with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param messageId the primary key of the message-boards message
	 * @return the message-boards message that was removed
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage remove(long messageId) throws NoSuchMessageException {
		return remove((Serializable)messageId);
	}

	@Override
	protected MBMessage removeImpl(MBMessage mbMessage) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(mbMessage)) {
				mbMessage = (MBMessage)session.get(
					MBMessageImpl.class, mbMessage.getPrimaryKeyObj());
			}

			if ((mbMessage != null) &&
				ctPersistenceHelper.isRemove(mbMessage)) {

				session.delete(mbMessage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (mbMessage != null) {
			clearCache(mbMessage);
		}

		return mbMessage;
	}

	@Override
	public MBMessage updateImpl(MBMessage mbMessage) {
		boolean isNew = mbMessage.isNew();

		if (!(mbMessage instanceof MBMessageModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(mbMessage.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(mbMessage);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in mbMessage proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom MBMessage implementation " +
					mbMessage.getClass());
		}

		MBMessageModelImpl mbMessageModelImpl = (MBMessageModelImpl)mbMessage;

		if (Validator.isNull(mbMessage.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			mbMessage.setUuid(uuid);
		}

		if (Validator.isNull(mbMessage.getExternalReferenceCode())) {
			mbMessage.setExternalReferenceCode(mbMessage.getUuid());
		}
		else {
			if (!Objects.equals(
					mbMessageModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					mbMessage.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = mbMessage.getCompanyId();

					long groupId = mbMessage.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = mbMessage.getPrimaryKey();
					}

					try {
						mbMessage.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								MBMessage.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								mbMessage.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			MBMessage ercMBMessage = fetchByERC_G(
				mbMessage.getExternalReferenceCode(), mbMessage.getGroupId());

			if (isNew) {
				if (ercMBMessage != null) {
					throw new DuplicateMBMessageExternalReferenceCodeException(
						"Duplicate message-boards message with external reference code " +
							mbMessage.getExternalReferenceCode() +
								" and group " + mbMessage.getGroupId());
				}
			}
			else {
				if ((ercMBMessage != null) &&
					(mbMessage.getMessageId() != ercMBMessage.getMessageId())) {

					throw new DuplicateMBMessageExternalReferenceCodeException(
						"Duplicate message-boards message with external reference code " +
							mbMessage.getExternalReferenceCode() +
								" and group " + mbMessage.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (mbMessage.getCreateDate() == null)) {
			if (serviceContext == null) {
				mbMessage.setCreateDate(date);
			}
			else {
				mbMessage.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!mbMessageModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				mbMessage.setModifiedDate(date);
			}
			else {
				mbMessage.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		long userId = GetterUtil.getLong(PrincipalThreadLocal.getName());

		if (userId > 0) {
			long companyId = mbMessage.getCompanyId();

			long groupId = mbMessage.getGroupId();

			long messageId = 0;

			if (!isNew) {
				messageId = mbMessage.getPrimaryKey();
			}

			try {
				mbMessage.setSubject(
					SanitizerUtil.sanitize(
						companyId, groupId, userId, MBMessage.class.getName(),
						messageId, ContentTypes.TEXT_PLAIN, Sanitizer.MODE_ALL,
						mbMessage.getSubject(), null));
			}
			catch (SanitizerException sanitizerException) {
				throw new SystemException(sanitizerException);
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(mbMessage)) {
				if (!isNew) {
					session.evict(
						MBMessageImpl.class, mbMessage.getPrimaryKeyObj());
				}

				session.save(mbMessage);
			}
			else {
				mbMessage = (MBMessage)session.merge(mbMessage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(mbMessage, false);

		if (isNew) {
			mbMessage.setNew(false);
		}

		mbMessage.resetOriginalValues();

		return mbMessage;
	}

	/**
	 * Returns the message-boards message with the primary key or throws a <code>NoSuchMessageException</code> if it could not be found.
	 *
	 * @param messageId the primary key of the message-boards message
	 * @return the message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage findByPrimaryKey(long messageId)
		throws NoSuchMessageException {

		return findByPrimaryKey((Serializable)messageId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the message-boards message with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param messageId the primary key of the message-boards message
	 * @return the message-boards message, or <code>null</code> if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage fetchByPrimaryKey(long messageId) {
		return fetchByPrimaryKey((Serializable)messageId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "messageId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_MBMESSAGE;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return MBMessageModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "MBMessage";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("categoryId");
		ctMergeColumnNames.add("threadId");
		ctMergeColumnNames.add("rootMessageId");
		ctMergeColumnNames.add("parentMessageId");
		ctMergeColumnNames.add("treePath");
		ctMergeColumnNames.add("subject");
		ctMergeColumnNames.add("urlSubject");
		ctMergeColumnNames.add("body");
		ctMergeColumnNames.add("format");
		ctMergeColumnNames.add("anonymous");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("allowPingbacks");
		ctMergeColumnNames.add("answer");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("messageId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"groupId", "urlSubject"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the message-boards message persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
			MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"mbMessage.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, MBMessage::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(MBMessage::getUuid), MBMessage::getGroupId),
			_SQL_SELECT_MBMESSAGE_WHERE, "",
			new FinderColumn<>(
				"mbMessage.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, MBMessage::getUuid),
			new FinderColumn<>(
				"mbMessage.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, MBMessage::getGroupId));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
				MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"mbMessage.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, MBMessage::getUuid),
				new FinderColumn<>(
					"mbMessage.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, MBMessage::getCompanyId));

		_collectionPersistenceFinderByGroupId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
				MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"mbMessage.categoryId != -1", "mbMessage.categoryId != -1",
				new FinderColumn<>(
					"mbMessage.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getGroupId));

		_collectionPersistenceFinderByCompanyId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, false),
				_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
				MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"mbMessage.categoryId != -1", "mbMessage.categoryId != -1",
				new FinderColumn<>(
					"mbMessage.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, MBMessage::getCompanyId));

		_collectionPersistenceFinderByUserId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, false),
				_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
				MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"mbMessage.categoryId != -1", "mbMessage.categoryId != -1",
				new FinderColumn<>(
					"mbMessage.", "userId", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getUserId));

		_collectionPersistenceFinderByThreadId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByThreadId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"threadId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByThreadId",
					new String[] {Long.class.getName()},
					new String[] {"threadId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByThreadId", new String[] {Long.class.getName()},
					new String[] {"threadId"}, false),
				_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
				MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"mbMessage.", "threadId", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getThreadId));

		_collectionPersistenceFinderByThreadIdReplies =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByThreadIdReplies",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"threadId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByThreadIdReplies",
					new String[] {Long.class.getName()},
					new String[] {"threadId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByThreadIdReplies",
					new String[] {Long.class.getName()},
					new String[] {"threadId"}, false),
				_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
				MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"mbMessage.parentMessageId != 0",
				"mbMessage.parentMessageId != 0",
				new FinderColumn<>(
					"mbMessage.", "threadId", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getThreadId));

		_collectionPersistenceFinderByParentMessageId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByParentMessageId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"parentMessageId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByParentMessageId",
					new String[] {Long.class.getName()},
					new String[] {"parentMessageId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByParentMessageId",
					new String[] {Long.class.getName()},
					new String[] {"parentMessageId"}, false),
				_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
				MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"mbMessage.", "parentMessageId", FinderColumn.Type.LONG,
					"=", true, true, MBMessage::getParentMessageId));

		_collectionPersistenceFinderByG_U =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "userId"}, false),
				_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
				MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"(mbMessage.categoryId != -1) AND (mbMessage.anonymous = [$FALSE$])",
				"(mbMessage.categoryId != -1) AND (mbMessage.anonymous = [$FALSE$])",
				new FinderColumn<>(
					"mbMessage.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getGroupId),
				new FinderColumn<>(
					"mbMessage.", "userId", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getUserId));

		_collectionPersistenceFinderByG_C =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "categoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "categoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "categoryId"}, false),
				_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
				MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"mbMessage.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getGroupId),
				new FinderColumn<>(
					"mbMessage.", "categoryId", FinderColumn.Type.LONG, "=",
					true, true, MBMessage::getCategoryId));

		_uniquePersistenceFinderByG_US = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_US",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "urlSubject"}, 0, 2, false,
				MBMessage::getGroupId,
				convertNullFunction(MBMessage::getUrlSubject)),
			_SQL_SELECT_MBMESSAGE_WHERE, "",
			new FinderColumn<>(
				"mbMessage.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, MBMessage::getGroupId),
			new FinderColumn<>(
				"mbMessage.", "urlSubject", FinderColumn.Type.STRING, "=", true,
				true, MBMessage::getUrlSubject));

		_collectionPersistenceFinderByG_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_S",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_S",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_S",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "status"}, false),
				_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
				MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"mbMessage.categoryId != -1", "mbMessage.categoryId != -1",
				new FinderColumn<>(
					"mbMessage.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getGroupId),
				new FinderColumn<>(
					"mbMessage.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, MBMessage::getStatus));

		_collectionPersistenceFinderByC_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"companyId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"companyId", "status"}, false),
			_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
			MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"mbMessage.categoryId != -1", "mbMessage.categoryId != -1",
			new FinderColumn<>(
				"mbMessage.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, MBMessage::getCompanyId),
			new FinderColumn<>(
				"mbMessage.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, MBMessage::getStatus));

		_collectionPersistenceFinderByU_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"userId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByU_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId", "classNameId"}, false),
			_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
			MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"mbMessage.", "userId", FinderColumn.Type.LONG, "=", true, true,
				MBMessage::getUserId),
			new ArrayableFinderColumn<>(
				"mbMessage.", "classNameId", FinderColumn.Type.LONG, "=", false,
				true, true, MBMessage::getClassNameId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, false),
			_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
			MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"mbMessage.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, MBMessage::getClassNameId),
			new FinderColumn<>(
				"mbMessage.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, MBMessage::getClassPK));

		_collectionPersistenceFinderByT_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"threadId", "parentMessageId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_P",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"threadId", "parentMessageId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByT_P",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"threadId", "parentMessageId"}, false),
			_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
			MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"mbMessage.", "threadId", FinderColumn.Type.LONG, "=", true,
				true, MBMessage::getThreadId),
			new FinderColumn<>(
				"mbMessage.", "parentMessageId", FinderColumn.Type.LONG, "=",
				true, true, MBMessage::getParentMessageId));

		_collectionPersistenceFinderByT_A = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_A",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"threadId", "answer"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_A",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"threadId", "answer"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByT_A",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"threadId", "answer"}, false),
			_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
			MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"mbMessage.", "threadId", FinderColumn.Type.LONG, "=", true,
				true, MBMessage::getThreadId),
			new FinderColumn<>(
				"mbMessage.", "answer", FinderColumn.Type.BOOLEAN, "=", true,
				true, MBMessage::isAnswer));

		_collectionPersistenceFinderByT_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"threadId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"threadId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByT_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"threadId", "status"}, false),
			_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
			MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"mbMessage.", "threadId", FinderColumn.Type.LONG, "=", true,
				true, MBMessage::getThreadId),
			new FinderColumn<>(
				"mbMessage.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, MBMessage::getStatus));

		_collectionPersistenceFinderByT_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_NotS",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"threadId", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByT_NotS",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"threadId", "status"}, false),
				_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
				MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"mbMessage.", "threadId", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getThreadId),
				new FinderColumn<>(
					"mbMessage.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, MBMessage::getStatus));

		_collectionPersistenceFinderByTR_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByTR_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"threadId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByTR_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"threadId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByTR_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"threadId", "status"}, false),
			_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
			MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"mbMessage.parentMessageId != 0", "mbMessage.parentMessageId != 0",
			new FinderColumn<>(
				"mbMessage.", "threadId", FinderColumn.Type.LONG, "=", true,
				true, MBMessage::getThreadId),
			new FinderColumn<>(
				"mbMessage.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, MBMessage::getStatus));

		_collectionPersistenceFinderByP_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"parentMessageId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"parentMessageId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"parentMessageId", "status"}, false),
			_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
			MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"mbMessage.", "parentMessageId", FinderColumn.Type.LONG, "=",
				true, true, MBMessage::getParentMessageId),
			new FinderColumn<>(
				"mbMessage.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, MBMessage::getStatus));

		_collectionPersistenceFinderByG_U_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "userId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "userId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "userId", "status"}, false),
				_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
				MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"(mbMessage.categoryId != -1) AND (mbMessage.anonymous = [$FALSE$])",
				"(mbMessage.categoryId != -1) AND (mbMessage.anonymous = [$FALSE$])",
				new FinderColumn<>(
					"mbMessage.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getGroupId),
				new FinderColumn<>(
					"mbMessage.", "userId", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getUserId),
				new FinderColumn<>(
					"mbMessage.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, MBMessage::getStatus));

		_collectionPersistenceFinderByG_C_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "categoryId", "threadId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "categoryId", "threadId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "categoryId", "threadId"}, false),
				_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
				MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"mbMessage.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getGroupId),
				new FinderColumn<>(
					"mbMessage.", "categoryId", FinderColumn.Type.LONG, "=",
					true, true, MBMessage::getCategoryId),
				new FinderColumn<>(
					"mbMessage.", "threadId", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getThreadId));

		_collectionPersistenceFinderByG_C_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "categoryId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "categoryId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "categoryId", "status"}, false),
				_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
				MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"mbMessage.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getGroupId),
				new FinderColumn<>(
					"mbMessage.", "categoryId", FinderColumn.Type.LONG, "=",
					true, true, MBMessage::getCategoryId),
				new FinderColumn<>(
					"mbMessage.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, MBMessage::getStatus));

		_collectionPersistenceFinderByU_C_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"userId", "classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"userId", "classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"userId", "classNameId", "classPK"}, false),
			_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
			MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"mbMessage.", "userId", FinderColumn.Type.LONG, "=", true, true,
				MBMessage::getUserId),
			new FinderColumn<>(
				"mbMessage.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, MBMessage::getClassNameId),
			new FinderColumn<>(
				"mbMessage.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, MBMessage::getClassPK));

		_collectionPersistenceFinderByU_C_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_C_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"userId", "classNameId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_C_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"userId", "classNameId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByU_C_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"userId", "classNameId", "status"}, false),
			_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
			MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"mbMessage.", "userId", FinderColumn.Type.LONG, "=", true, true,
				MBMessage::getUserId),
			new ArrayableFinderColumn<>(
				"mbMessage.", "classNameId", FinderColumn.Type.LONG, "=", false,
				true, true, MBMessage::getClassNameId),
			new FinderColumn<>(
				"mbMessage.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, MBMessage::getStatus));

		_collectionPersistenceFinderByC_C_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"classNameId", "classPK", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"classNameId", "classPK", "status"}, false),
			_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
			MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"mbMessage.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, MBMessage::getClassNameId),
			new FinderColumn<>(
				"mbMessage.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, MBMessage::getClassPK),
			new FinderColumn<>(
				"mbMessage.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, MBMessage::getStatus));

		_collectionPersistenceFinderByG_C_T_A =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_T_A",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "categoryId", "threadId", "answer"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_T_A",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"groupId", "categoryId", "threadId", "answer"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_T_A",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"groupId", "categoryId", "threadId", "answer"
					},
					false),
				_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
				MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"mbMessage.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getGroupId),
				new FinderColumn<>(
					"mbMessage.", "categoryId", FinderColumn.Type.LONG, "=",
					true, true, MBMessage::getCategoryId),
				new FinderColumn<>(
					"mbMessage.", "threadId", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getThreadId),
				new FinderColumn<>(
					"mbMessage.", "answer", FinderColumn.Type.BOOLEAN, "=",
					true, true, MBMessage::isAnswer));

		_collectionPersistenceFinderByG_C_T_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_T_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "categoryId", "threadId", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_T_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "categoryId", "threadId", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_T_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "categoryId", "threadId", "status"
					},
					false),
				_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
				MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"mbMessage.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getGroupId),
				new FinderColumn<>(
					"mbMessage.", "categoryId", FinderColumn.Type.LONG, "=",
					true, true, MBMessage::getCategoryId),
				new FinderColumn<>(
					"mbMessage.", "threadId", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getThreadId),
				new FinderColumn<>(
					"mbMessage.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, MBMessage::getStatus));

		_collectionPersistenceFinderByU_C_C_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_C_C_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId", "classNameId", "classPK", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_C_C_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"userId", "classNameId", "classPK", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_C_C_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"userId", "classNameId", "classPK", "status"},
					false),
				_SQL_SELECT_MBMESSAGE_WHERE, _SQL_COUNT_MBMESSAGE_WHERE,
				MBMessageModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"mbMessage.", "userId", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getUserId),
				new FinderColumn<>(
					"mbMessage.", "classNameId", FinderColumn.Type.LONG, "=",
					true, true, MBMessage::getClassNameId),
				new FinderColumn<>(
					"mbMessage.", "classPK", FinderColumn.Type.LONG, "=", true,
					true, MBMessage::getClassPK),
				new FinderColumn<>(
					"mbMessage.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, MBMessage::getStatus));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(MBMessage::getExternalReferenceCode),
				MBMessage::getGroupId),
			_SQL_SELECT_MBMESSAGE_WHERE, "",
			new FinderColumn<>(
				"mbMessage.", "externalReferenceCode", FinderColumn.Type.STRING,
				"=", true, true, MBMessage::getExternalReferenceCode),
			new FinderColumn<>(
				"mbMessage.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, MBMessage::getGroupId));

		MBMessageUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		MBMessageUtil.setPersistence(null);

		entityCache.removeCache(MBMessageImpl.class.getName());
	}

	@Override
	@Reference(
		target = MBPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = MBPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = MBPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		MBMessageModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_MBMESSAGE =
		"SELECT mbMessage FROM MBMessage mbMessage";

	private static final String _SQL_SELECT_MBMESSAGE_WHERE =
		"SELECT mbMessage FROM MBMessage mbMessage WHERE ";

	private static final String _SQL_COUNT_MBMESSAGE_WHERE =
		"SELECT COUNT(mbMessage) FROM MBMessage mbMessage WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No MBMessage exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		MBMessagePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1384714668