/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectFilterException;
import com.liferay.object.model.ObjectFilter;
import com.liferay.object.model.ObjectFilterTable;
import com.liferay.object.model.impl.ObjectFilterImpl;
import com.liferay.object.model.impl.ObjectFilterModelImpl;
import com.liferay.object.service.persistence.ObjectFilterPersistence;
import com.liferay.object.service.persistence.ObjectFilterUtil;
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
 * The persistence implementation for the object filter service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectFilterPersistence.class)
public class ObjectFilterPersistenceImpl
	extends BasePersistenceImpl<ObjectFilter, NoSuchObjectFilterException>
	implements ObjectFilterPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectFilterUtil</code> to access the object filter persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectFilterImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ObjectFilter, NoSuchObjectFilterException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object filters where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFilterModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object filters
	 * @param end the upper bound of the range of object filters (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object filters
	 */
	@Override
	public List<ObjectFilter> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectFilter> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object filter in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object filter
	 * @throws NoSuchObjectFilterException if a matching object filter could not be found
	 */
	@Override
	public ObjectFilter findByUuid_First(
			String uuid, OrderByComparator<ObjectFilter> orderByComparator)
		throws NoSuchObjectFilterException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object filter in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object filter, or <code>null</code> if a matching object filter could not be found
	 */
	@Override
	public ObjectFilter fetchByUuid_First(
		String uuid, OrderByComparator<ObjectFilter> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object filters where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object filters where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object filters
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ObjectFilter, NoSuchObjectFilterException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object filters where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFilterModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object filters
	 * @param end the upper bound of the range of object filters (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object filters
	 */
	@Override
	public List<ObjectFilter> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectFilter> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object filter in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object filter
	 * @throws NoSuchObjectFilterException if a matching object filter could not be found
	 */
	@Override
	public ObjectFilter findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectFilter> orderByComparator)
		throws NoSuchObjectFilterException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object filter in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object filter, or <code>null</code> if a matching object filter could not be found
	 */
	@Override
	public ObjectFilter fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectFilter> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object filters where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object filters where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object filters
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ObjectFilter, NoSuchObjectFilterException>
			_collectionPersistenceFinderByObjectFieldId;

	/**
	 * Returns an ordered range of all the object filters where objectFieldId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFilterModelImpl</code>.
	 * </p>
	 *
	 * @param objectFieldId the object field ID
	 * @param start the lower bound of the range of object filters
	 * @param end the upper bound of the range of object filters (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object filters
	 */
	@Override
	public List<ObjectFilter> findByObjectFieldId(
		long objectFieldId, int start, int end,
		OrderByComparator<ObjectFilter> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectFieldId.find(
			finderCache, new Object[] {objectFieldId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object filter in the ordered set where objectFieldId = &#63;.
	 *
	 * @param objectFieldId the object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object filter
	 * @throws NoSuchObjectFilterException if a matching object filter could not be found
	 */
	@Override
	public ObjectFilter findByObjectFieldId_First(
			long objectFieldId,
			OrderByComparator<ObjectFilter> orderByComparator)
		throws NoSuchObjectFilterException {

		return _collectionPersistenceFinderByObjectFieldId.findFirst(
			finderCache, new Object[] {objectFieldId}, orderByComparator);
	}

	/**
	 * Returns the first object filter in the ordered set where objectFieldId = &#63;.
	 *
	 * @param objectFieldId the object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object filter, or <code>null</code> if a matching object filter could not be found
	 */
	@Override
	public ObjectFilter fetchByObjectFieldId_First(
		long objectFieldId, OrderByComparator<ObjectFilter> orderByComparator) {

		return _collectionPersistenceFinderByObjectFieldId.fetchFirst(
			finderCache, new Object[] {objectFieldId}, orderByComparator);
	}

	/**
	 * Removes all the object filters where objectFieldId = &#63; from the database.
	 *
	 * @param objectFieldId the object field ID
	 */
	@Override
	public void removeByObjectFieldId(long objectFieldId) {
		_collectionPersistenceFinderByObjectFieldId.remove(
			finderCache, new Object[] {objectFieldId});
	}

	/**
	 * Returns the number of object filters where objectFieldId = &#63;.
	 *
	 * @param objectFieldId the object field ID
	 * @return the number of matching object filters
	 */
	@Override
	public int countByObjectFieldId(long objectFieldId) {
		return _collectionPersistenceFinderByObjectFieldId.count(
			finderCache, new Object[] {objectFieldId});
	}

	public ObjectFilterPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectFilter.class);

		setModelImplClass(ObjectFilterImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectFilterTable.INSTANCE);
	}

	/**
	 * Creates a new object filter with the primary key. Does not add the object filter to the database.
	 *
	 * @param objectFilterId the primary key for the new object filter
	 * @return the new object filter
	 */
	@Override
	public ObjectFilter create(long objectFilterId) {
		ObjectFilter objectFilter = new ObjectFilterImpl();

		objectFilter.setNew(true);
		objectFilter.setPrimaryKey(objectFilterId);

		String uuid = PortalUUIDUtil.generate();

		objectFilter.setUuid(uuid);

		objectFilter.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectFilter;
	}

	/**
	 * Removes the object filter with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectFilterId the primary key of the object filter
	 * @return the object filter that was removed
	 * @throws NoSuchObjectFilterException if a object filter with the primary key could not be found
	 */
	@Override
	public ObjectFilter remove(long objectFilterId)
		throws NoSuchObjectFilterException {

		return remove((Serializable)objectFilterId);
	}

	@Override
	protected ObjectFilter removeImpl(ObjectFilter objectFilter) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectFilter)) {
				objectFilter = (ObjectFilter)session.get(
					ObjectFilterImpl.class, objectFilter.getPrimaryKeyObj());
			}

			if (objectFilter != null) {
				session.delete(objectFilter);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectFilter != null) {
			clearCache(objectFilter);
		}

		return objectFilter;
	}

	@Override
	public ObjectFilter updateImpl(ObjectFilter objectFilter) {
		boolean isNew = objectFilter.isNew();

		if (!(objectFilter instanceof ObjectFilterModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectFilter.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectFilter);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectFilter proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectFilter implementation " +
					objectFilter.getClass());
		}

		ObjectFilterModelImpl objectFilterModelImpl =
			(ObjectFilterModelImpl)objectFilter;

		if (Validator.isNull(objectFilter.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectFilter.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectFilter.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectFilter.setCreateDate(date);
			}
			else {
				objectFilter.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!objectFilterModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectFilter.setModifiedDate(date);
			}
			else {
				objectFilter.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectFilter);
			}
			else {
				objectFilter = (ObjectFilter)session.merge(objectFilter);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectFilter, false);

		if (isNew) {
			objectFilter.setNew(false);
		}

		objectFilter.resetOriginalValues();

		return objectFilter;
	}

	/**
	 * Returns the object filter with the primary key or throws a <code>NoSuchObjectFilterException</code> if it could not be found.
	 *
	 * @param objectFilterId the primary key of the object filter
	 * @return the object filter
	 * @throws NoSuchObjectFilterException if a object filter with the primary key could not be found
	 */
	@Override
	public ObjectFilter findByPrimaryKey(long objectFilterId)
		throws NoSuchObjectFilterException {

		return findByPrimaryKey((Serializable)objectFilterId);
	}

	/**
	 * Returns the object filter with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectFilterId the primary key of the object filter
	 * @return the object filter, or <code>null</code> if a object filter with the primary key could not be found
	 */
	@Override
	public ObjectFilter fetchByPrimaryKey(long objectFilterId) {
		return fetchByPrimaryKey((Serializable)objectFilterId);
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
		return "objectFilterId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTFILTER;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectFilterModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object filter persistence.
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
			_SQL_SELECT_OBJECTFILTER_WHERE, _SQL_COUNT_OBJECTFILTER_WHERE,
			ObjectFilterModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"objectFilter.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, ObjectFilter::getUuid));

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
				_SQL_SELECT_OBJECTFILTER_WHERE, _SQL_COUNT_OBJECTFILTER_WHERE,
				ObjectFilterModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectFilter.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, ObjectFilter::getUuid),
				new FinderColumn<>(
					"objectFilter.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, ObjectFilter::getCompanyId));

		_collectionPersistenceFinderByObjectFieldId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByObjectFieldId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectFieldId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByObjectFieldId", new String[] {Long.class.getName()},
					new String[] {"objectFieldId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByObjectFieldId", new String[] {Long.class.getName()},
					new String[] {"objectFieldId"}, false),
				_SQL_SELECT_OBJECTFILTER_WHERE, _SQL_COUNT_OBJECTFILTER_WHERE,
				ObjectFilterModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectFilter.", "objectFieldId", FinderColumn.Type.LONG,
					"=", true, true, ObjectFilter::getObjectFieldId));

		ObjectFilterUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectFilterUtil.setPersistence(null);

		entityCache.removeCache(ObjectFilterImpl.class.getName());
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
		ObjectFilterModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTFILTER =
		"SELECT objectFilter FROM ObjectFilter objectFilter";

	private static final String _SQL_SELECT_OBJECTFILTER_WHERE =
		"SELECT objectFilter FROM ObjectFilter objectFilter WHERE ";

	private static final String _SQL_COUNT_OBJECTFILTER_WHERE =
		"SELECT COUNT(objectFilter) FROM ObjectFilter objectFilter WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-2031880833