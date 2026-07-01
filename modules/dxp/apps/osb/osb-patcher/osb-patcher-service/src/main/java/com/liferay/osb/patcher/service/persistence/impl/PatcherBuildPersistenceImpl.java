/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherBuildException;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherBuildTable;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.impl.PatcherBuildImpl;
import com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherBuildPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherBuildUtil;
import com.liferay.osb.patcher.service.persistence.impl.constants.OSBPatcherPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
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
 * The persistence implementation for the patcher build service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherBuildPersistence.class)
public class PatcherBuildPersistenceImpl
	extends BasePersistenceImpl<PatcherBuild, NoSuchPatcherBuildException>
	implements PatcherBuildPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherBuildUtil</code> to access the patcher build persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherBuildImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<PatcherBuild, NoSuchPatcherBuildException>
			_collectionPersistenceFinderByPatcherFixId;

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByPatcherFixId(
		long patcherFixId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPatcherFixId.find(
			finderCache, new Object[] {patcherFixId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByPatcherFixId_First(
			long patcherFixId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		return _collectionPersistenceFinderByPatcherFixId.findFirst(
			finderCache, new Object[] {patcherFixId}, orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByPatcherFixId_First(
		long patcherFixId, OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByPatcherFixId.fetchFirst(
			finderCache, new Object[] {patcherFixId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByPatcherFixId(
		long patcherFixId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByPatcherFixId.filterFind(
			finderCache, new Object[] {patcherFixId}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the patcher builds where patcherFixId = &#63; from the database.
	 *
	 * @param patcherFixId the patcher fix ID
	 */
	@Override
	public void removeByPatcherFixId(long patcherFixId) {
		_collectionPersistenceFinderByPatcherFixId.remove(
			finderCache, new Object[] {patcherFixId});
	}

	/**
	 * Returns the number of patcher builds where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByPatcherFixId(long patcherFixId) {
		return _collectionPersistenceFinderByPatcherFixId.count(
			finderCache, new Object[] {patcherFixId});
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByPatcherFixId(long patcherFixId) {
		return _collectionPersistenceFinderByPatcherFixId.filterCount(
			finderCache, new Object[] {patcherFixId});
	}

	private FilterCollectionPersistenceFinder
		<PatcherBuild, NoSuchPatcherBuildException>
			_collectionPersistenceFinderByPatcherProjectVersionId;

	/**
	 * Returns an ordered range of all the patcher builds where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPatcherProjectVersionId.find(
			finderCache, new Object[] {patcherProjectVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByPatcherProjectVersionId_First(
			long patcherProjectVersionId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		return _collectionPersistenceFinderByPatcherProjectVersionId.findFirst(
			finderCache, new Object[] {patcherProjectVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByPatcherProjectVersionId_First(
		long patcherProjectVersionId,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByPatcherProjectVersionId.fetchFirst(
			finderCache, new Object[] {patcherProjectVersionId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByPatcherProjectVersionId.filterFind(
			finderCache, new Object[] {patcherProjectVersionId}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the patcher builds where patcherProjectVersionId = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 */
	@Override
	public void removeByPatcherProjectVersionId(long patcherProjectVersionId) {
		_collectionPersistenceFinderByPatcherProjectVersionId.remove(
			finderCache, new Object[] {patcherProjectVersionId});
	}

	/**
	 * Returns the number of patcher builds where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByPatcherProjectVersionId(long patcherProjectVersionId) {
		return _collectionPersistenceFinderByPatcherProjectVersionId.count(
			finderCache, new Object[] {patcherProjectVersionId});
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return _collectionPersistenceFinderByPatcherProjectVersionId.
			filterCount(finderCache, new Object[] {patcherProjectVersionId});
	}

	private FilterCollectionPersistenceFinder
		<PatcherBuild, NoSuchPatcherBuildException>
			_collectionPersistenceFinderByKey;

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByKey(
		String key, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKey.find(
			finderCache, new Object[] {key}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByKey_First(
			String key, OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		return _collectionPersistenceFinderByKey.findFirst(
			finderCache, new Object[] {key}, orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByKey_First(
		String key, OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByKey.fetchFirst(
			finderCache, new Object[] {key}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByKey(
		String key, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByKey.filterFind(
			finderCache, new Object[] {key}, start, end, orderByComparator);
	}

	/**
	 * Removes all the patcher builds where key = &#63; from the database.
	 *
	 * @param key the key
	 */
	@Override
	public void removeByKey(String key) {
		_collectionPersistenceFinderByKey.remove(
			finderCache, new Object[] {key});
	}

	/**
	 * Returns the number of patcher builds where key = &#63;.
	 *
	 * @param key the key
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByKey(String key) {
		return _collectionPersistenceFinderByKey.count(
			finderCache, new Object[] {key});
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where key = &#63;.
	 *
	 * @param key the key
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByKey(String key) {
		return _collectionPersistenceFinderByKey.filterCount(
			finderCache, new Object[] {key});
	}

	private FilterCollectionPersistenceFinder
		<PatcherBuild, NoSuchPatcherBuildException>
			_collectionPersistenceFinderByP_P;

	/**
	 * Returns an ordered range of all the patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_P(
		long patcherAccountId, long patcherProductVersionId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_P.find(
			finderCache,
			new Object[] {patcherAccountId, patcherProductVersionId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByP_P_First(
			long patcherAccountId, long patcherProductVersionId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		return _collectionPersistenceFinderByP_P.findFirst(
			finderCache,
			new Object[] {patcherAccountId, patcherProductVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByP_P_First(
		long patcherAccountId, long patcherProductVersionId,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByP_P.fetchFirst(
			finderCache,
			new Object[] {patcherAccountId, patcherProductVersionId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByP_P(
		long patcherAccountId, long patcherProductVersionId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByP_P.filterFind(
			finderCache,
			new Object[] {patcherAccountId, patcherProductVersionId}, start,
			end, orderByComparator);
	}

	/**
	 * Removes all the patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63; from the database.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 */
	@Override
	public void removeByP_P(
		long patcherAccountId, long patcherProductVersionId) {

		_collectionPersistenceFinderByP_P.remove(
			finderCache,
			new Object[] {patcherAccountId, patcherProductVersionId});
	}

	/**
	 * Returns the number of patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByP_P(long patcherAccountId, long patcherProductVersionId) {
		return _collectionPersistenceFinderByP_P.count(
			finderCache,
			new Object[] {patcherAccountId, patcherProductVersionId});
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByP_P(
		long patcherAccountId, long patcherProductVersionId) {

		return _collectionPersistenceFinderByP_P.filterCount(
			finderCache,
			new Object[] {patcherAccountId, patcherProductVersionId});
	}

	private FilterCollectionPersistenceFinder
		<PatcherBuild, NoSuchPatcherBuildException>
			_collectionPersistenceFinderByP_C;

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_C(
		long patcherFixId, boolean childBuild, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_C.find(
			finderCache, new Object[] {patcherFixId, childBuild}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByP_C_First(
			long patcherFixId, boolean childBuild,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		return _collectionPersistenceFinderByP_C.findFirst(
			finderCache, new Object[] {patcherFixId, childBuild},
			orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByP_C_First(
		long patcherFixId, boolean childBuild,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByP_C.fetchFirst(
			finderCache, new Object[] {patcherFixId, childBuild},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByP_C(
		long patcherFixId, boolean childBuild, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByP_C.filterFind(
			finderCache, new Object[] {patcherFixId, childBuild}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the patcher builds where patcherFixId = &#63; and childBuild = &#63; from the database.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 */
	@Override
	public void removeByP_C(long patcherFixId, boolean childBuild) {
		_collectionPersistenceFinderByP_C.remove(
			finderCache, new Object[] {patcherFixId, childBuild});
	}

	/**
	 * Returns the number of patcher builds where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByP_C(long patcherFixId, boolean childBuild) {
		return _collectionPersistenceFinderByP_C.count(
			finderCache, new Object[] {patcherFixId, childBuild});
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByP_C(long patcherFixId, boolean childBuild) {
		return _collectionPersistenceFinderByP_C.filterCount(
			finderCache, new Object[] {patcherFixId, childBuild});
	}

	private UniquePersistenceFinder<PatcherBuild, NoSuchPatcherBuildException>
		_uniquePersistenceFinderByK_KV;

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or throws a <code>NoSuchPatcherBuildException</code> if it could not be found.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByK_KV(String key, double keyVersion)
		throws NoSuchPatcherBuildException {

		return _uniquePersistenceFinderByK_KV.find(
			finderCache, new Object[] {key, keyVersion});
	}

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByK_KV(
		String key, double keyVersion, boolean useFinderCache) {

		return _uniquePersistenceFinderByK_KV.fetch(
			finderCache, new Object[] {key, keyVersion}, useFinderCache);
	}

	/**
	 * Removes the patcher build where key = &#63; and keyVersion = &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the patcher build that was removed
	 */
	@Override
	public PatcherBuild removeByK_KV(String key, double keyVersion)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = findByK_KV(key, keyVersion);

		return remove(patcherBuild);
	}

	/**
	 * Returns the number of patcher builds where key = &#63; and keyVersion = &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByK_KV(String key, double keyVersion) {
		return _uniquePersistenceFinderByK_KV.count(
			finderCache, new Object[] {key, keyVersion});
	}

	private FilterCollectionPersistenceFinder
		<PatcherBuild, NoSuchPatcherBuildException>
			_collectionPersistenceFinderByK_GtKV;

	/**
	 * Returns all the patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_GtKV(String key, double keyVersion) {
		return findByK_GtKV(
			key, keyVersion, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_GtKV(
		String key, double keyVersion, int start, int end) {

		return findByK_GtKV(key, keyVersion, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_GtKV(
		String key, double keyVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByK_GtKV(
			key, keyVersion, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_GtKV(
		String key, double keyVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByK_GtKV.find(
			finderCache, new Object[] {key, keyVersion}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByK_GtKV_First(
			String key, double keyVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		return _collectionPersistenceFinderByK_GtKV.findFirst(
			finderCache, new Object[] {key, keyVersion}, orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByK_GtKV_First(
		String key, double keyVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByK_GtKV.fetchFirst(
			finderCache, new Object[] {key, keyVersion}, orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByK_GtKV(
		String key, double keyVersion) {

		return filterFindByK_GtKV(
			key, keyVersion, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByK_GtKV(
		String key, double keyVersion, int start, int end) {

		return filterFindByK_GtKV(key, keyVersion, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByK_GtKV(
		String key, double keyVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByK_GtKV.filterFind(
			finderCache, new Object[] {key, keyVersion}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the patcher builds where key = &#63; and keyVersion &gt; &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 */
	@Override
	public void removeByK_GtKV(String key, double keyVersion) {
		_collectionPersistenceFinderByK_GtKV.remove(
			finderCache, new Object[] {key, keyVersion});
	}

	/**
	 * Returns the number of patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByK_GtKV(String key, double keyVersion) {
		return _collectionPersistenceFinderByK_GtKV.count(
			finderCache, new Object[] {key, keyVersion});
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByK_GtKV(String key, double keyVersion) {
		return _collectionPersistenceFinderByK_GtKV.filterCount(
			finderCache, new Object[] {key, keyVersion});
	}

	private FilterCollectionPersistenceFinder
		<PatcherBuild, NoSuchPatcherBuildException>
			_collectionPersistenceFinderByK_LtKV;

	/**
	 * Returns all the patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_LtKV(String key, double keyVersion) {
		return findByK_LtKV(
			key, keyVersion, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_LtKV(
		String key, double keyVersion, int start, int end) {

		return findByK_LtKV(key, keyVersion, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_LtKV(
		String key, double keyVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByK_LtKV(
			key, keyVersion, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_LtKV(
		String key, double keyVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByK_LtKV.find(
			finderCache, new Object[] {key, keyVersion}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByK_LtKV_First(
			String key, double keyVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		return _collectionPersistenceFinderByK_LtKV.findFirst(
			finderCache, new Object[] {key, keyVersion}, orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByK_LtKV_First(
		String key, double keyVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByK_LtKV.fetchFirst(
			finderCache, new Object[] {key, keyVersion}, orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByK_LtKV(
		String key, double keyVersion) {

		return filterFindByK_LtKV(
			key, keyVersion, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByK_LtKV(
		String key, double keyVersion, int start, int end) {

		return filterFindByK_LtKV(key, keyVersion, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByK_LtKV(
		String key, double keyVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByK_LtKV.filterFind(
			finderCache, new Object[] {key, keyVersion}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the patcher builds where key = &#63; and keyVersion &lt; &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 */
	@Override
	public void removeByK_LtKV(String key, double keyVersion) {
		_collectionPersistenceFinderByK_LtKV.remove(
			finderCache, new Object[] {key, keyVersion});
	}

	/**
	 * Returns the number of patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByK_LtKV(String key, double keyVersion) {
		return _collectionPersistenceFinderByK_LtKV.count(
			finderCache, new Object[] {key, keyVersion});
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByK_LtKV(String key, double keyVersion) {
		return _collectionPersistenceFinderByK_LtKV.filterCount(
			finderCache, new Object[] {key, keyVersion});
	}

	private FilterCollectionPersistenceFinder
		<PatcherBuild, NoSuchPatcherBuildException>
			_collectionPersistenceFinderByK_L;

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_L(
		String key, boolean latestKeyBuild, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByK_L.find(
			finderCache, new Object[] {key, latestKeyBuild}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByK_L_First(
			String key, boolean latestKeyBuild,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		return _collectionPersistenceFinderByK_L.findFirst(
			finderCache, new Object[] {key, latestKeyBuild}, orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByK_L_First(
		String key, boolean latestKeyBuild,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByK_L.fetchFirst(
			finderCache, new Object[] {key, latestKeyBuild}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByK_L(
		String key, boolean latestKeyBuild, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByK_L.filterFind(
			finderCache, new Object[] {key, latestKeyBuild}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the patcher builds where key = &#63; and latestKeyBuild = &#63; from the database.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 */
	@Override
	public void removeByK_L(String key, boolean latestKeyBuild) {
		_collectionPersistenceFinderByK_L.remove(
			finderCache, new Object[] {key, latestKeyBuild});
	}

	/**
	 * Returns the number of patcher builds where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByK_L(String key, boolean latestKeyBuild) {
		return _collectionPersistenceFinderByK_L.count(
			finderCache, new Object[] {key, latestKeyBuild});
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByK_L(String key, boolean latestKeyBuild) {
		return _collectionPersistenceFinderByK_L.filterCount(
			finderCache, new Object[] {key, latestKeyBuild});
	}

	private FilterCollectionPersistenceFinder
		<PatcherBuild, NoSuchPatcherBuildException>
			_collectionPersistenceFinderByL_S;

	/**
	 * Returns an ordered range of all the patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByL_S(
		boolean latestSupportTicketBuild, String supportTicket, int start,
		int end, OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByL_S.find(
			finderCache, new Object[] {latestSupportTicketBuild, supportTicket},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByL_S_First(
			boolean latestSupportTicketBuild, String supportTicket,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		return _collectionPersistenceFinderByL_S.findFirst(
			finderCache, new Object[] {latestSupportTicketBuild, supportTicket},
			orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByL_S_First(
		boolean latestSupportTicketBuild, String supportTicket,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByL_S.fetchFirst(
			finderCache, new Object[] {latestSupportTicketBuild, supportTicket},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByL_S(
		boolean latestSupportTicketBuild, String supportTicket, int start,
		int end, OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByL_S.filterFind(
			finderCache, new Object[] {latestSupportTicketBuild, supportTicket},
			start, end, orderByComparator);
	}

	/**
	 * Removes all the patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63; from the database.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 */
	@Override
	public void removeByL_S(
		boolean latestSupportTicketBuild, String supportTicket) {

		_collectionPersistenceFinderByL_S.remove(
			finderCache,
			new Object[] {latestSupportTicketBuild, supportTicket});
	}

	/**
	 * Returns the number of patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByL_S(
		boolean latestSupportTicketBuild, String supportTicket) {

		return _collectionPersistenceFinderByL_S.count(
			finderCache,
			new Object[] {latestSupportTicketBuild, supportTicket});
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByL_S(
		boolean latestSupportTicketBuild, String supportTicket) {

		return _collectionPersistenceFinderByL_S.filterCount(
			finderCache,
			new Object[] {latestSupportTicketBuild, supportTicket});
	}

	private FilterCollectionPersistenceFinder
		<PatcherBuild, NoSuchPatcherBuildException>
			_collectionPersistenceFinderByS_GtS;

	/**
	 * Returns all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByS_GtS(
		String supportTicket, double supportTicketVersion) {

		return findByS_GtS(
			supportTicket, supportTicketVersion, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end) {

		return findByS_GtS(
			supportTicket, supportTicketVersion, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByS_GtS(
			supportTicket, supportTicketVersion, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByS_GtS.find(
			finderCache, new Object[] {supportTicket, supportTicketVersion},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByS_GtS_First(
			String supportTicket, double supportTicketVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		return _collectionPersistenceFinderByS_GtS.findFirst(
			finderCache, new Object[] {supportTicket, supportTicketVersion},
			orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByS_GtS_First(
		String supportTicket, double supportTicketVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByS_GtS.fetchFirst(
			finderCache, new Object[] {supportTicket, supportTicketVersion},
			orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByS_GtS(
		String supportTicket, double supportTicketVersion) {

		return filterFindByS_GtS(
			supportTicket, supportTicketVersion, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end) {

		return filterFindByS_GtS(
			supportTicket, supportTicketVersion, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByS_GtS.filterFind(
			finderCache, new Object[] {supportTicket, supportTicketVersion},
			start, end, orderByComparator);
	}

	/**
	 * Removes all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63; from the database.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 */
	@Override
	public void removeByS_GtS(
		String supportTicket, double supportTicketVersion) {

		_collectionPersistenceFinderByS_GtS.remove(
			finderCache, new Object[] {supportTicket, supportTicketVersion});
	}

	/**
	 * Returns the number of patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByS_GtS(String supportTicket, double supportTicketVersion) {
		return _collectionPersistenceFinderByS_GtS.count(
			finderCache, new Object[] {supportTicket, supportTicketVersion});
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByS_GtS(
		String supportTicket, double supportTicketVersion) {

		return _collectionPersistenceFinderByS_GtS.filterCount(
			finderCache, new Object[] {supportTicket, supportTicketVersion});
	}

	private FilterCollectionPersistenceFinder
		<PatcherBuild, NoSuchPatcherBuildException>
			_collectionPersistenceFinderByS_LtS;

	/**
	 * Returns all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByS_LtS(
		String supportTicket, double supportTicketVersion) {

		return findByS_LtS(
			supportTicket, supportTicketVersion, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end) {

		return findByS_LtS(
			supportTicket, supportTicketVersion, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByS_LtS(
			supportTicket, supportTicketVersion, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByS_LtS.find(
			finderCache, new Object[] {supportTicket, supportTicketVersion},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByS_LtS_First(
			String supportTicket, double supportTicketVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		return _collectionPersistenceFinderByS_LtS.findFirst(
			finderCache, new Object[] {supportTicket, supportTicketVersion},
			orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByS_LtS_First(
		String supportTicket, double supportTicketVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByS_LtS.fetchFirst(
			finderCache, new Object[] {supportTicket, supportTicketVersion},
			orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByS_LtS(
		String supportTicket, double supportTicketVersion) {

		return filterFindByS_LtS(
			supportTicket, supportTicketVersion, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end) {

		return filterFindByS_LtS(
			supportTicket, supportTicketVersion, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByS_LtS.filterFind(
			finderCache, new Object[] {supportTicket, supportTicketVersion},
			start, end, orderByComparator);
	}

	/**
	 * Removes all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63; from the database.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 */
	@Override
	public void removeByS_LtS(
		String supportTicket, double supportTicketVersion) {

		_collectionPersistenceFinderByS_LtS.remove(
			finderCache, new Object[] {supportTicket, supportTicketVersion});
	}

	/**
	 * Returns the number of patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByS_LtS(String supportTicket, double supportTicketVersion) {
		return _collectionPersistenceFinderByS_LtS.count(
			finderCache, new Object[] {supportTicket, supportTicketVersion});
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByS_LtS(
		String supportTicket, double supportTicketVersion) {

		return _collectionPersistenceFinderByS_LtS.filterCount(
			finderCache, new Object[] {supportTicket, supportTicketVersion});
	}

	private FilterCollectionPersistenceFinder
		<PatcherBuild, NoSuchPatcherBuildException>
			_collectionPersistenceFinderByLtM_N_S;

	/**
	 * Returns all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int status) {

		return findByLtM_N_S(
			modifiedDate, notified, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end) {

		return findByLtM_N_S(modifiedDate, notified, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByLtM_N_S(
			modifiedDate, notified, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtM_N_S.find(
			finderCache,
			new Object[] {modifiedDate, notified, new int[] {status}}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByLtM_N_S_First(
			Date modifiedDate, boolean notified, int status,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		return _collectionPersistenceFinderByLtM_N_S.findFirst(
			finderCache,
			new Object[] {modifiedDate, notified, new int[] {status}},
			orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByLtM_N_S_First(
		Date modifiedDate, boolean notified, int status,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByLtM_N_S.fetchFirst(
			finderCache,
			new Object[] {modifiedDate, notified, new int[] {status}},
			orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int status) {

		return filterFindByLtM_N_S(
			modifiedDate, notified, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end) {

		return filterFindByLtM_N_S(
			modifiedDate, notified, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByLtM_N_S.filterFind(
			finderCache,
			new Object[] {modifiedDate, notified, new int[] {status}}, start,
			end, orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses) {

		return filterFindByLtM_N_S(
			modifiedDate, notified, statuses, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start,
		int end) {

		return filterFindByLtM_N_S(
			modifiedDate, notified, statuses, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByLtM_N_S.filterFind(
			finderCache,
			new Object[] {
				modifiedDate, notified, ArrayUtil.sortedUnique(statuses)
			},
			start, end, orderByComparator);
	}

	/**
	 * Returns all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses) {

		return findByLtM_N_S(
			modifiedDate, notified, statuses, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start,
		int end) {

		return findByLtM_N_S(
			modifiedDate, notified, statuses, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByLtM_N_S(
			modifiedDate, notified, statuses, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtM_N_S.find(
			finderCache,
			new Object[] {
				modifiedDate, notified, ArrayUtil.sortedUnique(statuses)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63; from the database.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 */
	@Override
	public void removeByLtM_N_S(
		Date modifiedDate, boolean notified, int status) {

		_collectionPersistenceFinderByLtM_N_S.remove(
			finderCache,
			new Object[] {modifiedDate, notified, new int[] {status}});
	}

	/**
	 * Returns the number of patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByLtM_N_S(Date modifiedDate, boolean notified, int status) {
		return _collectionPersistenceFinderByLtM_N_S.count(
			finderCache,
			new Object[] {modifiedDate, notified, new int[] {status}});
	}

	/**
	 * Returns the number of patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses) {

		return _collectionPersistenceFinderByLtM_N_S.count(
			finderCache,
			new Object[] {
				modifiedDate, notified, ArrayUtil.sortedUnique(statuses)
			});
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByLtM_N_S(
		Date modifiedDate, boolean notified, int status) {

		return _collectionPersistenceFinderByLtM_N_S.filterCount(
			finderCache,
			new Object[] {modifiedDate, notified, new int[] {status}});
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses) {

		return _collectionPersistenceFinderByLtM_N_S.filterCount(
			finderCache,
			new Object[] {
				modifiedDate, notified, ArrayUtil.sortedUnique(statuses)
			});
	}

	private FilterCollectionPersistenceFinder
		<PatcherBuild, NoSuchPatcherBuildException>
			_collectionPersistenceFinderByP_NotP_C_NotT;

	/**
	 * Returns all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type) {

		return findByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end) {

		return findByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_NotP_C_NotT.find(
			finderCache,
			new Object[] {
				patcherFixId, patcherProductVersionId, childBuild, type
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByP_NotP_C_NotT_First(
			long patcherFixId, long patcherProductVersionId, boolean childBuild,
			int type, OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		return _collectionPersistenceFinderByP_NotP_C_NotT.findFirst(
			finderCache,
			new Object[] {
				patcherFixId, patcherProductVersionId, childBuild, type
			},
			orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByP_NotP_C_NotT_First(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByP_NotP_C_NotT.fetchFirst(
			finderCache,
			new Object[] {
				patcherFixId, patcherProductVersionId, childBuild, type
			},
			orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type) {

		return filterFindByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end) {

		return filterFindByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByP_NotP_C_NotT.filterFind(
			finderCache,
			new Object[] {
				patcherFixId, patcherProductVersionId, childBuild, type
			},
			start, end, orderByComparator);
	}

	/**
	 * Removes all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63; from the database.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 */
	@Override
	public void removeByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type) {

		_collectionPersistenceFinderByP_NotP_C_NotT.remove(
			finderCache,
			new Object[] {
				patcherFixId, patcherProductVersionId, childBuild, type
			});
	}

	/**
	 * Returns the number of patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type) {

		return _collectionPersistenceFinderByP_NotP_C_NotT.count(
			finderCache,
			new Object[] {
				patcherFixId, patcherProductVersionId, childBuild, type
			});
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type) {

		return _collectionPersistenceFinderByP_NotP_C_NotT.filterCount(
			finderCache,
			new Object[] {
				patcherFixId, patcherProductVersionId, childBuild, type
			});
	}

	private FilterCollectionPersistenceFinder
		<PatcherBuild, NoSuchPatcherBuildException>
			_collectionPersistenceFinderByP_N_L_A;

	/**
	 * Returns an ordered range of all the patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_N_L_A.find(
			finderCache,
			new Object[] {
				patcherProjectVersionId, accountEntryCode, latestKeyBuild, name
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByP_N_L_A_First(
			long patcherProjectVersionId, String accountEntryCode,
			boolean latestKeyBuild, String name,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		return _collectionPersistenceFinderByP_N_L_A.findFirst(
			finderCache,
			new Object[] {
				patcherProjectVersionId, accountEntryCode, latestKeyBuild, name
			},
			orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByP_N_L_A_First(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByP_N_L_A.fetchFirst(
			finderCache,
			new Object[] {
				patcherProjectVersionId, accountEntryCode, latestKeyBuild, name
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return _collectionPersistenceFinderByP_N_L_A.filterFind(
			finderCache,
			new Object[] {
				patcherProjectVersionId, accountEntryCode, latestKeyBuild, name
			},
			start, end, orderByComparator);
	}

	/**
	 * Removes all the patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 */
	@Override
	public void removeByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name) {

		_collectionPersistenceFinderByP_N_L_A.remove(
			finderCache,
			new Object[] {
				patcherProjectVersionId, accountEntryCode, latestKeyBuild, name
			});
	}

	/**
	 * Returns the number of patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name) {

		return _collectionPersistenceFinderByP_N_L_A.count(
			finderCache,
			new Object[] {
				patcherProjectVersionId, accountEntryCode, latestKeyBuild, name
			});
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name) {

		return _collectionPersistenceFinderByP_N_L_A.filterCount(
			finderCache,
			new Object[] {
				patcherProjectVersionId, accountEntryCode, latestKeyBuild, name
			});
	}

	public PatcherBuildPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("key", "key_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(PatcherBuild.class);

		setModelImplClass(PatcherBuildImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherBuildTable.INSTANCE);
	}

	/**
	 * Creates a new patcher build with the primary key. Does not add the patcher build to the database.
	 *
	 * @param patcherBuildId the primary key for the new patcher build
	 * @return the new patcher build
	 */
	@Override
	public PatcherBuild create(long patcherBuildId) {
		PatcherBuild patcherBuild = new PatcherBuildImpl();

		patcherBuild.setNew(true);
		patcherBuild.setPrimaryKey(patcherBuildId);

		patcherBuild.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherBuild;
	}

	/**
	 * Removes the patcher build with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build that was removed
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild remove(long patcherBuildId)
		throws NoSuchPatcherBuildException {

		return remove((Serializable)patcherBuildId);
	}

	@Override
	protected PatcherBuild removeImpl(PatcherBuild patcherBuild) {
		patcherBuildToPatcherAccountTableMapper.
			deleteLeftPrimaryKeyTableMappings(patcherBuild.getPrimaryKey());

		patcherBuildToPatcherFixTableMapper.deleteLeftPrimaryKeyTableMappings(
			patcherBuild.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherBuild)) {
				patcherBuild = (PatcherBuild)session.get(
					PatcherBuildImpl.class, patcherBuild.getPrimaryKeyObj());
			}

			if (patcherBuild != null) {
				session.delete(patcherBuild);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherBuild != null) {
			clearCache(patcherBuild);
		}

		return patcherBuild;
	}

	@Override
	public PatcherBuild updateImpl(PatcherBuild patcherBuild) {
		boolean isNew = patcherBuild.isNew();

		if (!(patcherBuild instanceof PatcherBuildModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherBuild.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherBuild);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherBuild proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherBuild implementation " +
					patcherBuild.getClass());
		}

		PatcherBuildModelImpl patcherBuildModelImpl =
			(PatcherBuildModelImpl)patcherBuild;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherBuild.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherBuild.setCreateDate(date);
			}
			else {
				patcherBuild.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!patcherBuildModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherBuild.setModifiedDate(date);
			}
			else {
				patcherBuild.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherBuild);
			}
			else {
				patcherBuild = (PatcherBuild)session.merge(patcherBuild);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(patcherBuild, false);

		if (isNew) {
			patcherBuild.setNew(false);
		}

		patcherBuild.resetOriginalValues();

		return patcherBuild;
	}

	/**
	 * Returns the patcher build with the primary key or throws a <code>NoSuchPatcherBuildException</code> if it could not be found.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild findByPrimaryKey(long patcherBuildId)
		throws NoSuchPatcherBuildException {

		return findByPrimaryKey((Serializable)patcherBuildId);
	}

	/**
	 * Returns the patcher build with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build, or <code>null</code> if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild fetchByPrimaryKey(long patcherBuildId) {
		return fetchByPrimaryKey((Serializable)patcherBuildId);
	}

	/**
	 * Returns the primaryKeys of patcher accounts associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return long[] of the primaryKeys of patcher accounts associated with the patcher build
	 */
	@Override
	public long[] getPatcherAccountPrimaryKeys(long pk) {
		long[] pks =
			patcherBuildToPatcherAccountTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the patcher build associated with the patcher account.
	 *
	 * @param pk the primary key of the patcher account
	 * @return the patcher builds associated with the patcher account
	 */
	@Override
	public List<PatcherBuild> getPatcherAccountPatcherBuilds(long pk) {
		return getPatcherAccountPatcherBuilds(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the patcher build associated with the patcher account.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher account
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of patcher builds associated with the patcher account
	 */
	@Override
	public List<PatcherBuild> getPatcherAccountPatcherBuilds(
		long pk, int start, int end) {

		return getPatcherAccountPatcherBuilds(pk, start, end, null);
	}

	/**
	 * Returns all the patcher build associated with the patcher account.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher account
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds associated with the patcher account
	 */
	@Override
	public List<PatcherBuild> getPatcherAccountPatcherBuilds(
		long pk, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return patcherBuildToPatcherAccountTableMapper.getLeftBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher accounts associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the number of patcher accounts associated with the patcher build
	 */
	@Override
	public int getPatcherAccountsSize(long pk) {
		long[] pks =
			patcherBuildToPatcherAccountTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher account is associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 * @return <code>true</code> if the patcher account is associated with the patcher build; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherAccount(long pk, long patcherAccountPK) {
		return patcherBuildToPatcherAccountTableMapper.containsTableMapping(
			pk, patcherAccountPK);
	}

	/**
	 * Returns <code>true</code> if the patcher build has any patcher accounts associated with it.
	 *
	 * @param pk the primary key of the patcher build to check for associations with patcher accounts
	 * @return <code>true</code> if the patcher build has any patcher accounts associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherAccounts(long pk) {
		if (getPatcherAccountsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 * @return <code>true</code> if an association between the patcher build and the patcher account was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherAccount(long pk, long patcherAccountPK) {
		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			return patcherBuildToPatcherAccountTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, patcherAccountPK);
		}
		else {
			return patcherBuildToPatcherAccountTableMapper.addTableMapping(
				patcherBuild.getCompanyId(), pk, patcherAccountPK);
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccount the patcher account
	 * @return <code>true</code> if an association between the patcher build and the patcher account was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherAccount(long pk, PatcherAccount patcherAccount) {
		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			return patcherBuildToPatcherAccountTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				patcherAccount.getPrimaryKey());
		}
		else {
			return patcherBuildToPatcherAccountTableMapper.addTableMapping(
				patcherBuild.getCompanyId(), pk,
				patcherAccount.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts
	 * @return <code>true</code> if at least one association between the patcher build and the patcher accounts was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherAccounts(long pk, long[] patcherAccountPKs) {
		long companyId = 0;

		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherBuild.getCompanyId();
		}

		long[] addedKeys =
			patcherBuildToPatcherAccountTableMapper.addTableMappings(
				companyId, pk, patcherAccountPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts
	 * @return <code>true</code> if at least one association between the patcher build and the patcher accounts was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherAccounts(
		long pk, List<PatcherAccount> patcherAccounts) {

		return addPatcherAccounts(
			pk,
			ListUtil.toLongArray(
				patcherAccounts, PatcherAccount.PATCHER_ACCOUNT_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the patcher build and its patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build to clear the associated patcher accounts from
	 */
	@Override
	public void clearPatcherAccounts(long pk) {
		patcherBuildToPatcherAccountTableMapper.
			deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 */
	@Override
	public void removePatcherAccount(long pk, long patcherAccountPK) {
		patcherBuildToPatcherAccountTableMapper.deleteTableMapping(
			pk, patcherAccountPK);
	}

	/**
	 * Removes the association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccount the patcher account
	 */
	@Override
	public void removePatcherAccount(long pk, PatcherAccount patcherAccount) {
		patcherBuildToPatcherAccountTableMapper.deleteTableMapping(
			pk, patcherAccount.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts
	 */
	@Override
	public void removePatcherAccounts(long pk, long[] patcherAccountPKs) {
		patcherBuildToPatcherAccountTableMapper.deleteTableMappings(
			pk, patcherAccountPKs);
	}

	/**
	 * Removes the association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts
	 */
	@Override
	public void removePatcherAccounts(
		long pk, List<PatcherAccount> patcherAccounts) {

		removePatcherAccounts(
			pk,
			ListUtil.toLongArray(
				patcherAccounts, PatcherAccount.PATCHER_ACCOUNT_ID_ACCESSOR));
	}

	/**
	 * Sets the patcher accounts associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts to be associated with the patcher build
	 */
	@Override
	public void setPatcherAccounts(long pk, long[] patcherAccountPKs) {
		Set<Long> newPatcherAccountPKsSet = SetUtil.fromArray(
			patcherAccountPKs);
		Set<Long> oldPatcherAccountPKsSet = SetUtil.fromArray(
			patcherBuildToPatcherAccountTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removePatcherAccountPKsSet = new HashSet<Long>(
			oldPatcherAccountPKsSet);

		removePatcherAccountPKsSet.removeAll(newPatcherAccountPKsSet);

		patcherBuildToPatcherAccountTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removePatcherAccountPKsSet));

		newPatcherAccountPKsSet.removeAll(oldPatcherAccountPKsSet);

		long companyId = 0;

		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherBuild.getCompanyId();
		}

		patcherBuildToPatcherAccountTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newPatcherAccountPKsSet));
	}

	/**
	 * Sets the patcher accounts associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts to be associated with the patcher build
	 */
	@Override
	public void setPatcherAccounts(
		long pk, List<PatcherAccount> patcherAccounts) {

		try {
			long[] patcherAccountPKs = new long[patcherAccounts.size()];

			for (int i = 0; i < patcherAccounts.size(); i++) {
				PatcherAccount patcherAccount = patcherAccounts.get(i);

				patcherAccountPKs[i] = patcherAccount.getPrimaryKey();
			}

			setPatcherAccounts(pk, patcherAccountPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	/**
	 * Returns the primaryKeys of patcher fixes associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return long[] of the primaryKeys of patcher fixes associated with the patcher build
	 */
	@Override
	public long[] getPatcherFixPrimaryKeys(long pk) {
		long[] pks = patcherBuildToPatcherFixTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the patcher build associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the patcher builds associated with the patcher fix
	 */
	@Override
	public List<PatcherBuild> getPatcherFixPatcherBuilds(long pk) {
		return getPatcherFixPatcherBuilds(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the patcher build associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of patcher builds associated with the patcher fix
	 */
	@Override
	public List<PatcherBuild> getPatcherFixPatcherBuilds(
		long pk, int start, int end) {

		return getPatcherFixPatcherBuilds(pk, start, end, null);
	}

	/**
	 * Returns all the patcher build associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds associated with the patcher fix
	 */
	@Override
	public List<PatcherBuild> getPatcherFixPatcherBuilds(
		long pk, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return patcherBuildToPatcherFixTableMapper.getLeftBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher fixes associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the number of patcher fixes associated with the patcher build
	 */
	@Override
	public int getPatcherFixesSize(long pk) {
		long[] pks = patcherBuildToPatcherFixTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher fix is associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if the patcher fix is associated with the patcher build; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherFix(long pk, long patcherFixPK) {
		return patcherBuildToPatcherFixTableMapper.containsTableMapping(
			pk, patcherFixPK);
	}

	/**
	 * Returns <code>true</code> if the patcher build has any patcher fixes associated with it.
	 *
	 * @param pk the primary key of the patcher build to check for associations with patcher fixes
	 * @return <code>true</code> if the patcher build has any patcher fixes associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherFixes(long pk) {
		if (getPatcherFixesSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if an association between the patcher build and the patcher fix was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherFix(long pk, long patcherFixPK) {
		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			return patcherBuildToPatcherFixTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, patcherFixPK);
		}
		else {
			return patcherBuildToPatcherFixTableMapper.addTableMapping(
				patcherBuild.getCompanyId(), pk, patcherFixPK);
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFix the patcher fix
	 * @return <code>true</code> if an association between the patcher build and the patcher fix was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherFix(long pk, PatcherFix patcherFix) {
		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			return patcherBuildToPatcherFixTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				patcherFix.getPrimaryKey());
		}
		else {
			return patcherBuildToPatcherFixTableMapper.addTableMapping(
				patcherBuild.getCompanyId(), pk, patcherFix.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher build and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherFixes(long pk, long[] patcherFixPKs) {
		long companyId = 0;

		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherBuild.getCompanyId();
		}

		long[] addedKeys = patcherBuildToPatcherFixTableMapper.addTableMappings(
			companyId, pk, patcherFixPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixes the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher build and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherFixes(long pk, List<PatcherFix> patcherFixes) {
		return addPatcherFixes(
			pk,
			ListUtil.toLongArray(
				patcherFixes, PatcherFix.PATCHER_FIX_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the patcher build and its patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build to clear the associated patcher fixes from
	 */
	@Override
	public void clearPatcherFixes(long pk) {
		patcherBuildToPatcherFixTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 */
	@Override
	public void removePatcherFix(long pk, long patcherFixPK) {
		patcherBuildToPatcherFixTableMapper.deleteTableMapping(
			pk, patcherFixPK);
	}

	/**
	 * Removes the association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFix the patcher fix
	 */
	@Override
	public void removePatcherFix(long pk, PatcherFix patcherFix) {
		patcherBuildToPatcherFixTableMapper.deleteTableMapping(
			pk, patcherFix.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 */
	@Override
	public void removePatcherFixes(long pk, long[] patcherFixPKs) {
		patcherBuildToPatcherFixTableMapper.deleteTableMappings(
			pk, patcherFixPKs);
	}

	/**
	 * Removes the association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixes the patcher fixes
	 */
	@Override
	public void removePatcherFixes(long pk, List<PatcherFix> patcherFixes) {
		removePatcherFixes(
			pk,
			ListUtil.toLongArray(
				patcherFixes, PatcherFix.PATCHER_FIX_ID_ACCESSOR));
	}

	/**
	 * Sets the patcher fixes associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixes to be associated with the patcher build
	 */
	@Override
	public void setPatcherFixes(long pk, long[] patcherFixPKs) {
		Set<Long> newPatcherFixPKsSet = SetUtil.fromArray(patcherFixPKs);
		Set<Long> oldPatcherFixPKsSet = SetUtil.fromArray(
			patcherBuildToPatcherFixTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removePatcherFixPKsSet = new HashSet<Long>(
			oldPatcherFixPKsSet);

		removePatcherFixPKsSet.removeAll(newPatcherFixPKsSet);

		patcherBuildToPatcherFixTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removePatcherFixPKsSet));

		newPatcherFixPKsSet.removeAll(oldPatcherFixPKsSet);

		long companyId = 0;

		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherBuild.getCompanyId();
		}

		patcherBuildToPatcherFixTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newPatcherFixPKsSet));
	}

	/**
	 * Sets the patcher fixes associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixes the patcher fixes to be associated with the patcher build
	 */
	@Override
	public void setPatcherFixes(long pk, List<PatcherFix> patcherFixes) {
		try {
			long[] patcherFixPKs = new long[patcherFixes.size()];

			for (int i = 0; i < patcherFixes.size(); i++) {
				PatcherFix patcherFix = patcherFixes.get(i);

				patcherFixPKs[i] = patcherFix.getPrimaryKey();
			}

			setPatcherFixes(pk, patcherFixPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
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
		return "patcherBuildId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERBUILD;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherBuildModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher build persistence.
	 */
	@Activate
	public void activate() {
		patcherBuildToPatcherAccountTableMapper =
			TableMapperFactory.getTableMapper(
				"OSBPatcher_PAccounts_PBuilds#patcherBuildId",
				"OSBPatcher_PAccounts_PBuilds", "companyId", "patcherBuildId",
				"patcherAccountId", this, PatcherAccount.class);

		patcherBuildToPatcherFixTableMapper = TableMapperFactory.getTableMapper(
			"OSBPatcher_PBuilds_PFixes#patcherBuildId",
			"OSBPatcher_PBuilds_PFixes", "companyId", "patcherBuildId",
			"patcherFixId", this, PatcherFix.class);

		_collectionPersistenceFinderByPatcherFixId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByPatcherFixId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"patcherFixId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByPatcherFixId", new String[] {Long.class.getName()},
					new String[] {"patcherFixId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByPatcherFixId", new String[] {Long.class.getName()},
					new String[] {"patcherFixId"}, false),
				_SQL_SELECT_PATCHERBUILD_WHERE, _SQL_COUNT_PATCHERBUILD_WHERE,
				PatcherBuildModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"patcherBuild.", "patcherFixId", FinderColumn.Type.LONG,
					"=", true, true, PatcherBuild::getPatcherFixId));

		_collectionPersistenceFinderByPatcherProjectVersionId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByPatcherProjectVersionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"patcherProjectVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByPatcherProjectVersionId",
					new String[] {Long.class.getName()},
					new String[] {"patcherProjectVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByPatcherProjectVersionId",
					new String[] {Long.class.getName()},
					new String[] {"patcherProjectVersionId"}, false),
				_SQL_SELECT_PATCHERBUILD_WHERE, _SQL_COUNT_PATCHERBUILD_WHERE,
				PatcherBuildModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"patcherBuild.", "patcherProjectVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherBuild::getPatcherProjectVersionId));

		_collectionPersistenceFinderByKey =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByKey",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"key_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByKey",
					new String[] {String.class.getName()},
					new String[] {"key_"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByKey",
					new String[] {String.class.getName()},
					new String[] {"key_"}, 0, 1, false, null),
				_SQL_SELECT_PATCHERBUILD_WHERE, _SQL_COUNT_PATCHERBUILD_WHERE,
				PatcherBuildModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"patcherBuild.", "key", "key_", FinderColumn.Type.STRING,
					"=", true, true, PatcherBuild::getKey));

		_collectionPersistenceFinderByP_P =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"patcherAccountId", "patcherProductVersionId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_P",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {
						"patcherAccountId", "patcherProductVersionId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_P",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {
						"patcherAccountId", "patcherProductVersionId"
					},
					false),
				_SQL_SELECT_PATCHERBUILD_WHERE, _SQL_COUNT_PATCHERBUILD_WHERE,
				PatcherBuildModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"patcherBuild.", "patcherAccountId", FinderColumn.Type.LONG,
					"=", true, true, PatcherBuild::getPatcherAccountId),
				new FinderColumn<>(
					"patcherBuild.", "patcherProductVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherBuild::getPatcherProductVersionId));

		_collectionPersistenceFinderByP_C =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_C",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"patcherFixId", "childBuild"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_C",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"patcherFixId", "childBuild"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_C",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"patcherFixId", "childBuild"}, false),
				_SQL_SELECT_PATCHERBUILD_WHERE, _SQL_COUNT_PATCHERBUILD_WHERE,
				PatcherBuildModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"patcherBuild.", "patcherFixId", FinderColumn.Type.LONG,
					"=", true, true, PatcherBuild::getPatcherFixId),
				new FinderColumn<>(
					"patcherBuild.", "childBuild", FinderColumn.Type.BOOLEAN,
					"=", true, true, PatcherBuild::isChildBuild));

		_uniquePersistenceFinderByK_KV = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByK_KV",
				new String[] {String.class.getName(), Double.class.getName()},
				new String[] {"key_", "keyVersion"}, 0, 1, false,
				convertNullFunction(PatcherBuild::getKey),
				PatcherBuild::getKeyVersion),
			_SQL_SELECT_PATCHERBUILD_WHERE, "",
			new FinderColumn<>(
				"patcherBuild.", "key", "key_", FinderColumn.Type.STRING, "=",
				true, true, PatcherBuild::getKey),
			new FinderColumn<>(
				"patcherBuild.", "keyVersion", FinderColumn.Type.DOUBLE, "=",
				true, true, PatcherBuild::getKeyVersion));

		_collectionPersistenceFinderByK_GtKV =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByK_GtKV",
					new String[] {
						String.class.getName(), Double.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"key_", "keyVersion"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByK_GtKV",
					new String[] {
						String.class.getName(), Double.class.getName()
					},
					new String[] {"key_", "keyVersion"}, false),
				_SQL_SELECT_PATCHERBUILD_WHERE, _SQL_COUNT_PATCHERBUILD_WHERE,
				PatcherBuildModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"patcherBuild.", "key", "key_", FinderColumn.Type.STRING,
					"=", true, true, PatcherBuild::getKey),
				new FinderColumn<>(
					"patcherBuild.", "keyVersion", FinderColumn.Type.DOUBLE,
					">", true, true, PatcherBuild::getKeyVersion));

		_collectionPersistenceFinderByK_LtKV =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByK_LtKV",
					new String[] {
						String.class.getName(), Double.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"key_", "keyVersion"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByK_LtKV",
					new String[] {
						String.class.getName(), Double.class.getName()
					},
					new String[] {"key_", "keyVersion"}, false),
				_SQL_SELECT_PATCHERBUILD_WHERE, _SQL_COUNT_PATCHERBUILD_WHERE,
				PatcherBuildModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"patcherBuild.", "key", "key_", FinderColumn.Type.STRING,
					"=", true, true, PatcherBuild::getKey),
				new FinderColumn<>(
					"patcherBuild.", "keyVersion", FinderColumn.Type.DOUBLE,
					"<", true, true, PatcherBuild::getKeyVersion));

		_collectionPersistenceFinderByK_L =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByK_L",
					new String[] {
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"key_", "latestKeyBuild"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByK_L",
					new String[] {
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {"key_", "latestKeyBuild"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByK_L",
					new String[] {
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {"key_", "latestKeyBuild"}, 0, 1, false, null),
				_SQL_SELECT_PATCHERBUILD_WHERE, _SQL_COUNT_PATCHERBUILD_WHERE,
				PatcherBuildModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"patcherBuild.", "key", "key_", FinderColumn.Type.STRING,
					"=", true, true, PatcherBuild::getKey),
				new FinderColumn<>(
					"patcherBuild.", "latestKeyBuild",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					PatcherBuild::isLatestKeyBuild));

		_collectionPersistenceFinderByL_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByL_S",
					new String[] {
						Boolean.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"latestSupportTicketBuild", "supportTicket"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByL_S",
					new String[] {
						Boolean.class.getName(), String.class.getName()
					},
					new String[] {"latestSupportTicketBuild", "supportTicket"},
					0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByL_S",
					new String[] {
						Boolean.class.getName(), String.class.getName()
					},
					new String[] {"latestSupportTicketBuild", "supportTicket"},
					0, 2, false, null),
				_SQL_SELECT_PATCHERBUILD_WHERE, _SQL_COUNT_PATCHERBUILD_WHERE,
				PatcherBuildModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"patcherBuild.", "latestSupportTicketBuild",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					PatcherBuild::isLatestSupportTicketBuild),
				new FinderColumn<>(
					"patcherBuild.", "supportTicket", FinderColumn.Type.STRING,
					"=", true, true, PatcherBuild::getSupportTicket));

		_collectionPersistenceFinderByS_GtS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByS_GtS",
					new String[] {
						String.class.getName(), Double.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"supportTicket", "supportTicketVersion"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByS_GtS",
					new String[] {
						String.class.getName(), Double.class.getName()
					},
					new String[] {"supportTicket", "supportTicketVersion"},
					false),
				_SQL_SELECT_PATCHERBUILD_WHERE, _SQL_COUNT_PATCHERBUILD_WHERE,
				PatcherBuildModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"patcherBuild.", "supportTicket", FinderColumn.Type.STRING,
					"=", true, true, PatcherBuild::getSupportTicket),
				new FinderColumn<>(
					"patcherBuild.", "supportTicketVersion",
					FinderColumn.Type.DOUBLE, ">", true, true,
					PatcherBuild::getSupportTicketVersion));

		_collectionPersistenceFinderByS_LtS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByS_LtS",
					new String[] {
						String.class.getName(), Double.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"supportTicket", "supportTicketVersion"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByS_LtS",
					new String[] {
						String.class.getName(), Double.class.getName()
					},
					new String[] {"supportTicket", "supportTicketVersion"},
					false),
				_SQL_SELECT_PATCHERBUILD_WHERE, _SQL_COUNT_PATCHERBUILD_WHERE,
				PatcherBuildModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"patcherBuild.", "supportTicket", FinderColumn.Type.STRING,
					"=", true, true, PatcherBuild::getSupportTicket),
				new FinderColumn<>(
					"patcherBuild.", "supportTicketVersion",
					FinderColumn.Type.DOUBLE, "<", true, true,
					PatcherBuild::getSupportTicketVersion));

		_collectionPersistenceFinderByLtM_N_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtM_N_S",
					new String[] {
						Date.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"modifiedDate", "notified", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtM_N_S",
					new String[] {
						Date.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {"modifiedDate", "notified", "status"}, false),
				_SQL_SELECT_PATCHERBUILD_WHERE, _SQL_COUNT_PATCHERBUILD_WHERE,
				PatcherBuildModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"patcherBuild.", "modifiedDate", FinderColumn.Type.DATE,
					"<", true, true, PatcherBuild::getModifiedDate),
				new FinderColumn<>(
					"patcherBuild.", "notified", FinderColumn.Type.BOOLEAN, "=",
					true, true, PatcherBuild::isNotified),
				new ArrayableFinderColumn<>(
					"patcherBuild.", "status", FinderColumn.Type.INTEGER, "=",
					false, true, true, PatcherBuild::getStatus));

		_collectionPersistenceFinderByP_NotP_C_NotT =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByP_NotP_C_NotT",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"patcherFixId", "patcherProductVersionId", "childBuild",
						"type_"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByP_NotP_C_NotT",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {
						"patcherFixId", "patcherProductVersionId", "childBuild",
						"type_"
					},
					false),
				_SQL_SELECT_PATCHERBUILD_WHERE, _SQL_COUNT_PATCHERBUILD_WHERE,
				PatcherBuildModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"patcherBuild.", "patcherFixId", FinderColumn.Type.LONG,
					"=", true, true, PatcherBuild::getPatcherFixId),
				new FinderColumn<>(
					"patcherBuild.", "patcherProductVersionId",
					FinderColumn.Type.LONG, "!=", true, true,
					PatcherBuild::getPatcherProductVersionId),
				new FinderColumn<>(
					"patcherBuild.", "childBuild", FinderColumn.Type.BOOLEAN,
					"=", true, true, PatcherBuild::isChildBuild),
				new FinderColumn<>(
					"patcherBuild.", "type", "type_", FinderColumn.Type.INTEGER,
					"!=", true, true, PatcherBuild::getType));

		_collectionPersistenceFinderByP_N_L_A =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_N_L_A",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"patcherProjectVersionId", "accountEntryCode",
						"latestKeyBuild", "name"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_N_L_A",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), String.class.getName()
					},
					new String[] {
						"patcherProjectVersionId", "accountEntryCode",
						"latestKeyBuild", "name"
					},
					0, 10, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_N_L_A",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), String.class.getName()
					},
					new String[] {
						"patcherProjectVersionId", "accountEntryCode",
						"latestKeyBuild", "name"
					},
					0, 10, false, null),
				_SQL_SELECT_PATCHERBUILD_WHERE, _SQL_COUNT_PATCHERBUILD_WHERE,
				PatcherBuildModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"patcherBuild.", "patcherProjectVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherBuild::getPatcherProjectVersionId),
				new FinderColumn<>(
					"patcherBuild.", "accountEntryCode",
					FinderColumn.Type.STRING, "=", true, true,
					PatcherBuild::getAccountEntryCode),
				new FinderColumn<>(
					"patcherBuild.", "latestKeyBuild",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					PatcherBuild::isLatestKeyBuild),
				new FinderColumn<>(
					"patcherBuild.", "name", FinderColumn.Type.STRING, "=",
					true, true, PatcherBuild::getName));

		PatcherBuildUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherBuildUtil.setPersistence(null);

		entityCache.removeCache(PatcherBuildImpl.class.getName());

		TableMapperFactory.removeTableMapper(
			"OSBPatcher_PAccounts_PBuilds#patcherBuildId");
		TableMapperFactory.removeTableMapper(
			"OSBPatcher_PBuilds_PFixes#patcherBuildId");
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

	protected TableMapper<PatcherBuild, PatcherAccount>
		patcherBuildToPatcherAccountTableMapper;
	protected TableMapper<PatcherBuild, PatcherFix>
		patcherBuildToPatcherFixTableMapper;

	private static Long _getTime(Date date) {
		if (date == null) {
			return null;
		}

		return date.getTime();
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		PatcherBuildModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PATCHERBUILD =
		"SELECT patcherBuild FROM PatcherBuild patcherBuild";

	private static final String _SQL_SELECT_PATCHERBUILD_WHERE =
		"SELECT patcherBuild FROM PatcherBuild patcherBuild WHERE ";

	private static final String _SQL_COUNT_PATCHERBUILD_WHERE =
		"SELECT COUNT(patcherBuild) FROM PatcherBuild patcherBuild WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"key", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-187522727