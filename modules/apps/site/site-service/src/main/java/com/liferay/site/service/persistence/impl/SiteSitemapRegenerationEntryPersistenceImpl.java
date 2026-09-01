/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.service.persistence.impl;

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
import com.liferay.site.exception.NoSuchSitemapRegenerationEntryException;
import com.liferay.site.model.SiteSitemapRegenerationEntry;
import com.liferay.site.model.SiteSitemapRegenerationEntryTable;
import com.liferay.site.model.impl.SiteSitemapRegenerationEntryImpl;
import com.liferay.site.model.impl.SiteSitemapRegenerationEntryModelImpl;
import com.liferay.site.service.persistence.SiteSitemapRegenerationEntryPersistence;
import com.liferay.site.service.persistence.SiteSitemapRegenerationEntryUtil;
import com.liferay.site.service.persistence.impl.constants.SitePersistenceConstants;

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
 * The persistence implementation for the site sitemap regeneration entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = SiteSitemapRegenerationEntryPersistence.class)
public class SiteSitemapRegenerationEntryPersistenceImpl
	extends BasePersistenceImpl
		<SiteSitemapRegenerationEntry, NoSuchSitemapRegenerationEntryException>
	implements SiteSitemapRegenerationEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SiteSitemapRegenerationEntryUtil</code> to access the site sitemap regeneration entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SiteSitemapRegenerationEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SiteSitemapRegenerationEntry, NoSuchSitemapRegenerationEntryException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the site sitemap regeneration entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteSitemapRegenerationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of site sitemap regeneration entries
	 * @param end the upper bound of the range of site sitemap regeneration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site sitemap regeneration entries
	 */
	@Override
	public List<SiteSitemapRegenerationEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<SiteSitemapRegenerationEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first site sitemap regeneration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site sitemap regeneration entry
	 * @throws NoSuchSitemapRegenerationEntryException if a matching site sitemap regeneration entry could not be found
	 */
	@Override
	public SiteSitemapRegenerationEntry findByCompanyId_First(
			long companyId,
			OrderByComparator<SiteSitemapRegenerationEntry> orderByComparator)
		throws NoSuchSitemapRegenerationEntryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first site sitemap regeneration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site sitemap regeneration entry, or <code>null</code> if a matching site sitemap regeneration entry could not be found
	 */
	@Override
	public SiteSitemapRegenerationEntry fetchByCompanyId_First(
		long companyId,
		OrderByComparator<SiteSitemapRegenerationEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the site sitemap regeneration entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of site sitemap regeneration entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching site sitemap regeneration entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<SiteSitemapRegenerationEntry, NoSuchSitemapRegenerationEntryException>
			_collectionPersistenceFinderByG_C_A;

	/**
	 * Returns an ordered range of all the site sitemap regeneration entries where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteSitemapRegenerationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 * @param start the lower bound of the range of site sitemap regeneration entries
	 * @param end the upper bound of the range of site sitemap regeneration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site sitemap regeneration entries
	 */
	@Override
	public List<SiteSitemapRegenerationEntry> findByG_C_A(
		long groupId, long companyId, String assetTypeKey, int start, int end,
		OrderByComparator<SiteSitemapRegenerationEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_A.find(
			finderCache, new Object[] {groupId, companyId, assetTypeKey}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first site sitemap regeneration entry in the ordered set where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site sitemap regeneration entry
	 * @throws NoSuchSitemapRegenerationEntryException if a matching site sitemap regeneration entry could not be found
	 */
	@Override
	public SiteSitemapRegenerationEntry findByG_C_A_First(
			long groupId, long companyId, String assetTypeKey,
			OrderByComparator<SiteSitemapRegenerationEntry> orderByComparator)
		throws NoSuchSitemapRegenerationEntryException {

		return _collectionPersistenceFinderByG_C_A.findFirst(
			finderCache, new Object[] {groupId, companyId, assetTypeKey},
			orderByComparator);
	}

	/**
	 * Returns the first site sitemap regeneration entry in the ordered set where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site sitemap regeneration entry, or <code>null</code> if a matching site sitemap regeneration entry could not be found
	 */
	@Override
	public SiteSitemapRegenerationEntry fetchByG_C_A_First(
		long groupId, long companyId, String assetTypeKey,
		OrderByComparator<SiteSitemapRegenerationEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_A.fetchFirst(
			finderCache, new Object[] {groupId, companyId, assetTypeKey},
			orderByComparator);
	}

	/**
	 * Removes all the site sitemap regeneration entries where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 */
	@Override
	public void removeByG_C_A(
		long groupId, long companyId, String assetTypeKey) {

		_collectionPersistenceFinderByG_C_A.remove(
			finderCache, new Object[] {groupId, companyId, assetTypeKey});
	}

	/**
	 * Returns the number of site sitemap regeneration entries where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 * @return the number of matching site sitemap regeneration entries
	 */
	@Override
	public int countByG_C_A(long groupId, long companyId, String assetTypeKey) {
		return _collectionPersistenceFinderByG_C_A.count(
			finderCache, new Object[] {groupId, companyId, assetTypeKey});
	}

	public SiteSitemapRegenerationEntryPersistenceImpl() {
		setModelClass(SiteSitemapRegenerationEntry.class);

		setModelImplClass(SiteSitemapRegenerationEntryImpl.class);
		setModelPKClass(long.class);

		setTable(SiteSitemapRegenerationEntryTable.INSTANCE);
	}

	/**
	 * Creates a new site sitemap regeneration entry with the primary key. Does not add the site sitemap regeneration entry to the database.
	 *
	 * @param siteSitemapRegenerationEntryId the primary key for the new site sitemap regeneration entry
	 * @return the new site sitemap regeneration entry
	 */
	@Override
	public SiteSitemapRegenerationEntry create(
		long siteSitemapRegenerationEntryId) {

		SiteSitemapRegenerationEntry siteSitemapRegenerationEntry =
			new SiteSitemapRegenerationEntryImpl();

		siteSitemapRegenerationEntry.setNew(true);
		siteSitemapRegenerationEntry.setPrimaryKey(
			siteSitemapRegenerationEntryId);

		siteSitemapRegenerationEntry.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return siteSitemapRegenerationEntry;
	}

	/**
	 * Removes the site sitemap regeneration entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param siteSitemapRegenerationEntryId the primary key of the site sitemap regeneration entry
	 * @return the site sitemap regeneration entry that was removed
	 * @throws NoSuchSitemapRegenerationEntryException if a site sitemap regeneration entry with the primary key could not be found
	 */
	@Override
	public SiteSitemapRegenerationEntry remove(
			long siteSitemapRegenerationEntryId)
		throws NoSuchSitemapRegenerationEntryException {

		return remove((Serializable)siteSitemapRegenerationEntryId);
	}

	@Override
	protected SiteSitemapRegenerationEntry removeImpl(
		SiteSitemapRegenerationEntry siteSitemapRegenerationEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(siteSitemapRegenerationEntry)) {
				siteSitemapRegenerationEntry =
					(SiteSitemapRegenerationEntry)session.get(
						SiteSitemapRegenerationEntryImpl.class,
						siteSitemapRegenerationEntry.getPrimaryKeyObj());
			}

			if (siteSitemapRegenerationEntry != null) {
				session.delete(siteSitemapRegenerationEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (siteSitemapRegenerationEntry != null) {
			clearCache(siteSitemapRegenerationEntry);
		}

		return siteSitemapRegenerationEntry;
	}

	@Override
	public SiteSitemapRegenerationEntry updateImpl(
		SiteSitemapRegenerationEntry siteSitemapRegenerationEntry) {

		boolean isNew = siteSitemapRegenerationEntry.isNew();

		if (!(siteSitemapRegenerationEntry instanceof
				SiteSitemapRegenerationEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					siteSitemapRegenerationEntry.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					siteSitemapRegenerationEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in siteSitemapRegenerationEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SiteSitemapRegenerationEntry implementation " +
					siteSitemapRegenerationEntry.getClass());
		}

		SiteSitemapRegenerationEntryModelImpl
			siteSitemapRegenerationEntryModelImpl =
				(SiteSitemapRegenerationEntryModelImpl)
					siteSitemapRegenerationEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(siteSitemapRegenerationEntry);
			}
			else {
				siteSitemapRegenerationEntry =
					(SiteSitemapRegenerationEntry)session.merge(
						siteSitemapRegenerationEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(siteSitemapRegenerationEntry, false);

		if (isNew) {
			siteSitemapRegenerationEntry.setNew(false);
		}

		siteSitemapRegenerationEntry.resetOriginalValues();

		return siteSitemapRegenerationEntry;
	}

	/**
	 * Returns the site sitemap regeneration entry with the primary key or throws a <code>NoSuchSitemapRegenerationEntryException</code> if it could not be found.
	 *
	 * @param siteSitemapRegenerationEntryId the primary key of the site sitemap regeneration entry
	 * @return the site sitemap regeneration entry
	 * @throws NoSuchSitemapRegenerationEntryException if a site sitemap regeneration entry with the primary key could not be found
	 */
	@Override
	public SiteSitemapRegenerationEntry findByPrimaryKey(
			long siteSitemapRegenerationEntryId)
		throws NoSuchSitemapRegenerationEntryException {

		return findByPrimaryKey((Serializable)siteSitemapRegenerationEntryId);
	}

	/**
	 * Returns the site sitemap regeneration entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param siteSitemapRegenerationEntryId the primary key of the site sitemap regeneration entry
	 * @return the site sitemap regeneration entry, or <code>null</code> if a site sitemap regeneration entry with the primary key could not be found
	 */
	@Override
	public SiteSitemapRegenerationEntry fetchByPrimaryKey(
		long siteSitemapRegenerationEntryId) {

		return fetchByPrimaryKey((Serializable)siteSitemapRegenerationEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "siteSitemapRegenerationEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SITESITEMAPREGENERATIONENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SiteSitemapRegenerationEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the site sitemap regeneration entry persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCompanyId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, false),
				_SQL_SELECT_SITESITEMAPREGENERATIONENTRY_WHERE,
				_SQL_COUNT_SITESITEMAPREGENERATIONENTRY_WHERE,
				SiteSitemapRegenerationEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"siteSitemapRegenerationEntry.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					SiteSitemapRegenerationEntry::getCompanyId));

		_collectionPersistenceFinderByG_C_A = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_A",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "companyId", "assetTypeKey"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_A",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "companyId", "assetTypeKey"}, 0, 4,
				true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_A",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "companyId", "assetTypeKey"}, 0, 4,
				false, null),
			_SQL_SELECT_SITESITEMAPREGENERATIONENTRY_WHERE,
			_SQL_COUNT_SITESITEMAPREGENERATIONENTRY_WHERE,
			SiteSitemapRegenerationEntryModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"siteSitemapRegenerationEntry.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				SiteSitemapRegenerationEntry::getGroupId),
			new FinderColumn<>(
				"siteSitemapRegenerationEntry.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				SiteSitemapRegenerationEntry::getCompanyId),
			new FinderColumn<>(
				"siteSitemapRegenerationEntry.", "assetTypeKey",
				FinderColumn.Type.STRING, "=", true, true,
				SiteSitemapRegenerationEntry::getAssetTypeKey));

		SiteSitemapRegenerationEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SiteSitemapRegenerationEntryUtil.setPersistence(null);

		entityCache.removeCache(
			SiteSitemapRegenerationEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = SitePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SitePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SitePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		SiteSitemapRegenerationEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SITESITEMAPREGENERATIONENTRY =
		"SELECT siteSitemapRegenerationEntry FROM SiteSitemapRegenerationEntry siteSitemapRegenerationEntry";

	private static final String _SQL_SELECT_SITESITEMAPREGENERATIONENTRY_WHERE =
		"SELECT siteSitemapRegenerationEntry FROM SiteSitemapRegenerationEntry siteSitemapRegenerationEntry WHERE ";

	private static final String _SQL_COUNT_SITESITEMAPREGENERATIONENTRY_WHERE =
		"SELECT COUNT(siteSitemapRegenerationEntry) FROM SiteSitemapRegenerationEntry siteSitemapRegenerationEntry WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-2089687063