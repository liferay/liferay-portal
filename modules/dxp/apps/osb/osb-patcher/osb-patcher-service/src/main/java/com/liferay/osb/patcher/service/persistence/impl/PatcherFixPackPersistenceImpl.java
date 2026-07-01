/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.PatcherFixPackTable;
import com.liferay.osb.patcher.model.impl.PatcherFixPackImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherFixPackPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherFixPackUtil;
import com.liferay.osb.patcher.service.persistence.impl.constants.OSBPatcherPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
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
 * The persistence implementation for the patcher fix pack service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherFixPackPersistence.class)
public class PatcherFixPackPersistenceImpl
	extends BasePersistenceImpl<PatcherFixPack, NoSuchPatcherFixPackException>
	implements PatcherFixPackPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherFixPackUtil</code> to access the patcher fix pack persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherFixPackImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<PatcherFixPack, NoSuchPatcherFixPackException>
			_uniquePersistenceFinderByPatcherBuildId;

	/**
	 * Returns the patcher fix pack where patcherBuildId = &#63; or throws a <code>NoSuchPatcherFixPackException</code> if it could not be found.
	 *
	 * @param patcherBuildId the patcher build ID
	 * @return the matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPatcherBuildId(long patcherBuildId)
		throws NoSuchPatcherFixPackException {

		return _uniquePersistenceFinderByPatcherBuildId.find(
			finderCache, new Object[] {patcherBuildId});
	}

	/**
	 * Returns the patcher fix pack where patcherBuildId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param patcherBuildId the patcher build ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPatcherBuildId(
		long patcherBuildId, boolean useFinderCache) {

		return _uniquePersistenceFinderByPatcherBuildId.fetch(
			finderCache, new Object[] {patcherBuildId}, useFinderCache);
	}

	/**
	 * Removes the patcher fix pack where patcherBuildId = &#63; from the database.
	 *
	 * @param patcherBuildId the patcher build ID
	 * @return the patcher fix pack that was removed
	 */
	@Override
	public PatcherFixPack removeByPatcherBuildId(long patcherBuildId)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = findByPatcherBuildId(patcherBuildId);

		return remove(patcherFixPack);
	}

	/**
	 * Returns the number of patcher fix packs where patcherBuildId = &#63;.
	 *
	 * @param patcherBuildId the patcher build ID
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByPatcherBuildId(long patcherBuildId) {
		return _uniquePersistenceFinderByPatcherBuildId.count(
			finderCache, new Object[] {patcherBuildId});
	}

	private FilterCollectionPersistenceFinder
		<PatcherFixPack, NoSuchPatcherFixPackException>
			_collectionPersistenceFinderByPatcherFixComponentId;

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPatcherFixComponentId(
		long patcherFixComponentId, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPatcherFixComponentId.find(
			finderCache, new Object[] {patcherFixComponentId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPatcherFixComponentId_First(
			long patcherFixComponentId,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		return _collectionPersistenceFinderByPatcherFixComponentId.findFirst(
			finderCache, new Object[] {patcherFixComponentId},
			orderByComparator);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPatcherFixComponentId_First(
		long patcherFixComponentId,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return _collectionPersistenceFinderByPatcherFixComponentId.fetchFirst(
			finderCache, new Object[] {patcherFixComponentId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPatcherFixComponentId(
		long patcherFixComponentId, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return _collectionPersistenceFinderByPatcherFixComponentId.filterFind(
			finderCache, new Object[] {patcherFixComponentId}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 */
	@Override
	public void removeByPatcherFixComponentId(long patcherFixComponentId) {
		_collectionPersistenceFinderByPatcherFixComponentId.remove(
			finderCache, new Object[] {patcherFixComponentId});
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByPatcherFixComponentId(long patcherFixComponentId) {
		return _collectionPersistenceFinderByPatcherFixComponentId.count(
			finderCache, new Object[] {patcherFixComponentId});
	}

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public int filterCountByPatcherFixComponentId(long patcherFixComponentId) {
		return _collectionPersistenceFinderByPatcherFixComponentId.filterCount(
			finderCache, new Object[] {patcherFixComponentId});
	}

	private FilterCollectionPersistenceFinder
		<PatcherFixPack, NoSuchPatcherFixPackException>
			_collectionPersistenceFinderByVersion;

	/**
	 * Returns an ordered range of all the patcher fix packs where version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByVersion(
		int version, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByVersion.find(
			finderCache, new Object[] {version}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where version = &#63;.
	 *
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByVersion_First(
			int version, OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		return _collectionPersistenceFinderByVersion.findFirst(
			finderCache, new Object[] {version}, orderByComparator);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where version = &#63;.
	 *
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByVersion_First(
		int version, OrderByComparator<PatcherFixPack> orderByComparator) {

		return _collectionPersistenceFinderByVersion.fetchFirst(
			finderCache, new Object[] {version}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByVersion(
		int version, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return _collectionPersistenceFinderByVersion.filterFind(
			finderCache, new Object[] {version}, start, end, orderByComparator);
	}

	/**
	 * Removes all the patcher fix packs where version = &#63; from the database.
	 *
	 * @param version the version
	 */
	@Override
	public void removeByVersion(int version) {
		_collectionPersistenceFinderByVersion.remove(
			finderCache, new Object[] {version});
	}

	/**
	 * Returns the number of patcher fix packs where version = &#63;.
	 *
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByVersion(int version) {
		return _collectionPersistenceFinderByVersion.count(
			finderCache, new Object[] {version});
	}

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where version = &#63;.
	 *
	 * @param version the version
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public int filterCountByVersion(int version) {
		return _collectionPersistenceFinderByVersion.filterCount(
			finderCache, new Object[] {version});
	}

	private FilterCollectionPersistenceFinder
		<PatcherFixPack, NoSuchPatcherFixPackException>
			_collectionPersistenceFinderByPFCI_PPVI;

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId, int start,
		int end, OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPFCI_PPVI.find(
			finderCache,
			new Object[] {patcherFixComponentId, patcherProjectVersionId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_PPVI_First(
			long patcherFixComponentId, long patcherProjectVersionId,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		return _collectionPersistenceFinderByPFCI_PPVI.findFirst(
			finderCache,
			new Object[] {patcherFixComponentId, patcherProjectVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_PPVI_First(
		long patcherFixComponentId, long patcherProjectVersionId,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return _collectionPersistenceFinderByPFCI_PPVI.fetchFirst(
			finderCache,
			new Object[] {patcherFixComponentId, patcherProjectVersionId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId, int start,
		int end, OrderByComparator<PatcherFixPack> orderByComparator) {

		return _collectionPersistenceFinderByPFCI_PPVI.filterFind(
			finderCache,
			new Object[] {patcherFixComponentId, patcherProjectVersionId},
			start, end, orderByComparator);
	}

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 */
	@Override
	public void removeByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId) {

		_collectionPersistenceFinderByPFCI_PPVI.remove(
			finderCache,
			new Object[] {patcherFixComponentId, patcherProjectVersionId});
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId) {

		return _collectionPersistenceFinderByPFCI_PPVI.count(
			finderCache,
			new Object[] {patcherFixComponentId, patcherProjectVersionId});
	}

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public int filterCountByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId) {

		return _collectionPersistenceFinderByPFCI_PPVI.filterCount(
			finderCache,
			new Object[] {patcherFixComponentId, patcherProjectVersionId});
	}

	private FilterCollectionPersistenceFinder
		<PatcherFixPack, NoSuchPatcherFixPackException>
			_collectionPersistenceFinderByPFCI_V;

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_V(
		long patcherFixComponentId, int version, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPFCI_V.find(
			finderCache, new Object[] {patcherFixComponentId, version}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_V_First(
			long patcherFixComponentId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		return _collectionPersistenceFinderByPFCI_V.findFirst(
			finderCache, new Object[] {patcherFixComponentId, version},
			orderByComparator);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_V_First(
		long patcherFixComponentId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return _collectionPersistenceFinderByPFCI_V.fetchFirst(
			finderCache, new Object[] {patcherFixComponentId, version},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_V(
		long patcherFixComponentId, int version, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return _collectionPersistenceFinderByPFCI_V.filterFind(
			finderCache, new Object[] {patcherFixComponentId, version}, start,
			end, orderByComparator);
	}

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; and version = &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 */
	@Override
	public void removeByPFCI_V(long patcherFixComponentId, int version) {
		_collectionPersistenceFinderByPFCI_V.remove(
			finderCache, new Object[] {patcherFixComponentId, version});
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByPFCI_V(long patcherFixComponentId, int version) {
		return _collectionPersistenceFinderByPFCI_V.count(
			finderCache, new Object[] {patcherFixComponentId, version});
	}

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public int filterCountByPFCI_V(long patcherFixComponentId, int version) {
		return _collectionPersistenceFinderByPFCI_V.filterCount(
			finderCache, new Object[] {patcherFixComponentId, version});
	}

	private UniquePersistenceFinder
		<PatcherFixPack, NoSuchPatcherFixPackException>
			_uniquePersistenceFinderByPFCI_N;

	/**
	 * Returns the patcher fix pack where patcherProjectVersionId = &#63; and name = &#63; or throws a <code>NoSuchPatcherFixPackException</code> if it could not be found.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @return the matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_N(
			long patcherProjectVersionId, String name)
		throws NoSuchPatcherFixPackException {

		return _uniquePersistenceFinderByPFCI_N.find(
			finderCache, new Object[] {patcherProjectVersionId, name});
	}

	/**
	 * Returns the patcher fix pack where patcherProjectVersionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_N(
		long patcherProjectVersionId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByPFCI_N.fetch(
			finderCache, new Object[] {patcherProjectVersionId, name},
			useFinderCache);
	}

	/**
	 * Removes the patcher fix pack where patcherProjectVersionId = &#63; and name = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @return the patcher fix pack that was removed
	 */
	@Override
	public PatcherFixPack removeByPFCI_N(
			long patcherProjectVersionId, String name)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = findByPFCI_N(
			patcherProjectVersionId, name);

		return remove(patcherFixPack);
	}

	/**
	 * Returns the number of patcher fix packs where patcherProjectVersionId = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByPFCI_N(long patcherProjectVersionId, String name) {
		return _uniquePersistenceFinderByPFCI_N.count(
			finderCache, new Object[] {patcherProjectVersionId, name});
	}

	private FilterCollectionPersistenceFinder
		<PatcherFixPack, NoSuchPatcherFixPackException>
			_collectionPersistenceFinderByPFCI_S;

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_S(
		long patcherProjectVersionId, int status, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPFCI_S.find(
			finderCache, new Object[] {patcherProjectVersionId, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_S_First(
			long patcherProjectVersionId, int status,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		return _collectionPersistenceFinderByPFCI_S.findFirst(
			finderCache, new Object[] {patcherProjectVersionId, status},
			orderByComparator);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_S_First(
		long patcherProjectVersionId, int status,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return _collectionPersistenceFinderByPFCI_S.fetchFirst(
			finderCache, new Object[] {patcherProjectVersionId, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_S(
		long patcherProjectVersionId, int status, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return _collectionPersistenceFinderByPFCI_S.filterFind(
			finderCache, new Object[] {patcherProjectVersionId, status}, start,
			end, orderByComparator);
	}

	/**
	 * Removes all the patcher fix packs where patcherProjectVersionId = &#63; and status = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 */
	@Override
	public void removeByPFCI_S(long patcherProjectVersionId, int status) {
		_collectionPersistenceFinderByPFCI_S.remove(
			finderCache, new Object[] {patcherProjectVersionId, status});
	}

	/**
	 * Returns the number of patcher fix packs where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByPFCI_S(long patcherProjectVersionId, int status) {
		return _collectionPersistenceFinderByPFCI_S.count(
			finderCache, new Object[] {patcherProjectVersionId, status});
	}

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public int filterCountByPFCI_S(long patcherProjectVersionId, int status) {
		return _collectionPersistenceFinderByPFCI_S.filterCount(
			finderCache, new Object[] {patcherProjectVersionId, status});
	}

	private FilterCollectionPersistenceFinder
		<PatcherFixPack, NoSuchPatcherFixPackException>
			_collectionPersistenceFinderByPFCI_PPVI_GtV;

	/**
	 * Returns all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return findByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end) {

		return findByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return findByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end, OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPFCI_PPVI_GtV.find(
			finderCache,
			new Object[] {
				patcherFixComponentId, patcherProjectVersionId, version
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_PPVI_GtV_First(
			long patcherFixComponentId, long patcherProjectVersionId,
			int version, OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		return _collectionPersistenceFinderByPFCI_PPVI_GtV.findFirst(
			finderCache,
			new Object[] {
				patcherFixComponentId, patcherProjectVersionId, version
			},
			orderByComparator);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_PPVI_GtV_First(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return _collectionPersistenceFinderByPFCI_PPVI_GtV.fetchFirst(
			finderCache,
			new Object[] {
				patcherFixComponentId, patcherProjectVersionId, version
			},
			orderByComparator);
	}

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return filterFindByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end) {

		return filterFindByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return _collectionPersistenceFinderByPFCI_PPVI_GtV.filterFind(
			finderCache,
			new Object[] {
				patcherFixComponentId, patcherProjectVersionId, version
			},
			start, end, orderByComparator);
	}

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 */
	@Override
	public void removeByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		_collectionPersistenceFinderByPFCI_PPVI_GtV.remove(
			finderCache,
			new Object[] {
				patcherFixComponentId, patcherProjectVersionId, version
			});
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return _collectionPersistenceFinderByPFCI_PPVI_GtV.count(
			finderCache,
			new Object[] {
				patcherFixComponentId, patcherProjectVersionId, version
			});
	}

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public int filterCountByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return _collectionPersistenceFinderByPFCI_PPVI_GtV.filterCount(
			finderCache,
			new Object[] {
				patcherFixComponentId, patcherProjectVersionId, version
			});
	}

	private FilterCollectionPersistenceFinder
		<PatcherFixPack, NoSuchPatcherFixPackException>
			_collectionPersistenceFinderByPFCI_PPVI_LtV;

	/**
	 * Returns all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return findByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end) {

		return findByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return findByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end, OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPFCI_PPVI_LtV.find(
			finderCache,
			new Object[] {
				patcherFixComponentId, patcherProjectVersionId, version
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_PPVI_LtV_First(
			long patcherFixComponentId, long patcherProjectVersionId,
			int version, OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		return _collectionPersistenceFinderByPFCI_PPVI_LtV.findFirst(
			finderCache,
			new Object[] {
				patcherFixComponentId, patcherProjectVersionId, version
			},
			orderByComparator);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_PPVI_LtV_First(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return _collectionPersistenceFinderByPFCI_PPVI_LtV.fetchFirst(
			finderCache,
			new Object[] {
				patcherFixComponentId, patcherProjectVersionId, version
			},
			orderByComparator);
	}

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return filterFindByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end) {

		return filterFindByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return _collectionPersistenceFinderByPFCI_PPVI_LtV.filterFind(
			finderCache,
			new Object[] {
				patcherFixComponentId, patcherProjectVersionId, version
			},
			start, end, orderByComparator);
	}

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 */
	@Override
	public void removeByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		_collectionPersistenceFinderByPFCI_PPVI_LtV.remove(
			finderCache,
			new Object[] {
				patcherFixComponentId, patcherProjectVersionId, version
			});
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return _collectionPersistenceFinderByPFCI_PPVI_LtV.count(
			finderCache,
			new Object[] {
				patcherFixComponentId, patcherProjectVersionId, version
			});
	}

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public int filterCountByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return _collectionPersistenceFinderByPFCI_PPVI_LtV.filterCount(
			finderCache,
			new Object[] {
				patcherFixComponentId, patcherProjectVersionId, version
			});
	}

	private UniquePersistenceFinder
		<PatcherFixPack, NoSuchPatcherFixPackException>
			_uniquePersistenceFinderByPFCI_PPVI_N_V;

	/**
	 * Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or throws a <code>NoSuchPatcherFixPackException</code> if it could not be found.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_PPVI_N_V(
			long patcherFixComponentId, long patcherProjectVersionId,
			String name, int version)
		throws NoSuchPatcherFixPackException {

		return _uniquePersistenceFinderByPFCI_PPVI_N_V.find(
			finderCache,
			new Object[] {
				patcherFixComponentId, patcherProjectVersionId, name, version
			});
	}

	/**
	 * Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_PPVI_N_V(
		long patcherFixComponentId, long patcherProjectVersionId, String name,
		int version, boolean useFinderCache) {

		return _uniquePersistenceFinderByPFCI_PPVI_N_V.fetch(
			finderCache,
			new Object[] {
				patcherFixComponentId, patcherProjectVersionId, name, version
			},
			useFinderCache);
	}

	/**
	 * Removes the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the patcher fix pack that was removed
	 */
	@Override
	public PatcherFixPack removeByPFCI_PPVI_N_V(
			long patcherFixComponentId, long patcherProjectVersionId,
			String name, int version)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = findByPFCI_PPVI_N_V(
			patcherFixComponentId, patcherProjectVersionId, name, version);

		return remove(patcherFixPack);
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByPFCI_PPVI_N_V(
		long patcherFixComponentId, long patcherProjectVersionId, String name,
		int version) {

		return _uniquePersistenceFinderByPFCI_PPVI_N_V.count(
			finderCache,
			new Object[] {
				patcherFixComponentId, patcherProjectVersionId, name, version
			});
	}

	public PatcherFixPackPersistenceImpl() {
		setModelClass(PatcherFixPack.class);

		setModelImplClass(PatcherFixPackImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherFixPackTable.INSTANCE);
	}

	/**
	 * Creates a new patcher fix pack with the primary key. Does not add the patcher fix pack to the database.
	 *
	 * @param patcherFixPackId the primary key for the new patcher fix pack
	 * @return the new patcher fix pack
	 */
	@Override
	public PatcherFixPack create(long patcherFixPackId) {
		PatcherFixPack patcherFixPack = new PatcherFixPackImpl();

		patcherFixPack.setNew(true);
		patcherFixPack.setPrimaryKey(patcherFixPackId);

		patcherFixPack.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherFixPack;
	}

	/**
	 * Removes the patcher fix pack with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack that was removed
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack remove(long patcherFixPackId)
		throws NoSuchPatcherFixPackException {

		return remove((Serializable)patcherFixPackId);
	}

	@Override
	protected PatcherFixPack removeImpl(PatcherFixPack patcherFixPack) {
		patcherFixPackToPatcherFixTableMapper.deleteLeftPrimaryKeyTableMappings(
			patcherFixPack.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherFixPack)) {
				patcherFixPack = (PatcherFixPack)session.get(
					PatcherFixPackImpl.class,
					patcherFixPack.getPrimaryKeyObj());
			}

			if (patcherFixPack != null) {
				session.delete(patcherFixPack);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherFixPack != null) {
			clearCache(patcherFixPack);
		}

		return patcherFixPack;
	}

	@Override
	public PatcherFixPack updateImpl(PatcherFixPack patcherFixPack) {
		boolean isNew = patcherFixPack.isNew();

		if (!(patcherFixPack instanceof PatcherFixPackModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherFixPack.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherFixPack);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherFixPack proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherFixPack implementation " +
					patcherFixPack.getClass());
		}

		PatcherFixPackModelImpl patcherFixPackModelImpl =
			(PatcherFixPackModelImpl)patcherFixPack;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherFixPack.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherFixPack.setCreateDate(date);
			}
			else {
				patcherFixPack.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!patcherFixPackModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherFixPack.setModifiedDate(date);
			}
			else {
				patcherFixPack.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherFixPack);
			}
			else {
				patcherFixPack = (PatcherFixPack)session.merge(patcherFixPack);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(patcherFixPack, false);

		if (isNew) {
			patcherFixPack.setNew(false);
		}

		patcherFixPack.resetOriginalValues();

		return patcherFixPack;
	}

	/**
	 * Returns the patcher fix pack with the primary key or throws a <code>NoSuchPatcherFixPackException</code> if it could not be found.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack findByPrimaryKey(long patcherFixPackId)
		throws NoSuchPatcherFixPackException {

		return findByPrimaryKey((Serializable)patcherFixPackId);
	}

	/**
	 * Returns the patcher fix pack with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack, or <code>null</code> if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack fetchByPrimaryKey(long patcherFixPackId) {
		return fetchByPrimaryKey((Serializable)patcherFixPackId);
	}

	/**
	 * Returns the primaryKeys of patcher fixes associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @return long[] of the primaryKeys of patcher fixes associated with the patcher fix pack
	 */
	@Override
	public long[] getPatcherFixPrimaryKeys(long pk) {
		long[] pks = patcherFixPackToPatcherFixTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the patcher fix pack associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the patcher fix packs associated with the patcher fix
	 */
	@Override
	public List<PatcherFixPack> getPatcherFixPatcherFixPacks(long pk) {
		return getPatcherFixPatcherFixPacks(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the patcher fix pack associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of patcher fix packs associated with the patcher fix
	 */
	@Override
	public List<PatcherFixPack> getPatcherFixPatcherFixPacks(
		long pk, int start, int end) {

		return getPatcherFixPatcherFixPacks(pk, start, end, null);
	}

	/**
	 * Returns all the patcher fix pack associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix packs associated with the patcher fix
	 */
	@Override
	public List<PatcherFixPack> getPatcherFixPatcherFixPacks(
		long pk, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return patcherFixPackToPatcherFixTableMapper.getLeftBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher fixes associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @return the number of patcher fixes associated with the patcher fix pack
	 */
	@Override
	public int getPatcherFixesSize(long pk) {
		long[] pks = patcherFixPackToPatcherFixTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher fix is associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if the patcher fix is associated with the patcher fix pack; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherFix(long pk, long patcherFixPK) {
		return patcherFixPackToPatcherFixTableMapper.containsTableMapping(
			pk, patcherFixPK);
	}

	/**
	 * Returns <code>true</code> if the patcher fix pack has any patcher fixes associated with it.
	 *
	 * @param pk the primary key of the patcher fix pack to check for associations with patcher fixes
	 * @return <code>true</code> if the patcher fix pack has any patcher fixes associated with it; <code>false</code> otherwise
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
	 * Adds an association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if an association between the patcher fix pack and the patcher fix was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherFix(long pk, long patcherFixPK) {
		PatcherFixPack patcherFixPack = fetchByPrimaryKey(pk);

		if (patcherFixPack == null) {
			return patcherFixPackToPatcherFixTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, patcherFixPK);
		}
		else {
			return patcherFixPackToPatcherFixTableMapper.addTableMapping(
				patcherFixPack.getCompanyId(), pk, patcherFixPK);
		}
	}

	/**
	 * Adds an association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFix the patcher fix
	 * @return <code>true</code> if an association between the patcher fix pack and the patcher fix was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherFix(long pk, PatcherFix patcherFix) {
		PatcherFixPack patcherFixPack = fetchByPrimaryKey(pk);

		if (patcherFixPack == null) {
			return patcherFixPackToPatcherFixTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				patcherFix.getPrimaryKey());
		}
		else {
			return patcherFixPackToPatcherFixTableMapper.addTableMapping(
				patcherFixPack.getCompanyId(), pk, patcherFix.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher fix pack and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherFixes(long pk, long[] patcherFixPKs) {
		long companyId = 0;

		PatcherFixPack patcherFixPack = fetchByPrimaryKey(pk);

		if (patcherFixPack == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherFixPack.getCompanyId();
		}

		long[] addedKeys =
			patcherFixPackToPatcherFixTableMapper.addTableMappings(
				companyId, pk, patcherFixPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixes the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher fix pack and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherFixes(long pk, List<PatcherFix> patcherFixes) {
		return addPatcherFixes(
			pk,
			ListUtil.toLongArray(
				patcherFixes, PatcherFix.PATCHER_FIX_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the patcher fix pack and its patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack to clear the associated patcher fixes from
	 */
	@Override
	public void clearPatcherFixes(long pk) {
		patcherFixPackToPatcherFixTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPK the primary key of the patcher fix
	 */
	@Override
	public void removePatcherFix(long pk, long patcherFixPK) {
		patcherFixPackToPatcherFixTableMapper.deleteTableMapping(
			pk, patcherFixPK);
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFix the patcher fix
	 */
	@Override
	public void removePatcherFix(long pk, PatcherFix patcherFix) {
		patcherFixPackToPatcherFixTableMapper.deleteTableMapping(
			pk, patcherFix.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 */
	@Override
	public void removePatcherFixes(long pk, long[] patcherFixPKs) {
		patcherFixPackToPatcherFixTableMapper.deleteTableMappings(
			pk, patcherFixPKs);
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
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
	 * Sets the patcher fixes associated with the patcher fix pack, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPKs the primary keys of the patcher fixes to be associated with the patcher fix pack
	 */
	@Override
	public void setPatcherFixes(long pk, long[] patcherFixPKs) {
		Set<Long> newPatcherFixPKsSet = SetUtil.fromArray(patcherFixPKs);
		Set<Long> oldPatcherFixPKsSet = SetUtil.fromArray(
			patcherFixPackToPatcherFixTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removePatcherFixPKsSet = new HashSet<Long>(
			oldPatcherFixPKsSet);

		removePatcherFixPKsSet.removeAll(newPatcherFixPKsSet);

		patcherFixPackToPatcherFixTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removePatcherFixPKsSet));

		newPatcherFixPKsSet.removeAll(oldPatcherFixPKsSet);

		long companyId = 0;

		PatcherFixPack patcherFixPack = fetchByPrimaryKey(pk);

		if (patcherFixPack == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherFixPack.getCompanyId();
		}

		patcherFixPackToPatcherFixTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newPatcherFixPKsSet));
	}

	/**
	 * Sets the patcher fixes associated with the patcher fix pack, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixes the patcher fixes to be associated with the patcher fix pack
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
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "patcherFixPackId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERFIXPACK;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherFixPackModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher fix pack persistence.
	 */
	@Activate
	public void activate() {
		patcherFixPackToPatcherFixTableMapper =
			TableMapperFactory.getTableMapper(
				"OSBPatcher_PFixes_PFixPacks#patcherFixPackId",
				"OSBPatcher_PFixes_PFixPacks", "companyId", "patcherFixPackId",
				"patcherFixId", this, PatcherFix.class);

		_uniquePersistenceFinderByPatcherBuildId =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByPatcherBuildId",
					new String[] {Long.class.getName()},
					new String[] {"patcherBuildId"}, 0, 0, false,
					PatcherFixPack::getPatcherBuildId),
				_SQL_SELECT_PATCHERFIXPACK_WHERE, "",
				new FinderColumn<>(
					"patcherFixPack.", "patcherBuildId", FinderColumn.Type.LONG,
					"=", true, true, PatcherFixPack::getPatcherBuildId));

		_collectionPersistenceFinderByPatcherFixComponentId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByPatcherFixComponentId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"patcherFixComponentId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByPatcherFixComponentId",
					new String[] {Long.class.getName()},
					new String[] {"patcherFixComponentId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByPatcherFixComponentId",
					new String[] {Long.class.getName()},
					new String[] {"patcherFixComponentId"}, false),
				_SQL_SELECT_PATCHERFIXPACK_WHERE,
				_SQL_COUNT_PATCHERFIXPACK_WHERE,
				PatcherFixPackModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"patcherFixPack.", "patcherFixComponentId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherFixPack::getPatcherFixComponentId));

		_collectionPersistenceFinderByVersion =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByVersion",
					new String[] {
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"version"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByVersion",
					new String[] {Integer.class.getName()},
					new String[] {"version"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByVersion",
					new String[] {Integer.class.getName()},
					new String[] {"version"}, false),
				_SQL_SELECT_PATCHERFIXPACK_WHERE,
				_SQL_COUNT_PATCHERFIXPACK_WHERE,
				PatcherFixPackModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"patcherFixPack.", "version", FinderColumn.Type.INTEGER,
					"=", true, true, PatcherFixPack::getVersion));

		_collectionPersistenceFinderByPFCI_PPVI =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPFCI_PPVI",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"patcherFixComponentId", "patcherProjectVersionId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByPFCI_PPVI",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {
						"patcherFixComponentId", "patcherProjectVersionId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByPFCI_PPVI",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {
						"patcherFixComponentId", "patcherProjectVersionId"
					},
					false),
				_SQL_SELECT_PATCHERFIXPACK_WHERE,
				_SQL_COUNT_PATCHERFIXPACK_WHERE,
				PatcherFixPackModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"patcherFixPack.", "patcherFixComponentId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherFixPack::getPatcherFixComponentId),
				new FinderColumn<>(
					"patcherFixPack.", "patcherProjectVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherFixPack::getPatcherProjectVersionId));

		_collectionPersistenceFinderByPFCI_V =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPFCI_V",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"patcherFixComponentId", "version"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByPFCI_V",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"patcherFixComponentId", "version"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByPFCI_V",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"patcherFixComponentId", "version"}, false),
				_SQL_SELECT_PATCHERFIXPACK_WHERE,
				_SQL_COUNT_PATCHERFIXPACK_WHERE,
				PatcherFixPackModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"patcherFixPack.", "patcherFixComponentId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherFixPack::getPatcherFixComponentId),
				new FinderColumn<>(
					"patcherFixPack.", "version", FinderColumn.Type.INTEGER,
					"=", true, true, PatcherFixPack::getVersion));

		_uniquePersistenceFinderByPFCI_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByPFCI_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"patcherProjectVersionId", "name"}, 0, 2, false,
				PatcherFixPack::getPatcherProjectVersionId,
				convertNullFunction(PatcherFixPack::getName)),
			_SQL_SELECT_PATCHERFIXPACK_WHERE, "",
			new FinderColumn<>(
				"patcherFixPack.", "patcherProjectVersionId",
				FinderColumn.Type.LONG, "=", true, true,
				PatcherFixPack::getPatcherProjectVersionId),
			new FinderColumn<>(
				"patcherFixPack.", "name", FinderColumn.Type.STRING, "=", true,
				true, PatcherFixPack::getName));

		_collectionPersistenceFinderByPFCI_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPFCI_S",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"patcherProjectVersionId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByPFCI_S",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"patcherProjectVersionId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByPFCI_S",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"patcherProjectVersionId", "status"}, false),
				_SQL_SELECT_PATCHERFIXPACK_WHERE,
				_SQL_COUNT_PATCHERFIXPACK_WHERE,
				PatcherFixPackModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"patcherFixPack.", "patcherProjectVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherFixPack::getPatcherProjectVersionId),
				new FinderColumn<>(
					"patcherFixPack.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, PatcherFixPack::getStatus));

		_collectionPersistenceFinderByPFCI_PPVI_GtV =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByPFCI_PPVI_GtV",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"patcherFixComponentId", "patcherProjectVersionId",
						"version"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByPFCI_PPVI_GtV",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"patcherFixComponentId", "patcherProjectVersionId",
						"version"
					},
					false),
				_SQL_SELECT_PATCHERFIXPACK_WHERE,
				_SQL_COUNT_PATCHERFIXPACK_WHERE,
				PatcherFixPackModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"patcherFixPack.", "patcherFixComponentId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherFixPack::getPatcherFixComponentId),
				new FinderColumn<>(
					"patcherFixPack.", "patcherProjectVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherFixPack::getPatcherProjectVersionId),
				new FinderColumn<>(
					"patcherFixPack.", "version", FinderColumn.Type.INTEGER,
					">", true, true, PatcherFixPack::getVersion));

		_collectionPersistenceFinderByPFCI_PPVI_LtV =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByPFCI_PPVI_LtV",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"patcherFixComponentId", "patcherProjectVersionId",
						"version"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByPFCI_PPVI_LtV",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"patcherFixComponentId", "patcherProjectVersionId",
						"version"
					},
					false),
				_SQL_SELECT_PATCHERFIXPACK_WHERE,
				_SQL_COUNT_PATCHERFIXPACK_WHERE,
				PatcherFixPackModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"patcherFixPack.", "patcherFixComponentId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherFixPack::getPatcherFixComponentId),
				new FinderColumn<>(
					"patcherFixPack.", "patcherProjectVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherFixPack::getPatcherProjectVersionId),
				new FinderColumn<>(
					"patcherFixPack.", "version", FinderColumn.Type.INTEGER,
					"<", true, true, PatcherFixPack::getVersion));

		_uniquePersistenceFinderByPFCI_PPVI_N_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByPFCI_PPVI_N_V",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName(), Integer.class.getName()
				},
				new String[] {
					"patcherFixComponentId", "patcherProjectVersionId", "name",
					"version"
				},
				0, 4, false, PatcherFixPack::getPatcherFixComponentId,
				PatcherFixPack::getPatcherProjectVersionId,
				convertNullFunction(PatcherFixPack::getName),
				PatcherFixPack::getVersion),
			_SQL_SELECT_PATCHERFIXPACK_WHERE, "",
			new FinderColumn<>(
				"patcherFixPack.", "patcherFixComponentId",
				FinderColumn.Type.LONG, "=", true, true,
				PatcherFixPack::getPatcherFixComponentId),
			new FinderColumn<>(
				"patcherFixPack.", "patcherProjectVersionId",
				FinderColumn.Type.LONG, "=", true, true,
				PatcherFixPack::getPatcherProjectVersionId),
			new FinderColumn<>(
				"patcherFixPack.", "name", FinderColumn.Type.STRING, "=", true,
				true, PatcherFixPack::getName),
			new FinderColumn<>(
				"patcherFixPack.", "version", FinderColumn.Type.INTEGER, "=",
				true, true, PatcherFixPack::getVersion));

		PatcherFixPackUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherFixPackUtil.setPersistence(null);

		entityCache.removeCache(PatcherFixPackImpl.class.getName());

		TableMapperFactory.removeTableMapper(
			"OSBPatcher_PFixes_PFixPacks#patcherFixPackId");
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

	protected TableMapper<PatcherFixPack, PatcherFix>
		patcherFixPackToPatcherFixTableMapper;

	private static final String _ENTITY_ALIAS_PREFIX =
		PatcherFixPackModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PATCHERFIXPACK =
		"SELECT patcherFixPack FROM PatcherFixPack patcherFixPack";

	private static final String _SQL_SELECT_PATCHERFIXPACK_WHERE =
		"SELECT patcherFixPack FROM PatcherFixPack patcherFixPack WHERE ";

	private static final String _SQL_COUNT_PATCHERFIXPACK_WHERE =
		"SELECT COUNT(patcherFixPack) FROM PatcherFixPack patcherFixPack WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PatcherFixPack exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherFixPackPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:855328521