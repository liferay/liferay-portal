/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.impl;

import com.liferay.commerce.exception.DuplicateCommerceOrderTypeRelExternalReferenceCodeException;
import com.liferay.commerce.exception.NoSuchOrderTypeRelException;
import com.liferay.commerce.model.CommerceOrderTypeRel;
import com.liferay.commerce.model.CommerceOrderTypeRelTable;
import com.liferay.commerce.model.impl.CommerceOrderTypeRelImpl;
import com.liferay.commerce.model.impl.CommerceOrderTypeRelModelImpl;
import com.liferay.commerce.service.persistence.CommerceOrderTypeRelPersistence;
import com.liferay.commerce.service.persistence.CommerceOrderTypeRelUtil;
import com.liferay.commerce.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce order type rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceOrderTypeRelPersistence.class)
public class CommerceOrderTypeRelPersistenceImpl
	extends BasePersistenceImpl
		<CommerceOrderTypeRel, NoSuchOrderTypeRelException>
	implements CommerceOrderTypeRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceOrderTypeRelUtil</code> to access the commerce order type rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceOrderTypeRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceOrderTypeRel, NoSuchOrderTypeRelException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce order type rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce order type rels
	 * @param end the upper bound of the range of commerce order type rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order type rels
	 */
	@Override
	public List<CommerceOrderTypeRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceOrderTypeRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce order type rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type rel
	 * @throws NoSuchOrderTypeRelException if a matching commerce order type rel could not be found
	 */
	@Override
	public CommerceOrderTypeRel findByUuid_First(
			String uuid,
			OrderByComparator<CommerceOrderTypeRel> orderByComparator)
		throws NoSuchOrderTypeRelException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce order type rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type rel, or <code>null</code> if a matching commerce order type rel could not be found
	 */
	@Override
	public CommerceOrderTypeRel fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceOrderTypeRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce order type rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce order type rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce order type rels
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<CommerceOrderTypeRel, NoSuchOrderTypeRelException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce order type rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce order type rels
	 * @param end the upper bound of the range of commerce order type rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order type rels
	 */
	@Override
	public List<CommerceOrderTypeRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceOrderTypeRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order type rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type rel
	 * @throws NoSuchOrderTypeRelException if a matching commerce order type rel could not be found
	 */
	@Override
	public CommerceOrderTypeRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceOrderTypeRel> orderByComparator)
		throws NoSuchOrderTypeRelException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce order type rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type rel, or <code>null</code> if a matching commerce order type rel could not be found
	 */
	@Override
	public CommerceOrderTypeRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceOrderTypeRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce order type rels where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce order type rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce order type rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommerceOrderTypeRel, NoSuchOrderTypeRelException>
			_collectionPersistenceFinderByCommerceOrderTypeId;

	/**
	 * Returns an ordered range of all the commerce order type rels where commerceOrderTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderTypeId the commerce order type ID
	 * @param start the lower bound of the range of commerce order type rels
	 * @param end the upper bound of the range of commerce order type rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order type rels
	 */
	@Override
	public List<CommerceOrderTypeRel> findByCommerceOrderTypeId(
		long commerceOrderTypeId, int start, int end,
		OrderByComparator<CommerceOrderTypeRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceOrderTypeId.find(
			finderCache, new Object[] {commerceOrderTypeId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order type rel in the ordered set where commerceOrderTypeId = &#63;.
	 *
	 * @param commerceOrderTypeId the commerce order type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type rel
	 * @throws NoSuchOrderTypeRelException if a matching commerce order type rel could not be found
	 */
	@Override
	public CommerceOrderTypeRel findByCommerceOrderTypeId_First(
			long commerceOrderTypeId,
			OrderByComparator<CommerceOrderTypeRel> orderByComparator)
		throws NoSuchOrderTypeRelException {

		return _collectionPersistenceFinderByCommerceOrderTypeId.findFirst(
			finderCache, new Object[] {commerceOrderTypeId}, orderByComparator);
	}

	/**
	 * Returns the first commerce order type rel in the ordered set where commerceOrderTypeId = &#63;.
	 *
	 * @param commerceOrderTypeId the commerce order type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type rel, or <code>null</code> if a matching commerce order type rel could not be found
	 */
	@Override
	public CommerceOrderTypeRel fetchByCommerceOrderTypeId_First(
		long commerceOrderTypeId,
		OrderByComparator<CommerceOrderTypeRel> orderByComparator) {

		return _collectionPersistenceFinderByCommerceOrderTypeId.fetchFirst(
			finderCache, new Object[] {commerceOrderTypeId}, orderByComparator);
	}

	/**
	 * Removes all the commerce order type rels where commerceOrderTypeId = &#63; from the database.
	 *
	 * @param commerceOrderTypeId the commerce order type ID
	 */
	@Override
	public void removeByCommerceOrderTypeId(long commerceOrderTypeId) {
		_collectionPersistenceFinderByCommerceOrderTypeId.remove(
			finderCache, new Object[] {commerceOrderTypeId});
	}

	/**
	 * Returns the number of commerce order type rels where commerceOrderTypeId = &#63;.
	 *
	 * @param commerceOrderTypeId the commerce order type ID
	 * @return the number of matching commerce order type rels
	 */
	@Override
	public int countByCommerceOrderTypeId(long commerceOrderTypeId) {
		return _collectionPersistenceFinderByCommerceOrderTypeId.count(
			finderCache, new Object[] {commerceOrderTypeId});
	}

	private CollectionPersistenceFinder
		<CommerceOrderTypeRel, NoSuchOrderTypeRelException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the commerce order type rels where classNameId = &#63; and commerceOrderTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param commerceOrderTypeId the commerce order type ID
	 * @param start the lower bound of the range of commerce order type rels
	 * @param end the upper bound of the range of commerce order type rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order type rels
	 */
	@Override
	public List<CommerceOrderTypeRel> findByC_C(
		long classNameId, long commerceOrderTypeId, int start, int end,
		OrderByComparator<CommerceOrderTypeRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, commerceOrderTypeId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order type rel in the ordered set where classNameId = &#63; and commerceOrderTypeId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param commerceOrderTypeId the commerce order type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type rel
	 * @throws NoSuchOrderTypeRelException if a matching commerce order type rel could not be found
	 */
	@Override
	public CommerceOrderTypeRel findByC_C_First(
			long classNameId, long commerceOrderTypeId,
			OrderByComparator<CommerceOrderTypeRel> orderByComparator)
		throws NoSuchOrderTypeRelException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {classNameId, commerceOrderTypeId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce order type rel in the ordered set where classNameId = &#63; and commerceOrderTypeId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param commerceOrderTypeId the commerce order type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type rel, or <code>null</code> if a matching commerce order type rel could not be found
	 */
	@Override
	public CommerceOrderTypeRel fetchByC_C_First(
		long classNameId, long commerceOrderTypeId,
		OrderByComparator<CommerceOrderTypeRel> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {classNameId, commerceOrderTypeId},
			orderByComparator);
	}

	/**
	 * Removes all the commerce order type rels where classNameId = &#63; and commerceOrderTypeId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param commerceOrderTypeId the commerce order type ID
	 */
	@Override
	public void removeByC_C(long classNameId, long commerceOrderTypeId) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {classNameId, commerceOrderTypeId});
	}

	/**
	 * Returns the number of commerce order type rels where classNameId = &#63; and commerceOrderTypeId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param commerceOrderTypeId the commerce order type ID
	 * @return the number of matching commerce order type rels
	 */
	@Override
	public int countByC_C(long classNameId, long commerceOrderTypeId) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, commerceOrderTypeId});
	}

	private UniquePersistenceFinder
		<CommerceOrderTypeRel, NoSuchOrderTypeRelException>
			_uniquePersistenceFinderByC_C_C;

	/**
	 * Returns the commerce order type rel where classNameId = &#63; and classPK = &#63; and commerceOrderTypeId = &#63; or throws a <code>NoSuchOrderTypeRelException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceOrderTypeId the commerce order type ID
	 * @return the matching commerce order type rel
	 * @throws NoSuchOrderTypeRelException if a matching commerce order type rel could not be found
	 */
	@Override
	public CommerceOrderTypeRel findByC_C_C(
			long classNameId, long classPK, long commerceOrderTypeId)
		throws NoSuchOrderTypeRelException {

		return _uniquePersistenceFinderByC_C_C.find(
			finderCache,
			new Object[] {classNameId, classPK, commerceOrderTypeId});
	}

	/**
	 * Returns the commerce order type rel where classNameId = &#63; and classPK = &#63; and commerceOrderTypeId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceOrderTypeId the commerce order type ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order type rel, or <code>null</code> if a matching commerce order type rel could not be found
	 */
	@Override
	public CommerceOrderTypeRel fetchByC_C_C(
		long classNameId, long classPK, long commerceOrderTypeId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_C.fetch(
			finderCache,
			new Object[] {classNameId, classPK, commerceOrderTypeId},
			useFinderCache);
	}

	/**
	 * Removes the commerce order type rel where classNameId = &#63; and classPK = &#63; and commerceOrderTypeId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceOrderTypeId the commerce order type ID
	 * @return the commerce order type rel that was removed
	 */
	@Override
	public CommerceOrderTypeRel removeByC_C_C(
			long classNameId, long classPK, long commerceOrderTypeId)
		throws NoSuchOrderTypeRelException {

		CommerceOrderTypeRel commerceOrderTypeRel = findByC_C_C(
			classNameId, classPK, commerceOrderTypeId);

		return remove(commerceOrderTypeRel);
	}

	/**
	 * Returns the number of commerce order type rels where classNameId = &#63; and classPK = &#63; and commerceOrderTypeId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceOrderTypeId the commerce order type ID
	 * @return the number of matching commerce order type rels
	 */
	@Override
	public int countByC_C_C(
		long classNameId, long classPK, long commerceOrderTypeId) {

		return _uniquePersistenceFinderByC_C_C.count(
			finderCache,
			new Object[] {classNameId, classPK, commerceOrderTypeId});
	}

	private UniquePersistenceFinder
		<CommerceOrderTypeRel, NoSuchOrderTypeRelException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the commerce order type rel where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchOrderTypeRelException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce order type rel
	 * @throws NoSuchOrderTypeRelException if a matching commerce order type rel could not be found
	 */
	@Override
	public CommerceOrderTypeRel findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOrderTypeRelException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the commerce order type rel where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order type rel, or <code>null</code> if a matching commerce order type rel could not be found
	 */
	@Override
	public CommerceOrderTypeRel fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the commerce order type rel where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce order type rel that was removed
	 */
	@Override
	public CommerceOrderTypeRel removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOrderTypeRelException {

		CommerceOrderTypeRel commerceOrderTypeRel = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commerceOrderTypeRel);
	}

	/**
	 * Returns the number of commerce order type rels where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce order type rels
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CommerceOrderTypeRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceOrderTypeRel.class);

		setModelImplClass(CommerceOrderTypeRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceOrderTypeRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce order type rel with the primary key. Does not add the commerce order type rel to the database.
	 *
	 * @param commerceOrderTypeRelId the primary key for the new commerce order type rel
	 * @return the new commerce order type rel
	 */
	@Override
	public CommerceOrderTypeRel create(long commerceOrderTypeRelId) {
		CommerceOrderTypeRel commerceOrderTypeRel =
			new CommerceOrderTypeRelImpl();

		commerceOrderTypeRel.setNew(true);
		commerceOrderTypeRel.setPrimaryKey(commerceOrderTypeRelId);

		String uuid = PortalUUIDUtil.generate();

		commerceOrderTypeRel.setUuid(uuid);

		commerceOrderTypeRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceOrderTypeRel;
	}

	/**
	 * Removes the commerce order type rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceOrderTypeRelId the primary key of the commerce order type rel
	 * @return the commerce order type rel that was removed
	 * @throws NoSuchOrderTypeRelException if a commerce order type rel with the primary key could not be found
	 */
	@Override
	public CommerceOrderTypeRel remove(long commerceOrderTypeRelId)
		throws NoSuchOrderTypeRelException {

		return remove((Serializable)commerceOrderTypeRelId);
	}

	@Override
	protected CommerceOrderTypeRel removeImpl(
		CommerceOrderTypeRel commerceOrderTypeRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceOrderTypeRel)) {
				commerceOrderTypeRel = (CommerceOrderTypeRel)session.get(
					CommerceOrderTypeRelImpl.class,
					commerceOrderTypeRel.getPrimaryKeyObj());
			}

			if (commerceOrderTypeRel != null) {
				session.delete(commerceOrderTypeRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceOrderTypeRel != null) {
			clearCache(commerceOrderTypeRel);
		}

		return commerceOrderTypeRel;
	}

	@Override
	public CommerceOrderTypeRel updateImpl(
		CommerceOrderTypeRel commerceOrderTypeRel) {

		boolean isNew = commerceOrderTypeRel.isNew();

		if (!(commerceOrderTypeRel instanceof CommerceOrderTypeRelModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceOrderTypeRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceOrderTypeRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceOrderTypeRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceOrderTypeRel implementation " +
					commerceOrderTypeRel.getClass());
		}

		CommerceOrderTypeRelModelImpl commerceOrderTypeRelModelImpl =
			(CommerceOrderTypeRelModelImpl)commerceOrderTypeRel;

		if (Validator.isNull(commerceOrderTypeRel.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceOrderTypeRel.setUuid(uuid);
		}

		if (Validator.isNull(commerceOrderTypeRel.getExternalReferenceCode())) {
			commerceOrderTypeRel.setExternalReferenceCode(
				commerceOrderTypeRel.getUuid());
		}
		else {
			if (!Objects.equals(
					commerceOrderTypeRelModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commerceOrderTypeRel.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commerceOrderTypeRel.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = commerceOrderTypeRel.getPrimaryKey();
					}

					try {
						commerceOrderTypeRel.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommerceOrderTypeRel.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								commerceOrderTypeRel.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommerceOrderTypeRel ercCommerceOrderTypeRel = fetchByERC_C(
				commerceOrderTypeRel.getExternalReferenceCode(),
				commerceOrderTypeRel.getCompanyId());

			if (isNew) {
				if (ercCommerceOrderTypeRel != null) {
					throw new DuplicateCommerceOrderTypeRelExternalReferenceCodeException(
						"Duplicate commerce order type rel with external reference code " +
							commerceOrderTypeRel.getExternalReferenceCode() +
								" and company " +
									commerceOrderTypeRel.getCompanyId());
				}
			}
			else {
				if ((ercCommerceOrderTypeRel != null) &&
					(commerceOrderTypeRel.getCommerceOrderTypeRelId() !=
						ercCommerceOrderTypeRel.getCommerceOrderTypeRelId())) {

					throw new DuplicateCommerceOrderTypeRelExternalReferenceCodeException(
						"Duplicate commerce order type rel with external reference code " +
							commerceOrderTypeRel.getExternalReferenceCode() +
								" and company " +
									commerceOrderTypeRel.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceOrderTypeRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceOrderTypeRel.setCreateDate(date);
			}
			else {
				commerceOrderTypeRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceOrderTypeRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceOrderTypeRel.setModifiedDate(date);
			}
			else {
				commerceOrderTypeRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceOrderTypeRel);
			}
			else {
				commerceOrderTypeRel = (CommerceOrderTypeRel)session.merge(
					commerceOrderTypeRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceOrderTypeRel, false);

		if (isNew) {
			commerceOrderTypeRel.setNew(false);
		}

		commerceOrderTypeRel.resetOriginalValues();

		return commerceOrderTypeRel;
	}

	/**
	 * Returns the commerce order type rel with the primary key or throws a <code>NoSuchOrderTypeRelException</code> if it could not be found.
	 *
	 * @param commerceOrderTypeRelId the primary key of the commerce order type rel
	 * @return the commerce order type rel
	 * @throws NoSuchOrderTypeRelException if a commerce order type rel with the primary key could not be found
	 */
	@Override
	public CommerceOrderTypeRel findByPrimaryKey(long commerceOrderTypeRelId)
		throws NoSuchOrderTypeRelException {

		return findByPrimaryKey((Serializable)commerceOrderTypeRelId);
	}

	/**
	 * Returns the commerce order type rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceOrderTypeRelId the primary key of the commerce order type rel
	 * @return the commerce order type rel, or <code>null</code> if a commerce order type rel with the primary key could not be found
	 */
	@Override
	public CommerceOrderTypeRel fetchByPrimaryKey(long commerceOrderTypeRelId) {
		return fetchByPrimaryKey((Serializable)commerceOrderTypeRelId);
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
		return "commerceOrderTypeRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEORDERTYPEREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceOrderTypeRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce order type rel persistence.
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
			_SQL_SELECT_COMMERCEORDERTYPEREL_WHERE,
			_SQL_COUNT_COMMERCEORDERTYPEREL_WHERE,
			CommerceOrderTypeRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"commerceOrderTypeRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceOrderTypeRel::getUuid));

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
				_SQL_SELECT_COMMERCEORDERTYPEREL_WHERE,
				_SQL_COUNT_COMMERCEORDERTYPEREL_WHERE,
				CommerceOrderTypeRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceOrderTypeRel.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceOrderTypeRel::getUuid),
				new FinderColumn<>(
					"commerceOrderTypeRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceOrderTypeRel::getCompanyId));

		_collectionPersistenceFinderByCommerceOrderTypeId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceOrderTypeId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceOrderTypeId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceOrderTypeId",
					new String[] {Long.class.getName()},
					new String[] {"commerceOrderTypeId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceOrderTypeId",
					new String[] {Long.class.getName()},
					new String[] {"commerceOrderTypeId"}, false),
				_SQL_SELECT_COMMERCEORDERTYPEREL_WHERE,
				_SQL_COUNT_COMMERCEORDERTYPEREL_WHERE,
				CommerceOrderTypeRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceOrderTypeRel.", "commerceOrderTypeId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceOrderTypeRel::getCommerceOrderTypeId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "commerceOrderTypeId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "commerceOrderTypeId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "commerceOrderTypeId"}, false),
			_SQL_SELECT_COMMERCEORDERTYPEREL_WHERE,
			_SQL_COUNT_COMMERCEORDERTYPEREL_WHERE,
			CommerceOrderTypeRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"commerceOrderTypeRel.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, CommerceOrderTypeRel::getClassNameId),
			new FinderColumn<>(
				"commerceOrderTypeRel.", "commerceOrderTypeId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceOrderTypeRel::getCommerceOrderTypeId));

		_uniquePersistenceFinderByC_C_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"classNameId", "classPK", "commerceOrderTypeId"},
				0, 0, false, CommerceOrderTypeRel::getClassNameId,
				CommerceOrderTypeRel::getClassPK,
				CommerceOrderTypeRel::getCommerceOrderTypeId),
			_SQL_SELECT_COMMERCEORDERTYPEREL_WHERE, "",
			new FinderColumn<>(
				"commerceOrderTypeRel.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, CommerceOrderTypeRel::getClassNameId),
			new FinderColumn<>(
				"commerceOrderTypeRel.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, CommerceOrderTypeRel::getClassPK),
			new FinderColumn<>(
				"commerceOrderTypeRel.", "commerceOrderTypeId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceOrderTypeRel::getCommerceOrderTypeId));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					CommerceOrderTypeRel::getExternalReferenceCode),
				CommerceOrderTypeRel::getCompanyId),
			_SQL_SELECT_COMMERCEORDERTYPEREL_WHERE, "",
			new FinderColumn<>(
				"commerceOrderTypeRel.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceOrderTypeRel::getExternalReferenceCode),
			new FinderColumn<>(
				"commerceOrderTypeRel.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, CommerceOrderTypeRel::getCompanyId));

		CommerceOrderTypeRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceOrderTypeRelUtil.setPersistence(null);

		entityCache.removeCache(CommerceOrderTypeRelImpl.class.getName());
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
		CommerceOrderTypeRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEORDERTYPEREL =
		"SELECT commerceOrderTypeRel FROM CommerceOrderTypeRel commerceOrderTypeRel";

	private static final String _SQL_SELECT_COMMERCEORDERTYPEREL_WHERE =
		"SELECT commerceOrderTypeRel FROM CommerceOrderTypeRel commerceOrderTypeRel WHERE ";

	private static final String _SQL_COUNT_COMMERCEORDERTYPEREL_WHERE =
		"SELECT COUNT(commerceOrderTypeRel) FROM CommerceOrderTypeRel commerceOrderTypeRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceOrderTypeRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderTypeRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1776144857