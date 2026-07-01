/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence.impl;

import com.liferay.commerce.discount.exception.DuplicateCommerceDiscountExternalReferenceCodeException;
import com.liferay.commerce.discount.exception.NoSuchDiscountException;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountTable;
import com.liferay.commerce.discount.model.impl.CommerceDiscountImpl;
import com.liferay.commerce.discount.model.impl.CommerceDiscountModelImpl;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountPersistence;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountUtil;
import com.liferay.commerce.discount.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce discount service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CommerceDiscountPersistence.class)
public class CommerceDiscountPersistenceImpl
	extends BasePersistenceImpl<CommerceDiscount, NoSuchDiscountException>
	implements CommerceDiscountPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceDiscountUtil</code> to access the commerce discount persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceDiscountImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<CommerceDiscount, NoSuchDiscountException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce discounts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce discount in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByUuid_First(
			String uuid, OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce discount in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByUuid_First(
		String uuid, OrderByComparator<CommerceDiscount> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce discounts that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			finderCache, new Object[] {uuid}, start, end, orderByComparator);
	}

	/**
	 * Removes all the commerce discounts where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce discounts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce discounts
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce discounts that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce discounts that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			finderCache, new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder
		<CommerceDiscount, NoSuchDiscountException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce discounts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce discount in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce discounts that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the commerce discounts where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce discounts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce discounts
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of commerce discounts that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce discounts that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			finderCache, new Object[] {uuid, companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<CommerceDiscount, NoSuchDiscountException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the commerce discounts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByCompanyId_First(
			long companyId,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce discount in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByCompanyId_First(
		long companyId, OrderByComparator<CommerceDiscount> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce discounts that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the commerce discounts where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of commerce discounts where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce discounts
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of commerce discounts that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce discounts that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<CommerceDiscount, NoSuchDiscountException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the commerce discounts where companyId = &#63; and couponCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByC_C(
		long companyId, String couponCode, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {companyId, couponCode}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount in the ordered set where companyId = &#63; and couponCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByC_C_First(
			long companyId, String couponCode,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {companyId, couponCode},
			orderByComparator);
	}

	/**
	 * Returns the first commerce discount in the ordered set where companyId = &#63; and couponCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByC_C_First(
		long companyId, String couponCode,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {companyId, couponCode},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce discounts that the user has permissions to view where companyId = &#63; and couponCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByC_C(
		long companyId, String couponCode, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return _collectionPersistenceFinderByC_C.filterFind(
			finderCache, new Object[] {companyId, couponCode}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the commerce discounts where companyId = &#63; and couponCode = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 */
	@Override
	public void removeByC_C(long companyId, String couponCode) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {companyId, couponCode});
	}

	/**
	 * Returns the number of commerce discounts where companyId = &#63; and couponCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @return the number of matching commerce discounts
	 */
	@Override
	public int countByC_C(long companyId, String couponCode) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {companyId, couponCode});
	}

	/**
	 * Returns the number of commerce discounts that the user has permission to view where companyId = &#63; and couponCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @return the number of matching commerce discounts that the user has permission to view
	 */
	@Override
	public int filterCountByC_C(long companyId, String couponCode) {
		return _collectionPersistenceFinderByC_C.filterCount(
			finderCache, new Object[] {companyId, couponCode}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<CommerceDiscount, NoSuchDiscountException>
			_collectionPersistenceFinderByLtD_S;

	/**
	 * Returns all the commerce discounts where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByLtD_S(Date displayDate, int status) {
		return findByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce discounts where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return findByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce discounts where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return findByLtD_S(
			displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce discounts where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtD_S.find(
			finderCache, new Object[] {displayDate, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		return _collectionPersistenceFinderByLtD_S.findFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Returns the first commerce discount in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return _collectionPersistenceFinderByLtD_S.fetchFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Returns all the commerce discounts that the user has permission to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByLtD_S(
		Date displayDate, int status) {

		return filterFindByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce discounts that the user has permission to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByLtD_S(
		Date displayDate, int status, int start, int end) {

		return filterFindByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce discounts that the user has permissions to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return _collectionPersistenceFinderByLtD_S.filterFind(
			finderCache, new Object[] {displayDate, status}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the commerce discounts where displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByLtD_S(Date displayDate, int status) {
		_collectionPersistenceFinderByLtD_S.remove(
			finderCache, new Object[] {displayDate, status});
	}

	/**
	 * Returns the number of commerce discounts where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching commerce discounts
	 */
	@Override
	public int countByLtD_S(Date displayDate, int status) {
		return _collectionPersistenceFinderByLtD_S.count(
			finderCache, new Object[] {displayDate, status});
	}

	/**
	 * Returns the number of commerce discounts that the user has permission to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching commerce discounts that the user has permission to view
	 */
	@Override
	public int filterCountByLtD_S(Date displayDate, int status) {
		return _collectionPersistenceFinderByLtD_S.filterCount(
			finderCache, new Object[] {displayDate, status});
	}

	private FilterCollectionPersistenceFinder
		<CommerceDiscount, NoSuchDiscountException>
			_collectionPersistenceFinderByLtE_S;

	/**
	 * Returns all the commerce discounts where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByLtE_S(Date expirationDate, int status) {
		return findByLtE_S(
			expirationDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce discounts where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByLtE_S(
		Date expirationDate, int status, int start, int end) {

		return findByLtE_S(expirationDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce discounts where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return findByLtE_S(
			expirationDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce discounts where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtE_S.find(
			finderCache, new Object[] {expirationDate, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByLtE_S_First(
			Date expirationDate, int status,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		return _collectionPersistenceFinderByLtE_S.findFirst(
			finderCache, new Object[] {expirationDate, status},
			orderByComparator);
	}

	/**
	 * Returns the first commerce discount in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByLtE_S_First(
		Date expirationDate, int status,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return _collectionPersistenceFinderByLtE_S.fetchFirst(
			finderCache, new Object[] {expirationDate, status},
			orderByComparator);
	}

	/**
	 * Returns all the commerce discounts that the user has permission to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByLtE_S(
		Date expirationDate, int status) {

		return filterFindByLtE_S(
			expirationDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce discounts that the user has permission to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByLtE_S(
		Date expirationDate, int status, int start, int end) {

		return filterFindByLtE_S(expirationDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce discounts that the user has permissions to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return _collectionPersistenceFinderByLtE_S.filterFind(
			finderCache, new Object[] {expirationDate, status}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the commerce discounts where expirationDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 */
	@Override
	public void removeByLtE_S(Date expirationDate, int status) {
		_collectionPersistenceFinderByLtE_S.remove(
			finderCache, new Object[] {expirationDate, status});
	}

	/**
	 * Returns the number of commerce discounts where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the number of matching commerce discounts
	 */
	@Override
	public int countByLtE_S(Date expirationDate, int status) {
		return _collectionPersistenceFinderByLtE_S.count(
			finderCache, new Object[] {expirationDate, status});
	}

	/**
	 * Returns the number of commerce discounts that the user has permission to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the number of matching commerce discounts that the user has permission to view
	 */
	@Override
	public int filterCountByLtE_S(Date expirationDate, int status) {
		return _collectionPersistenceFinderByLtE_S.filterCount(
			finderCache, new Object[] {expirationDate, status});
	}

	private UniquePersistenceFinder<CommerceDiscount, NoSuchDiscountException>
		_uniquePersistenceFinderByC_C_A;

	/**
	 * Returns the commerce discount where companyId = &#63; and couponCode = &#63; and active = &#63; or throws a <code>NoSuchDiscountException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param active the active
	 * @return the matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByC_C_A(
			long companyId, String couponCode, boolean active)
		throws NoSuchDiscountException {

		return _uniquePersistenceFinderByC_C_A.find(
			finderCache, new Object[] {companyId, couponCode, active});
	}

	/**
	 * Returns the commerce discount where companyId = &#63; and couponCode = &#63; and active = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param active the active
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByC_C_A(
		long companyId, String couponCode, boolean active,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_A.fetch(
			finderCache, new Object[] {companyId, couponCode, active},
			useFinderCache);
	}

	/**
	 * Removes the commerce discount where companyId = &#63; and couponCode = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param active the active
	 * @return the commerce discount that was removed
	 */
	@Override
	public CommerceDiscount removeByC_C_A(
			long companyId, String couponCode, boolean active)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = findByC_C_A(
			companyId, couponCode, active);

		return remove(commerceDiscount);
	}

	/**
	 * Returns the number of commerce discounts where companyId = &#63; and couponCode = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param active the active
	 * @return the number of matching commerce discounts
	 */
	@Override
	public int countByC_C_A(long companyId, String couponCode, boolean active) {
		return _uniquePersistenceFinderByC_C_A.count(
			finderCache, new Object[] {companyId, couponCode, active});
	}

	private FilterCollectionPersistenceFinder
		<CommerceDiscount, NoSuchDiscountException>
			_collectionPersistenceFinderByC_L_A_S;

	/**
	 * Returns an ordered range of all the commerce discounts where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByC_L_A_S(
		long companyId, String level, boolean active, int status, int start,
		int end, OrderByComparator<CommerceDiscount> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_L_A_S.find(
			finderCache, new Object[] {companyId, level, active, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount in the ordered set where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByC_L_A_S_First(
			long companyId, String level, boolean active, int status,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		return _collectionPersistenceFinderByC_L_A_S.findFirst(
			finderCache, new Object[] {companyId, level, active, status},
			orderByComparator);
	}

	/**
	 * Returns the first commerce discount in the ordered set where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByC_L_A_S_First(
		long companyId, String level, boolean active, int status,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return _collectionPersistenceFinderByC_L_A_S.fetchFirst(
			finderCache, new Object[] {companyId, level, active, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce discounts that the user has permissions to view where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByC_L_A_S(
		long companyId, String level, boolean active, int status, int start,
		int end, OrderByComparator<CommerceDiscount> orderByComparator) {

		return _collectionPersistenceFinderByC_L_A_S.filterFind(
			finderCache, new Object[] {companyId, level, active, status}, start,
			end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the commerce discounts where companyId = &#63; and level = &#63; and active = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 */
	@Override
	public void removeByC_L_A_S(
		long companyId, String level, boolean active, int status) {

		_collectionPersistenceFinderByC_L_A_S.remove(
			finderCache, new Object[] {companyId, level, active, status});
	}

	/**
	 * Returns the number of commerce discounts where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @return the number of matching commerce discounts
	 */
	@Override
	public int countByC_L_A_S(
		long companyId, String level, boolean active, int status) {

		return _collectionPersistenceFinderByC_L_A_S.count(
			finderCache, new Object[] {companyId, level, active, status});
	}

	/**
	 * Returns the number of commerce discounts that the user has permission to view where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @return the number of matching commerce discounts that the user has permission to view
	 */
	@Override
	public int filterCountByC_L_A_S(
		long companyId, String level, boolean active, int status) {

		return _collectionPersistenceFinderByC_L_A_S.filterCount(
			finderCache, new Object[] {companyId, level, active, status},
			companyId, 0);
	}

	private UniquePersistenceFinder<CommerceDiscount, NoSuchDiscountException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the commerce discount where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchDiscountException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchDiscountException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the commerce discount where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the commerce discount where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce discount that was removed
	 */
	@Override
	public CommerceDiscount removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commerceDiscount);
	}

	/**
	 * Returns the number of commerce discounts where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce discounts
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CommerceDiscountPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("level", "levelType");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceDiscount.class);

		setModelImplClass(CommerceDiscountImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceDiscountTable.INSTANCE);
	}

	/**
	 * Creates a new commerce discount with the primary key. Does not add the commerce discount to the database.
	 *
	 * @param commerceDiscountId the primary key for the new commerce discount
	 * @return the new commerce discount
	 */
	@Override
	public CommerceDiscount create(long commerceDiscountId) {
		CommerceDiscount commerceDiscount = new CommerceDiscountImpl();

		commerceDiscount.setNew(true);
		commerceDiscount.setPrimaryKey(commerceDiscountId);

		String uuid = PortalUUIDUtil.generate();

		commerceDiscount.setUuid(uuid);

		commerceDiscount.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceDiscount;
	}

	/**
	 * Removes the commerce discount with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceDiscountId the primary key of the commerce discount
	 * @return the commerce discount that was removed
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount remove(long commerceDiscountId)
		throws NoSuchDiscountException {

		return remove((Serializable)commerceDiscountId);
	}

	@Override
	protected CommerceDiscount removeImpl(CommerceDiscount commerceDiscount) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceDiscount)) {
				commerceDiscount = (CommerceDiscount)session.get(
					CommerceDiscountImpl.class,
					commerceDiscount.getPrimaryKeyObj());
			}

			if (commerceDiscount != null) {
				session.delete(commerceDiscount);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceDiscount != null) {
			clearCache(commerceDiscount);
		}

		return commerceDiscount;
	}

	@Override
	public CommerceDiscount updateImpl(CommerceDiscount commerceDiscount) {
		boolean isNew = commerceDiscount.isNew();

		if (!(commerceDiscount instanceof CommerceDiscountModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceDiscount.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceDiscount);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceDiscount proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceDiscount implementation " +
					commerceDiscount.getClass());
		}

		CommerceDiscountModelImpl commerceDiscountModelImpl =
			(CommerceDiscountModelImpl)commerceDiscount;

		if (Validator.isNull(commerceDiscount.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceDiscount.setUuid(uuid);
		}

		if (Validator.isNull(commerceDiscount.getExternalReferenceCode())) {
			commerceDiscount.setExternalReferenceCode(
				commerceDiscount.getUuid());
		}
		else {
			if (!Objects.equals(
					commerceDiscountModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commerceDiscount.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commerceDiscount.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = commerceDiscount.getPrimaryKey();
					}

					try {
						commerceDiscount.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommerceDiscount.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								commerceDiscount.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommerceDiscount ercCommerceDiscount = fetchByERC_C(
				commerceDiscount.getExternalReferenceCode(),
				commerceDiscount.getCompanyId());

			if (isNew) {
				if (ercCommerceDiscount != null) {
					throw new DuplicateCommerceDiscountExternalReferenceCodeException(
						"Duplicate commerce discount with external reference code " +
							commerceDiscount.getExternalReferenceCode() +
								" and company " +
									commerceDiscount.getCompanyId());
				}
			}
			else {
				if ((ercCommerceDiscount != null) &&
					(commerceDiscount.getCommerceDiscountId() !=
						ercCommerceDiscount.getCommerceDiscountId())) {

					throw new DuplicateCommerceDiscountExternalReferenceCodeException(
						"Duplicate commerce discount with external reference code " +
							commerceDiscount.getExternalReferenceCode() +
								" and company " +
									commerceDiscount.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceDiscount.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceDiscount.setCreateDate(date);
			}
			else {
				commerceDiscount.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceDiscountModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceDiscount.setModifiedDate(date);
			}
			else {
				commerceDiscount.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceDiscount);
			}
			else {
				commerceDiscount = (CommerceDiscount)session.merge(
					commerceDiscount);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceDiscount, false);

		if (isNew) {
			commerceDiscount.setNew(false);
		}

		commerceDiscount.resetOriginalValues();

		return commerceDiscount;
	}

	/**
	 * Returns the commerce discount with the primary key or throws a <code>NoSuchDiscountException</code> if it could not be found.
	 *
	 * @param commerceDiscountId the primary key of the commerce discount
	 * @return the commerce discount
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount findByPrimaryKey(long commerceDiscountId)
		throws NoSuchDiscountException {

		return findByPrimaryKey((Serializable)commerceDiscountId);
	}

	/**
	 * Returns the commerce discount with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceDiscountId the primary key of the commerce discount
	 * @return the commerce discount, or <code>null</code> if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount fetchByPrimaryKey(long commerceDiscountId) {
		return fetchByPrimaryKey((Serializable)commerceDiscountId);
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
		return "commerceDiscountId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEDISCOUNT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceDiscountModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce discount persistence.
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
				_SQL_SELECT_COMMERCEDISCOUNT_WHERE,
				_SQL_COUNT_COMMERCEDISCOUNT_WHERE,
				CommerceDiscountModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"commerceDiscount.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceDiscount::getUuid));

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
				_SQL_SELECT_COMMERCEDISCOUNT_WHERE,
				_SQL_COUNT_COMMERCEDISCOUNT_WHERE,
				CommerceDiscountModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"commerceDiscount.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceDiscount::getUuid),
				new FinderColumn<>(
					"commerceDiscount.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CommerceDiscount::getCompanyId));

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
				_SQL_SELECT_COMMERCEDISCOUNT_WHERE,
				_SQL_COUNT_COMMERCEDISCOUNT_WHERE,
				CommerceDiscountModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"commerceDiscount.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CommerceDiscount::getCompanyId));

		_collectionPersistenceFinderByC_C =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "couponCode"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "couponCode"}, 2, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "couponCode"}, 2, 2, false,
					null),
				_SQL_SELECT_COMMERCEDISCOUNT_WHERE,
				_SQL_COUNT_COMMERCEDISCOUNT_WHERE,
				CommerceDiscountModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"commerceDiscount.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CommerceDiscount::getCompanyId),
				new FinderColumn<>(
					"commerceDiscount.", "couponCode", FinderColumn.Type.STRING,
					"=", false, true, CommerceDiscount::getCouponCode));

		_collectionPersistenceFinderByLtD_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtD_S",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"displayDate", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtD_S",
					new String[] {
						Date.class.getName(), Integer.class.getName()
					},
					new String[] {"displayDate", "status"}, false),
				_SQL_SELECT_COMMERCEDISCOUNT_WHERE,
				_SQL_COUNT_COMMERCEDISCOUNT_WHERE,
				CommerceDiscountModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"commerceDiscount.", "displayDate", FinderColumn.Type.DATE,
					"<", true, true, CommerceDiscount::getDisplayDate),
				new FinderColumn<>(
					"commerceDiscount.", "status", FinderColumn.Type.INTEGER,
					"=", true, true, CommerceDiscount::getStatus));

		_collectionPersistenceFinderByLtE_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtE_S",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"expirationDate", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtE_S",
					new String[] {
						Date.class.getName(), Integer.class.getName()
					},
					new String[] {"expirationDate", "status"}, false),
				_SQL_SELECT_COMMERCEDISCOUNT_WHERE,
				_SQL_COUNT_COMMERCEDISCOUNT_WHERE,
				CommerceDiscountModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"commerceDiscount.", "expirationDate",
					FinderColumn.Type.DATE, "<", true, true,
					CommerceDiscount::getExpirationDate),
				new FinderColumn<>(
					"commerceDiscount.", "status", FinderColumn.Type.INTEGER,
					"=", true, true, CommerceDiscount::getStatus));

		_uniquePersistenceFinderByC_C_A = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_A",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"companyId", "couponCode", "active_"}, 2, 2,
				false, CommerceDiscount::getCompanyId,
				convertCaseFunction(CommerceDiscount::getCouponCode),
				CommerceDiscount::isActive),
			_SQL_SELECT_COMMERCEDISCOUNT_WHERE, "",
			new FinderColumn<>(
				"commerceDiscount.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CommerceDiscount::getCompanyId),
			new FinderColumn<>(
				"commerceDiscount.", "couponCode", FinderColumn.Type.STRING,
				"=", false, true, CommerceDiscount::getCouponCode),
			new FinderColumn<>(
				"commerceDiscount.", "active", "active_",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				CommerceDiscount::isActive));

		_collectionPersistenceFinderByC_L_A_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_L_A_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "levelType", "active_", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_L_A_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {
						"companyId", "levelType", "active_", "status"
					},
					0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_L_A_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {
						"companyId", "levelType", "active_", "status"
					},
					0, 2, false, null),
				_SQL_SELECT_COMMERCEDISCOUNT_WHERE,
				_SQL_COUNT_COMMERCEDISCOUNT_WHERE,
				CommerceDiscountModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"commerceDiscount.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CommerceDiscount::getCompanyId),
				new FinderColumn<>(
					"commerceDiscount.", "level", "levelType",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceDiscount::getLevel),
				new FinderColumn<>(
					"commerceDiscount.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					CommerceDiscount::isActive),
				new FinderColumn<>(
					"commerceDiscount.", "status", FinderColumn.Type.INTEGER,
					"=", true, true, CommerceDiscount::getStatus));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(CommerceDiscount::getExternalReferenceCode),
				CommerceDiscount::getCompanyId),
			_SQL_SELECT_COMMERCEDISCOUNT_WHERE, "",
			new FinderColumn<>(
				"commerceDiscount.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceDiscount::getExternalReferenceCode),
			new FinderColumn<>(
				"commerceDiscount.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CommerceDiscount::getCompanyId));

		CommerceDiscountUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceDiscountUtil.setPersistence(null);

		entityCache.removeCache(CommerceDiscountImpl.class.getName());
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
		CommerceDiscountModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEDISCOUNT =
		"SELECT commerceDiscount FROM CommerceDiscount commerceDiscount";

	private static final String _SQL_SELECT_COMMERCEDISCOUNT_WHERE =
		"SELECT commerceDiscount FROM CommerceDiscount commerceDiscount WHERE ";

	private static final String _SQL_COUNT_COMMERCEDISCOUNT_WHERE =
		"SELECT COUNT(commerceDiscount) FROM CommerceDiscount commerceDiscount WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "level", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1066991629