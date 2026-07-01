/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectEntryVersionException;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.model.ObjectEntryVersionTable;
import com.liferay.object.model.impl.ObjectEntryVersionImpl;
import com.liferay.object.model.impl.ObjectEntryVersionModelImpl;
import com.liferay.object.service.persistence.ObjectEntryVersionPersistence;
import com.liferay.object.service.persistence.ObjectEntryVersionUtil;
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
 * The persistence implementation for the object entry version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectEntryVersionPersistence.class)
public class ObjectEntryVersionPersistenceImpl
	extends BasePersistenceImpl
		<ObjectEntryVersion, NoSuchObjectEntryVersionException>
	implements ObjectEntryVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectEntryVersionUtil</code> to access the object entry version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectEntryVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ObjectEntryVersion, NoSuchObjectEntryVersionException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByUuid_First(
			String uuid,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByUuid_First(
		String uuid, OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object entry versions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object entry versions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object entry versions
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ObjectEntryVersion, NoSuchObjectEntryVersionException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object entry versions where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object entry versions
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ObjectEntryVersion, NoSuchObjectEntryVersionException>
			_collectionPersistenceFinderByObjectDefinitionId;

	/**
	 * Returns an ordered range of all the object entry versions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectDefinitionId.find(
			finderCache, new Object[] {objectDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry version in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		return _collectionPersistenceFinderByObjectDefinitionId.findFirst(
			finderCache, new Object[] {objectDefinitionId}, orderByComparator);
	}

	/**
	 * Returns the first object entry version in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByObjectDefinitionId.fetchFirst(
			finderCache, new Object[] {objectDefinitionId}, orderByComparator);
	}

	/**
	 * Removes all the object entry versions where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByObjectDefinitionId(long objectDefinitionId) {
		_collectionPersistenceFinderByObjectDefinitionId.remove(
			finderCache, new Object[] {objectDefinitionId});
	}

	/**
	 * Returns the number of object entry versions where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object entry versions
	 */
	@Override
	public int countByObjectDefinitionId(long objectDefinitionId) {
		return _collectionPersistenceFinderByObjectDefinitionId.count(
			finderCache, new Object[] {objectDefinitionId});
	}

	private CollectionPersistenceFinder
		<ObjectEntryVersion, NoSuchObjectEntryVersionException>
			_collectionPersistenceFinderByObjectEntryId;

	/**
	 * Returns an ordered range of all the object entry versions where objectEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByObjectEntryId(
		long objectEntryId, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectEntryId.find(
			finderCache, new Object[] {objectEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry version in the ordered set where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByObjectEntryId_First(
			long objectEntryId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		return _collectionPersistenceFinderByObjectEntryId.findFirst(
			finderCache, new Object[] {objectEntryId}, orderByComparator);
	}

	/**
	 * Returns the first object entry version in the ordered set where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByObjectEntryId_First(
		long objectEntryId,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByObjectEntryId.fetchFirst(
			finderCache, new Object[] {objectEntryId}, orderByComparator);
	}

	/**
	 * Removes all the object entry versions where objectEntryId = &#63; from the database.
	 *
	 * @param objectEntryId the object entry ID
	 */
	@Override
	public void removeByObjectEntryId(long objectEntryId) {
		_collectionPersistenceFinderByObjectEntryId.remove(
			finderCache, new Object[] {objectEntryId});
	}

	/**
	 * Returns the number of object entry versions where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @return the number of matching object entry versions
	 */
	@Override
	public int countByObjectEntryId(long objectEntryId) {
		return _collectionPersistenceFinderByObjectEntryId.count(
			finderCache, new Object[] {objectEntryId});
	}

	private CollectionPersistenceFinder
		<ObjectEntryVersion, NoSuchObjectEntryVersionException>
			_collectionPersistenceFinderByC_CD;

	/**
	 * Returns an ordered range of all the object entry versions where companyId = &#63; and createDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByC_CD(
		long companyId, Date createDate, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CD.find(
			finderCache, new Object[] {companyId, createDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry version in the ordered set where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByC_CD_First(
			long companyId, Date createDate,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		return _collectionPersistenceFinderByC_CD.findFirst(
			finderCache, new Object[] {companyId, createDate},
			orderByComparator);
	}

	/**
	 * Returns the first object entry version in the ordered set where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByC_CD_First(
		long companyId, Date createDate,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByC_CD.fetchFirst(
			finderCache, new Object[] {companyId, createDate},
			orderByComparator);
	}

	/**
	 * Removes all the object entry versions where companyId = &#63; and createDate = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 */
	@Override
	public void removeByC_CD(long companyId, Date createDate) {
		_collectionPersistenceFinderByC_CD.remove(
			finderCache, new Object[] {companyId, createDate});
	}

	/**
	 * Returns the number of object entry versions where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @return the number of matching object entry versions
	 */
	@Override
	public int countByC_CD(long companyId, Date createDate) {
		return _collectionPersistenceFinderByC_CD.count(
			finderCache, new Object[] {companyId, createDate});
	}

	private UniquePersistenceFinder
		<ObjectEntryVersion, NoSuchObjectEntryVersionException>
			_uniquePersistenceFinderByOEI_V;

	/**
	 * Returns the object entry version where objectEntryId = &#63; and version = &#63; or throws a <code>NoSuchObjectEntryVersionException</code> if it could not be found.
	 *
	 * @param objectEntryId the object entry ID
	 * @param version the version
	 * @return the matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByOEI_V(long objectEntryId, int version)
		throws NoSuchObjectEntryVersionException {

		return _uniquePersistenceFinderByOEI_V.find(
			finderCache, new Object[] {objectEntryId, version});
	}

	/**
	 * Returns the object entry version where objectEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectEntryId the object entry ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByOEI_V(
		long objectEntryId, int version, boolean useFinderCache) {

		return _uniquePersistenceFinderByOEI_V.fetch(
			finderCache, new Object[] {objectEntryId, version}, useFinderCache);
	}

	/**
	 * Removes the object entry version where objectEntryId = &#63; and version = &#63; from the database.
	 *
	 * @param objectEntryId the object entry ID
	 * @param version the version
	 * @return the object entry version that was removed
	 */
	@Override
	public ObjectEntryVersion removeByOEI_V(long objectEntryId, int version)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = findByOEI_V(
			objectEntryId, version);

		return remove(objectEntryVersion);
	}

	/**
	 * Returns the number of object entry versions where objectEntryId = &#63; and version = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param version the version
	 * @return the number of matching object entry versions
	 */
	@Override
	public int countByOEI_V(long objectEntryId, int version) {
		return _uniquePersistenceFinderByOEI_V.count(
			finderCache, new Object[] {objectEntryId, version});
	}

	private CollectionPersistenceFinder
		<ObjectEntryVersion, NoSuchObjectEntryVersionException>
			_collectionPersistenceFinderByOEI_S;

	/**
	 * Returns an ordered range of all the object entry versions where objectEntryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByOEI_S(
		long objectEntryId, int status, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByOEI_S.find(
			finderCache, new Object[] {objectEntryId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry version in the ordered set where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByOEI_S_First(
			long objectEntryId, int status,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		return _collectionPersistenceFinderByOEI_S.findFirst(
			finderCache, new Object[] {objectEntryId, status},
			orderByComparator);
	}

	/**
	 * Returns the first object entry version in the ordered set where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByOEI_S_First(
		long objectEntryId, int status,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByOEI_S.fetchFirst(
			finderCache, new Object[] {objectEntryId, status},
			orderByComparator);
	}

	/**
	 * Removes all the object entry versions where objectEntryId = &#63; and status = &#63; from the database.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 */
	@Override
	public void removeByOEI_S(long objectEntryId, int status) {
		_collectionPersistenceFinderByOEI_S.remove(
			finderCache, new Object[] {objectEntryId, status});
	}

	/**
	 * Returns the number of object entry versions where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @return the number of matching object entry versions
	 */
	@Override
	public int countByOEI_S(long objectEntryId, int status) {
		return _collectionPersistenceFinderByOEI_S.count(
			finderCache, new Object[] {objectEntryId, status});
	}

	public ObjectEntryVersionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectEntryVersion.class);

		setModelImplClass(ObjectEntryVersionImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectEntryVersionTable.INSTANCE);
	}

	/**
	 * Creates a new object entry version with the primary key. Does not add the object entry version to the database.
	 *
	 * @param objectEntryVersionId the primary key for the new object entry version
	 * @return the new object entry version
	 */
	@Override
	public ObjectEntryVersion create(long objectEntryVersionId) {
		ObjectEntryVersion objectEntryVersion = new ObjectEntryVersionImpl();

		objectEntryVersion.setNew(true);
		objectEntryVersion.setPrimaryKey(objectEntryVersionId);

		String uuid = PortalUUIDUtil.generate();

		objectEntryVersion.setUuid(uuid);

		objectEntryVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectEntryVersion;
	}

	/**
	 * Removes the object entry version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectEntryVersionId the primary key of the object entry version
	 * @return the object entry version that was removed
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	@Override
	public ObjectEntryVersion remove(long objectEntryVersionId)
		throws NoSuchObjectEntryVersionException {

		return remove((Serializable)objectEntryVersionId);
	}

	@Override
	protected ObjectEntryVersion removeImpl(
		ObjectEntryVersion objectEntryVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectEntryVersion)) {
				objectEntryVersion = (ObjectEntryVersion)session.get(
					ObjectEntryVersionImpl.class,
					objectEntryVersion.getPrimaryKeyObj());
			}

			if (objectEntryVersion != null) {
				session.delete(objectEntryVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectEntryVersion != null) {
			clearCache(objectEntryVersion);
		}

		return objectEntryVersion;
	}

	@Override
	public ObjectEntryVersion updateImpl(
		ObjectEntryVersion objectEntryVersion) {

		boolean isNew = objectEntryVersion.isNew();

		if (!(objectEntryVersion instanceof ObjectEntryVersionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectEntryVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectEntryVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectEntryVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectEntryVersion implementation " +
					objectEntryVersion.getClass());
		}

		ObjectEntryVersionModelImpl objectEntryVersionModelImpl =
			(ObjectEntryVersionModelImpl)objectEntryVersion;

		if (Validator.isNull(objectEntryVersion.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectEntryVersion.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectEntryVersion.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectEntryVersion.setCreateDate(date);
			}
			else {
				objectEntryVersion.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectEntryVersionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectEntryVersion.setModifiedDate(date);
			}
			else {
				objectEntryVersion.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectEntryVersion);
			}
			else {
				objectEntryVersion = (ObjectEntryVersion)session.merge(
					objectEntryVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectEntryVersion, false);

		if (isNew) {
			objectEntryVersion.setNew(false);
		}

		objectEntryVersion.resetOriginalValues();

		return objectEntryVersion;
	}

	/**
	 * Returns the object entry version with the primary key or throws a <code>NoSuchObjectEntryVersionException</code> if it could not be found.
	 *
	 * @param objectEntryVersionId the primary key of the object entry version
	 * @return the object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	@Override
	public ObjectEntryVersion findByPrimaryKey(long objectEntryVersionId)
		throws NoSuchObjectEntryVersionException {

		return findByPrimaryKey((Serializable)objectEntryVersionId);
	}

	/**
	 * Returns the object entry version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectEntryVersionId the primary key of the object entry version
	 * @return the object entry version, or <code>null</code> if a object entry version with the primary key could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByPrimaryKey(long objectEntryVersionId) {
		return fetchByPrimaryKey((Serializable)objectEntryVersionId);
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
		return "objectEntryVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTENTRYVERSION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectEntryVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object entry version persistence.
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
			_SQL_SELECT_OBJECTENTRYVERSION_WHERE,
			_SQL_COUNT_OBJECTENTRYVERSION_WHERE,
			ObjectEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"objectEntryVersion.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectEntryVersion::getUuid));

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
				_SQL_SELECT_OBJECTENTRYVERSION_WHERE,
				_SQL_COUNT_OBJECTENTRYVERSION_WHERE,
				ObjectEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"objectEntryVersion.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectEntryVersion::getUuid),
				new FinderColumn<>(
					"objectEntryVersion.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectEntryVersion::getCompanyId));

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
				_SQL_SELECT_OBJECTENTRYVERSION_WHERE,
				_SQL_COUNT_OBJECTENTRYVERSION_WHERE,
				ObjectEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"objectEntryVersion.", "objectDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectEntryVersion::getObjectDefinitionId));

		_collectionPersistenceFinderByObjectEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByObjectEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByObjectEntryId", new String[] {Long.class.getName()},
					new String[] {"objectEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByObjectEntryId", new String[] {Long.class.getName()},
					new String[] {"objectEntryId"}, false),
				_SQL_SELECT_OBJECTENTRYVERSION_WHERE,
				_SQL_COUNT_OBJECTENTRYVERSION_WHERE,
				ObjectEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"objectEntryVersion.", "objectEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectEntryVersion::getObjectEntryId));

		_collectionPersistenceFinderByC_CD = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CD",
				new String[] {
					Long.class.getName(), Date.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "createDate"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CD",
				new String[] {Long.class.getName(), Date.class.getName()},
				new String[] {"companyId", "createDate"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_CD",
				new String[] {Long.class.getName(), Date.class.getName()},
				new String[] {"companyId", "createDate"}, false),
			_SQL_SELECT_OBJECTENTRYVERSION_WHERE,
			_SQL_COUNT_OBJECTENTRYVERSION_WHERE,
			ObjectEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"objectEntryVersion.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, ObjectEntryVersion::getCompanyId),
			new FinderColumn<>(
				"objectEntryVersion.", "createDate", FinderColumn.Type.DATE,
				"=", true, true, ObjectEntryVersion::getCreateDate));

		_uniquePersistenceFinderByOEI_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByOEI_V",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"objectEntryId", "version"}, 0, 0, false,
				ObjectEntryVersion::getObjectEntryId,
				ObjectEntryVersion::getVersion),
			_SQL_SELECT_OBJECTENTRYVERSION_WHERE, "",
			new FinderColumn<>(
				"objectEntryVersion.", "objectEntryId", FinderColumn.Type.LONG,
				"=", true, true, ObjectEntryVersion::getObjectEntryId),
			new FinderColumn<>(
				"objectEntryVersion.", "version", FinderColumn.Type.INTEGER,
				"=", true, true, ObjectEntryVersion::getVersion));

		_collectionPersistenceFinderByOEI_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByOEI_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"objectEntryId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByOEI_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"objectEntryId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByOEI_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"objectEntryId", "status"}, false),
			_SQL_SELECT_OBJECTENTRYVERSION_WHERE,
			_SQL_COUNT_OBJECTENTRYVERSION_WHERE,
			ObjectEntryVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"objectEntryVersion.", "objectEntryId", FinderColumn.Type.LONG,
				"=", true, true, ObjectEntryVersion::getObjectEntryId),
			new FinderColumn<>(
				"objectEntryVersion.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, ObjectEntryVersion::getStatus));

		ObjectEntryVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectEntryVersionUtil.setPersistence(null);

		entityCache.removeCache(ObjectEntryVersionImpl.class.getName());
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
		ObjectEntryVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTENTRYVERSION =
		"SELECT objectEntryVersion FROM ObjectEntryVersion objectEntryVersion";

	private static final String _SQL_SELECT_OBJECTENTRYVERSION_WHERE =
		"SELECT objectEntryVersion FROM ObjectEntryVersion objectEntryVersion WHERE ";

	private static final String _SQL_COUNT_OBJECTENTRYVERSION_WHERE =
		"SELECT COUNT(objectEntryVersion) FROM ObjectEntryVersion objectEntryVersion WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1771205620