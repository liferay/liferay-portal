/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.impl;

import com.liferay.change.tracking.exception.NoSuchPreferencesException;
import com.liferay.change.tracking.model.CTPreferences;
import com.liferay.change.tracking.model.CTPreferencesTable;
import com.liferay.change.tracking.model.impl.CTPreferencesImpl;
import com.liferay.change.tracking.model.impl.CTPreferencesModelImpl;
import com.liferay.change.tracking.service.persistence.CTPreferencesPersistence;
import com.liferay.change.tracking.service.persistence.CTPreferencesUtil;
import com.liferay.change.tracking.service.persistence.impl.constants.CTPersistenceConstants;
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
 * The persistence implementation for the ct preferences service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = CTPreferencesPersistence.class)
public class CTPreferencesPersistenceImpl
	extends BasePersistenceImpl<CTPreferences, NoSuchPreferencesException>
	implements CTPreferencesPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CTPreferencesUtil</code> to access the ct preferences persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CTPreferencesImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CTPreferences, NoSuchPreferencesException>
			_collectionPersistenceFinderByCtCollectionId;

	/**
	 * Returns an ordered range of all the ct preferenceses where ctCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param start the lower bound of the range of ct preferenceses
	 * @param end the upper bound of the range of ct preferenceses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct preferenceses
	 */
	@Override
	public List<CTPreferences> findByCtCollectionId(
		long ctCollectionId, int start, int end,
		OrderByComparator<CTPreferences> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCtCollectionId.find(
			finderCache, new Object[] {ctCollectionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ct preferences in the ordered set where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct preferences
	 * @throws NoSuchPreferencesException if a matching ct preferences could not be found
	 */
	@Override
	public CTPreferences findByCtCollectionId_First(
			long ctCollectionId,
			OrderByComparator<CTPreferences> orderByComparator)
		throws NoSuchPreferencesException {

		return _collectionPersistenceFinderByCtCollectionId.findFirst(
			finderCache, new Object[] {ctCollectionId}, orderByComparator);
	}

	/**
	 * Returns the first ct preferences in the ordered set where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct preferences, or <code>null</code> if a matching ct preferences could not be found
	 */
	@Override
	public CTPreferences fetchByCtCollectionId_First(
		long ctCollectionId,
		OrderByComparator<CTPreferences> orderByComparator) {

		return _collectionPersistenceFinderByCtCollectionId.fetchFirst(
			finderCache, new Object[] {ctCollectionId}, orderByComparator);
	}

	/**
	 * Removes all the ct preferenceses where ctCollectionId = &#63; from the database.
	 *
	 * @param ctCollectionId the ct collection ID
	 */
	@Override
	public void removeByCtCollectionId(long ctCollectionId) {
		_collectionPersistenceFinderByCtCollectionId.remove(
			finderCache, new Object[] {ctCollectionId});
	}

	/**
	 * Returns the number of ct preferenceses where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @return the number of matching ct preferenceses
	 */
	@Override
	public int countByCtCollectionId(long ctCollectionId) {
		return _collectionPersistenceFinderByCtCollectionId.count(
			finderCache, new Object[] {ctCollectionId});
	}

	private CollectionPersistenceFinder
		<CTPreferences, NoSuchPreferencesException>
			_collectionPersistenceFinderByPreviousCtCollectionId;

	/**
	 * Returns an ordered range of all the ct preferenceses where previousCtCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param previousCtCollectionId the previous ct collection ID
	 * @param start the lower bound of the range of ct preferenceses
	 * @param end the upper bound of the range of ct preferenceses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct preferenceses
	 */
	@Override
	public List<CTPreferences> findByPreviousCtCollectionId(
		long previousCtCollectionId, int start, int end,
		OrderByComparator<CTPreferences> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPreviousCtCollectionId.find(
			finderCache, new Object[] {previousCtCollectionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ct preferences in the ordered set where previousCtCollectionId = &#63;.
	 *
	 * @param previousCtCollectionId the previous ct collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct preferences
	 * @throws NoSuchPreferencesException if a matching ct preferences could not be found
	 */
	@Override
	public CTPreferences findByPreviousCtCollectionId_First(
			long previousCtCollectionId,
			OrderByComparator<CTPreferences> orderByComparator)
		throws NoSuchPreferencesException {

		return _collectionPersistenceFinderByPreviousCtCollectionId.findFirst(
			finderCache, new Object[] {previousCtCollectionId},
			orderByComparator);
	}

	/**
	 * Returns the first ct preferences in the ordered set where previousCtCollectionId = &#63;.
	 *
	 * @param previousCtCollectionId the previous ct collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct preferences, or <code>null</code> if a matching ct preferences could not be found
	 */
	@Override
	public CTPreferences fetchByPreviousCtCollectionId_First(
		long previousCtCollectionId,
		OrderByComparator<CTPreferences> orderByComparator) {

		return _collectionPersistenceFinderByPreviousCtCollectionId.fetchFirst(
			finderCache, new Object[] {previousCtCollectionId},
			orderByComparator);
	}

	/**
	 * Removes all the ct preferenceses where previousCtCollectionId = &#63; from the database.
	 *
	 * @param previousCtCollectionId the previous ct collection ID
	 */
	@Override
	public void removeByPreviousCtCollectionId(long previousCtCollectionId) {
		_collectionPersistenceFinderByPreviousCtCollectionId.remove(
			finderCache, new Object[] {previousCtCollectionId});
	}

	/**
	 * Returns the number of ct preferenceses where previousCtCollectionId = &#63;.
	 *
	 * @param previousCtCollectionId the previous ct collection ID
	 * @return the number of matching ct preferenceses
	 */
	@Override
	public int countByPreviousCtCollectionId(long previousCtCollectionId) {
		return _collectionPersistenceFinderByPreviousCtCollectionId.count(
			finderCache, new Object[] {previousCtCollectionId});
	}

	private UniquePersistenceFinder<CTPreferences, NoSuchPreferencesException>
		_uniquePersistenceFinderByC_U;

	/**
	 * Returns the ct preferences where companyId = &#63; and userId = &#63; or throws a <code>NoSuchPreferencesException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching ct preferences
	 * @throws NoSuchPreferencesException if a matching ct preferences could not be found
	 */
	@Override
	public CTPreferences findByC_U(long companyId, long userId)
		throws NoSuchPreferencesException {

		return _uniquePersistenceFinderByC_U.find(
			finderCache, new Object[] {companyId, userId});
	}

	/**
	 * Returns the ct preferences where companyId = &#63; and userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ct preferences, or <code>null</code> if a matching ct preferences could not be found
	 */
	@Override
	public CTPreferences fetchByC_U(
		long companyId, long userId, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_U.fetch(
			finderCache, new Object[] {companyId, userId}, useFinderCache);
	}

	/**
	 * Removes the ct preferences where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the ct preferences that was removed
	 */
	@Override
	public CTPreferences removeByC_U(long companyId, long userId)
		throws NoSuchPreferencesException {

		CTPreferences ctPreferences = findByC_U(companyId, userId);

		return remove(ctPreferences);
	}

	/**
	 * Returns the number of ct preferenceses where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching ct preferenceses
	 */
	@Override
	public int countByC_U(long companyId, long userId) {
		return _uniquePersistenceFinderByC_U.count(
			finderCache, new Object[] {companyId, userId});
	}

	public CTPreferencesPersistenceImpl() {
		setModelClass(CTPreferences.class);

		setModelImplClass(CTPreferencesImpl.class);
		setModelPKClass(long.class);

		setTable(CTPreferencesTable.INSTANCE);
	}

	/**
	 * Creates a new ct preferences with the primary key. Does not add the ct preferences to the database.
	 *
	 * @param ctPreferencesId the primary key for the new ct preferences
	 * @return the new ct preferences
	 */
	@Override
	public CTPreferences create(long ctPreferencesId) {
		CTPreferences ctPreferences = new CTPreferencesImpl();

		ctPreferences.setNew(true);
		ctPreferences.setPrimaryKey(ctPreferencesId);

		ctPreferences.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ctPreferences;
	}

	/**
	 * Removes the ct preferences with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctPreferencesId the primary key of the ct preferences
	 * @return the ct preferences that was removed
	 * @throws NoSuchPreferencesException if a ct preferences with the primary key could not be found
	 */
	@Override
	public CTPreferences remove(long ctPreferencesId)
		throws NoSuchPreferencesException {

		return remove((Serializable)ctPreferencesId);
	}

	@Override
	protected CTPreferences removeImpl(CTPreferences ctPreferences) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ctPreferences)) {
				ctPreferences = (CTPreferences)session.get(
					CTPreferencesImpl.class, ctPreferences.getPrimaryKeyObj());
			}

			if (ctPreferences != null) {
				session.delete(ctPreferences);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ctPreferences != null) {
			clearCache(ctPreferences);
		}

		return ctPreferences;
	}

	@Override
	public CTPreferences updateImpl(CTPreferences ctPreferences) {
		boolean isNew = ctPreferences.isNew();

		if (!(ctPreferences instanceof CTPreferencesModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ctPreferences.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ctPreferences);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ctPreferences proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CTPreferences implementation " +
					ctPreferences.getClass());
		}

		CTPreferencesModelImpl ctPreferencesModelImpl =
			(CTPreferencesModelImpl)ctPreferences;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ctPreferences);
			}
			else {
				ctPreferences = (CTPreferences)session.merge(ctPreferences);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ctPreferences, false);

		if (isNew) {
			ctPreferences.setNew(false);
		}

		ctPreferences.resetOriginalValues();

		return ctPreferences;
	}

	/**
	 * Returns the ct preferences with the primary key or throws a <code>NoSuchPreferencesException</code> if it could not be found.
	 *
	 * @param ctPreferencesId the primary key of the ct preferences
	 * @return the ct preferences
	 * @throws NoSuchPreferencesException if a ct preferences with the primary key could not be found
	 */
	@Override
	public CTPreferences findByPrimaryKey(long ctPreferencesId)
		throws NoSuchPreferencesException {

		return findByPrimaryKey((Serializable)ctPreferencesId);
	}

	/**
	 * Returns the ct preferences with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctPreferencesId the primary key of the ct preferences
	 * @return the ct preferences, or <code>null</code> if a ct preferences with the primary key could not be found
	 */
	@Override
	public CTPreferences fetchByPrimaryKey(long ctPreferencesId) {
		return fetchByPrimaryKey((Serializable)ctPreferencesId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "ctPreferencesId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CTPREFERENCES;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CTPreferencesModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the ct preferences persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCtCollectionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCtCollectionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"ctCollectionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCtCollectionId", new String[] {Long.class.getName()},
					new String[] {"ctCollectionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCtCollectionId",
					new String[] {Long.class.getName()},
					new String[] {"ctCollectionId"}, false),
				_SQL_SELECT_CTPREFERENCES_WHERE, _SQL_COUNT_CTPREFERENCES_WHERE,
				CTPreferencesModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"ctPreferences.", "ctCollectionId", FinderColumn.Type.LONG,
					"=", true, true, CTPreferences::getCtCollectionId));

		_collectionPersistenceFinderByPreviousCtCollectionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByPreviousCtCollectionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"previousCtCollectionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByPreviousCtCollectionId",
					new String[] {Long.class.getName()},
					new String[] {"previousCtCollectionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByPreviousCtCollectionId",
					new String[] {Long.class.getName()},
					new String[] {"previousCtCollectionId"}, false),
				_SQL_SELECT_CTPREFERENCES_WHERE, _SQL_COUNT_CTPREFERENCES_WHERE,
				CTPreferencesModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"ctPreferences.", "previousCtCollectionId",
					FinderColumn.Type.LONG, "=", true, true,
					CTPreferences::getPreviousCtCollectionId));

		_uniquePersistenceFinderByC_U = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "userId"}, 0, 0, false,
				CTPreferences::getCompanyId, CTPreferences::getUserId),
			_SQL_SELECT_CTPREFERENCES_WHERE, "",
			new FinderColumn<>(
				"ctPreferences.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CTPreferences::getCompanyId),
			new FinderColumn<>(
				"ctPreferences.", "userId", FinderColumn.Type.LONG, "=", true,
				true, CTPreferences::getUserId));

		CTPreferencesUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CTPreferencesUtil.setPersistence(null);

		entityCache.removeCache(CTPreferencesImpl.class.getName());
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		CTPreferencesModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CTPREFERENCES =
		"SELECT ctPreferences FROM CTPreferences ctPreferences";

	private static final String _SQL_SELECT_CTPREFERENCES_WHERE =
		"SELECT ctPreferences FROM CTPreferences ctPreferences WHERE ";

	private static final String _SQL_COUNT_CTPREFERENCES_WHERE =
		"SELECT COUNT(ctPreferences) FROM CTPreferences ctPreferences WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1290673367