/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.view.count.service.persistence.impl;

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
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.view.count.exception.NoSuchEntryException;
import com.liferay.view.count.model.ViewCountEntry;
import com.liferay.view.count.model.ViewCountEntryTable;
import com.liferay.view.count.model.impl.ViewCountEntryImpl;
import com.liferay.view.count.model.impl.ViewCountEntryModelImpl;
import com.liferay.view.count.service.persistence.ViewCountEntryPK;
import com.liferay.view.count.service.persistence.ViewCountEntryPersistence;
import com.liferay.view.count.service.persistence.ViewCountEntryUtil;
import com.liferay.view.count.service.persistence.impl.constants.ViewCountPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the view count entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Preston Crary
 * @generated
 */
@Component(service = ViewCountEntryPersistence.class)
public class ViewCountEntryPersistenceImpl
	extends BasePersistenceImpl<ViewCountEntry, NoSuchEntryException>
	implements ViewCountEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ViewCountEntryUtil</code> to access the view count entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ViewCountEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<ViewCountEntry, NoSuchEntryException>
		_collectionPersistenceFinderByC_CN;

	/**
	 * Returns an ordered range of all the view count entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ViewCountEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of view count entries
	 * @param end the upper bound of the range of view count entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching view count entries
	 */
	@Override
	public List<ViewCountEntry> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<ViewCountEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CN.find(
			finderCache, new Object[] {companyId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first view count entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching view count entry
	 * @throws NoSuchEntryException if a matching view count entry could not be found
	 */
	@Override
	public ViewCountEntry findByC_CN_First(
			long companyId, long classNameId,
			OrderByComparator<ViewCountEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_CN.findFirst(
			finderCache, new Object[] {companyId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first view count entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching view count entry, or <code>null</code> if a matching view count entry could not be found
	 */
	@Override
	public ViewCountEntry fetchByC_CN_First(
		long companyId, long classNameId,
		OrderByComparator<ViewCountEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_CN.fetchFirst(
			finderCache, new Object[] {companyId, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the view count entries where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_CN(long companyId, long classNameId) {
		_collectionPersistenceFinderByC_CN.remove(
			finderCache, new Object[] {companyId, classNameId});
	}

	/**
	 * Returns the number of view count entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching view count entries
	 */
	@Override
	public int countByC_CN(long companyId, long classNameId) {
		return _collectionPersistenceFinderByC_CN.count(
			finderCache, new Object[] {companyId, classNameId});
	}

	public ViewCountEntryPersistenceImpl() {
		setModelClass(ViewCountEntry.class);

		setModelImplClass(ViewCountEntryImpl.class);
		setModelPKClass(ViewCountEntryPK.class);

		setTable(ViewCountEntryTable.INSTANCE);
	}

	/**
	 * Creates a new view count entry with the primary key. Does not add the view count entry to the database.
	 *
	 * @param viewCountEntryPK the primary key for the new view count entry
	 * @return the new view count entry
	 */
	@Override
	public ViewCountEntry create(ViewCountEntryPK viewCountEntryPK) {
		ViewCountEntry viewCountEntry = new ViewCountEntryImpl();

		viewCountEntry.setNew(true);
		viewCountEntry.setPrimaryKey(viewCountEntryPK);

		viewCountEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return viewCountEntry;
	}

	/**
	 * Removes the view count entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param viewCountEntryPK the primary key of the view count entry
	 * @return the view count entry that was removed
	 * @throws NoSuchEntryException if a view count entry with the primary key could not be found
	 */
	@Override
	public ViewCountEntry remove(ViewCountEntryPK viewCountEntryPK)
		throws NoSuchEntryException {

		return remove((Serializable)viewCountEntryPK);
	}

	@Override
	protected ViewCountEntry removeImpl(ViewCountEntry viewCountEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(viewCountEntry)) {
				viewCountEntry = (ViewCountEntry)session.get(
					ViewCountEntryImpl.class,
					viewCountEntry.getPrimaryKeyObj());
			}

			if (viewCountEntry != null) {
				session.delete(viewCountEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (viewCountEntry != null) {
			clearCache(viewCountEntry);
		}

		return viewCountEntry;
	}

	@Override
	public ViewCountEntry updateImpl(ViewCountEntry viewCountEntry) {
		boolean isNew = viewCountEntry.isNew();

		if (!(viewCountEntry instanceof ViewCountEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(viewCountEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					viewCountEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in viewCountEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ViewCountEntry implementation " +
					viewCountEntry.getClass());
		}

		ViewCountEntryModelImpl viewCountEntryModelImpl =
			(ViewCountEntryModelImpl)viewCountEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(viewCountEntry);
			}
			else {
				viewCountEntry = (ViewCountEntry)session.merge(viewCountEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(viewCountEntry, false);

		if (isNew) {
			viewCountEntry.setNew(false);
		}

		viewCountEntry.resetOriginalValues();

		return viewCountEntry;
	}

	/**
	 * Returns the view count entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param viewCountEntryPK the primary key of the view count entry
	 * @return the view count entry
	 * @throws NoSuchEntryException if a view count entry with the primary key could not be found
	 */
	@Override
	public ViewCountEntry findByPrimaryKey(ViewCountEntryPK viewCountEntryPK)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)viewCountEntryPK);
	}

	/**
	 * Returns the view count entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param viewCountEntryPK the primary key of the view count entry
	 * @return the view count entry, or <code>null</code> if a view count entry with the primary key could not be found
	 */
	@Override
	public ViewCountEntry fetchByPrimaryKey(ViewCountEntryPK viewCountEntryPK) {
		return fetchByPrimaryKey((Serializable)viewCountEntryPK);
	}

	@Override
	public Set<String> getCompoundPKColumnNames() {
		return _compoundPKColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "viewCountEntryPK";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_VIEWCOUNTENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ViewCountEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the view count entry persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByC_CN = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CN",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CN",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_CN",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, false),
			_SQL_SELECT_VIEWCOUNTENTRY_WHERE, _SQL_COUNT_VIEWCOUNTENTRY_WHERE,
			ViewCountEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"viewCountEntry.", "id.companyId", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				ViewCountEntry::getCompanyId),
			new FinderColumn<>(
				"viewCountEntry.", "id.classNameId", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				ViewCountEntry::getClassNameId));

		ViewCountEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ViewCountEntryUtil.setPersistence(null);

		entityCache.removeCache(ViewCountEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = ViewCountPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = ViewCountPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = ViewCountPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		ViewCountEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_VIEWCOUNTENTRY =
		"SELECT viewCountEntry FROM ViewCountEntry viewCountEntry";

	private static final String _SQL_SELECT_VIEWCOUNTENTRY_WHERE =
		"SELECT viewCountEntry FROM ViewCountEntry viewCountEntry WHERE ";

	private static final String _SQL_COUNT_VIEWCOUNTENTRY_WHERE =
		"SELECT COUNT(viewCountEntry) FROM ViewCountEntry viewCountEntry WHERE ";

	private static final Set<String> _compoundPKColumnNames = SetUtil.fromArray(
		new String[] {"companyId", "classNameId", "classPK"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1511552192