/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.service.persistence.impl;

import com.liferay.data.engine.exception.NoSuchDataListViewException;
import com.liferay.data.engine.model.DEDataListView;
import com.liferay.data.engine.model.DEDataListViewTable;
import com.liferay.data.engine.model.impl.DEDataListViewImpl;
import com.liferay.data.engine.model.impl.DEDataListViewModelImpl;
import com.liferay.data.engine.service.persistence.DEDataListViewPersistence;
import com.liferay.data.engine.service.persistence.DEDataListViewUtil;
import com.liferay.data.engine.service.persistence.impl.constants.DEPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
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
 * The persistence implementation for the de data list view service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DEDataListViewPersistence.class)
public class DEDataListViewPersistenceImpl
	extends BasePersistenceImpl<DEDataListView, NoSuchDataListViewException>
	implements DEDataListViewPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DEDataListViewUtil</code> to access the de data list view persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DEDataListViewImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DEDataListView, NoSuchDataListViewException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the de data list views where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DEDataListViewModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of de data list views
	 * @param end the upper bound of the range of de data list views (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching de data list views
	 */
	@Override
	public List<DEDataListView> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DEDataListView> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first de data list view in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data list view
	 * @throws NoSuchDataListViewException if a matching de data list view could not be found
	 */
	@Override
	public DEDataListView findByUuid_First(
			String uuid, OrderByComparator<DEDataListView> orderByComparator)
		throws NoSuchDataListViewException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first de data list view in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data list view, or <code>null</code> if a matching de data list view could not be found
	 */
	@Override
	public DEDataListView fetchByUuid_First(
		String uuid, OrderByComparator<DEDataListView> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the de data list views where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of de data list views where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching de data list views
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<DEDataListView, NoSuchDataListViewException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the de data list view where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchDataListViewException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching de data list view
	 * @throws NoSuchDataListViewException if a matching de data list view could not be found
	 */
	@Override
	public DEDataListView findByUUID_G(String uuid, long groupId)
		throws NoSuchDataListViewException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the de data list view where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching de data list view, or <code>null</code> if a matching de data list view could not be found
	 */
	@Override
	public DEDataListView fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the de data list view where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the de data list view that was removed
	 */
	@Override
	public DEDataListView removeByUUID_G(String uuid, long groupId)
		throws NoSuchDataListViewException {

		DEDataListView deDataListView = findByUUID_G(uuid, groupId);

		return remove(deDataListView);
	}

	/**
	 * Returns the number of de data list views where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching de data list views
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<DEDataListView, NoSuchDataListViewException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the de data list views where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DEDataListViewModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of de data list views
	 * @param end the upper bound of the range of de data list views (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching de data list views
	 */
	@Override
	public List<DEDataListView> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DEDataListView> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first de data list view in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data list view
	 * @throws NoSuchDataListViewException if a matching de data list view could not be found
	 */
	@Override
	public DEDataListView findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DEDataListView> orderByComparator)
		throws NoSuchDataListViewException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first de data list view in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data list view, or <code>null</code> if a matching de data list view could not be found
	 */
	@Override
	public DEDataListView fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DEDataListView> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the de data list views where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of de data list views where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching de data list views
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<DEDataListView, NoSuchDataListViewException>
			_collectionPersistenceFinderByDDMStructureId;

	/**
	 * Returns an ordered range of all the de data list views where ddmStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DEDataListViewModelImpl</code>.
	 * </p>
	 *
	 * @param ddmStructureId the ddm structure ID
	 * @param start the lower bound of the range of de data list views
	 * @param end the upper bound of the range of de data list views (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching de data list views
	 */
	@Override
	public List<DEDataListView> findByDDMStructureId(
		long ddmStructureId, int start, int end,
		OrderByComparator<DEDataListView> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByDDMStructureId.find(
			finderCache, new Object[] {ddmStructureId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first de data list view in the ordered set where ddmStructureId = &#63;.
	 *
	 * @param ddmStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data list view
	 * @throws NoSuchDataListViewException if a matching de data list view could not be found
	 */
	@Override
	public DEDataListView findByDDMStructureId_First(
			long ddmStructureId,
			OrderByComparator<DEDataListView> orderByComparator)
		throws NoSuchDataListViewException {

		return _collectionPersistenceFinderByDDMStructureId.findFirst(
			finderCache, new Object[] {ddmStructureId}, orderByComparator);
	}

	/**
	 * Returns the first de data list view in the ordered set where ddmStructureId = &#63;.
	 *
	 * @param ddmStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data list view, or <code>null</code> if a matching de data list view could not be found
	 */
	@Override
	public DEDataListView fetchByDDMStructureId_First(
		long ddmStructureId,
		OrderByComparator<DEDataListView> orderByComparator) {

		return _collectionPersistenceFinderByDDMStructureId.fetchFirst(
			finderCache, new Object[] {ddmStructureId}, orderByComparator);
	}

	/**
	 * Removes all the de data list views where ddmStructureId = &#63; from the database.
	 *
	 * @param ddmStructureId the ddm structure ID
	 */
	@Override
	public void removeByDDMStructureId(long ddmStructureId) {
		_collectionPersistenceFinderByDDMStructureId.remove(
			finderCache, new Object[] {ddmStructureId});
	}

	/**
	 * Returns the number of de data list views where ddmStructureId = &#63;.
	 *
	 * @param ddmStructureId the ddm structure ID
	 * @return the number of matching de data list views
	 */
	@Override
	public int countByDDMStructureId(long ddmStructureId) {
		return _collectionPersistenceFinderByDDMStructureId.count(
			finderCache, new Object[] {ddmStructureId});
	}

	private CollectionPersistenceFinder
		<DEDataListView, NoSuchDataListViewException>
			_collectionPersistenceFinderByG_C_DDMSI;

	/**
	 * Returns an ordered range of all the de data list views where groupId = &#63; and companyId = &#63; and ddmStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DEDataListViewModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param ddmStructureId the ddm structure ID
	 * @param start the lower bound of the range of de data list views
	 * @param end the upper bound of the range of de data list views (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching de data list views
	 */
	@Override
	public List<DEDataListView> findByG_C_DDMSI(
		long groupId, long companyId, long ddmStructureId, int start, int end,
		OrderByComparator<DEDataListView> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_DDMSI.find(
			finderCache, new Object[] {groupId, companyId, ddmStructureId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first de data list view in the ordered set where groupId = &#63; and companyId = &#63; and ddmStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param ddmStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data list view
	 * @throws NoSuchDataListViewException if a matching de data list view could not be found
	 */
	@Override
	public DEDataListView findByG_C_DDMSI_First(
			long groupId, long companyId, long ddmStructureId,
			OrderByComparator<DEDataListView> orderByComparator)
		throws NoSuchDataListViewException {

		return _collectionPersistenceFinderByG_C_DDMSI.findFirst(
			finderCache, new Object[] {groupId, companyId, ddmStructureId},
			orderByComparator);
	}

	/**
	 * Returns the first de data list view in the ordered set where groupId = &#63; and companyId = &#63; and ddmStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param ddmStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data list view, or <code>null</code> if a matching de data list view could not be found
	 */
	@Override
	public DEDataListView fetchByG_C_DDMSI_First(
		long groupId, long companyId, long ddmStructureId,
		OrderByComparator<DEDataListView> orderByComparator) {

		return _collectionPersistenceFinderByG_C_DDMSI.fetchFirst(
			finderCache, new Object[] {groupId, companyId, ddmStructureId},
			orderByComparator);
	}

	/**
	 * Removes all the de data list views where groupId = &#63; and companyId = &#63; and ddmStructureId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param ddmStructureId the ddm structure ID
	 */
	@Override
	public void removeByG_C_DDMSI(
		long groupId, long companyId, long ddmStructureId) {

		_collectionPersistenceFinderByG_C_DDMSI.remove(
			finderCache, new Object[] {groupId, companyId, ddmStructureId});
	}

	/**
	 * Returns the number of de data list views where groupId = &#63; and companyId = &#63; and ddmStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param ddmStructureId the ddm structure ID
	 * @return the number of matching de data list views
	 */
	@Override
	public int countByG_C_DDMSI(
		long groupId, long companyId, long ddmStructureId) {

		return _collectionPersistenceFinderByG_C_DDMSI.count(
			finderCache, new Object[] {groupId, companyId, ddmStructureId});
	}

	public DEDataListViewPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DEDataListView.class);

		setModelImplClass(DEDataListViewImpl.class);
		setModelPKClass(long.class);

		setTable(DEDataListViewTable.INSTANCE);
	}

	/**
	 * Creates a new de data list view with the primary key. Does not add the de data list view to the database.
	 *
	 * @param deDataListViewId the primary key for the new de data list view
	 * @return the new de data list view
	 */
	@Override
	public DEDataListView create(long deDataListViewId) {
		DEDataListView deDataListView = new DEDataListViewImpl();

		deDataListView.setNew(true);
		deDataListView.setPrimaryKey(deDataListViewId);

		String uuid = PortalUUIDUtil.generate();

		deDataListView.setUuid(uuid);

		deDataListView.setCompanyId(CompanyThreadLocal.getCompanyId());

		return deDataListView;
	}

	/**
	 * Removes the de data list view with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param deDataListViewId the primary key of the de data list view
	 * @return the de data list view that was removed
	 * @throws NoSuchDataListViewException if a de data list view with the primary key could not be found
	 */
	@Override
	public DEDataListView remove(long deDataListViewId)
		throws NoSuchDataListViewException {

		return remove((Serializable)deDataListViewId);
	}

	@Override
	protected DEDataListView removeImpl(DEDataListView deDataListView) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(deDataListView)) {
				deDataListView = (DEDataListView)session.get(
					DEDataListViewImpl.class,
					deDataListView.getPrimaryKeyObj());
			}

			if ((deDataListView != null) &&
				ctPersistenceHelper.isRemove(deDataListView)) {

				session.delete(deDataListView);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (deDataListView != null) {
			clearCache(deDataListView);
		}

		return deDataListView;
	}

	@Override
	public DEDataListView updateImpl(DEDataListView deDataListView) {
		boolean isNew = deDataListView.isNew();

		if (!(deDataListView instanceof DEDataListViewModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(deDataListView.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					deDataListView);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in deDataListView proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DEDataListView implementation " +
					deDataListView.getClass());
		}

		DEDataListViewModelImpl deDataListViewModelImpl =
			(DEDataListViewModelImpl)deDataListView;

		if (Validator.isNull(deDataListView.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			deDataListView.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (deDataListView.getCreateDate() == null)) {
			if (serviceContext == null) {
				deDataListView.setCreateDate(date);
			}
			else {
				deDataListView.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!deDataListViewModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				deDataListView.setModifiedDate(date);
			}
			else {
				deDataListView.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(deDataListView)) {
				if (!isNew) {
					session.evict(
						DEDataListViewImpl.class,
						deDataListView.getPrimaryKeyObj());
				}

				session.save(deDataListView);
			}
			else {
				deDataListView = (DEDataListView)session.merge(deDataListView);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(deDataListView, false);

		if (isNew) {
			deDataListView.setNew(false);
		}

		deDataListView.resetOriginalValues();

		return deDataListView;
	}

	/**
	 * Returns the de data list view with the primary key or throws a <code>NoSuchDataListViewException</code> if it could not be found.
	 *
	 * @param deDataListViewId the primary key of the de data list view
	 * @return the de data list view
	 * @throws NoSuchDataListViewException if a de data list view with the primary key could not be found
	 */
	@Override
	public DEDataListView findByPrimaryKey(long deDataListViewId)
		throws NoSuchDataListViewException {

		return findByPrimaryKey((Serializable)deDataListViewId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the de data list view with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param deDataListViewId the primary key of the de data list view
	 * @return the de data list view, or <code>null</code> if a de data list view with the primary key could not be found
	 */
	@Override
	public DEDataListView fetchByPrimaryKey(long deDataListViewId) {
		return fetchByPrimaryKey((Serializable)deDataListViewId);
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
		return "deDataListViewId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DEDATALISTVIEW;
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
		return DEDataListViewModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DEDataListView";
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
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("appliedFilters");
		ctMergeColumnNames.add("ddmStructureId");
		ctMergeColumnNames.add("fieldNames");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("sortField");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("deDataListViewId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});
	}

	/**
	 * Initializes the de data list view persistence.
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
			_SQL_SELECT_DEDATALISTVIEW_WHERE, _SQL_COUNT_DEDATALISTVIEW_WHERE,
			DEDataListViewModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"deDataListView.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, DEDataListView::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(DEDataListView::getUuid),
				DEDataListView::getGroupId),
			_SQL_SELECT_DEDATALISTVIEW_WHERE, "",
			new FinderColumn<>(
				"deDataListView.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, DEDataListView::getUuid),
			new FinderColumn<>(
				"deDataListView.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, DEDataListView::getGroupId));

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
				_SQL_SELECT_DEDATALISTVIEW_WHERE,
				_SQL_COUNT_DEDATALISTVIEW_WHERE,
				DEDataListViewModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"deDataListView.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					DEDataListView::getUuid),
				new FinderColumn<>(
					"deDataListView.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, DEDataListView::getCompanyId));

		_collectionPersistenceFinderByDDMStructureId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByDDMStructureId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"ddmStructureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByDDMStructureId", new String[] {Long.class.getName()},
					new String[] {"ddmStructureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByDDMStructureId",
					new String[] {Long.class.getName()},
					new String[] {"ddmStructureId"}, false),
				_SQL_SELECT_DEDATALISTVIEW_WHERE,
				_SQL_COUNT_DEDATALISTVIEW_WHERE,
				DEDataListViewModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"deDataListView.", "ddmStructureId", FinderColumn.Type.LONG,
					"=", true, true, DEDataListView::getDdmStructureId));

		_collectionPersistenceFinderByG_C_DDMSI =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_DDMSI",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "companyId", "ddmStructureId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_C_DDMSI",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "companyId", "ddmStructureId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_C_DDMSI",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "companyId", "ddmStructureId"},
					false),
				_SQL_SELECT_DEDATALISTVIEW_WHERE,
				_SQL_COUNT_DEDATALISTVIEW_WHERE,
				DEDataListViewModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"deDataListView.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, DEDataListView::getGroupId),
				new FinderColumn<>(
					"deDataListView.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, DEDataListView::getCompanyId),
				new FinderColumn<>(
					"deDataListView.", "ddmStructureId", FinderColumn.Type.LONG,
					"=", true, true, DEDataListView::getDdmStructureId));

		DEDataListViewUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DEDataListViewUtil.setPersistence(null);

		entityCache.removeCache(DEDataListViewImpl.class.getName());
	}

	@Override
	@Reference(
		target = DEPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DEPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DEPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		DEDataListViewModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DEDATALISTVIEW =
		"SELECT deDataListView FROM DEDataListView deDataListView";

	private static final String _SQL_SELECT_DEDATALISTVIEW_WHERE =
		"SELECT deDataListView FROM DEDataListView deDataListView WHERE ";

	private static final String _SQL_COUNT_DEDATALISTVIEW_WHERE =
		"SELECT COUNT(deDataListView) FROM DEDataListView deDataListView WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1630789316