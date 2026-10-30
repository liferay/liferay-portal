/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.persistence.impl;

import com.liferay.journal.exception.NoSuchContentSearchException;
import com.liferay.journal.model.JournalContentSearch;
import com.liferay.journal.model.JournalContentSearchTable;
import com.liferay.journal.model.impl.JournalContentSearchImpl;
import com.liferay.journal.model.impl.JournalContentSearchModelImpl;
import com.liferay.journal.service.persistence.JournalContentSearchPersistence;
import com.liferay.journal.service.persistence.JournalContentSearchUtil;
import com.liferay.journal.service.persistence.impl.constants.JournalPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
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
 * The persistence implementation for the journal content search service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = JournalContentSearchPersistence.class)
public class JournalContentSearchPersistenceImpl
	extends BasePersistenceImpl
		<JournalContentSearch, NoSuchContentSearchException>
	implements JournalContentSearchPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>JournalContentSearchUtil</code> to access the journal content search persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		JournalContentSearchImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<JournalContentSearch, NoSuchContentSearchException>
			_uniquePersistenceFinderByG_P_L_P_A;

	/**
	 * Returns the journal content search where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; and portletId = &#63; and articleId = &#63; or throws a <code>NoSuchContentSearchException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @param portletId the portlet ID
	 * @param articleId the article ID
	 * @return the matching journal content search
	 * @throws NoSuchContentSearchException if a matching journal content search could not be found
	 */
	@Override
	public JournalContentSearch findByG_P_L_P_A(
			long groupId, boolean privateLayout, long layoutId,
			String portletId, String articleId)
		throws NoSuchContentSearchException {

		return _uniquePersistenceFinderByG_P_L_P_A.find(
			finderCache,
			new Object[] {
				groupId, privateLayout, layoutId, portletId, articleId
			});
	}

	/**
	 * Returns the journal content search where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; and portletId = &#63; and articleId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @param portletId the portlet ID
	 * @param articleId the article ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching journal content search, or <code>null</code> if a matching journal content search could not be found
	 */
	@Override
	public JournalContentSearch fetchByG_P_L_P_A(
		long groupId, boolean privateLayout, long layoutId, String portletId,
		String articleId, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_P_L_P_A.fetch(
			finderCache,
			new Object[] {
				groupId, privateLayout, layoutId, portletId, articleId
			},
			useFinderCache);
	}

	/**
	 * Removes the journal content search where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; and portletId = &#63; and articleId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @param portletId the portlet ID
	 * @param articleId the article ID
	 * @return the journal content search that was removed
	 */
	@Override
	public JournalContentSearch removeByG_P_L_P_A(
			long groupId, boolean privateLayout, long layoutId,
			String portletId, String articleId)
		throws NoSuchContentSearchException {

		JournalContentSearch journalContentSearch = findByG_P_L_P_A(
			groupId, privateLayout, layoutId, portletId, articleId);

		return remove(journalContentSearch);
	}

	/**
	 * Returns the number of journal content searches where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; and portletId = &#63; and articleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @param portletId the portlet ID
	 * @param articleId the article ID
	 * @return the number of matching journal content searches
	 */
	@Override
	public int countByG_P_L_P_A(
		long groupId, boolean privateLayout, long layoutId, String portletId,
		String articleId) {

		return _uniquePersistenceFinderByG_P_L_P_A.count(
			finderCache,
			new Object[] {
				groupId, privateLayout, layoutId, portletId, articleId
			});
	}

	public JournalContentSearchPersistenceImpl() {
		setModelClass(JournalContentSearch.class);

		setModelImplClass(JournalContentSearchImpl.class);
		setModelPKClass(long.class);

		setTable(JournalContentSearchTable.INSTANCE);
	}

	/**
	 * Creates a new journal content search with the primary key. Does not add the journal content search to the database.
	 *
	 * @param contentSearchId the primary key for the new journal content search
	 * @return the new journal content search
	 */
	@Override
	public JournalContentSearch create(long contentSearchId) {
		JournalContentSearch journalContentSearch =
			new JournalContentSearchImpl();

		journalContentSearch.setNew(true);
		journalContentSearch.setPrimaryKey(contentSearchId);

		journalContentSearch.setCompanyId(CompanyThreadLocal.getCompanyId());

		return journalContentSearch;
	}

	/**
	 * Removes the journal content search with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param contentSearchId the primary key of the journal content search
	 * @return the journal content search that was removed
	 * @throws NoSuchContentSearchException if a journal content search with the primary key could not be found
	 */
	@Override
	public JournalContentSearch remove(long contentSearchId)
		throws NoSuchContentSearchException {

		return remove((Serializable)contentSearchId);
	}

	@Override
	protected JournalContentSearch removeImpl(
		JournalContentSearch journalContentSearch) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(journalContentSearch)) {
				journalContentSearch = (JournalContentSearch)session.get(
					JournalContentSearchImpl.class,
					journalContentSearch.getPrimaryKeyObj());
			}

			if ((journalContentSearch != null) &&
				ctPersistenceHelper.isRemove(journalContentSearch)) {

				session.delete(journalContentSearch);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (journalContentSearch != null) {
			clearCache(journalContentSearch);
		}

		return journalContentSearch;
	}

	@Override
	public JournalContentSearch updateImpl(
		JournalContentSearch journalContentSearch) {

		boolean isNew = journalContentSearch.isNew();

		if (!(journalContentSearch instanceof JournalContentSearchModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(journalContentSearch.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					journalContentSearch);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in journalContentSearch proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom JournalContentSearch implementation " +
					journalContentSearch.getClass());
		}

		JournalContentSearchModelImpl journalContentSearchModelImpl =
			(JournalContentSearchModelImpl)journalContentSearch;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(journalContentSearch)) {
				if (!isNew) {
					session.evict(
						JournalContentSearchImpl.class,
						journalContentSearch.getPrimaryKeyObj());
				}

				session.save(journalContentSearch);
			}
			else {
				journalContentSearch = (JournalContentSearch)session.merge(
					journalContentSearch);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(journalContentSearch, false);

		if (isNew) {
			journalContentSearch.setNew(false);
		}

		journalContentSearch.resetOriginalValues();

		return journalContentSearch;
	}

	/**
	 * Returns the journal content search with the primary key or throws a <code>NoSuchContentSearchException</code> if it could not be found.
	 *
	 * @param contentSearchId the primary key of the journal content search
	 * @return the journal content search
	 * @throws NoSuchContentSearchException if a journal content search with the primary key could not be found
	 */
	@Override
	public JournalContentSearch findByPrimaryKey(long contentSearchId)
		throws NoSuchContentSearchException {

		return findByPrimaryKey((Serializable)contentSearchId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the journal content search with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param contentSearchId the primary key of the journal content search
	 * @return the journal content search, or <code>null</code> if a journal content search with the primary key could not be found
	 */
	@Override
	public JournalContentSearch fetchByPrimaryKey(long contentSearchId) {
		return fetchByPrimaryKey((Serializable)contentSearchId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "contentSearchId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_JOURNALCONTENTSEARCH;
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
		return JournalContentSearchModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "JournalContentSearch";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctMergeColumnNames.add("privateLayout");
		ctMergeColumnNames.add("layoutId");
		ctMergeColumnNames.add("portletId");
		ctMergeColumnNames.add("articleId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("contentSearchId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {
				"groupId", "privateLayout", "layoutId", "portletId", "articleId"
			});
	}

	/**
	 * Initializes the journal content search persistence.
	 */
	@Activate
	public void activate() {
		_uniquePersistenceFinderByG_P_L_P_A = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_P_L_P_A",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {
					"groupId", "privateLayout", "layoutId", "portletId",
					"articleId"
				},
				0, 24, false, JournalContentSearch::getGroupId,
				JournalContentSearch::isPrivateLayout,
				JournalContentSearch::getLayoutId,
				convertNullFunction(JournalContentSearch::getPortletId),
				convertNullFunction(JournalContentSearch::getArticleId)),
			_SQL_SELECT_JOURNALCONTENTSEARCH_WHERE, "",
			new FinderColumn<>(
				"journalContentSearch.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, JournalContentSearch::getGroupId),
			new FinderColumn<>(
				"journalContentSearch.", "privateLayout",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				JournalContentSearch::isPrivateLayout),
			new FinderColumn<>(
				"journalContentSearch.", "layoutId", FinderColumn.Type.LONG,
				"=", true, true, JournalContentSearch::getLayoutId),
			new FinderColumn<>(
				"journalContentSearch.", "portletId", FinderColumn.Type.STRING,
				"=", true, true, JournalContentSearch::getPortletId),
			new FinderColumn<>(
				"journalContentSearch.", "articleId", FinderColumn.Type.STRING,
				"=", true, true, JournalContentSearch::getArticleId));

		JournalContentSearchUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		JournalContentSearchUtil.setPersistence(null);

		entityCache.removeCache(JournalContentSearchImpl.class.getName());
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

	private static final String _SQL_SELECT_JOURNALCONTENTSEARCH =
		"SELECT journalContentSearch FROM JournalContentSearch journalContentSearch";

	private static final String _SQL_SELECT_JOURNALCONTENTSEARCH_WHERE =
		"SELECT journalContentSearch FROM JournalContentSearch journalContentSearch WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2129116862