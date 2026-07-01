/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.persistence.impl;

import com.liferay.journal.exception.NoSuchArticleLocalizationException;
import com.liferay.journal.model.JournalArticleLocalization;
import com.liferay.journal.model.JournalArticleLocalizationTable;
import com.liferay.journal.model.impl.JournalArticleLocalizationImpl;
import com.liferay.journal.model.impl.JournalArticleLocalizationModelImpl;
import com.liferay.journal.service.persistence.JournalArticleLocalizationPersistence;
import com.liferay.journal.service.persistence.JournalArticleLocalizationUtil;
import com.liferay.journal.service.persistence.impl.constants.JournalPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
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
 * The persistence implementation for the journal article localization service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = JournalArticleLocalizationPersistence.class)
public class JournalArticleLocalizationPersistenceImpl
	extends BasePersistenceImpl
		<JournalArticleLocalization, NoSuchArticleLocalizationException>
	implements JournalArticleLocalizationPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>JournalArticleLocalizationUtil</code> to access the journal article localization persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		JournalArticleLocalizationImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<JournalArticleLocalization, NoSuchArticleLocalizationException>
			_collectionPersistenceFinderByC_A;

	/**
	 * Returns an ordered range of all the journal article localizations where companyId = &#63; and articlePK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalArticleLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param articlePK the article pk
	 * @param start the lower bound of the range of journal article localizations
	 * @param end the upper bound of the range of journal article localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal article localizations
	 */
	@Override
	public List<JournalArticleLocalization> findByC_A(
		long companyId, long articlePK, int start, int end,
		OrderByComparator<JournalArticleLocalization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A.find(
			finderCache, new Object[] {companyId, articlePK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article localization in the ordered set where companyId = &#63; and articlePK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param articlePK the article pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article localization
	 * @throws NoSuchArticleLocalizationException if a matching journal article localization could not be found
	 */
	@Override
	public JournalArticleLocalization findByC_A_First(
			long companyId, long articlePK,
			OrderByComparator<JournalArticleLocalization> orderByComparator)
		throws NoSuchArticleLocalizationException {

		return _collectionPersistenceFinderByC_A.findFirst(
			finderCache, new Object[] {companyId, articlePK},
			orderByComparator);
	}

	/**
	 * Returns the first journal article localization in the ordered set where companyId = &#63; and articlePK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param articlePK the article pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article localization, or <code>null</code> if a matching journal article localization could not be found
	 */
	@Override
	public JournalArticleLocalization fetchByC_A_First(
		long companyId, long articlePK,
		OrderByComparator<JournalArticleLocalization> orderByComparator) {

		return _collectionPersistenceFinderByC_A.fetchFirst(
			finderCache, new Object[] {companyId, articlePK},
			orderByComparator);
	}

	/**
	 * Removes all the journal article localizations where companyId = &#63; and articlePK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param articlePK the article pk
	 */
	@Override
	public void removeByC_A(long companyId, long articlePK) {
		_collectionPersistenceFinderByC_A.remove(
			finderCache, new Object[] {companyId, articlePK});
	}

	/**
	 * Returns the number of journal article localizations where companyId = &#63; and articlePK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param articlePK the article pk
	 * @return the number of matching journal article localizations
	 */
	@Override
	public int countByC_A(long companyId, long articlePK) {
		return _collectionPersistenceFinderByC_A.count(
			finderCache, new Object[] {companyId, articlePK});
	}

	private UniquePersistenceFinder
		<JournalArticleLocalization, NoSuchArticleLocalizationException>
			_uniquePersistenceFinderByC_A_L;

	/**
	 * Returns the journal article localization where companyId = &#63; and articlePK = &#63; and languageId = &#63; or throws a <code>NoSuchArticleLocalizationException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param articlePK the article pk
	 * @param languageId the language ID
	 * @return the matching journal article localization
	 * @throws NoSuchArticleLocalizationException if a matching journal article localization could not be found
	 */
	@Override
	public JournalArticleLocalization findByC_A_L(
			long companyId, long articlePK, String languageId)
		throws NoSuchArticleLocalizationException {

		return _uniquePersistenceFinderByC_A_L.find(
			finderCache, new Object[] {companyId, articlePK, languageId});
	}

	/**
	 * Returns the journal article localization where companyId = &#63; and articlePK = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param articlePK the article pk
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching journal article localization, or <code>null</code> if a matching journal article localization could not be found
	 */
	@Override
	public JournalArticleLocalization fetchByC_A_L(
		long companyId, long articlePK, String languageId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_A_L.fetch(
			finderCache, new Object[] {companyId, articlePK, languageId},
			useFinderCache);
	}

	/**
	 * Removes the journal article localization where companyId = &#63; and articlePK = &#63; and languageId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param articlePK the article pk
	 * @param languageId the language ID
	 * @return the journal article localization that was removed
	 */
	@Override
	public JournalArticleLocalization removeByC_A_L(
			long companyId, long articlePK, String languageId)
		throws NoSuchArticleLocalizationException {

		JournalArticleLocalization journalArticleLocalization = findByC_A_L(
			companyId, articlePK, languageId);

		return remove(journalArticleLocalization);
	}

	/**
	 * Returns the number of journal article localizations where companyId = &#63; and articlePK = &#63; and languageId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param articlePK the article pk
	 * @param languageId the language ID
	 * @return the number of matching journal article localizations
	 */
	@Override
	public int countByC_A_L(long companyId, long articlePK, String languageId) {
		return _uniquePersistenceFinderByC_A_L.count(
			finderCache, new Object[] {companyId, articlePK, languageId});
	}

	public JournalArticleLocalizationPersistenceImpl() {
		setModelClass(JournalArticleLocalization.class);

		setModelImplClass(JournalArticleLocalizationImpl.class);
		setModelPKClass(long.class);

		setTable(JournalArticleLocalizationTable.INSTANCE);
	}

	/**
	 * Creates a new journal article localization with the primary key. Does not add the journal article localization to the database.
	 *
	 * @param articleLocalizationId the primary key for the new journal article localization
	 * @return the new journal article localization
	 */
	@Override
	public JournalArticleLocalization create(long articleLocalizationId) {
		JournalArticleLocalization journalArticleLocalization =
			new JournalArticleLocalizationImpl();

		journalArticleLocalization.setNew(true);
		journalArticleLocalization.setPrimaryKey(articleLocalizationId);

		journalArticleLocalization.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return journalArticleLocalization;
	}

	/**
	 * Removes the journal article localization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param articleLocalizationId the primary key of the journal article localization
	 * @return the journal article localization that was removed
	 * @throws NoSuchArticleLocalizationException if a journal article localization with the primary key could not be found
	 */
	@Override
	public JournalArticleLocalization remove(long articleLocalizationId)
		throws NoSuchArticleLocalizationException {

		return remove((Serializable)articleLocalizationId);
	}

	@Override
	protected JournalArticleLocalization removeImpl(
		JournalArticleLocalization journalArticleLocalization) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(journalArticleLocalization)) {
				journalArticleLocalization =
					(JournalArticleLocalization)session.get(
						JournalArticleLocalizationImpl.class,
						journalArticleLocalization.getPrimaryKeyObj());
			}

			if ((journalArticleLocalization != null) &&
				ctPersistenceHelper.isRemove(journalArticleLocalization)) {

				session.delete(journalArticleLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (journalArticleLocalization != null) {
			clearCache(journalArticleLocalization);
		}

		return journalArticleLocalization;
	}

	@Override
	public JournalArticleLocalization updateImpl(
		JournalArticleLocalization journalArticleLocalization) {

		boolean isNew = journalArticleLocalization.isNew();

		if (!(journalArticleLocalization instanceof
				JournalArticleLocalizationModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(journalArticleLocalization.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					journalArticleLocalization);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in journalArticleLocalization proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom JournalArticleLocalization implementation " +
					journalArticleLocalization.getClass());
		}

		JournalArticleLocalizationModelImpl
			journalArticleLocalizationModelImpl =
				(JournalArticleLocalizationModelImpl)journalArticleLocalization;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(journalArticleLocalization)) {
				if (!isNew) {
					session.evict(
						JournalArticleLocalizationImpl.class,
						journalArticleLocalization.getPrimaryKeyObj());
				}

				session.save(journalArticleLocalization);
			}
			else {
				journalArticleLocalization =
					(JournalArticleLocalization)session.merge(
						journalArticleLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(journalArticleLocalization, false);

		if (isNew) {
			journalArticleLocalization.setNew(false);
		}

		journalArticleLocalization.resetOriginalValues();

		return journalArticleLocalization;
	}

	/**
	 * Returns the journal article localization with the primary key or throws a <code>NoSuchArticleLocalizationException</code> if it could not be found.
	 *
	 * @param articleLocalizationId the primary key of the journal article localization
	 * @return the journal article localization
	 * @throws NoSuchArticleLocalizationException if a journal article localization with the primary key could not be found
	 */
	@Override
	public JournalArticleLocalization findByPrimaryKey(
			long articleLocalizationId)
		throws NoSuchArticleLocalizationException {

		return findByPrimaryKey((Serializable)articleLocalizationId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the journal article localization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param articleLocalizationId the primary key of the journal article localization
	 * @return the journal article localization, or <code>null</code> if a journal article localization with the primary key could not be found
	 */
	@Override
	public JournalArticleLocalization fetchByPrimaryKey(
		long articleLocalizationId) {

		return fetchByPrimaryKey((Serializable)articleLocalizationId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "articleLocalizationId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_JOURNALARTICLELOCALIZATION;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return JournalArticleLocalizationModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "JournalArticleLocalization";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("companyId");
		ctMergeColumnNames.add("articlePK");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("languageId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("articleLocalizationId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"companyId", "articlePK", "languageId"});
	}

	/**
	 * Initializes the journal article localization persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByC_A = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "articlePK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "articlePK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "articlePK"}, false),
			_SQL_SELECT_JOURNALARTICLELOCALIZATION_WHERE,
			_SQL_COUNT_JOURNALARTICLELOCALIZATION_WHERE,
			JournalArticleLocalizationModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"journalArticleLocalization.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				JournalArticleLocalization::getCompanyId),
			new FinderColumn<>(
				"journalArticleLocalization.", "articlePK",
				FinderColumn.Type.LONG, "=", true, true,
				JournalArticleLocalization::getArticlePK));

		_uniquePersistenceFinderByC_A_L = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_A_L",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "articlePK", "languageId"}, 0, 4,
				false, JournalArticleLocalization::getCompanyId,
				JournalArticleLocalization::getArticlePK,
				convertNullFunction(JournalArticleLocalization::getLanguageId)),
			_SQL_SELECT_JOURNALARTICLELOCALIZATION_WHERE, "",
			new FinderColumn<>(
				"journalArticleLocalization.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				JournalArticleLocalization::getCompanyId),
			new FinderColumn<>(
				"journalArticleLocalization.", "articlePK",
				FinderColumn.Type.LONG, "=", true, true,
				JournalArticleLocalization::getArticlePK),
			new FinderColumn<>(
				"journalArticleLocalization.", "languageId",
				FinderColumn.Type.STRING, "=", true, true,
				JournalArticleLocalization::getLanguageId));

		JournalArticleLocalizationUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		JournalArticleLocalizationUtil.setPersistence(null);

		entityCache.removeCache(JournalArticleLocalizationImpl.class.getName());
	}

	@Override
	@Reference(
		target = JournalPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = JournalPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = JournalPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		JournalArticleLocalizationModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_JOURNALARTICLELOCALIZATION =
		"SELECT journalArticleLocalization FROM JournalArticleLocalization journalArticleLocalization";

	private static final String _SQL_SELECT_JOURNALARTICLELOCALIZATION_WHERE =
		"SELECT journalArticleLocalization FROM JournalArticleLocalization journalArticleLocalization WHERE ";

	private static final String _SQL_COUNT_JOURNALARTICLELOCALIZATION_WHERE =
		"SELECT COUNT(journalArticleLocalization) FROM JournalArticleLocalization journalArticleLocalization WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-733029452