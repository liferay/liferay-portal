/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.redirect.exception.NoSuchNotFoundEntryException;
import com.liferay.redirect.model.RedirectNotFoundEntry;
import com.liferay.redirect.model.RedirectNotFoundEntryTable;
import com.liferay.redirect.model.impl.RedirectNotFoundEntryImpl;
import com.liferay.redirect.model.impl.RedirectNotFoundEntryModelImpl;
import com.liferay.redirect.service.persistence.RedirectNotFoundEntryPersistence;
import com.liferay.redirect.service.persistence.RedirectNotFoundEntryUtil;
import com.liferay.redirect.service.persistence.impl.constants.RedirectPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the redirect not found entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = RedirectNotFoundEntryPersistence.class)
public class RedirectNotFoundEntryPersistenceImpl
	extends BasePersistenceImpl
		<RedirectNotFoundEntry, NoSuchNotFoundEntryException>
	implements RedirectNotFoundEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>RedirectNotFoundEntryUtil</code> to access the redirect not found entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		RedirectNotFoundEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<RedirectNotFoundEntry, NoSuchNotFoundEntryException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the redirect not found entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectNotFoundEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of redirect not found entries
	 * @param end the upper bound of the range of redirect not found entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching redirect not found entries
	 */
	@Override
	public List<RedirectNotFoundEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<RedirectNotFoundEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first redirect not found entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching redirect not found entry
	 * @throws NoSuchNotFoundEntryException if a matching redirect not found entry could not be found
	 */
	@Override
	public RedirectNotFoundEntry findByGroupId_First(
			long groupId,
			OrderByComparator<RedirectNotFoundEntry> orderByComparator)
		throws NoSuchNotFoundEntryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first redirect not found entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching redirect not found entry, or <code>null</code> if a matching redirect not found entry could not be found
	 */
	@Override
	public RedirectNotFoundEntry fetchByGroupId_First(
		long groupId,
		OrderByComparator<RedirectNotFoundEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the redirect not found entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of redirect not found entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching redirect not found entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private UniquePersistenceFinder
		<RedirectNotFoundEntry, NoSuchNotFoundEntryException>
			_uniquePersistenceFinderByG_U;

	/**
	 * Returns the redirect not found entry where groupId = &#63; and url = &#63; or throws a <code>NoSuchNotFoundEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param url the url
	 * @return the matching redirect not found entry
	 * @throws NoSuchNotFoundEntryException if a matching redirect not found entry could not be found
	 */
	@Override
	public RedirectNotFoundEntry findByG_U(long groupId, String url)
		throws NoSuchNotFoundEntryException {

		return _uniquePersistenceFinderByG_U.find(
			finderCache, new Object[] {groupId, url});
	}

	/**
	 * Returns the redirect not found entry where groupId = &#63; and url = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param url the url
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching redirect not found entry, or <code>null</code> if a matching redirect not found entry could not be found
	 */
	@Override
	public RedirectNotFoundEntry fetchByG_U(
		long groupId, String url, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_U.fetch(
			finderCache, new Object[] {groupId, url}, useFinderCache);
	}

	/**
	 * Removes the redirect not found entry where groupId = &#63; and url = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param url the url
	 * @return the redirect not found entry that was removed
	 */
	@Override
	public RedirectNotFoundEntry removeByG_U(long groupId, String url)
		throws NoSuchNotFoundEntryException {

		RedirectNotFoundEntry redirectNotFoundEntry = findByG_U(groupId, url);

		return remove(redirectNotFoundEntry);
	}

	/**
	 * Returns the number of redirect not found entries where groupId = &#63; and url = &#63;.
	 *
	 * @param groupId the group ID
	 * @param url the url
	 * @return the number of matching redirect not found entries
	 */
	@Override
	public int countByG_U(long groupId, String url) {
		return _uniquePersistenceFinderByG_U.count(
			finderCache, new Object[] {groupId, url});
	}

	public RedirectNotFoundEntryPersistenceImpl() {
		setModelClass(RedirectNotFoundEntry.class);

		setModelImplClass(RedirectNotFoundEntryImpl.class);
		setModelPKClass(long.class);

		setTable(RedirectNotFoundEntryTable.INSTANCE);
	}

	/**
	 * Creates a new redirect not found entry with the primary key. Does not add the redirect not found entry to the database.
	 *
	 * @param redirectNotFoundEntryId the primary key for the new redirect not found entry
	 * @return the new redirect not found entry
	 */
	@Override
	public RedirectNotFoundEntry create(long redirectNotFoundEntryId) {
		RedirectNotFoundEntry redirectNotFoundEntry =
			new RedirectNotFoundEntryImpl();

		redirectNotFoundEntry.setNew(true);
		redirectNotFoundEntry.setPrimaryKey(redirectNotFoundEntryId);

		redirectNotFoundEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return redirectNotFoundEntry;
	}

	/**
	 * Removes the redirect not found entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param redirectNotFoundEntryId the primary key of the redirect not found entry
	 * @return the redirect not found entry that was removed
	 * @throws NoSuchNotFoundEntryException if a redirect not found entry with the primary key could not be found
	 */
	@Override
	public RedirectNotFoundEntry remove(long redirectNotFoundEntryId)
		throws NoSuchNotFoundEntryException {

		return remove((Serializable)redirectNotFoundEntryId);
	}

	@Override
	protected RedirectNotFoundEntry removeImpl(
		RedirectNotFoundEntry redirectNotFoundEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(redirectNotFoundEntry)) {
				redirectNotFoundEntry = (RedirectNotFoundEntry)session.get(
					RedirectNotFoundEntryImpl.class,
					redirectNotFoundEntry.getPrimaryKeyObj());
			}

			if (redirectNotFoundEntry != null) {
				session.delete(redirectNotFoundEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (redirectNotFoundEntry != null) {
			clearCache(redirectNotFoundEntry);
		}

		return redirectNotFoundEntry;
	}

	@Override
	public RedirectNotFoundEntry updateImpl(
		RedirectNotFoundEntry redirectNotFoundEntry) {

		boolean isNew = redirectNotFoundEntry.isNew();

		if (!(redirectNotFoundEntry instanceof
				RedirectNotFoundEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(redirectNotFoundEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					redirectNotFoundEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in redirectNotFoundEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom RedirectNotFoundEntry implementation " +
					redirectNotFoundEntry.getClass());
		}

		RedirectNotFoundEntryModelImpl redirectNotFoundEntryModelImpl =
			(RedirectNotFoundEntryModelImpl)redirectNotFoundEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (redirectNotFoundEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				redirectNotFoundEntry.setCreateDate(date);
			}
			else {
				redirectNotFoundEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!redirectNotFoundEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				redirectNotFoundEntry.setModifiedDate(date);
			}
			else {
				redirectNotFoundEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		long userId = GetterUtil.getLong(PrincipalThreadLocal.getName());

		if (userId > 0) {
			long companyId = redirectNotFoundEntry.getCompanyId();

			long groupId = redirectNotFoundEntry.getGroupId();

			long redirectNotFoundEntryId = 0;

			if (!isNew) {
				redirectNotFoundEntryId = redirectNotFoundEntry.getPrimaryKey();
			}

			try {
				redirectNotFoundEntry.setUrl(
					SanitizerUtil.sanitize(
						companyId, groupId, userId,
						RedirectNotFoundEntry.class.getName(),
						redirectNotFoundEntryId, ContentTypes.TEXT_PLAIN,
						Sanitizer.MODE_ALL, redirectNotFoundEntry.getUrl(),
						null));
			}
			catch (SanitizerException sanitizerException) {
				throw new SystemException(sanitizerException);
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(redirectNotFoundEntry);
			}
			else {
				redirectNotFoundEntry = (RedirectNotFoundEntry)session.merge(
					redirectNotFoundEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(redirectNotFoundEntry, false);

		if (isNew) {
			redirectNotFoundEntry.setNew(false);
		}

		redirectNotFoundEntry.resetOriginalValues();

		return redirectNotFoundEntry;
	}

	/**
	 * Returns the redirect not found entry with the primary key or throws a <code>NoSuchNotFoundEntryException</code> if it could not be found.
	 *
	 * @param redirectNotFoundEntryId the primary key of the redirect not found entry
	 * @return the redirect not found entry
	 * @throws NoSuchNotFoundEntryException if a redirect not found entry with the primary key could not be found
	 */
	@Override
	public RedirectNotFoundEntry findByPrimaryKey(long redirectNotFoundEntryId)
		throws NoSuchNotFoundEntryException {

		return findByPrimaryKey((Serializable)redirectNotFoundEntryId);
	}

	/**
	 * Returns the redirect not found entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param redirectNotFoundEntryId the primary key of the redirect not found entry
	 * @return the redirect not found entry, or <code>null</code> if a redirect not found entry with the primary key could not be found
	 */
	@Override
	public RedirectNotFoundEntry fetchByPrimaryKey(
		long redirectNotFoundEntryId) {

		return fetchByPrimaryKey((Serializable)redirectNotFoundEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "redirectNotFoundEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_REDIRECTNOTFOUNDENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return RedirectNotFoundEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the redirect not found entry persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_REDIRECTNOTFOUNDENTRY_WHERE,
				_SQL_COUNT_REDIRECTNOTFOUNDENTRY_WHERE,
				RedirectNotFoundEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"redirectNotFoundEntry.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, RedirectNotFoundEntry::getGroupId));

		_uniquePersistenceFinderByG_U = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_U",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "url"}, 0, 2, false,
				RedirectNotFoundEntry::getGroupId,
				convertNullFunction(RedirectNotFoundEntry::getUrl)),
			_SQL_SELECT_REDIRECTNOTFOUNDENTRY_WHERE, "",
			new FinderColumn<>(
				"redirectNotFoundEntry.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, RedirectNotFoundEntry::getGroupId),
			new FinderColumn<>(
				"redirectNotFoundEntry.", "url", FinderColumn.Type.STRING, "=",
				true, true, RedirectNotFoundEntry::getUrl));

		RedirectNotFoundEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		RedirectNotFoundEntryUtil.setPersistence(null);

		entityCache.removeCache(RedirectNotFoundEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = RedirectPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = RedirectPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = RedirectPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		RedirectNotFoundEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_REDIRECTNOTFOUNDENTRY =
		"SELECT redirectNotFoundEntry FROM RedirectNotFoundEntry redirectNotFoundEntry";

	private static final String _SQL_SELECT_REDIRECTNOTFOUNDENTRY_WHERE =
		"SELECT redirectNotFoundEntry FROM RedirectNotFoundEntry redirectNotFoundEntry WHERE ";

	private static final String _SQL_COUNT_REDIRECTNOTFOUNDENTRY_WHERE =
		"SELECT COUNT(redirectNotFoundEntry) FROM RedirectNotFoundEntry redirectNotFoundEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No RedirectNotFoundEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		RedirectNotFoundEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-2074408883