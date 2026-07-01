/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectLayoutRowException;
import com.liferay.object.model.ObjectLayoutRow;
import com.liferay.object.model.ObjectLayoutRowTable;
import com.liferay.object.model.impl.ObjectLayoutRowImpl;
import com.liferay.object.model.impl.ObjectLayoutRowModelImpl;
import com.liferay.object.service.persistence.ObjectLayoutRowPersistence;
import com.liferay.object.service.persistence.ObjectLayoutRowUtil;
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
 * The persistence implementation for the object layout row service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectLayoutRowPersistence.class)
public class ObjectLayoutRowPersistenceImpl
	extends BasePersistenceImpl<ObjectLayoutRow, NoSuchObjectLayoutRowException>
	implements ObjectLayoutRowPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectLayoutRowUtil</code> to access the object layout row persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectLayoutRowImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ObjectLayoutRow, NoSuchObjectLayoutRowException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object layout rows where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutRowModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object layout rows
	 * @param end the upper bound of the range of object layout rows (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layout rows
	 */
	@Override
	public List<ObjectLayoutRow> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectLayoutRow> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object layout row in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout row
	 * @throws NoSuchObjectLayoutRowException if a matching object layout row could not be found
	 */
	@Override
	public ObjectLayoutRow findByUuid_First(
			String uuid, OrderByComparator<ObjectLayoutRow> orderByComparator)
		throws NoSuchObjectLayoutRowException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object layout row in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout row, or <code>null</code> if a matching object layout row could not be found
	 */
	@Override
	public ObjectLayoutRow fetchByUuid_First(
		String uuid, OrderByComparator<ObjectLayoutRow> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object layout rows where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object layout rows where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object layout rows
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ObjectLayoutRow, NoSuchObjectLayoutRowException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object layout rows where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutRowModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object layout rows
	 * @param end the upper bound of the range of object layout rows (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layout rows
	 */
	@Override
	public List<ObjectLayoutRow> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectLayoutRow> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object layout row in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout row
	 * @throws NoSuchObjectLayoutRowException if a matching object layout row could not be found
	 */
	@Override
	public ObjectLayoutRow findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectLayoutRow> orderByComparator)
		throws NoSuchObjectLayoutRowException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object layout row in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout row, or <code>null</code> if a matching object layout row could not be found
	 */
	@Override
	public ObjectLayoutRow fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectLayoutRow> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object layout rows where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object layout rows where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object layout rows
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ObjectLayoutRow, NoSuchObjectLayoutRowException>
			_collectionPersistenceFinderByObjectLayoutBoxId;

	/**
	 * Returns an ordered range of all the object layout rows where objectLayoutBoxId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutRowModelImpl</code>.
	 * </p>
	 *
	 * @param objectLayoutBoxId the object layout box ID
	 * @param start the lower bound of the range of object layout rows
	 * @param end the upper bound of the range of object layout rows (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layout rows
	 */
	@Override
	public List<ObjectLayoutRow> findByObjectLayoutBoxId(
		long objectLayoutBoxId, int start, int end,
		OrderByComparator<ObjectLayoutRow> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectLayoutBoxId.find(
			finderCache, new Object[] {objectLayoutBoxId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object layout row in the ordered set where objectLayoutBoxId = &#63;.
	 *
	 * @param objectLayoutBoxId the object layout box ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout row
	 * @throws NoSuchObjectLayoutRowException if a matching object layout row could not be found
	 */
	@Override
	public ObjectLayoutRow findByObjectLayoutBoxId_First(
			long objectLayoutBoxId,
			OrderByComparator<ObjectLayoutRow> orderByComparator)
		throws NoSuchObjectLayoutRowException {

		return _collectionPersistenceFinderByObjectLayoutBoxId.findFirst(
			finderCache, new Object[] {objectLayoutBoxId}, orderByComparator);
	}

	/**
	 * Returns the first object layout row in the ordered set where objectLayoutBoxId = &#63;.
	 *
	 * @param objectLayoutBoxId the object layout box ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout row, or <code>null</code> if a matching object layout row could not be found
	 */
	@Override
	public ObjectLayoutRow fetchByObjectLayoutBoxId_First(
		long objectLayoutBoxId,
		OrderByComparator<ObjectLayoutRow> orderByComparator) {

		return _collectionPersistenceFinderByObjectLayoutBoxId.fetchFirst(
			finderCache, new Object[] {objectLayoutBoxId}, orderByComparator);
	}

	/**
	 * Removes all the object layout rows where objectLayoutBoxId = &#63; from the database.
	 *
	 * @param objectLayoutBoxId the object layout box ID
	 */
	@Override
	public void removeByObjectLayoutBoxId(long objectLayoutBoxId) {
		_collectionPersistenceFinderByObjectLayoutBoxId.remove(
			finderCache, new Object[] {objectLayoutBoxId});
	}

	/**
	 * Returns the number of object layout rows where objectLayoutBoxId = &#63;.
	 *
	 * @param objectLayoutBoxId the object layout box ID
	 * @return the number of matching object layout rows
	 */
	@Override
	public int countByObjectLayoutBoxId(long objectLayoutBoxId) {
		return _collectionPersistenceFinderByObjectLayoutBoxId.count(
			finderCache, new Object[] {objectLayoutBoxId});
	}

	public ObjectLayoutRowPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectLayoutRow.class);

		setModelImplClass(ObjectLayoutRowImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectLayoutRowTable.INSTANCE);
	}

	/**
	 * Creates a new object layout row with the primary key. Does not add the object layout row to the database.
	 *
	 * @param objectLayoutRowId the primary key for the new object layout row
	 * @return the new object layout row
	 */
	@Override
	public ObjectLayoutRow create(long objectLayoutRowId) {
		ObjectLayoutRow objectLayoutRow = new ObjectLayoutRowImpl();

		objectLayoutRow.setNew(true);
		objectLayoutRow.setPrimaryKey(objectLayoutRowId);

		String uuid = PortalUUIDUtil.generate();

		objectLayoutRow.setUuid(uuid);

		objectLayoutRow.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectLayoutRow;
	}

	/**
	 * Removes the object layout row with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectLayoutRowId the primary key of the object layout row
	 * @return the object layout row that was removed
	 * @throws NoSuchObjectLayoutRowException if a object layout row with the primary key could not be found
	 */
	@Override
	public ObjectLayoutRow remove(long objectLayoutRowId)
		throws NoSuchObjectLayoutRowException {

		return remove((Serializable)objectLayoutRowId);
	}

	@Override
	protected ObjectLayoutRow removeImpl(ObjectLayoutRow objectLayoutRow) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectLayoutRow)) {
				objectLayoutRow = (ObjectLayoutRow)session.get(
					ObjectLayoutRowImpl.class,
					objectLayoutRow.getPrimaryKeyObj());
			}

			if (objectLayoutRow != null) {
				session.delete(objectLayoutRow);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectLayoutRow != null) {
			clearCache(objectLayoutRow);
		}

		return objectLayoutRow;
	}

	@Override
	public ObjectLayoutRow updateImpl(ObjectLayoutRow objectLayoutRow) {
		boolean isNew = objectLayoutRow.isNew();

		if (!(objectLayoutRow instanceof ObjectLayoutRowModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectLayoutRow.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectLayoutRow);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectLayoutRow proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectLayoutRow implementation " +
					objectLayoutRow.getClass());
		}

		ObjectLayoutRowModelImpl objectLayoutRowModelImpl =
			(ObjectLayoutRowModelImpl)objectLayoutRow;

		if (Validator.isNull(objectLayoutRow.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectLayoutRow.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectLayoutRow.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectLayoutRow.setCreateDate(date);
			}
			else {
				objectLayoutRow.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectLayoutRowModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectLayoutRow.setModifiedDate(date);
			}
			else {
				objectLayoutRow.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectLayoutRow);
			}
			else {
				objectLayoutRow = (ObjectLayoutRow)session.merge(
					objectLayoutRow);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectLayoutRow, false);

		if (isNew) {
			objectLayoutRow.setNew(false);
		}

		objectLayoutRow.resetOriginalValues();

		return objectLayoutRow;
	}

	/**
	 * Returns the object layout row with the primary key or throws a <code>NoSuchObjectLayoutRowException</code> if it could not be found.
	 *
	 * @param objectLayoutRowId the primary key of the object layout row
	 * @return the object layout row
	 * @throws NoSuchObjectLayoutRowException if a object layout row with the primary key could not be found
	 */
	@Override
	public ObjectLayoutRow findByPrimaryKey(long objectLayoutRowId)
		throws NoSuchObjectLayoutRowException {

		return findByPrimaryKey((Serializable)objectLayoutRowId);
	}

	/**
	 * Returns the object layout row with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectLayoutRowId the primary key of the object layout row
	 * @return the object layout row, or <code>null</code> if a object layout row with the primary key could not be found
	 */
	@Override
	public ObjectLayoutRow fetchByPrimaryKey(long objectLayoutRowId) {
		return fetchByPrimaryKey((Serializable)objectLayoutRowId);
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
		return "objectLayoutRowId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTLAYOUTROW;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectLayoutRowModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object layout row persistence.
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
			_SQL_SELECT_OBJECTLAYOUTROW_WHERE, _SQL_COUNT_OBJECTLAYOUTROW_WHERE,
			ObjectLayoutRowModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"objectLayoutRow.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, ObjectLayoutRow::getUuid));

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
				_SQL_SELECT_OBJECTLAYOUTROW_WHERE,
				_SQL_COUNT_OBJECTLAYOUTROW_WHERE,
				ObjectLayoutRowModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectLayoutRow.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectLayoutRow::getUuid),
				new FinderColumn<>(
					"objectLayoutRow.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectLayoutRow::getCompanyId));

		_collectionPersistenceFinderByObjectLayoutBoxId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByObjectLayoutBoxId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectLayoutBoxId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByObjectLayoutBoxId",
					new String[] {Long.class.getName()},
					new String[] {"objectLayoutBoxId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByObjectLayoutBoxId",
					new String[] {Long.class.getName()},
					new String[] {"objectLayoutBoxId"}, false),
				_SQL_SELECT_OBJECTLAYOUTROW_WHERE,
				_SQL_COUNT_OBJECTLAYOUTROW_WHERE,
				ObjectLayoutRowModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectLayoutRow.", "objectLayoutBoxId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectLayoutRow::getObjectLayoutBoxId));

		ObjectLayoutRowUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectLayoutRowUtil.setPersistence(null);

		entityCache.removeCache(ObjectLayoutRowImpl.class.getName());
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
		ObjectLayoutRowModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTLAYOUTROW =
		"SELECT objectLayoutRow FROM ObjectLayoutRow objectLayoutRow";

	private static final String _SQL_SELECT_OBJECTLAYOUTROW_WHERE =
		"SELECT objectLayoutRow FROM ObjectLayoutRow objectLayoutRow WHERE ";

	private static final String _SQL_COUNT_OBJECTLAYOUTROW_WHERE =
		"SELECT COUNT(objectLayoutRow) FROM ObjectLayoutRow objectLayoutRow WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ObjectLayoutRow exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:672694982