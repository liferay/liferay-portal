/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherBuildRelException;
import com.liferay.osb.patcher.model.PatcherBuildRel;
import com.liferay.osb.patcher.model.PatcherBuildRelTable;
import com.liferay.osb.patcher.model.impl.PatcherBuildRelImpl;
import com.liferay.osb.patcher.model.impl.PatcherBuildRelModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherBuildRelPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherBuildRelUtil;
import com.liferay.osb.patcher.service.persistence.impl.constants.OSBPatcherPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
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
 * The persistence implementation for the patcher build rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherBuildRelPersistence.class)
public class PatcherBuildRelPersistenceImpl
	extends BasePersistenceImpl<PatcherBuildRel, NoSuchPatcherBuildRelException>
	implements PatcherBuildRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherBuildRelUtil</code> to access the patcher build rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherBuildRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<PatcherBuildRel, NoSuchPatcherBuildRelException>
			_collectionPersistenceFinderByChildPatcherBuildId;

	/**
	 * Returns an ordered range of all the patcher build rels where childPatcherBuildId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildRelModelImpl</code>.
	 * </p>
	 *
	 * @param childPatcherBuildId the child patcher build ID
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher build rels
	 */
	@Override
	public List<PatcherBuildRel> findByChildPatcherBuildId(
		long childPatcherBuildId, int start, int end,
		OrderByComparator<PatcherBuildRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByChildPatcherBuildId.find(
			finderCache, new Object[] {childPatcherBuildId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build rel in the ordered set where childPatcherBuildId = &#63;.
	 *
	 * @param childPatcherBuildId the child patcher build ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build rel
	 * @throws NoSuchPatcherBuildRelException if a matching patcher build rel could not be found
	 */
	@Override
	public PatcherBuildRel findByChildPatcherBuildId_First(
			long childPatcherBuildId,
			OrderByComparator<PatcherBuildRel> orderByComparator)
		throws NoSuchPatcherBuildRelException {

		return _collectionPersistenceFinderByChildPatcherBuildId.findFirst(
			finderCache, new Object[] {childPatcherBuildId}, orderByComparator);
	}

	/**
	 * Returns the first patcher build rel in the ordered set where childPatcherBuildId = &#63;.
	 *
	 * @param childPatcherBuildId the child patcher build ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build rel, or <code>null</code> if a matching patcher build rel could not be found
	 */
	@Override
	public PatcherBuildRel fetchByChildPatcherBuildId_First(
		long childPatcherBuildId,
		OrderByComparator<PatcherBuildRel> orderByComparator) {

		return _collectionPersistenceFinderByChildPatcherBuildId.fetchFirst(
			finderCache, new Object[] {childPatcherBuildId}, orderByComparator);
	}

	/**
	 * Removes all the patcher build rels where childPatcherBuildId = &#63; from the database.
	 *
	 * @param childPatcherBuildId the child patcher build ID
	 */
	@Override
	public void removeByChildPatcherBuildId(long childPatcherBuildId) {
		_collectionPersistenceFinderByChildPatcherBuildId.remove(
			finderCache, new Object[] {childPatcherBuildId});
	}

	/**
	 * Returns the number of patcher build rels where childPatcherBuildId = &#63;.
	 *
	 * @param childPatcherBuildId the child patcher build ID
	 * @return the number of matching patcher build rels
	 */
	@Override
	public int countByChildPatcherBuildId(long childPatcherBuildId) {
		return _collectionPersistenceFinderByChildPatcherBuildId.count(
			finderCache, new Object[] {childPatcherBuildId});
	}

	private CollectionPersistenceFinder
		<PatcherBuildRel, NoSuchPatcherBuildRelException>
			_collectionPersistenceFinderByParentPatcherBuildId;

	/**
	 * Returns an ordered range of all the patcher build rels where parentPatcherBuildId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildRelModelImpl</code>.
	 * </p>
	 *
	 * @param parentPatcherBuildId the parent patcher build ID
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher build rels
	 */
	@Override
	public List<PatcherBuildRel> findByParentPatcherBuildId(
		long parentPatcherBuildId, int start, int end,
		OrderByComparator<PatcherBuildRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByParentPatcherBuildId.find(
			finderCache, new Object[] {parentPatcherBuildId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build rel in the ordered set where parentPatcherBuildId = &#63;.
	 *
	 * @param parentPatcherBuildId the parent patcher build ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build rel
	 * @throws NoSuchPatcherBuildRelException if a matching patcher build rel could not be found
	 */
	@Override
	public PatcherBuildRel findByParentPatcherBuildId_First(
			long parentPatcherBuildId,
			OrderByComparator<PatcherBuildRel> orderByComparator)
		throws NoSuchPatcherBuildRelException {

		return _collectionPersistenceFinderByParentPatcherBuildId.findFirst(
			finderCache, new Object[] {parentPatcherBuildId},
			orderByComparator);
	}

	/**
	 * Returns the first patcher build rel in the ordered set where parentPatcherBuildId = &#63;.
	 *
	 * @param parentPatcherBuildId the parent patcher build ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build rel, or <code>null</code> if a matching patcher build rel could not be found
	 */
	@Override
	public PatcherBuildRel fetchByParentPatcherBuildId_First(
		long parentPatcherBuildId,
		OrderByComparator<PatcherBuildRel> orderByComparator) {

		return _collectionPersistenceFinderByParentPatcherBuildId.fetchFirst(
			finderCache, new Object[] {parentPatcherBuildId},
			orderByComparator);
	}

	/**
	 * Removes all the patcher build rels where parentPatcherBuildId = &#63; from the database.
	 *
	 * @param parentPatcherBuildId the parent patcher build ID
	 */
	@Override
	public void removeByParentPatcherBuildId(long parentPatcherBuildId) {
		_collectionPersistenceFinderByParentPatcherBuildId.remove(
			finderCache, new Object[] {parentPatcherBuildId});
	}

	/**
	 * Returns the number of patcher build rels where parentPatcherBuildId = &#63;.
	 *
	 * @param parentPatcherBuildId the parent patcher build ID
	 * @return the number of matching patcher build rels
	 */
	@Override
	public int countByParentPatcherBuildId(long parentPatcherBuildId) {
		return _collectionPersistenceFinderByParentPatcherBuildId.count(
			finderCache, new Object[] {parentPatcherBuildId});
	}

	public PatcherBuildRelPersistenceImpl() {
		setModelClass(PatcherBuildRel.class);

		setModelImplClass(PatcherBuildRelImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherBuildRelTable.INSTANCE);
	}

	/**
	 * Creates a new patcher build rel with the primary key. Does not add the patcher build rel to the database.
	 *
	 * @param patcherBuildRelId the primary key for the new patcher build rel
	 * @return the new patcher build rel
	 */
	@Override
	public PatcherBuildRel create(long patcherBuildRelId) {
		PatcherBuildRel patcherBuildRel = new PatcherBuildRelImpl();

		patcherBuildRel.setNew(true);
		patcherBuildRel.setPrimaryKey(patcherBuildRelId);

		patcherBuildRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherBuildRel;
	}

	/**
	 * Removes the patcher build rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherBuildRelId the primary key of the patcher build rel
	 * @return the patcher build rel that was removed
	 * @throws NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	 */
	@Override
	public PatcherBuildRel remove(long patcherBuildRelId)
		throws NoSuchPatcherBuildRelException {

		return remove((Serializable)patcherBuildRelId);
	}

	@Override
	protected PatcherBuildRel removeImpl(PatcherBuildRel patcherBuildRel) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherBuildRel)) {
				patcherBuildRel = (PatcherBuildRel)session.get(
					PatcherBuildRelImpl.class,
					patcherBuildRel.getPrimaryKeyObj());
			}

			if (patcherBuildRel != null) {
				session.delete(patcherBuildRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherBuildRel != null) {
			clearCache(patcherBuildRel);
		}

		return patcherBuildRel;
	}

	@Override
	public PatcherBuildRel updateImpl(PatcherBuildRel patcherBuildRel) {
		boolean isNew = patcherBuildRel.isNew();

		if (!(patcherBuildRel instanceof PatcherBuildRelModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherBuildRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherBuildRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherBuildRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherBuildRel implementation " +
					patcherBuildRel.getClass());
		}

		PatcherBuildRelModelImpl patcherBuildRelModelImpl =
			(PatcherBuildRelModelImpl)patcherBuildRel;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherBuildRel);
			}
			else {
				patcherBuildRel = (PatcherBuildRel)session.merge(
					patcherBuildRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(patcherBuildRel, false);

		if (isNew) {
			patcherBuildRel.setNew(false);
		}

		patcherBuildRel.resetOriginalValues();

		return patcherBuildRel;
	}

	/**
	 * Returns the patcher build rel with the primary key or throws a <code>NoSuchPatcherBuildRelException</code> if it could not be found.
	 *
	 * @param patcherBuildRelId the primary key of the patcher build rel
	 * @return the patcher build rel
	 * @throws NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	 */
	@Override
	public PatcherBuildRel findByPrimaryKey(long patcherBuildRelId)
		throws NoSuchPatcherBuildRelException {

		return findByPrimaryKey((Serializable)patcherBuildRelId);
	}

	/**
	 * Returns the patcher build rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherBuildRelId the primary key of the patcher build rel
	 * @return the patcher build rel, or <code>null</code> if a patcher build rel with the primary key could not be found
	 */
	@Override
	public PatcherBuildRel fetchByPrimaryKey(long patcherBuildRelId) {
		return fetchByPrimaryKey((Serializable)patcherBuildRelId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "patcherBuildRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERBUILDREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherBuildRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher build rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByChildPatcherBuildId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByChildPatcherBuildId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"childPatcherBuildId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByChildPatcherBuildId",
					new String[] {Long.class.getName()},
					new String[] {"childPatcherBuildId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByChildPatcherBuildId",
					new String[] {Long.class.getName()},
					new String[] {"childPatcherBuildId"}, false),
				_SQL_SELECT_PATCHERBUILDREL_WHERE,
				_SQL_COUNT_PATCHERBUILDREL_WHERE,
				PatcherBuildRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"patcherBuildRel.", "childPatcherBuildId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherBuildRel::getChildPatcherBuildId));

		_collectionPersistenceFinderByParentPatcherBuildId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByParentPatcherBuildId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"parentPatcherBuildId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByParentPatcherBuildId",
					new String[] {Long.class.getName()},
					new String[] {"parentPatcherBuildId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByParentPatcherBuildId",
					new String[] {Long.class.getName()},
					new String[] {"parentPatcherBuildId"}, false),
				_SQL_SELECT_PATCHERBUILDREL_WHERE,
				_SQL_COUNT_PATCHERBUILDREL_WHERE,
				PatcherBuildRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"patcherBuildRel.", "parentPatcherBuildId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherBuildRel::getParentPatcherBuildId));

		PatcherBuildRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherBuildRelUtil.setPersistence(null);

		entityCache.removeCache(PatcherBuildRelImpl.class.getName());
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

	private static final String _ENTITY_ALIAS_PREFIX =
		PatcherBuildRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PATCHERBUILDREL =
		"SELECT patcherBuildRel FROM PatcherBuildRel patcherBuildRel";

	private static final String _SQL_SELECT_PATCHERBUILDREL_WHERE =
		"SELECT patcherBuildRel FROM PatcherBuildRel patcherBuildRel WHERE ";

	private static final String _SQL_COUNT_PATCHERBUILDREL_WHERE =
		"SELECT COUNT(patcherBuildRel) FROM PatcherBuildRel patcherBuildRel WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1710357704