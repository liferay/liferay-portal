/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectStateFlowException;
import com.liferay.object.model.ObjectStateFlow;
import com.liferay.object.model.ObjectStateFlowTable;
import com.liferay.object.model.impl.ObjectStateFlowImpl;
import com.liferay.object.model.impl.ObjectStateFlowModelImpl;
import com.liferay.object.service.persistence.ObjectStateFlowPersistence;
import com.liferay.object.service.persistence.ObjectStateFlowUtil;
import com.liferay.object.service.persistence.impl.constants.ObjectPersistenceConstants;
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
 * The persistence implementation for the object state flow service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectStateFlowPersistence.class)
public class ObjectStateFlowPersistenceImpl
	extends BasePersistenceImpl<ObjectStateFlow, NoSuchObjectStateFlowException>
	implements ObjectStateFlowPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectStateFlowUtil</code> to access the object state flow persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectStateFlowImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ObjectStateFlow, NoSuchObjectStateFlowException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object state flows where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectStateFlowModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object state flows
	 * @param end the upper bound of the range of object state flows (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object state flows
	 */
	@Override
	public List<ObjectStateFlow> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectStateFlow> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object state flow in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state flow
	 * @throws NoSuchObjectStateFlowException if a matching object state flow could not be found
	 */
	@Override
	public ObjectStateFlow findByUuid_First(
			String uuid, OrderByComparator<ObjectStateFlow> orderByComparator)
		throws NoSuchObjectStateFlowException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object state flow in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state flow, or <code>null</code> if a matching object state flow could not be found
	 */
	@Override
	public ObjectStateFlow fetchByUuid_First(
		String uuid, OrderByComparator<ObjectStateFlow> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object state flows where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object state flows where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object state flows
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ObjectStateFlow, NoSuchObjectStateFlowException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object state flows where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectStateFlowModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object state flows
	 * @param end the upper bound of the range of object state flows (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object state flows
	 */
	@Override
	public List<ObjectStateFlow> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectStateFlow> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object state flow in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state flow
	 * @throws NoSuchObjectStateFlowException if a matching object state flow could not be found
	 */
	@Override
	public ObjectStateFlow findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectStateFlow> orderByComparator)
		throws NoSuchObjectStateFlowException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object state flow in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object state flow, or <code>null</code> if a matching object state flow could not be found
	 */
	@Override
	public ObjectStateFlow fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectStateFlow> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object state flows where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object state flows where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object state flows
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private UniquePersistenceFinder
		<ObjectStateFlow, NoSuchObjectStateFlowException>
			_uniquePersistenceFinderByObjectFieldId;

	/**
	 * Returns the object state flow where objectFieldId = &#63; or throws a <code>NoSuchObjectStateFlowException</code> if it could not be found.
	 *
	 * @param objectFieldId the object field ID
	 * @return the matching object state flow
	 * @throws NoSuchObjectStateFlowException if a matching object state flow could not be found
	 */
	@Override
	public ObjectStateFlow findByObjectFieldId(long objectFieldId)
		throws NoSuchObjectStateFlowException {

		return _uniquePersistenceFinderByObjectFieldId.find(
			finderCache, new Object[] {objectFieldId});
	}

	/**
	 * Returns the object state flow where objectFieldId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectFieldId the object field ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object state flow, or <code>null</code> if a matching object state flow could not be found
	 */
	@Override
	public ObjectStateFlow fetchByObjectFieldId(
		long objectFieldId, boolean useFinderCache) {

		return _uniquePersistenceFinderByObjectFieldId.fetch(
			finderCache, new Object[] {objectFieldId}, useFinderCache);
	}

	/**
	 * Removes the object state flow where objectFieldId = &#63; from the database.
	 *
	 * @param objectFieldId the object field ID
	 * @return the object state flow that was removed
	 */
	@Override
	public ObjectStateFlow removeByObjectFieldId(long objectFieldId)
		throws NoSuchObjectStateFlowException {

		ObjectStateFlow objectStateFlow = findByObjectFieldId(objectFieldId);

		return remove(objectStateFlow);
	}

	/**
	 * Returns the number of object state flows where objectFieldId = &#63;.
	 *
	 * @param objectFieldId the object field ID
	 * @return the number of matching object state flows
	 */
	@Override
	public int countByObjectFieldId(long objectFieldId) {
		return _uniquePersistenceFinderByObjectFieldId.count(
			finderCache, new Object[] {objectFieldId});
	}

	public ObjectStateFlowPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectStateFlow.class);

		setModelImplClass(ObjectStateFlowImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectStateFlowTable.INSTANCE);
	}

	/**
	 * Creates a new object state flow with the primary key. Does not add the object state flow to the database.
	 *
	 * @param objectStateFlowId the primary key for the new object state flow
	 * @return the new object state flow
	 */
	@Override
	public ObjectStateFlow create(long objectStateFlowId) {
		ObjectStateFlow objectStateFlow = new ObjectStateFlowImpl();

		objectStateFlow.setNew(true);
		objectStateFlow.setPrimaryKey(objectStateFlowId);

		String uuid = PortalUUIDUtil.generate();

		objectStateFlow.setUuid(uuid);

		objectStateFlow.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectStateFlow;
	}

	/**
	 * Removes the object state flow with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectStateFlowId the primary key of the object state flow
	 * @return the object state flow that was removed
	 * @throws NoSuchObjectStateFlowException if a object state flow with the primary key could not be found
	 */
	@Override
	public ObjectStateFlow remove(long objectStateFlowId)
		throws NoSuchObjectStateFlowException {

		return remove((Serializable)objectStateFlowId);
	}

	@Override
	protected ObjectStateFlow removeImpl(ObjectStateFlow objectStateFlow) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectStateFlow)) {
				objectStateFlow = (ObjectStateFlow)session.get(
					ObjectStateFlowImpl.class,
					objectStateFlow.getPrimaryKeyObj());
			}

			if (objectStateFlow != null) {
				session.delete(objectStateFlow);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectStateFlow != null) {
			clearCache(objectStateFlow);
		}

		return objectStateFlow;
	}

	@Override
	public ObjectStateFlow updateImpl(ObjectStateFlow objectStateFlow) {
		boolean isNew = objectStateFlow.isNew();

		if (!(objectStateFlow instanceof ObjectStateFlowModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectStateFlow.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectStateFlow);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectStateFlow proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectStateFlow implementation " +
					objectStateFlow.getClass());
		}

		ObjectStateFlowModelImpl objectStateFlowModelImpl =
			(ObjectStateFlowModelImpl)objectStateFlow;

		if (Validator.isNull(objectStateFlow.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectStateFlow.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectStateFlow.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectStateFlow.setCreateDate(date);
			}
			else {
				objectStateFlow.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectStateFlowModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectStateFlow.setModifiedDate(date);
			}
			else {
				objectStateFlow.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectStateFlow);
			}
			else {
				objectStateFlow = (ObjectStateFlow)session.merge(
					objectStateFlow);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectStateFlow, false);

		if (isNew) {
			objectStateFlow.setNew(false);
		}

		objectStateFlow.resetOriginalValues();

		return objectStateFlow;
	}

	/**
	 * Returns the object state flow with the primary key or throws a <code>NoSuchObjectStateFlowException</code> if it could not be found.
	 *
	 * @param objectStateFlowId the primary key of the object state flow
	 * @return the object state flow
	 * @throws NoSuchObjectStateFlowException if a object state flow with the primary key could not be found
	 */
	@Override
	public ObjectStateFlow findByPrimaryKey(long objectStateFlowId)
		throws NoSuchObjectStateFlowException {

		return findByPrimaryKey((Serializable)objectStateFlowId);
	}

	/**
	 * Returns the object state flow with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectStateFlowId the primary key of the object state flow
	 * @return the object state flow, or <code>null</code> if a object state flow with the primary key could not be found
	 */
	@Override
	public ObjectStateFlow fetchByPrimaryKey(long objectStateFlowId) {
		return fetchByPrimaryKey((Serializable)objectStateFlowId);
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
		return "objectStateFlowId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTSTATEFLOW;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectStateFlowModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object state flow persistence.
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
			_SQL_SELECT_OBJECTSTATEFLOW_WHERE, _SQL_COUNT_OBJECTSTATEFLOW_WHERE,
			ObjectStateFlowModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"objectStateFlow.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, ObjectStateFlow::getUuid));

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
				_SQL_SELECT_OBJECTSTATEFLOW_WHERE,
				_SQL_COUNT_OBJECTSTATEFLOW_WHERE,
				ObjectStateFlowModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectStateFlow.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectStateFlow::getUuid),
				new FinderColumn<>(
					"objectStateFlow.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectStateFlow::getCompanyId));

		_uniquePersistenceFinderByObjectFieldId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByObjectFieldId",
				new String[] {Long.class.getName()},
				new String[] {"objectFieldId"}, 0, 0, false,
				ObjectStateFlow::getObjectFieldId),
			_SQL_SELECT_OBJECTSTATEFLOW_WHERE, "",
			new FinderColumn<>(
				"objectStateFlow.", "objectFieldId", FinderColumn.Type.LONG,
				"=", true, true, ObjectStateFlow::getObjectFieldId));

		ObjectStateFlowUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectStateFlowUtil.setPersistence(null);

		entityCache.removeCache(ObjectStateFlowImpl.class.getName());
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
		ObjectStateFlowModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTSTATEFLOW =
		"SELECT objectStateFlow FROM ObjectStateFlow objectStateFlow";

	private static final String _SQL_SELECT_OBJECTSTATEFLOW_WHERE =
		"SELECT objectStateFlow FROM ObjectStateFlow objectStateFlow WHERE ";

	private static final String _SQL_COUNT_OBJECTSTATEFLOW_WHERE =
		"SELECT COUNT(objectStateFlow) FROM ObjectStateFlow objectStateFlow WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ObjectStateFlow exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectStateFlowPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-372173842