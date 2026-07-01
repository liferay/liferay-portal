/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectLayoutBoxException;
import com.liferay.object.model.ObjectLayoutBox;
import com.liferay.object.model.ObjectLayoutBoxTable;
import com.liferay.object.model.impl.ObjectLayoutBoxImpl;
import com.liferay.object.model.impl.ObjectLayoutBoxModelImpl;
import com.liferay.object.service.persistence.ObjectLayoutBoxPersistence;
import com.liferay.object.service.persistence.ObjectLayoutBoxUtil;
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
 * The persistence implementation for the object layout box service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectLayoutBoxPersistence.class)
public class ObjectLayoutBoxPersistenceImpl
	extends BasePersistenceImpl<ObjectLayoutBox, NoSuchObjectLayoutBoxException>
	implements ObjectLayoutBoxPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectLayoutBoxUtil</code> to access the object layout box persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectLayoutBoxImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ObjectLayoutBox, NoSuchObjectLayoutBoxException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object layout boxes where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutBoxModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object layout boxes
	 * @param end the upper bound of the range of object layout boxes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layout boxes
	 */
	@Override
	public List<ObjectLayoutBox> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectLayoutBox> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object layout box in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout box
	 * @throws NoSuchObjectLayoutBoxException if a matching object layout box could not be found
	 */
	@Override
	public ObjectLayoutBox findByUuid_First(
			String uuid, OrderByComparator<ObjectLayoutBox> orderByComparator)
		throws NoSuchObjectLayoutBoxException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object layout box in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout box, or <code>null</code> if a matching object layout box could not be found
	 */
	@Override
	public ObjectLayoutBox fetchByUuid_First(
		String uuid, OrderByComparator<ObjectLayoutBox> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object layout boxes where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object layout boxes where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object layout boxes
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ObjectLayoutBox, NoSuchObjectLayoutBoxException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object layout boxes where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutBoxModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object layout boxes
	 * @param end the upper bound of the range of object layout boxes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layout boxes
	 */
	@Override
	public List<ObjectLayoutBox> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectLayoutBox> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object layout box in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout box
	 * @throws NoSuchObjectLayoutBoxException if a matching object layout box could not be found
	 */
	@Override
	public ObjectLayoutBox findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectLayoutBox> orderByComparator)
		throws NoSuchObjectLayoutBoxException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object layout box in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout box, or <code>null</code> if a matching object layout box could not be found
	 */
	@Override
	public ObjectLayoutBox fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectLayoutBox> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object layout boxes where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object layout boxes where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object layout boxes
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ObjectLayoutBox, NoSuchObjectLayoutBoxException>
			_collectionPersistenceFinderByObjectLayoutTabId;

	/**
	 * Returns an ordered range of all the object layout boxes where objectLayoutTabId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutBoxModelImpl</code>.
	 * </p>
	 *
	 * @param objectLayoutTabId the object layout tab ID
	 * @param start the lower bound of the range of object layout boxes
	 * @param end the upper bound of the range of object layout boxes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layout boxes
	 */
	@Override
	public List<ObjectLayoutBox> findByObjectLayoutTabId(
		long objectLayoutTabId, int start, int end,
		OrderByComparator<ObjectLayoutBox> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectLayoutTabId.find(
			finderCache, new Object[] {objectLayoutTabId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object layout box in the ordered set where objectLayoutTabId = &#63;.
	 *
	 * @param objectLayoutTabId the object layout tab ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout box
	 * @throws NoSuchObjectLayoutBoxException if a matching object layout box could not be found
	 */
	@Override
	public ObjectLayoutBox findByObjectLayoutTabId_First(
			long objectLayoutTabId,
			OrderByComparator<ObjectLayoutBox> orderByComparator)
		throws NoSuchObjectLayoutBoxException {

		return _collectionPersistenceFinderByObjectLayoutTabId.findFirst(
			finderCache, new Object[] {objectLayoutTabId}, orderByComparator);
	}

	/**
	 * Returns the first object layout box in the ordered set where objectLayoutTabId = &#63;.
	 *
	 * @param objectLayoutTabId the object layout tab ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout box, or <code>null</code> if a matching object layout box could not be found
	 */
	@Override
	public ObjectLayoutBox fetchByObjectLayoutTabId_First(
		long objectLayoutTabId,
		OrderByComparator<ObjectLayoutBox> orderByComparator) {

		return _collectionPersistenceFinderByObjectLayoutTabId.fetchFirst(
			finderCache, new Object[] {objectLayoutTabId}, orderByComparator);
	}

	/**
	 * Removes all the object layout boxes where objectLayoutTabId = &#63; from the database.
	 *
	 * @param objectLayoutTabId the object layout tab ID
	 */
	@Override
	public void removeByObjectLayoutTabId(long objectLayoutTabId) {
		_collectionPersistenceFinderByObjectLayoutTabId.remove(
			finderCache, new Object[] {objectLayoutTabId});
	}

	/**
	 * Returns the number of object layout boxes where objectLayoutTabId = &#63;.
	 *
	 * @param objectLayoutTabId the object layout tab ID
	 * @return the number of matching object layout boxes
	 */
	@Override
	public int countByObjectLayoutTabId(long objectLayoutTabId) {
		return _collectionPersistenceFinderByObjectLayoutTabId.count(
			finderCache, new Object[] {objectLayoutTabId});
	}

	public ObjectLayoutBoxPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectLayoutBox.class);

		setModelImplClass(ObjectLayoutBoxImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectLayoutBoxTable.INSTANCE);
	}

	/**
	 * Creates a new object layout box with the primary key. Does not add the object layout box to the database.
	 *
	 * @param objectLayoutBoxId the primary key for the new object layout box
	 * @return the new object layout box
	 */
	@Override
	public ObjectLayoutBox create(long objectLayoutBoxId) {
		ObjectLayoutBox objectLayoutBox = new ObjectLayoutBoxImpl();

		objectLayoutBox.setNew(true);
		objectLayoutBox.setPrimaryKey(objectLayoutBoxId);

		String uuid = PortalUUIDUtil.generate();

		objectLayoutBox.setUuid(uuid);

		objectLayoutBox.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectLayoutBox;
	}

	/**
	 * Removes the object layout box with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectLayoutBoxId the primary key of the object layout box
	 * @return the object layout box that was removed
	 * @throws NoSuchObjectLayoutBoxException if a object layout box with the primary key could not be found
	 */
	@Override
	public ObjectLayoutBox remove(long objectLayoutBoxId)
		throws NoSuchObjectLayoutBoxException {

		return remove((Serializable)objectLayoutBoxId);
	}

	@Override
	protected ObjectLayoutBox removeImpl(ObjectLayoutBox objectLayoutBox) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectLayoutBox)) {
				objectLayoutBox = (ObjectLayoutBox)session.get(
					ObjectLayoutBoxImpl.class,
					objectLayoutBox.getPrimaryKeyObj());
			}

			if (objectLayoutBox != null) {
				session.delete(objectLayoutBox);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectLayoutBox != null) {
			clearCache(objectLayoutBox);
		}

		return objectLayoutBox;
	}

	@Override
	public ObjectLayoutBox updateImpl(ObjectLayoutBox objectLayoutBox) {
		boolean isNew = objectLayoutBox.isNew();

		if (!(objectLayoutBox instanceof ObjectLayoutBoxModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectLayoutBox.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectLayoutBox);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectLayoutBox proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectLayoutBox implementation " +
					objectLayoutBox.getClass());
		}

		ObjectLayoutBoxModelImpl objectLayoutBoxModelImpl =
			(ObjectLayoutBoxModelImpl)objectLayoutBox;

		if (Validator.isNull(objectLayoutBox.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectLayoutBox.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectLayoutBox.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectLayoutBox.setCreateDate(date);
			}
			else {
				objectLayoutBox.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectLayoutBoxModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectLayoutBox.setModifiedDate(date);
			}
			else {
				objectLayoutBox.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectLayoutBox);
			}
			else {
				objectLayoutBox = (ObjectLayoutBox)session.merge(
					objectLayoutBox);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectLayoutBox, false);

		if (isNew) {
			objectLayoutBox.setNew(false);
		}

		objectLayoutBox.resetOriginalValues();

		return objectLayoutBox;
	}

	/**
	 * Returns the object layout box with the primary key or throws a <code>NoSuchObjectLayoutBoxException</code> if it could not be found.
	 *
	 * @param objectLayoutBoxId the primary key of the object layout box
	 * @return the object layout box
	 * @throws NoSuchObjectLayoutBoxException if a object layout box with the primary key could not be found
	 */
	@Override
	public ObjectLayoutBox findByPrimaryKey(long objectLayoutBoxId)
		throws NoSuchObjectLayoutBoxException {

		return findByPrimaryKey((Serializable)objectLayoutBoxId);
	}

	/**
	 * Returns the object layout box with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectLayoutBoxId the primary key of the object layout box
	 * @return the object layout box, or <code>null</code> if a object layout box with the primary key could not be found
	 */
	@Override
	public ObjectLayoutBox fetchByPrimaryKey(long objectLayoutBoxId) {
		return fetchByPrimaryKey((Serializable)objectLayoutBoxId);
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
		return "objectLayoutBoxId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTLAYOUTBOX;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectLayoutBoxModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object layout box persistence.
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
			_SQL_SELECT_OBJECTLAYOUTBOX_WHERE, _SQL_COUNT_OBJECTLAYOUTBOX_WHERE,
			ObjectLayoutBoxModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"objectLayoutBox.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, ObjectLayoutBox::getUuid));

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
				_SQL_SELECT_OBJECTLAYOUTBOX_WHERE,
				_SQL_COUNT_OBJECTLAYOUTBOX_WHERE,
				ObjectLayoutBoxModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectLayoutBox.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectLayoutBox::getUuid),
				new FinderColumn<>(
					"objectLayoutBox.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectLayoutBox::getCompanyId));

		_collectionPersistenceFinderByObjectLayoutTabId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByObjectLayoutTabId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectLayoutTabId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByObjectLayoutTabId",
					new String[] {Long.class.getName()},
					new String[] {"objectLayoutTabId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByObjectLayoutTabId",
					new String[] {Long.class.getName()},
					new String[] {"objectLayoutTabId"}, false),
				_SQL_SELECT_OBJECTLAYOUTBOX_WHERE,
				_SQL_COUNT_OBJECTLAYOUTBOX_WHERE,
				ObjectLayoutBoxModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectLayoutBox.", "objectLayoutTabId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectLayoutBox::getObjectLayoutTabId));

		ObjectLayoutBoxUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectLayoutBoxUtil.setPersistence(null);

		entityCache.removeCache(ObjectLayoutBoxImpl.class.getName());
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
		ObjectLayoutBoxModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTLAYOUTBOX =
		"SELECT objectLayoutBox FROM ObjectLayoutBox objectLayoutBox";

	private static final String _SQL_SELECT_OBJECTLAYOUTBOX_WHERE =
		"SELECT objectLayoutBox FROM ObjectLayoutBox objectLayoutBox WHERE ";

	private static final String _SQL_COUNT_OBJECTLAYOUTBOX_WHERE =
		"SELECT COUNT(objectLayoutBox) FROM ObjectLayoutBox objectLayoutBox WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ObjectLayoutBox exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-566248316