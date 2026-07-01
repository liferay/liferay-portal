/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.impl;

import com.liferay.commerce.exception.DuplicateCommerceShipmentExternalReferenceCodeException;
import com.liferay.commerce.exception.NoSuchShipmentException;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShipmentTable;
import com.liferay.commerce.model.impl.CommerceShipmentImpl;
import com.liferay.commerce.model.impl.CommerceShipmentModelImpl;
import com.liferay.commerce.service.persistence.CommerceShipmentPersistence;
import com.liferay.commerce.service.persistence.CommerceShipmentUtil;
import com.liferay.commerce.service.persistence.impl.constants.CommercePersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
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
 * The persistence implementation for the commerce shipment service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceShipmentPersistence.class)
public class CommerceShipmentPersistenceImpl
	extends BasePersistenceImpl<CommerceShipment, NoSuchShipmentException>
	implements CommerceShipmentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceShipmentUtil</code> to access the commerce shipment persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceShipmentImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceShipment, NoSuchShipmentException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce shipments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce shipments
	 * @param end the upper bound of the range of commerce shipments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipments
	 */
	@Override
	public List<CommerceShipment> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce shipment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment
	 * @throws NoSuchShipmentException if a matching commerce shipment could not be found
	 */
	@Override
	public CommerceShipment findByUuid_First(
			String uuid, OrderByComparator<CommerceShipment> orderByComparator)
		throws NoSuchShipmentException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce shipment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment, or <code>null</code> if a matching commerce shipment could not be found
	 */
	@Override
	public CommerceShipment fetchByUuid_First(
		String uuid, OrderByComparator<CommerceShipment> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce shipments where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce shipments where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce shipments
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<CommerceShipment, NoSuchShipmentException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the commerce shipment where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchShipmentException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce shipment
	 * @throws NoSuchShipmentException if a matching commerce shipment could not be found
	 */
	@Override
	public CommerceShipment findByUUID_G(String uuid, long groupId)
		throws NoSuchShipmentException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the commerce shipment where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce shipment, or <code>null</code> if a matching commerce shipment could not be found
	 */
	@Override
	public CommerceShipment fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the commerce shipment where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce shipment that was removed
	 */
	@Override
	public CommerceShipment removeByUUID_G(String uuid, long groupId)
		throws NoSuchShipmentException {

		CommerceShipment commerceShipment = findByUUID_G(uuid, groupId);

		return remove(commerceShipment);
	}

	/**
	 * Returns the number of commerce shipments where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce shipments
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CommerceShipment, NoSuchShipmentException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce shipments where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce shipments
	 * @param end the upper bound of the range of commerce shipments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipments
	 */
	@Override
	public List<CommerceShipment> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment
	 * @throws NoSuchShipmentException if a matching commerce shipment could not be found
	 */
	@Override
	public CommerceShipment findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceShipment> orderByComparator)
		throws NoSuchShipmentException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce shipment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment, or <code>null</code> if a matching commerce shipment could not be found
	 */
	@Override
	public CommerceShipment fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceShipment> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce shipments where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce shipments where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce shipments
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<CommerceShipment, NoSuchShipmentException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the commerce shipments where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce shipments
	 * @param end the upper bound of the range of commerce shipments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipments
	 */
	@Override
	public List<CommerceShipment> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {new long[] {groupId}}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipment in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment
	 * @throws NoSuchShipmentException if a matching commerce shipment could not be found
	 */
	@Override
	public CommerceShipment findByGroupId_First(
			long groupId, OrderByComparator<CommerceShipment> orderByComparator)
		throws NoSuchShipmentException {

		CommerceShipment commerceShipment = fetchByGroupId_First(
			groupId, orderByComparator);

		if (commerceShipment != null) {
			return commerceShipment;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchShipmentException(sb.toString());
	}

	/**
	 * Returns the first commerce shipment in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment, or <code>null</code> if a matching commerce shipment could not be found
	 */
	@Override
	public CommerceShipment fetchByGroupId_First(
		long groupId, OrderByComparator<CommerceShipment> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce shipments that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce shipments
	 * @param end the upper bound of the range of commerce shipments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipments that the user has permission to view
	 */
	@Override
	public List<CommerceShipment> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {new long[] {groupId}}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the commerce shipments that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of commerce shipments
	 * @param end the upper bound of the range of commerce shipments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipments that the user has permission to view
	 */
	@Override
	public List<CommerceShipment> filterFindByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupIds}, start, end, orderByComparator,
			groupIds);
	}

	/**
	 * Returns an ordered range of all the commerce shipments where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of commerce shipments
	 * @param end the upper bound of the range of commerce shipments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipments
	 */
	@Override
	public List<CommerceShipment> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds)}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the commerce shipments where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of commerce shipments where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce shipments
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of commerce shipments where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching commerce shipments
	 */
	@Override
	public int countByGroupId(long[] groupIds) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds)});
	}

	/**
	 * Returns the number of commerce shipments that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce shipments that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {new long[] {groupId}}, groupId);
	}

	/**
	 * Returns the number of commerce shipments that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching commerce shipments that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long[] groupIds) {
		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupIds}, groupIds);
	}

	private FilterCollectionPersistenceFinder
		<CommerceShipment, NoSuchShipmentException>
			_collectionPersistenceFinderByG_C;

	/**
	 * Returns an ordered range of all the commerce shipments where groupId = &#63; and commerceAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAddressId the commerce address ID
	 * @param start the lower bound of the range of commerce shipments
	 * @param end the upper bound of the range of commerce shipments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipments
	 */
	@Override
	public List<CommerceShipment> findByG_C(
		long groupId, long commerceAddressId, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C.find(
			finderCache, new Object[] {new long[] {groupId}, commerceAddressId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipment in the ordered set where groupId = &#63; and commerceAddressId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAddressId the commerce address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment
	 * @throws NoSuchShipmentException if a matching commerce shipment could not be found
	 */
	@Override
	public CommerceShipment findByG_C_First(
			long groupId, long commerceAddressId,
			OrderByComparator<CommerceShipment> orderByComparator)
		throws NoSuchShipmentException {

		CommerceShipment commerceShipment = fetchByG_C_First(
			groupId, commerceAddressId, orderByComparator);

		if (commerceShipment != null) {
			return commerceShipment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", commerceAddressId=");
		sb.append(commerceAddressId);

		sb.append("}");

		throw new NoSuchShipmentException(sb.toString());
	}

	/**
	 * Returns the first commerce shipment in the ordered set where groupId = &#63; and commerceAddressId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAddressId the commerce address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment, or <code>null</code> if a matching commerce shipment could not be found
	 */
	@Override
	public CommerceShipment fetchByG_C_First(
		long groupId, long commerceAddressId,
		OrderByComparator<CommerceShipment> orderByComparator) {

		return _collectionPersistenceFinderByG_C.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}, commerceAddressId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce shipments that the user has permissions to view where groupId = &#63; and commerceAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAddressId the commerce address ID
	 * @param start the lower bound of the range of commerce shipments
	 * @param end the upper bound of the range of commerce shipments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipments that the user has permission to view
	 */
	@Override
	public List<CommerceShipment> filterFindByG_C(
		long groupId, long commerceAddressId, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator) {

		return _collectionPersistenceFinderByG_C.filterFind(
			finderCache, new Object[] {new long[] {groupId}, commerceAddressId},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the commerce shipments that the user has permission to view where groupId = any &#63; and commerceAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param commerceAddressId the commerce address ID
	 * @param start the lower bound of the range of commerce shipments
	 * @param end the upper bound of the range of commerce shipments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipments that the user has permission to view
	 */
	@Override
	public List<CommerceShipment> filterFindByG_C(
		long[] groupIds, long commerceAddressId, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_C.filterFind(
			finderCache, new Object[] {groupIds, commerceAddressId}, start, end,
			orderByComparator, groupIds);
	}

	/**
	 * Returns an ordered range of all the commerce shipments where groupId = &#63; and commerceAddressId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param commerceAddressId the commerce address ID
	 * @param start the lower bound of the range of commerce shipments
	 * @param end the upper bound of the range of commerce shipments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipments
	 */
	@Override
	public List<CommerceShipment> findByG_C(
		long[] groupIds, long commerceAddressId, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C.find(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), commerceAddressId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the commerce shipments where groupId = &#63; and commerceAddressId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param commerceAddressId the commerce address ID
	 */
	@Override
	public void removeByG_C(long groupId, long commerceAddressId) {
		_collectionPersistenceFinderByG_C.remove(
			finderCache,
			new Object[] {new long[] {groupId}, commerceAddressId});
	}

	/**
	 * Returns the number of commerce shipments where groupId = &#63; and commerceAddressId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAddressId the commerce address ID
	 * @return the number of matching commerce shipments
	 */
	@Override
	public int countByG_C(long groupId, long commerceAddressId) {
		return _collectionPersistenceFinderByG_C.count(
			finderCache,
			new Object[] {new long[] {groupId}, commerceAddressId});
	}

	/**
	 * Returns the number of commerce shipments where groupId = any &#63; and commerceAddressId = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param commerceAddressId the commerce address ID
	 * @return the number of matching commerce shipments
	 */
	@Override
	public int countByG_C(long[] groupIds, long commerceAddressId) {
		return _collectionPersistenceFinderByG_C.count(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), commerceAddressId});
	}

	/**
	 * Returns the number of commerce shipments that the user has permission to view where groupId = &#63; and commerceAddressId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAddressId the commerce address ID
	 * @return the number of matching commerce shipments that the user has permission to view
	 */
	@Override
	public int filterCountByG_C(long groupId, long commerceAddressId) {
		return _collectionPersistenceFinderByG_C.filterCount(
			finderCache, new Object[] {new long[] {groupId}, commerceAddressId},
			groupId);
	}

	/**
	 * Returns the number of commerce shipments that the user has permission to view where groupId = any &#63; and commerceAddressId = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param commerceAddressId the commerce address ID
	 * @return the number of matching commerce shipments that the user has permission to view
	 */
	@Override
	public int filterCountByG_C(long[] groupIds, long commerceAddressId) {
		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_C.filterCount(
			finderCache, new Object[] {groupIds, commerceAddressId}, groupIds);
	}

	private FilterCollectionPersistenceFinder
		<CommerceShipment, NoSuchShipmentException>
			_collectionPersistenceFinderByG_S;

	/**
	 * Returns an ordered range of all the commerce shipments where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of commerce shipments
	 * @param end the upper bound of the range of commerce shipments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipments
	 */
	@Override
	public List<CommerceShipment> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S.find(
			finderCache, new Object[] {new long[] {groupId}, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipment in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment
	 * @throws NoSuchShipmentException if a matching commerce shipment could not be found
	 */
	@Override
	public CommerceShipment findByG_S_First(
			long groupId, int status,
			OrderByComparator<CommerceShipment> orderByComparator)
		throws NoSuchShipmentException {

		CommerceShipment commerceShipment = fetchByG_S_First(
			groupId, status, orderByComparator);

		if (commerceShipment != null) {
			return commerceShipment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchShipmentException(sb.toString());
	}

	/**
	 * Returns the first commerce shipment in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment, or <code>null</code> if a matching commerce shipment could not be found
	 */
	@Override
	public CommerceShipment fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<CommerceShipment> orderByComparator) {

		return _collectionPersistenceFinderByG_S.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce shipments that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of commerce shipments
	 * @param end the upper bound of the range of commerce shipments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipments that the user has permission to view
	 */
	@Override
	public List<CommerceShipment> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator) {

		return _collectionPersistenceFinderByG_S.filterFind(
			finderCache, new Object[] {new long[] {groupId}, status}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the commerce shipments that the user has permission to view where groupId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param status the status
	 * @param start the lower bound of the range of commerce shipments
	 * @param end the upper bound of the range of commerce shipments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipments that the user has permission to view
	 */
	@Override
	public List<CommerceShipment> filterFindByG_S(
		long[] groupIds, int status, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_S.filterFind(
			finderCache, new Object[] {groupIds, status}, start, end,
			orderByComparator, groupIds);
	}

	/**
	 * Returns an ordered range of all the commerce shipments where groupId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param status the status
	 * @param start the lower bound of the range of commerce shipments
	 * @param end the upper bound of the range of commerce shipments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipments
	 */
	@Override
	public List<CommerceShipment> findByG_S(
		long[] groupIds, int status, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S.find(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the commerce shipments where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_S(long groupId, int status) {
		_collectionPersistenceFinderByG_S.remove(
			finderCache, new Object[] {new long[] {groupId}, status});
	}

	/**
	 * Returns the number of commerce shipments where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching commerce shipments
	 */
	@Override
	public int countByG_S(long groupId, int status) {
		return _collectionPersistenceFinderByG_S.count(
			finderCache, new Object[] {new long[] {groupId}, status});
	}

	/**
	 * Returns the number of commerce shipments where groupId = any &#63; and status = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param status the status
	 * @return the number of matching commerce shipments
	 */
	@Override
	public int countByG_S(long[] groupIds, int status) {
		return _collectionPersistenceFinderByG_S.count(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), status});
	}

	/**
	 * Returns the number of commerce shipments that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching commerce shipments that the user has permission to view
	 */
	@Override
	public int filterCountByG_S(long groupId, int status) {
		return _collectionPersistenceFinderByG_S.filterCount(
			finderCache, new Object[] {new long[] {groupId}, status}, groupId);
	}

	/**
	 * Returns the number of commerce shipments that the user has permission to view where groupId = any &#63; and status = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param status the status
	 * @return the number of matching commerce shipments that the user has permission to view
	 */
	@Override
	public int filterCountByG_S(long[] groupIds, int status) {
		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_S.filterCount(
			finderCache, new Object[] {groupIds, status}, groupIds);
	}

	private UniquePersistenceFinder<CommerceShipment, NoSuchShipmentException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the commerce shipment where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchShipmentException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce shipment
	 * @throws NoSuchShipmentException if a matching commerce shipment could not be found
	 */
	@Override
	public CommerceShipment findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchShipmentException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the commerce shipment where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce shipment, or <code>null</code> if a matching commerce shipment could not be found
	 */
	@Override
	public CommerceShipment fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the commerce shipment where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce shipment that was removed
	 */
	@Override
	public CommerceShipment removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchShipmentException {

		CommerceShipment commerceShipment = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commerceShipment);
	}

	/**
	 * Returns the number of commerce shipments where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce shipments
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CommerceShipmentPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceShipment.class);

		setModelImplClass(CommerceShipmentImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceShipmentTable.INSTANCE);
	}

	/**
	 * Creates a new commerce shipment with the primary key. Does not add the commerce shipment to the database.
	 *
	 * @param commerceShipmentId the primary key for the new commerce shipment
	 * @return the new commerce shipment
	 */
	@Override
	public CommerceShipment create(long commerceShipmentId) {
		CommerceShipment commerceShipment = new CommerceShipmentImpl();

		commerceShipment.setNew(true);
		commerceShipment.setPrimaryKey(commerceShipmentId);

		String uuid = PortalUUIDUtil.generate();

		commerceShipment.setUuid(uuid);

		commerceShipment.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceShipment;
	}

	/**
	 * Removes the commerce shipment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceShipmentId the primary key of the commerce shipment
	 * @return the commerce shipment that was removed
	 * @throws NoSuchShipmentException if a commerce shipment with the primary key could not be found
	 */
	@Override
	public CommerceShipment remove(long commerceShipmentId)
		throws NoSuchShipmentException {

		return remove((Serializable)commerceShipmentId);
	}

	@Override
	protected CommerceShipment removeImpl(CommerceShipment commerceShipment) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceShipment)) {
				commerceShipment = (CommerceShipment)session.get(
					CommerceShipmentImpl.class,
					commerceShipment.getPrimaryKeyObj());
			}

			if (commerceShipment != null) {
				session.delete(commerceShipment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceShipment != null) {
			clearCache(commerceShipment);
		}

		return commerceShipment;
	}

	@Override
	public CommerceShipment updateImpl(CommerceShipment commerceShipment) {
		boolean isNew = commerceShipment.isNew();

		if (!(commerceShipment instanceof CommerceShipmentModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceShipment.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceShipment);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceShipment proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceShipment implementation " +
					commerceShipment.getClass());
		}

		CommerceShipmentModelImpl commerceShipmentModelImpl =
			(CommerceShipmentModelImpl)commerceShipment;

		if (Validator.isNull(commerceShipment.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceShipment.setUuid(uuid);
		}

		if (Validator.isNull(commerceShipment.getExternalReferenceCode())) {
			commerceShipment.setExternalReferenceCode(
				commerceShipment.getUuid());
		}
		else {
			if (!Objects.equals(
					commerceShipmentModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commerceShipment.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commerceShipment.getCompanyId();

					long groupId = commerceShipment.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = commerceShipment.getPrimaryKey();
					}

					try {
						commerceShipment.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommerceShipment.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								commerceShipment.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommerceShipment ercCommerceShipment = fetchByERC_C(
				commerceShipment.getExternalReferenceCode(),
				commerceShipment.getCompanyId());

			if (isNew) {
				if (ercCommerceShipment != null) {
					throw new DuplicateCommerceShipmentExternalReferenceCodeException(
						"Duplicate commerce shipment with external reference code " +
							commerceShipment.getExternalReferenceCode() +
								" and company " +
									commerceShipment.getCompanyId());
				}
			}
			else {
				if ((ercCommerceShipment != null) &&
					(commerceShipment.getCommerceShipmentId() !=
						ercCommerceShipment.getCommerceShipmentId())) {

					throw new DuplicateCommerceShipmentExternalReferenceCodeException(
						"Duplicate commerce shipment with external reference code " +
							commerceShipment.getExternalReferenceCode() +
								" and company " +
									commerceShipment.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceShipment.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceShipment.setCreateDate(date);
			}
			else {
				commerceShipment.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceShipmentModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceShipment.setModifiedDate(date);
			}
			else {
				commerceShipment.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceShipment);
			}
			else {
				commerceShipment = (CommerceShipment)session.merge(
					commerceShipment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceShipment, false);

		if (isNew) {
			commerceShipment.setNew(false);
		}

		commerceShipment.resetOriginalValues();

		return commerceShipment;
	}

	/**
	 * Returns the commerce shipment with the primary key or throws a <code>NoSuchShipmentException</code> if it could not be found.
	 *
	 * @param commerceShipmentId the primary key of the commerce shipment
	 * @return the commerce shipment
	 * @throws NoSuchShipmentException if a commerce shipment with the primary key could not be found
	 */
	@Override
	public CommerceShipment findByPrimaryKey(long commerceShipmentId)
		throws NoSuchShipmentException {

		return findByPrimaryKey((Serializable)commerceShipmentId);
	}

	/**
	 * Returns the commerce shipment with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceShipmentId the primary key of the commerce shipment
	 * @return the commerce shipment, or <code>null</code> if a commerce shipment with the primary key could not be found
	 */
	@Override
	public CommerceShipment fetchByPrimaryKey(long commerceShipmentId) {
		return fetchByPrimaryKey((Serializable)commerceShipmentId);
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
		return "commerceShipmentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCESHIPMENT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceShipmentModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce shipment persistence.
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
			_SQL_SELECT_COMMERCESHIPMENT_WHERE,
			_SQL_COUNT_COMMERCESHIPMENT_WHERE,
			CommerceShipmentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"commerceShipment.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, CommerceShipment::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CommerceShipment::getUuid),
				CommerceShipment::getGroupId),
			_SQL_SELECT_COMMERCESHIPMENT_WHERE, "",
			new FinderColumn<>(
				"commerceShipment.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, CommerceShipment::getUuid),
			new FinderColumn<>(
				"commerceShipment.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, CommerceShipment::getGroupId));

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
				_SQL_SELECT_COMMERCESHIPMENT_WHERE,
				_SQL_COUNT_COMMERCESHIPMENT_WHERE,
				CommerceShipmentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceShipment.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceShipment::getUuid),
				new FinderColumn<>(
					"commerceShipment.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CommerceShipment::getCompanyId));

		_collectionPersistenceFinderByGroupId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_COMMERCESHIPMENT_WHERE,
				_SQL_COUNT_COMMERCESHIPMENT_WHERE,
				CommerceShipmentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new ArrayableFinderColumn<>(
					"commerceShipment.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, CommerceShipment::getGroupId));

		_collectionPersistenceFinderByG_C =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "commerceAddressId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "commerceAddressId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_C",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "commerceAddressId"}, false),
				_SQL_SELECT_COMMERCESHIPMENT_WHERE,
				_SQL_COUNT_COMMERCESHIPMENT_WHERE,
				CommerceShipmentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new ArrayableFinderColumn<>(
					"commerceShipment.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, CommerceShipment::getGroupId),
				new FinderColumn<>(
					"commerceShipment.", "commerceAddressId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceShipment::getCommerceAddressId));

		_collectionPersistenceFinderByG_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_S",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_S",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_S",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "status"}, false),
				_SQL_SELECT_COMMERCESHIPMENT_WHERE,
				_SQL_COUNT_COMMERCESHIPMENT_WHERE,
				CommerceShipmentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new ArrayableFinderColumn<>(
					"commerceShipment.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, CommerceShipment::getGroupId),
				new FinderColumn<>(
					"commerceShipment.", "status", FinderColumn.Type.INTEGER,
					"=", true, true, CommerceShipment::getStatus));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(CommerceShipment::getExternalReferenceCode),
				CommerceShipment::getCompanyId),
			_SQL_SELECT_COMMERCESHIPMENT_WHERE, "",
			new FinderColumn<>(
				"commerceShipment.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceShipment::getExternalReferenceCode),
			new FinderColumn<>(
				"commerceShipment.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CommerceShipment::getCompanyId));

		CommerceShipmentUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceShipmentUtil.setPersistence(null);

		entityCache.removeCache(CommerceShipmentImpl.class.getName());
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		CommerceShipmentModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCESHIPMENT =
		"SELECT commerceShipment FROM CommerceShipment commerceShipment";

	private static final String _SQL_SELECT_COMMERCESHIPMENT_WHERE =
		"SELECT commerceShipment FROM CommerceShipment commerceShipment WHERE ";

	private static final String _SQL_COUNT_COMMERCESHIPMENT_WHERE =
		"SELECT COUNT(commerceShipment) FROM CommerceShipment commerceShipment WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceShipment exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceShipmentPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:522707961