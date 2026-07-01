/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.persistence.impl;

import com.liferay.oauth.client.persistence.exception.DuplicateOAuthClientEntryExternalReferenceCodeException;
import com.liferay.oauth.client.persistence.exception.NoSuchOAuthClientEntryException;
import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.model.OAuthClientEntryTable;
import com.liferay.oauth.client.persistence.model.impl.OAuthClientEntryImpl;
import com.liferay.oauth.client.persistence.model.impl.OAuthClientEntryModelImpl;
import com.liferay.oauth.client.persistence.service.persistence.OAuthClientEntryPersistence;
import com.liferay.oauth.client.persistence.service.persistence.OAuthClientEntryUtil;
import com.liferay.oauth.client.persistence.service.persistence.impl.constants.OAuthClientPersistenceConstants;
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
 * The persistence implementation for the o auth client entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = OAuthClientEntryPersistence.class)
public class OAuthClientEntryPersistenceImpl
	extends BasePersistenceImpl
		<OAuthClientEntry, NoSuchOAuthClientEntryException>
	implements OAuthClientEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>OAuthClientEntryUtil</code> to access the o auth client entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		OAuthClientEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<OAuthClientEntry, NoSuchOAuthClientEntryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the o auth client entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first o auth client entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry findByUuid_First(
			String uuid, OrderByComparator<OAuthClientEntry> orderByComparator)
		throws NoSuchOAuthClientEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first o auth client entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client entry, or <code>null</code> if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry fetchByUuid_First(
		String uuid, OrderByComparator<OAuthClientEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the o auth client entries that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client entries that the user has permission to view
	 */
	@Override
	public List<OAuthClientEntry> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			finderCache, new Object[] {uuid}, start, end, orderByComparator);
	}

	/**
	 * Removes all the o auth client entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of o auth client entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching o auth client entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of o auth client entries that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching o auth client entries that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			finderCache, new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder
		<OAuthClientEntry, NoSuchOAuthClientEntryException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the o auth client entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth client entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<OAuthClientEntry> orderByComparator)
		throws NoSuchOAuthClientEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first o auth client entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client entry, or <code>null</code> if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<OAuthClientEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the o auth client entries that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client entries that the user has permission to view
	 */
	@Override
	public List<OAuthClientEntry> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the o auth client entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of o auth client entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching o auth client entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of o auth client entries that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching o auth client entries that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			finderCache, new Object[] {uuid, companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<OAuthClientEntry, NoSuchOAuthClientEntryException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the o auth client entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth client entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry findByCompanyId_First(
			long companyId,
			OrderByComparator<OAuthClientEntry> orderByComparator)
		throws NoSuchOAuthClientEntryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first o auth client entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client entry, or <code>null</code> if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<OAuthClientEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the o auth client entries that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client entries that the user has permission to view
	 */
	@Override
	public List<OAuthClientEntry> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the o auth client entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of o auth client entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching o auth client entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of o auth client entries that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching o auth client entries that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<OAuthClientEntry, NoSuchOAuthClientEntryException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the o auth client entries where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findByUserId(
		long userId, int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			finderCache, new Object[] {userId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first o auth client entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry findByUserId_First(
			long userId, OrderByComparator<OAuthClientEntry> orderByComparator)
		throws NoSuchOAuthClientEntryException {

		return _collectionPersistenceFinderByUserId.findFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Returns the first o auth client entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client entry, or <code>null</code> if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry fetchByUserId_First(
		long userId, OrderByComparator<OAuthClientEntry> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the o auth client entries that the user has permissions to view where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client entries that the user has permission to view
	 */
	@Override
	public List<OAuthClientEntry> filterFindByUserId(
		long userId, int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator) {

		return _collectionPersistenceFinderByUserId.filterFind(
			finderCache, new Object[] {userId}, start, end, orderByComparator);
	}

	/**
	 * Removes all the o auth client entries where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the number of o auth client entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching o auth client entries
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the number of o auth client entries that the user has permission to view where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching o auth client entries that the user has permission to view
	 */
	@Override
	public int filterCountByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.filterCount(
			finderCache, new Object[] {userId});
	}

	private FilterCollectionPersistenceFinder
		<OAuthClientEntry, NoSuchOAuthClientEntryException>
			_collectionPersistenceFinderByC_A;

	/**
	 * Returns an ordered range of all the o auth client entries where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findByC_A(
		long companyId, String authServerWellKnownURI, int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A.find(
			finderCache, new Object[] {companyId, authServerWellKnownURI},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth client entry in the ordered set where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry findByC_A_First(
			long companyId, String authServerWellKnownURI,
			OrderByComparator<OAuthClientEntry> orderByComparator)
		throws NoSuchOAuthClientEntryException {

		return _collectionPersistenceFinderByC_A.findFirst(
			finderCache, new Object[] {companyId, authServerWellKnownURI},
			orderByComparator);
	}

	/**
	 * Returns the first o auth client entry in the ordered set where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client entry, or <code>null</code> if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry fetchByC_A_First(
		long companyId, String authServerWellKnownURI,
		OrderByComparator<OAuthClientEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_A.fetchFirst(
			finderCache, new Object[] {companyId, authServerWellKnownURI},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the o auth client entries that the user has permissions to view where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client entries that the user has permission to view
	 */
	@Override
	public List<OAuthClientEntry> filterFindByC_A(
		long companyId, String authServerWellKnownURI, int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_A.filterFind(
			finderCache, new Object[] {companyId, authServerWellKnownURI},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the o auth client entries where companyId = &#63; and authServerWellKnownURI = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 */
	@Override
	public void removeByC_A(long companyId, String authServerWellKnownURI) {
		_collectionPersistenceFinderByC_A.remove(
			finderCache, new Object[] {companyId, authServerWellKnownURI});
	}

	/**
	 * Returns the number of o auth client entries where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @return the number of matching o auth client entries
	 */
	@Override
	public int countByC_A(long companyId, String authServerWellKnownURI) {
		return _collectionPersistenceFinderByC_A.count(
			finderCache, new Object[] {companyId, authServerWellKnownURI});
	}

	/**
	 * Returns the number of o auth client entries that the user has permission to view where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @return the number of matching o auth client entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_A(long companyId, String authServerWellKnownURI) {
		return _collectionPersistenceFinderByC_A.filterCount(
			finderCache, new Object[] {companyId, authServerWellKnownURI},
			companyId, 0);
	}

	private UniquePersistenceFinder
		<OAuthClientEntry, NoSuchOAuthClientEntryException>
			_uniquePersistenceFinderByC_A_C;

	/**
	 * Returns the o auth client entry where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; or throws a <code>NoSuchOAuthClientEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the matching o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry findByC_A_C(
			long companyId, String authServerWellKnownURI, String clientId)
		throws NoSuchOAuthClientEntryException {

		return _uniquePersistenceFinderByC_A_C.find(
			finderCache,
			new Object[] {companyId, authServerWellKnownURI, clientId});
	}

	/**
	 * Returns the o auth client entry where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client entry, or <code>null</code> if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry fetchByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_A_C.fetch(
			finderCache,
			new Object[] {companyId, authServerWellKnownURI, clientId},
			useFinderCache);
	}

	/**
	 * Removes the o auth client entry where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the o auth client entry that was removed
	 */
	@Override
	public OAuthClientEntry removeByC_A_C(
			long companyId, String authServerWellKnownURI, String clientId)
		throws NoSuchOAuthClientEntryException {

		OAuthClientEntry oAuthClientEntry = findByC_A_C(
			companyId, authServerWellKnownURI, clientId);

		return remove(oAuthClientEntry);
	}

	/**
	 * Returns the number of o auth client entries where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the number of matching o auth client entries
	 */
	@Override
	public int countByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId) {

		return _uniquePersistenceFinderByC_A_C.count(
			finderCache,
			new Object[] {companyId, authServerWellKnownURI, clientId});
	}

	private UniquePersistenceFinder
		<OAuthClientEntry, NoSuchOAuthClientEntryException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the o auth client entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchOAuthClientEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOAuthClientEntryException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the o auth client entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client entry, or <code>null</code> if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the o auth client entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the o auth client entry that was removed
	 */
	@Override
	public OAuthClientEntry removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOAuthClientEntryException {

		OAuthClientEntry oAuthClientEntry = findByERC_C(
			externalReferenceCode, companyId);

		return remove(oAuthClientEntry);
	}

	/**
	 * Returns the number of o auth client entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching o auth client entries
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public OAuthClientEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(OAuthClientEntry.class);

		setModelImplClass(OAuthClientEntryImpl.class);
		setModelPKClass(long.class);

		setTable(OAuthClientEntryTable.INSTANCE);
	}

	/**
	 * Creates a new o auth client entry with the primary key. Does not add the o auth client entry to the database.
	 *
	 * @param oAuthClientEntryId the primary key for the new o auth client entry
	 * @return the new o auth client entry
	 */
	@Override
	public OAuthClientEntry create(long oAuthClientEntryId) {
		OAuthClientEntry oAuthClientEntry = new OAuthClientEntryImpl();

		oAuthClientEntry.setNew(true);
		oAuthClientEntry.setPrimaryKey(oAuthClientEntryId);

		String uuid = PortalUUIDUtil.generate();

		oAuthClientEntry.setUuid(uuid);

		oAuthClientEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return oAuthClientEntry;
	}

	/**
	 * Removes the o auth client entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param oAuthClientEntryId the primary key of the o auth client entry
	 * @return the o auth client entry that was removed
	 * @throws NoSuchOAuthClientEntryException if a o auth client entry with the primary key could not be found
	 */
	@Override
	public OAuthClientEntry remove(long oAuthClientEntryId)
		throws NoSuchOAuthClientEntryException {

		return remove((Serializable)oAuthClientEntryId);
	}

	@Override
	protected OAuthClientEntry removeImpl(OAuthClientEntry oAuthClientEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(oAuthClientEntry)) {
				oAuthClientEntry = (OAuthClientEntry)session.get(
					OAuthClientEntryImpl.class,
					oAuthClientEntry.getPrimaryKeyObj());
			}

			if (oAuthClientEntry != null) {
				session.delete(oAuthClientEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (oAuthClientEntry != null) {
			clearCache(oAuthClientEntry);
		}

		return oAuthClientEntry;
	}

	@Override
	public OAuthClientEntry updateImpl(OAuthClientEntry oAuthClientEntry) {
		boolean isNew = oAuthClientEntry.isNew();

		if (!(oAuthClientEntry instanceof OAuthClientEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(oAuthClientEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					oAuthClientEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in oAuthClientEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom OAuthClientEntry implementation " +
					oAuthClientEntry.getClass());
		}

		OAuthClientEntryModelImpl oAuthClientEntryModelImpl =
			(OAuthClientEntryModelImpl)oAuthClientEntry;

		if (Validator.isNull(oAuthClientEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			oAuthClientEntry.setUuid(uuid);
		}

		if (Validator.isNull(oAuthClientEntry.getExternalReferenceCode())) {
			oAuthClientEntry.setExternalReferenceCode(
				oAuthClientEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					oAuthClientEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					oAuthClientEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = oAuthClientEntry.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = oAuthClientEntry.getPrimaryKey();
					}

					try {
						oAuthClientEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								OAuthClientEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								oAuthClientEntry.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			OAuthClientEntry ercOAuthClientEntry = fetchByERC_C(
				oAuthClientEntry.getExternalReferenceCode(),
				oAuthClientEntry.getCompanyId());

			if (isNew) {
				if (ercOAuthClientEntry != null) {
					throw new DuplicateOAuthClientEntryExternalReferenceCodeException(
						"Duplicate o auth client entry with external reference code " +
							oAuthClientEntry.getExternalReferenceCode() +
								" and company " +
									oAuthClientEntry.getCompanyId());
				}
			}
			else {
				if ((ercOAuthClientEntry != null) &&
					(oAuthClientEntry.getOAuthClientEntryId() !=
						ercOAuthClientEntry.getOAuthClientEntryId())) {

					throw new DuplicateOAuthClientEntryExternalReferenceCodeException(
						"Duplicate o auth client entry with external reference code " +
							oAuthClientEntry.getExternalReferenceCode() +
								" and company " +
									oAuthClientEntry.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (oAuthClientEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				oAuthClientEntry.setCreateDate(date);
			}
			else {
				oAuthClientEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!oAuthClientEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				oAuthClientEntry.setModifiedDate(date);
			}
			else {
				oAuthClientEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(oAuthClientEntry);
			}
			else {
				oAuthClientEntry = (OAuthClientEntry)session.merge(
					oAuthClientEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(oAuthClientEntry, false);

		if (isNew) {
			oAuthClientEntry.setNew(false);
		}

		oAuthClientEntry.resetOriginalValues();

		return oAuthClientEntry;
	}

	/**
	 * Returns the o auth client entry with the primary key or throws a <code>NoSuchOAuthClientEntryException</code> if it could not be found.
	 *
	 * @param oAuthClientEntryId the primary key of the o auth client entry
	 * @return the o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a o auth client entry with the primary key could not be found
	 */
	@Override
	public OAuthClientEntry findByPrimaryKey(long oAuthClientEntryId)
		throws NoSuchOAuthClientEntryException {

		return findByPrimaryKey((Serializable)oAuthClientEntryId);
	}

	/**
	 * Returns the o auth client entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param oAuthClientEntryId the primary key of the o auth client entry
	 * @return the o auth client entry, or <code>null</code> if a o auth client entry with the primary key could not be found
	 */
	@Override
	public OAuthClientEntry fetchByPrimaryKey(long oAuthClientEntryId) {
		return fetchByPrimaryKey((Serializable)oAuthClientEntryId);
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
		return "oAuthClientEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OAUTHCLIENTENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return OAuthClientEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the o auth client entry persistence.
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
				_SQL_SELECT_OAUTHCLIENTENTRY_WHERE,
				_SQL_COUNT_OAUTHCLIENTENTRY_WHERE,
				OAuthClientEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"oAuthClientEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					OAuthClientEntry::getUuid));

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
				_SQL_SELECT_OAUTHCLIENTENTRY_WHERE,
				_SQL_COUNT_OAUTHCLIENTENTRY_WHERE,
				OAuthClientEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"oAuthClientEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					OAuthClientEntry::getUuid),
				new FinderColumn<>(
					"oAuthClientEntry.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, OAuthClientEntry::getCompanyId));

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
				_SQL_SELECT_OAUTHCLIENTENTRY_WHERE,
				_SQL_COUNT_OAUTHCLIENTENTRY_WHERE,
				OAuthClientEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"oAuthClientEntry.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, OAuthClientEntry::getCompanyId));

		_collectionPersistenceFinderByUserId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, false),
				_SQL_SELECT_OAUTHCLIENTENTRY_WHERE,
				_SQL_COUNT_OAUTHCLIENTENTRY_WHERE,
				OAuthClientEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"oAuthClientEntry.", "userId", FinderColumn.Type.LONG, "=",
					true, true, OAuthClientEntry::getUserId));

		_collectionPersistenceFinderByC_A =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "authServerWellKnownURI"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "authServerWellKnownURI"}, 0, 2,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "authServerWellKnownURI"}, 0, 2,
					false, null),
				_SQL_SELECT_OAUTHCLIENTENTRY_WHERE,
				_SQL_COUNT_OAUTHCLIENTENTRY_WHERE,
				OAuthClientEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"oAuthClientEntry.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, OAuthClientEntry::getCompanyId),
				new FinderColumn<>(
					"oAuthClientEntry.", "authServerWellKnownURI",
					FinderColumn.Type.STRING, "=", true, true,
					OAuthClientEntry::getAuthServerWellKnownURI));

		_uniquePersistenceFinderByC_A_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_A_C",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {
					"companyId", "authServerWellKnownURI", "clientId"
				},
				0, 6, false, OAuthClientEntry::getCompanyId,
				convertNullFunction(
					OAuthClientEntry::getAuthServerWellKnownURI),
				convertNullFunction(OAuthClientEntry::getClientId)),
			_SQL_SELECT_OAUTHCLIENTENTRY_WHERE, "",
			new FinderColumn<>(
				"oAuthClientEntry.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, OAuthClientEntry::getCompanyId),
			new FinderColumn<>(
				"oAuthClientEntry.", "authServerWellKnownURI",
				FinderColumn.Type.STRING, "=", true, true,
				OAuthClientEntry::getAuthServerWellKnownURI),
			new FinderColumn<>(
				"oAuthClientEntry.", "clientId", FinderColumn.Type.STRING, "=",
				true, true, OAuthClientEntry::getClientId));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(OAuthClientEntry::getExternalReferenceCode),
				OAuthClientEntry::getCompanyId),
			_SQL_SELECT_OAUTHCLIENTENTRY_WHERE, "",
			new FinderColumn<>(
				"oAuthClientEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				OAuthClientEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"oAuthClientEntry.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, OAuthClientEntry::getCompanyId));

		OAuthClientEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		OAuthClientEntryUtil.setPersistence(null);

		entityCache.removeCache(OAuthClientEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = OAuthClientPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OAuthClientPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OAuthClientPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		OAuthClientEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OAUTHCLIENTENTRY =
		"SELECT oAuthClientEntry FROM OAuthClientEntry oAuthClientEntry";

	private static final String _SQL_SELECT_OAUTHCLIENTENTRY_WHERE =
		"SELECT oAuthClientEntry FROM OAuthClientEntry oAuthClientEntry WHERE ";

	private static final String _SQL_COUNT_OAUTHCLIENTENTRY_WHERE =
		"SELECT COUNT(oAuthClientEntry) FROM OAuthClientEntry oAuthClientEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No OAuthClientEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		OAuthClientEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1523365615