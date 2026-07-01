/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectViewColumnException;
import com.liferay.object.model.ObjectViewColumn;
import com.liferay.object.model.ObjectViewColumnTable;
import com.liferay.object.model.impl.ObjectViewColumnImpl;
import com.liferay.object.model.impl.ObjectViewColumnModelImpl;
import com.liferay.object.service.persistence.ObjectViewColumnPersistence;
import com.liferay.object.service.persistence.ObjectViewColumnUtil;
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
 * The persistence implementation for the object view column service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectViewColumnPersistence.class)
public class ObjectViewColumnPersistenceImpl
	extends BasePersistenceImpl
		<ObjectViewColumn, NoSuchObjectViewColumnException>
	implements ObjectViewColumnPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectViewColumnUtil</code> to access the object view column persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectViewColumnImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ObjectViewColumn, NoSuchObjectViewColumnException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object view columns where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectViewColumnModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object view columns
	 * @param end the upper bound of the range of object view columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object view columns
	 */
	@Override
	public List<ObjectViewColumn> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectViewColumn> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object view column in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view column
	 * @throws NoSuchObjectViewColumnException if a matching object view column could not be found
	 */
	@Override
	public ObjectViewColumn findByUuid_First(
			String uuid, OrderByComparator<ObjectViewColumn> orderByComparator)
		throws NoSuchObjectViewColumnException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object view column in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view column, or <code>null</code> if a matching object view column could not be found
	 */
	@Override
	public ObjectViewColumn fetchByUuid_First(
		String uuid, OrderByComparator<ObjectViewColumn> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object view columns where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object view columns where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object view columns
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ObjectViewColumn, NoSuchObjectViewColumnException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object view columns where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectViewColumnModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object view columns
	 * @param end the upper bound of the range of object view columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object view columns
	 */
	@Override
	public List<ObjectViewColumn> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectViewColumn> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object view column in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view column
	 * @throws NoSuchObjectViewColumnException if a matching object view column could not be found
	 */
	@Override
	public ObjectViewColumn findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectViewColumn> orderByComparator)
		throws NoSuchObjectViewColumnException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object view column in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view column, or <code>null</code> if a matching object view column could not be found
	 */
	@Override
	public ObjectViewColumn fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectViewColumn> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object view columns where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object view columns where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object view columns
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ObjectViewColumn, NoSuchObjectViewColumnException>
			_collectionPersistenceFinderByObjectViewId;

	/**
	 * Returns an ordered range of all the object view columns where objectViewId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectViewColumnModelImpl</code>.
	 * </p>
	 *
	 * @param objectViewId the object view ID
	 * @param start the lower bound of the range of object view columns
	 * @param end the upper bound of the range of object view columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object view columns
	 */
	@Override
	public List<ObjectViewColumn> findByObjectViewId(
		long objectViewId, int start, int end,
		OrderByComparator<ObjectViewColumn> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectViewId.find(
			finderCache, new Object[] {objectViewId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object view column in the ordered set where objectViewId = &#63;.
	 *
	 * @param objectViewId the object view ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view column
	 * @throws NoSuchObjectViewColumnException if a matching object view column could not be found
	 */
	@Override
	public ObjectViewColumn findByObjectViewId_First(
			long objectViewId,
			OrderByComparator<ObjectViewColumn> orderByComparator)
		throws NoSuchObjectViewColumnException {

		return _collectionPersistenceFinderByObjectViewId.findFirst(
			finderCache, new Object[] {objectViewId}, orderByComparator);
	}

	/**
	 * Returns the first object view column in the ordered set where objectViewId = &#63;.
	 *
	 * @param objectViewId the object view ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view column, or <code>null</code> if a matching object view column could not be found
	 */
	@Override
	public ObjectViewColumn fetchByObjectViewId_First(
		long objectViewId,
		OrderByComparator<ObjectViewColumn> orderByComparator) {

		return _collectionPersistenceFinderByObjectViewId.fetchFirst(
			finderCache, new Object[] {objectViewId}, orderByComparator);
	}

	/**
	 * Removes all the object view columns where objectViewId = &#63; from the database.
	 *
	 * @param objectViewId the object view ID
	 */
	@Override
	public void removeByObjectViewId(long objectViewId) {
		_collectionPersistenceFinderByObjectViewId.remove(
			finderCache, new Object[] {objectViewId});
	}

	/**
	 * Returns the number of object view columns where objectViewId = &#63;.
	 *
	 * @param objectViewId the object view ID
	 * @return the number of matching object view columns
	 */
	@Override
	public int countByObjectViewId(long objectViewId) {
		return _collectionPersistenceFinderByObjectViewId.count(
			finderCache, new Object[] {objectViewId});
	}

	private CollectionPersistenceFinder
		<ObjectViewColumn, NoSuchObjectViewColumnException>
			_collectionPersistenceFinderByOVI_OFN;

	/**
	 * Returns an ordered range of all the object view columns where objectViewId = &#63; and objectFieldName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectViewColumnModelImpl</code>.
	 * </p>
	 *
	 * @param objectViewId the object view ID
	 * @param objectFieldName the object field name
	 * @param start the lower bound of the range of object view columns
	 * @param end the upper bound of the range of object view columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object view columns
	 */
	@Override
	public List<ObjectViewColumn> findByOVI_OFN(
		long objectViewId, String objectFieldName, int start, int end,
		OrderByComparator<ObjectViewColumn> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByOVI_OFN.find(
			finderCache, new Object[] {objectViewId, objectFieldName}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object view column in the ordered set where objectViewId = &#63; and objectFieldName = &#63;.
	 *
	 * @param objectViewId the object view ID
	 * @param objectFieldName the object field name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view column
	 * @throws NoSuchObjectViewColumnException if a matching object view column could not be found
	 */
	@Override
	public ObjectViewColumn findByOVI_OFN_First(
			long objectViewId, String objectFieldName,
			OrderByComparator<ObjectViewColumn> orderByComparator)
		throws NoSuchObjectViewColumnException {

		return _collectionPersistenceFinderByOVI_OFN.findFirst(
			finderCache, new Object[] {objectViewId, objectFieldName},
			orderByComparator);
	}

	/**
	 * Returns the first object view column in the ordered set where objectViewId = &#63; and objectFieldName = &#63;.
	 *
	 * @param objectViewId the object view ID
	 * @param objectFieldName the object field name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view column, or <code>null</code> if a matching object view column could not be found
	 */
	@Override
	public ObjectViewColumn fetchByOVI_OFN_First(
		long objectViewId, String objectFieldName,
		OrderByComparator<ObjectViewColumn> orderByComparator) {

		return _collectionPersistenceFinderByOVI_OFN.fetchFirst(
			finderCache, new Object[] {objectViewId, objectFieldName},
			orderByComparator);
	}

	/**
	 * Removes all the object view columns where objectViewId = &#63; and objectFieldName = &#63; from the database.
	 *
	 * @param objectViewId the object view ID
	 * @param objectFieldName the object field name
	 */
	@Override
	public void removeByOVI_OFN(long objectViewId, String objectFieldName) {
		_collectionPersistenceFinderByOVI_OFN.remove(
			finderCache, new Object[] {objectViewId, objectFieldName});
	}

	/**
	 * Returns the number of object view columns where objectViewId = &#63; and objectFieldName = &#63;.
	 *
	 * @param objectViewId the object view ID
	 * @param objectFieldName the object field name
	 * @return the number of matching object view columns
	 */
	@Override
	public int countByOVI_OFN(long objectViewId, String objectFieldName) {
		return _collectionPersistenceFinderByOVI_OFN.count(
			finderCache, new Object[] {objectViewId, objectFieldName});
	}

	public ObjectViewColumnPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectViewColumn.class);

		setModelImplClass(ObjectViewColumnImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectViewColumnTable.INSTANCE);
	}

	/**
	 * Creates a new object view column with the primary key. Does not add the object view column to the database.
	 *
	 * @param objectViewColumnId the primary key for the new object view column
	 * @return the new object view column
	 */
	@Override
	public ObjectViewColumn create(long objectViewColumnId) {
		ObjectViewColumn objectViewColumn = new ObjectViewColumnImpl();

		objectViewColumn.setNew(true);
		objectViewColumn.setPrimaryKey(objectViewColumnId);

		String uuid = PortalUUIDUtil.generate();

		objectViewColumn.setUuid(uuid);

		objectViewColumn.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectViewColumn;
	}

	/**
	 * Removes the object view column with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectViewColumnId the primary key of the object view column
	 * @return the object view column that was removed
	 * @throws NoSuchObjectViewColumnException if a object view column with the primary key could not be found
	 */
	@Override
	public ObjectViewColumn remove(long objectViewColumnId)
		throws NoSuchObjectViewColumnException {

		return remove((Serializable)objectViewColumnId);
	}

	@Override
	protected ObjectViewColumn removeImpl(ObjectViewColumn objectViewColumn) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectViewColumn)) {
				objectViewColumn = (ObjectViewColumn)session.get(
					ObjectViewColumnImpl.class,
					objectViewColumn.getPrimaryKeyObj());
			}

			if (objectViewColumn != null) {
				session.delete(objectViewColumn);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectViewColumn != null) {
			clearCache(objectViewColumn);
		}

		return objectViewColumn;
	}

	@Override
	public ObjectViewColumn updateImpl(ObjectViewColumn objectViewColumn) {
		boolean isNew = objectViewColumn.isNew();

		if (!(objectViewColumn instanceof ObjectViewColumnModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectViewColumn.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectViewColumn);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectViewColumn proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectViewColumn implementation " +
					objectViewColumn.getClass());
		}

		ObjectViewColumnModelImpl objectViewColumnModelImpl =
			(ObjectViewColumnModelImpl)objectViewColumn;

		if (Validator.isNull(objectViewColumn.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectViewColumn.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectViewColumn.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectViewColumn.setCreateDate(date);
			}
			else {
				objectViewColumn.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectViewColumnModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectViewColumn.setModifiedDate(date);
			}
			else {
				objectViewColumn.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectViewColumn);
			}
			else {
				objectViewColumn = (ObjectViewColumn)session.merge(
					objectViewColumn);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectViewColumn, false);

		if (isNew) {
			objectViewColumn.setNew(false);
		}

		objectViewColumn.resetOriginalValues();

		return objectViewColumn;
	}

	/**
	 * Returns the object view column with the primary key or throws a <code>NoSuchObjectViewColumnException</code> if it could not be found.
	 *
	 * @param objectViewColumnId the primary key of the object view column
	 * @return the object view column
	 * @throws NoSuchObjectViewColumnException if a object view column with the primary key could not be found
	 */
	@Override
	public ObjectViewColumn findByPrimaryKey(long objectViewColumnId)
		throws NoSuchObjectViewColumnException {

		return findByPrimaryKey((Serializable)objectViewColumnId);
	}

	/**
	 * Returns the object view column with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectViewColumnId the primary key of the object view column
	 * @return the object view column, or <code>null</code> if a object view column with the primary key could not be found
	 */
	@Override
	public ObjectViewColumn fetchByPrimaryKey(long objectViewColumnId) {
		return fetchByPrimaryKey((Serializable)objectViewColumnId);
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
		return "objectViewColumnId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTVIEWCOLUMN;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectViewColumnModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object view column persistence.
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
			_SQL_SELECT_OBJECTVIEWCOLUMN_WHERE,
			_SQL_COUNT_OBJECTVIEWCOLUMN_WHERE,
			ObjectViewColumnModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"objectViewColumn.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, ObjectViewColumn::getUuid));

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
				_SQL_SELECT_OBJECTVIEWCOLUMN_WHERE,
				_SQL_COUNT_OBJECTVIEWCOLUMN_WHERE,
				ObjectViewColumnModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"objectViewColumn.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectViewColumn::getUuid),
				new FinderColumn<>(
					"objectViewColumn.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectViewColumn::getCompanyId));

		_collectionPersistenceFinderByObjectViewId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByObjectViewId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectViewId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByObjectViewId", new String[] {Long.class.getName()},
					new String[] {"objectViewId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByObjectViewId", new String[] {Long.class.getName()},
					new String[] {"objectViewId"}, false),
				_SQL_SELECT_OBJECTVIEWCOLUMN_WHERE,
				_SQL_COUNT_OBJECTVIEWCOLUMN_WHERE,
				ObjectViewColumnModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"objectViewColumn.", "objectViewId", FinderColumn.Type.LONG,
					"=", true, true, ObjectViewColumn::getObjectViewId));

		_collectionPersistenceFinderByOVI_OFN =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByOVI_OFN",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectViewId", "objectFieldName"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByOVI_OFN",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"objectViewId", "objectFieldName"}, 0, 2,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByOVI_OFN",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"objectViewId", "objectFieldName"}, 0, 2,
					false, null),
				_SQL_SELECT_OBJECTVIEWCOLUMN_WHERE,
				_SQL_COUNT_OBJECTVIEWCOLUMN_WHERE,
				ObjectViewColumnModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"objectViewColumn.", "objectViewId", FinderColumn.Type.LONG,
					"=", true, true, ObjectViewColumn::getObjectViewId),
				new FinderColumn<>(
					"objectViewColumn.", "objectFieldName",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectViewColumn::getObjectFieldName));

		ObjectViewColumnUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectViewColumnUtil.setPersistence(null);

		entityCache.removeCache(ObjectViewColumnImpl.class.getName());
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
		ObjectViewColumnModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTVIEWCOLUMN =
		"SELECT objectViewColumn FROM ObjectViewColumn objectViewColumn";

	private static final String _SQL_SELECT_OBJECTVIEWCOLUMN_WHERE =
		"SELECT objectViewColumn FROM ObjectViewColumn objectViewColumn WHERE ";

	private static final String _SQL_COUNT_OBJECTVIEWCOLUMN_WHERE =
		"SELECT COUNT(objectViewColumn) FROM ObjectViewColumn objectViewColumn WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1314259238