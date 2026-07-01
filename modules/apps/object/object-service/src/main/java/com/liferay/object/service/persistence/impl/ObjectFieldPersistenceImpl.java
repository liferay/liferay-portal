/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectFieldException;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldTable;
import com.liferay.object.model.impl.ObjectFieldImpl;
import com.liferay.object.model.impl.ObjectFieldModelImpl;
import com.liferay.object.service.persistence.ObjectFieldPersistence;
import com.liferay.object.service.persistence.ObjectFieldUtil;
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
 * The persistence implementation for the object field service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectFieldPersistence.class)
public class ObjectFieldPersistenceImpl
	extends BasePersistenceImpl<ObjectField, NoSuchObjectFieldException>
	implements ObjectFieldPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectFieldUtil</code> to access the object field persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectFieldImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<ObjectField, NoSuchObjectFieldException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object fields where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByUuid_First(
			String uuid, OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByUuid_First(
		String uuid, OrderByComparator<ObjectField> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object fields where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object fields where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object fields
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder<ObjectField, NoSuchObjectFieldException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object fields where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectField> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object fields where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object fields where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object fields
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<ObjectField, NoSuchObjectFieldException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the object fields where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByCompanyId_First(
			long companyId, OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByCompanyId_First(
		long companyId, OrderByComparator<ObjectField> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the object fields where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of object fields where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching object fields
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder<ObjectField, NoSuchObjectFieldException>
		_collectionPersistenceFinderByListTypeDefinitionId;

	/**
	 * Returns an ordered range of all the object fields where listTypeDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByListTypeDefinitionId(
		long listTypeDefinitionId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByListTypeDefinitionId.find(
			finderCache, new Object[] {listTypeDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByListTypeDefinitionId_First(
			long listTypeDefinitionId,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		return _collectionPersistenceFinderByListTypeDefinitionId.findFirst(
			finderCache, new Object[] {listTypeDefinitionId},
			orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByListTypeDefinitionId_First(
		long listTypeDefinitionId,
		OrderByComparator<ObjectField> orderByComparator) {

		return _collectionPersistenceFinderByListTypeDefinitionId.fetchFirst(
			finderCache, new Object[] {listTypeDefinitionId},
			orderByComparator);
	}

	/**
	 * Removes all the object fields where listTypeDefinitionId = &#63; from the database.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 */
	@Override
	public void removeByListTypeDefinitionId(long listTypeDefinitionId) {
		_collectionPersistenceFinderByListTypeDefinitionId.remove(
			finderCache, new Object[] {listTypeDefinitionId});
	}

	/**
	 * Returns the number of object fields where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @return the number of matching object fields
	 */
	@Override
	public int countByListTypeDefinitionId(long listTypeDefinitionId) {
		return _collectionPersistenceFinderByListTypeDefinitionId.count(
			finderCache, new Object[] {listTypeDefinitionId});
	}

	private CollectionPersistenceFinder<ObjectField, NoSuchObjectFieldException>
		_collectionPersistenceFinderByObjectDefinitionId;

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectDefinitionId.find(
			finderCache, new Object[] {objectDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		return _collectionPersistenceFinderByObjectDefinitionId.findFirst(
			finderCache, new Object[] {objectDefinitionId}, orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectField> orderByComparator) {

		return _collectionPersistenceFinderByObjectDefinitionId.fetchFirst(
			finderCache, new Object[] {objectDefinitionId}, orderByComparator);
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByObjectDefinitionId(long objectDefinitionId) {
		_collectionPersistenceFinderByObjectDefinitionId.remove(
			finderCache, new Object[] {objectDefinitionId});
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object fields
	 */
	@Override
	public int countByObjectDefinitionId(long objectDefinitionId) {
		return _collectionPersistenceFinderByObjectDefinitionId.count(
			finderCache, new Object[] {objectDefinitionId});
	}

	private CollectionPersistenceFinder<ObjectField, NoSuchObjectFieldException>
		_collectionPersistenceFinderByC_U;

	/**
	 * Returns an ordered range of all the object fields where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_U.find(
			finderCache, new Object[] {companyId, userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByC_U_First(
			long companyId, long userId,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		return _collectionPersistenceFinderByC_U.findFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<ObjectField> orderByComparator) {

		return _collectionPersistenceFinderByC_U.fetchFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Removes all the object fields where companyId = &#63; and userId = &#63; from the database.
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
	 * Returns the number of object fields where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching object fields
	 */
	@Override
	public int countByC_U(long companyId, long userId) {
		return _collectionPersistenceFinderByC_U.count(
			finderCache, new Object[] {companyId, userId});
	}

	private CollectionPersistenceFinder<ObjectField, NoSuchObjectFieldException>
		_collectionPersistenceFinderByC_BT;

	/**
	 * Returns an ordered range of all the object fields where companyId = &#63; and businessType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param businessType the business type
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByC_BT(
		long companyId, String businessType, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_BT.find(
			finderCache, new Object[] {companyId, businessType}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where companyId = &#63; and businessType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param businessType the business type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByC_BT_First(
			long companyId, String businessType,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		return _collectionPersistenceFinderByC_BT.findFirst(
			finderCache, new Object[] {companyId, businessType},
			orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where companyId = &#63; and businessType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param businessType the business type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByC_BT_First(
		long companyId, String businessType,
		OrderByComparator<ObjectField> orderByComparator) {

		return _collectionPersistenceFinderByC_BT.fetchFirst(
			finderCache, new Object[] {companyId, businessType},
			orderByComparator);
	}

	/**
	 * Removes all the object fields where companyId = &#63; and businessType = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param businessType the business type
	 */
	@Override
	public void removeByC_BT(long companyId, String businessType) {
		_collectionPersistenceFinderByC_BT.remove(
			finderCache, new Object[] {companyId, businessType});
	}

	/**
	 * Returns the number of object fields where companyId = &#63; and businessType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param businessType the business type
	 * @return the number of matching object fields
	 */
	@Override
	public int countByC_BT(long companyId, String businessType) {
		return _collectionPersistenceFinderByC_BT.count(
			finderCache, new Object[] {companyId, businessType});
	}

	private CollectionPersistenceFinder<ObjectField, NoSuchObjectFieldException>
		_collectionPersistenceFinderByLTDI_S;

	/**
	 * Returns an ordered range of all the object fields where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByLTDI_S(
		long listTypeDefinitionId, boolean state, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLTDI_S.find(
			finderCache, new Object[] {listTypeDefinitionId, state}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByLTDI_S_First(
			long listTypeDefinitionId, boolean state,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		return _collectionPersistenceFinderByLTDI_S.findFirst(
			finderCache, new Object[] {listTypeDefinitionId, state},
			orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByLTDI_S_First(
		long listTypeDefinitionId, boolean state,
		OrderByComparator<ObjectField> orderByComparator) {

		return _collectionPersistenceFinderByLTDI_S.fetchFirst(
			finderCache, new Object[] {listTypeDefinitionId, state},
			orderByComparator);
	}

	/**
	 * Removes all the object fields where listTypeDefinitionId = &#63; and state = &#63; from the database.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 */
	@Override
	public void removeByLTDI_S(long listTypeDefinitionId, boolean state) {
		_collectionPersistenceFinderByLTDI_S.remove(
			finderCache, new Object[] {listTypeDefinitionId, state});
	}

	/**
	 * Returns the number of object fields where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @return the number of matching object fields
	 */
	@Override
	public int countByLTDI_S(long listTypeDefinitionId, boolean state) {
		return _collectionPersistenceFinderByLTDI_S.count(
			finderCache, new Object[] {listTypeDefinitionId, state});
	}

	private CollectionPersistenceFinder<ObjectField, NoSuchObjectFieldException>
		_collectionPersistenceFinderByODI_BT;

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_BT(
		long objectDefinitionId, String businessType, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI_BT.find(
			finderCache, new Object[] {objectDefinitionId, businessType}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_BT_First(
			long objectDefinitionId, String businessType,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		return _collectionPersistenceFinderByODI_BT.findFirst(
			finderCache, new Object[] {objectDefinitionId, businessType},
			orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_BT_First(
		long objectDefinitionId, String businessType,
		OrderByComparator<ObjectField> orderByComparator) {

		return _collectionPersistenceFinderByODI_BT.fetchFirst(
			finderCache, new Object[] {objectDefinitionId, businessType},
			orderByComparator);
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and businessType = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 */
	@Override
	public void removeByODI_BT(long objectDefinitionId, String businessType) {
		_collectionPersistenceFinderByODI_BT.remove(
			finderCache, new Object[] {objectDefinitionId, businessType});
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @return the number of matching object fields
	 */
	@Override
	public int countByODI_BT(long objectDefinitionId, String businessType) {
		return _collectionPersistenceFinderByODI_BT.count(
			finderCache, new Object[] {objectDefinitionId, businessType});
	}

	private CollectionPersistenceFinder<ObjectField, NoSuchObjectFieldException>
		_collectionPersistenceFinderByODI_DTN;

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_DTN(
		long objectDefinitionId, String dbTableName, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI_DTN.find(
			finderCache, new Object[] {objectDefinitionId, dbTableName}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_DTN_First(
			long objectDefinitionId, String dbTableName,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		return _collectionPersistenceFinderByODI_DTN.findFirst(
			finderCache, new Object[] {objectDefinitionId, dbTableName},
			orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_DTN_First(
		long objectDefinitionId, String dbTableName,
		OrderByComparator<ObjectField> orderByComparator) {

		return _collectionPersistenceFinderByODI_DTN.fetchFirst(
			finderCache, new Object[] {objectDefinitionId, dbTableName},
			orderByComparator);
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and dbTableName = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 */
	@Override
	public void removeByODI_DTN(long objectDefinitionId, String dbTableName) {
		_collectionPersistenceFinderByODI_DTN.remove(
			finderCache, new Object[] {objectDefinitionId, dbTableName});
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @return the number of matching object fields
	 */
	@Override
	public int countByODI_DTN(long objectDefinitionId, String dbTableName) {
		return _collectionPersistenceFinderByODI_DTN.count(
			finderCache, new Object[] {objectDefinitionId, dbTableName});
	}

	private CollectionPersistenceFinder<ObjectField, NoSuchObjectFieldException>
		_collectionPersistenceFinderByODI_I;

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_I(
		long objectDefinitionId, boolean indexed, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI_I.find(
			finderCache, new Object[] {objectDefinitionId, indexed}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_I_First(
			long objectDefinitionId, boolean indexed,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		return _collectionPersistenceFinderByODI_I.findFirst(
			finderCache, new Object[] {objectDefinitionId, indexed},
			orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_I_First(
		long objectDefinitionId, boolean indexed,
		OrderByComparator<ObjectField> orderByComparator) {

		return _collectionPersistenceFinderByODI_I.fetchFirst(
			finderCache, new Object[] {objectDefinitionId, indexed},
			orderByComparator);
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and indexed = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 */
	@Override
	public void removeByODI_I(long objectDefinitionId, boolean indexed) {
		_collectionPersistenceFinderByODI_I.remove(
			finderCache, new Object[] {objectDefinitionId, indexed});
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @return the number of matching object fields
	 */
	@Override
	public int countByODI_I(long objectDefinitionId, boolean indexed) {
		return _collectionPersistenceFinderByODI_I.count(
			finderCache, new Object[] {objectDefinitionId, indexed});
	}

	private CollectionPersistenceFinder<ObjectField, NoSuchObjectFieldException>
		_collectionPersistenceFinderByODI_L;

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_L(
		long objectDefinitionId, boolean localized, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI_L.find(
			finderCache, new Object[] {objectDefinitionId, localized}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_L_First(
			long objectDefinitionId, boolean localized,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		return _collectionPersistenceFinderByODI_L.findFirst(
			finderCache, new Object[] {objectDefinitionId, localized},
			orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_L_First(
		long objectDefinitionId, boolean localized,
		OrderByComparator<ObjectField> orderByComparator) {

		return _collectionPersistenceFinderByODI_L.fetchFirst(
			finderCache, new Object[] {objectDefinitionId, localized},
			orderByComparator);
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and localized = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 */
	@Override
	public void removeByODI_L(long objectDefinitionId, boolean localized) {
		_collectionPersistenceFinderByODI_L.remove(
			finderCache, new Object[] {objectDefinitionId, localized});
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @return the number of matching object fields
	 */
	@Override
	public int countByODI_L(long objectDefinitionId, boolean localized) {
		return _collectionPersistenceFinderByODI_L.count(
			finderCache, new Object[] {objectDefinitionId, localized});
	}

	private UniquePersistenceFinder<ObjectField, NoSuchObjectFieldException>
		_uniquePersistenceFinderByODI_N;

	/**
	 * Returns the object field where objectDefinitionId = &#63; and name = &#63; or throws a <code>NoSuchObjectFieldException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_N(long objectDefinitionId, String name)
		throws NoSuchObjectFieldException {

		return _uniquePersistenceFinderByODI_N.find(
			finderCache, new Object[] {objectDefinitionId, name});
	}

	/**
	 * Returns the object field where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_N(
		long objectDefinitionId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByODI_N.fetch(
			finderCache, new Object[] {objectDefinitionId, name},
			useFinderCache);
	}

	/**
	 * Removes the object field where objectDefinitionId = &#63; and name = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the object field that was removed
	 */
	@Override
	public ObjectField removeByODI_N(long objectDefinitionId, String name)
		throws NoSuchObjectFieldException {

		ObjectField objectField = findByODI_N(objectDefinitionId, name);

		return remove(objectField);
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the number of matching object fields
	 */
	@Override
	public int countByODI_N(long objectDefinitionId, String name) {
		return _uniquePersistenceFinderByODI_N.count(
			finderCache, new Object[] {objectDefinitionId, name});
	}

	private CollectionPersistenceFinder<ObjectField, NoSuchObjectFieldException>
		_collectionPersistenceFinderByODI_S;

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_S(
		long objectDefinitionId, boolean system, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI_S.find(
			finderCache, new Object[] {objectDefinitionId, system}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_S_First(
			long objectDefinitionId, boolean system,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		return _collectionPersistenceFinderByODI_S.findFirst(
			finderCache, new Object[] {objectDefinitionId, system},
			orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_S_First(
		long objectDefinitionId, boolean system,
		OrderByComparator<ObjectField> orderByComparator) {

		return _collectionPersistenceFinderByODI_S.fetchFirst(
			finderCache, new Object[] {objectDefinitionId, system},
			orderByComparator);
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and system = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 */
	@Override
	public void removeByODI_S(long objectDefinitionId, boolean system) {
		_collectionPersistenceFinderByODI_S.remove(
			finderCache, new Object[] {objectDefinitionId, system});
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @return the number of matching object fields
	 */
	@Override
	public int countByODI_S(long objectDefinitionId, boolean system) {
		return _collectionPersistenceFinderByODI_S.count(
			finderCache, new Object[] {objectDefinitionId, system});
	}

	private UniquePersistenceFinder<ObjectField, NoSuchObjectFieldException>
		_uniquePersistenceFinderByERC_C_ODI;

	/**
	 * Returns the object field where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or throws a <code>NoSuchObjectFieldException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectFieldException {

		return _uniquePersistenceFinderByERC_C_ODI.find(
			finderCache,
			new Object[] {
				externalReferenceCode, companyId, objectDefinitionId
			});
	}

	/**
	 * Returns the object field where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C_ODI.fetch(
			finderCache,
			new Object[] {externalReferenceCode, companyId, objectDefinitionId},
			useFinderCache);
	}

	/**
	 * Removes the object field where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the object field that was removed
	 */
	@Override
	public ObjectField removeByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectFieldException {

		ObjectField objectField = findByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);

		return remove(objectField);
	}

	/**
	 * Returns the number of object fields where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object fields
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

	private CollectionPersistenceFinder<ObjectField, NoSuchObjectFieldException>
		_collectionPersistenceFinderByODI_DBT_I;

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed, int start,
		int end, OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI_DBT_I.find(
			finderCache, new Object[] {objectDefinitionId, dbType, indexed},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_DBT_I_First(
			long objectDefinitionId, String dbType, boolean indexed,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		return _collectionPersistenceFinderByODI_DBT_I.findFirst(
			finderCache, new Object[] {objectDefinitionId, dbType, indexed},
			orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_DBT_I_First(
		long objectDefinitionId, String dbType, boolean indexed,
		OrderByComparator<ObjectField> orderByComparator) {

		return _collectionPersistenceFinderByODI_DBT_I.fetchFirst(
			finderCache, new Object[] {objectDefinitionId, dbType, indexed},
			orderByComparator);
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 */
	@Override
	public void removeByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed) {

		_collectionPersistenceFinderByODI_DBT_I.remove(
			finderCache, new Object[] {objectDefinitionId, dbType, indexed});
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @return the number of matching object fields
	 */
	@Override
	public int countByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed) {

		return _collectionPersistenceFinderByODI_DBT_I.count(
			finderCache, new Object[] {objectDefinitionId, dbType, indexed});
	}

	private CollectionPersistenceFinder<ObjectField, NoSuchObjectFieldException>
		_collectionPersistenceFinderByODI_L_S;

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system, int start,
		int end, OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI_L_S.find(
			finderCache, new Object[] {objectDefinitionId, localized, system},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_L_S_First(
			long objectDefinitionId, boolean localized, boolean system,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		return _collectionPersistenceFinderByODI_L_S.findFirst(
			finderCache, new Object[] {objectDefinitionId, localized, system},
			orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_L_S_First(
		long objectDefinitionId, boolean localized, boolean system,
		OrderByComparator<ObjectField> orderByComparator) {

		return _collectionPersistenceFinderByODI_L_S.fetchFirst(
			finderCache, new Object[] {objectDefinitionId, localized, system},
			orderByComparator);
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 */
	@Override
	public void removeByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system) {

		_collectionPersistenceFinderByODI_L_S.remove(
			finderCache, new Object[] {objectDefinitionId, localized, system});
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @return the number of matching object fields
	 */
	@Override
	public int countByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system) {

		return _collectionPersistenceFinderByODI_L_S.count(
			finderCache, new Object[] {objectDefinitionId, localized, system});
	}

	public ObjectFieldPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("state", "state_");
		dbColumnNames.put("system", "system_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectField.class);

		setModelImplClass(ObjectFieldImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectFieldTable.INSTANCE);
	}

	/**
	 * Creates a new object field with the primary key. Does not add the object field to the database.
	 *
	 * @param objectFieldId the primary key for the new object field
	 * @return the new object field
	 */
	@Override
	public ObjectField create(long objectFieldId) {
		ObjectField objectField = new ObjectFieldImpl();

		objectField.setNew(true);
		objectField.setPrimaryKey(objectFieldId);

		String uuid = PortalUUIDUtil.generate();

		objectField.setUuid(uuid);

		objectField.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectField;
	}

	/**
	 * Removes the object field with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectFieldId the primary key of the object field
	 * @return the object field that was removed
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField remove(long objectFieldId)
		throws NoSuchObjectFieldException {

		return remove((Serializable)objectFieldId);
	}

	@Override
	protected ObjectField removeImpl(ObjectField objectField) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectField)) {
				objectField = (ObjectField)session.get(
					ObjectFieldImpl.class, objectField.getPrimaryKeyObj());
			}

			if (objectField != null) {
				session.delete(objectField);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectField != null) {
			clearCache(objectField);
		}

		return objectField;
	}

	@Override
	public ObjectField updateImpl(ObjectField objectField) {
		boolean isNew = objectField.isNew();

		if (!(objectField instanceof ObjectFieldModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectField.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(objectField);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectField proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectField implementation " +
					objectField.getClass());
		}

		ObjectFieldModelImpl objectFieldModelImpl =
			(ObjectFieldModelImpl)objectField;

		if (Validator.isNull(objectField.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectField.setUuid(uuid);
		}

		if (Validator.isNull(objectField.getExternalReferenceCode())) {
			objectField.setExternalReferenceCode(objectField.getUuid());
		}
		else {
			if (!Objects.equals(
					objectFieldModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					objectField.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = objectField.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = objectField.getPrimaryKey();
					}

					try {
						objectField.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ObjectField.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								objectField.getExternalReferenceCode(), null));
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

		if (isNew && (objectField.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectField.setCreateDate(date);
			}
			else {
				objectField.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!objectFieldModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectField.setModifiedDate(date);
			}
			else {
				objectField.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectField);
			}
			else {
				objectField = (ObjectField)session.merge(objectField);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectField, false);

		if (isNew) {
			objectField.setNew(false);
		}

		objectField.resetOriginalValues();

		return objectField;
	}

	/**
	 * Returns the object field with the primary key or throws a <code>NoSuchObjectFieldException</code> if it could not be found.
	 *
	 * @param objectFieldId the primary key of the object field
	 * @return the object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField findByPrimaryKey(long objectFieldId)
		throws NoSuchObjectFieldException {

		return findByPrimaryKey((Serializable)objectFieldId);
	}

	/**
	 * Returns the object field with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectFieldId the primary key of the object field
	 * @return the object field, or <code>null</code> if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField fetchByPrimaryKey(long objectFieldId) {
		return fetchByPrimaryKey((Serializable)objectFieldId);
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
		return "objectFieldId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTFIELD;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectFieldModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object field persistence.
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
			_SQL_SELECT_OBJECTFIELD_WHERE, _SQL_COUNT_OBJECTFIELD_WHERE,
			ObjectFieldModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"objectField.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, ObjectField::getUuid));

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
				_SQL_SELECT_OBJECTFIELD_WHERE, _SQL_COUNT_OBJECTFIELD_WHERE,
				ObjectFieldModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectField.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, ObjectField::getUuid),
				new FinderColumn<>(
					"objectField.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, ObjectField::getCompanyId));

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
				_SQL_SELECT_OBJECTFIELD_WHERE, _SQL_COUNT_OBJECTFIELD_WHERE,
				ObjectFieldModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectField.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, ObjectField::getCompanyId));

		_collectionPersistenceFinderByListTypeDefinitionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByListTypeDefinitionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"listTypeDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByListTypeDefinitionId",
					new String[] {Long.class.getName()},
					new String[] {"listTypeDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByListTypeDefinitionId",
					new String[] {Long.class.getName()},
					new String[] {"listTypeDefinitionId"}, false),
				_SQL_SELECT_OBJECTFIELD_WHERE, _SQL_COUNT_OBJECTFIELD_WHERE,
				ObjectFieldModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectField.", "listTypeDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectField::getListTypeDefinitionId));

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
				_SQL_SELECT_OBJECTFIELD_WHERE, _SQL_COUNT_OBJECTFIELD_WHERE,
				ObjectFieldModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectField.", "objectDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectField::getObjectDefinitionId));

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
			_SQL_SELECT_OBJECTFIELD_WHERE, _SQL_COUNT_OBJECTFIELD_WHERE,
			ObjectFieldModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"objectField.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, ObjectField::getCompanyId),
			new FinderColumn<>(
				"objectField.", "userId", FinderColumn.Type.LONG, "=", true,
				true, ObjectField::getUserId));

		_collectionPersistenceFinderByC_BT = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_BT",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "businessType"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_BT",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "businessType"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_BT",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "businessType"}, 0, 2, false, null),
			_SQL_SELECT_OBJECTFIELD_WHERE, _SQL_COUNT_OBJECTFIELD_WHERE,
			ObjectFieldModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"objectField.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, ObjectField::getCompanyId),
			new FinderColumn<>(
				"objectField.", "businessType", FinderColumn.Type.STRING, "=",
				true, true, ObjectField::getBusinessType));

		_collectionPersistenceFinderByLTDI_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLTDI_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"listTypeDefinitionId", "state_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByLTDI_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"listTypeDefinitionId", "state_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByLTDI_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"listTypeDefinitionId", "state_"}, false),
				_SQL_SELECT_OBJECTFIELD_WHERE, _SQL_COUNT_OBJECTFIELD_WHERE,
				ObjectFieldModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectField.", "listTypeDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectField::getListTypeDefinitionId),
				new FinderColumn<>(
					"objectField.", "state", "state_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					ObjectField::isState));

		_collectionPersistenceFinderByODI_BT =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_BT",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectDefinitionId", "businessType"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI_BT",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"objectDefinitionId", "businessType"}, 0, 2,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI_BT",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"objectDefinitionId", "businessType"}, 0, 2,
					false, null),
				_SQL_SELECT_OBJECTFIELD_WHERE, _SQL_COUNT_OBJECTFIELD_WHERE,
				ObjectFieldModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectField.", "objectDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectField::getObjectDefinitionId),
				new FinderColumn<>(
					"objectField.", "businessType", FinderColumn.Type.STRING,
					"=", true, true, ObjectField::getBusinessType));

		_collectionPersistenceFinderByODI_DTN =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_DTN",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectDefinitionId", "dbTableName"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI_DTN",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"objectDefinitionId", "dbTableName"}, 0, 2,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI_DTN",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"objectDefinitionId", "dbTableName"}, 0, 2,
					false, null),
				_SQL_SELECT_OBJECTFIELD_WHERE, _SQL_COUNT_OBJECTFIELD_WHERE,
				ObjectFieldModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectField.", "objectDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectField::getObjectDefinitionId),
				new FinderColumn<>(
					"objectField.", "dbTableName", FinderColumn.Type.STRING,
					"=", true, true, ObjectField::getDBTableName));

		_collectionPersistenceFinderByODI_I = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_I",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"objectDefinitionId", "indexed"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI_I",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"objectDefinitionId", "indexed"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI_I",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"objectDefinitionId", "indexed"}, false),
			_SQL_SELECT_OBJECTFIELD_WHERE, _SQL_COUNT_OBJECTFIELD_WHERE,
			ObjectFieldModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"objectField.", "objectDefinitionId", FinderColumn.Type.LONG,
				"=", true, true, ObjectField::getObjectDefinitionId),
			new FinderColumn<>(
				"objectField.", "indexed", FinderColumn.Type.BOOLEAN, "=", true,
				true, ObjectField::isIndexed));

		_collectionPersistenceFinderByODI_L = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_L",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"objectDefinitionId", "localized"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI_L",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"objectDefinitionId", "localized"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI_L",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"objectDefinitionId", "localized"}, false),
			_SQL_SELECT_OBJECTFIELD_WHERE, _SQL_COUNT_OBJECTFIELD_WHERE,
			ObjectFieldModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"objectField.", "objectDefinitionId", FinderColumn.Type.LONG,
				"=", true, true, ObjectField::getObjectDefinitionId),
			new FinderColumn<>(
				"objectField.", "localized", FinderColumn.Type.BOOLEAN, "=",
				true, true, ObjectField::isLocalized));

		_uniquePersistenceFinderByODI_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByODI_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"objectDefinitionId", "name"}, 0, 2, false,
				ObjectField::getObjectDefinitionId,
				convertNullFunction(ObjectField::getName)),
			_SQL_SELECT_OBJECTFIELD_WHERE, "",
			new FinderColumn<>(
				"objectField.", "objectDefinitionId", FinderColumn.Type.LONG,
				"=", true, true, ObjectField::getObjectDefinitionId),
			new FinderColumn<>(
				"objectField.", "name", FinderColumn.Type.STRING, "=", true,
				true, ObjectField::getName));

		_collectionPersistenceFinderByODI_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_S",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"objectDefinitionId", "system_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI_S",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"objectDefinitionId", "system_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI_S",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"objectDefinitionId", "system_"}, false),
			_SQL_SELECT_OBJECTFIELD_WHERE, _SQL_COUNT_OBJECTFIELD_WHERE,
			ObjectFieldModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"objectField.", "objectDefinitionId", FinderColumn.Type.LONG,
				"=", true, true, ObjectField::getObjectDefinitionId),
			new FinderColumn<>(
				"objectField.", "system", "system_", FinderColumn.Type.BOOLEAN,
				"=", true, true, ObjectField::isSystem));

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
				convertNullFunction(ObjectField::getExternalReferenceCode),
				ObjectField::getCompanyId, ObjectField::getObjectDefinitionId),
			_SQL_SELECT_OBJECTFIELD_WHERE, "",
			new FinderColumn<>(
				"objectField.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectField::getExternalReferenceCode),
			new FinderColumn<>(
				"objectField.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, ObjectField::getCompanyId),
			new FinderColumn<>(
				"objectField.", "objectDefinitionId", FinderColumn.Type.LONG,
				"=", true, true, ObjectField::getObjectDefinitionId));

		_collectionPersistenceFinderByODI_DBT_I =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_DBT_I",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectDefinitionId", "dbType", "indexed"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByODI_DBT_I",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"objectDefinitionId", "dbType", "indexed"}, 0,
					2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByODI_DBT_I",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"objectDefinitionId", "dbType", "indexed"}, 0,
					2, false, null),
				_SQL_SELECT_OBJECTFIELD_WHERE, _SQL_COUNT_OBJECTFIELD_WHERE,
				ObjectFieldModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectField.", "objectDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectField::getObjectDefinitionId),
				new FinderColumn<>(
					"objectField.", "dbType", FinderColumn.Type.STRING, "=",
					true, true, ObjectField::getDBType),
				new FinderColumn<>(
					"objectField.", "indexed", FinderColumn.Type.BOOLEAN, "=",
					true, true, ObjectField::isIndexed));

		_collectionPersistenceFinderByODI_L_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_L_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectDefinitionId", "localized", "system_"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI_L_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"objectDefinitionId", "localized", "system_"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI_L_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"objectDefinitionId", "localized", "system_"},
					false),
				_SQL_SELECT_OBJECTFIELD_WHERE, _SQL_COUNT_OBJECTFIELD_WHERE,
				ObjectFieldModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"objectField.", "objectDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectField::getObjectDefinitionId),
				new FinderColumn<>(
					"objectField.", "localized", FinderColumn.Type.BOOLEAN, "=",
					true, true, ObjectField::isLocalized),
				new FinderColumn<>(
					"objectField.", "system", "system_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					ObjectField::isSystem));

		ObjectFieldUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectFieldUtil.setPersistence(null);

		entityCache.removeCache(ObjectFieldImpl.class.getName());
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
		ObjectFieldModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTFIELD =
		"SELECT objectField FROM ObjectField objectField";

	private static final String _SQL_SELECT_OBJECTFIELD_WHERE =
		"SELECT objectField FROM ObjectField objectField WHERE ";

	private static final String _SQL_COUNT_OBJECTFIELD_WHERE =
		"SELECT COUNT(objectField) FROM ObjectField objectField WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "state", "system"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1401100620