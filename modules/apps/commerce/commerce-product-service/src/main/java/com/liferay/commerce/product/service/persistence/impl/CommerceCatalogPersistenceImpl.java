/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.DuplicateCommerceCatalogExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCatalogException;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceCatalogTable;
import com.liferay.commerce.product.model.impl.CommerceCatalogImpl;
import com.liferay.commerce.product.model.impl.CommerceCatalogModelImpl;
import com.liferay.commerce.product.service.persistence.CommerceCatalogPersistence;
import com.liferay.commerce.product.service.persistence.CommerceCatalogUtil;
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
 * The persistence implementation for the commerce catalog service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CommerceCatalogPersistence.class)
public class CommerceCatalogPersistenceImpl
	extends BasePersistenceImpl<CommerceCatalog, NoSuchCatalogException>
	implements CommerceCatalogPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceCatalogUtil</code> to access the commerce catalog persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceCatalogImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<CommerceCatalog, NoSuchCatalogException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce catalogs where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceCatalogModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce catalogs
	 * @param end the upper bound of the range of commerce catalogs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce catalogs
	 */
	@Override
	public List<CommerceCatalog> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceCatalog> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce catalog in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce catalog
	 * @throws NoSuchCatalogException if a matching commerce catalog could not be found
	 */
	@Override
	public CommerceCatalog findByUuid_First(
			String uuid, OrderByComparator<CommerceCatalog> orderByComparator)
		throws NoSuchCatalogException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce catalog in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce catalog, or <code>null</code> if a matching commerce catalog could not be found
	 */
	@Override
	public CommerceCatalog fetchByUuid_First(
		String uuid, OrderByComparator<CommerceCatalog> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce catalogs that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceCatalogModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce catalogs
	 * @param end the upper bound of the range of commerce catalogs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce catalogs that the user has permission to view
	 */
	@Override
	public List<CommerceCatalog> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceCatalog> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			finderCache, new Object[] {uuid}, start, end, orderByComparator);
	}

	/**
	 * Removes all the commerce catalogs where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce catalogs where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce catalogs
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce catalogs that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce catalogs that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			finderCache, new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder
		<CommerceCatalog, NoSuchCatalogException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce catalogs where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceCatalogModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce catalogs
	 * @param end the upper bound of the range of commerce catalogs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce catalogs
	 */
	@Override
	public List<CommerceCatalog> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceCatalog> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce catalog in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce catalog
	 * @throws NoSuchCatalogException if a matching commerce catalog could not be found
	 */
	@Override
	public CommerceCatalog findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceCatalog> orderByComparator)
		throws NoSuchCatalogException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce catalog in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce catalog, or <code>null</code> if a matching commerce catalog could not be found
	 */
	@Override
	public CommerceCatalog fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceCatalog> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce catalogs that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceCatalogModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce catalogs
	 * @param end the upper bound of the range of commerce catalogs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce catalogs that the user has permission to view
	 */
	@Override
	public List<CommerceCatalog> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceCatalog> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the commerce catalogs where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce catalogs where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce catalogs
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of commerce catalogs that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce catalogs that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			finderCache, new Object[] {uuid, companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<CommerceCatalog, NoSuchCatalogException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the commerce catalogs where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceCatalogModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce catalogs
	 * @param end the upper bound of the range of commerce catalogs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce catalogs
	 */
	@Override
	public List<CommerceCatalog> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceCatalog> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce catalog in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce catalog
	 * @throws NoSuchCatalogException if a matching commerce catalog could not be found
	 */
	@Override
	public CommerceCatalog findByCompanyId_First(
			long companyId,
			OrderByComparator<CommerceCatalog> orderByComparator)
		throws NoSuchCatalogException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce catalog in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce catalog, or <code>null</code> if a matching commerce catalog could not be found
	 */
	@Override
	public CommerceCatalog fetchByCompanyId_First(
		long companyId, OrderByComparator<CommerceCatalog> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce catalogs that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceCatalogModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce catalogs
	 * @param end the upper bound of the range of commerce catalogs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce catalogs that the user has permission to view
	 */
	@Override
	public List<CommerceCatalog> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceCatalog> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the commerce catalogs where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of commerce catalogs where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce catalogs
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of commerce catalogs that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce catalogs that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<CommerceCatalog, NoSuchCatalogException>
			_collectionPersistenceFinderByAccountEntryId;

	/**
	 * Returns an ordered range of all the commerce catalogs where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceCatalogModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of commerce catalogs
	 * @param end the upper bound of the range of commerce catalogs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce catalogs
	 */
	@Override
	public List<CommerceCatalog> findByAccountEntryId(
		long accountEntryId, int start, int end,
		OrderByComparator<CommerceCatalog> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAccountEntryId.find(
			finderCache, new Object[] {accountEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce catalog in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce catalog
	 * @throws NoSuchCatalogException if a matching commerce catalog could not be found
	 */
	@Override
	public CommerceCatalog findByAccountEntryId_First(
			long accountEntryId,
			OrderByComparator<CommerceCatalog> orderByComparator)
		throws NoSuchCatalogException {

		return _collectionPersistenceFinderByAccountEntryId.findFirst(
			finderCache, new Object[] {accountEntryId}, orderByComparator);
	}

	/**
	 * Returns the first commerce catalog in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce catalog, or <code>null</code> if a matching commerce catalog could not be found
	 */
	@Override
	public CommerceCatalog fetchByAccountEntryId_First(
		long accountEntryId,
		OrderByComparator<CommerceCatalog> orderByComparator) {

		return _collectionPersistenceFinderByAccountEntryId.fetchFirst(
			finderCache, new Object[] {accountEntryId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce catalogs that the user has permissions to view where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceCatalogModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of commerce catalogs
	 * @param end the upper bound of the range of commerce catalogs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce catalogs that the user has permission to view
	 */
	@Override
	public List<CommerceCatalog> filterFindByAccountEntryId(
		long accountEntryId, int start, int end,
		OrderByComparator<CommerceCatalog> orderByComparator) {

		return _collectionPersistenceFinderByAccountEntryId.filterFind(
			finderCache, new Object[] {accountEntryId}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the commerce catalogs where accountEntryId = &#63; from the database.
	 *
	 * @param accountEntryId the account entry ID
	 */
	@Override
	public void removeByAccountEntryId(long accountEntryId) {
		_collectionPersistenceFinderByAccountEntryId.remove(
			finderCache, new Object[] {accountEntryId});
	}

	/**
	 * Returns the number of commerce catalogs where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @return the number of matching commerce catalogs
	 */
	@Override
	public int countByAccountEntryId(long accountEntryId) {
		return _collectionPersistenceFinderByAccountEntryId.count(
			finderCache, new Object[] {accountEntryId});
	}

	/**
	 * Returns the number of commerce catalogs that the user has permission to view where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @return the number of matching commerce catalogs that the user has permission to view
	 */
	@Override
	public int filterCountByAccountEntryId(long accountEntryId) {
		return _collectionPersistenceFinderByAccountEntryId.filterCount(
			finderCache, new Object[] {accountEntryId});
	}

	private FilterCollectionPersistenceFinder
		<CommerceCatalog, NoSuchCatalogException>
			_collectionPersistenceFinderByC_S;

	/**
	 * Returns an ordered range of all the commerce catalogs where companyId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceCatalogModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param system the system
	 * @param start the lower bound of the range of commerce catalogs
	 * @param end the upper bound of the range of commerce catalogs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce catalogs
	 */
	@Override
	public List<CommerceCatalog> findByC_S(
		long companyId, boolean system, int start, int end,
		OrderByComparator<CommerceCatalog> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S.find(
			finderCache, new Object[] {companyId, system}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce catalog in the ordered set where companyId = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce catalog
	 * @throws NoSuchCatalogException if a matching commerce catalog could not be found
	 */
	@Override
	public CommerceCatalog findByC_S_First(
			long companyId, boolean system,
			OrderByComparator<CommerceCatalog> orderByComparator)
		throws NoSuchCatalogException {

		return _collectionPersistenceFinderByC_S.findFirst(
			finderCache, new Object[] {companyId, system}, orderByComparator);
	}

	/**
	 * Returns the first commerce catalog in the ordered set where companyId = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce catalog, or <code>null</code> if a matching commerce catalog could not be found
	 */
	@Override
	public CommerceCatalog fetchByC_S_First(
		long companyId, boolean system,
		OrderByComparator<CommerceCatalog> orderByComparator) {

		return _collectionPersistenceFinderByC_S.fetchFirst(
			finderCache, new Object[] {companyId, system}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce catalogs that the user has permissions to view where companyId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceCatalogModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param system the system
	 * @param start the lower bound of the range of commerce catalogs
	 * @param end the upper bound of the range of commerce catalogs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce catalogs that the user has permission to view
	 */
	@Override
	public List<CommerceCatalog> filterFindByC_S(
		long companyId, boolean system, int start, int end,
		OrderByComparator<CommerceCatalog> orderByComparator) {

		return _collectionPersistenceFinderByC_S.filterFind(
			finderCache, new Object[] {companyId, system}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the commerce catalogs where companyId = &#63; and system = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param system the system
	 */
	@Override
	public void removeByC_S(long companyId, boolean system) {
		_collectionPersistenceFinderByC_S.remove(
			finderCache, new Object[] {companyId, system});
	}

	/**
	 * Returns the number of commerce catalogs where companyId = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param system the system
	 * @return the number of matching commerce catalogs
	 */
	@Override
	public int countByC_S(long companyId, boolean system) {
		return _collectionPersistenceFinderByC_S.count(
			finderCache, new Object[] {companyId, system});
	}

	/**
	 * Returns the number of commerce catalogs that the user has permission to view where companyId = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param system the system
	 * @return the number of matching commerce catalogs that the user has permission to view
	 */
	@Override
	public int filterCountByC_S(long companyId, boolean system) {
		return _collectionPersistenceFinderByC_S.filterCount(
			finderCache, new Object[] {companyId, system}, companyId, 0);
	}

	private UniquePersistenceFinder<CommerceCatalog, NoSuchCatalogException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the commerce catalog where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCatalogException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce catalog
	 * @throws NoSuchCatalogException if a matching commerce catalog could not be found
	 */
	@Override
	public CommerceCatalog findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCatalogException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the commerce catalog where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce catalog, or <code>null</code> if a matching commerce catalog could not be found
	 */
	@Override
	public CommerceCatalog fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the commerce catalog where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce catalog that was removed
	 */
	@Override
	public CommerceCatalog removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCatalogException {

		CommerceCatalog commerceCatalog = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commerceCatalog);
	}

	/**
	 * Returns the number of commerce catalogs where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce catalogs
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CommerceCatalogPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("system", "system_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceCatalog.class);

		setModelImplClass(CommerceCatalogImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceCatalogTable.INSTANCE);
	}

	/**
	 * Creates a new commerce catalog with the primary key. Does not add the commerce catalog to the database.
	 *
	 * @param commerceCatalogId the primary key for the new commerce catalog
	 * @return the new commerce catalog
	 */
	@Override
	public CommerceCatalog create(long commerceCatalogId) {
		CommerceCatalog commerceCatalog = new CommerceCatalogImpl();

		commerceCatalog.setNew(true);
		commerceCatalog.setPrimaryKey(commerceCatalogId);

		String uuid = PortalUUIDUtil.generate();

		commerceCatalog.setUuid(uuid);

		commerceCatalog.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceCatalog;
	}

	/**
	 * Removes the commerce catalog with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceCatalogId the primary key of the commerce catalog
	 * @return the commerce catalog that was removed
	 * @throws NoSuchCatalogException if a commerce catalog with the primary key could not be found
	 */
	@Override
	public CommerceCatalog remove(long commerceCatalogId)
		throws NoSuchCatalogException {

		return remove((Serializable)commerceCatalogId);
	}

	@Override
	protected CommerceCatalog removeImpl(CommerceCatalog commerceCatalog) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceCatalog)) {
				commerceCatalog = (CommerceCatalog)session.get(
					CommerceCatalogImpl.class,
					commerceCatalog.getPrimaryKeyObj());
			}

			if ((commerceCatalog != null) &&
				ctPersistenceHelper.isRemove(commerceCatalog)) {

				session.delete(commerceCatalog);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceCatalog != null) {
			clearCache(commerceCatalog);
		}

		return commerceCatalog;
	}

	@Override
	public CommerceCatalog updateImpl(CommerceCatalog commerceCatalog) {
		boolean isNew = commerceCatalog.isNew();

		if (!(commerceCatalog instanceof CommerceCatalogModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceCatalog.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceCatalog);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceCatalog proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceCatalog implementation " +
					commerceCatalog.getClass());
		}

		CommerceCatalogModelImpl commerceCatalogModelImpl =
			(CommerceCatalogModelImpl)commerceCatalog;

		if (Validator.isNull(commerceCatalog.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceCatalog.setUuid(uuid);
		}

		if (Validator.isNull(commerceCatalog.getExternalReferenceCode())) {
			commerceCatalog.setExternalReferenceCode(commerceCatalog.getUuid());
		}
		else {
			if (!Objects.equals(
					commerceCatalogModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commerceCatalog.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commerceCatalog.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = commerceCatalog.getPrimaryKey();
					}

					try {
						commerceCatalog.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommerceCatalog.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								commerceCatalog.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommerceCatalog ercCommerceCatalog = fetchByERC_C(
				commerceCatalog.getExternalReferenceCode(),
				commerceCatalog.getCompanyId());

			if (isNew) {
				if (ercCommerceCatalog != null) {
					throw new DuplicateCommerceCatalogExternalReferenceCodeException(
						"Duplicate commerce catalog with external reference code " +
							commerceCatalog.getExternalReferenceCode() +
								" and company " +
									commerceCatalog.getCompanyId());
				}
			}
			else {
				if ((ercCommerceCatalog != null) &&
					(commerceCatalog.getCommerceCatalogId() !=
						ercCommerceCatalog.getCommerceCatalogId())) {

					throw new DuplicateCommerceCatalogExternalReferenceCodeException(
						"Duplicate commerce catalog with external reference code " +
							commerceCatalog.getExternalReferenceCode() +
								" and company " +
									commerceCatalog.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceCatalog.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceCatalog.setCreateDate(date);
			}
			else {
				commerceCatalog.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceCatalogModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceCatalog.setModifiedDate(date);
			}
			else {
				commerceCatalog.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(commerceCatalog)) {
				if (!isNew) {
					session.evict(
						CommerceCatalogImpl.class,
						commerceCatalog.getPrimaryKeyObj());
				}

				session.save(commerceCatalog);
			}
			else {
				commerceCatalog = (CommerceCatalog)session.merge(
					commerceCatalog);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceCatalog, false);

		if (isNew) {
			commerceCatalog.setNew(false);
		}

		commerceCatalog.resetOriginalValues();

		return commerceCatalog;
	}

	/**
	 * Returns the commerce catalog with the primary key or throws a <code>NoSuchCatalogException</code> if it could not be found.
	 *
	 * @param commerceCatalogId the primary key of the commerce catalog
	 * @return the commerce catalog
	 * @throws NoSuchCatalogException if a commerce catalog with the primary key could not be found
	 */
	@Override
	public CommerceCatalog findByPrimaryKey(long commerceCatalogId)
		throws NoSuchCatalogException {

		return findByPrimaryKey((Serializable)commerceCatalogId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the commerce catalog with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceCatalogId the primary key of the commerce catalog
	 * @return the commerce catalog, or <code>null</code> if a commerce catalog with the primary key could not be found
	 */
	@Override
	public CommerceCatalog fetchByPrimaryKey(long commerceCatalogId) {
		return fetchByPrimaryKey((Serializable)commerceCatalogId);
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
		return "commerceCatalogId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCECATALOG;
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
		return CommerceCatalogModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CommerceCatalog";
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
		ctMergeColumnNames.add("accountEntryId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("commerceCurrencyCode");
		ctMergeColumnNames.add("catalogDefaultLanguageId");
		ctMergeColumnNames.add("system_");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("commerceCatalogId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the commerce catalog persistence.
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
				_SQL_SELECT_COMMERCECATALOG_WHERE,
				_SQL_COUNT_COMMERCECATALOG_WHERE,
				CommerceCatalogModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceCatalog.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceCatalog::getUuid));

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
				_SQL_SELECT_COMMERCECATALOG_WHERE,
				_SQL_COUNT_COMMERCECATALOG_WHERE,
				CommerceCatalogModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceCatalog.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceCatalog::getUuid),
				new FinderColumn<>(
					"commerceCatalog.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CommerceCatalog::getCompanyId));

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
				_SQL_SELECT_COMMERCECATALOG_WHERE,
				_SQL_COUNT_COMMERCECATALOG_WHERE,
				CommerceCatalogModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceCatalog.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CommerceCatalog::getCompanyId));

		_collectionPersistenceFinderByAccountEntryId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByAccountEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"accountEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByAccountEntryId", new String[] {Long.class.getName()},
					new String[] {"accountEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByAccountEntryId",
					new String[] {Long.class.getName()},
					new String[] {"accountEntryId"}, false),
				_SQL_SELECT_COMMERCECATALOG_WHERE,
				_SQL_COUNT_COMMERCECATALOG_WHERE,
				CommerceCatalogModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceCatalog.", "accountEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceCatalog::getAccountEntryId));

		_collectionPersistenceFinderByC_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "system_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "system_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "system_"}, false),
				_SQL_SELECT_COMMERCECATALOG_WHERE,
				_SQL_COUNT_COMMERCECATALOG_WHERE,
				CommerceCatalogModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceCatalog.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CommerceCatalog::getCompanyId),
				new FinderColumn<>(
					"commerceCatalog.", "system", "system_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					CommerceCatalog::isSystem));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(CommerceCatalog::getExternalReferenceCode),
				CommerceCatalog::getCompanyId),
			_SQL_SELECT_COMMERCECATALOG_WHERE, "",
			new FinderColumn<>(
				"commerceCatalog.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceCatalog::getExternalReferenceCode),
			new FinderColumn<>(
				"commerceCatalog.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CommerceCatalog::getCompanyId));

		CommerceCatalogUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceCatalogUtil.setPersistence(null);

		entityCache.removeCache(CommerceCatalogImpl.class.getName());
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
		CommerceCatalogModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCECATALOG =
		"SELECT commerceCatalog FROM CommerceCatalog commerceCatalog";

	private static final String _SQL_SELECT_COMMERCECATALOG_WHERE =
		"SELECT commerceCatalog FROM CommerceCatalog commerceCatalog WHERE ";

	private static final String _SQL_COUNT_COMMERCECATALOG_WHERE =
		"SELECT COUNT(commerceCatalog) FROM CommerceCatalog commerceCatalog WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceCatalog exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceCatalogPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "system"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:192323511