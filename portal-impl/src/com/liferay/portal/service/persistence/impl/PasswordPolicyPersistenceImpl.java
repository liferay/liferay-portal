/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchPasswordPolicyException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.PasswordPolicyTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.PasswordPolicyPersistence;
import com.liferay.portal.kernel.service.persistence.PasswordPolicyUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.model.impl.PasswordPolicyImpl;
import com.liferay.portal.model.impl.PasswordPolicyModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the password policy service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PasswordPolicyPersistenceImpl
	extends BasePersistenceImpl<PasswordPolicy, NoSuchPasswordPolicyException>
	implements PasswordPolicyPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PasswordPolicyUtil</code> to access the password policy persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PasswordPolicyImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<PasswordPolicy, NoSuchPasswordPolicyException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the password policies where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PasswordPolicyModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of password policies
	 * @param end the upper bound of the range of password policies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching password policies
	 */
	@Override
	public List<PasswordPolicy> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<PasswordPolicy> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first password policy in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching password policy
	 * @throws NoSuchPasswordPolicyException if a matching password policy could not be found
	 */
	@Override
	public PasswordPolicy findByUuid_First(
			String uuid, OrderByComparator<PasswordPolicy> orderByComparator)
		throws NoSuchPasswordPolicyException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first password policy in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching password policy, or <code>null</code> if a matching password policy could not be found
	 */
	@Override
	public PasswordPolicy fetchByUuid_First(
		String uuid, OrderByComparator<PasswordPolicy> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the password policies that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PasswordPolicyModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of password policies
	 * @param end the upper bound of the range of password policies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching password policies that the user has permission to view
	 */
	@Override
	public List<PasswordPolicy> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<PasswordPolicy> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the password policies where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of password policies where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching password policies
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of password policies that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching password policies that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder
		<PasswordPolicy, NoSuchPasswordPolicyException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the password policies where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PasswordPolicyModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of password policies
	 * @param end the upper bound of the range of password policies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching password policies
	 */
	@Override
	public List<PasswordPolicy> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<PasswordPolicy> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first password policy in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching password policy
	 * @throws NoSuchPasswordPolicyException if a matching password policy could not be found
	 */
	@Override
	public PasswordPolicy findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<PasswordPolicy> orderByComparator)
		throws NoSuchPasswordPolicyException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first password policy in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching password policy, or <code>null</code> if a matching password policy could not be found
	 */
	@Override
	public PasswordPolicy fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<PasswordPolicy> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the password policies that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PasswordPolicyModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of password policies
	 * @param end the upper bound of the range of password policies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching password policies that the user has permission to view
	 */
	@Override
	public List<PasswordPolicy> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<PasswordPolicy> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the password policies where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of password policies where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching password policies
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of password policies that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching password policies that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<PasswordPolicy, NoSuchPasswordPolicyException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the password policies where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PasswordPolicyModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of password policies
	 * @param end the upper bound of the range of password policies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching password policies
	 */
	@Override
	public List<PasswordPolicy> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<PasswordPolicy> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first password policy in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching password policy
	 * @throws NoSuchPasswordPolicyException if a matching password policy could not be found
	 */
	@Override
	public PasswordPolicy findByCompanyId_First(
			long companyId, OrderByComparator<PasswordPolicy> orderByComparator)
		throws NoSuchPasswordPolicyException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first password policy in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching password policy, or <code>null</code> if a matching password policy could not be found
	 */
	@Override
	public PasswordPolicy fetchByCompanyId_First(
		long companyId, OrderByComparator<PasswordPolicy> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the password policies that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PasswordPolicyModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of password policies
	 * @param end the upper bound of the range of password policies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching password policies that the user has permission to view
	 */
	@Override
	public List<PasswordPolicy> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<PasswordPolicy> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the password policies where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of password policies where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching password policies
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of password policies that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching password policies that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			companyId, 0);
	}

	private UniquePersistenceFinder
		<PasswordPolicy, NoSuchPasswordPolicyException>
			_uniquePersistenceFinderByC_N;

	/**
	 * Returns the password policy where companyId = &#63; and name = &#63; or throws a <code>NoSuchPasswordPolicyException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching password policy
	 * @throws NoSuchPasswordPolicyException if a matching password policy could not be found
	 */
	@Override
	public PasswordPolicy findByC_N(long companyId, String name)
		throws NoSuchPasswordPolicyException {

		return _uniquePersistenceFinderByC_N.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, name});
	}

	/**
	 * Returns the password policy where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching password policy, or <code>null</code> if a matching password policy could not be found
	 */
	@Override
	public PasswordPolicy fetchByC_N(
		long companyId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_N.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, name},
			useFinderCache);
	}

	/**
	 * Removes the password policy where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the password policy that was removed
	 */
	@Override
	public PasswordPolicy removeByC_N(long companyId, String name)
		throws NoSuchPasswordPolicyException {

		PasswordPolicy passwordPolicy = findByC_N(companyId, name);

		return remove(passwordPolicy);
	}

	/**
	 * Returns the number of password policies where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching password policies
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		return _uniquePersistenceFinderByC_N.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, name});
	}

	public PasswordPolicyPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(PasswordPolicy.class);

		setModelImplClass(PasswordPolicyImpl.class);
		setModelPKClass(long.class);

		setTable(PasswordPolicyTable.INSTANCE);
	}

	/**
	 * Creates a new password policy with the primary key. Does not add the password policy to the database.
	 *
	 * @param passwordPolicyId the primary key for the new password policy
	 * @return the new password policy
	 */
	@Override
	public PasswordPolicy create(long passwordPolicyId) {
		PasswordPolicy passwordPolicy = new PasswordPolicyImpl();

		passwordPolicy.setNew(true);
		passwordPolicy.setPrimaryKey(passwordPolicyId);

		String uuid = PortalUUIDUtil.generate();

		passwordPolicy.setUuid(uuid);

		passwordPolicy.setCompanyId(CompanyThreadLocal.getCompanyId());

		return passwordPolicy;
	}

	/**
	 * Removes the password policy with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @return the password policy that was removed
	 * @throws NoSuchPasswordPolicyException if a password policy with the primary key could not be found
	 */
	@Override
	public PasswordPolicy remove(long passwordPolicyId)
		throws NoSuchPasswordPolicyException {

		return remove((Serializable)passwordPolicyId);
	}

	@Override
	protected PasswordPolicy removeImpl(PasswordPolicy passwordPolicy) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(passwordPolicy)) {
				passwordPolicy = (PasswordPolicy)session.get(
					PasswordPolicyImpl.class,
					passwordPolicy.getPrimaryKeyObj());
			}

			if (passwordPolicy != null) {
				session.delete(passwordPolicy);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (passwordPolicy != null) {
			clearCache(passwordPolicy);
		}

		return passwordPolicy;
	}

	@Override
	public PasswordPolicy updateImpl(PasswordPolicy passwordPolicy) {
		boolean isNew = passwordPolicy.isNew();

		if (!(passwordPolicy instanceof PasswordPolicyModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(passwordPolicy.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					passwordPolicy);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in passwordPolicy proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PasswordPolicy implementation " +
					passwordPolicy.getClass());
		}

		PasswordPolicyModelImpl passwordPolicyModelImpl =
			(PasswordPolicyModelImpl)passwordPolicy;

		if (Validator.isNull(passwordPolicy.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			passwordPolicy.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (passwordPolicy.getCreateDate() == null)) {
			if (serviceContext == null) {
				passwordPolicy.setCreateDate(date);
			}
			else {
				passwordPolicy.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!passwordPolicyModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				passwordPolicy.setModifiedDate(date);
			}
			else {
				passwordPolicy.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(passwordPolicy);
			}
			else {
				passwordPolicy = (PasswordPolicy)session.merge(passwordPolicy);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(passwordPolicy, false);

		if (isNew) {
			passwordPolicy.setNew(false);
		}

		passwordPolicy.resetOriginalValues();

		return passwordPolicy;
	}

	/**
	 * Returns the password policy with the primary key or throws a <code>NoSuchPasswordPolicyException</code> if it could not be found.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @return the password policy
	 * @throws NoSuchPasswordPolicyException if a password policy with the primary key could not be found
	 */
	@Override
	public PasswordPolicy findByPrimaryKey(long passwordPolicyId)
		throws NoSuchPasswordPolicyException {

		return findByPrimaryKey((Serializable)passwordPolicyId);
	}

	/**
	 * Returns the password policy with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @return the password policy, or <code>null</code> if a password policy with the primary key could not be found
	 */
	@Override
	public PasswordPolicy fetchByPrimaryKey(long passwordPolicyId) {
		return fetchByPrimaryKey((Serializable)passwordPolicyId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "passwordPolicyId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PASSWORDPOLICY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PasswordPolicyModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the password policy persistence.
	 */
	public void afterPropertiesSet() {
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
				_SQL_SELECT_PASSWORDPOLICY_WHERE,
				_SQL_COUNT_PASSWORDPOLICY_WHERE,
				PasswordPolicyModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"passwordPolicy.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					PasswordPolicy::getUuid));

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
				_SQL_SELECT_PASSWORDPOLICY_WHERE,
				_SQL_COUNT_PASSWORDPOLICY_WHERE,
				PasswordPolicyModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"passwordPolicy.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					PasswordPolicy::getUuid),
				new FinderColumn<>(
					"passwordPolicy.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, PasswordPolicy::getCompanyId));

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
				_SQL_SELECT_PASSWORDPOLICY_WHERE,
				_SQL_COUNT_PASSWORDPOLICY_WHERE,
				PasswordPolicyModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"passwordPolicy.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, PasswordPolicy::getCompanyId));

		_uniquePersistenceFinderByC_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "name"}, 0, 2, false,
				PasswordPolicy::getCompanyId,
				convertNullFunction(PasswordPolicy::getName)),
			_SQL_SELECT_PASSWORDPOLICY_WHERE, "",
			new FinderColumn<>(
				"passwordPolicy.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, PasswordPolicy::getCompanyId),
			new FinderColumn<>(
				"passwordPolicy.", "name", FinderColumn.Type.STRING, "=", true,
				true, PasswordPolicy::getName));

		PasswordPolicyUtil.setPersistence(this);
	}

	public void destroy() {
		PasswordPolicyUtil.setPersistence(null);

		EntityCacheUtil.removeCache(PasswordPolicyImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		PasswordPolicyModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PASSWORDPOLICY =
		"SELECT passwordPolicy FROM PasswordPolicy passwordPolicy";

	private static final String _SQL_SELECT_PASSWORDPOLICY_WHERE =
		"SELECT passwordPolicy FROM PasswordPolicy passwordPolicy WHERE ";

	private static final String _SQL_COUNT_PASSWORDPOLICY_WHERE =
		"SELECT COUNT(passwordPolicy) FROM PasswordPolicy passwordPolicy WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PasswordPolicy exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PasswordPolicyPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1897827311