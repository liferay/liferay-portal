/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.DuplicateIndexEntryExternalReferenceCodeException;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchIndexEntryException;
import com.liferay.portal.tools.service.builder.test.model.IndexEntry;
import com.liferay.portal.tools.service.builder.test.model.IndexEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.IndexEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.IndexEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.IndexEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.IndexEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The persistence implementation for the index entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class IndexEntryPersistenceImpl
	extends BasePersistenceImpl<IndexEntry, NoSuchIndexEntryException>
	implements IndexEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>IndexEntryUtil</code> to access the index entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		IndexEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<IndexEntry, NoSuchIndexEntryException>
		_collectionPersistenceFinderByOwnerId;

	/**
	 * Returns an ordered range of all the index entries where ownerId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>IndexEntryModelImpl</code>.
	 * </p>
	 *
	 * @param ownerId the owner ID
	 * @param start the lower bound of the range of index entries
	 * @param end the upper bound of the range of index entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching index entries
	 */
	@Override
	public List<IndexEntry> findByOwnerId(
		long ownerId, int start, int end,
		OrderByComparator<IndexEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByOwnerId.find(
			finderCache, new Object[] {ownerId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first index entry in the ordered set where ownerId = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching index entry
	 * @throws NoSuchIndexEntryException if a matching index entry could not be found
	 */
	@Override
	public IndexEntry findByOwnerId_First(
			long ownerId, OrderByComparator<IndexEntry> orderByComparator)
		throws NoSuchIndexEntryException {

		return _collectionPersistenceFinderByOwnerId.findFirst(
			finderCache, new Object[] {ownerId}, orderByComparator);
	}

	/**
	 * Returns the first index entry in the ordered set where ownerId = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching index entry, or <code>null</code> if a matching index entry could not be found
	 */
	@Override
	public IndexEntry fetchByOwnerId_First(
		long ownerId, OrderByComparator<IndexEntry> orderByComparator) {

		return _collectionPersistenceFinderByOwnerId.fetchFirst(
			finderCache, new Object[] {ownerId}, orderByComparator);
	}

	/**
	 * Removes all the index entries where ownerId = &#63; from the database.
	 *
	 * @param ownerId the owner ID
	 */
	@Override
	public void removeByOwnerId(long ownerId) {
		_collectionPersistenceFinderByOwnerId.remove(
			finderCache, new Object[] {ownerId});
	}

	/**
	 * Returns the number of index entries where ownerId = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @return the number of matching index entries
	 */
	@Override
	public int countByOwnerId(long ownerId) {
		return _collectionPersistenceFinderByOwnerId.count(
			finderCache, new Object[] {ownerId});
	}

	private CollectionPersistenceFinder<IndexEntry, NoSuchIndexEntryException>
		_collectionPersistenceFinderByPlid;

	/**
	 * Returns an ordered range of all the index entries where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>IndexEntryModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of index entries
	 * @param end the upper bound of the range of index entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching index entries
	 */
	@Override
	public List<IndexEntry> findByPlid(
		long plid, int start, int end,
		OrderByComparator<IndexEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPlid.find(
			finderCache, new Object[] {plid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first index entry in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching index entry
	 * @throws NoSuchIndexEntryException if a matching index entry could not be found
	 */
	@Override
	public IndexEntry findByPlid_First(
			long plid, OrderByComparator<IndexEntry> orderByComparator)
		throws NoSuchIndexEntryException {

		return _collectionPersistenceFinderByPlid.findFirst(
			finderCache, new Object[] {plid}, orderByComparator);
	}

	/**
	 * Returns the first index entry in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching index entry, or <code>null</code> if a matching index entry could not be found
	 */
	@Override
	public IndexEntry fetchByPlid_First(
		long plid, OrderByComparator<IndexEntry> orderByComparator) {

		return _collectionPersistenceFinderByPlid.fetchFirst(
			finderCache, new Object[] {plid}, orderByComparator);
	}

	/**
	 * Removes all the index entries where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	@Override
	public void removeByPlid(long plid) {
		_collectionPersistenceFinderByPlid.remove(
			finderCache, new Object[] {plid});
	}

	/**
	 * Returns the number of index entries where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching index entries
	 */
	@Override
	public int countByPlid(long plid) {
		return _collectionPersistenceFinderByPlid.count(
			finderCache, new Object[] {plid});
	}

	private CollectionPersistenceFinder<IndexEntry, NoSuchIndexEntryException>
		_collectionPersistenceFinderByPortletId;

	/**
	 * Returns an ordered range of all the index entries where portletId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>IndexEntryModelImpl</code>.
	 * </p>
	 *
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of index entries
	 * @param end the upper bound of the range of index entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching index entries
	 */
	@Override
	public List<IndexEntry> findByPortletId(
		String portletId, int start, int end,
		OrderByComparator<IndexEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPortletId.find(
			finderCache, new Object[] {portletId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first index entry in the ordered set where portletId = &#63;.
	 *
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching index entry
	 * @throws NoSuchIndexEntryException if a matching index entry could not be found
	 */
	@Override
	public IndexEntry findByPortletId_First(
			String portletId, OrderByComparator<IndexEntry> orderByComparator)
		throws NoSuchIndexEntryException {

		return _collectionPersistenceFinderByPortletId.findFirst(
			finderCache, new Object[] {portletId}, orderByComparator);
	}

	/**
	 * Returns the first index entry in the ordered set where portletId = &#63;.
	 *
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching index entry, or <code>null</code> if a matching index entry could not be found
	 */
	@Override
	public IndexEntry fetchByPortletId_First(
		String portletId, OrderByComparator<IndexEntry> orderByComparator) {

		return _collectionPersistenceFinderByPortletId.fetchFirst(
			finderCache, new Object[] {portletId}, orderByComparator);
	}

	/**
	 * Removes all the index entries where portletId = &#63; from the database.
	 *
	 * @param portletId the portlet ID
	 */
	@Override
	public void removeByPortletId(String portletId) {
		_collectionPersistenceFinderByPortletId.remove(
			finderCache, new Object[] {portletId});
	}

	/**
	 * Returns the number of index entries where portletId = &#63;.
	 *
	 * @param portletId the portlet ID
	 * @return the number of matching index entries
	 */
	@Override
	public int countByPortletId(String portletId) {
		return _collectionPersistenceFinderByPortletId.count(
			finderCache, new Object[] {portletId});
	}

	private CollectionPersistenceFinder<IndexEntry, NoSuchIndexEntryException>
		_collectionPersistenceFinderByO_P;

	/**
	 * Returns an ordered range of all the index entries where ownerType = &#63; and portletId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>IndexEntryModelImpl</code>.
	 * </p>
	 *
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of index entries
	 * @param end the upper bound of the range of index entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching index entries
	 */
	@Override
	public List<IndexEntry> findByO_P(
		int ownerType, String portletId, int start, int end,
		OrderByComparator<IndexEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByO_P.find(
			finderCache, new Object[] {ownerType, portletId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first index entry in the ordered set where ownerType = &#63; and portletId = &#63;.
	 *
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching index entry
	 * @throws NoSuchIndexEntryException if a matching index entry could not be found
	 */
	@Override
	public IndexEntry findByO_P_First(
			int ownerType, String portletId,
			OrderByComparator<IndexEntry> orderByComparator)
		throws NoSuchIndexEntryException {

		return _collectionPersistenceFinderByO_P.findFirst(
			finderCache, new Object[] {ownerType, portletId},
			orderByComparator);
	}

	/**
	 * Returns the first index entry in the ordered set where ownerType = &#63; and portletId = &#63;.
	 *
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching index entry, or <code>null</code> if a matching index entry could not be found
	 */
	@Override
	public IndexEntry fetchByO_P_First(
		int ownerType, String portletId,
		OrderByComparator<IndexEntry> orderByComparator) {

		return _collectionPersistenceFinderByO_P.fetchFirst(
			finderCache, new Object[] {ownerType, portletId},
			orderByComparator);
	}

	/**
	 * Removes all the index entries where ownerType = &#63; and portletId = &#63; from the database.
	 *
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 */
	@Override
	public void removeByO_P(int ownerType, String portletId) {
		_collectionPersistenceFinderByO_P.remove(
			finderCache, new Object[] {ownerType, portletId});
	}

	/**
	 * Returns the number of index entries where ownerType = &#63; and portletId = &#63;.
	 *
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @return the number of matching index entries
	 */
	@Override
	public int countByO_P(int ownerType, String portletId) {
		return _collectionPersistenceFinderByO_P.count(
			finderCache, new Object[] {ownerType, portletId});
	}

	private CollectionPersistenceFinder<IndexEntry, NoSuchIndexEntryException>
		_collectionPersistenceFinderByP_P;

	/**
	 * Returns an ordered range of all the index entries where plid = &#63; and portletId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>IndexEntryModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of index entries
	 * @param end the upper bound of the range of index entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching index entries
	 */
	@Override
	public List<IndexEntry> findByP_P(
		long plid, String portletId, int start, int end,
		OrderByComparator<IndexEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_P.find(
			finderCache, new Object[] {plid, portletId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first index entry in the ordered set where plid = &#63; and portletId = &#63;.
	 *
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching index entry
	 * @throws NoSuchIndexEntryException if a matching index entry could not be found
	 */
	@Override
	public IndexEntry findByP_P_First(
			long plid, String portletId,
			OrderByComparator<IndexEntry> orderByComparator)
		throws NoSuchIndexEntryException {

		return _collectionPersistenceFinderByP_P.findFirst(
			finderCache, new Object[] {plid, portletId}, orderByComparator);
	}

	/**
	 * Returns the first index entry in the ordered set where plid = &#63; and portletId = &#63;.
	 *
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching index entry, or <code>null</code> if a matching index entry could not be found
	 */
	@Override
	public IndexEntry fetchByP_P_First(
		long plid, String portletId,
		OrderByComparator<IndexEntry> orderByComparator) {

		return _collectionPersistenceFinderByP_P.fetchFirst(
			finderCache, new Object[] {plid, portletId}, orderByComparator);
	}

	/**
	 * Removes all the index entries where plid = &#63; and portletId = &#63; from the database.
	 *
	 * @param plid the plid
	 * @param portletId the portlet ID
	 */
	@Override
	public void removeByP_P(long plid, String portletId) {
		_collectionPersistenceFinderByP_P.remove(
			finderCache, new Object[] {plid, portletId});
	}

	/**
	 * Returns the number of index entries where plid = &#63; and portletId = &#63;.
	 *
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @return the number of matching index entries
	 */
	@Override
	public int countByP_P(long plid, String portletId) {
		return _collectionPersistenceFinderByP_P.count(
			finderCache, new Object[] {plid, portletId});
	}

	private CollectionPersistenceFinder<IndexEntry, NoSuchIndexEntryException>
		_collectionPersistenceFinderByO_O_P;

	/**
	 * Returns an ordered range of all the index entries where ownerId = &#63; and ownerType = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>IndexEntryModelImpl</code>.
	 * </p>
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param start the lower bound of the range of index entries
	 * @param end the upper bound of the range of index entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching index entries
	 */
	@Override
	public List<IndexEntry> findByO_O_P(
		long ownerId, int ownerType, long plid, int start, int end,
		OrderByComparator<IndexEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByO_O_P.find(
			finderCache, new Object[] {ownerId, ownerType, plid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first index entry in the ordered set where ownerId = &#63; and ownerType = &#63; and plid = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching index entry
	 * @throws NoSuchIndexEntryException if a matching index entry could not be found
	 */
	@Override
	public IndexEntry findByO_O_P_First(
			long ownerId, int ownerType, long plid,
			OrderByComparator<IndexEntry> orderByComparator)
		throws NoSuchIndexEntryException {

		return _collectionPersistenceFinderByO_O_P.findFirst(
			finderCache, new Object[] {ownerId, ownerType, plid},
			orderByComparator);
	}

	/**
	 * Returns the first index entry in the ordered set where ownerId = &#63; and ownerType = &#63; and plid = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching index entry, or <code>null</code> if a matching index entry could not be found
	 */
	@Override
	public IndexEntry fetchByO_O_P_First(
		long ownerId, int ownerType, long plid,
		OrderByComparator<IndexEntry> orderByComparator) {

		return _collectionPersistenceFinderByO_O_P.fetchFirst(
			finderCache, new Object[] {ownerId, ownerType, plid},
			orderByComparator);
	}

	/**
	 * Removes all the index entries where ownerId = &#63; and ownerType = &#63; and plid = &#63; from the database.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param plid the plid
	 */
	@Override
	public void removeByO_O_P(long ownerId, int ownerType, long plid) {
		_collectionPersistenceFinderByO_O_P.remove(
			finderCache, new Object[] {ownerId, ownerType, plid});
	}

	/**
	 * Returns the number of index entries where ownerId = &#63; and ownerType = &#63; and plid = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @return the number of matching index entries
	 */
	@Override
	public int countByO_O_P(long ownerId, int ownerType, long plid) {
		return _collectionPersistenceFinderByO_O_P.count(
			finderCache, new Object[] {ownerId, ownerType, plid});
	}

	private CollectionPersistenceFinder<IndexEntry, NoSuchIndexEntryException>
		_collectionPersistenceFinderByO_O_PI;

	/**
	 * Returns an ordered range of all the index entries where ownerId = &#63; and ownerType = &#63; and portletId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>IndexEntryModelImpl</code>.
	 * </p>
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of index entries
	 * @param end the upper bound of the range of index entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching index entries
	 */
	@Override
	public List<IndexEntry> findByO_O_PI(
		long ownerId, int ownerType, String portletId, int start, int end,
		OrderByComparator<IndexEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByO_O_PI.find(
			finderCache, new Object[] {ownerId, ownerType, portletId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first index entry in the ordered set where ownerId = &#63; and ownerType = &#63; and portletId = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching index entry
	 * @throws NoSuchIndexEntryException if a matching index entry could not be found
	 */
	@Override
	public IndexEntry findByO_O_PI_First(
			long ownerId, int ownerType, String portletId,
			OrderByComparator<IndexEntry> orderByComparator)
		throws NoSuchIndexEntryException {

		return _collectionPersistenceFinderByO_O_PI.findFirst(
			finderCache, new Object[] {ownerId, ownerType, portletId},
			orderByComparator);
	}

	/**
	 * Returns the first index entry in the ordered set where ownerId = &#63; and ownerType = &#63; and portletId = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching index entry, or <code>null</code> if a matching index entry could not be found
	 */
	@Override
	public IndexEntry fetchByO_O_PI_First(
		long ownerId, int ownerType, String portletId,
		OrderByComparator<IndexEntry> orderByComparator) {

		return _collectionPersistenceFinderByO_O_PI.fetchFirst(
			finderCache, new Object[] {ownerId, ownerType, portletId},
			orderByComparator);
	}

	/**
	 * Removes all the index entries where ownerId = &#63; and ownerType = &#63; and portletId = &#63; from the database.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 */
	@Override
	public void removeByO_O_PI(long ownerId, int ownerType, String portletId) {
		_collectionPersistenceFinderByO_O_PI.remove(
			finderCache, new Object[] {ownerId, ownerType, portletId});
	}

	/**
	 * Returns the number of index entries where ownerId = &#63; and ownerType = &#63; and portletId = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @return the number of matching index entries
	 */
	@Override
	public int countByO_O_PI(long ownerId, int ownerType, String portletId) {
		return _collectionPersistenceFinderByO_O_PI.count(
			finderCache, new Object[] {ownerId, ownerType, portletId});
	}

	private CollectionPersistenceFinder<IndexEntry, NoSuchIndexEntryException>
		_collectionPersistenceFinderByO_P_P;

	/**
	 * Returns an ordered range of all the index entries where ownerType = &#63; and plid = &#63; and portletId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>IndexEntryModelImpl</code>.
	 * </p>
	 *
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of index entries
	 * @param end the upper bound of the range of index entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching index entries
	 */
	@Override
	public List<IndexEntry> findByO_P_P(
		int ownerType, long plid, String portletId, int start, int end,
		OrderByComparator<IndexEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByO_P_P.find(
			finderCache, new Object[] {ownerType, plid, portletId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first index entry in the ordered set where ownerType = &#63; and plid = &#63; and portletId = &#63;.
	 *
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching index entry
	 * @throws NoSuchIndexEntryException if a matching index entry could not be found
	 */
	@Override
	public IndexEntry findByO_P_P_First(
			int ownerType, long plid, String portletId,
			OrderByComparator<IndexEntry> orderByComparator)
		throws NoSuchIndexEntryException {

		return _collectionPersistenceFinderByO_P_P.findFirst(
			finderCache, new Object[] {ownerType, plid, portletId},
			orderByComparator);
	}

	/**
	 * Returns the first index entry in the ordered set where ownerType = &#63; and plid = &#63; and portletId = &#63;.
	 *
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching index entry, or <code>null</code> if a matching index entry could not be found
	 */
	@Override
	public IndexEntry fetchByO_P_P_First(
		int ownerType, long plid, String portletId,
		OrderByComparator<IndexEntry> orderByComparator) {

		return _collectionPersistenceFinderByO_P_P.fetchFirst(
			finderCache, new Object[] {ownerType, plid, portletId},
			orderByComparator);
	}

	/**
	 * Removes all the index entries where ownerType = &#63; and plid = &#63; and portletId = &#63; from the database.
	 *
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param portletId the portlet ID
	 */
	@Override
	public void removeByO_P_P(int ownerType, long plid, String portletId) {
		_collectionPersistenceFinderByO_P_P.remove(
			finderCache, new Object[] {ownerType, plid, portletId});
	}

	/**
	 * Returns the number of index entries where ownerType = &#63; and plid = &#63; and portletId = &#63;.
	 *
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @return the number of matching index entries
	 */
	@Override
	public int countByO_P_P(int ownerType, long plid, String portletId) {
		return _collectionPersistenceFinderByO_P_P.count(
			finderCache, new Object[] {ownerType, plid, portletId});
	}

	private CollectionPersistenceFinder<IndexEntry, NoSuchIndexEntryException>
		_collectionPersistenceFinderByC_O_O_LikeP;

	/**
	 * Returns all the index entries where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @return the matching index entries
	 */
	@Override
	public List<IndexEntry> findByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId) {

		return findByC_O_O_LikeP(
			companyId, ownerId, ownerType, portletId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the index entries where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>IndexEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of index entries
	 * @param end the upper bound of the range of index entries (not inclusive)
	 * @return the range of matching index entries
	 */
	@Override
	public List<IndexEntry> findByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId,
		int start, int end) {

		return findByC_O_O_LikeP(
			companyId, ownerId, ownerType, portletId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the index entries where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>IndexEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of index entries
	 * @param end the upper bound of the range of index entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching index entries
	 */
	@Override
	public List<IndexEntry> findByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId,
		int start, int end, OrderByComparator<IndexEntry> orderByComparator) {

		return findByC_O_O_LikeP(
			companyId, ownerId, ownerType, portletId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the index entries where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>IndexEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of index entries
	 * @param end the upper bound of the range of index entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching index entries
	 */
	@Override
	public List<IndexEntry> findByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId,
		int start, int end, OrderByComparator<IndexEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_O_O_LikeP.find(
			finderCache,
			new Object[] {companyId, ownerId, ownerType, portletId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first index entry in the ordered set where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching index entry
	 * @throws NoSuchIndexEntryException if a matching index entry could not be found
	 */
	@Override
	public IndexEntry findByC_O_O_LikeP_First(
			long companyId, long ownerId, int ownerType, String portletId,
			OrderByComparator<IndexEntry> orderByComparator)
		throws NoSuchIndexEntryException {

		return _collectionPersistenceFinderByC_O_O_LikeP.findFirst(
			finderCache,
			new Object[] {companyId, ownerId, ownerType, portletId},
			orderByComparator);
	}

	/**
	 * Returns the first index entry in the ordered set where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching index entry, or <code>null</code> if a matching index entry could not be found
	 */
	@Override
	public IndexEntry fetchByC_O_O_LikeP_First(
		long companyId, long ownerId, int ownerType, String portletId,
		OrderByComparator<IndexEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_O_O_LikeP.fetchFirst(
			finderCache,
			new Object[] {companyId, ownerId, ownerType, portletId},
			orderByComparator);
	}

	/**
	 * Removes all the index entries where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 */
	@Override
	public void removeByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId) {

		_collectionPersistenceFinderByC_O_O_LikeP.remove(
			finderCache,
			new Object[] {companyId, ownerId, ownerType, portletId});
	}

	/**
	 * Returns the number of index entries where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @return the number of matching index entries
	 */
	@Override
	public int countByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId) {

		return _collectionPersistenceFinderByC_O_O_LikeP.count(
			finderCache,
			new Object[] {companyId, ownerId, ownerType, portletId});
	}

	private UniquePersistenceFinder<IndexEntry, NoSuchIndexEntryException>
		_uniquePersistenceFinderByO_O_P_P;

	/**
	 * Returns the index entry where ownerId = &#63; and ownerType = &#63; and plid = &#63; and portletId = &#63; or throws a <code>NoSuchIndexEntryException</code> if it could not be found.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @return the matching index entry
	 * @throws NoSuchIndexEntryException if a matching index entry could not be found
	 */
	@Override
	public IndexEntry findByO_O_P_P(
			long ownerId, int ownerType, long plid, String portletId)
		throws NoSuchIndexEntryException {

		return _uniquePersistenceFinderByO_O_P_P.find(
			finderCache, new Object[] {ownerId, ownerType, plid, portletId});
	}

	/**
	 * Returns the index entry where ownerId = &#63; and ownerType = &#63; and plid = &#63; and portletId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching index entry, or <code>null</code> if a matching index entry could not be found
	 */
	@Override
	public IndexEntry fetchByO_O_P_P(
		long ownerId, int ownerType, long plid, String portletId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByO_O_P_P.fetch(
			finderCache, new Object[] {ownerId, ownerType, plid, portletId},
			useFinderCache);
	}

	/**
	 * Removes the index entry where ownerId = &#63; and ownerType = &#63; and plid = &#63; and portletId = &#63; from the database.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @return the index entry that was removed
	 */
	@Override
	public IndexEntry removeByO_O_P_P(
			long ownerId, int ownerType, long plid, String portletId)
		throws NoSuchIndexEntryException {

		IndexEntry indexEntry = findByO_O_P_P(
			ownerId, ownerType, plid, portletId);

		return remove(indexEntry);
	}

	/**
	 * Returns the number of index entries where ownerId = &#63; and ownerType = &#63; and plid = &#63; and portletId = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @return the number of matching index entries
	 */
	@Override
	public int countByO_O_P_P(
		long ownerId, int ownerType, long plid, String portletId) {

		return _uniquePersistenceFinderByO_O_P_P.count(
			finderCache, new Object[] {ownerId, ownerType, plid, portletId});
	}

	private UniquePersistenceFinder<IndexEntry, NoSuchIndexEntryException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the index entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchIndexEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching index entry
	 * @throws NoSuchIndexEntryException if a matching index entry could not be found
	 */
	@Override
	public IndexEntry findByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchIndexEntryException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the index entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching index entry, or <code>null</code> if a matching index entry could not be found
	 */
	@Override
	public IndexEntry fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the index entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the index entry that was removed
	 */
	@Override
	public IndexEntry removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchIndexEntryException {

		IndexEntry indexEntry = findByERC_C(externalReferenceCode, companyId);

		return remove(indexEntry);
	}

	/**
	 * Returns the number of index entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching index entries
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public IndexEntryPersistenceImpl() {
		setModelClass(IndexEntry.class);

		setModelImplClass(IndexEntryImpl.class);
		setModelPKClass(long.class);

		setTable(IndexEntryTable.INSTANCE);
	}

	/**
	 * Creates a new index entry with the primary key. Does not add the index entry to the database.
	 *
	 * @param indexEntryId the primary key for the new index entry
	 * @return the new index entry
	 */
	@Override
	public IndexEntry create(long indexEntryId) {
		IndexEntry indexEntry = new IndexEntryImpl();

		indexEntry.setNew(true);
		indexEntry.setPrimaryKey(indexEntryId);

		indexEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return indexEntry;
	}

	/**
	 * Removes the index entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param indexEntryId the primary key of the index entry
	 * @return the index entry that was removed
	 * @throws NoSuchIndexEntryException if a index entry with the primary key could not be found
	 */
	@Override
	public IndexEntry remove(long indexEntryId)
		throws NoSuchIndexEntryException {

		return remove((Serializable)indexEntryId);
	}

	@Override
	protected IndexEntry removeImpl(IndexEntry indexEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(indexEntry)) {
				indexEntry = (IndexEntry)session.get(
					IndexEntryImpl.class, indexEntry.getPrimaryKeyObj());
			}

			if ((indexEntry != null) &&
				ctPersistenceHelper.isRemove(indexEntry)) {

				session.delete(indexEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (indexEntry != null) {
			clearCache(indexEntry);
		}

		return indexEntry;
	}

	@Override
	public IndexEntry updateImpl(IndexEntry indexEntry) {
		boolean isNew = indexEntry.isNew();

		if (!(indexEntry instanceof IndexEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(indexEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(indexEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in indexEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom IndexEntry implementation " +
					indexEntry.getClass());
		}

		IndexEntryModelImpl indexEntryModelImpl =
			(IndexEntryModelImpl)indexEntry;

		if (Validator.isNull(indexEntry.getExternalReferenceCode())) {
			indexEntry.setExternalReferenceCode(
				String.valueOf(indexEntry.getPrimaryKey()));
		}
		else {
			if (!Objects.equals(
					indexEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					indexEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = indexEntry.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = indexEntry.getPrimaryKey();
					}

					try {
						indexEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								IndexEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								indexEntry.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			IndexEntry ercIndexEntry = fetchByERC_C(
				indexEntry.getExternalReferenceCode(),
				indexEntry.getCompanyId());

			if (isNew) {
				if (ercIndexEntry != null) {
					throw new DuplicateIndexEntryExternalReferenceCodeException(
						"Duplicate index entry with external reference code " +
							indexEntry.getExternalReferenceCode() +
								" and company " + indexEntry.getCompanyId());
				}
			}
			else {
				if ((ercIndexEntry != null) &&
					(indexEntry.getIndexEntryId() !=
						ercIndexEntry.getIndexEntryId())) {

					throw new DuplicateIndexEntryExternalReferenceCodeException(
						"Duplicate index entry with external reference code " +
							indexEntry.getExternalReferenceCode() +
								" and company " + indexEntry.getCompanyId());
				}
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(indexEntry)) {
				if (!isNew) {
					session.evict(
						IndexEntryImpl.class, indexEntry.getPrimaryKeyObj());
				}

				session.save(indexEntry);
			}
			else {
				indexEntry = (IndexEntry)session.merge(indexEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(indexEntry, false);

		if (isNew) {
			indexEntry.setNew(false);
		}

		indexEntry.resetOriginalValues();

		return indexEntry;
	}

	/**
	 * Returns the index entry with the primary key or throws a <code>NoSuchIndexEntryException</code> if it could not be found.
	 *
	 * @param indexEntryId the primary key of the index entry
	 * @return the index entry
	 * @throws NoSuchIndexEntryException if a index entry with the primary key could not be found
	 */
	@Override
	public IndexEntry findByPrimaryKey(long indexEntryId)
		throws NoSuchIndexEntryException {

		return findByPrimaryKey((Serializable)indexEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the index entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param indexEntryId the primary key of the index entry
	 * @return the index entry, or <code>null</code> if a index entry with the primary key could not be found
	 */
	@Override
	public IndexEntry fetchByPrimaryKey(long indexEntryId) {
		return fetchByPrimaryKey((Serializable)indexEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "indexEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_INDEXENTRY;
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
		return IndexEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "IndexEntry";
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
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("companyId");
		ctMergeColumnNames.add("ownerId");
		ctMergeColumnNames.add("ownerType");
		ctMergeColumnNames.add("plid");
		ctMergeColumnNames.add("portletId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("indexEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"ownerId", "ownerType", "plid", "portletId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the index entry persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByOwnerId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByOwnerId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"ownerId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByOwnerId",
					new String[] {Long.class.getName()},
					new String[] {"ownerId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByOwnerId",
					new String[] {Long.class.getName()},
					new String[] {"ownerId"}, false),
				_SQL_SELECT_INDEXENTRY_WHERE, _SQL_COUNT_INDEXENTRY_WHERE,
				IndexEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"indexEntry.", "ownerId", FinderColumn.Type.LONG, "=", true,
					true, IndexEntry::getOwnerId));

		_collectionPersistenceFinderByPlid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPlid",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"plid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByPlid",
				new String[] {Long.class.getName()}, new String[] {"plid"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByPlid",
				new String[] {Long.class.getName()}, new String[] {"plid"},
				false),
			_SQL_SELECT_INDEXENTRY_WHERE, _SQL_COUNT_INDEXENTRY_WHERE,
			IndexEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"indexEntry.", "plid", FinderColumn.Type.LONG, "=", true, true,
				IndexEntry::getPlid));

		_collectionPersistenceFinderByPortletId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPortletId",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"portletId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByPortletId", new String[] {String.class.getName()},
					new String[] {"portletId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByPortletId", new String[] {String.class.getName()},
					new String[] {"portletId"}, 0, 1, false, null),
				_SQL_SELECT_INDEXENTRY_WHERE, _SQL_COUNT_INDEXENTRY_WHERE,
				IndexEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"indexEntry.", "portletId", FinderColumn.Type.STRING, "=",
					true, true, IndexEntry::getPortletId));

		_collectionPersistenceFinderByO_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByO_P",
				new String[] {
					Integer.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"ownerType", "portletId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByO_P",
				new String[] {Integer.class.getName(), String.class.getName()},
				new String[] {"ownerType", "portletId"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByO_P",
				new String[] {Integer.class.getName(), String.class.getName()},
				new String[] {"ownerType", "portletId"}, 0, 2, false, null),
			_SQL_SELECT_INDEXENTRY_WHERE, _SQL_COUNT_INDEXENTRY_WHERE,
			IndexEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"indexEntry.", "ownerType", FinderColumn.Type.INTEGER, "=",
				true, true, IndexEntry::getOwnerType),
			new FinderColumn<>(
				"indexEntry.", "portletId", FinderColumn.Type.STRING, "=", true,
				true, IndexEntry::getPortletId));

		_collectionPersistenceFinderByP_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_P",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"plid", "portletId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_P",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"plid", "portletId"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_P",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"plid", "portletId"}, 0, 2, false, null),
			_SQL_SELECT_INDEXENTRY_WHERE, _SQL_COUNT_INDEXENTRY_WHERE,
			IndexEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"indexEntry.", "plid", FinderColumn.Type.LONG, "=", true, true,
				IndexEntry::getPlid),
			new FinderColumn<>(
				"indexEntry.", "portletId", FinderColumn.Type.STRING, "=", true,
				true, IndexEntry::getPortletId));

		_collectionPersistenceFinderByO_O_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByO_O_P",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"ownerId", "ownerType", "plid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByO_O_P",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Long.class.getName()
				},
				new String[] {"ownerId", "ownerType", "plid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByO_O_P",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Long.class.getName()
				},
				new String[] {"ownerId", "ownerType", "plid"}, false),
			_SQL_SELECT_INDEXENTRY_WHERE, _SQL_COUNT_INDEXENTRY_WHERE,
			IndexEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"indexEntry.", "ownerId", FinderColumn.Type.LONG, "=", true,
				true, IndexEntry::getOwnerId),
			new FinderColumn<>(
				"indexEntry.", "ownerType", FinderColumn.Type.INTEGER, "=",
				true, true, IndexEntry::getOwnerType),
			new FinderColumn<>(
				"indexEntry.", "plid", FinderColumn.Type.LONG, "=", true, true,
				IndexEntry::getPlid));

		_collectionPersistenceFinderByO_O_PI =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByO_O_PI",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"ownerId", "ownerType", "portletId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByO_O_PI",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						String.class.getName()
					},
					new String[] {"ownerId", "ownerType", "portletId"}, 0, 4,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByO_O_PI",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						String.class.getName()
					},
					new String[] {"ownerId", "ownerType", "portletId"}, 0, 4,
					false, null),
				_SQL_SELECT_INDEXENTRY_WHERE, _SQL_COUNT_INDEXENTRY_WHERE,
				IndexEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"indexEntry.", "ownerId", FinderColumn.Type.LONG, "=", true,
					true, IndexEntry::getOwnerId),
				new FinderColumn<>(
					"indexEntry.", "ownerType", FinderColumn.Type.INTEGER, "=",
					true, true, IndexEntry::getOwnerType),
				new FinderColumn<>(
					"indexEntry.", "portletId", FinderColumn.Type.STRING, "=",
					true, true, IndexEntry::getPortletId));

		_collectionPersistenceFinderByO_P_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByO_P_P",
				new String[] {
					Integer.class.getName(), Long.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"ownerType", "plid", "portletId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByO_P_P",
				new String[] {
					Integer.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"ownerType", "plid", "portletId"}, 0, 4, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByO_P_P",
				new String[] {
					Integer.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"ownerType", "plid", "portletId"}, 0, 4, false,
				null),
			_SQL_SELECT_INDEXENTRY_WHERE, _SQL_COUNT_INDEXENTRY_WHERE,
			IndexEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"indexEntry.", "ownerType", FinderColumn.Type.INTEGER, "=",
				true, true, IndexEntry::getOwnerType),
			new FinderColumn<>(
				"indexEntry.", "plid", FinderColumn.Type.LONG, "=", true, true,
				IndexEntry::getPlid),
			new FinderColumn<>(
				"indexEntry.", "portletId", FinderColumn.Type.STRING, "=", true,
				true, IndexEntry::getPortletId));

		_collectionPersistenceFinderByC_O_O_LikeP =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_O_O_LikeP",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "ownerId", "ownerType", "portletId"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByC_O_O_LikeP",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), String.class.getName()
					},
					new String[] {
						"companyId", "ownerId", "ownerType", "portletId"
					},
					false),
				_SQL_SELECT_INDEXENTRY_WHERE, _SQL_COUNT_INDEXENTRY_WHERE,
				IndexEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"indexEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, IndexEntry::getCompanyId),
				new FinderColumn<>(
					"indexEntry.", "ownerId", FinderColumn.Type.LONG, "=", true,
					true, IndexEntry::getOwnerId),
				new FinderColumn<>(
					"indexEntry.", "ownerType", FinderColumn.Type.INTEGER, "=",
					true, true, IndexEntry::getOwnerType),
				new FinderColumn<>(
					"indexEntry.", "portletId", FinderColumn.Type.STRING,
					"LIKE", true, true, IndexEntry::getPortletId));

		_uniquePersistenceFinderByO_O_P_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByO_O_P_P",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Long.class.getName(), String.class.getName()
				},
				new String[] {"ownerId", "ownerType", "plid", "portletId"}, 0,
				8, false, IndexEntry::getOwnerId, IndexEntry::getOwnerType,
				IndexEntry::getPlid,
				convertNullFunction(IndexEntry::getPortletId)),
			_SQL_SELECT_INDEXENTRY_WHERE, "",
			new FinderColumn<>(
				"indexEntry.", "ownerId", FinderColumn.Type.LONG, "=", true,
				true, IndexEntry::getOwnerId),
			new FinderColumn<>(
				"indexEntry.", "ownerType", FinderColumn.Type.INTEGER, "=",
				true, true, IndexEntry::getOwnerType),
			new FinderColumn<>(
				"indexEntry.", "plid", FinderColumn.Type.LONG, "=", true, true,
				IndexEntry::getPlid),
			new FinderColumn<>(
				"indexEntry.", "portletId", FinderColumn.Type.STRING, "=", true,
				true, IndexEntry::getPortletId));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(IndexEntry::getExternalReferenceCode),
				IndexEntry::getCompanyId),
			_SQL_SELECT_INDEXENTRY_WHERE, "",
			new FinderColumn<>(
				"indexEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				IndexEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"indexEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, IndexEntry::getCompanyId));

		IndexEntryUtil.setPersistence(this);
	}

	public void destroy() {
		IndexEntryUtil.setPersistence(null);

		entityCache.removeCache(IndexEntryImpl.class.getName());
	}

	@ServiceReference(type = CTPersistenceHelper.class)
	protected CTPersistenceHelper ctPersistenceHelper;

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		IndexEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_INDEXENTRY =
		"SELECT indexEntry FROM IndexEntry indexEntry";

	private static final String _SQL_SELECT_INDEXENTRY_WHERE =
		"SELECT indexEntry FROM IndexEntry indexEntry WHERE ";

	private static final String _SQL_COUNT_INDEXENTRY_WHERE =
		"SELECT COUNT(indexEntry) FROM IndexEntry indexEntry WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:617975574