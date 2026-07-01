/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectLayoutColumnException;
import com.liferay.object.model.ObjectLayoutColumn;
import com.liferay.object.model.ObjectLayoutColumnTable;
import com.liferay.object.model.impl.ObjectLayoutColumnImpl;
import com.liferay.object.model.impl.ObjectLayoutColumnModelImpl;
import com.liferay.object.service.persistence.ObjectLayoutColumnPersistence;
import com.liferay.object.service.persistence.ObjectLayoutColumnUtil;
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
 * The persistence implementation for the object layout column service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectLayoutColumnPersistence.class)
public class ObjectLayoutColumnPersistenceImpl
	extends BasePersistenceImpl
		<ObjectLayoutColumn, NoSuchObjectLayoutColumnException>
	implements ObjectLayoutColumnPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectLayoutColumnUtil</code> to access the object layout column persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectLayoutColumnImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ObjectLayoutColumn, NoSuchObjectLayoutColumnException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object layout columns where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutColumnModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object layout columns
	 * @param end the upper bound of the range of object layout columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layout columns
	 */
	@Override
	public List<ObjectLayoutColumn> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectLayoutColumn> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object layout column in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout column
	 * @throws NoSuchObjectLayoutColumnException if a matching object layout column could not be found
	 */
	@Override
	public ObjectLayoutColumn findByUuid_First(
			String uuid,
			OrderByComparator<ObjectLayoutColumn> orderByComparator)
		throws NoSuchObjectLayoutColumnException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object layout column in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout column, or <code>null</code> if a matching object layout column could not be found
	 */
	@Override
	public ObjectLayoutColumn fetchByUuid_First(
		String uuid, OrderByComparator<ObjectLayoutColumn> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object layout columns where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object layout columns where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object layout columns
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ObjectLayoutColumn, NoSuchObjectLayoutColumnException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object layout columns where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutColumnModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object layout columns
	 * @param end the upper bound of the range of object layout columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layout columns
	 */
	@Override
	public List<ObjectLayoutColumn> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectLayoutColumn> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object layout column in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout column
	 * @throws NoSuchObjectLayoutColumnException if a matching object layout column could not be found
	 */
	@Override
	public ObjectLayoutColumn findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectLayoutColumn> orderByComparator)
		throws NoSuchObjectLayoutColumnException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object layout column in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout column, or <code>null</code> if a matching object layout column could not be found
	 */
	@Override
	public ObjectLayoutColumn fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectLayoutColumn> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object layout columns where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object layout columns where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object layout columns
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ObjectLayoutColumn, NoSuchObjectLayoutColumnException>
			_collectionPersistenceFinderByObjectFieldId;

	/**
	 * Returns an ordered range of all the object layout columns where objectFieldId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutColumnModelImpl</code>.
	 * </p>
	 *
	 * @param objectFieldId the object field ID
	 * @param start the lower bound of the range of object layout columns
	 * @param end the upper bound of the range of object layout columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layout columns
	 */
	@Override
	public List<ObjectLayoutColumn> findByObjectFieldId(
		long objectFieldId, int start, int end,
		OrderByComparator<ObjectLayoutColumn> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectFieldId.find(
			finderCache, new Object[] {objectFieldId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object layout column in the ordered set where objectFieldId = &#63;.
	 *
	 * @param objectFieldId the object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout column
	 * @throws NoSuchObjectLayoutColumnException if a matching object layout column could not be found
	 */
	@Override
	public ObjectLayoutColumn findByObjectFieldId_First(
			long objectFieldId,
			OrderByComparator<ObjectLayoutColumn> orderByComparator)
		throws NoSuchObjectLayoutColumnException {

		return _collectionPersistenceFinderByObjectFieldId.findFirst(
			finderCache, new Object[] {objectFieldId}, orderByComparator);
	}

	/**
	 * Returns the first object layout column in the ordered set where objectFieldId = &#63;.
	 *
	 * @param objectFieldId the object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout column, or <code>null</code> if a matching object layout column could not be found
	 */
	@Override
	public ObjectLayoutColumn fetchByObjectFieldId_First(
		long objectFieldId,
		OrderByComparator<ObjectLayoutColumn> orderByComparator) {

		return _collectionPersistenceFinderByObjectFieldId.fetchFirst(
			finderCache, new Object[] {objectFieldId}, orderByComparator);
	}

	/**
	 * Removes all the object layout columns where objectFieldId = &#63; from the database.
	 *
	 * @param objectFieldId the object field ID
	 */
	@Override
	public void removeByObjectFieldId(long objectFieldId) {
		_collectionPersistenceFinderByObjectFieldId.remove(
			finderCache, new Object[] {objectFieldId});
	}

	/**
	 * Returns the number of object layout columns where objectFieldId = &#63;.
	 *
	 * @param objectFieldId the object field ID
	 * @return the number of matching object layout columns
	 */
	@Override
	public int countByObjectFieldId(long objectFieldId) {
		return _collectionPersistenceFinderByObjectFieldId.count(
			finderCache, new Object[] {objectFieldId});
	}

	private CollectionPersistenceFinder
		<ObjectLayoutColumn, NoSuchObjectLayoutColumnException>
			_collectionPersistenceFinderByObjectLayoutRowId;

	/**
	 * Returns an ordered range of all the object layout columns where objectLayoutRowId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutColumnModelImpl</code>.
	 * </p>
	 *
	 * @param objectLayoutRowId the object layout row ID
	 * @param start the lower bound of the range of object layout columns
	 * @param end the upper bound of the range of object layout columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layout columns
	 */
	@Override
	public List<ObjectLayoutColumn> findByObjectLayoutRowId(
		long objectLayoutRowId, int start, int end,
		OrderByComparator<ObjectLayoutColumn> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectLayoutRowId.find(
			finderCache, new Object[] {objectLayoutRowId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object layout column in the ordered set where objectLayoutRowId = &#63;.
	 *
	 * @param objectLayoutRowId the object layout row ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout column
	 * @throws NoSuchObjectLayoutColumnException if a matching object layout column could not be found
	 */
	@Override
	public ObjectLayoutColumn findByObjectLayoutRowId_First(
			long objectLayoutRowId,
			OrderByComparator<ObjectLayoutColumn> orderByComparator)
		throws NoSuchObjectLayoutColumnException {

		return _collectionPersistenceFinderByObjectLayoutRowId.findFirst(
			finderCache, new Object[] {objectLayoutRowId}, orderByComparator);
	}

	/**
	 * Returns the first object layout column in the ordered set where objectLayoutRowId = &#63;.
	 *
	 * @param objectLayoutRowId the object layout row ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout column, or <code>null</code> if a matching object layout column could not be found
	 */
	@Override
	public ObjectLayoutColumn fetchByObjectLayoutRowId_First(
		long objectLayoutRowId,
		OrderByComparator<ObjectLayoutColumn> orderByComparator) {

		return _collectionPersistenceFinderByObjectLayoutRowId.fetchFirst(
			finderCache, new Object[] {objectLayoutRowId}, orderByComparator);
	}

	/**
	 * Removes all the object layout columns where objectLayoutRowId = &#63; from the database.
	 *
	 * @param objectLayoutRowId the object layout row ID
	 */
	@Override
	public void removeByObjectLayoutRowId(long objectLayoutRowId) {
		_collectionPersistenceFinderByObjectLayoutRowId.remove(
			finderCache, new Object[] {objectLayoutRowId});
	}

	/**
	 * Returns the number of object layout columns where objectLayoutRowId = &#63;.
	 *
	 * @param objectLayoutRowId the object layout row ID
	 * @return the number of matching object layout columns
	 */
	@Override
	public int countByObjectLayoutRowId(long objectLayoutRowId) {
		return _collectionPersistenceFinderByObjectLayoutRowId.count(
			finderCache, new Object[] {objectLayoutRowId});
	}

	public ObjectLayoutColumnPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("size", "size_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectLayoutColumn.class);

		setModelImplClass(ObjectLayoutColumnImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectLayoutColumnTable.INSTANCE);
	}

	/**
	 * Creates a new object layout column with the primary key. Does not add the object layout column to the database.
	 *
	 * @param objectLayoutColumnId the primary key for the new object layout column
	 * @return the new object layout column
	 */
	@Override
	public ObjectLayoutColumn create(long objectLayoutColumnId) {
		ObjectLayoutColumn objectLayoutColumn = new ObjectLayoutColumnImpl();

		objectLayoutColumn.setNew(true);
		objectLayoutColumn.setPrimaryKey(objectLayoutColumnId);

		String uuid = PortalUUIDUtil.generate();

		objectLayoutColumn.setUuid(uuid);

		objectLayoutColumn.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectLayoutColumn;
	}

	/**
	 * Removes the object layout column with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectLayoutColumnId the primary key of the object layout column
	 * @return the object layout column that was removed
	 * @throws NoSuchObjectLayoutColumnException if a object layout column with the primary key could not be found
	 */
	@Override
	public ObjectLayoutColumn remove(long objectLayoutColumnId)
		throws NoSuchObjectLayoutColumnException {

		return remove((Serializable)objectLayoutColumnId);
	}

	@Override
	protected ObjectLayoutColumn removeImpl(
		ObjectLayoutColumn objectLayoutColumn) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectLayoutColumn)) {
				objectLayoutColumn = (ObjectLayoutColumn)session.get(
					ObjectLayoutColumnImpl.class,
					objectLayoutColumn.getPrimaryKeyObj());
			}

			if (objectLayoutColumn != null) {
				session.delete(objectLayoutColumn);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectLayoutColumn != null) {
			clearCache(objectLayoutColumn);
		}

		return objectLayoutColumn;
	}

	@Override
	public ObjectLayoutColumn updateImpl(
		ObjectLayoutColumn objectLayoutColumn) {

		boolean isNew = objectLayoutColumn.isNew();

		if (!(objectLayoutColumn instanceof ObjectLayoutColumnModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectLayoutColumn.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectLayoutColumn);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectLayoutColumn proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectLayoutColumn implementation " +
					objectLayoutColumn.getClass());
		}

		ObjectLayoutColumnModelImpl objectLayoutColumnModelImpl =
			(ObjectLayoutColumnModelImpl)objectLayoutColumn;

		if (Validator.isNull(objectLayoutColumn.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectLayoutColumn.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectLayoutColumn.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectLayoutColumn.setCreateDate(date);
			}
			else {
				objectLayoutColumn.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectLayoutColumnModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectLayoutColumn.setModifiedDate(date);
			}
			else {
				objectLayoutColumn.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectLayoutColumn);
			}
			else {
				objectLayoutColumn = (ObjectLayoutColumn)session.merge(
					objectLayoutColumn);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectLayoutColumn, false);

		if (isNew) {
			objectLayoutColumn.setNew(false);
		}

		objectLayoutColumn.resetOriginalValues();

		return objectLayoutColumn;
	}

	/**
	 * Returns the object layout column with the primary key or throws a <code>NoSuchObjectLayoutColumnException</code> if it could not be found.
	 *
	 * @param objectLayoutColumnId the primary key of the object layout column
	 * @return the object layout column
	 * @throws NoSuchObjectLayoutColumnException if a object layout column with the primary key could not be found
	 */
	@Override
	public ObjectLayoutColumn findByPrimaryKey(long objectLayoutColumnId)
		throws NoSuchObjectLayoutColumnException {

		return findByPrimaryKey((Serializable)objectLayoutColumnId);
	}

	/**
	 * Returns the object layout column with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectLayoutColumnId the primary key of the object layout column
	 * @return the object layout column, or <code>null</code> if a object layout column with the primary key could not be found
	 */
	@Override
	public ObjectLayoutColumn fetchByPrimaryKey(long objectLayoutColumnId) {
		return fetchByPrimaryKey((Serializable)objectLayoutColumnId);
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
		return "objectLayoutColumnId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTLAYOUTCOLUMN;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectLayoutColumnModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object layout column persistence.
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
			_SQL_SELECT_OBJECTLAYOUTCOLUMN_WHERE,
			_SQL_COUNT_OBJECTLAYOUTCOLUMN_WHERE,
			ObjectLayoutColumnModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"objectLayoutColumn.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectLayoutColumn::getUuid));

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
				_SQL_SELECT_OBJECTLAYOUTCOLUMN_WHERE,
				_SQL_COUNT_OBJECTLAYOUTCOLUMN_WHERE,
				ObjectLayoutColumnModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"objectLayoutColumn.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectLayoutColumn::getUuid),
				new FinderColumn<>(
					"objectLayoutColumn.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectLayoutColumn::getCompanyId));

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
				_SQL_SELECT_OBJECTLAYOUTCOLUMN_WHERE,
				_SQL_COUNT_OBJECTLAYOUTCOLUMN_WHERE,
				ObjectLayoutColumnModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"objectLayoutColumn.", "objectFieldId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectLayoutColumn::getObjectFieldId));

		_collectionPersistenceFinderByObjectLayoutRowId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByObjectLayoutRowId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectLayoutRowId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByObjectLayoutRowId",
					new String[] {Long.class.getName()},
					new String[] {"objectLayoutRowId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByObjectLayoutRowId",
					new String[] {Long.class.getName()},
					new String[] {"objectLayoutRowId"}, false),
				_SQL_SELECT_OBJECTLAYOUTCOLUMN_WHERE,
				_SQL_COUNT_OBJECTLAYOUTCOLUMN_WHERE,
				ObjectLayoutColumnModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"objectLayoutColumn.", "objectLayoutRowId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectLayoutColumn::getObjectLayoutRowId));

		ObjectLayoutColumnUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectLayoutColumnUtil.setPersistence(null);

		entityCache.removeCache(ObjectLayoutColumnImpl.class.getName());
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
		ObjectLayoutColumnModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTLAYOUTCOLUMN =
		"SELECT objectLayoutColumn FROM ObjectLayoutColumn objectLayoutColumn";

	private static final String _SQL_SELECT_OBJECTLAYOUTCOLUMN_WHERE =
		"SELECT objectLayoutColumn FROM ObjectLayoutColumn objectLayoutColumn WHERE ";

	private static final String _SQL_COUNT_OBJECTLAYOUTCOLUMN_WHERE =
		"SELECT COUNT(objectLayoutColumn) FROM ObjectLayoutColumn objectLayoutColumn WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "size"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1493432866