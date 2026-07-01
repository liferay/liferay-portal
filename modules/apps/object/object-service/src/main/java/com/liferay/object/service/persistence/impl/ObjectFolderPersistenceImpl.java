/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.DuplicateObjectFolderExternalReferenceCodeException;
import com.liferay.object.exception.NoSuchObjectFolderException;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectFolderTable;
import com.liferay.object.model.impl.ObjectFolderImpl;
import com.liferay.object.model.impl.ObjectFolderModelImpl;
import com.liferay.object.service.persistence.ObjectFolderPersistence;
import com.liferay.object.service.persistence.ObjectFolderUtil;
import com.liferay.object.service.persistence.impl.constants.ObjectPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
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
 * The persistence implementation for the object folder service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectFolderPersistence.class)
public class ObjectFolderPersistenceImpl
	extends BasePersistenceImpl<ObjectFolder, NoSuchObjectFolderException>
	implements ObjectFolderPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectFolderUtil</code> to access the object folder persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectFolderImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<ObjectFolder, NoSuchObjectFolderException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object folders
	 * @param end the upper bound of the range of object folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object folders
	 */
	@Override
	public List<ObjectFolder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectFolder> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object folder
	 * @throws NoSuchObjectFolderException if a matching object folder could not be found
	 */
	@Override
	public ObjectFolder findByUuid_First(
			String uuid, OrderByComparator<ObjectFolder> orderByComparator)
		throws NoSuchObjectFolderException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object folder, or <code>null</code> if a matching object folder could not be found
	 */
	@Override
	public ObjectFolder fetchByUuid_First(
		String uuid, OrderByComparator<ObjectFolder> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object folders that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object folders
	 * @param end the upper bound of the range of object folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object folders that the user has permission to view
	 */
	@Override
	public List<ObjectFolder> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectFolder> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			finderCache, new Object[] {uuid}, start, end, orderByComparator);
	}

	/**
	 * Removes all the object folders where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object folders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object folders
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object folders that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object folders that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			finderCache, new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder
		<ObjectFolder, NoSuchObjectFolderException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object folders
	 * @param end the upper bound of the range of object folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object folders
	 */
	@Override
	public List<ObjectFolder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectFolder> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object folder
	 * @throws NoSuchObjectFolderException if a matching object folder could not be found
	 */
	@Override
	public ObjectFolder findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectFolder> orderByComparator)
		throws NoSuchObjectFolderException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object folder, or <code>null</code> if a matching object folder could not be found
	 */
	@Override
	public ObjectFolder fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectFolder> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object folders that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object folders
	 * @param end the upper bound of the range of object folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object folders that the user has permission to view
	 */
	@Override
	public List<ObjectFolder> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectFolder> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the object folders where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object folders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object folders
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of object folders that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object folders that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			finderCache, new Object[] {uuid, companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<ObjectFolder, NoSuchObjectFolderException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the object folders where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object folders
	 * @param end the upper bound of the range of object folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object folders
	 */
	@Override
	public List<ObjectFolder> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ObjectFolder> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object folder
	 * @throws NoSuchObjectFolderException if a matching object folder could not be found
	 */
	@Override
	public ObjectFolder findByCompanyId_First(
			long companyId, OrderByComparator<ObjectFolder> orderByComparator)
		throws NoSuchObjectFolderException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first object folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object folder, or <code>null</code> if a matching object folder could not be found
	 */
	@Override
	public ObjectFolder fetchByCompanyId_First(
		long companyId, OrderByComparator<ObjectFolder> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object folders that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object folders
	 * @param end the upper bound of the range of object folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object folders that the user has permission to view
	 */
	@Override
	public List<ObjectFolder> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ObjectFolder> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the object folders where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of object folders where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching object folders
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of object folders that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching object folders that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	private UniquePersistenceFinder<ObjectFolder, NoSuchObjectFolderException>
		_uniquePersistenceFinderByC_N;

	/**
	 * Returns the object folder where companyId = &#63; and name = &#63; or throws a <code>NoSuchObjectFolderException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching object folder
	 * @throws NoSuchObjectFolderException if a matching object folder could not be found
	 */
	@Override
	public ObjectFolder findByC_N(long companyId, String name)
		throws NoSuchObjectFolderException {

		return _uniquePersistenceFinderByC_N.find(
			finderCache, new Object[] {companyId, name});
	}

	/**
	 * Returns the object folder where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object folder, or <code>null</code> if a matching object folder could not be found
	 */
	@Override
	public ObjectFolder fetchByC_N(
		long companyId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_N.fetch(
			finderCache, new Object[] {companyId, name}, useFinderCache);
	}

	/**
	 * Removes the object folder where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the object folder that was removed
	 */
	@Override
	public ObjectFolder removeByC_N(long companyId, String name)
		throws NoSuchObjectFolderException {

		ObjectFolder objectFolder = findByC_N(companyId, name);

		return remove(objectFolder);
	}

	/**
	 * Returns the number of object folders where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching object folders
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		return _uniquePersistenceFinderByC_N.count(
			finderCache, new Object[] {companyId, name});
	}

	private UniquePersistenceFinder<ObjectFolder, NoSuchObjectFolderException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the object folder where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchObjectFolderException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching object folder
	 * @throws NoSuchObjectFolderException if a matching object folder could not be found
	 */
	@Override
	public ObjectFolder findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchObjectFolderException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the object folder where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object folder, or <code>null</code> if a matching object folder could not be found
	 */
	@Override
	public ObjectFolder fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the object folder where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the object folder that was removed
	 */
	@Override
	public ObjectFolder removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchObjectFolderException {

		ObjectFolder objectFolder = findByERC_C(
			externalReferenceCode, companyId);

		return remove(objectFolder);
	}

	/**
	 * Returns the number of object folders where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching object folders
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public ObjectFolderPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectFolder.class);

		setModelImplClass(ObjectFolderImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectFolderTable.INSTANCE);
	}

	/**
	 * Creates a new object folder with the primary key. Does not add the object folder to the database.
	 *
	 * @param objectFolderId the primary key for the new object folder
	 * @return the new object folder
	 */
	@Override
	public ObjectFolder create(long objectFolderId) {
		ObjectFolder objectFolder = new ObjectFolderImpl();

		objectFolder.setNew(true);
		objectFolder.setPrimaryKey(objectFolderId);

		String uuid = PortalUUIDUtil.generate();

		objectFolder.setUuid(uuid);

		objectFolder.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectFolder;
	}

	/**
	 * Removes the object folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectFolderId the primary key of the object folder
	 * @return the object folder that was removed
	 * @throws NoSuchObjectFolderException if a object folder with the primary key could not be found
	 */
	@Override
	public ObjectFolder remove(long objectFolderId)
		throws NoSuchObjectFolderException {

		return remove((Serializable)objectFolderId);
	}

	@Override
	protected ObjectFolder removeImpl(ObjectFolder objectFolder) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectFolder)) {
				objectFolder = (ObjectFolder)session.get(
					ObjectFolderImpl.class, objectFolder.getPrimaryKeyObj());
			}

			if (objectFolder != null) {
				session.delete(objectFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectFolder != null) {
			clearCache(objectFolder);
		}

		return objectFolder;
	}

	@Override
	public ObjectFolder updateImpl(ObjectFolder objectFolder) {
		boolean isNew = objectFolder.isNew();

		if (!(objectFolder instanceof ObjectFolderModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectFolder.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectFolder);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectFolder proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectFolder implementation " +
					objectFolder.getClass());
		}

		ObjectFolderModelImpl objectFolderModelImpl =
			(ObjectFolderModelImpl)objectFolder;

		if (Validator.isNull(objectFolder.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectFolder.setUuid(uuid);
		}

		if (Validator.isNull(objectFolder.getExternalReferenceCode())) {
			objectFolder.setExternalReferenceCode(objectFolder.getUuid());
		}
		else {
			if (!Objects.equals(
					objectFolderModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					objectFolder.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = objectFolder.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = objectFolder.getPrimaryKey();
					}

					try {
						objectFolder.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ObjectFolder.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								objectFolder.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			ObjectFolder ercObjectFolder = fetchByERC_C(
				objectFolder.getExternalReferenceCode(),
				objectFolder.getCompanyId());

			if (isNew) {
				if (ercObjectFolder != null) {
					throw new DuplicateObjectFolderExternalReferenceCodeException(
						"Duplicate object folder with external reference code " +
							objectFolder.getExternalReferenceCode() +
								" and company " + objectFolder.getCompanyId());
				}
			}
			else {
				if ((ercObjectFolder != null) &&
					(objectFolder.getObjectFolderId() !=
						ercObjectFolder.getObjectFolderId())) {

					throw new DuplicateObjectFolderExternalReferenceCodeException(
						"Duplicate object folder with external reference code " +
							objectFolder.getExternalReferenceCode() +
								" and company " + objectFolder.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectFolder.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectFolder.setCreateDate(date);
			}
			else {
				objectFolder.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!objectFolderModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectFolder.setModifiedDate(date);
			}
			else {
				objectFolder.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectFolder);
			}
			else {
				objectFolder = (ObjectFolder)session.merge(objectFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectFolder, false);

		if (isNew) {
			objectFolder.setNew(false);
		}

		objectFolder.resetOriginalValues();

		return objectFolder;
	}

	/**
	 * Returns the object folder with the primary key or throws a <code>NoSuchObjectFolderException</code> if it could not be found.
	 *
	 * @param objectFolderId the primary key of the object folder
	 * @return the object folder
	 * @throws NoSuchObjectFolderException if a object folder with the primary key could not be found
	 */
	@Override
	public ObjectFolder findByPrimaryKey(long objectFolderId)
		throws NoSuchObjectFolderException {

		return findByPrimaryKey((Serializable)objectFolderId);
	}

	/**
	 * Returns the object folder with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectFolderId the primary key of the object folder
	 * @return the object folder, or <code>null</code> if a object folder with the primary key could not be found
	 */
	@Override
	public ObjectFolder fetchByPrimaryKey(long objectFolderId) {
		return fetchByPrimaryKey((Serializable)objectFolderId);
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
		return "objectFolderId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTFOLDER;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectFolderModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object folder persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
					new String[] {String.class.getName()},
					new String[] {"uuid_"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
					new String[] {String.class.getName()},
					new String[] {"uuid_"}, 0, 1, false, null),
				_SQL_SELECT_OBJECTFOLDER_WHERE, _SQL_COUNT_OBJECTFOLDER_WHERE,
				ObjectFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectFolder.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, ObjectFolder::getUuid));

		_collectionPersistenceFinderByUuid_C =
			new FilterCollectionPersistenceFinder<>(
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
				_SQL_SELECT_OBJECTFOLDER_WHERE, _SQL_COUNT_OBJECTFOLDER_WHERE,
				ObjectFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectFolder.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, ObjectFolder::getUuid),
				new FinderColumn<>(
					"objectFolder.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, ObjectFolder::getCompanyId));

		_collectionPersistenceFinderByCompanyId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, false),
				_SQL_SELECT_OBJECTFOLDER_WHERE, _SQL_COUNT_OBJECTFOLDER_WHERE,
				ObjectFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectFolder.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, ObjectFolder::getCompanyId));

		_uniquePersistenceFinderByC_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "name"}, 0, 2, false,
				ObjectFolder::getCompanyId,
				convertNullFunction(ObjectFolder::getName)),
			_SQL_SELECT_OBJECTFOLDER_WHERE, "",
			new FinderColumn<>(
				"objectFolder.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, ObjectFolder::getCompanyId),
			new FinderColumn<>(
				"objectFolder.", "name", FinderColumn.Type.STRING, "=", true,
				true, ObjectFolder::getName));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(ObjectFolder::getExternalReferenceCode),
				ObjectFolder::getCompanyId),
			_SQL_SELECT_OBJECTFOLDER_WHERE, "",
			new FinderColumn<>(
				"objectFolder.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectFolder::getExternalReferenceCode),
			new FinderColumn<>(
				"objectFolder.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, ObjectFolder::getCompanyId));

		ObjectFolderUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectFolderUtil.setPersistence(null);

		entityCache.removeCache(ObjectFolderImpl.class.getName());
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
		ObjectFolderModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTFOLDER =
		"SELECT objectFolder FROM ObjectFolder objectFolder";

	private static final String _SQL_SELECT_OBJECTFOLDER_WHERE =
		"SELECT objectFolder FROM ObjectFolder objectFolder WHERE ";

	private static final String _SQL_COUNT_OBJECTFOLDER_WHERE =
		"SELECT COUNT(objectFolder) FROM ObjectFolder objectFolder WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2041761602