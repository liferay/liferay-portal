/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectFieldSettingException;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectFieldSettingTable;
import com.liferay.object.model.impl.ObjectFieldSettingImpl;
import com.liferay.object.model.impl.ObjectFieldSettingModelImpl;
import com.liferay.object.service.persistence.ObjectFieldSettingPersistence;
import com.liferay.object.service.persistence.ObjectFieldSettingUtil;
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
 * The persistence implementation for the object field setting service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectFieldSettingPersistence.class)
public class ObjectFieldSettingPersistenceImpl
	extends BasePersistenceImpl
		<ObjectFieldSetting, NoSuchObjectFieldSettingException>
	implements ObjectFieldSettingPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectFieldSettingUtil</code> to access the object field setting persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectFieldSettingImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ObjectFieldSetting, NoSuchObjectFieldSettingException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object field settings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object field settings
	 * @param end the upper bound of the range of object field settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object field settings
	 */
	@Override
	public List<ObjectFieldSetting> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectFieldSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object field setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field setting
	 * @throws NoSuchObjectFieldSettingException if a matching object field setting could not be found
	 */
	@Override
	public ObjectFieldSetting findByUuid_First(
			String uuid,
			OrderByComparator<ObjectFieldSetting> orderByComparator)
		throws NoSuchObjectFieldSettingException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object field setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field setting, or <code>null</code> if a matching object field setting could not be found
	 */
	@Override
	public ObjectFieldSetting fetchByUuid_First(
		String uuid, OrderByComparator<ObjectFieldSetting> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object field settings where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object field settings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object field settings
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ObjectFieldSetting, NoSuchObjectFieldSettingException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object field settings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object field settings
	 * @param end the upper bound of the range of object field settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object field settings
	 */
	@Override
	public List<ObjectFieldSetting> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectFieldSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field setting
	 * @throws NoSuchObjectFieldSettingException if a matching object field setting could not be found
	 */
	@Override
	public ObjectFieldSetting findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectFieldSetting> orderByComparator)
		throws NoSuchObjectFieldSettingException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object field setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field setting, or <code>null</code> if a matching object field setting could not be found
	 */
	@Override
	public ObjectFieldSetting fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectFieldSetting> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object field settings where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object field settings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object field settings
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ObjectFieldSetting, NoSuchObjectFieldSettingException>
			_collectionPersistenceFinderByObjectFieldId;

	/**
	 * Returns an ordered range of all the object field settings where objectFieldId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldSettingModelImpl</code>.
	 * </p>
	 *
	 * @param objectFieldId the object field ID
	 * @param start the lower bound of the range of object field settings
	 * @param end the upper bound of the range of object field settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object field settings
	 */
	@Override
	public List<ObjectFieldSetting> findByObjectFieldId(
		long objectFieldId, int start, int end,
		OrderByComparator<ObjectFieldSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectFieldId.find(
			finderCache, new Object[] {objectFieldId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field setting in the ordered set where objectFieldId = &#63;.
	 *
	 * @param objectFieldId the object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field setting
	 * @throws NoSuchObjectFieldSettingException if a matching object field setting could not be found
	 */
	@Override
	public ObjectFieldSetting findByObjectFieldId_First(
			long objectFieldId,
			OrderByComparator<ObjectFieldSetting> orderByComparator)
		throws NoSuchObjectFieldSettingException {

		return _collectionPersistenceFinderByObjectFieldId.findFirst(
			finderCache, new Object[] {objectFieldId}, orderByComparator);
	}

	/**
	 * Returns the first object field setting in the ordered set where objectFieldId = &#63;.
	 *
	 * @param objectFieldId the object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field setting, or <code>null</code> if a matching object field setting could not be found
	 */
	@Override
	public ObjectFieldSetting fetchByObjectFieldId_First(
		long objectFieldId,
		OrderByComparator<ObjectFieldSetting> orderByComparator) {

		return _collectionPersistenceFinderByObjectFieldId.fetchFirst(
			finderCache, new Object[] {objectFieldId}, orderByComparator);
	}

	/**
	 * Removes all the object field settings where objectFieldId = &#63; from the database.
	 *
	 * @param objectFieldId the object field ID
	 */
	@Override
	public void removeByObjectFieldId(long objectFieldId) {
		_collectionPersistenceFinderByObjectFieldId.remove(
			finderCache, new Object[] {objectFieldId});
	}

	/**
	 * Returns the number of object field settings where objectFieldId = &#63;.
	 *
	 * @param objectFieldId the object field ID
	 * @return the number of matching object field settings
	 */
	@Override
	public int countByObjectFieldId(long objectFieldId) {
		return _collectionPersistenceFinderByObjectFieldId.count(
			finderCache, new Object[] {objectFieldId});
	}

	private UniquePersistenceFinder
		<ObjectFieldSetting, NoSuchObjectFieldSettingException>
			_uniquePersistenceFinderByOFI_N;

	/**
	 * Returns the object field setting where objectFieldId = &#63; and name = &#63; or throws a <code>NoSuchObjectFieldSettingException</code> if it could not be found.
	 *
	 * @param objectFieldId the object field ID
	 * @param name the name
	 * @return the matching object field setting
	 * @throws NoSuchObjectFieldSettingException if a matching object field setting could not be found
	 */
	@Override
	public ObjectFieldSetting findByOFI_N(long objectFieldId, String name)
		throws NoSuchObjectFieldSettingException {

		return _uniquePersistenceFinderByOFI_N.find(
			finderCache, new Object[] {objectFieldId, name});
	}

	/**
	 * Returns the object field setting where objectFieldId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectFieldId the object field ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object field setting, or <code>null</code> if a matching object field setting could not be found
	 */
	@Override
	public ObjectFieldSetting fetchByOFI_N(
		long objectFieldId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByOFI_N.fetch(
			finderCache, new Object[] {objectFieldId, name}, useFinderCache);
	}

	/**
	 * Removes the object field setting where objectFieldId = &#63; and name = &#63; from the database.
	 *
	 * @param objectFieldId the object field ID
	 * @param name the name
	 * @return the object field setting that was removed
	 */
	@Override
	public ObjectFieldSetting removeByOFI_N(long objectFieldId, String name)
		throws NoSuchObjectFieldSettingException {

		ObjectFieldSetting objectFieldSetting = findByOFI_N(
			objectFieldId, name);

		return remove(objectFieldSetting);
	}

	/**
	 * Returns the number of object field settings where objectFieldId = &#63; and name = &#63;.
	 *
	 * @param objectFieldId the object field ID
	 * @param name the name
	 * @return the number of matching object field settings
	 */
	@Override
	public int countByOFI_N(long objectFieldId, String name) {
		return _uniquePersistenceFinderByOFI_N.count(
			finderCache, new Object[] {objectFieldId, name});
	}

	public ObjectFieldSettingPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectFieldSetting.class);

		setModelImplClass(ObjectFieldSettingImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectFieldSettingTable.INSTANCE);
	}

	/**
	 * Creates a new object field setting with the primary key. Does not add the object field setting to the database.
	 *
	 * @param objectFieldSettingId the primary key for the new object field setting
	 * @return the new object field setting
	 */
	@Override
	public ObjectFieldSetting create(long objectFieldSettingId) {
		ObjectFieldSetting objectFieldSetting = new ObjectFieldSettingImpl();

		objectFieldSetting.setNew(true);
		objectFieldSetting.setPrimaryKey(objectFieldSettingId);

		String uuid = PortalUUIDUtil.generate();

		objectFieldSetting.setUuid(uuid);

		objectFieldSetting.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectFieldSetting;
	}

	/**
	 * Removes the object field setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectFieldSettingId the primary key of the object field setting
	 * @return the object field setting that was removed
	 * @throws NoSuchObjectFieldSettingException if a object field setting with the primary key could not be found
	 */
	@Override
	public ObjectFieldSetting remove(long objectFieldSettingId)
		throws NoSuchObjectFieldSettingException {

		return remove((Serializable)objectFieldSettingId);
	}

	@Override
	protected ObjectFieldSetting removeImpl(
		ObjectFieldSetting objectFieldSetting) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectFieldSetting)) {
				objectFieldSetting = (ObjectFieldSetting)session.get(
					ObjectFieldSettingImpl.class,
					objectFieldSetting.getPrimaryKeyObj());
			}

			if (objectFieldSetting != null) {
				session.delete(objectFieldSetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectFieldSetting != null) {
			clearCache(objectFieldSetting);
		}

		return objectFieldSetting;
	}

	@Override
	public ObjectFieldSetting updateImpl(
		ObjectFieldSetting objectFieldSetting) {

		boolean isNew = objectFieldSetting.isNew();

		if (!(objectFieldSetting instanceof ObjectFieldSettingModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectFieldSetting.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectFieldSetting);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectFieldSetting proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectFieldSetting implementation " +
					objectFieldSetting.getClass());
		}

		ObjectFieldSettingModelImpl objectFieldSettingModelImpl =
			(ObjectFieldSettingModelImpl)objectFieldSetting;

		if (Validator.isNull(objectFieldSetting.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectFieldSetting.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectFieldSetting.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectFieldSetting.setCreateDate(date);
			}
			else {
				objectFieldSetting.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectFieldSettingModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectFieldSetting.setModifiedDate(date);
			}
			else {
				objectFieldSetting.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectFieldSetting);
			}
			else {
				objectFieldSetting = (ObjectFieldSetting)session.merge(
					objectFieldSetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectFieldSetting, false);

		if (isNew) {
			objectFieldSetting.setNew(false);
		}

		objectFieldSetting.resetOriginalValues();

		return objectFieldSetting;
	}

	/**
	 * Returns the object field setting with the primary key or throws a <code>NoSuchObjectFieldSettingException</code> if it could not be found.
	 *
	 * @param objectFieldSettingId the primary key of the object field setting
	 * @return the object field setting
	 * @throws NoSuchObjectFieldSettingException if a object field setting with the primary key could not be found
	 */
	@Override
	public ObjectFieldSetting findByPrimaryKey(long objectFieldSettingId)
		throws NoSuchObjectFieldSettingException {

		return findByPrimaryKey((Serializable)objectFieldSettingId);
	}

	/**
	 * Returns the object field setting with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectFieldSettingId the primary key of the object field setting
	 * @return the object field setting, or <code>null</code> if a object field setting with the primary key could not be found
	 */
	@Override
	public ObjectFieldSetting fetchByPrimaryKey(long objectFieldSettingId) {
		return fetchByPrimaryKey((Serializable)objectFieldSettingId);
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
		return "objectFieldSettingId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTFIELDSETTING;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectFieldSettingModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object field setting persistence.
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
			_SQL_SELECT_OBJECTFIELDSETTING_WHERE,
			_SQL_COUNT_OBJECTFIELDSETTING_WHERE,
			ObjectFieldSettingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"objectFieldSetting.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectFieldSetting::getUuid));

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
				_SQL_SELECT_OBJECTFIELDSETTING_WHERE,
				_SQL_COUNT_OBJECTFIELDSETTING_WHERE,
				ObjectFieldSettingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"objectFieldSetting.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectFieldSetting::getUuid),
				new FinderColumn<>(
					"objectFieldSetting.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectFieldSetting::getCompanyId));

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
				_SQL_SELECT_OBJECTFIELDSETTING_WHERE,
				_SQL_COUNT_OBJECTFIELDSETTING_WHERE,
				ObjectFieldSettingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"objectFieldSetting.", "objectFieldId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectFieldSetting::getObjectFieldId));

		_uniquePersistenceFinderByOFI_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByOFI_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"objectFieldId", "name"}, 0, 2, false,
				ObjectFieldSetting::getObjectFieldId,
				convertNullFunction(ObjectFieldSetting::getName)),
			_SQL_SELECT_OBJECTFIELDSETTING_WHERE, "",
			new FinderColumn<>(
				"objectFieldSetting.", "objectFieldId", FinderColumn.Type.LONG,
				"=", true, true, ObjectFieldSetting::getObjectFieldId),
			new FinderColumn<>(
				"objectFieldSetting.", "name", FinderColumn.Type.STRING, "=",
				true, true, ObjectFieldSetting::getName));

		ObjectFieldSettingUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectFieldSettingUtil.setPersistence(null);

		entityCache.removeCache(ObjectFieldSettingImpl.class.getName());
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
		ObjectFieldSettingModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTFIELDSETTING =
		"SELECT objectFieldSetting FROM ObjectFieldSetting objectFieldSetting";

	private static final String _SQL_SELECT_OBJECTFIELDSETTING_WHERE =
		"SELECT objectFieldSetting FROM ObjectFieldSetting objectFieldSetting WHERE ";

	private static final String _SQL_COUNT_OBJECTFIELDSETTING_WHERE =
		"SELECT COUNT(objectFieldSetting) FROM ObjectFieldSetting objectFieldSetting WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:132170824