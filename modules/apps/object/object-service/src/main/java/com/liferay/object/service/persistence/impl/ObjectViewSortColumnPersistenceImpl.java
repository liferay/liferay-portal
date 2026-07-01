/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectViewSortColumnException;
import com.liferay.object.model.ObjectViewSortColumn;
import com.liferay.object.model.ObjectViewSortColumnTable;
import com.liferay.object.model.impl.ObjectViewSortColumnImpl;
import com.liferay.object.model.impl.ObjectViewSortColumnModelImpl;
import com.liferay.object.service.persistence.ObjectViewSortColumnPersistence;
import com.liferay.object.service.persistence.ObjectViewSortColumnUtil;
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
 * The persistence implementation for the object view sort column service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectViewSortColumnPersistence.class)
public class ObjectViewSortColumnPersistenceImpl
	extends BasePersistenceImpl
		<ObjectViewSortColumn, NoSuchObjectViewSortColumnException>
	implements ObjectViewSortColumnPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectViewSortColumnUtil</code> to access the object view sort column persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectViewSortColumnImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ObjectViewSortColumn, NoSuchObjectViewSortColumnException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object view sort columns where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectViewSortColumnModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object view sort columns
	 * @param end the upper bound of the range of object view sort columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object view sort columns
	 */
	@Override
	public List<ObjectViewSortColumn> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectViewSortColumn> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object view sort column in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view sort column
	 * @throws NoSuchObjectViewSortColumnException if a matching object view sort column could not be found
	 */
	@Override
	public ObjectViewSortColumn findByUuid_First(
			String uuid,
			OrderByComparator<ObjectViewSortColumn> orderByComparator)
		throws NoSuchObjectViewSortColumnException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object view sort column in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view sort column, or <code>null</code> if a matching object view sort column could not be found
	 */
	@Override
	public ObjectViewSortColumn fetchByUuid_First(
		String uuid,
		OrderByComparator<ObjectViewSortColumn> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object view sort columns where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object view sort columns where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object view sort columns
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ObjectViewSortColumn, NoSuchObjectViewSortColumnException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object view sort columns where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectViewSortColumnModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object view sort columns
	 * @param end the upper bound of the range of object view sort columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object view sort columns
	 */
	@Override
	public List<ObjectViewSortColumn> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectViewSortColumn> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object view sort column in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view sort column
	 * @throws NoSuchObjectViewSortColumnException if a matching object view sort column could not be found
	 */
	@Override
	public ObjectViewSortColumn findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectViewSortColumn> orderByComparator)
		throws NoSuchObjectViewSortColumnException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object view sort column in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view sort column, or <code>null</code> if a matching object view sort column could not be found
	 */
	@Override
	public ObjectViewSortColumn fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectViewSortColumn> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object view sort columns where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object view sort columns where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object view sort columns
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ObjectViewSortColumn, NoSuchObjectViewSortColumnException>
			_collectionPersistenceFinderByObjectViewId;

	/**
	 * Returns an ordered range of all the object view sort columns where objectViewId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectViewSortColumnModelImpl</code>.
	 * </p>
	 *
	 * @param objectViewId the object view ID
	 * @param start the lower bound of the range of object view sort columns
	 * @param end the upper bound of the range of object view sort columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object view sort columns
	 */
	@Override
	public List<ObjectViewSortColumn> findByObjectViewId(
		long objectViewId, int start, int end,
		OrderByComparator<ObjectViewSortColumn> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectViewId.find(
			finderCache, new Object[] {objectViewId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object view sort column in the ordered set where objectViewId = &#63;.
	 *
	 * @param objectViewId the object view ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view sort column
	 * @throws NoSuchObjectViewSortColumnException if a matching object view sort column could not be found
	 */
	@Override
	public ObjectViewSortColumn findByObjectViewId_First(
			long objectViewId,
			OrderByComparator<ObjectViewSortColumn> orderByComparator)
		throws NoSuchObjectViewSortColumnException {

		return _collectionPersistenceFinderByObjectViewId.findFirst(
			finderCache, new Object[] {objectViewId}, orderByComparator);
	}

	/**
	 * Returns the first object view sort column in the ordered set where objectViewId = &#63;.
	 *
	 * @param objectViewId the object view ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view sort column, or <code>null</code> if a matching object view sort column could not be found
	 */
	@Override
	public ObjectViewSortColumn fetchByObjectViewId_First(
		long objectViewId,
		OrderByComparator<ObjectViewSortColumn> orderByComparator) {

		return _collectionPersistenceFinderByObjectViewId.fetchFirst(
			finderCache, new Object[] {objectViewId}, orderByComparator);
	}

	/**
	 * Removes all the object view sort columns where objectViewId = &#63; from the database.
	 *
	 * @param objectViewId the object view ID
	 */
	@Override
	public void removeByObjectViewId(long objectViewId) {
		_collectionPersistenceFinderByObjectViewId.remove(
			finderCache, new Object[] {objectViewId});
	}

	/**
	 * Returns the number of object view sort columns where objectViewId = &#63;.
	 *
	 * @param objectViewId the object view ID
	 * @return the number of matching object view sort columns
	 */
	@Override
	public int countByObjectViewId(long objectViewId) {
		return _collectionPersistenceFinderByObjectViewId.count(
			finderCache, new Object[] {objectViewId});
	}

	private CollectionPersistenceFinder
		<ObjectViewSortColumn, NoSuchObjectViewSortColumnException>
			_collectionPersistenceFinderByOVI_OFN;

	/**
	 * Returns an ordered range of all the object view sort columns where objectViewId = &#63; and objectFieldName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectViewSortColumnModelImpl</code>.
	 * </p>
	 *
	 * @param objectViewId the object view ID
	 * @param objectFieldName the object field name
	 * @param start the lower bound of the range of object view sort columns
	 * @param end the upper bound of the range of object view sort columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object view sort columns
	 */
	@Override
	public List<ObjectViewSortColumn> findByOVI_OFN(
		long objectViewId, String objectFieldName, int start, int end,
		OrderByComparator<ObjectViewSortColumn> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByOVI_OFN.find(
			finderCache, new Object[] {objectViewId, objectFieldName}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object view sort column in the ordered set where objectViewId = &#63; and objectFieldName = &#63;.
	 *
	 * @param objectViewId the object view ID
	 * @param objectFieldName the object field name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view sort column
	 * @throws NoSuchObjectViewSortColumnException if a matching object view sort column could not be found
	 */
	@Override
	public ObjectViewSortColumn findByOVI_OFN_First(
			long objectViewId, String objectFieldName,
			OrderByComparator<ObjectViewSortColumn> orderByComparator)
		throws NoSuchObjectViewSortColumnException {

		return _collectionPersistenceFinderByOVI_OFN.findFirst(
			finderCache, new Object[] {objectViewId, objectFieldName},
			orderByComparator);
	}

	/**
	 * Returns the first object view sort column in the ordered set where objectViewId = &#63; and objectFieldName = &#63;.
	 *
	 * @param objectViewId the object view ID
	 * @param objectFieldName the object field name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view sort column, or <code>null</code> if a matching object view sort column could not be found
	 */
	@Override
	public ObjectViewSortColumn fetchByOVI_OFN_First(
		long objectViewId, String objectFieldName,
		OrderByComparator<ObjectViewSortColumn> orderByComparator) {

		return _collectionPersistenceFinderByOVI_OFN.fetchFirst(
			finderCache, new Object[] {objectViewId, objectFieldName},
			orderByComparator);
	}

	/**
	 * Removes all the object view sort columns where objectViewId = &#63; and objectFieldName = &#63; from the database.
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
	 * Returns the number of object view sort columns where objectViewId = &#63; and objectFieldName = &#63;.
	 *
	 * @param objectViewId the object view ID
	 * @param objectFieldName the object field name
	 * @return the number of matching object view sort columns
	 */
	@Override
	public int countByOVI_OFN(long objectViewId, String objectFieldName) {
		return _collectionPersistenceFinderByOVI_OFN.count(
			finderCache, new Object[] {objectViewId, objectFieldName});
	}

	public ObjectViewSortColumnPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectViewSortColumn.class);

		setModelImplClass(ObjectViewSortColumnImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectViewSortColumnTable.INSTANCE);
	}

	/**
	 * Creates a new object view sort column with the primary key. Does not add the object view sort column to the database.
	 *
	 * @param objectViewSortColumnId the primary key for the new object view sort column
	 * @return the new object view sort column
	 */
	@Override
	public ObjectViewSortColumn create(long objectViewSortColumnId) {
		ObjectViewSortColumn objectViewSortColumn =
			new ObjectViewSortColumnImpl();

		objectViewSortColumn.setNew(true);
		objectViewSortColumn.setPrimaryKey(objectViewSortColumnId);

		String uuid = PortalUUIDUtil.generate();

		objectViewSortColumn.setUuid(uuid);

		objectViewSortColumn.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectViewSortColumn;
	}

	/**
	 * Removes the object view sort column with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectViewSortColumnId the primary key of the object view sort column
	 * @return the object view sort column that was removed
	 * @throws NoSuchObjectViewSortColumnException if a object view sort column with the primary key could not be found
	 */
	@Override
	public ObjectViewSortColumn remove(long objectViewSortColumnId)
		throws NoSuchObjectViewSortColumnException {

		return remove((Serializable)objectViewSortColumnId);
	}

	@Override
	protected ObjectViewSortColumn removeImpl(
		ObjectViewSortColumn objectViewSortColumn) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectViewSortColumn)) {
				objectViewSortColumn = (ObjectViewSortColumn)session.get(
					ObjectViewSortColumnImpl.class,
					objectViewSortColumn.getPrimaryKeyObj());
			}

			if (objectViewSortColumn != null) {
				session.delete(objectViewSortColumn);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectViewSortColumn != null) {
			clearCache(objectViewSortColumn);
		}

		return objectViewSortColumn;
	}

	@Override
	public ObjectViewSortColumn updateImpl(
		ObjectViewSortColumn objectViewSortColumn) {

		boolean isNew = objectViewSortColumn.isNew();

		if (!(objectViewSortColumn instanceof ObjectViewSortColumnModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectViewSortColumn.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectViewSortColumn);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectViewSortColumn proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectViewSortColumn implementation " +
					objectViewSortColumn.getClass());
		}

		ObjectViewSortColumnModelImpl objectViewSortColumnModelImpl =
			(ObjectViewSortColumnModelImpl)objectViewSortColumn;

		if (Validator.isNull(objectViewSortColumn.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectViewSortColumn.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectViewSortColumn.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectViewSortColumn.setCreateDate(date);
			}
			else {
				objectViewSortColumn.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectViewSortColumnModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectViewSortColumn.setModifiedDate(date);
			}
			else {
				objectViewSortColumn.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectViewSortColumn);
			}
			else {
				objectViewSortColumn = (ObjectViewSortColumn)session.merge(
					objectViewSortColumn);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectViewSortColumn, false);

		if (isNew) {
			objectViewSortColumn.setNew(false);
		}

		objectViewSortColumn.resetOriginalValues();

		return objectViewSortColumn;
	}

	/**
	 * Returns the object view sort column with the primary key or throws a <code>NoSuchObjectViewSortColumnException</code> if it could not be found.
	 *
	 * @param objectViewSortColumnId the primary key of the object view sort column
	 * @return the object view sort column
	 * @throws NoSuchObjectViewSortColumnException if a object view sort column with the primary key could not be found
	 */
	@Override
	public ObjectViewSortColumn findByPrimaryKey(long objectViewSortColumnId)
		throws NoSuchObjectViewSortColumnException {

		return findByPrimaryKey((Serializable)objectViewSortColumnId);
	}

	/**
	 * Returns the object view sort column with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectViewSortColumnId the primary key of the object view sort column
	 * @return the object view sort column, or <code>null</code> if a object view sort column with the primary key could not be found
	 */
	@Override
	public ObjectViewSortColumn fetchByPrimaryKey(long objectViewSortColumnId) {
		return fetchByPrimaryKey((Serializable)objectViewSortColumnId);
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
		return "objectViewSortColumnId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTVIEWSORTCOLUMN;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectViewSortColumnModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object view sort column persistence.
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
			_SQL_SELECT_OBJECTVIEWSORTCOLUMN_WHERE,
			_SQL_COUNT_OBJECTVIEWSORTCOLUMN_WHERE,
			ObjectViewSortColumnModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"objectViewSortColumn.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectViewSortColumn::getUuid));

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
				_SQL_SELECT_OBJECTVIEWSORTCOLUMN_WHERE,
				_SQL_COUNT_OBJECTVIEWSORTCOLUMN_WHERE,
				ObjectViewSortColumnModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"objectViewSortColumn.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectViewSortColumn::getUuid),
				new FinderColumn<>(
					"objectViewSortColumn.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectViewSortColumn::getCompanyId));

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
				_SQL_SELECT_OBJECTVIEWSORTCOLUMN_WHERE,
				_SQL_COUNT_OBJECTVIEWSORTCOLUMN_WHERE,
				ObjectViewSortColumnModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"objectViewSortColumn.", "objectViewId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectViewSortColumn::getObjectViewId));

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
				_SQL_SELECT_OBJECTVIEWSORTCOLUMN_WHERE,
				_SQL_COUNT_OBJECTVIEWSORTCOLUMN_WHERE,
				ObjectViewSortColumnModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"objectViewSortColumn.", "objectViewId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectViewSortColumn::getObjectViewId),
				new FinderColumn<>(
					"objectViewSortColumn.", "objectFieldName",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectViewSortColumn::getObjectFieldName));

		ObjectViewSortColumnUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectViewSortColumnUtil.setPersistence(null);

		entityCache.removeCache(ObjectViewSortColumnImpl.class.getName());
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
		ObjectViewSortColumnModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTVIEWSORTCOLUMN =
		"SELECT objectViewSortColumn FROM ObjectViewSortColumn objectViewSortColumn";

	private static final String _SQL_SELECT_OBJECTVIEWSORTCOLUMN_WHERE =
		"SELECT objectViewSortColumn FROM ObjectViewSortColumn objectViewSortColumn WHERE ";

	private static final String _SQL_COUNT_OBJECTVIEWSORTCOLUMN_WHERE =
		"SELECT COUNT(objectViewSortColumn) FROM ObjectViewSortColumn objectViewSortColumn WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-709731140