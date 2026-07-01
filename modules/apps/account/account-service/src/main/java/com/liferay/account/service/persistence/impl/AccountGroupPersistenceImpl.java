/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.persistence.impl;

import com.liferay.account.exception.DuplicateAccountGroupExternalReferenceCodeException;
import com.liferay.account.exception.NoSuchGroupException;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.model.AccountGroupTable;
import com.liferay.account.model.impl.AccountGroupImpl;
import com.liferay.account.model.impl.AccountGroupModelImpl;
import com.liferay.account.service.persistence.AccountGroupPersistence;
import com.liferay.account.service.persistence.AccountGroupUtil;
import com.liferay.account.service.persistence.impl.constants.AccountPersistenceConstants;
import com.liferay.petra.string.StringBundler;
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
 * The persistence implementation for the account group service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AccountGroupPersistence.class)
public class AccountGroupPersistenceImpl
	extends BasePersistenceImpl<AccountGroup, NoSuchGroupException>
	implements AccountGroupPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AccountGroupUtil</code> to access the account group persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AccountGroupImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<AccountGroup, NoSuchGroupException> _collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the account groups where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account groups
	 */
	@Override
	public List<AccountGroup> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first account group in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group
	 * @throws NoSuchGroupException if a matching account group could not be found
	 */
	@Override
	public AccountGroup findByUuid_First(
			String uuid, OrderByComparator<AccountGroup> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first account group in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group, or <code>null</code> if a matching account group could not be found
	 */
	@Override
	public AccountGroup fetchByUuid_First(
		String uuid, OrderByComparator<AccountGroup> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account groups that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account groups that the user has permission to view
	 */
	@Override
	public List<AccountGroup> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			finderCache, new Object[] {uuid}, start, end, orderByComparator);
	}

	/**
	 * Removes all the account groups where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of account groups where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching account groups
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of account groups that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching account groups that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			finderCache, new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder
		<AccountGroup, NoSuchGroupException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the account groups where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account groups
	 */
	@Override
	public List<AccountGroup> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first account group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group
	 * @throws NoSuchGroupException if a matching account group could not be found
	 */
	@Override
	public AccountGroup findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<AccountGroup> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first account group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group, or <code>null</code> if a matching account group could not be found
	 */
	@Override
	public AccountGroup fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<AccountGroup> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account groups that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account groups that the user has permission to view
	 */
	@Override
	public List<AccountGroup> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the account groups where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of account groups where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching account groups
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of account groups that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching account groups that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			finderCache, new Object[] {uuid, companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<AccountGroup, NoSuchGroupException>
			_collectionPersistenceFinderByAccountGroupId;

	/**
	 * Returns an ordered range of all the account groups where accountGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param accountGroupId the account group ID
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account groups
	 */
	@Override
	public List<AccountGroup> findByAccountGroupId(
		long accountGroupId, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAccountGroupId.find(
			finderCache, new Object[] {new long[] {accountGroupId}}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first account group in the ordered set where accountGroupId = &#63;.
	 *
	 * @param accountGroupId the account group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group
	 * @throws NoSuchGroupException if a matching account group could not be found
	 */
	@Override
	public AccountGroup findByAccountGroupId_First(
			long accountGroupId,
			OrderByComparator<AccountGroup> orderByComparator)
		throws NoSuchGroupException {

		AccountGroup accountGroup = fetchByAccountGroupId_First(
			accountGroupId, orderByComparator);

		if (accountGroup != null) {
			return accountGroup;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("accountGroupId=");
		sb.append(accountGroupId);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first account group in the ordered set where accountGroupId = &#63;.
	 *
	 * @param accountGroupId the account group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group, or <code>null</code> if a matching account group could not be found
	 */
	@Override
	public AccountGroup fetchByAccountGroupId_First(
		long accountGroupId,
		OrderByComparator<AccountGroup> orderByComparator) {

		return _collectionPersistenceFinderByAccountGroupId.fetchFirst(
			finderCache, new Object[] {new long[] {accountGroupId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account groups that the user has permissions to view where accountGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param accountGroupId the account group ID
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account groups that the user has permission to view
	 */
	@Override
	public List<AccountGroup> filterFindByAccountGroupId(
		long accountGroupId, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator) {

		return _collectionPersistenceFinderByAccountGroupId.filterFind(
			finderCache, new Object[] {new long[] {accountGroupId}}, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account groups that the user has permission to view where accountGroupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param accountGroupIds the account group IDs
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account groups that the user has permission to view
	 */
	@Override
	public List<AccountGroup> filterFindByAccountGroupId(
		long[] accountGroupIds, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator) {

		return _collectionPersistenceFinderByAccountGroupId.filterFind(
			finderCache, new Object[] {ArrayUtil.sortedUnique(accountGroupIds)},
			start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account groups where accountGroupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param accountGroupIds the account group IDs
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account groups
	 */
	@Override
	public List<AccountGroup> findByAccountGroupId(
		long[] accountGroupIds, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAccountGroupId.find(
			finderCache, new Object[] {ArrayUtil.sortedUnique(accountGroupIds)},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the account groups where accountGroupId = &#63; from the database.
	 *
	 * @param accountGroupId the account group ID
	 */
	@Override
	public void removeByAccountGroupId(long accountGroupId) {
		_collectionPersistenceFinderByAccountGroupId.remove(
			finderCache, new Object[] {new long[] {accountGroupId}});
	}

	/**
	 * Returns the number of account groups where accountGroupId = &#63;.
	 *
	 * @param accountGroupId the account group ID
	 * @return the number of matching account groups
	 */
	@Override
	public int countByAccountGroupId(long accountGroupId) {
		return _collectionPersistenceFinderByAccountGroupId.count(
			finderCache, new Object[] {new long[] {accountGroupId}});
	}

	/**
	 * Returns the number of account groups where accountGroupId = any &#63;.
	 *
	 * @param accountGroupIds the account group IDs
	 * @return the number of matching account groups
	 */
	@Override
	public int countByAccountGroupId(long[] accountGroupIds) {
		return _collectionPersistenceFinderByAccountGroupId.count(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(accountGroupIds)});
	}

	/**
	 * Returns the number of account groups that the user has permission to view where accountGroupId = &#63;.
	 *
	 * @param accountGroupId the account group ID
	 * @return the number of matching account groups that the user has permission to view
	 */
	@Override
	public int filterCountByAccountGroupId(long accountGroupId) {
		return _collectionPersistenceFinderByAccountGroupId.filterCount(
			finderCache, new Object[] {new long[] {accountGroupId}});
	}

	/**
	 * Returns the number of account groups that the user has permission to view where accountGroupId = any &#63;.
	 *
	 * @param accountGroupIds the account group IDs
	 * @return the number of matching account groups that the user has permission to view
	 */
	@Override
	public int filterCountByAccountGroupId(long[] accountGroupIds) {
		return _collectionPersistenceFinderByAccountGroupId.filterCount(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(accountGroupIds)});
	}

	private FilterCollectionPersistenceFinder
		<AccountGroup, NoSuchGroupException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the account groups where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account groups
	 */
	@Override
	public List<AccountGroup> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first account group in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group
	 * @throws NoSuchGroupException if a matching account group could not be found
	 */
	@Override
	public AccountGroup findByCompanyId_First(
			long companyId, OrderByComparator<AccountGroup> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first account group in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group, or <code>null</code> if a matching account group could not be found
	 */
	@Override
	public AccountGroup fetchByCompanyId_First(
		long companyId, OrderByComparator<AccountGroup> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account groups that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account groups that the user has permission to view
	 */
	@Override
	public List<AccountGroup> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the account groups where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of account groups where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching account groups
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of account groups that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching account groups that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<AccountGroup, NoSuchGroupException> _collectionPersistenceFinderByC_D;

	/**
	 * Returns an ordered range of all the account groups where companyId = &#63; and defaultAccountGroup = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param defaultAccountGroup the default account group
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account groups
	 */
	@Override
	public List<AccountGroup> findByC_D(
		long companyId, boolean defaultAccountGroup, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_D.find(
			finderCache, new Object[] {companyId, defaultAccountGroup}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first account group in the ordered set where companyId = &#63; and defaultAccountGroup = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultAccountGroup the default account group
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group
	 * @throws NoSuchGroupException if a matching account group could not be found
	 */
	@Override
	public AccountGroup findByC_D_First(
			long companyId, boolean defaultAccountGroup,
			OrderByComparator<AccountGroup> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByC_D.findFirst(
			finderCache, new Object[] {companyId, defaultAccountGroup},
			orderByComparator);
	}

	/**
	 * Returns the first account group in the ordered set where companyId = &#63; and defaultAccountGroup = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultAccountGroup the default account group
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group, or <code>null</code> if a matching account group could not be found
	 */
	@Override
	public AccountGroup fetchByC_D_First(
		long companyId, boolean defaultAccountGroup,
		OrderByComparator<AccountGroup> orderByComparator) {

		return _collectionPersistenceFinderByC_D.fetchFirst(
			finderCache, new Object[] {companyId, defaultAccountGroup},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account groups that the user has permissions to view where companyId = &#63; and defaultAccountGroup = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param defaultAccountGroup the default account group
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account groups that the user has permission to view
	 */
	@Override
	public List<AccountGroup> filterFindByC_D(
		long companyId, boolean defaultAccountGroup, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator) {

		return _collectionPersistenceFinderByC_D.filterFind(
			finderCache, new Object[] {companyId, defaultAccountGroup}, start,
			end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the account groups where companyId = &#63; and defaultAccountGroup = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param defaultAccountGroup the default account group
	 */
	@Override
	public void removeByC_D(long companyId, boolean defaultAccountGroup) {
		_collectionPersistenceFinderByC_D.remove(
			finderCache, new Object[] {companyId, defaultAccountGroup});
	}

	/**
	 * Returns the number of account groups where companyId = &#63; and defaultAccountGroup = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultAccountGroup the default account group
	 * @return the number of matching account groups
	 */
	@Override
	public int countByC_D(long companyId, boolean defaultAccountGroup) {
		return _collectionPersistenceFinderByC_D.count(
			finderCache, new Object[] {companyId, defaultAccountGroup});
	}

	/**
	 * Returns the number of account groups that the user has permission to view where companyId = &#63; and defaultAccountGroup = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultAccountGroup the default account group
	 * @return the number of matching account groups that the user has permission to view
	 */
	@Override
	public int filterCountByC_D(long companyId, boolean defaultAccountGroup) {
		return _collectionPersistenceFinderByC_D.filterCount(
			finderCache, new Object[] {companyId, defaultAccountGroup},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<AccountGroup, NoSuchGroupException>
			_collectionPersistenceFinderByC_LikeN;

	/**
	 * Returns all the account groups where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching account groups
	 */
	@Override
	public List<AccountGroup> findByC_LikeN(long companyId, String name) {
		return findByC_LikeN(
			companyId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the account groups where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @return the range of matching account groups
	 */
	@Override
	public List<AccountGroup> findByC_LikeN(
		long companyId, String name, int start, int end) {

		return findByC_LikeN(companyId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the account groups where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account groups
	 */
	@Override
	public List<AccountGroup> findByC_LikeN(
		long companyId, String name, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator) {

		return findByC_LikeN(
			companyId, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the account groups where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account groups
	 */
	@Override
	public List<AccountGroup> findByC_LikeN(
		long companyId, String name, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_LikeN.find(
			finderCache, new Object[] {companyId, name}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first account group in the ordered set where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group
	 * @throws NoSuchGroupException if a matching account group could not be found
	 */
	@Override
	public AccountGroup findByC_LikeN_First(
			long companyId, String name,
			OrderByComparator<AccountGroup> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByC_LikeN.findFirst(
			finderCache, new Object[] {companyId, name}, orderByComparator);
	}

	/**
	 * Returns the first account group in the ordered set where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group, or <code>null</code> if a matching account group could not be found
	 */
	@Override
	public AccountGroup fetchByC_LikeN_First(
		long companyId, String name,
		OrderByComparator<AccountGroup> orderByComparator) {

		return _collectionPersistenceFinderByC_LikeN.fetchFirst(
			finderCache, new Object[] {companyId, name}, orderByComparator);
	}

	/**
	 * Returns all the account groups that the user has permission to view where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching account groups that the user has permission to view
	 */
	@Override
	public List<AccountGroup> filterFindByC_LikeN(long companyId, String name) {
		return filterFindByC_LikeN(
			companyId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the account groups that the user has permission to view where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @return the range of matching account groups that the user has permission to view
	 */
	@Override
	public List<AccountGroup> filterFindByC_LikeN(
		long companyId, String name, int start, int end) {

		return filterFindByC_LikeN(companyId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the account groups that the user has permissions to view where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account groups that the user has permission to view
	 */
	@Override
	public List<AccountGroup> filterFindByC_LikeN(
		long companyId, String name, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator) {

		return _collectionPersistenceFinderByC_LikeN.filterFind(
			finderCache, new Object[] {companyId, name}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the account groups where companyId = &#63; and name LIKE &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 */
	@Override
	public void removeByC_LikeN(long companyId, String name) {
		_collectionPersistenceFinderByC_LikeN.remove(
			finderCache, new Object[] {companyId, name});
	}

	/**
	 * Returns the number of account groups where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching account groups
	 */
	@Override
	public int countByC_LikeN(long companyId, String name) {
		return _collectionPersistenceFinderByC_LikeN.count(
			finderCache, new Object[] {companyId, name});
	}

	/**
	 * Returns the number of account groups that the user has permission to view where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching account groups that the user has permission to view
	 */
	@Override
	public int filterCountByC_LikeN(long companyId, String name) {
		return _collectionPersistenceFinderByC_LikeN.filterCount(
			finderCache, new Object[] {companyId, name}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<AccountGroup, NoSuchGroupException> _collectionPersistenceFinderByC_T;

	/**
	 * Returns an ordered range of all the account groups where companyId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account groups
	 */
	@Override
	public List<AccountGroup> findByC_T(
		long companyId, String type, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_T.find(
			finderCache, new Object[] {companyId, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first account group in the ordered set where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group
	 * @throws NoSuchGroupException if a matching account group could not be found
	 */
	@Override
	public AccountGroup findByC_T_First(
			long companyId, String type,
			OrderByComparator<AccountGroup> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByC_T.findFirst(
			finderCache, new Object[] {companyId, type}, orderByComparator);
	}

	/**
	 * Returns the first account group in the ordered set where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group, or <code>null</code> if a matching account group could not be found
	 */
	@Override
	public AccountGroup fetchByC_T_First(
		long companyId, String type,
		OrderByComparator<AccountGroup> orderByComparator) {

		return _collectionPersistenceFinderByC_T.fetchFirst(
			finderCache, new Object[] {companyId, type}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account groups that the user has permissions to view where companyId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account groups that the user has permission to view
	 */
	@Override
	public List<AccountGroup> filterFindByC_T(
		long companyId, String type, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator) {

		return _collectionPersistenceFinderByC_T.filterFind(
			finderCache, new Object[] {companyId, type}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the account groups where companyId = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 */
	@Override
	public void removeByC_T(long companyId, String type) {
		_collectionPersistenceFinderByC_T.remove(
			finderCache, new Object[] {companyId, type});
	}

	/**
	 * Returns the number of account groups where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the number of matching account groups
	 */
	@Override
	public int countByC_T(long companyId, String type) {
		return _collectionPersistenceFinderByC_T.count(
			finderCache, new Object[] {companyId, type});
	}

	/**
	 * Returns the number of account groups that the user has permission to view where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the number of matching account groups that the user has permission to view
	 */
	@Override
	public int filterCountByC_T(long companyId, String type) {
		return _collectionPersistenceFinderByC_T.filterCount(
			finderCache, new Object[] {companyId, type}, companyId, 0);
	}

	private UniquePersistenceFinder<AccountGroup, NoSuchGroupException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the account group where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching account group
	 * @throws NoSuchGroupException if a matching account group could not be found
	 */
	@Override
	public AccountGroup findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchGroupException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the account group where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching account group, or <code>null</code> if a matching account group could not be found
	 */
	@Override
	public AccountGroup fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the account group where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the account group that was removed
	 */
	@Override
	public AccountGroup removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchGroupException {

		AccountGroup accountGroup = findByERC_C(
			externalReferenceCode, companyId);

		return remove(accountGroup);
	}

	/**
	 * Returns the number of account groups where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching account groups
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public AccountGroupPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(AccountGroup.class);

		setModelImplClass(AccountGroupImpl.class);
		setModelPKClass(long.class);

		setTable(AccountGroupTable.INSTANCE);
	}

	/**
	 * Creates a new account group with the primary key. Does not add the account group to the database.
	 *
	 * @param accountGroupId the primary key for the new account group
	 * @return the new account group
	 */
	@Override
	public AccountGroup create(long accountGroupId) {
		AccountGroup accountGroup = new AccountGroupImpl();

		accountGroup.setNew(true);
		accountGroup.setPrimaryKey(accountGroupId);

		String uuid = PortalUUIDUtil.generate();

		accountGroup.setUuid(uuid);

		accountGroup.setCompanyId(CompanyThreadLocal.getCompanyId());

		return accountGroup;
	}

	/**
	 * Removes the account group with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param accountGroupId the primary key of the account group
	 * @return the account group that was removed
	 * @throws NoSuchGroupException if a account group with the primary key could not be found
	 */
	@Override
	public AccountGroup remove(long accountGroupId)
		throws NoSuchGroupException {

		return remove((Serializable)accountGroupId);
	}

	@Override
	protected AccountGroup removeImpl(AccountGroup accountGroup) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(accountGroup)) {
				accountGroup = (AccountGroup)session.get(
					AccountGroupImpl.class, accountGroup.getPrimaryKeyObj());
			}

			if (accountGroup != null) {
				session.delete(accountGroup);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (accountGroup != null) {
			clearCache(accountGroup);
		}

		return accountGroup;
	}

	@Override
	public AccountGroup updateImpl(AccountGroup accountGroup) {
		boolean isNew = accountGroup.isNew();

		if (!(accountGroup instanceof AccountGroupModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(accountGroup.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					accountGroup);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in accountGroup proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AccountGroup implementation " +
					accountGroup.getClass());
		}

		AccountGroupModelImpl accountGroupModelImpl =
			(AccountGroupModelImpl)accountGroup;

		if (Validator.isNull(accountGroup.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			accountGroup.setUuid(uuid);
		}

		if (Validator.isNull(accountGroup.getExternalReferenceCode())) {
			accountGroup.setExternalReferenceCode(accountGroup.getUuid());
		}
		else {
			if (!Objects.equals(
					accountGroupModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					accountGroup.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = accountGroup.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = accountGroup.getPrimaryKey();
					}

					try {
						accountGroup.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								AccountGroup.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								accountGroup.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			AccountGroup ercAccountGroup = fetchByERC_C(
				accountGroup.getExternalReferenceCode(),
				accountGroup.getCompanyId());

			if (isNew) {
				if (ercAccountGroup != null) {
					throw new DuplicateAccountGroupExternalReferenceCodeException(
						"Duplicate account group with external reference code " +
							accountGroup.getExternalReferenceCode() +
								" and company " + accountGroup.getCompanyId());
				}
			}
			else {
				if ((ercAccountGroup != null) &&
					(accountGroup.getAccountGroupId() !=
						ercAccountGroup.getAccountGroupId())) {

					throw new DuplicateAccountGroupExternalReferenceCodeException(
						"Duplicate account group with external reference code " +
							accountGroup.getExternalReferenceCode() +
								" and company " + accountGroup.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (accountGroup.getCreateDate() == null)) {
			if (serviceContext == null) {
				accountGroup.setCreateDate(date);
			}
			else {
				accountGroup.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!accountGroupModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				accountGroup.setModifiedDate(date);
			}
			else {
				accountGroup.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(accountGroup);
			}
			else {
				accountGroup = (AccountGroup)session.merge(accountGroup);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(accountGroup, false);

		if (isNew) {
			accountGroup.setNew(false);
		}

		accountGroup.resetOriginalValues();

		return accountGroup;
	}

	/**
	 * Returns the account group with the primary key or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param accountGroupId the primary key of the account group
	 * @return the account group
	 * @throws NoSuchGroupException if a account group with the primary key could not be found
	 */
	@Override
	public AccountGroup findByPrimaryKey(long accountGroupId)
		throws NoSuchGroupException {

		return findByPrimaryKey((Serializable)accountGroupId);
	}

	/**
	 * Returns the account group with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param accountGroupId the primary key of the account group
	 * @return the account group, or <code>null</code> if a account group with the primary key could not be found
	 */
	@Override
	public AccountGroup fetchByPrimaryKey(long accountGroupId) {
		return fetchByPrimaryKey((Serializable)accountGroupId);
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
		return "accountGroupId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ACCOUNTGROUP;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return AccountGroupModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the account group persistence.
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
				_SQL_SELECT_ACCOUNTGROUP_WHERE, _SQL_COUNT_ACCOUNTGROUP_WHERE,
				AccountGroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"accountGroup.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, AccountGroup::getUuid));

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
				_SQL_SELECT_ACCOUNTGROUP_WHERE, _SQL_COUNT_ACCOUNTGROUP_WHERE,
				AccountGroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"accountGroup.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, AccountGroup::getUuid),
				new FinderColumn<>(
					"accountGroup.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, AccountGroup::getCompanyId));

		_collectionPersistenceFinderByAccountGroupId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByAccountGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"accountGroupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByAccountGroupId", new String[] {Long.class.getName()},
					new String[] {"accountGroupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByAccountGroupId",
					new String[] {Long.class.getName()},
					new String[] {"accountGroupId"}, false),
				_SQL_SELECT_ACCOUNTGROUP_WHERE, _SQL_COUNT_ACCOUNTGROUP_WHERE,
				AccountGroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new ArrayableFinderColumn<>(
					"accountGroup.", "accountGroupId", FinderColumn.Type.LONG,
					"=", false, true, true, AccountGroup::getAccountGroupId));

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
				_SQL_SELECT_ACCOUNTGROUP_WHERE, _SQL_COUNT_ACCOUNTGROUP_WHERE,
				AccountGroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"accountGroup.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, AccountGroup::getCompanyId));

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
					new String[] {"companyId", "defaultAccountGroup"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_D",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "defaultAccountGroup"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_D",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "defaultAccountGroup"}, false),
				_SQL_SELECT_ACCOUNTGROUP_WHERE, _SQL_COUNT_ACCOUNTGROUP_WHERE,
				AccountGroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"accountGroup.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, AccountGroup::getCompanyId),
				new FinderColumn<>(
					"accountGroup.", "defaultAccountGroup",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					AccountGroup::isDefaultAccountGroup));

		_collectionPersistenceFinderByC_LikeN =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LikeN",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "name"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LikeN",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "name"}, false),
				_SQL_SELECT_ACCOUNTGROUP_WHERE, _SQL_COUNT_ACCOUNTGROUP_WHERE,
				AccountGroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"accountGroup.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, AccountGroup::getCompanyId),
				new FinderColumn<>(
					"accountGroup.", "name", FinderColumn.Type.STRING, "LIKE",
					false, true, AccountGroup::getName));

		_collectionPersistenceFinderByC_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_T",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "type_"}, 0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_T",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "type_"}, 0, 2, false, null),
				_SQL_SELECT_ACCOUNTGROUP_WHERE, _SQL_COUNT_ACCOUNTGROUP_WHERE,
				AccountGroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"accountGroup.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, AccountGroup::getCompanyId),
				new FinderColumn<>(
					"accountGroup.", "type", "type_", FinderColumn.Type.STRING,
					"=", true, true, AccountGroup::getType));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(AccountGroup::getExternalReferenceCode),
				AccountGroup::getCompanyId),
			_SQL_SELECT_ACCOUNTGROUP_WHERE, "",
			new FinderColumn<>(
				"accountGroup.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				AccountGroup::getExternalReferenceCode),
			new FinderColumn<>(
				"accountGroup.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, AccountGroup::getCompanyId));

		AccountGroupUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AccountGroupUtil.setPersistence(null);

		entityCache.removeCache(AccountGroupImpl.class.getName());
	}

	@Override
	@Reference(
		target = AccountPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = AccountPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = AccountPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		AccountGroupModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ACCOUNTGROUP =
		"SELECT accountGroup FROM AccountGroup accountGroup";

	private static final String _SQL_SELECT_ACCOUNTGROUP_WHERE =
		"SELECT accountGroup FROM AccountGroup accountGroup WHERE ";

	private static final String _SQL_COUNT_ACCOUNTGROUP_WHERE =
		"SELECT COUNT(accountGroup) FROM AccountGroup accountGroup WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No AccountGroup exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		AccountGroupPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1538491607