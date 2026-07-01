/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.service.persistence.impl;

import com.liferay.commerce.currency.exception.DuplicateCommerceCurrencyExternalReferenceCodeException;
import com.liferay.commerce.currency.exception.NoSuchCurrencyException;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceCurrencyTable;
import com.liferay.commerce.currency.model.impl.CommerceCurrencyImpl;
import com.liferay.commerce.currency.model.impl.CommerceCurrencyModelImpl;
import com.liferay.commerce.currency.service.persistence.CommerceCurrencyPersistence;
import com.liferay.commerce.currency.service.persistence.CommerceCurrencyUtil;
import com.liferay.commerce.currency.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce currency service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Andrea Di Giorgi
 * @generated
 */
@Component(service = CommerceCurrencyPersistence.class)
public class CommerceCurrencyPersistenceImpl
	extends BasePersistenceImpl<CommerceCurrency, NoSuchCurrencyException>
	implements CommerceCurrencyPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceCurrencyUtil</code> to access the commerce currency persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceCurrencyImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceCurrency, NoSuchCurrencyException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce currencies where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceCurrencyModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce currencies
	 * @param end the upper bound of the range of commerce currencies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce currencies
	 */
	@Override
	public List<CommerceCurrency> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceCurrency> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce currency in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce currency
	 * @throws NoSuchCurrencyException if a matching commerce currency could not be found
	 */
	@Override
	public CommerceCurrency findByUuid_First(
			String uuid, OrderByComparator<CommerceCurrency> orderByComparator)
		throws NoSuchCurrencyException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce currency in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce currency, or <code>null</code> if a matching commerce currency could not be found
	 */
	@Override
	public CommerceCurrency fetchByUuid_First(
		String uuid, OrderByComparator<CommerceCurrency> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce currencies where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce currencies where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce currencies
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<CommerceCurrency, NoSuchCurrencyException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce currencies where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceCurrencyModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce currencies
	 * @param end the upper bound of the range of commerce currencies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce currencies
	 */
	@Override
	public List<CommerceCurrency> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceCurrency> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce currency in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce currency
	 * @throws NoSuchCurrencyException if a matching commerce currency could not be found
	 */
	@Override
	public CommerceCurrency findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceCurrency> orderByComparator)
		throws NoSuchCurrencyException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce currency in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce currency, or <code>null</code> if a matching commerce currency could not be found
	 */
	@Override
	public CommerceCurrency fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceCurrency> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce currencies where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce currencies where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce currencies
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommerceCurrency, NoSuchCurrencyException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the commerce currencies where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceCurrencyModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce currencies
	 * @param end the upper bound of the range of commerce currencies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce currencies
	 */
	@Override
	public List<CommerceCurrency> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceCurrency> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce currency in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce currency
	 * @throws NoSuchCurrencyException if a matching commerce currency could not be found
	 */
	@Override
	public CommerceCurrency findByCompanyId_First(
			long companyId,
			OrderByComparator<CommerceCurrency> orderByComparator)
		throws NoSuchCurrencyException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce currency in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce currency, or <code>null</code> if a matching commerce currency could not be found
	 */
	@Override
	public CommerceCurrency fetchByCompanyId_First(
		long companyId, OrderByComparator<CommerceCurrency> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce currencies where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of commerce currencies where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce currencies
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private UniquePersistenceFinder<CommerceCurrency, NoSuchCurrencyException>
		_uniquePersistenceFinderByC_C;

	/**
	 * Returns the commerce currency where companyId = &#63; and code = &#63; or throws a <code>NoSuchCurrencyException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param code the code
	 * @return the matching commerce currency
	 * @throws NoSuchCurrencyException if a matching commerce currency could not be found
	 */
	@Override
	public CommerceCurrency findByC_C(long companyId, String code)
		throws NoSuchCurrencyException {

		return _uniquePersistenceFinderByC_C.find(
			finderCache, new Object[] {companyId, code});
	}

	/**
	 * Returns the commerce currency where companyId = &#63; and code = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param code the code
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce currency, or <code>null</code> if a matching commerce currency could not be found
	 */
	@Override
	public CommerceCurrency fetchByC_C(
		long companyId, String code, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C.fetch(
			finderCache, new Object[] {companyId, code}, useFinderCache);
	}

	/**
	 * Removes the commerce currency where companyId = &#63; and code = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param code the code
	 * @return the commerce currency that was removed
	 */
	@Override
	public CommerceCurrency removeByC_C(long companyId, String code)
		throws NoSuchCurrencyException {

		CommerceCurrency commerceCurrency = findByC_C(companyId, code);

		return remove(commerceCurrency);
	}

	/**
	 * Returns the number of commerce currencies where companyId = &#63; and code = &#63;.
	 *
	 * @param companyId the company ID
	 * @param code the code
	 * @return the number of matching commerce currencies
	 */
	@Override
	public int countByC_C(long companyId, String code) {
		return _uniquePersistenceFinderByC_C.count(
			finderCache, new Object[] {companyId, code});
	}

	private CollectionPersistenceFinder
		<CommerceCurrency, NoSuchCurrencyException>
			_collectionPersistenceFinderByC_P;

	/**
	 * Returns an ordered range of all the commerce currencies where companyId = &#63; and primary = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceCurrencyModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param primary the primary
	 * @param start the lower bound of the range of commerce currencies
	 * @param end the upper bound of the range of commerce currencies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce currencies
	 */
	@Override
	public List<CommerceCurrency> findByC_P(
		long companyId, boolean primary, int start, int end,
		OrderByComparator<CommerceCurrency> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_P.find(
			finderCache, new Object[] {companyId, primary}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce currency in the ordered set where companyId = &#63; and primary = &#63;.
	 *
	 * @param companyId the company ID
	 * @param primary the primary
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce currency
	 * @throws NoSuchCurrencyException if a matching commerce currency could not be found
	 */
	@Override
	public CommerceCurrency findByC_P_First(
			long companyId, boolean primary,
			OrderByComparator<CommerceCurrency> orderByComparator)
		throws NoSuchCurrencyException {

		return _collectionPersistenceFinderByC_P.findFirst(
			finderCache, new Object[] {companyId, primary}, orderByComparator);
	}

	/**
	 * Returns the first commerce currency in the ordered set where companyId = &#63; and primary = &#63;.
	 *
	 * @param companyId the company ID
	 * @param primary the primary
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce currency, or <code>null</code> if a matching commerce currency could not be found
	 */
	@Override
	public CommerceCurrency fetchByC_P_First(
		long companyId, boolean primary,
		OrderByComparator<CommerceCurrency> orderByComparator) {

		return _collectionPersistenceFinderByC_P.fetchFirst(
			finderCache, new Object[] {companyId, primary}, orderByComparator);
	}

	/**
	 * Removes all the commerce currencies where companyId = &#63; and primary = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param primary the primary
	 */
	@Override
	public void removeByC_P(long companyId, boolean primary) {
		_collectionPersistenceFinderByC_P.remove(
			finderCache, new Object[] {companyId, primary});
	}

	/**
	 * Returns the number of commerce currencies where companyId = &#63; and primary = &#63;.
	 *
	 * @param companyId the company ID
	 * @param primary the primary
	 * @return the number of matching commerce currencies
	 */
	@Override
	public int countByC_P(long companyId, boolean primary) {
		return _collectionPersistenceFinderByC_P.count(
			finderCache, new Object[] {companyId, primary});
	}

	private CollectionPersistenceFinder
		<CommerceCurrency, NoSuchCurrencyException>
			_collectionPersistenceFinderByC_A;

	/**
	 * Returns an ordered range of all the commerce currencies where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceCurrencyModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce currencies
	 * @param end the upper bound of the range of commerce currencies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce currencies
	 */
	@Override
	public List<CommerceCurrency> findByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<CommerceCurrency> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A.find(
			finderCache, new Object[] {companyId, active}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce currency in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce currency
	 * @throws NoSuchCurrencyException if a matching commerce currency could not be found
	 */
	@Override
	public CommerceCurrency findByC_A_First(
			long companyId, boolean active,
			OrderByComparator<CommerceCurrency> orderByComparator)
		throws NoSuchCurrencyException {

		return _collectionPersistenceFinderByC_A.findFirst(
			finderCache, new Object[] {companyId, active}, orderByComparator);
	}

	/**
	 * Returns the first commerce currency in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce currency, or <code>null</code> if a matching commerce currency could not be found
	 */
	@Override
	public CommerceCurrency fetchByC_A_First(
		long companyId, boolean active,
		OrderByComparator<CommerceCurrency> orderByComparator) {

		return _collectionPersistenceFinderByC_A.fetchFirst(
			finderCache, new Object[] {companyId, active}, orderByComparator);
	}

	/**
	 * Removes all the commerce currencies where companyId = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 */
	@Override
	public void removeByC_A(long companyId, boolean active) {
		_collectionPersistenceFinderByC_A.remove(
			finderCache, new Object[] {companyId, active});
	}

	/**
	 * Returns the number of commerce currencies where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching commerce currencies
	 */
	@Override
	public int countByC_A(long companyId, boolean active) {
		return _collectionPersistenceFinderByC_A.count(
			finderCache, new Object[] {companyId, active});
	}

	private CollectionPersistenceFinder
		<CommerceCurrency, NoSuchCurrencyException>
			_collectionPersistenceFinderByC_P_A;

	/**
	 * Returns an ordered range of all the commerce currencies where companyId = &#63; and primary = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceCurrencyModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param primary the primary
	 * @param active the active
	 * @param start the lower bound of the range of commerce currencies
	 * @param end the upper bound of the range of commerce currencies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce currencies
	 */
	@Override
	public List<CommerceCurrency> findByC_P_A(
		long companyId, boolean primary, boolean active, int start, int end,
		OrderByComparator<CommerceCurrency> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_P_A.find(
			finderCache, new Object[] {companyId, primary, active}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce currency in the ordered set where companyId = &#63; and primary = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param primary the primary
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce currency
	 * @throws NoSuchCurrencyException if a matching commerce currency could not be found
	 */
	@Override
	public CommerceCurrency findByC_P_A_First(
			long companyId, boolean primary, boolean active,
			OrderByComparator<CommerceCurrency> orderByComparator)
		throws NoSuchCurrencyException {

		return _collectionPersistenceFinderByC_P_A.findFirst(
			finderCache, new Object[] {companyId, primary, active},
			orderByComparator);
	}

	/**
	 * Returns the first commerce currency in the ordered set where companyId = &#63; and primary = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param primary the primary
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce currency, or <code>null</code> if a matching commerce currency could not be found
	 */
	@Override
	public CommerceCurrency fetchByC_P_A_First(
		long companyId, boolean primary, boolean active,
		OrderByComparator<CommerceCurrency> orderByComparator) {

		return _collectionPersistenceFinderByC_P_A.fetchFirst(
			finderCache, new Object[] {companyId, primary, active},
			orderByComparator);
	}

	/**
	 * Removes all the commerce currencies where companyId = &#63; and primary = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param primary the primary
	 * @param active the active
	 */
	@Override
	public void removeByC_P_A(long companyId, boolean primary, boolean active) {
		_collectionPersistenceFinderByC_P_A.remove(
			finderCache, new Object[] {companyId, primary, active});
	}

	/**
	 * Returns the number of commerce currencies where companyId = &#63; and primary = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param primary the primary
	 * @param active the active
	 * @return the number of matching commerce currencies
	 */
	@Override
	public int countByC_P_A(long companyId, boolean primary, boolean active) {
		return _collectionPersistenceFinderByC_P_A.count(
			finderCache, new Object[] {companyId, primary, active});
	}

	private UniquePersistenceFinder<CommerceCurrency, NoSuchCurrencyException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the commerce currency where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCurrencyException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce currency
	 * @throws NoSuchCurrencyException if a matching commerce currency could not be found
	 */
	@Override
	public CommerceCurrency findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCurrencyException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the commerce currency where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce currency, or <code>null</code> if a matching commerce currency could not be found
	 */
	@Override
	public CommerceCurrency fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the commerce currency where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce currency that was removed
	 */
	@Override
	public CommerceCurrency removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCurrencyException {

		CommerceCurrency commerceCurrency = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commerceCurrency);
	}

	/**
	 * Returns the number of commerce currencies where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce currencies
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CommerceCurrencyPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("code", "code_");
		dbColumnNames.put("primary", "primary_");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceCurrency.class);

		setModelImplClass(CommerceCurrencyImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceCurrencyTable.INSTANCE);
	}

	/**
	 * Creates a new commerce currency with the primary key. Does not add the commerce currency to the database.
	 *
	 * @param commerceCurrencyId the primary key for the new commerce currency
	 * @return the new commerce currency
	 */
	@Override
	public CommerceCurrency create(long commerceCurrencyId) {
		CommerceCurrency commerceCurrency = new CommerceCurrencyImpl();

		commerceCurrency.setNew(true);
		commerceCurrency.setPrimaryKey(commerceCurrencyId);

		String uuid = PortalUUIDUtil.generate();

		commerceCurrency.setUuid(uuid);

		commerceCurrency.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceCurrency;
	}

	/**
	 * Removes the commerce currency with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceCurrencyId the primary key of the commerce currency
	 * @return the commerce currency that was removed
	 * @throws NoSuchCurrencyException if a commerce currency with the primary key could not be found
	 */
	@Override
	public CommerceCurrency remove(long commerceCurrencyId)
		throws NoSuchCurrencyException {

		return remove((Serializable)commerceCurrencyId);
	}

	@Override
	protected CommerceCurrency removeImpl(CommerceCurrency commerceCurrency) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceCurrency)) {
				commerceCurrency = (CommerceCurrency)session.get(
					CommerceCurrencyImpl.class,
					commerceCurrency.getPrimaryKeyObj());
			}

			if (commerceCurrency != null) {
				session.delete(commerceCurrency);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceCurrency != null) {
			clearCache(commerceCurrency);
		}

		return commerceCurrency;
	}

	@Override
	public CommerceCurrency updateImpl(CommerceCurrency commerceCurrency) {
		boolean isNew = commerceCurrency.isNew();

		if (!(commerceCurrency instanceof CommerceCurrencyModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceCurrency.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceCurrency);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceCurrency proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceCurrency implementation " +
					commerceCurrency.getClass());
		}

		CommerceCurrencyModelImpl commerceCurrencyModelImpl =
			(CommerceCurrencyModelImpl)commerceCurrency;

		if (Validator.isNull(commerceCurrency.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceCurrency.setUuid(uuid);
		}

		if (Validator.isNull(commerceCurrency.getExternalReferenceCode())) {
			commerceCurrency.setExternalReferenceCode(
				commerceCurrency.getUuid());
		}
		else {
			if (!Objects.equals(
					commerceCurrencyModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commerceCurrency.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commerceCurrency.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = commerceCurrency.getPrimaryKey();
					}

					try {
						commerceCurrency.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommerceCurrency.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								commerceCurrency.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommerceCurrency ercCommerceCurrency = fetchByERC_C(
				commerceCurrency.getExternalReferenceCode(),
				commerceCurrency.getCompanyId());

			if (isNew) {
				if (ercCommerceCurrency != null) {
					throw new DuplicateCommerceCurrencyExternalReferenceCodeException(
						"Duplicate commerce currency with external reference code " +
							commerceCurrency.getExternalReferenceCode() +
								" and company " +
									commerceCurrency.getCompanyId());
				}
			}
			else {
				if ((ercCommerceCurrency != null) &&
					(commerceCurrency.getCommerceCurrencyId() !=
						ercCommerceCurrency.getCommerceCurrencyId())) {

					throw new DuplicateCommerceCurrencyExternalReferenceCodeException(
						"Duplicate commerce currency with external reference code " +
							commerceCurrency.getExternalReferenceCode() +
								" and company " +
									commerceCurrency.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceCurrency.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceCurrency.setCreateDate(date);
			}
			else {
				commerceCurrency.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceCurrencyModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceCurrency.setModifiedDate(date);
			}
			else {
				commerceCurrency.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceCurrency);
			}
			else {
				commerceCurrency = (CommerceCurrency)session.merge(
					commerceCurrency);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceCurrency, false);

		if (isNew) {
			commerceCurrency.setNew(false);
		}

		commerceCurrency.resetOriginalValues();

		return commerceCurrency;
	}

	/**
	 * Returns the commerce currency with the primary key or throws a <code>NoSuchCurrencyException</code> if it could not be found.
	 *
	 * @param commerceCurrencyId the primary key of the commerce currency
	 * @return the commerce currency
	 * @throws NoSuchCurrencyException if a commerce currency with the primary key could not be found
	 */
	@Override
	public CommerceCurrency findByPrimaryKey(long commerceCurrencyId)
		throws NoSuchCurrencyException {

		return findByPrimaryKey((Serializable)commerceCurrencyId);
	}

	/**
	 * Returns the commerce currency with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceCurrencyId the primary key of the commerce currency
	 * @return the commerce currency, or <code>null</code> if a commerce currency with the primary key could not be found
	 */
	@Override
	public CommerceCurrency fetchByPrimaryKey(long commerceCurrencyId) {
		return fetchByPrimaryKey((Serializable)commerceCurrencyId);
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
		return "commerceCurrencyId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCECURRENCY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceCurrencyModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce currency persistence.
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
			_SQL_SELECT_COMMERCECURRENCY_WHERE,
			_SQL_COUNT_COMMERCECURRENCY_WHERE,
			CommerceCurrencyModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"commerceCurrency.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, CommerceCurrency::getUuid));

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
				_SQL_SELECT_COMMERCECURRENCY_WHERE,
				_SQL_COUNT_COMMERCECURRENCY_WHERE,
				CommerceCurrencyModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceCurrency.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceCurrency::getUuid),
				new FinderColumn<>(
					"commerceCurrency.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CommerceCurrency::getCompanyId));

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
				_SQL_SELECT_COMMERCECURRENCY_WHERE,
				_SQL_COUNT_COMMERCECURRENCY_WHERE,
				CommerceCurrencyModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceCurrency.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CommerceCurrency::getCompanyId));

		_uniquePersistenceFinderByC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "code_"}, 0, 2, false,
				CommerceCurrency::getCompanyId,
				convertNullFunction(CommerceCurrency::getCode)),
			_SQL_SELECT_COMMERCECURRENCY_WHERE, "",
			new FinderColumn<>(
				"commerceCurrency.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CommerceCurrency::getCompanyId),
			new FinderColumn<>(
				"commerceCurrency.", "code", "code_", FinderColumn.Type.STRING,
				"=", true, true, CommerceCurrency::getCode));

		_collectionPersistenceFinderByC_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_P",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "primary_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_P",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"companyId", "primary_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_P",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"companyId", "primary_"}, false),
			_SQL_SELECT_COMMERCECURRENCY_WHERE,
			_SQL_COUNT_COMMERCECURRENCY_WHERE,
			CommerceCurrencyModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"commerceCurrency.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CommerceCurrency::getCompanyId),
			new FinderColumn<>(
				"commerceCurrency.", "primary", "primary_",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				CommerceCurrency::isPrimary));

		_collectionPersistenceFinderByC_A = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"companyId", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"companyId", "active_"}, false),
			_SQL_SELECT_COMMERCECURRENCY_WHERE,
			_SQL_COUNT_COMMERCECURRENCY_WHERE,
			CommerceCurrencyModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"commerceCurrency.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CommerceCurrency::getCompanyId),
			new FinderColumn<>(
				"commerceCurrency.", "active", "active_",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				CommerceCurrency::isActive));

		_collectionPersistenceFinderByC_P_A = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_P_A",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Boolean.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "primary_", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_P_A",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"companyId", "primary_", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_P_A",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"companyId", "primary_", "active_"}, false),
			_SQL_SELECT_COMMERCECURRENCY_WHERE,
			_SQL_COUNT_COMMERCECURRENCY_WHERE,
			CommerceCurrencyModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"commerceCurrency.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CommerceCurrency::getCompanyId),
			new FinderColumn<>(
				"commerceCurrency.", "primary", "primary_",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				CommerceCurrency::isPrimary),
			new FinderColumn<>(
				"commerceCurrency.", "active", "active_",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				CommerceCurrency::isActive));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(CommerceCurrency::getExternalReferenceCode),
				CommerceCurrency::getCompanyId),
			_SQL_SELECT_COMMERCECURRENCY_WHERE, "",
			new FinderColumn<>(
				"commerceCurrency.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceCurrency::getExternalReferenceCode),
			new FinderColumn<>(
				"commerceCurrency.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CommerceCurrency::getCompanyId));

		CommerceCurrencyUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceCurrencyUtil.setPersistence(null);

		entityCache.removeCache(CommerceCurrencyImpl.class.getName());
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
		CommerceCurrencyModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCECURRENCY =
		"SELECT commerceCurrency FROM CommerceCurrency commerceCurrency";

	private static final String _SQL_SELECT_COMMERCECURRENCY_WHERE =
		"SELECT commerceCurrency FROM CommerceCurrency commerceCurrency WHERE ";

	private static final String _SQL_COUNT_COMMERCECURRENCY_WHERE =
		"SELECT COUNT(commerceCurrency) FROM CommerceCurrency commerceCurrency WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceCurrency exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceCurrencyPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "code", "primary", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:46837055