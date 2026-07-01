/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.persistence.impl;

import com.liferay.knowledge.base.exception.DuplicateKBFolderExternalReferenceCodeException;
import com.liferay.knowledge.base.exception.NoSuchFolderException;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.model.KBFolderTable;
import com.liferay.knowledge.base.model.impl.KBFolderImpl;
import com.liferay.knowledge.base.model.impl.KBFolderModelImpl;
import com.liferay.knowledge.base.service.persistence.KBFolderPersistence;
import com.liferay.knowledge.base.service.persistence.KBFolderUtil;
import com.liferay.knowledge.base.service.persistence.impl.constants.KBPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
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
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
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
 * The persistence implementation for the kb folder service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KBFolderPersistence.class)
public class KBFolderPersistenceImpl
	extends BasePersistenceImpl<KBFolder, NoSuchFolderException>
	implements KBFolderPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KBFolderUtil</code> to access the kb folder persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KBFolderImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<KBFolder, NoSuchFolderException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the kb folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<KBFolder> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByUuid_First(
			String uuid, OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first kb folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByUuid_First(
		String uuid, OrderByComparator<KBFolder> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the kb folders where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of kb folders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching kb folders
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<KBFolder, NoSuchFolderException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the kb folder where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByUUID_G(String uuid, long groupId)
		throws NoSuchFolderException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the kb folder where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the kb folder where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the kb folder that was removed
	 */
	@Override
	public KBFolder removeByUUID_G(String uuid, long groupId)
		throws NoSuchFolderException {

		KBFolder kbFolder = findByUUID_G(uuid, groupId);

		return remove(kbFolder);
	}

	/**
	 * Returns the number of kb folders where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching kb folders
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<KBFolder, NoSuchFolderException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the kb folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KBFolder> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first kb folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<KBFolder> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the kb folders where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of kb folders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching kb folders
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<KBFolder, NoSuchFolderException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the kb folders where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KBFolder> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByCompanyId_First(
			long companyId, OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first kb folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByCompanyId_First(
		long companyId, OrderByComparator<KBFolder> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the kb folders where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of kb folders where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kb folders
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private FilterCollectionPersistenceFinder<KBFolder, NoSuchFolderException>
		_collectionPersistenceFinderByG_P;

	/**
	 * Returns an ordered range of all the kb folders where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByG_P(
		long groupId, long parentKBFolderId, int start, int end,
		OrderByComparator<KBFolder> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P.find(
			finderCache, new Object[] {groupId, parentKBFolderId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb folder in the ordered set where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByG_P_First(
			long groupId, long parentKBFolderId,
			OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByG_P.findFirst(
			finderCache, new Object[] {groupId, parentKBFolderId},
			orderByComparator);
	}

	/**
	 * Returns the first kb folder in the ordered set where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByG_P_First(
		long groupId, long parentKBFolderId,
		OrderByComparator<KBFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_P.fetchFirst(
			finderCache, new Object[] {groupId, parentKBFolderId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb folders that the user has permissions to view where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb folders that the user has permission to view
	 */
	@Override
	public List<KBFolder> filterFindByG_P(
		long groupId, long parentKBFolderId, int start, int end,
		OrderByComparator<KBFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_P.filterFind(
			finderCache, new Object[] {groupId, parentKBFolderId}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the kb folders where groupId = &#63; and parentKBFolderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 */
	@Override
	public void removeByG_P(long groupId, long parentKBFolderId) {
		_collectionPersistenceFinderByG_P.remove(
			finderCache, new Object[] {groupId, parentKBFolderId});
	}

	/**
	 * Returns the number of kb folders where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @return the number of matching kb folders
	 */
	@Override
	public int countByG_P(long groupId, long parentKBFolderId) {
		return _collectionPersistenceFinderByG_P.count(
			finderCache, new Object[] {groupId, parentKBFolderId});
	}

	/**
	 * Returns the number of kb folders that the user has permission to view where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @return the number of matching kb folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(long groupId, long parentKBFolderId) {
		return _collectionPersistenceFinderByG_P.filterCount(
			finderCache, new Object[] {groupId, parentKBFolderId}, groupId);
	}

	private UniquePersistenceFinder<KBFolder, NoSuchFolderException>
		_uniquePersistenceFinderByG_P_N;

	/**
	 * Returns the kb folder where groupId = &#63; and parentKBFolderId = &#63; and name = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param name the name
	 * @return the matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByG_P_N(
			long groupId, long parentKBFolderId, String name)
		throws NoSuchFolderException {

		return _uniquePersistenceFinderByG_P_N.find(
			finderCache, new Object[] {groupId, parentKBFolderId, name});
	}

	/**
	 * Returns the kb folder where groupId = &#63; and parentKBFolderId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByG_P_N(
		long groupId, long parentKBFolderId, String name,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_P_N.fetch(
			finderCache, new Object[] {groupId, parentKBFolderId, name},
			useFinderCache);
	}

	/**
	 * Removes the kb folder where groupId = &#63; and parentKBFolderId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param name the name
	 * @return the kb folder that was removed
	 */
	@Override
	public KBFolder removeByG_P_N(
			long groupId, long parentKBFolderId, String name)
		throws NoSuchFolderException {

		KBFolder kbFolder = findByG_P_N(groupId, parentKBFolderId, name);

		return remove(kbFolder);
	}

	/**
	 * Returns the number of kb folders where groupId = &#63; and parentKBFolderId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param name the name
	 * @return the number of matching kb folders
	 */
	@Override
	public int countByG_P_N(long groupId, long parentKBFolderId, String name) {
		return _uniquePersistenceFinderByG_P_N.count(
			finderCache, new Object[] {groupId, parentKBFolderId, name});
	}

	private UniquePersistenceFinder<KBFolder, NoSuchFolderException>
		_uniquePersistenceFinderByG_P_UT;

	/**
	 * Returns the kb folder where groupId = &#63; and parentKBFolderId = &#63; and urlTitle = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param urlTitle the url title
	 * @return the matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByG_P_UT(
			long groupId, long parentKBFolderId, String urlTitle)
		throws NoSuchFolderException {

		return _uniquePersistenceFinderByG_P_UT.find(
			finderCache, new Object[] {groupId, parentKBFolderId, urlTitle});
	}

	/**
	 * Returns the kb folder where groupId = &#63; and parentKBFolderId = &#63; and urlTitle = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param urlTitle the url title
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByG_P_UT(
		long groupId, long parentKBFolderId, String urlTitle,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_P_UT.fetch(
			finderCache, new Object[] {groupId, parentKBFolderId, urlTitle},
			useFinderCache);
	}

	/**
	 * Removes the kb folder where groupId = &#63; and parentKBFolderId = &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param urlTitle the url title
	 * @return the kb folder that was removed
	 */
	@Override
	public KBFolder removeByG_P_UT(
			long groupId, long parentKBFolderId, String urlTitle)
		throws NoSuchFolderException {

		KBFolder kbFolder = findByG_P_UT(groupId, parentKBFolderId, urlTitle);

		return remove(kbFolder);
	}

	/**
	 * Returns the number of kb folders where groupId = &#63; and parentKBFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param urlTitle the url title
	 * @return the number of matching kb folders
	 */
	@Override
	public int countByG_P_UT(
		long groupId, long parentKBFolderId, String urlTitle) {

		return _uniquePersistenceFinderByG_P_UT.count(
			finderCache, new Object[] {groupId, parentKBFolderId, urlTitle});
	}

	private FilterCollectionPersistenceFinder<KBFolder, NoSuchFolderException>
		_collectionPersistenceFinderByG_P_S;

	/**
	 * Returns an ordered range of all the kb folders where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByG_P_S(
		long groupId, long parentKBFolderId, int status, int start, int end,
		OrderByComparator<KBFolder> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_S.find(
			finderCache, new Object[] {groupId, parentKBFolderId, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb folder in the ordered set where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByG_P_S_First(
			long groupId, long parentKBFolderId, int status,
			OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		return _collectionPersistenceFinderByG_P_S.findFirst(
			finderCache, new Object[] {groupId, parentKBFolderId, status},
			orderByComparator);
	}

	/**
	 * Returns the first kb folder in the ordered set where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByG_P_S_First(
		long groupId, long parentKBFolderId, int status,
		OrderByComparator<KBFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_P_S.fetchFirst(
			finderCache, new Object[] {groupId, parentKBFolderId, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb folders that the user has permissions to view where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb folders that the user has permission to view
	 */
	@Override
	public List<KBFolder> filterFindByG_P_S(
		long groupId, long parentKBFolderId, int status, int start, int end,
		OrderByComparator<KBFolder> orderByComparator) {

		return _collectionPersistenceFinderByG_P_S.filterFind(
			finderCache, new Object[] {groupId, parentKBFolderId, status},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the kb folders where groupId = &#63; and parentKBFolderId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 */
	@Override
	public void removeByG_P_S(long groupId, long parentKBFolderId, int status) {
		_collectionPersistenceFinderByG_P_S.remove(
			finderCache, new Object[] {groupId, parentKBFolderId, status});
	}

	/**
	 * Returns the number of kb folders where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @return the number of matching kb folders
	 */
	@Override
	public int countByG_P_S(long groupId, long parentKBFolderId, int status) {
		return _collectionPersistenceFinderByG_P_S.count(
			finderCache, new Object[] {groupId, parentKBFolderId, status});
	}

	/**
	 * Returns the number of kb folders that the user has permission to view where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @return the number of matching kb folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_S(
		long groupId, long parentKBFolderId, int status) {

		return _collectionPersistenceFinderByG_P_S.filterCount(
			finderCache, new Object[] {groupId, parentKBFolderId, status},
			groupId);
	}

	private UniquePersistenceFinder<KBFolder, NoSuchFolderException>
		_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the kb folder where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchFolderException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the kb folder where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the kb folder where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the kb folder that was removed
	 */
	@Override
	public KBFolder removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchFolderException {

		KBFolder kbFolder = findByERC_G(externalReferenceCode, groupId);

		return remove(kbFolder);
	}

	/**
	 * Returns the number of kb folders where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching kb folders
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public KBFolderPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(KBFolder.class);

		setModelImplClass(KBFolderImpl.class);
		setModelPKClass(long.class);

		setTable(KBFolderTable.INSTANCE);
	}

	/**
	 * Creates a new kb folder with the primary key. Does not add the kb folder to the database.
	 *
	 * @param kbFolderId the primary key for the new kb folder
	 * @return the new kb folder
	 */
	@Override
	public KBFolder create(long kbFolderId) {
		KBFolder kbFolder = new KBFolderImpl();

		kbFolder.setNew(true);
		kbFolder.setPrimaryKey(kbFolderId);

		String uuid = PortalUUIDUtil.generate();

		kbFolder.setUuid(uuid);

		kbFolder.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kbFolder;
	}

	/**
	 * Removes the kb folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kbFolderId the primary key of the kb folder
	 * @return the kb folder that was removed
	 * @throws NoSuchFolderException if a kb folder with the primary key could not be found
	 */
	@Override
	public KBFolder remove(long kbFolderId) throws NoSuchFolderException {
		return remove((Serializable)kbFolderId);
	}

	@Override
	protected KBFolder removeImpl(KBFolder kbFolder) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kbFolder)) {
				kbFolder = (KBFolder)session.get(
					KBFolderImpl.class, kbFolder.getPrimaryKeyObj());
			}

			if ((kbFolder != null) && ctPersistenceHelper.isRemove(kbFolder)) {
				session.delete(kbFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kbFolder != null) {
			clearCache(kbFolder);
		}

		return kbFolder;
	}

	@Override
	public KBFolder updateImpl(KBFolder kbFolder) {
		boolean isNew = kbFolder.isNew();

		if (!(kbFolder instanceof KBFolderModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kbFolder.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(kbFolder);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kbFolder proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KBFolder implementation " +
					kbFolder.getClass());
		}

		KBFolderModelImpl kbFolderModelImpl = (KBFolderModelImpl)kbFolder;

		if (Validator.isNull(kbFolder.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			kbFolder.setUuid(uuid);
		}

		if (Validator.isNull(kbFolder.getExternalReferenceCode())) {
			kbFolder.setExternalReferenceCode(kbFolder.getUuid());
		}
		else {
			if (!Objects.equals(
					kbFolderModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					kbFolder.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = kbFolder.getCompanyId();

					long groupId = kbFolder.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = kbFolder.getPrimaryKey();
					}

					try {
						kbFolder.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								KBFolder.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								kbFolder.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			KBFolder ercKBFolder = fetchByERC_G(
				kbFolder.getExternalReferenceCode(), kbFolder.getGroupId());

			if (isNew) {
				if (ercKBFolder != null) {
					throw new DuplicateKBFolderExternalReferenceCodeException(
						"Duplicate kb folder with external reference code " +
							kbFolder.getExternalReferenceCode() +
								" and group " + kbFolder.getGroupId());
				}
			}
			else {
				if ((ercKBFolder != null) &&
					(kbFolder.getKbFolderId() != ercKBFolder.getKbFolderId())) {

					throw new DuplicateKBFolderExternalReferenceCodeException(
						"Duplicate kb folder with external reference code " +
							kbFolder.getExternalReferenceCode() +
								" and group " + kbFolder.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kbFolder.getCreateDate() == null)) {
			if (serviceContext == null) {
				kbFolder.setCreateDate(date);
			}
			else {
				kbFolder.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!kbFolderModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kbFolder.setModifiedDate(date);
			}
			else {
				kbFolder.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kbFolder)) {
				if (!isNew) {
					session.evict(
						KBFolderImpl.class, kbFolder.getPrimaryKeyObj());
				}

				session.save(kbFolder);
			}
			else {
				kbFolder = (KBFolder)session.merge(kbFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kbFolder, false);

		if (isNew) {
			kbFolder.setNew(false);
		}

		kbFolder.resetOriginalValues();

		return kbFolder;
	}

	/**
	 * Returns the kb folder with the primary key or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param kbFolderId the primary key of the kb folder
	 * @return the kb folder
	 * @throws NoSuchFolderException if a kb folder with the primary key could not be found
	 */
	@Override
	public KBFolder findByPrimaryKey(long kbFolderId)
		throws NoSuchFolderException {

		return findByPrimaryKey((Serializable)kbFolderId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kb folder with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kbFolderId the primary key of the kb folder
	 * @return the kb folder, or <code>null</code> if a kb folder with the primary key could not be found
	 */
	@Override
	public KBFolder fetchByPrimaryKey(long kbFolderId) {
		return fetchByPrimaryKey((Serializable)kbFolderId);
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
		return "kbFolderId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KBFOLDER;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return KBFolderModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KBFolder";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("parentKBFolderId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("urlTitle");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("kbFolderId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the kb folder persistence.
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
			_SQL_SELECT_KBFOLDER_WHERE, _SQL_COUNT_KBFOLDER_WHERE,
			KBFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"kbFolder.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, KBFolder::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(KBFolder::getUuid), KBFolder::getGroupId),
			_SQL_SELECT_KBFOLDER_WHERE, "",
			new FinderColumn<>(
				"kbFolder.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, KBFolder::getUuid),
			new FinderColumn<>(
				"kbFolder.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				KBFolder::getGroupId));

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
				_SQL_SELECT_KBFOLDER_WHERE, _SQL_COUNT_KBFOLDER_WHERE,
				KBFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbFolder.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, KBFolder::getUuid),
				new FinderColumn<>(
					"kbFolder.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, KBFolder::getCompanyId));

		_collectionPersistenceFinderByCompanyId =
			new CollectionPersistenceFinder<>(
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
				_SQL_SELECT_KBFOLDER_WHERE, _SQL_COUNT_KBFOLDER_WHERE,
				KBFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbFolder.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, KBFolder::getCompanyId));

		_collectionPersistenceFinderByG_P =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "parentKBFolderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "parentKBFolderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "parentKBFolderId"}, false),
				_SQL_SELECT_KBFOLDER_WHERE, _SQL_COUNT_KBFOLDER_WHERE,
				KBFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbFolder.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBFolder::getGroupId),
				new FinderColumn<>(
					"kbFolder.", "parentKBFolderId", FinderColumn.Type.LONG,
					"=", true, true, KBFolder::getParentKBFolderId));

		_uniquePersistenceFinderByG_P_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_P_N",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "parentKBFolderId", "name"}, 0, 4,
				false, KBFolder::getGroupId, KBFolder::getParentKBFolderId,
				convertNullFunction(KBFolder::getName)),
			_SQL_SELECT_KBFOLDER_WHERE, "",
			new FinderColumn<>(
				"kbFolder.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				KBFolder::getGroupId),
			new FinderColumn<>(
				"kbFolder.", "parentKBFolderId", FinderColumn.Type.LONG, "=",
				true, true, KBFolder::getParentKBFolderId),
			new FinderColumn<>(
				"kbFolder.", "name", FinderColumn.Type.STRING, "=", true, true,
				KBFolder::getName));

		_uniquePersistenceFinderByG_P_UT = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_P_UT",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "parentKBFolderId", "urlTitle"}, 0, 4,
				false, KBFolder::getGroupId, KBFolder::getParentKBFolderId,
				convertNullFunction(KBFolder::getUrlTitle)),
			_SQL_SELECT_KBFOLDER_WHERE, "",
			new FinderColumn<>(
				"kbFolder.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				KBFolder::getGroupId),
			new FinderColumn<>(
				"kbFolder.", "parentKBFolderId", FinderColumn.Type.LONG, "=",
				true, true, KBFolder::getParentKBFolderId),
			new FinderColumn<>(
				"kbFolder.", "urlTitle", FinderColumn.Type.STRING, "=", true,
				true, KBFolder::getUrlTitle));

		_collectionPersistenceFinderByG_P_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "parentKBFolderId", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "parentKBFolderId", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "parentKBFolderId", "status"},
					false),
				_SQL_SELECT_KBFOLDER_WHERE, _SQL_COUNT_KBFOLDER_WHERE,
				KBFolderModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"kbFolder.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, KBFolder::getGroupId),
				new FinderColumn<>(
					"kbFolder.", "parentKBFolderId", FinderColumn.Type.LONG,
					"=", true, true, KBFolder::getParentKBFolderId),
				new FinderColumn<>(
					"kbFolder.", "status", FinderColumn.Type.INTEGER, "=", true,
					true, KBFolder::getStatus));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(KBFolder::getExternalReferenceCode),
				KBFolder::getGroupId),
			_SQL_SELECT_KBFOLDER_WHERE, "",
			new FinderColumn<>(
				"kbFolder.", "externalReferenceCode", FinderColumn.Type.STRING,
				"=", true, true, KBFolder::getExternalReferenceCode),
			new FinderColumn<>(
				"kbFolder.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				KBFolder::getGroupId));

		KBFolderUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KBFolderUtil.setPersistence(null);

		entityCache.removeCache(KBFolderImpl.class.getName());
	}

	@Override
	@Reference(
		target = KBPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = KBPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = KBPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		KBFolderModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KBFOLDER =
		"SELECT kbFolder FROM KBFolder kbFolder";

	private static final String _SQL_SELECT_KBFOLDER_WHERE =
		"SELECT kbFolder FROM KBFolder kbFolder WHERE ";

	private static final String _SQL_COUNT_KBFOLDER_WHERE =
		"SELECT COUNT(kbFolder) FROM KBFolder kbFolder WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1023895803