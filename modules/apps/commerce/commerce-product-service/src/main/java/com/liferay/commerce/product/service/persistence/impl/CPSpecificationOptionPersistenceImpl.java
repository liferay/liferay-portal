/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.DuplicateCPSpecificationOptionExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPSpecificationOptionException;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.model.CPSpecificationOptionTable;
import com.liferay.commerce.product.model.impl.CPSpecificationOptionImpl;
import com.liferay.commerce.product.model.impl.CPSpecificationOptionModelImpl;
import com.liferay.commerce.product.service.persistence.CPSpecificationOptionPersistence;
import com.liferay.commerce.product.service.persistence.CPSpecificationOptionUtil;
import com.liferay.commerce.product.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
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
 * The persistence implementation for the cp specification option service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPSpecificationOptionPersistence.class)
public class CPSpecificationOptionPersistenceImpl
	extends BasePersistenceImpl
		<CPSpecificationOption, NoSuchCPSpecificationOptionException>
	implements CPSpecificationOptionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPSpecificationOptionUtil</code> to access the cp specification option persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPSpecificationOptionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<CPSpecificationOption, NoSuchCPSpecificationOptionException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cp specification options where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp specification option in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption findByUuid_First(
			String uuid,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cp specification option in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption fetchByUuid_First(
		String uuid,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp specification options that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp specification options that the user has permission to view
	 */
	@Override
	public List<CPSpecificationOption> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			finderCache, new Object[] {uuid}, start, end, orderByComparator);
	}

	/**
	 * Removes all the cp specification options where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cp specification options where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp specification options
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cp specification options that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp specification options that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			finderCache, new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder
		<CPSpecificationOption, NoSuchCPSpecificationOptionException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cp specification options where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp specification option in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp specification option in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp specification options that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp specification options that the user has permission to view
	 */
	@Override
	public List<CPSpecificationOption> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the cp specification options where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of cp specification options where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp specification options
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of cp specification options that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp specification options that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			finderCache, new Object[] {uuid, companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<CPSpecificationOption, NoSuchCPSpecificationOptionException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the cp specification options where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp specification option in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption findByCompanyId_First(
			long companyId,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp specification option in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp specification options that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp specification options that the user has permission to view
	 */
	@Override
	public List<CPSpecificationOption> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the cp specification options where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of cp specification options where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp specification options
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of cp specification options that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp specification options that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<CPSpecificationOption, NoSuchCPSpecificationOptionException>
			_collectionPersistenceFinderByCPOptionCategoryId;

	/**
	 * Returns an ordered range of all the cp specification options where CPOptionCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByCPOptionCategoryId(
		long CPOptionCategoryId, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPOptionCategoryId.find(
			finderCache, new Object[] {CPOptionCategoryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp specification option in the ordered set where CPOptionCategoryId = &#63;.
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption findByCPOptionCategoryId_First(
			long CPOptionCategoryId,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		return _collectionPersistenceFinderByCPOptionCategoryId.findFirst(
			finderCache, new Object[] {CPOptionCategoryId}, orderByComparator);
	}

	/**
	 * Returns the first cp specification option in the ordered set where CPOptionCategoryId = &#63;.
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption fetchByCPOptionCategoryId_First(
		long CPOptionCategoryId,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		return _collectionPersistenceFinderByCPOptionCategoryId.fetchFirst(
			finderCache, new Object[] {CPOptionCategoryId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp specification options that the user has permissions to view where CPOptionCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp specification options that the user has permission to view
	 */
	@Override
	public List<CPSpecificationOption> filterFindByCPOptionCategoryId(
		long CPOptionCategoryId, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		return _collectionPersistenceFinderByCPOptionCategoryId.filterFind(
			finderCache, new Object[] {CPOptionCategoryId}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the cp specification options where CPOptionCategoryId = &#63; from the database.
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 */
	@Override
	public void removeByCPOptionCategoryId(long CPOptionCategoryId) {
		_collectionPersistenceFinderByCPOptionCategoryId.remove(
			finderCache, new Object[] {CPOptionCategoryId});
	}

	/**
	 * Returns the number of cp specification options where CPOptionCategoryId = &#63;.
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @return the number of matching cp specification options
	 */
	@Override
	public int countByCPOptionCategoryId(long CPOptionCategoryId) {
		return _collectionPersistenceFinderByCPOptionCategoryId.count(
			finderCache, new Object[] {CPOptionCategoryId});
	}

	/**
	 * Returns the number of cp specification options that the user has permission to view where CPOptionCategoryId = &#63;.
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @return the number of matching cp specification options that the user has permission to view
	 */
	@Override
	public int filterCountByCPOptionCategoryId(long CPOptionCategoryId) {
		return _collectionPersistenceFinderByCPOptionCategoryId.filterCount(
			finderCache, new Object[] {CPOptionCategoryId});
	}

	private UniquePersistenceFinder
		<CPSpecificationOption, NoSuchCPSpecificationOptionException>
			_uniquePersistenceFinderByC_K;

	/**
	 * Returns the cp specification option where companyId = &#63; and key = &#63; or throws a <code>NoSuchCPSpecificationOptionException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @return the matching cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption findByC_K(long companyId, String key)
		throws NoSuchCPSpecificationOptionException {

		return _uniquePersistenceFinderByC_K.find(
			finderCache, new Object[] {companyId, key});
	}

	/**
	 * Returns the cp specification option where companyId = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption fetchByC_K(
		long companyId, String key, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_K.fetch(
			finderCache, new Object[] {companyId, key}, useFinderCache);
	}

	/**
	 * Removes the cp specification option where companyId = &#63; and key = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @return the cp specification option that was removed
	 */
	@Override
	public CPSpecificationOption removeByC_K(long companyId, String key)
		throws NoSuchCPSpecificationOptionException {

		CPSpecificationOption cpSpecificationOption = findByC_K(companyId, key);

		return remove(cpSpecificationOption);
	}

	/**
	 * Returns the number of cp specification options where companyId = &#63; and key = &#63;.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @return the number of matching cp specification options
	 */
	@Override
	public int countByC_K(long companyId, String key) {
		return _uniquePersistenceFinderByC_K.count(
			finderCache, new Object[] {companyId, key});
	}

	private UniquePersistenceFinder
		<CPSpecificationOption, NoSuchCPSpecificationOptionException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the cp specification option where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPSpecificationOptionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPSpecificationOptionException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the cp specification option where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the cp specification option where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp specification option that was removed
	 */
	@Override
	public CPSpecificationOption removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPSpecificationOptionException {

		CPSpecificationOption cpSpecificationOption = findByERC_C(
			externalReferenceCode, companyId);

		return remove(cpSpecificationOption);
	}

	/**
	 * Returns the number of cp specification options where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp specification options
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CPSpecificationOptionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("key", "key_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPSpecificationOption.class);

		setModelImplClass(CPSpecificationOptionImpl.class);
		setModelPKClass(long.class);

		setTable(CPSpecificationOptionTable.INSTANCE);
	}

	/**
	 * Creates a new cp specification option with the primary key. Does not add the cp specification option to the database.
	 *
	 * @param CPSpecificationOptionId the primary key for the new cp specification option
	 * @return the new cp specification option
	 */
	@Override
	public CPSpecificationOption create(long CPSpecificationOptionId) {
		CPSpecificationOption cpSpecificationOption =
			new CPSpecificationOptionImpl();

		cpSpecificationOption.setNew(true);
		cpSpecificationOption.setPrimaryKey(CPSpecificationOptionId);

		String uuid = PortalUUIDUtil.generate();

		cpSpecificationOption.setUuid(uuid);

		cpSpecificationOption.setCompanyId(CompanyThreadLocal.getCompanyId());

		return cpSpecificationOption;
	}

	/**
	 * Removes the cp specification option with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPSpecificationOptionId the primary key of the cp specification option
	 * @return the cp specification option that was removed
	 * @throws NoSuchCPSpecificationOptionException if a cp specification option with the primary key could not be found
	 */
	@Override
	public CPSpecificationOption remove(long CPSpecificationOptionId)
		throws NoSuchCPSpecificationOptionException {

		return remove((Serializable)CPSpecificationOptionId);
	}

	@Override
	protected CPSpecificationOption removeImpl(
		CPSpecificationOption cpSpecificationOption) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpSpecificationOption)) {
				cpSpecificationOption = (CPSpecificationOption)session.get(
					CPSpecificationOptionImpl.class,
					cpSpecificationOption.getPrimaryKeyObj());
			}

			if ((cpSpecificationOption != null) &&
				ctPersistenceHelper.isRemove(cpSpecificationOption)) {

				session.delete(cpSpecificationOption);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpSpecificationOption != null) {
			clearCache(cpSpecificationOption);
		}

		return cpSpecificationOption;
	}

	@Override
	public CPSpecificationOption updateImpl(
		CPSpecificationOption cpSpecificationOption) {

		boolean isNew = cpSpecificationOption.isNew();

		if (!(cpSpecificationOption instanceof
				CPSpecificationOptionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpSpecificationOption.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpSpecificationOption);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpSpecificationOption proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPSpecificationOption implementation " +
					cpSpecificationOption.getClass());
		}

		CPSpecificationOptionModelImpl cpSpecificationOptionModelImpl =
			(CPSpecificationOptionModelImpl)cpSpecificationOption;

		if (Validator.isNull(cpSpecificationOption.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpSpecificationOption.setUuid(uuid);
		}

		if (Validator.isNull(
				cpSpecificationOption.getExternalReferenceCode())) {

			cpSpecificationOption.setExternalReferenceCode(
				cpSpecificationOption.getUuid());
		}
		else {
			if (!Objects.equals(
					cpSpecificationOptionModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					cpSpecificationOption.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = cpSpecificationOption.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = cpSpecificationOption.getPrimaryKey();
					}

					try {
						cpSpecificationOption.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CPSpecificationOption.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								cpSpecificationOption.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CPSpecificationOption ercCPSpecificationOption = fetchByERC_C(
				cpSpecificationOption.getExternalReferenceCode(),
				cpSpecificationOption.getCompanyId());

			if (isNew) {
				if (ercCPSpecificationOption != null) {
					throw new DuplicateCPSpecificationOptionExternalReferenceCodeException(
						"Duplicate cp specification option with external reference code " +
							cpSpecificationOption.getExternalReferenceCode() +
								" and company " +
									cpSpecificationOption.getCompanyId());
				}
			}
			else {
				if ((ercCPSpecificationOption != null) &&
					(cpSpecificationOption.getCPSpecificationOptionId() !=
						ercCPSpecificationOption.
							getCPSpecificationOptionId())) {

					throw new DuplicateCPSpecificationOptionExternalReferenceCodeException(
						"Duplicate cp specification option with external reference code " +
							cpSpecificationOption.getExternalReferenceCode() +
								" and company " +
									cpSpecificationOption.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpSpecificationOption.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpSpecificationOption.setCreateDate(date);
			}
			else {
				cpSpecificationOption.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!cpSpecificationOptionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpSpecificationOption.setModifiedDate(date);
			}
			else {
				cpSpecificationOption.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cpSpecificationOption)) {
				if (!isNew) {
					session.evict(
						CPSpecificationOptionImpl.class,
						cpSpecificationOption.getPrimaryKeyObj());
				}

				session.save(cpSpecificationOption);
			}
			else {
				cpSpecificationOption = (CPSpecificationOption)session.merge(
					cpSpecificationOption);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cpSpecificationOption, false);

		if (isNew) {
			cpSpecificationOption.setNew(false);
		}

		cpSpecificationOption.resetOriginalValues();

		return cpSpecificationOption;
	}

	/**
	 * Returns the cp specification option with the primary key or throws a <code>NoSuchCPSpecificationOptionException</code> if it could not be found.
	 *
	 * @param CPSpecificationOptionId the primary key of the cp specification option
	 * @return the cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a cp specification option with the primary key could not be found
	 */
	@Override
	public CPSpecificationOption findByPrimaryKey(long CPSpecificationOptionId)
		throws NoSuchCPSpecificationOptionException {

		return findByPrimaryKey((Serializable)CPSpecificationOptionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cp specification option with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPSpecificationOptionId the primary key of the cp specification option
	 * @return the cp specification option, or <code>null</code> if a cp specification option with the primary key could not be found
	 */
	@Override
	public CPSpecificationOption fetchByPrimaryKey(
		long CPSpecificationOptionId) {

		return fetchByPrimaryKey((Serializable)CPSpecificationOptionId);
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
		return "CPSpecificationOptionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPSPECIFICATIONOPTION;
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
		return CPSpecificationOptionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPSpecificationOption";
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
		ctMergeColumnNames.add("CPOptionCategoryId");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("facetable");
		ctMergeColumnNames.add("key_");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("visible");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CPSpecificationOptionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"companyId", "key_"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the cp specification option persistence.
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
				_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE,
				_SQL_COUNT_CPSPECIFICATIONOPTION_WHERE,
				CPSpecificationOptionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpSpecificationOption.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CPSpecificationOption::getUuid));

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
				_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE,
				_SQL_COUNT_CPSPECIFICATIONOPTION_WHERE,
				CPSpecificationOptionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpSpecificationOption.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CPSpecificationOption::getUuid),
				new FinderColumn<>(
					"cpSpecificationOption.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CPSpecificationOption::getCompanyId));

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
				_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE,
				_SQL_COUNT_CPSPECIFICATIONOPTION_WHERE,
				CPSpecificationOptionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpSpecificationOption.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CPSpecificationOption::getCompanyId));

		_collectionPersistenceFinderByCPOptionCategoryId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPOptionCategoryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPOptionCategoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPOptionCategoryId",
					new String[] {Long.class.getName()},
					new String[] {"CPOptionCategoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPOptionCategoryId",
					new String[] {Long.class.getName()},
					new String[] {"CPOptionCategoryId"}, false),
				_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE,
				_SQL_COUNT_CPSPECIFICATIONOPTION_WHERE,
				CPSpecificationOptionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpSpecificationOption.", "CPOptionCategoryId",
					FinderColumn.Type.LONG, "=", true, true,
					CPSpecificationOption::getCPOptionCategoryId));

		_uniquePersistenceFinderByC_K = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_K",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "key_"}, 0, 2, false,
				CPSpecificationOption::getCompanyId,
				convertNullFunction(CPSpecificationOption::getKey)),
			_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE, "",
			new FinderColumn<>(
				"cpSpecificationOption.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, CPSpecificationOption::getCompanyId),
			new FinderColumn<>(
				"cpSpecificationOption.", "key", "key_",
				FinderColumn.Type.STRING, "=", true, true,
				CPSpecificationOption::getKey));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					CPSpecificationOption::getExternalReferenceCode),
				CPSpecificationOption::getCompanyId),
			_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE, "",
			new FinderColumn<>(
				"cpSpecificationOption.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CPSpecificationOption::getExternalReferenceCode),
			new FinderColumn<>(
				"cpSpecificationOption.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, CPSpecificationOption::getCompanyId));

		CPSpecificationOptionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPSpecificationOptionUtil.setPersistence(null);

		entityCache.removeCache(CPSpecificationOptionImpl.class.getName());
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
		CPSpecificationOptionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPSPECIFICATIONOPTION =
		"SELECT cpSpecificationOption FROM CPSpecificationOption cpSpecificationOption";

	private static final String _SQL_SELECT_CPSPECIFICATIONOPTION_WHERE =
		"SELECT cpSpecificationOption FROM CPSpecificationOption cpSpecificationOption WHERE ";

	private static final String _SQL_COUNT_CPSPECIFICATIONOPTION_WHERE =
		"SELECT COUNT(cpSpecificationOption) FROM CPSpecificationOption cpSpecificationOption WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "key"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2026949558