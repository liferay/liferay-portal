/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.service.persistence.impl;

import com.liferay.layout.seo.exception.NoSuchEntryCustomMetaTagException;
import com.liferay.layout.seo.model.LayoutSEOEntryCustomMetaTag;
import com.liferay.layout.seo.model.LayoutSEOEntryCustomMetaTagTable;
import com.liferay.layout.seo.model.impl.LayoutSEOEntryCustomMetaTagImpl;
import com.liferay.layout.seo.model.impl.LayoutSEOEntryCustomMetaTagModelImpl;
import com.liferay.layout.seo.service.persistence.LayoutSEOEntryCustomMetaTagPersistence;
import com.liferay.layout.seo.service.persistence.LayoutSEOEntryCustomMetaTagUtil;
import com.liferay.layout.seo.service.persistence.impl.constants.LayoutSEOPersistenceConstants;
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
 * The persistence implementation for the layout seo entry custom meta tag service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = LayoutSEOEntryCustomMetaTagPersistence.class)
public class LayoutSEOEntryCustomMetaTagPersistenceImpl
	extends BasePersistenceImpl
		<LayoutSEOEntryCustomMetaTag, NoSuchEntryCustomMetaTagException>
	implements LayoutSEOEntryCustomMetaTagPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutSEOEntryCustomMetaTagUtil</code> to access the layout seo entry custom meta tag persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutSEOEntryCustomMetaTagImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LayoutSEOEntryCustomMetaTag, NoSuchEntryCustomMetaTagException>
			_collectionPersistenceFinderByG_L;

	/**
	 * Returns an ordered range of all the layout seo entry custom meta tags where groupId = &#63; and layoutSEOEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSEOEntryCustomMetaTagModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutSEOEntryId the layout seo entry ID
	 * @param start the lower bound of the range of layout seo entry custom meta tags
	 * @param end the upper bound of the range of layout seo entry custom meta tags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout seo entry custom meta tags
	 */
	@Override
	public List<LayoutSEOEntryCustomMetaTag> findByG_L(
		long groupId, long layoutSEOEntryId, int start, int end,
		OrderByComparator<LayoutSEOEntryCustomMetaTag> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_L.find(
			finderCache, new Object[] {groupId, layoutSEOEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout seo entry custom meta tag in the ordered set where groupId = &#63; and layoutSEOEntryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutSEOEntryId the layout seo entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout seo entry custom meta tag
	 * @throws NoSuchEntryCustomMetaTagException if a matching layout seo entry custom meta tag could not be found
	 */
	@Override
	public LayoutSEOEntryCustomMetaTag findByG_L_First(
			long groupId, long layoutSEOEntryId,
			OrderByComparator<LayoutSEOEntryCustomMetaTag> orderByComparator)
		throws NoSuchEntryCustomMetaTagException {

		return _collectionPersistenceFinderByG_L.findFirst(
			finderCache, new Object[] {groupId, layoutSEOEntryId},
			orderByComparator);
	}

	/**
	 * Returns the first layout seo entry custom meta tag in the ordered set where groupId = &#63; and layoutSEOEntryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutSEOEntryId the layout seo entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout seo entry custom meta tag, or <code>null</code> if a matching layout seo entry custom meta tag could not be found
	 */
	@Override
	public LayoutSEOEntryCustomMetaTag fetchByG_L_First(
		long groupId, long layoutSEOEntryId,
		OrderByComparator<LayoutSEOEntryCustomMetaTag> orderByComparator) {

		return _collectionPersistenceFinderByG_L.fetchFirst(
			finderCache, new Object[] {groupId, layoutSEOEntryId},
			orderByComparator);
	}

	/**
	 * Removes all the layout seo entry custom meta tags where groupId = &#63; and layoutSEOEntryId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutSEOEntryId the layout seo entry ID
	 */
	@Override
	public void removeByG_L(long groupId, long layoutSEOEntryId) {
		_collectionPersistenceFinderByG_L.remove(
			finderCache, new Object[] {groupId, layoutSEOEntryId});
	}

	/**
	 * Returns the number of layout seo entry custom meta tags where groupId = &#63; and layoutSEOEntryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutSEOEntryId the layout seo entry ID
	 * @return the number of matching layout seo entry custom meta tags
	 */
	@Override
	public int countByG_L(long groupId, long layoutSEOEntryId) {
		return _collectionPersistenceFinderByG_L.count(
			finderCache, new Object[] {groupId, layoutSEOEntryId});
	}

	public LayoutSEOEntryCustomMetaTagPersistenceImpl() {
		setModelClass(LayoutSEOEntryCustomMetaTag.class);

		setModelImplClass(LayoutSEOEntryCustomMetaTagImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutSEOEntryCustomMetaTagTable.INSTANCE);
	}

	/**
	 * Creates a new layout seo entry custom meta tag with the primary key. Does not add the layout seo entry custom meta tag to the database.
	 *
	 * @param layoutSEOEntryCustomMetaTagId the primary key for the new layout seo entry custom meta tag
	 * @return the new layout seo entry custom meta tag
	 */
	@Override
	public LayoutSEOEntryCustomMetaTag create(
		long layoutSEOEntryCustomMetaTagId) {

		LayoutSEOEntryCustomMetaTag layoutSEOEntryCustomMetaTag =
			new LayoutSEOEntryCustomMetaTagImpl();

		layoutSEOEntryCustomMetaTag.setNew(true);
		layoutSEOEntryCustomMetaTag.setPrimaryKey(
			layoutSEOEntryCustomMetaTagId);

		layoutSEOEntryCustomMetaTag.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return layoutSEOEntryCustomMetaTag;
	}

	/**
	 * Removes the layout seo entry custom meta tag with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutSEOEntryCustomMetaTagId the primary key of the layout seo entry custom meta tag
	 * @return the layout seo entry custom meta tag that was removed
	 * @throws NoSuchEntryCustomMetaTagException if a layout seo entry custom meta tag with the primary key could not be found
	 */
	@Override
	public LayoutSEOEntryCustomMetaTag remove(
			long layoutSEOEntryCustomMetaTagId)
		throws NoSuchEntryCustomMetaTagException {

		return remove((Serializable)layoutSEOEntryCustomMetaTagId);
	}

	@Override
	protected LayoutSEOEntryCustomMetaTag removeImpl(
		LayoutSEOEntryCustomMetaTag layoutSEOEntryCustomMetaTag) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutSEOEntryCustomMetaTag)) {
				layoutSEOEntryCustomMetaTag =
					(LayoutSEOEntryCustomMetaTag)session.get(
						LayoutSEOEntryCustomMetaTagImpl.class,
						layoutSEOEntryCustomMetaTag.getPrimaryKeyObj());
			}

			if ((layoutSEOEntryCustomMetaTag != null) &&
				ctPersistenceHelper.isRemove(layoutSEOEntryCustomMetaTag)) {

				session.delete(layoutSEOEntryCustomMetaTag);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutSEOEntryCustomMetaTag != null) {
			clearCache(layoutSEOEntryCustomMetaTag);
		}

		return layoutSEOEntryCustomMetaTag;
	}

	@Override
	public LayoutSEOEntryCustomMetaTag updateImpl(
		LayoutSEOEntryCustomMetaTag layoutSEOEntryCustomMetaTag) {

		boolean isNew = layoutSEOEntryCustomMetaTag.isNew();

		if (!(layoutSEOEntryCustomMetaTag instanceof
				LayoutSEOEntryCustomMetaTagModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					layoutSEOEntryCustomMetaTag.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutSEOEntryCustomMetaTag);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutSEOEntryCustomMetaTag proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutSEOEntryCustomMetaTag implementation " +
					layoutSEOEntryCustomMetaTag.getClass());
		}

		LayoutSEOEntryCustomMetaTagModelImpl
			layoutSEOEntryCustomMetaTagModelImpl =
				(LayoutSEOEntryCustomMetaTagModelImpl)
					layoutSEOEntryCustomMetaTag;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(layoutSEOEntryCustomMetaTag)) {
				if (!isNew) {
					session.evict(
						LayoutSEOEntryCustomMetaTagImpl.class,
						layoutSEOEntryCustomMetaTag.getPrimaryKeyObj());
				}

				session.save(layoutSEOEntryCustomMetaTag);
			}
			else {
				layoutSEOEntryCustomMetaTag =
					(LayoutSEOEntryCustomMetaTag)session.merge(
						layoutSEOEntryCustomMetaTag);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layoutSEOEntryCustomMetaTag, false);

		if (isNew) {
			layoutSEOEntryCustomMetaTag.setNew(false);
		}

		layoutSEOEntryCustomMetaTag.resetOriginalValues();

		return layoutSEOEntryCustomMetaTag;
	}

	/**
	 * Returns the layout seo entry custom meta tag with the primary key or throws a <code>NoSuchEntryCustomMetaTagException</code> if it could not be found.
	 *
	 * @param layoutSEOEntryCustomMetaTagId the primary key of the layout seo entry custom meta tag
	 * @return the layout seo entry custom meta tag
	 * @throws NoSuchEntryCustomMetaTagException if a layout seo entry custom meta tag with the primary key could not be found
	 */
	@Override
	public LayoutSEOEntryCustomMetaTag findByPrimaryKey(
			long layoutSEOEntryCustomMetaTagId)
		throws NoSuchEntryCustomMetaTagException {

		return findByPrimaryKey((Serializable)layoutSEOEntryCustomMetaTagId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the layout seo entry custom meta tag with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutSEOEntryCustomMetaTagId the primary key of the layout seo entry custom meta tag
	 * @return the layout seo entry custom meta tag, or <code>null</code> if a layout seo entry custom meta tag with the primary key could not be found
	 */
	@Override
	public LayoutSEOEntryCustomMetaTag fetchByPrimaryKey(
		long layoutSEOEntryCustomMetaTagId) {

		return fetchByPrimaryKey((Serializable)layoutSEOEntryCustomMetaTagId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "layoutSEOEntryCustomMetaTagId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTSEOENTRYCUSTOMMETATAG;
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
		return LayoutSEOEntryCustomMetaTagModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LayoutSEOEntryCustomMetaTag";
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
		ctMergeColumnNames.add("layoutSEOEntryId");
		ctMergeColumnNames.add("content");
		ctMergeColumnNames.add("property");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("layoutSEOEntryCustomMetaTagId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the layout seo entry custom meta tag persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByG_L = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_L",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "layoutSEOEntryId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_L",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "layoutSEOEntryId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_L",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "layoutSEOEntryId"}, false),
			_SQL_SELECT_LAYOUTSEOENTRYCUSTOMMETATAG_WHERE,
			_SQL_COUNT_LAYOUTSEOENTRYCUSTOMMETATAG_WHERE,
			LayoutSEOEntryCustomMetaTagModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"layoutSEOEntryCustomMetaTag.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				LayoutSEOEntryCustomMetaTag::getGroupId),
			new FinderColumn<>(
				"layoutSEOEntryCustomMetaTag.", "layoutSEOEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				LayoutSEOEntryCustomMetaTag::getLayoutSEOEntryId));

		LayoutSEOEntryCustomMetaTagUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LayoutSEOEntryCustomMetaTagUtil.setPersistence(null);

		entityCache.removeCache(
			LayoutSEOEntryCustomMetaTagImpl.class.getName());
	}

	@Override
	@Reference(
		target = LayoutSEOPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = LayoutSEOPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = LayoutSEOPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		LayoutSEOEntryCustomMetaTagModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUTSEOENTRYCUSTOMMETATAG =
		"SELECT layoutSEOEntryCustomMetaTag FROM LayoutSEOEntryCustomMetaTag layoutSEOEntryCustomMetaTag";

	private static final String _SQL_SELECT_LAYOUTSEOENTRYCUSTOMMETATAG_WHERE =
		"SELECT layoutSEOEntryCustomMetaTag FROM LayoutSEOEntryCustomMetaTag layoutSEOEntryCustomMetaTag WHERE ";

	private static final String _SQL_COUNT_LAYOUTSEOENTRYCUSTOMMETATAG_WHERE =
		"SELECT COUNT(layoutSEOEntryCustomMetaTag) FROM LayoutSEOEntryCustomMetaTag layoutSEOEntryCustomMetaTag WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-583380150