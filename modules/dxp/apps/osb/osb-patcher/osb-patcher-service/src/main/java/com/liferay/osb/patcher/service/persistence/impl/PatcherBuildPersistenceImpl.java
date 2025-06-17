/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherBuildException;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherBuildTable;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.impl.PatcherBuildImpl;
import com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherBuildPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherBuildUtil;
import com.liferay.osb.patcher.service.persistence.impl.constants.OSBPatcherPersistenceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
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
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the patcher build service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherBuildPersistence.class)
public class PatcherBuildPersistenceImpl
	extends BasePersistenceImpl<PatcherBuild>
	implements PatcherBuildPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherBuildUtil</code> to access the patcher build persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherBuildImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathFetchByK_KV;

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or throws a <code>NoSuchPatcherBuildException</code> if it could not be found.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByK_KV(String key, double keyVersion)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByK_KV(key, keyVersion);

		if (patcherBuild == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("key=");
			sb.append(key);

			sb.append(", keyVersion=");
			sb.append(keyVersion);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPatcherBuildException(sb.toString());
		}

		return patcherBuild;
	}

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByK_KV(String key, double keyVersion) {
		return fetchByK_KV(key, keyVersion, true);
	}

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByK_KV(
		String key, double keyVersion, boolean useFinderCache) {

		key = Objects.toString(key, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {key, keyVersion};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByK_KV, finderArgs, this);
		}

		if (result instanceof PatcherBuild) {
			PatcherBuild patcherBuild = (PatcherBuild)result;

			if (!Objects.equals(key, patcherBuild.getKey()) ||
				(keyVersion != patcherBuild.getKeyVersion())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_K_KV_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_K_KV_KEY_2);
			}

			sb.append(_FINDER_COLUMN_K_KV_KEYVERSION_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindKey) {
					queryPos.add(key);
				}

				queryPos.add(keyVersion);

				List<PatcherBuild> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByK_KV, finderArgs, list);
					}
				}
				else {
					PatcherBuild patcherBuild = list.get(0);

					result = patcherBuild;

					cacheResult(patcherBuild);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		if (result instanceof List<?>) {
			return null;
		}
		else {
			return (PatcherBuild)result;
		}
	}

	/**
	 * Removes the patcher build where key = &#63; and keyVersion = &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the patcher build that was removed
	 */
	@Override
	public PatcherBuild removeByK_KV(String key, double keyVersion)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = findByK_KV(key, keyVersion);

		return remove(patcherBuild);
	}

	/**
	 * Returns the number of patcher builds where key = &#63; and keyVersion = &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByK_KV(String key, double keyVersion) {
		PatcherBuild patcherBuild = fetchByK_KV(key, keyVersion);

		if (patcherBuild == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_K_KV_KEY_2 =
		"patcherBuild.key = ? AND ";

	private static final String _FINDER_COLUMN_K_KV_KEY_3 =
		"(patcherBuild.key IS NULL OR patcherBuild.key = '') AND ";

	private static final String _FINDER_COLUMN_K_KV_KEYVERSION_2 =
		"patcherBuild.keyVersion = ?";

	public PatcherBuildPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("key", "key_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(PatcherBuild.class);

		setModelImplClass(PatcherBuildImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherBuildTable.INSTANCE);
	}

	/**
	 * Caches the patcher build in the entity cache if it is enabled.
	 *
	 * @param patcherBuild the patcher build
	 */
	@Override
	public void cacheResult(PatcherBuild patcherBuild) {
		entityCache.putResult(
			PatcherBuildImpl.class, patcherBuild.getPrimaryKey(), patcherBuild);

		finderCache.putResult(
			_finderPathFetchByK_KV,
			new Object[] {patcherBuild.getKey(), patcherBuild.getKeyVersion()},
			patcherBuild);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the patcher builds in the entity cache if it is enabled.
	 *
	 * @param patcherBuilds the patcher builds
	 */
	@Override
	public void cacheResult(List<PatcherBuild> patcherBuilds) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (patcherBuilds.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PatcherBuild patcherBuild : patcherBuilds) {
			if (entityCache.getResult(
					PatcherBuildImpl.class, patcherBuild.getPrimaryKey()) ==
						null) {

				cacheResult(patcherBuild);
			}
		}
	}

	/**
	 * Clears the cache for all patcher builds.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(PatcherBuildImpl.class);

		finderCache.clearCache(PatcherBuildImpl.class);
	}

	/**
	 * Clears the cache for the patcher build.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherBuild patcherBuild) {
		entityCache.removeResult(PatcherBuildImpl.class, patcherBuild);
	}

	@Override
	public void clearCache(List<PatcherBuild> patcherBuilds) {
		for (PatcherBuild patcherBuild : patcherBuilds) {
			entityCache.removeResult(PatcherBuildImpl.class, patcherBuild);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(PatcherBuildImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(PatcherBuildImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		PatcherBuildModelImpl patcherBuildModelImpl) {

		Object[] args = new Object[] {
			patcherBuildModelImpl.getKey(),
			patcherBuildModelImpl.getKeyVersion()
		};

		finderCache.putResult(
			_finderPathFetchByK_KV, args, patcherBuildModelImpl);
	}

	/**
	 * Creates a new patcher build with the primary key. Does not add the patcher build to the database.
	 *
	 * @param patcherBuildId the primary key for the new patcher build
	 * @return the new patcher build
	 */
	@Override
	public PatcherBuild create(long patcherBuildId) {
		PatcherBuild patcherBuild = new PatcherBuildImpl();

		patcherBuild.setNew(true);
		patcherBuild.setPrimaryKey(patcherBuildId);

		patcherBuild.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherBuild;
	}

	/**
	 * Removes the patcher build with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build that was removed
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild remove(long patcherBuildId)
		throws NoSuchPatcherBuildException {

		return remove((Serializable)patcherBuildId);
	}

	/**
	 * Removes the patcher build with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher build
	 * @return the patcher build that was removed
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild remove(Serializable primaryKey)
		throws NoSuchPatcherBuildException {

		Session session = null;

		try {
			session = openSession();

			PatcherBuild patcherBuild = (PatcherBuild)session.get(
				PatcherBuildImpl.class, primaryKey);

			if (patcherBuild == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherBuildException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(patcherBuild);
		}
		catch (NoSuchPatcherBuildException noSuchEntityException) {
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
	protected PatcherBuild removeImpl(PatcherBuild patcherBuild) {
		patcherBuildToPatcherAccountTableMapper.
			deleteLeftPrimaryKeyTableMappings(patcherBuild.getPrimaryKey());

		patcherBuildToPatcherFixTableMapper.deleteLeftPrimaryKeyTableMappings(
			patcherBuild.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherBuild)) {
				patcherBuild = (PatcherBuild)session.get(
					PatcherBuildImpl.class, patcherBuild.getPrimaryKeyObj());
			}

			if (patcherBuild != null) {
				session.delete(patcherBuild);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherBuild != null) {
			clearCache(patcherBuild);
		}

		return patcherBuild;
	}

	@Override
	public PatcherBuild updateImpl(PatcherBuild patcherBuild) {
		boolean isNew = patcherBuild.isNew();

		if (!(patcherBuild instanceof PatcherBuildModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherBuild.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherBuild);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherBuild proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherBuild implementation " +
					patcherBuild.getClass());
		}

		PatcherBuildModelImpl patcherBuildModelImpl =
			(PatcherBuildModelImpl)patcherBuild;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherBuild.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherBuild.setCreateDate(date);
			}
			else {
				patcherBuild.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!patcherBuildModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherBuild.setModifiedDate(date);
			}
			else {
				patcherBuild.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherBuild);
			}
			else {
				patcherBuild = (PatcherBuild)session.merge(patcherBuild);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			PatcherBuildImpl.class, patcherBuildModelImpl, false, true);

		cacheUniqueFindersCache(patcherBuildModelImpl);

		if (isNew) {
			patcherBuild.setNew(false);
		}

		patcherBuild.resetOriginalValues();

		return patcherBuild;
	}

	/**
	 * Returns the patcher build with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher build
	 * @return the patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByPrimaryKey(primaryKey);

		if (patcherBuild == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherBuildException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return patcherBuild;
	}

	/**
	 * Returns the patcher build with the primary key or throws a <code>NoSuchPatcherBuildException</code> if it could not be found.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild findByPrimaryKey(long patcherBuildId)
		throws NoSuchPatcherBuildException {

		return findByPrimaryKey((Serializable)patcherBuildId);
	}

	/**
	 * Returns the patcher build with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build, or <code>null</code> if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild fetchByPrimaryKey(long patcherBuildId) {
		return fetchByPrimaryKey((Serializable)patcherBuildId);
	}

	/**
	 * Returns all the patcher builds.
	 *
	 * @return the patcher builds
	 */
	@Override
	public List<PatcherBuild> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of patcher builds
	 */
	@Override
	public List<PatcherBuild> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds
	 */
	@Override
	public List<PatcherBuild> findAll(
		int start, int end, OrderByComparator<PatcherBuild> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher builds
	 */
	@Override
	public List<PatcherBuild> findAll(
		int start, int end, OrderByComparator<PatcherBuild> orderByComparator,
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

		List<PatcherBuild> list = null;

		if (useFinderCache) {
			list = (List<PatcherBuild>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PATCHERBUILD);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERBUILD;

				sql = sql.concat(PatcherBuildModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PatcherBuild>)QueryUtil.list(
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
	 * Removes all the patcher builds from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (PatcherBuild patcherBuild : findAll()) {
			remove(patcherBuild);
		}
	}

	/**
	 * Returns the number of patcher builds.
	 *
	 * @return the number of patcher builds
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_PATCHERBUILD);

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
	 * Returns the primaryKeys of patcher accounts associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return long[] of the primaryKeys of patcher accounts associated with the patcher build
	 */
	@Override
	public long[] getPatcherAccountPrimaryKeys(long pk) {
		long[] pks =
			patcherBuildToPatcherAccountTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the patcher build associated with the patcher account.
	 *
	 * @param pk the primary key of the patcher account
	 * @return the patcher builds associated with the patcher account
	 */
	@Override
	public List<PatcherBuild> getPatcherAccountPatcherBuilds(long pk) {
		return getPatcherAccountPatcherBuilds(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the patcher build associated with the patcher account.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher account
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of patcher builds associated with the patcher account
	 */
	@Override
	public List<PatcherBuild> getPatcherAccountPatcherBuilds(
		long pk, int start, int end) {

		return getPatcherAccountPatcherBuilds(pk, start, end, null);
	}

	/**
	 * Returns all the patcher build associated with the patcher account.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher account
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds associated with the patcher account
	 */
	@Override
	public List<PatcherBuild> getPatcherAccountPatcherBuilds(
		long pk, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return patcherBuildToPatcherAccountTableMapper.getLeftBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher accounts associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the number of patcher accounts associated with the patcher build
	 */
	@Override
	public int getPatcherAccountsSize(long pk) {
		long[] pks =
			patcherBuildToPatcherAccountTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher account is associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 * @return <code>true</code> if the patcher account is associated with the patcher build; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherAccount(long pk, long patcherAccountPK) {
		return patcherBuildToPatcherAccountTableMapper.containsTableMapping(
			pk, patcherAccountPK);
	}

	/**
	 * Returns <code>true</code> if the patcher build has any patcher accounts associated with it.
	 *
	 * @param pk the primary key of the patcher build to check for associations with patcher accounts
	 * @return <code>true</code> if the patcher build has any patcher accounts associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherAccounts(long pk) {
		if (getPatcherAccountsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 * @return <code>true</code> if an association between the patcher build and the patcher account was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherAccount(long pk, long patcherAccountPK) {
		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			return patcherBuildToPatcherAccountTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, patcherAccountPK);
		}
		else {
			return patcherBuildToPatcherAccountTableMapper.addTableMapping(
				patcherBuild.getCompanyId(), pk, patcherAccountPK);
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccount the patcher account
	 * @return <code>true</code> if an association between the patcher build and the patcher account was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherAccount(long pk, PatcherAccount patcherAccount) {
		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			return patcherBuildToPatcherAccountTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				patcherAccount.getPrimaryKey());
		}
		else {
			return patcherBuildToPatcherAccountTableMapper.addTableMapping(
				patcherBuild.getCompanyId(), pk,
				patcherAccount.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts
	 * @return <code>true</code> if at least one association between the patcher build and the patcher accounts was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherAccounts(long pk, long[] patcherAccountPKs) {
		long companyId = 0;

		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherBuild.getCompanyId();
		}

		long[] addedKeys =
			patcherBuildToPatcherAccountTableMapper.addTableMappings(
				companyId, pk, patcherAccountPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts
	 * @return <code>true</code> if at least one association between the patcher build and the patcher accounts was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherAccounts(
		long pk, List<PatcherAccount> patcherAccounts) {

		return addPatcherAccounts(
			pk,
			ListUtil.toLongArray(
				patcherAccounts, PatcherAccount.PATCHER_ACCOUNT_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the patcher build and its patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build to clear the associated patcher accounts from
	 */
	@Override
	public void clearPatcherAccounts(long pk) {
		patcherBuildToPatcherAccountTableMapper.
			deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 */
	@Override
	public void removePatcherAccount(long pk, long patcherAccountPK) {
		patcherBuildToPatcherAccountTableMapper.deleteTableMapping(
			pk, patcherAccountPK);
	}

	/**
	 * Removes the association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccount the patcher account
	 */
	@Override
	public void removePatcherAccount(long pk, PatcherAccount patcherAccount) {
		patcherBuildToPatcherAccountTableMapper.deleteTableMapping(
			pk, patcherAccount.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts
	 */
	@Override
	public void removePatcherAccounts(long pk, long[] patcherAccountPKs) {
		patcherBuildToPatcherAccountTableMapper.deleteTableMappings(
			pk, patcherAccountPKs);
	}

	/**
	 * Removes the association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts
	 */
	@Override
	public void removePatcherAccounts(
		long pk, List<PatcherAccount> patcherAccounts) {

		removePatcherAccounts(
			pk,
			ListUtil.toLongArray(
				patcherAccounts, PatcherAccount.PATCHER_ACCOUNT_ID_ACCESSOR));
	}

	/**
	 * Sets the patcher accounts associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts to be associated with the patcher build
	 */
	@Override
	public void setPatcherAccounts(long pk, long[] patcherAccountPKs) {
		Set<Long> newPatcherAccountPKsSet = SetUtil.fromArray(
			patcherAccountPKs);
		Set<Long> oldPatcherAccountPKsSet = SetUtil.fromArray(
			patcherBuildToPatcherAccountTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removePatcherAccountPKsSet = new HashSet<Long>(
			oldPatcherAccountPKsSet);

		removePatcherAccountPKsSet.removeAll(newPatcherAccountPKsSet);

		patcherBuildToPatcherAccountTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removePatcherAccountPKsSet));

		newPatcherAccountPKsSet.removeAll(oldPatcherAccountPKsSet);

		long companyId = 0;

		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherBuild.getCompanyId();
		}

		patcherBuildToPatcherAccountTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newPatcherAccountPKsSet));
	}

	/**
	 * Sets the patcher accounts associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts to be associated with the patcher build
	 */
	@Override
	public void setPatcherAccounts(
		long pk, List<PatcherAccount> patcherAccounts) {

		try {
			long[] patcherAccountPKs = new long[patcherAccounts.size()];

			for (int i = 0; i < patcherAccounts.size(); i++) {
				PatcherAccount patcherAccount = patcherAccounts.get(i);

				patcherAccountPKs[i] = patcherAccount.getPrimaryKey();
			}

			setPatcherAccounts(pk, patcherAccountPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	/**
	 * Returns the primaryKeys of patcher fixes associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return long[] of the primaryKeys of patcher fixes associated with the patcher build
	 */
	@Override
	public long[] getPatcherFixPrimaryKeys(long pk) {
		long[] pks = patcherBuildToPatcherFixTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the patcher build associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the patcher builds associated with the patcher fix
	 */
	@Override
	public List<PatcherBuild> getPatcherFixPatcherBuilds(long pk) {
		return getPatcherFixPatcherBuilds(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the patcher build associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of patcher builds associated with the patcher fix
	 */
	@Override
	public List<PatcherBuild> getPatcherFixPatcherBuilds(
		long pk, int start, int end) {

		return getPatcherFixPatcherBuilds(pk, start, end, null);
	}

	/**
	 * Returns all the patcher build associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds associated with the patcher fix
	 */
	@Override
	public List<PatcherBuild> getPatcherFixPatcherBuilds(
		long pk, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return patcherBuildToPatcherFixTableMapper.getLeftBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher fixes associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the number of patcher fixes associated with the patcher build
	 */
	@Override
	public int getPatcherFixesSize(long pk) {
		long[] pks = patcherBuildToPatcherFixTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher fix is associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if the patcher fix is associated with the patcher build; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherFix(long pk, long patcherFixPK) {
		return patcherBuildToPatcherFixTableMapper.containsTableMapping(
			pk, patcherFixPK);
	}

	/**
	 * Returns <code>true</code> if the patcher build has any patcher fixes associated with it.
	 *
	 * @param pk the primary key of the patcher build to check for associations with patcher fixes
	 * @return <code>true</code> if the patcher build has any patcher fixes associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherFixes(long pk) {
		if (getPatcherFixesSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if an association between the patcher build and the patcher fix was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherFix(long pk, long patcherFixPK) {
		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			return patcherBuildToPatcherFixTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, patcherFixPK);
		}
		else {
			return patcherBuildToPatcherFixTableMapper.addTableMapping(
				patcherBuild.getCompanyId(), pk, patcherFixPK);
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFix the patcher fix
	 * @return <code>true</code> if an association between the patcher build and the patcher fix was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherFix(long pk, PatcherFix patcherFix) {
		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			return patcherBuildToPatcherFixTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				patcherFix.getPrimaryKey());
		}
		else {
			return patcherBuildToPatcherFixTableMapper.addTableMapping(
				patcherBuild.getCompanyId(), pk, patcherFix.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher build and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherFixes(long pk, long[] patcherFixPKs) {
		long companyId = 0;

		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherBuild.getCompanyId();
		}

		long[] addedKeys = patcherBuildToPatcherFixTableMapper.addTableMappings(
			companyId, pk, patcherFixPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixes the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher build and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherFixes(long pk, List<PatcherFix> patcherFixes) {
		return addPatcherFixes(
			pk,
			ListUtil.toLongArray(
				patcherFixes, PatcherFix.PATCHER_FIX_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the patcher build and its patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build to clear the associated patcher fixes from
	 */
	@Override
	public void clearPatcherFixes(long pk) {
		patcherBuildToPatcherFixTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 */
	@Override
	public void removePatcherFix(long pk, long patcherFixPK) {
		patcherBuildToPatcherFixTableMapper.deleteTableMapping(
			pk, patcherFixPK);
	}

	/**
	 * Removes the association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFix the patcher fix
	 */
	@Override
	public void removePatcherFix(long pk, PatcherFix patcherFix) {
		patcherBuildToPatcherFixTableMapper.deleteTableMapping(
			pk, patcherFix.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 */
	@Override
	public void removePatcherFixes(long pk, long[] patcherFixPKs) {
		patcherBuildToPatcherFixTableMapper.deleteTableMappings(
			pk, patcherFixPKs);
	}

	/**
	 * Removes the association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixes the patcher fixes
	 */
	@Override
	public void removePatcherFixes(long pk, List<PatcherFix> patcherFixes) {
		removePatcherFixes(
			pk,
			ListUtil.toLongArray(
				patcherFixes, PatcherFix.PATCHER_FIX_ID_ACCESSOR));
	}

	/**
	 * Sets the patcher fixes associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixes to be associated with the patcher build
	 */
	@Override
	public void setPatcherFixes(long pk, long[] patcherFixPKs) {
		Set<Long> newPatcherFixPKsSet = SetUtil.fromArray(patcherFixPKs);
		Set<Long> oldPatcherFixPKsSet = SetUtil.fromArray(
			patcherBuildToPatcherFixTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removePatcherFixPKsSet = new HashSet<Long>(
			oldPatcherFixPKsSet);

		removePatcherFixPKsSet.removeAll(newPatcherFixPKsSet);

		patcherBuildToPatcherFixTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removePatcherFixPKsSet));

		newPatcherFixPKsSet.removeAll(oldPatcherFixPKsSet);

		long companyId = 0;

		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherBuild.getCompanyId();
		}

		patcherBuildToPatcherFixTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newPatcherFixPKsSet));
	}

	/**
	 * Sets the patcher fixes associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixes the patcher fixes to be associated with the patcher build
	 */
	@Override
	public void setPatcherFixes(long pk, List<PatcherFix> patcherFixes) {
		try {
			long[] patcherFixPKs = new long[patcherFixes.size()];

			for (int i = 0; i < patcherFixes.size(); i++) {
				PatcherFix patcherFix = patcherFixes.get(i);

				patcherFixPKs[i] = patcherFix.getPrimaryKey();
			}

			setPatcherFixes(pk, patcherFixPKs);
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
		return "patcherBuildId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERBUILD;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherBuildModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher build persistence.
	 */
	@Activate
	public void activate() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		patcherBuildToPatcherAccountTableMapper =
			TableMapperFactory.getTableMapper(
				"OSBPatcher_PAccounts_PBuilds#patcherBuildId",
				"OSBPatcher_PAccounts_PBuilds", "companyId", "patcherBuildId",
				"patcherAccountId", this, PatcherAccount.class);

		patcherBuildToPatcherFixTableMapper = TableMapperFactory.getTableMapper(
			"OSBPatcher_PBuilds_PFixes#patcherBuildId",
			"OSBPatcher_PBuilds_PFixes", "companyId", "patcherBuildId",
			"patcherFixId", this, PatcherFix.class);

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathFetchByK_KV = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByK_KV",
			new String[] {String.class.getName(), Double.class.getName()},
			new String[] {"key_", "keyVersion"}, true);

		PatcherBuildUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherBuildUtil.setPersistence(null);

		entityCache.removeCache(PatcherBuildImpl.class.getName());

		TableMapperFactory.removeTableMapper(
			"OSBPatcher_PAccounts_PBuilds#patcherBuildId");
		TableMapperFactory.removeTableMapper(
			"OSBPatcher_PBuilds_PFixes#patcherBuildId");
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

	protected TableMapper<PatcherBuild, PatcherAccount>
		patcherBuildToPatcherAccountTableMapper;
	protected TableMapper<PatcherBuild, PatcherFix>
		patcherBuildToPatcherFixTableMapper;

	private static final String _SQL_SELECT_PATCHERBUILD =
		"SELECT patcherBuild FROM PatcherBuild patcherBuild";

	private static final String _SQL_SELECT_PATCHERBUILD_WHERE =
		"SELECT patcherBuild FROM PatcherBuild patcherBuild WHERE ";

	private static final String _SQL_COUNT_PATCHERBUILD =
		"SELECT COUNT(patcherBuild) FROM PatcherBuild patcherBuild";

	private static final String _SQL_COUNT_PATCHERBUILD_WHERE =
		"SELECT COUNT(patcherBuild) FROM PatcherBuild patcherBuild WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherBuild.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PatcherBuild exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PatcherBuild exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherBuildPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"key", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}