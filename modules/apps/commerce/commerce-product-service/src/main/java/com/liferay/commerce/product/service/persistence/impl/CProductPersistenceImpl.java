/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.DuplicateCProductExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCProductException;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CProductTable;
import com.liferay.commerce.product.model.impl.CProductImpl;
import com.liferay.commerce.product.model.impl.CProductModelImpl;
import com.liferay.commerce.product.service.persistence.CProductPersistence;
import com.liferay.commerce.product.service.persistence.CProductUtil;
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
 * The persistence implementation for the c product service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CProductPersistence.class)
public class CProductPersistenceImpl
	extends BasePersistenceImpl<CProduct, NoSuchCProductException>
	implements CProductPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CProductUtil</code> to access the c product persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CProductImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<CProduct, NoSuchCProductException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the c products where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CProductModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of c products
	 * @param end the upper bound of the range of c products (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching c products
	 */
	@Override
	public List<CProduct> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CProduct> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first c product in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching c product
	 * @throws NoSuchCProductException if a matching c product could not be found
	 */
	@Override
	public CProduct findByUuid_First(
			String uuid, OrderByComparator<CProduct> orderByComparator)
		throws NoSuchCProductException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first c product in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching c product, or <code>null</code> if a matching c product could not be found
	 */
	@Override
	public CProduct fetchByUuid_First(
		String uuid, OrderByComparator<CProduct> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the c products where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of c products where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching c products
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<CProduct, NoSuchCProductException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the c product where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCProductException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching c product
	 * @throws NoSuchCProductException if a matching c product could not be found
	 */
	@Override
	public CProduct findByUUID_G(String uuid, long groupId)
		throws NoSuchCProductException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the c product where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching c product, or <code>null</code> if a matching c product could not be found
	 */
	@Override
	public CProduct fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the c product where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the c product that was removed
	 */
	@Override
	public CProduct removeByUUID_G(String uuid, long groupId)
		throws NoSuchCProductException {

		CProduct cProduct = findByUUID_G(uuid, groupId);

		return remove(cProduct);
	}

	/**
	 * Returns the number of c products where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching c products
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<CProduct, NoSuchCProductException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the c products where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CProductModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of c products
	 * @param end the upper bound of the range of c products (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching c products
	 */
	@Override
	public List<CProduct> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CProduct> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first c product in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching c product
	 * @throws NoSuchCProductException if a matching c product could not be found
	 */
	@Override
	public CProduct findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CProduct> orderByComparator)
		throws NoSuchCProductException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first c product in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching c product, or <code>null</code> if a matching c product could not be found
	 */
	@Override
	public CProduct fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CProduct> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the c products where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of c products where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching c products
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<CProduct, NoSuchCProductException>
		_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the c products where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CProductModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of c products
	 * @param end the upper bound of the range of c products (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching c products
	 */
	@Override
	public List<CProduct> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CProduct> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first c product in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching c product
	 * @throws NoSuchCProductException if a matching c product could not be found
	 */
	@Override
	public CProduct findByGroupId_First(
			long groupId, OrderByComparator<CProduct> orderByComparator)
		throws NoSuchCProductException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first c product in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching c product, or <code>null</code> if a matching c product could not be found
	 */
	@Override
	public CProduct fetchByGroupId_First(
		long groupId, OrderByComparator<CProduct> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the c products where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of c products where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching c products
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private UniquePersistenceFinder<CProduct, NoSuchCProductException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the c product where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCProductException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching c product
	 * @throws NoSuchCProductException if a matching c product could not be found
	 */
	@Override
	public CProduct findByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchCProductException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the c product where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching c product, or <code>null</code> if a matching c product could not be found
	 */
	@Override
	public CProduct fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the c product where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the c product that was removed
	 */
	@Override
	public CProduct removeByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchCProductException {

		CProduct cProduct = findByERC_C(externalReferenceCode, companyId);

		return remove(cProduct);
	}

	/**
	 * Returns the number of c products where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching c products
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CProductPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CProduct.class);

		setModelImplClass(CProductImpl.class);
		setModelPKClass(long.class);

		setTable(CProductTable.INSTANCE);
	}

	/**
	 * Creates a new c product with the primary key. Does not add the c product to the database.
	 *
	 * @param CProductId the primary key for the new c product
	 * @return the new c product
	 */
	@Override
	public CProduct create(long CProductId) {
		CProduct cProduct = new CProductImpl();

		cProduct.setNew(true);
		cProduct.setPrimaryKey(CProductId);

		String uuid = PortalUUIDUtil.generate();

		cProduct.setUuid(uuid);

		cProduct.setCompanyId(CompanyThreadLocal.getCompanyId());

		return cProduct;
	}

	/**
	 * Removes the c product with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CProductId the primary key of the c product
	 * @return the c product that was removed
	 * @throws NoSuchCProductException if a c product with the primary key could not be found
	 */
	@Override
	public CProduct remove(long CProductId) throws NoSuchCProductException {
		return remove((Serializable)CProductId);
	}

	@Override
	protected CProduct removeImpl(CProduct cProduct) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cProduct)) {
				cProduct = (CProduct)session.get(
					CProductImpl.class, cProduct.getPrimaryKeyObj());
			}

			if ((cProduct != null) && ctPersistenceHelper.isRemove(cProduct)) {
				session.delete(cProduct);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cProduct != null) {
			clearCache(cProduct);
		}

		return cProduct;
	}

	@Override
	public CProduct updateImpl(CProduct cProduct) {
		boolean isNew = cProduct.isNew();

		if (!(cProduct instanceof CProductModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cProduct.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(cProduct);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cProduct proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CProduct implementation " +
					cProduct.getClass());
		}

		CProductModelImpl cProductModelImpl = (CProductModelImpl)cProduct;

		if (Validator.isNull(cProduct.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cProduct.setUuid(uuid);
		}

		if (Validator.isNull(cProduct.getExternalReferenceCode())) {
			cProduct.setExternalReferenceCode(cProduct.getUuid());
		}
		else {
			if (!Objects.equals(
					cProductModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					cProduct.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = cProduct.getCompanyId();

					long groupId = cProduct.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = cProduct.getPrimaryKey();
					}

					try {
						cProduct.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CProduct.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								cProduct.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CProduct ercCProduct = fetchByERC_C(
				cProduct.getExternalReferenceCode(), cProduct.getCompanyId());

			if (isNew) {
				if (ercCProduct != null) {
					throw new DuplicateCProductExternalReferenceCodeException(
						"Duplicate c product with external reference code " +
							cProduct.getExternalReferenceCode() +
								" and company " + cProduct.getCompanyId());
				}
			}
			else {
				if ((ercCProduct != null) &&
					(cProduct.getCProductId() != ercCProduct.getCProductId())) {

					throw new DuplicateCProductExternalReferenceCodeException(
						"Duplicate c product with external reference code " +
							cProduct.getExternalReferenceCode() +
								" and company " + cProduct.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cProduct.getCreateDate() == null)) {
			if (serviceContext == null) {
				cProduct.setCreateDate(date);
			}
			else {
				cProduct.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!cProductModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cProduct.setModifiedDate(date);
			}
			else {
				cProduct.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cProduct)) {
				if (!isNew) {
					session.evict(
						CProductImpl.class, cProduct.getPrimaryKeyObj());
				}

				session.save(cProduct);
			}
			else {
				cProduct = (CProduct)session.merge(cProduct);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cProduct, false);

		if (isNew) {
			cProduct.setNew(false);
		}

		cProduct.resetOriginalValues();

		return cProduct;
	}

	/**
	 * Returns the c product with the primary key or throws a <code>NoSuchCProductException</code> if it could not be found.
	 *
	 * @param CProductId the primary key of the c product
	 * @return the c product
	 * @throws NoSuchCProductException if a c product with the primary key could not be found
	 */
	@Override
	public CProduct findByPrimaryKey(long CProductId)
		throws NoSuchCProductException {

		return findByPrimaryKey((Serializable)CProductId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the c product with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CProductId the primary key of the c product
	 * @return the c product, or <code>null</code> if a c product with the primary key could not be found
	 */
	@Override
	public CProduct fetchByPrimaryKey(long CProductId) {
		return fetchByPrimaryKey((Serializable)CProductId);
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
		return "CProductId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPRODUCT;
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
		return CProductModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CProduct";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("publishedCPDefinitionId");
		ctMergeColumnNames.add("latestVersion");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("CProductId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the c product persistence.
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
			_SQL_SELECT_CPRODUCT_WHERE, _SQL_COUNT_CPRODUCT_WHERE,
			CProductModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"cProduct.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, CProduct::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CProduct::getUuid), CProduct::getGroupId),
			_SQL_SELECT_CPRODUCT_WHERE, "",
			new FinderColumn<>(
				"cProduct.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, CProduct::getUuid),
			new FinderColumn<>(
				"cProduct.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				CProduct::getGroupId));

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
				_SQL_SELECT_CPRODUCT_WHERE, _SQL_COUNT_CPRODUCT_WHERE,
				CProductModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cProduct.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, CProduct::getUuid),
				new FinderColumn<>(
					"cProduct.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, CProduct::getCompanyId));

		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
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
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_CPRODUCT_WHERE, _SQL_COUNT_CPRODUCT_WHERE,
				CProductModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cProduct.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, CProduct::getGroupId));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false, convertNullFunction(CProduct::getExternalReferenceCode),
				CProduct::getCompanyId),
			_SQL_SELECT_CPRODUCT_WHERE, "",
			new FinderColumn<>(
				"cProduct.", "externalReferenceCode", FinderColumn.Type.STRING,
				"=", true, true, CProduct::getExternalReferenceCode),
			new FinderColumn<>(
				"cProduct.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, CProduct::getCompanyId));

		CProductUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CProductUtil.setPersistence(null);

		entityCache.removeCache(CProductImpl.class.getName());
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
		CProductModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPRODUCT =
		"SELECT cProduct FROM CProduct cProduct";

	private static final String _SQL_SELECT_CPRODUCT_WHERE =
		"SELECT cProduct FROM CProduct cProduct WHERE ";

	private static final String _SQL_COUNT_CPRODUCT_WHERE =
		"SELECT COUNT(cProduct) FROM CProduct cProduct WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CProduct exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CProductPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1289829730