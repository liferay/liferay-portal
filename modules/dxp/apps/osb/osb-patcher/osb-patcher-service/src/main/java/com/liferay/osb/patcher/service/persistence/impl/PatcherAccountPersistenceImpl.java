/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherAccountException;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherAccountTable;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.impl.PatcherAccountImpl;
import com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherAccountPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherAccountUtil;
import com.liferay.osb.patcher.service.persistence.impl.constants.OSBPatcherPersistenceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the patcher account service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherAccountPersistence.class)
public class PatcherAccountPersistenceImpl
	extends BasePersistenceImpl<PatcherAccount>
	implements PatcherAccountPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherAccountUtil</code> to access the patcher account persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherAccountImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;

	public PatcherAccountPersistenceImpl() {
		setModelClass(PatcherAccount.class);

		setModelImplClass(PatcherAccountImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherAccountTable.INSTANCE);
	}

	/**
	 * Caches the patcher account in the entity cache if it is enabled.
	 *
	 * @param patcherAccount the patcher account
	 */
	@Override
	public void cacheResult(PatcherAccount patcherAccount) {
		entityCache.putResult(
			PatcherAccountImpl.class, patcherAccount.getPrimaryKey(),
			patcherAccount);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the patcher accounts in the entity cache if it is enabled.
	 *
	 * @param patcherAccounts the patcher accounts
	 */
	@Override
	public void cacheResult(List<PatcherAccount> patcherAccounts) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (patcherAccounts.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PatcherAccount patcherAccount : patcherAccounts) {
			if (entityCache.getResult(
					PatcherAccountImpl.class, patcherAccount.getPrimaryKey()) ==
						null) {

				cacheResult(patcherAccount);
			}
		}
	}

	/**
	 * Clears the cache for all patcher accounts.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(PatcherAccountImpl.class);

		finderCache.clearCache(PatcherAccountImpl.class);
	}

	/**
	 * Clears the cache for the patcher account.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherAccount patcherAccount) {
		entityCache.removeResult(PatcherAccountImpl.class, patcherAccount);
	}

	@Override
	public void clearCache(List<PatcherAccount> patcherAccounts) {
		for (PatcherAccount patcherAccount : patcherAccounts) {
			entityCache.removeResult(PatcherAccountImpl.class, patcherAccount);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(PatcherAccountImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(PatcherAccountImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new patcher account with the primary key. Does not add the patcher account to the database.
	 *
	 * @param patcherAccountId the primary key for the new patcher account
	 * @return the new patcher account
	 */
	@Override
	public PatcherAccount create(long patcherAccountId) {
		PatcherAccount patcherAccount = new PatcherAccountImpl();

		patcherAccount.setNew(true);
		patcherAccount.setPrimaryKey(patcherAccountId);

		patcherAccount.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherAccount;
	}

	/**
	 * Removes the patcher account with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherAccountId the primary key of the patcher account
	 * @return the patcher account that was removed
	 * @throws NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 */
	@Override
	public PatcherAccount remove(long patcherAccountId)
		throws NoSuchPatcherAccountException {

		return remove((Serializable)patcherAccountId);
	}

	/**
	 * Removes the patcher account with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher account
	 * @return the patcher account that was removed
	 * @throws NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 */
	@Override
	public PatcherAccount remove(Serializable primaryKey)
		throws NoSuchPatcherAccountException {

		Session session = null;

		try {
			session = openSession();

			PatcherAccount patcherAccount = (PatcherAccount)session.get(
				PatcherAccountImpl.class, primaryKey);

			if (patcherAccount == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherAccountException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(patcherAccount);
		}
		catch (NoSuchPatcherAccountException noSuchEntityException) {
			throw noSuchEntityException;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected PatcherAccount removeImpl(PatcherAccount patcherAccount) {
		patcherAccountToPatcherBuildTableMapper.
			deleteLeftPrimaryKeyTableMappings(patcherAccount.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherAccount)) {
				patcherAccount = (PatcherAccount)session.get(
					PatcherAccountImpl.class,
					patcherAccount.getPrimaryKeyObj());
			}

			if (patcherAccount != null) {
				session.delete(patcherAccount);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherAccount != null) {
			clearCache(patcherAccount);
		}

		return patcherAccount;
	}

	@Override
	public PatcherAccount updateImpl(PatcherAccount patcherAccount) {
		boolean isNew = patcherAccount.isNew();

		if (!(patcherAccount instanceof PatcherAccountModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherAccount.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherAccount);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherAccount proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherAccount implementation " +
					patcherAccount.getClass());
		}

		PatcherAccountModelImpl patcherAccountModelImpl =
			(PatcherAccountModelImpl)patcherAccount;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherAccount.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherAccount.setCreateDate(date);
			}
			else {
				patcherAccount.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!patcherAccountModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherAccount.setModifiedDate(date);
			}
			else {
				patcherAccount.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherAccount);
			}
			else {
				patcherAccount = (PatcherAccount)session.merge(patcherAccount);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			PatcherAccountImpl.class, patcherAccount, false, true);

		if (isNew) {
			patcherAccount.setNew(false);
		}

		patcherAccount.resetOriginalValues();

		return patcherAccount;
	}

	/**
	 * Returns the patcher account with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher account
	 * @return the patcher account
	 * @throws NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 */
	@Override
	public PatcherAccount findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherAccountException {

		PatcherAccount patcherAccount = fetchByPrimaryKey(primaryKey);

		if (patcherAccount == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherAccountException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return patcherAccount;
	}

	/**
	 * Returns the patcher account with the primary key or throws a <code>NoSuchPatcherAccountException</code> if it could not be found.
	 *
	 * @param patcherAccountId the primary key of the patcher account
	 * @return the patcher account
	 * @throws NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 */
	@Override
	public PatcherAccount findByPrimaryKey(long patcherAccountId)
		throws NoSuchPatcherAccountException {

		return findByPrimaryKey((Serializable)patcherAccountId);
	}

	/**
	 * Returns the patcher account with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherAccountId the primary key of the patcher account
	 * @return the patcher account, or <code>null</code> if a patcher account with the primary key could not be found
	 */
	@Override
	public PatcherAccount fetchByPrimaryKey(long patcherAccountId) {
		return fetchByPrimaryKey((Serializable)patcherAccountId);
	}

	/**
	 * Returns all the patcher accounts.
	 *
	 * @return the patcher accounts
	 */
	@Override
	public List<PatcherAccount> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher accounts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of patcher accounts
	 */
	@Override
	public List<PatcherAccount> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher accounts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher accounts
	 */
	@Override
	public List<PatcherAccount> findAll(
		int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher accounts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher accounts
	 */
	@Override
	public List<PatcherAccount> findAll(
		int start, int end, OrderByComparator<PatcherAccount> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindAll;
				finderArgs = FINDER_ARGS_EMPTY;
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindAll;
			finderArgs = new Object[] {start, end, orderByComparator};
		}

		List<PatcherAccount> list = null;

		if (useFinderCache) {
			list = (List<PatcherAccount>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PATCHERACCOUNT);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERACCOUNT;

				sql = sql.concat(PatcherAccountModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PatcherAccount>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the patcher accounts from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (PatcherAccount patcherAccount : findAll()) {
			remove(patcherAccount);
		}
	}

	/**
	 * Returns the number of patcher accounts.
	 *
	 * @return the number of patcher accounts
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_PATCHERACCOUNT);

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	/**
	 * Returns the primaryKeys of patcher builds associated with the patcher account.
	 *
	 * @param pk the primary key of the patcher account
	 * @return long[] of the primaryKeys of patcher builds associated with the patcher account
	 */
	@Override
	public long[] getPatcherBuildPrimaryKeys(long pk) {
		long[] pks =
			patcherAccountToPatcherBuildTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the patcher account associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the patcher accounts associated with the patcher build
	 */
	@Override
	public List<PatcherAccount> getPatcherBuildPatcherAccounts(long pk) {
		return getPatcherBuildPatcherAccounts(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the patcher account associated with the patcher build.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of patcher accounts associated with the patcher build
	 */
	@Override
	public List<PatcherAccount> getPatcherBuildPatcherAccounts(
		long pk, int start, int end) {

		return getPatcherBuildPatcherAccounts(pk, start, end, null);
	}

	/**
	 * Returns all the patcher account associated with the patcher build.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher accounts associated with the patcher build
	 */
	@Override
	public List<PatcherAccount> getPatcherBuildPatcherAccounts(
		long pk, int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator) {

		return patcherAccountToPatcherBuildTableMapper.getLeftBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher builds associated with the patcher account.
	 *
	 * @param pk the primary key of the patcher account
	 * @return the number of patcher builds associated with the patcher account
	 */
	@Override
	public int getPatcherBuildsSize(long pk) {
		long[] pks =
			patcherAccountToPatcherBuildTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher build is associated with the patcher account.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPK the primary key of the patcher build
	 * @return <code>true</code> if the patcher build is associated with the patcher account; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherBuild(long pk, long patcherBuildPK) {
		return patcherAccountToPatcherBuildTableMapper.containsTableMapping(
			pk, patcherBuildPK);
	}

	/**
	 * Returns <code>true</code> if the patcher account has any patcher builds associated with it.
	 *
	 * @param pk the primary key of the patcher account to check for associations with patcher builds
	 * @return <code>true</code> if the patcher account has any patcher builds associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherBuilds(long pk) {
		if (getPatcherBuildsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPK the primary key of the patcher build
	 * @return <code>true</code> if an association between the patcher account and the patcher build was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherBuild(long pk, long patcherBuildPK) {
		PatcherAccount patcherAccount = fetchByPrimaryKey(pk);

		if (patcherAccount == null) {
			return patcherAccountToPatcherBuildTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, patcherBuildPK);
		}
		else {
			return patcherAccountToPatcherBuildTableMapper.addTableMapping(
				patcherAccount.getCompanyId(), pk, patcherBuildPK);
		}
	}

	/**
	 * Adds an association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuild the patcher build
	 * @return <code>true</code> if an association between the patcher account and the patcher build was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherBuild(long pk, PatcherBuild patcherBuild) {
		PatcherAccount patcherAccount = fetchByPrimaryKey(pk);

		if (patcherAccount == null) {
			return patcherAccountToPatcherBuildTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				patcherBuild.getPrimaryKey());
		}
		else {
			return patcherAccountToPatcherBuildTableMapper.addTableMapping(
				patcherAccount.getCompanyId(), pk,
				patcherBuild.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPKs the primary keys of the patcher builds
	 * @return <code>true</code> if at least one association between the patcher account and the patcher builds was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherBuilds(long pk, long[] patcherBuildPKs) {
		long companyId = 0;

		PatcherAccount patcherAccount = fetchByPrimaryKey(pk);

		if (patcherAccount == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherAccount.getCompanyId();
		}

		long[] addedKeys =
			patcherAccountToPatcherBuildTableMapper.addTableMappings(
				companyId, pk, patcherBuildPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuilds the patcher builds
	 * @return <code>true</code> if at least one association between the patcher account and the patcher builds was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherBuilds(long pk, List<PatcherBuild> patcherBuilds) {
		return addPatcherBuilds(
			pk,
			ListUtil.toLongArray(
				patcherBuilds, PatcherBuild.PATCHER_BUILD_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the patcher account and its patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account to clear the associated patcher builds from
	 */
	@Override
	public void clearPatcherBuilds(long pk) {
		patcherAccountToPatcherBuildTableMapper.
			deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPK the primary key of the patcher build
	 */
	@Override
	public void removePatcherBuild(long pk, long patcherBuildPK) {
		patcherAccountToPatcherBuildTableMapper.deleteTableMapping(
			pk, patcherBuildPK);
	}

	/**
	 * Removes the association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuild the patcher build
	 */
	@Override
	public void removePatcherBuild(long pk, PatcherBuild patcherBuild) {
		patcherAccountToPatcherBuildTableMapper.deleteTableMapping(
			pk, patcherBuild.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPKs the primary keys of the patcher builds
	 */
	@Override
	public void removePatcherBuilds(long pk, long[] patcherBuildPKs) {
		patcherAccountToPatcherBuildTableMapper.deleteTableMappings(
			pk, patcherBuildPKs);
	}

	/**
	 * Removes the association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuilds the patcher builds
	 */
	@Override
	public void removePatcherBuilds(long pk, List<PatcherBuild> patcherBuilds) {
		removePatcherBuilds(
			pk,
			ListUtil.toLongArray(
				patcherBuilds, PatcherBuild.PATCHER_BUILD_ID_ACCESSOR));
	}

	/**
	 * Sets the patcher builds associated with the patcher account, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPKs the primary keys of the patcher builds to be associated with the patcher account
	 */
	@Override
	public void setPatcherBuilds(long pk, long[] patcherBuildPKs) {
		Set<Long> newPatcherBuildPKsSet = SetUtil.fromArray(patcherBuildPKs);
		Set<Long> oldPatcherBuildPKsSet = SetUtil.fromArray(
			patcherAccountToPatcherBuildTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removePatcherBuildPKsSet = new HashSet<Long>(
			oldPatcherBuildPKsSet);

		removePatcherBuildPKsSet.removeAll(newPatcherBuildPKsSet);

		patcherAccountToPatcherBuildTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removePatcherBuildPKsSet));

		newPatcherBuildPKsSet.removeAll(oldPatcherBuildPKsSet);

		long companyId = 0;

		PatcherAccount patcherAccount = fetchByPrimaryKey(pk);

		if (patcherAccount == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherAccount.getCompanyId();
		}

		patcherAccountToPatcherBuildTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newPatcherBuildPKsSet));
	}

	/**
	 * Sets the patcher builds associated with the patcher account, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuilds the patcher builds to be associated with the patcher account
	 */
	@Override
	public void setPatcherBuilds(long pk, List<PatcherBuild> patcherBuilds) {
		try {
			long[] patcherBuildPKs = new long[patcherBuilds.size()];

			for (int i = 0; i < patcherBuilds.size(); i++) {
				PatcherBuild patcherBuild = patcherBuilds.get(i);

				patcherBuildPKs[i] = patcherBuild.getPrimaryKey();
			}

			setPatcherBuilds(pk, patcherBuildPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "patcherAccountId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERACCOUNT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherAccountModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher account persistence.
	 */
	@Activate
	public void activate() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		patcherAccountToPatcherBuildTableMapper =
			TableMapperFactory.getTableMapper(
				"OSBPatcher_PAccounts_PBuilds#patcherAccountId",
				"OSBPatcher_PAccounts_PBuilds", "companyId", "patcherAccountId",
				"patcherBuildId", this, PatcherBuild.class);

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		PatcherAccountUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherAccountUtil.setPersistence(null);

		entityCache.removeCache(PatcherAccountImpl.class.getName());

		TableMapperFactory.removeTableMapper(
			"OSBPatcher_PAccounts_PBuilds#patcherAccountId");
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	protected TableMapper<PatcherAccount, PatcherBuild>
		patcherAccountToPatcherBuildTableMapper;

	private static final String _SQL_SELECT_PATCHERACCOUNT =
		"SELECT patcherAccount FROM PatcherAccount patcherAccount";

	private static final String _SQL_COUNT_PATCHERACCOUNT =
		"SELECT COUNT(patcherAccount) FROM PatcherAccount patcherAccount";

	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherAccount.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PatcherAccount exists with the primary key ";

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherAccountPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}