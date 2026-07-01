/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherProjectVersionException;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.model.PatcherProjectVersionTable;
import com.liferay.osb.patcher.model.impl.PatcherProjectVersionImpl;
import com.liferay.osb.patcher.model.impl.PatcherProjectVersionModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherProjectVersionPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherProjectVersionUtil;
import com.liferay.osb.patcher.service.persistence.impl.constants.OSBPatcherPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the patcher project version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherProjectVersionPersistence.class)
public class PatcherProjectVersionPersistenceImpl
	extends BasePersistenceImpl
		<PatcherProjectVersion, NoSuchPatcherProjectVersionException>
	implements PatcherProjectVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherProjectVersionUtil</code> to access the patcher project version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherProjectVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<PatcherProjectVersion, NoSuchPatcherProjectVersionException>
			_collectionPersistenceFinderByPatcherProductVersionId;

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByPatcherProductVersionId(
		long patcherProductVersionId, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPatcherProductVersionId.find(
			finderCache, new Object[] {patcherProductVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion findByPatcherProductVersionId_First(
			long patcherProductVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		return _collectionPersistenceFinderByPatcherProductVersionId.findFirst(
			finderCache, new Object[] {patcherProductVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByPatcherProductVersionId_First(
		long patcherProductVersionId,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return _collectionPersistenceFinderByPatcherProductVersionId.fetchFirst(
			finderCache, new Object[] {patcherProductVersionId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher project versions that the user has permissions to view where patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions that the user has permission to view
	 */
	@Override
	public List<PatcherProjectVersion> filterFindByPatcherProductVersionId(
		long patcherProductVersionId, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return _collectionPersistenceFinderByPatcherProductVersionId.filterFind(
			finderCache, new Object[] {patcherProductVersionId}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the patcher project versions where patcherProductVersionId = &#63; from the database.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 */
	@Override
	public void removeByPatcherProductVersionId(long patcherProductVersionId) {
		_collectionPersistenceFinderByPatcherProductVersionId.remove(
			finderCache, new Object[] {patcherProductVersionId});
	}

	/**
	 * Returns the number of patcher project versions where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher project versions
	 */
	@Override
	public int countByPatcherProductVersionId(long patcherProductVersionId) {
		return _collectionPersistenceFinderByPatcherProductVersionId.count(
			finderCache, new Object[] {patcherProductVersionId});
	}

	/**
	 * Returns the number of patcher project versions that the user has permission to view where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher project versions that the user has permission to view
	 */
	@Override
	public int filterCountByPatcherProductVersionId(
		long patcherProductVersionId) {

		return _collectionPersistenceFinderByPatcherProductVersionId.
			filterCount(finderCache, new Object[] {patcherProductVersionId});
	}

	private FilterCollectionPersistenceFinder
		<PatcherProjectVersion, NoSuchPatcherProjectVersionException>
			_collectionPersistenceFinderByRootPatcherProjectVersionId;

	/**
	 * Returns an ordered range of all the patcher project versions where rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByRootPatcherProjectVersionId.find(
			finderCache, new Object[] {rootPatcherProjectVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher project version in the ordered set where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion findByRootPatcherProjectVersionId_First(
			long rootPatcherProjectVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		return _collectionPersistenceFinderByRootPatcherProjectVersionId.
			findFirst(
				finderCache, new Object[] {rootPatcherProjectVersionId},
				orderByComparator);
	}

	/**
	 * Returns the first patcher project version in the ordered set where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByRootPatcherProjectVersionId_First(
		long rootPatcherProjectVersionId,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return _collectionPersistenceFinderByRootPatcherProjectVersionId.
			fetchFirst(
				finderCache, new Object[] {rootPatcherProjectVersionId},
				orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher project versions that the user has permissions to view where rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions that the user has permission to view
	 */
	@Override
	public List<PatcherProjectVersion> filterFindByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return _collectionPersistenceFinderByRootPatcherProjectVersionId.
			filterFind(
				finderCache, new Object[] {rootPatcherProjectVersionId}, start,
				end, orderByComparator);
	}

	/**
	 * Removes all the patcher project versions where rootPatcherProjectVersionId = &#63; from the database.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 */
	@Override
	public void removeByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId) {

		_collectionPersistenceFinderByRootPatcherProjectVersionId.remove(
			finderCache, new Object[] {rootPatcherProjectVersionId});
	}

	/**
	 * Returns the number of patcher project versions where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the number of matching patcher project versions
	 */
	@Override
	public int countByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId) {

		return _collectionPersistenceFinderByRootPatcherProjectVersionId.count(
			finderCache, new Object[] {rootPatcherProjectVersionId});
	}

	/**
	 * Returns the number of patcher project versions that the user has permission to view where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the number of matching patcher project versions that the user has permission to view
	 */
	@Override
	public int filterCountByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId) {

		return _collectionPersistenceFinderByRootPatcherProjectVersionId.
			filterCount(
				finderCache, new Object[] {rootPatcherProjectVersionId});
	}

	private UniquePersistenceFinder
		<PatcherProjectVersion, NoSuchPatcherProjectVersionException>
			_uniquePersistenceFinderByCommittish;

	/**
	 * Returns the patcher project version where committish = &#63; or throws a <code>NoSuchPatcherProjectVersionException</code> if it could not be found.
	 *
	 * @param committish the committish
	 * @return the matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion findByCommittish(String committish)
		throws NoSuchPatcherProjectVersionException {

		return _uniquePersistenceFinderByCommittish.find(
			finderCache, new Object[] {committish});
	}

	/**
	 * Returns the patcher project version where committish = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param committish the committish
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByCommittish(
		String committish, boolean useFinderCache) {

		return _uniquePersistenceFinderByCommittish.fetch(
			finderCache, new Object[] {committish}, useFinderCache);
	}

	/**
	 * Removes the patcher project version where committish = &#63; from the database.
	 *
	 * @param committish the committish
	 * @return the patcher project version that was removed
	 */
	@Override
	public PatcherProjectVersion removeByCommittish(String committish)
		throws NoSuchPatcherProjectVersionException {

		PatcherProjectVersion patcherProjectVersion = findByCommittish(
			committish);

		return remove(patcherProjectVersion);
	}

	/**
	 * Returns the number of patcher project versions where committish = &#63;.
	 *
	 * @param committish the committish
	 * @return the number of matching patcher project versions
	 */
	@Override
	public int countByCommittish(String committish) {
		return _uniquePersistenceFinderByCommittish.count(
			finderCache, new Object[] {committish});
	}

	private UniquePersistenceFinder
		<PatcherProjectVersion, NoSuchPatcherProjectVersionException>
			_uniquePersistenceFinderByName;

	/**
	 * Returns the patcher project version where name = &#63; or throws a <code>NoSuchPatcherProjectVersionException</code> if it could not be found.
	 *
	 * @param name the name
	 * @return the matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion findByName(String name)
		throws NoSuchPatcherProjectVersionException {

		return _uniquePersistenceFinderByName.find(
			finderCache, new Object[] {name});
	}

	/**
	 * Returns the patcher project version where name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByName(
		String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByName.fetch(
			finderCache, new Object[] {name}, useFinderCache);
	}

	/**
	 * Removes the patcher project version where name = &#63; from the database.
	 *
	 * @param name the name
	 * @return the patcher project version that was removed
	 */
	@Override
	public PatcherProjectVersion removeByName(String name)
		throws NoSuchPatcherProjectVersionException {

		PatcherProjectVersion patcherProjectVersion = findByName(name);

		return remove(patcherProjectVersion);
	}

	/**
	 * Returns the number of patcher project versions where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching patcher project versions
	 */
	@Override
	public int countByName(String name) {
		return _uniquePersistenceFinderByName.count(
			finderCache, new Object[] {name});
	}

	private FilterCollectionPersistenceFinder
		<PatcherProjectVersion, NoSuchPatcherProjectVersionException>
			_collectionPersistenceFinderByP_R;

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_R.find(
			finderCache,
			new Object[] {patcherProductVersionId, rootPatcherProjectVersionId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion findByP_R_First(
			long patcherProductVersionId, long rootPatcherProjectVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		return _collectionPersistenceFinderByP_R.findFirst(
			finderCache,
			new Object[] {patcherProductVersionId, rootPatcherProjectVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByP_R_First(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return _collectionPersistenceFinderByP_R.fetchFirst(
			finderCache,
			new Object[] {patcherProductVersionId, rootPatcherProjectVersionId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher project versions that the user has permissions to view where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions that the user has permission to view
	 */
	@Override
	public List<PatcherProjectVersion> filterFindByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return _collectionPersistenceFinderByP_R.filterFind(
			finderCache,
			new Object[] {patcherProductVersionId, rootPatcherProjectVersionId},
			start, end, orderByComparator);
	}

	/**
	 * Removes all the patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63; from the database.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 */
	@Override
	public void removeByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId) {

		_collectionPersistenceFinderByP_R.remove(
			finderCache,
			new Object[] {
				patcherProductVersionId, rootPatcherProjectVersionId
			});
	}

	/**
	 * Returns the number of patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the number of matching patcher project versions
	 */
	@Override
	public int countByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId) {

		return _collectionPersistenceFinderByP_R.count(
			finderCache,
			new Object[] {
				patcherProductVersionId, rootPatcherProjectVersionId
			});
	}

	/**
	 * Returns the number of patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the number of matching patcher project versions that the user has permission to view
	 */
	@Override
	public int filterCountByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId) {

		return _collectionPersistenceFinderByP_R.filterCount(
			finderCache,
			new Object[] {
				patcherProductVersionId, rootPatcherProjectVersionId
			});
	}

	private FilterCollectionPersistenceFinder
		<PatcherProjectVersion, NoSuchPatcherProjectVersionException>
			_collectionPersistenceFinderByP_RN;

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByP_RN(
		long patcherProductVersionId, String repositoryName, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_RN.find(
			finderCache, new Object[] {patcherProductVersionId, repositoryName},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion findByP_RN_First(
			long patcherProductVersionId, String repositoryName,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		return _collectionPersistenceFinderByP_RN.findFirst(
			finderCache, new Object[] {patcherProductVersionId, repositoryName},
			orderByComparator);
	}

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByP_RN_First(
		long patcherProductVersionId, String repositoryName,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return _collectionPersistenceFinderByP_RN.fetchFirst(
			finderCache, new Object[] {patcherProductVersionId, repositoryName},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher project versions that the user has permissions to view where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions that the user has permission to view
	 */
	@Override
	public List<PatcherProjectVersion> filterFindByP_RN(
		long patcherProductVersionId, String repositoryName, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return _collectionPersistenceFinderByP_RN.filterFind(
			finderCache, new Object[] {patcherProductVersionId, repositoryName},
			start, end, orderByComparator);
	}

	/**
	 * Removes all the patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63; from the database.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 */
	@Override
	public void removeByP_RN(
		long patcherProductVersionId, String repositoryName) {

		_collectionPersistenceFinderByP_RN.remove(
			finderCache,
			new Object[] {patcherProductVersionId, repositoryName});
	}

	/**
	 * Returns the number of patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @return the number of matching patcher project versions
	 */
	@Override
	public int countByP_RN(
		long patcherProductVersionId, String repositoryName) {

		return _collectionPersistenceFinderByP_RN.count(
			finderCache,
			new Object[] {patcherProductVersionId, repositoryName});
	}

	/**
	 * Returns the number of patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @return the number of matching patcher project versions that the user has permission to view
	 */
	@Override
	public int filterCountByP_RN(
		long patcherProductVersionId, String repositoryName) {

		return _collectionPersistenceFinderByP_RN.filterCount(
			finderCache,
			new Object[] {patcherProductVersionId, repositoryName});
	}

	public PatcherProjectVersionPersistenceImpl() {
		setModelClass(PatcherProjectVersion.class);

		setModelImplClass(PatcherProjectVersionImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherProjectVersionTable.INSTANCE);
	}

	/**
	 * Creates a new patcher project version with the primary key. Does not add the patcher project version to the database.
	 *
	 * @param patcherProjectVersionId the primary key for the new patcher project version
	 * @return the new patcher project version
	 */
	@Override
	public PatcherProjectVersion create(long patcherProjectVersionId) {
		PatcherProjectVersion patcherProjectVersion =
			new PatcherProjectVersionImpl();

		patcherProjectVersion.setNew(true);
		patcherProjectVersion.setPrimaryKey(patcherProjectVersionId);

		patcherProjectVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherProjectVersion;
	}

	/**
	 * Removes the patcher project version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version that was removed
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion remove(long patcherProjectVersionId)
		throws NoSuchPatcherProjectVersionException {

		return remove((Serializable)patcherProjectVersionId);
	}

	@Override
	protected PatcherProjectVersion removeImpl(
		PatcherProjectVersion patcherProjectVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherProjectVersion)) {
				patcherProjectVersion = (PatcherProjectVersion)session.get(
					PatcherProjectVersionImpl.class,
					patcherProjectVersion.getPrimaryKeyObj());
			}

			if (patcherProjectVersion != null) {
				session.delete(patcherProjectVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherProjectVersion != null) {
			clearCache(patcherProjectVersion);
		}

		return patcherProjectVersion;
	}

	@Override
	public PatcherProjectVersion updateImpl(
		PatcherProjectVersion patcherProjectVersion) {

		boolean isNew = patcherProjectVersion.isNew();

		if (!(patcherProjectVersion instanceof
				PatcherProjectVersionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherProjectVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherProjectVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherProjectVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherProjectVersion implementation " +
					patcherProjectVersion.getClass());
		}

		PatcherProjectVersionModelImpl patcherProjectVersionModelImpl =
			(PatcherProjectVersionModelImpl)patcherProjectVersion;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherProjectVersion.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherProjectVersion.setCreateDate(date);
			}
			else {
				patcherProjectVersion.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!patcherProjectVersionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherProjectVersion.setModifiedDate(date);
			}
			else {
				patcherProjectVersion.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherProjectVersion);
			}
			else {
				patcherProjectVersion = (PatcherProjectVersion)session.merge(
					patcherProjectVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(patcherProjectVersion, false);

		if (isNew) {
			patcherProjectVersion.setNew(false);
		}

		patcherProjectVersion.resetOriginalValues();

		return patcherProjectVersion;
	}

	/**
	 * Returns the patcher project version with the primary key or throws a <code>NoSuchPatcherProjectVersionException</code> if it could not be found.
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion findByPrimaryKey(long patcherProjectVersionId)
		throws NoSuchPatcherProjectVersionException {

		return findByPrimaryKey((Serializable)patcherProjectVersionId);
	}

	/**
	 * Returns the patcher project version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version, or <code>null</code> if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByPrimaryKey(
		long patcherProjectVersionId) {

		return fetchByPrimaryKey((Serializable)patcherProjectVersionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "patcherProjectVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERPROJECTVERSION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherProjectVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher project version persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByPatcherProductVersionId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByPatcherProductVersionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"patcherProductVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByPatcherProductVersionId",
					new String[] {Long.class.getName()},
					new String[] {"patcherProductVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByPatcherProductVersionId",
					new String[] {Long.class.getName()},
					new String[] {"patcherProductVersionId"}, false),
				_SQL_SELECT_PATCHERPROJECTVERSION_WHERE,
				_SQL_COUNT_PATCHERPROJECTVERSION_WHERE,
				PatcherProjectVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"patcherProjectVersion.", "patcherProductVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherProjectVersion::getPatcherProductVersionId));

		_collectionPersistenceFinderByRootPatcherProjectVersionId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByRootPatcherProjectVersionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"rootPatcherProjectVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByRootPatcherProjectVersionId",
					new String[] {Long.class.getName()},
					new String[] {"rootPatcherProjectVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByRootPatcherProjectVersionId",
					new String[] {Long.class.getName()},
					new String[] {"rootPatcherProjectVersionId"}, false),
				_SQL_SELECT_PATCHERPROJECTVERSION_WHERE,
				_SQL_COUNT_PATCHERPROJECTVERSION_WHERE,
				PatcherProjectVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"patcherProjectVersion.", "rootPatcherProjectVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherProjectVersion::getRootPatcherProjectVersionId));

		_uniquePersistenceFinderByCommittish = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCommittish",
				new String[] {String.class.getName()},
				new String[] {"committish"}, 0, 1, false,
				convertNullFunction(PatcherProjectVersion::getCommittish)),
			_SQL_SELECT_PATCHERPROJECTVERSION_WHERE, "",
			new FinderColumn<>(
				"patcherProjectVersion.", "committish",
				FinderColumn.Type.STRING, "=", true, true,
				PatcherProjectVersion::getCommittish));

		_uniquePersistenceFinderByName = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByName",
				new String[] {String.class.getName()}, new String[] {"name"}, 0,
				1, false, convertNullFunction(PatcherProjectVersion::getName)),
			_SQL_SELECT_PATCHERPROJECTVERSION_WHERE, "",
			new FinderColumn<>(
				"patcherProjectVersion.", "name", FinderColumn.Type.STRING, "=",
				true, true, PatcherProjectVersion::getName));

		_collectionPersistenceFinderByP_R =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_R",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"patcherProductVersionId", "rootPatcherProjectVersionId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_R",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {
						"patcherProductVersionId", "rootPatcherProjectVersionId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_R",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {
						"patcherProductVersionId", "rootPatcherProjectVersionId"
					},
					false),
				_SQL_SELECT_PATCHERPROJECTVERSION_WHERE,
				_SQL_COUNT_PATCHERPROJECTVERSION_WHERE,
				PatcherProjectVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"patcherProjectVersion.", "patcherProductVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherProjectVersion::getPatcherProductVersionId),
				new FinderColumn<>(
					"patcherProjectVersion.", "rootPatcherProjectVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherProjectVersion::getRootPatcherProjectVersionId));

		_collectionPersistenceFinderByP_RN =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_RN",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"patcherProductVersionId", "repositoryName"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_RN",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"patcherProductVersionId", "repositoryName"},
					0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_RN",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"patcherProductVersionId", "repositoryName"},
					0, 2, false, null),
				_SQL_SELECT_PATCHERPROJECTVERSION_WHERE,
				_SQL_COUNT_PATCHERPROJECTVERSION_WHERE,
				PatcherProjectVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"patcherProjectVersion.", "patcherProductVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherProjectVersion::getPatcherProductVersionId),
				new FinderColumn<>(
					"patcherProjectVersion.", "repositoryName",
					FinderColumn.Type.STRING, "=", true, true,
					PatcherProjectVersion::getRepositoryName));

		PatcherProjectVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherProjectVersionUtil.setPersistence(null);

		entityCache.removeCache(PatcherProjectVersionImpl.class.getName());
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		PatcherProjectVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PATCHERPROJECTVERSION =
		"SELECT patcherProjectVersion FROM PatcherProjectVersion patcherProjectVersion";

	private static final String _SQL_SELECT_PATCHERPROJECTVERSION_WHERE =
		"SELECT patcherProjectVersion FROM PatcherProjectVersion patcherProjectVersion WHERE ";

	private static final String _SQL_COUNT_PATCHERPROJECTVERSION_WHERE =
		"SELECT COUNT(patcherProjectVersion) FROM PatcherProjectVersion patcherProjectVersion WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1998809827