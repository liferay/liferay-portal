/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectFolderItemException;
import com.liferay.object.model.ObjectFolderItem;
import com.liferay.object.model.ObjectFolderItemTable;
import com.liferay.object.model.impl.ObjectFolderItemImpl;
import com.liferay.object.model.impl.ObjectFolderItemModelImpl;
import com.liferay.object.service.persistence.ObjectFolderItemPersistence;
import com.liferay.object.service.persistence.ObjectFolderItemUtil;
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
 * The persistence implementation for the object folder item service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectFolderItemPersistence.class)
public class ObjectFolderItemPersistenceImpl
	extends BasePersistenceImpl
		<ObjectFolderItem, NoSuchObjectFolderItemException>
	implements ObjectFolderItemPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectFolderItemUtil</code> to access the object folder item persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectFolderItemImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ObjectFolderItem, NoSuchObjectFolderItemException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object folder items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFolderItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object folder items
	 * @param end the upper bound of the range of object folder items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object folder items
	 */
	@Override
	public List<ObjectFolderItem> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectFolderItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object folder item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object folder item
	 * @throws NoSuchObjectFolderItemException if a matching object folder item could not be found
	 */
	@Override
	public ObjectFolderItem findByUuid_First(
			String uuid, OrderByComparator<ObjectFolderItem> orderByComparator)
		throws NoSuchObjectFolderItemException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object folder item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object folder item, or <code>null</code> if a matching object folder item could not be found
	 */
	@Override
	public ObjectFolderItem fetchByUuid_First(
		String uuid, OrderByComparator<ObjectFolderItem> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object folder items where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object folder items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object folder items
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ObjectFolderItem, NoSuchObjectFolderItemException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object folder items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFolderItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object folder items
	 * @param end the upper bound of the range of object folder items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object folder items
	 */
	@Override
	public List<ObjectFolderItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectFolderItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object folder item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object folder item
	 * @throws NoSuchObjectFolderItemException if a matching object folder item could not be found
	 */
	@Override
	public ObjectFolderItem findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectFolderItem> orderByComparator)
		throws NoSuchObjectFolderItemException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object folder item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object folder item, or <code>null</code> if a matching object folder item could not be found
	 */
	@Override
	public ObjectFolderItem fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectFolderItem> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object folder items where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object folder items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object folder items
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ObjectFolderItem, NoSuchObjectFolderItemException>
			_collectionPersistenceFinderByObjectDefinitionId;

	/**
	 * Returns an ordered range of all the object folder items where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFolderItemModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object folder items
	 * @param end the upper bound of the range of object folder items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object folder items
	 */
	@Override
	public List<ObjectFolderItem> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectFolderItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectDefinitionId.find(
			finderCache, new Object[] {objectDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object folder item in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object folder item
	 * @throws NoSuchObjectFolderItemException if a matching object folder item could not be found
	 */
	@Override
	public ObjectFolderItem findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectFolderItem> orderByComparator)
		throws NoSuchObjectFolderItemException {

		return _collectionPersistenceFinderByObjectDefinitionId.findFirst(
			finderCache, new Object[] {objectDefinitionId}, orderByComparator);
	}

	/**
	 * Returns the first object folder item in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object folder item, or <code>null</code> if a matching object folder item could not be found
	 */
	@Override
	public ObjectFolderItem fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectFolderItem> orderByComparator) {

		return _collectionPersistenceFinderByObjectDefinitionId.fetchFirst(
			finderCache, new Object[] {objectDefinitionId}, orderByComparator);
	}

	/**
	 * Removes all the object folder items where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByObjectDefinitionId(long objectDefinitionId) {
		_collectionPersistenceFinderByObjectDefinitionId.remove(
			finderCache, new Object[] {objectDefinitionId});
	}

	/**
	 * Returns the number of object folder items where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object folder items
	 */
	@Override
	public int countByObjectDefinitionId(long objectDefinitionId) {
		return _collectionPersistenceFinderByObjectDefinitionId.count(
			finderCache, new Object[] {objectDefinitionId});
	}

	private CollectionPersistenceFinder
		<ObjectFolderItem, NoSuchObjectFolderItemException>
			_collectionPersistenceFinderByObjectFolderId;

	/**
	 * Returns an ordered range of all the object folder items where objectFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFolderItemModelImpl</code>.
	 * </p>
	 *
	 * @param objectFolderId the object folder ID
	 * @param start the lower bound of the range of object folder items
	 * @param end the upper bound of the range of object folder items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object folder items
	 */
	@Override
	public List<ObjectFolderItem> findByObjectFolderId(
		long objectFolderId, int start, int end,
		OrderByComparator<ObjectFolderItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectFolderId.find(
			finderCache, new Object[] {objectFolderId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object folder item in the ordered set where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object folder item
	 * @throws NoSuchObjectFolderItemException if a matching object folder item could not be found
	 */
	@Override
	public ObjectFolderItem findByObjectFolderId_First(
			long objectFolderId,
			OrderByComparator<ObjectFolderItem> orderByComparator)
		throws NoSuchObjectFolderItemException {

		return _collectionPersistenceFinderByObjectFolderId.findFirst(
			finderCache, new Object[] {objectFolderId}, orderByComparator);
	}

	/**
	 * Returns the first object folder item in the ordered set where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object folder item, or <code>null</code> if a matching object folder item could not be found
	 */
	@Override
	public ObjectFolderItem fetchByObjectFolderId_First(
		long objectFolderId,
		OrderByComparator<ObjectFolderItem> orderByComparator) {

		return _collectionPersistenceFinderByObjectFolderId.fetchFirst(
			finderCache, new Object[] {objectFolderId}, orderByComparator);
	}

	/**
	 * Removes all the object folder items where objectFolderId = &#63; from the database.
	 *
	 * @param objectFolderId the object folder ID
	 */
	@Override
	public void removeByObjectFolderId(long objectFolderId) {
		_collectionPersistenceFinderByObjectFolderId.remove(
			finderCache, new Object[] {objectFolderId});
	}

	/**
	 * Returns the number of object folder items where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @return the number of matching object folder items
	 */
	@Override
	public int countByObjectFolderId(long objectFolderId) {
		return _collectionPersistenceFinderByObjectFolderId.count(
			finderCache, new Object[] {objectFolderId});
	}

	private UniquePersistenceFinder
		<ObjectFolderItem, NoSuchObjectFolderItemException>
			_uniquePersistenceFinderByODI_OFI;

	/**
	 * Returns the object folder item where objectDefinitionId = &#63; and objectFolderId = &#63; or throws a <code>NoSuchObjectFolderItemException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param objectFolderId the object folder ID
	 * @return the matching object folder item
	 * @throws NoSuchObjectFolderItemException if a matching object folder item could not be found
	 */
	@Override
	public ObjectFolderItem findByODI_OFI(
			long objectDefinitionId, long objectFolderId)
		throws NoSuchObjectFolderItemException {

		return _uniquePersistenceFinderByODI_OFI.find(
			finderCache, new Object[] {objectDefinitionId, objectFolderId});
	}

	/**
	 * Returns the object folder item where objectDefinitionId = &#63; and objectFolderId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param objectFolderId the object folder ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object folder item, or <code>null</code> if a matching object folder item could not be found
	 */
	@Override
	public ObjectFolderItem fetchByODI_OFI(
		long objectDefinitionId, long objectFolderId, boolean useFinderCache) {

		return _uniquePersistenceFinderByODI_OFI.fetch(
			finderCache, new Object[] {objectDefinitionId, objectFolderId},
			useFinderCache);
	}

	/**
	 * Removes the object folder item where objectDefinitionId = &#63; and objectFolderId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param objectFolderId the object folder ID
	 * @return the object folder item that was removed
	 */
	@Override
	public ObjectFolderItem removeByODI_OFI(
			long objectDefinitionId, long objectFolderId)
		throws NoSuchObjectFolderItemException {

		ObjectFolderItem objectFolderItem = findByODI_OFI(
			objectDefinitionId, objectFolderId);

		return remove(objectFolderItem);
	}

	/**
	 * Returns the number of object folder items where objectDefinitionId = &#63; and objectFolderId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param objectFolderId the object folder ID
	 * @return the number of matching object folder items
	 */
	@Override
	public int countByODI_OFI(long objectDefinitionId, long objectFolderId) {
		return _uniquePersistenceFinderByODI_OFI.count(
			finderCache, new Object[] {objectDefinitionId, objectFolderId});
	}

	public ObjectFolderItemPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectFolderItem.class);

		setModelImplClass(ObjectFolderItemImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectFolderItemTable.INSTANCE);
	}

	/**
	 * Creates a new object folder item with the primary key. Does not add the object folder item to the database.
	 *
	 * @param objectFolderItemId the primary key for the new object folder item
	 * @return the new object folder item
	 */
	@Override
	public ObjectFolderItem create(long objectFolderItemId) {
		ObjectFolderItem objectFolderItem = new ObjectFolderItemImpl();

		objectFolderItem.setNew(true);
		objectFolderItem.setPrimaryKey(objectFolderItemId);

		String uuid = PortalUUIDUtil.generate();

		objectFolderItem.setUuid(uuid);

		objectFolderItem.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectFolderItem;
	}

	/**
	 * Removes the object folder item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectFolderItemId the primary key of the object folder item
	 * @return the object folder item that was removed
	 * @throws NoSuchObjectFolderItemException if a object folder item with the primary key could not be found
	 */
	@Override
	public ObjectFolderItem remove(long objectFolderItemId)
		throws NoSuchObjectFolderItemException {

		return remove((Serializable)objectFolderItemId);
	}

	@Override
	protected ObjectFolderItem removeImpl(ObjectFolderItem objectFolderItem) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectFolderItem)) {
				objectFolderItem = (ObjectFolderItem)session.get(
					ObjectFolderItemImpl.class,
					objectFolderItem.getPrimaryKeyObj());
			}

			if (objectFolderItem != null) {
				session.delete(objectFolderItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectFolderItem != null) {
			clearCache(objectFolderItem);
		}

		return objectFolderItem;
	}

	@Override
	public ObjectFolderItem updateImpl(ObjectFolderItem objectFolderItem) {
		boolean isNew = objectFolderItem.isNew();

		if (!(objectFolderItem instanceof ObjectFolderItemModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectFolderItem.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectFolderItem);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectFolderItem proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectFolderItem implementation " +
					objectFolderItem.getClass());
		}

		ObjectFolderItemModelImpl objectFolderItemModelImpl =
			(ObjectFolderItemModelImpl)objectFolderItem;

		if (Validator.isNull(objectFolderItem.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectFolderItem.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectFolderItem.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectFolderItem.setCreateDate(date);
			}
			else {
				objectFolderItem.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectFolderItemModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectFolderItem.setModifiedDate(date);
			}
			else {
				objectFolderItem.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectFolderItem);
			}
			else {
				objectFolderItem = (ObjectFolderItem)session.merge(
					objectFolderItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectFolderItem, false);

		if (isNew) {
			objectFolderItem.setNew(false);
		}

		objectFolderItem.resetOriginalValues();

		return objectFolderItem;
	}

	/**
	 * Returns the object folder item with the primary key or throws a <code>NoSuchObjectFolderItemException</code> if it could not be found.
	 *
	 * @param objectFolderItemId the primary key of the object folder item
	 * @return the object folder item
	 * @throws NoSuchObjectFolderItemException if a object folder item with the primary key could not be found
	 */
	@Override
	public ObjectFolderItem findByPrimaryKey(long objectFolderItemId)
		throws NoSuchObjectFolderItemException {

		return findByPrimaryKey((Serializable)objectFolderItemId);
	}

	/**
	 * Returns the object folder item with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectFolderItemId the primary key of the object folder item
	 * @return the object folder item, or <code>null</code> if a object folder item with the primary key could not be found
	 */
	@Override
	public ObjectFolderItem fetchByPrimaryKey(long objectFolderItemId) {
		return fetchByPrimaryKey((Serializable)objectFolderItemId);
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
		return "objectFolderItemId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTFOLDERITEM;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectFolderItemModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object folder item persistence.
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
			_SQL_SELECT_OBJECTFOLDERITEM_WHERE,
			_SQL_COUNT_OBJECTFOLDERITEM_WHERE,
			ObjectFolderItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"objectFolderItem.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, ObjectFolderItem::getUuid));

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
				_SQL_SELECT_OBJECTFOLDERITEM_WHERE,
				_SQL_COUNT_OBJECTFOLDERITEM_WHERE,
				ObjectFolderItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectFolderItem.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectFolderItem::getUuid),
				new FinderColumn<>(
					"objectFolderItem.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectFolderItem::getCompanyId));

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
				_SQL_SELECT_OBJECTFOLDERITEM_WHERE,
				_SQL_COUNT_OBJECTFOLDERITEM_WHERE,
				ObjectFolderItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectFolderItem.", "objectDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectFolderItem::getObjectDefinitionId));

		_collectionPersistenceFinderByObjectFolderId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByObjectFolderId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectFolderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByObjectFolderId", new String[] {Long.class.getName()},
					new String[] {"objectFolderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByObjectFolderId",
					new String[] {Long.class.getName()},
					new String[] {"objectFolderId"}, false),
				_SQL_SELECT_OBJECTFOLDERITEM_WHERE,
				_SQL_COUNT_OBJECTFOLDERITEM_WHERE,
				ObjectFolderItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectFolderItem.", "objectFolderId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectFolderItem::getObjectFolderId));

		_uniquePersistenceFinderByODI_OFI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByODI_OFI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"objectDefinitionId", "objectFolderId"}, 0, 0,
				false, ObjectFolderItem::getObjectDefinitionId,
				ObjectFolderItem::getObjectFolderId),
			_SQL_SELECT_OBJECTFOLDERITEM_WHERE, "",
			new FinderColumn<>(
				"objectFolderItem.", "objectDefinitionId",
				FinderColumn.Type.LONG, "=", true, true,
				ObjectFolderItem::getObjectDefinitionId),
			new FinderColumn<>(
				"objectFolderItem.", "objectFolderId", FinderColumn.Type.LONG,
				"=", true, true, ObjectFolderItem::getObjectFolderId));

		ObjectFolderItemUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectFolderItemUtil.setPersistence(null);

		entityCache.removeCache(ObjectFolderItemImpl.class.getName());
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
		ObjectFolderItemModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTFOLDERITEM =
		"SELECT objectFolderItem FROM ObjectFolderItem objectFolderItem";

	private static final String _SQL_SELECT_OBJECTFOLDERITEM_WHERE =
		"SELECT objectFolderItem FROM ObjectFolderItem objectFolderItem WHERE ";

	private static final String _SQL_COUNT_OBJECTFOLDERITEM_WHERE =
		"SELECT COUNT(objectFolderItem) FROM ObjectFolderItem objectFolderItem WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ObjectFolderItem exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectFolderItemPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-966042779