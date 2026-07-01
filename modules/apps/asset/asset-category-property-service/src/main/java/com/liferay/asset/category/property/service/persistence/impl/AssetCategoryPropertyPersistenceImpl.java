/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.category.property.service.persistence.impl;

import com.liferay.asset.category.property.exception.DuplicateAssetCategoryPropertyExternalReferenceCodeException;
import com.liferay.asset.category.property.exception.NoSuchCategoryPropertyException;
import com.liferay.asset.category.property.model.AssetCategoryProperty;
import com.liferay.asset.category.property.model.AssetCategoryPropertyTable;
import com.liferay.asset.category.property.model.impl.AssetCategoryPropertyImpl;
import com.liferay.asset.category.property.model.impl.AssetCategoryPropertyModelImpl;
import com.liferay.asset.category.property.service.persistence.AssetCategoryPropertyPersistence;
import com.liferay.asset.category.property.service.persistence.AssetCategoryPropertyUtil;
import com.liferay.asset.category.property.service.persistence.impl.constants.AssetPersistenceConstants;
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
 * The persistence implementation for the asset category property service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AssetCategoryPropertyPersistence.class)
public class AssetCategoryPropertyPersistenceImpl
	extends BasePersistenceImpl
		<AssetCategoryProperty, NoSuchCategoryPropertyException>
	implements AssetCategoryPropertyPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AssetCategoryPropertyUtil</code> to access the asset category property persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AssetCategoryPropertyImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AssetCategoryProperty, NoSuchCategoryPropertyException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the asset category properties where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryPropertyModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset category properties
	 * @param end the upper bound of the range of asset category properties (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset category properties
	 */
	@Override
	public List<AssetCategoryProperty> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AssetCategoryProperty> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category property in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category property
	 * @throws NoSuchCategoryPropertyException if a matching asset category property could not be found
	 */
	@Override
	public AssetCategoryProperty findByCompanyId_First(
			long companyId,
			OrderByComparator<AssetCategoryProperty> orderByComparator)
		throws NoSuchCategoryPropertyException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first asset category property in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category property, or <code>null</code> if a matching asset category property could not be found
	 */
	@Override
	public AssetCategoryProperty fetchByCompanyId_First(
		long companyId,
		OrderByComparator<AssetCategoryProperty> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the asset category properties where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of asset category properties where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching asset category properties
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<AssetCategoryProperty, NoSuchCategoryPropertyException>
			_collectionPersistenceFinderByCategoryId;

	/**
	 * Returns an ordered range of all the asset category properties where categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryPropertyModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of asset category properties
	 * @param end the upper bound of the range of asset category properties (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset category properties
	 */
	@Override
	public List<AssetCategoryProperty> findByCategoryId(
		long categoryId, int start, int end,
		OrderByComparator<AssetCategoryProperty> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCategoryId.find(
			finderCache, new Object[] {categoryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category property in the ordered set where categoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category property
	 * @throws NoSuchCategoryPropertyException if a matching asset category property could not be found
	 */
	@Override
	public AssetCategoryProperty findByCategoryId_First(
			long categoryId,
			OrderByComparator<AssetCategoryProperty> orderByComparator)
		throws NoSuchCategoryPropertyException {

		return _collectionPersistenceFinderByCategoryId.findFirst(
			finderCache, new Object[] {categoryId}, orderByComparator);
	}

	/**
	 * Returns the first asset category property in the ordered set where categoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category property, or <code>null</code> if a matching asset category property could not be found
	 */
	@Override
	public AssetCategoryProperty fetchByCategoryId_First(
		long categoryId,
		OrderByComparator<AssetCategoryProperty> orderByComparator) {

		return _collectionPersistenceFinderByCategoryId.fetchFirst(
			finderCache, new Object[] {categoryId}, orderByComparator);
	}

	/**
	 * Removes all the asset category properties where categoryId = &#63; from the database.
	 *
	 * @param categoryId the category ID
	 */
	@Override
	public void removeByCategoryId(long categoryId) {
		_collectionPersistenceFinderByCategoryId.remove(
			finderCache, new Object[] {categoryId});
	}

	/**
	 * Returns the number of asset category properties where categoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @return the number of matching asset category properties
	 */
	@Override
	public int countByCategoryId(long categoryId) {
		return _collectionPersistenceFinderByCategoryId.count(
			finderCache, new Object[] {categoryId});
	}

	private CollectionPersistenceFinder
		<AssetCategoryProperty, NoSuchCategoryPropertyException>
			_collectionPersistenceFinderByC_K;

	/**
	 * Returns an ordered range of all the asset category properties where companyId = &#63; and key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryPropertyModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @param start the lower bound of the range of asset category properties
	 * @param end the upper bound of the range of asset category properties (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset category properties
	 */
	@Override
	public List<AssetCategoryProperty> findByC_K(
		long companyId, String key, int start, int end,
		OrderByComparator<AssetCategoryProperty> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_K.find(
			finderCache, new Object[] {companyId, key}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category property in the ordered set where companyId = &#63; and key = &#63;.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category property
	 * @throws NoSuchCategoryPropertyException if a matching asset category property could not be found
	 */
	@Override
	public AssetCategoryProperty findByC_K_First(
			long companyId, String key,
			OrderByComparator<AssetCategoryProperty> orderByComparator)
		throws NoSuchCategoryPropertyException {

		return _collectionPersistenceFinderByC_K.findFirst(
			finderCache, new Object[] {companyId, key}, orderByComparator);
	}

	/**
	 * Returns the first asset category property in the ordered set where companyId = &#63; and key = &#63;.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category property, or <code>null</code> if a matching asset category property could not be found
	 */
	@Override
	public AssetCategoryProperty fetchByC_K_First(
		long companyId, String key,
		OrderByComparator<AssetCategoryProperty> orderByComparator) {

		return _collectionPersistenceFinderByC_K.fetchFirst(
			finderCache, new Object[] {companyId, key}, orderByComparator);
	}

	/**
	 * Removes all the asset category properties where companyId = &#63; and key = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 */
	@Override
	public void removeByC_K(long companyId, String key) {
		_collectionPersistenceFinderByC_K.remove(
			finderCache, new Object[] {companyId, key});
	}

	/**
	 * Returns the number of asset category properties where companyId = &#63; and key = &#63;.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @return the number of matching asset category properties
	 */
	@Override
	public int countByC_K(long companyId, String key) {
		return _collectionPersistenceFinderByC_K.count(
			finderCache, new Object[] {companyId, key});
	}

	private UniquePersistenceFinder
		<AssetCategoryProperty, NoSuchCategoryPropertyException>
			_uniquePersistenceFinderByCA_K;

	/**
	 * Returns the asset category property where categoryId = &#63; and key = &#63; or throws a <code>NoSuchCategoryPropertyException</code> if it could not be found.
	 *
	 * @param categoryId the category ID
	 * @param key the key
	 * @return the matching asset category property
	 * @throws NoSuchCategoryPropertyException if a matching asset category property could not be found
	 */
	@Override
	public AssetCategoryProperty findByCA_K(long categoryId, String key)
		throws NoSuchCategoryPropertyException {

		return _uniquePersistenceFinderByCA_K.find(
			finderCache, new Object[] {categoryId, key});
	}

	/**
	 * Returns the asset category property where categoryId = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param categoryId the category ID
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset category property, or <code>null</code> if a matching asset category property could not be found
	 */
	@Override
	public AssetCategoryProperty fetchByCA_K(
		long categoryId, String key, boolean useFinderCache) {

		return _uniquePersistenceFinderByCA_K.fetch(
			finderCache, new Object[] {categoryId, key}, useFinderCache);
	}

	/**
	 * Removes the asset category property where categoryId = &#63; and key = &#63; from the database.
	 *
	 * @param categoryId the category ID
	 * @param key the key
	 * @return the asset category property that was removed
	 */
	@Override
	public AssetCategoryProperty removeByCA_K(long categoryId, String key)
		throws NoSuchCategoryPropertyException {

		AssetCategoryProperty assetCategoryProperty = findByCA_K(
			categoryId, key);

		return remove(assetCategoryProperty);
	}

	/**
	 * Returns the number of asset category properties where categoryId = &#63; and key = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param key the key
	 * @return the number of matching asset category properties
	 */
	@Override
	public int countByCA_K(long categoryId, String key) {
		return _uniquePersistenceFinderByCA_K.count(
			finderCache, new Object[] {categoryId, key});
	}

	private UniquePersistenceFinder
		<AssetCategoryProperty, NoSuchCategoryPropertyException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the asset category property where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCategoryPropertyException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching asset category property
	 * @throws NoSuchCategoryPropertyException if a matching asset category property could not be found
	 */
	@Override
	public AssetCategoryProperty findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCategoryPropertyException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the asset category property where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset category property, or <code>null</code> if a matching asset category property could not be found
	 */
	@Override
	public AssetCategoryProperty fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the asset category property where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the asset category property that was removed
	 */
	@Override
	public AssetCategoryProperty removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCategoryPropertyException {

		AssetCategoryProperty assetCategoryProperty = findByERC_C(
			externalReferenceCode, companyId);

		return remove(assetCategoryProperty);
	}

	/**
	 * Returns the number of asset category properties where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching asset category properties
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public AssetCategoryPropertyPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("key", "key_");

		setDBColumnNames(dbColumnNames);

		setModelClass(AssetCategoryProperty.class);

		setModelImplClass(AssetCategoryPropertyImpl.class);
		setModelPKClass(long.class);

		setTable(AssetCategoryPropertyTable.INSTANCE);
	}

	/**
	 * Creates a new asset category property with the primary key. Does not add the asset category property to the database.
	 *
	 * @param categoryPropertyId the primary key for the new asset category property
	 * @return the new asset category property
	 */
	@Override
	public AssetCategoryProperty create(long categoryPropertyId) {
		AssetCategoryProperty assetCategoryProperty =
			new AssetCategoryPropertyImpl();

		assetCategoryProperty.setNew(true);
		assetCategoryProperty.setPrimaryKey(categoryPropertyId);

		assetCategoryProperty.setCompanyId(CompanyThreadLocal.getCompanyId());

		return assetCategoryProperty;
	}

	/**
	 * Removes the asset category property with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param categoryPropertyId the primary key of the asset category property
	 * @return the asset category property that was removed
	 * @throws NoSuchCategoryPropertyException if a asset category property with the primary key could not be found
	 */
	@Override
	public AssetCategoryProperty remove(long categoryPropertyId)
		throws NoSuchCategoryPropertyException {

		return remove((Serializable)categoryPropertyId);
	}

	@Override
	protected AssetCategoryProperty removeImpl(
		AssetCategoryProperty assetCategoryProperty) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(assetCategoryProperty)) {
				assetCategoryProperty = (AssetCategoryProperty)session.get(
					AssetCategoryPropertyImpl.class,
					assetCategoryProperty.getPrimaryKeyObj());
			}

			if ((assetCategoryProperty != null) &&
				ctPersistenceHelper.isRemove(assetCategoryProperty)) {

				session.delete(assetCategoryProperty);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (assetCategoryProperty != null) {
			clearCache(assetCategoryProperty);
		}

		return assetCategoryProperty;
	}

	@Override
	public AssetCategoryProperty updateImpl(
		AssetCategoryProperty assetCategoryProperty) {

		boolean isNew = assetCategoryProperty.isNew();

		if (!(assetCategoryProperty instanceof
				AssetCategoryPropertyModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(assetCategoryProperty.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					assetCategoryProperty);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in assetCategoryProperty proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AssetCategoryProperty implementation " +
					assetCategoryProperty.getClass());
		}

		AssetCategoryPropertyModelImpl assetCategoryPropertyModelImpl =
			(AssetCategoryPropertyModelImpl)assetCategoryProperty;

		if (Validator.isNull(
				assetCategoryProperty.getExternalReferenceCode())) {

			assetCategoryProperty.setExternalReferenceCode(
				String.valueOf(assetCategoryProperty.getPrimaryKey()));
		}
		else {
			if (!Objects.equals(
					assetCategoryPropertyModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					assetCategoryProperty.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = assetCategoryProperty.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = assetCategoryProperty.getPrimaryKey();
					}

					try {
						assetCategoryProperty.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								AssetCategoryProperty.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								assetCategoryProperty.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			AssetCategoryProperty ercAssetCategoryProperty = fetchByERC_C(
				assetCategoryProperty.getExternalReferenceCode(),
				assetCategoryProperty.getCompanyId());

			if (isNew) {
				if (ercAssetCategoryProperty != null) {
					throw new DuplicateAssetCategoryPropertyExternalReferenceCodeException(
						"Duplicate asset category property with external reference code " +
							assetCategoryProperty.getExternalReferenceCode() +
								" and company " +
									assetCategoryProperty.getCompanyId());
				}
			}
			else {
				if ((ercAssetCategoryProperty != null) &&
					(assetCategoryProperty.getCategoryPropertyId() !=
						ercAssetCategoryProperty.getCategoryPropertyId())) {

					throw new DuplicateAssetCategoryPropertyExternalReferenceCodeException(
						"Duplicate asset category property with external reference code " +
							assetCategoryProperty.getExternalReferenceCode() +
								" and company " +
									assetCategoryProperty.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (assetCategoryProperty.getCreateDate() == null)) {
			if (serviceContext == null) {
				assetCategoryProperty.setCreateDate(date);
			}
			else {
				assetCategoryProperty.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!assetCategoryPropertyModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				assetCategoryProperty.setModifiedDate(date);
			}
			else {
				assetCategoryProperty.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(assetCategoryProperty)) {
				if (!isNew) {
					session.evict(
						AssetCategoryPropertyImpl.class,
						assetCategoryProperty.getPrimaryKeyObj());
				}

				session.save(assetCategoryProperty);
			}
			else {
				assetCategoryProperty = (AssetCategoryProperty)session.merge(
					assetCategoryProperty);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(assetCategoryProperty, false);

		if (isNew) {
			assetCategoryProperty.setNew(false);
		}

		assetCategoryProperty.resetOriginalValues();

		return assetCategoryProperty;
	}

	/**
	 * Returns the asset category property with the primary key or throws a <code>NoSuchCategoryPropertyException</code> if it could not be found.
	 *
	 * @param categoryPropertyId the primary key of the asset category property
	 * @return the asset category property
	 * @throws NoSuchCategoryPropertyException if a asset category property with the primary key could not be found
	 */
	@Override
	public AssetCategoryProperty findByPrimaryKey(long categoryPropertyId)
		throws NoSuchCategoryPropertyException {

		return findByPrimaryKey((Serializable)categoryPropertyId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the asset category property with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param categoryPropertyId the primary key of the asset category property
	 * @return the asset category property, or <code>null</code> if a asset category property with the primary key could not be found
	 */
	@Override
	public AssetCategoryProperty fetchByPrimaryKey(long categoryPropertyId) {
		return fetchByPrimaryKey((Serializable)categoryPropertyId);
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
		return "categoryPropertyId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ASSETCATEGORYPROPERTY;
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
		return AssetCategoryPropertyModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AssetCategoryProperty";
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
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("categoryId");
		ctMergeColumnNames.add("key_");
		ctMergeColumnNames.add("value");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("categoryPropertyId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"categoryId", "key_"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the asset category property persistence.
	 */
	@Activate
	public void activate() {
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
				_SQL_SELECT_ASSETCATEGORYPROPERTY_WHERE,
				_SQL_COUNT_ASSETCATEGORYPROPERTY_WHERE,
				AssetCategoryPropertyModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"assetCategoryProperty.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetCategoryProperty::getCompanyId));

		_collectionPersistenceFinderByCategoryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCategoryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"categoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCategoryId", new String[] {Long.class.getName()},
					new String[] {"categoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCategoryId", new String[] {Long.class.getName()},
					new String[] {"categoryId"}, false),
				_SQL_SELECT_ASSETCATEGORYPROPERTY_WHERE,
				_SQL_COUNT_ASSETCATEGORYPROPERTY_WHERE,
				AssetCategoryPropertyModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"assetCategoryProperty.", "categoryId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetCategoryProperty::getCategoryId));

		_collectionPersistenceFinderByC_K = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_K",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "key_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_K",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "key_"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_K",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "key_"}, 0, 2, false, null),
			_SQL_SELECT_ASSETCATEGORYPROPERTY_WHERE,
			_SQL_COUNT_ASSETCATEGORYPROPERTY_WHERE,
			AssetCategoryPropertyModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"assetCategoryProperty.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, AssetCategoryProperty::getCompanyId),
			new FinderColumn<>(
				"assetCategoryProperty.", "key", "key_",
				FinderColumn.Type.STRING, "=", true, true,
				AssetCategoryProperty::getKey));

		_uniquePersistenceFinderByCA_K = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCA_K",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"categoryId", "key_"}, 0, 2, false,
				AssetCategoryProperty::getCategoryId,
				convertNullFunction(AssetCategoryProperty::getKey)),
			_SQL_SELECT_ASSETCATEGORYPROPERTY_WHERE, "",
			new FinderColumn<>(
				"assetCategoryProperty.", "categoryId", FinderColumn.Type.LONG,
				"=", true, true, AssetCategoryProperty::getCategoryId),
			new FinderColumn<>(
				"assetCategoryProperty.", "key", "key_",
				FinderColumn.Type.STRING, "=", true, true,
				AssetCategoryProperty::getKey));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					AssetCategoryProperty::getExternalReferenceCode),
				AssetCategoryProperty::getCompanyId),
			_SQL_SELECT_ASSETCATEGORYPROPERTY_WHERE, "",
			new FinderColumn<>(
				"assetCategoryProperty.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				AssetCategoryProperty::getExternalReferenceCode),
			new FinderColumn<>(
				"assetCategoryProperty.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, AssetCategoryProperty::getCompanyId));

		AssetCategoryPropertyUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AssetCategoryPropertyUtil.setPersistence(null);

		entityCache.removeCache(AssetCategoryPropertyImpl.class.getName());
	}

	@Override
	@Reference(
		target = AssetPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = AssetPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = AssetPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		AssetCategoryPropertyModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ASSETCATEGORYPROPERTY =
		"SELECT assetCategoryProperty FROM AssetCategoryProperty assetCategoryProperty";

	private static final String _SQL_SELECT_ASSETCATEGORYPROPERTY_WHERE =
		"SELECT assetCategoryProperty FROM AssetCategoryProperty assetCategoryProperty WHERE ";

	private static final String _SQL_COUNT_ASSETCATEGORYPROPERTY_WHERE =
		"SELECT COUNT(assetCategoryProperty) FROM AssetCategoryProperty assetCategoryProperty WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No AssetCategoryProperty exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		AssetCategoryPropertyPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"key"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:674179028