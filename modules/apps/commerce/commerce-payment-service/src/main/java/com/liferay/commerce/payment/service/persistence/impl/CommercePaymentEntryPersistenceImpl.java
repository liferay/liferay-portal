/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.persistence.impl;

import com.liferay.commerce.payment.exception.DuplicateCommercePaymentEntryExternalReferenceCodeException;
import com.liferay.commerce.payment.exception.NoSuchPaymentEntryException;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.model.CommercePaymentEntryTable;
import com.liferay.commerce.payment.model.impl.CommercePaymentEntryImpl;
import com.liferay.commerce.payment.model.impl.CommercePaymentEntryModelImpl;
import com.liferay.commerce.payment.service.persistence.CommercePaymentEntryPersistence;
import com.liferay.commerce.payment.service.persistence.CommercePaymentEntryUtil;
import com.liferay.commerce.payment.service.persistence.impl.constants.CommercePersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
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
 * The persistence implementation for the commerce payment entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(service = CommercePaymentEntryPersistence.class)
public class CommercePaymentEntryPersistenceImpl
	extends BasePersistenceImpl
		<CommercePaymentEntry, NoSuchPaymentEntryException>
	implements CommercePaymentEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommercePaymentEntryUtil</code> to access the commerce payment entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommercePaymentEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<CommercePaymentEntry, NoSuchPaymentEntryException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the commerce payment entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommercePaymentEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce payment entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry
	 * @throws NoSuchPaymentEntryException if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry findByCompanyId_First(
			long companyId,
			OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce payment entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry, or <code>null</code> if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce payment entries that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntry> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the commerce payment entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of commerce payment entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce payment entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of commerce payment entries that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<CommercePaymentEntry, NoSuchPaymentEntryException>
			_collectionPersistenceFinderByC_C_C;

	/**
	 * Returns an ordered range of all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end,
		OrderByComparator<CommercePaymentEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_C.find(
			finderCache, new Object[] {companyId, classNameId, classPK}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry
	 * @throws NoSuchPaymentEntryException if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry findByC_C_C_First(
			long companyId, long classNameId, long classPK,
			OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		return _collectionPersistenceFinderByC_C_C.findFirst(
			finderCache, new Object[] {companyId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry, or <code>null</code> if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry fetchByC_C_C_First(
		long companyId, long classNameId, long classPK,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C_C.fetchFirst(
			finderCache, new Object[] {companyId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce payment entries that the user has permissions to view where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntry> filterFindByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C_C.filterFind(
			finderCache, new Object[] {companyId, classNameId, classPK}, start,
			end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C_C(long companyId, long classNameId, long classPK) {
		_collectionPersistenceFinderByC_C_C.remove(
			finderCache, new Object[] {companyId, classNameId, classPK});
	}

	/**
	 * Returns the number of commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching commerce payment entries
	 */
	@Override
	public int countByC_C_C(long companyId, long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C_C.count(
			finderCache, new Object[] {companyId, classNameId, classPK});
	}

	/**
	 * Returns the number of commerce payment entries that the user has permission to view where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_C_C(
		long companyId, long classNameId, long classPK) {

		return _collectionPersistenceFinderByC_C_C.filterCount(
			finderCache, new Object[] {companyId, classNameId, classPK},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<CommercePaymentEntry, NoSuchPaymentEntryException>
			_collectionPersistenceFinderByC_C_C_T;

	/**
	 * Returns an ordered range of all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByC_C_C_T(
		long companyId, long classNameId, long classPK, int type, int start,
		int end, OrderByComparator<CommercePaymentEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_C_T.find(
			finderCache, new Object[] {companyId, classNameId, classPK, type},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry
	 * @throws NoSuchPaymentEntryException if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry findByC_C_C_T_First(
			long companyId, long classNameId, long classPK, int type,
			OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		return _collectionPersistenceFinderByC_C_C_T.findFirst(
			finderCache, new Object[] {companyId, classNameId, classPK, type},
			orderByComparator);
	}

	/**
	 * Returns the first commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry, or <code>null</code> if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry fetchByC_C_C_T_First(
		long companyId, long classNameId, long classPK, int type,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C_C_T.fetchFirst(
			finderCache, new Object[] {companyId, classNameId, classPK, type},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce payment entries that the user has permissions to view where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntry> filterFindByC_C_C_T(
		long companyId, long classNameId, long classPK, int type, int start,
		int end, OrderByComparator<CommercePaymentEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C_C_T.filterFind(
			finderCache, new Object[] {companyId, classNameId, classPK, type},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 */
	@Override
	public void removeByC_C_C_T(
		long companyId, long classNameId, long classPK, int type) {

		_collectionPersistenceFinderByC_C_C_T.remove(
			finderCache, new Object[] {companyId, classNameId, classPK, type});
	}

	/**
	 * Returns the number of commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching commerce payment entries
	 */
	@Override
	public int countByC_C_C_T(
		long companyId, long classNameId, long classPK, int type) {

		return _collectionPersistenceFinderByC_C_C_T.count(
			finderCache, new Object[] {companyId, classNameId, classPK, type});
	}

	/**
	 * Returns the number of commerce payment entries that the user has permission to view where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_C_C_T(
		long companyId, long classNameId, long classPK, int type) {

		return _collectionPersistenceFinderByC_C_C_T.filterCount(
			finderCache, new Object[] {companyId, classNameId, classPK, type},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<CommercePaymentEntry, NoSuchPaymentEntryException>
			_collectionPersistenceFinderByC_C_C_P_T;

	/**
	 * Returns an ordered range of all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByC_C_C_P_T(
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type, int start, int end,
		OrderByComparator<CommercePaymentEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_C_P_T.find(
			finderCache,
			new Object[] {companyId, classNameId, classPK, paymentStatus, type},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry
	 * @throws NoSuchPaymentEntryException if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry findByC_C_C_P_T_First(
			long companyId, long classNameId, long classPK, int paymentStatus,
			int type, OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		return _collectionPersistenceFinderByC_C_C_P_T.findFirst(
			finderCache,
			new Object[] {companyId, classNameId, classPK, paymentStatus, type},
			orderByComparator);
	}

	/**
	 * Returns the first commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry, or <code>null</code> if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry fetchByC_C_C_P_T_First(
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type, OrderByComparator<CommercePaymentEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C_C_P_T.fetchFirst(
			finderCache,
			new Object[] {companyId, classNameId, classPK, paymentStatus, type},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce payment entries that the user has permissions to view where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntry> filterFindByC_C_C_P_T(
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type, int start, int end,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C_C_P_T.filterFind(
			finderCache,
			new Object[] {companyId, classNameId, classPK, paymentStatus, type},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 */
	@Override
	public void removeByC_C_C_P_T(
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type) {

		_collectionPersistenceFinderByC_C_C_P_T.remove(
			finderCache,
			new Object[] {
				companyId, classNameId, classPK, paymentStatus, type
			});
	}

	/**
	 * Returns the number of commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @return the number of matching commerce payment entries
	 */
	@Override
	public int countByC_C_C_P_T(
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type) {

		return _collectionPersistenceFinderByC_C_C_P_T.count(
			finderCache,
			new Object[] {
				companyId, classNameId, classPK, paymentStatus, type
			});
	}

	/**
	 * Returns the number of commerce payment entries that the user has permission to view where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @return the number of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_C_C_P_T(
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type) {

		return _collectionPersistenceFinderByC_C_C_P_T.filterCount(
			finderCache,
			new Object[] {companyId, classNameId, classPK, paymentStatus, type},
			companyId, 0);
	}

	private UniquePersistenceFinder
		<CommercePaymentEntry, NoSuchPaymentEntryException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the commerce payment entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchPaymentEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce payment entry
	 * @throws NoSuchPaymentEntryException if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchPaymentEntryException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the commerce payment entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce payment entry, or <code>null</code> if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the commerce payment entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce payment entry that was removed
	 */
	@Override
	public CommercePaymentEntry removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchPaymentEntryException {

		CommercePaymentEntry commercePaymentEntry = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commercePaymentEntry);
	}

	/**
	 * Returns the number of commerce payment entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce payment entries
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CommercePaymentEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommercePaymentEntry.class);

		setModelImplClass(CommercePaymentEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CommercePaymentEntryTable.INSTANCE);
	}

	/**
	 * Creates a new commerce payment entry with the primary key. Does not add the commerce payment entry to the database.
	 *
	 * @param commercePaymentEntryId the primary key for the new commerce payment entry
	 * @return the new commerce payment entry
	 */
	@Override
	public CommercePaymentEntry create(long commercePaymentEntryId) {
		CommercePaymentEntry commercePaymentEntry =
			new CommercePaymentEntryImpl();

		commercePaymentEntry.setNew(true);
		commercePaymentEntry.setPrimaryKey(commercePaymentEntryId);

		commercePaymentEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commercePaymentEntry;
	}

	/**
	 * Removes the commerce payment entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commercePaymentEntryId the primary key of the commerce payment entry
	 * @return the commerce payment entry that was removed
	 * @throws NoSuchPaymentEntryException if a commerce payment entry with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntry remove(long commercePaymentEntryId)
		throws NoSuchPaymentEntryException {

		return remove((Serializable)commercePaymentEntryId);
	}

	@Override
	protected CommercePaymentEntry removeImpl(
		CommercePaymentEntry commercePaymentEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commercePaymentEntry)) {
				commercePaymentEntry = (CommercePaymentEntry)session.get(
					CommercePaymentEntryImpl.class,
					commercePaymentEntry.getPrimaryKeyObj());
			}

			if (commercePaymentEntry != null) {
				session.delete(commercePaymentEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commercePaymentEntry != null) {
			clearCache(commercePaymentEntry);
		}

		return commercePaymentEntry;
	}

	@Override
	public CommercePaymentEntry updateImpl(
		CommercePaymentEntry commercePaymentEntry) {

		boolean isNew = commercePaymentEntry.isNew();

		if (!(commercePaymentEntry instanceof CommercePaymentEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commercePaymentEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commercePaymentEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commercePaymentEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommercePaymentEntry implementation " +
					commercePaymentEntry.getClass());
		}

		CommercePaymentEntryModelImpl commercePaymentEntryModelImpl =
			(CommercePaymentEntryModelImpl)commercePaymentEntry;

		if (Validator.isNull(commercePaymentEntry.getExternalReferenceCode())) {
			commercePaymentEntry.setExternalReferenceCode(
				String.valueOf(commercePaymentEntry.getPrimaryKey()));
		}
		else {
			if (!Objects.equals(
					commercePaymentEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commercePaymentEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commercePaymentEntry.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = commercePaymentEntry.getPrimaryKey();
					}

					try {
						commercePaymentEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommercePaymentEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								commercePaymentEntry.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommercePaymentEntry ercCommercePaymentEntry = fetchByERC_C(
				commercePaymentEntry.getExternalReferenceCode(),
				commercePaymentEntry.getCompanyId());

			if (isNew) {
				if (ercCommercePaymentEntry != null) {
					throw new DuplicateCommercePaymentEntryExternalReferenceCodeException(
						"Duplicate commerce payment entry with external reference code " +
							commercePaymentEntry.getExternalReferenceCode() +
								" and company " +
									commercePaymentEntry.getCompanyId());
				}
			}
			else {
				if ((ercCommercePaymentEntry != null) &&
					(commercePaymentEntry.getCommercePaymentEntryId() !=
						ercCommercePaymentEntry.getCommercePaymentEntryId())) {

					throw new DuplicateCommercePaymentEntryExternalReferenceCodeException(
						"Duplicate commerce payment entry with external reference code " +
							commercePaymentEntry.getExternalReferenceCode() +
								" and company " +
									commercePaymentEntry.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commercePaymentEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				commercePaymentEntry.setCreateDate(date);
			}
			else {
				commercePaymentEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commercePaymentEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commercePaymentEntry.setModifiedDate(date);
			}
			else {
				commercePaymentEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commercePaymentEntry);
			}
			else {
				commercePaymentEntry = (CommercePaymentEntry)session.merge(
					commercePaymentEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commercePaymentEntry, false);

		if (isNew) {
			commercePaymentEntry.setNew(false);
		}

		commercePaymentEntry.resetOriginalValues();

		return commercePaymentEntry;
	}

	/**
	 * Returns the commerce payment entry with the primary key or throws a <code>NoSuchPaymentEntryException</code> if it could not be found.
	 *
	 * @param commercePaymentEntryId the primary key of the commerce payment entry
	 * @return the commerce payment entry
	 * @throws NoSuchPaymentEntryException if a commerce payment entry with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntry findByPrimaryKey(long commercePaymentEntryId)
		throws NoSuchPaymentEntryException {

		return findByPrimaryKey((Serializable)commercePaymentEntryId);
	}

	/**
	 * Returns the commerce payment entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commercePaymentEntryId the primary key of the commerce payment entry
	 * @return the commerce payment entry, or <code>null</code> if a commerce payment entry with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntry fetchByPrimaryKey(long commercePaymentEntryId) {
		return fetchByPrimaryKey((Serializable)commercePaymentEntryId);
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
		return "commercePaymentEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEPAYMENTENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommercePaymentEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce payment entry persistence.
	 */
	@Activate
	public void activate() {
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
				_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE,
				_SQL_COUNT_COMMERCEPAYMENTENTRY_WHERE,
				CommercePaymentEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commercePaymentEntry.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePaymentEntry::getCompanyId));

		_collectionPersistenceFinderByC_C_C =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"companyId", "classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"companyId", "classNameId", "classPK"},
					false),
				_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE,
				_SQL_COUNT_COMMERCEPAYMENTENTRY_WHERE,
				CommercePaymentEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commercePaymentEntry.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePaymentEntry::getCompanyId),
				new FinderColumn<>(
					"commercePaymentEntry.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePaymentEntry::getClassNameId),
				new FinderColumn<>(
					"commercePaymentEntry.", "classPK", FinderColumn.Type.LONG,
					"=", true, true, CommercePaymentEntry::getClassPK));

		_collectionPersistenceFinderByC_C_C_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "type_"
					},
					false),
				_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE,
				_SQL_COUNT_COMMERCEPAYMENTENTRY_WHERE,
				CommercePaymentEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commercePaymentEntry.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePaymentEntry::getCompanyId),
				new FinderColumn<>(
					"commercePaymentEntry.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePaymentEntry::getClassNameId),
				new FinderColumn<>(
					"commercePaymentEntry.", "classPK", FinderColumn.Type.LONG,
					"=", true, true, CommercePaymentEntry::getClassPK),
				new FinderColumn<>(
					"commercePaymentEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					CommercePaymentEntry::getType));

		_collectionPersistenceFinderByC_C_C_P_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_C_P_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "paymentStatus",
						"type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByC_C_C_P_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "paymentStatus",
						"type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByC_C_C_P_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "paymentStatus",
						"type_"
					},
					false),
				_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE,
				_SQL_COUNT_COMMERCEPAYMENTENTRY_WHERE,
				CommercePaymentEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commercePaymentEntry.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePaymentEntry::getCompanyId),
				new FinderColumn<>(
					"commercePaymentEntry.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePaymentEntry::getClassNameId),
				new FinderColumn<>(
					"commercePaymentEntry.", "classPK", FinderColumn.Type.LONG,
					"=", true, true, CommercePaymentEntry::getClassPK),
				new FinderColumn<>(
					"commercePaymentEntry.", "paymentStatus",
					FinderColumn.Type.INTEGER, "=", true, true,
					CommercePaymentEntry::getPaymentStatus),
				new FinderColumn<>(
					"commercePaymentEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					CommercePaymentEntry::getType));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					CommercePaymentEntry::getExternalReferenceCode),
				CommercePaymentEntry::getCompanyId),
			_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE, "",
			new FinderColumn<>(
				"commercePaymentEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CommercePaymentEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"commercePaymentEntry.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, CommercePaymentEntry::getCompanyId));

		CommercePaymentEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommercePaymentEntryUtil.setPersistence(null);

		entityCache.removeCache(CommercePaymentEntryImpl.class.getName());
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
		CommercePaymentEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEPAYMENTENTRY =
		"SELECT commercePaymentEntry FROM CommercePaymentEntry commercePaymentEntry";

	private static final String _SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE =
		"SELECT commercePaymentEntry FROM CommercePaymentEntry commercePaymentEntry WHERE ";

	private static final String _SQL_COUNT_COMMERCEPAYMENTENTRY_WHERE =
		"SELECT COUNT(commercePaymentEntry) FROM CommercePaymentEntry commercePaymentEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommercePaymentEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePaymentEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-584195798