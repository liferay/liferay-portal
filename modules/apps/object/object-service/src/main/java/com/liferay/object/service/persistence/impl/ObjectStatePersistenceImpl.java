/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectStateException;
import com.liferay.object.model.ObjectState;
import com.liferay.object.model.ObjectStateTable;
import com.liferay.object.model.impl.ObjectStateImpl;
import com.liferay.object.model.impl.ObjectStateModelImpl;
import com.liferay.object.service.persistence.ObjectStatePersistence;
import com.liferay.object.service.persistence.ObjectStateUtil;
import com.liferay.object.service.persistence.impl.constants.ObjectPersistenceConstants;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the object state service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectStatePersistence.class)
public class ObjectStatePersistenceImpl
	extends BasePersistenceImpl<ObjectState, NoSuchObjectStateException>
	implements ObjectStatePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectStateUtil</code> to access the object state persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectStateImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<ObjectState, NoSuchObjectStateException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object states where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectStateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object states
	 * @param end the upper bound of the range of object states (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object states
	 */
	@Override
	public List<ObjectState> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectState> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object state in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state
	 * @throws NoSuchObjectStateException if a matching object state could not be found
	 */
	@Override
	public ObjectState findByUuid_First(
			String uuid, OrderByComparator<ObjectState> orderByComparator)
		throws NoSuchObjectStateException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object state in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state, or <code>null</code> if a matching object state could not be found
	 */
	@Override
	public ObjectState fetchByUuid_First(
		String uuid, OrderByComparator<ObjectState> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object states where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object states where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object states
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder<ObjectState, NoSuchObjectStateException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object states where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectStateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object states
	 * @param end the upper bound of the range of object states (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object states
	 */
	@Override
	public List<ObjectState> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectState> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object state in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state
	 * @throws NoSuchObjectStateException if a matching object state could not be found
	 */
	@Override
	public ObjectState findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectState> orderByComparator)
		throws NoSuchObjectStateException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object state in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state, or <code>null</code> if a matching object state could not be found
	 */
	@Override
	public ObjectState fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectState> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object states where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object states where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object states
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<ObjectState, NoSuchObjectStateException>
		_collectionPersistenceFinderByListTypeEntryId;

	/**
	 * Returns an ordered range of all the object states where listTypeEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectStateModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeEntryId the list type entry ID
	 * @param start the lower bound of the range of object states
	 * @param end the upper bound of the range of object states (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object states
	 */
	@Override
	public List<ObjectState> findByListTypeEntryId(
		long listTypeEntryId, int start, int end,
		OrderByComparator<ObjectState> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByListTypeEntryId.find(
			finderCache, new Object[] {listTypeEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object state in the ordered set where listTypeEntryId = &#63;.
	 *
	 * @param listTypeEntryId the list type entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state
	 * @throws NoSuchObjectStateException if a matching object state could not be found
	 */
	@Override
	public ObjectState findByListTypeEntryId_First(
			long listTypeEntryId,
			OrderByComparator<ObjectState> orderByComparator)
		throws NoSuchObjectStateException {

		return _collectionPersistenceFinderByListTypeEntryId.findFirst(
			finderCache, new Object[] {listTypeEntryId}, orderByComparator);
	}

	/**
	 * Returns the first object state in the ordered set where listTypeEntryId = &#63;.
	 *
	 * @param listTypeEntryId the list type entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state, or <code>null</code> if a matching object state could not be found
	 */
	@Override
	public ObjectState fetchByListTypeEntryId_First(
		long listTypeEntryId,
		OrderByComparator<ObjectState> orderByComparator) {

		return _collectionPersistenceFinderByListTypeEntryId.fetchFirst(
			finderCache, new Object[] {listTypeEntryId}, orderByComparator);
	}

	/**
	 * Removes all the object states where listTypeEntryId = &#63; from the database.
	 *
	 * @param listTypeEntryId the list type entry ID
	 */
	@Override
	public void removeByListTypeEntryId(long listTypeEntryId) {
		_collectionPersistenceFinderByListTypeEntryId.remove(
			finderCache, new Object[] {listTypeEntryId});
	}

	/**
	 * Returns the number of object states where listTypeEntryId = &#63;.
	 *
	 * @param listTypeEntryId the list type entry ID
	 * @return the number of matching object states
	 */
	@Override
	public int countByListTypeEntryId(long listTypeEntryId) {
		return _collectionPersistenceFinderByListTypeEntryId.count(
			finderCache, new Object[] {listTypeEntryId});
	}

	private CollectionPersistenceFinder<ObjectState, NoSuchObjectStateException>
		_collectionPersistenceFinderByObjectStateFlowId;

	/**
	 * Returns an ordered range of all the object states where objectStateFlowId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectStateModelImpl</code>.
	 * </p>
	 *
	 * @param objectStateFlowId the object state flow ID
	 * @param start the lower bound of the range of object states
	 * @param end the upper bound of the range of object states (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object states
	 */
	@Override
	public List<ObjectState> findByObjectStateFlowId(
		long objectStateFlowId, int start, int end,
		OrderByComparator<ObjectState> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectStateFlowId.find(
			finderCache, new Object[] {objectStateFlowId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object state in the ordered set where objectStateFlowId = &#63;.
	 *
	 * @param objectStateFlowId the object state flow ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state
	 * @throws NoSuchObjectStateException if a matching object state could not be found
	 */
	@Override
	public ObjectState findByObjectStateFlowId_First(
			long objectStateFlowId,
			OrderByComparator<ObjectState> orderByComparator)
		throws NoSuchObjectStateException {

		return _collectionPersistenceFinderByObjectStateFlowId.findFirst(
			finderCache, new Object[] {objectStateFlowId}, orderByComparator);
	}

	/**
	 * Returns the first object state in the ordered set where objectStateFlowId = &#63;.
	 *
	 * @param objectStateFlowId the object state flow ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state, or <code>null</code> if a matching object state could not be found
	 */
	@Override
	public ObjectState fetchByObjectStateFlowId_First(
		long objectStateFlowId,
		OrderByComparator<ObjectState> orderByComparator) {

		return _collectionPersistenceFinderByObjectStateFlowId.fetchFirst(
			finderCache, new Object[] {objectStateFlowId}, orderByComparator);
	}

	/**
	 * Removes all the object states where objectStateFlowId = &#63; from the database.
	 *
	 * @param objectStateFlowId the object state flow ID
	 */
	@Override
	public void removeByObjectStateFlowId(long objectStateFlowId) {
		_collectionPersistenceFinderByObjectStateFlowId.remove(
			finderCache, new Object[] {objectStateFlowId});
	}

	/**
	 * Returns the number of object states where objectStateFlowId = &#63;.
	 *
	 * @param objectStateFlowId the object state flow ID
	 * @return the number of matching object states
	 */
	@Override
	public int countByObjectStateFlowId(long objectStateFlowId) {
		return _collectionPersistenceFinderByObjectStateFlowId.count(
			finderCache, new Object[] {objectStateFlowId});
	}

	private UniquePersistenceFinder<ObjectState, NoSuchObjectStateException>
		_uniquePersistenceFinderByLTEI_OSFI;

	/**
	 * Returns the object state where listTypeEntryId = &#63; and objectStateFlowId = &#63; or throws a <code>NoSuchObjectStateException</code> if it could not be found.
	 *
	 * @param listTypeEntryId the list type entry ID
	 * @param objectStateFlowId the object state flow ID
	 * @return the matching object state
	 * @throws NoSuchObjectStateException if a matching object state could not be found
	 */
	@Override
	public ObjectState findByLTEI_OSFI(
			long listTypeEntryId, long objectStateFlowId)
		throws NoSuchObjectStateException {

		return _uniquePersistenceFinderByLTEI_OSFI.find(
			finderCache, new Object[] {listTypeEntryId, objectStateFlowId});
	}

	/**
	 * Returns the object state where listTypeEntryId = &#63; and objectStateFlowId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param listTypeEntryId the list type entry ID
	 * @param objectStateFlowId the object state flow ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object state, or <code>null</code> if a matching object state could not be found
	 */
	@Override
	public ObjectState fetchByLTEI_OSFI(
		long listTypeEntryId, long objectStateFlowId, boolean useFinderCache) {

		return _uniquePersistenceFinderByLTEI_OSFI.fetch(
			finderCache, new Object[] {listTypeEntryId, objectStateFlowId},
			useFinderCache);
	}

	/**
	 * Removes the object state where listTypeEntryId = &#63; and objectStateFlowId = &#63; from the database.
	 *
	 * @param listTypeEntryId the list type entry ID
	 * @param objectStateFlowId the object state flow ID
	 * @return the object state that was removed
	 */
	@Override
	public ObjectState removeByLTEI_OSFI(
			long listTypeEntryId, long objectStateFlowId)
		throws NoSuchObjectStateException {

		ObjectState objectState = findByLTEI_OSFI(
			listTypeEntryId, objectStateFlowId);

		return remove(objectState);
	}

	/**
	 * Returns the number of object states where listTypeEntryId = &#63; and objectStateFlowId = &#63;.
	 *
	 * @param listTypeEntryId the list type entry ID
	 * @param objectStateFlowId the object state flow ID
	 * @return the number of matching object states
	 */
	@Override
	public int countByLTEI_OSFI(long listTypeEntryId, long objectStateFlowId) {
		return _uniquePersistenceFinderByLTEI_OSFI.count(
			finderCache, new Object[] {listTypeEntryId, objectStateFlowId});
	}

	public ObjectStatePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectState.class);

		setModelImplClass(ObjectStateImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectStateTable.INSTANCE);
	}

	/**
	 * Creates a new object state with the primary key. Does not add the object state to the database.
	 *
	 * @param objectStateId the primary key for the new object state
	 * @return the new object state
	 */
	@Override
	public ObjectState create(long objectStateId) {
		ObjectState objectState = new ObjectStateImpl();

		objectState.setNew(true);
		objectState.setPrimaryKey(objectStateId);

		String uuid = PortalUUIDUtil.generate();

		objectState.setUuid(uuid);

		objectState.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectState;
	}

	/**
	 * Removes the object state with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectStateId the primary key of the object state
	 * @return the object state that was removed
	 * @throws NoSuchObjectStateException if a object state with the primary key could not be found
	 */
	@Override
	public ObjectState remove(long objectStateId)
		throws NoSuchObjectStateException {

		return remove((Serializable)objectStateId);
	}

	@Override
	protected ObjectState removeImpl(ObjectState objectState) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectState)) {
				objectState = (ObjectState)session.get(
					ObjectStateImpl.class, objectState.getPrimaryKeyObj());
			}

			if (objectState != null) {
				session.delete(objectState);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectState != null) {
			clearCache(objectState);
		}

		return objectState;
	}

	@Override
	public ObjectState updateImpl(ObjectState objectState) {
		boolean isNew = objectState.isNew();

		if (!(objectState instanceof ObjectStateModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectState.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(objectState);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectState proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectState implementation " +
					objectState.getClass());
		}

		ObjectStateModelImpl objectStateModelImpl =
			(ObjectStateModelImpl)objectState;

		if (Validator.isNull(objectState.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectState.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectState.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectState.setCreateDate(date);
			}
			else {
				objectState.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!objectStateModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectState.setModifiedDate(date);
			}
			else {
				objectState.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectState);
			}
			else {
				objectState = (ObjectState)session.merge(objectState);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectState, false);

		if (isNew) {
			objectState.setNew(false);
		}

		objectState.resetOriginalValues();

		return objectState;
	}

	/**
	 * Returns the object state with the primary key or throws a <code>NoSuchObjectStateException</code> if it could not be found.
	 *
	 * @param objectStateId the primary key of the object state
	 * @return the object state
	 * @throws NoSuchObjectStateException if a object state with the primary key could not be found
	 */
	@Override
	public ObjectState findByPrimaryKey(long objectStateId)
		throws NoSuchObjectStateException {

		return findByPrimaryKey((Serializable)objectStateId);
	}

	/**
	 * Returns the object state with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectStateId the primary key of the object state
	 * @return the object state, or <code>null</code> if a object state with the primary key could not be found
	 */
	@Override
	public ObjectState fetchByPrimaryKey(long objectStateId) {
		return fetchByPrimaryKey((Serializable)objectStateId);
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
		return "objectStateId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTSTATE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectStateModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object state persistence.
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
			_SQL_SELECT_OBJECTSTATE_WHERE, _SQL_COUNT_OBJECTSTATE_WHERE,
			ObjectStateModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"objectState.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, ObjectState::getUuid));

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
				_SQL_SELECT_OBJECTSTATE_WHERE, _SQL_COUNT_OBJECTSTATE_WHERE,
				ObjectStateModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectState.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, ObjectState::getUuid),
				new FinderColumn<>(
					"objectState.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, ObjectState::getCompanyId));

		_collectionPersistenceFinderByListTypeEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByListTypeEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"listTypeEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByListTypeEntryId",
					new String[] {Long.class.getName()},
					new String[] {"listTypeEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByListTypeEntryId",
					new String[] {Long.class.getName()},
					new String[] {"listTypeEntryId"}, false),
				_SQL_SELECT_OBJECTSTATE_WHERE, _SQL_COUNT_OBJECTSTATE_WHERE,
				ObjectStateModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectState.", "listTypeEntryId", FinderColumn.Type.LONG,
					"=", true, true, ObjectState::getListTypeEntryId));

		_collectionPersistenceFinderByObjectStateFlowId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByObjectStateFlowId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectStateFlowId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByObjectStateFlowId",
					new String[] {Long.class.getName()},
					new String[] {"objectStateFlowId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByObjectStateFlowId",
					new String[] {Long.class.getName()},
					new String[] {"objectStateFlowId"}, false),
				_SQL_SELECT_OBJECTSTATE_WHERE, _SQL_COUNT_OBJECTSTATE_WHERE,
				ObjectStateModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectState.", "objectStateFlowId", FinderColumn.Type.LONG,
					"=", true, true, ObjectState::getObjectStateFlowId));

		_uniquePersistenceFinderByLTEI_OSFI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByLTEI_OSFI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"listTypeEntryId", "objectStateFlowId"}, 0, 0,
				false, ObjectState::getListTypeEntryId,
				ObjectState::getObjectStateFlowId),
			_SQL_SELECT_OBJECTSTATE_WHERE, "",
			new FinderColumn<>(
				"objectState.", "listTypeEntryId", FinderColumn.Type.LONG, "=",
				true, true, ObjectState::getListTypeEntryId),
			new FinderColumn<>(
				"objectState.", "objectStateFlowId", FinderColumn.Type.LONG,
				"=", true, true, ObjectState::getObjectStateFlowId));

		ObjectStateUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectStateUtil.setPersistence(null);

		entityCache.removeCache(ObjectStateImpl.class.getName());
	}

	@Override
	@Reference(
		target = ObjectPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = ObjectPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = ObjectPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		ObjectStateModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTSTATE =
		"SELECT objectState FROM ObjectState objectState";

	private static final String _SQL_SELECT_OBJECTSTATE_WHERE =
		"SELECT objectState FROM ObjectState objectState WHERE ";

	private static final String _SQL_COUNT_OBJECTSTATE_WHERE =
		"SELECT COUNT(objectState) FROM ObjectState objectState WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:437200358