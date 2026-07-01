/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.DuplicateCPTaxCategoryExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPTaxCategoryException;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.model.CPTaxCategoryTable;
import com.liferay.commerce.product.model.impl.CPTaxCategoryImpl;
import com.liferay.commerce.product.model.impl.CPTaxCategoryModelImpl;
import com.liferay.commerce.product.service.persistence.CPTaxCategoryPersistence;
import com.liferay.commerce.product.service.persistence.CPTaxCategoryUtil;
import com.liferay.commerce.product.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
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
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
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
 * The persistence implementation for the cp tax category service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPTaxCategoryPersistence.class)
public class CPTaxCategoryPersistenceImpl
	extends BasePersistenceImpl<CPTaxCategory, NoSuchCPTaxCategoryException>
	implements CPTaxCategoryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPTaxCategoryUtil</code> to access the cp tax category persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPTaxCategoryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<CPTaxCategory, NoSuchCPTaxCategoryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cp tax categories where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPTaxCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp tax categories
	 * @param end the upper bound of the range of cp tax categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp tax categories
	 */
	@Override
	public List<CPTaxCategory> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPTaxCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp tax category in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp tax category
	 * @throws NoSuchCPTaxCategoryException if a matching cp tax category could not be found
	 */
	@Override
	public CPTaxCategory findByUuid_First(
			String uuid, OrderByComparator<CPTaxCategory> orderByComparator)
		throws NoSuchCPTaxCategoryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cp tax category in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp tax category, or <code>null</code> if a matching cp tax category could not be found
	 */
	@Override
	public CPTaxCategory fetchByUuid_First(
		String uuid, OrderByComparator<CPTaxCategory> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp tax categories that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPTaxCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp tax categories
	 * @param end the upper bound of the range of cp tax categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp tax categories that the user has permission to view
	 */
	@Override
	public List<CPTaxCategory> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPTaxCategory> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			finderCache, new Object[] {uuid}, start, end, orderByComparator);
	}

	/**
	 * Removes all the cp tax categories where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cp tax categories where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp tax categories
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cp tax categories that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp tax categories that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			finderCache, new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder
		<CPTaxCategory, NoSuchCPTaxCategoryException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cp tax categories where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPTaxCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp tax categories
	 * @param end the upper bound of the range of cp tax categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp tax categories
	 */
	@Override
	public List<CPTaxCategory> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPTaxCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp tax category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp tax category
	 * @throws NoSuchCPTaxCategoryException if a matching cp tax category could not be found
	 */
	@Override
	public CPTaxCategory findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPTaxCategory> orderByComparator)
		throws NoSuchCPTaxCategoryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp tax category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp tax category, or <code>null</code> if a matching cp tax category could not be found
	 */
	@Override
	public CPTaxCategory fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPTaxCategory> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp tax categories that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPTaxCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp tax categories
	 * @param end the upper bound of the range of cp tax categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp tax categories that the user has permission to view
	 */
	@Override
	public List<CPTaxCategory> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPTaxCategory> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the cp tax categories where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of cp tax categories where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp tax categories
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of cp tax categories that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp tax categories that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			finderCache, new Object[] {uuid, companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<CPTaxCategory, NoSuchCPTaxCategoryException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the cp tax categories where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPTaxCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp tax categories
	 * @param end the upper bound of the range of cp tax categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp tax categories
	 */
	@Override
	public List<CPTaxCategory> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPTaxCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp tax category in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp tax category
	 * @throws NoSuchCPTaxCategoryException if a matching cp tax category could not be found
	 */
	@Override
	public CPTaxCategory findByCompanyId_First(
			long companyId, OrderByComparator<CPTaxCategory> orderByComparator)
		throws NoSuchCPTaxCategoryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp tax category in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp tax category, or <code>null</code> if a matching cp tax category could not be found
	 */
	@Override
	public CPTaxCategory fetchByCompanyId_First(
		long companyId, OrderByComparator<CPTaxCategory> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp tax categories that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPTaxCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp tax categories
	 * @param end the upper bound of the range of cp tax categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp tax categories that the user has permission to view
	 */
	@Override
	public List<CPTaxCategory> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPTaxCategory> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the cp tax categories where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of cp tax categories where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp tax categories
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of cp tax categories that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp tax categories that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	private UniquePersistenceFinder<CPTaxCategory, NoSuchCPTaxCategoryException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the cp tax category where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPTaxCategoryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp tax category
	 * @throws NoSuchCPTaxCategoryException if a matching cp tax category could not be found
	 */
	@Override
	public CPTaxCategory findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPTaxCategoryException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the cp tax category where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp tax category, or <code>null</code> if a matching cp tax category could not be found
	 */
	@Override
	public CPTaxCategory fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the cp tax category where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp tax category that was removed
	 */
	@Override
	public CPTaxCategory removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPTaxCategoryException {

		CPTaxCategory cpTaxCategory = findByERC_C(
			externalReferenceCode, companyId);

		return remove(cpTaxCategory);
	}

	/**
	 * Returns the number of cp tax categories where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp tax categories
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CPTaxCategoryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPTaxCategory.class);

		setModelImplClass(CPTaxCategoryImpl.class);
		setModelPKClass(long.class);

		setTable(CPTaxCategoryTable.INSTANCE);
	}

	/**
	 * Creates a new cp tax category with the primary key. Does not add the cp tax category to the database.
	 *
	 * @param CPTaxCategoryId the primary key for the new cp tax category
	 * @return the new cp tax category
	 */
	@Override
	public CPTaxCategory create(long CPTaxCategoryId) {
		CPTaxCategory cpTaxCategory = new CPTaxCategoryImpl();

		cpTaxCategory.setNew(true);
		cpTaxCategory.setPrimaryKey(CPTaxCategoryId);

		String uuid = PortalUUIDUtil.generate();

		cpTaxCategory.setUuid(uuid);

		cpTaxCategory.setCompanyId(CompanyThreadLocal.getCompanyId());

		return cpTaxCategory;
	}

	/**
	 * Removes the cp tax category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPTaxCategoryId the primary key of the cp tax category
	 * @return the cp tax category that was removed
	 * @throws NoSuchCPTaxCategoryException if a cp tax category with the primary key could not be found
	 */
	@Override
	public CPTaxCategory remove(long CPTaxCategoryId)
		throws NoSuchCPTaxCategoryException {

		return remove((Serializable)CPTaxCategoryId);
	}

	@Override
	protected CPTaxCategory removeImpl(CPTaxCategory cpTaxCategory) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpTaxCategory)) {
				cpTaxCategory = (CPTaxCategory)session.get(
					CPTaxCategoryImpl.class, cpTaxCategory.getPrimaryKeyObj());
			}

			if ((cpTaxCategory != null) &&
				ctPersistenceHelper.isRemove(cpTaxCategory)) {

				session.delete(cpTaxCategory);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpTaxCategory != null) {
			clearCache(cpTaxCategory);
		}

		return cpTaxCategory;
	}

	@Override
	public CPTaxCategory updateImpl(CPTaxCategory cpTaxCategory) {
		boolean isNew = cpTaxCategory.isNew();

		if (!(cpTaxCategory instanceof CPTaxCategoryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpTaxCategory.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpTaxCategory);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpTaxCategory proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPTaxCategory implementation " +
					cpTaxCategory.getClass());
		}

		CPTaxCategoryModelImpl cpTaxCategoryModelImpl =
			(CPTaxCategoryModelImpl)cpTaxCategory;

		if (Validator.isNull(cpTaxCategory.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpTaxCategory.setUuid(uuid);
		}

		if (Validator.isNull(cpTaxCategory.getExternalReferenceCode())) {
			cpTaxCategory.setExternalReferenceCode(cpTaxCategory.getUuid());
		}
		else {
			if (!Objects.equals(
					cpTaxCategoryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					cpTaxCategory.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = cpTaxCategory.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = cpTaxCategory.getPrimaryKey();
					}

					try {
						cpTaxCategory.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CPTaxCategory.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								cpTaxCategory.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CPTaxCategory ercCPTaxCategory = fetchByERC_C(
				cpTaxCategory.getExternalReferenceCode(),
				cpTaxCategory.getCompanyId());

			if (isNew) {
				if (ercCPTaxCategory != null) {
					throw new DuplicateCPTaxCategoryExternalReferenceCodeException(
						"Duplicate cp tax category with external reference code " +
							cpTaxCategory.getExternalReferenceCode() +
								" and company " + cpTaxCategory.getCompanyId());
				}
			}
			else {
				if ((ercCPTaxCategory != null) &&
					(cpTaxCategory.getCPTaxCategoryId() !=
						ercCPTaxCategory.getCPTaxCategoryId())) {

					throw new DuplicateCPTaxCategoryExternalReferenceCodeException(
						"Duplicate cp tax category with external reference code " +
							cpTaxCategory.getExternalReferenceCode() +
								" and company " + cpTaxCategory.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpTaxCategory.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpTaxCategory.setCreateDate(date);
			}
			else {
				cpTaxCategory.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!cpTaxCategoryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpTaxCategory.setModifiedDate(date);
			}
			else {
				cpTaxCategory.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cpTaxCategory)) {
				if (!isNew) {
					session.evict(
						CPTaxCategoryImpl.class,
						cpTaxCategory.getPrimaryKeyObj());
				}

				session.save(cpTaxCategory);
			}
			else {
				cpTaxCategory = (CPTaxCategory)session.merge(cpTaxCategory);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cpTaxCategory, false);

		if (isNew) {
			cpTaxCategory.setNew(false);
		}

		cpTaxCategory.resetOriginalValues();

		return cpTaxCategory;
	}

	/**
	 * Returns the cp tax category with the primary key or throws a <code>NoSuchCPTaxCategoryException</code> if it could not be found.
	 *
	 * @param CPTaxCategoryId the primary key of the cp tax category
	 * @return the cp tax category
	 * @throws NoSuchCPTaxCategoryException if a cp tax category with the primary key could not be found
	 */
	@Override
	public CPTaxCategory findByPrimaryKey(long CPTaxCategoryId)
		throws NoSuchCPTaxCategoryException {

		return findByPrimaryKey((Serializable)CPTaxCategoryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cp tax category with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPTaxCategoryId the primary key of the cp tax category
	 * @return the cp tax category, or <code>null</code> if a cp tax category with the primary key could not be found
	 */
	@Override
	public CPTaxCategory fetchByPrimaryKey(long CPTaxCategoryId) {
		return fetchByPrimaryKey((Serializable)CPTaxCategoryId);
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
		return "CPTaxCategoryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPTAXCATEGORY;
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
		return CPTaxCategoryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPTaxCategory";
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
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CPTaxCategoryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the cp tax category persistence.
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
				_SQL_SELECT_CPTAXCATEGORY_WHERE, _SQL_COUNT_CPTAXCATEGORY_WHERE,
				CPTaxCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"cpTaxCategory.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, CPTaxCategory::getUuid));

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
				_SQL_SELECT_CPTAXCATEGORY_WHERE, _SQL_COUNT_CPTAXCATEGORY_WHERE,
				CPTaxCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"cpTaxCategory.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, CPTaxCategory::getUuid),
				new FinderColumn<>(
					"cpTaxCategory.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, CPTaxCategory::getCompanyId));

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
				_SQL_SELECT_CPTAXCATEGORY_WHERE, _SQL_COUNT_CPTAXCATEGORY_WHERE,
				CPTaxCategoryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"cpTaxCategory.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, CPTaxCategory::getCompanyId));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(CPTaxCategory::getExternalReferenceCode),
				CPTaxCategory::getCompanyId),
			_SQL_SELECT_CPTAXCATEGORY_WHERE, "",
			new FinderColumn<>(
				"cpTaxCategory.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CPTaxCategory::getExternalReferenceCode),
			new FinderColumn<>(
				"cpTaxCategory.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CPTaxCategory::getCompanyId));

		CPTaxCategoryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPTaxCategoryUtil.setPersistence(null);

		entityCache.removeCache(CPTaxCategoryImpl.class.getName());
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
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CPTaxCategoryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPTAXCATEGORY =
		"SELECT cpTaxCategory FROM CPTaxCategory cpTaxCategory";

	private static final String _SQL_SELECT_CPTAXCATEGORY_WHERE =
		"SELECT cpTaxCategory FROM CPTaxCategory cpTaxCategory WHERE ";

	private static final String _SQL_COUNT_CPTAXCATEGORY_WHERE =
		"SELECT COUNT(cpTaxCategory) FROM CPTaxCategory cpTaxCategory WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CPTaxCategory exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CPTaxCategoryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:293007225