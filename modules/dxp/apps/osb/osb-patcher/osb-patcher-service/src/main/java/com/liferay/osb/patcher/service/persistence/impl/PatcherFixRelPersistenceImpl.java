/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherFixRelException;
import com.liferay.osb.patcher.model.PatcherFixRel;
import com.liferay.osb.patcher.model.PatcherFixRelTable;
import com.liferay.osb.patcher.model.impl.PatcherFixRelImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixRelModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherFixRelPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherFixRelUtil;
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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the patcher fix rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherFixRelPersistence.class)
public class PatcherFixRelPersistenceImpl
	extends BasePersistenceImpl<PatcherFixRel>
	implements PatcherFixRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherFixRelUtil</code> to access the patcher fix rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherFixRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;

	public PatcherFixRelPersistenceImpl() {
		setModelClass(PatcherFixRel.class);

		setModelImplClass(PatcherFixRelImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherFixRelTable.INSTANCE);
	}

	/**
	 * Caches the patcher fix rel in the entity cache if it is enabled.
	 *
	 * @param patcherFixRel the patcher fix rel
	 */
	@Override
	public void cacheResult(PatcherFixRel patcherFixRel) {
		entityCache.putResult(
			PatcherFixRelImpl.class, patcherFixRel.getPrimaryKey(),
			patcherFixRel);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the patcher fix rels in the entity cache if it is enabled.
	 *
	 * @param patcherFixRels the patcher fix rels
	 */
	@Override
	public void cacheResult(List<PatcherFixRel> patcherFixRels) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (patcherFixRels.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PatcherFixRel patcherFixRel : patcherFixRels) {
			if (entityCache.getResult(
					PatcherFixRelImpl.class, patcherFixRel.getPrimaryKey()) ==
						null) {

				cacheResult(patcherFixRel);
			}
		}
	}

	/**
	 * Clears the cache for all patcher fix rels.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(PatcherFixRelImpl.class);

		finderCache.clearCache(PatcherFixRelImpl.class);
	}

	/**
	 * Clears the cache for the patcher fix rel.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherFixRel patcherFixRel) {
		entityCache.removeResult(PatcherFixRelImpl.class, patcherFixRel);
	}

	@Override
	public void clearCache(List<PatcherFixRel> patcherFixRels) {
		for (PatcherFixRel patcherFixRel : patcherFixRels) {
			entityCache.removeResult(PatcherFixRelImpl.class, patcherFixRel);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(PatcherFixRelImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(PatcherFixRelImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new patcher fix rel with the primary key. Does not add the patcher fix rel to the database.
	 *
	 * @param patcherFixRelId the primary key for the new patcher fix rel
	 * @return the new patcher fix rel
	 */
	@Override
	public PatcherFixRel create(long patcherFixRelId) {
		PatcherFixRel patcherFixRel = new PatcherFixRelImpl();

		patcherFixRel.setNew(true);
		patcherFixRel.setPrimaryKey(patcherFixRelId);

		patcherFixRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherFixRel;
	}

	/**
	 * Removes the patcher fix rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixRelId the primary key of the patcher fix rel
	 * @return the patcher fix rel that was removed
	 * @throws NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 */
	@Override
	public PatcherFixRel remove(long patcherFixRelId)
		throws NoSuchPatcherFixRelException {

		return remove((Serializable)patcherFixRelId);
	}

	/**
	 * Removes the patcher fix rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher fix rel
	 * @return the patcher fix rel that was removed
	 * @throws NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 */
	@Override
	public PatcherFixRel remove(Serializable primaryKey)
		throws NoSuchPatcherFixRelException {

		Session session = null;

		try {
			session = openSession();

			PatcherFixRel patcherFixRel = (PatcherFixRel)session.get(
				PatcherFixRelImpl.class, primaryKey);

			if (patcherFixRel == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherFixRelException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(patcherFixRel);
		}
		catch (NoSuchPatcherFixRelException noSuchEntityException) {
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
	protected PatcherFixRel removeImpl(PatcherFixRel patcherFixRel) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherFixRel)) {
				patcherFixRel = (PatcherFixRel)session.get(
					PatcherFixRelImpl.class, patcherFixRel.getPrimaryKeyObj());
			}

			if (patcherFixRel != null) {
				session.delete(patcherFixRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherFixRel != null) {
			clearCache(patcherFixRel);
		}

		return patcherFixRel;
	}

	@Override
	public PatcherFixRel updateImpl(PatcherFixRel patcherFixRel) {
		boolean isNew = patcherFixRel.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherFixRel);
			}
			else {
				patcherFixRel = (PatcherFixRel)session.merge(patcherFixRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			PatcherFixRelImpl.class, patcherFixRel, false, true);

		if (isNew) {
			patcherFixRel.setNew(false);
		}

		patcherFixRel.resetOriginalValues();

		return patcherFixRel;
	}

	/**
	 * Returns the patcher fix rel with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher fix rel
	 * @return the patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 */
	@Override
	public PatcherFixRel findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherFixRelException {

		PatcherFixRel patcherFixRel = fetchByPrimaryKey(primaryKey);

		if (patcherFixRel == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherFixRelException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return patcherFixRel;
	}

	/**
	 * Returns the patcher fix rel with the primary key or throws a <code>NoSuchPatcherFixRelException</code> if it could not be found.
	 *
	 * @param patcherFixRelId the primary key of the patcher fix rel
	 * @return the patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 */
	@Override
	public PatcherFixRel findByPrimaryKey(long patcherFixRelId)
		throws NoSuchPatcherFixRelException {

		return findByPrimaryKey((Serializable)patcherFixRelId);
	}

	/**
	 * Returns the patcher fix rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixRelId the primary key of the patcher fix rel
	 * @return the patcher fix rel, or <code>null</code> if a patcher fix rel with the primary key could not be found
	 */
	@Override
	public PatcherFixRel fetchByPrimaryKey(long patcherFixRelId) {
		return fetchByPrimaryKey((Serializable)patcherFixRelId);
	}

	/**
	 * Returns all the patcher fix rels.
	 *
	 * @return the patcher fix rels
	 */
	@Override
	public List<PatcherFixRel> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @return the range of patcher fix rels
	 */
	@Override
	public List<PatcherFixRel> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix rels
	 */
	@Override
	public List<PatcherFixRel> findAll(
		int start, int end,
		OrderByComparator<PatcherFixRel> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fix rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher fix rels
	 */
	@Override
	public List<PatcherFixRel> findAll(
		int start, int end, OrderByComparator<PatcherFixRel> orderByComparator,
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

		List<PatcherFixRel> list = null;

		if (useFinderCache) {
			list = (List<PatcherFixRel>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PATCHERFIXREL);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERFIXREL;

				sql = sql.concat(PatcherFixRelModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PatcherFixRel>)QueryUtil.list(
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
	 * Removes all the patcher fix rels from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (PatcherFixRel patcherFixRel : findAll()) {
			remove(patcherFixRel);
		}
	}

	/**
	 * Returns the number of patcher fix rels.
	 *
	 * @return the number of patcher fix rels
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_PATCHERFIXREL);

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

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "patcherFixRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERFIXREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherFixRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher fix rel persistence.
	 */
	@Activate
	public void activate() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		PatcherFixRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherFixRelUtil.setPersistence(null);

		entityCache.removeCache(PatcherFixRelImpl.class.getName());
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

	private static final String _SQL_SELECT_PATCHERFIXREL =
		"SELECT patcherFixRel FROM PatcherFixRel patcherFixRel";

	private static final String _SQL_COUNT_PATCHERFIXREL =
		"SELECT COUNT(patcherFixRel) FROM PatcherFixRel patcherFixRel";

	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherFixRel.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PatcherFixRel exists with the primary key ";

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherFixRelPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}