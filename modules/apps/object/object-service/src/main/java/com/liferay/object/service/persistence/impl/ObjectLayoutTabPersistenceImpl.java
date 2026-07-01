/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectLayoutTabException;
import com.liferay.object.model.ObjectLayoutTab;
import com.liferay.object.model.ObjectLayoutTabTable;
import com.liferay.object.model.impl.ObjectLayoutTabImpl;
import com.liferay.object.model.impl.ObjectLayoutTabModelImpl;
import com.liferay.object.service.persistence.ObjectLayoutTabPersistence;
import com.liferay.object.service.persistence.ObjectLayoutTabUtil;
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
 * The persistence implementation for the object layout tab service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectLayoutTabPersistence.class)
public class ObjectLayoutTabPersistenceImpl
	extends BasePersistenceImpl<ObjectLayoutTab, NoSuchObjectLayoutTabException>
	implements ObjectLayoutTabPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectLayoutTabUtil</code> to access the object layout tab persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectLayoutTabImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ObjectLayoutTab, NoSuchObjectLayoutTabException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object layout tabs where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutTabModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object layout tabs
	 * @param end the upper bound of the range of object layout tabs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layout tabs
	 */
	@Override
	public List<ObjectLayoutTab> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectLayoutTab> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object layout tab in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout tab
	 * @throws NoSuchObjectLayoutTabException if a matching object layout tab could not be found
	 */
	@Override
	public ObjectLayoutTab findByUuid_First(
			String uuid, OrderByComparator<ObjectLayoutTab> orderByComparator)
		throws NoSuchObjectLayoutTabException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object layout tab in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout tab, or <code>null</code> if a matching object layout tab could not be found
	 */
	@Override
	public ObjectLayoutTab fetchByUuid_First(
		String uuid, OrderByComparator<ObjectLayoutTab> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object layout tabs where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object layout tabs where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object layout tabs
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ObjectLayoutTab, NoSuchObjectLayoutTabException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object layout tabs where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutTabModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object layout tabs
	 * @param end the upper bound of the range of object layout tabs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layout tabs
	 */
	@Override
	public List<ObjectLayoutTab> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectLayoutTab> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object layout tab in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout tab
	 * @throws NoSuchObjectLayoutTabException if a matching object layout tab could not be found
	 */
	@Override
	public ObjectLayoutTab findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectLayoutTab> orderByComparator)
		throws NoSuchObjectLayoutTabException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object layout tab in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout tab, or <code>null</code> if a matching object layout tab could not be found
	 */
	@Override
	public ObjectLayoutTab fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectLayoutTab> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object layout tabs where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object layout tabs where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object layout tabs
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ObjectLayoutTab, NoSuchObjectLayoutTabException>
			_collectionPersistenceFinderByObjectLayoutId;

	/**
	 * Returns an ordered range of all the object layout tabs where objectLayoutId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutTabModelImpl</code>.
	 * </p>
	 *
	 * @param objectLayoutId the object layout ID
	 * @param start the lower bound of the range of object layout tabs
	 * @param end the upper bound of the range of object layout tabs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layout tabs
	 */
	@Override
	public List<ObjectLayoutTab> findByObjectLayoutId(
		long objectLayoutId, int start, int end,
		OrderByComparator<ObjectLayoutTab> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectLayoutId.find(
			finderCache, new Object[] {objectLayoutId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object layout tab in the ordered set where objectLayoutId = &#63;.
	 *
	 * @param objectLayoutId the object layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout tab
	 * @throws NoSuchObjectLayoutTabException if a matching object layout tab could not be found
	 */
	@Override
	public ObjectLayoutTab findByObjectLayoutId_First(
			long objectLayoutId,
			OrderByComparator<ObjectLayoutTab> orderByComparator)
		throws NoSuchObjectLayoutTabException {

		return _collectionPersistenceFinderByObjectLayoutId.findFirst(
			finderCache, new Object[] {objectLayoutId}, orderByComparator);
	}

	/**
	 * Returns the first object layout tab in the ordered set where objectLayoutId = &#63;.
	 *
	 * @param objectLayoutId the object layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout tab, or <code>null</code> if a matching object layout tab could not be found
	 */
	@Override
	public ObjectLayoutTab fetchByObjectLayoutId_First(
		long objectLayoutId,
		OrderByComparator<ObjectLayoutTab> orderByComparator) {

		return _collectionPersistenceFinderByObjectLayoutId.fetchFirst(
			finderCache, new Object[] {objectLayoutId}, orderByComparator);
	}

	/**
	 * Removes all the object layout tabs where objectLayoutId = &#63; from the database.
	 *
	 * @param objectLayoutId the object layout ID
	 */
	@Override
	public void removeByObjectLayoutId(long objectLayoutId) {
		_collectionPersistenceFinderByObjectLayoutId.remove(
			finderCache, new Object[] {objectLayoutId});
	}

	/**
	 * Returns the number of object layout tabs where objectLayoutId = &#63;.
	 *
	 * @param objectLayoutId the object layout ID
	 * @return the number of matching object layout tabs
	 */
	@Override
	public int countByObjectLayoutId(long objectLayoutId) {
		return _collectionPersistenceFinderByObjectLayoutId.count(
			finderCache, new Object[] {objectLayoutId});
	}

	private CollectionPersistenceFinder
		<ObjectLayoutTab, NoSuchObjectLayoutTabException>
			_collectionPersistenceFinderByObjectRelationshipId;

	/**
	 * Returns an ordered range of all the object layout tabs where objectRelationshipId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutTabModelImpl</code>.
	 * </p>
	 *
	 * @param objectRelationshipId the object relationship ID
	 * @param start the lower bound of the range of object layout tabs
	 * @param end the upper bound of the range of object layout tabs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layout tabs
	 */
	@Override
	public List<ObjectLayoutTab> findByObjectRelationshipId(
		long objectRelationshipId, int start, int end,
		OrderByComparator<ObjectLayoutTab> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectRelationshipId.find(
			finderCache, new Object[] {objectRelationshipId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object layout tab in the ordered set where objectRelationshipId = &#63;.
	 *
	 * @param objectRelationshipId the object relationship ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout tab
	 * @throws NoSuchObjectLayoutTabException if a matching object layout tab could not be found
	 */
	@Override
	public ObjectLayoutTab findByObjectRelationshipId_First(
			long objectRelationshipId,
			OrderByComparator<ObjectLayoutTab> orderByComparator)
		throws NoSuchObjectLayoutTabException {

		return _collectionPersistenceFinderByObjectRelationshipId.findFirst(
			finderCache, new Object[] {objectRelationshipId},
			orderByComparator);
	}

	/**
	 * Returns the first object layout tab in the ordered set where objectRelationshipId = &#63;.
	 *
	 * @param objectRelationshipId the object relationship ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout tab, or <code>null</code> if a matching object layout tab could not be found
	 */
	@Override
	public ObjectLayoutTab fetchByObjectRelationshipId_First(
		long objectRelationshipId,
		OrderByComparator<ObjectLayoutTab> orderByComparator) {

		return _collectionPersistenceFinderByObjectRelationshipId.fetchFirst(
			finderCache, new Object[] {objectRelationshipId},
			orderByComparator);
	}

	/**
	 * Removes all the object layout tabs where objectRelationshipId = &#63; from the database.
	 *
	 * @param objectRelationshipId the object relationship ID
	 */
	@Override
	public void removeByObjectRelationshipId(long objectRelationshipId) {
		_collectionPersistenceFinderByObjectRelationshipId.remove(
			finderCache, new Object[] {objectRelationshipId});
	}

	/**
	 * Returns the number of object layout tabs where objectRelationshipId = &#63;.
	 *
	 * @param objectRelationshipId the object relationship ID
	 * @return the number of matching object layout tabs
	 */
	@Override
	public int countByObjectRelationshipId(long objectRelationshipId) {
		return _collectionPersistenceFinderByObjectRelationshipId.count(
			finderCache, new Object[] {objectRelationshipId});
	}

	public ObjectLayoutTabPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectLayoutTab.class);

		setModelImplClass(ObjectLayoutTabImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectLayoutTabTable.INSTANCE);
	}

	/**
	 * Creates a new object layout tab with the primary key. Does not add the object layout tab to the database.
	 *
	 * @param objectLayoutTabId the primary key for the new object layout tab
	 * @return the new object layout tab
	 */
	@Override
	public ObjectLayoutTab create(long objectLayoutTabId) {
		ObjectLayoutTab objectLayoutTab = new ObjectLayoutTabImpl();

		objectLayoutTab.setNew(true);
		objectLayoutTab.setPrimaryKey(objectLayoutTabId);

		String uuid = PortalUUIDUtil.generate();

		objectLayoutTab.setUuid(uuid);

		objectLayoutTab.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectLayoutTab;
	}

	/**
	 * Removes the object layout tab with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectLayoutTabId the primary key of the object layout tab
	 * @return the object layout tab that was removed
	 * @throws NoSuchObjectLayoutTabException if a object layout tab with the primary key could not be found
	 */
	@Override
	public ObjectLayoutTab remove(long objectLayoutTabId)
		throws NoSuchObjectLayoutTabException {

		return remove((Serializable)objectLayoutTabId);
	}

	@Override
	protected ObjectLayoutTab removeImpl(ObjectLayoutTab objectLayoutTab) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectLayoutTab)) {
				objectLayoutTab = (ObjectLayoutTab)session.get(
					ObjectLayoutTabImpl.class,
					objectLayoutTab.getPrimaryKeyObj());
			}

			if (objectLayoutTab != null) {
				session.delete(objectLayoutTab);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectLayoutTab != null) {
			clearCache(objectLayoutTab);
		}

		return objectLayoutTab;
	}

	@Override
	public ObjectLayoutTab updateImpl(ObjectLayoutTab objectLayoutTab) {
		boolean isNew = objectLayoutTab.isNew();

		if (!(objectLayoutTab instanceof ObjectLayoutTabModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectLayoutTab.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectLayoutTab);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectLayoutTab proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectLayoutTab implementation " +
					objectLayoutTab.getClass());
		}

		ObjectLayoutTabModelImpl objectLayoutTabModelImpl =
			(ObjectLayoutTabModelImpl)objectLayoutTab;

		if (Validator.isNull(objectLayoutTab.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectLayoutTab.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectLayoutTab.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectLayoutTab.setCreateDate(date);
			}
			else {
				objectLayoutTab.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectLayoutTabModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectLayoutTab.setModifiedDate(date);
			}
			else {
				objectLayoutTab.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectLayoutTab);
			}
			else {
				objectLayoutTab = (ObjectLayoutTab)session.merge(
					objectLayoutTab);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectLayoutTab, false);

		if (isNew) {
			objectLayoutTab.setNew(false);
		}

		objectLayoutTab.resetOriginalValues();

		return objectLayoutTab;
	}

	/**
	 * Returns the object layout tab with the primary key or throws a <code>NoSuchObjectLayoutTabException</code> if it could not be found.
	 *
	 * @param objectLayoutTabId the primary key of the object layout tab
	 * @return the object layout tab
	 * @throws NoSuchObjectLayoutTabException if a object layout tab with the primary key could not be found
	 */
	@Override
	public ObjectLayoutTab findByPrimaryKey(long objectLayoutTabId)
		throws NoSuchObjectLayoutTabException {

		return findByPrimaryKey((Serializable)objectLayoutTabId);
	}

	/**
	 * Returns the object layout tab with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectLayoutTabId the primary key of the object layout tab
	 * @return the object layout tab, or <code>null</code> if a object layout tab with the primary key could not be found
	 */
	@Override
	public ObjectLayoutTab fetchByPrimaryKey(long objectLayoutTabId) {
		return fetchByPrimaryKey((Serializable)objectLayoutTabId);
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
		return "objectLayoutTabId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTLAYOUTTAB;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectLayoutTabModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object layout tab persistence.
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
			_SQL_SELECT_OBJECTLAYOUTTAB_WHERE, _SQL_COUNT_OBJECTLAYOUTTAB_WHERE,
			ObjectLayoutTabModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"objectLayoutTab.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, ObjectLayoutTab::getUuid));

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
				_SQL_SELECT_OBJECTLAYOUTTAB_WHERE,
				_SQL_COUNT_OBJECTLAYOUTTAB_WHERE,
				ObjectLayoutTabModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectLayoutTab.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectLayoutTab::getUuid),
				new FinderColumn<>(
					"objectLayoutTab.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectLayoutTab::getCompanyId));

		_collectionPersistenceFinderByObjectLayoutId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByObjectLayoutId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectLayoutId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByObjectLayoutId", new String[] {Long.class.getName()},
					new String[] {"objectLayoutId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByObjectLayoutId",
					new String[] {Long.class.getName()},
					new String[] {"objectLayoutId"}, false),
				_SQL_SELECT_OBJECTLAYOUTTAB_WHERE,
				_SQL_COUNT_OBJECTLAYOUTTAB_WHERE,
				ObjectLayoutTabModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectLayoutTab.", "objectLayoutId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectLayoutTab::getObjectLayoutId));

		_collectionPersistenceFinderByObjectRelationshipId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByObjectRelationshipId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectRelationshipId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByObjectRelationshipId",
					new String[] {Long.class.getName()},
					new String[] {"objectRelationshipId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByObjectRelationshipId",
					new String[] {Long.class.getName()},
					new String[] {"objectRelationshipId"}, false),
				_SQL_SELECT_OBJECTLAYOUTTAB_WHERE,
				_SQL_COUNT_OBJECTLAYOUTTAB_WHERE,
				ObjectLayoutTabModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectLayoutTab.", "objectRelationshipId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectLayoutTab::getObjectRelationshipId));

		ObjectLayoutTabUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectLayoutTabUtil.setPersistence(null);

		entityCache.removeCache(ObjectLayoutTabImpl.class.getName());
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
		ObjectLayoutTabModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTLAYOUTTAB =
		"SELECT objectLayoutTab FROM ObjectLayoutTab objectLayoutTab";

	private static final String _SQL_SELECT_OBJECTLAYOUTTAB_WHERE =
		"SELECT objectLayoutTab FROM ObjectLayoutTab objectLayoutTab WHERE ";

	private static final String _SQL_COUNT_OBJECTLAYOUTTAB_WHERE =
		"SELECT COUNT(objectLayoutTab) FROM ObjectLayoutTab objectLayoutTab WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ObjectLayoutTab exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1107467076