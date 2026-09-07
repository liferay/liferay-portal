/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.impl.ObjectEntryImpl;
import com.liferay.object.model.impl.ObjectEntryModelImpl;
import com.liferay.object.service.persistence.ObjectEntryPersistence;
import com.liferay.object.service.persistence.ObjectEntryUtil;
import com.liferay.object.service.persistence.impl.constants.ObjectPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
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
 * The persistence implementation for the object entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectEntryPersistence.class)
public class ObjectEntryPersistenceImpl
	extends BasePersistenceImpl<ObjectEntry, NoSuchObjectEntryException>
	implements ObjectEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectEntryUtil</code> to access the object entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<ObjectEntry, NoSuchObjectEntryException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByUuid_First(
			String uuid, OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByUuid_First(
		String uuid, OrderByComparator<ObjectEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<ObjectEntry, NoSuchObjectEntryException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the object entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchObjectEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchObjectEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the object entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the object entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the object entry that was removed
	 */
	@Override
	public ObjectEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = findByUUID_G(uuid, groupId);

		return remove(objectEntry);
	}

	/**
	 * Returns the number of object entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<ObjectEntry, NoSuchObjectEntryException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private UniquePersistenceFinder<ObjectEntry, NoSuchObjectEntryException>
		_uniquePersistenceFinderByHeadObjectEntryId;

	/**
	 * Returns the object entry where headObjectEntryId = &#63; or throws a <code>NoSuchObjectEntryException</code> if it could not be found.
	 *
	 * @param headObjectEntryId the head object entry ID
	 * @return the matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByHeadObjectEntryId(long headObjectEntryId)
		throws NoSuchObjectEntryException {

		return _uniquePersistenceFinderByHeadObjectEntryId.find(
			finderCache, new Object[] {headObjectEntryId});
	}

	/**
	 * Returns the object entry where headObjectEntryId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param headObjectEntryId the head object entry ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByHeadObjectEntryId(
		long headObjectEntryId, boolean useFinderCache) {

		return _uniquePersistenceFinderByHeadObjectEntryId.fetch(
			finderCache, new Object[] {headObjectEntryId}, useFinderCache);
	}

	/**
	 * Removes the object entry where headObjectEntryId = &#63; from the database.
	 *
	 * @param headObjectEntryId the head object entry ID
	 * @return the object entry that was removed
	 */
	@Override
	public ObjectEntry removeByHeadObjectEntryId(long headObjectEntryId)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = findByHeadObjectEntryId(headObjectEntryId);

		return remove(objectEntry);
	}

	/**
	 * Returns the number of object entries where headObjectEntryId = &#63;.
	 *
	 * @param headObjectEntryId the head object entry ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByHeadObjectEntryId(long headObjectEntryId) {
		return _uniquePersistenceFinderByHeadObjectEntryId.count(
			finderCache, new Object[] {headObjectEntryId});
	}

	private CollectionPersistenceFinder<ObjectEntry, NoSuchObjectEntryException>
		_collectionPersistenceFinderByObjectDefinitionId;

	/**
	 * Returns an ordered range of all the object entries where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectDefinitionId.find(
			finderCache, new Object[] {objectDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		return _collectionPersistenceFinderByObjectDefinitionId.findFirst(
			finderCache, new Object[] {objectDefinitionId}, orderByComparator);
	}

	/**
	 * Returns the first object entry in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return _collectionPersistenceFinderByObjectDefinitionId.fetchFirst(
			finderCache, new Object[] {objectDefinitionId}, orderByComparator);
	}

	/**
	 * Removes all the object entries where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByObjectDefinitionId(long objectDefinitionId) {
		_collectionPersistenceFinderByObjectDefinitionId.remove(
			finderCache, new Object[] {objectDefinitionId});
	}

	/**
	 * Returns the number of object entries where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByObjectDefinitionId(long objectDefinitionId) {
		return _collectionPersistenceFinderByObjectDefinitionId.count(
			finderCache, new Object[] {objectDefinitionId});
	}

	private CollectionPersistenceFinder<ObjectEntry, NoSuchObjectEntryException>
		_collectionPersistenceFinderByG_ODI;

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_ODI(
		long groupId, long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_ODI.find(
			finderCache, new Object[] {groupId, objectDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByG_ODI_First(
			long groupId, long objectDefinitionId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		return _collectionPersistenceFinderByG_ODI.findFirst(
			finderCache, new Object[] {groupId, objectDefinitionId},
			orderByComparator);
	}

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByG_ODI_First(
		long groupId, long objectDefinitionId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_ODI.fetchFirst(
			finderCache, new Object[] {groupId, objectDefinitionId},
			orderByComparator);
	}

	/**
	 * Removes all the object entries where groupId = &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByG_ODI(long groupId, long objectDefinitionId) {
		_collectionPersistenceFinderByG_ODI.remove(
			finderCache, new Object[] {groupId, objectDefinitionId});
	}

	/**
	 * Returns the number of object entries where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByG_ODI(long groupId, long objectDefinitionId) {
		return _collectionPersistenceFinderByG_ODI.count(
			finderCache, new Object[] {groupId, objectDefinitionId});
	}

	private CollectionPersistenceFinder<ObjectEntry, NoSuchObjectEntryException>
		_collectionPersistenceFinderByG_OEFI;

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_OEFI(
		long groupId, long objectEntryFolderId, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_OEFI.find(
			finderCache, new Object[] {groupId, objectEntryFolderId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByG_OEFI_First(
			long groupId, long objectEntryFolderId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		return _collectionPersistenceFinderByG_OEFI.findFirst(
			finderCache, new Object[] {groupId, objectEntryFolderId},
			orderByComparator);
	}

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByG_OEFI_First(
		long groupId, long objectEntryFolderId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_OEFI.fetchFirst(
			finderCache, new Object[] {groupId, objectEntryFolderId},
			orderByComparator);
	}

	/**
	 * Removes all the object entries where groupId = &#63; and objectEntryFolderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 */
	@Override
	public void removeByG_OEFI(long groupId, long objectEntryFolderId) {
		_collectionPersistenceFinderByG_OEFI.remove(
			finderCache, new Object[] {groupId, objectEntryFolderId});
	}

	/**
	 * Returns the number of object entries where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByG_OEFI(long groupId, long objectEntryFolderId) {
		return _collectionPersistenceFinderByG_OEFI.count(
			finderCache, new Object[] {groupId, objectEntryFolderId});
	}

	private CollectionPersistenceFinder<ObjectEntry, NoSuchObjectEntryException>
		_collectionPersistenceFinderByU_ODI;

	/**
	 * Returns an ordered range of all the object entries where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByU_ODI(
		long userId, long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_ODI.find(
			finderCache, new Object[] {userId, objectDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry in the ordered set where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByU_ODI_First(
			long userId, long objectDefinitionId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		return _collectionPersistenceFinderByU_ODI.findFirst(
			finderCache, new Object[] {userId, objectDefinitionId},
			orderByComparator);
	}

	/**
	 * Returns the first object entry in the ordered set where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByU_ODI_First(
		long userId, long objectDefinitionId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return _collectionPersistenceFinderByU_ODI.fetchFirst(
			finderCache, new Object[] {userId, objectDefinitionId},
			orderByComparator);
	}

	/**
	 * Removes all the object entries where userId = &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByU_ODI(long userId, long objectDefinitionId) {
		_collectionPersistenceFinderByU_ODI.remove(
			finderCache, new Object[] {userId, objectDefinitionId});
	}

	/**
	 * Returns the number of object entries where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByU_ODI(long userId, long objectDefinitionId) {
		return _collectionPersistenceFinderByU_ODI.count(
			finderCache, new Object[] {userId, objectDefinitionId});
	}

	private CollectionPersistenceFinder<ObjectEntry, NoSuchObjectEntryException>
		_collectionPersistenceFinderByODI_NotS;

	/**
	 * Returns all the object entries where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @return the matching object entries
	 */
	@Override
	public List<ObjectEntry> findByODI_NotS(
		long objectDefinitionId, int status) {

		return findByODI_NotS(
			objectDefinitionId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object entries where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByODI_NotS(
		long objectDefinitionId, int status, int start, int end) {

		return findByODI_NotS(objectDefinitionId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entries where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByODI_NotS(
		long objectDefinitionId, int status, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return findByODI_NotS(
			objectDefinitionId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entries where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByODI_NotS(
		long objectDefinitionId, int status, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI_NotS.find(
			finderCache, new Object[] {objectDefinitionId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry in the ordered set where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByODI_NotS_First(
			long objectDefinitionId, int status,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		return _collectionPersistenceFinderByODI_NotS.findFirst(
			finderCache, new Object[] {objectDefinitionId, status},
			orderByComparator);
	}

	/**
	 * Returns the first object entry in the ordered set where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByODI_NotS_First(
		long objectDefinitionId, int status,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return _collectionPersistenceFinderByODI_NotS.fetchFirst(
			finderCache, new Object[] {objectDefinitionId, status},
			orderByComparator);
	}

	/**
	 * Removes all the object entries where objectDefinitionId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 */
	@Override
	public void removeByODI_NotS(long objectDefinitionId, int status) {
		_collectionPersistenceFinderByODI_NotS.remove(
			finderCache, new Object[] {objectDefinitionId, status});
	}

	/**
	 * Returns the number of object entries where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @return the number of matching object entries
	 */
	@Override
	public int countByODI_NotS(long objectDefinitionId, int status) {
		return _collectionPersistenceFinderByODI_NotS.count(
			finderCache, new Object[] {objectDefinitionId, status});
	}

	private CollectionPersistenceFinder<ObjectEntry, NoSuchObjectEntryException>
		_collectionPersistenceFinderByROEI_NotS;

	/**
	 * Returns all the object entries where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @return the matching object entries
	 */
	@Override
	public List<ObjectEntry> findByROEI_NotS(
		long rootObjectEntryId, int status) {

		return findByROEI_NotS(
			rootObjectEntryId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object entries where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByROEI_NotS(
		long rootObjectEntryId, int status, int start, int end) {

		return findByROEI_NotS(rootObjectEntryId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entries where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByROEI_NotS(
		long rootObjectEntryId, int status, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return findByROEI_NotS(
			rootObjectEntryId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entries where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByROEI_NotS(
		long rootObjectEntryId, int status, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByROEI_NotS.find(
			finderCache, new Object[] {rootObjectEntryId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry in the ordered set where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByROEI_NotS_First(
			long rootObjectEntryId, int status,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		return _collectionPersistenceFinderByROEI_NotS.findFirst(
			finderCache, new Object[] {rootObjectEntryId, status},
			orderByComparator);
	}

	/**
	 * Returns the first object entry in the ordered set where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByROEI_NotS_First(
		long rootObjectEntryId, int status,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return _collectionPersistenceFinderByROEI_NotS.fetchFirst(
			finderCache, new Object[] {rootObjectEntryId, status},
			orderByComparator);
	}

	/**
	 * Removes all the object entries where rootObjectEntryId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 */
	@Override
	public void removeByROEI_NotS(long rootObjectEntryId, int status) {
		_collectionPersistenceFinderByROEI_NotS.remove(
			finderCache, new Object[] {rootObjectEntryId, status});
	}

	/**
	 * Returns the number of object entries where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @return the number of matching object entries
	 */
	@Override
	public int countByROEI_NotS(long rootObjectEntryId, int status) {
		return _collectionPersistenceFinderByROEI_NotS.count(
			finderCache, new Object[] {rootObjectEntryId, status});
	}

	private CollectionPersistenceFinder<ObjectEntry, NoSuchObjectEntryException>
		_collectionPersistenceFinderByG_C_OEFI;

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_C_OEFI(
		long groupId, long companyId, long objectEntryFolderId, int start,
		int end, OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_OEFI.find(
			finderCache, new Object[] {groupId, companyId, objectEntryFolderId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByG_C_OEFI_First(
			long groupId, long companyId, long objectEntryFolderId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		return _collectionPersistenceFinderByG_C_OEFI.findFirst(
			finderCache, new Object[] {groupId, companyId, objectEntryFolderId},
			orderByComparator);
	}

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByG_C_OEFI_First(
		long groupId, long companyId, long objectEntryFolderId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_OEFI.fetchFirst(
			finderCache, new Object[] {groupId, companyId, objectEntryFolderId},
			orderByComparator);
	}

	/**
	 * Removes all the object entries where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 */
	@Override
	public void removeByG_C_OEFI(
		long groupId, long companyId, long objectEntryFolderId) {

		_collectionPersistenceFinderByG_C_OEFI.remove(
			finderCache,
			new Object[] {groupId, companyId, objectEntryFolderId});
	}

	/**
	 * Returns the number of object entries where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByG_C_OEFI(
		long groupId, long companyId, long objectEntryFolderId) {

		return _collectionPersistenceFinderByG_C_OEFI.count(
			finderCache,
			new Object[] {groupId, companyId, objectEntryFolderId});
	}

	private CollectionPersistenceFinder<ObjectEntry, NoSuchObjectEntryException>
		_collectionPersistenceFinderByG_ODI_S;

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_ODI_S(
		long groupId, long objectDefinitionId, int status, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_ODI_S.find(
			finderCache, new Object[] {groupId, objectDefinitionId, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByG_ODI_S_First(
			long groupId, long objectDefinitionId, int status,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		return _collectionPersistenceFinderByG_ODI_S.findFirst(
			finderCache, new Object[] {groupId, objectDefinitionId, status},
			orderByComparator);
	}

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByG_ODI_S_First(
		long groupId, long objectDefinitionId, int status,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_ODI_S.fetchFirst(
			finderCache, new Object[] {groupId, objectDefinitionId, status},
			orderByComparator);
	}

	/**
	 * Removes all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 */
	@Override
	public void removeByG_ODI_S(
		long groupId, long objectDefinitionId, int status) {

		_collectionPersistenceFinderByG_ODI_S.remove(
			finderCache, new Object[] {groupId, objectDefinitionId, status});
	}

	/**
	 * Returns the number of object entries where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @return the number of matching object entries
	 */
	@Override
	public int countByG_ODI_S(
		long groupId, long objectDefinitionId, int status) {

		return _collectionPersistenceFinderByG_ODI_S.count(
			finderCache, new Object[] {groupId, objectDefinitionId, status});
	}

	private CollectionPersistenceFinder<ObjectEntry, NoSuchObjectEntryException>
		_collectionPersistenceFinderByG_ODI_NotS;

	/**
	 * Returns all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @return the matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_ODI_NotS(
		long groupId, long objectDefinitionId, int status) {

		return findByG_ODI_NotS(
			groupId, objectDefinitionId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_ODI_NotS(
		long groupId, long objectDefinitionId, int status, int start, int end) {

		return findByG_ODI_NotS(
			groupId, objectDefinitionId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_ODI_NotS(
		long groupId, long objectDefinitionId, int status, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return findByG_ODI_NotS(
			groupId, objectDefinitionId, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_ODI_NotS(
		long groupId, long objectDefinitionId, int status, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_ODI_NotS.find(
			finderCache, new Object[] {groupId, objectDefinitionId, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByG_ODI_NotS_First(
			long groupId, long objectDefinitionId, int status,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		return _collectionPersistenceFinderByG_ODI_NotS.findFirst(
			finderCache, new Object[] {groupId, objectDefinitionId, status},
			orderByComparator);
	}

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByG_ODI_NotS_First(
		long groupId, long objectDefinitionId, int status,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_ODI_NotS.fetchFirst(
			finderCache, new Object[] {groupId, objectDefinitionId, status},
			orderByComparator);
	}

	/**
	 * Removes all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 */
	@Override
	public void removeByG_ODI_NotS(
		long groupId, long objectDefinitionId, int status) {

		_collectionPersistenceFinderByG_ODI_NotS.remove(
			finderCache, new Object[] {groupId, objectDefinitionId, status});
	}

	/**
	 * Returns the number of object entries where groupId = &#63; and objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @return the number of matching object entries
	 */
	@Override
	public int countByG_ODI_NotS(
		long groupId, long objectDefinitionId, int status) {

		return _collectionPersistenceFinderByG_ODI_NotS.count(
			finderCache, new Object[] {groupId, objectDefinitionId, status});
	}

	private CollectionPersistenceFinder<ObjectEntry, NoSuchObjectEntryException>
		_collectionPersistenceFinderByU_GtCD_ODI;

	/**
	 * Returns all the object entries where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entries
	 */
	@Override
	public List<ObjectEntry> findByU_GtCD_ODI(
		long userId, Date createDate, long objectDefinitionId) {

		return findByU_GtCD_ODI(
			userId, createDate, objectDefinitionId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entries where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByU_GtCD_ODI(
		long userId, Date createDate, long objectDefinitionId, int start,
		int end) {

		return findByU_GtCD_ODI(
			userId, createDate, objectDefinitionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entries where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByU_GtCD_ODI(
		long userId, Date createDate, long objectDefinitionId, int start,
		int end, OrderByComparator<ObjectEntry> orderByComparator) {

		return findByU_GtCD_ODI(
			userId, createDate, objectDefinitionId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entries where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByU_GtCD_ODI(
		long userId, Date createDate, long objectDefinitionId, int start,
		int end, OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_GtCD_ODI.find(
			finderCache, new Object[] {userId, createDate, objectDefinitionId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry in the ordered set where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByU_GtCD_ODI_First(
			long userId, Date createDate, long objectDefinitionId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		return _collectionPersistenceFinderByU_GtCD_ODI.findFirst(
			finderCache, new Object[] {userId, createDate, objectDefinitionId},
			orderByComparator);
	}

	/**
	 * Returns the first object entry in the ordered set where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByU_GtCD_ODI_First(
		long userId, Date createDate, long objectDefinitionId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return _collectionPersistenceFinderByU_GtCD_ODI.fetchFirst(
			finderCache, new Object[] {userId, createDate, objectDefinitionId},
			orderByComparator);
	}

	/**
	 * Removes all the object entries where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByU_GtCD_ODI(
		long userId, Date createDate, long objectDefinitionId) {

		_collectionPersistenceFinderByU_GtCD_ODI.remove(
			finderCache, new Object[] {userId, createDate, objectDefinitionId});
	}

	/**
	 * Returns the number of object entries where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByU_GtCD_ODI(
		long userId, Date createDate, long objectDefinitionId) {

		return _collectionPersistenceFinderByU_GtCD_ODI.count(
			finderCache, new Object[] {userId, createDate, objectDefinitionId});
	}

	private UniquePersistenceFinder<ObjectEntry, NoSuchObjectEntryException>
		_uniquePersistenceFinderByERC_G_C_ODI;

	/**
	 * Returns the object entry where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; and objectDefinitionId = &#63; or throws a <code>NoSuchObjectEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByERC_G_C_ODI(
			String externalReferenceCode, long groupId, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectEntryException {

		return _uniquePersistenceFinderByERC_G_C_ODI.find(
			finderCache,
			new Object[] {
				externalReferenceCode, groupId, companyId, objectDefinitionId
			});
	}

	/**
	 * Returns the object entry where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByERC_G_C_ODI(
		String externalReferenceCode, long groupId, long companyId,
		long objectDefinitionId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G_C_ODI.fetch(
			finderCache,
			new Object[] {
				externalReferenceCode, groupId, companyId, objectDefinitionId
			},
			useFinderCache);
	}

	/**
	 * Removes the object entry where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the object entry that was removed
	 */
	@Override
	public ObjectEntry removeByERC_G_C_ODI(
			String externalReferenceCode, long groupId, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = findByERC_G_C_ODI(
			externalReferenceCode, groupId, companyId, objectDefinitionId);

		return remove(objectEntry);
	}

	/**
	 * Returns the number of object entries where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByERC_G_C_ODI(
		String externalReferenceCode, long groupId, long companyId,
		long objectDefinitionId) {

		return _uniquePersistenceFinderByERC_G_C_ODI.count(
			finderCache,
			new Object[] {
				externalReferenceCode, groupId, companyId, objectDefinitionId
			});
	}

	public ObjectEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectEntry.class);

		setModelImplClass(ObjectEntryImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectEntryTable.INSTANCE);
	}

	/**
	 * Creates a new object entry with the primary key. Does not add the object entry to the database.
	 *
	 * @param objectEntryId the primary key for the new object entry
	 * @return the new object entry
	 */
	@Override
	public ObjectEntry create(long objectEntryId) {
		ObjectEntry objectEntry = new ObjectEntryImpl();

		objectEntry.setNew(true);
		objectEntry.setPrimaryKey(objectEntryId);

		String uuid = PortalUUIDUtil.generate();

		objectEntry.setUuid(uuid);

		objectEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectEntry;
	}

	/**
	 * Removes the object entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectEntryId the primary key of the object entry
	 * @return the object entry that was removed
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	@Override
	public ObjectEntry remove(long objectEntryId)
		throws NoSuchObjectEntryException {

		return remove((Serializable)objectEntryId);
	}

	@Override
	protected ObjectEntry removeImpl(ObjectEntry objectEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectEntry)) {
				objectEntry = (ObjectEntry)session.get(
					ObjectEntryImpl.class, objectEntry.getPrimaryKeyObj());
			}

			if (objectEntry != null) {
				session.delete(objectEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectEntry != null) {
			clearCache(objectEntry);
		}

		return objectEntry;
	}

	@Override
	public ObjectEntry updateImpl(ObjectEntry objectEntry) {
		boolean isNew = objectEntry.isNew();

		if (!(objectEntry instanceof ObjectEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(objectEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectEntry implementation " +
					objectEntry.getClass());
		}

		ObjectEntryModelImpl objectEntryModelImpl =
			(ObjectEntryModelImpl)objectEntry;

		if (Validator.isNull(objectEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectEntry.setUuid(uuid);
		}

		if (Validator.isNull(objectEntry.getExternalReferenceCode())) {
			objectEntry.setExternalReferenceCode(objectEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					objectEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					objectEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = objectEntry.getCompanyId();

					long groupId = objectEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = objectEntry.getPrimaryKey();
					}

					try {
						objectEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ObjectEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								objectEntry.getExternalReferenceCode(), null));
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

		if (isNew && (objectEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectEntry.setCreateDate(date);
			}
			else {
				objectEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!objectEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectEntry.setModifiedDate(date);
			}
			else {
				objectEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectEntry);
			}
			else {
				objectEntry = (ObjectEntry)session.merge(objectEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectEntry, false);

		if (isNew) {
			objectEntry.setNew(false);
		}

		objectEntry.resetOriginalValues();

		return objectEntry;
	}

	/**
	 * Returns the object entry with the primary key or throws a <code>NoSuchObjectEntryException</code> if it could not be found.
	 *
	 * @param objectEntryId the primary key of the object entry
	 * @return the object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	@Override
	public ObjectEntry findByPrimaryKey(long objectEntryId)
		throws NoSuchObjectEntryException {

		return findByPrimaryKey((Serializable)objectEntryId);
	}

	/**
	 * Returns the object entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectEntryId the primary key of the object entry
	 * @return the object entry, or <code>null</code> if a object entry with the primary key could not be found
	 */
	@Override
	public ObjectEntry fetchByPrimaryKey(long objectEntryId) {
		return fetchByPrimaryKey((Serializable)objectEntryId);
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
		return "objectEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object entry persistence.
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
			_SQL_SELECT_OBJECTENTRY_WHERE, _SQL_COUNT_OBJECTENTRY_WHERE,
			ObjectEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"objectEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, ObjectEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(ObjectEntry::getUuid),
				ObjectEntry::getGroupId),
			_SQL_SELECT_OBJECTENTRY_WHERE, "",
			new FinderColumn<>(
				"objectEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, ObjectEntry::getUuid),
			new FinderColumn<>(
				"objectEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, ObjectEntry::getGroupId));

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
				_SQL_SELECT_OBJECTENTRY_WHERE, _SQL_COUNT_OBJECTENTRY_WHERE,
				ObjectEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, ObjectEntry::getUuid),
				new FinderColumn<>(
					"objectEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, ObjectEntry::getCompanyId));

		_uniquePersistenceFinderByHeadObjectEntryId =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByHeadObjectEntryId",
					new String[] {Long.class.getName()},
					new String[] {"headObjectEntryId"}, 0, 0, false,
					ObjectEntry::getHeadObjectEntryId),
				_SQL_SELECT_OBJECTENTRY_WHERE,
				"objectEntry.objectEntryId != objectEntry.headObjectEntryId",
				new FinderColumn<>(
					"objectEntry.", "headObjectEntryId", FinderColumn.Type.LONG,
					"=", true, true, ObjectEntry::getHeadObjectEntryId));

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
				_SQL_SELECT_OBJECTENTRY_WHERE, _SQL_COUNT_OBJECTENTRY_WHERE,
				ObjectEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"objectEntry.objectEntryId = objectEntry.headObjectEntryId",
				"objectEntry.objectEntryId = objectEntry.headObjectEntryId",
				null,
				new FinderColumn<>(
					"objectEntry.", "objectDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectEntry::getObjectDefinitionId));

		_collectionPersistenceFinderByG_ODI = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_ODI",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "objectDefinitionId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_ODI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "objectDefinitionId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_ODI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "objectDefinitionId"}, false),
			_SQL_SELECT_OBJECTENTRY_WHERE, _SQL_COUNT_OBJECTENTRY_WHERE,
			ObjectEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"objectEntry.objectEntryId = objectEntry.headObjectEntryId",
			"objectEntry.objectEntryId = objectEntry.headObjectEntryId", null,
			new FinderColumn<>(
				"objectEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, ObjectEntry::getGroupId),
			new FinderColumn<>(
				"objectEntry.", "objectDefinitionId", FinderColumn.Type.LONG,
				"=", true, true, ObjectEntry::getObjectDefinitionId));

		_collectionPersistenceFinderByG_OEFI =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_OEFI",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "objectEntryFolderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_OEFI",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "objectEntryFolderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_OEFI",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "objectEntryFolderId"}, false),
				_SQL_SELECT_OBJECTENTRY_WHERE, _SQL_COUNT_OBJECTENTRY_WHERE,
				ObjectEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"objectEntry.objectEntryId = objectEntry.headObjectEntryId",
				"objectEntry.objectEntryId = objectEntry.headObjectEntryId",
				null,
				new FinderColumn<>(
					"objectEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, ObjectEntry::getGroupId),
				new FinderColumn<>(
					"objectEntry.", "objectEntryFolderId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectEntry::getObjectEntryFolderId));

		_collectionPersistenceFinderByU_ODI = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_ODI",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"userId", "objectDefinitionId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_ODI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId", "objectDefinitionId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_ODI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId", "objectDefinitionId"}, false),
			_SQL_SELECT_OBJECTENTRY_WHERE, _SQL_COUNT_OBJECTENTRY_WHERE,
			ObjectEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"objectEntry.objectEntryId = objectEntry.headObjectEntryId",
			"objectEntry.objectEntryId = objectEntry.headObjectEntryId", null,
			new FinderColumn<>(
				"objectEntry.", "userId", FinderColumn.Type.LONG, "=", true,
				true, ObjectEntry::getUserId),
			new FinderColumn<>(
				"objectEntry.", "objectDefinitionId", FinderColumn.Type.LONG,
				"=", true, true, ObjectEntry::getObjectDefinitionId));

		_collectionPersistenceFinderByODI_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_NotS",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectDefinitionId", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByODI_NotS",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"objectDefinitionId", "status"}, false),
				_SQL_SELECT_OBJECTENTRY_WHERE, _SQL_COUNT_OBJECTENTRY_WHERE,
				ObjectEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"objectEntry.objectEntryId = objectEntry.headObjectEntryId",
				"objectEntry.objectEntryId = objectEntry.headObjectEntryId",
				null,
				new FinderColumn<>(
					"objectEntry.", "objectDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectEntry::getObjectDefinitionId),
				new FinderColumn<>(
					"objectEntry.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, ObjectEntry::getStatus));

		_collectionPersistenceFinderByROEI_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByROEI_NotS",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"rootObjectEntryId", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByROEI_NotS",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"rootObjectEntryId", "status"}, false),
				_SQL_SELECT_OBJECTENTRY_WHERE, _SQL_COUNT_OBJECTENTRY_WHERE,
				ObjectEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"objectEntry.objectEntryId = objectEntry.headObjectEntryId",
				"objectEntry.objectEntryId = objectEntry.headObjectEntryId",
				null,
				new FinderColumn<>(
					"objectEntry.", "rootObjectEntryId", FinderColumn.Type.LONG,
					"=", true, true, ObjectEntry::getRootObjectEntryId),
				new FinderColumn<>(
					"objectEntry.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, ObjectEntry::getStatus));

		_collectionPersistenceFinderByG_C_OEFI =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_OEFI",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "companyId", "objectEntryFolderId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_OEFI",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"groupId", "companyId", "objectEntryFolderId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_C_OEFI",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"groupId", "companyId", "objectEntryFolderId"
					},
					false),
				_SQL_SELECT_OBJECTENTRY_WHERE, _SQL_COUNT_OBJECTENTRY_WHERE,
				ObjectEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"objectEntry.objectEntryId = objectEntry.headObjectEntryId",
				"objectEntry.objectEntryId = objectEntry.headObjectEntryId",
				null,
				new FinderColumn<>(
					"objectEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, ObjectEntry::getGroupId),
				new FinderColumn<>(
					"objectEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, ObjectEntry::getCompanyId),
				new FinderColumn<>(
					"objectEntry.", "objectEntryFolderId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectEntry::getObjectEntryFolderId));

		_collectionPersistenceFinderByG_ODI_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_ODI_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "objectDefinitionId", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_ODI_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "objectDefinitionId", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_ODI_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "objectDefinitionId", "status"},
					false),
				_SQL_SELECT_OBJECTENTRY_WHERE, _SQL_COUNT_OBJECTENTRY_WHERE,
				ObjectEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"objectEntry.objectEntryId = objectEntry.headObjectEntryId",
				"objectEntry.objectEntryId = objectEntry.headObjectEntryId",
				null,
				new FinderColumn<>(
					"objectEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, ObjectEntry::getGroupId),
				new FinderColumn<>(
					"objectEntry.", "objectDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectEntry::getObjectDefinitionId),
				new FinderColumn<>(
					"objectEntry.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, ObjectEntry::getStatus));

		_collectionPersistenceFinderByG_ODI_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_ODI_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "objectDefinitionId", "status"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_ODI_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "objectDefinitionId", "status"},
					false),
				_SQL_SELECT_OBJECTENTRY_WHERE, _SQL_COUNT_OBJECTENTRY_WHERE,
				ObjectEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"objectEntry.objectEntryId = objectEntry.headObjectEntryId",
				"objectEntry.objectEntryId = objectEntry.headObjectEntryId",
				null,
				new FinderColumn<>(
					"objectEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, ObjectEntry::getGroupId),
				new FinderColumn<>(
					"objectEntry.", "objectDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectEntry::getObjectDefinitionId),
				new FinderColumn<>(
					"objectEntry.", "status", FinderColumn.Type.INTEGER, "!=",
					true, true, ObjectEntry::getStatus));

		_collectionPersistenceFinderByU_GtCD_ODI =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_GtCD_ODI",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId", "createDate", "objectDefinitionId"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByU_GtCD_ODI",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Long.class.getName()
					},
					new String[] {"userId", "createDate", "objectDefinitionId"},
					false),
				_SQL_SELECT_OBJECTENTRY_WHERE, _SQL_COUNT_OBJECTENTRY_WHERE,
				ObjectEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"objectEntry.objectEntryId = objectEntry.headObjectEntryId",
				"objectEntry.objectEntryId = objectEntry.headObjectEntryId",
				null,
				new FinderColumn<>(
					"objectEntry.", "userId", FinderColumn.Type.LONG, "=", true,
					true, ObjectEntry::getUserId),
				new FinderColumn<>(
					"objectEntry.", "createDate", FinderColumn.Type.DATE, ">",
					true, true, ObjectEntry::getCreateDate),
				new FinderColumn<>(
					"objectEntry.", "objectDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectEntry::getObjectDefinitionId));

		_uniquePersistenceFinderByERC_G_C_ODI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G_C_ODI",
				new String[] {
					String.class.getName(), Long.class.getName(),
					Long.class.getName(), Long.class.getName()
				},
				new String[] {
					"externalReferenceCode", "groupId", "companyId",
					"objectDefinitionId"
				},
				0, 1, false,
				convertNullFunction(ObjectEntry::getExternalReferenceCode),
				ObjectEntry::getGroupId, ObjectEntry::getCompanyId,
				ObjectEntry::getObjectDefinitionId),
			_SQL_SELECT_OBJECTENTRY_WHERE, "",
			new FinderColumn<>(
				"objectEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"objectEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, ObjectEntry::getGroupId),
			new FinderColumn<>(
				"objectEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, ObjectEntry::getCompanyId),
			new FinderColumn<>(
				"objectEntry.", "objectDefinitionId", FinderColumn.Type.LONG,
				"=", true, true, ObjectEntry::getObjectDefinitionId));

		ObjectEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectEntryUtil.setPersistence(null);

		entityCache.removeCache(ObjectEntryImpl.class.getName());
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
		ObjectEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTENTRY =
		"SELECT objectEntry FROM ObjectEntry objectEntry";

	private static final String _SQL_SELECT_OBJECTENTRY_WHERE =
		"SELECT objectEntry FROM ObjectEntry objectEntry WHERE ";

	private static final String _SQL_COUNT_OBJECTENTRY_WHERE =
		"SELECT COUNT(objectEntry) FROM ObjectEntry objectEntry WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1342389250