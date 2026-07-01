/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectEntryFolderException;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectEntryFolderTable;
import com.liferay.object.model.impl.ObjectEntryFolderImpl;
import com.liferay.object.model.impl.ObjectEntryFolderModelImpl;
import com.liferay.object.service.persistence.ObjectEntryFolderPersistence;
import com.liferay.object.service.persistence.ObjectEntryFolderUtil;
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
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
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
 * The persistence implementation for the object entry folder service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectEntryFolderPersistence.class)
public class ObjectEntryFolderPersistenceImpl
	extends BasePersistenceImpl
		<ObjectEntryFolder, NoSuchObjectEntryFolderException>
	implements ObjectEntryFolderPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectEntryFolderUtil</code> to access the object entry folder persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectEntryFolderImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ObjectEntryFolder, NoSuchObjectEntryFolderException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object entry folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object entry folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder findByUuid_First(
			String uuid, OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws NoSuchObjectEntryFolderException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object entry folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByUuid_First(
		String uuid, OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object entry folders where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object entry folders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object entry folders
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<ObjectEntryFolder, NoSuchObjectEntryFolderException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the object entry folder where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchObjectEntryFolderException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder findByUUID_G(String uuid, long groupId)
		throws NoSuchObjectEntryFolderException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the object entry folder where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the object entry folder where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the object entry folder that was removed
	 */
	@Override
	public ObjectEntryFolder removeByUUID_G(String uuid, long groupId)
		throws NoSuchObjectEntryFolderException {

		ObjectEntryFolder objectEntryFolder = findByUUID_G(uuid, groupId);

		return remove(objectEntryFolder);
	}

	/**
	 * Returns the number of object entry folders where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching object entry folders
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<ObjectEntryFolder, NoSuchObjectEntryFolderException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object entry folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws NoSuchObjectEntryFolderException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object entry folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object entry folders where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object entry folders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object entry folders
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private UniquePersistenceFinder
		<ObjectEntryFolder, NoSuchObjectEntryFolderException>
			_uniquePersistenceFinderByERC_G_C;

	/**
	 * Returns the object entry folder where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; or throws a <code>NoSuchObjectEntryFolderException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @return the matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder findByERC_G_C(
			String externalReferenceCode, long groupId, long companyId)
		throws NoSuchObjectEntryFolderException {

		return _uniquePersistenceFinderByERC_G_C.find(
			finderCache,
			new Object[] {externalReferenceCode, groupId, companyId});
	}

	/**
	 * Returns the object entry folder where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByERC_G_C(
		String externalReferenceCode, long groupId, long companyId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G_C.fetch(
			finderCache,
			new Object[] {externalReferenceCode, groupId, companyId},
			useFinderCache);
	}

	/**
	 * Removes the object entry folder where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @return the object entry folder that was removed
	 */
	@Override
	public ObjectEntryFolder removeByERC_G_C(
			String externalReferenceCode, long groupId, long companyId)
		throws NoSuchObjectEntryFolderException {

		ObjectEntryFolder objectEntryFolder = findByERC_G_C(
			externalReferenceCode, groupId, companyId);

		return remove(objectEntryFolder);
	}

	/**
	 * Returns the number of object entry folders where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @return the number of matching object entry folders
	 */
	@Override
	public int countByERC_G_C(
		String externalReferenceCode, long groupId, long companyId) {

		return _uniquePersistenceFinderByERC_G_C.count(
			finderCache,
			new Object[] {externalReferenceCode, groupId, companyId});
	}

	private FilterCollectionPersistenceFinder
		<ObjectEntryFolder, NoSuchObjectEntryFolderException>
			_collectionPersistenceFinderByG_C_P;

	/**
	 * Returns an ordered range of all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId, int start,
		int end, OrderByComparator<ObjectEntryFolder> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_P.find(
			finderCache,
			new Object[] {groupId, companyId, parentObjectEntryFolderId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder findByG_C_P_First(
			long groupId, long companyId, long parentObjectEntryFolderId,
			OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws NoSuchObjectEntryFolderException {

		return _collectionPersistenceFinderByG_C_P.findFirst(
			finderCache,
			new Object[] {groupId, companyId, parentObjectEntryFolderId},
			orderByComparator);
	}

	/**
	 * Returns the first object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByG_C_P_First(
		long groupId, long companyId, long parentObjectEntryFolderId,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_C_P.fetchFirst(
			finderCache,
			new Object[] {groupId, companyId, parentObjectEntryFolderId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object entry folders that the user has permissions to view where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry folders that the user has permission to view
	 */
	@Override
	public List<ObjectEntryFolder> filterFindByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId, int start,
		int end, OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_C_P.filterFind(
			finderCache,
			new Object[] {groupId, companyId, parentObjectEntryFolderId}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 */
	@Override
	public void removeByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId) {

		_collectionPersistenceFinderByG_C_P.remove(
			finderCache,
			new Object[] {groupId, companyId, parentObjectEntryFolderId});
	}

	/**
	 * Returns the number of object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @return the number of matching object entry folders
	 */
	@Override
	public int countByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId) {

		return _collectionPersistenceFinderByG_C_P.count(
			finderCache,
			new Object[] {groupId, companyId, parentObjectEntryFolderId});
	}

	/**
	 * Returns the number of object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @return the number of matching object entry folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId) {

		return _collectionPersistenceFinderByG_C_P.filterCount(
			finderCache,
			new Object[] {groupId, companyId, parentObjectEntryFolderId},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<ObjectEntryFolder, NoSuchObjectEntryFolderException>
			_collectionPersistenceFinderByG_C_LikeT;

	/**
	 * Returns all the object entry folders where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @return the matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByG_C_LikeT(
		long groupId, long companyId, String treePath) {

		return findByG_C_LikeT(
			groupId, companyId, treePath, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object entry folders where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @return the range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByG_C_LikeT(
		long groupId, long companyId, String treePath, int start, int end) {

		return findByG_C_LikeT(groupId, companyId, treePath, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entry folders where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByG_C_LikeT(
		long groupId, long companyId, String treePath, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return findByG_C_LikeT(
			groupId, companyId, treePath, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entry folders where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByG_C_LikeT(
		long groupId, long companyId, String treePath, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_LikeT.find(
			finderCache, new Object[] {groupId, companyId, treePath}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder findByG_C_LikeT_First(
			long groupId, long companyId, String treePath,
			OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws NoSuchObjectEntryFolderException {

		return _collectionPersistenceFinderByG_C_LikeT.findFirst(
			finderCache, new Object[] {groupId, companyId, treePath},
			orderByComparator);
	}

	/**
	 * Returns the first object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByG_C_LikeT_First(
		long groupId, long companyId, String treePath,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_C_LikeT.fetchFirst(
			finderCache, new Object[] {groupId, companyId, treePath},
			orderByComparator);
	}

	/**
	 * Returns all the object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @return the matching object entry folders that the user has permission to view
	 */
	@Override
	public List<ObjectEntryFolder> filterFindByG_C_LikeT(
		long groupId, long companyId, String treePath) {

		return filterFindByG_C_LikeT(
			groupId, companyId, treePath, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @return the range of matching object entry folders that the user has permission to view
	 */
	@Override
	public List<ObjectEntryFolder> filterFindByG_C_LikeT(
		long groupId, long companyId, String treePath, int start, int end) {

		return filterFindByG_C_LikeT(
			groupId, companyId, treePath, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entry folders that the user has permissions to view where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry folders that the user has permission to view
	 */
	@Override
	public List<ObjectEntryFolder> filterFindByG_C_LikeT(
		long groupId, long companyId, String treePath, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_C_LikeT.filterFind(
			finderCache, new Object[] {groupId, companyId, treePath}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the object entry folders where groupId = &#63; and companyId = &#63; and treePath LIKE &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 */
	@Override
	public void removeByG_C_LikeT(
		long groupId, long companyId, String treePath) {

		_collectionPersistenceFinderByG_C_LikeT.remove(
			finderCache, new Object[] {groupId, companyId, treePath});
	}

	/**
	 * Returns the number of object entry folders where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @return the number of matching object entry folders
	 */
	@Override
	public int countByG_C_LikeT(long groupId, long companyId, String treePath) {
		return _collectionPersistenceFinderByG_C_LikeT.count(
			finderCache, new Object[] {groupId, companyId, treePath});
	}

	/**
	 * Returns the number of object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @return the number of matching object entry folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_LikeT(
		long groupId, long companyId, String treePath) {

		return _collectionPersistenceFinderByG_C_LikeT.filterCount(
			finderCache, new Object[] {groupId, companyId, treePath}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<ObjectEntryFolder, NoSuchObjectEntryFolderException>
			_collectionPersistenceFinderByG_C_P_N_NotS;

	/**
	 * Returns all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @return the matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status) {

		return findByG_C_P_N_NotS(
			groupId, companyId, parentObjectEntryFolderId, name, status,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @return the range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status, int start, int end) {

		return findByG_C_P_N_NotS(
			groupId, companyId, parentObjectEntryFolderId, name, status, start,
			end, null);
	}

	/**
	 * Returns an ordered range of all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return findByG_C_P_N_NotS(
			groupId, companyId, parentObjectEntryFolderId, name, status, start,
			end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_P_N_NotS.find(
			finderCache,
			new Object[] {
				groupId, companyId, parentObjectEntryFolderId, name, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder findByG_C_P_N_NotS_First(
			long groupId, long companyId, long parentObjectEntryFolderId,
			String name, int status,
			OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws NoSuchObjectEntryFolderException {

		return _collectionPersistenceFinderByG_C_P_N_NotS.findFirst(
			finderCache,
			new Object[] {
				groupId, companyId, parentObjectEntryFolderId, name, status
			},
			orderByComparator);
	}

	/**
	 * Returns the first object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByG_C_P_N_NotS_First(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_C_P_N_NotS.fetchFirst(
			finderCache,
			new Object[] {
				groupId, companyId, parentObjectEntryFolderId, name, status
			},
			orderByComparator);
	}

	/**
	 * Returns all the object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @return the matching object entry folders that the user has permission to view
	 */
	@Override
	public List<ObjectEntryFolder> filterFindByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status) {

		return filterFindByG_C_P_N_NotS(
			groupId, companyId, parentObjectEntryFolderId, name, status,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @return the range of matching object entry folders that the user has permission to view
	 */
	@Override
	public List<ObjectEntryFolder> filterFindByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status, int start, int end) {

		return filterFindByG_C_P_N_NotS(
			groupId, companyId, parentObjectEntryFolderId, name, status, start,
			end, null);
	}

	/**
	 * Returns an ordered range of all the object entry folders that the user has permissions to view where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry folders that the user has permission to view
	 */
	@Override
	public List<ObjectEntryFolder> filterFindByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_C_P_N_NotS.filterFind(
			finderCache,
			new Object[] {
				groupId, companyId, parentObjectEntryFolderId, name, status
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 */
	@Override
	public void removeByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status) {

		_collectionPersistenceFinderByG_C_P_N_NotS.remove(
			finderCache,
			new Object[] {
				groupId, companyId, parentObjectEntryFolderId, name, status
			});
	}

	/**
	 * Returns the number of object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @return the number of matching object entry folders
	 */
	@Override
	public int countByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status) {

		return _collectionPersistenceFinderByG_C_P_N_NotS.count(
			finderCache,
			new Object[] {
				groupId, companyId, parentObjectEntryFolderId, name, status
			});
	}

	/**
	 * Returns the number of object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @return the number of matching object entry folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status) {

		return _collectionPersistenceFinderByG_C_P_N_NotS.filterCount(
			finderCache,
			new Object[] {
				groupId, companyId, parentObjectEntryFolderId, name, status
			},
			groupId);
	}

	public ObjectEntryFolderPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectEntryFolder.class);

		setModelImplClass(ObjectEntryFolderImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectEntryFolderTable.INSTANCE);
	}

	/**
	 * Creates a new object entry folder with the primary key. Does not add the object entry folder to the database.
	 *
	 * @param objectEntryFolderId the primary key for the new object entry folder
	 * @return the new object entry folder
	 */
	@Override
	public ObjectEntryFolder create(long objectEntryFolderId) {
		ObjectEntryFolder objectEntryFolder = new ObjectEntryFolderImpl();

		objectEntryFolder.setNew(true);
		objectEntryFolder.setPrimaryKey(objectEntryFolderId);

		String uuid = PortalUUIDUtil.generate();

		objectEntryFolder.setUuid(uuid);

		objectEntryFolder.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectEntryFolder;
	}

	/**
	 * Removes the object entry folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectEntryFolderId the primary key of the object entry folder
	 * @return the object entry folder that was removed
	 * @throws NoSuchObjectEntryFolderException if a object entry folder with the primary key could not be found
	 */
	@Override
	public ObjectEntryFolder remove(long objectEntryFolderId)
		throws NoSuchObjectEntryFolderException {

		return remove((Serializable)objectEntryFolderId);
	}

	@Override
	protected ObjectEntryFolder removeImpl(
		ObjectEntryFolder objectEntryFolder) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectEntryFolder)) {
				objectEntryFolder = (ObjectEntryFolder)session.get(
					ObjectEntryFolderImpl.class,
					objectEntryFolder.getPrimaryKeyObj());
			}

			if (objectEntryFolder != null) {
				session.delete(objectEntryFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectEntryFolder != null) {
			clearCache(objectEntryFolder);
		}

		return objectEntryFolder;
	}

	@Override
	public ObjectEntryFolder updateImpl(ObjectEntryFolder objectEntryFolder) {
		boolean isNew = objectEntryFolder.isNew();

		if (!(objectEntryFolder instanceof ObjectEntryFolderModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectEntryFolder.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectEntryFolder);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectEntryFolder proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectEntryFolder implementation " +
					objectEntryFolder.getClass());
		}

		ObjectEntryFolderModelImpl objectEntryFolderModelImpl =
			(ObjectEntryFolderModelImpl)objectEntryFolder;

		if (Validator.isNull(objectEntryFolder.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectEntryFolder.setUuid(uuid);
		}

		if (Validator.isNull(objectEntryFolder.getExternalReferenceCode())) {
			objectEntryFolder.setExternalReferenceCode(
				objectEntryFolder.getUuid());
		}
		else {
			if (!Objects.equals(
					objectEntryFolderModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					objectEntryFolder.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = objectEntryFolder.getCompanyId();

					long groupId = objectEntryFolder.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = objectEntryFolder.getPrimaryKey();
					}

					try {
						objectEntryFolder.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ObjectEntryFolder.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								objectEntryFolder.getExternalReferenceCode(),
								null));
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

		if (isNew && (objectEntryFolder.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectEntryFolder.setCreateDate(date);
			}
			else {
				objectEntryFolder.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectEntryFolderModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectEntryFolder.setModifiedDate(date);
			}
			else {
				objectEntryFolder.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectEntryFolder);
			}
			else {
				objectEntryFolder = (ObjectEntryFolder)session.merge(
					objectEntryFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectEntryFolder, false);

		if (isNew) {
			objectEntryFolder.setNew(false);
		}

		objectEntryFolder.resetOriginalValues();

		return objectEntryFolder;
	}

	/**
	 * Returns the object entry folder with the primary key or throws a <code>NoSuchObjectEntryFolderException</code> if it could not be found.
	 *
	 * @param objectEntryFolderId the primary key of the object entry folder
	 * @return the object entry folder
	 * @throws NoSuchObjectEntryFolderException if a object entry folder with the primary key could not be found
	 */
	@Override
	public ObjectEntryFolder findByPrimaryKey(long objectEntryFolderId)
		throws NoSuchObjectEntryFolderException {

		return findByPrimaryKey((Serializable)objectEntryFolderId);
	}

	/**
	 * Returns the object entry folder with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectEntryFolderId the primary key of the object entry folder
	 * @return the object entry folder, or <code>null</code> if a object entry folder with the primary key could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByPrimaryKey(long objectEntryFolderId) {
		return fetchByPrimaryKey((Serializable)objectEntryFolderId);
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
		return "objectEntryFolderId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTENTRYFOLDER;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectEntryFolderModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object entry folder persistence.
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
			_SQL_SELECT_OBJECTENTRYFOLDER_WHERE,
			_SQL_COUNT_OBJECTENTRYFOLDER_WHERE,
			ObjectEntryFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"objectEntryFolder.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, ObjectEntryFolder::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(ObjectEntryFolder::getUuid),
				ObjectEntryFolder::getGroupId),
			_SQL_SELECT_OBJECTENTRYFOLDER_WHERE, "",
			new FinderColumn<>(
				"objectEntryFolder.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, ObjectEntryFolder::getUuid),
			new FinderColumn<>(
				"objectEntryFolder.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, ObjectEntryFolder::getGroupId));

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
				_SQL_SELECT_OBJECTENTRYFOLDER_WHERE,
				_SQL_COUNT_OBJECTENTRYFOLDER_WHERE,
				ObjectEntryFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"objectEntryFolder.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectEntryFolder::getUuid),
				new FinderColumn<>(
					"objectEntryFolder.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectEntryFolder::getCompanyId));

		_uniquePersistenceFinderByERC_G_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G_C",
				new String[] {
					String.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"externalReferenceCode", "groupId", "companyId"},
				0, 1, false,
				convertNullFunction(
					ObjectEntryFolder::getExternalReferenceCode),
				ObjectEntryFolder::getGroupId, ObjectEntryFolder::getCompanyId),
			_SQL_SELECT_OBJECTENTRYFOLDER_WHERE, "",
			new FinderColumn<>(
				"objectEntryFolder.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectEntryFolder::getExternalReferenceCode),
			new FinderColumn<>(
				"objectEntryFolder.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, ObjectEntryFolder::getGroupId),
			new FinderColumn<>(
				"objectEntryFolder.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, ObjectEntryFolder::getCompanyId));

		_collectionPersistenceFinderByG_C_P =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "companyId", "parentObjectEntryFolderId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"groupId", "companyId", "parentObjectEntryFolderId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"groupId", "companyId", "parentObjectEntryFolderId"
					},
					false),
				_SQL_SELECT_OBJECTENTRYFOLDER_WHERE,
				_SQL_COUNT_OBJECTENTRYFOLDER_WHERE,
				ObjectEntryFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"objectEntryFolder.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, ObjectEntryFolder::getGroupId),
				new FinderColumn<>(
					"objectEntryFolder.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectEntryFolder::getCompanyId),
				new FinderColumn<>(
					"objectEntryFolder.", "parentObjectEntryFolderId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectEntryFolder::getParentObjectEntryFolderId));

		_collectionPersistenceFinderByG_C_LikeT =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_LikeT",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "companyId", "treePath"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_C_LikeT",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName()
					},
					new String[] {"groupId", "companyId", "treePath"}, false),
				_SQL_SELECT_OBJECTENTRYFOLDER_WHERE,
				_SQL_COUNT_OBJECTENTRYFOLDER_WHERE,
				ObjectEntryFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"objectEntryFolder.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, ObjectEntryFolder::getGroupId),
				new FinderColumn<>(
					"objectEntryFolder.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectEntryFolder::getCompanyId),
				new FinderColumn<>(
					"objectEntryFolder.", "treePath", FinderColumn.Type.STRING,
					"LIKE", true, true, ObjectEntryFolder::getTreePath));

		_collectionPersistenceFinderByG_C_P_N_NotS =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_C_P_N_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "companyId", "parentObjectEntryFolderId",
						"name", "status"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_C_P_N_NotS",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"groupId", "companyId", "parentObjectEntryFolderId",
						"name", "status"
					},
					false),
				_SQL_SELECT_OBJECTENTRYFOLDER_WHERE,
				_SQL_COUNT_OBJECTENTRYFOLDER_WHERE,
				ObjectEntryFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"objectEntryFolder.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, ObjectEntryFolder::getGroupId),
				new FinderColumn<>(
					"objectEntryFolder.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectEntryFolder::getCompanyId),
				new FinderColumn<>(
					"objectEntryFolder.", "parentObjectEntryFolderId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectEntryFolder::getParentObjectEntryFolderId),
				new FinderColumn<>(
					"objectEntryFolder.", "name", FinderColumn.Type.STRING, "=",
					true, true, ObjectEntryFolder::getName),
				new FinderColumn<>(
					"objectEntryFolder.", "status", FinderColumn.Type.INTEGER,
					"!=", true, true, ObjectEntryFolder::getStatus));

		ObjectEntryFolderUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectEntryFolderUtil.setPersistence(null);

		entityCache.removeCache(ObjectEntryFolderImpl.class.getName());
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
		ObjectEntryFolderModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTENTRYFOLDER =
		"SELECT objectEntryFolder FROM ObjectEntryFolder objectEntryFolder";

	private static final String _SQL_SELECT_OBJECTENTRYFOLDER_WHERE =
		"SELECT objectEntryFolder FROM ObjectEntryFolder objectEntryFolder WHERE ";

	private static final String _SQL_COUNT_OBJECTENTRYFOLDER_WHERE =
		"SELECT COUNT(objectEntryFolder) FROM ObjectEntryFolder objectEntryFolder WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-2127971482