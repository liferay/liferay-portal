/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.persistence.impl;

import com.liferay.account.exception.NoSuchEntryOrganizationRelException;
import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.model.AccountEntryOrganizationRelTable;
import com.liferay.account.model.impl.AccountEntryOrganizationRelImpl;
import com.liferay.account.model.impl.AccountEntryOrganizationRelModelImpl;
import com.liferay.account.service.persistence.AccountEntryOrganizationRelPersistence;
import com.liferay.account.service.persistence.AccountEntryOrganizationRelUtil;
import com.liferay.account.service.persistence.impl.constants.AccountPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the account entry organization rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AccountEntryOrganizationRelPersistence.class)
public class AccountEntryOrganizationRelPersistenceImpl
	extends BasePersistenceImpl
		<AccountEntryOrganizationRel, NoSuchEntryOrganizationRelException>
	implements AccountEntryOrganizationRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AccountEntryOrganizationRelUtil</code> to access the account entry organization rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AccountEntryOrganizationRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AccountEntryOrganizationRel, NoSuchEntryOrganizationRelException>
			_collectionPersistenceFinderByAccountEntryId;

	/**
	 * Returns an ordered range of all the account entry organization rels where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountEntryOrganizationRelModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account entry organization rels
	 * @param end the upper bound of the range of account entry organization rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account entry organization rels
	 */
	@Override
	public List<AccountEntryOrganizationRel> findByAccountEntryId(
		long accountEntryId, int start, int end,
		OrderByComparator<AccountEntryOrganizationRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAccountEntryId.find(
			finderCache, new Object[] {accountEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first account entry organization rel in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account entry organization rel
	 * @throws NoSuchEntryOrganizationRelException if a matching account entry organization rel could not be found
	 */
	@Override
	public AccountEntryOrganizationRel findByAccountEntryId_First(
			long accountEntryId,
			OrderByComparator<AccountEntryOrganizationRel> orderByComparator)
		throws NoSuchEntryOrganizationRelException {

		return _collectionPersistenceFinderByAccountEntryId.findFirst(
			finderCache, new Object[] {accountEntryId}, orderByComparator);
	}

	/**
	 * Returns the first account entry organization rel in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account entry organization rel, or <code>null</code> if a matching account entry organization rel could not be found
	 */
	@Override
	public AccountEntryOrganizationRel fetchByAccountEntryId_First(
		long accountEntryId,
		OrderByComparator<AccountEntryOrganizationRel> orderByComparator) {

		return _collectionPersistenceFinderByAccountEntryId.fetchFirst(
			finderCache, new Object[] {accountEntryId}, orderByComparator);
	}

	/**
	 * Removes all the account entry organization rels where accountEntryId = &#63; from the database.
	 *
	 * @param accountEntryId the account entry ID
	 */
	@Override
	public void removeByAccountEntryId(long accountEntryId) {
		_collectionPersistenceFinderByAccountEntryId.remove(
			finderCache, new Object[] {accountEntryId});
	}

	/**
	 * Returns the number of account entry organization rels where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @return the number of matching account entry organization rels
	 */
	@Override
	public int countByAccountEntryId(long accountEntryId) {
		return _collectionPersistenceFinderByAccountEntryId.count(
			finderCache, new Object[] {accountEntryId});
	}

	private CollectionPersistenceFinder
		<AccountEntryOrganizationRel, NoSuchEntryOrganizationRelException>
			_collectionPersistenceFinderByOrganizationId;

	/**
	 * Returns an ordered range of all the account entry organization rels where organizationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountEntryOrganizationRelModelImpl</code>.
	 * </p>
	 *
	 * @param organizationId the organization ID
	 * @param start the lower bound of the range of account entry organization rels
	 * @param end the upper bound of the range of account entry organization rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account entry organization rels
	 */
	@Override
	public List<AccountEntryOrganizationRel> findByOrganizationId(
		long organizationId, int start, int end,
		OrderByComparator<AccountEntryOrganizationRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByOrganizationId.find(
			finderCache, new Object[] {organizationId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first account entry organization rel in the ordered set where organizationId = &#63;.
	 *
	 * @param organizationId the organization ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account entry organization rel
	 * @throws NoSuchEntryOrganizationRelException if a matching account entry organization rel could not be found
	 */
	@Override
	public AccountEntryOrganizationRel findByOrganizationId_First(
			long organizationId,
			OrderByComparator<AccountEntryOrganizationRel> orderByComparator)
		throws NoSuchEntryOrganizationRelException {

		return _collectionPersistenceFinderByOrganizationId.findFirst(
			finderCache, new Object[] {organizationId}, orderByComparator);
	}

	/**
	 * Returns the first account entry organization rel in the ordered set where organizationId = &#63;.
	 *
	 * @param organizationId the organization ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account entry organization rel, or <code>null</code> if a matching account entry organization rel could not be found
	 */
	@Override
	public AccountEntryOrganizationRel fetchByOrganizationId_First(
		long organizationId,
		OrderByComparator<AccountEntryOrganizationRel> orderByComparator) {

		return _collectionPersistenceFinderByOrganizationId.fetchFirst(
			finderCache, new Object[] {organizationId}, orderByComparator);
	}

	/**
	 * Removes all the account entry organization rels where organizationId = &#63; from the database.
	 *
	 * @param organizationId the organization ID
	 */
	@Override
	public void removeByOrganizationId(long organizationId) {
		_collectionPersistenceFinderByOrganizationId.remove(
			finderCache, new Object[] {organizationId});
	}

	/**
	 * Returns the number of account entry organization rels where organizationId = &#63;.
	 *
	 * @param organizationId the organization ID
	 * @return the number of matching account entry organization rels
	 */
	@Override
	public int countByOrganizationId(long organizationId) {
		return _collectionPersistenceFinderByOrganizationId.count(
			finderCache, new Object[] {organizationId});
	}

	private UniquePersistenceFinder
		<AccountEntryOrganizationRel, NoSuchEntryOrganizationRelException>
			_uniquePersistenceFinderByA_O;

	/**
	 * Returns the account entry organization rel where accountEntryId = &#63; and organizationId = &#63; or throws a <code>NoSuchEntryOrganizationRelException</code> if it could not be found.
	 *
	 * @param accountEntryId the account entry ID
	 * @param organizationId the organization ID
	 * @return the matching account entry organization rel
	 * @throws NoSuchEntryOrganizationRelException if a matching account entry organization rel could not be found
	 */
	@Override
	public AccountEntryOrganizationRel findByA_O(
			long accountEntryId, long organizationId)
		throws NoSuchEntryOrganizationRelException {

		return _uniquePersistenceFinderByA_O.find(
			finderCache, new Object[] {accountEntryId, organizationId});
	}

	/**
	 * Returns the account entry organization rel where accountEntryId = &#63; and organizationId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param accountEntryId the account entry ID
	 * @param organizationId the organization ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching account entry organization rel, or <code>null</code> if a matching account entry organization rel could not be found
	 */
	@Override
	public AccountEntryOrganizationRel fetchByA_O(
		long accountEntryId, long organizationId, boolean useFinderCache) {

		return _uniquePersistenceFinderByA_O.fetch(
			finderCache, new Object[] {accountEntryId, organizationId},
			useFinderCache);
	}

	/**
	 * Removes the account entry organization rel where accountEntryId = &#63; and organizationId = &#63; from the database.
	 *
	 * @param accountEntryId the account entry ID
	 * @param organizationId the organization ID
	 * @return the account entry organization rel that was removed
	 */
	@Override
	public AccountEntryOrganizationRel removeByA_O(
			long accountEntryId, long organizationId)
		throws NoSuchEntryOrganizationRelException {

		AccountEntryOrganizationRel accountEntryOrganizationRel = findByA_O(
			accountEntryId, organizationId);

		return remove(accountEntryOrganizationRel);
	}

	/**
	 * Returns the number of account entry organization rels where accountEntryId = &#63; and organizationId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param organizationId the organization ID
	 * @return the number of matching account entry organization rels
	 */
	@Override
	public int countByA_O(long accountEntryId, long organizationId) {
		return _uniquePersistenceFinderByA_O.count(
			finderCache, new Object[] {accountEntryId, organizationId});
	}

	public AccountEntryOrganizationRelPersistenceImpl() {
		setModelClass(AccountEntryOrganizationRel.class);

		setModelImplClass(AccountEntryOrganizationRelImpl.class);
		setModelPKClass(long.class);

		setTable(AccountEntryOrganizationRelTable.INSTANCE);
	}

	/**
	 * Creates a new account entry organization rel with the primary key. Does not add the account entry organization rel to the database.
	 *
	 * @param accountEntryOrganizationRelId the primary key for the new account entry organization rel
	 * @return the new account entry organization rel
	 */
	@Override
	public AccountEntryOrganizationRel create(
		long accountEntryOrganizationRelId) {

		AccountEntryOrganizationRel accountEntryOrganizationRel =
			new AccountEntryOrganizationRelImpl();

		accountEntryOrganizationRel.setNew(true);
		accountEntryOrganizationRel.setPrimaryKey(
			accountEntryOrganizationRelId);

		accountEntryOrganizationRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return accountEntryOrganizationRel;
	}

	/**
	 * Removes the account entry organization rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param accountEntryOrganizationRelId the primary key of the account entry organization rel
	 * @return the account entry organization rel that was removed
	 * @throws NoSuchEntryOrganizationRelException if a account entry organization rel with the primary key could not be found
	 */
	@Override
	public AccountEntryOrganizationRel remove(
			long accountEntryOrganizationRelId)
		throws NoSuchEntryOrganizationRelException {

		return remove((Serializable)accountEntryOrganizationRelId);
	}

	@Override
	protected AccountEntryOrganizationRel removeImpl(
		AccountEntryOrganizationRel accountEntryOrganizationRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(accountEntryOrganizationRel)) {
				accountEntryOrganizationRel =
					(AccountEntryOrganizationRel)session.get(
						AccountEntryOrganizationRelImpl.class,
						accountEntryOrganizationRel.getPrimaryKeyObj());
			}

			if (accountEntryOrganizationRel != null) {
				session.delete(accountEntryOrganizationRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (accountEntryOrganizationRel != null) {
			clearCache(accountEntryOrganizationRel);
		}

		return accountEntryOrganizationRel;
	}

	@Override
	public AccountEntryOrganizationRel updateImpl(
		AccountEntryOrganizationRel accountEntryOrganizationRel) {

		boolean isNew = accountEntryOrganizationRel.isNew();

		if (!(accountEntryOrganizationRel instanceof
				AccountEntryOrganizationRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					accountEntryOrganizationRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					accountEntryOrganizationRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in accountEntryOrganizationRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AccountEntryOrganizationRel implementation " +
					accountEntryOrganizationRel.getClass());
		}

		AccountEntryOrganizationRelModelImpl
			accountEntryOrganizationRelModelImpl =
				(AccountEntryOrganizationRelModelImpl)
					accountEntryOrganizationRel;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(accountEntryOrganizationRel);
			}
			else {
				accountEntryOrganizationRel =
					(AccountEntryOrganizationRel)session.merge(
						accountEntryOrganizationRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(accountEntryOrganizationRel, false);

		if (isNew) {
			accountEntryOrganizationRel.setNew(false);
		}

		accountEntryOrganizationRel.resetOriginalValues();

		return accountEntryOrganizationRel;
	}

	/**
	 * Returns the account entry organization rel with the primary key or throws a <code>NoSuchEntryOrganizationRelException</code> if it could not be found.
	 *
	 * @param accountEntryOrganizationRelId the primary key of the account entry organization rel
	 * @return the account entry organization rel
	 * @throws NoSuchEntryOrganizationRelException if a account entry organization rel with the primary key could not be found
	 */
	@Override
	public AccountEntryOrganizationRel findByPrimaryKey(
			long accountEntryOrganizationRelId)
		throws NoSuchEntryOrganizationRelException {

		return findByPrimaryKey((Serializable)accountEntryOrganizationRelId);
	}

	/**
	 * Returns the account entry organization rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param accountEntryOrganizationRelId the primary key of the account entry organization rel
	 * @return the account entry organization rel, or <code>null</code> if a account entry organization rel with the primary key could not be found
	 */
	@Override
	public AccountEntryOrganizationRel fetchByPrimaryKey(
		long accountEntryOrganizationRelId) {

		return fetchByPrimaryKey((Serializable)accountEntryOrganizationRelId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "accountEntryOrganizationRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ACCOUNTENTRYORGANIZATIONREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return AccountEntryOrganizationRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the account entry organization rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByAccountEntryId =
			new CollectionPersistenceFinder<>(
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
				_SQL_SELECT_ACCOUNTENTRYORGANIZATIONREL_WHERE,
				_SQL_COUNT_ACCOUNTENTRYORGANIZATIONREL_WHERE,
				AccountEntryOrganizationRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"accountEntryOrganizationRel.", "accountEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					AccountEntryOrganizationRel::getAccountEntryId));

		_collectionPersistenceFinderByOrganizationId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByOrganizationId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"organizationId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByOrganizationId", new String[] {Long.class.getName()},
					new String[] {"organizationId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByOrganizationId",
					new String[] {Long.class.getName()},
					new String[] {"organizationId"}, false),
				_SQL_SELECT_ACCOUNTENTRYORGANIZATIONREL_WHERE,
				_SQL_COUNT_ACCOUNTENTRYORGANIZATIONREL_WHERE,
				AccountEntryOrganizationRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"accountEntryOrganizationRel.", "organizationId",
					FinderColumn.Type.LONG, "=", true, true,
					AccountEntryOrganizationRel::getOrganizationId));

		_uniquePersistenceFinderByA_O = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByA_O",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"accountEntryId", "organizationId"}, 0, 0, false,
				AccountEntryOrganizationRel::getAccountEntryId,
				AccountEntryOrganizationRel::getOrganizationId),
			_SQL_SELECT_ACCOUNTENTRYORGANIZATIONREL_WHERE, "",
			new FinderColumn<>(
				"accountEntryOrganizationRel.", "accountEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				AccountEntryOrganizationRel::getAccountEntryId),
			new FinderColumn<>(
				"accountEntryOrganizationRel.", "organizationId",
				FinderColumn.Type.LONG, "=", true, true,
				AccountEntryOrganizationRel::getOrganizationId));

		AccountEntryOrganizationRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AccountEntryOrganizationRelUtil.setPersistence(null);

		entityCache.removeCache(
			AccountEntryOrganizationRelImpl.class.getName());
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
		AccountEntryOrganizationRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ACCOUNTENTRYORGANIZATIONREL =
		"SELECT accountEntryOrganizationRel FROM AccountEntryOrganizationRel accountEntryOrganizationRel";

	private static final String _SQL_SELECT_ACCOUNTENTRYORGANIZATIONREL_WHERE =
		"SELECT accountEntryOrganizationRel FROM AccountEntryOrganizationRel accountEntryOrganizationRel WHERE ";

	private static final String _SQL_COUNT_ACCOUNTENTRYORGANIZATIONREL_WHERE =
		"SELECT COUNT(accountEntryOrganizationRel) FROM AccountEntryOrganizationRel accountEntryOrganizationRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No AccountEntryOrganizationRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		AccountEntryOrganizationRelPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-824652065