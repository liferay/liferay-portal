/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.link.service.persistence.impl;

import com.liferay.asset.link.exception.NoSuchLinkException;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.model.AssetLinkTable;
import com.liferay.asset.link.model.impl.AssetLinkImpl;
import com.liferay.asset.link.model.impl.AssetLinkModelImpl;
import com.liferay.asset.link.service.persistence.AssetLinkPersistence;
import com.liferay.asset.link.service.persistence.AssetLinkUtil;
import com.liferay.asset.link.service.persistence.impl.constants.AssetPersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

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
 * The persistence implementation for the asset link service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AssetLinkPersistence.class)
public class AssetLinkPersistenceImpl
	extends BasePersistenceImpl<AssetLink, NoSuchLinkException>
	implements AssetLinkPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AssetLinkUtil</code> to access the asset link persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AssetLinkImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<AssetLink, NoSuchLinkException>
		_collectionPersistenceFinderByEntryId1;

	/**
	 * Returns an ordered range of all the asset links where entryId1 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetLinkModelImpl</code>.
	 * </p>
	 *
	 * @param entryId1 the entry id1
	 * @param start the lower bound of the range of asset links
	 * @param end the upper bound of the range of asset links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset links
	 */
	@Override
	public List<AssetLink> findByEntryId1(
		long entryId1, int start, int end,
		OrderByComparator<AssetLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByEntryId1.find(
			finderCache, new Object[] {entryId1}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset link in the ordered set where entryId1 = &#63;.
	 *
	 * @param entryId1 the entry id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset link
	 * @throws NoSuchLinkException if a matching asset link could not be found
	 */
	@Override
	public AssetLink findByEntryId1_First(
			long entryId1, OrderByComparator<AssetLink> orderByComparator)
		throws NoSuchLinkException {

		return _collectionPersistenceFinderByEntryId1.findFirst(
			finderCache, new Object[] {entryId1}, orderByComparator);
	}

	/**
	 * Returns the first asset link in the ordered set where entryId1 = &#63;.
	 *
	 * @param entryId1 the entry id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset link, or <code>null</code> if a matching asset link could not be found
	 */
	@Override
	public AssetLink fetchByEntryId1_First(
		long entryId1, OrderByComparator<AssetLink> orderByComparator) {

		return _collectionPersistenceFinderByEntryId1.fetchFirst(
			finderCache, new Object[] {entryId1}, orderByComparator);
	}

	/**
	 * Removes all the asset links where entryId1 = &#63; from the database.
	 *
	 * @param entryId1 the entry id1
	 */
	@Override
	public void removeByEntryId1(long entryId1) {
		_collectionPersistenceFinderByEntryId1.remove(
			finderCache, new Object[] {entryId1});
	}

	/**
	 * Returns the number of asset links where entryId1 = &#63;.
	 *
	 * @param entryId1 the entry id1
	 * @return the number of matching asset links
	 */
	@Override
	public int countByEntryId1(long entryId1) {
		return _collectionPersistenceFinderByEntryId1.count(
			finderCache, new Object[] {entryId1});
	}

	private CollectionPersistenceFinder<AssetLink, NoSuchLinkException>
		_collectionPersistenceFinderByEntryId2;

	/**
	 * Returns an ordered range of all the asset links where entryId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetLinkModelImpl</code>.
	 * </p>
	 *
	 * @param entryId2 the entry id2
	 * @param start the lower bound of the range of asset links
	 * @param end the upper bound of the range of asset links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset links
	 */
	@Override
	public List<AssetLink> findByEntryId2(
		long entryId2, int start, int end,
		OrderByComparator<AssetLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByEntryId2.find(
			finderCache, new Object[] {entryId2}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset link in the ordered set where entryId2 = &#63;.
	 *
	 * @param entryId2 the entry id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset link
	 * @throws NoSuchLinkException if a matching asset link could not be found
	 */
	@Override
	public AssetLink findByEntryId2_First(
			long entryId2, OrderByComparator<AssetLink> orderByComparator)
		throws NoSuchLinkException {

		return _collectionPersistenceFinderByEntryId2.findFirst(
			finderCache, new Object[] {entryId2}, orderByComparator);
	}

	/**
	 * Returns the first asset link in the ordered set where entryId2 = &#63;.
	 *
	 * @param entryId2 the entry id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset link, or <code>null</code> if a matching asset link could not be found
	 */
	@Override
	public AssetLink fetchByEntryId2_First(
		long entryId2, OrderByComparator<AssetLink> orderByComparator) {

		return _collectionPersistenceFinderByEntryId2.fetchFirst(
			finderCache, new Object[] {entryId2}, orderByComparator);
	}

	/**
	 * Removes all the asset links where entryId2 = &#63; from the database.
	 *
	 * @param entryId2 the entry id2
	 */
	@Override
	public void removeByEntryId2(long entryId2) {
		_collectionPersistenceFinderByEntryId2.remove(
			finderCache, new Object[] {entryId2});
	}

	/**
	 * Returns the number of asset links where entryId2 = &#63;.
	 *
	 * @param entryId2 the entry id2
	 * @return the number of matching asset links
	 */
	@Override
	public int countByEntryId2(long entryId2) {
		return _collectionPersistenceFinderByEntryId2.count(
			finderCache, new Object[] {entryId2});
	}

	private CollectionPersistenceFinder<AssetLink, NoSuchLinkException>
		_collectionPersistenceFinderByE_E;

	/**
	 * Returns an ordered range of all the asset links where entryId1 = &#63; and entryId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetLinkModelImpl</code>.
	 * </p>
	 *
	 * @param entryId1 the entry id1
	 * @param entryId2 the entry id2
	 * @param start the lower bound of the range of asset links
	 * @param end the upper bound of the range of asset links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset links
	 */
	@Override
	public List<AssetLink> findByE_E(
		long entryId1, long entryId2, int start, int end,
		OrderByComparator<AssetLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByE_E.find(
			finderCache, new Object[] {entryId1, entryId2}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset link in the ordered set where entryId1 = &#63; and entryId2 = &#63;.
	 *
	 * @param entryId1 the entry id1
	 * @param entryId2 the entry id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset link
	 * @throws NoSuchLinkException if a matching asset link could not be found
	 */
	@Override
	public AssetLink findByE_E_First(
			long entryId1, long entryId2,
			OrderByComparator<AssetLink> orderByComparator)
		throws NoSuchLinkException {

		return _collectionPersistenceFinderByE_E.findFirst(
			finderCache, new Object[] {entryId1, entryId2}, orderByComparator);
	}

	/**
	 * Returns the first asset link in the ordered set where entryId1 = &#63; and entryId2 = &#63;.
	 *
	 * @param entryId1 the entry id1
	 * @param entryId2 the entry id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset link, or <code>null</code> if a matching asset link could not be found
	 */
	@Override
	public AssetLink fetchByE_E_First(
		long entryId1, long entryId2,
		OrderByComparator<AssetLink> orderByComparator) {

		return _collectionPersistenceFinderByE_E.fetchFirst(
			finderCache, new Object[] {entryId1, entryId2}, orderByComparator);
	}

	/**
	 * Removes all the asset links where entryId1 = &#63; and entryId2 = &#63; from the database.
	 *
	 * @param entryId1 the entry id1
	 * @param entryId2 the entry id2
	 */
	@Override
	public void removeByE_E(long entryId1, long entryId2) {
		_collectionPersistenceFinderByE_E.remove(
			finderCache, new Object[] {entryId1, entryId2});
	}

	/**
	 * Returns the number of asset links where entryId1 = &#63; and entryId2 = &#63;.
	 *
	 * @param entryId1 the entry id1
	 * @param entryId2 the entry id2
	 * @return the number of matching asset links
	 */
	@Override
	public int countByE_E(long entryId1, long entryId2) {
		return _collectionPersistenceFinderByE_E.count(
			finderCache, new Object[] {entryId1, entryId2});
	}

	private CollectionPersistenceFinder<AssetLink, NoSuchLinkException>
		_collectionPersistenceFinderByE1_T;

	/**
	 * Returns an ordered range of all the asset links where entryId1 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetLinkModelImpl</code>.
	 * </p>
	 *
	 * @param entryId1 the entry id1
	 * @param type the type
	 * @param start the lower bound of the range of asset links
	 * @param end the upper bound of the range of asset links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset links
	 */
	@Override
	public List<AssetLink> findByE1_T(
		long entryId1, int type, int start, int end,
		OrderByComparator<AssetLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByE1_T.find(
			finderCache, new Object[] {entryId1, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset link in the ordered set where entryId1 = &#63; and type = &#63;.
	 *
	 * @param entryId1 the entry id1
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset link
	 * @throws NoSuchLinkException if a matching asset link could not be found
	 */
	@Override
	public AssetLink findByE1_T_First(
			long entryId1, int type,
			OrderByComparator<AssetLink> orderByComparator)
		throws NoSuchLinkException {

		return _collectionPersistenceFinderByE1_T.findFirst(
			finderCache, new Object[] {entryId1, type}, orderByComparator);
	}

	/**
	 * Returns the first asset link in the ordered set where entryId1 = &#63; and type = &#63;.
	 *
	 * @param entryId1 the entry id1
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset link, or <code>null</code> if a matching asset link could not be found
	 */
	@Override
	public AssetLink fetchByE1_T_First(
		long entryId1, int type,
		OrderByComparator<AssetLink> orderByComparator) {

		return _collectionPersistenceFinderByE1_T.fetchFirst(
			finderCache, new Object[] {entryId1, type}, orderByComparator);
	}

	/**
	 * Removes all the asset links where entryId1 = &#63; and type = &#63; from the database.
	 *
	 * @param entryId1 the entry id1
	 * @param type the type
	 */
	@Override
	public void removeByE1_T(long entryId1, int type) {
		_collectionPersistenceFinderByE1_T.remove(
			finderCache, new Object[] {entryId1, type});
	}

	/**
	 * Returns the number of asset links where entryId1 = &#63; and type = &#63;.
	 *
	 * @param entryId1 the entry id1
	 * @param type the type
	 * @return the number of matching asset links
	 */
	@Override
	public int countByE1_T(long entryId1, int type) {
		return _collectionPersistenceFinderByE1_T.count(
			finderCache, new Object[] {entryId1, type});
	}

	private CollectionPersistenceFinder<AssetLink, NoSuchLinkException>
		_collectionPersistenceFinderByE2_T;

	/**
	 * Returns an ordered range of all the asset links where entryId2 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetLinkModelImpl</code>.
	 * </p>
	 *
	 * @param entryId2 the entry id2
	 * @param type the type
	 * @param start the lower bound of the range of asset links
	 * @param end the upper bound of the range of asset links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset links
	 */
	@Override
	public List<AssetLink> findByE2_T(
		long entryId2, int type, int start, int end,
		OrderByComparator<AssetLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByE2_T.find(
			finderCache, new Object[] {entryId2, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset link in the ordered set where entryId2 = &#63; and type = &#63;.
	 *
	 * @param entryId2 the entry id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset link
	 * @throws NoSuchLinkException if a matching asset link could not be found
	 */
	@Override
	public AssetLink findByE2_T_First(
			long entryId2, int type,
			OrderByComparator<AssetLink> orderByComparator)
		throws NoSuchLinkException {

		return _collectionPersistenceFinderByE2_T.findFirst(
			finderCache, new Object[] {entryId2, type}, orderByComparator);
	}

	/**
	 * Returns the first asset link in the ordered set where entryId2 = &#63; and type = &#63;.
	 *
	 * @param entryId2 the entry id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset link, or <code>null</code> if a matching asset link could not be found
	 */
	@Override
	public AssetLink fetchByE2_T_First(
		long entryId2, int type,
		OrderByComparator<AssetLink> orderByComparator) {

		return _collectionPersistenceFinderByE2_T.fetchFirst(
			finderCache, new Object[] {entryId2, type}, orderByComparator);
	}

	/**
	 * Removes all the asset links where entryId2 = &#63; and type = &#63; from the database.
	 *
	 * @param entryId2 the entry id2
	 * @param type the type
	 */
	@Override
	public void removeByE2_T(long entryId2, int type) {
		_collectionPersistenceFinderByE2_T.remove(
			finderCache, new Object[] {entryId2, type});
	}

	/**
	 * Returns the number of asset links where entryId2 = &#63; and type = &#63;.
	 *
	 * @param entryId2 the entry id2
	 * @param type the type
	 * @return the number of matching asset links
	 */
	@Override
	public int countByE2_T(long entryId2, int type) {
		return _collectionPersistenceFinderByE2_T.count(
			finderCache, new Object[] {entryId2, type});
	}

	private UniquePersistenceFinder<AssetLink, NoSuchLinkException>
		_uniquePersistenceFinderByE_E_T;

	/**
	 * Returns the asset link where entryId1 = &#63; and entryId2 = &#63; and type = &#63; or throws a <code>NoSuchLinkException</code> if it could not be found.
	 *
	 * @param entryId1 the entry id1
	 * @param entryId2 the entry id2
	 * @param type the type
	 * @return the matching asset link
	 * @throws NoSuchLinkException if a matching asset link could not be found
	 */
	@Override
	public AssetLink findByE_E_T(long entryId1, long entryId2, int type)
		throws NoSuchLinkException {

		return _uniquePersistenceFinderByE_E_T.find(
			finderCache, new Object[] {entryId1, entryId2, type});
	}

	/**
	 * Returns the asset link where entryId1 = &#63; and entryId2 = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param entryId1 the entry id1
	 * @param entryId2 the entry id2
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset link, or <code>null</code> if a matching asset link could not be found
	 */
	@Override
	public AssetLink fetchByE_E_T(
		long entryId1, long entryId2, int type, boolean useFinderCache) {

		return _uniquePersistenceFinderByE_E_T.fetch(
			finderCache, new Object[] {entryId1, entryId2, type},
			useFinderCache);
	}

	/**
	 * Removes the asset link where entryId1 = &#63; and entryId2 = &#63; and type = &#63; from the database.
	 *
	 * @param entryId1 the entry id1
	 * @param entryId2 the entry id2
	 * @param type the type
	 * @return the asset link that was removed
	 */
	@Override
	public AssetLink removeByE_E_T(long entryId1, long entryId2, int type)
		throws NoSuchLinkException {

		AssetLink assetLink = findByE_E_T(entryId1, entryId2, type);

		return remove(assetLink);
	}

	/**
	 * Returns the number of asset links where entryId1 = &#63; and entryId2 = &#63; and type = &#63;.
	 *
	 * @param entryId1 the entry id1
	 * @param entryId2 the entry id2
	 * @param type the type
	 * @return the number of matching asset links
	 */
	@Override
	public int countByE_E_T(long entryId1, long entryId2, int type) {
		return _uniquePersistenceFinderByE_E_T.count(
			finderCache, new Object[] {entryId1, entryId2, type});
	}

	public AssetLinkPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(AssetLink.class);

		setModelImplClass(AssetLinkImpl.class);
		setModelPKClass(long.class);

		setTable(AssetLinkTable.INSTANCE);
	}

	/**
	 * Creates a new asset link with the primary key. Does not add the asset link to the database.
	 *
	 * @param linkId the primary key for the new asset link
	 * @return the new asset link
	 */
	@Override
	public AssetLink create(long linkId) {
		AssetLink assetLink = new AssetLinkImpl();

		assetLink.setNew(true);
		assetLink.setPrimaryKey(linkId);

		assetLink.setCompanyId(CompanyThreadLocal.getCompanyId());

		return assetLink;
	}

	/**
	 * Removes the asset link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param linkId the primary key of the asset link
	 * @return the asset link that was removed
	 * @throws NoSuchLinkException if a asset link with the primary key could not be found
	 */
	@Override
	public AssetLink remove(long linkId) throws NoSuchLinkException {
		return remove((Serializable)linkId);
	}

	@Override
	protected AssetLink removeImpl(AssetLink assetLink) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(assetLink)) {
				assetLink = (AssetLink)session.get(
					AssetLinkImpl.class, assetLink.getPrimaryKeyObj());
			}

			if ((assetLink != null) &&
				ctPersistenceHelper.isRemove(assetLink)) {

				session.delete(assetLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (assetLink != null) {
			clearCache(assetLink);
		}

		return assetLink;
	}

	@Override
	public AssetLink updateImpl(AssetLink assetLink) {
		boolean isNew = assetLink.isNew();

		if (!(assetLink instanceof AssetLinkModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(assetLink.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(assetLink);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in assetLink proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AssetLink implementation " +
					assetLink.getClass());
		}

		AssetLinkModelImpl assetLinkModelImpl = (AssetLinkModelImpl)assetLink;

		if (isNew && (assetLink.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				assetLink.setCreateDate(date);
			}
			else {
				assetLink.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(assetLink)) {
				if (!isNew) {
					session.evict(
						AssetLinkImpl.class, assetLink.getPrimaryKeyObj());
				}

				session.save(assetLink);
			}
			else {
				assetLink = (AssetLink)session.merge(assetLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(assetLink, false);

		if (isNew) {
			assetLink.setNew(false);
		}

		assetLink.resetOriginalValues();

		return assetLink;
	}

	/**
	 * Returns the asset link with the primary key or throws a <code>NoSuchLinkException</code> if it could not be found.
	 *
	 * @param linkId the primary key of the asset link
	 * @return the asset link
	 * @throws NoSuchLinkException if a asset link with the primary key could not be found
	 */
	@Override
	public AssetLink findByPrimaryKey(long linkId) throws NoSuchLinkException {
		return findByPrimaryKey((Serializable)linkId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the asset link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param linkId the primary key of the asset link
	 * @return the asset link, or <code>null</code> if a asset link with the primary key could not be found
	 */
	@Override
	public AssetLink fetchByPrimaryKey(long linkId) {
		return fetchByPrimaryKey((Serializable)linkId);
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
		return "linkId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ASSETLINK;
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
		return AssetLinkModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AssetLink";
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
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctMergeColumnNames.add("entryId1");
		ctMergeColumnNames.add("entryId2");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("weight");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("linkId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"entryId1", "entryId2", "type_"});
	}

	/**
	 * Initializes the asset link persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByEntryId1 =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByEntryId1",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"entryId1"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByEntryId1",
					new String[] {Long.class.getName()},
					new String[] {"entryId1"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByEntryId1", new String[] {Long.class.getName()},
					new String[] {"entryId1"}, false),
				_SQL_SELECT_ASSETLINK_WHERE, _SQL_COUNT_ASSETLINK_WHERE,
				AssetLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"assetLink.", "entryId1", FinderColumn.Type.LONG, "=", true,
					true, AssetLink::getEntryId1));

		_collectionPersistenceFinderByEntryId2 =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByEntryId2",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"entryId2"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByEntryId2",
					new String[] {Long.class.getName()},
					new String[] {"entryId2"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByEntryId2", new String[] {Long.class.getName()},
					new String[] {"entryId2"}, false),
				_SQL_SELECT_ASSETLINK_WHERE, _SQL_COUNT_ASSETLINK_WHERE,
				AssetLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"assetLink.", "entryId2", FinderColumn.Type.LONG, "=", true,
					true, AssetLink::getEntryId2));

		_collectionPersistenceFinderByE_E = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByE_E",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"entryId1", "entryId2"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByE_E",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"entryId1", "entryId2"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByE_E",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"entryId1", "entryId2"}, false),
			_SQL_SELECT_ASSETLINK_WHERE, _SQL_COUNT_ASSETLINK_WHERE,
			AssetLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"assetLink.", "entryId1", FinderColumn.Type.LONG, "=", true,
				true, AssetLink::getEntryId1),
			new FinderColumn<>(
				"assetLink.", "entryId2", FinderColumn.Type.LONG, "=", true,
				true, AssetLink::getEntryId2));

		_collectionPersistenceFinderByE1_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByE1_T",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"entryId1", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByE1_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"entryId1", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByE1_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"entryId1", "type_"}, false),
			_SQL_SELECT_ASSETLINK_WHERE, _SQL_COUNT_ASSETLINK_WHERE,
			AssetLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"assetLink.", "entryId1", FinderColumn.Type.LONG, "=", true,
				true, AssetLink::getEntryId1),
			new FinderColumn<>(
				"assetLink.", "type", "type_", FinderColumn.Type.INTEGER, "=",
				true, true, AssetLink::getType));

		_collectionPersistenceFinderByE2_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByE2_T",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"entryId2", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByE2_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"entryId2", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByE2_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"entryId2", "type_"}, false),
			_SQL_SELECT_ASSETLINK_WHERE, _SQL_COUNT_ASSETLINK_WHERE,
			AssetLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"assetLink.", "entryId2", FinderColumn.Type.LONG, "=", true,
				true, AssetLink::getEntryId2),
			new FinderColumn<>(
				"assetLink.", "type", "type_", FinderColumn.Type.INTEGER, "=",
				true, true, AssetLink::getType));

		_uniquePersistenceFinderByE_E_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByE_E_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"entryId1", "entryId2", "type_"}, 0, 0, false,
				AssetLink::getEntryId1, AssetLink::getEntryId2,
				AssetLink::getType),
			_SQL_SELECT_ASSETLINK_WHERE, "",
			new FinderColumn<>(
				"assetLink.", "entryId1", FinderColumn.Type.LONG, "=", true,
				true, AssetLink::getEntryId1),
			new FinderColumn<>(
				"assetLink.", "entryId2", FinderColumn.Type.LONG, "=", true,
				true, AssetLink::getEntryId2),
			new FinderColumn<>(
				"assetLink.", "type", "type_", FinderColumn.Type.INTEGER, "=",
				true, true, AssetLink::getType));

		AssetLinkUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AssetLinkUtil.setPersistence(null);

		entityCache.removeCache(AssetLinkImpl.class.getName());
	}

	@Override
	@Reference(
		target = AssetPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = AssetPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = AssetPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		AssetLinkModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ASSETLINK =
		"SELECT assetLink FROM AssetLink assetLink";

	private static final String _SQL_SELECT_ASSETLINK_WHERE =
		"SELECT assetLink FROM AssetLink assetLink WHERE ";

	private static final String _SQL_COUNT_ASSETLINK_WHERE =
		"SELECT COUNT(assetLink) FROM AssetLink assetLink WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No AssetLink exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		AssetLinkPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1395856680