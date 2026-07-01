/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectStateTransitionException;
import com.liferay.object.model.ObjectStateTransition;
import com.liferay.object.model.ObjectStateTransitionTable;
import com.liferay.object.model.impl.ObjectStateTransitionImpl;
import com.liferay.object.model.impl.ObjectStateTransitionModelImpl;
import com.liferay.object.service.persistence.ObjectStateTransitionPersistence;
import com.liferay.object.service.persistence.ObjectStateTransitionUtil;
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
 * The persistence implementation for the object state transition service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectStateTransitionPersistence.class)
public class ObjectStateTransitionPersistenceImpl
	extends BasePersistenceImpl
		<ObjectStateTransition, NoSuchObjectStateTransitionException>
	implements ObjectStateTransitionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectStateTransitionUtil</code> to access the object state transition persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectStateTransitionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ObjectStateTransition, NoSuchObjectStateTransitionException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object state transitions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectStateTransitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object state transitions
	 * @param end the upper bound of the range of object state transitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object state transitions
	 */
	@Override
	public List<ObjectStateTransition> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectStateTransition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object state transition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state transition
	 * @throws NoSuchObjectStateTransitionException if a matching object state transition could not be found
	 */
	@Override
	public ObjectStateTransition findByUuid_First(
			String uuid,
			OrderByComparator<ObjectStateTransition> orderByComparator)
		throws NoSuchObjectStateTransitionException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object state transition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state transition, or <code>null</code> if a matching object state transition could not be found
	 */
	@Override
	public ObjectStateTransition fetchByUuid_First(
		String uuid,
		OrderByComparator<ObjectStateTransition> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object state transitions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object state transitions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object state transitions
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ObjectStateTransition, NoSuchObjectStateTransitionException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object state transitions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectStateTransitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object state transitions
	 * @param end the upper bound of the range of object state transitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object state transitions
	 */
	@Override
	public List<ObjectStateTransition> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectStateTransition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object state transition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state transition
	 * @throws NoSuchObjectStateTransitionException if a matching object state transition could not be found
	 */
	@Override
	public ObjectStateTransition findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectStateTransition> orderByComparator)
		throws NoSuchObjectStateTransitionException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object state transition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state transition, or <code>null</code> if a matching object state transition could not be found
	 */
	@Override
	public ObjectStateTransition fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectStateTransition> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object state transitions where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object state transitions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object state transitions
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ObjectStateTransition, NoSuchObjectStateTransitionException>
			_collectionPersistenceFinderByObjectStateFlowId;

	/**
	 * Returns an ordered range of all the object state transitions where objectStateFlowId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectStateTransitionModelImpl</code>.
	 * </p>
	 *
	 * @param objectStateFlowId the object state flow ID
	 * @param start the lower bound of the range of object state transitions
	 * @param end the upper bound of the range of object state transitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object state transitions
	 */
	@Override
	public List<ObjectStateTransition> findByObjectStateFlowId(
		long objectStateFlowId, int start, int end,
		OrderByComparator<ObjectStateTransition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectStateFlowId.find(
			finderCache, new Object[] {objectStateFlowId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object state transition in the ordered set where objectStateFlowId = &#63;.
	 *
	 * @param objectStateFlowId the object state flow ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state transition
	 * @throws NoSuchObjectStateTransitionException if a matching object state transition could not be found
	 */
	@Override
	public ObjectStateTransition findByObjectStateFlowId_First(
			long objectStateFlowId,
			OrderByComparator<ObjectStateTransition> orderByComparator)
		throws NoSuchObjectStateTransitionException {

		return _collectionPersistenceFinderByObjectStateFlowId.findFirst(
			finderCache, new Object[] {objectStateFlowId}, orderByComparator);
	}

	/**
	 * Returns the first object state transition in the ordered set where objectStateFlowId = &#63;.
	 *
	 * @param objectStateFlowId the object state flow ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state transition, or <code>null</code> if a matching object state transition could not be found
	 */
	@Override
	public ObjectStateTransition fetchByObjectStateFlowId_First(
		long objectStateFlowId,
		OrderByComparator<ObjectStateTransition> orderByComparator) {

		return _collectionPersistenceFinderByObjectStateFlowId.fetchFirst(
			finderCache, new Object[] {objectStateFlowId}, orderByComparator);
	}

	/**
	 * Removes all the object state transitions where objectStateFlowId = &#63; from the database.
	 *
	 * @param objectStateFlowId the object state flow ID
	 */
	@Override
	public void removeByObjectStateFlowId(long objectStateFlowId) {
		_collectionPersistenceFinderByObjectStateFlowId.remove(
			finderCache, new Object[] {objectStateFlowId});
	}

	/**
	 * Returns the number of object state transitions where objectStateFlowId = &#63;.
	 *
	 * @param objectStateFlowId the object state flow ID
	 * @return the number of matching object state transitions
	 */
	@Override
	public int countByObjectStateFlowId(long objectStateFlowId) {
		return _collectionPersistenceFinderByObjectStateFlowId.count(
			finderCache, new Object[] {objectStateFlowId});
	}

	private CollectionPersistenceFinder
		<ObjectStateTransition, NoSuchObjectStateTransitionException>
			_collectionPersistenceFinderBySourceObjectStateId;

	/**
	 * Returns an ordered range of all the object state transitions where sourceObjectStateId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectStateTransitionModelImpl</code>.
	 * </p>
	 *
	 * @param sourceObjectStateId the source object state ID
	 * @param start the lower bound of the range of object state transitions
	 * @param end the upper bound of the range of object state transitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object state transitions
	 */
	@Override
	public List<ObjectStateTransition> findBySourceObjectStateId(
		long sourceObjectStateId, int start, int end,
		OrderByComparator<ObjectStateTransition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySourceObjectStateId.find(
			finderCache, new Object[] {sourceObjectStateId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object state transition in the ordered set where sourceObjectStateId = &#63;.
	 *
	 * @param sourceObjectStateId the source object state ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state transition
	 * @throws NoSuchObjectStateTransitionException if a matching object state transition could not be found
	 */
	@Override
	public ObjectStateTransition findBySourceObjectStateId_First(
			long sourceObjectStateId,
			OrderByComparator<ObjectStateTransition> orderByComparator)
		throws NoSuchObjectStateTransitionException {

		return _collectionPersistenceFinderBySourceObjectStateId.findFirst(
			finderCache, new Object[] {sourceObjectStateId}, orderByComparator);
	}

	/**
	 * Returns the first object state transition in the ordered set where sourceObjectStateId = &#63;.
	 *
	 * @param sourceObjectStateId the source object state ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state transition, or <code>null</code> if a matching object state transition could not be found
	 */
	@Override
	public ObjectStateTransition fetchBySourceObjectStateId_First(
		long sourceObjectStateId,
		OrderByComparator<ObjectStateTransition> orderByComparator) {

		return _collectionPersistenceFinderBySourceObjectStateId.fetchFirst(
			finderCache, new Object[] {sourceObjectStateId}, orderByComparator);
	}

	/**
	 * Removes all the object state transitions where sourceObjectStateId = &#63; from the database.
	 *
	 * @param sourceObjectStateId the source object state ID
	 */
	@Override
	public void removeBySourceObjectStateId(long sourceObjectStateId) {
		_collectionPersistenceFinderBySourceObjectStateId.remove(
			finderCache, new Object[] {sourceObjectStateId});
	}

	/**
	 * Returns the number of object state transitions where sourceObjectStateId = &#63;.
	 *
	 * @param sourceObjectStateId the source object state ID
	 * @return the number of matching object state transitions
	 */
	@Override
	public int countBySourceObjectStateId(long sourceObjectStateId) {
		return _collectionPersistenceFinderBySourceObjectStateId.count(
			finderCache, new Object[] {sourceObjectStateId});
	}

	private CollectionPersistenceFinder
		<ObjectStateTransition, NoSuchObjectStateTransitionException>
			_collectionPersistenceFinderByTargetObjectStateId;

	/**
	 * Returns an ordered range of all the object state transitions where targetObjectStateId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectStateTransitionModelImpl</code>.
	 * </p>
	 *
	 * @param targetObjectStateId the target object state ID
	 * @param start the lower bound of the range of object state transitions
	 * @param end the upper bound of the range of object state transitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object state transitions
	 */
	@Override
	public List<ObjectStateTransition> findByTargetObjectStateId(
		long targetObjectStateId, int start, int end,
		OrderByComparator<ObjectStateTransition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByTargetObjectStateId.find(
			finderCache, new Object[] {targetObjectStateId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object state transition in the ordered set where targetObjectStateId = &#63;.
	 *
	 * @param targetObjectStateId the target object state ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state transition
	 * @throws NoSuchObjectStateTransitionException if a matching object state transition could not be found
	 */
	@Override
	public ObjectStateTransition findByTargetObjectStateId_First(
			long targetObjectStateId,
			OrderByComparator<ObjectStateTransition> orderByComparator)
		throws NoSuchObjectStateTransitionException {

		return _collectionPersistenceFinderByTargetObjectStateId.findFirst(
			finderCache, new Object[] {targetObjectStateId}, orderByComparator);
	}

	/**
	 * Returns the first object state transition in the ordered set where targetObjectStateId = &#63;.
	 *
	 * @param targetObjectStateId the target object state ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state transition, or <code>null</code> if a matching object state transition could not be found
	 */
	@Override
	public ObjectStateTransition fetchByTargetObjectStateId_First(
		long targetObjectStateId,
		OrderByComparator<ObjectStateTransition> orderByComparator) {

		return _collectionPersistenceFinderByTargetObjectStateId.fetchFirst(
			finderCache, new Object[] {targetObjectStateId}, orderByComparator);
	}

	/**
	 * Removes all the object state transitions where targetObjectStateId = &#63; from the database.
	 *
	 * @param targetObjectStateId the target object state ID
	 */
	@Override
	public void removeByTargetObjectStateId(long targetObjectStateId) {
		_collectionPersistenceFinderByTargetObjectStateId.remove(
			finderCache, new Object[] {targetObjectStateId});
	}

	/**
	 * Returns the number of object state transitions where targetObjectStateId = &#63;.
	 *
	 * @param targetObjectStateId the target object state ID
	 * @return the number of matching object state transitions
	 */
	@Override
	public int countByTargetObjectStateId(long targetObjectStateId) {
		return _collectionPersistenceFinderByTargetObjectStateId.count(
			finderCache, new Object[] {targetObjectStateId});
	}

	public ObjectStateTransitionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectStateTransition.class);

		setModelImplClass(ObjectStateTransitionImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectStateTransitionTable.INSTANCE);
	}

	/**
	 * Creates a new object state transition with the primary key. Does not add the object state transition to the database.
	 *
	 * @param objectStateTransitionId the primary key for the new object state transition
	 * @return the new object state transition
	 */
	@Override
	public ObjectStateTransition create(long objectStateTransitionId) {
		ObjectStateTransition objectStateTransition =
			new ObjectStateTransitionImpl();

		objectStateTransition.setNew(true);
		objectStateTransition.setPrimaryKey(objectStateTransitionId);

		String uuid = PortalUUIDUtil.generate();

		objectStateTransition.setUuid(uuid);

		objectStateTransition.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectStateTransition;
	}

	/**
	 * Removes the object state transition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectStateTransitionId the primary key of the object state transition
	 * @return the object state transition that was removed
	 * @throws NoSuchObjectStateTransitionException if a object state transition with the primary key could not be found
	 */
	@Override
	public ObjectStateTransition remove(long objectStateTransitionId)
		throws NoSuchObjectStateTransitionException {

		return remove((Serializable)objectStateTransitionId);
	}

	@Override
	protected ObjectStateTransition removeImpl(
		ObjectStateTransition objectStateTransition) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectStateTransition)) {
				objectStateTransition = (ObjectStateTransition)session.get(
					ObjectStateTransitionImpl.class,
					objectStateTransition.getPrimaryKeyObj());
			}

			if (objectStateTransition != null) {
				session.delete(objectStateTransition);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectStateTransition != null) {
			clearCache(objectStateTransition);
		}

		return objectStateTransition;
	}

	@Override
	public ObjectStateTransition updateImpl(
		ObjectStateTransition objectStateTransition) {

		boolean isNew = objectStateTransition.isNew();

		if (!(objectStateTransition instanceof
				ObjectStateTransitionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectStateTransition.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectStateTransition);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectStateTransition proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectStateTransition implementation " +
					objectStateTransition.getClass());
		}

		ObjectStateTransitionModelImpl objectStateTransitionModelImpl =
			(ObjectStateTransitionModelImpl)objectStateTransition;

		if (Validator.isNull(objectStateTransition.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectStateTransition.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectStateTransition.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectStateTransition.setCreateDate(date);
			}
			else {
				objectStateTransition.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectStateTransitionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectStateTransition.setModifiedDate(date);
			}
			else {
				objectStateTransition.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectStateTransition);
			}
			else {
				objectStateTransition = (ObjectStateTransition)session.merge(
					objectStateTransition);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectStateTransition, false);

		if (isNew) {
			objectStateTransition.setNew(false);
		}

		objectStateTransition.resetOriginalValues();

		return objectStateTransition;
	}

	/**
	 * Returns the object state transition with the primary key or throws a <code>NoSuchObjectStateTransitionException</code> if it could not be found.
	 *
	 * @param objectStateTransitionId the primary key of the object state transition
	 * @return the object state transition
	 * @throws NoSuchObjectStateTransitionException if a object state transition with the primary key could not be found
	 */
	@Override
	public ObjectStateTransition findByPrimaryKey(long objectStateTransitionId)
		throws NoSuchObjectStateTransitionException {

		return findByPrimaryKey((Serializable)objectStateTransitionId);
	}

	/**
	 * Returns the object state transition with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectStateTransitionId the primary key of the object state transition
	 * @return the object state transition, or <code>null</code> if a object state transition with the primary key could not be found
	 */
	@Override
	public ObjectStateTransition fetchByPrimaryKey(
		long objectStateTransitionId) {

		return fetchByPrimaryKey((Serializable)objectStateTransitionId);
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
		return "objectStateTransitionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTSTATETRANSITION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectStateTransitionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object state transition persistence.
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
			_SQL_SELECT_OBJECTSTATETRANSITION_WHERE,
			_SQL_COUNT_OBJECTSTATETRANSITION_WHERE,
			ObjectStateTransitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"objectStateTransition.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectStateTransition::getUuid));

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
				_SQL_SELECT_OBJECTSTATETRANSITION_WHERE,
				_SQL_COUNT_OBJECTSTATETRANSITION_WHERE,
				ObjectStateTransitionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"objectStateTransition.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectStateTransition::getUuid),
				new FinderColumn<>(
					"objectStateTransition.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectStateTransition::getCompanyId));

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
				_SQL_SELECT_OBJECTSTATETRANSITION_WHERE,
				_SQL_COUNT_OBJECTSTATETRANSITION_WHERE,
				ObjectStateTransitionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"objectStateTransition.", "objectStateFlowId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectStateTransition::getObjectStateFlowId));

		_collectionPersistenceFinderBySourceObjectStateId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findBySourceObjectStateId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"sourceObjectStateId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findBySourceObjectStateId",
					new String[] {Long.class.getName()},
					new String[] {"sourceObjectStateId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countBySourceObjectStateId",
					new String[] {Long.class.getName()},
					new String[] {"sourceObjectStateId"}, false),
				_SQL_SELECT_OBJECTSTATETRANSITION_WHERE,
				_SQL_COUNT_OBJECTSTATETRANSITION_WHERE,
				ObjectStateTransitionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"objectStateTransition.", "sourceObjectStateId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectStateTransition::getSourceObjectStateId));

		_collectionPersistenceFinderByTargetObjectStateId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByTargetObjectStateId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"targetObjectStateId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByTargetObjectStateId",
					new String[] {Long.class.getName()},
					new String[] {"targetObjectStateId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByTargetObjectStateId",
					new String[] {Long.class.getName()},
					new String[] {"targetObjectStateId"}, false),
				_SQL_SELECT_OBJECTSTATETRANSITION_WHERE,
				_SQL_COUNT_OBJECTSTATETRANSITION_WHERE,
				ObjectStateTransitionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"objectStateTransition.", "targetObjectStateId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectStateTransition::getTargetObjectStateId));

		ObjectStateTransitionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectStateTransitionUtil.setPersistence(null);

		entityCache.removeCache(ObjectStateTransitionImpl.class.getName());
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
		ObjectStateTransitionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTSTATETRANSITION =
		"SELECT objectStateTransition FROM ObjectStateTransition objectStateTransition";

	private static final String _SQL_SELECT_OBJECTSTATETRANSITION_WHERE =
		"SELECT objectStateTransition FROM ObjectStateTransition objectStateTransition WHERE ";

	private static final String _SQL_COUNT_OBJECTSTATETRANSITION_WHERE =
		"SELECT COUNT(objectStateTransition) FROM ObjectStateTransition objectStateTransition WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-403123428