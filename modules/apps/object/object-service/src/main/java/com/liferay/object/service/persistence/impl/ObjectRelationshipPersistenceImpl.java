/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectRelationshipException;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.model.ObjectRelationshipTable;
import com.liferay.object.model.impl.ObjectRelationshipImpl;
import com.liferay.object.model.impl.ObjectRelationshipModelImpl;
import com.liferay.object.service.persistence.ObjectRelationshipPersistence;
import com.liferay.object.service.persistence.ObjectRelationshipUtil;
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
 * The persistence implementation for the object relationship service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectRelationshipPersistence.class)
public class ObjectRelationshipPersistenceImpl
	extends BasePersistenceImpl
		<ObjectRelationship, NoSuchObjectRelationshipException>
	implements ObjectRelationshipPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectRelationshipUtil</code> to access the object relationship persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectRelationshipImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object relationships where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByUuid_First(
			String uuid,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByUuid_First(
		String uuid, OrderByComparator<ObjectRelationship> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object relationships where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object relationships where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object relationships where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object relationships where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object relationships where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the object relationships where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByCompanyId_First(
			long companyId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByCompanyId_First(
		long companyId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the object relationships where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of object relationships where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_collectionPersistenceFinderByObjectDefinitionId1;

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByObjectDefinitionId1(
		long objectDefinitionId1, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectDefinitionId1.find(
			finderCache, new Object[] {objectDefinitionId1}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByObjectDefinitionId1_First(
			long objectDefinitionId1,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		return _collectionPersistenceFinderByObjectDefinitionId1.findFirst(
			finderCache, new Object[] {objectDefinitionId1}, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByObjectDefinitionId1_First(
		long objectDefinitionId1,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return _collectionPersistenceFinderByObjectDefinitionId1.fetchFirst(
			finderCache, new Object[] {objectDefinitionId1}, orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 */
	@Override
	public void removeByObjectDefinitionId1(long objectDefinitionId1) {
		_collectionPersistenceFinderByObjectDefinitionId1.remove(
			finderCache, new Object[] {objectDefinitionId1});
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByObjectDefinitionId1(long objectDefinitionId1) {
		return _collectionPersistenceFinderByObjectDefinitionId1.count(
			finderCache, new Object[] {objectDefinitionId1});
	}

	private CollectionPersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_collectionPersistenceFinderByObjectDefinitionId2;

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByObjectDefinitionId2(
		long objectDefinitionId2, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectDefinitionId2.find(
			finderCache, new Object[] {objectDefinitionId2}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByObjectDefinitionId2_First(
			long objectDefinitionId2,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		return _collectionPersistenceFinderByObjectDefinitionId2.findFirst(
			finderCache, new Object[] {objectDefinitionId2}, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByObjectDefinitionId2_First(
		long objectDefinitionId2,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return _collectionPersistenceFinderByObjectDefinitionId2.fetchFirst(
			finderCache, new Object[] {objectDefinitionId2}, orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId2 = &#63; from the database.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 */
	@Override
	public void removeByObjectDefinitionId2(long objectDefinitionId2) {
		_collectionPersistenceFinderByObjectDefinitionId2.remove(
			finderCache, new Object[] {objectDefinitionId2});
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByObjectDefinitionId2(long objectDefinitionId2) {
		return _collectionPersistenceFinderByObjectDefinitionId2.count(
			finderCache, new Object[] {objectDefinitionId2});
	}

	private UniquePersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_uniquePersistenceFinderByObjectFieldId2;

	/**
	 * Returns the object relationship where objectFieldId2 = &#63; or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param objectFieldId2 the object field id2
	 * @return the matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByObjectFieldId2(long objectFieldId2)
		throws NoSuchObjectRelationshipException {

		return _uniquePersistenceFinderByObjectFieldId2.find(
			finderCache, new Object[] {objectFieldId2});
	}

	/**
	 * Returns the object relationship where objectFieldId2 = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectFieldId2 the object field id2
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByObjectFieldId2(
		long objectFieldId2, boolean useFinderCache) {

		return _uniquePersistenceFinderByObjectFieldId2.fetch(
			finderCache, new Object[] {objectFieldId2}, useFinderCache);
	}

	/**
	 * Removes the object relationship where objectFieldId2 = &#63; from the database.
	 *
	 * @param objectFieldId2 the object field id2
	 * @return the object relationship that was removed
	 */
	@Override
	public ObjectRelationship removeByObjectFieldId2(long objectFieldId2)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = findByObjectFieldId2(
			objectFieldId2);

		return remove(objectRelationship);
	}

	/**
	 * Returns the number of object relationships where objectFieldId2 = &#63;.
	 *
	 * @param objectFieldId2 the object field id2
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByObjectFieldId2(long objectFieldId2) {
		return _uniquePersistenceFinderByObjectFieldId2.count(
			finderCache, new Object[] {objectFieldId2});
	}

	private CollectionPersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_collectionPersistenceFinderByParameterObjectFieldId;

	/**
	 * Returns an ordered range of all the object relationships where parameterObjectFieldId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByParameterObjectFieldId(
		long parameterObjectFieldId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByParameterObjectFieldId.find(
			finderCache, new Object[] {parameterObjectFieldId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByParameterObjectFieldId_First(
			long parameterObjectFieldId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		return _collectionPersistenceFinderByParameterObjectFieldId.findFirst(
			finderCache, new Object[] {parameterObjectFieldId},
			orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByParameterObjectFieldId_First(
		long parameterObjectFieldId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return _collectionPersistenceFinderByParameterObjectFieldId.fetchFirst(
			finderCache, new Object[] {parameterObjectFieldId},
			orderByComparator);
	}

	/**
	 * Removes all the object relationships where parameterObjectFieldId = &#63; from the database.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 */
	@Override
	public void removeByParameterObjectFieldId(long parameterObjectFieldId) {
		_collectionPersistenceFinderByParameterObjectFieldId.remove(
			finderCache, new Object[] {parameterObjectFieldId});
	}

	/**
	 * Returns the number of object relationships where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByParameterObjectFieldId(long parameterObjectFieldId) {
		return _collectionPersistenceFinderByParameterObjectFieldId.count(
			finderCache, new Object[] {parameterObjectFieldId});
	}

	private CollectionPersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_collectionPersistenceFinderByC_U;

	/**
	 * Returns an ordered range of all the object relationships where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_U.find(
			finderCache, new Object[] {companyId, userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByC_U_First(
			long companyId, long userId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		return _collectionPersistenceFinderByC_U.findFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return _collectionPersistenceFinderByC_U.fetchFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Removes all the object relationships where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByC_U(long companyId, long userId) {
		_collectionPersistenceFinderByC_U.remove(
			finderCache, new Object[] {companyId, userId});
	}

	/**
	 * Returns the number of object relationships where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByC_U(long companyId, long userId) {
		return _collectionPersistenceFinderByC_U.count(
			finderCache, new Object[] {companyId, userId});
	}

	private CollectionPersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_collectionPersistenceFinderByODI1_E;

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_E(
		long objectDefinitionId1, boolean edge, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI1_E.find(
			finderCache, new Object[] {objectDefinitionId1, edge}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_E_First(
			long objectDefinitionId1, boolean edge,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		return _collectionPersistenceFinderByODI1_E.findFirst(
			finderCache, new Object[] {objectDefinitionId1, edge},
			orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_E_First(
		long objectDefinitionId1, boolean edge,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return _collectionPersistenceFinderByODI1_E.fetchFirst(
			finderCache, new Object[] {objectDefinitionId1, edge},
			orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and edge = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 */
	@Override
	public void removeByODI1_E(long objectDefinitionId1, boolean edge) {
		_collectionPersistenceFinderByODI1_E.remove(
			finderCache, new Object[] {objectDefinitionId1, edge});
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI1_E(long objectDefinitionId1, boolean edge) {
		return _collectionPersistenceFinderByODI1_E.count(
			finderCache, new Object[] {objectDefinitionId1, edge});
	}

	private CollectionPersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_collectionPersistenceFinderByODI1_N;

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_N(
		long objectDefinitionId1, String name, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI1_N.find(
			finderCache, new Object[] {objectDefinitionId1, name}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_N_First(
			long objectDefinitionId1, String name,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		return _collectionPersistenceFinderByODI1_N.findFirst(
			finderCache, new Object[] {objectDefinitionId1, name},
			orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_N_First(
		long objectDefinitionId1, String name,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return _collectionPersistenceFinderByODI1_N.fetchFirst(
			finderCache, new Object[] {objectDefinitionId1, name},
			orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and name = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 */
	@Override
	public void removeByODI1_N(long objectDefinitionId1, String name) {
		_collectionPersistenceFinderByODI1_N.remove(
			finderCache, new Object[] {objectDefinitionId1, name});
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI1_N(long objectDefinitionId1, String name) {
		return _collectionPersistenceFinderByODI1_N.count(
			finderCache, new Object[] {objectDefinitionId1, name});
	}

	private CollectionPersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_collectionPersistenceFinderByODI1_R;

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_R(
		long objectDefinitionId1, boolean reverse, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI1_R.find(
			finderCache, new Object[] {objectDefinitionId1, reverse}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_R_First(
			long objectDefinitionId1, boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		return _collectionPersistenceFinderByODI1_R.findFirst(
			finderCache, new Object[] {objectDefinitionId1, reverse},
			orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_R_First(
		long objectDefinitionId1, boolean reverse,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return _collectionPersistenceFinderByODI1_R.fetchFirst(
			finderCache, new Object[] {objectDefinitionId1, reverse},
			orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 */
	@Override
	public void removeByODI1_R(long objectDefinitionId1, boolean reverse) {
		_collectionPersistenceFinderByODI1_R.remove(
			finderCache, new Object[] {objectDefinitionId1, reverse});
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI1_R(long objectDefinitionId1, boolean reverse) {
		return _collectionPersistenceFinderByODI1_R.count(
			finderCache, new Object[] {objectDefinitionId1, reverse});
	}

	private CollectionPersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_collectionPersistenceFinderByODI2_E;

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI2_E(
		long objectDefinitionId2, boolean edge, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI2_E.find(
			finderCache, new Object[] {objectDefinitionId2, edge}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI2_E_First(
			long objectDefinitionId2, boolean edge,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		return _collectionPersistenceFinderByODI2_E.findFirst(
			finderCache, new Object[] {objectDefinitionId2, edge},
			orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI2_E_First(
		long objectDefinitionId2, boolean edge,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return _collectionPersistenceFinderByODI2_E.fetchFirst(
			finderCache, new Object[] {objectDefinitionId2, edge},
			orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId2 = &#63; and edge = &#63; from the database.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 */
	@Override
	public void removeByODI2_E(long objectDefinitionId2, boolean edge) {
		_collectionPersistenceFinderByODI2_E.remove(
			finderCache, new Object[] {objectDefinitionId2, edge});
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI2_E(long objectDefinitionId2, boolean edge) {
		return _collectionPersistenceFinderByODI2_E.count(
			finderCache, new Object[] {objectDefinitionId2, edge});
	}

	private CollectionPersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_collectionPersistenceFinderByODI2_R;

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI2_R(
		long objectDefinitionId2, boolean reverse, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI2_R.find(
			finderCache, new Object[] {objectDefinitionId2, reverse}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI2_R_First(
			long objectDefinitionId2, boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		return _collectionPersistenceFinderByODI2_R.findFirst(
			finderCache, new Object[] {objectDefinitionId2, reverse},
			orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI2_R_First(
		long objectDefinitionId2, boolean reverse,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return _collectionPersistenceFinderByODI2_R.fetchFirst(
			finderCache, new Object[] {objectDefinitionId2, reverse},
			orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; from the database.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 */
	@Override
	public void removeByODI2_R(long objectDefinitionId2, boolean reverse) {
		_collectionPersistenceFinderByODI2_R.remove(
			finderCache, new Object[] {objectDefinitionId2, reverse});
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI2_R(long objectDefinitionId2, boolean reverse) {
		return _collectionPersistenceFinderByODI2_R.count(
			finderCache, new Object[] {objectDefinitionId2, reverse});
	}

	private UniquePersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_uniquePersistenceFinderByDTN_R;

	/**
	 * Returns the object relationship where dbTableName = &#63; and reverse = &#63; or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param dbTableName the db table name
	 * @param reverse the reverse
	 * @return the matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByDTN_R(String dbTableName, boolean reverse)
		throws NoSuchObjectRelationshipException {

		return _uniquePersistenceFinderByDTN_R.find(
			finderCache, new Object[] {dbTableName, reverse});
	}

	/**
	 * Returns the object relationship where dbTableName = &#63; and reverse = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param dbTableName the db table name
	 * @param reverse the reverse
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByDTN_R(
		String dbTableName, boolean reverse, boolean useFinderCache) {

		return _uniquePersistenceFinderByDTN_R.fetch(
			finderCache, new Object[] {dbTableName, reverse}, useFinderCache);
	}

	/**
	 * Removes the object relationship where dbTableName = &#63; and reverse = &#63; from the database.
	 *
	 * @param dbTableName the db table name
	 * @param reverse the reverse
	 * @return the object relationship that was removed
	 */
	@Override
	public ObjectRelationship removeByDTN_R(String dbTableName, boolean reverse)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = findByDTN_R(
			dbTableName, reverse);

		return remove(objectRelationship);
	}

	/**
	 * Returns the number of object relationships where dbTableName = &#63; and reverse = &#63;.
	 *
	 * @param dbTableName the db table name
	 * @param reverse the reverse
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByDTN_R(String dbTableName, boolean reverse) {
		return _uniquePersistenceFinderByDTN_R.count(
			finderCache, new Object[] {dbTableName, reverse});
	}

	private UniquePersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_uniquePersistenceFinderByERC_C_ODI1;

	/**
	 * Returns the object relationship where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId1 = &#63; or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId1 the object definition id1
	 * @return the matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByERC_C_ODI1(
			String externalReferenceCode, long companyId,
			long objectDefinitionId1)
		throws NoSuchObjectRelationshipException {

		return _uniquePersistenceFinderByERC_C_ODI1.find(
			finderCache,
			new Object[] {
				externalReferenceCode, companyId, objectDefinitionId1
			});
	}

	/**
	 * Returns the object relationship where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId1 = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId1 the object definition id1
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByERC_C_ODI1(
		String externalReferenceCode, long companyId, long objectDefinitionId1,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C_ODI1.fetch(
			finderCache,
			new Object[] {
				externalReferenceCode, companyId, objectDefinitionId1
			},
			useFinderCache);
	}

	/**
	 * Removes the object relationship where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId1 = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId1 the object definition id1
	 * @return the object relationship that was removed
	 */
	@Override
	public ObjectRelationship removeByERC_C_ODI1(
			String externalReferenceCode, long companyId,
			long objectDefinitionId1)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = findByERC_C_ODI1(
			externalReferenceCode, companyId, objectDefinitionId1);

		return remove(objectRelationship);
	}

	/**
	 * Returns the number of object relationships where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId1 = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId1 the object definition id1
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByERC_C_ODI1(
		String externalReferenceCode, long companyId,
		long objectDefinitionId1) {

		return _uniquePersistenceFinderByERC_C_ODI1.count(
			finderCache,
			new Object[] {
				externalReferenceCode, companyId, objectDefinitionId1
			});
	}

	private CollectionPersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_collectionPersistenceFinderByODI1_ODI2_T;

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type,
		int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI1_ODI2_T.find(
			finderCache,
			new Object[] {objectDefinitionId1, objectDefinitionId2, type},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_ODI2_T_First(
			long objectDefinitionId1, long objectDefinitionId2, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		return _collectionPersistenceFinderByODI1_ODI2_T.findFirst(
			finderCache,
			new Object[] {objectDefinitionId1, objectDefinitionId2, type},
			orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_ODI2_T_First(
		long objectDefinitionId1, long objectDefinitionId2, String type,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return _collectionPersistenceFinderByODI1_ODI2_T.fetchFirst(
			finderCache,
			new Object[] {objectDefinitionId1, objectDefinitionId2, type},
			orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 */
	@Override
	public void removeByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type) {

		_collectionPersistenceFinderByODI1_ODI2_T.remove(
			finderCache,
			new Object[] {objectDefinitionId1, objectDefinitionId2, type});
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type) {

		return _collectionPersistenceFinderByODI1_ODI2_T.count(
			finderCache,
			new Object[] {objectDefinitionId1, objectDefinitionId2, type});
	}

	private CollectionPersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_collectionPersistenceFinderByODI1_DT_R;

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse,
		int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI1_DT_R.find(
			finderCache,
			new Object[] {objectDefinitionId1, deletionType, reverse}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_DT_R_First(
			long objectDefinitionId1, String deletionType, boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		return _collectionPersistenceFinderByODI1_DT_R.findFirst(
			finderCache,
			new Object[] {objectDefinitionId1, deletionType, reverse},
			orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_DT_R_First(
		long objectDefinitionId1, String deletionType, boolean reverse,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return _collectionPersistenceFinderByODI1_DT_R.fetchFirst(
			finderCache,
			new Object[] {objectDefinitionId1, deletionType, reverse},
			orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 */
	@Override
	public void removeByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse) {

		_collectionPersistenceFinderByODI1_DT_R.remove(
			finderCache,
			new Object[] {objectDefinitionId1, deletionType, reverse});
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse) {

		return _collectionPersistenceFinderByODI1_DT_R.count(
			finderCache,
			new Object[] {objectDefinitionId1, deletionType, reverse});
	}

	private CollectionPersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_collectionPersistenceFinderByODI1_R_T;

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type, int start,
		int end, OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI1_R_T.find(
			finderCache, new Object[] {objectDefinitionId1, reverse, type},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_R_T_First(
			long objectDefinitionId1, boolean reverse, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		return _collectionPersistenceFinderByODI1_R_T.findFirst(
			finderCache, new Object[] {objectDefinitionId1, reverse, type},
			orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_R_T_First(
		long objectDefinitionId1, boolean reverse, String type,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return _collectionPersistenceFinderByODI1_R_T.fetchFirst(
			finderCache, new Object[] {objectDefinitionId1, reverse, type},
			orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 */
	@Override
	public void removeByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type) {

		_collectionPersistenceFinderByODI1_R_T.remove(
			finderCache, new Object[] {objectDefinitionId1, reverse, type});
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type) {

		return _collectionPersistenceFinderByODI1_R_T.count(
			finderCache, new Object[] {objectDefinitionId1, reverse, type});
	}

	private CollectionPersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_collectionPersistenceFinderByODI2_R_T;

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type, int start,
		int end, OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI2_R_T.find(
			finderCache, new Object[] {objectDefinitionId2, reverse, type},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI2_R_T_First(
			long objectDefinitionId2, boolean reverse, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		return _collectionPersistenceFinderByODI2_R_T.findFirst(
			finderCache, new Object[] {objectDefinitionId2, reverse, type},
			orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI2_R_T_First(
		long objectDefinitionId2, boolean reverse, String type,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return _collectionPersistenceFinderByODI2_R_T.fetchFirst(
			finderCache, new Object[] {objectDefinitionId2, reverse, type},
			orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 */
	@Override
	public void removeByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type) {

		_collectionPersistenceFinderByODI2_R_T.remove(
			finderCache, new Object[] {objectDefinitionId2, reverse, type});
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type) {

		return _collectionPersistenceFinderByODI2_R_T.count(
			finderCache, new Object[] {objectDefinitionId2, reverse, type});
	}

	private CollectionPersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_collectionPersistenceFinderByODI1_ODI2_N_T;

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI1_ODI2_N_T.find(
			finderCache,
			new Object[] {objectDefinitionId1, objectDefinitionId2, name, type},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_ODI2_N_T_First(
			long objectDefinitionId1, long objectDefinitionId2, String name,
			String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		return _collectionPersistenceFinderByODI1_ODI2_N_T.findFirst(
			finderCache,
			new Object[] {objectDefinitionId1, objectDefinitionId2, name, type},
			orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_ODI2_N_T_First(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type, OrderByComparator<ObjectRelationship> orderByComparator) {

		return _collectionPersistenceFinderByODI1_ODI2_N_T.fetchFirst(
			finderCache,
			new Object[] {objectDefinitionId1, objectDefinitionId2, name, type},
			orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 */
	@Override
	public void removeByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type) {

		_collectionPersistenceFinderByODI1_ODI2_N_T.remove(
			finderCache,
			new Object[] {
				objectDefinitionId1, objectDefinitionId2, name, type
			});
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type) {

		return _collectionPersistenceFinderByODI1_ODI2_N_T.count(
			finderCache,
			new Object[] {
				objectDefinitionId1, objectDefinitionId2, name, type
			});
	}

	private UniquePersistenceFinder
		<ObjectRelationship, NoSuchObjectRelationshipException>
			_uniquePersistenceFinderByODI1_ODI2_N_R_T;

	/**
	 * Returns the object relationship where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and reverse = &#63; and type = &#63; or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param reverse the reverse
	 * @param type the type
	 * @return the matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_ODI2_N_R_T(
			long objectDefinitionId1, long objectDefinitionId2, String name,
			boolean reverse, String type)
		throws NoSuchObjectRelationshipException {

		return _uniquePersistenceFinderByODI1_ODI2_N_R_T.find(
			finderCache,
			new Object[] {
				objectDefinitionId1, objectDefinitionId2, name, reverse, type
			});
	}

	/**
	 * Returns the object relationship where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and reverse = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param reverse the reverse
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_ODI2_N_R_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		boolean reverse, String type, boolean useFinderCache) {

		return _uniquePersistenceFinderByODI1_ODI2_N_R_T.fetch(
			finderCache,
			new Object[] {
				objectDefinitionId1, objectDefinitionId2, name, reverse, type
			},
			useFinderCache);
	}

	/**
	 * Removes the object relationship where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and reverse = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param reverse the reverse
	 * @param type the type
	 * @return the object relationship that was removed
	 */
	@Override
	public ObjectRelationship removeByODI1_ODI2_N_R_T(
			long objectDefinitionId1, long objectDefinitionId2, String name,
			boolean reverse, String type)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = findByODI1_ODI2_N_R_T(
			objectDefinitionId1, objectDefinitionId2, name, reverse, type);

		return remove(objectRelationship);
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param reverse the reverse
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI1_ODI2_N_R_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		boolean reverse, String type) {

		return _uniquePersistenceFinderByODI1_ODI2_N_R_T.count(
			finderCache,
			new Object[] {
				objectDefinitionId1, objectDefinitionId2, name, reverse, type
			});
	}

	public ObjectRelationshipPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("system", "system_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectRelationship.class);

		setModelImplClass(ObjectRelationshipImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectRelationshipTable.INSTANCE);
	}

	/**
	 * Creates a new object relationship with the primary key. Does not add the object relationship to the database.
	 *
	 * @param objectRelationshipId the primary key for the new object relationship
	 * @return the new object relationship
	 */
	@Override
	public ObjectRelationship create(long objectRelationshipId) {
		ObjectRelationship objectRelationship = new ObjectRelationshipImpl();

		objectRelationship.setNew(true);
		objectRelationship.setPrimaryKey(objectRelationshipId);

		String uuid = PortalUUIDUtil.generate();

		objectRelationship.setUuid(uuid);

		objectRelationship.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectRelationship;
	}

	/**
	 * Removes the object relationship with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectRelationshipId the primary key of the object relationship
	 * @return the object relationship that was removed
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship remove(long objectRelationshipId)
		throws NoSuchObjectRelationshipException {

		return remove((Serializable)objectRelationshipId);
	}

	@Override
	protected ObjectRelationship removeImpl(
		ObjectRelationship objectRelationship) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectRelationship)) {
				objectRelationship = (ObjectRelationship)session.get(
					ObjectRelationshipImpl.class,
					objectRelationship.getPrimaryKeyObj());
			}

			if (objectRelationship != null) {
				session.delete(objectRelationship);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectRelationship != null) {
			clearCache(objectRelationship);
		}

		return objectRelationship;
	}

	@Override
	public ObjectRelationship updateImpl(
		ObjectRelationship objectRelationship) {

		boolean isNew = objectRelationship.isNew();

		if (!(objectRelationship instanceof ObjectRelationshipModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectRelationship.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectRelationship);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectRelationship proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectRelationship implementation " +
					objectRelationship.getClass());
		}

		ObjectRelationshipModelImpl objectRelationshipModelImpl =
			(ObjectRelationshipModelImpl)objectRelationship;

		if (Validator.isNull(objectRelationship.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectRelationship.setUuid(uuid);
		}

		if (Validator.isNull(objectRelationship.getExternalReferenceCode())) {
			objectRelationship.setExternalReferenceCode(
				objectRelationship.getUuid());
		}
		else {
			if (!Objects.equals(
					objectRelationshipModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					objectRelationship.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = objectRelationship.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = objectRelationship.getPrimaryKey();
					}

					try {
						objectRelationship.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ObjectRelationship.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								objectRelationship.getExternalReferenceCode(),
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

		if (isNew && (objectRelationship.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectRelationship.setCreateDate(date);
			}
			else {
				objectRelationship.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectRelationshipModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectRelationship.setModifiedDate(date);
			}
			else {
				objectRelationship.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectRelationship);
			}
			else {
				objectRelationship = (ObjectRelationship)session.merge(
					objectRelationship);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectRelationship, false);

		if (isNew) {
			objectRelationship.setNew(false);
		}

		objectRelationship.resetOriginalValues();

		return objectRelationship;
	}

	/**
	 * Returns the object relationship with the primary key or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param objectRelationshipId the primary key of the object relationship
	 * @return the object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship findByPrimaryKey(long objectRelationshipId)
		throws NoSuchObjectRelationshipException {

		return findByPrimaryKey((Serializable)objectRelationshipId);
	}

	/**
	 * Returns the object relationship with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectRelationshipId the primary key of the object relationship
	 * @return the object relationship, or <code>null</code> if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship fetchByPrimaryKey(long objectRelationshipId) {
		return fetchByPrimaryKey((Serializable)objectRelationshipId);
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
		return "objectRelationshipId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTRELATIONSHIP;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectRelationshipModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object relationship persistence.
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
			_SQL_SELECT_OBJECTRELATIONSHIP_WHERE,
			_SQL_COUNT_OBJECTRELATIONSHIP_WHERE,
			ObjectRelationshipModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"objectRelationship.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectRelationship::getUuid));

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
				_SQL_SELECT_OBJECTRELATIONSHIP_WHERE,
				_SQL_COUNT_OBJECTRELATIONSHIP_WHERE,
				ObjectRelationshipModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectRelationship.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectRelationship::getUuid),
				new FinderColumn<>(
					"objectRelationship.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectRelationship::getCompanyId));

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
				_SQL_SELECT_OBJECTRELATIONSHIP_WHERE,
				_SQL_COUNT_OBJECTRELATIONSHIP_WHERE,
				ObjectRelationshipModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectRelationship.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectRelationship::getCompanyId));

		_collectionPersistenceFinderByObjectDefinitionId1 =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByObjectDefinitionId1",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectDefinitionId1"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByObjectDefinitionId1",
					new String[] {Long.class.getName()},
					new String[] {"objectDefinitionId1"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByObjectDefinitionId1",
					new String[] {Long.class.getName()},
					new String[] {"objectDefinitionId1"}, false),
				_SQL_SELECT_OBJECTRELATIONSHIP_WHERE,
				_SQL_COUNT_OBJECTRELATIONSHIP_WHERE,
				ObjectRelationshipModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectRelationship.", "objectDefinitionId1",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectRelationship::getObjectDefinitionId1));

		_collectionPersistenceFinderByObjectDefinitionId2 =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByObjectDefinitionId2",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectDefinitionId2"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByObjectDefinitionId2",
					new String[] {Long.class.getName()},
					new String[] {"objectDefinitionId2"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByObjectDefinitionId2",
					new String[] {Long.class.getName()},
					new String[] {"objectDefinitionId2"}, false),
				_SQL_SELECT_OBJECTRELATIONSHIP_WHERE,
				_SQL_COUNT_OBJECTRELATIONSHIP_WHERE,
				ObjectRelationshipModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectRelationship.", "objectDefinitionId2",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectRelationship::getObjectDefinitionId2));

		_uniquePersistenceFinderByObjectFieldId2 =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByObjectFieldId2",
					new String[] {Long.class.getName()},
					new String[] {"objectFieldId2"}, 0, 0, false,
					ObjectRelationship::getObjectFieldId2),
				_SQL_SELECT_OBJECTRELATIONSHIP_WHERE, "",
				new FinderColumn<>(
					"objectRelationship.", "objectFieldId2",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectRelationship::getObjectFieldId2));

		_collectionPersistenceFinderByParameterObjectFieldId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByParameterObjectFieldId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"parameterObjectFieldId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByParameterObjectFieldId",
					new String[] {Long.class.getName()},
					new String[] {"parameterObjectFieldId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByParameterObjectFieldId",
					new String[] {Long.class.getName()},
					new String[] {"parameterObjectFieldId"}, false),
				_SQL_SELECT_OBJECTRELATIONSHIP_WHERE,
				_SQL_COUNT_OBJECTRELATIONSHIP_WHERE,
				ObjectRelationshipModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectRelationship.", "parameterObjectFieldId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectRelationship::getParameterObjectFieldId));

		_collectionPersistenceFinderByC_U = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_U",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "userId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "userId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "userId"}, false),
			_SQL_SELECT_OBJECTRELATIONSHIP_WHERE,
			_SQL_COUNT_OBJECTRELATIONSHIP_WHERE,
			ObjectRelationshipModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"objectRelationship.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, ObjectRelationship::getCompanyId),
			new FinderColumn<>(
				"objectRelationship.", "userId", FinderColumn.Type.LONG, "=",
				true, true, ObjectRelationship::getUserId));

		_collectionPersistenceFinderByODI1_E =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI1_E",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectDefinitionId1", "edge"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI1_E",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"objectDefinitionId1", "edge"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI1_E",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"objectDefinitionId1", "edge"}, false),
				_SQL_SELECT_OBJECTRELATIONSHIP_WHERE,
				_SQL_COUNT_OBJECTRELATIONSHIP_WHERE,
				ObjectRelationshipModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectRelationship.", "objectDefinitionId1",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectRelationship::getObjectDefinitionId1),
				new FinderColumn<>(
					"objectRelationship.", "edge", FinderColumn.Type.BOOLEAN,
					"=", true, true, ObjectRelationship::isEdge));

		_collectionPersistenceFinderByODI1_N =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI1_N",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectDefinitionId1", "name"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI1_N",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"objectDefinitionId1", "name"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI1_N",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"objectDefinitionId1", "name"}, 0, 2, false,
					null),
				_SQL_SELECT_OBJECTRELATIONSHIP_WHERE,
				_SQL_COUNT_OBJECTRELATIONSHIP_WHERE,
				ObjectRelationshipModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectRelationship.", "objectDefinitionId1",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectRelationship::getObjectDefinitionId1),
				new FinderColumn<>(
					"objectRelationship.", "name", FinderColumn.Type.STRING,
					"=", true, true, ObjectRelationship::getName));

		_collectionPersistenceFinderByODI1_R =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI1_R",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectDefinitionId1", "reverse"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI1_R",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"objectDefinitionId1", "reverse"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI1_R",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"objectDefinitionId1", "reverse"}, false),
				_SQL_SELECT_OBJECTRELATIONSHIP_WHERE,
				_SQL_COUNT_OBJECTRELATIONSHIP_WHERE,
				ObjectRelationshipModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectRelationship.", "objectDefinitionId1",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectRelationship::getObjectDefinitionId1),
				new FinderColumn<>(
					"objectRelationship.", "reverse", FinderColumn.Type.BOOLEAN,
					"=", true, true, ObjectRelationship::isReverse));

		_collectionPersistenceFinderByODI2_E =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI2_E",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectDefinitionId2", "edge"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI2_E",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"objectDefinitionId2", "edge"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI2_E",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"objectDefinitionId2", "edge"}, false),
				_SQL_SELECT_OBJECTRELATIONSHIP_WHERE,
				_SQL_COUNT_OBJECTRELATIONSHIP_WHERE,
				ObjectRelationshipModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectRelationship.", "objectDefinitionId2",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectRelationship::getObjectDefinitionId2),
				new FinderColumn<>(
					"objectRelationship.", "edge", FinderColumn.Type.BOOLEAN,
					"=", true, true, ObjectRelationship::isEdge));

		_collectionPersistenceFinderByODI2_R =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI2_R",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectDefinitionId2", "reverse"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI2_R",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"objectDefinitionId2", "reverse"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI2_R",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"objectDefinitionId2", "reverse"}, false),
				_SQL_SELECT_OBJECTRELATIONSHIP_WHERE,
				_SQL_COUNT_OBJECTRELATIONSHIP_WHERE,
				ObjectRelationshipModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectRelationship.", "objectDefinitionId2",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectRelationship::getObjectDefinitionId2),
				new FinderColumn<>(
					"objectRelationship.", "reverse", FinderColumn.Type.BOOLEAN,
					"=", true, true, ObjectRelationship::isReverse));

		_uniquePersistenceFinderByDTN_R = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByDTN_R",
				new String[] {String.class.getName(), Boolean.class.getName()},
				new String[] {"dbTableName", "reverse"}, 0, 1, false,
				convertNullFunction(ObjectRelationship::getDBTableName),
				ObjectRelationship::isReverse),
			_SQL_SELECT_OBJECTRELATIONSHIP_WHERE, "",
			new FinderColumn<>(
				"objectRelationship.", "dbTableName", FinderColumn.Type.STRING,
				"=", true, true, ObjectRelationship::getDBTableName),
			new FinderColumn<>(
				"objectRelationship.", "reverse", FinderColumn.Type.BOOLEAN,
				"=", true, true, ObjectRelationship::isReverse));

		_uniquePersistenceFinderByERC_C_ODI1 = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C_ODI1",
				new String[] {
					String.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"externalReferenceCode", "companyId", "objectDefinitionId1"
				},
				0, 1, false,
				convertNullFunction(
					ObjectRelationship::getExternalReferenceCode),
				ObjectRelationship::getCompanyId,
				ObjectRelationship::getObjectDefinitionId1),
			_SQL_SELECT_OBJECTRELATIONSHIP_WHERE, "",
			new FinderColumn<>(
				"objectRelationship.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectRelationship::getExternalReferenceCode),
			new FinderColumn<>(
				"objectRelationship.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, ObjectRelationship::getCompanyId),
			new FinderColumn<>(
				"objectRelationship.", "objectDefinitionId1",
				FinderColumn.Type.LONG, "=", true, true,
				ObjectRelationship::getObjectDefinitionId1));

		_collectionPersistenceFinderByODI1_ODI2_T =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI1_ODI2_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"objectDefinitionId1", "objectDefinitionId2", "type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByODI1_ODI2_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName()
					},
					new String[] {
						"objectDefinitionId1", "objectDefinitionId2", "type_"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByODI1_ODI2_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName()
					},
					new String[] {
						"objectDefinitionId1", "objectDefinitionId2", "type_"
					},
					0, 4, false, null),
				_SQL_SELECT_OBJECTRELATIONSHIP_WHERE,
				_SQL_COUNT_OBJECTRELATIONSHIP_WHERE,
				ObjectRelationshipModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectRelationship.", "objectDefinitionId1",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectRelationship::getObjectDefinitionId1),
				new FinderColumn<>(
					"objectRelationship.", "objectDefinitionId2",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectRelationship::getObjectDefinitionId2),
				new FinderColumn<>(
					"objectRelationship.", "type", "type_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectRelationship::getType));

		_collectionPersistenceFinderByODI1_DT_R =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI1_DT_R",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"objectDefinitionId1", "deletionType", "reverse"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByODI1_DT_R",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName()
					},
					new String[] {
						"objectDefinitionId1", "deletionType", "reverse"
					},
					0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByODI1_DT_R",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName()
					},
					new String[] {
						"objectDefinitionId1", "deletionType", "reverse"
					},
					0, 2, false, null),
				_SQL_SELECT_OBJECTRELATIONSHIP_WHERE,
				_SQL_COUNT_OBJECTRELATIONSHIP_WHERE,
				ObjectRelationshipModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectRelationship.", "objectDefinitionId1",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectRelationship::getObjectDefinitionId1),
				new FinderColumn<>(
					"objectRelationship.", "deletionType",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectRelationship::getDeletionType),
				new FinderColumn<>(
					"objectRelationship.", "reverse", FinderColumn.Type.BOOLEAN,
					"=", true, true, ObjectRelationship::isReverse));

		_collectionPersistenceFinderByODI1_R_T =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI1_R_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectDefinitionId1", "reverse", "type_"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI1_R_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {"objectDefinitionId1", "reverse", "type_"}, 0,
					4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByODI1_R_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {"objectDefinitionId1", "reverse", "type_"}, 0,
					4, false, null),
				_SQL_SELECT_OBJECTRELATIONSHIP_WHERE,
				_SQL_COUNT_OBJECTRELATIONSHIP_WHERE,
				ObjectRelationshipModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectRelationship.", "objectDefinitionId1",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectRelationship::getObjectDefinitionId1),
				new FinderColumn<>(
					"objectRelationship.", "reverse", FinderColumn.Type.BOOLEAN,
					"=", true, true, ObjectRelationship::isReverse),
				new FinderColumn<>(
					"objectRelationship.", "type", "type_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectRelationship::getType));

		_collectionPersistenceFinderByODI2_R_T =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI2_R_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectDefinitionId2", "reverse", "type_"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI2_R_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {"objectDefinitionId2", "reverse", "type_"}, 0,
					4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByODI2_R_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {"objectDefinitionId2", "reverse", "type_"}, 0,
					4, false, null),
				_SQL_SELECT_OBJECTRELATIONSHIP_WHERE,
				_SQL_COUNT_OBJECTRELATIONSHIP_WHERE,
				ObjectRelationshipModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectRelationship.", "objectDefinitionId2",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectRelationship::getObjectDefinitionId2),
				new FinderColumn<>(
					"objectRelationship.", "reverse", FinderColumn.Type.BOOLEAN,
					"=", true, true, ObjectRelationship::isReverse),
				new FinderColumn<>(
					"objectRelationship.", "type", "type_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectRelationship::getType));

		_collectionPersistenceFinderByODI1_ODI2_N_T =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByODI1_ODI2_N_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"objectDefinitionId1", "objectDefinitionId2", "name",
						"type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByODI1_ODI2_N_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), String.class.getName()
					},
					new String[] {
						"objectDefinitionId1", "objectDefinitionId2", "name",
						"type_"
					},
					0, 12, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByODI1_ODI2_N_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), String.class.getName()
					},
					new String[] {
						"objectDefinitionId1", "objectDefinitionId2", "name",
						"type_"
					},
					0, 12, false, null),
				_SQL_SELECT_OBJECTRELATIONSHIP_WHERE,
				_SQL_COUNT_OBJECTRELATIONSHIP_WHERE,
				ObjectRelationshipModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectRelationship.", "objectDefinitionId1",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectRelationship::getObjectDefinitionId1),
				new FinderColumn<>(
					"objectRelationship.", "objectDefinitionId2",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectRelationship::getObjectDefinitionId2),
				new FinderColumn<>(
					"objectRelationship.", "name", FinderColumn.Type.STRING,
					"=", true, true, ObjectRelationship::getName),
				new FinderColumn<>(
					"objectRelationship.", "type", "type_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectRelationship::getType));

		_uniquePersistenceFinderByODI1_ODI2_N_R_T =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByODI1_ODI2_N_R_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {
						"objectDefinitionId1", "objectDefinitionId2", "name",
						"reverse", "type_"
					},
					0, 20, false, ObjectRelationship::getObjectDefinitionId1,
					ObjectRelationship::getObjectDefinitionId2,
					convertNullFunction(ObjectRelationship::getName),
					ObjectRelationship::isReverse,
					convertNullFunction(ObjectRelationship::getType)),
				_SQL_SELECT_OBJECTRELATIONSHIP_WHERE, "",
				new FinderColumn<>(
					"objectRelationship.", "objectDefinitionId1",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectRelationship::getObjectDefinitionId1),
				new FinderColumn<>(
					"objectRelationship.", "objectDefinitionId2",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectRelationship::getObjectDefinitionId2),
				new FinderColumn<>(
					"objectRelationship.", "name", FinderColumn.Type.STRING,
					"=", true, true, ObjectRelationship::getName),
				new FinderColumn<>(
					"objectRelationship.", "reverse", FinderColumn.Type.BOOLEAN,
					"=", true, true, ObjectRelationship::isReverse),
				new FinderColumn<>(
					"objectRelationship.", "type", "type_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectRelationship::getType));

		ObjectRelationshipUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectRelationshipUtil.setPersistence(null);

		entityCache.removeCache(ObjectRelationshipImpl.class.getName());
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
		ObjectRelationshipModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTRELATIONSHIP =
		"SELECT objectRelationship FROM ObjectRelationship objectRelationship";

	private static final String _SQL_SELECT_OBJECTRELATIONSHIP_WHERE =
		"SELECT objectRelationship FROM ObjectRelationship objectRelationship WHERE ";

	private static final String _SQL_COUNT_OBJECTRELATIONSHIP_WHERE =
		"SELECT COUNT(objectRelationship) FROM ObjectRelationship objectRelationship WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ObjectRelationship exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectRelationshipPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "system", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-513536071