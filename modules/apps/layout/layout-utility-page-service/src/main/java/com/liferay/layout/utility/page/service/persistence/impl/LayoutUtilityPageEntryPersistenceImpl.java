/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.service.persistence.impl;

import com.liferay.layout.utility.page.exception.DuplicateLayoutUtilityPageEntryExternalReferenceCodeException;
import com.liferay.layout.utility.page.exception.NoSuchLayoutUtilityPageEntryException;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntryTable;
import com.liferay.layout.utility.page.model.impl.LayoutUtilityPageEntryImpl;
import com.liferay.layout.utility.page.model.impl.LayoutUtilityPageEntryModelImpl;
import com.liferay.layout.utility.page.service.persistence.LayoutUtilityPageEntryPersistence;
import com.liferay.layout.utility.page.service.persistence.LayoutUtilityPageEntryUtil;
import com.liferay.layout.utility.page.service.persistence.impl.constants.LayoutUtilityPagePersistenceConstants;
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
 * The persistence implementation for the layout utility page entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = LayoutUtilityPageEntryPersistence.class)
public class LayoutUtilityPageEntryPersistenceImpl
	extends BasePersistenceImpl
		<LayoutUtilityPageEntry, NoSuchLayoutUtilityPageEntryException>
	implements LayoutUtilityPageEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutUtilityPageEntryUtil</code> to access the layout utility page entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutUtilityPageEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LayoutUtilityPageEntry, NoSuchLayoutUtilityPageEntryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the layout utility page entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout utility page entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByUuid_First(
			String uuid,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first layout utility page entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByUuid_First(
		String uuid,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the layout utility page entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of layout utility page entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<LayoutUtilityPageEntry, NoSuchLayoutUtilityPageEntryException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the layout utility page entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchLayoutUtilityPageEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchLayoutUtilityPageEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the layout utility page entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the layout utility page entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout utility page entry that was removed
	 */
	@Override
	public LayoutUtilityPageEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = findByUUID_G(
			uuid, groupId);

		return remove(layoutUtilityPageEntry);
	}

	/**
	 * Returns the number of layout utility page entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<LayoutUtilityPageEntry, NoSuchLayoutUtilityPageEntryException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the layout utility page entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout utility page entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first layout utility page entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the layout utility page entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of layout utility page entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<LayoutUtilityPageEntry, NoSuchLayoutUtilityPageEntryException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout utility page entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByGroupId_First(
			long groupId,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first layout utility page entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByGroupId_First(
		long groupId,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Removes all the layout utility page entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of layout utility page entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of layout utility page entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupId}, groupId);
	}

	private UniquePersistenceFinder
		<LayoutUtilityPageEntry, NoSuchLayoutUtilityPageEntryException>
			_uniquePersistenceFinderByPlid;

	/**
	 * Returns the layout utility page entry where plid = &#63; or throws a <code>NoSuchLayoutUtilityPageEntryException</code> if it could not be found.
	 *
	 * @param plid the plid
	 * @return the matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByPlid(long plid)
		throws NoSuchLayoutUtilityPageEntryException {

		return _uniquePersistenceFinderByPlid.find(
			finderCache, new Object[] {plid});
	}

	/**
	 * Returns the layout utility page entry where plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByPlid(
		long plid, boolean useFinderCache) {

		return _uniquePersistenceFinderByPlid.fetch(
			finderCache, new Object[] {plid}, useFinderCache);
	}

	/**
	 * Removes the layout utility page entry where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 * @return the layout utility page entry that was removed
	 */
	@Override
	public LayoutUtilityPageEntry removeByPlid(long plid)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = findByPlid(plid);

		return remove(layoutUtilityPageEntry);
	}

	/**
	 * Returns the number of layout utility page entries where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByPlid(long plid) {
		return _uniquePersistenceFinderByPlid.count(
			finderCache, new Object[] {plid});
	}

	private FilterCollectionPersistenceFinder
		<LayoutUtilityPageEntry, NoSuchLayoutUtilityPageEntryException>
			_collectionPersistenceFinderByG_T;

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_T(
		long groupId, String type, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T.find(
			finderCache, new Object[] {groupId, new String[] {type}}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout utility page entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByG_T_First(
			long groupId, String type,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByG_T_First(
			groupId, type, orderByComparator);

		if (layoutUtilityPageEntry != null) {
			return layoutUtilityPageEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchLayoutUtilityPageEntryException(sb.toString());
	}

	/**
	 * Returns the first layout utility page entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByG_T_First(
		long groupId, String type,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_T.fetchFirst(
			finderCache, new Object[] {groupId, new String[] {type}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries that the user has permissions to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_T(
		long groupId, String type, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_T.filterFind(
			finderCache, new Object[] {groupId, new String[] {type}}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries that the user has permission to view where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_T(
		long groupId, String[] types, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_T.filterFind(
			finderCache, new Object[] {groupId, ArrayUtil.sortedUnique(types)},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_T(
		long groupId, String[] types, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T.find(
			finderCache, new Object[] {groupId, ArrayUtil.sortedUnique(types)},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layout utility page entries where groupId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 */
	@Override
	public void removeByG_T(long groupId, String type) {
		_collectionPersistenceFinderByG_T.remove(
			finderCache, new Object[] {groupId, new String[] {type}});
	}

	/**
	 * Returns the number of layout utility page entries where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByG_T(long groupId, String type) {
		return _collectionPersistenceFinderByG_T.count(
			finderCache, new Object[] {groupId, new String[] {type}});
	}

	/**
	 * Returns the number of layout utility page entries where groupId = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByG_T(long groupId, String[] types) {
		return _collectionPersistenceFinderByG_T.count(
			finderCache, new Object[] {groupId, ArrayUtil.sortedUnique(types)});
	}

	/**
	 * Returns the number of layout utility page entries that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_T(long groupId, String type) {
		return _collectionPersistenceFinderByG_T.filterCount(
			finderCache, new Object[] {groupId, new String[] {type}}, groupId);
	}

	/**
	 * Returns the number of layout utility page entries that the user has permission to view where groupId = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @return the number of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_T(long groupId, String[] types) {
		return _collectionPersistenceFinderByG_T.filterCount(
			finderCache, new Object[] {groupId, ArrayUtil.sortedUnique(types)},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutUtilityPageEntry, NoSuchLayoutUtilityPageEntryException>
			_collectionPersistenceFinderByG_D_T;

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_D_T(
		long groupId, boolean defaultLayoutUtilityPageEntry, String type,
		int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_D_T.find(
			finderCache,
			new Object[] {groupId, defaultLayoutUtilityPageEntry, type}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout utility page entry in the ordered set where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByG_D_T_First(
			long groupId, boolean defaultLayoutUtilityPageEntry, String type,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		return _collectionPersistenceFinderByG_D_T.findFirst(
			finderCache,
			new Object[] {groupId, defaultLayoutUtilityPageEntry, type},
			orderByComparator);
	}

	/**
	 * Returns the first layout utility page entry in the ordered set where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByG_D_T_First(
		long groupId, boolean defaultLayoutUtilityPageEntry, String type,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_D_T.fetchFirst(
			finderCache,
			new Object[] {groupId, defaultLayoutUtilityPageEntry, type},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries that the user has permissions to view where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_D_T(
		long groupId, boolean defaultLayoutUtilityPageEntry, String type,
		int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_D_T.filterFind(
			finderCache,
			new Object[] {groupId, defaultLayoutUtilityPageEntry, type}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the layout utility page entries where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 */
	@Override
	public void removeByG_D_T(
		long groupId, boolean defaultLayoutUtilityPageEntry, String type) {

		_collectionPersistenceFinderByG_D_T.remove(
			finderCache,
			new Object[] {groupId, defaultLayoutUtilityPageEntry, type});
	}

	/**
	 * Returns the number of layout utility page entries where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByG_D_T(
		long groupId, boolean defaultLayoutUtilityPageEntry, String type) {

		return _collectionPersistenceFinderByG_D_T.count(
			finderCache,
			new Object[] {groupId, defaultLayoutUtilityPageEntry, type});
	}

	/**
	 * Returns the number of layout utility page entries that the user has permission to view where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @return the number of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_D_T(
		long groupId, boolean defaultLayoutUtilityPageEntry, String type) {

		return _collectionPersistenceFinderByG_D_T.filterCount(
			finderCache,
			new Object[] {groupId, defaultLayoutUtilityPageEntry, type},
			groupId);
	}

	private UniquePersistenceFinder
		<LayoutUtilityPageEntry, NoSuchLayoutUtilityPageEntryException>
			_uniquePersistenceFinderByG_N_T;

	/**
	 * Returns the layout utility page entry where groupId = &#63; and name = &#63; and type = &#63; or throws a <code>NoSuchLayoutUtilityPageEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByG_N_T(
			long groupId, String name, String type)
		throws NoSuchLayoutUtilityPageEntryException {

		return _uniquePersistenceFinderByG_N_T.find(
			finderCache, new Object[] {groupId, name, type});
	}

	/**
	 * Returns the layout utility page entry where groupId = &#63; and name = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByG_N_T(
		long groupId, String name, String type, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_N_T.fetch(
			finderCache, new Object[] {groupId, name, type}, useFinderCache);
	}

	/**
	 * Removes the layout utility page entry where groupId = &#63; and name = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the layout utility page entry that was removed
	 */
	@Override
	public LayoutUtilityPageEntry removeByG_N_T(
			long groupId, String name, String type)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = findByG_N_T(
			groupId, name, type);

		return remove(layoutUtilityPageEntry);
	}

	/**
	 * Returns the number of layout utility page entries where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByG_N_T(long groupId, String name, String type) {
		return _uniquePersistenceFinderByG_N_T.count(
			finderCache, new Object[] {groupId, name, type});
	}

	private FilterCollectionPersistenceFinder
		<LayoutUtilityPageEntry, NoSuchLayoutUtilityPageEntryException>
			_collectionPersistenceFinderByG_LikeN_T;

	/**
	 * Returns all the layout utility page entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_LikeN_T(
		long groupId, String name, String type) {

		return findByG_LikeN_T(
			groupId, name, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout utility page entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_LikeN_T(
		long groupId, String name, String type, int start, int end) {

		return findByG_LikeN_T(groupId, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_LikeN_T(
		long groupId, String name, String type, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return findByG_LikeN_T(
			groupId, name, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_LikeN_T(
		long groupId, String name, String type, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN_T.find(
			finderCache, new Object[] {groupId, name, new String[] {type}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout utility page entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByG_LikeN_T_First(
			long groupId, String name, String type,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByG_LikeN_T_First(
			groupId, name, type, orderByComparator);

		if (layoutUtilityPageEntry != null) {
			return layoutUtilityPageEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", nameLIKE");
		sb.append(name);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchLayoutUtilityPageEntryException(sb.toString());
	}

	/**
	 * Returns the first layout utility page entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByG_LikeN_T_First(
		long groupId, String name, String type,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN_T.fetchFirst(
			finderCache, new Object[] {groupId, name, new String[] {type}},
			orderByComparator);
	}

	/**
	 * Returns all the layout utility page entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_LikeN_T(
		long groupId, String name, String type) {

		return filterFindByG_LikeN_T(
			groupId, name, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout utility page entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_LikeN_T(
		long groupId, String name, String type, int start, int end) {

		return filterFindByG_LikeN_T(groupId, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries that the user has permissions to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_LikeN_T(
		long groupId, String name, String type, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN_T.filterFind(
			finderCache, new Object[] {groupId, name, new String[] {type}},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the layout utility page entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_LikeN_T(
		long groupId, String name, String[] types) {

		return filterFindByG_LikeN_T(
			groupId, name, types, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout utility page entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_LikeN_T(
		long groupId, String name, String[] types, int start, int end) {

		return filterFindByG_LikeN_T(groupId, name, types, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_LikeN_T(
		long groupId, String name, String[] types, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN_T.filterFind(
			finderCache,
			new Object[] {groupId, name, ArrayUtil.sortedUnique(types)}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Returns all the layout utility page entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_LikeN_T(
		long groupId, String name, String[] types) {

		return findByG_LikeN_T(
			groupId, name, types, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout utility page entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_LikeN_T(
		long groupId, String name, String[] types, int start, int end) {

		return findByG_LikeN_T(groupId, name, types, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_LikeN_T(
		long groupId, String name, String[] types, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return findByG_LikeN_T(
			groupId, name, types, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63; and name LIKE &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_LikeN_T(
		long groupId, String name, String[] types, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN_T.find(
			finderCache,
			new Object[] {groupId, name, ArrayUtil.sortedUnique(types)}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layout utility page entries where groupId = &#63; and name LIKE &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 */
	@Override
	public void removeByG_LikeN_T(long groupId, String name, String type) {
		_collectionPersistenceFinderByG_LikeN_T.remove(
			finderCache, new Object[] {groupId, name, new String[] {type}});
	}

	/**
	 * Returns the number of layout utility page entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByG_LikeN_T(long groupId, String name, String type) {
		return _collectionPersistenceFinderByG_LikeN_T.count(
			finderCache, new Object[] {groupId, name, new String[] {type}});
	}

	/**
	 * Returns the number of layout utility page entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByG_LikeN_T(long groupId, String name, String[] types) {
		return _collectionPersistenceFinderByG_LikeN_T.count(
			finderCache,
			new Object[] {groupId, name, ArrayUtil.sortedUnique(types)});
	}

	/**
	 * Returns the number of layout utility page entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeN_T(long groupId, String name, String type) {
		return _collectionPersistenceFinderByG_LikeN_T.filterCount(
			finderCache, new Object[] {groupId, name, new String[] {type}},
			groupId);
	}

	/**
	 * Returns the number of layout utility page entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the number of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeN_T(
		long groupId, String name, String[] types) {

		return _collectionPersistenceFinderByG_LikeN_T.filterCount(
			finderCache,
			new Object[] {groupId, name, ArrayUtil.sortedUnique(types)},
			groupId);
	}

	private UniquePersistenceFinder
		<LayoutUtilityPageEntry, NoSuchLayoutUtilityPageEntryException>
			_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the layout utility page entry where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchLayoutUtilityPageEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchLayoutUtilityPageEntryException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the layout utility page entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the layout utility page entry where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout utility page entry that was removed
	 */
	@Override
	public LayoutUtilityPageEntry removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = findByERC_G(
			externalReferenceCode, groupId);

		return remove(layoutUtilityPageEntry);
	}

	/**
	 * Returns the number of layout utility page entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public LayoutUtilityPageEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutUtilityPageEntry.class);

		setModelImplClass(LayoutUtilityPageEntryImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutUtilityPageEntryTable.INSTANCE);
	}

	/**
	 * Creates a new layout utility page entry with the primary key. Does not add the layout utility page entry to the database.
	 *
	 * @param LayoutUtilityPageEntryId the primary key for the new layout utility page entry
	 * @return the new layout utility page entry
	 */
	@Override
	public LayoutUtilityPageEntry create(long LayoutUtilityPageEntryId) {
		LayoutUtilityPageEntry layoutUtilityPageEntry =
			new LayoutUtilityPageEntryImpl();

		layoutUtilityPageEntry.setNew(true);
		layoutUtilityPageEntry.setPrimaryKey(LayoutUtilityPageEntryId);

		String uuid = PortalUUIDUtil.generate();

		layoutUtilityPageEntry.setUuid(uuid);

		layoutUtilityPageEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return layoutUtilityPageEntry;
	}

	/**
	 * Removes the layout utility page entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the layout utility page entry
	 * @return the layout utility page entry that was removed
	 * @throws NoSuchLayoutUtilityPageEntryException if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry remove(long LayoutUtilityPageEntryId)
		throws NoSuchLayoutUtilityPageEntryException {

		return remove((Serializable)LayoutUtilityPageEntryId);
	}

	@Override
	protected LayoutUtilityPageEntry removeImpl(
		LayoutUtilityPageEntry layoutUtilityPageEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutUtilityPageEntry)) {
				layoutUtilityPageEntry = (LayoutUtilityPageEntry)session.get(
					LayoutUtilityPageEntryImpl.class,
					layoutUtilityPageEntry.getPrimaryKeyObj());
			}

			if ((layoutUtilityPageEntry != null) &&
				ctPersistenceHelper.isRemove(layoutUtilityPageEntry)) {

				session.delete(layoutUtilityPageEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutUtilityPageEntry != null) {
			clearCache(layoutUtilityPageEntry);
		}

		return layoutUtilityPageEntry;
	}

	@Override
	public LayoutUtilityPageEntry updateImpl(
		LayoutUtilityPageEntry layoutUtilityPageEntry) {

		boolean isNew = layoutUtilityPageEntry.isNew();

		if (!(layoutUtilityPageEntry instanceof
				LayoutUtilityPageEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(layoutUtilityPageEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutUtilityPageEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutUtilityPageEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutUtilityPageEntry implementation " +
					layoutUtilityPageEntry.getClass());
		}

		LayoutUtilityPageEntryModelImpl layoutUtilityPageEntryModelImpl =
			(LayoutUtilityPageEntryModelImpl)layoutUtilityPageEntry;

		if (Validator.isNull(layoutUtilityPageEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			layoutUtilityPageEntry.setUuid(uuid);
		}

		if (Validator.isNull(
				layoutUtilityPageEntry.getExternalReferenceCode())) {

			layoutUtilityPageEntry.setExternalReferenceCode(
				layoutUtilityPageEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					layoutUtilityPageEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					layoutUtilityPageEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = layoutUtilityPageEntry.getCompanyId();

					long groupId = layoutUtilityPageEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = layoutUtilityPageEntry.getPrimaryKey();
					}

					try {
						layoutUtilityPageEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								LayoutUtilityPageEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								layoutUtilityPageEntry.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			LayoutUtilityPageEntry ercLayoutUtilityPageEntry = fetchByERC_G(
				layoutUtilityPageEntry.getExternalReferenceCode(),
				layoutUtilityPageEntry.getGroupId());

			if (isNew) {
				if (ercLayoutUtilityPageEntry != null) {
					throw new DuplicateLayoutUtilityPageEntryExternalReferenceCodeException(
						"Duplicate layout utility page entry with external reference code " +
							layoutUtilityPageEntry.getExternalReferenceCode() +
								" and group " +
									layoutUtilityPageEntry.getGroupId());
				}
			}
			else {
				if ((ercLayoutUtilityPageEntry != null) &&
					(layoutUtilityPageEntry.getLayoutUtilityPageEntryId() !=
						ercLayoutUtilityPageEntry.
							getLayoutUtilityPageEntryId())) {

					throw new DuplicateLayoutUtilityPageEntryExternalReferenceCodeException(
						"Duplicate layout utility page entry with external reference code " +
							layoutUtilityPageEntry.getExternalReferenceCode() +
								" and group " +
									layoutUtilityPageEntry.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutUtilityPageEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutUtilityPageEntry.setCreateDate(date);
			}
			else {
				layoutUtilityPageEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutUtilityPageEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutUtilityPageEntry.setModifiedDate(date);
			}
			else {
				layoutUtilityPageEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(layoutUtilityPageEntry)) {
				if (!isNew) {
					session.evict(
						LayoutUtilityPageEntryImpl.class,
						layoutUtilityPageEntry.getPrimaryKeyObj());
				}

				session.save(layoutUtilityPageEntry);
			}
			else {
				layoutUtilityPageEntry = (LayoutUtilityPageEntry)session.merge(
					layoutUtilityPageEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layoutUtilityPageEntry, false);

		if (isNew) {
			layoutUtilityPageEntry.setNew(false);
		}

		layoutUtilityPageEntry.resetOriginalValues();

		return layoutUtilityPageEntry;
	}

	/**
	 * Returns the layout utility page entry with the primary key or throws a <code>NoSuchLayoutUtilityPageEntryException</code> if it could not be found.
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the layout utility page entry
	 * @return the layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByPrimaryKey(
			long LayoutUtilityPageEntryId)
		throws NoSuchLayoutUtilityPageEntryException {

		return findByPrimaryKey((Serializable)LayoutUtilityPageEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the layout utility page entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the layout utility page entry
	 * @return the layout utility page entry, or <code>null</code> if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByPrimaryKey(
		long LayoutUtilityPageEntryId) {

		return fetchByPrimaryKey((Serializable)LayoutUtilityPageEntryId);
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
		return "LayoutUtilityPageEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTUTILITYPAGEENTRY;
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
		return LayoutUtilityPageEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LayoutUtilityPageEntry";
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
		ctMergeColumnNames.add("plid");
		ctMergeColumnNames.add("previewFileEntryId");
		ctMergeColumnNames.add("defaultLayoutUtilityPageEntry");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("LayoutUtilityPageEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"plid"});

		_uniqueIndexColumnNames.add(new String[] {"groupId", "name", "type_"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the layout utility page entry persistence.
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
			_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE,
			_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE,
			LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"layoutUtilityPageEntry.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutUtilityPageEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(LayoutUtilityPageEntry::getUuid),
				LayoutUtilityPageEntry::getGroupId),
			_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE, "",
			new FinderColumn<>(
				"layoutUtilityPageEntry.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutUtilityPageEntry::getUuid),
			new FinderColumn<>(
				"layoutUtilityPageEntry.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, LayoutUtilityPageEntry::getGroupId));

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
				_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE,
				_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE,
				LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layoutUtilityPageEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutUtilityPageEntry::getUuid),
				new FinderColumn<>(
					"layoutUtilityPageEntry.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutUtilityPageEntry::getCompanyId));

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
				_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE,
				_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE,
				LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layoutUtilityPageEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutUtilityPageEntry::getGroupId));

		_uniquePersistenceFinderByPlid = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByPlid",
				new String[] {Long.class.getName()}, new String[] {"plid"}, 0,
				0, false, LayoutUtilityPageEntry::getPlid),
			_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE, "",
			new FinderColumn<>(
				"layoutUtilityPageEntry.", "plid", FinderColumn.Type.LONG, "=",
				true, true, LayoutUtilityPageEntry::getPlid));

		_collectionPersistenceFinderByG_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "type_"}, 0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_T",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "type_"}, 0, 2, false, null),
				_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE,
				_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE,
				LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layoutUtilityPageEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutUtilityPageEntry::getGroupId),
				new ArrayableFinderColumn<>(
					"layoutUtilityPageEntry.", "type", "type_",
					FinderColumn.Type.STRING, "=", false, true, true,
					LayoutUtilityPageEntry::getType));

		_collectionPersistenceFinderByG_D_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_D_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "defaultLayoutUtilityPageEntry", "type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_D_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {
						"groupId", "defaultLayoutUtilityPageEntry", "type_"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_D_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {
						"groupId", "defaultLayoutUtilityPageEntry", "type_"
					},
					0, 4, false, null),
				_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE,
				_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE,
				LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layoutUtilityPageEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutUtilityPageEntry::getGroupId),
				new FinderColumn<>(
					"layoutUtilityPageEntry.", "defaultLayoutUtilityPageEntry",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					LayoutUtilityPageEntry::isDefaultLayoutUtilityPageEntry),
				new FinderColumn<>(
					"layoutUtilityPageEntry.", "type", "type_",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutUtilityPageEntry::getType));

		_uniquePersistenceFinderByG_N_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_N_T",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "name", "type_"}, 0, 6, false,
				LayoutUtilityPageEntry::getGroupId,
				convertNullFunction(LayoutUtilityPageEntry::getName),
				convertNullFunction(LayoutUtilityPageEntry::getType)),
			_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE, "",
			new FinderColumn<>(
				"layoutUtilityPageEntry.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, LayoutUtilityPageEntry::getGroupId),
			new FinderColumn<>(
				"layoutUtilityPageEntry.", "name", FinderColumn.Type.STRING,
				"=", true, true, LayoutUtilityPageEntry::getName),
			new FinderColumn<>(
				"layoutUtilityPageEntry.", "type", "type_",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutUtilityPageEntry::getType));

		_collectionPersistenceFinderByG_LikeN_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeN_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name", "type_"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LikeN_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {"groupId", "name", "type_"}, false),
				_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE,
				_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE,
				LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layoutUtilityPageEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutUtilityPageEntry::getGroupId),
				new FinderColumn<>(
					"layoutUtilityPageEntry.", "name", FinderColumn.Type.STRING,
					"LIKE", true, true, LayoutUtilityPageEntry::getName),
				new ArrayableFinderColumn<>(
					"layoutUtilityPageEntry.", "type", "type_",
					FinderColumn.Type.STRING, "=", false, true, true,
					LayoutUtilityPageEntry::getType));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(
					LayoutUtilityPageEntry::getExternalReferenceCode),
				LayoutUtilityPageEntry::getGroupId),
			_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE, "",
			new FinderColumn<>(
				"layoutUtilityPageEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutUtilityPageEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"layoutUtilityPageEntry.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, LayoutUtilityPageEntry::getGroupId));

		LayoutUtilityPageEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LayoutUtilityPageEntryUtil.setPersistence(null);

		entityCache.removeCache(LayoutUtilityPageEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = LayoutUtilityPagePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = LayoutUtilityPagePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = LayoutUtilityPagePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		LayoutUtilityPageEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUTUTILITYPAGEENTRY =
		"SELECT layoutUtilityPageEntry FROM LayoutUtilityPageEntry layoutUtilityPageEntry";

	private static final String _SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE =
		"SELECT layoutUtilityPageEntry FROM LayoutUtilityPageEntry layoutUtilityPageEntry WHERE ";

	private static final String _SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE =
		"SELECT COUNT(layoutUtilityPageEntry) FROM LayoutUtilityPageEntry layoutUtilityPageEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LayoutUtilityPageEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutUtilityPageEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2048396676