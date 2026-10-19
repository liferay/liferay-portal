/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.service.persistence;

import com.liferay.commerce.tax.engine.fixed.exception.NoSuchTaxFixedRateAddressRelException;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRateAddressRel;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the commerce tax fixed rate address rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CommerceTaxFixedRateAddressRelUtil
 * @generated
 */
@ProviderType
public interface CommerceTaxFixedRateAddressRelPersistence
	extends BasePersistence<CommerceTaxFixedRateAddressRel> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CommerceTaxFixedRateAddressRelUtil} to access the commerce tax fixed rate address rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tax fixed rate address rels
	 */
	public java.util.List<CommerceTaxFixedRateAddressRel>
		findByCommerceTaxMethodId(
			long commerceTaxMethodId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceTaxFixedRateAddressRel> orderByComparator,
			boolean useFinderCache);

	/**
	 * Returns the first commerce tax fixed rate address rel in the ordered set where commerceTaxMethodId = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax fixed rate address rel
	 * @throws NoSuchTaxFixedRateAddressRelException if a matching commerce tax fixed rate address rel could not be found
	 */
	public CommerceTaxFixedRateAddressRel findByCommerceTaxMethodId_First(
			long commerceTaxMethodId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceTaxFixedRateAddressRel> orderByComparator)
		throws NoSuchTaxFixedRateAddressRelException;

	/**
	 * Returns the first commerce tax fixed rate address rel in the ordered set where commerceTaxMethodId = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax fixed rate address rel, or <code>null</code> if a matching commerce tax fixed rate address rel could not be found
	 */
	public CommerceTaxFixedRateAddressRel fetchByCommerceTaxMethodId_First(
		long commerceTaxMethodId,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceTaxFixedRateAddressRel> orderByComparator);

	/**
	 * Removes all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; from the database.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 */
	public void removeByCommerceTaxMethodId(long commerceTaxMethodId);

	/**
	 * Returns the number of commerce tax fixed rate address rels where commerceTaxMethodId = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @return the number of matching commerce tax fixed rate address rels
	 */
	public int countByCommerceTaxMethodId(long commerceTaxMethodId);

	/**
	 * Returns an ordered range of all the commerce tax fixed rate address rels where CPTaxCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tax fixed rate address rels
	 */
	public java.util.List<CommerceTaxFixedRateAddressRel> findByCPTaxCategoryId(
		long CPTaxCategoryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceTaxFixedRateAddressRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce tax fixed rate address rel in the ordered set where CPTaxCategoryId = &#63;.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax fixed rate address rel
	 * @throws NoSuchTaxFixedRateAddressRelException if a matching commerce tax fixed rate address rel could not be found
	 */
	public CommerceTaxFixedRateAddressRel findByCPTaxCategoryId_First(
			long CPTaxCategoryId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceTaxFixedRateAddressRel> orderByComparator)
		throws NoSuchTaxFixedRateAddressRelException;

	/**
	 * Returns the first commerce tax fixed rate address rel in the ordered set where CPTaxCategoryId = &#63;.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax fixed rate address rel, or <code>null</code> if a matching commerce tax fixed rate address rel could not be found
	 */
	public CommerceTaxFixedRateAddressRel fetchByCPTaxCategoryId_First(
		long CPTaxCategoryId,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceTaxFixedRateAddressRel> orderByComparator);

	/**
	 * Removes all the commerce tax fixed rate address rels where CPTaxCategoryId = &#63; from the database.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 */
	public void removeByCPTaxCategoryId(long CPTaxCategoryId);

	/**
	 * Returns the number of commerce tax fixed rate address rels where CPTaxCategoryId = &#63;.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @return the number of matching commerce tax fixed rate address rels
	 */
	public int countByCPTaxCategoryId(long CPTaxCategoryId);

	/**
	 * Returns an ordered range of all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and countryId = &#63; and regionId = &#63; and zip = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param countryId the country ID
	 * @param regionId the region ID
	 * @param zip the zip
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tax fixed rate address rels
	 */
	public java.util.List<CommerceTaxFixedRateAddressRel> findByC_C_R_Z(
		long commerceTaxMethodId, long countryId, long regionId, String zip,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceTaxFixedRateAddressRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce tax fixed rate address rel in the ordered set where commerceTaxMethodId = &#63; and countryId = &#63; and regionId = &#63; and zip = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param countryId the country ID
	 * @param regionId the region ID
	 * @param zip the zip
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax fixed rate address rel
	 * @throws NoSuchTaxFixedRateAddressRelException if a matching commerce tax fixed rate address rel could not be found
	 */
	public CommerceTaxFixedRateAddressRel findByC_C_R_Z_First(
			long commerceTaxMethodId, long countryId, long regionId, String zip,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceTaxFixedRateAddressRel> orderByComparator)
		throws NoSuchTaxFixedRateAddressRelException;

	/**
	 * Returns the first commerce tax fixed rate address rel in the ordered set where commerceTaxMethodId = &#63; and countryId = &#63; and regionId = &#63; and zip = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param countryId the country ID
	 * @param regionId the region ID
	 * @param zip the zip
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax fixed rate address rel, or <code>null</code> if a matching commerce tax fixed rate address rel could not be found
	 */
	public CommerceTaxFixedRateAddressRel fetchByC_C_R_Z_First(
		long commerceTaxMethodId, long countryId, long regionId, String zip,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceTaxFixedRateAddressRel> orderByComparator);

	/**
	 * Returns an ordered range of all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and countryId = &#63; and regionId = &#63; and zip = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param countryIds the country IDs
	 * @param regionIds the region IDs
	 * @param zip the zip
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tax fixed rate address rels
	 */
	public java.util.List<CommerceTaxFixedRateAddressRel> findByC_C_R_Z(
		long commerceTaxMethodId, long[] countryIds, long[] regionIds,
		String zip, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceTaxFixedRateAddressRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and countryId = &#63; and regionId = &#63; and zip = &#63; from the database.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param countryId the country ID
	 * @param regionId the region ID
	 * @param zip the zip
	 */
	public void removeByC_C_R_Z(
		long commerceTaxMethodId, long countryId, long regionId, String zip);

	/**
	 * Returns the number of commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and countryId = &#63; and regionId = &#63; and zip = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param countryId the country ID
	 * @param regionId the region ID
	 * @param zip the zip
	 * @return the number of matching commerce tax fixed rate address rels
	 */
	public int countByC_C_R_Z(
		long commerceTaxMethodId, long countryId, long regionId, String zip);

	/**
	 * Returns the number of commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and countryId = any &#63; and regionId = any &#63; and zip = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param countryIds the country IDs
	 * @param regionIds the region IDs
	 * @param zip the zip
	 * @return the number of matching commerce tax fixed rate address rels
	 */
	public int countByC_C_R_Z(
		long commerceTaxMethodId, long[] countryIds, long[] regionIds,
		String zip);

	/**
	 * Returns an ordered range of all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and CPTaxCategoryId = &#63; and countryId = &#63; and regionId = &#63; and zip = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param countryId the country ID
	 * @param regionId the region ID
	 * @param zip the zip
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tax fixed rate address rels
	 */
	public java.util.List<CommerceTaxFixedRateAddressRel> findByC_C_C_R_Z(
		long commerceTaxMethodId, long CPTaxCategoryId, long countryId,
		long regionId, String zip, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceTaxFixedRateAddressRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce tax fixed rate address rel in the ordered set where commerceTaxMethodId = &#63; and CPTaxCategoryId = &#63; and countryId = &#63; and regionId = &#63; and zip = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param countryId the country ID
	 * @param regionId the region ID
	 * @param zip the zip
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax fixed rate address rel
	 * @throws NoSuchTaxFixedRateAddressRelException if a matching commerce tax fixed rate address rel could not be found
	 */
	public CommerceTaxFixedRateAddressRel findByC_C_C_R_Z_First(
			long commerceTaxMethodId, long CPTaxCategoryId, long countryId,
			long regionId, String zip,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceTaxFixedRateAddressRel> orderByComparator)
		throws NoSuchTaxFixedRateAddressRelException;

	/**
	 * Returns the first commerce tax fixed rate address rel in the ordered set where commerceTaxMethodId = &#63; and CPTaxCategoryId = &#63; and countryId = &#63; and regionId = &#63; and zip = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param countryId the country ID
	 * @param regionId the region ID
	 * @param zip the zip
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax fixed rate address rel, or <code>null</code> if a matching commerce tax fixed rate address rel could not be found
	 */
	public CommerceTaxFixedRateAddressRel fetchByC_C_C_R_Z_First(
		long commerceTaxMethodId, long CPTaxCategoryId, long countryId,
		long regionId, String zip,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceTaxFixedRateAddressRel> orderByComparator);

	/**
	 * Returns an ordered range of all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and CPTaxCategoryId = &#63; and countryId = &#63; and regionId = &#63; and zip = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param countryIds the country IDs
	 * @param regionIds the region IDs
	 * @param zip the zip
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tax fixed rate address rels
	 */
	public java.util.List<CommerceTaxFixedRateAddressRel> findByC_C_C_R_Z(
		long commerceTaxMethodId, long CPTaxCategoryId, long[] countryIds,
		long[] regionIds, String zip, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceTaxFixedRateAddressRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and CPTaxCategoryId = &#63; and countryId = &#63; and regionId = &#63; and zip = &#63; from the database.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param countryId the country ID
	 * @param regionId the region ID
	 * @param zip the zip
	 */
	public void removeByC_C_C_R_Z(
		long commerceTaxMethodId, long CPTaxCategoryId, long countryId,
		long regionId, String zip);

	/**
	 * Returns the number of commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and CPTaxCategoryId = &#63; and countryId = &#63; and regionId = &#63; and zip = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param countryId the country ID
	 * @param regionId the region ID
	 * @param zip the zip
	 * @return the number of matching commerce tax fixed rate address rels
	 */
	public int countByC_C_C_R_Z(
		long commerceTaxMethodId, long CPTaxCategoryId, long countryId,
		long regionId, String zip);

	/**
	 * Returns the number of commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and CPTaxCategoryId = &#63; and countryId = any &#63; and regionId = any &#63; and zip = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param countryIds the country IDs
	 * @param regionIds the region IDs
	 * @param zip the zip
	 * @return the number of matching commerce tax fixed rate address rels
	 */
	public int countByC_C_C_R_Z(
		long commerceTaxMethodId, long CPTaxCategoryId, long[] countryIds,
		long[] regionIds, String zip);

	/**
	 * Creates a new commerce tax fixed rate address rel with the primary key. Does not add the commerce tax fixed rate address rel to the database.
	 *
	 * @param commerceTaxFixedRateAddressRelId the primary key for the new commerce tax fixed rate address rel
	 * @return the new commerce tax fixed rate address rel
	 */
	public CommerceTaxFixedRateAddressRel create(
		long commerceTaxFixedRateAddressRelId);

	/**
	 * Removes the commerce tax fixed rate address rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceTaxFixedRateAddressRelId the primary key of the commerce tax fixed rate address rel
	 * @return the commerce tax fixed rate address rel that was removed
	 * @throws NoSuchTaxFixedRateAddressRelException if a commerce tax fixed rate address rel with the primary key could not be found
	 */
	public CommerceTaxFixedRateAddressRel remove(
			long commerceTaxFixedRateAddressRelId)
		throws NoSuchTaxFixedRateAddressRelException;

	public CommerceTaxFixedRateAddressRel updateImpl(
		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel);

	/**
	 * Returns the commerce tax fixed rate address rel with the primary key or throws a <code>NoSuchTaxFixedRateAddressRelException</code> if it could not be found.
	 *
	 * @param commerceTaxFixedRateAddressRelId the primary key of the commerce tax fixed rate address rel
	 * @return the commerce tax fixed rate address rel
	 * @throws NoSuchTaxFixedRateAddressRelException if a commerce tax fixed rate address rel with the primary key could not be found
	 */
	public CommerceTaxFixedRateAddressRel findByPrimaryKey(
			long commerceTaxFixedRateAddressRelId)
		throws NoSuchTaxFixedRateAddressRelException;

	/**
	 * Returns the commerce tax fixed rate address rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceTaxFixedRateAddressRelId the primary key of the commerce tax fixed rate address rel
	 * @return the commerce tax fixed rate address rel, or <code>null</code> if a commerce tax fixed rate address rel with the primary key could not be found
	 */
	public CommerceTaxFixedRateAddressRel fetchByPrimaryKey(
		long commerceTaxFixedRateAddressRelId);

	/**
	 * Returns all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @return the matching commerce tax fixed rate address rels
	 */
	public default java.util.List<CommerceTaxFixedRateAddressRel>
		findByCommerceTaxMethodId(long commerceTaxMethodId) {

		return findByCommerceTaxMethodId(
			commerceTaxMethodId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @return the range of matching commerce tax fixed rate address rels
	 */
	public default java.util.List<CommerceTaxFixedRateAddressRel>
		findByCommerceTaxMethodId(
			long commerceTaxMethodId, int start, int end) {

		return findByCommerceTaxMethodId(
			commerceTaxMethodId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce tax fixed rate address rels
	 */
	public default java.util.List<CommerceTaxFixedRateAddressRel>
		findByCommerceTaxMethodId(
			long commerceTaxMethodId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceTaxFixedRateAddressRel> orderByComparator) {

		return findByCommerceTaxMethodId(
			commerceTaxMethodId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce tax fixed rate address rels where CPTaxCategoryId = &#63;.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @return the matching commerce tax fixed rate address rels
	 */
	public default java.util.List<CommerceTaxFixedRateAddressRel>
		findByCPTaxCategoryId(long CPTaxCategoryId) {

		return findByCPTaxCategoryId(
			CPTaxCategoryId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce tax fixed rate address rels where CPTaxCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @return the range of matching commerce tax fixed rate address rels
	 */
	public default java.util.List<CommerceTaxFixedRateAddressRel>
		findByCPTaxCategoryId(long CPTaxCategoryId, int start, int end) {

		return findByCPTaxCategoryId(CPTaxCategoryId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce tax fixed rate address rels where CPTaxCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce tax fixed rate address rels
	 */
	public default java.util.List<CommerceTaxFixedRateAddressRel>
		findByCPTaxCategoryId(
			long CPTaxCategoryId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceTaxFixedRateAddressRel> orderByComparator) {

		return findByCPTaxCategoryId(
			CPTaxCategoryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and countryId = &#63; and regionId = &#63; and zip = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param countryId the country ID
	 * @param regionId the region ID
	 * @param zip the zip
	 * @return the matching commerce tax fixed rate address rels
	 */
	public default java.util.List<CommerceTaxFixedRateAddressRel> findByC_C_R_Z(
		long commerceTaxMethodId, long countryId, long regionId, String zip) {

		return findByC_C_R_Z(
			commerceTaxMethodId, countryId, regionId, zip,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and countryId = &#63; and regionId = &#63; and zip = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param countryId the country ID
	 * @param regionId the region ID
	 * @param zip the zip
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @return the range of matching commerce tax fixed rate address rels
	 */
	public default java.util.List<CommerceTaxFixedRateAddressRel> findByC_C_R_Z(
		long commerceTaxMethodId, long countryId, long regionId, String zip,
		int start, int end) {

		return findByC_C_R_Z(
			commerceTaxMethodId, countryId, regionId, zip, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and countryId = &#63; and regionId = &#63; and zip = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param countryId the country ID
	 * @param regionId the region ID
	 * @param zip the zip
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce tax fixed rate address rels
	 */
	public default java.util.List<CommerceTaxFixedRateAddressRel> findByC_C_R_Z(
		long commerceTaxMethodId, long countryId, long regionId, String zip,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceTaxFixedRateAddressRel> orderByComparator) {

		return findByC_C_R_Z(
			commerceTaxMethodId, countryId, regionId, zip, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and countryId = any &#63; and regionId = any &#63; and zip = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param countryIds the country IDs
	 * @param regionIds the region IDs
	 * @param zip the zip
	 * @return the matching commerce tax fixed rate address rels
	 */
	public default java.util.List<CommerceTaxFixedRateAddressRel> findByC_C_R_Z(
		long commerceTaxMethodId, long[] countryIds, long[] regionIds,
		String zip) {

		return findByC_C_R_Z(
			commerceTaxMethodId, countryIds, regionIds, zip,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and countryId = any &#63; and regionId = any &#63; and zip = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param countryIds the country IDs
	 * @param regionIds the region IDs
	 * @param zip the zip
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @return the range of matching commerce tax fixed rate address rels
	 */
	public default java.util.List<CommerceTaxFixedRateAddressRel> findByC_C_R_Z(
		long commerceTaxMethodId, long[] countryIds, long[] regionIds,
		String zip, int start, int end) {

		return findByC_C_R_Z(
			commerceTaxMethodId, countryIds, regionIds, zip, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and countryId = any &#63; and regionId = any &#63; and zip = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param countryIds the country IDs
	 * @param regionIds the region IDs
	 * @param zip the zip
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce tax fixed rate address rels
	 */
	public default java.util.List<CommerceTaxFixedRateAddressRel> findByC_C_R_Z(
		long commerceTaxMethodId, long[] countryIds, long[] regionIds,
		String zip, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceTaxFixedRateAddressRel> orderByComparator) {

		return findByC_C_R_Z(
			commerceTaxMethodId, countryIds, regionIds, zip, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and CPTaxCategoryId = &#63; and countryId = &#63; and regionId = &#63; and zip = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param countryId the country ID
	 * @param regionId the region ID
	 * @param zip the zip
	 * @return the matching commerce tax fixed rate address rels
	 */
	public default java.util.List<CommerceTaxFixedRateAddressRel>
		findByC_C_C_R_Z(
			long commerceTaxMethodId, long CPTaxCategoryId, long countryId,
			long regionId, String zip) {

		return findByC_C_C_R_Z(
			commerceTaxMethodId, CPTaxCategoryId, countryId, regionId, zip,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and CPTaxCategoryId = &#63; and countryId = &#63; and regionId = &#63; and zip = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param countryId the country ID
	 * @param regionId the region ID
	 * @param zip the zip
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @return the range of matching commerce tax fixed rate address rels
	 */
	public default java.util.List<CommerceTaxFixedRateAddressRel>
		findByC_C_C_R_Z(
			long commerceTaxMethodId, long CPTaxCategoryId, long countryId,
			long regionId, String zip, int start, int end) {

		return findByC_C_C_R_Z(
			commerceTaxMethodId, CPTaxCategoryId, countryId, regionId, zip,
			start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and CPTaxCategoryId = &#63; and countryId = &#63; and regionId = &#63; and zip = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param countryId the country ID
	 * @param regionId the region ID
	 * @param zip the zip
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce tax fixed rate address rels
	 */
	public default java.util.List<CommerceTaxFixedRateAddressRel>
		findByC_C_C_R_Z(
			long commerceTaxMethodId, long CPTaxCategoryId, long countryId,
			long regionId, String zip, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceTaxFixedRateAddressRel> orderByComparator) {

		return findByC_C_C_R_Z(
			commerceTaxMethodId, CPTaxCategoryId, countryId, regionId, zip,
			start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and CPTaxCategoryId = &#63; and countryId = any &#63; and regionId = any &#63; and zip = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param countryIds the country IDs
	 * @param regionIds the region IDs
	 * @param zip the zip
	 * @return the matching commerce tax fixed rate address rels
	 */
	public default java.util.List<CommerceTaxFixedRateAddressRel>
		findByC_C_C_R_Z(
			long commerceTaxMethodId, long CPTaxCategoryId, long[] countryIds,
			long[] regionIds, String zip) {

		return findByC_C_C_R_Z(
			commerceTaxMethodId, CPTaxCategoryId, countryIds, regionIds, zip,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and CPTaxCategoryId = &#63; and countryId = any &#63; and regionId = any &#63; and zip = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param countryIds the country IDs
	 * @param regionIds the region IDs
	 * @param zip the zip
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @return the range of matching commerce tax fixed rate address rels
	 */
	public default java.util.List<CommerceTaxFixedRateAddressRel>
		findByC_C_C_R_Z(
			long commerceTaxMethodId, long CPTaxCategoryId, long[] countryIds,
			long[] regionIds, String zip, int start, int end) {

		return findByC_C_C_R_Z(
			commerceTaxMethodId, CPTaxCategoryId, countryIds, regionIds, zip,
			start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; and CPTaxCategoryId = &#63; and countryId = any &#63; and regionId = any &#63; and zip = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param countryIds the country IDs
	 * @param regionIds the region IDs
	 * @param zip the zip
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce tax fixed rate address rels
	 */
	public default java.util.List<CommerceTaxFixedRateAddressRel>
		findByC_C_C_R_Z(
			long commerceTaxMethodId, long CPTaxCategoryId, long[] countryIds,
			long[] regionIds, String zip, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceTaxFixedRateAddressRel> orderByComparator) {

		return findByC_C_C_R_Z(
			commerceTaxMethodId, CPTaxCategoryId, countryIds, regionIds, zip,
			start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:531167977