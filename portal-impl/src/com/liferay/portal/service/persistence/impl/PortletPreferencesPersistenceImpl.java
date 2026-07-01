/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchPortletPreferencesException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.PortletPreferencesTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.PortletPreferencesPersistence;
import com.liferay.portal.kernel.service.persistence.PortletPreferencesUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.PortletPreferencesImpl;
import com.liferay.portal.model.impl.PortletPreferencesModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the portlet preferences service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PortletPreferencesPersistenceImpl
	extends BasePersistenceImpl
		<PortletPreferences, NoSuchPortletPreferencesException>
	implements PortletPreferencesPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PortletPreferencesUtil</code> to access the portlet preferences persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PortletPreferencesImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<PortletPreferences, NoSuchPortletPreferencesException>
			_collectionPersistenceFinderByOwnerId;

	/**
	 * Returns an ordered range of all the portlet preferenceses where ownerId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param ownerId the owner ID
	 * @param start the lower bound of the range of portlet preferenceses
	 * @param end the upper bound of the range of portlet preferenceses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet preferenceses
	 */
	@Override
	public List<PortletPreferences> findByOwnerId(
		long ownerId, int start, int end,
		OrderByComparator<PortletPreferences> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByOwnerId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {ownerId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first portlet preferences in the ordered set where ownerId = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preferences
	 * @throws NoSuchPortletPreferencesException if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences findByOwnerId_First(
			long ownerId,
			OrderByComparator<PortletPreferences> orderByComparator)
		throws NoSuchPortletPreferencesException {

		return _collectionPersistenceFinderByOwnerId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {ownerId},
			orderByComparator);
	}

	/**
	 * Returns the first portlet preferences in the ordered set where ownerId = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preferences, or <code>null</code> if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences fetchByOwnerId_First(
		long ownerId, OrderByComparator<PortletPreferences> orderByComparator) {

		return _collectionPersistenceFinderByOwnerId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {ownerId},
			orderByComparator);
	}

	/**
	 * Removes all the portlet preferenceses where ownerId = &#63; from the database.
	 *
	 * @param ownerId the owner ID
	 */
	@Override
	public void removeByOwnerId(long ownerId) {
		_collectionPersistenceFinderByOwnerId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {ownerId});
	}

	/**
	 * Returns the number of portlet preferenceses where ownerId = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @return the number of matching portlet preferenceses
	 */
	@Override
	public int countByOwnerId(long ownerId) {
		return _collectionPersistenceFinderByOwnerId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {ownerId});
	}

	private CollectionPersistenceFinder
		<PortletPreferences, NoSuchPortletPreferencesException>
			_collectionPersistenceFinderByPlid;

	/**
	 * Returns an ordered range of all the portlet preferenceses where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of portlet preferenceses
	 * @param end the upper bound of the range of portlet preferenceses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet preferenceses
	 */
	@Override
	public List<PortletPreferences> findByPlid(
		long plid, int start, int end,
		OrderByComparator<PortletPreferences> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPlid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {plid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first portlet preferences in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preferences
	 * @throws NoSuchPortletPreferencesException if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences findByPlid_First(
			long plid, OrderByComparator<PortletPreferences> orderByComparator)
		throws NoSuchPortletPreferencesException {

		return _collectionPersistenceFinderByPlid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {plid},
			orderByComparator);
	}

	/**
	 * Returns the first portlet preferences in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preferences, or <code>null</code> if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences fetchByPlid_First(
		long plid, OrderByComparator<PortletPreferences> orderByComparator) {

		return _collectionPersistenceFinderByPlid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {plid},
			orderByComparator);
	}

	/**
	 * Removes all the portlet preferenceses where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	@Override
	public void removeByPlid(long plid) {
		_collectionPersistenceFinderByPlid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {plid});
	}

	/**
	 * Returns the number of portlet preferenceses where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching portlet preferenceses
	 */
	@Override
	public int countByPlid(long plid) {
		return _collectionPersistenceFinderByPlid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {plid});
	}

	private CollectionPersistenceFinder
		<PortletPreferences, NoSuchPortletPreferencesException>
			_collectionPersistenceFinderByPortletId;

	/**
	 * Returns an ordered range of all the portlet preferenceses where portletId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of portlet preferenceses
	 * @param end the upper bound of the range of portlet preferenceses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet preferenceses
	 */
	@Override
	public List<PortletPreferences> findByPortletId(
		String portletId, int start, int end,
		OrderByComparator<PortletPreferences> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPortletId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {portletId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first portlet preferences in the ordered set where portletId = &#63;.
	 *
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preferences
	 * @throws NoSuchPortletPreferencesException if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences findByPortletId_First(
			String portletId,
			OrderByComparator<PortletPreferences> orderByComparator)
		throws NoSuchPortletPreferencesException {

		return _collectionPersistenceFinderByPortletId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {portletId},
			orderByComparator);
	}

	/**
	 * Returns the first portlet preferences in the ordered set where portletId = &#63;.
	 *
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preferences, or <code>null</code> if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences fetchByPortletId_First(
		String portletId,
		OrderByComparator<PortletPreferences> orderByComparator) {

		return _collectionPersistenceFinderByPortletId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {portletId},
			orderByComparator);
	}

	/**
	 * Removes all the portlet preferenceses where portletId = &#63; from the database.
	 *
	 * @param portletId the portlet ID
	 */
	@Override
	public void removeByPortletId(String portletId) {
		_collectionPersistenceFinderByPortletId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {portletId});
	}

	/**
	 * Returns the number of portlet preferenceses where portletId = &#63;.
	 *
	 * @param portletId the portlet ID
	 * @return the number of matching portlet preferenceses
	 */
	@Override
	public int countByPortletId(String portletId) {
		return _collectionPersistenceFinderByPortletId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {portletId});
	}

	private CollectionPersistenceFinder
		<PortletPreferences, NoSuchPortletPreferencesException>
			_collectionPersistenceFinderByO_P;

	/**
	 * Returns an ordered range of all the portlet preferenceses where ownerType = &#63; and portletId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of portlet preferenceses
	 * @param end the upper bound of the range of portlet preferenceses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet preferenceses
	 */
	@Override
	public List<PortletPreferences> findByO_P(
		int ownerType, String portletId, int start, int end,
		OrderByComparator<PortletPreferences> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByO_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerType, portletId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first portlet preferences in the ordered set where ownerType = &#63; and portletId = &#63;.
	 *
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preferences
	 * @throws NoSuchPortletPreferencesException if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences findByO_P_First(
			int ownerType, String portletId,
			OrderByComparator<PortletPreferences> orderByComparator)
		throws NoSuchPortletPreferencesException {

		return _collectionPersistenceFinderByO_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerType, portletId}, orderByComparator);
	}

	/**
	 * Returns the first portlet preferences in the ordered set where ownerType = &#63; and portletId = &#63;.
	 *
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preferences, or <code>null</code> if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences fetchByO_P_First(
		int ownerType, String portletId,
		OrderByComparator<PortletPreferences> orderByComparator) {

		return _collectionPersistenceFinderByO_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerType, portletId}, orderByComparator);
	}

	/**
	 * Removes all the portlet preferenceses where ownerType = &#63; and portletId = &#63; from the database.
	 *
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 */
	@Override
	public void removeByO_P(int ownerType, String portletId) {
		_collectionPersistenceFinderByO_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerType, portletId});
	}

	/**
	 * Returns the number of portlet preferenceses where ownerType = &#63; and portletId = &#63;.
	 *
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @return the number of matching portlet preferenceses
	 */
	@Override
	public int countByO_P(int ownerType, String portletId) {
		return _collectionPersistenceFinderByO_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerType, portletId});
	}

	private CollectionPersistenceFinder
		<PortletPreferences, NoSuchPortletPreferencesException>
			_collectionPersistenceFinderByP_P;

	/**
	 * Returns an ordered range of all the portlet preferenceses where plid = &#63; and portletId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of portlet preferenceses
	 * @param end the upper bound of the range of portlet preferenceses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet preferenceses
	 */
	@Override
	public List<PortletPreferences> findByP_P(
		long plid, String portletId, int start, int end,
		OrderByComparator<PortletPreferences> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_P.find(
			FinderCacheUtil.getFinderCache(), new Object[] {plid, portletId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first portlet preferences in the ordered set where plid = &#63; and portletId = &#63;.
	 *
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preferences
	 * @throws NoSuchPortletPreferencesException if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences findByP_P_First(
			long plid, String portletId,
			OrderByComparator<PortletPreferences> orderByComparator)
		throws NoSuchPortletPreferencesException {

		return _collectionPersistenceFinderByP_P.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {plid, portletId},
			orderByComparator);
	}

	/**
	 * Returns the first portlet preferences in the ordered set where plid = &#63; and portletId = &#63;.
	 *
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preferences, or <code>null</code> if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences fetchByP_P_First(
		long plid, String portletId,
		OrderByComparator<PortletPreferences> orderByComparator) {

		return _collectionPersistenceFinderByP_P.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {plid, portletId},
			orderByComparator);
	}

	/**
	 * Removes all the portlet preferenceses where plid = &#63; and portletId = &#63; from the database.
	 *
	 * @param plid the plid
	 * @param portletId the portlet ID
	 */
	@Override
	public void removeByP_P(long plid, String portletId) {
		_collectionPersistenceFinderByP_P.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {plid, portletId});
	}

	/**
	 * Returns the number of portlet preferenceses where plid = &#63; and portletId = &#63;.
	 *
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @return the number of matching portlet preferenceses
	 */
	@Override
	public int countByP_P(long plid, String portletId) {
		return _collectionPersistenceFinderByP_P.count(
			FinderCacheUtil.getFinderCache(), new Object[] {plid, portletId});
	}

	private CollectionPersistenceFinder
		<PortletPreferences, NoSuchPortletPreferencesException>
			_collectionPersistenceFinderByO_O_P;

	/**
	 * Returns an ordered range of all the portlet preferenceses where ownerId = &#63; and ownerType = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param start the lower bound of the range of portlet preferenceses
	 * @param end the upper bound of the range of portlet preferenceses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet preferenceses
	 */
	@Override
	public List<PortletPreferences> findByO_O_P(
		long ownerId, int ownerType, long plid, int start, int end,
		OrderByComparator<PortletPreferences> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByO_O_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerId, ownerType, plid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first portlet preferences in the ordered set where ownerId = &#63; and ownerType = &#63; and plid = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preferences
	 * @throws NoSuchPortletPreferencesException if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences findByO_O_P_First(
			long ownerId, int ownerType, long plid,
			OrderByComparator<PortletPreferences> orderByComparator)
		throws NoSuchPortletPreferencesException {

		return _collectionPersistenceFinderByO_O_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerId, ownerType, plid}, orderByComparator);
	}

	/**
	 * Returns the first portlet preferences in the ordered set where ownerId = &#63; and ownerType = &#63; and plid = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preferences, or <code>null</code> if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences fetchByO_O_P_First(
		long ownerId, int ownerType, long plid,
		OrderByComparator<PortletPreferences> orderByComparator) {

		return _collectionPersistenceFinderByO_O_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerId, ownerType, plid}, orderByComparator);
	}

	/**
	 * Removes all the portlet preferenceses where ownerId = &#63; and ownerType = &#63; and plid = &#63; from the database.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param plid the plid
	 */
	@Override
	public void removeByO_O_P(long ownerId, int ownerType, long plid) {
		_collectionPersistenceFinderByO_O_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerId, ownerType, plid});
	}

	/**
	 * Returns the number of portlet preferenceses where ownerId = &#63; and ownerType = &#63; and plid = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @return the number of matching portlet preferenceses
	 */
	@Override
	public int countByO_O_P(long ownerId, int ownerType, long plid) {
		return _collectionPersistenceFinderByO_O_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerId, ownerType, plid});
	}

	private CollectionPersistenceFinder
		<PortletPreferences, NoSuchPortletPreferencesException>
			_collectionPersistenceFinderByO_O_PI;

	/**
	 * Returns an ordered range of all the portlet preferenceses where ownerId = &#63; and ownerType = &#63; and portletId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of portlet preferenceses
	 * @param end the upper bound of the range of portlet preferenceses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet preferenceses
	 */
	@Override
	public List<PortletPreferences> findByO_O_PI(
		long ownerId, int ownerType, String portletId, int start, int end,
		OrderByComparator<PortletPreferences> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByO_O_PI.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerId, ownerType, portletId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first portlet preferences in the ordered set where ownerId = &#63; and ownerType = &#63; and portletId = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preferences
	 * @throws NoSuchPortletPreferencesException if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences findByO_O_PI_First(
			long ownerId, int ownerType, String portletId,
			OrderByComparator<PortletPreferences> orderByComparator)
		throws NoSuchPortletPreferencesException {

		return _collectionPersistenceFinderByO_O_PI.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerId, ownerType, portletId}, orderByComparator);
	}

	/**
	 * Returns the first portlet preferences in the ordered set where ownerId = &#63; and ownerType = &#63; and portletId = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preferences, or <code>null</code> if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences fetchByO_O_PI_First(
		long ownerId, int ownerType, String portletId,
		OrderByComparator<PortletPreferences> orderByComparator) {

		return _collectionPersistenceFinderByO_O_PI.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerId, ownerType, portletId}, orderByComparator);
	}

	/**
	 * Removes all the portlet preferenceses where ownerId = &#63; and ownerType = &#63; and portletId = &#63; from the database.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 */
	@Override
	public void removeByO_O_PI(long ownerId, int ownerType, String portletId) {
		_collectionPersistenceFinderByO_O_PI.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerId, ownerType, portletId});
	}

	/**
	 * Returns the number of portlet preferenceses where ownerId = &#63; and ownerType = &#63; and portletId = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @return the number of matching portlet preferenceses
	 */
	@Override
	public int countByO_O_PI(long ownerId, int ownerType, String portletId) {
		return _collectionPersistenceFinderByO_O_PI.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerId, ownerType, portletId});
	}

	private CollectionPersistenceFinder
		<PortletPreferences, NoSuchPortletPreferencesException>
			_collectionPersistenceFinderByO_P_P;

	/**
	 * Returns an ordered range of all the portlet preferenceses where ownerType = &#63; and plid = &#63; and portletId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of portlet preferenceses
	 * @param end the upper bound of the range of portlet preferenceses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet preferenceses
	 */
	@Override
	public List<PortletPreferences> findByO_P_P(
		int ownerType, long plid, String portletId, int start, int end,
		OrderByComparator<PortletPreferences> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByO_P_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerType, plid, portletId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first portlet preferences in the ordered set where ownerType = &#63; and plid = &#63; and portletId = &#63;.
	 *
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preferences
	 * @throws NoSuchPortletPreferencesException if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences findByO_P_P_First(
			int ownerType, long plid, String portletId,
			OrderByComparator<PortletPreferences> orderByComparator)
		throws NoSuchPortletPreferencesException {

		return _collectionPersistenceFinderByO_P_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerType, plid, portletId}, orderByComparator);
	}

	/**
	 * Returns the first portlet preferences in the ordered set where ownerType = &#63; and plid = &#63; and portletId = &#63;.
	 *
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preferences, or <code>null</code> if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences fetchByO_P_P_First(
		int ownerType, long plid, String portletId,
		OrderByComparator<PortletPreferences> orderByComparator) {

		return _collectionPersistenceFinderByO_P_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerType, plid, portletId}, orderByComparator);
	}

	/**
	 * Removes all the portlet preferenceses where ownerType = &#63; and plid = &#63; and portletId = &#63; from the database.
	 *
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param portletId the portlet ID
	 */
	@Override
	public void removeByO_P_P(int ownerType, long plid, String portletId) {
		_collectionPersistenceFinderByO_P_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerType, plid, portletId});
	}

	/**
	 * Returns the number of portlet preferenceses where ownerType = &#63; and plid = &#63; and portletId = &#63;.
	 *
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @return the number of matching portlet preferenceses
	 */
	@Override
	public int countByO_P_P(int ownerType, long plid, String portletId) {
		return _collectionPersistenceFinderByO_P_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerType, plid, portletId});
	}

	private CollectionPersistenceFinder
		<PortletPreferences, NoSuchPortletPreferencesException>
			_collectionPersistenceFinderByC_O_O_LikeP;

	/**
	 * Returns all the portlet preferenceses where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @return the matching portlet preferenceses
	 */
	@Override
	public List<PortletPreferences> findByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId) {

		return findByC_O_O_LikeP(
			companyId, ownerId, ownerType, portletId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the portlet preferenceses where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of portlet preferenceses
	 * @param end the upper bound of the range of portlet preferenceses (not inclusive)
	 * @return the range of matching portlet preferenceses
	 */
	@Override
	public List<PortletPreferences> findByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId,
		int start, int end) {

		return findByC_O_O_LikeP(
			companyId, ownerId, ownerType, portletId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the portlet preferenceses where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of portlet preferenceses
	 * @param end the upper bound of the range of portlet preferenceses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portlet preferenceses
	 */
	@Override
	public List<PortletPreferences> findByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId,
		int start, int end,
		OrderByComparator<PortletPreferences> orderByComparator) {

		return findByC_O_O_LikeP(
			companyId, ownerId, ownerType, portletId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the portlet preferenceses where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of portlet preferenceses
	 * @param end the upper bound of the range of portlet preferenceses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet preferenceses
	 */
	@Override
	public List<PortletPreferences> findByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId,
		int start, int end,
		OrderByComparator<PortletPreferences> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_O_O_LikeP.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, ownerId, ownerType, portletId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first portlet preferences in the ordered set where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preferences
	 * @throws NoSuchPortletPreferencesException if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences findByC_O_O_LikeP_First(
			long companyId, long ownerId, int ownerType, String portletId,
			OrderByComparator<PortletPreferences> orderByComparator)
		throws NoSuchPortletPreferencesException {

		return _collectionPersistenceFinderByC_O_O_LikeP.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, ownerId, ownerType, portletId},
			orderByComparator);
	}

	/**
	 * Returns the first portlet preferences in the ordered set where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preferences, or <code>null</code> if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences fetchByC_O_O_LikeP_First(
		long companyId, long ownerId, int ownerType, String portletId,
		OrderByComparator<PortletPreferences> orderByComparator) {

		return _collectionPersistenceFinderByC_O_O_LikeP.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, ownerId, ownerType, portletId},
			orderByComparator);
	}

	/**
	 * Removes all the portlet preferenceses where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63; from the database.
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
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, ownerId, ownerType, portletId});
	}

	/**
	 * Returns the number of portlet preferenceses where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @return the number of matching portlet preferenceses
	 */
	@Override
	public int countByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId) {

		return _collectionPersistenceFinderByC_O_O_LikeP.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, ownerId, ownerType, portletId});
	}

	private UniquePersistenceFinder
		<PortletPreferences, NoSuchPortletPreferencesException>
			_uniquePersistenceFinderByO_O_P_P;

	/**
	 * Returns the portlet preferences where ownerId = &#63; and ownerType = &#63; and plid = &#63; and portletId = &#63; or throws a <code>NoSuchPortletPreferencesException</code> if it could not be found.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @return the matching portlet preferences
	 * @throws NoSuchPortletPreferencesException if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences findByO_O_P_P(
			long ownerId, int ownerType, long plid, String portletId)
		throws NoSuchPortletPreferencesException {

		return _uniquePersistenceFinderByO_O_P_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerId, ownerType, plid, portletId});
	}

	/**
	 * Returns the portlet preferences where ownerId = &#63; and ownerType = &#63; and plid = &#63; and portletId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching portlet preferences, or <code>null</code> if a matching portlet preferences could not be found
	 */
	@Override
	public PortletPreferences fetchByO_O_P_P(
		long ownerId, int ownerType, long plid, String portletId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByO_O_P_P.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerId, ownerType, plid, portletId}, useFinderCache);
	}

	/**
	 * Removes the portlet preferences where ownerId = &#63; and ownerType = &#63; and plid = &#63; and portletId = &#63; from the database.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @return the portlet preferences that was removed
	 */
	@Override
	public PortletPreferences removeByO_O_P_P(
			long ownerId, int ownerType, long plid, String portletId)
		throws NoSuchPortletPreferencesException {

		PortletPreferences portletPreferences = findByO_O_P_P(
			ownerId, ownerType, plid, portletId);

		return remove(portletPreferences);
	}

	/**
	 * Returns the number of portlet preferenceses where ownerId = &#63; and ownerType = &#63; and plid = &#63; and portletId = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param plid the plid
	 * @param portletId the portlet ID
	 * @return the number of matching portlet preferenceses
	 */
	@Override
	public int countByO_O_P_P(
		long ownerId, int ownerType, long plid, String portletId) {

		return _uniquePersistenceFinderByO_O_P_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerId, ownerType, plid, portletId});
	}

	public PortletPreferencesPersistenceImpl() {
		setModelClass(PortletPreferences.class);

		setModelImplClass(PortletPreferencesImpl.class);
		setModelPKClass(long.class);

		setTable(PortletPreferencesTable.INSTANCE);
	}

	/**
	 * Creates a new portlet preferences with the primary key. Does not add the portlet preferences to the database.
	 *
	 * @param portletPreferencesId the primary key for the new portlet preferences
	 * @return the new portlet preferences
	 */
	@Override
	public PortletPreferences create(long portletPreferencesId) {
		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		portletPreferences.setNew(true);
		portletPreferences.setPrimaryKey(portletPreferencesId);

		portletPreferences.setCompanyId(CompanyThreadLocal.getCompanyId());

		return portletPreferences;
	}

	/**
	 * Removes the portlet preferences with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param portletPreferencesId the primary key of the portlet preferences
	 * @return the portlet preferences that was removed
	 * @throws NoSuchPortletPreferencesException if a portlet preferences with the primary key could not be found
	 */
	@Override
	public PortletPreferences remove(long portletPreferencesId)
		throws NoSuchPortletPreferencesException {

		return remove((Serializable)portletPreferencesId);
	}

	@Override
	protected PortletPreferences removeImpl(
		PortletPreferences portletPreferences) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(portletPreferences)) {
				portletPreferences = (PortletPreferences)session.get(
					PortletPreferencesImpl.class,
					portletPreferences.getPrimaryKeyObj());
			}

			if ((portletPreferences != null) &&
				CTPersistenceHelperUtil.isRemove(portletPreferences)) {

				session.delete(portletPreferences);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (portletPreferences != null) {
			clearCache(portletPreferences);
		}

		return portletPreferences;
	}

	@Override
	public PortletPreferences updateImpl(
		PortletPreferences portletPreferences) {

		boolean isNew = portletPreferences.isNew();

		if (!(portletPreferences instanceof PortletPreferencesModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(portletPreferences.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					portletPreferences);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in portletPreferences proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PortletPreferences implementation " +
					portletPreferences.getClass());
		}

		PortletPreferencesModelImpl portletPreferencesModelImpl =
			(PortletPreferencesModelImpl)portletPreferences;

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(portletPreferences)) {
				if (!isNew) {
					session.evict(
						PortletPreferencesImpl.class,
						portletPreferences.getPrimaryKeyObj());
				}

				session.save(portletPreferences);
			}
			else {
				portletPreferences = (PortletPreferences)session.merge(
					portletPreferences);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(portletPreferences, false);

		if (isNew) {
			portletPreferences.setNew(false);
		}

		portletPreferences.resetOriginalValues();

		return portletPreferences;
	}

	/**
	 * Returns the portlet preferences with the primary key or throws a <code>NoSuchPortletPreferencesException</code> if it could not be found.
	 *
	 * @param portletPreferencesId the primary key of the portlet preferences
	 * @return the portlet preferences
	 * @throws NoSuchPortletPreferencesException if a portlet preferences with the primary key could not be found
	 */
	@Override
	public PortletPreferences findByPrimaryKey(long portletPreferencesId)
		throws NoSuchPortletPreferencesException {

		return findByPrimaryKey((Serializable)portletPreferencesId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the portlet preferences with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param portletPreferencesId the primary key of the portlet preferences
	 * @return the portlet preferences, or <code>null</code> if a portlet preferences with the primary key could not be found
	 */
	@Override
	public PortletPreferences fetchByPrimaryKey(long portletPreferencesId) {
		return fetchByPrimaryKey((Serializable)portletPreferencesId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "portletPreferencesId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PORTLETPREFERENCES;
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
		return PortletPreferencesModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "PortletPreferences";
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
		ctStrictColumnNames.add("companyId");
		ctMergeColumnNames.add("ownerId");
		ctMergeColumnNames.add("ownerType");
		ctMergeColumnNames.add("plid");
		ctMergeColumnNames.add("portletId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("portletPreferencesId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"ownerId", "ownerType", "plid", "portletId"});
	}

	/**
	 * Initializes the portlet preferences persistence.
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
				_SQL_SELECT_PORTLETPREFERENCES_WHERE,
				_SQL_COUNT_PORTLETPREFERENCES_WHERE,
				PortletPreferencesModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"portletPreferences.", "ownerId", FinderColumn.Type.LONG,
					"=", true, true, PortletPreferences::getOwnerId));

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
			_SQL_SELECT_PORTLETPREFERENCES_WHERE,
			_SQL_COUNT_PORTLETPREFERENCES_WHERE,
			PortletPreferencesModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"portletPreferences.", "plid", FinderColumn.Type.LONG, "=",
				true, true, PortletPreferences::getPlid));

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
				_SQL_SELECT_PORTLETPREFERENCES_WHERE,
				_SQL_COUNT_PORTLETPREFERENCES_WHERE,
				PortletPreferencesModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"portletPreferences.", "portletId",
					FinderColumn.Type.STRING, "=", true, true,
					PortletPreferences::getPortletId));

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
			_SQL_SELECT_PORTLETPREFERENCES_WHERE,
			_SQL_COUNT_PORTLETPREFERENCES_WHERE,
			PortletPreferencesModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"portletPreferences.", "ownerType", FinderColumn.Type.INTEGER,
				"=", true, true, PortletPreferences::getOwnerType),
			new FinderColumn<>(
				"portletPreferences.", "portletId", FinderColumn.Type.STRING,
				"=", true, true, PortletPreferences::getPortletId));

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
			_SQL_SELECT_PORTLETPREFERENCES_WHERE,
			_SQL_COUNT_PORTLETPREFERENCES_WHERE,
			PortletPreferencesModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"portletPreferences.", "plid", FinderColumn.Type.LONG, "=",
				true, true, PortletPreferences::getPlid),
			new FinderColumn<>(
				"portletPreferences.", "portletId", FinderColumn.Type.STRING,
				"=", true, true, PortletPreferences::getPortletId));

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
			_SQL_SELECT_PORTLETPREFERENCES_WHERE,
			_SQL_COUNT_PORTLETPREFERENCES_WHERE,
			PortletPreferencesModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"portletPreferences.", "ownerId", FinderColumn.Type.LONG, "=",
				true, true, PortletPreferences::getOwnerId),
			new FinderColumn<>(
				"portletPreferences.", "ownerType", FinderColumn.Type.INTEGER,
				"=", true, true, PortletPreferences::getOwnerType),
			new FinderColumn<>(
				"portletPreferences.", "plid", FinderColumn.Type.LONG, "=",
				true, true, PortletPreferences::getPlid));

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
				_SQL_SELECT_PORTLETPREFERENCES_WHERE,
				_SQL_COUNT_PORTLETPREFERENCES_WHERE,
				PortletPreferencesModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"portletPreferences.", "ownerId", FinderColumn.Type.LONG,
					"=", true, true, PortletPreferences::getOwnerId),
				new FinderColumn<>(
					"portletPreferences.", "ownerType",
					FinderColumn.Type.INTEGER, "=", true, true,
					PortletPreferences::getOwnerType),
				new FinderColumn<>(
					"portletPreferences.", "portletId",
					FinderColumn.Type.STRING, "=", true, true,
					PortletPreferences::getPortletId));

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
			_SQL_SELECT_PORTLETPREFERENCES_WHERE,
			_SQL_COUNT_PORTLETPREFERENCES_WHERE,
			PortletPreferencesModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"portletPreferences.", "ownerType", FinderColumn.Type.INTEGER,
				"=", true, true, PortletPreferences::getOwnerType),
			new FinderColumn<>(
				"portletPreferences.", "plid", FinderColumn.Type.LONG, "=",
				true, true, PortletPreferences::getPlid),
			new FinderColumn<>(
				"portletPreferences.", "portletId", FinderColumn.Type.STRING,
				"=", true, true, PortletPreferences::getPortletId));

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
				_SQL_SELECT_PORTLETPREFERENCES_WHERE,
				_SQL_COUNT_PORTLETPREFERENCES_WHERE,
				PortletPreferencesModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"portletPreferences.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, PortletPreferences::getCompanyId),
				new FinderColumn<>(
					"portletPreferences.", "ownerId", FinderColumn.Type.LONG,
					"=", true, true, PortletPreferences::getOwnerId),
				new FinderColumn<>(
					"portletPreferences.", "ownerType",
					FinderColumn.Type.INTEGER, "=", true, true,
					PortletPreferences::getOwnerType),
				new FinderColumn<>(
					"portletPreferences.", "portletId",
					FinderColumn.Type.STRING, "LIKE", true, true,
					PortletPreferences::getPortletId));

		_uniquePersistenceFinderByO_O_P_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByO_O_P_P",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Long.class.getName(), String.class.getName()
				},
				new String[] {"ownerId", "ownerType", "plid", "portletId"}, 0,
				8, false, PortletPreferences::getOwnerId,
				PortletPreferences::getOwnerType, PortletPreferences::getPlid,
				convertNullFunction(PortletPreferences::getPortletId)),
			_SQL_SELECT_PORTLETPREFERENCES_WHERE, "",
			new FinderColumn<>(
				"portletPreferences.", "ownerId", FinderColumn.Type.LONG, "=",
				true, true, PortletPreferences::getOwnerId),
			new FinderColumn<>(
				"portletPreferences.", "ownerType", FinderColumn.Type.INTEGER,
				"=", true, true, PortletPreferences::getOwnerType),
			new FinderColumn<>(
				"portletPreferences.", "plid", FinderColumn.Type.LONG, "=",
				true, true, PortletPreferences::getPlid),
			new FinderColumn<>(
				"portletPreferences.", "portletId", FinderColumn.Type.STRING,
				"=", true, true, PortletPreferences::getPortletId));

		PortletPreferencesUtil.setPersistence(this);
	}

	public void destroy() {
		PortletPreferencesUtil.setPersistence(null);

		EntityCacheUtil.removeCache(PortletPreferencesImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		PortletPreferencesModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PORTLETPREFERENCES =
		"SELECT portletPreferences FROM PortletPreferences portletPreferences";

	private static final String _SQL_SELECT_PORTLETPREFERENCES_WHERE =
		"SELECT portletPreferences FROM PortletPreferences portletPreferences WHERE ";

	private static final String _SQL_COUNT_PORTLETPREFERENCES_WHERE =
		"SELECT COUNT(portletPreferences) FROM PortletPreferences portletPreferences WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PortletPreferences exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PortletPreferencesPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1836680284