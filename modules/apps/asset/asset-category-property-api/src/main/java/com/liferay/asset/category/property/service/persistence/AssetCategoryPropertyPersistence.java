/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.category.property.service.persistence;

import com.liferay.asset.category.property.exception.NoSuchCategoryPropertyException;
import com.liferay.asset.category.property.model.AssetCategoryProperty;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the asset category property service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AssetCategoryPropertyUtil
 * @generated
 */
@ProviderType
public interface AssetCategoryPropertyPersistence
	extends BasePersistence<AssetCategoryProperty>,
			CTPersistence<AssetCategoryProperty> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link AssetCategoryPropertyUtil} to access the asset category property persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the asset category properties where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.category.property.model.impl.AssetCategoryPropertyModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset category properties
	 * @param end the upper bound of the range of asset category properties (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset category properties
	 */
	public java.util.List<AssetCategoryProperty> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetCategoryProperty>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset category property in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category property
	 * @throws NoSuchCategoryPropertyException if a matching asset category property could not be found
	 */
	public AssetCategoryProperty findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetCategoryProperty> orderByComparator)
		throws NoSuchCategoryPropertyException;

	/**
	 * Returns the first asset category property in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category property, or <code>null</code> if a matching asset category property could not be found
	 */
	public AssetCategoryProperty fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<AssetCategoryProperty>
			orderByComparator);

	/**
	 * Removes all the asset category properties where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of asset category properties where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching asset category properties
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns an ordered range of all the asset category properties where categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.category.property.model.impl.AssetCategoryPropertyModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of asset category properties
	 * @param end the upper bound of the range of asset category properties (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset category properties
	 */
	public java.util.List<AssetCategoryProperty> findByCategoryId(
		long categoryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetCategoryProperty>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset category property in the ordered set where categoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category property
	 * @throws NoSuchCategoryPropertyException if a matching asset category property could not be found
	 */
	public AssetCategoryProperty findByCategoryId_First(
			long categoryId,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetCategoryProperty> orderByComparator)
		throws NoSuchCategoryPropertyException;

	/**
	 * Returns the first asset category property in the ordered set where categoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category property, or <code>null</code> if a matching asset category property could not be found
	 */
	public AssetCategoryProperty fetchByCategoryId_First(
		long categoryId,
		com.liferay.portal.kernel.util.OrderByComparator<AssetCategoryProperty>
			orderByComparator);

	/**
	 * Removes all the asset category properties where categoryId = &#63; from the database.
	 *
	 * @param categoryId the category ID
	 */
	public void removeByCategoryId(long categoryId);

	/**
	 * Returns the number of asset category properties where categoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @return the number of matching asset category properties
	 */
	public int countByCategoryId(long categoryId);

	/**
	 * Returns the asset category property where categoryId = &#63; and key = &#63; or throws a <code>NoSuchCategoryPropertyException</code> if it could not be found.
	 *
	 * @param categoryId the category ID
	 * @param key the key
	 * @return the matching asset category property
	 * @throws NoSuchCategoryPropertyException if a matching asset category property could not be found
	 */
	public AssetCategoryProperty findByCA_K(long categoryId, String key)
		throws NoSuchCategoryPropertyException;

	/**
	 * Returns the asset category property where categoryId = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param categoryId the category ID
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset category property, or <code>null</code> if a matching asset category property could not be found
	 */
	public AssetCategoryProperty fetchByCA_K(
		long categoryId, String key, boolean useFinderCache);

	/**
	 * Removes the asset category property where categoryId = &#63; and key = &#63; from the database.
	 *
	 * @param categoryId the category ID
	 * @param key the key
	 * @return the asset category property that was removed
	 */
	public AssetCategoryProperty removeByCA_K(long categoryId, String key)
		throws NoSuchCategoryPropertyException;

	/**
	 * Returns the number of asset category properties where categoryId = &#63; and key = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param key the key
	 * @return the number of matching asset category properties
	 */
	public int countByCA_K(long categoryId, String key);

	/**
	 * Returns the asset category property where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCategoryPropertyException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching asset category property
	 * @throws NoSuchCategoryPropertyException if a matching asset category property could not be found
	 */
	public AssetCategoryProperty findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCategoryPropertyException;

	/**
	 * Returns the asset category property where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset category property, or <code>null</code> if a matching asset category property could not be found
	 */
	public AssetCategoryProperty fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the asset category property where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the asset category property that was removed
	 */
	public AssetCategoryProperty removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCategoryPropertyException;

	/**
	 * Returns the number of asset category properties where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching asset category properties
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Creates a new asset category property with the primary key. Does not add the asset category property to the database.
	 *
	 * @param categoryPropertyId the primary key for the new asset category property
	 * @return the new asset category property
	 */
	public AssetCategoryProperty create(long categoryPropertyId);

	/**
	 * Removes the asset category property with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param categoryPropertyId the primary key of the asset category property
	 * @return the asset category property that was removed
	 * @throws NoSuchCategoryPropertyException if a asset category property with the primary key could not be found
	 */
	public AssetCategoryProperty remove(long categoryPropertyId)
		throws NoSuchCategoryPropertyException;

	public AssetCategoryProperty updateImpl(
		AssetCategoryProperty assetCategoryProperty);

	/**
	 * Returns the asset category property with the primary key or throws a <code>NoSuchCategoryPropertyException</code> if it could not be found.
	 *
	 * @param categoryPropertyId the primary key of the asset category property
	 * @return the asset category property
	 * @throws NoSuchCategoryPropertyException if a asset category property with the primary key could not be found
	 */
	public AssetCategoryProperty findByPrimaryKey(long categoryPropertyId)
		throws NoSuchCategoryPropertyException;

	/**
	 * Returns the asset category property with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param categoryPropertyId the primary key of the asset category property
	 * @return the asset category property, or <code>null</code> if a asset category property with the primary key could not be found
	 */
	public AssetCategoryProperty fetchByPrimaryKey(long categoryPropertyId);

	/**
	 * Returns the asset category property where categoryId = &#63; and key = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param categoryId the category ID
	 * @param key the key
	 * @return the matching asset category property, or <code>null</code> if a matching asset category property could not be found
	 */
	public default AssetCategoryProperty fetchByCA_K(
		long categoryId, String key) {

		return fetchByCA_K(categoryId, key, true);
	}

	/**
	 * Returns the asset category property where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching asset category property, or <code>null</code> if a matching asset category property could not be found
	 */
	public default AssetCategoryProperty fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns all the asset category properties where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching asset category properties
	 */
	public default java.util.List<AssetCategoryProperty> findByCompanyId(
		long companyId) {

		return findByCompanyId(
			companyId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset category properties where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.category.property.model.impl.AssetCategoryPropertyModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset category properties
	 * @param end the upper bound of the range of asset category properties (not inclusive)
	 * @return the range of matching asset category properties
	 */
	public default java.util.List<AssetCategoryProperty> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset category properties where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.category.property.model.impl.AssetCategoryPropertyModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset category properties
	 * @param end the upper bound of the range of asset category properties (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset category properties
	 */
	public default java.util.List<AssetCategoryProperty> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetCategoryProperty>
			orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset category properties where categoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @return the matching asset category properties
	 */
	public default java.util.List<AssetCategoryProperty> findByCategoryId(
		long categoryId) {

		return findByCategoryId(
			categoryId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset category properties where categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.category.property.model.impl.AssetCategoryPropertyModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of asset category properties
	 * @param end the upper bound of the range of asset category properties (not inclusive)
	 * @return the range of matching asset category properties
	 */
	public default java.util.List<AssetCategoryProperty> findByCategoryId(
		long categoryId, int start, int end) {

		return findByCategoryId(categoryId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset category properties where categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.category.property.model.impl.AssetCategoryPropertyModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of asset category properties
	 * @param end the upper bound of the range of asset category properties (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset category properties
	 */
	public default java.util.List<AssetCategoryProperty> findByCategoryId(
		long categoryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetCategoryProperty>
			orderByComparator) {

		return findByCategoryId(
			categoryId, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1040744786