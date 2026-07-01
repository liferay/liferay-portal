/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectLayoutException;
import com.liferay.object.model.ObjectLayout;
import com.liferay.object.model.ObjectLayoutTable;
import com.liferay.object.model.impl.ObjectLayoutImpl;
import com.liferay.object.model.impl.ObjectLayoutModelImpl;
import com.liferay.object.service.persistence.ObjectLayoutPersistence;
import com.liferay.object.service.persistence.ObjectLayoutUtil;
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
 * The persistence implementation for the object layout service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectLayoutPersistence.class)
public class ObjectLayoutPersistenceImpl
	extends BasePersistenceImpl<ObjectLayout, NoSuchObjectLayoutException>
	implements ObjectLayoutPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectLayoutUtil</code> to access the object layout persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectLayoutImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ObjectLayout, NoSuchObjectLayoutException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout findByUuid_First(
			String uuid, OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout fetchByUuid_First(
		String uuid, OrderByComparator<ObjectLayout> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object layouts where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object layouts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object layouts
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ObjectLayout, NoSuchObjectLayoutException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object layouts where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object layouts
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ObjectLayout, NoSuchObjectLayoutException>
			_collectionPersistenceFinderByObjectDefinitionId;

	/**
	 * Returns an ordered range of all the object layouts where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectDefinitionId.find(
			finderCache, new Object[] {objectDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object layout in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		return _collectionPersistenceFinderByObjectDefinitionId.findFirst(
			finderCache, new Object[] {objectDefinitionId}, orderByComparator);
	}

	/**
	 * Returns the first object layout in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return _collectionPersistenceFinderByObjectDefinitionId.fetchFirst(
			finderCache, new Object[] {objectDefinitionId}, orderByComparator);
	}

	/**
	 * Removes all the object layouts where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByObjectDefinitionId(long objectDefinitionId) {
		_collectionPersistenceFinderByObjectDefinitionId.remove(
			finderCache, new Object[] {objectDefinitionId});
	}

	/**
	 * Returns the number of object layouts where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object layouts
	 */
	@Override
	public int countByObjectDefinitionId(long objectDefinitionId) {
		return _collectionPersistenceFinderByObjectDefinitionId.count(
			finderCache, new Object[] {objectDefinitionId});
	}

	private CollectionPersistenceFinder
		<ObjectLayout, NoSuchObjectLayoutException>
			_collectionPersistenceFinderByC_DOL;

	/**
	 * Returns an ordered range of all the object layouts where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByC_DOL(
		long companyId, boolean defaultObjectLayout, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_DOL.find(
			finderCache, new Object[] {companyId, defaultObjectLayout}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object layout in the ordered set where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout findByC_DOL_First(
			long companyId, boolean defaultObjectLayout,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		return _collectionPersistenceFinderByC_DOL.findFirst(
			finderCache, new Object[] {companyId, defaultObjectLayout},
			orderByComparator);
	}

	/**
	 * Returns the first object layout in the ordered set where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout fetchByC_DOL_First(
		long companyId, boolean defaultObjectLayout,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return _collectionPersistenceFinderByC_DOL.fetchFirst(
			finderCache, new Object[] {companyId, defaultObjectLayout},
			orderByComparator);
	}

	/**
	 * Removes all the object layouts where companyId = &#63; and defaultObjectLayout = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 */
	@Override
	public void removeByC_DOL(long companyId, boolean defaultObjectLayout) {
		_collectionPersistenceFinderByC_DOL.remove(
			finderCache, new Object[] {companyId, defaultObjectLayout});
	}

	/**
	 * Returns the number of object layouts where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @return the number of matching object layouts
	 */
	@Override
	public int countByC_DOL(long companyId, boolean defaultObjectLayout) {
		return _collectionPersistenceFinderByC_DOL.count(
			finderCache, new Object[] {companyId, defaultObjectLayout});
	}

	private CollectionPersistenceFinder
		<ObjectLayout, NoSuchObjectLayoutException>
			_collectionPersistenceFinderByODI_DOL;

	/**
	 * Returns an ordered range of all the object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout, int start,
		int end, OrderByComparator<ObjectLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI_DOL.find(
			finderCache, new Object[] {objectDefinitionId, defaultObjectLayout},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object layout in the ordered set where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout findByODI_DOL_First(
			long objectDefinitionId, boolean defaultObjectLayout,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		return _collectionPersistenceFinderByODI_DOL.findFirst(
			finderCache, new Object[] {objectDefinitionId, defaultObjectLayout},
			orderByComparator);
	}

	/**
	 * Returns the first object layout in the ordered set where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout fetchByODI_DOL_First(
		long objectDefinitionId, boolean defaultObjectLayout,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return _collectionPersistenceFinderByODI_DOL.fetchFirst(
			finderCache, new Object[] {objectDefinitionId, defaultObjectLayout},
			orderByComparator);
	}

	/**
	 * Removes all the object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 */
	@Override
	public void removeByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout) {

		_collectionPersistenceFinderByODI_DOL.remove(
			finderCache,
			new Object[] {objectDefinitionId, defaultObjectLayout});
	}

	/**
	 * Returns the number of object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @return the number of matching object layouts
	 */
	@Override
	public int countByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout) {

		return _collectionPersistenceFinderByODI_DOL.count(
			finderCache,
			new Object[] {objectDefinitionId, defaultObjectLayout});
	}

	public ObjectLayoutPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectLayout.class);

		setModelImplClass(ObjectLayoutImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectLayoutTable.INSTANCE);
	}

	/**
	 * Creates a new object layout with the primary key. Does not add the object layout to the database.
	 *
	 * @param objectLayoutId the primary key for the new object layout
	 * @return the new object layout
	 */
	@Override
	public ObjectLayout create(long objectLayoutId) {
		ObjectLayout objectLayout = new ObjectLayoutImpl();

		objectLayout.setNew(true);
		objectLayout.setPrimaryKey(objectLayoutId);

		String uuid = PortalUUIDUtil.generate();

		objectLayout.setUuid(uuid);

		objectLayout.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectLayout;
	}

	/**
	 * Removes the object layout with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectLayoutId the primary key of the object layout
	 * @return the object layout that was removed
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	@Override
	public ObjectLayout remove(long objectLayoutId)
		throws NoSuchObjectLayoutException {

		return remove((Serializable)objectLayoutId);
	}

	@Override
	protected ObjectLayout removeImpl(ObjectLayout objectLayout) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectLayout)) {
				objectLayout = (ObjectLayout)session.get(
					ObjectLayoutImpl.class, objectLayout.getPrimaryKeyObj());
			}

			if (objectLayout != null) {
				session.delete(objectLayout);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectLayout != null) {
			clearCache(objectLayout);
		}

		return objectLayout;
	}

	@Override
	public ObjectLayout updateImpl(ObjectLayout objectLayout) {
		boolean isNew = objectLayout.isNew();

		if (!(objectLayout instanceof ObjectLayoutModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectLayout.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectLayout);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectLayout proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectLayout implementation " +
					objectLayout.getClass());
		}

		ObjectLayoutModelImpl objectLayoutModelImpl =
			(ObjectLayoutModelImpl)objectLayout;

		if (Validator.isNull(objectLayout.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectLayout.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectLayout.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectLayout.setCreateDate(date);
			}
			else {
				objectLayout.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!objectLayoutModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectLayout.setModifiedDate(date);
			}
			else {
				objectLayout.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectLayout);
			}
			else {
				objectLayout = (ObjectLayout)session.merge(objectLayout);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectLayout, false);

		if (isNew) {
			objectLayout.setNew(false);
		}

		objectLayout.resetOriginalValues();

		return objectLayout;
	}

	/**
	 * Returns the object layout with the primary key or throws a <code>NoSuchObjectLayoutException</code> if it could not be found.
	 *
	 * @param objectLayoutId the primary key of the object layout
	 * @return the object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	@Override
	public ObjectLayout findByPrimaryKey(long objectLayoutId)
		throws NoSuchObjectLayoutException {

		return findByPrimaryKey((Serializable)objectLayoutId);
	}

	/**
	 * Returns the object layout with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectLayoutId the primary key of the object layout
	 * @return the object layout, or <code>null</code> if a object layout with the primary key could not be found
	 */
	@Override
	public ObjectLayout fetchByPrimaryKey(long objectLayoutId) {
		return fetchByPrimaryKey((Serializable)objectLayoutId);
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
		return "objectLayoutId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTLAYOUT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectLayoutModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object layout persistence.
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
			_SQL_SELECT_OBJECTLAYOUT_WHERE, _SQL_COUNT_OBJECTLAYOUT_WHERE,
			ObjectLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"objectLayout.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, ObjectLayout::getUuid));

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
				_SQL_SELECT_OBJECTLAYOUT_WHERE, _SQL_COUNT_OBJECTLAYOUT_WHERE,
				ObjectLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectLayout.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, ObjectLayout::getUuid),
				new FinderColumn<>(
					"objectLayout.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, ObjectLayout::getCompanyId));

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
				_SQL_SELECT_OBJECTLAYOUT_WHERE, _SQL_COUNT_OBJECTLAYOUT_WHERE,
				ObjectLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectLayout.", "objectDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectLayout::getObjectDefinitionId));

		_collectionPersistenceFinderByC_DOL = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_DOL",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "defaultObjectLayout"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_DOL",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"companyId", "defaultObjectLayout"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_DOL",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"companyId", "defaultObjectLayout"}, false),
			_SQL_SELECT_OBJECTLAYOUT_WHERE, _SQL_COUNT_OBJECTLAYOUT_WHERE,
			ObjectLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"objectLayout.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, ObjectLayout::getCompanyId),
			new FinderColumn<>(
				"objectLayout.", "defaultObjectLayout",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				ObjectLayout::isDefaultObjectLayout));

		_collectionPersistenceFinderByODI_DOL =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_DOL",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectDefinitionId", "defaultObjectLayout"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI_DOL",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"objectDefinitionId", "defaultObjectLayout"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI_DOL",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"objectDefinitionId", "defaultObjectLayout"},
					false),
				_SQL_SELECT_OBJECTLAYOUT_WHERE, _SQL_COUNT_OBJECTLAYOUT_WHERE,
				ObjectLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectLayout.", "objectDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectLayout::getObjectDefinitionId),
				new FinderColumn<>(
					"objectLayout.", "defaultObjectLayout",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					ObjectLayout::isDefaultObjectLayout));

		ObjectLayoutUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectLayoutUtil.setPersistence(null);

		entityCache.removeCache(ObjectLayoutImpl.class.getName());
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
		ObjectLayoutModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTLAYOUT =
		"SELECT objectLayout FROM ObjectLayout objectLayout";

	private static final String _SQL_SELECT_OBJECTLAYOUT_WHERE =
		"SELECT objectLayout FROM ObjectLayout objectLayout WHERE ";

	private static final String _SQL_COUNT_OBJECTLAYOUT_WHERE =
		"SELECT COUNT(objectLayout) FROM ObjectLayout objectLayout WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1915758923