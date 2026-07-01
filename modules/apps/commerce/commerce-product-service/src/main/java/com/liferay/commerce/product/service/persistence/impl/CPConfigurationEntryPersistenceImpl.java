/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.DuplicateCPConfigurationEntryExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPConfigurationEntryException;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationEntryTable;
import com.liferay.commerce.product.model.impl.CPConfigurationEntryImpl;
import com.liferay.commerce.product.model.impl.CPConfigurationEntryModelImpl;
import com.liferay.commerce.product.service.persistence.CPConfigurationEntryPersistence;
import com.liferay.commerce.product.service.persistence.CPConfigurationEntryUtil;
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
 * The persistence implementation for the cp configuration entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPConfigurationEntryPersistence.class)
public class CPConfigurationEntryPersistenceImpl
	extends BasePersistenceImpl
		<CPConfigurationEntry, NoSuchCPConfigurationEntryException>
	implements CPConfigurationEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPConfigurationEntryUtil</code> to access the cp configuration entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPConfigurationEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CPConfigurationEntry, NoSuchCPConfigurationEntryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cp configuration entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByUuid_First(
			String uuid,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByUuid_First(
		String uuid,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the cp configuration entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cp configuration entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp configuration entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CPConfigurationEntry, NoSuchCPConfigurationEntryException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the cp configuration entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPConfigurationEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchCPConfigurationEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the cp configuration entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the cp configuration entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp configuration entry that was removed
	 */
	@Override
	public CPConfigurationEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = findByUUID_G(uuid, groupId);

		return remove(cpConfigurationEntry);
	}

	/**
	 * Returns the number of cp configuration entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp configuration entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CPConfigurationEntry, NoSuchCPConfigurationEntryException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cp configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp configuration entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of cp configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp configuration entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CPConfigurationEntry, NoSuchCPConfigurationEntryException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the cp configuration entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByCompanyId_First(
			long companyId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp configuration entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of cp configuration entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp configuration entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<CPConfigurationEntry, NoSuchCPConfigurationEntryException>
			_collectionPersistenceFinderByCPConfigurationListId;

	/**
	 * Returns an ordered range of all the cp configuration entries where CPConfigurationListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByCPConfigurationListId(
		long CPConfigurationListId, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPConfigurationListId.find(
			finderCache, new Object[] {CPConfigurationListId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByCPConfigurationListId_First(
			long CPConfigurationListId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		return _collectionPersistenceFinderByCPConfigurationListId.findFirst(
			finderCache, new Object[] {CPConfigurationListId},
			orderByComparator);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByCPConfigurationListId_First(
		long CPConfigurationListId,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return _collectionPersistenceFinderByCPConfigurationListId.fetchFirst(
			finderCache, new Object[] {CPConfigurationListId},
			orderByComparator);
	}

	/**
	 * Removes all the cp configuration entries where CPConfigurationListId = &#63; from the database.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 */
	@Override
	public void removeByCPConfigurationListId(long CPConfigurationListId) {
		_collectionPersistenceFinderByCPConfigurationListId.remove(
			finderCache, new Object[] {CPConfigurationListId});
	}

	/**
	 * Returns the number of cp configuration entries where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the number of matching cp configuration entries
	 */
	@Override
	public int countByCPConfigurationListId(long CPConfigurationListId) {
		return _collectionPersistenceFinderByCPConfigurationListId.count(
			finderCache, new Object[] {CPConfigurationListId});
	}

	private CollectionPersistenceFinder
		<CPConfigurationEntry, NoSuchCPConfigurationEntryException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the cp configuration entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the cp configuration entries where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C(long classNameId, long classPK) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the number of cp configuration entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching cp configuration entries
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private UniquePersistenceFinder
		<CPConfigurationEntry, NoSuchCPConfigurationEntryException>
			_uniquePersistenceFinderByC_C_C;

	/**
	 * Returns the cp configuration entry where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63; or throws a <code>NoSuchCPConfigurationEntryException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByC_C_C(
			long classNameId, long classPK, long CPConfigurationListId)
		throws NoSuchCPConfigurationEntryException {

		return _uniquePersistenceFinderByC_C_C.find(
			finderCache,
			new Object[] {classNameId, classPK, CPConfigurationListId});
	}

	/**
	 * Returns the cp configuration entry where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByC_C_C(
		long classNameId, long classPK, long CPConfigurationListId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_C.fetch(
			finderCache,
			new Object[] {classNameId, classPK, CPConfigurationListId},
			useFinderCache);
	}

	/**
	 * Removes the cp configuration entry where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the cp configuration entry that was removed
	 */
	@Override
	public CPConfigurationEntry removeByC_C_C(
			long classNameId, long classPK, long CPConfigurationListId)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = findByC_C_C(
			classNameId, classPK, CPConfigurationListId);

		return remove(cpConfigurationEntry);
	}

	/**
	 * Returns the number of cp configuration entries where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the number of matching cp configuration entries
	 */
	@Override
	public int countByC_C_C(
		long classNameId, long classPK, long CPConfigurationListId) {

		return _uniquePersistenceFinderByC_C_C.count(
			finderCache,
			new Object[] {classNameId, classPK, CPConfigurationListId});
	}

	private UniquePersistenceFinder
		<CPConfigurationEntry, NoSuchCPConfigurationEntryException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the cp configuration entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPConfigurationEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPConfigurationEntryException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the cp configuration entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the cp configuration entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp configuration entry that was removed
	 */
	@Override
	public CPConfigurationEntry removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = findByERC_C(
			externalReferenceCode, companyId);

		return remove(cpConfigurationEntry);
	}

	/**
	 * Returns the number of cp configuration entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp configuration entries
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CPConfigurationEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPConfigurationEntry.class);

		setModelImplClass(CPConfigurationEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CPConfigurationEntryTable.INSTANCE);
	}

	/**
	 * Creates a new cp configuration entry with the primary key. Does not add the cp configuration entry to the database.
	 *
	 * @param CPConfigurationEntryId the primary key for the new cp configuration entry
	 * @return the new cp configuration entry
	 */
	@Override
	public CPConfigurationEntry create(long CPConfigurationEntryId) {
		CPConfigurationEntry cpConfigurationEntry =
			new CPConfigurationEntryImpl();

		cpConfigurationEntry.setNew(true);
		cpConfigurationEntry.setPrimaryKey(CPConfigurationEntryId);

		String uuid = PortalUUIDUtil.generate();

		cpConfigurationEntry.setUuid(uuid);

		cpConfigurationEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return cpConfigurationEntry;
	}

	/**
	 * Removes the cp configuration entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPConfigurationEntryId the primary key of the cp configuration entry
	 * @return the cp configuration entry that was removed
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	@Override
	public CPConfigurationEntry remove(long CPConfigurationEntryId)
		throws NoSuchCPConfigurationEntryException {

		return remove((Serializable)CPConfigurationEntryId);
	}

	@Override
	protected CPConfigurationEntry removeImpl(
		CPConfigurationEntry cpConfigurationEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpConfigurationEntry)) {
				cpConfigurationEntry = (CPConfigurationEntry)session.get(
					CPConfigurationEntryImpl.class,
					cpConfigurationEntry.getPrimaryKeyObj());
			}

			if ((cpConfigurationEntry != null) &&
				ctPersistenceHelper.isRemove(cpConfigurationEntry)) {

				session.delete(cpConfigurationEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpConfigurationEntry != null) {
			clearCache(cpConfigurationEntry);
		}

		return cpConfigurationEntry;
	}

	@Override
	public CPConfigurationEntry updateImpl(
		CPConfigurationEntry cpConfigurationEntry) {

		boolean isNew = cpConfigurationEntry.isNew();

		if (!(cpConfigurationEntry instanceof CPConfigurationEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpConfigurationEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpConfigurationEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpConfigurationEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPConfigurationEntry implementation " +
					cpConfigurationEntry.getClass());
		}

		CPConfigurationEntryModelImpl cpConfigurationEntryModelImpl =
			(CPConfigurationEntryModelImpl)cpConfigurationEntry;

		if (Validator.isNull(cpConfigurationEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpConfigurationEntry.setUuid(uuid);
		}

		if (Validator.isNull(cpConfigurationEntry.getExternalReferenceCode())) {
			cpConfigurationEntry.setExternalReferenceCode(
				cpConfigurationEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					cpConfigurationEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					cpConfigurationEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = cpConfigurationEntry.getCompanyId();

					long groupId = cpConfigurationEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = cpConfigurationEntry.getPrimaryKey();
					}

					try {
						cpConfigurationEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CPConfigurationEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								cpConfigurationEntry.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CPConfigurationEntry ercCPConfigurationEntry = fetchByERC_C(
				cpConfigurationEntry.getExternalReferenceCode(),
				cpConfigurationEntry.getCompanyId());

			if (isNew) {
				if (ercCPConfigurationEntry != null) {
					throw new DuplicateCPConfigurationEntryExternalReferenceCodeException(
						"Duplicate cp configuration entry with external reference code " +
							cpConfigurationEntry.getExternalReferenceCode() +
								" and company " +
									cpConfigurationEntry.getCompanyId());
				}
			}
			else {
				if ((ercCPConfigurationEntry != null) &&
					(cpConfigurationEntry.getCPConfigurationEntryId() !=
						ercCPConfigurationEntry.getCPConfigurationEntryId())) {

					throw new DuplicateCPConfigurationEntryExternalReferenceCodeException(
						"Duplicate cp configuration entry with external reference code " +
							cpConfigurationEntry.getExternalReferenceCode() +
								" and company " +
									cpConfigurationEntry.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpConfigurationEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpConfigurationEntry.setCreateDate(date);
			}
			else {
				cpConfigurationEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!cpConfigurationEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpConfigurationEntry.setModifiedDate(date);
			}
			else {
				cpConfigurationEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cpConfigurationEntry)) {
				if (!isNew) {
					session.evict(
						CPConfigurationEntryImpl.class,
						cpConfigurationEntry.getPrimaryKeyObj());
				}

				session.save(cpConfigurationEntry);
			}
			else {
				cpConfigurationEntry = (CPConfigurationEntry)session.merge(
					cpConfigurationEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cpConfigurationEntry, false);

		if (isNew) {
			cpConfigurationEntry.setNew(false);
		}

		cpConfigurationEntry.resetOriginalValues();

		return cpConfigurationEntry;
	}

	/**
	 * Returns the cp configuration entry with the primary key or throws a <code>NoSuchCPConfigurationEntryException</code> if it could not be found.
	 *
	 * @param CPConfigurationEntryId the primary key of the cp configuration entry
	 * @return the cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	@Override
	public CPConfigurationEntry findByPrimaryKey(long CPConfigurationEntryId)
		throws NoSuchCPConfigurationEntryException {

		return findByPrimaryKey((Serializable)CPConfigurationEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cp configuration entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPConfigurationEntryId the primary key of the cp configuration entry
	 * @return the cp configuration entry, or <code>null</code> if a cp configuration entry with the primary key could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByPrimaryKey(long CPConfigurationEntryId) {
		return fetchByPrimaryKey((Serializable)CPConfigurationEntryId);
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
		return "CPConfigurationEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPCONFIGURATIONENTRY;
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
		return CPConfigurationEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPConfigurationEntry";
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
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("CPConfigurationListId");
		ctMergeColumnNames.add("CPTaxCategoryId");
		ctMergeColumnNames.add("allowedOrderQuantities");
		ctMergeColumnNames.add("backOrders");
		ctMergeColumnNames.add("commerceAvailabilityEstimateId");
		ctMergeColumnNames.add("CPDefinitionInventoryEngine");
		ctMergeColumnNames.add("depth");
		ctMergeColumnNames.add("displayAvailability");
		ctMergeColumnNames.add("displayStockQuantity");
		ctMergeColumnNames.add("freeShipping");
		ctMergeColumnNames.add("height");
		ctMergeColumnNames.add("lowStockActivity");
		ctMergeColumnNames.add("maxOrderQuantity");
		ctMergeColumnNames.add("minOrderQuantity");
		ctMergeColumnNames.add("minStockQuantity");
		ctMergeColumnNames.add("multipleOrderQuantity");
		ctMergeColumnNames.add("purchasable");
		ctMergeColumnNames.add("shippable");
		ctMergeColumnNames.add("shippingExtraPrice");
		ctMergeColumnNames.add("shipSeparately");
		ctMergeColumnNames.add("taxExempt");
		ctMergeColumnNames.add("weight");
		ctMergeColumnNames.add("width");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CPConfigurationEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"classNameId", "classPK", "CPConfigurationListId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the cp configuration entry persistence.
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
			_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE,
			_SQL_COUNT_CPCONFIGURATIONENTRY_WHERE,
			CPConfigurationEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"cpConfigurationEntry.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPConfigurationEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CPConfigurationEntry::getUuid),
				CPConfigurationEntry::getGroupId),
			_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE, "",
			new FinderColumn<>(
				"cpConfigurationEntry.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPConfigurationEntry::getUuid),
			new FinderColumn<>(
				"cpConfigurationEntry.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, CPConfigurationEntry::getGroupId));

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
				_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE,
				_SQL_COUNT_CPCONFIGURATIONENTRY_WHERE,
				CPConfigurationEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpConfigurationEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CPConfigurationEntry::getUuid),
				new FinderColumn<>(
					"cpConfigurationEntry.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CPConfigurationEntry::getCompanyId));

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
				_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE,
				_SQL_COUNT_CPCONFIGURATIONENTRY_WHERE,
				CPConfigurationEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpConfigurationEntry.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CPConfigurationEntry::getCompanyId));

		_collectionPersistenceFinderByCPConfigurationListId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPConfigurationListId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPConfigurationListId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPConfigurationListId",
					new String[] {Long.class.getName()},
					new String[] {"CPConfigurationListId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPConfigurationListId",
					new String[] {Long.class.getName()},
					new String[] {"CPConfigurationListId"}, false),
				_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE,
				_SQL_COUNT_CPCONFIGURATIONENTRY_WHERE,
				CPConfigurationEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpConfigurationEntry.", "CPConfigurationListId",
					FinderColumn.Type.LONG, "=", true, true,
					CPConfigurationEntry::getCPConfigurationListId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, false),
			_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE,
			_SQL_COUNT_CPCONFIGURATIONENTRY_WHERE,
			CPConfigurationEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"cpConfigurationEntry.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, CPConfigurationEntry::getClassNameId),
			new FinderColumn<>(
				"cpConfigurationEntry.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, CPConfigurationEntry::getClassPK));

		_uniquePersistenceFinderByC_C_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"classNameId", "classPK", "CPConfigurationListId"
				},
				0, 0, false, CPConfigurationEntry::getClassNameId,
				CPConfigurationEntry::getClassPK,
				CPConfigurationEntry::getCPConfigurationListId),
			_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE, "",
			new FinderColumn<>(
				"cpConfigurationEntry.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, CPConfigurationEntry::getClassNameId),
			new FinderColumn<>(
				"cpConfigurationEntry.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, CPConfigurationEntry::getClassPK),
			new FinderColumn<>(
				"cpConfigurationEntry.", "CPConfigurationListId",
				FinderColumn.Type.LONG, "=", true, true,
				CPConfigurationEntry::getCPConfigurationListId));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					CPConfigurationEntry::getExternalReferenceCode),
				CPConfigurationEntry::getCompanyId),
			_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE, "",
			new FinderColumn<>(
				"cpConfigurationEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CPConfigurationEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"cpConfigurationEntry.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, CPConfigurationEntry::getCompanyId));

		CPConfigurationEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPConfigurationEntryUtil.setPersistence(null);

		entityCache.removeCache(CPConfigurationEntryImpl.class.getName());
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
		CPConfigurationEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPCONFIGURATIONENTRY =
		"SELECT cpConfigurationEntry FROM CPConfigurationEntry cpConfigurationEntry";

	private static final String _SQL_SELECT_CPCONFIGURATIONENTRY_WHERE =
		"SELECT cpConfigurationEntry FROM CPConfigurationEntry cpConfigurationEntry WHERE ";

	private static final String _SQL_COUNT_CPCONFIGURATIONENTRY_WHERE =
		"SELECT COUNT(cpConfigurationEntry) FROM CPConfigurationEntry cpConfigurationEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CPConfigurationEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CPConfigurationEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-191363127