/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectActionException;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectActionTable;
import com.liferay.object.model.impl.ObjectActionImpl;
import com.liferay.object.model.impl.ObjectActionModelImpl;
import com.liferay.object.service.persistence.ObjectActionPersistence;
import com.liferay.object.service.persistence.ObjectActionUtil;
import com.liferay.object.service.persistence.impl.constants.ObjectPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
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
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the object action service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectActionPersistence.class)
public class ObjectActionPersistenceImpl
	extends BasePersistenceImpl<ObjectAction, NoSuchObjectActionException>
	implements ObjectActionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectActionUtil</code> to access the object action persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectActionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ObjectAction, NoSuchObjectActionException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object actions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object action in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByUuid_First(
			String uuid, OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object action in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByUuid_First(
		String uuid, OrderByComparator<ObjectAction> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object actions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object actions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object actions
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ObjectAction, NoSuchObjectActionException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object actions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object action in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object action in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectAction> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object actions where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object actions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object actions
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ObjectAction, NoSuchObjectActionException>
			_collectionPersistenceFinderByObjectDefinitionId;

	/**
	 * Returns an ordered range of all the object actions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectDefinitionId.find(
			finderCache, new Object[] {objectDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object action in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		return _collectionPersistenceFinderByObjectDefinitionId.findFirst(
			finderCache, new Object[] {objectDefinitionId}, orderByComparator);
	}

	/**
	 * Returns the first object action in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectAction> orderByComparator) {

		return _collectionPersistenceFinderByObjectDefinitionId.fetchFirst(
			finderCache, new Object[] {objectDefinitionId}, orderByComparator);
	}

	/**
	 * Removes all the object actions where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByObjectDefinitionId(long objectDefinitionId) {
		_collectionPersistenceFinderByObjectDefinitionId.remove(
			finderCache, new Object[] {objectDefinitionId});
	}

	/**
	 * Returns the number of object actions where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object actions
	 */
	@Override
	public int countByObjectDefinitionId(long objectDefinitionId) {
		return _collectionPersistenceFinderByObjectDefinitionId.count(
			finderCache, new Object[] {objectDefinitionId});
	}

	private UniquePersistenceFinder<ObjectAction, NoSuchObjectActionException>
		_uniquePersistenceFinderByODI_N;

	/**
	 * Returns the object action where objectDefinitionId = &#63; and name = &#63; or throws a <code>NoSuchObjectActionException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByODI_N(long objectDefinitionId, String name)
		throws NoSuchObjectActionException {

		return _uniquePersistenceFinderByODI_N.find(
			finderCache, new Object[] {objectDefinitionId, name});
	}

	/**
	 * Returns the object action where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByODI_N(
		long objectDefinitionId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByODI_N.fetch(
			finderCache, new Object[] {objectDefinitionId, name},
			useFinderCache);
	}

	/**
	 * Removes the object action where objectDefinitionId = &#63; and name = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the object action that was removed
	 */
	@Override
	public ObjectAction removeByODI_N(long objectDefinitionId, String name)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = findByODI_N(objectDefinitionId, name);

		return remove(objectAction);
	}

	/**
	 * Returns the number of object actions where objectDefinitionId = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the number of matching object actions
	 */
	@Override
	public int countByODI_N(long objectDefinitionId, String name) {
		return _uniquePersistenceFinderByODI_N.count(
			finderCache, new Object[] {objectDefinitionId, name});
	}

	private CollectionPersistenceFinder
		<ObjectAction, NoSuchObjectActionException>
			_collectionPersistenceFinderByA_OAEK;

	/**
	 * Returns an ordered range of all the object actions where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByA_OAEK(
		boolean active, String objectActionExecutorKey, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByA_OAEK.find(
			finderCache, new Object[] {active, objectActionExecutorKey}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object action in the ordered set where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByA_OAEK_First(
			boolean active, String objectActionExecutorKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		return _collectionPersistenceFinderByA_OAEK.findFirst(
			finderCache, new Object[] {active, objectActionExecutorKey},
			orderByComparator);
	}

	/**
	 * Returns the first object action in the ordered set where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByA_OAEK_First(
		boolean active, String objectActionExecutorKey,
		OrderByComparator<ObjectAction> orderByComparator) {

		return _collectionPersistenceFinderByA_OAEK.fetchFirst(
			finderCache, new Object[] {active, objectActionExecutorKey},
			orderByComparator);
	}

	/**
	 * Removes all the object actions where active = &#63; and objectActionExecutorKey = &#63; from the database.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 */
	@Override
	public void removeByA_OAEK(boolean active, String objectActionExecutorKey) {
		_collectionPersistenceFinderByA_OAEK.remove(
			finderCache, new Object[] {active, objectActionExecutorKey});
	}

	/**
	 * Returns the number of object actions where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @return the number of matching object actions
	 */
	@Override
	public int countByA_OAEK(boolean active, String objectActionExecutorKey) {
		return _collectionPersistenceFinderByA_OAEK.count(
			finderCache, new Object[] {active, objectActionExecutorKey});
	}

	private UniquePersistenceFinder<ObjectAction, NoSuchObjectActionException>
		_uniquePersistenceFinderByERC_C_ODI;

	/**
	 * Returns the object action where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or throws a <code>NoSuchObjectActionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectActionException {

		return _uniquePersistenceFinderByERC_C_ODI.find(
			finderCache,
			new Object[] {
				externalReferenceCode, companyId, objectDefinitionId
			});
	}

	/**
	 * Returns the object action where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C_ODI.fetch(
			finderCache,
			new Object[] {externalReferenceCode, companyId, objectDefinitionId},
			useFinderCache);
	}

	/**
	 * Removes the object action where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the object action that was removed
	 */
	@Override
	public ObjectAction removeByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = findByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);

		return remove(objectAction);
	}

	/**
	 * Returns the number of object actions where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object actions
	 */
	@Override
	public int countByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId) {

		return _uniquePersistenceFinderByERC_C_ODI.count(
			finderCache,
			new Object[] {
				externalReferenceCode, companyId, objectDefinitionId
			});
	}

	private CollectionPersistenceFinder
		<ObjectAction, NoSuchObjectActionException>
			_collectionPersistenceFinderByC_A_OATK;

	/**
	 * Returns an ordered range of all the object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey,
		int start, int end, OrderByComparator<ObjectAction> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A_OATK.find(
			finderCache,
			new Object[] {companyId, active, objectActionTriggerKey}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object action in the ordered set where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByC_A_OATK_First(
			long companyId, boolean active, String objectActionTriggerKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		return _collectionPersistenceFinderByC_A_OATK.findFirst(
			finderCache,
			new Object[] {companyId, active, objectActionTriggerKey},
			orderByComparator);
	}

	/**
	 * Returns the first object action in the ordered set where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByC_A_OATK_First(
		long companyId, boolean active, String objectActionTriggerKey,
		OrderByComparator<ObjectAction> orderByComparator) {

		return _collectionPersistenceFinderByC_A_OATK.fetchFirst(
			finderCache,
			new Object[] {companyId, active, objectActionTriggerKey},
			orderByComparator);
	}

	/**
	 * Removes all the object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 */
	@Override
	public void removeByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey) {

		_collectionPersistenceFinderByC_A_OATK.remove(
			finderCache,
			new Object[] {companyId, active, objectActionTriggerKey});
	}

	/**
	 * Returns the number of object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the number of matching object actions
	 */
	@Override
	public int countByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey) {

		return _collectionPersistenceFinderByC_A_OATK.count(
			finderCache,
			new Object[] {companyId, active, objectActionTriggerKey});
	}

	private CollectionPersistenceFinder
		<ObjectAction, NoSuchObjectActionException>
			_collectionPersistenceFinderByO_A_OATK;

	/**
	 * Returns an ordered range of all the object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByO_A_OATK(
		long objectDefinitionId, boolean active, String objectActionTriggerKey,
		int start, int end, OrderByComparator<ObjectAction> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByO_A_OATK.find(
			finderCache,
			new Object[] {objectDefinitionId, active, objectActionTriggerKey},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object action in the ordered set where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByO_A_OATK_First(
			long objectDefinitionId, boolean active,
			String objectActionTriggerKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		return _collectionPersistenceFinderByO_A_OATK.findFirst(
			finderCache,
			new Object[] {objectDefinitionId, active, objectActionTriggerKey},
			orderByComparator);
	}

	/**
	 * Returns the first object action in the ordered set where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByO_A_OATK_First(
		long objectDefinitionId, boolean active, String objectActionTriggerKey,
		OrderByComparator<ObjectAction> orderByComparator) {

		return _collectionPersistenceFinderByO_A_OATK.fetchFirst(
			finderCache,
			new Object[] {objectDefinitionId, active, objectActionTriggerKey},
			orderByComparator);
	}

	/**
	 * Removes all the object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 */
	@Override
	public void removeByO_A_OATK(
		long objectDefinitionId, boolean active,
		String objectActionTriggerKey) {

		_collectionPersistenceFinderByO_A_OATK.remove(
			finderCache,
			new Object[] {objectDefinitionId, active, objectActionTriggerKey});
	}

	/**
	 * Returns the number of object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the number of matching object actions
	 */
	@Override
	public int countByO_A_OATK(
		long objectDefinitionId, boolean active,
		String objectActionTriggerKey) {

		return _collectionPersistenceFinderByO_A_OATK.count(
			finderCache,
			new Object[] {objectDefinitionId, active, objectActionTriggerKey});
	}

	private UniquePersistenceFinder<ObjectAction, NoSuchObjectActionException>
		_uniquePersistenceFinderByODI_A_N_OATK;

	/**
	 * Returns the object action where objectDefinitionId = &#63; and active = &#63; and name = &#63; and objectActionTriggerKey = &#63; or throws a <code>NoSuchObjectActionException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param name the name
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByODI_A_N_OATK(
			long objectDefinitionId, boolean active, String name,
			String objectActionTriggerKey)
		throws NoSuchObjectActionException {

		return _uniquePersistenceFinderByODI_A_N_OATK.find(
			finderCache,
			new Object[] {
				objectDefinitionId, active, name, objectActionTriggerKey
			});
	}

	/**
	 * Returns the object action where objectDefinitionId = &#63; and active = &#63; and name = &#63; and objectActionTriggerKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param name the name
	 * @param objectActionTriggerKey the object action trigger key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByODI_A_N_OATK(
		long objectDefinitionId, boolean active, String name,
		String objectActionTriggerKey, boolean useFinderCache) {

		return _uniquePersistenceFinderByODI_A_N_OATK.fetch(
			finderCache,
			new Object[] {
				objectDefinitionId, active, name, objectActionTriggerKey
			},
			useFinderCache);
	}

	/**
	 * Removes the object action where objectDefinitionId = &#63; and active = &#63; and name = &#63; and objectActionTriggerKey = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param name the name
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the object action that was removed
	 */
	@Override
	public ObjectAction removeByODI_A_N_OATK(
			long objectDefinitionId, boolean active, String name,
			String objectActionTriggerKey)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = findByODI_A_N_OATK(
			objectDefinitionId, active, name, objectActionTriggerKey);

		return remove(objectAction);
	}

	/**
	 * Returns the number of object actions where objectDefinitionId = &#63; and active = &#63; and name = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param name the name
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the number of matching object actions
	 */
	@Override
	public int countByODI_A_N_OATK(
		long objectDefinitionId, boolean active, String name,
		String objectActionTriggerKey) {

		return _uniquePersistenceFinderByODI_A_N_OATK.count(
			finderCache,
			new Object[] {
				objectDefinitionId, active, name, objectActionTriggerKey
			});
	}

	public ObjectActionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("active", "active_");
		dbColumnNames.put("system", "system_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectAction.class);

		setModelImplClass(ObjectActionImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectActionTable.INSTANCE);
	}

	/**
	 * Creates a new object action with the primary key. Does not add the object action to the database.
	 *
	 * @param objectActionId the primary key for the new object action
	 * @return the new object action
	 */
	@Override
	public ObjectAction create(long objectActionId) {
		ObjectAction objectAction = new ObjectActionImpl();

		objectAction.setNew(true);
		objectAction.setPrimaryKey(objectActionId);

		String uuid = PortalUUIDUtil.generate();

		objectAction.setUuid(uuid);

		objectAction.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectAction;
	}

	/**
	 * Removes the object action with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectActionId the primary key of the object action
	 * @return the object action that was removed
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	@Override
	public ObjectAction remove(long objectActionId)
		throws NoSuchObjectActionException {

		return remove((Serializable)objectActionId);
	}

	@Override
	protected ObjectAction removeImpl(ObjectAction objectAction) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectAction)) {
				objectAction = (ObjectAction)session.get(
					ObjectActionImpl.class, objectAction.getPrimaryKeyObj());
			}

			if (objectAction != null) {
				session.delete(objectAction);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectAction != null) {
			clearCache(objectAction);
		}

		return objectAction;
	}

	@Override
	public ObjectAction updateImpl(ObjectAction objectAction) {
		boolean isNew = objectAction.isNew();

		if (!(objectAction instanceof ObjectActionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectAction.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectAction);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectAction proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectAction implementation " +
					objectAction.getClass());
		}

		ObjectActionModelImpl objectActionModelImpl =
			(ObjectActionModelImpl)objectAction;

		if (Validator.isNull(objectAction.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectAction.setUuid(uuid);
		}

		if (Validator.isNull(objectAction.getExternalReferenceCode())) {
			objectAction.setExternalReferenceCode(objectAction.getUuid());
		}
		else {
			if (!Objects.equals(
					objectActionModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					objectAction.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = objectAction.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = objectAction.getPrimaryKey();
					}

					try {
						objectAction.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ObjectAction.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								objectAction.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectAction.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectAction.setCreateDate(date);
			}
			else {
				objectAction.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!objectActionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectAction.setModifiedDate(date);
			}
			else {
				objectAction.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectAction);
			}
			else {
				objectAction = (ObjectAction)session.merge(objectAction);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectAction, false);

		if (isNew) {
			objectAction.setNew(false);
		}

		objectAction.resetOriginalValues();

		return objectAction;
	}

	/**
	 * Returns the object action with the primary key or throws a <code>NoSuchObjectActionException</code> if it could not be found.
	 *
	 * @param objectActionId the primary key of the object action
	 * @return the object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	@Override
	public ObjectAction findByPrimaryKey(long objectActionId)
		throws NoSuchObjectActionException {

		return findByPrimaryKey((Serializable)objectActionId);
	}

	/**
	 * Returns the object action with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectActionId the primary key of the object action
	 * @return the object action, or <code>null</code> if a object action with the primary key could not be found
	 */
	@Override
	public ObjectAction fetchByPrimaryKey(long objectActionId) {
		return fetchByPrimaryKey((Serializable)objectActionId);
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
		return "objectActionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTACTION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectActionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object action persistence.
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
			_SQL_SELECT_OBJECTACTION_WHERE, _SQL_COUNT_OBJECTACTION_WHERE,
			ObjectActionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"objectAction.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, ObjectAction::getUuid));

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
				_SQL_SELECT_OBJECTACTION_WHERE, _SQL_COUNT_OBJECTACTION_WHERE,
				ObjectActionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"objectAction.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, ObjectAction::getUuid),
				new FinderColumn<>(
					"objectAction.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, ObjectAction::getCompanyId));

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
				_SQL_SELECT_OBJECTACTION_WHERE, _SQL_COUNT_OBJECTACTION_WHERE,
				ObjectActionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"objectAction.", "objectDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectAction::getObjectDefinitionId));

		_uniquePersistenceFinderByODI_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByODI_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"objectDefinitionId", "name"}, 0, 2, false,
				ObjectAction::getObjectDefinitionId,
				convertNullFunction(ObjectAction::getName)),
			_SQL_SELECT_OBJECTACTION_WHERE, "",
			new FinderColumn<>(
				"objectAction.", "objectDefinitionId", FinderColumn.Type.LONG,
				"=", true, true, ObjectAction::getObjectDefinitionId),
			new FinderColumn<>(
				"objectAction.", "name", FinderColumn.Type.STRING, "=", true,
				true, ObjectAction::getName));

		_collectionPersistenceFinderByA_OAEK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByA_OAEK",
					new String[] {
						Boolean.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"active_", "objectActionExecutorKey"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByA_OAEK",
					new String[] {
						Boolean.class.getName(), String.class.getName()
					},
					new String[] {"active_", "objectActionExecutorKey"}, 0, 2,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByA_OAEK",
					new String[] {
						Boolean.class.getName(), String.class.getName()
					},
					new String[] {"active_", "objectActionExecutorKey"}, 0, 2,
					false, null),
				_SQL_SELECT_OBJECTACTION_WHERE, _SQL_COUNT_OBJECTACTION_WHERE,
				ObjectActionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"objectAction.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					ObjectAction::isActive),
				new FinderColumn<>(
					"objectAction.", "objectActionExecutorKey",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectAction::getObjectActionExecutorKey));

		_uniquePersistenceFinderByERC_C_ODI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C_ODI",
				new String[] {
					String.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"externalReferenceCode", "companyId", "objectDefinitionId"
				},
				0, 1, false,
				convertNullFunction(ObjectAction::getExternalReferenceCode),
				ObjectAction::getCompanyId,
				ObjectAction::getObjectDefinitionId),
			_SQL_SELECT_OBJECTACTION_WHERE, "",
			new FinderColumn<>(
				"objectAction.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectAction::getExternalReferenceCode),
			new FinderColumn<>(
				"objectAction.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, ObjectAction::getCompanyId),
			new FinderColumn<>(
				"objectAction.", "objectDefinitionId", FinderColumn.Type.LONG,
				"=", true, true, ObjectAction::getObjectDefinitionId));

		_collectionPersistenceFinderByC_A_OATK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A_OATK",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "active_", "objectActionTriggerKey"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A_OATK",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {
						"companyId", "active_", "objectActionTriggerKey"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByC_A_OATK",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {
						"companyId", "active_", "objectActionTriggerKey"
					},
					0, 4, false, null),
				_SQL_SELECT_OBJECTACTION_WHERE, _SQL_COUNT_OBJECTACTION_WHERE,
				ObjectActionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"objectAction.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, ObjectAction::getCompanyId),
				new FinderColumn<>(
					"objectAction.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					ObjectAction::isActive),
				new FinderColumn<>(
					"objectAction.", "objectActionTriggerKey",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectAction::getObjectActionTriggerKey));

		_collectionPersistenceFinderByO_A_OATK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByO_A_OATK",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"objectDefinitionId", "active_",
						"objectActionTriggerKey"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByO_A_OATK",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {
						"objectDefinitionId", "active_",
						"objectActionTriggerKey"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByO_A_OATK",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {
						"objectDefinitionId", "active_",
						"objectActionTriggerKey"
					},
					0, 4, false, null),
				_SQL_SELECT_OBJECTACTION_WHERE, _SQL_COUNT_OBJECTACTION_WHERE,
				ObjectActionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"objectAction.", "objectDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectAction::getObjectDefinitionId),
				new FinderColumn<>(
					"objectAction.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					ObjectAction::isActive),
				new FinderColumn<>(
					"objectAction.", "objectActionTriggerKey",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectAction::getObjectActionTriggerKey));

		_uniquePersistenceFinderByODI_A_N_OATK = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByODI_A_N_OATK",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					String.class.getName(), String.class.getName()
				},
				new String[] {
					"objectDefinitionId", "active_", "name",
					"objectActionTriggerKey"
				},
				0, 12, false, ObjectAction::getObjectDefinitionId,
				ObjectAction::isActive,
				convertNullFunction(ObjectAction::getName),
				convertNullFunction(ObjectAction::getObjectActionTriggerKey)),
			_SQL_SELECT_OBJECTACTION_WHERE, "",
			new FinderColumn<>(
				"objectAction.", "objectDefinitionId", FinderColumn.Type.LONG,
				"=", true, true, ObjectAction::getObjectDefinitionId),
			new FinderColumn<>(
				"objectAction.", "active", "active_", FinderColumn.Type.BOOLEAN,
				"=", true, true, ObjectAction::isActive),
			new FinderColumn<>(
				"objectAction.", "name", FinderColumn.Type.STRING, "=", true,
				true, ObjectAction::getName),
			new FinderColumn<>(
				"objectAction.", "objectActionTriggerKey",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectAction::getObjectActionTriggerKey));

		ObjectActionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectActionUtil.setPersistence(null);

		entityCache.removeCache(ObjectActionImpl.class.getName());
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
		ObjectActionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTACTION =
		"SELECT objectAction FROM ObjectAction objectAction";

	private static final String _SQL_SELECT_OBJECTACTION_WHERE =
		"SELECT objectAction FROM ObjectAction objectAction WHERE ";

	private static final String _SQL_COUNT_OBJECTACTION_WHERE =
		"SELECT COUNT(objectAction) FROM ObjectAction objectAction WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ObjectAction exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectActionPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "active", "system"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-63572318