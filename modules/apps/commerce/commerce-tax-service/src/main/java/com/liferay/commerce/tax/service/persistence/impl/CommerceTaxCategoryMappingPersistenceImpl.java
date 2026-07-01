/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.service.persistence.impl;

import com.liferay.commerce.tax.exception.DuplicateCommerceTaxCategoryMappingExternalReferenceCodeException;
import com.liferay.commerce.tax.exception.NoSuchTaxCategoryMappingException;
import com.liferay.commerce.tax.model.CommerceTaxCategoryMapping;
import com.liferay.commerce.tax.model.CommerceTaxCategoryMappingTable;
import com.liferay.commerce.tax.model.impl.CommerceTaxCategoryMappingImpl;
import com.liferay.commerce.tax.model.impl.CommerceTaxCategoryMappingModelImpl;
import com.liferay.commerce.tax.service.persistence.CommerceTaxCategoryMappingPersistence;
import com.liferay.commerce.tax.service.persistence.CommerceTaxCategoryMappingUtil;
import com.liferay.commerce.tax.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce tax category mapping service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CommerceTaxCategoryMappingPersistence.class)
public class CommerceTaxCategoryMappingPersistenceImpl
	extends BasePersistenceImpl
		<CommerceTaxCategoryMapping, NoSuchTaxCategoryMappingException>
	implements CommerceTaxCategoryMappingPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceTaxCategoryMappingUtil</code> to access the commerce tax category mapping persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceTaxCategoryMappingImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceTaxCategoryMapping, NoSuchTaxCategoryMappingException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce tax category mappings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTaxCategoryMappingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce tax category mappings
	 * @param end the upper bound of the range of commerce tax category mappings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tax category mappings
	 */
	@Override
	public List<CommerceTaxCategoryMapping> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceTaxCategoryMapping> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce tax category mapping in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax category mapping
	 * @throws NoSuchTaxCategoryMappingException if a matching commerce tax category mapping could not be found
	 */
	@Override
	public CommerceTaxCategoryMapping findByUuid_First(
			String uuid,
			OrderByComparator<CommerceTaxCategoryMapping> orderByComparator)
		throws NoSuchTaxCategoryMappingException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce tax category mapping in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax category mapping, or <code>null</code> if a matching commerce tax category mapping could not be found
	 */
	@Override
	public CommerceTaxCategoryMapping fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceTaxCategoryMapping> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce tax category mappings where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce tax category mappings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce tax category mappings
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CommerceTaxCategoryMapping, NoSuchTaxCategoryMappingException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the commerce tax category mapping where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchTaxCategoryMappingException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce tax category mapping
	 * @throws NoSuchTaxCategoryMappingException if a matching commerce tax category mapping could not be found
	 */
	@Override
	public CommerceTaxCategoryMapping findByUUID_G(String uuid, long groupId)
		throws NoSuchTaxCategoryMappingException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the commerce tax category mapping where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce tax category mapping, or <code>null</code> if a matching commerce tax category mapping could not be found
	 */
	@Override
	public CommerceTaxCategoryMapping fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the commerce tax category mapping where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce tax category mapping that was removed
	 */
	@Override
	public CommerceTaxCategoryMapping removeByUUID_G(String uuid, long groupId)
		throws NoSuchTaxCategoryMappingException {

		CommerceTaxCategoryMapping commerceTaxCategoryMapping = findByUUID_G(
			uuid, groupId);

		return remove(commerceTaxCategoryMapping);
	}

	/**
	 * Returns the number of commerce tax category mappings where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce tax category mappings
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CommerceTaxCategoryMapping, NoSuchTaxCategoryMappingException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce tax category mappings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTaxCategoryMappingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce tax category mappings
	 * @param end the upper bound of the range of commerce tax category mappings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tax category mappings
	 */
	@Override
	public List<CommerceTaxCategoryMapping> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceTaxCategoryMapping> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce tax category mapping in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax category mapping
	 * @throws NoSuchTaxCategoryMappingException if a matching commerce tax category mapping could not be found
	 */
	@Override
	public CommerceTaxCategoryMapping findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceTaxCategoryMapping> orderByComparator)
		throws NoSuchTaxCategoryMappingException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce tax category mapping in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax category mapping, or <code>null</code> if a matching commerce tax category mapping could not be found
	 */
	@Override
	public CommerceTaxCategoryMapping fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceTaxCategoryMapping> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce tax category mappings where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce tax category mappings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce tax category mappings
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommerceTaxCategoryMapping, NoSuchTaxCategoryMappingException>
			_collectionPersistenceFinderByCommerceTaxMethodId;

	/**
	 * Returns an ordered range of all the commerce tax category mappings where commerceTaxMethodId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTaxCategoryMappingModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param start the lower bound of the range of commerce tax category mappings
	 * @param end the upper bound of the range of commerce tax category mappings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tax category mappings
	 */
	@Override
	public List<CommerceTaxCategoryMapping> findByCommerceTaxMethodId(
		long commerceTaxMethodId, int start, int end,
		OrderByComparator<CommerceTaxCategoryMapping> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceTaxMethodId.find(
			finderCache, new Object[] {commerceTaxMethodId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce tax category mapping in the ordered set where commerceTaxMethodId = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax category mapping
	 * @throws NoSuchTaxCategoryMappingException if a matching commerce tax category mapping could not be found
	 */
	@Override
	public CommerceTaxCategoryMapping findByCommerceTaxMethodId_First(
			long commerceTaxMethodId,
			OrderByComparator<CommerceTaxCategoryMapping> orderByComparator)
		throws NoSuchTaxCategoryMappingException {

		return _collectionPersistenceFinderByCommerceTaxMethodId.findFirst(
			finderCache, new Object[] {commerceTaxMethodId}, orderByComparator);
	}

	/**
	 * Returns the first commerce tax category mapping in the ordered set where commerceTaxMethodId = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax category mapping, or <code>null</code> if a matching commerce tax category mapping could not be found
	 */
	@Override
	public CommerceTaxCategoryMapping fetchByCommerceTaxMethodId_First(
		long commerceTaxMethodId,
		OrderByComparator<CommerceTaxCategoryMapping> orderByComparator) {

		return _collectionPersistenceFinderByCommerceTaxMethodId.fetchFirst(
			finderCache, new Object[] {commerceTaxMethodId}, orderByComparator);
	}

	/**
	 * Removes all the commerce tax category mappings where commerceTaxMethodId = &#63; from the database.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 */
	@Override
	public void removeByCommerceTaxMethodId(long commerceTaxMethodId) {
		_collectionPersistenceFinderByCommerceTaxMethodId.remove(
			finderCache, new Object[] {commerceTaxMethodId});
	}

	/**
	 * Returns the number of commerce tax category mappings where commerceTaxMethodId = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @return the number of matching commerce tax category mappings
	 */
	@Override
	public int countByCommerceTaxMethodId(long commerceTaxMethodId) {
		return _collectionPersistenceFinderByCommerceTaxMethodId.count(
			finderCache, new Object[] {commerceTaxMethodId});
	}

	private UniquePersistenceFinder
		<CommerceTaxCategoryMapping, NoSuchTaxCategoryMappingException>
			_uniquePersistenceFinderByC_C;

	/**
	 * Returns the commerce tax category mapping where commerceTaxMethodId = &#63; and CPTaxCategoryId = &#63; or throws a <code>NoSuchTaxCategoryMappingException</code> if it could not be found.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param CPTaxCategoryId the cp tax category ID
	 * @return the matching commerce tax category mapping
	 * @throws NoSuchTaxCategoryMappingException if a matching commerce tax category mapping could not be found
	 */
	@Override
	public CommerceTaxCategoryMapping findByC_C(
			long commerceTaxMethodId, long CPTaxCategoryId)
		throws NoSuchTaxCategoryMappingException {

		return _uniquePersistenceFinderByC_C.find(
			finderCache, new Object[] {commerceTaxMethodId, CPTaxCategoryId});
	}

	/**
	 * Returns the commerce tax category mapping where commerceTaxMethodId = &#63; and CPTaxCategoryId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce tax category mapping, or <code>null</code> if a matching commerce tax category mapping could not be found
	 */
	@Override
	public CommerceTaxCategoryMapping fetchByC_C(
		long commerceTaxMethodId, long CPTaxCategoryId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C.fetch(
			finderCache, new Object[] {commerceTaxMethodId, CPTaxCategoryId},
			useFinderCache);
	}

	/**
	 * Removes the commerce tax category mapping where commerceTaxMethodId = &#63; and CPTaxCategoryId = &#63; from the database.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param CPTaxCategoryId the cp tax category ID
	 * @return the commerce tax category mapping that was removed
	 */
	@Override
	public CommerceTaxCategoryMapping removeByC_C(
			long commerceTaxMethodId, long CPTaxCategoryId)
		throws NoSuchTaxCategoryMappingException {

		CommerceTaxCategoryMapping commerceTaxCategoryMapping = findByC_C(
			commerceTaxMethodId, CPTaxCategoryId);

		return remove(commerceTaxCategoryMapping);
	}

	/**
	 * Returns the number of commerce tax category mappings where commerceTaxMethodId = &#63; and CPTaxCategoryId = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param CPTaxCategoryId the cp tax category ID
	 * @return the number of matching commerce tax category mappings
	 */
	@Override
	public int countByC_C(long commerceTaxMethodId, long CPTaxCategoryId) {
		return _uniquePersistenceFinderByC_C.count(
			finderCache, new Object[] {commerceTaxMethodId, CPTaxCategoryId});
	}

	private UniquePersistenceFinder
		<CommerceTaxCategoryMapping, NoSuchTaxCategoryMappingException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the commerce tax category mapping where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchTaxCategoryMappingException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce tax category mapping
	 * @throws NoSuchTaxCategoryMappingException if a matching commerce tax category mapping could not be found
	 */
	@Override
	public CommerceTaxCategoryMapping findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchTaxCategoryMappingException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the commerce tax category mapping where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce tax category mapping, or <code>null</code> if a matching commerce tax category mapping could not be found
	 */
	@Override
	public CommerceTaxCategoryMapping fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the commerce tax category mapping where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce tax category mapping that was removed
	 */
	@Override
	public CommerceTaxCategoryMapping removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchTaxCategoryMappingException {

		CommerceTaxCategoryMapping commerceTaxCategoryMapping = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commerceTaxCategoryMapping);
	}

	/**
	 * Returns the number of commerce tax category mappings where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce tax category mappings
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CommerceTaxCategoryMappingPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceTaxCategoryMapping.class);

		setModelImplClass(CommerceTaxCategoryMappingImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceTaxCategoryMappingTable.INSTANCE);
	}

	/**
	 * Creates a new commerce tax category mapping with the primary key. Does not add the commerce tax category mapping to the database.
	 *
	 * @param commerceTaxCategoryMappingId the primary key for the new commerce tax category mapping
	 * @return the new commerce tax category mapping
	 */
	@Override
	public CommerceTaxCategoryMapping create(
		long commerceTaxCategoryMappingId) {

		CommerceTaxCategoryMapping commerceTaxCategoryMapping =
			new CommerceTaxCategoryMappingImpl();

		commerceTaxCategoryMapping.setNew(true);
		commerceTaxCategoryMapping.setPrimaryKey(commerceTaxCategoryMappingId);

		String uuid = PortalUUIDUtil.generate();

		commerceTaxCategoryMapping.setUuid(uuid);

		commerceTaxCategoryMapping.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceTaxCategoryMapping;
	}

	/**
	 * Removes the commerce tax category mapping with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceTaxCategoryMappingId the primary key of the commerce tax category mapping
	 * @return the commerce tax category mapping that was removed
	 * @throws NoSuchTaxCategoryMappingException if a commerce tax category mapping with the primary key could not be found
	 */
	@Override
	public CommerceTaxCategoryMapping remove(long commerceTaxCategoryMappingId)
		throws NoSuchTaxCategoryMappingException {

		return remove((Serializable)commerceTaxCategoryMappingId);
	}

	@Override
	protected CommerceTaxCategoryMapping removeImpl(
		CommerceTaxCategoryMapping commerceTaxCategoryMapping) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceTaxCategoryMapping)) {
				commerceTaxCategoryMapping =
					(CommerceTaxCategoryMapping)session.get(
						CommerceTaxCategoryMappingImpl.class,
						commerceTaxCategoryMapping.getPrimaryKeyObj());
			}

			if (commerceTaxCategoryMapping != null) {
				session.delete(commerceTaxCategoryMapping);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceTaxCategoryMapping != null) {
			clearCache(commerceTaxCategoryMapping);
		}

		return commerceTaxCategoryMapping;
	}

	@Override
	public CommerceTaxCategoryMapping updateImpl(
		CommerceTaxCategoryMapping commerceTaxCategoryMapping) {

		boolean isNew = commerceTaxCategoryMapping.isNew();

		if (!(commerceTaxCategoryMapping instanceof
				CommerceTaxCategoryMappingModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceTaxCategoryMapping.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceTaxCategoryMapping);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceTaxCategoryMapping proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceTaxCategoryMapping implementation " +
					commerceTaxCategoryMapping.getClass());
		}

		CommerceTaxCategoryMappingModelImpl
			commerceTaxCategoryMappingModelImpl =
				(CommerceTaxCategoryMappingModelImpl)commerceTaxCategoryMapping;

		if (Validator.isNull(commerceTaxCategoryMapping.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceTaxCategoryMapping.setUuid(uuid);
		}

		if (Validator.isNull(
				commerceTaxCategoryMapping.getExternalReferenceCode())) {

			commerceTaxCategoryMapping.setExternalReferenceCode(
				commerceTaxCategoryMapping.getUuid());
		}
		else {
			if (!Objects.equals(
					commerceTaxCategoryMappingModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commerceTaxCategoryMapping.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commerceTaxCategoryMapping.getCompanyId();

					long groupId = commerceTaxCategoryMapping.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = commerceTaxCategoryMapping.getPrimaryKey();
					}

					try {
						commerceTaxCategoryMapping.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommerceTaxCategoryMapping.class.getName(),
								classPK, ContentTypes.TEXT_HTML,
								Sanitizer.MODE_ALL,
								commerceTaxCategoryMapping.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommerceTaxCategoryMapping ercCommerceTaxCategoryMapping =
				fetchByERC_C(
					commerceTaxCategoryMapping.getExternalReferenceCode(),
					commerceTaxCategoryMapping.getCompanyId());

			if (isNew) {
				if (ercCommerceTaxCategoryMapping != null) {
					throw new DuplicateCommerceTaxCategoryMappingExternalReferenceCodeException(
						"Duplicate commerce tax category mapping with external reference code " +
							commerceTaxCategoryMapping.
								getExternalReferenceCode() + " and company " +
									commerceTaxCategoryMapping.getCompanyId());
				}
			}
			else {
				if ((ercCommerceTaxCategoryMapping != null) &&
					(commerceTaxCategoryMapping.
						getCommerceTaxCategoryMappingId() !=
							ercCommerceTaxCategoryMapping.
								getCommerceTaxCategoryMappingId())) {

					throw new DuplicateCommerceTaxCategoryMappingExternalReferenceCodeException(
						"Duplicate commerce tax category mapping with external reference code " +
							commerceTaxCategoryMapping.
								getExternalReferenceCode() + " and company " +
									commerceTaxCategoryMapping.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceTaxCategoryMapping.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceTaxCategoryMapping.setCreateDate(date);
			}
			else {
				commerceTaxCategoryMapping.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceTaxCategoryMappingModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceTaxCategoryMapping.setModifiedDate(date);
			}
			else {
				commerceTaxCategoryMapping.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceTaxCategoryMapping);
			}
			else {
				commerceTaxCategoryMapping =
					(CommerceTaxCategoryMapping)session.merge(
						commerceTaxCategoryMapping);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceTaxCategoryMapping, false);

		if (isNew) {
			commerceTaxCategoryMapping.setNew(false);
		}

		commerceTaxCategoryMapping.resetOriginalValues();

		return commerceTaxCategoryMapping;
	}

	/**
	 * Returns the commerce tax category mapping with the primary key or throws a <code>NoSuchTaxCategoryMappingException</code> if it could not be found.
	 *
	 * @param commerceTaxCategoryMappingId the primary key of the commerce tax category mapping
	 * @return the commerce tax category mapping
	 * @throws NoSuchTaxCategoryMappingException if a commerce tax category mapping with the primary key could not be found
	 */
	@Override
	public CommerceTaxCategoryMapping findByPrimaryKey(
			long commerceTaxCategoryMappingId)
		throws NoSuchTaxCategoryMappingException {

		return findByPrimaryKey((Serializable)commerceTaxCategoryMappingId);
	}

	/**
	 * Returns the commerce tax category mapping with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceTaxCategoryMappingId the primary key of the commerce tax category mapping
	 * @return the commerce tax category mapping, or <code>null</code> if a commerce tax category mapping with the primary key could not be found
	 */
	@Override
	public CommerceTaxCategoryMapping fetchByPrimaryKey(
		long commerceTaxCategoryMappingId) {

		return fetchByPrimaryKey((Serializable)commerceTaxCategoryMappingId);
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
		return "commerceTaxCategoryMappingId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCETAXCATEGORYMAPPING;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceTaxCategoryMappingModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce tax category mapping persistence.
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
			_SQL_SELECT_COMMERCETAXCATEGORYMAPPING_WHERE,
			_SQL_COUNT_COMMERCETAXCATEGORYMAPPING_WHERE,
			CommerceTaxCategoryMappingModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"commerceTaxCategoryMapping.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceTaxCategoryMapping::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CommerceTaxCategoryMapping::getUuid),
				CommerceTaxCategoryMapping::getGroupId),
			_SQL_SELECT_COMMERCETAXCATEGORYMAPPING_WHERE, "",
			new FinderColumn<>(
				"commerceTaxCategoryMapping.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceTaxCategoryMapping::getUuid),
			new FinderColumn<>(
				"commerceTaxCategoryMapping.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceTaxCategoryMapping::getGroupId));

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
				_SQL_SELECT_COMMERCETAXCATEGORYMAPPING_WHERE,
				_SQL_COUNT_COMMERCETAXCATEGORYMAPPING_WHERE,
				CommerceTaxCategoryMappingModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceTaxCategoryMapping.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceTaxCategoryMapping::getUuid),
				new FinderColumn<>(
					"commerceTaxCategoryMapping.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceTaxCategoryMapping::getCompanyId));

		_collectionPersistenceFinderByCommerceTaxMethodId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceTaxMethodId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceTaxMethodId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceTaxMethodId",
					new String[] {Long.class.getName()},
					new String[] {"commerceTaxMethodId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceTaxMethodId",
					new String[] {Long.class.getName()},
					new String[] {"commerceTaxMethodId"}, false),
				_SQL_SELECT_COMMERCETAXCATEGORYMAPPING_WHERE,
				_SQL_COUNT_COMMERCETAXCATEGORYMAPPING_WHERE,
				CommerceTaxCategoryMappingModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceTaxCategoryMapping.", "commerceTaxMethodId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceTaxCategoryMapping::getCommerceTaxMethodId));

		_uniquePersistenceFinderByC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"commerceTaxMethodId", "CPTaxCategoryId"}, 0, 0,
				false, CommerceTaxCategoryMapping::getCommerceTaxMethodId,
				CommerceTaxCategoryMapping::getCPTaxCategoryId),
			_SQL_SELECT_COMMERCETAXCATEGORYMAPPING_WHERE, "",
			new FinderColumn<>(
				"commerceTaxCategoryMapping.", "commerceTaxMethodId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceTaxCategoryMapping::getCommerceTaxMethodId),
			new FinderColumn<>(
				"commerceTaxCategoryMapping.", "CPTaxCategoryId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceTaxCategoryMapping::getCPTaxCategoryId));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					CommerceTaxCategoryMapping::getExternalReferenceCode),
				CommerceTaxCategoryMapping::getCompanyId),
			_SQL_SELECT_COMMERCETAXCATEGORYMAPPING_WHERE, "",
			new FinderColumn<>(
				"commerceTaxCategoryMapping.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceTaxCategoryMapping::getExternalReferenceCode),
			new FinderColumn<>(
				"commerceTaxCategoryMapping.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceTaxCategoryMapping::getCompanyId));

		CommerceTaxCategoryMappingUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceTaxCategoryMappingUtil.setPersistence(null);

		entityCache.removeCache(CommerceTaxCategoryMappingImpl.class.getName());
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
		CommerceTaxCategoryMappingModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCETAXCATEGORYMAPPING =
		"SELECT commerceTaxCategoryMapping FROM CommerceTaxCategoryMapping commerceTaxCategoryMapping";

	private static final String _SQL_SELECT_COMMERCETAXCATEGORYMAPPING_WHERE =
		"SELECT commerceTaxCategoryMapping FROM CommerceTaxCategoryMapping commerceTaxCategoryMapping WHERE ";

	private static final String _SQL_COUNT_COMMERCETAXCATEGORYMAPPING_WHERE =
		"SELECT COUNT(commerceTaxCategoryMapping) FROM CommerceTaxCategoryMapping commerceTaxCategoryMapping WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-676389480