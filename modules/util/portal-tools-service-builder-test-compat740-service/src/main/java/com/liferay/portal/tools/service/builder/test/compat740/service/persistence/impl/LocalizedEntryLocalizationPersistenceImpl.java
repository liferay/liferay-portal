/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchLocalizedEntryLocalizationException;
import com.liferay.portal.tools.service.builder.test.compat740.model.LocalizedEntryLocalization;
import com.liferay.portal.tools.service.builder.test.compat740.model.LocalizedEntryLocalizationTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.LocalizedEntryLocalizationImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.LocalizedEntryLocalizationModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.LocalizedEntryLocalizationPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.LocalizedEntryLocalizationUtil;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl.constants.SBCompat740PersistenceConstants;

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
 * The persistence implementation for the localized entry localization service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = LocalizedEntryLocalizationPersistence.class)
public class LocalizedEntryLocalizationPersistenceImpl
	extends BasePersistenceImpl
		<LocalizedEntryLocalization, NoSuchLocalizedEntryLocalizationException>
	implements LocalizedEntryLocalizationPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LocalizedEntryLocalizationUtil</code> to access the localized entry localization persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LocalizedEntryLocalizationImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LocalizedEntryLocalization, NoSuchLocalizedEntryLocalizationException>
			_collectionPersistenceFinderByLocalizedEntryId;

	/**
	 * Returns an ordered range of all the localized entry localizations where localizedEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LocalizedEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param localizedEntryId the localized entry ID
	 * @param start the lower bound of the range of localized entry localizations
	 * @param end the upper bound of the range of localized entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching localized entry localizations
	 */
	@Override
	public List<LocalizedEntryLocalization> findByLocalizedEntryId(
		long localizedEntryId, int start, int end,
		OrderByComparator<LocalizedEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLocalizedEntryId.find(
			finderCache, new Object[] {localizedEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first localized entry localization in the ordered set where localizedEntryId = &#63;.
	 *
	 * @param localizedEntryId the localized entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching localized entry localization
	 * @throws NoSuchLocalizedEntryLocalizationException if a matching localized entry localization could not be found
	 */
	@Override
	public LocalizedEntryLocalization findByLocalizedEntryId_First(
			long localizedEntryId,
			OrderByComparator<LocalizedEntryLocalization> orderByComparator)
		throws NoSuchLocalizedEntryLocalizationException {

		return _collectionPersistenceFinderByLocalizedEntryId.findFirst(
			finderCache, new Object[] {localizedEntryId}, orderByComparator);
	}

	/**
	 * Returns the first localized entry localization in the ordered set where localizedEntryId = &#63;.
	 *
	 * @param localizedEntryId the localized entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching localized entry localization, or <code>null</code> if a matching localized entry localization could not be found
	 */
	@Override
	public LocalizedEntryLocalization fetchByLocalizedEntryId_First(
		long localizedEntryId,
		OrderByComparator<LocalizedEntryLocalization> orderByComparator) {

		return _collectionPersistenceFinderByLocalizedEntryId.fetchFirst(
			finderCache, new Object[] {localizedEntryId}, orderByComparator);
	}

	/**
	 * Removes all the localized entry localizations where localizedEntryId = &#63; from the database.
	 *
	 * @param localizedEntryId the localized entry ID
	 */
	@Override
	public void removeByLocalizedEntryId(long localizedEntryId) {
		_collectionPersistenceFinderByLocalizedEntryId.remove(
			finderCache, new Object[] {localizedEntryId});
	}

	/**
	 * Returns the number of localized entry localizations where localizedEntryId = &#63;.
	 *
	 * @param localizedEntryId the localized entry ID
	 * @return the number of matching localized entry localizations
	 */
	@Override
	public int countByLocalizedEntryId(long localizedEntryId) {
		return _collectionPersistenceFinderByLocalizedEntryId.count(
			finderCache, new Object[] {localizedEntryId});
	}

	private UniquePersistenceFinder
		<LocalizedEntryLocalization, NoSuchLocalizedEntryLocalizationException>
			_uniquePersistenceFinderByLocalizedEntryId_LanguageId;

	/**
	 * Returns the localized entry localization where localizedEntryId = &#63; and languageId = &#63; or throws a <code>NoSuchLocalizedEntryLocalizationException</code> if it could not be found.
	 *
	 * @param localizedEntryId the localized entry ID
	 * @param languageId the language ID
	 * @return the matching localized entry localization
	 * @throws NoSuchLocalizedEntryLocalizationException if a matching localized entry localization could not be found
	 */
	@Override
	public LocalizedEntryLocalization findByLocalizedEntryId_LanguageId(
			long localizedEntryId, String languageId)
		throws NoSuchLocalizedEntryLocalizationException {

		return _uniquePersistenceFinderByLocalizedEntryId_LanguageId.find(
			finderCache, new Object[] {localizedEntryId, languageId});
	}

	/**
	 * Returns the localized entry localization where localizedEntryId = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param localizedEntryId the localized entry ID
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching localized entry localization, or <code>null</code> if a matching localized entry localization could not be found
	 */
	@Override
	public LocalizedEntryLocalization fetchByLocalizedEntryId_LanguageId(
		long localizedEntryId, String languageId, boolean useFinderCache) {

		return _uniquePersistenceFinderByLocalizedEntryId_LanguageId.fetch(
			finderCache, new Object[] {localizedEntryId, languageId},
			useFinderCache);
	}

	/**
	 * Removes the localized entry localization where localizedEntryId = &#63; and languageId = &#63; from the database.
	 *
	 * @param localizedEntryId the localized entry ID
	 * @param languageId the language ID
	 * @return the localized entry localization that was removed
	 */
	@Override
	public LocalizedEntryLocalization removeByLocalizedEntryId_LanguageId(
			long localizedEntryId, String languageId)
		throws NoSuchLocalizedEntryLocalizationException {

		LocalizedEntryLocalization localizedEntryLocalization =
			findByLocalizedEntryId_LanguageId(localizedEntryId, languageId);

		return remove(localizedEntryLocalization);
	}

	/**
	 * Returns the number of localized entry localizations where localizedEntryId = &#63; and languageId = &#63;.
	 *
	 * @param localizedEntryId the localized entry ID
	 * @param languageId the language ID
	 * @return the number of matching localized entry localizations
	 */
	@Override
	public int countByLocalizedEntryId_LanguageId(
		long localizedEntryId, String languageId) {

		return _uniquePersistenceFinderByLocalizedEntryId_LanguageId.count(
			finderCache, new Object[] {localizedEntryId, languageId});
	}

	public LocalizedEntryLocalizationPersistenceImpl() {
		setModelClass(LocalizedEntryLocalization.class);

		setModelImplClass(LocalizedEntryLocalizationImpl.class);
		setModelPKClass(long.class);

		setTable(LocalizedEntryLocalizationTable.INSTANCE);
	}

	/**
	 * Creates a new localized entry localization with the primary key. Does not add the localized entry localization to the database.
	 *
	 * @param localizedEntryLocalizationId the primary key for the new localized entry localization
	 * @return the new localized entry localization
	 */
	@Override
	public LocalizedEntryLocalization create(
		long localizedEntryLocalizationId) {

		LocalizedEntryLocalization localizedEntryLocalization =
			new LocalizedEntryLocalizationImpl();

		localizedEntryLocalization.setNew(true);
		localizedEntryLocalization.setPrimaryKey(localizedEntryLocalizationId);

		return localizedEntryLocalization;
	}

	/**
	 * Removes the localized entry localization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param localizedEntryLocalizationId the primary key of the localized entry localization
	 * @return the localized entry localization that was removed
	 * @throws NoSuchLocalizedEntryLocalizationException if a localized entry localization with the primary key could not be found
	 */
	@Override
	public LocalizedEntryLocalization remove(long localizedEntryLocalizationId)
		throws NoSuchLocalizedEntryLocalizationException {

		return remove((Serializable)localizedEntryLocalizationId);
	}

	@Override
	protected LocalizedEntryLocalization removeImpl(
		LocalizedEntryLocalization localizedEntryLocalization) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(localizedEntryLocalization)) {
				localizedEntryLocalization =
					(LocalizedEntryLocalization)session.get(
						LocalizedEntryLocalizationImpl.class,
						localizedEntryLocalization.getPrimaryKeyObj());
			}

			if (localizedEntryLocalization != null) {
				session.delete(localizedEntryLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (localizedEntryLocalization != null) {
			clearCache(localizedEntryLocalization);
		}

		return localizedEntryLocalization;
	}

	@Override
	public LocalizedEntryLocalization updateImpl(
		LocalizedEntryLocalization localizedEntryLocalization) {

		boolean isNew = localizedEntryLocalization.isNew();

		if (!(localizedEntryLocalization instanceof
				LocalizedEntryLocalizationModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(localizedEntryLocalization.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					localizedEntryLocalization);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in localizedEntryLocalization proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LocalizedEntryLocalization implementation " +
					localizedEntryLocalization.getClass());
		}

		LocalizedEntryLocalizationModelImpl
			localizedEntryLocalizationModelImpl =
				(LocalizedEntryLocalizationModelImpl)localizedEntryLocalization;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(localizedEntryLocalization);
			}
			else {
				localizedEntryLocalization =
					(LocalizedEntryLocalization)session.merge(
						localizedEntryLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(localizedEntryLocalization, false);

		if (isNew) {
			localizedEntryLocalization.setNew(false);
		}

		localizedEntryLocalization.resetOriginalValues();

		return localizedEntryLocalization;
	}

	/**
	 * Returns the localized entry localization with the primary key or throws a <code>NoSuchLocalizedEntryLocalizationException</code> if it could not be found.
	 *
	 * @param localizedEntryLocalizationId the primary key of the localized entry localization
	 * @return the localized entry localization
	 * @throws NoSuchLocalizedEntryLocalizationException if a localized entry localization with the primary key could not be found
	 */
	@Override
	public LocalizedEntryLocalization findByPrimaryKey(
			long localizedEntryLocalizationId)
		throws NoSuchLocalizedEntryLocalizationException {

		return findByPrimaryKey((Serializable)localizedEntryLocalizationId);
	}

	/**
	 * Returns the localized entry localization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param localizedEntryLocalizationId the primary key of the localized entry localization
	 * @return the localized entry localization, or <code>null</code> if a localized entry localization with the primary key could not be found
	 */
	@Override
	public LocalizedEntryLocalization fetchByPrimaryKey(
		long localizedEntryLocalizationId) {

		return fetchByPrimaryKey((Serializable)localizedEntryLocalizationId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "localizedEntryLocalizationId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LOCALIZEDENTRYLOCALIZATION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return LocalizedEntryLocalizationModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the localized entry localization persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByLocalizedEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLocalizedEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"localizedEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLocalizedEntryId",
					new String[] {Long.class.getName()},
					new String[] {"localizedEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLocalizedEntryId",
					new String[] {Long.class.getName()},
					new String[] {"localizedEntryId"}, false),
				_SQL_SELECT_LOCALIZEDENTRYLOCALIZATION_WHERE,
				_SQL_COUNT_LOCALIZEDENTRYLOCALIZATION_WHERE,
				LocalizedEntryLocalizationModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"localizedEntryLocalization.", "localizedEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					LocalizedEntryLocalization::getLocalizedEntryId));

		_uniquePersistenceFinderByLocalizedEntryId_LanguageId =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY,
					"fetchByLocalizedEntryId_LanguageId",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"localizedEntryId", "languageId"}, 0, 2,
					false, LocalizedEntryLocalization::getLocalizedEntryId,
					convertNullFunction(
						LocalizedEntryLocalization::getLanguageId)),
				_SQL_SELECT_LOCALIZEDENTRYLOCALIZATION_WHERE, "",
				new FinderColumn<>(
					"localizedEntryLocalization.", "localizedEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					LocalizedEntryLocalization::getLocalizedEntryId),
				new FinderColumn<>(
					"localizedEntryLocalization.", "languageId",
					FinderColumn.Type.STRING, "=", true, true,
					LocalizedEntryLocalization::getLanguageId));

		LocalizedEntryLocalizationUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LocalizedEntryLocalizationUtil.setPersistence(null);

		entityCache.removeCache(LocalizedEntryLocalizationImpl.class.getName());
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		LocalizedEntryLocalizationModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LOCALIZEDENTRYLOCALIZATION =
		"SELECT localizedEntryLocalization FROM LocalizedEntryLocalization localizedEntryLocalization";

	private static final String _SQL_SELECT_LOCALIZEDENTRYLOCALIZATION_WHERE =
		"SELECT localizedEntryLocalization FROM LocalizedEntryLocalization localizedEntryLocalization WHERE ";

	private static final String _SQL_COUNT_LOCALIZEDENTRYLOCALIZATION_WHERE =
		"SELECT COUNT(localizedEntryLocalization) FROM LocalizedEntryLocalization localizedEntryLocalization WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:139627070