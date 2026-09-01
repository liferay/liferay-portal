/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence.impl;

import com.liferay.layout.page.template.exception.DuplicateLayoutPageTemplateEntryExternalReferenceCodeException;
import com.liferay.layout.page.template.exception.NoSuchPageTemplateEntryException;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntryTable;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryImpl;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateEntryPersistence;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateEntryUtil;
import com.liferay.layout.page.template.service.persistence.impl.constants.LayoutPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
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
 * The persistence implementation for the layout page template entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = LayoutPageTemplateEntryPersistence.class)
public class LayoutPageTemplateEntryPersistenceImpl
	extends BasePersistenceImpl
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
	implements LayoutPageTemplateEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutPageTemplateEntryUtil</code> to access the layout page template entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutPageTemplateEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the layout page template entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByUuid_First(
			String uuid,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByUuid_First(
		String uuid,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of layout page template entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the layout page template entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchPageTemplateEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the layout page template entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the layout page template entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout page template entry that was removed
	 */
	@Override
	public LayoutPageTemplateEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchPageTemplateEntryException {

		LayoutPageTemplateEntry layoutPageTemplateEntry = findByUUID_G(
			uuid, groupId);

		return remove(layoutPageTemplateEntry);
	}

	/**
	 * Returns the number of layout page template entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the layout page template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of layout page template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByGroupId_First(
			long groupId,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByGroupId_First(
		long groupId,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupId}, groupId);
	}

	private CollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByLayoutPrototypeId;

	/**
	 * Returns an ordered range of all the layout page template entries where layoutPrototypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByLayoutPrototypeId(
		long layoutPrototypeId, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLayoutPrototypeId.find(
			finderCache, new Object[] {layoutPrototypeId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where layoutPrototypeId = &#63;.
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByLayoutPrototypeId_First(
			long layoutPrototypeId,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByLayoutPrototypeId.findFirst(
			finderCache, new Object[] {layoutPrototypeId}, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where layoutPrototypeId = &#63;.
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByLayoutPrototypeId_First(
		long layoutPrototypeId,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByLayoutPrototypeId.fetchFirst(
			finderCache, new Object[] {layoutPrototypeId}, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where layoutPrototypeId = &#63; from the database.
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 */
	@Override
	public void removeByLayoutPrototypeId(long layoutPrototypeId) {
		_collectionPersistenceFinderByLayoutPrototypeId.remove(
			finderCache, new Object[] {layoutPrototypeId});
	}

	/**
	 * Returns the number of layout page template entries where layoutPrototypeId = &#63;.
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByLayoutPrototypeId(long layoutPrototypeId) {
		return _collectionPersistenceFinderByLayoutPrototypeId.count(
			finderCache, new Object[] {layoutPrototypeId});
	}

	private UniquePersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_uniquePersistenceFinderByPlid;

	/**
	 * Returns the layout page template entry where plid = &#63; or throws a <code>NoSuchPageTemplateEntryException</code> if it could not be found.
	 *
	 * @param plid the plid
	 * @return the matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByPlid(long plid)
		throws NoSuchPageTemplateEntryException {

		return _uniquePersistenceFinderByPlid.find(
			finderCache, new Object[] {plid});
	}

	/**
	 * Returns the layout page template entry where plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByPlid(
		long plid, boolean useFinderCache) {

		return _uniquePersistenceFinderByPlid.fetch(
			finderCache, new Object[] {plid}, useFinderCache);
	}

	/**
	 * Removes the layout page template entry where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 * @return the layout page template entry that was removed
	 */
	@Override
	public LayoutPageTemplateEntry removeByPlid(long plid)
		throws NoSuchPageTemplateEntryException {

		LayoutPageTemplateEntry layoutPageTemplateEntry = findByPlid(plid);

		return remove(layoutPageTemplateEntry);
	}

	/**
	 * Returns the number of layout page template entries where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByPlid(long plid) {
		return _uniquePersistenceFinderByPlid.count(
			finderCache, new Object[] {plid});
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_L;

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_L(
		long groupId, long layoutPageTemplateCollectionId, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_L.find(
			finderCache, new Object[] {groupId, layoutPageTemplateCollectionId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_L_First(
			long groupId, long layoutPageTemplateCollectionId,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_L.findFirst(
			finderCache, new Object[] {groupId, layoutPageTemplateCollectionId},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_L_First(
		long groupId, long layoutPageTemplateCollectionId,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_L.fetchFirst(
			finderCache, new Object[] {groupId, layoutPageTemplateCollectionId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_L(
		long groupId, long layoutPageTemplateCollectionId, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_L.filterFind(
			finderCache, new Object[] {groupId, layoutPageTemplateCollectionId},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 */
	@Override
	public void removeByG_L(long groupId, long layoutPageTemplateCollectionId) {
		_collectionPersistenceFinderByG_L.remove(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_L(long groupId, long layoutPageTemplateCollectionId) {
		return _collectionPersistenceFinderByG_L.count(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_L(
		long groupId, long layoutPageTemplateCollectionId) {

		return _collectionPersistenceFinderByG_L.filterCount(
			finderCache, new Object[] {groupId, layoutPageTemplateCollectionId},
			groupId);
	}

	private UniquePersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_uniquePersistenceFinderByG_LPTEK;

	/**
	 * Returns the layout page template entry where groupId = &#63; and layoutPageTemplateEntryKey = &#63; or throws a <code>NoSuchPageTemplateEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryKey the layout page template entry key
	 * @return the matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_LPTEK(
			long groupId, String layoutPageTemplateEntryKey)
		throws NoSuchPageTemplateEntryException {

		return _uniquePersistenceFinderByG_LPTEK.find(
			finderCache, new Object[] {groupId, layoutPageTemplateEntryKey});
	}

	/**
	 * Returns the layout page template entry where groupId = &#63; and layoutPageTemplateEntryKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryKey the layout page template entry key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_LPTEK(
		long groupId, String layoutPageTemplateEntryKey,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_LPTEK.fetch(
			finderCache, new Object[] {groupId, layoutPageTemplateEntryKey},
			useFinderCache);
	}

	/**
	 * Removes the layout page template entry where groupId = &#63; and layoutPageTemplateEntryKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryKey the layout page template entry key
	 * @return the layout page template entry that was removed
	 */
	@Override
	public LayoutPageTemplateEntry removeByG_LPTEK(
			long groupId, String layoutPageTemplateEntryKey)
		throws NoSuchPageTemplateEntryException {

		LayoutPageTemplateEntry layoutPageTemplateEntry = findByG_LPTEK(
			groupId, layoutPageTemplateEntryKey);

		return remove(layoutPageTemplateEntry);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryKey the layout page template entry key
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_LPTEK(long groupId, String layoutPageTemplateEntryKey) {
		return _uniquePersistenceFinderByG_LPTEK.count(
			finderCache, new Object[] {groupId, layoutPageTemplateEntryKey});
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_N;

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_N(
		long groupId, String name, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_N.find(
			finderCache, new Object[] {groupId, name}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_N_First(
			long groupId, String name,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_N.findFirst(
			finderCache, new Object[] {groupId, name}, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_N_First(
		long groupId, String name,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_N.fetchFirst(
			finderCache, new Object[] {groupId, name}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_N(
		long groupId, String name, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_N.filterFind(
			finderCache, new Object[] {groupId, name}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 */
	@Override
	public void removeByG_N(long groupId, String name) {
		_collectionPersistenceFinderByG_N.remove(
			finderCache, new Object[] {groupId, name});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_N(long groupId, String name) {
		return _collectionPersistenceFinderByG_N.count(
			finderCache, new Object[] {groupId, name});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_N(long groupId, String name) {
		return _collectionPersistenceFinderByG_N.filterCount(
			finderCache, new Object[] {groupId, name}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_T;

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T.find(
			finderCache, new Object[] {groupId, new int[] {type}}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_T_First(
			long groupId, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_T.findFirst(
			finderCache, new Object[] {groupId, new int[] {type}},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_T_First(
		long groupId, int type,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_T.fetchFirst(
			finderCache, new Object[] {groupId, new int[] {type}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_T(
		long groupId, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_T.filterFind(
			finderCache, new Object[] {groupId, new int[] {type}}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_T(
		long groupId, int[] types, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_T.filterFind(
			finderCache, new Object[] {groupId, ArrayUtil.sortedUnique(types)},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int[] types, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T.find(
			finderCache, new Object[] {groupId, ArrayUtil.sortedUnique(types)},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 */
	@Override
	public void removeByG_T(long groupId, int type) {
		_collectionPersistenceFinderByG_T.remove(
			finderCache, new Object[] {groupId, new int[] {type}});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_T(long groupId, int type) {
		return _collectionPersistenceFinderByG_T.count(
			finderCache, new Object[] {groupId, new int[] {type}});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_T(long groupId, int[] types) {
		return _collectionPersistenceFinderByG_T.count(
			finderCache, new Object[] {groupId, ArrayUtil.sortedUnique(types)});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_T(long groupId, int type) {
		return _collectionPersistenceFinderByG_T.filterCount(
			finderCache, new Object[] {groupId, new int[] {type}}, groupId);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_T(long groupId, int[] types) {
		return _collectionPersistenceFinderByG_T.filterCount(
			finderCache, new Object[] {groupId, ArrayUtil.sortedUnique(types)},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_S;

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S.find(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_S_First(
			long groupId, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_S.findFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_S.fetchFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_S.filterFind(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and status = &#63; from the database.
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
	 * Returns the number of layout page template entries where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_S(long groupId, int status) {
		return _collectionPersistenceFinderByG_S.count(
			finderCache, new Object[] {groupId, status});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_S(long groupId, int status) {
		return _collectionPersistenceFinderByG_S.filterCount(
			finderCache, new Object[] {groupId, status}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_L_LikeN;

	/**
	 * Returns all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @return the matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name) {

		return findByG_L_LikeN(
			groupId, layoutPageTemplateCollectionId, name, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end) {

		return findByG_L_LikeN(
			groupId, layoutPageTemplateCollectionId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_L_LikeN(
			groupId, layoutPageTemplateCollectionId, name, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_L_LikeN.find(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, name}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_L_LikeN_First(
			long groupId, long layoutPageTemplateCollectionId, String name,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_L_LikeN.findFirst(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, name},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_L_LikeN_First(
		long groupId, long layoutPageTemplateCollectionId, String name,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_L_LikeN.fetchFirst(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, name},
			orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @return the matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name) {

		return filterFindByG_L_LikeN(
			groupId, layoutPageTemplateCollectionId, name, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end) {

		return filterFindByG_L_LikeN(
			groupId, layoutPageTemplateCollectionId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_L_LikeN.filterFind(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, name}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 */
	@Override
	public void removeByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name) {

		_collectionPersistenceFinderByG_L_LikeN.remove(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, name});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name) {

		return _collectionPersistenceFinderByG_L_LikeN.count(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, name});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name) {

		return _collectionPersistenceFinderByG_L_LikeN.filterCount(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, name},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_L_T;

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type, int start,
		int end, OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_L_T.find(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, type}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_L_T_First(
			long groupId, long layoutPageTemplateCollectionId, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_L_T.findFirst(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, type},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_L_T_First(
		long groupId, long layoutPageTemplateCollectionId, int type,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_L_T.fetchFirst(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, type},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type, int start,
		int end, OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_L_T.filterFind(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, type}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 */
	@Override
	public void removeByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type) {

		_collectionPersistenceFinderByG_L_T.remove(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, type});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type) {

		return _collectionPersistenceFinderByG_L_T.count(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, type});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type) {

		return _collectionPersistenceFinderByG_L_T.filterCount(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, type},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_L_S;

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_L_S.find(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_L_S_First(
			long groupId, long layoutPageTemplateCollectionId, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_L_S.findFirst(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, status},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_L_S_First(
		long groupId, long layoutPageTemplateCollectionId, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_L_S.fetchFirst(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_L_S.filterFind(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 */
	@Override
	public void removeByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status) {

		_collectionPersistenceFinderByG_L_S.remove(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, status});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status) {

		return _collectionPersistenceFinderByG_L_S.count(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, status});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status) {

		return _collectionPersistenceFinderByG_L_S.filterCount(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, status},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_N_T;

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_N_T(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_N_T.find(
			finderCache, new Object[] {groupId, name, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_N_T_First(
			long groupId, String name, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_N_T.findFirst(
			finderCache, new Object[] {groupId, name, type}, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_N_T_First(
		long groupId, String name, int type,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_N_T.fetchFirst(
			finderCache, new Object[] {groupId, name, type}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_N_T(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_N_T.filterFind(
			finderCache, new Object[] {groupId, name, type}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and name = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 */
	@Override
	public void removeByG_N_T(long groupId, String name, int type) {
		_collectionPersistenceFinderByG_N_T.remove(
			finderCache, new Object[] {groupId, name, type});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_N_T(long groupId, String name, int type) {
		return _collectionPersistenceFinderByG_N_T.count(
			finderCache, new Object[] {groupId, name, type});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_N_T(long groupId, String name, int type) {
		return _collectionPersistenceFinderByG_N_T.filterCount(
			finderCache, new Object[] {groupId, name, type}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_T_LikeN;

	/**
	 * Returns all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int type) {

		return findByG_T_LikeN(
			groupId, name, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int type, int start, int end) {

		return findByG_T_LikeN(groupId, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_T_LikeN(
			groupId, name, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T_LikeN.find(
			finderCache, new Object[] {groupId, name, new int[] {type}}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_T_LikeN_First(
			long groupId, String name, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_T_LikeN.findFirst(
			finderCache, new Object[] {groupId, name, new int[] {type}},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_T_LikeN_First(
		long groupId, String name, int type,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_T_LikeN.fetchFirst(
			finderCache, new Object[] {groupId, name, new int[] {type}},
			orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_T_LikeN(
		long groupId, String name, int type) {

		return filterFindByG_T_LikeN(
			groupId, name, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_T_LikeN(
		long groupId, String name, int type, int start, int end) {

		return filterFindByG_T_LikeN(groupId, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_T_LikeN(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_T_LikeN.filterFind(
			finderCache, new Object[] {groupId, name, new int[] {type}}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_T_LikeN(
		long groupId, String name, int[] types) {

		return filterFindByG_T_LikeN(
			groupId, name, types, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_T_LikeN(
		long groupId, String name, int[] types, int start, int end) {

		return filterFindByG_T_LikeN(groupId, name, types, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_T_LikeN(
		long groupId, String name, int[] types, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_T_LikeN.filterFind(
			finderCache,
			new Object[] {groupId, name, ArrayUtil.sortedUnique(types)}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int[] types) {

		return findByG_T_LikeN(
			groupId, name, types, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int[] types, int start, int end) {

		return findByG_T_LikeN(groupId, name, types, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int[] types, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_T_LikeN(
			groupId, name, types, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int[] types, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T_LikeN.find(
			finderCache,
			new Object[] {groupId, name, ArrayUtil.sortedUnique(types)}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 */
	@Override
	public void removeByG_T_LikeN(long groupId, String name, int type) {
		_collectionPersistenceFinderByG_T_LikeN.remove(
			finderCache, new Object[] {groupId, name, new int[] {type}});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_T_LikeN(long groupId, String name, int type) {
		return _collectionPersistenceFinderByG_T_LikeN.count(
			finderCache, new Object[] {groupId, name, new int[] {type}});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_T_LikeN(long groupId, String name, int[] types) {
		return _collectionPersistenceFinderByG_T_LikeN.count(
			finderCache,
			new Object[] {groupId, name, ArrayUtil.sortedUnique(types)});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_T_LikeN(long groupId, String name, int type) {
		return _collectionPersistenceFinderByG_T_LikeN.filterCount(
			finderCache, new Object[] {groupId, name, new int[] {type}},
			groupId);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_T_LikeN(long groupId, String name, int[] types) {
		return _collectionPersistenceFinderByG_T_LikeN.filterCount(
			finderCache,
			new Object[] {groupId, name, ArrayUtil.sortedUnique(types)},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_T_S;

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T_S.find(
			finderCache, new Object[] {groupId, new int[] {type}, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_T_S_First(
			long groupId, int type, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_T_S.findFirst(
			finderCache, new Object[] {groupId, new int[] {type}, status},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_T_S_First(
		long groupId, int type, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_T_S.fetchFirst(
			finderCache, new Object[] {groupId, new int[] {type}, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_T_S(
		long groupId, int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_T_S.filterFind(
			finderCache, new Object[] {groupId, new int[] {type}, status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_T_S(
		long groupId, int[] types, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_T_S.filterFind(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(types), status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int[] types, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T_S.find(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(types), status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 */
	@Override
	public void removeByG_T_S(long groupId, int type, int status) {
		_collectionPersistenceFinderByG_T_S.remove(
			finderCache, new Object[] {groupId, new int[] {type}, status});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_T_S(long groupId, int type, int status) {
		return _collectionPersistenceFinderByG_T_S.count(
			finderCache, new Object[] {groupId, new int[] {type}, status});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_T_S(long groupId, int[] types, int status) {
		return _collectionPersistenceFinderByG_T_S.count(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(types), status});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_T_S(long groupId, int type, int status) {
		return _collectionPersistenceFinderByG_T_S.filterCount(
			finderCache, new Object[] {groupId, new int[] {type}, status},
			groupId);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_T_S(long groupId, int[] types, int status) {
		return _collectionPersistenceFinderByG_T_S.filterCount(
			finderCache,
			new Object[] {groupId, ArrayUtil.sortedUnique(types), status},
			groupId);
	}

	private UniquePersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_uniquePersistenceFinderByG_L_N_T;

	/**
	 * Returns the layout page template entry where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or throws a <code>NoSuchPageTemplateEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_L_N_T(
			long groupId, long layoutPageTemplateCollectionId, String name,
			int type)
		throws NoSuchPageTemplateEntryException {

		return _uniquePersistenceFinderByG_L_N_T.find(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, name, type});
	}

	/**
	 * Returns the layout page template entry where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_L_N_T(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int type, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_L_N_T.fetch(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, name, type},
			useFinderCache);
	}

	/**
	 * Removes the layout page template entry where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the layout page template entry that was removed
	 */
	@Override
	public LayoutPageTemplateEntry removeByG_L_N_T(
			long groupId, long layoutPageTemplateCollectionId, String name,
			int type)
		throws NoSuchPageTemplateEntryException {

		LayoutPageTemplateEntry layoutPageTemplateEntry = findByG_L_N_T(
			groupId, layoutPageTemplateCollectionId, name, type);

		return remove(layoutPageTemplateEntry);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_L_N_T(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int type) {

		return _uniquePersistenceFinderByG_L_N_T.count(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionId, name, type});
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_L_LikeN_S;

	/**
	 * Returns all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status) {

		return findByG_L_LikeN_S(
			groupId, layoutPageTemplateCollectionId, name, status,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end) {

		return findByG_L_LikeN_S(
			groupId, layoutPageTemplateCollectionId, name, status, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_L_LikeN_S(
			groupId, layoutPageTemplateCollectionId, name, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_L_LikeN_S.find(
			finderCache,
			new Object[] {
				groupId, layoutPageTemplateCollectionId, name, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_L_LikeN_S_First(
			long groupId, long layoutPageTemplateCollectionId, String name,
			int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_L_LikeN_S.findFirst(
			finderCache,
			new Object[] {
				groupId, layoutPageTemplateCollectionId, name, status
			},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_L_LikeN_S_First(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_L_LikeN_S.fetchFirst(
			finderCache,
			new Object[] {
				groupId, layoutPageTemplateCollectionId, name, status
			},
			orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status) {

		return filterFindByG_L_LikeN_S(
			groupId, layoutPageTemplateCollectionId, name, status,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end) {

		return filterFindByG_L_LikeN_S(
			groupId, layoutPageTemplateCollectionId, name, status, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_L_LikeN_S.filterFind(
			finderCache,
			new Object[] {
				groupId, layoutPageTemplateCollectionId, name, status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 */
	@Override
	public void removeByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status) {

		_collectionPersistenceFinderByG_L_LikeN_S.remove(
			finderCache,
			new Object[] {
				groupId, layoutPageTemplateCollectionId, name, status
			});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status) {

		return _collectionPersistenceFinderByG_L_LikeN_S.count(
			finderCache,
			new Object[] {
				groupId, layoutPageTemplateCollectionId, name, status
			});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status) {

		return _collectionPersistenceFinderByG_L_LikeN_S.filterCount(
			finderCache,
			new Object[] {
				groupId, layoutPageTemplateCollectionId, name, status
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_C_C_T;

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C_T.find(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, type
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_C_C_T_First(
			long groupId, long classNameId, String classTypeKey, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_C_C_T.findFirst(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, type
			},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_C_C_T_First(
		long groupId, long classNameId, String classTypeKey, int type,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_T.fetchFirst(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, type
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_T.filterFind(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, type
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_C_C_T(
		long[] groupIds, long classNameId, String classTypeKey, int type,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_C_C_T.filterFind(
			finderCache,
			new Object[] {groupIds, classNameId, classTypeKey, type}, start,
			end, orderByComparator, groupIds);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_T(
		long[] groupIds, long classNameId, String classTypeKey, int type,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C_T.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), classNameId, classTypeKey,
				type
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 */
	@Override
	public void removeByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type) {

		_collectionPersistenceFinderByG_C_C_T.remove(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, type
			});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type) {

		return _collectionPersistenceFinderByG_C_C_T.count(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, type
			});
	}

	/**
	 * Returns the number of layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_C_C_T(
		long[] groupIds, long classNameId, String classTypeKey, int type) {

		return _collectionPersistenceFinderByG_C_C_T.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), classNameId, classTypeKey,
				type
			});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type) {

		return _collectionPersistenceFinderByG_C_C_T.filterCount(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, type
			},
			groupId);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C_T(
		long[] groupIds, long classNameId, String classTypeKey, int type) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_C_C_T.filterCount(
			finderCache,
			new Object[] {groupIds, classNameId, classTypeKey, type}, groupIds);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_C_C_D;

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C_D.find(
			finderCache,
			new Object[] {groupId, classNameId, classTypeKey, defaultTemplate},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_C_C_D_First(
			long groupId, long classNameId, String classTypeKey,
			boolean defaultTemplate,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_C_C_D.findFirst(
			finderCache,
			new Object[] {groupId, classNameId, classTypeKey, defaultTemplate},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_C_C_D_First(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_D.fetchFirst(
			finderCache,
			new Object[] {groupId, classNameId, classTypeKey, defaultTemplate},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_D.filterFind(
			finderCache,
			new Object[] {groupId, classNameId, classTypeKey, defaultTemplate},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 */
	@Override
	public void removeByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate) {

		_collectionPersistenceFinderByG_C_C_D.remove(
			finderCache,
			new Object[] {groupId, classNameId, classTypeKey, defaultTemplate});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate) {

		return _collectionPersistenceFinderByG_C_C_D.count(
			finderCache,
			new Object[] {groupId, classNameId, classTypeKey, defaultTemplate});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate) {

		return _collectionPersistenceFinderByG_C_C_D.filterCount(
			finderCache,
			new Object[] {groupId, classNameId, classTypeKey, defaultTemplate},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_C_T_D;

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_T_D.find(
			finderCache,
			new Object[] {groupId, classNameId, type, defaultTemplate}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_C_T_D_First(
			long groupId, long classNameId, int type, boolean defaultTemplate,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_C_T_D.findFirst(
			finderCache,
			new Object[] {groupId, classNameId, type, defaultTemplate},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_C_T_D_First(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_T_D.fetchFirst(
			finderCache,
			new Object[] {groupId, classNameId, type, defaultTemplate},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_T_D.filterFind(
			finderCache,
			new Object[] {groupId, classNameId, type, defaultTemplate}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 */
	@Override
	public void removeByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate) {

		_collectionPersistenceFinderByG_C_T_D.remove(
			finderCache,
			new Object[] {groupId, classNameId, type, defaultTemplate});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate) {

		return _collectionPersistenceFinderByG_C_T_D.count(
			finderCache,
			new Object[] {groupId, classNameId, type, defaultTemplate});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate) {

		return _collectionPersistenceFinderByG_C_T_D.filterCount(
			finderCache,
			new Object[] {groupId, classNameId, type, defaultTemplate},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_T_LikeN_S;

	/**
	 * Returns all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int type, int status) {

		return findByG_T_LikeN_S(
			groupId, name, type, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int type, int status, int start, int end) {

		return findByG_T_LikeN_S(groupId, name, type, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_T_LikeN_S(
			groupId, name, type, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T_LikeN_S.find(
			finderCache, new Object[] {groupId, name, new int[] {type}, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_T_LikeN_S_First(
			long groupId, String name, int type, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_T_LikeN_S.findFirst(
			finderCache, new Object[] {groupId, name, new int[] {type}, status},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_T_LikeN_S_First(
		long groupId, String name, int type, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_T_LikeN_S.fetchFirst(
			finderCache, new Object[] {groupId, name, new int[] {type}, status},
			orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_T_LikeN_S(
		long groupId, String name, int type, int status) {

		return filterFindByG_T_LikeN_S(
			groupId, name, type, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_T_LikeN_S(
		long groupId, String name, int type, int status, int start, int end) {

		return filterFindByG_T_LikeN_S(
			groupId, name, type, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_T_LikeN_S(
		long groupId, String name, int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_T_LikeN_S.filterFind(
			finderCache, new Object[] {groupId, name, new int[] {type}, status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_T_LikeN_S(
		long groupId, String name, int[] types, int status) {

		return filterFindByG_T_LikeN_S(
			groupId, name, types, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_T_LikeN_S(
		long groupId, String name, int[] types, int status, int start,
		int end) {

		return filterFindByG_T_LikeN_S(
			groupId, name, types, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_T_LikeN_S(
		long groupId, String name, int[] types, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_T_LikeN_S.filterFind(
			finderCache,
			new Object[] {groupId, name, ArrayUtil.sortedUnique(types), status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int[] types, int status) {

		return findByG_T_LikeN_S(
			groupId, name, types, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int[] types, int status, int start,
		int end) {

		return findByG_T_LikeN_S(
			groupId, name, types, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int[] types, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_T_LikeN_S(
			groupId, name, types, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int[] types, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T_LikeN_S.find(
			finderCache,
			new Object[] {groupId, name, ArrayUtil.sortedUnique(types), status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 */
	@Override
	public void removeByG_T_LikeN_S(
		long groupId, String name, int type, int status) {

		_collectionPersistenceFinderByG_T_LikeN_S.remove(
			finderCache,
			new Object[] {groupId, name, new int[] {type}, status});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_T_LikeN_S(
		long groupId, String name, int type, int status) {

		return _collectionPersistenceFinderByG_T_LikeN_S.count(
			finderCache,
			new Object[] {groupId, name, new int[] {type}, status});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_T_LikeN_S(
		long groupId, String name, int[] types, int status) {

		return _collectionPersistenceFinderByG_T_LikeN_S.count(
			finderCache,
			new Object[] {
				groupId, name, ArrayUtil.sortedUnique(types), status
			});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_T_LikeN_S(
		long groupId, String name, int type, int status) {

		return _collectionPersistenceFinderByG_T_LikeN_S.filterCount(
			finderCache, new Object[] {groupId, name, new int[] {type}, status},
			groupId);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_T_LikeN_S(
		long groupId, String name, int[] types, int status) {

		return _collectionPersistenceFinderByG_T_LikeN_S.filterCount(
			finderCache,
			new Object[] {groupId, name, ArrayUtil.sortedUnique(types), status},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_T_D_S;

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status, int start,
		int end, OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T_D_S.find(
			finderCache, new Object[] {groupId, type, defaultTemplate, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_T_D_S_First(
			long groupId, int type, boolean defaultTemplate, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_T_D_S.findFirst(
			finderCache, new Object[] {groupId, type, defaultTemplate, status},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_T_D_S_First(
		long groupId, int type, boolean defaultTemplate, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_T_D_S.fetchFirst(
			finderCache, new Object[] {groupId, type, defaultTemplate, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status, int start,
		int end, OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_T_D_S.filterFind(
			finderCache, new Object[] {groupId, type, defaultTemplate, status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 */
	@Override
	public void removeByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status) {

		_collectionPersistenceFinderByG_T_D_S.remove(
			finderCache, new Object[] {groupId, type, defaultTemplate, status});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status) {

		return _collectionPersistenceFinderByG_T_D_S.count(
			finderCache, new Object[] {groupId, type, defaultTemplate, status});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status) {

		return _collectionPersistenceFinderByG_T_D_S.filterCount(
			finderCache, new Object[] {groupId, type, defaultTemplate, status},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_C_C_LikeN_T;

	/**
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type) {

		return findByG_C_C_LikeN_T(
			groupId, classNameId, classTypeKey, name, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int start, int end) {

		return findByG_C_C_LikeN_T(
			groupId, classNameId, classTypeKey, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_C_C_LikeN_T(
			groupId, classNameId, classTypeKey, name, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C_LikeN_T.find(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, name, type
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_C_C_LikeN_T_First(
			long groupId, long classNameId, String classTypeKey, String name,
			int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_C_C_LikeN_T.findFirst(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, name, type
			},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_C_C_LikeN_T_First(
		long groupId, long classNameId, String classTypeKey, String name,
		int type,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_LikeN_T.fetchFirst(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, name, type
			},
			orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type) {

		return filterFindByG_C_C_LikeN_T(
			groupId, classNameId, classTypeKey, name, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int start, int end) {

		return filterFindByG_C_C_LikeN_T(
			groupId, classNameId, classTypeKey, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_LikeN_T.filterFind(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, name, type
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type) {

		return filterFindByG_C_C_LikeN_T(
			groupIds, classNameId, classTypeKey, name, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int start, int end) {

		return filterFindByG_C_C_LikeN_T(
			groupIds, classNameId, classTypeKey, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_C_C_LikeN_T.filterFind(
			finderCache,
			new Object[] {groupIds, classNameId, classTypeKey, name, type},
			start, end, orderByComparator, groupIds);
	}

	/**
	 * Returns all the layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type) {

		return findByG_C_C_LikeN_T(
			groupIds, classNameId, classTypeKey, name, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int start, int end) {

		return findByG_C_C_LikeN_T(
			groupIds, classNameId, classTypeKey, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_C_C_LikeN_T(
			groupIds, classNameId, classTypeKey, name, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C_LikeN_T.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), classNameId, classTypeKey,
				name, type
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 */
	@Override
	public void removeByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type) {

		_collectionPersistenceFinderByG_C_C_LikeN_T.remove(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, name, type
			});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type) {

		return _collectionPersistenceFinderByG_C_C_LikeN_T.count(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, name, type
			});
	}

	/**
	 * Returns the number of layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_C_C_LikeN_T(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type) {

		return _collectionPersistenceFinderByG_C_C_LikeN_T.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), classNameId, classTypeKey,
				name, type
			});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type) {

		return _collectionPersistenceFinderByG_C_C_LikeN_T.filterCount(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, name, type
			},
			groupId);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C_LikeN_T(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_C_C_LikeN_T.filterCount(
			finderCache,
			new Object[] {groupIds, classNameId, classTypeKey, name, type},
			groupIds);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_C_C_T_S;

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C_T_S.find(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, type, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_C_C_T_S_First(
			long groupId, long classNameId, String classTypeKey, int type,
			int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_C_C_T_S.findFirst(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, type, status
			},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_C_C_T_S_First(
		long groupId, long classNameId, String classTypeKey, int type,
		int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_T_S.fetchFirst(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, type, status
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_T_S.filterFind(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, type, status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_C_C_T_S(
		long[] groupIds, long classNameId, String classTypeKey, int type,
		int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_C_C_T_S.filterFind(
			finderCache,
			new Object[] {groupIds, classNameId, classTypeKey, type, status},
			start, end, orderByComparator, groupIds);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_T_S(
		long[] groupIds, long classNameId, String classTypeKey, int type,
		int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C_T_S.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), classNameId, classTypeKey,
				type, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 */
	@Override
	public void removeByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status) {

		_collectionPersistenceFinderByG_C_C_T_S.remove(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, type, status
			});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status) {

		return _collectionPersistenceFinderByG_C_C_T_S.count(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, type, status
			});
	}

	/**
	 * Returns the number of layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_C_C_T_S(
		long[] groupIds, long classNameId, String classTypeKey, int type,
		int status) {

		return _collectionPersistenceFinderByG_C_C_T_S.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), classNameId, classTypeKey,
				type, status
			});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status) {

		return _collectionPersistenceFinderByG_C_C_T_S.filterCount(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, type, status
			},
			groupId);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C_T_S(
		long[] groupIds, long classNameId, String classTypeKey, int type,
		int status) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_C_C_T_S.filterCount(
			finderCache,
			new Object[] {groupIds, classNameId, classTypeKey, type, status},
			groupIds);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_C_C_D_S;

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C_D_S.find(
			finderCache,
			new Object[] {
				groupId, classNameId, classTypeKey, defaultTemplate, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_C_C_D_S_First(
			long groupId, long classNameId, String classTypeKey,
			boolean defaultTemplate, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_C_C_D_S.findFirst(
			finderCache,
			new Object[] {
				groupId, classNameId, classTypeKey, defaultTemplate, status
			},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_C_C_D_S_First(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_D_S.fetchFirst(
			finderCache,
			new Object[] {
				groupId, classNameId, classTypeKey, defaultTemplate, status
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_D_S.filterFind(
			finderCache,
			new Object[] {
				groupId, classNameId, classTypeKey, defaultTemplate, status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 */
	@Override
	public void removeByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status) {

		_collectionPersistenceFinderByG_C_C_D_S.remove(
			finderCache,
			new Object[] {
				groupId, classNameId, classTypeKey, defaultTemplate, status
			});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status) {

		return _collectionPersistenceFinderByG_C_C_D_S.count(
			finderCache,
			new Object[] {
				groupId, classNameId, classTypeKey, defaultTemplate, status
			});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status) {

		return _collectionPersistenceFinderByG_C_C_D_S.filterCount(
			finderCache,
			new Object[] {
				groupId, classNameId, classTypeKey, defaultTemplate, status
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_collectionPersistenceFinderByG_C_C_LikeN_T_S;

	/**
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status) {

		return findByG_C_C_LikeN_T_S(
			groupId, classNameId, classTypeKey, name, type, status,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end) {

		return findByG_C_C_LikeN_T_S(
			groupId, classNameId, classTypeKey, name, type, status, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_C_C_LikeN_T_S(
			groupId, classNameId, classTypeKey, name, type, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C_LikeN_T_S.find(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, name, type,
				status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByG_C_C_LikeN_T_S_First(
			long groupId, long classNameId, String classTypeKey, String name,
			int type, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException {

		return _collectionPersistenceFinderByG_C_C_LikeN_T_S.findFirst(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, name, type,
				status
			},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByG_C_C_LikeN_T_S_First(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_LikeN_T_S.fetchFirst(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, name, type,
				status
			},
			orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status) {

		return filterFindByG_C_C_LikeN_T_S(
			groupId, classNameId, classTypeKey, name, type, status,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end) {

		return filterFindByG_C_C_LikeN_T_S(
			groupId, classNameId, classTypeKey, name, type, status, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_LikeN_T_S.filterFind(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, name, type,
				status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T_S(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int status) {

		return filterFindByG_C_C_LikeN_T_S(
			groupIds, classNameId, classTypeKey, name, type, status,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T_S(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end) {

		return filterFindByG_C_C_LikeN_T_S(
			groupIds, classNameId, classTypeKey, name, type, status, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T_S(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_C_C_LikeN_T_S.filterFind(
			finderCache,
			new Object[] {
				groupIds, classNameId, classTypeKey, name, type, status
			},
			start, end, orderByComparator, groupIds);
	}

	/**
	 * Returns all the layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int status) {

		return findByG_C_C_LikeN_T_S(
			groupIds, classNameId, classTypeKey, name, type, status,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end) {

		return findByG_C_C_LikeN_T_S(
			groupIds, classNameId, classTypeKey, name, type, status, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_C_C_LikeN_T_S(
			groupIds, classNameId, classTypeKey, name, type, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	@Override
	public List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C_LikeN_T_S.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), classNameId, classTypeKey,
				name, type, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 */
	@Override
	public void removeByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status) {

		_collectionPersistenceFinderByG_C_C_LikeN_T_S.remove(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, name, type,
				status
			});
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status) {

		return _collectionPersistenceFinderByG_C_C_LikeN_T_S.count(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, name, type,
				status
			});
	}

	/**
	 * Returns the number of layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByG_C_C_LikeN_T_S(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int status) {

		return _collectionPersistenceFinderByG_C_C_LikeN_T_S.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), classNameId, classTypeKey,
				name, type, status
			});
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status) {

		return _collectionPersistenceFinderByG_C_C_LikeN_T_S.filterCount(
			finderCache,
			new Object[] {
				new long[] {groupId}, classNameId, classTypeKey, name, type,
				status
			},
			groupId);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C_LikeN_T_S(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int status) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_C_C_LikeN_T_S.filterCount(
			finderCache,
			new Object[] {
				groupIds, classNameId, classTypeKey, name, type, status
			},
			groupIds);
	}

	private UniquePersistenceFinder
		<LayoutPageTemplateEntry, NoSuchPageTemplateEntryException>
			_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the layout page template entry where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchPageTemplateEntryException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the layout page template entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the layout page template entry where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout page template entry that was removed
	 */
	@Override
	public LayoutPageTemplateEntry removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchPageTemplateEntryException {

		LayoutPageTemplateEntry layoutPageTemplateEntry = findByERC_G(
			externalReferenceCode, groupId);

		return remove(layoutPageTemplateEntry);
	}

	/**
	 * Returns the number of layout page template entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layout page template entries
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public LayoutPageTemplateEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutPageTemplateEntry.class);

		setModelImplClass(LayoutPageTemplateEntryImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutPageTemplateEntryTable.INSTANCE);
	}

	/**
	 * Creates a new layout page template entry with the primary key. Does not add the layout page template entry to the database.
	 *
	 * @param layoutPageTemplateEntryId the primary key for the new layout page template entry
	 * @return the new layout page template entry
	 */
	@Override
	public LayoutPageTemplateEntry create(long layoutPageTemplateEntryId) {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			new LayoutPageTemplateEntryImpl();

		layoutPageTemplateEntry.setNew(true);
		layoutPageTemplateEntry.setPrimaryKey(layoutPageTemplateEntryId);

		String uuid = PortalUUIDUtil.generate();

		layoutPageTemplateEntry.setUuid(uuid);

		layoutPageTemplateEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return layoutPageTemplateEntry;
	}

	/**
	 * Removes the layout page template entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the layout page template entry
	 * @return the layout page template entry that was removed
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateEntry remove(long layoutPageTemplateEntryId)
		throws NoSuchPageTemplateEntryException {

		return remove((Serializable)layoutPageTemplateEntryId);
	}

	@Override
	protected LayoutPageTemplateEntry removeImpl(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutPageTemplateEntry)) {
				layoutPageTemplateEntry = (LayoutPageTemplateEntry)session.get(
					LayoutPageTemplateEntryImpl.class,
					layoutPageTemplateEntry.getPrimaryKeyObj());
			}

			if ((layoutPageTemplateEntry != null) &&
				ctPersistenceHelper.isRemove(layoutPageTemplateEntry)) {

				session.delete(layoutPageTemplateEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutPageTemplateEntry != null) {
			clearCache(layoutPageTemplateEntry);
		}

		return layoutPageTemplateEntry;
	}

	@Override
	public LayoutPageTemplateEntry updateImpl(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		boolean isNew = layoutPageTemplateEntry.isNew();

		if (!(layoutPageTemplateEntry instanceof
				LayoutPageTemplateEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(layoutPageTemplateEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutPageTemplateEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutPageTemplateEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutPageTemplateEntry implementation " +
					layoutPageTemplateEntry.getClass());
		}

		LayoutPageTemplateEntryModelImpl layoutPageTemplateEntryModelImpl =
			(LayoutPageTemplateEntryModelImpl)layoutPageTemplateEntry;

		if (Validator.isNull(layoutPageTemplateEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			layoutPageTemplateEntry.setUuid(uuid);
		}

		if (Validator.isNull(
				layoutPageTemplateEntry.getExternalReferenceCode())) {

			layoutPageTemplateEntry.setExternalReferenceCode(
				layoutPageTemplateEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					layoutPageTemplateEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					layoutPageTemplateEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = layoutPageTemplateEntry.getCompanyId();

					long groupId = layoutPageTemplateEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = layoutPageTemplateEntry.getPrimaryKey();
					}

					try {
						layoutPageTemplateEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								LayoutPageTemplateEntry.class.getName(),
								classPK, ContentTypes.TEXT_HTML,
								Sanitizer.MODE_ALL,
								layoutPageTemplateEntry.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			LayoutPageTemplateEntry ercLayoutPageTemplateEntry = fetchByERC_G(
				layoutPageTemplateEntry.getExternalReferenceCode(),
				layoutPageTemplateEntry.getGroupId());

			if (isNew) {
				if (ercLayoutPageTemplateEntry != null) {
					throw new DuplicateLayoutPageTemplateEntryExternalReferenceCodeException(
						"Duplicate layout page template entry with external reference code " +
							layoutPageTemplateEntry.getExternalReferenceCode() +
								" and group " +
									layoutPageTemplateEntry.getGroupId());
				}
			}
			else {
				if ((ercLayoutPageTemplateEntry != null) &&
					(layoutPageTemplateEntry.getLayoutPageTemplateEntryId() !=
						ercLayoutPageTemplateEntry.
							getLayoutPageTemplateEntryId())) {

					throw new DuplicateLayoutPageTemplateEntryExternalReferenceCodeException(
						"Duplicate layout page template entry with external reference code " +
							layoutPageTemplateEntry.getExternalReferenceCode() +
								" and group " +
									layoutPageTemplateEntry.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutPageTemplateEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutPageTemplateEntry.setCreateDate(date);
			}
			else {
				layoutPageTemplateEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutPageTemplateEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutPageTemplateEntry.setModifiedDate(date);
			}
			else {
				layoutPageTemplateEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(layoutPageTemplateEntry)) {
				if (!isNew) {
					session.evict(
						LayoutPageTemplateEntryImpl.class,
						layoutPageTemplateEntry.getPrimaryKeyObj());
				}

				session.save(layoutPageTemplateEntry);
			}
			else {
				layoutPageTemplateEntry =
					(LayoutPageTemplateEntry)session.merge(
						layoutPageTemplateEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layoutPageTemplateEntry, false);

		if (isNew) {
			layoutPageTemplateEntry.setNew(false);
		}

		layoutPageTemplateEntry.resetOriginalValues();

		return layoutPageTemplateEntry;
	}

	/**
	 * Returns the layout page template entry with the primary key or throws a <code>NoSuchPageTemplateEntryException</code> if it could not be found.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the layout page template entry
	 * @return the layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateEntry findByPrimaryKey(
			long layoutPageTemplateEntryId)
		throws NoSuchPageTemplateEntryException {

		return findByPrimaryKey((Serializable)layoutPageTemplateEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the layout page template entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the layout page template entry
	 * @return the layout page template entry, or <code>null</code> if a layout page template entry with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateEntry fetchByPrimaryKey(
		long layoutPageTemplateEntryId) {

		return fetchByPrimaryKey((Serializable)layoutPageTemplateEntryId);
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
		return "layoutPageTemplateEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTPAGETEMPLATEENTRY;
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
		return LayoutPageTemplateEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LayoutPageTemplateEntry";
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
		ctMergeColumnNames.add("layoutPageTemplateCollectionId");
		ctMergeColumnNames.add("layoutPageTemplateEntryKey");
		ctStrictColumnNames.add("classNameId");
		ctMergeColumnNames.add("classTypeId");
		ctMergeColumnNames.add("classTypeKey");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("previewFileEntryId");
		ctMergeColumnNames.add("defaultTemplate");
		ctMergeColumnNames.add("layoutPrototypeId");
		ctMergeColumnNames.add("plid");
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
			CTColumnResolutionType.PK,
			Collections.singleton("layoutPageTemplateEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"plid"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "layoutPageTemplateEntryKey"});

		_uniqueIndexColumnNames.add(
			new String[] {
				"groupId", "layoutPageTemplateCollectionId", "name", "type_"
			});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the layout page template entry persistence.
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
			_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
			_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
			LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"layoutPageTemplateEntry.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutPageTemplateEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(LayoutPageTemplateEntry::getUuid),
				LayoutPageTemplateEntry::getGroupId),
			_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE, "",
			new FinderColumn<>(
				"layoutPageTemplateEntry.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutPageTemplateEntry::getUuid),
			new FinderColumn<>(
				"layoutPageTemplateEntry.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, LayoutPageTemplateEntry::getGroupId));

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
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutPageTemplateEntry::getUuid),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getCompanyId));

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
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getGroupId));

		_collectionPersistenceFinderByLayoutPrototypeId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLayoutPrototypeId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"layoutPrototypeId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLayoutPrototypeId",
					new String[] {Long.class.getName()},
					new String[] {"layoutPrototypeId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLayoutPrototypeId",
					new String[] {Long.class.getName()},
					new String[] {"layoutPrototypeId"}, false),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "layoutPrototypeId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getLayoutPrototypeId));

		_uniquePersistenceFinderByPlid = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByPlid",
				new String[] {Long.class.getName()}, new String[] {"plid"}, 0,
				0, false, LayoutPageTemplateEntry::getPlid),
			_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE, "",
			new FinderColumn<>(
				"layoutPageTemplateEntry.", "plid", FinderColumn.Type.LONG, "=",
				true, true, LayoutPageTemplateEntry::getPlid));

		_collectionPersistenceFinderByG_L =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_L",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "layoutPageTemplateCollectionId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_L",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "layoutPageTemplateCollectionId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_L",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "layoutPageTemplateCollectionId"},
					false),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.",
					"layoutPageTemplateCollectionId", FinderColumn.Type.LONG,
					"=", true, true,
					LayoutPageTemplateEntry::
						getLayoutPageTemplateCollectionId));

		_uniquePersistenceFinderByG_LPTEK = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_LPTEK",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "layoutPageTemplateEntryKey"}, 0, 2,
				false, LayoutPageTemplateEntry::getGroupId,
				convertNullFunction(
					LayoutPageTemplateEntry::getLayoutPageTemplateEntryKey)),
			_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE, "",
			new FinderColumn<>(
				"layoutPageTemplateEntry.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, LayoutPageTemplateEntry::getGroupId),
			new FinderColumn<>(
				"layoutPageTemplateEntry.", "layoutPageTemplateEntryKey",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutPageTemplateEntry::getLayoutPageTemplateEntryKey));

		_collectionPersistenceFinderByG_N =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "name"}, 0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_N",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "name"}, 0, 2, false, null),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "name",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutPageTemplateEntry::getName));

		_collectionPersistenceFinderByG_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_T",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "type_"}, false),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getGroupId),
				new ArrayableFinderColumn<>(
					"layoutPageTemplateEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", false, true, true,
					LayoutPageTemplateEntry::getType));

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
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateEntry::getStatus));

		_collectionPersistenceFinderByG_L_LikeN =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_L_LikeN",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "layoutPageTemplateCollectionId", "name"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_L_LikeN",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName()
					},
					new String[] {
						"groupId", "layoutPageTemplateCollectionId", "name"
					},
					false),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.",
					"layoutPageTemplateCollectionId", FinderColumn.Type.LONG,
					"=", true, true,
					LayoutPageTemplateEntry::getLayoutPageTemplateCollectionId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "name",
					FinderColumn.Type.STRING, "LIKE", true, true,
					LayoutPageTemplateEntry::getName));

		_collectionPersistenceFinderByG_L_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_L_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "layoutPageTemplateCollectionId", "type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_L_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "layoutPageTemplateCollectionId", "type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_L_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "layoutPageTemplateCollectionId", "type_"
					},
					false),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.",
					"layoutPageTemplateCollectionId", FinderColumn.Type.LONG,
					"=", true, true,
					LayoutPageTemplateEntry::getLayoutPageTemplateCollectionId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateEntry::getType));

		_collectionPersistenceFinderByG_L_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_L_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "layoutPageTemplateCollectionId", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_L_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "layoutPageTemplateCollectionId", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_L_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "layoutPageTemplateCollectionId", "status"
					},
					false),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.",
					"layoutPageTemplateCollectionId", FinderColumn.Type.LONG,
					"=", true, true,
					LayoutPageTemplateEntry::getLayoutPageTemplateCollectionId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateEntry::getStatus));

		_collectionPersistenceFinderByG_N_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "name", "type_"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_N_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "name", "type_"}, 0, 2, false,
					null),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "name",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutPageTemplateEntry::getName),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateEntry::getType));

		_collectionPersistenceFinderByG_T_LikeN =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T_LikeN",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name", "type_"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_T_LikeN",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "name", "type_"}, false),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "name",
					FinderColumn.Type.STRING, "LIKE", true, true,
					LayoutPageTemplateEntry::getName),
				new ArrayableFinderColumn<>(
					"layoutPageTemplateEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", false, true, true,
					LayoutPageTemplateEntry::getType));

		_collectionPersistenceFinderByG_T_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T_S",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "type_", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T_S",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "type_", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_T_S",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "type_", "status"}, false),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getGroupId),
				new ArrayableFinderColumn<>(
					"layoutPageTemplateEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", false, true, true,
					LayoutPageTemplateEntry::getType),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateEntry::getStatus));

		_uniquePersistenceFinderByG_L_N_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_L_N_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName(), Integer.class.getName()
				},
				new String[] {
					"groupId", "layoutPageTemplateCollectionId", "name", "type_"
				},
				0, 4, false, LayoutPageTemplateEntry::getGroupId,
				LayoutPageTemplateEntry::getLayoutPageTemplateCollectionId,
				convertNullFunction(LayoutPageTemplateEntry::getName),
				LayoutPageTemplateEntry::getType),
			_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE, "",
			new FinderColumn<>(
				"layoutPageTemplateEntry.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, LayoutPageTemplateEntry::getGroupId),
			new FinderColumn<>(
				"layoutPageTemplateEntry.", "layoutPageTemplateCollectionId",
				FinderColumn.Type.LONG, "=", true, true,
				LayoutPageTemplateEntry::getLayoutPageTemplateCollectionId),
			new FinderColumn<>(
				"layoutPageTemplateEntry.", "name", FinderColumn.Type.STRING,
				"=", true, true, LayoutPageTemplateEntry::getName),
			new FinderColumn<>(
				"layoutPageTemplateEntry.", "type", "type_",
				FinderColumn.Type.INTEGER, "=", true, true,
				LayoutPageTemplateEntry::getType));

		_collectionPersistenceFinderByG_L_LikeN_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_L_LikeN_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "layoutPageTemplateCollectionId", "name",
						"status"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_L_LikeN_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "layoutPageTemplateCollectionId", "name",
						"status"
					},
					false),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.",
					"layoutPageTemplateCollectionId", FinderColumn.Type.LONG,
					"=", true, true,
					LayoutPageTemplateEntry::getLayoutPageTemplateCollectionId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "name",
					FinderColumn.Type.STRING, "LIKE", true, true,
					LayoutPageTemplateEntry::getName),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateEntry::getStatus));

		_collectionPersistenceFinderByG_C_C_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classTypeKey", "type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classTypeKey", "type_"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classTypeKey", "type_"
					},
					0, 4, false, null),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new ArrayableFinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", false, true, true,
					LayoutPageTemplateEntry::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getClassNameId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "classTypeKey",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutPageTemplateEntry::getClassTypeKey),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateEntry::getType));

		_collectionPersistenceFinderByG_C_C_D =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C_D",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classTypeKey",
						"defaultTemplate"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C_D",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classTypeKey",
						"defaultTemplate"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_C_D",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classTypeKey",
						"defaultTemplate"
					},
					0, 4, false, null),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getClassNameId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "classTypeKey",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutPageTemplateEntry::getClassTypeKey),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "defaultTemplate",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					LayoutPageTemplateEntry::isDefaultTemplate));

		_collectionPersistenceFinderByG_C_T_D =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_T_D",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "type_", "defaultTemplate"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_T_D",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "type_", "defaultTemplate"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_T_D",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "type_", "defaultTemplate"
					},
					false),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getClassNameId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateEntry::getType),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "defaultTemplate",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					LayoutPageTemplateEntry::isDefaultTemplate));

		_collectionPersistenceFinderByG_T_LikeN_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T_LikeN_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name", "type_", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_T_LikeN_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "name", "type_", "status"}, false),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "name",
					FinderColumn.Type.STRING, "LIKE", true, true,
					LayoutPageTemplateEntry::getName),
				new ArrayableFinderColumn<>(
					"layoutPageTemplateEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", false, true, true,
					LayoutPageTemplateEntry::getType),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateEntry::getStatus));

		_collectionPersistenceFinderByG_T_D_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T_D_S",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "type_", "defaultTemplate", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T_D_S",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "type_", "defaultTemplate", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_T_D_S",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "type_", "defaultTemplate", "status"
					},
					false),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateEntry::getType),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "defaultTemplate",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					LayoutPageTemplateEntry::isDefaultTemplate),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateEntry::getStatus));

		_collectionPersistenceFinderByG_C_C_LikeN_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_C_C_LikeN_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classTypeKey", "name",
						"type_"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_C_C_LikeN_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classTypeKey", "name",
						"type_"
					},
					false),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new ArrayableFinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", false, true, true,
					LayoutPageTemplateEntry::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getClassNameId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "classTypeKey",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutPageTemplateEntry::getClassTypeKey),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "name",
					FinderColumn.Type.STRING, "LIKE", true, true,
					LayoutPageTemplateEntry::getName),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateEntry::getType));

		_collectionPersistenceFinderByG_C_C_T_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C_T_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classTypeKey", "type_",
						"status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_C_C_T_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classTypeKey", "type_",
						"status"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_C_C_T_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classTypeKey", "type_",
						"status"
					},
					0, 4, false, null),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new ArrayableFinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", false, true, true,
					LayoutPageTemplateEntry::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getClassNameId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "classTypeKey",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutPageTemplateEntry::getClassTypeKey),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateEntry::getType),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateEntry::getStatus));

		_collectionPersistenceFinderByG_C_C_D_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C_D_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classTypeKey",
						"defaultTemplate", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_C_C_D_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classTypeKey",
						"defaultTemplate", "status"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_C_C_D_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classTypeKey",
						"defaultTemplate", "status"
					},
					0, 4, false, null),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getClassNameId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "classTypeKey",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutPageTemplateEntry::getClassTypeKey),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "defaultTemplate",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					LayoutPageTemplateEntry::isDefaultTemplate),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateEntry::getStatus));

		_collectionPersistenceFinderByG_C_C_LikeN_T_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_C_C_LikeN_T_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classTypeKey", "name",
						"type_", "status"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_C_C_LikeN_T_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classTypeKey", "name",
						"type_", "status"
					},
					false),
				_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE,
				LayoutPageTemplateEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new ArrayableFinderColumn<>(
					"layoutPageTemplateEntry.", "groupId",
					FinderColumn.Type.LONG, "=", false, true, true,
					LayoutPageTemplateEntry::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateEntry::getClassNameId),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "classTypeKey",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutPageTemplateEntry::getClassTypeKey),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "name",
					FinderColumn.Type.STRING, "LIKE", true, true,
					LayoutPageTemplateEntry::getName),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateEntry::getType),
				new FinderColumn<>(
					"layoutPageTemplateEntry.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateEntry::getStatus));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(
					LayoutPageTemplateEntry::getExternalReferenceCode),
				LayoutPageTemplateEntry::getGroupId),
			_SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE, "",
			new FinderColumn<>(
				"layoutPageTemplateEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutPageTemplateEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"layoutPageTemplateEntry.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, LayoutPageTemplateEntry::getGroupId));

		LayoutPageTemplateEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LayoutPageTemplateEntryUtil.setPersistence(null);

		entityCache.removeCache(LayoutPageTemplateEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = LayoutPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = LayoutPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = LayoutPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		LayoutPageTemplateEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUTPAGETEMPLATEENTRY =
		"SELECT layoutPageTemplateEntry FROM LayoutPageTemplateEntry layoutPageTemplateEntry";

	private static final String _SQL_SELECT_LAYOUTPAGETEMPLATEENTRY_WHERE =
		"SELECT layoutPageTemplateEntry FROM LayoutPageTemplateEntry layoutPageTemplateEntry WHERE ";

	private static final String _SQL_COUNT_LAYOUTPAGETEMPLATEENTRY_WHERE =
		"SELECT COUNT(layoutPageTemplateEntry) FROM LayoutPageTemplateEntry layoutPageTemplateEntry WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:524244426