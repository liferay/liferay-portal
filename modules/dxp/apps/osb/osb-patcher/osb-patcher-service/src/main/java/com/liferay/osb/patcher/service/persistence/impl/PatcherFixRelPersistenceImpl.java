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
	extends BasePersistenceImpl<PatcherFixRel, NoSuchPatcherFixRelException>
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

	private CollectionPersistenceFinder
		<PatcherFixRel, NoSuchPatcherFixRelException>
			_collectionPersistenceFinderByChildPatcherFixId;

	/**
	 * Returns an ordered range of all the patcher fix rels where childPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix rels
	 */
	@Override
	public List<PatcherFixRel> findByChildPatcherFixId(
		long childPatcherFixId, int start, int end,
		OrderByComparator<PatcherFixRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByChildPatcherFixId.find(
			finderCache, new Object[] {childPatcherFixId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix rel in the ordered set where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a matching patcher fix rel could not be found
	 */
	@Override
	public PatcherFixRel findByChildPatcherFixId_First(
			long childPatcherFixId,
			OrderByComparator<PatcherFixRel> orderByComparator)
		throws NoSuchPatcherFixRelException {

		return _collectionPersistenceFinderByChildPatcherFixId.findFirst(
			finderCache, new Object[] {childPatcherFixId}, orderByComparator);
	}

	/**
	 * Returns the first patcher fix rel in the ordered set where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix rel, or <code>null</code> if a matching patcher fix rel could not be found
	 */
	@Override
	public PatcherFixRel fetchByChildPatcherFixId_First(
		long childPatcherFixId,
		OrderByComparator<PatcherFixRel> orderByComparator) {

		return _collectionPersistenceFinderByChildPatcherFixId.fetchFirst(
			finderCache, new Object[] {childPatcherFixId}, orderByComparator);
	}

	/**
	 * Removes all the patcher fix rels where childPatcherFixId = &#63; from the database.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 */
	@Override
	public void removeByChildPatcherFixId(long childPatcherFixId) {
		_collectionPersistenceFinderByChildPatcherFixId.remove(
			finderCache, new Object[] {childPatcherFixId});
	}

	/**
	 * Returns the number of patcher fix rels where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @return the number of matching patcher fix rels
	 */
	@Override
	public int countByChildPatcherFixId(long childPatcherFixId) {
		return _collectionPersistenceFinderByChildPatcherFixId.count(
			finderCache, new Object[] {childPatcherFixId});
	}

	private CollectionPersistenceFinder
		<PatcherFixRel, NoSuchPatcherFixRelException>
			_collectionPersistenceFinderByParentPatcherFixId;

	/**
	 * Returns an ordered range of all the patcher fix rels where parentPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix rels
	 */
	@Override
	public List<PatcherFixRel> findByParentPatcherFixId(
		long parentPatcherFixId, int start, int end,
		OrderByComparator<PatcherFixRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByParentPatcherFixId.find(
			finderCache, new Object[] {parentPatcherFixId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix rel in the ordered set where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a matching patcher fix rel could not be found
	 */
	@Override
	public PatcherFixRel findByParentPatcherFixId_First(
			long parentPatcherFixId,
			OrderByComparator<PatcherFixRel> orderByComparator)
		throws NoSuchPatcherFixRelException {

		return _collectionPersistenceFinderByParentPatcherFixId.findFirst(
			finderCache, new Object[] {parentPatcherFixId}, orderByComparator);
	}

	/**
	 * Returns the first patcher fix rel in the ordered set where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix rel, or <code>null</code> if a matching patcher fix rel could not be found
	 */
	@Override
	public PatcherFixRel fetchByParentPatcherFixId_First(
		long parentPatcherFixId,
		OrderByComparator<PatcherFixRel> orderByComparator) {

		return _collectionPersistenceFinderByParentPatcherFixId.fetchFirst(
			finderCache, new Object[] {parentPatcherFixId}, orderByComparator);
	}

	/**
	 * Removes all the patcher fix rels where parentPatcherFixId = &#63; from the database.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 */
	@Override
	public void removeByParentPatcherFixId(long parentPatcherFixId) {
		_collectionPersistenceFinderByParentPatcherFixId.remove(
			finderCache, new Object[] {parentPatcherFixId});
	}

	/**
	 * Returns the number of patcher fix rels where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @return the number of matching patcher fix rels
	 */
	@Override
	public int countByParentPatcherFixId(long parentPatcherFixId) {
		return _collectionPersistenceFinderByParentPatcherFixId.count(
			finderCache, new Object[] {parentPatcherFixId});
	}

	public PatcherFixRelPersistenceImpl() {
		setModelClass(PatcherFixRel.class);

		setModelImplClass(PatcherFixRelImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherFixRelTable.INSTANCE);
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

		if (!(patcherFixRel instanceof PatcherFixRelModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherFixRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherFixRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherFixRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherFixRel implementation " +
					patcherFixRel.getClass());
		}

		PatcherFixRelModelImpl patcherFixRelModelImpl =
			(PatcherFixRelModelImpl)patcherFixRel;

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

		cacheUniqueFindersResult(patcherFixRel, false);

		if (isNew) {
			patcherFixRel.setNew(false);
		}

		patcherFixRel.resetOriginalValues();

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
		_collectionPersistenceFinderByChildPatcherFixId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByChildPatcherFixId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"childPatcherFixId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByChildPatcherFixId",
					new String[] {Long.class.getName()},
					new String[] {"childPatcherFixId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByChildPatcherFixId",
					new String[] {Long.class.getName()},
					new String[] {"childPatcherFixId"}, false),
				_SQL_SELECT_PATCHERFIXREL_WHERE, _SQL_COUNT_PATCHERFIXREL_WHERE,
				PatcherFixRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"patcherFixRel.", "childPatcherFixId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherFixRel::getChildPatcherFixId));

		_collectionPersistenceFinderByParentPatcherFixId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByParentPatcherFixId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"parentPatcherFixId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByParentPatcherFixId",
					new String[] {Long.class.getName()},
					new String[] {"parentPatcherFixId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByParentPatcherFixId",
					new String[] {Long.class.getName()},
					new String[] {"parentPatcherFixId"}, false),
				_SQL_SELECT_PATCHERFIXREL_WHERE, _SQL_COUNT_PATCHERFIXREL_WHERE,
				PatcherFixRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"patcherFixRel.", "parentPatcherFixId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherFixRel::getParentPatcherFixId));

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

	private static final String _ENTITY_ALIAS_PREFIX =
		PatcherFixRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PATCHERFIXREL =
		"SELECT patcherFixRel FROM PatcherFixRel patcherFixRel";

	private static final String _SQL_SELECT_PATCHERFIXREL_WHERE =
		"SELECT patcherFixRel FROM PatcherFixRel patcherFixRel WHERE ";

	private static final String _SQL_COUNT_PATCHERFIXREL_WHERE =
		"SELECT COUNT(patcherFixRel) FROM PatcherFixRel patcherFixRel WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1976556813