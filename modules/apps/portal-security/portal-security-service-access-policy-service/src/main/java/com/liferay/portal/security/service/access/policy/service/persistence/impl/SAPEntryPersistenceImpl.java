/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.service.access.policy.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.security.service.access.policy.exception.NoSuchEntryException;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.model.SAPEntryTable;
import com.liferay.portal.security.service.access.policy.model.impl.SAPEntryImpl;
import com.liferay.portal.security.service.access.policy.model.impl.SAPEntryModelImpl;
import com.liferay.portal.security.service.access.policy.service.persistence.SAPEntryPersistence;
import com.liferay.portal.security.service.access.policy.service.persistence.SAPEntryUtil;
import com.liferay.portal.security.service.access.policy.service.persistence.impl.constants.SAPPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the sap entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = SAPEntryPersistence.class)
public class SAPEntryPersistenceImpl
	extends BasePersistenceImpl<SAPEntry, NoSuchEntryException>
	implements SAPEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SAPEntryUtil</code> to access the sap entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SAPEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder<SAPEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the sap entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SAPEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of sap entries
	 * @param end the upper bound of the range of sap entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching sap entries
	 */
	@Override
	public List<SAPEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SAPEntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first sap entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sap entry
	 * @throws NoSuchEntryException if a matching sap entry could not be found
	 */
	@Override
	public SAPEntry findByUuid_First(
			String uuid, OrderByComparator<SAPEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first sap entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sap entry, or <code>null</code> if a matching sap entry could not be found
	 */
	@Override
	public SAPEntry fetchByUuid_First(
		String uuid, OrderByComparator<SAPEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the sap entries that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SAPEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of sap entries
	 * @param end the upper bound of the range of sap entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching sap entries that the user has permission to view
	 */
	@Override
	public List<SAPEntry> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<SAPEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			finderCache, new Object[] {uuid}, start, end, orderByComparator);
	}

	/**
	 * Removes all the sap entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of sap entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching sap entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of sap entries that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching sap entries that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			finderCache, new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder<SAPEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the sap entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SAPEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of sap entries
	 * @param end the upper bound of the range of sap entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching sap entries
	 */
	@Override
	public List<SAPEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SAPEntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first sap entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sap entry
	 * @throws NoSuchEntryException if a matching sap entry could not be found
	 */
	@Override
	public SAPEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<SAPEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first sap entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sap entry, or <code>null</code> if a matching sap entry could not be found
	 */
	@Override
	public SAPEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<SAPEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the sap entries that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SAPEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of sap entries
	 * @param end the upper bound of the range of sap entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching sap entries that the user has permission to view
	 */
	@Override
	public List<SAPEntry> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SAPEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the sap entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of sap entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching sap entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of sap entries that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching sap entries that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			finderCache, new Object[] {uuid, companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder<SAPEntry, NoSuchEntryException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the sap entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SAPEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of sap entries
	 * @param end the upper bound of the range of sap entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching sap entries
	 */
	@Override
	public List<SAPEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<SAPEntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first sap entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sap entry
	 * @throws NoSuchEntryException if a matching sap entry could not be found
	 */
	@Override
	public SAPEntry findByCompanyId_First(
			long companyId, OrderByComparator<SAPEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first sap entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sap entry, or <code>null</code> if a matching sap entry could not be found
	 */
	@Override
	public SAPEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<SAPEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the sap entries that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SAPEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of sap entries
	 * @param end the upper bound of the range of sap entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching sap entries that the user has permission to view
	 */
	@Override
	public List<SAPEntry> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<SAPEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the sap entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of sap entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching sap entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of sap entries that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching sap entries that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder<SAPEntry, NoSuchEntryException>
		_collectionPersistenceFinderByC_D;

	/**
	 * Returns an ordered range of all the sap entries where companyId = &#63; and defaultSAPEntry = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SAPEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param defaultSAPEntry the default sap entry
	 * @param start the lower bound of the range of sap entries
	 * @param end the upper bound of the range of sap entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching sap entries
	 */
	@Override
	public List<SAPEntry> findByC_D(
		long companyId, boolean defaultSAPEntry, int start, int end,
		OrderByComparator<SAPEntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_D.find(
			finderCache, new Object[] {companyId, defaultSAPEntry}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first sap entry in the ordered set where companyId = &#63; and defaultSAPEntry = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultSAPEntry the default sap entry
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sap entry
	 * @throws NoSuchEntryException if a matching sap entry could not be found
	 */
	@Override
	public SAPEntry findByC_D_First(
			long companyId, boolean defaultSAPEntry,
			OrderByComparator<SAPEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_D.findFirst(
			finderCache, new Object[] {companyId, defaultSAPEntry},
			orderByComparator);
	}

	/**
	 * Returns the first sap entry in the ordered set where companyId = &#63; and defaultSAPEntry = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultSAPEntry the default sap entry
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sap entry, or <code>null</code> if a matching sap entry could not be found
	 */
	@Override
	public SAPEntry fetchByC_D_First(
		long companyId, boolean defaultSAPEntry,
		OrderByComparator<SAPEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_D.fetchFirst(
			finderCache, new Object[] {companyId, defaultSAPEntry},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the sap entries that the user has permissions to view where companyId = &#63; and defaultSAPEntry = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SAPEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param defaultSAPEntry the default sap entry
	 * @param start the lower bound of the range of sap entries
	 * @param end the upper bound of the range of sap entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching sap entries that the user has permission to view
	 */
	@Override
	public List<SAPEntry> filterFindByC_D(
		long companyId, boolean defaultSAPEntry, int start, int end,
		OrderByComparator<SAPEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_D.filterFind(
			finderCache, new Object[] {companyId, defaultSAPEntry}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the sap entries where companyId = &#63; and defaultSAPEntry = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param defaultSAPEntry the default sap entry
	 */
	@Override
	public void removeByC_D(long companyId, boolean defaultSAPEntry) {
		_collectionPersistenceFinderByC_D.remove(
			finderCache, new Object[] {companyId, defaultSAPEntry});
	}

	/**
	 * Returns the number of sap entries where companyId = &#63; and defaultSAPEntry = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultSAPEntry the default sap entry
	 * @return the number of matching sap entries
	 */
	@Override
	public int countByC_D(long companyId, boolean defaultSAPEntry) {
		return _collectionPersistenceFinderByC_D.count(
			finderCache, new Object[] {companyId, defaultSAPEntry});
	}

	/**
	 * Returns the number of sap entries that the user has permission to view where companyId = &#63; and defaultSAPEntry = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultSAPEntry the default sap entry
	 * @return the number of matching sap entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_D(long companyId, boolean defaultSAPEntry) {
		return _collectionPersistenceFinderByC_D.filterCount(
			finderCache, new Object[] {companyId, defaultSAPEntry}, companyId,
			0);
	}

	private UniquePersistenceFinder<SAPEntry, NoSuchEntryException>
		_uniquePersistenceFinderByC_N;

	/**
	 * Returns the sap entry where companyId = &#63; and name = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching sap entry
	 * @throws NoSuchEntryException if a matching sap entry could not be found
	 */
	@Override
	public SAPEntry findByC_N(long companyId, String name)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByC_N.find(
			finderCache, new Object[] {companyId, name});
	}

	/**
	 * Returns the sap entry where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching sap entry, or <code>null</code> if a matching sap entry could not be found
	 */
	@Override
	public SAPEntry fetchByC_N(
		long companyId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_N.fetch(
			finderCache, new Object[] {companyId, name}, useFinderCache);
	}

	/**
	 * Removes the sap entry where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the sap entry that was removed
	 */
	@Override
	public SAPEntry removeByC_N(long companyId, String name)
		throws NoSuchEntryException {

		SAPEntry sapEntry = findByC_N(companyId, name);

		return remove(sapEntry);
	}

	/**
	 * Returns the number of sap entries where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching sap entries
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		return _uniquePersistenceFinderByC_N.count(
			finderCache, new Object[] {companyId, name});
	}

	public SAPEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(SAPEntry.class);

		setModelImplClass(SAPEntryImpl.class);
		setModelPKClass(long.class);

		setTable(SAPEntryTable.INSTANCE);
	}

	/**
	 * Creates a new sap entry with the primary key. Does not add the sap entry to the database.
	 *
	 * @param sapEntryId the primary key for the new sap entry
	 * @return the new sap entry
	 */
	@Override
	public SAPEntry create(long sapEntryId) {
		SAPEntry sapEntry = new SAPEntryImpl();

		sapEntry.setNew(true);
		sapEntry.setPrimaryKey(sapEntryId);

		String uuid = PortalUUIDUtil.generate();

		sapEntry.setUuid(uuid);

		sapEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return sapEntry;
	}

	/**
	 * Removes the sap entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param sapEntryId the primary key of the sap entry
	 * @return the sap entry that was removed
	 * @throws NoSuchEntryException if a sap entry with the primary key could not be found
	 */
	@Override
	public SAPEntry remove(long sapEntryId) throws NoSuchEntryException {
		return remove((Serializable)sapEntryId);
	}

	@Override
	protected SAPEntry removeImpl(SAPEntry sapEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(sapEntry)) {
				sapEntry = (SAPEntry)session.get(
					SAPEntryImpl.class, sapEntry.getPrimaryKeyObj());
			}

			if (sapEntry != null) {
				session.delete(sapEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (sapEntry != null) {
			clearCache(sapEntry);
		}

		return sapEntry;
	}

	@Override
	public SAPEntry updateImpl(SAPEntry sapEntry) {
		boolean isNew = sapEntry.isNew();

		if (!(sapEntry instanceof SAPEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(sapEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(sapEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in sapEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SAPEntry implementation " +
					sapEntry.getClass());
		}

		SAPEntryModelImpl sapEntryModelImpl = (SAPEntryModelImpl)sapEntry;

		if (Validator.isNull(sapEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			sapEntry.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (sapEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				sapEntry.setCreateDate(date);
			}
			else {
				sapEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!sapEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				sapEntry.setModifiedDate(date);
			}
			else {
				sapEntry.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(sapEntry);
			}
			else {
				sapEntry = (SAPEntry)session.merge(sapEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(sapEntry, false);

		if (isNew) {
			sapEntry.setNew(false);
		}

		sapEntry.resetOriginalValues();

		return sapEntry;
	}

	/**
	 * Returns the sap entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param sapEntryId the primary key of the sap entry
	 * @return the sap entry
	 * @throws NoSuchEntryException if a sap entry with the primary key could not be found
	 */
	@Override
	public SAPEntry findByPrimaryKey(long sapEntryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)sapEntryId);
	}

	/**
	 * Returns the sap entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param sapEntryId the primary key of the sap entry
	 * @return the sap entry, or <code>null</code> if a sap entry with the primary key could not be found
	 */
	@Override
	public SAPEntry fetchByPrimaryKey(long sapEntryId) {
		return fetchByPrimaryKey((Serializable)sapEntryId);
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
		return "sapEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SAPENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SAPEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the sap entry persistence.
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
				_SQL_SELECT_SAPENTRY_WHERE, _SQL_COUNT_SAPENTRY_WHERE,
				SAPEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"sapEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, SAPEntry::getUuid));

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
				_SQL_SELECT_SAPENTRY_WHERE, _SQL_COUNT_SAPENTRY_WHERE,
				SAPEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"sapEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, SAPEntry::getUuid),
				new FinderColumn<>(
					"sapEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, SAPEntry::getCompanyId));

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
				_SQL_SELECT_SAPENTRY_WHERE, _SQL_COUNT_SAPENTRY_WHERE,
				SAPEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"sapEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, SAPEntry::getCompanyId));

		_collectionPersistenceFinderByC_D =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_D",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "defaultSAPEntry"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_D",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "defaultSAPEntry"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_D",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "defaultSAPEntry"}, false),
				_SQL_SELECT_SAPENTRY_WHERE, _SQL_COUNT_SAPENTRY_WHERE,
				SAPEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"sapEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, SAPEntry::getCompanyId),
				new FinderColumn<>(
					"sapEntry.", "defaultSAPEntry", FinderColumn.Type.BOOLEAN,
					"=", true, true, SAPEntry::isDefaultSAPEntry));

		_uniquePersistenceFinderByC_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "name"}, 2, 2, false,
				SAPEntry::getCompanyId, convertCaseFunction(SAPEntry::getName)),
			_SQL_SELECT_SAPENTRY_WHERE, "",
			new FinderColumn<>(
				"sapEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, SAPEntry::getCompanyId),
			new FinderColumn<>(
				"sapEntry.", "name", FinderColumn.Type.STRING, "=", false, true,
				SAPEntry::getName));

		SAPEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SAPEntryUtil.setPersistence(null);

		entityCache.removeCache(SAPEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = SAPPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SAPPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SAPPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		SAPEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SAPENTRY =
		"SELECT sapEntry FROM SAPEntry sapEntry";

	private static final String _SQL_SELECT_SAPENTRY_WHERE =
		"SELECT sapEntry FROM SAPEntry sapEntry WHERE ";

	private static final String _SQL_COUNT_SAPENTRY_WHERE =
		"SELECT COUNT(sapEntry) FROM SAPEntry sapEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SAPEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SAPEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1546414501