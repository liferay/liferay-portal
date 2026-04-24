/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.production.readiness.ignore.exception.NoSuchProductionReadinessIgnoreException;
import com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the production readiness ignore service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ProductionReadinessIgnoreUtil
 * @generated
 */
@ProviderType
public interface ProductionReadinessIgnorePersistence
	extends BasePersistence<ProductionReadinessIgnore> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ProductionReadinessIgnoreUtil} to access the production readiness ignore persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the production readiness ignores where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching production readiness ignores
	 */
	public java.util.List<ProductionReadinessIgnore> findByCompanyId(
		long companyId);

	/**
	 * Returns a range of all the production readiness ignores where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @return the range of matching production readiness ignores
	 */
	public java.util.List<ProductionReadinessIgnore> findByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the production readiness ignores where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching production readiness ignores
	 */
	public java.util.List<ProductionReadinessIgnore> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<ProductionReadinessIgnore> orderByComparator);

	/**
	 * Returns an ordered range of all the production readiness ignores where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching production readiness ignores
	 */
	public java.util.List<ProductionReadinessIgnore> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<ProductionReadinessIgnore> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first production readiness ignore in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching production readiness ignore
	 * @throws NoSuchProductionReadinessIgnoreException if a matching production readiness ignore could not be found
	 */
	public ProductionReadinessIgnore findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<ProductionReadinessIgnore> orderByComparator)
		throws NoSuchProductionReadinessIgnoreException;

	/**
	 * Returns the first production readiness ignore in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching production readiness ignore, or <code>null</code> if a matching production readiness ignore could not be found
	 */
	public ProductionReadinessIgnore fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<ProductionReadinessIgnore> orderByComparator);

	/**
	 * Removes all the production readiness ignores where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of production readiness ignores where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching production readiness ignores
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns the production readiness ignore where companyId = &#63; and ruleKey = &#63; or throws a <code>NoSuchProductionReadinessIgnoreException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the matching production readiness ignore
	 * @throws NoSuchProductionReadinessIgnoreException if a matching production readiness ignore could not be found
	 */
	public ProductionReadinessIgnore findByC_R(long companyId, String ruleKey)
		throws NoSuchProductionReadinessIgnoreException;

	/**
	 * Returns the production readiness ignore where companyId = &#63; and ruleKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the matching production readiness ignore, or <code>null</code> if a matching production readiness ignore could not be found
	 */
	public ProductionReadinessIgnore fetchByC_R(long companyId, String ruleKey);

	/**
	 * Returns the production readiness ignore where companyId = &#63; and ruleKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching production readiness ignore, or <code>null</code> if a matching production readiness ignore could not be found
	 */
	public ProductionReadinessIgnore fetchByC_R(
		long companyId, String ruleKey, boolean useFinderCache);

	/**
	 * Removes the production readiness ignore where companyId = &#63; and ruleKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the production readiness ignore that was removed
	 */
	public ProductionReadinessIgnore removeByC_R(long companyId, String ruleKey)
		throws NoSuchProductionReadinessIgnoreException;

	/**
	 * Returns the number of production readiness ignores where companyId = &#63; and ruleKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the number of matching production readiness ignores
	 */
	public int countByC_R(long companyId, String ruleKey);

	/**
	 * Caches the production readiness ignore in the entity cache if it is enabled.
	 *
	 * @param productionReadinessIgnore the production readiness ignore
	 */
	public void cacheResult(
		ProductionReadinessIgnore productionReadinessIgnore);

	/**
	 * Caches the production readiness ignores in the entity cache if it is enabled.
	 *
	 * @param productionReadinessIgnores the production readiness ignores
	 */
	public void cacheResult(
		java.util.List<ProductionReadinessIgnore> productionReadinessIgnores);

	/**
	 * Creates a new production readiness ignore with the primary key. Does not add the production readiness ignore to the database.
	 *
	 * @param productionReadinessIgnoreId the primary key for the new production readiness ignore
	 * @return the new production readiness ignore
	 */
	public ProductionReadinessIgnore create(long productionReadinessIgnoreId);

	/**
	 * Removes the production readiness ignore with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param productionReadinessIgnoreId the primary key of the production readiness ignore
	 * @return the production readiness ignore that was removed
	 * @throws NoSuchProductionReadinessIgnoreException if a production readiness ignore with the primary key could not be found
	 */
	public ProductionReadinessIgnore remove(long productionReadinessIgnoreId)
		throws NoSuchProductionReadinessIgnoreException;

	public ProductionReadinessIgnore updateImpl(
		ProductionReadinessIgnore productionReadinessIgnore);

	/**
	 * Returns the production readiness ignore with the primary key or throws a <code>NoSuchProductionReadinessIgnoreException</code> if it could not be found.
	 *
	 * @param productionReadinessIgnoreId the primary key of the production readiness ignore
	 * @return the production readiness ignore
	 * @throws NoSuchProductionReadinessIgnoreException if a production readiness ignore with the primary key could not be found
	 */
	public ProductionReadinessIgnore findByPrimaryKey(
			long productionReadinessIgnoreId)
		throws NoSuchProductionReadinessIgnoreException;

	/**
	 * Returns the production readiness ignore with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param productionReadinessIgnoreId the primary key of the production readiness ignore
	 * @return the production readiness ignore, or <code>null</code> if a production readiness ignore with the primary key could not be found
	 */
	public ProductionReadinessIgnore fetchByPrimaryKey(
		long productionReadinessIgnoreId);

	/**
	 * Returns all the production readiness ignores.
	 *
	 * @return the production readiness ignores
	 */
	public java.util.List<ProductionReadinessIgnore> findAll();

	/**
	 * Returns a range of all the production readiness ignores.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @return the range of production readiness ignores
	 */
	public java.util.List<ProductionReadinessIgnore> findAll(
		int start, int end);

	/**
	 * Returns an ordered range of all the production readiness ignores.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of production readiness ignores
	 */
	public java.util.List<ProductionReadinessIgnore> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<ProductionReadinessIgnore> orderByComparator);

	/**
	 * Returns an ordered range of all the production readiness ignores.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of production readiness ignores
	 */
	public java.util.List<ProductionReadinessIgnore> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<ProductionReadinessIgnore> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the production readiness ignores from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of production readiness ignores.
	 *
	 * @return the number of production readiness ignores
	 */
	public int countAll();

}
// LIFERAY-SERVICE-BUILDER-HASH:1745051273