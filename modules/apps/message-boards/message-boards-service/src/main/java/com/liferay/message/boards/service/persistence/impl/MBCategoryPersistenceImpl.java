/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.impl;

import com.liferay.message.boards.exception.DuplicateMBCategoryExternalReferenceCodeException;
import com.liferay.message.boards.exception.NoSuchCategoryException;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBCategoryTable;
import com.liferay.message.boards.model.impl.MBCategoryImpl;
import com.liferay.message.boards.model.impl.MBCategoryModelImpl;
import com.liferay.message.boards.service.persistence.MBCategoryPersistence;
import com.liferay.message.boards.service.persistence.MBCategoryUtil;
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
 * The persistence implementation for the message boards category service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = MBCategoryPersistence.class)
public class MBCategoryPersistenceImpl
	extends BasePersistenceImpl<MBCategory, NoSuchCategoryException>
	implements MBCategoryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>MBCategoryUtil</code> to access the message boards category persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		MBCategoryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<MBCategory, NoSuchCategoryException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the message boards categories where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByUuid_First(
			String uuid, OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first message boards category in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByUuid_First(
		String uuid, OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the message boards categories where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of message boards categories where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<MBCategory, NoSuchCategoryException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the message boards category where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByUUID_G(String uuid, long groupId)
		throws NoSuchCategoryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the message boards category where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the message boards category where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the message boards category that was removed
	 */
	@Override
	public MBCategory removeByUUID_G(String uuid, long groupId)
		throws NoSuchCategoryException {

		MBCategory mbCategory = findByUUID_G(uuid, groupId);

		return remove(mbCategory);
	}

	/**
	 * Returns the number of message boards categories where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<MBCategory, NoSuchCategoryException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the message boards categories where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first message boards category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the message boards categories where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of message boards categories where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<MBCategory, NoSuchCategoryException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByGroupId_First(
			long groupId, OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByGroupId_First(
		long groupId, OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Removes all the message boards categories where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupId}, groupId);
	}

	private CollectionPersistenceFinder<MBCategory, NoSuchCategoryException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the message boards categories where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByCompanyId_First(
			long companyId, OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first message boards category in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByCompanyId_First(
		long companyId, OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the message boards categories where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of message boards categories where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private FilterCollectionPersistenceFinder
		<MBCategory, NoSuchCategoryException> _collectionPersistenceFinderByG_P;

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P(
		long groupId, long parentCategoryId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P.find(
			finderCache, new Object[] {groupId, new long[] {parentCategoryId}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByG_P_First(
			long groupId, long parentCategoryId,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByG_P_First(
			groupId, parentCategoryId, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByG_P_First(
		long groupId, long parentCategoryId,
		OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_P.fetchFirst(
			finderCache, new Object[] {groupId, new long[] {parentCategoryId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P(
		long groupId, long parentCategoryId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_P.filterFind(
			finderCache, new Object[] {groupId, new long[] {parentCategoryId}},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P(
		long groupId, long[] parentCategoryIds, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_P.filterFind(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(parentCategoryIds)},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P(
		long groupId, long[] parentCategoryIds, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P.find(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(parentCategoryIds)},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the message boards categories where groupId = &#63; and parentCategoryId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 */
	@Override
	public void removeByG_P(long groupId, long parentCategoryId) {
		_collectionPersistenceFinderByG_P.remove(
			finderCache, new Object[] {groupId, new long[] {parentCategoryId}});
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByG_P(long groupId, long parentCategoryId) {
		return _collectionPersistenceFinderByG_P.count(
			finderCache, new Object[] {groupId, new long[] {parentCategoryId}});
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByG_P(long groupId, long[] parentCategoryIds) {
		return _collectionPersistenceFinderByG_P.count(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(parentCategoryIds)});
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(long groupId, long parentCategoryId) {
		return _collectionPersistenceFinderByG_P.filterCount(
			finderCache, new Object[] {groupId, new long[] {parentCategoryId}},
			groupId);
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(long groupId, long[] parentCategoryIds) {
		return _collectionPersistenceFinderByG_P.filterCount(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(parentCategoryIds)},
			groupId);
	}

	private UniquePersistenceFinder<MBCategory, NoSuchCategoryException>
		_uniquePersistenceFinderByG_F;

	/**
	 * Returns the message boards category where groupId = &#63; and friendlyURL = &#63; or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param friendlyURL the friendly url
	 * @return the matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByG_F(long groupId, String friendlyURL)
		throws NoSuchCategoryException {

		return _uniquePersistenceFinderByG_F.find(
			finderCache, new Object[] {groupId, friendlyURL});
	}

	/**
	 * Returns the message boards category where groupId = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param friendlyURL the friendly url
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByG_F(
		long groupId, String friendlyURL, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_F.fetch(
			finderCache, new Object[] {groupId, friendlyURL}, useFinderCache);
	}

	/**
	 * Removes the message boards category where groupId = &#63; and friendlyURL = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param friendlyURL the friendly url
	 * @return the message boards category that was removed
	 */
	@Override
	public MBCategory removeByG_F(long groupId, String friendlyURL)
		throws NoSuchCategoryException {

		MBCategory mbCategory = findByG_F(groupId, friendlyURL);

		return remove(mbCategory);
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and friendlyURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param friendlyURL the friendly url
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByG_F(long groupId, String friendlyURL) {
		return _uniquePersistenceFinderByG_F.count(
			finderCache, new Object[] {groupId, friendlyURL});
	}

	private FilterCollectionPersistenceFinder
		<MBCategory, NoSuchCategoryException> _collectionPersistenceFinderByG_S;

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S.find(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByG_S_First(
			long groupId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		return _collectionPersistenceFinderByG_S.findFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_S.fetchFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_S.filterFind(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the message boards categories where groupId = &#63; and status = &#63; from the database.
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
	 * Returns the number of message boards categories where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByG_S(long groupId, int status) {
		return _collectionPersistenceFinderByG_S.count(
			finderCache, new Object[] {groupId, status});
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_S(long groupId, int status) {
		return _collectionPersistenceFinderByG_S.filterCount(
			finderCache, new Object[] {groupId, status}, groupId);
	}

	private CollectionPersistenceFinder<MBCategory, NoSuchCategoryException>
		_collectionPersistenceFinderByC_S;

	/**
	 * Returns an ordered range of all the message boards categories where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S.find(
			finderCache, new Object[] {companyId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByC_S_First(
			long companyId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		return _collectionPersistenceFinderByC_S.findFirst(
			finderCache, new Object[] {companyId, status}, orderByComparator);
	}

	/**
	 * Returns the first message boards category in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByC_S_First(
		long companyId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByC_S.fetchFirst(
			finderCache, new Object[] {companyId, status}, orderByComparator);
	}

	/**
	 * Removes all the message boards categories where companyId = &#63; and status = &#63; from the database.
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
	 * Returns the number of message boards categories where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByC_S(long companyId, int status) {
		return _collectionPersistenceFinderByC_S.count(
			finderCache, new Object[] {companyId, status});
	}

	private FilterCollectionPersistenceFinder
		<MBCategory, NoSuchCategoryException>
			_collectionPersistenceFinderByNotC_G_P;

	/**
	 * Returns all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId) {

		return findByNotC_G_P(
			categoryId, groupId, parentCategoryId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId, int start,
		int end) {

		return findByNotC_G_P(
			categoryId, groupId, parentCategoryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId, int start,
		int end, OrderByComparator<MBCategory> orderByComparator) {

		return findByNotC_G_P(
			categoryId, groupId, parentCategoryId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId, int start,
		int end, OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByNotC_G_P.find(
			finderCache,
			new Object[] {
				new long[] {categoryId}, groupId, new long[] {parentCategoryId}
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByNotC_G_P_First(
			long categoryId, long groupId, long parentCategoryId,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByNotC_G_P_First(
			categoryId, groupId, parentCategoryId, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("categoryId!=");
		sb.append(categoryId);

		sb.append(", groupId=");
		sb.append(groupId);

		sb.append(", parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first message boards category in the ordered set where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByNotC_G_P_First(
		long categoryId, long groupId, long parentCategoryId,
		OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByNotC_G_P.fetchFirst(
			finderCache,
			new Object[] {
				new long[] {categoryId}, groupId, new long[] {parentCategoryId}
			},
			orderByComparator);
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId) {

		return filterFindByNotC_G_P(
			categoryId, groupId, parentCategoryId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId, int start,
		int end) {

		return filterFindByNotC_G_P(
			categoryId, groupId, parentCategoryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId, int start,
		int end, OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByNotC_G_P.filterFind(
			finderCache,
			new Object[] {
				new long[] {categoryId}, groupId, new long[] {parentCategoryId}
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds) {

		return filterFindByNotC_G_P(
			categoryIds, groupId, parentCategoryIds, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int start,
		int end) {

		return filterFindByNotC_G_P(
			categoryIds, groupId, parentCategoryIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int start,
		int end, OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByNotC_G_P.filterFind(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(categoryIds), groupId,
				ArrayUtil.sortedUnique(parentCategoryIds)
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds) {

		return findByNotC_G_P(
			categoryIds, groupId, parentCategoryIds, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int start,
		int end) {

		return findByNotC_G_P(
			categoryIds, groupId, parentCategoryIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int start,
		int end, OrderByComparator<MBCategory> orderByComparator) {

		return findByNotC_G_P(
			categoryIds, groupId, parentCategoryIds, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int start,
		int end, OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByNotC_G_P.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(categoryIds), groupId,
				ArrayUtil.sortedUnique(parentCategoryIds)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; from the database.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 */
	@Override
	public void removeByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId) {

		_collectionPersistenceFinderByNotC_G_P.remove(
			finderCache,
			new Object[] {
				new long[] {categoryId}, groupId, new long[] {parentCategoryId}
			});
	}

	/**
	 * Returns the number of message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId) {

		return _collectionPersistenceFinderByNotC_G_P.count(
			finderCache,
			new Object[] {
				new long[] {categoryId}, groupId, new long[] {parentCategoryId}
			});
	}

	/**
	 * Returns the number of message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds) {

		return _collectionPersistenceFinderByNotC_G_P.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(categoryIds), groupId,
				ArrayUtil.sortedUnique(parentCategoryIds)
			});
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId) {

		return _collectionPersistenceFinderByNotC_G_P.filterCount(
			finderCache,
			new Object[] {
				new long[] {categoryId}, groupId, new long[] {parentCategoryId}
			},
			groupId);
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds) {

		return _collectionPersistenceFinderByNotC_G_P.filterCount(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(categoryIds), groupId,
				ArrayUtil.sortedUnique(parentCategoryIds)
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<MBCategory, NoSuchCategoryException>
			_collectionPersistenceFinderByG_P_S;

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_S(
		long groupId, long parentCategoryId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_S.find(
			finderCache,
			new Object[] {groupId, new long[] {parentCategoryId}, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByG_P_S_First(
			long groupId, long parentCategoryId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByG_P_S_First(
			groupId, parentCategoryId, status, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByG_P_S_First(
		long groupId, long parentCategoryId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_P_S.fetchFirst(
			finderCache,
			new Object[] {groupId, new long[] {parentCategoryId}, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_S(
		long groupId, long parentCategoryId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_P_S.filterFind(
			finderCache,
			new Object[] {groupId, new long[] {parentCategoryId}, status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_S(
		long groupId, long[] parentCategoryIds, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_P_S.filterFind(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentCategoryIds), status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_S(
		long groupId, long[] parentCategoryIds, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_S.find(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentCategoryIds), status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 */
	@Override
	public void removeByG_P_S(long groupId, long parentCategoryId, int status) {
		_collectionPersistenceFinderByG_P_S.remove(
			finderCache,
			new Object[] {groupId, new long[] {parentCategoryId}, status});
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByG_P_S(long groupId, long parentCategoryId, int status) {
		return _collectionPersistenceFinderByG_P_S.count(
			finderCache,
			new Object[] {groupId, new long[] {parentCategoryId}, status});
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByG_P_S(
		long groupId, long[] parentCategoryIds, int status) {

		return _collectionPersistenceFinderByG_P_S.count(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentCategoryIds), status
			});
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_S(
		long groupId, long parentCategoryId, int status) {

		return _collectionPersistenceFinderByG_P_S.filterCount(
			finderCache,
			new Object[] {groupId, new long[] {parentCategoryId}, status},
			groupId);
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_S(
		long groupId, long[] parentCategoryIds, int status) {

		return _collectionPersistenceFinderByG_P_S.filterCount(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentCategoryIds), status
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<MBCategory, NoSuchCategoryException>
			_collectionPersistenceFinderByG_P_NotS;

	/**
	 * Returns all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_NotS(
		long groupId, long parentCategoryId, int status) {

		return findByG_P_NotS(
			groupId, parentCategoryId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_NotS(
		long groupId, long parentCategoryId, int status, int start, int end) {

		return findByG_P_NotS(
			groupId, parentCategoryId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_NotS(
		long groupId, long parentCategoryId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return findByG_P_NotS(
			groupId, parentCategoryId, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_NotS(
		long groupId, long parentCategoryId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_NotS.find(
			finderCache,
			new Object[] {groupId, new long[] {parentCategoryId}, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByG_P_NotS_First(
			long groupId, long parentCategoryId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByG_P_NotS_First(
			groupId, parentCategoryId, status, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByG_P_NotS_First(
		long groupId, long parentCategoryId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_P_NotS.fetchFirst(
			finderCache,
			new Object[] {groupId, new long[] {parentCategoryId}, status},
			orderByComparator);
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_NotS(
		long groupId, long parentCategoryId, int status) {

		return filterFindByG_P_NotS(
			groupId, parentCategoryId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_NotS(
		long groupId, long parentCategoryId, int status, int start, int end) {

		return filterFindByG_P_NotS(
			groupId, parentCategoryId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_NotS(
		long groupId, long parentCategoryId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_P_NotS.filterFind(
			finderCache,
			new Object[] {groupId, new long[] {parentCategoryId}, status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status) {

		return filterFindByG_P_NotS(
			groupId, parentCategoryIds, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status, int start,
		int end) {

		return filterFindByG_P_NotS(
			groupId, parentCategoryIds, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByG_P_NotS.filterFind(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentCategoryIds), status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status) {

		return findByG_P_NotS(
			groupId, parentCategoryIds, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status, int start,
		int end) {

		return findByG_P_NotS(
			groupId, parentCategoryIds, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return findByG_P_NotS(
			groupId, parentCategoryIds, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_NotS.find(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentCategoryIds), status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 */
	@Override
	public void removeByG_P_NotS(
		long groupId, long parentCategoryId, int status) {

		_collectionPersistenceFinderByG_P_NotS.remove(
			finderCache,
			new Object[] {groupId, new long[] {parentCategoryId}, status});
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByG_P_NotS(
		long groupId, long parentCategoryId, int status) {

		return _collectionPersistenceFinderByG_P_NotS.count(
			finderCache,
			new Object[] {groupId, new long[] {parentCategoryId}, status});
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status) {

		return _collectionPersistenceFinderByG_P_NotS.count(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentCategoryIds), status
			});
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_NotS(
		long groupId, long parentCategoryId, int status) {

		return _collectionPersistenceFinderByG_P_NotS.filterCount(
			finderCache,
			new Object[] {groupId, new long[] {parentCategoryId}, status},
			groupId);
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status) {

		return _collectionPersistenceFinderByG_P_NotS.filterCount(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(parentCategoryIds), status
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<MBCategory, NoSuchCategoryException>
			_collectionPersistenceFinderByNotC_G_P_S;

	/**
	 * Returns all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status) {

		return findByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status,
		int start, int end) {

		return findByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status,
		int start, int end, OrderByComparator<MBCategory> orderByComparator) {

		return findByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status,
		int start, int end, OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByNotC_G_P_S.find(
			finderCache,
			new Object[] {
				new long[] {categoryId}, groupId, new long[] {parentCategoryId},
				status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByNotC_G_P_S_First(
			long categoryId, long groupId, long parentCategoryId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByNotC_G_P_S_First(
			categoryId, groupId, parentCategoryId, status, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("categoryId!=");
		sb.append(categoryId);

		sb.append(", groupId=");
		sb.append(groupId);

		sb.append(", parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first message boards category in the ordered set where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByNotC_G_P_S_First(
		long categoryId, long groupId, long parentCategoryId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByNotC_G_P_S.fetchFirst(
			finderCache,
			new Object[] {
				new long[] {categoryId}, groupId, new long[] {parentCategoryId},
				status
			},
			orderByComparator);
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status) {

		return filterFindByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status,
		int start, int end) {

		return filterFindByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status,
		int start, int end, OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByNotC_G_P_S.filterFind(
			finderCache,
			new Object[] {
				new long[] {categoryId}, groupId, new long[] {parentCategoryId},
				status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds,
		int status) {

		return filterFindByNotC_G_P_S(
			categoryIds, groupId, parentCategoryIds, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int status,
		int start, int end) {

		return filterFindByNotC_G_P_S(
			categoryIds, groupId, parentCategoryIds, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int status,
		int start, int end, OrderByComparator<MBCategory> orderByComparator) {

		return _collectionPersistenceFinderByNotC_G_P_S.filterFind(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(categoryIds), groupId,
				ArrayUtil.sortedUnique(parentCategoryIds), status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds,
		int status) {

		return findByNotC_G_P_S(
			categoryIds, groupId, parentCategoryIds, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int status,
		int start, int end) {

		return findByNotC_G_P_S(
			categoryIds, groupId, parentCategoryIds, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int status,
		int start, int end, OrderByComparator<MBCategory> orderByComparator) {

		return findByNotC_G_P_S(
			categoryIds, groupId, parentCategoryIds, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int status,
		int start, int end, OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByNotC_G_P_S.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(categoryIds), groupId,
				ArrayUtil.sortedUnique(parentCategoryIds), status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63; from the database.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 */
	@Override
	public void removeByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status) {

		_collectionPersistenceFinderByNotC_G_P_S.remove(
			finderCache,
			new Object[] {
				new long[] {categoryId}, groupId, new long[] {parentCategoryId},
				status
			});
	}

	/**
	 * Returns the number of message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status) {

		return _collectionPersistenceFinderByNotC_G_P_S.count(
			finderCache,
			new Object[] {
				new long[] {categoryId}, groupId, new long[] {parentCategoryId},
				status
			});
	}

	/**
	 * Returns the number of message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds,
		int status) {

		return _collectionPersistenceFinderByNotC_G_P_S.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(categoryIds), groupId,
				ArrayUtil.sortedUnique(parentCategoryIds), status
			});
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status) {

		return _collectionPersistenceFinderByNotC_G_P_S.filterCount(
			finderCache,
			new Object[] {
				new long[] {categoryId}, groupId, new long[] {parentCategoryId},
				status
			},
			groupId);
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds,
		int status) {

		return _collectionPersistenceFinderByNotC_G_P_S.filterCount(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(categoryIds), groupId,
				ArrayUtil.sortedUnique(parentCategoryIds), status
			},
			groupId);
	}

	private UniquePersistenceFinder<MBCategory, NoSuchCategoryException>
		_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the message boards category where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchCategoryException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the message boards category where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the message boards category where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the message boards category that was removed
	 */
	@Override
	public MBCategory removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchCategoryException {

		MBCategory mbCategory = findByERC_G(externalReferenceCode, groupId);

		return remove(mbCategory);
	}

	/**
	 * Returns the number of message boards categories where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public MBCategoryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(MBCategory.class);

		setModelImplClass(MBCategoryImpl.class);
		setModelPKClass(long.class);

		setTable(MBCategoryTable.INSTANCE);
	}

	/**
	 * Creates a new message boards category with the primary key. Does not add the message boards category to the database.
	 *
	 * @param categoryId the primary key for the new message boards category
	 * @return the new message boards category
	 */
	@Override
	public MBCategory create(long categoryId) {
		MBCategory mbCategory = new MBCategoryImpl();

		mbCategory.setNew(true);
		mbCategory.setPrimaryKey(categoryId);

		String uuid = PortalUUIDUtil.generate();

		mbCategory.setUuid(uuid);

		mbCategory.setCompanyId(CompanyThreadLocal.getCompanyId());

		return mbCategory;
	}

	/**
	 * Removes the message boards category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param categoryId the primary key of the message boards category
	 * @return the message boards category that was removed
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory remove(long categoryId) throws NoSuchCategoryException {
		return remove((Serializable)categoryId);
	}

	@Override
	protected MBCategory removeImpl(MBCategory mbCategory) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(mbCategory)) {
				mbCategory = (MBCategory)session.get(
					MBCategoryImpl.class, mbCategory.getPrimaryKeyObj());
			}

			if ((mbCategory != null) &&
				ctPersistenceHelper.isRemove(mbCategory)) {

				session.delete(mbCategory);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (mbCategory != null) {
			clearCache(mbCategory);
		}

		return mbCategory;
	}

	@Override
	public MBCategory updateImpl(MBCategory mbCategory) {
		boolean isNew = mbCategory.isNew();

		if (!(mbCategory instanceof MBCategoryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(mbCategory.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(mbCategory);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in mbCategory proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom MBCategory implementation " +
					mbCategory.getClass());
		}

		MBCategoryModelImpl mbCategoryModelImpl =
			(MBCategoryModelImpl)mbCategory;

		if (Validator.isNull(mbCategory.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			mbCategory.setUuid(uuid);
		}

		if (Validator.isNull(mbCategory.getExternalReferenceCode())) {
			mbCategory.setExternalReferenceCode(mbCategory.getUuid());
		}
		else {
			if (!Objects.equals(
					mbCategoryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					mbCategory.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = mbCategory.getCompanyId();

					long groupId = mbCategory.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = mbCategory.getPrimaryKey();
					}

					try {
						mbCategory.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								MBCategory.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								mbCategory.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			MBCategory ercMBCategory = fetchByERC_G(
				mbCategory.getExternalReferenceCode(), mbCategory.getGroupId());

			if (isNew) {
				if (ercMBCategory != null) {
					throw new DuplicateMBCategoryExternalReferenceCodeException(
						"Duplicate message boards category with external reference code " +
							mbCategory.getExternalReferenceCode() +
								" and group " + mbCategory.getGroupId());
				}
			}
			else {
				if ((ercMBCategory != null) &&
					(mbCategory.getCategoryId() !=
						ercMBCategory.getCategoryId())) {

					throw new DuplicateMBCategoryExternalReferenceCodeException(
						"Duplicate message boards category with external reference code " +
							mbCategory.getExternalReferenceCode() +
								" and group " + mbCategory.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (mbCategory.getCreateDate() == null)) {
			if (serviceContext == null) {
				mbCategory.setCreateDate(date);
			}
			else {
				mbCategory.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!mbCategoryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				mbCategory.setModifiedDate(date);
			}
			else {
				mbCategory.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(mbCategory)) {
				if (!isNew) {
					session.evict(
						MBCategoryImpl.class, mbCategory.getPrimaryKeyObj());
				}

				session.save(mbCategory);
			}
			else {
				mbCategory = (MBCategory)session.merge(mbCategory);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(mbCategory, false);

		if (isNew) {
			mbCategory.setNew(false);
		}

		mbCategory.resetOriginalValues();

		return mbCategory;
	}

	/**
	 * Returns the message boards category with the primary key or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param categoryId the primary key of the message boards category
	 * @return the message boards category
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory findByPrimaryKey(long categoryId)
		throws NoSuchCategoryException {

		return findByPrimaryKey((Serializable)categoryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the message boards category with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param categoryId the primary key of the message boards category
	 * @return the message boards category, or <code>null</code> if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory fetchByPrimaryKey(long categoryId) {
		return fetchByPrimaryKey((Serializable)categoryId);
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
		return "categoryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_MBCATEGORY;
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
		return MBCategoryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "MBCategory";
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
		ctMergeColumnNames.add("parentCategoryId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("displayStyle");
		ctMergeColumnNames.add("friendlyURL");
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
			CTColumnResolutionType.PK, Collections.singleton("categoryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"groupId", "friendlyURL"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the message boards category persistence.
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
			_SQL_SELECT_MBCATEGORY_WHERE, _SQL_COUNT_MBCATEGORY_WHERE,
			MBCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"mbCategory.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, MBCategory::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(MBCategory::getUuid),
				MBCategory::getGroupId),
			_SQL_SELECT_MBCATEGORY_WHERE, "",
			new FinderColumn<>(
				"mbCategory.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, MBCategory::getUuid),
			new FinderColumn<>(
				"mbCategory.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, MBCategory::getGroupId));

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
				_SQL_SELECT_MBCATEGORY_WHERE, _SQL_COUNT_MBCATEGORY_WHERE,
				MBCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"mbCategory.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, MBCategory::getUuid),
				new FinderColumn<>(
					"mbCategory.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, MBCategory::getCompanyId));

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
				_SQL_SELECT_MBCATEGORY_WHERE, _SQL_COUNT_MBCATEGORY_WHERE,
				MBCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"mbCategory.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, MBCategory::getGroupId));

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
				_SQL_SELECT_MBCATEGORY_WHERE, _SQL_COUNT_MBCATEGORY_WHERE,
				MBCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"mbCategory.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, MBCategory::getCompanyId));

		_collectionPersistenceFinderByG_P =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "parentCategoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "parentCategoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "parentCategoryId"}, false),
				_SQL_SELECT_MBCATEGORY_WHERE, _SQL_COUNT_MBCATEGORY_WHERE,
				MBCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"mbCategory.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, MBCategory::getGroupId),
				new ArrayableFinderColumn<>(
					"mbCategory.", "parentCategoryId", FinderColumn.Type.LONG,
					"=", false, true, true, MBCategory::getParentCategoryId));

		_uniquePersistenceFinderByG_F = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_F",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "friendlyURL"}, 0, 2, false,
				MBCategory::getGroupId,
				convertNullFunction(MBCategory::getFriendlyURL)),
			_SQL_SELECT_MBCATEGORY_WHERE, "",
			new FinderColumn<>(
				"mbCategory.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, MBCategory::getGroupId),
			new FinderColumn<>(
				"mbCategory.", "friendlyURL", FinderColumn.Type.STRING, "=",
				true, true, MBCategory::getFriendlyURL));

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
				_SQL_SELECT_MBCATEGORY_WHERE, _SQL_COUNT_MBCATEGORY_WHERE,
				MBCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"mbCategory.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, MBCategory::getGroupId),
				new FinderColumn<>(
					"mbCategory.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, MBCategory::getStatus));

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
			_SQL_SELECT_MBCATEGORY_WHERE, _SQL_COUNT_MBCATEGORY_WHERE,
			MBCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"mbCategory.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, MBCategory::getCompanyId),
			new FinderColumn<>(
				"mbCategory.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, MBCategory::getStatus));

		_collectionPersistenceFinderByNotC_G_P =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByNotC_G_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"categoryId", "groupId", "parentCategoryId"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByNotC_G_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"categoryId", "groupId", "parentCategoryId"},
					false),
				_SQL_SELECT_MBCATEGORY_WHERE, _SQL_COUNT_MBCATEGORY_WHERE,
				MBCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new ArrayableFinderColumn<>(
					"mbCategory.", "categoryId", FinderColumn.Type.LONG, "!=",
					true, true, true, MBCategory::getCategoryId),
				new FinderColumn<>(
					"mbCategory.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, MBCategory::getGroupId),
				new ArrayableFinderColumn<>(
					"mbCategory.", "parentCategoryId", FinderColumn.Type.LONG,
					"=", false, true, true, MBCategory::getParentCategoryId));

		_collectionPersistenceFinderByG_P_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "parentCategoryId", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "parentCategoryId", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "parentCategoryId", "status"},
					false),
				_SQL_SELECT_MBCATEGORY_WHERE, _SQL_COUNT_MBCATEGORY_WHERE,
				MBCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"mbCategory.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, MBCategory::getGroupId),
				new ArrayableFinderColumn<>(
					"mbCategory.", "parentCategoryId", FinderColumn.Type.LONG,
					"=", false, true, true, MBCategory::getParentCategoryId),
				new FinderColumn<>(
					"mbCategory.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, MBCategory::getStatus));

		_collectionPersistenceFinderByG_P_NotS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "parentCategoryId", "status"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "parentCategoryId", "status"},
					false),
				_SQL_SELECT_MBCATEGORY_WHERE, _SQL_COUNT_MBCATEGORY_WHERE,
				MBCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"mbCategory.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, MBCategory::getGroupId),
				new ArrayableFinderColumn<>(
					"mbCategory.", "parentCategoryId", FinderColumn.Type.LONG,
					"=", false, true, true, MBCategory::getParentCategoryId),
				new FinderColumn<>(
					"mbCategory.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, MBCategory::getStatus));

		_collectionPersistenceFinderByNotC_G_P_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByNotC_G_P_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"categoryId", "groupId", "parentCategoryId", "status"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByNotC_G_P_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"categoryId", "groupId", "parentCategoryId", "status"
					},
					false),
				_SQL_SELECT_MBCATEGORY_WHERE, _SQL_COUNT_MBCATEGORY_WHERE,
				MBCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new ArrayableFinderColumn<>(
					"mbCategory.", "categoryId", FinderColumn.Type.LONG, "!=",
					true, true, true, MBCategory::getCategoryId),
				new FinderColumn<>(
					"mbCategory.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, MBCategory::getGroupId),
				new ArrayableFinderColumn<>(
					"mbCategory.", "parentCategoryId", FinderColumn.Type.LONG,
					"=", false, true, true, MBCategory::getParentCategoryId),
				new FinderColumn<>(
					"mbCategory.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, MBCategory::getStatus));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(MBCategory::getExternalReferenceCode),
				MBCategory::getGroupId),
			_SQL_SELECT_MBCATEGORY_WHERE, "",
			new FinderColumn<>(
				"mbCategory.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				MBCategory::getExternalReferenceCode),
			new FinderColumn<>(
				"mbCategory.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, MBCategory::getGroupId));

		MBCategoryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		MBCategoryUtil.setPersistence(null);

		entityCache.removeCache(MBCategoryImpl.class.getName());
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
		MBCategoryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_MBCATEGORY =
		"SELECT mbCategory FROM MBCategory mbCategory";

	private static final String _SQL_SELECT_MBCATEGORY_WHERE =
		"SELECT mbCategory FROM MBCategory mbCategory WHERE ";

	private static final String _SQL_COUNT_MBCATEGORY_WHERE =
		"SELECT COUNT(mbCategory) FROM MBCategory mbCategory WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No MBCategory exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		MBCategoryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-832855607