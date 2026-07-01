/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.persistence.impl;

import com.liferay.account.exception.DuplicateAccountRoleExternalReferenceCodeException;
import com.liferay.account.exception.NoSuchRoleException;
import com.liferay.account.model.AccountRole;
import com.liferay.account.model.AccountRoleTable;
import com.liferay.account.model.impl.AccountRoleImpl;
import com.liferay.account.model.impl.AccountRoleModelImpl;
import com.liferay.account.service.persistence.AccountRolePersistence;
import com.liferay.account.service.persistence.AccountRoleUtil;
import com.liferay.account.service.persistence.impl.constants.AccountPersistenceConstants;
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
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the account role service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AccountRolePersistence.class)
public class AccountRolePersistenceImpl
	extends BasePersistenceImpl<AccountRole, NoSuchRoleException>
	implements AccountRolePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AccountRoleUtil</code> to access the account role persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AccountRoleImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder<AccountRole, NoSuchRoleException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the account roles where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account roles
	 */
	@Override
	public List<AccountRole> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first account role in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account role
	 * @throws NoSuchRoleException if a matching account role could not be found
	 */
	@Override
	public AccountRole findByCompanyId_First(
			long companyId, OrderByComparator<AccountRole> orderByComparator)
		throws NoSuchRoleException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first account role in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account role, or <code>null</code> if a matching account role could not be found
	 */
	@Override
	public AccountRole fetchByCompanyId_First(
		long companyId, OrderByComparator<AccountRole> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account roles that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the account roles where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of account roles where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching account roles
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of account roles that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching account roles that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder<AccountRole, NoSuchRoleException>
		_collectionPersistenceFinderByAccountEntryId;

	/**
	 * Returns an ordered range of all the account roles where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account roles
	 */
	@Override
	public List<AccountRole> findByAccountEntryId(
		long accountEntryId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAccountEntryId.find(
			finderCache, new Object[] {new long[] {accountEntryId}}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first account role in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account role
	 * @throws NoSuchRoleException if a matching account role could not be found
	 */
	@Override
	public AccountRole findByAccountEntryId_First(
			long accountEntryId,
			OrderByComparator<AccountRole> orderByComparator)
		throws NoSuchRoleException {

		AccountRole accountRole = fetchByAccountEntryId_First(
			accountEntryId, orderByComparator);

		if (accountRole != null) {
			return accountRole;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("accountEntryId=");
		sb.append(accountEntryId);

		sb.append("}");

		throw new NoSuchRoleException(sb.toString());
	}

	/**
	 * Returns the first account role in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account role, or <code>null</code> if a matching account role could not be found
	 */
	@Override
	public AccountRole fetchByAccountEntryId_First(
		long accountEntryId, OrderByComparator<AccountRole> orderByComparator) {

		return _collectionPersistenceFinderByAccountEntryId.fetchFirst(
			finderCache, new Object[] {new long[] {accountEntryId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account roles that the user has permissions to view where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByAccountEntryId(
		long accountEntryId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		return _collectionPersistenceFinderByAccountEntryId.filterFind(
			finderCache, new Object[] {new long[] {accountEntryId}}, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account roles that the user has permission to view where accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByAccountEntryId(
		long[] accountEntryIds, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		return _collectionPersistenceFinderByAccountEntryId.filterFind(
			finderCache, new Object[] {ArrayUtil.sortedUnique(accountEntryIds)},
			start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account roles where accountEntryId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account roles
	 */
	@Override
	public List<AccountRole> findByAccountEntryId(
		long[] accountEntryIds, int start, int end,
		OrderByComparator<AccountRole> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAccountEntryId.find(
			finderCache, new Object[] {ArrayUtil.sortedUnique(accountEntryIds)},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the account roles where accountEntryId = &#63; from the database.
	 *
	 * @param accountEntryId the account entry ID
	 */
	@Override
	public void removeByAccountEntryId(long accountEntryId) {
		_collectionPersistenceFinderByAccountEntryId.remove(
			finderCache, new Object[] {new long[] {accountEntryId}});
	}

	/**
	 * Returns the number of account roles where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @return the number of matching account roles
	 */
	@Override
	public int countByAccountEntryId(long accountEntryId) {
		return _collectionPersistenceFinderByAccountEntryId.count(
			finderCache, new Object[] {new long[] {accountEntryId}});
	}

	/**
	 * Returns the number of account roles where accountEntryId = any &#63;.
	 *
	 * @param accountEntryIds the account entry IDs
	 * @return the number of matching account roles
	 */
	@Override
	public int countByAccountEntryId(long[] accountEntryIds) {
		return _collectionPersistenceFinderByAccountEntryId.count(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(accountEntryIds)});
	}

	/**
	 * Returns the number of account roles that the user has permission to view where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @return the number of matching account roles that the user has permission to view
	 */
	@Override
	public int filterCountByAccountEntryId(long accountEntryId) {
		return _collectionPersistenceFinderByAccountEntryId.filterCount(
			finderCache, new Object[] {new long[] {accountEntryId}});
	}

	/**
	 * Returns the number of account roles that the user has permission to view where accountEntryId = any &#63;.
	 *
	 * @param accountEntryIds the account entry IDs
	 * @return the number of matching account roles that the user has permission to view
	 */
	@Override
	public int filterCountByAccountEntryId(long[] accountEntryIds) {
		return _collectionPersistenceFinderByAccountEntryId.filterCount(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(accountEntryIds)});
	}

	private UniquePersistenceFinder<AccountRole, NoSuchRoleException>
		_uniquePersistenceFinderByRoleId;

	/**
	 * Returns the account role where roleId = &#63; or throws a <code>NoSuchRoleException</code> if it could not be found.
	 *
	 * @param roleId the role ID
	 * @return the matching account role
	 * @throws NoSuchRoleException if a matching account role could not be found
	 */
	@Override
	public AccountRole findByRoleId(long roleId) throws NoSuchRoleException {
		return _uniquePersistenceFinderByRoleId.find(
			finderCache, new Object[] {roleId});
	}

	/**
	 * Returns the account role where roleId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param roleId the role ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching account role, or <code>null</code> if a matching account role could not be found
	 */
	@Override
	public AccountRole fetchByRoleId(long roleId, boolean useFinderCache) {
		return _uniquePersistenceFinderByRoleId.fetch(
			finderCache, new Object[] {roleId}, useFinderCache);
	}

	/**
	 * Removes the account role where roleId = &#63; from the database.
	 *
	 * @param roleId the role ID
	 * @return the account role that was removed
	 */
	@Override
	public AccountRole removeByRoleId(long roleId) throws NoSuchRoleException {
		AccountRole accountRole = findByRoleId(roleId);

		return remove(accountRole);
	}

	/**
	 * Returns the number of account roles where roleId = &#63;.
	 *
	 * @param roleId the role ID
	 * @return the number of matching account roles
	 */
	@Override
	public int countByRoleId(long roleId) {
		return _uniquePersistenceFinderByRoleId.count(
			finderCache, new Object[] {roleId});
	}

	private FilterCollectionPersistenceFinder<AccountRole, NoSuchRoleException>
		_collectionPersistenceFinderByC_A;

	/**
	 * Returns an ordered range of all the account roles where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account roles
	 */
	@Override
	public List<AccountRole> findByC_A(
		long companyId, long accountEntryId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A.find(
			finderCache, new Object[] {companyId, new long[] {accountEntryId}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first account role in the ordered set where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account role
	 * @throws NoSuchRoleException if a matching account role could not be found
	 */
	@Override
	public AccountRole findByC_A_First(
			long companyId, long accountEntryId,
			OrderByComparator<AccountRole> orderByComparator)
		throws NoSuchRoleException {

		AccountRole accountRole = fetchByC_A_First(
			companyId, accountEntryId, orderByComparator);

		if (accountRole != null) {
			return accountRole;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", accountEntryId=");
		sb.append(accountEntryId);

		sb.append("}");

		throw new NoSuchRoleException(sb.toString());
	}

	/**
	 * Returns the first account role in the ordered set where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account role, or <code>null</code> if a matching account role could not be found
	 */
	@Override
	public AccountRole fetchByC_A_First(
		long companyId, long accountEntryId,
		OrderByComparator<AccountRole> orderByComparator) {

		return _collectionPersistenceFinderByC_A.fetchFirst(
			finderCache, new Object[] {companyId, new long[] {accountEntryId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account roles that the user has permissions to view where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByC_A(
		long companyId, long accountEntryId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		return _collectionPersistenceFinderByC_A.filterFind(
			finderCache, new Object[] {companyId, new long[] {accountEntryId}},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Returns an ordered range of all the account roles that the user has permission to view where companyId = &#63; and accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByC_A(
		long companyId, long[] accountEntryIds, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		return _collectionPersistenceFinderByC_A.filterFind(
			finderCache,
			new Object[] {companyId, ArrayUtil.sortedUnique(accountEntryIds)},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Returns an ordered range of all the account roles where companyId = &#63; and accountEntryId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account roles
	 */
	@Override
	public List<AccountRole> findByC_A(
		long companyId, long[] accountEntryIds, int start, int end,
		OrderByComparator<AccountRole> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A.find(
			finderCache,
			new Object[] {companyId, ArrayUtil.sortedUnique(accountEntryIds)},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the account roles where companyId = &#63; and accountEntryId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 */
	@Override
	public void removeByC_A(long companyId, long accountEntryId) {
		_collectionPersistenceFinderByC_A.remove(
			finderCache, new Object[] {companyId, new long[] {accountEntryId}});
	}

	/**
	 * Returns the number of account roles where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @return the number of matching account roles
	 */
	@Override
	public int countByC_A(long companyId, long accountEntryId) {
		return _collectionPersistenceFinderByC_A.count(
			finderCache, new Object[] {companyId, new long[] {accountEntryId}});
	}

	/**
	 * Returns the number of account roles where companyId = &#63; and accountEntryId = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @return the number of matching account roles
	 */
	@Override
	public int countByC_A(long companyId, long[] accountEntryIds) {
		return _collectionPersistenceFinderByC_A.count(
			finderCache,
			new Object[] {companyId, ArrayUtil.sortedUnique(accountEntryIds)});
	}

	/**
	 * Returns the number of account roles that the user has permission to view where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @return the number of matching account roles that the user has permission to view
	 */
	@Override
	public int filterCountByC_A(long companyId, long accountEntryId) {
		return _collectionPersistenceFinderByC_A.filterCount(
			finderCache, new Object[] {companyId, new long[] {accountEntryId}},
			companyId, 0);
	}

	/**
	 * Returns the number of account roles that the user has permission to view where companyId = &#63; and accountEntryId = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @return the number of matching account roles that the user has permission to view
	 */
	@Override
	public int filterCountByC_A(long companyId, long[] accountEntryIds) {
		return _collectionPersistenceFinderByC_A.filterCount(
			finderCache,
			new Object[] {companyId, ArrayUtil.sortedUnique(accountEntryIds)},
			companyId, 0);
	}

	private UniquePersistenceFinder<AccountRole, NoSuchRoleException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the account role where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchRoleException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching account role
	 * @throws NoSuchRoleException if a matching account role could not be found
	 */
	@Override
	public AccountRole findByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchRoleException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the account role where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching account role, or <code>null</code> if a matching account role could not be found
	 */
	@Override
	public AccountRole fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the account role where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the account role that was removed
	 */
	@Override
	public AccountRole removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchRoleException {

		AccountRole accountRole = findByERC_C(externalReferenceCode, companyId);

		return remove(accountRole);
	}

	/**
	 * Returns the number of account roles where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching account roles
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public AccountRolePersistenceImpl() {
		setModelClass(AccountRole.class);

		setModelImplClass(AccountRoleImpl.class);
		setModelPKClass(long.class);

		setTable(AccountRoleTable.INSTANCE);
	}

	/**
	 * Creates a new account role with the primary key. Does not add the account role to the database.
	 *
	 * @param accountRoleId the primary key for the new account role
	 * @return the new account role
	 */
	@Override
	public AccountRole create(long accountRoleId) {
		AccountRole accountRole = new AccountRoleImpl();

		accountRole.setNew(true);
		accountRole.setPrimaryKey(accountRoleId);

		accountRole.setCompanyId(CompanyThreadLocal.getCompanyId());

		return accountRole;
	}

	/**
	 * Removes the account role with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param accountRoleId the primary key of the account role
	 * @return the account role that was removed
	 * @throws NoSuchRoleException if a account role with the primary key could not be found
	 */
	@Override
	public AccountRole remove(long accountRoleId) throws NoSuchRoleException {
		return remove((Serializable)accountRoleId);
	}

	@Override
	protected AccountRole removeImpl(AccountRole accountRole) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(accountRole)) {
				accountRole = (AccountRole)session.get(
					AccountRoleImpl.class, accountRole.getPrimaryKeyObj());
			}

			if (accountRole != null) {
				session.delete(accountRole);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (accountRole != null) {
			clearCache(accountRole);
		}

		return accountRole;
	}

	@Override
	public AccountRole updateImpl(AccountRole accountRole) {
		boolean isNew = accountRole.isNew();

		if (!(accountRole instanceof AccountRoleModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(accountRole.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(accountRole);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in accountRole proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AccountRole implementation " +
					accountRole.getClass());
		}

		AccountRoleModelImpl accountRoleModelImpl =
			(AccountRoleModelImpl)accountRole;

		if (Validator.isNull(accountRole.getExternalReferenceCode())) {
			accountRole.setExternalReferenceCode(
				String.valueOf(accountRole.getPrimaryKey()));
		}
		else {
			if (!Objects.equals(
					accountRoleModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					accountRole.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = accountRole.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = accountRole.getPrimaryKey();
					}

					try {
						accountRole.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								AccountRole.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								accountRole.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			AccountRole ercAccountRole = fetchByERC_C(
				accountRole.getExternalReferenceCode(),
				accountRole.getCompanyId());

			if (isNew) {
				if (ercAccountRole != null) {
					throw new DuplicateAccountRoleExternalReferenceCodeException(
						"Duplicate account role with external reference code " +
							accountRole.getExternalReferenceCode() +
								" and company " + accountRole.getCompanyId());
				}
			}
			else {
				if ((ercAccountRole != null) &&
					(accountRole.getAccountRoleId() !=
						ercAccountRole.getAccountRoleId())) {

					throw new DuplicateAccountRoleExternalReferenceCodeException(
						"Duplicate account role with external reference code " +
							accountRole.getExternalReferenceCode() +
								" and company " + accountRole.getCompanyId());
				}
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(accountRole);
			}
			else {
				accountRole = (AccountRole)session.merge(accountRole);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(accountRole, false);

		if (isNew) {
			accountRole.setNew(false);
		}

		accountRole.resetOriginalValues();

		return accountRole;
	}

	/**
	 * Returns the account role with the primary key or throws a <code>NoSuchRoleException</code> if it could not be found.
	 *
	 * @param accountRoleId the primary key of the account role
	 * @return the account role
	 * @throws NoSuchRoleException if a account role with the primary key could not be found
	 */
	@Override
	public AccountRole findByPrimaryKey(long accountRoleId)
		throws NoSuchRoleException {

		return findByPrimaryKey((Serializable)accountRoleId);
	}

	/**
	 * Returns the account role with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param accountRoleId the primary key of the account role
	 * @return the account role, or <code>null</code> if a account role with the primary key could not be found
	 */
	@Override
	public AccountRole fetchByPrimaryKey(long accountRoleId) {
		return fetchByPrimaryKey((Serializable)accountRoleId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "accountRoleId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ACCOUNTROLE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return AccountRoleModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the account role persistence.
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
				_SQL_SELECT_ACCOUNTROLE_WHERE, _SQL_COUNT_ACCOUNTROLE_WHERE,
				AccountRoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"accountRole.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, AccountRole::getCompanyId));

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
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByAccountEntryId",
					new String[] {Long.class.getName()},
					new String[] {"accountEntryId"}, false),
				_SQL_SELECT_ACCOUNTROLE_WHERE, _SQL_COUNT_ACCOUNTROLE_WHERE,
				AccountRoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new ArrayableFinderColumn<>(
					"accountRole.", "accountEntryId", FinderColumn.Type.LONG,
					"=", false, true, true, AccountRole::getAccountEntryId));

		_uniquePersistenceFinderByRoleId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByRoleId",
				new String[] {Long.class.getName()}, new String[] {"roleId"}, 0,
				0, false, AccountRole::getRoleId),
			_SQL_SELECT_ACCOUNTROLE_WHERE, "",
			new FinderColumn<>(
				"accountRole.", "roleId", FinderColumn.Type.LONG, "=", true,
				true, AccountRole::getRoleId));

		_collectionPersistenceFinderByC_A =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "accountEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"companyId", "accountEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_A",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"companyId", "accountEntryId"}, false),
				_SQL_SELECT_ACCOUNTROLE_WHERE, _SQL_COUNT_ACCOUNTROLE_WHERE,
				AccountRoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"accountRole.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, AccountRole::getCompanyId),
				new ArrayableFinderColumn<>(
					"accountRole.", "accountEntryId", FinderColumn.Type.LONG,
					"=", false, true, true, AccountRole::getAccountEntryId));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(AccountRole::getExternalReferenceCode),
				AccountRole::getCompanyId),
			_SQL_SELECT_ACCOUNTROLE_WHERE, "",
			new FinderColumn<>(
				"accountRole.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				AccountRole::getExternalReferenceCode),
			new FinderColumn<>(
				"accountRole.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, AccountRole::getCompanyId));

		AccountRoleUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AccountRoleUtil.setPersistence(null);

		entityCache.removeCache(AccountRoleImpl.class.getName());
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
		AccountRoleModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ACCOUNTROLE =
		"SELECT accountRole FROM AccountRole accountRole";

	private static final String _SQL_SELECT_ACCOUNTROLE_WHERE =
		"SELECT accountRole FROM AccountRole accountRole WHERE ";

	private static final String _SQL_COUNT_ACCOUNTROLE_WHERE =
		"SELECT COUNT(accountRole) FROM AccountRole accountRole WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No AccountRole exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		AccountRolePersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:108756624