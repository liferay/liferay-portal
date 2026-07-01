/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.persistence.impl;

import com.liferay.depot.exception.NoSuchEntryPinException;
import com.liferay.depot.model.DepotEntryPin;
import com.liferay.depot.model.DepotEntryPinTable;
import com.liferay.depot.model.impl.DepotEntryPinImpl;
import com.liferay.depot.model.impl.DepotEntryPinModelImpl;
import com.liferay.depot.service.persistence.DepotEntryPinPersistence;
import com.liferay.depot.service.persistence.DepotEntryPinUtil;
import com.liferay.depot.service.persistence.impl.constants.DepotPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
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
 * The persistence implementation for the depot entry pin service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DepotEntryPinPersistence.class)
public class DepotEntryPinPersistenceImpl
	extends BasePersistenceImpl<DepotEntryPin, NoSuchEntryPinException>
	implements DepotEntryPinPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DepotEntryPinUtil</code> to access the depot entry pin persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DepotEntryPinImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<DepotEntryPin, NoSuchEntryPinException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the depot entry pins where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryPinModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of depot entry pins
	 * @param end the upper bound of the range of depot entry pins (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry pins
	 */
	@Override
	public List<DepotEntryPin> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DepotEntryPin> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first depot entry pin in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry pin
	 * @throws NoSuchEntryPinException if a matching depot entry pin could not be found
	 */
	@Override
	public DepotEntryPin findByUuid_First(
			String uuid, OrderByComparator<DepotEntryPin> orderByComparator)
		throws NoSuchEntryPinException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first depot entry pin in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry pin, or <code>null</code> if a matching depot entry pin could not be found
	 */
	@Override
	public DepotEntryPin fetchByUuid_First(
		String uuid, OrderByComparator<DepotEntryPin> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the depot entry pins where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of depot entry pins where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching depot entry pins
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<DepotEntryPin, NoSuchEntryPinException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the depot entry pin where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryPinException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching depot entry pin
	 * @throws NoSuchEntryPinException if a matching depot entry pin could not be found
	 */
	@Override
	public DepotEntryPin findByUUID_G(String uuid, long groupId)
		throws NoSuchEntryPinException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the depot entry pin where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching depot entry pin, or <code>null</code> if a matching depot entry pin could not be found
	 */
	@Override
	public DepotEntryPin fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the depot entry pin where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the depot entry pin that was removed
	 */
	@Override
	public DepotEntryPin removeByUUID_G(String uuid, long groupId)
		throws NoSuchEntryPinException {

		DepotEntryPin depotEntryPin = findByUUID_G(uuid, groupId);

		return remove(depotEntryPin);
	}

	/**
	 * Returns the number of depot entry pins where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching depot entry pins
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<DepotEntryPin, NoSuchEntryPinException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the depot entry pins where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryPinModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of depot entry pins
	 * @param end the upper bound of the range of depot entry pins (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry pins
	 */
	@Override
	public List<DepotEntryPin> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DepotEntryPin> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first depot entry pin in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry pin
	 * @throws NoSuchEntryPinException if a matching depot entry pin could not be found
	 */
	@Override
	public DepotEntryPin findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DepotEntryPin> orderByComparator)
		throws NoSuchEntryPinException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first depot entry pin in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry pin, or <code>null</code> if a matching depot entry pin could not be found
	 */
	@Override
	public DepotEntryPin fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DepotEntryPin> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the depot entry pins where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of depot entry pins where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching depot entry pins
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<DepotEntryPin, NoSuchEntryPinException>
		_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the depot entry pins where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryPinModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of depot entry pins
	 * @param end the upper bound of the range of depot entry pins (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry pins
	 */
	@Override
	public List<DepotEntryPin> findByUserId(
		long userId, int start, int end,
		OrderByComparator<DepotEntryPin> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			finderCache, new Object[] {userId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first depot entry pin in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry pin
	 * @throws NoSuchEntryPinException if a matching depot entry pin could not be found
	 */
	@Override
	public DepotEntryPin findByUserId_First(
			long userId, OrderByComparator<DepotEntryPin> orderByComparator)
		throws NoSuchEntryPinException {

		return _collectionPersistenceFinderByUserId.findFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Returns the first depot entry pin in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry pin, or <code>null</code> if a matching depot entry pin could not be found
	 */
	@Override
	public DepotEntryPin fetchByUserId_First(
		long userId, OrderByComparator<DepotEntryPin> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Removes all the depot entry pins where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the number of depot entry pins where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching depot entry pins
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			finderCache, new Object[] {userId});
	}

	private CollectionPersistenceFinder<DepotEntryPin, NoSuchEntryPinException>
		_collectionPersistenceFinderByDepotEntryId;

	/**
	 * Returns an ordered range of all the depot entry pins where depotEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryPinModelImpl</code>.
	 * </p>
	 *
	 * @param depotEntryId the depot entry ID
	 * @param start the lower bound of the range of depot entry pins
	 * @param end the upper bound of the range of depot entry pins (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry pins
	 */
	@Override
	public List<DepotEntryPin> findByDepotEntryId(
		long depotEntryId, int start, int end,
		OrderByComparator<DepotEntryPin> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByDepotEntryId.find(
			finderCache, new Object[] {depotEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first depot entry pin in the ordered set where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry pin
	 * @throws NoSuchEntryPinException if a matching depot entry pin could not be found
	 */
	@Override
	public DepotEntryPin findByDepotEntryId_First(
			long depotEntryId,
			OrderByComparator<DepotEntryPin> orderByComparator)
		throws NoSuchEntryPinException {

		return _collectionPersistenceFinderByDepotEntryId.findFirst(
			finderCache, new Object[] {depotEntryId}, orderByComparator);
	}

	/**
	 * Returns the first depot entry pin in the ordered set where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry pin, or <code>null</code> if a matching depot entry pin could not be found
	 */
	@Override
	public DepotEntryPin fetchByDepotEntryId_First(
		long depotEntryId, OrderByComparator<DepotEntryPin> orderByComparator) {

		return _collectionPersistenceFinderByDepotEntryId.fetchFirst(
			finderCache, new Object[] {depotEntryId}, orderByComparator);
	}

	/**
	 * Removes all the depot entry pins where depotEntryId = &#63; from the database.
	 *
	 * @param depotEntryId the depot entry ID
	 */
	@Override
	public void removeByDepotEntryId(long depotEntryId) {
		_collectionPersistenceFinderByDepotEntryId.remove(
			finderCache, new Object[] {depotEntryId});
	}

	/**
	 * Returns the number of depot entry pins where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @return the number of matching depot entry pins
	 */
	@Override
	public int countByDepotEntryId(long depotEntryId) {
		return _collectionPersistenceFinderByDepotEntryId.count(
			finderCache, new Object[] {depotEntryId});
	}

	private UniquePersistenceFinder<DepotEntryPin, NoSuchEntryPinException>
		_uniquePersistenceFinderByU_D;

	/**
	 * Returns the depot entry pin where userId = &#63; and depotEntryId = &#63; or throws a <code>NoSuchEntryPinException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param depotEntryId the depot entry ID
	 * @return the matching depot entry pin
	 * @throws NoSuchEntryPinException if a matching depot entry pin could not be found
	 */
	@Override
	public DepotEntryPin findByU_D(long userId, long depotEntryId)
		throws NoSuchEntryPinException {

		return _uniquePersistenceFinderByU_D.find(
			finderCache, new Object[] {userId, depotEntryId});
	}

	/**
	 * Returns the depot entry pin where userId = &#63; and depotEntryId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param depotEntryId the depot entry ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching depot entry pin, or <code>null</code> if a matching depot entry pin could not be found
	 */
	@Override
	public DepotEntryPin fetchByU_D(
		long userId, long depotEntryId, boolean useFinderCache) {

		return _uniquePersistenceFinderByU_D.fetch(
			finderCache, new Object[] {userId, depotEntryId}, useFinderCache);
	}

	/**
	 * Removes the depot entry pin where userId = &#63; and depotEntryId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param depotEntryId the depot entry ID
	 * @return the depot entry pin that was removed
	 */
	@Override
	public DepotEntryPin removeByU_D(long userId, long depotEntryId)
		throws NoSuchEntryPinException {

		DepotEntryPin depotEntryPin = findByU_D(userId, depotEntryId);

		return remove(depotEntryPin);
	}

	/**
	 * Returns the number of depot entry pins where userId = &#63; and depotEntryId = &#63;.
	 *
	 * @param userId the user ID
	 * @param depotEntryId the depot entry ID
	 * @return the number of matching depot entry pins
	 */
	@Override
	public int countByU_D(long userId, long depotEntryId) {
		return _uniquePersistenceFinderByU_D.count(
			finderCache, new Object[] {userId, depotEntryId});
	}

	public DepotEntryPinPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DepotEntryPin.class);

		setModelImplClass(DepotEntryPinImpl.class);
		setModelPKClass(long.class);

		setTable(DepotEntryPinTable.INSTANCE);
	}

	/**
	 * Creates a new depot entry pin with the primary key. Does not add the depot entry pin to the database.
	 *
	 * @param depotEntryPinId the primary key for the new depot entry pin
	 * @return the new depot entry pin
	 */
	@Override
	public DepotEntryPin create(long depotEntryPinId) {
		DepotEntryPin depotEntryPin = new DepotEntryPinImpl();

		depotEntryPin.setNew(true);
		depotEntryPin.setPrimaryKey(depotEntryPinId);

		String uuid = PortalUUIDUtil.generate();

		depotEntryPin.setUuid(uuid);

		depotEntryPin.setCompanyId(CompanyThreadLocal.getCompanyId());

		return depotEntryPin;
	}

	/**
	 * Removes the depot entry pin with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param depotEntryPinId the primary key of the depot entry pin
	 * @return the depot entry pin that was removed
	 * @throws NoSuchEntryPinException if a depot entry pin with the primary key could not be found
	 */
	@Override
	public DepotEntryPin remove(long depotEntryPinId)
		throws NoSuchEntryPinException {

		return remove((Serializable)depotEntryPinId);
	}

	@Override
	protected DepotEntryPin removeImpl(DepotEntryPin depotEntryPin) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(depotEntryPin)) {
				depotEntryPin = (DepotEntryPin)session.get(
					DepotEntryPinImpl.class, depotEntryPin.getPrimaryKeyObj());
			}

			if ((depotEntryPin != null) &&
				ctPersistenceHelper.isRemove(depotEntryPin)) {

				session.delete(depotEntryPin);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (depotEntryPin != null) {
			clearCache(depotEntryPin);
		}

		return depotEntryPin;
	}

	@Override
	public DepotEntryPin updateImpl(DepotEntryPin depotEntryPin) {
		boolean isNew = depotEntryPin.isNew();

		if (!(depotEntryPin instanceof DepotEntryPinModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(depotEntryPin.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					depotEntryPin);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in depotEntryPin proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DepotEntryPin implementation " +
					depotEntryPin.getClass());
		}

		DepotEntryPinModelImpl depotEntryPinModelImpl =
			(DepotEntryPinModelImpl)depotEntryPin;

		if (Validator.isNull(depotEntryPin.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			depotEntryPin.setUuid(uuid);
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(depotEntryPin)) {
				if (!isNew) {
					session.evict(
						DepotEntryPinImpl.class,
						depotEntryPin.getPrimaryKeyObj());
				}

				session.save(depotEntryPin);
			}
			else {
				depotEntryPin = (DepotEntryPin)session.merge(depotEntryPin);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(depotEntryPin, false);

		if (isNew) {
			depotEntryPin.setNew(false);
		}

		depotEntryPin.resetOriginalValues();

		return depotEntryPin;
	}

	/**
	 * Returns the depot entry pin with the primary key or throws a <code>NoSuchEntryPinException</code> if it could not be found.
	 *
	 * @param depotEntryPinId the primary key of the depot entry pin
	 * @return the depot entry pin
	 * @throws NoSuchEntryPinException if a depot entry pin with the primary key could not be found
	 */
	@Override
	public DepotEntryPin findByPrimaryKey(long depotEntryPinId)
		throws NoSuchEntryPinException {

		return findByPrimaryKey((Serializable)depotEntryPinId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the depot entry pin with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param depotEntryPinId the primary key of the depot entry pin
	 * @return the depot entry pin, or <code>null</code> if a depot entry pin with the primary key could not be found
	 */
	@Override
	public DepotEntryPin fetchByPrimaryKey(long depotEntryPinId) {
		return fetchByPrimaryKey((Serializable)depotEntryPinId);
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
		return "depotEntryPinId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DEPOTENTRYPIN;
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
		return DepotEntryPinModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DepotEntryPin";
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
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctMergeColumnNames.add("depotEntryId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("depotEntryPinId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"userId", "depotEntryId"});
	}

	/**
	 * Initializes the depot entry pin persistence.
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
			_SQL_SELECT_DEPOTENTRYPIN_WHERE, _SQL_COUNT_DEPOTENTRYPIN_WHERE,
			DepotEntryPinModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"depotEntryPin.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, DepotEntryPin::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(DepotEntryPin::getUuid),
				DepotEntryPin::getGroupId),
			_SQL_SELECT_DEPOTENTRYPIN_WHERE, "",
			new FinderColumn<>(
				"depotEntryPin.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, DepotEntryPin::getUuid),
			new FinderColumn<>(
				"depotEntryPin.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, DepotEntryPin::getGroupId));

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
				_SQL_SELECT_DEPOTENTRYPIN_WHERE, _SQL_COUNT_DEPOTENTRYPIN_WHERE,
				DepotEntryPinModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"depotEntryPin.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, DepotEntryPin::getUuid),
				new FinderColumn<>(
					"depotEntryPin.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, DepotEntryPin::getCompanyId));

		_collectionPersistenceFinderByUserId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, false),
				_SQL_SELECT_DEPOTENTRYPIN_WHERE, _SQL_COUNT_DEPOTENTRYPIN_WHERE,
				DepotEntryPinModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"depotEntryPin.", "userId", FinderColumn.Type.LONG, "=",
					true, true, DepotEntryPin::getUserId));

		_collectionPersistenceFinderByDepotEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByDepotEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"depotEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByDepotEntryId", new String[] {Long.class.getName()},
					new String[] {"depotEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByDepotEntryId", new String[] {Long.class.getName()},
					new String[] {"depotEntryId"}, false),
				_SQL_SELECT_DEPOTENTRYPIN_WHERE, _SQL_COUNT_DEPOTENTRYPIN_WHERE,
				DepotEntryPinModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"depotEntryPin.", "depotEntryId", FinderColumn.Type.LONG,
					"=", true, true, DepotEntryPin::getDepotEntryId));

		_uniquePersistenceFinderByU_D = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU_D",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId", "depotEntryId"}, 0, 0, false,
				DepotEntryPin::getUserId, DepotEntryPin::getDepotEntryId),
			_SQL_SELECT_DEPOTENTRYPIN_WHERE, "",
			new FinderColumn<>(
				"depotEntryPin.", "userId", FinderColumn.Type.LONG, "=", true,
				true, DepotEntryPin::getUserId),
			new FinderColumn<>(
				"depotEntryPin.", "depotEntryId", FinderColumn.Type.LONG, "=",
				true, true, DepotEntryPin::getDepotEntryId));

		DepotEntryPinUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DepotEntryPinUtil.setPersistence(null);

		entityCache.removeCache(DepotEntryPinImpl.class.getName());
	}

	@Override
	@Reference(
		target = DepotPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DepotPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DepotPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		DepotEntryPinModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DEPOTENTRYPIN =
		"SELECT depotEntryPin FROM DepotEntryPin depotEntryPin";

	private static final String _SQL_SELECT_DEPOTENTRYPIN_WHERE =
		"SELECT depotEntryPin FROM DepotEntryPin depotEntryPin WHERE ";

	private static final String _SQL_COUNT_DEPOTENTRYPIN_WHERE =
		"SELECT COUNT(depotEntryPin) FROM DepotEntryPin depotEntryPin WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-381652188