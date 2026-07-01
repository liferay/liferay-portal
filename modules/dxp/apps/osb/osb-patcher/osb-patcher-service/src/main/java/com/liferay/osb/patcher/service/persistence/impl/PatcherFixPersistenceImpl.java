/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherFixException;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.PatcherFixTable;
import com.liferay.osb.patcher.model.impl.PatcherFixImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherFixPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherFixUtil;
import com.liferay.osb.patcher.service.persistence.impl.constants.OSBPatcherPersistenceConstants;
import com.liferay.petra.string.StringBundler;
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
 * The persistence implementation for the patcher fix service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherFixPersistence.class)
public class PatcherFixPersistenceImpl
	extends BasePersistenceImpl<PatcherFix, NoSuchPatcherFixException>
	implements PatcherFixPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherFixUtil</code> to access the patcher fix persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherFixImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<PatcherFix, NoSuchPatcherFixException>
			_collectionPersistenceFinderByPatcherProjectVersionId;

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPatcherProjectVersionId.find(
			finderCache, new Object[] {patcherProjectVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByPatcherProjectVersionId_First(
			long patcherProjectVersionId,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		return _collectionPersistenceFinderByPatcherProjectVersionId.findFirst(
			finderCache, new Object[] {patcherProjectVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByPatcherProjectVersionId_First(
		long patcherProjectVersionId,
		OrderByComparator<PatcherFix> orderByComparator) {

		return _collectionPersistenceFinderByPatcherProjectVersionId.fetchFirst(
			finderCache, new Object[] {patcherProjectVersionId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return _collectionPersistenceFinderByPatcherProjectVersionId.filterFind(
			finderCache, new Object[] {patcherProjectVersionId}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the patcher fixes where patcherProjectVersionId = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 */
	@Override
	public void removeByPatcherProjectVersionId(long patcherProjectVersionId) {
		_collectionPersistenceFinderByPatcherProjectVersionId.remove(
			finderCache, new Object[] {patcherProjectVersionId});
	}

	/**
	 * Returns the number of patcher fixes where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByPatcherProjectVersionId(long patcherProjectVersionId) {
		return _collectionPersistenceFinderByPatcherProjectVersionId.count(
			finderCache, new Object[] {patcherProjectVersionId});
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return _collectionPersistenceFinderByPatcherProjectVersionId.
			filterCount(finderCache, new Object[] {patcherProjectVersionId});
	}

	private FilterCollectionPersistenceFinder
		<PatcherFix, NoSuchPatcherFixException>
			_collectionPersistenceFinderByP_L_T;

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_L_T.find(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByP_L_T_First(
			long patcherProjectVersionId, boolean latestFix, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		return _collectionPersistenceFinderByP_L_T.findFirst(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type},
			orderByComparator);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByP_L_T_First(
		long patcherProjectVersionId, boolean latestFix, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		return _collectionPersistenceFinderByP_L_T.fetchFirst(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		return _collectionPersistenceFinderByP_L_T.filterFind(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 */
	@Override
	public void removeByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type) {

		_collectionPersistenceFinderByP_L_T.remove(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type});
	}

	/**
	 * Returns the number of patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return _collectionPersistenceFinderByP_L_T.count(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type});
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return _collectionPersistenceFinderByP_L_T.filterCount(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type});
	}

	private FilterCollectionPersistenceFinder
		<PatcherFix, NoSuchPatcherFixException>
			_collectionPersistenceFinderByP_L_NotT;

	/**
	 * Returns all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return findByP_L_NotT(
			patcherProjectVersionId, latestFix, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end) {

		return findByP_L_NotT(
			patcherProjectVersionId, latestFix, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		return findByP_L_NotT(
			patcherProjectVersionId, latestFix, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_L_NotT.find(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByP_L_NotT_First(
			long patcherProjectVersionId, boolean latestFix, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		return _collectionPersistenceFinderByP_L_NotT.findFirst(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type},
			orderByComparator);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByP_L_NotT_First(
		long patcherProjectVersionId, boolean latestFix, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		return _collectionPersistenceFinderByP_L_NotT.fetchFirst(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type},
			orderByComparator);
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return filterFindByP_L_NotT(
			patcherProjectVersionId, latestFix, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end) {

		return filterFindByP_L_NotT(
			patcherProjectVersionId, latestFix, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		return _collectionPersistenceFinderByP_L_NotT.filterFind(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 */
	@Override
	public void removeByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type) {

		_collectionPersistenceFinderByP_L_NotT.remove(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type});
	}

	/**
	 * Returns the number of patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return _collectionPersistenceFinderByP_L_NotT.count(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type});
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return _collectionPersistenceFinderByP_L_NotT.filterCount(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type});
	}

	private FilterCollectionPersistenceFinder
		<PatcherFix, NoSuchPatcherFixException>
			_collectionPersistenceFinderByK_GtKV_NotT;

	/**
	 * Returns all the patcher fixes where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_GtKV_NotT(
		String key, double keyVersion, int type) {

		return findByK_GtKV_NotT(
			key, keyVersion, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_GtKV_NotT(
		String key, double keyVersion, int type, int start, int end) {

		return findByK_GtKV_NotT(key, keyVersion, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_GtKV_NotT(
		String key, double keyVersion, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return findByK_GtKV_NotT(
			key, keyVersion, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_GtKV_NotT(
		String key, double keyVersion, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByK_GtKV_NotT.find(
			finderCache, new Object[] {key, keyVersion, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix in the ordered set where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByK_GtKV_NotT_First(
			String key, double keyVersion, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		return _collectionPersistenceFinderByK_GtKV_NotT.findFirst(
			finderCache, new Object[] {key, keyVersion, type},
			orderByComparator);
	}

	/**
	 * Returns the first patcher fix in the ordered set where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByK_GtKV_NotT_First(
		String key, double keyVersion, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		return _collectionPersistenceFinderByK_GtKV_NotT.fetchFirst(
			finderCache, new Object[] {key, keyVersion, type},
			orderByComparator);
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByK_GtKV_NotT(
		String key, double keyVersion, int type) {

		return filterFindByK_GtKV_NotT(
			key, keyVersion, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByK_GtKV_NotT(
		String key, double keyVersion, int type, int start, int end) {

		return filterFindByK_GtKV_NotT(key, keyVersion, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByK_GtKV_NotT(
		String key, double keyVersion, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return _collectionPersistenceFinderByK_GtKV_NotT.filterFind(
			finderCache, new Object[] {key, keyVersion, type}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the patcher fixes where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 */
	@Override
	public void removeByK_GtKV_NotT(String key, double keyVersion, int type) {
		_collectionPersistenceFinderByK_GtKV_NotT.remove(
			finderCache, new Object[] {key, keyVersion, type});
	}

	/**
	 * Returns the number of patcher fixes where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByK_GtKV_NotT(String key, double keyVersion, int type) {
		return _collectionPersistenceFinderByK_GtKV_NotT.count(
			finderCache, new Object[] {key, keyVersion, type});
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByK_GtKV_NotT(
		String key, double keyVersion, int type) {

		return _collectionPersistenceFinderByK_GtKV_NotT.filterCount(
			finderCache, new Object[] {key, keyVersion, type});
	}

	private FilterCollectionPersistenceFinder
		<PatcherFix, NoSuchPatcherFixException>
			_collectionPersistenceFinderByK_LtKV_NotT;

	/**
	 * Returns all the patcher fixes where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_LtKV_NotT(
		String key, double keyVersion, int type) {

		return findByK_LtKV_NotT(
			key, keyVersion, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_LtKV_NotT(
		String key, double keyVersion, int type, int start, int end) {

		return findByK_LtKV_NotT(key, keyVersion, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_LtKV_NotT(
		String key, double keyVersion, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return findByK_LtKV_NotT(
			key, keyVersion, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_LtKV_NotT(
		String key, double keyVersion, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByK_LtKV_NotT.find(
			finderCache, new Object[] {key, keyVersion, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix in the ordered set where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByK_LtKV_NotT_First(
			String key, double keyVersion, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		return _collectionPersistenceFinderByK_LtKV_NotT.findFirst(
			finderCache, new Object[] {key, keyVersion, type},
			orderByComparator);
	}

	/**
	 * Returns the first patcher fix in the ordered set where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByK_LtKV_NotT_First(
		String key, double keyVersion, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		return _collectionPersistenceFinderByK_LtKV_NotT.fetchFirst(
			finderCache, new Object[] {key, keyVersion, type},
			orderByComparator);
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByK_LtKV_NotT(
		String key, double keyVersion, int type) {

		return filterFindByK_LtKV_NotT(
			key, keyVersion, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByK_LtKV_NotT(
		String key, double keyVersion, int type, int start, int end) {

		return filterFindByK_LtKV_NotT(key, keyVersion, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByK_LtKV_NotT(
		String key, double keyVersion, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return _collectionPersistenceFinderByK_LtKV_NotT.filterFind(
			finderCache, new Object[] {key, keyVersion, type}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the patcher fixes where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 */
	@Override
	public void removeByK_LtKV_NotT(String key, double keyVersion, int type) {
		_collectionPersistenceFinderByK_LtKV_NotT.remove(
			finderCache, new Object[] {key, keyVersion, type});
	}

	/**
	 * Returns the number of patcher fixes where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByK_LtKV_NotT(String key, double keyVersion, int type) {
		return _collectionPersistenceFinderByK_LtKV_NotT.count(
			finderCache, new Object[] {key, keyVersion, type});
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByK_LtKV_NotT(
		String key, double keyVersion, int type) {

		return _collectionPersistenceFinderByK_LtKV_NotT.filterCount(
			finderCache, new Object[] {key, keyVersion, type});
	}

	private FilterCollectionPersistenceFinder
		<PatcherFix, NoSuchPatcherFixException>
			_collectionPersistenceFinderByK_L_NotT;

	/**
	 * Returns all the patcher fixes where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_L_NotT(
		String key, boolean latestFix, int type) {

		return findByK_L_NotT(
			key, latestFix, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_L_NotT(
		String key, boolean latestFix, int type, int start, int end) {

		return findByK_L_NotT(key, latestFix, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_L_NotT(
		String key, boolean latestFix, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return findByK_L_NotT(
			key, latestFix, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_L_NotT(
		String key, boolean latestFix, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByK_L_NotT.find(
			finderCache, new Object[] {key, latestFix, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix in the ordered set where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByK_L_NotT_First(
			String key, boolean latestFix, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		return _collectionPersistenceFinderByK_L_NotT.findFirst(
			finderCache, new Object[] {key, latestFix, type},
			orderByComparator);
	}

	/**
	 * Returns the first patcher fix in the ordered set where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByK_L_NotT_First(
		String key, boolean latestFix, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		return _collectionPersistenceFinderByK_L_NotT.fetchFirst(
			finderCache, new Object[] {key, latestFix, type},
			orderByComparator);
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByK_L_NotT(
		String key, boolean latestFix, int type) {

		return filterFindByK_L_NotT(
			key, latestFix, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByK_L_NotT(
		String key, boolean latestFix, int type, int start, int end) {

		return filterFindByK_L_NotT(key, latestFix, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByK_L_NotT(
		String key, boolean latestFix, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return _collectionPersistenceFinderByK_L_NotT.filterFind(
			finderCache, new Object[] {key, latestFix, type}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the patcher fixes where key = &#63; and latestFix = &#63; and type &ne; &#63; from the database.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 */
	@Override
	public void removeByK_L_NotT(String key, boolean latestFix, int type) {
		_collectionPersistenceFinderByK_L_NotT.remove(
			finderCache, new Object[] {key, latestFix, type});
	}

	/**
	 * Returns the number of patcher fixes where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByK_L_NotT(String key, boolean latestFix, int type) {
		return _collectionPersistenceFinderByK_L_NotT.count(
			finderCache, new Object[] {key, latestFix, type});
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByK_L_NotT(String key, boolean latestFix, int type) {
		return _collectionPersistenceFinderByK_L_NotT.filterCount(
			finderCache, new Object[] {key, latestFix, type});
	}

	private FilterCollectionPersistenceFinder
		<PatcherFix, NoSuchPatcherFixException>
			_collectionPersistenceFinderByLtM_N_T_S;

	/**
	 * Returns all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @return the matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status) {

		return findByLtM_N_T_S(
			modifiedDate, notified, type, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status, int start,
		int end) {

		return findByLtM_N_T_S(
			modifiedDate, notified, type, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		return findByLtM_N_T_S(
			modifiedDate, notified, type, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtM_N_T_S.find(
			finderCache,
			new Object[] {modifiedDate, notified, new int[] {type}, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByLtM_N_T_S_First(
			Date modifiedDate, boolean notified, int type, int status,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByLtM_N_T_S_First(
			modifiedDate, notified, type, status, orderByComparator);

		if (patcherFix != null) {
			return patcherFix;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("modifiedDate<");
		sb.append(modifiedDate);

		sb.append(", notified=");
		sb.append(notified);

		sb.append(", type=");
		sb.append(type);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPatcherFixException(sb.toString());
	}

	/**
	 * Returns the first patcher fix in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByLtM_N_T_S_First(
		Date modifiedDate, boolean notified, int type, int status,
		OrderByComparator<PatcherFix> orderByComparator) {

		return _collectionPersistenceFinderByLtM_N_T_S.fetchFirst(
			finderCache,
			new Object[] {modifiedDate, notified, new int[] {type}, status},
			orderByComparator);
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @return the matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status) {

		return filterFindByLtM_N_T_S(
			modifiedDate, notified, type, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status, int start,
		int end) {

		return filterFindByLtM_N_T_S(
			modifiedDate, notified, type, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		return _collectionPersistenceFinderByLtM_N_T_S.filterFind(
			finderCache,
			new Object[] {modifiedDate, notified, new int[] {type}, status},
			start, end, orderByComparator);
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @return the matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status) {

		return filterFindByLtM_N_T_S(
			modifiedDate, notified, types, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status, int start,
		int end) {

		return filterFindByLtM_N_T_S(
			modifiedDate, notified, types, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		return _collectionPersistenceFinderByLtM_N_T_S.filterFind(
			finderCache,
			new Object[] {
				modifiedDate, notified, ArrayUtil.sortedUnique(types), status
			},
			start, end, orderByComparator);
	}

	/**
	 * Returns all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @return the matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status) {

		return findByLtM_N_T_S(
			modifiedDate, notified, types, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status, int start,
		int end) {

		return findByLtM_N_T_S(
			modifiedDate, notified, types, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		return findByLtM_N_T_S(
			modifiedDate, notified, types, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtM_N_T_S.find(
			finderCache,
			new Object[] {
				modifiedDate, notified, ArrayUtil.sortedUnique(types), status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 */
	@Override
	public void removeByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status) {

		_collectionPersistenceFinderByLtM_N_T_S.remove(
			finderCache,
			new Object[] {modifiedDate, notified, new int[] {type}, status});
	}

	/**
	 * Returns the number of patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status) {

		return _collectionPersistenceFinderByLtM_N_T_S.count(
			finderCache,
			new Object[] {modifiedDate, notified, new int[] {type}, status});
	}

	/**
	 * Returns the number of patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status) {

		return _collectionPersistenceFinderByLtM_N_T_S.count(
			finderCache,
			new Object[] {
				modifiedDate, notified, ArrayUtil.sortedUnique(types), status
			});
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status) {

		return _collectionPersistenceFinderByLtM_N_T_S.filterCount(
			finderCache,
			new Object[] {modifiedDate, notified, new int[] {type}, status});
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status) {

		return _collectionPersistenceFinderByLtM_N_T_S.filterCount(
			finderCache,
			new Object[] {
				modifiedDate, notified, ArrayUtil.sortedUnique(types), status
			});
	}

	private FilterCollectionPersistenceFinder
		<PatcherFix, NoSuchPatcherFixException>
			_collectionPersistenceFinderByP_L_N_NotT;

	/**
	 * Returns all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @return the matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name,
		int type) {

		return findByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		int start, int end) {

		return findByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		int start, int end, OrderByComparator<PatcherFix> orderByComparator) {

		return findByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		int start, int end, OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_L_N_NotT.find(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, name, type},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByP_L_N_NotT_First(
			long patcherProjectVersionId, boolean latestFix, String name,
			int type, OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		return _collectionPersistenceFinderByP_L_N_NotT.findFirst(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, name, type},
			orderByComparator);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByP_L_N_NotT_First(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		return _collectionPersistenceFinderByP_L_N_NotT.fetchFirst(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, name, type},
			orderByComparator);
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @return the matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name,
		int type) {

		return filterFindByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		int start, int end) {

		return filterFindByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		int start, int end, OrderByComparator<PatcherFix> orderByComparator) {

		return _collectionPersistenceFinderByP_L_N_NotT.filterFind(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, name, type},
			start, end, orderByComparator);
	}

	/**
	 * Removes all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 */
	@Override
	public void removeByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name,
		int type) {

		_collectionPersistenceFinderByP_L_N_NotT.remove(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, name, type});
	}

	/**
	 * Returns the number of patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name,
		int type) {

		return _collectionPersistenceFinderByP_L_N_NotT.count(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, name, type});
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name,
		int type) {

		return _collectionPersistenceFinderByP_L_N_NotT.filterCount(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, name, type});
	}

	private FilterCollectionPersistenceFinder
		<PatcherFix, NoSuchPatcherFixException>
			_collectionPersistenceFinderByP_L_NotT_S;

	/**
	 * Returns all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @return the matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status) {

		return findByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		int start, int end) {

		return findByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		int start, int end, OrderByComparator<PatcherFix> orderByComparator) {

		return findByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		int start, int end, OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_L_NotT_S.find(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByP_L_NotT_S_First(
			long patcherProjectVersionId, boolean latestFix, int type,
			int status, OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		return _collectionPersistenceFinderByP_L_NotT_S.findFirst(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type, status},
			orderByComparator);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByP_L_NotT_S_First(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		OrderByComparator<PatcherFix> orderByComparator) {

		return _collectionPersistenceFinderByP_L_NotT_S.fetchFirst(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type, status},
			orderByComparator);
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @return the matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status) {

		return filterFindByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		int start, int end) {

		return filterFindByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		int start, int end, OrderByComparator<PatcherFix> orderByComparator) {

		return _collectionPersistenceFinderByP_L_NotT_S.filterFind(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type, status},
			start, end, orderByComparator);
	}

	/**
	 * Removes all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 */
	@Override
	public void removeByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status) {

		_collectionPersistenceFinderByP_L_NotT_S.remove(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type, status});
	}

	/**
	 * Returns the number of patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status) {

		return _collectionPersistenceFinderByP_L_NotT_S.count(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type, status});
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status) {

		return _collectionPersistenceFinderByP_L_NotT_S.filterCount(
			finderCache,
			new Object[] {patcherProjectVersionId, latestFix, type, status});
	}

	public PatcherFixPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("key", "key_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(PatcherFix.class);

		setModelImplClass(PatcherFixImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherFixTable.INSTANCE);
	}

	/**
	 * Creates a new patcher fix with the primary key. Does not add the patcher fix to the database.
	 *
	 * @param patcherFixId the primary key for the new patcher fix
	 * @return the new patcher fix
	 */
	@Override
	public PatcherFix create(long patcherFixId) {
		PatcherFix patcherFix = new PatcherFixImpl();

		patcherFix.setNew(true);
		patcherFix.setPrimaryKey(patcherFixId);

		patcherFix.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherFix;
	}

	/**
	 * Removes the patcher fix with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix that was removed
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix remove(long patcherFixId)
		throws NoSuchPatcherFixException {

		return remove((Serializable)patcherFixId);
	}

	@Override
	protected PatcherFix removeImpl(PatcherFix patcherFix) {
		patcherFixToPatcherBuildTableMapper.deleteLeftPrimaryKeyTableMappings(
			patcherFix.getPrimaryKey());

		patcherFixToPatcherFixPackTableMapper.deleteLeftPrimaryKeyTableMappings(
			patcherFix.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherFix)) {
				patcherFix = (PatcherFix)session.get(
					PatcherFixImpl.class, patcherFix.getPrimaryKeyObj());
			}

			if (patcherFix != null) {
				session.delete(patcherFix);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherFix != null) {
			clearCache(patcherFix);
		}

		return patcherFix;
	}

	@Override
	public PatcherFix updateImpl(PatcherFix patcherFix) {
		boolean isNew = patcherFix.isNew();

		if (!(patcherFix instanceof PatcherFixModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherFix.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(patcherFix);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherFix proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherFix implementation " +
					patcherFix.getClass());
		}

		PatcherFixModelImpl patcherFixModelImpl =
			(PatcherFixModelImpl)patcherFix;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherFix.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherFix.setCreateDate(date);
			}
			else {
				patcherFix.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!patcherFixModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherFix.setModifiedDate(date);
			}
			else {
				patcherFix.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherFix);
			}
			else {
				patcherFix = (PatcherFix)session.merge(patcherFix);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(patcherFix, false);

		if (isNew) {
			patcherFix.setNew(false);
		}

		patcherFix.resetOriginalValues();

		return patcherFix;
	}

	/**
	 * Returns the patcher fix with the primary key or throws a <code>NoSuchPatcherFixException</code> if it could not be found.
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix findByPrimaryKey(long patcherFixId)
		throws NoSuchPatcherFixException {

		return findByPrimaryKey((Serializable)patcherFixId);
	}

	/**
	 * Returns the patcher fix with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix, or <code>null</code> if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix fetchByPrimaryKey(long patcherFixId) {
		return fetchByPrimaryKey((Serializable)patcherFixId);
	}

	/**
	 * Returns the primaryKeys of patcher builds associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return long[] of the primaryKeys of patcher builds associated with the patcher fix
	 */
	@Override
	public long[] getPatcherBuildPrimaryKeys(long pk) {
		long[] pks = patcherFixToPatcherBuildTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the patcher fix associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the patcher fixes associated with the patcher build
	 */
	@Override
	public List<PatcherFix> getPatcherBuildPatcherFixes(long pk) {
		return getPatcherBuildPatcherFixes(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the patcher fix associated with the patcher build.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of patcher fixes associated with the patcher build
	 */
	@Override
	public List<PatcherFix> getPatcherBuildPatcherFixes(
		long pk, int start, int end) {

		return getPatcherBuildPatcherFixes(pk, start, end, null);
	}

	/**
	 * Returns all the patcher fix associated with the patcher build.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fixes associated with the patcher build
	 */
	@Override
	public List<PatcherFix> getPatcherBuildPatcherFixes(
		long pk, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return patcherFixToPatcherBuildTableMapper.getLeftBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher builds associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the number of patcher builds associated with the patcher fix
	 */
	@Override
	public int getPatcherBuildsSize(long pk) {
		long[] pks = patcherFixToPatcherBuildTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher build is associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPK the primary key of the patcher build
	 * @return <code>true</code> if the patcher build is associated with the patcher fix; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherBuild(long pk, long patcherBuildPK) {
		return patcherFixToPatcherBuildTableMapper.containsTableMapping(
			pk, patcherBuildPK);
	}

	/**
	 * Returns <code>true</code> if the patcher fix has any patcher builds associated with it.
	 *
	 * @param pk the primary key of the patcher fix to check for associations with patcher builds
	 * @return <code>true</code> if the patcher fix has any patcher builds associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherBuilds(long pk) {
		if (getPatcherBuildsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPK the primary key of the patcher build
	 * @return <code>true</code> if an association between the patcher fix and the patcher build was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherBuild(long pk, long patcherBuildPK) {
		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			return patcherFixToPatcherBuildTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, patcherBuildPK);
		}
		else {
			return patcherFixToPatcherBuildTableMapper.addTableMapping(
				patcherFix.getCompanyId(), pk, patcherBuildPK);
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuild the patcher build
	 * @return <code>true</code> if an association between the patcher fix and the patcher build was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherBuild(long pk, PatcherBuild patcherBuild) {
		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			return patcherFixToPatcherBuildTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				patcherBuild.getPrimaryKey());
		}
		else {
			return patcherFixToPatcherBuildTableMapper.addTableMapping(
				patcherFix.getCompanyId(), pk, patcherBuild.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPKs the primary keys of the patcher builds
	 * @return <code>true</code> if at least one association between the patcher fix and the patcher builds was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherBuilds(long pk, long[] patcherBuildPKs) {
		long companyId = 0;

		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherFix.getCompanyId();
		}

		long[] addedKeys = patcherFixToPatcherBuildTableMapper.addTableMappings(
			companyId, pk, patcherBuildPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuilds the patcher builds
	 * @return <code>true</code> if at least one association between the patcher fix and the patcher builds was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherBuilds(long pk, List<PatcherBuild> patcherBuilds) {
		return addPatcherBuilds(
			pk,
			ListUtil.toLongArray(
				patcherBuilds, PatcherBuild.PATCHER_BUILD_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the patcher fix and its patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix to clear the associated patcher builds from
	 */
	@Override
	public void clearPatcherBuilds(long pk) {
		patcherFixToPatcherBuildTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPK the primary key of the patcher build
	 */
	@Override
	public void removePatcherBuild(long pk, long patcherBuildPK) {
		patcherFixToPatcherBuildTableMapper.deleteTableMapping(
			pk, patcherBuildPK);
	}

	/**
	 * Removes the association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuild the patcher build
	 */
	@Override
	public void removePatcherBuild(long pk, PatcherBuild patcherBuild) {
		patcherFixToPatcherBuildTableMapper.deleteTableMapping(
			pk, patcherBuild.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPKs the primary keys of the patcher builds
	 */
	@Override
	public void removePatcherBuilds(long pk, long[] patcherBuildPKs) {
		patcherFixToPatcherBuildTableMapper.deleteTableMappings(
			pk, patcherBuildPKs);
	}

	/**
	 * Removes the association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuilds the patcher builds
	 */
	@Override
	public void removePatcherBuilds(long pk, List<PatcherBuild> patcherBuilds) {
		removePatcherBuilds(
			pk,
			ListUtil.toLongArray(
				patcherBuilds, PatcherBuild.PATCHER_BUILD_ID_ACCESSOR));
	}

	/**
	 * Sets the patcher builds associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPKs the primary keys of the patcher builds to be associated with the patcher fix
	 */
	@Override
	public void setPatcherBuilds(long pk, long[] patcherBuildPKs) {
		Set<Long> newPatcherBuildPKsSet = SetUtil.fromArray(patcherBuildPKs);
		Set<Long> oldPatcherBuildPKsSet = SetUtil.fromArray(
			patcherFixToPatcherBuildTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removePatcherBuildPKsSet = new HashSet<Long>(
			oldPatcherBuildPKsSet);

		removePatcherBuildPKsSet.removeAll(newPatcherBuildPKsSet);

		patcherFixToPatcherBuildTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removePatcherBuildPKsSet));

		newPatcherBuildPKsSet.removeAll(oldPatcherBuildPKsSet);

		long companyId = 0;

		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherFix.getCompanyId();
		}

		patcherFixToPatcherBuildTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newPatcherBuildPKsSet));
	}

	/**
	 * Sets the patcher builds associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuilds the patcher builds to be associated with the patcher fix
	 */
	@Override
	public void setPatcherBuilds(long pk, List<PatcherBuild> patcherBuilds) {
		try {
			long[] patcherBuildPKs = new long[patcherBuilds.size()];

			for (int i = 0; i < patcherBuilds.size(); i++) {
				PatcherBuild patcherBuild = patcherBuilds.get(i);

				patcherBuildPKs[i] = patcherBuild.getPrimaryKey();
			}

			setPatcherBuilds(pk, patcherBuildPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	/**
	 * Returns the primaryKeys of patcher fix packs associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return long[] of the primaryKeys of patcher fix packs associated with the patcher fix
	 */
	@Override
	public long[] getPatcherFixPackPrimaryKeys(long pk) {
		long[] pks = patcherFixToPatcherFixPackTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the patcher fix associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @return the patcher fixes associated with the patcher fix pack
	 */
	@Override
	public List<PatcherFix> getPatcherFixPackPatcherFixes(long pk) {
		return getPatcherFixPackPatcherFixes(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the patcher fix associated with the patcher fix pack.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of patcher fixes associated with the patcher fix pack
	 */
	@Override
	public List<PatcherFix> getPatcherFixPackPatcherFixes(
		long pk, int start, int end) {

		return getPatcherFixPackPatcherFixes(pk, start, end, null);
	}

	/**
	 * Returns all the patcher fix associated with the patcher fix pack.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fixes associated with the patcher fix pack
	 */
	@Override
	public List<PatcherFix> getPatcherFixPackPatcherFixes(
		long pk, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return patcherFixToPatcherFixPackTableMapper.getLeftBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher fix packs associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the number of patcher fix packs associated with the patcher fix
	 */
	@Override
	public int getPatcherFixPacksSize(long pk) {
		long[] pks = patcherFixToPatcherFixPackTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher fix pack is associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPK the primary key of the patcher fix pack
	 * @return <code>true</code> if the patcher fix pack is associated with the patcher fix; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherFixPack(long pk, long patcherFixPackPK) {
		return patcherFixToPatcherFixPackTableMapper.containsTableMapping(
			pk, patcherFixPackPK);
	}

	/**
	 * Returns <code>true</code> if the patcher fix has any patcher fix packs associated with it.
	 *
	 * @param pk the primary key of the patcher fix to check for associations with patcher fix packs
	 * @return <code>true</code> if the patcher fix has any patcher fix packs associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherFixPacks(long pk) {
		if (getPatcherFixPacksSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPK the primary key of the patcher fix pack
	 * @return <code>true</code> if an association between the patcher fix and the patcher fix pack was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherFixPack(long pk, long patcherFixPackPK) {
		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			return patcherFixToPatcherFixPackTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, patcherFixPackPK);
		}
		else {
			return patcherFixToPatcherFixPackTableMapper.addTableMapping(
				patcherFix.getCompanyId(), pk, patcherFixPackPK);
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPack the patcher fix pack
	 * @return <code>true</code> if an association between the patcher fix and the patcher fix pack was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherFixPack(long pk, PatcherFixPack patcherFixPack) {
		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			return patcherFixToPatcherFixPackTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				patcherFixPack.getPrimaryKey());
		}
		else {
			return patcherFixToPatcherFixPackTableMapper.addTableMapping(
				patcherFix.getCompanyId(), pk, patcherFixPack.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPKs the primary keys of the patcher fix packs
	 * @return <code>true</code> if at least one association between the patcher fix and the patcher fix packs was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherFixPacks(long pk, long[] patcherFixPackPKs) {
		long companyId = 0;

		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherFix.getCompanyId();
		}

		long[] addedKeys =
			patcherFixToPatcherFixPackTableMapper.addTableMappings(
				companyId, pk, patcherFixPackPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPacks the patcher fix packs
	 * @return <code>true</code> if at least one association between the patcher fix and the patcher fix packs was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherFixPacks(
		long pk, List<PatcherFixPack> patcherFixPacks) {

		return addPatcherFixPacks(
			pk,
			ListUtil.toLongArray(
				patcherFixPacks, PatcherFixPack.PATCHER_FIX_PACK_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the patcher fix and its patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix to clear the associated patcher fix packs from
	 */
	@Override
	public void clearPatcherFixPacks(long pk) {
		patcherFixToPatcherFixPackTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPK the primary key of the patcher fix pack
	 */
	@Override
	public void removePatcherFixPack(long pk, long patcherFixPackPK) {
		patcherFixToPatcherFixPackTableMapper.deleteTableMapping(
			pk, patcherFixPackPK);
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPack the patcher fix pack
	 */
	@Override
	public void removePatcherFixPack(long pk, PatcherFixPack patcherFixPack) {
		patcherFixToPatcherFixPackTableMapper.deleteTableMapping(
			pk, patcherFixPack.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPKs the primary keys of the patcher fix packs
	 */
	@Override
	public void removePatcherFixPacks(long pk, long[] patcherFixPackPKs) {
		patcherFixToPatcherFixPackTableMapper.deleteTableMappings(
			pk, patcherFixPackPKs);
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPacks the patcher fix packs
	 */
	@Override
	public void removePatcherFixPacks(
		long pk, List<PatcherFixPack> patcherFixPacks) {

		removePatcherFixPacks(
			pk,
			ListUtil.toLongArray(
				patcherFixPacks, PatcherFixPack.PATCHER_FIX_PACK_ID_ACCESSOR));
	}

	/**
	 * Sets the patcher fix packs associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPKs the primary keys of the patcher fix packs to be associated with the patcher fix
	 */
	@Override
	public void setPatcherFixPacks(long pk, long[] patcherFixPackPKs) {
		Set<Long> newPatcherFixPackPKsSet = SetUtil.fromArray(
			patcherFixPackPKs);
		Set<Long> oldPatcherFixPackPKsSet = SetUtil.fromArray(
			patcherFixToPatcherFixPackTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removePatcherFixPackPKsSet = new HashSet<Long>(
			oldPatcherFixPackPKsSet);

		removePatcherFixPackPKsSet.removeAll(newPatcherFixPackPKsSet);

		patcherFixToPatcherFixPackTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removePatcherFixPackPKsSet));

		newPatcherFixPackPKsSet.removeAll(oldPatcherFixPackPKsSet);

		long companyId = 0;

		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherFix.getCompanyId();
		}

		patcherFixToPatcherFixPackTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newPatcherFixPackPKsSet));
	}

	/**
	 * Sets the patcher fix packs associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPacks the patcher fix packs to be associated with the patcher fix
	 */
	@Override
	public void setPatcherFixPacks(
		long pk, List<PatcherFixPack> patcherFixPacks) {

		try {
			long[] patcherFixPackPKs = new long[patcherFixPacks.size()];

			for (int i = 0; i < patcherFixPacks.size(); i++) {
				PatcherFixPack patcherFixPack = patcherFixPacks.get(i);

				patcherFixPackPKs[i] = patcherFixPack.getPrimaryKey();
			}

			setPatcherFixPacks(pk, patcherFixPackPKs);
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
		return "patcherFixId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERFIX;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherFixModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher fix persistence.
	 */
	@Activate
	public void activate() {
		patcherFixToPatcherBuildTableMapper = TableMapperFactory.getTableMapper(
			"OSBPatcher_PBuilds_PFixes#patcherFixId",
			"OSBPatcher_PBuilds_PFixes", "companyId", "patcherFixId",
			"patcherBuildId", this, PatcherBuild.class);

		patcherFixToPatcherFixPackTableMapper =
			TableMapperFactory.getTableMapper(
				"OSBPatcher_PFixes_PFixPacks#patcherFixId",
				"OSBPatcher_PFixes_PFixPacks", "companyId", "patcherFixId",
				"patcherFixPackId", this, PatcherFixPack.class);

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
				_SQL_SELECT_PATCHERFIX_WHERE, _SQL_COUNT_PATCHERFIX_WHERE,
				PatcherFixModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"patcherFix.", "patcherProjectVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherFix::getPatcherProjectVersionId));

		_collectionPersistenceFinderByP_L_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_L_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"patcherProjectVersionId", "latestFix", "type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_L_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"patcherProjectVersionId", "latestFix", "type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_L_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"patcherProjectVersionId", "latestFix", "type_"
					},
					false),
				_SQL_SELECT_PATCHERFIX_WHERE, _SQL_COUNT_PATCHERFIX_WHERE,
				PatcherFixModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"patcherFix.", "patcherProjectVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherFix::getPatcherProjectVersionId),
				new FinderColumn<>(
					"patcherFix.", "latestFix", FinderColumn.Type.BOOLEAN, "=",
					true, true, PatcherFix::isLatestFix),
				new FinderColumn<>(
					"patcherFix.", "type", "type_", FinderColumn.Type.INTEGER,
					"=", true, true, PatcherFix::getType));

		_collectionPersistenceFinderByP_L_NotT =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_L_NotT",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"patcherProjectVersionId", "latestFix", "type_"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByP_L_NotT",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"patcherProjectVersionId", "latestFix", "type_"
					},
					false),
				_SQL_SELECT_PATCHERFIX_WHERE, _SQL_COUNT_PATCHERFIX_WHERE,
				PatcherFixModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"patcherFix.", "patcherProjectVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherFix::getPatcherProjectVersionId),
				new FinderColumn<>(
					"patcherFix.", "latestFix", FinderColumn.Type.BOOLEAN, "=",
					true, true, PatcherFix::isLatestFix),
				new FinderColumn<>(
					"patcherFix.", "type", "type_", FinderColumn.Type.INTEGER,
					"!=", true, true, PatcherFix::getType));

		_collectionPersistenceFinderByK_GtKV_NotT =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByK_GtKV_NotT",
					new String[] {
						String.class.getName(), Double.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"key_", "keyVersion", "type_"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByK_GtKV_NotT",
					new String[] {
						String.class.getName(), Double.class.getName(),
						Integer.class.getName()
					},
					new String[] {"key_", "keyVersion", "type_"}, false),
				_SQL_SELECT_PATCHERFIX_WHERE, _SQL_COUNT_PATCHERFIX_WHERE,
				PatcherFixModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"patcherFix.", "key", "key_", FinderColumn.Type.STRING, "=",
					true, true, PatcherFix::getKey),
				new FinderColumn<>(
					"patcherFix.", "keyVersion", FinderColumn.Type.DOUBLE, ">",
					true, true, PatcherFix::getKeyVersion),
				new FinderColumn<>(
					"patcherFix.", "type", "type_", FinderColumn.Type.INTEGER,
					"!=", true, true, PatcherFix::getType));

		_collectionPersistenceFinderByK_LtKV_NotT =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByK_LtKV_NotT",
					new String[] {
						String.class.getName(), Double.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"key_", "keyVersion", "type_"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByK_LtKV_NotT",
					new String[] {
						String.class.getName(), Double.class.getName(),
						Integer.class.getName()
					},
					new String[] {"key_", "keyVersion", "type_"}, false),
				_SQL_SELECT_PATCHERFIX_WHERE, _SQL_COUNT_PATCHERFIX_WHERE,
				PatcherFixModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"patcherFix.", "key", "key_", FinderColumn.Type.STRING, "=",
					true, true, PatcherFix::getKey),
				new FinderColumn<>(
					"patcherFix.", "keyVersion", FinderColumn.Type.DOUBLE, "<",
					true, true, PatcherFix::getKeyVersion),
				new FinderColumn<>(
					"patcherFix.", "type", "type_", FinderColumn.Type.INTEGER,
					"!=", true, true, PatcherFix::getType));

		_collectionPersistenceFinderByK_L_NotT =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByK_L_NotT",
					new String[] {
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"key_", "latestFix", "type_"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByK_L_NotT",
					new String[] {
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {"key_", "latestFix", "type_"}, false),
				_SQL_SELECT_PATCHERFIX_WHERE, _SQL_COUNT_PATCHERFIX_WHERE,
				PatcherFixModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"patcherFix.", "key", "key_", FinderColumn.Type.STRING, "=",
					true, true, PatcherFix::getKey),
				new FinderColumn<>(
					"patcherFix.", "latestFix", FinderColumn.Type.BOOLEAN, "=",
					true, true, PatcherFix::isLatestFix),
				new FinderColumn<>(
					"patcherFix.", "type", "type_", FinderColumn.Type.INTEGER,
					"!=", true, true, PatcherFix::getType));

		_collectionPersistenceFinderByLtM_N_T_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtM_N_T_S",
					new String[] {
						Date.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"modifiedDate", "notified", "type_", "status"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtM_N_T_S",
					new String[] {
						Date.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName()
					},
					new String[] {
						"modifiedDate", "notified", "type_", "status"
					},
					false),
				_SQL_SELECT_PATCHERFIX_WHERE, _SQL_COUNT_PATCHERFIX_WHERE,
				PatcherFixModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"patcherFix.", "modifiedDate", FinderColumn.Type.DATE, "<",
					true, true, PatcherFix::getModifiedDate),
				new FinderColumn<>(
					"patcherFix.", "notified", FinderColumn.Type.BOOLEAN, "=",
					true, true, PatcherFix::isNotified),
				new ArrayableFinderColumn<>(
					"patcherFix.", "type", "type_", FinderColumn.Type.INTEGER,
					"=", false, true, true, PatcherFix::getType),
				new FinderColumn<>(
					"patcherFix.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, PatcherFix::getStatus));

		_collectionPersistenceFinderByP_L_N_NotT =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_L_N_NotT",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"patcherProjectVersionId", "latestFix", "name", "type_"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByP_L_N_NotT",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {
						"patcherProjectVersionId", "latestFix", "name", "type_"
					},
					false),
				_SQL_SELECT_PATCHERFIX_WHERE, _SQL_COUNT_PATCHERFIX_WHERE,
				PatcherFixModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"patcherFix.", "patcherProjectVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherFix::getPatcherProjectVersionId),
				new FinderColumn<>(
					"patcherFix.", "latestFix", FinderColumn.Type.BOOLEAN, "=",
					true, true, PatcherFix::isLatestFix),
				new FinderColumn<>(
					"patcherFix.", "name", FinderColumn.Type.STRING, "=", true,
					true, PatcherFix::getName),
				new FinderColumn<>(
					"patcherFix.", "type", "type_", FinderColumn.Type.INTEGER,
					"!=", true, true, PatcherFix::getType));

		_collectionPersistenceFinderByP_L_NotT_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_L_NotT_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"patcherProjectVersionId", "latestFix", "type_",
						"status"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByP_L_NotT_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName()
					},
					new String[] {
						"patcherProjectVersionId", "latestFix", "type_",
						"status"
					},
					false),
				_SQL_SELECT_PATCHERFIX_WHERE, _SQL_COUNT_PATCHERFIX_WHERE,
				PatcherFixModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"patcherFix.", "patcherProjectVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherFix::getPatcherProjectVersionId),
				new FinderColumn<>(
					"patcherFix.", "latestFix", FinderColumn.Type.BOOLEAN, "=",
					true, true, PatcherFix::isLatestFix),
				new FinderColumn<>(
					"patcherFix.", "type", "type_", FinderColumn.Type.INTEGER,
					"!=", true, true, PatcherFix::getType),
				new FinderColumn<>(
					"patcherFix.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, PatcherFix::getStatus));

		PatcherFixUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherFixUtil.setPersistence(null);

		entityCache.removeCache(PatcherFixImpl.class.getName());

		TableMapperFactory.removeTableMapper(
			"OSBPatcher_PBuilds_PFixes#patcherFixId");
		TableMapperFactory.removeTableMapper(
			"OSBPatcher_PFixes_PFixPacks#patcherFixId");
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

	protected TableMapper<PatcherFix, PatcherBuild>
		patcherFixToPatcherBuildTableMapper;
	protected TableMapper<PatcherFix, PatcherFixPack>
		patcherFixToPatcherFixPackTableMapper;

	private static Long _getTime(Date date) {
		if (date == null) {
			return null;
		}

		return date.getTime();
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		PatcherFixModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PATCHERFIX =
		"SELECT patcherFix FROM PatcherFix patcherFix";

	private static final String _SQL_SELECT_PATCHERFIX_WHERE =
		"SELECT patcherFix FROM PatcherFix patcherFix WHERE ";

	private static final String _SQL_COUNT_PATCHERFIX_WHERE =
		"SELECT COUNT(patcherFix) FROM PatcherFix patcherFix WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PatcherFix exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"key", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:570631126