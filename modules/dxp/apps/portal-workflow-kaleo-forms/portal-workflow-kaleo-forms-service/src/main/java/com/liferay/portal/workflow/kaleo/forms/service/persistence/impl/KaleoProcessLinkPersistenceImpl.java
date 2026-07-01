/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.service.persistence.impl;

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
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.workflow.kaleo.forms.exception.NoSuchKaleoProcessLinkException;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLinkTable;
import com.liferay.portal.workflow.kaleo.forms.model.impl.KaleoProcessLinkImpl;
import com.liferay.portal.workflow.kaleo.forms.model.impl.KaleoProcessLinkModelImpl;
import com.liferay.portal.workflow.kaleo.forms.service.persistence.KaleoProcessLinkPersistence;
import com.liferay.portal.workflow.kaleo.forms.service.persistence.KaleoProcessLinkUtil;
import com.liferay.portal.workflow.kaleo.forms.service.persistence.impl.constants.KaleoFormsPersistenceConstants;

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
 * The persistence implementation for the kaleo process link service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marcellus Tavares
 * @generated
 */
@Component(service = KaleoProcessLinkPersistence.class)
public class KaleoProcessLinkPersistenceImpl
	extends BasePersistenceImpl
		<KaleoProcessLink, NoSuchKaleoProcessLinkException>
	implements KaleoProcessLinkPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoProcessLinkUtil</code> to access the kaleo process link persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoProcessLinkImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<KaleoProcessLink, NoSuchKaleoProcessLinkException>
			_collectionPersistenceFinderByKaleoProcessId;

	/**
	 * Returns an ordered range of all the kaleo process links where kaleoProcessId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoProcessLinkModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoProcessId the kaleo process ID
	 * @param start the lower bound of the range of kaleo process links
	 * @param end the upper bound of the range of kaleo process links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo process links
	 */
	@Override
	public List<KaleoProcessLink> findByKaleoProcessId(
		long kaleoProcessId, int start, int end,
		OrderByComparator<KaleoProcessLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoProcessId.find(
			finderCache, new Object[] {kaleoProcessId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo process link in the ordered set where kaleoProcessId = &#63;.
	 *
	 * @param kaleoProcessId the kaleo process ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo process link
	 * @throws NoSuchKaleoProcessLinkException if a matching kaleo process link could not be found
	 */
	@Override
	public KaleoProcessLink findByKaleoProcessId_First(
			long kaleoProcessId,
			OrderByComparator<KaleoProcessLink> orderByComparator)
		throws NoSuchKaleoProcessLinkException {

		return _collectionPersistenceFinderByKaleoProcessId.findFirst(
			finderCache, new Object[] {kaleoProcessId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo process link in the ordered set where kaleoProcessId = &#63;.
	 *
	 * @param kaleoProcessId the kaleo process ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo process link, or <code>null</code> if a matching kaleo process link could not be found
	 */
	@Override
	public KaleoProcessLink fetchByKaleoProcessId_First(
		long kaleoProcessId,
		OrderByComparator<KaleoProcessLink> orderByComparator) {

		return _collectionPersistenceFinderByKaleoProcessId.fetchFirst(
			finderCache, new Object[] {kaleoProcessId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo process links where kaleoProcessId = &#63; from the database.
	 *
	 * @param kaleoProcessId the kaleo process ID
	 */
	@Override
	public void removeByKaleoProcessId(long kaleoProcessId) {
		_collectionPersistenceFinderByKaleoProcessId.remove(
			finderCache, new Object[] {kaleoProcessId});
	}

	/**
	 * Returns the number of kaleo process links where kaleoProcessId = &#63;.
	 *
	 * @param kaleoProcessId the kaleo process ID
	 * @return the number of matching kaleo process links
	 */
	@Override
	public int countByKaleoProcessId(long kaleoProcessId) {
		return _collectionPersistenceFinderByKaleoProcessId.count(
			finderCache, new Object[] {kaleoProcessId});
	}

	private UniquePersistenceFinder
		<KaleoProcessLink, NoSuchKaleoProcessLinkException>
			_uniquePersistenceFinderByKPI_WTN;

	/**
	 * Returns the kaleo process link where kaleoProcessId = &#63; and workflowTaskName = &#63; or throws a <code>NoSuchKaleoProcessLinkException</code> if it could not be found.
	 *
	 * @param kaleoProcessId the kaleo process ID
	 * @param workflowTaskName the workflow task name
	 * @return the matching kaleo process link
	 * @throws NoSuchKaleoProcessLinkException if a matching kaleo process link could not be found
	 */
	@Override
	public KaleoProcessLink findByKPI_WTN(
			long kaleoProcessId, String workflowTaskName)
		throws NoSuchKaleoProcessLinkException {

		return _uniquePersistenceFinderByKPI_WTN.find(
			finderCache, new Object[] {kaleoProcessId, workflowTaskName});
	}

	/**
	 * Returns the kaleo process link where kaleoProcessId = &#63; and workflowTaskName = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param kaleoProcessId the kaleo process ID
	 * @param workflowTaskName the workflow task name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo process link, or <code>null</code> if a matching kaleo process link could not be found
	 */
	@Override
	public KaleoProcessLink fetchByKPI_WTN(
		long kaleoProcessId, String workflowTaskName, boolean useFinderCache) {

		return _uniquePersistenceFinderByKPI_WTN.fetch(
			finderCache, new Object[] {kaleoProcessId, workflowTaskName},
			useFinderCache);
	}

	/**
	 * Removes the kaleo process link where kaleoProcessId = &#63; and workflowTaskName = &#63; from the database.
	 *
	 * @param kaleoProcessId the kaleo process ID
	 * @param workflowTaskName the workflow task name
	 * @return the kaleo process link that was removed
	 */
	@Override
	public KaleoProcessLink removeByKPI_WTN(
			long kaleoProcessId, String workflowTaskName)
		throws NoSuchKaleoProcessLinkException {

		KaleoProcessLink kaleoProcessLink = findByKPI_WTN(
			kaleoProcessId, workflowTaskName);

		return remove(kaleoProcessLink);
	}

	/**
	 * Returns the number of kaleo process links where kaleoProcessId = &#63; and workflowTaskName = &#63;.
	 *
	 * @param kaleoProcessId the kaleo process ID
	 * @param workflowTaskName the workflow task name
	 * @return the number of matching kaleo process links
	 */
	@Override
	public int countByKPI_WTN(long kaleoProcessId, String workflowTaskName) {
		return _uniquePersistenceFinderByKPI_WTN.count(
			finderCache, new Object[] {kaleoProcessId, workflowTaskName});
	}

	public KaleoProcessLinkPersistenceImpl() {
		setModelClass(KaleoProcessLink.class);

		setModelImplClass(KaleoProcessLinkImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoProcessLinkTable.INSTANCE);
	}

	/**
	 * Creates a new kaleo process link with the primary key. Does not add the kaleo process link to the database.
	 *
	 * @param kaleoProcessLinkId the primary key for the new kaleo process link
	 * @return the new kaleo process link
	 */
	@Override
	public KaleoProcessLink create(long kaleoProcessLinkId) {
		KaleoProcessLink kaleoProcessLink = new KaleoProcessLinkImpl();

		kaleoProcessLink.setNew(true);
		kaleoProcessLink.setPrimaryKey(kaleoProcessLinkId);

		kaleoProcessLink.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kaleoProcessLink;
	}

	/**
	 * Removes the kaleo process link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoProcessLinkId the primary key of the kaleo process link
	 * @return the kaleo process link that was removed
	 * @throws NoSuchKaleoProcessLinkException if a kaleo process link with the primary key could not be found
	 */
	@Override
	public KaleoProcessLink remove(long kaleoProcessLinkId)
		throws NoSuchKaleoProcessLinkException {

		return remove((Serializable)kaleoProcessLinkId);
	}

	@Override
	protected KaleoProcessLink removeImpl(KaleoProcessLink kaleoProcessLink) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoProcessLink)) {
				kaleoProcessLink = (KaleoProcessLink)session.get(
					KaleoProcessLinkImpl.class,
					kaleoProcessLink.getPrimaryKeyObj());
			}

			if (kaleoProcessLink != null) {
				session.delete(kaleoProcessLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoProcessLink != null) {
			clearCache(kaleoProcessLink);
		}

		return kaleoProcessLink;
	}

	@Override
	public KaleoProcessLink updateImpl(KaleoProcessLink kaleoProcessLink) {
		boolean isNew = kaleoProcessLink.isNew();

		if (!(kaleoProcessLink instanceof KaleoProcessLinkModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kaleoProcessLink.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					kaleoProcessLink);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoProcessLink proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoProcessLink implementation " +
					kaleoProcessLink.getClass());
		}

		KaleoProcessLinkModelImpl kaleoProcessLinkModelImpl =
			(KaleoProcessLinkModelImpl)kaleoProcessLink;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(kaleoProcessLink);
			}
			else {
				kaleoProcessLink = (KaleoProcessLink)session.merge(
					kaleoProcessLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kaleoProcessLink, false);

		if (isNew) {
			kaleoProcessLink.setNew(false);
		}

		kaleoProcessLink.resetOriginalValues();

		return kaleoProcessLink;
	}

	/**
	 * Returns the kaleo process link with the primary key or throws a <code>NoSuchKaleoProcessLinkException</code> if it could not be found.
	 *
	 * @param kaleoProcessLinkId the primary key of the kaleo process link
	 * @return the kaleo process link
	 * @throws NoSuchKaleoProcessLinkException if a kaleo process link with the primary key could not be found
	 */
	@Override
	public KaleoProcessLink findByPrimaryKey(long kaleoProcessLinkId)
		throws NoSuchKaleoProcessLinkException {

		return findByPrimaryKey((Serializable)kaleoProcessLinkId);
	}

	/**
	 * Returns the kaleo process link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoProcessLinkId the primary key of the kaleo process link
	 * @return the kaleo process link, or <code>null</code> if a kaleo process link with the primary key could not be found
	 */
	@Override
	public KaleoProcessLink fetchByPrimaryKey(long kaleoProcessLinkId) {
		return fetchByPrimaryKey((Serializable)kaleoProcessLinkId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "kaleoProcessLinkId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEOPROCESSLINK;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return KaleoProcessLinkModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the kaleo process link persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByKaleoProcessId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByKaleoProcessId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoProcessId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByKaleoProcessId", new String[] {Long.class.getName()},
					new String[] {"kaleoProcessId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKaleoProcessId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoProcessId"}, false),
				_SQL_SELECT_KALEOPROCESSLINK_WHERE,
				_SQL_COUNT_KALEOPROCESSLINK_WHERE,
				KaleoProcessLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"kaleoProcessLink.", "kaleoProcessId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoProcessLink::getKaleoProcessId));

		_uniquePersistenceFinderByKPI_WTN = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByKPI_WTN",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"kaleoProcessId", "workflowTaskName"}, 0, 2,
				false, KaleoProcessLink::getKaleoProcessId,
				convertNullFunction(KaleoProcessLink::getWorkflowTaskName)),
			_SQL_SELECT_KALEOPROCESSLINK_WHERE, "",
			new FinderColumn<>(
				"kaleoProcessLink.", "kaleoProcessId", FinderColumn.Type.LONG,
				"=", true, true, KaleoProcessLink::getKaleoProcessId),
			new FinderColumn<>(
				"kaleoProcessLink.", "workflowTaskName",
				FinderColumn.Type.STRING, "=", true, true,
				KaleoProcessLink::getWorkflowTaskName));

		KaleoProcessLinkUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoProcessLinkUtil.setPersistence(null);

		entityCache.removeCache(KaleoProcessLinkImpl.class.getName());
	}

	@Override
	@Reference(
		target = KaleoFormsPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = KaleoFormsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = KaleoFormsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		KaleoProcessLinkModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KALEOPROCESSLINK =
		"SELECT kaleoProcessLink FROM KaleoProcessLink kaleoProcessLink";

	private static final String _SQL_SELECT_KALEOPROCESSLINK_WHERE =
		"SELECT kaleoProcessLink FROM KaleoProcessLink kaleoProcessLink WHERE ";

	private static final String _SQL_COUNT_KALEOPROCESSLINK_WHERE =
		"SELECT COUNT(kaleoProcessLink) FROM KaleoProcessLink kaleoProcessLink WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:190756682