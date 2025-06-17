/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherFixException;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.PatcherFixTable;
import com.liferay.osb.patcher.model.impl.PatcherFixImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherFixPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherFixUtil;
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
import java.util.HashMap;
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
 * The persistence implementation for the patcher fix service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherFixPersistence.class)
public class PatcherFixPersistenceImpl
	extends BasePersistenceImpl<PatcherFix> implements PatcherFixPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherFixUtil</code> to access the patcher fix persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherFixImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;

	public PatcherFixPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("key", "key_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(PatcherFix.class);

		setModelImplClass(PatcherFixImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherFixTable.INSTANCE);
	}

	/**
	 * Caches the patcher fix in the entity cache if it is enabled.
	 *
	 * @param patcherFix the patcher fix
	 */
	@Override
	public void cacheResult(PatcherFix patcherFix) {
		entityCache.putResult(
			PatcherFixImpl.class, patcherFix.getPrimaryKey(), patcherFix);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the patcher fixes in the entity cache if it is enabled.
	 *
	 * @param patcherFixes the patcher fixes
	 */
	@Override
	public void cacheResult(List<PatcherFix> patcherFixes) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (patcherFixes.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PatcherFix patcherFix : patcherFixes) {
			if (entityCache.getResult(
					PatcherFixImpl.class, patcherFix.getPrimaryKey()) == null) {

				cacheResult(patcherFix);
			}
		}
	}

	/**
	 * Clears the cache for all patcher fixes.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(PatcherFixImpl.class);

		finderCache.clearCache(PatcherFixImpl.class);
	}

	/**
	 * Clears the cache for the patcher fix.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherFix patcherFix) {
		entityCache.removeResult(PatcherFixImpl.class, patcherFix);
	}

	@Override
	public void clearCache(List<PatcherFix> patcherFixes) {
		for (PatcherFix patcherFix : patcherFixes) {
			entityCache.removeResult(PatcherFixImpl.class, patcherFix);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(PatcherFixImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(PatcherFixImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new patcher fix with the primary key. Does not add the patcher fix to the database.
	 *
	 * @param patcherFixId the primary key for the new patcher fix
	 * @return the new patcher fix
	 */
	@Override
	public PatcherFix create(long patcherFixId) {
		PatcherFix patcherFix = new PatcherFixImpl();

		patcherFix.setNew(true);
		patcherFix.setPrimaryKey(patcherFixId);

		patcherFix.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherFix;
	}

	/**
	 * Removes the patcher fix with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix that was removed
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix remove(long patcherFixId)
		throws NoSuchPatcherFixException {

		return remove((Serializable)patcherFixId);
	}

	/**
	 * Removes the patcher fix with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher fix
	 * @return the patcher fix that was removed
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix remove(Serializable primaryKey)
		throws NoSuchPatcherFixException {

		Session session = null;

		try {
			session = openSession();

			PatcherFix patcherFix = (PatcherFix)session.get(
				PatcherFixImpl.class, primaryKey);

			if (patcherFix == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherFixException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(patcherFix);
		}
		catch (NoSuchPatcherFixException noSuchEntityException) {
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
	protected PatcherFix removeImpl(PatcherFix patcherFix) {
		patcherFixToPatcherBuildTableMapper.deleteLeftPrimaryKeyTableMappings(
			patcherFix.getPrimaryKey());

		patcherFixToPatcherFixPackTableMapper.deleteLeftPrimaryKeyTableMappings(
			patcherFix.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherFix)) {
				patcherFix = (PatcherFix)session.get(
					PatcherFixImpl.class, patcherFix.getPrimaryKeyObj());
			}

			if (patcherFix != null) {
				session.delete(patcherFix);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherFix != null) {
			clearCache(patcherFix);
		}

		return patcherFix;
	}

	@Override
	public PatcherFix updateImpl(PatcherFix patcherFix) {
		boolean isNew = patcherFix.isNew();

		if (!(patcherFix instanceof PatcherFixModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherFix.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(patcherFix);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherFix proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherFix implementation " +
					patcherFix.getClass());
		}

		PatcherFixModelImpl patcherFixModelImpl =
			(PatcherFixModelImpl)patcherFix;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherFix.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherFix.setCreateDate(date);
			}
			else {
				patcherFix.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!patcherFixModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherFix.setModifiedDate(date);
			}
			else {
				patcherFix.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherFix);
			}
			else {
				patcherFix = (PatcherFix)session.merge(patcherFix);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(PatcherFixImpl.class, patcherFix, false, true);

		if (isNew) {
			patcherFix.setNew(false);
		}

		patcherFix.resetOriginalValues();

		return patcherFix;
	}

	/**
	 * Returns the patcher fix with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher fix
	 * @return the patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByPrimaryKey(primaryKey);

		if (patcherFix == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherFixException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return patcherFix;
	}

	/**
	 * Returns the patcher fix with the primary key or throws a <code>NoSuchPatcherFixException</code> if it could not be found.
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix findByPrimaryKey(long patcherFixId)
		throws NoSuchPatcherFixException {

		return findByPrimaryKey((Serializable)patcherFixId);
	}

	/**
	 * Returns the patcher fix with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix, or <code>null</code> if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix fetchByPrimaryKey(long patcherFixId) {
		return fetchByPrimaryKey((Serializable)patcherFixId);
	}

	/**
	 * Returns all the patcher fixes.
	 *
	 * @return the patcher fixes
	 */
	@Override
	public List<PatcherFix> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of patcher fixes
	 */
	@Override
	public List<PatcherFix> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fixes
	 */
	@Override
	public List<PatcherFix> findAll(
		int start, int end, OrderByComparator<PatcherFix> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher fixes
	 */
	@Override
	public List<PatcherFix> findAll(
		int start, int end, OrderByComparator<PatcherFix> orderByComparator,
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

		List<PatcherFix> list = null;

		if (useFinderCache) {
			list = (List<PatcherFix>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PATCHERFIX);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERFIX;

				sql = sql.concat(PatcherFixModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PatcherFix>)QueryUtil.list(
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
	 * Removes all the patcher fixes from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (PatcherFix patcherFix : findAll()) {
			remove(patcherFix);
		}
	}

	/**
	 * Returns the number of patcher fixes.
	 *
	 * @return the number of patcher fixes
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_PATCHERFIX);

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
	 * Returns the primaryKeys of patcher builds associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return long[] of the primaryKeys of patcher builds associated with the patcher fix
	 */
	@Override
	public long[] getPatcherBuildPrimaryKeys(long pk) {
		long[] pks = patcherFixToPatcherBuildTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the patcher fix associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the patcher fixes associated with the patcher build
	 */
	@Override
	public List<PatcherFix> getPatcherBuildPatcherFixes(long pk) {
		return getPatcherBuildPatcherFixes(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the patcher fix associated with the patcher build.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of patcher fixes associated with the patcher build
	 */
	@Override
	public List<PatcherFix> getPatcherBuildPatcherFixes(
		long pk, int start, int end) {

		return getPatcherBuildPatcherFixes(pk, start, end, null);
	}

	/**
	 * Returns all the patcher fix associated with the patcher build.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fixes associated with the patcher build
	 */
	@Override
	public List<PatcherFix> getPatcherBuildPatcherFixes(
		long pk, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return patcherFixToPatcherBuildTableMapper.getLeftBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher builds associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the number of patcher builds associated with the patcher fix
	 */
	@Override
	public int getPatcherBuildsSize(long pk) {
		long[] pks = patcherFixToPatcherBuildTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher build is associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPK the primary key of the patcher build
	 * @return <code>true</code> if the patcher build is associated with the patcher fix; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherBuild(long pk, long patcherBuildPK) {
		return patcherFixToPatcherBuildTableMapper.containsTableMapping(
			pk, patcherBuildPK);
	}

	/**
	 * Returns <code>true</code> if the patcher fix has any patcher builds associated with it.
	 *
	 * @param pk the primary key of the patcher fix to check for associations with patcher builds
	 * @return <code>true</code> if the patcher fix has any patcher builds associated with it; <code>false</code> otherwise
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
	 * Adds an association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPK the primary key of the patcher build
	 * @return <code>true</code> if an association between the patcher fix and the patcher build was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherBuild(long pk, long patcherBuildPK) {
		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			return patcherFixToPatcherBuildTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, patcherBuildPK);
		}
		else {
			return patcherFixToPatcherBuildTableMapper.addTableMapping(
				patcherFix.getCompanyId(), pk, patcherBuildPK);
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuild the patcher build
	 * @return <code>true</code> if an association between the patcher fix and the patcher build was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherBuild(long pk, PatcherBuild patcherBuild) {
		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			return patcherFixToPatcherBuildTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				patcherBuild.getPrimaryKey());
		}
		else {
			return patcherFixToPatcherBuildTableMapper.addTableMapping(
				patcherFix.getCompanyId(), pk, patcherBuild.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPKs the primary keys of the patcher builds
	 * @return <code>true</code> if at least one association between the patcher fix and the patcher builds was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherBuilds(long pk, long[] patcherBuildPKs) {
		long companyId = 0;

		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherFix.getCompanyId();
		}

		long[] addedKeys = patcherFixToPatcherBuildTableMapper.addTableMappings(
			companyId, pk, patcherBuildPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuilds the patcher builds
	 * @return <code>true</code> if at least one association between the patcher fix and the patcher builds was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherBuilds(long pk, List<PatcherBuild> patcherBuilds) {
		return addPatcherBuilds(
			pk,
			ListUtil.toLongArray(
				patcherBuilds, PatcherBuild.PATCHER_BUILD_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the patcher fix and its patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix to clear the associated patcher builds from
	 */
	@Override
	public void clearPatcherBuilds(long pk) {
		patcherFixToPatcherBuildTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPK the primary key of the patcher build
	 */
	@Override
	public void removePatcherBuild(long pk, long patcherBuildPK) {
		patcherFixToPatcherBuildTableMapper.deleteTableMapping(
			pk, patcherBuildPK);
	}

	/**
	 * Removes the association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuild the patcher build
	 */
	@Override
	public void removePatcherBuild(long pk, PatcherBuild patcherBuild) {
		patcherFixToPatcherBuildTableMapper.deleteTableMapping(
			pk, patcherBuild.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPKs the primary keys of the patcher builds
	 */
	@Override
	public void removePatcherBuilds(long pk, long[] patcherBuildPKs) {
		patcherFixToPatcherBuildTableMapper.deleteTableMappings(
			pk, patcherBuildPKs);
	}

	/**
	 * Removes the association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
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
	 * Sets the patcher builds associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPKs the primary keys of the patcher builds to be associated with the patcher fix
	 */
	@Override
	public void setPatcherBuilds(long pk, long[] patcherBuildPKs) {
		Set<Long> newPatcherBuildPKsSet = SetUtil.fromArray(patcherBuildPKs);
		Set<Long> oldPatcherBuildPKsSet = SetUtil.fromArray(
			patcherFixToPatcherBuildTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removePatcherBuildPKsSet = new HashSet<Long>(
			oldPatcherBuildPKsSet);

		removePatcherBuildPKsSet.removeAll(newPatcherBuildPKsSet);

		patcherFixToPatcherBuildTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removePatcherBuildPKsSet));

		newPatcherBuildPKsSet.removeAll(oldPatcherBuildPKsSet);

		long companyId = 0;

		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherFix.getCompanyId();
		}

		patcherFixToPatcherBuildTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newPatcherBuildPKsSet));
	}

	/**
	 * Sets the patcher builds associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuilds the patcher builds to be associated with the patcher fix
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

	/**
	 * Returns the primaryKeys of patcher fix packs associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return long[] of the primaryKeys of patcher fix packs associated with the patcher fix
	 */
	@Override
	public long[] getPatcherFixPackPrimaryKeys(long pk) {
		long[] pks = patcherFixToPatcherFixPackTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the patcher fix associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @return the patcher fixes associated with the patcher fix pack
	 */
	@Override
	public List<PatcherFix> getPatcherFixPackPatcherFixes(long pk) {
		return getPatcherFixPackPatcherFixes(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the patcher fix associated with the patcher fix pack.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of patcher fixes associated with the patcher fix pack
	 */
	@Override
	public List<PatcherFix> getPatcherFixPackPatcherFixes(
		long pk, int start, int end) {

		return getPatcherFixPackPatcherFixes(pk, start, end, null);
	}

	/**
	 * Returns all the patcher fix associated with the patcher fix pack.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fixes associated with the patcher fix pack
	 */
	@Override
	public List<PatcherFix> getPatcherFixPackPatcherFixes(
		long pk, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return patcherFixToPatcherFixPackTableMapper.getLeftBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher fix packs associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the number of patcher fix packs associated with the patcher fix
	 */
	@Override
	public int getPatcherFixPacksSize(long pk) {
		long[] pks = patcherFixToPatcherFixPackTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher fix pack is associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPK the primary key of the patcher fix pack
	 * @return <code>true</code> if the patcher fix pack is associated with the patcher fix; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherFixPack(long pk, long patcherFixPackPK) {
		return patcherFixToPatcherFixPackTableMapper.containsTableMapping(
			pk, patcherFixPackPK);
	}

	/**
	 * Returns <code>true</code> if the patcher fix has any patcher fix packs associated with it.
	 *
	 * @param pk the primary key of the patcher fix to check for associations with patcher fix packs
	 * @return <code>true</code> if the patcher fix has any patcher fix packs associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherFixPacks(long pk) {
		if (getPatcherFixPacksSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPK the primary key of the patcher fix pack
	 * @return <code>true</code> if an association between the patcher fix and the patcher fix pack was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherFixPack(long pk, long patcherFixPackPK) {
		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			return patcherFixToPatcherFixPackTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, patcherFixPackPK);
		}
		else {
			return patcherFixToPatcherFixPackTableMapper.addTableMapping(
				patcherFix.getCompanyId(), pk, patcherFixPackPK);
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPack the patcher fix pack
	 * @return <code>true</code> if an association between the patcher fix and the patcher fix pack was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherFixPack(long pk, PatcherFixPack patcherFixPack) {
		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			return patcherFixToPatcherFixPackTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				patcherFixPack.getPrimaryKey());
		}
		else {
			return patcherFixToPatcherFixPackTableMapper.addTableMapping(
				patcherFix.getCompanyId(), pk, patcherFixPack.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPKs the primary keys of the patcher fix packs
	 * @return <code>true</code> if at least one association between the patcher fix and the patcher fix packs was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherFixPacks(long pk, long[] patcherFixPackPKs) {
		long companyId = 0;

		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherFix.getCompanyId();
		}

		long[] addedKeys =
			patcherFixToPatcherFixPackTableMapper.addTableMappings(
				companyId, pk, patcherFixPackPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPacks the patcher fix packs
	 * @return <code>true</code> if at least one association between the patcher fix and the patcher fix packs was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherFixPacks(
		long pk, List<PatcherFixPack> patcherFixPacks) {

		return addPatcherFixPacks(
			pk,
			ListUtil.toLongArray(
				patcherFixPacks, PatcherFixPack.PATCHER_FIX_PACK_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the patcher fix and its patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix to clear the associated patcher fix packs from
	 */
	@Override
	public void clearPatcherFixPacks(long pk) {
		patcherFixToPatcherFixPackTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPK the primary key of the patcher fix pack
	 */
	@Override
	public void removePatcherFixPack(long pk, long patcherFixPackPK) {
		patcherFixToPatcherFixPackTableMapper.deleteTableMapping(
			pk, patcherFixPackPK);
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPack the patcher fix pack
	 */
	@Override
	public void removePatcherFixPack(long pk, PatcherFixPack patcherFixPack) {
		patcherFixToPatcherFixPackTableMapper.deleteTableMapping(
			pk, patcherFixPack.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPKs the primary keys of the patcher fix packs
	 */
	@Override
	public void removePatcherFixPacks(long pk, long[] patcherFixPackPKs) {
		patcherFixToPatcherFixPackTableMapper.deleteTableMappings(
			pk, patcherFixPackPKs);
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPacks the patcher fix packs
	 */
	@Override
	public void removePatcherFixPacks(
		long pk, List<PatcherFixPack> patcherFixPacks) {

		removePatcherFixPacks(
			pk,
			ListUtil.toLongArray(
				patcherFixPacks, PatcherFixPack.PATCHER_FIX_PACK_ID_ACCESSOR));
	}

	/**
	 * Sets the patcher fix packs associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPKs the primary keys of the patcher fix packs to be associated with the patcher fix
	 */
	@Override
	public void setPatcherFixPacks(long pk, long[] patcherFixPackPKs) {
		Set<Long> newPatcherFixPackPKsSet = SetUtil.fromArray(
			patcherFixPackPKs);
		Set<Long> oldPatcherFixPackPKsSet = SetUtil.fromArray(
			patcherFixToPatcherFixPackTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removePatcherFixPackPKsSet = new HashSet<Long>(
			oldPatcherFixPackPKsSet);

		removePatcherFixPackPKsSet.removeAll(newPatcherFixPackPKsSet);

		patcherFixToPatcherFixPackTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removePatcherFixPackPKsSet));

		newPatcherFixPackPKsSet.removeAll(oldPatcherFixPackPKsSet);

		long companyId = 0;

		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherFix.getCompanyId();
		}

		patcherFixToPatcherFixPackTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newPatcherFixPackPKsSet));
	}

	/**
	 * Sets the patcher fix packs associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPacks the patcher fix packs to be associated with the patcher fix
	 */
	@Override
	public void setPatcherFixPacks(
		long pk, List<PatcherFixPack> patcherFixPacks) {

		try {
			long[] patcherFixPackPKs = new long[patcherFixPacks.size()];

			for (int i = 0; i < patcherFixPacks.size(); i++) {
				PatcherFixPack patcherFixPack = patcherFixPacks.get(i);

				patcherFixPackPKs[i] = patcherFixPack.getPrimaryKey();
			}

			setPatcherFixPacks(pk, patcherFixPackPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
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
		return "patcherFixId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERFIX;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherFixModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher fix persistence.
	 */
	@Activate
	public void activate() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		patcherFixToPatcherBuildTableMapper = TableMapperFactory.getTableMapper(
			"OSBPatcher_PBuilds_PFixes#patcherFixId",
			"OSBPatcher_PBuilds_PFixes", "companyId", "patcherFixId",
			"patcherBuildId", this, PatcherBuild.class);

		patcherFixToPatcherFixPackTableMapper =
			TableMapperFactory.getTableMapper(
				"OSBPatcher_PFixes_PFixPacks#patcherFixId",
				"OSBPatcher_PFixes_PFixPacks", "companyId", "patcherFixId",
				"patcherFixPackId", this, PatcherFixPack.class);

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		PatcherFixUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherFixUtil.setPersistence(null);

		entityCache.removeCache(PatcherFixImpl.class.getName());

		TableMapperFactory.removeTableMapper(
			"OSBPatcher_PBuilds_PFixes#patcherFixId");
		TableMapperFactory.removeTableMapper(
			"OSBPatcher_PFixes_PFixPacks#patcherFixId");
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

	protected TableMapper<PatcherFix, PatcherBuild>
		patcherFixToPatcherBuildTableMapper;
	protected TableMapper<PatcherFix, PatcherFixPack>
		patcherFixToPatcherFixPackTableMapper;

	private static final String _SQL_SELECT_PATCHERFIX =
		"SELECT patcherFix FROM PatcherFix patcherFix";

	private static final String _SQL_COUNT_PATCHERFIX =
		"SELECT COUNT(patcherFix) FROM PatcherFix patcherFix";

	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherFix.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PatcherFix exists with the primary key ";

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherFixPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"key", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}