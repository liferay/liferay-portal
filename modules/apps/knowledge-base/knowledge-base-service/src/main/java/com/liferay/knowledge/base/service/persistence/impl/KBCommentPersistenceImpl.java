/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.persistence.impl;

import com.liferay.knowledge.base.exception.NoSuchCommentException;
import com.liferay.knowledge.base.model.KBComment;
import com.liferay.knowledge.base.model.KBCommentTable;
import com.liferay.knowledge.base.model.impl.KBCommentImpl;
import com.liferay.knowledge.base.model.impl.KBCommentModelImpl;
import com.liferay.knowledge.base.service.persistence.KBCommentPersistence;
import com.liferay.knowledge.base.service.persistence.KBCommentUtil;
import com.liferay.knowledge.base.service.persistence.impl.constants.KBPersistenceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
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
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the kb comment service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KBCommentPersistence.class)
public class KBCommentPersistenceImpl
	extends BasePersistenceImpl<KBComment, NoSuchCommentException>
	implements KBCommentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KBCommentUtil</code> to access the kb comment persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KBCommentImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<KBComment, NoSuchCommentException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the kb comments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	@Override
	public List<KBComment> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<KBComment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb comment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	@Override
	public KBComment findByUuid_First(
			String uuid, OrderByComparator<KBComment> orderByComparator)
		throws NoSuchCommentException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first kb comment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	@Override
	public KBComment fetchByUuid_First(
		String uuid, OrderByComparator<KBComment> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the kb comments where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of kb comments where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching kb comments
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<KBComment, NoSuchCommentException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the kb comment where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCommentException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	@Override
	public KBComment findByUUID_G(String uuid, long groupId)
		throws NoSuchCommentException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the kb comment where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	@Override
	public KBComment fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the kb comment where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the kb comment that was removed
	 */
	@Override
	public KBComment removeByUUID_G(String uuid, long groupId)
		throws NoSuchCommentException {

		KBComment kbComment = findByUUID_G(uuid, groupId);

		return remove(kbComment);
	}

	/**
	 * Returns the number of kb comments where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching kb comments
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<KBComment, NoSuchCommentException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the kb comments where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	@Override
	public List<KBComment> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KBComment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb comment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	@Override
	public KBComment findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<KBComment> orderByComparator)
		throws NoSuchCommentException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first kb comment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	@Override
	public KBComment fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<KBComment> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the kb comments where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of kb comments where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching kb comments
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<KBComment, NoSuchCommentException>
		_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the kb comments where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	@Override
	public List<KBComment> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<KBComment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb comment in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	@Override
	public KBComment findByGroupId_First(
			long groupId, OrderByComparator<KBComment> orderByComparator)
		throws NoSuchCommentException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first kb comment in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	@Override
	public KBComment fetchByGroupId_First(
		long groupId, OrderByComparator<KBComment> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the kb comments where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of kb comments where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching kb comments
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder<KBComment, NoSuchCommentException>
		_collectionPersistenceFinderByG_C;

	/**
	 * Returns an ordered range of all the kb comments where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	@Override
	public List<KBComment> findByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<KBComment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C.find(
			finderCache, new Object[] {groupId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb comment in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	@Override
	public KBComment findByG_C_First(
			long groupId, long classNameId,
			OrderByComparator<KBComment> orderByComparator)
		throws NoSuchCommentException {

		return _collectionPersistenceFinderByG_C.findFirst(
			finderCache, new Object[] {groupId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first kb comment in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	@Override
	public KBComment fetchByG_C_First(
		long groupId, long classNameId,
		OrderByComparator<KBComment> orderByComparator) {

		return _collectionPersistenceFinderByG_C.fetchFirst(
			finderCache, new Object[] {groupId, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the kb comments where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_C(long groupId, long classNameId) {
		_collectionPersistenceFinderByG_C.remove(
			finderCache, new Object[] {groupId, classNameId});
	}

	/**
	 * Returns the number of kb comments where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching kb comments
	 */
	@Override
	public int countByG_C(long groupId, long classNameId) {
		return _collectionPersistenceFinderByG_C.count(
			finderCache, new Object[] {groupId, classNameId});
	}

	private CollectionPersistenceFinder<KBComment, NoSuchCommentException>
		_collectionPersistenceFinderByG_S;

	/**
	 * Returns an ordered range of all the kb comments where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	@Override
	public List<KBComment> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<KBComment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S.find(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb comment in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	@Override
	public KBComment findByG_S_First(
			long groupId, int status,
			OrderByComparator<KBComment> orderByComparator)
		throws NoSuchCommentException {

		return _collectionPersistenceFinderByG_S.findFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns the first kb comment in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	@Override
	public KBComment fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<KBComment> orderByComparator) {

		return _collectionPersistenceFinderByG_S.fetchFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Removes all the kb comments where groupId = &#63; and status = &#63; from the database.
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
	 * Returns the number of kb comments where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb comments
	 */
	@Override
	public int countByG_S(long groupId, int status) {
		return _collectionPersistenceFinderByG_S.count(
			finderCache, new Object[] {groupId, status});
	}

	private CollectionPersistenceFinder<KBComment, NoSuchCommentException>
		_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the kb comments where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	@Override
	public List<KBComment> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<KBComment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb comment in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	@Override
	public KBComment findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<KBComment> orderByComparator)
		throws NoSuchCommentException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first kb comment in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	@Override
	public KBComment fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<KBComment> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the kb comments where classNameId = &#63; and classPK = &#63; from the database.
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
	 * Returns the number of kb comments where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching kb comments
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private CollectionPersistenceFinder<KBComment, NoSuchCommentException>
		_collectionPersistenceFinderByU_C_C;

	/**
	 * Returns an ordered range of all the kb comments where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	@Override
	public List<KBComment> findByU_C_C(
		long userId, long classNameId, long classPK, int start, int end,
		OrderByComparator<KBComment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_C_C.find(
			finderCache, new Object[] {userId, classNameId, classPK}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb comment in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	@Override
	public KBComment findByU_C_C_First(
			long userId, long classNameId, long classPK,
			OrderByComparator<KBComment> orderByComparator)
		throws NoSuchCommentException {

		return _collectionPersistenceFinderByU_C_C.findFirst(
			finderCache, new Object[] {userId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first kb comment in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	@Override
	public KBComment fetchByU_C_C_First(
		long userId, long classNameId, long classPK,
		OrderByComparator<KBComment> orderByComparator) {

		return _collectionPersistenceFinderByU_C_C.fetchFirst(
			finderCache, new Object[] {userId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the kb comments where userId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
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
	 * Returns the number of kb comments where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching kb comments
	 */
	@Override
	public int countByU_C_C(long userId, long classNameId, long classPK) {
		return _collectionPersistenceFinderByU_C_C.count(
			finderCache, new Object[] {userId, classNameId, classPK});
	}

	private CollectionPersistenceFinder<KBComment, NoSuchCommentException>
		_collectionPersistenceFinderByC_C_S;

	/**
	 * Returns an ordered range of all the kb comments where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	@Override
	public List<KBComment> findByC_C_S(
		long classNameId, long classPK, int status, int start, int end,
		OrderByComparator<KBComment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_S.find(
			finderCache,
			new Object[] {classNameId, classPK, new int[] {status}}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb comment in the ordered set where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	@Override
	public KBComment findByC_C_S_First(
			long classNameId, long classPK, int status,
			OrderByComparator<KBComment> orderByComparator)
		throws NoSuchCommentException {

		KBComment kbComment = fetchByC_C_S_First(
			classNameId, classPK, status, orderByComparator);

		if (kbComment != null) {
			return kbComment;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCommentException(sb.toString());
	}

	/**
	 * Returns the first kb comment in the ordered set where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	@Override
	public KBComment fetchByC_C_S_First(
		long classNameId, long classPK, int status,
		OrderByComparator<KBComment> orderByComparator) {

		return _collectionPersistenceFinderByC_C_S.fetchFirst(
			finderCache,
			new Object[] {classNameId, classPK, new int[] {status}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb comments where classNameId = &#63; and classPK = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	@Override
	public List<KBComment> findByC_C_S(
		long classNameId, long classPK, int[] statuses, int start, int end,
		OrderByComparator<KBComment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_S.find(
			finderCache,
			new Object[] {
				classNameId, classPK, ArrayUtil.sortedUnique(statuses)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb comments where classNameId = &#63; and classPK = &#63; and status = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 */
	@Override
	public void removeByC_C_S(long classNameId, long classPK, int status) {
		_collectionPersistenceFinderByC_C_S.remove(
			finderCache,
			new Object[] {classNameId, classPK, new int[] {status}});
	}

	/**
	 * Returns the number of kb comments where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @return the number of matching kb comments
	 */
	@Override
	public int countByC_C_S(long classNameId, long classPK, int status) {
		return _collectionPersistenceFinderByC_C_S.count(
			finderCache,
			new Object[] {classNameId, classPK, new int[] {status}});
	}

	/**
	 * Returns the number of kb comments where classNameId = &#63; and classPK = &#63; and status = any &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param statuses the statuses
	 * @return the number of matching kb comments
	 */
	@Override
	public int countByC_C_S(long classNameId, long classPK, int[] statuses) {
		return _collectionPersistenceFinderByC_C_S.count(
			finderCache,
			new Object[] {
				classNameId, classPK, ArrayUtil.sortedUnique(statuses)
			});
	}

	public KBCommentPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(KBComment.class);

		setModelImplClass(KBCommentImpl.class);
		setModelPKClass(long.class);

		setTable(KBCommentTable.INSTANCE);
	}

	/**
	 * Creates a new kb comment with the primary key. Does not add the kb comment to the database.
	 *
	 * @param kbCommentId the primary key for the new kb comment
	 * @return the new kb comment
	 */
	@Override
	public KBComment create(long kbCommentId) {
		KBComment kbComment = new KBCommentImpl();

		kbComment.setNew(true);
		kbComment.setPrimaryKey(kbCommentId);

		String uuid = PortalUUIDUtil.generate();

		kbComment.setUuid(uuid);

		kbComment.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kbComment;
	}

	/**
	 * Removes the kb comment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kbCommentId the primary key of the kb comment
	 * @return the kb comment that was removed
	 * @throws NoSuchCommentException if a kb comment with the primary key could not be found
	 */
	@Override
	public KBComment remove(long kbCommentId) throws NoSuchCommentException {
		return remove((Serializable)kbCommentId);
	}

	@Override
	protected KBComment removeImpl(KBComment kbComment) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kbComment)) {
				kbComment = (KBComment)session.get(
					KBCommentImpl.class, kbComment.getPrimaryKeyObj());
			}

			if ((kbComment != null) &&
				ctPersistenceHelper.isRemove(kbComment)) {

				session.delete(kbComment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kbComment != null) {
			clearCache(kbComment);
		}

		return kbComment;
	}

	@Override
	public KBComment updateImpl(KBComment kbComment) {
		boolean isNew = kbComment.isNew();

		if (!(kbComment instanceof KBCommentModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kbComment.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(kbComment);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kbComment proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KBComment implementation " +
					kbComment.getClass());
		}

		KBCommentModelImpl kbCommentModelImpl = (KBCommentModelImpl)kbComment;

		if (Validator.isNull(kbComment.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			kbComment.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kbComment.getCreateDate() == null)) {
			if (serviceContext == null) {
				kbComment.setCreateDate(date);
			}
			else {
				kbComment.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!kbCommentModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kbComment.setModifiedDate(date);
			}
			else {
				kbComment.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kbComment)) {
				if (!isNew) {
					session.evict(
						KBCommentImpl.class, kbComment.getPrimaryKeyObj());
				}

				session.save(kbComment);
			}
			else {
				kbComment = (KBComment)session.merge(kbComment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kbComment, false);

		if (isNew) {
			kbComment.setNew(false);
		}

		kbComment.resetOriginalValues();

		return kbComment;
	}

	/**
	 * Returns the kb comment with the primary key or throws a <code>NoSuchCommentException</code> if it could not be found.
	 *
	 * @param kbCommentId the primary key of the kb comment
	 * @return the kb comment
	 * @throws NoSuchCommentException if a kb comment with the primary key could not be found
	 */
	@Override
	public KBComment findByPrimaryKey(long kbCommentId)
		throws NoSuchCommentException {

		return findByPrimaryKey((Serializable)kbCommentId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kb comment with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kbCommentId the primary key of the kb comment
	 * @return the kb comment, or <code>null</code> if a kb comment with the primary key could not be found
	 */
	@Override
	public KBComment fetchByPrimaryKey(long kbCommentId) {
		return fetchByPrimaryKey((Serializable)kbCommentId);
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
		return "kbCommentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KBCOMMENT;
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
		return KBCommentModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KBComment";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("content");
		ctMergeColumnNames.add("userRating");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("kbCommentId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});
	}

	/**
	 * Initializes the kb comment persistence.
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
			_SQL_SELECT_KBCOMMENT_WHERE, _SQL_COUNT_KBCOMMENT_WHERE,
			KBCommentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"kbComment.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, KBComment::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(KBComment::getUuid), KBComment::getGroupId),
			_SQL_SELECT_KBCOMMENT_WHERE, "",
			new FinderColumn<>(
				"kbComment.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, KBComment::getUuid),
			new FinderColumn<>(
				"kbComment.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, KBComment::getGroupId));

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
				_SQL_SELECT_KBCOMMENT_WHERE, _SQL_COUNT_KBCOMMENT_WHERE,
				KBCommentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kbComment.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, KBComment::getUuid),
				new FinderColumn<>(
					"kbComment.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, KBComment::getCompanyId));

		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
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
				_SQL_SELECT_KBCOMMENT_WHERE, _SQL_COUNT_KBCOMMENT_WHERE,
				KBCommentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kbComment.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBComment::getGroupId));

		_collectionPersistenceFinderByG_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "classNameId"}, false),
			_SQL_SELECT_KBCOMMENT_WHERE, _SQL_COUNT_KBCOMMENT_WHERE,
			KBCommentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"kbComment.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, KBComment::getGroupId),
			new FinderColumn<>(
				"kbComment.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, KBComment::getClassNameId));

		_collectionPersistenceFinderByG_S = new CollectionPersistenceFinder<>(
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
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"groupId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"groupId", "status"}, false),
			_SQL_SELECT_KBCOMMENT_WHERE, _SQL_COUNT_KBCOMMENT_WHERE,
			KBCommentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"kbComment.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, KBComment::getGroupId),
			new FinderColumn<>(
				"kbComment.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, KBComment::getStatus));

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
			_SQL_SELECT_KBCOMMENT_WHERE, _SQL_COUNT_KBCOMMENT_WHERE,
			KBCommentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"kbComment.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, KBComment::getClassNameId),
			new FinderColumn<>(
				"kbComment.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, KBComment::getClassPK));

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
			_SQL_SELECT_KBCOMMENT_WHERE, _SQL_COUNT_KBCOMMENT_WHERE,
			KBCommentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"kbComment.", "userId", FinderColumn.Type.LONG, "=", true, true,
				KBComment::getUserId),
			new FinderColumn<>(
				"kbComment.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, KBComment::getClassNameId),
			new FinderColumn<>(
				"kbComment.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, KBComment::getClassPK));

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
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_C_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"classNameId", "classPK", "status"}, false),
			_SQL_SELECT_KBCOMMENT_WHERE, _SQL_COUNT_KBCOMMENT_WHERE,
			KBCommentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"kbComment.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, KBComment::getClassNameId),
			new FinderColumn<>(
				"kbComment.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, KBComment::getClassPK),
			new ArrayableFinderColumn<>(
				"kbComment.", "status", FinderColumn.Type.INTEGER, "=", false,
				true, true, KBComment::getStatus));

		KBCommentUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KBCommentUtil.setPersistence(null);

		entityCache.removeCache(KBCommentImpl.class.getName());
	}

	@Override
	@Reference(
		target = KBPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = KBPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = KBPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		KBCommentModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KBCOMMENT =
		"SELECT kbComment FROM KBComment kbComment";

	private static final String _SQL_SELECT_KBCOMMENT_WHERE =
		"SELECT kbComment FROM KBComment kbComment WHERE ";

	private static final String _SQL_COUNT_KBCOMMENT_WHERE =
		"SELECT COUNT(kbComment) FROM KBComment kbComment WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No KBComment exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		KBCommentPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-507126066