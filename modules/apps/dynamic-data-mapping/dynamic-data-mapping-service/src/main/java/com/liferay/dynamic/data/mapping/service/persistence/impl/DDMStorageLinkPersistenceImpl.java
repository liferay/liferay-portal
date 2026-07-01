/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.exception.NoSuchStorageLinkException;
import com.liferay.dynamic.data.mapping.model.DDMStorageLink;
import com.liferay.dynamic.data.mapping.model.DDMStorageLinkTable;
import com.liferay.dynamic.data.mapping.model.impl.DDMStorageLinkImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStorageLinkModelImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStorageLinkPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStorageLinkUtil;
import com.liferay.dynamic.data.mapping.service.persistence.impl.constants.DDMPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the ddm storage link service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDMStorageLinkPersistence.class)
public class DDMStorageLinkPersistenceImpl
	extends BasePersistenceImpl<DDMStorageLink, NoSuchStorageLinkException>
	implements DDMStorageLinkPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDMStorageLinkUtil</code> to access the ddm storage link persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDMStorageLinkImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DDMStorageLink, NoSuchStorageLinkException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the ddm storage links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStorageLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm storage links
	 * @param end the upper bound of the range of ddm storage links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm storage links
	 */
	@Override
	public List<DDMStorageLink> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DDMStorageLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm storage link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm storage link
	 * @throws NoSuchStorageLinkException if a matching ddm storage link could not be found
	 */
	@Override
	public DDMStorageLink findByUuid_First(
			String uuid, OrderByComparator<DDMStorageLink> orderByComparator)
		throws NoSuchStorageLinkException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first ddm storage link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm storage link, or <code>null</code> if a matching ddm storage link could not be found
	 */
	@Override
	public DDMStorageLink fetchByUuid_First(
		String uuid, OrderByComparator<DDMStorageLink> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the ddm storage links where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of ddm storage links where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching ddm storage links
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<DDMStorageLink, NoSuchStorageLinkException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the ddm storage links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStorageLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm storage links
	 * @param end the upper bound of the range of ddm storage links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm storage links
	 */
	@Override
	public List<DDMStorageLink> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMStorageLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm storage link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm storage link
	 * @throws NoSuchStorageLinkException if a matching ddm storage link could not be found
	 */
	@Override
	public DDMStorageLink findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DDMStorageLink> orderByComparator)
		throws NoSuchStorageLinkException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first ddm storage link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm storage link, or <code>null</code> if a matching ddm storage link could not be found
	 */
	@Override
	public DDMStorageLink fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DDMStorageLink> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the ddm storage links where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of ddm storage links where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching ddm storage links
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private UniquePersistenceFinder<DDMStorageLink, NoSuchStorageLinkException>
		_uniquePersistenceFinderByClassPK;

	/**
	 * Returns the ddm storage link where classPK = &#63; or throws a <code>NoSuchStorageLinkException</code> if it could not be found.
	 *
	 * @param classPK the class pk
	 * @return the matching ddm storage link
	 * @throws NoSuchStorageLinkException if a matching ddm storage link could not be found
	 */
	@Override
	public DDMStorageLink findByClassPK(long classPK)
		throws NoSuchStorageLinkException {

		return _uniquePersistenceFinderByClassPK.find(
			finderCache, new Object[] {classPK});
	}

	/**
	 * Returns the ddm storage link where classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm storage link, or <code>null</code> if a matching ddm storage link could not be found
	 */
	@Override
	public DDMStorageLink fetchByClassPK(long classPK, boolean useFinderCache) {
		return _uniquePersistenceFinderByClassPK.fetch(
			finderCache, new Object[] {classPK}, useFinderCache);
	}

	/**
	 * Removes the ddm storage link where classPK = &#63; from the database.
	 *
	 * @param classPK the class pk
	 * @return the ddm storage link that was removed
	 */
	@Override
	public DDMStorageLink removeByClassPK(long classPK)
		throws NoSuchStorageLinkException {

		DDMStorageLink ddmStorageLink = findByClassPK(classPK);

		return remove(ddmStorageLink);
	}

	/**
	 * Returns the number of ddm storage links where classPK = &#63;.
	 *
	 * @param classPK the class pk
	 * @return the number of matching ddm storage links
	 */
	@Override
	public int countByClassPK(long classPK) {
		return _uniquePersistenceFinderByClassPK.count(
			finderCache, new Object[] {classPK});
	}

	private CollectionPersistenceFinder
		<DDMStorageLink, NoSuchStorageLinkException>
			_collectionPersistenceFinderByStructureId;

	/**
	 * Returns an ordered range of all the ddm storage links where structureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStorageLinkModelImpl</code>.
	 * </p>
	 *
	 * @param structureId the structure ID
	 * @param start the lower bound of the range of ddm storage links
	 * @param end the upper bound of the range of ddm storage links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm storage links
	 */
	@Override
	public List<DDMStorageLink> findByStructureId(
		long structureId, int start, int end,
		OrderByComparator<DDMStorageLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByStructureId.find(
			finderCache, new Object[] {structureId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm storage link in the ordered set where structureId = &#63;.
	 *
	 * @param structureId the structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm storage link
	 * @throws NoSuchStorageLinkException if a matching ddm storage link could not be found
	 */
	@Override
	public DDMStorageLink findByStructureId_First(
			long structureId,
			OrderByComparator<DDMStorageLink> orderByComparator)
		throws NoSuchStorageLinkException {

		return _collectionPersistenceFinderByStructureId.findFirst(
			finderCache, new Object[] {structureId}, orderByComparator);
	}

	/**
	 * Returns the first ddm storage link in the ordered set where structureId = &#63;.
	 *
	 * @param structureId the structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm storage link, or <code>null</code> if a matching ddm storage link could not be found
	 */
	@Override
	public DDMStorageLink fetchByStructureId_First(
		long structureId, OrderByComparator<DDMStorageLink> orderByComparator) {

		return _collectionPersistenceFinderByStructureId.fetchFirst(
			finderCache, new Object[] {structureId}, orderByComparator);
	}

	/**
	 * Removes all the ddm storage links where structureId = &#63; from the database.
	 *
	 * @param structureId the structure ID
	 */
	@Override
	public void removeByStructureId(long structureId) {
		_collectionPersistenceFinderByStructureId.remove(
			finderCache, new Object[] {structureId});
	}

	/**
	 * Returns the number of ddm storage links where structureId = &#63;.
	 *
	 * @param structureId the structure ID
	 * @return the number of matching ddm storage links
	 */
	@Override
	public int countByStructureId(long structureId) {
		return _collectionPersistenceFinderByStructureId.count(
			finderCache, new Object[] {structureId});
	}

	private CollectionPersistenceFinder
		<DDMStorageLink, NoSuchStorageLinkException>
			_collectionPersistenceFinderByStructureVersionId;

	/**
	 * Returns an ordered range of all the ddm storage links where structureVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStorageLinkModelImpl</code>.
	 * </p>
	 *
	 * @param structureVersionId the structure version ID
	 * @param start the lower bound of the range of ddm storage links
	 * @param end the upper bound of the range of ddm storage links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm storage links
	 */
	@Override
	public List<DDMStorageLink> findByStructureVersionId(
		long structureVersionId, int start, int end,
		OrderByComparator<DDMStorageLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByStructureVersionId.find(
			finderCache, new Object[] {new long[] {structureVersionId}}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm storage link in the ordered set where structureVersionId = &#63;.
	 *
	 * @param structureVersionId the structure version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm storage link
	 * @throws NoSuchStorageLinkException if a matching ddm storage link could not be found
	 */
	@Override
	public DDMStorageLink findByStructureVersionId_First(
			long structureVersionId,
			OrderByComparator<DDMStorageLink> orderByComparator)
		throws NoSuchStorageLinkException {

		return _collectionPersistenceFinderByStructureVersionId.findFirst(
			finderCache, new Object[] {new long[] {structureVersionId}},
			orderByComparator);
	}

	/**
	 * Returns the first ddm storage link in the ordered set where structureVersionId = &#63;.
	 *
	 * @param structureVersionId the structure version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm storage link, or <code>null</code> if a matching ddm storage link could not be found
	 */
	@Override
	public DDMStorageLink fetchByStructureVersionId_First(
		long structureVersionId,
		OrderByComparator<DDMStorageLink> orderByComparator) {

		return _collectionPersistenceFinderByStructureVersionId.fetchFirst(
			finderCache, new Object[] {new long[] {structureVersionId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm storage links where structureVersionId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStorageLinkModelImpl</code>.
	 * </p>
	 *
	 * @param structureVersionIds the structure version IDs
	 * @param start the lower bound of the range of ddm storage links
	 * @param end the upper bound of the range of ddm storage links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm storage links
	 */
	@Override
	public List<DDMStorageLink> findByStructureVersionId(
		long[] structureVersionIds, int start, int end,
		OrderByComparator<DDMStorageLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByStructureVersionId.find(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(structureVersionIds)}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the ddm storage links where structureVersionId = &#63; from the database.
	 *
	 * @param structureVersionId the structure version ID
	 */
	@Override
	public void removeByStructureVersionId(long structureVersionId) {
		_collectionPersistenceFinderByStructureVersionId.remove(
			finderCache, new Object[] {new long[] {structureVersionId}});
	}

	/**
	 * Returns the number of ddm storage links where structureVersionId = &#63;.
	 *
	 * @param structureVersionId the structure version ID
	 * @return the number of matching ddm storage links
	 */
	@Override
	public int countByStructureVersionId(long structureVersionId) {
		return _collectionPersistenceFinderByStructureVersionId.count(
			finderCache, new Object[] {new long[] {structureVersionId}});
	}

	/**
	 * Returns the number of ddm storage links where structureVersionId = any &#63;.
	 *
	 * @param structureVersionIds the structure version IDs
	 * @return the number of matching ddm storage links
	 */
	@Override
	public int countByStructureVersionId(long[] structureVersionIds) {
		return _collectionPersistenceFinderByStructureVersionId.count(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(structureVersionIds)});
	}

	public DDMStorageLinkPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DDMStorageLink.class);

		setModelImplClass(DDMStorageLinkImpl.class);
		setModelPKClass(long.class);

		setTable(DDMStorageLinkTable.INSTANCE);
	}

	/**
	 * Creates a new ddm storage link with the primary key. Does not add the ddm storage link to the database.
	 *
	 * @param storageLinkId the primary key for the new ddm storage link
	 * @return the new ddm storage link
	 */
	@Override
	public DDMStorageLink create(long storageLinkId) {
		DDMStorageLink ddmStorageLink = new DDMStorageLinkImpl();

		ddmStorageLink.setNew(true);
		ddmStorageLink.setPrimaryKey(storageLinkId);

		String uuid = PortalUUIDUtil.generate();

		ddmStorageLink.setUuid(uuid);

		ddmStorageLink.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ddmStorageLink;
	}

	/**
	 * Removes the ddm storage link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param storageLinkId the primary key of the ddm storage link
	 * @return the ddm storage link that was removed
	 * @throws NoSuchStorageLinkException if a ddm storage link with the primary key could not be found
	 */
	@Override
	public DDMStorageLink remove(long storageLinkId)
		throws NoSuchStorageLinkException {

		return remove((Serializable)storageLinkId);
	}

	@Override
	protected DDMStorageLink removeImpl(DDMStorageLink ddmStorageLink) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddmStorageLink)) {
				ddmStorageLink = (DDMStorageLink)session.get(
					DDMStorageLinkImpl.class,
					ddmStorageLink.getPrimaryKeyObj());
			}

			if ((ddmStorageLink != null) &&
				ctPersistenceHelper.isRemove(ddmStorageLink)) {

				session.delete(ddmStorageLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmStorageLink != null) {
			clearCache(ddmStorageLink);
		}

		return ddmStorageLink;
	}

	@Override
	public DDMStorageLink updateImpl(DDMStorageLink ddmStorageLink) {
		boolean isNew = ddmStorageLink.isNew();

		if (!(ddmStorageLink instanceof DDMStorageLinkModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddmStorageLink.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ddmStorageLink);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddmStorageLink proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDMStorageLink implementation " +
					ddmStorageLink.getClass());
		}

		DDMStorageLinkModelImpl ddmStorageLinkModelImpl =
			(DDMStorageLinkModelImpl)ddmStorageLink;

		if (Validator.isNull(ddmStorageLink.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			ddmStorageLink.setUuid(uuid);
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddmStorageLink)) {
				if (!isNew) {
					session.evict(
						DDMStorageLinkImpl.class,
						ddmStorageLink.getPrimaryKeyObj());
				}

				session.save(ddmStorageLink);
			}
			else {
				ddmStorageLink = (DDMStorageLink)session.merge(ddmStorageLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ddmStorageLink, false);

		if (isNew) {
			ddmStorageLink.setNew(false);
		}

		ddmStorageLink.resetOriginalValues();

		return ddmStorageLink;
	}

	/**
	 * Returns the ddm storage link with the primary key or throws a <code>NoSuchStorageLinkException</code> if it could not be found.
	 *
	 * @param storageLinkId the primary key of the ddm storage link
	 * @return the ddm storage link
	 * @throws NoSuchStorageLinkException if a ddm storage link with the primary key could not be found
	 */
	@Override
	public DDMStorageLink findByPrimaryKey(long storageLinkId)
		throws NoSuchStorageLinkException {

		return findByPrimaryKey((Serializable)storageLinkId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the ddm storage link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param storageLinkId the primary key of the ddm storage link
	 * @return the ddm storage link, or <code>null</code> if a ddm storage link with the primary key could not be found
	 */
	@Override
	public DDMStorageLink fetchByPrimaryKey(long storageLinkId) {
		return fetchByPrimaryKey((Serializable)storageLinkId);
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
		return "storageLinkId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDMSTORAGELINK;
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
		return DDMStorageLinkModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDMStorageLink";
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
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("structureId");
		ctMergeColumnNames.add("structureVersionId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("storageLinkId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"classPK"});
	}

	/**
	 * Initializes the ddm storage link persistence.
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
			_SQL_SELECT_DDMSTORAGELINK_WHERE, _SQL_COUNT_DDMSTORAGELINK_WHERE,
			DDMStorageLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"ddmStorageLink.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, DDMStorageLink::getUuid));

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
				_SQL_SELECT_DDMSTORAGELINK_WHERE,
				_SQL_COUNT_DDMSTORAGELINK_WHERE,
				DDMStorageLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"ddmStorageLink.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					DDMStorageLink::getUuid),
				new FinderColumn<>(
					"ddmStorageLink.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, DDMStorageLink::getCompanyId));

		_uniquePersistenceFinderByClassPK = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByClassPK",
				new String[] {Long.class.getName()}, new String[] {"classPK"},
				0, 0, false, DDMStorageLink::getClassPK),
			_SQL_SELECT_DDMSTORAGELINK_WHERE, "",
			new FinderColumn<>(
				"ddmStorageLink.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, DDMStorageLink::getClassPK));

		_collectionPersistenceFinderByStructureId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByStructureId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"structureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByStructureId", new String[] {Long.class.getName()},
					new String[] {"structureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByStructureId", new String[] {Long.class.getName()},
					new String[] {"structureId"}, false),
				_SQL_SELECT_DDMSTORAGELINK_WHERE,
				_SQL_COUNT_DDMSTORAGELINK_WHERE,
				DDMStorageLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"ddmStorageLink.", "structureId", FinderColumn.Type.LONG,
					"=", true, true, DDMStorageLink::getStructureId));

		_collectionPersistenceFinderByStructureVersionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByStructureVersionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"structureVersionId"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByStructureVersionId",
					new String[] {Long.class.getName()},
					new String[] {"structureVersionId"}, false),
				_SQL_SELECT_DDMSTORAGELINK_WHERE,
				_SQL_COUNT_DDMSTORAGELINK_WHERE,
				DDMStorageLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new ArrayableFinderColumn<>(
					"ddmStorageLink.", "structureVersionId",
					FinderColumn.Type.LONG, "=", false, true, true,
					DDMStorageLink::getStructureVersionId));

		DDMStorageLinkUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDMStorageLinkUtil.setPersistence(null);

		entityCache.removeCache(DDMStorageLinkImpl.class.getName());
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		DDMStorageLinkModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DDMSTORAGELINK =
		"SELECT ddmStorageLink FROM DDMStorageLink ddmStorageLink";

	private static final String _SQL_SELECT_DDMSTORAGELINK_WHERE =
		"SELECT ddmStorageLink FROM DDMStorageLink ddmStorageLink WHERE ";

	private static final String _SQL_COUNT_DDMSTORAGELINK_WHERE =
		"SELECT COUNT(ddmStorageLink) FROM DDMStorageLink ddmStorageLink WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1329297692