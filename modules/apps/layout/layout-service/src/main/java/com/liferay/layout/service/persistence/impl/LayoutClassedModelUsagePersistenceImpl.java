/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service.persistence.impl;

import com.liferay.layout.exception.NoSuchLayoutClassedModelUsageException;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.model.LayoutClassedModelUsageTable;
import com.liferay.layout.model.impl.LayoutClassedModelUsageImpl;
import com.liferay.layout.model.impl.LayoutClassedModelUsageModelImpl;
import com.liferay.layout.service.persistence.LayoutClassedModelUsagePersistence;
import com.liferay.layout.service.persistence.LayoutClassedModelUsageUtil;
import com.liferay.layout.service.persistence.impl.constants.LayoutPersistenceConstants;
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
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the layout classed model usage service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = LayoutClassedModelUsagePersistence.class)
public class LayoutClassedModelUsagePersistenceImpl
	extends BasePersistenceImpl
		<LayoutClassedModelUsage, NoSuchLayoutClassedModelUsageException>
	implements LayoutClassedModelUsagePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutClassedModelUsageUtil</code> to access the layout classed model usage persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutClassedModelUsageImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LayoutClassedModelUsage, NoSuchLayoutClassedModelUsageException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the layout classed model usages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByUuid_First(
			String uuid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByUuid_First(
		String uuid,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of layout classed model usages where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<LayoutClassedModelUsage, NoSuchLayoutClassedModelUsageException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the layout classed model usage where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchLayoutClassedModelUsageException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByUUID_G(String uuid, long groupId)
		throws NoSuchLayoutClassedModelUsageException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the layout classed model usage where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the layout classed model usage where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout classed model usage that was removed
	 */
	@Override
	public LayoutClassedModelUsage removeByUUID_G(String uuid, long groupId)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = findByUUID_G(
			uuid, groupId);

		return remove(layoutClassedModelUsage);
	}

	/**
	 * Returns the number of layout classed model usages where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<LayoutClassedModelUsage, NoSuchLayoutClassedModelUsageException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the layout classed model usages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of layout classed model usages where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<LayoutClassedModelUsage, NoSuchLayoutClassedModelUsageException>
			_collectionPersistenceFinderByPlid;

	/**
	 * Returns an ordered range of all the layout classed model usages where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByPlid(
		long plid, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPlid.find(
			finderCache, new Object[] {plid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByPlid_First(
			long plid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		return _collectionPersistenceFinderByPlid.findFirst(
			finderCache, new Object[] {plid}, orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByPlid_First(
		long plid,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return _collectionPersistenceFinderByPlid.fetchFirst(
			finderCache, new Object[] {plid}, orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	@Override
	public void removeByPlid(long plid) {
		_collectionPersistenceFinderByPlid.remove(
			finderCache, new Object[] {plid});
	}

	/**
	 * Returns the number of layout classed model usages where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByPlid(long plid) {
		return _collectionPersistenceFinderByPlid.count(
			finderCache, new Object[] {plid});
	}

	private CollectionPersistenceFinder
		<LayoutClassedModelUsage, NoSuchLayoutClassedModelUsageException>
			_collectionPersistenceFinderByC_CN;

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CN.find(
			finderCache, new Object[] {companyId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByC_CN_First(
			long companyId, long classNameId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		return _collectionPersistenceFinderByC_CN.findFirst(
			finderCache, new Object[] {companyId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByC_CN_First(
		long companyId, long classNameId,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return _collectionPersistenceFinderByC_CN.fetchFirst(
			finderCache, new Object[] {companyId, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_CN(long companyId, long classNameId) {
		_collectionPersistenceFinderByC_CN.remove(
			finderCache, new Object[] {companyId, classNameId});
	}

	/**
	 * Returns the number of layout classed model usages where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByC_CN(long companyId, long classNameId) {
		return _collectionPersistenceFinderByC_CN.count(
			finderCache, new Object[] {companyId, classNameId});
	}

	private CollectionPersistenceFinder
		<LayoutClassedModelUsage, NoSuchLayoutClassedModelUsageException>
			_collectionPersistenceFinderByCN_CPK;

	/**
	 * Returns an ordered range of all the layout classed model usages where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByCN_CPK(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCN_CPK.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByCN_CPK_First(
			long classNameId, long classPK,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		return _collectionPersistenceFinderByCN_CPK.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByCN_CPK_First(
		long classNameId, long classPK,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return _collectionPersistenceFinderByCN_CPK.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByCN_CPK(long classNameId, long classPK) {
		_collectionPersistenceFinderByCN_CPK.remove(
			finderCache, new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the number of layout classed model usages where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByCN_CPK(long classNameId, long classPK) {
		return _collectionPersistenceFinderByCN_CPK.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private CollectionPersistenceFinder
		<LayoutClassedModelUsage, NoSuchLayoutClassedModelUsageException>
			_collectionPersistenceFinderByC_CERC_CN;

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CERC_CN(
		long companyId, String classExternalReferenceCode, long classNameId,
		int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CERC_CN.find(
			finderCache,
			new Object[] {companyId, classExternalReferenceCode, classNameId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByC_CERC_CN_First(
			long companyId, String classExternalReferenceCode, long classNameId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		return _collectionPersistenceFinderByC_CERC_CN.findFirst(
			finderCache,
			new Object[] {companyId, classExternalReferenceCode, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByC_CERC_CN_First(
		long companyId, String classExternalReferenceCode, long classNameId,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return _collectionPersistenceFinderByC_CERC_CN.fetchFirst(
			finderCache,
			new Object[] {companyId, classExternalReferenceCode, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_CERC_CN(
		long companyId, String classExternalReferenceCode, long classNameId) {

		_collectionPersistenceFinderByC_CERC_CN.remove(
			finderCache,
			new Object[] {companyId, classExternalReferenceCode, classNameId});
	}

	/**
	 * Returns the number of layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByC_CERC_CN(
		long companyId, String classExternalReferenceCode, long classNameId) {

		return _collectionPersistenceFinderByC_CERC_CN.count(
			finderCache,
			new Object[] {companyId, classExternalReferenceCode, classNameId});
	}

	private CollectionPersistenceFinder
		<LayoutClassedModelUsage, NoSuchLayoutClassedModelUsageException>
			_collectionPersistenceFinderByC_CN_CT;

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CN_CT(
		long companyId, long classNameId, long containerType, int start,
		int end, OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CN_CT.find(
			finderCache, new Object[] {companyId, classNameId, containerType},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByC_CN_CT_First(
			long companyId, long classNameId, long containerType,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		return _collectionPersistenceFinderByC_CN_CT.findFirst(
			finderCache, new Object[] {companyId, classNameId, containerType},
			orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByC_CN_CT_First(
		long companyId, long classNameId, long containerType,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return _collectionPersistenceFinderByC_CN_CT.fetchFirst(
			finderCache, new Object[] {companyId, classNameId, containerType},
			orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where companyId = &#63; and classNameId = &#63; and containerType = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 */
	@Override
	public void removeByC_CN_CT(
		long companyId, long classNameId, long containerType) {

		_collectionPersistenceFinderByC_CN_CT.remove(
			finderCache, new Object[] {companyId, classNameId, containerType});
	}

	/**
	 * Returns the number of layout classed model usages where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByC_CN_CT(
		long companyId, long classNameId, long containerType) {

		return _collectionPersistenceFinderByC_CN_CT.count(
			finderCache, new Object[] {companyId, classNameId, containerType});
	}

	private CollectionPersistenceFinder
		<LayoutClassedModelUsage, NoSuchLayoutClassedModelUsageException>
			_collectionPersistenceFinderByCN_CPK_T;

	/**
	 * Returns an ordered range of all the layout classed model usages where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByCN_CPK_T(
		long classNameId, long classPK, int type, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCN_CPK_T.find(
			finderCache, new Object[] {classNameId, classPK, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByCN_CPK_T_First(
			long classNameId, long classPK, int type,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		return _collectionPersistenceFinderByCN_CPK_T.findFirst(
			finderCache, new Object[] {classNameId, classPK, type},
			orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByCN_CPK_T_First(
		long classNameId, long classPK, int type,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return _collectionPersistenceFinderByCN_CPK_T.fetchFirst(
			finderCache, new Object[] {classNameId, classPK, type},
			orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where classNameId = &#63; and classPK = &#63; and type = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 */
	@Override
	public void removeByCN_CPK_T(long classNameId, long classPK, int type) {
		_collectionPersistenceFinderByCN_CPK_T.remove(
			finderCache, new Object[] {classNameId, classPK, type});
	}

	/**
	 * Returns the number of layout classed model usages where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByCN_CPK_T(long classNameId, long classPK, int type) {
		return _collectionPersistenceFinderByCN_CPK_T.count(
			finderCache, new Object[] {classNameId, classPK, type});
	}

	private CollectionPersistenceFinder
		<LayoutClassedModelUsage, NoSuchLayoutClassedModelUsageException>
			_collectionPersistenceFinderByCK_CT_P;

	/**
	 * Returns an ordered range of all the layout classed model usages where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByCK_CT_P(
		String containerKey, long containerType, long plid, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCK_CT_P.find(
			finderCache, new Object[] {containerKey, containerType, plid},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByCK_CT_P_First(
			String containerKey, long containerType, long plid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		return _collectionPersistenceFinderByCK_CT_P.findFirst(
			finderCache, new Object[] {containerKey, containerType, plid},
			orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByCK_CT_P_First(
		String containerKey, long containerType, long plid,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return _collectionPersistenceFinderByCK_CT_P.fetchFirst(
			finderCache, new Object[] {containerKey, containerType, plid},
			orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where containerKey = &#63; and containerType = &#63; and plid = &#63; from the database.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 */
	@Override
	public void removeByCK_CT_P(
		String containerKey, long containerType, long plid) {

		_collectionPersistenceFinderByCK_CT_P.remove(
			finderCache, new Object[] {containerKey, containerType, plid});
	}

	/**
	 * Returns the number of layout classed model usages where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByCK_CT_P(
		String containerKey, long containerType, long plid) {

		return _collectionPersistenceFinderByCK_CT_P.count(
			finderCache, new Object[] {containerKey, containerType, plid});
	}

	private CollectionPersistenceFinder
		<LayoutClassedModelUsage, NoSuchLayoutClassedModelUsageException>
			_collectionPersistenceFinderByC_CERC_CN_T;

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CERC_CN_T(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CERC_CN_T.find(
			finderCache,
			new Object[] {
				companyId, classExternalReferenceCode, classNameId, type
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByC_CERC_CN_T_First(
			long companyId, String classExternalReferenceCode, long classNameId,
			int type,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		return _collectionPersistenceFinderByC_CERC_CN_T.findFirst(
			finderCache,
			new Object[] {
				companyId, classExternalReferenceCode, classNameId, type
			},
			orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByC_CERC_CN_T_First(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return _collectionPersistenceFinderByC_CERC_CN_T.fetchFirst(
			finderCache,
			new Object[] {
				companyId, classExternalReferenceCode, classNameId, type
			},
			orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 */
	@Override
	public void removeByC_CERC_CN_T(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type) {

		_collectionPersistenceFinderByC_CERC_CN_T.remove(
			finderCache,
			new Object[] {
				companyId, classExternalReferenceCode, classNameId, type
			});
	}

	/**
	 * Returns the number of layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByC_CERC_CN_T(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type) {

		return _collectionPersistenceFinderByC_CERC_CN_T.count(
			finderCache,
			new Object[] {
				companyId, classExternalReferenceCode, classNameId, type
			});
	}

	private UniquePersistenceFinder
		<LayoutClassedModelUsage, NoSuchLayoutClassedModelUsageException>
			_uniquePersistenceFinderByG_CERC_CN_CPK_CK_CT_P;

	/**
	 * Returns the layout classed model usage where groupId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and classPK = &#63; and containerKey = &#63; and containerType = &#63; and plid = &#63; or throws a <code>NoSuchLayoutClassedModelUsageException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @return the matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByG_CERC_CN_CPK_CK_CT_P(
			long groupId, String classExternalReferenceCode, long classNameId,
			long classPK, String containerKey, long containerType, long plid)
		throws NoSuchLayoutClassedModelUsageException {

		return _uniquePersistenceFinderByG_CERC_CN_CPK_CK_CT_P.find(
			finderCache,
			new Object[] {
				groupId, classExternalReferenceCode, classNameId, classPK,
				containerKey, containerType, plid
			});
	}

	/**
	 * Returns the layout classed model usage where groupId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and classPK = &#63; and containerKey = &#63; and containerType = &#63; and plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByG_CERC_CN_CPK_CK_CT_P(
		long groupId, String classExternalReferenceCode, long classNameId,
		long classPK, String containerKey, long containerType, long plid,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_CERC_CN_CPK_CK_CT_P.fetch(
			finderCache,
			new Object[] {
				groupId, classExternalReferenceCode, classNameId, classPK,
				containerKey, containerType, plid
			},
			useFinderCache);
	}

	/**
	 * Removes the layout classed model usage where groupId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and classPK = &#63; and containerKey = &#63; and containerType = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @return the layout classed model usage that was removed
	 */
	@Override
	public LayoutClassedModelUsage removeByG_CERC_CN_CPK_CK_CT_P(
			long groupId, String classExternalReferenceCode, long classNameId,
			long classPK, String containerKey, long containerType, long plid)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage =
			findByG_CERC_CN_CPK_CK_CT_P(
				groupId, classExternalReferenceCode, classNameId, classPK,
				containerKey, containerType, plid);

		return remove(layoutClassedModelUsage);
	}

	/**
	 * Returns the number of layout classed model usages where groupId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and classPK = &#63; and containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByG_CERC_CN_CPK_CK_CT_P(
		long groupId, String classExternalReferenceCode, long classNameId,
		long classPK, String containerKey, long containerType, long plid) {

		return _uniquePersistenceFinderByG_CERC_CN_CPK_CK_CT_P.count(
			finderCache,
			new Object[] {
				groupId, classExternalReferenceCode, classNameId, classPK,
				containerKey, containerType, plid
			});
	}

	public LayoutClassedModelUsagePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutClassedModelUsage.class);

		setModelImplClass(LayoutClassedModelUsageImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutClassedModelUsageTable.INSTANCE);
	}

	/**
	 * Creates a new layout classed model usage with the primary key. Does not add the layout classed model usage to the database.
	 *
	 * @param layoutClassedModelUsageId the primary key for the new layout classed model usage
	 * @return the new layout classed model usage
	 */
	@Override
	public LayoutClassedModelUsage create(long layoutClassedModelUsageId) {
		LayoutClassedModelUsage layoutClassedModelUsage =
			new LayoutClassedModelUsageImpl();

		layoutClassedModelUsage.setNew(true);
		layoutClassedModelUsage.setPrimaryKey(layoutClassedModelUsageId);

		String uuid = PortalUUIDUtil.generate();

		layoutClassedModelUsage.setUuid(uuid);

		layoutClassedModelUsage.setCompanyId(CompanyThreadLocal.getCompanyId());

		return layoutClassedModelUsage;
	}

	/**
	 * Removes the layout classed model usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutClassedModelUsageId the primary key of the layout classed model usage
	 * @return the layout classed model usage that was removed
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	@Override
	public LayoutClassedModelUsage remove(long layoutClassedModelUsageId)
		throws NoSuchLayoutClassedModelUsageException {

		return remove((Serializable)layoutClassedModelUsageId);
	}

	@Override
	protected LayoutClassedModelUsage removeImpl(
		LayoutClassedModelUsage layoutClassedModelUsage) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutClassedModelUsage)) {
				layoutClassedModelUsage = (LayoutClassedModelUsage)session.get(
					LayoutClassedModelUsageImpl.class,
					layoutClassedModelUsage.getPrimaryKeyObj());
			}

			if ((layoutClassedModelUsage != null) &&
				ctPersistenceHelper.isRemove(layoutClassedModelUsage)) {

				session.delete(layoutClassedModelUsage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutClassedModelUsage != null) {
			clearCache(layoutClassedModelUsage);
		}

		return layoutClassedModelUsage;
	}

	@Override
	public LayoutClassedModelUsage updateImpl(
		LayoutClassedModelUsage layoutClassedModelUsage) {

		boolean isNew = layoutClassedModelUsage.isNew();

		if (!(layoutClassedModelUsage instanceof
				LayoutClassedModelUsageModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(layoutClassedModelUsage.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutClassedModelUsage);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutClassedModelUsage proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutClassedModelUsage implementation " +
					layoutClassedModelUsage.getClass());
		}

		LayoutClassedModelUsageModelImpl layoutClassedModelUsageModelImpl =
			(LayoutClassedModelUsageModelImpl)layoutClassedModelUsage;

		if (Validator.isNull(layoutClassedModelUsage.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			layoutClassedModelUsage.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutClassedModelUsage.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutClassedModelUsage.setCreateDate(date);
			}
			else {
				layoutClassedModelUsage.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutClassedModelUsageModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutClassedModelUsage.setModifiedDate(date);
			}
			else {
				layoutClassedModelUsage.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(layoutClassedModelUsage)) {
				if (!isNew) {
					session.evict(
						LayoutClassedModelUsageImpl.class,
						layoutClassedModelUsage.getPrimaryKeyObj());
				}

				session.save(layoutClassedModelUsage);
			}
			else {
				layoutClassedModelUsage =
					(LayoutClassedModelUsage)session.merge(
						layoutClassedModelUsage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layoutClassedModelUsage, false);

		if (isNew) {
			layoutClassedModelUsage.setNew(false);
		}

		layoutClassedModelUsage.resetOriginalValues();

		return layoutClassedModelUsage;
	}

	/**
	 * Returns the layout classed model usage with the primary key or throws a <code>NoSuchLayoutClassedModelUsageException</code> if it could not be found.
	 *
	 * @param layoutClassedModelUsageId the primary key of the layout classed model usage
	 * @return the layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByPrimaryKey(
			long layoutClassedModelUsageId)
		throws NoSuchLayoutClassedModelUsageException {

		return findByPrimaryKey((Serializable)layoutClassedModelUsageId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the layout classed model usage with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutClassedModelUsageId the primary key of the layout classed model usage
	 * @return the layout classed model usage, or <code>null</code> if a layout classed model usage with the primary key could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByPrimaryKey(
		long layoutClassedModelUsageId) {

		return fetchByPrimaryKey((Serializable)layoutClassedModelUsageId);
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
		return "layoutClassedModelUsageId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTCLASSEDMODELUSAGE;
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
		return LayoutClassedModelUsageModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LayoutClassedModelUsage";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("classExternalReferenceCode");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("containerKey");
		ctMergeColumnNames.add("containerType");
		ctMergeColumnNames.add("plid");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("layoutClassedModelUsageId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {
				"groupId", "classExternalReferenceCode", "classNameId",
				"classPK", "containerKey", "containerType", "plid"
			});
	}

	/**
	 * Initializes the layout classed model usage persistence.
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
			_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE,
			_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE,
			LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layoutClassedModelUsage.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutClassedModelUsage::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(LayoutClassedModelUsage::getUuid),
				LayoutClassedModelUsage::getGroupId),
			_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE, "",
			new FinderColumn<>(
				"layoutClassedModelUsage.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutClassedModelUsage::getUuid),
			new FinderColumn<>(
				"layoutClassedModelUsage.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, LayoutClassedModelUsage::getGroupId));

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
				_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE,
				_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE,
				LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layoutClassedModelUsage.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutClassedModelUsage::getUuid),
				new FinderColumn<>(
					"layoutClassedModelUsage.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutClassedModelUsage::getCompanyId));

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
			_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE,
			_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE,
			LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX,
			"layoutClassedModelUsage.containerKey IS NOT NULL",
			"layoutClassedModelUsage.containerKey IS NOT NULL",
			new FinderColumn<>(
				"layoutClassedModelUsage.", "plid", FinderColumn.Type.LONG, "=",
				true, true, LayoutClassedModelUsage::getPlid));

		_collectionPersistenceFinderByC_CN = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CN",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CN",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_CN",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, false),
			_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE,
			_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE,
			LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX,
			"layoutClassedModelUsage.containerKey IS NOT NULL",
			"layoutClassedModelUsage.containerKey IS NOT NULL",
			new FinderColumn<>(
				"layoutClassedModelUsage.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, LayoutClassedModelUsage::getCompanyId),
			new FinderColumn<>(
				"layoutClassedModelUsage.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				LayoutClassedModelUsage::getClassNameId));

		_collectionPersistenceFinderByCN_CPK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCN_CPK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCN_CPK",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCN_CPK",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"classNameId", "classPK"}, false),
				_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE,
				_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE,
				LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX,
				"layoutClassedModelUsage.containerKey IS NOT NULL",
				"layoutClassedModelUsage.containerKey IS NOT NULL",
				new FinderColumn<>(
					"layoutClassedModelUsage.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutClassedModelUsage::getClassNameId),
				new FinderColumn<>(
					"layoutClassedModelUsage.", "classPK",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutClassedModelUsage::getClassPK));

		_collectionPersistenceFinderByC_CERC_CN =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CERC_CN",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "classExternalReferenceCode", "classNameId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByC_CERC_CN",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"companyId", "classExternalReferenceCode", "classNameId"
					},
					0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByC_CERC_CN",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"companyId", "classExternalReferenceCode", "classNameId"
					},
					0, 2, false, null),
				_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE,
				_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE,
				LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX,
				"layoutClassedModelUsage.containerKey IS NOT NULL",
				"layoutClassedModelUsage.containerKey IS NOT NULL",
				new FinderColumn<>(
					"layoutClassedModelUsage.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutClassedModelUsage::getCompanyId),
				new FinderColumn<>(
					"layoutClassedModelUsage.", "classExternalReferenceCode",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutClassedModelUsage::getClassExternalReferenceCode),
				new FinderColumn<>(
					"layoutClassedModelUsage.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutClassedModelUsage::getClassNameId));

		_collectionPersistenceFinderByC_CN_CT =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CN_CT",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "classNameId", "containerType"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CN_CT",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"companyId", "classNameId", "containerType"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_CN_CT",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"companyId", "classNameId", "containerType"},
					false),
				_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE,
				_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE,
				LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX,
				"layoutClassedModelUsage.containerKey IS NOT NULL",
				"layoutClassedModelUsage.containerKey IS NOT NULL",
				new FinderColumn<>(
					"layoutClassedModelUsage.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutClassedModelUsage::getCompanyId),
				new FinderColumn<>(
					"layoutClassedModelUsage.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutClassedModelUsage::getClassNameId),
				new FinderColumn<>(
					"layoutClassedModelUsage.", "containerType",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutClassedModelUsage::getContainerType));

		_collectionPersistenceFinderByCN_CPK_T =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCN_CPK_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"classNameId", "classPK", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCN_CPK_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"classNameId", "classPK", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCN_CPK_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"classNameId", "classPK", "type_"}, false),
				_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE,
				_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE,
				LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX,
				"layoutClassedModelUsage.containerKey IS NOT NULL",
				"layoutClassedModelUsage.containerKey IS NOT NULL",
				new FinderColumn<>(
					"layoutClassedModelUsage.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutClassedModelUsage::getClassNameId),
				new FinderColumn<>(
					"layoutClassedModelUsage.", "classPK",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutClassedModelUsage::getClassPK),
				new FinderColumn<>(
					"layoutClassedModelUsage.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutClassedModelUsage::getType));

		_collectionPersistenceFinderByCK_CT_P =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCK_CT_P",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"containerKey", "containerType", "plid"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCK_CT_P",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"containerKey", "containerType", "plid"}, 0,
					1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCK_CT_P",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"containerKey", "containerType", "plid"}, 0,
					1, false, null),
				_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE,
				_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE,
				LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX,
				"layoutClassedModelUsage.containerKey IS NOT NULL",
				"layoutClassedModelUsage.containerKey IS NOT NULL",
				new FinderColumn<>(
					"layoutClassedModelUsage.", "containerKey",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutClassedModelUsage::getContainerKey),
				new FinderColumn<>(
					"layoutClassedModelUsage.", "containerType",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutClassedModelUsage::getContainerType),
				new FinderColumn<>(
					"layoutClassedModelUsage.", "plid", FinderColumn.Type.LONG,
					"=", true, true, LayoutClassedModelUsage::getPlid));

		_collectionPersistenceFinderByC_CERC_CN_T =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CERC_CN_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "classExternalReferenceCode",
						"classNameId", "type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByC_CERC_CN_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"companyId", "classExternalReferenceCode",
						"classNameId", "type_"
					},
					0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByC_CERC_CN_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"companyId", "classExternalReferenceCode",
						"classNameId", "type_"
					},
					0, 2, false, null),
				_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE,
				_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE,
				LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX,
				"layoutClassedModelUsage.containerKey IS NOT NULL",
				"layoutClassedModelUsage.containerKey IS NOT NULL",
				new FinderColumn<>(
					"layoutClassedModelUsage.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutClassedModelUsage::getCompanyId),
				new FinderColumn<>(
					"layoutClassedModelUsage.", "classExternalReferenceCode",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutClassedModelUsage::getClassExternalReferenceCode),
				new FinderColumn<>(
					"layoutClassedModelUsage.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutClassedModelUsage::getClassNameId),
				new FinderColumn<>(
					"layoutClassedModelUsage.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutClassedModelUsage::getType));

		_uniquePersistenceFinderByG_CERC_CN_CPK_CK_CT_P =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByG_CERC_CN_CPK_CK_CT_P",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"groupId", "classExternalReferenceCode", "classNameId",
						"classPK", "containerKey", "containerType", "plid"
					},
					0, 18, false, LayoutClassedModelUsage::getGroupId,
					convertNullFunction(
						LayoutClassedModelUsage::getClassExternalReferenceCode),
					LayoutClassedModelUsage::getClassNameId,
					LayoutClassedModelUsage::getClassPK,
					convertNullFunction(
						LayoutClassedModelUsage::getContainerKey),
					LayoutClassedModelUsage::getContainerType,
					LayoutClassedModelUsage::getPlid),
				_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE, "",
				new FinderColumn<>(
					"layoutClassedModelUsage.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutClassedModelUsage::getGroupId),
				new FinderColumn<>(
					"layoutClassedModelUsage.", "classExternalReferenceCode",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutClassedModelUsage::getClassExternalReferenceCode),
				new FinderColumn<>(
					"layoutClassedModelUsage.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutClassedModelUsage::getClassNameId),
				new FinderColumn<>(
					"layoutClassedModelUsage.", "classPK",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutClassedModelUsage::getClassPK),
				new FinderColumn<>(
					"layoutClassedModelUsage.", "containerKey",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutClassedModelUsage::getContainerKey),
				new FinderColumn<>(
					"layoutClassedModelUsage.", "containerType",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutClassedModelUsage::getContainerType),
				new FinderColumn<>(
					"layoutClassedModelUsage.", "plid", FinderColumn.Type.LONG,
					"=", true, true, LayoutClassedModelUsage::getPlid));

		LayoutClassedModelUsageUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LayoutClassedModelUsageUtil.setPersistence(null);

		entityCache.removeCache(LayoutClassedModelUsageImpl.class.getName());
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
		LayoutClassedModelUsageModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUTCLASSEDMODELUSAGE =
		"SELECT layoutClassedModelUsage FROM LayoutClassedModelUsage layoutClassedModelUsage";

	private static final String _SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE =
		"SELECT layoutClassedModelUsage FROM LayoutClassedModelUsage layoutClassedModelUsage WHERE ";

	private static final String _SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE =
		"SELECT COUNT(layoutClassedModelUsage) FROM LayoutClassedModelUsage layoutClassedModelUsage WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LayoutClassedModelUsage exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutClassedModelUsagePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-953048551