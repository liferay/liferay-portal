/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchVirtualHostException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.model.VirtualHostTable;
import com.liferay.portal.kernel.service.persistence.VirtualHostPersistence;
import com.liferay.portal.kernel.service.persistence.VirtualHostUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.VirtualHostImpl;
import com.liferay.portal.model.impl.VirtualHostModelImpl;

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
 * The persistence implementation for the virtual host service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class VirtualHostPersistenceImpl
	extends BasePersistenceImpl<VirtualHost, NoSuchVirtualHostException>
	implements VirtualHostPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>VirtualHostUtil</code> to access the virtual host persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		VirtualHostImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<VirtualHost, NoSuchVirtualHostException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the virtual hosts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VirtualHostModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of virtual hosts
	 * @param end the upper bound of the range of virtual hosts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching virtual hosts
	 */
	@Override
	public List<VirtualHost> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<VirtualHost> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first virtual host in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching virtual host
	 * @throws NoSuchVirtualHostException if a matching virtual host could not be found
	 */
	@Override
	public VirtualHost findByCompanyId_First(
			long companyId, OrderByComparator<VirtualHost> orderByComparator)
		throws NoSuchVirtualHostException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first virtual host in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching virtual host, or <code>null</code> if a matching virtual host could not be found
	 */
	@Override
	public VirtualHost fetchByCompanyId_First(
		long companyId, OrderByComparator<VirtualHost> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the virtual hosts where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of virtual hosts where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching virtual hosts
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private UniquePersistenceFinder<VirtualHost, NoSuchVirtualHostException>
		_uniquePersistenceFinderByHostname;

	/**
	 * Returns the virtual host where hostname = &#63; or throws a <code>NoSuchVirtualHostException</code> if it could not be found.
	 *
	 * @param hostname the hostname
	 * @return the matching virtual host
	 * @throws NoSuchVirtualHostException if a matching virtual host could not be found
	 */
	@Override
	public VirtualHost findByHostname(String hostname)
		throws NoSuchVirtualHostException {

		return _uniquePersistenceFinderByHostname.find(
			FinderCacheUtil.getFinderCache(), new Object[] {hostname});
	}

	/**
	 * Returns the virtual host where hostname = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param hostname the hostname
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching virtual host, or <code>null</code> if a matching virtual host could not be found
	 */
	@Override
	public VirtualHost fetchByHostname(
		String hostname, boolean useFinderCache) {

		return _uniquePersistenceFinderByHostname.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {hostname},
			useFinderCache);
	}

	/**
	 * Removes the virtual host where hostname = &#63; from the database.
	 *
	 * @param hostname the hostname
	 * @return the virtual host that was removed
	 */
	@Override
	public VirtualHost removeByHostname(String hostname)
		throws NoSuchVirtualHostException {

		VirtualHost virtualHost = findByHostname(hostname);

		return remove(virtualHost);
	}

	/**
	 * Returns the number of virtual hosts where hostname = &#63;.
	 *
	 * @param hostname the hostname
	 * @return the number of matching virtual hosts
	 */
	@Override
	public int countByHostname(String hostname) {
		return _uniquePersistenceFinderByHostname.count(
			FinderCacheUtil.getFinderCache(), new Object[] {hostname});
	}

	private CollectionPersistenceFinder<VirtualHost, NoSuchVirtualHostException>
		_collectionPersistenceFinderByC_L;

	/**
	 * Returns an ordered range of all the virtual hosts where companyId = &#63; and layoutSetId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VirtualHostModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param layoutSetId the layout set ID
	 * @param start the lower bound of the range of virtual hosts
	 * @param end the upper bound of the range of virtual hosts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching virtual hosts
	 */
	@Override
	public List<VirtualHost> findByC_L(
		long companyId, long layoutSetId, int start, int end,
		OrderByComparator<VirtualHost> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_L.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, layoutSetId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first virtual host in the ordered set where companyId = &#63; and layoutSetId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutSetId the layout set ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching virtual host
	 * @throws NoSuchVirtualHostException if a matching virtual host could not be found
	 */
	@Override
	public VirtualHost findByC_L_First(
			long companyId, long layoutSetId,
			OrderByComparator<VirtualHost> orderByComparator)
		throws NoSuchVirtualHostException {

		return _collectionPersistenceFinderByC_L.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, layoutSetId}, orderByComparator);
	}

	/**
	 * Returns the first virtual host in the ordered set where companyId = &#63; and layoutSetId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutSetId the layout set ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching virtual host, or <code>null</code> if a matching virtual host could not be found
	 */
	@Override
	public VirtualHost fetchByC_L_First(
		long companyId, long layoutSetId,
		OrderByComparator<VirtualHost> orderByComparator) {

		return _collectionPersistenceFinderByC_L.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, layoutSetId}, orderByComparator);
	}

	/**
	 * Removes all the virtual hosts where companyId = &#63; and layoutSetId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param layoutSetId the layout set ID
	 */
	@Override
	public void removeByC_L(long companyId, long layoutSetId) {
		_collectionPersistenceFinderByC_L.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, layoutSetId});
	}

	/**
	 * Returns the number of virtual hosts where companyId = &#63; and layoutSetId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutSetId the layout set ID
	 * @return the number of matching virtual hosts
	 */
	@Override
	public int countByC_L(long companyId, long layoutSetId) {
		return _collectionPersistenceFinderByC_L.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, layoutSetId});
	}

	private CollectionPersistenceFinder<VirtualHost, NoSuchVirtualHostException>
		_collectionPersistenceFinderByNotL_H;

	/**
	 * Returns all the virtual hosts where layoutSetId &ne; &#63; and hostname = &#63;.
	 *
	 * @param layoutSetId the layout set ID
	 * @param hostname the hostname
	 * @return the matching virtual hosts
	 */
	@Override
	public List<VirtualHost> findByNotL_H(long layoutSetId, String hostname) {
		return findByNotL_H(
			layoutSetId, hostname, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the virtual hosts where layoutSetId &ne; &#63; and hostname = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VirtualHostModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetId the layout set ID
	 * @param hostname the hostname
	 * @param start the lower bound of the range of virtual hosts
	 * @param end the upper bound of the range of virtual hosts (not inclusive)
	 * @return the range of matching virtual hosts
	 */
	@Override
	public List<VirtualHost> findByNotL_H(
		long layoutSetId, String hostname, int start, int end) {

		return findByNotL_H(layoutSetId, hostname, start, end, null);
	}

	/**
	 * Returns an ordered range of all the virtual hosts where layoutSetId &ne; &#63; and hostname = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VirtualHostModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetId the layout set ID
	 * @param hostname the hostname
	 * @param start the lower bound of the range of virtual hosts
	 * @param end the upper bound of the range of virtual hosts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching virtual hosts
	 */
	@Override
	public List<VirtualHost> findByNotL_H(
		long layoutSetId, String hostname, int start, int end,
		OrderByComparator<VirtualHost> orderByComparator) {

		return findByNotL_H(
			layoutSetId, hostname, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the virtual hosts where layoutSetId &ne; &#63; and hostname = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VirtualHostModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetId the layout set ID
	 * @param hostname the hostname
	 * @param start the lower bound of the range of virtual hosts
	 * @param end the upper bound of the range of virtual hosts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching virtual hosts
	 */
	@Override
	public List<VirtualHost> findByNotL_H(
		long layoutSetId, String hostname, int start, int end,
		OrderByComparator<VirtualHost> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByNotL_H.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetId, new String[] {hostname}}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first virtual host in the ordered set where layoutSetId &ne; &#63; and hostname = &#63;.
	 *
	 * @param layoutSetId the layout set ID
	 * @param hostname the hostname
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching virtual host
	 * @throws NoSuchVirtualHostException if a matching virtual host could not be found
	 */
	@Override
	public VirtualHost findByNotL_H_First(
			long layoutSetId, String hostname,
			OrderByComparator<VirtualHost> orderByComparator)
		throws NoSuchVirtualHostException {

		VirtualHost virtualHost = fetchByNotL_H_First(
			layoutSetId, hostname, orderByComparator);

		if (virtualHost != null) {
			return virtualHost;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("layoutSetId!=");
		sb.append(layoutSetId);

		sb.append(", hostname=");
		sb.append(hostname);

		sb.append("}");

		throw new NoSuchVirtualHostException(sb.toString());
	}

	/**
	 * Returns the first virtual host in the ordered set where layoutSetId &ne; &#63; and hostname = &#63;.
	 *
	 * @param layoutSetId the layout set ID
	 * @param hostname the hostname
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching virtual host, or <code>null</code> if a matching virtual host could not be found
	 */
	@Override
	public VirtualHost fetchByNotL_H_First(
		long layoutSetId, String hostname,
		OrderByComparator<VirtualHost> orderByComparator) {

		return _collectionPersistenceFinderByNotL_H.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetId, new String[] {hostname}},
			orderByComparator);
	}

	/**
	 * Returns all the virtual hosts where layoutSetId &ne; &#63; and hostname = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VirtualHostModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetId the layout set ID
	 * @param hostnames the hostnames
	 * @return the matching virtual hosts
	 */
	@Override
	public List<VirtualHost> findByNotL_H(
		long layoutSetId, String[] hostnames) {

		return findByNotL_H(
			layoutSetId, hostnames, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the virtual hosts where layoutSetId &ne; &#63; and hostname = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VirtualHostModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetId the layout set ID
	 * @param hostnames the hostnames
	 * @param start the lower bound of the range of virtual hosts
	 * @param end the upper bound of the range of virtual hosts (not inclusive)
	 * @return the range of matching virtual hosts
	 */
	@Override
	public List<VirtualHost> findByNotL_H(
		long layoutSetId, String[] hostnames, int start, int end) {

		return findByNotL_H(layoutSetId, hostnames, start, end, null);
	}

	/**
	 * Returns an ordered range of all the virtual hosts where layoutSetId &ne; &#63; and hostname = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VirtualHostModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetId the layout set ID
	 * @param hostnames the hostnames
	 * @param start the lower bound of the range of virtual hosts
	 * @param end the upper bound of the range of virtual hosts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching virtual hosts
	 */
	@Override
	public List<VirtualHost> findByNotL_H(
		long layoutSetId, String[] hostnames, int start, int end,
		OrderByComparator<VirtualHost> orderByComparator) {

		return findByNotL_H(
			layoutSetId, hostnames, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the virtual hosts where layoutSetId &ne; &#63; and hostname = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VirtualHostModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetId the layout set ID
	 * @param hostnames the hostnames
	 * @param start the lower bound of the range of virtual hosts
	 * @param end the upper bound of the range of virtual hosts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching virtual hosts
	 */
	@Override
	public List<VirtualHost> findByNotL_H(
		long layoutSetId, String[] hostnames, int start, int end,
		OrderByComparator<VirtualHost> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByNotL_H.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetId, ArrayUtil.sortedUnique(hostnames)},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the virtual hosts where layoutSetId &ne; &#63; and hostname = &#63; from the database.
	 *
	 * @param layoutSetId the layout set ID
	 * @param hostname the hostname
	 */
	@Override
	public void removeByNotL_H(long layoutSetId, String hostname) {
		_collectionPersistenceFinderByNotL_H.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetId, new String[] {hostname}});
	}

	/**
	 * Returns the number of virtual hosts where layoutSetId &ne; &#63; and hostname = &#63;.
	 *
	 * @param layoutSetId the layout set ID
	 * @param hostname the hostname
	 * @return the number of matching virtual hosts
	 */
	@Override
	public int countByNotL_H(long layoutSetId, String hostname) {
		return _collectionPersistenceFinderByNotL_H.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetId, new String[] {hostname}});
	}

	/**
	 * Returns the number of virtual hosts where layoutSetId &ne; &#63; and hostname = any &#63;.
	 *
	 * @param layoutSetId the layout set ID
	 * @param hostnames the hostnames
	 * @return the number of matching virtual hosts
	 */
	@Override
	public int countByNotL_H(long layoutSetId, String[] hostnames) {
		return _collectionPersistenceFinderByNotL_H.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetId, ArrayUtil.sortedUnique(hostnames)});
	}

	public VirtualHostPersistenceImpl() {
		setModelClass(VirtualHost.class);

		setModelImplClass(VirtualHostImpl.class);
		setModelPKClass(long.class);

		setTable(VirtualHostTable.INSTANCE);
	}

	/**
	 * Creates a new virtual host with the primary key. Does not add the virtual host to the database.
	 *
	 * @param virtualHostId the primary key for the new virtual host
	 * @return the new virtual host
	 */
	@Override
	public VirtualHost create(long virtualHostId) {
		VirtualHost virtualHost = new VirtualHostImpl();

		virtualHost.setNew(true);
		virtualHost.setPrimaryKey(virtualHostId);

		return virtualHost;
	}

	/**
	 * Removes the virtual host with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param virtualHostId the primary key of the virtual host
	 * @return the virtual host that was removed
	 * @throws NoSuchVirtualHostException if a virtual host with the primary key could not be found
	 */
	@Override
	public VirtualHost remove(long virtualHostId)
		throws NoSuchVirtualHostException {

		return remove((Serializable)virtualHostId);
	}

	@Override
	protected VirtualHost removeImpl(VirtualHost virtualHost) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(virtualHost)) {
				virtualHost = (VirtualHost)session.get(
					VirtualHostImpl.class, virtualHost.getPrimaryKeyObj());
			}

			if ((virtualHost != null) &&
				CTPersistenceHelperUtil.isRemove(virtualHost)) {

				session.delete(virtualHost);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (virtualHost != null) {
			clearCache(virtualHost);
		}

		return virtualHost;
	}

	@Override
	public VirtualHost updateImpl(VirtualHost virtualHost) {
		boolean isNew = virtualHost.isNew();

		if (!(virtualHost instanceof VirtualHostModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(virtualHost.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(virtualHost);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in virtualHost proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom VirtualHost implementation " +
					virtualHost.getClass());
		}

		VirtualHostModelImpl virtualHostModelImpl =
			(VirtualHostModelImpl)virtualHost;

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(virtualHost)) {
				if (!isNew) {
					session.evict(
						VirtualHostImpl.class, virtualHost.getPrimaryKeyObj());
				}

				session.save(virtualHost);
			}
			else {
				virtualHost = (VirtualHost)session.merge(virtualHost);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(virtualHost, false);

		if (isNew) {
			virtualHost.setNew(false);
		}

		virtualHost.resetOriginalValues();

		return virtualHost;
	}

	/**
	 * Returns the virtual host with the primary key or throws a <code>NoSuchVirtualHostException</code> if it could not be found.
	 *
	 * @param virtualHostId the primary key of the virtual host
	 * @return the virtual host
	 * @throws NoSuchVirtualHostException if a virtual host with the primary key could not be found
	 */
	@Override
	public VirtualHost findByPrimaryKey(long virtualHostId)
		throws NoSuchVirtualHostException {

		return findByPrimaryKey((Serializable)virtualHostId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the virtual host with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param virtualHostId the primary key of the virtual host
	 * @return the virtual host, or <code>null</code> if a virtual host with the primary key could not be found
	 */
	@Override
	public VirtualHost fetchByPrimaryKey(long virtualHostId) {
		return fetchByPrimaryKey((Serializable)virtualHostId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "virtualHostId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_VIRTUALHOST;
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
		return VirtualHostModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "VirtualHost";
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
		ctMergeColumnNames.add("layoutSetId");
		ctMergeColumnNames.add("hostname");
		ctMergeColumnNames.add("defaultVirtualHost");
		ctMergeColumnNames.add("languageId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("virtualHostId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"hostname"});
	}

	/**
	 * Initializes the virtual host persistence.
	 */
	public void afterPropertiesSet() {
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
				_SQL_SELECT_VIRTUALHOST_WHERE, _SQL_COUNT_VIRTUALHOST_WHERE,
				VirtualHostModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"virtualHost.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, VirtualHost::getCompanyId));

		_uniquePersistenceFinderByHostname = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByHostname",
				new String[] {String.class.getName()},
				new String[] {"hostname"}, 0, 1, false,
				convertNullFunction(VirtualHost::getHostname)),
			_SQL_SELECT_VIRTUALHOST_WHERE, "",
			new FinderColumn<>(
				"virtualHost.", "hostname", FinderColumn.Type.STRING, "=", true,
				true, VirtualHost::getHostname));

		_collectionPersistenceFinderByC_L = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_L",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "layoutSetId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_L",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "layoutSetId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_L",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "layoutSetId"}, false),
			_SQL_SELECT_VIRTUALHOST_WHERE, _SQL_COUNT_VIRTUALHOST_WHERE,
			VirtualHostModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"virtualHost.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, VirtualHost::getCompanyId),
			new FinderColumn<>(
				"virtualHost.", "layoutSetId", FinderColumn.Type.LONG, "=",
				true, true, VirtualHost::getLayoutSetId));

		_collectionPersistenceFinderByNotL_H =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByNotL_H",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"layoutSetId", "hostname"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByNotL_H",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"layoutSetId", "hostname"}, false),
				_SQL_SELECT_VIRTUALHOST_WHERE, _SQL_COUNT_VIRTUALHOST_WHERE,
				VirtualHostModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"virtualHost.", "layoutSetId", FinderColumn.Type.LONG, "!=",
					true, true, VirtualHost::getLayoutSetId),
				new ArrayableFinderColumn<>(
					"virtualHost.", "hostname", FinderColumn.Type.STRING, "=",
					false, true, true, VirtualHost::getHostname));

		VirtualHostUtil.setPersistence(this);
	}

	public void destroy() {
		VirtualHostUtil.setPersistence(null);

		EntityCacheUtil.removeCache(VirtualHostImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		VirtualHostModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_VIRTUALHOST =
		"SELECT virtualHost FROM VirtualHost virtualHost";

	private static final String _SQL_SELECT_VIRTUALHOST_WHERE =
		"SELECT virtualHost FROM VirtualHost virtualHost WHERE ";

	private static final String _SQL_COUNT_VIRTUALHOST_WHERE =
		"SELECT COUNT(virtualHost) FROM VirtualHost virtualHost WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No VirtualHost exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		VirtualHostPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1019819958