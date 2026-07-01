/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.DuplicateObjectDefinitionExternalReferenceCodeException;
import com.liferay.object.exception.NoSuchObjectDefinitionException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionTable;
import com.liferay.object.model.impl.ObjectDefinitionImpl;
import com.liferay.object.model.impl.ObjectDefinitionModelImpl;
import com.liferay.object.service.persistence.ObjectDefinitionPersistence;
import com.liferay.object.service.persistence.ObjectDefinitionUtil;
import com.liferay.object.service.persistence.impl.constants.ObjectPersistenceConstants;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
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
 * The persistence implementation for the object definition service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectDefinitionPersistence.class)
public class ObjectDefinitionPersistenceImpl
	extends BasePersistenceImpl
		<ObjectDefinition, NoSuchObjectDefinitionException>
	implements ObjectDefinitionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectDefinitionUtil</code> to access the object definition persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectDefinitionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<ObjectDefinition, NoSuchObjectDefinitionException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object definitions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	@Override
	public List<ObjectDefinition> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition findByUuid_First(
			String uuid, OrderByComparator<ObjectDefinition> orderByComparator)
		throws NoSuchObjectDefinitionException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition fetchByUuid_First(
		String uuid, OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	@Override
	public List<ObjectDefinition> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			finderCache, new Object[] {uuid}, start, end, orderByComparator);
	}

	/**
	 * Removes all the object definitions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object definitions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object definitions
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object definitions that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			finderCache, new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder
		<ObjectDefinition, NoSuchObjectDefinitionException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	@Override
	public List<ObjectDefinition> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws NoSuchObjectDefinitionException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	@Override
	public List<ObjectDefinition> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the object definitions where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object definitions
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object definitions that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			finderCache, new Object[] {uuid, companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<ObjectDefinition, NoSuchObjectDefinitionException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	@Override
	public List<ObjectDefinition> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition findByCompanyId_First(
			long companyId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws NoSuchObjectDefinitionException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition fetchByCompanyId_First(
		long companyId, OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	@Override
	public List<ObjectDefinition> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the object definitions where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of object definitions where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching object definitions
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching object definitions that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<ObjectDefinition, NoSuchObjectDefinitionException>
			_collectionPersistenceFinderByObjectFolderId;

	/**
	 * Returns an ordered range of all the object definitions where objectFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param objectFolderId the object folder ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	@Override
	public List<ObjectDefinition> findByObjectFolderId(
		long objectFolderId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectFolderId.find(
			finderCache, new Object[] {objectFolderId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition findByObjectFolderId_First(
			long objectFolderId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws NoSuchObjectDefinitionException {

		return _collectionPersistenceFinderByObjectFolderId.findFirst(
			finderCache, new Object[] {objectFolderId}, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition fetchByObjectFolderId_First(
		long objectFolderId,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByObjectFolderId.fetchFirst(
			finderCache, new Object[] {objectFolderId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where objectFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param objectFolderId the object folder ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	@Override
	public List<ObjectDefinition> filterFindByObjectFolderId(
		long objectFolderId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByObjectFolderId.filterFind(
			finderCache, new Object[] {objectFolderId}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the object definitions where objectFolderId = &#63; from the database.
	 *
	 * @param objectFolderId the object folder ID
	 */
	@Override
	public void removeByObjectFolderId(long objectFolderId) {
		_collectionPersistenceFinderByObjectFolderId.remove(
			finderCache, new Object[] {objectFolderId});
	}

	/**
	 * Returns the number of object definitions where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @return the number of matching object definitions
	 */
	@Override
	public int countByObjectFolderId(long objectFolderId) {
		return _collectionPersistenceFinderByObjectFolderId.count(
			finderCache, new Object[] {objectFolderId});
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @return the number of matching object definitions that the user has permission to view
	 */
	@Override
	public int filterCountByObjectFolderId(long objectFolderId) {
		return _collectionPersistenceFinderByObjectFolderId.filterCount(
			finderCache, new Object[] {objectFolderId});
	}

	private FilterCollectionPersistenceFinder
		<ObjectDefinition, NoSuchObjectDefinitionException>
			_collectionPersistenceFinderByAccountEntryRestricted;

	/**
	 * Returns an ordered range of all the object definitions where accountEntryRestricted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	@Override
	public List<ObjectDefinition> findByAccountEntryRestricted(
		boolean accountEntryRestricted, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAccountEntryRestricted.find(
			finderCache, new Object[] {accountEntryRestricted}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition findByAccountEntryRestricted_First(
			boolean accountEntryRestricted,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws NoSuchObjectDefinitionException {

		return _collectionPersistenceFinderByAccountEntryRestricted.findFirst(
			finderCache, new Object[] {accountEntryRestricted},
			orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition fetchByAccountEntryRestricted_First(
		boolean accountEntryRestricted,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByAccountEntryRestricted.fetchFirst(
			finderCache, new Object[] {accountEntryRestricted},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where accountEntryRestricted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	@Override
	public List<ObjectDefinition> filterFindByAccountEntryRestricted(
		boolean accountEntryRestricted, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByAccountEntryRestricted.filterFind(
			finderCache, new Object[] {accountEntryRestricted}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the object definitions where accountEntryRestricted = &#63; from the database.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 */
	@Override
	public void removeByAccountEntryRestricted(boolean accountEntryRestricted) {
		_collectionPersistenceFinderByAccountEntryRestricted.remove(
			finderCache, new Object[] {accountEntryRestricted});
	}

	/**
	 * Returns the number of object definitions where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @return the number of matching object definitions
	 */
	@Override
	public int countByAccountEntryRestricted(boolean accountEntryRestricted) {
		return _collectionPersistenceFinderByAccountEntryRestricted.count(
			finderCache, new Object[] {accountEntryRestricted});
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @return the number of matching object definitions that the user has permission to view
	 */
	@Override
	public int filterCountByAccountEntryRestricted(
		boolean accountEntryRestricted) {

		return _collectionPersistenceFinderByAccountEntryRestricted.filterCount(
			finderCache, new Object[] {accountEntryRestricted});
	}

	private UniquePersistenceFinder
		<ObjectDefinition, NoSuchObjectDefinitionException>
			_uniquePersistenceFinderByClassName;

	/**
	 * Returns the object definition where className = &#63; or throws a <code>NoSuchObjectDefinitionException</code> if it could not be found.
	 *
	 * @param className the class name
	 * @return the matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition findByClassName(String className)
		throws NoSuchObjectDefinitionException {

		return _uniquePersistenceFinderByClassName.find(
			finderCache, new Object[] {className});
	}

	/**
	 * Returns the object definition where className = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param className the class name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition fetchByClassName(
		String className, boolean useFinderCache) {

		return _uniquePersistenceFinderByClassName.fetch(
			finderCache, new Object[] {className}, useFinderCache);
	}

	/**
	 * Removes the object definition where className = &#63; from the database.
	 *
	 * @param className the class name
	 * @return the object definition that was removed
	 */
	@Override
	public ObjectDefinition removeByClassName(String className)
		throws NoSuchObjectDefinitionException {

		ObjectDefinition objectDefinition = findByClassName(className);

		return remove(objectDefinition);
	}

	/**
	 * Returns the number of object definitions where className = &#63;.
	 *
	 * @param className the class name
	 * @return the number of matching object definitions
	 */
	@Override
	public int countByClassName(String className) {
		return _uniquePersistenceFinderByClassName.count(
			finderCache, new Object[] {className});
	}

	private FilterCollectionPersistenceFinder
		<ObjectDefinition, NoSuchObjectDefinitionException>
			_collectionPersistenceFinderBySystem;

	/**
	 * Returns an ordered range of all the object definitions where system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	@Override
	public List<ObjectDefinition> findBySystem(
		boolean system, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySystem.find(
			finderCache, new Object[] {system}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where system = &#63;.
	 *
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition findBySystem_First(
			boolean system,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws NoSuchObjectDefinitionException {

		return _collectionPersistenceFinderBySystem.findFirst(
			finderCache, new Object[] {system}, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where system = &#63;.
	 *
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition fetchBySystem_First(
		boolean system, OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderBySystem.fetchFirst(
			finderCache, new Object[] {system}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	@Override
	public List<ObjectDefinition> filterFindBySystem(
		boolean system, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderBySystem.filterFind(
			finderCache, new Object[] {system}, start, end, orderByComparator);
	}

	/**
	 * Removes all the object definitions where system = &#63; from the database.
	 *
	 * @param system the system
	 */
	@Override
	public void removeBySystem(boolean system) {
		_collectionPersistenceFinderBySystem.remove(
			finderCache, new Object[] {system});
	}

	/**
	 * Returns the number of object definitions where system = &#63;.
	 *
	 * @param system the system
	 * @return the number of matching object definitions
	 */
	@Override
	public int countBySystem(boolean system) {
		return _collectionPersistenceFinderBySystem.count(
			finderCache, new Object[] {system});
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where system = &#63;.
	 *
	 * @param system the system
	 * @return the number of matching object definitions that the user has permission to view
	 */
	@Override
	public int filterCountBySystem(boolean system) {
		return _collectionPersistenceFinderBySystem.filterCount(
			finderCache, new Object[] {system});
	}

	private FilterCollectionPersistenceFinder
		<ObjectDefinition, NoSuchObjectDefinitionException>
			_collectionPersistenceFinderByC_U;

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	@Override
	public List<ObjectDefinition> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_U.find(
			finderCache, new Object[] {companyId, userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition findByC_U_First(
			long companyId, long userId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws NoSuchObjectDefinitionException {

		return _collectionPersistenceFinderByC_U.findFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_U.fetchFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	@Override
	public List<ObjectDefinition> filterFindByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_U.filterFind(
			finderCache, new Object[] {companyId, userId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the object definitions where companyId = &#63; and userId = &#63; from the database.
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
	 * Returns the number of object definitions where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching object definitions
	 */
	@Override
	public int countByC_U(long companyId, long userId) {
		return _collectionPersistenceFinderByC_U.count(
			finderCache, new Object[] {companyId, userId});
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching object definitions that the user has permission to view
	 */
	@Override
	public int filterCountByC_U(long companyId, long userId) {
		return _collectionPersistenceFinderByC_U.filterCount(
			finderCache, new Object[] {companyId, userId}, companyId, 0);
	}

	private UniquePersistenceFinder
		<ObjectDefinition, NoSuchObjectDefinitionException>
			_uniquePersistenceFinderByC_C;

	/**
	 * Returns the object definition where companyId = &#63; and className = &#63; or throws a <code>NoSuchObjectDefinitionException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @return the matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition findByC_C(long companyId, String className)
		throws NoSuchObjectDefinitionException {

		return _uniquePersistenceFinderByC_C.find(
			finderCache, new Object[] {companyId, className});
	}

	/**
	 * Returns the object definition where companyId = &#63; and className = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition fetchByC_C(
		long companyId, String className, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C.fetch(
			finderCache, new Object[] {companyId, className}, useFinderCache);
	}

	/**
	 * Removes the object definition where companyId = &#63; and className = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @return the object definition that was removed
	 */
	@Override
	public ObjectDefinition removeByC_C(long companyId, String className)
		throws NoSuchObjectDefinitionException {

		ObjectDefinition objectDefinition = findByC_C(companyId, className);

		return remove(objectDefinition);
	}

	/**
	 * Returns the number of object definitions where companyId = &#63; and className = &#63;.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @return the number of matching object definitions
	 */
	@Override
	public int countByC_C(long companyId, String className) {
		return _uniquePersistenceFinderByC_C.count(
			finderCache, new Object[] {companyId, className});
	}

	private UniquePersistenceFinder
		<ObjectDefinition, NoSuchObjectDefinitionException>
			_uniquePersistenceFinderByC_N;

	/**
	 * Returns the object definition where companyId = &#63; and name = &#63; or throws a <code>NoSuchObjectDefinitionException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition findByC_N(long companyId, String name)
		throws NoSuchObjectDefinitionException {

		return _uniquePersistenceFinderByC_N.find(
			finderCache, new Object[] {companyId, name});
	}

	/**
	 * Returns the object definition where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition fetchByC_N(
		long companyId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_N.fetch(
			finderCache, new Object[] {companyId, name}, useFinderCache);
	}

	/**
	 * Removes the object definition where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the object definition that was removed
	 */
	@Override
	public ObjectDefinition removeByC_N(long companyId, String name)
		throws NoSuchObjectDefinitionException {

		ObjectDefinition objectDefinition = findByC_N(companyId, name);

		return remove(objectDefinition);
	}

	/**
	 * Returns the number of object definitions where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching object definitions
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		return _uniquePersistenceFinderByC_N.count(
			finderCache, new Object[] {companyId, name});
	}

	private FilterCollectionPersistenceFinder
		<ObjectDefinition, NoSuchObjectDefinitionException>
			_collectionPersistenceFinderByC_S;

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	@Override
	public List<ObjectDefinition> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S.find(
			finderCache, new Object[] {companyId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition findByC_S_First(
			long companyId, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws NoSuchObjectDefinitionException {

		return _collectionPersistenceFinderByC_S.findFirst(
			finderCache, new Object[] {companyId, status}, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition fetchByC_S_First(
		long companyId, int status,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_S.fetchFirst(
			finderCache, new Object[] {companyId, status}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	@Override
	public List<ObjectDefinition> filterFindByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_S.filterFind(
			finderCache, new Object[] {companyId, status}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the object definitions where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_S(long companyId, int status) {
		_collectionPersistenceFinderByC_S.remove(
			finderCache, new Object[] {companyId, status});
	}

	/**
	 * Returns the number of object definitions where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching object definitions
	 */
	@Override
	public int countByC_S(long companyId, int status) {
		return _collectionPersistenceFinderByC_S.count(
			finderCache, new Object[] {companyId, status});
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching object definitions that the user has permission to view
	 */
	@Override
	public int filterCountByC_S(long companyId, int status) {
		return _collectionPersistenceFinderByC_S.filterCount(
			finderCache, new Object[] {companyId, status}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<ObjectDefinition, NoSuchObjectDefinitionException>
			_collectionPersistenceFinderByS_S;

	/**
	 * Returns an ordered range of all the object definitions where system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	@Override
	public List<ObjectDefinition> findByS_S(
		boolean system, int status, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByS_S.find(
			finderCache, new Object[] {system, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition findByS_S_First(
			boolean system, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws NoSuchObjectDefinitionException {

		return _collectionPersistenceFinderByS_S.findFirst(
			finderCache, new Object[] {system, status}, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition fetchByS_S_First(
		boolean system, int status,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByS_S.fetchFirst(
			finderCache, new Object[] {system, status}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	@Override
	public List<ObjectDefinition> filterFindByS_S(
		boolean system, int status, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByS_S.filterFind(
			finderCache, new Object[] {system, status}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the object definitions where system = &#63; and status = &#63; from the database.
	 *
	 * @param system the system
	 * @param status the status
	 */
	@Override
	public void removeByS_S(boolean system, int status) {
		_collectionPersistenceFinderByS_S.remove(
			finderCache, new Object[] {system, status});
	}

	/**
	 * Returns the number of object definitions where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @return the number of matching object definitions
	 */
	@Override
	public int countByS_S(boolean system, int status) {
		return _collectionPersistenceFinderByS_S.count(
			finderCache, new Object[] {system, status});
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @return the number of matching object definitions that the user has permission to view
	 */
	@Override
	public int filterCountByS_S(boolean system, int status) {
		return _collectionPersistenceFinderByS_S.filterCount(
			finderCache, new Object[] {system, status});
	}

	private FilterCollectionPersistenceFinder
		<ObjectDefinition, NoSuchObjectDefinitionException>
			_collectionPersistenceFinderByC_A_S;

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	@Override
	public List<ObjectDefinition> findByC_A_S(
		long companyId, boolean active, int status, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A_S.find(
			finderCache, new Object[] {companyId, active, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition findByC_A_S_First(
			long companyId, boolean active, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws NoSuchObjectDefinitionException {

		return _collectionPersistenceFinderByC_A_S.findFirst(
			finderCache, new Object[] {companyId, active, status},
			orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition fetchByC_A_S_First(
		long companyId, boolean active, int status,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_A_S.fetchFirst(
			finderCache, new Object[] {companyId, active, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	@Override
	public List<ObjectDefinition> filterFindByC_A_S(
		long companyId, boolean active, int status, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_A_S.filterFind(
			finderCache, new Object[] {companyId, active, status}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the object definitions where companyId = &#63; and active = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 */
	@Override
	public void removeByC_A_S(long companyId, boolean active, int status) {
		_collectionPersistenceFinderByC_A_S.remove(
			finderCache, new Object[] {companyId, active, status});
	}

	/**
	 * Returns the number of object definitions where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @return the number of matching object definitions
	 */
	@Override
	public int countByC_A_S(long companyId, boolean active, int status) {
		return _collectionPersistenceFinderByC_A_S.count(
			finderCache, new Object[] {companyId, active, status});
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @return the number of matching object definitions that the user has permission to view
	 */
	@Override
	public int filterCountByC_A_S(long companyId, boolean active, int status) {
		return _collectionPersistenceFinderByC_A_S.filterCount(
			finderCache, new Object[] {companyId, active, status}, companyId,
			0);
	}

	private FilterCollectionPersistenceFinder
		<ObjectDefinition, NoSuchObjectDefinitionException>
			_collectionPersistenceFinderByC_M_S;

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	@Override
	public List<ObjectDefinition> findByC_M_S(
		long companyId, boolean modifiable, boolean system, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_M_S.find(
			finderCache, new Object[] {companyId, modifiable, system}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition findByC_M_S_First(
			long companyId, boolean modifiable, boolean system,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws NoSuchObjectDefinitionException {

		return _collectionPersistenceFinderByC_M_S.findFirst(
			finderCache, new Object[] {companyId, modifiable, system},
			orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition fetchByC_M_S_First(
		long companyId, boolean modifiable, boolean system,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_M_S.fetchFirst(
			finderCache, new Object[] {companyId, modifiable, system},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	@Override
	public List<ObjectDefinition> filterFindByC_M_S(
		long companyId, boolean modifiable, boolean system, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_M_S.filterFind(
			finderCache, new Object[] {companyId, modifiable, system}, start,
			end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the object definitions where companyId = &#63; and modifiable = &#63; and system = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 */
	@Override
	public void removeByC_M_S(
		long companyId, boolean modifiable, boolean system) {

		_collectionPersistenceFinderByC_M_S.remove(
			finderCache, new Object[] {companyId, modifiable, system});
	}

	/**
	 * Returns the number of object definitions where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @return the number of matching object definitions
	 */
	@Override
	public int countByC_M_S(
		long companyId, boolean modifiable, boolean system) {

		return _collectionPersistenceFinderByC_M_S.count(
			finderCache, new Object[] {companyId, modifiable, system});
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @return the number of matching object definitions that the user has permission to view
	 */
	@Override
	public int filterCountByC_M_S(
		long companyId, boolean modifiable, boolean system) {

		return _collectionPersistenceFinderByC_M_S.filterCount(
			finderCache, new Object[] {companyId, modifiable, system},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<ObjectDefinition, NoSuchObjectDefinitionException>
			_collectionPersistenceFinderByC_A_S_S;

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	@Override
	public List<ObjectDefinition> findByC_A_S_S(
		long companyId, boolean active, boolean system, int status, int start,
		int end, OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A_S_S.find(
			finderCache, new Object[] {companyId, active, system, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition findByC_A_S_S_First(
			long companyId, boolean active, boolean system, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws NoSuchObjectDefinitionException {

		return _collectionPersistenceFinderByC_A_S_S.findFirst(
			finderCache, new Object[] {companyId, active, system, status},
			orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition fetchByC_A_S_S_First(
		long companyId, boolean active, boolean system, int status,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_A_S_S.fetchFirst(
			finderCache, new Object[] {companyId, active, system, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	@Override
	public List<ObjectDefinition> filterFindByC_A_S_S(
		long companyId, boolean active, boolean system, int status, int start,
		int end, OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_A_S_S.filterFind(
			finderCache, new Object[] {companyId, active, system, status},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the object definitions where companyId = &#63; and active = &#63; and system = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 */
	@Override
	public void removeByC_A_S_S(
		long companyId, boolean active, boolean system, int status) {

		_collectionPersistenceFinderByC_A_S_S.remove(
			finderCache, new Object[] {companyId, active, system, status});
	}

	/**
	 * Returns the number of object definitions where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @return the number of matching object definitions
	 */
	@Override
	public int countByC_A_S_S(
		long companyId, boolean active, boolean system, int status) {

		return _collectionPersistenceFinderByC_A_S_S.count(
			finderCache, new Object[] {companyId, active, system, status});
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @return the number of matching object definitions that the user has permission to view
	 */
	@Override
	public int filterCountByC_A_S_S(
		long companyId, boolean active, boolean system, int status) {

		return _collectionPersistenceFinderByC_A_S_S.filterCount(
			finderCache, new Object[] {companyId, active, system, status},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<ObjectDefinition, NoSuchObjectDefinitionException>
			_collectionPersistenceFinderByC_OFI_A_E_S_S;

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	@Override
	public List<ObjectDefinition> findByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end, OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_OFI_A_E_S_S.find(
			finderCache,
			new Object[] {
				companyId, new long[] {objectFolderId}, active,
				enableObjectEntryDraft, scope, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition findByC_OFI_A_E_S_S_First(
			long companyId, long objectFolderId, boolean active,
			boolean enableObjectEntryDraft, String scope, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws NoSuchObjectDefinitionException {

		ObjectDefinition objectDefinition = fetchByC_OFI_A_E_S_S_First(
			companyId, objectFolderId, active, enableObjectEntryDraft, scope,
			status, orderByComparator);

		if (objectDefinition != null) {
			return objectDefinition;
		}

		StringBundler sb = new StringBundler(14);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", objectFolderId=");
		sb.append(objectFolderId);

		sb.append(", active=");
		sb.append(active);

		sb.append(", enableObjectEntryDraft=");
		sb.append(enableObjectEntryDraft);

		sb.append(", scope=");
		sb.append(scope);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchObjectDefinitionException(sb.toString());
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition fetchByC_OFI_A_E_S_S_First(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_OFI_A_E_S_S.fetchFirst(
			finderCache,
			new Object[] {
				companyId, new long[] {objectFolderId}, active,
				enableObjectEntryDraft, scope, status
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	@Override
	public List<ObjectDefinition> filterFindByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end, OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_OFI_A_E_S_S.filterFind(
			finderCache,
			new Object[] {
				companyId, new long[] {objectFolderId}, active,
				enableObjectEntryDraft, scope, status
			},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permission to view where companyId = &#63; and objectFolderId = any &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	@Override
	public List<ObjectDefinition> filterFindByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end, OrderByComparator<ObjectDefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_OFI_A_E_S_S.filterFind(
			finderCache,
			new Object[] {
				companyId, ArrayUtil.sortedUnique(objectFolderIds), active,
				enableObjectEntryDraft, scope, status
			},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	@Override
	public List<ObjectDefinition> findByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end, OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_OFI_A_E_S_S.find(
			finderCache,
			new Object[] {
				companyId, ArrayUtil.sortedUnique(objectFolderIds), active,
				enableObjectEntryDraft, scope, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the object definitions where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 */
	@Override
	public void removeByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status) {

		_collectionPersistenceFinderByC_OFI_A_E_S_S.remove(
			finderCache,
			new Object[] {
				companyId, new long[] {objectFolderId}, active,
				enableObjectEntryDraft, scope, status
			});
	}

	/**
	 * Returns the number of object definitions where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the number of matching object definitions
	 */
	@Override
	public int countByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status) {

		return _collectionPersistenceFinderByC_OFI_A_E_S_S.count(
			finderCache,
			new Object[] {
				companyId, new long[] {objectFolderId}, active,
				enableObjectEntryDraft, scope, status
			});
	}

	/**
	 * Returns the number of object definitions where companyId = &#63; and objectFolderId = any &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the number of matching object definitions
	 */
	@Override
	public int countByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status) {

		return _collectionPersistenceFinderByC_OFI_A_E_S_S.count(
			finderCache,
			new Object[] {
				companyId, ArrayUtil.sortedUnique(objectFolderIds), active,
				enableObjectEntryDraft, scope, status
			});
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the number of matching object definitions that the user has permission to view
	 */
	@Override
	public int filterCountByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status) {

		return _collectionPersistenceFinderByC_OFI_A_E_S_S.filterCount(
			finderCache,
			new Object[] {
				companyId, new long[] {objectFolderId}, active,
				enableObjectEntryDraft, scope, status
			},
			companyId, 0);
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and objectFolderId = any &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the number of matching object definitions that the user has permission to view
	 */
	@Override
	public int filterCountByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status) {

		return _collectionPersistenceFinderByC_OFI_A_E_S_S.filterCount(
			finderCache,
			new Object[] {
				companyId, ArrayUtil.sortedUnique(objectFolderIds), active,
				enableObjectEntryDraft, scope, status
			},
			companyId, 0);
	}

	private UniquePersistenceFinder
		<ObjectDefinition, NoSuchObjectDefinitionException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the object definition where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchObjectDefinitionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchObjectDefinitionException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the object definition where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	@Override
	public ObjectDefinition fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the object definition where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the object definition that was removed
	 */
	@Override
	public ObjectDefinition removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchObjectDefinitionException {

		ObjectDefinition objectDefinition = findByERC_C(
			externalReferenceCode, companyId);

		return remove(objectDefinition);
	}

	/**
	 * Returns the number of object definitions where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching object definitions
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public ObjectDefinitionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"accountEntryRestrictedObjectFieldId", "accountERObjectFieldId");
		dbColumnNames.put("active", "active_");
		dbColumnNames.put("system", "system_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectDefinition.class);

		setModelImplClass(ObjectDefinitionImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectDefinitionTable.INSTANCE);
	}

	/**
	 * Creates a new object definition with the primary key. Does not add the object definition to the database.
	 *
	 * @param objectDefinitionId the primary key for the new object definition
	 * @return the new object definition
	 */
	@Override
	public ObjectDefinition create(long objectDefinitionId) {
		ObjectDefinition objectDefinition = new ObjectDefinitionImpl();

		objectDefinition.setNew(true);
		objectDefinition.setPrimaryKey(objectDefinitionId);

		String uuid = PortalUUIDUtil.generate();

		objectDefinition.setUuid(uuid);

		objectDefinition.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectDefinition;
	}

	/**
	 * Removes the object definition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectDefinitionId the primary key of the object definition
	 * @return the object definition that was removed
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	@Override
	public ObjectDefinition remove(long objectDefinitionId)
		throws NoSuchObjectDefinitionException {

		return remove((Serializable)objectDefinitionId);
	}

	@Override
	protected ObjectDefinition removeImpl(ObjectDefinition objectDefinition) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectDefinition)) {
				objectDefinition = (ObjectDefinition)session.get(
					ObjectDefinitionImpl.class,
					objectDefinition.getPrimaryKeyObj());
			}

			if (objectDefinition != null) {
				session.delete(objectDefinition);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectDefinition != null) {
			clearCache(objectDefinition);
		}

		return objectDefinition;
	}

	@Override
	public ObjectDefinition updateImpl(ObjectDefinition objectDefinition) {
		boolean isNew = objectDefinition.isNew();

		if (!(objectDefinition instanceof ObjectDefinitionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectDefinition.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectDefinition);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectDefinition proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectDefinition implementation " +
					objectDefinition.getClass());
		}

		ObjectDefinitionModelImpl objectDefinitionModelImpl =
			(ObjectDefinitionModelImpl)objectDefinition;

		if (Validator.isNull(objectDefinition.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectDefinition.setUuid(uuid);
		}

		if (Validator.isNull(objectDefinition.getExternalReferenceCode())) {
			objectDefinition.setExternalReferenceCode(
				objectDefinition.getUuid());
		}
		else {
			if (!Objects.equals(
					objectDefinitionModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					objectDefinition.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = objectDefinition.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = objectDefinition.getPrimaryKey();
					}

					try {
						objectDefinition.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ObjectDefinition.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								objectDefinition.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			ObjectDefinition ercObjectDefinition = fetchByERC_C(
				objectDefinition.getExternalReferenceCode(),
				objectDefinition.getCompanyId());

			if (isNew) {
				if (ercObjectDefinition != null) {
					throw new DuplicateObjectDefinitionExternalReferenceCodeException(
						"Duplicate object definition with external reference code " +
							objectDefinition.getExternalReferenceCode() +
								" and company " +
									objectDefinition.getCompanyId());
				}
			}
			else {
				if ((ercObjectDefinition != null) &&
					(objectDefinition.getObjectDefinitionId() !=
						ercObjectDefinition.getObjectDefinitionId())) {

					throw new DuplicateObjectDefinitionExternalReferenceCodeException(
						"Duplicate object definition with external reference code " +
							objectDefinition.getExternalReferenceCode() +
								" and company " +
									objectDefinition.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectDefinition.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectDefinition.setCreateDate(date);
			}
			else {
				objectDefinition.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectDefinitionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectDefinition.setModifiedDate(date);
			}
			else {
				objectDefinition.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectDefinition);
			}
			else {
				objectDefinition = (ObjectDefinition)session.merge(
					objectDefinition);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectDefinition, false);

		if (isNew) {
			objectDefinition.setNew(false);
		}

		objectDefinition.resetOriginalValues();

		return objectDefinition;
	}

	/**
	 * Returns the object definition with the primary key or throws a <code>NoSuchObjectDefinitionException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the primary key of the object definition
	 * @return the object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	@Override
	public ObjectDefinition findByPrimaryKey(long objectDefinitionId)
		throws NoSuchObjectDefinitionException {

		return findByPrimaryKey((Serializable)objectDefinitionId);
	}

	/**
	 * Returns the object definition with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectDefinitionId the primary key of the object definition
	 * @return the object definition, or <code>null</code> if a object definition with the primary key could not be found
	 */
	@Override
	public ObjectDefinition fetchByPrimaryKey(long objectDefinitionId) {
		return fetchByPrimaryKey((Serializable)objectDefinitionId);
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
		return "objectDefinitionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTDEFINITION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectDefinitionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object definition persistence.
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
				_SQL_SELECT_OBJECTDEFINITION_WHERE,
				_SQL_COUNT_OBJECTDEFINITION_WHERE,
				ObjectDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectDefinition.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectDefinition::getUuid));

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
				_SQL_SELECT_OBJECTDEFINITION_WHERE,
				_SQL_COUNT_OBJECTDEFINITION_WHERE,
				ObjectDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectDefinition.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectDefinition::getUuid),
				new FinderColumn<>(
					"objectDefinition.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectDefinition::getCompanyId));

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
				_SQL_SELECT_OBJECTDEFINITION_WHERE,
				_SQL_COUNT_OBJECTDEFINITION_WHERE,
				ObjectDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectDefinition.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectDefinition::getCompanyId));

		_collectionPersistenceFinderByObjectFolderId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByObjectFolderId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectFolderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByObjectFolderId", new String[] {Long.class.getName()},
					new String[] {"objectFolderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByObjectFolderId",
					new String[] {Long.class.getName()},
					new String[] {"objectFolderId"}, false),
				_SQL_SELECT_OBJECTDEFINITION_WHERE,
				_SQL_COUNT_OBJECTDEFINITION_WHERE,
				ObjectDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectDefinition.", "objectFolderId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectDefinition::getObjectFolderId));

		_collectionPersistenceFinderByAccountEntryRestricted =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByAccountEntryRestricted",
					new String[] {
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"accountEntryRestricted"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByAccountEntryRestricted",
					new String[] {Boolean.class.getName()},
					new String[] {"accountEntryRestricted"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByAccountEntryRestricted",
					new String[] {Boolean.class.getName()},
					new String[] {"accountEntryRestricted"}, false),
				_SQL_SELECT_OBJECTDEFINITION_WHERE,
				_SQL_COUNT_OBJECTDEFINITION_WHERE,
				ObjectDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectDefinition.", "accountEntryRestricted",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					ObjectDefinition::isAccountEntryRestricted));

		_uniquePersistenceFinderByClassName = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByClassName",
				new String[] {String.class.getName()},
				new String[] {"className"}, 0, 1, false,
				convertNullFunction(ObjectDefinition::getClassName)),
			_SQL_SELECT_OBJECTDEFINITION_WHERE, "",
			new FinderColumn<>(
				"objectDefinition.", "className", FinderColumn.Type.STRING, "=",
				true, true, ObjectDefinition::getClassName));

		_collectionPersistenceFinderBySystem =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findBySystem",
					new String[] {
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"system_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findBySystem",
					new String[] {Boolean.class.getName()},
					new String[] {"system_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countBySystem",
					new String[] {Boolean.class.getName()},
					new String[] {"system_"}, false),
				_SQL_SELECT_OBJECTDEFINITION_WHERE,
				_SQL_COUNT_OBJECTDEFINITION_WHERE,
				ObjectDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectDefinition.", "system", "system_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					ObjectDefinition::isSystem));

		_collectionPersistenceFinderByC_U =
			new FilterCollectionPersistenceFinder<>(
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
				_SQL_SELECT_OBJECTDEFINITION_WHERE,
				_SQL_COUNT_OBJECTDEFINITION_WHERE,
				ObjectDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectDefinition.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectDefinition::getCompanyId),
				new FinderColumn<>(
					"objectDefinition.", "userId", FinderColumn.Type.LONG, "=",
					true, true, ObjectDefinition::getUserId));

		_uniquePersistenceFinderByC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "className"}, 0, 2, false,
				ObjectDefinition::getCompanyId,
				convertNullFunction(ObjectDefinition::getClassName)),
			_SQL_SELECT_OBJECTDEFINITION_WHERE, "",
			new FinderColumn<>(
				"objectDefinition.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, ObjectDefinition::getCompanyId),
			new FinderColumn<>(
				"objectDefinition.", "className", FinderColumn.Type.STRING, "=",
				true, true, ObjectDefinition::getClassName));

		_uniquePersistenceFinderByC_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "name"}, 0, 2, true,
				ObjectDefinition::getCompanyId,
				convertNullFunction(ObjectDefinition::getName)),
			_SQL_SELECT_OBJECTDEFINITION_WHERE, "",
			new FinderColumn<>(
				"objectDefinition.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, ObjectDefinition::getCompanyId),
			new FinderColumn<>(
				"objectDefinition.", "name", FinderColumn.Type.STRING, "=",
				true, true, ObjectDefinition::getName));

		_collectionPersistenceFinderByC_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"companyId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"companyId", "status"}, false),
				_SQL_SELECT_OBJECTDEFINITION_WHERE,
				_SQL_COUNT_OBJECTDEFINITION_WHERE,
				ObjectDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectDefinition.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectDefinition::getCompanyId),
				new FinderColumn<>(
					"objectDefinition.", "status", FinderColumn.Type.INTEGER,
					"=", true, true, ObjectDefinition::getStatus));

		_collectionPersistenceFinderByS_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByS_S",
					new String[] {
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"system_", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByS_S",
					new String[] {
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {"system_", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByS_S",
					new String[] {
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {"system_", "status"}, false),
				_SQL_SELECT_OBJECTDEFINITION_WHERE,
				_SQL_COUNT_OBJECTDEFINITION_WHERE,
				ObjectDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectDefinition.", "system", "system_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					ObjectDefinition::isSystem),
				new FinderColumn<>(
					"objectDefinition.", "status", FinderColumn.Type.INTEGER,
					"=", true, true, ObjectDefinition::getStatus));

		_collectionPersistenceFinderByC_A_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "active_", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {"companyId", "active_", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {"companyId", "active_", "status"}, false),
				_SQL_SELECT_OBJECTDEFINITION_WHERE,
				_SQL_COUNT_OBJECTDEFINITION_WHERE,
				ObjectDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectDefinition.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectDefinition::getCompanyId),
				new FinderColumn<>(
					"objectDefinition.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					ObjectDefinition::isActive),
				new FinderColumn<>(
					"objectDefinition.", "status", FinderColumn.Type.INTEGER,
					"=", true, true, ObjectDefinition::getStatus));

		_collectionPersistenceFinderByC_M_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_M_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "modifiable", "system_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_M_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"companyId", "modifiable", "system_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_M_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"companyId", "modifiable", "system_"}, false),
				_SQL_SELECT_OBJECTDEFINITION_WHERE,
				_SQL_COUNT_OBJECTDEFINITION_WHERE,
				ObjectDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectDefinition.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectDefinition::getCompanyId),
				new FinderColumn<>(
					"objectDefinition.", "modifiable",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					ObjectDefinition::isModifiable),
				new FinderColumn<>(
					"objectDefinition.", "system", "system_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					ObjectDefinition::isSystem));

		_collectionPersistenceFinderByC_A_S_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A_S_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "active_", "system_", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A_S_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {"companyId", "active_", "system_", "status"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A_S_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {"companyId", "active_", "system_", "status"},
					false),
				_SQL_SELECT_OBJECTDEFINITION_WHERE,
				_SQL_COUNT_OBJECTDEFINITION_WHERE,
				ObjectDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectDefinition.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectDefinition::getCompanyId),
				new FinderColumn<>(
					"objectDefinition.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					ObjectDefinition::isActive),
				new FinderColumn<>(
					"objectDefinition.", "system", "system_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					ObjectDefinition::isSystem),
				new FinderColumn<>(
					"objectDefinition.", "status", FinderColumn.Type.INTEGER,
					"=", true, true, ObjectDefinition::getStatus));

		_collectionPersistenceFinderByC_OFI_A_E_S_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByC_OFI_A_E_S_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "objectFolderId", "active_",
						"enableObjectEntryDraft", "scope", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByC_OFI_A_E_S_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {
						"companyId", "objectFolderId", "active_",
						"enableObjectEntryDraft", "scope", "status"
					},
					0, 16, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByC_OFI_A_E_S_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {
						"companyId", "objectFolderId", "active_",
						"enableObjectEntryDraft", "scope", "status"
					},
					0, 16, false, null),
				_SQL_SELECT_OBJECTDEFINITION_WHERE,
				_SQL_COUNT_OBJECTDEFINITION_WHERE,
				ObjectDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"objectDefinition.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ObjectDefinition::getCompanyId),
				new ArrayableFinderColumn<>(
					"objectDefinition.", "objectFolderId",
					FinderColumn.Type.LONG, "=", false, true, true,
					ObjectDefinition::getObjectFolderId),
				new FinderColumn<>(
					"objectDefinition.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					ObjectDefinition::isActive),
				new FinderColumn<>(
					"objectDefinition.", "enableObjectEntryDraft",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					ObjectDefinition::isEnableObjectEntryDraft),
				new FinderColumn<>(
					"objectDefinition.", "scope", FinderColumn.Type.STRING, "=",
					true, true, ObjectDefinition::getScope),
				new FinderColumn<>(
					"objectDefinition.", "status", FinderColumn.Type.INTEGER,
					"=", true, true, ObjectDefinition::getStatus));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(ObjectDefinition::getExternalReferenceCode),
				ObjectDefinition::getCompanyId),
			_SQL_SELECT_OBJECTDEFINITION_WHERE, "",
			new FinderColumn<>(
				"objectDefinition.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectDefinition::getExternalReferenceCode),
			new FinderColumn<>(
				"objectDefinition.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, ObjectDefinition::getCompanyId));

		ObjectDefinitionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectDefinitionUtil.setPersistence(null);

		entityCache.removeCache(ObjectDefinitionImpl.class.getName());
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
		ObjectDefinitionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTDEFINITION =
		"SELECT objectDefinition FROM ObjectDefinition objectDefinition";

	private static final String _SQL_SELECT_OBJECTDEFINITION_WHERE =
		"SELECT objectDefinition FROM ObjectDefinition objectDefinition WHERE ";

	private static final String _SQL_COUNT_OBJECTDEFINITION_WHERE =
		"SELECT COUNT(objectDefinition) FROM ObjectDefinition objectDefinition WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ObjectDefinition exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectDefinitionPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {
			"uuid", "accountEntryRestrictedObjectFieldId", "active", "system"
		});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1308342332