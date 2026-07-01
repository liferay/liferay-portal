/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.persistence.impl;

import com.liferay.account.exception.NoSuchGroupRelException;
import com.liferay.account.model.AccountGroupRel;
import com.liferay.account.model.AccountGroupRelTable;
import com.liferay.account.model.impl.AccountGroupRelImpl;
import com.liferay.account.model.impl.AccountGroupRelModelImpl;
import com.liferay.account.service.persistence.AccountGroupRelPersistence;
import com.liferay.account.service.persistence.AccountGroupRelUtil;
import com.liferay.account.service.persistence.impl.constants.AccountPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the account group rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AccountGroupRelPersistence.class)
public class AccountGroupRelPersistenceImpl
	extends BasePersistenceImpl<AccountGroupRel, NoSuchGroupRelException>
	implements AccountGroupRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AccountGroupRelUtil</code> to access the account group rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AccountGroupRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AccountGroupRel, NoSuchGroupRelException>
			_collectionPersistenceFinderByAccountGroupId;

	/**
	 * Returns an ordered range of all the account group rels where accountGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param accountGroupId the account group ID
	 * @param start the lower bound of the range of account group rels
	 * @param end the upper bound of the range of account group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account group rels
	 */
	@Override
	public List<AccountGroupRel> findByAccountGroupId(
		long accountGroupId, int start, int end,
		OrderByComparator<AccountGroupRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAccountGroupId.find(
			finderCache, new Object[] {accountGroupId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first account group rel in the ordered set where accountGroupId = &#63;.
	 *
	 * @param accountGroupId the account group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group rel
	 * @throws NoSuchGroupRelException if a matching account group rel could not be found
	 */
	@Override
	public AccountGroupRel findByAccountGroupId_First(
			long accountGroupId,
			OrderByComparator<AccountGroupRel> orderByComparator)
		throws NoSuchGroupRelException {

		return _collectionPersistenceFinderByAccountGroupId.findFirst(
			finderCache, new Object[] {accountGroupId}, orderByComparator);
	}

	/**
	 * Returns the first account group rel in the ordered set where accountGroupId = &#63;.
	 *
	 * @param accountGroupId the account group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group rel, or <code>null</code> if a matching account group rel could not be found
	 */
	@Override
	public AccountGroupRel fetchByAccountGroupId_First(
		long accountGroupId,
		OrderByComparator<AccountGroupRel> orderByComparator) {

		return _collectionPersistenceFinderByAccountGroupId.fetchFirst(
			finderCache, new Object[] {accountGroupId}, orderByComparator);
	}

	/**
	 * Removes all the account group rels where accountGroupId = &#63; from the database.
	 *
	 * @param accountGroupId the account group ID
	 */
	@Override
	public void removeByAccountGroupId(long accountGroupId) {
		_collectionPersistenceFinderByAccountGroupId.remove(
			finderCache, new Object[] {accountGroupId});
	}

	/**
	 * Returns the number of account group rels where accountGroupId = &#63;.
	 *
	 * @param accountGroupId the account group ID
	 * @return the number of matching account group rels
	 */
	@Override
	public int countByAccountGroupId(long accountGroupId) {
		return _collectionPersistenceFinderByAccountGroupId.count(
			finderCache, new Object[] {accountGroupId});
	}

	private CollectionPersistenceFinder
		<AccountGroupRel, NoSuchGroupRelException>
			_collectionPersistenceFinderByA_C;

	/**
	 * Returns an ordered range of all the account group rels where accountGroupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param accountGroupId the account group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of account group rels
	 * @param end the upper bound of the range of account group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account group rels
	 */
	@Override
	public List<AccountGroupRel> findByA_C(
		long accountGroupId, long classNameId, int start, int end,
		OrderByComparator<AccountGroupRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByA_C.find(
			finderCache, new Object[] {accountGroupId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first account group rel in the ordered set where accountGroupId = &#63; and classNameId = &#63;.
	 *
	 * @param accountGroupId the account group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group rel
	 * @throws NoSuchGroupRelException if a matching account group rel could not be found
	 */
	@Override
	public AccountGroupRel findByA_C_First(
			long accountGroupId, long classNameId,
			OrderByComparator<AccountGroupRel> orderByComparator)
		throws NoSuchGroupRelException {

		return _collectionPersistenceFinderByA_C.findFirst(
			finderCache, new Object[] {accountGroupId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first account group rel in the ordered set where accountGroupId = &#63; and classNameId = &#63;.
	 *
	 * @param accountGroupId the account group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group rel, or <code>null</code> if a matching account group rel could not be found
	 */
	@Override
	public AccountGroupRel fetchByA_C_First(
		long accountGroupId, long classNameId,
		OrderByComparator<AccountGroupRel> orderByComparator) {

		return _collectionPersistenceFinderByA_C.fetchFirst(
			finderCache, new Object[] {accountGroupId, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the account group rels where accountGroupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param accountGroupId the account group ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByA_C(long accountGroupId, long classNameId) {
		_collectionPersistenceFinderByA_C.remove(
			finderCache, new Object[] {accountGroupId, classNameId});
	}

	/**
	 * Returns the number of account group rels where accountGroupId = &#63; and classNameId = &#63;.
	 *
	 * @param accountGroupId the account group ID
	 * @param classNameId the class name ID
	 * @return the number of matching account group rels
	 */
	@Override
	public int countByA_C(long accountGroupId, long classNameId) {
		return _collectionPersistenceFinderByA_C.count(
			finderCache, new Object[] {accountGroupId, classNameId});
	}

	private CollectionPersistenceFinder
		<AccountGroupRel, NoSuchGroupRelException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the account group rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of account group rels
	 * @param end the upper bound of the range of account group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account group rels
	 */
	@Override
	public List<AccountGroupRel> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<AccountGroupRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first account group rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group rel
	 * @throws NoSuchGroupRelException if a matching account group rel could not be found
	 */
	@Override
	public AccountGroupRel findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<AccountGroupRel> orderByComparator)
		throws NoSuchGroupRelException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first account group rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account group rel, or <code>null</code> if a matching account group rel could not be found
	 */
	@Override
	public AccountGroupRel fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<AccountGroupRel> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the account group rels where classNameId = &#63; and classPK = &#63; from the database.
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
	 * Returns the number of account group rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching account group rels
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private UniquePersistenceFinder<AccountGroupRel, NoSuchGroupRelException>
		_uniquePersistenceFinderByA_C_C;

	/**
	 * Returns the account group rel where accountGroupId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchGroupRelException</code> if it could not be found.
	 *
	 * @param accountGroupId the account group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching account group rel
	 * @throws NoSuchGroupRelException if a matching account group rel could not be found
	 */
	@Override
	public AccountGroupRel findByA_C_C(
			long accountGroupId, long classNameId, long classPK)
		throws NoSuchGroupRelException {

		return _uniquePersistenceFinderByA_C_C.find(
			finderCache, new Object[] {accountGroupId, classNameId, classPK});
	}

	/**
	 * Returns the account group rel where accountGroupId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param accountGroupId the account group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching account group rel, or <code>null</code> if a matching account group rel could not be found
	 */
	@Override
	public AccountGroupRel fetchByA_C_C(
		long accountGroupId, long classNameId, long classPK,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByA_C_C.fetch(
			finderCache, new Object[] {accountGroupId, classNameId, classPK},
			useFinderCache);
	}

	/**
	 * Removes the account group rel where accountGroupId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param accountGroupId the account group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the account group rel that was removed
	 */
	@Override
	public AccountGroupRel removeByA_C_C(
			long accountGroupId, long classNameId, long classPK)
		throws NoSuchGroupRelException {

		AccountGroupRel accountGroupRel = findByA_C_C(
			accountGroupId, classNameId, classPK);

		return remove(accountGroupRel);
	}

	/**
	 * Returns the number of account group rels where accountGroupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param accountGroupId the account group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching account group rels
	 */
	@Override
	public int countByA_C_C(
		long accountGroupId, long classNameId, long classPK) {

		return _uniquePersistenceFinderByA_C_C.count(
			finderCache, new Object[] {accountGroupId, classNameId, classPK});
	}

	public AccountGroupRelPersistenceImpl() {
		setModelClass(AccountGroupRel.class);

		setModelImplClass(AccountGroupRelImpl.class);
		setModelPKClass(long.class);

		setTable(AccountGroupRelTable.INSTANCE);
	}

	/**
	 * Creates a new account group rel with the primary key. Does not add the account group rel to the database.
	 *
	 * @param accountGroupRelId the primary key for the new account group rel
	 * @return the new account group rel
	 */
	@Override
	public AccountGroupRel create(long accountGroupRelId) {
		AccountGroupRel accountGroupRel = new AccountGroupRelImpl();

		accountGroupRel.setNew(true);
		accountGroupRel.setPrimaryKey(accountGroupRelId);

		accountGroupRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return accountGroupRel;
	}

	/**
	 * Removes the account group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param accountGroupRelId the primary key of the account group rel
	 * @return the account group rel that was removed
	 * @throws NoSuchGroupRelException if a account group rel with the primary key could not be found
	 */
	@Override
	public AccountGroupRel remove(long accountGroupRelId)
		throws NoSuchGroupRelException {

		return remove((Serializable)accountGroupRelId);
	}

	@Override
	protected AccountGroupRel removeImpl(AccountGroupRel accountGroupRel) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(accountGroupRel)) {
				accountGroupRel = (AccountGroupRel)session.get(
					AccountGroupRelImpl.class,
					accountGroupRel.getPrimaryKeyObj());
			}

			if (accountGroupRel != null) {
				session.delete(accountGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (accountGroupRel != null) {
			clearCache(accountGroupRel);
		}

		return accountGroupRel;
	}

	@Override
	public AccountGroupRel updateImpl(AccountGroupRel accountGroupRel) {
		boolean isNew = accountGroupRel.isNew();

		if (!(accountGroupRel instanceof AccountGroupRelModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(accountGroupRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					accountGroupRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in accountGroupRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AccountGroupRel implementation " +
					accountGroupRel.getClass());
		}

		AccountGroupRelModelImpl accountGroupRelModelImpl =
			(AccountGroupRelModelImpl)accountGroupRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (accountGroupRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				accountGroupRel.setCreateDate(date);
			}
			else {
				accountGroupRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!accountGroupRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				accountGroupRel.setModifiedDate(date);
			}
			else {
				accountGroupRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(accountGroupRel);
			}
			else {
				accountGroupRel = (AccountGroupRel)session.merge(
					accountGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(accountGroupRel, false);

		if (isNew) {
			accountGroupRel.setNew(false);
		}

		accountGroupRel.resetOriginalValues();

		return accountGroupRel;
	}

	/**
	 * Returns the account group rel with the primary key or throws a <code>NoSuchGroupRelException</code> if it could not be found.
	 *
	 * @param accountGroupRelId the primary key of the account group rel
	 * @return the account group rel
	 * @throws NoSuchGroupRelException if a account group rel with the primary key could not be found
	 */
	@Override
	public AccountGroupRel findByPrimaryKey(long accountGroupRelId)
		throws NoSuchGroupRelException {

		return findByPrimaryKey((Serializable)accountGroupRelId);
	}

	/**
	 * Returns the account group rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param accountGroupRelId the primary key of the account group rel
	 * @return the account group rel, or <code>null</code> if a account group rel with the primary key could not be found
	 */
	@Override
	public AccountGroupRel fetchByPrimaryKey(long accountGroupRelId) {
		return fetchByPrimaryKey((Serializable)accountGroupRelId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "accountGroupRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ACCOUNTGROUPREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return AccountGroupRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the account group rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByAccountGroupId =
			new CollectionPersistenceFinder<>(
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
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByAccountGroupId",
					new String[] {Long.class.getName()},
					new String[] {"accountGroupId"}, false),
				_SQL_SELECT_ACCOUNTGROUPREL_WHERE,
				_SQL_COUNT_ACCOUNTGROUPREL_WHERE,
				AccountGroupRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"accountGroupRel.", "accountGroupId",
					FinderColumn.Type.LONG, "=", true, true,
					AccountGroupRel::getAccountGroupId));

		_collectionPersistenceFinderByA_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByA_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"accountGroupId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByA_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"accountGroupId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByA_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"accountGroupId", "classNameId"}, false),
			_SQL_SELECT_ACCOUNTGROUPREL_WHERE, _SQL_COUNT_ACCOUNTGROUPREL_WHERE,
			AccountGroupRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"accountGroupRel.", "accountGroupId", FinderColumn.Type.LONG,
				"=", true, true, AccountGroupRel::getAccountGroupId),
			new FinderColumn<>(
				"accountGroupRel.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, AccountGroupRel::getClassNameId));

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
			_SQL_SELECT_ACCOUNTGROUPREL_WHERE, _SQL_COUNT_ACCOUNTGROUPREL_WHERE,
			AccountGroupRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"accountGroupRel.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, AccountGroupRel::getClassNameId),
			new FinderColumn<>(
				"accountGroupRel.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, AccountGroupRel::getClassPK));

		_uniquePersistenceFinderByA_C_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByA_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"accountGroupId", "classNameId", "classPK"}, 0, 0,
				false, AccountGroupRel::getAccountGroupId,
				AccountGroupRel::getClassNameId, AccountGroupRel::getClassPK),
			_SQL_SELECT_ACCOUNTGROUPREL_WHERE, "",
			new FinderColumn<>(
				"accountGroupRel.", "accountGroupId", FinderColumn.Type.LONG,
				"=", true, true, AccountGroupRel::getAccountGroupId),
			new FinderColumn<>(
				"accountGroupRel.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, AccountGroupRel::getClassNameId),
			new FinderColumn<>(
				"accountGroupRel.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, AccountGroupRel::getClassPK));

		AccountGroupRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AccountGroupRelUtil.setPersistence(null);

		entityCache.removeCache(AccountGroupRelImpl.class.getName());
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
		AccountGroupRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ACCOUNTGROUPREL =
		"SELECT accountGroupRel FROM AccountGroupRel accountGroupRel";

	private static final String _SQL_SELECT_ACCOUNTGROUPREL_WHERE =
		"SELECT accountGroupRel FROM AccountGroupRel accountGroupRel WHERE ";

	private static final String _SQL_COUNT_ACCOUNTGROUPREL_WHERE =
		"SELECT COUNT(accountGroupRel) FROM AccountGroupRel accountGroupRel WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1257751503