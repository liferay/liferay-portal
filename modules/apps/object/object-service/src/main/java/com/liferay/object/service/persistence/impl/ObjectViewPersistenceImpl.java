/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectViewException;
import com.liferay.object.model.ObjectView;
import com.liferay.object.model.ObjectViewTable;
import com.liferay.object.model.impl.ObjectViewImpl;
import com.liferay.object.model.impl.ObjectViewModelImpl;
import com.liferay.object.service.persistence.ObjectViewPersistence;
import com.liferay.object.service.persistence.ObjectViewUtil;
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
 * The persistence implementation for the object view service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectViewPersistence.class)
public class ObjectViewPersistenceImpl
	extends BasePersistenceImpl<ObjectView, NoSuchObjectViewException>
	implements ObjectViewPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectViewUtil</code> to access the object view persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectViewImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<ObjectView, NoSuchObjectViewException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object views where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectViewModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object views
	 * @param end the upper bound of the range of object views (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object views
	 */
	@Override
	public List<ObjectView> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectView> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object view in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view
	 * @throws NoSuchObjectViewException if a matching object view could not be found
	 */
	@Override
	public ObjectView findByUuid_First(
			String uuid, OrderByComparator<ObjectView> orderByComparator)
		throws NoSuchObjectViewException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object view in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view, or <code>null</code> if a matching object view could not be found
	 */
	@Override
	public ObjectView fetchByUuid_First(
		String uuid, OrderByComparator<ObjectView> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object views where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object views where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object views
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder<ObjectView, NoSuchObjectViewException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object views where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectViewModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object views
	 * @param end the upper bound of the range of object views (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object views
	 */
	@Override
	public List<ObjectView> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectView> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object view in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view
	 * @throws NoSuchObjectViewException if a matching object view could not be found
	 */
	@Override
	public ObjectView findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectView> orderByComparator)
		throws NoSuchObjectViewException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object view in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view, or <code>null</code> if a matching object view could not be found
	 */
	@Override
	public ObjectView fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectView> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object views where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object views where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object views
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<ObjectView, NoSuchObjectViewException>
		_collectionPersistenceFinderByObjectDefinitionId;

	/**
	 * Returns an ordered range of all the object views where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectViewModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object views
	 * @param end the upper bound of the range of object views (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object views
	 */
	@Override
	public List<ObjectView> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectView> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectDefinitionId.find(
			finderCache, new Object[] {objectDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object view in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view
	 * @throws NoSuchObjectViewException if a matching object view could not be found
	 */
	@Override
	public ObjectView findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectView> orderByComparator)
		throws NoSuchObjectViewException {

		return _collectionPersistenceFinderByObjectDefinitionId.findFirst(
			finderCache, new Object[] {objectDefinitionId}, orderByComparator);
	}

	/**
	 * Returns the first object view in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view, or <code>null</code> if a matching object view could not be found
	 */
	@Override
	public ObjectView fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectView> orderByComparator) {

		return _collectionPersistenceFinderByObjectDefinitionId.fetchFirst(
			finderCache, new Object[] {objectDefinitionId}, orderByComparator);
	}

	/**
	 * Removes all the object views where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByObjectDefinitionId(long objectDefinitionId) {
		_collectionPersistenceFinderByObjectDefinitionId.remove(
			finderCache, new Object[] {objectDefinitionId});
	}

	/**
	 * Returns the number of object views where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object views
	 */
	@Override
	public int countByObjectDefinitionId(long objectDefinitionId) {
		return _collectionPersistenceFinderByObjectDefinitionId.count(
			finderCache, new Object[] {objectDefinitionId});
	}

	private CollectionPersistenceFinder<ObjectView, NoSuchObjectViewException>
		_collectionPersistenceFinderByODI_DOV;

	/**
	 * Returns an ordered range of all the object views where objectDefinitionId = &#63; and defaultObjectView = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectViewModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectView the default object view
	 * @param start the lower bound of the range of object views
	 * @param end the upper bound of the range of object views (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object views
	 */
	@Override
	public List<ObjectView> findByODI_DOV(
		long objectDefinitionId, boolean defaultObjectView, int start, int end,
		OrderByComparator<ObjectView> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI_DOV.find(
			finderCache, new Object[] {objectDefinitionId, defaultObjectView},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object view in the ordered set where objectDefinitionId = &#63; and defaultObjectView = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectView the default object view
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view
	 * @throws NoSuchObjectViewException if a matching object view could not be found
	 */
	@Override
	public ObjectView findByODI_DOV_First(
			long objectDefinitionId, boolean defaultObjectView,
			OrderByComparator<ObjectView> orderByComparator)
		throws NoSuchObjectViewException {

		return _collectionPersistenceFinderByODI_DOV.findFirst(
			finderCache, new Object[] {objectDefinitionId, defaultObjectView},
			orderByComparator);
	}

	/**
	 * Returns the first object view in the ordered set where objectDefinitionId = &#63; and defaultObjectView = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectView the default object view
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object view, or <code>null</code> if a matching object view could not be found
	 */
	@Override
	public ObjectView fetchByODI_DOV_First(
		long objectDefinitionId, boolean defaultObjectView,
		OrderByComparator<ObjectView> orderByComparator) {

		return _collectionPersistenceFinderByODI_DOV.fetchFirst(
			finderCache, new Object[] {objectDefinitionId, defaultObjectView},
			orderByComparator);
	}

	/**
	 * Removes all the object views where objectDefinitionId = &#63; and defaultObjectView = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectView the default object view
	 */
	@Override
	public void removeByODI_DOV(
		long objectDefinitionId, boolean defaultObjectView) {

		_collectionPersistenceFinderByODI_DOV.remove(
			finderCache, new Object[] {objectDefinitionId, defaultObjectView});
	}

	/**
	 * Returns the number of object views where objectDefinitionId = &#63; and defaultObjectView = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectView the default object view
	 * @return the number of matching object views
	 */
	@Override
	public int countByODI_DOV(
		long objectDefinitionId, boolean defaultObjectView) {

		return _collectionPersistenceFinderByODI_DOV.count(
			finderCache, new Object[] {objectDefinitionId, defaultObjectView});
	}

	public ObjectViewPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectView.class);

		setModelImplClass(ObjectViewImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectViewTable.INSTANCE);
	}

	/**
	 * Creates a new object view with the primary key. Does not add the object view to the database.
	 *
	 * @param objectViewId the primary key for the new object view
	 * @return the new object view
	 */
	@Override
	public ObjectView create(long objectViewId) {
		ObjectView objectView = new ObjectViewImpl();

		objectView.setNew(true);
		objectView.setPrimaryKey(objectViewId);

		String uuid = PortalUUIDUtil.generate();

		objectView.setUuid(uuid);

		objectView.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectView;
	}

	/**
	 * Removes the object view with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectViewId the primary key of the object view
	 * @return the object view that was removed
	 * @throws NoSuchObjectViewException if a object view with the primary key could not be found
	 */
	@Override
	public ObjectView remove(long objectViewId)
		throws NoSuchObjectViewException {

		return remove((Serializable)objectViewId);
	}

	@Override
	protected ObjectView removeImpl(ObjectView objectView) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectView)) {
				objectView = (ObjectView)session.get(
					ObjectViewImpl.class, objectView.getPrimaryKeyObj());
			}

			if (objectView != null) {
				session.delete(objectView);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectView != null) {
			clearCache(objectView);
		}

		return objectView;
	}

	@Override
	public ObjectView updateImpl(ObjectView objectView) {
		boolean isNew = objectView.isNew();

		if (!(objectView instanceof ObjectViewModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectView.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(objectView);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectView proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectView implementation " +
					objectView.getClass());
		}

		ObjectViewModelImpl objectViewModelImpl =
			(ObjectViewModelImpl)objectView;

		if (Validator.isNull(objectView.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectView.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectView.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectView.setCreateDate(date);
			}
			else {
				objectView.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!objectViewModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectView.setModifiedDate(date);
			}
			else {
				objectView.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectView);
			}
			else {
				objectView = (ObjectView)session.merge(objectView);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectView, false);

		if (isNew) {
			objectView.setNew(false);
		}

		objectView.resetOriginalValues();

		return objectView;
	}

	/**
	 * Returns the object view with the primary key or throws a <code>NoSuchObjectViewException</code> if it could not be found.
	 *
	 * @param objectViewId the primary key of the object view
	 * @return the object view
	 * @throws NoSuchObjectViewException if a object view with the primary key could not be found
	 */
	@Override
	public ObjectView findByPrimaryKey(long objectViewId)
		throws NoSuchObjectViewException {

		return findByPrimaryKey((Serializable)objectViewId);
	}

	/**
	 * Returns the object view with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectViewId the primary key of the object view
	 * @return the object view, or <code>null</code> if a object view with the primary key could not be found
	 */
	@Override
	public ObjectView fetchByPrimaryKey(long objectViewId) {
		return fetchByPrimaryKey((Serializable)objectViewId);
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
		return "objectViewId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTVIEW;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectViewModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object view persistence.
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
			_SQL_SELECT_OBJECTVIEW_WHERE, _SQL_COUNT_OBJECTVIEW_WHERE,
			ObjectViewModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"objectView.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, ObjectView::getUuid));

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
				_SQL_SELECT_OBJECTVIEW_WHERE, _SQL_COUNT_OBJECTVIEW_WHERE,
				ObjectViewModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"objectView.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, ObjectView::getUuid),
				new FinderColumn<>(
					"objectView.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, ObjectView::getCompanyId));

		_collectionPersistenceFinderByObjectDefinitionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByObjectDefinitionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByObjectDefinitionId",
					new String[] {Long.class.getName()},
					new String[] {"objectDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByObjectDefinitionId",
					new String[] {Long.class.getName()},
					new String[] {"objectDefinitionId"}, false),
				_SQL_SELECT_OBJECTVIEW_WHERE, _SQL_COUNT_OBJECTVIEW_WHERE,
				ObjectViewModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"objectView.", "objectDefinitionId", FinderColumn.Type.LONG,
					"=", true, true, ObjectView::getObjectDefinitionId));

		_collectionPersistenceFinderByODI_DOV =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_DOV",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectDefinitionId", "defaultObjectView"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI_DOV",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"objectDefinitionId", "defaultObjectView"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI_DOV",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"objectDefinitionId", "defaultObjectView"},
					false),
				_SQL_SELECT_OBJECTVIEW_WHERE, _SQL_COUNT_OBJECTVIEW_WHERE,
				ObjectViewModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"objectView.", "objectDefinitionId", FinderColumn.Type.LONG,
					"=", true, true, ObjectView::getObjectDefinitionId),
				new FinderColumn<>(
					"objectView.", "defaultObjectView",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					ObjectView::isDefaultObjectView));

		ObjectViewUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectViewUtil.setPersistence(null);

		entityCache.removeCache(ObjectViewImpl.class.getName());
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
		ObjectViewModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTVIEW =
		"SELECT objectView FROM ObjectView objectView";

	private static final String _SQL_SELECT_OBJECTVIEW_WHERE =
		"SELECT objectView FROM ObjectView objectView WHERE ";

	private static final String _SQL_COUNT_OBJECTVIEW_WHERE =
		"SELECT COUNT(objectView) FROM ObjectView objectView WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ObjectView exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:88471042