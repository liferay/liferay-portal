/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.service.persistence.impl;

import com.liferay.commerce.order.rule.exception.DuplicateCOREntryExternalReferenceCodeException;
import com.liferay.commerce.order.rule.exception.NoSuchCOREntryException;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.model.COREntryTable;
import com.liferay.commerce.order.rule.model.impl.COREntryImpl;
import com.liferay.commerce.order.rule.model.impl.COREntryModelImpl;
import com.liferay.commerce.order.rule.service.persistence.COREntryPersistence;
import com.liferay.commerce.order.rule.service.persistence.COREntryUtil;
import com.liferay.commerce.order.rule.service.persistence.impl.constants.CORPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
 * The persistence implementation for the cor entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(service = COREntryPersistence.class)
public class COREntryPersistenceImpl
	extends BasePersistenceImpl<COREntry, NoSuchCOREntryException>
	implements COREntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>COREntryUtil</code> to access the cor entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		COREntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder<COREntry, NoSuchCOREntryException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cor entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cor entries
	 */
	@Override
	public List<COREntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<COREntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cor entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cor entry
	 * @throws NoSuchCOREntryException if a matching cor entry could not be found
	 */
	@Override
	public COREntry findByUuid_First(
			String uuid, OrderByComparator<COREntry> orderByComparator)
		throws NoSuchCOREntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cor entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cor entry, or <code>null</code> if a matching cor entry could not be found
	 */
	@Override
	public COREntry fetchByUuid_First(
		String uuid, OrderByComparator<COREntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cor entries that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cor entries that the user has permission to view
	 */
	@Override
	public List<COREntry> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<COREntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			finderCache, new Object[] {uuid}, start, end, orderByComparator);
	}

	/**
	 * Removes all the cor entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cor entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cor entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cor entries that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cor entries that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			finderCache, new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder<COREntry, NoSuchCOREntryException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cor entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cor entries
	 */
	@Override
	public List<COREntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<COREntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cor entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cor entry
	 * @throws NoSuchCOREntryException if a matching cor entry could not be found
	 */
	@Override
	public COREntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<COREntry> orderByComparator)
		throws NoSuchCOREntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cor entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cor entry, or <code>null</code> if a matching cor entry could not be found
	 */
	@Override
	public COREntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<COREntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cor entries that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cor entries that the user has permission to view
	 */
	@Override
	public List<COREntry> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<COREntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the cor entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of cor entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cor entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of cor entries that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cor entries that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			finderCache, new Object[] {uuid, companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder<COREntry, NoSuchCOREntryException>
		_collectionPersistenceFinderByC_A;

	/**
	 * Returns an ordered range of all the cor entries where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cor entries
	 */
	@Override
	public List<COREntry> findByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<COREntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A.find(
			finderCache, new Object[] {companyId, active}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cor entry in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cor entry
	 * @throws NoSuchCOREntryException if a matching cor entry could not be found
	 */
	@Override
	public COREntry findByC_A_First(
			long companyId, boolean active,
			OrderByComparator<COREntry> orderByComparator)
		throws NoSuchCOREntryException {

		return _collectionPersistenceFinderByC_A.findFirst(
			finderCache, new Object[] {companyId, active}, orderByComparator);
	}

	/**
	 * Returns the first cor entry in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cor entry, or <code>null</code> if a matching cor entry could not be found
	 */
	@Override
	public COREntry fetchByC_A_First(
		long companyId, boolean active,
		OrderByComparator<COREntry> orderByComparator) {

		return _collectionPersistenceFinderByC_A.fetchFirst(
			finderCache, new Object[] {companyId, active}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cor entries that the user has permissions to view where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cor entries that the user has permission to view
	 */
	@Override
	public List<COREntry> filterFindByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<COREntry> orderByComparator) {

		return _collectionPersistenceFinderByC_A.filterFind(
			finderCache, new Object[] {companyId, active}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the cor entries where companyId = &#63; and active = &#63; from the database.
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
	 * Returns the number of cor entries where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching cor entries
	 */
	@Override
	public int countByC_A(long companyId, boolean active) {
		return _collectionPersistenceFinderByC_A.count(
			finderCache, new Object[] {companyId, active});
	}

	/**
	 * Returns the number of cor entries that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching cor entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_A(long companyId, boolean active) {
		return _collectionPersistenceFinderByC_A.filterCount(
			finderCache, new Object[] {companyId, active}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder<COREntry, NoSuchCOREntryException>
		_collectionPersistenceFinderByC_LikeType;

	/**
	 * Returns all the cor entries where companyId = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the matching cor entries
	 */
	@Override
	public List<COREntry> findByC_LikeType(long companyId, String type) {
		return findByC_LikeType(
			companyId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cor entries where companyId = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @return the range of matching cor entries
	 */
	@Override
	public List<COREntry> findByC_LikeType(
		long companyId, String type, int start, int end) {

		return findByC_LikeType(companyId, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cor entries where companyId = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cor entries
	 */
	@Override
	public List<COREntry> findByC_LikeType(
		long companyId, String type, int start, int end,
		OrderByComparator<COREntry> orderByComparator) {

		return findByC_LikeType(
			companyId, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cor entries where companyId = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cor entries
	 */
	@Override
	public List<COREntry> findByC_LikeType(
		long companyId, String type, int start, int end,
		OrderByComparator<COREntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_LikeType.find(
			finderCache, new Object[] {companyId, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cor entry in the ordered set where companyId = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cor entry
	 * @throws NoSuchCOREntryException if a matching cor entry could not be found
	 */
	@Override
	public COREntry findByC_LikeType_First(
			long companyId, String type,
			OrderByComparator<COREntry> orderByComparator)
		throws NoSuchCOREntryException {

		return _collectionPersistenceFinderByC_LikeType.findFirst(
			finderCache, new Object[] {companyId, type}, orderByComparator);
	}

	/**
	 * Returns the first cor entry in the ordered set where companyId = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cor entry, or <code>null</code> if a matching cor entry could not be found
	 */
	@Override
	public COREntry fetchByC_LikeType_First(
		long companyId, String type,
		OrderByComparator<COREntry> orderByComparator) {

		return _collectionPersistenceFinderByC_LikeType.fetchFirst(
			finderCache, new Object[] {companyId, type}, orderByComparator);
	}

	/**
	 * Returns all the cor entries that the user has permission to view where companyId = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the matching cor entries that the user has permission to view
	 */
	@Override
	public List<COREntry> filterFindByC_LikeType(long companyId, String type) {
		return filterFindByC_LikeType(
			companyId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cor entries that the user has permission to view where companyId = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @return the range of matching cor entries that the user has permission to view
	 */
	@Override
	public List<COREntry> filterFindByC_LikeType(
		long companyId, String type, int start, int end) {

		return filterFindByC_LikeType(companyId, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cor entries that the user has permissions to view where companyId = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cor entries that the user has permission to view
	 */
	@Override
	public List<COREntry> filterFindByC_LikeType(
		long companyId, String type, int start, int end,
		OrderByComparator<COREntry> orderByComparator) {

		return _collectionPersistenceFinderByC_LikeType.filterFind(
			finderCache, new Object[] {companyId, type}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the cor entries where companyId = &#63; and type LIKE &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 */
	@Override
	public void removeByC_LikeType(long companyId, String type) {
		_collectionPersistenceFinderByC_LikeType.remove(
			finderCache, new Object[] {companyId, type});
	}

	/**
	 * Returns the number of cor entries where companyId = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the number of matching cor entries
	 */
	@Override
	public int countByC_LikeType(long companyId, String type) {
		return _collectionPersistenceFinderByC_LikeType.count(
			finderCache, new Object[] {companyId, type});
	}

	/**
	 * Returns the number of cor entries that the user has permission to view where companyId = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the number of matching cor entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_LikeType(long companyId, String type) {
		return _collectionPersistenceFinderByC_LikeType.filterCount(
			finderCache, new Object[] {companyId, type}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder<COREntry, NoSuchCOREntryException>
		_collectionPersistenceFinderByLtD_S;

	/**
	 * Returns all the cor entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching cor entries
	 */
	@Override
	public List<COREntry> findByLtD_S(Date displayDate, int status) {
		return findByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cor entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @return the range of matching cor entries
	 */
	@Override
	public List<COREntry> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return findByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cor entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cor entries
	 */
	@Override
	public List<COREntry> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<COREntry> orderByComparator) {

		return findByLtD_S(
			displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cor entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cor entries
	 */
	@Override
	public List<COREntry> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<COREntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByLtD_S.find(
			finderCache, new Object[] {displayDate, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cor entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cor entry
	 * @throws NoSuchCOREntryException if a matching cor entry could not be found
	 */
	@Override
	public COREntry findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<COREntry> orderByComparator)
		throws NoSuchCOREntryException {

		return _collectionPersistenceFinderByLtD_S.findFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Returns the first cor entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cor entry, or <code>null</code> if a matching cor entry could not be found
	 */
	@Override
	public COREntry fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<COREntry> orderByComparator) {

		return _collectionPersistenceFinderByLtD_S.fetchFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Returns all the cor entries that the user has permission to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching cor entries that the user has permission to view
	 */
	@Override
	public List<COREntry> filterFindByLtD_S(Date displayDate, int status) {
		return filterFindByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cor entries that the user has permission to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @return the range of matching cor entries that the user has permission to view
	 */
	@Override
	public List<COREntry> filterFindByLtD_S(
		Date displayDate, int status, int start, int end) {

		return filterFindByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cor entries that the user has permissions to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cor entries that the user has permission to view
	 */
	@Override
	public List<COREntry> filterFindByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<COREntry> orderByComparator) {

		return _collectionPersistenceFinderByLtD_S.filterFind(
			finderCache, new Object[] {displayDate, status}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the cor entries where displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByLtD_S(Date displayDate, int status) {
		_collectionPersistenceFinderByLtD_S.remove(
			finderCache, new Object[] {displayDate, status});
	}

	/**
	 * Returns the number of cor entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching cor entries
	 */
	@Override
	public int countByLtD_S(Date displayDate, int status) {
		return _collectionPersistenceFinderByLtD_S.count(
			finderCache, new Object[] {displayDate, status});
	}

	/**
	 * Returns the number of cor entries that the user has permission to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching cor entries that the user has permission to view
	 */
	@Override
	public int filterCountByLtD_S(Date displayDate, int status) {
		return _collectionPersistenceFinderByLtD_S.filterCount(
			finderCache, new Object[] {displayDate, status});
	}

	private FilterCollectionPersistenceFinder<COREntry, NoSuchCOREntryException>
		_collectionPersistenceFinderByLtE_S;

	/**
	 * Returns all the cor entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the matching cor entries
	 */
	@Override
	public List<COREntry> findByLtE_S(Date expirationDate, int status) {
		return findByLtE_S(
			expirationDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cor entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @return the range of matching cor entries
	 */
	@Override
	public List<COREntry> findByLtE_S(
		Date expirationDate, int status, int start, int end) {

		return findByLtE_S(expirationDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cor entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cor entries
	 */
	@Override
	public List<COREntry> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<COREntry> orderByComparator) {

		return findByLtE_S(
			expirationDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cor entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cor entries
	 */
	@Override
	public List<COREntry> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<COREntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByLtE_S.find(
			finderCache, new Object[] {expirationDate, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cor entry in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cor entry
	 * @throws NoSuchCOREntryException if a matching cor entry could not be found
	 */
	@Override
	public COREntry findByLtE_S_First(
			Date expirationDate, int status,
			OrderByComparator<COREntry> orderByComparator)
		throws NoSuchCOREntryException {

		return _collectionPersistenceFinderByLtE_S.findFirst(
			finderCache, new Object[] {expirationDate, status},
			orderByComparator);
	}

	/**
	 * Returns the first cor entry in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cor entry, or <code>null</code> if a matching cor entry could not be found
	 */
	@Override
	public COREntry fetchByLtE_S_First(
		Date expirationDate, int status,
		OrderByComparator<COREntry> orderByComparator) {

		return _collectionPersistenceFinderByLtE_S.fetchFirst(
			finderCache, new Object[] {expirationDate, status},
			orderByComparator);
	}

	/**
	 * Returns all the cor entries that the user has permission to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the matching cor entries that the user has permission to view
	 */
	@Override
	public List<COREntry> filterFindByLtE_S(Date expirationDate, int status) {
		return filterFindByLtE_S(
			expirationDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cor entries that the user has permission to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @return the range of matching cor entries that the user has permission to view
	 */
	@Override
	public List<COREntry> filterFindByLtE_S(
		Date expirationDate, int status, int start, int end) {

		return filterFindByLtE_S(expirationDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cor entries that the user has permissions to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cor entries that the user has permission to view
	 */
	@Override
	public List<COREntry> filterFindByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<COREntry> orderByComparator) {

		return _collectionPersistenceFinderByLtE_S.filterFind(
			finderCache, new Object[] {expirationDate, status}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the cor entries where expirationDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 */
	@Override
	public void removeByLtE_S(Date expirationDate, int status) {
		_collectionPersistenceFinderByLtE_S.remove(
			finderCache, new Object[] {expirationDate, status});
	}

	/**
	 * Returns the number of cor entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the number of matching cor entries
	 */
	@Override
	public int countByLtE_S(Date expirationDate, int status) {
		return _collectionPersistenceFinderByLtE_S.count(
			finderCache, new Object[] {expirationDate, status});
	}

	/**
	 * Returns the number of cor entries that the user has permission to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the number of matching cor entries that the user has permission to view
	 */
	@Override
	public int filterCountByLtE_S(Date expirationDate, int status) {
		return _collectionPersistenceFinderByLtE_S.filterCount(
			finderCache, new Object[] {expirationDate, status});
	}

	private FilterCollectionPersistenceFinder<COREntry, NoSuchCOREntryException>
		_collectionPersistenceFinderByC_A_LikeType;

	/**
	 * Returns all the cor entries where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @return the matching cor entries
	 */
	@Override
	public List<COREntry> findByC_A_LikeType(
		long companyId, boolean active, String type) {

		return findByC_A_LikeType(
			companyId, active, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the cor entries where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @return the range of matching cor entries
	 */
	@Override
	public List<COREntry> findByC_A_LikeType(
		long companyId, boolean active, String type, int start, int end) {

		return findByC_A_LikeType(companyId, active, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cor entries where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cor entries
	 */
	@Override
	public List<COREntry> findByC_A_LikeType(
		long companyId, boolean active, String type, int start, int end,
		OrderByComparator<COREntry> orderByComparator) {

		return findByC_A_LikeType(
			companyId, active, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cor entries where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cor entries
	 */
	@Override
	public List<COREntry> findByC_A_LikeType(
		long companyId, boolean active, String type, int start, int end,
		OrderByComparator<COREntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A_LikeType.find(
			finderCache, new Object[] {companyId, active, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cor entry in the ordered set where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cor entry
	 * @throws NoSuchCOREntryException if a matching cor entry could not be found
	 */
	@Override
	public COREntry findByC_A_LikeType_First(
			long companyId, boolean active, String type,
			OrderByComparator<COREntry> orderByComparator)
		throws NoSuchCOREntryException {

		return _collectionPersistenceFinderByC_A_LikeType.findFirst(
			finderCache, new Object[] {companyId, active, type},
			orderByComparator);
	}

	/**
	 * Returns the first cor entry in the ordered set where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cor entry, or <code>null</code> if a matching cor entry could not be found
	 */
	@Override
	public COREntry fetchByC_A_LikeType_First(
		long companyId, boolean active, String type,
		OrderByComparator<COREntry> orderByComparator) {

		return _collectionPersistenceFinderByC_A_LikeType.fetchFirst(
			finderCache, new Object[] {companyId, active, type},
			orderByComparator);
	}

	/**
	 * Returns all the cor entries that the user has permission to view where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @return the matching cor entries that the user has permission to view
	 */
	@Override
	public List<COREntry> filterFindByC_A_LikeType(
		long companyId, boolean active, String type) {

		return filterFindByC_A_LikeType(
			companyId, active, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the cor entries that the user has permission to view where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @return the range of matching cor entries that the user has permission to view
	 */
	@Override
	public List<COREntry> filterFindByC_A_LikeType(
		long companyId, boolean active, String type, int start, int end) {

		return filterFindByC_A_LikeType(
			companyId, active, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cor entries that the user has permissions to view where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @param start the lower bound of the range of cor entries
	 * @param end the upper bound of the range of cor entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cor entries that the user has permission to view
	 */
	@Override
	public List<COREntry> filterFindByC_A_LikeType(
		long companyId, boolean active, String type, int start, int end,
		OrderByComparator<COREntry> orderByComparator) {

		return _collectionPersistenceFinderByC_A_LikeType.filterFind(
			finderCache, new Object[] {companyId, active, type}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the cor entries where companyId = &#63; and active = &#63; and type LIKE &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 */
	@Override
	public void removeByC_A_LikeType(
		long companyId, boolean active, String type) {

		_collectionPersistenceFinderByC_A_LikeType.remove(
			finderCache, new Object[] {companyId, active, type});
	}

	/**
	 * Returns the number of cor entries where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @return the number of matching cor entries
	 */
	@Override
	public int countByC_A_LikeType(
		long companyId, boolean active, String type) {

		return _collectionPersistenceFinderByC_A_LikeType.count(
			finderCache, new Object[] {companyId, active, type});
	}

	/**
	 * Returns the number of cor entries that the user has permission to view where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @return the number of matching cor entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_A_LikeType(
		long companyId, boolean active, String type) {

		return _collectionPersistenceFinderByC_A_LikeType.filterCount(
			finderCache, new Object[] {companyId, active, type}, companyId, 0);
	}

	private UniquePersistenceFinder<COREntry, NoSuchCOREntryException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the cor entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCOREntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cor entry
	 * @throws NoSuchCOREntryException if a matching cor entry could not be found
	 */
	@Override
	public COREntry findByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchCOREntryException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the cor entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cor entry, or <code>null</code> if a matching cor entry could not be found
	 */
	@Override
	public COREntry fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the cor entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cor entry that was removed
	 */
	@Override
	public COREntry removeByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchCOREntryException {

		COREntry corEntry = findByERC_C(externalReferenceCode, companyId);

		return remove(corEntry);
	}

	/**
	 * Returns the number of cor entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cor entries
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public COREntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("active", "active_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(COREntry.class);

		setModelImplClass(COREntryImpl.class);
		setModelPKClass(long.class);

		setTable(COREntryTable.INSTANCE);
	}

	/**
	 * Creates a new cor entry with the primary key. Does not add the cor entry to the database.
	 *
	 * @param COREntryId the primary key for the new cor entry
	 * @return the new cor entry
	 */
	@Override
	public COREntry create(long COREntryId) {
		COREntry corEntry = new COREntryImpl();

		corEntry.setNew(true);
		corEntry.setPrimaryKey(COREntryId);

		String uuid = PortalUUIDUtil.generate();

		corEntry.setUuid(uuid);

		corEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return corEntry;
	}

	/**
	 * Removes the cor entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param COREntryId the primary key of the cor entry
	 * @return the cor entry that was removed
	 * @throws NoSuchCOREntryException if a cor entry with the primary key could not be found
	 */
	@Override
	public COREntry remove(long COREntryId) throws NoSuchCOREntryException {
		return remove((Serializable)COREntryId);
	}

	@Override
	protected COREntry removeImpl(COREntry corEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(corEntry)) {
				corEntry = (COREntry)session.get(
					COREntryImpl.class, corEntry.getPrimaryKeyObj());
			}

			if (corEntry != null) {
				session.delete(corEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (corEntry != null) {
			clearCache(corEntry);
		}

		return corEntry;
	}

	@Override
	public COREntry updateImpl(COREntry corEntry) {
		boolean isNew = corEntry.isNew();

		if (!(corEntry instanceof COREntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(corEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(corEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in corEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom COREntry implementation " +
					corEntry.getClass());
		}

		COREntryModelImpl corEntryModelImpl = (COREntryModelImpl)corEntry;

		if (Validator.isNull(corEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			corEntry.setUuid(uuid);
		}

		if (Validator.isNull(corEntry.getExternalReferenceCode())) {
			corEntry.setExternalReferenceCode(corEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					corEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					corEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = corEntry.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = corEntry.getPrimaryKey();
					}

					try {
						corEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								COREntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								corEntry.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			COREntry ercCOREntry = fetchByERC_C(
				corEntry.getExternalReferenceCode(), corEntry.getCompanyId());

			if (isNew) {
				if (ercCOREntry != null) {
					throw new DuplicateCOREntryExternalReferenceCodeException(
						"Duplicate cor entry with external reference code " +
							corEntry.getExternalReferenceCode() +
								" and company " + corEntry.getCompanyId());
				}
			}
			else {
				if ((ercCOREntry != null) &&
					(corEntry.getCOREntryId() != ercCOREntry.getCOREntryId())) {

					throw new DuplicateCOREntryExternalReferenceCodeException(
						"Duplicate cor entry with external reference code " +
							corEntry.getExternalReferenceCode() +
								" and company " + corEntry.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (corEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				corEntry.setCreateDate(date);
			}
			else {
				corEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!corEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				corEntry.setModifiedDate(date);
			}
			else {
				corEntry.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(corEntry);
			}
			else {
				corEntry = (COREntry)session.merge(corEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(corEntry, false);

		if (isNew) {
			corEntry.setNew(false);
		}

		corEntry.resetOriginalValues();

		return corEntry;
	}

	/**
	 * Returns the cor entry with the primary key or throws a <code>NoSuchCOREntryException</code> if it could not be found.
	 *
	 * @param COREntryId the primary key of the cor entry
	 * @return the cor entry
	 * @throws NoSuchCOREntryException if a cor entry with the primary key could not be found
	 */
	@Override
	public COREntry findByPrimaryKey(long COREntryId)
		throws NoSuchCOREntryException {

		return findByPrimaryKey((Serializable)COREntryId);
	}

	/**
	 * Returns the cor entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param COREntryId the primary key of the cor entry
	 * @return the cor entry, or <code>null</code> if a cor entry with the primary key could not be found
	 */
	@Override
	public COREntry fetchByPrimaryKey(long COREntryId) {
		return fetchByPrimaryKey((Serializable)COREntryId);
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
		return "COREntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CORENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return COREntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the cor entry persistence.
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
				_SQL_SELECT_CORENTRY_WHERE, _SQL_COUNT_CORENTRY_WHERE,
				COREntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"corEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, COREntry::getUuid));

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
				_SQL_SELECT_CORENTRY_WHERE, _SQL_COUNT_CORENTRY_WHERE,
				COREntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"corEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, COREntry::getUuid),
				new FinderColumn<>(
					"corEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, COREntry::getCompanyId));

		_collectionPersistenceFinderByC_A =
			new FilterCollectionPersistenceFinder<>(
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
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "active_"}, false),
				_SQL_SELECT_CORENTRY_WHERE, _SQL_COUNT_CORENTRY_WHERE,
				COREntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"corEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, COREntry::getCompanyId),
				new FinderColumn<>(
					"corEntry.", "active", "active_", FinderColumn.Type.BOOLEAN,
					"=", true, true, COREntry::isActive));

		_collectionPersistenceFinderByC_LikeType =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LikeType",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "type_"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LikeType",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "type_"}, false),
				_SQL_SELECT_CORENTRY_WHERE, _SQL_COUNT_CORENTRY_WHERE,
				COREntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"corEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, COREntry::getCompanyId),
				new FinderColumn<>(
					"corEntry.", "type", "type_", FinderColumn.Type.STRING,
					"LIKE", true, true, COREntry::getType));

		_collectionPersistenceFinderByLtD_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtD_S",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"displayDate", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtD_S",
					new String[] {
						Date.class.getName(), Integer.class.getName()
					},
					new String[] {"displayDate", "status"}, false),
				_SQL_SELECT_CORENTRY_WHERE, _SQL_COUNT_CORENTRY_WHERE,
				COREntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"corEntry.", "displayDate", FinderColumn.Type.DATE, "<",
					true, true, COREntry::getDisplayDate),
				new FinderColumn<>(
					"corEntry.", "status", FinderColumn.Type.INTEGER, "=", true,
					true, COREntry::getStatus));

		_collectionPersistenceFinderByLtE_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtE_S",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"expirationDate", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtE_S",
					new String[] {
						Date.class.getName(), Integer.class.getName()
					},
					new String[] {"expirationDate", "status"}, false),
				_SQL_SELECT_CORENTRY_WHERE, _SQL_COUNT_CORENTRY_WHERE,
				COREntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"corEntry.", "expirationDate", FinderColumn.Type.DATE, "<",
					true, true, COREntry::getExpirationDate),
				new FinderColumn<>(
					"corEntry.", "status", FinderColumn.Type.INTEGER, "=", true,
					true, COREntry::getStatus));

		_collectionPersistenceFinderByC_A_LikeType =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByC_A_LikeType",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "active_", "type_"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByC_A_LikeType",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {"companyId", "active_", "type_"}, false),
				_SQL_SELECT_CORENTRY_WHERE, _SQL_COUNT_CORENTRY_WHERE,
				COREntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"corEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, COREntry::getCompanyId),
				new FinderColumn<>(
					"corEntry.", "active", "active_", FinderColumn.Type.BOOLEAN,
					"=", true, true, COREntry::isActive),
				new FinderColumn<>(
					"corEntry.", "type", "type_", FinderColumn.Type.STRING,
					"LIKE", true, true, COREntry::getType));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false, convertNullFunction(COREntry::getExternalReferenceCode),
				COREntry::getCompanyId),
			_SQL_SELECT_CORENTRY_WHERE, "",
			new FinderColumn<>(
				"corEntry.", "externalReferenceCode", FinderColumn.Type.STRING,
				"=", true, true, COREntry::getExternalReferenceCode),
			new FinderColumn<>(
				"corEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, COREntry::getCompanyId));

		COREntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		COREntryUtil.setPersistence(null);

		entityCache.removeCache(COREntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = CORPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CORPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CORPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		COREntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CORENTRY =
		"SELECT corEntry FROM COREntry corEntry";

	private static final String _SQL_SELECT_CORENTRY_WHERE =
		"SELECT corEntry FROM COREntry corEntry WHERE ";

	private static final String _SQL_COUNT_CORENTRY_WHERE =
		"SELECT COUNT(corEntry) FROM COREntry corEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No COREntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		COREntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "active", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1775736397