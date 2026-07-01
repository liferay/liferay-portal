/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.service.persistence.impl;

import com.liferay.dispatch.exception.DuplicateDispatchTriggerExternalReferenceCodeException;
import com.liferay.dispatch.exception.NoSuchTriggerException;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.model.DispatchTriggerTable;
import com.liferay.dispatch.model.impl.DispatchTriggerImpl;
import com.liferay.dispatch.model.impl.DispatchTriggerModelImpl;
import com.liferay.dispatch.service.persistence.DispatchTriggerPersistence;
import com.liferay.dispatch.service.persistence.DispatchTriggerUtil;
import com.liferay.dispatch.service.persistence.impl.constants.DispatchPersistenceConstants;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
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
 * The persistence implementation for the dispatch trigger service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matija Petanjek
 * @generated
 */
@Component(service = DispatchTriggerPersistence.class)
public class DispatchTriggerPersistenceImpl
	extends BasePersistenceImpl<DispatchTrigger, NoSuchTriggerException>
	implements DispatchTriggerPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DispatchTriggerUtil</code> to access the dispatch trigger persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DispatchTriggerImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<DispatchTrigger, NoSuchTriggerException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the dispatch triggers where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByUuid_First(
			String uuid, OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByUuid_First(
		String uuid, OrderByComparator<DispatchTrigger> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			finderCache, new Object[] {uuid}, start, end, orderByComparator);
	}

	/**
	 * Removes all the dispatch triggers where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of dispatch triggers where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of dispatch triggers that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			finderCache, new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder
		<DispatchTrigger, NoSuchTriggerException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the dispatch triggers where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the dispatch triggers where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of dispatch triggers where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of dispatch triggers that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			finderCache, new Object[] {uuid, companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<DispatchTrigger, NoSuchTriggerException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the dispatch triggers where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByCompanyId_First(
			long companyId,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByCompanyId_First(
		long companyId, OrderByComparator<DispatchTrigger> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the dispatch triggers where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of dispatch triggers where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of dispatch triggers that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<DispatchTrigger, NoSuchTriggerException>
			_collectionPersistenceFinderByActive;

	/**
	 * Returns an ordered range of all the dispatch triggers where active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByActive(
		boolean active, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByActive.find(
			finderCache, new Object[] {active}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where active = &#63;.
	 *
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByActive_First(
			boolean active,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		return _collectionPersistenceFinderByActive.findFirst(
			finderCache, new Object[] {active}, orderByComparator);
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where active = &#63;.
	 *
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByActive_First(
		boolean active, OrderByComparator<DispatchTrigger> orderByComparator) {

		return _collectionPersistenceFinderByActive.fetchFirst(
			finderCache, new Object[] {active}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers that the user has permissions to view where active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByActive(
		boolean active, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return _collectionPersistenceFinderByActive.filterFind(
			finderCache, new Object[] {active}, start, end, orderByComparator);
	}

	/**
	 * Removes all the dispatch triggers where active = &#63; from the database.
	 *
	 * @param active the active
	 */
	@Override
	public void removeByActive(boolean active) {
		_collectionPersistenceFinderByActive.remove(
			finderCache, new Object[] {active});
	}

	/**
	 * Returns the number of dispatch triggers where active = &#63;.
	 *
	 * @param active the active
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByActive(boolean active) {
		return _collectionPersistenceFinderByActive.count(
			finderCache, new Object[] {active});
	}

	/**
	 * Returns the number of dispatch triggers that the user has permission to view where active = &#63;.
	 *
	 * @param active the active
	 * @return the number of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public int filterCountByActive(boolean active) {
		return _collectionPersistenceFinderByActive.filterCount(
			finderCache, new Object[] {active});
	}

	private FilterCollectionPersistenceFinder
		<DispatchTrigger, NoSuchTriggerException>
			_collectionPersistenceFinderByC_U;

	/**
	 * Returns an ordered range of all the dispatch triggers where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_U.find(
			finderCache, new Object[] {companyId, userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByC_U_First(
			long companyId, long userId,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		return _collectionPersistenceFinderByC_U.findFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return _collectionPersistenceFinderByC_U.fetchFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers that the user has permissions to view where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return _collectionPersistenceFinderByC_U.filterFind(
			finderCache, new Object[] {companyId, userId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the dispatch triggers where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByC_U(long companyId, long userId) {
		_collectionPersistenceFinderByC_U.remove(
			finderCache, new Object[] {companyId, userId});
	}

	/**
	 * Returns the number of dispatch triggers where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByC_U(long companyId, long userId) {
		return _collectionPersistenceFinderByC_U.count(
			finderCache, new Object[] {companyId, userId});
	}

	/**
	 * Returns the number of dispatch triggers that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public int filterCountByC_U(long companyId, long userId) {
		return _collectionPersistenceFinderByC_U.filterCount(
			finderCache, new Object[] {companyId, userId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<DispatchTrigger, NoSuchTriggerException>
			_collectionPersistenceFinderByC_DTET;

	/**
	 * Returns an ordered range of all the dispatch triggers where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByC_DTET(
		long companyId, String dispatchTaskExecutorType, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_DTET.find(
			finderCache, new Object[] {companyId, dispatchTaskExecutorType},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByC_DTET_First(
			long companyId, String dispatchTaskExecutorType,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		return _collectionPersistenceFinderByC_DTET.findFirst(
			finderCache, new Object[] {companyId, dispatchTaskExecutorType},
			orderByComparator);
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByC_DTET_First(
		long companyId, String dispatchTaskExecutorType,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return _collectionPersistenceFinderByC_DTET.fetchFirst(
			finderCache, new Object[] {companyId, dispatchTaskExecutorType},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers that the user has permissions to view where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByC_DTET(
		long companyId, String dispatchTaskExecutorType, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return _collectionPersistenceFinderByC_DTET.filterFind(
			finderCache, new Object[] {companyId, dispatchTaskExecutorType},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the dispatch triggers where companyId = &#63; and dispatchTaskExecutorType = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 */
	@Override
	public void removeByC_DTET(
		long companyId, String dispatchTaskExecutorType) {

		_collectionPersistenceFinderByC_DTET.remove(
			finderCache, new Object[] {companyId, dispatchTaskExecutorType});
	}

	/**
	 * Returns the number of dispatch triggers where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByC_DTET(long companyId, String dispatchTaskExecutorType) {
		return _collectionPersistenceFinderByC_DTET.count(
			finderCache, new Object[] {companyId, dispatchTaskExecutorType});
	}

	/**
	 * Returns the number of dispatch triggers that the user has permission to view where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @return the number of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public int filterCountByC_DTET(
		long companyId, String dispatchTaskExecutorType) {

		return _collectionPersistenceFinderByC_DTET.filterCount(
			finderCache, new Object[] {companyId, dispatchTaskExecutorType},
			companyId, 0);
	}

	private UniquePersistenceFinder<DispatchTrigger, NoSuchTriggerException>
		_uniquePersistenceFinderByC_N;

	/**
	 * Returns the dispatch trigger where companyId = &#63; and name = &#63; or throws a <code>NoSuchTriggerException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByC_N(long companyId, String name)
		throws NoSuchTriggerException {

		return _uniquePersistenceFinderByC_N.find(
			finderCache, new Object[] {companyId, name});
	}

	/**
	 * Returns the dispatch trigger where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByC_N(
		long companyId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_N.fetch(
			finderCache, new Object[] {companyId, name}, useFinderCache);
	}

	/**
	 * Removes the dispatch trigger where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the dispatch trigger that was removed
	 */
	@Override
	public DispatchTrigger removeByC_N(long companyId, String name)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = findByC_N(companyId, name);

		return remove(dispatchTrigger);
	}

	/**
	 * Returns the number of dispatch triggers where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		return _uniquePersistenceFinderByC_N.count(
			finderCache, new Object[] {companyId, name});
	}

	private FilterCollectionPersistenceFinder
		<DispatchTrigger, NoSuchTriggerException>
			_collectionPersistenceFinderByA_DTCM;

	/**
	 * Returns an ordered range of all the dispatch triggers where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByA_DTCM(
		boolean active, int dispatchTaskClusterMode, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByA_DTCM.find(
			finderCache,
			new Object[] {active, new int[] {dispatchTaskClusterMode}}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByA_DTCM_First(
			boolean active, int dispatchTaskClusterMode,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = fetchByA_DTCM_First(
			active, dispatchTaskClusterMode, orderByComparator);

		if (dispatchTrigger != null) {
			return dispatchTrigger;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("active=");
		sb.append(active);

		sb.append(", dispatchTaskClusterMode=");
		sb.append(dispatchTaskClusterMode);

		sb.append("}");

		throw new NoSuchTriggerException(sb.toString());
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByA_DTCM_First(
		boolean active, int dispatchTaskClusterMode,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return _collectionPersistenceFinderByA_DTCM.fetchFirst(
			finderCache,
			new Object[] {active, new int[] {dispatchTaskClusterMode}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers that the user has permissions to view where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByA_DTCM(
		boolean active, int dispatchTaskClusterMode, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return _collectionPersistenceFinderByA_DTCM.filterFind(
			finderCache,
			new Object[] {active, new int[] {dispatchTaskClusterMode}}, start,
			end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers that the user has permission to view where active = &#63; and dispatchTaskClusterMode = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param dispatchTaskClusterModes the dispatch task cluster modes
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByA_DTCM(
		boolean active, int[] dispatchTaskClusterModes, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return _collectionPersistenceFinderByA_DTCM.filterFind(
			finderCache,
			new Object[] {
				active, ArrayUtil.sortedUnique(dispatchTaskClusterModes)
			},
			start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers where active = &#63; and dispatchTaskClusterMode = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param dispatchTaskClusterModes the dispatch task cluster modes
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByA_DTCM(
		boolean active, int[] dispatchTaskClusterModes, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByA_DTCM.find(
			finderCache,
			new Object[] {
				active, ArrayUtil.sortedUnique(dispatchTaskClusterModes)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the dispatch triggers where active = &#63; and dispatchTaskClusterMode = &#63; from the database.
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 */
	@Override
	public void removeByA_DTCM(boolean active, int dispatchTaskClusterMode) {
		_collectionPersistenceFinderByA_DTCM.remove(
			finderCache,
			new Object[] {active, new int[] {dispatchTaskClusterMode}});
	}

	/**
	 * Returns the number of dispatch triggers where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByA_DTCM(boolean active, int dispatchTaskClusterMode) {
		return _collectionPersistenceFinderByA_DTCM.count(
			finderCache,
			new Object[] {active, new int[] {dispatchTaskClusterMode}});
	}

	/**
	 * Returns the number of dispatch triggers where active = &#63; and dispatchTaskClusterMode = any &#63;.
	 *
	 * @param active the active
	 * @param dispatchTaskClusterModes the dispatch task cluster modes
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByA_DTCM(boolean active, int[] dispatchTaskClusterModes) {
		return _collectionPersistenceFinderByA_DTCM.count(
			finderCache,
			new Object[] {
				active, ArrayUtil.sortedUnique(dispatchTaskClusterModes)
			});
	}

	/**
	 * Returns the number of dispatch triggers that the user has permission to view where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @return the number of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public int filterCountByA_DTCM(
		boolean active, int dispatchTaskClusterMode) {

		return _collectionPersistenceFinderByA_DTCM.filterCount(
			finderCache,
			new Object[] {active, new int[] {dispatchTaskClusterMode}});
	}

	/**
	 * Returns the number of dispatch triggers that the user has permission to view where active = &#63; and dispatchTaskClusterMode = any &#63;.
	 *
	 * @param active the active
	 * @param dispatchTaskClusterModes the dispatch task cluster modes
	 * @return the number of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public int filterCountByA_DTCM(
		boolean active, int[] dispatchTaskClusterModes) {

		return _collectionPersistenceFinderByA_DTCM.filterCount(
			finderCache,
			new Object[] {
				active, ArrayUtil.sortedUnique(dispatchTaskClusterModes)
			});
	}

	private UniquePersistenceFinder<DispatchTrigger, NoSuchTriggerException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the dispatch trigger where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchTriggerException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchTriggerException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the dispatch trigger where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the dispatch trigger where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the dispatch trigger that was removed
	 */
	@Override
	public DispatchTrigger removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = findByERC_C(
			externalReferenceCode, companyId);

		return remove(dispatchTrigger);
	}

	/**
	 * Returns the number of dispatch triggers where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public DispatchTriggerPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("active", "active_");
		dbColumnNames.put("system", "system_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DispatchTrigger.class);

		setModelImplClass(DispatchTriggerImpl.class);
		setModelPKClass(long.class);

		setTable(DispatchTriggerTable.INSTANCE);
	}

	/**
	 * Creates a new dispatch trigger with the primary key. Does not add the dispatch trigger to the database.
	 *
	 * @param dispatchTriggerId the primary key for the new dispatch trigger
	 * @return the new dispatch trigger
	 */
	@Override
	public DispatchTrigger create(long dispatchTriggerId) {
		DispatchTrigger dispatchTrigger = new DispatchTriggerImpl();

		dispatchTrigger.setNew(true);
		dispatchTrigger.setPrimaryKey(dispatchTriggerId);

		String uuid = PortalUUIDUtil.generate();

		dispatchTrigger.setUuid(uuid);

		dispatchTrigger.setCompanyId(CompanyThreadLocal.getCompanyId());

		return dispatchTrigger;
	}

	/**
	 * Removes the dispatch trigger with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dispatchTriggerId the primary key of the dispatch trigger
	 * @return the dispatch trigger that was removed
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger remove(long dispatchTriggerId)
		throws NoSuchTriggerException {

		return remove((Serializable)dispatchTriggerId);
	}

	@Override
	protected DispatchTrigger removeImpl(DispatchTrigger dispatchTrigger) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dispatchTrigger)) {
				dispatchTrigger = (DispatchTrigger)session.get(
					DispatchTriggerImpl.class,
					dispatchTrigger.getPrimaryKeyObj());
			}

			if (dispatchTrigger != null) {
				session.delete(dispatchTrigger);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dispatchTrigger != null) {
			clearCache(dispatchTrigger);
		}

		return dispatchTrigger;
	}

	@Override
	public DispatchTrigger updateImpl(DispatchTrigger dispatchTrigger) {
		boolean isNew = dispatchTrigger.isNew();

		if (!(dispatchTrigger instanceof DispatchTriggerModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dispatchTrigger.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					dispatchTrigger);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dispatchTrigger proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DispatchTrigger implementation " +
					dispatchTrigger.getClass());
		}

		DispatchTriggerModelImpl dispatchTriggerModelImpl =
			(DispatchTriggerModelImpl)dispatchTrigger;

		if (Validator.isNull(dispatchTrigger.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			dispatchTrigger.setUuid(uuid);
		}

		if (Validator.isNull(dispatchTrigger.getExternalReferenceCode())) {
			dispatchTrigger.setExternalReferenceCode(dispatchTrigger.getUuid());
		}
		else {
			if (!Objects.equals(
					dispatchTriggerModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					dispatchTrigger.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = dispatchTrigger.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = dispatchTrigger.getPrimaryKey();
					}

					try {
						dispatchTrigger.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								DispatchTrigger.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								dispatchTrigger.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			DispatchTrigger ercDispatchTrigger = fetchByERC_C(
				dispatchTrigger.getExternalReferenceCode(),
				dispatchTrigger.getCompanyId());

			if (isNew) {
				if (ercDispatchTrigger != null) {
					throw new DuplicateDispatchTriggerExternalReferenceCodeException(
						"Duplicate dispatch trigger with external reference code " +
							dispatchTrigger.getExternalReferenceCode() +
								" and company " +
									dispatchTrigger.getCompanyId());
				}
			}
			else {
				if ((ercDispatchTrigger != null) &&
					(dispatchTrigger.getDispatchTriggerId() !=
						ercDispatchTrigger.getDispatchTriggerId())) {

					throw new DuplicateDispatchTriggerExternalReferenceCodeException(
						"Duplicate dispatch trigger with external reference code " +
							dispatchTrigger.getExternalReferenceCode() +
								" and company " +
									dispatchTrigger.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (dispatchTrigger.getCreateDate() == null)) {
			if (serviceContext == null) {
				dispatchTrigger.setCreateDate(date);
			}
			else {
				dispatchTrigger.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!dispatchTriggerModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				dispatchTrigger.setModifiedDate(date);
			}
			else {
				dispatchTrigger.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(dispatchTrigger);
			}
			else {
				dispatchTrigger = (DispatchTrigger)session.merge(
					dispatchTrigger);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(dispatchTrigger, false);

		if (isNew) {
			dispatchTrigger.setNew(false);
		}

		dispatchTrigger.resetOriginalValues();

		return dispatchTrigger;
	}

	/**
	 * Returns the dispatch trigger with the primary key or throws a <code>NoSuchTriggerException</code> if it could not be found.
	 *
	 * @param dispatchTriggerId the primary key of the dispatch trigger
	 * @return the dispatch trigger
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger findByPrimaryKey(long dispatchTriggerId)
		throws NoSuchTriggerException {

		return findByPrimaryKey((Serializable)dispatchTriggerId);
	}

	/**
	 * Returns the dispatch trigger with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dispatchTriggerId the primary key of the dispatch trigger
	 * @return the dispatch trigger, or <code>null</code> if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger fetchByPrimaryKey(long dispatchTriggerId) {
		return fetchByPrimaryKey((Serializable)dispatchTriggerId);
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
		return "dispatchTriggerId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DISPATCHTRIGGER;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return DispatchTriggerModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the dispatch trigger persistence.
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
				_SQL_SELECT_DISPATCHTRIGGER_WHERE,
				_SQL_COUNT_DISPATCHTRIGGER_WHERE,
				DispatchTriggerModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"dispatchTrigger.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					DispatchTrigger::getUuid));

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
				_SQL_SELECT_DISPATCHTRIGGER_WHERE,
				_SQL_COUNT_DISPATCHTRIGGER_WHERE,
				DispatchTriggerModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"dispatchTrigger.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					DispatchTrigger::getUuid),
				new FinderColumn<>(
					"dispatchTrigger.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, DispatchTrigger::getCompanyId));

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
				_SQL_SELECT_DISPATCHTRIGGER_WHERE,
				_SQL_COUNT_DISPATCHTRIGGER_WHERE,
				DispatchTriggerModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"dispatchTrigger.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, DispatchTrigger::getCompanyId));

		_collectionPersistenceFinderByActive =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByActive",
					new String[] {
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByActive",
					new String[] {Boolean.class.getName()},
					new String[] {"active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByActive",
					new String[] {Boolean.class.getName()},
					new String[] {"active_"}, false),
				_SQL_SELECT_DISPATCHTRIGGER_WHERE,
				_SQL_COUNT_DISPATCHTRIGGER_WHERE,
				DispatchTriggerModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"dispatchTrigger.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					DispatchTrigger::isActive));

		_collectionPersistenceFinderByC_U =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_U",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_U",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"companyId", "userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_U",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"companyId", "userId"}, false),
				_SQL_SELECT_DISPATCHTRIGGER_WHERE,
				_SQL_COUNT_DISPATCHTRIGGER_WHERE,
				DispatchTriggerModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"dispatchTrigger.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, DispatchTrigger::getCompanyId),
				new FinderColumn<>(
					"dispatchTrigger.", "userId", FinderColumn.Type.LONG, "=",
					true, true, DispatchTrigger::getUserId));

		_collectionPersistenceFinderByC_DTET =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_DTET",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "dispatchTaskExecutorType"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_DTET",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "dispatchTaskExecutorType"}, 0,
					2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_DTET",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "dispatchTaskExecutorType"}, 0,
					2, false, null),
				_SQL_SELECT_DISPATCHTRIGGER_WHERE,
				_SQL_COUNT_DISPATCHTRIGGER_WHERE,
				DispatchTriggerModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"dispatchTrigger.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, DispatchTrigger::getCompanyId),
				new FinderColumn<>(
					"dispatchTrigger.", "dispatchTaskExecutorType",
					FinderColumn.Type.STRING, "=", true, true,
					DispatchTrigger::getDispatchTaskExecutorType));

		_uniquePersistenceFinderByC_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "name"}, 0, 2, false,
				DispatchTrigger::getCompanyId,
				convertNullFunction(DispatchTrigger::getName)),
			_SQL_SELECT_DISPATCHTRIGGER_WHERE, "",
			new FinderColumn<>(
				"dispatchTrigger.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, DispatchTrigger::getCompanyId),
			new FinderColumn<>(
				"dispatchTrigger.", "name", FinderColumn.Type.STRING, "=", true,
				true, DispatchTrigger::getName));

		_collectionPersistenceFinderByA_DTCM =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByA_DTCM",
					new String[] {
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"active_", "dispatchTaskClusterMode"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByA_DTCM",
					new String[] {
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {"active_", "dispatchTaskClusterMode"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByA_DTCM",
					new String[] {
						Boolean.class.getName(), Integer.class.getName()
					},
					new String[] {"active_", "dispatchTaskClusterMode"}, false),
				_SQL_SELECT_DISPATCHTRIGGER_WHERE,
				_SQL_COUNT_DISPATCHTRIGGER_WHERE,
				DispatchTriggerModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"dispatchTrigger.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					DispatchTrigger::isActive),
				new ArrayableFinderColumn<>(
					"dispatchTrigger.", "dispatchTaskClusterMode",
					FinderColumn.Type.INTEGER, "=", false, true, true,
					DispatchTrigger::getDispatchTaskClusterMode));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(DispatchTrigger::getExternalReferenceCode),
				DispatchTrigger::getCompanyId),
			_SQL_SELECT_DISPATCHTRIGGER_WHERE, "",
			new FinderColumn<>(
				"dispatchTrigger.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				DispatchTrigger::getExternalReferenceCode),
			new FinderColumn<>(
				"dispatchTrigger.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, DispatchTrigger::getCompanyId));

		DispatchTriggerUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DispatchTriggerUtil.setPersistence(null);

		entityCache.removeCache(DispatchTriggerImpl.class.getName());
	}

	@Override
	@Reference(
		target = DispatchPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DispatchPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DispatchPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		DispatchTriggerModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DISPATCHTRIGGER =
		"SELECT dispatchTrigger FROM DispatchTrigger dispatchTrigger";

	private static final String _SQL_SELECT_DISPATCHTRIGGER_WHERE =
		"SELECT dispatchTrigger FROM DispatchTrigger dispatchTrigger WHERE ";

	private static final String _SQL_COUNT_DISPATCHTRIGGER_WHERE =
		"SELECT COUNT(dispatchTrigger) FROM DispatchTrigger dispatchTrigger WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DispatchTrigger exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DispatchTriggerPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "active", "system"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2140473569